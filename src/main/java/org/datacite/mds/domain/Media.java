package org.datacite.mds.domain;

import java.util.Date;

import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;

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
public class Media {

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

    public interface SecondLevelConstraint {
    };
}
