package org.datacite.mds.domain;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.datacite.mds.util.FilterPredicates;
import org.datacite.mds.util.Utils;
import org.datacite.mds.validation.constraints.Email;
import org.datacite.mds.validation.constraints.Symbol;
import org.datacite.mds.validation.constraints.Unique;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

@RooJavaBean
@RooToString
@RooEntity(finders = { "findAllocatorsBySymbolEquals", "findAllocatorsByNameLike" })
@Unique(field = "symbol")
public class Allocator implements AllocatorOrDatacentre {

    private static Logger log4j = Logger.getLogger(Allocator.class);

    @NotNull
    @Symbol(Symbol.Type.ALLOCATOR)
    @Column(unique = true)
    private String symbol;

    private String password;

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

    @ManyToMany(cascade = CascadeType.ALL)
    @OrderBy("prefix")
    private Set<org.datacite.mds.domain.Prefix> prefixes = new java.util.HashSet<org.datacite.mds.domain.Prefix>();

    private Boolean isActive = true;

    private String roleName = "ROLE_ALLOCATOR";
    
    @Size(max = 4000)
    private String comments;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date updated;

    private String experiments;

    @SuppressWarnings("unchecked")
    public static List<Allocator> findAllAllocators() {
        return entityManager().createQuery("select o from Allocator o order by symbol").getResultList();
    }

    @SuppressWarnings("unchecked")
    public static List<Allocator> findAllocatorEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("select o from Allocator o order by symbol").setFirstResult(firstResult)
                .setMaxResults(maxResults).getResultList();
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
    public Allocator merge() {
        setUpdated(new Date());
        if (this.entityManager == null)
            this.entityManager = entityManager();
        Allocator merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    /**
     * retrieve a allocator by symbol
     * 
     * @param symbol
     *            of an allocator
     * @return allocator with the given symbol or null if no such allocator
     *         exists
     */
    public static Allocator findAllocatorBySymbol(String symbol) {
        if (symbol == null) {
            return null;
        }
        try {
            log4j.trace("search for '" + symbol + "'");
            Allocator al = findAllocatorsBySymbolEquals(symbol).getSingleResult();
            log4j.trace("found '" + symbol + "'");
            return al;
        } catch (Exception e) {
            log4j.trace("no allocator found");
            return null;
        }
    }
    
    public static List<Allocator> findAllocatorsByPrefix (Prefix prefix) {
        List<Allocator> list = findAllAllocators();
        Predicate containsPrefix = FilterPredicates.getAllocatorOrDatacentreContainsPrefixPredicate(prefix);
        CollectionUtils.filter(list, containsPrefix);
        return list;
    }
    
    private transient long countDatasets; 
    
    public long getCountDatasets() {
        return Dataset.countDatasetsByAllocatorOrDatacentre(this)
                - Dataset.countTestDatasetsByAllocatorOrDatacentre(this);
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
    
    @Override
    public String toString() {
        return getSymbol() + " (id=" + getId() + ")";
    }
}
