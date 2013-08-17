/*
 * @(#)Integer.java	1.43 98/07/07
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
 * The Integer class wraps a value of the primitive type <code>int</code> 
 * in an object. An object of type <code>Integer</code> contains a 
 * single field whose type is <code>int</code>. 
 * <p>
 * In addition, this class provides several methods for converting 
 * an <code>int</code> to a <code>String</code> and a 
 * <code>String</code> to an <code>int</code>, as well as other 
 * constants and methods useful when dealing with an 
 * <code>int</code>. 
 *
 * @author  Lee Boynton
 * @author  Arthur van Hoff
 * @version 1.43, 07/07/98
 * @since   JDK1.0
 */
public final
class Integer extends Number {
    /**
     * The smallest value of type <code>int</code>. 
     *
     * @since   JDK1.0
     */
    public static final int   MIN_VALUE = 0x80000000;

    /**
     * The largest value of type <code>int</code>. 
     *
     * @since   JDK1.0
     */
    public static final int   MAX_VALUE = 0x7fffffff;

    /**
     * The Class object representing the primitive type int.
     *
     * @since   JDK1.1
     */
    public static final Class	TYPE = Class.getPrimitiveClass("int");

    /**
     * All possible chars for representing a number as a String 
     */
    final static char[] digits = { 
	'0' , '1' , '2' , '3' , '4' , '5' , 
	'6' , '7' , '8' , '9' , 'a' , 'b' , 
	'c' , 'd' , 'e' , 'f' , 'g' , 'h' , 
	'i' , 'j' , 'k' , 'l' , 'm' , 'n' , 
	'o' , 'p' , 'q' , 'r' , 's' , 't' , 
	'u' , 'v' , 'w' , 'x' , 'y' , 'z' 
    };
    
    /**
     * Array of chars to lookup the char for the digit in the tenth's 
     * place for a two digit, base ten number.  The char can be got by 
     * using the number as the index.
     */
    private final static char[] radixTenTenths = {
	'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', 
	'1', '1', '1', '1', '1', '1', '1', '1', '1', '1', 
	'2', '2', '2', '2', '2', '2', '2', '2', '2', '2', 
	'3', '3', '3', '3', '3', '3', '3', '3', '3', '3', 
	'4', '4', '4', '4', '4', '4', '4', '4', '4', '4', 
	'5', '5', '5', '5', '5', '5', '5', '5', '5', '5', 
	'6', '6', '6', '6', '6', '6', '6', '6', '6', '6', 
	'7', '7', '7', '7', '7', '7', '7', '7', '7', '7', 
	'8', '8', '8', '8', '8', '8', '8', '8', '8', '8', 
	'9', '9', '9', '9', '9', '9', '9', '9', '9', '9'
    };
    
    /**
     * Array of chars to lookup the char for the digit in the unit's 
     * place for a two digit, base ten number.  The char can be got by 
     * using the number as the index.
     */
    private final static char[] radixTenUnits = {
	'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
	'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
	'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
	'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
	'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
	'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
	'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
	'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
	'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
	'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    /**
     * Creates a string representation of the first argument in the 
     * radix specified by the second argument. 
     * <p>
     * If the radix is smaller than <code>Character.MIN_RADIX</code> or 
     * larger than <code>Character.MAX_RADIX</code>, then the radix 
     * <code>10</code> is used instead. 
     * <p>
     * If the first argument is negative, the first element of the 
     * result is the ASCII minus character <code>'-'</code>. If the first 
     * argument is not negative, no sign character appears in the result. 
     * The following ASCII characters are used as digits: 
     * <ul><code>
     *   0123456789abcdefghijklmnopqrstuvwxyz
     * </code></ul>
     *
     * @param   i       an integer.
     * @param   radix   the radix.
     * @return  a string representation of the argument in the specified radix.
     * @see     java.lang.Character#MAX_RADIX
     * @see     java.lang.Character#MIN_RADIX
     * @since   JDK1.0
     */
    public static String toString(int i, int radix) {
	if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
	    radix = 10;
	
	/* Use the faster version */
	if (radix == 10) {
	    return toString(i);
	}
	
	char buf[] = new char[33];
	boolean negative = (i < 0);
	int charPos = 32;
		
	if (!negative) {
	    i = -i;
	}
		    
	while (i <= -radix) {
	    buf[charPos--] = digits[-(i % radix)];
	    i = i / radix;
	}
	buf[charPos] = digits[-i];

	if (negative) {
	    buf[--charPos] = '-';
	}

	return new String(buf, charPos, (33 - charPos));
    }

    /**
     * Creates a string representation of the integer argument as an 
     * unsigned integer in base&nbsp;16. 
     * <p>
     * The unsigned integer value is the argument plus 2<sup>32</sup> if 
     * the argument is negative; otherwise, it is equal to the argument. 
     * This value is converted to a string of ASCII digits in hexadecimal 
     * (base&nbsp;16) with no extra leading <code>0</code>s. 
     *
     * @param   i   an integer.
     * @return  the string representation of the unsigned integer value
     *          represented by the argument in hexadecimal (base&nbsp;16).
     * @since   JDK1.0.2
     */
    public static String toHexString(int i) {
	return toUnsignedString(i, 4);
    }

    /**
     * Creates a string representation of the integer argument as an 
     * unsigned integer in base 8. 
     * <p>
     * The unsigned integer value is the argument plus 2<sup>32</sup> if 
     * the argument is negative; otherwise, it is equal to the argument. 
     * This value is converted to a string of ASCII digits in octal 
     * (base&nbsp;8) with no extra leading <code>0</code>s. 
     *
     * @param   i   an integer
     * @return  the string representation of the unsigned integer value
     *          represented by the argument in octal (base&mnsp;8).
     * @since   JDK1.0.2
     */
    public static String toOctalString(int i) {
	return toUnsignedString(i, 3);
    }

    /**
     * Creates a string representation of the integer argument as an 
     * unsigned integer in base&nbsp;2. 
     * <p>
     * The unsigned integer value is the argument plus 2<sup>32</sup>if 
     * the argument is negative; otherwise it is equal to the argument. 
     * This value is converted to a string of ASCII digits in binary 
     * (base&nbsp;2) with no extra leading <code>0</code>s. 
     *
     * @param   i   an integer.
     * @return  the string representation of the unsigned integer value
     *          represented by the argument in binary (base&nbsp;2).
     * @since   JDK1.0.2
     */
    public static String toBinaryString(int i) {
	return toUnsignedString(i, 1);
    }

    /** 
     * Convert the integer to an unsigned number.
     */
    private static String toUnsignedString(int i, int shift) {
	char[] buf = new char[32];
	int charPos = 32;
	int radix = 1 << shift;
	int mask = radix - 1;
	do {
	    buf[--charPos] = digits[i & mask];
	    i >>>= shift;
	} while (i != 0);
	
	return new String(buf, charPos, (32 - charPos));
    }

    /**
     * Returns a new String object representing the specified integer. The radix
     * is assumed to be 10.
     *
     * @param   i   an integer to be converted.
     * @return  a string representation of the argument in base&nbsp;10.
     * @since   JDK1.0
     */
    public static String toString(int i) {
	/** 
	 * Performance improvements -
	 *
	 * 1) Avoid a method call and radix checks by inlining the code for 
	 *    radix = 10 in this method.
	 * 2) Use char arrays instead of StringBuffer and avoid calls to
	 *    Character.forDigit.
	 * 3) Do computations in positive space to avoid negation each time
	 *    around the loop.
	 * 4) Unroll loop by half and use a static array of chars to look-
	 *    up chars for a digit.
	 * The other option for 4) was to use a switch statement and assign 
	 * the char for the current digit.  That was a little slower than 4)
	 * with most jits. 
	 * Speed-up = (approximately) 4x on both Solaris and Win32.
	 */
	char[] buf = new char[12];
	boolean negative = (i < 0);
	int charPos = 12;
	
	if (i == Integer.MIN_VALUE) {
	    return "-2147483648";
	}
		
	if (negative) {
	    i = -i;
	}
	
	do {
	    int digit = i%100;
	    buf[--charPos] = radixTenUnits[digit];
	    buf[--charPos] = radixTenTenths[digit];
	    i = i / 100;
	} while(i != 0);
	
	if (buf[charPos] == '0') {
	    charPos++;
	}
	if (negative) {
	    buf[--charPos] = '-';
	}
		
	return new String(buf , charPos , (12 - charPos));
    }
    
    /**
     * Parses the string argument as a signed integer in the radix 
     * specified by the second argument. The characters in the string 
     * must all be digits of the specified radix (as determined by 
     * whether <code>Character.digit</code> returns a 
     * nonnegative value), except that the first character may be an 
     * ASCII minus sign <code>'-'</code> to indicate a negative value. 
     * The resulting integer value is returned. 
     *
     * @param      s   the <code>String</code> containing the integer.
     * @param      radix   the radix to be used.
     * @return     the integer represented by the string argument in the
     *             specified radix.
     * @exception  NumberFormatException  if the string does not contain a
     *               parsable integer.

     * @since      JDK1.0
     */
    public static int parseInt(String s, int radix) 
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
	    
	int result = 0;
	boolean negative = false;
	int i = 0, max = s.length();
	int limit;
	int multmin;
	int digit;

	if (max > 0) {
	    if (s.charAt(0) == '-') {
		negative = true;
		limit = Integer.MIN_VALUE;
		i++;
	    } else {
		limit = -Integer.MAX_VALUE;
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
     * Parses the string argument as a signed decimal integer. The 
     * characters in the string must all be decimal digits, except that 
     * the first character may be an ASCII minus sign <code>'-'</code> to 
     * indicate a negative value. 
     *
     * @param      s   a string.
     * @return     the integer represented by the argument in decimal.
     * @exception  NumberFormatException  if the string does not contain a
     *               parsable integer.
     * @since      JDK1.0
     */
    public static int parseInt(String s) throws NumberFormatException {
	return parseInt(s,10);
    }

    /**
     * Returns a new Integer object initialized to the value of the
     * specified String.  Throws an exception if the String cannot be
     * parsed as an int.
     *
     * @param      s   the string to be parsed.
     * @return     a newly constructed <code>Integer</code> initialized to the
     *             value represented by the string argument in the specified
     *             radix.
     * @exception  NumberFormatException  if the <code>String</code> does not
     *               contain a parsable integer.
     * @since   JDK1.0
     */
    public static Integer valueOf(String s, int radix) throws NumberFormatException {
	return new Integer(parseInt(s,radix));
    }

    /**
     * Returns a new Integer object initialized to the value of the
     * specified String.  Throws an exception if the String cannot be
     * parsed as an int. The radix is assumed to be 10.
     *
     * @param      s   the string to be parsed.
     * @return     a newly constructed <code>Integer</code> initialized to the
     *             value represented by the string argument.
     * @exception  NumberFormatException  if the string does not contain a
     *               parsable integer.
     * @since   JDK1.0
     */
    public static Integer valueOf(String s) throws NumberFormatException
    {
	return new Integer(parseInt(s, 10));
    }

    /**
     * The value of the Integer.
     */
    private int value;

    /**
     * Constructs a newly allocated <code>Integer</code> object that 
     * represents the primitive <code>int</code> argument. 
     *
     * @param   value   the value to be represented by the <code>Integer</code>.
     * @since   JDK1.0
     */
    public Integer(int value) {
	this.value = value;
    }

    /**
     * Constructs a newly allocated <code>Integer</code> object that 
     * represents the value represented by the string. The string is 
     * converted to an int value as if by the <code>valueOf</code> method. 
     *
     * @param      s   the <code>String</code> to be converted to an
     *                 <code>Integer</code>.
     * @exception  NumberFormatException  if the <code>String</code> does not
     *               contain a parsable integer.
     * @see        java.lang.Integer#valueOf(java.lang.String, int)
     * @since      JDK1.0
     */
    public Integer(String s) throws NumberFormatException {
	this.value = parseInt(s, 10);
    }

    /**
     * Returns the value of this Integer as a byte.
     *
     * @since   JDK1.1
     */
    public byte byteValue() {
	return (byte)value;
    }

    /**
     * Returns the value of this Integer as a short.
     *
     * @since   JDK1.1
     */
    public short shortValue() {
	return (short)value;
    }

    /**
     * Returns the value of this Integer as an int.
     *
     * @return  the <code>int</code> value represented by this object.
     * @since   JDK1.0
     */
    public int intValue() {
	return value;
    }

    /**
     * Returns the value of this Integer as a long.
     *
     * @return  the <code>int</code> value represented by this object that is
     *          converted to type <code>long</code> and the result of the
     *          conversion is returned.
     * @since   JDK1.0
     */
    public long longValue() {
	return (long)value;
    }

    /**
     * Returns the value of this Integer as a float.
     *
     * @return  the <code>int</code> value represented by this object is
     *          converted to type <code>float</code> and the result of the
     *          conversion is returned.
     * @since   JDK1.0
     */
    public float floatValue() {
	return (float)value;
    }

    /**
     * Returns the value of this Integer as a double.
     *
     * @return  the <code>int</code> value represented by this object is
     *          converted to type <code>double</code> and the result of the
     *          conversion is returned.
     * @since   JDK1.0
     */
    public double doubleValue() {
	return (double)value;
    }

    /**
     * Returns a String object representing this Integer's value.
     *
     * @return  a string representation of the value of this object in
     *          base&nbsp;10.
     * @since   JDK1.0
     */
    public String toString() {
	return String.valueOf(value);
    }

    /**
     * Returns a hashcode for this Integer.
     *
     * @return  a hash code value for this object. 
     * @since   JDK1.0
     */
    public int hashCode() {
	return value;
    }

    /**
     * Compares this object to the specified object.
     * The result is <code>true</code> if and only if the argument is not 
     * <code>null</code> and is an <code>Integer</code> object that contains 
     * the same <code>int</code> value as this object. 
     *
     * @param   obj   the object to compare with.
     * @return  <code>true</code> if the objects are the same;
     *          <code>false</code> otherwise.
     * @since   JDK1.0
     */
    public boolean equals(Object obj) {
	if ((obj != null) && (obj instanceof Integer)) {
	    return value == ((Integer)obj).intValue();
	}
	return false;
    }

    /**
     * Determines the integer value of the system property with the 
     * specified name. 
     * <p>
     * The first argument is treated as the name of a system property. 
     * System properties are accessible through <code>getProperty</code>  
     * and , a method defined by the <code>System</code> class. The 
     * string value of this property is then interpreted as an integer 
     * value and an <code>Integer</code> object representing this value is 
     * returned. Details of possible numeric formats can be found with 
     * the definition of <code>getProperty</code>. 
     * <p>
     * If there is no property with the specified name, or if the 
     * property does not have the correct numeric format, then 
     * <code>null</code> is returned. 
     *
     * @param   nm   property name.
     * @return  the <code>Integer</code> value of the property.
     * @see     java.lang.System#getProperty(java.lang.String)
     * @see     java.lang.System#getProperty(java.lang.String, java.lang.String)
     * @since   JDK1.0
     */
    public static Integer getInteger(String nm) {
	SecurityManager sm = System.getSecurityManager();
	if (sm != null)
	    sm.checkPropertyAccess(nm);
	return getInteger(nm, null);
    }

    /**
     * Determines the integer value of the system property with the 
     * specified name. 
     * <p>
     * The first argument is treated as the name of a system property. 
     * System properties are accessible through <code>getProperty</code>  
     * and , a method defined by the <code>System</code> class. The 
     * string value of this property is then interpreted as an integer 
     * value and an <code>Integer</code> object representing this value is 
     * returned. Details of possible numeric formats can be found with 
     * the definition of <code>getProperty</code>. 
     * <p>
     * If there is no property with the specified name, or if the 
     * property does not have the correct numeric format, then an 
     * <code>Integer</code> object that represents the value of the 
     * second argument is returned. 
     *
     * @param   nm   property name.
     * @param   val   default value.
     * @return  the <code>Integer</code> value of the property.
     * @see     java.lang.System#getProperty(java.lang.String)
     * @see     java.lang.System#getProperty(java.lang.String, java.lang.String)
     * @since   JDK1.0
     */
    public static Integer getInteger(String nm, int val) {
	SecurityManager sm = System.getSecurityManager();
	if (sm != null)
	    sm.checkPropertyAccess(nm);
        Integer result = getInteger(nm, null);
        return (result == null) ? new Integer(val) : result;
    }

    /**
     * Determines the integer value of the system property with the 
     * specified name. 
     * <p>
     * The first argument is treated as the name of a system property. 
     * System properties are accessible through <code>getProperty</code>  
     * and , a method defined by the <code>System</code> class. The 
     * string value of this property is then interpreted as an integer 
     * value and an <code>Integer</code> object representing this value is 
     * returned. 
     * <p>
     * If the property value begins with "<code>0x</code>" or 
     * "<code>#</code>", not followed by a minus sign, the rest 
     * of it is parsed as a hexadecimal integer exactly as for the method 
     * <code>Integer.valueOf</code> with radix 16. 
     * <p>
     * If the property value begins with "<code>0</code>" then 
     * it is parsed as an octal integer exactly as for the method 
     * <code>Integer.valueOf</code> with radix 8. 
     * <p>
     * Otherwise the property value is parsed as a decimal integer 
     * exactly as for the method <code>Integer.valueOf</code> with radix 10.
     * <p>
     * The second argument is the default value. If there is no property 
     * of the specified name, or if the property does not have the 
     * correct numeric format, then the second argument is returned. 
     *
     * @param   nm   property name.
     * @param   val   default value.
     * @return  the <code>Integer</code> value of the property.
     * @see     java.lang.System#getProperty(java.lang.String)
     * @see     java.lang.System#getProperty(java.lang.String, java.lang.String)
     * @since   JDK1.0
     */
    public static Integer getInteger(String nm, Integer val) {
	SecurityManager sm = System.getSecurityManager();
	if (sm != null)
	    sm.checkPropertyAccess(nm);
	String v = System.getProperty(nm);
	if (v != null) {
	    try {
		return Integer.decode(v);
	    } catch (NumberFormatException e) {
	    }
	}	
	return val;
    }

    /**
     * Decodes a string into an Integer. Deals with decimal, hexadecimal,
     * and octal numbers.
     * @param nm the string to decode
     * @since   JDK1.1
     */
    public static Integer decode(String nm) throws NumberFormatException {
	if (nm.startsWith("0x")) {
	    return Integer.valueOf(nm.substring(2), 16);
	}
	if (nm.startsWith("#")) {
	    return Integer.valueOf(nm.substring(1), 16);
	}
	if (nm.startsWith("0") && nm.length() > 1) {
	    return Integer.valueOf(nm.substring(1), 8);
	}
	
	return Integer.valueOf(nm);
    }

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = 1360826667806852920L;
}
