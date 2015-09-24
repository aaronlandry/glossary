package com.superiad.glossary.persistence;

import com.superiad.glossary.model.Persistable;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Interface declaring common functionality for Data Access Objects.
 * @param <ID> 
 * @param <E> 
 * @author Aaron
 */
public interface Repository<ID extends Serializable, E extends Persistable> extends Serializable {
    
    /**
     * Returns Finds the database id of the passed entity.
     * @param entity The entity to inspect
     * @return Unique database id
     */
    ID getId(E entity);
    
    /**
     * Saves any changes to the entity to the underlying database.  Must
     * be wrapped in @Transactional.
     * @param entity A modified entity to update
     * @return The updated entity
     */
    E update(E entity);
    
    /**
     * Saves the passed object to the underlying database.
     * @param entity The entity to persist
     */
    void persist(E entity);
    
    /**
     * Removes the passed object from the underlying database.  Must
     * be wrapped in @Transactional.
     * @param entity The entity to delete
     */
    void delete(E entity);
    
    /**
     * Removes the object corresponding to the passed id from the underlying database.  Must
     * be wrapped in @Transactional.
     * @param id The id of the object to be deleted
     * @throws EntityNotFoundException  
     */
    void deleteById(ID id) throws EntityNotFoundException;
    
    /**
     * Returns the matching entity for the passed id from the database.
     * @param id The id to lookup
     * @return The entity matching the passed id
     * @throws EntityNotFoundException  
     */
    E findById(ID id) throws EntityNotFoundException;
    
    /**
     * Returns all matching entities for the passed id collection from the database.
     * @param ids The ids to lookup
     * @return The entities matching the passed id
     */
    List<E> findById(Collection<ID> ids);
    
    /**
     * Returns all entities from the database.
     * @return List of entities
     */
    List<E> findAll();
    
    /**
     * Returns a count of all entities in the database.
     * @return
     */
    Long count();
    
    /**
     * Makes the passed entity "transient"; ie detached from the database.
     * @param entity The entity to make transient
     */
    void detach(E entity);
    
    /**
     * Returns the entity class managed by the Repository.
     * @return Managed entity class
     */
    Class<E> getEntityClass();

    /**
     * Force a flush of the current changes.
     */
    void flush();
    
    E findByLabel(String label) throws EntityNotFoundException, LabelNotUniqueException, LabelInvalidException;
    
    String[] getLabelProperties();
    
}
