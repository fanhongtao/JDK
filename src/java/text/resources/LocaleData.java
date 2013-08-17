/*
 * @(#)LocaleData.java	1.12 97/02/24
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

package java.text.resources;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * An internal data warehouse for information (locale elements) needed to
 * implement locale dependent formatting for the standard set of locales
 * The current implementation utilizes a class LocaleElements which is
 * created from plain text source file(s) by a small compiler (LocaleTool)
 *
 * Example of use:
 * <pre>
 *  Locale desiredLocale = new Locale("DA","DK","");
 *  ResourceBundle resource = ResourceBundle.load("java.util.LocaleElements",
 *                                    desiredLocale);
 *  // Pass one of the following:
 *  "LocaleString",      // locale id based on iso codes
 *  "LocaleID",          // Windows id
 *  "ShortLanguage",     // iso-3 abbrev lang name
 *  "ShortCountry",      // iso-3 abbrev country name
 *  "Languages",         // language names
 *  "Countries",         // country names
 *  "MonthNames",        // ARRAY january
 *  "MonthAbbreviations",   // ARRAY abb january
 *  "DayNames",          // ARRAY Monday
 *  "DayAbbreviations",  // ARRAY abb Monday
 *  "AmPmMarkers",       // ARRAY am marker
 *  "Eras",              // era strings
 *  "NumberPatterns",    // ARRAY decimal pattern
 *  "CurrencyElements",  // ARRAY local currency symbol
 *  "DateTimePatterns",  // ARRAY full time pattern
 *  "DateTimeElements",  // ARRAY first day of week
 *  "CollationElements", // collation order
 *
 *  String myString string = resource.getString(LocaleString);
 *  // or
 *  String[] myStrings strings = resource.getStringArray(LocaleString);
 * </pre>
 * @author Asmus Freytag
 * @author Mark Davis
 * @version 1.12 02/24/97
 */

public class LocaleData extends ResourceBundle {

    // each subclass MUST call Init() in its constructor.
    public Object handleGetObject(String key)
    {
        return localeKeys.get(key);
    }

    // use to get the list of locales that have this data
    public static Locale[] getAvailableLocales(String key)
    {
        return localeList;  // hard-coded for now
    }

    /**
     * Implementation of ResourceBundle.getKeys.
     */
    public Enumeration getKeys() {
        return localeKeys.keys();
    }

    // given string data that matches the keys, sets up the hash
    // table for quick access.
    protected void init(String[] data) {
        if (data.length != keys.length) {
            System.out.println("Data length (" + data.length +
                               ") != key length (" + keys.length + ")");
            throw new ArrayIndexOutOfBoundsException();
        }
        // set ups a hashtable for the locale keys
        localeKeys = new Hashtable();

        int oldIndex = 0;
        int i;
        for(i = 1; i < data.length; i++ )
        {
            if (!keys[i].equals(keys[oldIndex])) {
                storeStrings(data,oldIndex, i-1);
                oldIndex = i;
            }
        }
        storeStrings(data,oldIndex, i-1);
    }

    // ========== privates ==========

    private Hashtable localeKeys;

    /*
     * Splits the string into pieces that it returns in an array.
     * @param source string to split
     * @separator char to use to split. NOTE: two separators in a row,
     * or one at the end or start, will produce a null string; e.g.
     * ";a;b;" -> {"", "a", "b", ""}
     */
    private static String[] split(String source, char separator) {
        String[] temp = new String[200]; // TODO remove limited length
        int oldOffset = 0;
        int i;
        for (i = 0; oldOffset < source.length(); ++i) {
            int newOffset = source.indexOf(separator, oldOffset);
            if (newOffset == -1)
                newOffset = source.length();
            temp[i] = source.substring(oldOffset, newOffset);
            oldOffset = newOffset + 1;
        }
        String[] result = new String[i];
        System.arraycopy(temp,0,result,0,i);
        return result;
    }
    // stores either a single string, or an array (in case of multiple keys)
    private void storeStrings(String[] data, int start, int end) {
        if (keys[start].equals("Eras")
            || keys[start].equals("Languages")
            || keys[start].equals("Countries")) {
            String[] strings = split(data[start], ';');
            // workaround for bug
            for (int i = 0; i < strings.length; ++i)
                strings[i] = strings[i].trim();
            // break up "_'
            if (keys[start].equals("Eras"))
                localeKeys.put(keys[start], strings);
            else {
                String[][] pairedStrings = new String[strings.length][2];
                for (int i = 0; i < strings.length; ++i)
                    pairedStrings[i] = split(strings[i],'_');
                localeKeys.put(keys[start], pairedStrings);
            }
        } else if (start == end) { // store single string
            localeKeys.put(keys[start], data[start]);
        } else {
            String[] temp = new String[end - start + 1];
            System.arraycopy(data, start, temp, 0, end - start + 1);
            localeKeys.put(keys[start], temp);
        }
    }

    private static String keys[] = {
        "LocaleString",/*locale id based on iso codes*/
        "LocaleID",/*Windows id*/
        "ShortLanguage",/*iso-3 abbrev lang name*/
        "ShortCountry",/*iso-3 abbrev country name*/
        "Languages",/*language names*/
        "Countries",/*country names*/
        "MonthNames",/*january*/
        "MonthNames",/*february*/
        "MonthNames",/*march*/
        "MonthNames",/*april*/
        "MonthNames",/*may*/
        "MonthNames",/*june*/
        "MonthNames",/*july*/
        "MonthNames",/*august*/
        "MonthNames",/*september*/
        "MonthNames",/*october*/
        "MonthNames",/*november*/
        "MonthNames",/*december*/
        "MonthNames",/*month 13 if applicable*/
        "MonthAbbreviations",/*abb january*/
        "MonthAbbreviations",/*abb february*/
        "MonthAbbreviations",/*abb march*/
        "MonthAbbreviations",/*abb april*/
        "MonthAbbreviations",/*abb may*/
        "MonthAbbreviations",/*abb june*/
        "MonthAbbreviations",/*abb july*/
        "MonthAbbreviations",/*abb august*/
        "MonthAbbreviations",/*abb september*/
        "MonthAbbreviations",/*abb october*/
        "MonthAbbreviations",/*abb november*/
        "MonthAbbreviations",/*abb december*/
        "MonthAbbreviations",/*abb month 13 if applicable*/
        "DayNames",/*Monday*/
        "DayNames",/*Tuesday*/
        "DayNames",/*Wednesday*/
        "DayNames",/*Thursday*/
        "DayNames",/*Friday*/
        "DayNames",/*Saturday*/
        "DayNames",/*Sunday*/
        "DayAbbreviations",/*abb Monday*/
        "DayAbbreviations",/*abb Tuesday*/
        "DayAbbreviations",/*abb Wednesday*/
        "DayAbbreviations",/*abb Thursday*/
        "DayAbbreviations",/*abb Friday*/
        "DayAbbreviations",/*abb Saturday*/
        "DayAbbreviations",/*abb Sunday*/
        "AmPmMarkers",/*am marker*/
        "AmPmMarkers",/*pm marker*/
        "Eras",/*era strings*/
        "NumberPatterns",/*decimal pattern*/
        "NumberPatterns",/*currency pattern*/
        "NumberPatterns",/*percent pattern*/
        "NumberElements",/*decimal separator*/
        "NumberElements",/*group (thousands) separator*/
        "NumberElements",/*list separator*/
        "NumberElements",/*percent sign*/
        "NumberElements",/*native 0 digit*/
        "NumberElements",/*pattern digit*/
        "NumberElements",/*minus sign*/
        "NumberElements",/*exponential*/
        "CurrencyElements",/*local currency symbol*/
        "CurrencyElements",/*intl currency symbol*/
        "CurrencyElements",/*monetary decimal separator*/
        "DateTimePatterns",/*full time pattern*/
        "DateTimePatterns",/*long time pattern*/
        "DateTimePatterns",/*medium time pattern*/
        "DateTimePatterns",/*short time pattern*/
        "DateTimePatterns",/*full date pattern*/
        "DateTimePatterns",/*long date pattern*/
        "DateTimePatterns",/*medium date pattern*/
        "DateTimePatterns",/*short date pattern*/
        "DateTimePatterns",/*date-time pattern*/
        "DateTimeElements",/*first day of week*/
        "DateTimeElements",/*min days in first week*/
        "CollationElements",/*collation order*/
    };

    // for now, we hard-code the enumeration
    private static Locale[] localeList = {
	new Locale("ar", "", ""),
	new Locale("be", "", ""),
	new Locale("bg", "", ""),
	new Locale("ca", "", ""),
	new Locale("cs", "", ""),
	new Locale("da", "", ""),
	new Locale("de", "", ""),
	new Locale("de", "AT", ""),
	new Locale("de", "CH", ""),
	new Locale("el", "", ""),
	new Locale("en", "CA", ""),
	new Locale("en", "GB", ""),
	new Locale("en", "IE", ""),
	new Locale("en", "US", ""),
	new Locale("es", "", ""),
	new Locale("et", "", ""),
	new Locale("fi", "", ""),
	new Locale("fr", "", ""),
	new Locale("fr", "BE", ""),
	new Locale("fr", "CA", ""),
	new Locale("fr", "CH", ""),
	new Locale("hr", "", ""),
	new Locale("hu", "", ""),
	new Locale("is", "", ""),
	new Locale("it", "", ""),
	new Locale("it", "CH", ""),
	new Locale("iw", "", ""),
	new Locale("ja", "", ""),
	new Locale("ko", "", ""),
	new Locale("lt", "", ""),
	new Locale("lv", "", ""),
	new Locale("mk", "", ""),
	new Locale("nl", "", ""),
	new Locale("nl", "BE", ""),
	new Locale("no", "", ""),
	new Locale("no", "NO", "NY"),
	new Locale("pl", "", ""),
	new Locale("pt", "", ""),
	new Locale("ro", "", ""),
	new Locale("ru", "", ""),
	new Locale("sh", "", ""),
	new Locale("sk", "", ""),
	new Locale("sl", "", ""),
	new Locale("sq", "", ""),
	new Locale("sr", "", ""),
	new Locale("sv", "", ""),
	new Locale("tr", "", ""),
	new Locale("uk", "", ""),
	new Locale("zh", "", ""),
	new Locale("zh", "TW", "")
    };
}
