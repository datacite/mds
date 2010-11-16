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
    public static Allocator getAllocator() throws SecurityException {
        log4j.debug("retrieving user from security context");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String symbol;
        if (auth != null)
            symbol = auth.getName();
        else
            throw new SecurityException("please log in");

        log4j.debug("retrieving allocator from database");
        List all = (List) Allocator.findAllocatorsBySymbolEquals(symbol).getResultList();
        
        Allocator allocator;

        if (all.size() != 0) {
            allocator = (Allocator) all.get(0);
            log4j.debug("found allocator based on login: " + allocator.getSymbol());

            if (!allocator.getIsActive()) {
                log4j.warn("allocator is inactive: " + symbol);
                throw new SecurityException("allocator not activated");
            }
        } else {
            log4j.warn("allocator not found in database: " + symbol);
            throw new SecurityException("allocator not registered");
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
    public static Datacentre getDatacentre() throws SecurityException {
        log4j.debug("retrieving user from security context");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String datacentreSymbol;
        if (auth != null)
            datacentreSymbol = auth.getName();
        else
            throw new SecurityException("please log in");

        log4j.debug("retrieving datacentre from database");
        List datacentres = (List) Datacentre.findDatacentresBySymbolEquals(datacentreSymbol).getResultList();
        Datacentre datacentre;

        if (datacentres.size() != 0) {
            datacentre = (Datacentre) datacentres.get(0);
            log4j.debug("found datacentre based on login: " + datacentre.getSymbol());

            if (!datacentre.getIsActive()) {
                log4j.warn("datacentre is inactive: " + datacentreSymbol);
                throw new SecurityException("datacentre not activated");
            }
        } else {
            log4j.warn("datacentre not found in database: " + datacentreSymbol);
            throw new SecurityException("datacentre not registered");
        }

        return datacentre;
    }
    
    /**
     * Checks if a Datacentre still has available DOIs  
     * @param datacentre
     * @throws ForbiddenException Datacentre run out of quota
     */
    public static void checkQuota(Datacentre datacentre) throws SecurityException {
		if (datacentre.getDoiQuotaAllowed() <= datacentre.getDoiQuotaUsed()) {
			String message = "datacentre quota exceeded: " + datacentre.getSymbol();
			log4j.info(message);
			throw new SecurityException(message);
		}
	}
    
    /**
     * Checks if 3 conditions are met:
     * (1) DOI has prefix belonging to the Datacentre
     * (2) URL is a valid URL
     * (3) domain in URL is on Datacentre's allowed list
     * @param doi
     * @param url
     * @param datacentre
     * @throws ForbiddenException if any condition not met
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
				String message = "DOI's prefix not assigned to this datacentre: " + doi + ", "
						+ datacentre.getSymbol();
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
		String[] domains = datacentre.getDomains().split(";");
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


    public static Object getCurrentUser() {
        log4j.debug("get current auth");
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        String symbol = currentAuth.getName();
        log4j.debug("search for '" + symbol + "'");
        try {
            Allocator al = Allocator.findAllocatorsBySymbolEquals(symbol).getSingleResult();
            log4j.debug("found allocator '" + symbol + "'");
            return al;
        } catch (Exception e) {
        }
    
        try {
            Datacentre dc = Datacentre.findDatacentresBySymbolEquals(symbol).getSingleResult();
            log4j.debug("found datacentre '" + symbol + "'");
            return dc;
        } catch (Exception e) {
        }
    
        log4j.debug("no allocator or datacentre found");
    
        return null;
    }
}
