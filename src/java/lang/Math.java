/*
 * @(#)Math.java	1.34 98/07/23
 *
 * Copyright 1994-1998 by Sun Microsystems, Inc.,
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
import java.util.Random;


/**
 * The class <code>Math</code> contains methods for performing basic 
 * numeric operations such as the elementary exponential, logarithm, 
 * square root, and trigonometric functions. 
 * <p>
 * To help ensure portability of Java programs, the definitions of 
 * many of the numeric functions in this package require that they 
 * produce the same results as certain published algorithms. These 
 * algorithms are available from the well-known network library 
 * <code>netlib</code> as the package "Freely Distributable 
 * Math Library" (<code>fdlibm</code>). These algorithms, which 
 * are written in the C programming language, are then to be 
 * understood as executed with all floating-point operations 
 * following the rules of Java floating-point arithmetic. 
 * <p>
 * The network library may be found on the World Wide Web at:
 * <blockquote><pre>
 *   http://sunsite.unc.edu/
 * </pre></blockquote>
 * <p>
 * The Java math library is defined with respect to the version of 
 * <code>fdlibm</code> dated January 4, 1995. Where 
 * <code>fdlibm</code> provides more than one definition for a 
 * function (such as <code>acos</code>), use the "IEEE 754 core 
 * function" version (residing in a file whose name begins with 
 * the letter <code>e</code>). 
 *
 * @author  unascribed
 * @version 1.34, 07/23/98
 * @since   JDK1.0
 */

public final class Math {

    /**
     * Don't let anyone instantiate this class.
     */
    private Math() {}

    /**
     * The <code>double</code> value that is closer than any other to 
     * <code>e</code>, the base of the natural logarithms. 
     */
    public static final double E = 2.7182818284590452354;

    /**
     * The <code>double</code> value that is closer than any other to 
     * <i>pi</i>, the ratio of the circumference of a circle to its diameter. 
     */
    public static final double PI = 3.14159265358979323846;

    /**
     * Returns the trigonometric sine of an angle.
     *
     * @param   a   an angle, in radians.
     * @return  the sine of the argument.
     */
    public static native double sin(double a);
    
    /**
     * Returns the trigonometric cosine of an angle.
     *
     * @param   a   an angle, in radians.
     * @return  the cosine of the argument.
     */
    public static native double cos(double a);
   
    /**
     * Returns the trigonometric tangent of an angle.
     *
     * @param   a   an angle, in radians.
     * @return  the tangent of the argument.
     */
    public static native double tan(double a);

    /**
     * Returns the arc sine of an angle, in the range of -<i>pi</i>/2 through
     * <i>pi</i>/2.
     *
     * @param   a   the <code>double</code> value whose arc sine is to 
     *              be returned.
     * @return  the arc sine of the argument.
     */
    public static native double asin(double a);

    /**
     * Returns the arc cosine of an angle, in the range of 0.0 through
     * <i>pi</i>.
     *
     * @param   a   the <code>double</code> value whose arc cosine is to 
     *              be returned.
     * @return  the arc cosine of the argument.
     */
    public static native double acos(double a); 

    /**
     * Returns the arc tangent of an angle, in the range of -<i>pi</i>/2
     * through <i>pi</i>/2.
     *
     * @param   a   the <code>double</code> value whose arc tangent is to 
     *              be returned.
     * @return  the arc tangent of the argument.
     */
    public static native double atan(double a);

    /**
     * Converts an angle measured in degrees to the equivalent angle
     * measured in radians.
     *
     * @param   angdeg   an angle, in degrees
     * @return  the measurement of the angle <code>angdeg</code>
     *          in radians.
     * @since   JDK1.2
     */
    public static double toRadians(double angdeg) {
	return angdeg / 180.0 * PI;
    }

    /**
     * Converts an angle measured in radians to the equivalent angle
     * measured in degrees.
     *
     * @param   angrad   an angle, in radians
     * @return  the measurement of the angle <code>angrad</code>
     *          in degrees.
     * @since   JDK1.2
     */
    public static double toDegrees(double angrad) {
	return angrad * 180.0 / PI;
    }

    /**
     * Returns the exponential number <i>e</i> (i.e., 2.718...) raised to
     * the power of a <code>double</code> value.
     *
     * @param   a   a <code>double</code> value.
     * @return  the value <i>e</i><sup>a</sup>, where <i>e</i> is the base of
     *          the natural logarithms.
     */
    public static native double exp(double a);

    /**
     * Returns the natural logarithm (base <i>e</i>) of a <code>double</code>
     * value.
     *
     * @param   a   a number greater than <code>0.0</code>.
     * @return  the value ln&nbsp;<code>a</code>, the natural logarithm of
     *          <code>a</code>.
     */
    public static native double log(double a);

    /**
     * Returns the square root of a <code>double</code> value.
     *
     * @param   a   a <code>double</code> value.
     * <!--@return  the value of &radic;&nbsp;<code>a</code>.-->
     * @return  the square root of <code>a</code>.
     *          If the argument is NaN or less than zero, the result is NaN.
     */
    public static native double sqrt(double a);

    /**
     * Computes the remainder operation on two arguments as prescribed 
     * by the IEEE 754 standard.
     * The remainder value is mathematically equal to 
     * <code>f1&nbsp;-&nbsp;f2</code>&nbsp;&times;&nbsp;<i>n</i>,
     * where <i>n</i> is the mathematical integer closest to the exact 
     * mathematical value of the quotient <code>f1/f2</code>, and if two 
     * mathematical integers are equally close to <code>f1/f2</code>, 
     * then <i>n</i> is the integer that is even. If the remainder is 
     * zero, its sign is the same as the sign of the first argument. 
     *
     * @param   f1   the dividend.
     * @param   f2   the divisor.
     * @return  the remainder when <code>f1</code> is divided by
     *          <code>f2</code>.
     */
    public static native double IEEEremainder(double f1, double f2);

    /**
     * Returns the smallest (closest to negative infinity) 
     * <code>double</code> value that is not less than the argument and is 
     * equal to a mathematical integer. 
     *
     * @param   a   a <code>double</code> value.
     * <!--@return  the value &lceil;&nbsp;<code>a</code>&nbsp;&rceil;.-->
     * @return  the smallest (closest to negative infinity) 
     *          <code>double</code> value that is not less than the argument
     *          and is equal to a mathematical integer. 
     */
    public static native double ceil(double a);

    /**
     * Returns the largest (closest to positive infinity) 
     * <code>double</code> value that is not greater than the argument and 
     * is equal to a mathematical integer. 
     *
     * @param   a   a <code>double</code> value.
     * @param   a   an assigned value.
     * <!--@return  the value &lfloor;&nbsp;<code>a</code>&nbsp;&rfloor;.-->
     * @return  the largest (closest to positive infinity) 
     *          <code>double</code> value that is not greater than the argument
     *          and is equal to a mathematical integer. 
     */
    public static native double floor(double a);

    /**
     * returns the closest integer to the argument. 
     *
     * @param   a   a <code>double</code> value.
     * @return  the closest <code>double</code> value to <code>a</code> that is
     *          equal to a mathematical integer. If two <code>double</code>
     *          values that are mathematical integers are equally close to the
     *          value of the argument, the result is the integer value that
     *          is even.
     */
    public static native double rint(double a);

    /**
     * Converts rectangular coordinates (<code>b</code>,&nbsp;<code>a</code>)
     * to polar (r,&nbsp;<i>theta</i>).
     * This method computes the phase <i>theta</i> by computing an arc tangent
     * of <code>a/b</code> in the range of -<i>pi</i> to <i>pi</i>.
     *
     * @param   a   a <code>double</code> value.
     * @param   b   a <code>double</code> value.
     * @return  the <i>theta</i> component of the point
     *          (<i>r</i>,&nbsp;<i>theta</i>)
     *          in polar coordinates that corresponds to the point
     *          (<i>b</i>,&nbsp;<i>a</i>) in Cartesian coordinates.
     */
    public static native double atan2(double a, double b);


    /**
     * Returns of value of the first argument raised to the power of the
     * second argument.
     * <p>
     * If (<code>a&nbsp;==&nbsp;0.0</code>), then <code>b</code> must be
     * greater than <code>0.0</code>; otherwise an exception is thrown. 
     * An exception also will occur if (<code>a&nbsp;&lt;=&nbsp;0.0</code>)
     * and <code>b</code> is not equal to a whole number.
     *
     * @param   a   a <code>double</code> value.
     * @param   b   a <code>double</code> value.
     * @return  the value <code>a<sup>b</sup></code>.
     * @exception ArithmeticException  if (<code>a&nbsp;==&nbsp;0.0</code>) and
     *              (<code>b&nbsp;&lt;=&nbsp;0.0</code>), or
     *              if (<code>a&nbsp;&lt;=&nbsp;0.0</code>) and <code>b</code>
     *              is not equal to a whole number.
     */
    public static native double pow(double a, double b);

    /**
     * Returns the closest <code>int</code> to the argument. 
     * <p>
     * If the argument is negative infinity or any value less than or 
     * equal to the value of <code>Integer.MIN_VALUE</code>, the result is 
     * equal to the value of <code>Integer.MIN_VALUE</code>. 
     * <p>
     * If the argument is positive infinity or any value greater than or 
     * equal to the value of <code>Integer.MAX_VALUE</code>, the result is 
     * equal to the value of <code>Integer.MAX_VALUE</code>. 
     *
     * @param   a   a <code>float</code> value.
     * @return  the value of the argument rounded to the nearest
     *          <code>int</code> value.
     * @see     java.lang.Integer#MAX_VALUE
     * @see     java.lang.Integer#MIN_VALUE
     */
    public static int round(float a) {
	return (int)floor(a + 0.5f);
    }

    /**
     * Returns the closest <code>long</code> to the argument. 
     * <p>
     * If the argument is negative infinity or any value less than or 
     * equal to the value of <code>Long.MIN_VALUE</code>, the result is 
     * equal to the value of <code>Long.MIN_VALUE</code>. 
     * <p>
     * If the argument is positive infinity or any value greater than or 
     * equal to the value of <code>Long.MAX_VALUE</code>, the result is 
     * equal to the value of <code>Long.MAX_VALUE</code>. 
     *
     * @param   a   a <code>double</code> value.
     * @return  the value of the argument rounded to the nearest
     *          <code>long</code> value.
     * @see     java.lang.Long#MAX_VALUE
     * @see     java.lang.Long#MIN_VALUE
     */
    public static long round(double a) {
	return (long)floor(a + 0.5d);
    }

    private static Random randomNumberGenerator;

    /**
     * Returns a random number greater than or equal to <code>0.0</code> 
     * and less than <code>1.0</code>. Returned values are chosen 
     * pseudorandomly with (approximately) uniform distribution from that 
     * range. 
     * <p>
     * When this method is first called, it creates a single new 
     * pseudorandom-number generator, exactly as if by the expression 
     * <blockquote><pre>new java.util.Random</pre></blockquote>
     * This new pseudorandom-number generator is used thereafter for all 
     * calls to this method and is used nowhere else. 
     * <p>
     * This method is properly synchronized to allow correct use by more 
     * than one thread. However, if many threads need to generate 
     * pseudorandom numbers at a great rate, it may reduce contention for 
     * each thread to have its own pseudorandom-number generator.
     *  
     * @return  a pseudorandom <code>double</code> greater than or equal 
     * to <code>0.0</code> and less than <code>1.0</code>.
     * @see     java.util.Random#nextDouble()
     */
    public static synchronized double random() {
        if (randomNumberGenerator == null)
            randomNumberGenerator = new Random();
        return randomNumberGenerator.nextDouble();
    }

    /**
     * Returns the absolute value of an <code>int</code> value.
     * If the argument is not negative, the argument is returned.
     * If the argument is negative, the negation of the argument is returned. 
     * <p>
     * Note that if the argument is equal to the value of 
     * <code>Integer.MIN_VALUE</code>, the most negative representable 
     * <code>int</code> value, the result is that same value, which is 
     * negative. 
     *
     * @param   a   an <code>int</code> value.
     * @return  the absolute value of the argument.
     * @see     java.lang.Integer#MIN_VALUE
     */
    public static int abs(int a) {
	return (a < 0) ? -a : a;
    }

    /**
     * Returns the absolute value of a <code>long</code> value.
     * If the argument is not negative, the argument is returned.
     * If the argument is negative, the negation of the argument is returned. 
     * <p>
     * Note that if the argument is equal to the value of 
     * <code>Long.MIN_VALUE</code>, the most negative representable 
     * <code>long</code> value, the result is that same value, which is 
     * negative. 
     *
     * @param   a   a <code>long</code> value.
     * @return  the absolute value of the argument.
     * @see     java.lang.Long#MIN_VALUE
     */
    public static long abs(long a) {
	return (a < 0) ? -a : a;
    }

    /**
     * Returns the absolute value of a <code>float</code> value.
     * If the argument is not negative, the argument is returned.
     * If the argument is negative, the negation of the argument is returned. 
     *
     * @param   a   a <code>float</code> value.
     * @return  the absolute value of the argument.
     */
    public static float abs(float a) {
        return (a <= 0.0F) ? 0.0F - a : a;
    }
  
    /**
     * Returns the absolute value of a <code>double</code> value.
     * If the argument is not negative, the argument is returned.
     * If the argument is negative, the negation of the argument is returned. 
     *
     * @param   a   a <code>double</code> value.
     * @return  the absolute value of the argument.
     */
    public static double abs(double a) {
        return (a <= 0.0D) ? 0.0D - a : a;
    }

    /**
     * Returns the greater of two <code>int</code> values.
     *
     * @param   a   an <code>int</code> value.
     * @param   b   an <code>int</code> value.
     * @return  the larger of <code>a</code> and <code>b</code>.
     */
    public static int max(int a, int b) {
	return (a >= b) ? a : b;
    }

    /**
     * Returns the greater of two <code>long</code> values.
     *
     * @param   a   a <code>long</code> value.
     * @param   b   a <code>long</code> value.
     * @return  the larger of <code>a</code> and <code>b</code>.
     */
    public static long max(long a, long b) {
	return (a >= b) ? a : b;
    }

    private static long negativeZeroFloatBits = Float.floatToIntBits(-0.0f);
    private static long negativeZeroDoubleBits = Double.doubleToLongBits(-0.0d);

    /**
     * Returns the greater of two <code>float</code> values.  If either value
     * is <code>NaN</code>, then the result is <code>NaN</code>.  Unlike the
     * the numerical comparison operators, this method considers negative zero
     * to be strictly smaller than positive zero.
     *
     * @param   a   a <code>float</code> value.
     * @param   b   a <code>float</code> value.
     * @return  the larger of <code>a</code> and <code>b</code>.
     */
    public static float max(float a, float b) {
        if (a != a) return a;	// a is NaN
	if ((a == 0.0f) && (b == 0.0f)
	    && (Float.floatToIntBits(a) == negativeZeroFloatBits)) {
	    return b;
	}
	return (a >= b) ? a : b;
    }

    /**
     * Returns the greater of two <code>double</code> values.  If either value
     * is <code>NaN</code>, then the result is <code>NaN</code>.  Unlike the
     * the numerical comparison operators, this method considers negative zero
     * to be strictly smaller than positive zero.
     *
     * @param   a   a <code>double</code> value.
     * @param   b   a <code>double</code> value.
     * @return  the larger of <code>a</code> and <code>b</code>.
     */
    public static double max(double a, double b) {
        if (a != a) return a;	// a is NaN
	if ((a == 0.0d) && (b == 0.0d)
	    && (Double.doubleToLongBits(a) == negativeZeroDoubleBits)) {
	    return b;
	}
	return (a >= b) ? a : b;
    }

    /**
     * Returns the smaller of two <code>int</code> values.
     *
     * @param   a   an <code>int</code> value.
     * @param   b   an <code>int</code> value.
     * @return  the smaller of <code>a</code> and <code>b</code>.
     */
    public static int min(int a, int b) {
	return (a <= b) ? a : b;
    }

    /**
     * Returns the smaller of two <code>long</code> values.
     *
     * @param   a   a <code>long</code> value.
     * @param   b   a <code>long</code> value.
     * @return  the smaller of <code>a</code> and <code>b</code>.
     */
    public static long min(long a, long b) {
	return (a <= b) ? a : b;
    }

    /**
     * Returns the smaller of two <code>float</code> values.  If either value
     * is <code>NaN</code>, then the result is <code>NaN</code>.  Unlike the
     * the numerical comparison operators, this method considers negative zero
     * to be strictly smaller than positive zero.
     *
     * @param   a   a <code>float</code> value.
     * @param   b   a <code>float</code> value.
     * @return  the smaller of <code>a</code> and <code>b.</code>
     */
    public static float min(float a, float b) {
        if (a != a) return a;	// a is NaN
	if ((a == 0.0f) && (b == 0.0f)
	    && (Float.floatToIntBits(b) == negativeZeroFloatBits)) {
	    return b;
	}
	return (a <= b) ? a : b;
    }

    /**
     * Returns the smaller of two <code>double</code> values.  If either value
     * is <code>NaN</code>, then the result is <code>NaN</code>.  Unlike the
     * the numerical comparison operators, this method considers negative zero
     * to be strictly smaller than positive zero.
     *
     * @param   a   a <code>double</code> value.
     * @param   b   a <code>double</code> value.
     * @return  the smaller of <code>a</code> and <code>b</code>.
     */
    public static double min(double a, double b) {
        if (a != a) return a;	// a is NaN
	if ((a == 0.0d) && (b == 0.0d)
	    && (Double.doubleToLongBits(b) == negativeZeroDoubleBits)) {
	    return b;
	}
	return (a <= b) ? a : b;
    }

}
