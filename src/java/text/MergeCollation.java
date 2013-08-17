/*
 * @(#)MergeCollation.java	1.7 97/01/20
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

import java.util.Vector;

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
 * @version    1.7 01/20/97
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
        for (int i = 0; i < patterns.size(); ++i) {
            PatternEntry entry = (PatternEntry) patterns.elementAt(i);
            if (entry.extension.length() != 0)
                entry.addToBuffer(result, false, withWhiteSpace,
                                  findLastWithNoExtension(i));
            else
                entry.addToBuffer(result, false, withWhiteSpace, null);
        }
        return result.toString();
    }

    /**
     * emits the pattern for collation builder.
     * @return emits the string in the format understable to the collation
     * builder.
     */
    public String emitPattern() {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < patterns.size(); ++i)
        {
            PatternEntry entry = (PatternEntry) patterns.elementAt(i);
            if (entry.chars.charAt(0) == '@') {
            }
            result.append(entry.toString());
        }
        return result.toString();
    }

    /**
     * sets the pattern.
     */
    public void setPattern(String pattern) throws ParseException
    {
        patterns.removeAllElements();
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
        int i = 0;
        while (true) {
            PatternEntry entry = new PatternEntry();
            i = entry.getNextEntry(pattern, i);
            if (i < 0)
                break;
            fixEntry(entry);
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
        return (PatternEntry) patterns.elementAt(index);
    }

    //============================================================
    // privates
    //============================================================
    Vector patterns = new Vector(); // a vector of PatternEntries

    PatternEntry lastEntry = null;

    private final PatternEntry findLastWithNoExtension(int i) {
        for (--i;i >= 0; --i) {
            PatternEntry entry = (PatternEntry) patterns.elementAt(i);
            if (entry.extension.length() == 0) {
                return entry;
            }
        }
        return null;
    }

    private byte[] statusArray = new byte[8192];
    private int lastIndex = -1;
    private final byte BITARRAYMASK = (byte)0x1;

    /*
      If the strength is RESET, then just change the lastEntry to
      be the current. (If the current is not in patterns, signal an error).
      If not, then remove the current entry, and add it after lastEntry
      (which is usually at the end).
      */
    private final void fixEntry(PatternEntry newEntry) throws ParseException
    {
        if (newEntry.strength != PatternEntry.RESET) {
            int oldIndex = -1;
            boolean setArray = false;
            if ((newEntry.chars.length() == 1)) {
                setArray = true;
                oldIndex = newEntry.chars.charAt(0) / 8;
                if ((statusArray[oldIndex]
                     & (byte)(BITARRAYMASK << (newEntry.chars.charAt(0) % 8))) != 0)
                {
                      setArray = false;
                      patterns.removeElement(newEntry);
                      lastIndex = patterns.indexOf(lastEntry);
                }
            }
            else {
                oldIndex = patterns.indexOf(newEntry);
                if (oldIndex != -1)
                    patterns.removeElementAt(oldIndex);
            }
            StringBuffer excess = new StringBuffer();
            lastIndex = findLastEntry(lastEntry, excess);
            if (excess.length() != 0)
                newEntry.extension = excess + newEntry.extension;
            patterns.insertElementAt(newEntry, lastIndex);  // add at end
            if (setArray) {
                statusArray[oldIndex] |=
                     (byte)((BITARRAYMASK) << (newEntry.chars.charAt(0) % 8));
            }
        } else {
            lastIndex = patterns.indexOf(newEntry);
        }
        lastEntry = newEntry;
    }

    private final int findLastEntry(PatternEntry lastEntry,
                              StringBuffer excess) throws ParseException
    {
    	if (lastEntry == null)
	    	return 0;
        if (lastEntry.strength != PatternEntry.RESET) {
            // Search backwards for string that contains this one;
            // most likely entry is last one
            int oldIndex = -1;
            if ((lastEntry.chars.length() == 1)) {
                int index = lastEntry.chars.charAt(0) / 8;
//                System.out.println("array content : " + statusArray[index] + " phrase : " + (byte)(BITARRAYMASK << index));
                if ((statusArray[index]
                     & (byte)(BITARRAYMASK << (lastEntry.chars.charAt(0) % 8))) != 0)
                    oldIndex = lastIndex + 1;
            } else {
                oldIndex = patterns.indexOf(lastEntry) + 1;
            }
            if ((oldIndex == -1))
                throw new ParseException("couldn't find last entry: "
                                          + lastEntry, oldIndex);
            return oldIndex;
        } else {
            int i;
            for (i = patterns.size() - 1; i >= 0; --i) {
                PatternEntry entry = (PatternEntry) patterns.elementAt(i);
                if (entry.chars.regionMatches(0,lastEntry.chars,0,
                                              entry.chars.length())) {
                    excess.append(lastEntry.chars.substring(entry.chars.length(),
                                                            lastEntry.chars.length()));
                    break;
                }
            }
            if (i == -1)
                throw new ParseException("couldn't find: " + lastEntry, i);
            return i + 1;
        }
    }
}

