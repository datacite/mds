package org.datacite.mds.domain;

import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.TypedQuery;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.datacite.mds.validation.constraints.Email;
import org.datacite.mds.validation.constraints.ListOfDomains;
import org.datacite.mds.validation.constraints.MatchSymbolPrefix;
import org.datacite.mds.validation.constraints.Symbol;
import org.datacite.mds.validation.constraints.Unique;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.transaction.annotation.Transactional;

@RooJavaBean
@RooToString
@RooEntity(finders = { "findDatacentresBySymbolEquals", "findDatacentresByNameLike" })
@MatchSymbolPrefix
@Unique(field = "symbol")
public class Datacentre {

    @NotNull
    @Symbol(Symbol.Type.DATACENTRE)
    @Column(unique = true)
    private String symbol;

    @NotNull
    @Size(min = 8, max = 30)
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
    @Min(0L)
    @Max(999999999L)
    private Integer doiQuotaAllowed;

    @NotNull
    @Min(0L)
    @Max(999999999L)
    private Integer doiQuotaUsed;

    private Boolean isActive = true;

    private String roleName = "ROLE_DATACENTRE";

    @Size(min = 0, max = 255)
    @ListOfDomains
    private String domains;

    @Size(max = 4000)
    private String comments;

    @NotNull
    @ManyToOne(targetEntity = Allocator.class)
    @JoinColumn
    private Allocator allocator;

    @ManyToMany(cascade = CascadeType.ALL)
    @OrderBy("prefix")
    @NotNull
    private Set<org.datacite.mds.domain.Prefix> prefixes = new java.util.HashSet<org.datacite.mds.domain.Prefix>();

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(iso = ISO.DATE_TIME)

    private Date updated;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date created;

    @Transactional
    public void incQuotaUsed() {
        String qlString = "update Datacentre a set a.doiQuotaUsed = a.doiQuotaUsed + 1 where a.symbol = :symbol";
        entityManager.createQuery(qlString).setParameter("symbol", getSymbol()).executeUpdate();
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
        setCreated(new Date());
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
}
