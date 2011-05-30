package org.datacite.mds.service;

import javax.validation.ValidationException;

import org.datacite.mds.domain.Dataset;
import org.datacite.mds.web.api.NotFoundException;

/**
 * An interface to create and update DOIs
 */
public interface DoiService {

    /**
     * Creates a new or use a existing Dataset object and mint or update DOI.
     * Checks if the following conditions are met:
     * <ol>
     * <li>A datacentre is logged in and active</li>
     * <li>DOI has prefix belonging to the Datacentre</li>
     * <li>URL is a valid URL</li>
     * <li>domain in URL is on Datacentre's allowed list</li>
     * </ol>
     * 
     * @param doi
     *            handle to be created/updated
     * @param url
     *            location to be resolved, if null or empty - no minting
     * @param testMode
     *            if true, checks will be conducted but neither record in DB
     *            will be created nor handle minted
     * @throws HandleException
     *             exception from Handle Service
     * @throws SecurityException
     *             when datacentre not logged in, not active or has exceeded quota
     * @throws ValidationException
     *             when any of above conditions remaining not met
     */
    Dataset createOrUpdate(String doi, String url, boolean testMode) throws HandleException, SecurityException, ValidationException;
    
    Dataset resolve(String doi) throws HandleException, NotFoundException, SecurityException;

}
