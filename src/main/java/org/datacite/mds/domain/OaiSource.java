package org.datacite.mds.domain;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.datacite.mds.validation.constraints.Symbol;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEntity
public class OaiSource {

    @NotNull
    @URL
    private String url;

    @NotNull
    @Symbol(hasToExist=true, value={Symbol.Type.DATACENTRE, Symbol.Type.ALLOCATOR})
    private String owner;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date lastHarvest;

    private String lastStatus;
}
