package org.datacite.mds.validation;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidationHelper {

    @Autowired
    Validator validator;

    public void validate(Object object) throws ConstraintViolationException {
        Set<ConstraintViolation<Object>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            Set<ConstraintViolation<?>> castedViolations = new HashSet<ConstraintViolation<?>>(violations);
            throw new ConstraintViolationException(castedViolations);
        }
    }

}
