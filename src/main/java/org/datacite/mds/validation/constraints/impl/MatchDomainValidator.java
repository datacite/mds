package org.datacite.mds.validation.constraints.impl;

import java.net.URL;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.datacite.mds.domain.Dataset;
import org.datacite.mds.util.Utils;
import org.datacite.mds.validation.constraints.MatchDoiPrefix;
import org.datacite.mds.validation.constraints.MatchDomain;

public class MatchDomainValidator implements ConstraintValidator<MatchDomain, Dataset> {
    String defaultMessage;

    public void initialize(MatchDomain constraintAnnotation) {
        defaultMessage = constraintAnnotation.message();
    }

    public boolean isValid(Dataset dataset, ConstraintValidatorContext context) {
        if (dataset.getUrl() == null || dataset.getUrl().isEmpty() || dataset.getDatacentre() == null
                || !Utils.isValid(dataset, "url")) {
            // don't check until a datacentre is selected and valid url is given
            return true;
        }

        // check each allowed domain of the datacentre against the used one
        String hostname = Utils.getHostname(dataset.getUrl());

        for (String domain : Utils.csvToList(dataset.getDatacentre().getDomains())) {
            if (domain.equals(hostname)) {
                return true;
            }
        }

        context.disableDefaultConstraintViolation();
        Utils.addConstraintViolation(context, defaultMessage, "url");
        return false;
    }
}
