package org.datacite.mds.domain;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.TypedQuery;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import org.datacite.mds.validation.constraints.DoiPrefix;
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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@RooJavaBean
@RooToString
@RooEntity(finders = { "findPrefixesByPrefixLike" })
@Unique(field = "prefix")
@Entity
@XmlRootElement
public class Prefix {

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
    @DoiPrefix
    @Column(unique = true)
    private String prefix;

    @XmlTransient
    public Date getCreated() {
        return this.created;
    }
    
    public void setCreated(Date created) {
        this.created = created;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date created;

    @SuppressWarnings("unchecked")
    public static List<Prefix> findAllPrefixes() {
        return entityManager().createQuery("select o from Prefix o order by prefix").getResultList();
    }

    @SuppressWarnings("unchecked")
    public static List<Prefix> findPrefixEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("select o from Prefix o order by prefix").setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    @Transactional
    public void persist() {
        setCreated(new Date());
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
}
