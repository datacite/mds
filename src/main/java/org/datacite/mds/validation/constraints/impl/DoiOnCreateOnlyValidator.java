package org.datacite.mds.validation.constraints.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.datacite.mds.domain.Dataset;
import org.datacite.mds.validation.constraints.Doi;

public class DoiOnCreateOnlyValidator implements ConstraintValidator<Doi, Dataset> {
    
    DoiValidator doiValidator;

    public void initialize(Doi constraintAnnotation) {
        doiValidator = new DoiValidator();
        doiValidator.initialize(constraintAnnotation);
    }

    public boolean isValid(Dataset dataset, ConstraintValidatorContext context) {
        boolean exists = dataset.getId() != null;
        return exists || doiValidator.isValid(dataset.getDoi(), context);
    }
    
}
