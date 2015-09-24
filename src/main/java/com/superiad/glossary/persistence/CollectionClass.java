package com.superiad.glossary.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the class-type contained within a Collection.  This is used
 * by the auditing and validation framework in the case that entityType
 * cannot be determined from the targetEntity of @ManyToMany or @ManyToOne.  The type cannot be determined
 * from the return signature due to type erasure.
 * @author Aaron
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface CollectionClass { 
    /**
     * The class of the underlying collection.
     * @return 
     */
    Class value();
}