package org.datacite.mds.validation;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidationHelper {
    
    Logger log4j = Logger.getLogger(ValidationHelper.class);
    
    @Autowired 
    Validator validator;
    
    
    /**
     * Getter for the default JSR-303 validator
     * 
     * @return validator
     */
    private Validator getValidator() {
        return this.validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
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
        Set<?> violations = getValidator().validateProperty(object, propertyName);
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
        Set<?> violations = getValidator().validate(object);
        return violations.isEmpty();
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
        Set<ConstraintViolation<T>> violations = getValidator().validate(object);

        for (ConstraintViolation<T> violation : violations) {
            if (violation.getConstraintDescriptor().getAnnotation().annotationType().equals(constraint)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Method to get the error message from the first violation thrown by the
     * validator on the given object
     * 
     * @param object
     *            object to be validated
     * @return String containing the first error message of null if the object
     *         is valid
     */
    public <T> String getFirstViolationMessage(T object) {
        for (ConstraintViolation<T> violation : getValidator().validate(object)) {
            return violation.getMessage();
        }
        return null;
    }

}
