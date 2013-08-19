/*
 * @(#)RBTableBuilder.java	1.9 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * (C) Copyright Taligent, Inc. 1996, 1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996-1998 - All Rights Reserved
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

import java.util.Vector;
import sun.text.CompactIntArray;
import sun.text.IntHashtable;
import sun.text.Normalizer;
import sun.text.ComposedCharIter;
import sun.text.NormalizerUtilities;

/**
 * This class contains all the code to parse a RuleBasedCollator pattern
 * and build a RBCollationTables object from it.  A particular instance
 * of tis class exists only during the actual build process-- once an
 * RBCollationTables object has been built, the RBTableBuilder object
 * goes away.  This object carries all of the state which is only needed
 * during the build process, plus a "shadow" copy of all of the state
 * that will go into the tables object itself.  This object communicates
 * with RBCollationTables through a separate class, RBCollationTables.BuildAPI,
 * this is an inner class of RBCollationTables and provides a separate
 * private API for communication with RBTableBuilder.
 * This class isn't just an inner class of RBCollationTables itself because
 * of its large size.  For source-code readability, it seemed better for the
 * builder to have its own source file.
 */
final class RBTableBuilder {

    public RBTableBuilder(RBCollationTables.BuildAPI tables) {
        this.tables = tables;
    }

    /**
     * Create a table-based collation object with the given rules.
     * This is the main function that actually builds the tables and
     * stores them back in the RBCollationTables object.  It is called
     * ONLY by the RBCollationTables constructor.
     * @see java.util.RuleBasedCollator#RuleBasedCollator
     * @exception ParseException If the rules format is incorrect.
     */
    public void build(String pattern, int decmp) throws ParseException
    {
        boolean isSource = true;
        int i = 0;
        String expChars;
        String groupChars;
        if (pattern.length() == 0)
            throw new ParseException("Build rules empty.", 0);

        // This array maps Unicode characters to their collation ordering
        mapping = new CompactIntArray((int)RBCollationTables.UNMAPPED);

        // Normalize the build rules.  Find occurances of all decomposed characters
        // and normalize the rules before feeding into the builder.  By "normalize",
        // we mean that all precomposed Unicode characters must be converted into
        // a base character and one or more combining characters (such as accents).
        // When there are multiple combining characters attached to a base character,
        // the combining characters must be in their canonical order
        //
        Normalizer.Mode mode = NormalizerUtilities.toNormalizerMode(decmp);
        pattern = Normalizer.normalize(pattern, mode, 0, true);

        // Build the merged collation entries
        // Since rules can be specified in any order in the string
        // (e.g. "c , C < d , D < e , E .... C < CH")
        // this splits all of the rules in the string out into separate
        // objects and then sorts them.  In the above example, it merges the
        // "C < CH" rule in just before the "C < D" rule.
        //
        mPattern = new MergeCollation(pattern);
        int order = 0;

        // Now walk though each entry and add it to my own tables
        for (i = 0; i < mPattern.getCount(); ++i)
        {
            PatternEntry entry = mPattern.getItemAt(i);
            if (entry != null) {
                groupChars = entry.getChars();
                if (groupChars.length() > 1) {
		    switch(groupChars.charAt(groupChars.length()-1)) {
		    case '@':
			frenchSec = true;
			groupChars = groupChars.substring(0, groupChars.length()-1);
			break;
		    case '!':
			seAsianSwapping = true;
			groupChars = groupChars.substring(0, groupChars.length()-1);
			break;
		    }
                }

                order = increment(entry.getStrength(), order);
                expChars = entry.getExtension();

                if (expChars.length() != 0) {
                    addExpandOrder(groupChars, expChars, order);
                } else if (groupChars.length() > 1) {
                    addContractOrder(groupChars, order);
                } else {
                    char ch = groupChars.charAt(0);
                    addOrder(ch, order);
                }
            }
        }

        addComposedChars();

        commit();
        mapping.compact();

        tables.fillInTables(frenchSec, seAsianSwapping, mapping, contractTable, expandTable,
                    contractFlags, maxSecOrder, maxTerOrder);
    }

    /** Add expanding entries for pre-composed unicode characters so that this
     * collator can be used reasonably well with decomposition turned off.
     */
    private void addComposedChars() throws ParseException {
        StringBuffer buf = new StringBuffer(1);

        // Iterate through all of the pre-composed characters in Unicode
        ComposedCharIter iter = new ComposedCharIter(false, Normalizer.IGNORE_HANGUL);

        while (iter.hasNext()) {
            char c = iter.next();

            if (getCharOrder(c) == RBCollationTables.UNMAPPED) {
                //
                // We don't already have an ordering for this pre-composed character.
                //
                // First, see if the decomposed string is already in our
                // tables as a single contracting-string ordering.
                // If so, just map the precomposed character to that order.
                //
                // TODO: What we should really be doing here is trying to find the
                // longest initial substring of the decomposition that is present
                // in the tables as a contracting character sequence, and find its
                // ordering.  Then do this recursively with the remaining chars
                // so that we build a list of orderings, and add that list to
                // the expansion table.
                // That would be more correct but also significantly slower, so
                // I'm not totally sure it's worth doing.
                //
                String s = iter.decomposition();
                int contractOrder = getContractOrder(s);
                if (contractOrder != RBCollationTables.UNMAPPED) {
                    addOrder(c, contractOrder);
                } else {
                    //
                    // We don't have a contracting ordering for the entire string
                    // that results from the decomposition, but if we have orders
                    // for each individual character, we can add an expanding
                    // table entry for the pre-composed character
                    //
                    boolean allThere = true;
                    for (int i = 0; i < s.length(); i++) {
                        if (getCharOrder(s.charAt(i)) == RBCollationTables.UNMAPPED) {
                            allThere = false;
                            break;
                        }
                    }
                    if (allThere) {
                        buf.setLength(0);
                        buf.append(c);
                        addExpandOrder(buf.toString(), s, RBCollationTables.UNMAPPED);
                    }
                }
            }
        }
    }

    /**
     * Look up for unmapped values in the expanded character table.
     *
     * When the expanding character tables are built by addExpandOrder,
     * it doesn't know what the final ordering of each character
     * in the expansion will be.  Instead, it just puts the raw character
     * code into the table, adding CHARINDEX as a flag.  Now that we've
     * finished building the mapping table, we can go back and look up
     * that character to see what its real collation order is and
     * stick that into the expansion table.  That lets us avoid doing
     * a two-stage lookup later.
     */
    private final void commit()
    {
        if (expandTable != null) {
            for (int i = 0; i < expandTable.size(); i++) {
                int[] valueList = (int [])expandTable.elementAt(i);
                for (int j = 0; j < valueList.length; j++) {
                    int order = valueList[j];
                    if (order < RBCollationTables.EXPANDCHARINDEX && order > CHARINDEX) {
                        // found a expanding character that isn't filled in yet
                        char ch = (char)(order - CHARINDEX);

                        // Get the real values for the non-filled entry
                        int realValue = getCharOrder(ch);

                        if (realValue == RBCollationTables.UNMAPPED) {
                            // The real value is still unmapped, maybe it's ignorable
                            valueList[j] = IGNORABLEMASK & ch;
                        } else {
                            // just fill in the value
                            valueList[j] = realValue;
                        }
                    }
                }
            }
        }
    }
    /**
     *  Increment of the last order based on the comparison level.
     */
    private final int increment(int aStrength, int lastValue)
    {
        switch(aStrength)
        {
        case Collator.PRIMARY:
            // increment priamry order  and mask off secondary and tertiary difference
            lastValue += PRIMARYORDERINCREMENT;
            lastValue &= RBCollationTables.PRIMARYORDERMASK;
            isOverIgnore = true;
            break;
        case Collator.SECONDARY:
            // increment secondary order and mask off tertiary difference
            lastValue += SECONDARYORDERINCREMENT;
            lastValue &= RBCollationTables.SECONDARYDIFFERENCEONLY;
            // record max # of ignorable chars with secondary difference
            if (!isOverIgnore)
                maxSecOrder++;
            break;
        case Collator.TERTIARY:
            // increment tertiary order
            lastValue += TERTIARYORDERINCREMENT;
            // record max # of ignorable chars with tertiary difference
            if (!isOverIgnore)
                maxTerOrder++;
            break;
        }
        return lastValue;
    }

    /**
     *  Adds a character and its designated order into the collation table.
     */
    private final void addOrder(char ch,
                                int anOrder)
    {
        // See if the char already has an order in the mapping table
        int order = mapping.elementAt(ch);

        if (order >= RBCollationTables.CONTRACTCHARINDEX) {
            // There's already an entry for this character that points to a contracting
            // character table.  Instead of adding the character directly to the mapping
            // table, we must add it to the contract table instead.

            key.setLength(0);
            key.append(ch);
            addContractOrder(key.toString(), anOrder);
        } else {
            // add the entry to the mapping table,
            // the same later entry replaces the previous one
            mapping.setElementAt(ch, anOrder);
        }
    }

    private final void addContractOrder(String groupChars, int anOrder) {
        addContractOrder(groupChars, anOrder, true);
    }

    /**
     *  Adds the contracting string into the collation table.
     */
    private final void addContractOrder(String groupChars, int anOrder,
                                          boolean fwd)
    {
        if (contractTable == null) {
            contractTable = new Vector(INITIALTABLESIZE);
        }

        // See if the initial character of the string already has a contract table.
        int entry = mapping.elementAt(groupChars.charAt(0));
        Vector entryTable = getContractValues(entry - RBCollationTables.CONTRACTCHARINDEX);

        if (entryTable == null) {
            // We need to create a new table of contract entries for this base char
            int tableIndex = RBCollationTables.CONTRACTCHARINDEX + contractTable.size();
            entryTable = new Vector(INITIALTABLESIZE);
            contractTable.addElement(entryTable);

            // Add the initial character's current ordering first. then
            // update its mapping to point to this contract table
            entryTable.addElement(new EntryPair(groupChars.substring(0,1), entry));
            mapping.setElementAt(groupChars.charAt(0), tableIndex);
        }

        // Now add (or replace) this string in the table
        int index = RBCollationTables.getEntry(entryTable, groupChars, fwd);
        if (index != RBCollationTables.UNMAPPED) {
            EntryPair pair = (EntryPair) entryTable.elementAt(index);
            pair.value = anOrder;
        } else {
            EntryPair pair = (EntryPair)entryTable.lastElement();

            // NOTE:  This little bit of logic is here to speed CollationElementIterator
            // .nextContractChar().  This code ensures that the longest sequence in
            // this list is always the _last_ one in the list.  This keeps
            // nextContractChar() from having to search the entire list for the longest
            // sequence.
            if (groupChars.length() > pair.entryName.length()) {
                entryTable.addElement(new EntryPair(groupChars, anOrder, fwd));
            } else {
                entryTable.insertElementAt(new EntryPair(groupChars, anOrder,
                        fwd), entryTable.size() - 1);
            }
        }

        // If this was a forward mapping for a contracting string, also add a
        // reverse mapping for it, so that CollationElementIterator.previous
        // can work right
        if (fwd && groupChars.length() > 1) {
            addContractFlags(groupChars);
            addContractOrder(new StringBuffer(groupChars).reverse().toString(),
                             anOrder, false);
        }
    }

    /**
     * If the given string has been specified as a contracting string
     * in this collation table, return its ordering.
     * Otherwise return UNMAPPED.
     */
    private int getContractOrder(String groupChars)
    {
        int result = RBCollationTables.UNMAPPED;
        if (contractTable != null) {
            Vector entryTable = getContractValues(groupChars.charAt(0));
            if (entryTable != null) {
                int index = RBCollationTables.getEntry(entryTable, groupChars, true);
                if (index != RBCollationTables.UNMAPPED) {
                    EntryPair pair = (EntryPair) entryTable.elementAt(index);
                    result = pair.value;
                }
            }
        }
        return result;
    }

    private final int getCharOrder(char ch) {
        int order = mapping.elementAt(ch);

        if (order >= RBCollationTables.CONTRACTCHARINDEX) {
            Vector groupList = getContractValues(order - RBCollationTables.CONTRACTCHARINDEX);
            EntryPair pair = (EntryPair)groupList.firstElement();
            order = pair.value;
        }
        return order;
    }

    /**
     *  Get the entry of hash table of the contracting string in the collation
     *  table.
     *  @param ch the starting character of the contracting string
     */
    Vector getContractValues(char ch)
    {
        int index = mapping.elementAt(ch);
        return getContractValues(index - RBCollationTables.CONTRACTCHARINDEX);
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
                                int anOrder) throws ParseException
    {
        // Create an expansion table entry
        int tableIndex = addExpansion(anOrder, expandChars);

        // And add its index into the main mapping table
        if (contractChars.length() > 1) {
            addContractOrder(contractChars, tableIndex);
        } else {
            addOrder(contractChars.charAt(0), tableIndex);
        }
    }

    /**
     * Create a new entry in the expansion table that contains the orderings
     * for the given characers.  If anOrder is valid, it is added to the
     * beginning of the expanded list of orders.
     */
    private int addExpansion(int anOrder, String expandChars) {
        if (expandTable == null) {
            expandTable = new Vector(INITIALTABLESIZE);
        }

        // If anOrder is valid, we want to add it at the beginning of the list
        int offset = (anOrder == RBCollationTables.UNMAPPED) ? 0 : 1;

        int[] valueList = new int[expandChars.length() + offset];
        if (offset == 1) {
            valueList[0] = anOrder;
        }

        for (int i = 0; i < expandChars.length(); i++) {
            char ch = expandChars.charAt(i);
            int mapValue = getCharOrder(ch);

            if (mapValue != RBCollationTables.UNMAPPED) {
                valueList[i+offset] = mapValue;
            } else {
                // can't find it in the table, will be filled in by commit().
                valueList[i+offset] = CHARINDEX + (int)ch;
            }
        }

        // Add the expanding char list into the expansion table.
        int tableIndex = RBCollationTables.EXPANDCHARINDEX + expandTable.size();
        expandTable.addElement(valueList);

        return tableIndex;
    }

    private void addContractFlags(String chars) {
        char c;
        int len = chars.length();
        for (int i = 0; i < len; i++) {
            c = chars.charAt(i);
            contractFlags.put(c, 1);
        }
    }

    // ==============================================================
    // constants
    // ==============================================================
    final static int CHARINDEX = 0x70000000;  // need look up in .commit()

    private final static int IGNORABLEMASK = 0x0000ffff;
    private final static int PRIMARYORDERINCREMENT = 0x00010000;
    private final static int SECONDARYORDERINCREMENT = 0x00000100;
    private final static int TERTIARYORDERINCREMENT = 0x00000001;
    private final static int INITIALTABLESIZE = 20;
    private final static int MAXKEYSIZE = 5;

    // ==============================================================
    // instance variables
    // ==============================================================

    // variables used by the build process
    private RBCollationTables.BuildAPI tables = null;
    private MergeCollation mPattern = null;
    private boolean isOverIgnore = false;
    private StringBuffer key = new StringBuffer(MAXKEYSIZE);
    private IntHashtable contractFlags = new IntHashtable(100);

    // "shadow" copies of the instance variables in RBCollationTables
    // (the values in these variables are copied back into RBCollationTables
    // at the end of the build process)
    private boolean frenchSec = false;
    private boolean seAsianSwapping = false;

    private CompactIntArray mapping = null;
    private Vector   contractTable = null;
    private Vector   expandTable = null;

    private short maxSecOrder = 0;
    private short maxTerOrder = 0;
}
