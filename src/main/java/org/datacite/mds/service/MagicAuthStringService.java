package org.datacite.mds.service;

import java.util.Collection;

import org.datacite.mds.domain.AllocatorOrDatacentre;

public interface MagicAuthStringService {
    public Collection<String> getValidAuthStrings(String symbol);

    public String getCurrentAuthString(String symbol);

    public String getCurrentAuthString(AllocatorOrDatacentre user);

    public boolean isValidAuthString(String symbol, String auth);
}
