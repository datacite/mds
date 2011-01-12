package org.datacite.mds.validation.constraints.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
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
        context.disableDefaultConstraintViolation();
        if (symbol == null) {
            return true;
        }
        if (hasToExist) {
            // do database lookup only
            return exists(symbol, context);
        } else {
            return !isMalformed(symbol, context);
        }
    }

    boolean isMalformed(String symbol, ConstraintValidatorContext context) {
        for (Type t : types) {
            if (PATTERNS.get(t).matcher(symbol).matches() && symbol.indexOf("--") == -1) {
                //check against the pattern for each given type 
                return false;
            } else {
                String message = "{org.datacite.mds.validation.constraints.Symbol." + t.name() + ".message}";
                ValidationUtils.addConstraintViolation(context, message);
            }
        }
        return true;
    }

    boolean exists(String symbol, ConstraintValidatorContext context) {
        //check table Allocator or Datacentre based on given types
        if (types.contains(Type.ALLOCATOR)) {
            if (Allocator.findAllocatorsBySymbolEquals(symbol).getResultList().size() > 0) {
                return true;
            }
        }
        
        if (types.contains(Type.DATACENTRE)) {
            if (Datacentre.findDatacentresBySymbolEquals(symbol).getResultList().size() > 0) {
                return true;
            }
        }

        ValidationUtils.addConstraintViolation(context, "{org.datacite.mds.validation.constraints.Symbol.notfound}");
        return false;
    }
}
