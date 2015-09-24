package com.superiad.glossary.authentication;

/**
 * Exception thrown when the application cannot determine the actively logged in user.
 * @author Aaron Landry
 */
public class NoActiveUserException extends Exception {

    /**
     * Empty constructor.
     */
    public NoActiveUserException() {
    
    }

    /**
     * Constructor defining a message.
     * @param message Error message
     */
    public NoActiveUserException(String message) {
        super(message);
    }
    
    /**
     * Constructor defining message and exception chain.
     * @param message Error message
     * @param chain Exception chain
     */
    public NoActiveUserException(String message, Exception chain) {
        super (message,chain);
    }
    
    /**
     * Constructor defining exception chain.
     * @param chain
     */
    public NoActiveUserException(Exception chain) {
        super (chain);
    }
    
}
