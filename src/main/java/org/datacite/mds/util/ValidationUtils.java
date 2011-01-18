package org.datacite.mds.util;

import java.net.IDN;
import java.net.URL;

import javax.validation.ConstraintValidatorContext;

import org.apache.commons.validator.UrlValidator;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

/**
 * Util class with validation related static methods.
 */
public class ValidationUtils {

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
        context.disableDefaultConstraintViolation();
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
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }

    /**
     * <p>
     * Copy a validation error from a field to the object itself. This makes
     * sense for fields not rendered but might cause a validation error
     * (typically a assertTrue Annotation on method level)
     * </p>
     * 
     * <p>
     * If there is no error in the specified field, this method does nothing.
     * </p>
     * 
     * @param result
     *            binding result
     * @param field
     *            name of field to be copied to object level
     */
    public static void copyFieldErrorToObject(BindingResult result, String field) {
        FieldError fieldError = result.getFieldError(field);
        if (fieldError != null) {
            ObjectError error = new ObjectError(fieldError.getObjectName(), fieldError.getDefaultMessage());
            result.addError(error);
        }
    }

    public static void copyFieldErrorToField(BindingResult result, String fromField, String toField) {
        FieldError fieldError = result.getFieldError(fromField);
        if (fieldError != null) {
            FieldError newError = new FieldError(fieldError.getObjectName(), toField, fieldError.getDefaultMessage());
            result.addError(newError);
        }
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
