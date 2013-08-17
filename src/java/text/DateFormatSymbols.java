/*
 * @(#)DateFormatSymbols.java	1.20 98/01/12
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

package java.text;
import java.util.Locale;
import java.util.ResourceBundle;
import java.io.Serializable;
import java.util.Hashtable;

/**
 * <code>DateFormatSymbols</code> is a public class for encapsulating
 * localizable date-time formatting data, such as the names of the
 * months, the names of the days of the week, and the time zone data.
 * <code>DateFormat</code> and <code>SimpleDateFormat</code> both use
 * <code>DateFormatSymbols</code> to encapsulate this information.
 *
 * <p>
 * Typically you shouldn't use <code>DateFormatSymbols</code> directly.
 * Rather, you are encouraged to create a date-time formatter with the
 * <code>DateFormat</code> class's factory methods: <code>getTimeInstance</code>,
 * <code>getDateInstance</code>, or <code>getDateTimeInstance</code>. 
 * These methods automatically create a <code>DateFormatSymbols</code> for
 * the formatter so that you don't have to. After the
 * formatter is created, you may modify its format pattern using the
 * <code>setPattern</code> method. For more information about
 * creating formatters using <code>DateFormat</code>'s factory methods,
 * see <a href="java.text.DateFormat.html"><code>DateFormat</code></a>.
 *
 * <p>
 * If you decide to create a date-time formatter with a specific
 * format pattern for a specific locale, you can do so with:
 * <blockquote>
 * <pre>
 * new SimpleDateFormat(aPattern, new DateFormatSymbols(aLocale)).
 * </pre>
 * </blockquote>
 *
 * <p>
 * <code>DateFormatSymbols</code> objects are clonable. When you obtain
 * a <code>DateFormatSymbols</code> object, feel free to modify the
 * date-time formatting data. For instance, you can replace the localized
 * date-time format pattern characters with the ones that you feel easy
 * to remember. Or you can change the representative cities
 * to your favorite ones.
 *
 * <p>
 * New <code>DateFormatSymbols</code> subclasses may be added to support
 * <code>SimpleDateFormat</code> for date-time formatting for additional locales.

 * @see          DateFormat
 * @see          SimpleDateFormat
 * @see          java.util.SimpleTimeZone
 * @version      1.20 01/12/98
 * @author       Chen-Lieh Huang
 */
public class DateFormatSymbols implements Serializable, Cloneable {

    /**
     * Construct a DateFormatSymbols object by loading format data from
     * resources for the default locale.
     *
     * @exception  java.util.MissingResourceException
     *             if the resources for the default locale cannot be
     *             found or cannot be loaded.
     */
    public DateFormatSymbols()
    {
        initializeData(Locale.getDefault());
    }

    /**
     * Construct a DateFormatSymbols object by loading format data from
     * resources for the given locale.
     *
     * @exception  java.util.MissingResourceException
     *             if the resources for the specified locale cannot be
     *             found or cannot be loaded.
     */
    public DateFormatSymbols(Locale locale)
    {
        initializeData(locale);
    }

    /**
     * Era strings. For example: "AD" and "BC".
     */
    String eras[] = null;
    /**
     * Month strings. For example: "January", "February", etc.
     */
    String months[] = null;
    /**
     * Short month strings. For example: "Jan", "Feb", etc.
     */
    String shortMonths[] = null;
    /**
     * Weekday strings. For example: "Sunday", "Monday", etc.
     */
    String weekdays[] = null;
    /**
     * Short weekday strings. For example: "Sun", "Mon", etc.
     */
    String shortWeekdays[] = null;
    /**
     * Ampm strings. For example: "AM" and "PM".
     */
    String ampms[] = null;
    /**
     * The format data of all the timezones in this locale.
     */
    String zoneStrings[][] = null;
    /**
     * Unlocalized date-time pattern characters. For example: 'y', 'd', etc.
     * All locales use the same these unlocalized pattern characters.
     */
    static final String  patternChars = "GyMdkHmsSEDFwWahKz";
    /**
     * Localized date-time pattern characters. For example: use 'u' as 'y'.
     */
    String  localPatternChars = null;

    static final long serialVersionUID = -5987973545549424702L;

    /**
     * Gets era strings. For example: "AD" and "BC".
     * @return the era strings.
     */
    public String[] getEras() {
        return duplicate(eras);
    }

    /**
     * Sets era strings. For example: "AD" and "BC".
     * @param newEras the new era strings.
     */
    public void setEras(String[] newEras) {
        eras = duplicate(newEras);
    }

    /**
     * Gets month strings. For example: "January", "February", etc.
     * @return the month strings.
     */
    public String[] getMonths() {
        return duplicate(months);
    }

    /**
     * Sets month strings. For example: "January", "February", etc.
     * @param newMonths the new month strings.
     */
    public void setMonths(String[] newMonths) {
        months = duplicate(newMonths);
    }

    /**
     * Gets short month strings. For example: "Jan", "Feb", etc.
     * @return the short month strings.
     */
    public String[] getShortMonths() {
        return duplicate(shortMonths);
    }

    /**
     * Sets short month strings. For example: "Jan", "Feb", etc.
     * @param newShortMonths the new short month strings.
     */
    public void setShortMonths(String[] newShortMonths) {
        shortMonths = duplicate(newShortMonths);
    }

    /**
     * Gets weekday strings. For example: "Sunday", "Monday", etc.
     * @return the weekday strings.
     */
    public String[] getWeekdays() {
        return duplicate(weekdays);
    }

    /**
     * Sets weekday strings. For example: "Sunday", "Monday", etc.
     * @param newWeekdays the new weekday strings.
     */
    public void setWeekdays(String[] newWeekdays) {
        weekdays = duplicate(newWeekdays);
    }

    /**
     * Gets short weekday strings. For example: "Sun", "Mon", etc.
     * @return the short weekday strings.
     */
    public String[] getShortWeekdays() {
        return duplicate(shortWeekdays);
    }

    /**
     * Sets short weekday strings. For example: "Sun", "Mon", etc.
     * @param newShortWeekdays the new short weekday strings.
     */
    public void setShortWeekdays(String[] newShortWeekdays) {
        shortWeekdays = duplicate(newShortWeekdays);
    }

    /**
     * Gets ampm strings. For example: "AM" and "PM".
     * @return the weekday strings.
     */
    public String[] getAmPmStrings() {
        return duplicate(ampms);
    }

    /**
     * Sets ampm strings. For example: "AM" and "PM".
     * @param newAmpms the new ampm strings.
     */
    public void setAmPmStrings(String[] newAmpms) {
        ampms = duplicate(newAmpms);
    }

    /**
     * Gets timezone strings.
     * @return the timezone strings.
     */
    public String[][] getZoneStrings() {
        String[][] aCopy = new String[zoneStrings.length][];
        for (int i = 0; i < zoneStrings.length; ++i)
            aCopy[i] = duplicate(zoneStrings[i]);
        return aCopy;
    }

    /**
     * Sets timezone strings.
     * @param newZoneStrings the new timezone strings.
     */
    public void setZoneStrings(String[][] newZoneStrings) {
        String[][] aCopy = new String[newZoneStrings.length][];
        for (int i = 0; i < newZoneStrings.length; ++i)
            aCopy[i] = duplicate(newZoneStrings[i]);
        zoneStrings = aCopy;
    }

    /**
     * Gets localized date-time pattern characters. For example: 'u', 't', etc.
     * @return the localized date-time pattern characters.
     */
    public String getLocalPatternChars() {
        return new String(localPatternChars);
    }

    /**
     * Sets localized date-time pattern characters. For example: 'u', 't', etc.
     * @param newLocalPatternChars the new localized date-time
     * pattern characters.
     */
    public void setLocalPatternChars(String newLocalPatternChars) {
        localPatternChars = new String(newLocalPatternChars);
    }

    /**
     * Overrides Cloneable
     */
    public Object clone()
    {
        try
        {
            DateFormatSymbols other = (DateFormatSymbols)super.clone();
            copyMembers(this, other);
            return other;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    /**
     * Override hashCode.
     * Generates a hash code for the DateFormatSymbols object.
     */
    public int hashCode() {
        int hashcode = 0;
        for (int index = 0; index < this.zoneStrings[0].length; ++index)
            hashcode ^= this.zoneStrings[0][index].hashCode();
        return hashcode;
    }

    /**
     * Override equals
     */
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DateFormatSymbols that = (DateFormatSymbols) obj;
        return (Utility.arrayEquals(eras, that.eras)
                && Utility.arrayEquals(months, that.months)
                && Utility.arrayEquals(shortMonths, that.shortMonths)
                && Utility.arrayEquals(weekdays, that.weekdays)
                && Utility.arrayEquals(shortWeekdays, that.shortWeekdays)
                && Utility.arrayEquals(ampms, that.ampms)
                && Utility.arrayEquals(zoneStrings, that.zoneStrings)
                && Utility.arrayEquals(localPatternChars,
                                       that.localPatternChars));
    }

    // =======================privates===============================

    /**
     * Useful constant for defining timezone offsets.
     */
    static final int millisPerHour = 60*60*1000;
    
    /**
     * Cache to hold the LocaleElements and DateFormatZoneData ResourceBundles
     * of a Locale.
     */
    private static final Hashtable cachedLocaleData = new Hashtable(3);
    
    private void initializeData(Locale desiredLocale)
    {
	int i;
	ResourceBundle resource;
	ResourceBundle zoneResource;
	/* try the cache first */
	ResourceBundle[] data = 
	    (ResourceBundle[]) cachedLocaleData.get(desiredLocale);
	if (data == null) {   /* cache miss */
	    data = new ResourceBundle[2];
	    data[0] = ResourceBundle.getBundle
		("java.text.resources.LocaleElements", desiredLocale);
	    data[1] = ResourceBundle.getBundle
		("java.text.resources.DateFormatZoneData", desiredLocale);
	    /* update cache */
	    cachedLocaleData.put(desiredLocale, data);
	}
	resource = data[0];
	zoneResource = data[1];

        eras = (String[])resource.getObject("Eras");
        months = resource.getStringArray("MonthNames");
        shortMonths = resource.getStringArray("MonthAbbreviations");
        String[] lWeekdays = resource.getStringArray("DayNames");
        weekdays = new String[8];
        weekdays[0] = "";  // 1-based
        for (i=0; i<lWeekdays.length; i++)
            weekdays[i+1] = lWeekdays[i];
        String[] sWeekdays = resource.getStringArray("DayAbbreviations");
        shortWeekdays = new String[8];
        shortWeekdays[0] = "";  // 1-based
        for (i=0; i<sWeekdays.length; i++)
            shortWeekdays[i+1] = sWeekdays[i];
        ampms = resource.getStringArray("AmPmMarkers");
        zoneStrings = (String[][])zoneResource.getObject("zoneStrings");
        localPatternChars
            = (String) zoneResource.getObject("localPatternChars");
    }

    /**
     * Package private: used by SimpleDateFormat
     * Gets the index for the given time zone ID to obtain the timezone
     * strings for formatting. The time zone ID is just for programmatic
     * lookup. NOT LOCALIZED!!!
     * @param ID the given time zone ID.
     * @return the index of the given time zone ID.  Returns -1 if
     * the given time zone ID can't be located in the DateFormatSymbols object.
     * @see java.util.SimpleTimeZone
     */
    final int getZoneIndex (String ID)
    {
        for (int index=0; index<zoneStrings.length; index++)
        {
            if (ID.equalsIgnoreCase(zoneStrings[index][0])) return index;
        }

        return -1;
    }

    /**
     * Clones an array of Strings.
     * @param srcArray the source array to be cloned.
     * @param count the number of elements in the given source array.
     * @return a cloned array.
     */
    private final String[] duplicate(String[] srcArray)
    {
        String[] dstArray = new String[srcArray.length];
        System.arraycopy(srcArray, 0, dstArray, 0, srcArray.length);
        return dstArray;
    }

    /**
     * Clones all the data members from the source DateFormatSymbols to
     * the target DateFormatSymbols. This is only for subclasses.
     * @param src the source DateFormatSymbols.
     * @param dst the target DateFormatSymbols.
     */
    private final void copyMembers(DateFormatSymbols src, DateFormatSymbols dst)
    {
        dst.eras = duplicate(src.eras);
        dst.months = duplicate(src.months);
        dst.shortMonths = duplicate(src.shortMonths);
        dst.weekdays = duplicate(src.weekdays);
        dst.shortWeekdays = duplicate(src.shortWeekdays);
        dst.ampms = duplicate(src.ampms);
        for (int i = 0; i < dst.zoneStrings.length; ++i)
            dst.zoneStrings[i] = duplicate(src.zoneStrings[i]);
        dst.localPatternChars = new String (src.localPatternChars);
    }

    /**
     * Compares the equality of the two arrays of String.
     * @param current this String array.
     * @param other that String array.
     */
    private final boolean equals(String[] current, String[] other)
    {
        int count = current.length;

        for (int i = 0; i < count; ++i)
            if (!current[i].equals(other[i]))
                return false;
        return true;
    }

}
