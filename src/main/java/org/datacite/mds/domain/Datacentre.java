package org.datacite.mds.domain;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.Version;
import javax.validation.GroupSequence;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.datacite.mds.util.FilterPredicates;
import org.datacite.mds.util.Utils;
import org.datacite.mds.validation.constraints.Email;
import org.datacite.mds.validation.constraints.ListOfDomains;
import org.datacite.mds.validation.constraints.MatchPrefixes;
import org.datacite.mds.validation.constraints.MatchSymbolPrefix;
import org.datacite.mds.validation.constraints.Symbol;
import org.datacite.mds.validation.constraints.Unique;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

@RooJavaBean
@RooToString(excludeFields = { "quotaExceeded" })
@RooEntity(finders = { "findDatacentresBySymbolEquals", "findDatacentresByNameLike" })
@MatchPrefixes(groups = Datacentre.SecondLevelConstraint.class)
@MatchSymbolPrefix(groups = Datacentre.SecondLevelConstraint.class)
@Unique(field = "symbol")
@Entity
@XmlRootElement
@GroupSequence({ Datacentre.class, Datacentre.SecondLevelConstraint.class })
public class Datacentre implements AllocatorOrDatacentre {

    private static Logger log4j = Logger.getLogger(Datacentre.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    
    @Version
    @Column(name = "version")
    private Integer version;
    
    @XmlTransient
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    @XmlTransient
    public Integer getVersion() {
        return this.version;
    }
    
    public void setVersion(Integer version) {
        this.version = version;
    }    

    @NotNull
    @Symbol(Symbol.Type.DATACENTRE)
    @Column(unique = true)
    private String symbol;

    private String password;
    
    @XmlTransient
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    @NotNull
    @Size(min = 3, max = 255)
    private String name;

    @NotNull
    @Size(min = 2, max = 80)
    private String contactName;

    @NotNull
    @Email
    private String contactEmail;

    @NotNull
    private Integer doiQuotaAllowed = -1;

    @NotNull
    @Min(0L)
    @Max(999999999L)
    private Integer doiQuotaUsed = 0;

    private Boolean isActive = true;

    private String roleName = "ROLE_DATACENTRE";

    @XmlTransient
    public String getRoleName() {
        return this.roleName;
    }
    
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Size(min = 0, max = 255)
    @ListOfDomains
    private String domains;

    @Size(max = 4000)
    private String comments;

    @NotNull
    @ManyToOne(targetEntity = Allocator.class)
    @JoinColumn
    private Allocator allocator;

    @XmlTransient 
    public Allocator getAllocator() {
        return this.allocator;
    }
    
    public void setAllocator(Allocator allocator) {
        this.allocator = allocator;
    }

    @ManyToMany(cascade = CascadeType.ALL)
    @OrderBy("prefix")
    private Set<org.datacite.mds.domain.Prefix> prefixes = new java.util.HashSet<org.datacite.mds.domain.Prefix>();

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(iso = ISO.DATE_TIME)

    private Date updated;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date created;
    
    private String experiments;

    public enum ForceRefresh { YES, NO };
    
    /**
     * Increase used quota counter for a datacentre.
     * 
     * Implementation uses HQL update in order to maintain potential concurrent access (i.e. a datacentre using 
     * concurrently many API clients. Using HQL update makes sure database row level lock will guarantee only one
     * client changes the value at the time.
     *  
     * @param forceRefresh the consequence of using HQL update is lack of the value in the instance field. 
     * Use ForceRefresh.YES to reread the value from database but be aware that refresh() rereads all fields, not
     * only doiQuotaUsed so if you have any other changes in the object persist them first.
     */
    @Transactional
    public void incQuotaUsed(ForceRefresh forceRefresh) {
        String qlString = "update Datacentre a set a.doiQuotaUsed = a.doiQuotaUsed + 1 where a.symbol = :symbol";
        entityManager.createQuery(qlString).setParameter("symbol", getSymbol()).executeUpdate();
        
        if (forceRefresh == ForceRefresh.YES)
            refresh();
    }
    
    /**
     * Check if quota exceeded.
     * 
     * Implementation uses HQL select in order to maintain potential concurrent access (i.e. a datacentre using 
     * concurrently many API clients.
     *  
     * @return true if quota is exceeded
     */
    @Transactional
    public boolean isQuotaExceeded() {
        if (getDoiQuotaAllowed() < 0)
            return false;
        
        String qlString = "select doiQuotaAllowed - doiQuotaUsed from Datacentre o where id = :id";
        Integer diff = (Integer) entityManager().createQuery(qlString).setParameter("id", getId()).getSingleResult();
        
        return diff <= 0;
    }

    @SuppressWarnings("unchecked")
    public static List<Datacentre> findAllDatacentresByAllocator(Allocator allocator) {
        String qlString = "select o from Datacentre o where allocator = :allocator order by symbol";
        return entityManager().createQuery(qlString).setParameter("allocator", allocator).getResultList();
    }

    @SuppressWarnings("unchecked")
    public static List<Datacentre> findDatacentreEntriesByAllocator(Allocator allocator, int firstResult, int maxResults) {
        String qlString = "select o from Datacentre o where allocator = :allocator order by symbol";
        return entityManager().createQuery(qlString).setParameter("allocator", allocator).setFirstResult(firstResult)
                .setMaxResults(maxResults).getResultList();
    }
    
    public static long countDatacentresByAllocator(Allocator allocator) {
        TypedQuery<Long> q = entityManager().createQuery("SELECT COUNT(*) FROM Datacentre WHERE allocator = :allocator", Long.class);
        q.setParameter("allocator", allocator);
        return q.getSingleResult();
    }

    @Transactional
    public void persist() {
        Date date = new Date();
        setCreated(date);
        setUpdated(date);
        if (this.entityManager == null)
            this.entityManager = entityManager();
        this.entityManager.persist(this);
    }

    @Transactional
    public Datacentre merge() {
        setUpdated(new Date());
        if (this.entityManager == null)
            this.entityManager = entityManager();
        Datacentre merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

        @Transactional
    public void refresh() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        this.entityManager.refresh(this);
    }

    /**
     * retrieve a datacentre by symbol
     * @param symbol of an datacentre
     * @return datacentre with the given symbol or null if no such datacentre exists
     */
    public static Datacentre findDatacentreBySymbol(String symbol) {
        if (symbol == null) {
            return null;
        }
        try {
            log4j.trace("search for '" + symbol + "'");
            Datacentre dc = findDatacentresBySymbolEquals(symbol).getSingleResult();
            log4j.trace("found '" + symbol + "'");
            return dc;
        } catch (Exception e) {
            log4j.trace("no datacentre found");
            return null;
        }
    }
    
    /**
     * calculate String to be used for magic auth key
     * 
     * @return (unhashed) base part of the magic auth string
     */
    public String getBaseAuthString() {
        StringBuilder str = new StringBuilder();
        str.append(getId());
        str.append(getSymbol());
        str.append(StringUtils.defaultString(getPassword()));
        return str.toString();
    }

    public void setDomains(String domains) {
        this.domains = Utils.normalizeCsvStandard(domains);
    }
    
    public static List<Datacentre> findDatacentresByPrefix (Prefix prefix) {
        List<Datacentre> list = findAllDatacentres();
        Predicate containsPrefix = FilterPredicates.getAllocatorOrDatacentreContainsPrefixPredicate(prefix);
        CollectionUtils.filter(list, containsPrefix);
        return list;
    }
    
    public Collection<String> getExperiments() {
        return Utils.csvToList(this.experiments);
    }
    
    public void setExperiments(Collection<String> experiments) {
        this.experiments = Utils.collectionToCsv(experiments);
    }
    
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail.trim();
    }
    
    public void setName(String name) {
        this.name = name.replaceAll("\r?\n", " ").trim();
    }

    private transient long countDatasets; 
    
    public long getCountDatasets() {
        return Dataset.countDatasetsByAllocatorOrDatacentre(this)
                - Dataset.countTestDatasetsByAllocatorOrDatacentre(this);
    }
    
    @Override
    public String toString() {
        return getSymbol() + " (id=" + getId() + ")";
    }
    
    public interface SecondLevelConstraint {};
}
