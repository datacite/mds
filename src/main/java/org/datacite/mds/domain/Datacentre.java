package org.datacite.mds.domain;

import javax.persistence.Entity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import org.datacite.mds.domain.Allocator;
import org.datacite.mds.validation.constraints.Email;
import org.datacite.mds.validation.constraints.ListOfDomains;
import org.datacite.mds.validation.constraints.MatchSymbolPrefix;
import org.datacite.mds.validation.constraints.Symbol;

import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import java.util.Set;
import javax.persistence.ManyToMany;
import javax.persistence.CascadeType;

@Entity
@RooJavaBean
@RooToString
@RooEntity(finders = { "findDatacentresBySymbolEquals", "findDatacentresByNameLike" })
@MatchSymbolPrefix
public class Datacentre {

    @NotNull
    @Symbol(Symbol.Type.DATACENTRE)
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

    private Boolean isActive;
    
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
    private Set<org.datacite.mds.domain.Prefix> prefixes = new java.util.HashSet<org.datacite.mds.domain.Prefix>();
    
    @Transactional
    public void incQuotaUsed() {
        String qlString = "update Datacentre a set a.doiQuotaUsed = a.doiQuotaUsed + 1 where a.symbol = :symbol";
        entityManager.createQuery(qlString).setParameter("symbol", getSymbol()).executeUpdate();
    }
}
