/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * (C) Copyright Taligent, Inc. 1996, 1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - 1998 - All Rights Reserved
 *
 *   The original version of this source code and documentation is copyrighted
 * and owned by Taligent, Inc., a wholly-owned subsidiary of IBM. These
 * materials are provided under terms of a License Agreement between Taligent
 * and Sun. This technology is protected by multiple US and International
 * patents. This notice and attribution to Taligent may not be removed.
 *   Taligent is a registered trademark of Taligent, Inc.
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
 * @version      1.31, 02/06/02
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
     * Gets the character used for zero. Different for Arabic, etc.
     */
    public char getZeroDigit() {
        return zeroDigit;
    }

    /**
     * Set the character used for zero. Different for Arabic, etc.
     */
    public void setZeroDigit(char zeroDigit) {
        this.zeroDigit = zeroDigit;
    }

    /**
     * Gets the character used for thousands separator. Different for French, etc.
     */
    public char getGroupingSeparator() {
        return groupingSeparator;
    }

    /**
     * Set the character used for thousands separator. Different for French, etc.
     */
    public void setGroupingSeparator(char groupingSeparator) {
        this.groupingSeparator = groupingSeparator;
    }

    /**
     * Gets the character used for decimal sign. Different for French, etc.
     */
    public char getDecimalSeparator() {
        return decimalSeparator;
    }

    /**
     * Set the character used for decimal sign. Different for French, etc.
     */
    public void setDecimalSeparator(char decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
    }

    /**
     * Gets the character used for mille percent sign. Different for Arabic, etc.
     */
    public char getPerMill() {
        return perMill;
    }

    /**
     * Set the character used for mille percent sign. Different for Arabic, etc.
     */
    public void setPerMill(char perMill) {
        this.perMill = perMill;
    }

    /**
     * Gets the character used for percent sign. Different for Arabic, etc.
     */
    public char getPercent() {
        return percent;
    }

    /**
     * Set the character used for percent sign. Different for Arabic, etc.
     */
    public void setPercent(char percent) {
        this.percent = percent;
    }

    /**
     * Gets the character used for a digit in a pattern.
     */
    public char getDigit() {
        return digit;
    }

    /**
     * Set the character used for a digit in a pattern.
     */
    public void setDigit(char digit) {
        this.digit = digit;
    }

    /**
     * Gets the character used to separate positive and negative subpatterns
     * in a pattern.
     */
    public char getPatternSeparator() {
        return patternSeparator;
    }

    /**
     * Set the character used to separate positive and negative subpatterns
     * in a pattern.
     */
    public void setPatternSeparator(char patternSeparator) {
        this.patternSeparator = patternSeparator;
    }

    /**
     * Gets the character used to represent infinity. Almost always left
     * unchanged.
     */
    public String getInfinity() {
        return infinity;
    }

    /**
     * Set the character used to represent infinity. Almost always left
     * unchanged.
     */
    public void setInfinity(String infinity) {
        this.infinity = infinity;
    }

    /**
     * Gets the character used to represent NaN. Almost always left
     * unchanged.
     */
    public String getNaN() {
        return NaN;
    }

    /**
     * Set the character used to represent NaN. Almost always left
     * unchanged.
     */
    public void setNaN(String NaN) {
        this.NaN = NaN;
    }

    /**
     * Gets the character used to represent minus sign. If no explicit
     * negative format is specified, one is formed by prefixing
     * minusSign to the positive format.
     */
    public char getMinusSign() {
        return minusSign;
    }

    /**
     * Set the character used to represent minus sign. If no explicit
     * negative format is specified, one is formed by prefixing
     * minusSign to the positive format.
     */
    public void setMinusSign(char minusSign) {
        this.minusSign = minusSign;
    }

    /**
     * Return the string denoting the local currency.
     */
    public String getCurrencySymbol()
    {
        return currencySymbol;
    }

    /**
     * Set the string denoting the local currency.
     */
    public void setCurrencySymbol(String currency)
    {
        currencySymbol = currency;
    }

    /**
     * Return the international string denoting the local currency.
     */
    public String getInternationalCurrencySymbol()
    {
        return intlCurrencySymbol;
    }

    /**
     * Set the international string denoting the local currency.
     */
    public void setInternationalCurrencySymbol(String currency)
    {
        intlCurrencySymbol = currency;
    }

    /**
     * Return the monetary decimal separator.
     */
    public char getMonetaryDecimalSeparator()
    {
        return monetarySeparator;
    }

    /**
     * Set the monetary decimal separator.
     */
    public void setMonetaryDecimalSeparator(char sep)
    {
        monetarySeparator = sep;
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
     * Override equals.
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
     * Override hashCode.
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

        // if the resource data specified the empty string as the monetary decimal
        // separator, that means we should just use the regular separator as the
        // monetary separator
        if (currencyElements[2].length() == 0)
            monetarySeparator = decimalSeparator;
        else
            monetarySeparator = currencyElements[2].charAt(0);
    }

    /**
     * Read the default serializable fields, then if <code>serialVersionOnStream</code>
     * is less than 1, initialize <code>monetarySeparator</code> to be
     * the same as <code>decimalSeparator</code> and <code>exponential</code>
     * to be 'E'.
     * Finally, set serialVersionOnStream back to the maximum allowed value so that
     * default serialization will work properly if this object is streamed out again.
     *
     * @since JDK 1.1.6
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

    /**
     * Character used for zero.
     *
     * @serial
     * @see #getZeroDigit
     */
    private  char    zeroDigit;

    /**
     * Character used for thousands separator.
     *
     * @serial
     * @see #getGroupingSeparator
     */
    private  char    groupingSeparator;

    /**
     * Character used for decimal sign.
     *
     * @serial
     * @see #getDecimalSeparator
     */
    private  char    decimalSeparator;

    /**
     * Character used for mille percent sign.
     *
     * @serial
     * @see #getPerMill
     */
    private  char    perMill;

    /**
     * Character used for percent sign.
     * @serial
     * @see #getPercent
     */
    private  char    percent;

    /**
     * Character used for a digit in a pattern.
     *
     * @serial
     * @see #getDigit
     */
    private  char    digit;

    /**
     * Character used to separate positive and negative subpatterns
     * in a pattern.
     *
     * @serial
     * @see #getPatternSeparator
     */
    private  char    patternSeparator;

    /**
     * Character used to represent infinity.
     * @serial
     * @see #getInfinity
     */
    private  String  infinity;

    /**
     * Character used to represent NaN.
     * @serial
     * @see #getNaN
     */
    private  String  NaN;

    /**
     * Character used to represent minus sign.
     * @serial
     * @see #getMinusSign
     */
    private  char    minusSign;

    /**
     * String denoting the local currency, e.g. "$".
     * @serial
     * @see #getCurrencySymbol
     */
    private  String  currencySymbol;

    /**
     * International string denoting the local currency, e.g. "USD".
     * @serial
     * @see #getInternationalCurrencySymbol
     */
    private  String  intlCurrencySymbol;

    /**
     * The decimal separator used when formatting currency values.
     * @serial
     * @since JDK 1.1.6
     * @see #getMonetaryDecimalSeparator
     */
    private  char    monetarySeparator; // Field new in JDK 1.1.6

    /**
     * The character used to distinguish the exponent in a number formatted
     * in exponential notation, e.g. 'E' for a number such as "1.23E45".
     * <p>
     * Note that the public API provides no way to set this field,
     * even though it is supported by the implementation and the stream format.
     * The intent is that this will be added to the API in the future.
     *
     * @serial
     * @since JDK 1.1.6
     */
    private  char    exponential;       // Field new in JDK 1.1.6

    // Proclaim JDK 1.1 FCS compatibility
    static final long serialVersionUID = 5772796243397350300L;

    // The internal serial version which says which version was written
    // - 0 (default) for version up to JDK 1.1.5
    // - 1 for version from JDK 1.1.6, which includes two new fields:
    //     monetarySeparator and exponential.
    private static final int currentSerialVersion = 1;
    
    /**
     * Describes the version of <code>DecimalFormatSymbols</code> present on the stream.
     * Possible values are:
     * <ul>
     * <li><b>0</b> (or uninitialized): versions prior to JDK 1.1.6.
     *
     * <li><b>1</b>: Versions written by JDK 1.1.6 or later, which includes
     *      two new fields: <code>monetarySeparator</code> and <code>exponential</code>.
     * </ul>
     * When streaming out a <code>DecimalFormatSymbols</code>, the most recent format
     * (corresponding to the highest allowable <code>serialVersionOnStream</code>)
     * is always written.
     *
     * @serial
     * @since JDK 1.1.6
     */
    private int serialVersionOnStream = currentSerialVersion;

    /**
     * cache to hold the NumberElements and the CurrencyElements
     * of a Locale.
     */
    private static final Hashtable cachedLocaleData = new Hashtable(3);
}
