package org.datacite.mds.web;

/**
 * An exception representing lack of credentials or other 
 * required attributes to complete a request e.g. quota
 */
public class ForbiddenException extends Exception {
    private static final long serialVersionUID = 1L;

    public ForbiddenException() {
        super();
    }

    public ForbiddenException(String message) {
        super(message);
    }
}
