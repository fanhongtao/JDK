/*
 * @(#)Date.java	1.41 97/03/03
 *
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * CopyrightVersion 1.1_beta
 *
 */

package java.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.SimpleTimeZone;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

/**
 * The class <code>Date</code> represents a specific instant
 * in time, with millisecond precision.
 * <p>
 * Prior to JDK&nbsp;1.1, the class <code>Date</code> had two additional
 * functions.  It allowed the interpretation of dates as year, month, day, hour,
 * minute, and second values.  It also allowed the formatting and parsing
 * of date strings.  Unfortunately, the API for these functions was not
 * amenable to internationalization.  As of JDK&nbsp;1.1, the
 * <code>Calendar</code> class should be used to convert between dates and time
 * fields and the <code>DateFormat</code> class should be used to format and
 * parse date strings.
 * The corresponding methods in <code>Date</code> are deprecated.
 * <p>
 * Although the <code>Date</code> class is intended to reflect 
 * coordinated universal time (UTC), it may not do so exactly, 
 * depending on the host environment of the Java Virtual Machine. 
 * Nearly all modern operating systems assume that 1&nbsp;day&nbsp;=
 * 24&nbsp;×&nbsp;60&nbsp;×&nbsp;60&nbsp;= 86400 seconds 
 * in all cases. In UTC, however, about once every year or two there 
 * is an extra second, called a "leap second." The leap 
 * second is always added as the last second of the day, and always 
 * on December 31 or June 30. For example, the last minute of the 
 * year 1995 was 61 seconds long, thanks to an added leap second. 
 * Most computer clocks are not accurate enough to be able to reflect 
 * the leap-second distinction. 
 * <p>
 * Some computer standards are defined in terms of Greenwich mean 
 * time (GMT), which is equivalent to universal time (UT).  GMT is 
 * the "civil" name for the standard; UT is the 
 * "scientific" name for the same standard. The 
 * distinction between UTC and UT is that UTC is based on an atomic 
 * clock and UT is based on astronomical observations, which for all 
 * practical purposes is an invisibly fine hair to split. Because the 
 * earth's rotation is not uniform (it slows down and speeds up 
 * in complicated ways), UT does not always flow uniformly. Leap 
 * seconds are introduced as needed into UTC so as to keep UTC within 
 * 0.9 seconds of UT1, which is a version of UT with certain 
 * corrections applied. There are other time and date systems as 
 * well; for example, the time scale used by the satellite-based 
 * global positioning system (GPS) is synchronized to UTC but is 
 * <i>not</i> adjusted for leap seconds. An interesting source of 
 * further information is the U.S. Naval Observatory, particularly 
 * the Directorate of Time at:
 * <ul><code>
 *     http://tycho.usno.navy.mil
 * </code></ul>
 * <p>
 * and their definitions of "Systems of Time" at:
 * <ul><code>http://tycho.usno.navy.mil/systime.html
 * </code></ul>
 * <p>
 * In all methods of class <code>Date</code> that accept or return 
 * year, month, date, hours, minutes, and seconds values, the 
 * following representations are used: 
 * <ul>
 * <li>A year <i>y</i> is represented by the integer 
 *     <i>y</i>&nbsp;<code>-&nbsp;1900</code>. 
 * <li>A month is represented by an integer form 0 to 11; 0 is January, 
 *     1 is February, and so forth; thus 11 is December. 
 * <li>A date (day of month) is represented by an integer from 1 to 31 
 *     in the usual manner. 
 * <li>An hour is represented by an integer from 0 to 23. Thus, the hour 
 *     from midnight to 1 a.m. is hour 0, and the hour from noon to 1 
 *     p.m. is hour 12. 
 * <li>A minute is represented by an integer from 0 to 59 in the usual manner.
 * <li>A second is represented by an integer from 0 to 60; the value 60 occurs
 *     only for leap seconds and even then only in Java implementations that
 *     actually track leap seconds correctly.
 * </ul>
 * <p>
 * In all cases, arguments given to methods for these purposes need 
 * not fall within the indicated ranges; for example, a date may be 
 * specified as January 32 and is interpreted as meaning February 1.
 *
 * @author  James Gosling
 * @author  Arthur van Hoff
 * @version 1.41, 03/03/97
 * @see     java.text.DateFormat
 * @see     java.util.Calendar
 * @see     java.util.TimeZone
 * @since   JDK1.0
 */
public
class Date implements java.io.Serializable, Cloneable {

    private transient Calendar cal;
    private transient long fastTime;
    private transient boolean modified;

    /* use serialVersionUID from modified java.util.Date for
     * interoperability with JDK1.1. The Date was modified to write
     * and read only the UTC time.
     */
    private static final long serialVersionUID = 7523967970034938905L;

    /**
     * Allocates a <code>Date</code> object and initializes it so that 
     * it represents the time at which it was allocated measured to the 
     * nearest millisecond. 
     *
     * @see     java.lang.System#currentTimeMillis()
     * @since   JDK1.0
     */
    public Date() {
	this(System.currentTimeMillis());
    }

    /**
     * Allocates a <code>Date</code> object and initializes it to 
     * represent the specified number of milliseconds since January 1, 
     * 1970, 00:00:00 GMT. 
     *
     * @param   date   the milliseconds since January 1, 1970, 00:00:00 GMT.
     * @see     java.lang.System#currentTimeMillis()
     * @since   JDK1.0
     */
    public Date(long date) {
        cal = null;
        fastTime = date;
        modified = false;

        //cal = new GregorianCalendar(TimeZone.getDefault());
        //cal.setTimeInMillis(date);
//	    cal.computeFields();  // done in get(field)
    }

    /**
     * Allocates a <code>Date</code> object and initializes it so that 
     * it represents midnight, local time, at the beginning of the day 
     * specified by the <code>year</code>, <code>month</code>, and 
     * <code>date</code> arguments. 
     *
     * @param   year    the year minus 1900.
     * @param   month   the month between 0-11.
     * @param   date    the day of the month between 1-31.
     * @see     java.util.Calendar
     * @since   JDK1.0
     * @deprecated
     */
    public Date(int year, int month, int date) {
	this(year, month, date, 0, 0, 0);
    }

    /**
     * Allocates a <code>Date</code> object and initializes it so that 
     * it represents the specified hour and minute, local time, of the 
     * date specified by the <code>year</code>, <code>month</code>,
     * <code>date</code>, <code>hrs</code>, and <code>min</code> arguments. 
     *
     * @param   year    the year minus 1900.
     * @param   month   the month between 0-11.
     * @param   date    the day of the month between 1-31.
     * @param   hrs     the hours between 0-23.
     * @param   min     the minutes between 0-59.
     * @see     java.util.Calendar
     * @since   JDK1.0
     * @deprecated
     */
    public Date(int year, int month, int date, int hrs, int min) {
	this(year, month, date, hrs, min, 0);
    }

    /**
     * Allocates a <code>Date</code> object and initializes it so that 
     * it represents the specified hour, minute, and second, local time 
     * of the date specified by the <code>year</code>, <code>month</code>,
     * <code>date</code>, <code>hrs</code>, <code>min</code>, and
     * <code>sec</code> arguments. 
     *
     * @param   year    the year minus 1900.
     * @param   month   the month between 0-11.
     * @param   date    the day of the month between 1-31.
     * @param   hrs     the hours between 0-23.
     * @param   min     the minutes between 0-59.
     * @param   sec     the seconds between 0-59.
     * @see     java.util.Calendar
     * @since   JDK1.0
     * @deprecated
     */
    public Date(int year, int month, int date, int hrs, int min, int sec) {
        cal = new GregorianCalendar(year + 1900, month, date, hrs,
                                    min, sec);
        cal.complete();    // to normalize field parameters
        modified = false;
    }

    /**
     * Allocates a <code>Date</code> object and initializes it so that 
     * it represents the date and time indicated by the string 
     * <code>s</code>, which is interpreted as if by the 
     * <code>parse</code> method. 
     *
     * @param   s   a string representation of the date.
     * @see     java.text.DateFormat
     * @see     java.util.Date#parse(java.lang.String)
     * @since   JDK1.0
     * @deprecated
     */
    public Date(String s) {
	this(parse(s));
    }

    /**
     * Determines the date and time based on the arguments. The 
     * arguments are interpreted in UTC, not in the local time zone
     *
     * @param   year    the year minus 1900.
     * @param   month   the month between 0-11.
     * @param   date    the day of the month between 1-31.
     * @param   hrs     the hours between 0-23.
     * @param   min     the minutes between 0-59.
     * @param   sec     the seconds between 0-59.
     * @return  the number of seconds since January 1, 1970, 00:00:00 GMT for
     *          the date and time specified by the arguments. 
     * @see     java.util.Calendar
     * @since   JDK1.0
     * @deprecated
     */
    public static long UTC(int year, int month, int date,
			        int hrs, int min, int sec) {

        Calendar cal =
          new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        cal.set(Calendar.YEAR, year + 1900);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DATE, date);
        cal.set(Calendar.HOUR_OF_DAY, hrs);
        cal.set(Calendar.MINUTE, min);
        cal.set(Calendar.SECOND, sec);

        return cal.getTimeInMillis();
    }

    /**
     * Given a string representing a time, parse it and return the time 
     * value. This method recognizes most standard syntaxes. 
     * <p>
     * It accepts many syntaxes; in particular, it recognizes the IETF 
     * standard date syntax: "Sat, 12 Aug 1995 13:30:00 GMT". It also 
     * understands the continental U.S. time-zone abbreviations, but for 
     * general use, a time-zone offset should be used: "Sat, 12 Aug 1995 
     * 13:30:00 GMT+0430" (4 hours, 30 minutes west of the Greenwich 
     * meridian). If no time zone is specified, the local time zone is 
     * assumed. GMT and UTC are considered equivalent. 
     *
     * @param   s   a string to be parsed as a date.
     * @return  the number of milliseconds since January 1, 1970, 00:00:00 GMT
     *          represented by the string argument.
     * @see     java.text.DateFormat
     * @since   JDK1.0
     * @deprecated
     */
    public static long parse(String s) {
	int year = -1;
	int mon = -1;
	int mday = -1;
	int hour = -1;
	int min = -1;
	int sec = -1;
	int millis = -1;
	int c = -1;
	int i = 0;
	int n = -1;
	int wst = -1;
	int tzoffset = -1;
	int prevc = 0;
syntax:
	{
	    if (s == null)
		break syntax;
	    int limit = s.length();
	    while (i < limit) {
		c = s.charAt(i);
		i++;
		if (c <= ' ' || c == ',')
		    continue;
		if (c == '(') {	// skip comments
		    int depth = 1;
		    while (i < limit) {
			c = s.charAt(i);
			i++;
			if (c == '(') depth++;
			else if (c == ')')
			    if (--depth <= 0)
			        break;
		    }
		    continue;
		}
		if ('0' <= c && c <= '9') {
		    n = c - '0';
		    while (i < limit && '0' <= (c = s.charAt(i)) && c <= '9') {
			n = n * 10 + c - '0';
			i++;
		    }
		    if (prevc == '+' || prevc == '-' && year>=0) {
			// timezone offset
			if (n < 24)
			    n = n * 60;	// EG. "GMT-3"
			else
			    n = n % 100 + n / 100 * 60;	// eg "GMT-0430"
			if (prevc == '+')	// plus means east of GMT
			    n = -n;
			if (tzoffset != 0 && tzoffset != -1)
			    break syntax;
			tzoffset = n;
		    } else if (n >= 70)
			if (year >= 0)
			    break syntax;
			else if (c <= ' ' || c == ',' || c == '/' || i >= limit)
			    year = n < 1900 ? n : n - 1900;
			else
			    break syntax;
		    else if (c == ':')
			if (hour < 0)
			    hour = (byte) n;
			else if (min < 0)
			    min = (byte) n;
			else
			    break syntax;
		    else if (c == '/')
			if (mon < 0)
			    mon = (byte) (n - 1);
			else if (mday < 0)
			    mday = (byte) n;
			else
			    break syntax;
		    else if (i < limit && c != ',' && c > ' ' && c != '-')
			break syntax;
		    else if (hour >= 0 && min < 0)
			min = (byte) n;
		    else if (min >= 0 && sec < 0)
			sec = (byte) n;
		    else if (mday < 0)
			mday = (byte) n;
		    else
			break syntax;
		    prevc = 0;
		} else if (c == '/' || c == ':' || c == '+' || c == '-')
		    prevc = c;
		else {
		    int st = i - 1;
		    while (i < limit) {
			c = s.charAt(i);
			if (!('A' <= c && c <= 'Z' || 'a' <= c && c <= 'z'))
			    break;
			i++;
		    }
		    if (i <= st + 1)
			break syntax;
		    int k;
		    for (k = wtb.length; --k >= 0;)
			if (wtb[k].regionMatches(true, 0, s, st, i - st)) {
			    int action = ttb[k];
			    if (action != 0) {
				if (action == 1) {	// pm
				    if (hour > 12 || hour < 1)
					break syntax;
				    else if (hour < 12)
                                        hour += 12;
                                } else if (action == 14) {  // am
				    if (hour > 12 || hour < 1)
					break syntax;
				    else if (hour == 12)
                                        hour = 0;
                                } else if (action <= 13) {	// month!
				    if (mon < 0)
					mon = (byte) (action - 2);
				    else
					break syntax;
                                } else {
				    tzoffset = action - 10000;
                                }
                            }
			    break;
			}
		    if (k < 0)
			break syntax;
		    prevc = 0;
		}
	    }
	    if (year < 0 || mon < 0 || mday < 0)
		break syntax;
	    if (sec < 0)
		sec = 0;
	    if (min < 0)
		min = 0;
	    if (hour < 0)
		hour = 0;
	    if (tzoffset == -1)	// no time zone specified, have to use local
		return new Date (year, mon, mday, hour, min, sec).getTime();
	    return UTC(year, mon, mday, hour, min, sec) + tzoffset * (60 * 1000);
	}
	// syntax error
	throw new IllegalArgumentException();
    }
    private final static String wtb[] = {
	"am", "pm",
	"monday", "tuesday", "wednesday", "thursday", "friday",
	"saturday", "sunday",
	"january", "february", "march", "april", "may", "june",
	"july", "august", "september", "october", "november", "december",
	"gmt", "ut", "utc", "est", "edt", "cst", "cdt",
	"mst", "mdt", "pst", "pdt"
	// this time zone table needs to be expanded
    };
    private final static int ttb[] = {
	14, 1, 0, 0, 0, 0, 0, 0, 0,
	2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
	10000 + 0, 10000 + 0, 10000 + 0,	// GMT/UT/UTC
	10000 + 5 * 60, 10000 + 4 * 60,	// EST/EDT
	10000 + 6 * 60, 10000 + 5 * 60,
	10000 + 7 * 60, 10000 + 6 * 60,
	10000 + 8 * 60, 10000 + 7 * 60
    };

    /**
     * Returns the year represented by this date, minus 1900.
     *
     * @return  the year represented by this date, minus 1900.
     * @see     java.util.Calendar
     * @since   JDK1.0
     * @deprecated
     */
    public int getYear() {
        checkCal();
        checkModified();
        return (cal.get(Calendar.YEAR) - 1900);
    }

    /**
     * Sets the year of this date to be the specified value plus 1900. 
     *
     * @param   year    the year value.
     * @see     java.util.Calendar
     * @since   JDK1.0
     * @deprecated
     */
    public void setYear(int year) {
        checkCal();
        cal.clear(Calendar.DST_OFFSET);
        cal.clear(Calendar.ZONE_OFFSET);
        cal.set(Calendar.YEAR, year + 1900);
        modified = true;
    }

    /**
     * Returns the month represented by this date. The value returned is
     * between <code>0</code> and <code>11</code>, with the value
     * <code>0</code> representing January.
     *
     * @return  the month represented by this date.
     * @see     java.util.Calendar
     * @since   JDK1.0
     * @deprecated
     */
    public int getMonth() {
        checkCal();
        checkModified();
        return cal.get(Calendar.MONTH);
    }

    /**
     * Sets the month of this date to the specified value. 
     *
     * @param   month   the month value between 0-11.
     * @see     java.util.Calendar
     * @since   JDK1.0
     * @deprecated
     */
    public void setMonth(int month) {
        checkCal();
        cal.clear(Calendar.DST_OFFSET);
        cal.clear(Calendar.ZONE_OFFSET);
        cal.set(Calendar.MONTH, month);
        modified = true;
    }

    /**
     * Returns the day of the month represented by this date. The value
     * returned is between <code>1</code> and <code>31</code>.
     *
     * @return  the day of the month represented by this date.
     * @see     java.util.Calendar
     * @since   JDK1.0
     * @deprecated
     */
    public int getDate() {
        checkCal();
        checkModified();
        return cal.get(Calendar.DATE);
    }

    /**
     * Sets the day of the month of this date to the specified value. 
     *
     * @param   date   the day of the month value between 1-31.
     * @see     java.util.Calendar
     * @since   JDK1.0
     * @deprecated
     */
    public void setDate(int date) {
        checkCal();
        cal.clear(Calendar.DST_OFFSET);
        cal.clear(Calendar.ZONE_OFFSET);
        cal.set(Calendar.DATE, date);
        modified = true;
    }

    /**
     * Returns the day of the week represented by this date. The value returned
     * is between <code>0</code> and <code>6</code>, where <code>0</code>
     * represents Sunday.
     *
     * @return  the day of the week represented by this date.
     * @see     java.util.Calendar
     * @since   JDK1.0
     * @deprecated
     */
    public int getDay() {
        checkCal();
        checkModified();
        return cal.get(Calendar.DAY_OF_WEEK) - 1; // SUNDAY is 1 in Calendar
    }

    /**
     * Returns the hour represented by this date. The value returned is between
     * <code>0</code> and <code>23</code>, where <code>0</code> represents
     * midnight.
     *
     * @return  the hour represented by this date.
     * @see     java.util.Calendar
     * @since   JDK1.0
     * @deprecated
     */
    public int getHours() {
        checkCal();
        checkModified();
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * Sets the hour of this date to the specified value. 
     *
     * @param   hours   the hour value.
     * @see     java.util.Calendar
     * @since   JDK1.0
     * @deprecated
     */
    public void setHours(int hours) {
        checkCal();
        cal.clear(Calendar.DST_OFFSET);
        cal.clear(Calendar.ZONE_OFFSET);
        cal.set(Calendar.HOUR_OF_DAY, hours);
        modified = true;
    }

    /**
     * Returns the number of minutes past the hour represented by this date.
     * The value returned is between <code>0</code> and <code>59</code>.
     *
     * @return  the number of minutes past the hour represented by this date.
     * @see     java.util.Calendar
     * @since   JDK1.0
     * @deprecated
     */
    public int getMinutes() {
        checkCal();
        checkModified();
        return cal.get(Calendar.MINUTE);
    }

    /**
     * Sets the minutes of this date to the specified value. 
     *
     * @param   minutes   the value of the minutes.
     * @see     java.util.Calendar
     * @since   JDK1.0
     * @deprecated
     */
    public void setMinutes(int minutes) {
        checkCal();
        cal.clear(Calendar.DST_OFFSET);
        cal.clear(Calendar.ZONE_OFFSET);
        cal.set(Calendar.MINUTE, minutes);
        modified = true;
    }

    /**
     * Returns the number of seconds past the minute represented by this date.
     * The value returned is between <code>0</code> and <code>60</code>. The
     * value <code>60</code> can only occur on those Java Virtual Machines that
     * take leap seconds into account.
     *
     * @return  the number of seconds past the minute represented by this date.
     * @see     java.util.Calendar
     * @since   JDK1.0
     * @deprecated
     */
    public int getSeconds() {
        checkCal();
        checkModified();
        return cal.get(Calendar.SECOND);
    }

    /**
     * Sets the seconds of this date to the specified value. 
     *
     * @param   seconds   the seconds value.
     * @see     java.util.Calendar
     * @since   JDK1.0
     * @deprecated
     */
    public void setSeconds(int seconds) {
        checkCal();
        cal.clear(Calendar.DST_OFFSET);
        cal.clear(Calendar.ZONE_OFFSET);
        cal.set(Calendar.SECOND, seconds);
        modified = true;
    }

    /**
     * Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT
     * represented by this date.
     *
     * @return  the number of milliseconds since January 1, 1970, 00:00:00 GMT
     *          represented by this date.
     * @since   JDK1.0
     */
    public long getTime() {
        if (cal == null) {
            return fastTime;
        }
        checkModified();
        return cal.getTimeInMillis();
    }

    /**
     * Sets this date to represent the specified number of milliseconds 
     * since January 1, 1970 00:00:00 GMT. 
     *
     * @param   time   the number of milliseconds.
     * @since   JDK1.0
     */
    public void setTime(long time) {
        if (cal == null) {
            fastTime = time;
            modified = false;
            return;
        }
        cal.setTimeInMillis(time);
        cal.computeFields();
        modified = false;
    }

    /**
     * Tests if this date is before the specified date.
     *
     * @param   when   a date.
     * @return  <code>true</code> if this date is before the argument date;
     *          <code>false</code> otherwise.
     * @since   JDK1.0
     */
    public boolean before(Date when) {
	return getTime() < when.getTime();
    }

    /**
     * Tests if this date is after the specified date.
     *
     * @param   when   a date.
     * @return  <code>true</code> if this date is after the argument date;
     *          <code>false</code> otherwise.
     * @since   JDK1.0
     */
    public boolean after(Date when) {
	return getTime() > when.getTime();
    }

    /**
     * Compares two dates.
     * The result is <code>true</code> if and only if the argument is 
     * not <code>null</code> and is a <code>Date</code> object that 
     * represents the same point in time, to the millisecond, as this object.
     * <p>
     * Thus, two <code>Date</code> objects are equal if and only if the 
     * <code>getTime</code> method returns the same <code>long</code> 
     * value for both. 
     *
     * @param   obj   the object to compare with.
     * @return  <code>true</code> if the objects are the same;
     *          <code>false</code> otherwise.
     * @see     java.util.Date#getTime()
     * @since   JDK1.0
     */
    public boolean equals(Object obj) {
	return obj != null && obj instanceof Date &&getTime() == ((Date) obj).getTime();
    }

    /**
     * Returns a hash code value for this object. 
     *
     * @return  a hash code value for this object. 
     * @since   JDK1.0
     */
    public int hashCode() {
	long ht = getTime();
	return (int) ht ^ (int) (ht >> 32);
    }

    /**
     * Creates a canonical string representation of the date. The result 
     * is of the form <code>"Sat Aug 12 02:30:00 PDT 1995"</code>.
     *
     * @return   a string representation of this date. 
     * @since   JDK1.0
     */
    public String toString() {
        DateFormat formatter
            = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(this);
    }

    /**
     * Creates a string representation of this date in an 
     * implementation-dependent form. The intent is that the form should 
     * be familiar to the user of the Java application, wherever it may 
     * happen to be running. The intent is comparable to that of the 
     * "<code>%c</code>" format supported by the <code>strftime()</code> 
     * function of ISO&nbsp;C. 
     *
     * @return  a string representation of this date, using the locale
     *          conventions.
     * @see     java.text.DateFormat
     * @since   JDK1.0
     * @deprecated
     */
    public String toLocaleString() {
        DateFormat formatter
            = DateFormat.getDateTimeInstance();
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(this);
    }

    /**
     * Creates a string representation of this date. The result is of the form:
     * <ul><code>
     *     "12 Aug 1995 02:30:00 GMT"
     * </code></ul>
     * <p>
     * in which the day of the month is always one or two digits. The 
     * other fields have exactly the width shown. The time zone is always 
     * given as "GMT".
     * 
     * @return  a string representation of this date, using the Internet GMT
     *          conventions.
     * @see     java.text.DateFormat
     * @since   JDK1.0
     * @deprecated
     */
    public String toGMTString() {
        DateFormat formatter
            = new SimpleDateFormat("d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter.format(this);
    }

    /**
     * Returns the local time-zone offset. The time-zone offset is 
     * the number of minutes that must be added to GMT to give the local 
     * time zone. This value includes the correction, if necessary, for 
     * daylight saving time. 
     *
     * @return  the time-zone offset, in minutes, for the current locale.
     * @see     java.util.Calendar
     * @see     java.util.TimeZone
     * @since   JDK1.0
     * @deprecated
     */
    public int getTimezoneOffset() {
        checkCal();
        checkModified();
        TimeZone tz = cal.getTimeZone();
        int offset = tz.getOffset(cal.get(Calendar.ERA),
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DATE),
                            cal.get(Calendar.DAY_OF_WEEK),
                            cal.get(Calendar.HOUR_OF_DAY) *
                            cal.get(Calendar.MINUTE) *
                            cal.get(Calendar.SECOND) * 1000);
        return -(offset / 1000 / 60);  // convert to minutes
    }

    /**
     * WriteObject is called to save the Date to a stream.
     * The UTC time is written to the stream as a long.
     */
    private void writeObject(ObjectOutputStream s)
        throws IOException
    {
	s.writeLong(getTime());
    }

    /**
     * readObject is called to restore a date from the stream.
     * The UTC time is read and the date set from it.
     */
    private void readObject(ObjectInputStream s)
         throws IOException, ClassNotFoundException
    {
	long date = s.readLong();
	cal = new GregorianCalendar(TimeZone.getDefault());
	cal.setTimeInMillis(date);
	cal.computeFields();
        modified = false;
    }

    /**
     * if Calendar has not been initialized, do it
     * Otherwise, do nothing
     */
    private void checkCal() {
        if (cal == null) {
            cal = new GregorianCalendar(TimeZone.getDefault());
            cal.setTimeInMillis(fastTime);
            cal.computeFields();
            modified = false;
        }
    }

    /**
     * check to see if the fields have been modified by user.
     * if so, re-calculate/normalize fields as necessary
     */
    private void checkModified() {
        if (modified) {
            cal.complete();
            modified = false;
        }
    }
}
