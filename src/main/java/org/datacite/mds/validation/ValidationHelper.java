package org.datacite.mds.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.datacite.mds.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidationHelper {

    @Autowired
    Validator validator;

    /**
     * Method to get the error message from the first violation thrown by the
     * validator on the given object
     * 
     * @param object
     *            object to be validated
     * @return String containing the first error message of null if the object
     *         is valid
     */

    public void validate(Object object) throws ValidationException {
        Set<ConstraintViolation<Object>> violations = validator.validate(object);
        String violationMessage = ValidationUtils.collateViolationMessages(violations);
        if (violationMessage != null)
            throw new ValidationException(violationMessage);
    }

}
