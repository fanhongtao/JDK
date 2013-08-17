/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

/**
 * The Long class wraps a value of the primitive type <code>long</code>
 * in an object. An object of type <code>Long</code> contains a single
 * field whose type is <code>long</code>.
 * <p>
 * In addition, this class provides several methods for converting a
 * <code>long</code> to a <code>String</code> and a
 * <code>String</code> to a <code>long</code>, as well as other
 * constants and methods useful when dealing with a
 * <code>long</code>.
 *
 * @author  Lee Boynton
 * @author  Arthur van Hoff
 * @version 1.53, 02/06/02
 * @since   JDK1.0
 */
public final class Long extends Number implements Comparable {
    /**
     * The smallest value of type <code>long</code>.
     */
    public static final long MIN_VALUE = 0x8000000000000000L;

    /**
     * The largest value of type <code>long</code>.
     */
    public static final long MAX_VALUE = 0x7fffffffffffffffL;

    /**
     * The Class object representing the primitive type long.
     *
     * @since   JDK1.1
     */
    public static final Class	TYPE = Class.getPrimitiveClass("long");

    /**
     * Creates a string representation of the first argument in the
     * radix specified by the second argument.
     * <p>
     * If the radix is smaller than <code>Character.MIN_RADIX</code> or
     * larger than <code>Character.MAX_RADIX</code>, then the radix
     * <code>10</code> is used instead.
     * <p>
     * If the first argument is negative, the first element of the 
     * result is the ASCII minus sign <code>'-'</code> 
     * (<code>'&#92;u002d'</code>. If the first argument is not negative, 
     * no sign character appears in the result. 
     * <p>
     * The remaining characters of the result represent the magnitude of 
     * the first argument. If the magnitude is zero, it is represented by 
     * a single zero character <code>'0'</code> 
     * (<code>'&#92;u0030'</code>); otherwise, the first character of the
     * representation of the magnitude will not be the zero character.
     * The following ASCII characters are used as digits: 
     * <blockquote><pre>
     *   0123456789abcdefghijklmnopqrstuvwxyz
     * </pre></blockquote>
     * These are <tt>'&#92;u0030'</tt> through <tt>'&#92;u0039'</tt> 
     * and <tt>'&#92;u0061'</tt> through <tt>'&#92;u007a'</tt>. If the 
     * radix is <var>N</var>, then the first <var>N</var> of these 
     * characters are used as radix-<var>N</var> digits in the order 
     * shown. Thus, the digits for hexadecimal (radix 16) are 
     * <tt>0123456789abcdef</tt>. If uppercase letters
     * are desired, the {@link java.lang.String#toUpperCase()} method 
     * may be called on the result: 
     * <blockquote><pre>
     * Long.toString(n, 16).toUpperCase()
     * </pre></blockquote>
     * 
     * @param   i       a long.
     * @param   radix   the radix.
     * @return  a string representation of the argument in the specified radix.
     * @see     java.lang.Character#MAX_RADIX
     * @see     java.lang.Character#MIN_RADIX
     */
    public static String toString(long i, int radix) {
        if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
	    radix = 10;

	char[] buf = new char[65];
	int charPos = 64;
	boolean negative = (i < 0);

	if (!negative) {
	    i = -i;
	}

	while (i <= -radix) {
	    buf[charPos--] = Integer.digits[(int)(-(i % radix))];
	    i = i / radix;
	}
	buf[charPos] = Integer.digits[(int)(-i)];

	if (negative) {
	    buf[--charPos] = '-';
	}

	return new String(buf, charPos, (65 - charPos));
    }

    /**
     * Creates a string representation of the long argument as an
     * unsigned integer in base&nbsp;16.
     * <p>
     * The unsigned long value is the argument plus 2<sup>64</sup> if 
     * the argument is negative; otherwise, it is equal to the argument. 
     * <p>
     * If the unsigned magnitude is zero, it is represented by a single 
     * zero character <tt>'0'</tt> (<tt>'&#92;u0030'</tt>); otherwise, 
     * the first character of the representation of the unsigned magnitude 
     * will not be the zero character. The following characters are used 
     * as hexadecimal digits:
     * <blockquote><pre>
     * 0123456789abcdef
     * </pre></blockquote>
     * These are the characters <tt>'&#92;u0030'</tt> through 
     * <tt>'&#92;u0039'</tt> and '&#92;u0061' through <tt>'&#92;u0066'</tt>. 
     * If uppercase letters are desired, the 
     * {@link java.lang.String#toUpperCase()} method may be called on 
     * the result: 
     * <blockquote><pre>
     * Long.toHexString(n).toUpperCase()
     * </pre></blockquote>
     *
     * @param   i   a <code>long</code>.
     * @return  the string representation of the unsigned long value
     *          represented by the argument in hexadecimal (base&nbsp;16).
     * @since   JDK 1.0.2
     */
    public static String toHexString(long i) {
	return toUnsignedString(i, 4);
    }

    /**
     * Creates a string representation of the long argument as an
     * unsigned integer in base&nbsp;8.
     * <p>
     * The unsigned long value is the argument plus 2<sup>64</sup> if 
     * the argument is negative; otherwise, it is equal to the argument. 
     * <p>
     * If the unsigned magnitude is zero, it is represented by a single zero
     * character <tt>'0'</tt> (<tt>'&#92;u0030'</tt>); otherwise, the 
     * first character of the representation of the unsigned magnitude 
     * will not be the zero character. The following characters are used 
     * as octal digits:
     * <blockquote><pre>
     * 01234567
     * </pre></blockquote>
     * These are the characters <tt>'&#92;u0030'</tt> through 
     * <tt>'&#92;u0037'</tt>. 
     *
     * @param   i   a <code>long</code>.
     * @return  the string representation of the unsigned long value
     *          represented by the argument in octal (base&nbsp;8).
     * @since   JDK 1.0.2
     */
    public static String toOctalString(long i) {
	return toUnsignedString(i, 3);
    }

    /**
     * Creates a string representation of the long argument as an
     * unsigned integer in base&nbsp;2.
     * <p>
     * The unsigned long value is the argument plus 2<sup>64</sup> if 
     * the argument is negative; otherwise, it is equal to the argument. 
     * <p>
     * If the unsigned magnitude is zero, it is represented by a single zero
     * character <tt>'0'</tt> (<tt>'&392;u0030'</tt>); otherwise, the 
     * first character of the representation of the unsigned magnitude 
     * will not be the zero character. The characters <tt>'0'</tt> 
     * (<tt>'&#92;u0030'</tt>) and <tt>'1'</tt> (<tt>'&#92;u0031'</tt>) 
     * are used as binary digits. 
     *
     * @param   i   a long.
     * @return  the string representation of the unsigned long value
     *          represented by the argument in binary (base&nbsp;2).
     * @since   JDK 1.0.2
     */
    public static String toBinaryString(long i) {
	return toUnsignedString(i, 1);
    }

    /**
     * Convert the integer to an unsigned number.
     */
    private static String toUnsignedString(long i, int shift) {
	char[] buf = new char[64];
	int charPos = 64;
	int radix = 1 << shift;
	long mask = radix - 1;
	do {
	    buf[--charPos] = Integer.digits[(int)(i & mask)];
	    i >>>= shift;
	} while (i != 0);
	return new String(buf, charPos, (64 - charPos));
    }

    /**
     * Returns a new String object representing the specified integer. 
     * The argument is converted to signed decimal representation and 
     * returned as a string, exactly as if the argument and the radix 
     * 10 were given as arguments to the 
     * {@link #toString(long, int)} method that takes two arguments.
     *
     * @param   i   a <code>long</code> to be converted.
     * @return  a string representation of the argument in base&nbsp;10.
     */
    public static String toString(long i) {
	return toString(i, 10);
    }

    /**
     * Parses the string argument as a signed <code>long</code> in the 
     * radix specified by the second argument. The characters in the 
     * string must all be digits of the specified radix (as determined by 
     * whether <code>Character.digit</code> returns a 
     * nonnegative value), except that the first character may be an 
     * ASCII minus sign <code>'-'</code> (<tt>'&#92;u002d'</tt> to indicate 
     * a negative value. The resulting <code>long</code> value is returned. 
     * <p>
     * Note that neither <tt>L</tt> nor <tt>l</tt> is permitted to appear at 
     * the end of the string as a type indicator, as would be permitted in 
     * Java programming language source code - except that either <tt>L</tt> 
     * or <tt>l</tt> may appear as a digit for a radix greater than 22.
     * <p>
     * An exception of type <tt>NumberFormatException</tt> is thrown if any of
     * the following situations occurs:
     * <ul>
     * <li>The first argument is <tt>null</tt> or is a string of length zero. 
     * <li>The <tt>radix</tt> is either smaller than 
     *     {@link java.lang.Character#MIN_RADIX} or larger than 
     *     {@link java.lang.Character#MAX_RADIX}. 
     * <li>The first character of the string is not a digit of the
     *     specified <tt>radix</tt> and is not a minus sign <tt>'-'</tt> 
     *     (<tt>'&#92;u002d'</tt>). 
     * <li>The first character of the string is a minus sign and the
     *     string is of length 1. 
     * <li>Any character of the string after the first is not a digit of
     *     the specified <tt>radix</tt>. 
     * <li>The integer value represented by the string cannot be
     *     represented as a value of type <tt>long</tt>. 
     * </ul><p>
     * Examples:
     * <blockquote><pre>
     * parseLong("0", 10) returns 0L
     * parseLong("473", 10) returns 473L
     * parseLong("-0", 10) returns 0L
     * parseLong("-FF", 16) returns -255L
     * parseLong("1100110", 2) returns 102L
     * parseLong("99", 8) throws a NumberFormatException
     * parseLong("Hazelnut", 10) throws a NumberFormatException
     * parseLong("Hazelnut", 36) returns 1356099454469L
     * </pre></blockquote>
     * 
     * @param      s       the <code>String</code> containing the
     *                     <code>long</code>.
     * @param      radix   the radix to be used.
     * @return     the <code>long</code> represented by the string argument in
     *             the specified radix.
     * @exception  NumberFormatException  if the string does not contain a
     *               parsable integer.
     */
    public static long parseLong(String s, int radix)
              throws NumberFormatException
    {
        if (s == null) {
            throw new NumberFormatException("null");
        }

	if (radix < Character.MIN_RADIX) {
	    throw new NumberFormatException("radix " + radix +
					    " less than Character.MIN_RADIX");
	}
	if (radix > Character.MAX_RADIX) {
	    throw new NumberFormatException("radix " + radix +
					    " greater than Character.MAX_RADIX");
	}

	long result = 0;
	boolean negative = false;
	int i = 0, max = s.length();
	long limit;
	long multmin;
	int digit;

	if (max > 0) {
	    if (s.charAt(0) == '-') {
		negative = true;
		limit = Long.MIN_VALUE;
		i++;
	    } else {
		limit = -Long.MAX_VALUE;
	    }
	    multmin = limit / radix;
            if (i < max) {
                digit = Character.digit(s.charAt(i++),radix);
		if (digit < 0) {
		    throw new NumberFormatException(s);
		} else {
		    result = -digit;
		}
	    }
	    while (i < max) {
		// Accumulating negatively avoids surprises near MAX_VALUE
		digit = Character.digit(s.charAt(i++),radix);
		if (digit < 0) {
		    throw new NumberFormatException(s);
		}
		if (result < multmin) {
		    throw new NumberFormatException(s);
		}
		result *= radix;
		if (result < limit + digit) {
		    throw new NumberFormatException(s);
		}
		result -= digit;
	    }
	} else {
	    throw new NumberFormatException(s);
	}
	if (negative) {
	    if (i > 1) {
		return result;
	    } else {	/* Only got "-" */
		throw new NumberFormatException(s);
	    }
	} else {
	    return -result;
	}
    }

    /**
     * Parses the string argument as a signed decimal <code>long</code>. 
     * The characters in the string must all be decimal digits, except 
     * that the first character may be an ASCII minus sign 
     * <code>'-'</code> (<code>&#92;u002d'</code>) to indicate a negative 
     * value. The resulting long value is returned, exactly as if the
     * argument and the radix <tt>10</tt> were given as arguments to the
     * {@link #parseLong(String, int)} method that takes two arguments. 
     * <p>
     * Note that neither <tt>L</tt> nor <tt>l</tt> is permitted to appear 
     * at the end of the string as a type indicator, as would be permitted
     * in Java programming language source code.
     *
     * @param      s   a string.
     * @return     the <code>long</code> represented by the argument in decimal.
     * @exception  NumberFormatException  if the string does not contain a
     *               parsable <code>long</code>.
     */
    public static long parseLong(String s) throws NumberFormatException {
	return parseLong(s, 10);
    }

    /**
     * Returns a new long object initialized to the value of the
     * specified String. Throws an exception if the String cannot be
     * parsed as a long.
     * <p>
     * The first argument is interpreted as representing a signed integer
     * in the radix specified by the second argument, exactly as if the
     * arguments were given to the {@link #parseLong(java.lang.String, int)} 
     * method that takes two arguments. The result is a <tt>Long</tt> object 
     * that represents the integer value specified by the string. 
     * <p>
     * In other words, this method returns a <tt>Long</tt> object equal 
     * to the value of:
     * <blockquote><pre>
     * new Long(Long.parseLong(s, radix))
     * </pre></blockquote>
     *
     * @param      s       the <code>String</code> containing the
     *                     <code>long</code>.
     * @param      radix   the radix to be used.
     * @return     a newly constructed <code>Long</code> initialized to the
     *             value represented by the string argument in the specified
     *             radix.
     * @exception  NumberFormatException  If the <code>String</code> does not
     *               contain a parsable <code>long</code>.
     */
    public static Long valueOf(String s, int radix) throws NumberFormatException {
	return new Long(parseLong(s, radix));
    }

    /**
     * Returns a new long object initialized to the value of the
     * specified String. Throws an exception if the String cannot be
     * parsed as a long. The radix is assumed to be 10.
     * <p>
     * The argument is interpreted as representing a signed decimal
     * integer, exactly as if the argument were given to the 
     * {@link #parseLong(java.lang.String)} method that takes one argument). 
     * The result is a <code>Long</code> object that represents the integer 
     * value specified by the string. 
     * <p>
     * In other words, this method returns a <tt>Long</tt> object equal to 
     * the value of:
     * <blockquote><pre>
     * new Long(Long.parseLong(s))
     * </pre></blockquote>
     *
     * @param      s   the string to be parsed.
     * @return     a newly constructed <code>Long</code> initialized to the
     *             value represented by the string argument.
     * @exception  NumberFormatException  If the <code>String</code> does not
     *               contain a parsable <code>long</code>.
     */
    public static Long valueOf(String s) throws NumberFormatException
    {
	return new Long(parseLong(s, 10));
    }

    /**
     * Decodes a <code>String</code> into a <code>Long</code>.  Accepts
     * decimal, hexadecimal, and octal numbers, in the following formats:
     * <pre>
     *     [-]        decimal constant
     *     [-] 0x     hex constant
     *     [-] #      hex constant
     *     [-] 0      octal constant
     * </pre>
     *
     * The constant following an (optional) negative sign and/or "radix
     * specifier" is parsed as by the <code>Long.parseLong</code> method
     * with the specified radix (10, 8 or 16).  This constant must be positive
     * or a NumberFormatException will result.  The result is made negative if
     * first character of the specified <code>String</code> is the negative
     * sign.  No whitespace characters are permitted in the
     * <code>String</code>.
     *
     * @param     nm the <code>String</code> to decode.
     * @return    the <code>Long</code> represented by the specified string.
     * @exception NumberFormatException  if the <code>String</code> does not
     *            contain a parsable long.
     * @see java.lang.Long#parseLong(String, int)
     */
    public static Long decode(String nm) throws NumberFormatException {
        int radix = 10;
        int index = 0;
        boolean negative = false;
        Long result;

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
            result = Long.valueOf(nm.substring(index), radix);
            result = negative ? new Long((long)-result.longValue()) : result;
        } catch (NumberFormatException e) {
            // If number is Long.MIN_VALUE, we'll end up here. The next line
            // handles this case, and causes any genuine format error to be
            // rethrown.
            String constant = negative ? new String("-" + nm.substring(index))
                                       : nm.substring(index);
            result = Long.valueOf(constant, radix);
        }
        return result;
    }

    /**
     * The value of the Long.
     *
     * @serial
     */
    private long value;

    /**
     * Constructs a newly allocated <code>Long</code> object that
     * represents the primitive <code>long</code> argument.
     *
     * @param   value   the value to be represented by the 
     *          <code>Long</code> object.
     */
    public Long(long value) {
	this.value = value;
    }

    /**
     * Constructs a newly allocated <code>Long</code> object that 
     * represents the value represented by the string in decimal form. 
     * The string is converted to an <code>long</code> value as if by the 
     * {@link #parseLong(java.lang.String, int)} method for radix 10. 
     *
     * @param      s   the string to be converted to a <code>Long</code>.
     * @exception  NumberFormatException  if the <code>String</code> does not
     *               contain a parsable long integer.
     * @see        java.lang.Long#valueOf(java.lang.String)
     */
    public Long(String s) throws NumberFormatException {
	this.value = parseLong(s, 10);
    }

    /**
     * Returns the value of this Long as a byte.
     *
     * @since   JDK1.1
     */
    public byte byteValue() {
	return (byte)value;
    }

    /**
     * Returns the value of this Long as a short.
     *
     * @since   JDK1.1
     */
    public short shortValue() {
	return (short)value;
    }

    /**
     * Returns the value of this Long as an int.
     *
     * @return  the <code>long</code> value represented by this object is
     *          converted to type <code>int</code> and the result of the
     *          conversion is returned.
     */
    public int intValue() {
	return (int)value;
    }

    /**
     * Returns the value of this Long as a long value.
     *
     * @return  the <code>long</code> value represented by this object.
     */
    public long longValue() {
	return (long)value;
    }

    /**
     * Returns the value of this Long as a float.
     *
     * @return  the <code>long</code> value represented by this object is
     *          converted to type <code>float</code> and the result of
     *          the conversion is returned.
     */
    public float floatValue() {
	return (float)value;
    }

    /**
     * Returns the value of this Long as a double.
     *
     * @return  the <code>long</code> value represented by this object that
     *          is converted to type <code>double</code> and the result of
     *          the conversion is returned.
     */
    public double doubleValue() {
	return (double)value;
    }

    /**
     * Returns a String object representing this Long's value. 
     * The long integer value represented by this Long object is converted 
     * to signed decimal representation and returned as a string, exactly 
     * as if the long value were given as an argument to the 
     * {@link #toString(long)} method that takes one argument.
     *
     * @return  a string representation of this object in base&nbsp;10.
     */
    public String toString() {
	return String.valueOf(value);
    }

    /**
     * Computes a hashcode for this Long. The result is the exclusive 
     * OR of the two halves of the primitive <code>long</code> value 
     * represented by this <code>Long</code> object. That is, the hashcode 
     * is the value of the expression: 
     * <blockquote><pre>
     * (int)(this.longValue()^(this.longValue()>>>32))
     * </pre></blockquote>
     *
     * @return  a hash code value for this object.
     */
    public int hashCode() {
	return (int)(value ^ (value >> 32));
    }

    /**
     * Compares this object against the specified object.
     * The result is <code>true</code> if and only if the argument is
     * not <code>null</code> and is a <code>Long</code> object that
     * contains the same <code>long</code> value as this object.
     *
     * @param   obj   the object to compare with.
     * @return  <code>true</code> if the objects are the same;
     *          <code>false</code> otherwise.
     */
    public boolean equals(Object obj) {
	if (obj instanceof Long) {
	    return value == ((Long)obj).longValue();
	}
	return false;
    }

    /**
     * Determines the <code>long</code> value of the system property
     * with the specified name.
     * <p>
     * The first argument is treated as the name of a system property. 
     * System properties are accessible through the 
     * {@link java.lang.System#getProperty(java.lang.String)} method. The 
     * string value of this property is then interpreted as a long value 
     * and a <code>Long</code> object representing this value is returned. 
     * Details of possible numeric formats can be found with the 
     * definition of <code>getProperty</code>. 
     * <p>
     * If there is no property with the specified name, if the specified name
     * is empty or null, or if the property does not have the correct numeric
     * format, then <code>null</code> is returned.
     * <p>
     * In other words, this method returns a <tt>Long</tt> object equal to 
     * the value of:
     * <blockquote><pre>
     * getLong(nm, null)
     * </pre></blockquote>
     *
     * @param   nm   property name.
     * @return  the <code>Long</code> value of the property.
     * @see     java.lang.System#getProperty(java.lang.String)
     * @see     java.lang.System#getProperty(java.lang.String, java.lang.String)
     */
    public static Long getLong(String nm) {
	return getLong(nm, null);
    }

    /**
     * Determines the <code>long</code> value of the system property
     * with the specified name.
     * <p>
     * The first argument is treated as the name of a system property. 
     * System properties are accessible through the 
     * {@link java.lang.System#getProperty(java.lang.String)} method. The 
     * string value of this property is then interpreted as a long value 
     * and a <code>Long</code> object representing this value is returned. 
     * Details of possible numeric formats can be found with the 
     * definition of <code>getProperty</code>. 
     * <p>
     * The second argument is the default value. A <code>Long</code> object
     * that represents the value of the second argument is returned if there
     * is no property of the specified name, if the property does not have
     * the correct numeric format, or if the specified name is empty or null.
     * <p>
     * In other words, this method returns a <tt>Long</tt> object equal 
     * to the value of:
     * <blockquote><pre>
     * getLong(nm, new Long(val))
     * </pre></blockquote>
     * but in practice it may be implemented in a manner such as: 
     * <blockquote><pre>
     * Long result = getLong(nm, null);
     * return (result == null) ? new Long(val) : result;
     * </pre></blockquote>
     * to avoid the unnecessary allocation of a <tt>Long</tt> object when 
     * the default value is not needed. 
     *
     * @param   nm    property name.
     * @param   val   default value.
     * @return  the <code>Long</code> value of the property.
     * @see     java.lang.System#getProperty(java.lang.String)
     * @see     java.lang.System#getProperty(java.lang.String, java.lang.String)
     */
    public static Long getLong(String nm, long val) {
        Long result = Long.getLong(nm, null);
        return (result == null) ? new Long(val) : result;
    }

    /**
     * Returns the long value of the system property with the specified
     * name.  The first argument is treated as the name of a system property.
     * System properties are accessible through the 
     * {@link java.lang.System#getProperty(java.lang.String)} method. 
     * The string value of this property is then interpreted as a long 
     * value, as per the <code>Long.decode</code> method, and a 
     * <code>Long</code> object representing this value is returned.
     * <p><ul>
     * <li>If the property value begins with the two ASCII characters
     * <tt>0x</tt> or the ASCII character <tt>#</tt>, not followed by a 
     * minus sign, then the rest of it is parsed as a hexadecimal integer
     * exactly as for the method {@link #valueOf(java.lang.String, int)} 
     * with radix 16. 
     * <li>If the property value begins with the character <tt>0</tt> followed
     * by another character, it is parsed as an octal integer exactly
     * as for the method {@link #valueOf(java.lang.String, int)} with radix 8. 
     * <li>Otherwise the property value is parsed as a decimal
     * integer exactly as for the method 
     * {@link #valueOf(java.lang.String, int)} with radix 10. 
     * </ul>
     * <p>
     * Note that, in every case, neither <tt>L</tt> nor <tt>l</tt> is 
     * permitted to appear at the end of the property value as a type 
     * indicator, as would be permitted in Java programming language 
     * source code.
     * <p>
     * The second argument is the default value. The default value is
     * returned if there is no property of the specified name, if the
     * property does not have the correct numeric format, or if the
     * specified name is empty or null.
     *
     * @param   nm   property name.
     * @param   val   default value.
     * @return  the <code>Long</code> value of the property.
     * @see     java.lang.System#getProperty(java.lang.String)
     * @see java.lang.System#getProperty(java.lang.String, java.lang.String)
     * @see java.lang.Long#decode
     */
    public static Long getLong(String nm, Long val) {
        String v = null;
        try {
            v = System.getProperty(nm);
        } catch (IllegalArgumentException e) {
        } catch (NullPointerException e) {
        }
	if (v != null) {
	    try {
		return Long.decode(v);
	    } catch (NumberFormatException e) {
	    }
	}
	return val;
    }

    /**
     * Compares two Longs numerically.
     *
     * @param   anotherLong   the <code>Long</code> to be compared.
     * @return  the value <code>0</code> if the argument Long is equal to
     *          this Long; a value less than <code>0</code> if this Long
     *          is numerically less than the Long argument; and a
     *          value greater than <code>0</code> if this Long is
     *          numerically greater than the Long argument
     *		(signed comparison).
     * @since   1.2
     */
    public int compareTo(Long anotherLong) {
	long thisVal = this.value;
	long anotherVal = anotherLong.value;
	return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
    }

    /**
     * Compares this Long to another Object.  If the Object is a Long,
     * this function behaves like <code>compareTo(Long)</code>.  Otherwise,
     * it throws a <code>ClassCastException</code> (as Longs are comparable
     * only to other Longs).
     *
     * @param   o the <code>Object</code> to be compared.
     * @return  the value <code>0</code> if the argument is a Long
     *		numerically equal to this Long; a value less than
     *		<code>0</code> if the argument is a Long numerically
     *		greater than this Long; and a value greater than
     *		<code>0</code> if the argument is a Long numerically
     *		less than this Long.
     * @exception <code>ClassCastException</code> if the argument is not a
     *		  <code>Long</code>.
     * @see     java.lang.Comparable
     * @since   1.2
     */
    public int compareTo(Object o) {
	return compareTo((Long)o);
    }

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = 4290774380558885855L;
}
