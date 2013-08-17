/*
 * @(#)TextMeasurer.java	1.6 98/03/12
 * 
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
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

import java.util.Map;

/**
 * This class implements the 'primitive' operations needed for line
 * break: measuring up to a given advance, determining the advance of
 * a range of characters, and generating a TextLayout for a range of
 * characters. It also provides optimizations for incremental editing
 * of paragraphs.
 *
 * Most clients will use the more convenient LineBreakMeasurer, which
 * implements the standard line break policy (placing as many words as
 * will fit on each line).
 *
 * @see LineBreakMeasurer
 */

 // TO DO:  figure out how to 'recycle' TextLayoutComponents during line break,
 // and get rid of redundant AttributedString

class TextMeasurer {

    private FontRenderContext fFrc;

    private int fStart;

    // characters in source text
    private char[] fChars;

    // our copy of the text - eliminate when we can reuse TextLayoutComponents
    private AttributedString fText;
    private TextLine.FontSource fFontSource;

    // TextLabelFactory used on this line
    private TextLabelFactory fFactory;

    // glyph arrays in logical order
    private TextLineComponent[] fComponents;

    // paragraph data - same across all layouts
    private boolean fIsDirectionLTR;

    private byte fBaseline;
    private float[] fBaselineOffsets;

    private float fJustifyRatio;

    /**
     * Construct a TextMeasurer from the source text.  The source text
     * should be a single entire paragraph.
     * @param text the source paragraph
     */
    public TextMeasurer(AttributedCharacterIterator text, FontRenderContext frc) {

        fFrc = frc;
        initSelf(text);
    }

    private static AttributedString createAttrString(
                                        AttributedCharacterIterator text,
                                        char[] chars) {

        String string = new String(chars);
        int textStart = text.getBeginIndex();
        AttributedString attrString = new AttributedString(string);
        for (char c = text.first(); 
                    c != text.DONE; 
                    c = text.setIndex(text.getRunLimit())) {
            
            Map attrs = text.getAttributes();
            attrString.addAttributes(attrs, text.getRunStart()-textStart, text.getRunLimit()-textStart);
        }

        return attrString;
    }

    private void initSelf(AttributedCharacterIterator text) {

        fStart = text.getBeginIndex();

        // extract chars
        fChars = new char[text.getEndIndex() - fStart];

        int n = 0;
        for (char c = text.first(); c != text.DONE; c = text.next()) {
            fChars[n++] = c;
        }

        //fText = new AttributedString(text);
        fText = createAttrString(text, fChars);
        fFontSource = new TextLine.ACIFontSource(fText.getIterator());

        // make factory
        Bidi bidi = new Bidi(fChars);
        fIsDirectionLTR = bidi.isDirectionLTR();
        fFactory = new TextLabelFactory(fFrc, fChars, bidi);

        TextLine.FontSource fontSource = new TextLine.ACIFontSource(text);
        int[] charsLtoV = bidi.getLogicalToVisualMap();
        byte[] levels = bidi.getLevels();

        fComponents = TextLine.getComponents(
            fFontSource, fChars, 0, fChars.length, charsLtoV, levels, fFactory, null);

        Font firstFont = fFontSource.fontAt(0);
        if (firstFont == null) {
            firstFont = fFontSource.getBestFontAt(0);
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
     * Accumulate the advances of the characters at and after start,
     * until a character is reached whose advance would equal or
     * exceed maxAdvance. Return the index of that character.
     * @param start the character index at which to start measuring.
     * This is the absolute index, not the relative index within the
     * paragraph.
     * @param maxAdvance the maximumAdvance
     * @return the index of the character whose advance exceeded
     * maxAdvance.
     */
    public int getLineBreakIndex(int start, float maxAdvance) {
        
        return calcLineBreak(start - fStart, maxAdvance) + fStart;
    }

    /**
     * Return the sum of the advances for the characters
     * from start up to limit.
     *
     * @param start the character index at which to start measuring
     * @param limit the character index at which to stop measuring
     * @return the sum of the advances of the range of characters.
     */
    public float getAdvanceBetween(int start, int limit) {
        // REMIND jk df (need a real implementation)
        // jr Or do we?  Is there really a better way to do this?
        TextLayout hack = getLayout(start, limit);
        return hack.getAdvance();
    }

    /**
     * Return a layout that represents the characters.  The number
     * of characters must be >= 1.  The returned layout will apply the
     * bidi 'line reordering' rules to the text.
     *
     * @param start the index of the first character to use
     * @param limit the index past the last character to use
     * @return a new layout
     */
    public TextLayout getLayout(int start, int limit) {
        
        TextLine textLine = TextLine.createLineFromText(fChars, 
                                start-fStart, limit-fStart, fFontSource, 
                                fFactory, fIsDirectionLTR, fBaselineOffsets);

        return new TextLayout(
                        textLine, fBaseline, fBaselineOffsets, fJustifyRatio);
    }

    /**
     * An optimization to facilitate inserting single characters from
     * a paragraph. Clients can then remeasure and reextract layouts as
     * required.  In general, clients can always begin remeasuring from
     * the layout preceeding the one that contains the new character,
     * and stop measuring once the start of a subsequent layout matches
     * up with the start of a layout the client has cached (plus one
     * for the inserted character).
     *
     * @param newParagraph the text of the paragraph after performing
     * the insertion.
     * @param insertPos the position in the text
     * at which the single character was inserted.
     * @see #deleteChar
     */
    public void insertChar(AttributedCharacterIterator newParagraph, int insertPos) {
        initSelf(newParagraph);
    }
    
    /**
     * An optimization to facilitate deleting single characters from
     * a paragraph.
     *
     * @param newParagraph the text of the paragraph after performing
     * the deletion.
     * @param deletePos the position in the text
     * at which the single character was deleted.
     * @see #insertChar
     */
    public void deleteChar(AttributedCharacterIterator newParagraph, int deletePos) {
        initSelf(newParagraph);
    }
}
