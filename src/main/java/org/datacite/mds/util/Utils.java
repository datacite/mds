package org.datacite.mds.util;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * Class with several static util methods
 */
public class Utils {

    /**
     * returns the prefix of a doi
     * 
     * @param doi
     *            Doi (e.g. "10.5072/foobar")
     * @return doi prefix (e.g. "10.5072")
     */
    public static String getDoiPrefix(String doi) {
        return doi.split("/")[0];
    }

    /**
     * returns the suffix of a doi
     * 
     * @param doi
     *            Doi (e.g. "10.5072/foobar")
     * @return doi prefix (e.g. "foobar")
     */
    public static String getDoiSuffix(String doi) {
        return doi.split("/")[1];
    }

    /**
     * converts a string with comma separated values to a List of Strings
     * 
     * @param csv
     *            comma separated values
     * @return List of Strings
     */
    public static List<String> csvToList(String csv) {
        return Arrays.asList(csv.split(","));
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
