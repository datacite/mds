package org.datacite.mds.validation.constraints.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.validation.constraints.MatchDomain;

public class MatchDomainValidatorForDataset extends MatchDomainValidator implements ConstraintValidator<MatchDomain, Dataset> {
    public boolean isValid(Dataset dataset, ConstraintValidatorContext context) {
        String url = dataset.getUrl();
        Datacentre datacentre = dataset.getDatacentre();
        return isValid(url, datacentre, context);
    }
}
