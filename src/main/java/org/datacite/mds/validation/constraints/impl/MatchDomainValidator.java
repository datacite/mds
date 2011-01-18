package org.datacite.mds.validation.constraints.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.datacite.mds.domain.Dataset;
import org.datacite.mds.util.Utils;
import org.datacite.mds.util.ValidationUtils;
import org.datacite.mds.validation.ValidationHelper;
import org.datacite.mds.validation.constraints.MatchDomain;
import org.springframework.beans.factory.annotation.Autowired;

public class MatchDomainValidator implements ConstraintValidator<MatchDomain, Dataset> {
    String defaultMessage;

    @Autowired
    ValidationHelper validationHelper;

    public void initialize(MatchDomain constraintAnnotation) {
        defaultMessage = constraintAnnotation.message();
    }

    public boolean isValid(Dataset dataset, ConstraintValidatorContext context) {
        if (dataset.getUrl() == null || dataset.getUrl().isEmpty() || dataset.getDatacentre() == null) {
            return true;
        }

        // check each allowed domain of the datacentre against the used one
        String hostname = Utils.getHostname(dataset.getUrl()).toLowerCase();

        for (String domain : Utils.csvToList(dataset.getDatacentre().getDomains())) {
            domain = domain.toLowerCase();
            if (hostname.equals(domain) || hostname.endsWith("." + domain)) {
                return true;
            }
        }

        ValidationUtils.addConstraintViolation(context, defaultMessage, "url");
        return false;
    }
}
