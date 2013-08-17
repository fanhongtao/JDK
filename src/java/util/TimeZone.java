/*
 * Copyright 2007 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * (C) Copyright Taligent, Inc. 1996 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - All Rights Reserved
 *
 *   The original version of this source code and documentation is copyrighted
 * and owned by Taligent, Inc., a wholly-owned subsidiary of IBM. These
 * materials are provided under terms of a License Agreement between Taligent
 * and Sun. This technology is protected by multiple US and International
 * patents. This notice and attribution to Taligent may not be removed.
 *   Taligent is a registered trademark of Taligent, Inc.
 *
 */

package java.util;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import sun.security.action.GetPropertyAction;

/**
 * <code>TimeZone</code> represents a time zone offset, and also figures out daylight
 * savings.
 *
 * <p>
 * Typically, you get a <code>TimeZone</code> using <code>getDefault</code>
 * which creates a <code>TimeZone</code> based on the time zone where the program
 * is running. For example, for a program running in Japan, <code>getDefault</code>
 * creates a <code>TimeZone</code> object based on Japanese Standard Time.
 *
 * <p>
 * You can also get a <code>TimeZone</code> using <code>getTimeZone</code>
 * along with a time zone ID. For instance, the time zone ID for the
 * U.S. Pacific Time zone is "America/Los_Angeles". So, you can get a
 * U.S. Pacific Time <code>TimeZone</code> object with:
 * <blockquote>
 * <pre>
 * TimeZone tz = TimeZone.getTimeZone("America/Los_Angeles");
 * </pre>
 * </blockquote>
 * You can use <code>getAvailableIDs</code> method to iterate through
 * all the supported time zone IDs. You can then choose a
 * supported ID to get a <code>TimeZone</code>.
 * If the time zone you want is not represented by one of the
 * supported IDs, then you can create a custom time zone ID with
 * the following syntax:
 *
 * <blockquote>
 * <pre>
 * GMT[+|-]hh[[:]mm]
 * </pre>
 * </blockquote>
 *
 * For example, you might specify GMT+14:00 as a custom
 * time zone ID.  The <code>TimeZone</code> that is returned
 * when you specify a custom time zone ID does not include
 * daylight savings time.
 * <p>
 * For compatibility with JDK 1.1.x, some other three-letter time zone IDs
 * (such as "PST", "CTT", "AST") are also supported. However, <strong>their
 * use is deprecated</strong> because the same abbreviation is often used
 * for multiple time zones (for example, "CST" could be U.S. "Central Standard
 * Time" and "China Standard Time"), and the Java platform can then only
 * recognize one of them.
 *
 *
 * @see          Calendar
 * @see          GregorianCalendar
 * @see          SimpleTimeZone
 * @version      1.63 01/18/07
 * @author       Mark Davis, David Goldsmith, Chen-Lieh Huang, Alan Liu
 * @since        JDK1.1
 */
abstract public class TimeZone implements Serializable, Cloneable {
    /**
     * Sole constructor.  (For invocation by subclass constructors, typically
     * implicit.)
     */
    public TimeZone() {
    }

    /**
     * A style specifier for <code>getDisplayName()</code> indicating
     * a short name, such as "PST."
     * @see #LONG
     * @since 1.2
     */
    public static final int SHORT = 0;

    /**
     * A style specifier for <code>getDisplayName()</code> indicating
     * a long name, such as "Pacific Standard Time."
     * @see #SHORT
     * @since 1.2
     */
    public static final int LONG  = 1;

    // Constants used internally; unit is milliseconds
    private static final int ONE_MINUTE = 60*1000;
    private static final int ONE_HOUR   = 60*ONE_MINUTE;
    private static final int ONE_DAY    = 24*ONE_HOUR;

    /**
     * Cache to hold the SimpleDateFormat objects for a Locale.
     */
    private static Hashtable cachedLocaleData = new Hashtable(3);

    // Proclaim serialization compatibility with JDK 1.1
    static final long serialVersionUID = 3581463369166924961L;

    /**
     * Gets the time zone offset, for current date, modified in case of
     * daylight savings. This is the offset to add *to* UTC to get local time.
     * @param era the era of the given date.
     * @param year the year in the given date.
     * @param month the month in the given date.
     * Month is 0-based. e.g., 0 for January.
     * @param day the day-in-month of the given date.
     * @param dayOfWeek the day-of-week of the given date.
     * @param milliseconds the millis in day in <em>standard</em> local time.
     * @return the offset to add *to* GMT to get local time.
     */
    abstract public int getOffset(int era, int year, int month, int day,
                                  int dayOfWeek, int milliseconds);

    /**
     * Gets the time zone offset, for current date, modified in case of
     * daylight savings. This is the offset to add *to* UTC to get local time.
     * @param era the era of the given date.
     * @param year the year in the given date.
     * @param month the month in the given date.
     * Month is 0-based. e.g., 0 for January.
     * @param day the day-in-month of the given date.
     * @param dayOfWeek the day-of-week of the given date.
     * @param milliseconds the millis in day in <em>standard</em> local time.
     * @param monthLength the length of the given month in days.
     * @param prevMonthLength the length of the previous month in days.
     * @return the offset to add *to* GMT to get local time.
     */
    int getOffset(int era, int year, int month, int day,
		  int dayOfWeek, int milliseconds, int monthLength, int prevMonthLength) {
	// Default implementation which ignores the monthLength.
	// SimpleTimeZone overrides this and actually uses monthLength.
	return getOffset(era, year, month, day, dayOfWeek, milliseconds);
    }
    

    /**
     * Sets the base time zone offset to GMT.
     * This is the offset to add *to* UTC to get local time.
     * @param offsetMillis the given base time zone offset to GMT.
     */
    abstract public void setRawOffset(int offsetMillis);

    /**
     * Gets unmodified offset, NOT modified in case of daylight savings.
     * This is the offset to add *to* UTC to get local time.
     * @return the unmodified offset to add *to* UTC to get local time.
     */
    abstract public int getRawOffset();

    /**
     * Gets the ID of this time zone.
     * @return the ID of this time zone.
     */
    public String getID()
    {
        return ID;
    }

    /**
     * Sets the time zone ID. This does not change any other data in
     * the time zone object.
     * @param ID the new time zone ID.
     */
    public void setID(String ID)
    {
        if (ID == null) {
            throw new NullPointerException();
        }
        this.ID = ID;
    }

    /**
     * Returns a name of this time zone suitable for presentation to the user
     * in the default locale.
     * This method returns the long name, not including daylight savings.
     * If the display name is not available for the locale,
     * then this method returns a string in the format
     * <code>GMT[+-]hh:mm</code>.
     * @return the human-readable name of this time zone in the default locale.
     * @since 1.2
     */
    public final String getDisplayName() {
        return getDisplayName(false, LONG, Locale.getDefault());
    }

    /**
     * Returns a name of this time zone suitable for presentation to the user
     * in the specified locale.
     * This method returns the long name, not including daylight savings.
     * If the display name is not available for the locale,
     * then this method returns a string in the format
     * <code>GMT[+-]hh:mm</code>.
     * @param locale the locale in which to supply the display name.
     * @return the human-readable name of this time zone in the given locale
     * or in the default locale if the given locale is not recognized.
     * @since 1.2
     */
    public final String getDisplayName(Locale locale) {
        return getDisplayName(false, LONG, locale);
    }

    /**
     * Returns a name of this time zone suitable for presentation to the user
     * in the default locale.
     * If the display name is not available for the locale,
     * then this method returns a string in the format
     * <code>GMT[+-]hh:mm</code>.
     * @param daylight if true, return the daylight savings name.
     * @param style either <code>LONG</code> or <code>SHORT</code>
     * @return the human-readable name of this time zone in the default locale.
     * @since 1.2
     */
    public final String getDisplayName(boolean daylight, int style) {
        return getDisplayName(daylight, style, Locale.getDefault());
    }

    /**
     * Returns a name of this time zone suitable for presentation to the user
     * in the specified locale.
     * If the display name is not available for the locale,
     * then this method returns a string in the format
     * <code>GMT[+-]hh:mm</code>.
     * @param daylight if true, return the daylight savings name.
     * @param style either <code>LONG</code> or <code>SHORT</code>
     * @param locale the locale in which to supply the display name.
     * @return the human-readable name of this time zone in the given locale
     * or in the default locale if the given locale is not recognized.
     * @exception IllegalArgumentException style is invalid.
     * @since 1.2
     */
    public String getDisplayName(boolean daylight, int style, Locale locale) {
        /* NOTES:
         * (1) We use SimpleDateFormat for simplicity; we could do this
         * more efficiently but it would duplicate the SimpleDateFormat code
         * here, which is undesirable.
         * (2) Attempts to move the code from SimpleDateFormat to here also run
         * aground because this requires SimpleDateFormat to keep a Locale
         * object around, which it currently doesn't; to synthesize such a
         * locale upon resurrection; and to somehow handle the special case of
         * construction from a DateFormatSymbols object.
         */
        if (style != SHORT && style != LONG) {
            throw new IllegalArgumentException("Illegal style: " + style);
        }
        // We keep a cache, indexed by locale.  The cache contains a
        // SimpleDateFormat object, which we create on demand.
        SoftReference data = (SoftReference)cachedLocaleData.get(locale);
        SimpleDateFormat format;
        if (data == null ||
            (format = (SimpleDateFormat)data.get()) == null) {
            format = new SimpleDateFormat(null, locale);
            cachedLocaleData.put(locale, new SoftReference(format));
        }
        // Create a new SimpleTimeZone as a stand-in for this zone; the stand-in
        // will have no DST, or DST during January, but the same ID and offset,
        // and hence the same display name.  We don't cache these because
        // they're small and cheap to create.
        SimpleTimeZone tz;
        if (daylight && useDaylightTime()) {
            int savings = ONE_HOUR;
            try {
                savings = ((SimpleTimeZone) this).getDSTSavings();
            } catch (ClassCastException e) {}
            tz = new SimpleTimeZone(getRawOffset(), getID(),
                                    Calendar.JANUARY, 1, 0, 0,
                                    Calendar.FEBRUARY, 1, 0, 0,
                                    savings);
        } else {
            tz = new SimpleTimeZone(getRawOffset(), getID());
        }
        format.applyPattern(style == LONG ? "zzzz" : "z");      
        format.setTimeZone(tz);
        // Format a date in January.  We use the value 10*ONE_DAY == Jan 11 1970
        // 0:00 GMT.
        return format.format(new Date(864000000L));
    }

    /**
     * Queries if this time zone uses daylight savings time.
     * @return true if this time zone uses daylight savings time,
     * false, otherwise.
     */
    abstract public boolean useDaylightTime();

    /**
     * Queries if the given date is in daylight savings time in
     * this time zone.
     * @param date the given Date.
     * @return true if the given date is in daylight savings time,
     * false, otherwise.
     */
    abstract public boolean inDaylightTime(Date date);

    /**
     * Gets the <code>TimeZone</code> for the given ID.
     *
     * @param ID the ID for a <code>TimeZone</code>, either an abbreviation
     * such as "PST", a full name such as "America/Los_Angeles", or a custom
     * ID such as "GMT-8:00". Note that the support of abbreviations is
     * for JDK 1.1.x compatibility only and full names should be used.
     *
     * @return the specified <code>TimeZone</code>, or the GMT zone if the given ID
     * cannot be understood.
     */
    public static synchronized TimeZone getTimeZone(String ID) {
        /* We first try to lookup the zone ID in our hashtable.  If this fails,
         * we try to parse it as a custom string GMT[+-]hh:mm.  This allows us
         * to recognize zones in user.timezone that otherwise cannot be
         * identified.  We do the recognition here, rather than in getDefault(),
         * so that the default zone is always the result of calling
         * getTimeZone() with the property user.timezone.
         *
         * If all else fails, we return GMT, which is probably not what the user
         * wants, but at least is a functioning TimeZone object. */
        TimeZone zone = TimeZoneData.get(ID);
        if (zone == null) zone = parseCustomTimeZone(ID);
        if (zone == null) zone = (TimeZone)GMT.clone();
        return zone;
    }

    /**
     * Gets the available IDs according to the given time zone offset.
     * @param rawOffset the given time zone GMT offset.
     * @return an array of IDs, where the time zone for that ID has
     * the specified GMT offset. For example, "America/Phoenix" and "America/Denver"
     * both have GMT-07:00, but differ in daylight savings behavior.
     */
    public static synchronized String[] getAvailableIDs(int rawOffset) {
	String[] result;
	Vector matched = new Vector();

	/* The array TimeZoneData.zones is no longer sorted by raw offset.
	 * Now scanning through all zone data to match offset.
	 */
	for (int i = 0; i < TimeZoneData.zones.length; ++i) {
	    if (TimeZoneData.zones[i].getRawOffset() == rawOffset)
		matched.add(TimeZoneData.zones[i].getID());
	}
	result = new String[matched.size()];
	matched.toArray(result);

        return result;
    }

    /**
     * Gets all the available IDs supported.
     * @return an array of IDs.
     */
    public static synchronized String[] getAvailableIDs() {
        String[]    resultArray = new String[TimeZoneData.zones.length];
        int         count = 0;
        for (int i = 0; i < TimeZoneData.zones.length; ++i)
            resultArray[count++] = TimeZoneData.zones[i].getID();

        // copy into array of the right size and return
        String[] finalResult = new String[count];
        System.arraycopy(resultArray, 0, finalResult, 0, count);

        return finalResult;
    }
    
    /**
     * Gets the platform defined TimeZone ID.
     **/
    private static native String getSystemTimeZoneID(String javaHome, 
						     String region);

    /**
     * Gets the default <code>TimeZone</code> for this host.
     * The source of the default <code>TimeZone</code> 
     * may vary with implementation.
     * @return a default <code>TimeZone</code>.
     */
    public static synchronized TimeZone getDefault() {
        if (defaultZone == null) {
            // get the time zone ID from the system properties
	    String zoneID = (String) AccessController.doPrivileged(
		new GetPropertyAction("user.timezone"));

	    // if the time zone ID is not set (yet), perform the
	    // platform to Java time zone ID mapping.
	    if (zoneID == null || zoneID.equals("")) { 
		String region = (String) AccessController.doPrivileged(
		    new GetPropertyAction("user.region"));
		String javaHome = (String) AccessController.doPrivileged(
		    new GetPropertyAction("java.home"));
		zoneID = getSystemTimeZoneID(javaHome, region);
		if (zoneID == null) {
		    zoneID = GMT_ID;
		}
		final String id = zoneID;
		AccessController.doPrivileged(new PrivilegedAction() {
		    public Object run() {
			System.setProperty("user.timezone", id);
			return null;
		    }
		});
	    }
            defaultZone = getTimeZone(zoneID);
        }
        return (TimeZone)defaultZone.clone();
    }

    /**
     * Sets the <code>TimeZone</code> that is
     * returned by the <code>getDefault</code> method.  If <code>zone</code>
     * is null, reset the default to the value it had originally when the
     * VM first started.
     * @param zone the new default time zone
     */
    public static synchronized void setDefault(TimeZone zone)
    {
        defaultZone = zone;
    }

    /**
     * Returns true if this zone has the same rule and offset as another zone.
     * That is, if this zone differs only in ID, if at all.  Returns false
     * if the other zone is null.
     * @param other the <code>TimeZone</code> object to be compared with
     * @return true if the other zone is not null and is the same as this one,
     * with the possible exception of the ID
     * @since 1.2
     */
    public boolean hasSameRules(TimeZone other) {
        return other != null && getRawOffset() == other.getRawOffset() &&
            useDaylightTime() == other.useDaylightTime();
    }

    /**
     * Overrides Cloneable
     */
    public Object clone()
    {
        try {
            TimeZone other = (TimeZone) super.clone();
            other.ID = ID;
            return other;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    // =======================privates===============================

    /**
     * The string identifier of this <code>TimeZone</code>.  This is a
     * programmatic identifier used internally to look up <code>TimeZone</code>
     * objects from the system table and also to map them to their localized
     * display names.  <code>ID</code> values are unique in the system
     * table but may not be for dynamically created zones.
     * @serial
     */
    private String           ID;
    private static TimeZone  defaultZone = null;

    static final String         GMT_ID        = "GMT";
    private static final int    GMT_ID_LENGTH = 3;
    private static final String CUSTOM_ID     = "Custom";

    private static NumberFormat numberFormat = null;

    private static final TimeZone GMT = new SimpleTimeZone(0, GMT_ID);

    /**
     * Parse a custom time zone identifier and return a corresponding zone.
     * @param id a string of the form GMT[+-]hh:mm, GMT[+-]hhmm, or
     * GMT[+-]hh.
     * @return a newly created SimpleTimeZone with the given offset and
     * no daylight savings time, or null if the id cannot be parsed.
     */
    private static final SimpleTimeZone parseCustomTimeZone(String id) {
        if (id.length() > GMT_ID_LENGTH &&
            id.regionMatches(true, 0, GMT_ID, 0, GMT_ID_LENGTH)) {
            ParsePosition pos = new ParsePosition(GMT_ID_LENGTH);
            boolean negative = false;
            int offset;

            if (id.charAt(pos.getIndex()) == '-')
                negative = true;
            else if (id.charAt(pos.getIndex()) != '+')
                return null;
            pos.setIndex(pos.getIndex() + 1);

            // Create NumberFormat if necessary
            synchronized (TimeZoneData.class) {
                if (numberFormat == null) {
                    numberFormat = NumberFormat.getInstance();
                    numberFormat.setParseIntegerOnly(true);
                }
            }

            synchronized (numberFormat) {
                // Look for either hh:mm, hhmm, or hh
                int start = pos.getIndex();
                Number n = numberFormat.parse(id, pos);
                if (n == null) return null;
                offset = n.intValue();

                if (pos.getIndex() < id.length() &&
                    id.charAt(pos.getIndex()) == ':') {
                    // hh:mm
                    offset *= 60;
                    pos.setIndex(pos.getIndex() + 1);
                    n = numberFormat.parse(id, pos);
                    if (n == null) return null;
                    offset += n.intValue();
                }
                else {
                    // hhmm or hh

                    // Be strict about interpreting something as hh; it must be
                    // an offset < 30, and it must be one or two digits. Thus
                    // 0010 is interpreted as 00:10, but 10 is interpreted as
                    // 10:00.
                    if (offset < 30 && (pos.getIndex() - start) <= 2)
                        offset *= 60; // hh, from 00 to 29; 30 is 00:30
                    else
                        offset = offset % 100 + offset / 100 * 60; // hhmm
                }

                if (negative) offset = -offset;
                return new SimpleTimeZone(offset * 60000, CUSTOM_ID);
            }
        }

        return null;
    }

    // Internal Implementation Notes [LIU]
    //
    // TimeZone data is stored in two parts.  The first is an encoding of the
    // rules for each TimeZone.  A TimeZone rule includes the offset of a zone
    // in milliseconds from GMT, the starting month and day for daylight savings
    // time, if there is any, and the ending month and day for daylight savings
    // time.  The starting and ending days are specified in terms of the n-th
    // day of the week, for instance, the first Sunday or the last ("-1"-th)
    // Sunday of the month.  The rules are stored as statically-constructed
    // SimpleTimeZone objects in the TimeZone class.
    //
    // Each rule has a unique internal identifier string which is used to
    // specify it.  This identifier string is arbitrary, and is not to be shown
    // to the user -- it is for programmatic use only.  In order to instantiate
    // a TimeZone object, you pass its identifier string to
    // TimeZone.getTimeZone().  (This identifier is also used to index the
    // localized string data.)
    //
    // The second part of the data consists of localized string names used by
    // DateFormat to describe various TimeZones.  A TimeZone may have up to four
    // names: The abbreviated and long name for standard time in that zone, and
    // the abbreviated and long name for daylight savings time in that zone.
    // The data also includes a representative city.  For example, [ "PST",
    // "Pacific Standard Time", "PDT", "Pacific Daylight Time", "Los Angeles" ]
    // might be one such set of string names in the en_US locale.  These strings
    // are intended to be shown to the user.  The string data is indexed in the
    // system by a pair (String id, Locale locale).  The id is the unique string
    // identifier for the rule for the given TimeZone (as passed to
    // TimeZone.getTimeZone()).  String names are stored as localized resource
    // data of the class java.text.resources.DateFormatZoneData???  where ??? is
    // the Locale specifier (e.g., DateFormatZoneData_en_US).  This data is a
    // two-dimensional array of strings with N rows and 6 columns.  The columns
    // are id, short standard name, long standard name, short daylight name,
    // long daylight name, representative city name.
    //
    // The mapping between rules (SimpleTimeZone objects) and localized string
    // names (DateFormatZoneData objects) is one-to-many.  That is, there will
    // sometimes be more than one localized string name sets associated with
    // each rule.
    //
    // Each locale can potentially have localized name data for all time zones.
    // Since we support approximately 90 time zones and approximately 50
    // locales, there can be over 4500 sets of localized names.  In practice,
    // only a fraction of these names are provided.  If a time zone needs to be
    // displayed to the user in a given locale, and there is no string data in
    // that locale for that time zone, then the default representation will be
    // shown.  This is a string of the form GMT+HHMM or GMT-HHMM, where HHMM
    // represents the offset in hours and minutes with respect to GMT.  This
    // format is used because it is recognized in all locales.  In order to make
    // this mechanism to work, the root resource data (in the class
    // DateFormatZoneData) is left empty.
    //
    // The current default TimeZone is determined via the system property
    // user.timezone.  This is set by the platform-dependent native code to
    // a three-letter abbreviation.  We interpret these into our own internal
    // IDs using a lookup table.
}

/**
 * Encapsulates data for international timezones.  This package-private class is for
 * internal use only by TimeZone.  It encapsulates the list of recognized international
 * timezones.  By implementing this as a separate class, the loading and initialization
 * cost for this array is delayed until a TimeZone object is actually created from its ID.
 * This class contains only static variables and static methods; it cannot be instantiated.
 */
class TimeZoneData
{
    static final TimeZone get(String ID) {
        Object o = lookup.get(ID);
        return o == null ? null : (TimeZone)((TimeZone)o).clone(); // [sic]
    }

    // ---------------- BEGIN GENERATED DATA ----------------
    private static final int ONE_HOUR = 60*60*1000;
    private static final int ONE_MINUTE = 60*1000;

    // The following data is based on tzdata2007a.

    static final SimpleTimeZone zones[] = {
	//--------------------------------------------------------------------
	new SimpleTimeZone(-11*ONE_HOUR, "MIT" /* Pacific/Apia */),
	// Zone MIT	-11:00	-	WST			# Samoa Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(-11*ONE_HOUR, "Pacific/Apia"),
	// Zone Pacific/Apia	-11:00	-	WST			# Samoa Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(-11*ONE_HOUR, "Pacific/Niue"),
	// Zone Pacific/Niue	-11:00	-	NUT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-11*ONE_HOUR, "Pacific/Pago_Pago"),
	// Zone Pacific/Pago_Pago	-11:00	-	SST			# S=Samoa
	//--------------------------------------------------------------------
	new SimpleTimeZone(-10*ONE_HOUR, "America/Adak",
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	US	2007	max	-	Mar	Sun>=8	2:00	1:00	D
	// Rule	US	2007	max	-	Nov	Sun>=1	2:00	0	S
	// Zone America/Adak	-10:00	US	HA%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-10*ONE_HOUR, "HST" /* Pacific/Honolulu */),
	// Zone HST	-10:00	-	HST
	//--------------------------------------------------------------------
	new SimpleTimeZone(-10*ONE_HOUR, "Pacific/Fakaofo"),
	// Zone Pacific/Fakaofo	-10:00	-	TKT	# Tokelau Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(-10*ONE_HOUR, "Pacific/Honolulu"),
	// Zone Pacific/Honolulu	-10:00	-	HST
	//--------------------------------------------------------------------
	new SimpleTimeZone(-10*ONE_HOUR, "Pacific/Rarotonga"),
	// Zone Pacific/Rarotonga	-10:00	Cook	CK%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-10*ONE_HOUR, "Pacific/Tahiti"),
	// Zone Pacific/Tahiti	-10:00	-	TAHT	# Tahiti Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(-(9*ONE_HOUR+30*ONE_MINUTE), "Pacific/Marquesas"),
	// Zone Pacific/Marquesas	-9:30	-	MART	# Marquesas Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(-9*ONE_HOUR, "AST" /* America/Anchorage */,
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	US	2007	max	-	Mar	Sun>=8	2:00	1:00	D
	// Rule	US	2007	max	-	Nov	Sun>=1	2:00	0	S
	// Zone AST	-9:00	US	AK%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-9*ONE_HOUR, "America/Anchorage",
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	US	2007	max	-	Mar	Sun>=8	2:00	1:00	D
	// Rule	US	2007	max	-	Nov	Sun>=1	2:00	0	S
	// Zone America/Anchorage	-9:00	US	AK%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-9*ONE_HOUR, "Pacific/Gambier"),
	// Zone Pacific/Gambier	-9:00	-	GAMT	# Gambier Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(-8*ONE_HOUR, "America/Dawson",
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	Canada	2007	max	-	Mar	Sun>=8	2:00	1:00	D
	// Rule	Canada	2007	max	-	Nov	Sun>=1	2:00	0	S
	// Zone America/Dawson	-8:00	Canada	P%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-8*ONE_HOUR, "America/Los_Angeles",
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	US	2007	max	-	Mar	Sun>=8	2:00	1:00	D
	// Rule	US	2007	max	-	Nov	Sun>=1	2:00	0	S
	// Zone America/Los_Angeles	-8:00	US	P%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-8*ONE_HOUR, "America/Tijuana",
	  Calendar.APRIL, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	Mexico	2002	max	-	Apr	Sun>=1	2:00	1:00	D
	// Rule	Mexico	2002	max	-	Oct	lastSun	2:00	0	S
	// Zone America/Tijuana	-8:00	Mexico	P%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-8*ONE_HOUR, "America/Vancouver",
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	Canada	2007	max	-	Mar	Sun>=8	2:00	1:00	D
	// Rule	Canada	2007	max	-	Nov	Sun>=1	2:00	0	S
	// Zone America/Vancouver	-8:00	Canada	P%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-8*ONE_HOUR, "PST" /* America/Los_Angeles */,
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	US	2007	max	-	Mar	Sun>=8	2:00	1:00	D
	// Rule	US	2007	max	-	Nov	Sun>=1	2:00	0	S
	// Zone PST	-8:00	US	P%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-8*ONE_HOUR, "Pacific/Pitcairn"),
	// Zone Pacific/Pitcairn	-8:00	-	PST	# Pitcairn Standard Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(-7*ONE_HOUR, "America/Dawson_Creek"),
	// Zone America/Dawson_Creek	-7:00	-	MST
	//--------------------------------------------------------------------
	new SimpleTimeZone(-7*ONE_HOUR, "America/Denver",
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	US	2007	max	-	Mar	Sun>=8	2:00	1:00	D
	// Rule	US	2007	max	-	Nov	Sun>=1	2:00	0	S
	// Zone America/Denver	-7:00	US	M%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-7*ONE_HOUR, "America/Edmonton",
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	Canada	2007	max	-	Mar	Sun>=8	2:00	1:00	D
	// Rule	Canada	2007	max	-	Nov	Sun>=1	2:00	0	S
	// Zone America/Edmonton	-7:00	Canada	M%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-7*ONE_HOUR, "America/Mazatlan",
	  Calendar.APRIL, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	Mexico	2002	max	-	Apr	Sun>=1	2:00	1:00	D
	// Rule	Mexico	2002	max	-	Oct	lastSun	2:00	0	S
	// Zone America/Mazatlan	-7:00	Mexico	M%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-7*ONE_HOUR, "America/Phoenix"),
	// Zone America/Phoenix	-7:00	-	MST
	//--------------------------------------------------------------------
	new SimpleTimeZone(-7*ONE_HOUR, "MST" /* America/Denver */,
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	US	2007	max	-	Mar	Sun>=8	2:00	1:00	D
	// Rule	US	2007	max	-	Nov	Sun>=1	2:00	0	S
	// Zone MST	-7:00	US	M%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-7*ONE_HOUR, "PNT" /* America/Phoenix */),
	// Zone PNT	-7:00	-	MST
	//--------------------------------------------------------------------
	new SimpleTimeZone(-6*ONE_HOUR, "America/Belize"),
	// Zone America/Belize	-6:00	Belize	C%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-6*ONE_HOUR, "America/Chicago",
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	US	2007	max	-	Mar	Sun>=8	2:00	1:00	D
	// Rule	US	2007	max	-	Nov	Sun>=1	2:00	0	S
	// Zone America/Chicago	-6:00	US	C%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-6*ONE_HOUR, "America/Costa_Rica"),
	// Zone America/Costa_Rica	-6:00	CR	C%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-6*ONE_HOUR, "America/El_Salvador"),
	// Zone America/El_Salvador	-6:00	Salv	C%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-6*ONE_HOUR, "America/Guatemala"),
	// Zone America/Guatemala	-6:00	Guat	C%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-6*ONE_HOUR, "America/Indiana/Knox",
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	US	2007	max	-	Mar	Sun>=8	2:00	1:00	D
	// Rule	US	2007	max	-	Nov	Sun>=1	2:00	0	S
	// Zone America/Indiana/Knox	-6:00	US	C%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-6*ONE_HOUR, "America/Indiana/Petersburg",
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	US	2007	max	-	Mar	Sun>=8	2:00	1:00	D
	// Rule	US	2007	max	-	Nov	Sun>=1	2:00	0	S
	// Zone America/Indiana/Petersburg	-6:00	US	C%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-6*ONE_HOUR, "America/Indiana/Vincennes",
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	US	2007	max	-	Mar	Sun>=8	2:00	1:00	D
	// Rule	US	2007	max	-	Nov	Sun>=1	2:00	0	S
	// Zone America/Indiana/Vincennes	-6:00	US	C%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-6*ONE_HOUR, "America/Managua"),
	// Zone America/Managua	-6:00	Nic	C%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-6*ONE_HOUR, "America/Mexico_City",
	  Calendar.APRIL, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	Mexico	2002	max	-	Apr	Sun>=1	2:00	1:00	D
	// Rule	Mexico	2002	max	-	Oct	lastSun	2:00	0	S
	// Zone America/Mexico_City	-6:00	Mexico	C%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-6*ONE_HOUR, "America/Regina"),
	// Zone America/Regina	-6:00	-	CST
	//--------------------------------------------------------------------
	new SimpleTimeZone(-6*ONE_HOUR, "America/Tegucigalpa",
	  Calendar.MAY, 1, -Calendar.SUNDAY, 0, SimpleTimeZone.WALL_TIME,
	  Calendar.AUGUST, 1, -Calendar.MONDAY, 0, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	Hond	2006	2009	-	May	Sun>=1	0:00	1:00	D
	// Rule	Hond	2006	2009	-	Aug	Mon>=1	0:00	0	S
	// Zone America/Tegucigalpa	-6:00	Hond	C%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-6*ONE_HOUR, "America/Winnipeg",
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	Canada	2007	max	-	Mar	Sun>=8	2:00	1:00	D
	// Rule	Canada	2007	max	-	Nov	Sun>=1	2:00	0	S
	// Zone America/Winnipeg	-6:00	Canada	C%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-6*ONE_HOUR, "CST" /* America/Chicago */,
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	US	2007	max	-	Mar	Sun>=8	2:00	1:00	D
	// Rule	US	2007	max	-	Nov	Sun>=1	2:00	0	S
	// Zone CST	-6:00	US	C%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-6*ONE_HOUR, "Pacific/Easter",
	  Calendar.OCTOBER, 9, -Calendar.SUNDAY, 4*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.MARCH, 9, -Calendar.SUNDAY, 3*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	Chile	1999	max	-	Oct	Sun>=9	4:00u	1:00	S
	// Rule	Chile	2000	max	-	Mar	Sun>=9	3:00u	0	-
	// Zone Pacific/Easter	-6:00	Chile	EAS%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-6*ONE_HOUR, "Pacific/Galapagos"),
	// Zone Pacific/Galapagos	-6:00	-	GALT	     # Galapagos Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(-5*ONE_HOUR, "America/Atikokan"),
	// Zone America/Atikokan	-5:00	-	EST
	//--------------------------------------------------------------------
	new SimpleTimeZone(-5*ONE_HOUR, "America/Bogota"),
	// Zone America/Bogota	-5:00	CO	CO%sT	# Colombia Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(-5*ONE_HOUR, "America/Cayman"),
	// Zone America/Cayman	-5:00	-	EST
	//--------------------------------------------------------------------
	new SimpleTimeZone(-5*ONE_HOUR, "America/Grand_Turk",
	  Calendar.APRIL, 1, -Calendar.SUNDAY, 0, SimpleTimeZone.WALL_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 0, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	TC	1987	max	-	Apr	Sun>=1	0:00	1:00	D
	// Rule	TC	1979	max	-	Oct	lastSun	0:00	0	S
	// Zone America/Grand_Turk	-5:00	TC	E%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-5*ONE_HOUR, "America/Guayaquil"),
	// Zone America/Guayaquil	-5:00	-	ECT	     # Ecuador Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(-5*ONE_HOUR, "America/Havana",
	  Calendar.APRIL, 1, -Calendar.SUNDAY, 0, SimpleTimeZone.STANDARD_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 0, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule	Cuba	2000	max	-	Apr	Sun>=1	0:00s	1:00	D
	// Rule	Cuba	2006	max	-	Oct	lastSun	0:00s	0	S
	// Zone America/Havana	-5:00	Cuba	C%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-5*ONE_HOUR, "America/Indiana/Indianapolis",
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	US	2007	max	-	Mar	Sun>=8	2:00	1:00	D
	// Rule	US	2007	max	-	Nov	Sun>=1	2:00	0	S
	// Zone America/Indiana/Indianapolis	-5:00	US	E%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-5*ONE_HOUR, "America/Indiana/Marengo",
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	US	2007	max	-	Mar	Sun>=8	2:00	1:00	D
	// Rule	US	2007	max	-	Nov	Sun>=1	2:00	0	S
	// Zone America/Indiana/Marengo	-5:00	US	E%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-5*ONE_HOUR, "America/Indianapolis" /* America/Indiana/Indianapolis */,
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	US	2007	max	-	Mar	Sun>=8	2:00	1:00	D
	// Rule	US	2007	max	-	Nov	Sun>=1	2:00	0	S
	// Zone America/Indianapolis	-5:00	US	E%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-5*ONE_HOUR, "America/Jamaica"),
	// Zone America/Jamaica	-5:00	-	EST
	//--------------------------------------------------------------------
	new SimpleTimeZone(-5*ONE_HOUR, "America/Lima"),
	// Zone America/Lima	-5:00	Peru	PE%sT	# Peru Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(-5*ONE_HOUR, "America/Montreal",
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	Canada	2007	max	-	Mar	Sun>=8	2:00	1:00	D
	// Rule	Canada	2007	max	-	Nov	Sun>=1	2:00	0	S
	// Zone America/Montreal	-5:00	Canada	E%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-5*ONE_HOUR, "America/Nassau",
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	US	2007	max	-	Mar	Sun>=8	2:00	1:00	D
	// Rule	US	2007	max	-	Nov	Sun>=1	2:00	0	S
	// Zone America/Nassau	-5:00	US	E%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-5*ONE_HOUR, "America/New_York",
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	US	2007	max	-	Mar	Sun>=8	2:00	1:00	D
	// Rule	US	2007	max	-	Nov	Sun>=1	2:00	0	S
	// Zone America/New_York	-5:00	US	E%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-5*ONE_HOUR, "America/Panama"),
	// Zone America/Panama	-5:00	-	EST
	//--------------------------------------------------------------------
	new SimpleTimeZone(-5*ONE_HOUR, "America/Port-au-Prince",
	  Calendar.APRIL, 1, -Calendar.SUNDAY, 0, SimpleTimeZone.WALL_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 0, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	Haiti	2005	max	-	Apr	Sun>=1	0:00	1:00	D
	// Rule	Haiti	2005	max	-	Oct	lastSun	0:00	0	S
	// Zone America/Port-au-Prince	-5:00	Haiti	E%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-5*ONE_HOUR, "America/Porto_Acre" /* America/Rio_Branco */),
	// Zone America/Porto_Acre	-5:00	-	ACT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-5*ONE_HOUR, "America/Rio_Branco"),
	// Zone America/Rio_Branco	-5:00	-	ACT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-5*ONE_HOUR, "America/Toronto",
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	Canada	2007	max	-	Mar	Sun>=8	2:00	1:00	D
	// Rule	Canada	2007	max	-	Nov	Sun>=1	2:00	0	S
	// Zone America/Toronto	-5:00	Canada	E%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-5*ONE_HOUR, "EST" /* America/New_York */,
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	US	2007	max	-	Mar	Sun>=8	2:00	1:00	D
	// Rule	US	2007	max	-	Nov	Sun>=1	2:00	0	S
	// Zone EST	-5:00	US	E%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-5*ONE_HOUR, "IET" /* America/Indiana/Indianapolis */,
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	US	2007	max	-	Mar	Sun>=8	2:00	1:00	D
	// Rule	US	2007	max	-	Nov	Sun>=1	2:00	0	S
	// Zone IET	-5:00	US	E%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "America/Anguilla"),
	// Zone America/Anguilla	-4:00	-	AST
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "America/Antigua"),
	// Zone America/Antigua	-4:00	-	AST
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "America/Aruba"),
	// Zone America/Aruba	-4:00	-	AST
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "America/Asuncion",
	  Calendar.OCTOBER, 15, -Calendar.SUNDAY, 0, SimpleTimeZone.WALL_TIME,
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 0, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	Para	2004	max	-	Oct	Sun>=15	0:00	1:00	S
	// Rule	Para	2005	max	-	Mar	Sun>=8	0:00	0	-
	// Zone America/Asuncion	-4:00	Para	PY%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "America/Barbados"),
	// Zone America/Barbados	-4:00	Barb	A%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "America/Caracas"),
	// Zone America/Caracas	-4:00	-	VET
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "America/Cuiaba",
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 0, SimpleTimeZone.WALL_TIME,
	  Calendar.FEBRUARY, -1, Calendar.SUNDAY, 0, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	Brazil	2006	max	-	Nov	Sun>=1	 0:00	1:00	S
	// Rule	Brazil	2007	max	-	Feb	lastSun	 0:00	0	-
	// Zone America/Cuiaba	-4:00	Brazil	AM%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "America/Curacao"),
	// Zone America/Curacao	-4:00	-	AST
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "America/Dominica"),
	// Zone America/Dominica	-4:00	-	AST
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "America/Grenada"),
	// Zone America/Grenada	-4:00	-	AST
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "America/Guadeloupe"),
	// Zone America/Guadeloupe	-4:00	-	AST
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "America/Guyana"),
	// Zone America/Guyana	-4:00	-	GYT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "America/Halifax",
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	Canada	2007	max	-	Mar	Sun>=8	2:00	1:00	D
	// Rule	Canada	2007	max	-	Nov	Sun>=1	2:00	0	S
	// Zone America/Halifax	-4:00	Canada	A%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "America/La_Paz"),
	// Zone America/La_Paz	-4:00	-	BOT	# Bolivia Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "America/Manaus"),
	// Zone America/Manaus	-4:00	-	AMT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "America/Martinique"),
	// Zone America/Martinique	-4:00	-	AST
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "America/Moncton",
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	Canada	2007	max	-	Mar	Sun>=8	2:00	1:00	D
	// Rule	Canada	2007	max	-	Nov	Sun>=1	2:00	0	S
	// Zone America/Moncton	-4:00	Canada	A%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "America/Montserrat"),
	// Zone America/Montserrat	-4:00	-	AST
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "America/Port_of_Spain"),
	// Zone America/Port_of_Spain	-4:00	-	AST
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "America/Puerto_Rico"),
	// Zone America/Puerto_Rico	-4:00	-	AST
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "America/Santiago",
	  Calendar.OCTOBER, 9, -Calendar.SUNDAY, 4*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.MARCH, 9, -Calendar.SUNDAY, 3*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	Chile	1999	max	-	Oct	Sun>=9	4:00u	1:00	S
	// Rule	Chile	2000	max	-	Mar	Sun>=9	3:00u	0	-
	// Zone America/Santiago	-4:00	Chile	CL%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "America/Santo_Domingo"),
	// Zone America/Santo_Domingo	-4:00	-	AST
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "America/St_Kitts"),
	// Zone America/St_Kitts	-4:00	-	AST
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "America/St_Lucia"),
	// Zone America/St_Lucia	-4:00	-	AST
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "America/St_Thomas"),
	// Zone America/St_Thomas	-4:00	-	AST
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "America/St_Vincent"),
	// Zone America/St_Vincent	-4:00	-	AST
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "America/Thule",
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	Thule	2007	max	-	Mar	Sun>=8	2:00	1:00	D
	// Rule	Thule	2007	max	-	Nov	Sun>=1	2:00	0	S
	// Zone America/Thule	-4:00	Thule	A%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "America/Tortola"),
	// Zone America/Tortola	-4:00	-	AST
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "Antarctica/Palmer",
	  Calendar.OCTOBER, 9, -Calendar.SUNDAY, 4*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.MARCH, 9, -Calendar.SUNDAY, 3*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	ChileAQ	1999	max	-	Oct	Sun>=9	4:00u	1:00	S
	// Rule	ChileAQ	2000	max	-	Mar	Sun>=9	3:00u	0	-
	// Zone Antarctica/Palmer	-4:00	ChileAQ	CL%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "Atlantic/Bermuda",
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	US	2007	max	-	Mar	Sun>=8	2:00	1:00	D
	// Rule	US	2007	max	-	Nov	Sun>=1	2:00	0	S
	// Zone Atlantic/Bermuda	-4:00	US	A%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "Atlantic/Stanley",
	  Calendar.SEPTEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.APRIL, 15, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	Falk	2001	max	-	Sep	Sun>=1	2:00	1:00	S
	// Rule	Falk	2001	max	-	Apr	Sun>=15	2:00	0	-
	// Zone Atlantic/Stanley	-4:00	Falk	FK%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-4*ONE_HOUR, "PRT" /* America/Puerto_Rico */),
	// Zone PRT	-4:00	-	AST
	//--------------------------------------------------------------------
	new SimpleTimeZone(-(3*ONE_HOUR+30*ONE_MINUTE), "America/St_Johns",
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 1*ONE_MINUTE, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 1*ONE_MINUTE, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	StJohns	2007	max	-	Mar	Sun>=8	0:01	1:00	D
	// Rule	StJohns	2007	max	-	Nov	Sun>=1	0:01	0	S
	// Zone America/St_Johns	-3:30	StJohns	N%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-(3*ONE_HOUR+30*ONE_MINUTE), "CNT" /* America/St_Johns */,
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 1*ONE_MINUTE, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 1*ONE_MINUTE, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	StJohns	2007	max	-	Mar	Sun>=8	0:01	1:00	D
	// Rule	StJohns	2007	max	-	Nov	Sun>=1	0:01	0	S
	// Zone CNT	-3:30	StJohns	N%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-3*ONE_HOUR, "AGT" /* America/Argentina/Buenos_Aires */),
	// Zone AGT	-3:00	-	ART
	//--------------------------------------------------------------------
	new SimpleTimeZone(-3*ONE_HOUR, "America/Argentina/Buenos_Aires"),
	// Zone America/Argentina/Buenos_Aires	-3:00	-	ART
	//--------------------------------------------------------------------
	new SimpleTimeZone(-3*ONE_HOUR, "America/Buenos_Aires" /* America/Argentina/Buenos_Aires */),
	// Zone America/Buenos_Aires	-3:00	-	ART
	//--------------------------------------------------------------------
	new SimpleTimeZone(-3*ONE_HOUR, "America/Cayenne"),
	// Zone America/Cayenne	-3:00	-	GFT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-3*ONE_HOUR, "America/Fortaleza"),
	// Zone America/Fortaleza	-3:00	-	BRT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-3*ONE_HOUR, "America/Godthab",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone America/Godthab	-3:00	EU	WG%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-3*ONE_HOUR, "America/Miquelon",
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	Canada	2007	max	-	Mar	Sun>=8	2:00	1:00	D
	// Rule	Canada	2007	max	-	Nov	Sun>=1	2:00	0	S
	// Zone America/Miquelon	-3:00	Canada	PM%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-3*ONE_HOUR, "America/Montevideo",
	  Calendar.OCTOBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.MARCH, 8, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	Uruguay	2006	max	-	Oct	Sun>=1	 2:00	1:00	S
	// Rule	Uruguay	2007	max	-	Mar	Sun>=8	 2:00	0	-
	// Zone America/Montevideo	-3:00	Uruguay	UY%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-3*ONE_HOUR, "America/Paramaribo"),
	// Zone America/Paramaribo	-3:00	-	SRT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-3*ONE_HOUR, "America/Sao_Paulo",
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 0, SimpleTimeZone.WALL_TIME,
	  Calendar.FEBRUARY, -1, Calendar.SUNDAY, 0, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	Brazil	2006	max	-	Nov	Sun>=1	 0:00	1:00	S
	// Rule	Brazil	2007	max	-	Feb	lastSun	 0:00	0	-
	// Zone America/Sao_Paulo	-3:00	Brazil	BR%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-3*ONE_HOUR, "BET" /* America/Sao_Paulo */,
	  Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 0, SimpleTimeZone.WALL_TIME,
	  Calendar.FEBRUARY, -1, Calendar.SUNDAY, 0, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	Brazil	2006	max	-	Nov	Sun>=1	 0:00	1:00	S
	// Rule	Brazil	2007	max	-	Feb	lastSun	 0:00	0	-
	// Zone BET	-3:00	Brazil	BR%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-2*ONE_HOUR, "America/Noronha"),
	// Zone America/Noronha	-2:00	-	FNT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-2*ONE_HOUR, "Atlantic/South_Georgia"),
	// Zone Atlantic/South_Georgia	-2:00	-	GST	# South Georgia Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(-1*ONE_HOUR, "America/Scoresbysund",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone America/Scoresbysund	-1:00	EU	EG%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-1*ONE_HOUR, "Atlantic/Azores",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Atlantic/Azores	-1:00	EU	AZO%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(-1*ONE_HOUR, "Atlantic/Cape_Verde"),
	// Zone Atlantic/Cape_Verde	-1:00	-	CVT
	//--------------------------------------------------------------------
	new SimpleTimeZone(0, "Africa/Abidjan"),
	// Zone Africa/Abidjan	0:00	-	GMT
	//--------------------------------------------------------------------
	new SimpleTimeZone(0, "Africa/Accra"),
	// Zone Africa/Accra	0:00	Ghana	%s
	//--------------------------------------------------------------------
	new SimpleTimeZone(0, "Africa/Bamako"),
	// Zone Africa/Bamako	0:00	-	GMT
	//--------------------------------------------------------------------
	new SimpleTimeZone(0, "Africa/Banjul"),
	// Zone Africa/Banjul	0:00	-	GMT
	//--------------------------------------------------------------------
	new SimpleTimeZone(0, "Africa/Bissau"),
	// Zone Africa/Bissau	0:00	-	GMT
	//--------------------------------------------------------------------
	new SimpleTimeZone(0, "Africa/Casablanca"),
	// Zone Africa/Casablanca	0:00	-	WET
	//--------------------------------------------------------------------
	new SimpleTimeZone(0, "Africa/Conakry"),
	// Zone Africa/Conakry	0:00	-	GMT
	//--------------------------------------------------------------------
	new SimpleTimeZone(0, "Africa/Dakar"),
	// Zone Africa/Dakar	0:00	-	GMT
	//--------------------------------------------------------------------
	new SimpleTimeZone(0, "Africa/Freetown"),
	// Zone Africa/Freetown	0:00	SL	%s
	//--------------------------------------------------------------------
	new SimpleTimeZone(0, "Africa/Lome"),
	// Zone Africa/Lome	0:00	-	GMT
	//--------------------------------------------------------------------
	new SimpleTimeZone(0, "Africa/Monrovia"),
	// Zone Africa/Monrovia	0:00	-	GMT
	//--------------------------------------------------------------------
	new SimpleTimeZone(0, "Africa/Nouakchott"),
	// Zone Africa/Nouakchott	0:00	-	GMT
	//--------------------------------------------------------------------
	new SimpleTimeZone(0, "Africa/Ouagadougou"),
	// Zone Africa/Ouagadougou	0:00	-	GMT
	//--------------------------------------------------------------------
	new SimpleTimeZone(0, "Africa/Sao_Tome"),
	// Zone Africa/Sao_Tome	0:00	-	GMT
	//--------------------------------------------------------------------
	new SimpleTimeZone(0, "Africa/Timbuktu" /* Africa/Bamako */),
	// Zone Africa/Timbuktu	0:00	-	GMT
	//--------------------------------------------------------------------
	new SimpleTimeZone(0, "Atlantic/Canary",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Atlantic/Canary	0:00	EU	WE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(0, "Atlantic/Faeroe" /* Atlantic/Faroe */,
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Atlantic/Faeroe	0:00	EU	WE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(0, "Atlantic/Faroe",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Atlantic/Faroe	0:00	EU	WE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(0, "Atlantic/Reykjavik"),
	// Zone Atlantic/Reykjavik	0:00	-	GMT
	//--------------------------------------------------------------------
	new SimpleTimeZone(0, "Atlantic/St_Helena"),
	// Zone Atlantic/St_Helena	0:00	-	GMT
	//--------------------------------------------------------------------
	new SimpleTimeZone(0, "Europe/Dublin",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Dublin	0:00	EU	GMT/IST
	//--------------------------------------------------------------------
	new SimpleTimeZone(0, "Europe/Lisbon",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Lisbon	0:00	EU	WE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(0, "Europe/London",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/London	0:00	EU	GMT/BST
	//--------------------------------------------------------------------
	new SimpleTimeZone(0, "GMT"),
	// Zone	GMT		0:00	-	GMT
	//--------------------------------------------------------------------
	new SimpleTimeZone(0, "UTC" /* GMT */),
	// Zone	GMT		0:00	-	GMT
	//--------------------------------------------------------------------
	new SimpleTimeZone(0, "WET",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone	WET		0:00	EU	WE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Africa/Algiers"),
	// Zone Africa/Algiers	1:00	-	CET
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Africa/Bangui"),
	// Zone Africa/Bangui	1:00	-	WAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Africa/Douala"),
	// Zone Africa/Douala	1:00	-	WAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Africa/Kinshasa"),
	// Zone Africa/Kinshasa	1:00	-	WAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Africa/Lagos"),
	// Zone Africa/Lagos	1:00	-	WAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Africa/Libreville"),
	// Zone Africa/Libreville	1:00	-	WAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Africa/Luanda"),
	// Zone Africa/Luanda	1:00	-	WAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Africa/Malabo"),
	// Zone Africa/Malabo	1:00	-	WAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Africa/Ndjamena"),
	// Zone Africa/Ndjamena	1:00	-	WAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Africa/Niamey"),
	// Zone Africa/Niamey	1:00	-	WAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Africa/Porto-Novo"),
	// Zone Africa/Porto-Novo	1:00	-	WAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Africa/Tunis",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule	Tunisia	2006	max	-	Mar	lastSun	 2:00s	1:00	S
	// Rule	Tunisia	2006	max	-	Oct	lastSun	 2:00s	0	-
	// Zone Africa/Tunis	1:00	Tunisia	CE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Africa/Windhoek",
	  Calendar.SEPTEMBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.APRIL, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	Namibia	1994	max	-	Sep	Sun>=1	2:00	1:00	S
	// Rule	Namibia	1995	max	-	Apr	Sun>=1	2:00	0	-
	// Zone Africa/Windhoek	1:00	Namibia	WA%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Atlantic/Jan_Mayen" /* Europe/Oslo */,
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Atlantic/Jan_Mayen	1:00	EU	CE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "ECT" /* Europe/Paris */,
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone ECT	1:00	EU	CE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Europe/Amsterdam",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Amsterdam	1:00	EU	CE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Europe/Andorra",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Andorra	1:00	EU	CE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Europe/Belgrade",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Belgrade	1:00	EU	CE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Europe/Berlin",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Berlin	1:00	EU	CE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Europe/Brussels",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Brussels	1:00	EU	CE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Europe/Budapest",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Budapest	1:00	EU	CE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Europe/Copenhagen",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Copenhagen	1:00	EU	CE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Europe/Gibraltar",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Gibraltar	1:00	EU	CE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Europe/Luxembourg",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Luxembourg	1:00	EU	CE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Europe/Madrid",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Madrid	1:00	EU	CE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Europe/Malta",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Malta	1:00	EU	CE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Europe/Monaco",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Monaco	1:00	EU	CE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Europe/Oslo",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Oslo	1:00	EU	CE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Europe/Paris",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Paris	1:00	EU	CE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Europe/Prague",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Prague	1:00	EU	CE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Europe/Rome",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Rome	1:00	EU	CE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Europe/Stockholm",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Stockholm	1:00	EU	CE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Europe/Tirane",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Tirane	1:00	EU	CE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Europe/Vaduz",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Vaduz	1:00	EU	CE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Europe/Vienna",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Vienna	1:00	EU	CE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Europe/Warsaw",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Warsaw	1:00	EU	CE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(1*ONE_HOUR, "Europe/Zurich",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Zurich	1:00	EU	CE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "ART" /* Africa/Cairo */,
	  Calendar.APRIL, -1, Calendar.FRIDAY, 0, SimpleTimeZone.STANDARD_TIME,
	  Calendar.SEPTEMBER, -1, Calendar.THURSDAY, 23*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule	Egypt	1995	max	-	Apr	lastFri	 0:00s	1:00	S
	// Rule	Egypt	2007	max	-	Sep	lastThu	23:00s	0	-
	// Zone ART	2:00	Egypt	EE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Africa/Blantyre"),
	// Zone Africa/Blantyre	2:00	-	CAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Africa/Bujumbura"),
	// Zone Africa/Bujumbura	2:00	-	CAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Africa/Cairo",
	  Calendar.APRIL, -1, Calendar.FRIDAY, 0, SimpleTimeZone.STANDARD_TIME,
	  Calendar.SEPTEMBER, -1, Calendar.THURSDAY, 23*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule	Egypt	1995	max	-	Apr	lastFri	 0:00s	1:00	S
	// Rule	Egypt	2007	max	-	Sep	lastThu	23:00s	0	-
	// Zone Africa/Cairo	2:00	Egypt	EE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Africa/Gaborone"),
	// Zone Africa/Gaborone	2:00	-	CAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Africa/Harare"),
	// Zone Africa/Harare	2:00	-	CAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Africa/Johannesburg"),
	// Zone Africa/Johannesburg	2:00	SA	SAST
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Africa/Kigali"),
	// Zone Africa/Kigali	2:00	-	CAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Africa/Lubumbashi"),
	// Zone Africa/Lubumbashi	2:00	-	CAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Africa/Lusaka"),
	// Zone Africa/Lusaka	2:00	-	CAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Africa/Maputo"),
	// Zone Africa/Maputo	2:00	-	CAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Africa/Maseru"),
	// Zone Africa/Maseru	2:00	-	SAST
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Africa/Mbabane"),
	// Zone Africa/Mbabane	2:00	-	SAST
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Africa/Tripoli"),
	// Zone Africa/Tripoli	2:00	-	EET
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Asia/Amman",
	  Calendar.MARCH, -1, Calendar.THURSDAY, 0, SimpleTimeZone.STANDARD_TIME,
	  Calendar.OCTOBER, -1, Calendar.FRIDAY, 0, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule	Jordan	2000	max	-	Mar	lastThu	0:00s	1:00	S
	// Rule	Jordan	2006	max	-	Oct	lastFri	0:00s	0	-
	// Zone Asia/Amman	2:00	Jordan	EE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Asia/Beirut",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 0, SimpleTimeZone.WALL_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 0, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	Lebanon	1993	max	-	Mar	lastSun	0:00	1:00	S
	// Rule	Lebanon	1999	max	-	Oct	lastSun	0:00	0	-
	// Zone Asia/Beirut	2:00	Lebanon	EE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Asia/Damascus",
	  Calendar.APRIL, 1, 0, 0, SimpleTimeZone.WALL_TIME,
	  Calendar.OCTOBER, 1, 0, 0, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	Syria	1999	max	-	Apr	 1	0:00	1:00	S
	// Rule	Syria	2007	max	-	Oct	 1	0:00	0	-
	// Zone Asia/Damascus	2:00	Syria	EE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Asia/Jerusalem",
	  Calendar.MARCH, 26, -Calendar.FRIDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.SEPTEMBER, 16, 0, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	Zion	2006	2010	-	Mar	Fri>=26	2:00	1:00	D
	// Rule	Zion	2007	only	-	Sep	16	2:00	0	S
	// Zone Asia/Jerusalem	2:00	Zion	I%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Asia/Nicosia",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EUAsia	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EUAsia	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Asia/Nicosia	2:00	EUAsia	EE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "CAT" /* Africa/Harare */),
	// Zone CAT	2:00	-	CAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "EET",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone	EET		2:00	EU	EE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Europe/Athens",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Athens	2:00	EU	EE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Europe/Bucharest",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Bucharest	2:00	EU	EE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Europe/Chisinau",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Chisinau	2:00	EU	EE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Europe/Helsinki",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Helsinki	2:00	EU	EE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Europe/Istanbul",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Istanbul	2:00	EU	EE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Europe/Kaliningrad",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule	Russia	1993	max	-	Mar	lastSun	 2:00s	1:00	S
	// Rule	Russia	1996	max	-	Oct	lastSun	 2:00s	0	-
	// Zone Europe/Kaliningrad	2:00	Russia	EE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Europe/Kiev",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Kiev	2:00	EU	EE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Europe/Minsk",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule	Russia	1993	max	-	Mar	lastSun	 2:00s	1:00	S
	// Rule	Russia	1996	max	-	Oct	lastSun	 2:00s	0	-
	// Zone Europe/Minsk	2:00	Russia	EE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Europe/Riga",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Riga	2:00	EU	EE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Europe/Simferopol",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Simferopol	2:00	EU	EE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Europe/Sofia",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Sofia	2:00	EU	EE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Europe/Tallinn",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Tallinn	2:00	EU	EE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(2*ONE_HOUR, "Europe/Vilnius",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 1*ONE_HOUR, SimpleTimeZone.UTC_TIME,
	  1*ONE_HOUR),
	// Rule	EU	1981	max	-	Mar	lastSun	 1:00u	1:00	S
	// Rule	EU	1996	max	-	Oct	lastSun	 1:00u	0	-
	// Zone Europe/Vilnius	2:00	EU	EE%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(3*ONE_HOUR, "Africa/Addis_Ababa"),
	// Zone Africa/Addis_Ababa	3:00	-	EAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(3*ONE_HOUR, "Africa/Asmara"),
	// Zone Africa/Asmara	3:00	-	EAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(3*ONE_HOUR, "Africa/Asmera" /* Africa/Asmara */),
	// Zone Africa/Asmera	3:00	-	EAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(3*ONE_HOUR, "Africa/Dar_es_Salaam"),
	// Zone Africa/Dar_es_Salaam	3:00	-	EAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(3*ONE_HOUR, "Africa/Djibouti"),
	// Zone Africa/Djibouti	3:00	-	EAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(3*ONE_HOUR, "Africa/Kampala"),
	// Zone Africa/Kampala	3:00	-	EAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(3*ONE_HOUR, "Africa/Khartoum"),
	// Zone Africa/Khartoum	3:00	-	EAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(3*ONE_HOUR, "Africa/Mogadishu"),
	// Zone Africa/Mogadishu	3:00	-	EAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(3*ONE_HOUR, "Africa/Nairobi"),
	// Zone Africa/Nairobi	3:00	-	EAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(3*ONE_HOUR, "Asia/Aden"),
	// Zone Asia/Aden	3:00	-	AST
	//--------------------------------------------------------------------
	new SimpleTimeZone(3*ONE_HOUR, "Asia/Baghdad",
	  Calendar.APRIL, 1, 0, 3*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  Calendar.OCTOBER, 1, 0, 3*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule	Iraq	1991	max	-	Apr	 1	3:00s	1:00	D
	// Rule	Iraq	1991	max	-	Oct	 1	3:00s	0	S
	// Zone Asia/Baghdad	3:00	Iraq	A%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(3*ONE_HOUR, "Asia/Bahrain"),
	// Zone Asia/Bahrain	3:00	-	AST
	//--------------------------------------------------------------------
	new SimpleTimeZone(3*ONE_HOUR, "Asia/Kuwait"),
	// Zone Asia/Kuwait	3:00	-	AST
	//--------------------------------------------------------------------
	new SimpleTimeZone(3*ONE_HOUR, "Asia/Qatar"),
	// Zone Asia/Qatar	3:00	-	AST
	//--------------------------------------------------------------------
	new SimpleTimeZone(3*ONE_HOUR, "Asia/Riyadh"),
	// Zone Asia/Riyadh	3:00	-	AST
	//--------------------------------------------------------------------
	new SimpleTimeZone(3*ONE_HOUR, "EAT" /* Africa/Addis_Ababa */),
	// Zone EAT	3:00	-	EAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(3*ONE_HOUR, "Europe/Moscow",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule	Russia	1993	max	-	Mar	lastSun	 2:00s	1:00	S
	// Rule	Russia	1996	max	-	Oct	lastSun	 2:00s	0	-
	// Zone Europe/Moscow	3:00	Russia	MSK/MSD
	//--------------------------------------------------------------------
	new SimpleTimeZone(3*ONE_HOUR, "Indian/Antananarivo"),
	// Zone Indian/Antananarivo	3:00	-	EAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(3*ONE_HOUR, "Indian/Comoro"),
	// Zone Indian/Comoro	3:00	-	EAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(3*ONE_HOUR, "Indian/Mayotte"),
	// Zone Indian/Mayotte	3:00	-	EAT
	//--------------------------------------------------------------------
	new SimpleTimeZone(3*ONE_HOUR+30*ONE_MINUTE, "Asia/Tehran"),
	// Zone Asia/Tehran	3:30	Iran	IR%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(3*ONE_HOUR+30*ONE_MINUTE, "MET" /* Asia/Tehran */),
	// Zone MET	3:30	Iran	IR%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(4*ONE_HOUR, "Asia/Baku",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 4*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 5*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	Azer	1997	max	-	Mar	lastSun	 4:00	1:00	S
	// Rule	Azer	1997	max	-	Oct	lastSun	 5:00	0	-
	// Zone Asia/Baku	4:00	Azer	AZ%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(4*ONE_HOUR, "Asia/Dubai"),
	// Zone Asia/Dubai	4:00	-	GST
	//--------------------------------------------------------------------
	new SimpleTimeZone(4*ONE_HOUR, "Asia/Muscat"),
	// Zone Asia/Muscat	4:00	-	GST
	//--------------------------------------------------------------------
	new SimpleTimeZone(4*ONE_HOUR, "Asia/Tbilisi"),
	// Zone Asia/Tbilisi	4:00	-	GET
	//--------------------------------------------------------------------
	new SimpleTimeZone(4*ONE_HOUR, "Asia/Yerevan",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule RussiaAsia	1993	max	-	Mar	lastSun	 2:00s	1:00	S
	// Rule RussiaAsia	1996	max	-	Oct	lastSun	 2:00s	0	-
	// Zone Asia/Yerevan	4:00 RussiaAsia	AM%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(4*ONE_HOUR, "Europe/Samara",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule	Russia	1993	max	-	Mar	lastSun	 2:00s	1:00	S
	// Rule	Russia	1996	max	-	Oct	lastSun	 2:00s	0	-
	// Zone Europe/Samara	4:00	Russia	SAM%sT	# Samara Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(4*ONE_HOUR, "Indian/Mahe"),
	// Zone Indian/Mahe	4:00	-	SCT	# Seychelles Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(4*ONE_HOUR, "Indian/Mauritius"),
	// Zone Indian/Mauritius	4:00	-	MUT	# Mauritius Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(4*ONE_HOUR, "Indian/Reunion"),
	// Zone Indian/Reunion	4:00	-	RET	# Reunion Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(4*ONE_HOUR, "NET" /* Asia/Yerevan */,
	  Calendar.MARCH, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule RussiaAsia	1993	max	-	Mar	lastSun	 2:00s	1:00	S
	// Rule RussiaAsia	1996	max	-	Oct	lastSun	 2:00s	0	-
	// Zone NET	4:00 RussiaAsia	AM%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(4*ONE_HOUR+30*ONE_MINUTE, "Asia/Kabul"),
	// Zone Asia/Kabul	4:30	-	AFT
	//--------------------------------------------------------------------
	new SimpleTimeZone(5*ONE_HOUR, "Asia/Aqtau"),
	// Zone Asia/Aqtau	5:00	-	AQTT
	//--------------------------------------------------------------------
	new SimpleTimeZone(5*ONE_HOUR, "Asia/Aqtobe"),
	// Zone Asia/Aqtobe	5:00	-	AQTT
	//--------------------------------------------------------------------
	new SimpleTimeZone(5*ONE_HOUR, "Asia/Ashgabat"),
	// Zone Asia/Ashgabat	5:00	-	TMT
	//--------------------------------------------------------------------
	new SimpleTimeZone(5*ONE_HOUR, "Asia/Ashkhabad" /* Asia/Ashgabat */),
	// Zone Asia/Ashkhabad	5:00	-	TMT
	//--------------------------------------------------------------------
	new SimpleTimeZone(5*ONE_HOUR, "Asia/Dushanbe"),
	// Zone Asia/Dushanbe	5:00	-	TJT		    # Tajikistan Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(5*ONE_HOUR, "Asia/Karachi"),
	// Zone Asia/Karachi	5:00 Pakistan	PK%sT	# Pakistan Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(5*ONE_HOUR, "Asia/Tashkent"),
	// Zone Asia/Tashkent	5:00	-	UZT
	//--------------------------------------------------------------------
	new SimpleTimeZone(5*ONE_HOUR, "Asia/Yekaterinburg",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule	Russia	1993	max	-	Mar	lastSun	 2:00s	1:00	S
	// Rule	Russia	1996	max	-	Oct	lastSun	 2:00s	0	-
	// Zone Asia/Yekaterinburg	5:00	Russia	YEK%sT	# Yekaterinburg Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(5*ONE_HOUR, "Indian/Kerguelen"),
	// Zone Indian/Kerguelen	5:00	-	TFT	# ISO code TF Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(5*ONE_HOUR, "Indian/Maldives"),
	// Zone Indian/Maldives	5:00	-	MVT		# Maldives Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(5*ONE_HOUR, "PLT" /* Asia/Karachi */),
	// Zone PLT	5:00 Pakistan	PK%sT	# Pakistan Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(5*ONE_HOUR+30*ONE_MINUTE, "Asia/Calcutta"),
	// Zone Asia/Calcutta	5:30	-	IST
	//--------------------------------------------------------------------
	new SimpleTimeZone(5*ONE_HOUR+30*ONE_MINUTE, "Asia/Colombo"),
	// Zone Asia/Colombo	5:30	-	IST
	//--------------------------------------------------------------------
	new SimpleTimeZone(5*ONE_HOUR+30*ONE_MINUTE, "IST" /* Asia/Calcutta */),
	// Zone IST	5:30	-	IST
	//--------------------------------------------------------------------
	new SimpleTimeZone(5*ONE_HOUR+45*ONE_MINUTE, "Asia/Katmandu"),
	// Zone Asia/Katmandu	5:45	-	NPT	# Nepal Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(6*ONE_HOUR, "Antarctica/Mawson"),
	// Zone Antarctica/Mawson	6:00	-	MAWT	# Mawson Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(6*ONE_HOUR, "Asia/Almaty"),
	// Zone Asia/Almaty	6:00	-	ALMT
	//--------------------------------------------------------------------
	new SimpleTimeZone(6*ONE_HOUR, "Asia/Bishkek"),
	// Zone Asia/Bishkek	6:00	-	KGT
	//--------------------------------------------------------------------
	new SimpleTimeZone(6*ONE_HOUR, "Asia/Dacca" /* Asia/Dhaka */),
	// Zone Asia/Dacca	6:00	-	BDT	# Bangladesh Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(6*ONE_HOUR, "Asia/Dhaka"),
	// Zone Asia/Dhaka	6:00	-	BDT	# Bangladesh Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(6*ONE_HOUR, "Asia/Novosibirsk",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule	Russia	1993	max	-	Mar	lastSun	 2:00s	1:00	S
	// Rule	Russia	1996	max	-	Oct	lastSun	 2:00s	0	-
	// Zone Asia/Novosibirsk	6:00	Russia	NOV%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(6*ONE_HOUR, "Asia/Thimbu" /* Asia/Thimphu */),
	// Zone Asia/Thimbu	6:00	-	BTT	# Bhutan Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(6*ONE_HOUR, "Asia/Thimphu"),
	// Zone Asia/Thimphu	6:00	-	BTT	# Bhutan Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(6*ONE_HOUR, "BST" /* Asia/Dhaka */),
	// Zone BST	6:00	-	BDT	# Bangladesh Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(6*ONE_HOUR, "Indian/Chagos"),
	// Zone Indian/Chagos	6:00	-	IOT
	//--------------------------------------------------------------------
	new SimpleTimeZone(6*ONE_HOUR+30*ONE_MINUTE, "Asia/Rangoon"),
	// Zone Asia/Rangoon	6:30	-	MMT		   # Myanmar Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(6*ONE_HOUR+30*ONE_MINUTE, "Indian/Cocos"),
	// Zone Indian/Cocos	6:30	-	CCT	# Cocos Islands Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(7*ONE_HOUR, "Asia/Bangkok"),
	// Zone Asia/Bangkok	7:00	-	ICT
	//--------------------------------------------------------------------
	new SimpleTimeZone(7*ONE_HOUR, "Asia/Jakarta"),
	// Zone Asia/Jakarta	7:00	-	WIT
	//--------------------------------------------------------------------
	new SimpleTimeZone(7*ONE_HOUR, "Asia/Krasnoyarsk",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule	Russia	1993	max	-	Mar	lastSun	 2:00s	1:00	S
	// Rule	Russia	1996	max	-	Oct	lastSun	 2:00s	0	-
	// Zone Asia/Krasnoyarsk	7:00	Russia	KRA%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(7*ONE_HOUR, "Asia/Phnom_Penh"),
	// Zone Asia/Phnom_Penh	7:00	-	ICT
	//--------------------------------------------------------------------
	new SimpleTimeZone(7*ONE_HOUR, "Asia/Saigon"),
	// Zone Asia/Saigon	7:00	-	ICT
	//--------------------------------------------------------------------
	new SimpleTimeZone(7*ONE_HOUR, "Asia/Vientiane"),
	// Zone Asia/Vientiane	7:00	-	ICT
	//--------------------------------------------------------------------
	new SimpleTimeZone(7*ONE_HOUR, "Indian/Christmas"),
	// Zone Indian/Christmas	7:00	-	CXT	# Christmas Island Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(7*ONE_HOUR, "VST" /* Asia/Saigon */),
	// Zone VST	7:00	-	ICT
	//--------------------------------------------------------------------
	new SimpleTimeZone(8*ONE_HOUR, "Antarctica/Casey"),
	// Zone Antarctica/Casey	8:00	-	WST	# Western (Aus) Standard Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(8*ONE_HOUR, "Asia/Brunei"),
	// Zone Asia/Brunei	8:00	-	BNT
	//--------------------------------------------------------------------
	new SimpleTimeZone(8*ONE_HOUR, "Asia/Hong_Kong"),
	// Zone Asia/Hong_Kong	8:00	HK	HK%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(8*ONE_HOUR, "Asia/Irkutsk",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule	Russia	1993	max	-	Mar	lastSun	 2:00s	1:00	S
	// Rule	Russia	1996	max	-	Oct	lastSun	 2:00s	0	-
	// Zone Asia/Irkutsk	8:00	Russia	IRK%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(8*ONE_HOUR, "Asia/Kuala_Lumpur"),
	// Zone Asia/Kuala_Lumpur	8:00	-	MYT	# Malaysia Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(8*ONE_HOUR, "Asia/Macao" /* Asia/Macau */),
	// Zone Asia/Macao	8:00	PRC	C%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(8*ONE_HOUR, "Asia/Macau"),
	// Zone Asia/Macau	8:00	PRC	C%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(8*ONE_HOUR, "Asia/Makassar"),
	// Zone Asia/Makassar	8:00	-	CIT
	//--------------------------------------------------------------------
	new SimpleTimeZone(8*ONE_HOUR, "Asia/Manila"),
	// Zone Asia/Manila	8:00	Phil	PH%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(8*ONE_HOUR, "Asia/Shanghai"),
	// Zone Asia/Shanghai	8:00	PRC	C%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(8*ONE_HOUR, "Asia/Singapore"),
	// Zone Asia/Singapore	8:00	-	SGT
	//--------------------------------------------------------------------
	new SimpleTimeZone(8*ONE_HOUR, "Asia/Taipei"),
	// Zone Asia/Taipei	8:00	Taiwan	C%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(8*ONE_HOUR, "Asia/Ujung_Pandang" /* Asia/Makassar */),
	// Zone Asia/Ujung_Pandang	8:00	-	CIT
	//--------------------------------------------------------------------
	new SimpleTimeZone(8*ONE_HOUR, "Asia/Ulaanbaatar",
	  Calendar.MARCH, -1, Calendar.SATURDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.SEPTEMBER, -1, Calendar.SATURDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	Mongol	2002	max	-	Mar	lastSat	2:00	1:00	S
	// Rule	Mongol	2001	max	-	Sep	lastSat	2:00	0	-
	// Zone Asia/Ulaanbaatar	8:00	Mongol	ULA%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(8*ONE_HOUR, "Asia/Ulan_Bator" /* Asia/Ulaanbaatar */,
	  Calendar.MARCH, -1, Calendar.SATURDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.SEPTEMBER, -1, Calendar.SATURDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  1*ONE_HOUR),
	// Rule	Mongol	2002	max	-	Mar	lastSat	2:00	1:00	S
	// Rule	Mongol	2001	max	-	Sep	lastSat	2:00	0	-
	// Zone Asia/Ulan_Bator	8:00	Mongol	ULA%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(8*ONE_HOUR, "Australia/Perth",
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  Calendar.MARCH, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule	AW	2007	2008	-	Oct	lastSun	2:00s	1:00	-
	// Rule	AW	2007	2009	-	Mar	lastSun	2:00s	0	-
	// Zone Australia/Perth	8:00	AW	WST
	//--------------------------------------------------------------------
	new SimpleTimeZone(8*ONE_HOUR, "CTT" /* Asia/Shanghai */),
	// Zone CTT	8:00	PRC	C%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(8*ONE_HOUR+45*ONE_MINUTE, "Australia/Eucla",
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  Calendar.MARCH, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule	AW	2007	2008	-	Oct	lastSun	2:00s	1:00	-
	// Rule	AW	2007	2009	-	Mar	lastSun	2:00s	0	-
	// Zone Australia/Eucla	8:45	AW	CWST
	//--------------------------------------------------------------------
	new SimpleTimeZone(9*ONE_HOUR, "Asia/Jayapura"),
	// Zone Asia/Jayapura	9:00	-	EIT
	//--------------------------------------------------------------------
	new SimpleTimeZone(9*ONE_HOUR, "Asia/Pyongyang"),
	// Zone Asia/Pyongyang	9:00	-	KST
	//--------------------------------------------------------------------
	new SimpleTimeZone(9*ONE_HOUR, "Asia/Seoul"),
	// Zone Asia/Seoul	9:00	ROK	K%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(9*ONE_HOUR, "Asia/Tokyo"),
	// Zone Asia/Tokyo	9:00	Japan	J%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(9*ONE_HOUR, "Asia/Yakutsk",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule	Russia	1993	max	-	Mar	lastSun	 2:00s	1:00	S
	// Rule	Russia	1996	max	-	Oct	lastSun	 2:00s	0	-
	// Zone Asia/Yakutsk	9:00	Russia	YAK%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(9*ONE_HOUR, "JST" /* Asia/Tokyo */),
	// Zone JST	9:00	Japan	J%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(9*ONE_HOUR, "Pacific/Palau"),
	// Zone Pacific/Palau	9:00	-	PWT	# Palau Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(9*ONE_HOUR+30*ONE_MINUTE, "ACT" /* Australia/Darwin */),
	// Zone ACT	9:30	Aus	CST
	//--------------------------------------------------------------------
	new SimpleTimeZone(9*ONE_HOUR+30*ONE_MINUTE, "Australia/Adelaide",
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  Calendar.MARCH, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule	AS	1987	max	-	Oct	lastSun	2:00s	1:00	-
	// Rule	AS	2007	max	-	Mar	lastSun	2:00s	0	-
	// Zone Australia/Adelaide	9:30	AS	CST
	//--------------------------------------------------------------------
	new SimpleTimeZone(9*ONE_HOUR+30*ONE_MINUTE, "Australia/Broken_Hill",
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  Calendar.MARCH, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule	AS	1987	max	-	Oct	lastSun	2:00s	1:00	-
	// Rule	AS	2007	max	-	Mar	lastSun	2:00s	0	-
	// Zone Australia/Broken_Hill	9:30	AS	CST
	//--------------------------------------------------------------------
	new SimpleTimeZone(9*ONE_HOUR+30*ONE_MINUTE, "Australia/Darwin"),
	// Zone Australia/Darwin	9:30	Aus	CST
	//--------------------------------------------------------------------
	new SimpleTimeZone(10*ONE_HOUR, "AET" /* Australia/Sydney */,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  Calendar.MARCH, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule	AN	2001	max	-	Oct	lastSun	2:00s	1:00	-
	// Rule	AN	2007	max	-	Mar	lastSun	2:00s	0	-
	// Zone AET	10:00	AN	EST
	//--------------------------------------------------------------------
	new SimpleTimeZone(10*ONE_HOUR, "Antarctica/DumontDUrville"),
	// Zone Antarctica/DumontDUrville	10:00	-	DDUT	# Dumont-d'Urville Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(10*ONE_HOUR, "Asia/Vladivostok",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule	Russia	1993	max	-	Mar	lastSun	 2:00s	1:00	S
	// Rule	Russia	1996	max	-	Oct	lastSun	 2:00s	0	-
	// Zone Asia/Vladivostok	10:00	Russia	VLA%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(10*ONE_HOUR, "Australia/Brisbane"),
	// Zone Australia/Brisbane	10:00	AQ	EST
	//--------------------------------------------------------------------
	new SimpleTimeZone(10*ONE_HOUR, "Australia/Hobart",
	  Calendar.OCTOBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  Calendar.MARCH, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule	AT	2001	max	-	Oct	Sun>=1	2:00s	1:00	-
	// Rule	AT	2007	max	-	Mar	lastSun	2:00s	0	-
	// Zone Australia/Hobart	10:00	AT	EST
	//--------------------------------------------------------------------
	new SimpleTimeZone(10*ONE_HOUR, "Australia/Sydney",
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  Calendar.MARCH, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule	AN	2001	max	-	Oct	lastSun	2:00s	1:00	-
	// Rule	AN	2007	max	-	Mar	lastSun	2:00s	0	-
	// Zone Australia/Sydney	10:00	AN	EST
	//--------------------------------------------------------------------
	new SimpleTimeZone(10*ONE_HOUR, "Pacific/Guam"),
	// Zone Pacific/Guam	10:00	-	ChST	# Chamorro Standard Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(10*ONE_HOUR, "Pacific/Port_Moresby"),
	// Zone Pacific/Port_Moresby	10:00	-	PGT		# Papua New Guinea Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(10*ONE_HOUR, "Pacific/Saipan"),
	// Zone Pacific/Saipan	10:00	-	ChST	# Chamorro Standard Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(10*ONE_HOUR, "Pacific/Truk"),
	// Zone Pacific/Truk	10:00	-	TRUT			# Truk Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(10*ONE_HOUR+30*ONE_MINUTE, "Australia/Lord_Howe",
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  Calendar.MARCH, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.WALL_TIME,
	  30*ONE_MINUTE),
	// Rule	LH	2001	max	-	Oct	lastSun	2:00	0:30	-
	// Rule	LH	2007	max	-	Mar	lastSun	2:00	0	-
	// Zone Australia/Lord_Howe	10:30	LH	LHST
	//--------------------------------------------------------------------
	new SimpleTimeZone(11*ONE_HOUR, "Asia/Magadan",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule	Russia	1993	max	-	Mar	lastSun	 2:00s	1:00	S
	// Rule	Russia	1996	max	-	Oct	lastSun	 2:00s	0	-
	// Zone Asia/Magadan	11:00	Russia	MAG%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(11*ONE_HOUR, "Pacific/Efate"),
	// Zone Pacific/Efate	11:00	Vanuatu	VU%sT	# Vanuatu Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(11*ONE_HOUR, "Pacific/Guadalcanal"),
	// Zone Pacific/Guadalcanal	11:00	-	SBT	# Solomon Is Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(11*ONE_HOUR, "Pacific/Kosrae"),
	// Zone Pacific/Kosrae	11:00	-	KOST
	//--------------------------------------------------------------------
	new SimpleTimeZone(11*ONE_HOUR, "Pacific/Noumea"),
	// Zone Pacific/Noumea	11:00	NC	NC%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(11*ONE_HOUR, "Pacific/Ponape"),
	// Zone Pacific/Ponape	11:00	-	PONT			# Ponape Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(11*ONE_HOUR, "SST" /* Pacific/Guadalcanal */),
	// Zone SST	11:00	-	SBT	# Solomon Is Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(11*ONE_HOUR+30*ONE_MINUTE, "Pacific/Norfolk"),
	// Zone Pacific/Norfolk	11:30	-	NFT		# Norfolk Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(12*ONE_HOUR, "Antarctica/McMurdo",
	  Calendar.OCTOBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  Calendar.MARCH, 15, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule	NZAQ	1990	max	-	Oct	Sun>=1	2:00s	1:00	D
	// Rule	NZAQ	1990	max	-	Mar	Sun>=15	2:00s	0	S
	// Zone Antarctica/McMurdo	12:00	NZAQ	NZ%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(12*ONE_HOUR, "Asia/Anadyr",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule	Russia	1993	max	-	Mar	lastSun	 2:00s	1:00	S
	// Rule	Russia	1996	max	-	Oct	lastSun	 2:00s	0	-
	// Zone Asia/Anadyr	12:00	Russia	ANA%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(12*ONE_HOUR, "Asia/Kamchatka",
	  Calendar.MARCH, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule	Russia	1993	max	-	Mar	lastSun	 2:00s	1:00	S
	// Rule	Russia	1996	max	-	Oct	lastSun	 2:00s	0	-
	// Zone Asia/Kamchatka	12:00	Russia	PET%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(12*ONE_HOUR, "NST" /* Pacific/Auckland */,
	  Calendar.OCTOBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  Calendar.MARCH, 15, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule	NZ	1990	max	-	Oct	Sun>=1	2:00s	1:00	D
	// Rule	NZ	1990	max	-	Mar	Sun>=15	2:00s	0	S
	// Zone NST	12:00	NZ	NZ%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(12*ONE_HOUR, "Pacific/Auckland",
	  Calendar.OCTOBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  Calendar.MARCH, 15, -Calendar.SUNDAY, 2*ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule	NZ	1990	max	-	Oct	Sun>=1	2:00s	1:00	D
	// Rule	NZ	1990	max	-	Mar	Sun>=15	2:00s	0	S
	// Zone Pacific/Auckland	12:00	NZ	NZ%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(12*ONE_HOUR, "Pacific/Fiji"),
	// Zone Pacific/Fiji	12:00	Fiji	FJ%sT	# Fiji Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(12*ONE_HOUR, "Pacific/Funafuti"),
	// Zone Pacific/Funafuti	12:00	-	TVT	# Tuvalu Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(12*ONE_HOUR, "Pacific/Majuro"),
	// Zone Pacific/Majuro	12:00	-	MHT
	//--------------------------------------------------------------------
	new SimpleTimeZone(12*ONE_HOUR, "Pacific/Nauru"),
	// Zone Pacific/Nauru	12:00	-	NRT
	//--------------------------------------------------------------------
	new SimpleTimeZone(12*ONE_HOUR, "Pacific/Tarawa"),
	// Zone Pacific/Tarawa	12:00	-	GILT		 # Gilbert Is Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(12*ONE_HOUR, "Pacific/Wake"),
	// Zone Pacific/Wake	12:00	-	WAKT	# Wake Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(12*ONE_HOUR, "Pacific/Wallis"),
	// Zone Pacific/Wallis	12:00	-	WFT	# Wallis & Futuna Time
	//--------------------------------------------------------------------
	new SimpleTimeZone(12*ONE_HOUR+45*ONE_MINUTE, "Pacific/Chatham",
	  Calendar.OCTOBER, 1, -Calendar.SUNDAY, 2*ONE_HOUR+45*ONE_MINUTE, SimpleTimeZone.STANDARD_TIME,
	  Calendar.MARCH, 15, -Calendar.SUNDAY, 2*ONE_HOUR+45*ONE_MINUTE, SimpleTimeZone.STANDARD_TIME,
	  1*ONE_HOUR),
	// Rule	Chatham	1990	max	-	Oct	Sun>=1	2:45s	1:00	D
	// Rule	Chatham	1990	max	-	Mar	Sun>=15	2:45s	0	S
	// Zone Pacific/Chatham	12:45	Chatham	CHA%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(13*ONE_HOUR, "Pacific/Enderbury"),
	// Zone Pacific/Enderbury	13:00	-	PHOT
	//--------------------------------------------------------------------
	new SimpleTimeZone(13*ONE_HOUR, "Pacific/Tongatapu"),
	// Zone Pacific/Tongatapu	13:00	Tonga	TO%sT
	//--------------------------------------------------------------------
	new SimpleTimeZone(14*ONE_HOUR, "Pacific/Kiritimati"),
	// Zone Pacific/Kiritimati	14:00	-	LINT
    };

    // ---------------- END GENERATED DATA ----------------

    private static Hashtable lookup = new Hashtable(zones.length);

    static {
        for (int i=0; i < zones.length; ++i)
            lookup.put(zones[i].getID(), zones[i]);
	TimeZone.getDefault(); // to cache default system time zone
    }
}

//eof
