package org.datacite.mds.validation.constraints.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.datacite.mds.validation.constraints.Email;

public class EmailValidator implements ConstraintValidator<Email, String> {

    public void initialize(Email constraintAnnotation) {
        // nothing to initialize
    }
    
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return org.apache.commons.validator.EmailValidator.getInstance().isValid(email);
    }
}
