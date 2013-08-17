/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * (C) Copyright IBM Corp. 1998, All Rights Reserved
 *
 */

package java.awt.font;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Toolkit;

import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import java.awt.im.InputMethodHighlight;

import java.text.CharacterIterator;
import java.text.AttributedCharacterIterator;
import java.text.Annotation;

import java.util.Map;
import java.util.Hashtable;

import sun.awt.font.Bidi;
import sun.awt.font.ExtendedTextLabel;
import sun.awt.font.ExtendedTextLabelComponent;
import sun.awt.font.GraphicComponent;
import sun.awt.font.TextLabelFactory;
import sun.awt.font.TextLineComponent;

import sun.java2d.SunGraphicsEnvironment;

// TO DO:  get rid of FontSource - use something else or go back to ACI

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

    private abstract static class Function {

        abstract float computeFunction(TextLine line,
                                       int componentIndex,
                                       int indexInArray);
    }

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

            // add up Component advances before componentIndex
            // Note how we have to get the component advances.  It would
            // be better to have a getAdvance mathod on component.
            float componentsAdvance = 0;
            if (line.fComponentVisualOrder == null) {
                for (int i=0; i < componentIndex; i++) {
                    Rectangle2D lb = line.fComponents[i].getLogicalBounds();
                    componentsAdvance += (float)lb.getWidth();
                }
            }
            else {
                for (int i=0; line.fComponentVisualOrder[i] != componentIndex; i++) {
                    int index = line.fComponentVisualOrder[i];
                    Rectangle2D lb = line.fComponents[index].getLogicalBounds();
                    componentsAdvance += (float)lb.getWidth();
                }
            }

            TextLineComponent tlc = line.fComponents[componentIndex];
            return componentsAdvance + tlc.getCharX(indexInArray);
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

        if (fMetrics == null) {

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

                Rectangle2D lb = tlc.getLogicalBounds();
                advance += (float)lb.getWidth();

                byte baseline = (byte) tlc.getLineMetrics().getBaselineIndex();
                LineMetrics lm = tlc.getLineMetrics();

                if (baseline >= 0) {
                    float baselineOffset = fBaselineOffsets[baseline];

                    ascent = Math.max(ascent, -baselineOffset + lm.getAscent());

                    float gd = baselineOffset + lm.getDescent();
                    descent = Math.max(descent, gd);

                    leading = Math.max(leading, gd + lm.getLeading());
                }
                else {
                    fitTopAndBottomGraphics = true;
                    float graphicHeight = lm.getAscent() + lm.getDescent();
                    float graphicHeightWithLeading = graphicHeight + lm.getLeading();
                    maxGraphicHeight = Math.max(maxGraphicHeight,
                                                graphicHeight);
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

            fMetrics = new TextLineMetrics(ascent, descent, leading, advance);
        }

        return fMetrics;
    }

    public int visualToLogical(int visualIndex) {

        if (fCharLogicalOrder == null) {
            return visualIndex;
        }

        if (fCharVisualOrder == null) {
            fCharVisualOrder = Bidi.getInverseOrder(fCharLogicalOrder);
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

        if (logicalIndex < 0) {
            throw new IllegalArgumentException("Negative logicalIndex.");
        }

        if (logicalIndex > fCharsLimit - fCharsStart) {
            throw new IllegalArgumentException("logicalIndex too large.");
        }

        int currentTlc = 0;
        int tlcLimit = 0;

        do {
            tlcLimit += fComponents[currentTlc].getNumCharacters();
            if (tlcLimit > logicalIndex) {
                break;
            }
            ++currentTlc;
        } while(currentTlc < fComponents.length);

        return fComponents[currentTlc].getItalicAngle();
    }

    private LineMetrics getLineMetricsAt(int logicalIndex) {

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

        return fComponents[currentTlc].getLineMetrics();
    }

    public float getCharAscent(int logicalIndex) {

        return getLineMetricsAt(logicalIndex).getAscent();
    }

    public float getCharDescent(int logicalIndex) {

        return getLineMetricsAt(logicalIndex).getDescent();
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

                // now accumulate advances of visually preceding tlc's
                float componentsAdvance = 0;
                if (fComponentVisualOrder == null) {
                    for (int j=0; j < i; j++) {
                        Rectangle2D lb = fComponents[j].getLogicalBounds();
                        componentsAdvance += (float)lb.getWidth();
                    }
                }
                else {
                    for (int j=0; fComponentVisualOrder[j] != i; j++) {
                        int index = fComponentVisualOrder[j];
                        Rectangle2D lb = fComponents[index].getLogicalBounds();
                        componentsAdvance += (float)lb.getWidth();
                    }
                }

                chBounds.setRect(chBounds.getX() + componentsAdvance,
                                 chBounds.getY(),
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

        byte baseline = (byte) fComponents[index].getLineMetrics().getBaselineIndex();
        if (baseline >= 0) {
            return fBaselineOffsets[baseline];
        }
        else {
            TextLineMetrics lineMetrics = getMetrics();

            // don't bother to get chars right here:
            LineMetrics compMetrics = fComponents[index].getLineMetrics();

            if (baseline == GraphicAttribute.TOP_ALIGNMENT) {
                return compMetrics.getAscent() - lineMetrics.ascent;
            }
            else {
                return lineMetrics.descent - compMetrics.getDescent();
            }
        }
    }

    public void draw(Graphics2D g2, float x, float y) {

        float nx = x;

        for (int i = 0; i < fComponents.length; i++) {

            int index = fComponentVisualOrder==null? i : fComponentVisualOrder[i];
            TextLineComponent tlc = fComponents[index];

            float shift = getComponentShift(index);

            tlc.draw(g2, nx, y + shift);

            if (i != fComponents.length-1) {
                Rectangle2D lb = tlc.getLogicalBounds();
                nx += (float)lb.getWidth();
            }
        }
    }

    public Rectangle2D getBounds() {

        float tlcAdvance = 0;
        float left = Float.MAX_VALUE, right = Float.MIN_VALUE;
        float top = Float.MAX_VALUE, bottom = Float.MIN_VALUE;

        for (int i=0; i < fComponents.length; i++) {

            int index = fComponentVisualOrder==null? i : fComponentVisualOrder[i];
            TextLineComponent tlc = fComponents[index];

            Rectangle2D tlcBounds = tlc.getVisualBounds();

            left = Math.min(left, (float) tlcBounds.getX() + tlcAdvance);
            right = Math.max(right, (float) tlcBounds.getMaxX() + tlcAdvance);

            float shift = getComponentShift(index);

            top = Math.min(top, (float) tlcBounds.getY()+shift);
            bottom = Math.max(bottom, (float) tlcBounds.getMaxY()+shift);

            Rectangle2D lb = tlc.getLogicalBounds();
            tlcAdvance += (float)lb.getWidth();
        }

        return new Rectangle2D.Float(left, top, right-left, bottom-top);
    }

    public Shape getOutline(AffineTransform tx) {

        GeneralPath dstShape = new GeneralPath(GeneralPath.WIND_NON_ZERO);

        float x = 0;
        for (int i=0; i < fComponents.length; i++) {

            int index = fComponentVisualOrder==null? i : fComponentVisualOrder[i];
            TextLineComponent tlc = fComponents[index];

            float shift = getComponentShift(index);
            dstShape.append(tlc.getOutline(x, shift), false);

            Rectangle2D lb = tlc.getLogicalBounds();
            x += (float)lb.getWidth();
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
                                              char[] text,
                                              int start,
                                              int limit,
                                              Font font,
                                              LineMetrics lm,
                                              Map attributes) {

        boolean isDirectionLTR = true;
        byte[] levels = null;
        int[] charsLtoV = null;
        Bidi bidi = null;
        char[] chars;
        int characterCount = limit - start;

        if (start != 0) {
            chars = new char[characterCount];
            System.arraycopy(text, start, chars, 0, characterCount);
        }
        else {
            chars = text;
        }
        
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
              if (intLevel >= -15 && intLevel < 16) {
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
          for (int i = 0; i < chars.length; i++) {
            if (Bidi.requiresBidi(chars[i])) {
              requiresBidi = true;
              break;
            }
          }
        }

        if (requiresBidi) {
          if (!directionKnown) {
            isDirectionLTR = Bidi.defaultIsLTR(chars, 0, characterCount);
          }
          if (embs == null) {
            embs = Bidi.getEmbeddingArray(chars, isDirectionLTR);
          }
          bidi = new Bidi(chars, embs, isDirectionLTR);
          levels = bidi.getLevels();
          charsLtoV = bidi.getLogicalToVisualMap();
        }

        if (attributes != null) {
            attributes = addInputMethodAttrs(attributes);
        }

        TextLabelFactory factory = new TextLabelFactory(frc, chars, bidi);

        TextLineComponent[] components;

        // one component per level
        if (bidi == null || bidi.getLevelLimit(0) == characterCount) {
          components = new TextLineComponent[1];

          ExtendedTextLabel label =
            factory.createExtended(font, lm, 0, characterCount);

          components[0] = new ExtendedTextLabelComponent(label, attributes);
        } else {
          int count = 0;
          int pos = 0;
          while (pos < characterCount) {
            pos = bidi.getLevelLimit(pos);
            ++count;
          }
          components = new TextLineComponent[count];

          count = 0;
          pos = 0;
          while (pos < characterCount) {
            int newpos = bidi.getLevelLimit(pos);
            ExtendedTextLabel label = factory.createExtended(font, lm, pos, newpos);
            components[count] = new ExtendedTextLabelComponent(label, attributes);
            ++count;
            pos = newpos;
          }
        }

        return new TextLine(components, lm.getBaselineOffsets(),
                            text, start, limit, charsLtoV, levels, isDirectionLTR);
    }

    static abstract class FontSource {

        // 0-based
        abstract int getLength();

        abstract int getRunLimit(int pos);

        /**
         * Return graphic at position.  Null if no graphic.
         */
        abstract GraphicAttribute graphicAt(int pos);

        /**
         * Return font at position.  Does not try to substitute
         * another font.  Null if no font explicitly on the text.
         */
        abstract Font fontAt(int pos);

        /**
         * Compute best font for text.  Should use
         * SunGraphicsEnvironment.getBestFontFor.
         */
        abstract Font getBestFontAt(int pos);

        /**
         * Get attributes.
         */
        abstract Map attributesAt(int pos);
    }

    static class ACIFontSource extends FontSource {

        private AttributedCharacterIterator fIter;
        private int fIterStart;

        public ACIFontSource(AttributedCharacterIterator iter) {

            fIter = iter;
            fIterStart = iter.getBeginIndex();
        }

        int getLength() {
            return fIter.getEndIndex() - fIterStart;
        }

        int getRunLimit(int pos) {
            fIter.setIndex(pos + fIterStart);
            return fIter.getRunLimit() - fIterStart;
        }

        GraphicAttribute graphicAt(int pos) {
            fIter.setIndex(pos + fIterStart);
            return (GraphicAttribute)
                    fIter.getAttribute(TextAttribute.CHAR_REPLACEMENT);
        }

        Font fontAt(int pos) {
            fIter.setIndex(pos + fIterStart);
            return (Font) fIter.getAttribute(TextAttribute.FONT);
        }


        Font getBestFontAt(int pos) {
            int iterPos = pos + fIterStart;
            fIter.setIndex(iterPos);
            return SunGraphicsEnvironment.getBestFontFor(
                                        fIter, iterPos, fIter.getRunLimit());
        }

        Map attributesAt(int pos) {
            fIter.setIndex(pos + fIterStart);
            return fIter.getAttributes();
        }
    }

    // for getComponents() use - pretty specialized
    private static TextLineComponent[] expandArrays(TextLineComponent[] orig) {

        TextLineComponent[] newComponents = new TextLineComponent[orig.length + 8];
        System.arraycopy(orig, 0, newComponents, 0, orig.length);

        return newComponents;
    }

    private static Map addInputMethodAttrs(Map oldStyles) {

        Object value = oldStyles.get(TextAttribute.INPUT_METHOD_HIGHLIGHT);

        try {
            if (value != null) {
                if (value instanceof Annotation) {
                    value = ((Annotation)value).getValue();
                }

                InputMethodHighlight hl;
                hl = (InputMethodHighlight) value;
                
                Map imStyles = null;
                try {
                    imStyles = hl.getStyle();
                } catch (NoSuchMethodError e) {
                }
                
                if (imStyles == null) {
                    Toolkit tk = Toolkit.getDefaultToolkit();
                    imStyles = tk.mapInputMethodHighlight(hl);
                }

                if (imStyles != null) {
                    Hashtable newStyles = new Hashtable(5, (float)0.9);
                    newStyles.putAll(oldStyles);

                    newStyles.putAll(imStyles);

                    return newStyles;
                }
            }
        }
        catch(ClassCastException e) {
        }

        return oldStyles;
    }

    /**
     * Returns an array (in logical order) of the glyphsets representing
     * the text.  The glyphsets are both logically and visually contiguous.
     * If varBaselines is not null, then the array of baselines for the
     * TextLineComponents is returned in varBaselines[0].
     */
    public static TextLineComponent[] getComponents(FontSource fontSource,
                                                    char[] chars,
                                                    int textStart,
                                                    int textLimit,
                                                    int[] charsLtoV,
                                                    byte[] levels,
                                                    TextLabelFactory factory) {

        FontRenderContext frc = factory.getFontRenderContext();

        int numComponents = 0;
        TextLineComponent[] tempComponents = new TextLineComponent[8];

        /*
         * text may be inside some larger text, be sure to adjust before
         * accessing arrays, which map zero to the start of the text.
         *
         */
        int pos = textStart;
        do {
            //text.setIndex(pos);
            int runLimit = fontSource.getRunLimit(pos); // <= textLimit
            if (runLimit > textLimit) {
                runLimit = textLimit;
            }

            GraphicAttribute graphicAttribute = fontSource.graphicAt(pos);

            if (graphicAttribute != null) {

                do {
                    int chunkLimit =
                        textStart + firstVisualChunk(charsLtoV, levels,
                                    pos - textStart, runLimit - textStart);

                    Map attrs = fontSource.attributesAt(pos);
                    GraphicComponent nextGraphic =
                            new GraphicComponent(graphicAttribute, attrs, charsLtoV, levels, pos-textStart, chunkLimit-textStart);
                    pos = chunkLimit;

                    ++numComponents;
                    if (numComponents >= tempComponents.length) {
                        tempComponents = expandArrays(tempComponents);
                    }

                    tempComponents[numComponents-1] = nextGraphic;

                } while(pos < runLimit);
            }
            else {
                do {
                    /*
                     * If the client has indicated a font, they're responsible for
                     * ensuring that it can display all the text to which it is
                     * applied.  We won't do anything to handle it.
                     */
                    int displayLimit = runLimit; // default

                    Font font = fontSource.fontAt(pos);

                    if (font == null) {
                        font = fontSource.getBestFontAt(pos);
                        // !!! REMIND dlf 081498
                        // this can cause the same char with the same style to use different fonts
                        // if the limit is less than the entire run.
                        displayLimit = font.canDisplayUpTo(chars, pos, runLimit);
                        if (displayLimit == pos) {
                          ++displayLimit;
                        }
                    }

                    do {
                        int chunkLimit = textStart + firstVisualChunk(charsLtoV,
                                     levels, pos - textStart,
                                     displayLimit - textStart); // <= displayLimit

                        do {
                            Map attrs = fontSource.attributesAt(pos);
                            int startPos = pos;
                            LineMetrics lm = font.getLineMetrics(chars, pos, chunkLimit, frc);
                            pos += lm.getNumChars();

                            ExtendedTextLabel ga =
                                    factory.createExtended(font, lm, startPos, pos);

                            attrs = addInputMethodAttrs(attrs);

                            TextLineComponent nextComponent =
                                    new ExtendedTextLabelComponent(ga, attrs);

                            ++numComponents;
                            if (numComponents >= tempComponents.length) {
                                tempComponents = expandArrays(tempComponents);
                            }

                            tempComponents[numComponents-1] = nextComponent;

                        } while (pos < chunkLimit);

                    } while (pos < displayLimit);

                } while (pos < runLimit);
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
     * range.  The range is relative to both the FontSource and the
     * character array.
     */
    public static TextLine createLineFromText(char[] chars,
                                              int start,
                                              int limit,
                                              FontSource fontSource,
                                              TextLabelFactory factory,
                                              boolean isDirectionLTR,
                                              float[] baselineOffsets) {

        factory.setLineContext(start, limit);

        Bidi lineBidi = factory.getLineBidi();
        int[] charsLtoV = null;
        byte[] levels = null;

        if (lineBidi != null) {
            charsLtoV = lineBidi.getLogicalToVisualMap();
            levels = lineBidi.getLevels();
        }

        TextLineComponent[] components =
            getComponents(fontSource, chars, start, limit, charsLtoV, levels, factory);

        return new TextLine(components, baselineOffsets,
                            chars, start, limit, charsLtoV, levels, isDirectionLTR);
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

            componentOrder = Bidi.getContiguousOrder(componentOrder);
            componentOrder = Bidi.getInverseOrder(componentOrder);
        }
        return componentOrder;
    }

    /**
     * Create a Bidi for the paragraph in <tt>text</tt>.  If <tt>chars</tt>
     * is not null it must be the characters in the paragraph.
     */
    // should this live in Bidi??
    static Bidi createBidiOnParagraph(AttributedCharacterIterator text,
                                      char[] chars) {
    
        final int begin = text.getBeginIndex();
        final int end = text.getEndIndex();
        final int length = end - begin;
        
        if (chars == null) {
            int n = 0;
            chars = new char[length];
            for (char c = text.first(); c != text.DONE; c = text.next()) {
                chars[n++] = c;
            }
        }
        else {
            if (chars.length != length) {
                throw new IllegalArgumentException("chars length is not iter length");
            }
        }
        
        boolean isDirectionLTR = true;
        Bidi bidi = null;

        boolean requiresBidi = false;
        boolean directionKnown = false;
        byte[] embs = null;

        text.first();
        try {
          Boolean runDirection = (Boolean)text.getAttribute(TextAttribute.RUN_DIRECTION);
          if (runDirection != null) {
            directionKnown = true;
            isDirectionLTR = TextAttribute.RUN_DIRECTION_LTR.equals(runDirection);
            requiresBidi = !isDirectionLTR;
          }
        }
        catch (ClassCastException e) {
        }

        int pos = begin;
        byte level = 0;
        byte baselevel = (byte)((directionKnown && !isDirectionLTR) ? 1 : 0);
        do {
          text.setIndex(pos);
          Object embeddingLevel = text.getAttribute(TextAttribute.BIDI_EMBEDDING);
          int newpos = text.getRunLimit(TextAttribute.BIDI_EMBEDDING);

          if (embeddingLevel != null) {
            try {
              int intLevel = ((Integer)embeddingLevel).intValue();
              if (intLevel >= -15 && intLevel < 16) {
                level = (byte)intLevel;
                if (embs == null) {
                  embs = new byte[length];
                  requiresBidi = true;
                  if (!directionKnown) {
                    directionKnown = true;
                    isDirectionLTR = Bidi.defaultIsLTR(chars, 0, length);
                    baselevel = (byte)(isDirectionLTR ? 0 : 1);
                  }
                  if (!isDirectionLTR) {
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
          for (int i = 0; i < length; i++) {
            if (Bidi.requiresBidi(chars[i])) {
              requiresBidi = true;
              break;
            }
          }
        }

        if (requiresBidi) {
          if (!directionKnown) {
            isDirectionLTR = Bidi.defaultIsLTR(chars, 0, length);
          }
          if (embs == null) {
            embs = Bidi.getEmbeddingArray(chars, isDirectionLTR);
          }

          bidi = new Bidi(chars, embs, isDirectionLTR);
        }

        return bidi;        
    }

    /**
     * Create a TextLine from the text.  chars is just the text in the iterator.
     */
    public static TextLine standardCreateTextLine(FontRenderContext frc,
                                                  AttributedCharacterIterator text,
                                                  char[] chars,
                                                  float[] baselineOffsets) {

        FontSource fontSource = new ACIFontSource(text);
        Bidi bidi = createBidiOnParagraph(text, chars);
        
        TextLabelFactory factory = new TextLabelFactory(frc, chars, bidi);

        boolean isDirectionLTR = true;
        if (bidi != null) {
            isDirectionLTR = bidi.isDirectionLTR();
        }
        return createLineFromText(chars, 0, chars.length, fontSource, factory, isDirectionLTR, baselineOffsets);
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
