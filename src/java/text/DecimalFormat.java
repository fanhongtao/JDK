/*
 * @(#)DecimalFormat.java	1.27 97/05/02
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
 * ;      separates formats.
 * -      default negative prefix.
 * %      divide by 100 and show as percentage
 * X      any other characters can be used in the prefix or suffix
 * '      used to quote special characters in a prefix or suffix.
 * </pre>
 * <p><strong>Notes</strong>
 * <p>
 * If there is no explicit negative subpattern, - is prefixed to the
 * positive form. That is, "0.00" alone is equivalent to "0.00;-0.00".
 *
 * <p>
 * Illegal formats, such as "#.#.#" or mixing '_' and '*' in the
 * same format, will cause an <code>ParseException</code> to be thrown.
 * From that <code>ParseException</code>, you can find the place in the string
 * where the error occurred.
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
 * This class only handles localized digits where the 10 digits
 * are contiguous in Unicode, from 0 to 9. Other digits sets
 * (such as superscripts) would need a different subclass.
 *
 * @see          java.util.Format
 * @see          java.util.NumberFormat
 * @see          java.util.ChoiceFormat
 * @version      1.27 05/02/97
 * @author       Mark Davis
 */
/*
 * Requested Features
 * Symbol Meaning
 * $      currency symbol as decimal point
 * …   escapes text
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
        // Get the pattern for the default locale.
        ResourceBundle rb = ResourceBundle.getBundle
                            ("java.text.resources.LocaleElements",
                             Locale.getDefault());
        String[] patterns = rb.getStringArray("NumberPatterns");
        applyPattern( patterns[0], false );

        this.symbols = new DecimalFormatSymbols( Locale.getDefault() );
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
        applyPattern( pattern, false );
        this.symbols = new DecimalFormatSymbols( Locale.getDefault() );
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
        applyPattern( pattern, false );
        this.symbols = symbols;
    }


    // Overrides
    public StringBuffer format(double number, StringBuffer result,
                               FieldPosition fieldPosition)
    {
        // Initialize
        fieldPosition.beginIndex = fieldPosition.endIndex = 0;

        if (Double.isNaN(number)) {
            result.append(symbols.getNaN());
        } else {
            boolean isNegative = (number < 0);
            if (!isNegative)
                result.append(positivePrefix);
            else {
                result.append(negativePrefix);
                number = -number;
            }
            if (Double.isInfinite(number)) {
                result.append(symbols.getInfinity());
            } else {
                if (multiplier != 1) number *= multiplier;
                digitList.set(number, getMaximumFractionDigits());
                appendNativeDigits(result, fieldPosition);
            }
            if (!isNegative)
                result.append(positiveSuffix);
            else result.append(negativeSuffix);
        }
        return result;
    }

    public StringBuffer format(long number, StringBuffer result,
                               FieldPosition fieldPosition) {
        // Initialize
        fieldPosition.beginIndex = fieldPosition.endIndex = 0;

        if (Double.isNaN(number)) {
            result.append(symbols.getNaN());
        } else {
            boolean isNegative = (number < 0);
            if (!isNegative)
                result.append(positivePrefix);
            else {
                result.append(negativePrefix);
                number = -number;
            }
            if (Double.isInfinite(number)) {
                result.append(symbols.getInfinity());
            } else {
                if (multiplier != 1) number *= multiplier;
                digitList.set(number);
                appendNativeDigits(result, fieldPosition);
            }
            if (!isNegative)
                result.append(positiveSuffix);
            else result.append(negativeSuffix);
        }
        return result;
    }

    public Number parse(String text, ParsePosition status) {
        int start = status.index;
        // special case NaN
        if (text.regionMatches(start,symbols.getNaN(),
                               0,symbols.getNaN().length())) {
            status.index = start + symbols.getNaN().length();
            return new Double(Double.NaN);
        }

        // check for positivePrefix; take longest
        boolean gotPositive = text.regionMatches(start,positivePrefix,0,
                                                 positivePrefix.length());
        boolean gotNegative = text.regionMatches(start,negativePrefix,0,
                                                 negativePrefix.length());
        if (gotPositive && gotNegative) {
            if (positivePrefix.length() > negativePrefix.length())
                gotNegative = false;
            else if (positivePrefix.length() < negativePrefix.length())
                gotPositive = false;
        }
        if (false) System.out.println("positive/negative:" + gotPositive
                                      + ", " + gotNegative);
        if (gotPositive) start += positivePrefix.length();
        else if (gotNegative) start += negativePrefix.length();
        else return null;

        // process digits or Inf, find decimal position
        double  doubleResult = Double.NaN;
        long    longResult = Long.MIN_VALUE;
        boolean gotDouble = true;
        if (text.regionMatches(start,symbols.getInfinity(),0,
                               symbols.getInfinity().length()))
        {
            start += symbols.getInfinity().length();
            digitList.decimalAt = start;
            doubleResult = Double.POSITIVE_INFINITY;
        } else {
            digitList.count = 0;
            digitList.decimalAt = -1;
            int backup = -1;
            int tentativeDecimal = -1;
	    boolean sawZeroDigit = false; // Temporary fix to 4048975 [LIU]
            for (;start < text.length(); ++start) {
                char ch = text.charAt(start);
                if (symbols.getZeroDigit() < ch && ch <= (char)
                        (symbols.getZeroDigit() + 9)) {
                    backup = -1;
                    digitList.decimalAt = tentativeDecimal;
                    digitList.append(ch - symbols.getZeroDigit() + '0');
                } else if (ch == symbols.getZeroDigit()) {
		    sawZeroDigit = true; // Temporary fix 4048975 [LIU]
                    if (digitList.count == 0 && tentativeDecimal == -1) // [LIU]
                        continue;
                    backup = -1;
                    digitList.decimalAt = tentativeDecimal;
                    if ((digitList.count != 0) || (tentativeDecimal != -1)) {
                        digitList.append(ch - symbols.getZeroDigit() +'0');
                    }
                } else if (digitList.decimalAt >= 0) {
                    break;
                } else if (ch == symbols.getDecimalSeparator() && !isParseIntegerOnly()) {
                    backup = start;
                    tentativeDecimal = digitList.count;
                } else if (ch == symbols.getGroupingSeparator() && isGroupingUsed()) {
                    backup = start;
                } else {
                    break;
                }
            }
            if (backup != -1) start = backup;
            if (digitList.decimalAt == -1)
                digitList.decimalAt = digitList.count;
            if (digitList.decimalAt == digitList.count
                && digitList.count <= DigitList.MAX_COUNT)
            {
                gotDouble = false;
                if (digitList.count == DigitList.MAX_COUNT && gotNegative &&
                        isLongMIN_VALUE(digitList))
                    longResult = Long.MIN_VALUE;
                else {
		    if (digitList.count == 0)
		      { // Temporary fix 4048975 [LIU]
			if (!sawZeroDigit) return null;
			longResult = 0;
		      } else
		    longResult = digitList.getLong();
		}
            } else {
		if (digitList.count == 0)
		    return null;
                doubleResult = digitList.getDouble();
	    }
        }

        // check for positiveSuffix
        if (gotPositive)
            gotPositive = text.regionMatches(start,positiveSuffix,0,
                                             positiveSuffix.length());
        if (gotNegative)
            gotNegative = text.regionMatches(start,negativeSuffix,0,
                                             negativeSuffix.length());

        // fail if neither ok
        if (!gotPositive && !gotNegative) return null;

        // if both match, take longest
        if (gotPositive && gotNegative) {
            if (positiveSuffix.length() > negativeSuffix.length())
                gotNegative = false;
            else if (positiveSuffix.length() < negativeSuffix.length())
                gotPositive = false;
            else
                return null; // fail if we can't distinguish!
        }

        // fail if neither or both
        if (gotPositive == gotNegative) return null;
        if (false) System.out.println("positive/negative:" + gotPositive
                                      + ", " + gotNegative);

        // return final value
        if (multiplier != 1)
            if (gotDouble)
                doubleResult /= multiplier;
            else {
                doubleResult = ((double)longResult) / multiplier;
                gotDouble = true;
            }
        if (gotPositive) {
            status.index = start + positiveSuffix.length();// mark success!
        } else {
            status.index = start + negativeSuffix.length();// mark success!
            doubleResult = -doubleResult;
            longResult = -longResult;
        }
        if (gotDouble)
            return new Double(doubleResult);
        else if (digitList.decimalAt == digitList.count && // no decimal
            (longResult >= Long.MIN_VALUE || longResult <= Long.MAX_VALUE))
            return new Long(longResult);
        else
            return new Double((double)longResult);
    }

    /**
     * Returns the decimal format symbols, which is generally not changed
     * by the programmer or user.
     * @return desired DecimalFormatSymbols
     * @see java.util.DecimalFormatSymbols
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
     * @see java.util.DecimalFormatSymbols
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
    };

    /**
     * Overrides equals
     */
    public boolean equals(Object obj)
    {
      if (!super.equals(obj)) return false; // super does class check
      DecimalFormat other = (DecimalFormat) obj;
      return (positivePrefix.equals(other.positivePrefix)
      	&& positiveSuffix.equals(other.positiveSuffix)
      	&& negativePrefix.equals(other.negativePrefix)
      	&& negativeSuffix.equals(other.negativeSuffix)
      	&& multiplier == other.multiplier
      	&& groupingSize == other.groupingSize
      	&& decimalSeparatorAlwaysShown == other.decimalSeparatorAlwaysShown
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
     * Does the real work of generating a pattern.
     */
    private String toPattern(boolean localized) {
        StringBuffer result = new StringBuffer();
        for (int j = 1; j >= 0; --j) {
            if (j == 1)
                result.append(positivePrefix);
            else result.append(negativePrefix);
            int tempMax = Math.max(groupingSize, getMinimumIntegerDigits())+1;
            int i;
            for (i = tempMax; i > 0; --i) {
                if (i == groupingSize)
                    result.append(localized ? symbols.getGroupingSeparator() :
                                  patternGroupingSeparator);
                if (i <= getMinimumIntegerDigits()) {
                    result.append(localized ? symbols.getZeroDigit() :
                                  patternZeroDigit);
                } else {
                    result.append(localized ? symbols.getDigit() :
                                  patternDigit);
                }
            }
            if (getMaximumFractionDigits() > 0)
                result.append(localized ? symbols.getDecimalSeparator() :
                              patternDecimalSeparator);
            for (i = 0; i < getMaximumFractionDigits(); ++i) {
                if (i < getMinimumFractionDigits()) {
                    result.append(localized ? symbols.getZeroDigit() :
                                  patternZeroDigit);
                } else {
                    result.append(localized ? symbols.getDigit() :
                                  patternDigit);
                }
            }
            if (j == 1) {
                result.append(positiveSuffix);
                if (negativeSuffix.equals(positiveSuffix)) {
                    if (negativePrefix.equals(symbols.getMinusSign() + positivePrefix))
                        break;
                }
                result.append(localized ? symbols.getPatternSeparator() :
                              patternSeparator);
            } else result.append(negativeSuffix);
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
        int start = 0;
        boolean inQuote = false;
        boolean gotNegative = false;
        for (int j = 1; j >= 0 && start < pattern.length(); --j) {
            StringBuffer prefix = new StringBuffer();
            StringBuffer suffix = new StringBuffer();
            byte maxIntegerCount = 0;
            byte minIntegerCount = 0;
            byte maxDecimalCount = 0;
            byte minDecimalCount = 0;
            int multiplier = 1;
            boolean useThousands = false;
            boolean useDecimalAlways = false;
            byte groupingSize = 0;
            char zeroDigit = patternZeroDigit;
            char groupingSeparator = patternGroupingSeparator;
            char decimalSeparator = patternDecimalSeparator;
            char percent = patternPercent;
            char perMill = patternPerMill;
            char digit = patternDigit;
            char separator = patternSeparator;
            if (localized) {
                zeroDigit = symbols.getZeroDigit();
                groupingSeparator = symbols.getGroupingSeparator();
                decimalSeparator = symbols.getDecimalSeparator();
                percent = symbols.getPercent();
                perMill = symbols.getPerMill();
                digit = symbols.getDigit();
                separator = symbols.getPatternSeparator();
            }
            // for now, minimal checking for syntax errors in pattern
            for (; start < pattern.length(); ++start) {
                char ch = pattern.charAt(start);
                if (inQuote) {
                    if (ch == '\'') {
                        inQuote = false;
                        continue;
                    }
                    if (maxIntegerCount <= 0)
                        prefix.append(ch);
                    else
                        suffix.append(ch);
                } else if (ch == '\'') {
                    inQuote = true;
                    continue;
                } else if (ch == separator) {
                    ++start;
                    break;
                } else if (!useDecimalAlways) { // in integer part
                    if (ch == digit) {
                        ++maxIntegerCount;
                        if (minIntegerCount > 0)
                            throw new IllegalArgumentException();
                        if (useThousands) ++groupingSize;
                    } else if (ch == zeroDigit) {
                        ++minIntegerCount;
                        ++maxIntegerCount;
                        if (useThousands) ++groupingSize;
                    } else if (ch == groupingSeparator) {
                        useThousands = true;
                        groupingSize = 0;  // reset, ignore all but last
                    } else if (ch == decimalSeparator) {
                        if (useDecimalAlways)
                            throw new IllegalArgumentException();
                        useDecimalAlways = true;
                    } else if (maxIntegerCount > 0) {
                        // LATER: clean up the code a bit
                        if (useDecimalAlways)
                            throw new IllegalArgumentException();

                        useDecimalAlways = true;
                        if (ch == percent) {
                            suffix.append(symbols.getPercent());
                            multiplier = 100;
                        } else if (ch == perMill) {
                            suffix.append(symbols.getPerMill());
                            multiplier = 1000;
                        } else {
                            suffix.append(ch);
                        }
                    } else {
                        prefix.append(ch);
                    }
                } else {        // in decimal part
                    if (ch == digit) {
                        ++maxDecimalCount;
                    } else if (ch == zeroDigit) {
                        ++minDecimalCount;
                        ++maxDecimalCount;
                    } else if (ch == percent) {
                        suffix.append(symbols.getPercent());
                        multiplier = 100;
                    } else if (ch == perMill) {
                        suffix.append(symbols.getPerMill());
                        multiplier = 1000;
                    } else {
                        suffix.append(ch);
                    }
                }
            }
            if (j == 1) {
                this.positivePrefix = prefix.toString();
                this.positiveSuffix = suffix.toString();
                this.negativePrefix = positivePrefix;   // assume these for now
                this.negativeSuffix = positiveSuffix;
                setMaximumIntegerDigits(127);
                setMinimumIntegerDigits(minIntegerCount);
                setMaximumFractionDigits(maxDecimalCount);
                setMinimumFractionDigits(minDecimalCount);
                setGroupingUsed(useThousands);
                useDecimalAlways = false; // ignore pattern
                setDecimalSeparatorAlwaysShown(useDecimalAlways);
                this.groupingSize = groupingSize;
                this.multiplier = multiplier;
            } else {
                // LATER; do consistency checks & throw exceptions
                this.negativePrefix = prefix.toString();
                this.negativeSuffix = suffix.toString();
                gotNegative = true;
            }
        }
        if (!gotNegative ||
            (negativePrefix.equals(positivePrefix)
             && negativeSuffix.equals(positiveSuffix))) {
            negativeSuffix = positiveSuffix;
            negativePrefix = symbols.getMinusSign() + negativePrefix;
        }
    }

    // ================privates===================


    /**
     * Utility routine to add current digits to a result. Used
     * in format.
     */
    private void appendNativeDigits(StringBuffer result,
                                    FieldPosition fieldPosition)
    {
        int endOffset = digitList.decimalAt;

        if (fieldPosition.field == NumberFormat.INTEGER_FIELD)
            fieldPosition.beginIndex = result.length();

        // do before the decimal point
        int startOffset = 0;
        int segmentCount = Math.max(getMinimumIntegerDigits(),
                                    Math.min(endOffset - startOffset,
                                             getMaximumIntegerDigits()));
        for (int i = segmentCount; i > 0; --i) {
            if (i > endOffset - startOffset || endOffset - i >= digitList.count) {
                result.append(symbols.getZeroDigit());
            } else {
                result.append((char)(digitList.digits[endOffset - i] - '0'
                    + symbols.getZeroDigit()));
            }
            if (isGroupingUsed() && (((i-1) % groupingSize) == 0)
                && i != 1)
                result.append(symbols.getGroupingSeparator());
        }


        if (fieldPosition.field == NumberFormat.INTEGER_FIELD)
            fieldPosition.endIndex = result.length();

        // do after the decimal point
        startOffset = endOffset;
        endOffset = digitList.count;
        segmentCount = Math.max(getMinimumFractionDigits(),
                                Math.min(endOffset - startOffset,
                                         getMaximumFractionDigits()));
        if (decimalSeparatorAlwaysShown || segmentCount > 0) {
            result.append(symbols.getDecimalSeparator());

            if (fieldPosition.field == NumberFormat.FRACTION_FIELD)
                fieldPosition.beginIndex = result.length();

            for (int i = 0; i < segmentCount; ++i) {
                if (i >= endOffset - startOffset) {
                    result.append(symbols.getZeroDigit());
                } else {
                    result.append((char)(digitList.digits[startOffset + i] - '0'
                        + symbols.getZeroDigit()));
                }
            }
            if (fieldPosition.field == NumberFormat.FRACTION_FIELD)
                fieldPosition.endIndex = result.length();
        }
    }

    private boolean isSpecialChar(char ch) {
        return ((ch == patternZeroDigit) ||
                (ch == patternGroupingSeparator) ||
                (ch == patternDecimalSeparator) ||
                (ch == patternPercent) ||
                (ch == patternPerMill) ||
                (ch == patternDigit) ||
                (ch == patternSeparator));
    }

    // Returns true if DigitList.digits is equal to Long.MIN_VALUE;
    // false, otherwise.
    //
    private boolean isLongMIN_VALUE(DigitList dl)
    {
        StringBuffer temp = new StringBuffer(dl.count+1);
        temp.append('-');
        for (int i = 0; i < dl.count; ++i)
            temp.append((char)(dl.digits[i]));
        if (temp.toString().regionMatches(false, 0,
            Long.toString(Long.MIN_VALUE), 0, dl.count+1))
            return true;
        return false;
    }

    private transient DigitList digitList = new DigitList();

    // these are often left as localized
    private String  positivePrefix = "";
    private String  positiveSuffix = "";
    private String  negativePrefix = "-";
    private String  negativeSuffix = "";
    private int     multiplier = 1;
    private byte    groupingSize = 3;  // invariant, > 0 if useThousands
    private boolean decimalSeparatorAlwaysShown = false;

    private DecimalFormatSymbols symbols = new DecimalFormatSymbols();

    // Constants for characters used in programmatic (unlocalized) patterns.
    // FIXME - These should be renamed to follow the convention for constants
    // (ie UPPER_CASE)
    private static final char       patternZeroDigit = '0';
    private static final char       patternGroupingSeparator = ',';
    private static final char       patternDecimalSeparator = '.';
    private static final char       patternPerMill = '\u2030';
    private static final char       patternPercent = '%';
    private static final char       patternDigit = '#';
    private static final char       patternSeparator = ';';
}

