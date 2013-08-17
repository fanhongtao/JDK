/*
 * @(#)Short.java	1.8 98/07/01
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

package java.lang;

/**
 * The Short class is the standard wrapper for short values.
 *
 * @author  Nakul Saraiya
 * @version 1.8, 07/01/98
 * @see     java.lang.Number
 * @since   JDK1.1
 */
public final
class Short extends Number {
    /**
     * The minimum value a Short can have.
     *
     * @since   JDK1.1
     */
    public static final short   MIN_VALUE = -32768;

    /**
     * The maximum value a Short can have.
     *
     * @since   JDK1.1
     */
    public static final short   MAX_VALUE = 32767;

    /**
     * The Class object representing the primitive type short.
     *
     * @since   JDK1.1
     */
    public static final Class	TYPE = Class.getPrimitiveClass("short");

    /**
     * Returns a new String object representing the specified
     * Short. The radix is assumed to be 10.
     *
     * @param s the short to be converted
     * @since   JDK1.1
     */
    public static String toString(short s) {
	return Integer.toString((int)s, 10);
    }

    /**
     * Assuming the specified String represents a short, returns
     * that short's value. Throws an exception if the String cannot
     * be parsed as a short.  The radix is assumed to be 10.
     *
     * @param s		the String containing the short
     * @exception	NumberFormatException If the string does not
     *			contain a parsable short.
     * @since   JDK1.1
     */
    public static short parseShort(String s) throws NumberFormatException {
	return parseShort(s, 10);
    }

    /**
     * Assuming the specified String represents a short, returns
     * that short's value. Throws an exception if the String cannot
     * be parsed as a short.
     *
     * @param s		the String containing the short
     * @param radix	the radix to be used
     * @exception	NumberFormatException If the String does not
     *			contain a parsable short.
     * @since   JDK1.1
     */
    public static short parseShort(String s, int radix)
	throws NumberFormatException {
	int i = Integer.parseInt(s, radix);
	if (i < MIN_VALUE || i > MAX_VALUE)
	    throw new NumberFormatException();
	return (short)i;
    }

    /**
     * Assuming the specified String represents a short, returns a
     * new Short object initialized to that value.  Throws an
     * exception if the String cannot be parsed as a short.
     *
     * @param s		the String containing the integer
     * @param radix 	the radix to be used
     * @exception	NumberFormatException If the String does not
     *			contain a parsable short.
     * @since   JDK1.1
     */
    public static Short valueOf(String s, int radix)
	throws NumberFormatException {
	return new Short(parseShort(s, radix));
    }

    /**
     * Assuming the specified String represents a short, returns a
     * new Short object initialized to that value.  Throws an
     * exception if the String cannot be parsed as a short.
     *
     * @param s		the String containing the integer
     * @exception	NumberFormatException If the String does not
     *			contain a parsable short.
     * @since   JDK1.1
     */
    public static Short valueOf(String s) throws NumberFormatException {
	return valueOf(s, 10);
    }

    /**
     * Decodes a String into a Short.  The String may represent
     * decimal, hexadecimal, and octal numbers.
     *
     * @param nm the string to decode
     * @since   JDK1.1
     */
    public static Short decode(String nm) throws NumberFormatException {
	if (nm.startsWith("0x")) {
	    return Short.valueOf(nm.substring(2), 16);
	}
	if (nm.startsWith("#")) {
	    return Short.valueOf(nm.substring(1), 16);
	}
	if (nm.startsWith("0") && nm.length() > 1) {
	    return Short.valueOf(nm.substring(1), 8);
	}

	return Short.valueOf(nm);
    }

    /**
     * The value of the Short.
     */
    private short value;

    /**
     * Constructs a Short object initialized to the specified short value.
     *
     * @param value	the initial value of the Short
     * @since   JDK1.1
     */
    public Short(short value) {
	this.value = value;
    }

    /**
     * Constructs a Short object initialized to the value specified by the
     * String parameter.  The radix is assumed to be 10.
     *
     * @param s		the String to be converted to a Short
     * @exception	NumberFormatException If the String does not
     *			contain a parsable short.
     * @since   JDK1.1
     */
    public Short(String s) throws NumberFormatException {
	this.value = parseShort(s);
    }

    /**
     * Returns the value of this Short as a byte.
     *
     * @since   JDK1.1
     */
    public byte byteValue() {
	return (byte)value;
    }

    /**
     * Returns the value of this Short as a short.
     *
     * @since   JDK1.1
     */
    public short shortValue() {
	return value;
    }

    /**
     * Returns the value of this Short as an int.
     *
     * @since   JDK1.1
     */
    public int intValue() {
	return (int)value;
    }

    /**
     * Returns the value of this Short as a long.
     *
     * @since   JDK1.1
     */
    public long longValue() {
	return (long)value;
    }

    /**
     * Returns the value of this Short as a float.
     *
     * @since   JDK1.1
     */
    public float floatValue() {
	return (float)value;
    }

    /**
     * Returns the value of this Short as a double.
     *
     * @since   JDK1.1
     */
    public double doubleValue() {
	return (double)value;
    }

    /**
     * Returns a String object representing this Short's value.
     *
     * @since   JDK1.1
     */
    public String toString() {
	return String.valueOf((int)value);
    }

    /**
     * Returns a hashcode for this Short.
     *
     * @since   JDK1.1
     */
    public int hashCode() {
	return (int)value;
    }

    /**
     * Compares this object to the specified object.
     *
     * @param obj	the object to compare with
     * @return 		true if the objects are the same; false otherwise.
     * @since   JDK1.1
     */
    public boolean equals(Object obj) {
	if ((obj != null) && (obj instanceof Short)) {
	    return value == ((Short)obj).shortValue();
	}
	return false;
    }
}
