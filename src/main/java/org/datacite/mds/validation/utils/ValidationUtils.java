package org.datacite.mds.validation.utils;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public class ValidationUtils {

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
    public static boolean isValid(Object object, String propertyName) {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set violations = validator.validateProperty(object, propertyName);
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
    public static <T> boolean isConstraintValid(T object, Class<? extends Annotation> constraint) {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(object);
    
        for (ConstraintViolation<T> violation : violations) {
            if (violation.getConstraintDescriptor().getAnnotation().annotationType().equals(constraint)) {
                return false;
            }
        }
    
        return true;
    }

    public static void addConstraintViolation(ConstraintValidatorContext context, String message, String node) {
        context.buildConstraintViolationWithTemplate(message).addNode(node).addConstraintViolation();
    }

    public static void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }

}
