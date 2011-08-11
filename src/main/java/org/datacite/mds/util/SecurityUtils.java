package org.datacite.mds.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;
import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.AllocatorOrDatacentre;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.service.SecurityException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * security related utils
 */
public class SecurityUtils {

    private static Logger log4j = Logger.getLogger(SecurityUtils.class);

    public static boolean isLoggedIn() {
        return getCurrentSymbolOrNull() != null;
    }

    public static boolean isLoggedInAsAllocator() {
        return getCurrentAllocatorOrNull() != null;
    }

    public static boolean isLoggedInAsDatacentre() {
        return getCurrentDatacentreOrNull() != null;
    }

    /**
     * retrieves Allocator object matching symbol used for logging into
     * application
     * 
     * @return Allocator object
     * @throws SecurityException
     *             when not logged in as Allocator
     */
    public static Allocator getCurrentAllocator() throws SecurityException {
        Allocator allocator = getCurrentAllocatorOrNull();
        if (allocator == null) {
            throw new SecurityException("not logged in as a allocator");
        }
        return allocator;
    }

    /**
     * retrieves Datacentre object matching symbol used for logging into
     * application with Datacentre's symbol
     * 
     * @return Datacentre object
     * @throws SecurityException
     *             when not logged in as Datacentre
     */
    public static Datacentre getCurrentDatacentre() throws SecurityException {
        Datacentre datacentre = getCurrentDatacentreOrNull();
        if (datacentre == null) {
            throw new SecurityException("not logged in as a datacentre");
        }
        return datacentre;
    }

    public static AllocatorOrDatacentre getCurrentAllocatorOrDatacentre() throws SecurityException {
        AllocatorOrDatacentre user = getCurrentAllocatorOrDatacentreOrNull();
        if (user == null) {
            throw new SecurityException("not logged in as a datacentre or allocator");
        }
        return user;
    }

    private static Allocator getCurrentAllocatorOrNull() {
        String symbol = getCurrentSymbolOrNull();
        return Allocator.findAllocatorBySymbol(symbol);
    }

    private static Datacentre getCurrentDatacentreOrNull() {
        String symbol = getCurrentSymbolOrNull();
        return Datacentre.findDatacentreBySymbol(symbol);
    }

    private static AllocatorOrDatacentre getCurrentAllocatorOrDatacentreOrNull() {
        Datacentre datacentre = getCurrentDatacentreOrNull();
        if (datacentre != null)
            return datacentre;
        else
            return getCurrentAllocatorOrNull();
    }

    private static String getCurrentSymbolOrNull() {
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuth == null) {
            return null;
        } else {
            return currentAuth.getName();
        }
    }

    /**
     * Checks if a Datacentre still has available DOIs
     * 
     * @param datacentre
     * @throws SecurityException
     *             Datacentre run out of quota
     */
    public static void checkQuota(Datacentre datacentre) throws SecurityException {
        if (datacentre.isQuotaExceeded()) {
            String message = "datacentre quota exceeded: " + datacentre.getSymbol();
            log4j.info(message);
            throw new SecurityException(message);
        }
    }

    public static void checkDatasetOwnership(Dataset dataset, AllocatorOrDatacentre user) throws SecurityException {
        String datacentreSymbol = dataset.getDatacentre().getSymbol();
        String allocatorSymbol = dataset.getDatacentre().getAllocator().getSymbol();
        String userSymbol = user.getSymbol();
        if (!(userSymbol.equals(datacentreSymbol) || userSymbol.equals(allocatorSymbol)))
            throw new SecurityException("cannot access dataset which belongs to another party");
    }

    private static List<String> ROLES = new ArrayList<String>() {
        {
            add("ROLE_DATACENTRE");
            add("ROLE_ALLOCATOR");
            add("ROLE_ADMIN");
            add("ROLE_DEV");
        }
    };

    public static boolean isUserSuperiorTo(AllocatorOrDatacentre superior, AllocatorOrDatacentre inferior) {
        int superiorLevel = ROLES.indexOf(superior.getRoleName());
        int inferiorLevel = ROLES.indexOf(inferior.getRoleName());

        if (superiorLevel <= inferiorLevel)
            return false;
        else if (superiorLevel >= ROLES.indexOf("ROLE_ADMIN"))
            return true;
        else if (inferior instanceof Datacentre) {
            Datacentre datacentre = (Datacentre) inferior;
            return datacentre.getAllocator() == superior;
        } else
            return false;
    }

    public static Collection<? extends AllocatorOrDatacentre> getDirectInferiorsOfCurrentAllocator() {
        try {
            Collection<? extends AllocatorOrDatacentre> users;
            Allocator allocator = getCurrentAllocator();
            if (allocator.getRoleName().equals("ROLE_ALLOCATOR"))
                users = Datacentre.findAllDatacentresByAllocator(allocator);
            else
                users = Allocator.findAllAllocators();

            Predicate inferiorPredicate = FilterPredicates.getAllocatorOrDatacentreIsInferiorOfPredicate(allocator);
            CollectionUtils.filter(users, inferiorPredicate);
            return users;
        } catch (SecurityException e) {
            return new ArrayList<AllocatorOrDatacentre>();
        }
    }

}
