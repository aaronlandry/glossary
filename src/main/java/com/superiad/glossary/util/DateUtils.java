package com.superiad.glossary.util;

import java.util.*;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;
import static java.util.Calendar.*;

/**
 * This class assumes -- as does the rest of the code -- that the application 
 * will be running with the default time zone set to UTC.
 *
 */
public class DateUtils {

    // these are NOT validation patterns!  Just quick checks to determine if 
    // the string MIGHT be this kind of input.
    private final static Pattern datePattern = Pattern.compile("\\d{1,4}[\\-\\/]\\d{1,4}[\\-\\/]\\d{1,4}");
    private final static Pattern datetimePattern = Pattern.compile("\\d{1,4}[\\-\\/]\\d{1,4}[\\-\\/]\\d{1,4}\\s\\d{1,2}\\:\\d{2}(\\s{0,1}[ap]m){0,1}",Pattern.CASE_INSENSITIVE);
    private final static Pattern timePattern = Pattern.compile("\\d{1,2}:\\d{2}(\\s{0,1}[ap]m){0,1}",Pattern.CASE_INSENSITIVE);
    private final static List<String> MONTHS = new ArrayList<>();
    private final static List<String> SHORT_MONTHS = new ArrayList<>();
    private final static List<Integer> YEARS = new ArrayList<>();
    private final static List<Integer> FUTUREYEARS = new ArrayList<>();
    static {
        MONTHS.addAll(Arrays.asList(new String[] { "January","February","March",
            "April","May","June","July","August","September","October","November","December"
        }));
        SHORT_MONTHS.addAll(Arrays.asList(new String[] { "Jan","Feb","Mar",
            "Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"
        }));
        for (int i = 1950; i <= Calendar.getInstance().get(Calendar.YEAR); i++) {
            YEARS.add(i);
        }
        for (int i = Calendar.getInstance().get(Calendar.YEAR); i <= Calendar.getInstance().get(Calendar.YEAR) + 15; i++) {
            FUTUREYEARS.add(i);
        }
    }
    
    public static DateType determineDateType(String textDate) {
        if (datetimePattern.matcher(textDate).matches()) {
            return DateType.DATETIME;
        }
        else if (datePattern.matcher(textDate).matches()) {
            return DateType.DATE;
        }
        else if (timePattern.matcher(textDate).matches()) {
            return DateType.TIME;
        }
        return null;
    }
    
    /**
     * Return a calendar representing the specified time in the specified
     * time zone.
     *
     * @param instant date to represent
     * @param tz timezone 
     * @return calender in the specified time zone representing the 
     *          specified time
     */
    public static Calendar getLocalizedCalendar(Date instant, TimeZone tz) {
        Calendar c = Calendar.getInstance(tz);
        c.setTimeInMillis(instant.getTime());
        return c;
    }

    /**
     * Return a calendar representing the current time in the specified 
     * time zone.
     *
     * @param tz timezone 
     * @return calender in the appropriate time zone representing the current 
     *         time
     */
    public static Calendar getLocalizedCalendar(TimeZone tz) {
        return getLocalizedCalendar(new Date(), tz);
    }

    /**
     * Return a date offset to a prior date by a number of milliseconds.
     * @param date date to offset
     * @param msBefore number of milliseconds to offset it by
     * @return offset date
     */
    public static Date msBefore(Date date, long msBefore) {
        Long d = date.getTime() - msBefore;
        return new Date(d);
    }

    /**
     * Return a date offset to a later date by a number of milliseconds.
     * @param date date to offset
     * @param msAfter number of milliseconds to offset it by
     * @return offset date
     */
    public static Date msAfter(Date date, long msAfter) {
        Long d = date.getTime() + msAfter;
        return new Date(d);
    }

    /**
     * Return a date offset to a prior date by a number of hours.
     * @param date date to offset
     * @param hoursBefore 
     * @return offset date
     */
    public static Date hoursBefore(Date date, int hoursBefore) {
        Long d = date.getTime() - hoursToMs(hoursBefore);
        return new Date(d);
    }

    /**
     * Return a date offset to a later date by a number of hours.
     * @param date date to offset
     * @param hoursAfter number of hours to offset it by
     * @return offset date
     */
    public static Date hoursAfter(Date date, int hoursAfter) {
        Long d = date.getTime() + hoursToMs(hoursAfter);
        return new Date(d);
    }

    public static Date minutesBefore(Date date, int minutesBefore) {
        Long d = date.getTime() - minutesToMs(minutesBefore);
        return new Date(d);
    }
    
    public static Date minutesAfter(Date date, int minutesAfter) {
        Long d = date.getTime() + minutesToMs(minutesAfter);
        return new Date(d);
    }

    /**
     * Return a date offset to a prior date by a number of business days.
     * Current implementation only excludes Sat/Sun, not holidays.
     * Review and implement Joda solution.
     * @param date date to offset
     * @param days number of days to offset it by
     * @return offset date
     */
    public static Date businessDaysBefore(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return DateUtils.businessDaysBefore(calendar, days).getTime();
    }
    
    /**
     * Return a date offset to a future date by a number of business days.
     * Current implementation only excludes Sat/Sun, not holidays.
     * Review and implement Joda solution.
     * @param date date to offset
     * @param days number of days to offset it by
     * @return offset date
     */
    public static Date businessDaysAfter(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return DateUtils.businessDaysAfter(calendar, days).getTime();
    }
    
    /**
     * Return a date offset to a prior date by a number of business days.
     * Current implementation only excludes Sat/Sun, not holidays.
     * Review and implement Joda solution.
     * @param date date to offset
     * @param days number of days to offset it by
     * @return offset date
     */
    public static Calendar businessDaysBefore(Calendar date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date.getTime());
        for(int i=0;i<days;) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            // ONLY INCREMENT IF WEEKDAY
            if(calendar.get(Calendar.DAY_OF_WEEK)<=5) {
                i++;
            }
        }
        return calendar;
    }
    
    /**
     * Return a date offset to a future date by a number of business days.
     * Current implementation only excludes Sat/Sun, not holidays.
     * Review and implement Joda solution.
     * @param date date to offset
     * @param days number of days to offset it by
     * @return offset date
     */
    public static Calendar businessDaysAfter(Calendar date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date.getTime());
        for(int i=0;i<days;) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            // ONLY INCREMENT IF WEEKDAY
            if(calendar.get(Calendar.DAY_OF_WEEK)<=5) {
                i++;
            }
        }
        return calendar;
    }
    
    public static long daysToMs(int days) {
        return 24L * hoursToMs(days);
    }
    
    public static long hoursToMs(int hours) {
        return hours * 3600000L;
    }

    public static long minutesToMs(int minutes) {
        return minutes * 60000L;
    }
    
    /**
     * Merge two dates, taking the year/month/day from the first,
     * and the hour/minute/second from the second.  The merging is done 
     * using the specified timezone.
     *
     * @param dayDate date from which year/month/day info is taken
     * @param timeDate date from which hour/minute/second info is taken
     * @param tz time zone wrapper
     * @return the resulting, merged date.
     */
    public static Date mergeDates(Date dayDate, Date timeDate, TimeZone tz) {
        Calendar dc = new GregorianCalendar(tz);
        Calendar tc = new GregorianCalendar(tz);
        dc.setTimeInMillis(dayDate.getTime());
        tc.setTimeInMillis(timeDate.getTime());
        return mergeCalendars(dc, tc, tz).getTime();
    }

    /**
     * Merge two calendars, taking y/m/d from the first, and h/m/s from the 
     * second.  The merged calendar uses the specified timezone.  
     *
     * @param dayCal calendar from which y/m/d info is taken
     * @param timeCal calendar from which h/m/s info is taken
     * @param tz timezone
     * @return merged calendar
     */
    public static Calendar mergeCalendars(Calendar dayCal, Calendar timeCal, 
            TimeZone tz) {
        Calendar merged = new GregorianCalendar(tz);
        merged.set(Calendar.YEAR, dayCal.get(Calendar.YEAR));
        merged.set(Calendar.MONTH, dayCal.get(Calendar.MONTH));
        merged.set(Calendar.DAY_OF_MONTH, dayCal.get(Calendar.DAY_OF_MONTH));
        merged.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
        merged.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
        merged.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));
        merged.set(Calendar.MILLISECOND,merged.getMinimum(Calendar.MILLISECOND));
        return merged;
    }
    
    public static Calendar beginningOfMonth(Calendar input, TimeZone tz) {
        Calendar output = Calendar.getInstance(tz);
        output.setTimeInMillis(input.getTimeInMillis());
        output.set(DAY_OF_MONTH,1);
        output.set(HOUR_OF_DAY, 0);
        output.set(MINUTE, 0);
        output.set(SECOND, 0);
        output.set(MILLISECOND, 0);
        return output;
    }
    
    public static Calendar beginningOfDay(Calendar input, TimeZone tz) {
        Calendar output = Calendar.getInstance(tz);
        output.setTimeInMillis(input.getTimeInMillis());
        output.set(HOUR_OF_DAY, 0);
        output.set(MINUTE, 0);
        output.set(SECOND, 0);
        output.set(MILLISECOND, 0);
        return output;
    }
    
    public static Date endOfWorkDay(Date referenceDate, TimeZone tz) {
        Calendar c = DateUtils.getDateCalendar(referenceDate, tz);
        c.set(Calendar.HOUR_OF_DAY, 17);
        return new Date(c.getTimeInMillis());
    }
    
    public static Date beginningOfWorkDay(Date referenceDate, TimeZone tz) {
        Calendar c = DateUtils.getDateCalendar(referenceDate, tz);
        c.set(Calendar.HOUR_OF_DAY, 8);
        return new Date(c.getTimeInMillis());
    }
    
    public static Date endOfMonth(Date pointInTime, TimeZone tz) {
        Calendar c = Calendar.getInstance(tz);
        c.setTime(pointInTime);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        return endOfDay(c,tz).getTime();
    }
    
    public static Calendar endOfDay(Calendar input, TimeZone tz) {
        Calendar output = Calendar.getInstance(tz);
        output.setTimeInMillis(input.getTimeInMillis());
        output.set(HOUR_OF_DAY, output.getActualMaximum(HOUR_OF_DAY));
        output.set(MINUTE, output.getActualMaximum(MINUTE));
        output.set(SECOND, output.getActualMaximum(SECOND));
        return output;
    }

    /**
     * Return a calendar for the specified date, zeroing out all fields
     * below 'day'.
     * @param d
     * @param tz
     * @return  
     */
    public static Calendar getDateCalendar(Date d, TimeZone tz) {
        return DateUtils.beginningOfDay(getLocalizedCalendar(d, tz),tz);
    }
    
    /**
     * Return a calendar for the specified date, zeroing out the second
     * and millisecond fields.
     * @param d
     * @param tz
     * @return  
     */
    public static Calendar getDatetimeCalendar(Date d, TimeZone tz) {
        Calendar c = getLocalizedCalendar(d, tz);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        return c;
    }
    
    /**
     * Return a calendar for the specified date, zeroing out all fields
     * except hour and minute.
     * @param d
     * @param tz
     * @return  
     */
    public static Calendar getTimeCalendar(Date d, TimeZone tz) {
        Calendar c = getLocalizedCalendar(d, tz);
        c.set(Calendar.YEAR,0);
        c.set(Calendar.MONTH,0);
        c.set(Calendar.DAY_OF_MONTH,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        return c;
    }

    /**
     * Returns the difference between the two dates as a [positive] number of 
     * milliseconds.
     * @param a
     * @param b
     * @return  
     */
    public static Long differenceAsMilli(Date a, Date b) {
        Long A = a.getTime();
        Long B = b.getTime();
        return Math.max(A, B) - Math.min(A,B);
    }
    
    /**
     * Returns the difference between the two dates as a [positive] number of 
     * hours.
     * @param a
     * @param b
     * @return  
     */
    public static Long differenceAsHours(Date a, Date b) {
        return differenceAsMilli(a,b) / 3600000L;
    }
    
    /**
     * <p>Checks if two dates are on the same day ignoring time.</p>
     * @param date1  the first date, not altered, not null
     * @param date2  the second date, not altered, not null
     * @return true if they represent the same day
     * @throws IllegalArgumentException if either date is <code>null</code>
     */
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }
    
    /**
     * <p>Checks if two calendars represent the same day ignoring time.</p>
     * @param cal1  the first calendar, not altered, not null
     * @param cal2  the second calendar, not altered, not null
     * @return true if they represent the same day
     * @throws IllegalArgumentException if either calendar is <code>null</code>
     */
    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }
    
    /**
     * <p>Checks if a date is today.</p>
     * @param date the date, not altered, not null.
     * @return true if the date is today.
     * @throws IllegalArgumentException if the date is <code>null</code>
     */
    public static boolean isToday(Date date) {
        return isSameDay(date, Calendar.getInstance().getTime());
    }
    
    /**
     * <p>Checks if a calendar date is today.</p>
     * @param cal  the calendar, not altered, not null
     * @return true if date is today or later
     * @throws IllegalArgumentException if the date is <code>null</code>
     */
    public static boolean isToday(Calendar cal) {
        return isSameDay(cal, Calendar.getInstance());
    }
    
    /**
     * Checks if a date occurs today or in the future.
     * @param date
     * @return 
     */
    public static boolean isTodayOrLater(Date date) {
        Date now = Calendar.getInstance().getTime();
        return date.after(now) || isSameDay(date, now);
    }
    
    /**
     * <p>Checks if a calendar date occurs today or in the future.</p>
     * @param cal  the calendar, not altered, not null
     * @return true if cal date is today or later
     * @throws IllegalArgumentException if the calendar is <code>null</code>
     */
    public static boolean isTodayOrLater(Calendar cal) {
        return isTodayOrLater(cal.getTime());
    }
    
    /**
     * @param referenceDate 
     * @param dayOfWeek should be one of the constants that might be returned by Calendar.DAY_OF_WEEK queries
     * @param tz 
     * @return  
     */
    public static Date nextDayOfWeek(Date referenceDate, int dayOfWeek, TimeZone tz) {
        Calendar objCalendar = Calendar.getInstance(tz);
        objCalendar.setTime(referenceDate);
        int counter = 0;
        while(true) {
            objCalendar.add(Calendar.DATE, 1);
            if (objCalendar.get(Calendar.DAY_OF_WEEK) == dayOfWeek) {
                return DateUtils.beginningOfDay(objCalendar,tz).getTime();
            }
            if (counter++ > 7) {
                throw new RuntimeException("Too many loops encountered.");
            }
        }
    }
    
    /**
     * Offsets the passed date by the number of business days specified in offset.
     * If offset is 0 and the passed date is on a weekend date, the date is 
     * adjusted to the closest non-business day.
     * @param referenceDate
     * @param offset
     * @param tz 
     * @return  
     */
    /* NYI
    public static Date offsetBusinessDays(Date referenceDate, int offset, TimeZone tz) {
        // TODO: holidays
        LocalDate localReference = LocalDate.fromCalendarFields(DateUtils.getLocalizedCalendar(referenceDate, tz));
        DateCalculator<LocalDate> cal = LocalDateKitCalculatorsFactory.getDefaultInstance()
            .getDateCalculator(null, HolidayHandlerType.FORWARD_UNLESS_MOVING_BACK);
        cal.setStartDate(new LocalDate(referenceDate.getTime(), DateTimeZone.forTimeZone(tz)));
        if (cal.isNonWorkingDay(localReference) && offset > 0) {
            offset--;   // deals with the fact that the engine automatically jumps
                        // forward one day with business days
        }
        cal.moveByBusinessDays(offset);
        return cal.getCurrentBusinessDate().toDateMidnight(tz.getDateTimeZone()).toDate();
    }
    */
    
    /**
     * 
     * @param referenceDate
     * @param tz
     * @param calendarUnit
     * @param offset
     * @return 
     */
    public static Date offset(Date referenceDate, TimeZone tz, int offset, int calendarUnit) {
        Calendar c = DateUtils.getLocalizedCalendar(referenceDate, tz);
        c.add(calendarUnit, offset);
        return c.getTime();
    }
    
    /**
     * Offsets the passed date by the number of days specified in offset.
     * @param referenceDate
     * @param offset 
     * @param tz
     * @return  
     */
    public static Date offsetDays(Date referenceDate, TimeZone tz, int offset) {
        Calendar objCalendar = Calendar.getInstance();
        Date returnDate = null;
        if (referenceDate != null) {
            objCalendar.setTime(referenceDate);
            if (offset == 0) {
                return referenceDate;
            } 
            else if (offset < 0) {
                while (offset < 0) {
                    objCalendar.add(Calendar.DATE, -1);
                    offset++;
                }
            } 
            else if (offset > 0) {
                while (offset > 0) {
                    objCalendar.add(Calendar.DATE, 1);
                    offset--;
                }
            }
            returnDate = objCalendar.getTime();
        }
        return returnDate;
    }
    
    /**
     * Converts a number of milliseconds to a number of hours (rounded down).
     * @param ms
     * @return  
     */
    public static Integer msToHours(Long ms) {
        return new Long(ms / 3600000).intValue();
    }
    
    /**
     * Converts a number of milliseconds to a number of hours (rounded up).
     * @param ms
     * @return  
     */
    public static Integer msToHoursUp(Long ms) {
        int hours = new Long(ms / 3600000).intValue();
        if ((ms % 3600000) > 0) {
            hours += 1;
        }
        return hours;
    }
    
    /**
     * Converts a number of milliseconds to a number of hours (no rounding).
     * @param ms
     * @return  
     */
    public static Double msToFractionalHours(Long ms) {
        return ms / 3600000D;
    }
    
    /**
     * Converts a number of milliseconds to a number of hours (with rounding for very close cases).
     * @param ms
     * @param millisecondTolerance
     * @return  
     */
    public static Double msToFractionalHours(Long ms, Long millisecondTolerance) {
        Long oneHour = 3600000L;
        Long over = ms % oneHour;
        if (over <= millisecondTolerance) {
            // within tolerance over an hour
            return new Double(Math.round(msToFractionalHours(ms)));
        }
        else if ((over + millisecondTolerance) >= oneHour) {
            // within tolerance beneath an hour
            return new Double(Math.round(msToFractionalHours(ms)));
        }
        else {
            return msToFractionalHours(ms);
        }
    }
    
    /**
     * Returns whether the two passed dates are on the same day.
     * @param a
     * @param b 
     * @param tz
     * @return  
     */
    public static Boolean sameDay(Date a, Date b, TimeZone tz) {
        Calendar A = DateUtils.getDateCalendar(a, tz);
        Calendar B = DateUtils.getDateCalendar(b,tz);
        return A.get(Calendar.DAY_OF_YEAR) == B.get(Calendar.DAY_OF_YEAR) && 
            A.get(Calendar.YEAR) == B.get(Calendar.YEAR);
    }
    
    /** 
     * Returns the difference between the two dates as a formatted string.
     * @param a
     * @param b
     * @return  
     */
    public static String differenceAsString(Date a, Date b) {
        return msToString(differenceAsMilli(a,b));
    }
    
    public static String msToString(Long ms) {
        if (ms == null) {
            return "Null";
        }
        if (ms == 0L) {
            return "0 seconds";
        }
        Long seconds = ms / 1000;
        if (seconds < 60) {
            return "<1 minute";
        }
        Long days = null;
        Long hours = null;
        Long minutes = null;
        int sInDay = 60 * 60 * 24;
        int sInHour = 60 * 60;
        int sInMinute = 60;
        if (seconds > sInDay) {
            days = seconds / sInDay;
            seconds = seconds % sInDay;
        }
        if (seconds > sInHour) {
            hours = seconds / sInHour;
            seconds = seconds % sInHour;
        }
        if (seconds > sInMinute) {
            minutes = seconds / sInMinute;
            seconds = seconds % sInMinute;
        }
        StringBuilder sb = new StringBuilder();
        if (days != null && days > 0) {
            sb.append(days).append(" day").append(days > 1 ? "s " : " ");
        }
        if (hours != null) {
            sb.append(hours).append(" hour").append(hours == 1 ? " " : "s ");
        }
        if (minutes != null) {
            sb.append(minutes).append(" minute").append(minutes == 1 ? "" : "s");
        }
        // ignore seconds for right now
        return sb.toString();
    }

    public static Date findNextWeekDay(Date origin, int weekday, TimeZone tz) {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(tz);
        c.setTime(origin);
        while (c.get(Calendar.DAY_OF_WEEK) != weekday){
            c.add(Calendar.DAY_OF_WEEK, 1);
        }
        return c.getTime();
    }
    
    /**
     * Print a date in the DT format used by iCal.  Note that iCal times 
     * are typically printed in UTC, so make sure the right date is passed
     * in.
     * @param date
     * @return  
     */
    public static String icalDate(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd'T'kkmmss'Z'");
        return fmt.format(date);
    }
    
    public static String secondsToString(Integer seconds) {
        int h = 0;
        int m = 0;
        int s = 0;
        if (seconds > 3600) {
            h = (int) Math.floor(seconds / 3600);
            seconds = seconds % 3600;
        }
        if (seconds > 60) {
            m = (int) Math.floor(seconds / 60);
            seconds = seconds % 60;
        }
        StringBuilder sb = new StringBuilder();
        if (h > 0) {
            sb.append(h).append(" hours");
        }
        if (m > 0) {
            sb.append(m).append(" minutes");
        }
        if (s > 0 || sb.length() == 0) {
            sb.append(s).append(" seconds");
        }
        return sb.toString();
    }
    
    // TLD FUNCTIONS
    
    public static List months() {
        return MONTHS;
    }
    
    public static List shortMonths() {
        return SHORT_MONTHS;
    }
    
    public static List years() {
        return YEARS;
    }
    
    public static List futureYears() {
        return FUTUREYEARS;
    }
    
    public static Boolean sameYear(Date date, Integer test) {
        if (date == null || test == null) {
            return false;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.YEAR) == test;
    }
    
    public static Boolean sameMonth(Date date, Integer test) {
        if (date == null || test == null) {
            return false;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MONTH) == test;
    }
    
    public static Boolean inPast(Date date) {
        return date != null && date.before(new Date());
    }
    
}
