package com.superiad.glossary.controllers.editors;

import com.superiad.glossary.model.User;
import com.superiad.glossary.util.DateFormatter;
import com.superiad.glossary.util.DateParser;
import com.superiad.glossary.util.DateType;
import java.beans.PropertyEditorSupport;
import java.util.Date;

/**
 *
 * @author Aaron
 */
public class DateEditor extends PropertyEditorSupport {

    private User user;
    
    public DateEditor(User user) {
        super();
        this.user = user;
    }
    
    @Override
    public void setAsText(String text) {
        if (text == null) {
            setValue(null);
            return;
        } 
        text = text.trim();
        if (text.isEmpty() || text.isEmpty() || text.equals("0")) {
            setValue(null);
            return;
        }
        // HACK
        if (text.length() == 4) {
            // THIS IS JUST A YEAR
            
            setValue(new DateParser(user,text).setDateType(DateType.DATETIME).parse());
        }
        else {
            setValue(new DateParser(user,text).setDateType(DateType.DATE).parse());
        }
    }
    
    @Override
    public String getAsText() {
        Date value = (Date)getValue();
        return (value != null ? DateFormatter.formatDateOnly(user, value) : "");
    }
    
}