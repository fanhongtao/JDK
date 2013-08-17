/*
 * @(#)BigInteger.java	1.26 99/04/22
 *
 * Copyright 1996-1999 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.math;

import java.util.Random;

/**
 * Immutable arbitrary-precision integers.  All operations behave as if
 * BigIntegers were represented in two's-complement notation (like Java's
 * primitive integer types).  BigInteger provides analogues to all of Java's
 * primitive integer operators, and all relevant methods from java.lang.Math.
 * Additionally, BigInteger provides operations for modular arithmetic, GCD
 * calculation, primality testing, prime generation, bit manipulation,
 * and a few other miscellaneous operations.
 * <p>
 * Semantics of arithmetic operations exactly mimic those of Java's integer
 * arithmetic operators, as defined in <i>The Java Language Specification</i>.
 * For example, division by zero throws an <tt>ArithmeticException</tt>, and
 * division of a negative by a positive yields a negative (or zero) remainder.
 * All of the details in the Spec concerning overflow are ignored, as
 * BigIntegers are made as large as necessary to accommodate the results of an
 * operation.
 * <p>
 * Semantics of shift operations extend those of Java's shift operators
 * to allow for negative shift distances.  A right-shift with a negative
 * shift distance results in a left shift, and vice-versa.  The unsigned
 * right shift operator (&gt;&gt;&gt;) is omitted, as this operation makes
 * little sense in combination with the "infinite word size" abstraction
 * provided by this class.
 * <p>
 * Semantics of bitwise logical operations exactly mimic those of Java's
 * bitwise integer operators.  The binary operators (<tt>and</tt>,
 * <tt>or</tt>, <tt>xor</tt>) implicitly perform sign extension on the shorter
 * of the two operands prior to performing the operation.
 * <p>
 * Comparison operations perform signed integer comparisons, analogous to
 * those performed by Java's relational and equality operators.
 * <p>
 * Modular arithmetic operations are provided to compute residues, perform
 * exponentiation, and compute multiplicative inverses.  These methods always
 * return a non-negative result, between <tt>0</tt> and <tt>(modulus - 1)</tt>,
 * inclusive.
 * <p>
 * Bit operations operate on a single bit of the two's-complement
 * representation of their operand.  If necessary, the operand is sign-
 * extended so that it contains the designated bit.  None of the single-bit
 * operations can produce a BigInteger with a different sign from the
 * BigInteger being operated on, as they affect only a single bit, and the
 * "infinite word size" abstraction provided by this class ensures that there
 * are infinitely many "virtual sign bits" preceding each BigInteger.
 * <p>
 * For the sake of brevity and clarity, pseudo-code is used throughout the
 * descriptions of BigInteger methods.  The pseudo-code expression
 * <tt>(i + j)</tt> is shorthand for "a BigInteger whose value is
 * that of the BigInteger <tt>i</tt> plus that of the BigInteger <tt>j</tt>."
 * The pseudo-code expression <tt>(i == j)</tt> is shorthand for
 * "<tt>true</tt> if and only if the BigInteger <tt>i</tt> represents the same
 * value as the the BigInteger <tt>j</tt>."  Other pseudo-code expressions are
 * interpreted similarly. 
 *
 * @see     BigDecimal
 * @version 1.26, 01/05/07
 * @author  Josh Bloch
 * @since JDK1.1
 */
public class BigInteger extends Number implements Comparable {
    /**
     * The signum of this BigInteger: -1 for negative, 0 for zero, or
     * 1 for positive.  Note that the BigInteger zero <i>must</i> have
     * a signum of 0.  This is necessary to ensures that there is exactly one
     * representation for each BigInteger value.
     *
     * @serial
     */
    private int signum;

    /**
     * The magnitude of this BigInteger, in <i>big-endian</i> byte-order: the
     * zeroth element of this array is the most-significant byte of the
     * magnitude.  The magnitude must be "minimal" in that the most-significant
     * byte (<tt>magnitude[0]</tt>) must be non-zero.  This is necessary to
     * ensure that there is exactly one representation for each BigInteger
     * value.  Note that this implies that the BigInteger zero has a
     * zero-length magnitude array.
     *
     * @serial
     */
    private byte[] magnitude;


    // These "redundant fields" are initialized with recognizable nonsense
    // values, and cached the first time they are needed (or never, if they
    // aren't needed).

    /**
     * The bitCount of this BigInteger, as returned by bitCount(), or -1
     * (either value is acceptable).
     *
     * @serial
     * @see #bitCount
     */
    private int bitCount =  -1;

    /**
     * The bitLength of this BigInteger, as returned by bitLength(), or -1
     * (either value is acceptable).
     *
     * @serial
     * @see #bitLength
     */
    private int bitLength = -1;

    /**
     * The lowest set bit of this BigInteger, as returned by getLowestSetBit(),
     * or -2 (either value is acceptable).
     *
     * @serial
     * @see #getLowestSetBit
     */
    private int lowestSetBit = -2;

    /**
     * The byte-number of the lowest-order nonzero byte in the magnitude of
     * this BigInteger, or -2 (either value is acceptable).  The least
     * significant byte has byte-number 0, the next byte in order of
     * increasing significance has byte-number 1, and so forth.
     *
     * @serial
     */
    private int firstNonzeroByteNum = -2;  // Only used for negative numbers


    //Constructors

    /**
     * Translates a byte array containing the two's-complement binary
     * representation of a BigInteger into a BigInteger.  The input array is
     * assumed to be in <i>big-endian</i> byte-order: the most significant
     * byte is in the zeroth element.
     *
     * @param  val big-endian two's-complement binary representation of
     *	       BigInteger.
     * @throws NumberFormatException <tt>val</tt> is zero bytes long.
     */
    public BigInteger(byte[] val) {
	if (val.length == 0)
	    throw new NumberFormatException("Zero length BigInteger");

	if (val[0] < 0) {
	    magnitude = makePositive(val);
	    signum = -1;
	} else {
	    magnitude = stripLeadingZeroBytes(val);
	    signum = (magnitude.length == 0 ? 0 : 1);
	}
    }

    /**
     * Translates the sign-magnitude representation of a BigInteger into a
     * BigInteger.  The sign is represented as an integer signum value: -1 for
     * negative, 0 for zero, or 1 for positive.  The magnitude is a byte array
     * in <i>big-endian</i> byte-order: the most significant byte is in the
     * zeroth element.  A zero-length magnitude array is permissible, and will
     * result in in a BigInteger value of 0, whether signum is -1, 0 or 1.
     *
     * @param  signum signum of the number (-1 for negative, 0 for zero, 1
     * 	       for positive).
     * @param  magnitude big-endian binary representation of the magnitude of
     * 	       the number.
     * @throws NumberFormatException <tt>signum</tt> is not one of the three
     *	       legal values (-1, 0, and 1), or <tt>signum</tt> is 0 and
     *	       <tt>magnitude</tt> contains one or more non-zero bytes.
     */
    public BigInteger(int signum, byte[] magnitude) {
	this.magnitude = stripLeadingZeroBytes(magnitude);

	if (signum < -1 || signum > 1)
	    throw(new NumberFormatException("Invalid signum value"));

	if (this.magnitude.length==0) {
	    this.signum = 0;
	} else {
	    if (signum == 0)
		throw(new NumberFormatException("signum-magnitude mismatch"));
	    this.signum = signum;
	}
    }

    /**
     * Translates the String representation of a BigInteger in the specified
     * radix into a BigInteger.  The String representation consists of an
     * optional minus sign followed by a sequence of one or more digits in the
     * specified radix.  The character-to-digit mapping is provided by
     * <tt>Character.digit</tt>.  The String may not contain any extraneous
     * characters (whitespace, for example).
     *
     * @param val String representation of BigInteger.
     * @param radix radix to be used in interpreting <tt>val</tt>.
     * @throws NumberFormatException <tt>val</tt> is not a valid representation
     *	       of a BigInteger in the specified radix, or <tt>radix</tt> is
     *	       outside the range from <tt>Character.MIN_RADIX</tt> (2) to
     *	       <tt>Character.MAX_RADIX</tt> (36), inclusive.
     * @see    Character#digit
     */
    public BigInteger(String val, int radix) {
	int cursor = 0, numDigits;

	if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
	    throw new NumberFormatException("Radix out of range");
	if (val.length() == 0)
	    throw new NumberFormatException("Zero length BigInteger");

	// Check for leading minus sign
	signum = 1;
	if (val.charAt(0) == '-') {
	    if (val.length() == 1)
		throw new NumberFormatException("Zero length BigInteger");
	    signum = -1;
	    cursor = 1;
	}

        // Skip leading zeros and compute number of digits in magnitude
	while (cursor<val.length() &&
               Character.digit(val.charAt(cursor),radix) == 0)
	    cursor++;
	if (cursor==val.length()) {
	    signum = 0;
	    magnitude = new byte[0];
	    return;
	} else {
	    numDigits = val.length() - cursor;
	}

	// Process first (potentially short) digit group, if necessary
	int firstGroupLen = numDigits % digitsPerLong[radix];
	if (firstGroupLen == 0)
	    firstGroupLen = digitsPerLong[radix];
	String group = val.substring(cursor, cursor += firstGroupLen);
	BigInteger tmp = valueOf(Long.parseLong(group, radix));

	// Process remaining digit groups
	while (cursor < val.length()) {
	    group = val.substring(cursor, cursor += digitsPerLong[radix]);
	    long groupVal = Long.parseLong(group, radix);
	    if (groupVal <0)
		throw new NumberFormatException("Illegal digit");
	    tmp = tmp.multiply(longRadix[radix]).add(valueOf(groupVal));
	}

	magnitude = tmp.magnitude;
    }


    /**
     * Translates the decimal String representation of a BigInteger into a
     * BigInteger.  The String representation consists of an optional minus
     * sign followed by a sequence of one or more decimal digits.  The
     * character-to-digit mapping is provided by <tt>Character.digit</tt>.
     * The String may not contain any extraneous characters (whitespace, for
     * example).
     *
     * @param val decimal String representation of BigInteger.
     * @throws NumberFormatException <tt>val</tt> is not a valid representation
     *	       of a BigInteger.
     * @see    Character#digit
     */
    public BigInteger(String val) {
	this(val, 10);
    }

    /**
     * Constructs a randomly generated BigInteger, uniformly distributed over
     * the range <tt>0</tt> to <tt>(2<sup>numBits</sup> - 1)</tt>, inclusive.
     * The uniformity of the distribution assumes that a fair source of random
     * bits is provided in <tt>rnd</tt>.  Note that this constructor always
     * constructs a non-negative BigInteger.
     *
     * @param  numBits maximum bitLength of the new BigInteger.
     * @param  rnd source of randomness to be used in computing the new
     *	       BigInteger.
     * @throws IllegalArgumentException <tt>numBits</tt> is negative.
     * @see #bitLength
     */
    public BigInteger(int numBits, Random rnd) {
	this(1, randomBits(numBits, rnd));
    }

    private static byte[] randomBits(int numBits, Random rnd) {
	if (numBits < 0)
	    throw new IllegalArgumentException("numBits must be non-negative");
	int numBytes = (numBits+7)/8;
	byte[] randomBits = new byte[numBytes];

	// Generate random bytes and mask out any excess bits
	if (numBytes > 0) {
	    rnd.nextBytes(randomBits);
	    int excessBits = 8*numBytes - numBits;
	    randomBits[0] &= (1 << (8-excessBits)) - 1;
	}
	return randomBits;
    }

    /**
     * Constructs a randomly generated positive BigInteger that is probably
     * prime, with the specified bitLength.
     *
     * @param  bitLength bitLength of the returned BigInteger.
     * @param  certainty a measure of the uncertainty that the caller is
     *         willing to tolerate.  The probability that the new BigInteger
     *	       represents a prime number will exceed
     *	       <tt>(1 - 1/2<sup>certainty</sup></tt>).  The execution time of
     *	       this constructor is proportional to the value of this parameter.
     * @param  rnd source of random bits used to select candidates to be
     *	       tested for primality.
     * @throws ArithmeticException <tt>bitLength &lt; 2</tt>.
     * @see    #bitLength
     */
    public BigInteger(int bitLength, int certainty, Random rnd) {
	if (bitLength < 2)
	    throw new ArithmeticException("bitLength < 2");

	BigInteger p;
	do {
            // Select a candidate of exactly the right length.  Note that
	    // Plumb's generator doesn't handle bitLength&lt;=16 properly.
	    do {
		p = new BigInteger(bitLength-1, rnd).setBit(bitLength-1);
		p = (bitLength <= 16
		     ? (bitLength > 2 ? p.setBit(0) : p)
		     : new BigInteger(plumbGeneratePrime(p.magnitude), 1));
	    } while (p.bitLength() != bitLength);
	} while (!p.isProbablePrime(certainty));

	signum = 1;
	magnitude = p.magnitude;
    }


    /**
     * This private constructor differs from its public cousin
     * with the arguments reversed in two ways: it assumes that its
     * arguments are correct, and it doesn't copy the magnitude array.
     */
    private BigInteger(byte[] magnitude, int signum) {
	this.signum = (magnitude.length==0 ? 0 : signum);
	this.magnitude = magnitude;
    }


    //Static Factory Methods

    /**
     * Returns a BigInteger whose value is equal to that of the specified
     * long.  This "static factory method" is provided in preference to a
     * (long) constructor because it allows for reuse of frequently used
     * BigIntegers. 
     *
     * @param  val value of the BigInteger to return.
     * @return a BigInteger with the specified value.
     */
    public static BigInteger valueOf(long val) {
	// If -MAX_CONSTANT < val < MAX_CONSTANT, return stashed constant
	if (val == 0)
	    return ZERO;
	if (val > 0 && val <= MAX_CONSTANT)
	    return posConst[(int) val];
	else if (val < 0 && val >= -MAX_CONSTANT)
	    return negConst[(int) -val];

	// Dump two's complement binary into valArray
	byte valArray[] = new byte[8];
	for (int i=0; i<8; i++, val >>= 8)
	    valArray[7-i] = (byte)val;
	return new BigInteger(valArray);
    }

    /**
     * Returns a BigInteger with the given two's complement representation.
     * Assumes that the input array will not be modified (the returned
     * BigInteger will reference the input array if feasible).
     */
    private static BigInteger valueOf(byte val[]) {
	return (val[0]>0 ? new BigInteger(val, 1) : new BigInteger(val));
    }

    // Constants

    /**
     * Initialize static constant array when class is loaded.
     */
    private final static int MAX_CONSTANT = 16;
    private static BigInteger posConst[] = new BigInteger[MAX_CONSTANT+1];
    private static BigInteger negConst[] = new BigInteger[MAX_CONSTANT+1];
    static {
	for (int i = 1; i <= MAX_CONSTANT; i++) {
	    byte[] magnitude = new byte[1];
	    magnitude[0] = (byte) i;
	    posConst[i] = new BigInteger(magnitude,  1);
	    negConst[i] = new BigInteger(magnitude, -1);
	}
    }

    /**
     * The BigInteger constant zero.
     *
     * @since   JDK1.2
     */
    public static final BigInteger ZERO = new BigInteger(new byte[0], 0);

    /**
     * The BigInteger constant one.
     *
     * @since   JDK1.2
     */
    public static final BigInteger ONE = valueOf(1);

    /**
     * The BigInteger constant two.  (Not exported.)
     */
    private static final BigInteger TWO = valueOf(2);


    // Arithmetic Operations

    /**
     * Returns a BigInteger whose value is <tt>(this + val)</tt>.
     *
     * @param  val value to be added to this BigInteger.
     * @return <tt>this + val</tt>
     */
    public BigInteger add(BigInteger val) {
	if (val.signum == 0)
	    return this;
	else if (this.signum == 0)
	    return val;
	else if (val.signum == signum)
	    return new BigInteger(plumbAdd(magnitude, val.magnitude), signum);
	else if (this.signum < 0)
	    return plumbSubtract(val.magnitude, magnitude);
	else  // val.signum < 0
	    return plumbSubtract(magnitude, val.magnitude);
    }

    /**
     * Returns a BigInteger whose value is <tt>(this - val)</tt>.
     *
     * @param  val value to be subtracted from this BigInteger.
     * @return <tt>this - val</tt>
     */
    public BigInteger subtract(BigInteger val) {
	return add(new BigInteger(val.magnitude, -val.signum));
    }

    /**
     * Returns a BigInteger whose value is <tt>(this * val)</tt>.
     *
     * @param  val value to be multiplied by this BigInteger.
     * @return <tt>this * val</tt>
     */
    public BigInteger multiply(BigInteger val) {
	if (val.signum == 0 || this.signum==0)
	    return ZERO;
	else
	    return new BigInteger(plumbMultiply(magnitude, val.magnitude),
				  signum * val.signum);
    }

    /**
     * Returns a BigInteger whose value is <tt>(this / val)</tt>.
     *
     * @param  val value by which this BigInteger is to be divided.
     * @return <tt>this / val</tt>
     * @throws ArithmeticException <tt>val==0</tt>
     */
    public BigInteger divide(BigInteger val) {
	if (val.signum == 0)
	    throw new ArithmeticException("BigInteger divide by zero");
	else if (this.signum == 0)
	    return ZERO;
	else
	    return new BigInteger(plumbDivide(magnitude, val.magnitude),
				  signum * val.signum);
    }

    /**
     * Returns a BigInteger whose value is <tt>(this % val)</tt>.
     *
     * @param  val value by which this BigInteger is to be divided, and the
     *	       remainder computed.
     * @return <tt>this % val</tt>
     * @throws ArithmeticException <tt>val==0</tt>
     */
    public BigInteger remainder(BigInteger val) {
	if (val.signum == 0)
	    throw new ArithmeticException("BigInteger divide by zero");
	else if (this.signum == 0)
	    return ZERO;
	else if (this.magnitude.length < val.magnitude.length)
	    return this; // *** WORKAROUND FOR BUG IN R1.1 OF PLUMB'S PKG ***
	else
	    return new BigInteger(plumbRemainder(magnitude,val.magnitude),
				  signum);
    }

    /**
     * Returns an array of two BigIntegers containing <tt>(this / val)</tt>
     * followed by <tt>(this % val)</tt>.
     *
     * @param  val value by which this BigInteger is to be divided, and the
     *	       remainder computed.
     * @return an array of two BigIntegers: the quotient <tt>(this / val)</tt>
     *	       is the initial element, and the remainder <tt>(this % val)</tt>
     *	       is the final element.
     * @throws ArithmeticException <tt>val==0</tt>
     */
    public BigInteger[] divideAndRemainder(BigInteger val) {
	BigInteger result[] = new BigInteger[2];

	if (val.signum == 0) {
	    throw new ArithmeticException("BigInteger divide by zero");
	} else if (this.signum == 0) {
	    result[0] = result[1] = ZERO;
	} else if (this.magnitude.length < val.magnitude.length) {
	    // *** WORKAROUND FOR A BUG IN R1.1 OF PLUMB'S PACKAGE ***
	    result[0] = ZERO;
	    result[1] = this;
	} else {
	    byte resultMagnitude[][] =
		plumbDivideAndRemainder(magnitude, val.magnitude);
	    result[0] = new BigInteger(resultMagnitude[0], signum*val.signum);
	    result[1] = new BigInteger(resultMagnitude[1], signum);
	}
	return result;
    }

    /**
     * Returns a BigInteger whose value is <tt>(this<sup>exponent</sup>)</tt>.
     * Returns one if this BigInteger and <tt>exponent</tt> are both zero.
     * Note that <tt>exponent</tt> is an integer rather than a BigInteger.
     *
     * @param  exponent exponent to which this BigInteger is to be raised.
     * @return <tt>this<sup>exponent</sup></tt>
     * @throws ArithmeticException <tt>exponent</tt> is negative.  (This would
     *	       cause the operation to yield a non-integer value.)
     */
    public BigInteger pow(int exponent) {
	if (exponent < 0)
	    throw new ArithmeticException("Negative exponent");
	if (signum==0)
	    return (exponent==0 ? ONE : this);

	/* Perform exponentiation using repeated squaring trick */
	BigInteger result = valueOf(exponent<0 && (exponent&1)==1 ? -1 : 1);
	BigInteger baseToPow2 = this;
	while (exponent != 0) {
	    if ((exponent & 1)==1)
		result = result.multiply(baseToPow2);
	    if ((exponent >>= 1) != 0)
		baseToPow2 = new BigInteger(
				    plumbSquare(baseToPow2.magnitude), 1);
	}
	return result;
    }

    /**
     * Returns a BigInteger whose value is the greatest common divisor of
     * <tt>abs(this)</tt> and <tt>abs(val)</tt>.  Returns 0 if
     * <tt>this==0 &amp;&amp; val==0</tt>.
     *
     * @param  val value with with the GCD is to be computed.
     * @return <tt>GCD(abs(this), abs(val))</tt>
     */
    public BigInteger gcd(BigInteger val) {
	if (val.signum == 0)
	    return this.abs();
	else if (this.signum == 0)
	    return val.abs();
	else
	    return new BigInteger(plumbGcd(magnitude, val.magnitude), 1);
    }

    /**
     * Returns a BigInteger whose value is the absolute value of this
     * BigInteger. 
     *
     * @return <tt>abs(this)</tt>
     */
    public BigInteger abs() {
	return (signum >= 0 ? this : this.negate());
    }

    /**
     * Returns a BigInteger whose value is <tt>(-this)</tt>.
     *
     * @return <tt>-this</tt>
     */
    public BigInteger negate() {
	return new BigInteger(this.magnitude, -this.signum);
    }

    /**
     * Returns the signum function of this BigInteger.
     *
     * @return -1, 0 or 1 as the value of this BigInteger is negative, zero or
     *	       positive.
     */
    public int signum() {
	return this.signum;
    }

    // Modular Arithmetic Operations

    /**
     * Returns a BigInteger whose value is <tt>(this mod m</tt>).  This method
     * differs from <tt>remainder</tt> in that it always returns a
     * <i>positive</i> BigInteger.
     *
     * @param  m the modulus.
     * @return <tt>this mod m</tt>
     * @throws ArithmeticException <tt>m &lt;= 0</tt>
     * @see    #remainder
     */
    public BigInteger mod(BigInteger m) {
	if (m.signum <= 0)
	    throw new ArithmeticException("BigInteger: modulus not positive");

	BigInteger result = this.remainder(m);
	return (result.signum >= 0 ? result : result.add(m));
    }

    /**
     * Returns a BigInteger whose value is
     * <tt>(this<sup>exponent</sup> mod m)</tt>.  (Unlike <tt>pow</tt>, this
     * method permits negative exponents.)
     *
     * @param  exponent the exponent.
     * @param  m the modulus.
     * @return <tt>this<sup>exponent</sup> mod m</tt>
     * @throws ArithmeticException <tt>m &lt;= 0</tt>
     * @see    #modInverse
     */
    public BigInteger modPow(BigInteger exponent, BigInteger m) {
	if (m.signum <= 0)
	    throw new ArithmeticException("BigInteger: modulus not positive");

	/* Workaround for a bug in Plumb: x^0 (y) dumps core for x != 0 */
	if (exponent.signum == 0)
            return (m.equals(ONE) ? ZERO : ONE);

	boolean invertResult;
	if ((invertResult = (exponent.signum < 0)))
	    exponent = exponent.negate();

	BigInteger base = (this.signum < 0 || this.compareTo(m) >= 0
			   ? this.mod(m) : this);
	BigInteger result;
	if (m.testBit(0)) { /* Odd modulus: just pass it on to Plumb */
	    result = new BigInteger
	     (plumbModPow(base.magnitude, exponent.magnitude, m.magnitude), 1);
	} else {
	    /*
	     * Even modulus.  Plumb only supports odd, so tear it into
	     * "odd part" (m1) and power of two (m2), use Plumb to exponentiate
	     * mod m1, manually exponentiate mod m2, and use Chinese Remainder
	     * Theorem to combine results.
	     */

	    /* Tear m apart into odd part (m1) and power of 2 (m2) */
	    int p = m.getLowestSetBit();      /* Max pow of 2 that divides m */
	    BigInteger m1 = m.shiftRight(p);  /* m/2**p */
	    BigInteger m2 = ONE.shiftLeft(p); /* 2**p */

            /* Calculate new base from m1 */
            BigInteger base2 = (this.signum < 0 || this.compareTo(m1) >= 0
                                ? this.mod(m1) : this);

            /* Caculate (base ** exponent) mod m1.*/
            BigInteger a1 = new BigInteger(plumbModPow(base2.magnitude,
                                       exponent.magnitude, m1.magnitude), 1);

	    /* Caculate (this ** exponent) mod m2 */
	    BigInteger a2 = base.modPow2(exponent, p);

	    /* Combine results using Chinese Remainder Theorem */
	    BigInteger y1 = m2.modInverse(m1);
	    BigInteger y2 = m1.modInverse(m2);
	    result = a1.multiply(m2).multiply(y1).add
		     (a2.multiply(m1).multiply(y2)).mod(m);
	}

	return (invertResult ? result.modInverse(m) : result);
    }

    /**
     * Returns a BigInteger whose value is (this ** exponent) mod (2**p)
     */
    private BigInteger modPow2(BigInteger exponent, int p) {
	/*
	 * Perform exponentiation using repeated squaring trick, chopping off
	 * high order bits as indicated by modulus.
	 */
	BigInteger result = valueOf(1);
	BigInteger baseToPow2 = this.mod2(p);
	while (exponent.signum != 0) {
	    if (exponent.testBit(0))
		result = result.multiply(baseToPow2).mod2(p);
	    exponent = exponent.shiftRight(1);
	    if (exponent.signum != 0)
		baseToPow2 = new BigInteger(
			       plumbSquare(baseToPow2.magnitude), 1).mod2(p);
	}
	return result;
    }

    /**
     * Returns a BigInteger whose value is this mod(2**p).
     * Assumes that this BigInteger &gt;= 0 and p &gt; 0.
     */
    private BigInteger mod2(int p) {
	if (bitLength() <= p)
	    return this;

	/* Copy remaining bytes of magnitude */
	int numBytes = (p+7)/8;
	byte[] mag = new byte[numBytes];
	for (int i=0; i<numBytes; i++)
	    mag[i] = magnitude[i + (magnitude.length - numBytes)];

	/* Mask out any excess bits */
	int excessBits = 8*numBytes - p;
	mag[0] &= (1 << (8-excessBits)) - 1;

	return (mag[0]==0 ? new BigInteger(1, mag) : new BigInteger(mag, 1));
    }

    /**
     * Returns a BigInteger whose value is <tt>(this<sup>-1</sup> mod m)</tt>.
     *
     * @param  m the modulus.
     * @return <tt>this<sup>-1</sup> mod m</tt>.
     * @throws ArithmeticException <tt> m &lt;= 0</tt>, or this BigInteger
     *	       has no multiplicative inverse mod m (that is, this BigInteger
     *	       is not <i>relatively prime</i> to m).
     */
    public BigInteger modInverse(BigInteger m) {
	if (m.signum != 1)
	    throw new ArithmeticException("BigInteger: modulus not positive");

	/* Calculate (this mod m) */
	BigInteger modVal = this.remainder(m);
	if (modVal.signum < 0)
	    modVal = modVal.add(m);
	if (!modVal.gcd(m).equals(ONE))
	    throw new ArithmeticException("BigInteger not invertible");

	return new BigInteger(plumbModInverse(modVal.magnitude,m.magnitude),1);
    }


    // Shift Operations

    /**
     * Returns a BigInteger whose value is <tt>(this &lt;&lt; n)</tt>.
     * The shift distance, <tt>n</tt>, may be negative, in which case
     * this method performs a right shift.
     * (Computes <tt>floor(this * 2<sup>n</sup>)</tt>.)
     *
     * @param  n shift distance, in bits.
     * @return <tt>this &lt;&lt; n</tt>
     * @see #shiftRight
     */
    public BigInteger shiftLeft(int n) {
	if (n==0)
	    return this;
	if (n<0)
	    return shiftRight(-n);

	int nBytes = n/8;
	int nBits = n%8;

	byte result[] = new byte[(bitLength()+n)/8+1];
	if (nBits == 0) {
	    for (int i=nBytes; i<result.length; i++)
		result[result.length-1-i] = getByte(i-nBytes);
	} else {
	    for (int i=nBytes; i<result.length; i++)
		result[result.length-1-i] = (byte)
		    ((getByte(i-nBytes) << nBits)
			| (i==nBytes ? 0
			   : ((getByte(i-nBytes-1)&0xff) >> (8-nBits))));
	}

	return valueOf(result);
    }

    /**
     * Returns a BigInteger whose value is <tt>(this &gt;&gt; n)</tt>.  Sign
     * extension is performed.  The shift distance, <tt>n</tt>, may be
     * negative, in which case this method performs a left shift.
     * (Computes <tt>floor(this / 2<sup>n</sup>)</tt>.) 
     *
     * @param  n shift distance, in bits.
     * @return <tt>this &gt;&gt; n</tt>
     * @see #shiftLeft
     */
    public BigInteger shiftRight(int n) {
	if (n==0)
	    return this;
	if (n<0)
	    return shiftLeft(-n);
	if (n >= bitLength())
	    return (signum<0 ? valueOf(-1) : ZERO);

	int nBytes = n/8;
	int nBits = n%8;

	byte result[] = new byte[(bitLength-n)/8+1];
	if (nBits == 0) {
	    for (int i=0; i<result.length; i++)
		result[result.length-1-i] = getByte(nBytes+i);
	} else {
	    for (int i=0; i<result.length; i++)
		result[result.length-1-i] = (byte)
		((getByte(nBytes+i+1)<<8 | (getByte(nBytes+i)&0xff)) >> nBits);
	}

	return valueOf(result);
    }


    // Bitwise Operations

    /**
     * Returns a BigInteger whose value is <tt>(this &amp; val)</tt>.  (This
     * method returns a negative BigInteger if and only if this and val are
     * both negative.)
     *
     * @param val value to be AND'ed with this BigInteger.
     * @return <tt>this &amp; val</tt>
     */
    public BigInteger and(BigInteger val) {
	byte[] result = new byte[Math.max(byteLength(), val.byteLength())];
	for (int i=0; i<result.length; i++)
	    result[i] = (byte) (getByte(result.length-i-1)
				& val.getByte(result.length-i-1));

	return valueOf(result);
    }

    /**
     * Returns a BigInteger whose value is <tt>(this | val)</tt>.  (This method
     * returns a negative BigInteger if and only if either this or val is
     * negative.) 
     *
     * @param val value to be OR'ed with this BigInteger.
     * @return <tt>this | val</tt>
     */
    public BigInteger or(BigInteger val) {
	byte[] result = new byte[Math.max(byteLength(), val.byteLength())];
	for (int i=0; i<result.length; i++)
	    result[i] = (byte) (getByte(result.length-i-1)
				| val.getByte(result.length-i-1));

	return valueOf(result);
    }

    /**
     * Returns a BigInteger whose value is <tt>(this ^ val)</tt>.  (This method
     * returns a negative BigInteger if and only if exactly one of this and
     * val are negative.)
     *
     * @param val value to be XOR'ed with this BigInteger.
     * @return <tt>this ^ val</tt>
     */
    public BigInteger xor(BigInteger val) {
	byte[] result = new byte[Math.max(byteLength(), val.byteLength())];
	for (int i=0; i<result.length; i++)
	    result[i] = (byte) (getByte(result.length-i-1)
				^ val.getByte(result.length-i-1));

	return valueOf(result);
    }

    /**
     * Returns a BigInteger whose value is <tt>(~this)</tt>.  (This method
     * returns a negative value if and only if this BigInteger is
     * non-negative.)
     *
     * @return <tt>~this</tt>
     */
    public BigInteger not() {
	byte[] result = new byte[byteLength()];
	for (int i=0; i<result.length; i++)
	    result[i] = (byte) ~getByte(result.length-i-1);

	return valueOf(result);
    }

    /**
     * Returns a BigInteger whose value is <tt>(this &amp; ~val)</tt>.  This
     * method, which is equivalent to <tt>and(val.not())</tt>, is provided as
     * a convenience for masking operations.  (This method returns a negative
     * BigInteger if and only if <tt>this</tt> is negative and <tt>val</tt> is
     * positive.)
     *
     * @param val value to be complemented and AND'ed with this BigInteger.
     * @return <tt>this &amp; ~val</tt>
     */
    public BigInteger andNot(BigInteger val) {
	byte[] result = new byte[Math.max(byteLength(), val.byteLength())];
	for (int i=0; i<result.length; i++)
	    result[i] = (byte) (getByte(result.length-i-1)
				& ~val.getByte(result.length-i-1));

	return valueOf(result);
    }


    // Single Bit Operations

    /**
     * Returns <tt>true</tt> if and only if the designated bit is set.
     * (Computes <tt>((this &amp; (1&lt;&lt;n)) != 0)</tt>.)
     *
     * @param  n index of bit to test.
     * @return <tt>true</tt> if and only if the designated bit is set.
     * @throws ArithmeticException <tt>n</tt> is negative.
     */
    public boolean testBit(int n) {
	if (n<0)
	    throw new ArithmeticException("Negative bit address");

	return (getByte(n/8) & (1 << (n%8))) != 0;
    }

    /**
     * Returns a BigInteger whose value is equivalent to this BigInteger
     * with the designated bit set.  (Computes <tt>(this | (1&lt;&lt;n))</tt>.)
     *
     * @param  n index of bit to set.
     * @return <tt>this | (1&lt;&lt;n)</tt>
     * @throws ArithmeticException <tt>n</tt> is negative.
     */
    public BigInteger setBit(int n) {
	if (n<0)
	    throw new ArithmeticException("Negative bit address");

	int byteNum = n/8;
	byte[] result = new byte[Math.max(byteLength(), byteNum+2)];

	for (int i=0; i<result.length; i++)
	    result[result.length-i-1] = getByte(i);

	result[result.length-byteNum-1] |= (1 << (n%8));

	return valueOf(result);
    }

    /**
     * Returns a BigInteger whose value is equivalent to this BigInteger
     * with the designated bit cleared.
     * (Computes <tt>(this &amp; ~(1&lt;&lt;n))</tt>.)
     *
     * @param  n index of bit to clear.
     * @return <tt>this & ~(1&lt;&lt;n)</tt>
     * @throws ArithmeticException <tt>n</tt> is negative.
     */
    public BigInteger clearBit(int n) {
	if (n<0)
	    throw new ArithmeticException("Negative bit address");

	int byteNum = n/8;
	byte[] result = new byte[Math.max(byteLength(), (n+1)/8+1)];

	for (int i=0; i<result.length; i++)
	    result[result.length-i-1] = getByte(i);

	result[result.length-byteNum-1] &= ~(1 << (n%8));

	return valueOf(result);
    }

    /**
     * Returns a BigInteger whose value is equivalent to this BigInteger
     * with the designated bit flipped.
     * (Computes <tt>(this ^ (1&lt;&lt;n))</tt>.)
     *
     * @param  n index of bit to flip.
     * @return <tt>this ^ (1&lt;&lt;n)</tt>
     * @throws ArithmeticException <tt>n</tt> is negative.
     */
    public BigInteger flipBit(int n) {
	if (n<0)
	    throw new ArithmeticException("Negative bit address");

	int byteNum = n/8;
	byte[] result = new byte[Math.max(byteLength(), byteNum+2)];

	for (int i=0; i<result.length; i++)
	    result[result.length-i-1] = getByte(i);

	result[result.length-byteNum-1] ^= (1 << (n%8));

	return valueOf(result);
    }

    /**
     * Returns the index of the rightmost (lowest-order) one bit in this
     * BigInteger (the number of zero bits to the right of the rightmost
     * one bit).  Returns -1 if this BigInteger contains no one bits.
     * (Computes <tt>(this==0? -1 : log<sub>2</sub>(this &amp; -this))</tt>.)
     *
     * @return index of the rightmost one bit in this BigInteger.
     */
    public int getLowestSetBit() {
	/*
	 * Initialize lowestSetBit field the first time this method is
	 * executed. This method depends on the atomicity of int modifies;
	 * without this guarantee, it would have to be synchronized.
	 */
	if (lowestSetBit == -2) {
	    if (signum == 0) {
		lowestSetBit = -1;
	    } else {
		/* Search for lowest order nonzero byte */
		int i;
		byte b;
		for (i=0; (b = getByte(i))==0; i++)
		    ;
		lowestSetBit = 8*i + trailingZeroCnt[b & 0xFF];
	    }
	}
	return lowestSetBit;
    }


    // Miscellaneous Bit Operations

    /**
     * Returns the number of bits in the minimal two's-complement
     * representation of this BigInteger, <i>excluding</i> a sign bit.
     * For positive BigIntegers, this is equivalent to the number of bits in
     * the ordinary binary representation.  (Computes
     * <tt>(ceil(log<sub>2</sub>(this &lt; 0 ? -this : this+1)))</tt>.)
     *
     * @return number of bits in the minimal two's-complement
     *         representation of this BigInteger, <i>excluding</i> a sign bit.
     */
    public int bitLength() {
	/*
	 * Initialize bitLength field the first time this method is executed.
	 * This method depends on the atomicity of int modifies; without
	 * this guarantee, it would have to be synchronized.
	 */
	if (bitLength == -1) {
	    if (signum == 0) {
		bitLength = 0;
	    } else {
		/* Calculate the bit length of the magnitude */
		int magBitLength = 8*(magnitude.length-1)
		    		   + bitLen[magnitude[0] & 0xff];

		if (signum < 0) {
		    /* Check if magnitude is a power of two */
		    boolean pow2 = (bitCnt[magnitude[0]&0xff] == 1);
		    for(int i=1; i<magnitude.length && pow2; i++)
			pow2 = (magnitude[i]==0);

		    bitLength = (pow2 ? magBitLength-1 : magBitLength);
		} else {
		    bitLength = magBitLength;
		}
	    }
	}
	return bitLength;
    }

    /*
     * bitLen[i] is the number of bits in the binary representation of i.
     */
    private final static byte bitLen[] = {
	0, 1, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4,
	5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
	6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
	6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
	7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
	7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
	7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
	7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
	8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
	8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
	8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
	8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
	8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
	8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
	8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
	8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8};

    /**
     * Returns the number of bits in the two's complement representation
     * of this BigInteger that differ from its sign bit.  This method is
     * useful when implementing bit-vector style sets atop BigIntegers.
     *
     * @return number of bits in the two's complement representation
     *         of this BigInteger that differ from its sign bit.
     */
    public int bitCount() {
	/*
	 * Initialize bitCount field the first time this method is executed.
	 * This method depends on the atomicity of int modifies; without
	 * this guarantee, it would have to be synchronized.
	 */
	if (bitCount == -1) {
	    /* Count the bits in the magnitude */
	    int magBitCount = 0;
	    for (int i=0; i<magnitude.length; i++)
		magBitCount += bitCnt[magnitude[i] & 0xff];

	    if (signum < 0) {
		/* Count the trailing zeros in the magnitude */
		int magTrailingZeroCount = 0, j;
		for (j=magnitude.length-1; magnitude[j]==0; j--)
		    magTrailingZeroCount += 8;
		magTrailingZeroCount += trailingZeroCnt[magnitude[j] & 0xff];

		bitCount = magBitCount + magTrailingZeroCount - 1;
	    } else {
		bitCount = magBitCount;
	    }
	}
	return bitCount;
    }

    /*
     * bitCnt[i] is the number of 1 bits in the binary representation of i.
     */
    private final static byte bitCnt[] = {
	0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4,
	1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
	1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
	2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
	1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
	2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
	2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
	3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
	1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
	2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
	2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
	3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
	2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
	3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
	3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
	4, 5, 5, 6, 5, 6, 6, 7, 5, 6, 6, 7, 6, 7, 7, 8};

    /*
     * trailingZeroCnt[i] is the number of trailing zero bits in the binary
     * representaion of i.
     */
    private final static byte trailingZeroCnt[] = {
	8, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
	4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
	5, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
	4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
	6, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
	4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
	5, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
	4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
	7, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
	4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
	5, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
	4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
	6, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
	4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
	5, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
	4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0};



    // Primality Testing

    /**
     * Returns <tt>true</tt> if this BigInteger is probably prime,
     * <tt>false</tt> if it's definitely composite.
     *
     * @param  certainty a measure of the uncertainty that the caller is
     *	       willing to tolerate: if the call returns <tt>true</tt>
     *	       the probability that this BigInteger is prime exceeds
     *	       <tt>(1 - 1/2<sup>certainty</sup>)</tt>.  The execution time of
     * 	       this method is proportional to the value of this parameter.
     * @return <tt>true</tt> if this BigInteger is probably prime,
     * 	       <tt>false</tt> if it's definitely composite.
     */
    public boolean isProbablePrime(int certainty) {
	/*
	 * This test is taken from the DSA spec.
	 */
	int n = (certainty+1)/2;
	if (n <= 0)
	    return true;
	BigInteger w = this.abs();
	if (w.equals(TWO))
	    return true;
	if (!w.testBit(0) || w.equals(ONE))
	    return false;

	// Find a and m such that m is odd and w == 1 + 2**a * m
	BigInteger m = w.subtract(ONE);
	int a = m.getLowestSetBit();
	m = m.shiftRight(a);

	// Do the tests
        Random rnd = new Random();
	for(int i=0; i<n; i++) {
	    /* Generate a uniform random on (1, w) */
	    BigInteger b;
	    do {
		b = new BigInteger(w.bitLength(), rnd);
	    } while (b.compareTo(ONE) <= 0 || b.compareTo(w) >= 0);

	    int j = 0;
	    BigInteger z = b.modPow(m, w);
	    while(!((j==0 && z.equals(ONE)) || z.equals(w.subtract(ONE)))) {
		if (j>0 && z.equals(ONE) || ++j==a)
		    return false;
		z = z.modPow(TWO, w);
	    }
	}
	return true;
    }

    // Comparison Operations

    /**
     * Compares this BigInteger with the specified BigInteger.  This method is
     * provided in preference to individual methods for each of the six
     * boolean comparison operators (&lt;, ==, &gt;, &gt;=, !=, &lt;=).  The
     * suggested idiom for performing these comparisons is:
     * <tt>(x.compareTo(y)</tt> &lt;<i>op</i>&gt; <tt>0)</tt>,
     * where &lt;<i>op</i>&gt; is one of the six comparison operators.
     *
     * @param  val BigInteger to which this BigInteger is to be compared.
     * @return -1, 0 or 1 as this BigInteger is numerically less than, equal
     *         to, or greater than <tt>val</tt>.
     */
    public int compareTo(BigInteger val) {
	return (signum==val.signum
		? signum*byteArrayCmp(magnitude, val.magnitude)
		: (signum>val.signum ? 1 : -1));
    }

    /**
     * Compares this BigInteger with the specified Object.  If the Object is a
     * BigInteger, this method behaves like <tt>compareTo(BigInteger)</tt>.
     * Otherwise, it throws a <tt>ClassCastException</tt> (as BigIntegers are
     * comparable only to other BigIntegers).
     *
     * @param   o Object to which this BigInteger is to be compared.
     * @return  a negative number, zero, or a positive number as this
     *		BigInteger is numerically less than, equal to, or greater
     *		than <tt>o</tt>, which must be a BigInteger.
     * @throws  ClassCastException <tt>o</tt> is not a BigInteger.
     * @see     #compareTo(java.math.BigInteger)
     * @see     Comparable
     * @since   JDK1.2
     */
    public int compareTo(Object o) {
	return compareTo((BigInteger)o);
    }

    /*
     * Returns -1, 0 or +1 as big-endian unsigned byte array arg1 is
     * less than, equal to, or greater than arg2.
     */
    private static int byteArrayCmp(byte[] arg1, byte[] arg2) {
	if (arg1.length < arg2.length)
	    return -1;
	if (arg1.length > arg2.length)
	    return 1;

	/* Argument lengths are equal; compare the values */
	for (int i=0; i<arg1.length; i++) {
	    int b1 = arg1[i] & 0xff;
	    int b2 = arg2[i] & 0xff;
	    if (b1 < b2)
		return -1;
	    if (b1 > b2)
		return 1;
	}
	return 0;
    }

    /**
     * Compares this BigInteger with the specified Object for equality.
     *
     * @param  o Object to which this BigInteger is to be compared.
     * @return <tt>true</tt> if and only if the specified Object is a
     *	       BigInteger whose value is numerically equal to this BigInteger.
     */
    public boolean equals(Object x) {
	// This test is just an optimization, which may or may not help
	if (x == this)
	    return true;

	if (!(x instanceof BigInteger))
	    return false;
	BigInteger xInt = (BigInteger) x;

	if (xInt.signum != signum || xInt.magnitude.length != magnitude.length)
	    return false;

	for (int i=0; i<magnitude.length; i++)
	    if (xInt.magnitude[i] != magnitude[i])
		return false;

	return true;
    }

    /**
     * Returns the minimum of this BigInteger and <tt>val</tt>.
     *
     * @param  val value with with the minimum is to be computed.
     * @return the BigInteger whose value is the lesser of this BigInteger and 
     *	       <tt>val</tt>.  If they are equal, either may be returned.
     */
    public BigInteger min(BigInteger val) {
	return (compareTo(val)<0 ? this : val);
    }

    /**
     * Returns the maximum of this BigInteger and <tt>val</tt>.
     *
     * @param  val value with with the maximum is to be computed.
     * @return the BigInteger whose value is the greater of this and
     *         <tt>val</tt>.  If they are equal, either may be returned.
     */
    public BigInteger max(BigInteger val) {
	return (compareTo(val)>0 ? this : val);
    }


    // Hash Function

    /**
     * Returns the hash code for this BigInteger.
     *
     * @return hash code for this BigInteger.
     */
    public int hashCode() {
	int hashCode = 0;

	for (int i=0; i<magnitude.length; i++)
	    hashCode = 31*hashCode + (magnitude[i] & 0xff);

	return hashCode * signum;
    }

    // Format Converters

    /**
     * Returns the String representation of this BigInteger in the given radix.
     * If the radix is outside the range from <tt>Character.MIN_RADIX</tt> (2)
     * to <tt>Character.MAX_RADIX</tt> (36) inclusive, it will default to 10
     * (as is the case for <tt>Integer.toString</tt>).  The digit-to-character
     * mapping provided by <tt>Character.forDigit</tt> is used, and a minus
     * sign is prepended if appropriate.  (This representation is compatible
     * with the (String, int) constructor.)
     *
     * @param  radix  radix of the String representation.
     * @return String representation of this BigInteger in the given radix.
     * @see    Integer#toString
     * @see    Character#forDigit
     * @see    #BigInteger(java.lang.String, int)
     */
    public String toString(int radix) {
	if (signum == 0)
	    return "0";
	if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
	    radix = 10;

	/* Compute upper bound on number of digit groups and allocate space */
	int maxNumDigitGroups = (magnitude.length + 6)/7;
	String digitGroup[] = new String[maxNumDigitGroups];

	/* Translate number to string, a digit group at a time */
	BigInteger tmp = this.abs();
	int numGroups = 0;
	while (tmp.signum != 0) {
	    BigInteger b[] = tmp.divideAndRemainder(longRadix[radix]);
	    digitGroup[numGroups++] = Long.toString(b[1].longValue(), radix);
	    tmp = b[0];
	}

	/* Put sign (if any) and first digit group into result buffer */
	StringBuffer buf = new StringBuffer(numGroups*digitsPerLong[radix]+1);
	if (signum<0)
	    buf.append('-');
	buf.append(digitGroup[numGroups-1]);

	/* Append remaining digit groups padded with leading zeros */
	for (int i=numGroups-2; i>=0; i--) {
	    /* Prepend (any) leading zeros for this digit group */
	    int numLeadingZeros = digitsPerLong[radix]-digitGroup[i].length();
	    if (numLeadingZeros != 0)
		buf.append(zeros[numLeadingZeros]);
	    buf.append(digitGroup[i]);
	}

	return buf.toString();
    }

    /* zero[i] is a string of i consecutive zeros. */
    private static String zeros[] = new String[64];
    static {
	zeros[63] =
	    "000000000000000000000000000000000000000000000000000000000000000";
	for (int i=0; i<63; i++)
	    zeros[i] = zeros[63].substring(0, i);
    }

    /**
     * Returns the decimal String representation of this BigInteger.  The
     * digit-to-character mapping provided by <tt>Character.forDigit</tt> is
     * used, and a minus sign is prepended if appropriate.  (This
     * representation is compatible with the (String) constructor, and allows
     * for String concatenation with Java's + operator.)
     *
     * @return decimal String representation of this BigInteger.
     * @see    Character#forDigit
     * @see    #BigInteger(java.lang.String)
     */
    public String toString() {
	return toString(10);
    }

    /**
     * Returns a byte array containing the two's-complement representation of
     * this BigInteger.  The byte array will be in <i>big-endian</i>
     * byte-order: the most significant byte is in the zeroth element.  The
     * array will contain the minimum number of bytes required to represent
     * this BigInteger, including at least one sign bit,  which is
     * <tt>(ceil((this.bitLength() + 1)/8))</tt>.  (This representation is
     * compatible with the (byte[]) constructor.) 
     *
     * @return a byte array containing the two's-complement representation of
     *	       this BigInteger.
     * @see    #BigInteger(byte[])
     */
    public byte[] toByteArray() {
	byte[] result = new byte[byteLength()];
	for(int i=0; i<result.length; i++)
	    result[i] = getByte(result.length-i-1);

	return result;
    }

    /**
     * Converts this BigInteger to an int.  Standard <i>narrowing primitive
     * conversion</i> as defined in <i>The Java Language Specification</i>:
     * if this BigInteger is too big to fit in an int, only the low-order
     * 32 bits are returned.
     *
     * @return this BigInteger converted to an int.
     */
    public int intValue() {
	int result = 0;

	for (int i=3; i>=0; i--)
	    result = (result << 8) + (getByte(i) & 0xff);
	return result;
    }

    /**
     * Converts this BigInteger to a long.  Standard <i>narrowing primitive
     * conversion</i> as defined in <i>The Java Language Specification</i>:
     * if this BigInteger is too big to fit in a long, only the low-order
     * 64 bits are returned.
     *
     * @return this BigInteger converted to a long.
     */
    public long longValue() {
	long result = 0;

	for (int i=7; i>=0; i--)
	    result = (result << 8) + (getByte(i) & 0xff);
	return result;
    }

    /**
     * Converts this BigInteger to a float.  Similar to the double-to-float
     * <i>narrowing primitive conversion</i> defined in <i>The Java Language
     * Specification</i>: if this BigInteger has too great a magnitude to
     * represent as a float, it will be converted to infinity or negative
     * infinity, as appropriate.
     *
     * @return this BigInteger converted to a float.
     */
    public float floatValue() {
	// Somewhat inefficient, but guaranteed to work.
	return Float.valueOf(this.toString()).floatValue();
    }

    /**
     * Converts this BigInteger to a double.  Similar to the double-to-float
     * <i>narrowing primitive conversion</i> defined in <i>The Java Language
     * Specification</i>: if this BigInteger has too great a magnitude to
     * represent as a double, it will be converted to infinity or negative
     * infinity, as appropriate.
     *
     * @return this BigInteger converted to a double.
     */
    public double doubleValue() {
	// Somewhat inefficient, but guaranteed to work.
	return Double.valueOf(this.toString()).doubleValue();
    }

    static {
	java.security.AccessController.doPrivileged(
		  new sun.security.action.LoadLibraryAction("math"));
	plumbInit();
    }


    /**
     * Returns a copy of the input array stripped of any leading zero bytes.
     */
    static private byte[] stripLeadingZeroBytes(byte a[]) {
	int keep;

	/* Find first nonzero byte */
	for (keep=0; keep<a.length && a[keep]==0; keep++)
	    ;

	/* Allocate new array and copy relevant part of input array */
	byte result[] = new byte[a.length - keep];
	for (int i = keep; i<a.length; i++)
	    result[i - keep] = a[i];

	return result;
    }

    /**
     * Takes an array a representing a negative 2's-complement number and
     * returns the minimal (no leading zero bytes) unsigned whose value is -a.
     */
    static private byte[] makePositive(byte a[]) {
	int keep, j;

	/* Find first non-sign (0xff) byte of input */
	for (keep=0; keep<a.length && a[keep]==-1; keep++)
	    ;

	/* Allocate output array.  If all non-sign bytes are 0x00, we must
	 * allocate space for one extra output byte. */
	for (j=keep; j<a.length && a[j]==0; j++)
	    ;
	int extraByte = (j==a.length ? 1 : 0);
	byte result[] = new byte[a.length - keep + extraByte];

	/* Copy one's complement of input into into output, leaving extra
	 * byte (if it exists) == 0x00 */
	for (int i = keep; i<a.length; i++)
	    result[i - keep + extraByte] = (byte) ~a[i];

	/* Add one to one's complement to generate two's complement */
	for (int i=result.length-1; ++result[i]==0; i--)
	    ;

	return result;
    }

    /*
     * The following two arrays are used for fast String conversions.  Both
     * are indexed by radix.  The first is the number of digits of the given
     * radix that can fit in a Java long without "going negative", i.e., the
     * highest integer n such that radix**n < 2**63.  The second is the
     * "long radix" that tears each number into "long digits", each of which
     * consists of the number of digits in the corresponding element in
     * digitsPerLong (longRadix[i] = i**digitPerLong[i]).  Both arrays have
     * nonsense values in their 0 and 1 elements, as radixes 0 and 1 are not
     * used.
     */

    private static int digitsPerLong[] = {0, 0,
	62, 39, 31, 27, 24, 22, 20, 19, 18, 18, 17, 17, 16, 16, 15, 15, 15, 14,
	14, 14, 14, 13, 13, 13, 13, 13, 13, 12, 12, 12, 12, 12, 12, 12, 12};

    private static BigInteger longRadix[] = {null, null,
        valueOf(0x4000000000000000L), valueOf(0x383d9170b85ff80bL),
	valueOf(0x4000000000000000L), valueOf(0x6765c793fa10079dL),
	valueOf(0x41c21cb8e1000000L), valueOf(0x3642798750226111L),
        valueOf(0x1000000000000000L), valueOf(0x12bf307ae81ffd59L),
	valueOf( 0xde0b6b3a7640000L), valueOf(0x4d28cb56c33fa539L),
	valueOf(0x1eca170c00000000L), valueOf(0x780c7372621bd74dL),
	valueOf(0x1e39a5057d810000L), valueOf(0x5b27ac993df97701L),
	valueOf(0x1000000000000000L), valueOf(0x27b95e997e21d9f1L),
	valueOf(0x5da0e1e53c5c8000L), valueOf( 0xb16a458ef403f19L),
	valueOf(0x16bcc41e90000000L), valueOf(0x2d04b7fdd9c0ef49L),
	valueOf(0x5658597bcaa24000L), valueOf( 0x6feb266931a75b7L),
	valueOf( 0xc29e98000000000L), valueOf(0x14adf4b7320334b9L),
	valueOf(0x226ed36478bfa000L), valueOf(0x383d9170b85ff80bL),
	valueOf(0x5a3c23e39c000000L), valueOf( 0x4e900abb53e6b71L),
	valueOf( 0x7600ec618141000L), valueOf( 0xaee5720ee830681L),
	valueOf(0x1000000000000000L), valueOf(0x172588ad4f5f0981L),
	valueOf(0x211e44f7d02c1000L), valueOf(0x2ee56725f06e5c71L),
	valueOf(0x41c21cb8e1000000L)};


    /**
     * These routines provide access to the two's complement representation
     * of BigIntegers.
     */

    /**
     * Returns the length of the two's complement representation in bytes,
     * including space for at least one sign bit,
     */
    private int byteLength() {
	return bitLength()/8 + 1;
    }

    /* Returns sign bit */
    private int signBit() {
	return (signum < 0 ? 1 : 0);
    }

    /* Returns a byte of sign bits */
    private byte signByte() {
	return (byte) (signum < 0 ? -1 : 0);
    }

    /**
     * Returns the specified byte of the little-endian two's complement
     * representation (byte 0 is the LSB).  The byte number can be arbitrarily
     * high (values are logically preceded by infinitely many sign bytes).
     */
    private byte getByte(int n) {
	if (n >= magnitude.length)
	    return signByte();

	byte magByte = magnitude[magnitude.length-n-1];

	return (byte) (signum >= 0 ? magByte :
		       (n <= firstNonzeroByteNum() ? -magByte : ~magByte));
    }

    /**
     * Returns the index of the first nonzero byte in the little-endian
     * binary representation of the magnitude (byte 0 is the LSB).  If
     * the magnitude is zero, return value is undefined.
     */

     private int firstNonzeroByteNum() {
	/*
	 * Initialize firstNonzeroByteNum field the first time this method is
	 * executed. This method depends on the atomicity of int modifies;
	 * without this guarantee, it would have to be synchronized.
	 */
	if (firstNonzeroByteNum == -2) {
	    /* Search for the first nonzero byte */
	    int i;
	    for (i=magnitude.length-1; i>=0 && magnitude[i]==0; i--)
		;
	    firstNonzeroByteNum = magnitude.length-i-1;
	}
	return firstNonzeroByteNum;
    }


    /**
     * Native method wrappers for Colin Plumb's big positive integer package.
     * Each of these wrappers (except for plumbInit) behaves as follows:
     *
     * 	1) Translate input arguments from java byte arrays into plumbNums.
     *
     *  2) Perform the requested computation.
     *
     *  3) Translate result(s) into Java byte array(s).  (The subtract
     *	   operation translates its result into a BigInteger, as its result
     *	   is, effectively, signed.)
     *
     *	4) Deallocate all of the plumbNums.
     *
     *  5) Return the resulting Java array(s) (or, in the case of subtract,
     *	   BigInteger).
     */

    private static native void plumbInit();
    private static native byte[] plumbAdd(byte[] a, byte[] b);
    private static native BigInteger plumbSubtract(byte[] a, byte[] b);
    private static native byte[] plumbMultiply(byte[] a, byte[] b);
    private static native byte[] plumbDivide(byte[] a, byte[] b);
    private static native byte[] plumbRemainder(byte[] a, byte[] b);
    private static native byte[][] plumbDivideAndRemainder(byte[] a, byte[] b);
    private static native byte[] plumbGcd(byte[] a, byte[] b);
    private static native byte[] plumbModPow(byte[] a, byte[] b, byte[] m);
    private static native byte[] plumbModInverse(byte[] a, byte[] m);
    private static native byte[] plumbSquare(byte[] a);
    private static native byte[] plumbGeneratePrime(byte[] a);

    /** use serialVersionUID from JDK 1.1. for interoperability */
    private static final long serialVersionUID = -8287574255936472291L;

    /**
     * Reconstitute the <tt>BigInteger</tt> instance from a stream (that is,
     * deserialize it).
     */
    private synchronized void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {
        // Read in all fields
	s.defaultReadObject();

        // Defensively copy magnitude to ensure immutability
        magnitude = (byte[]) magnitude.clone();

        // Validate signum
	if (signum < -1 || signum > 1)
	    throw new java.io.StreamCorruptedException(
                        "BigInteger: Invalid signum value");
	if ((magnitude.length==0) != (signum==0))
	    throw new java.io.StreamCorruptedException(
                        "BigInteger: signum-magnitude mismatch");

        // Set "cached computation" fields to their initial values
        bitCount = bitLength = -1;
        lowestSetBit = firstNonzeroByteNum = -2;
    }
}
