/*
 * @(#)CharacterIterator.java	1.7 97/01/20
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


/**
 * This interface defines a protocol for bidirectional iteration over text.
 * The iterator iterates over a bounded sequence of characters.  Characters
 * are indexed with values beginning with the value returned by getBeginIndex and
 * continuing through the value returned by getEndIndex()-1.  The index of the
 * current character can be retrieved by calling getIndex.  Calling setIndex
 * will move the iterator to a new position within the sequence of characters.
 * If at any time the iterator's current index moves outside the range of
 * getBeginIndex and getEndIndex, previous() and next() will return DONE, signaling that
 * the iterator has reached the end of the sequence.
 * <P>Examples:<P>
 *
 * Traverse the text from start to finish
 * <pre>
 * public void traverseForward(CharacterIterator iter) {
 *     for(char c = iter.first(); c != CharacterIterator.DONE; c = iter.next()) {
 *         processChar(c);
 *     }
 * }
 * </pre>
 *
 * Traverse the text backwards, from end to start
 * <pre>
 * public void traverseBackward(CharacterIterator iter) {
 *     for(char c = iter.last(); c != CharacterIterator.DONE; c = iter.prev()) {
 *         processChar(c);
 *     }
 * }
 * </pre>
 *
 * Traverse both forward and backward from a given position in the text.
 * Calls to notBoundary() in this example represents some
 * additional stopping criteria.
 * <pre>
 * public void traverseOut(CharacterIterator iter, int pos) {
 *     for (char c = iter.setIndex(pos);
 *          c != CharacterIterator.DONE && notBoundary(c);
 *          c = iter.next()) {}
 * int end = iter.getIndex();
 * for (char c = iter.setIndex(pos);
 *     c != CharacterIterator.DONE && notBoundary(c);
 *     c = iter.prev()) {}
 * int start = iter.getIndex();
 * processSection(start,end);
 * }
 * </pre>
 *
 * @see StringCharacterIterator
 */

public interface CharacterIterator extends Cloneable
{

    /**
     * Constant that is returned when the iterator has reached either the end
     * or the beginning of the text.  The unicode 2.0 standard states that
     * '\\uFFFF' is an invalid unicode value and should not occur in any valid
     * unicode string.
     */
    public static final char DONE = '\uFFFF';

    /**
     * Set the position to getBeginIndex() and return the character at that
     * position.
     * @return the first character in the text
     * @see getBeginIndex
     */
    public char first();

    /**
     * Set the position to getEndIndex()-1, return the character at that position.
     * @return the last character in the text
     * @see getEndIndex
     */
    public char last();

    /**
     * Get the character at the current position (as returned by getIndex()).
     * @return the character at the current position or DONE if the current
     * position is off the end of the text.
     * @see getIndex
     */
    public char current();

    /**
     * Increment the iterator's index by one and return the character
     * at the new index.  If the resulting index is greater or equal
     * to getEndIndex(), the current index is reset to getEndIndex() and
     * a value of DONE is returned.
     * @return the character at the new position or DONE if the current
     * position is off the end of the text.
     */
    public char next();

    /**
     * Decrement the iterator's index by one and return the character
     * at the new index.  If the resulting index is
     * less than getBeginIndex(), the current index is reset to getBeginIndex()
     * and a value of DONE is returned.
     * @return the character at the new position or DONE if the current
     * position is off the end of the text.
     */
    public char previous();

    /**
     * Set the position to the specified position in the text and return that
     * character.
     * @param position the position within the text.  Valid values range from
     * getBeginIndex() to getEndIndex() - 1.  An IllegalArgumentException is thrown
     * if an invalid value is supplied.
     * @return the character at the specified position.
     */
    public char setIndex(int position);

    /**
     * Return the start index of the text.
     * @return the index at which the text begins.
     */
    public int getBeginIndex();

    /**
     * Return the end index of the text.  This index is the index of the first
     * character following the end of the text.
     * @return the index at which the text end.
     */
    public int getEndIndex();

    /**
     * Return the current index.
     * @return the current index.
     */
    public int getIndex();

    /**
     * Create a copy of this iterator
     * @return A copy of this
     */
    public Object clone();

}
