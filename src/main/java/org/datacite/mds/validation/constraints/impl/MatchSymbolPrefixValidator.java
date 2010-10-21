package org.datacite.mds.validation.constraints.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.util.Utils;
import org.datacite.mds.validation.constraints.MatchSymbolPrefix;

public class MatchSymbolPrefixValidator implements ConstraintValidator<MatchSymbolPrefix, Datacentre> {
    String defaultMessage;

    public void initialize(MatchSymbolPrefix constraintAnnotation) {
        defaultMessage = constraintAnnotation.message();
    }

    public boolean isValid(Datacentre datacentre, ConstraintValidatorContext context) {
        if (datacentre.getAllocator() == null || !Utils.isValid(datacentre, "symbol")) {
            // don't check until a allocator is selected and a valid symbol is given
            return true;
        }
        
        if (!datacentre.getSymbol().startsWith(datacentre.getAllocator().getSymbol()+".")) {
            context.disableDefaultConstraintViolation();
            Utils.addConstraintViolation(context, defaultMessage, "symbol");
            return false;
        }
        
        return true;
    }
}
