/*
 * @(#)SimpleDateFormat.java	1.22 97/03/10
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
import java.util.TimeZone;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.SimpleTimeZone;
import java.util.GregorianCalendar;

/**
 * <code>SimpleDateFormat</code> is a concrete class for formatting and
 * parsing dates in a locale-sensitive manner. It allows for formatting
 * (millis -> text), parsing (text -> millis), and normalization. 
 *
 * <p>
 * <code>SimpleDateFormat</code> allows you to start by choosing
 * any user-defined patterns for date-time formatting. However, you
 * are encouraged to create a date-time formatter with either
 * <code>getTimeInstance</code>, <code>getDateInstance</code>, or
 * <code>getDateTimeInstance</code> in <code>DateFormat</code>. Each
 * of these class methods can return a date/time formatter initialized
 * with a default format pattern. You may modify the format pattern
 * using the <code>applyPattern</code> methods as desired.
 * For more information on using these methods, see
 * <a href="java.text.DateFormat.html"><code>DateFormat</code></a>.
 *
 * <p> 
 * <strong>Time Format Syntax:</strong>
 * <p>
 * To specify the time format use a <em>time pattern</em> string.
 * In this pattern, all ASCII letters are reserved as pattern letters,
 * which are defined as the following:
 * <blockquote>
 * <pre>
 * Symbol   Meaning                 Presentation        Example
 * ------   -------                 ------------        -------
 * G        era designator          (Text)              AD
 * y        year                    (Number)            1996
 * M        month in year           (Text & Number)     July & 07
 * d        day in month            (Number)            10
 * h        hour in am/pm (1~12)    (Number)            12
 * H        hour in day (0~23)      (Number)            0
 * m        minute in hour          (Number)            30
 * s        second in minute        (Number)            55
 * S        millisecond             (Number)            978
 * E        day in week             (Text)              Tuesday
 * D        day in year             (Number)            189
 * F        day of week in month    (Number)            2 (2nd Wed in July)
 * w        week in year            (Number)            27
 * W        week in month           (Number)            2
 * a        am/pm marker            (Text)              PM
 * k        hour in day (1~24)      (Number)            24
 * K        hour in am/pm (0~11)    (Number)            0
 * z        time zone               (Text)              Pacific Standard Time
 * '        escape for text
 * ''        single quote                              '
 * </pre>
 * </blockquote>
 * The count of pattern letters determine the format.
 * <p>
 * <strong>(Text)</strong>: 4 or more pattern letters--use full form,
 * &lt 4--use short or abbreviated form if one exists.
 * <p>
 * <strong>(Number)</strong>: the minimum number of digits. Shorter
 * numbers are zero-padded to this amount. Year is handled specially;
 * that is, if the count of 'y' is 2, the Year will be truncated to 2 digits.
 * <p>
 * <strong>(Text & Number)</strong>: 3 or over, use text, otherwise use number.
 * <p>
 * Any characters in the pattern that are not in the ranges of ['a'..'z']
 * and ['A'..'Z'] will be treated as quoted text. For instance, characters
 * like ':', '.', ' ', '#' and '@' will appear in the resulting time text
 * even they are not embraced within single quotes.
 * <p>
 * A pattern containing any invalid pattern letter will result in a thrown
 * exception during formatting or parsing.
 *
 * <p>
 * <strong>Examples Using the US Locale:</strong>
 * <blockquote>
 * <pre>
 * Format Pattern                         Result
 * --------------                         -------
 * "yyyy.MM.dd G 'at' hh:mm:ss z"    ->>  1996.07.10 AD at 15:08:56 PDT
 * "EEE, MMM d, ''yy"                ->>  Wed, July 10, '96
 * "h:mm a"                          ->>  12:08 PM
 * "hh 'o''''clock' a, zzzz"         ->>  12 o'clock PM, Pacific Daylight Time
 * "K:mm a, z"                       ->>  0:00 PM, PST
 * "yyyyy.MMMMM.dd GGG hh:mm aaa"    ->>  1996.July.10 AD 12:08 PM
 * </pre>
 * </blockquote>
 * <strong>Code Sample:</strong>
 * <pre>
 * <blockquote>
 * SimpleTimeZone pdt = new SimpleTimeZone(-8 * 60 * 60 * 1000, "PST");
 * pdt.setStartRule(DateFields.APRIL, 1, DateFields.SUNDAY, 2*60*60*1000);
 * pdt.setEndRule(DateFields.OCTOBER, -1, DateFields.SUNDAY, 2*60*60*1000);
 *
 * // Format the current time.
 * SimpleDateFormat formatter
 *     = new SimpleDateFormat ("yyyy.mm.dd e 'at' hh:mm:ss a zzz");
 * Date currentTime_1 = new Date();
 * String dateString = formatter.format(currentTime_1);
 *
 * // Parse the previous string back into a Date.
 * ParsePosition pos = new ParsePosition(0);
 * Date currentTime_2 = formatter.parse(dateString, pos);
 * </pre>
 * </blockquote>
 * In the example, the time value <code>currentTime_2</code> obtained from
 * parsing will be equal to <code>currentTime_1</code>. However, they may not be
 * equal if the am/pm marker 'a' is left out from the format pattern while
 * the "hour in am/pm" pattern symbol is used. This information loss can
 * happen when formatting the time in PM.
 *
 * <p>
 * For time zones that have no names, use strings GMT+hours:minutes or
 * GMT-hours:minutes.
 *
 * <p>
 * The calendar defines what is the first day of the week, the first week
 * of the year, whether hours are zero based or not (0 vs 12 or 24), and the
 * time zone. There is one common decimal format to handle all the numbers;
 * the digit count is handled programmatically according to the pattern.
 *
 * @see          java.util.Calendar
 * @see          java.util.GregorianCalendar
 * @see          java.util.TimeZone
 * @see          DateFormat
 * @see          DateFormatSymbols
 * @see          DecimalFormat
 * @version      1.22 03/10/97
 * @author       Mark Davis, Chen-Lieh Huang
 */
public class SimpleDateFormat extends DateFormat {

    private String pattern;
    private DateFormatSymbols formatData;

    private static final int millisPerHour = 60 * 60 * 1000;
    private static final int millisPerMinute = 60 * 1000;

    // For time zones that have no names, use strings GMT+minutes and
    // GMT-minutes. For instance, in France the time zone is GMT+60.
    private static final String GMT_PLUS = "GMT+";
    private static final String GMT_MINUS = "GMT-";
    private static final String GMT = "GMT";

    /**
     * Construct a SimpleDateFormat using the default pattern for the default
     * locale.  <b>Note:</b> Not all locales support SimpleDateFormat; for full
     * generality, use the factory methods in the DateFormat class.
     *
     * @see java.text.DateFormat
     */
    public SimpleDateFormat() {
	this(SHORT, SHORT + 4, Locale.getDefault());
    }

    /**
     * Construct a SimpleDateFormat using the given pattern in the default
     * locale.  <b>Note:</b> Not all locales support SimpleDateFormat; for full
     * generality, use the factory methods in the DateFormat class.
     */
    public SimpleDateFormat(String pattern)
    {
	this.pattern = pattern;
	this.formatData = new DateFormatSymbols();
	initialize(Locale.getDefault());
    }

    /**
     * Construct a SimpleDateFormat using the given pattern and locale.
     * <b>Note:</b> Not all locales support SimpleDateFormat; for full
     * generality, use the factory methods in the DateFormat class.
     */
    public SimpleDateFormat(String pattern, Locale loc)
    {
	this.pattern = pattern;
	this.formatData = new DateFormatSymbols(loc);
	initialize(loc);
    }

    /**
     * Construct a SimpleDateFormat using the given pattern and
     * locale-specific symbol data.
     */
    public SimpleDateFormat(String pattern, DateFormatSymbols formatData)
    {
	this.pattern = pattern;
	this.formatData = formatData;
	initialize(Locale.getDefault());
    }

    /* Package-private, called by DateFormat factory methods */
    SimpleDateFormat(int timeStyle, int dateStyle, Locale loc) {
	ResourceBundle r = ResourceBundle.getBundle
	    ("java.text.resources.LocaleElements", loc);

	formatData = new DateFormatSymbols(loc);
	String[] dateTimePatterns = r.getStringArray("DateTimePatterns");

	if ((timeStyle >= 0) && (dateStyle >= 0)) {
	    Object[] dateTimeArgs = {dateTimePatterns[timeStyle],
				     dateTimePatterns[dateStyle]};
	    pattern = MessageFormat.format(dateTimePatterns[8], dateTimeArgs);
	}
	else if (timeStyle >= 0)
	    pattern = dateTimePatterns[timeStyle];
	else if (dateStyle >= 0)
	    pattern = dateTimePatterns[dateStyle];
	else
	    throw new IllegalArgumentException("No date or time style specified");

	initialize(loc);
    }

    /* Initialize calendar and numberFormat fields */
    private void initialize(Locale loc) {
	this.calendar = (Calendar.getInstance((SimpleTimeZone)
					     TimeZone.getTimeZone(formatData.zoneStrings[0][0]),
					     loc));
	this.numberFormat = NumberFormat.getInstance(loc);
	numberFormat.setGroupingUsed(false);
	if (numberFormat instanceof DecimalFormat)
	    ((DecimalFormat)numberFormat).setDecimalSeparatorAlwaysShown(false);
        numberFormat.setParseIntegerOnly(true);	/* So that dd.mm.yy can be parsed */
    }

    /**
     * Overrides DateFormat
     * <p>Formats a date or time, which is the standard millis
     * since 24:00 GMT, Jan 1, 1970.
     * <p>Example: using the US locale:
     * "yyyy.MM.dd e 'at' HH:mm:ss zzz" ->> 1996.07.10 AD at 15:08:56 PDT
     * @param date the date-time value to be formatted into a date-time string.
     * @param toAppendTo where the new date-time text is to be appended.
     * @param pos the formatting position. On input: an alignment field,
     * if desired. On output: the offsets of the alignment field.
     * @return the formatted date-time string.
     * @see java.util.DateFormat
     */
    public StringBuffer format(Date date, StringBuffer toAppendTo,
                               FieldPosition pos)
    {
        // Initialize
        pos.beginIndex = pos.endIndex = 0;

        // Convert input date to time field list
        calendar.setTime(date);

        boolean inQuote = false; // inQuote set true when hits 1st single quote
        char prevCh = 0;
        int count = 0;  // number of time pattern characters repeated
        for (int i=0; i<pattern.length(); ++i)
        {
            char ch = pattern.charAt(i);
            if (inQuote)
            {
                if (ch == '\'')
                {
                    // ends with 2nd single quote
                    inQuote = false;
                    if (count == 0)
                        toAppendTo.append(ch);  // two consecutive quotes: ''
                    else count = 0;
                }
                else
                {
                    toAppendTo.append(ch);
                    count++;
                }
            }
            else // !inQuote
            {
                if (ch == '\'')
                {
                    inQuote = true;
                    if (count > 0) // handle cases like: yyyy'....
                    {
                        toAppendTo.append(subFormat(prevCh, count,
                                                    toAppendTo.length(),
                                                    pos));
                        count = 0;
                        prevCh = 0;
                    }
                }
                else if (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z')
                {
                    // ch is a date-time pattern
                    if (ch != prevCh && count > 0) //handle cases: eg, yyyyMMdd
                    {
                        toAppendTo.append(subFormat(prevCh, count,
                                                    toAppendTo.length(),
                                                    pos));
                        prevCh = ch;
                        count = 1;
                    }
                    else
                    {
                        if (ch != prevCh)
                            prevCh = ch;
                        count++;
                    }
                }
                else if (count > 0) // handle cases like: MM-dd-yy or HH:mm:ss
                {
                    toAppendTo.append(subFormat(prevCh, count,
                                                toAppendTo.length(),
                                                pos));
                    toAppendTo.append(ch);
                    prevCh = 0;
                    count = 0;
                }
                else // any other unquoted characters
                    toAppendTo.append(ch);
            }
        }
        // Format the last item in the pattern
        if (count > 0)
        {
            toAppendTo.append(subFormat(prevCh, count,
                                        toAppendTo.length(), pos));
        }
        return toAppendTo;
    }


    // Private member function that does the real date/time formatting.
    private String subFormat(char ch, int count, int beginOffset,
                             FieldPosition pos)
    throws IllegalArgumentException
    {
        int     value = 0;
        int     patternCharIndex = -1;
        int     maxIntCount = 10;
        String  current = "";

        if ((patternCharIndex=formatData.patternChars.indexOf(ch)) == -1)
            throw new IllegalArgumentException("Illegal pattern character " +
                                               "'" + ch + "'");
        switch (patternCharIndex) {
        case 0: // 'G' - ERA
            value = calendar.get(Calendar.ERA);
            current = formatData.eras[value];
            break;
        case 1: // 'y' - YEAR
            value = calendar.get(Calendar.YEAR);
            if (count >= 4)
//                current = zeroPaddingNumber(value, 4, count);
                current = zeroPaddingNumber(value, 4, maxIntCount);
            else // count < 4
                current = zeroPaddingNumber(value, 2, 2); // clip 1996 to 96
            break;
        case 2: // 'M' - MONTH
            value = calendar.get(Calendar.MONTH);
            if (count >= 4)
                current = formatData.months[value];
            else if (count == 3)
                current = formatData.shortMonths[value];
            else
                current = zeroPaddingNumber(value+1, count, maxIntCount);
            break;
        case 3: // 'd' - DATE
            value = calendar.get(Calendar.DATE);
            current = zeroPaddingNumber(value, count, maxIntCount);
            break;
        case 4: // 'k' - HOUR_OF_DAY: 1-based.  eg, 23:59 + 1 hour =>> 24:59
            if ((value=calendar.get(Calendar.HOUR_OF_DAY)) == 0)
                current = zeroPaddingNumber(
                    calendar.getMaximum(Calendar.HOUR_OF_DAY)+1,
                    count, maxIntCount);
            else
                current = zeroPaddingNumber(value, count, maxIntCount);
            break;
        case 5: // 'H' - HOUR_OF_DAY:0-based.  eg, 23:59 + 1 hour =>> 00:59
            value = calendar.get(Calendar.HOUR_OF_DAY);
            current = zeroPaddingNumber(value, count, maxIntCount);
            break;
        case 6: // 'm' - MINUTE
            value = calendar.get(Calendar.MINUTE);
            current = zeroPaddingNumber(value, count, maxIntCount);
            break;
        case 7: // 's' - SECOND
            value = calendar.get(Calendar.SECOND);
            current = zeroPaddingNumber(value, count, maxIntCount);
            break;
        case 8: // 'S' - MILLISECOND
            value = calendar.get(Calendar.MILLISECOND);
            if (count > 3)
                count = 3;
            else if (count == 2)
                value = value / 10;
            else if (count == 1)
                value = value / 100;
            current = zeroPaddingNumber(value, count, maxIntCount);
            break;
        case 9: // 'E' - DAY_OF_WEEK
            value = calendar.get(Calendar.DAY_OF_WEEK);
            if (count >= 4)
                current = formatData.weekdays[value];
            else // count < 4, use abbreviated form if exists
                current = formatData.shortWeekdays[value];
            break;
        case 10:    // 'D' - DAY_OF_YEAR
            value = calendar.get(Calendar.DAY_OF_YEAR);
            current = zeroPaddingNumber(value, count, maxIntCount);
            break;
        case 11:   // 'F' - DAY_OF_WEEK_IN_MONTH
            value = calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH);
            current = zeroPaddingNumber(value, count, maxIntCount);
            break;
        case 12:    // 'w' - WEEK_OF_YEAR
            value = calendar.get(Calendar.WEEK_OF_YEAR);
            current = zeroPaddingNumber(value, count, maxIntCount);
            break;
        case 13:    // 'W' - WEEK_OF_MONTH
            value = calendar.get(Calendar.WEEK_OF_MONTH);
            current = zeroPaddingNumber(value, count, maxIntCount);
            break;
        case 14:    // 'a' - AM_PM
            value = calendar.get(Calendar.AM_PM);
            current = formatData.ampms[value];
            break;
        case 15: // 'h' - HOUR:1-based.  eg, 11PM + 1 hour =>> 12 AM
            if ((value=calendar.get(Calendar.HOUR)) == 0)
                current = zeroPaddingNumber(
                    calendar.getLeastMaximum(Calendar.HOUR)+1,
                    count, maxIntCount);
            else
                current = zeroPaddingNumber(value, count, maxIntCount);
            break;
        case 16: // 'K' - HOUR: 0-based.  eg, 11PM + 1 hour =>> 0 AM
            value = calendar.get(Calendar.HOUR);
            current = zeroPaddingNumber(value, count, maxIntCount);
            break;
        case 17: // 'z' - ZONE_OFFSET
            int zoneIndex
                = formatData.getZoneIndex (calendar.getTimeZone().getID());

            if (zoneIndex == -1)
            {
                // For time zones that have no names, use strings
                // GMT+hours:minutes and GMT-hours:minutes.
                // For instance, France time zone uses GMT+01:00.
                StringBuffer zoneString = new StringBuffer();

                value = calendar.get(Calendar.ZONE_OFFSET)
                        + calendar.get(Calendar.DST_OFFSET);

                if (value < 0)
                {
                    zoneString.append(GMT_MINUS);
                    value = -value; // suppress the '-' sign for text display.
                }
                else
                    zoneString.append(GMT_PLUS);
                zoneString.append(
                    zeroPaddingNumber((int)(value/millisPerHour), 2, 2));
                zoneString.append(':');
                zoneString.append(
                    zeroPaddingNumber(
                        (int)((value%millisPerHour)/millisPerMinute), 2, 2));
                current = zoneString.toString();
            }
            else if (calendar.get(Calendar.DST_OFFSET) != 0)
            {
                if (count >= 4)
                    current = formatData.zoneStrings[zoneIndex][3];
                else
                    // count < 4, use abbreviated form if exists
                    current = formatData.zoneStrings[zoneIndex][4];
            }
            else
            {
                if (count >= 4)
                    current = formatData.zoneStrings[zoneIndex][1];
                else
                    current = formatData.zoneStrings[zoneIndex][2];
            }
            break;
        }

        if (pos.field == patternCharIndex) {
            // set for the first occurence only.
            if (pos.beginIndex == 0 && pos.endIndex == 0) {
                pos.beginIndex = beginOffset;
                pos.endIndex = beginOffset + current.length();
            }
        }

        return current;
    }


    // Pad the shorter numbers up to maxCount digits.
    private String zeroPaddingNumber(long value, int minDigits, int maxDigits)
    {
        numberFormat.setMinimumIntegerDigits(minDigits);
        numberFormat.setMaximumIntegerDigits(maxDigits);
        return numberFormat.format(value);
    }


    /**
     * Overrides DateFormat
     * @see java.util.DateFormat
     */
    public Date parse(String text, ParsePosition pos)
    {
        int start = pos.index;
        int oldStart = start;
        calendar.clear(); // Clears all the time fields

        boolean inQuote = false; // inQuote set true when hits 1st single quote
        char prevCh = 0;
        int count = 0;
        for (int i=0; i<pattern.length(); ++i)
        {
            char ch = pattern.charAt(i);
            if (inQuote)
            {
                if (ch == '\'')
                {
                    // ends with 2nd single quote
                    inQuote = false;
                    if (prevCh == '\'')
                        // two consecutive quotes
                        start++;
                }
                else
                {
                    // pattern uses text following from 1st single quote.
                    if (ch != text.charAt(start)) {
                        // Check for cases like: 'at' in pattern vs "xt"
                        // in time text, where 'a' doesn't match with 'x'.
                        // If fail to match, return null.
                        pos.index = oldStart; // left unchanged
                        return null;
                    }
                    start++;
                    prevCh = ch;
                }
            }
            else    // !inQuote
            {
                if (ch == '\'')
                {
                    inQuote = true;
                    if (count > 0) // handle cases like: e'at'
                    {
                        if ((start=subParse(text, start, prevCh, count))<0) {
                            pos.index = oldStart;
                            return null;
                        }
                        count = 0;
                    }
                    prevCh = ch;
                }
                else if (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z')
                {
                    // ch is a date-time pattern
                    if (ch != prevCh && count > 0) // e.g., yyyymmdd
                    {
                        if ((start=subParse(text, start, prevCh, count))<0) {
                            pos.index = oldStart;
                            return null;
                        }
                        prevCh = ch;
                        count = 1;
                    }
                    else
                    {
                        if (ch != prevCh)
                            prevCh = ch;
                        count++;
                    }
                }
                else if (count > 0)
                {
                    // handle cases like: MM-dd-yy, HH:mm:ss, or yyyy MM dd,
                    // where ch = '-', ':', or ' ', repectively.
                    if ((start=subParse(text, start, prevCh, count))<0) {
                        pos.index = oldStart;
                        return null;
                    }
                    if (ch != text.charAt(start)) {
                        // handle cases like: 'MMMM dd' in pattern vs. "janx20"
                        // in time text, where ' ' doesn't match with 'x'.
                        pos.index = oldStart;
                        return null;
                    }
                    start++;
                    count = 0;
                    prevCh = 0;
                }
                else // any other unquoted characters
                {
                    if (ch != text.charAt(start)) {
                        // handle cases like: 'MMMM   dd' in pattern vs.
                        // "jan,,,20" in time text, where "   " doesn't
                        // match with ",,,".
                        pos.index = oldStart;
                        return null;
                    }
                    start++;
                }
            }
        }
        // Parse the last item in the pattern
        if (count > 0)
        {
            if ((start=subParse(text, start, prevCh, count)) < 0) {
                pos.index = oldStart;
                return null;
            }
        }

        pos.index = start;


        // If any of yy MM dd hh mm ss SSS is missing, the "default day" --
        // Jan 1, 1970 -- and the "default time" -- 00:00:00.000 am --
        // will be used.
        //
        if (!calendar.isSet(Calendar.YEAR))
        {calendar.set(Calendar.YEAR, 1970);}
        if (!calendar.isSet(Calendar.MONTH))
        {calendar.set(Calendar.MONTH, 0);}  // January, 0-based
        if (!calendar.isSet(Calendar.DATE))
        {calendar.set(Calendar.DATE, 1);}
        if (!calendar.isSet(Calendar.HOUR_OF_DAY) &&
            !calendar.isSet(Calendar.HOUR))
        {calendar.set(Calendar.HOUR_OF_DAY, 0);}
        if (!calendar.isSet(Calendar.MINUTE))
        {calendar.set(Calendar.MINUTE, 0);}
        if (!calendar.isSet(Calendar.SECOND))
        {calendar.set(Calendar.SECOND, 0);}
        if (!calendar.isSet(Calendar.MILLISECOND))
        {calendar.set(Calendar.MILLISECOND, 0);}
 	if (!calendar.isSet(Calendar.ERA))
 	{calendar.set(Calendar.ERA,GregorianCalendar.AD);}

        return calendar.getTime();
    }

    /**
     * Private code-size reduction function used by subParse.
     * @param text the time text being parsed.
     * @param start where to start parsing.
     * @param field the date field being parsed.
     * @param data the string array to parsed.
     * @return the new start position if matching succeeded; a negative number
     * indicating matching failure, otherwise.
     */
    private int matchString(String text, int start, int field, String[] data)
    {
        int i = 0;
        int count = data.length;

        if (field == Calendar.DAY_OF_WEEK)
            i = 1;
        for (; i<count; i++)
            if (data[i].length() != 0 &&
                text.regionMatches(true, start, data[i], 0, data[i].length()))
                break;
        if (i < count)
            calendar.set(field, i);
        else return -start;
        return (start + data[i].length());
    }

    /**
     * Private member function that returns the year of the current century.
     * For instance, 1900 for 20th century.
     */
//    private int getCurrentCenturyYear()
//    {
//        Calendar cal = Calendar.getInstance();
//	      cal.setTime(new Date());
//        return ((cal.get(Calendar.YEAR)/100) * 100);
//    }

    /**
     * Private member function that converts the parsed date strings into
     * timeFields. Returns -start (for ParsePosition) if failed.
     * @param text the time text to be parsed.
     * @param start where to start parsing.
     * @param ch the pattern character for the date field text to be parsed.
     * @param count the count of a pattern character.
     * @return the new start position if matching succeeded; a negative number
     * indicating matching failure, otherwise.
     */
    private int subParse(String text, int start, char ch, int count)
    {
        int value = 0;
        int i;
        ParsePosition pos = new ParsePosition(0);
        int patternCharIndex = -1;

        if ((patternCharIndex=formatData.patternChars.indexOf(ch)) == -1)
            return -start;

//        numberFormat.setParseIntegerOnly(true);  // so that dd.mm.yy can be parsed.
        pos.index = start;
        switch (patternCharIndex)
        {
        case 0: // 'G' - ERA
            return matchString(text, start, Calendar.ERA, formatData.eras);
        case 1: // 'y' - YEAR
            if (count >= 4)
            {
                value = (int) Integer.valueOf(
                            text.substring(start, start+count)).longValue();
                calendar.set(Calendar.YEAR, value);
            }
            else // count < 4
            {
                value = (int) Integer.valueOf(
                            text.substring(start, start+2)).longValue();
//                calendar.set(Calendar.YEAR, value+getCurrentCenturyYear());
                calendar.set(Calendar.YEAR, value+1900);// hardcoded 1900-based
            }
            return (start + count);
        case 2: { // 'M' - MONTH
            if (count <= 2) // i.e., M or MM.
            {
                // Don't want to parse the month if it is a string
                // while pattern uses numeric style: M or MM.
                calendar.set(Calendar.MONTH,
                             numberFormat.parse(text, pos).intValue() - 1);
                return pos.index;
            }
            // count >= 3 // i.e., MMM or MMMM
            // Want to be able to parse both short and long forms.
            // Try count == 4 first:
            int newStart = 0;
            if ((newStart=matchString(text, start, Calendar.MONTH,
                formatData.months)) > 0)
                return newStart;
            else // count == 4 failed, now try count == 3
                return matchString(text, start, Calendar.MONTH,
                formatData.shortMonths);
            }
        case 3: // 'd' - DATE
            calendar.set(Calendar.DATE,
                         numberFormat.parse(text, pos).intValue());
            return pos.index;
        case 4: // 'k' - HOUR_OF_DAY: 1-based.  eg, 23:59 + 1 hour =>> 24:59
            value = numberFormat.parse(text, pos).intValue();
            if (value == calendar.getMaximum(Calendar.HOUR_OF_DAY)+1)
                calendar.set(Calendar.HOUR_OF_DAY, 0);
            else
                calendar.set(Calendar.HOUR_OF_DAY, value);
            return pos.index;
        case 5: // 'H' - HOUR_OF_DAY:0-based.  eg, 23:59 + 1 hour =>> 00:59
            calendar.set(Calendar.HOUR_OF_DAY,
                         numberFormat.parse(text, pos).intValue());
            return pos.index;
        case 6: // 'm' - MINUTE
            calendar.set(Calendar.MINUTE,
                         numberFormat.parse(text, pos).intValue());
            return pos.index;
        case 7: // 's' - SECOND
            calendar.set(Calendar.SECOND,
                         numberFormat.parse(text, pos).intValue());
            return pos.index;
        case 8: // 'S' - MILLISECOND
            // handle ss.SSS using
            // numberFormat.parse(text, pos).doubleValue()
            calendar.set(Calendar.MILLISECOND,
                         numberFormat.parse(text, pos).intValue());
            return pos.index;
        case 9: { // 'E' - DAY_OF_WEEK
            // Want to be able to parse both short and long forms.
            // Try count == 4 (DDDD) first:
            int newStart = 0;
            if ((newStart=matchString(text, start, Calendar.DAY_OF_WEEK,
                formatData.weekdays)) > 0)
                return newStart;
            else // DDDD failed, now try DDD
                return matchString(text, start, Calendar.DAY_OF_WEEK,
                formatData.shortWeekdays);
            }
        case 10:    // 'D' - DAY_OF_YEAR
            calendar.set(Calendar.DAY_OF_YEAR,
                         numberFormat.parse(text, pos).intValue());
            return pos.index;
        case 11:   // 'F' - DAY_OF_WEEK_IN_MONTH
            calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH,
                         numberFormat.parse(text, pos).intValue());
            return pos.index;
        case 12:    // 'w' - WEEK_OF_YEAR
            calendar.set(Calendar.WEEK_OF_YEAR,
                         numberFormat.parse(text, pos).intValue());
            return pos.index;
        case 13:    // 'W' - WEEK_OF_MONTH
            calendar.set(Calendar.WEEK_OF_MONTH,
                         numberFormat.parse(text, pos).intValue());
            return pos.index;
        case 14:    // 'a' - AM_PM
            return matchString(text, start, Calendar.AM_PM, formatData.ampms);
        case 15: // 'h' - HOUR:1-based.  eg, 11PM + 1 hour =>> 12 AM
            value = numberFormat.parse(text, pos).intValue();
            if (value == calendar.getLeastMaximum(Calendar.HOUR)+1)
                calendar.set(Calendar.HOUR, 0);
            else
                calendar.set(Calendar.HOUR, value);
            return pos.index;
        case 16: // 'K' - HOUR: 0-based.  eg, 11PM + 1 hour =>> 0 AM
            calendar.set(Calendar.HOUR,
                         numberFormat.parse(text, pos).intValue());
            return pos.index;
        case 17: // 'z' - ZONE_OFFSET
            // Want to be able to parse both short and long forms.
            for (i=0; i<formatData.zoneStrings.length; i++)
            {
                // Checking long and short zones [1 & 2],
                // and long and short daylight [3 & 4].
                int j = 1;
                for (; j <= 4; ++j)
                {
                    if (text.regionMatches(true, start,
                                       formatData.zoneStrings[i][j], 0,
                                       formatData.zoneStrings[i][j].length()))
                        break;
                }
                if (j <= 4)
                {
                    calendar.set(Calendar.ZONE_OFFSET,
                                 TimeZone.getTimeZone(
                                 formatData.zoneStrings[i][0]).getRawOffset());
                    if (j >= 3)
                        calendar.set(Calendar.DST_OFFSET, millisPerHour);
                    return (start + formatData.zoneStrings[i][j].length());
                }
            }
            // For time zones that have no known names, look for strings
            // of the form:
            //    GMT[+-]hours:minutes or
            //    GMT[+-]hhmm or
            //    GMT.
            if (text.regionMatches(true,start, GMT, 0, GMT.length()))
            {
                calendar.set(Calendar.DST_OFFSET, 0);

                pos.index = start + GMT.length();
                int offsetInMinutes = 0;
                int sign;
                
                if( text.regionMatches(pos.index, "+", 0, 1) ) 
                    sign = 1;
                else if( text.regionMatches(pos.index, "-", 0, 1) ) 
                    sign = -1;
                else {
                    calendar.set(Calendar.ZONE_OFFSET, 0 );
                    return pos.index;
                }

                // Look for hours:minutes or hhmm.
                pos.index++;
                Number tzNumber = numberFormat.parse(text, pos);
                if( tzNumber == null ) {
                    return -start;
                }
                if( text.regionMatches(pos.index, ":", 0, 1) ) {
                    // This is the hours:minutes case
                    offsetInMinutes = tzNumber.intValue() * 60;
                    pos.index++;
                    tzNumber = numberFormat.parse(text, pos);
                    if( tzNumber == null ) {
                        return -start;
                    }
                    offsetInMinutes += tzNumber.intValue();
                }
                else {
                    // This is the hhmm case.
                    int offset = tzNumber.intValue();
                    if( offset < 24 )
                        offsetInMinutes = offset * 60;
                    else
                        offsetInMinutes = offset % 100 + offset / 100 * 60;
                }

                calendar.set(Calendar.ZONE_OFFSET,
                             offsetInMinutes*millisPerMinute*sign);
                return pos.index;
            }
            else {
                // Look for numeric timezones of the form [+-]hhmm as
                // specified by RFC 822.  This code is actually a little
                // more permissive than RFC 822.  It will try to do its
                // best with numbers that aren't strictly 4 digits long.
                DecimalFormat fmt
                    = new DecimalFormat("+####;-####",
                                        new DecimalFormatSymbols(Locale.US) );
                Number tzNumber = fmt.parse( text, pos );
                if( tzNumber == null ) {
                    return -start;   // Wasn't actually a number.
                }
                int offset = tzNumber.intValue();
                int sign = 1;
                if( offset < 0 ) {
                    sign = -1;
                    offset *= -1;
                }
                if( offset < 24 )
                    offset = offset * 60;
                else
                    offset = offset % 100 + offset / 100 * 60;
                calendar.set(Calendar.ZONE_OFFSET, offset*millisPerMinute*sign);
                return pos.index;
            }
        }
        return -start;
    }


    /**
     * Translate a pattern, mapping each character in the from string to the
     * corresponding character in the to string.
     */
    private String translatePattern(String pattern, String from, String to) {
	StringBuffer result = new StringBuffer();
	boolean inQuote = false;
	for (int i = 0; i < pattern.length(); ++i) {
	    char c = pattern.charAt(i);
	    if (inQuote) {
		if (c == '\'')
		    inQuote = false;
	    }
	    else {
		if (c == '\'')
		    inQuote = true;
		else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
		    int ci = from.indexOf(c);
		    if (ci == -1)
			throw new IllegalArgumentException("Illegal pattern " +
							   " character '" +
							   c + "'");
		    c = to.charAt(ci);
		}
	    }
	    result.append(c);
	}
	if (inQuote)
	    throw new IllegalArgumentException("Unfinished quote in pattern");
	return result.toString();
    }

    /**
     * Return a pattern string describing this date format.
     */
    public String toPattern() {
	return pattern;
    }

    /**
     * Return a localized pattern string describing this date format.
     */
    public String toLocalizedPattern() {
	return translatePattern(pattern,
				formatData.patternChars,
				formatData.localPatternChars);
    }

    /**
     * Apply the given unlocalized pattern string to this date format.
     */
    public void applyPattern (String pattern)
    {
	this.pattern = pattern;
    }

    /**
     * Apply the given localized pattern string to this date format.
     */
    public void applyLocalizedPattern(String pattern) {
	this.pattern = translatePattern(pattern,
					formatData.localPatternChars,
					formatData.patternChars);
    }

    /**
     * Gets the date/time formatting data.
     * @return a copy of the date-time formatting data associated
     * with this date-time formatter.
     */
    public DateFormatSymbols getDateFormatSymbols()
    {
        return (DateFormatSymbols)formatData.clone();
    }

    /**
     * Allows you to set the date/time formatting data.
     * @param newFormatData the given date-time formatting data.
     */
    public void setDateFormatSymbols(DateFormatSymbols newFormatSymbols)
    {
        this.formatData = (DateFormatSymbols)newFormatSymbols.clone();
    }

    /**
     * Overrides Cloneable
     */
    public Object clone() {
    	SimpleDateFormat other = (SimpleDateFormat) super.clone();
    	other.formatData = (DateFormatSymbols) formatData.clone();
    	return other;
    }

    /**
     * Override hashCode.
     * Generates the hash code for the SimpleDateFormat object
     */
    public int hashCode()
    {
        return pattern.hashCode();
    	// just enough fields for a reasonable distribution
    }

    /**
     * Override equals.
     */
    public boolean equals(Object obj)
    {
    	if (!super.equals(obj)) return false; // super does class check
        SimpleDateFormat that = (SimpleDateFormat) obj;
        return (pattern.equals(that.pattern)
        		&& formatData.equals(that.formatData));
    }
}
