/*
 * @(#)Byte.java	1.8 98/07/01
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
 *
 * The Byte class is the standard wrapper for byte values.
 *
 * @author  Nakul Saraiya
 * @version 1.8, 07/01/98
 * @see     java.lang.Number
 * @since   JDK1.1
 */
public final
class Byte extends Number {

    /**
     * The minimum value a Byte can have.
     *
     * @since   JDK1.1
     */
    public static final byte   MIN_VALUE = -128;

    /**
     * The maximum value a Byte can have.
     *
     * @since   JDK1.1
     */
    public static final byte   MAX_VALUE = 127;

    /**
     * The Class object representing the primitive type byte.
     *
     * @since   JDK1.1
     */
    public static final Class	TYPE = Class.getPrimitiveClass("byte");

    /**
     * Returns a new String object representing the specified Byte. The radix
     * is assumed to be 10.
     *
     * @param b	the byte to be converted
     * @since   JDK1.1
     */
    public static String toString(byte b) {
	return Integer.toString((int)b, 10);
    }

    /**
     * Assuming the specified String represents a byte, returns
     * that byte's value. Throws an exception if the String cannot
     * be parsed as a byte.  The radix is assumed to be 10.
     *
     * @param s		the String containing the byte
     * @exception	NumberFormatException If the string does not
     *			contain a parsable byte.
     * @since   JDK1.1
     */
    public static byte parseByte(String s) throws NumberFormatException {
	return parseByte(s, 10);
    }

    /**
     * Assuming the specified String represents a byte, returns
     * that byte's value. Throws an exception if the String cannot
     * be parsed as a byte.
     *
     * @param s		the String containing the byte
     * @param radix	the radix to be used
     * @exception	NumberFormatException If the String does not
     *			contain a parsable byte.
     * @since   JDK1.1
     */
    public static byte parseByte(String s, int radix)
	throws NumberFormatException {
	int i = Integer.parseInt(s, radix);
	if (i < MIN_VALUE || i > MAX_VALUE)
	    throw new NumberFormatException();
	return (byte)i;
    }

    /**
     * Assuming the specified String represents a byte, returns a
     * new Byte object initialized to that value.  Throws an
     * exception if the String cannot be parsed as a byte.
     *
     * @param s		the String containing the integer
     * @param radix 	the radix to be used
     * @exception	NumberFormatException If the String does not
     *			contain a parsable byte.
     * @since   JDK1.1
     */
    public static Byte valueOf(String s, int radix)
	throws NumberFormatException {
	return new Byte(parseByte(s, radix));
    }

    /**
     * Assuming the specified String represents a byte, returns a
     * new Byte object initialized to that value.  Throws an
     * exception if the String cannot be parsed as a byte.
     * The radix is assumed to be 10.
     *
     * @param s		the String containing the integer
     * @exception	NumberFormatException If the String does not
     *			contain a parsable byte.
     * @since   JDK1.1
     */
    public static Byte valueOf(String s) throws NumberFormatException {
	return valueOf(s, 10);
    }

    /**
     * Decodes a String into a Byte.  The String may represent
     * decimal, hexadecimal, and octal numbers.
     *
     * @param nm the string to decode
     * @since   JDK1.1
     */
    public static Byte decode(String nm) throws NumberFormatException {
	if (nm.startsWith("0x")) {
	    return Byte.valueOf(nm.substring(2), 16);
	}
	if (nm.startsWith("#")) {
	    return Byte.valueOf(nm.substring(1), 16);
	}
	if (nm.startsWith("0") && nm.length() > 1) {
	    return Byte.valueOf(nm.substring(1), 8);
	}

	return Byte.valueOf(nm);
    }

    /**
     * The value of the Byte.
     */
    private byte value;

    /**
     * Constructs a Byte object initialized to the specified byte value.
     *
     * @param value	the initial value of the Byte
     * @since   JDK1.1
     */
    public Byte(byte value) {
	this.value = value;
    }

    /**
     * Constructs a Byte object initialized to the value specified by the
     * String parameter.  The radix is assumed to be 10.
     *
     * @param s		the String to be converted to a Byte
     * @exception	NumberFormatException If the String does not
     *			contain a parsable byte.
     * @since   JDK1.1
     */
    public Byte(String s) throws NumberFormatException {
	this.value = parseByte(s);
    }

    /**
     * Returns the value of this Byte as a byte.
     *
     * @since   JDK1.1
     */
    public byte byteValue() {
	return value;
    }

    /**
     * Returns the value of this Byte as a short.
     *
     * @since   JDK1.1
     */
    public short shortValue() {
	return (short)value;
    }

    /**
     * Returns the value of this Byte as an int.
     *
     * @since   JDK1.1
     */
    public int intValue() {
	return (int)value;
    }

    /**
     * Returns the value of this Byte as a long.
     *
     * @since   JDK1.1
     */
    public long longValue() {
	return (long)value;
    }

    /**
     * Returns the value of this Byte as a float.
     *
     * @since   JDK1.1
     */
    public float floatValue() {
	return (float)value;
    }

    /**
     * Returns the value of this Byte as a double.
     *
     * @since   JDK1.1
     */
    public double doubleValue() {
	return (double)value;
    }

    /**
     * Returns a String object representing this Byte's value.
     *
     * @since   JDK1.1
     */
    public String toString() {
	return String.valueOf((int)value);
    }

    /**
     * Returns a hashcode for this Byte.
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
	if ((obj != null) && (obj instanceof Byte)) {
	    return value == ((Byte)obj).byteValue();
	}
	return false;
    }
}
