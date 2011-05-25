package org.datacite.mds.service;

import org.datacite.mds.web.api.NotFoundException;

/**
 * An interface to access Handle System
 */
public interface HandleService {
    
    /**
     * Resolve a DOI
     * @param doi name to be resolved
     * @return url of DOI
     * @throws NotFoundException if DOI does not exist or does not have a URL
     * @throws HandleException wraps exception from the server
     */
    String resolve(String doi) throws HandleException, NotFoundException;
    
    /**
     * Mints a new DOI
     * @param doi name to be minted
     * @param url location to be resolved 
     * @throws HandleException wraps exception from the server
     */
    void create(String doi, String url) throws HandleException;
    
    /**
     * Updates an existing DOI
     * @param doi name to be updated
     * @param newUrl a new location to be resolved
     * @throws HandleException wraps exception from the server
     */
    void update(String doi, String newUrl) throws HandleException;
    
}
