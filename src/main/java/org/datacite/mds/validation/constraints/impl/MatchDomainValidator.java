package org.datacite.mds.validation.constraints.impl;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.util.Utils;
import org.datacite.mds.util.ValidationUtils;
import org.datacite.mds.validation.constraints.MatchDomain;

public class MatchDomainValidator implements ConstraintValidator<MatchDomain, Dataset> {
    String defaultMessage;
    String wildCard;

    public void initialize(MatchDomain constraintAnnotation) {
        defaultMessage = constraintAnnotation.message();
        wildCard = constraintAnnotation.wildCard();
    }

    public boolean isValid(Dataset dataset, ConstraintValidatorContext context) {
        boolean isValidationUnneeded = dataset.getDatacentre() == null || StringUtils.isEmpty(dataset.getUrl()); 
        if (isValidationUnneeded)
            return true;

        String hostname = Utils.getHostname(dataset.getUrl()).toLowerCase();
        List<String> allowedDomains = Utils.csvToList(dataset.getDatacentre().getDomains());
        
        for (String domain : allowedDomains) {
            domain = domain.toLowerCase();
            boolean matchWildCard = Utils.wildCardMatch(hostname, domain, wildCard);
            boolean matchSubDomain = Utils.wildCardMatch(hostname, "*." + domain, wildCard); 
            if (matchWildCard || matchSubDomain) {
                return true;
            }
        }

        ValidationUtils.addConstraintViolation(context, defaultMessage, "url");
        return false;
    }
}
