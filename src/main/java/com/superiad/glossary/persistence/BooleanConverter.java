package com.superiad.glossary.persistence;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;

/**
 * Converter to transform a Boolean value to a 1/0 for storage in 
 * databases that do not support boolean fields (ie, Oracle).
 * @author Aaron
 */
public class BooleanConverter implements Converter {
    
    @Override
    public Object convertDataValueToObjectValue(Object data, Session session) {
        if (data == null) {
            return Boolean.FALSE;
        }
        Integer d = Integer.valueOf(data.toString());
        return d == 1 ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public Object convertObjectValueToDataValue(Object data, Session session) {
        if (data == null) {
            return 0;
        }
        return ((Boolean)data) ? 1 : 0;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public void initialize(DatabaseMapping mapping, Session session) { }
    
}
