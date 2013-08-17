/*
 * @(#)UnicodeClassMapping.java	1.7 98/01/12
 *
 * (C) Copyright Taligent, Inc. 1996-1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996-1997 - All Rights Reserved
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
/**
 * This class maps categories to state change inputs for the
 * WordBreakTable.  An entire category is mapped to the same
 * value unless the character in question appears in the exception list.
 */
final class UnicodeClassMapping
{
    private int mappedValue[];
    private SpecialMapping exceptionChars[];
    private boolean hasException[];
    private int asciiValues[];

    /**
     * Create a mapping given a mapping from categories and a list
     * of exceptions.  Both the mapping list and exceptionChars list must
     * be sorted in ascending order.
     */
    public UnicodeClassMapping(int mappedValue[],
                               SpecialMapping exceptionChars[],
                               boolean hasException[],
                               int asciiValues[])
    {
        this.mappedValue = mappedValue;
        this.exceptionChars = exceptionChars;
        this.hasException = hasException;
        this.asciiValues = asciiValues;
    }

    /**
     * Map a character to a stage change input for WordBreakTable
     * @param ch The character to map.
     * @return The mapped value.
     */
    public int mappedChar(char ch)
    {
        if (ch <= 255)
            return asciiValues[ch];

        // get an appropriate category based on the character's Unicode class
        // if there's no entry in the exception table for that Unicode class,
        // we're done; otherwise we have to look in the exception table for
        // the character's category (\uffff is treated here as a sentinel
        // value meaning "end of the string"-- we always look in the exception
        // table for its category)
        int    charType = Character.getType(ch);
        if ((exceptionChars.length == 0) //|| (ch > '\u003f' && ch < '\u00a0')
                || (!hasException[charType] && ch != '\uffff')) {
            return mappedValue[charType];
        }

        //do binary search of exceptionChars table
        int min = 0;
        int max = exceptionChars.length - 1;
        while (max > min) {
            int pos = (max + min) >> 1;
            if (ch > exceptionChars[pos].endChar)
                min = pos + 1;
            else
                max = pos;
        }
        SpecialMapping sm = exceptionChars[min];
        if (sm.startChar <= ch && ch <= sm.endChar)
            return sm.newValue;
        else
            return mappedValue[charType];
    }
}

