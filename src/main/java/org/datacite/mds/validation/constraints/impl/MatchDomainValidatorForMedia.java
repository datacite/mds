package org.datacite.mds.validation.constraints.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Media;
import org.datacite.mds.validation.constraints.MatchDomain;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class MatchDomainValidatorForMedia extends MatchDomainValidator implements ConstraintValidator<MatchDomain, Media> {
    public boolean isValid(Media media, ConstraintValidatorContext context) {
        String url = media.getUrl();
        Datacentre datacentre = media.getDataset().getDatacentre();
        return isValid(url, datacentre, context);
    }
}
