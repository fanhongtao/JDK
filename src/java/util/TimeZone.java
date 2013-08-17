/*
 * @(#)TimeZone.java	1.17 97/01/29
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
 * @version      1.17 01/29/97
 * @author       Mark Davis, David Goldsmith, Chen-Lieh Huang
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
     * @param milliseconds the millis in day.
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
     * @return a TimeZone.
     */
    public static synchronized TimeZone getTimeZone(String ID)
    {
        try {
            return (SimpleTimeZone) lookup.get(ID);
        } catch (MissingResourceException e) {
            return new SimpleTimeZone(-8*millisPerHour, "PST",
            Calendar.APRIL, 1, Calendar.SUNDAY, 2*millisPerHour,
            Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*millisPerHour);
        }
    }

    /**
     * Gets the available IDs according to the given time zone offset.
     * @param rawOffset the given time zone GMT offset.
     * @return an array of IDs, where the time zone for that ID has
     * the specified GMT offset. For example, {"Phoenix", "Denver"},
     * since both have GMT-07:00, but differ in daylight savings behavior.
     */
    public static synchronized String[] getAvailableIDs(int rawOffset) {
        String[]    resultArray = new String[10]; // normally 2 ~ 3 IDs
        int         count = 0;
        for (int i = 0; i < timeZoneData.length; ++i)
            if (rawOffset == timeZoneData[i].getRawOffset())
                resultArray[count++] = timeZoneData[i].getID();

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
        String[]    resultArray = new String[40];
        int         count = 0;

        for (int i = 0; i < timeZoneData.length; ++i)
            resultArray[count++] = timeZoneData[i].getID();

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
            // get the ID from the system properties
            String ID = System.getProperty("user.timezone", "GMT");
            if (ID != null) {
                defaultZone = getTimeZone(ID);
                if (defaultZone != null)
                    return defaultZone;
            }

            // we couldn't get it from the properties, so use the
            // offset from the native (host) system.
            int rawOffset = -8*millisPerHour;

            // BRIAN FIXME
            // reset rawOffset to be the system offset from GMT
            String[] matches = getAvailableIDs(rawOffset);
            if (matches != null) {
                defaultZone = getTimeZone(matches[0]);
            }
            if (defaultZone == null) {
                defaultZone = getTimeZone("PST");
            }
            return defaultZone;
        }
        else return defaultZone;
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
    private static TimeZone  defaultZone=null;
    private static final int millisPerHour = 60*60*1000;

    private static final SimpleTimeZone[] timeZoneData = {
        // GMT is the ID for Greenwich Mean Time time zone.
        new SimpleTimeZone(0, "GMT",
            Calendar.MARCH, -1, Calendar.SUNDAY, 2*millisPerHour,
            Calendar.OCTOBER, 4, Calendar.SUNDAY, 2*millisPerHour),
        // ECT is the ID for European Central Time time zone.
        new SimpleTimeZone(1*millisPerHour, "ECT",
            Calendar.MARCH, -1, Calendar.SUNDAY, 2*millisPerHour,
            Calendar.SEPTEMBER, -1, Calendar.SUNDAY, 2*millisPerHour),
        // EET is the ID for Eastern European Time time zone.
        new SimpleTimeZone(2*millisPerHour, "EET",
            Calendar.MARCH, -1, Calendar.SUNDAY, 2*millisPerHour,
            Calendar.SEPTEMBER, -1, Calendar.SUNDAY, 2*millisPerHour),
        // ART is the ID for (Arabic) Egypt Standard Time timezone.
        new SimpleTimeZone(2*millisPerHour, "ART", // 5/1 ~ 10/1
            Calendar.MAY, 1, Calendar.SUNDAY, 2*millisPerHour,
            Calendar.OCTOBER, 1, Calendar.SUNDAY, 2*millisPerHour),
        // EAT is the ID for Eastern African Time time zone.
        new SimpleTimeZone(3*millisPerHour, "EAT"),
        // MET is the ID for Middle East Time time zone.
        new SimpleTimeZone((int)(3.5*millisPerHour), "MET"),
        // NET is the ID for Near East Time time zone.
        new SimpleTimeZone(4*millisPerHour, "NET"),
        // PLT is the ID for Pakistan Lahore Time time zone.
        new SimpleTimeZone(5*millisPerHour, "PLT"),
        // IST is the ID for India Standard Time time zone.
        new SimpleTimeZone((int)(5.5*millisPerHour), "IST"),
        // BST is the ID for Bangladesh Standard Time time zone.
        new SimpleTimeZone(6*millisPerHour, "BST"),
        // VST is the ID for Vietnam Standard Time time zone.
        new SimpleTimeZone(7*millisPerHour, "VST"),
        // CTT is the ID for China Taiwan Time time zone.
        new SimpleTimeZone(8*millisPerHour, "CTT"),
        // JST is the ID for Japan Standard Time time zone.
        new SimpleTimeZone(9*millisPerHour, "JST"),
        // ACT is the ID for Australia Central Time time zone.
        new SimpleTimeZone((int)(9.5*millisPerHour), "ACT",
            Calendar.APRIL, 1, Calendar.SUNDAY, 2*millisPerHour,
            Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*millisPerHour),
        // AET is the ID for Australia Eastern Time time zone.
        new SimpleTimeZone(10*millisPerHour, "AET",
            Calendar.APRIL, 1, Calendar.SUNDAY, 2*millisPerHour,
            Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*millisPerHour),
        // SST is the ID for Solomon Standard Time time zone.
        new SimpleTimeZone(11*millisPerHour, "SST"),
        // NST is the ID for New Zealand Standard Time time zone.
        new SimpleTimeZone(12*millisPerHour, "NST",
            Calendar.APRIL, 1, Calendar.SUNDAY, 2*millisPerHour,
            Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*millisPerHour),
        // MIT is the ID for Midway Islands Time time zone.
        new SimpleTimeZone(-11*millisPerHour, "MIT"),
        // HST is the ID for Hawaii Standard Time time zone.
        new SimpleTimeZone(-10*millisPerHour, "HST",
            Calendar.APRIL, 1, Calendar.SUNDAY, 2*millisPerHour,
            Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*millisPerHour),
        // AST is the ID for Alaska Standard Time time zone.
        new SimpleTimeZone(-9*millisPerHour, "AST",
            Calendar.APRIL, 1, Calendar.SUNDAY, 2*millisPerHour,
            Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*millisPerHour),
        // PST is the ID for Pacific Standard Time time zone.
        new SimpleTimeZone(-8*millisPerHour, "PST",
            Calendar.APRIL, 1, Calendar.SUNDAY, 2*millisPerHour,
            Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*millisPerHour),
        // PNT is the ID for Phoenix Standard Time time zone.
        new SimpleTimeZone(-7*millisPerHour, "PNT"),
        // MST is the ID for Mountain Standard Time time zone.
        new SimpleTimeZone(-7*millisPerHour, "MST",
            Calendar.APRIL, 1, Calendar.SUNDAY, 2*millisPerHour,
            Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*millisPerHour),
        // CST is the ID for Central Standard Time time zone.
        new SimpleTimeZone(-6*millisPerHour, "CST",
            Calendar.APRIL, 1, Calendar.SUNDAY, 2*millisPerHour,
            Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*millisPerHour),
        // EST is the ID for Eastern Standard Time time zone.
        new SimpleTimeZone(-5*millisPerHour, "EST",
            Calendar.APRIL, 1, Calendar.SUNDAY, 2*millisPerHour,
            Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*millisPerHour),
        // IET is the ID for Indiana Eastern Standard Time time zone.
        new SimpleTimeZone(-5*millisPerHour, "IET"),
        // PRT is the ID for Puerto Rico and US Virgin Islands Time time zone.
        new SimpleTimeZone(-4*millisPerHour, "PRT"),
        // CNT is the ID for Canada Newfoundland Time time zone.
        new SimpleTimeZone((int)(-3.5*millisPerHour), "CNT",
            Calendar.APRIL, 1, Calendar.SUNDAY, 2*millisPerHour,
            Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*millisPerHour),
        // AGT is the ID for Argentina Standard Time time zone.
        new SimpleTimeZone(-3*millisPerHour, "AGT"),
        // BET is the ID for Brazil Eastern Time time zone.
        new SimpleTimeZone(-3*millisPerHour, "BET",
            Calendar.APRIL, 1, Calendar.SUNDAY, 2*millisPerHour,
            Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*millisPerHour),
        // CAT is the ID for Central African Time time zone.
        new SimpleTimeZone(-1*millisPerHour, "CAT")
    };

    private static Hashtable lookup = new Hashtable(timeZoneData.length);
    static {
        for (int i = 0; i < timeZoneData.length; ++i)
            lookup.put(timeZoneData[i].getID(), timeZoneData[i]);
    }
}
