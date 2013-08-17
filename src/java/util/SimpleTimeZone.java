/*
 * @(#)SimpleTimeZone.java	1.19 98/10/22
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

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

/**
 * <code>SimpleTimeZone</code> is a concrete subclass of <code>TimeZone</code>
 * that represents a time zone for use with a Gregorian calendar. This
 * class does not handle historical changes.
 *
 * <P>
 * Use a negative value for <code>dayOfWeekInMonth</code> to indicate that
 * <code>SimpleTimeZone</code> should count from the end of the month backwards.
 * For example, Daylight Savings Time ends at the last
 * (dayOfWeekInMonth = -1) Sunday in October, at 2 AM in standard time.
 *
 * @see      Calendar
 * @see      GregorianCalendar
 * @see      TimeZone
 * @version  1.19 10/22/98
 * @author   David Goldsmith, Mark Davis, Chen-Lieh Huang, Alan Liu
 */
public class SimpleTimeZone extends TimeZone {
    /**
     * Constructs a SimpleTimeZone with the given base time zone offset from GMT
     * and time zone ID. Timezone IDs can be obtained from
     * TimeZone.getAvailableIDs. Normally you should use TimeZone.getDefault to
     * construct a TimeZone.
     *
     * @param rawOffset  The given base time zone offset to GMT.
     * @param ID         The time zone ID which is obtained from
     *                   TimeZone.getAvailableIDs.
     */
    public SimpleTimeZone(int rawOffset, String ID)
    {
        this.rawOffset = rawOffset;
        setID (ID);
    dstSavings = millisPerHour; // In case user sets rules later
    }

    /**
     * Construct a SimpleTimeZone with the given base time zone offset from
     * GMT, time zone ID, time to start and end the daylight time. Timezone IDs
     * can be obtained from TimeZone.getAvailableIDs. Normally you should use
     * TimeZone.getDefault to create a TimeZone. For a time zone that does not
     * use daylight saving time, do not use this constructor; instead you should
     * use SimpleTimeZone(rawOffset, ID).
     *
     * By default, this constructor specifies day-of-week-in-month rules. That
     * is, if the startDay is 1, and the startDayOfWeek is SUNDAY, then this
     * indicates the first Sunday in the startMonth. A startDay of -1 likewise
     * indicates the last Sunday. However, by using negative or zero values for
     * certain parameters, other types of rules can be specified.
     *
     * Day of month. To specify an exact day of the month, such as March 1, set
     * startDayOfWeek to zero.
     *
     * Day of week after day of month. To specify the first day of the week
     * occurring on or after an exact day of the month, make the day of the week
     * negative. For example, if startDay is 5 and startDayOfWeek is -MONDAY,
     * this indicates the first Monday on or after the 5th day of the
     * startMonth.
     *
     * Day of week before day of month. To specify the last day of the week
     * occurring on or before an exact day of the month, make the day of the
     * week and the day of the month negative. For example, if startDay is -21
     * and startDayOfWeek is -WEDNESDAY, this indicates the last Wednesday on or
     * before the 21st of the startMonth.
     *
     * The above examples refer to the startMonth, startDay, and startDayOfWeek;
     * the same applies for the endMonth, endDay, and endDayOfWeek.
     *
     * @param rawOffset       The given base time zone offset to GMT.
     * @param ID              The time zone ID which is obtained from
     *                        TimeZone.getAvailableIDs.
     * @param startMonth      The daylight savings starting month. Month is
     *                        0-based. eg, 0 for January.
     * @param startDay        The daylight savings starting
     *                        day-of-week-in-month. Please see the member
     *                        description for an example.
     * @param startDayOfWeek  The daylight savings starting day-of-week. Please
     *                        see the member description for an example.
     * @param startTime       The daylight savings starting time. Please see the
     *                        member description for an example.
     * @param endMonth        The daylight savings ending month. Month is
     *                        0-based. eg, 0 for January.
     * @param endDay          The daylight savings ending day-of-week-in-month.
     *                        Please see the member description for an example.
     * @param endDayOfWeek    The daylight savings ending day-of-week. Please
     *                        see the member description for an example.
     * @param endTime         The daylight savings ending time. Please see the
     *                        member description for an example.
     * @exception IllegalArgumentException the month, day, dayOfWeek, or time
     * parameters are out of range for the start or end rule
     */
    public SimpleTimeZone(int rawOffset, String ID,
                          int startMonth, int startDay, int startDayOfWeek, int startTime,
                          int endMonth, int endDay, int endDayOfWeek, int endTime)
    {
        this(rawOffset, ID,
             startMonth, startDay, startDayOfWeek, startTime,
             endMonth, endDay, endDayOfWeek, endTime,
             millisPerHour);
    }

    /**
     * Constructor.  This constructor is package private at this point.  It will
     * be made public at the next API change.  It is identical to the 10-argument
     * constructor, but also takes a dstSavings parameter.
     * @param dstSavings   The amount of time in ms saved during DST.
     * @exception IllegalArgumentException the month, day, dayOfWeek, or time
     * parameters are out of range for the start or end rule
     */
    SimpleTimeZone(int rawOffset, String ID,
                   int startMonth, int startDay, int startDayOfWeek, int startTime,
                   int endMonth, int endDay, int endDayOfWeek, int endTime,
                   int dstSavings)
    {
        setID(ID);
        this.rawOffset      = rawOffset;
        this.startMonth     = startMonth;
        this.startDay       = startDay;
        this.startDayOfWeek = startDayOfWeek;
        this.startTime      = startTime;
        this.endMonth       = endMonth;
        this.endDay         = endDay;
        this.endDayOfWeek   = endDayOfWeek;
        this.endTime        = endTime;
        this.dstSavings     = dstSavings;
        // this.useDaylight    = true; // Set by decodeRules
        decodeRules();
        if (dstSavings <= 0) {
            throw new IllegalArgumentException("Illegal DST savings");
        }
    }

    /**
     * Sets the daylight savings starting year.
     *
     * @param year  The daylight savings starting year.
     */
    public void setStartYear(int year)
    {
        startYear = year;
    }

    /**
     * Sets the daylight savings starting rule. For example, Daylight Savings
     * Time starts at the first Sunday in April, at 2 AM in standard time.
     * Therefore, you can set the start rule by calling:
     * setStartRule(TimeFields.APRIL, 1, TimeFields.SUNDAY, 2*60*60*1000);
     *
     * @param month             The daylight savings starting month. Month is
     *                          0-based. eg, 0 for January.
     * @param dayOfWeekInMonth  The daylight savings starting
     *                          day-of-week-in-month. Please see the member
     *                          description for an example.
     * @param dayOfWeek         The daylight savings starting day-of-week.
     *                          Please see the member description for an
     *                          example.
     * @param time              The daylight savings starting time. Please see
     *                          the member description for an example.
     * @exception IllegalArgumentException the month, dayOfWeekInMonth,
     * dayOfWeek, or time parameters are out of range
     */
    public void setStartRule(int month, int dayOfWeekInMonth, int dayOfWeek,
                             int time)
    {
        startMonth = month;
        startDay = dayOfWeekInMonth;
        startDayOfWeek = dayOfWeek;
        startTime = time;
        // useDaylight = true; // Set by decodeRules
        decodeStartRule();
    }

    /**
     * Sets the daylight savings ending rule. For example, Daylight Savings Time
     * ends at the last (-1) Sunday in October, at 2 AM in standard time.
     * Therefore, you can set the end rule by calling:
     * setEndRule(TimeFields.OCTOBER, -1, TimeFields.SUNDAY, 2*60*60*1000);
     *
     * @param month             The daylight savings ending month. Month is
     *                          0-based. eg, 0 for January.
     * @param dayOfWeekInMonth  The daylight savings ending
     *                          day-of-week-in-month. Please see the member
     *                          description for an example.
     * @param dayOfWeek         The daylight savings ending day-of-week. Please
     *                          see the member description for an example.
     * @param time              The daylight savings ending time. Please see the
     *                          member description for an example.
     * @exception IllegalArgumentException the month, dayOfWeekInMonth,
     * dayOfWeek, or time parameters are out of range
     */
    public void setEndRule(int month, int dayOfWeekInMonth, int dayOfWeek,
                           int time)
    {
        endMonth = month;
        endDay = dayOfWeekInMonth;
        endDayOfWeek = dayOfWeek;
        endTime = time;
        // useDaylight = true; // Set by decodeRules
        decodeEndRule();
    }

    /**
     * Overrides TimeZone
     * Gets offset, for current date, modified in case of
     * daylight savings. This is the offset to add *to* UTC to get local time.
     * Gets the time zone offset, for current date, modified in case of daylight
     * savings. This is the offset to add *to* UTC to get local time. Assume
     * that the start and end month are distinct. This method may return incorrect
     * results for rules that start at the end of February (e.g., last Sunday in
     * February) or the beginning of March (e.g., March 1).
     *
     * @param era           The era of the given date.
     * @param year          The year in the given date.
     * @param month         The month in the given date. Month is 0-based. e.g.,
     *                      0 for January.
     * @param day           The day-in-month of the given date.
     * @param dayOfWeek     The day-of-week of the given date.
     * @param millis        The milliseconds in day in <em>standard</em> local time.
     * @return              The offset to add *to* GMT to get local time.
     * @exception IllegalArgumentException the era, month, day,
     * dayOfWeek, or millis parameters are out of range
     */
    public int getOffset(int era, int year, int month, int day, int dayOfWeek,
                         int millis)
    {
        if (true) {
            /* Use this parameter checking code for normal operation.  Only one
             * of these two blocks should actually get compiled into the class
             * file.  */
            if ((era != GregorianCalendar.AD && era != GregorianCalendar.BC)
                || month < Calendar.JANUARY
                || month > Calendar.DECEMBER
                || day < 1
                || day > maxMonthLength[month]
                || dayOfWeek < Calendar.SUNDAY
                || dayOfWeek > Calendar.SATURDAY
                || millis < 0
                || millis >= millisPerDay) {
                throw new IllegalArgumentException();
            }
        } else {
            /* This parameter checking code is better for debugging, but
             * overkill for normal operation.  Only one of these two blocks
             * should actually get compiled into the class file.  */
            if (era != GregorianCalendar.AD && era != GregorianCalendar.BC) {
                throw new IllegalArgumentException("Illegal era " + era);
            }
            if (month < Calendar.JANUARY
                || month > Calendar.DECEMBER) {
                throw new IllegalArgumentException("Illegal month " + month);
            }
            if (day < 1
                || day > maxMonthLength[month]) {
                throw new IllegalArgumentException("Illegal day " + day);
            }
            if (dayOfWeek < Calendar.SUNDAY
                || dayOfWeek > Calendar.SATURDAY) {
                throw new IllegalArgumentException("Illegal day of week " + dayOfWeek);
            }
            if (millis < 0
                || millis >= millisPerDay) {
                throw new IllegalArgumentException("Illegal millis " + millis);
            }
        }

        int result = rawOffset;

        // Bail out if we are before the onset of daylight savings time
        if (!useDaylight || year < startYear || era != GregorianCalendar.AD) return result;

        // Check for southern hemisphere.  We assume that the start and end
        // month are different.
        boolean southern = (startMonth > endMonth);

        // Compare the date to the starting and ending rules.  For the ending
        // rule comparison, we add the dstSavings to the millis passed in to convert
        // them from standard to wall time.  +1 = date>rule, -1 = date<rule, 0 =
        // date==rule.
        int startCompare = compareToRule(month, day, dayOfWeek, millis,
                                         startMode, startMonth, startDayOfWeek,
                                         startDay, startTime);
        int endCompare = compareToRule(month, day, dayOfWeek, millis + dstSavings,
                                       endMode, endMonth, endDayOfWeek,
                                       endDay, endTime);

        // Check for both the northern and southern hemisphere cases.  We
        // assume that in the northern hemisphere, the start rule is before the
        // end rule within the calendar year, and vice versa for the southern
        // hemisphere.
        if ((!southern && (startCompare >= 0 && endCompare < 0)) ||
            (southern && (startCompare >= 0 || endCompare < 0)))
            result += dstSavings;

        return result;
    }

    /**
     * Compare a given date in the year to a rule. Return 1, 0, or -1, depending
     * on whether the date is after, equal to, or before the rule date. The
     * millis are compared directly against the ruleMillis, so any
     * standard-daylight adjustments must be handled by the caller. Assume that
     * no rule references the end of February (e.g., last Sunday in February).
     *
     * @return  1 if the date is after the rule date, -1 if the date is before
     *          the rule date, or 0 if the date is equal to the rule date.
     */
    private static int compareToRule(int month, int dayOfMonth, int dayOfWeek, int millis,
                                     int ruleMode, int ruleMonth, int ruleDayOfWeek,
                                     int ruleDay, int ruleMillis)
    {
        if (month < ruleMonth) return -1;
        else if (month > ruleMonth) return 1;

        int ruleDayOfMonth = 0;
        switch (ruleMode)
        {
        case DOM_MODE:
            ruleDayOfMonth = ruleDay;
            break;
        case DOW_IN_MONTH_MODE:
            // In this case ruleDay is the day-of-week-in-month
            if (ruleDay > 0)
                ruleDayOfMonth = 1 + (ruleDay - 1) * 7 +
                    (7 + ruleDayOfWeek - (dayOfWeek - dayOfMonth + 1)) % 7;
            else // Assume ruleDay < 0 here
            {
                int monthLen = staticMonthLength[month];

                ruleDayOfMonth = monthLen + (ruleDay + 1) * 7 -
                    (7 + (dayOfWeek + monthLen - dayOfMonth) - ruleDayOfWeek) % 7;
            }
            break;
        case DOW_GE_DOM_MODE:
            ruleDayOfMonth = ruleDay +
                (49 + ruleDayOfWeek - ruleDay - dayOfWeek + dayOfMonth) % 7;
            break;
        case DOW_LE_DOM_MODE:
            ruleDayOfMonth = ruleDay -
                (49 - ruleDayOfWeek + ruleDay + dayOfWeek - dayOfMonth) % 7;
            // Note at this point ruleDayOfMonth may be <1, although it will
            // be >=1 for well-formed rules.
            break;
        }

        if (dayOfMonth < ruleDayOfMonth) return -1;
        else if (dayOfMonth > ruleDayOfMonth) return 1;

        if (millis < ruleMillis) return -1;
        else if (millis > ruleMillis) return 1;
        else return 0;
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
    public boolean inDaylightTime(Date date)
    {
        GregorianCalendar gc = new GregorianCalendar(this);
        gc.setTime(date);
        return gc.inDaylightTime();
    }

    /**
     * Overrides Cloneable
     */
    public Object clone()
    {
        return super.clone();
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
     *
     * @param obj  The SimpleTimeZone object to be compared with.
     * @return     True if the given obj is the same as this SimpleTimeZone
     *             object; false otherwise.
     */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (!(obj instanceof SimpleTimeZone))
            return false;

        SimpleTimeZone that = (SimpleTimeZone) obj;

        return this.getID().equals(that.getID()) &&
            rawOffset == that.rawOffset &&
            useDaylight == that.useDaylight &&
            dstSavings == that.dstSavings &&
            startMode == that.startMode &&
            startMonth == that.startMonth &&
            startDay == that.startDay &&
            startDayOfWeek == that.startDayOfWeek &&
            startTime == that.startTime &&
            endMode == that.endMode &&
            endMonth == that.endMonth &&
            endDay == that.endDay &&
            endDayOfWeek == that.endDayOfWeek &&
            endTime == that.endTime &&
            startYear == that.startYear;
    }

    /**
     * Return a string representation of this time zone.
     * @return  a string representation of this time zone.
     */
    public String toString() {
        return getClass().getName() +
            "[id=" + getID() +
            ",offset=" + rawOffset +
            ",dstSavings=" + dstSavings +
            ",useDaylight=" + useDaylight +
            ",startYear=" + startYear +
            ",startMode=" + startMode +
            ",startMonth=" + startMonth +
            ",startDay=" + startDay +
            ",startDayOfWeek=" + startDayOfWeek +
            ",startTime=" + startTime +
            ",endMode=" + endMode +
            ",endMonth=" + endMonth +
            ",endDay=" + endDay +
            ",endDayOfWeek=" + endDayOfWeek +
            ",endTime=" + endTime + ']';
    }

    // =======================privates===============================

    private int startMonth, startDay, startDayOfWeek, startTime;
    private int endMonth, endDay, endDayOfWeek, endTime;
    private int startYear;
    private int rawOffset;
    private boolean useDaylight=false; // indicate if this time zone uses DST
    private static final int millisPerHour = 60*60*1000;
    private static final int millisPerDay  = 24*millisPerHour;
    // WARNING: assumes that no rule is measured from the end of February,
    // since we don't handle leap years. Could handle assuming always
    // Gregorian, since we know they didn't have daylight time when
    // Gregorian calendar started.
    // monthLength was non-static in JDK 1.1, so we have to keep it that way
    // to maintain serialization compatibility. However, there's no need to
    // recreate the array each time we create a new time zone.
    private final byte monthLength[] = staticMonthLength;
    private final static byte staticMonthLength[] = {31,28,31,30,31,30,31,31,30,31,30,31};

    /**
     * maxMonthLength is used only for range checking.
     */
    private final static byte maxMonthLength[] = {31,29,31,30,31,30,31,31,30,31,30,31};

    /** 
     * Variables specifying the mode of the start and end rules.
     */
    private int startMode, endMode; // fields new in JDK 1.1.4

    /**
     * A positive value indicating the amount of time saved during DST in ms.
     * Typically one hour; sometimes 30 minutes.
     */
    private int dstSavings; // field new in JDK 1.1.4

    /** 
     * Constants specifying values of startMode and endMode.
     */
    private static final int DOM_MODE          = 1; // Exact day of month, "Mar 1"
    private static final int DOW_IN_MONTH_MODE = 2; // Day of week in month, "lastSun"
    private static final int DOW_GE_DOM_MODE   = 3; // Day of week after day of month, "Sun>=15"
    private static final int DOW_LE_DOM_MODE   = 4; // Day of week before day of month, "Sun<=21"

    // Proclaim compatibility with 1.1
    static final long serialVersionUID = -403250971215465050L;

    // the internal serial version which says which version was written
    // - 0 (default) for version up to JDK 1.1.3
    // - 1 for version from JDK 1.1.4, which includes 3 new fields
    static final int currentSerialVersion = 1;
    private int serialVersionOnStream = currentSerialVersion;

    //----------------------------------------------------------------------
    // Rule representation
    //
    // We represent the following flavors of rules:
    //       5        the fifth of the month
    //       lastSun  the last Sunday in the month
    //       lastMon  the last Monday in the month
    //       Sun>=8   first Sunday on or after the eighth
    //       Sun<=25  last Sunday on or before the 25th
    // This is further complicated by the fact that we need to remain
    // backward compatible with the 1.1 FCS.  Finally, we need to minimize
    // API changes.  In order to satisfy these requirements, we support
    // three representation systems, and we translate between them.
    //
    // INTERNAL REPRESENTATION
    // This is the format SimpleTimeZone objects take after construction or
    // streaming in is complete.  Rules are represented directly, using an
    // unencoded format.  We will discuss the start rule only below; the end
    // rule is analogous.
    //   startMode      Takes on enumerated values DAY_OF_MONTH,
    //                  DOW_IN_MONTH, DOW_AFTER_DOM, or DOW_BEFORE_DOM.
    //   startDay       The day of the month, or for DOW_IN_MONTH mode, a
    //                  value indicating which DOW, such as +1 for first,
    //                  +2 for second, -1 for last, etc.
    //   startDayOfWeek The day of the week.  Ignored for DAY_OF_MONTH.
    //
    // ENCODED REPRESENTATION
    // This is the format accepted by the constructor and by setStartRule()
    // and setEndRule().  It uses various combinations of positive, negative,
    // and zero values to encode the different rules.  This representation
    // allows us to specify all the different rule flavors without altering
    // the API.
    //   MODE              startMonth    startDay    startDayOfWeek
    //   DOW_IN_MONTH_MODE >=0           !=0         >0
    //   DOM_MODE          >=0           >0          ==0
    //   DOW_GE_DOM_MODE   >=0           >0          <0
    //   DOW_LE_DOM_MODE   >=0           <0          <0
    //   (no DST)          don't care    ==0         don't care
    //
    // STREAMED REPRESENTATION
    // We must retain binary compatibility with the 1.1 FCS.  The 1.1 code only
    // handles DOW_IN_MONTH_MODE and non-DST mode, the latter indicated by the
    // flag useDaylight.  When we stream an object out, we translate into an
    // approximate DOW_IN_MONTH_MODE representation so the object can be parsed
    // and used by 1.1 code.  Following that, we write out the full
    // representation separately so that contemporary code can recognize and
    // parse it.  The full representation is written in a "packed" format,
    // consisting of a version number, a length, and an array of bytes.  Future
    // versions of this class may specify different versions.  If they wish to
    // include additional data, they should do so by storing them after the
    // packed representation below.
    //----------------------------------------------------------------------

    /**
     * Given a set of encoded rules in startDay and startDayOfMonth, decode
     * them and set the startMode appropriately.  Do the same for endDay and
     * endDayOfMonth.  Upon entry, the day of week variables may be zero or
     * negative, in order to indicate special modes.  The day of month
     * variables may also be negative.  Upon exit, the mode variables will be
     * set, and the day of week and day of month variables will be positive.
     * This method also recognizes a startDay or endDay of zero as indicating
     * no DST.
     */
    private void decodeRules()
    {
        decodeStartRule();
        decodeEndRule();
    }
    
    /**
     * Decode the start rule and validate the parameters.  The parameters are
     * expected to be in encoded form, which represents the various rule modes
     * by negating or zeroing certain values.  Representation formats are:
     * <p>
     * <pre>
     *            DOW_IN_MONTH  DOM    DOW>=DOM  DOW<=DOM  no DST
     *            ------------  -----  --------  --------  ----------
     * month       0..11        same    same      same     don't care
     * day        -5..5         1..31   1..31    -1..-31   0
     * dayOfWeek   1..7         0      -1..-7    -1..-7    don't care
     * time        0..ONEDAY    same    same      same     don't care
     * </pre>
     * The range for month does not include UNDECIMBER since this class is
     * really specific to GregorianCalendar, which does not use that month.
     * The range for time includes ONEDAY (vs. ending at ONEDAY-1) because the
     * end rule is an exclusive limit point.  That is, the range of times that
     * are in DST include those >= the start and < the end.  For this reason,
     * it should be possible to specify an end of ONEDAY in order to include the
     * entire day.  Although this is equivalent to time 0 of the following day,
     * it's not always possible to specify that, for example, on December 31.
     * While arguably the start range should still be 0..ONEDAY-1, we keep
     * the start and end ranges the same for consistency.
     */
    private void decodeStartRule() {
        useDaylight = (startDay != 0) && (endDay != 0);
        if (startDay != 0) {
            if (startMonth < Calendar.JANUARY || startMonth > Calendar.DECEMBER) {
                throw new IllegalArgumentException(
                        "Illegal start month " + startMonth);
            }
            if (startTime < 0 || startTime > millisPerDay) {
                throw new IllegalArgumentException(
                        "Illegal start time " + startTime);
            }
            if (startDayOfWeek == 0) {
                startMode = DOM_MODE;
            } else {
                if (startDayOfWeek > 0) {
                    startMode = DOW_IN_MONTH_MODE;
                } else {
                    startDayOfWeek = -startDayOfWeek;
                    if (startDay > 0) {
                        startMode = DOW_GE_DOM_MODE;
                    } else {
                        startDay = -startDay;
                        startMode = DOW_LE_DOM_MODE;
                    }
                }
                if (startDayOfWeek > Calendar.SATURDAY) {
                    throw new IllegalArgumentException(
                           "Illegal start day of week " + startDayOfWeek);
                }
            }
            if (startMode == DOW_IN_MONTH_MODE) {
                if (startDay < -5 || startDay > 5) {
                    throw new IllegalArgumentException(
                            "Illegal start day of week in month " + startDay);
                }
            } else if (startDay > staticMonthLength[startMonth]) {
                throw new IllegalArgumentException(
                        "Illegal start day " + startDay);
            }
        }
    }

    /**
     * Decode the end rule and validate the parameters.  This method is exactly
     * analogous to decodeStartRule().
     * @see decodeStartRule
     */
    private void decodeEndRule() {
        useDaylight = (startDay != 0) && (endDay != 0);
        if (endDay != 0) {
            if (endMonth < Calendar.JANUARY || endMonth > Calendar.DECEMBER) {
                throw new IllegalArgumentException(
                        "Illegal end month " + endMonth);
            }
            if (endTime < 0 || endTime > millisPerDay) {
                throw new IllegalArgumentException(
                        "Illegal end time " + endTime);
            }
            if (endDayOfWeek == 0) {
                endMode = DOM_MODE;
            } else {
                if (endDayOfWeek > 0) {
                    endMode = DOW_IN_MONTH_MODE;
                } else {
                    endDayOfWeek = -endDayOfWeek;
                    if (endDay > 0) {
                        endMode = DOW_GE_DOM_MODE;
                    } else {
                        endDay = -endDay;
                        endMode = DOW_LE_DOM_MODE;
                    }
                }
                if (endDayOfWeek > Calendar.SATURDAY) {
                    throw new IllegalArgumentException(
                           "Illegal end day of week " + endDayOfWeek);
                }
            }
            if (endMode == DOW_IN_MONTH_MODE) {
                if (endDay < -5 || endDay > 5) {
                    throw new IllegalArgumentException(
                            "Illegal end day of week in month " + endDay);
                }
            } else if (endDay > staticMonthLength[endMonth]) {
                throw new IllegalArgumentException(
                        "Illegal end day " + endDay);
            }
        }
    }

    /** 
     * Make rules compatible to 1.1 FCS code.  Since 1.1 FCS code only understands
     * day-of-week-in-month rules, we must modify other modes of rules to their
     * approximate equivalent in 1.1 FCS terms.  This method is used when streaming
     * out objects of this class.  After it is called, the rules will be modified,
     * with a possible loss of information.  startMode and endMode will NOT be
     * altered, even though semantically they should be set to DOW_IN_MONTH_MODE,
     * since the rule modification is only intended to be temporary.
     */
    private void makeRulesCompatible()
    {
        switch (startMode)
        {
        case DOM_MODE:
            startDay = 1 + (startDay / 7);
            startDayOfWeek = Calendar.SUNDAY;
            break;
        case DOW_GE_DOM_MODE:
            // A day-of-month of 1 is equivalent to DOW_IN_MONTH_MODE
            // that is, Sun>=1 == firstSun.
            if (startDay != 1)
                startDay = 1 + (startDay / 7);
            break;
        case DOW_LE_DOM_MODE:
            if (startDay >= 30)
                startDay = -1;
            else
                startDay = 1 + (startDay / 7);
            break;
        }

        switch (endMode)
        {
        case DOM_MODE:
            endDay = 1 + (endDay / 7);
            endDayOfWeek = Calendar.SUNDAY;
            break;
        case DOW_GE_DOM_MODE:
            // A day-of-month of 1 is equivalent to DOW_IN_MONTH_MODE
            // that is, Sun>=1 == firstSun.
            if (endDay != 1)
                endDay = 1 + (endDay / 7);
            break;
        case DOW_LE_DOM_MODE:
            if (endDay >= 30)
                endDay = -1;
            else
                endDay = 1 + (endDay / 7);
            break;
        }
    }

    /**
     * Pack the start and end rules into an array of bytes.  Only pack
     * data which is not preserved by makeRulesCompatible.
     */
    private byte[] packRules()
    {
        byte[] rules = new byte[4];
        rules[0] = (byte)startDay;
        rules[1] = (byte)startDayOfWeek;
        rules[2] = (byte)endDay;
        rules[3] = (byte)endDayOfWeek;
        return rules;
    }

    /**
     * Given an array of bytes produced by packRules, interpret them
     * as the start and end rules.
     */
    private void unpackRules(byte[] rules)
    {
        startDay       = rules[0];
        startDayOfWeek = rules[1];
        endDay         = rules[2];
        endDayOfWeek   = rules[3];
    }

    /**
     * Write object out to a serialization stream.  Note that we write out two
     * formats, a 1.1 FCS-compatible format, using DOW_IN_MONTH_MODE rules,
     * in the "required" section, followed by the full rules, in packed format,
     * in the "optional" section.  The optional section will be ignored by 1.1
     * FCS code upon stream in.
     */
    private void writeObject(ObjectOutputStream stream)
         throws IOException
    {
        // Construct a binary rule
        byte[] rules = packRules();

        // Convert to 1.1 FCS rules.  This step may cause us to lose information.
        makeRulesCompatible();

        // Write out the 1.1 FCS rules
        stream.defaultWriteObject();

        // Write out the binary rules in the optional data area of the stream.
        stream.writeInt(rules.length);
        stream.write(rules);

        // Recover the original rules.  This recovers the information lost
        // by makeRulesCompatible.
        unpackRules(rules);
    }

    /**
     * Read this object out to a serialization stream.  We handle both 1.1 FCS
     * binary formats, and full formats with a packed byte array.
     */
    private void readObject(ObjectInputStream stream)
         throws IOException, ClassNotFoundException
    {
        stream.defaultReadObject();

        if (serialVersionOnStream < 1)
        {
            // Fix a bug in the 1.1 SimpleTimeZone code -- namely,
            // startDayOfWeek and endDayOfWeek were usually uninitialized.  We can't do
            // too much, so we assume SUNDAY, which actually works most of the time.
            if (startDayOfWeek == 0) startDayOfWeek = Calendar.SUNDAY;
            if (endDayOfWeek == 0) endDayOfWeek = Calendar.SUNDAY;

            // The variables dstSavings, startMode, and endMode are post-1.1, so they
            // won't be present if we're reading from a 1.1 stream.  Fix them up.
            startMode = endMode = DOW_IN_MONTH_MODE;
            dstSavings = millisPerHour;
        }
        else
        {
            // For 1.1.4, in addition to the 3 new instance variables, we also
            // store the actual rules (which have not be made compatible with 1.1)
            // in the optional area.  Read them in here and parse them.
            int length = stream.readInt();
            byte[] rules = new byte[length];
            stream.readFully(rules);
            unpackRules(rules);
        }

        serialVersionOnStream = currentSerialVersion;
    }
}

//eof
