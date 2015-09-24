package com.superiad.glossary.persistence;

import java.io.Serializable;

/**
 * An interface for enums intended to be persisted to a database column.
 * By convention, classes that inherit PersistableEnum should define a static method:
 * <code>
 *      public static E findForValue(Integer value);
 * </code>
 * Also by convention, enums assigned a negative value are for internal assignment
 * only and are not displayed to users in selection lists.
 * If a PersistableEnum is used as a Persistable property type, it must be
 * annotated with a @Converter
 * <code>
 *     @Converter("persistableEnum")
 * </code>
 * @see PersistableEnumConverter
 * @author Aaron
 */
public interface PersistableEnum extends Serializable {
    
    /**
     * Gets the internally assigned integer value assigned to the enum.  This value
     * is used primary for database persistence.
     * @return 
     */
    public Integer getValue();
    
    /**
     * Gets the string representation of the enum.
     * @return 
     */
    String getLabel();
    
    /**
     * Gets the string representation of the enum as an html element.
     * @return 
     */
    String getHtmlLabel();
    
    // For consistency with Persistables
    
    /**
     * An alias for getLabel(), implemented for consistency with Persistables in view elements.
     * @return 
     */
    String getSortableString();
    
    /**
     * An alias for getId(), implemented for consistency with Persistables in view elements.
     * @return 
     */
    Integer getId();
    
}
