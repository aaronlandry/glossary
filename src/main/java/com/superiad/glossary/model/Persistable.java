package com.superiad.glossary.model;

import com.superiad.glossary.persistence.Repository;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Interface for a model entity.  Persistence of Persistable entities can be managed in one of two ways:
 * calling methods directly, eg:  persist(); or through the Repository, eg:
 * getRepository().persist(E).  Both call the same underlying code, and both
 * are required to be wrapped in a @Transactional block.  This domain-driven model also applies to 
 * managers managed via EntityIntrospector, e.g. EntityActionManagers and PXSecurityManagers.
 * @param <E> 
 * @param <R> 
 * @author Aaron
 */
public interface Persistable<E extends Persistable, R extends Repository> extends Serializable, Comparable<E> {
    
    /**
     * Sets the id of the object.  This method is defined for specific usage in 
     * EntityActionManager implementations.  It is dangerous!  Do not call on 
     * attached entities.
     * @param id 
     */
    void setId(Long id);
    
    /**
     * Returns the database id (primary key) of the object.
     * @return Unique database id
     */
    Long getId();
    
    /**
     * Returns the repository object responsible for managing persistence of the entity.
     * @return Managing Repository for entity class
     */
    R getRepository();
    
    /**
     * Gets the Date when the Entity was created.
     * @return Date when the Entity was created
     */
    Date getCreationDate();
    
    /**
     * Removes the entity from the underlying database by making a call to 
     * its controlling Repository.  This call is required to be wrapped in a transaction.
     */
    void delete();
    
    /**
     * Persists the entity to the underlying database by making a call to 
     * its controlling Repository.  This call is required to be wrapped in a transaction.
     */
    void persist();
    
    /**
     * Saves changes to the entity to the underlying database by making a call to 
     * its controlling Repository.  This call is required to be wrapped in a transaction.
     * @return The updated Entity
     */
    E update();
    
    /**
     * Detaches the entity from persistence context.
     */
    void detach();
    
    /**
     * The string label to be used in sorted context, ie tables and select lists.  
     * By default, it is the same as toString.
     * @return String label to be used in select lists
     */
    String getSortableString();
    
    /**
     * Performs any configuration necessary whenever an entity is created or updated.
     * (Use sparingly)
     */
    void configure();
 
    /**
     * The string label to be used in sorted context, ie tables and select lists,
     * truncated to 50 characters.  By default, it is the same as toString.
     * @return 
     */
    String getTruncatedSortableString();
    
    /**
     * Sets the date on which the object was craeted.
     * @param date 
     */
    void setCreationDate(Date date);
    
    Map<String,Object> toJson();
    
}
