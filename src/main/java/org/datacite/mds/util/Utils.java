package org.datacite.mds.util;

import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.validator.UrlValidator;
import org.apache.log4j.Logger;
import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Class with several static util methods
 */
public class Utils {

    static Logger log = Logger.getLogger(Utils.class);

    /**
     * returns the prefix of a doi
     * 
     * @param doi
     *            Doi (e.g. "10.5072/foobar")
     * @return doi prefix (e.g. "10.5072")
     */
    public static String getDoiPrefix(String doi) {
        return doi.split("/")[0];
    }

    /**
     * returns the suffix of a doi
     * 
     * @param doi
     *            Doi (e.g. "10.5072/foobar")
     * @return doi prefix (e.g. "foobar")
     */
    public static String getDoiSuffix(String doi) {
        return doi.split("/")[1];
    }

    /**
     * converts a string with comma separated values to a List of Strings
     * 
     * @param csv
     *            comma separated values
     * @return List of Strings
     */
    public static List<String> csvToList(String csv) {
        if (csv == null) {
            return new ArrayList<String>();
        }
        return Arrays.asList(csv.split(","));
    }

    /**
     * Wrapper to simply validate a property of an object.
     * 
     * @param object
     *            object to validate
     * @param propertyName
     *            property to validate (e.g. field)
     * @return true if property validates
     * @see javax.validation.Validator.validateProperty
     */
    public static boolean isValid(Object object, String propertyName) {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set violations = validator.validateProperty(object, propertyName);
        return violations.isEmpty();
    }

    /**
     * Check if a specific constraint annotation is violated
     * 
     * @param object
     *            object to validate
     * @param constraint
     *            constraint annotation to be checked
     * @return true if the given constraint is not violated
     */
    public static <T> boolean isConstraintValid(T object, Class<? extends Annotation> constraint) {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(object);

        for (ConstraintViolation<T> violation : violations) {
            if (violation.getConstraintDescriptor().getAnnotation().annotationType().equals(constraint)) {
                return false;
            }
        }

        return true;
    }

    public static void addConstraintViolation(ConstraintValidatorContext context, String message, String node) {
        context.buildConstraintViolationWithTemplate(message).addNode(node).addConstraintViolation();
    }

    public static void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }

    public static boolean isHostname(String str) {
        try {
            URL url = new URL("http://" + str);
            if (!url.getHost().equals(str)) {
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
        return true;
    }

    public static String getHostname(String urlStr) {
        URL url;
        try {
            url = new URL(urlStr);
            String hostname = url.getHost();
            return hostname;
        } catch (MalformedURLException e) {
            return null;
        }

    }

    public static Object getCurrentUser() {
        log.debug("get current auth");
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        String symbol = currentAuth.getName();
        log.debug("search for '" + symbol + "'");
        try {
            Allocator al = Allocator.findAllocatorsBySymbolEquals(symbol).getSingleResult();
            log.debug("found allocator '" + symbol + "'");
            return al;
        } catch (Exception e) {
        }

        try {
            Datacentre dc = Datacentre.findDatacentresBySymbolEquals(symbol).getSingleResult();
            log.debug("found datacentre '" + symbol + "'");
            return dc;
        } catch (Exception e) {
        }

        log.debug("no allocator or datacentre found");

        return null;
    }

    public static String formatXML(String xml) throws Exception {
        Document doc = DocumentHelper.parseText(xml);
        StringWriter sw = new StringWriter();
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter xw = new XMLWriter(sw, format);
        xw.write(doc);
        String result = sw.toString();
        return result;
    }

    public static SortedSet<String> getAllSymbols() {
        SortedSet<String> symbols = new TreeSet<String>();
        for (Datacentre datacentre : Datacentre.findAllDatacentres()) {
            symbols.add(datacentre.getSymbol());
        }
        for (Allocator allocator : Allocator.findAllAllocators()) {
            symbols.add(allocator.getSymbol());
        }
        return symbols;
    }

}
