/*
 * @(#)BigDecimal.java	1.10 99/02/09
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

/**
 * Immutable, arbitrary-precision signed decimal numbers.  A BigDecimal
 * consists of an arbitrary precision integer value and a non-negative
 * integer scale, which represents the number of decimal digits to the
 * right of the decimal point.  (The number represented by the BigDecimal
 * is intVal/10**scale.)  BigDecimals provide operations for basic arithmetic,
 * scale manipulation, comparison, format conversion and hashing.
 *
 * <p>The BigDecimal class gives its user complete control over rounding
 * behavior, forcing the user to explicitly specify a rounding behavior for
 * operations capable of discarding precision (divide and setScale).  Eight
 * <em>rounding modes</em> are provided for this purpose.
 *
 * Two types of operations are provided for manipulating the scale of a
 * BigDecimal: scaling/rounding operations and decimal point motion operations.
 * Scaling/Rounding operations (SetScale) return a BigDecimal whose value is
 * approximately (or exactly) equal to that of the operand, but whose scale is
 * the specified value; that is, they increase or decrease the precision
 * of the number with minimal effect on its value.  Decimal point motion
 * operations (movePointLeft and movePointRight) return a BigDecimal created
 * from the operand by moving the decimal point a specified distance in the
 * specified direction; that is, they change a number's value without affecting
 * its precision.
 *
 * @see BigInteger
 * @version 	1.10, 99/02/09
 * @author      Josh Bloch
 */
public class BigDecimal extends Number {
    private BigInteger intVal;
    private int	       scale = 0;

    /* Appease the serialization gods */
    private static final long serialVersionUID = 6108874887143696463L;

    // Constructors

    /**
     * Constructs a BigDecimal from a string containing an optional minus
     * sign followed by a sequence of zero or more decimal digits, optionally
     * followed by a fraction, which consists of a decimal point followed by
     * zero or more decimal digits.  The string must contain at least one
     * digit in the integer or fractional part.  The scale of the resulting
     * BigDecimal will be the number of digits to the right of the decimal
     * point in the string, or 0 if the string contains no decimal point.
     * The character-to-digit mapping is provided by Character.digit.
     * Any extraneous characters (including whitespace) will result in
     * a NumberFormatException.
     */
    public BigDecimal(String val) throws NumberFormatException {
	int pointPos = val.indexOf('.');
	if (pointPos == -1) {			 /* e.g. "123" */
	    intVal = new BigInteger(val);
	} else if (pointPos == val.length()-1) { /* e.g. "123." */
	    intVal = new BigInteger(val.substring(0, val.length()-1));
	} else {    /* Fraction part exists */
	    String fracString = val.substring(pointPos+1);
	    scale = fracString.length();
	    BigInteger fraction =  new BigInteger(fracString);
	    if (fraction.signum() < 0)		 /* ".-123" illegal! */
		throw new NumberFormatException();

	    if (pointPos==0) {			 /* e.g.  ".123" */
		intVal = fraction;
	    } else if (val.charAt(0)=='-' && pointPos==1) {
		intVal = fraction.negate();	 /* e.g. "-.123" */
	    } else  {				 /* e.g. "-123.456" */
		String intString = val.substring(0, pointPos);
		BigInteger intPart = new BigInteger(intString);
		if (val.charAt(0) == '-')
		    fraction = fraction.negate();
		intVal = timesTenToThe(intPart, scale).add(fraction);
	    }
	}
    }

    /**
     * Translates a double into a BigDecimal.  The scale of the BigDecimal
     * is the smallest value such that (10**scale * val) is an integer.
     * A double whose value is -infinity, +infinity or NaN will result in a
     * NumberFormatException.
     */
    public BigDecimal(double val) throws NumberFormatException{
	if (Double.isInfinite(val) || Double.isNaN(val))
	    throw new NumberFormatException("Infinite or NaN");

	/*
	 * Translate the double into sign, exponent and mantissa, according
	 * to the formulae in JLS, Section 20.10.22.
	 */
	long valBits = Double.doubleToLongBits(val);
	int sign = ((valBits >> 63)==0 ? 1 : -1);
	int exponent = (int) ((valBits >> 52) & 0x7ffL);
	long mantissa = (exponent==0 ? (valBits & ((1L<<52) - 1)) << 1
				     : (valBits & ((1L<<52) - 1)) | (1L<<52));
	exponent -= 1075;
	/* At this point, val == sign * mantissa * 2**exponent */

	/*
	 * Special case zero to to supress nonterminating normalization
	 * and bogus scale calculation.
	 */
	if (mantissa == 0) {
	    intVal = BigInteger.valueOf(0);
	    return;
	}

	/* Normalize */
	while((mantissa & 1) == 0) {    /*  i.e., Mantissa is even */
	    mantissa >>= 1;
	    exponent++;
	}

	/* Calculate intVal and scale */
	intVal = BigInteger.valueOf(sign*mantissa);
	if (exponent < 0) {
	    intVal = intVal.multiply(BigInteger.valueOf(5).pow(-exponent));
	    scale = -exponent;
	} else if (exponent > 0) {
	    intVal = intVal.multiply(BigInteger.valueOf(2).pow(exponent));
	}
    }

    /**
     * Translates a BigInteger into a BigDecimal.  The scale of the BigDecimal
     * is zero.
     */
    public BigDecimal(BigInteger val) {
	intVal = val;
    }

    /**
     * Translates a BigInteger and a scale into a BigDecimal.  The value
     * of the BigDecimal is (BigInteger/10**scale).  A negative scale
     * will result in a NumberFormatException.
     */
    public BigDecimal(BigInteger val, int scale) throws NumberFormatException {
	if (scale < 0)
	    throw new NumberFormatException("Negative scale");

	intVal = val;
	this.scale = scale;
    }


    // Static Factory Methods

    /**
     * Returns a BigDecimal with a value of (val/10**scale).  This factory
     * is provided in preference to a (long) constructor because it allows
     * for reuse of frequently used BigDecimals (like 0 and 1), obviating
     * the need for exported constants.  A negative scale will result in a
     * NumberFormatException.
     */
    public static BigDecimal valueOf(long val, int scale)
	    throws NumberFormatException {
	return new BigDecimal(BigInteger.valueOf(val), scale);
    }

    /**
     * Returns a BigDecimal with the given value and a scale of zero.
     * This factory is provided in preference to a (long) constructor
     * because it allows for reuse of frequently used BigDecimals (like
     * 0 and 1), obviating the need for exported constants.
     */
    public static BigDecimal valueOf(long val) {
	return valueOf(val, 0);
    }


    // Arithmetic Operations

    /**
     * Returns a BigDecimal whose value is (this + val), and whose scale is
     * MAX(this.scale(), val.scale).
     */
    public BigDecimal add(BigDecimal val){
	BigDecimal arg[] = new BigDecimal[2];
	arg[0] = this;	arg[1] = val;
	matchScale(arg);
	return new BigDecimal(arg[0].intVal.add(arg[1].intVal), arg[0].scale);
    }

    /**
     * Returns a BigDecimal whose value is (this - val), and whose scale is
     * MAX(this.scale(), val.scale).
     */
    public BigDecimal subtract(BigDecimal val){
	BigDecimal arg[] = new BigDecimal[2];
	arg[0] = this;	arg[1] = val;
	matchScale(arg);
	return new BigDecimal(arg[0].intVal.subtract(arg[1].intVal),
			      arg[0].scale);
    }

    /**
     * Returns a BigDecimal whose value is (this * val), and whose scale is
     * this.scale() + val.scale.
     */
    public BigDecimal multiply(BigDecimal val){
	return new BigDecimal(intVal.multiply(val.intVal), scale+val.scale);
    }

    /**
     * Returns a BigDecimal whose value is (this / val), and whose scale
     * is as specified.  If rounding must be performed to generate a
     * result with the given scale, the specified rounding mode is
     * applied.  Throws an ArithmeticException if val == 0, scale < 0,
     * or the rounding mode is ROUND_UNNECESSARY and the specified scale
     * is insufficient to represent the result of the division exactly.
     * Throws an IllegalArgumentException if roundingMode does not
     * represent a valid rounding mode.
     */
    public BigDecimal divide(BigDecimal val, int scale, int roundingMode)
	    throws ArithmeticException, IllegalArgumentException {
	if (scale < 0)
	    throw new ArithmeticException("Negative scale");
	if (roundingMode < ROUND_UP || roundingMode > ROUND_UNNECESSARY)
	    throw new IllegalArgumentException("Invalid rounding mode");

	/*
	 * Rescale dividend or divisor (whichever can be "upscaled" to
	 * produce correctly scaled quotient).
	 */
	BigDecimal dividend, divisor;
	if (scale + val.scale >= this.scale) {
	    dividend = this.setScale(scale + val.scale);
	    divisor = val;
	} else {
	    dividend = this;
	    divisor = val.setScale(this.scale - scale);
	}

	/* Do the division and return result if it's exact */
	BigInteger i[] = dividend.intVal.divideAndRemainder(divisor.intVal);
	BigInteger q = i[0], r = i[1];
	if (r.signum() == 0)
	    return new BigDecimal(q, scale);
	else if (roundingMode == ROUND_UNNECESSARY) /* Rounding prohibited */
	    throw new ArithmeticException("Rounding necessary");

	/* Round as appropriate */
	int signum = dividend.signum() * divisor.signum(); /* Sign of result */
	boolean increment;
	if (roundingMode == ROUND_UP) {		    /* Away from zero */
	    increment = true;
	} else if (roundingMode == ROUND_DOWN) {    /* Towards zero */
	    increment = false;
	} else if (roundingMode == ROUND_CEILING) { /* Towards +infinity */
	    increment = (signum > 0);
	} else if (roundingMode == ROUND_FLOOR) {   /* Towards -infinity */
	    increment = (signum < 0);
	} else { /* Remaining modes based on nearest-neighbor determination */
	    int cmpFracHalf = r.abs().multiply(BigInteger.valueOf(2)).
					 compareTo(divisor.intVal.abs());
	    if (cmpFracHalf < 0) {	   /* We're closer to higher digit */
		increment = false;
	    } else if (cmpFracHalf > 0) {  /* We're closer to lower digit */
		increment = true;
	    } else { 			   /* We're dead-center */
		if (roundingMode == ROUND_HALF_UP)
		    increment = true;
		else if (roundingMode == ROUND_HALF_DOWN)
		    increment = false;
		else  /* roundingMode == ROUND_HALF_EVEN */
		    increment = q.testBit(0);	/* true iff q is odd */
	    }
	}
	return (increment
		? new BigDecimal(q.add(BigInteger.valueOf(signum)), scale)
		: new BigDecimal(q, scale));
    }

    /**
     * Returns a BigDecimal whose value is (this / val), and whose scale
     * is this.scale().  If rounding must be performed to generate a
     * result with the given scale, the specified rounding mode is
     * applied.  Throws an ArithmeticException if val == 0.  Throws
     * an IllegalArgumentException if roundingMode does not represent a
     * valid rounding mode.
     */
    public BigDecimal divide(BigDecimal val, int roundingMode)
	throws ArithmeticException, IllegalArgumentException{
	    return this.divide(val, scale, roundingMode);
    }

   /**
    * Returns a BigDecimal whose value is the absolute value of this
    * number, and whose scale is this.scale().
    */
    public BigDecimal abs(){
	return (signum() < 0 ? negate() : this);
    }

    /**
     * Returns a BigDecimal whose value is -1 * this, and whose scale is
     * this.scale().
     */
    public BigDecimal negate(){
	return new BigDecimal(intVal.negate(), scale);
    }

    /**
     * Returns the signum function of this number (i.e., -1, 0 or 1 as
     * the value of this number is negative, zero or positive).
     */
    public int signum(){
	return intVal.signum();
    }

    /**
     * Returns the scale of this number.
     */
    public int scale(){
	return scale;
    }


    // Rounding Modes

    /**
     * Always increment the digit prior to a non-zero discarded fraction.
     * Note that this rounding mode never decreases the magnitude.
     * (Rounds away from zero.)
     */
    public final static int ROUND_UP = 		 0;

    /**
     * Never increment the digit prior to a discarded fraction (i.e.,
     * truncate).  Note that this rounding mode never increases the magnitude.
     * (Rounds towards zero.)
     */
    public final static int ROUND_DOWN = 	 1;

    /**
     * If the BigDecimal is positive, behave as for ROUND_UP; if negative,
     * behave as for ROUND_DOWN.  Note that this rounding mode never decreases
     * the value.  (Rounds towards positive infinity.)
     */
    public final static int ROUND_CEILING = 	 2;

    /**
     * If the BigDecimal is positive, behave as for ROUND_DOWN; if negative
     * behave as for ROUND_UP.  Note that this rounding mode never increases
     * the value.  (Rounds towards negative infinity.)
     */
    public final static int ROUND_FLOOR = 	 3;

    /**
     * Behave as for ROUND_UP if the discarded fraction is >= .5; otherwise,
     * behave as for ROUND_DOWN.  (Rounds towards "nearest neighbor" unless
     * both neighbors are equidistant, in which case rounds up.)
     */
    public final static int ROUND_HALF_UP = 	 4;

    /**
     * Behave as for ROUND_UP if the discarded fraction is > .5; otherwise,
     * behave as for ROUND_DOWN.   (Rounds towards "nearest neighbor" unless
     * both neighbors are equidistant, in which case rounds down.)
     */
    public final static int ROUND_HALF_DOWN = 	 5;

    /**
     * Behave as for ROUND_HALF_UP if the digit to the left of the discarded
     * fraction is odd; behave as for ROUND_HALF_DOWN if it's even.  (Rounds
     * towards the "nearest neighbor" unless both neighbors are equidistant,
     * in which case, rounds towards the even neighbor.)
     */
    public final static int ROUND_HALF_EVEN = 	 6;

    /**
     * This "pseudo-rounding-mode" is actually an assertion that the requested
     * operation has an exact result, hence no rounding is necessary.  If this
     * rounding mode is specified on an operation that yields an inexact result,
     * an arithmetic exception is thrown.
     */
    public final static int ROUND_UNNECESSARY =  7;


    // Scaling/Rounding Operations

    /**
     * Returns a BigDecimal whose scale is the specified value, and whose
     * integer value is determined by multiplying or dividing this BigDecimal's
     * integer value by the appropriate power of ten to maintain the overall
     * value.  If the scale is reduced by the operation, the integer value
     * must be divided (rather than multiplied), and precision may be lost;
     * in this case, the specified rounding mode is applied to the division.
     * Throws an ArithmeticException if scale is negative, or the rounding
     * mode is ROUND_UNNECESSARY and it is impossible to perform the
     * specified scaling operation without loss of precision.  Throws an
     * IllegalArgumentException if roundingMode does not represent a valid
     * rounding mode.
     */
    public BigDecimal setScale(int scale, int roundingMode)
	throws ArithmeticException, IllegalArgumentException {
	if (scale < 0)
	    throw new ArithmeticException("Negative scale");
	if (roundingMode < ROUND_UP || roundingMode > ROUND_UNNECESSARY)
	    throw new IllegalArgumentException("Invalid rounding mode");

	/* Handle the easy cases */
	if (scale == this.scale)
	    return this;
	else if (scale > this.scale)
	    return new BigDecimal(timesTenToThe(intVal, scale-this.scale),
				  scale);
	else /* scale < this.scale */
	    return divide(valueOf(1), scale, roundingMode);
    }

    /**
     * Returns a BigDecimal whose scale is the specified value, and whose
     * value is exactly equal to this number's.  Throws an ArithmeticException
     * if this is not possible.  This call is typically used to increase
     * the scale, in which case it is guaranteed that there exists a BigDecimal
     * of the specified scale and the correct value.  The call can also be used
     * to reduce the scale if the caller knows that the number has sufficiently
     * many zeros at the end of its fractional part (i.e., factors of ten in
     * its integer value) to allow for the rescaling without loss of precision.
     * Note that this call returns the same result as the two argument version
     * of setScale, but saves the caller the trouble of specifying a rounding
     * mode in cases where it is irrelevant.
     */
    public BigDecimal setScale(int scale)
	throws ArithmeticException, IllegalArgumentException
    {
	return setScale(scale, ROUND_UNNECESSARY);
    }


    // Decimal Point Motion Operations

    /**
     * Returns a BigDecimal which is equivalent to this one with the decimal
     * point moved n places to the left.  If n is non-negative, the call merely
     * adds n to the scale.  If n is negative, the call is equivalent to
     * movePointRight(-n).  (The BigDecimal returned by this call has value
     * (this * 10**-n) and scale MAX(this.scale()+n, 0).)
     */
    public BigDecimal movePointLeft(int n){
	return (n>=0 ? new BigDecimal(intVal, scale+n) : movePointRight(-n));
    }

    /**
     * Moves the decimal point the specified number of places to the right.
     * If this number's scale is >= n, the call merely subtracts n from the
     * scale; otherwise, it sets the scale to zero, and multiplies the integer
     * value by 10 ** (n - this.scale).  If n is negative, the call is
     * equivalent to movePointLeft(-n). (The BigDecimal returned by this call
     * has value (this * 10**n) and scale MAX(this.scale()-n, 0).)
     */
    public BigDecimal movePointRight(int n){
	return (scale >= n ? new BigDecimal(intVal, scale-n)
		           : new BigDecimal(timesTenToThe(intVal, n-scale),0));
    }

    // Comparison Operations

    /**
     * Returns -1, 0 or 1 as this number is less than, equal to, or greater
     * than val.  Two BigDecimals that are equal in value but have a
     * different scale (e.g., 2.0, 2.00) are considered equal by this method.
     * This method is provided in preference to individual methods for each
     * of the six boolean comparison operators (<, ==, >, >=, !=, <=).  The
     * suggested idiom for performing these comparisons is:  (x.compareTo(y)
     * <op> 0), where <op> is one of the six comparison operators.
     */
    public int compareTo(BigDecimal val){
	/* Optimization: would run fine without the next three lines */
	int sigDiff = signum() - val.signum();
	if (sigDiff != 0)
	    return (sigDiff > 0 ? 1 : -1);

	/* If signs match, scale and compare intVals */
	BigDecimal arg[] = new BigDecimal[2];
	arg[0] = this;	arg[1] = val;
	matchScale(arg);
	return arg[0].intVal.compareTo(arg[1].intVal);
    }

    /**
     * Returns true iff x is a BigDecimal whose value is equal to this number.
     * This method is provided so that BigDecimals can be used as hash keys.
     * Unlike compareTo, this method considers two BigDecimals equal only
     * if they are equal in value and scale.
     */
    public boolean equals(Object x){
	if (!(x instanceof BigDecimal))
	    return false;
	BigDecimal xDec = (BigDecimal) x;

	return scale == xDec.scale && intVal.equals(xDec.intVal);
    }

    /**
     * Returns the BigDecimal whose value is the lesser of this and val.
     * If the values are equal (as defined by the compareTo operator),
     * either may be returned.
     */
    public BigDecimal min(BigDecimal val){
	return (compareTo(val)<0 ? this : val);
    }

    /**
     * Returns the BigDecimal whose value is the greater of this and val.
     * If the values are equal (as defined by the compareTo operator),
     * either may be returned.
     */
    public BigDecimal max(BigDecimal val){
	return (compareTo(val)>0 ? this : val);
    }


    // Hash Function

    /**
     * Computes a hash code for this object.  Note that two BigDecimals
     * that are numerically equal but differ in scale (e.g., 2.0, 2.00) will
     * not generally have the same hash code.
     */
    public int hashCode(){
	return 37*intVal.hashCode() + scale;
    }

    // Format Converters

    /**
     * Returns the string representation of this number.  The digit-to-
     * character mapping provided by Character.forDigit is used.  The minus
     * sign and decimal point are used to indicate sign and scale.  (This
     * representation is compatible with the (String, int) constructor.)
     */
    public String toString(){
	if (scale == 0)	/* No decimal point */
	    return intVal.toString();

	/* Insert decimal point */
	StringBuffer buf;
	String intString = intVal.abs().toString();
	int signum = signum();
	int insertionPoint = intString.length() - scale;
	if (insertionPoint == 0) {  /* Point goes right before intVal */
	    return (signum<0 ? "-0." : "0.") + intString;
	} else if (insertionPoint > 0) { /* Point goes inside intVal */
	    buf = new StringBuffer(intString);
	    buf.insert(insertionPoint, '.');
	    if (signum < 0)
		buf.insert(0, '-');
	} else { /* We must insert zeros between point and intVal */
	    buf = new StringBuffer(3-insertionPoint + intString.length());
	    buf.append(signum<0 ? "-0." : "0.");
	    for (int i=0; i<-insertionPoint; i++)
		buf.append('0');
	    buf.append(intString);
	}
	return buf.toString();
    }

    /**
     * Converts this number to a BigInteger.  Standard narrowing primitive
     * conversion as per The Java Language Specification.  In particular,
     * note that any fractional part of this number will be truncated.
     */
    public BigInteger toBigInteger(){
	return (scale==0 ? intVal
			 : intVal.divide(BigInteger.valueOf(10).pow(scale)));
    }

    /**
     * Converts this number to an int.  Standard narrowing primitive conversion
     * as per The Java Language Specification.  In particular, note that any
     * fractional part of this number will be truncated.
     */
    public int intValue(){
	return toBigInteger().intValue();
    }

    /**
     * Converts this number to a long.  Standard narrowing primitive conversion
     * as per The Java Language Specification.  In particular, note that any
     * fractional part of this number will be truncated.
     */
    public long longValue(){
	return toBigInteger().longValue();
    }

    /**
     * Converts this number to a float.  Similar to the double-to-float
     * narrowing primitive conversion defined in The Java Language
     * Specification: if the number has too great a magnitude to represent
     * as a float, it will be converted to infinity or negative infinity,
     * as appropriate.
     */
    public float floatValue(){
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
    public double doubleValue(){
	/* Somewhat inefficient, but guaranteed to work. */
	return Double.valueOf(this.toString()).doubleValue();
    }


    // Private "Helper" Methods

    /* Returns (a * 10^b) */
    private static BigInteger timesTenToThe(BigInteger a, int b) {
	return a.multiply(BigInteger.valueOf(10).pow(b));
    }

    /*
     * If the scales of val[0] and val[1] differ, rescale (non-destructively)
     * the lower-scaled BigDecimal so they match.
     */
    private static void matchScale(BigDecimal[] val) {
	if (val[0].scale < val[1].scale)
	    val[0] = val[0].setScale(val[1].scale);
	else if (val[1].scale < val[0].scale)
	    val[1] = val[1].setScale(val[0].scale);
    }

    /**
     * Reconstitute the <tt>BigDecimal</tt> instance from a stream (that is,
     * deserialize it).
     */
    private synchronized void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {
        // Read in all fields
	s.defaultReadObject();

        // Validate scale factor
        if (scale < 0)
	    throw new java.io.StreamCorruptedException(
                                      "BigDecimal: Negative scale");
    }
}
