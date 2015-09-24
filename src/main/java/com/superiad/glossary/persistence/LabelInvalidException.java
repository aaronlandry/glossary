package com.superiad.glossary.persistence;

/**
 *
 * @author Aaron
 */
public class LabelInvalidException extends Exception {
 
    /**
     * Empty constructor.
     */
    public LabelInvalidException() {
    
    }

    /**
     * Constructor defining a message.
     * @param message Error message
     */
    public LabelInvalidException(String message) {
        super(message);
    }
    
    /**
     * Constructor defining message and exception chain.
     * @param message Error message
     * @param chain Exception chain
     */
    public LabelInvalidException(String message, Exception chain) {
        super (message,chain);
    }
    
    /**
     * Constructor defining exception chain.
     * @param chain
     */
    public LabelInvalidException(Exception chain) {
        super (chain);
    }
    
}