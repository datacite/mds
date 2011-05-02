package org.datacite.mds.util;

import java.net.IDN;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.UrlValidator;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

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
     * Copy a validation error from a field to another field. This makes sense
     * for fields not rendered but might cause a validation error (typically a
     * assertTrue Annotation on method level)
     * </p>
     * 
     * <p>
     * If there is no error in the specified field, this method does nothing.
     * </p>
     * 
     * @param result
     *            binding result
     * @param fromfield
     *            name of field to be copied from
     * @param toField
     *            name of field to be copied to
     */
    public static void copyFieldErrorToField(BindingResult result, String fromField, String toField) {
        FieldError fieldError = result.getFieldError(fromField);
        if (fieldError != null) {
            FieldError newError = new FieldError(fieldError.getObjectName(), toField, fieldError.getDefaultMessage());
            result.addError(newError);
        }
    }

    public static String collateViolationMessages(Set<ConstraintViolation<?>> violations) {
        Collection<String> messages = new ArrayList<String>();
        for (ConstraintViolation<?> violation : violations) {
            messages.add("[" + violation.getPropertyPath() + "] " + violation.getMessage());
        }
        String messagesJoined = StringUtils.join(messages, "; ");
        return StringUtils.defaultIfEmpty(messagesJoined, null);
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
        } catch (MalformedURLException ex) {
            // unreachable, because URL is always constructed with known
            // protocol
            return false;
        }
        return true;
    }

}
