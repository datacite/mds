package org.datacite.mds.validation.constraints;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractContraintsTest {
    @Autowired
    private Validator validator;

    AbstractContraintsTest() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    /**
     * Check if a specific constraint annotation is violated
     * 
     * @param object
     *            object to validate
     * @param constraint
     *            constraint annotation to be checked
     * @return true if the given constraint is not violated
     */
    public <T> boolean isValid(T object, Class<? extends Annotation> constraint) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);

        for (ConstraintViolation<T> violation : violations) {
            if (violation.getConstraintDescriptor().getAnnotation().annotationType().equals(constraint)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Wrapper to simply validate a property of an object.
     * 
     * @param object
     *            object to validate
     * @param propertyName
     *            property to validate (e.g. field)
     * @return true if property validates
     * @see javax.validation.Validator.validateProperty
     */
    public <T> boolean isValid(T object, String propertyName) {
        Set<?> violations = validator.validateProperty(object, propertyName);
        return violations.isEmpty();
    }

    /**
     * Wrapper to validate an object.
     * 
     * @param object
     *            object to validate
     * @return true if object validates
     * @see javax.validation.Validator.validateProperty
     */
    public <T> boolean isValid(T object) {
        Set<?> violations = validator.validate(object);
        return violations.isEmpty();
    }

}
