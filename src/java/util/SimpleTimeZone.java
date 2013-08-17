/*
 * @(#)SimpleTimeZone.java	1.12 97/03/05
 *
 * (C) Copyright Taligent, Inc. 1996 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - All Rights Reserved
 *
 * Portions copyright (c) 1996 Sun Microsystems, Inc. All Rights Reserved.
 *
 *   The original version of this source code and documentation is copyrighted
 * and owned by Taligent, Inc., a wholly-owned subsidiary of IBM. These
 * materials are provided under terms of a License Agreement between Taligent
 * and Sun. This technology is protected by multiple US and International
 * patents. This notice and attribution to Taligent may not be removed.
 *   Taligent is a registered trademark of Taligent, Inc.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */

package java.util;

/**
 * <code>SimpleTimeZone</code> is a concrete subclass of <code>TimeZone</code>
 * that represents a time zone for use with a Gregorian
 * calendar. This simple class does not handle historical
 * changes, and has limited rules.
 *
 * <P>
 * Use a negative value for <code>dayOfWeekInMonth</code> to indicate that
 * <code>SimpleTimeZone</code> should count from the end of the month backwards.
 * For example, Daylight Savings Time ends at the last
 * (dayOfWeekInMonth = -1) Sunday in October, at 2 AM in standard time.
 *
 * @see          Calendar
 * @see          GregorianCalendar
 * @see          TimeZone
 * @version      1.12 03/05/97
 * @author       David Goldsmith, Mark Davis, Chen-Lieh Huang
 */
public class SimpleTimeZone extends TimeZone {
    /**
     * Constructs a SimpleTimeZone with the given base time zone offset
     * from GMT and time zone ID. Timezone IDs can be obtained from
     * TimeZone.getAvailableIDs. Normally you should use TimeZone.getDefault
     * to construct a TimeZone.
     * @param rawOffset the given base time zone offset to GMT.
     * @param ID the time zone ID which is obtained from
     * TimeZone.getAvailableIDs.
     */
    public SimpleTimeZone(int rawOffset, String ID)
    {
        this.rawOffset = rawOffset;
        setID (ID);
    }

    /**
     * Constructs a SimpleTimeZone with the given base time zone offset
     * from GMT, time zone ID, time to start and end the daylight time.
     * Timezone IDs can be obtained from TimeZone.getAvailableIDs.
     * Normally you should use TimeZone.getDefault to create a TimeZone.
     * For a time zone that does not use daylight saving time, do not
     * use this constructor; instead you should use
     * SimpleTimeZone(rawOffset, ID).
     * @param rawOffset the given base time zone offset to GMT.
     * @param ID the time zone ID which is obtained from
     * TimeZone.getAvailableIDs.
     * @param startMonth the daylight savings starting month. Month is 0-based.
     * eg, 0 for January.
     * @param startDayOfWeekInMonth the daylight savings starting
     * day-of-week-in-month. Please see the member description for an example.
     * @param startDayOfWeek the daylight savings starting day-of-week.
     * Please see the member description for an example.
     * @param startTime the daylight savings starting time. Please see the
     * member description for an example.
     * @param endMonth the daylight savings ending month. Month is 0-based.
     * eg, 0 for January.
     * @param endDayOfWeekInMonth the daylight savings ending
     * day-of-week-in-month. Please see the member description for an example.
     * @param endDayOfWeek the daylight savings ending day-of-week. Please see
     * the member description for an example.
     * @param endTime the daylight savings ending time. Please see the member
     * description for an example.
     */
    public SimpleTimeZone(int rawOffset, String ID, int startMonth,
    int startDayOfWeekInMonth, int startDayOfWeek, int startTime,
    int endMonth, int endDayOfWeekInMonth, int endDayOfWeek, int endTime)
    {
        setID (ID);
        this.rawOffset = rawOffset;
        this.startMonth = startMonth;
        startDay = startDayOfWeekInMonth;
        startDayOfWeek = startDayOfWeek;
        this.startTime = startTime;
        this.endMonth = endMonth;
        endDay = endDayOfWeekInMonth;
        endDayOfWeek = endDayOfWeek;
        this.endTime = endTime;
        this.useDaylight = true;
    }

    /**
     * Sets the daylight savings starting year.
     * @param year the daylight savings starting year.
     */
    public void setStartYear(int year)
    {
        startYear = year;
        if (!useDaylight) useDaylight = true;
    }

    /**
     * Sets the daylight savings starting rule. For example, Daylight Savings
     * Time starts at the first Sunday in April, at 2 AM in standard time.
     * Therefore, you can set the start rule by calling:
     * setStartRule(TimeFields.APRIL, 1, TimeFields.SUNDAY, 2*60*60*1000);
     * @param month the daylight savings starting month. Month is 0-based.
     * eg, 0 for January.
     * @param dayOfWeekInMonth the daylight savings starting
     * day-of-week-in-month. Please see the member description for an example.
     * @param dayOfWeek the daylight savings starting day-of-week. Please see
     * the member description for an example.
     * @param time the daylight savings starting time. Please see the member
     * description for an example.
     */
    public void setStartRule(int month, int dayOfWeekInMonth, int dayOfWeek,
                             int time)
    {
        startMonth = month;
        startDay = dayOfWeekInMonth;
        startDayOfWeek = dayOfWeek;
        startTime = time;
        if (!useDaylight) useDaylight = true;
    }

    /**
     * Sets the daylight savings ending rule. For example, Daylight Savings
     * Time ends at the last (-1) Sunday in October, at 2 AM in standard time.
     * Therefore, you can set the end rule by calling:
     * setEndRule(TimeFields.OCTOBER, -1, TimeFields.SUNDAY, 2*60*60*1000);
     * @param month the daylight savings ending month. Month is 0-based.
     * eg, 0 for January.
     * @param dayOfWeekInMonth the daylight savings ending
     * day-of-week-in-month. Please see the member description for an example.
     * @param dayOfWeek the daylight savings ending day-of-week. Please see
     * the member description for an example.
     * @param time the daylight savings ending time. Please see the member
     * description for an example.
     */
    public void setEndRule(int month, int dayOfWeekInMonth, int dayOfWeek,
                           int time)
    {
        endMonth = month;
        endDay = dayOfWeekInMonth;
        endDayOfWeek = dayOfWeek;
        endTime = time;
        if (!useDaylight) useDaylight = true;
    }

    /**
     * Overrides TimeZone
     * Gets offset, for current date, modified in case of daylight savings.
     * This is the offset to add *to* UTC to get local time.
     * Please see TimeZone.getOffset for descriptions on parameters.
     */
    public int getOffset(int era, int year, int month, int day, int dayOfWeek,
                         int millis)
    {
        int result = rawOffset;

        if (month < startMonth || month > endMonth ||
            year < startYear || era != GregorianCalendar.AD)
            return result;
        if (month == startMonth)
        {
            int actualStart;
            if (startDay > 0)
                actualStart = 1 + (startDay-1)*7 +
                              (7 + startDayOfWeek - (dayOfWeek - day + 1)) % 7;
            else
                actualStart = monthLength[month] + (startDay+1)*7 -
                              (7 + (dayOfWeek + monthLength[month] - day) -
                               startDayOfWeek) % 7;
            if (day > actualStart || (day==actualStart && millis>=startTime))
                result += millisPerHour;
        }
        else if (month == endMonth)
        {
            int actualEnd;
            if (endDay > 0)
                actualEnd = 1 + (endDay-1)*7 +
                            (7 + endDayOfWeek - (dayOfWeek - day + 1)) % 7;
            else
                actualEnd = monthLength[month] + (endDay+1)*7 -
                            (7 + (dayOfWeek + monthLength[month] - day) -
                             endDayOfWeek) % 7;
            if (day < actualEnd || (day == actualEnd && millis < endTime))
                result += millisPerHour;
        }
        else
            result += millisPerHour;
        return result;
    }

    /**
     * Overrides TimeZone
     * Gets the GMT offset for this time zone.
     */
    public int getRawOffset()
    {
        // The given date will be taken into account while
        // we have the historical time zone data in place.
        return rawOffset;
    }

    /**
     * Overrides TimeZone
     * Sets the base time zone offset to GMT.
     * This is the offset to add *to* UTC to get local time.
     * Please see TimeZone.setRawOffset for descriptions on the parameter.
     */
    public void setRawOffset(int offsetMillis)
    {
        this.rawOffset = offsetMillis;
    }

    /**
     * Overrides TimeZone
     * Queries if this time zone uses Daylight Savings Time.
     */
    public boolean useDaylightTime()
    {
        return useDaylight;
    }

    /**
     * Overrides TimeZone
     * Queries if the given date is in Daylight Savings Time.
     */
    public boolean inDaylightTime(Date date) {
	GregorianCalendar gc = new GregorianCalendar();
	gc.setTime(date);
        if (this.getRawOffset() !=
            this.getOffset(GregorianCalendar.AD, 
			   gc.get(Calendar.YEAR),
                           gc.get(Calendar.MONTH),
			   gc.get(Calendar.DATE), 
			   gc.get(Calendar.DAY_OF_WEEK), 0))
            return true;
        else return false;
    }

    /**
     * Overrides Cloneable
     */
    public Object clone()
    {
        return (SimpleTimeZone)super.clone();
        // other fields are bit-copied
    }

    /**
     * Override hashCode.
     * Generates the hash code for the SimpleDateFormat object
     */
    public synchronized int hashCode()
    {
        return startMonth ^ startDay ^ startDayOfWeek ^ startTime ^
               endMonth ^ endDay ^ endDayOfWeek ^ endTime ^ rawOffset;
    }

    /**
     * Compares the equality of two SimpleTimeZone objects.
     * @param obj the SimpleTimeZone object to be compared with.
     * @return true if the given obj is the same as this SimpleTimeZone
     * object; false otherwise.
     */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (!(obj instanceof SimpleTimeZone))
            return false;

        SimpleTimeZone that = (SimpleTimeZone) obj;

        if (this.hashCode() != that.hashCode())
            return false;
        if (!this.getID().equals(that.getID()))
            return false;
        if (this.startMonth != that.startMonth)
            return false;
        if (this.startDay != that.startDay)
            return false;
        if (this.startDayOfWeek != that.startDayOfWeek)
            return false;
        if (this.startTime != that.startTime)
            return false;
        if (this.endMonth != that.endMonth)
            return false;
        if (this.endDay != that.endDay)
            return false;
        if (this.endDayOfWeek != that.endDayOfWeek)
            return false;
        if (this.endTime != that.endTime)
            return false;
        if (this.startYear != that.startYear)
            return false;
        if (this.rawOffset != that.rawOffset)
            return false;
        if (this.useDaylight != that.useDaylight)
            return false;
        return true;
    }

    // =======================privates===============================

    private int startMonth, startDay, startDayOfWeek, startTime;
    private int endMonth, endDay, endDayOfWeek, endTime;
    private int startYear;
    private int rawOffset;
    private boolean useDaylight=false; // indicate if this time zone uses DST
    private static final int millisPerHour = 60*60*1000;
    // WARNING: assumes that no rule is measured from the end of February,
    // since we don't handle leap years. Could handle assuming always
    // Gregorian, since we know they didn't have daylight time when
    // Gregorian calendar started.
    private final byte monthLength[] = {31,28,31,30,31,30,31,31,30,31,30,31};

}
