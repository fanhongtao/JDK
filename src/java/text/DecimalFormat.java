/*
 * @(#)DecimalFormat.java	1.48 98/09/21
 *
 * (C) Copyright Taligent, Inc. 1996, 1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - 1998 - All Rights Reserved
 *
 * Portions copyright (c) 1996-1998 Sun Microsystems, Inc.
 * All Rights Reserved.
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
 * SUN MAKE NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */

package java.text;
import java.util.ResourceBundle;
import java.util.Locale;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Hashtable;

/**
 * <code>DecimalFormat</code> is a concrete subclass of <code>NumberFormat</code>
 * for formatting decimal numbers. This class allows for a variety
 * of parameters, and localization to Western, Arabic, or Indic numbers.
 *
 * <p>
 * Normally, you get the proper <code>NumberFormat</code> for a specific
 * locale (including the default locale) using one of <code>NumberFormat</code>'s
 * factory methods such as <code>getInstance</code>. You may then modify it
 * from there (after testing to make sure it is a <code>DecimalFormat</code>,
 * of course!)
 *
 * <p>
 * Either the prefixes or the suffixes must be different for
 * the parse to distinguish positive from negative.
 * Parsing will be unreliable if the digits, thousands or decimal separators
 * are the same, or if any of them occur in the prefixes or suffixes.
 *
 * <p>
 * <strong>Special cases:</strong>
 *
 * <p>
 * <code>NaN</code> is formatted as a single character, typically
 * <code>\\uFFFD</code>.
 *
 * <p>
 * +/-Infinity is formatted as a single character, typically <code>\\u221E</code>,
 * plus the positive and negative pre/suffixes.
 *
 * <p><code>Note:</code> this class is designed for common users; for very
 * large or small numbers, use a format that can express exponential values.

 * <p><strong>Example:</strong>
 * <blockquote>
 * <pre>
 * // normally we would have a GUI with a menu for this
 * Locale[] locales = NumberFormat.getAvailableLocales();
 *
 * double myNumber = -1234.56;
 * NumberFormat form;
 *
 * // just for fun, we print out a number with the locale number, currency
 * // and percent format for each locale we can.
 * for (int j = 0; j < 3; ++j) {
 *     System.out.println("FORMAT");
 *     for (int i = 0; i < locales.length; ++i) {
 *         if (locales[i].getCountry().length() == 0) {
 *            // skip language-only
 *            continue;
 *         }
 *         System.out.print(locales[i].getDisplayName());
 *         switch (j) {
 *         default:
 *             form = NumberFormat.getInstance(locales[i]); break;
 *         case 1:
 *             form = NumberFormat.getCurrencyInstance(locales[i]); break;
 *         case 0:
 *             form = NumberFormat.getPercentInstance(locales[i]); break;
 *         }
 *         try {
 *             System.out.print(": " + ((DecimalFormat)form).toPattern()
 *                          + " -> " + form.format(myNumber));
 *         } catch (IllegalArgumentException iae) { }
 *         try {
 *             System.out.println(" -> " + form.parse(form.format(myNumber)));
 *         } catch (ParseException pe) { }
 *     }
 * }
 * </pre>
 * </blockquote>
 * <strong>The following shows the structure of the pattern.</strong>
 * <pre>
 * pattern    := subpattern{;subpattern}
 * subpattern := {prefix}integer{.fraction}{suffix}
 *
 * prefix     := '\\u0000'..'\\uFFFD' - specialCharacters
 * suffix     := '\\u0000'..'\\uFFFD' - specialCharacters
 * integer    := '#'* '0'* '0'
 * fraction   := '0'* '#'*
 *
 * Notation:
 *  X*       0 or more instances of X
 *  (X | Y)  either X or Y.
 *  X..Y     any character from X up to Y, inclusive.
 *  S - T    characters in S, except those in T
 * </pre>
 * The first subpattern is for positive numbers. The second (optional)
 * subpattern is for negative numbers. (In both cases, ',' can occur
 * inside the integer portion--it is just too messy to indicate in BNF.)
 *
 * <p>
 * Here are the special characters used in the parts of the
 * subpattern, with notes on their usage.
 * <pre>
 * Symbol Meaning
 * 0      a digit
 * #      a digit, zero shows as absent
 * .      placeholder for decimal separator
 * ,      placeholder for grouping separator.
 * E      separates mantissa and exponent for exponential formats.
 * ;      separates formats.
 * -      default negative prefix.
 * %      multiply by 100 and show as percentage
 * \u2030 multiply by 1000 and show as per mille
 * \u00A4 currency sign; replaced by currency symbol; if
 *        doubled, replaced by international currency symbol.
 *        If present in a pattern, the monetary decimal separator
 *        is used instead of the decimal separator.
 * X      any other characters can be used in the prefix or suffix
 * '      used to quote special characters in a prefix or suffix.
 * </pre>
 * <p><strong>Notes</strong>
 * <p>
 * If there is no explicit negative subpattern, - is prefixed to the
 * positive form. That is, "0.00" alone is equivalent to "0.00;-0.00".
 * If there is an explicit negative subpattern, it serves only to specify the
 * negative prefix and suffix; the number of digits, minimal digits, and other
 * characteristics are all the same as the positive pattern. That means that
 * "#,##0.0#;(#)" has precisely the same result as "#,##0.0#;(#,##0.0#)".
 * <p>
 * The exponent character must be immediately followed by one or more
 * digit characters. Example: "0.###E0". The number of digit characters
 * after the exponent character gives the minimum exponent digit count;
 * there is no maximum. Negative exponents are denoted using the same
 * prefix and/or suffix specified for the number itself. The minimum
 * number of integer digits is achieved by adjusting the exponent. The
 * maximum number of integer digits, if any, specifies the exponent
 * grouping. For example, 12345 is formatted using "##0.###E0" as
 * "12.345E3".
 * <p>
 * Illegal patterns, such as "#.#.#" or mixing '_' and '*' in the
 * same pattern, will cause an <code>IllegalArgumentException</code> to be
 * thrown. From the message of <code>IllegalArgumentException</code>, you can
 * find the place in the string where the error occurred.
 *
 * <p>
 * The grouping separator is commonly used for thousands, but in some
 * countries for ten-thousands. The interval is a constant number of
 * digits between the grouping characters, such as 100,000,000 or 1,0000,0000.
 * If you supply a pattern with multiple grouping characters, the interval
 * between the last one and the end of the integer is the one that is
 * used. So "#,##,###,####" == "######,####" == "##,####,####".
 *
 * <p>
 * When calling DecimalFormat.parse(String, ParsePosition) and parsing
 * fails, a null object will be returned.  The unchanged parse position
 * also reflects that an error has occurred during parsing.  When calling
 * the convenient method DecimalFormat.parse(String) and parsing fails,
 * a ParseException will be thrown.
 * <p>
 * This class handles all Unicode characters that represent decimal digits.
 * This set of characters is defined in the Unicode standard. 
 *
 * @see          java.text.Format
 * @see          java.text.NumberFormat
 * @see          java.text.ChoiceFormat
 * @version      1.48 09/21/98
 * @author       Mark Davis
 * @author       Alan Liu
 */
/*
 * Requested Features
 * Symbol Meaning
 * $      currency symbol as decimal point
 * \u0085   escapes text
 * \u2030 divide by 1000 and show as per/mil
 */
public class DecimalFormat extends NumberFormat {

    /**
     * Create a DecimalFormat using the default pattern and symbols
     * for the default locale. This is a convenient way to obtain a
     * DecimalFormat when internationalization is not the main concern.
     * <p>
     * To obtain standard formats for a given locale, use the factory methods
     * on NumberFormat such as getNumberInstance. These factories will
     * return the most appropriate sub-class of NumberFormat for a given
     * locale.
     * @see java.text.NumberFormat#getInstance
     * @see java.text.NumberFormat#getNumberInstance
     * @see java.text.NumberFormat#getCurrencyInstance
     * @see java.text.NumberFormat#getPercentInstance
     */
    public DecimalFormat() {
    Locale def = Locale.getDefault();
    /* try to get the pattern from the cache */
    String pattern = (String) cachedLocaleData.get(def);
    if (pattern == null) {  /* cache miss */
        // Get the pattern for the default locale.
        ResourceBundle rb = ResourceBundle.getBundle
        ("java.text.resources.LocaleElements", def);
        String[] all = rb.getStringArray("NumberPatterns");
        pattern = all[0];
        /* update cache */
        cachedLocaleData.put(def, pattern);
    }

    /* Always applyPattern after the symbols are set */
        this.symbols = new DecimalFormatSymbols( def );
        applyPattern( pattern, false );
    }


    /**
     * Create a DecimalFormat from the given pattern and the symbols
     * for the default locale. This is a convenient way to obtain a
     * DecimalFormat when internationalization is not the main concern.
     * <p>
     * To obtain standard formats for a given locale, use the factory methods
     * on NumberFormat such as getNumberInstance. These factories will
     * return the most appropriate sub-class of NumberFormat for a given
     * locale.
     * @param pattern A non-localized pattern string.
     * @exception IllegalArgumentException if the given pattern is invalid.
     * @see java.text.NumberFormat#getInstance
     * @see java.text.NumberFormat#getNumberInstance
     * @see java.text.NumberFormat#getCurrencyInstance
     * @see java.text.NumberFormat#getPercentInstance
     */
    public DecimalFormat(String pattern) {
    // Always applyPattern after the symbols are set
        this.symbols = new DecimalFormatSymbols( Locale.getDefault() );
        applyPattern( pattern, false );
    }


    /**
     * Create a DecimalFormat from the given pattern and symbols.
     * Use this constructor when you need to completely customize the
     * behavior of the format.
     * <p>
     * To obtain standard formats for a given
     * locale, use the factory methods on NumberFormat such as
     * getInstance or getCurrencyInstance. If you need only minor adjustments
     * to a standard format, you can modify the format returned by
     * a NumberFormat factory method.
     * @param pattern a non-localized pattern string
     * @param symbols the set of symbols to be used
     * @exception IllegalArgumentException if the given pattern is invalid
     * @see java.text.NumberFormat#getInstance
     * @see java.text.NumberFormat#getNumberInstance
     * @see java.text.NumberFormat#getCurrencyInstance
     * @see java.text.NumberFormat#getPercentInstance
     * @see java.text.DecimalFormatSymbols
     */
    public DecimalFormat (String pattern, DecimalFormatSymbols symbols) {
        // Always applyPattern after the symbols are set
        this.symbols = (DecimalFormatSymbols)symbols.clone();
        applyPattern( pattern, false );
    }


    // Overrides
    public StringBuffer format(double number, StringBuffer result,
                               FieldPosition fieldPosition)
    {
        fieldPosition.setBeginIndex(0);
        fieldPosition.setEndIndex(0);

        if (Double.isNaN(number))
        {
            if (fieldPosition.getField() == NumberFormat.INTEGER_FIELD)
            fieldPosition.setBeginIndex(result.length());

            result.append(symbols.getNaN());

            if (fieldPosition.getField() == NumberFormat.INTEGER_FIELD)
            fieldPosition.setEndIndex(result.length());

            return result;
        }

        /* Detecting whether a double is negative is easy with the exception of
         * the value -0.0.  This is a double which has a zero mantissa (and
         * exponent), but a negative sign bit.  It is semantically distinct from
         * a zero with a positive sign bit, and this distinction is important
         * to certain kinds of computations.  However, it's a little tricky to
         * detect, since (-0.0 == 0.0) and !(-0.0 < 0.0).  How then, you may
         * ask, does it behave distinctly from +0.0?  Well, 1/(-0.0) ==
         * -Infinity.  Proper detection of -0.0 is needed to deal with the
         * issues raised by bugs 4106658, 4106667, and 4147706.  Liu 7/6/98.
         */
        boolean isNegative = (number < 0.0) || (number == 0.0 && 1/number < 0.0);
        if (isNegative) number = -number;

        // Do this BEFORE checking to see if value is infinite!
        if (multiplier != 1) number *= multiplier;

        if (Double.isInfinite(number))
        {
            result.append(isNegative ? negativePrefix : positivePrefix);

            if (fieldPosition.getField() == NumberFormat.INTEGER_FIELD)
            fieldPosition.setBeginIndex(result.length());

            result.append(symbols.getInfinity());

            if (fieldPosition.getField() == NumberFormat.INTEGER_FIELD)
            fieldPosition.setEndIndex(result.length());

            result.append(isNegative ? negativeSuffix : positiveSuffix);
            return result;
        }

        // At this point we are guaranteed a nonnegative finite
        // number.
        synchronized(digitList) {
            digitList.set(number, useExponentialNotation ?
                      getMaximumIntegerDigits() + getMaximumFractionDigits() :
                      getMaximumFractionDigits(),
                      !useExponentialNotation);

            return subformat(result, fieldPosition, isNegative, false);
        }
    }

    public StringBuffer format(long number, StringBuffer result,
                               FieldPosition fieldPosition)
    {
        fieldPosition.setBeginIndex(0);
        fieldPosition.setEndIndex(0);

        boolean isNegative = (number < 0);
        if (isNegative) number = -number;

        // In general, long values always represent real finite numbers, so
        // we don't have to check for +/- Infinity or NaN.  However, there
        // is one case we have to be careful of:  The multiplier can push
        // a number near MIN_VALUE or MAX_VALUE outside the legal range.  We
        // check for this before multiplying, and if it happens we use doubles
        // instead, trading off accuracy for range.
        if (multiplier != 1 && multiplier != 0)
        {
            boolean useDouble = false;

            if (number < 0) // This can only happen if number == Long.MIN_VALUE
            {
                long cutoff = Long.MIN_VALUE / multiplier;
                useDouble = (number < cutoff);
            }
            else
            {
                long cutoff = Long.MAX_VALUE / multiplier;
                useDouble = (number > cutoff);
            }

            if (useDouble)
            {
                double dnumber = (double)(isNegative ? -number : number);
                return format(dnumber, result, fieldPosition);
            }
        }

        number *= multiplier;
        synchronized(digitList) {
            digitList.set(number, useExponentialNotation ?
                      getMaximumIntegerDigits() + getMaximumFractionDigits() : 0);

            return subformat(result, fieldPosition, isNegative, true);
        }
    }

    /**
     * Complete the formatting of a finite number.  On entry, the digitList must
     * be filled in with the correct digits.
     */
    private StringBuffer subformat(StringBuffer result, FieldPosition fieldPosition,
                   boolean isNegative, boolean isInteger)
    {
        // NOTE: This isn't required anymore because DigitList takes care of this.
        //
        //  // The negative of the exponent represents the number of leading
        //  // zeros between the decimal and the first non-zero digit, for
        //  // a value < 0.1 (e.g., for 0.00123, -fExponent == 2).  If this
        //  // is more than the maximum fraction digits, then we have an underflow
        //  // for the printed representation.  We recognize this here and set
        //  // the DigitList representation to zero in this situation.
        //
        //  if (-digitList.decimalAt >= getMaximumFractionDigits())
        //  {
        //      digitList.count = 0;
        //  }

        char zero = symbols.getZeroDigit();
        int zeroDelta = zero - '0'; // '0' is the DigitList representation of zero
        char grouping = symbols.getGroupingSeparator();
        char decimal = isCurrencyFormat ?
            symbols.getMonetaryDecimalSeparator() :
            symbols.getDecimalSeparator();

        /* Per bug 4147706, DecimalFormat must respect the sign of numbers which
         * format as zero.  This allows sensible computations and preserves
         * relations such as signum(1/x) = signum(x), where x is +Infinity or
         * -Infinity.  Prior to this fix, we always formatted zero values as if
         * they were positive.  Liu 7/6/98.
         */
        if (digitList.isZero())
        {
            digitList.decimalAt = 0; // Normalize
        }

        result.append(isNegative ? negativePrefix : positivePrefix);

        if (useExponentialNotation)
        {
            // Record field information for caller.
            if (fieldPosition.getField() == NumberFormat.INTEGER_FIELD)
            {
                fieldPosition.setBeginIndex(result.length());
                fieldPosition.setEndIndex(-1);
            }
            else if (fieldPosition.getField() == NumberFormat.FRACTION_FIELD)
            {
                fieldPosition.setBeginIndex(-1);
            }

            // Minimum integer digits are handled in exponential format by
            // adjusting the exponent.  For example, 0.01234 with 3 minimum
            // integer digits is "123.4E-4".

            // Maximum integer digits are interpreted as indicating the
            // repeating range.  This is useful for engineering notation, in
            // which the exponent is restricted to a multiple of 3.  For
            // example, 0.01234 with 3 maximum integer digits is "12.34e-3".
            // If maximum integer digits are defined and are larger than
            // minimum integer digits, then minimum integer digits are
            // ignored.
            int exponent = digitList.decimalAt;
            int repeat = getMaximumIntegerDigits();
            if (repeat > 1 &&
            repeat != getMinimumIntegerDigits())
            {
                // A repeating range is defined; adjust to it as follows.
                // If repeat == 3, we have 5,4,3=>3; 2,1,0=>0; -1,-2,-3=>-3;
                // -4,-5-,6=>-6, etc.  Also, the exponent we have here is
                // off by one from what we expect; that is, it is for the format
                // 0.MMMMMx10^n.  So we subtract another 1 to get it in range.
                exponent -= (exponent < 0) ? repeat : 1;
                exponent = (exponent / repeat) * repeat;
            }
            else
            {
                // No repeating range is defined; use minimum integer digits.
                exponent -= getMinimumIntegerDigits();
            }

            // We now output a minimum number of digits, and more if there
            // are more digits, up to the maximum number of digits.  We
            // place the decimal point after the "integer" digits, which
            // are the first (decimalAt - exponent) digits.
            int minimumDigits = getMinimumIntegerDigits()
                                + getMinimumFractionDigits();
            // The number of integer digits is handled specially if the number
            // is zero, since then there may be no digits.
            int integerDigits = digitList.isZero() ? getMinimumIntegerDigits() :
            digitList.decimalAt - exponent;
            int totalDigits = digitList.count;
            if (minimumDigits > totalDigits) totalDigits = minimumDigits;

            for (int i=0; i<totalDigits; ++i)
            {
                if (i == integerDigits)
                {
                    // Record field information for caller.
                    if (fieldPosition.getField() == NumberFormat.INTEGER_FIELD)
                    fieldPosition.setEndIndex(result.length());

                    result.append(decimal);

                    // Record field information for caller.
                    if (fieldPosition.getField() == NumberFormat.FRACTION_FIELD)
                    fieldPosition.setBeginIndex(result.length());
                }
                result.append((i < digitList.count) ?
                          (char)(digitList.digits[i] + zeroDelta) :
                          zero);
            }

            // Record field information
            if (fieldPosition.getField() == NumberFormat.INTEGER_FIELD)
            {
                if (fieldPosition.getEndIndex() < 0)
                    fieldPosition.setEndIndex(result.length());
            }
            else if (fieldPosition.getField() == NumberFormat.FRACTION_FIELD)
            {
                if (fieldPosition.getBeginIndex() < 0)
                    fieldPosition.setBeginIndex(result.length());
                fieldPosition.setEndIndex(result.length());
            }

            // The exponent is output using the pattern-specified minimum
            // exponent digits.  There is no maximum limit to the exponent
            // digits, since truncating the exponent would result in an
            // unacceptable inaccuracy.
            result.append(symbols.getExponentialSymbol());

            // For zero values, we force the exponent to zero.  We
            // must do this here, and not earlier, because the value
            // is used to determine integer digit count above.
            if (digitList.isZero()) exponent = 0;

            boolean negativeExponent = exponent < 0;
            if (negativeExponent) exponent = -exponent;
            result.append(negativeExponent ? negativePrefix : positivePrefix);
            digitList.set(exponent);
            for (int i=digitList.decimalAt; i<minExponentDigits; ++i) result.append(zero);
            for (int i=0; i<digitList.decimalAt; ++i)
            {
                result.append((i < digitList.count) ?
                          (char)(digitList.digits[i] + zeroDelta) : zero);
            }
            result.append(negativeExponent ? negativeSuffix : positiveSuffix);
        }
        else
        {
            // Record field information for caller.
            if (fieldPosition.getField() == NumberFormat.INTEGER_FIELD)
                fieldPosition.setBeginIndex(result.length());

            // Output the integer portion.  Here 'count' is the total
            // number of integer digits we will display, including both
            // leading zeros required to satisfy getMinimumIntegerDigits,
            // and actual digits present in the number.
            int count = getMinimumIntegerDigits();
            int digitIndex = 0; // Index into digitList.fDigits[]
            if (digitList.decimalAt > 0 && count < digitList.decimalAt)
                count = digitList.decimalAt;

            // Handle the case where getMaximumIntegerDigits() is smaller
            // than the real number of integer digits.  If this is so, we
            // output the least significant max integer digits.  For example,
            // the value 1997 printed with 2 max integer digits is just "97".

            if (count > getMaximumIntegerDigits())
            {
                count = getMaximumIntegerDigits();
                digitIndex = digitList.decimalAt - count;
            }

            int sizeBeforeIntegerPart = result.length();
            for (int i=count-1; i>=0; --i)
            {
                if (i < digitList.decimalAt && digitIndex < digitList.count)
                {
                    // Output a real digit
                    result.append((char)(digitList.digits[digitIndex++] + zeroDelta));
                }
                else
                {
                    // Output a leading zero
                    result.append(zero);
                }

                // Output grouping separator if necessary.  Don't output a
                // grouping separator if i==0 though; that's at the end of
                // the integer part.
                if (isGroupingUsed() && i>0 && (groupingSize != 0) && (i % groupingSize == 0))
                {
                    result.append(grouping);
                }
            }

            // Record field information for caller.
            if (fieldPosition.getField() == NumberFormat.INTEGER_FIELD)
            fieldPosition.setEndIndex(result.length());

            // Determine whether or not there are any printable fractional
            // digits.  If we've used up the digits we know there aren't.
            boolean fractionPresent = (getMinimumFractionDigits() > 0) ||
            (!isInteger && digitIndex < digitList.count);

            // If there is no fraction present, and we haven't printed any
            // integer digits, then print a zero.  Otherwise we won't print
            // _any_ digits, and we won't be able to parse this string.
            if (!fractionPresent && result.length() == sizeBeforeIntegerPart)
                result.append(zero);

            // Output the decimal separator if we always do so.
            if (decimalSeparatorAlwaysShown || fractionPresent)
                result.append(decimal);

            // Record field information for caller.
            if (fieldPosition.getField() == NumberFormat.FRACTION_FIELD)
                fieldPosition.setBeginIndex(result.length());

            for (int i=0; i < getMaximumFractionDigits(); ++i)
            {
                // Here is where we escape from the loop.  We escape if we've output
                // the maximum fraction digits (specified in the for expression above).
                // We also stop when we've output the minimum digits and either:
                // we have an integer, so there is no fractional stuff to display,
                // or we're out of significant digits.
                if (i >= getMinimumFractionDigits() &&
                    (isInteger || digitIndex >= digitList.count))
                    break;

                // Output leading fractional zeros.  These are zeros that come after
                // the decimal but before any significant digits.  These are only
                // output if abs(number being formatted) < 1.0.
                if (-1-i > (digitList.decimalAt-1))
                {
                    result.append(zero);
                    continue;
                }

                // Output a digit, if we have any precision left, or a
                // zero if we don't.  We don't want to output noise digits.
                if (!isInteger && digitIndex < digitList.count)
                {
                    result.append((char)(digitList.digits[digitIndex++] + zeroDelta));
                }
                else
                {
                    result.append(zero);
                }
            }

            // Record field information for caller.
            if (fieldPosition.getField() == NumberFormat.FRACTION_FIELD)
            fieldPosition.setEndIndex(result.length());
        }

        result.append(isNegative ? negativeSuffix : positiveSuffix);

        return result;
    }

    public Number parse(String text, ParsePosition parsePosition)
    {
        // special case NaN
        if (text.regionMatches(parsePosition.index, symbols.getNaN(),
                   0, symbols.getNaN().length())) {
            parsePosition.index = parsePosition.index + symbols.getNaN().length();
            return new Double(Double.NaN);
        }

        boolean[] status = new boolean[STATUS_LENGTH];

        if (!subparse(text, parsePosition, digitList, false, status))
            return null;

        double  doubleResult = 0.0;
        long    longResult = 0;
        boolean gotDouble = true;

        // Finally, have DigitList parse the digits into a value.
        if (status[STATUS_INFINITE])
        {
            doubleResult = Double.POSITIVE_INFINITY;
        }
        else if (digitList.fitsIntoLong(status[STATUS_POSITIVE]))
        {
            gotDouble = false;
            longResult = digitList.getLong();
        }
        else doubleResult = digitList.getDouble();

        // Divide by multiplier. We have to be careful here not to do unneeded
        // conversions between double and long.
        if (multiplier != 1)
        {
            if (gotDouble)
                doubleResult /= multiplier;
            else {
                // Avoid converting to double if we can
                if (longResult % multiplier == 0) {
                    longResult /= multiplier;
                } else {
                    doubleResult = ((double)longResult) / multiplier;
                    if (doubleResult < 0) doubleResult = -doubleResult;
                    gotDouble = true;
                }
            }
        }

        if (!status[STATUS_POSITIVE])
        {
            doubleResult = -doubleResult;
            longResult = -longResult;
        }

        // At this point, if we divided the result by the multiplier, the result may
        // fit into a long.  We check for this case and return a long if possible.
        // We must do this AFTER applying the negative (if appropriate) in order to
        // handle the case of LONG_MIN; otherwise, if we do this with a positive value
        // -LONG_MIN, the double is > 0, but the long is < 0.  This is a C++-specific
        // situation.  We also must retain a double in the case of -0.0, which will
        // compare as == to a long 0 cast to a double (bug 4162852).
        if (multiplier != 1 && gotDouble)
        {
            longResult = (long)doubleResult;
            gotDouble = (doubleResult != (double)longResult)
                || (doubleResult == 0.0 && !status[STATUS_POSITIVE]);
        }

        return gotDouble ? (Number)new Double(doubleResult) : (Number)new Long(longResult);
    }

    private static final int STATUS_INFINITE = 0;
    private static final int STATUS_POSITIVE = 1;
    private static final int STATUS_LENGTH   = 2;

    /**
     * Parse the given text into a number.  The text is parsed beginning at
     * parsePosition, until an unparseable character is seen.
     * @param text The string to parse.
     * @param parsePosition The position at which to being parsing.  Upon
     * return, the first unparseable character.
     * @param digits The DigitList to set to the parsed value.
     * @param isExponent If true, parse an exponent.  This means no
     * infinite values and integer only.
     * @param status Upon return contains boolean status flags indicating
     * whether the value was infinite and whether it was positive.
     */
    private final boolean subparse(String text, ParsePosition parsePosition,
                   DigitList digits, boolean isExponent,
                   boolean status[])
    {
        int position = parsePosition.index;
        int oldStart = parsePosition.index;
        int backup;

        // check for positivePrefix; take longest
        boolean gotPositive = text.regionMatches(position,positivePrefix,0,
                                                 positivePrefix.length());
        boolean gotNegative = text.regionMatches(position,negativePrefix,0,
                                                 negativePrefix.length());
        if (gotPositive && gotNegative) {
            if (positivePrefix.length() > negativePrefix.length())
                gotNegative = false;
            else if (positivePrefix.length() < negativePrefix.length())
                gotPositive = false;
        }
        if (gotPositive) {
            position += positivePrefix.length();
        } else if (gotNegative) {
            position += negativePrefix.length();
        } else {
            parsePosition.errorIndex = position;
            return false;
        }
        // process digits or Inf, find decimal position
        status[STATUS_INFINITE] = false;
        if (!isExponent && text.regionMatches(position,symbols.getInfinity(),0,
                          symbols.getInfinity().length()))
        {
            position += symbols.getInfinity().length();
            status[STATUS_INFINITE] = true;
        } else {
            // We now have a string of digits, possibly with grouping symbols,
            // and decimal points.  We want to process these into a DigitList.
            // We don't want to put a bunch of leading zeros into the DigitList
            // though, so we keep track of the location of the decimal point,
            // put only significant digits into the DigitList, and adjust the
            // exponent as needed.

            digits.decimalAt = digits.count = 0;
            char zero = symbols.getZeroDigit();
            char decimal = isCurrencyFormat ?
            symbols.getMonetaryDecimalSeparator() : symbols.getDecimalSeparator();
            char grouping = symbols.getGroupingSeparator();
            char exponentChar = symbols.getExponentialSymbol();
            boolean sawDecimal = false;
            boolean sawExponent = false;
            boolean sawDigit = false;
            int exponent = 0; // Set to the exponent value, if any

            // We have to track digitCount ourselves, because digits.count will
            // pin when the maximum allowable digits is reached.
            int digitCount = 0;

            backup = -1;
            for (; position < text.length(); ++position)
            {
                char ch = text.charAt(position);

                /* We recognize all digit ranges, not only the Latin digit range
                 * '0'..'9'.  We do so by using the Character.digit() method,
                 * which converts a valid Unicode digit to the range 0..9.
                 *
                 * The character 'ch' may be a digit.  If so, place its value
                 * from 0 to 9 in 'digit'.  First try using the locale digit,
                 * which may or MAY NOT be a standard Unicode digit range.  If
                 * this fails, try using the standard Unicode digit ranges by
                 * calling Character.digit().  If this also fails, digit will
                 * have a value outside the range 0..9.
                 */
                int digit = ch - zero;
                if (digit < 0 || digit > 9) digit = Character.digit(ch, 10);

                if (digit == 0)
                {
                    // Cancel out backup setting (see grouping handler below)
                    backup = -1; // Do this BEFORE continue statement below!!!
                    sawDigit = true;

                    // Handle leading zeros
                    if (digits.count == 0)
                    {
                        // Ignore leading zeros in integer part of number.
                        if (!sawDecimal) continue;

                        // If we have seen the decimal, but no significant digits yet,
                        // then we account for leading zeros by decrementing the
                        // digits.decimalAt into negative values.
                        --digits.decimalAt;
                    }
                    else
                    {
                        ++digitCount;
                        digits.append((char)(digit + '0'));
                    }
                }
                else if (digit > 0 && digit <= 9) // [sic] digit==0 handled above
                {
                    sawDigit = true;
                    ++digitCount;
                    digits.append((char)(digit + '0'));

                    // Cancel out backup setting (see grouping handler below)
                    backup = -1;
                }
                else if (!isExponent && ch == decimal)
                {
                    // If we're only parsing integers, or if we ALREADY saw the
                    // decimal, then don't parse this one.
                    if (isParseIntegerOnly() || sawDecimal) break;
                    digits.decimalAt = digitCount; // Not digits.count!
                    sawDecimal = true;
                }
                else if (!isExponent && ch == grouping && isGroupingUsed())
                {
                    if (sawDecimal) {
                        break;
                    }
                    // Ignore grouping characters, if we are using them, but require
                    // that they be followed by a digit.  Otherwise we backup and
                    // reprocess them.
                    backup = position;
                }
                else if (!isExponent && ch == exponentChar && !sawExponent)
                {
                    // Process the exponent by recursively calling this method.
                    ParsePosition pos = new ParsePosition(position + 1);
                    boolean[] stat = new boolean[STATUS_LENGTH];
                    DigitList exponentDigits = new DigitList();

                    if (subparse(text, pos, exponentDigits, true, stat) &&
                    exponentDigits.fitsIntoLong(stat[STATUS_POSITIVE]))
                    {
                    position = pos.index; // Advance past the exponent
                    exponent = (int)exponentDigits.getLong();
                    if (!stat[STATUS_POSITIVE]) exponent = -exponent;
                    sawExponent = true;
                    }
                    break; // Whether we fail or succeed, we exit this loop
                }
                else break;
            }

            if (backup != -1) position = backup;

            // If there was no decimal point we have an integer
            if (!sawDecimal) digits.decimalAt = digitCount; // Not digits.count!

            // Adjust for exponent, if any
            digits.decimalAt += exponent;

            // If none of the text string was recognized.  For example, parse
            // "x" with pattern "#0.00" (return index and error index both 0)
            // parse "$" with pattern "$#0.00". (return index 0 and error index
            // 1).
            if (!sawDigit && digitCount == 0) {
                parsePosition.index = oldStart;
                parsePosition.errorIndex = oldStart;
                return false;
            }
        }

        // check for positiveSuffix
        if (gotPositive)
            gotPositive = text.regionMatches(position,positiveSuffix,0,
                                             positiveSuffix.length());
        if (gotNegative)
            gotNegative = text.regionMatches(position,negativeSuffix,0,
                                             negativeSuffix.length());

        // if both match, take longest
        if (gotPositive && gotNegative) {
            if (positiveSuffix.length() > negativeSuffix.length())
                gotNegative = false;
            else if (positiveSuffix.length() < negativeSuffix.length())
                gotPositive = false;
        }

        // fail if neither or both
        if (gotPositive == gotNegative) {
            parsePosition.errorIndex = position;
            return false;
        }

        parsePosition.index = position +
            (gotPositive ? positiveSuffix.length() : negativeSuffix.length()); // mark success!

        status[STATUS_POSITIVE] = gotPositive;
        if (parsePosition.index == oldStart) {
            parsePosition.errorIndex = position;
            return false;
        }
        return true;
    }

    /**
     * Returns the decimal format symbols, which is generally not changed
     * by the programmer or user.
     * @return desired DecimalFormatSymbols
     * @see java.text.DecimalFormatSymbols
     */
    public DecimalFormatSymbols getDecimalFormatSymbols() {
        try {
            // don't allow multiple references
            return (DecimalFormatSymbols) symbols.clone();
        } catch (Exception foo) {
            return null; // should never happen
        }
    }


    /**
     * Sets the decimal format symbols, which is generally not changed
     * by the programmer or user.
     * @param newSymbols desired DecimalFormatSymbols
     * @see java.text.DecimalFormatSymbols
     */
    public void setDecimalFormatSymbols(DecimalFormatSymbols newSymbols) {
        try {
            // don't allow multiple references
            symbols = (DecimalFormatSymbols) newSymbols.clone();
        } catch (Exception foo) {
            // should never happen
        }
    }

    /**
     * Get the positive prefix.
     * <P>Examples: +123, $123, sFr123
     */
    public String getPositivePrefix () {
        return positivePrefix;
    }

    /**
     * Set the positive prefix.
     * <P>Examples: +123, $123, sFr123
     */
    public void setPositivePrefix (String newValue) {
        positivePrefix = newValue;
    }

    /**
     * Get the negative prefix.
     * <P>Examples: -123, ($123) (with negative suffix), sFr-123
     */
    public String getNegativePrefix () {
        return negativePrefix;
    }

    /**
     * Set the negative prefix.
     * <P>Examples: -123, ($123) (with negative suffix), sFr-123
     */
    public void setNegativePrefix (String newValue) {
        negativePrefix = newValue;
    }

    /**
     * Get the positive suffix.
     * <P>Example: 123%
     */
    public String getPositiveSuffix () {
        return positiveSuffix;
    }

    /**
     * Set the positive suffix.
     * <P>Example: 123%
     */
    public void setPositiveSuffix (String newValue) {
        positiveSuffix = newValue;
    }

    /**
     * Get the negative suffix.
     * <P>Examples: -123%, ($123) (with positive suffixes)
     */
    public String getNegativeSuffix () {
        return negativeSuffix;
    }

    /**
     * Set the positive suffix.
     * <P>Examples: 123%
     */
    public void setNegativeSuffix (String newValue) {
        negativeSuffix = newValue;
    }

    /**
     * Get the multiplier for use in percent, permill, etc.
     * For a percentage, set the suffixes to have "%" and the multiplier to be 100.
     * (For Arabic, use arabic percent symbol).
     * For a permill, set the suffixes to have "\u2031" and the multiplier to be 1000.
     * <P>Examples: with 100, 1.23 -> "123", and "123" -> 1.23
     */
    public int getMultiplier () {
        return multiplier;
    }

    /**
     * Set the multiplier for use in percent, permill, etc.
     * For a percentage, set the suffixes to have "%" and the multiplier to be 100.
     * (For Arabic, use arabic percent symbol).
     * For a permill, set the suffixes to have "\u2031" and the multiplier to be 1000.
     * <P>Examples: with 100, 1.23 -> "123", and "123" -> 1.23
     */
    public void setMultiplier (int newValue) {
        multiplier = newValue;
    }

    /**
     * Return the grouping size. Grouping size is the number of digits between
     * grouping separators in the integer portion of a number.  For example,
     * in the number "123,456.78", the grouping size is 3.
     * @see #setGroupingSize
     * @see java.text.NumberFormat#isGroupingUsed
     * @see java.text.DecimalFormatSymbols#getGroupingSeparator
     */
    public int getGroupingSize () {
        return groupingSize;
    }

    /**
     * Set the grouping size. Grouping size is the number of digits between
     * grouping separators in the integer portion of a number.  For example,
     * in the number "123,456.78", the grouping size is 3.
     * @see #getGroupingSize
     * @see java.text.NumberFormat#setGroupingUsed
     * @see java.text.DecimalFormatSymbols#setGroupingSeparator
     */
    public void setGroupingSize (int newValue) {
        groupingSize = (byte)newValue;
    }

    /**
     * Allows you to get the behavior of the decimal separator with integers.
     * (The decimal separator will always appear with decimals.)
     * <P>Example: Decimal ON: 12345 -> 12345.; OFF: 12345 -> 12345
     */
    public boolean isDecimalSeparatorAlwaysShown() {
        return decimalSeparatorAlwaysShown;
    }

    /**
     * Allows you to set the behavior of the decimal separator with integers.
     * (The decimal separator will always appear with decimals.)
     * <P>Example: Decimal ON: 12345 -> 12345.; OFF: 12345 -> 12345
     */
    public void setDecimalSeparatorAlwaysShown(boolean newValue) {
        decimalSeparatorAlwaysShown = newValue;
    }

    /**
     * Standard override; no change in semantics.
     */
    public Object clone() {
        try {
            DecimalFormat other = (DecimalFormat) super.clone();
            other.symbols = (DecimalFormatSymbols) symbols.clone();
            return other;
        } catch (Exception e) {
            throw new InternalError();
        }
    }

    /**
     * Overrides equals
     */
    public boolean equals(Object obj)
    {
        if (obj == null) return false;
        if (!super.equals(obj)) return false; // super does class check
        DecimalFormat other = (DecimalFormat) obj;
        return (positivePrefix.equals(other.positivePrefix)
            && positiveSuffix.equals(other.positiveSuffix)
            && negativePrefix.equals(other.negativePrefix)
            && negativeSuffix.equals(other.negativeSuffix)
            && multiplier == other.multiplier
            && groupingSize == other.groupingSize
            && decimalSeparatorAlwaysShown == other.decimalSeparatorAlwaysShown
            && useExponentialNotation == other.useExponentialNotation
            && (!useExponentialNotation ||
                minExponentDigits == other.minExponentDigits)
            && symbols.equals(other.symbols));
    }

    /**
     * Overrides hashCode
     */
    public int hashCode() {
        return super.hashCode() * 37 + positivePrefix.hashCode();
        // just enough fields for a reasonable distribution
    }

    /**
     * Synthesizes a pattern string that represents the current state
     * of this Format object.
     * @see #applyPattern
     */
    public String toPattern() {
        return toPattern( false );
    }

    /**
     * Synthesizes a localized pattern string that represents the current
     * state of this Format object.
     * @see #applyPattern
     */
    public String toLocalizedPattern() {
        return toPattern( true );
    }

    /**
     * Append an affix to the given StringBuffer, using quotes if
     * there are special characters.  Single quotes themselves must be
     * escaped in either case.
     */
    private void appendAffix(StringBuffer buffer, String affix, boolean localized) {
        boolean needQuote;
        if (localized) {
            needQuote = affix.indexOf(symbols.getZeroDigit()) >= 0
                || affix.indexOf(symbols.getGroupingSeparator()) >= 0
                || affix.indexOf(symbols.getDecimalSeparator()) >= 0
                || affix.indexOf(symbols.getPercent()) >= 0
                || affix.indexOf(symbols.getPerMill()) >= 0
                || affix.indexOf(symbols.getDigit()) >= 0
                || affix.indexOf(symbols.getPatternSeparator()) >= 0
                || affix.indexOf(symbols.getExponentialSymbol()) >= 0;
        }
        else {
            needQuote = affix.indexOf(PATTERN_ZERO_DIGIT) >= 0
                || affix.indexOf(PATTERN_GROUPING_SEPARATOR) >= 0
                || affix.indexOf(PATTERN_DECIMAL_SEPARATOR) >= 0
                || affix.indexOf(PATTERN_PERCENT) >= 0
                || affix.indexOf(PATTERN_PER_MILLE) >= 0
                || affix.indexOf(PATTERN_DIGIT) >= 0
                || affix.indexOf(PATTERN_SEPARATOR) >= 0
                || affix.indexOf(PATTERN_EXPONENT) >= 0;
        }
        if (needQuote) buffer.append('\'');
        if (affix.indexOf('\'') < 0) buffer.append(affix);
        else {
            for (int j=0; j<affix.length(); ++j) {
                char c = affix.charAt(j);
                buffer.append(c);
                if (c == '\'') buffer.append(c);
            }
        }
        if (needQuote) buffer.append('\'');
    }

    /**
     * Does the real work of generating a pattern.  */
    private String toPattern(boolean localized) {
        StringBuffer result = new StringBuffer();
        for (int j = 1; j >= 0; --j) {
            if (j == 1)
                appendAffix(result, positivePrefix, localized);
            else appendAffix(result, negativePrefix, localized);
        int i;
        if (useExponentialNotation)
        {
            for (i = getMaximumIntegerDigits(); i > 0; --i)
            {
                if (i == groupingSize)
                    result.append(localized ? symbols.getGroupingSeparator() :
                                  PATTERN_GROUPING_SEPARATOR);
                if (i <= getMinimumIntegerDigits())
                    result.append(localized ? symbols.getZeroDigit() :
                                  PATTERN_ZERO_DIGIT);
                else
                    result.append(localized ? symbols.getDigit() :
                                  PATTERN_DIGIT);
                
            }
        }
        else
        {
        int tempMax = Math.max(groupingSize, getMinimumIntegerDigits())+1;
        for (i = tempMax; i > 0; --i) {
            if (i == groupingSize)
            result.append(localized ? symbols.getGroupingSeparator() :
                      PATTERN_GROUPING_SEPARATOR);
            if (i <= getMinimumIntegerDigits()) {
            result.append(localized ? symbols.getZeroDigit() :
                      PATTERN_ZERO_DIGIT);
            } else {
            result.append(localized ? symbols.getDigit() :
                      PATTERN_DIGIT);
            }
        }
            }
            if (getMaximumFractionDigits() > 0 || decimalSeparatorAlwaysShown)
                result.append(localized ? symbols.getDecimalSeparator() :
                              PATTERN_DECIMAL_SEPARATOR);
            for (i = 0; i < getMaximumFractionDigits(); ++i) {
                if (i < getMinimumFractionDigits()) {
                    result.append(localized ? symbols.getZeroDigit() :
                                  PATTERN_ZERO_DIGIT);
                } else {
                    result.append(localized ? symbols.getDigit() :
                                  PATTERN_DIGIT);
                }
            }
        if (useExponentialNotation)
        {
        result.append(localized ? symbols.getExponentialSymbol() :
                  PATTERN_EXPONENT);
        for (i=0; i<minExponentDigits; ++i)
                    result.append(localized ? symbols.getZeroDigit() :
                                  PATTERN_ZERO_DIGIT);
        }
            if (j == 1) {
                appendAffix(result, positiveSuffix, localized);
                if (negativeSuffix.equals(positiveSuffix)) {
                    if (negativePrefix.equals(symbols.getMinusSign() + positivePrefix))
                        break;
                }
                result.append(localized ? symbols.getPatternSeparator() :
                              PATTERN_SEPARATOR);
            } else appendAffix(result, negativeSuffix, localized);
        }
        return result.toString();
    }


    /**
     * Apply the given pattern to this Format object.  A pattern is a
     * short-hand specification for the various formatting properties.
     * These properties can also be changed individually through the
     * various setter methods.
     * <p>
     * There is no limit to integer digits are set
     * by this routine, since that is the typical end-user desire;
     * use setMaximumInteger if you want to set a real value.
     * For negative numbers, use a second pattern, separated by a semicolon
     * <P>Example "#,#00.0#" -> 1,234.56
     * <P>This means a minimum of 2 integer digits, 1 fraction digit, and
     * a maximum of 2 fraction digits.
     * <p>Example: "#,#00.0#;(#,#00.0#)" for negatives in parantheses.
     * <p>In negative patterns, the minimum and maximum counts are ignored;
     * these are presumed to be set in the positive pattern.
     */
    public void applyPattern( String pattern ) {
        applyPattern( pattern, false );
    }

    /**
     * Apply the given pattern to this Format object.  The pattern
     * is assumed to be in a localized notation. A pattern is a
     * short-hand specification for the various formatting properties.
     * These properties can also be changed individually through the
     * various setter methods.
     * <p>
     * There is no limit to integer digits are set
     * by this routine, since that is the typical end-user desire;
     * use setMaximumInteger if you want to set a real value.
     * For negative numbers, use a second pattern, separated by a semicolon
     * <P>Example "#,#00.0#" -> 1,234.56
     * <P>This means a minimum of 2 integer digits, 1 fraction digit, and
     * a maximum of 2 fraction digits.
     * <p>Example: "#,#00.0#;(#,#00.0#)" for negatives in parantheses.
     * <p>In negative patterns, the minimum and maximum counts are ignored;
     * these are presumed to be set in the positive pattern.
     */
    public void applyLocalizedPattern( String pattern ) {
        applyPattern( pattern, true );
    }

    /**
     * Does the real work of applying a pattern.
     */
    private void applyPattern(String pattern, boolean localized)
    {
        char zeroDigit         = PATTERN_ZERO_DIGIT;
        char groupingSeparator = PATTERN_GROUPING_SEPARATOR;
        char decimalSeparator  = PATTERN_DECIMAL_SEPARATOR;
        char percent           = PATTERN_PERCENT;
        char perMill           = PATTERN_PER_MILLE;
        char digit             = PATTERN_DIGIT;
        char separator         = PATTERN_SEPARATOR;
        char exponent          = PATTERN_EXPONENT;
        if (localized) {
            zeroDigit         = symbols.getZeroDigit();
            groupingSeparator = symbols.getGroupingSeparator();
            decimalSeparator  = symbols.getDecimalSeparator();
            percent           = symbols.getPercent();
            perMill           = symbols.getPerMill();
            digit             = symbols.getDigit();
            separator         = symbols.getPatternSeparator();
            exponent          = symbols.getExponentialSymbol();
        }
        boolean gotNegative = false;

        decimalSeparatorAlwaysShown = false;
        isCurrencyFormat = false;
        useExponentialNotation = false;

        // Two variables are used to record the subrange of the pattern
        // occupied by phase 1.  This is used during the processing of the
        // second pattern (the one representing negative numbers) to ensure
        // that no deviation exists in phase 1 between the two patterns.
        int phaseOneStart = 0;
        int phaseOneLength = 0;
        /** Back-out comment : HShih
         * boolean phaseTwo = false;
         */

        int start = 0;
        for (int j = 1; j >= 0 && start < pattern.length(); --j)
        {
            boolean inQuote = false;
            StringBuffer prefix = new StringBuffer();
            StringBuffer suffix = new StringBuffer();
            int decimalPos = -1;
            int multiplier = 1;
            int digitLeftCount = 0, zeroDigitCount = 0, digitRightCount = 0;
            byte groupingCount = -1;

            // The phase ranges from 0 to 2.  Phase 0 is the prefix.  Phase 1 is
            // the section of the pattern with digits, decimal separator,
            // grouping characters.  Phase 2 is the suffix.  In phases 0 and 2,
            // percent, permille, and currency symbols are recognized and
            // translated.  The separation of the characters into phases is
            // strictly enforced; if phase 1 characters are to appear in the
            // suffix, for example, they must be quoted.
            int phase = 0;

            // The affix is either the prefix or the suffix.
            StringBuffer affix = prefix;

            for (int pos = start; pos < pattern.length(); ++pos)
            {
                char ch = pattern.charAt(pos);
            switch (phase)
            {
                case 0:
                case 2:
                // Process the prefix / suffix characters
                if (inQuote)
                {
                    // A quote within quotes indicates either the closing
                    // quote or two quotes, which is a quote literal.  That is,
                    // we have the second quote in 'do' or 'don''t'.
                    if (ch == QUOTE)
                    {
                        if ((pos+1) < pattern.length() &&
                        pattern.charAt(pos+1) == QUOTE)
                        {
                        ++pos;
                        affix.append(ch); // 'don''t'
                        }
                        else
                        {
                        inQuote = false; // 'do'
                        }
                        continue;
                    }
                }
                else
                {
                    // Process unquoted characters seen in prefix or suffix
                    // phase.
                    if (ch == digit ||
                        ch == zeroDigit ||
                        ch == groupingSeparator ||
                        ch == decimalSeparator)
                    {
                        // Any of these characters implicitly begins the next
                        // phase.  If we are in phase 2, there is no next phase,
                        // so these characters are illegal.
                        /**
                         * 1.2 Back-out comment : HShih
                         * Can't throw exception here.
                         * if (phase == 2)
                         *    throw new IllegalArgumentException("Unquoted special character '" +
                         *                   ch + "' in pattern \"" +
                         *                   pattern + '"');
                         */
                        phase = 1;
                        if (j == 1) phaseOneStart = pos;
                        --pos; // Reprocess this character
                        continue;
                    }
                    else if (ch == CURRENCY_SIGN)
                    {
                        // Use lookahead to determine if the currency sign is
                        // doubled or not.
                        boolean doubled = (pos + 1) < pattern.length() &&
                        pattern.charAt(pos + 1) == CURRENCY_SIGN;
                        affix.append(doubled ?
                             symbols.getInternationalCurrencySymbol() :
                             symbols.getCurrencySymbol());
                        if (doubled) ++pos; // Skip over the doubled character
                        isCurrencyFormat = true;
                        continue;
                    }
                    else if (ch == QUOTE)
                    {
                        // A quote outside quotes indicates either the opening
                        // quote or two quotes, which is a quote literal.  That is,
                        // we have the first quote in 'do' or o''clock.
                        if (ch == QUOTE)
                        {
                        if ((pos+1) < pattern.length() &&
                            pattern.charAt(pos+1) == QUOTE)
                        {
                            ++pos;
                            affix.append(ch); // o''clock
                        }
                        else
                        {
                            inQuote = true; // 'do'
                        }
                        continue;
                        }
                    }
                    else if (ch == separator)
                    {
                        // Don't allow separators before we see digit characters of phase
                        // 1, and don't allow separators in the second pattern (j == 0).
                        if (phase == 0 || j == 0)
                        throw new IllegalArgumentException("Unquoted special character '" +
                                           ch + "' in pattern \"" +
                                           pattern + '"');
                        start = pos + 1;
                        pos = pattern.length();
                        continue;
                    }

                    // Next handle characters which are appended directly.
                    else if (ch == percent)
                    {
                        if (multiplier != 1)
                        throw new IllegalArgumentException("Too many percent/permille characters in pattern \"" +
                                           pattern + '"');
                        multiplier = 100;
                        ch = symbols.getPercent();
                    }
                    else if (ch == perMill)
                    {
                        if (multiplier != 1)
                        throw new IllegalArgumentException("Too many percent/permille characters in pattern \"" +
                                           pattern + '"');
                        multiplier = 1000;
                        ch = symbols.getPerMill();
                    }
                }
                // Note that if we are within quotes, or if this is an unquoted,
                // non-special character, then we usually fall through to here.
                affix.append(ch);
                break;
            case 1:
                // Phase one must be identical in the two sub-patterns.  We
                // enforce this by doing a direct comparison.  While
                // processing the first sub-pattern, we just record its
                // length.  While processing the second, we compare
                // characters.
                if (j == 1) ++phaseOneLength;
                else
                {
                    /**
                     * 1.2 Back-out comment : HShih
                     * if (ch != pattern.charAt(phaseOneStart++))
                     *    throw new IllegalArgumentException("Subpattern mismatch in \"" +
                     *                       pattern + '"');
                     * phaseTwo = true;
                     */
                    if (--phaseOneLength == 0)
                    {
                        phase = 2;
                        affix = suffix;
                    }
                    continue;
                }

                // Process the digits, decimal, and grouping characters.  We
                // record five pieces of information.  We expect the digits
                // to occur in the pattern ####0000.####, and we record the
                // number of left digits, zero (central) digits, and right
                // digits.  The position of the last grouping character is
                // recorded (should be somewhere within the first two blocks
                // of characters), as is the position of the decimal point,
                // if any (should be in the zero digits).  If there is no
                // decimal point, then there should be no right digits.
                if (ch == digit)
                {
                    if (zeroDigitCount > 0) ++digitRightCount; else ++digitLeftCount;
                    if (groupingCount >= 0 && decimalPos < 0) ++groupingCount;
                }
                else if (ch == zeroDigit)
                {
                    if (digitRightCount > 0)
                       throw new IllegalArgumentException("Unexpected '0' in pattern \"" +
                                       pattern + '"');
                    ++zeroDigitCount;
                    if (groupingCount >= 0 && decimalPos < 0) ++groupingCount;
                }
                else if (ch == groupingSeparator)
                {
                    groupingCount = 0;
                }
                else if (ch == decimalSeparator)
                {
                    if (decimalPos >= 0)
                        throw new IllegalArgumentException("Multiple decimal separators in pattern \"" +
                                       pattern + '"');
                    decimalPos = digitLeftCount + zeroDigitCount + digitRightCount;
                }
                else if (ch == exponent)
                {
                    if (useExponentialNotation)
                        throw new IllegalArgumentException("Multiple exponential " +
                                       "symbols in pattern \"" +
                                       pattern + '"');
                    useExponentialNotation = true;
                    minExponentDigits = 0;

                    // Use lookahead to parse out the exponential part of the
                    // pattern, then jump into phase 2.
                    while (++pos < pattern.length() &&
                           pattern.charAt(pos) == zeroDigit)
                    {
                        ++minExponentDigits;
                        ++phaseOneLength;
                    }

                    if ((digitLeftCount + zeroDigitCount) < 1 ||
                        minExponentDigits < 1)
                        throw new IllegalArgumentException("Malformed exponential " +
                                           "pattern \"" +
                                           pattern + '"');

                    // Transition to phase 2
                    phase = 2;
                    affix = suffix;
                    --pos;
                    continue;
                }
                else
                {
                    phase = 2;
                    affix = suffix;
                    --pos;
                    --phaseOneLength;
                    continue;
                }
                break;
            }
        }
        /**
         * 1.2 Back-out comment : HShih
         * if (phaseTwo && phaseOneLength > 0)
         *      throw new IllegalArgumentException("Subpattern mismatch in \"" +
         *                                   pattern + '"');
         */
        // Handle patterns with no '0' pattern character.  These patterns
        // are legal, but must be interpreted.  "##.###" -> "#0.###".
        // ".###" -> ".0##".
        /* We allow patterns of the form "####" to produce a zeroDigitCount of
         * zero (got that?); although this seems like it might make it possible
         * for format() to produce empty strings, format() checks for this
         * condition and outputs a zero digit in this situation.  Having a
         * zeroDigitCount of zero yields a minimum integer digits of zero, which
         * allows proper round-trip patterns.  That is, we don't want "#" to
         * become "#0" when toPattern() is called (even though that's what it
         * really is, semantically).  */
        if (zeroDigitCount == 0 && digitLeftCount > 0 &&
            decimalPos >= 0) {
            // Handle "###.###" and "###." and ".###"
            int n = decimalPos;
            if (n == 0) ++n; // Handle ".###"
            digitRightCount = digitLeftCount - n;
            digitLeftCount = n - 1;
            zeroDigitCount = 1;
        }

        // Do syntax checking on the digits.
        if ((decimalPos < 0 && digitRightCount > 0) ||
        (decimalPos >= 0 &&
         (decimalPos < digitLeftCount ||
          decimalPos > (digitLeftCount + zeroDigitCount))) ||
        groupingCount == 0 ||
        inQuote)
            throw new IllegalArgumentException("Malformed pattern \"" +
                               pattern + '"');

            if (j == 1) {
                this.positivePrefix = prefix.toString();
                this.positiveSuffix = suffix.toString();
                this.negativePrefix = positivePrefix;   // assume these for now
                this.negativeSuffix = positiveSuffix;
            int digitTotalCount = digitLeftCount + zeroDigitCount + digitRightCount;
            /* The effectiveDecimalPos is the position the decimal is at or
             * would be at if there is no decimal.  Note that if decimalPos<0,
             * then digitTotalCount == digitLeftCount + zeroDigitCount.  */
            int effectiveDecimalPos = decimalPos >= 0 ? decimalPos : digitTotalCount;
            setMinimumIntegerDigits(effectiveDecimalPos - digitLeftCount);
            setMaximumIntegerDigits(useExponentialNotation ?
                        digitLeftCount + getMinimumIntegerDigits() : DOUBLE_INTEGER_DIGITS);
            setMaximumFractionDigits(decimalPos >= 0 ? (digitTotalCount - decimalPos) : 0);
            setMinimumFractionDigits(decimalPos >= 0 ?
                         (digitLeftCount + zeroDigitCount - decimalPos) : 0);
            setGroupingUsed(groupingCount > 0);
            this.groupingSize = (groupingCount > 0) ? groupingCount : 0;
            this.multiplier = multiplier;
            setDecimalSeparatorAlwaysShown(decimalPos == 0 || decimalPos == digitTotalCount);
            } else {
                this.negativePrefix = prefix.toString();
                this.negativeSuffix = suffix.toString();
                gotNegative = true;
            }
        }

    // If there was no negative pattern, or if the negative pattern is identical
    // to the positive pattern, then prepend the minus sign to the positive
    // pattern to form the negative pattern.
        if (!gotNegative ||
            (negativePrefix.equals(positivePrefix)
             && negativeSuffix.equals(positiveSuffix))) {
            negativeSuffix = positiveSuffix;
            negativePrefix = symbols.getMinusSign() + positivePrefix;
        }
    }

    /**
     * Sets the maximum number of digits allowed in the integer portion of a
     * number. This override limits the integer digit count to 309.
     * @see NumberFormat#setMaximumIntegerDigits
     */
    public void setMaximumIntegerDigits(int newValue) {
        super.setMaximumIntegerDigits(Math.min(newValue, DOUBLE_INTEGER_DIGITS));
    }

    /**
     * Sets the minimum number of digits allowed in the integer portion of a
     * number. This override limits the integer digit count to 309.
     * @see NumberFormat#setMinimumIntegerDigits
     */
    public void setMinimumIntegerDigits(int newValue) {
        super.setMinimumIntegerDigits(Math.min(newValue, DOUBLE_INTEGER_DIGITS));
    }

    /**
     * Sets the maximum number of digits allowed in the fraction portion of a
     * number. This override limits the fraction digit count to 340.
     * @see NumberFormat#setMaximumFractionDigits
     */
    public void setMaximumFractionDigits(int newValue) {
        super.setMaximumFractionDigits(Math.min(newValue, DOUBLE_FRACTION_DIGITS));
    }

    /**
     * Sets the minimum number of digits allowed in the fraction portion of a
     * number. This override limits the fraction digit count to 340.
     * @see NumberFormat#setMinimumFractionDigits
     */
    public void setMinimumFractionDigits(int newValue) {
        super.setMinimumFractionDigits(Math.min(newValue, DOUBLE_FRACTION_DIGITS));
    }

    /**
     * First, read the default serializable fields from the stream.  Then
     * if <code>serialVersionOnStream</code> is less than 1, indicating that
     * the stream was written by JDK 1.1, initialize <code>useExponentialNotation</code>
     * to false, since it was not present in JDK 1.1.
     * Finally, set serialVersionOnStream back to the maximum allowed value so that
     * default serialization will work properly if this object is streamed out again.
     */
    private void readObject(ObjectInputStream stream)
         throws IOException, ClassNotFoundException
    {
        stream.defaultReadObject();
        if (serialVersionOnStream < 1) {
            // Didn't have exponential fields
            useExponentialNotation = false;
        }
        serialVersionOnStream = currentSerialVersion;
        digitList = new DigitList();
    }

    //----------------------------------------------------------------------
    // INSTANCE VARIABLES
    //----------------------------------------------------------------------

    private transient DigitList digitList = new DigitList();

    /**
     * The symbol used as a prefix when formatting positive numbers, e.g. "+".
     *
     * @serial
     * @see #getPositivePrefix
     */
    private String  positivePrefix = "";

    /**
     * The symbol used as a suffix when formatting positive numbers.
     * This is often an empty string.
     *
     * @serial
     * @see #getPositiveSuffix
     */
    private String  positiveSuffix = "";

    /**
     * The symbol used as a prefix when formatting negative numbers, e.g. "-".
     *
     * @serial
     * @see #getNegativePrefix
     */
    private String  negativePrefix = "-";

    /**
     * The symbol used as a suffix when formatting negative numbers.
     * This is often an empty string.
     *
     * @serial
     * @see #getNegativeSuffix
     */
    private String  negativeSuffix = "";

    /**
     * The multiplier for use in percent, permill, etc.
     *
     * @serial
     * @see #getMultiplier
     */
    private int     multiplier = 1;
    
    /**
     * The number of digits between grouping separators in the integer
     * portion of a number.  Must be greater than 0 if
     * <code>NumberFormat.groupingUsed</code> is true.
     *
     * @serial
     * @see #getGroupingSize
     * @see java.text.NumberFormat#isGroupingUsed
     */
    private byte    groupingSize = 3;  // invariant, > 0 if useThousands
    
    /**
     * If true, forces the decimal separator to always appear in a formatted
     * number, even if the fractional part of the number is zero.
     *
     * @serial
     * @see #isDecimalSeparatorAlwaysShown
     */
    private boolean decimalSeparatorAlwaysShown = false;
    
    /**
     * True if this object represents a currency format.  This determines
     * whether the monetary decimal separator is used instead of the normal one.
     */
    private transient boolean isCurrencyFormat = false;
    
    /**
     * The <code>DecimalFormatSymbols</code> object used by this format.
     * It contains the symbols used to format numbers, e.g. the grouping separator,
     * decimal separator, and so on.
     *
     * @serial
     * @see #setDecimalFormatSymbols
     * @see java.text.DecimalFormatSymbols
     */
    private DecimalFormatSymbols symbols = null; // LIU new DecimalFormatSymbols();

    /**
     * True to force the use of exponential (i.e. scientific) notation when formatting
     * numbers.
     * <p>
     * Note that the JDK 1.2 public API provides no way to set this field,
     * even though it is supported by the implementation and the stream format.
     * The intent is that this will be added to the API in the future.
     *
     * @serial
     * @since JDK 1.2
     */
    private boolean useExponentialNotation;  // Newly persistent in JDK 1.2

    /**
     * The minimum number of digits used to display the exponent when a number is
     * formatted in exponential notation.  This field is ignored if
     * <code>useExponentialNotation</code> is not true.
     * <p>
     * Note that the JDK 1.2 public API provides no way to set this field,
     * even though it is supported by the implementation and the stream format.
     * The intent is that this will be added to the API in the future.
     *
     * @serial
     * @since JDK 1.2
     */
    private byte    minExponentDigits;       // Newly persistent in JDK 1.2

    //----------------------------------------------------------------------

    static final int currentSerialVersion = 1;

    /**
     * The internal serial version which says which version was written
     * Possible values are:
     * <ul>
     * <li><b>0</b> (default): versions before JDK 1.2
     * <li><b>1</b>: version from JDK 1.2 and later, which includes the two new fields
     *      <code>useExponentialNotation</code> and <code>minExponentDigits</code>.
     * </ul>
     * @since JDK 1.2
     * @serial
     */
    private int serialVersionOnStream = currentSerialVersion;

    //----------------------------------------------------------------------
    // CONSTANTS
    //----------------------------------------------------------------------

    // Constants for characters used in programmatic (unlocalized) patterns.
    private static final char       PATTERN_ZERO_DIGIT         = '0';
    private static final char       PATTERN_GROUPING_SEPARATOR = ',';
    private static final char       PATTERN_DECIMAL_SEPARATOR  = '.';
    private static final char       PATTERN_PER_MILLE          = '\u2030';
    private static final char       PATTERN_PERCENT            = '%';
    private static final char       PATTERN_DIGIT              = '#';
    private static final char       PATTERN_SEPARATOR          = ';';
    private static final char       PATTERN_EXPONENT           = 'E';

    /**
     * The CURRENCY_SIGN is the standard Unicode symbol for currency.  It
     * is used in patterns and substitued with either the currency symbol,
     * or if it is doubled, with the international currency symbol.  If the
     * CURRENCY_SIGN is seen in a pattern, then the decimal separator is
     * replaced with the monetary decimal separator.
     *
     * The CURRENCY_SIGN is not localized.
     */
    private static final char       CURRENCY_SIGN = '\u00A4';

    private static final char       QUOTE = '\'';

    // Upper limit on integer and fraction digits for a Java double
    static final int DOUBLE_INTEGER_DIGITS  = 309;
    static final int DOUBLE_FRACTION_DIGITS = 340;

    // Proclaim JDK 1.1 serial compatibility.
    static final long serialVersionUID = 864413376551465018L;

    /**
     * Cache to hold the NumberPattern of a Locale.
     */
    private static Hashtable cachedLocaleData = new Hashtable(3);
}

//eof
