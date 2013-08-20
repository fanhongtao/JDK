/*
 * @(#)StrictMath.java	1.26 04/06/14
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;
import java.util.Random;
import sun.misc.FpUtils;

/**
 * The class <code>StrictMath</code> contains methods for performing basic 
 * numeric operations such as the elementary exponential, logarithm, 
 * square root, and trigonometric functions. 
 * 
 * <p>To help ensure portability of Java programs, the definitions of
 * some of the numeric functions in this package require that they
 * produce the same results as certain published algorithms. These
 * algorithms are available from the well-known network library
 * <code>netlib</code> as the package "Freely Distributable Math
 * Library," <a
 * href="ftp://ftp.netlib.org/fdlibm.tar"><code>fdlibm</code></a>. These
 * algorithms, which are written in the C programming language, are
 * then to be understood as executed with all floating-point
 * operations following the rules of Java floating-point arithmetic.
 * 
 * <p>The Java math library is defined with respect to
 * <code>fdlibm</code> version 5.3. Where <code>fdlibm</code> provides
 * more than one definition for a function (such as
 * <code>acos</code>), use the "IEEE 754 core function" version
 * (residing in a file whose name begins with the letter
 * <code>e</code>).  The methods which require <code>fdlibm</code>
 * semantics are <code>sin</code>, <code>cos</code>, <code>tan</code>,
 * <code>asin</code>, <code>acos</code>, <code>atan</code>,
 * <code>exp</code>, <code>log</code>, <code>log10</code>,
 * <code>cbrt</code>, <code>atan2</code>, <code>pow</code>,
 * <code>sinh</code>, <code>cosh</code>, <code>tanh</code>,
 * <code>hypot</code>, <code>expm1</code>, and <code>log1p</code>.
 *
 * @author  unascribed
 * @author  Joseph D. Darcy
 * @version 1.26, 06/14/04
 * @since   1.3
 */

public final class StrictMath {

    /**
     * Don't let anyone instantiate this class.
     */
    private StrictMath() {}

    /**
     * The <code>double</code> value that is closer than any other to
     * <i>e</i>, the base of the natural logarithms.
     */
    public static final double E = 2.7182818284590452354;

    /**
     * The <code>double</code> value that is closer than any other to
     * <i>pi</i>, the ratio of the circumference of a circle to its
     * diameter.
     */
    public static final double PI = 3.14159265358979323846;

    /**
     * Returns the trigonometric sine of an angle. Special cases:
     * <ul><li>If the argument is NaN or an infinity, then the 
     * result is NaN.
     * <li>If the argument is zero, then the result is a zero with the
     * same sign as the argument.</ul>
     *
     * @param   a   an angle, in radians.
     * @return  the sine of the argument.
     */
    public static native double sin(double a);
    
    /**
     * Returns the trigonometric cosine of an angle. Special cases:
     * <ul><li>If the argument is NaN or an infinity, then the 
     * result is NaN.</ul>
     *
     * @param   a   an angle, in radians.
     * @return  the cosine of the argument.
     */
    public static native double cos(double a);
   
    /**
     * Returns the trigonometric tangent of an angle. Special cases:
     * <ul><li>If the argument is NaN or an infinity, then the result 
     * is NaN.
     * <li>If the argument is zero, then the result is a zero with the
     * same sign as the argument.</ul>
     *
     * @param   a   an angle, in radians.
     * @return  the tangent of the argument.
     */
    public static native double tan(double a);

    /**
     * Returns the arc sine of an angle, in the range of -<i>pi</i>/2 through
     * <i>pi</i>/2. Special cases: 
     * <ul><li>If the argument is NaN or its absolute value is greater 
     * than 1, then the result is NaN.
     * <li>If the argument is zero, then the result is a zero with the
     * same sign as the argument.</ul>
     *
     * @param   a   the value whose arc sine is to be returned.
     * @return  the arc sine of the argument.
     */
    public static native double asin(double a);

    /**
     * Returns the arc cosine of an angle, in the range of 0.0 through
     * <i>pi</i>. Special case:
     * <ul><li>If the argument is NaN or its absolute value is greater 
     * than 1, then the result is NaN.</ul>
     *
     * @param   a   the value whose arc cosine is to be returned.
     * @return  the arc cosine of the argument.
     */
    public static native double acos(double a); 

    /**
     * Returns the arc tangent of an angle, in the range of -<i>pi</i>/2
     * through <i>pi</i>/2. Special cases: 
     * <ul><li>If the argument is NaN, then the result is NaN.
     * <li>If the argument is zero, then the result is a zero with the
     * same sign as the argument.</ul>
     *
     * @param   a   the value whose arc tangent is to be returned.
     * @return  the arc tangent of the argument.
     */
    public static native double atan(double a);

    /**
     * Converts an angle measured in degrees to an approximately
     * equivalent angle measured in radians.  The conversion from
     * degrees to radians is generally inexact.
     *
     * @param   angdeg   an angle, in degrees
     * @return  the measurement of the angle <code>angdeg</code>
     *          in radians.
     */
    public static strictfp double toRadians(double angdeg) {
	return angdeg / 180.0 * PI;
    }

    /**
     * Converts an angle measured in radians to an approximately
     * equivalent angle measured in degrees.  The conversion from
     * radians to degrees is generally inexact; users should
     * <i>not</i> expect <code>cos(toRadians(90.0))</code> to exactly
     * equal <code>0.0</code>.
     *
     * @param   angrad   an angle, in radians
     * @return  the measurement of the angle <code>angrad</code>
     *          in degrees.
     */
    public static strictfp double toDegrees(double angrad) {
	return angrad * 180.0 / PI;
    }

    /**
     * Returns Euler's number <i>e</i> raised to the power of a
     * <code>double</code> value. Special cases:
     * <ul><li>If the argument is NaN, the result is NaN.
     * <li>If the argument is positive infinity, then the result is 
     * positive infinity.
     * <li>If the argument is negative infinity, then the result is 
     * positive zero.</ul>
     *
     * @param   a   the exponent to raise <i>e</i> to.
     * @return  the value <i>e</i><sup><code>a</code></sup>, 
     *		where <i>e</i> is the base of the natural logarithms.
     */
    public static native double exp(double a);

    /**
     * Returns the natural logarithm (base <i>e</i>) of a <code>double</code>
     * value. Special cases:
     * <ul><li>If the argument is NaN or less than zero, then the result 
     * is NaN.
     * <li>If the argument is positive infinity, then the result is 
     * positive infinity.
     * <li>If the argument is positive zero or negative zero, then the 
     * result is negative infinity.</ul>
     *
     * @param   a   a value
     * @return  the value ln&nbsp;<code>a</code>, the natural logarithm of
     *          <code>a</code>.
     */
    public static native double log(double a);


    /**
     * Returns the base 10 logarithm of a <code>double</code> value.
     * Special cases:
     *
     * <ul><li>If the argument is NaN or less than zero, then the result 
     * is NaN.
     * <li>If the argument is positive infinity, then the result is 
     * positive infinity.
     * <li>If the argument is positive zero or negative zero, then the 
     * result is negative infinity.
     * <li> If the argument is equal to 10<sup><i>n</i></sup> for
     * integer <i>n</i>, then the result is <i>n</i>.
     * </ul>
     *
     * @param   a   a value
     * @return  the base 10 logarithm of  <code>a</code>.
     * @since 1.5
     */
    public static native double log10(double a);

    /**
     * Returns the correctly rounded positive square root of a
     * <code>double</code> value.
     * Special cases:
     * <ul><li>If the argument is NaN or less than zero, then the result 
     * is NaN. 
     * <li>If the argument is positive infinity, then the result is positive 
     * infinity. 
     * <li>If the argument is positive zero or negative zero, then the 
     * result is the same as the argument.</ul>
     * Otherwise, the result is the <code>double</code> value closest to 
     * the true mathematical square root of the argument value.
     *
     * @param   a   a value.
     * @return  the positive square root of <code>a</code>.
     */
    public static native double sqrt(double a);

    /**
     * Returns the cube root of a <code>double</code> value.  For
     * positive finite <code>x</code>, <code>cbrt(-x) ==
     * -cbrt(x)</code>; that is, the cube root of a negative value is
     * the negative of the cube root of that value's magnitude.
     * Special cases: 
     *
     * <ul>
     *
     * <li>If the argument is NaN, then the result is NaN.
     *
     * <li>If the argument is infinite, then the result is an infinity
     * with the same sign as the argument.
     *
     * <li>If the argument is zero, then the result is a zero with the
     * same sign as the argument.
     * 
     * </ul>
     *
     * @param   a   a value.
     * @return  the cube root of <code>a</code>.
     * @since 1.5
     */
    public static native double cbrt(double a);

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
     * Special cases:
     * <ul><li>If either argument is NaN, or the first argument is infinite, 
     * or the second argument is positive zero or negative zero, then the 
     * result is NaN.
     * <li>If the first argument is finite and the second argument is 
     * infinite, then the result is the same as the first argument.</ul>
     *
     * @param   f1   the dividend.
     * @param   f2   the divisor.
     * @return  the remainder when <code>f1</code> is divided by
     *          <code>f2</code>.
     */
    public static native double IEEEremainder(double f1, double f2);

    /**
     * Returns the smallest (closest to negative infinity)
     * <code>double</code> value that is greater than or equal to the
     * argument and is equal to a mathematical integer. Special cases:
     * <ul><li>If the argument value is already equal to a
     * mathematical integer, then the result is the same as the
     * argument.  <li>If the argument is NaN or an infinity or
     * positive zero or negative zero, then the result is the same as
     * the argument.  <li>If the argument value is less than zero but
     * greater than -1.0, then the result is negative zero.</ul> Note
     * that the value of <code>StrictMath.ceil(x)</code> is exactly the
     * value of <code>-StrictMath.floor(-x)</code>.
     *
     * @param   a   a value.
     * @return  the smallest (closest to negative infinity) 
     *          floating-point value that is greater than or equal to
     *          the argument and is equal to a mathematical integer. 
     */
    public static native double ceil(double a);

    /**
     * Returns the largest (closest to positive infinity)
     * <code>double</code> value that is less than or equal to the
     * argument and is equal to a mathematical integer. Special cases:
     * <ul><li>If the argument value is already equal to a
     * mathematical integer, then the result is the same as the
     * argument.  <li>If the argument is NaN or an infinity or
     * positive zero or negative zero, then the result is the same as
     * the argument.</ul>
     *
     * @param   a   a value.
     * @return  the largest (closest to positive infinity) 
     *          floating-point value that less than or equal to the argument
     *          and is equal to a mathematical integer. 
     */
    public static native double floor(double a);

    /**
     * Returns the <code>double</code> value that is closest in value
     * to the argument and is equal to a mathematical integer. If two
     * <code>double</code> values that are mathematical integers are
     * equally close to the value of the argument, the result is the
     * integer value that is even. Special cases:
     * <ul><li>If the argument value is already equal to a mathematical 
     * integer, then the result is the same as the argument. 
     * <li>If the argument is NaN or an infinity or positive zero or negative 
     * zero, then the result is the same as the argument.</ul>
     *
     * @param   a   a value.
     * @return  the closest floating-point value to <code>a</code> that is
     *          equal to a mathematical integer.
     * @author Joseph D. Darcy
     */
    public static double rint(double a) {
	/*
	 * If the absolute value of a is not less than 2^52, it
	 * is either a finite integer (the double format does not have
	 * enough significand bits for a number that large to have any
	 * fractional portion), an infinity, or a NaN.  In any of
	 * these cases, rint of the argument is the argument.
	 *
	 * Otherwise, the sum (twoToThe52 + a ) will properly round
	 * away any fractional portion of a since ulp(twoToThe52) ==
	 * 1.0; subtracting out twoToThe52 from this sum will then be
	 * exact and leave the rounded integer portion of a.
	 *
	 * This method does *not* need to be declared strictfp to get
	 * fully reproducible results.  Whether or not a method is
	 * declared strictfp can only make a difference in the
	 * returned result if some operation would overflow or
	 * underflow with strictfp semantics.  The operation
	 * (twoToThe52 + a ) cannot overflow since large values of a
	 * are screened out; the add cannot underflow since twoToThe52
	 * is too large.  The subtraction ((twoToThe52 + a ) -
	 * twoToThe52) will be exact as discussed above and thus
	 * cannot overflow or meaningfully underflow.  Finally, the
	 * last multiply in the return statement is by plus or minus
	 * 1.0, which is exact too.
	 */
	double twoToThe52 = (double)(1L << 52); // 2^52
	double sign = FpUtils.rawCopySign(1.0, a); // preserve sign info
	a = Math.abs(a);
	
	if (a < twoToThe52) { // E_min <= ilogb(a) <= 51
	    a = ((twoToThe52 + a ) - twoToThe52);
	} 
	
	return sign * a; // restore original sign
    }

    /**
     * Converts rectangular coordinates (<code>x</code>,&nbsp;<code>y</code>)
     * to polar (r,&nbsp;<i>theta</i>).
     * This method computes the phase <i>theta</i> by computing an arc tangent
     * of <code>y/x</code> in the range of -<i>pi</i> to <i>pi</i>. Special 
     * cases:
     * <ul><li>If either argument is NaN, then the result is NaN. 
     * <li>If the first argument is positive zero and the second argument 
     * is positive, or the first argument is positive and finite and the 
     * second argument is positive infinity, then the result is positive 
     * zero. 
     * <li>If the first argument is negative zero and the second argument 
     * is positive, or the first argument is negative and finite and the 
     * second argument is positive infinity, then the result is negative zero. 
     * <li>If the first argument is positive zero and the second argument 
     * is negative, or the first argument is positive and finite and the 
     * second argument is negative infinity, then the result is the 
     * <code>double</code> value closest to <i>pi</i>. 
     * <li>If the first argument is negative zero and the second argument 
     * is negative, or the first argument is negative and finite and the 
     * second argument is negative infinity, then the result is the 
     * <code>double</code> value closest to -<i>pi</i>. 
     * <li>If the first argument is positive and the second argument is 
     * positive zero or negative zero, or the first argument is positive 
     * infinity and the second argument is finite, then the result is the 
     * <code>double</code> value closest to <i>pi</i>/2. 
     * <li>If the first argument is negative and the second argument is 
     * positive zero or negative zero, or the first argument is negative 
     * infinity and the second argument is finite, then the result is the 
     * <code>double</code> value closest to -<i>pi</i>/2. 
     * <li>If both arguments are positive infinity, then the result is the 
     * <code>double</code> value closest to <i>pi</i>/4. 
     * <li>If the first argument is positive infinity and the second argument 
     * is negative infinity, then the result is the <code>double</code> 
     * value closest to 3*<i>pi</i>/4. 
     * <li>If the first argument is negative infinity and the second argument 
     * is positive infinity, then the result is the <code>double</code> value 
     * closest to -<i>pi</i>/4. 
     * <li>If both arguments are negative infinity, then the result is the 
     * <code>double</code> value closest to -3*<i>pi</i>/4.</ul>
     *
     * @param   y   the ordinate coordinate
     * @param   x   the abscissa coordinate
     * @return  the <i>theta</i> component of the point
     *          (<i>r</i>,&nbsp;<i>theta</i>)
     *          in polar coordinates that corresponds to the point
     *          (<i>x</i>,&nbsp;<i>y</i>) in Cartesian coordinates.
     */
    public static native double atan2(double y, double x);


    /**
     * Returns the value of the first argument raised to the power of the
     * second argument. Special cases:
     *
     * <ul><li>If the second argument is positive or negative zero, then the 
     * result is 1.0. 
     * <li>If the second argument is 1.0, then the result is the same as the 
     * first argument.
     * <li>If the second argument is NaN, then the result is NaN. 
     * <li>If the first argument is NaN and the second argument is nonzero, 
     * then the result is NaN. 
     *
     * <li>If
     * <ul>
     * <li>the absolute value of the first argument is greater than 1
     * and the second argument is positive infinity, or
     * <li>the absolute value of the first argument is less than 1 and
     * the second argument is negative infinity,
     * </ul>
     * then the result is positive infinity. 
     *
     * <li>If 
     * <ul>
     * <li>the absolute value of the first argument is greater than 1 and 
     * the second argument is negative infinity, or 
     * <li>the absolute value of the 
     * first argument is less than 1 and the second argument is positive 
     * infinity,
     * </ul>
     * then the result is positive zero. 
     *
     * <li>If the absolute value of the first argument equals 1 and the 
     * second argument is infinite, then the result is NaN. 
     *
     * <li>If 
     * <ul>
     * <li>the first argument is positive zero and the second argument
     * is greater than zero, or
     * <li>the first argument is positive infinity and the second
     * argument is less than zero,
     * </ul>
     * then the result is positive zero. 
     *
     * <li>If 
     * <ul>
     * <li>the first argument is positive zero and the second argument
     * is less than zero, or
     * <li>the first argument is positive infinity and the second
     * argument is greater than zero,
     * </ul>
     * then the result is positive infinity.
     *
     * <li>If 
     * <ul>
     * <li>the first argument is negative zero and the second argument
     * is greater than zero but not a finite odd integer, or
     * <li>the first argument is negative infinity and the second
     * argument is less than zero but not a finite odd integer,
     * </ul>
     * then the result is positive zero. 
     *
     * <li>If 
     * <ul>
     * <li>the first argument is negative zero and the second argument
     * is a positive finite odd integer, or
     * <li>the first argument is negative infinity and the second
     * argument is a negative finite odd integer,
     * </ul>
     * then the result is negative zero. 
     *
     * <li>If
     * <ul>
     * <li>the first argument is negative zero and the second argument
     * is less than zero but not a finite odd integer, or
     * <li>the first argument is negative infinity and the second
     * argument is greater than zero but not a finite odd integer,
     * </ul>
     * then the result is positive infinity. 
     *
     * <li>If 
     * <ul>
     * <li>the first argument is negative zero and the second argument
     * is a negative finite odd integer, or
     * <li>the first argument is negative infinity and the second
     * argument is a positive finite odd integer,
     * </ul>
     * then the result is negative infinity. 
     *
     * <li>If the first argument is finite and less than zero
     * <ul>
     * <li> if the second argument is a finite even integer, the
     * result is equal to the result of raising the absolute value of
     * the first argument to the power of the second argument
     *
     * <li>if the second argument is a finite odd integer, the result
     * is equal to the negative of the result of raising the absolute
     * value of the first argument to the power of the second
     * argument
     *
     * <li>if the second argument is finite and not an integer, then
     * the result is NaN.
     * </ul>
     *
     * <li>If both arguments are integers, then the result is exactly equal 
     * to the mathematical result of raising the first argument to the power 
     * of the second argument if that result can in fact be represented 
     * exactly as a <code>double</code> value.</ul>
     * 
     * <p>(In the foregoing descriptions, a floating-point value is
     * considered to be an integer if and only if it is finite and a
     * fixed point of the method {@link #ceil <tt>ceil</tt>} or,
     * equivalently, a fixed point of the method {@link #floor
     * <tt>floor</tt>}. A value is a fixed point of a one-argument
     * method if and only if the result of applying the method to the
     * value is equal to the value.)
     *
     * @param   a   base.
     * @param   b   the exponent.
     * @return  the value <code>a<sup>b</sup></code>.
     */
    public static native double pow(double a, double b);

    /**
     * Returns the closest <code>int</code> to the argument. The 
     * result is rounded to an integer by adding 1/2, taking the 
     * floor of the result, and casting the result to type <code>int</code>. 
     * In other words, the result is equal to the value of the expression:
     * <p><pre>(int)Math.floor(a + 0.5f)</pre>
     * <p>
     * Special cases:
     * <ul><li>If the argument is NaN, the result is 0.
     * <li>If the argument is negative infinity or any value less than or 
     * equal to the value of <code>Integer.MIN_VALUE</code>, the result is 
     * equal to the value of <code>Integer.MIN_VALUE</code>. 
     * <li>If the argument is positive infinity or any value greater than or 
     * equal to the value of <code>Integer.MAX_VALUE</code>, the result is 
     * equal to the value of <code>Integer.MAX_VALUE</code>.</ul> 
     *
     * @param   a   a floating-point value to be rounded to an integer.
     * @return  the value of the argument rounded to the nearest
     *          <code>int</code> value.
     * @see     java.lang.Integer#MAX_VALUE
     * @see     java.lang.Integer#MIN_VALUE
     */
    public static int round(float a) {
	return (int)floor(a + 0.5f);
    }

    /**
     * Returns the closest <code>long</code> to the argument. The result 
     * is rounded to an integer by adding 1/2, taking the floor of the 
     * result, and casting the result to type <code>long</code>. In other 
     * words, the result is equal to the value of the expression:
     * <p><pre>(long)Math.floor(a + 0.5d)</pre>
     * <p>
     * Special cases:
     * <ul><li>If the argument is NaN, the result is 0.
     * <li>If the argument is negative infinity or any value less than or 
     * equal to the value of <code>Long.MIN_VALUE</code>, the result is 
     * equal to the value of <code>Long.MIN_VALUE</code>. 
     * <li>If the argument is positive infinity or any value greater than or 
     * equal to the value of <code>Long.MAX_VALUE</code>, the result is 
     * equal to the value of <code>Long.MAX_VALUE</code>.</ul> 
     *
     * @param   a  a floating-point value to be rounded to a
     *		<code>long</code>. 
     * @return  the value of the argument rounded to the nearest
     *          <code>long</code> value.
     * @see     java.lang.Long#MAX_VALUE
     * @see     java.lang.Long#MIN_VALUE
     */
    public static long round(double a) {
	return (long)floor(a + 0.5d);
    }

    private static Random randomNumberGenerator;

    private static synchronized void initRNG() {
        if (randomNumberGenerator == null) 
            randomNumberGenerator = new Random();
    }

    /**
     * Returns a <code>double</code> value with a positive sign, greater 
     * than or equal to <code>0.0</code> and less than <code>1.0</code>. 
     * Returned values are chosen pseudorandomly with (approximately) 
     * uniform distribution from that range. 
     * 
     * <p>When this method is first called, it creates a single new
     * pseudorandom-number generator, exactly as if by the expression
     * <blockquote><pre>new java.util.Random</pre></blockquote> This
     * new pseudorandom-number generator is used thereafter for all
     * calls to this method and is used nowhere else.
     * 
     * <p>This method is properly synchronized to allow correct use by
     * more than one thread. However, if many threads need to generate
     * pseudorandom numbers at a great rate, it may reduce contention
     * for each thread to have its own pseudorandom number generator.
     *  
     * @return  a pseudorandom <code>double</code> greater than or equal 
     * to <code>0.0</code> and less than <code>1.0</code>.
     * @see     java.util.Random#nextDouble()
     */
    public static double random() {
        if (randomNumberGenerator == null) initRNG();
        return randomNumberGenerator.nextDouble();
    }

    /**
     * Returns the absolute value of an <code>int</code> value..
     * If the argument is not negative, the argument is returned.
     * If the argument is negative, the negation of the argument is returned. 
     * 
     * <p>Note that if the argument is equal to the value of
     * <code>Integer.MIN_VALUE</code>, the most negative representable
     * <code>int</code> value, the result is that same value, which is
     * negative.
     *
     * @param   a   the  argument whose absolute value is to be determined.
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
     * 
     * <p>Note that if the argument is equal to the value of
     * <code>Long.MIN_VALUE</code>, the most negative representable
     * <code>long</code> value, the result is that same value, which
     * is negative.
     *
     * @param   a   the  argument whose absolute value is to be determined.
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
     * Special cases:
     * <ul><li>If the argument is positive zero or negative zero, the 
     * result is positive zero. 
     * <li>If the argument is infinite, the result is positive infinity. 
     * <li>If the argument is NaN, the result is NaN.</ul>
     * In other words, the result is the same as the value of the expression: 
     * <p><pre>Float.intBitsToFloat(0x7fffffff & Float.floatToIntBits(a))</pre>
     *
     * @param   a   the argument whose absolute value is to be determined
     * @return  the absolute value of the argument.
     */
    public static float abs(float a) {
        return (a <= 0.0F) ? 0.0F - a : a;
    }
  
    /**
     * Returns the absolute value of a <code>double</code> value.
     * If the argument is not negative, the argument is returned.
     * If the argument is negative, the negation of the argument is returned. 
     * Special cases:
     * <ul><li>If the argument is positive zero or negative zero, the result 
     * is positive zero. 
     * <li>If the argument is infinite, the result is positive infinity. 
     * <li>If the argument is NaN, the result is NaN.</ul>
     * In other words, the result is the same as the value of the expression: 
     * <p><code>Double.longBitsToDouble((Double.doubleToLongBits(a)&lt;&lt;1)&gt;&gt;&gt;1)</code>
     *
     * @param   a   the argument whose absolute value is to be determined
     * @return  the absolute value of the argument.
     */
    public static double abs(double a) {
        return (a <= 0.0D) ? 0.0D - a : a;
    }

    /**
     * Returns the greater of two <code>int</code> values. That is, the 
     * result is the argument closer to the value of 
     * <code>Integer.MAX_VALUE</code>. If the arguments have the same value, 
     * the result is that same value.
     *
     * @param   a   an argument.
     * @param   b   another argument.
     * @return  the larger of <code>a</code> and <code>b</code>.
     * @see     java.lang.Long#MAX_VALUE
     */
    public static int max(int a, int b) {
	return (a >= b) ? a : b;
    }

    /**
     * Returns the greater of two <code>long</code> values. That is, the 
     * result is the argument closer to the value of 
     * <code>Long.MAX_VALUE</code>. If the arguments have the same value, 
     * the result is that same value. 
     *
     * @param   a   an argument.
     * @param   b   another argument.
     * @return  the larger of <code>a</code> and <code>b</code>.
     * @see     java.lang.Long#MAX_VALUE
     */
    public static long max(long a, long b) {
	return (a >= b) ? a : b;
    }

    private static long negativeZeroFloatBits = Float.floatToIntBits(-0.0f);
    private static long negativeZeroDoubleBits = Double.doubleToLongBits(-0.0d);

    /**
     * Returns the greater of two <code>float</code> values.  That is,
     * the result is the argument closer to positive infinity. If the
     * arguments have the same value, the result is that same
     * value. If either value is NaN, then the result is NaN.  Unlike
     * the numerical comparison operators, this method considers
     * negative zero to be strictly smaller than positive zero. If one
     * argument is positive zero and the other negative zero, the
     * result is positive zero.
     *
     * @param   a   an argument.
     * @param   b   another argument.
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
     * Returns the greater of two <code>double</code> values.  That
     * is, the result is the argument closer to positive infinity. If
     * the arguments have the same value, the result is that same
     * value. If either value is NaN, then the result is NaN.  Unlike
     * the numerical comparison operators, this method considers
     * negative zero to be strictly smaller than positive zero. If one
     * argument is positive zero and the other negative zero, the
     * result is positive zero.
     *
     * @param   a   an argument.
     * @param   b   another argument.
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
     * Returns the smaller of two <code>int</code> values. That is,
     * the result the argument closer to the value of
     * <code>Integer.MIN_VALUE</code>.  If the arguments have the same
     * value, the result is that same value.
     *
     * @param   a   an argument.
     * @param   b   another argument.
     * @return  the smaller of <code>a</code> and <code>b</code>.
     * @see     java.lang.Long#MIN_VALUE
     */
    public static int min(int a, int b) {
	return (a <= b) ? a : b;
    }

    /**
     * Returns the smaller of two <code>long</code> values. That is,
     * the result is the argument closer to the value of
     * <code>Long.MIN_VALUE</code>. If the arguments have the same
     * value, the result is that same value.
     *
     * @param   a   an argument.
     * @param   b   another argument.
     * @return  the smaller of <code>a</code> and <code>b</code>.
     * @see     java.lang.Long#MIN_VALUE
     */
    public static long min(long a, long b) {
	return (a <= b) ? a : b;
    }

    /**
     * Returns the smaller of two <code>float</code> values.  That is,
     * the result is the value closer to negative infinity. If the
     * arguments have the same value, the result is that same
     * value. If either value is NaN, then the result is NaN.  Unlike
     * the numerical comparison operators, this method considers
     * negative zero to be strictly smaller than positive zero.  If
     * one argument is positive zero and the other is negative zero,
     * the result is negative zero.
     *
     * @param   a   an argument.
     * @param   b   another argument.
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
     * Returns the smaller of two <code>double</code> values.  That
     * is, the result is the value closer to negative infinity. If the
     * arguments have the same value, the result is that same
     * value. If either value is NaN, then the result is NaN.  Unlike
     * the numerical comparison operators, this method considers
     * negative zero to be strictly smaller than positive zero. If one
     * argument is positive zero and the other is negative zero, the
     * result is negative zero.
     *
     * @param   a   an argument.
     * @param   b   another argument.
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

    /**
     * Returns the size of an ulp of the argument.  An ulp of a
     * <code>double</code> value is the positive distance between this
     * floating-point value and the <code>double</code> value next
     * larger in magnitude.  Note that for non-NaN <i>x</i>,
     * <code>ulp(-<i>x</i>) == ulp(<i>x</i>)</code>.
     * 
     * <p>Special Cases:
     * <ul>
     * <li> If the argument is NaN, then the result is NaN.
     * <li> If the argument is positive or negative infinity, then the
     * result is positive infinity.
     * <li> If the argument is positive or negative zero, then the result is
     * <code>Double.MIN_VALUE</code>.
     * <li> If the argument is &plusmn;<code>Double.MAX_VALUE</code>, then
     * the result is equal to 2<sup>971</sup>.
     * </ul>
     *
     * @param d the floating-point value whose ulp is to be returned
     * @return the size of an ulp of the argument
     * @author Joseph D. Darcy
     * @since 1.5
     */
    public static double ulp(double d) {
	return sun.misc.FpUtils.ulp(d);
    }

    /**
     * Returns the size of an ulp of the argument.  An ulp of a
     * <code>float</code> value is the positive distance between this
     * floating-point value and the <code>float</code> value next
     * larger in magnitude.  Note that for non-NaN <i>x</i>,
     * <code>ulp(-<i>x</i>) == ulp(<i>x</i>)</code>.
     * 
     * <p>Special Cases:
     * <ul>
     * <li> If the argument is NaN, then the result is NaN.
     * <li> If the argument is positive or negative infinity, then the
     * result is positive infinity.
     * <li> If the argument is positive or negative zero, then the result is
     * <code>Float.MIN_VALUE</code>.
     * <li> If the argument is &plusmn;<code>Float.MAX_VALUE</code>, then
     * the result is equal to 2<sup>104</sup>.
     * </ul>
     *
     * @param f the floating-point value whose ulp is to be returned
     * @return the size of an ulp of the argument
     * @author Joseph D. Darcy
     * @since 1.5
     */
    public static float ulp(float f) {
	return sun.misc.FpUtils.ulp(f);
    }

    /**
     * Returns the signum function of the argument; zero if the argument
     * is zero, 1.0 if the argument is greater than zero, -1.0 if the
     * argument is less than zero.
     *
     * <p>Special Cases:
     * <ul>
     * <li> If the argument is NaN, then the result is NaN.
     * <li> If the argument is positive zero or negative zero, then the
     *      result is the same as the argument.
     * </ul>
     *
     * @param d the floating-point value whose signum is to be returned
     * @return the signum function of the argument
     * @author Joseph D. Darcy
     * @since 1.5
     */
    public static double signum(double d) {
	return sun.misc.FpUtils.signum(d);
    }

    /**
     * Returns the signum function of the argument; zero if the argument
     * is zero, 1.0f if the argument is greater than zero, -1.0f if the
     * argument is less than zero.
     *
     * <p>Special Cases:
     * <ul>
     * <li> If the argument is NaN, then the result is NaN.
     * <li> If the argument is positive zero or negative zero, then the
     *      result is the same as the argument.
     * </ul>
     *
     * @param f the floating-point value whose signum is to be returned
     * @return the signum function of the argument
     * @author Joseph D. Darcy
     * @since 1.5
     */
    public static float signum(float f) {
	return sun.misc.FpUtils.signum(f);
    }

    /**
     * Returns the hyperbolic sine of a <code>double</code> value.
     * The hyperbolic sine of <i>x</i> is defined to be
     * (<i>e<sup>x</sup>&nbsp;-&nbsp;e<sup>-x</sup></i>)/2
     * where <i>e</i> is {@linkplain Math#E Euler's number}.
     *
     * <p>Special cases:
     * <ul>
     *
     * <li>If the argument is NaN, then the result is NaN.
     *
     * <li>If the argument is infinite, then the result is an infinity
     * with the same sign as the argument.
     *
     * <li>If the argument is zero, then the result is a zero with the
     * same sign as the argument.
     *
     * </ul>
     *
     * @param   x The number whose hyperbolic sine is to be returned.
     * @return  The hyperbolic sine of <code>x</code>.
     * @since 1.5
     */
    public static native double sinh(double x);

    /**
     * Returns the hyperbolic cosine of a <code>double</code> value.
     * The hyperbolic cosine of <i>x</i> is defined to be
     * (<i>e<sup>x</sup>&nbsp;+&nbsp;e<sup>-x</sup></i>)/2
     * where <i>e</i> is {@linkplain Math#E Euler's number}.
     *
     * <p>Special cases:
     * <ul>
     *
     * <li>If the argument is NaN, then the result is NaN.
     *
     * <li>If the argument is infinite, then the result is positive
     * infinity.
     *
     * <li>If the argument is zero, then the result is <code>1.0</code>.
     *
     * </ul>
     *
     * @param   x The number whose hyperbolic cosine is to be returned.
     * @return  The hyperbolic cosine of <code>x</code>.
     * @since 1.5
     */
    public static native double cosh(double x);

    /**
     * Returns the hyperbolic tangent of a <code>double</code> value.
     * The hyperbolic tangent of <i>x</i> is defined to be
     * (<i>e<sup>x</sup>&nbsp;-&nbsp;e<sup>-x</sup></i>)/(<i>e<sup>x</sup>&nbsp;+&nbsp;e<sup>-x</sup></i>),
     * in other words, {@linkplain Math#sinh
     * sinh(<i>x</i>)}/{@linkplain Math#cosh cosh(<i>x</i>)}.  Note
     * that the absolute value of the exact tanh is always less than
     * 1.
     *
     * <p>Special cases:
     * <ul>
     *
     * <li>If the argument is NaN, then the result is NaN.
     *
     * <li>If the argument is zero, then the result is a zero with the
     * same sign as the argument.
     *
     * <li>If the argument is positive infinity, then the result is
     * <code>+1.0</code>.
     *
     * <li>If the argument is negative infinity, then the result is
     * <code>-1.0</code>.
     *  
     * </ul>
     *
     * @param   x The number whose hyperbolic tangent is to be returned.
     * @return  The hyperbolic tangent of <code>x</code>.
     * @since 1.5
     */
    public static native double tanh(double x);

    /**
     * Returns sqrt(<i>x</i><sup>2</sup>&nbsp;+<i>y</i><sup>2</sup>)
     * without intermediate overflow or underflow.
     *
     * <p>Special cases:
     * <ul>
     *
     * <li> If either argument is infinite, then the result
     * is positive infinity.
     *
     * <li> If either argument is NaN and neither argument is infinite,
     * then the result is NaN.
     *
     * </ul>
     *
     * @param x a value
     * @param y a value
     * @return sqrt(<i>x</i><sup>2</sup>&nbsp;+<i>y</i><sup>2</sup>)
     * without intermediate overflow or underflow
     * @since 1.5
     */
    public static native double hypot(double x, double y);

    /**
     * Returns <i>e</i><sup>x</sup>&nbsp;-1.  Note that for values of
     * <i>x</i> near 0, the exact sum of
     * <code>expm1(x)</code>&nbsp;+&nbsp;1 is much closer to the true
     * result of <i>e</i><sup>x</sup> than <code>exp(x)</code>.
     *
     * <p>Special cases:
     * <ul>
     * <li>If the argument is NaN, the result is NaN.
     *
     * <li>If the argument is positive infinity, then the result is
     * positive infinity.
     *
     * <li>If the argument is negative infinity, then the result is
     * -1.0.
     *
     * <li>If the argument is zero, then the result is a zero with the
     * same sign as the argument.
     *
     * </ul>
     *
     * @param   x   the exponent to raise <i>e</i> to in the computation of
     *              <i>e</i><sup><code>x</code></sup>&nbsp;-1.
     * @return  the value <i>e</i><sup><code>x</code></sup>&nbsp;-&nbsp;1.
     */
    public static native double expm1(double x);

    /**
     * Returns the natural logarithm of the sum of the argument and 1.
     * Note that for small values <code>x</code>, the result of
     * <code>log1p(x)</code> is much closer to the true result of ln(1
     * + <code>x</code>) than the floating-point evaluation of
     * <code>log(1.0+x)</code>.
     *
     * <p>Special cases:
     *
     * <ul>
     *
     * <li>If the argument is NaN or less than -1, then the result is
     * NaN.
     *
     * <li>If the argument is positive infinity, then the result is
     * positive infinity.
     *
     * <li>If the argument is negative one, then the result is
     * negative infinity.
     *
     * <li>If the argument is zero, then the result is a zero with the
     * same sign as the argument.
     *
     * </ul>
     *
     * @param   x   a value
     * @return the value ln(<code>x</code>&nbsp;+&nbsp;1), the natural
     * log of <code>x</code>&nbsp;+&nbsp;1
     */
    public static native double log1p(double x);
}
