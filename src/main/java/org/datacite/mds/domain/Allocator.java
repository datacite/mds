package org.datacite.mds.domain;

import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.datacite.mds.validation.constraints.Email;
import org.datacite.mds.validation.constraints.Symbol;
import org.datacite.mds.validation.constraints.Unique;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.transaction.annotation.Transactional;

@RooJavaBean
@RooToString
@RooEntity(finders = { "findAllocatorsBySymbolEquals", "findAllocatorsByNameLike" })
@Unique(field = "symbol")
public class Allocator {

    @NotNull
    @Symbol(Symbol.Type.ALLOCATOR)
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

    @ManyToMany(cascade = CascadeType.ALL)
    @OrderBy("prefix")
    @NotNull
    private Set<org.datacite.mds.domain.Prefix> prefixes = new java.util.HashSet<org.datacite.mds.domain.Prefix>();

    private Boolean isActive = true;

    private String roleName = "ROLE_ALLOCATOR";

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date updated;

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
     * @param symbol of an allocator
     * @return allocator with the given symbol or null if no such allocator exists
     */
    public static Allocator findAllocatorBySymbol(String symbol) {
        if (symbol == null) {
            return null;
        }
        try {
            Allocator al = findAllocatorsBySymbolEquals(symbol).getSingleResult();
            return al;
        } catch (Exception e) {
            return null;
        }
    }

}
