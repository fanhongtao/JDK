/*
 * @(#)BigInteger.java	1.11 99/02/09
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
 * primitive integer types).  BigIntegers provide analogues to all of Java's
 * primitive integer operators, and all relevant static methods from
 * java.lang.Math.  Additionally, BigIntegers provide operations for modular
 * arithmetic, GCD calculation, primality testing, prime generation,
 * single-bit manipulation, and a few other odds and ends.
 *
 * <P>Semantics of arithmetic operations exactly mimic those of java's integer
 * arithmetic operators, as defined in The Java Language Specification.  For
 * example, division by zero throws an ArithmeticException, and division of
 * a negative by a positive yields a negative (or zero) remainder.  All of
 * the details in the Spec concerning overflow are ignored, as BigIntegers
 * are made as large as necessary to accommodate the results of an operation.
 *
 * <P>Semantics of shift operations extend those of Java's shift operators
 * to allow for negative shift distances.  A right-shift with a negative
 * shift distance results in a left shift, and vice-versa.  The unsigned
 * right shift operator (>>>) is omitted, as this operation makes little
 * sense in combination with the "infinite word size" abstraction provided
 * by this class.
 *
 * <P>Semantics of bitwise logical operations are are exactly mimic those of
 * Java's bitwise integer operators.  The Binary operators (and, or, xor)
 * implicitly perform sign extension on the shorter of the two operands
 * prior to performing the operation.
 *
 * <P>Comparison operations perform signed integer comparisons, analogous to
 * those performed by java's relational and equality operators.
 *
 * <P>Modular arithmetic operations are provided to compute residues, perform
 * exponentiation, and compute multiplicative inverses.  These methods always
 * return a non-negative result, between 0 and (modulus - 1), inclusive.
 *
 * <P>Single-bit operations operate on a single bit of the two's-complement
 * representation of their operand.  If necessary, the operand is sign
 * extended so that it contains the designated bit.  None of the single-bit
 * operations can produce a number with a different sign from the the
 * BigInteger being operated on, as they affect only a single bit, and the
 * "infinite word size" abstraction provided by this class ensures that there
 * are infinitely many "virtual sign bits" preceding each BigInteger.
 *
 *
 * @see BigDecimal
 * @version 	1.11, 99/02/09
 * @author      Josh Bloch
 */
public class BigInteger extends Number {

    /*
     * The number is internally stored in "minimal" sign-magnitude format
     * (i.e., no BigIntegers have a leading zero byte in their magnitudes).
     * Zero is represented with a signum of 0 (and a zero-length magnitude).
     * Thus, there is exactly one representation for each value.
     */
    private int signum;
    private byte[] magnitude;

    /*
     * These "redundant fields" are initialized with recognizable nonsense
     * values, and cached the first time they are needed (or never, if they
     * aren't needed).
     */
    private int bitCount =  -1;
    private int bitLength = -1;
    private int firstNonzeroByteNum = -2;  /* Only used for negative numbers */
    private int lowestSetBit = -2;

    //Constructors

    /**
     * Translates a byte array containing the two's-complement representation
     * of a (signed) integer into a BigInteger.  The input array is assumed to
     * be big-endian (i.e., the most significant byte is in the [0] position).
     * (The most significant bit of the most significant byte is the sign bit.)
     * The array must contain at least one byte or a NumberFormatException
     * will be thrown.
     */
    public BigInteger(byte[] val) throws NumberFormatException{
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
     * Translates the sign-magnitude representation of an integer into a
     * BigInteger.  The sign is represented as an integer signum value (-1 for
     * negative, 0 for zero, 1 for positive).  The magnitude is represented
     * as a big-endian byte array (i.e., the most significant byte is in the
     * [0] position).  An invalid signum value or a 0 signum value coupled
     * with a nonzero magnitude will result in a NumberFormatException.
     * A zero length magnitude array is permissible, and will result in
     * in a value of 0 (irrespective of the given signum value).
     */
    public BigInteger(int signum, byte[] magnitude)
    throws NumberFormatException{
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
     * Translates a string containing an optional minus sign followed by a
     * sequence of one or more digits in the specified radix into a BigInteger.
     * The character-to-digit mapping is provided by Character.digit.
     * Any extraneous characters (including whitespace), or a radix outside
     * the range from Character.MIN_RADIX(2) to Character.MAX_RADIX(36),
     * inclusive, will result in a NumberFormatException.
     */
    public BigInteger(String val, int radix) throws NumberFormatException {
	int cursor = 0, numDigits;

	if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
	    throw new NumberFormatException("Radix out of range");
	if (val.length() == 0)
	    throw new NumberFormatException("Zero length BigInteger");

	/* Check for leading minus sign */
	signum = 1;
	if (val.charAt(0) == '-') {
	    if (val.length() == 1)
		throw new NumberFormatException("Zero length BigInteger");
	    signum = -1;
	    cursor = 1;
	}

	/* Skip leading zeros and compute number of digits in magnitude */
	while (cursor<val.length() && val.charAt(cursor)==ZERO_CHAR)
	    cursor++;
	if (cursor==val.length()) {
	    signum = 0;
	    magnitude = new byte[0];
	    return;
	} else {
	    numDigits = val.length() - cursor;
	}

	/* Process first (potentially short) digit group, if necessary */
	int firstGroupLen = numDigits % digitsPerLong[radix];
	if (firstGroupLen == 0)
	    firstGroupLen = digitsPerLong[radix];
	String group = val.substring(cursor, cursor += firstGroupLen);
	BigInteger tmp = valueOf(Long.parseLong(group, radix));

	/* Process remaining digit groups */
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
     * Translates a string containing an optional minus sign followed by a
     * sequence of one or more decimal digits into a BigInteger.  The
     * character-to-digit mapping is provided by Character.digit.
     * Any extraneous characters (including whitespace) will result in a
     * NumberFormatException. 
     */
    public BigInteger(String val) throws NumberFormatException {
	this(val, 10);
    }

    /**
     * Returns a random number uniformly distributed on [0, 2**numBits - 1]
     * (assuming a fair source of random bits is provided in rndSrc).
     * Note that this constructor always returns a non-negative BigInteger.
     * Throws an IllegalArgumentException if numBits < 0.
     */
    public BigInteger(int numBits, Random rndSrc)
    throws IllegalArgumentException {
	this(1, randomBits(numBits, rndSrc));
    }

    private static byte[] randomBits(int numBits, Random rndSrc)
    throws IllegalArgumentException {
	if (numBits < 0)
	    throw new IllegalArgumentException("numBits must be non-negative");
	int numBytes = (numBits+7)/8;
	byte[] randomBits = new byte[numBytes];

	/* Generate random bytes and mask out any excess bits */
	if (numBytes > 0) {
	    rndSrc.nextBytes(randomBits);
	    int excessBits = 8*numBytes - numBits;
	    randomBits[0] &= (1 << (8-excessBits)) - 1;
	}
	return randomBits;
    }


    /**
     * Returns a randomly selected BigInteger with the specified bitLength
     * that is probably prime.  The certainty parameter is a measure of
     * the uncertainty that the caller is willing to tolerate: the probability
     * that the number is prime will exceed 1 - 1/2**certainty.  The execution
     * time is proportional to the value of the certainty parameter.  The
     * given random number generator is used to select candidates to be
     * tested for primality.  Throws an ArithmeticException if bitLength < 2.
     */
    public BigInteger(int bitLength, int certainty, Random rnd) {
	if (bitLength < 2)
	    throw new ArithmeticException("bitLength < 2");

	BigInteger p;
	do {
	    /*
	     * Select a candidate of exactly the right length.  Note that
	     * Plumb's generator doesn't handle bitLength<=16 properly.
	     */
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
     * Returns a BigInteger with the specified value.  This factory is provided
     * in preference to a (long) constructor because it allows for reuse
     * of frequently used BigIntegers (like 0 and 1), obviating the need for
     * exported constants.
     */
    public static BigInteger valueOf(long val) {
	/* If -MAX_CONSTANT < val < MAX_CONSTANT, return stashed constant */
	if (val == 0)
	    return ZERO;
	if (val > 0 && val <= MAX_CONSTANT)
	    return posConst[(int) val];
	else if (val < 0 && val >= -MAX_CONSTANT)
	    return negConst[(int) -val];

	/* Dump two's complement binary into valArray */
	byte valArray[] = new byte[8];
	for (int i=0; i<8; i++, val >>= 8)
	    valArray[7-i] = (byte)val;
	return new BigInteger(valArray);
    }

    private final static BigInteger ZERO = new BigInteger(new byte[0], 0);

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
     * Returns a BigInteger with the given two's complement representation.
     * Assumes that the input array will not be modified (the returned
     * BigInteger will reference the input array if feasible).
     */
    private static BigInteger valueOf(byte val[]) {
	return (val[0]>0 ? new BigInteger(val, 1) : new BigInteger(val));
    }


    // Arithmetic Operations

    /**
     * Returns a BigInteger whose value is (this + val).
     */
    public BigInteger add(BigInteger val) throws ArithmeticException {
	if (val.signum == 0)
	    return this;
	else if (this.signum == 0)
	    return val;
	else if (val.signum == signum)
	    return new BigInteger(plumbAdd(magnitude, val.magnitude), signum);
	else if (this.signum < 0)
	    return plumbSubtract(val.magnitude, magnitude);
	else  /* val.signum < 0 */
	    return plumbSubtract(magnitude, val.magnitude);
    }

    /**
     * Returns a BigInteger whose value is (this - val).
     */
    public BigInteger subtract(BigInteger val) {
	return add(new BigInteger(val.magnitude, -val.signum));
    }

    /**
     * Returns a BigInteger whose value is (this * val).
     */
    public BigInteger multiply(BigInteger val) {

	if (val.signum == 0 || this.signum==0)
	    return ZERO;
	else
	    return new BigInteger(plumbMultiply(magnitude, val.magnitude),
				  signum * val.signum);
    }

    /**
     * Returns a BigInteger whose value is (this / val).  Throws an
     * ArithmeticException if val == 0.
     */
    public BigInteger divide(BigInteger val) throws ArithmeticException {

	if (val.signum == 0)
	    throw new ArithmeticException("BigInteger divide by zero");
	else if (this.signum == 0)
	    return ZERO;
	else
	    return new BigInteger(plumbDivide(magnitude, val.magnitude),
				  signum * val.signum);
    }

    /**
     * Returns a BigInteger whose value is (this % val).  Throws an
     * ArithmeticException if val == 0.
     */
    public BigInteger remainder(BigInteger val) throws ArithmeticException {

	if (val.signum == 0)
	    throw new ArithmeticException("BigInteger divide by zero");
	else if (this.signum == 0)
	    return ZERO;
	else if (this.magnitude.length < val.magnitude.length)
	    return this; /*** WORKAROUND FOR BUG IN R1.1 OF PLUMB'S PKG ***/
	else
	    return new BigInteger(plumbRemainder(magnitude,val.magnitude),
				  signum);
    }

    /**
     * Returns an array of two BigIntegers. The first ([0]) element of
     * the return value is the quotient (this / val), and the second ([1])
     * element is the remainder (this % val).  Throws an ArithmeticException
     * if val == 0.
     */
    public BigInteger[] divideAndRemainder(BigInteger val)
    throws ArithmeticException {
	BigInteger result[] = new BigInteger[2];

	if (val.signum == 0) {
	    throw new ArithmeticException("BigInteger divide by zero");
	} else if (this.signum == 0) {
	    result[0] = result[1] = ZERO;
	} else if (this.magnitude.length < val.magnitude.length) {
	    /*** WORKAROUND FOR A BUG IN R1.1 OF PLUMB'S PACKAGE ***/
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
     * Returns a BigInteger whose value is (this ** exponent).  Throws
     * an ArithmeticException if exponent < 0 (as the operation would yield
     * a non-integer value). Note that exponent is an integer rather than
     * a BigInteger.
     */
    public BigInteger pow(int exponent) throws ArithmeticException {
	if (exponent < 0)
	    throw new ArithmeticException("Negative exponent");
	if (signum==0)
	    return (exponent==0 ? ONE : this);

	/* Perform exponetiation using repeated squaring trick */
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
     * Returns a BigInteger whose value is the greatest common denominator
     * of abs(this) and abs(val).  Returns 0 if this == 0 && val == 0.
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
    * number.
    */
    public BigInteger abs() {
	return (signum >= 0 ? this : this.negate());
    }

    /**
     * Returns a BigInteger whose value is (-1 * this).
     */
    public BigInteger negate() {
	return new BigInteger(this.magnitude, -this.signum);
    }

    /**
     * Returns the signum function of this number (i.e., -1, 0 or 1 as
     * the value of this number is negative, zero or positive).
     */
    public int signum() {
	return this.signum;
    }

    // Modular Arithmetic Operations

    /**
     * Returns a BigInteger whose value is this mod m. Throws an
     * ArithmeticException if m <= 0.
     */
    public BigInteger mod(BigInteger m) {
	if (m.signum <= 0)
	    throw new ArithmeticException("BigInteger: modulus not positive");

	BigInteger result = this.remainder(m);
	return (result.signum >= 0 ? result : result.add(m));
    }

    /**
     * Returns a BigInteger whose value is (this ** exponent) mod m.  (If
     * exponent == 1, the returned value is (this mod m).  If exponent < 0,
     * the returned value is the modular multiplicative inverse of
     * (this ** -exponent).)  Throws an ArithmeticException if m <= 0.
     */
    public BigInteger modPow(BigInteger exponent, BigInteger m) {
	if (m.signum <= 0)
	    throw new ArithmeticException("BigInteger: modulus not positive");

	/* Workaround for a bug in Plumb: x^0 (y) dumps core for x != 0 */
	if (exponent.signum == 0)
	    return ONE;

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

	    /* Caculate (base ** exponent) mod m1 */
	    BigInteger a1 = new BigInteger
	     (plumbModPow(base.magnitude, exponent.magnitude, m1.magnitude),1);

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
     * Returns (this ** exponent) mod(2**p)
     */
    private BigInteger modPow2(BigInteger exponent, int p) {
	/*
	 * Perform exponetiation using repeated squaring trick, chopping off
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
     * Returns this mod(2**p).  Assumes that this BigInteger >= 0 and p > 0.
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
     * Returns modular multiplicative inverse of this, mod m.  Throws an
     * ArithmeticException if m <= 0 or this has no multiplicative inverse
     * mod m (i.e., gcd(this, m) != 1).
     */
    public BigInteger modInverse(BigInteger m) throws ArithmeticException {
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
     * Returns a BigInteger whose value is (this << n).  (Computes
     * floor(this * 2**n).)
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
     * Returns a BigInteger whose value is (this >> n).  Sign extension is
     * performed.  (Computes floor(this / 2**n).)
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
     * Returns a BigInteger whose value is (this & val).  (This method
     * returns a negative number iff this and val are both negative.)
     */
    public BigInteger and(BigInteger val) {
	byte[] result = new byte[Math.max(byteLength(), val.byteLength())];
	for (int i=0; i<result.length; i++)
	    result[i] = (byte) (getByte(result.length-i-1)
				& val.getByte(result.length-i-1));

	return valueOf(result);
    }

    /**
     * Returns a BigInteger whose value is (this | val).  (This method
     * returns a negative number iff either this or val is negative.)
     */
    public BigInteger or(BigInteger val) {
	byte[] result = new byte[Math.max(byteLength(), val.byteLength())];
	for (int i=0; i<result.length; i++)
	    result[i] = (byte) (getByte(result.length-i-1)
				| val.getByte(result.length-i-1));

	return valueOf(result);
    }

    /**
     * Returns a BigInteger whose value is (this ^ val).  (This method
     * returns a negative number iff exactly one of this and val are 
     * negative.)
     */
    public BigInteger xor(BigInteger val) {
	byte[] result = new byte[Math.max(byteLength(), val.byteLength())];
	for (int i=0; i<result.length; i++)
	    result[i] = (byte) (getByte(result.length-i-1)
				^ val.getByte(result.length-i-1));

	return valueOf(result);
    }

    /**
     * Returns a BigInteger whose value is (~this).  (This method returns
     * a negative value iff this number is non-negative.)
     */
    public BigInteger not() {
	byte[] result = new byte[byteLength()];
	for (int i=0; i<result.length; i++)
	    result[i] = (byte) ~getByte(result.length-i-1);

	return valueOf(result);
    }

    /**
     * Returns a BigInteger whose value is (this & ~val).  This method,
     * which is equivalent to and(val.not()), is provided as a convenience
     * for masking operations.  (This method returns a negative number iff
     * this is negative and val is positive.)
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
     * Returns true iff the designated bit is set. (Computes
     * ((this & (1<<n)) != 0).)  Throws an ArithmeticException if n < 0.
     */
    public boolean testBit(int n) throws ArithmeticException {
	if (n<0)
	    throw new ArithmeticException("Negative bit address");

	return (getByte(n/8) & (1 << (n%8))) != 0;
    }

    /**
     * Returns a BigInteger whose value is equivalent to this number
     * with the designated bit set.  (Computes (this | (1<<n)).)
     * Throws an ArithmeticException if n < 0.
     */
    public BigInteger setBit(int n) throws ArithmeticException {
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
     * Returns a BigInteger whose value is equivalent to this number
     * with the designated bit cleared. (Computes (this & ~(1<<n)).)
     * Throws an ArithmeticException if n < 0.
     */
    public BigInteger clearBit(int n) throws ArithmeticException {
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
     * Returns a BigInteger whose value is equivalent to this number
     * with the designated bit flipped.  (Computes (this ^ (1<<n)).)
     * Throws an ArithmeticException if n < 0.
     */
    public BigInteger flipBit(int n) throws ArithmeticException {
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
     * number (i.e., the number of zero bits to the right of the rightmost
     * one bit).  Returns -1 if this number contains no one bits.
     * (Computes (this==0? -1 : log2(this & -this)).)
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
     * representation of this number, *excluding* a sign bit, i.e.,
     * (ceil(log2(this < 0 ? -this : this + 1))).  (For positive
     * numbers, this is equivalent to the number of bits in the
     * ordinary binary representation.)
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
     * bitLen[i] is the number of bits in the binary representaion of i.
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
     * of this number that differ from its sign bit.  This method is
     * useful when implementing bit-vector style sets atop BigIntegers.
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
     * Returns true if this BigInteger is probably prime, false if it's
     * definitely composite.  The certainty parameter is a measure 
     * of the uncertainty that the caller is willing to tolerate:
     * the method returns true if the probability that this number is
     * is prime exceeds 1 - 1/2**certainty.  The execution time is
     * proportional to the value of the certainty parameter.
     */
    public boolean isProbablePrime(int certainty) {
	/*
	 * This test is taken from the DSA spec.
	 */
	int n = certainty/2;
	if (n <= 0)
	    return true;
	BigInteger w = this.abs();
	if (w.equals(TWO))
	    return true;
	if (!w.testBit(0) || w.equals(ONE))
	    return false;

	/* Find a and m such that m is odd and w == 1 + 2**a * m */
	BigInteger m = w.subtract(ONE);
	int a = m.getLowestSetBit();
	m = m.shiftRight(a);

	/* Do the tests */
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
     * Returns -1, 0 or 1 as this number is less than, equal to, or
     * greater than val.  This method is provided in preference to
     * individual methods for each of the six boolean comparison operators
     * (<, ==, >, >=, !=, <=).  The suggested idiom for performing these
     * comparisons is:  (x.compareTo(y) <op> 0), where <op> is one of the
     * six comparison operators.
     */
    public int compareTo(BigInteger val) {
	return (signum==val.signum
		? signum*byteArrayCmp(magnitude, val.magnitude)
		: (signum>val.signum ? 1 : -1));
    }

    /*
     * Returns -1, 0 or +1 as big-endian unsigned byte array arg1 is
     * <, == or > arg2.
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
     * Returns true iff x is a BigInteger whose value is equal to this number.
     * This method is provided so that BigIntegers can be used as hash keys.
     */
    public boolean equals(Object x) {
	if (!(x instanceof BigInteger))
	    return false;
	BigInteger xInt = (BigInteger) x;

	if (xInt.signum != signum || xInt.magnitude.length != magnitude.length)
	    return false;

	/* This test is just an optimization, which may or may not help */
	if (xInt == this)
	    return true;

	for (int i=0; i<magnitude.length; i++)
	    if (xInt.magnitude[i] != magnitude[i])
		return false;

	return true;
    }

    /**
     * Returns the BigInteger whose value is the lesser of this and val.
     * If the values are equal, either may be returned.
     */
    public BigInteger min(BigInteger val) {
	return (compareTo(val)<0 ? this : val);
    }

    /**
     * Returns the BigInteger whose value is the greater of this and val.
     * If the values are equal, either may be returned.
     */
    public BigInteger max(BigInteger val) {
	return (compareTo(val)>0 ? this : val);
    }


    // Hash Function

    /**
     * Computes a hash code for this object.
     */
    public int hashCode() {
	int hashCode = 0;

	for (int i=0; i<magnitude.length; i++)
	    hashCode = 37*hashCode + (magnitude[i] & 0xff);

	return hashCode * signum;
    }

    // Format Converters

    /**
     * Returns the string representation of this number in the given radix.
     * If the radix is outside the range from Character.MIN_RADIX(2) to
     * Character.MAX_RADIX(36) inclusive, it will default to 10 (as is the
     * case for Integer.toString).  The digit-to-character mapping provided
     * by Character.forDigit is used, and a minus sign is prepended if
     * appropriate.  (This representation is compatible with the (String, int)
     * constructor.)
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
     * Returns the string representation of this number, radix 10.  The
     * digit-to-character mapping provided by Character.forDigit is used,
     * and a minus sign is prepended if appropriate.  (This representation
     * is compatible with the (String) constructor, and allows for string
     * concatenation with Java's + operator.)
     */
    public String toString() {
	return toString(10);
    }

    /**
     * Returns the two's-complement representation of this number.  The array
     * is big-endian (i.e., the most significant byte is in the [0] position).
     * The array contains the minimum number of bytes required to represent
     * the number (ceil((this.bitLength() + 1)/8)).  (This representation is
     * compatible with the (byte[]) constructor.) 
     */
    public byte[] toByteArray() {
	byte[] result = new byte[byteLength()];
	for(int i=0; i<result.length; i++)
	    result[i] = getByte(result.length-i-1);

	return result;
    }

    /**
     * Converts this number to an int.  Standard narrowing primitive conversion
     * as per The Java Language Specification.
     */
    public int intValue() {
	int result = 0;

	for (int i=3; i>=0; i--)
	    result = (result << 8) + (getByte(i) & 0xff);
	return result;
    }

    /**
     * Converts this number to a long.  Standard narrowing primitive conversion
     * as per The Java Language Specification.
     */
    public long longValue() {
	long result = 0;

	for (int i=7; i>=0; i--)
	    result = (result << 8) + (getByte(i) & 0xff);
	return result;
    }

    /**
     * Converts this number to a float.  Similar to the double-to-float
     * narrowing primitive conversion defined in The Java Language
     * Specification: if the number has too great a magnitude to represent
     * as a float, it will be converted to infinity or negative infinity,
     * as appropriate.
     */
    public float floatValue() {
	/* Somewhat inefficient, but guaranteed to work. */
	return Float.valueOf(this.toString()).floatValue();
    }

    /**
     * Converts the number to a double.  Similar to the double-to-float
     * narrowing primitive conversion defined in The Java Language
     * Specification: if the number has too great a magnitude to represent
     * as a double, it will be converted to infinity or negative infinity,
     * as appropriate.
     */
    public double doubleValue() {
	/* Somewhat inefficient, but guaranteed to work. */
	return Double.valueOf(this.toString()).doubleValue();
    }

    static {
	System.loadLibrary("math");
	plumbInit();
    }


    private final static BigInteger ONE = valueOf(1);
    private final static BigInteger TWO = valueOf(2);

    private final static char ZERO_CHAR = Character.forDigit(0, 2);

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
     * highest integer n such that radix ** n < 2 ** 63.  The second is the
     * "long radix" that tears each number into "long digits", each of which
     * consists of the number of digits in the corresponding element in
     * digitsPerLong (longRadix[i] = i ** digitPerLong[i]).  Both arrays have
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
	 * Initialize bitCount field the first time this method is executed.
	 * This method depends on the atomicity of int modifies; without
	 * this guarantee, it would have to be synchronized.
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
