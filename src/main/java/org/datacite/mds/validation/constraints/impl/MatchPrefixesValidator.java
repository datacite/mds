package org.datacite.mds.validation.constraints.impl;

import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Prefix;
import org.datacite.mds.util.ValidationUtils;
import org.datacite.mds.validation.constraints.MatchPrefixes;

public class MatchPrefixesValidator implements ConstraintValidator<MatchPrefixes, Datacentre> {
    String defaultMessage;
    
    public void initialize(MatchPrefixes constraintAnnotation) {
        defaultMessage = constraintAnnotation.message();
    }

    public boolean isValid(Datacentre datacentre, ConstraintValidatorContext context) {
        Set<Prefix> allowedPrefixes = datacentre.getAllocator().getPrefixes();
        Set<Prefix> prefixes = datacentre.getPrefixes();
        boolean isValid = allowedPrefixes.containsAll(prefixes); 
        ValidationUtils.addConstraintViolation(context, defaultMessage, "prefixes");
        return isValid;
    }
}
