/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

/**
 * The Short class is the standard wrapper for short values.
 *
 * @author  Nakul Saraiya
 * @version 1.23, 02/06/02
 * @see     java.lang.Number
 * @since   JDK1.1
 */
public final
class Short extends Number implements Comparable {

    /**
     * The minimum value a Short can have.
     */
    public static final short   MIN_VALUE = -32768;

    /**
     * The maximum value a Short can have.
     */
    public static final short   MAX_VALUE = 32767;

    /**
     * The Class object representing the primitive type short.
     */
    public static final Class	TYPE = Class.getPrimitiveClass("short");

    /**
     * Returns a new String object representing the specified
     * Short. The radix is assumed to be 10.
     *
     * @param s the short to be converted
     * @return The String that represents the specified short in radix 10.
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
     * @return short    the value represented by the specified string
     * @exception	NumberFormatException If the string does not
     *			contain a parsable short.
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
     * @return          The short value represented by the specified string in
     *                  the specified radix.
     * @exception	NumberFormatException If the String does not
     *			contain a parsable short.
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
     * @return          The Short value represented by the specified string in
     *                  the specified radix.
     * @exception	NumberFormatException If the String does not
     *			contain a parsable short.
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
     * @return Short    of the value represented by the specified string in
     *                  radix 10.
     * @exception	NumberFormatException If the String does not
     *			contain a parsable short.
     */
    public static Short valueOf(String s) throws NumberFormatException {
	return valueOf(s, 10);
    }

    /**
     * Decodes a <code>String</code> into a <code>Short</code>.  Accepts
     * decimal, hexadecimal, and octal numbers, in the following formats:
     * <pre>
     *     [-]        decimal constant
     *     [-] 0x     hex constant
     *     [-] #      hex constant
     *     [-] 0      octal constant
     * </pre>
     *
     * The constant following an (optional) negative sign and/or "radix
     * specifier" is parsed as by the <code>Short.parseShort</code> method
     * with the specified radix (10, 8 or 16).  This constant must be positive
     * or a NumberFormatException will result.  The result is made negative if
     * first character of the specified <code>String</code> is the negative
     * sign.  No whitespace characters are permitted in the
     * <code>String</code>.
     *
     * @param     nm the <code>String</code> to decode.
     * @return    the <code>Short</code> represented by the specified string.
     * @exception NumberFormatException  if the <code>String</code> does not
     *            contain a parsable short.
     * @see java.lang.Short#parseShort(String, int)
     */
    public static Short decode(String nm) throws NumberFormatException {
        int radix = 10;
        int index = 0;
        boolean negative = false;
        Short result;

        // Handle minus sign, if present
        if (nm.startsWith("-")) {
            negative = true;
            index++;
        }

        // Handle radix specifier, if present
	if (nm.startsWith("0x", index) || nm.startsWith("0X", index)) {
	    index += 2;
            radix = 16;
	}
	else if (nm.startsWith("#", index)) {
	    index ++;
            radix = 16;
	}
	else if (nm.startsWith("0", index) && nm.length() > 1 + index) {
	    index ++;
            radix = 8;
	}

        if (nm.startsWith("-", index))
            throw new NumberFormatException("Negative sign in wrong position");

        try {
            result = Short.valueOf(nm.substring(index), radix);
            result = negative ? new Short((short)-result.shortValue()) :result;
        } catch (NumberFormatException e) {
            // If number is Short.MIN_VALUE, we'll end up here. The next line
            // handles this case, and causes any genuine format error to be
            // rethrown.
            String constant = negative ? new String("-" + nm.substring(index))
                                       : nm.substring(index);
            result = Short.valueOf(constant, radix);
        }
        return result;
    }

    /**
     * The value of the Short.
     *
     * @serial
     */
    private short value;

    /**
     * Constructs a Short object initialized to the specified short value.
     *
     * @param value	the initial value of the Short
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
     */
    public Short(String s) throws NumberFormatException {
	this.value = parseShort(s);
    }

    /**
     * Returns the value of this Short as a byte.
     */
    public byte byteValue() {
	return (byte)value;
    }

    /**
     * Returns the value of this Short as a short.
     */
    public short shortValue() {
	return value;
    }

    /**
     * Returns the value of this Short as an int.
     */
    public int intValue() {
	return (int)value;
    }

    /**
     * Returns the value of this Short as a long.
     */
    public long longValue() {
	return (long)value;
    }

    /**
     * Returns the value of this Short as a float.
     */
    public float floatValue() {
	return (float)value;
    }

    /**
     * Returns the value of this Short as a double.
     */
    public double doubleValue() {
	return (double)value;
    }

    /**
     * Returns a String object representing this Short's value.
     */
    public String toString() {
	return String.valueOf((int)value);
    }

    /**
     * Returns a hashcode for this Short.
     */
    public int hashCode() {
	return (int)value;
    }

    /**
     * Compares this object to the specified object.
     *
     * @param obj	the object to compare with
     * @return 		true if the objects are the same; false otherwise.
     */
    public boolean equals(Object obj) {
	if (obj instanceof Short) {
	    return value == ((Short)obj).shortValue();
	}
	return false;
    }

    /**
     * Compares two Shorts numerically.
     *
     * @param   anotherShort   the <code>Short</code> to be compared.
     * @return  the value <code>0</code> if the argument Short is equal to
     *          this Short; a value less than <code>0</code> if this Short
     *          is numerically less than the Short argument; and a
     *          value greater than <code>0</code> if this Short is
     *          numerically greater than the Short argument
     *		(signed comparison).
     * @since   1.2
     */
    public int compareTo(Short anotherShort) {
	return this.value - anotherShort.value;
    }

    /**
     * Compares this Short to another Object.  If the Object is a Short,
     * this function behaves like <code>compareTo(Short)</code>.  Otherwise,
     * it throws a <code>ClassCastException</code> (as Shorts are comparable
     * only to other Shorts).
     *
     * @param   o the <code>Object</code> to be compared.
     * @return  the value <code>0</code> if the argument is a Short
     *		numerically equal to this Short; a value less than
     *		<code>0</code> if the argument is a Short numerically
     *		greater than this Short; and a value greater than
     *		<code>0</code> if the argument is a Short numerically
     *		less than this Short.
     * @exception <code>ClassCastException</code> if the argument is not a
     *		  <code>Short</code>.
     * @see     java.lang.Comparable
     * @since   1.2
     */
    public int compareTo(Object o) {
	return compareTo((Short)o);
    }

    /** use serialVersionUID from JDK 1.1. for interoperability */
    private static final long serialVersionUID = 7515723908773894738L;
}
