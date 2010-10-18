package org.datacite.mds.validation.constraints.impl;

import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Prefix;
import org.datacite.mds.validation.constraints.MatchDoiPrefix;

public class MatchDoiPrefixValidator implements ConstraintValidator<MatchDoiPrefix, Dataset> {
    String defaultMessage;

    public void initialize(MatchDoiPrefix constraintAnnotation) {
        defaultMessage = constraintAnnotation.message();
    }

    public boolean isValid(Dataset dataset, ConstraintValidatorContext context) {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set violations = validator.validateProperty(dataset, "doi");

        if (dataset.getDatacentre() == null || !violations.isEmpty()) {
            return true;
        }

        String prefixStr = dataset.getDoi().split("/")[0];
        for (Prefix prefix : dataset.getDatacentre().getPrefixes()) {
            if (prefix.getPrefix().equals(prefixStr)) {
                return true;
            }
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(defaultMessage).addNode("doi").addConstraintViolation();
        return false;
    }
}
