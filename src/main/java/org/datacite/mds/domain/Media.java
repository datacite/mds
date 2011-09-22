package org.datacite.mds.domain;

import javax.persistence.ManyToOne;
import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;

import org.datacite.mds.validation.constraints.MatchDomain;
import org.datacite.mds.validation.constraints.MediaType;
import org.datacite.mds.validation.constraints.Unique;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEntity(finders = { "findMediasByDataset" })
@Unique(field = {"dataset", "mediaType"})
@MatchDomain(groups = Media.SecondLevelConstraint.class)
@GroupSequence({ Media.class, Media.SecondLevelConstraint.class })
public class Media {

    @ManyToOne
    @NotNull
    private Dataset dataset;

    @MediaType
    private String mediaType;

    @URL
    @NotEmpty
    private String url;
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getDataset().getDatacentre().getSymbol()).append(":");
        sb.append(getDataset().getDoi()).append(" ");
        sb.append(getMediaType());
        sb.append(" (id=" + getId() + ")");
        return sb.toString();
    }

    public interface SecondLevelConstraint {};
}
