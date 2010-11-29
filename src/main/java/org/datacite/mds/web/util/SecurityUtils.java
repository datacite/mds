package org.datacite.mds.web.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Prefix;
import org.datacite.mds.service.SecurityException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * security related utils
 */
public class SecurityUtils {

    private static Logger log4j = Logger.getLogger(SecurityUtils.class);

    /**
     * retrieves Allocator object matching symbol used for logging into
     * application
     * 
     * @return Allocator object
     * @throws SecurityException
     *             when no login info, no Allocator with such symbol or
     *             Allocator not active
     */
    public static Allocator getCurrentAllocatorWithExceptions() throws SecurityException {
        String symbol = getCurrentSymbol();
        if (symbol == null) {
            throw new SecurityException("please log in");
        }

        Allocator allocator = getCurrentAllocator();

        if (allocator == null) {
            throw new SecurityException("allocator not registered");
        }

        if (!allocator.getIsActive()) {
            log4j.warn("allocator is inactive: " + symbol);
            throw new SecurityException("allocator not activated");
        }

        return allocator;
    }

    /**
     * retrieves Datacentre object matching symbol used for logging into
     * application with Datacentre's symbol
     * 
     * @return Datacentre object
     * @throws SecurityException
     *             when no login info, no Datacentre with such symbol or
     *             Datacentre not active
     */
    public static Datacentre getCurrentDatacentreWithException() throws SecurityException {
        String symbol = SecurityUtils.getCurrentSymbol();
        if (symbol == null) {
            throw new SecurityException("please log in");
        }

        Datacentre datacentre = getCurrentDatacentre();
        
        if (datacentre == null) {
            throw new SecurityException("datacentre not registered");
        }

        if (!datacentre.getIsActive()) {
            log4j.warn("datacentre is inactive: " + symbol);
            throw new SecurityException("datacentre not activated");
        }

        return datacentre;
    }

    /**
     * Checks if a Datacentre still has available DOIs
     * 
     * @param datacentre
     * @throws ForbiddenException
     *             Datacentre run out of quota
     */
    public static void checkQuota(Datacentre datacentre) throws SecurityException {
        if (datacentre.getDoiQuotaAllowed() <= datacentre.getDoiQuotaUsed()) {
            String message = "datacentre quota exceeded: " + datacentre.getSymbol();
            log4j.info(message);
            throw new SecurityException(message);
        }
    }

    /**
     * Checks if 3 conditions are met: (1) DOI has prefix belonging to the
     * Datacentre (2) URL is a valid URL (3) domain in URL is on Datacentre's
     * allowed list
     * 
     * @param doi
     * @param url
     * @param datacentre
     * @throws ForbiddenException
     *             if any condition not met
     */
    public static void checkRestrictions(String doi, String urlString, Datacentre datacentre) throws SecurityException {
        log4j.debug("checking restrictions for " + doi);
        // prefix test
        if (doi != null && !doi.equals("")) {
            Set<Prefix> prefixes = datacentre.getPrefixes();
            boolean didMatchedSome = false;
            for (Prefix prefix : prefixes) {
                if (doi.startsWith(prefix.getPrefix())) {
                    didMatchedSome = true;
                    break;
                }
            }

            if (!didMatchedSome) {
                String message = "DOI's prefix not assigned to this datacentre: " + doi + ", " + datacentre.getSymbol();
                log4j.info(message);
                throw new SecurityException(message);
            }
        }
        log4j.debug("DOI prefix: OK");

        // URL test
        if (urlString == null || urlString.equals("") || urlString.length() < 10) {
            String message = "Empty or bad URL: " + urlString;
            log4j.warn(message);
            throw new SecurityException(message);
        }

        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            String message = "Malformed URL: " + url;
            log4j.warn(message);
            throw new SecurityException(message);
        }
        log4j.debug("URL: OK");

        // domain test
        String host = url.getHost();
        String[] domains = datacentre.getDomains().split(",");
        boolean didMatchedSome = false;
        for (String domain : domains) {
            if (host.toUpperCase().endsWith(domain.toUpperCase())) {
                didMatchedSome = true;
                break;
            }
        }

        if (!didMatchedSome) {
            String message = "URL with domain not assigned to this datacentre: " + host;
            log4j.warn(message);
            throw new SecurityException(message);
        }
        log4j.debug("Domain: OK");

        log4j.debug("All checks are OK");
    }

    /**
     * get the current logged in symbol
     * 
     * @return login symbol or null if not logged in
     */
    public static String getCurrentSymbol() {
        log4j.debug("get current auth");
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuth == null) {
            log4j.debug("not logged in");
            return null;
        } else {
            return currentAuth.getName();
        }
    }

    /**
     * get the current logged in allocator
     * 
     * @return allocator or null if not logged in (as a allocator)
     */
    public static Allocator getCurrentAllocator() {
        String symbol = getCurrentSymbol();
        return Allocator.findAllocatorBySymbol(symbol);
    }

    /**
     * get the current logged in datacentre
     * 
     * @return datacentre or null if not logged in (as a datacentre)
     */
    public static Datacentre getCurrentDatacentre() {
        String symbol = getCurrentSymbol();
        return Datacentre.findDatacentreBySymbol(symbol);
    }

}
