package org.datacite.mds.domain;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.datacite.mds.validation.constraints.Email;
import org.datacite.mds.validation.constraints.Symbol;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEntity(finders = { "findAllocatorsBySymbolEquals", "findAllocatorsByNameLike" })
public class Allocator {

    @NotNull
    @Symbol(Symbol.Type.ALLOCATOR)
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
    private Set<org.datacite.mds.domain.Prefix> prefixes = new java.util.HashSet<org.datacite.mds.domain.Prefix>();

    private Boolean isActive;

    private String roleName = "ROLE_ALLOCATOR";

    @SuppressWarnings("unchecked")
    public static List<Allocator> findAllAllocators() {
        return entityManager().createQuery("select o from Allocator o order by symbol").getResultList();
    }

    @SuppressWarnings("unchecked")
    public static List<Allocator> findAllocatorEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("select o from Allocator o order by symbol").setFirstResult(firstResult).setMaxResults(
                maxResults).getResultList();
    }
}
