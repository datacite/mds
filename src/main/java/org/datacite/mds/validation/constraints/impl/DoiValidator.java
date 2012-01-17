package org.datacite.mds.validation.constraints.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;
import org.datacite.mds.util.Utils;
import org.datacite.mds.util.ValidationUtils;
import org.datacite.mds.validation.constraints.Doi;

public class DoiValidator implements ConstraintValidator<Doi, String> {
    
    String node;

    public void initialize(Doi constraintAnnotation) {
        // nothing to initialize
    }

    public boolean isValid(String doi, ConstraintValidatorContext context) {
        if (doi == null) {
            return true;
        }
        String prefix = Utils.getDoiPrefix(doi);
        String suffix = Utils.getDoiSuffix(doi);

        if(StringUtils.isEmpty(prefix) || StringUtils.isEmpty(suffix))
            return false;
        
        boolean isPrefixOK = prefix.matches("10\\.(\\d)+");
        boolean hasValidChars = suffix.matches("[^\u0000-\u001F\u0080-\u009F]+");
        boolean hasValidSuffixStart = !suffix.matches("./.*");
        boolean hasSpaces = StringUtils.contains(doi, " ");
        
        if (!isPrefixOK)
            addCustomMessage("DoiPrefix.message", context);

        if (hasSpaces)
            addCustomMessage("Doi.spaces", context);

        return isPrefixOK && hasValidChars && hasValidSuffixStart && !hasSpaces;
    }
    
    private void addCustomMessage(String type, ConstraintValidatorContext context) {
        String msg = "{org.datacite.mds.validation.constraints." + type + "}";
        ValidationUtils.addConstraintViolation(context, msg, node);
    }

    public void setNodeForMessage(String node) {
        this.node = node;
    }
}
