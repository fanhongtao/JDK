/*
 * @(#)Calendar.java	1.36 00/02/10
 *
 * (C) Copyright Taligent, Inc. 1996-1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996-1997 - All Rights Reserved
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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
 * <strong>Inconsistent information.</strong> If fields conflict, the calendar
 * will give preference to fields set more recently. For example, when
 * determining the day, the calendar will look for one of the following
 * combinations of fields.  The most recent combination, as determined by the
 * most recently set single field, will be used.
 *
 * <blockquote>
 * <pre>
 * MONTH + DAY_OF_MONTH
 * MONTH + WEEK_OF_MONTH + DAY_OF_WEEK
 * MONTH + DAY_OF_WEEK_IN_MONTH + DAY_OF_WEEK
 * DAY_OF_YEAR
 * DAY_OF_WEEK + WEEK_OF_YEAR
 * </pre>
 * </blockquote>
 *
 * For the time of day:
 *
 * <blockquote>
 * <pre>
 * HOUR_OF_DAY
 * AM_PM + HOUR
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
 * <p><strong>Field manipulation methods</strong></p>
 * 
 * <p><code>Calendar</code> fields can be changed using three methods:
 * <code>set()</code>, <code>add()</code>, and <code>roll()</code>.</p>
 * 
 * <p><strong><code>set(f, value)</code></strong> changes field
 * <code>f</code> to <code>value</code>.  In addition, it sets an
 * internal member variable to indicate that field <code>f</code> has
 * been changed. Although field <code>f</code> is changed immediately,
 * the calendar's milliseconds is not recomputed until the next call to
 * <code>get()</code>, <code>getTime()</code>, or
 * <code>getTimeInMillis()</code> is made. Thus, multiple calls to
 * <code>set()</code> do not trigger multiple, unnecessary
 * computations. As a result of changing a field using
 * <code>set()</code>, other fields may also change, depending on the
 * field, the field value, and the calendar system. In addition,
 * <code>get(f)</code> will not necessarily return <code>value</code>
 * after the fields have been recomputed. The specifics are determined by
 * the concrete calendar class.</p>
 * 
 * <p><em>Example</em>: Consider a <code>GregorianCalendar</code>
 * originally set to August 31, 1999. Calling <code>set(Calendar.MONTH,
 * Calendar.SEPTEMBER)</code> sets the calendar to September 31,
 * 1999. This is a temporary internal representation that resolves to
 * October 1, 1999 if <code>getTime()</code>is then called. However, a
 * call to <code>set(Calendar.DAY_OF_MONTH, 30)</code> before the call to
 * <code>getTime()</code> sets the calendar to September 30, 1999, since
 * no recomputation occurs after <code>set()</code> itself.</p>
 * 
 * <p><strong><code>add(f, delta)</code></strong> adds <code>delta</code>
 * to field <code>f</code>.  This is equivalent to calling <code>set(f,
 * get(f) + delta)</code> with two adjustments:</p>
 * 
 * <blockquote>
 *   <p><strong>Add rule 1</strong>. The value of field <code>f</code>
 *   after the call minus the value of field <code>f</code> before the
 *   call is <code>delta</code>, modulo any overflow that has occurred in
 *   field <code>f</code>. Overflow occurs when a field value exceeds its
 *   range and, as a result, the next larger field is incremented or
 *   decremented and the field value is adjusted back into its range.</p>
 * 
 *   <p><strong>Add rule 2</strong>. If a smaller field is expected to be
 *   invariant, but &nbsp; it is impossible for it to be equal to its
 *   prior value because of changes in its minimum or maximum after field
 *   <code>f</code> is changed, then its value is adjusted to be as close
 *   as possible to its expected value. A smaller field represents a
 *   smaller unit of time. <code>HOUR</code> is a smaller field than
 *   <code>DAY_OF_MONTH</code>. No adjustment is made to smaller fields
 *   that are not expected to be invariant. The calendar system
 *   determines what fields are expected to be invariant.</p>
 * </blockquote>
 * 
 * <p>In addition, unlike <code>set()</code>, <code>add()</code> forces
 * an immediate recomputation of the calendar's milliseconds and all
 * fields.</p>
 * 
 * <p><em>Example</em>: Consider a <code>GregorianCalendar</code>
 * originally set to August 31, 1999. Calling <code>add(Calendar.MONTH,
 * 13)</code> sets the calendar to September 30, 2000. <strong>Add rule
 * 1</strong> sets the <code>MONTH</code> field to September, since
 * adding 13 months to August gives September of the next year. Since
 * <code>DAY_OF_MONTH</code> cannot be 31 in September in a
 * <code>GregorianCalendar</code>, <strong>add rule 2</strong> sets the
 * <code>DAY_OF_MONTH</code> to 30, the closest possible value. Although
 * it is a smaller field, <code>DAY_OF_WEEK</code> is not adjusted by
 * rule 2, since it is expected to change when the month changes in a
 * <code>GregorianCalendar</code>.</p>
 * 
 * <p><strong><code>roll(f, up/down)</code></strong> adds
 * <code>+1/-1</code> to field <code>f</code> without changing larger
 * fields. This is equivalent to calling <code>add(f, +1/-1)</code> with
 * the following adjustment:</p>
 * 
 * <blockquote>
 *   <p><strong>Roll rule</strong>. Larger fields are unchanged after the
 *   call. A larger field represents a larger unit of
 *   time. <code>DAY_OF_MONTH</code> is a larger field than
 *   <code>HOUR</code>.</p>
 * </blockquote>
 * 
 * <p><em>Example</em>: Consider a <code>GregorianCalendar</code> originally
 * set to October 31, 1999. Calling <code>roll(Calendar.MONTH, true)</code>
 * sets the calendar to November 30, 1999. Add
 * rule 1 sets the <code>MONTH</code> field to November. Using a
 * <code>GregorianCalendar</code>, the <code>DAY_OF_MONTH</code> cannot
 * be 31 in the month November. Add rule 2 sets it to the closest possible
 * value, 30. Calling <code>roll(Calendar.MONTH, true)</code> two more
 * times sets the calendar further to January 30, <strong>1999</strong>.
 * When rolling from December to January, the roll rule maintains the
 * <code>YEAR</code> field value of 1999.</p>
 * 
 * <p><em>Example</em>: Consider a <code>GregorianCalendar</code>
 * originally set to Sunday June 6, 1999. Calling
 * <code>roll(Calendar.WEEK_OF_MONTH, false)</code> sets the calendar to
 * Tuesday June 1, 1999, whereas calling
 * <code>add(Calendar.WEEK_OF_MONTH, -1)</code> sets the calendar to
 * Sunday May 30, 1999. This is because the roll rule imposes an
 * additional constraint: The <code>MONTH</code> must not change when the
 * <code>WEEK_OF_MONTH</code> is rolled. Taken together with add rule 1,
 * the resultant date must be between Tuesday June 1 and Saturday June
 * 5. According to add rule 2, the <code>DAY_OF_WEEK</code>, an invariant
 * when changing the <code>WEEK_OF_MONTH</code>, is set to Tuesday, the
 * closest possible value to Sunday (where Sunday is the first day of the
 * week).</p>
 * 
 * <p><strong>Usage model</strong>. To motivate the behavior of
 * <code>add()</code> and <code>roll()</code>, consider a user interface
 * component with increment and decrement buttons for the month, day, and
 * year, and an underlying <code>GregorianCalendar</code>. If the
 * interface reads January 31, 1999 and the user presses the month
 * increment button, what should it read? If the underlying
 * implementation uses <code>set()</code>, it might read March 3, 1999.
 * A better result would be February 28, 1999.  If the underlying
 * implementation uses <code>add(Calendar.MONTH, 1)</code>,
 * it will read February 28, 1999.  Furthermore, if the user presses
 * the month increment button again, it should read March 31, 1999,
 * not March 28, 1999. By saving the original date and using
 * <code>add(Calendar.MONTH, 2)</code>, after the user presses the month
 * increment button twice, it will read March 31, 1999 as most users
 * will intuitively expect.  In contrast, since <code>roll()</code> can
 * only go up or down by one, calling <code>roll(Calendar.MONTH, true)</code>
 * twice will result in March 28, 1999. In addition, <code>roll()</code>
 * does not change the YEAR field.</p>
 *
 * @see          Date
 * @see          GregorianCalendar
 * @see          TimeZone
 * @see          java.text.DateFormat
 * @version      1.17 06 Jan 1997
 * @author Mark Davis, David Goldsmith, Chen-Lieh Huang, Alan Liu
 */
public abstract class Calendar implements Serializable, Cloneable {

    // Data flow in Calendar
    // ---------------------

    // The current time is represented in two ways by Calendar: as UTC
    // milliseconds from the epoch start (1 January 1970 0:00 UTC), and as local
    // fields such as MONTH, HOUR, AM_PM, etc.  It is possible to compute the
    // millis from the fields, and vice versa.  The data needed to do this
    // conversion is encapsulated by a TimeZone object owned by the Calendar.
    // The data provided by the TimeZone object may also be overridden if the
    // user sets the ZONE_OFFSET and/or DST_OFFSET fields directly. The class
    // keeps track of what information was most recently set by the caller, and
    // uses that to compute any other information as needed.

    // If the user sets the fields using set(), the data flow is as follows.
    // This is implemented by the Calendar subclass's computeTime() method.
    // During this process, certain fields may be ignored.  The disambiguation
    // algorithm for resolving which fields to pay attention to is described
    // above.

    //   local fields (YEAR, MONTH, DATE, HOUR, MINUTE, etc.)
    //           |
    //           | Using Calendar-specific algorithm
    //           V
    //   local standard millis
    //           |
    //           | Using TimeZone or user-set ZONE_OFFSET / DST_OFFSET
    //           V
    //   UTC millis (in time data member)

    // If the user sets the UTC millis using setTime(), the data flow is as
    // follows.  This is implemented by the Calendar subclass's computeFields()
    // method.

    //   UTC millis (in time data member)
    //           |
    //           | Using TimeZone getOffset()
    //           V
    //   local standard millis
    //           |
    //           | Using Calendar-specific algorithm
    //           V
    //   local fields (YEAR, MONTH, DATE, HOUR, MINUTE, etc.)

    // In general, a round trip from fields, through local and UTC millis, and
    // back out to fields is made when necessary.  This is implemented by the
    // complete() method.  Resolving a partial set of fields into a UTC millis
    // value allows all remaining fields to be generated from that value.  If
    // the Calendar is lenient, the fields are also renormalized to standard
    // ranges when they are regenerated.

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
     * This is a synonym for DAY_OF_MONTH.
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

    // Internal notes:
    // Calendar contains two kinds of time representations: current "time" in
    // milliseconds, and a set of time "fields" representing the current time.
    // The two representations are usually in sync, but can get out of sync
    // as follows.
    // 1. Initially, no fields are set, and the time is invalid.
    // 2. If the time is set, all fields are computed and in sync.
    // 3. If a single field is set, the time is invalid.
    // Recomputation of the time and fields happens when the object needs
    // to return a result to the user, or use a result for a computation.

    /**
     * The time fields containing values into which the millis is computed.
     */
    protected int           fields[]; // NOTE: Make transient when possible

    /**
     * The flags which tell if a specified time field for the calendar is set.
     * A new object has no fields set.  After the first call to a method
     * which generates the fields, they all remain set after that.
     */
    protected boolean       isSet[]; // NOTE: Remove when possible

    /**
     * Pseudo-time-stamps which specify when each field was set. There
     * are two special values, UNSET and INTERNALLY_SET. Values from
     * MINIMUM_USER_SET to Integer.MAX_VALUE are legal user set values.
     */
    transient int           stamp[];

    /**
     * The current time set for the calendar.
     */
    protected long          time;

    /**
     * The flag which indicates if the current time is set for the calendar.
     * The time is made invalid by the user setting an individual field.
     */
    protected boolean       isTimeSet; // NOTE: Make transient when possible

    /**
     * True if the fields are in sync with the currently set time of this Calendar.
     * If false, then the next attempt to get the value of a field will
     * force a recomputation of all fields from the current value of the time
     * field.
     *
     * This should really be named areFieldsInSync, but the old name is retained
     * for backward compatibility.
     */
    protected boolean       areFieldsSet; // NOTE: Make transient when possible

    /**
     * True if all fields have been set.
     *
     * NOTE: MAKE PROTECTED AT NEXT API CHANGE, or ADD ACCESSOR METHODS.
     */
    transient boolean       areAllFieldsSet;

    /**
     * @see #setLenient
     */
    private boolean         lenient = true;

    /**
     * Time zone affects the time calculation done by Calendar. Calendar uses
     * the time zone data to produce the local time. Both firstDayOfWeek
     * and minimalDaysInFirstWeek are locale-dependent. For example,
     * in US locale, firstDayOfWeek is SUNDAY; minimalDaysInFirstWeek is 1.
     * They are used to figure out the week count for a specific date for
     * a given locale. These must be set when a Calendar is constructed.
     */
    private TimeZone        zone;
    private int             firstDayOfWeek;
    private int             minimalDaysInFirstWeek;

    /**
     * Cache to hold the firstDayOfWeek and minimalDaysInFirstWeek
     * of a Locale.
     */
    private static Hashtable cachedLocaleData = new Hashtable(3);

    // Special values of stamp[]
    static final int        UNSET = 0;
    static final int        INTERNALLY_SET = 1;
    static final int        MINIMUM_USER_STAMP = 2;

    // The next available value for stampp[]
    private int             nextStamp = MINIMUM_USER_STAMP;

    // the internal serial version which says which version was written
    // - 0 (default) for version up to JDK 1.1.5
    // - 1 for version from JDK 1.1.6, which writes a correct 'time' value
    //     as well as compatible values for other fields.  This is a
    //     transitional format.
    // - 2 (not implemented yet) a future version, in which fields[],
    //     areFieldsSet, and isTimeSet become transient, and isSet[] is
    //     removed. In JDK 1.1.6 we write a format compatible with version 2.
    static final int        currentSerialVersion = 1;
    private int             serialVersionOnStream = currentSerialVersion;

    // Proclaim serialization compatibility with JDK 1.1
    static final long       serialVersionUID = -1807547505821590642L;

    /**
     * Constructs a Calendar with the default time zone as returned
     * by TimeZone.getDefault(), and the default locale.
     * @see     TimeZone#getDefault
     */
    protected Calendar()
    {
        this(TimeZone.getDefault(), Locale.getDefault());
    }

    /**
     * Constructs a Calendar with the given time zone and locale.
     * @param zone the given time zone.
     */
    protected Calendar(TimeZone zone, Locale aLocale)
    {
        fields = new int[FIELD_COUNT];
        isSet = new boolean[FIELD_COUNT];
        stamp = new int[FIELD_COUNT];

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
     * a new time that is set for the calendar.  The time is <em>not</em>
     * recomputed first; to recompute the time, then the fields, call the
     * <code>complete</code> method.
     * @see #complete
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
     * @return the current time as UTC milliseconds from the epoch.
     */
    protected long getTimeInMillis() {
        if (!isTimeSet) updateTime();
        return time;
    }

    /**
     * Sets this Calendar's current time from the given long value.
     * @param date the new time in UTC milliseconds from the epoch.
     */
    protected void setTimeInMillis( long millis ) {
        isTimeSet = true;
        time = millis;
        areFieldsSet = false;
        if (!areFieldsSet) {
            computeFields();
            areFieldsSet = true;
            areAllFieldsSet = true;
        }
    }

    /**
     * Gets the value for a given time field.
     * @param field the given time field.
     * @return the value for the given time field.
     */
    public final int get(int field)
    {
        complete();
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
     * Sets the value for the given time field.  This is an internal
     * fast setter for subclasses.  It does not affect the areFieldsSet, isTimeSet,
     * or areAllFieldsSet flags.
     */
    final void internalSet(int field, int value)
    {
        fields[field] = value;
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
        stamp[field] = nextStamp++;
        areFieldsSet = false;
        isSet[field] = true; // Remove later
    }

    /**
     * Sets the values for the fields year, month, and date.
     * Previous values of other fields are retained.  If this is not desired,
     * call <code>clear</code> first.
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
     * Previous values of other fields are retained.  If this is not desired,
     * call <code>clear</code> first.
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
     * Sets the values for the fields year, month, date, hour, minute, and second.
     * Previous values of other fields are retained.  If this is not desired,
     * call <code>clear</code> first.
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
        fields = new int[FIELD_COUNT];
        stamp = new int[FIELD_COUNT];
        areFieldsSet = false;
        areAllFieldsSet = false;
        isSet = new boolean[FIELD_COUNT]; // Remove later
        isTimeSet = false;
    }

    /**
     * Clears the value in the given time field.
     * @param field the time field to be cleared.
     */
    public final void clear(int field)
    {
        fields[field] = 0;
        stamp[field] = UNSET;
        areFieldsSet = false;
        areAllFieldsSet = false;
        isSet[field] = false; // Remove later
        isTimeSet = false;
    }

    /**
     * Determines if the given time field has a value set.
     * @return true if the given time field has a value set; false otherwise.
     */
    public final boolean isSet(int field)
    {
        return stamp[field] != UNSET;
        // return isSet[field];
    }

    /**
     * Fills in any unset fields in the time field list.
     */
    protected void complete()
    {
        if (!isTimeSet) updateTime();
        if (!areFieldsSet) {
            computeFields(); // fills in unset fields
            areFieldsSet = true;
            areAllFieldsSet = true;
        }
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
    public abstract boolean equals(Object obj); // This becomes concrete in 1.2

    /**
     * Compares the time field records.
     * Equivalent to comparing result of conversion to UTC.
     * @param when the Calendar to be compared with this Calendar.
     * @return true if the current time of this Calendar is before
     * the time of Calendar when; false otherwise.
     */
    public abstract boolean before(Object when); // This becomes concrete in 1.2

    /**
     * Compares the time field records.
     * Equivalent to comparing result of conversion to UTC.
     * @param when the Calendar to be compared with this Calendar.
     * @return true if the current time of this Calendar is after
     * the time of Calendar when; false otherwise.
     */
    public abstract boolean after(Object when); // This becomes concrete in 1.2

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
        try {
            Calendar other = (Calendar) super.clone();

            other.fields = new int[FIELD_COUNT];
            other.isSet = new boolean[FIELD_COUNT];
            other.stamp = new int[FIELD_COUNT];
            System.arraycopy(this.fields, 0, other.fields, 0, FIELD_COUNT);
            System.arraycopy(this.isSet, 0, other.isSet, 0, FIELD_COUNT);
            System.arraycopy(this.stamp, 0, other.stamp, 0, FIELD_COUNT);

            other.zone = (TimeZone) zone.clone();
            return other;
        }
        catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    private static final String[] FIELD_NAME = {
        ",ERA=", ",YEAR=", ",MONTH=", ",WEEK_OF_YEAR=", ",WEEK_OF_MONTH=", ",DAY_OF_MONTH=",
        ",DAY_OF_YEAR=", ",DAY_OF_WEEK=", ",DAY_OF_WEEK_IN_MONTH=", ",AM_PM=", ",HOUR=",
        ",HOUR_OF_DAY=", ",MINUTE=", ",SECOND=", ",MILLISECOND=", ",ZONE_OFFSET=",
        ",DST_OFFSET="
    };

    /**
     * Return a string representation of this calendar.
     * @return  a string representation of this calendar.
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getClass().getName());
        buffer.append("[time=");
        buffer.append(isTimeSet ? String.valueOf(time) : "?");
        buffer.append(",areFieldsSet=");
        buffer.append(areFieldsSet);
        buffer.append(",areAllFieldsSet=");
        buffer.append(areAllFieldsSet);
        buffer.append(",lenient=");
        buffer.append(lenient);
        buffer.append(",zone=");
        buffer.append(zone);
        buffer.append(",firstDayOfWeek=");
        buffer.append(firstDayOfWeek);
        buffer.append(",minimalDaysInFirstWeek=");
        buffer.append(minimalDaysInFirstWeek);
        for (int i=0; i<FIELD_COUNT; ++i) {
            buffer.append(FIELD_NAME[i]);
            buffer.append(isSet(i) ? String.valueOf(fields[i]) : "?");
        }
        buffer.append(']');
        return buffer.toString();
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
	/* try to get the Locale data from the cache */
	int[] data = (int[]) cachedLocaleData.get(desiredLocale);
	if (data == null) {  /* cache miss */
	    ResourceBundle resource
		= ResourceBundle.getBundle("java.text.resources.LocaleElements",
					   desiredLocale);
	    String[] dateTimePatterns = 
		resource.getStringArray("DateTimeElements");
	    data = new int[2];
	    data[0] = Integer.parseInt(dateTimePatterns[0]);
	    data[1] = Integer.parseInt(dateTimePatterns[1]);
	    /* cache update */
	    cachedLocaleData.put(desiredLocale, data);
	}
	firstDayOfWeek = data[0];
	minimalDaysInFirstWeek = data[1];
    }

    /**
     * Recompute the time and update the status fields isTimeSet
     * and areFieldsSet.  Callers should check isTimeSet and only
     * call this method if isTimeSet is false.
     */
    private void updateTime() {
        computeTime();
        // If we are lenient, we need to recompute the fields to normalize
        // the values.  Also, if we haven't set all the fields yet (i.e.,
        // in a newly-created object), we need to fill in the fields. [LIU]
        if (isLenient() || !areAllFieldsSet) areFieldsSet = false;
        isTimeSet = true;
    }

    /**
     * Write object out to a serialization stream.
     */
    private void writeObject(ObjectOutputStream stream)
         throws IOException
    {
        /*
         * Calendar has many private fields that are not marked transient, such
         * as fields[], isTimeSet, and areFieldsSet.  In addition, it has a
         * field which is obsolete, isSet[]. These fields should not be part of
         * the persistent state. Unfortunately, this bug didn't get fixed before
         * JDK 1.1 shipped. This means that any fix has to be done in a way that
         * doesn't break serialization compatibility with 1.1. Fields in later
         * versions can only be removed from the serialized format (i.e., made
         * transient) if it can be shown that the Calendar implementation in 1.1
         * (and possibly modified versions in later releases) can function
         * correctly if all the removed fields are initialized to their default
         * values (0, null, false).
         *
         * If that's not possible, it might be possible that we'll declare for
         * some release that it's no longer two-way serialization compatible,
         * i.e., data can move from older to newer versions, but not back, or
         * back only to a specified release. To make that possible, we implement
         * a special form of serialization for now, such that (a) it works
         * correctly if fields that we intend to remove are initialized to their
         * default values and (b) the fields that we intend to keep are written
         * to streams with complete and up-to-date information so that the other
         * fields are not necessary. In other words, we write all fields, but on
         * reading ignore those that we intend to remove.
         */

        // Try to compute the time correctly, for the future (stream
        // version 2) in which we don't write out fields[] or isSet[].
        if (!isTimeSet) {
            try {
                updateTime();
            }
            catch (IllegalArgumentException e) {}
        }

        // Write out the 1.1 FCS object.
        stream.defaultWriteObject();
    }

    /**
     * Read this object out to a serialization stream.
     */
    private void readObject(ObjectInputStream stream)
         throws IOException, ClassNotFoundException
    {
        stream.defaultReadObject();

        stamp = new int[FIELD_COUNT];

        // Starting with version 2 (not implemented yet), we expect that
        // fields[], isSet[], isTimeSet, and areFieldsSet may not be
        // streamed out anymore.  We expect 'time' to be correct.
        if (serialVersionOnStream >= 2)
        {
            isTimeSet = true;
            if (fields == null) fields = new int[FIELD_COUNT];
            if (isSet == null) isSet = new boolean[FIELD_COUNT];
        }
        else if (serialVersionOnStream == 0)
        {
            for (int i=0; i<FIELD_COUNT; ++i)
                stamp[i] = isSet[i] ? INTERNALLY_SET : UNSET;
        }

        serialVersionOnStream = currentSerialVersion;
    }
}
