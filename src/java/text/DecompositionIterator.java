/*
 * @(#)DecompositionIterator.java	1.13 97/01/20
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
  * Contains statics for decomposing strings.
  * Eventually ought to be in either Char or String.  This class is only
  * for internal use for now.
  * <p>
  * Unicode defines a set of non-spacing marks that can be combined with
  * base characters to form a single "conceptual" character.  Often these
  * combinations have pre-composed equivalents elsewhere in Unicode.  For
  * operations such as locale-sensitive text comparison, it is necessary to
  * be able to decompose a Unicode character into an equivalent string.
  * The composition operation is also necessary.  It is proposed that at
  * least the decompose function be added as a static method on String, or
  * StringBuffer or Character.
  * @see        Collator
  * @see        RuleBasedCollator
  * @version    1.13 01/20/97
  * @author     Mark Davis, Helena Shih
*/
class DecompositionIterator {
    /**
     * Null order which indicates the end of string is reached by the
     * cursor.
     */
    public final static char NULLORDER = 0xffff;
    /**
     * Create a decomposed string iterator.
     */
    public DecompositionIterator(String src, int start, int end, int mode) {
        str = src;
        parsedStr = new StringBuffer(src.length());
        sIndex = start;
        pIndex = 0;
        getBeginIndex = start;
        getEndIndex = end;
        decmpMode = mode;
    }
    /**
     * Get the next character.
     */
    public char next() {
        char ch;
        if (decmpMode == Collator.NO_DECOMPOSITION) {
            if (sIndex >= str.length())
                return NULLORDER;
            return str.charAt(sIndex++);
        }
        if (pIndex >= parsedStr.length()) {
            parsedStr.setLength(0);
            pIndex = 0;
            uptoOffset = sIndex;
            if (sIndex >= getEndIndex) {
                return NULLORDER;
            }
            ch = str.charAt(uptoOffset++);
            parsedStr.append(ch);
            while (uptoOffset < getEndIndex) {
                ch = str.charAt(uptoOffset);
                if (((((1 << Character.NON_SPACING_MARK) |
                       (1 << Character.ENCLOSING_MARK) |
                       (1 << Character.COMBINING_SPACING_MARK)) >>
                     Character.getType(ch)) & 1) != 0) {
                    break;
                }
                parsedStr.append(ch);
                uptoOffset++;
            }
            decompose(parsedStr, decmpMode);
            fixCanonical(parsedStr);
            uptoOffset -= sIndex;
            sIndex += uptoOffset;
        }
        return parsedStr.charAt(pIndex++);
    }
    public char previous() {
        if (decmpMode == Collator.NO_DECOMPOSITION) {
            if (sIndex <= getBeginIndex)
                return NULLORDER;
            return str.charAt(--sIndex);
        }
        if (pIndex <= parsedStr.length()) {
            if (pIndex > 1) {
                pIndex--;
                return parsedStr.charAt(pIndex);
            } else {
                parsedStr.setLength(0);
                pIndex = 0;
                sIndex -= uptoOffset;
            }
        }
        if (sIndex < str.length())
            return str.charAt(sIndex);
        else
            return NULLORDER;
    }
    /**
     * Resets the cursor to the beginning of the string.
     */
    public void reset() {
        sIndex = getBeginIndex;
        pIndex = 0;
    }

    // -------------------------------------------------------------
    // private
    // -------------------------------------------------------------
    /**
     * Decomposes a character into a string
     * If the source can't be decomposed, return "".
     * @param source the character to be decomposed with
     * @param mode the decomposition mode
     * @return the decomposed string
     * @see java.text.Collator
     * @see java.text.Collator#getDecomposition
     * @see java.text.Collator#setDecomposition
     */
    static String decompose(char source, int mode) {
        if (mode == Collator.NO_DECOMPOSITION) {
            StringBuffer tmp = new StringBuffer();
            tmp.append(source);
            return tmp.toString();
        }
        int limit = (mode == Collator.CANONICAL_DECOMPOSITION) ?
            maximumCanonical : SHORT_MAX_VALUE;
        int index = startOffsets.elementAt(source);
        if (index >= limit) return "";
        StringBuffer result = new StringBuffer();
        while (true) {
            char ch = contents.charAt(index++);
            if (ch == '\u0000') break;
            result.append(ch);
        }
        return result.toString();
    }
    static void decompose(StringBuffer source, int mode) {
        decompose(source,0,source.length(),mode);
    }

    static void decompose(StringBuffer source, int start, int end,
                                   int mode)
    {
        if (mode == Collator.NO_DECOMPOSITION) {
            String temp = source.toString().substring(start, end);
            source.setLength(0);
            source.append(temp);
        }
        int limit = (mode == Collator.CANONICAL_DECOMPOSITION) ?
            maximumCanonical : SHORT_MAX_VALUE;
        StringBuffer result = new StringBuffer();
        for (int i = start; i < end; ++i) {
            char ch = source.charAt(i);
            int index = startOffsets.elementAt(ch);
            if (index >= limit) result.append(ch);
            else while (true) {
                ch = contents.charAt(index++);
                if (ch == '\u0000') break;
                result.append(ch);
            }
        }
        source.setLength(0);
        source.append(result.toString());
    }
    /**
     * Decomposes string into string
     * If the source can't be decomposed, return "".
     * @param source the string to be decomposed with
     * @param mode the decomposition mode
     * @return the decomposed string
     * @see java.text.Collator
     * @see java.text.Collator#getDecomposition
     * @see java.text.Collator#setDecomposition
     */
    static String decompose(String source, int mode) {
        return decompose(source,0,source.length(),mode);
    }
    /**
     * Decomposes a range of string into string
     * If the source can't be decomposed, return "".
     * @param source the string to be decomposed with
     * @param start the start offset of the text range
     * @param end the end offset of the text range
     * @param mode the decomposition mode
     * @return the decomposed string
     * @see java.text.Collator
     * @see java.text.Collator#getDecomposition
     * @see java.text.Collator#setDecomposition
     */
    static String decompose(String source, int start, int end,
                                   int mode)
    {
        if (mode == Collator.NO_DECOMPOSITION) {
            return source.substring(start, end);
        }
        int limit = (mode == Collator.CANONICAL_DECOMPOSITION) ?
            maximumCanonical : SHORT_MAX_VALUE;
        StringBuffer result = new StringBuffer();
        for (int i = start; i < end; ++i) {
            char ch = source.charAt(i);
            int index = startOffsets.elementAt(ch);
            if (index >= limit) result.append(ch);
            else while (true) {
                ch = contents.charAt(index++);
                if (ch == '\u0000') break;
                result.append(ch);
            }
        }
        return result.toString();
    }
    /**
     * Use for fast access in a tight loop.
     * Puts count characters into output buffer, with no heap allocations.
     * If the source can't be decomposed, returns length == 0;
     * Make sure that you allocate an array of getMaximumDecomposition()
     * members.
     * @param source the character to be decomposed with
     * @param output the character array to store the deomposed result string
     * @param mode the decomposition mode
     * @return the number of character of decomposed source string
     * @see java.text.Collator
     * @see java.text.Collator#getDecomposition
     * @see java.text.Collator#setDecomposition
     */
    static int decompose(char source,
                         char[] output,
                         int mode)
    {
        if (mode == Collator.NO_DECOMPOSITION) {
            output[0] = source;
            output[1] = 0;
            return 1;
        }
        int limit = (mode == Collator.CANONICAL_DECOMPOSITION) ?
            maximumCanonical : SHORT_MAX_VALUE;
        int index = startOffsets.elementAt(source);
        if (index >= limit) return 0;
        int i = 0;
        while (true) {
            char ch = contents.charAt(index++);
            if (ch == '\u0000') break;
            output[i++] = ch;
        }
        return i;
    }
    /**
     * Use to allocate array for using decompose in a tight loop.
     * @return the maximum decomposition result characters for output
     */
    static int getMaximumDecomposition() {
        return maximumDecomposition;
    }

    //-----------------------------------------------------------
    // privates
    //-----------------------------------------------------------
    //should be in Short class
    private static final short SHORT_MAX_VALUE = 32767;
    private static final char STERMINATOR = (char)0x0000;

    // GENERATED
    // Only making short array!!!
    private static final short kOffsetIndex[] = {
    (short)0, (short)128, (short)256, (short)384, (short)512,
    (short)640,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)896,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768, (short)768, (short)768, (short)768, (short)768,
    (short)768 };

    private static final short kOffsetValues[] =    {
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)736, (short)739,
    (short)742, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)745,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)747, (short)32767, (short)750,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)752,
    (short)32767, (short)32767, (short)755, (short)757, (short)759,
    (short)762, (short)32767, (short)32767, (short)764, (short)767,
    (short)769, (short)32767, (short)771, (short)775, (short)779,
    (short)32767, (short)0, (short)3, (short)6, (short)9,
    (short)12, (short)15, (short)32767, (short)18, (short)24,
    (short)27, (short)30, (short)33, (short)39, (short)42,
    (short)45, (short)48, (short)32767, (short)54, (short)57,
    (short)60, (short)63, (short)66, (short)69, (short)32767,
    (short)72, (short)78, (short)81, (short)84, (short)87,
    (short)90, (short)32767, (short)32767, (short)93, (short)96,
    (short)99, (short)102, (short)105, (short)108, (short)32767,
    (short)111, (short)117, (short)120, (short)123, (short)126,
    (short)132, (short)135, (short)138, (short)141, (short)32767,
    (short)147, (short)150, (short)153, (short)156, (short)159,
    (short)162, (short)32767, (short)165, (short)171, (short)174,
    (short)177, (short)180, (short)183, (short)32767, (short)186,
    (short)189, (short)192, (short)195, (short)198, (short)201,
    (short)204, (short)207, (short)210, (short)213, (short)216,
    (short)219, (short)222, (short)225, (short)228, (short)231,
    (short)234, (short)21, (short)114, (short)237, (short)240,
    (short)243, (short)246, (short)249, (short)252, (short)255,
    (short)258, (short)261, (short)264, (short)267, (short)270,
    (short)273, (short)276, (short)279, (short)282, (short)285,
    (short)288, (short)291, (short)294, (short)36, (short)129,
    (short)297, (short)300, (short)303, (short)306, (short)309,
    (short)312, (short)315, (short)318, (short)321, (short)32767,
    (short)783, (short)786, (short)324, (short)327, (short)330,
    (short)333, (short)32767, (short)336, (short)339, (short)342,
    (short)345, (short)348, (short)351, (short)789, (short)792,
    (short)51, (short)144, (short)354, (short)357, (short)360,
    (short)363, (short)366, (short)369, (short)795, (short)32767,
    (short)32767, (short)372, (short)375, (short)378, (short)381,
    (short)384, (short)387, (short)32767, (short)32767, (short)390,
    (short)393, (short)396, (short)399, (short)402, (short)405,
    (short)408, (short)411, (short)414, (short)417, (short)420,
    (short)423, (short)426, (short)429, (short)432, (short)435,
    (short)438, (short)441, (short)75, (short)168, (short)444,
    (short)447, (short)450, (short)453, (short)456, (short)459,
    (short)462, (short)465, (short)468, (short)471, (short)474,
    (short)477, (short)480, (short)483, (short)486, (short)489,
    (short)492, (short)495, (short)498, (short)501, (short)504,
    (short)507, (short)510, (short)798, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)513, (short)516, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)519, (short)522, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)800, (short)804, (short)808, (short)812,
    (short)815, (short)818, (short)821, (short)824, (short)827,
    (short)525, (short)528, (short)531, (short)534, (short)537,
    (short)540, (short)543, (short)546, (short)549, (short)553,
    (short)557, (short)561, (short)565, (short)569, (short)573,
    (short)577, (short)32767, (short)581, (short)585, (short)589,
    (short)593, (short)597, (short)600, (short)32767, (short)32767,
    (short)603, (short)606, (short)609, (short)612, (short)615,
    (short)618, (short)621, (short)625, (short)629, (short)632,
    (short)635, (short)830, (short)833, (short)836, (short)638,
    (short)641, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)644, (short)648, (short)652, (short)655, (short)658,
    (short)661, (short)664, (short)667, (short)670, (short)673,
    (short)676, (short)679, (short)682, (short)685, (short)688,
    (short)691, (short)694, (short)697, (short)700, (short)703,
    (short)706, (short)709, (short)712, (short)715, (short)718,
    (short)721, (short)724, (short)727, (short)730, (short)733,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)839, (short)841, (short)843,
    (short)845, (short)847, (short)849, (short)851, (short)853,
    (short)855, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)857, (short)860, (short)863,
    (short)866, (short)869, (short)872, (short)32767, (short)32767,
    (short)875, (short)877, (short)879, (short)881, (short)883,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)885, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767, (short)32767, (short)32767,
    (short)32767, (short)32767, (short)32767 };

    private static final short kCanonicalIndex[] = {
    (short)0, (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)128, (short)0, (short)0, (short)256, (short)0,
    (short)384, (short)512, (short)640, (short)0, (short)0,
    (short)0, (short)0, (short)768, (short)896, (short)1024,
    (short)1152, (short)1280, (short)1408, (short)1536, (short)1664,
    (short)1792, (short)0, (short)1920, (short)2048, (short)2176,
    (short)2304, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)2432,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)2560, (short)2688, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)0, (short)0, (short)0,
    (short)0, (short)2816, (short)0, (short)0, (short)0,
    (short)0, (short)0, (short)2944, (short)0, (short)0,
    (short)0 };

    private static final byte kCanonicalValues[] =
    {
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)102, (byte)102, (byte)102,
    (byte)102, (byte)102, (byte)102, (byte)102, (byte)102,
    (byte)102, (byte)102, (byte)102, (byte)102, (byte)102,
    (byte)102, (byte)102, (byte)102, (byte)102, (byte)102,
    (byte)102, (byte)102, (byte)102, (byte)104, (byte)92,
    (byte)92, (byte)92, (byte)92, (byte)104, (byte)88,
    (byte)92, (byte)92, (byte)92, (byte)92, (byte)92,
    (byte)74, (byte)74, (byte)92, (byte)92, (byte)92,
    (byte)92, (byte)74, (byte)74, (byte)92, (byte)92,
    (byte)92, (byte)92, (byte)92, (byte)92, (byte)92,
    (byte)92, (byte)92, (byte)92, (byte)92, (byte)-127,
    (byte)-127, (byte)-127, (byte)-127, (byte)-127, (byte)92,
    (byte)92, (byte)92, (byte)92, (byte)102, (byte)102,
    (byte)102, (byte)102, (byte)102, (byte)102, (byte)102,
    (byte)102, (byte)92, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)106, (byte)106,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)102, (byte)102,
    (byte)102, (byte)102, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)92, (byte)102, (byte)102, (byte)102, (byte)102,
    (byte)92, (byte)102, (byte)102, (byte)102, (byte)94,
    (byte)92, (byte)102, (byte)102, (byte)102, (byte)102,
    (byte)102, (byte)102, (byte)-128, (byte)92, (byte)92,
    (byte)92, (byte)92, (byte)92, (byte)102, (byte)102,
    (byte)92, (byte)102, (byte)102, (byte)94, (byte)102,
    (byte)102, (byte)-118, (byte)-117, (byte)-116, (byte)-115,
    (byte)-114, (byte)-113, (byte)-112, (byte)-111, (byte)-110,
    (byte)-109, (byte)-128, (byte)-108, (byte)-107, (byte)-106,
    (byte)-128, (byte)-105, (byte)-128, (byte)-104, (byte)-103,
    (byte)-128, (byte)102, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-101, (byte)-100, (byte)-99, (byte)-98,
    (byte)-97, (byte)-96, (byte)-95, (byte)-94, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-93, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)102, (byte)102, (byte)102, (byte)102, (byte)102,
    (byte)102, (byte)102, (byte)-128, (byte)-128, (byte)102,
    (byte)102, (byte)102, (byte)102, (byte)92, (byte)102,
    (byte)-128, (byte)-128, (byte)102, (byte)102, (byte)-128,
    (byte)92, (byte)102, (byte)102, (byte)92, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-91, (byte)-92,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-121, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-90, (byte)-89, (byte)-88,
    (byte)-87, (byte)-86, (byte)-85, (byte)-84, (byte)-83,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-119,
    (byte)-128, (byte)-128, (byte)-128, (byte)-82, (byte)-81,
    (byte)102, (byte)102, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-80, (byte)-79, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-78, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-121, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-77, (byte)-76, (byte)-75, (byte)-74, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-119, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-73, (byte)-72,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-71, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-121, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-70, (byte)-69,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-68,
    (byte)-67, (byte)-128, (byte)-128, (byte)-66, (byte)-65,
    (byte)-119, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-64, (byte)-63, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-61, (byte)-62, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-121, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-60, (byte)-59, (byte)-58, (byte)-57,
    (byte)-56, (byte)-128, (byte)-55, (byte)-54, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-119, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-53, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-121,
    (byte)-128, (byte)-128, (byte)-52, (byte)-128, (byte)-51,
    (byte)-50, (byte)-49, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-119, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)102, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-48, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-119,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-47, (byte)-46, (byte)-45,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-44, (byte)-43, (byte)-42, (byte)-128, (byte)-41,
    (byte)-40, (byte)-39, (byte)-119, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-38, (byte)-37, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-36, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-35, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-34,
    (byte)-119, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-33, (byte)-32, (byte)-31, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-119, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-30, (byte)-128,
    (byte)-128, (byte)-29, (byte)-28, (byte)-27, (byte)-26,
    (byte)-25, (byte)-24, (byte)-23, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-22, (byte)-21, (byte)-20, (byte)-19, (byte)-18,
    (byte)-17, (byte)-16, (byte)0, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-15, (byte)-128, (byte)-128, (byte)-14,
    (byte)-13, (byte)-12, (byte)-11, (byte)-10, (byte)-9,
    (byte)-128, (byte)-8, (byte)-7, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-6,
    (byte)-5, (byte)-4, (byte)-3, (byte)-2, (byte)-1,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)92,
    (byte)92, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)102, (byte)-128,
    (byte)102, (byte)-128, (byte)88, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)92, (byte)92, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)1, (byte)2,
    (byte)3, (byte)4, (byte)5, (byte)6, (byte)7,
    (byte)8, (byte)9, (byte)10, (byte)11, (byte)12,
    (byte)13, (byte)14, (byte)-128, (byte)15, (byte)16,
    (byte)102, (byte)102, (byte)-119, (byte)-128, (byte)102,
    (byte)102, (byte)102, (byte)102, (byte)102, (byte)102,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-122,
    (byte)-122, (byte)-122, (byte)-122, (byte)-122, (byte)-122,
    (byte)-128, (byte)-122, (byte)-128, (byte)-122, (byte)-122,
    (byte)-122, (byte)-122, (byte)-122, (byte)-122, (byte)-122,
    (byte)-122, (byte)-122, (byte)-122, (byte)-122, (byte)-122,
    (byte)-122, (byte)-122, (byte)-122, (byte)-122, (byte)-122,
    (byte)-122, (byte)-122, (byte)-122, (byte)-122, (byte)-128,
    (byte)-128, (byte)-128, (byte)-122, (byte)-122, (byte)-122,
    (byte)-122, (byte)-122, (byte)-122, (byte)-122, (byte)-128,
    (byte)-122, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)102, (byte)102, (byte)-127, (byte)-127,
    (byte)102, (byte)102, (byte)102, (byte)102, (byte)-127,
    (byte)-127, (byte)-127, (byte)102, (byte)102, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-127, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)90, (byte)100, (byte)104, (byte)94,
    (byte)96, (byte)96, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-120, (byte)-120, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-102, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)102, (byte)102, (byte)102, (byte)102, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128, (byte)-128, (byte)-128, (byte)-128, (byte)-128,
    (byte)-128 };

    private static String contents =
    "A\u0300\u0000A\u0301\u0000A\u0302\u0000A\u0303\u0000A\u0308\u0000"+
    "A\u030A\u0000C\u0327\u0000D\u0335\u0000E\u0300\u0000E\u0301\u0000"+
    "E\u0302\u0000E\u0308\u0000H\u0335\u0000I\u0300\u0000I\u0301\u0000"+
    "I\u0302\u0000I\u0308\u0000L\u0337\u0000N\u0303\u0000O\u0300\u0000"+
    "O\u0301\u0000O\u0302\u0000O\u0303\u0000O\u0308\u0000O\u0338\u0000"+
    "T\u0335\u0000U\u0300\u0000U\u0301\u0000U\u0302\u0000U\u0308\u0000"+
    "Y\u0301\u0000a\u0300\u0000a\u0301\u0000a\u0302\u0000a\u0303\u0000"+
    "a\u0308\u0000a\u030A\u0000c\u0327\u0000d\u0335\u0000e\u0300\u0000"+
    "e\u0301\u0000e\u0302\u0000e\u0308\u0000h\u0335\u0000i\u0300\u0000"+
    "i\u0301\u0000i\u0302\u0000i\u0308\u0000l\u0337\u0000n\u0303\u0000"+
    "o\u0300\u0000o\u0301\u0000o\u0302\u0000o\u0303\u0000o\u0308\u0000"+
    "o\u0338\u0000t\u0335\u0000u\u0300\u0000u\u0301\u0000u\u0302\u0000"+
    "u\u0308\u0000y\u0301\u0000y\u0308\u0000A\u0304\u0000a\u0304\u0000"+
    "A\u0306\u0000a\u0306\u0000A\u0328\u0000a\u0328\u0000C\u0301\u0000"+
    "c\u0301\u0000C\u0302\u0000c\u0302\u0000C\u0307\u0000c\u0307\u0000"+
    "C\u030C\u0000c\u030C\u0000D\u030C\u0000d\u030C\u0000E\u0304\u0000"+
    "e\u0304\u0000E\u0306\u0000e\u0306\u0000E\u0307\u0000e\u0307\u0000"+
    "E\u0328\u0000e\u0328\u0000E\u030C\u0000e\u030C\u0000G\u0302\u0000"+
    "g\u0302\u0000G\u0306\u0000g\u0306\u0000G\u0307\u0000g\u0307\u0000"+
    "G\u0327\u0000g\u0327\u0000H\u0302\u0000h\u0302\u0000I\u0303\u0000"+
    "i\u0303\u0000I\u0304\u0000i\u0304\u0000I\u0306\u0000i\u0306\u0000"+
    "I\u0328\u0000i\u0328\u0000I\u0307\u0000J\u0302\u0000j\u0302\u0000"+
    "K\u0327\u0000k\u0327\u0000L\u0301\u0000l\u0301\u0000L\u0327\u0000"+
    "l\u0327\u0000L\u030C\u0000l\u030C\u0000N\u0301\u0000n\u0301\u0000"+
    "N\u0327\u0000n\u0327\u0000N\u030C\u0000n\u030C\u0000O\u0304\u0000"+
    "o\u0304\u0000O\u0306\u0000o\u0306\u0000O\u030B\u0000o\u030B\u0000"+
    "R\u0301\u0000r\u0301\u0000R\u0327\u0000r\u0327\u0000R\u030C\u0000"+
    "r\u030C\u0000S\u0301\u0000s\u0301\u0000S\u0302\u0000s\u0302\u0000"+
    "S\u0327\u0000s\u0327\u0000S\u030C\u0000s\u030C\u0000T\u0327\u0000"+
    "t\u0327\u0000T\u030C\u0000t\u030C\u0000U\u0303\u0000u\u0303\u0000"+
    "U\u0304\u0000u\u0304\u0000U\u0306\u0000u\u0306\u0000U\u030A\u0000"+
    "u\u030A\u0000U\u030B\u0000u\u030B\u0000U\u0328\u0000u\u0328\u0000"+
    "W\u0302\u0000w\u0302\u0000Y\u0302\u0000y\u0302\u0000Y\u0308\u0000"+
    "Z\u0301\u0000z\u0301\u0000Z\u0307\u0000z\u0307\u0000Z\u030C\u0000"+
    "z\u030C\u0000O\u031B\u0000o\u031B\u0000U\u031B\u0000u\u031B\u0000"+
    "A\u030C\u0000a\u030C\u0000I\u030C\u0000i\u030C\u0000O\u030C\u0000"+
    "o\u030C\u0000U\u030C\u0000u\u030C\u0000U\u0308\u0304\u0000u\u0308"+
    "\u0304\u0000U\u0308\u0301\u0000u\u0308\u0301\u0000U\u0308\u030C"+
    "\u0000u\u0308\u030C\u0000U\u0308\u0300\u0000u\u0308\u0300\u0000"+
    "A\u0308\u0304\u0000a\u0308\u0304\u0000A\u0307\u0304\u0000a\u0307"+
    "\u0304\u0000Æ\u0304\u0000æ\u0304\u0000G\u030C\u0000g\u030C\u0000"+
    "K\u030C\u0000k\u030C\u0000O\u0328\u0000o\u0328\u0000O\u0328\u0304"+
    "\u0000o\u0328\u0304\u0000\u01B7\u030C\u0000\u0292\u030C\u0000"+
    "j\u030C\u0000G\u0301\u0000g\u0301\u0000A\u030A\u0301\u0000a\u030A"+
    "\u0301\u0000Æ\u0301\u0000æ\u0301\u0000Ø\u0301\u0000ø\u0301\u0000"+
    "A\u030F\u0000a\u030F\u0000A\u0311\u0000a\u0311\u0000E\u030F\u0000"+
    "e\u030F\u0000E\u0311\u0000e\u0311\u0000I\u030F\u0000i\u030F\u0000"+
    "I\u0311\u0000i\u0311\u0000O\u030F\u0000o\u030F\u0000O\u0311\u0000"+
    "o\u0311\u0000R\u030F\u0000r\u030F\u0000R\u0311\u0000r\u0311\u0000"+
    "U\u030F\u0000u\u030F\u0000U\u0311\u0000u\u0311\u0000 \u0302\u0000"+
    " \u0332\u0000 \u0300\u0000 \u0000 \u0308\u0000a\u0000 \u0304\u0000"+
    "2\u00003\u0000 \u0301\u0000\u03BC\u0000 \u0327\u00001\u0000o\u0000"+
    "1\u20444\u00001\u20442\u00003\u20444\u0000IJ\u0000ij\u0000L·\u0000"+
    "l·\u0000\u02BCn\u0000s\u0000DZ\u030C\u0000Dz\u030C\u0000dz\u030C"+
    "\u0000LJ\u0000Lj\u0000lj\u0000NJ\u0000Nj\u0000nj\u0000DZ\u0000"+
    "Dz\u0000dz\u0000h\u0000\u0266\u0000j\u0000r\u0000\u0279\u0000"+
    "\u027B\u0000\u0281\u0000w\u0000y\u0000 \u0306\u0000 \u0307\u0000"+
    " \u030A\u0000 \u0328\u0000 \u0303\u0000 \u030B\u0000\u0263\u0000"+
    "l\u0000s\u0000x\u0000\u0295\u0000Fr\u0000";

private static int maximumDecomposition = 888;
private static int maximumCanonical = 736;
    private StringBuffer parsedStr = null;
    private String str = null;
    private int pIndex = 0;
    private int sIndex = 0;
    private int getEndIndex = 0;
    private int decmpMode = 0;
    private int getBeginIndex = 0;
    private int uptoOffset = 0;

    // END OF GENERATED CODE
    private static final CompactShortArray startOffsets
        = new CompactShortArray(kOffsetIndex, kOffsetValues);

    // old canonical class
    // used since canonical values are 0..255
    static final byte BASE = (byte)-128;
    private static CompactByteArray canonicals
        = new CompactByteArray(kCanonicalIndex, kCanonicalValues);
    /** Fixes canonical order.
        <BR>Optimized for the case where NO swaps are necessary.
     */
    private static void fixCanonical(StringBuffer result) {
        int i = result.length() - 1;
        byte lastType;
        byte currentType = canonicals.elementAt(result.charAt(i));
        for (--i; i >= 0; --i) {
            lastType = currentType;
            currentType = canonicals.elementAt(result.charAt(i));
            // a swap is presumed to be rare (and a double-swap very rare),
            // so don't worry about efficiency here.
            if (currentType > lastType && lastType != BASE) {
                // swap characters
                char temp = result.charAt(i);
                result.setCharAt(i, result.charAt(i+1));
                result.setCharAt(i+1, temp);
                // if not at end, backup (one further, to compensate for for-loop)
                if (i < result.length() - 2)
                    i += 2;
                // reset type, since we swapped.
                currentType = canonicals.elementAt(result.charAt(i));
            }
        }
    }
}
