/*
 * @(#)DigitList.java	1.9 97/03/03
 *
 * (C) Copyright Taligent, Inc. 1996 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - All Rights Reserved
 *
 * Portions copyright (c) 1996 Sun Microsystems, Inc. All Rights Reserved.
 *
 *   The original version of this source code and documentation is copyrighted
 * and owned by Taligent, Inc., a wholly-owned subsidiary of IBM. These
 * materials are provided under terms of a License Agreement between Taligent
 * and Sun. This technology is protected by multiple US and International
 * patents. This notice and attribution to Taligent may not be removed.
 *   Taligent is a registered trademark of Taligent, Inc.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */

package java.text;

/**
 * Digit List. Private to Decimal Format
 * @see  Locale
 * @see  Format
 * @see  NumberFormat
 * @see  DecimalFormat
 * @see  ChoiceFormat
 * @see  MessageFormat
 * @version      1.9 03/03/97
 * @author       Mark Davis
 */
final class DigitList implements Cloneable {
    /**
     * The fields can be set directly.
     */
    public static final int MAX_COUNT = 19;
    public int decimalAt = 0;
    public int count = 0;
    public byte[] digits = new byte[MAX_COUNT];

    /**
     * Clears out the digits.
     * Use before appending them.
     * Typically, you set a series of digits with append, then at the point
     * you hit the decimal point, you set myDigitList.decimalAt = myDigitList.count;
     * then go on appending digits.
     */
    public void clear () {
        decimalAt = 0;
        count = 0;
    }
    /**
     * Appends digits to the list. Ignores all digits over 20,
     * since they are not significant for either longs or doubles.
     */
    public void append (int digit) {
        if (count < MAX_COUNT)
            digits[count] = (byte) digit;
        ++count;
    }
    /**
     * Utility routine to get the value of the digit list
     * If (count == 0) this throws a NumberFormatException, which
     * mimics Long.parseLong().
     * FIXME shouldn't this use Double.valueOf()??
     * INTEGRATION WITH JAVA: REWRITE IN NATIVE
     */
    public final double getDouble() {
        // for now, simple implementation; later, do proper IEEE native stuff
	StringBuffer temp = new StringBuffer(count);
	if (count > MAX_COUNT)
	    count = MAX_COUNT;
	for (int i = 0; i < count; ++i)
	    temp.append((char)(digits[i]));
	long value = Long.parseLong(temp.toString());
	return (value * Math.pow(10, decimalAt - count));
    }

    /**
     * Utility routine to get the value of the digit list
     * If (count == 0) this throws a NumberFormatException, which
     * mimics Long.parseLong().
     * INTEGRATION WITH JAVA: REWRITE IN NATIVE
     */
    public final long getLong() {
        // for now, simple implementation; later, do proper IEEE native stuff
	StringBuffer temp = new StringBuffer(count);
	for (int i = 0; i < count; ++i)
	    temp.append((char)(digits[i]));
	return Long.parseLong(temp.toString());
    }

    private int fixNumber(double toBeFixed, StringBuffer buffer, int p) {
        int correction = 0;
        double logValue1 = 0.0, logValue2 = 0.0;
        int i = 0;
        StringBuffer temp = null;
        Double aValue = null;
        double result = toBeFixed;
        logValue1 = Math.log(result);
        logValue2 = Math.log(10);
        double value = logValue1 / logValue2;
        correction = (int)(value - MAX_COUNT);
        while (result > Long.MAX_VALUE) {
            result /= 10;
            i++;
        }
        String digitsList = Long.toString((long)result);
        int n = digitsList.length() - 1;
        for (; n >= 0; n--) {
            if (digitsList.charAt(n) != '0')
                break;
        }
        for (int j = 0; j < n+1; j++) {
            buffer.append(digitsList.charAt(j));
            digits[j] = (byte) digitsList.charAt(j);
        }
        decimalAt = (int)(i - correction + value - p);
        return n+1;
    }

    /**
     * Utility routine to set the value of the digit list from a double
     * Note: input must not be Inf, -Inf, or Nan.
     * INTEGRATION WITH JAVA: REWRITE IN NATIVE
     */
    public final void set(double source, int maxDecimalCount) {
//        if (maxDecimalCount > MAX_COUNT) maxDecimalCount = MAX_COUNT;
        // for now, simple implementation; later, do proper IEEE stuff
        int i = 0;
        double sourceDouble = (source * Math.pow(10, maxDecimalCount));
        long temp = (long)(sourceDouble);
        boolean skipCheck = false;
        StringBuffer stringDigits = new StringBuffer(MAX_COUNT);
        if (temp < 0) {
            count = fixNumber(sourceDouble, stringDigits, maxDecimalCount);
            skipCheck = true;
        }
        if (!skipCheck) {
            stringDigits.append(Long.toString(temp));
            while ((temp > 0) && (temp < Math.pow(10, maxDecimalCount-1))) {
                stringDigits.insert(0, '0');
                temp *= 10;
            }
            for (i = stringDigits.length() - 1; i >= 0; --i) { // strip trailing zeros
                if (stringDigits.charAt(i) != '0') break;
                maxDecimalCount--;
            }
            count = i+1;
            for (int j = 0; j < count; ++j)
                digits[j] = (byte) stringDigits.charAt(j);
            decimalAt = count - maxDecimalCount;
        }
        if (decimalAt < 0) decimalAt = 0;
    }
    /**
     * Utility routine to set the value of the digit list from a long
     * INTEGRATION WITH JAVA: REWRITE IN NATIVE
     */
    public final void set(long source) {
        // for now, simple implementation; later, do proper IEEE stuff
//        String stringDigits = Long.toString(source);
        String stringDigits;

        // This method does not expect a negative number. However,
        // "source" can be a Long.MIN_VALUE (-9223372036854775808),
        // if the number being formatted is a Long.MIN_VALUE. -- CLH, 12/19/96
        if (source == Long.MIN_VALUE) {
            source = - (source + 1);
            stringDigits = Long.toString(source).substring(0, MAX_COUNT-1);
            char lastDigit = Long.toString(source).charAt(MAX_COUNT-1);
            long actualDigit = Character.digit(lastDigit, 10) + 1;
            if (actualDigit <= 9)
                stringDigits = stringDigits + Long.toString(actualDigit);
            else
                // Not likely to happen!
                throw new InternalError("Internal NumberFormat error!");
        }
        else
            stringDigits = Long.toString(source);

        if (stringDigits.length() > MAX_COUNT) {
            stringDigits = stringDigits.substring(0, MAX_COUNT);
        }
        int i;
        int maxDecimalCount = 0;
        for (i = stringDigits.length() - 1; i >= 0; --i) { // strip trailing zeros
            if (stringDigits.charAt(i) != '0') break;
            maxDecimalCount--;
        }
        count = i+1;
        for (int j = 0; j < count; ++j)
            digits[j] = (byte) stringDigits.charAt(j);
        decimalAt = count - maxDecimalCount;
        if (decimalAt < 0) decimalAt = 0;
    }
    /**
     * Standard override; no change in semantics.
     */
    public Object clone() {
        try {
            return (DigitList) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    };
    /**
     * equality test between two digit lists.
     */
    public boolean equals(Object obj) {
        if (this == obj)                      // quick check
            return true;
        if (!(obj instanceof DigitList))         // (1) same object?
            return false;
        DigitList other = (DigitList) obj;
        if (hashCode() != other.hashCode())
            return false;       // quick check
        if (!(count == other.count))
            return false;
        if (!(digits.length == other.digits.length))
            return false;
        for (int i = 0; i < digits.length; i++)
            if (digits[i] != other.digits[i])
                return false;
        if (decimalAt != other.decimalAt)
            return false;
        return true;
    }

    /**
     * Generates the hash code for the digit list.
     */
    public int hashCode() {
        int hashcode = 0;
        for (int i = 0; i < digits.length; i++) {
            short mostSignificant = (short)(((int)digits[i]) >> 16);
            short leastSignificant = digits[i];
            hashcode ^= mostSignificant ^ leastSignificant;
        }
        return hashcode;
    }
}
