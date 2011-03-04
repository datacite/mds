package org.datacite.mds.util;

import org.apache.log4j.Logger;
import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
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
    public static Allocator getCurrentAllocator() throws SecurityException {
        Allocator allocator = getCurrentAllocatorOrNull();

        if (allocator == null) {
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
    public static Datacentre getCurrentDatacentre() throws SecurityException {
        Datacentre datacentre = getCurrentDatacentreOrNull();
        
        if (datacentre == null) {
            throw new SecurityException("datacentre not registered");
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
        if (datacentre.isQuotaExceeded()) {
            String message = "datacentre quota exceeded: " + datacentre.getSymbol();
            log4j.info(message);
            throw new SecurityException(message);
        }
    }
    
    public static boolean isLoggedIn() {
        return getCurrentSymbol() != null;
    }
    
    public static boolean isLoggedInAsAllocator() {
        return getCurrentAllocatorOrNull() != null;
    }

    public static boolean isLoggedInAsDatacentre() {
        return getCurrentDatacentreOrNull() != null;
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
    static Allocator getCurrentAllocatorOrNull() {
        String symbol = getCurrentSymbol();
        return Allocator.findAllocatorBySymbol(symbol);
    }

    /**
     * get the current logged in datacentre
     * 
     * @return datacentre or null if not logged in (as a datacentre)
     */
    static Datacentre getCurrentDatacentreOrNull() {
        String symbol = getCurrentSymbol();
        return Datacentre.findDatacentreBySymbol(symbol);
    }
    

}
