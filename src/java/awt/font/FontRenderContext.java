/*
 * @(#)FontRenderContext.java	1.29 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
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
*   Typically, instances of <code>FontRenderContext</code> are
*   obtained from a {@link java.awt.Graphics2D Graphics2D} object.  A
*   <code>FontRenderContext</code> which is directly constructed will
*   most likely not represent any actual graphics device, and may lead
*   to unexpected or incorrect results.
*   <p>
*   @see java.awt.RenderingHints#KEY_TEXT_ANTIALIASING
*   @see java.awt.RenderingHints#KEY_FRACTIONALMETRICS
*   @see java.awt.Graphics2D#getFontRenderContext()
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
        if (tx != null && !tx.isIdentity()) {
            this.tx = new AffineTransform(tx);
        }
        this.bIsAntiAliased = isAntiAliased;
        this.bUsesFractionalMetrics = usesFractionalMetrics;
    }


    /**
    *   Gets the transform that is used to scale typographical points
    *   to pixels in this <code>FontRenderContext</code>.
    *   @return the <code>AffineTransform</code> of this
    *    <code>FontRenderContext</code>.
    *   @see AffineTransform
    */
    public AffineTransform getTransform() {
        return (tx == null) ? new AffineTransform() : new AffineTransform(tx);
    }

    /**
    *   Gets the text anti-aliasing mode used in this 
    *   <code>FontRenderContext</code>.
    *   @return    <code>true</code>, if text is anti-aliased in this
    *   <code>FontRenderContext</code>; <code>false</code> otherwise.
    *   @see        java.awt.RenderingHints#KEY_TEXT_ANTIALIASING
    */
    public boolean isAntiAliased() {
        return this.bIsAntiAliased;
    }

    /**
    *   Gets the text fractional metrics mode requested by the application
    *   for use in this <code>FontRenderContext</code>.
    *   @return    <code>true</code>, if layout should be performed with
    *   fractional metrics; <code>false</code> otherwise.
    *               in this <code>FontRenderContext</code>.
    *   @see java.awt.RenderingHints#KEY_FRACTIONALMETRICS
    */
    public boolean usesFractionalMetrics() {
        return this.bUsesFractionalMetrics;
    }

    /**
     * Return true if obj is an instance of FontRenderContext and has the same
     * transform, antialiasing, and fractional metrics values as this.
     * @param obj the object to test for equality
     * @return <code>true</code> if the specified object is equal to
     *         this <code>FontRenderContext</code>; <code>false</code>
     *         otherwise.
     */
    public boolean equals(Object obj) {
	try {
	    return equals((FontRenderContext)obj);
	}
	catch (ClassCastException e) {
	    return false;
	}
    }

    /**
     * Return true if rhs has the same transform, antialiasing, 
     * and fractional metrics values as this.
     * @param rhs the <code>FontRenderContext</code> to test for equality
     * @return <code>true</code> if <code>rhs</code> is equal to
     *         this <code>FontRenderContext</code>; <code>false</code>
     *         otherwise.
     */
    public boolean equals(FontRenderContext rhs) {
	if (this == rhs) {
	    return true;
	}
	if (rhs != null &&
	    rhs.bIsAntiAliased == bIsAntiAliased &&
	    rhs.bUsesFractionalMetrics == bUsesFractionalMetrics) {
	    
	    return tx == null ? rhs.tx == null : tx.equals(rhs.tx);
	}
	return false;
    }

    /**
     * Return a hashcode for this FontRenderContext.
     */
    public int hashCode() {
	int hash = tx == null ? 0 : tx.hashCode();
	if (bIsAntiAliased) {
	    hash ^= 0x1;
	}
	if (bUsesFractionalMetrics) {
	    hash ^= 0x2;
	}
	return hash;
    }
}
