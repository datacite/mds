package org.datacite.mds.service;

import java.util.Collection;

import org.datacite.mds.domain.AllocatorOrDatacentre;

public interface MagicAuthStringService {
    public Collection<String> getValidAuthStrings(AllocatorOrDatacentre user);

    public String getCurrentAuthString(AllocatorOrDatacentre user);

    public boolean isValidAuthString(AllocatorOrDatacentre user, String auth);
}
