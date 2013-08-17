/*
 * @(#)Long.java	1.34 98/07/07
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
 * @version 1.34, 07/07/98
 * @since   JDK1.0
 */
public final
class Long extends Number {
    /**
     * The smallest value of type <code>long</code>. 
     *
     * @since   JDK1.0
     */
    public static final long MIN_VALUE = 0x8000000000000000L;

    /**
     * The largest value of type <code>long</code>. 
     *
     * @since   JDK1.0
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
     * result is the ASCII minus sign <code>'-'</code>. If the first 
     * argument is not negative, no sign character appears in the result. 
     * The following ASCII characters are used as digits: 
     * <ul><code>
     *   0123456789abcdefghijklmnopqrstuvwxyz
     * </code></ul>
     *
     * @param   i       a long.
     * @param   radix   the radix.
     * @return  a string representation of the argument in the specified radix.
     * @see     java.lang.Character#MAX_RADIX
     * @see     java.lang.Character#MIN_RADIX
     * @since   JDK1.0
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
     * This value is converted to a string of ASCII digits in hexadecimal 
     * (base&nbsp;16) with no extra leading <code>0</code>s. 
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
     * This value is converted to a string of ASCII digits in octal 
     * (base&nbsp;8) with no extra leading <code>0</code>s. 
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
     * This value is converted to a string of ASCII digits in binary 
     * (base&nbsp;2) with no extra leading <code>0</code>s. 
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
     * Returns a new String object representing the specified integer. The radix
     * is assumed to be 10.
     *
     * @param   i   a <code>long</code> to be converted.
     * @return  a string representation of the argument in base&nbsp;10.
     * @since   JDK1.0
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
     * ASCII minus sign <code>'-'</code> to indicate a negative value. 
     * The resulting <code>long</code> value is returned. 
     *
     * @param      s       the <code>String</code> containing the
     *                     <code>long</code>.
     * @param      radix   the radix to be used.
     * @return     the <code>long</code> represented by the string argument in
     *             the specified radix.
     * @exception  NumberFormatException  if the string does not contain a
     *               parsable integer.
     * @since      JDK1.0
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
     * <code>'-'</code> to indicate a negative value. 
     *
     * @param      s   a string.
     * @return     the <code>long</code> represented by the argument in decimal.
     * @exception  NumberFormatException  if the string does not contain a
     *               parsable <code>long</code>.
     * @since      JDK1.0
     */
    public static long parseLong(String s) throws NumberFormatException {
	return parseLong(s, 10);
    }

    /**
     * Returns a new long object initialized to the value of the
     * specified String. Throws an exception if the String cannot be
     * parsed as a long.
     *
     * @param      s       the <code>String</code> containing the
     *                     <code>long</code>.
     * @param      radix   the radix to be used. 
     * @return     a newly constructed <code>Long</code> initialized to the
     *             value represented by the string argument in the specified
     *             radix.
     * @exception  NumberFormatException  If the <code>String</code> does not
     *               contain a parsable <code>long</code>.
     * @since      JDK1.0
     */
    public static Long valueOf(String s, int radix) throws NumberFormatException {
	return new Long(parseLong(s, radix));
    }

    /**
     * Returns a new long object initialized to the value of the
     * specified String. Throws an exception if the String cannot be
     * parsed as a long. The radix is assumed to be 10.
     *
     * @param      s   the string to be parsed.
     * @return     a newly constructed <code>Long</code> initialized to the
     *             value represented by the string argument.
     * @exception  NumberFormatException  If the <code>String</code> does not
     *               contain a parsable <code>long</code>.
     * @since   JDK1.0
     */
    public static Long valueOf(String s) throws NumberFormatException 
    {
	return new Long(parseLong(s, 10));
    }

    /**
     * The value of the Long.
     */
    private long value;

    /**
     * Constructs a newly allocated <code>Long</code> object that 
     * represents the primitive <code>long</code> argument. 
     *
     * @param   value   the value to be represented by the <code>Long</code>.
     * @since   JDK1.0
     */
    public Long(long value) {
	this.value = value;
    }

    /**
     * Constructs a newly allocated <code>Long</code> object that 
     * represents the value represented by the string. The string is 
     * converted to an <code>long</code> value as if by the 
     * <code>valueOf</code> method. 
     *
     * @param      s   the string to be converted to a <code>Long</code>.
     * @exception  NumberFormatException  if the <code>String</code> does not
     *               contain a parsable long integer.
     * @see        java.lang.Long#valueOf(java.lang.String)
     * @since      JDK1.0
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
     * @since   JDK1.0
     */
    public int intValue() {
	return (int)value;
    }

    /**
     * Returns the value of this Long as a long.
     *
     * @return  the <code>long</code> value represented by this object.
     * @since   JDK1.0
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
     * @since   JDK1.0
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
     * @since   JDK1.0
     */
    public double doubleValue() {
	return (double)value;
    }

    /**
     * Returns a String object representing this Long's value.
     *
     * @return  a string representation of this object in base&nbsp;10.
     * @since   JDK1.0
     */
    public String toString() {
	return String.valueOf(value);
    }

    /**
     * Computes a hashcode for this Long.
     *
     * @return  a hash code value for this object. 
     * @since   JDK1.0
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
     * @since   JDK1.0
     */
    public boolean equals(Object obj) {
	if ((obj != null) && (obj instanceof Long)) {
	    return value == ((Long)obj).longValue();
	}
	return false;
    }

    /**
     * Determines the <code>long</code> value of the system property 
     * with the specified name. 
     * <p>
     * The first argument is treated as the name of a system property. 
     * System properties are accessible through <code>getProperty</code>  
     * and , a method defined by the <code>System</code> class. The 
     * string value of this property is then interpreted as a long value 
     * and a <code>Long</code> object representing this value is returned. 
     * Details of possible numeric formats can be found with the 
     * definition of <code>getProperty</code>. 
     * <p>
     * If there is no property with the specified name, or if the 
     * property does not have the correct numeric format, then 
     * <code>null</code> is returned. 
     *
     * @param   nm   property name.
     * @return  the <code>Long</code> value of the property.
     * @see     java.lang.System#getProperty(java.lang.String)
     * @see     java.lang.System#getProperty(java.lang.String, java.lang.String)
     * @since   JDK1.0
     */
    public static Long getLong(String nm) {
	SecurityManager sm = System.getSecurityManager();
	if (sm != null)
	    sm.checkPropertyAccess(nm);
	return getLong(nm, null);
    }

    /**
     * Determines the <code>long</code> value of the system property 
     * with the specified name. 
     * <p>
     * The first argument is treated as the name of a system property. 
     * System properties are accessible through <code>getProperty</code>  
     * and , a method defined by the <code>System</code> class. The 
     * string value of this property is then interpreted as a long value 
     * and a <code>Long</code> object representing this value is returned. 
     * Details of possible numeric formats can be found with the 
     * definition of <code>getProperty</code>. 
     * <p>
     * If there is no property with the specified name, or if the 
     * property does not have the correct numeric format, then a 
     * <code>Long</code> object that represents the value of the second 
     * argument is returned. 
     *
     * @param   nm    property name.
     * @param   val   default value.
     * @return  the <code>Long</code> value of the property.
     * @see     java.lang.System#getProperty(java.lang.String)
     * @see     java.lang.System#getProperty(java.lang.String, java.lang.String)
     * @since   JDK1.0
     */
    public static Long getLong(String nm, long val) {
	SecurityManager sm = System.getSecurityManager();
	if (sm != null)
	    sm.checkPropertyAccess(nm);
        Long result = Long.getLong(nm, null);
        return (result == null) ? new Long(val) : result;
    }

    /**
     * Determines the <code>long</code> value of the system property 
     * with the specified name. 
     * <p>
     * The first argument is treated as the name of a system property. 
     * System properties are accessible through <code>getProperty</code>  
     * and , a method defined by the <code>System</code> class. The 
     * string value of this property is then interpreted as a long value 
     * and a <code>Long</code> object representing this value is returned. 
     * <p>
     * If the property value begins with "<code>0x</code>" or 
     * "<code>#</code>", not followed by a minus sign, the rest 
     * of it is parsed as a hexadecimal integer exactly as for the method 
     * <code>Long.valueOf</code> with radix 16. 
     * <p>
     * If the property value begins with "<code>0</code>", 
     * then it is parsed as an octal integer exactly as for the method 
     * <code>Long.valueOf</code> with radix 8. 
     * <p>
     * Otherwise the property value is parsed as a decimal integer 
     * exactly as for the method <code>Long.valueOf</code> with radix 10. 
     * <p>
     * Note that, in every case, neither <code>L</code> nor 
     * <code>l</code> is permitted to appear at the end of the string. 
     * <p>
     * The second argument is the default value. If there is no property 
     * of the specified name, or if the property does not have the 
     * correct numeric format, then the second argument is returned. 
     *
     * @param   nm    the property name.
     * @param   val   the default <code>Long</code> value.
     * @return  the <code>long</code> value of the property.
     * @see     java.lang.Long#valueOf(java.lang.String, int)
     * @see     java.lang.System#getProperty(java.lang.String)
     * @see     java.lang.System#getProperty(java.lang.String, java.lang.String)
     * @since   JDK1.0
     */
    public static Long getLong(String nm, Long val) {
	SecurityManager sm = System.getSecurityManager();
	if (sm != null)
	    sm.checkPropertyAccess(nm);
	String v = System.getProperty(nm);
	if (v != null) {
	    try {
		if (v.startsWith("0x")) {
		    return Long.valueOf(v.substring(2), 16);
		}
		if (v.startsWith("#")) {
		    return Long.valueOf(v.substring(1), 16);
		}
		if (v.startsWith("0") && v.length() > 1) {
		    return Long.valueOf(v.substring(1), 8);
		}
		return Long.valueOf(v);
	    } catch (NumberFormatException e) {
	    }
	}	
	return val;
    }

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = 4290774380558885855L;
}
