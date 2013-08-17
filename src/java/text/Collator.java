/*
 * @(#)Collator.java	1.14 98/08/19
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
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.text.resources.*;
import java.util.Hashtable;


/**
 * The <code>Collator</code> class performs locale-sensitive
 * <code>String</code> comparison. You use this class to build
 * searching and sorting routines for natural language text.
 *
 * <p>
 * <code>Collator</code> is an abstract base class. Subclasses
 * implement specific collation strategies. One subclass,
 * <code>RuleBasedCollator</code>, is currently provided with
 * the JDK and is applicable to a wide set of languages. Other
 * subclasses may be created to handle more specialized needs.
 *
 * <p>
 * Like other locale-sensitive classes, you can use the static
 * factory method, <code>getInstance</code>, to obtain the appropriate
 * <code>Collator</code> object for a given locale. You will only need
 * to look at the subclasses of <code>Collator</code> if you need
 * to understand the details of a particular collation strategy or
 * if you need to modify that strategy.
 *
 * <p>
 * The following example shows how to compare two strings using
 * the <code>Collator</code> for the default locale.
 * <blockquote>
 * <pre>
 * // Compare two strings in the default locale
 * Collator myCollator = Collator.getInstance();
 * if( myCollator.compare("abc", "ABC") < 0 )
 *     System.out.println("abc is less than ABC");
 * else
 *     System.out.println("abc is greater than or equal to ABC");
 * </pre>
 * </blockquote>
 *
 * <p>
 * You can set a <code>Collator</code>'s <em>strength</em> property
 * to determine the level of difference considered significant in
 * comparisons. Four strengths are provided: <code>PRIMARY</code>,
 * <code>SECONDARY</code>, <code>TERTIARY</code>, and <code>IDENTICAL</code>.
 * The exact assignment of strengths to language features is
 * locale dependant.  For example, in Czech, "e" and "f" are considered
 * primary differences, while "e" and "\u00EA" are secondary differences,
 * "e" and "E" are tertiary differences and "e" and "e" are identical.
 * The following shows how both case and accents could be ignored for
 * US English.
 * <blockquote>
 * <pre>
 * //Get the Collator for US English and set its strength to PRIMARY
 * Collator usCollator = Collator.getInstance(Locale.US);
 * usCollator.setStrength(Collator.PRIMARY);
 * if( usCollator.compare("abc", "ABC") == 0 ) {
 *     System.out.println("Strings are equivalent");
 * }
 * </pre>
 * </blockquote>
 * <p>
 * For comparing <code>String</code>s exactly once, the <code>compare</code>
 * method provides the best performance. When sorting a list of
 * <code>String</code>s however, it is generally necessary to compare each
 * <code>String</code> multiple times. In this case, <code>CollationKey</code>s
 * provide better performance. The <code>CollationKey</code> class converts
 * a <code>String</code> to a series of bits that can be compared bitwise
 * against other <code>CollationKey</code>s. A <code>CollationKey</code> is
 * created by a <code>Collator</code> object for a given <code>String</code>.
 * <br>
 * <strong>Note:</strong> <code>CollationKey</code>s from different
 * <code>Collator</code>s can not be compared. See the class description
 * for <a href="java.text.CollationKey.html"><code>CollationKey</code></a>
 * for an example using <code>CollationKey</code>s.
 *
 * @see         RuleBasedCollator
 * @see         CollationKey
 * @see         CollationElementIterator
 * @see         Locale
 * @version     1.14 08/19/98
 * @author      Helena Shih
 */

public abstract class Collator implements Cloneable, Serializable {
    /**
     * Collator strength value.  When set, only PRIMARY differences are
     * considered significant during comparison. The assignment of strengths
     * to language features is locale dependant. A common example is for
     * different base letters ("a" vs "b") to be considered a PRIMARY difference.
     * @see java.text.Collator#setStrength
     * @see java.text.Collator#getStrength
     */
    public final static int PRIMARY = 0;
    /**
     * Collator strength value.  When set, only SECONDARY and above differences are
     * considered significant during comparison. The assignment of strengths
     * to language features is locale dependant. A common example is for
     * different accented forms of the same base letter ("a" vs "\u00E4") to be
     * considered a SECONDARY difference.
     * @see java.text.Collator#setStrength
     * @see java.text.Collator#getStrength
     */
    public final static int SECONDARY = 1;
    /**
     * Collator strength value.  When set, only TERTIARY and above differences are
     * considered significant during comparison. The assignment of strengths
     * to language features is locale dependant. A common example is for
     * case differences ("a" vs "A") to be considered a TERTIARY difference.
     * @see java.text.Collator#setStrength
     * @see java.text.Collator#getStrength
     */
    public final static int TERTIARY = 2;

    /**
     * Collator strength value.  When set, all differences are
     * considered significant during comparison. The assignment of strengths
     * to language features is locale dependant. A common example is for control
     * characters ("&#092;u0001" vs "&#092;u0002") to be considered equal at the
     * PRIMARY, SECONDARY, and TERTIARY levels but different at the IDENTICAL
     * level.  Additionally, differences between pre-composed accents such as
     * "&#092;u00C0" (A-grave) and combining accents such as "A&#092;u0300"
     * (A, combining-grave) will be considered significant at the tertiary
     * level if decomposition is set to NO_DECOMPOSITION.
     */
    public final static int IDENTICAL = 3;

    /**
     * Decomposition mode value. With NO_DECOMPOSITION
     * set, accented characters will not be decomposed for collation. This
     * provides the fastest collation but will only produce correct results
     * for languages that do not use accents.
     * @see java.text.Collator#getDecomposition
     * @see java.text.Collator#setDecomposition
     */
    public final static int NO_DECOMPOSITION = 0;

    /**
     * Decomposition mode value. With CANONICAL_DECOMPOSITION
     * set, characters that are canonical variants according to Unicode 2.0
     * will be decomposed for collation. This is the default setting and
     * should be used to get correct collation of accented characters.
     * @see java.text.Collator#getDecomposition
     * @see java.text.Collator#setDecomposition
     */
    public final static int CANONICAL_DECOMPOSITION = 1;

    /**
     * Decomposition mode value. With FULL_DECOMPOSITION
     * set, both Unicode canonical variants and Unicode compatibility variants
     * will be decomposed for collation.  This causes not only accented
     * characters to be collated, but also characters that have special formats
     * to be collated with their norminal form. For example, the half-width and
     * full-width ASCII and Katakana characters are then collated together.
     * FULL_DECOMPOSITION is the most complete and therefore the slowest
     * decomposition mode.
     * @see java.text.Collator#getDecomposition
     * @see java.text.Collator#setDecomposition
     */
    public final static int FULL_DECOMPOSITION = 2;

    /**
     * Gets the Collator for the current default locale.
     * The default locale is determined by java.util.Locale.getDefault.
     * @return the Collator for the default locale.(for example, en_US)
     * @see java.util.Locale#getDefault
     */
    public static synchronized Collator getInstance() {
        return getInstance(Locale.getDefault());
    }

    /**
     * Gets the Collator for the desired locale.
     * @param desiredLocale the desired locale.
     * @return the Collator for the desired locale.
     * @see java.util.Locale
     * @see java.util.ResourceBundle
     */
    public static synchronized
    Collator getInstance(Locale desiredLocale)
    {
        RuleBasedCollator result = null;
        result = (RuleBasedCollator) cache.get(desiredLocale);
        if (result != null) {
            return (Collator)result.clone();  // make the world safe
        }

        // Load the resource of the desired locale from resource
        // manager.
        String colString;
        try {
            ResourceBundle resource = ResourceBundle.getBundle
                                      ("java.text.resources.LocaleElements",
                                       desiredLocale);

            colString = resource.getString("CollationElements");
        } catch (MissingResourceException e) {
            // return default US collation
            colString = "";
        }
        try
        {
            result = new RuleBasedCollator( CollationRules.DEFAULTRULES +
                                            colString );
        }
        catch(ParseException foo)
        {
            // predefined tables should contain correct grammar
            try {
                result = new RuleBasedCollator( CollationRules.DEFAULTRULES );
            } catch (ParseException bar) {
                // do nothing
            }
        }
        cache.put(desiredLocale,result);
        return result;
    }

    /**
     * Compares the source string to the target string according to the
     * collation rules for this Collator.  Returns an integer less than,
     * equal to or greater than zero depending on whether the source String is
     * less than, equal to or greater than the target string.  See the Collator
     * class description for an example of use.
     * <p>
     * For a one time comparison, this method has the best performance. If a
     * given String will be involved in multiple comparisons, CollationKey.compareTo
     * has the best performance. See the Collator class description for an example
     * using CollationKeys.
     * @param source the source string.
     * @param target the target string.
     * @return Returns an integer value. Value is less than zero if source is less than
     * target, value is zero if source and target are equal, value is greater than zero
     * if source is greater than target.
     * @see java.text.CollationKey
     * @see java.text.Collator#getCollationKey
     */
    public abstract int compare(String source, String target);

    /**
     * Transforms the String into a series of bits that can be compared bitwise
     * to other CollationKeys. CollationKeys provide better performance than
     * Collator.compare when Strings are involved in multiple comparisons.
     * See the Collator class description for an example using CollationKeys.
     * @param source the string to be transformed into a collation key.
     * @return the CollationKey for the given String based on this Collator's collation
     * rules. If the source String is null, a null CollationKey is returned.
     * @see java.text.CollationKey
     * @see java.text.Collator#compare
     */
    public abstract CollationKey getCollationKey(String source);

    /**
     * Convenience method for comparing the equality of two strings based on
     * this Collator's collation rules.
     * @param source the source string to be compared with.
     * @param target the target string to be compared with.
     * @return true if the strings are equal according to the collation
     * rules.  false, otherwise.
     * @see java.text.Collator#compare
     */
    public boolean equals(String source, String target)
    {
        return (compare(source, target) == Collator.EQUAL);
    }

    /**
     * Returns this Collator's strength property.  The strength property determines
     * the minimum level of difference considered significant during comparison.
     * See the Collator class description for an example of use.
     * @return this Collator's current strength property.
     * @see java.text.Collator#setStrength
     * @see java.text.Collator#PRIMARY
     * @see java.text.Collator#SECONDARY
     * @see java.text.Collator#TERTIARY
     * @see java.text.Collator#IDENTICAL
     */
    public synchronized int getStrength()
    {
        return strength;
    }

    /**
     * Sets this Collator's strength property.  The strength property determines
     * the minimum level of difference considered significant during comparison.
     * See the Collator class description for an example of use.
     * @param the new strength value.
     * @see java.text.Collator#getStrength
     * @see java.text.Collator#PRIMARY
     * @see java.text.Collator#SECONDARY
     * @see java.text.Collator#TERTIARY
     * @see java.text.Collator#IDENTICAL
     * @exception  IllegalArgumentException If the new strength value is not one of
     * PRIMARY, SECONDARY, TERTIARY or IDENTICAL.
     */
    public synchronized void setStrength(int newStrength) {
        if ((newStrength != PRIMARY) &&
            (newStrength != SECONDARY) &&
            (newStrength != TERTIARY) &&
            (newStrength != IDENTICAL))
            throw new IllegalArgumentException("Incorrect comparison level.");
        strength = newStrength;
    }

    /**
     * Get the decomposition mode of this Collator. Decomposition mode
     * determines how Unicode composed characters are handled. Adjusting
     * decomposition mode allows the user to select between faster and more
     * complete collation behavior.
     * <p>The three values for decomposition mode are:
     * <UL>
     * <LI>NO_DECOMPOSITION,
     * <LI>CANONICAL_DECOMPOSITION
     * <LI>FULL_DECOMPOSITION.
     * </UL>
     * See the documentation for these three constants for a description
     * of their meaning.
     * @return the decomposition mode
     * @see java.text.Collator#setDecomposition
     * @see java.text.Collator#NO_DECOMPOSITION
     * @see java.text.Collator#CANONICAL_DECOMPOSITION
     * @see java.text.Collator#FULL_DECOMPOSITION
     */
    public synchronized int getDecomposition()
    {
        return decmp;
    }
    /**
     * Set the decomposition mode of this Collator. See getDecomposition
     * for a description of decomposition mode.
     * @param the new decomposition mode
     * @see java.text.Collator#getDecomposition
     * @see java.text.Collator#NO_DECOMPOSITION
     * @see java.text.Collator#CANONICAL_DECOMPOSITION
     * @see java.text.Collator#FULL_DECOMPOSITION
     * @exception IllegalArgumentException If the given value is not a valid decomposition
     * mode.
     */
    public synchronized void setDecomposition(int decompositionMode) {
        if ((decompositionMode != NO_DECOMPOSITION) &&
            (decompositionMode != CANONICAL_DECOMPOSITION) &&
            (decompositionMode != FULL_DECOMPOSITION))
            throw new IllegalArgumentException("Wrong decomposition mode.");
        decmp = decompositionMode;
    }

    /**
     * Get the set of Locales for which Collators are installed.
     * @return the list of available locales which collators are installed.
     */
    public static synchronized Locale[] getAvailableLocales() {
        return LocaleData.getAvailableLocales("CollationElements");
    }

    /**
     * Overrides Cloneable
     */
    public Object clone()
    {
        try {
            return (Collator)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    /**
     * Compares the equality of two Collators.
     * @param that the Collator to be compared with this.
     * @return true if this Collator is the same as that Collator;
     * false otherwise.
     */
    public boolean equals(Object that)
    {
        if (this == that) return true;
        if (that == null) return false;
        if (getClass() != that.getClass()) return false;
        Collator other = (Collator) that;
        return ((strength == other.strength) &&
                (decmp == other.decmp));
    }

    /**
     * Generates the hash code for this Collator.
     */
    abstract public int hashCode();

    /**
     * Default constructor.  This constructor is
     * protected so subclasses can get access to it. Users typically create
     * a Collator sub-class by calling the factory method getInstance.
     * @see java.text.Collator#getInstance
     */
    protected Collator()
    {
        strength = TERTIARY;
        decmp = CANONICAL_DECOMPOSITION;
    }

    private int strength = 0;
    private int decmp = 0;
    private static Hashtable cache = new Hashtable();

    //
    // FIXME: These three constants should be removed.
    //
    /**
     * LESS is returned if source string is compared to be less than target
     * string in the compare() method.
     * @see java.text.Collator#compare
     */
    final static int LESS = -1;
    /**
     * EQUAL is returned if source string is compared to be equal to target
     * string in the compare() method.
     * @see java.text.Collator#compare
     */
    final static int EQUAL = 0;
    /**
     * GREATER is returned if source string is compared to be greater than
     * target string in the compare() method.
     * @see java.text.Collator#compare
     */
    final static int GREATER = 1;

    // Proclaims serialization compatibility to 1.1.
    static final long serialVersionUID = -7718728969026499504L;
 }
