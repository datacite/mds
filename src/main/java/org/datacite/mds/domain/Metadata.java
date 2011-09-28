package org.datacite.mds.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;
import javax.validation.GroupSequence;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;
import org.datacite.mds.service.SchemaService;
import org.datacite.mds.validation.constraints.MatchDoi;
import org.datacite.mds.validation.constraints.ValidXML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

@RooJavaBean
@RooToString
@RooEntity
@MatchDoi(groups = Metadata.SecondLevelConstraint.class)
@GroupSequence({ Metadata.class, Metadata.SecondLevelConstraint.class })
public class Metadata {

    private static Logger log4j = Logger.getLogger(Metadata.class);
    
    @Autowired
    @Transient
    SchemaService schemaService;

    @ValidXML
    @Column(length=10000)
    private byte[] xml;
    
    private String namespace;

    @Min(0L)
    private Integer metadataVersion;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date created;

    @NotNull
    @ManyToOne(targetEntity = Dataset.class)
    @JoinColumn
    private Dataset dataset;
    
    private Boolean isConvertedByMds = false;
    
    @Transient
    private Query maxMetaVerQuery;

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
        if (maxMetaVerQuery == null)
            maxMetaVerQuery = entityManager.
              createQuery("SELECT MAX(metadataVersion) FROM Metadata WHERE dataset = :dataset");
        
        maxMetaVerQuery.setParameter("dataset", getDataset());
        Integer max = (Integer) maxMetaVerQuery.getSingleResult();
        Integer maxVersion = max == null ? -1 : max;
        setMetadataVersion(maxVersion + 1);
        setCreated(new Date());
        entityManager.persist(this);
        
        log4j.info(getDataset().getDatacentre().getSymbol() + " successfuly stored metadata for " + getDataset().getDoi());
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

    public static Metadata findLatestMetadatasByDataset(Dataset dataset) {
        if (dataset == null)
            throw new IllegalArgumentException("The dataset argument is required");

        Integer maxVersion = findMaxMetadataVersionByDataset(dataset);
        
        EntityManager em = Metadata.entityManager();
        TypedQuery<Metadata> q = em.createQuery(
                "SELECT Metadata FROM Metadata AS metadata WHERE metadata.dataset = :dataset "
                + "AND metadata.metadataVersion = :metadataVersion", 
                Metadata.class);
        q.setParameter("dataset", dataset);
        q.setParameter("metadataVersion", maxVersion);
        
        Metadata result;
        try {
            result = q.getSingleResult();
        } catch (Exception e) {
            result = null;
        }

        return result;
    }
    
    private static String hqlFindLatestMetadatas = "select m from Metadata m WHERE m.metadataVersion = (select max(metadataVersion) from Metadata AS mm WHERE mm.dataset = m.dataset)"; 
    
    public static List<Metadata> findLatestMetadatas() {
        return entityManager().createQuery(hqlFindLatestMetadatas, Metadata.class).getResultList();
    }
    
    public static List<Metadata> findLatestMetadatasByNamespace(String namespace) {
        String hql;
        if (namespace == null) 
            hql = hqlFindLatestMetadatas + " AND m.namespace is null";
        else
            hql = hqlFindLatestMetadatas + " AND m.namespace = :namespace";
        
        TypedQuery<Metadata> q = entityManager().createQuery(hql, Metadata.class);
        
        if (namespace != null)
            q.setParameter("namespace", namespace);
        return q.getResultList();
    }
    
    public void setXml(byte[] xml) {
        if (schemaService == null) 
            throw new IllegalStateException("schemaService has not been injected");
        String namespace = schemaService.getNamespace(xml);
        setNamespace(namespace);
        this.xml = xml;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public Boolean getIsConvertedByMds() {
        return isConvertedByMds;
    }

    public void setIsConvertedByMds(Boolean isConvertedByMds) {
        this.isConvertedByMds = isConvertedByMds;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getDataset().getDatacentre().getSymbol()).append(":");
        sb.append(getDataset().getDoi());
        sb.append(" #").append(getMetadataVersion());
        sb.append(" (id=" + getId() + ")");
        return sb.toString();
    }

    public String debugString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Id: ").append(getId()).append(", ");
        sb.append("Version: ").append(getVersion()).append(", ");
        sb.append("Xml: ").append(java.util.Arrays.toString(getXml())).append(", ");
        sb.append("MetadataVersion: ").append(getMetadataVersion()).append(", ");
        sb.append("Created: ").append(getCreated()).append(", ");
        sb.append("Dataset: ").append(getDataset());
        return sb.toString();
    }

    public interface SecondLevelConstraint {};
    
}
