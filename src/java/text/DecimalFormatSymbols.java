/*
 * @(#)DecimalFormatSymbols.java	1.17 98/02/19
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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.Hashtable;

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
 * @author       Alan Liu
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

    //------------------------------------------------------------
    // BEGIN   Package Private methods ... to be made public later
    //------------------------------------------------------------

    /**
     * Return the character used to separate the mantissa from the exponent.
     */
    char getExponentialSymbol()
    {
	return exponential;
    }

    /**
     * Set the character used to separate the mantissa from the exponent.
     */
    void setExponentialSymbol(char exp)
    {
	exponential = exp;
    }

    /**
     * Return the string denoting the local currency.
     */
    String getCurrencySymbol()
    {
	return currencySymbol;
    }

    /**
     * Set the string denoting the local currency.
     */
    void setCurrencySymbol(String currency)
    {
	currencySymbol = currency;
    }

    /**
     * Return the international string denoting the local currency.
     */
    String getInternationalCurrencySymbol()
    {
	return intlCurrencySymbol;
    }

    /**
     * Set the international string denoting the local currency.
     */
    void setInternationalCurrencySymbol(String currency)
    {
	intlCurrencySymbol = currency;
    }

    /**
     * Return the monetary decimal separator.
     */
    char getMonetaryDecimalSeparator()
    {
	return monetarySeparator;
    }

    /**
     * Set the monetary decimal separator.
     */
    void setMonetaryDecimalSeparator(char sep)
    {
	monetarySeparator = sep;
    }

    //------------------------------------------------------------
    // END     Package Private methods ... to be made public later
    //------------------------------------------------------------

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
        if (obj == null) return false;
        if (this == obj) return true;
        if (getClass() != obj.getClass()) return false;
        DecimalFormatSymbols other = (DecimalFormatSymbols) obj;
        return (zeroDigit == other.zeroDigit &&
		groupingSeparator == other.groupingSeparator &&
		decimalSeparator == other.decimalSeparator &&
		percent == other.percent &&
		perMill == other.perMill &&
		digit == other.digit &&
		minusSign == other.minusSign &&
		patternSeparator == other.patternSeparator &&
		infinity.equals(other.infinity) &&
		NaN.equals(other.NaN) &&
		currencySymbol.equals(other.currencySymbol) &&
		intlCurrencySymbol.equals(other.intlCurrencySymbol) &&
		monetarySeparator == other.monetarySeparator);
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
	/* try the cache first */
	String[][] data = (String[][]) cachedLocaleData.get(locale);
	String[] numberElements;
	String[] currencyElements;
	if (data == null) {  /* cache miss */
	    data = new String[2][];
	    ResourceBundle rb = ResourceBundle.getBundle
		("java.text.resources.LocaleElements", locale);
	    data[0] = rb.getStringArray("NumberElements");
	    data[1] = rb.getStringArray("CurrencyElements");
	    /* update cache */
	    cachedLocaleData.put(locale, data);
	}
	numberElements = data[0];
	currencyElements = data[1];
	
        decimalSeparator = numberElements[0].charAt(0);
        groupingSeparator = numberElements[1].charAt(0);
        patternSeparator = numberElements[2].charAt(0);
        percent = numberElements[3].charAt(0);
        zeroDigit = numberElements[4].charAt(0); //different for Arabic,etc.
        digit = numberElements[5].charAt(0);
        minusSign = numberElements[6].charAt(0);
	exponential = numberElements[7].charAt(0);
        perMill = numberElements[8].charAt(0);
        infinity  = numberElements[9];
        NaN = numberElements[10];

	currencySymbol = currencyElements[0];
        intlCurrencySymbol = currencyElements[1];
	monetarySeparator = currencyElements[2].charAt(0);
    }

    /**
     * Override readObject.
     */
    private void readObject(ObjectInputStream stream)
	 throws IOException, ClassNotFoundException
    {
	stream.defaultReadObject();
	if (serialVersionOnStream < 1)
	{
	    // Didn't have monetarySeparator or exponential field;
	    // use defaults.
	    monetarySeparator = decimalSeparator;
	    exponential       = 'E';
	}
	serialVersionOnStream = currentSerialVersion;
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
    private  char    monetarySeparator; // Field new in JDK 1.1.6
    private  char    exponential;       // Field new in JDK 1.1.6

    // Proclaim JDK 1.1 FCS compatibility
    static final long serialVersionUID = 5772796243397350300L;

    // The internal serial version which says which version was written
    // - 0 (default) for version up to JDK 1.1.5
    // - 1 for version from JDK 1.1.6, which includes two new fields:
    //     monetarySeparator and exponential.
    private static final int currentSerialVersion = 1;
    private int serialVersionOnStream = currentSerialVersion;

    /**
     * cache to hold the NumberElements and the CurrencyElements
     * of a Locale.
     */
    private static final Hashtable cachedLocaleData = new Hashtable(3);
}
