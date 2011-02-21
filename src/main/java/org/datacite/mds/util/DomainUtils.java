package org.datacite.mds.util;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.AllocatorOrDatacentre;
import org.datacite.mds.domain.Datacentre;

public class DomainUtils {

    /**
     * @return joined list of allocator and datacentre symbols
     */
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

    public static AllocatorOrDatacentre findAllocatorOrDatacentreBySymbol(String symbol) {
        AllocatorOrDatacentre user = Allocator.findAllocatorBySymbol(symbol);
        if (user == null) {
            user = Datacentre.findDatacentreBySymbol(symbol);
        }
        return user;
    }
    
    public static Allocator getAdmin() {
        for (Allocator allocator : Allocator.findAllAllocators()) {
            if (allocator.getRoleName().equals("ROLE_ADMIN")) {
                return allocator;
            }
        }
        return null;
    }
    
}
