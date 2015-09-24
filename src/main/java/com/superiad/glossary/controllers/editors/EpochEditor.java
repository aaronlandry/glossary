package com.superiad.glossary.controllers.editors;

import com.superiad.glossary.model.SuperiadEpoch;
import java.beans.PropertyEditorSupport;

/**
 *
 * @author Aaron
 */

public class EpochEditor extends PropertyEditorSupport {

    public EpochEditor() {
        super();
    }
    
    @Override
    public void setAsText(String text) {
        if (text == null || text.toLowerCase().equals("null")) {
            setValue(null);
        } 
        else {
            Integer id = Integer.valueOf(text);
            if (id.equals(0)) {
                setValue(null);
                return;
            }
            setValue(SuperiadEpoch.findForValue(id));
        }
    }
    
    @Override
    public String getAsText() {
        SuperiadEpoch value = (SuperiadEpoch)getValue();
        return (value != null ? value.getValue().toString() : "");
    }
    
}
