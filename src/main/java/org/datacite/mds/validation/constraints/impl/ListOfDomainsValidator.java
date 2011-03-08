package org.datacite.mds.validation.constraints.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;
import org.datacite.mds.util.Utils;
import org.datacite.mds.util.ValidationUtils;
import org.datacite.mds.validation.constraints.ListOfDomains;

public class ListOfDomainsValidator implements ConstraintValidator<ListOfDomains, String> {
    
    String wildCard;

    public void initialize(ListOfDomains constraintAnnotation) {
        this.wildCard = constraintAnnotation.wildCard();
    }

    public boolean isValid(String domains, ConstraintValidatorContext context) {
        for (String domain : Utils.csvToList(domains)) {
            boolean containsWildcard = StringUtils.contains(domain, wildCard);
            boolean isHostName = ValidationUtils.isHostname(domain); 
            if (! containsWildcard && ! isHostName) {
                return false;
            }
        }
        return true;
    }
}
