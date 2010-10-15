package org.datacite.mds.validation.constraints.impl;

import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.datacite.mds.validation.constraints.Symbol;
import org.datacite.mds.validation.constraints.Symbol.Type;

public class SymbolValidator implements ConstraintValidator<Symbol, String> {

    List<Type> types;

    public void initialize(Symbol constraintAnnotation) {
        types = Arrays.asList(constraintAnnotation.value());
    }

    public boolean isValid(String symbol, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        for (Type t : types) {
            if (Symbol.PATTERNS.get(t).matcher(symbol).matches()) {
                return true;
            } else {
                String message = "{org.datacite.mds.validation.constraints.Symbol." + t.name() + ".message}";
                context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            }
        }

        return false;
    }
}
