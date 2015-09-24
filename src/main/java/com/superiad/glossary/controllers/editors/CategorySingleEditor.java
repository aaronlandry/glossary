package com.superiad.glossary.controllers.editors;

import com.superiad.glossary.model.Category;
import com.superiad.glossary.persistence.EntityNotFoundException;
import com.superiad.glossary.persistence.Repository;
import java.beans.PropertyEditorSupport;

/**
 *
 * @author Aaron
 */

public class CategorySingleEditor extends PropertyEditorSupport {

    private transient Repository repository;
    
    public CategorySingleEditor() {
        super();
    }
    
    @Override
    public void setAsText(String text) {if (text == null || text.toLowerCase().equals("null")) {
            setValue(null);
        } 
        else {
            Long id = Long.valueOf(text);
            if (id.equals(0L)) {
                setValue(null);
                return;
            }
            try {
                setValue(getRepository().findById(id));
            }
            catch (EntityNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
    
    private Repository getRepository() {
        if (repository == null) {
            repository = new Category().getRepository();
        }
        return repository;
    }
    
    @Override
    public String getAsText() {
        Category value = (Category)getValue();
        return (value != null ? value.getId().toString() : "");
    }
    
}
