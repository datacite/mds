package org.datacite.mds.validation;

/**
 * An exception representing constraint violations
 */
public class ValidationException extends Exception {
    private static final long serialVersionUID = 1L;

    public ValidationException() {
        super();
    }

    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(Throwable cause) {
        super(cause);
    }
}
