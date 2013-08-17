/*
 * @(#)TimeZone.java	1.29 99/01/25
 *
 * (C) Copyright Taligent, Inc. 1996 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - All Rights Reserved
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
import java.io.Serializable;

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
 * You can also get a <code>TimeZone</code> using <code>getTimeZone</code> along
 * with a time zone ID. For instance, the time zone ID for the Pacific
 * Standard Time zone is "PST". So, you can get a PST <code>TimeZone</code> object
 * with:
 * <blockquote>
 * <pre>
 * TimeZone tz = TimeZone.getTimeZone("PST");
 * </pre>
 * </blockquote>
 * You can use <code>getAvailableIDs</code> method to iterate through
 * all the supported time zone IDs. You can then choose a
 * supported ID to get a favorite <code>TimeZone</code>.
 *
 * @see          Calendar
 * @see          GregorianCalendar
 * @see          SimpleTimeZone
 * @version      1.29 01/25/99
 * @author       Mark Davis, David Goldsmith, Chen-Lieh Huang, Alan Liu
 */
abstract public class TimeZone implements Serializable, Cloneable {

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
        this.ID = ID;
    }

    /**
     * Queries if this time zone uses Daylight Savings Time.
     * @return true if this time zone uses Daylight Savings Time,
     * false, otherwise.
     */
    abstract public boolean useDaylightTime();

    /**
     * Queries if the given date is in Daylight Savings Time in
     * this time zone.
     * @param date the given Date.
     * @return true if the given date is in Daylight Savings Time,
     * false, otherwise.
     */
    abstract public boolean inDaylightTime(Date date);

    /**
     * Gets the TimeZone for the given ID.
     * @param ID the given ID.
     * @return a TimeZone, or null if the given ID is not recognized.
     */
    public static synchronized TimeZone getTimeZone(String ID)
    {
        // Don't allow long IDs yet
        TimeZone zone = (ID.length() <= 3) ? TimeZoneData.get(ID) : null;
        return zone != null ? zone : TimeZoneData.get(DEFAULT_SHORT_ID);
    }

    /**
     * Gets the available IDs according to the given time zone offset.
     * @param rawOffset the given time zone GMT offset.
     * @return an array of IDs, where the time zone for that ID has
     * the specified GMT offset. For example, "America/Phoenix" and "America/Denver"
     * both have GMT-07:00, but differ in daylight savings behavior.
     */
    public static synchronized String[] getAvailableIDs(int rawOffset) {
        String[]    resultArray = new String[TimeZoneData.MAXIMUM_ZONES_PER_OFFSET];
        int         count = 0;
        for (int i = 0; i < TimeZoneData.zones.length; ++i) {
            if (rawOffset == TimeZoneData.zones[i].getRawOffset() &&
                TimeZoneData.zones[i].getID().length() <= 3) // Hide long IDs
                resultArray[count++] = TimeZoneData.zones[i].getID();
        }

        // copy into array of the right size and return
        String[] finalResult = new String[count];
        System.arraycopy(resultArray, 0, finalResult, 0, count);

        return finalResult;
    }

    /**
     * Gets all the available IDs supported.
     * @return an array of IDs.
     */
    public static synchronized String[] getAvailableIDs() {
        String[]    resultArray = new String[TimeZoneData.zones.length];
        int         count = 0;
        for (int i = 0; i < TimeZoneData.zones.length; ++i)
            if (TimeZoneData.zones[i].getID().length() <= 3) // Hide long IDs
                resultArray[count++] = TimeZoneData.zones[i].getID();

        // copy into array of the right size and return
        String[] finalResult = new String[count];
        System.arraycopy(resultArray, 0, finalResult, 0, count);

        return finalResult;
    }
    
    /**
     * Gets the default TimeZone for this host.
     * @return a default TimeZone.
     */
    public static synchronized TimeZone getDefault() {
        if (defaultZone == null) {
            // get the ID from the system properties, and translate the
            // 3-letter code to a full TimeZone id.
            String ID = System.getProperty("user.timezone", DEFAULT_SHORT_ID);
            String remappedID = (String)idlookup.get(ID);
            if (remappedID != null) ID = remappedID;
            // The ID will only be null at this point if the user has set
            // user.timezone to an invalid value.
            if (ID == null) ID = DEFAULT_ID;
            ID = TimeZoneData.mapLongIDtoShortID(ID); // For compatibility with 1.1 FCS
            defaultZone = getTimeZone(ID);
        }
        return (TimeZone)defaultZone.clone();
    }

    /**
     * Sets time zone to using the given TimeZone.
     * @param zone the given time zone.
     */
    public static synchronized void setDefault(TimeZone zone)
    {
        defaultZone = zone;
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

    private String           ID;
    private static TimeZone  defaultZone = null;

    // These are the default IDs for timezones; we use these if we can't get
    // the host timezone data, or if we don't recognize it.
    private static final String DEFAULT_SHORT_ID = "GMT";
    private static final String DEFAULT_ID       = "Africa/Casablanca";

    /**
     * This array maps from the user.timezone 3-letter ID to a usable
     * TimeZone ID.
     *
     * It also contains mappings from zones we don't support to similar zones
     * that we do support.  These zones DST rules which differ from the zone
     * they are approximating, but have the same raw offset.
     *
     * This table is used only to resolve the user.timezone name to a default
     * zone.
     */
    private static final String[] idMap =
    {
        // Windows name               user.timezone ID   TimeZone ID
        // ------------               ----------------   -----------
        /* "GMT Standard Time",              */  "GMT",  "Africa/Casablanca",
        /* "Romance Standard Time",          */  "ECT",  "Europe/Paris",
        /* "Egypt Standard Time",            */  "EET",  "Africa/Cairo",
        /* "Saudi Arabia Standard Time",     */  "EAT",  "Asia/Riyadh",
        /* "Iran Standard Time",             */  "MET",  "Asia/Tehran",
        /* "Arabian Standard Time",          */  "NET",  "Asia/Yerevan",
        /* "West Asia Standard Time",        */  "PLT",  "Asia/Karachi",
        /* "India Standard Time",            */  "IST",  "Asia/Calcutta",
        /* "Central Asia Standard Time",     */  "BST",  "Asia/Dacca",
        /* "Bangkok Standard Time",          */  "VST",  "Asia/Bangkok",
        /* "China Standard Time",            */  "CTT",  "Asia/Shanghai",
        /* "Tokyo Standard Time",            */  "JST",  "Asia/Tokyo",
        /* "Cen. Australia Standard Time",   */  "ACT",  "Australia/Darwin",
        /* "Sydney Standard Time",           */  "AET",  "Australia/Sydney",
        /* "Central Pacific Standard Time",  */  "SST",  "Pacific/Guadalcanal",
        /* "New Zealand Standard Time",      */  "NST",  "Pacific/Fiji",
        /* "Samoa Standard Time",            */  "MIT",  "Pacific/Apia",
        /* "Hawaiian Standard Time",         */  "HST",  "Pacific/Honolulu",
        /* "Alaskan Standard Time",          */  "AST",  "America/Anchorage",
        /* "Pacific Standard Time",          */  "PST",  "America/Los_Angeles",
        /* "US Mountain Standard Time",      */  "MST",  "America/Denver",
        /* "Central Standard Time",          */  "CST",  "America/Chicago",
        /* "Eastern Standard Time",          */  "EST",  "America/New_York",
        /* "Atlantic Standard Time",         */  "PRT",  "America/Caracas",
        /* "Newfoundland Standard Time",     */  "CNT",  "America/St_Johns",
        /* "SA Eastern Standard Time",       */  "AGT",  "America/Buenos_Aires",
        /* "E. South America Standard Time", */  "BET",  "America/Sao_Paulo",
        /* "Azores Standard Time",           */  "CAT",  "Atlantic/Cape_Verde",

      /* user.timezone name      approximation (same offset, different rules) */
        "America/Costa_Rica",   "America/Chicago",   /* GMT-6 */
        "Asia/Beirut",          "Europe/Istanbul",   /* GMT+2 */
        "Africa/Johannesburg",  "Africa/Cairo",      /* GMT+2 */
        "Asia/Jerusalem",       "Europe/Istanbul",   /* GMT+2 */
        "Europe/Moscow",        "Asia/Riyadh",       /* GMT+3 */
        "Asia/Vladivostok",     "Australia/Sydney",  /* GMT+10 */
        "Australia/Hobart",     "Australia/Sydney",  /* GMT+10 */
    };

    private static Hashtable idlookup = new Hashtable(idMap.length / 2);

    static {
        for (int i=0; i<idMap.length; i+=2)
        {
            if (false)
            {
                // Debugging code
                if (TimeZoneData.get(idMap[i+1]) == null)
                    System.out.println("*** Bad TimeZone.idMap at " + i);
                if (idlookup.get(idMap[i]) != null)
                    System.out.println("*** Duplicate idMap " + idMap[i]);
            }
            idlookup.put(idMap[i], idMap[i+1]);
        }
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
    // The following value must be set to the maximum number of zones sharing
    // a single base offset value.
    static final int MAXIMUM_ZONES_PER_OFFSET = 13;

    static final TimeZone get(String ID)
    {
        Object o = lookup.get(ID);
        return o == null ? null : (TimeZone)((TimeZone)o).clone(); // [sic]
    }

    private static final int millisPerHour = 60*60*1000;

    static SimpleTimeZone[] zones =
    {
        // The following data is current as of 1997
        //----------------------------------------------------------
        new SimpleTimeZone(-11 * millisPerHour, "Pacific/Apia" /*WST*/),
        // Pacific/Apia W Samoa -11:00  -       WST     # W Samoa Time
        // Pacific/Midway       ?       -11:00  -       SST     # S=Samoa
        // Pacific/Niue Niue    -11:00  -       NUT
        // Pacific/Pago_Pago    American Samoa  -11:00  -       SST     # S=Samoa
        //----------------------------------------------------------
        new SimpleTimeZone(-10 * millisPerHour, "Pacific/Honolulu" /*HST*/),
        // Pacific/Honolulu     Hawaii  -10:00  -       HST
        // Pacific/Fakaofo      Tokelau Is      -10:00  -       TKT     # Tokelau Time
        // Pacific/Johnston     Johnston        -10:00  -       HST
        // Pacific/Tahiti       French Polynesia        -10:00  -       TAHT    # Tahiti Time
        //----------------------------------------------------------
//        new SimpleTimeZone(-10 * millisPerHour, "America/Adak" /*HA%sT*/,
//                Calendar.APRIL, 1, -Calendar.SUNDAY /*DOW>=DOM*/, 2 * millisPerHour,
//                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 2 * millisPerHour, 1 * millisPerHour),
        // Rule US      1967    max     -       Oct     lastSun 2:00    0       S
        // Rule US      1987    max     -       Apr     Sun>=1  2:00    1:00    D
        // America/Adak Alaska  -10:00  US      HA%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(-10 * millisPerHour, "Pacific/Rarotonga" /*CK%sT*/,
//                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 0 * millisPerHour,
//                Calendar.MARCH, 1, -Calendar.SUNDAY /*DOW>=DOM*/, 0 * millisPerHour, (int)(0.5 * millisPerHour)),
        // Rule Cook    1979    max     -       Mar     Sun>=1  0:00    0       -
        // Rule Cook    1979    max     -       Oct     lastSun 0:00    0:30    HS
        // Pacific/Rarotonga    Cook Is -10:00  Cook    CK%sT
        //----------------------------------------------------------
//        new SimpleTimeZone((int)(-9.5 * millisPerHour), "Pacific/Marquesas" /*MART*/),
        // Pacific/Marquesas    French Polynesia        -9:30   -       MART    # Marquesas Time
        //----------------------------------------------------------
//        new SimpleTimeZone(-9 * millisPerHour, "Pacific/Gambier" /*GAMT*/),
        // Pacific/Gambier      French Polynesia        -9:00   -       GAMT    # Gambier Time
        //----------------------------------------------------------
        new SimpleTimeZone(-9 * millisPerHour, "America/Anchorage" /*AK%sT*/,
                Calendar.APRIL, 1, -Calendar.SUNDAY /*DOW>=DOM*/, 2 * millisPerHour,
                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 2 * millisPerHour, 1 * millisPerHour),
        // Rule US      1967    max     -       Oct     lastSun 2:00    0       S
        // Rule US      1987    max     -       Apr     Sun>=1  2:00    1:00    D
        // America/Anchorage    Alaska  -9:00   US      AK%sT
        // America/Juneau       Alaska  -9:00   US      AK%sT
        // America/Nome Alaska  -9:00   US      AK%sT
        // America/Yakutat      Alaska  -9:00   US      AK%sT
        //----------------------------------------------------------
//        new SimpleTimeZone((int)(-8.5 * millisPerHour), "Pacific/Pitcairn" /*PNT*/),
        // Pacific/Pitcairn     Pitcairn        -8:30   -       PNT     # Pitcairn Time
        //----------------------------------------------------------
        new SimpleTimeZone(-8 * millisPerHour, "America/Los_Angeles" /*P%sT*/,
                Calendar.APRIL, 1, -Calendar.SUNDAY /*DOW>=DOM*/, 2 * millisPerHour,
                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 2 * millisPerHour, 1 * millisPerHour),
        // Rule US      1967    max     -       Oct     lastSun 2:00    0       S
        // Rule US      1987    max     -       Apr     Sun>=1  2:00    1:00    D
        // America/Los_Angeles  US Pacific time, represented by Los Angeles     -8:00   US      P%sT
        // America/Ensenada     Mexico  -8:00   Mexico  P%sT
        // America/Tijuana      Mexico  -8:00   Mexico  P%sT
        // America/Dawson       Northwest Territories, Yukon    -8:00   NT_YK   P%sT
        // America/Whitehorse   Northwest Territories, Yukon    -8:00   NT_YK   P%sT
        // America/Vancouver    British Columbia        -8:00   Vanc    P%sT
        //----------------------------------------------------------
        new SimpleTimeZone(-7 * millisPerHour, "America/Phoenix" /*MST*/),
        // America/Phoenix      ?       -7:00   -       MST
        // America/Dawson_Creek British Columbia        -7:00   -       MST
        //----------------------------------------------------------
        new SimpleTimeZone(-7 * millisPerHour, "America/Denver" /*M%sT*/,
                Calendar.APRIL, 1, -Calendar.SUNDAY /*DOW>=DOM*/, 2 * millisPerHour,
                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 2 * millisPerHour, 1 * millisPerHour),
        // Rule US      1967    max     -       Oct     lastSun 2:00    0       S
        // Rule US      1987    max     -       Apr     Sun>=1  2:00    1:00    D
        // America/Denver       US Mountain time, represented by Denver -7:00   US      M%sT
        // America/Edmonton     Alberta -7:00   Edm     M%sT
        // America/Mazatlan     Mexico  -7:00   Mexico  M%sT
        // America/Inuvik       Northwest Territories, Yukon    -7:00   NT_YK   M%sT
        // America/Yellowknife  Northwest Territories, Yukon    -7:00   NT_YK   M%sT
        // America/Boise        ?       -7:00   US      M%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(-6 * millisPerHour, "America/Costa_Rica" /*C%sT*/),
        // America/Costa_Rica   Costa Rica      -6:00   -       C%sT
        // America/Belize       Belize  -6:00   -       C%sT
        // America/El_Salvador  El Salvador     -6:00   -       C%sT
        // America/Guatemala    Guatemala       -6:00   -       C%sT
        // America/Regina       Saskatchewan    -6:00   -       CST
        // America/Swift_Current        Saskatchewan    -6:00   -       CST
        // America/Tegucigalpa  Honduras        -6:00   -       C%sT
        // Pacific/Galapagos    Ecuador -6:00   -       GALT    # Galapagos Time
        //----------------------------------------------------------
        new SimpleTimeZone(-6 * millisPerHour, "America/Chicago" /*C%sT*/,
                Calendar.APRIL, 1, -Calendar.SUNDAY /*DOW>=DOM*/, 2 * millisPerHour,
                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 2 * millisPerHour, 1 * millisPerHour),
        // Rule US      1967    max     -       Oct     lastSun 2:00    0       S
        // Rule US      1987    max     -       Apr     Sun>=1  2:00    1:00    D
        // America/Chicago      US Central time, represented by Chicago -6:00   US      C%sT
        // America/Rainy_River  Ontario, Quebec -6:00   Canada  C%sT
        // America/Mexico_City  Mexico  -6:00   Mexico  C%sT
        // America/Rankin_Inlet Northwest Territories, Yukon    -6:00   NT_YK   C%sT
        // America/Menominee    Michigan        -6:00   US      C%sT
        // America/Winnipeg     Manitoba        -6:00   Winn    C%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(-6 * millisPerHour, "Pacific/Easter" /*EAS%sT*/,
//                Calendar.OCTOBER, 9, -Calendar.SUNDAY /*DOW>=DOM*/, 0 * millisPerHour,
//                Calendar.MARCH, 9, -Calendar.SUNDAY /*DOW>=DOM*/, 0 * millisPerHour, 1 * millisPerHour),
        // Rule Chile   1969    max     -       Oct     Sun>=9  0:00    1:00    S
        // Rule Chile   1970    max     -       Mar     Sun>=9  0:00    0       -
        // Pacific/Easter       Chile   -6:00   Chile   EAS%sT
        //----------------------------------------------------------
        new SimpleTimeZone(-5 * millisPerHour, "America/Indianapolis" /*EST*/),
        // America/Indianapolis Indiana -5:00   -       EST
        // America/Bogota       Colombia        -5:00   -       CO%sT   # Colombia Time
        // America/Cayman       Cayman Is       -5:00   -       EST
        // America/Guayaquil    Ecuador -5:00   -       ECT     # Ecuador Time
        // America/Indiana/Knox Indiana -5:00   -       EST
        // America/Indiana/Marengo      Indiana -5:00   -       EST
        // America/Indiana/Vevay        Indiana -5:00   -       EST
        // America/Jamaica      Jamaica -5:00   -       EST
        // America/Lima Peru    -5:00   -       PE%sT   # Peru Time
        // America/Managua      Nicaragua       -5:00   -       EST
        // America/Panama       Panama  -5:00   -       EST
        // America/Porto_Acre   Brazil  -5:00   -       AST
        //----------------------------------------------------------
        new SimpleTimeZone(-5 * millisPerHour, "America/New_York" /*E%sT*/,
                Calendar.APRIL, 1, -Calendar.SUNDAY /*DOW>=DOM*/, 2 * millisPerHour,
                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 2 * millisPerHour, 1 * millisPerHour),
        // Rule US      1967    max     -       Oct     lastSun 2:00    0       S
        // Rule US      1987    max     -       Apr     Sun>=1  2:00    1:00    D
        // America/New_York     US Eastern time, represented by New York        -5:00   US      E%sT
        // America/Nassau       Bahamas -5:00   Bahamas E%sT
        // America/Nipigon      Ontario, Quebec -5:00   Canada  E%sT
        // America/Thunder_Bay  Ontario, Quebec -5:00   Canada  E%sT
        // America/Montreal     Ontario, Quebec -5:00   Mont    E%sT
        // America/Iqaluit      Northwest Territories, Yukon    -5:00   NT_YK   E%sT
        // America/Detroit      Michigan        -5:00   US      E%sT
        // America/Louisville   ?       -5:00   US      E%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(-5 * millisPerHour, "America/Havana" /*C%sT*/,
//                Calendar.APRIL, 1, -Calendar.SUNDAY /*DOW>=DOM*/, 0 * millisPerHour,
//                Calendar.OCTOBER, 8, -Calendar.SUNDAY /*DOW>=DOM*/, 1 * millisPerHour, 1 * millisPerHour),
        // Rule Cuba    1990    max     -       Apr     Sun>=1  0:00    1:00    D
        // Rule Cuba    1997    max     -       Oct     Sun>=8  0:00s   0       S
        // America/Havana       Cuba    -5:00   Cuba    C%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(-5 * millisPerHour, "America/Port-au-Prince" /*E%sT*/,
//                Calendar.APRIL, 1, -Calendar.SUNDAY /*DOW>=DOM*/, 1 * millisPerHour,
//                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 2 * millisPerHour, 1 * millisPerHour),
        // Rule Haiti   1988    max     -       Apr     Sun>=1  1:00s   1:00    D
        // Rule Haiti   1988    max     -       Oct     lastSun 1:00s   0       S
        // America/Port-au-Prince       Haiti   -5:00   Haiti   E%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(-5 * millisPerHour, "America/Grand_Turk" /*E%sT*/,
//                Calendar.APRIL, 1, -Calendar.SUNDAY /*DOW>=DOM*/, 0 * millisPerHour,
//                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 0 * millisPerHour, 1 * millisPerHour),
        // Rule TC      1979    max     -       Oct     lastSun 0:00    0       S
        // Rule TC      1987    max     -       Apr     Sun>=1  0:00    1:00    D
        // America/Grand_Turk   Turks and Caicos        -5:00   TC      E%sT
        //----------------------------------------------------------
        new SimpleTimeZone(-4 * millisPerHour, "America/Caracas" /*VET*/),
        // America/Caracas      Venezuela       -4:00   -       VET
        // America/Anguilla     Anguilla        -4:00   -       AST
        // America/Antigua      Antigua and Barbuda     -4:00   -       AST
        // America/Aruba        Aruba   -4:00   -       AST
        // America/Barbados     Barbados        -4:00   -       A%sT
        // America/Curacao      Curacao -4:00   -       AST
        // America/Dominica     Dominica        -4:00   -       AST
        // America/Grenada      Grenada -4:00   -       AST
        // America/Guadeloupe   Guadeloupe      -4:00   -       AST
        // America/Guyana       Guyana  -4:00   -       GYT
        // America/La_Paz       Bolivia -4:00   -       BOT     # Bolivia Time
        // America/Manaus       Brazil  -4:00   -       WST
        // America/Martinique   Martinique      -4:00   -       AST
        // America/Montserrat   Montserrat      -4:00   -       AST
        // America/Port_of_Spain        Trinidad and Tobago     -4:00   -       AST
        // America/Puerto_Rico  Puerto Rico     -4:00   -       AST
        // America/Santo_Domingo        Dominican Republic      -4:00   -       AST
        // America/St_Kitts     St Kitts-Nevis  -4:00   -       AST
        // America/St_Lucia     St Lucia        -4:00   -       AST
        // America/St_Thomas    Virgin Is       -4:00   -       AST
        // America/St_Vincent   St Vincent and the Grenadines   -4:00   -       AST
        // America/Tortola      British Virgin Is       -4:00   -       AST
        //----------------------------------------------------------
//        new SimpleTimeZone(-4 * millisPerHour, "America/Cuiaba" /*W%sT*/,
//                Calendar.OCTOBER, 1, -Calendar.SUNDAY /*DOW>=DOM*/, 0 * millisPerHour,
//                Calendar.FEBRUARY, 11, -Calendar.SUNDAY /*DOW>=DOM*/, 0 * millisPerHour, 1 * millisPerHour),
        // Rule Brazil  1996    max     -       Feb     Sun>=11 0:00    0       S
        // Rule Brazil  1996    max     -       Oct     Sun>=1  0:00    1:00    D
        // America/Cuiaba       Brazil  -4:00   Brazil  W%sT
        //----------------------------------------------------------
        new SimpleTimeZone(-4 * millisPerHour, "America/Halifax" /*A%sT*/,
                Calendar.APRIL, 1, -Calendar.SUNDAY /*DOW>=DOM*/, 2 * millisPerHour,
                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 2 * millisPerHour, 1 * millisPerHour),
        // Rule Halifax 1962    max     -       Oct     lastSun 2:00    0       S
        // Rule Halifax 1987    max     -       Apr     Sun>=1  2:00    1:00    D
        // America/Halifax      ?       -4:00   Halifax A%sT
        // Atlantic/Bermuda     Bermuda -4:00   Bahamas A%sT
        // America/Glace_Bay    ?       -4:00   Halifax A%sT
        // America/Pangnirtung  Northwest Territories, Yukon    -4:00   NT_YK   A%sT
        // America/Goose_Bay    east Labrador   -4:00   StJohns A%sT
        // America/Thule        ?       -4:00   Thule   A%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(-4 * millisPerHour, "America/Santiago" /*CL%sT*/,
//                Calendar.OCTOBER, 9, -Calendar.SUNDAY /*DOW>=DOM*/, 0 * millisPerHour,
//                Calendar.MARCH, 9, -Calendar.SUNDAY /*DOW>=DOM*/, 0 * millisPerHour, 1 * millisPerHour),
        // Rule Chile   1969    max     -       Oct     Sun>=9  0:00    1:00    S
        // Rule Chile   1970    max     -       Mar     Sun>=9  0:00    0       -
        // America/Santiago     Chile   -4:00   Chile   CL%sT
        // Antarctica/Palmer    USA - year-round bases  -4:00   ChileAQ CL%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(-4 * millisPerHour, "Atlantic/Stanley" /*FK%sT*/,
//                Calendar.SEPTEMBER, 8, -Calendar.SUNDAY /*DOW>=DOM*/, 0 * millisPerHour,
//                Calendar.APRIL, 16, -Calendar.SUNDAY /*DOW>=DOM*/, 0 * millisPerHour, 1 * millisPerHour),
        // Rule Falk    1986    max     -       Apr     Sun>=16 0:00    0       -
        // Rule Falk    1996    max     -       Sep     Sun>=8  0:00    1:00    S
        // Atlantic/Stanley     Falklands       -4:00   Falk    FK%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(-4 * millisPerHour, "America/Asuncion" /*PY%sT*/,
//                Calendar.OCTOBER, 1, 0 /*DOM*/, 0 * millisPerHour,
//                Calendar.MARCH, 1, 0 /*DOM*/, 0 * millisPerHour, 1 * millisPerHour),
        // Rule Para    1996    max     -       Mar     1       0:00    0       -
        // Rule Para    1997    max     -       Oct     1       0:00    1:00    S
        // America/Asuncion     Paraguay        -4:00   Para    PY%sT
        //----------------------------------------------------------
        new SimpleTimeZone((int)(-3.5 * millisPerHour), "America/St_Johns" /*N%sT*/,
                Calendar.APRIL, 1, -Calendar.SUNDAY /*DOW>=DOM*/, 2 * millisPerHour,
                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 2 * millisPerHour, 1 * millisPerHour),
        // Rule StJohns 1960    max     -       Oct     lastSun 2:00    0       S
        // Rule StJohns 1989    max     -       Apr     Sun>=1  2:00    1:00    D
        // America/St_Johns     Canada  -3:30   StJohns N%sT
        //----------------------------------------------------------
        new SimpleTimeZone(-3 * millisPerHour, "America/Buenos_Aires" /*AR%sT*/),
        // America/Buenos_Aires Argentina       -3:00   -       AR%sT
        // America/Catamarca    Argentina       -3:00   -       ART
        // America/Cayenne      French Guiana   -3:00   -       GFT
        // America/Cordoba      Argentina       -3:00   -       ART
        // America/Fortaleza    Brazil  -3:00   -       EST
        // America/Jujuy        Argentina       -3:00   -       ART
        // America/Mendoza      Argentina       -3:00   -       ART
        // America/Montevideo   Uruguay -3:00   -       UY%sT
        // America/Paramaribo   Suriname        -3:00   -       SRT
        // America/Rosario      Argentina       -3:00   -       ART
        //----------------------------------------------------------
        new SimpleTimeZone(-3 * millisPerHour, "America/Sao_Paulo" /*E%sT*/,
                Calendar.OCTOBER, 1, -Calendar.SUNDAY /*DOW>=DOM*/, 0 * millisPerHour,
                Calendar.FEBRUARY, 11, -Calendar.SUNDAY /*DOW>=DOM*/, 0 * millisPerHour, 1 * millisPerHour),
        // Rule Brazil  1996    max     -       Feb     Sun>=11 0:00    0       S
        // Rule Brazil  1996    max     -       Oct     Sun>=1  0:00    1:00    D
        // America/Sao_Paulo    Brazil  -3:00   Brazil  E%sT
        // America/Maceio       Brazil  -3:00   Brazil  E%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(-3 * millisPerHour, "America/Miquelon" /*PM%sT*/,
//                Calendar.APRIL, 1, -Calendar.SUNDAY /*DOW>=DOM*/, 2 * millisPerHour,
//                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 2 * millisPerHour, 1 * millisPerHour),
        // Rule Mont    1957    max     -       Oct     lastSun 2:00    0       S
        // Rule Mont    1987    max     -       Apr     Sun>=1  2:00    1:00    D
        // America/Miquelon     St Pierre and Miquelon  -3:00   Mont    PM%sT   # Pierre & Miquelon Time
        //----------------------------------------------------------
//        new SimpleTimeZone(-3 * millisPerHour, "America/Godthab" /*WG%sT*/,
//                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, -2 * millisPerHour,
//                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, -2 * millisPerHour, 1 * millisPerHour),
        // Rule EU      1981    max     -       Mar     lastSun 1:00u   1:00    S
        // Rule EU      1996    max     -       Oct     lastSun 1:00u   0       -
        // America/Godthab      ?       -3:00   EU      WG%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(-2 * millisPerHour, "Atlantic/South_Georgia" /*GST*/),
        // Atlantic/South_Georgia       South Georgia   -2:00   -       GST     # South Georgia Time
        // America/Noronha      Brazil  -2:00   -       FST
        //----------------------------------------------------------
        new SimpleTimeZone(-1 * millisPerHour, "Atlantic/Cape_Verde" /*CVT*/),
        // Atlantic/Cape_Verde  Cape Verde      -1:00   -       CVT
        // Atlantic/Jan_Mayen   Norway  -1:00   -       EGT
        //----------------------------------------------------------
        new SimpleTimeZone(-1 * millisPerHour, "Atlantic/Azores" /*AZO%sT*/,
                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 0 * millisPerHour,
                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 0 * millisPerHour, 1 * millisPerHour),
        // Rule EU      1981    max     -       Mar     lastSun 1:00u   1:00    S
        // Rule EU      1996    max     -       Oct     lastSun 1:00u   0       -
        // Atlantic/Azores      Portugal        -1:00   EU      AZO%sT
        // America/Scoresbysund ?       -1:00   EU      EG%sT
        //----------------------------------------------------------
        new SimpleTimeZone(0 * millisPerHour, "Africa/Casablanca" /*WET*/),
        // Africa/Casablanca    Morocco 0:00    -       WET
        // Africa/Abidjan       Cote D'Ivoire   0:00    -       WAT
        // Africa/Accra Ghana   0:00    -       %s
        // Africa/Bamako        Mali    0:00    -       WAT
        // Africa/Banjul        Gambia  0:00    -       WAT
        // Africa/Bissau        Guinea-Bissau   0:00    -       WAT
        // Africa/Conakry       Guinea  0:00    -       WAT
        // Africa/Dakar Senegal 0:00    -       WAT
        // Africa/El_Aaiun      Morocco 0:00    -       WET
        // Africa/Freetown      Sierra Leone    0:00    -       WA%sT
        // Africa/Lome  Togo    0:00    -       WAT
        // Africa/Monrovia      Liberia 0:00    -       WAT
        // Africa/Nouakchott    Mauritania      0:00    -       WAT
        // Africa/Ouagadougou   Burkina Faso    0:00    -       WAT
        // Africa/Sao_Tome      Sao Tome and Principe   0:00    -       WAT
        // Africa/Timbuktu      Mali    0:00    -       WAT
        // Atlantic/Reykjavik   Iceland 0:00    -       GMT
        // Atlantic/St_Helena   St Helena       0:00    -       GMT
        //----------------------------------------------------------
//        new SimpleTimeZone(0 * millisPerHour, "Europe/London" /*GMT/BST*/,
//                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 1 * millisPerHour,
//                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 1 * millisPerHour, 1 * millisPerHour),
        // Rule EU      1981    max     -       Mar     lastSun 1:00u   1:00    S
        // Rule EU      1996    max     -       Oct     lastSun 1:00u   0       -
        // Europe/London        United Kingdom  0:00    EU      GMT/BST
        // Atlantic/Canary      Spain   0:00    EU      WE%sT
        // Atlantic/Faeroe      Denmark 0:00    EU      WE%sT
        // Atlantic/Madeira     Portugal        0:00    EU      WE%sT
        // Europe/Belfast       United Kingdom  0:00    EU      GMT/BST
        // Europe/Dublin        United Kingdom  0:00    EU      GMT/IST
        // Europe/Lisbon        Portugal        0:00    EU      WE%sT
        // WET  Continental Europe      0:00    EU      WE%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(1 * millisPerHour, "Africa/Lagos" /*CAT*/),
        // Africa/Lagos Nigeria 1:00    -       CAT
        // Africa/Algiers       Algeria 1:00    -       CET
        // Africa/Bangui        Central African Republic        1:00    -       CAT
        // Africa/Brazzaville   Congo   1:00    -       CAT
        // Africa/Douala        Cameroon        1:00    -       CAT
        // Africa/Kinshasa      Zaire   1:00    -       CAT
        // Africa/Libreville    Gabon   1:00    -       CAT
        // Africa/Luanda        Angola  1:00    -       CAT
        // Africa/Malabo        Equatorial Guinea       1:00    -       CAT
        // Africa/Ndjamena      Chad    1:00    -       CAT
        // Africa/Niamey        Niger   1:00    -       CAT
        // Africa/Porto-Novo    Benin   1:00    -       CAT
        // Africa/Tunis Tunisia 1:00    -       CE%sT
        //----------------------------------------------------------
        new SimpleTimeZone(1 * millisPerHour, "Europe/Paris" /*CE%sT*/,
                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 2 * millisPerHour,
                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 2 * millisPerHour, 1 * millisPerHour),
        // Rule EU      1981    max     -       Mar     lastSun 1:00u   1:00    S
        // Rule EU      1996    max     -       Oct     lastSun 1:00u   0       -
        // Europe/Paris France  1:00    EU      CE%sT
        // Africa/Ceuta Spain   1:00    EU      CE%sT
        // Europe/Amsterdam     Netherlands     1:00    EU      CE%sT
        // Europe/Andorra       Andorra 1:00    EU      CE%sT
        // Europe/Belgrade      Yugoslavia      1:00    EU      CE%sT
        // Europe/Berlin        Germany 1:00    EU      CE%sT
        // Europe/Brussels      Belgium 1:00    EU      CE%sT
        // Europe/Budapest      Hungary 1:00    EU      CE%sT
        // Europe/Copenhagen    Denmark 1:00    EU      CE%sT
        // Europe/Gibraltar     Gibraltar       1:00    EU      CE%sT
        // Europe/Ljubljana     Slovenia        1:00    EU      CE%sT
        // Europe/Luxembourg    Luxembourg      1:00    EU      CE%sT
        // Europe/Madrid        Spain   1:00    EU      CE%sT
        // Europe/Malta Malta   1:00    EU      CE%sT
        // Europe/Monaco        Monaco  1:00    EU      CE%sT
        // Europe/Oslo  Norway  1:00    EU      CE%sT
        // Europe/Prague        Czech Republic  1:00    EU      CE%sT
        // Europe/Rome  Italy   1:00    EU      CE%sT
        // Europe/Sarajevo      Bosnia and Herzegovina  1:00    EU      CE%sT
        // Europe/Skopje        Macedonia       1:00    EU      CE%sT
        // Europe/Stockholm     Sweden  1:00    EU      CE%sT
        // Europe/Tirane        Albania 1:00    EU      CE%sT
        // Europe/Vaduz Liechtenstein   1:00    EU      CE%sT
        // Europe/Vienna        Austria 1:00    EU      CE%sT
        // Europe/Zagreb        Croatia 1:00    EU      CE%sT
        // Europe/Zurich        Switzerland     1:00    EU      CE%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(1 * millisPerHour, "Africa/Tripoli" /*CE%sT*/,
//                Calendar.MARCH, -1, Calendar.THURSDAY /*DOW_IN_DOM*/, 2 * millisPerHour,
//                Calendar.OCTOBER, 1, -Calendar.THURSDAY /*DOW>=DOM*/, 3 * millisPerHour, 1 * millisPerHour),
        // Rule Libya   1997    max     -       Mar     lastThu 2:00s   1:00    S
        // Rule Libya   1997    max     -       Oct     Thu>=1  2:00s   0       -
        // Africa/Tripoli       Libya   1:00    Libya   CE%sT
        //----------------------------------------------------------
        // Omitting zone CET
        // Rule C-Eur   1981    max     -       Mar     lastSun 2:00s   1:00    S
        // Rule C-Eur   1996    max     -       Oct     lastSun 2:00s   0       -
        // CET  Continental Europe      1:00    C-Eur   CE%sT
        // MET  Continental Europe      1:00    C-Eur   ME%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(1 * millisPerHour, "Europe/Warsaw" /*CE%sT*/,
//                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 1 * millisPerHour,
//                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 2 * millisPerHour, 1 * millisPerHour),
        // Rule W-Eur   1981    max     -       Mar     lastSun 1:00s   1:00    S
        // Rule W-Eur   1996    max     -       Oct     lastSun 1:00s   0       -
        // Europe/Warsaw        Poland  1:00    W-Eur   CE%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(2 * millisPerHour, "Africa/Johannesburg" /*SA%sT*/),
        // Africa/Johannesburg  South Africa    2:00    -       SA%sT
        // Africa/Blantyre      Malawi  2:00    -       SAT
        // Africa/Bujumbura     Burundi 2:00    -       SAT
        // Africa/Gaborone      Botswana        2:00    -       SAT
        // Africa/Harare        Zimbabwe        2:00    -       SAT
        // Africa/Khartoum      Sudan   2:00    -       EE%sT
        // Africa/Kigali        Rwanda  2:00    -       SAT
        // Africa/Lubumbashi    Zaire   2:00    -       SAT
        // Africa/Lusaka        Zambia  2:00    -       SAT
        // Africa/Maputo        Mozambique      2:00    -       SAT
        // Africa/Maseru        Lesotho 2:00    -       SAT
        // Africa/Mbabane       Swaziland       2:00    -       SAT
        //----------------------------------------------------------
//        new SimpleTimeZone(2 * millisPerHour, "Europe/Bucharest" /*EE%sT*/,
//                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 0 * millisPerHour,
//                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 0 * millisPerHour, 1 * millisPerHour),
        // Rule E-Eur   1981    max     -       Mar     lastSun 0:00    1:00    S
        // Rule E-Eur   1996    max     -       Oct     lastSun 0:00    0       -
        // Europe/Bucharest     Romania 2:00    E-Eur   EE%sT
        // Europe/Chisinau      Moldova 2:00    E-Eur   EE%sT
        // Europe/Sofia Bulgaria        2:00    E-Eur   EE%sT
        //----------------------------------------------------------
        new SimpleTimeZone(2 * millisPerHour, "Europe/Istanbul" /*EE%sT*/,
                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 3 * millisPerHour,
                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 3 * millisPerHour, 1 * millisPerHour),
        // Rule EU      1981    max     -       Mar     lastSun 1:00u   1:00    S
        // Rule EU      1996    max     -       Oct     lastSun 1:00u   0       -
        // Europe/Istanbul      Turkey  2:00    EU      EE%sT
        // EET  Continental Europe      2:00    EU      EE%sT
        // Europe/Athens        Greece  2:00    EU      EE%sT
        // Europe/Helsinki      Finland 2:00    EU      EE%sT
        // Europe/Kiev  Ukraine 2:00    EU      EE%sT
        //----------------------------------------------------------
        new SimpleTimeZone(2 * millisPerHour, "Africa/Cairo" /*EE%sT*/,
                Calendar.APRIL, -1, Calendar.FRIDAY /*DOW_IN_DOM*/, 0 * millisPerHour,
                Calendar.SEPTEMBER, -1, Calendar.FRIDAY /*DOW_IN_DOM*/, 0 * millisPerHour, 1 * millisPerHour),
        // Rule Egypt   1995    max     -       Apr     lastFri 0:00    1:00    S
        // Rule Egypt   1995    max     -       Sep     lastFri 0:00    0       -
        // Africa/Cairo Egypt   2:00    Egypt   EE%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(2 * millisPerHour, "Asia/Amman" /*EE%sT*/,
//                Calendar.APRIL, 1, -Calendar.FRIDAY /*DOW>=DOM*/, 0 * millisPerHour,
//                Calendar.SEPTEMBER, 15, -Calendar.FRIDAY /*DOW>=DOM*/, 1 * millisPerHour, 1 * millisPerHour),
        // Rule    Jordan       1993    max     -       Apr     Fri>=1  0:00    1:00    S
        // Rule    Jordan       1995    max     -       Sep     Fri>=15 0:00s   0       -
        // Asia/Amman   Jordan  2:00    Jordan  EE%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(2 * millisPerHour, "Europe/Riga" /*EE%sT*/,
//                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 2 * millisPerHour,
//                Calendar.SEPTEMBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 3 * millisPerHour, 1 * millisPerHour),
        // Rule Latvia  1992    max     -       Mar     lastSun 2:00s   1:00    S
        // Rule Latvia  1992    max     -       Sep     lastSun 2:00s   0       -
        // Europe/Riga  Latvia  2:00    Latvia  EE%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(2 * millisPerHour, "Asia/Beirut" /*EE%sT*/,
//                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 0 * millisPerHour,
//                Calendar.SEPTEMBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 0 * millisPerHour, 1 * millisPerHour),
        // Rule Lebanon 1993    max     -       Mar     lastSun 0:00    1:00    S
        // Rule Lebanon 1993    max     -       Sep     lastSun 0:00    0       -
        // Asia/Beirut  Lebanon 2:00    Lebanon EE%sT
        // Asia/Nicosia Cyprus  2:00    Cyprus  EE%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(2 * millisPerHour, "Africa/Windhoek" /*SA%sT*/,
//                Calendar.SEPTEMBER, 1, -Calendar.SUNDAY /*DOW>=DOM*/, 2 * millisPerHour,
//                Calendar.APRIL, 1, -Calendar.SUNDAY /*DOW>=DOM*/, 2 * millisPerHour, 1 * millisPerHour),
        // Rule Namibia 1994    max     -       Sep     Sun>=1  2:00    1:00    S
        // Rule Namibia 1995    max     -       Apr     Sun>=1  2:00    0       -
        // Africa/Windhoek      Namibia 2:00    Namibia SA%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(2 * millisPerHour, "Europe/Minsk" /*EE%sT*/,
//                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 2 * millisPerHour,
//                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 3 * millisPerHour, 1 * millisPerHour),
        // Rule Russia  1993    max     -       Mar     lastSun 2:00s   1:00    S
        // Rule Russia  1996    max     -       Oct     lastSun 2:00s   0       -
        // Europe/Minsk Belarus 2:00    Russia  EE%sT
        // Europe/Tallinn       Estonia 2:00    C-Eur   EE%sT
        // Europe/Vilnius       Lithuania       2:00    C-Eur   EE%sT
        // Europe/Kaliningrad   Russia  2:00    Russia  EE%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(2 * millisPerHour, "Asia/Damascus" /*EE%sT*/,
//                Calendar.APRIL, 1, 0 /*DOM*/, 0 * millisPerHour,
//                Calendar.OCTOBER, 1, 0 /*DOM*/, 0 * millisPerHour, 1 * millisPerHour),
        // Rule Syria   1994    max     -       Apr     1       0:00    1:00    S
        // Rule Syria   1994    max     -       Oct     1       0:00    0       -
        // Asia/Damascus        Syria   2:00    Syria   EE%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(2 * millisPerHour, "Asia/Jerusalem" /*I%sT*/,
//                Calendar.MARCH, 15, -Calendar.FRIDAY /*DOW>=DOM*/, 0 * millisPerHour,
//                Calendar.SEPTEMBER, 15, -Calendar.SUNDAY /*DOW>=DOM*/, 0 * millisPerHour, 1 * millisPerHour),
        // Rule Zion    1999    max     -       Mar     Fri>=15 0:00    1:00    D
        // Rule Zion    1999    max     -       Sep     Sun>=15 0:00    0       S
        // Asia/Jerusalem       Israel  2:00    Zion    I%sT
        // Asia/Gaza    Palestine       2:00    Zion    I%sT
        //----------------------------------------------------------
        new SimpleTimeZone(3 * millisPerHour, "Asia/Riyadh" /*AST*/),
        // Asia/Riyadh  Saudi Arabia    3:00    -       AST
        // Africa/Addis_Ababa   Ethiopia        3:00    -       EAT
        // Africa/Asmera        Eritrea 3:00    -       EAT
        // Africa/Dar_es_Salaam Tanzania        3:00    -       EAT
        // Africa/Djibouti      Djibouti        3:00    -       EAT
        // Africa/Kampala       Uganda  3:00    -       EAT
        // Africa/Mogadishu     Somalia 3:00    -       EAT
        // Africa/Nairobi       Kenya   3:00    -       EAT
        // Asia/Aden    Yemen   3:00    -       AST
        // Asia/Bahrain Bahrain 3:00    -       AST
        // Asia/Kuwait  Kuwait  3:00    -       AST
        // Asia/Qatar   Qatar   3:00    -       AST
        // Indian/Antananarivo  Madagascar      3:00    -       EAT
        // Indian/Comoro        Comoros 3:00    -       EAT
        // Indian/Mayotte       Mayotte 3:00    -       EAT
        //----------------------------------------------------------
//        new SimpleTimeZone(3 * millisPerHour, "Europe/Simferopol" /*MSK/MSD*/,
//                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 3 * millisPerHour,
//                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 3 * millisPerHour, 1 * millisPerHour),
        // Rule Crimea  1996    max     -       Mar     lastSun 0:00u   1:00    -
        // Rule Crimea  1996    max     -       Oct     lastSun 0:00u   0       -
        // Europe/Simferopol    Ukraine 3:00    Crimea  MSK/MSD
        //----------------------------------------------------------
//        new SimpleTimeZone(3 * millisPerHour, "Asia/Baghdad" /*A%sT*/,
//                Calendar.APRIL, 1, 0 /*DOM*/, 3 * millisPerHour,
//                Calendar.OCTOBER, 1, 0 /*DOM*/, 4 * millisPerHour, 1 * millisPerHour),
        // Rule Iraq    1991    max     -       Apr     1       3:00s   1:00    D
        // Rule Iraq    1991    max     -       Oct     1       3:00s   0       D
        // Asia/Baghdad Iraq    3:00    Iraq    A%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(3 * millisPerHour, "Europe/Moscow" /*MSK/MSD*/,
//                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 2 * millisPerHour,
//                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 3 * millisPerHour, 1 * millisPerHour),
        // Rule Russia  1993    max     -       Mar     lastSun 2:00s   1:00    S
        // Rule Russia  1996    max     -       Oct     lastSun 2:00s   0       -
        // Europe/Moscow        Russia  3:00    Russia  MSK/MSD
        //----------------------------------------------------------
        new SimpleTimeZone((int)(3.5 * millisPerHour), "Asia/Tehran" /*IR%sT*/,
                Calendar.MARCH, 4, 0 /*DOM*/, 0 * millisPerHour,
                Calendar.SEPTEMBER, 4, 0 /*DOM*/, 0 * millisPerHour, 1 * millisPerHour),
        // Rule Iran    1997    1999    -       Mar     21      0:00    1:00    S
        // Rule Iran    1997    1999    -       Sep     23      0:00    0       -
        // Asia/Tehran  Iran    3:30    Iran    IR%sT
        //----------------------------------------------------------
        new SimpleTimeZone(4 * millisPerHour, "Asia/Yerevan" /*AM%sT*/),
        // Asia/Yerevan Armenia 4:00    -       AM%sT
        // Asia/Dubai   United Arab Emirates    4:00    -       GST
        // Asia/Muscat  Oman    4:00    -       GST
        // Indian/Mahe  Seychelles      4:00    -       SCT     # Seychelles Time
        // Indian/Mauritius     Mauritius       4:00    -       MUT     # Mauritius Time
        // Indian/Reunion       Reunion 4:00    -       RET     # Reunion Time
        //----------------------------------------------------------
//        new SimpleTimeZone(4 * millisPerHour, "Asia/Aqtau" /*AQT%sT*/,
//                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 0 * millisPerHour,
//                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 0 * millisPerHour, 1 * millisPerHour),
        // Rule E-EurAsia       1981    max     -       Mar     lastSun 0:00    1:00    S
        // Rule E-EurAsia       1996    max     -       Oct     lastSun 0:00    0       -
        // Asia/Aqtau   Kazakhstan      4:00 E-EurAsia  AQT%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(4 * millisPerHour, "Asia/Baku" /*AZ%sT*/,
//                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 5 * millisPerHour,
//                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 5 * millisPerHour, 1 * millisPerHour),
        // Rule EUAsia  1981    max     -       Mar     lastSun 1:00u   1:00    S
        // Rule EUAsia  1996    max     -       Oct     lastSun 1:00u   0       -
        // Asia/Baku    Azerbaijan      4:00    EUAsia  AZ%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(4 * millisPerHour, "Europe/Samara" /*SAM%sT*/,
//                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 2 * millisPerHour,
//                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 3 * millisPerHour, 1 * millisPerHour),
        // Rule Russia  1993    max     -       Mar     lastSun 2:00s   1:00    S
        // Rule Russia  1996    max     -       Oct     lastSun 2:00s   0       -
        // Europe/Samara        Russia  4:00    Russia  SAM%sT
        //----------------------------------------------------------
//        new SimpleTimeZone((int)(4.5 * millisPerHour), "Asia/Kabul" /*AFT*/),
        // Asia/Kabul   Afghanistan     4:30    -       AFT
        //----------------------------------------------------------
        new SimpleTimeZone(5 * millisPerHour, "Asia/Karachi" /*PKT*/),
        // Asia/Karachi Pakistan        5:00    -       PKT     # Pakistan Time
        // Asia/Ashkhabad       Turkmenistan    5:00    -       TMT     # Turkmenistan Time
        // Asia/Dushanbe        Tajikistan      5:00    -       TJT     # Tajikistan Time
        // Asia/Tashkent        Uzbekistan      5:00    -       UZT     # Uzbekistan Time
        // Asia/Tbilisi Georgia 5:00    -       GET
        // Indian/Chagos        British Indian Ocean Territory  5:00    -       IOT     # BIOT Time
        // Indian/Kerguelen     France - year-round bases       5:00    -       TFT     # ISO code TF Time
        // Indian/Maldives      Maldives        5:00    -       MVT     # Maldives Time
        //----------------------------------------------------------
//        new SimpleTimeZone(5 * millisPerHour, "Asia/Aqtobe" /*AQT%sT*/,
//                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 0 * millisPerHour,
//                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 0 * millisPerHour, 1 * millisPerHour),
        // Rule E-EurAsia       1981    max     -       Mar     lastSun 0:00    1:00    S
        // Rule E-EurAsia       1996    max     -       Oct     lastSun 0:00    0       -
        // Asia/Aqtobe  Kazakhstan      5:00 E-EurAsia  AQT%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(5 * millisPerHour, "Asia/Bishkek" /*KG%sT*/,
//                Calendar.APRIL, 7, -Calendar.SUNDAY /*DOW>=DOM*/, 0 * millisPerHour,
//                Calendar.SEPTEMBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 0 * millisPerHour, 1 * millisPerHour),
        // Rule Kirgiz  1992    max     -       Apr     Sun>=7  0:00    1:00    S
        // Rule Kirgiz  1991    max     -       Sep     lastSun 0:00    0       -
        // Asia/Bishkek Kirgizstan      5:00    Kirgiz  KG%sT   # Kirgizstan Time
        //----------------------------------------------------------
//        new SimpleTimeZone(5 * millisPerHour, "Asia/Yekaterinburg" /*YEK%sT*/,
//                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 2 * millisPerHour,
//                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 3 * millisPerHour, 1 * millisPerHour),
        // Rule Russia  1993    max     -       Mar     lastSun 2:00s   1:00    S
        // Rule Russia  1996    max     -       Oct     lastSun 2:00s   0       -
        // Asia/Yekaterinburg   Russia  5:00    Russia  YEK%sT  # Yekaterinburg Time
        //----------------------------------------------------------
        new SimpleTimeZone((int)(5.5 * millisPerHour), "Asia/Calcutta" /*IST*/),
        // Asia/Calcutta        India   5:30    -       IST
        //----------------------------------------------------------
//        new SimpleTimeZone((int)(5.75 * millisPerHour), "Asia/Katmandu" /*NPT*/),
        // Asia/Katmandu        Nepal   5:45    -       NPT     # Nepal Time
        //----------------------------------------------------------
        new SimpleTimeZone(6 * millisPerHour, "Asia/Dacca" /*BDT*/),
        // Asia/Dacca   Bangladesh      6:00    -       BDT     # Bangladesh Time
        // Antarctica/Mawson    Australia - territories 6:00    -       MAWT    # Mawson Time
        // Asia/Colombo Sri Lanka       6:00    -       LKT
        // Asia/Thimbu  Bhutan  6:00    -       BTT     # Bhutan Time
        //----------------------------------------------------------
//        new SimpleTimeZone(6 * millisPerHour, "Asia/Alma-Ata" /*ALM%sT*/,
//                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 0 * millisPerHour,
//                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 0 * millisPerHour, 1 * millisPerHour),
        // Rule E-EurAsia       1981    max     -       Mar     lastSun 0:00    1:00    S
        // Rule E-EurAsia       1996    max     -       Oct     lastSun 0:00    0       -
        // Asia/Alma-Ata        Kazakhstan      6:00 E-EurAsia  ALM%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(6 * millisPerHour, "Asia/Novosibirsk" /*NOV%sT*/,
//                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 2 * millisPerHour,
//                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 3 * millisPerHour, 1 * millisPerHour),
        // Rule Russia  1993    max     -       Mar     lastSun 2:00s   1:00    S
        // Rule Russia  1996    max     -       Oct     lastSun 2:00s   0       -
        // Asia/Novosibirsk     Russia  6:00    Russia  NOV%sT
        // Asia/Omsk    Russia  6:00    Russia  OMS%sT
        //----------------------------------------------------------
//        new SimpleTimeZone((int)(6.5 * millisPerHour), "Asia/Rangoon" /*MMT*/),
        // Asia/Rangoon Burma / Myanmar 6:30    -       MMT     # Myanmar Time
        // Indian/Cocos Cocos   6:30    -       CCT     # Cocos Islands Time
        //----------------------------------------------------------
        new SimpleTimeZone(7 * millisPerHour, "Asia/Bangkok" /*ICT*/),
        // Asia/Bangkok Thailand        7:00    -       ICT
        // Asia/Jakarta Indonesia       7:00    -       JAVT
        // Asia/Phnom_Penh      Cambodia        7:00    -       ICT
        // Asia/Saigon  Vietnam 7:00    -       ICT
        // Asia/Vientiane       Laos    7:00    -       ICT
        // Indian/Christmas     Australian miscellany   7:00    -       CXT     # Christmas Island Time
        //----------------------------------------------------------
//        new SimpleTimeZone(7 * millisPerHour, "Asia/Krasnoyarsk" /*KRA%sT*/,
//                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 2 * millisPerHour,
//                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 3 * millisPerHour, 1 * millisPerHour),
        // Rule Russia  1993    max     -       Mar     lastSun 2:00s   1:00    S
        // Rule Russia  1996    max     -       Oct     lastSun 2:00s   0       -
        // Asia/Krasnoyarsk     Russia  7:00    Russia  KRA%sT
        //----------------------------------------------------------
        new SimpleTimeZone(8 * millisPerHour, "Asia/Shanghai" /*C%sT*/),
        // Asia/Shanghai        People's Republic of China      8:00    -       C%sT
        // Antarctica/Casey     Australia - territories 8:00    -       WST     # Western (Aus) Standard Time
        // Asia/Brunei  Brunei  8:00    -       BNT
        // Asia/Chungking       People's Republic of China      8:00    -       C%sT
        // Asia/Harbin  People's Republic of China      8:00    -       C%sT
        // Asia/Hong_Kong       Hong Kong       8:00    -       C%sT
        // Asia/Ishigaki        Japan   8:00    -       CST
        // Asia/Kashgar People's Republic of China      8:00    -       C%sT
        // Asia/Kuala_Lumpur    Malaysia        8:00    -       MYT     # Malaysia Time
        // Asia/Kuching Malaysia        8:00    -       MYT
        // Asia/Macao   Macao   8:00    -       C%sT
        // Asia/Manila  Philippines     8:00    -       PH%sT
        // Asia/Singapore       Singapore       8:00    -       SGT
        // Asia/Taipei  Republic of China       8:00    -       C%sT
        // Asia/Ujung_Pandang   Indonesia       8:00    -       BORT
        // Asia/Urumqi  People's Republic of China      8:00    -       C%sT
        // Australia/Perth      Australia       8:00    -       WST
        //----------------------------------------------------------
//        new SimpleTimeZone(8 * millisPerHour, "Asia/Ulan_Bator" /*ULA%sT*/,
//                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 0 * millisPerHour,
//                Calendar.SEPTEMBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 0 * millisPerHour, 1 * millisPerHour),
        // Rule Mongol  1991    max     -       Mar     lastSun 0:00    1:00    S
        // Rule Mongol  1997    max     -       Sep     lastSun 0:00    0       -
        // Asia/Ulan_Bator      Mongolia        8:00    Mongol  ULA%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(8 * millisPerHour, "Asia/Irkutsk" /*IRK%sT*/,
//                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 2 * millisPerHour,
//                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 3 * millisPerHour, 1 * millisPerHour),
        // Rule Russia  1993    max     -       Mar     lastSun 2:00s   1:00    S
        // Rule Russia  1996    max     -       Oct     lastSun 2:00s   0       -
        // Asia/Irkutsk Russia  8:00    Russia  IRK%sT
        //----------------------------------------------------------
        new SimpleTimeZone(9 * millisPerHour, "Asia/Tokyo" /*JST*/),
        // Asia/Tokyo   Japan   9:00    -       JST
        // Asia/Jayapura        Indonesia       9:00    -       JAYT
        // Asia/Pyongyang       Korea   9:00    -       KST
        // Asia/Seoul   Korea   9:00    -       K%sT
        // Pacific/Palau        Palau   9:00    -       PWT     # Palau Time
        //----------------------------------------------------------
//        new SimpleTimeZone(9 * millisPerHour, "Asia/Yakutsk" /*YAK%sT*/,
//                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 2 * millisPerHour,
//                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 3 * millisPerHour, 1 * millisPerHour),
        // Rule Russia  1993    max     -       Mar     lastSun 2:00s   1:00    S
        // Rule Russia  1996    max     -       Oct     lastSun 2:00s   0       -
        // Asia/Yakutsk Russia  9:00    Russia  YAK%sT
        //----------------------------------------------------------
        new SimpleTimeZone((int)(9.5 * millisPerHour), "Australia/Darwin" /*CST*/),
        // Australia/Darwin     Australia       9:30    -       CST
        //----------------------------------------------------------
        new SimpleTimeZone((int)(9.5 * millisPerHour), "Australia/Adelaide" /*CST*/,
                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 2 * millisPerHour,
                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 3 * millisPerHour, 1 * millisPerHour),
        // Rule AS      1987    max     -       Oct     lastSun 2:00s   1:00    -
        // Rule AS      1995    max     -       Mar     lastSun 2:00s   0       -
        // Australia/Adelaide   South Australia 9:30    AS      CST
        // Australia/Broken_Hill        New South Wales 9:30    AN      CST
        //----------------------------------------------------------
//        new SimpleTimeZone(10 * millisPerHour, "Australia/Brisbane" /*EST*/),
        // Australia/Brisbane   Australia       10:00   -       EST
        // Antarctica/DumontDUrville    France - year-round bases       10:00   -       DDUT    # Dumont-d'Urville Time
        // Australia/Lindeman   Australia       10:00   -       EST
        // Pacific/Guam Guam    10:00   -       GST
        // Pacific/Port_Moresby Papua New Guinea        10:00   -       PGT     # Papua New Guinea Time
        // Pacific/Saipan       N Mariana Is    10:00   -       MPT
        // Pacific/Truk Micronesia      10:00   -       TRUT    # Truk Time
        // Pacific/Yap  Micronesia      10:00   -       YAPT
        //----------------------------------------------------------
        new SimpleTimeZone(10 * millisPerHour, "Australia/Sydney" /*EST*/,
                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 2 * millisPerHour,
                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 3 * millisPerHour, 1 * millisPerHour),
        // Rule AN      1987    max     -       Oct     lastSun 2:00s   1:00    -
        // Rule AN      1996    max     -       Mar     lastSun 2:00s   0       -
        // Australia/Sydney     New South Wales 10:00   AN      EST
        // Australia/Melbourne  Victoria        10:00   AV      EST
        //----------------------------------------------------------
//        new SimpleTimeZone(10 * millisPerHour, "Australia/Hobart" /*EST*/,
//                Calendar.OCTOBER, 1, -Calendar.SUNDAY /*DOW>=DOM*/, 2 * millisPerHour,
//                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 3 * millisPerHour, 1 * millisPerHour),
        // Rule AT      1991    max     -       Oct     Sun>=1  2:00s   1:00    -
        // Rule AT      1991    max     -       Mar     lastSun 2:00s   0       -
        // Australia/Hobart     Tasmania        10:00   AT      EST
        //----------------------------------------------------------
//        new SimpleTimeZone(10 * millisPerHour, "Asia/Vladivostok" /*VLA%sT*/,
//                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 2 * millisPerHour,
//                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 3 * millisPerHour, 1 * millisPerHour),
        // Rule Russia  1993    max     -       Mar     lastSun 2:00s   1:00    S
        // Rule Russia  1996    max     -       Oct     lastSun 2:00s   0       -
        // Asia/Vladivostok     Russia  10:00   Russia  VLA%sT
        //----------------------------------------------------------
//        new SimpleTimeZone((int)(10.5 * millisPerHour), "Australia/Lord_Howe" /*LHST*/,
//                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 2 * millisPerHour,
//                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 3 * millisPerHour, (int)(0.5 * millisPerHour)),
        // Rule LH      1987    max     -       Oct     lastSun 2:00s   0:30    -
        // Rule LH      1996    max     -       Mar     lastSun 2:00s   0       -
        // Australia/Lord_Howe  Lord Howe Island        10:30   LH      LHST
        //----------------------------------------------------------
        new SimpleTimeZone(11 * millisPerHour, "Pacific/Guadalcanal" /*SBT*/),
        // Pacific/Guadalcanal  Solomon Is      11:00   -       SBT     # Solomon Is Time
        // Pacific/Efate        Vanuatu 11:00   -       VU%sT   # Vanuatu Time
        // Pacific/Ponape       Micronesia      11:00   -       PONT    # Ponape Time
        //----------------------------------------------------------
//        new SimpleTimeZone(11 * millisPerHour, "Pacific/Noumea" /*NC%sT*/,
//                Calendar.NOVEMBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 2 * millisPerHour,
//                Calendar.MARCH, 1, -Calendar.SUNDAY /*DOW>=DOM*/, 3 * millisPerHour, 1 * millisPerHour),
        // Rule NC      1997    max     -       Mar     Sun>=1  2:00s   0       -
        // Rule NC      1997    max     -       Nov     lastSun 2:00s   1:00    S
        // Pacific/Noumea       New Caledonia   11:00   NC      NC%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(11 * millisPerHour, "Asia/Magadan" /*MAG%sT*/,
//                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 2 * millisPerHour,
//                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 3 * millisPerHour, 1 * millisPerHour),
        // Rule Russia  1993    max     -       Mar     lastSun 2:00s   1:00    S
        // Rule Russia  1996    max     -       Oct     lastSun 2:00s   0       -
        // Asia/Magadan Russia  11:00   Russia  MAG%sT
        //----------------------------------------------------------
//        new SimpleTimeZone((int)(11.5 * millisPerHour), "Pacific/Norfolk" /*NFT*/),
        // Pacific/Norfolk      Norfolk 11:30   -       NFT     # Norfolk Time
        //----------------------------------------------------------
        new SimpleTimeZone(12 * millisPerHour, "Pacific/Fiji" /*FJT*/),
        // Pacific/Fiji Fiji    12:00   -       FJT     # Fiji Time
        // Pacific/Funafuti     Tuvalu  12:00   -       TVT     # Tuvalu Time
        // Pacific/Kosrae       Micronesia      12:00   -       KOST    # Kosrae Time
        // Pacific/Kwajalein    Marshall Is     12:00   -       MHT
        // Pacific/Majuro       Marshall Is     12:00   -       MHT
        // Pacific/Nauru        Nauru   12:00   -       NRT
        // Pacific/Tarawa       Kiribati        12:00   -       GILT    # Gilbert Is Time
        // Pacific/Wake Wake    12:00   -       WAKT    # Wake Time
        // Pacific/Wallis       Wallis and Futuna       12:00   -       WFT     # Wallis & Futuna Time
        //----------------------------------------------------------
        new SimpleTimeZone(12 * millisPerHour, "Pacific/Auckland" /*NZ%sT*/,
                Calendar.OCTOBER, 1, -Calendar.SUNDAY /*DOW>=DOM*/, 2 * millisPerHour,
                Calendar.MARCH, 15, -Calendar.SUNDAY /*DOW>=DOM*/, 3 * millisPerHour, 1 * millisPerHour),
        // Rule NZ      1990    max     -       Oct     Sun>=1  2:00s   1:00    D
        // Rule NZ      1990    max     -       Mar     Sun>=15 2:00s   0       S
        // Pacific/Auckland     New Zealand     12:00   NZ      NZ%sT
        // Antarctica/McMurdo   USA - year-round bases  12:00   NZAQ    NZ%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(12 * millisPerHour, "Asia/Kamchatka" /*PET%sT*/,
//                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 2 * millisPerHour,
//                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 3 * millisPerHour, 1 * millisPerHour),
        // Rule Russia  1993    max     -       Mar     lastSun 2:00s   1:00    S
        // Rule Russia  1996    max     -       Oct     lastSun 2:00s   0       -
        // Asia/Kamchatka       Russia  12:00   Russia  PET%sT
        //----------------------------------------------------------
//        new SimpleTimeZone((int)(12.75 * millisPerHour), "Pacific/Chatham" /*CHA%sT*/,
//                Calendar.OCTOBER, 1, -Calendar.SUNDAY /*DOW>=DOM*/, (int)(2.75 * millisPerHour),
//                Calendar.MARCH, 15, -Calendar.SUNDAY /*DOW>=DOM*/, (int)(3.75 * millisPerHour), 1 * millisPerHour),
        // Rule Chatham 1990    max     -       Oct     Sun>=1  2:45s   1:00    D
        // Rule Chatham 1991    max     -       Mar     Sun>=15 2:45s   0       S
        // Pacific/Chatham      New Zealand     12:45   Chatham CHA%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(13 * millisPerHour, "Pacific/Tongatapu" /*TOT*/),
        // Pacific/Tongatapu    Tonga   13:00   -       TOT
        // Pacific/Enderbury    Kiribati        13:00   -       PHOT
        //----------------------------------------------------------
//        new SimpleTimeZone(13 * millisPerHour, "Asia/Anadyr" /*ANA%sT*/,
//                Calendar.MARCH, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 2 * millisPerHour,
//                Calendar.OCTOBER, -1, Calendar.SUNDAY /*DOW_IN_DOM*/, 3 * millisPerHour, 1 * millisPerHour),
        // Rule Russia  1993    max     -       Mar     lastSun 2:00s   1:00    S
        // Rule Russia  1996    max     -       Oct     lastSun 2:00s   0       -
        // Asia/Anadyr  Russia  13:00   Russia  ANA%sT
        //----------------------------------------------------------
//        new SimpleTimeZone(14 * millisPerHour, "Pacific/Kiritimati" /*LINT*/),
        // Pacific/Kiritimati   Kiribati        14:00   -       LINT
        //----------------------------------------------------------
        // 347 total zones
        // 120 zones not including identical offset/rule zones
        // 96 zones not including equivalent offset/rule zones
    };

    /**
     * This array maps between the old three-letter IDs we used to use
     * and the current names of those zones.  We use this array during
     * initialization to provide backwards compatibility.
     *
     * Note in particular that these three-letter IDs are completely
     * wrong in some cases, and do not represent the correct abbreviations
     * in common use.  For example, "AST" is not the correct abbreviation for
     * Alaskan Standard Time; it is "AKST".  These IDs are for compatibilty
     * only, and their use is in fact deprecated.
     */
    private static final String[] compatibilityMap =
    {
        // GMT is the ID for Greenwich Mean Time time zone.
        /*GMT+0*/ "GMT", "Africa/Casablanca", // NOT Europe/London
                  "UTC", "Africa/Casablanca",
        // ECT is the ID for European Central Time time zone.
        /*GMT+1*/ "ECT", "Europe/Paris",
        // EET is the ID for Eastern European Time time zone.
        /*GMT+2*/ "EET", "Europe/Istanbul",
        // ART is the ID for (Arabic) Egypt Standard Time timezone.
        /*GMT+2*/ "ART", "Africa/Cairo",
        // EAT is the ID for Eastern African Time time zone.
        /*GMT+3*/ "EAT", "Asia/Riyadh",
        // MET is the ID for Middle East Time time zone.
        /*GMT+0330*/ "MET", "Asia/Tehran",
        // NET is the ID for Near East Time time zone.
        /*GMT+4*/ "NET", "Asia/Yerevan",
        // PLT is the ID for Pakistan Lahore Time time zone.
        /*GMT+5*/ "PLT", "Asia/Karachi",
        // IST is the ID for India Standard Time time zone.
        /*GMT+0550*/ "IST", "Asia/Calcutta",
        // BST is the ID for Bangladesh Standard Time time zone.
        /*GMT+6*/ "BST", "Asia/Dacca",
        // VST is the ID for Vietnam Standard Time time zone.
        /*GMT+7*/ "VST", "Asia/Bangkok",
        // CTT is the ID for China Taiwan Time time zone.
        /*GMT+8*/ "CTT", "Asia/Shanghai",
        // JST is the ID for Japan Standard Time time zone.
        /*GMT+9*/ "JST", "Asia/Tokyo",
        // ACT is the ID for Australia Central Time time zone.
        /*GMT+0930*/ "ACT", "Australia/Darwin",
        // AET is the ID for Australia Eastern Time time zone.
        /*GMT+10*/ "AET", "Australia/Sydney",
        // SST is the ID for Solomon Standard Time time zone.
        /*GMT+11*/ "SST", "Pacific/Guadalcanal",
        // NST is the ID for New Zealand Standard Time time zone.
        /*GMT+12*/ "NST", "Pacific/Fiji",
        // MIT is the ID for Midway Islands Time time zone.
        /*GMT-11*/ "MIT", "Pacific/Apia",
        // HST is the ID for Hawaii Standard Time time zone.
        /*GMT-10*/ "HST", "Pacific/Honolulu",
        // AST is the ID for Alaska Standard Time time zone.
        /*GMT-9*/ "AST", "America/Anchorage",
        // PST is the ID for Pacific Standard Time time zone.
        /*GMT-8*/ "PST", "America/Los_Angeles",
        // PNT is the ID for Phoenix Standard Time time zone.
        /*GMT-7*/ "PNT", "America/Phoenix",
        // MST is the ID for Mountain Standard Time time zone.
        /*GMT-7*/ "MST", "America/Denver",
        // CST is the ID for Central Standard Time time zone.
        /*GMT-6*/ "CST", "America/Chicago",
        // EST is the ID for Eastern Standard Time time zone.
        /*GMT-5*/ "EST", "America/New_York",
        // IET is the ID for Indiana Eastern Standard Time time zone.
        /*GMT-5*/ "IET", "America/Indianapolis",
        // PRT is the ID for Puerto Rico and US Virgin Islands Time time zone.
        /*GMT-4*/ "PRT", "America/Caracas",
        // CNT is the ID for Canada Newfoundland Time time zone.
        /*GMT-0330*/ "CNT", "America/St_Johns",
        // AGT is the ID for Argentina Standard Time time zone.
        /*GMT-3*/ "AGT", "America/Buenos_Aires",
        // BET is the ID for Brazil Eastern Time time zone.
        /*GMT-3*/ "BET", "America/Sao_Paulo",
        // CAT is the ID for Central African Time time zone.
        /*GMT-1*/ "CAT", "Atlantic/Cape_Verde",
    };

    private static Hashtable lookup = new Hashtable(zones.length);

    static {
        for (int i = 0; i < zones.length; ++i)
            lookup.put(zones[i].getID(), zones[i]);

        // We must create a new array with the cloned zones
        SimpleTimeZone[] newZones =
            new SimpleTimeZone[zones.length + (compatibilityMap.length / 2)];
        System.arraycopy(zones, 0, newZones, 0, zones.length);

        for (int i=0; i<compatibilityMap.length; i+=2)
        {
            // Make the map recognize the three-letter abbreviations as keys
            if (false)
            {
                // Debugging code
                if (lookup.get(compatibilityMap[i+1]) == null)
                    throw new InternalError("Bad TimeZone.idMap at " + i);
                if (lookup.get(compatibilityMap[i]) != null)
                    throw new InternalError("Duplicate compatibilityMap " + compatibilityMap[i]);
            }

            // Implement the three-letter zone names in addition to the
            // long zone names.
            SimpleTimeZone zone =
                (SimpleTimeZone) ((TimeZone) lookup.get(compatibilityMap[i+1])).clone();
            zone.setID(compatibilityMap[i]);
            newZones[zones.length + (i / 2)] = zone;
            lookup.put(compatibilityMap[i], zone);
        }

        zones = newZones;

        // Determine the MAXIMUM_ZONES_PER_OFFSET.  To use this to recompute the
        // maximum zones per offset, first set MAXIMUM_ZONES_PER_OFFSET to a
        // large value (like 100).  Then recompile.  Then set the boolean in the
        // if condition below to true.  Recompile again.  Now run any program
        // which will trigger this static initialization block.  Note the output
        // value.  Change MAXIMUM_ZONES_PER_OFFSET to the displayed value,
        // change the if condition back to false, and recompile again.
        if (false) {
            int max = 0;
            for (int i=-12; i<=12; ++i) {
                int n = TimeZone.getAvailableIDs(i * 60*60*1000).length;
                if (n > max) max = n;
            }
            System.out.println("    private static final int MAXIMUM_ZONES_PER_OFFSET = " +
                               max + ";");
        }
    }

    /**
     * Map a long ID to a short ID, if possible.  This method is used for
     * backward compatibility.
     * @param id the TimeZone long id.
     * @result the corresponding short ID, or the same ID if no short ID is found.
     */
    final static String mapLongIDtoShortID(String id) {
        for (int i=1; i<compatibilityMap.length; i+=2) {
            if (id.equals(compatibilityMap[i]))
                return compatibilityMap[i-1];
        }
        return id;
    }
}

//eof
