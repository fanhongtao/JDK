/*
 * @(#)Date.java	1.6 98/07/01
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
 * JDBC to identify this as a SQL DATE value. It adds formatting and
 * parsing operations to support the JDBC escape syntax for date
 * values.
 */
public class Date extends java.util.Date {

    /**
     * Construct a Date  
     *
     * @param year year-1900
     * @param month 0 to 11 
     * @param day 1 to 31
     */
    public Date(int year, int month, int day) {
	super(year, month, day);
    }

    /**
     * Construct a Date using a milliseconds time value
     *
     * @param date milliseconds since January 1, 1970, 00:00:00 GMT
     */
    public Date(long date) {
	// If the millisecond date value contains time info, mask it out.
	super(date);
	int year = getYear();
	int month = getMonth();
	int day = getDate();
	super.setTime(0);
	setYear(year);
	setMonth(month);
	setDate(day);
    }

    /**
     * Set a Date using a milliseconds time value
     *
     * @param date milliseconds since January 1, 1970, 00:00:00 GMT
     */
    public void setTime(long date) {
	// If the millisecond date value contains time info, mask it out.
	super.setTime(date);
	int year = getYear();
	int month = getMonth();
	int day = getDate();
	super.setTime(0);
	setYear(year);
	setMonth(month);
	setDate(day);
    }

    /**
     * Convert a string in JDBC date escape format to a Date value
     *
     * @param s date in format "yyyy-mm-dd"
     * @return corresponding Date
     */
    public static Date valueOf(String s) {
	int year;
	int month;
	int day;
	int firstDash;
	int secondDash;

	if (s == null) throw new java.lang.IllegalArgumentException();

	firstDash = s.indexOf('-');
	secondDash = s.indexOf('-', firstDash+1);
	if ((firstDash > 0) & (secondDash > 0) & (secondDash < s.length()-1)) {
	    year = Integer.parseInt(s.substring(0, firstDash)) - 1900;
	    month = Integer.parseInt(s.substring(firstDash+1, secondDash)) - 1;
	    day = Integer.parseInt(s.substring(secondDash+1));	 
	} else {
	    throw new java.lang.IllegalArgumentException();
	}
			
	return new Date(year, month, day);
    }

    /**
     * Format a date in JDBC date escape format  
     *
     * @return a String in yyyy-mm-dd format
     */
    public String toString () {
	int year = super.getYear() + 1900;
	int month = super.getMonth() + 1;
	int day = super.getDate();
	String yearString;
	String monthString;
	String dayString;

		
	yearString = Integer.toString(year);

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

	return ( yearString + "-" + monthString + "-" + dayString);
    }

    // Override all the time operations inherited from java.util.Date;
    public int getHours() {
	throw new java.lang.IllegalArgumentException();
    }

    public int getMinutes() {
	throw new java.lang.IllegalArgumentException();
    }
    
    public int getSeconds() {
	throw new java.lang.IllegalArgumentException();
    }

    public void setHours(int i) {
	throw new java.lang.IllegalArgumentException();
    }

    public void setMinutes(int i) {
	throw new java.lang.IllegalArgumentException();
    }

    public void setSeconds(int i) {
	throw new java.lang.IllegalArgumentException();
    }

}

