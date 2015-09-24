package com.superiad.glossary.persistence;

/**
 * Exception thrown when an invoked object is not found in the database.
 * @author Aaron Landry
 */
public class EntityNotFoundException extends Exception {

    /**
     * Empty constructor.
     */
    public EntityNotFoundException() {
    
    }

    /**
     * Constructor defining a message.
     * @param message Error message
     */
    public EntityNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Constructor defining message and exception chain.
     * @param message Error message
     * @param chain Exception chain
     */
    public EntityNotFoundException(String message, Exception chain) {
        super (message,chain);
    }
    
    /**
     * Constructor defining exception chain.
     * @param chain
     */
    public EntityNotFoundException(Exception chain) {
        super (chain);
    }
    
}
