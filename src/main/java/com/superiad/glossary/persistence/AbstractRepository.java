package com.superiad.glossary.persistence;

import com.superiad.glossary.model.Persistable;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.sessions.Session;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Abstract implementation of the Repository interface, providing definition for 
 * shared methods.
 * @param <ID> 
 * @param <E> 
 * @author Aaron
 */
@org.springframework.stereotype.Repository
public abstract class AbstractRepository<ID extends Serializable, E extends Persistable> implements Repository<ID,E> {

    // Injected services/beans
    @PersistenceContext
    private EntityManager entityManager;
    // Other properties
    private final Class<E> entityClass;
    
    @SuppressWarnings("unchecked")
    public AbstractRepository() {
        this.entityClass = (Class<E>)((ParameterizedType)getClass()
            .getGenericSuperclass()).getActualTypeArguments()[1];
    }
    
    @Override
    public ID getId(E entity) {
        return (ID)entity.getId();
    }

    @Override
    public E findById(ID id) throws EntityNotFoundException {
        E entity = entityManager.find(getEntityClass(), id);
        if (entity == null) {
            throw new EntityNotFoundException("Entity not found " + getEntityClass().getSimpleName() + ":" + id);
        }
        return entity;
    }
    
    @Override
    public List<E> findById(Collection<ID> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(entityClass);
        Root e = cq.from(entityClass);
        cq.where(e.get("id").in(ids));
        Query query = getEntityManager().createQuery(cq);
        return query.getResultList();
    }
    
    @Override
    public List<E> findAll() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(entityClass);
        Query query = getEntityManager().createQuery(cq);
        return query.getResultList();
    }
    
    @Override
    public Long count() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root e = cq.from(entityClass);
        cq.select(cb.count(e));
        Query query = getEntityManager().createQuery(cq);
        return (Long)query.getSingleResult();
    }
    
    @Override
    // WE DON'T WANT TO HANDLE TRANSACTIONS HERE, BUT WE SHOULD REQUIRE
    // THAT THE CALLER/SERVICE WRAPS THE CALL IN A TRANSACTION
    @Transactional(propagation=Propagation.MANDATORY)
    public E update(E entity) {
        entity.configure();
        return entityManager.merge(entity);
    }
    
    @Override
    // WE DON'T WANT TO HANDLE TRANSACTIONS HERE, BUT WE SHOULD REQUIRE
    // THAT THE CALLER/SERVICE WRAPS THE CALL IN A TRANSACTION
    @Transactional(propagation=Propagation.MANDATORY)
    public void persist(E entity) {
        entity.configure();
        entityManager.persist(entity);
    }

    @Override
    // WE DON'T WANT TO HANDLE TRANSACTIONS HERE, BUT WE SHOULD REQUIRE
    // THAT THE CALLER/SERVICE WRAPS THE CALL IN A TRANSACTION
    @Transactional(propagation=Propagation.MANDATORY)
    public void delete(E entity) {
        entityManager.remove(entity);
    }

    @Override
    // WE DON'T WANT TO HANDLE TRANSACTIONS HERE, BUT WE SHOULD REQUIRE
    // THAT THE CALLER/SERVICE WRAPS THE CALL IN A TRANSACTION
    @Transactional(propagation=Propagation.MANDATORY)
    public void deleteById(ID id) throws EntityNotFoundException {
        delete(findById(id));
    }

    @Override
    public void detach(E entity) {
        entityManager.detach(entity);
    }

    @Override
    public Class<E> getEntityClass() {
        return entityClass;
    }
    
    protected Session getJpaSession() {
        return ((JpaEntityManager)entityManager).getActiveSession();
    }
    
    protected EntityManager getEntityManager() {
        return entityManager;
    }
    
    protected void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    @Override
    public void flush() {
        getEntityManager().flush();
    }
    
    
    @Override
    public E findByLabel(String label) throws EntityNotFoundException, LabelNotUniqueException, LabelInvalidException {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(entityClass);
        Root e = cq.from(entityClass);
        cq.where(cb.equal(cb.lower(e.get(getLabelProperties()[0])), label.trim().toLowerCase()));
        Query query = getEntityManager().createQuery(cq);
        List<E> matches = query.getResultList();
        if (matches.isEmpty()) {
            throw new EntityNotFoundException();
        } 
        else if (matches.size() > 1) {
            throw new LabelNotUniqueException(label + " is not unique within the database.");
        }
        return matches.get(0);
    }
    
    @Override
    public String[] getLabelProperties() {
        return new String[] { "name" };
    }
    
}
