package org.datacite.mds.util;

import java.lang.annotation.Annotation;
import java.net.IDN;
import java.net.URL;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.validator.UrlValidator;

/**
 * Util class with validation related static methods.
 */
public class ValidationUtils {

    /**
     * Getter for the default JSR-303 validator
     * 
     * @return validator
     */
    private static Validator getValidator() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        return validator;
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
    public static <T> boolean isValid(T object, String propertyName) {
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
    public static <T> boolean isValid(T object) {
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
    public static <T> boolean isValid(T object, Class<? extends Annotation> constraint) {
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
    public static <T> String getFirstViolationMessage(T object) {
        for (ConstraintViolation<T> violation : getValidator().validate(object)) {
            return violation.getMessage();
        }
        return null;
    }

    /**
     * shortcut for building a constraint violation attached to a specific node
     * 
     * @param context
     *            ConstraintValidatorContext
     * @param message
     *            message of the violation (e.g. template to be evaluated)
     * @param node
     *            node to attach the violation to
     */
    public static void addConstraintViolation(ConstraintValidatorContext context, String message, String node) {
        context.buildConstraintViolationWithTemplate(message).addNode(node).addConstraintViolation();
    }

    /**
     * shortcut for building a constraint violation
     * 
     * @param context
     *            ConstraintValidatorContext
     * @param message
     *            message of the violation (e.g. template to be evaluated)
     */
    public static void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }

    /**
     * 
     * validation method to check a string, if it is a valid hostname. E.g. it
     * must not contain a path, port or schema.
     * 
     * @param str
     *            String to be checked
     * @return true if the given string is a valid hostname, false otherwise
     */
    public static boolean isHostname(String str) {
        try {
            URL url = new URL("http://" + str);
            if (!url.getHost().equals(str)) {
                // domain should only consists of the pure host name
                return false;
            }

            str = IDN.toASCII(str); // convert international domain names (IDN)
            if (str.matches(".*\\.xn--[^.]*$")) {
                // UrlValidator doesn't handle top level IDNs
                // so we add .org if necessary
                str += ".org";
            }

            UrlValidator urlValidator = new UrlValidator();
            if (!urlValidator.isValid("http://" + str)) {
                // url should be valid, e.g. "test.t" or "com" should be fail
                return false;
            }
        } catch (Exception ex) {
            // url should be well formed
            return false;
        }
        return true;
    }

}
