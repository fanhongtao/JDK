/*
 * @(#)ReplicateScaleFilter.java	1.3 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
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

import java.awt.image.ImageConsumer;
import java.awt.image.ColorModel;
import java.util.Hashtable;
import java.awt.Rectangle;

/**
 * An ImageFilter class for scaling images using the simplest algorithm.
 * This class extends the basic ImageFilter Class to scale an existing
 * image and provide a source for a new image containing the resampled
 * image.  The pixels in the source image are sampled to produce pixels
 * for an image of the specified size by replicating rows and columns of
 * pixels to scale up or omitting rows and columns of pixels to scale
 * down.
 * It is meant to be used in conjunction with a FilteredImageSource
 * object to produce scaled versions of existing images.
 *
 * @see FilteredImageSource
 * @see ImageFilter
 *
 * @version	1.3 07/01/98
 * @author 	Jim Graham
 */
public class ReplicateScaleFilter extends ImageFilter {
    protected int srcWidth;
    protected int srcHeight;
    protected int destWidth;
    protected int destHeight;

    protected int srcrows[];
    protected int srccols[];
    protected Object outpixbuf;

    /**
     * Constructs a ReplicateScaleFilter that scales the pixels from
     * its source Image as specified by the width and height parameters.
     * @param width the target width to scale the image
     * @param height the target height to scale the image
     */
    public ReplicateScaleFilter(int width, int height) {
	destWidth = width;
	destHeight = height;
    }

    /**
     * Passes along the properties from the source object after adding a
     * property indicating the scale applied.
     */
    public void setProperties(Hashtable props) {
	props = (Hashtable) props.clone();
	String key = "rescale";
	String val = destWidth + "x" + destHeight;
	Object o = props.get(key);
	if (o != null && o instanceof String) {
	    val = ((String) o) + ", " + val;
	}
	props.put(key, val);
	super.setProperties(props);
    }

    /**
     * Override the dimensions of the source image and pass the dimensions
     * of the new scaled size to the ImageConsumer.
     * @see ImageConsumer
     */
    public void setDimensions(int w, int h) {
	srcWidth = w;
	srcHeight = h;
	if (destWidth < 0) {
	    if (destHeight < 0) {
		destWidth = srcWidth;
		destHeight = srcHeight;
	    } else {
		destWidth = srcWidth * destHeight / srcHeight;
	    }
	} else if (destHeight < 0) {
	    destHeight = srcHeight * destWidth / srcWidth;
	}
	consumer.setDimensions(destWidth, destHeight);
    }

    private void calculateMaps() {
	srcrows = new int[destHeight + 1];
	for (int y = 0; y <= destHeight; y++) {
	    srcrows[y] = (2 * y * srcHeight + srcHeight) / (2 * destHeight);
	}
	srccols = new int[destWidth + 1];
	for (int x = 0; x <= destWidth; x++) {
	    srccols[x] = (2 * x * srcWidth + srcWidth) / (2 * destWidth);
	}
    }
   
    /**
     * Choose which rows and columns of the delivered byte pixels are
     * needed for the destination scaled image and pass through just
     * those rows and columns that are needed, replicated as necessary.
     */
    public void setPixels(int x, int y, int w, int h,
			  ColorModel model, byte pixels[], int off,
			  int scansize) {
	if (srcrows == null || srccols == null) {
	    calculateMaps();
	}
	int sx, sy;
	int dx1 = (2 * x * destWidth + srcWidth - 1) / (2 * srcWidth);
	int dy1 = (2 * y * destHeight + srcHeight - 1) / (2 * srcHeight);
	byte outpix[];
	if (outpixbuf != null && outpixbuf instanceof byte[]) {
	    outpix = (byte[]) outpixbuf;
	} else {
	    outpix = new byte[destWidth];
	    outpixbuf = outpix;
	}
	for (int dy = dy1; (sy = srcrows[dy]) < y + h; dy++) {
	    int srcoff = off + scansize * (sy - y);
	    int dx;
	    for (dx = dx1; (sx = srccols[dx]) < x + w; dx++) {
		outpix[dx] = pixels[srcoff + sx];
	    }
	    if (dx > dx1) {
		consumer.setPixels(dx1, dy, dx - dx1, 1,
				   model, outpix, dx1, destWidth);
	    }
	}
    }

    /**
     * Choose which rows and columns of the delivered int pixels are
     * needed for the destination scaled image and pass through just
     * those rows and columns that are needed, replicated as necessary.
     */
    public void setPixels(int x, int y, int w, int h,
			  ColorModel model, int pixels[], int off,
			  int scansize) {
	if (srcrows == null || srccols == null) {
	    calculateMaps();
	}
	int sx, sy;
	int dx1 = (2 * x * destWidth + srcWidth - 1) / (2 * srcWidth);
	int dy1 = (2 * y * destHeight + srcHeight - 1) / (2 * srcHeight);
	int outpix[];
	if (outpixbuf != null && outpixbuf instanceof int[]) {
	    outpix = (int[]) outpixbuf;
	} else {
	    outpix = new int[destWidth];
	    outpixbuf = outpix;
	}
	for (int dy = dy1; (sy = srcrows[dy]) < y + h; dy++) {
	    int srcoff = off + scansize * (sy - y);
	    int dx;
	    for (dx = dx1; (sx = srccols[dx]) < x + w; dx++) {
		outpix[dx] = pixels[srcoff + sx];
	    }
	    if (dx > dx1) {
		consumer.setPixels(dx1, dy, dx - dx1, 1,
				   model, outpix, dx1, destWidth);
	    }
	}
    }
}
