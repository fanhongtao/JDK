/*
 * @(#)MergeCollation.java	1.10 97/12/05
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
 * @version    1.10 12/05/97
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
        int i;
        for (i = 0; i < patterns.size(); ++i) {
            PatternEntry entry = (PatternEntry) patterns.elementAt(i);
            if (entry.extension.length() != 0) {
                if (extList == null)
                    extList = new Vector();
                extList.insertElementAt(entry, extList.size());
            } else {
                if (extList != null) {
                    PatternEntry last = findLastWithNoExtension(i-1);
                    for (int j = extList.size() - 1; j >= 0 ; j--) {
                        tmp = (PatternEntry)(extList.elementAt(j));
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
                tmp = (PatternEntry)(extList.elementAt(j));
                tmp.addToBuffer(result, false, withWhiteSpace, last);
            }
            extList = null;
        }
        return result.toString();
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
            PatternEntry entry = (PatternEntry) patterns.elementAt(i);
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
    private PatternEntry saveEntry = null;
    private int lastIndex = -1;
    private Vector extList = null;
    private final byte BITARRAYMASK = (byte)0x1;
    private	final int  BYTEPOWER = 3;
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
            boolean setArray = false;
            byte bitClump = 0;
            byte setBit = 0;
            if ((newEntry.chars.length() == 1)) {
                oldIndex = newEntry.chars.charAt(0) >> BYTEPOWER;
                bitClump = statusArray[oldIndex];
                setBit = (byte)(BITARRAYMASK << (newEntry.chars.charAt(0) & BYTEMASK));
                if (bitClump != 0 && (bitClump & setBit) != 0)
                {
    				oldIndex = -1;
    				int i = 0;
    		        for (i = patterns.size() - 1; i >= 0; --i) {
    				    PatternEntry entry = (PatternEntry)patterns.elementAt(i);
    					if ((entry != null) &&
    					    (entry.chars.equals(newEntry.chars))) {
    						oldIndex = i;
    						break;
    					}
    				}
    				if (oldIndex != -1) {
    					patterns.removeElementAt(oldIndex);
    				} else {
    					System.out.println("FAILED SEARCH FOR value " + newEntry.chars);
    				}
    				lastIndex = patterns.indexOf(lastEntry);;
    			} else {
    				setArray = true;
    			}
            } else {
                oldIndex = patterns.indexOf(newEntry);
                if (oldIndex != -1)
                    patterns.removeElementAt(oldIndex);
            }
            StringBuffer excess = new StringBuffer();
            lastIndex = findLastEntry(lastEntry, excess);
            if (setArray) statusArray[oldIndex] = (byte)(bitClump | setBit);
            if (excess.length() != 0) {
                newEntry.extension = excess + newEntry.extension;
                if (lastIndex != patterns.size()) {
                    lastEntry = saveEntry;
                    changeLastEntry = false;
                }
            }
     		if (lastIndex == patterns.size()) {
    			patterns.addElement(newEntry);
                saveEntry = newEntry;
            } else {
    			patterns.insertElementAt(newEntry, lastIndex);
    			lastEntry = saveEntry;
            }
        }
        if (changeLastEntry)
            lastEntry = newEntry;
        else
            lastIndex = patterns.indexOf(lastEntry);
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
    			int index = lastEntry.chars.charAt(0) >> BYTEPOWER;
    			if ((statusArray[index] &
    				(BITARRAYMASK << (lastEntry.chars.charAt(0) & BYTEMASK))) != 0) {
    				oldIndex = lastIndex;
    			}
            } else {
                oldIndex = patterns.indexOf(lastEntry);
            }
            if ((oldIndex == -1))
                throw new ParseException("couldn't find last entry: "
                                          + lastEntry, oldIndex);
            return oldIndex + 1;
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

