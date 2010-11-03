package org.datacite.mds.domain;

import java.util.List;
import javax.persistence.Column;
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

@RooJavaBean
@RooToString
@RooEntity(finders = { "findPrefixesByPrefixLike" })
@Unique(field = "prefix")
public class Prefix {

    @NotNull
    @DoiPrefix
    @Column(unique = true)
    private String prefix;

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
