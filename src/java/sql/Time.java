/*
 * @(#)Time.java	1.7 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.sql;

/**
 * <P>This class is a thin wrapper around java.util.Date that allows
 * JDBC to identify this as a SQL TIME value. It adds formatting and
 * parsing operations to support the JDBC escape syntax for time
 * values.
 */
public class Time extends java.util.Date {

    /**
     * Construct a Time Object
     *
     * @param hour 0 to 23
     * @param minute 0 to 59
     * @param second 0 to 59
     */
    public Time(int hour, int minute, int second) {
	super(70, 0, 1, hour, minute, second);
    }
   
    /**
     * Construct a Time using a milliseconds time value
     *
     * @param time milliseconds since January 1, 1970, 00:00:00 GMT
     */
    public Time(long time) {
	// If the millisecond time value contains date info, mask it out.
	super(time);
	int hours = getHours();
	int minutes = getMinutes();
	int seconds = getSeconds();
	super.setTime(0);
	setHours(hours);
	setMinutes(minutes);
	setSeconds(seconds);
    }

    /**
     * Set a Time using a milliseconds time value
     *
     * @param time milliseconds since January 1, 1970, 00:00:00 GMT
     */
    public void setTime(long time) {
	// If the millisecond time value contains date info, mask it out.
	super.setTime(time);
	int hours = getHours();
	int minutes = getMinutes();
	int seconds = getSeconds();
	super.setTime(0);
	setHours(hours);
	setMinutes(minutes);
	setSeconds(seconds);
    }

    /**
     * Convert a string in JDBC time escape format to a Time value
     *
     * @param s time in format "hh:mm:ss"
     * @return corresponding Time
     */
    public static Time valueOf(String s) {
	int hour;
	int minute;
	int second;
	int firstColon;
	int secondColon;

	if (s == null) throw new java.lang.IllegalArgumentException();

	firstColon = s.indexOf(':');
	secondColon = s.indexOf(':', firstColon+1);
	if ((firstColon > 0) & (secondColon > 0) & 
	    (secondColon < s.length()-1)) {
	    hour = Integer.parseInt(s.substring(0, firstColon));
	    minute = 
		Integer.parseInt(s.substring(firstColon+1, secondColon));
	    second = Integer.parseInt(s.substring(secondColon+1));	    
	} else {
	    throw new java.lang.IllegalArgumentException();
	}

	return new Time(hour, minute, second);
    }
   
    /**
     * Format a time in JDBC date escape format  
     *
     * @return a String in hh:mm:ss format
     */
    public String toString () {
	int hour = super.getHours();
	int minute = super.getMinutes();
	int second = super.getSeconds();
	String hourString;
	String minuteString;
	String secondString;

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
	return (hourString + ":" + minuteString + ":" + secondString);
    }

    // Override all the date operations inherited from java.util.Date;
    public int getYear() {
	throw new java.lang.IllegalArgumentException();
    }

    public int getMonth() {
	throw new java.lang.IllegalArgumentException();
    }
    
    public int getDay() {
	throw new java.lang.IllegalArgumentException();
    }

    public int getDate() {
	throw new java.lang.IllegalArgumentException();
    }

    public void setYear(int i) {
	throw new java.lang.IllegalArgumentException();
    }

    public void setMonth(int i) {
	throw new java.lang.IllegalArgumentException();
    }

    public void setDate(int i) {
	throw new java.lang.IllegalArgumentException();
    }
}
