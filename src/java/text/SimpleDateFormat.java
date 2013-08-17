/*
 * @(#)SimpleDateFormat.java	1.32 99/03/15
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
import java.io.ObjectInputStream;
import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.util.Hashtable;

/**
 * <code>SimpleDateFormat</code> is a concrete class for formatting and
 * parsing dates in a locale-sensitive manner. It allows for formatting
 * (date -> text), parsing (text -> date), and normalization.
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
 * '        escape for text         (Delimiter)
 * ''       single quote            (Literal)           '
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
 * "hh 'o''clock' a, zzzz"           ->>  12 o'clock PM, Pacific Daylight Time
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
 * When parsing a date string using the abbreviated year pattern,
 * SimpleDateFormat must interpret the abbreviated year
 * relative to some century.  It does this by adjusting dates to be
 * within 80 years before and 20 years after the time the SimpleDateFormat
 * instance is created. For example, using a pattern of MM/dd/yy and a
 * SimpleDateFormat instance created on Jan 1, 1997,  the string
 * "01/11/12" would be interpreted as Jan 11, 2012 while the string "05/04/64"
 * would be interpreted as May 4, 1964.
 * During parsing, only strings consisting of exactly two digits, as defined by
 * <code>Character.isDigit(char)</code>, will be parsed into the default century.
 * Any other numeric string, such as a one digit string, a three or more digit
 * string, or a two digit string that isn't all digits (for example, "-1"), is
 * interpreted literally.  So "01/02/3" or "01/02/003" are parsed, using the
 * same pattern, as Jan 2, 3 AD.  Likewise, "01/02/-3" is parsed as Jan 2, 4 BC.
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
 * @version      1.32 03/15/99
 * @author       Mark Davis, Chen-Lieh Huang, Alan Liu
 */
public class SimpleDateFormat extends DateFormat {

    // the official serial version ID which says cryptically
    // which version we're compatible with
    static final long serialVersionUID = 4774881970558875024L;

    // the internal serial version which says which version was written
    // - 0 (default) for version up to JDK 1.1.3
    // - 1 for version from JDK 1.1.4, which includes a new field
    static final int currentSerialVersion = 1;
    private int serialVersionOnStream = currentSerialVersion;

    private String pattern;
    private DateFormatSymbols formatData;

    // if dates have ambiguous years, we map them into the century starting
    // at defaultCenturyStart, which may be any date.
    private Date defaultCenturyStart; // field new in JDK 1.1.4
    transient private int defaultCenturyStartYear;

    private static final int millisPerHour = 60 * 60 * 1000;
    private static final int millisPerMinute = 60 * 1000;

    // For time zones that have no names, use strings GMT+minutes and
    // GMT-minutes. For instance, in France the time zone is GMT+60.
    private static final String GMT_PLUS = "GMT+";
    private static final String GMT_MINUS = "GMT-";
    private static final String GMT = "GMT";

    /**
     * Cache to hold the DateTimePatterns of a Locale.
     */
    private static final Hashtable cachedLocaleData = new Hashtable(3);


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
        this(pattern, Locale.getDefault());
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
        this.formatData = (DateFormatSymbols) formatData.clone();
        initialize(Locale.getDefault());
    }

    /* Package-private, called by DateFormat factory methods */
    SimpleDateFormat(int timeStyle, int dateStyle, Locale loc) {
	/* try the cache first */
	String[] dateTimePatterns = (String[]) cachedLocaleData.get(loc);
	if (dateTimePatterns == null) { /* cache miss */
	    ResourceBundle r = ResourceBundle.getBundle
		("java.text.resources.LocaleElements", loc);
	    dateTimePatterns = r.getStringArray("DateTimePatterns");
	    /* update cache */
	    cachedLocaleData.put(loc, dateTimePatterns);
	}

        formatData = new DateFormatSymbols(loc);
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
        // The format object must be constructed using the symbols for this zone.
        // However, the calendar should use the current default TimeZone.
        // If this is not contained in the locale zone strings, then the zone
        // will be formatted using generic GMT+/-H:MM nomenclature.
        calendar = Calendar.getInstance(TimeZone.getDefault(), loc);
        numberFormat = NumberFormat.getInstance(loc);
        numberFormat.setGroupingUsed(false);
        if (numberFormat instanceof DecimalFormat)
            ((DecimalFormat)numberFormat).setDecimalSeparatorAlwaysShown(false);
        numberFormat.setParseIntegerOnly(true); /* So that dd.mm.yy can be parsed */
        numberFormat.setMinimumFractionDigits(0); // To prevent "Jan 1.00, 1997.00"

        initializeDefaultCentury();
    }

    /* Initialize the fields we use to disambiguate ambiguous years. Separate
     * so we can call it from readObject().
     */
    private void initializeDefaultCentury() {
        calendar.setTime( new Date() );
        calendar.add( Calendar.YEAR, -80 );
        parseAmbiguousDatesAsAfter(calendar.getTime());
    }

    /* Define one-century window into which to disambiguate dates using
     * two-digit years. Make public in JDK 1.2.
     */
    private void parseAmbiguousDatesAsAfter(Date startDate) {
        defaultCenturyStart = startDate;
        calendar.setTime(startDate);
        defaultCenturyStartYear = calendar.get(Calendar.YEAR);
    }

    /**
     * Overrides DateFormat
     * <p>Formats a date or time, which is the standard millis
     * since January 1, 1970, 00:00:00 GMT.
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
        int interQuoteCount = 1; // Number of characters between quotes
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
                        toAppendTo.append(ch);  // two consecutive quotes outside a quote: ''
                    else count = 0;
                    interQuoteCount = 0;
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

                    // We count characters between quotes so we can recognize
                    // two single quotes inside a quote.  Example: 'o''clock'.
                    if (interQuoteCount == 0)
                    {
                        toAppendTo.append(ch);
                        count = 1; // Make it look like we never left.
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

                ++interQuoteCount;
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

    // Map index into pattern character string to Calendar field number
    private static final int[] PATTERN_INDEX_TO_CALENDAR_FIELD =
    {
        Calendar.ERA, Calendar.YEAR, Calendar.MONTH, Calendar.DATE,
        Calendar.HOUR_OF_DAY, Calendar.HOUR_OF_DAY, Calendar.MINUTE,
        Calendar.SECOND, Calendar.MILLISECOND, Calendar.DAY_OF_WEEK,
        Calendar.DAY_OF_YEAR, Calendar.DAY_OF_WEEK_IN_MONTH,
        Calendar.WEEK_OF_YEAR, Calendar.WEEK_OF_MONTH,
        Calendar.AM_PM, Calendar.HOUR, Calendar.HOUR, Calendar.ZONE_OFFSET
    };

    // Map index into pattern character string to DateFormat field number
    private static final int[] PATTERN_INDEX_TO_DATE_FORMAT_FIELD = {
        DateFormat.ERA_FIELD, DateFormat.YEAR_FIELD, DateFormat.MONTH_FIELD,
        DateFormat.DATE_FIELD, DateFormat.HOUR_OF_DAY1_FIELD,
        DateFormat.HOUR_OF_DAY0_FIELD, DateFormat.MINUTE_FIELD,
        DateFormat.SECOND_FIELD, DateFormat.MILLISECOND_FIELD,
        DateFormat.DAY_OF_WEEK_FIELD, DateFormat.DAY_OF_YEAR_FIELD,
        DateFormat.DAY_OF_WEEK_IN_MONTH_FIELD, DateFormat.WEEK_OF_YEAR_FIELD,
        DateFormat.WEEK_OF_MONTH_FIELD, DateFormat.AM_PM_FIELD,
        DateFormat.HOUR1_FIELD, DateFormat.HOUR0_FIELD,
        DateFormat.TIMEZONE_FIELD,
    };

    // Private member function that does the real date/time formatting.
    private String subFormat(char ch, int count, int beginOffset,
                             FieldPosition pos)
         throws IllegalArgumentException
    {
        int     patternCharIndex = -1;
        int     maxIntCount = 10;
        String  current = "";

        if ((patternCharIndex=formatData.patternChars.indexOf(ch)) == -1)
            throw new IllegalArgumentException("Illegal pattern character " +
                                               "'" + ch + "'");

        int field = PATTERN_INDEX_TO_CALENDAR_FIELD[patternCharIndex];
        int value = calendar.get(field);

        switch (patternCharIndex) {
        case 0: // 'G' - ERA
            current = formatData.eras[value];
            break;
        case 1: // 'y' - YEAR
            if (count >= 4)
                //                current = zeroPaddingNumber(value, 4, count);
                current = zeroPaddingNumber(value, 4, maxIntCount);
            else // count < 4
                current = zeroPaddingNumber(value, 2, 2); // clip 1996 to 96
            break;
        case 2: // 'M' - MONTH
            if (count >= 4)
                current = formatData.months[value];
            else if (count == 3)
                current = formatData.shortMonths[value];
            else
                current = zeroPaddingNumber(value+1, count, maxIntCount);
            break;
        case 4: // 'k' - HOUR_OF_DAY: 1-based.  eg, 23:59 + 1 hour =>> 24:59
            if (value == 0)
                current = zeroPaddingNumber(
                                            calendar.getMaximum(Calendar.HOUR_OF_DAY)+1,
                                            count, maxIntCount);
            else
                current = zeroPaddingNumber(value, count, maxIntCount);
            break;
        case 8: // 'S' - MILLISECOND
            if (count > 3)
                count = 3;
            else if (count == 2)
                value = value / 10;
            else if (count == 1)
                value = value / 100;
            current = zeroPaddingNumber(value, count, maxIntCount);
            break;
        case 9: // 'E' - DAY_OF_WEEK
            if (count >= 4)
                current = formatData.weekdays[value];
            else // count < 4, use abbreviated form if exists
                current = formatData.shortWeekdays[value];
            break;
        case 14:    // 'a' - AM_PM
            current = formatData.ampms[value];
            break;
        case 15: // 'h' - HOUR:1-based.  eg, 11PM + 1 hour =>> 12 AM
            if (value == 0)
                current = zeroPaddingNumber(
                                            calendar.getLeastMaximum(Calendar.HOUR)+1,
                                            count, maxIntCount);
            else
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

                value = calendar.get(Calendar.ZONE_OFFSET) +
                    calendar.get(Calendar.DST_OFFSET);

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
        default:
            // case 3: // 'd' - DATE
            // case 5: // 'H' - HOUR_OF_DAY:0-based.  eg, 23:59 + 1 hour =>> 00:59
            // case 6: // 'm' - MINUTE
            // case 7: // 's' - SECOND
            // case 10: // 'D' - DAY_OF_YEAR
            // case 11: // 'F' - DAY_OF_WEEK_IN_MONTH
            // case 12: // 'w' - WEEK_OF_YEAR
            // case 13: // 'W' - WEEK_OF_MONTH
            // case 16: // 'K' - HOUR: 0-based.  eg, 11PM + 1 hour =>> 0 AM
            current = zeroPaddingNumber(value, count, maxIntCount);
            break;
        } // switch (patternCharIndex)

        if (pos.field == PATTERN_INDEX_TO_DATE_FORMAT_FIELD[patternCharIndex]) {
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
        boolean[] ambiguousYear = {false};

        calendar.clear(); // Clears all the time fields

        boolean inQuote = false; // inQuote set true when hits 1st single quote
        char prevCh = 0;
        int count = 0;
        int interQuoteCount = 1; // Number of chars between quotes

        for (int i=0; i<pattern.length(); ++i)
        {
            char ch = pattern.charAt(i);

            if (inQuote)
            {
                if (ch == '\'')
                {
                    // ends with 2nd single quote
                    inQuote = false;
                    // two consecutive quotes outside a quote means we have
                    // a quote literal we need to match.
                    if (count == 0)
                    {
                        if (ch != text.charAt(start))
                        {
                            pos.index = oldStart;
                            return null;
                        }
                        ++start;
                    }
                    count = 0;
                    interQuoteCount = 0;
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
                    ++count;
                    ++start;
                }
            }
            else    // !inQuote
            {
                if (ch == '\'')
                {
                    inQuote = true;
                    if (count > 0) // handle cases like: e'at'
                    {
                        start=subParse(text, start, prevCh, count,
                                       false, ambiguousYear);
                        if ( start<0 ) {
                            pos.index = oldStart;
                            return null;
                        }
                        count = 0;
                    }

                    if (interQuoteCount == 0)
                    {
                        // This indicates two consecutive quotes inside a quote,
                        // for example, 'o''clock'.  We need to parse this as
                        // representing a single quote within the quote.
                        if (ch != text.charAt(start))
                        {
                            pos.index = oldStart;
                            return null;
                        }
                        ++start;
                        count = 1; // Make it look like we never left
                    }
                }
                else if (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z')
                {
                    // ch is a date-time pattern
                    if (ch != prevCh && count > 0) // e.g., yyyymmdd
                    {
                        // This is the only case where we pass in 'true' for
                        // obeyCount.  That's because the next field directly
                        // abuts this one, so we have to use the count to know when
                        // to stop parsing. [LIU]
                        start = subParse(text, start, prevCh, count, true,
                                         ambiguousYear);
                        if (start < 0) {
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
                    start=subParse(text, start, prevCh, count,
                                   false, ambiguousYear);
                    if ( start < 0 ) {
                        pos.index = oldStart;
                        return null;
                    }
                    if (start >= text.length() || ch != text.charAt(start)) {
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

                ++interQuoteCount;
            }
        }
        // Parse the last item in the pattern
        if (count > 0)
        {
            start=subParse(text, start, prevCh, count,
                           false, ambiguousYear);
            if ( start < 0 ) {
                pos.index = oldStart;
                return null;
            }
        }

        // At this point the fields of Calendar have been set.  Calendar
        // will fill in default values for missing fields when the time
        // is computed.

        pos.index = start;

        // This part is a problem:  When we call parsedDate.after, we compute the time.
        // Take the date April 3 2004 at 2:30 am.  When this is first set up, the year
        // will be wrong if we're parsing a 2-digit year pattern.  It will be 1904.
        // April 3 1904 is a Sunday (unlike 2004) so it is the DST onset day.  2:30 am
        // is therefore an "impossible" time, since the time goes from 1:59 to 3:00 am
        // on that day.  It is therefore parsed out to fields as 3:30 am.  Then we
        // add 100 years, and get April 3 2004 at 3:30 am.  Note that April 3 2004 is
        // a Saturday, so it can have a 2:30 am -- and it should. [LIU]
        /*
        Date parsedDate = calendar.getTime();
        if( ambiguousYear[0] && !parsedDate.after(defaultCenturyStart) ) {
            calendar.add(Calendar.YEAR, 100);
            parsedDate = calendar.getTime();
        }
        */
        // Because of the above condition, save off the fields in case we need to readjust.
        // The procedure we use here is not particularly efficient, but there is no other
        // way to do this given the API restrictions present in Calendar.  We minimize
        // inefficiency by only performing this computation when it might apply, that is,
        // when the two-digit year is equal to the start year, and thus might fall at the
        // front or the back of the default century.  This only works because we adjust
        // the year correctly to start with in other cases -- see subParse().
        Date parsedDate;
        try {
            if (ambiguousYear[0]) // If this is true then the two-digit year == the default start year
            {
                // We need a copy of the fields, and we need to avoid triggering a call to
                // complete(), which will recalculate the fields.  Since we can't access
                // the fields[] array in Calendar, we clone the entire object.  This will
                // stop working if Calendar.clone() is ever rewritten to call complete().
                Calendar savedCalendar = (Calendar)calendar.clone();
                parsedDate = calendar.getTime();
                if (parsedDate.before(defaultCenturyStart))
                {
                    // We can't use add here because that does a complete() first.
                    savedCalendar.set(Calendar.YEAR, defaultCenturyStartYear + 100);
                    parsedDate = savedCalendar.getTime();
                }
            }
            else parsedDate = calendar.getTime();
        }
        // An IllegalArgumentException will be thrown by Calendar.getTime()
        // if any fields are out of range, e.g., MONTH == 17.
        catch (IllegalArgumentException e) {
            pos.index = oldStart;
            return null;
        }

        return parsedDate;
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

        if (field == Calendar.DAY_OF_WEEK) i = 1;

        // There may be multiple strings in the data[] array which begin with
        // the same prefix (e.g., Cerven and Cervenec (June and July) in Czech).
        // We keep track of the longest match, and return that.  Note that this
        // unfortunately requires us to test all array elements.
        int bestMatchLength = 0, bestMatch = -1;
        for (; i<count; ++i)
        {
            int length = data[i].length();
            // Always compare if we have no match yet; otherwise only compare
            // against potentially better matches (longer strings).
            if (length > bestMatchLength &&
                text.regionMatches(true, start, data[i], 0, length))
            {
                bestMatch = i;
                bestMatchLength = length;
            }
        }
        if (bestMatch >= 0)
        {
            calendar.set(field, bestMatch);
            return start + bestMatchLength;
        }
        return -start;
    }

    /**
     * Private member function that converts the parsed date strings into
     * timeFields. Returns -start (for ParsePosition) if failed.
     * @param text the time text to be parsed.
     * @param start where to start parsing.
     * @param ch the pattern character for the date field text to be parsed.
     * @param count the count of a pattern character.
     * @param obeyCount if true, then the next field directly abuts this one,
     * and we should use the count to know when to stop parsing.
     * @param ambiguousYear return parameter; upon return, if ambiguousYear[0]
     * is true, then a two-digit year was parsed and may need to be readjusted.
     * @return the new start position if matching succeeded; a negative number
     * indicating matching failure, otherwise.
     */
    private int subParse(String text, int start, char ch, int count,
                         boolean obeyCount, boolean[] ambiguousYear)
    {
        Number number;
        int value = 0;
        int i;
        ParsePosition pos = new ParsePosition(0);
        int patternCharIndex = -1;

        if ((patternCharIndex=formatData.patternChars.indexOf(ch)) == -1)
            return -start;

        pos.index = start;

        int field = PATTERN_INDEX_TO_CALENDAR_FIELD[patternCharIndex];

        // If there are any spaces here, skip over them.  If we hit the end
        // of the string, then fail.
        for (;;) {
            if (pos.index >= text.length()) return -start;
            char c = text.charAt(pos.index);
            if (c != ' ' && c != '\t') break;
            ++pos.index;
        }

        // We handle a few special cases here where we need to parse
        // a number value.  We handle further, more generic cases below.  We need
        // to handle some of them here because some fields require extra processing on
        // the parsed value.
        if (patternCharIndex == 4 /*HOUR_OF_DAY1_FIELD*/ ||
            patternCharIndex == 15 /*HOUR1_FIELD*/ ||
            (patternCharIndex == 2 /*MONTH_FIELD*/ && count <= 2) ||
            patternCharIndex == 1 /*YEAR*/)
        {
            // It would be good to unify this with the obeyCount logic below,
            // but that's going to be difficult.
            if (obeyCount)
            {
                if ((start+count) > text.length()) return -start;
                number = numberFormat.parse(text.substring(0, start+count), pos);
            }
            else number = numberFormat.parse(text, pos);
            if (number == null) return -start;
            value = number.intValue();
        }

        switch (patternCharIndex)
        {
        case 0: // 'G' - ERA
            return matchString(text, start, Calendar.ERA, formatData.eras);
        case 1: // 'y' - YEAR
            // If there are 3 or more YEAR pattern characters, this indicates
            // that the year value is to be treated literally, without any
            // two-digit year adjustments (e.g., from "01" to 2001).  Otherwise
            // we made adjustments to place the 2-digit year in the proper
            // century, for parsed strings from "00" to "99".  Any other string
            // is treated literally:  "2250", "-1", "1", "002".
            if (count <= 2 && (pos.index - start) == 2
                && Character.isDigit(text.charAt(start))
                && Character.isDigit(text.charAt(start+1)))
            {
                // Assume for example that the defaultCenturyStart is 6/18/1903.
                // This means that two-digit years will be forced into the range
                // 6/18/1903 to 6/17/2003.  As a result, years 00, 01, and 02
                // correspond to 2000, 2001, and 2002.  Years 04, 05, etc. correspond
                // to 1904, 1905, etc.  If the year is 03, then it is 2003 if the
                // other fields specify a date before 6/18, or 1903 if they specify a
                // date afterwards.  As a result, 03 is an ambiguous year.  All other
                // two-digit years are unambiguous.
                int ambiguousTwoDigitYear = defaultCenturyStartYear % 100;
                ambiguousYear[0] = value == ambiguousTwoDigitYear;
                value += (defaultCenturyStartYear/100)*100 +
                    (value < ambiguousTwoDigitYear ? 100 : 0);
            }
            calendar.set(Calendar.YEAR, value);
            return pos.index;
        case 2: // 'M' - MONTH
            if (count <= 2) // i.e., M or MM.
            {
                // Don't want to parse the month if it is a string
                // while pattern uses numeric style: M or MM.
                // [We computed 'value' above.]
                calendar.set(Calendar.MONTH, value - 1);
                return pos.index;
            }
            else
            {
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
        case 4: // 'k' - HOUR_OF_DAY: 1-based.  eg, 23:59 + 1 hour =>> 24:59
            // [We computed 'value' above.]
            if (value == calendar.getMaximum(Calendar.HOUR_OF_DAY)+1) value = 0;
            calendar.set(Calendar.HOUR_OF_DAY, value);
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
        case 14:    // 'a' - AM_PM
            return matchString(text, start, Calendar.AM_PM, formatData.ampms);
        case 15: // 'h' - HOUR:1-based.  eg, 11PM + 1 hour =>> 12 AM
            // [We computed 'value' above.]
            if (value == calendar.getLeastMaximum(Calendar.HOUR)+1) value = 0;
            calendar.set(Calendar.HOUR, value);
            return pos.index;
        case 17: // 'z' - ZONE_OFFSET
            // First try to parse generic forms such as GMT-07:00. Do this first
            // in case localized DateFormatZoneData contains the string "GMT"
            // for a zone; in that case, we don't want to match the first three
            // characters of GMT+/-HH:MM etc.
            {
                int sign = 0;
                int offset;

                // For time zones that have no known names, look for strings
                // of the form:
                //    GMT[+-]hours:minutes or
                //    GMT[+-]hhmm or
                //    GMT.
                if (text.regionMatches(true,start, GMT, 0, GMT.length()))
                {
                    calendar.set(Calendar.DST_OFFSET, 0);

                    pos.index = start + GMT.length();
                    
                    if (pos.index == text.length()) {
                          calendar.set(Calendar.ZONE_OFFSET, 0 );
                          return pos.index;
                    }
                    else if( text.charAt(pos.index) == '+' )
                        sign = 1; 
                    else if( text.charAt(pos.index) == '-' )
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
                    if( text.charAt(pos.index) == ':' ) {
                        // This is the hours:minutes case
                        offset = tzNumber.intValue() * 60;
                        pos.index++;
                        tzNumber = numberFormat.parse(text, pos);
                        if( tzNumber == null ) {
                            return -start;
                        }
                        offset += tzNumber.intValue();
                    }
                    else {
                        // This is the hhmm case.
                        offset = tzNumber.intValue();
                        if( offset < 24 )
                            offset *= 60;
                        else
                            offset = offset % 100 + offset / 100 * 60;
                    }

                    // Fall through for final processing below of 'offset' and 'sign'.
                }
                else {
                    // At this point, check for named time zones by looking through
                    // the locale data from the DateFormatZoneData strings.
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
                            TimeZone tz = TimeZone.getTimeZone(formatData.zoneStrings[i][0]);
                            calendar.set(Calendar.ZONE_OFFSET, tz.getRawOffset());
                            // Must call set() with something -- TODO -- Fix this to
                            // use the correct DST SAVINGS for the zone.
                            calendar.set(Calendar.DST_OFFSET, j >= 3 ? millisPerHour : 0);
                            return (start + formatData.zoneStrings[i][j].length());
                        }
                    }

                    // As a last resort, look for numeric timezones of the form
                    // [+-]hhmm as specified by RFC 822.  This code is actually
                    // a little more permissive than RFC 822.  It will try to do
                    // its best with numbers that aren't strictly 4 digits long.
                    DecimalFormat fmt = new DecimalFormat("+####;-####");
                    fmt.setParseIntegerOnly(true);
                    Number tzNumber = fmt.parse( text, pos );
                    if( tzNumber == null ) {
                        return -start;   // Wasn't actually a number.
                    }
                    offset = tzNumber.intValue();
                    sign = 1;
                    if( offset < 0 ) {
                        sign = -1;
                        offset = -offset;
                    }
                    if( offset < 24 )
                        offset = offset * 60;
                    else
                        offset = offset % 100 + offset / 100 * 60;

                    // Fall through for final processing below of 'offset' and 'sign'.
                }

                // Do the final processing for both of the above cases.  We only
                // arrive here if the form GMT+/-... or an RFC 822 form was seen.
                if (sign != 0)
                {
                    offset *= millisPerMinute * sign;

                    if (calendar.getTimeZone().useDaylightTime())
                    {
                        calendar.set(Calendar.DST_OFFSET, millisPerHour);
                        offset -= millisPerHour;
                    }
                    calendar.set(Calendar.ZONE_OFFSET, offset);

                    return pos.index;
                }
            }

            // All efforts to parse a zone failed.
            return -start;

        default:
            // case 3: // 'd' - DATE
            // case 5: // 'H' - HOUR_OF_DAY:0-based.  eg, 23:59 + 1 hour =>> 00:59
            // case 6: // 'm' - MINUTE
            // case 7: // 's' - SECOND
            // case 8: // 'S' - MILLISECOND
            // case 10: // 'D' - DAY_OF_YEAR
            // case 11: // 'F' - DAY_OF_WEEK_IN_MONTH
            // case 12: // 'w' - WEEK_OF_YEAR
            // case 13: // 'W' - WEEK_OF_MONTH
            // case 16: // 'K' - HOUR: 0-based.  eg, 11PM + 1 hour =>> 0 AM

            // Handle "generic" fields
            if (obeyCount)
            {
                if ((start+count) > text.length()) return -start;
                number = numberFormat.parse(text.substring(0, start+count), pos);
            }
            else number = numberFormat.parse(text, pos);
            if (number != null)
            {
                calendar.set(field, number.intValue());
                return pos.index;
            }
            return -start;
        }
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

    /**
     * Override readObject.
     */
    private void readObject(ObjectInputStream stream)
         throws IOException, ClassNotFoundException {
             stream.defaultReadObject();
             if (serialVersionOnStream < 1) {
                 // didn't have defaultCenturyStart field
                 initializeDefaultCentury();
             }
             else {
                 // fill in dependent transient field
                 parseAmbiguousDatesAsAfter(defaultCenturyStart);
             }
             serialVersionOnStream = currentSerialVersion;
    }
}
