package org.datacite.mds.validation.constraints.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.validator.UrlValidator;
import org.datacite.mds.validation.constraints.ListOfDomains;

public class ListOfDomainsValidator implements ConstraintValidator<ListOfDomains, String> {

    public void initialize(ListOfDomains constraintAnnotation) {
        // nothing to initialize
    }

    public boolean isValid(String domains, ConstraintValidatorContext context) {
        List<String> domainList = Arrays.asList(domains.split(","));
        for (String domain : domainList) {
            try {
                URL url = new URL("http://" + domain);
                UrlValidator urlValidator = new UrlValidator();
                if (!url.getHost().equals(domain) || !urlValidator.isValid(url.toString())) {
                    return false;
                }
            } catch (MalformedURLException ex) {
                return false;
            }
        }
        return true;
    }
}
