/*
 * @(#)SimpleTextBoundary.java	1.26 03/01/23
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

import java.io.IOException;

/**
 * SimpleTextBoundary is an implementation of the BreakIterator
 * protocol.  SimpleTextBoundary uses a state machine to compute breaks.
 * There are currently several subclasses of SimpleTextBoundary that
 * compute breaks for sentences, words, lines, and characters.  They are
 * accessable through static functions of SimpleTextBoundary.
 *
 * @see BreakIterator
 */

final class SimpleTextBoundary extends BreakIterator
{

    private transient int pos;
    private transient CharacterIterator text;
    private TextBoundaryData data;

    // internally, the not-a-Unicode value is used as a sentinel value meaning
    // "the end of the string" for the purposes of looking up an appropriate
    // state transition when you've run off the end of the string
    private static final char END_OF_STRING = '\uffff';

    /**
     * Create a SimpleTextBoundary using the specified tables. Currently,
     * the table format is private.
     * @param data data used for boundary determination
     */
    protected SimpleTextBoundary(TextBoundaryData data)
    {
        this.data = data;
        text = new StringCharacterIterator("");
        pos = text.getBeginIndex();
    }

    /**
     * Compares the equality of two SimpleTextBoundary objects.
     * @param obj the SimpleTextBoundary object to be compared with.
     * @return true if the given obj is the same as this
     * SimpleTextBoundary object; false otherwise.
     */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (!(obj instanceof SimpleTextBoundary))
            return false;

        SimpleTextBoundary that = (SimpleTextBoundary) obj;

        // The data classes are final and sharable. Only the
        // class type needs to be compared.
        if (this.data.getClass() != that.data.getClass())
            return false;
        if (this.hashCode() != that.hashCode())
            return false;
        if (pos != that.pos)
            return false;
        if (!text.equals(that.text))
            return false;
        return true;
    }

    /**
     * Compute a hashcode for this enumeration
     * @return A hash code
     */
    public int hashCode()
    {
        return getClass().hashCode() ^ text.hashCode();
    }

    /**
     * Overrides Cloneable
     */
    public Object clone()
    {
        try {
            SimpleTextBoundary other = (SimpleTextBoundary) super.clone();
            other.text = (CharacterIterator) text.clone();
            // The data classes are final and sharable.
            // They don't need to be cloned.
            return other;
        } catch (InternalError e) {
            throw new InternalError();
        }
    }

    /**
     * Get the text being scanned by the enumeration
     * @return the text being scanned by the enumeration
     */
    public CharacterIterator getText()
    {
        return text;
    }

    /**
     * Set a new text string for enumeration.  The position of the
     * enumerator is reset to first().
     * @param newText new text to scan.
     */
    public void setText(String newText)
    {
        text = new StringCharacterIterator(newText);
        pos = text.getBeginIndex();
    }

    /**
     * Set a new text to scan.  The position is reset to first().
     * @param newText new text to scan.
     */
    public void setText(CharacterIterator newText)
    {
        text = newText;
        pos = text.getBeginIndex();
    }

    /**
     * Return the first boundary. The iterator's current position is set
     * to the first boundary.
     */
    public int first()
    {
        pos = text.getBeginIndex();
        return pos;
    }

    /**
     * Return the last boundary. The iterator's current position is set
     * to the last boundary.
     */
    public int last()
    {
        pos = text.getEndIndex();
        return pos;
    }

    /**
     * Return the nth boundary from the current boundary
     * @param index which boundary to return.  A value of 0
     * does nothing.
     * @return the nth boundary from the current position.
     */
    public int next(int increment)
    {
        int result = current();
        if (increment < 0) {
            for (int i = increment; (i < 0) && (result != DONE); ++i) {
                result = previous();
            }
        }
        else {
            for(int i = increment; (i > 0) && (result != DONE); --i) {
                result = next();
            }
        }
        return result;
    }

    /**
     * Return the boundary preceding the last boundary
     */
    public int previous()
    {
        if (pos > text.getBeginIndex()) {
            int startBoundary = pos;
            pos = previousSafePosition(pos-1);
            int prev = pos;
            int next = next();
            while (next < startBoundary && next != DONE) {
                prev = next;
                next = next();
            }
            pos = prev;
            return pos;
        }
        else {
            return DONE;
        }
    }

    /**
     * Return the next text boundary
     * @return the character offset of the text boundary or DONE if all
     * boundaries have been returned.
     */
    public int next()
    {
        int result = pos;
        if (pos < text.getEndIndex()) {
            pos = nextPosition(pos);
            result = pos;
        }
        else {
            result = DONE;
        }
        return result;
    }

    /**
     * Return true if the specified position is a boundary position.
     * @param offset the offset to check.
     * @return True if "offset" is a boundary position.
     */
    public boolean isBoundary(int offset) {
        int begin = text.getBeginIndex();
        if (offset < begin || offset >= text.getEndIndex())
            throw new IllegalArgumentException(
              "isBoundary offset out of bounds");

        if (offset == begin)
            return true;
        else
            return following(offset - 1) == offset;
    }

    /**
     * Return the first boundary after the specified offset
     * @param offset the offset to start
     * @return int the first boundary after offset
     */
    public int following(int offset)
    {
        if (offset < text.getBeginIndex() || offset >= text.getEndIndex())
            throw new IllegalArgumentException(
              "nextBoundaryAt offset out of bounds");
        pos = previousSafePosition(offset);
        int result;
        do {
            result = next();
        } while (result <= offset && result != DONE);
        return result;
    }

    /**
     * Return the last boundary preceding the specified offset
     * @param offset the offset to start
     * @return the last boundary before offset
     */
    public int preceding(int offset)
    {
        if (offset < text.getBeginIndex() || offset >= text.getEndIndex())
            throw new IllegalArgumentException("preceding() offset out of bounds");
        if (offset == text.getBeginIndex())
            return BreakIterator.DONE;
        pos = previousSafePosition(offset);
        int curr = pos;
        int last;
        do {
            last = curr;
            curr = next();
        } while (curr < offset && curr != BreakIterator.DONE);
        pos = last;
        return last;
    }

    /**
     * Return the boundary last returned by previous or next
     * @return int the boundary last returned by previous or next
     */
    public int current()
    {
        return pos;
    }

    //.................................................
    //utility functions.  These functions don't change the current position.
    private int previousSafePosition(int offset)
    {
        int result = text.getBeginIndex();
        int state = data.backward().initialState();

        if (offset == result)
            ++offset;
        for (char c = text.setIndex(offset - 1);
             c != CharacterIterator.DONE && !data.backward().isEndState(state);
             c = text.previous()) {

            state = data.backward().get(state, mappedChar(c));
            if (data.backward().isMarkState(state)) {
                result = text.getIndex();
            }
        }
        return result;
    }

    private int nextPosition(int offset)
    {
        int getEndIndex = text.getEndIndex();
        int state = data.forward().initialState();

        for (char c = text.setIndex(offset);
             c != CharacterIterator.DONE && !data.forward().isEndState(state);
             c = text.next()) {
            state = data.forward().get(state, mappedChar(c));
            if (data.forward().isMarkState(state)) {
                getEndIndex = text.getIndex();
            }
        }
        if (data.forward().isEndState(state))
            return getEndIndex;
        else {
            state = data.forward().get(state, mappedChar(END_OF_STRING));
            if (data.forward().isMarkState(state))
                return text.getEndIndex();
            else
                return getEndIndex;
        }
    }

    protected int mappedChar(char c)
    {
        return data.map().mappedChar(c);
    }

}
