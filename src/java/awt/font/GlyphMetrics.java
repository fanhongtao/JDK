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

import java.awt.geom.Rectangle2D;

/**
 * The <code>GlyphMetrics</code> class represents infomation for a
 * single glyph.   A glyph is the visual representation of one or more
 * characters.  Many different glyphs can be used to represent a single
 * character or combination of characters.  <code>GlyphMetrics</code>
 * instances are produced by {@link java.awt.Font Font} and are applicable 
 * to a specific glyph in a particular <code>Font</code>.
 * <p>
 * Glyphs are either STANDARD, LIGATURE, COMBINING, or COMPONENT.
 * <ul>
 * <li>STANDARD glyphs are commonly used to represent single characters.
 * <li>LIGATURE glyphs are used to represent sequences of characters.
 * <li>COMPONENT glyphs in a {@link GlyphVector} do not correspond to a
 * particular character in a text model. Instead, COMPONENT glyphs are
 * added for typographical reasons, such as Arabic justification.
 * <li>COMBINING glyphs embellish STANDARD or LIGATURE glyphs, such
 * as accent marks.  Carets do not appear before COMBINING glyphs.
 * </ul>
 * <p>
 * Other metrics available through <code>GlyphMetrics</code> are the
 * advance, bounds, and left and right side bearings.  
 * <p>
 * The advance of a glyph is the distance from the glyph's origin to the
 * origin of the next glyph.  Note that, in a <code>GlyphVector</code>, 
 * the distance from a glyph to its following glyph might not be the 
 * glyph's advance.  
 * <p>
 * The bounds is the smallest rectangle that completely contains the
 * visible portion of the glyph.  The bounds rectangle is relative to the
 * glyph's origin.  The left-side bearing is the distance from the glyph
 * origin to the left of its bounds rectangle. If the left-side bearing is
 * negative, part of the glyph is drawn to the left of its origin.  The
 * right-side bearing is the distance from the right side of the bounds
 * rectangle to the next glyph origin (the origin plus the advance).  If
 * negative, part of the glyph is drawn to the right of the next glyph's
 * origin.
 * <p>
 * Although instances of <code>GlyphMetrics</code> can be directly
 * constructed, they are almost always obtained from a 
 * <code>GlyphVector</code>.  Once constructed, <code>GlyphMetrics</code>
 * objects are immutable.
 * <p>
 * <strong>Example</strong>:<p>
 * Querying a <code>Font</code> for glyph information
 * <blockquote><pre>
 * Font font = ...;
 * int glyphIndex = ...;
 * GlyphMetrics metrics = GlyphVector.getGlyphMetrics(glyphIndex);
 * int isStandard = metrics.isStandard();
 * float glyphAdvance = metrics.getAdvance();
 * </blockquote></pre>
 * @see java.awt.Font
 * @see GlyphVector
 */

public final class GlyphMetrics {
    /**
     * The advance (horizontal or vertical) of the associated glyph.
     */
    // please please don't change the name of this field!  Some JNI code uses it
    private float advance;

    /**
     * The bounds of the associated glyph.
     */
    // please please don't change the name of this field!  Some JNI code uses it
    private Rectangle2D.Float bounds;

    /**
     * Additional information about the glyph encoded as a byte.
     */
    // please please don't change the name of this field!  Some JNI code uses it
    private byte glyphType;

    /**
     * Indicates a glyph that represents a single standard
     * character.
     */
    public static final byte STANDARD = 0;

    /**
     * Indicates a glyph that represents multiple characters
     * as a ligature, for example 'fi' or 'ffi'.  It is followed by
     * filler glyphs for the remaining characters. Filler and combining
     * glyphs can be intermixed to control positioning of accent marks
     * on the logically preceeding ligature.
     */
    public static final byte LIGATURE = 1;

    /**
     * Indicates a glyph that represents a combining character,
     * such as an umlaut.  There is no caret position between this glyph
     * and the preceeding glyph.
     */
    public static final byte COMBINING = 2;

    /**
     * Indicates a glyph with no corresponding character in the
     * backing store.  The glyph is associated with the character
     * represented by the logicaly preceeding non-component glyph.  This
     * is used for kashida justification or other visual modifications to
     * existing glyphs.  There is no caret position between this glyph
     * and the preceeding glyph.
     */
    public static final byte COMPONENT = 3;


    /**
     * Indicates a glyph with no visual representation. It can
     * be added to the other code values to indicate an invisible glyph.
     */
    public static final byte WHITESPACE = 4;

    /**
     * Constructs a <code>GlyphMetrics</code> object.
     * @param advance the advance width or height of the glyph
     * @param bounds the black box bounds of the glyph
     * @param glyphType the type of the glyph
     */
    public GlyphMetrics(float advance, Rectangle2D bounds, byte glyphType) {
        this.advance = advance;
        this.bounds = new Rectangle2D.Float();
        this.bounds.setRect(bounds);
        this.glyphType = glyphType;
    }

    /**
     * Returns the advance width or height of the glyph.
     * @return the advance of the glyph.
     */
    public float getAdvance() {
        return advance;
    }

    /**
     * Returns the black box bounds of the glyph.
     * @return a {@link Rectangle2D} that is the bounds of the glyph.
     */
    public Rectangle2D getBounds2D() {
        return new Rectangle2D.Float(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    /**
     * Returns the left (top) side bearing of the glyph.
     * <p>
     * This is the distance from 0,&nbsp;0 to the left (top) of the glyph
     * bounds.  If the bounds of the glyph is to the left of (above) the
     * origin, the LSB is negative.
     * @return the left side bearing of the glyph.
     */
    public float getLSB() {
        return bounds.x;
    }

    /**
     * Returns the right (bottom) side bearing of the glyph.
     * <p>
     * This is the distance from the right (bottom) of the glyph bounds to
     * the advance. If the bounds of the glyph is to the right of (below)
     * the advance, the RSB is negative.
     * @return the right side bearing of the glyph.
     */
    public float getRSB() {
        return advance - bounds.x - bounds.width;
    }

    /**
     * Returns the raw glyph type code.
     * @return the raw glyph type code.
     */
    public int getType() {
        return glyphType;
    }

    /**
     * Returns <code>true</code> if this is a standard glyph.
     * @return <code>true</code> if this is a standard glyph;
     *		<code>false</code> otherwise.
     */
    public boolean isStandard() {
        return (glyphType & 0x3) == STANDARD;
    }

    /**
     * Returns <code>true</code> if this is a ligature glyph.
     * @return <code>true</code> if this is a ligature glyph;
     *		<code>false</code> otherwise.
     */
    public boolean isLigature() {
        return (glyphType & 0x3) == LIGATURE;
    }

    /**
     * Returns <code>true</code> if this is a combining glyph.
     * @return <code>true</code> if this is a combining glyph;
     *		<code>false</code> otherwise.
     */
    public boolean isCombining() {
        return (glyphType & 0x3) == COMBINING;
    }

    /**
     * Returns <code>true</code> if this is a component glyph.
     * @return <code>true</code> if this is a component glyph;
     *		<code>false</code> otherwise.
     */
    public boolean isComponent() {
        return (glyphType & 0x3) == COMPONENT;
    }

    /**
     * Returns <code>true</code> if this is a whitespace glyph.
     * @return <code>true</code> if this is a whitespace glyph;
     *		<code>false</code> otherwise.
     */
    public boolean isWhitespace() {
        return (glyphType & 0x4) == WHITESPACE;
    }
}

