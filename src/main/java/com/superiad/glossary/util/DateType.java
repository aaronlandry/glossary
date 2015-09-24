package com.superiad.glossary.util;

public enum DateType {

    DATETIME, DATE, TIME;
    
    public static DateType findFormat(String name) {
        for (DateType f : DateType.values()) {
            if (f.name().equals(name.toUpperCase())) {
                return f;
            }
        }
        return DATETIME;
    }

    private DateType() {
    }
    
    @Override
    public String toString() {
        return name();
    }
    
}