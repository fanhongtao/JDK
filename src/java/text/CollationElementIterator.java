/*
 * @(#)CollationElementIterator.java	1.16 98/10/07
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

/**
 * The <code>CollationElementIterator</code> class is used as an iterator
 * to walk through each character of an international string. Use the iterator
 * to return the ordering priority of the positioned character. The ordering
 * priority of a character, which we refer to as a key, defines how a character
 * is collated in the given collation object.
 *
 * <p>
 * For example, consider the following in Spanish:
 * <blockquote>
 * <pre>
 * "ca" -> the first key is key('c') and second key is key('a').
 * "cha" -> the first key is key('ch') and second key is key('a').
 * </pre>
 * </blockquote>
 * And in German,
 * <blockquote>
 * <pre>
 * "\u00e4b"-> the first key is key('a'), the second key is key('e'), and
 * the third key is key('b').
 * </pre>
 * </blockquote>
 * The key of a character is an integer composed of primary order(short),
 * secondary order(byte), and tertiary order(byte). Java strictly defines
 * the size and signedness of its primitive data types. Therefore, the static
 * functions <code>primaryOrder</code>, <code>secondaryOrder</code>, and
 * <code>tertiaryOrder</code> return <code>int</code>, <code>short</code>,
 * and <code>short</code> respectively to ensure the correctness of the key
 * value.
 *
 * <p>
 * Example of the iterator usage,
 * <blockquote>
 * <pre>
 * // get the first key of the string
 * String str = "This is a test";
 * CollationElementIterator c =
 *     new CollationElementIterator(str, 0, str.length(),
 *                                  Collator.getInstance());
 * int primaryOrder = CollationElementIterator.primaryOrder(c->next());
 * </pre>
 * </blockquote>
 *
 * <p>
 * <code>CollationElementIterator.next</code> returns the collation order
 * of the next character. A collation order consists of primary order,
 * secondary order and tertiary order. The data type of the collation
 * order is <strong>int</strong>. The first 16 bits of a collation order
 * is its primary order; the next 8 bits is the secondary order and the
 * last 8 bits is the tertiary order.
 *
 * @see                Collator
 * @see                RuleBasedCollator
 * @version            1.16 10/07/98
 * @author             Helena Shih
 */
public final class CollationElementIterator
{
    /**
     * Null order which indicates the end of string is reached by the
     * cursor.
     */
    public final static int NULLORDER = 0xffffffff;
    /**
     * CollationElementIterator constructor.  This takes the source string and
     * the collation object.  The cursor will walk thru the source string based
     * on the predefined collation rules.  If the source string is empty,
     * NULLORDER will be returned on the calls to next().
     * @param sourceText the source string.
     * @param order the collation object.
     */
    CollationElementIterator(String sourceText, RuleBasedCollator order) {
        ordering = order;
        if ( sourceText.length() != 0 ) {
            text = new DecompositionIterator(sourceText, 0,
                                             sourceText.length(),
                                             order.getDecomposition());
        }
    }
    /**
     * Resets the cursor to the beginning of the string.
     */
    public void reset()
    {
        if (text != null) {
            text.reset();
            text.setDecomposition(ordering.getDecomposition());
        }
        buffer = null;
        expIndex = 0;
        swapOrder = 0;
    }

    /**
     * Get the ordering priority of the next character in the string.
     * @return the next character's ordering.  Returns NULLORDER if
     * the end of string is reached.
     */
    public int next()
    {
        if (text == null)
            return NULLORDER;
        if (text.getDecomposition() != ordering.getDecomposition())
            text.setDecomposition(ordering.getDecomposition());
        if (buffer != null)
        {
            if (expIndex < buffer.length)
            {
                return strengthOrder(buffer[expIndex++]);
            }
            else
            {
                buffer = null;
                expIndex = 0;
            }
        } else if (swapOrder != 0) {
            int order = swapOrder << 16;
            swapOrder = 0;
            return order;
        }
        char ch = text.next();
        if (ch == DecompositionIterator.NULLORDER) {
            return NULLORDER;
        }

        int value = ordering.getUnicodeOrder(ch);
        if (value == RuleBasedCollator.UNMAPPED)
        {
            swapOrder = ch;
            return UNMAPPEDCHARVALUE;
        }
        else if (value < RuleBasedCollator.CHARINDEX)
        {
            return strengthOrder(value);
        }
        // contract characters
        else if (value >= RuleBasedCollator.CONTRACTCHARINDEX)
        {
            return strengthOrder(nextContractChar(ch));
        }
        else if (value >= RuleBasedCollator.EXPANDCHARINDEX)
        {
            buffer = ordering.getExpandValueList(ch);
            expIndex = 0;
            return strengthOrder(buffer[expIndex++]);
        }
        return NULLORDER;
    }
    /**
     * Get the primary order of a collation order.
     * @param order the collation order
     * @return the primary order of a collation order.
     */
    public final static int primaryOrder(int order)
    {
        order &= RuleBasedCollator.PRIMARYORDERMASK;
        return (order >>> RuleBasedCollator.PRIMARYORDERSHIFT);
    }
    /**
     * Get the secondary order of a collation order.
     * @param order the collation order
     * @return the secondary order of a collation order.
     */
    public final static short secondaryOrder(int order)
    {
        order = order & RuleBasedCollator.SECONDARYORDERMASK;
        return ((short)(order >> RuleBasedCollator.SECONDARYORDERSHIFT));
    }
    /**
     * Get the tertiary order of a collation order.
     * @param order the collation order
     * @return the tertiary order of a collation order.
     */
    public final static short tertiaryOrder(int order)
    {
        return ((short)(order &= RuleBasedCollator.TERTIARYORDERMASK));
    }
    // ============================================================
    // package private (These need to be made public for searching)
    // ============================================================
    /**
     *  Check if a comparison order is ignorable.
     *  @return true if a character is ignorable, false otherwise.
     */
    final static boolean isIgnorable(int order)
    {
        return ((primaryOrder(order) == 0) ? true : false);
    }

    /**
     *  Get the comparison order in the desired strength.  Ignore the other
     *  differences.
     *  @param order The order value
     */
    final int strengthOrder(int order)
    {
        int s = ordering.getStrength();
        if (s == Collator.PRIMARY)
        {
            order &= RuleBasedCollator.PRIMARYDIFFERENCEONLY;
        } else if (s == Collator.SECONDARY)
        {
            order &= RuleBasedCollator.SECONDARYDIFFERENCEONLY;
        }
        return order;
    }

    /**
     *  Set the current offset of the character in the processed source string.
     *  @param newOffset The new offset of the accessed character
     */
    final void setOffset(int newOffset)
    {
        if (text != null)
            text.setOffset(newOffset);
    }

    /**
     *  Get the current offset of the character in the processed source string.
     *  @return The offset of the accessed character
     */
    final int getOffset()
    {
        if (text != null)
            return text.getOffset();
        return 0;
    }
    /**
     * Set the iterator string.
     * @param source the new string.
     */
    void setText(String source)
    {
        buffer = null;
        swapOrder = 0;
        if (text == null) {
            text = new DecompositionIterator(source, ordering.getDecomposition());
        } else {
            text.setDecomposition(ordering.getDecomposition());
            text.setText(source);
        }
    }

    //============================================================
    // privates
    //============================================================
    private int getEntry(Vector list, String name) {
        for (int i = 0; i < list.size(); i++) {
            EntryPair pair = (EntryPair)list.elementAt(i);
            if (pair.entryName.equals(name)) {
                return i;
            }
        }
        return RuleBasedCollator.UNMAPPED;
    }

    /**
     * Get the ordering priority of the next contracting character in the
     * string.
     * @param ch the starting character of a contracting character token
     * @return the next contracting character's ordering.  Returns NULLORDER
     * if the end of string is reached.
     */
    private int nextContractChar(char ch)
    {
        EntryPair pair = null;
        Vector list = ordering.getContractValues(ch);
        int intValue = 0;
        key.setLength(0);
        key.append(ch);
        int n = 0;
        if ((n = getEntry(list, key.toString())) != RuleBasedCollator.UNMAPPED) {
            pair = (EntryPair)list.elementAt(n);
            intValue = pair.value;
        }
        while((ch = text.next()) != DecompositionIterator.NULLORDER)
        {
            key.append(ch);
            if ((n = getEntry(list, key.toString())) ==
                     RuleBasedCollator.UNMAPPED) {
                ch = text.previous();
                break;
            }
            pair = (EntryPair)list.elementAt(n);
            intValue = pair.value;
        }
        // if contracting char is also an expanding char
        if ((intValue  - RuleBasedCollator.EXPANDCHARINDEX) >= 0)
        {
            buffer = ordering.getExpandValueList
                     (intValue - RuleBasedCollator.EXPANDCHARINDEX);
            expIndex = 0;
            intValue = buffer[expIndex++];
        }
        return intValue;
    }

    final static int UNMAPPEDCHARVALUE = 0x7FFF0000;

    private DecompositionIterator text = null;
    private int[] buffer = null;
    private int expIndex = 0;
    private StringBuffer key = new StringBuffer(5);
    private int swapOrder = 0;
    private RuleBasedCollator ordering;
}
