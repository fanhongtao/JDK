/*
 * @(#)GregorianCalendar.java	1.20 97/03/09
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
 * 	// get the supported ids for GMT-08:00 (Pacific Standard Time)
 * String[] ids = TimeZone.getAvailableIDs(-8 * 60 * 60 * 1000);
 * 	// if no ids were returned, something is wrong. get out.
 * if (ids.length == 0)
 *     System.exit(0);
 *
 * 	// begin output
 * System.out.println("Current Time");
 *
 * 	// create a Pacific Standard Time time zone
 * SimpleTimeZone pdt = new SimpleTimeZone(-8 * 60 * 60 * 1000, ids[0]);
 *
 * 	// set up rules for daylight savings time
 * pdt.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
 * pdt.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
 *
 * 	// create a GregorianCalendar with the Pacific Daylight time zone
 *	// and the current date and time
 * Calendar calendar = new GregorianCalendar(pdt);
 * Date trialTime = new Date();
 * calendar.setTime(trialTime);
 *
 *	// print out a bunch of interesting things
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
 * @version      1.20 03/09/97
 * @author       David Goldsmith, Mark Davis, Chen-Lieh Huang
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
    private static final int numDays[]
    = {0,31,59,90,120,151,181,212,243,273,304,334}; // 0-based, for day-in-year
    private static final int leapNumDays[]
    = {0,31,60,91,121,152,182,213,244,274,305,335}; // 0-based, for day-in-year
    private static final int maxDaysInMonth[]
    = {31,28,31,30,31,30,31,31,30,31,30,31}; // 0-based

    // This is measured from the standard epoch, not in Julian Days.
    // Default is 00:00:00 local time, October 15, 1582.
    private long gregorianCutover = -12219292800000L;

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
        super(TimeZone.getDefault(), Locale.getDefault());
	this.set(Calendar.ERA, AD);
        time = System.currentTimeMillis();
        isTimeSet = true;
        areFieldsSet = false;
    }

    /**
     * Constructs a GregorianCalendar based on the current time
     * in the given time zone with the default locale.
     * @param zone the given time zone.
     */
    public GregorianCalendar(TimeZone zone)
    {
        super(zone, Locale.getDefault());
	this.set(Calendar.ERA, AD);
        time = System.currentTimeMillis();
        isTimeSet = true;
        areFieldsSet = false;
    }

    /**
     * Constructs a GregorianCalendar based on the current time
     * in the default time zone with the given locale.
     * @param aLocale the given locale.
     */
    public GregorianCalendar(Locale aLocale)
    {
        super(TimeZone.getDefault(), aLocale);
	this.set(Calendar.ERA, AD);
        time = System.currentTimeMillis();
        isTimeSet = true;
        areFieldsSet = false;
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
	this.set(Calendar.ERA, AD);
        time = System.currentTimeMillis();
        isTimeSet = true;
        areFieldsSet = false;
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
        this.set(Calendar.ERA, AD);
        this.set(Calendar.YEAR, year);
        this.set(Calendar.MONTH, month);
        this.set(Calendar.DATE, date);
//        computeTime();
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
        this.set(Calendar.ERA, AD);
        this.set(Calendar.YEAR, year);
        this.set(Calendar.MONTH, month);
        this.set(Calendar.DATE, date);
        this.set(Calendar.HOUR_OF_DAY, hour);
        this.set(Calendar.MINUTE, minute);
//        computeTime();
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
        this.set(Calendar.ERA, AD);
        this.set(Calendar.YEAR, year);
        this.set(Calendar.MONTH, month);
        this.set(Calendar.DATE, date);
        this.set(Calendar.HOUR_OF_DAY, hour);
        this.set(Calendar.MINUTE, minute);
        this.set(Calendar.SECOND, second);
//        computeTime();
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

    // Converts the time field list to the time as milliseconds.
    private final void timeToFields(long theTime)
    {
        int year, month, date, dayOfWeek, dayOfYear, weekCount, era = AD;
        // The following variable names are somewhat cryptic. Unfortunately,
        // they are from the original program cited above, and no explanation
        // for their meaning is given. Given that the algorithm is cryptic too,
        // perhaps it doesn't matter...
        long ja, jb, jd;
//        int jc, je;
        long jc, je; // to fix number overflow problem.

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
//        jc = (int) (6680.0 + ((double) (jb - 2439870) - 122.1) / 365.25);
        jc = (long) (6680.0 + ((double) (jb - 2439870) - 122.1) / 365.25);
        jd = (long) (365*jc + (0.25 * jc));
//        je = (int) ((jb-jd)/30.6001);
        je = (long) ((jb-jd)/30.6001);
//        date = (int) (jb-jd-(int) (30.6001 * je));
        date = (int) (jb-jd-(long) (30.6001 * je));
//        month = je - 1;
        month = (int) je - 1;
        if (month > 12)
            month -= 12;
        else if (month < 0) // added by CLH
            month += 12;    // added by CLH, 8-7-96
        year = (int) (jc-4715);
        if (month > 2)
            --year;
        if (year <= 0)
        {
            era = BC;
            year = 1-year;
        }

        this.set(Calendar.ERA, era);
        this.set(Calendar.YEAR, year);
        this.set(Calendar.MONTH, month-1); // 0-based
        this.set(Calendar.DATE, date);
        if (julian >= -1)
            dayOfWeek = Calendar.SUNDAY + (int) ((julian + 1) % 7);
        else // julian <= -2
            dayOfWeek = Calendar.SUNDAY + (int) ((julian + 1) % 7 + 7);
        // CLH, 8-7-96
        this.set(Calendar.DAY_OF_WEEK, dayOfWeek);

        if (isLeapYear(year))
            dayOfYear = leapNumDays[month-1] + date; // month: 0-based
        else
            dayOfYear = numDays[month-1] + date; // month: 0-based
        this.set(Calendar.DAY_OF_YEAR, dayOfYear);

        int firstDatesDay = ((dayOfWeek-1) - dayOfYear + 1) % 7;
        // dayOfWeek-1 makes 0-based
        if (firstDatesDay < 0) firstDatesDay += 7;      // fix modulo
        weekCount = weekNumber(dayOfYear, dayOfWeek-1, firstDatesDay,
                               getFirstDayOfWeek()-1,
                               getMinimalDaysInFirstWeek());
        this.set(Calendar.WEEK_OF_YEAR, weekCount);

        firstDatesDay = ((dayOfWeek-1) - date + 1) % 7;
        if (firstDatesDay < 0) firstDatesDay += 7;      // fix modulo
        weekCount = weekNumber(date, dayOfWeek-1, firstDatesDay,
                               getFirstDayOfWeek()-1,
                               getMinimalDaysInFirstWeek());
        this.set(Calendar.WEEK_OF_MONTH, weekCount);
    }


    /**
     * @param date day-of-year or day-of-month. Month is zero based.
     * @param day day-of-week for date. Zero based.
     * @param firstDatesDay the day-of-week for the first day of the year
     * or month.
     * @param firstDay the first day of the week for the calendar.
     * e.g. Sunday for US.
     * @param minimalDaysInFirstWeek the minimal number of days in the
     * week to qualify as the first week; otherwise partial week belongs
     * to last (year/month).
     * <note> weekCount is the number of weeks in the (year/month) for the
     * date. If zero, belongs to last (year/month). You MUST add the date to
     * the last day of the previous (year/month), and call this method again!
     * @return week number, one-based.
     */
    private static int weekNumber(int date, int day, int firstDatesDay,
                                  int firstDay, int minimalDaysInFirstWeek)
    {
		// first estimate the number of weeks
		int weekCount = (date - 1)/7 + 1;
		// now the day-of-week of the last day in any week.
		int lastDay = (firstDay + 6) % 7;
		// bump it up to normalize, if less than first date's day-of-week
		if (lastDay < firstDatesDay)
			lastDay += 7;
		// now, if the first week is invalid, decrement the weekCount
		if (lastDay - firstDatesDay + 1 < minimalDaysInFirstWeek)
			--weekCount;
		return weekCount;
    }


    // XXX Currently this method has a bug, and it is not being used.
    private static int dateFrom(int weekNumber, int day, int firstDatesDay,
                                int firstDay, int minimalDaysInFirstWeek)
    {
		// first estimate the number of days
		int diff = day - firstDatesDay;
		if (diff < 0)
			diff += 7;
		int dayCount = (weekNumber - 1)*7 + diff + 1;
		// now the day-of-week of the last day in any week.
		int lastDay = (firstDay + 6) % 7;
		// bump it up to normalize, if less than first date's day-of-week
		if (lastDay < firstDatesDay)
			lastDay += 7;

		// now, if the first week is invalid, decrement the day count.
		if (lastDay - firstDatesDay + 1 < minimalDaysInFirstWeek)
			dayCount -= 7;
		return dayCount;
    }


    /**
     * Determines if the given year is a leap year. Returns true if the
     * given year is a leap year.
     * @param year the given year.
     * @return true if the given year is a leap year; false otherwise.
     */
    public boolean isLeapYear(int year)
    {
        return ((year%4 == 0) && ((year%100 != 0) || (year%400 == 0)));
    }

    /**
     * Overrides Calendar
     * Converts UTC as milliseconds to time field values.
     */
    protected void computeFields()
    {
        if (!isTimeSet) return;

        long localTime = time;

        TimeZone zone = getTimeZone();

        if (zone != null)
        {
            localTime += zone.getRawOffset();
        }

        timeToFields(localTime);
        int era = this.internalGet(Calendar.ERA);
        int year = this.internalGet(Calendar.YEAR);
        int month = this.internalGet(Calendar.MONTH);
        int date = this.internalGet(Calendar.DATE);
        int dayOfWeek = this.internalGet(Calendar.DAY_OF_WEEK);

        int timeOfDay = (int) (localTime % millisPerDay);
        if (timeOfDay < 0)
            timeOfDay += millisPerDay;
        int gmtOffset = 0;
        int dstOffset = 0;
        if (zone != null)
        {
            gmtOffset = zone.getRawOffset();
            dstOffset = zone.getOffset(era, year, month, date, dayOfWeek,
                                       timeOfDay);
            localTime = time + dstOffset;
            dstOffset -= gmtOffset;
            if (timeOfDay + dstOffset >= millisPerDay) // CLH: Fixed > to >=
            {
                // Uh-oh... daylight time changed the date.
                // We must recompute.
                timeToFields(localTime);

                                // Refetch the fields for computations below
                dayOfWeek = this.internalGet(Calendar.DAY_OF_WEEK);
                date = this.internalGet(Calendar.DATE);
                month = this.internalGet(Calendar.MONTH);
                year = this.internalGet(Calendar.YEAR);
                era = this.internalGet(Calendar.ERA);
            }
            timeOfDay = (int) (localTime % millisPerDay);
            if (timeOfDay < 0)
                timeOfDay += millisPerDay;
        }
        this.set(Calendar.MILLISECOND, timeOfDay % 1000);
        timeOfDay /= 1000;
        this.set(Calendar.SECOND, timeOfDay % 60);
        timeOfDay /= 60;
        this.set(Calendar.MINUTE, timeOfDay % 60);
        timeOfDay /= 60;
        this.set(Calendar.HOUR_OF_DAY, timeOfDay);
        this.set(Calendar.AM_PM, timeOfDay / 12);
        int hour = timeOfDay % 12;
        this.set(Calendar.HOUR, hour);
        this.set(Calendar.ZONE_OFFSET, gmtOffset);
        this.set(Calendar.DST_OFFSET, dstOffset);

        // Following may need fixing with respect to
        // "first day of year/month" setting
        this.set(Calendar.DAY_OF_WEEK_IN_MONTH, (date-1) / 7 + 1);

        areFieldsSet = true;
    }

    /**
     * Validates the values of the set time fields.
     */
    private boolean validateFields()
    {
        int    field;

        for (field = Calendar.ERA; field <= Calendar.WEEK_OF_MONTH; field++)
            if (isSet(field) && !boundsCheck(this.internalGet(field), field))
                return false;

        // Values differ in Least-Maximum and Maximum should be handled
        // specialy.
        if (isSet(Calendar.DAY_OF_MONTH))
        {
            int year = this.internalGet(Calendar.YEAR);
            int month = this.internalGet(Calendar.MONTH);
            int date = this.internalGet(Calendar.DAY_OF_MONTH);

            if (month==Calendar.FEBRUARY && isLeapYear(year)) {
                if (date < getMinimum(Calendar.DAY_OF_MONTH) || date > 29)
                    return false;
            }
            else if (date < getMinimum(Calendar.DAY_OF_MONTH)
                     || date > maxDaysInMonth[month])
                return false;
        }


        if (isSet(Calendar.DAY_OF_YEAR))
        {
            int year = this.internalGet(Calendar.YEAR);
            int days = this.internalGet(Calendar.DAY_OF_YEAR);

            if (isLeapYear(year)) {
                if (days < 1 || days > 366)
                    return false;
            }
            else if (days < 1 || days > 365)
                return false;
        }


        for (field = Calendar.DAY_OF_WEEK; field <= Calendar.DST_OFFSET; field++)
            if (isSet(field) && !boundsCheck(this.internalGet(field), field))
                return false;

        return true;
    }

    /**
     * Validates the value of the given time field.
     */
    private boolean boundsCheck(int value, int field)
    {
        if (value < getMinimum(field) || value > getMaximum(field))
            return false;
        else return true;
    }

    /**
     * Overrides Calendar
     * Converts time field values to UTC as milliseconds.
     * @exception IllegalArgumentException if an unknown field is given.
     */
    protected void computeTime()
    {
        if (!areFieldsSet) return;

        if (!isLenient() && validateFields() == false)
            throw new IllegalArgumentException();

        isTimeSet = true;

        // This function takes advantage of the fact that unset fields in
        // the time field list have a value of zero.
        time = 0;

        int era = this.internalGet(Calendar.ERA);
        if (era < BC || era > AD)
            throw new IllegalArgumentException();

        // The year is required.
        int year = this.internalGet(Calendar.YEAR);
        int month = 0, date = 0;

        if (year <= 0)
            throw new IllegalArgumentException();

        if (era == BC)
            year = 1 - year;

        long julian = 0;

        // The following code is somewhat convoluted. The various nested
        //  if's handle the different cases of what fields are present.
        if (isSet(Calendar.MONTH) &&
            (isSet(Calendar.DAY_OF_MONTH) ||
             (isSet(Calendar.DAY_OF_WEEK) &&
              (isSet(Calendar.WEEK_OF_MONTH) ||
               isSet(Calendar.DAY_OF_WEEK_IN_MONTH))
                 )
                ))
        {
            // We have the month specified. Make it 1-based for the algorithm.
            month = this.internalGet(Calendar.MONTH) + 1;
            if (month < 1) {
                throw new IllegalArgumentException();
            } else if (month > 12) {
                // normalize month
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

            if (isSet(Calendar.DAY_OF_MONTH))
            {
                date = this.internalGet(Calendar.DAY_OF_MONTH);
                julian += date;
            }
            else // compute from day of week
            {
                // ??To do: compute from day of week plus week,
                // or number of day in month.
                throw new IllegalArgumentException();
            }
        }
        else if (isSet(Calendar.DAY_OF_YEAR) ||
                 (isSet(Calendar.DAY_OF_WEEK) && (isSet(Calendar.WEEK_OF_YEAR))))
        {
            // No month, start with January 0, then adjust.
            --year;
            julian = (long) (Math.floor(365.25*year) + 428 + 1720995);

            if (isSet(Calendar.DAY_OF_YEAR))
                julian += this.internalGet(Calendar.DAY_OF_YEAR);
            else    // Day of week plus week of year
            {
                throw new IllegalArgumentException();
            }
        }
        else    // Not enough information
            throw new IllegalArgumentException();

        // Now adjust for Gregorian if necessary. Note that dates that fall in
        // the "gap" between the Julian and Gregorian calendars
        // will be treated as Gregorian. Strictly speaking, they're illegal.
        if (julianDayToMillis(julian) >= gregorianCutover)
        {
            long adjust = (long) (0.01 * year);
            julian += 2 - adjust + (long) (0.25*adjust);
        }

        time = julianDayToMillis(julian);

        // Now we can do the time portion of the conversion.
        // ??To do: worth doing bounds checking here?

        int tmpTime = 0;

        // Hours
        if (isSet(Calendar.HOUR_OF_DAY))
            // ??Do we need to normalize hourOfDay??
            // tmpTime += this.internalGet(Calendar.HOUR_OF_DAY);
            tmpTime += this.internalGet(Calendar.HOUR_OF_DAY) % 24;
        else if (isSet(Calendar.HOUR))
        {
            // ??Do we need to normalize hour??
            // tmpTime += this.internalGet(Calendar.HOUR);
            tmpTime += this.internalGet(Calendar.HOUR) % 12;
            //tmpTime += 12 * (this.internalGet(Calendar.AM_PM) % 1);//unset ==0
            tmpTime += 12 * this.internalGet(Calendar.AM_PM);
        }

        // Minutes. We use the fact that unset == 0
        tmpTime *= 60;
        tmpTime += this.internalGet(Calendar.MINUTE);

        // Seconds. unset == 0
        tmpTime *= 60;
        tmpTime += this.internalGet(Calendar.SECOND);

        // Milliseconds. unset == 0
        tmpTime *= 1000;
        tmpTime += this.internalGet(Calendar.MILLISECOND);

        // Now add date and tmpTime together, plus time zone adjustment.
        time += tmpTime;

        int offset = 0;
        if (isSet(Calendar.ZONE_OFFSET)) // override calendar's time zone
        {
            offset = this.internalGet(Calendar.ZONE_OFFSET);
            offset += this.internalGet(Calendar.DST_OFFSET);
        }
        else
        {
            TimeZone zone = getTimeZone();
            if (zone != null)
            {
                // We need to have the month, the day, and the day of the week.
                // If we have the month and date, compute the day of the week
                // locally as an optimization. Otherwise, we need to get a
                // Calendar record. Remember, this is local time.

                if (isSet(Calendar.MONTH) && isSet(Calendar.DATE))
                {
                    int dayOfWeek;
                    if (isSet(Calendar.DAY_OF_WEEK))
                        dayOfWeek = this.internalGet(Calendar.DAY_OF_WEEK);
                    else
                        dayOfWeek = Calendar.SUNDAY + (int) ((julian + 1) % 7);
                    offset = zone.getOffset(era,
                                            this.internalGet(Calendar.YEAR),
                                            this.internalGet(Calendar.MONTH),
                                            this.internalGet(Calendar.DATE),
                                            dayOfWeek, tmpTime);
                }
                else
                {
                    long timeSave = time;
                    setTimeInMillis(time - zone.getRawOffset());
                    computeFields();
                    offset
                        = zone.getOffset(era,
                                         this.internalGet(Calendar.YEAR),
                                         this.internalGet(Calendar.MONTH),
                                         this.internalGet(Calendar.DATE),
                                         this.internalGet(Calendar.DAY_OF_WEEK),
                                         tmpTime);
                    time = timeSave;
                }
            }
        }

        time -= offset;
    }

    /**
     * Overrides Cloneable
     */
    public Object clone()
    {
        try
        {
            GregorianCalendar other = (GregorianCalendar) super.clone();
            other.setLenient(this.isLenient());
            other.setTimeZone(this.getTimeZone());
            other.setFirstDayOfWeek(this.getFirstDayOfWeek());
            other.setMinimalDaysInFirstWeek(this.getMinimalDaysInFirstWeek());
            return other;
        }
        catch (InternalError e)
        {
            // this shouldn't happen, since we are Cloneable
            throw e;
        }
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
     * Compares the time field records.
     * Equivalent to comparing result of conversion to UTC.
     * Please see Calendar.equals for descriptions on parameters and
     * the return value.
     */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (!(obj instanceof GregorianCalendar))
            return false;

        GregorianCalendar that = (GregorianCalendar) obj;

        if (getTimeInMillis() != that.getTimeInMillis())
            return false;
        if (this.hashCode() != that.hashCode())
            return false;
        if (isLenient() != that.isLenient())
            return false;
        if (getFirstDayOfWeek() != that.getFirstDayOfWeek())
            return false;
        if (getMinimalDaysInFirstWeek() != that.getMinimalDaysInFirstWeek())
            return false;
        if (!getTimeZone().equals(that.getTimeZone()))
            return false;
        return true;
    }

    /**
     * Overrides Calendar
     * Compares the time field records.
     * Equivalent to comparing result of conversion to UTC.
     * Please see Calendar.before for descriptions on parameters and
     * the return value.
     */
    public boolean before(Object when)
    {
        if (this == when) return true;
        if (when == null || !getClass().equals(when.getClass())) {
            return false;
        }
        GregorianCalendar other = (GregorianCalendar)when;

        return (getTimeInMillis() < other.getTimeInMillis());
    }

    /**
     * Overrides Calendar
     * Compares the time field records.
     * Equivalent to comparing result of conversion to UTC.
     * Please see Calendar.after for descriptions on parameters and
     * the return value.
     */
    public boolean after(Object when)
    {
        if (this == when) return true;
        if (when == null || !getClass().equals(when.getClass())) {
            return false;
        }
        GregorianCalendar other = (GregorianCalendar)when;

        return (getTimeInMillis() > other.getTimeInMillis());
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
        if (!areFieldsSet) computeFields();
        if (amount == 0) return;   // Do nothing!

        if (field == Calendar.YEAR)
        {
            int year = this.internalGet(Calendar.YEAR);
            if (this.internalGet(Calendar.ERA) == GregorianCalendar.AD)
            {
                year += amount;
                if (year > 0)
                    this.set(Calendar.YEAR, year);
                else // year <= 0
                {
                    this.set(Calendar.YEAR, 1 - year);
                    // if year == 0, you get 1 BC
                    this.set(Calendar.ERA, GregorianCalendar.BC);
                }
            }
            else // era == BC
            {
                year -= amount;
                if (year > 0)
                    this.set(Calendar.YEAR, year);
                else // year <= 0
                {
                    this.set(Calendar.YEAR, 1 - year);
                    // if year == 0, you get 1 AD
                    this.set(Calendar.ERA, GregorianCalendar.AD);
                }
            }
        }
        else if (field == Calendar.MONTH)
        {
            int month = this.internalGet(Calendar.MONTH) + amount;
            if (month >= 0)
            {
                add (Calendar.YEAR, (int) (month / 12));
                this.set(Calendar.MONTH, (int) (month % 12));
            }
            else // month < 0
            {
                add (Calendar.YEAR, (int) ((month + 1) / 12) - 1);
                if ((month % 12) == 0)
                    this.set(Calendar.MONTH, Calendar.JANUARY);
                else
                    this.set(Calendar.MONTH, (month % 12) + 12);
            }
/*
            // Obsolete: when month was 1-based.
            int month = (this.internalGet(Calendar.MONTH) - 1) + amount;
            // make month 0-based
            if (month >= 0)
            {
                add (Calendar.YEAR, (int) (month / 12)); // update the year
                this.set(Calendar.MONTH, (month % 12) + 1);
                // make month 1-based again
            }
            else // month < 0
            {
                add (Calendar.YEAR, (int) (month / 12) - 1);
                if ((month % 12) == 0)
                    this.set(Calendar.MONTH, Calendar.JANUARY);
                else
                    this.set(Calendar.MONTH, (month % 12) + 12 + 1);
            }
*/
        }
        else
        {
            long delta = amount;
            switch (field)
            {
            case Calendar.ERA:
            case Calendar.ZONE_OFFSET:
            case Calendar.DST_OFFSET:
                throw new IllegalArgumentException();

            case Calendar.WEEK_OF_YEAR:
            case Calendar.WEEK_OF_MONTH:
            case Calendar.DAY_OF_WEEK_IN_MONTH:
                delta *= 7 * 24 * 60 * 60 * 1000; // 7 days
                break;

            case Calendar.AM_PM:
                delta *= 12 * 60 * 60 * 1000; // 12 hrs
                break;

            case Calendar.DATE: // synonym of DAY_OF_MONTH
            case Calendar.DAY_OF_YEAR:
            case Calendar.DAY_OF_WEEK:
                delta *= 24 * 60 * 60 * 1000; // 1 day
                break;

            case Calendar.HOUR_OF_DAY:
            case Calendar.HOUR:
                delta *= 60 * 60 * 1000; // 1 hour
                break;

            case Calendar.MINUTE:
                delta *= 60 * 1000; // 1 minute
                break;

            case Calendar.SECOND:
                delta *= 1000; // 1 second
                break;

            default:    // Simply break out on MILLISECOND
                break;
            }

            time += delta;
            setTimeInMillis(time);
            computeFields();
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
        if (!areFieldsSet) computeFields();

        if (up)
        {
            switch (field)
            {
            case Calendar.ERA:
            case Calendar.ZONE_OFFSET:
            case Calendar.DST_OFFSET:
            case Calendar.WEEK_OF_YEAR:
            case Calendar.WEEK_OF_MONTH:
            case Calendar.DAY_OF_WEEK_IN_MONTH:
            case Calendar.AM_PM:
            case Calendar.DAY_OF_YEAR:
            case Calendar.DAY_OF_WEEK:
            case Calendar.HOUR:
                throw new IllegalArgumentException();

            case Calendar.YEAR:
                this.set(Calendar.YEAR, (this.internalGet(Calendar.YEAR)+1)
                         % getMaximum(Calendar.YEAR));
                break;

            case Calendar.MONTH:
                this.set(Calendar.MONTH,
                         (this.internalGet(Calendar.MONTH)+1) % 12);
//                       (this.internalGet(Calendar.MONTH)%12) + 1);-if 1-based
                break;

            case Calendar.DATE: // synonym of DAY_OF_MONTH
                if (this.internalGet(Calendar.MONTH)==Calendar.FEBRUARY &&
                    isLeapYear(this.internalGet(Calendar.YEAR)))
                    this.set(Calendar.DATE,
                             (this.internalGet(Calendar.DATE)%29) + 1);
                else
                    this.set(Calendar.DATE,
                             (this.internalGet(Calendar.DATE) %
                              maxDaysInMonth[this.internalGet(Calendar.MONTH)])
                              + 1);
                break;

            case Calendar.HOUR_OF_DAY:
                this.set(Calendar.HOUR_OF_DAY,
                         (this.internalGet(Calendar.HOUR_OF_DAY)+1) % 24);
                break;

            case Calendar.MINUTE:
                this.set(Calendar.MINUTE,
                         (this.internalGet(Calendar.MINUTE)+1) % 60);
                break;

            case Calendar.SECOND:
                this.set(Calendar.SECOND,
                         (this.internalGet(Calendar.SECOND)+1) % 60);
                break;

            case Calendar.MILLISECOND:
                this.set(Calendar.MILLISECOND,
                         (this.internalGet(Calendar.MILLISECOND)+1) % 1000);
                break;

            default:
                break;
            }
        }
        else  // rolling down by 1
        {
            switch (field)
            {
            case Calendar.ERA:
            case Calendar.ZONE_OFFSET:
            case Calendar.DST_OFFSET:
            case Calendar.WEEK_OF_YEAR:
            case Calendar.WEEK_OF_MONTH:
            case Calendar.DAY_OF_WEEK_IN_MONTH:
            case Calendar.AM_PM:
            case Calendar.DAY_OF_YEAR:
            case Calendar.DAY_OF_WEEK:
            case Calendar.HOUR:
                throw new IllegalArgumentException();

            case Calendar.YEAR:
                rollDown(Calendar.YEAR);
                break;

            case Calendar.MONTH:
                rollDown(Calendar.MONTH);
                break;

            case Calendar.DATE: // synonym of DAY_OF_MONTH
                if (this.internalGet(Calendar.DATE)==getMinimum(Calendar.DATE))
                {
                    if (this.internalGet(Calendar.MONTH)==Calendar.FEBRUARY
                        && isLeapYear(this.internalGet(Calendar.YEAR)))
                        this.set(Calendar.DATE, 29);
                    else
                        this.set(Calendar.DATE,
                                 maxDaysInMonth[
                                 this.internalGet(Calendar.MONTH)]);
                }
                else
                    this.set(Calendar.DATE,
                             this.internalGet(Calendar.DATE) - 1);
                break;

            case Calendar.HOUR_OF_DAY:
                rollDown(Calendar.HOUR_OF_DAY);
                break;

            case Calendar.MINUTE:
                rollDown(Calendar.MINUTE);
                break;

            case Calendar.SECOND:
                rollDown(Calendar.SECOND);
                break;

            case Calendar.MILLISECOND:
                rollDown(Calendar.MILLISECOND);
                break;

            default:
                break;
            }
        }
    }


    // Roll the time field value down by 1.
    private void rollDown(int field)
    {
        if (this.internalGet(field) == getMinimum(field))
            this.set(field, getMaximum(field));
        else
            this.set(field, this.internalGet(field) - 1);
    }


    /**
     * <pre>
     * Field names Minimum Greatest Minimum Least Maximum Maximum
     * ----------- ------- ---------------- ------------- -------
     * ERA 0 0 1 1
     * YEAR 1 1 5,000,000 5,000,000
     * MONTH 0 0 11 11
     * WEEK_OF_YEAR 1 1 53 54
     * WEEK_OF_MONTH 1 1 4 6
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
    = {0,1,0,1,1,1,1,1,-1,0,0,0,0,0,0,-12*60*60*1000,0};
    private static final int GreatestMinValues[]
    = {0,1,0,1,1,1,1,1,-1,0,0,0,0,0,0,-12*60*60*1000,0};// same as MinValues
    private static final int LeastMaxValues[]
    = {1,5000000,11,53,4,28,365,7,4,1,11,23,59,59,999,
       12*60*60*1000,1*60*60*1000};
    private static final int MaxValues[]
    = {1,5000000,11,54,6,31,366,7,6,1,12,23,59,59,999,
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
}
