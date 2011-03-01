package org.datacite.mds.validation.constraints.impl;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.util.Utils;
import org.datacite.mds.util.ValidationUtils;
import org.datacite.mds.validation.ValidationHelper;
import org.datacite.mds.validation.constraints.MatchDomain;
import org.springframework.beans.factory.annotation.Autowired;

public class MatchDomainValidator implements ConstraintValidator<MatchDomain, Dataset> {
    String defaultMessage;

    public void initialize(MatchDomain constraintAnnotation) {
        defaultMessage = constraintAnnotation.message();
    }

    public boolean isValid(Dataset dataset, ConstraintValidatorContext context) {
        boolean isValidationUnneeded = dataset.getDatacentre() == null || StringUtils.isEmpty(dataset.getUrl()); 
        if (isValidationUnneeded)
            return true;

        String hostname = Utils.getHostname(dataset.getUrl()).toLowerCase();
        List<String> allowedDomains = Utils.csvToList(dataset.getDatacentre().getDomains());
        
        for (String domain : allowedDomains) {
            domain = domain.toLowerCase();
            if (hostname.equals(domain) || hostname.endsWith("." + domain)) {
                return true;
            }
        }

        ValidationUtils.addConstraintViolation(context, defaultMessage, "url");
        return false;
    }
}
