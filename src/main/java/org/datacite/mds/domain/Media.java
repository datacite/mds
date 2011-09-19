package org.datacite.mds.domain;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.validation.constraints.MediaType;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@RooJavaBean
@RooToString
@RooEntity(finders = { "findMediasByDataset" })
public class Media {

    @ManyToOne
    @NotNull
    private Dataset dataset;

    @MediaType
    private String mediaType;

    @URL
    @NotEmpty
    private String url;
}
