package org.datacite.mds.validation;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.datacite.mds.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class ValidationHelper {

    @Autowired
    Validator validator;

    public void validate(Object... objects) throws ConstraintViolationException {
        Set<ConstraintViolation<Object>> violations = new HashSet<ConstraintViolation<Object>>();
        for (Object object : objects)
            violations.addAll(validator.validate(object));

        if (!violations.isEmpty()) {
            Set<ConstraintViolation<?>> castedViolations = new HashSet<ConstraintViolation<?>>(violations);
            throw new ConstraintViolationException(castedViolations);
        }
    }
    
    public void validateTo(BindingResult result, Object... objects) {
        try {
            validate(objects);
        } catch (ConstraintViolationException ex) {
            ValidationUtils.constraintViolationToBindingResult(ex, result);
        }
    }

}
