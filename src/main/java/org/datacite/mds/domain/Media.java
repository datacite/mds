package org.datacite.mds.domain;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.datacite.mds.domain.Dataset;
import javax.persistence.ManyToOne;

@RooJavaBean
@RooToString
@RooEntity
public class Media {

    @ManyToOne
    private Dataset dataset;

    private String mediaType;

    private String url;
}
