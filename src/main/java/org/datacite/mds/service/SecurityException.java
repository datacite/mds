package org.datacite.mds.service;

/**
 * An exception representing lack of credentials or other 
 * required attributes to complete a request e.g. quota
 */
public class SecurityException extends Exception {
    private static final long serialVersionUID = 1L;

    public SecurityException() {
        super();
    }

    public SecurityException(String message) {
        super(message);
    }
}
