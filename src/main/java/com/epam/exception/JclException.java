package com.epam.exception;

/**
 * Created by Yayheniy_Lepkovich on 6/12/2017.
 */
public class JclException extends RuntimeException {
    /**
     * Default serial id
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor
     */
    public JclException() {
        super();
    }

    /**
     * @param message
     */
    public JclException(String message) {
        super( message );
    }

    /**
     * @param cause
     */
    public JclException(Throwable cause) {
        super( cause );
    }

    /**
     * @param message
     * @param cause
     */
    public JclException(String message, Throwable cause) {
        super( message, cause );
    }
}
