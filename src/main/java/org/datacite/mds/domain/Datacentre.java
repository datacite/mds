package org.datacite.mds.domain;

import javax.persistence.Entity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.roo.addon.entity.RooEntity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import org.datacite.mds.domain.Allocator;
import org.hibernate.validator.constraints.Email;

import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import java.util.Set;
import javax.persistence.ManyToMany;
import javax.persistence.CascadeType;

@Entity
@RooJavaBean
@RooToString
@RooEntity(finders = { "findDatacentresBySymbolEquals", "findDatacentresByNameLike" })
public class Datacentre {

    @NotNull
    @Size(min = 5, max = 17)
    @Pattern(regexp = "[A-Z]+.[A-Z].")
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
    
    private String roleName;

    @Size(min = 3, max = 255)
    private String domains;

    @Size(max = 4000)
    private String comments;

    @NotNull
    @ManyToOne(targetEntity = Allocator.class)
    @JoinColumn
    private Allocator allocator;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<org.datacite.mds.domain.Prefix> prefixes = new java.util.HashSet<org.datacite.mds.domain.Prefix>();
}
