/*
 * @(#)NumberFormat.java	1.36 98/10/05
 *
 * (C) Copyright Taligent, Inc. 1996 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - All Rights Reserved
 *
 * Portions copyright (c) 1996-1997 Sun Microsystems, Inc. All Rights Reserved.
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
import java.util.Locale;
import java.util.ResourceBundle;
import java.text.resources.*;
import java.util.Hashtable;
import java.math.BigInteger;

/**
 * <code>NumberFormat</code> is the abstract base class for all number
 * formats. This class provides the interface for formatting and parsing
 * numbers. <code>NumberFormat</code> also provides methods for determining
 * which locales have number formats, and what their names are.
 *
 * <p>
 * <code>NumberFormat</code> helps you to format and parse numbers for any locale.
 * Your code can be completely independent of the locale conventions for
 * decimal points, thousands-separators, or even the particular decimal
 * digits used, or whether the number format is even decimal.
 *
 * <p>
 * To format a number for the current Locale, use one of the factory
 * class methods:
 * <blockquote>
 * <pre>
 *  myString = NumberFormat.getInstance().format(myNumber);
 * </pre>
 * </blockquote>
 * If you are formatting multiple numbers, it is
 * more efficient to get the format and use it multiple times so that
 * the system doesn't have to fetch the information about the local
 * language and country conventions multiple times.
 * <blockquote>
 * <pre>
 * NumberFormat nf = NumberFormat.getInstance();
 * for (int i = 0; i < a.length; ++i) {
 *     output.println(nf.format(myNumber[i]) + "; ");
 * }
 * </pre>
 * </blockquote>
 * To format a number for a different Locale, specify it in the
 * call to <code>getInstance</code>.
 * <blockquote>
 * <pre>
 * NumberFormat nf = NumberFormat.getInstance(Locale.FRENCH);
 * </pre>
 * </blockquote>
 * You can also use a <code>NumberFormat</code> to parse numbers:
 * <blockquote>
 * <pre>
 * myNumber = nf.parse(myString);
 * </pre>
 * </blockquote>
 * Use <code>getInstance</code> or <code>getNumberInstance</code> to get the
 * normal number format. Use <code>getCurrencyInstance</code> to get the
 * currency number format. And use <code>getPercentInstance</code> to get a
 * format for displaying percentages. With this format, a fraction like
 * 0.53 is displayed as 53%.
 *
 * <p>
 * You can also control the display of numbers with such methods as
 * <code>setMinimumFractionDigits</code>.
 * If you want even more control over the format or parsing,
 * or want to give your users more control,
 * you can try casting the <code>NumberFormat</code> you get from the factory methods
 * to a <code>DecimalNumberFormat</code>. This will work for the vast majority
 * of locales; just remember to put it in a <code>try</code> block in case you
 * encounter an unusual one.
 *
 * <p>
 * NumberFormat and DecimalFormat are designed such that some controls
 * work for formatting and others work for parsing.  The following is
 * the detailed description for each these control methods,
 * <p>
 * setParseIntegerOnly : only affects parsing, e.g.
 * if true,  "3456.78" -> 3456 (and leaves the parse position just after index 6)
 * if false, "3456.78" -> 3456.78 (and leaves the parse position just after index 8)
 * This is independent of formatting.  If you want to not show a decimal point
 * where there might be no digits after the decimal point, use
 * setDecimalSeparatorAlwaysShown.
 * <p>
 * setDecimalSeparatorAlwaysShown : only affects formatting, and only where
 * there might be no digits after the decimal point, such as with a pattern
 * like "#,##0.##", e.g.,
 * if true,  3456.00 -> "3,456."
 * if false, 3456.00 -> "3456"
 * This is independent of parsing.  If you want parsing to stop at the decimal
 * point, use setParseIntegerOnly.
 *
 * <p>
 * You can also use forms of the <code>parse</code> and <code>format</code>
 * methods with <code>ParsePosition</code> and <code>FieldPosition</code> to
 * allow you to:
 * <ul>
 * <li> progressively parse through pieces of a string
 * <li> align the decimal point and other areas
 * </ul>
 * For example, you can align numbers in two ways:
 * <ol>
 * <li> If you are using a monospaced font with spacing for alignment,
 *      you can pass the <code>FieldPosition</code> in your format call, with
 *      <code>field</code> = <code>INTEGER_FIELD</code>. On output,
 *      <code>getEndIndex</code> will be set to the offset between the
 *      last character of the integer and the decimal. Add
 *      (desiredSpaceCount - getEndIndex) spaces at the front of the string.
 *
 * <li> If you are using proportional fonts,
 *      instead of padding with spaces, measure the width
 *      of the string in pixels from the start to <code>getEndIndex</code>.
 *      Then move the pen by
 *      (desiredPixelWidth - widthToAlignmentPoint) before drawing the text.
 *      It also works where there is no decimal, but possibly additional
 *      characters at the end, e.g. with parentheses in negative
 *      numbers: "(12)" for -12.
 * </ol>
 *
 * @see          DecimalFormat
 * @see          ChoiceFormat
 * @version      1.22 29 Jan 1997
 * @author       Mark Davis
 * @author       Helena Shih
 */
public abstract class NumberFormat extends Format implements java.lang.Cloneable {

    /**
     * Field constant used to construct a FieldPosition object. Signifies that
     * the position of the integer part of a formatted number should be returned.
     * @see java.text.FieldPosition
     */
    public static final int INTEGER_FIELD = 0;

    /**
     * Field constant used to construct a FieldPosition object. Signifies that
     * the position of the fraction part of a formatted number should be returned.
     * @see java.text.FieldPosition
     */
    public static final int FRACTION_FIELD = 1;

    public final StringBuffer format(Object number,
                                     StringBuffer toAppendTo,
                                     FieldPosition pos)
    {
        if (number instanceof Long ||
            (number instanceof BigInteger && ((BigInteger)number).bitLength() < 64)) {
            return format(((Number)number).longValue(), toAppendTo, pos);
        }
        /* Here is the code that's required to get all the bits we can out of
         * BigDecimal into a long or double.  In the interests of simplicity, we
         * don't use this code; we just convert BigDecimal values into doubles.
         * (Actually, to really do things right, you'd compare against both
         * Long.MIN_VALUE and Long.MAX_VALUE, since they differ in magnitude.)
         * Liu 6/98
         */
        //  else if (number instanceof BigDecimal) {
        //      BigDecimal bd = (BigDecimal)number;
        //      try {
        //          if (bd.setScale(0, BigDecimal.ROUND_UNNECESSARY).
        //              abs().compareTo(new BigDecimal("9223372036854775807")) <= 0) {
        //              return format(((Number)number).longValue(), toAppendTo, pos);
        //          }
        //      }
        //      catch (ArithmeticException e) {}
        //      return format(((Number)number).doubleValue(), toAppendTo, pos);        
        //  }
        else if (number instanceof Number) {
            return format(((Number)number).doubleValue(), toAppendTo, pos);
        }
        else {
            throw new IllegalArgumentException("Cannot format given Object as a Number");
        }
    }

    public final Object parseObject(String source,
                                    ParsePosition parsePosition)
    {
        return parse(source, parsePosition);
    }

   /**
     * Specialization of format.
     * @see java.text.Format#format
     */
    public final String format (double number) {
        return format(number,new StringBuffer(),
                      new FieldPosition(0)).toString();
    }

   /**
     * Specialization of format.
     * @see java.text.Format#format
     */
    public final String format (long number) {
        return format(number,new StringBuffer(),
                      new FieldPosition(0)).toString();
    }

   /**
     * Specialization of format.
     * @see java.text.Format#format
     */
    public abstract StringBuffer format(double number,
                                        StringBuffer toAppendTo,
                                        FieldPosition pos);

   /**
     * Specialization of format.
     * @see java.text.Format#format
     */
    public abstract StringBuffer format(long number,
                                        StringBuffer toAppendTo,
                                        FieldPosition pos);

   /**
     * Returns a Long if possible (e.g. within range [Long.MIN_VALUE,
     * Long.MAX_VALUE], and with no decimals), otherwise a Double.
     * If IntegerOnly is set, will stop at a decimal
     * point (or equivalent; e.g. for rational numbers "1 2/3", will stop
     * after the 1).
     * Does not throw an exception; if no object can be parsed, index is
     * unchanged!
     * @see java.text.NumberFormat#isParseIntegerOnly
     * @see java.text.Format#parseObject
     */
    public abstract Number parse(String text, ParsePosition parsePosition);

    /**
     * Convenience method.
     *
     * @exception ParseException if the specified string is invalid.
     * @see #format
     */
    public Number parse(String text) throws ParseException {
        ParsePosition parsePosition = new ParsePosition(0);
        Number result = parse(text, parsePosition);
        if (parsePosition.index == 0) {
            throw new ParseException("Unparseable number: \"" + text + "\"", 0);
        }
        return result;
    }

    /**
     * Returns true if this format will parse numbers as integers only.
     * For example in the English locale, with ParseIntegerOnly true, the
     * string "1234." would be parsed as the integer value 1234 and parsing
     * would stop at the "." character.  Of course, the exact format accepted
     * by the parse operation is locale dependant and determined by sub-classes
     * of NumberFormat.
     */
    public boolean isParseIntegerOnly() {
        return parseIntegerOnly;
    }

    /**
     * Sets whether or not numbers should be parsed as integers only.
     * @see #isParseIntegerOnly
     */
    public void setParseIntegerOnly(boolean value) {
        parseIntegerOnly = value;
    }

    //============== Locale Stuff =====================

    /**
     * Returns the default number format for the current default locale.
     * The default format is one of the styles provided by the other
     * factory methods: getNumberInstance, getCurrencyInstance or getPercentInstance.
     * Exactly which one is locale dependant.
     */
    public final static NumberFormat getInstance() {
        return getInstance(Locale.getDefault(), NUMBERSTYLE);
    }

    /**
     * Returns the default number format for the specified locale.
     * The default format is one of the styles provided by the other
     * factory methods: getNumberInstance, getCurrencyInstance or getPercentInstance.
     * Exactly which one is locale dependant.
     */
    public static NumberFormat getInstance(Locale inLocale) {
        return getInstance(inLocale, NUMBERSTYLE);
    }

    /**
     * Returns a general-purpose number format for the current default locale.
     */
    public final static NumberFormat getNumberInstance() {
        return getInstance(Locale.getDefault(), NUMBERSTYLE);
    }

    /**
     * Returns a general-purpose number format for the specified locale.
     */
    public static NumberFormat getNumberInstance(Locale inLocale) {
        return getInstance(inLocale, NUMBERSTYLE);
    }

    /**
     * Returns a currency format for the current default locale.
     */
    public final static NumberFormat getCurrencyInstance() {
        return getInstance(Locale.getDefault(), CURRENCYSTYLE);
    }

    /**
     * Returns a currency format for the specified locale.
     */
    public static NumberFormat getCurrencyInstance(Locale inLocale) {
        return getInstance(inLocale, CURRENCYSTYLE);
    }

    /**
     * Returns a percentage format for the current default locale.
     */
    public final static NumberFormat getPercentInstance() {
        return getInstance(Locale.getDefault(), PERCENTSTYLE);
    }

    /**
     * Returns a percentage format for the specified locale.
     */
    public static NumberFormat getPercentInstance(Locale inLocale) {
        return getInstance(inLocale, PERCENTSTYLE);
    }

    /**
     * Returns a scientific format for the current default locale.
     */
    /*public*/ final static NumberFormat getScientificInstance() {
        return getInstance(Locale.getDefault(), SCIENTIFICSTYLE);
    }

    /**
     * Returns a scientific format for the specified locale.
     */
    /*public*/ static NumberFormat getScientificInstance(Locale inLocale) {
        return getInstance(inLocale, SCIENTIFICSTYLE);
    }


    /**
     * Get the set of Locales for which NumberFormats are installed
     * @return available locales
     */
    public static Locale[] getAvailableLocales() {
        return LocaleData.getAvailableLocales("NumberPatterns");
    }

    /**
     * Overrides hashCode
     */
    public int hashCode() {
        return maxIntegerDigits * 37 + maxFractionDigits;
        // just enough fields for a reasonable distribution
    }

    /**
     * Overrides equals
     */
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        NumberFormat other = (NumberFormat) obj;
        return (maxIntegerDigits == other.maxIntegerDigits
            && minIntegerDigits == other.minIntegerDigits
            && maxFractionDigits == other.maxFractionDigits
            && minFractionDigits == other.minFractionDigits
            && groupingUsed == other.groupingUsed
            && parseIntegerOnly == other.parseIntegerOnly);
    }

    /**
     * Overrides Cloneable
     */
    public Object clone()
    {
        NumberFormat other = (NumberFormat) super.clone();
        return other;
    }

    /**
     * Returns true if grouping is used in this format. For example, in the
     * English locale, with grouping on, the number 1234567 might be formatted
     * as "1,234,567". The grouping separator as well as the size of each group
     * is locale dependant and is determined by sub-classes of NumberFormat.
     * @see #setGroupingUsed
     */
    public boolean isGroupingUsed() {
        return groupingUsed;
    }

    /**
     * Set whether or not grouping will be used in this format.
     * @see #isGroupingUsed
     */
    public void setGroupingUsed(boolean newValue) {
        groupingUsed = newValue;
    }

    /**
     * Returns the maximum number of digits allowed in the integer portion of a
     * number.
     * @see #setMaximumIntegerDigits
     */
    public int getMaximumIntegerDigits() {
        return maxIntegerDigits;
    }

    /**
     * Sets the maximum number of digits allowed in the integer portion of a
     * number. maximumIntegerDigits must be >= minimumIntegerDigits.  If the
     * new value for maximumIntegerDigits is less than the current value
     * of minimumIntegerDigits, then minimumIntegerDigits will also be set to
     * the new value.

     * @see #getMaximumIntegerDigits
     */
    public void setMaximumIntegerDigits(int newValue) {
        maxIntegerDigits = (byte) Math.max(0,Math.min(newValue,308));
        if (minIntegerDigits > maxIntegerDigits)
            minIntegerDigits = maxIntegerDigits;
    }

    /**
     * Returns the minimum number of digits allowed in the integer portion of a
     * number.
     * @see #setMinimumIntegerDigits
     */
    public int getMinimumIntegerDigits() {
        return minIntegerDigits;
    }

    /**
     * Sets the minimum number of digits allowed in the integer portion of a
     * number. minimumIntegerDigits must be <= maximumIntegerDigits.  If the
     * new value for minimumIntegerDigits exceeds the current value
     * of maximumIntegerDigits, then maximumIntegerDigits will also be set to
     * the new value
     * @see #getMinimumIntegerDigits
     */
    public void setMinimumIntegerDigits(int newValue) {
        minIntegerDigits = (byte) Math.max(0,Math.min(newValue,127));
        if (minIntegerDigits > maxIntegerDigits)
            maxIntegerDigits = minIntegerDigits;
    }

    /**
     * Returns the maximum number of digits allowed in the fraction portion of a
     * number.
     * @see #setMaximumFractionDigits
     */
    public int getMaximumFractionDigits() {
        return maxFractionDigits;
    }

    /**
     * Sets the maximum number of digits allowed in the fraction portion of a
     * number. maximumFractionDigits must be >= minimumFractionDigits.  If the
     * new value for maximumFractionDigits is less than the current value
     * of minimumFractionDigits, then minimumFractionDigits will also be set to
     * the new value.
     * @see #getMaximumFractionDigits
     */
    public void setMaximumFractionDigits(int newValue) {
        maxFractionDigits = (byte) Math.max(0,Math.min(newValue,340));
        if (maxFractionDigits < minFractionDigits)
            minFractionDigits = maxFractionDigits;
    }

    /**
     * Returns the minimum number of digits allowed in the fraction portion of a
     * number.
     * @see #setMinimumFractionDigits
     */
    public int getMinimumFractionDigits() {
        return minFractionDigits;
    }

    /**
     * Sets the minimum number of digits allowed in the fraction portion of a
     * number. minimumFractionDigits must be <= maximumFractionDigits.  If the
     * new value for minimumFractionDigits exceeds the current value
     * of maximumFractionDigits, then maximumIntegerDigits will also be set to
     * the new value
     * @see #getMinimumFractionDigits
     */
    public void setMinimumFractionDigits(int newValue) {
        minFractionDigits = (byte) Math.max(0,Math.min(newValue,127));
        if (maxFractionDigits < minFractionDigits)
            maxFractionDigits = minFractionDigits;
    }

    // =======================privates===============================

    private static NumberFormat getInstance(Locale desiredLocale,
                                           int choice)
    {
	/* try the cache first */
	String[] numberPatterns = (String[])cachedLocaleData.get(desiredLocale);
	if (numberPatterns == null) { /* cache miss */
	    ResourceBundle resource = ResourceBundle.getBundle
		("java.text.resources.LocaleElements", desiredLocale);
	    numberPatterns = resource.getStringArray("NumberPatterns");
	    /* update cache */
	    cachedLocaleData.put(desiredLocale, numberPatterns);
	}

        return new DecimalFormat(numberPatterns[choice],
                                 new DecimalFormatSymbols(desiredLocale));
    }

    /**
     * Cache to hold the NumberPatterns of a Locale.
     */
    private static final Hashtable cachedLocaleData = new Hashtable(3);

    // Constants used by factory methods to specify a style of format.
    private static final int NUMBERSTYLE = 0;
    private static final int CURRENCYSTYLE = 1;
    private static final int PERCENTSTYLE = 2;
    private static final int SCIENTIFICSTYLE = 3;

    private boolean groupingUsed = true;
    private byte    maxIntegerDigits = 40;
    private byte    minIntegerDigits = 1;
    private byte    maxFractionDigits = 3;    // invariant, >= minFractionDigits
    private byte    minFractionDigits = 0;
    private boolean parseIntegerOnly = false;
    // Removed "implements Cloneable" clause.  Needs to update serialization
    // ID for backward compatibility.
    static final long serialVersionUID = -2308460125733713944L;
}
