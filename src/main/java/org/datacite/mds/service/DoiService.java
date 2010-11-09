package org.datacite.mds.service;

import org.datacite.mds.domain.Dataset;

/**
 * An interface to create and update DOIs
 */
public interface DoiService {

    /**
     * Creates a Dataset object and mints DOI. Checks if the following
     * conditions are met:
     * <ol>
     * <li>A datacentre is logged in and active</li>
     * <li>DOI has prefix belonging to the Datacentre</li>
     * <li>URL is a valid URL</li>
     * <li>domain in URL is on Datacentre's allowed list</li>
     * </ol>
     * 
     * @param doi
     *            handle to be created
     * @param url
     *            location to be resolved, if null or empty - no minting
     * @param testMode
     *            if true, checks will be conducted but neither record in DB
     *            will be created nor handle minted
     * @throws HandleException
     *             exception from Handle Service
     * @throws SecurityException
     *             when any of above conditions not met
     */
    Dataset create(String doi, String url, boolean testMode) throws HandleException, SecurityException;

    /**
     * Updates DOI. Checks if the following conditions are met:
     * <ol>
     * <li>A datacentre is logged in and active</li>
     * <li>DOI belongs to the Datacentre</li>
     * <li>URL is a valid URL</li>
     * <li>domain in URL is on Datacentre's allowed list</li>
     * </ol>
     * 
     * @param doi
     *            handle to be updated
     * @param newUrl
     *            a new location
     * @param testMode
     *            if true, checks will be conducted but no handle will be
     *            updated
     * @throws HandleException
     *             exception from Handle Service
     * @throws SecurityException
     *             when any of above conditions not met
     */
    void update(String doi, String newUrl, boolean testMode) throws HandleException, SecurityException;

}
