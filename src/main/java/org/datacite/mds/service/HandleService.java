package org.datacite.mds.service;

/**
 * An interface to access Handle System
 */
public interface HandleService {
    
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
