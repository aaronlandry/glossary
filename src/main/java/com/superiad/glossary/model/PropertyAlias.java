package com.superiad.glossary.model;

import java.lang.annotation.*;

/**
 * Annotation to specify the user-readable label for a property of an entity class.  
 * This alias is used by the view and service layer to generate user content, for
 * example in forms and validation output. PropertyAlias is only useful if
 * a readable string cannot be determined from the property name.  For example,
 * username = "Username", but "firstName" = "FirstName", so an alias of "First Name"
 * would add readability to the UI.
 * @author Aaron
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface PropertyAlias { 
    String value();
}