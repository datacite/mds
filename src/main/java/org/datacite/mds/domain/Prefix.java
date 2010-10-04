package org.datacite.mds.domain;

import javax.persistence.Entity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.roo.addon.entity.RooEntity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;

@Entity
@RooJavaBean
@RooToString
@RooEntity(finders = { "findPrefixesByPrefixLike" })
public class Prefix {

    @NotNull
    @Size(min = 4, max = 80)
    @Pattern(regexp = "10.(\\d)+")
    private String prefix;
}
