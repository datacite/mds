package org.datacite.mds.util;

import java.util.Set;

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

    public static boolean isValid(Object object, String propertyName) {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set violations = validator.validateProperty(object, propertyName);
        return violations.isEmpty();
    }

}
