package com.superiad.glossary.persistence;

import com.superiad.glossary.model.User;

/**
 * Extends the Repository interface to add user-specific functionality.
 * @author Aaron
 */
public interface UserRepository extends Repository<Long,User> {
    
    User findByUsername(String username) throws EntityNotFoundException;
    
}
