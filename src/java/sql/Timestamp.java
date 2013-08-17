/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.sql;

/**
 * <P>A thin wrapper around <code>java.util.Date</code> that allows
 * the JDBC API to identify this as an SQL <code>TIMESTAMP</code> value.
 * It adds the ability
 * to hold the SQL <code>TIMESTAMP</code> nanos value and provides formatting and
 * parsing operations to support the JDBC escape syntax for timestamp values.
 *
 * <P><B>Note:</B> This type is a composite of a <code>java.util.Date</code> and a
 * separate nanoseconds value. Only integral seconds are stored in the
 * <code>java.util.Date</code> component. The fractional seconds - the nanos - are
 * separate. The <code>getTime</code> method will return only integral seconds. If
 * a time value that includes the fractional seconds is desired, you
 * must convert nanos to milliseconds (nanos/1000000) and add this to
 * the <code>getTime</code> value.  The
 * <code>Timestamp.equals(Object)</code> method never returns 
 * <code>true</code> when passed a value of type <code>java.util.Date</code>
 * because the nanos component of a date is unknown.
 * As a result, the <code>Timestamp.equals(Object)</code>
 * method is not symmetric with respect to the
 * <code>java.util.Date.equals(Object)</code>
 * method.  Also, the <code>hashcode</code> method uses the underlying 
 * <code>java.util.Data</code> 
 * implementation and therefore does not include nanos in its computation.  
 * 
 * Due to the differences between the <code>Timestamp</code> class
 * and the <code>java.util.Date</code>
 * class mentioned above, it is recommended that code not view
 * <code>Timestamp</code> values generically as an instance of
 * <code>java.util.Date</code>.  The
 * inheritance relationship between <code>Timestamp</code>
 * and <code>java.util.Date</code> really 
 * denotes implementation inheritance, and not type inheritance.  
 */
public class Timestamp extends java.util.Date {

    /**
     * Constructs a <code>Timestamp</code> object initialized
	 * with the given values.
     *
     * @param year year-1900
     * @param month 0 to 11 
     * @param date 1 to 31
     * @param hour 0 to 23
     * @param minute 0 to 59
     * @param second 0 to 59
     * @param nano 0 to 999,999,999
     * @deprecated instead use the constructor <code>Timestamp(long millis)</code>
     */
    public Timestamp(int year, int month, int date, 
		     int hour, int minute, int second, int nano) {
	super(year, month, date, hour, minute, second);
	if (nano > 999999999 || nano < 0) {
	    throw new IllegalArgumentException("nanos > 999999999 or < 0");
	}
	nanos = nano;
    }

    /**
     * Constructs a <code>Timestamp</code> object 
     * using a milliseconds time value. The
     * integral seconds are stored in the underlying date value; the
     * fractional seconds are stored in the <code>nanos</code> field of
	 * the <code>Timestamp</code> object.
     *
     * @param time milliseconds since January 1, 1970, 00:00:00 GMT.
	 *        A negative number is the number of milliseconds before 
	 *         January 1, 1970, 00:00:00 GMT.
     */
    public Timestamp(long time) {
	super((time/1000)*1000);
	nanos = (int)((time%1000) * 1000000);
	if (nanos < 0) {
	    nanos = 1000000000 + nanos;	    
	    setTime(((time/1000)-1)*1000);
	}
    }

	/**
	 * @serial
	 */
    private int nanos;

    /**
     * Converts a <code>String</code> object in JDBC timestamp escape format to a
	 * <code>Timestamp</code> value.
     *
     * @param s timestamp in format <code>yyyy-mm-dd hh:mm:ss.fffffffff</code>
     * @return corresponding <code>Timestamp</code> value
	 * @exception java.lang.IllegalArgumentException if the given argument
	 * does not have the format <code>yyyy-mm-dd hh:mm:ss.fffffffff</code>
     */
    public static Timestamp valueOf(String s) {
	String date_s;
	String time_s;
	String nanos_s;
	int year;
	int month;
	int day;
	int hour;
	int minute;
	int second;
	int a_nanos = 0;
	int firstDash;
	int secondDash;
	int dividingSpace;
	int firstColon = 0;
	int secondColon = 0;
	int period = 0;
	String formatError = "Timestamp format must be yyyy-mm-dd hh:mm:ss.fffffffff";
	String zeros = "000000000";

	if (s == null) throw new java.lang.IllegalArgumentException("null string");

	// Split the string into date and time components
	s = s.trim();
	dividingSpace = s.indexOf(' ');
	if (dividingSpace > 0) {
	    date_s = s.substring(0,dividingSpace);
	    time_s = s.substring(dividingSpace+1);
	} else {
	    throw new java.lang.IllegalArgumentException(formatError);
	}


	// Parse the date
	firstDash = date_s.indexOf('-');
	secondDash = date_s.indexOf('-', firstDash+1);

	// Parse the time
	if (time_s == null) 
	    throw new java.lang.IllegalArgumentException(formatError);
	firstColon = time_s.indexOf(':');
	secondColon = time_s.indexOf(':', firstColon+1);
	period = time_s.indexOf('.', secondColon+1);

	// Convert the date
	if ((firstDash > 0) & (secondDash > 0) & 
	    (secondDash < date_s.length()-1)) {
	    year = Integer.parseInt(date_s.substring(0, firstDash)) - 1900;
	    month = 
		Integer.parseInt(date_s.substring
				 (firstDash+1, secondDash)) - 1;
	    day = Integer.parseInt(date_s.substring(secondDash+1));
	} else {		
	    throw new java.lang.IllegalArgumentException(formatError);
	}

	// Convert the time; default missing nanos
	if ((firstColon > 0) & (secondColon > 0) & 
	    (secondColon < time_s.length()-1)) {
	    hour = Integer.parseInt(time_s.substring(0, firstColon));
	    minute = 
		Integer.parseInt(time_s.substring(firstColon+1, secondColon));
	    if ((period > 0) & (period < time_s.length()-1)) {
		second = 
		    Integer.parseInt(time_s.substring(secondColon+1, period));
		nanos_s = time_s.substring(period+1);
		if (nanos_s.length() > 9) 
		    throw new java.lang.IllegalArgumentException(formatError);
		if (!Character.isDigit(nanos_s.charAt(0)))
		    throw new java.lang.IllegalArgumentException(formatError);
		nanos_s = nanos_s + zeros.substring(0,9-nanos_s.length());
		a_nanos = Integer.parseInt(nanos_s);
	    } else if (period > 0) {
		throw new java.lang.IllegalArgumentException(formatError);
	    } else {
		second = Integer.parseInt(time_s.substring(secondColon+1));
	    }
	} else {
	    throw new java.lang.IllegalArgumentException();
	}

	return new Timestamp(year, month, day, hour, minute, second, a_nanos);
    }

    /**
     * Formats a timestamp in JDBC timestamp escape format.
     *
     * @return a <code>String</code> object in
	 *           <code>yyyy-mm-dd hh:mm:ss.fffffffff</code> format
	 * @overrides <code>toString</code> in class <code>java.util.Date</code>
     */
    public String toString () {
	int year = super.getYear() + 1900;
	int month = super.getMonth() + 1;
	int day = super.getDate();
	int hour = super.getHours();
	int minute = super.getMinutes();
	int second = super.getSeconds();
	String yearString;
	String monthString;
	String dayString;
	String hourString;
	String minuteString;
	String secondString;
	String nanosString;
	String zeros = "000000000";

		
	yearString = "" + year;
	if (month < 10) {
	    monthString = "0" + month;
	} else {		
	    monthString = Integer.toString(month);
	}
	if (day < 10) {
	    dayString = "0" + day;
	} else {		
	    dayString = Integer.toString(day);
	}
	if (hour < 10) {
	    hourString = "0" + hour;
	} else {		
	    hourString = Integer.toString(hour);
	}
	if (minute < 10) {
	    minuteString = "0" + minute;
	} else {		
	    minuteString = Integer.toString(minute);
	}
	if (second < 10) {
	    secondString = "0" + second;
	} else {		
	    secondString = Integer.toString(second);
	}
	if (nanos == 0) {
	    nanosString = "0";
	} else {
	    nanosString = Integer.toString(nanos);

	    // Add leading zeros
	    nanosString = zeros.substring(0,(9-nanosString.length())) + 
		nanosString;
	    
	    // Truncate trailing zeros
	    char[] nanosChar = new char[nanosString.length()];
	    nanosString.getChars(0, nanosString.length(), nanosChar, 0);
	    int truncIndex = 8;	    
	    while (nanosChar[truncIndex] == '0') {
		truncIndex--;
	    }
	    nanosString = new String(nanosChar,0,truncIndex+1);
	}
	
	return (yearString + "-" + monthString + "-" + dayString + " " + 
		hourString + ":" + minuteString + ":" + secondString + "."
                + nanosString);
    }

    /**
     * Gets this <code>Timestamp</code> object's <code>nanos</code> value.
     *
     * @return this <code>Timestamp</code> object's fractional seconds component
     */
    public int getNanos() {
	return nanos;
    }

    /**
     * Sets this <code>Timestamp</code> object's <code>nanos</code> field
	 * to the given value.
     *
     * @param n the new fractional seconds component
	 * @exception java.lang.IllegalArgumentException if the given argument
	 *            is greater than 999999999 or less than 0
     */
    public void setNanos(int n) {
	if (n > 999999999 || n < 0) {
	    throw new IllegalArgumentException("nanos > 999999999 or < 0");
	}
	nanos = n;
    }

    /**
     * Tests to see if this <code>Timestamp</code> object is
	 * equal to the given <code>Timestamp</code> object.
     *
     * @param ts the <code>Timestamp</code> value to compare with
     */
    public boolean equals(Timestamp ts) {
	if (super.equals(ts)) {
	    if  (nanos == ts.nanos) {
		return true;
	    } else {
		return false;
	    }
	} else {
	    return false;
	}
    }

    /**
     * Tests to see if this <code>Timestamp</code> object is
	 * equal to the given object.
     *
     * This version of the method <code>equals</code> has been added
	 * to fix the incorrect 
     * signature of <code>Timestamp.equals(Timestamp)</code> and to preserve backward 
     * compatibility with existing class files.
     *
     * Note: This method is not symmetric with respect to the 
     * <code>equals(Object)</code> method in the base class.
     *
     * @param ts the <code>Object</code> value to compare with
     */
    public boolean equals(java.lang.Object ts) {
      if (ts instanceof Timestamp) {
	return this.equals((Timestamp)ts);
      } else {
	return false;
      }
    }

    /**
     * Indicates whether this <code>Timestamp</code> object is
	 * earlier than the given <code>Timestamp</code> object.
     *
     * @param ts the <code>Timestamp</code> value to compare with
	 * @return <code>true</code> if this <code>Timestamp</code> object is earlier;
	 *        <code>false</code> otherwise
     */
    public boolean before(Timestamp ts) {
	if (super.before(ts)) {
	    return true;
	} else {
	    if (super.equals(ts)) {
		if (nanos < ts.nanos) {
		    return true;
		} else {
		    return false;
		}
	    } else {
		return false;
	    }
	}
    }

    /**
     * Indicates whether this <code>Timestamp</code> object is
	 * later than the given <code>Timestamp</code> object.
     *
     * @param ts the <code>Timestamp</code> value to compare with
	 * @return <code>true</code> if this <code>Timestamp</code> object is later;
	 *        <code>false</code> otherwise
     */
    public boolean after(Timestamp ts) {
	if (super.after(ts)) {
	    return true;
	} else {
	    if (super.equals(ts)) {
		if (nanos > ts.nanos) {
		    return true;
		} else {
		    return false;
		}
	    } else {
		return false;
	    }
	}
    }

    static final long serialVersionUID = 2745179027874758501L;

}

