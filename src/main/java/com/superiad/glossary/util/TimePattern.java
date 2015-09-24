package com.superiad.glossary.util;

import com.superiad.glossary.persistence.PersistableEnum;

public enum TimePattern implements PersistableEnum {

    STANDARD(1, "h:mm a", "12:00 AM","12 Hour"),
    MILITARY(2, "H:mm", "00:00","24 Hour"),
    DATABASE(-2, "HH:mm", "00:00","Internal"),
    NONE(-1, "", "","None"),
    LONG(-3,"hh:mm aaa","00:00","Internal");
    
    public static TimePattern findForValue(Integer value) {
        for (TimePattern tp : TimePattern.values()) {
            if (tp.getValue().equals(value)) {
                return tp;
            }
        }
        return NONE;
    }
    
    private String pattern;
    private String emptyString;
    private Integer value;
    private String userString;

    TimePattern(Integer value, String pattern, String emptyString, String userString) {
        this.value = value;
        this.pattern = pattern;
        this.emptyString = emptyString;
        this.userString = userString;
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

    public String getPattern() {
        return pattern;
    }

    @Override
    public Integer getValue() {
        return value;
    }
    
    public boolean isTwelveHour() {
        return equals(STANDARD);
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
