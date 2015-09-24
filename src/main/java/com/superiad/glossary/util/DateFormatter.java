package com.superiad.glossary.util;

import com.superiad.glossary.model.User;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * A utility class for doing basic date formatting.
 * @author Aaron Landry
 */
public class DateFormatter {
    
    private static DatePattern getDatePattern(User user) {
        return DatePattern.MMDDYY;
    }

    private static TimePattern getTimePattern(User user) {
        return TimePattern.STANDARD;
    }

    private Date date;
    private Date referenceDate;
    private Date referenceTime;
    private Date mergedDate;
    private User user;
    private DateType dateType = DateType.DATETIME;
    private boolean includeTimeZone = true;

    public DateFormatter(User user, Date date) {
        this.user = user;
        this.date = date;
    }
    
    public DateFormatter setIncludeTimeZone(boolean includeTimeZone) {
        this.includeTimeZone = includeTimeZone;
        return this; // chainable
    }
    
    public DateFormatter setReferenceDate(Date referenceDate) {
        this.referenceDate = referenceDate;
        this.mergedDate = null;
        return this; // chainable
    }
    
    public DateFormatter setReferenceTime(Date referenceTime) {
        this.referenceTime = referenceTime;
        this.mergedDate = null;
        return this; // chainable
    }
    
    public DateFormatter setDateType(DateType dateType) {
        if (dateType == null) {
            throw new NullPointerException();
        }
        this.dateType = dateType;
        return this; // chainable
    }

    protected Date getMergedDate() {
        if (mergedDate != null) {
            return mergedDate;
        }
        mergedDate = date;
        if (referenceDate != null) {
            mergedDate = DateUtils.mergeDates(referenceDate, date, TimeZone.getDefault());
        }
        else if (referenceTime != null) {
            mergedDate = DateUtils.mergeDates(date, referenceTime, TimeZone.getDefault());
        }
        return mergedDate;
    }
    
    public String format() {
        DatePattern dateFormat = DateFormatter.getDatePattern(user);
        TimePattern timeFormat = DateFormatter.getTimePattern(user);
        String rtn = null;
        switch (dateType) {
            case DATE:
                rtn = format(dateFormat, null);
                break;
            case TIME:
                rtn = format(null, timeFormat);
                break;
            default:
                rtn = format(dateFormat, timeFormat);
        }
        return rtn;
    }
    
    public String format(DatePattern date_format, TimePattern time_format) {
        if (date_format == null || date_format == DatePattern.NONE) {
            if (time_format == TimePattern.NONE) {
                return "";
            } 
            else {
                return format(time_format);
            }
        }
        String parsed_date = format(date_format);
        if (time_format == null || time_format == TimePattern.NONE) {
            return parsed_date;
        }
        return parsed_date + " " + format(time_format);
    }

    public String format(DatePattern date_format) {
        String format_string = date_format.getPattern();
        SimpleDateFormat sfmt = new SimpleDateFormat(format_string);
        sfmt.setTimeZone(TimeZone.getDefault());
        String parsed_date;
        try {
            parsed_date = sfmt.format(getMergedDate());
        } 
        catch (Exception e) {
            parsed_date = date_format.getEmptyString();
            // NOT FINISHED - LOG ERROR
        }
        return parsed_date;
    }

    public String format(TimePattern time_format) {
        String format_string = time_format.getPattern();// + 
            //(includeTimeZone ? " z" : "");
        SimpleDateFormat sfmt = new SimpleDateFormat(format_string);
        sfmt.setTimeZone(TimeZone.getDefault());
        String parsed_time;
        try {
            parsed_time = sfmt.format(getMergedDate());
            if (includeTimeZone) {
                parsed_time += " " + TimeZone.getDefault().getDisplayName(true, TimeZone.SHORT);
            }
        } 
        catch (Exception e) {
            parsed_time = time_format.getEmptyString();
        // NOT FINISHED - LOG ERROR
        }
        return parsed_time;
    }
    
    /**
     * Shorthand for new DateFormatter(date).format()
     * @param user 
     * @param date
     * @return  
     */
    public static String format(User user, Date date) {
        if (date == null) {
            return "";
        }
        return new DateFormatter(user,date).setIncludeTimeZone(false).format();
    }
    
    public static String formatDateOnly(User user, Date date) {
        if (date == null) {
            return "";
        }
        return new DateFormatter(user,date).setDateType(DateType.DATE).format();
    }
    
    public static String textDate(User user, Date date) {
        if (date == null) {
            return "";
        }
        return new DateFormatter(user,date).format(DatePattern.TEXT_ONE);
    }
    
    public static String year(User user, Date date) {
        if (date == null) {
            return "";
        }
        return new DateFormatter(user,date).format(DatePattern.YEAR);
    }
    
    public static String monthAndYear(User user, Date date) {
        if (date == null) {
            return "";
        }
        return new DateFormatter(user,date).format(DatePattern.MONTH_YEAR);
    }
    
    public static String month(User user, Date date) {
        if (date == null) {
            return "";
        }
        return new DateFormatter(user,date).format(DatePattern.MONTH);
    }
    
    public static String day(User user, Date date) {
        if (date == null) {
            return "";
        }
        return new DateFormatter(user,date).format(DatePattern.DAY);
    }
    
}
