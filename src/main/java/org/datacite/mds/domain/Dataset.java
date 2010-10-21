package org.datacite.mds.domain;

import javax.persistence.Entity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.roo.addon.entity.RooEntity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.validation.constraints.Doi;
import org.datacite.mds.validation.constraints.MatchDoiPrefix;

import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

@Entity
@RooJavaBean
@RooToString
@RooEntity(finders = { "findDatasetsByDoiEquals" })
@MatchDoiPrefix
public class Dataset {

    @NotNull
    @Doi
    private String doi;

    @NotNull
    private Boolean isActive;

    private Boolean isRefQuality;

    @Min(100L)
    @Max(510L)
    private Integer lastLandingPageStatus;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "S-")
    private Date lastLandingPageStatusCheck;

    private String lastMetadataStatus;

    @NotNull
    @ManyToOne(targetEntity = Datacentre.class)
    @JoinColumn
    private Datacentre datacentre;
}
