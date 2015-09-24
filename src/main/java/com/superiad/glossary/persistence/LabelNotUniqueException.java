package com.superiad.glossary.persistence;

/**
 * Exception thrown when an object cannot be loaded from the database by label due
 * to duplication.
 * @author Aaron Landry
 */
public class LabelNotUniqueException extends Exception {

    /**
     * Empty constructor.
     */
    public LabelNotUniqueException() {
    
    }

    /**
     * Constructor defining a message.
     * @param message Error message
     */
    public LabelNotUniqueException(String message) {
        super(message);
    }
    
    /**
     * Constructor defining message and exception chain.
     * @param message Error message
     * @param chain Exception chain
     */
    public LabelNotUniqueException(String message, Exception chain) {
        super (message,chain);
    }
    
    /**
     * Constructor defining exception chain.
     * @param chain
     */
    public LabelNotUniqueException(Exception chain) {
        super (chain);
    }
    
}
