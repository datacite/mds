package org.datacite.mds.util;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public class Utils {
    public static String getDoiPrefix(String doi) {
        return doi.split("/")[0];
    }

    public static String getDoiSuffix(String doi) {
        return doi.split("/")[1];
    }
    
    public static List<String> csvToList(String csv) {
        return Arrays.asList(csv.split(","));
    }

    public static boolean isValid(Object object, String propertyName) {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set violations = validator.validateProperty(object, propertyName);
        return violations.isEmpty();
    }

    public static void addConstraintViolation(ConstraintValidatorContext context, String message, String node) {
        context.buildConstraintViolationWithTemplate(message).addNode(node).addConstraintViolation();
    }

    public static void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }

}
