package com.superiad.glossary.persistence;

/**
 *
 * @author Aaron
 */
public class ClassNotManagedException extends Exception {
    
    public ClassNotManagedException() {
        
    }

    public ClassNotManagedException(String msg) {
        super(msg);
    }
    
    public ClassNotManagedException(Exception chain) {
        super(chain);
    }
    
    public ClassNotManagedException(String msg, Exception chain) {
        super(msg,chain);
    }
    
}
