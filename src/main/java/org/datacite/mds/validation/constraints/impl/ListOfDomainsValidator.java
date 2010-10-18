package org.datacite.mds.validation.constraints.impl;

import java.net.MalformedURLException;
import java.net.URL;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.validator.UrlValidator;
import org.datacite.mds.util.Utils;
import org.datacite.mds.validation.constraints.ListOfDomains;

public class ListOfDomainsValidator implements ConstraintValidator<ListOfDomains, String> {

    public void initialize(ListOfDomains constraintAnnotation) {
        // nothing to initialize
    }

    public boolean isValid(String domains, ConstraintValidatorContext context) {
        for (String domain : Utils.csvToList(domains)) {
            // for each comma separated value
            try {
                URL url = new URL("http://" + domain);
                if (!url.getHost().equals(domain)) {
                    // domain should only consists of the pure host name
                    return false;
                }
                
                UrlValidator urlValidator = new UrlValidator();
                if (!urlValidator.isValid(url.toString())) {
                    // url should be valid, e.g. "test.t" or "com" should be fail  
                    return false;
                }
            } catch (MalformedURLException ex) {
                // url should be well formed
                return false;
            }
        }
        return true;
    }
}
