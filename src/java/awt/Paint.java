/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt;

import java.awt.image.ColorModel;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * This <code>Paint</code> interface defines how color patterns
 * can be generated for {@link Graphics2D} operations.  A class
 * implementing the <code>Paint</code> interface is added to the
 * <code>Graphics2D</code> context in order to define the color 
 * pattern used by the <code>draw</code> and <code>fill</code> methods.
 * <p>
 * Instances of classes implementing <code>Paint</code> must be 
 * read-only because the <code>Graphics2D</code> does not clone
 * these objects when they are set as an attribute with the 
 * <code>setPaint</code> method or when the <code>Graphics2D</code>
 * object is itself cloned.
 * @see PaintContext
 * @see Color
 * @see GradientPaint
 * @see TexturePaint
 * @see Graphics2D#setPaint
 * @version 1.24, 02/06/02
 */

public interface Paint extends Transparency {
    /**
     * Creates and returns a {@link PaintContext} used to 
     * generate the color pattern.
     * @param cm the {@link ColorModel} that receives the
     * <code>Paint</code> data. This is used only as a hint.
     * @param deviceBounds the device space bounding box
     *                     of the graphics primitive being rendered
     * @param userBounds the user space bounding box 
     *                     of the graphics primitive being rendered
     * @param xform the {@link AffineTransform} from user
     *      space into device space
     * @param hints the hint that the context object uses to
     *              choose between rendering alternatives
     * @return the <code>PaintContext</code> for
     *              generating color patterns
     * @see PaintContext
     */
    public PaintContext createContext(ColorModel cm,
				      Rectangle deviceBounds,
				      Rectangle2D userBounds,
				      AffineTransform xform,
                                      RenderingHints hints);

}

