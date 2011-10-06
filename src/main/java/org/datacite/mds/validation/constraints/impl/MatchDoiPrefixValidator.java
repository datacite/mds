package org.datacite.mds.validation.constraints.impl;

import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Prefix;
import org.datacite.mds.util.Utils;
import org.datacite.mds.util.ValidationUtils;
import org.datacite.mds.validation.constraints.MatchDoiPrefix;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class MatchDoiPrefixValidator implements ConstraintValidator<MatchDoiPrefix, Dataset> {
    String defaultMessage;
    
    String testPrefix;
    
    public void initialize(MatchDoiPrefix constraintAnnotation) {
        defaultMessage = constraintAnnotation.message();
    }

    public boolean isValid(Dataset dataset, ConstraintValidatorContext context) {
        boolean isValidationUnneeded = dataset.getDatacentre() == null || StringUtils.isEmpty(dataset.getDoi()) || dataset.getId() != null;
        if (isValidationUnneeded)
            return true;

        String prefixOfDataset = Utils.getDoiPrefix(dataset.getDoi());
        if (prefixOfDataset.equals(testPrefix))
            return true;
        
        Set<Prefix> allowedPrefixes = dataset.getDatacentre().getPrefixes();
        for (Prefix prefix : allowedPrefixes) {
            if (prefix.getPrefix().equals(prefixOfDataset)) {
                return true;
            }
        }

        ValidationUtils.addConstraintViolation(context, defaultMessage, "doi");
        return false;
    }

    public String getTestPrefix() {
        return testPrefix;
    }

    public void setTestPrefix(String testPrefix) {
        this.testPrefix = testPrefix;
    }
}
