/*
 * @(#)Calendar.java	1.23 97/01/29
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
import java.io.Serializable;
import java.text.DateFormat;

/**
 * <code>Calendar</code> is an abstract base class for converting between
 * a <code>Date</code> object and a set of integer fields such as
 * <code>YEAR</code>, <code>MONTH</code>, <code>DAY</code>, <code>HOUR</code>,
 * and so on. (A <code>Date</code> object represents a specific instant in
 * time with millisecond precision. See
 * <a href="java.util.Date.html"><code>java.util.Date</code></a>
 * for information about the <code>Date</code> class.)
 *
 * <p>
 * Subclasses of <code>Calendar</code> interpret a <code>Date</code>
 * according to the rules of a specific calendar system. The JDK
 * provides one concrete subclass of <code>Calendar</code>:
 * <code>GregorianCalendar</code>. Future subclasses could represent
 * the various types of lunar calendars in use in many parts of the world.
 *
 * <p>
 * Like other locale-sensitive classes, <code>Calendar</code> provides a
 * class method, <code>getInstance</code>, for getting a generally useful
 * object of this type. <code>Calendar</code>'s <code>getInstance</code> method
 * returns a <code>GregorianCalendar</code> object whose
 * time fields have been initialized with the current date and time:
 * <blockquote>
 * <pre>
 * Calendar rightNow = Calendar.getInstance();
 * </pre>
 * </blockquote>
 *
 * <p>
 * A <code>Calendar</code> object can produce all the time field values
 * needed to implement the date-time formatting for a particular language
 * and calendar style (for example, Japanese-Gregorian, Japanese-Traditional).
 *
 * <p>
 * When computing a <code>Date</code> from time fields, two special circumstances
 * may arise: there may be insufficient information to compute the
 * <code>Date</code> (such as only year and month but no day in the month),
 * or there may be inconsistent information (such as "Tuesday, July 15, 1996"
 * -- July 15, 1996 is actually a Monday).
 *
 * <p>
 * <strong>Insufficient information.</strong> The calendar will use default
 * information to specify the missing fields. This may vary by calendar; for
 * the Gregorian calendar, the default for a field is the same as that of the
 * start of the epoch: i.e., YEAR = 1970, MONTH = JANUARY, DATE = 1, etc.
 *
 * <p>
 * <strong>Inconsistent information.</strong> The calendar will give preference
 * to the combinations of fields in the following order (and disregard other
 * inconsistent information).
 *
 * <blockquote>
 * <pre>
 * month + day-of-month
 * month + week-of-month + day-of-week
 * month + day-of-week-of-month + day-of-week
 * day-of-year
 * day-of-week + week-of-year
 * hour-of-day
 * ampm + hour-of-ampm
 * </pre>
 * </blockquote>
 *
 * <p>
 * <strong>Note:</strong> for some non-Gregorian calendars, different
 * fields may be necessary for complete disambiguation. For example, a full
 * specification of the historial Arabic astronomical calendar requires year,
 * month, day-of-month <em>and</em> day-of-week in some cases.
 *
 * <p>
 * <strong>Note:</strong> There are certain possible ambiguities in
 * interpretation of certain singular times, which are resolved in the
 * following ways:
 * <ol>
 *     <li> 24:00:00 "belongs" to the following day. That is,
 *          23:59 on Dec 31, 1969 &lt; 24:00 on Jan 1, 1970 &lt; 24:01:00 on Jan 1, 1970
 *
 *     <li> Although historically not precise, midnight also belongs to "am",
 *          and noon belongs to "pm", so on the same day,
 *          12:00 am (midnight) &lt; 12:01 am, and 12:00 pm (noon) &lt; 12:01 pm
 * </ol>
 *
 * <p>
 * The date or time format strings are not part of the definition of a
 * calendar, as those must be modifiable or overridable by the user at
 * runtime. Use <a href="java.text.DateFormat.html">java.text.DateFormat</a>
 * to format dates.
 *
 * <p>
 * <code>Calendar</code> provides an API for field "rolling", where fields
 * can be incremented or decremented, but wrap around. For example, rolling the
 * month up in the date "September 12, 1996" results in "October 12, 1996".
 *
 * <p>
 * <code>Calendar</code> also provides a date arithmetic function for 
 * adding the specified (signed) amount of time to a particular time field.
 * For example, subtracting 5 days from the date "September 12, 1996" results
 * in "September 7, 1996".
 *
 * @see          Date
 * @see          GregorianCalendar
 * @see          TimeZone
 * @see          java.text.DateFormat
 * @version      1.17 06 Jan 1997
 * @author Mark Davis, David Goldsmith, Chen-Lieh Huang
 */
public abstract class Calendar implements Serializable, Cloneable {

    /**
     * Useful constant for date and time. Used in time fields.
     * ERA is calendar specific.
     */
    public final static int ERA = 0;
    /**
     * Useful constant for date and time. Used in time fields.
     */
    public final static int YEAR = 1;
    /**
     * Useful constant for date and time. Used in time fields.
     */
    public final static int MONTH = 2;
    /**
     * Useful constant for date and time. Used in time fields.
     */
    public final static int WEEK_OF_YEAR = 3;
    /**
     * Useful constant for date and time. Used in time fields.
     */
    public final static int WEEK_OF_MONTH = 4;
    /**
     * Useful constant for date and time. Used in time fields.
     * DATE is the time field name for day-of-month.
     */
    public final static int DATE = 5;
    /**
     * Useful constant for date and time. Used in time fields.
     * This is a synonym for DATE.
     */
    public final static int DAY_OF_MONTH = 5;
    /**
     * Useful constant for date and time. Used in time fields.
     */
    public final static int DAY_OF_YEAR = 6;
    /**
     * Useful constant for date and time. Used in time fields.
     */
    public final static int DAY_OF_WEEK = 7;
    /**
     * Useful constant for date and time. Used in time fields.
     */
    public final static int DAY_OF_WEEK_IN_MONTH = 8;
    /**
     * Useful constant for date and time. Used in time fields.
     */
    public final static int AM_PM = 9;
    /**
     * Useful constant for date and time. Used in time fields.
     * HOUR is used for the 12-hour clock.
     */
    public final static int HOUR = 10;
    /**
     * Useful constant for date and time. Used in time fields.
     * HOUR_OF_DAY is used for the 24-hour clock.
     */
    public final static int HOUR_OF_DAY = 11;
    /**
     * Useful constant for date and time. Used in time fields.
     */
    public final static int MINUTE = 12;
    /**
     * Useful constant for date and time. Used in time fields.
     */
    public final static int SECOND = 13;
    /**
     * Useful constant for date and time. Used in time fields.
     */
    public final static int MILLISECOND = 14;
    /**
     * Useful constant for date and time. Used in time fields.
     */
    public final static int ZONE_OFFSET = 15;
    /**
     * Useful constant for date and time. Used in time fields.
     */
    public final static int DST_OFFSET = 16;
    /**
     * Useful constant for date and time.
     * FIELD_COUNT is used for the time field array creation.
     */
    public final static int FIELD_COUNT = 17;

    /**
     * Useful constant for days of week. Used in GregorianCalendar.
     */
    public final static int SUNDAY = 1;
    /**
     * Useful constant for days of week. Used in GregorianCalendar.
     */
    public final static int MONDAY = 2;
    /**
     * Useful constant for days of week. Used in GregorianCalendar.
     */
    public final static int TUESDAY = 3;
    /**
     * Useful constant for days of week. Used in GregorianCalendar.
     */
    public final static int WEDNESDAY = 4;
    /**
     * Useful constant for days of week. Used in GregorianCalendar.
     */
    public final static int THURSDAY = 5;
    /**
     * Useful constant for days of week. Used in GregorianCalendar.
     */
    public final static int FRIDAY = 6;
    /**
     * Useful constant for days of week. Used in GregorianCalendar.
     */
    public final static int SATURDAY = 7;
    /**
     * Useful constant for month. Used in GregorianCalendar.
     * Note: Calendar month is now 0-based.
     */
    public final static int JANUARY = 0;
    /**
     * Useful constant for month. Used in GregorianCalendar.
     */
    public final static int FEBRUARY = 1;
    /**
     * Useful constant for month. Used in GregorianCalendar.
     */
    public final static int MARCH = 2;
    /**
     * Useful constant for month. Used in GregorianCalendar.
     */
    public final static int APRIL = 3;
    /**
     * Useful constant for month. Used in GregorianCalendar.
     */
    public final static int MAY = 4;
    /**
     * Useful constant for month. Used in GregorianCalendar.
     */
    public final static int JUNE = 5;
    /**
     * Useful constant for month. Used in GregorianCalendar.
     */
    public final static int JULY = 6;
    /**
     * Useful constant for month. Used in GregorianCalendar.
     */
    public final static int AUGUST = 7;
    /**
     * Useful constant for month. Used in GregorianCalendar.
     */
    public final static int SEPTEMBER = 8;
    /**
     * Useful constant for month. Used in GregorianCalendar.
     */
    public final static int OCTOBER = 9;
    /**
     * Useful constant for month. Used in GregorianCalendar.
     */
    public final static int NOVEMBER = 10;
    /**
     * Useful constant for month. Used in GregorianCalendar.
     */
    public final static int DECEMBER = 11;
    /**
     * Useful constant for month. Used in GregorianCalendar.
     * UNDECIMBER is an artifical name. This 13th month is for lunar
     * calendars.
     */
    public final static int UNDECIMBER = 12;
    /**
     * Useful constant for hour in 12-hour clock. Used in GregorianCalendar.
     */
    public final static int AM = 0;
    /**
     * Useful constant for hour in 12-hour clock. Used in GregorianCalendar.
     */
    public final static int PM = 1;

    //
    // Internal notes:
    // Calendar contains two kinds of time representations: current "time" in
    // milliseconds, and a set of time "fields" representing the current time.
    // Calling setTime() method only sets the current time without recomputing
    // the time fields until the next get() method is called. Similarly,
    // calling one of the set() methods only sets time fields without
    // recomputing the current time value until the next getTime() method is
    // called.
    //

    /**
     * The time fields containing values into which the millis is computed.
     */
    protected int           fields[];
    /**
     * The flags which tell if a specified time field for the calendar is set.
     */
    protected boolean       isSet[];
    /**
     * The current time set for the calendar.
     */
    protected long          time;
    /**
     * The flag which indicates if the current time is set for the calendar.
     */
    protected boolean       isTimeSet;
    /**
     * The flag which tells if the time fields are set for the calendar.
     */
    protected boolean       areFieldsSet;

    /**
     * @see #setLenient
     */
    private   boolean       lenient=true;

    /**
     * Time zone affects the time calculation done by Calendar. Calendar uses
     * the time zone data to produce the local time. Both firstDayOfWeek
     * and minimalDaysInFirstWeek are locale-dependent. For example,
     * in US locale, firstDayOfWeek is SUNDAY; minimalDaysInFirstWeek is 1.
     * They are used to figure out the week count for a specific date for
     * a given locale. These must be set when a Calendar is constructed.
     */
    private   TimeZone      zone;
    private   int           firstDayOfWeek;
    private   int           minimalDaysInFirstWeek;

    /**
     * Constructs a Calendar with the default time zone as returned
     * by TimeZone.getDefault(), and the default locale.
     * @see     TimeZone#getDefault
     */
    protected Calendar()
    {
        fields = new int[FIELD_COUNT];
        isSet = new boolean[FIELD_COUNT];

        zone = TimeZone.getDefault();
        setWeekCountData(Locale.getDefault());
    }

    /**
     * Constructs a Calendar with the given time zone and locale.
     * @param zone the given time zone.
     */
    protected Calendar(TimeZone zone, Locale aLocale)
    {
        fields = new int[FIELD_COUNT];
        isSet = new boolean[FIELD_COUNT];

        this.zone = zone;
        setWeekCountData(aLocale);
    }

    /**
     * Gets a Calendar using the default timezone and locale.
     * @return a Calendar.
     */
    public static synchronized Calendar getInstance()
    {
    	return new GregorianCalendar();
    }

    /**
     * Gets a Calendar using the given timezone and default locale.
     * @param zone the given timezone.
     * @return a Calendar.
     */
    public static synchronized Calendar getInstance(TimeZone zone)
    {
    	return new GregorianCalendar(zone, Locale.getDefault());
    }

    /**
     * Gets a Calendar using the default timezone and given locale.
     * @param aLocale the given locale.
     * @return a Calendar.
     */
    public static synchronized Calendar getInstance(Locale aLocale)
    {
    	return new GregorianCalendar(TimeZone.getDefault(), aLocale);
    }

    /**
     * Gets a Calendar using the given timezone and given locale.
     * @param zone the given timezone.
     * @param aLocale the given locale.
     * @return a Calendar.
     */
    public static synchronized Calendar getInstance(TimeZone zone,
                                                   Locale aLocale)
    {
    	return new GregorianCalendar(zone, aLocale);
    }

    /**
     * Gets the set of locales for which Calendars are installed.
     * @return the set of locales for which Calendars are installed.
     */
    public static synchronized Locale[] getAvailableLocales()
    {
        return DateFormat.getAvailableLocales();
    }

    /**
     * Converts Calendar's time field values to UTC as milliseconds.
     */
    protected abstract void computeTime();

    /**
     * Converts UTC as milliseconds to time field values.
     * This allows you to sync up the time field values with
     * a new time that is set for the calendar.
     */
    protected abstract void computeFields();

    /**
     * Gets this Calendar's current time.
     * @return the current time.
     */
    public final Date getTime() {
        return new Date( getTimeInMillis() );
    }

    /**
     * Sets this Calendar's current time with the given Date.
     * @param date the given Date.
     */
    public final void setTime(Date date) {
        setTimeInMillis( date.getTime() );
    }

    /**
     * Gets this Calendar's current time as a long.
     * @return the current time as milliseconds from the epoch.
     */
    protected long getTimeInMillis() {
        if (!isTimeSet) computeTime();
        return time;
    }

    /**
     * Sets this Calendar's current time from the given long value.
     * @param date the new time in milliseconds from the epoch.
     */
    protected void setTimeInMillis( long millis ) {
        areFieldsSet = false;
        isTimeSet = true;
        time = millis;
    }


    /**
     * Gets the value for a given time field.
     * @param field the given time field.
     * @return the value for the given time field.
     */
    public final int get(int field)
    {
        if (!areFieldsSet) computeFields();
        return fields[field];
    }

    /**
     * Gets the value for a given time field. This is an internal
     * fast time field value getter for the subclasses.
     * @param field the given time field.
     * @return the value for the given time field.
     */
    protected final int internalGet(int field)
    {
        return fields[field];
    }

    /**
     * Sets the time field with the given value.
     * @param field the given time field.
     * @param value the value to be set for the given time field.
     */
    public final void set(int field, int value)
    {
        isTimeSet = false;
        fields[field] = value;
        isSet[field] = true;
        areFieldsSet = true;
    }

    /**
     * Sets the values for the fields year, month, and date.
     * @param year the value used to set the YEAR time field.
     * @param month the value used to set the MONTH time field.
     * Month value is 0-based. e.g., 0 for January.
     * @param date the value used to set the DATE time field.
     */
    public final void set(int year, int month, int date)
    {
        set(YEAR, year);
        set(MONTH, month);
        set(DATE, date);
    }

    /**
     * Sets the values for the fields year, month, date, hour, and minute.
     * @param year the value used to set the YEAR time field.
     * @param month the value used to set the MONTH time field.
     * Month value is 0-based. e.g., 0 for January.
     * @param date the value used to set the DATE time field.
     * @param hour the value used to set the HOUR_OF_DAY time field.
     * @param minute the value used to set the MINUTE time field.
     */
    public final void set(int year, int month, int date, int hour, int minute)
    {
        set(YEAR, year);
        set(MONTH, month);
        set(DATE, date);
        set(HOUR_OF_DAY, hour);
        set(MINUTE, minute);
    }

    /**
     * Sets the values for the fields year, month, date, hour, minute,
     * and second.
     * @param year the value used to set the YEAR time field.
     * @param month the value used to set the MONTH time field.
     * Month value is 0-based. e.g., 0 for January.
     * @param date the value used to set the DATE time field.
     * @param hour the value used to set the HOUR_OF_DAY time field.
     * @param minute the value used to set the MINUTE time field.
     * @param second the value used to set the SECOND time field.
     */
    public final void set(int year, int month, int date, int hour, int minute,
                          int second)
    {
        set(YEAR, year);
        set(MONTH, month);
        set(DATE, date);
        set(HOUR_OF_DAY, hour);
        set(MINUTE, minute);
        set(SECOND, second);
    }

    /**
     * Clears the values of all the time fields.
     */
    public final void clear()
    {
        isSet = new boolean[FIELD_COUNT];
        fields = new int[FIELD_COUNT];
        areFieldsSet = false;
    }

    /**
     * Clears the value in the given time field.
     * @param field the time field to be cleared.
     */
    public final void clear(int field)
    {
        isSet[field] = false;
        fields[field] = 0;
    }

    /**
     * Determines if the given time field has a value set.
     * @return true if the given time field has a value set; false otherwise.
     */
    public final boolean isSet(int field)
    {
        return isSet[field];
    }

    /**
     * Fills in any unset fields in the time field list.
     */
    protected void complete()
    {
        computeTime();  // computes time from incomplete fields
        computeFields();// fills in unset fields
    }

    /**
     * Compares the time field records.
     * Equivalent to comparing result of conversion to UTC.
     * @param when the Calendar to be compared with this Calendar.
     * @return true if the current time of this Calendar is equal
     * to the time of Calendar when; false otherwise.
     */
    abstract public boolean equals(Object when);

    /**
     * Compares the time field records.
     * Equivalent to comparing result of conversion to UTC.
     * @param when the Calendar to be compared with this Calendar.
     * @return true if the current time of this Calendar is before
     * the time of Calendar when; false otherwise.
     */
    abstract public boolean before(Object when);

    /**
     * Compares the time field records.
     * Equivalent to comparing result of conversion to UTC.
     * @param when the Calendar to be compared with this Calendar.
     * @return true if the current time of this Calendar is after
     * the time of Calendar when; false otherwise.
     */
    abstract public boolean after(Object when);

    /**
     * Date Arithmetic function.
     * Adds the specified (signed) amount of time to the given time field,
     * based on the calendar's rules. For example, to subtract 5 days from
     * the current time of the calendar, you can achieve it by calling:
     * <p>add(Calendar.DATE, -5).
     * @param field the time field.
     * @param amount the amount of date or time to be added to the field.
     */
    abstract public void add(int field, int amount);

    /**
     * Time Field Rolling function.
     * Rolls (up/down) a single unit of time on the given time field. For
     * example, to roll the current date up by one day, you can achieve it
     * by calling:
     * <p>roll(Calendar.DATE, true).
     * When rolling on the year or Calendar.YEAR field, it will roll the year
     * value in the range between 1 and the value returned by calling
     * getMaximum(Calendar.YEAR).
     * When rolling on the month or Calendar.MONTH field, other fields like
     * date might conflict and, need to be changed. For instance,
     * rolling the month on the date 01/31/96 will result in 03/02/96.
     * When rolling on the hour-in-day or Calendar.HOUR_OF_DAY field, it will
     * roll the hour value in the range between 0 and 23, which is zero-based.
     * @param field the time field.
     * @param up indicates if the value of the specified time field is to be
     * rolled up or rolled down. Use true if rolling up, false otherwise.
     */
    abstract public void roll(int field, boolean up);

    /**
     * Sets the time zone with the given time zone value.
     * @param value the given time zone.
     */
    public void setTimeZone(TimeZone value)
    {
        zone = value;
    }

    /**
     * Gets the time zone.
     * @return the time zone object associated with this calendar.
     */
    public TimeZone getTimeZone()
    {
        return zone;
    }

    /**
     * Specify whether or not date/time interpretation is to be lenient.  With
     * lenient interpretation, a date such as "February 942, 1996" will be
     * treated as being equivalent to the 941st day after February 1, 1996.
     * With strict interpretation, such dates will cause an exception to be
     * thrown.
     *
     * @see java.text.DateFormat#setLenient
     */
    public void setLenient(boolean lenient)
    {
        this.lenient = lenient;
    }

    /**
     * Tell whether date/time interpretation is to be lenient.
     */
    public boolean isLenient()
    {
        return lenient;
    }

    /**
     * Sets what the first day of the week is; e.g., Sunday in US,
     * Monday in France.
     * @param value the given first day of the week.
     */
    public void setFirstDayOfWeek(int value)
    {
        firstDayOfWeek = value;
    }

    /**
     * Gets what the first day of the week is; e.g., Sunday in US,
     * Monday in France.
     * @return the first day of the week.
     */
    public int getFirstDayOfWeek()
    {
        return firstDayOfWeek;
    }

    /**
     * Sets what the minimal days required in the first week of the year are;
     * For example, if the first week is defined as one that contains the first
     * day of the first month of a year, call the method with value 1. If it
     * must be a full week, use value 7.
     * @param value the given minimal days required in the first week
     * of the year.
     */
    public void setMinimalDaysInFirstWeek(int value)
    {
        minimalDaysInFirstWeek = value;
    }

    /**
     * Gets what the minimal days required in the first week of the year are;
     * e.g., if the first week is defined as one that contains the first day
     * of the first month of a year, getMinimalDaysInFirstWeek returns 1. If
     * the minimal days required must be a full week, getMinimalDaysInFirstWeek
     * returns 7.
     * @return the minimal days required in the first week of the year.
     */
    public int getMinimalDaysInFirstWeek()
    {
        return minimalDaysInFirstWeek;
    }

    /**
     * Gets the minimum value for the given time field.
     * e.g., for Gregorian DAY_OF_MONTH, 1.
     * @param field the given time field.
     * @return the minimum value for the given time field.
     */
    abstract public int getMinimum(int field);

    /**
     * Gets the maximum value for the given time field.
     * e.g. for Gregorian DAY_OF_MONTH, 31.
     * @param field the given time field.
     * @return the maximum value for the given time field.
     */
    abstract public int getMaximum(int field);

    /**
     * Gets the highest minimum value for the given field if varies.
     * Otherwise same as getMinimum(). For Gregorian, no difference.
     * @param field the given time field.
     * @return the highest minimum value for the given time field.
     */
    abstract public int getGreatestMinimum(int field);

    /**
     * Gets the lowest maximum value for the given field if varies.
     * Otherwise same as getMaximum(). e.g., for Gregorian DAY_OF_MONTH, 28.
     * @param field the given time field.
     * @return the lowest maximum value for the given time field.
     */
    abstract public int getLeastMaximum(int field);

    /**
     * Overrides Cloneable
     */
    public Object clone()
    {
        try
        {
            Calendar other = (Calendar) super.clone();

            other.fields = new int[FIELD_COUNT];
            other.isSet = new boolean[FIELD_COUNT];
            System.arraycopy(this.fields, 0, other.fields, 0, FIELD_COUNT);
            System.arraycopy(this.isSet, 0, other.isSet, 0, FIELD_COUNT);

            other.zone = (TimeZone) zone.clone();
            return other;
        }
        catch (CloneNotSupportedException e)
        {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }


    // =======================privates===============================

    /**
     * Both firstDayOfWeek and minimalDaysInFirstWeek are locale-dependent.
     * They are used to figure out the week count for a specific date for
     * a given locale. These must be set when a Calendar is constructed.
     * @param desiredLocale the given locale.
     */
    private void setWeekCountData(Locale desiredLocale)
    {
        ResourceBundle resource
            = ResourceBundle.getBundle("java.text.resources.LocaleElements",
                                               desiredLocale);
        String[] dateTimePatterns
        = resource.getStringArray("DateTimeElements");

        firstDayOfWeek = Integer.parseInt(dateTimePatterns[0]);
        minimalDaysInFirstWeek = Integer.parseInt(dateTimePatterns[1]);
    }
}
