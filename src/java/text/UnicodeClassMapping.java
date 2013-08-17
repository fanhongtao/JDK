/*
 * @(#)UnicodeClassMapping.java	1.5 97/03/07
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

    /**
     * Create a mapping given a mapping from categories and a list
     * of exceptions.  Both the mapping list and exceptionChars list must
     * be sorted in ascending order.
     */
    public UnicodeClassMapping(int mappedValue[],
                               SpecialMapping exceptionChars[])
    {
        this.mappedValue = mappedValue;
        this.exceptionChars = exceptionChars;
    }

    /**
     * Map a character to a stage change input for WordBreakTable
     * @param ch The character to map.
     * @return The mapped value.
     */
    public int mappedChar(char ch)
    {
        //This is a special case optimization for ASCII text.  If the
        //exception lists change to include items in this range, this
        //should be removed.
        if ((exceptionChars.length == 0) || ('\u003F' < ch && ch < '\u00A0')) {
            return mappedValue[Character.getType(ch)];
        }

        //do binary search of exceptionChars table
        int min = 0;
        int max = exceptionChars.length;
	int pos;
	while ((max - (pos = ((max - min) >> 1) + min)) > 1) {
            if (ch > exceptionChars[pos].endChar) {
                min = pos+1;
            }
            else {
                max = pos;
            }
        }
        SpecialMapping sm = exceptionChars[min];
        if (sm.startChar <= ch && ch <= sm.endChar) {
            return sm.newValue;
        }
        else if (max < exceptionChars.length) {
            sm = exceptionChars[max];
            if (sm.startChar <= ch && ch <= sm.endChar) {
                return sm.newValue;
            }
            else {
                return mappedValue[Character.getType(ch)];
            }
        }
        else {
            return mappedValue[Character.getType(ch)];
        }
    }
}

