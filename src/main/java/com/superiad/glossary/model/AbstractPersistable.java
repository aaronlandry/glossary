package com.superiad.glossary.model;

import com.superiad.glossary.persistence.Repository;
import com.superiad.glossary.util.StringUtils;
import com.superiad.glossary.validation.AllGroup;
import java.util.Date;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Abstract implementation of the Persistable model interface, defining common
 * methods.
 * @param <E> 
 * @param <R> 
 * @author Aaron
 */
@Access(AccessType.PROPERTY)
@MappedSuperclass
@Configurable // ALLOW @Autowired WITHOUT SPRING MANAGEMENT (VIA Aspectj)
//@Customizer(GridCacheCustomizer.class)    // ENABLE COHERENCE, NYI
//@Cacheable(false) // NYI, DISABLES ALL CACHING - REVIEW AND IMPLEMENT COHERENCE!!!!
//@Cache(type=CacheType.WEAK,isolation=CacheIsolationType.SHARED,expiry=3600000,alwaysRefresh=true)   // NYI, DISABLES ALL CACHING - REVIEW AND IMPLEMENT COHERENCE!!!!
public abstract class AbstractPersistable<E extends Persistable, R extends Repository> implements Persistable<E,R> {
    
    private Date creationDate = new Date();
    protected Long id;
    
    // public for introspection, do not change
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Sets the creation date of the Entity.
     * @param creationDate Date when the Entity was created
     */
    // NEEDS TO BE PUBLIC FOR INTROSPECTION, DO NOT CHANGE
    @Override
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    
    @Override
    @PropertyAlias("Creation Date")
    // VALIDATION
    @NotNull(message="Creation Date is required", groups = AllGroup.class )
    // PERSISTENCE
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creationDate", updatable = false, nullable = false)
    public Date getCreationDate() {
        return creationDate;
    }
    
    @Override
    // WE DON'T WANT TO HANDLE TRANSACTIONS HERE, BUT WE SHOULD REQUIRE
    // THAT THE CALLER/SERVICE WRAPS THE CALL IN A TRANSACTION
    @Transactional(propagation=Propagation.MANDATORY)
    public void persist() {
        getRepository().persist(this);
    }
    
    @Override
    // WE DON'T WANT TO HANDLE TRANSACTIONS HERE, BUT WE SHOULD REQUIRE
    // THAT THE CALLER/SERVICE WRAPS THE CALL IN A TRANSACTION
    @Transactional(propagation=Propagation.MANDATORY)
    public void delete() {
        getRepository().delete(this);
    }
    
    @Override
    // WE DON'T WANT TO HANDLE TRANSACTIONS HERE, BUT WE SHOULD REQUIRE
    // THAT THE CALLER/SERVICE WRAPS THE CALL IN A TRANSACTION
    @Transactional(propagation=Propagation.MANDATORY)
    public E update() {
        return (E)getRepository().update(this);
    }
    
    @Override
    // WE DON'T WANT TO HANDLE TRANSACTIONS HERE, BUT WE SHOULD REQUIRE
    // THAT THE CALLER/SERVICE WRAPS THE CALL IN A TRANSACTION
    @Transactional(propagation=Propagation.MANDATORY)
    public void detach() {
        getRepository().detach(this);
    }
    
    @Override
    @Transient
    public String getSortableString() {
        return toString();
    }
    
    @Override
    @Transient
    public String getTruncatedSortableString() {
        return StringUtils.truncate(getSortableString(), 30);
    }
    
    @Override
    // WE DON'T WANT TO HANDLE TRANSACTIONS HERE, BUT WE SHOULD REQUIRE
    // THAT THE CALLER/SERVICE WRAPS THE CALL IN A TRANSACTION
    @Transactional(propagation=Propagation.MANDATORY)
    public void configure() {
        /* */
    }
    
}
