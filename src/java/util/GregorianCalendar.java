/*
 * @(#)GregorianCalendar.java	1.40 99/03/08
 *
 * (C) Copyright Taligent, Inc. 1996-1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996-1997 - All Rights Reserved
 *
 * Portions copyright (c) 1996-1999 Sun Microsystems, Inc. All Rights Reserved.
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
 * <code>GregorianCalendar</code> is a concrete subclass of
 * <a href="java.util.Calendar.html"><code>Calendar</code></a>
 * and provides the standard calendar used by most of the world.
 *
 * <p>
 * The standard (Gregorian) calendar has 2 eras, BC and AD.
 *
 * <p>
 * This implementation handles a single discontinuity, which corresponds
 * by default to the date the Gregorian calendar was instituted (October 15,
 * 1582 in some countries, later in others). This cutover date may be changed
 * by the caller.
 *
 * <p>
 * Prior to the institution of the Gregorian calendar, New Year's Day was
 * March 25. To avoid confusion, this calendar always uses January 1. A manual
 * adjustment may be made if desired for dates that are prior to the Gregorian
 * changeover and which fall between January 1 and March 24.
 *
 * <p>
 * <strong>Example:</strong>
 * <blockquote>
 * <pre>
 * // get the supported ids for GMT-08:00 (Pacific Standard Time)
 * String[] ids = TimeZone.getAvailableIDs(-8 * 60 * 60 * 1000);
 * // if no ids were returned, something is wrong. get out.
 * if (ids.length == 0)
 *     System.exit(0);
 *
 *  // begin output
 * System.out.println("Current Time");
 *
 * // create a Pacific Standard Time time zone
 * SimpleTimeZone pdt = new SimpleTimeZone(-8 * 60 * 60 * 1000, ids[0]);
 *
 * // set up rules for daylight savings time
 * pdt.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
 * pdt.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
 *
 * // create a GregorianCalendar with the Pacific Daylight time zone
 * // and the current date and time
 * Calendar calendar = new GregorianCalendar(pdt);
 * Date trialTime = new Date();
 * calendar.setTime(trialTime);
 *
 * // print out a bunch of interesting things
 * System.out.println("ERA: " + calendar.get(Calendar.ERA));
 * System.out.println("YEAR: " + calendar.get(Calendar.YEAR));
 * System.out.println("MONTH: " + calendar.get(Calendar.MONTH));
 * System.out.println("WEEK_OF_YEAR: " + calendar.get(Calendar.WEEK_OF_YEAR));
 * System.out.println("WEEK_OF_MONTH: " + calendar.get(Calendar.WEEK_OF_MONTH));
 * System.out.println("DATE: " + calendar.get(Calendar.DATE));
 * System.out.println("DAY_OF_MONTH: " + calendar.get(Calendar.DAY_OF_MONTH));
 * System.out.println("DAY_OF_YEAR: " + calendar.get(Calendar.DAY_OF_YEAR));
 * System.out.println("DAY_OF_WEEK: " + calendar.get(Calendar.DAY_OF_WEEK));
 * System.out.println("DAY_OF_WEEK_IN_MONTH: "
 *                    + calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH));
 * System.out.println("AM_PM: " + calendar.get(Calendar.AM_PM));
 * System.out.println("HOUR: " + calendar.get(Calendar.HOUR));
 * System.out.println("HOUR_OF_DAY: " + calendar.get(Calendar.HOUR_OF_DAY));
 * System.out.println("MINUTE: " + calendar.get(Calendar.MINUTE));
 * System.out.println("SECOND: " + calendar.get(Calendar.SECOND));
 * System.out.println("MILLISECOND: " + calendar.get(Calendar.MILLISECOND));
 * System.out.println("ZONE_OFFSET: "
 *                    + (calendar.get(Calendar.ZONE_OFFSET)/(60*60*1000)));
 * System.out.println("DST_OFFSET: "
 *                    + (calendar.get(Calendar.DST_OFFSET)/(60*60*1000)));

 * System.out.println("Current Time, with hour reset to 3");
 * calendar.clear(Calendar.HOUR_OF_DAY); // so doesn't override
 * calendar.set(Calendar.HOUR, 3);
 * System.out.println("ERA: " + calendar.get(Calendar.ERA));
 * System.out.println("YEAR: " + calendar.get(Calendar.YEAR));
 * System.out.println("MONTH: " + calendar.get(Calendar.MONTH));
 * System.out.println("WEEK_OF_YEAR: " + calendar.get(Calendar.WEEK_OF_YEAR));
 * System.out.println("WEEK_OF_MONTH: " + calendar.get(Calendar.WEEK_OF_MONTH));
 * System.out.println("DATE: " + calendar.get(Calendar.DATE));
 * System.out.println("DAY_OF_MONTH: " + calendar.get(Calendar.DAY_OF_MONTH));
 * System.out.println("DAY_OF_YEAR: " + calendar.get(Calendar.DAY_OF_YEAR));
 * System.out.println("DAY_OF_WEEK: " + calendar.get(Calendar.DAY_OF_WEEK));
 * System.out.println("DAY_OF_WEEK_IN_MONTH: "
 *                    + calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH));
 * System.out.println("AM_PM: " + calendar.get(Calendar.AM_PM));
 * System.out.println("HOUR: " + calendar.get(Calendar.HOUR));
 * System.out.println("HOUR_OF_DAY: " + calendar.get(Calendar.HOUR_OF_DAY));
 * System.out.println("MINUTE: " + calendar.get(Calendar.MINUTE));
 * System.out.println("SECOND: " + calendar.get(Calendar.SECOND));
 * System.out.println("MILLISECOND: " + calendar.get(Calendar.MILLISECOND));
 * System.out.println("ZONE_OFFSET: "
 *        + (calendar.get(Calendar.ZONE_OFFSET)/(60*60*1000))); // in hours
 * System.out.println("DST_OFFSET: "
 *        + (calendar.get(Calendar.DST_OFFSET)/(60*60*1000))); // in hours
 * </pre>
 * </blockquote>
 *
 * @see          Calendar
 * @see          TimeZone
 * @version      1.40 03/08/99
 * @author       David Goldsmith, Mark Davis, Chen-Lieh Huang, Alan Liu
 */
public class GregorianCalendar extends Calendar {

    // Internal notes:
    // This algorithm is based on the one presented on pp. 10-12 of
    // "Numerical Recipes in C", William H. Press, et. al., Cambridge
    // University Press 1988, ISBN 0-521-35465-X.

    /**
     * Useful constant for GregorianCalendar.
     */
    public static final int BC = 0;
    /**
     * Useful constant for GregorianCalendar.
     */
    public static final int AD = 1;

    // Note that the Julian date used here is not a true Julian date, since
    // it is measured from midnight, not noon.

    private static final long julianDayOffset = 2440588;
    private static final int millisPerDay = 24 * 60 * 60 * 1000;
    private static final int NUM_DAYS[]
    = {0,31,59,90,120,151,181,212,243,273,304,334}; // 0-based, for day-in-year
    private static final int LEAP_NUM_DAYS[]
    = {0,31,60,91,121,152,182,213,244,274,305,335}; // 0-based, for day-in-year
    private static final int MONTH_LENGTH[]
    = {31,28,31,30,31,30,31,31,30,31,30,31}; // 0-based
    private static final int LEAP_MONTH_LENGTH[]
    = {31,29,31,30,31,30,31,31,30,31,30,31}; // 0-based

    // This is measured from the standard epoch, not in Julian Days.
    // Default is 00:00:00 local time, October 15, 1582.
    private long gregorianCutover = -12219292800000L;

    // The onset of the Julian calendar is 45 B.C.  The Julian day
    // number for the start of the year 45 B.C. is 1712653.  We compute
    // the Julian onset as epoch-based millis.  Note that this number is
    // useful for rough comparison purposes only; it's not exact. [LIU]
    private static long JULIAN_ONSET = (1712653 - julianDayOffset) * millisPerDay;

    // Useful millisecond constants
    private static final long ONE_SECOND = 1000;
    private static final long ONE_MINUTE = 60*ONE_SECOND;
    private static final long ONE_HOUR   = 60*ONE_MINUTE;
    private static final long ONE_DAY    = 24*ONE_HOUR;
    private static final long ONE_WEEK   = 7*ONE_DAY;

    // Proclaim serialization compatiblity with JDK 1.1
    static final long serialVersionUID = -8125100834729963327L;

    /**
     * Converts time as milliseconds to Julian date.
     * @param millis the given milliseconds.
     * @return the Julian date number.
     */
    private static final long millisToJulianDay(long millis)
    {
        if (millis >= 0)
            return julianDayOffset + (millis / millisPerDay);
        else
            return julianDayOffset
                + ((millis - millisPerDay + 1) / millisPerDay);
    }

    /**
     * Converts Julian date to time as milliseconds.
     * @param julian the given Julian date number.
     * @return time as milliseconds.
     */
    private static final long julianDayToMillis(long julian)
    {
        return (julian - julianDayOffset) * millisPerDay;
    }

    /**
     * Constructs a default GregorianCalendar using the current time
     * in the default time zone with the default locale.
     */
    public GregorianCalendar()
    {
        this(TimeZone.getDefault(), Locale.getDefault());
    }

    /**
     * Constructs a GregorianCalendar based on the current time
     * in the given time zone with the default locale.
     * @param zone the given time zone.
     */
    public GregorianCalendar(TimeZone zone)
    {
        this(zone, Locale.getDefault());
    }

    /**
     * Constructs a GregorianCalendar based on the current time
     * in the default time zone with the given locale.
     * @param aLocale the given locale.
     */
    public GregorianCalendar(Locale aLocale)
    {
        this(TimeZone.getDefault(), aLocale);
    }

    /**
     * Constructs a GregorianCalendar based on the current time
     * in the given time zone with the given locale.
     * @param zone the given time zone.
     * @param aLocale the given locale.
     */
    public GregorianCalendar(TimeZone zone, Locale aLocale)
    {
        super(zone, aLocale);
        setTimeInMillis(System.currentTimeMillis());
    }

    /**
     * Constructs a GregorianCalendar with the given date set
     * in the default time zone with the default locale.
     * @param year the value used to set the YEAR time field in the calendar.
     * @param month the value used to set the MONTH time field in the calendar.
     * Month value is 0-based. e.g., 0 for January.
     * @param date the value used to set the DATE time field in the calendar.
     */
    public GregorianCalendar(int year, int month, int date)
    {
        super(TimeZone.getDefault(), Locale.getDefault());
        this.set(ERA, AD);
        this.set(YEAR, year);
        this.set(MONTH, month);
        this.set(DATE, date);
    }

    /**
     * Constructs a GregorianCalendar with the given date
     * and time set for the default time zone with the default locale.
     * @param year the value used to set the YEAR time field in the calendar.
     * @param month the value used to set the MONTH time field in the calendar.
     * Month value is 0-based. e.g., 0 for January.
     * @param date the value used to set the DATE time field in the calendar.
     * @param hour the value used to set the HOUR_OF_DAY time field
     * in the calendar.
     * @param minute the value used to set the MINUTE time field
     * in the calendar.
     */
    public GregorianCalendar(int year, int month, int date, int hour,
                             int minute)
    {
        super(TimeZone.getDefault(), Locale.getDefault());
        this.set(ERA, AD);
        this.set(YEAR, year);
        this.set(MONTH, month);
        this.set(DATE, date);
        this.set(HOUR_OF_DAY, hour);
        this.set(MINUTE, minute);
    }

    /**
     * Constructs a GregorianCalendar with the given date
     * and time set for the default time zone with the default locale.
     * @param year the value used to set the YEAR time field in the calendar.
     * @param month the value used to set the MONTH time field in the calendar.
     * Month value is 0-based. e.g., 0 for January.
     * @param date the value used to set the DATE time field in the calendar.
     * @param hour the value used to set the HOUR_OF_DAY time field
     * in the calendar.
     * @param minute the value used to set the MINUTE time field
     * in the calendar.
     * @param second the value used to set the SECOND time field
     * in the calendar.
     */
    public GregorianCalendar(int year, int month, int date, int hour,
                             int minute, int second)
    {
        super(TimeZone.getDefault(), Locale.getDefault());
        this.set(ERA, AD);
        this.set(YEAR, year);
        this.set(MONTH, month);
        this.set(DATE, date);
        this.set(HOUR_OF_DAY, hour);
        this.set(MINUTE, minute);
        this.set(SECOND, second);
    }

    /**
     * Compares this calendar to the specified object.
     * The result is <code>true</code> if and only if the argument is
     * not <code>null</code> and is a <code>Calendar</code> object that
     * represents the same calendar as this object.
     * @param obj the object to compare with.
     * @return <code>true</code> if the objects are the same;
     * <code>false</code> otherwise.
     */
    public boolean equals(Object obj) {
        // This body moves to Calendar in 1.2
        if (this == obj)
            return true;
        if (!(obj instanceof Calendar))
            return false;

        Calendar that = (Calendar)obj;

        return getTimeInMillis() == that.getTimeInMillis() &&
            isLenient() == that.isLenient() &&
	    getFirstDayOfWeek() == that.getFirstDayOfWeek() &&
            getMinimalDaysInFirstWeek() == that.getMinimalDaysInFirstWeek() &&
            getTimeZone().equals(that.getTimeZone());
    }

    /**
     * Compares the time field records.
     * Equivalent to comparing result of conversion to UTC.
     * @param when the Calendar to be compared with this Calendar.
     * @return true if the current time of this Calendar is before
     * the time of Calendar when; false otherwise.
     */
    public boolean before(Object when) {
        // This body moves to Calendar in 1.2
        return when instanceof Calendar &&
            getTimeInMillis() < ((Calendar) when).getTimeInMillis();
    }

    /**
     * Compares the time field records.
     * Equivalent to comparing result of conversion to UTC.
     * @param when the Calendar to be compared with this Calendar.
     * @return true if the current time of this Calendar is after
     * the time of Calendar when; false otherwise.
     */
    public boolean after(Object when) {
        // This body moves to Calendar in 1.2
        return when instanceof Calendar &&
            getTimeInMillis() > ((Calendar) when).getTimeInMillis();
    }

    /**
     * Sets the GregorianCalendar change date. This is the point when the
     * switch from Julian dates to Gregorian dates occurred. Default is
     * 00:00:00 local time, October 15, 1582. Previous to this time and date
     * will be Julian dates.
     *
     * @param date the given Gregorian cutover date.
     */
    public void setGregorianChange(Date date)
    {
        gregorianCutover = date.getTime();
    }

    /**
     * Gets the Gregorian Calendar change date.  This is the point when the
     * switch from Julian dates to Gregorian dates occurred. Default is
     * 00:00:00 local time, October 15, 1582. Previous to
     * this time and date will be Julian dates.
     * @return the Gregorian cutover time for this calendar.
     */
    public final Date getGregorianChange()
    {
        return new Date(gregorianCutover);
    }

    private static final int julianDayToDayOfWeek(long julian)
    {
        // If julian is negative, then julian%7 will be negative, so we adjust
        // accordingly.  We add 1 because Julian day 0 is Monday.
        int dayOfWeek = (int)((julian + 1) % 7);
        return dayOfWeek + ((dayOfWeek < 0) ? (7 + SUNDAY) : SUNDAY);
    }

    /**
     * Convert the time as milliseconds to the "big" fields.  Millis must be
     * given as local wall millis to get the correct local day.  For example,
     * if it is 11:30 pm Standard, and DST is in effect, the correct DST millis
     * must be passed in to get the right date.
     *
     * Fields that are completed by this method: ERA, YEAR, MONTH, DATE,
     * DAY_OF_WEEK, DAY_OF_YEAR, WEEK_OF_YEAR, WEEK_OF_MONTH,
     * DAY_OF_WEEK_IN_MONTH.
     * @param quick if true, only compute the ERA, YEAR, MONTH, DATE,
     * DAY_OF_WEEK, and DAY_OF_YEAR.
     */
    private final void timeToFields(long theTime, boolean quick)
    {
        int rawYear, year, month, date, dayOfWeek, dayOfYear, weekCount, era = AD;

        //---------------------------------------------------------------------
        // BEGIN modified caldat()
        //---------------------------------------------------------------------
        // The following variable names are somewhat cryptic. Unfortunately,
        // they are from the original program cited above, and no explanation
        // for their meaning is given. Given that the algorithm is cryptic too,
        // perhaps it doesn't matter...
        long ja, jb, jd;
        long jc, je; // changed from int to fix number overflow problem.

        long julian = millisToJulianDay(theTime);

        if (theTime >= gregorianCutover)
        {
            long jalpha = (long) (((double) (julian - 1867216) - 0.25)
                                  / 36524.25);
            ja = julian + 1 + jalpha - (long) (0.25 * jalpha);
        }
        else
        {
            ja = julian;
        }
        jb = ja + 1524;
        jc = (long) Math.floor(6680.0 + ((double) (jb - 2439870) - 122.1) / 365.25);
        jd = (long) Math.floor(365*jc + (0.25 * jc));
        je = (long) ((jb-jd)/30.6001);
        date = (int) (jb-jd-(long) (30.6001 * je));
        month = (int) je - 1;
        if (month > 12)
            month -= 12;

        rawYear = (int) (jc-4715);
        if (month > 2) --rawYear;
        year = rawYear;
        if (rawYear <= 0) {
            era = BC;
            year = 1-rawYear;
        }
        //---------------------------------------------------------------------
        // END modified caldat()
        //---------------------------------------------------------------------

        internalSet(ERA, era);
        internalSet(YEAR, year);
        internalSet(MONTH, month-1); // 0-based
        internalSet(DATE, date);

        dayOfWeek = julianDayToDayOfWeek(julian);
        internalSet(DAY_OF_WEEK, dayOfWeek); // CLH, 8-7-96

        dayOfYear =
            (isLeapYear(rawYear) ? LEAP_NUM_DAYS[month-1] : NUM_DAYS[month-1])
            + date; // arrays are zero-based, 'month' is one-based
        internalSet(DAY_OF_YEAR, dayOfYear);

        if (quick) return;
        
        // Compute the week of the year.  Valid week numbers run from 1 to 52
        // or 53, depending on the year, the first day of the week, and the
        // minimal days in the first week.  Days at the start of the year may
        // fall into the last week of the previous year; days at the end of
        // the year may fall into the first week of the next year.
        int relDow = (dayOfWeek + 7 - getFirstDayOfWeek()) % 7; // 0..6
        int relDowJan1 = (dayOfWeek - dayOfYear + 701 - getFirstDayOfWeek()) % 7; // 0..6
        int woy = (dayOfYear - 1 + relDowJan1) / 7; // 0..53
        if ((7 - relDowJan1) >= getMinimalDaysInFirstWeek()) {
            ++woy;
            // Check to see if we are in the last week; if so, we need
            // to handle the case in which we are the first week of the
            // next year.
            int lastDoy = yearLength();
            int lastRelDow = (relDow + lastDoy - dayOfYear) % 7;
            if (lastRelDow < 0) lastRelDow += 7;
            if (dayOfYear > 359 && // Fast check which eliminates most cases
                (6 - lastRelDow) >= getMinimalDaysInFirstWeek() &&
                (dayOfYear + 7 - relDow) > lastDoy) woy = 1;
        }
        else if (woy == 0) {
            // We are the last week of the previous year.
            int prevDoy = dayOfYear + yearLength(rawYear - 1);
            woy = weekNumber(prevDoy, dayOfWeek);
        }
        internalSet(WEEK_OF_YEAR, woy);

        internalSet(WEEK_OF_MONTH, weekNumber(date, dayOfWeek));

        internalSet(DAY_OF_WEEK_IN_MONTH, (date-1) / 7 + 1);
    }

    /**
     * Return the week number of a day, within a period. This may be the week number in
     * a year, or the week number in a month. Usually this will be a value >= 1, but if
     * some initial days of the period are excluded from week 1, because
     * minimalDaysInFirstWeek is > 1, then the week number will be zero for those
     * initial days. Requires the day of week for the given date in order to determine
     * the day of week of the first day of the period.
     *
     * @param dayOfPeriod  Day-of-year or day-of-month. Should be 1 for first day of period.
     * @param day   Day-of-week for given dayOfPeriod. 1-based with 1=Sunday.
     * @return      Week number, one-based, or zero if the day falls in part of the
     *              month before the first week, when there are days before the first
     *              week because the minimum days in the first week is more than one.
     */
    private int weekNumber(int dayOfPeriod, int dayOfWeek)
    {
        // Determine the day of the week of the first day of the period
        // in question (either a year or a month).  Zero represents the
        // first day of the week on this calendar.
        int periodStartDayOfWeek = (dayOfWeek - getFirstDayOfWeek() - dayOfPeriod + 1) % 7;
        if (periodStartDayOfWeek < 0) periodStartDayOfWeek += 7;

        // Compute the week number.  Initially, ignore the first week, which
        // may be fractional (or may not be).  We add periodStartDayOfWeek in
        // order to fill out the first week, if it is fractional.
        int weekNo = (dayOfPeriod + periodStartDayOfWeek - 1)/7;

        // If the first week is long enough, then count it.  If
        // the minimal days in the first week is one, or if the period start
        // is zero, we always increment weekNo.
        if ((7 - periodStartDayOfWeek) >= getMinimalDaysInFirstWeek()) ++weekNo;

        return weekNo;
    }

    /**
     * Determines if the given year is a leap year. Returns true if the
     * given year is a leap year.
     * @param year the given year.
     * @return true if the given year is a leap year; false otherwise.
     */
    public boolean isLeapYear(int year)
    {
        // Compute the rough millis for the year.  We only need this number to be
        // good enough to compare it against JULIAN_ONSET.
        long equivalent_millis = (long)((year - 1970) * 365.2422 * millisPerDay);

        // No leap years before onset of Julian calendar
        if (equivalent_millis < JULIAN_ONSET)
            return false;

        return (equivalent_millis > gregorianCutover) ?
            ((year%4 == 0) && ((year%100 != 0) || (year%400 == 0))) : // Gregorian
            (year%4 == 0); // Julian
    }

    /**
     * Overrides Calendar
     * Converts UTC as milliseconds to time field values.
     * The time is <em>not</em>
     * recomputed first; to recompute the time, then the fields, call the
     * <code>complete</code> method.
     * @see Calendar#complete
     */
    protected void computeFields()
    {
        int gmtOffset = getTimeZone().getRawOffset();
        long localMillis = time + gmtOffset;

        // Time to fields takes the wall millis (Standard or DST).
        timeToFields(localMillis, false);

        int era = internalGetEra();
        int year = internalGet(YEAR);
        int month = internalGet(MONTH);
        int date = internalGet(DATE);
        int dayOfWeek = internalGet(DAY_OF_WEEK);

        long days = (long) (localMillis / millisPerDay);
        int millisInDay = (int) (localMillis - (days * millisPerDay));
        if (millisInDay < 0) millisInDay += millisPerDay;

        // Call getOffset() to get the TimeZone offset.  The millisInDay value must
        // be standard local millis.
        int dstOffset = getTimeZone().getOffset(era,year,month,date,dayOfWeek,millisInDay) -
            gmtOffset;

        // Adjust our millisInDay for DST, if necessary.
        millisInDay += dstOffset;

        // If DST has pushed us into the next day, we must call timeToFields() again.
        // This happens in DST between 12:00 am and 1:00 am every day.  The call to
        // timeToFields() will give the wrong day, since the Standard time is in the
        // previous day.
        if (millisInDay >= millisPerDay)
        {
            millisInDay -= millisPerDay;
            localMillis += dstOffset;
            timeToFields(localMillis, false);
        }

        // Fill in all time-related fields based on millisInDay.  Call internalSet()
        // so as not to perturb flags.
        internalSet(MILLISECOND, millisInDay % 1000);
        millisInDay /= 1000;
        internalSet(SECOND, millisInDay % 60);
        millisInDay /= 60;
        internalSet(MINUTE, millisInDay % 60);
        millisInDay /= 60;
        internalSet(HOUR_OF_DAY, millisInDay);
        internalSet(AM_PM, millisInDay / 12);
        internalSet(HOUR, millisInDay % 12);

        internalSet(ZONE_OFFSET, gmtOffset);
        internalSet(DST_OFFSET, dstOffset);

    // Careful here: We are manually setting the time stamps[] flags to
        // INTERNALLY_SET, so we must be sure that the above code actually does
        // set all these fields.
    for (int i=0; i<FIELD_COUNT; ++i) stamp[i] = INTERNALLY_SET;

        // Careful here: We are manually setting the isSet[] flags to true, so we
        // must be sure that the above code actually does set all these fields.
        for (int i=0; i<FIELD_COUNT; ++i) isSet[i] = true; // Remove later
    }

    /**
     * Return true if the current time for this Calendar is in Daylignt
     * Savings Time.
     *
     * Note -- MAKE THIS PUBLIC AT THE NEXT API CHANGE.  POSSIBLY DEPRECATE
     * AND REMOVE TimeZone.inDaylightTime().
     */
    boolean inDaylightTime()
    {
        if (!getTimeZone().useDaylightTime()) return false;
        complete(); // Force update of DST_OFFSET field
        return internalGet(DST_OFFSET) != 0;
    }

    private final int monthLength(int month, int year)
    {
        return isLeapYear(year) ? LEAP_MONTH_LENGTH[month] : MONTH_LENGTH[month];
    }

    private final int monthLength(int month) {
        int year = internalGet(YEAR);
        if (internalGetEra() == BC) {
            year = 1-year;
        }
        return monthLength(month, year);
    }

    private final int yearLength(int year) {
        return isLeapYear(year) ? 366 : 365;
    }

    private final int yearLength() {
        return isLeapYear(internalGet(YEAR)) ? 366 : 365;
    }

    /**
     * After adjustments such as add(MONTH), add(YEAR), we don't want the
     * month to jump around.  E.g., we don't want Jan 31 + 1 month to go to Mar
     * 3, we want it to go to Feb 28.  Adjustments which might run into this
     * problem call this method to retain the proper month.
     */
    private final void pinDayOfMonth() {
        int monthLen = monthLength(internalGet(MONTH));
        int dom = internalGet(DAY_OF_MONTH);
        if (dom > monthLen) set(DAY_OF_MONTH, monthLen);
    }

    /**
     * Validates the values of the set time fields.
     */
    private boolean validateFields()
    {
        for (int field = 0; field < FIELD_COUNT; field++)
        {
            // Ignore DATE and DAY_OF_YEAR which are handled below
            if (field != DATE &&
                field != DAY_OF_YEAR &&
                isSet(field) &&
                !boundsCheck(internalGet(field), field))

                return false;
        }

        // Values differ in Least-Maximum and Maximum should be handled
        // specially.
        if (isSet(DATE))
        {
            int date = internalGet(DATE);
            return (date >= getMinimum(DATE) &&
                    date <= monthLength(internalGet(MONTH)));
        }

        if (isSet(DAY_OF_YEAR))
        {
            int days = internalGet(DAY_OF_YEAR);

            if (isLeapYear(internalGet(YEAR))) {
                if (days < 1 || days > 366)
                    return false;
            }
            else if (days < 1 || days > 365)
                return false;
        }

        // Handle DAY_OF_WEEK_IN_MONTH, which must not have the value zero.
        // We've checked against minimum and maximum above already.
        if (isSet(DAY_OF_WEEK_IN_MONTH) &&
            0 == internalGet(DAY_OF_WEEK_IN_MONTH)) return false;

        return true;
    }

    /**
     * Validates the value of the given time field.
     */
    private boolean boundsCheck(int value, int field)
    {
        return value >= getMinimum(field) && value <= getMaximum(field);
    }

    /**
     * Divide two integers, returning the floor of the quotient, and
     * the modulus remainder.
     * <p>
     * Unlike the built-in division, this is mathematically well-behaved.
     * E.g., <code>-1/4</code> => 0 and <code>-1%4</code> => -1,
     * but <code>floorDivide(-1,4)</code> => -1 with <code>remainder[0]</code> => 3.
     * @param numerator the numerator
     * @param denominator a divisor which must be > 0
     * @param remainder an array of at least one element in which the value
     * <code>numerator mod denominator</code> is returned. Unlike <code>numerator
     * % denominator</code>, this will always be non-negative.
     * @return the floor of the quotient.
     */
    private static final int floorDivide(long numerator, int denominator, int[] remainder) {
        if (numerator >= 0) {
            remainder[0] = (int)(numerator % denominator);
            return (int)(numerator / denominator);
        }
        int quotient = (int)(((numerator + 1) / denominator) - 1);
        remainder[0] = (int)(numerator - (quotient * denominator));
        return quotient;
    }

    /**
     * Return the pseudo-time-stamp for two fields, given their
     * individual pseudo-time-stamps.  If either of the fields
     * is unset, then the aggregate is unset.  Otherwise, the
     * aggregate is the later of the two stamps.
     */
    private final int aggregateStamp(int stamp_a, int stamp_b) {
    return (stamp_a != UNSET && stamp_b != UNSET) ?
        Math.max(stamp_a, stamp_b) : UNSET;
    }

    /**
     * Overrides Calendar
     * Converts time field values to UTC as milliseconds.
     * @exception IllegalArgumentException if any fields are invalid.
     */
    protected void computeTime()
    {
        if (!isLenient() && !validateFields())
            throw new IllegalArgumentException();

        // This function takes advantage of the fact that unset fields in
        // the time field list have a value of zero.
        long millis = 0;

        // The year defaults to the epoch start.
        int year = (stamp[YEAR] != UNSET) ? internalGet(YEAR) : 1970;
        int month = 0, date = 0;

        int era = AD;
        if (stamp[ERA] != UNSET) {
            era = internalGet(ERA);
            if (era == BC)
                year = 1 - year;
            // Even in lenient mode we disallow ERA values other than AD & BC
            else if (era != AD)
                throw new IllegalArgumentException("Invalid era");
        }

        long julian = 0;

        // Find the most recent group of fields specifying the day within
        // the year.  These may be any of the following combinations:
        //   MONTH + DAY_OF_MONTH
        //   MONTH + WEEK_OF_MONTH + DAY_OF_WEEK
        //   MONTH + DAY_OF_WEEK_IN_MONTH + DAY_OF_WEEK
        //   DAY_OF_YEAR
        //   WEEK_OF_YEAR + DAY_OF_WEEK
        // We look for the most recent of the fields in each group to determine
        // the age of the group.  For groups involving a week-related field such
        // as WEEK_OF_MONTH, DAY_OF_WEEK_IN_MONTH, or WEEK_OF_YEAR, both the
        // week-related field and the DAY_OF_WEEK must be set for the group as a
        // whole to be considered.  (See bug 4153860 - liu 7/24/98.)
    int dowStamp = stamp[DAY_OF_WEEK];
    int monthStamp = stamp[MONTH];
    int domStamp = stamp[DAY_OF_MONTH];
    int womStamp = aggregateStamp(stamp[WEEK_OF_MONTH], dowStamp);
    int dowimStamp = aggregateStamp(stamp[DAY_OF_WEEK_IN_MONTH], dowStamp);
    int doyStamp = stamp[DAY_OF_YEAR];
    int woyStamp = aggregateStamp(stamp[WEEK_OF_YEAR], dowStamp);

    int bestStamp = (monthStamp > domStamp) ? monthStamp : domStamp;
    if (womStamp > bestStamp) bestStamp = womStamp;
    if (dowimStamp > bestStamp) bestStamp = dowimStamp;
    if (doyStamp > bestStamp) bestStamp = doyStamp;
    if (woyStamp > bestStamp) bestStamp = woyStamp;

    if (bestStamp != UNSET &&
        (bestStamp == monthStamp ||
         bestStamp == domStamp ||
         bestStamp == womStamp ||
         bestStamp == dowimStamp)) {

            // We have the month specified. Make it 1-based for the algorithm.
            month = (monthStamp != UNSET ? internalGet(MONTH) : JANUARY) + 1;
            // normalize month
            if (month < 1) {
                year += month / 12 - 1;
                month = 12 + month % 12;
            } else if (month > 12) {
                year += month / 12;
                month = month % 12;
            }

            if (month > 2)
                ++month;
            else
            {
                --year;
                month += 13;
            }
            julian = (long) (Math.floor(365.25*year)
                     + Math.floor(30.6001*month) + 1720995);

            if (bestStamp == domStamp ||
        bestStamp == monthStamp) {

        date = (domStamp != UNSET) ? internalGet(DAY_OF_MONTH) : 1;
        }
        else { // assert(bestStamp == womStamp || bestStamp == dowimStamp)
                // Compute from day of week plus week number or from the day of
                // week plus the day of week in month.  The computations are
                // almost identical.

                // The first thing we have to do is do the Gregorian adjustment,
                // if necessary.  We figure out the adjusted value 'j' and use
                // that.  We redo this later when we get the real final number.
                // This double computation provides the best accuracy around the
                // Gregorian cutover.
                long j = julian;
                if (julianDayToMillis(julian) >= gregorianCutover)
                {
                    long adjust = (long) (0.01 * year);
                    j += 2 - adjust + (long) (0.25*adjust);
                }

                // Find the day of the week for the first of this month.  This
                // is zero-based, with 0 being the locale-specific first day of
                // the week.  Add 1 to get the 1st day of month.  Subtract
                // getFirstDayOfWeek() to make 0-based.
                int fdm = julianDayToDayOfWeek(j + 1) - getFirstDayOfWeek();
                if (fdm < 0) fdm += 7;

                // Find the start of the first week.  This will be a date from
                // 1..-6.  It represents the locale-specific first day of the
                // week of the first day of the month, ignoring minimal days in
                // first week.
        date = 1 - fdm + ((stamp[DAY_OF_WEEK] != UNSET) ?
                  (internalGet(DAY_OF_WEEK) - getFirstDayOfWeek()) : 0);

                if (bestStamp == womStamp)
                {
                    // Adjust for minimal days in first week.
                    if ((7 - fdm) < getMinimalDaysInFirstWeek()) date += 7;

                    // Now adjust for the week number.
                    date += 7 * (internalGet(WEEK_OF_MONTH) - 1);
                }
                else // assert(bestStamp == dowimStamp)
                {
                    // Adjust into the month, if needed.
                    if (date < 1) date += 7;

                    // We are basing this on the day-of-week-in-month.  The only
                    // trickiness occurs if the day-of-week-in-month is
                    // negative.
                    int dim = internalGet(DAY_OF_WEEK_IN_MONTH);
                    if (dim >= 0) date += 7*(dim - 1);
                    else
                    {
                        // Move date to the last of this day-of-week in this
                        // month, then back up as needed.  If dim==-1, we don't
                        // back up at all.  If dim==-2, we back up once, etc.
                        // Don't back up past the first of the given day-of-week
                        // in this month.  Note that we handle -2, -3,
                        // etc. correctly, even though values < -1 are
                        // technically disallowed.
                        date += ((monthLength(internalGet(MONTH), year) - date) / 7 + dim + 1) * 7;
                    }
                }
            }
            julian += date;
    }
    else {
        // assert(bestStamp == doyStamp || bestStamp == woyStamp ||
        // bestStamp == UNSET).  In the last case we should use January 1.

            // No month, start with January 0 (day before Jan 1), then adjust.
            --year;
            julian = (long) (Math.floor(365.25*year) + 428 + 1720995);

        if (bestStamp == UNSET) {
        ++julian;
        }
            else if (bestStamp == doyStamp) {
                julian += internalGet(DAY_OF_YEAR);
        }
        else if (bestStamp == woyStamp) {
                // Compute from day of week plus week of year

                // The first thing we have to do is do the Gregorian adjustment,
                // if necessary.  We figure out the adjusted value 'j' and use
                // that.  We redo this later when we get the real final number.
                // This double computation provides the best accuracy around the
                // Gregorian cutover.
                long j = julian;
                if (julianDayToMillis(julian) >= gregorianCutover)
                {
                    long adjust = (long) (0.01 * year);
                    j += 2 - adjust + (long) (0.25*adjust);
                }

                // Find the day of the week for the first of this year.  This
                // is zero-based, with 0 being the locale-specific first day of
                // the week.  Add 1 to get the 1st day of month.  Subtract
                // getFirstDayOfWeek() to make 0-based.
                int fdy = julianDayToDayOfWeek(j + 1) - getFirstDayOfWeek();
                if (fdy < 0) fdy += 7;

                // Find the start of the first week.  This may be a valid date
                // from 1..7, or a date before the first, from 0..-6.  It
                // represents the locale-specific first day of the week
                // of the first day of the year.

                // First ignore the minimal days in first week.
        date = 1 - fdy + ((stamp[DAY_OF_WEEK] != UNSET) ?
                  (internalGet(DAY_OF_WEEK) - getFirstDayOfWeek()) : 0);

                // Adjust for minimal days in first week.
                if ((7 - fdy) < getMinimalDaysInFirstWeek()) date += 7;

                // Now adjust for the week number.
                date += 7 * (internalGet(WEEK_OF_YEAR) - 1);

                julian += date;
            }
    }

    // Now adjust for Gregorian if necessary. Note that dates that fall in
    // the "gap" between the Julian and Gregorian calendars will be treated
    // as Gregorian. Strictly speaking, they're illegal.
    millis = julianDayToMillis(julian);
    if (millis >= gregorianCutover)
        {
            long adjust = (long) (0.01 * year);
            julian += 2 - adjust + (long) (0.25*adjust);
            millis = julianDayToMillis(julian);
        }

        // Now we can do the time portion of the conversion.

        int millisInDay = 0;

    // Find the best set of fields specifying the time of day.  There
    // are only two possibilities here; the HOUR_OF_DAY or the
    // AM_PM and the HOUR.
    int hourOfDayStamp = stamp[HOUR_OF_DAY];
    int hourStamp = stamp[HOUR];
    bestStamp = (hourStamp > hourOfDayStamp) ? hourStamp : hourOfDayStamp;

        // Hours
    if (bestStamp != UNSET) {
        if (bestStamp == hourOfDayStamp)
        // Don't normalize here; let overflow bump into the next period.
        // This is consistent with how we handle other fields.
        millisInDay += internalGet(HOUR_OF_DAY);

        else {
        // Don't normalize here; let overflow bump into the next period.
        // This is consistent with how we handle other fields.
        millisInDay += internalGet(HOUR);

        millisInDay += 12 * internalGet(AM_PM); // Default works for unset AM_PM
        }
    }

        // Minutes. We use the fact that unset == 0
        millisInDay *= 60;
        millisInDay += internalGet(MINUTE);

        // Seconds. unset == 0
        millisInDay *= 60;
        millisInDay += internalGet(SECOND);

        // Milliseconds. unset == 0
        millisInDay *= 1000;
        millisInDay += internalGet(MILLISECOND);

        // Compute the time zone offset and DST offset.  There are two potential
        // ambiguities here.  We'll assume a 2:00 am (wall time) switchover time
        // for discussion purposes here.
        // 1. The transition into DST.  Here, a designated time of 2:00 am - 2:59 am
        //    can be in standard or in DST depending.  However, 2:00 am is an invalid
        //    representation (the representation jumps from 1:59:59 am Std to 3:00:00 am DST).
        //    We assume standard time.
        // 2. The transition out of DST.  Here, a designated time of 1:00 am - 1:59 am
        //    can be in standard or DST.  Both are valid representations (the rep
        //    jumps from 1:59:59 DST to 1:00:00 Std).
        //    Again, we assume standard time.
        // We use the TimeZone object, unless the user has explicitly set the ZONE_OFFSET
        // or DST_OFFSET fields; then we use those fields.
        TimeZone zone = getTimeZone();
        int zoneOffset = (stamp[ZONE_OFFSET] >= MINIMUM_USER_STAMP)
        /*isSet(ZONE_OFFSET) && userSetZoneOffset*/ ?
            internalGet(ZONE_OFFSET) : zone.getRawOffset();

        // Now add date and millisInDay together, to make millis contain local wall
        // millis, with no zone or DST adjustments
        millis += millisInDay;

        int dstOffset = 0;
        if (stamp[ZONE_OFFSET] >= MINIMUM_USER_STAMP
        /*isSet(DST_OFFSET) && userSetDSTOffset*/) dstOffset = internalGet(DST_OFFSET);
        else
        {
            /* Normalize the millisInDay to 0..ONE_DAY-1.  If the millis is out
             * of range, then we must call timeToFields() to recompute our
             * fields. */
            int[] normalizedMillisInDay = new int[1];
            floorDivide(millis, (int)ONE_DAY, normalizedMillisInDay);

            // We need to have the month, the day, and the day of the week.
            // Calling timeToFields will compute the MONTH and DATE fields.
            // If we're lenient then we need to call timeToFields() to
            // normalize the year, month, and date numbers.
            int dow;
            if (isLenient() || stamp[MONTH] == UNSET || stamp[DATE] == UNSET
                || millisInDay != normalizedMillisInDay[0]) {
                timeToFields(millis, true); // Use wall time; true == do quick computation
                dow = internalGet(DAY_OF_WEEK); // DOW is computed by timeToFields
                millisInDay = normalizedMillisInDay[0];
            }
            else {
                // It's tempting to try to use DAY_OF_WEEK here, if it
                // is set, but we CAN'T.  Even if it's set, it might have
                // been set wrong by the user.  We should rely only on
                // the Julian day number, which has been computed correctly
                // using the disambiguation algorithm above. [LIU]
                dow = julianDayToDayOfWeek(julian);
            }

            // It's tempting to try to use DAY_OF_WEEK here, if it
            // is set, but we CAN'T.  Even if it's set, it might have
            // been set wrong by the user.  We should rely only on
            // the Julian day number, which has been computed correctly
            // using the disambiguation algorithm above. [LIU]
            dstOffset = zone.getOffset(era,
                                       internalGet(YEAR),
                                       internalGet(MONTH),
                                       internalGet(DATE),
                                       julianDayToDayOfWeek(julian),
                                       millisInDay) -
                zoneOffset;
            // Note: Because we pass in wall millisInDay, rather than
            // standard millisInDay, we interpret "1:00 am" on the day
            // of cessation of DST as "1:00 am Std" (assuming the time
            // of cessation is 2:00 am).
        }

        // Store our final computed GMT time, with timezone adjustments.
        time = millis - zoneOffset - dstOffset;
    }

    /**
     * Override hashCode.
     * Generates the hash code for the GregorianCalendar object
     */
    public synchronized int hashCode()
    {
        return getFirstDayOfWeek() ^ getMinimalDaysInFirstWeek();
    }

    /**
     * Overrides Calendar
     * Date Arithmetic function.
     * Adds the specified (signed) amount of time to the given time field,
     * based on the calendar's rules.
     * @param field the time field.
     * @param amount the amount of date or time to be added to the field.
     * @exception IllegalArgumentException if an unknown field is given.
     */
    public void add(int field, int amount)
    {
        if (amount == 0) return;   // Do nothing!
        complete();

        if (field == YEAR)
        {
            int year = this.internalGet(YEAR);
            if (this.internalGetEra() == AD)
            {
                year += amount;
                if (year > 0)
                    this.set(YEAR, year);
                else // year <= 0
                {
                    this.set(YEAR, 1 - year);
                    // if year == 0, you get 1 BC
                    this.set(ERA, BC);
                }
            }
            else // era == BC
            {
                year -= amount;
                if (year > 0)
                    this.set(YEAR, year);
                else // year <= 0
                {
                    this.set(YEAR, 1 - year);
                    // if year == 0, you get 1 AD
                    this.set(ERA, AD);
                }
            }
            pinDayOfMonth();
        }
        else if (field == MONTH)
        {
            int month = this.internalGet(MONTH) + amount;
            if (month >= 0)
            {
                set(YEAR, internalGet(YEAR) + (month / 12));
                set(MONTH, (int) (month % 12));
            }
            else // month < 0
            {
                set(YEAR, internalGet(YEAR) + ((month + 1) / 12) - 1);
                month %= 12;
                if (month < 0) month += 12;
                set(MONTH, JANUARY + month);
            }
            pinDayOfMonth();
        }
        else if (field == ERA)
        {
            int era = internalGet(ERA) + amount;
            if (era < 0) era = 0;
            if (era > 1) era = 1;
            set(ERA, era);
        }
        else
        {
            // We handle most fields here.  The algorithm is to add a computed amount
            // of millis to the current millis.  The only wrinkle is with DST -- if
            // the result of the add operation is to move from DST to Standard, or vice
            // versa, we need to adjust by an hour forward or back, respectively.
            // Otherwise you get weird effects in which the hour seems to shift when
            // you add to the DAY_OF_MONTH field, for instance.

            // Save the current DST state.
            long dst = internalGet(DST_OFFSET);

            long delta = amount;
            switch (field)
            {
            case WEEK_OF_YEAR:
            case WEEK_OF_MONTH:
            case DAY_OF_WEEK_IN_MONTH:
                delta *= 7 * 24 * 60 * 60 * 1000; // 7 days
                break;

            case AM_PM:
                delta *= 12 * 60 * 60 * 1000; // 12 hrs
                break;

            case DATE: // synonym of DAY_OF_MONTH
            case DAY_OF_YEAR:
            case DAY_OF_WEEK:
                delta *= 24 * 60 * 60 * 1000; // 1 day
                break;

            case HOUR_OF_DAY:
            case HOUR:
                delta *= 60 * 60 * 1000; // 1 hour
                break;

            case MINUTE:
                delta *= 60 * 1000; // 1 minute
                break;

            case SECOND:
                delta *= 1000; // 1 second
                break;

            case MILLISECOND:
                // Simply break out on MILLISECOND
                break;

            case ZONE_OFFSET:
            case DST_OFFSET:
            default:
                throw new IllegalArgumentException();
            }

            setTimeInMillis(time + delta); // Automatically computes fields if necessary

            // Now do the DST adjustment alluded to above.
            // Only call setTimeInMillis if necessary, because it's an expensive call.
            dst -= internalGet(DST_OFFSET);
            if (delta != 0) setTimeInMillis(time + dst);
        }
    }


    /**
     * Overrides Calendar
     * Time Field Rolling function.
     * Rolls (up/down) a single unit of time on the given time field.
     * @param field the time field.
     * @param up Indicates if rolling up or rolling down the field value.
     * @exception IllegalArgumentException if an unknown field value is given.
     */
    public void roll(int field, boolean up)
    {
        roll(field, up ? +1 : -1);
    }

    /**
     * Roll a field by a signed amount.
     * Note: This will be made public later. [LIU]
     */
    void roll(int field, int amount)
    {
        if (amount == 0) return; // Nothing to do

        complete();

        int min = getMinimum(field);
        int max = getMaximum(field);
        int gap;

        switch (field) {
        case ERA:
        case YEAR:
        case AM_PM:
        case HOUR:
        case HOUR_OF_DAY:
        case MINUTE:
        case SECOND:
        case MILLISECOND:
            // These fields are handled simply, since they have fixed minima
            // and maxima.  The field DAY_OF_MONTH is almost as simple.  Other
            // fields are complicated, since the range within they must roll
            // varies depending on the date.
            break;

    case MONTH:
        // Rolling the month involves both pinning the final value to [0, 11]
        // and adjusting the DAY_OF_MONTH if necessary.  We only adjust the
        // DAY_OF_MONTH if, after updating the MONTH field, it is illegal.
        // E.g., <jan31>.roll(MONTH, 1) -> <feb28> or <feb29>.
        {
        int mon = (internalGet(MONTH) + amount) % 12;
        if (mon < 0) mon += 12;
        set(MONTH, mon);

        // Keep the day of month in range.  We don't want to spill over
        // into the next month; e.g., we don't want jan31 + 1 mo -> feb31 ->
        // mar3.
        // NOTE: We could optimize this later by checking for dom <= 28
        // first.  Do this if there appears to be a need. [LIU]
                int monthLen = monthLength(mon);
        int dom = internalGet(DAY_OF_MONTH);
        if (dom > monthLen) set(DAY_OF_MONTH, monthLen);
        return;
        }

        case WEEK_OF_YEAR:
            {
                // Unlike WEEK_OF_MONTH, WEEK_OF_YEAR never shifts the day of the
                // week.  Also, rolling the week of the year can have seemingly
                // strange effects simply because the year of the week of year
                // may be different from the calendar year.  For example, the
                // date Dec 28, 1997 is the first day of week 1 of 1998 (if
                // weeks start on Sunday and the minimal days in first week is
                // <= 3).
                int woy = internalGet(WEEK_OF_YEAR);
                // Get the "canonical" year, which matches the week of year.  This
                // may be one year before or after the calendar year.
                int canonicalYear = internalGet(YEAR);
                int canonicalDoy = internalGet(DAY_OF_YEAR);
                if (internalGet(MONTH) == Calendar.JANUARY) {
                    if (woy >= 52) {
                        --canonicalYear;
                        canonicalDoy += yearLength(canonicalYear);
                    }
                }
                else {
                    if (woy == 1) {
                        canonicalDoy -= yearLength(canonicalYear);
                        ++canonicalYear;
                    }
                }
                woy += amount;
                // Do fast checks to avoid unnecessary computation:
                if (woy < 1 || woy > 52) {
                    // Determine the last week of the CANONICAL year.
                    // We do this using the standard formula we use
                    // everywhere in this file.  If we can see that the
                    // days at the end of the year are going to fall into
                    // week 1 of the next year, we drop the last week by
                    // subtracting 7 from the last day of the year.
                    int lastDoy = yearLength(canonicalYear);
                    int lastRelDow = (lastDoy - canonicalDoy + internalGet(DAY_OF_WEEK) - getFirstDayOfWeek()) % 7;
                    if (lastRelDow < 0) lastRelDow += 7;
                    if ((6 - lastRelDow) >= getMinimalDaysInFirstWeek()) lastDoy -= 7;
                    int lastWoy = weekNumber(lastDoy, lastRelDow + 1);
                    woy = ((woy + lastWoy - 1) % lastWoy) + 1;
                }
                set(WEEK_OF_YEAR, woy);
                set(YEAR, canonicalYear);
                return;
            }
        case WEEK_OF_MONTH:
            {
                // This is tricky, because during the roll we may have to shift
                // to a different day of the week.  For example:

                //    s  m  t  w  r  f  s
                //          1  2  3  4  5
                //    6  7  8  9 10 11 12

                // When rolling from the 6th or 7th back one week, we go to the
                // 1st (assuming that the first partial week counts).  The same
                // thing happens at the end of the month.

                // The other tricky thing is that we have to figure out whether
                // the first partial week actually counts or not, based on the
                // minimal first days in the week.  And we have to use the
                // correct first day of the week to delineate the week
                // boundaries.

                // Here's our algorithm.  First, we find the real boundaries of
                // the month.  Then we discard the first partial week if it
                // doesn't count in this locale.  Then we fill in the ends with
                // phantom days, so that the first partial week and the last
                // partial week are full weeks.  We then have a nice square
                // block of weeks.  We do the usual rolling within this block,
                // as is done elsewhere in this method.  If we wind up on one of
                // the phantom days that we added, we recognize this and pin to
                // the first or the last day of the month.  Easy, eh?

                // Normalize the DAY_OF_WEEK so that 0 is the first day of the week
                // in this locale.  We have dow in 0..6.
                int dow = internalGet(DAY_OF_WEEK) - getFirstDayOfWeek();
                if (dow < 0) dow += 7;

                // Find the day of the week (normalized for locale) for the first
                // of the month.
                int fdm = (dow - internalGet(DAY_OF_MONTH) + 1) % 7;
                if (fdm < 0) fdm += 7;

                // Get the first day of the first full week of the month,
                // including phantom days, if any.  Figure out if the first week
                // counts or not; if it counts, then fill in phantom days.  If
                // not, advance to the first real full week (skip the partial week).
                int start;
                if ((7 - fdm) < getMinimalDaysInFirstWeek())
                    start = 8 - fdm; // Skip the first partial week
                else
                    start = 1 - fdm; // This may be zero or negative

                // Get the day of the week (normalized for locale) for the last
                // day of the month.
                int monthLen = monthLength(internalGet(MONTH));
                int ldm = (monthLen - internalGet(DAY_OF_MONTH) + dow) % 7;
                // We know monthLen >= DAY_OF_MONTH so we skip the += 7 step here.

                // Get the limit day for the blocked-off rectangular month; that
                // is, the day which is one past the last day of the month,
                // after the month has already been filled in with phantom days
                // to fill out the last week.  This day has a normalized DOW of 0.
                int limit = monthLen + 7 - ldm;

                // Now roll between start and (limit - 1).
                gap = limit - start;
                int day_of_month = (internalGet(DAY_OF_MONTH) + amount*7 -
                                    start) % gap;
                if (day_of_month < 0) day_of_month += gap;
                day_of_month += start;

                // Finally, pin to the real start and end of the month.
                if (day_of_month < 1) day_of_month = 1;
                if (day_of_month > monthLen) day_of_month = monthLen;

                // Set the DAY_OF_MONTH.  We rely on the fact that this field
                // takes precedence over everything else (since all other fields
                // are also set at this point).  If this fact changes (if the
                // disambiguation algorithm changes) then we will have to unset
                // the appropriate fields here so that DAY_OF_MONTH is attended
                // to.
                set(DAY_OF_MONTH, day_of_month);
                return;
            }
        case DAY_OF_MONTH:
            max = monthLength(internalGet(MONTH));
            break;
        case DAY_OF_YEAR:
            {
                // Roll the day of year using millis.  Compute the millis for
                // the start of the year, and get the length of the year.
                long delta = amount * ONE_DAY; // Scale up from days to millis
                long min2 = time - (internalGet(DAY_OF_YEAR) - 1) * ONE_DAY;
                int yearLength = isLeapYear(internalGet(YEAR)) ? 366 : 365;
                time = (time + delta - min2) % (yearLength*ONE_DAY);
                if (time < 0) time += yearLength*ONE_DAY;
                setTimeInMillis(time + min2);
                return;
            }
        case DAY_OF_WEEK:
            {
                // Roll the day of week using millis.  Compute the millis for
                // the start of the week, using the first day of week setting.
                // Restrict the millis to [start, start+7days).
                long delta = amount * ONE_DAY; // Scale up from days to millis
                // Compute the number of days before the current day in this
                // week.  This will be a value 0..6.
                int leadDays = internalGet(DAY_OF_WEEK) - getFirstDayOfWeek();
                if (leadDays < 0) leadDays += 7;
                long min2 = time - leadDays * ONE_DAY;
                time = (time + delta - min2) % ONE_WEEK;
                if (time < 0) time += ONE_WEEK;
                setTimeInMillis(time + min2);
                return;
            }
        case DAY_OF_WEEK_IN_MONTH:
            {
                // Roll the day of week in the month using millis.  Determine
                // the first day of the week in the month, and then the last,
                // and then roll within that range.
                long delta = amount * ONE_WEEK; // Scale up from weeks to millis
                // Find the number of same days of the week before this one
                // in this month.
                int preWeeks = (internalGet(DAY_OF_MONTH) - 1) / 7;
                // Find the number of same days of the week after this one
                // in this month.
                int postWeeks = (monthLength(internalGet(MONTH)) -
                                 internalGet(DAY_OF_MONTH)) / 7;
                // From these compute the min and gap millis for rolling.
                long min2 = time - preWeeks * ONE_WEEK;
                long gap2 = ONE_WEEK * (preWeeks + postWeeks + 1); // Must add 1!
                // Roll within this range
                time = (time + delta - min2) % gap2;
                if (time < 0) time += gap2;
                setTimeInMillis(time + min2);
                return;
            }
        case ZONE_OFFSET:
        case DST_OFFSET:
        default:
            // These fields cannot be rolled
            throw new IllegalArgumentException();
        }

        // These are the standard roll instructions.  These work for all
        // simple cases, that is, cases in which the limits are fixed, such
        // as the hour, the month, and the era.
        gap = max - min + 1;
        int value = internalGet(field) + amount;
        value = (value - min) % gap;
        if (value < 0) value += gap;
        value += min;

        set(field, value);
    }

    /**
     * <pre>
     * Field names Minimum Greatest Minimum Least Maximum Maximum
     * ----------- ------- ---------------- ------------- -------
     * ERA 0 0 1 1
     * YEAR 1 1 5,000,000 5,000,000
     * MONTH 0 0 11 11
     * WEEK_OF_YEAR 1 1 52 53
     * WEEK_OF_MONTH 0 0 4 6
     * DAY_OF_MONTH 1 1 28 31
     * DAY_OF_YEAR 1 1 365 366
     * DAY_OF_WEEK 1 1 7 7
     * DAY_OF_WEEK_IN_MONTH -1 -1 4 6
     * AM_PM 0 0 1 1
     * HOUR 0 0 11 12
     * HOUR_OF_DAY 0 0 23 23
     * MINUTE 0 0 59 59
     * SECOND 0 0 59 59
     * MILLISECOND 0 0 999 999
     * ZONE_OFFSET -12*60*60*1000 -12*60*60*1000 12*60*60*1000 12*60*60*1000
     * DST_OFFSET 0 0 1*60*60*1000 1*60*60*1000
     * </pre>
     */
    private static final int MinValues[]
    = {0,1,0,1,0,1,1,1,-1,0,0,0,0,0,0,-12*60*60*1000,0};
    private static final int GreatestMinValues[]
    = {0,1,0,1,0,1,1,1,-1,0,0,0,0,0,0,-12*60*60*1000,0};// same as MinValues
    private static final int LeastMaxValues[]
    = {1,5000000,11,52,4,28,365,7,4,1,11,23,59,59,999,
       12*60*60*1000,1*60*60*1000};
    private static final int MaxValues[]
    = {1,5000000,11,53,6,31,366,7,6,1,12,23,59,59,999,
       12*60*60*1000,1*60*60*1000};

    /**
     * Returns minimum value for the given field.
     * e.g. for Gregorian DAY_OF_MONTH, 1
     * Please see Calendar.getMinimum for descriptions on parameters and
     * the return value.
     */
    public int getMinimum(int field)
    {
        return MinValues[field];
    }

    /**
     * Returns maximum value for the given field.
     * e.g. for Gregorian DAY_OF_MONTH, 31
     * Please see Calendar.getMaximum for descriptions on parameters and
     * the return value.
     */
    public int getMaximum(int field)
    {
        return MaxValues[field];
    }

    /**
     * Returns highest minimum value for the given field if varies.
     * Otherwise same as getMinimum(). For Gregorian, no difference.
     * Please see Calendar.getGreatestMinimum for descriptions on parameters
     * and the return value.
     */
    public int getGreatestMinimum(int field)
    {
        return GreatestMinValues[field];
    }

    /**
     * Returns lowest maximum value for the given field if varies.
     * Otherwise same as getMaximum(). For Gregorian DAY_OF_MONTH, 28
     * Please see Calendar.getLeastMaximum for descriptions on parameters and
     * the return value.
     */
    public int getLeastMaximum(int field)
    {
        return LeastMaxValues[field];
    }
    
    /**
     * Return the ERA.  We need a special method for this because the
     * default ERA is AD, but a zero (unset) ERA is BC.
     */
    private final int internalGetEra() {
        return isSet(ERA) ? internalGet(ERA) : AD;
    }
}
