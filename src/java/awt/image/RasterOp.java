/*
 * @(#)RasterOp.java	1.7 98/07/27
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

package java.awt.image;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.RenderingHints;

/**
 * This interface describes single-input/single-output
 * operations performed on Raster objects.  It is implemented by such
 * classes as AffineTransformOp, ConvolveOp, and LookupOp.  The Source
 * and Destination objects must contain the appropriate number
 * of bands for the particular classes implementing this interface.
 * Otherwise, an exception is thrown.  This interface cannot be used to
 * describe more sophisticated Ops such as ones that take multiple sources.
 * Each class implementing this interface will specify whether or not it
 * will allow an in-place filtering operation (i.e. source object equal
 * to the destination object).  Note that the restriction to single-input
 * operations means that the values of destination pixels prior to the
 * operation are not used as input to the filter operation.
 * @see AffineTransformOp
 * @see BandCombineOp
 * @see ColorConvertOp
 * @see ConvolveOp
 * @see LookupOp
 * @see RescaleOp
 * @version 10 Feb 1997
 */
public interface RasterOp {
    /**
     * Performs a single-input/single-output operation from a source Raster
     * to a destination Raster.  If the destination Raster is null, a
     * new Raster will be created.  The IllegalArgumentException may be thrown
     * if the source and/or destination Raster is incompatible with the types
     * of Rasters allowed by the class implementing this filter.
     */
    public WritableRaster filter(Raster src, WritableRaster dest);

    /**
     * Returns the bounding box of the filtered destination Raster.
     * The IllegalArgumentException may be thrown if the source Raster
     * is incompatible with the types of Rasters allowed
     * by the class implementing this filter.
     */
    public Rectangle2D getBounds2D(Raster src);

    /**
     * Creates a zeroed destination Raster with the correct size and number of
     * bands.
     * The IllegalArgumentException may be thrown if the source Raster
     * is incompatible with the types of Rasters allowed
     * by the class implementing this filter.
     */
    public WritableRaster createCompatibleDestRaster(Raster src);

    /**
     * Returns the location of the destination point given a
     * point in the source Raster.  If dstPt is non-null, it
     * will be used to hold the return value.
     */
    public Point2D getPoint2D(Point2D srcPt, Point2D dstPt);

    /**
     * Returns the rendering hints for this RasterOp.  Returns
     * null if no hints have been set.
     */
    public RenderingHints getRenderingHints();
}
