package com.superiad.glossary.util;

import com.superiad.glossary.model.User;
import java.lang.reflect.Method;
import java.sql.Time;
import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * A utility class for doing basic date parsing.
 * @author Aaron Landry
 */
public class DateParser {
    
    private static Logger logger = LoggerFactory.getLogger(DateParser.class);

    private static DatePattern getDatePattern(User user) {
        return DatePattern.MMDDYY;
    }

    private static TimePattern getTimePattern(User user) {
        return TimePattern.STANDARD;
    }
    
    public static DateType getFormatFromContext(Method readMethod) {
        if (readMethod.getReturnType().equals(Time.class)) {
            return DateType.TIME;
        }
        return DateType.DATETIME;
    }
    
    private String input;
    private String repairedInput;
    private Date referenceDate;
    private Date referenceTime;
    private User user;
    private DateType dateType = DateType.DATETIME;

    public DateParser(User user, String input) {
        this.input = input;
        this.user = user;
        repair();
    }

    public DateParser setInput(String newInput) {
        input = newInput;
        repair();
        return this;
    }

    // clears preceding 0s that can cause validation errors
    private void repair() {
        if (input == null) {
            return;
        }
        List<String> datetimeParts = Arrays.asList(input.split(" "));
        repairedInput = "";
        int yearPos = DateParser.getDatePattern(user).yearPosition();
        // remove preceding zeros on first element/hours
        for (int i = 0, j = datetimeParts.size(); i < j; i++) { 
            if (datetimeParts.get(i).substring(0,1).equals("0"))
                    datetimeParts.set(i,datetimeParts.get(i).substring(1));
        }
        // arrange such that years are 4-digit
        List<String> dateParts = Arrays.asList(datetimeParts.get(0).split("[\\-\\/]"));
        for (int i = 0, j = dateParts.size(); i < j; i++) {
            if (i == yearPos) {
                if (dateParts.get(i).length() == 2) {
                    Integer v = Integer.valueOf(dateParts.get(i));
                    if (v <= 20) {
                        v += 2000;
                    }
                    else {
                        v += 1900;
                    }
                    dateParts.set(i,v.toString());
                }
            }   
        }
        // this will ensure that the date is delimited by "-" rather than "/"
        datetimeParts.set(0,StringUtils.collectionToDelimitedString(dateParts,"-"));
        repairedInput = StringUtils.collectionToDelimitedString(datetimeParts," ");
        // there must be a space the minute and am|pm
        if (repairedInput.endsWith("m") || repairedInput.endsWith("M")) {
            if (repairedInput.charAt(repairedInput.length() - 3) != ' ') {
                repairedInput = repairedInput.substring(0, repairedInput.length()-2) + 
                    " " + repairedInput.substring(repairedInput.length()-2);
            }
        }
    }
    
    public DateParser setReferenceDate(Date referenceDate) {
        this.referenceDate = referenceDate;
        return this; // chainable
    }
    
    public DateParser setReferenceTime(Date referenceTime) {
        this.referenceTime = referenceTime;
        return this;    // chainable
    }
    
    public DateParser setDateType(DateType dateType) {
        if (dateType == null) {
            throw new NullPointerException();
        }
        this.dateType = dateType;
        return this; // chainable
    }

    protected Date mergeDate(Date date) {
        Date mergedDate = date;
        if (referenceDate != null) {
            mergedDate = DateUtils.mergeDates(referenceDate, date, TimeZone.getDefault());
        }
        else if (referenceTime != null) {
            mergedDate = DateUtils.mergeDates(date, referenceTime, TimeZone.getDefault());
        }
        return mergedDate;
    }
    
    public Date parse() {
        String rawFormat = getDatePattern();
        DateFormat fmt = new SimpleDateFormat(rawFormat);
        fmt.setTimeZone(TimeZone.getDefault());
        Date date = null;
        try {
            date = fmt.parse(repairedInput);
        } 
        catch (Exception e) {
            //logger.warn("Failure parsing user date: " + repairedInput + " (" + rawFormat + ")", e);
            return null;
        }
        return mergeDate(date);
    }
    
    public String getDatePattern() {
        DatePattern dateFormat = DateParser.getDatePattern(user);
        TimePattern timeFormat = DateParser.getTimePattern(user);
        if (dateType.equals(DateType.DATE)) {
            timeFormat = TimePattern.NONE;
        }
        if (dateType.equals(DateType.TIME)) {
            dateFormat = DatePattern.NONE;
        }
        String rawFormat = null;
        if (dateFormat.equals(DatePattern.NONE) && timeFormat.equals(TimePattern.NONE)) {
            return null;
        }
        if (!dateFormat.equals(DatePattern.NONE)) {
            if (timeFormat.equals(TimePattern.NONE)) {
                rawFormat = dateFormat.getPattern();
            } 
            else {
                rawFormat = dateFormat.getPattern() + " " + timeFormat.getPattern();
            }
        } 
        else {
            rawFormat = timeFormat.getPattern();
        }
        return rawFormat;
    }
    
    public boolean isValid() {
        String rawFormat = getDatePattern();
        if (rawFormat == null) {
            return false;
        }
        if (repairedInput == null || repairedInput.toString().length() == 0) {
            return true;
        }
        DateFormat fmt = new SimpleDateFormat(rawFormat);
        fmt.setLenient(false);
        fmt.setTimeZone(TimeZone.getDefault());
        Date date = null;
        try {
            date = fmt.parse(repairedInput);
        } 
        catch (Exception e) {
            return false;
        }
        // IF IT DOESN'T CONVERT BACK, THERE IS AN ISSUE
        // ALLOW SINGLE DIGIT MONTHS/DAYS
        String connector = "-";
        List<String> parts = Arrays.asList(repairedInput.split(connector));
        int yearPos = DateParser.getDatePattern(user).yearPosition();
        for (int i = 0; i < parts.size(); i++) {
            if (i != yearPos) {
                if (parts.get(i).length() == 1) parts.set(i,"0" + parts.get(i));
            }
        }
        if (!fmt.format(date).toUpperCase().equals(StringUtils.collectionToDelimitedString(parts, connector).toUpperCase())) {
            return false;
        }
        return true;
    }
    
}
