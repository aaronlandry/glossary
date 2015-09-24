package com.superiad.glossary.persistence;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;

/**
 * Converter to transform an PersistableEnum to a value for database storage; and to
 * retrieve a PersistableEnum based on the stored value.  The Enum must implement
 * PersistableEnum and obey its contract.
 * @see PersistableEnum
 * @author Aaron
 */
public class PersistableEnumConverter implements Converter {
 
    private Class<? extends Enum> enumClass;
    private Class<?> identifierType;
    private Method getIndentifierMethod;
    private Method lookupFromIdentifierMethod;
    
    @Override
    public Object convertDataValueToObjectValue(Object data, Session session) {
        if (data == null) {
            return null; //getConvertableEnum();
        }
        try {
            if (Long.class.isAssignableFrom(identifierType)) {
                data = convertToLong(data);
            }
            else if (Integer.class.isAssignableFrom(identifierType)) {
                data = convertToInt(data);
            }
            return lookupFromIdentifierMethod.invoke(enumClass, new Object[] { data });
        } 
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException("Exception while invoking lookupFromIdentifierMethod method " + 
                    lookupFromIdentifierMethod.getName() + " of PersistableEnum class " + enumClass.getSimpleName(), e);
        }
    }

    @Override
    public Object convertObjectValueToDataValue(Object data, Session session) {
        if (data == null) {
            return null; //getPersistableEnum().convert();
        }
        try {
            return getIndentifierMethod.invoke(data, new Object[0]);
        } 
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException("Exception while invoking getIdentifierMethod " + getIndentifierMethod.getName() +
                    " of PersistableEnum class " + enumClass.getSimpleName(), e);
        }
    }
    
    @Override
    public void initialize(DatabaseMapping mapping, Session session) {
        // we use reflection to lookup the appropriate methods of the enum
        // it must obey the PersistableEnum contract and define findForValue
        enumClass = mapping.getAttributeClassification().asSubclass(Enum.class);
        if (!PersistableEnum.class.isAssignableFrom(enumClass)) {
            throw new RuntimeException("Class " + enumClass.getSimpleName() + " is not of type PersistableEnum.");
        }
        try {
            getIndentifierMethod = enumClass.getMethod("getValue", new Class[0]);
            identifierType = getIndentifierMethod.getReturnType();
        } 
        catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException("Failed to obtain getValue method for PersistableEnum " + enumClass.getSimpleName(), e);
        }
        try {
            lookupFromIdentifierMethod = enumClass.getMethod("findForValue", new Class[] { identifierType });
        } 
        catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException("Failed to obtain findValue method for PersistableEnum " + enumClass.getSimpleName(), e);
        }
    }
 
    @Override
    public boolean isMutable() {
        return false;
    }

    private Long convertToLong(Object data) {
        if (data == null) {
            return (Long)null;
        }
        if (Long.class.isInstance(data)) {
            return (Long)data;
        }
        if (String.class.isInstance(data)) {
            return Long.parseLong((String)data);
        }
        if (BigDecimal.class.isInstance(data)) {
            return ((BigDecimal)data).longValueExact();
        }
        return (Long)data;
    }
    
    private Integer convertToInt(Object data) {
        if (data == null) {
            return (Integer)null;
        }
        if (Integer.class.isInstance(data)) {
            return (Integer)data;
        }
        if (String.class.isInstance(data)) {
            return Integer.parseInt((String)data);
        }
        if (BigDecimal.class.isInstance(data)) {
            return ((BigDecimal)data).intValueExact();
        }
        return (Integer)data;
    }
    
}