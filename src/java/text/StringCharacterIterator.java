/*
 * @(#)StringCharacterIterator.java	1.15 98/02/02
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
 * <code>StringCharacterIterator</code> implements the
 * <code>CharacterIterater</code> protocol for a <code>String</code>.
 * The <code>StringCharacterIterator</code> class iterates over the
 * entire <code>String</code>.
 *
 * <P>
 * <strong>Examples</strong>:
 *
 * <P>
 * Traverse the text from start to finish
 * <blockquote>
 * <pre>
 * public void traverseForward(CharacterIterator iter) {
 *     for (char c = iter.first(); c != CharacterIterator.DONE; c = iter.next()) {
 *         processChar(c);
 *     }
 * }
 * </pre>
 * </blockquote>
 * Traverse the text backwards, from end to start
 * <blockquote>
 * <pre>
 * public void traverseBackward(CharacterIterator iter) {
 *     for (char c = iter.last(); c != CharacterIterator.DONE; c = iter.prev()) {
 *         processChar(c);
 *     }
 * }
 * </pre>
 * </blockquote>
 *
 * Traverse both forward and backward from a given position in the text.
 * <blockquote>
 * <pre>
 * public void traverseOut(CharacterIterator iter, int pos) {
 *     for (char c = iter.setIndex(pos);
 *          c != CharacterIterator.DONE && notBoundary(c);
 *          c = iter.next()) {}
 *     int end = iter.getIndex();
 *     for (char c = iter.setIndex(pos);
 *          c != CharacterIterator.DONE && notBoundary(c);
 *          c = iter.prev()) {}
 *     int start = iter.getIndex();
 *     processSection(iter.getText.subString(start,end);
 * }
 * </pre>
 * </blockquote>
 */

public final class StringCharacterIterator implements CharacterIterator
{
    private String text;
    private int begin;
    private int end;
    private int pos;

    /**
     * Construct an iterator with an initial index of 0.
     */
    public StringCharacterIterator(String text)
    {
        this(text, 0);
    }

    /**
     * Construct an iterator with the specified initial index.
     *
     * @param  text   The String to be iterated over
     * @param  pos    Initial iterator position
     */
    public StringCharacterIterator(String text, int pos)
    {
    this(text, 0, text.length(), pos);
    }

    /**
     * Construct an iterator over the given range of the given string, with the
     * index set at the specified position.
     *
     * @param  text   The String to be iterated over
     * @param  begin  Index of the first character
     * @param  end    Index of the character following the last character
     * @param  pos    Initial iterator position
     */
    public StringCharacterIterator(String text, int begin, int end, int pos) {
        if (text == null)
            throw new NullPointerException();
        this.text = text;

	if (begin < 0 || begin > end || end > text.length())
	    throw new IllegalArgumentException("Invalid substring range");

        if (pos < begin || pos > end)
            throw new IllegalArgumentException("Invalid position");

        this.begin = begin;
        this.end = end;
        this.pos = pos;
    }


    /**
     * Set the position to getBeginIndex() and return the character at that
     * position.
     */
    public char first()
    {
        pos = begin;
        return text.charAt(pos);
    }

    /**
     * Set the position to getEndIndex() and return the
     * character at that position.
     */
    public char last()
    {
        pos = end - 1;
        return text.charAt(pos);
    }

    /**
     * Set the position to specified position in the text and return that
     * character.
     */
    public char setIndex(int p)
    {
	if (p < begin || p >= end)
            throw new IllegalArgumentException("Invalid index");
        pos = p;
        return text.charAt(p);
    }

    /**
     * Get the character at the current position (as returned by getIndex()).
     * @return the character at the current position or DONE if the current
     * position is off the end of the text.
     */
    public char current()
    {
        if (pos >= begin && pos < end) {
            return text.charAt(pos);
        }
        else {
            return DONE;
        }
    }

    /**
     * Increment the iterator's index by one and return the character
     * at the new index.  If the resulting index is greater or equal
     * to getEndIndex(), the current index is reset to getEndIndex() and
     * a value of DONE is returned.
     * @return the character at the new position or DONE if the current
     * position is off the end of the text.
     */
    public char next()
    {
        if (pos < end - 1) {
            pos++;
            return text.charAt(pos);
        }
        else {
            pos = end;
            return DONE;
        }
    }

    /**
     * Decrement the iterator's index by one and return the character
     * at the new index.  If the resulting index is
     * less than getBeginIndex(), the current index is reset to getBeginIndex()
     * and a value of DONE is returned.
     * @return the character at the new position or DONE if the current
     * position is off the end of the text.
     */
    public char previous()
    {
        if (pos > begin) {
            return text.charAt(--pos);
        }
        else {
            return DONE;
        }
    }

    /**
     * Return the start index of the text.
     * @return the index at which the text begins.
     */
    public int getBeginIndex()
    {
        return begin;
    }

    /**
     * Return the end index of the text.  This index is the index of the
     * first character following the end of the text.
     * @return the index at which the text end.
     */
    public int getEndIndex()
    {
        return end;
    }

    /**
     * Return the current index.
     * @return the current index.
     */
    public int getIndex()
    {
        return pos;
    }

    /**
     * Compares the equality of two StringCharacterIterator objects.
     * @param obj the StringCharacterIterator object to be compared with.
     * @return true if the given obj is the same as this
     * StringCharacterIterator object; false otherwise.
     */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (!(obj instanceof StringCharacterIterator))
            return false;

        StringCharacterIterator that = (StringCharacterIterator) obj;

        if (hashCode() != that.hashCode())
            return false;
        if (!text.equals(that.text))
            return false;
        if (pos != that.pos || begin != that.begin || end != that.end)
            return false;
        return true;
    }

    /**
     * Compute a hashcode for this enumeration
     * @return A hash code
     */
    public int hashCode()
    {
        return text.hashCode() ^ pos ^ begin ^ end;
    }

    /**
     * Create a copy of this boundary
     * @return A copy of this
     */
    public Object clone()
    {
        try {
            StringCharacterIterator other
            = (StringCharacterIterator) super.clone();
            return other;
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

}
