package org.datacite.mds.domain;

import javax.persistence.Entity;

import org.datacite.mds.validation.constraints.Symbol;
import org.hibernate.validator.constraints.URL;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.roo.addon.entity.RooEntity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@RooJavaBean
@RooToString
@RooEntity
public class OaiSources {

    @NotNull
    @URL
    private String url;

    @NotNull
    @Symbol(hasToExist=true, value={Symbol.Type.DATACENTRE, Symbol.Type.ALLOCATOR})
    private String owner;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "S-")
    private Date lastHarvest;

    private String lastStatus;
}
