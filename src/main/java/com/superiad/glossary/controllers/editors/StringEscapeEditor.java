package com.superiad.glossary.controllers.editors;

import com.superiad.glossary.util.StringUtils;
import java.beans.PropertyEditorSupport;

/**
 * StringEscapeEditor provides tools for escaping fields passed to a 
 * Controller (to fight script/sql/etc injection).
 * @author Aaron
 */
public class StringEscapeEditor extends PropertyEditorSupport {

    /**
     * Creates a StringEscapeEditor.
     */
    public StringEscapeEditor() {
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
            //value = value.replaceAll("\\<.*?\\>","");
            // we rely on eclipselink to escape SQL
            //setValue(value);
            setValue(StringUtils.stripXSS(value,true));
        }
    }

    @Override
    public String getAsText() {
        Object value = getValue();
        return (value != null ? value.toString() : "");
    }
    
}