/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.image;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.RenderingHints;

/**
 * This interface describes single-input/single-output
 * operations performed on BufferedImage objects.
 * It is implemented by such classes as AffineTransformOp, ConvolveOp,
 * BandCombineOp, and LookupOp.  These objects can be passed into
 * a BufferedImageFilter to operate on a BufferedImage in the
 * ImageProducer-ImageFilter-ImageConsumer paradigm.
 * This interface cannot be used to describe more sophisticated Ops
 * such as ones that take multiple sources.  Each class implementing this
 * interface will specify whether or not it will allow an in-place filtering
 * operation (i.e. source object equal to the destination object).  Note
 * that the restriction to single-input operations means that the
 * values of destination pixels prior to the operation are not used
 * as input to the filter operation.
 * @see BufferedImage
 * @see BufferedImageFilter
 * @see AffineTransformOp
 * @see BandCombineOp
 * @see ColorConvertOp
 * @see ConvolveOp
 * @see LookupOp
 * @see RescaleOp
 * @version 10 Feb 1997
 */
public interface BufferedImageOp {
    /**
     * Performs a single-input/single-output operation on a BufferedImage.
     * If the color models for the two images do not match, a color
     * conversion into the destination color model will be performed.
     * If the destination image is null,
     * a BufferedImage with an appropriate ColorModel will be created.
     * The IllegalArgumentException may be thrown if the source and/or
     * destination image is incompatible with the types of images allowed
     * by the class implementing this filter.
     */
    public BufferedImage filter(BufferedImage src, BufferedImage dest);

    /**
     * Returns the bounding box of the filtered destination image.
     * The IllegalArgumentException may be thrown if the source
     * image is incompatible with the types of images allowed
     * by the class implementing this filter.
     */
    public Rectangle2D getBounds2D (BufferedImage src);

    /**
     * Creates a zeroed destination image with the correct size and number of
     * bands.
     * The IllegalArgumentException may be thrown if the source 
     * image is incompatible with the types of images allowed
     * by the class implementing this filter.
     * @param src       Source image for the filter operation.
     * @param destCM    ColorModel of the destination.  If null, the
     *                  ColorModel of the source will be used.
     */
    public BufferedImage createCompatibleDestImage (BufferedImage src,
						    ColorModel destCM);

    /**
     * Returns the location of the destination point given a
     * point in the source image.  If dstPt is non-null, it
     * will be used to hold the return value.
     */
    public Point2D getPoint2D (Point2D srcPt, Point2D dstPt);

    /**
     * Returns the rendering hints for this BufferedImageOp.  Returns
     * null if no hints have been set.
     */
    public RenderingHints getRenderingHints();
}
