/*
 * @(#)Double.java	1.42 98/07/01
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
 * The Double class wraps a value of the primitive type 
 * <code>double</code> in an object. An object of type 
 * <code>Double</code> contains a single field whose type is 
 * <code>double</code>. 
 * <p>
 * In addition, this class provides several methods for converting a 
 * <code>double</code> to a <code>String</code> and a 
 * <code>String</code> to a <code>double</code>, as well as other 
 * constants and methods useful when dealing with a 
 * <code>double</code>. 
 *
 * @author  Lee Boynton
 * @author  Arthur van Hoff
 * @version 1.42, 07/01/98
 * @since   JDK1.0
 */
public final
class Double extends Number {
    /**
     * The positive infinity of type <code>double</code>. 
     *
     * @since   JDK1.0
     */
    public static final double POSITIVE_INFINITY = 1.0 / 0.0;

    /**
     * The negative infinity of type <code>double</code>. 
     *
     * @since   JDK1.0
     */
    public static final double NEGATIVE_INFINITY = -1.0 / 0.0;

    /** 
     * A NaN value of type <code>double</code>. 
     *
     * @since   JDK1.0
     */
    public static final double NaN = 0.0d / 0.0;

    /**
     * The largest positive value of type <code>double</code>. 
     *
     * @since   JDK1.0
     */
    public static final double MAX_VALUE = 1.79769313486231570e+308;

    /**
     * The smallest positive value of type <code>double</code>. 
     *
     * @since   JDK1.0
     */
//  public static final double MIN_VALUE = 4.94065645841246544e-324;
    public static final double MIN_VALUE = longBitsToDouble(1L);

    /**
     * The Class object representing the primitive type double.
     *
     * @since   JDK1.1
     */
    public static final Class	TYPE = Class.getPrimitiveClass("double");

    /**
     * Creates a string representation of the <code>double</code> 
     * argument. 
     * <p>
     * The values <code>NaN</code>, <code>NEGATIVE_INFINITY</code>, 
     * <code>POSITIVE_INFINITY</code>, <code>-0.0</code>, and 
     * <code>+0.0</code> are represented by the strings 
     * <code>"NaN"</code>, <code>"-Infinity"</code>, 
     * <code>"Infinity"</code>, <code>"-0.0"</code>, and 
     * <code>"0.0"</code>, respectively. 
     * <p>
     * If <code>d</code> is in the range 
     * <code>10<sup>-3</sup>&nbsp;&lt;= |d|&nbsp;&lt;=10<sup>7</sup></code>,
     * then it is converted to a string in the style 
     * <code>[-]ddd.ddd</code>. Otherwise, it is converted to a 
     * string in the style <code>[-]m.ddddE&#177;xx</code>.
     * <p>
     * There is always a minimum of one digit after the decimal point. 
     * The number of digits is the minimum needed to uniquely distinguish 
     * the argument value from adjacent values of type 
     * <code>double</code>. 
     *
     * @param   d   the double to be converted.
     * @return  a string representation of the argument.
     * @since   JDK1.0
     */
    public static String toString(double d){
	return new FloatingDecimal(d).toJavaFormatString();
    }

    /**
     * Returns a new Double value initialized to the value represented by the 
     * specified String.
     *
     * @param      s   the string to be parsed.
     * @return     a newly constructed <code>Double</code> initialized to the
     *             value represented by the string argument.
     * @exception  NumberFormatException  if the string does not contain a
     *               parsable number.
     * @since      JDK1.0
     */
    public static Double valueOf(String s) throws NumberFormatException { 
	return new Double(valueOf0(s));
    }

    /**
     * Returns true if the specified number is the special Not-a-Number (NaN)
     * value.
     *
     * @param   v   the value to be tested.
     * @return  <code>true</code> if the value of the argument is NaN;
     *          <code>false</code> otherwise.
     * @since   JDK1.0
     */
    static public boolean isNaN(double v) {
	return (v != v);
    }

    /**
     * Returns true if the specified number is infinitely large in magnitude.
     *
     * @param   v   the value to be tested.
     * @return  <code>true</code> if the value of the argument is positive
     *          infinity or negative infinity; <code>false</code> otherwise.
     * @since   JDK1.0
     */
    static public boolean isInfinite(double v) {
	return (v == POSITIVE_INFINITY) || (v == NEGATIVE_INFINITY);
    }

    /**
     * The value of the Double.
     */
    private double value;

    /**
     * Constructs a newly allocated <code>Double</code> object that 
     * represents the primitive <code>double</code> argument. 
     *
     * @param   value   the value to be represented by the <code>Double</code>.
     * @since   JDK1.0
     */
    public Double(double value) {
	this.value = value;
    }

    /**
     * Constructs a newly allocated <code>Double</code> object that 
     * represents the floating- point value of type <code>double</code> 
     * represented by the string. The string is converted to a 
     * <code>double</code> value as if by the <code>valueOf</code> method. 
     *
     * @param      s   a string to be converted to a <code>Double</code>.
     * @exception  NumberFormatException  if the string does not contain a
     *               parsable number.
     * @see        java.lang.Double#valueOf(java.lang.String)
     * @since      JDK1.0
     */
    public Double(String s) throws NumberFormatException {
	// REMIND: this is inefficient
	this(valueOf(s).doubleValue());
    }

    /**
     * Returns true if this Double value is the special Not-a-Number (NaN)
     * value.
     *
     * @return  <code>true</code> if the value represented by this object is
     *          NaN; <code>false</code> otherwise.
     * @since   JDK1.0
     */
    public boolean isNaN() {
	return isNaN(value);
    }

    /**
     * Returns true if this Double value is infinitely large in magnitude.
     *
     * @return  <code>true</code> if the value represented by this object is
     *          positive infinity or negative infinity;
     *          <code>false</code> otherwise.
     * @since   JDK1.0
     */
    public boolean isInfinite() {
	return isInfinite(value);
    }

    /**
     * Returns a String representation of this Double object.
     * The primitive <code>double</code> value represented by this 
     * object is converted to a string exactly as if by the method 
     * <code>toString</code> of one argument. 
     *
     * @return  a <code>String</code> representation of this object.
     * @see     java.lang.Double#toString(double)
     * @since   JDK1.0
     */
    public String toString() {
	return String.valueOf(value);
    }

    /**
     * Returns the value of this Double as a byte (by casting to a byte).
     *
     * @since   JDK1.1
     */
    public byte byteValue() {
	return (byte)value;
    }

    /**
     * Returns the value of this Double as a short (by casting to a short).
     *
     * @since   JDK1.1
     */
    public short shortValue() {
	return (short)value;
    }

    /**
     * Returns the integer value of this Double (by casting to an int).
     *
     * @return  the <code>double</code> value represented by this object is
     *          converted to type <code>int</code> and the result of the
     *          conversion is returned.
     * @since   JDK1.0
     */
    public int intValue() {
	return (int)value;
    }

    /**
     * Returns the long value of this Double (by casting to a long).
     *
     * @return  the <code>double</code> value represented by this object is
     *          converted to type <code>long</code> and the result of the
     *          conversion is returned.
     * @since   JDK1.0
     */
    public long longValue() {
	return (long)value;
    }

    /**
     * Returns the float value of this Double.
     *
     * @return  the <code>double</code> value represented by this object is
     *          converted to type <code>float</code> and the result of the
     *          conversion is returned.
     * @since   JDK1.0     */
    public float floatValue() {
	return (float)value;
    }

    /**
     * Returns the double value of this Double.
     *
     * @return  the <code>double</code> value represented by this object.
     * @since   JDK1.0
     */
    public double doubleValue() {
	return (double)value;
    }

    /**
     * Returns a hashcode for this Double.
     *
     * @return  a <code>hash code</code> value for this object. 
     * @since   JDK1.0
     */
    public int hashCode() {
	long bits = doubleToLongBits(value);
	return (int)(bits ^ (bits >> 32));
    }

    /**
     * Compares this object against the specified object.
     * The result is <code>true</code> if and only if the argument is 
     * not <code>null</code> and is a <code>Double</code> object that 
     * represents a double that has the identical bit pattern to the bit 
     * pattern of the double represented by this object. 
     * <p>
     * Note that in most cases, for two instances of class 
     * <code>Double</code>, <code>d1</code> and <code>d2</code>, the 
     * value of <code>d1.equals(d2)</code> is <code>true</code> if and 
     * only if 
     * <ul><code>
     *   d1.doubleValue()&nbsp;== d2.doubleValue()
     * </code></ul>
     * <p>
     * also has the value <code>true</code>. However, there are two 
     * exceptions: 
     * <ul>
     * <li>If <code>d1</code> and <code>d2</code> both represent 
     *     <code>Double.NaN</code>, then the <code>equals</code> method 
     *     returns <code>true</code>, even though 
     *     <code>Double.NaN==Double.NaN</code> has the value 
     *     <code>false</code>.
     * <li>If <code>d1</code> represents <code>+0.0</code> while
     *     <code>d2</code> represents <code>-0.0</code>, or vice versa,
     *     the <code>equal</code> test has the value <code>false</code>,
     *     even though <code>+0.0==-0.0</code> has the value <code>true.</code>
     * </ul>
     *
     * @param   obj   the object to compare with.
     * @return  <code>true</code> if the objects are the same;
     *          <code>false</code> otherwise.
     * @since   JDK1.0
     */
    public boolean equals(Object obj) {
	return (obj != null)
	       && (obj instanceof Double) 
	       && (doubleToLongBits(((Double)obj).value) == 
		      doubleToLongBits(value));
    }

    /**
     * Returns a representation of the specified floating-point value 
     * according to the IEEE 754 floating-point "double 
     * format" bit layout. 
     * <p>
     * Bit 63 represents the sign of the floating-point number. Bits 
     * 62-52 represent the exponent. Bits 51-0 represent 
     * the significand (sometimes called the mantissa) of the 
     * floating-point number. 
     * <p>
     * If the argument is positive infinity, the result is 
     * <code>0x7ff0000000000000L</code>. 
     * <p>
     * If the argument is negative infinity, the result is 
     * <code>0xfff0000000000000L</code>. 
     * <p>
     * If the argument is NaN, the result is 
     * <code>0x7ff8000000000000L</code>. 
     *
     * @param   value   a double precision floating-point number.
     * @return  the bits that represent the floating-point number.
     * @since   JDK1.0
     */
    public static native long doubleToLongBits(double value);

    /**
     * Returns the double-float corresponding to a given bit represention.
     * The argument is considered to be a representation of a 
     * floating-point value according to the IEEE 754 floating-point 
     * "double precision" bit layout. That floating-point 
     * value is returned as the result. 
     * <p>
     * If the argument is <code>0x7f80000000000000L</code>, the result 
     * is positive infinity. 
     * <p>
     * If the argument is <code>0xff80000000000000L</code>, the result 
     * is negative infinity. 
     * <p>
     * If the argument is any value in the range 
     * <code>0x7ff0000000000001L</code> through 
     * <code>0x7fffffffffffffffL</code> or in the range 
     * <code>0xfff0000000000001L</code> through 
     * <code>0xffffffffffffffffL</code>, the result is NaN. All IEEE 754 
     * NaN values are, in effect, lumped together by the Java language 
     * into a single value. 
     *
     * @param   bits   any <code>long</code> integer.
     * @return  the <code>double</code> floating-point value with the same
     *          bit pattern.
     * @since   JDK1.0
     */
    public static native double longBitsToDouble(long bits);

    /* Converts a string to a double.  Also used by Float.valueOf */
    static native double valueOf0(String s) throws NumberFormatException;

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = -9172774392245257468L;
}
