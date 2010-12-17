package org.datacite.mds.service;

import java.util.Collection;

import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;

public interface MagicAuthStringService {
    public Collection<String> getValidAuthStrings(String symbol);

    public String getCurrentAuthString(String symbol);

    public String getCurrentAuthString(Datacentre datacentre);

    public String getCurrentAuthString(Allocator allocator);

    public boolean isValidAuthString(String symbol, String auth);
}
