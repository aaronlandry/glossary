package com.superiad.glossary.util;

import com.superiad.glossary.persistence.PersistableEnum;

public enum DatePattern implements PersistableEnum {

    YYMMDD(2, "yyyy-MM-dd", "0000-00-00", "yy-mm-dd","Year - Month - Day",0),
    MMDDYY(1, "MM-dd-yyyy", "00-00-0000", "mm-dd-yy", "Month - Day - Year",2),
    DDMMYY(3, "dd-MM-yyyy", "00-00-0000", "dd-mm-yy", "Day - Month - Year",2),
    DATABASE(-1, "yyyy-MM-dd", "0000-00-00","yy-mm-dd","Internal",0),
    TEXT_ONE(-2, "MMMM dd, yyyy", "Date Not Parseable","","Error",null),
    TEXT_TWO(-3,"EEE, MMM d","Date Not Parseable","","Error",null),
    NONE(-4, "", "None","","None",null),
    MONTH_YEAR(-5,"MMM yyyy", "Date Not Parseable","M yy","Error",null),
    YEAR(-6,"yyyy", "Date Not Parseable","yy","Error",null),
    MONTH(-7,"MMM", "Date Not Parseable","M","Error",null),
    DAY(-8,"d", "Date Not Parseable","d","Error",null);

    public static DatePattern findForValue(Integer value) {
        for (DatePattern df : DatePattern.values()) {
            if (df.getValue().equals(value)) {
                return df;
            }
        }
        return MMDDYY;
    }
    
    private String pattern;
    private String emptyString;
    private Integer value;
    private String jsPattern;
    private String userString;
    private Integer yearPosition;
    
    private DatePattern(Integer value, String pattern, String emptyString, String jsPattern,
            String userString, Integer yearPosition) {
        this.value = value;
        this.pattern = pattern;
        this.emptyString = emptyString;
        this.jsPattern = jsPattern;
        this.userString = userString;
        this.yearPosition = yearPosition;
    }

    public String getEmptyString() {
        return emptyString;
    }

    @Override
    public String getLabel() {
        return userString;
    }
    
    @Override
    public String toString() {
        return getLabel();
    }

    @Override
    public Integer getValue() {
        return value;
    }

    public String getPattern() {
        return pattern;
    }

    public String getJsPattern() {
        return jsPattern;
    }

    public Integer yearPosition() {
        return yearPosition;
    }
    
    @Override
    public String getHtmlLabel() {
        return getLabel();
    }
    
    // For consistency with Persistables
    
    @Override
    public String getSortableString() {
        return getLabel();
    }
    
    @Override
    public Integer getId() {
        return getValue();
    }
    
};
