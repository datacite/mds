package org.datacite.mds.validation.constraints.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.datacite.mds.util.DomainUtils;
import org.datacite.mds.util.ValidationUtils;
import org.datacite.mds.validation.constraints.Symbol;
import org.datacite.mds.validation.constraints.Symbol.Type;

public class SymbolValidator implements ConstraintValidator<Symbol, String> {

    List<Type> types;
    boolean hasToExist;

    static final HashMap<Type, Pattern> PATTERNS = new HashMap<Type, Pattern>() {
        {
            String symbol = "[A-Z][A-Z\\-]{0,6}[A-Z]";
            put(Type.ALLOCATOR, Pattern.compile(symbol));
            put(Type.DATACENTRE, Pattern.compile(symbol + "\\." + symbol));
        }
    };

    public void initialize(Symbol constraintAnnotation) {
        types = Arrays.asList(constraintAnnotation.value());
        hasToExist = constraintAnnotation.hasToExist();
    }

    public boolean isValid(String symbol, ConstraintValidatorContext context) {
        if (symbol == null)
            return true;

        if (isMalformed(symbol, context))
            return false;

        if (hasToExist)
            return exists(symbol, context);

        return true;
    }

    boolean isMalformed(String symbol, ConstraintValidatorContext context) {
        List<String> violationMessages = new ArrayList<String>();
        for (Type t : types) {
            if (PATTERNS.get(t).matcher(symbol).matches() && symbol.indexOf("--") == -1) {
                // check against the pattern for each given type
                return false;
            } else {
                violationMessages.add("{org.datacite.mds.validation.constraints.Symbol." + t.name() + ".message}");
            }
        }
        for (String message : violationMessages)
            ValidationUtils.addConstraintViolation(context, message);
        return true;
    }

    boolean exists(String symbol, ConstraintValidatorContext context) {
        boolean foundSymbol = DomainUtils.findAllocatorOrDatacentreBySymbol(symbol) != null;
        if (foundSymbol)
            return true;

        ValidationUtils.addConstraintViolation(context, "{org.datacite.mds.validation.constraints.Symbol.notfound}");
        return false;
    }
}
