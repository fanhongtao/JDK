/*
 * @(#)TextLine.java	1.52 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * (C) Copyright IBM Corp. 1998-2003, All Rights Reserved
 *
 */

package java.awt.font;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;

import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import java.awt.im.InputMethodHighlight;

import java.text.CharacterIterator;
import java.text.AttributedCharacterIterator;
import java.text.Annotation;
import java.text.Bidi;

import java.util.Map;
import java.util.Hashtable;

import sun.font.BidiUtils;
import sun.font.CoreMetrics;
import sun.font.Decoration;
import sun.font.FontLineMetrics;
import sun.font.FontResolver;
import sun.font.GraphicComponent;
import sun.font.TextLabelFactory;
import sun.font.TextLineComponent;
import sun.text.CodePointIterator;

final class TextLine {

    static final class TextLineMetrics {
        public final float ascent;
        public final float descent;
        public final float leading;
        public final float advance;

        public TextLineMetrics(float ascent,
                           float descent,
                           float leading,
                           float advance) {
            this.ascent = ascent;
            this.descent = descent;
            this.leading = leading;
            this.advance = advance;
        }
    }

    private TextLineComponent[] fComponents;
    private float[] fBaselineOffsets;
    private int[] fComponentVisualOrder; // if null, ltr
    private float[] locs; // x,y pairs for components in visual order
    private char[] fChars;
    private int fCharsStart;
    private int fCharsLimit;
    private int[] fCharVisualOrder;  // if null, ltr
    private int[] fCharLogicalOrder; // if null, ltr
    private byte[] fCharLevels;     // if null, 0
    private boolean fIsDirectionLTR;

    private TextLineMetrics fMetrics = null; // built on demand in getMetrics

    public TextLine(TextLineComponent[] components,
                    float[] baselineOffsets,
                    char[] chars,
                    int charsStart,
                    int charsLimit,
                    int[] charLogicalOrder,
                    byte[] charLevels,
                    boolean isDirectionLTR) {

        int[] componentVisualOrder = computeComponentOrder(components,
                                                           charLogicalOrder);

        fComponents = components;
        fBaselineOffsets = baselineOffsets;
        fComponentVisualOrder = componentVisualOrder;
        fChars = chars;
        fCharsStart = charsStart;
        fCharsLimit = charsLimit;
        fCharLogicalOrder = charLogicalOrder;
        fCharLevels = charLevels;
        fIsDirectionLTR = isDirectionLTR;
        checkCtorArgs();

        init();
    }

    private void checkCtorArgs() {

        int checkCharCount = 0;
        for (int i=0; i < fComponents.length; i++) {
            checkCharCount += fComponents[i].getNumCharacters();
        }

        if (checkCharCount != this.characterCount()) {
            throw new IllegalArgumentException("Invalid TextLine!  " +
                                "char count is different from " +
                                "sum of char counts of components.");
        }
    }

    private void init() {

	// first, we need to check for graphic components on the TOP or BOTTOM baselines.  So
	// we perform the work that used to be in getMetrics here.

	float ascent = 0;
	float descent = 0;
	float leading = 0;
	float advance = 0;

	// ascent + descent must not be less than this value
	float maxGraphicHeight = 0;
	float maxGraphicHeightWithLeading = 0;

	// walk through EGA's
	TextLineComponent tlc;
	boolean fitTopAndBottomGraphics = false;

	for (int i = 0; i < fComponents.length; i++) {
	    tlc = fComponents[i];

	    CoreMetrics cm = tlc.getCoreMetrics();
	    byte baseline = (byte)cm.baselineIndex;

	    if (baseline >= 0) {
		float baselineOffset = fBaselineOffsets[baseline];

		ascent = Math.max(ascent, -baselineOffset + cm.ascent);

		float gd = baselineOffset + cm.descent;
		descent = Math.max(descent, gd);

		leading = Math.max(leading, gd + cm.leading);
	    }
	    else {
		fitTopAndBottomGraphics = true;
		float graphicHeight = cm.ascent + cm.descent;
		float graphicHeightWithLeading = graphicHeight + cm.leading;
		maxGraphicHeight = Math.max(maxGraphicHeight, graphicHeight);
		maxGraphicHeightWithLeading = Math.max(maxGraphicHeightWithLeading,
						       graphicHeightWithLeading);
	    }
	}

	if (fitTopAndBottomGraphics) {
	    if (maxGraphicHeight > ascent + descent) {
		descent = maxGraphicHeight - ascent;
	    }
	    if (maxGraphicHeightWithLeading > ascent + leading) {
		leading = maxGraphicHeightWithLeading - ascent;
	    }
	}

	leading -= descent;

	// we now know enough to compute the locs, but we need the final loc
	// for the advance before we can create the metrics object

	if (fitTopAndBottomGraphics) {
	    // we have top or bottom baselines, so expand the baselines array
	    // full offsets are needed by CoreMetrics.effectiveBaselineOffset
	    fBaselineOffsets = new float[] {
		fBaselineOffsets[0],
		fBaselineOffsets[1],
		fBaselineOffsets[2],
		descent,
		-ascent
	    };
	}
	    
        float x = 0;
	float y = 0;
        CoreMetrics pcm = null;

        locs = new float[fComponents.length * 2 + 2];

        for (int i = 0, n = 0; i < fComponents.length; ++i, n += 2) {
            int vi = fComponentVisualOrder == null ? i : fComponentVisualOrder[i];
            
            tlc = fComponents[vi];
            CoreMetrics cm = tlc.getCoreMetrics();

            if ((pcm != null) &&
                (pcm.italicAngle != 0 || cm.italicAngle != 0) &&  // adjust because of italics
                (pcm.italicAngle != cm.italicAngle ||
		 pcm.baselineIndex != cm.baselineIndex ||
		 pcm.ssOffset != cm.ssOffset)) {

                        // 1) compute the area of overlap - min effective ascent and min effective descent
                        // 2) compute the x positions along italic angle of ascent and descent for left and right
                        // 3) compute maximum left - right, adjust right position by this value
                        // this is a crude form of kerning between textcomponents

                        // 1)
			float pb = pcm.effectiveBaselineOffset(fBaselineOffsets);
                        float pa = pb - pcm.ascent;
                        float pd = pb + pcm.descent;
                        pb += pcm.ssOffset;
                        
			float cb = cm.effectiveBaselineOffset(fBaselineOffsets);
                        float ca = cb - cm.ascent;
                        float cd = cb + cm.descent;
                        cb += cm.ssOffset;

                        float a = Math.max(pa, ca);
                        float d = Math.min(pd, cd);

                        // 2)
                        float pax = pcm.italicAngle * (pb - a);
                        float pdx = pcm.italicAngle * (pb - d);

                        float cax = cm.italicAngle * (cb - a);
                        float cdx = cm.italicAngle * (cb - d);

                        // 3)
                        float dax = pax - cax;
                        float ddx = pdx - cdx;
                        float dx = Math.max(dax, ddx);

                        x += dx;
			y = cb;
            } else {
		// no italic adjustment for x, but still need to compute y
		y = cm.effectiveBaselineOffset(fBaselineOffsets) + cm.ssOffset;
	    }

            locs[n] = x;
            locs[n+1] = y;

            x += tlc.getAdvance();
            pcm = cm;
        }

	// do we want italic padding at the right of the line?
        if (pcm.italicAngle != 0) {
	    float pb = pcm.effectiveBaselineOffset(fBaselineOffsets);
	    float pa = pb - pcm.ascent;
	    float pd = pb + pcm.descent;
	    pb += pcm.ssOffset;

            float d;
	    if (pcm.italicAngle > 0) {
		d = pb + pcm.ascent;
	    } else {
		d = pb - pcm.descent;
	    }
	    d *= pcm.italicAngle;

	    x += d;
	}
	locs[locs.length - 2] = x;
	// locs[locs.length - 1] = 0; // final offset is always back on baseline

	// ok, build fMetrics since we have the final advance
	advance = x;
	fMetrics = new TextLineMetrics(ascent, descent, leading, advance);
    }

    private abstract static class Function {

        abstract float computeFunction(TextLine line,
                                       int componentIndex,
                                       int indexInArray);
    }

    private static Function fgPosAdvF = new Function() {
        float computeFunction(TextLine line,
                              int componentIndex,
                              int indexInArray) {

            TextLineComponent tlc = line.fComponents[componentIndex];
            int vi = line.fComponentVisualOrder == null
                ? componentIndex 
                : line.fComponentVisualOrder[componentIndex];
            return line.locs[vi * 2] + tlc.getCharX(indexInArray) + tlc.getCharAdvance(indexInArray);
        }
    };

    private static Function fgAdvanceF = new Function() {

        float computeFunction(TextLine line,
                              int componentIndex,
                              int indexInArray) {

            TextLineComponent tlc = line.fComponents[componentIndex];
            return tlc.getCharAdvance(indexInArray);
        }
    };

    private static Function fgXPositionF = new Function() {

        float computeFunction(TextLine line,
                              int componentIndex,
                              int indexInArray) {

            int vi = line.fComponentVisualOrder == null
                ? componentIndex 
                : line.fComponentVisualOrder[componentIndex];
            TextLineComponent tlc = line.fComponents[componentIndex];
            return line.locs[vi * 2] + tlc.getCharX(indexInArray);
        }
    };

    private static Function fgYPositionF = new Function() {

        float computeFunction(TextLine line,
                              int componentIndex,
                              int indexInArray) {

            TextLineComponent tlc = line.fComponents[componentIndex];
            float charPos = tlc.getCharY(indexInArray);

            // charPos is relative to the component - adjust for
            // baseline

            return charPos + line.getComponentShift(componentIndex);
        }
    };

    public int characterCount() {

        return fCharsLimit - fCharsStart;
    }

    public boolean isDirectionLTR() {

        return fIsDirectionLTR;
    }

    public TextLineMetrics getMetrics() {
	return fMetrics;
    }

    public int visualToLogical(int visualIndex) {

        if (fCharLogicalOrder == null) {
	    return visualIndex;
	}

	if (fCharVisualOrder == null) {
	    fCharVisualOrder = BidiUtils.createInverseMap(fCharLogicalOrder);
	}
 
        return fCharVisualOrder[visualIndex];
    }

    public int logicalToVisual(int logicalIndex) {

        return (fCharLogicalOrder == null)?
            logicalIndex : fCharLogicalOrder[logicalIndex];
    }

    public byte getCharLevel(int logicalIndex) {

        return fCharLevels==null? 0 : fCharLevels[logicalIndex];
    }

    public boolean isCharLTR(int logicalIndex) {

        return (getCharLevel(logicalIndex) & 0x1) == 0;
    }

    public int getCharType(int logicalIndex) {

        return Character.getType(fChars[logicalIndex + fCharsStart]);
    }

    public boolean isCharSpace(int logicalIndex) {

        return Character.isSpaceChar(fChars[logicalIndex + fCharsStart]);
    }

    public boolean isCharWhitespace(int logicalIndex) {

        return Character.isWhitespace(fChars[logicalIndex + fCharsStart]);
    }

    public float getCharAngle(int logicalIndex) {

        return getCoreMetricsAt(logicalIndex).italicAngle;
    }

    public CoreMetrics getCoreMetricsAt(int logicalIndex) {

        if (logicalIndex < 0) {
            throw new IllegalArgumentException("Negative logicalIndex.");
        }

        if (logicalIndex > fCharsLimit - fCharsStart) {
            throw new IllegalArgumentException("logicalIndex too large.");
        }

        int currentTlc = 0;
        int tlcStart = 0;
        int tlcLimit = 0;

        do {
            tlcLimit += fComponents[currentTlc].getNumCharacters();
            if (tlcLimit > logicalIndex) {
                break;
            }
            ++currentTlc;
            tlcStart = tlcLimit;
        } while(currentTlc < fComponents.length);

        return fComponents[currentTlc].getCoreMetrics();
    }

    public float getCharAscent(int logicalIndex) {

        return getCoreMetricsAt(logicalIndex).ascent;
    }

    public float getCharDescent(int logicalIndex) {

        return getCoreMetricsAt(logicalIndex).descent;
    }

    public float getCharShift(int logicalIndex) {

        return getCoreMetricsAt(logicalIndex).ssOffset;
    }

    private float applyFunctionAtIndex(int logicalIndex, Function f) {

        if (logicalIndex < 0) {
            throw new IllegalArgumentException("Negative logicalIndex.");
        }

        int tlcStart = 0;

        for(int i=0; i < fComponents.length; i++) {

            int tlcLimit = tlcStart + fComponents[i].getNumCharacters();
            if (tlcLimit > logicalIndex) {
                return f.computeFunction(this, i, logicalIndex - tlcStart);
            }
            else {
                tlcStart = tlcLimit;
            }
        }

        throw new IllegalArgumentException("logicalIndex too large.");
    }

    public float getCharAdvance(int logicalIndex) {

        return applyFunctionAtIndex(logicalIndex, fgAdvanceF);
    }

    public float getCharXPosition(int logicalIndex) {

        return applyFunctionAtIndex(logicalIndex, fgXPositionF);
    }

    public float getCharYPosition(int logicalIndex) {

        return applyFunctionAtIndex(logicalIndex, fgYPositionF);
    }

    public float getCharLinePosition(int logicalIndex) {

        return getCharXPosition(logicalIndex);
    }

    public float getCharLinePosition(int logicalIndex, boolean leading) {
        Function f = isCharLTR(logicalIndex) == leading ? fgXPositionF : fgPosAdvF;
        return applyFunctionAtIndex(logicalIndex, f);
    }

    public boolean caretAtOffsetIsValid(int offset) {

        if (offset < 0) {
            throw new IllegalArgumentException("Negative offset.");
        }

        int tlcStart = 0;

        for(int i=0; i < fComponents.length; i++) {

            int tlcLimit = tlcStart + fComponents[i].getNumCharacters();
            if (tlcLimit > offset) {
                return fComponents[i].caretAtOffsetIsValid(offset-tlcStart);
            }
            else {
                tlcStart = tlcLimit;
            }
        }

        throw new IllegalArgumentException("logicalIndex too large.");
    }

    public Rectangle2D getCharBounds(int logicalIndex) {

        if (logicalIndex < 0) {
            throw new IllegalArgumentException("Negative logicalIndex.");
        }

        int tlcStart = 0;

        for (int i=0; i < fComponents.length; i++) {

            int tlcLimit = tlcStart + fComponents[i].getNumCharacters();
            if (tlcLimit > logicalIndex) {

                TextLineComponent tlc = fComponents[i];
                int indexInTlc = logicalIndex - tlcStart;
                Rectangle2D chBounds = tlc.getCharVisualBounds(indexInTlc);

                int vi = fComponentVisualOrder == null ? i : fComponentVisualOrder[i];
                chBounds.setRect(chBounds.getX() + locs[vi * 2],
                                 chBounds.getY() + locs[vi * 2 + 1],
                                 chBounds.getWidth(),
                                 chBounds.getHeight());
                return chBounds;
            }
            else {
                tlcStart = tlcLimit;
            }
        }

        throw new IllegalArgumentException("logicalIndex too large.");
    }

    private float getComponentShift(int index) {
	CoreMetrics cm = fComponents[index].getCoreMetrics();
	return cm.effectiveBaselineOffset(fBaselineOffsets);
    }

    public void draw(Graphics2D g2, float x, float y) {

        for (int i = 0, n = 0; i < fComponents.length; i++, n += 2) {
            int vi = fComponentVisualOrder==null? i : fComponentVisualOrder[i];
            TextLineComponent tlc = fComponents[vi];
            tlc.draw(g2, locs[n] + x, locs[n+1] + y);
        }
    }

    /** return the union of the visual bounds of all the components */

    public Rectangle2D getBounds() {

        float left = Float.MAX_VALUE, right = -Float.MAX_VALUE;
        float top = Float.MAX_VALUE, bottom = -Float.MAX_VALUE;

        for (int i=0, n = 0; i < fComponents.length; i++, n += 2) {
            int vi = fComponentVisualOrder==null? i : fComponentVisualOrder[i];
            TextLineComponent tlc = fComponents[vi];

            Rectangle2D tlcBounds = tlc.getVisualBounds();
            float x = locs[n];
            float y = locs[n+1];

            left = Math.min(left, x + (float)tlcBounds.getX());
            right = Math.max(right, x + (float)tlcBounds.getMaxX());

            top = Math.min(top, y + (float)tlcBounds.getY());
            bottom = Math.max(bottom, y + (float)tlcBounds.getMaxY());
        }

        return new Rectangle2D.Float(left, top, right-left, bottom-top);
    }

    public Rectangle2D getItalicBounds() {
        
        float left = Float.MAX_VALUE, right = -Float.MAX_VALUE;
        float top = Float.MAX_VALUE, bottom = -Float.MAX_VALUE;

        for (int i=0, n = 0; i < fComponents.length; i++, n += 2) {
            int vi = fComponentVisualOrder==null? i : fComponentVisualOrder[i];
            TextLineComponent tlc = fComponents[vi];

            Rectangle2D tlcBounds = tlc.getItalicBounds();
            float x = locs[n];
            float y = locs[n+1];

            left = Math.min(left, x + (float)tlcBounds.getX());
            right = Math.max(right, x + (float)tlcBounds.getMaxX());

            top = Math.min(top, y + (float)tlcBounds.getY());
            bottom = Math.max(bottom, y + (float)tlcBounds.getMaxY());
        }

        return new Rectangle2D.Float(left, top, right-left, bottom-top);
    }

    public Shape getOutline(AffineTransform tx) {

        GeneralPath dstShape = new GeneralPath(GeneralPath.WIND_NON_ZERO);

        for (int i=0, n = 0; i < fComponents.length; i++, n += 2) {
            int vi = fComponentVisualOrder==null? i : fComponentVisualOrder[i];
            TextLineComponent tlc = fComponents[vi];

            dstShape.append(tlc.getOutline(locs[n], locs[n+1]), false);
        }

        if (tx != null) {
            dstShape.transform(tx);
        }
        return dstShape;
    }

    public int hashCode() {
        return (fComponents.length << 16) ^
                    (fComponents[0].hashCode() << 3) ^ (fCharsLimit-fCharsStart);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < fComponents.length; i++) {
            buf.append(fComponents[i]);
        }

        return buf.toString();
    }

    /**
     * Create a TextLine from the text.  The Font must be able to
     * display all of the text.
     * attributes==null is equivalent to using an empty Map for
     * attributes
     */
    public static TextLine fastCreateTextLine(FontRenderContext frc,
                                              char[] chars,
                                              Font font,
                                              CoreMetrics lm,
                                              Map attributes) {

        boolean isDirectionLTR = true;
        byte[] levels = null;
        int[] charsLtoV = null;
        Bidi bidi = null;
        int characterCount = chars.length;

        boolean requiresBidi = false;
        boolean directionKnown = false;
        byte[] embs = null;
        if (attributes != null) {
          try {
            Boolean runDirection = (Boolean)attributes.get(TextAttribute.RUN_DIRECTION);
            if (runDirection != null) {
              directionKnown = true;
              isDirectionLTR = TextAttribute.RUN_DIRECTION_LTR.equals(runDirection);
              requiresBidi = !isDirectionLTR;
            }
          }
          catch (ClassCastException e) {
          }

          try {
            Integer embeddingLevel = (Integer)attributes.get(TextAttribute.BIDI_EMBEDDING);
            if (embeddingLevel != null) {
              int intLevel = embeddingLevel.intValue();
              if (intLevel >= -61 && intLevel < 62) {
                byte level = (byte)intLevel;
                requiresBidi = true;
                embs = new byte[characterCount];
                for (int i = 0; i < embs.length; ++i) {
                  embs[i] = level;
                }
              }
            }
          }
          catch (ClassCastException e) {
          }
        }

        if (!requiresBidi) {
	    requiresBidi = Bidi.requiresBidi(chars, 0, chars.length);
        }

        if (requiresBidi) {
	  int bidiflags = Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT;
          if (directionKnown) {
	      if (isDirectionLTR) {
		  bidiflags = Bidi.DIRECTION_LEFT_TO_RIGHT;
	      } else {
		  bidiflags = Bidi.DIRECTION_RIGHT_TO_LEFT;
	      }
          }

          bidi = new Bidi(chars, 0, embs, 0, chars.length, bidiflags);
	  if (!bidi.isLeftToRight()) {
	      levels = BidiUtils.getLevels(bidi);
	      int[] charsVtoL = BidiUtils.createVisualToLogicalMap(levels);
	      charsLtoV = BidiUtils.createInverseMap(charsVtoL);
	      isDirectionLTR = bidi.baseIsLeftToRight();
	  }
        }

        Decoration decorator;
        if (attributes != null) {
            decorator = Decoration.getDecoration(StyledParagraph.addInputMethodAttrs(attributes));
        }
        else {
            decorator = Decoration.getPlainDecoration();
        }
	int layoutFlags = 0; // no extra info yet, bidi determines run and line direction
        TextLabelFactory factory = new TextLabelFactory(frc, chars, bidi, layoutFlags);

        TextLineComponent[] components = new TextLineComponent[1];
        
        components = createComponentsOnRun(0, chars.length,
                                           chars,
                                           charsLtoV, levels,
                                           factory, font, lm,
                                           frc,
                                           decorator,
                                           components,
                                           0);
                                           
        int numComponents = components.length;
        while (components[numComponents-1] == null) {
            numComponents -= 1;
        }
        
        if (numComponents != components.length) {
            TextLineComponent[] temp = new TextLineComponent[numComponents];
            System.arraycopy(components, 0, temp, 0, numComponents);
            components = temp;
        }
        
        return new TextLine(components, lm.baselineOffsets,
                            chars, 0, chars.length, charsLtoV, levels, isDirectionLTR);
    }

    private static TextLineComponent[] expandArray(TextLineComponent[] orig) {

        TextLineComponent[] newComponents = new TextLineComponent[orig.length + 8];
        System.arraycopy(orig, 0, newComponents, 0, orig.length);

        return newComponents;
    }

    /**
     * Returns an array in logical order of the TextLineComponents on
     * the text in the given range, with the given attributes.
     */
    public static TextLineComponent[] createComponentsOnRun(int runStart,
                                                            int runLimit,
                                                            char[] chars,
                                                            int[] charsLtoV,
                                                            byte[] levels,
                                                            TextLabelFactory factory,
                                                            Font font,
                                                            CoreMetrics cm,
                                                            FontRenderContext frc,
                                                            Decoration decorator,
                                                            TextLineComponent[] components,
                                                            int numComponents) {

        int pos = runStart;
        do {
            int chunkLimit = firstVisualChunk(charsLtoV, levels, pos, runLimit); // <= displayLimit

            do {
                int startPos = pos;
                int lmCount;
                
                if (cm == null) {
                    LineMetrics lineMetrics = font.getLineMetrics(chars, startPos, chunkLimit, frc);
                    cm = CoreMetrics.get(lineMetrics);
                    lmCount = lineMetrics.getNumChars();
                }
                else {
                    lmCount = (chunkLimit-startPos);
                }

                TextLineComponent nextComponent =
                    factory.createExtended(font, cm, decorator, startPos, startPos + lmCount);

                ++numComponents;
                if (numComponents >= components.length) {
                    components = expandArray(components);
                }

                components[numComponents-1] = nextComponent;
                
                pos += lmCount;
            } while (pos < chunkLimit);

        } while (pos < runLimit);
        
        return components;
    }

    /**
     * Returns an array (in logical order) of the TextLineComponents representing
     * the text.  The components are both logically and visually contiguous.
     */
    public static TextLineComponent[] getComponents(StyledParagraph styledParagraph,
                                                    char[] chars,
                                                    int textStart,
                                                    int textLimit,
                                                    int[] charsLtoV,
                                                    byte[] levels,
                                                    TextLabelFactory factory) {

        FontRenderContext frc = factory.getFontRenderContext();

        int numComponents = 0;
        TextLineComponent[] tempComponents = new TextLineComponent[1];

        int pos = textStart;
        do {
            int runLimit = Math.min(styledParagraph.getRunLimit(pos), textLimit);
            
            Decoration decorator = styledParagraph.getDecorationAt(pos);

            Object graphicOrFont = styledParagraph.getFontOrGraphicAt(pos);

            if (graphicOrFont instanceof GraphicAttribute) {
                
                GraphicAttribute graphicAttribute = (GraphicAttribute) graphicOrFont;
                do {
                    int chunkLimit = firstVisualChunk(charsLtoV, levels,
                                    pos, runLimit);

                    GraphicComponent nextGraphic =
                            new GraphicComponent(graphicAttribute, decorator, charsLtoV, levels, pos, chunkLimit);
                    pos = chunkLimit;

                    ++numComponents;
                    if (numComponents >= tempComponents.length) {
                        tempComponents = expandArray(tempComponents);
                    }

                    tempComponents[numComponents-1] = nextGraphic;

                } while(pos < runLimit);
            }
            else {
                Font font = (Font) graphicOrFont;

                tempComponents = createComponentsOnRun(pos, runLimit,
                                                        chars,
                                                        charsLtoV, levels,
                                                        factory, font, null,
                                                        frc,
                                                        decorator,
                                                        tempComponents,
                                                        numComponents);
                pos = runLimit;
                numComponents = tempComponents.length;
                while (tempComponents[numComponents-1] == null) {
                    numComponents -= 1;
                }
            }

        } while (pos < textLimit);

        TextLineComponent[] components;
        if (tempComponents.length == numComponents) {
            components = tempComponents;
        }
        else {
            components = new TextLineComponent[numComponents];
            System.arraycopy(tempComponents, 0, components, 0, numComponents);
        }

        return components;
    }

    /**
     * Create a TextLine from the Font and character data over the
     * range.  The range is relative to both the StyledParagraph and the
     * character array.
     */
    public static TextLine createLineFromText(char[] chars,
                                              StyledParagraph styledParagraph,
                                              TextLabelFactory factory,
                                              boolean isDirectionLTR,
                                              float[] baselineOffsets) {

        factory.setLineContext(0, chars.length);

        Bidi lineBidi = factory.getLineBidi();
        int[] charsLtoV = null;
        byte[] levels = null;

        if (lineBidi != null) {
            levels = BidiUtils.getLevels(lineBidi);
	    int[] charsVtoL = BidiUtils.createVisualToLogicalMap(levels);
            charsLtoV = BidiUtils.createInverseMap(charsVtoL);
        }

        TextLineComponent[] components =
            getComponents(styledParagraph, chars, 0, chars.length, charsLtoV, levels, factory);

        return new TextLine(components, baselineOffsets,
                            chars, 0, chars.length, charsLtoV, levels, isDirectionLTR);
    }

    /**
     * Compute the components order from the given components array and
     * logical-to-visual character mapping.  May return null if canonical.
     */
    private static int[] computeComponentOrder(TextLineComponent[] components,
                                               int[] charsLtoV) {

        /*
         * Create a visual ordering for the glyph sets.  The important thing
         * here is that the values have the proper rank with respect to
         * each other, not the exact values.  For example, the first glyph
         * set that appears visually should have the lowest value.  The last
         * should have the highest value.  The values are then normalized
         * to map 1-1 with positions in glyphs.
         *
         */
        int[] componentOrder = null;
        if (charsLtoV != null && components.length > 1) {
            componentOrder = new int[components.length];
            int gStart = 0;
            for (int i = 0; i < components.length; i++) {
                componentOrder[i] = charsLtoV[gStart];
                gStart += components[i].getNumCharacters();
            }

            componentOrder = BidiUtils.createContiguousOrder(componentOrder);
            componentOrder = BidiUtils.createInverseMap(componentOrder);
        }
        return componentOrder;
    }


    /**
     * Create a TextLine from the text.  chars is just the text in the iterator.
     */
    public static TextLine standardCreateTextLine(FontRenderContext frc,
                                                  AttributedCharacterIterator text,
                                                  char[] chars,
                                                  float[] baselineOffsets) {

        StyledParagraph styledParagraph = new StyledParagraph(text, chars);
        Bidi bidi = new Bidi(text);
	if (bidi.isLeftToRight()) {
	    bidi = null;
	}
        int layoutFlags = 0; // no extra info yet, bidi determines run and line direction
        TextLabelFactory factory = new TextLabelFactory(frc, chars, bidi, layoutFlags);

        boolean isDirectionLTR = true;
        if (bidi != null) {
            isDirectionLTR = bidi.baseIsLeftToRight();
        }
        return createLineFromText(chars, styledParagraph, factory, isDirectionLTR, baselineOffsets);
    }



    /*
     * A utility to get a range of text that is both logically and visually
     * contiguous.
     * If the entire range is ok, return limit, otherwise return the first
     * directional change after start.  We could do better than this, but
     * it doesn't seem worth it at the moment.
    private static int firstVisualChunk(int order[], byte direction[],
                                        int start, int limit)
    {
        if (order != null) {
            int min = order[start];
            int max = order[start];
            int count = limit - start;
            for (int i = start + 1; i < limit; i++) {
                min = Math.min(min, order[i]);
                max = Math.max(max, order[i]);
                if (max - min >= count) {
                    if (direction != null) {
                        byte baseLevel = direction[start];
                        for (int j = start + 1; j < i; j++) {
                            if (direction[j] != baseLevel) {
                                return j;
                            }
                        }
                    }
                    return i;
                }
            }
        }
        return limit;
    }
     */
    
    /**
     * When this returns, the ACI's current position will be at the start of the
     * first run which does NOT contain a GraphicAttribute.  If no such run exists
     * the ACI's position will be at the end, and this method will return false.
     */
    static boolean advanceToFirstFont(AttributedCharacterIterator aci) {
        
        for (char ch = aci.first(); ch != aci.DONE; ch = aci.setIndex(aci.getRunLimit())) {

            if (aci.getAttribute(TextAttribute.CHAR_REPLACEMENT) == null) {
                return true;
            }
        }
        
        return false;
    }
    
    static float[] getNormalizedOffsets(float[] baselineOffsets, byte baseline) {
        
        if (baselineOffsets[baseline] != 0) {
            float base = baselineOffsets[baseline];
            float[] temp = new float[baselineOffsets.length];
            for (int i = 0; i < temp.length; i++)
                temp[i] = baselineOffsets[i] - base;
            baselineOffsets = temp;
        }
        return baselineOffsets;
    }
    
    static Font getFontAtCurrentPos(AttributedCharacterIterator aci) {
        
        Object value = aci.getAttribute(TextAttribute.FONT);
        if (value != null) {
            return (Font) value;
        }
        if (aci.getAttribute(TextAttribute.FAMILY) != null) {
            return Font.getFont(aci.getAttributes());
        }

        int ch = CodePointIterator.create(aci).next();
	if (ch != CodePointIterator.DONE) {
	    FontResolver resolver = FontResolver.getInstance();
	    return resolver.getFont(resolver.getFontIndex(ch), aci.getAttributes());
	}
	return null;
    }
    
    /**
     * Utility method for getting justification ratio from attributes.
     */
    static float getJustifyRatio(Map attributes) {
        
        Object value = attributes.get(TextAttribute.JUSTIFICATION);
        
        if (value == null) {
            return 1;
        }
        
        float justifyRatio = ((Float)value).floatValue();
        if (justifyRatio < 0) {
            justifyRatio = 0;
        }
        else if (justifyRatio > 1) {
            justifyRatio = 1;
        }
        
        return justifyRatio;
    }

  /*
   * The new version requires that chunks be at the same level.
   */
    private static int firstVisualChunk(int order[], byte direction[],
                                        int start, int limit)
    {
        if (order != null && direction != null) {
          byte dir = direction[start];
          while (++start < limit && direction[start] == dir) {}
          return start;
        }
        return limit;
    }

  /*
   * create a new line with characters between charStart and charLimit
   * justified using the provided width and ratio.
   */
    public TextLine getJustifiedLine(float justificationWidth, float justifyRatio, int justStart, int justLimit) {

        TextLineComponent[] newComponents = new TextLineComponent[fComponents.length];
        System.arraycopy(fComponents, 0, newComponents, 0, fComponents.length);

        float leftHang = 0;
        float adv = 0;
        float justifyDelta = 0;
        boolean rejustify = false;
        do {
            adv = getAdvanceBetween(newComponents, 0, characterCount());

            // all characters outside the justification range must be in the base direction
            // of the layout, otherwise justification makes no sense.

            float justifyAdvance = getAdvanceBetween(newComponents, justStart, justLimit);

            // get the actual justification delta
            justifyDelta = (justificationWidth - justifyAdvance) * justifyRatio;

            // generate an array of GlyphJustificationInfo records to pass to
            // the justifier.  Array is visually ordered.

            // get positions that each component will be using
            int[] infoPositions = new int[newComponents.length];
            int infoCount = 0;
            for (int visIndex = 0; visIndex < newComponents.length; visIndex++) {
                int logIndex = fComponentVisualOrder == null ? visIndex : fComponentVisualOrder[visIndex];
                infoPositions[logIndex] = infoCount;
                infoCount += newComponents[logIndex].getNumJustificationInfos();
            }
            GlyphJustificationInfo[] infos = new GlyphJustificationInfo[infoCount];

            // get justification infos
            int compStart = 0;
            for (int i = 0; i < newComponents.length; i++) {
                TextLineComponent comp = newComponents[i];
                int compLength = comp.getNumCharacters();
                int compLimit = compStart + compLength;
                if (compLimit > justStart) {
                    int rangeMin = Math.max(0, justStart - compStart);
                    int rangeMax = Math.min(compLength, justLimit - compStart);
                    comp.getJustificationInfos(infos, infoPositions[i], rangeMin, rangeMax);

                    if (compLimit >= justLimit) {
                        break;
                    }
                }
            }

            // records are visually ordered, and contiguous, so start and end are
            // simply the places where we didn't fetch records
            int infoStart = 0;
            int infoLimit = infoCount;
            while (infoStart < infoLimit && infos[infoStart] == null) {
                ++infoStart;
            }

            while (infoLimit > infoStart && infos[infoLimit - 1] == null) {
                --infoLimit;
            }

            // invoke justifier on the records
            TextJustifier justifier = new TextJustifier(infos, infoStart, infoLimit);

            float[] deltas = justifier.justify(justifyDelta);

            boolean canRejustify = rejustify == false;
            boolean wantRejustify = false;
            boolean[] flags = new boolean[1];

            // apply justification deltas
            compStart = 0;
            for (int i = 0; i < newComponents.length; i++) {
                TextLineComponent comp = newComponents[i];
                int compLength = comp.getNumCharacters();
                int compLimit = compStart + compLength;
                if (compLimit > justStart) {
                    int rangeMin = Math.max(0, justStart - compStart);
                    int rangeMax = Math.min(compLength, justLimit - compStart);
                    newComponents[i] = comp.applyJustificationDeltas(deltas, infoPositions[i] * 2, flags);

                    wantRejustify |= flags[0];

                    if (compLimit >= justLimit) {
                        break;
                    }
                }
            }

            rejustify = wantRejustify && !rejustify; // only make two passes
        } while (rejustify);

        return new TextLine(newComponents, fBaselineOffsets, fChars, fCharsStart,
                            fCharsLimit, fCharLogicalOrder, fCharLevels,
                            fIsDirectionLTR);
    }

    // return the sum of the advances of text between the logical start and limit
    public static float getAdvanceBetween(TextLineComponent[] components, int start, int limit) {
        float advance = 0;

        int tlcStart = 0;
        for(int i = 0; i < components.length; i++) {
            TextLineComponent comp = components[i];

            int tlcLength = comp.getNumCharacters();
            int tlcLimit = tlcStart + tlcLength;
            if (tlcLimit > start) {
                int measureStart = Math.max(0, start - tlcStart);
                int measureLimit = Math.min(tlcLength, limit - tlcStart);
                advance += comp.getAdvanceBetween(measureStart, measureLimit);
                if (tlcLimit >= limit) {
                    break;
                }
            }

            tlcStart = tlcLimit;
        }

        return advance;
    }
}
