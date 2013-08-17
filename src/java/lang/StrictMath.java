/*
 * @(#)StrictMath.java	1.9 00/02/02
 *
 * Copyright 1994-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.lang;
import java.util.Random;


/**
 * The class <code>StrictMath</code> contains methods for performing basic 
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
 *   <a href="http://metalab.unc.edu/">http://metalab.unc.edu/</a>
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
 * @version 1.9, 02/02/00
 * @since   1.3
 */

public final strictfp class StrictMath {

    /**
     * Don't let anyone instantiate this class.
     */
    private StrictMath() {}

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
     * Returns the trigonometric sine of an angle. Special cases:
     * <ul><li>If the argument is NaN or an infinity, then the 
     * result is NaN.
     * <li>If the argument is positive zero, then the result is 
     * positive zero; if the argument is negative zero, then the 
     * result is negative zero.</ul>
     *
     * @param   a   an angle, in radians.
     * @return  the sine of the argument.
     */
    public static native double sin(double a);
    
    /**
     * Returns the trigonometric cosine of an angle. Special case:
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
     * <li>If the argument is positive zero, then the result is 
     * positive zero; if the argument is negative zero, then the 
     * result is negative zero</ul>
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
     * <li>If the argument is positive zero, then the result is positive 
     * zero; if the argument is negative zero, then the result is 
     * negative zero.</ul>
     *
     * @param   a   the <code>double</code> value whose arc sine is to 
     *              be returned.
     * @return  the arc sine of the argument.
     */
    public static native double asin(double a);

    /**
     * Returns the arc cosine of an angle, in the range of 0.0 through
     * <i>pi</i>. Special case:
     * <ul><li>If the argument is NaN or its absolute value is greater 
     * than 1, then the result is NaN.</ul>
     *
     * @param   a   the <code>double</code> value whose arc cosine is to 
     *              be returned.
     * @return  the arc cosine of the argument.
     */
    public static native double acos(double a); 

    /**
     * Returns the arc tangent of an angle, in the range of -<i>pi</i>/2
     * through <i>pi</i>/2. Special cases: 
     * <ul><li>If the argument is NaN, then the result is NaN.
     * <li>If the argument is positive zero, then the result is positive 
     * zero; if the argument is negative zero, then the result is 
     * negative zero.</ul>
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
     */
    public static double toDegrees(double angrad) {
	return angrad * 180.0 / PI;
    }

    /**
     * Returns the exponential number <i>e</i> (i.e., 2.718...) raised to
     * the power of a <code>double</code> value. Special cases:
     * <ul><li>If the argument is NaN, the result is NaN.
     * <li>If the argument is positive infinity, then the result is 
     * positive infinity.
     * <li>If the argument is negative infinity, then the result is 
     * positive zero.</ul>
     *
     * @param   a   a <code>double</code> value.
     * @return  the value <i>e</i><sup>a</sup>, where <i>e</i> is the base of
     *          the natural logarithms.
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
     * @param   a   a number greater than <code>0.0</code>.
     * @return  the value ln&nbsp;<code>a</code>, the natural logarithm of
     *          <code>a</code>.
     */
    public static native double log(double a);

    /**
     * Returns the positive square root of a <code>double</code> value. 
     * Special cases:
     * <ul><li>If the argument is NaN or less than zero, then the result 
     * is NaN. 
     * <li>If the argument is positive infinity, then the result is positive 
     * infinity. 
     * <li>If the argument is positive zero or negative zero, then the 
     * result is the same as the argument.</ul>
     * Otherwise, the result is the <code>double</code> value closest to 
     * the true mathetmatical square root of the argument value.
     *
     * @param   a   a <code>double</code> value.
     * <!--@return  the value of &radic;&nbsp;<code>a</code>.-->
     * @return  the positive square root of <code>a</code>.
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
     * <code>double</code> value that is not less than the argument and is 
     * equal to a mathematical integer. Special cases:
     * <ul><li>If the argument value is already equal to a mathematical 
     * integer, then the result is the same as the argument. 
     * <li>If the argument is NaN or an infinity or positive zero or negative 
     * zero, then the result is the same as the argument. 
     * <li>If the argument value is less than zero but greater than -1.0, 
     * then the result is negative zero.</ul>
     * Note that the value of <code>Math.ceil(x)</code> is exactly the 
     * value of <code>-Math.floor(-x)</code>.
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
     * is equal to a mathematical integer. Special cases:
     * <ul><li>If the argument value is already equal to a mathematical 
     * integer, then the result is the same as the argument. 
     * <li>If the argument is NaN or an infinity or positive zero or 
     * negative zero, then the result is the same as the argument.</ul>
     *
     * @param   a   a <code>double</code> value.
     * <!--@return  the value &lfloor;&nbsp;<code>a</code>&nbsp;&rfloor;.-->
     * @return  the largest (closest to positive infinity) 
     *          <code>double</code> value that is not greater than the argument
     *          and is equal to a mathematical integer. 
     */
    public static native double floor(double a);

    /**
     * Returns the <code>double</code> value that is closest in value to 
     * <code>a</code> and is equal to a mathematical integer. If two 
     * <code>double</code> values that are mathematical integers are equally 
     * close to the value of the argument, the result is the integer value 
     * that is even. Special cases:
     * <ul><li>If the argument value is already equal to a mathematical 
     * integer, then the result is the same as the argument. 
     * <li>If the argument is NaN or an infinity or positive zero or negative 
     * zero, then the result is the same as the argument.</ul>
     *
     * @param   a   a <code>double</code> value.
     * @return  the closest <code>double</code> value to <code>a</code> that is
     *          equal to a mathematical integer.
     */
    public static native double rint(double a);

    /**
     * Converts rectangular coordinates (<code>b</code>,&nbsp;<code>a</code>)
     * to polar (r,&nbsp;<i>theta</i>).
     * This method computes the phase <i>theta</i> by computing an arc tangent
     * of <code>a/b</code> in the range of -<i>pi</i> to <i>pi</i>. Special 
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
     * <code>double</code> value closest to pi. 
     * <li>If the first argument is negative zero and the second argument 
     * is negative, or the first argument is negative and finite and the 
     * second argument is negative infinity, then the result is the 
     * <code>double</code> value closest to -pi. 
     * <li>If the first argument is positive and the second argument is 
     * positive zero or negative zero, or the first argument is positive 
     * infinity and the second argument is finite, then the result is the 
     * <code>double</code> value closest to pi/2. 
     * <li>If the first argument is negative and the second argument is 
     * positive zero or negative zero, or the first argument is negative 
     * infinity and the second argument is finite, then the result is the 
     * <code>double</code> value closest to -pi/2. 
     * <li>If both arguments are positive infinity, then the result is the 
     * <code>double</code> value closest to pi/4. 
     * <li>If the first argument is positive infinity and the second argument 
     * is negative infinity, then the result is the <code>double</code> 
     * value closest to 3*pi/4. 
     * <li>If the first argument is negative infinity and the second argument 
     * is positive infinity, then the result is the <code>double</code> value 
     * closest to -pi/4. 
     * <li>If both arguments are negative infinity, then the result is the 
     * <code>double</code> value closest to -3*pi/4.</ul>
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
     * second argument. Special cases:
     * <ul><li>If the second argument is positive or negative zero, then the 
     * result is 1.0. 
     * <li>If the second argument is 1.0, then the result is the same as the 
     * first argument.
     * <li>If the second argument is NaN, then the result is NaN. 
     * <li>If the first argument is NaN and the second argument is nonzero, 
     * then the result is NaN. 
     * <li>If the absolute value of the first argument is greater than 1 and 
     * the second argument is positive infinity, or the absolute value of the 
     * first argument is less than 1 and the second argument is negative 
     * infinity, then the result is positive infinity. 
     * <li>If the absolute value of the first argument is greater than 1 and 
     * the second argument is negative infinity, or the absolute value of the 
     * first argument is less than 1 and the second argument is positive 
     * infinity, then the result is positive zero. 
     * <li>If the absolute value of the first argument equals 1 and the 
     * second argument is infinite, then the result is NaN. 
     * <li>If the first argument is positive zero and the second argument is 
     * greater than zero, or the first argument is positive infinity and the 
     * second argument is less than zero, then the result is positive zero. 
     * <li>If the first argument is positive zero and the second argument is 
     * less than zero, or the first argument is positive infinity and the 
     * second argument is greater than zero, then the result is positive 
     * infinity. 
     * <li>If the first argument is negative zero and the second argument is 
     * greater than zero but not a finite odd integer, or the first argument 
     * is negative infinity and the second argument is less than zero but not 
     * a finite odd integer, then the result is positive zero. 
     * <li>If the first argument is negative zero and the second argument is 
     * a positive finite odd integer, or the first argument is negative 
     * infinity and the second argument is a negative finite odd integer, 
     * then the result is negative zero. 
     * <li>If the first argument is negative zero and the second argument is 
     * less than zero but not a finite odd integer, or the first argument is 
     * negative infinity and the second argument is greater than zero but not 
     * a finite odd integer, then the result is positive infinity. 
     * <li>If the first argument is negative zero and the second argument is 
     * a negative finite odd integer, or the first argument is negative 
     * infinity and the second argument is a positive finite odd integer, 
     * then the result is negative infinity. 
     * <li>If the first argument is less than zero and the second argument is 
     * a finite even integer, then the result is equal to the result of 
     * raising the absolute value of the first argument to the power of the 
     * second argument. 
     * <li>If the first argument is less than zero and the second argument 
     * is a finite odd integer, then the result is equal to the negative of 
     * the result of raising the absolute value of the first argument to the 
     * power of the second argument. 
     * <li>If the first argument is finite and less than zero and the second 
     * argument is finite and not an integer, then the result is NaN. 
     * <li>If both arguments are integers, then the result is exactly equal 
     * to the mathematical result of raising the first argument to the power 
     * of the second argument if that result can in fact be represented 
     * exactly as a double value.</ul>
     * 
     * <p>(In the foregoing descriptions, a floating-point value is 
     * considered to be an integer if and only if it is a fixed point of the 
     * method {@link #ceil <tt>ceil</tt>} or, which is the same thing, a fixed 
     * point of the method {@link #floor <tt>floor</tt>}. A value is a fixed 
     * point of a one-argument method if and only if the result of applying 
     * the method to the value is equal to the value.)  
     *
     * @param   a   a <code>double</code> value.
     * @param   b   a <code>double</code> value.
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

    private static synchronized void initRNG() {
        if (randomNumberGenerator == null) 
            randomNumberGenerator = new Random();
    }

    /**
     * Returns a <code>double</code> value with a positive sign, greater 
     * than or equal to <code>0.0</code> and less than <code>1.0</code>. 
     * Returned values are chosen pseudorandomly with (approximately) 
     * uniform distribution from that range. 
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
     * each thread to have its own pseudorandom number generator.
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
     * <p>
     * Note that if the argument is equal to the value of 
     * <code>Integer.MIN_VALUE</code>, the most negative representable 
     * <code>int</code> value, the result is that same value, which is 
     * negative. 
     *
     * @param   a   the <code>int</code> argument whose absolute value is 
     *          to be determined.
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
     * Special cases:
     * <ul><li>If the argument is positive zero or negative zero, the 
     * result is positive zero. 
     * <li>If the argument is infinite, the result is positive infinity. 
     * <li>If the argument is NaN, the result is NaN.</ul>
     * In other words, the result is equal to the value of the expression: 
     * <p><pre>Float.intBitsToFloat(0x7fffffff & Float.floatToIntBits(a))</pre>
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
     * Special cases:
     * <ul><li>If the argument is positive zero or negative zero, the result 
     * is positive zero. 
     * <li>If the argument is infinite, the result is positive infinity. 
     * <li>If the argument is NaN, the result is NaN.</ul>
     * In other words, the result is equal to the value of the expression: 
     * <p><pre>Double.longBitsToDouble((Double.doubleToLongBits(a)<<1)>>>1)</pre>
     *
     * @param   a   a <code>double</code> value.
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
     * @param   a   an <code>int</code> value.
     * @param   b   an <code>int</code> value.
     * @return  the larger of <code>a</code> and <code>b</code>.
     * @see     java.lang.Long#MAX_VALUE
     */
    public static int max(int a, int b) {
	return (a >= b) ? a : b;
    }

    /**
     * Returns the greater of two <code>long</code> values. That is, the 
     * result is the argument closer to the value of 
     * <code>Long.MAX_VALUE</code>. If the argumens have the same value, 
     * the result is that same value. 
     *
     * @param   a   a <code>long</code> value.
     * @param   b   a <code>long</code> value.
     * @return  the larger of <code>a</code> and <code>b</code>.
     * @see     java.lang.Long#MAX_VALUE
     */
    public static long max(long a, long b) {
	return (a >= b) ? a : b;
    }

    private static long negativeZeroFloatBits = Float.floatToIntBits(-0.0f);
    private static long negativeZeroDoubleBits = Double.doubleToLongBits(-0.0d);

    /**
     * Returns the greater of two <code>float</code> values.  That is, the 
     * result is the argument closer to positive infinity. If the 
     * arguments have the same value, the result is that same value. If 
     * either value is <code>NaN</code>, then the result is <code>NaN</code>.  
     * Unlike the the numerical comparison operators, this method considers 
     * negative zero to be strictly smaller than positive zero. If one 
     * argument is positive zero and the other negative zero, the result 
     * is positive zero.
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
     * Returns the greater of two <code>double</code> values.  That is, the 
     * result is the argument closer to positive infinity. If the 
     * arguments have the same value, the result is that same value. If 
     * either value is <code>NaN</code>, then the result is <code>NaN</code>.  
     * Unlike the the numerical comparison operators, this method considers 
     * negative zero to be strictly smaller than positive zero. If one 
     * argument is positive zero and the other negative zero, the result 
     * is positive zero.
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
     * Returns the smaller of two <code>int</code> values. That is, the 
     * result the argument closer to the value of <code>Integer.MIN_VALUE</code>. 
     * If the arguments have the same value, the result is that same value.
     *
     * @param   a   an <code>int</code> value.
     * @param   b   an <code>int</code> value.
     * @return  the smaller of <code>a</code> and <code>b</code>.
     * @see     java.lang.Long#MIN_VALUE
     */
    public static int min(int a, int b) {
	return (a <= b) ? a : b;
    }

    /**
     * Returns the smaller of two <code>long</code> values. That is, the 
     * result is the argument closer to the value of
     * <code>Long.MIN_VALUE</code>. If the arguments have the same value, 
     * the result is that same value.
     *
     * @param   a   a <code>long</code> value.
     * @param   b   a <code>long</code> value.
     * @return  the smaller of <code>a</code> and <code>b</code>.
     * @see     java.lang.Long#MIN_VALUE
     */
    public static long min(long a, long b) {
	return (a <= b) ? a : b;
    }

    /**
     * Returns the smaller of two <code>float</code> values.  That is, the 
     * result is the value closer to negative infinity. If the arguments 
     * have the same value, the result is that same value. If either value
     * is <code>NaN</code>, then the result is <code>NaN</code>.  Unlike the
     * the numerical comparison operators, this method considers negative zero
     * to be strictly smaller than positive zero.  If one argument is 
     * positive zero and the other is negative zero, the result is negative 
     * zero.
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
     * Returns the smaller of two <code>double</code> values.  That is, the 
     * result is the value closer to negative infinity. If the arguments have 
     * the same value, the result is that same value. If either value
     * is <code>NaN</code>, then the result is <code>NaN</code>.  Unlike the
     * the numerical comparison operators, this method considers negative zero
     * to be strictly smaller than positive zero. If one argument is 
     * positive zero and the other is negative zero, the result is negative 
     * zero.
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
