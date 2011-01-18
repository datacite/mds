package org.datacite.mds.validation.constraints.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Prefix;
import org.datacite.mds.util.Utils;
import org.datacite.mds.util.ValidationUtils;
import org.datacite.mds.validation.constraints.MatchDoiPrefix;

public class MatchDoiPrefixValidator implements ConstraintValidator<MatchDoiPrefix, Dataset> {
    String defaultMessage;
    
    public void initialize(MatchDoiPrefix constraintAnnotation) {
        defaultMessage = constraintAnnotation.message();
    }

    public boolean isValid(Dataset dataset, ConstraintValidatorContext context) {
        if (dataset.getDatacentre() == null || dataset.getDoi() == null) {
            return true;
        }

        // check each allowed prefix of the datacentre against the used one
        String prefixStr = Utils.getDoiPrefix(dataset.getDoi());
        for (Prefix prefix : dataset.getDatacentre().getPrefixes()) {
            if (prefix.getPrefix().equals(prefixStr)) {
                return true;
            }
        }

        ValidationUtils.addConstraintViolation(context, defaultMessage, "doi");
        return false;
    }
}
