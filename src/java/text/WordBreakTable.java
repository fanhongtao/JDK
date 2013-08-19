/*
 * @(#)WordBreakTable.java	1.11 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * (C) Copyright Taligent, Inc. 1996, 1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - 1998 - All Rights Reserved
 *
 * The original version of this source code and documentation
 * is copyrighted and owned by Taligent, Inc., a wholly-owned
 * subsidiary of IBM. These materials are provided under terms
 * of a License Agreement between Taligent and Sun. This technology
 * is protected by multiple US and International patents.
 *
 * This notice and attribution to Taligent may not be removed.
 * Taligent is a registered trademark of Taligent, Inc.
 *
 */

package java.text;

/**
 * This class implements a state transition table.
 * After each transition, using the get method, the
 * new state is returned along with information about
 * the state change (ex. was it a "marked" transition").
 * This class is internal only.
 */
final class WordBreakTable
{
    /**
     * Construct a table using existing data.  See SentenceBreakBoundary and
     * the other SimpleTextBoundary subclasses for examples.
     * @param cols number of columns in the table
     * @param data an encoded byte array containing state and transition data
     */
    public WordBreakTable(int cols, byte data[])
    {
        this.data = data;
        this.cols = cols;
    }

    /**
     * Get the resulting state moving from oldState accepting input
     * @param oldState current state
     * @param input input
     * @return int resulting state and transition data
     */
    public int get(int oldState, int input)
    {
        return data[(oldState & INDEX_MASK) * cols + input];
    }

    /**
     * Checks to see if the transition into the specified state was "marked"
     * @param state the state as returned by get, initialState, or endState
     * @return true if transition into state was marked.
     */
    public boolean isMarkState(int state)
    {
        return (state & MARK_MASK) != 0;
    }

    /**
     * Check is a state is the end state
     * @param state the state to check
     * @return true if state is an end state
     */
    public boolean isEndState(int state)
    {
        return (state & INDEX_MASK) == END_STATE;
    }

    /**
     * Get the start state
     * @return the initial state
     */
    public int initialState()
    {
        return INITIAL_STATE;
    }

    static final byte MARK_MASK = (byte)0x80;
    static final byte INDEX_MASK = (byte)0x7F;
    private static final int INITIAL_STATE = 1;
    private static final int END_STATE = 0;
    private byte data[];
    private int cols;
}

