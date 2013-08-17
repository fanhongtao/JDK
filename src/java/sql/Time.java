/*
 * @(#)Time.java	1.17 98/09/27
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
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
 * <P>A thin wrapper around <code>java.util.Date</code> that allows
 * JDBC to identify this as a SQL TIME value. The <code>Time</code>
 * class adds formatting and
 * parsing operations to support the JDBC escape syntax for time
 * values. 
 * <p>The date components should be set to the "zero epoch"
 * value of January 1, 1970 and should not be accessed. 
 */
public class Time extends java.util.Date {

    /**
     * Constructs a <code>Time</code> object initialized with the 
	 * given values for the hour, minute, and second.
	 * The driver sets the date components to January 1, 1970.
	 * Any method that attempts to access the date components of a
     * <code>Time</code> object will throw a
	 * <code>java.lang.IllegalArgumentException</code>.
	 *
     * @param hour 0 to 23
     * @param minute 0 to 59
     * @param second 0 to 59
     */
    public Time(int hour, int minute, int second) {
	super(70, 0, 1, hour, minute, second);
    }
   
    /**
     * Constructs a <code>Time</code> object using a milliseconds time value.
     *
     * @param time milliseconds since January 1, 1970, 00:00:00 GMT;
	 *             a negative number is milliseconds before
	 *               January 1, 1970, 00:00:00 GMT
     */
    public Time(long time) {
	super(time);
    }

    /**
     * Sets a <code>Time</code> object using a milliseconds time value.
     *
     * @param time milliseconds since January 1, 1970, 00:00:00 GMT;
	 *             a negative number is milliseconds before
	 *               January 1, 1970, 00:00:00 GMT
     */
    public void setTime(long time) {
	super.setTime(time);
    }

    /**
     * Converts a string in JDBC time escape format to a <code>Time</code> value.
     *
     * @param s time in format "hh:mm:ss"
     * @return a corresponding <code>Time</code> object
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

   /**
    * This method is deprecated and should not be used because SQL Time 
    * values do not have a year component.
    *
    * @deprecated
    * @exception <code>java.lang.IllegalArgumentException</code> if this
    */
    public int getYear() {
	throw new java.lang.IllegalArgumentException();
    }

   /**
    * This method is deprecated and should not be used because SQL Time 
    * values do not have a month component.
    *
    * @deprecated
	* @exception <code>java.lang.IllegalArgumentException</code> if this
    */
    public int getMonth() {
	throw new java.lang.IllegalArgumentException();
    }
    
   /**
    * This method is deprecated and should not be used because SQL Time 
    * values do not have a day component.
    *
    * @deprecated
	* @exception <code>java.lang.IllegalArgumentException</code> if this
    */
    public int getDay() {
	throw new java.lang.IllegalArgumentException();
    }

   /**
    * This method is deprecated and should not be used because SQL Time 
    * values do not have a date component.
    *
    * @deprecated
	* @exception <code>java.lang.IllegalArgumentException</code> if this
    */
    public int getDate() {
	throw new java.lang.IllegalArgumentException();
    }

   /**
    * This method is deprecated and should not be used because SQL Time 
    * values do not have a year component.
    *
    * @deprecated
	* @exception <code>java.lang.IllegalArgumentException</code> if this
    */
    public void setYear(int i) {
	throw new java.lang.IllegalArgumentException();
    }

   /**
    * This method is deprecated and should not be used because SQL Time 
    * values do not have a month component.
    *
    * @deprecated
	* @exception <code>java.lang.IllegalArgumentException</code> if this
    */
    public void setMonth(int i) {
	throw new java.lang.IllegalArgumentException();
    }

   /**
    * This method is deprecated and should not be used because SQL Time 
    * values do not have a date component.
    *
    * @deprecated
	* @exception <code>java.lang.IllegalArgumentException</code> if this
    */
    public void setDate(int i) {
	throw new java.lang.IllegalArgumentException();
    }
}



