/*
 * @(#)Float.java	1.40 98/07/01
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
 * The Float class wraps a value of primitive type <code>float</code> in 
 * an object. An object of type <code>Float</code> contains a single 
 * field whose type is <code>float</code>. 
 * <p>
 * In addition, this class provides several methods for converting a 
 * <code>float</code> to a <code>String</code> and a 
 * <code>String</code> to a <code>float</code>, as well as other 
 * constants and methods useful when dealing with a 
 * <code>float</code>. 
 *
 * @author  Lee Boynton
 * @author  Arthur van Hoff
 * @version 1.40, 07/01/98
 * @since   JDK1.0
 */
public final
class Float extends Number {
    /**
     * The positive infinity of type <code>float</code>. 
     *
     * @since   JDK1.0
     */
    public static final float POSITIVE_INFINITY = 1.0f / 0.0f;

    /**
     * The negative infinity of type <code>float</code>. 
     *
     * @since   JDK1.0
     */
    public static final float NEGATIVE_INFINITY = -1.0f / 0.0f;

    /** 
     * The NaN value of type <code>float</code>. 
     *
     * @since   JDK1.0
     */
    public static final float NaN = 0.0f / 0.0f;

    /**
     * The largest positive value of type <code>float</code>. 
     *
     * @since   JDK1.0
     */
    public static final float MAX_VALUE = 3.40282346638528860e+38f;

    /**
     * The smallest positive value of type <code>float</code>. 
     *
     * @since   JDK1.0
     */
    public static final float MIN_VALUE = 1.40129846432481707e-45f;

    /**
     * The Class object representing the primitive type float.
     *
     * @since   JDK1.1
     */
    public static final Class	TYPE = Class.getPrimitiveClass("float");

    /**
     * Returns a String representation for the specified float value.
     * <p>
     * The values <code>NaN</code>, <code>NEGATIVE_INFINITY</code>, 
     * <code>POSITIVE_INFINITY</code>, <code>-0.0</code>, and 
     * <code>+0.0</code> are represented by the strings 
     * <code>"NaN"</code>, <code>"-Infinity"</code>, 
     * <code>"Infinity"</code>,<code> "-0.0"</code>, and 
     * <code>"0.0"</code>, respectively. 
     * <p>
     * If <code>d</code> is in the range 
     * <code>10<sup>-3</sup>&nbsp;&lt;=
     *   |d|&nbsp;&lt;=&nbsp;10<sup>7</sup></code>, 
     * then it is converted to a <code>String</code> in the style 
     * <code>[-]ddd.ddd</code>. Otherwise, it is converted to a 
     * string in the style <code>[-]m.ddddE&#177;xx</code>.
     * <p>
     * There is always a minimum of 1 digit after the decimal point. The 
     * number of digits is the minimum needed to uniquely distinguish the 
     * argument value from adjacent values of type <code>float</code>. 
     *
     * @param   d   the float to be converted.
     * @return  a string representation of the argument.
     * @since   JDK1.0
     */
    public static String toString(float f){
	return new FloatingDecimal(f).toJavaFormatString();
    }

    /**
     * Returns the floating point value represented by the specified String.
     *
     * @param      s   the string to be parsed.
     * @return     a newly constructed <code>Float</code> initialized to the
     *             value represented by the <code>String</code> argument.
     * @exception  NumberFormatException  if the string does not contain a
     *               parsable number.
     * @since   JDK1.0
     */
    public static Float valueOf(String s) throws NumberFormatException { 
	return new Float(Double.valueOf0(s));
    }

    /**
     * Returns true if the specified number is the special Not-a-Number (NaN)
     * value.
     *
     * @param   v   the value to be tested.
     * @return  <code>true</code> if the argument is NaN;
     *          <code>false</code> otherwise.
     * @since   JDK1.0
     */
    static public boolean isNaN(float v) {
	return (v != v);
    }

    /**
     * Returns true if the specified number is infinitely large in magnitude.
     *
     * @param   v   the value to be tested.
     * @return  <code>true</code> if the argument is positive infinity or
     *          negative infinity; <code>false</code> otherwise.
     * @since   JDK1.0
     */
    static public boolean isInfinite(float v) {
	return (v == POSITIVE_INFINITY) || (v == NEGATIVE_INFINITY);
    }

    /**
     * The value of the Float.
     */
    private float value;

    /**
     * Constructs a newly allocated <code>Float</code> object that 
     * represents the primitive <code>float</code> argument. 
     *
     * @param   value   the value to be represented by the <code>Float</code>.
     * @since   JDK1.0
     */
    public Float(float value) {
	this.value = value;
    }

    /**
     * Constructs a newly allocated <code>Float</code>object that 
     * represents the argument converted to type <code>float</code>.
     *
     * @param   value   the value to be represented by the <code>Float</code>.
     * @since   JDK1.0
     */
    public Float(double value) {
	this.value = (float)value;
    }

    /**
     * Constructs a newly allocated <code>Float</code> object that 
     * represents the floating- point value of type <code>float</code> 
     * represented by the string. The string is converted to a 
     * <code>float</code> value as if by the <code>valueOf</code> method. 
     *
     * @param      s   a string to be converted to a <code>Float</code>.
     * @exception  NumberFormatException  if the string does not contain a
     *               parsable number.
     * @see        java.lang.Float#valueOf(java.lang.String)
     * @since      JDK1.0
     */
    public Float(String s) throws NumberFormatException {
	// REMIND: this is inefficient
	this(valueOf(s).floatValue());
    }

    /**
     * Returns true if this Float value is Not-a-Number (NaN).
     *
     * @return  <code>true</code> if the value represented by this object is
     *          NaN; <code>false</code> otherwise.
     * @since   JDK1.0
     */
    public boolean isNaN() {
	return isNaN(value);
    }

    /**
     * Returns true if this Float value is infinitely large in magnitude.
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
     * Returns a String representation of this Float object.
     * The primitive <code>float</code> value represented by this object 
     * is converted to a <code>String</code> exactly as if by the method 
     * <code>toString</code> of one argument. 
     *
     * @return  a <code>String</code> representation of this object.
     * @see     java.lang.Float#toString(float)
     * @since   JDK1.0
     */
    public String toString() {
	return String.valueOf(value);
    }

    /**
     * Returns the value of this Float as a byte (by casting to a byte).
     *
     * @since   JDK1.1
     */
    public byte byteValue() {
	return (byte)value;
    }

    /**
     * Returns the value of this Float as a short (by casting to a short).
     *
     * @since   JDK1.1
     */
    public short shortValue() {
	return (short)value;
    }

    /**
     * Returns the integer value of this Float (by casting to an int).
     *
     * @return  the <code>float</code> value represented by this object
     *          converted to type <code>int</code> and the result of the
     *          conversion is returned.
     * @since   JDK1.0
     */
    public int intValue() {
	return (int)value;
    }

    /**
     * Returns the long value of this Float (by casting to a long).
     *
     * @return  the <code>float</code> value represented by this object is
     *          converted to type <code>long</code> and the result of the
     *          conversion is returned.
     * @since   JDK1.0
     */
    public long longValue() {
	return (long)value;
    }

    /**
     * Returns the float value of this Float object.
     *
     * @return  the <code>float</code> value represented by this object.
     * @since   JDK1.0
     */
    public float floatValue() {
	return value;
    }

    /**
     * Returns the double value of this Float.
     *
     * @since   JDK1.0
     */
    public double doubleValue() {
	return (double)value;
    }

    /**
     * Returns a hashcode for this Float.
     *
     * @return  a hash code value for this object. 
     * @since   JDK1.0
     */
    public int hashCode() {
	return floatToIntBits(value);
    }

    /**
     * Compares this object against some other object.
     * The result is <code>true</code> if and only if the argument is 
     * not <code>null</code> and is a <code>Float</code> object that 
     * represents a <code>float</code> that has the identical bit pattern 
     * to the bit pattern of the <code>float</code> represented by this 
     * object. 
     * <p>
     * Note that in most cases, for two instances of class 
     * <code>Float</code>, <code>f1</code> and <code>f2</code>, the value 
     * of <code>f1.equals(f2)</code> is <code>true</code> if and only if 
     * <ul><code>
     *   f1.floatValue()&nbsp;== f2.floatValue()
     * </code></ul>
     * <p>
     * also has the value <code>true</code>. However, there are two exceptions:
     * <ul>
     * <li>If <code>f1</code> and <code>f2</code> both represent 
     *     <code>Float.NaN</code>, then the <code>equals</code> method returns 
     *     <code>true</code>, even though <code>Float.NaN==Float.NaN</code> 
     *     has the value <code>false</code>.
     * <li>If <code>f1</code> represents <code>+0.0f</code> while
     *     <code>f2</code> represents <code>-0.0f</code>, or vice versa,
     *     the <code>equal</code> test has the value <code>false</code>,
     *     even though <code>0.0f==-0.0f</code> has the value <code>true</code>.
     * </ul>
     *
     * @return  <code>true</code> if the objects are the same;
     *          <code>false</code> otherwise.
     * @see     java.lang.Float#floatToIntBits(float)
     * @since   JDK1.0
     */
    public boolean equals(Object obj) {
	return (obj != null)
	       && (obj instanceof Float) 
	       && (floatToIntBits(((Float)obj).value) == floatToIntBits(value));
    }

    /**
     * Returns the bit represention of a single-float value.
     * The result is a representation of the floating-point argument 
     * according to the IEEE 754 floating-point "single 
     * precision" bit layout. 
     * <p>
     * Bit 31 represents the sign of the floating-point number. Bits 
     * 30-23 represent the exponent. Bits 22-0 represent 
     * the significand (sometimes called the mantissa) of the 
     * floating-point number. 
     * <p>
     * If the argument is positive infinity, the result is 
     * <code>0x7f800000</code>. 
     * <p>
     * If the argument is negative infinity, the result is 
     * <code>0xff800000</code>. 
     * <p>
     * If the argument is NaN, the result is <code>0x7fc00000</code>. 
     *
     * @param   value   a floating-point number.
     * @return  the bits that represent the floating-point number.
     * @since   JDK1.0
     */
    public static native int floatToIntBits(float value);

    /**
     * Returns the single-float corresponding to a given bit represention.
     * The argument is considered to be a representation of a 
     * floating-point value according to the IEEE 754 floating-point 
     * "single precision" bit layout.
     * <p>
     * If the argument is <code>0x7f800000</code>, the result is positive 
     * infinity. 
     * <p>
     * If the argument is <code>0xff800000</code>, the result is negative 
     * infinity. 
     * <p>
     * If the argument is any value in the range <code>0x7f800001</code> 
     * through <code>0x7f8fffff</code> or in the range 
     * <code>0xff800001</code> through <code>0xff8fffff</code>, the result is 
     * NaN. All IEEE 754 NaN values are, in effect, lumped together by 
     * the Java language into a single value. 
     *
     * @param   bits   an integer.
     * @return  the single-format floating-point value with the same bit
     *          pattern.
     * @since   JDK1.0
     */
    public static native float intBitsToFloat(int bits);

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = -2671257302660747028L;
}
