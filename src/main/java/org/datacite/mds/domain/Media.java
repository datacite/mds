package org.datacite.mds.domain;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.datacite.mds.validation.constraints.MatchDomain;
import org.datacite.mds.validation.constraints.MediaType;
import org.datacite.mds.validation.constraints.URL;
import org.datacite.mds.validation.constraints.Unique;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

@RooJavaBean
@RooToString
@RooEntity(finders = { "findMediasByDataset" })
@Unique(field = { "dataset", "mediaType" })
@MatchDomain(groups = Media.SecondLevelConstraint.class)
@GroupSequence( { Media.class, Media.SecondLevelConstraint.class })
public class Media implements Comparable {

    @ManyToOne
    @NotNull
    private Dataset dataset;

    @MediaType
    private String mediaType;

    @URL
    @NotEmpty
    private String url;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date updated;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getDataset().getDatacentre().getSymbol()).append(":");
        sb.append(getDataset().getDoi()).append(" ");
        sb.append(getMediaType());
        sb.append(" (id=" + getId() + ")");
        return sb.toString();
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
    public Media merge() {
        setUpdated(new Date());
        if (this.entityManager == null)
            this.entityManager = entityManager();
        Media merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
    public static TypedQuery<Media> findMediasByDataset(Dataset dataset) {
        if (dataset == null) throw new IllegalArgumentException("The dataset argument is required");
        EntityManager em = Media.entityManager();
        TypedQuery<Media> q = em.createQuery("SELECT o FROM Media AS o WHERE o.dataset = :dataset ORDER BY o.mediaType ASC", Media.class);
        q.setParameter("dataset", dataset);
        return q;
    }

    public static Media findMediaByDatasetAndMediaType(Dataset dataset, String mediaType) {
        if (dataset == null) throw new IllegalArgumentException("The dataset argument is required");
        EntityManager em = Media.entityManager();
        TypedQuery<Media> q = em.createQuery("SELECT o FROM Media AS o WHERE o.dataset = :dataset AND o.mediaType = :mediaType", Media.class);
        q.setParameter("dataset", dataset);
        q.setParameter("mediaType", mediaType);
        return q.getSingleResult();
    }

    @Override
    public int compareTo(Object o) {
        Media media = (Media) o;
        return  this.mediaType.compareTo(media.mediaType);
    };

    public interface SecondLevelConstraint {
    }

}
