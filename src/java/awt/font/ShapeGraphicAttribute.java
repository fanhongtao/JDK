/*
 * @(#)ShapeGraphicAttribute.java	1.15 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
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

import java.awt.Shape;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * The <code>ShapeGraphicAttribute</code> class is an implementation of
 * {@link GraphicAttribute} that draws shapes in a {@link TextLayout}.
 * @see GraphicAttribute 
 */
public final class ShapeGraphicAttribute extends GraphicAttribute {

    private Shape fShape;
    private boolean fStroke;

    /** 
     * A key indicating the shape should be stroked with a 1-pixel wide stroke. 
     */
    public static final boolean STROKE = true;

    /** 
     * A key indicating the shape should be filled. 
     */
    public static final boolean FILL = false;

    // cache shape bounds, since GeneralPath doesn't
    private Rectangle2D fShapeBounds;

    /**
     * Constructs a <code>ShapeGraphicAttribute</code> for the specified
     * {@link Shape}.
     * @param shape the <code>Shape</code> to render.  The 
     * <code>Shape</code> is rendered with its origin at the origin of
     * this <code>ShapeGraphicAttribute</code> in the
     * host <code>TextLayout</code>.  This object maintains a reference to
     * <code>shape</code>.
     * @param alignment one of the alignments from this
     * <code>ShapeGraphicAttribute</code>.
     * @param stroke <code>true</code> if the <code>Shape</code> should be
     * stroked; <code>false</code> if the <code>Shape</code> should be
     * filled.
     */
    public ShapeGraphicAttribute(Shape shape,
                                 int alignment,
                                 boolean stroke) {

        super(alignment);

        fShape = shape;
        fStroke = stroke;
        fShapeBounds = fShape.getBounds2D();
    }

    /**
     * Returns the ascent of this <code>ShapeGraphicAttribute</code>.  The
     * ascent of a <code>ShapeGraphicAttribute</code> is the positive
     * distance from the origin of its <code>Shape</code> to the top of
     * bounds of its <code>Shape</code>.
     * @return the ascent of this <code>ShapeGraphicAttribute</code>.
     */
    public float getAscent() {

        return (float) Math.max(0, -fShapeBounds.getMinY());
    }

    /**
     * Returns the descent of this <code>ShapeGraphicAttribute</code>.
     * The descent of a <code>ShapeGraphicAttribute</code> is the distance
     * from the origin of its <code>Shape</code> to the bottom of the
     * bounds of its <code>Shape</code>.
     * @return the descent of this <code>ShapeGraphicAttribute</code>.
     */
    public float getDescent() {

        return (float) Math.max(0, fShapeBounds.getMaxY());
    }

    /**
     * Returns the advance of this <code>ShapeGraphicAttribute</code>.
     * The advance of a <code>ShapeGraphicAttribute</code> is the distance
     * from the origin of its <code>Shape</code> to the right side of the
     * bounds of its <code>Shape</code>.
     * @return the advance of this <code>ShapeGraphicAttribute</code>.
     */
    public float getAdvance() {

        return (float) Math.max(0, fShapeBounds.getMaxX());
    }

    /**
     * Draws the graphic at the given location.  The <code>Shape</code>
     * is drawn with its origin at (x,&nbsp;y).
     * @param graphics the {@link Graphics2D} into which to draw the
     * graphic
     * @param x,&nbsp;y the user space coordinates where the graphic is
     * drawn
     */
    public void draw(Graphics2D graphics, float x, float y) {

        // translating graphics to draw Shape !!!
        graphics.translate((int)x, (int)y);

        try {
            if (fStroke == STROKE) {
                // REMIND: set stroke to correct size
                graphics.draw(fShape);
            }
            else {
                graphics.fill(fShape);
            }
        }
        finally {
            graphics.translate(-(int)x, -(int)y);
        }
    }

    /**
     * Returns a {@link Rectangle2D} that encloses all of the
     * bits drawn by this <code>ShapeGraphicAttribute</code> relative to
     * the rendering position.  A graphic can be rendered beyond its
     * origin, ascent, descent, or advance;  but if it does, this method's
     * implementation should indicate where the graphic is rendered.
     * @return a <code>Rectangle2D</code> that encloses all of the bits
     * rendered by this <code>ShapeGraphicAttribute</code>.
     */
    public Rectangle2D getBounds() {

        Rectangle2D.Float bounds = new Rectangle2D.Float();
        bounds.setRect(fShapeBounds);

        if (fStroke == STROKE) {
            ++bounds.width;
            ++bounds.height;
        }

        return bounds;
    }

    /**
     * Returns a hashcode for this <code>ShapeGraphicAttribute</code>.
     * @return  a hash code value for this
     * <code>ShapeGraphicAttribute</code>.
     */
    public int hashCode() {

        return fShape.hashCode();
    }

    /**
     * Compares this <code>ShapeGraphicAttribute</code> to the specified
     * <code>Object</code>.
     * @param rhs the <code>Object</code> to compare for equality
     * @return <code>true</code> if this
     * <code>ShapeGraphicAttribute</code> equals <code>rhs</code>;
     * <code>false</code> otherwise.
     */
    public boolean equals(Object rhs) {

        try {
            return equals((ShapeGraphicAttribute) rhs);
        }
        catch(ClassCastException e) {
            return false;
        }
    }

    /**
     * Compares this <code>ShapeGraphicAttribute</code> to the specified
     * <code>ShapeGraphicAttribute</code>.
     * @param rhs the <code>ShapeGraphicAttribute</code> to compare for
     * equality
     * @return <code>true</code> if this
     * <code>ShapeGraphicAttribute</code> equals <code>rhs</code>;
     * <code>false</code> otherwise.
     */
    public boolean equals(ShapeGraphicAttribute rhs) {

        if (rhs == null) {
            return false;
        }

        if (this == rhs) {
            return true;
        }

        if (fStroke != rhs.fStroke) {
            return false;
        }

        if (getAlignment() != rhs.getAlignment()) {
            return false;
        }

        if (!fShape.equals(rhs.fShape)) {
            return false;
        }

        return true;
    }
}
