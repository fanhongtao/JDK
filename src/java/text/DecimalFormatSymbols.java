/*
 * @(#)DecimalFormatSymbols.java	1.12 97/01/29
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
import java.io.Serializable;
import java.util.ResourceBundle;
import java.util.Locale;

/**
 * This class represents the set of symbols (such as the decimal separator,
 * the grouping separator, and so on) needed by <code>DecimalFormat</code>
 * to format numbers. <code>DecimalFormat</code> creates for itself an instance of
 * <code>DecimalFormatSymbols</code> from its locale data.  If you need to change any
 * of these symbols, you can get the <code>DecimalFormatSymbols</code> object from
 * your <code>DecimalFormat</code> and modify it.
 *
 * @see          java.util.Locale
 * @see          DecimalFormat
 * @version      1.12 29 Jan 1997
 * @author       Mark Davis
 */

final public class DecimalFormatSymbols implements Cloneable, Serializable {

    /**
     * Create a DecimalFormatSymbols object for the default locale.
     */
    public DecimalFormatSymbols() {
        initialize( Locale.getDefault() );
    }

    /**
     * Create a DecimalFormatSymbols object for the given locale.
     */
    public DecimalFormatSymbols( Locale locale ) {
        initialize( locale );
    }

    /**
     * character used for zero. Different for Arabic, etc.
     */
    public char getZeroDigit() {
        return zeroDigit;
    }

    public void setZeroDigit(char zeroDigit) {
        this.zeroDigit = zeroDigit;
    }

    /**
     * character used for thousands separator. Different for French, etc.
     */
    public char getGroupingSeparator() {
        return groupingSeparator;
    }

    public void setGroupingSeparator(char groupingSeparator) {
        this.groupingSeparator = groupingSeparator;
    }

    /**
     * character used for decimal sign. Different for French, etc.
     */
    public char getDecimalSeparator() {
        return decimalSeparator;
    }

    public void setDecimalSeparator(char decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
    }

    /**
     * character used for mille percent sign. Different for Arabic, etc.
     */
    public char getPerMill() {
        return perMill;
    }

    public void setPerMill(char perMill) {
        this.perMill = perMill;
    }

    /**
     * character used for percent sign. Different for Arabic, etc.
     */
    public char getPercent() {
        return percent;
    }

    public void setPercent(char percent) {
        this.percent = percent;
    }

    /**
     * character used for a digit in a pattern.
     */
    public char getDigit() {
        return digit;
    }

    public void setDigit(char digit) {
        this.digit = digit;
    }

    /**
     * character used to separate positive and negative subpatterns
     * in a pattern.
     */
    public char getPatternSeparator() {
        return patternSeparator;
    }

    public void setPatternSeparator(char patternSeparator) {
        this.patternSeparator = patternSeparator;
    }

    /**
     * character used to represent infinity. Almost always left
     * unchanged.
     */

    public String getInfinity() {
        return infinity;
    }

    public void setInfinity(String infinity) {
        this.infinity = infinity;
    }

    /**
     * character used to represent NaN. Almost always left
     * unchanged.
     */
    public String getNaN() {
        return NaN;
    }

    public void setNaN(String NaN) {
        this.NaN = NaN;
    }

    /**
     * character used to represent minus sign. If no explicit
     * negative format is specified, one is formed by prefixing
     * minusSign to the positive format.
     */
    public char getMinusSign() {
        return minusSign;
    }

    public void setMinusSign(char minusSign) {
        this.minusSign = minusSign;
    }


    /**
     * Standard override.
     */
    public Object clone() {
        try {
            return (DecimalFormatSymbols)super.clone();
            // other fields are bit-copied
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    /**
     * Override equals
     */
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (getClass() != obj.getClass()) return false;
        DecimalFormatSymbols other = (DecimalFormatSymbols) obj;
        return (zeroDigit == other.zeroDigit
        		&& groupingSeparator == other.groupingSeparator
        		&& decimalSeparator == other.decimalSeparator
        		&& percent == other.percent
        		&& perMill == other.perMill
        		&& digit == other.digit
        		&& minusSign == other.minusSign
        		&& patternSeparator == other.patternSeparator
        		&& infinity.equals(other.infinity)
        		&& NaN.equals(other.NaN)
        		&& currencySymbol.equals(other.currencySymbol)
        		&& intlCurrencySymbol.equals(other.intlCurrencySymbol));
    }

    /**
     * Override hashCode
     */
    public int hashCode() {
    		int result = zeroDigit;
    		result = result * 37 + groupingSeparator;
    		result = result * 37 + decimalSeparator;
    		return result;
    }

    /**
     * Initializes the symbols from the LocaleElements resource bundle.
     * Note: The organization of LocaleElements badly needs to be
     * cleaned up.
     */
    private void initialize( Locale locale ) {
        ResourceBundle rb = ResourceBundle.getBundle
                            ("java.text.resources.LocaleElements", locale);

        String[] numberElements = rb.getStringArray("NumberElements");

        this.decimalSeparator = numberElements[0].charAt(0);
        this.groupingSeparator = numberElements[1].charAt(0);
        // workaround bug - numberElements[2].charAt(
        this.patternSeparator = ';';
        this.percent = numberElements[3].charAt(0);
        this.zeroDigit = numberElements[4].charAt(0); //different for Arabic,etc.
        this.digit = numberElements[5].charAt(0);
        this.minusSign = numberElements[6].charAt(0);

        String[] currencyElements = rb.getStringArray("CurrencyElements");
        currencySymbol = currencyElements[0];
        intlCurrencySymbol = currencyElements[1];

        // Not yet available from resource bundle.
        this.perMill = '\u2030';
        this.infinity  = "\u221E";
        this.NaN = "\uFFFD";
    }

    private  char    zeroDigit;
    private  char    groupingSeparator;
    private  char    decimalSeparator;
    private  char    perMill;
    private  char    percent;
    private  char    digit;
    private  char    patternSeparator;
    private  String  infinity;
    private  String  NaN;
    private  char    minusSign;
    private  String  currencySymbol;
    private  String  intlCurrencySymbol;
}
