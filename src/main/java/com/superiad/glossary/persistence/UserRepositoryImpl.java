package com.superiad.glossary.persistence;

import com.superiad.glossary.model.User;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Repository for interacting with base Users.
 * @author Aaron
 */
@org.springframework.stereotype.Repository
public class UserRepositoryImpl extends AbstractRepository<Long,User> implements UserRepository {

    @Override
    public User findByUsername(String username) throws EntityNotFoundException {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(User.class);
        Root e = cq.from(User.class);
        Predicate local = cb.equal(e.get("username"), username);
        cq.where(local);
        Query query = getEntityManager().createQuery(cq);
        User u = (User)query.getSingleResult();
        if (u == null) {
            throw new EntityNotFoundException("No user found matching username " + username + ".");
        }
        return u;
    }
    
    @Override
    public String[] getLabelProperties() {
        return new String[] { "lastName", "firstName" };
    }
    
}
