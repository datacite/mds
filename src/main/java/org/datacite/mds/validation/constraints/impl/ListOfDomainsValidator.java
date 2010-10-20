package org.datacite.mds.validation.constraints.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.datacite.mds.util.Utils;
import org.datacite.mds.validation.constraints.ListOfDomains;

public class ListOfDomainsValidator implements ConstraintValidator<ListOfDomains, String> {

    public void initialize(ListOfDomains constraintAnnotation) {
        // nothing to initialize
    }

    public boolean isValid(String domains, ConstraintValidatorContext context) {
        for (String domain : Utils.csvToList(domains)) {
            // for each comma separated value
            if (!Utils.isHostname(domain)) {
                return false;
            }
        }
        return true;
    }
}
