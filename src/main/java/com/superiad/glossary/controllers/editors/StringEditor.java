package com.superiad.glossary.controllers.editors;

import com.superiad.glossary.util.StringUtils;
import java.beans.PropertyEditorSupport;

/**
 * @author Aaron
 */
public class StringEditor extends PropertyEditorSupport {

    /**
     * Creates a StringEditor.
     */
    public StringEditor() {
        super();
    }

    @Override
    public void setAsText(String text) {
        if (text == null) {
            setValue(null);
        } 
        else {
            String value = text.trim();
            if ("".equals(value)) {
                setValue(null);
                return;
            }
            // WE ONLY ESCAPE JAVASCRIPT AND SIMILAR HERE
            // HTML/XML ARE VALID FOR THIS FIELD
            //setValue(StringUtils.escapeJavascript(value));
            setValue(StringUtils.stripXSS(value,false));
            //setValue(value);
        }
    }

    @Override
    public String getAsText() {
        Object value = getValue();
        return (value != null ? value.toString() : "");
    }
    
}