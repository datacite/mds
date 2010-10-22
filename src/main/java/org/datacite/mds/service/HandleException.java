package org.datacite.mds.service;

/**
 * Wrapper for exceptions from Handle Service
 */
public class HandleException extends Exception {
    
    private static final long serialVersionUID = 1L;

	public HandleException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public HandleException(String message) {
		super(message);
	}
}
