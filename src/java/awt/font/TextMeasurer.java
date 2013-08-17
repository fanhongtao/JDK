/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * (C) Copyright Taligent, Inc. 1996 - 1997, All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - 1998, All Rights Reserved
 *
 * The original version of this source code and documentation is
 * copyrighted and owned by Taligent, Inc., a wholly-owned subsidiary
 * of IBM. These materials are provided under terms of a License
 * Agreement between Taligent and Sun. This technology is protected
 * by multiple US and International patents.
 *
 * This notice and attribution to Taligent may not be removed.
 * Taligent is a registered trademark of Taligent, Inc.
 *
 */

package java.awt.font;

import java.awt.Font;

import java.text.CharacterIterator;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import java.awt.font.FontRenderContext;

import sun.awt.font.TextLineComponent;
import sun.awt.font.TextLabelFactory;
import sun.awt.font.Bidi;

/**
 * <code>TextMeasurer</code> provides the primitive operations needed for line
 * break: measuring up to a given advance, determining the advance of
 * a range of characters, and generating a <code>TextLayout</code> for a range of
 * characters. It also provides methods for incremental editing
 * of paragraphs.
 * <p>
 * Most clients will use the more convenient <code>LineBreakMeasurer</code>, which
 * implements the standard line break policy (placing as many words as
 * will fit on each line).
 *
 * @author John Raley
 * @version 1.26, 02/06/02
 * @see LineBreakMeasurer
 * @since 1.3
 */

public final class TextMeasurer {

    private FontRenderContext fFrc;

    private int fStart;

    // characters in source text
    private char[] fChars;

    // Bidi for this paragraph
    private Bidi fBidi;

    // Levels array for chars in this paragraph - needed to reorder
    // trailing counterdirectional whitespace
    private byte[] fLevels;

    // glyph arrays in logical order
    private TextLineComponent[] fComponents;

    // paragraph data - same across all layouts
    private boolean fIsDirectionLTR;
    private byte fBaseline;
    private float[] fBaselineOffsets;
    private float fJustifyRatio = 1;

    /**
     * Constructs a <code>TextMeasurer</code> from the source text.  
     * The source text should be a single entire paragraph.
     * @param text the source paragraph.  Cannot be null.
     * @param frc the information about a graphics device which is needed 
     *       to measure the text correctly.  Cannot be null.
     */
    public TextMeasurer(AttributedCharacterIterator text, FontRenderContext frc) {

        fFrc = frc;
        initAll(text);
    }

    /**
     * Initialize state, including fChars array, direction, and
     * fBidi.
     */
    private void initAll(AttributedCharacterIterator text) {

        fStart = text.getBeginIndex();

        // extract chars
        fChars = new char[text.getEndIndex() - fStart];

        int n = 0;
        for (char c = text.first(); c != text.DONE; c = text.next()) {
            fChars[n++] = c;
        }
        
        text.first();
        
        try {
            Float justifyLF = (Float)text.getAttribute(TextAttribute.JUSTIFICATION);
            if (justifyLF != null) {
              fJustifyRatio = justifyLF.floatValue();

              if (fJustifyRatio < 0) {
                fJustifyRatio = 0;
              } else if (fJustifyRatio > 1) {
                fJustifyRatio = 1;
              }
            }
        }
        catch (ClassCastException e) {
        }

        fBidi = TextLine.createBidiOnParagraph(text, fChars);
        generateComponents(text);
    }
    
    /**
     * Generate components for the paragraph.  fChars, fBidi should have been 
     * initialized already.
     */
    private void generateComponents(AttributedCharacterIterator text) {
        
        TextLine.FontSource fontSource = new TextLine.ACIFontSource(text);

        TextLabelFactory factory = new TextLabelFactory(fFrc, fChars, fBidi);

        int[] charsLtoV = null;
        
        if (fBidi != null) {
            charsLtoV = fBidi.getLogicalToVisualMap();
            fLevels = fBidi.getLevels();
            fIsDirectionLTR = fBidi.isDirectionLTR();
        }
        else {
            fLevels = null;
            fIsDirectionLTR = true;
        }
        
        fComponents = TextLine.getComponents(
            fontSource, fChars, 0, fChars.length, charsLtoV, fLevels, factory);

        Font firstFont = fontSource.fontAt(0);
        if (firstFont == null) {
            firstFont = fontSource.getBestFontAt(0);
        }

        LineMetrics lm = firstFont.getLineMetrics(fChars, 0, 1, fFrc);
        fBaselineOffsets = lm.getBaselineOffsets();
    }

    private int calcLineBreak(int startPos, float width) {

        int tlcIndex;
        int tlcStart = 0;

        for (tlcIndex = 0; tlcIndex < fComponents.length; tlcIndex++) {
            int gaLimit = tlcStart + fComponents[tlcIndex].getNumCharacters();
            if (gaLimit > startPos) {
                break;
            }
            else {
                tlcStart = gaLimit;
            }
        }

        // tlcStart is now the start of the tlc at tlcIndex

        for (; tlcIndex < fComponents.length; tlcIndex++) {
            
            TextLineComponent tlc = fComponents[tlcIndex];
            int numCharsInGa = tlc.getNumCharacters();

            int lineBreak = tlc.getLineBreakIndex(startPos - tlcStart, width);
            if (lineBreak == numCharsInGa) {
                width -= tlc.getAdvanceBetween(startPos - tlcStart, lineBreak);
                tlcStart += numCharsInGa;
                startPos = tlcStart;
            }
            else {
                return tlcStart + lineBreak;
            }
        }

        return fChars.length;
    }

    /**
     * According to the Unicode Bidirectional Behavior specification
     * (Unicode Standard 2.0, section 3.11), whitespace at the ends
     * of lines which would naturally flow against the base direction
     * must be made to flow with the line direction, and moved to the
     * end of the line.  This method returns the start of the sequence
     * of trailing whitespace characters to move to the end of a
     * line taken from the given range.
     */
    private int trailingCdWhitespaceStart(int startPos, int limitPos) {

        int cdWsStart = limitPos;

        if (fLevels != null) {
            // Back up over counterdirectional whitespace
            final byte baseLevel = (byte) (fIsDirectionLTR? 0 : 1); 
            for (cdWsStart = limitPos-1; cdWsStart >= startPos; cdWsStart--) {
                if ((fLevels[cdWsStart] % 2) == baseLevel || 
                        Bidi.getDirectionCode(fChars[cdWsStart]) != Bidi.WS) {
                    cdWsStart++;
                    break;
                }
            }
        }

        return cdWsStart;
    }

    private TextLineComponent[] makeComponentsOnRange(int startPos, 
                                                      int limitPos) {

        // sigh I really hate to do this here since it's part of the
        // bidi algorithm.
        // cdWsStart is the start of the trailing counterdirectional
        // whitespace
        final int cdWsStart = trailingCdWhitespaceStart(startPos, limitPos);

        int tlcIndex;
        int tlcStart = 0;

        for (tlcIndex = 0; tlcIndex < fComponents.length; tlcIndex++) {
            int gaLimit = tlcStart + fComponents[tlcIndex].getNumCharacters();
            if (gaLimit > startPos) {
                break;
            }
            else {
                tlcStart = gaLimit;
            }
        }

        // tlcStart is now the start of the tlc at tlcIndex

        int componentCount;
        {
            boolean split = false;
            int compStart = tlcStart;
            int lim=tlcIndex;
            for (boolean cont=true; cont; lim++) {
                int gaLimit = compStart + fComponents[lim].getNumCharacters();
                if (cdWsStart > Math.max(compStart, startPos) 
                            && cdWsStart < Math.min(gaLimit, limitPos)) {
                    split = true;
                }
                if (gaLimit >= limitPos) {
                    cont=false;
                }
                else {
                    compStart = gaLimit;
                }
            }
            componentCount = lim-tlcIndex;
            if (split) {
                componentCount++;
            }
        }

        TextLineComponent[] components = new TextLineComponent[componentCount];
        int newCompIndex = 0;
        int linePos = startPos;

        int breakPt = cdWsStart;

        int subsetFlag;
        if (breakPt == startPos) {
            subsetFlag = fIsDirectionLTR? TextLineComponent.LEFT_TO_RIGHT :
                                          TextLineComponent.RIGHT_TO_LEFT;
            breakPt = limitPos;
        }
        else {
            subsetFlag = TextLineComponent.UNCHANGED;
        }

        while (linePos < limitPos) {
            
            int compLength = fComponents[tlcIndex].getNumCharacters();
            int tlcLimit = tlcStart + compLength;

            int start = Math.max(linePos, tlcStart);
            int limit = Math.min(breakPt, tlcLimit);
            
            components[newCompIndex++] = fComponents[tlcIndex].getSubset(
                                                                start-tlcStart,
                                                                limit-tlcStart,
                                                                subsetFlag);
            linePos += (limit-start);
            if (linePos == breakPt) {
                breakPt = limitPos;
                subsetFlag = fIsDirectionLTR? TextLineComponent.LEFT_TO_RIGHT :
                                              TextLineComponent.RIGHT_TO_LEFT;
            }
            if (linePos == tlcLimit) {
                tlcIndex++;
                tlcStart = tlcLimit;
            }
        }

        return components;
    }

    private TextLine makeTextLineOnRange(int startPos, int limitPos) {

        int[] charsLtoV = null;
        byte[] charLevels = null;

        if (fBidi != null) {
            Bidi lineBidi = fBidi.createLineBidi(startPos, limitPos);
            charsLtoV = lineBidi.getLogicalToVisualMap();
            charLevels = lineBidi.getLevels();
        }

        TextLineComponent[] components = makeComponentsOnRange(startPos, limitPos);

        return new TextLine(components, 
                            fBaselineOffsets,
                            fChars,
                            startPos,
                            limitPos,
                            charsLtoV,
                            charLevels,
                            fIsDirectionLTR);

    }

    /**
     * Returns the index of the first character which will not fit on
     * on a line which begins at <code>start</code> and may be up to
     * <code>maxAdvance</code> in graphical width.
     *
     * @param start the character index at which to start measuring.
     *  <code>start</code> is an absolute index, not relative to the
     *  start of the paragraph
     * @param maxAdvance the graphical width in which the line must fit
     * @return the index after the last character which will fit
     *  on a line beginning at <code>start</code>, which is not longer
     *  than <code>maxAdvance</code> in graphical width
     */
    public int getLineBreakIndex(int start, float maxAdvance) {
        
        return calcLineBreak(start - fStart, maxAdvance) + fStart;
    }

    /**
     * Returns the graphical width of a line beginning at <code>start</code>
     * and including characters up to <code>limit</code>.
     * <code>start</code> and <code>limit</code> are absolute indices,
     * not relative to the start of the paragraph.
     *
     * @param start the character index at which to start measuring
     * @param limit the character index at which to stop measuring
     * @return the graphical width of a line beginning at <code>start</code>
     *   and including characters up to <code>limit</code>
     */
    public float getAdvanceBetween(int start, int limit) {
        
        TextLine line = makeTextLineOnRange(start - fStart, limit - fStart);
        return line.getMetrics().advance;
        // could cache line in case getLayout is called with same start, limit
    }

    /**
     * Returns a <code>TextLayout</code> on the given character range.
     *
     * @param start the index of the first character
     * @param limit the index after the last character.  Must be greater
     *   than <code>start</code>
     * @return a <code>TextLayout</code> for the characters beginning at
     *  <code>start</code> up to (but not including) <code>limit</code>
     */
    public TextLayout getLayout(int start, int limit) {
        
        TextLine textLine = makeTextLineOnRange(start-fStart, limit-fStart);

        return new TextLayout(
                        textLine, fBaseline, fBaselineOffsets, fJustifyRatio);
    }

    /**
     * Updates the <code>TextMeasurer</code> after a single character has 
     * been inserted
     * into the paragraph currently represented by this
     * <code>TextMeasurer</code>.  After this call, this
     * <code>TextMeasurer</code> is equivalent to a new <code>TextMeasurer</code>
     * created from the text;  however, it will usually be more efficient
     * to update an existing <code>TextMeasurer</code> than to create a new one
     * from scratch.
     *
     * @param newParagraph the text of the paragraph after performing
     * the insertion.  Cannot be null.
     * @param insertPos the position in the text where the character was inserted.  
     * Must not be less than
     * the start of <code>newParagraph</code>, and must be less than the
     * end of <code>newParagraph</code>.
     */
    public void insertChar(AttributedCharacterIterator newParagraph, int insertPos) {

        fStart = newParagraph.getBeginIndex();
        int end = newParagraph.getEndIndex();
        if (end - fStart != fChars.length+1) {
            initAll(newParagraph);
        }
        
        char[] newChars = new char[end-fStart];
        int newCharIndex = insertPos - fStart;
        System.arraycopy(fChars, 0, newChars, 0, newCharIndex);
        
        char newChar = newParagraph.setIndex(insertPos);
        newChars[newCharIndex] = newChar;
        System.arraycopy(fChars, 
                         newCharIndex, 
                         newChars, 
                         newCharIndex+1, 
                         end-insertPos-1);
        fChars = newChars;
        
        if (fBidi != null || Bidi.requiresBidi(newChar) || 
                newParagraph.getAttribute(TextAttribute.BIDI_EMBEDDING) != null) {

            fBidi = TextLine.createBidiOnParagraph(newParagraph, fChars);
        }
        
        generateComponents(newParagraph);
    }
    
    /**
     * Updates the <code>TextMeasurer</code> after a single character has 
     * been deleted
     * from the paragraph currently represented by this
     * <code>TextMeasurer</code>.  After this call, this
     * <code>TextMeasurer</code> is equivalent to a new <code>TextMeasurer</code>
     * created from the text;  however, it will usually be more efficient
     * to update an existing <code>TextMeasurer</code> than to create a new one
     * from scratch.
     *
     * @param newParagraph the text of the paragraph after performing
     * the deletion.  Cannot be null.
     * @param deletePos the position in the text where the character was removed.  
     * Must not be less than
     * the start of <code>newParagraph</code>, and must not be greater than the
     * end of <code>newParagraph</code>.
     */
    public void deleteChar(AttributedCharacterIterator newParagraph, int deletePos) {

        fStart = newParagraph.getBeginIndex();
        int end = newParagraph.getEndIndex();
        if (end - fStart != fChars.length-1) {
            initAll(newParagraph);
        }
        
        char[] newChars = new char[end-fStart];
        int changedIndex = deletePos-fStart;
        
        System.arraycopy(fChars, 0, newChars, 0, deletePos-fStart);
        System.arraycopy(fChars, changedIndex+1, newChars, changedIndex, end-deletePos);
        fChars = newChars;
        
        if (fBidi != null) {
            fBidi = TextLine.createBidiOnParagraph(newParagraph, fChars);
        }
        
        generateComponents(newParagraph);
    }

    /**
     * NOTE:  This method is only for LineBreakMeasurer's use.  It is package-
     * private because it returns internal data.
     */
    char[] getChars() {

        return fChars;
    }
}
