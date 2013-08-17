/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @author Charlton Innovations, Inc.
 */

package java.awt.font;

import java.awt.geom.AffineTransform;

/**
*   The <code>FontRenderContext</code> class is a container for the
*   information needed to correctly measure text.  The measurement of text
*   can vary because of rules that map outlines to pixels, and rendering
*   hints provided by an application.
*   <p>
*   One such piece of information is a transform that scales
*   typographical points to pixels. (A point is defined to be exactly 1/72
*   of an inch, which is slightly different than
*   the traditional mechanical measurement of a point.)  A character that 
*   is rendered at 12pt on a 600dpi device might have a different size
*   than the same character rendered at 12pt on a 72dpi device because of
*   such factors as rounding to pixel boundaries and hints that the font
*   designer may have specified.
*   <p>
*   Anti-aliasing and Fractional-metrics specified by an application can also
*   affect the size of a character because of rounding to pixel
*   boundaries.
*   <p>
*   Typically, instances of <code>FontRenderContext</code> are obtained from
*   a {@link Graphics2D} object.  A <code>FontRenderContext</code> 
*   which is directly constructed will most likely not represent any actual
*   graphics device, and may lead to unexpected or incorrect results.
*   <p>
*   @see java.awt.RenderingHints#KEY_TEXT_ANTIALIASING
*   @see java.awt.RenderingHints#KEY_FRACTIONALMETRICS
*   @see java.awt.Graphics2D#getFontRenderContext
*   @see java.awt.font.LineMetrics
*/

public class FontRenderContext {
    private transient AffineTransform tx;
    private transient boolean bIsAntiAliased;
    private transient boolean bUsesFractionalMetrics;

    /**
     * Constructs a new <code>FontRenderContext</code>
     * object.
     *
     */
    protected FontRenderContext() {
        this.tx = new AffineTransform();
        this.bIsAntiAliased = false;
        this.bUsesFractionalMetrics = false;
    }

    /**
     * Constructs a <code>FontRenderContext</code> object from an
     * optional {@link AffineTransform} and two <code>boolean</code>
     * values that determine if the newly constructed object has
     * anti-aliasing or fractional metrics.
     * @param tx the transform which is used to scale typographical points
     *  to pixels in this <code>FontRenderContext</code>.  If null, an
     *  identity tranform is used.
     * @param isAntiAliased determines if the newly contructed object has
     * anti-aliasing
     * @param usesFractionalMetrics determines if the newly constructed
     * object uses fractional metrics
     */
    public FontRenderContext(AffineTransform tx,
                            boolean isAntiAliased,
                            boolean usesFractionalMetrics) {
        if (tx == null) {
            this.tx = new AffineTransform();
        }
        else {
            this.tx = new AffineTransform(tx);
        }
        this.bIsAntiAliased = isAntiAliased;
        this.bUsesFractionalMetrics = usesFractionalMetrics;
    }


    /**
    *   Gets the transform that is used to scale typographical points
    *   to pixels in this <code>FontRenderContext</code>.
    *   @returns the <code>AffineTransform</code> of this
    *    <code>FontRenderContext</code>.
    *   @see AffineTransform
    */
    public AffineTransform getTransform() {
        return new AffineTransform(tx);
    }

    /**
    *   Gets the text anti-aliasing mode used in this 
    *   <code>FontRenderContext</code>.
    *   @returns    <code>true</code>, if text is anti-aliased in this
    *   <code>FontRenderContext</code>; <code>false</code> otherwise.
    *   @see        java.awt.RenderingHints#KEY_TEXT_ANTIALIASING
    */
    public boolean isAntiAliased() {
        return this.bIsAntiAliased;
    }

    /**
    *   Gets the text fractional metrics mode requested by the application
    *   for use in this <code>FontRenderContext</code>.
    *   @returns    <code>true</code>, if layout should be performed with
    *   fractional metrics; <code>false</code> otherwise.
    *               in this <code>FontRenderContext</code>.
    *   @see java.awt.RenderingHints#KEY_FRACTIONALMETRICS
    */
    public boolean usesFractionalMetrics() {
        return this.bUsesFractionalMetrics;
    }
}
