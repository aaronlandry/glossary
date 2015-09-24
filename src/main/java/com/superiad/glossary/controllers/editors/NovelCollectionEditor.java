package com.superiad.glossary.controllers.editors;

import com.superiad.glossary.model.Novel;
import com.superiad.glossary.model.Persistable;
import com.superiad.glossary.persistence.EntityNotFoundException;
import com.superiad.glossary.persistence.Repository;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;

/**
 *
 * @author Aaron
 */
public class NovelCollectionEditor extends CustomCollectionEditor {
    
    private transient Repository repository;
    
    public NovelCollectionEditor() {
        super(List.class);
    }
    
    @Override
    protected Object convertElement(Object element) {
        if (element instanceof Persistable) {
            return element;
        }
        if (element instanceof String) {
            String stringElement = (String)element;
            if (stringElement.isEmpty() || stringElement.toLowerCase().equals("null")) {
                return null;
            }
            Long id = Long.valueOf(stringElement);
            if (id.equals(0L)) {
                return null;
            }
            try {
                return getRepository().findById(id);
            } 
            catch (EntityNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return null;
    }
    
    private Repository getRepository() {
        if (repository == null) {
            repository = new Novel().getRepository();
        }
        return repository;
    }
    
    @Override
    // override to prevent adding null values to collection
    public void setValue(Object value) {
        super.setValue(value);
        Object obj = getValue();
        if (obj != null && obj instanceof Collection) {
            ((Collection)obj).removeAll(Collections.singleton(null));
        }
    }
    
}
