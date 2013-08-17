/*
 * @(#)TextMeasurer.java	1.21 99/04/22
 *
 * Copyright 1997-1999 by Sun Microsystems, Inc.,
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

class TextMeasurer {

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
     * Construct a TextMeasurer from the source text.  The source text
     * should be a single entire paragraph.
     * @param text the source paragraph
     */
    public TextMeasurer(AttributedCharacterIterator text, FontRenderContext frc) {

        fFrc = frc;
        initSelf(text);
    }

    private void initSelf(AttributedCharacterIterator text) {

        fStart = text.getBeginIndex();

        // extract chars
        fChars = new char[text.getEndIndex() - fStart];

        int n = 0;
        for (char c = text.first(); c != text.DONE; c = text.next()) {
            fChars[n++] = c;
        }

        TextLine.FontSource fontSource = new TextLine.ACIFontSource(text);

        fIsDirectionLTR = true;

        boolean requiresBidi = false;
        boolean directionKnown = false;
        byte[] embs = null;

        text.first();
        Map attributes = text.getAttributes();
        if (attributes != null) {
          try {
            Boolean runDirection = (Boolean)attributes.get(TextAttribute.RUN_DIRECTION);
            if (runDirection != null) {
              directionKnown = true;
              fIsDirectionLTR = TextAttribute.RUN_DIRECTION_LTR.equals(runDirection);
              requiresBidi = !fIsDirectionLTR;
            }
          }
          catch (ClassCastException e) {
          }

	  try {
	    Float justifyLF = (Float)attributes.get(TextAttribute.JUSTIFICATION);
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
        }

        int begin = text.getBeginIndex();
        int end = text.getEndIndex();
        int pos = begin;
        byte level = 0;
        byte baselevel = (byte)((directionKnown && !fIsDirectionLTR) ? 1 : 0);
        do {
          text.setIndex(pos);
          attributes = text.getAttributes();
          Object embeddingLevel = attributes.get(TextAttribute.BIDI_EMBEDDING);
          int newpos = text.getRunLimit(TextAttribute.BIDI_EMBEDDING);

          if (embeddingLevel != null) {
            try {
              int intLevel = ((Integer)embeddingLevel).intValue();
              if (intLevel >= -15 && intLevel < 16) {
                level = (byte)intLevel;
                if (embs == null) {
                  embs = new byte[fChars.length];
                  requiresBidi = true;
                  if (!directionKnown) {
                    directionKnown = true;
                    fIsDirectionLTR = Bidi.defaultIsLTR(fChars, 0, fChars.length);
                    baselevel = (byte)(fIsDirectionLTR ? 0 : 1);
                  }
                  if (!fIsDirectionLTR) {
                    for (int i = 0; i < pos - begin; ++i) {
                      embs[i] = baselevel; // set initial level if rtl, already 0 so ok if ltr
                    }
                  }
                }
              }
            }
            catch (ClassCastException e) {
            }
          } else {
            if (embs != null) {
              level = baselevel;
            }
          }
          if (embs != null && level != 0) {
            for (int i = pos - begin; i < newpos - begin; ++i) {
              embs[i] = level;
            }
          }

          pos = newpos;
        } while (pos < end);
        
        if (!requiresBidi) {
          for (int i = 0; i < fChars.length; i++) {
            if (Bidi.requiresBidi(fChars[i])) {
              requiresBidi = true;
              break;
            }
          }
        }

        if (requiresBidi) {
          if (!directionKnown) {
            fIsDirectionLTR = Bidi.defaultIsLTR(fChars, 0, fChars.length);
          }
          if (embs == null) {
            embs = Bidi.getEmbeddingArray(fChars, fIsDirectionLTR);
          }

          fBidi = new Bidi(fChars, embs, fIsDirectionLTR);
        }

        TextLabelFactory factory = new TextLabelFactory(fFrc, fChars, fBidi);

        int[] charsLtoV = null;
        fLevels = null;
        if (fBidi != null) {
	    charsLtoV = fBidi.getLogicalToVisualMap();
	    fLevels = fBidi.getLevels();
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
        
        TextLine line = makeTextLineOnRange(start - fStart, limit - fStart);
        return line.getMetrics().advance;
        // could cache line in case getLayout is called with same start, limit
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
        
        TextLine textLine = makeTextLineOnRange(start-fStart, limit-fStart);

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
