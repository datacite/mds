package org.datacite.mds.domain;

import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.datacite.mds.validation.constraints.ValidXML;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

@RooJavaBean
@RooToString
@RooEntity(finders = { "findMetadatasByDataset" })
public class Metadata {

    @Lob
    @Column(name = "xml", columnDefinition = "BLOB NOT NULL")
    @ValidXML
    private byte[] xml;

    @Min(0L)
    private Integer metadataVersion;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "S-")
    private Date lastUpdated;

    @NotNull
    @ManyToOne(targetEntity = Dataset.class)
    @JoinColumn
    private Dataset dataset;

    public static Integer findMaxMetadataVersionByDataset(Dataset dataset) {
        if (dataset == null)
            throw new IllegalArgumentException("The dataset argument is required");
        EntityManager em = entityManager();
        Query q = em.createQuery("SELECT MAX(metadataVersion) FROM Metadata WHERE dataset = :dataset");
        q.setParameter("dataset", dataset);
        Integer max = (Integer) q.getSingleResult();
        return max == null ? -1 : max;
    }

    @Transactional
    public void persist() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        Integer maxVersion = findMaxMetadataVersionByDataset(getDataset());
        setMetadataVersion(maxVersion + 1);
        setLastUpdated(new Date());
        this.entityManager.persist(this);
    }

    public static TypedQuery<Metadata> findMetadatasByDataset(Dataset dataset) {
        if (dataset == null)
            throw new IllegalArgumentException("The dataset argument is required");
        EntityManager em = Metadata.entityManager();
        TypedQuery<Metadata> q = em.createQuery(
                "SELECT Metadata FROM Metadata AS metadata WHERE metadata.dataset = :dataset ORDER BY metadataVersion DESC", Metadata.class);
        q.setParameter("dataset", dataset);
        return q;
    }
}
