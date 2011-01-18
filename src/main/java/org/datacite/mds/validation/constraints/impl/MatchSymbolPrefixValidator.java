package org.datacite.mds.validation.constraints.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.util.ValidationUtils;
import org.datacite.mds.validation.constraints.MatchSymbolPrefix;

public class MatchSymbolPrefixValidator implements ConstraintValidator<MatchSymbolPrefix, Datacentre> {
    String defaultMessage;

    public void initialize(MatchSymbolPrefix constraintAnnotation) {
        defaultMessage = constraintAnnotation.message();
    }

    public boolean isValid(Datacentre datacentre, ConstraintValidatorContext context) {
        boolean isValidationUnneeded =  datacentre.getAllocator() == null || StringUtils.isEmpty(datacentre.getSymbol());
        if (isValidationUnneeded)
            return true;
        
        String datacentreSymbol = datacentre.getSymbol();
        String allocatorSymbol = datacentre.getAllocator().getSymbol();
        
        ValidationUtils.addConstraintViolation(context, defaultMessage, "symbol");

        return datacentreSymbol.startsWith(allocatorSymbol + ".");
    }

}
