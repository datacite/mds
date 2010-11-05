package org.datacite.mds.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.datacite.mds.validation.constraints.Symbol;
import org.datacite.mds.validation.constraints.Unique;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@Unique(field = "url")
@RooEntity(finders = { "findOaiSourcesByUrl" })
@XmlRootElement
@Entity
public class OaiSource {

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
    @URL
    @Column(unique = true)
    private String url;

    @NotNull
    //hasToExists=true is not possible (MDS-129)
    @Symbol(hasToExist = false, value = { Symbol.Type.DATACENTRE, Symbol.Type.ALLOCATOR })
    private String owner;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date lastHarvest;

    private String lastStatus = "NONE";
}
