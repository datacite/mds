package org.datacite.mds.validation.constraints;

import javax.validation.Validation;
import javax.validation.Validator;

import org.datacite.mds.validation.ValidationHelper;

public abstract class AbstractContraintsTest {
    private ValidationHelper validationHelper;
    
    AbstractContraintsTest() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        validationHelper = new ValidationHelper();
        validationHelper.setValidator(validator);
    }
    
    public ValidationHelper getValidationHelper() {
        return validationHelper;
    }
}
