package org.datacite.mds.validation.constraints;

import javax.validation.Validation;
import javax.validation.Validator;

import org.datacite.mds.validation.ValidationHelper;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractContraintsTest {
    @Autowired
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
