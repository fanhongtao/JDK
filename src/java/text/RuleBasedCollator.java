/*
 * @(#)RuleBasedCollator.java	1.9 97/03/03
 *
 * (C) Copyright Taligent, Inc. 1996, 1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996, 1997 - All Rights Reserved
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

import java.lang.Character;
import java.util.Vector;
import java.util.Date;

/**
 * The <code>RuleBasedCollator</code> class is a concrete subclass of
 * <code>Collator</code> that provides a simple, data-driven, table collator.
 * With this class you can create a customized table-based <code>Collator</code>.
 * <code>RuleBasedCollator</code> maps characters to sort keys.
 *
 * <p>
 * <code>RuleBasedCollator</code> has the following restrictions
 * for efficiency (other subclasses may be used for more complex languages) :
 * <ol>
 * <li>The French secondary ordering is applied to the whole collator
 *     object.
 * <li>All non-mentioned Unicode characters are at the end of the
 *     collation order.
 * <li>Private use characters are treated as identical.  The private
 *     use area in Unicode is <code>0xE800</code>-<code>0xF8FF</code>.
 * </ol>
 *
 * <p>
 * The collation table is composed of a list of collation rules, where each
 * rule is of three forms:
 * <pre>
 *    < modifier >
 *    < relation > < text-argument >
 *    < reset > < text-argument >
 * </pre>
 * The following demonstrates how to create your own collation rules:
 * <UL Type=round>
 *    <LI><strong>Text-Argument</strong>: A text-argument is any sequence of
 *        characters, excluding special characters (that is, whitespace
 *        characters and the characters used in modifier, relation and reset).
 *        If those characters are desired, you can put them in single quotes
 *        (e.g. ampersand => '&').
 *    <LI><strong>Modifier</strong>: There is a single modifier
 *        which is used to specify that all accents (secondary differences) are
 *        backwards.
 *        <p>'@' : Indicates that accents are sorted backwards, as in French.
 *    <LI><strong>Relation</strong>: The relations are the following:
 *        <UL Type=square>
 *            <LI>'<' : Greater, as a letter difference (primary)
 *            <LI>';' : Greater, as an accent difference (secondary)
 *            <LI>',' : Greater, as a case difference (tertiary)
 *            <LI>'=' : Equal
 *        </UL>
 *    <LI><strong>Reset</strong>: There is a single reset
 *        which is used primarily for contractions and expansions, but which
 *        can also be used to add a modification at the end of a set of rules.
 *        <p>'&' : Indicates that the next rule follows the position to where
 *            the reset text-argument would be sorted.
 * </UL>
 *
 * <p>
 * This sounds more complicated than it is in practice. For example, the
 * following are equivalent ways of expressing the same thing:
 * <blockquote>
 * <pre>
 * a < b < c
 * a < b & b < c
 * a < c & a < b
 * </pre>
 * </blockquote>
 * Notice that the order is important, as the subsequent item goes immediately
 * after the text-argument. The following are not equivalent:
 * <blockquote>
 * <pre>
 * a < b & a < c
 * a < c & a < b
 * </pre>
 * </blockquote>
 * Either the text-argument must already be present in the sequence, or some
 * initial substring of the text-argument must be present. (e.g. "a < b & ae <
 * e" is valid since "a" is present in the sequence before "ae" is reset). In
 * this latter case, "ae" is not entered and treated as a single character;
 * instead, "e" is sorted as if it were expanded to two characters: "a"
 * followed by an "e". This difference appears in natural languages: in
 * traditional Spanish "ch" is treated as though it contracts to a single
 * character (expressed as "c < ch < d"), while in traditional German "ä"
 * (a-umlaut) is treated as though it expands to two characters (expressed as
 * "a & ae ; ä < b").
 *
 * <p>
 * <strong>Ignorable Characters</strong>
 * <p>
 * For ignorable characters, the first rule must start with a relation (the
 * examples we have used above are really fragments; "a < b" really should be
 * "< a < b"). If, however, the first relation is not "<", then all the all
 * text-arguments up to the first "<" are ignorable. For example, ", - < a < b"
 * makes "-" an ignorable character, as we saw earlier in the word
 * "black-birds". In the samples for different languages, you see that most
 * accents are ignorable.
 *
 * <p><strong>Normalization and Accents</strong>
 * <p> 
 * The <code>Collator</code> object automatically normalizes text internally
 * to separate accents from base characters where possible. This is done both when
 * processing the rules, and when comparing two strings. <code>Collator</code>
 * also uses the Unicode canonical mapping to ensure that combining sequences
 * are sorted properly (for more information, see
 * <A HREF="http://www.aw.com/devpress">The Unicode Standard, Version 2.0</A>.)</P>
 *
 * <p><strong>Errors</strong>
 * <p>
 * The following are errors:
 * <UL Type=round>
 *     <LI>A text-argument not preceded by either a reset or relation character
 *        (e.g. "a < b c < d").
 *     <LI>A relation or reset character not followed by a text-argument
 *        (e.g. "a < , b").
 *     <LI>A reset where the text-argument (or an initial substring of the
 *         text-argument) is not already in the sequence.
 *         (e.g. "a < b & e < f")
 * </UL>
 * If you produce one of these errors, a <code>RuleBasedCollator</code> throws
 * a <code>ParseException</code>.
 *
 * <p><strong>Examples</strong>
 * <p>Simple:     "< a < b < c < d"
 * <p>Norwegian:  "< a,A< b,B< c,C< d,D< e,E< f,F< g,G< h,H< i,I< j,J
 *                 < k,K< l,L< m,M< n,N< o,O< p,P< q,Q< r,R< s,S< t,T
 *                 < u,U< v,V< w,W< x,X< y,Y< z,Z
 *                 < \u00E5=a\u030A,\u00C5=A\u030A
 *                 ;aa,AA< \u00E6,\u00C6< \u00F8,\u00D8"
 *
 * <p>
 * Normally, to create a rule-based Collator object, you will use
 * <code>Collator</code>'s factory method <code>getInstance</code>.
 * However, to create a rule-based Collator object with specialized
 * rules tailored to your needs, you construct the <code>RuleBasedCollator</code>
 * with the rules contained in a <code>String</code> object. For example:
 * <blockquote>
 * <pre>
 * String Simple = "< a < b < c < d";
 * RuleBasedCollator mySimple = new RuleBasedCollator(Simple);
 * </pre>
 * </blockquote>
 * Or:
 * <blockquote>
 * <pre>
 * String Norwegian = "< a,A< b,B< c,C< d,D< e,E< f,F< g,G< h,H< i,I< j,J" +
 *                 "< k,K< l,L< m,M< n,N< o,O< p,P< q,Q< r,R< s,S< t,T" +
 *                 "< u,U< v,V< w,W< x,X< y,Y< z,Z" +
 *                 "< \u00E5=a\u030A,\u00C5=A\u030A" +
 *                 ";aa,AA< \u00E6,\u00C6< \u00F8,\u00D8";
 * RuleBasedCollator myNorwegian = new RuleBasedCollator(Norwegian);
 * </pre>
 * </blockquote>
 *
 * <p>
 * Combining <code>Collator</code>s is as simple as concatenating strings.
 * Here's an example that combines two <code>Collator</code>s from two
 * different locales:
 * <blockquote>
 * <pre>
 * // Create an en_US Collator object
 * RuleBasedCollator en_USCollator = (RuleBasedCollator)
 *     Collator.getInstance(new Locale("en", "US", ""));
 * // Create a da_DK Collator object
 * RuleBasedCollator da_DKCollator = (RuleBasedCollator)
 *     Collator.getInstance(new Locale("da", "DK", ""));
 * // Combine the two
 * // First, get the collation rules from en_USCollator
 * String en_USRules = en_USCollator.getRules();
 * // Second, get the collation rules from da_DKCollator
 * String da_DKRules = da_DKCollator.getRules();
 * RuleBasedCollator newCollator =
 *     new RuleBasedCollator(en_USRules + da_DKRules);
 * // newCollator has the combined rules
 * </pre>
 * </blockquote>
 *
 * <p>
 * Another more interesting example would be to make changes on an existing
 * table to create a new <code>Collator</code> object.  For example, add
 * "& C < ch, cH, Ch, CH" to the <code>en_USCollator</code> object to create
 * your own:
 * <blockquote>
 * <pre>
 * // Create a new Collator object with additional rules
 * String addRules = "& C < ch, cH, Ch, CH";
 * RuleBasedCollator myCollator =
 *     new RuleBasedCollator(en_USCollator + addRules);
 * // myCollator contains the new rules
 * </pre>
 * </blockquote>
 *
 * <p>
 * The following example demonstrates how to change the order of
 * non-spacing accents,
 * <blockquote>
 * <pre>
 * // old rule
 * String oldRules = "=\u0301;\u0300;\u0302;\u0308"    // main accents
 *                 + ";\u0327;\u0303;\u0304;\u0305"    // main accents
 *                 + ";\u0306;\u0307;\u0309;\u030A"    // main accents
 *                 + ";\u030B;\u030C;\u030D;\u030E"    // main accents
 *                 + ";\u030F;\u0310;\u0311;\u0312"    // main accents
 *                 + "< a , A ; ae, AE ; \u00e6 , \u00c6"
 *                 + "< b , B < c, C < e, E & C < d, D";
 * // change the order of accent characters
 * String addOn = "& \u0300 ; \u0308 ; \u0302";
 * RuleBasedCollator myCollator = new RuleBasedCollator(oldRules + addOn);
 * </pre>
 * </blockquote>
 *
 * <p>
 * The last example shows how to put new primary ordering in before the
 * default setting. For example, in Japanese <code>Collator</code>, you
 * can either sort English characters before or after Japanese characters,
 * <blockquote>
 * <pre>
 * // get en_US Collator rules
 * RuleBasedCollator en_USCollator = (RuleBasedCollator)Collator.getInstance(Locale.US);
 * // add a few Japanese character to sort before English characters
 * // suppose the last character before the first base letter 'a' in
 * // the English collation rule is \u2212
 * String jaString = "& \u2212 < \u3041, \u3042 < \u3043, \u3044";
 * RuleBasedCollator myJapaneseCollator = new
 *     RuleBasedCollator(en_USCollator.getRules() + jaString);
 * </pre>
 * </blockquote>
 *
 * @see        Collator
 * @see        CollationElementIterator
 * @version    1.9 03/03/97
 * @author     Helena Shih
 */
public class RuleBasedCollator extends Collator{

    /**
     * RuleBasedCollator constructor.  This takes the table rules and builds
     * a collation table out of them.  Please see RuleBasedCollator class
     * description for more details on the collation rule syntax.
     * @see java.util.Locale
     * @param rules the collation rules to build the collation table from.
     * @exception ParseException A format exception
     * will be thrown if the build process of the rules fails. For
     * example, build rule "a < b c < d" will cause the constructor to
     * throw the ParseException.
     */
    public RuleBasedCollator(String rules) throws ParseException {
        setStrength(Collator.TERTIARY);
        build(rules);
    }

    /**
     * Gets the table-based rules for the collation object.
     * @return returns the collation rules that the table collation object
     * was created from.
     */
    public String getRules()
    {
        if (ruleTable == null) {
            ruleTable = mPattern.getPattern();
            mPattern = null;
        }
        return ruleTable;
    }


    /**
     * Return a CollationElementIterator for the given String.
     * @see java.text.CollationElementIterator
     */
    public CollationElementIterator getCollationElementIterator(String source) {
        return new CollationElementIterator( source, this );
    }

    /**
     * Compares the character data stored in two different strings based on the
     * collation rules.  Returns information about whether a string is less
     * than, greater than or equal to another string in a language.
     * This can be overriden in a subclass.
     */
    public int compare(String source, String target)
    {
        int result = Collator.EQUAL;
        CollationElementIterator targetCursor
            = new CollationElementIterator(target, this);
        CollationElementIterator sourceCursor
            = new CollationElementIterator(source, this);
        int sOrder = 0;
        int tOrder = 0;
        boolean gets = true, gett = true;
        boolean checks = true, checkt = true;
        while(true) {
            if (gets) sOrder = sourceCursor.next(); else gets = true;
            if (gett) tOrder = targetCursor.next(); else gett = true;
            if ((sOrder == CollationElementIterator.NULLORDER)||
                (tOrder == CollationElementIterator.NULLORDER))
                break;
            if (checks) sOrder = strengthOrder(sOrder);
            if (checkt) tOrder = strengthOrder(tOrder);
            checks = true;
            checkt = true;
            if (sOrder == CollationElementIterator.UNMAPPEDCHARVALUE)
                checks = false;
            if (tOrder == CollationElementIterator.UNMAPPEDCHARVALUE)
                checkt = false;
            if (sOrder == tOrder)
                continue;
            if (CollationElementIterator.primaryOrder(sOrder) !=
                CollationElementIterator.primaryOrder(tOrder))
            {
                if (sOrder == 0) {
                    gett = false;
                    continue;
                }
                if (tOrder == 0) {
                    gets = false;
                    continue;
                }
                if (sOrder < MAXIGNORABLE)  // sk is ignorable
                {
                    if (tOrder < MAXIGNORABLE)  // tk is ignorable
                    {
                        result = checkSecTerDiff(sOrder, tOrder, result);
                        continue;                   // both advances
                    }
                    else
                    {
                        if (isFrenchSec ||
                            ((result == Collator.EQUAL) ||
                             (strengthResult != Collator.SECONDARY)))
                        {
                            strengthResult = Collator.SECONDARY;
                            result = Collator.GREATER;
                        }
                        gett = false;
                        continue;
                    }
                }
                else if (tOrder < MAXIGNORABLE)
                {
                    // record differences
                    if (isFrenchSec ||
                        ((result == Collator.EQUAL) ||
                         (strengthResult != Collator.SECONDARY)))
                    {
                        result = Collator.LESS;
                        strengthResult = Collator.SECONDARY;
                    }
                    gets = false;
                    continue;
                }
                if (CollationElementIterator.primaryOrder(sOrder) <
                    CollationElementIterator.primaryOrder(tOrder))
                    result = Collator.LESS;
                else
                    result = Collator.GREATER;
                break;
            }
            else
                result = checkSecTerDiff(sOrder, tOrder, result);
        } // while()
        if (sOrder != CollationElementIterator.NULLORDER) {
            if (tOrder == CollationElementIterator.NULLORDER) {
                // later check if ignorable, secondary or tertiary difference
                if (isIgnorable(sOrder) && !isFrenchSec)
                    return result;
                if (strengthOrder(sOrder) != 0)
                    result = Collator.GREATER;
            }
        }
        if (tOrder != CollationElementIterator.NULLORDER) {
            if (sOrder == CollationElementIterator.NULLORDER) {
                if (isIgnorable(tOrder) && !isFrenchSec)
                    return result;
                if (strengthOrder(tOrder) != 0)
                    result = Collator.LESS;
            }
        }
        return result;
    }
    /**
     * Transforms the string into a series of characters that can be compared
     * with CollationKey.compareTo. This overrides java.text.Collator.getCollationKey.
     * It can be overriden in a subclass.
     */
    public CollationKey getCollationKey(String source)
    {
        if (source == null)
            return null;
        primResult.setLength(0);
        secResult.setLength(0);
        terResult.setLength(0);
        int order = 0;
        boolean compareSec = (getStrength() >= Collator.SECONDARY);
        boolean compareTer = (getStrength() >= Collator.TERTIARY);
        int secOrder = CollationElementIterator.NULLORDER;
        int terOrder = CollationElementIterator.NULLORDER;
        CollationElementIterator sourceCursor = new
            CollationElementIterator(source, this);
        while ((order = sourceCursor.next()) !=
               CollationElementIterator.NULLORDER)
        {
            secOrder = CollationElementIterator.secondaryOrder(order);
            terOrder = CollationElementIterator.tertiaryOrder(order);
            if (!isIgnorable(order))
            {
                primResult.append((char)
                     (CollationElementIterator.primaryOrder(order)
                      + COLLATIONKEYOFFSET));
                if (compareSec)
                    secResult.append((char)(secOrder+ COLLATIONKEYOFFSET));
                if (compareTer)
                    terResult.append((char)(terOrder+ COLLATIONKEYOFFSET));
            }
            else
            {
                if (compareSec)
                    secResult.append((char)
                        (secOrder+maxSecOrder+ COLLATIONKEYOFFSET));
                if (compareTer)
                    terResult.append((char)
                        (terOrder+maxTerOrder+ COLLATIONKEYOFFSET));
            }
        }
        if (isFrenchSec)
        {
            // reverse the secondary and tertiary portion
            reverse(secResult);
            reverse(terResult);
        }
        primResult.append((char)0);
        secResult.append((char)0);
        secResult.append(terResult.toString());
        primResult.append(secResult.toString());
        return new CollationKey(source, primResult.toString());
    }
    /**
     * Standard override; no change in semantics.
     */
    public Object clone() {
        RuleBasedCollator other = (RuleBasedCollator) super.clone();
        other.primResult = new StringBuffer(MAXTOKENLEN);
        other.secResult = new StringBuffer(MAXTOKENLEN);
        other.terResult = new StringBuffer(MAXTOKENLEN);
        other.key = new StringBuffer(MAXKEYSIZE);
        return other;
    }

    /**
     * Compares the equality of two collation objects.
     * @param obj the table-based collation object to be compared with this.
     * @return true if the current table-based collation object is the same
     * as the table-based collation object obj; false otherwise.
     */
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;  // super does class check
        RuleBasedCollator other = (RuleBasedCollator) obj;
        // all other non-transient information is also contained in rules.
        return (getRules().equals(other.getRules()));
    }
    /**
     * Generates the hash code for the table-based collation object
     */
    public int hashCode() {
        return getRules().hashCode();
    }

    // ==============================================================
    // private
    // ==============================================================

    /**
     * Create a table-based collation object with the given rules.
     * @see java.util.RuleBasedCollator#RuleBasedCollator
	 * @exception ParseException If the rules format is incorrect.
     */
    private void build(String pattern) throws ParseException
    {
        mapping = new CompactIntArray((int)UNMAPPED);
        int aStrength = Collator.IDENTICAL;
        boolean isSource = true;
        int i = 0;
        String expChars;
        String groupChars;
        if (pattern.length() == 0)
            throw new ParseException("Build rules empty.", 0);
        /* Normalize the build rules.  Find occurances of all
         *  decomposed characters and normalize the rules before
         *  feeding into the builder.
         */
        pattern = DecompositionIterator.decompose(pattern, getDecomposition());
        mPattern = new MergeCollation(pattern);
        for (i = 0; i < mPattern.getCount(); ++i)
        {
		    PatternEntry entry = mPattern.getItemAt(i);
    		if (entry != null) {
	    		groupChars = entry.getChars();
			    if ((groupChars.length() > 1) &&
				    (groupChars.charAt(groupChars.length()-1) == '@')) {
    				isFrenchSec = true;
    				groupChars = groupChars.substring(0, groupChars.length()-1);
		    	}
		    	expChars = entry.getExtension();
    			if (expChars.length() != 0) {
    				addExpandOrder(groupChars, expChars, entry.getStrength());
    			} else if (groupChars.length() > 1) {
	    			addContractOrder(groupChars, entry.getStrength());
		    		lastChar = groupChars.charAt(0);
    			} else {
	    			char ch = groupChars.charAt(0);
		    		addOrder(ch, entry.getStrength());
			    	lastChar = ch;
			    }
		    }
	    }
        commit();
        mapping.compact();
    }
    /**
     * Look up for unmapped values in the expanded character table.
     */
    private final void commit()
    {
        if (expandTable != null)
        {
            for (int i = 0; i < expandTable.size(); i++)
            {
                int[] valueList = (int [])expandTable.elementAt(i);
                for (int j = 0; j < valueList.length; j++)
                {
                    if ((valueList[j] < EXPANDCHARINDEX) &&
                             (valueList[j] > CHARINDEX))
                    {
                        char ch = (char)(valueList[j] - CHARINDEX);
                        int realValue = mapping.elementAt(ch);
                        if (realValue == UNMAPPED)  // ignorable?
                        {
                            valueList[j] = IGNORABLEMASK & valueList[j-1];
                        }
                        else if (realValue >= CONTRACTCHARINDEX)
                        {
                            EntryPair pair = null;
                            Vector groupList = (Vector)
                                contractTable.elementAt(realValue
                                                        - CONTRACTCHARINDEX);
                            pair = (EntryPair)groupList.lastElement();
                            valueList[j] = pair.value;
                        }
                        else
                        {
                            valueList[j] = realValue;
                        }
                    }
                }
            }
        }
    }
    /**
     *  Returns true if a character is a seperator.
     */
    private final boolean isSpecialChar(char c)
    {
        return (c=='/' || c==',' || c==';' || c=='<' ||
                c=='@' || c=='=');
    }
    /**
     *  Increment of the last order based on the comparison level.
     */
    private final int increment(int aStrength, int lastValue)
    {
        switch(aStrength)
        {
        case Collator.PRIMARY:
            lastValue += PRIMARYORDERINCREMENT;
            lastValue &= PRIMARYORDERMASK;
            break;
        case Collator.SECONDARY:
            lastValue += SECONDARYORDERINCREMENT;
            lastValue &= SECONDARYDIFFERENCEONLY;
            if (isOverIgnore)
                maxSecOrder++;
            break;
        case Collator.TERTIARY:
            lastValue += TERTIARYORDERINCREMENT;
            if (isOverIgnore)
                maxTerOrder++;
            break;
        }
        return lastValue;
    }
    /**
     *  Adds a character and its designated order into the collation table.
     */
    private final void addOrder(char ch,
                                int aStrength)
    {
        int order = mapping.elementAt(ch);
        if (order == UNMAPPED)
        {
            currentOrder = increment(aStrength, currentOrder);
            mapping.setElementAt(ch, currentOrder);
        } else if (order < 0) {
    		int entry = order & SECONDARYRESETMASK;
	    	int value = CONTRACTCHARINDEX + entry;
	    	key.setLength(0);
	    	key.append(ch);
            Vector list = getContractValues(entry);
    		EntryPair pair = new EntryPair();
	    	pair.entryName = key.toString();
            currentOrder = increment(aStrength, currentOrder);
    		pair.value = currentOrder;
	    	mapping.setElementAt(ch, value);
            list.insertElementAt(pair, 0);
        } else if (order > CONTRACTCHARINDEX) {
            key.setLength(0);
            key.append(ch);
            addContractOrder(key.toString(), aStrength);
        }
    }
    /**
     *  Adds the contracting string into the collation table.
     */
    private final void addContractOrder(String groupChars,
                                  int   aStrength)
    {
        if (contractTable == null)
        {
            contractTable = new Vector(INITIALTABLESIZE);
        }
        // Look for the entry, for example c as in ch entry
        key.setLength(0);
        key.append(groupChars.charAt(0));
        int entry = UNMAPPED;
        Vector tableEntry = null;
        EntryPair pair = new EntryPair();
        int i;
        for (i = 0; i < contractTable.size(); i++)
        {
            tableEntry = (Vector)contractTable.elementAt(i);
            if ((entry = getEntry(tableEntry, key.toString())) != UNMAPPED)
                break;
        }
        int lastValue = 0;
        if (aStrength != IDENTICAL)
            lastValue = increment(aStrength, currentOrder);
        else
        {
            lastValue = mapping.elementAt(groupChars.charAt(0));
	    	if (lastValue == UNMAPPED) {
		    	currentOrder = mapping.elementAt(lastChar);
			    lastValue = increment(aStrength, currentOrder);
    		}
        }
        if (entry != UNMAPPED) // found one
        {
            pair.entryName = groupChars;
            pair.value = lastValue;
            tableEntry.insertElementAt(pair, 0);
        }
        else
        {
            Vector valueTable = new Vector(INITIALTABLESIZE);
            int tmpValue = CONTRACTCHARINDEX + contractTable.size();
            // put last char in the subdict
    		int order = mapping.elementAt(groupChars.charAt(0));
	    	if (order == UNMAPPED) {
		    	pair = null;
			    mapping.setElementAt(groupChars.charAt(0),
			                 PRIMARYORDERMASK + contractTable.size());
    		} else {
	            pair.entryName = key.toString();
	            mapping.setElementAt(groupChars.charAt(0), tmpValue);
			    pair.value = order;
    	        valueTable.insertElementAt(pair, 0);
	    	}
            EntryPair swapPair = new EntryPair();
            swapPair.entryName = groupChars;
            swapPair.value = lastValue;
            valueTable.insertElementAt(swapPair, 0);
            contractTable.insertElementAt(valueTable, contractTable.size());
        }
        if (aStrength != IDENTICAL)
            currentOrder = lastValue;
    }

    private final int getEntry(Vector list, String name) {
        for (int i = 0; i < list.size(); i++) {
            EntryPair pair = (EntryPair)list.elementAt(i);
            if (pair.entryName.equals(name)) {
                return i;
            }
        }
        return UNMAPPED;
    }
    /**
     *  Get the entry of hash table of the contracting string in the collation
     *  table.
     *  @param ch the starting character of the contracting string
     */
    Vector getContractValues(char ch)
    {
        int index = mapping.elementAt(ch);
    	return getContractValues(index - CONTRACTCHARINDEX);
    }

    Vector getContractValues(int index)
    {
        if (index >= 0)
        {
            return (Vector)contractTable.elementAt(index);
        }
        else // not found
        {
            return null;
        }
    }

    /**
     *  Adds the expanding string into the collation table.
     */
    private final void addExpandOrder(String contractChars,
                                String expandChars,
                                int   aStrength) throws ParseException
    {
        EntryPair pair = new EntryPair();
        // INITIALTABLESIZE is an estimated number
        if (expandTable == null)
        {
            expandTable = new Vector(INITIALTABLESIZE);
        }
        int[] valueList = new int[expandChars.length()+1];
        // Expand flag + index
        int tmpValue = EXPANDCHARINDEX + expandTable.size();
        // need to check if the entry is key or not later
        key.setLength(0);
        int keyValue = UNMAPPED;
        if (contractChars.length() > 1)
        {
    		addContractOrder(contractChars, aStrength);
	    	lastChar = contractChars.charAt(0);
            Vector list = getContractValues(contractChars.charAt(0));
            int entry = UNMAPPED;
            entry = getEntry(list, contractChars);
            if (entry != UNMAPPED) {
                pair = (EntryPair)list.elementAt(entry);
                keyValue = pair.value;
            }
            pair.entryName = contractChars;
            pair.value = tmpValue;
        }
        else
        {
            char ch = contractChars.charAt(0);
	    	if (mapping.elementAt(ch) == UNMAPPED) {
		    	addOrder(ch, aStrength);
			    lastChar = ch;
    		}
            keyValue = mapping.elementAt(lastChar);
            mapping.setElementAt(lastChar, tmpValue);
        }
        valueList[0] = keyValue;
        for (int i = 0; i < expandChars.length(); i++)
        {
            int mapValue = mapping.elementAt(expandChars.charAt(i));
            if (mapValue >= CONTRACTCHARINDEX)
            {
                key.append(expandChars.charAt(i));
                int foundValue = CHARINDEX + expandChars.charAt(i);
                Vector list = getContractValues(expandChars.charAt(i));
                if (list != null) {
                    int entry = UNMAPPED;
                    entry = getEntry(list, key.toString());
                    if (entry != UNMAPPED) {
                        pair = (EntryPair)list.elementAt(entry);
                        foundValue = pair.value;
                    }
                }
                key.setLength(0);
                valueList[i+1] = foundValue;
            }
            else if (mapValue != UNMAPPED)
            {
                valueList[i+1] = mapValue;
            }
            else
            {
                valueList[i+1] = CHARINDEX + (int)(expandChars.charAt(i));
            }
        }
        expandTable.insertElementAt(valueList, expandTable.size());
    }

    /**
     *  Get the entry of hash table of the expanding string in the collation
     *  table.
     *  @param ch the starting character of the expanding string
     */
    final int[] getExpandValueList(char ch)
    {
        int expIndex = mapping.elementAt(ch);
        if ((expIndex >= EXPANDCHARINDEX) &&
            (expIndex < CONTRACTCHARINDEX))
        {
            int tmpIndex = expIndex - EXPANDCHARINDEX;
            return (int[])expandTable.elementAt(tmpIndex);
        }
        else
        {
            return null;
        }
    }
    /**
     *  Get the entry of hash table of the expanding string in the collation
     *  table.
     *  @param idx the index of the expanding string value list
     */
    final int[] getExpandValueList(int idx)
    {
        if (idx < expandTable.size())
        {
            return (int[])expandTable.elementAt(idx);
        }
        else
        {
            return null;
        }
    }
    /**
     *  Get the comparison order in the desired strength.  Ignore the other
     *  differences.
     *  @param order The order value
     */
    private final int strengthOrder(int order)
    {
        if (getStrength() == Collator.PRIMARY)
        {
            order &= PRIMARYDIFFERENCEONLY;
        } else if (getStrength() == Collator.SECONDARY)
        {
            order &= SECONDARYDIFFERENCEONLY;
        }
        return order;
    }
    /**
     *  Check if a comparison order is ignorable.
     *  @return true if a character is ignorable, false otherwise.
     */
    final boolean isIgnorable(int order)
    {
        return (((int)CollationElementIterator.primaryOrder(order) == 0) ?
                true : false);
    }
    /**
     *  Get the comarison order of a character from the collation table.
     *  @return the comparison order of a character.
     */
    final int getUnicodeOrder(char ch)
    {
        return mapping.elementAt(ch);
    }

    /**
     * Check for the secondary and tertiary differences of source and
     * target comparison orders.
     * @return Collator.LESS if sOrder < tOrder; EQUAL if sOrder == tOrder;
     * Collator.GREATER if sOrder > tOrder.
     */
    private final int checkSecTerDiff(int sOrder,
                                      int tOrder,
                                      int result)
    {
        int endResult = result;
        if (CollationElementIterator.secondaryOrder(sOrder) !=
            CollationElementIterator.secondaryOrder(tOrder))
        {
            if (isFrenchSec ||
                ((endResult == Collator.EQUAL) ||
                 (strengthResult != Collator.SECONDARY)))
            {
                strengthResult = Collator.SECONDARY;
                if (CollationElementIterator.secondaryOrder(sOrder) <
                    CollationElementIterator.secondaryOrder(tOrder))
                    endResult = Collator.LESS;
                else
                    endResult = Collator.GREATER;
            }
        }
        else if ((CollationElementIterator.tertiaryOrder(sOrder) !=
                  CollationElementIterator.tertiaryOrder(tOrder)) &&
		 ((endResult == Collator.EQUAL) || isFrenchSec))
        {
            strengthResult = Collator.TERTIARY;
            if (CollationElementIterator.tertiaryOrder(sOrder) <
                CollationElementIterator.tertiaryOrder(tOrder))
                endResult = Collator.LESS;
            else
                endResult = Collator.GREATER;
        }
        return endResult;
    }
    /**
     * Reverse a string.
     */
    private final void reverse (StringBuffer result)
    {
        String store = result.toString();
        result.setLength(0);
        for (int i = store.length() - 1; i >= 0; i--) {
            result.append(store.charAt(i));
        }
    }

    static int CHARINDEX = 0x70000000;  // need look up in .commit()
    static int EXPANDCHARINDEX = 0x7E000000; // Expand index follows
    static int CONTRACTCHARINDEX = 0x7F000000;  // contract indexes follow
    static int UNMAPPED = 0xFFFFFFFF;

    private final static int SHORT_MAX_VALUE = 32767;
    private final static int PRIMARYORDERINCREMENT = 0x00010000;
    private final static int MAXIGNORABLE = 0x00010000;
    private final static int SECONDARYORDERINCREMENT = 0x00000100;
    private final static int TERTIARYORDERINCREMENT = 0x00000001;
    final static int PRIMARYORDERMASK = 0xffff0000;
    final static int SECONDARYORDERMASK = 0x0000ff00;
    final static int TERTIARYORDERMASK = 0x000000ff;
    private final static int SECONDARYRESETMASK = 0x0000ffff;
    private final static int IGNORABLEMASK = 0x0000ffff;
    private final static int PRIMARYDIFFERENCEONLY = 0xffff0000;
    private final static int SECONDARYDIFFERENCEONLY = 0xffffff00;
    private final static int INITIALTABLESIZE = 20;
    private final static int MAXKEYSIZE = 5;
    static int PRIMARYORDERSHIFT = 16;
    static int SECONDARYORDERSHIFT = 8;
    private final static int MAXTOKENLEN = 256;
    private final static int MAXRULELEN = 512;
    private final static int COLLATIONKEYOFFSET = 1;

    private boolean isFrenchSec = false;
    private String ruleTable = null;

    private CompactIntArray mapping = null;
    private Vector   contractTable = null;
    private Vector   expandTable = null;

    // transients, only used in build or processing
    private transient MergeCollation mPattern = null;
    private transient boolean isOverIgnore = false;
    private transient int currentOrder = 0;
    private transient short maxSecOrder = 0;
    private transient short maxTerOrder = 0;
    private transient char lastChar;
    private transient StringBuffer key = new StringBuffer(MAXKEYSIZE);
    private transient int strengthResult = Collator.IDENTICAL;
    private transient StringBuffer primResult = new StringBuffer(MAXTOKENLEN);
    private transient StringBuffer secResult = new StringBuffer(MAXTOKENLEN);
    private transient StringBuffer terResult = new StringBuffer(MAXTOKENLEN);
}

