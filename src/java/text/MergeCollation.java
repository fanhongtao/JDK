/*
 * @(#)MergeCollation.java	1.13 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)MergeCollation.java	1.13 01/11/29
 *
 * (C) Copyright Taligent, Inc. 1996, 1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996, 1997 - All Rights Reserved
 *
 * Portions copyright (c) 1996-1998 Sun Microsystems, Inc. All Rights Reserved.
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

import java.util.ArrayList;

/**
 * Utility class for normalizing and merging patterns for collation.
 * Patterns are strings of the form <entry>*, where <entry> has the
 * form:
 * <pattern> := <entry>*
 * <entry> := <separator><chars>{"/"<extension>}
 * <separator> := "=", ",", ";", "<", "&"
 * <chars>, and <extension> are both arbitrary strings.
 * unquoted whitespaces are ignored.
 * 'xxx' can be used to quote characters
 * One difference from Collator is that & is used to reset to a current
 * point. Or, in other words, it introduces a new sequence which is to
 * be added to the old.
 * That is: "a < b < c < d" is the same as "a < b & b < c & c < d" OR
 * "a < b < d & b < c"
 * XXX: make '' be a single quote.
 * @see PatternEntry
 * @version    1.13 11/29/01
 * @author             Mark Davis, Helena Shih
 */

final class MergeCollation {

    /**
     * Creates from a pattern
     * @exception ParseException If the input pattern is incorrect.
     */
    public MergeCollation(String pattern) throws ParseException
    {
        for (int i = 0; i < statusArray.length; i++)
            statusArray[i] = 0;
        setPattern(pattern);
    }

    /**
     * recovers current pattern
     */
    public String getPattern() {
        return getPattern(true);
    }

    /**
     * recovers current pattern.
     * @param withWhiteSpace puts spacing around the entries, and \n
     * before & and <
     */
    public String getPattern(boolean withWhiteSpace) {
        StringBuffer result = new StringBuffer();
        PatternEntry tmp = null;
        ArrayList extList = null;
        int i;
        for (i = 0; i < patterns.size(); ++i) {
            PatternEntry entry = (PatternEntry) patterns.get(i);
            if (entry.extension.length() != 0) {
                if (extList == null)
                    extList = new ArrayList();
                extList.add(entry);
            } else {
                if (extList != null) {
                    PatternEntry last = findLastWithNoExtension(i-1);
                    for (int j = extList.size() - 1; j >= 0 ; j--) {
                        tmp = (PatternEntry)(extList.get(j));
                        tmp.addToBuffer(result, false, withWhiteSpace, last);
                    }
                    extList = null;
                }
                entry.addToBuffer(result, false, withWhiteSpace, null);
            }
        }
        if (extList != null) {
            PatternEntry last = findLastWithNoExtension(i-1);
            for (int j = extList.size() - 1; j >= 0 ; j--) {
                tmp = (PatternEntry)(extList.get(j));
                tmp.addToBuffer(result, false, withWhiteSpace, last);
            }
            extList = null;
        }
        return result.toString();
    }

    private final PatternEntry findLastWithNoExtension(int i) {
        for (--i;i >= 0; --i) {
            PatternEntry entry = (PatternEntry) patterns.get(i);
            if (entry.extension.length() == 0) {
                return entry;
            }
        }
        return null;
    }

    /**
     * emits the pattern for collation builder.
     * @return emits the string in the format understable to the collation
     * builder.
     */
    public String emitPattern() {
        return emitPattern(true);
    }

    /**
     * emits the pattern for collation builder.
     * @param withWhiteSpace puts spacing around the entries, and \n
     * before & and <
     * @return emits the string in the format understable to the collation
     * builder.
     */
    public String emitPattern(boolean withWhiteSpace) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < patterns.size(); ++i)
        {
            PatternEntry entry = (PatternEntry) patterns.get(i);
            if (entry != null) {
                entry.addToBuffer(result, true, withWhiteSpace, null);
            }
        }
        return result.toString();
    }

    /**
     * sets the pattern.
     */
    public void setPattern(String pattern) throws ParseException
    {
        patterns.clear();
        addPattern(pattern);
    }

    /**
     * adds a pattern to the current one.
     * @param pattern the new pattern to be added
     */
    public void addPattern(String pattern) throws ParseException
    {
        if (pattern == null)
            return;
        
        PatternEntry.Parser parser = new PatternEntry.Parser(pattern);
        
        PatternEntry entry = parser.next();
        while (entry != null) {
            fixEntry(entry);
            entry = parser.next();
        }
    }

    /**
     * gets count of separate entries
     * @return the size of pattern entries
     */
    public int getCount() {
        return patterns.size();
    }

    /**
     * gets count of separate entries
     * @param index the offset of the desired pattern entry
     * @return the requested pattern entry
     */
    public PatternEntry getItemAt(int index) {
        return (PatternEntry) patterns.get(index);
    }

    //============================================================
    // privates
    //============================================================
    ArrayList patterns = new ArrayList(); // a list of PatternEntries

    private transient PatternEntry saveEntry = null;
    private transient PatternEntry lastEntry = null;
    
    // This is really used as a local variable inside fixEntry, but we cache
    // it here to avoid newing it up every time the method is called.
    private transient StringBuffer excess = new StringBuffer();

    //
    // When building a MergeCollation, we need to do lots of searches to see
    // whether a given entry is already in the table.  Since we're using an
    // array, this would make the algorithm O(N*N).  To speed things up, we
    // use this bit array to remember whether the array contains any entries
    // starting with each Unicode character.  If not, we can avoid the search.
    // Using BitSet would make this easier, but it's significantly slower.
    //
    private transient byte[] statusArray = new byte[8192];
    private final byte BITARRAYMASK = (byte)0x1;
    private final int  BYTEPOWER = 3;
    private final int  BYTEMASK = (1 << BYTEPOWER) - 1;

    /*
      If the strength is RESET, then just change the lastEntry to
      be the current. (If the current is not in patterns, signal an error).
      If not, then remove the current entry, and add it after lastEntry
      (which is usually at the end).
      */
    private final void fixEntry(PatternEntry newEntry) throws ParseException
    {
        boolean changeLastEntry = true;
        if (newEntry.strength != PatternEntry.RESET) {
            int oldIndex = -1;

            if ((newEntry.chars.length() == 1)) {
            
                char c = newEntry.chars.charAt(0);
                int statusIndex = c >> BYTEPOWER;
                byte bitClump = statusArray[statusIndex];
                byte setBit = (byte)(BITARRAYMASK << (c & BYTEMASK));
                
                if (bitClump != 0 && (bitClump & setBit) != 0) {
                    oldIndex = patterns.lastIndexOf(newEntry);
                } else {
                    // We're going to add an element that starts with this
                    // character, so go ahead and set its bit.
                    statusArray[statusIndex] = (byte)(bitClump | setBit);
                }
            } else {
                oldIndex = patterns.lastIndexOf(newEntry);
            }
            if (oldIndex != -1) {
                patterns.remove(oldIndex);
            }
            
            excess.setLength(0);
            int lastIndex = findLastEntry(lastEntry, excess);

            if (excess.length() != 0) {
                newEntry.extension = excess + newEntry.extension;
                if (lastIndex != patterns.size()) {
                    lastEntry = saveEntry;
                    changeLastEntry = false;
                }
            }
            if (lastIndex == patterns.size()) {
                patterns.add(newEntry);
                saveEntry = newEntry;
            } else {
                patterns.add(lastIndex, newEntry);
            }
        }
        if (changeLastEntry) {
            lastEntry = newEntry;
        }
    }

    private final int findLastEntry(PatternEntry entry,
                              StringBuffer excessChars) throws ParseException
    {
        if (entry == null)
            return 0;
            
        if (entry.strength != PatternEntry.RESET) {
            // Search backwards for string that contains this one;
            // most likely entry is last one
            
            int oldIndex = -1;
            if ((entry.chars.length() == 1)) {
                int index = entry.chars.charAt(0) >> BYTEPOWER;
                if ((statusArray[index] &
                    (BITARRAYMASK << (entry.chars.charAt(0) & BYTEMASK))) != 0) {
                    oldIndex = patterns.lastIndexOf(entry);
                }
            } else {
                oldIndex = patterns.lastIndexOf(entry);
            }
            if ((oldIndex == -1))
                throw new ParseException("couldn't find last entry: "
                                          + entry, oldIndex);
            return oldIndex + 1;
        } else {
            int i;
            for (i = patterns.size() - 1; i >= 0; --i) {
                PatternEntry e = (PatternEntry) patterns.get(i);
                if (e.chars.regionMatches(0,entry.chars,0,
                                              e.chars.length())) {
                    excessChars.append(entry.chars.substring(e.chars.length(),
                                                            entry.chars.length()));
                    break;
                }
            }
            if (i == -1)
                throw new ParseException("couldn't find: " + entry, i);
            return i + 1;
        }
    }
}

