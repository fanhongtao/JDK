/*
 * @(#)RGBImageFilter.java	1.12 98/07/01
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

/**
 * This class provides an easy way to create an ImageFilter which modifies
 * the pixels of an image in the default RGB ColorModel.  It is meant to
 * be used in conjunction with a FilteredImageSource object to produce
 * filtered versions of existing images.  It is an abstract class that
 * provides the calls needed to channel all of the pixel data through a
 * single method which converts pixels one at a time in the default RGB
 * ColorModel regardless of the ColorModel being used by the ImageProducer.
 * The only method which needs to be defined to create a useable image
 * filter is the filterRGB method.  Here is an example of a definition
 * of a filter which swaps the red and blue components of an image:
 * <pre>
 *
 *	class RedBlueSwapFilter extends RGBImageFilter {
 *	    public RedBlueSwapFilter() {
 *		// The filter's operation does not depend on the
 *		// pixel's location, so IndexColorModels can be
 *		// filtered directly.
 *		canFilterIndexColorModel = true;
 *	    }
 *
 *	    public int filterRGB(int x, int y, int rgb) {
 *		return ((rgb & 0xff00ff00)
 *			| ((rgb & 0xff0000) >> 16)
 *			| ((rgb & 0xff) << 16));
 *	    }
 *	}
 *
 * </pre>
 *
 * @see FilteredImageSource
 * @see ImageFilter
 * @see ColorModel#getRGBdefault
 *
 * @version	1.12 07/01/98
 * @author 	Jim Graham
 */
public abstract class RGBImageFilter extends ImageFilter {
    protected ColorModel origmodel;
    protected ColorModel newmodel;

    /**
     * This boolean indicates whether or not it is acceptable to apply
     * the color filtering of the filterRGB method to the color table
     * entries of an IndexColorModel object in lieu of pixel by pixel
     * filtering.  Subclasses should set this variable to true in their
     * constructor if their filterRGB method does not depend on the
     * coordinate of the pixel being filtered.
     * @see #substituteColorModel
     * @see #filterRGB
     * @see IndexColorModel
     */
    protected boolean canFilterIndexColorModel;

    /**
     * If the ColorModel is an IndexColorModel, and the subclass has
     * set the canFilterIndexColorModel flag to true, we substitute
     * a filtered version of the color model here and wherever
     * that original ColorModel object appears in the setPixels methods. Otherwise
     * overrides the default ColorModel used by the ImageProducer and
     * specifies the default RGB ColorModel instead.

     * @see ImageConsumer
     * @see ColorModel#getRGBdefault
     */
    public void setColorModel(ColorModel model) {
	if (canFilterIndexColorModel && (model instanceof IndexColorModel)) {
	    ColorModel newcm = filterIndexColorModel((IndexColorModel)model);
	    substituteColorModel(model, newcm);
	    consumer.setColorModel(newcm);
	} else {
	    consumer.setColorModel(ColorModel.getRGBdefault());
	}
    }

    /**
     * Registers two ColorModel objects for substitution.  If the oldcm
     * is encountered during any of the setPixels methods, the newcm
     * is substituted and the pixels passed through
     * untouched (but with the new ColorModel object).
     * @param oldcm the ColorModel object to be replaced on the fly
     * @param newcm the ColorModel object to replace oldcm on the fly
     */
    public void substituteColorModel(ColorModel oldcm, ColorModel newcm) {
	origmodel = oldcm;
	newmodel = newcm;
    }

    /**
     * Filters an IndexColorModel object by running each entry in its
     * color tables through the filterRGB function that RGBImageFilter
     * subclasses must provide.  Uses coordinates of -1 to indicate that
     * a color table entry is being filtered rather than an actual
     * pixel value.
     * @param icm the IndexColorModel object to be filtered
     * @return a new IndexColorModel representing the filtered colors
     */
    public IndexColorModel filterIndexColorModel(IndexColorModel icm) {
	int mapsize = icm.getMapSize();
	byte r[] = new byte[mapsize];
	byte g[] = new byte[mapsize];
	byte b[] = new byte[mapsize];
	byte a[] = new byte[mapsize];
	icm.getReds(r);
	icm.getGreens(g);
	icm.getBlues(b);
	icm.getAlphas(a);
	int trans = icm.getTransparentPixel();
	boolean needalpha = false;
	for (int i = 0; i < mapsize; i++) {
	    int rgb = filterRGB(-1, -1, icm.getRGB(i));
	    a[i] = (byte) (rgb >> 24);
	    if (a[i] != ((byte)0xff) && i != trans) {
		needalpha = true;
	    }
	    r[i] = (byte) (rgb >> 16);
	    g[i] = (byte) (rgb >> 8);
	    b[i] = (byte) (rgb >> 0);
	}
	if (needalpha) {
	    return new IndexColorModel(icm.getPixelSize(), mapsize,
				       r, g, b, a);
	} else {
	    return new IndexColorModel(icm.getPixelSize(), mapsize,
				       r, g, b, trans);
	}
    }

    /**
     * Filters a buffer of pixels in the default RGB ColorModel by passing
     * them one by one through the filterRGB method.
     * @see ColorModel#getRGBdefault
     * @see #filterRGB
     */
    public void filterRGBPixels(int x, int y, int w, int h,
				int pixels[], int off, int scansize) {
	int index = off;
	for (int cy = 0; cy < h; cy++) {
	    for (int cx = 0; cx < w; cx++) {
		pixels[index] = filterRGB(x + cx, y + cy, pixels[index]);
		index++;
	    }
	    index += scansize - w;
	}
	consumer.setPixels(x, y, w, h, ColorModel.getRGBdefault(),
			   pixels, off, scansize);
    }

    /**
     * If the ColorModel object is the same one that has already
     * been converted, then simply passes the pixels through with the
     * converted ColorModel. Otherwise converts the buffer of byte
     * pixels to the default RGB ColorModel and passes the converted
     * buffer to the filterRGBPixels method to be converted one by one.
     * @see ColorModel#getRGBdefault
     * @see #filterRGBPixels
     */
    public void setPixels(int x, int y, int w, int h,
			  ColorModel model, byte pixels[], int off,
			  int scansize) {
	if (model == origmodel) {
	    consumer.setPixels(x, y, w, h, newmodel, pixels, off, scansize);
	} else {
	    int filteredpixels[] = new int[w];
	    int index = off;
	    for (int cy = 0; cy < h; cy++) {
		for (int cx = 0; cx < w; cx++) {
		    filteredpixels[cx] = model.getRGB((pixels[index] & 0xff));
		    index++;
		}
		index += scansize - w;
		filterRGBPixels(x, y + cy, w, 1, filteredpixels, 0, w);
	    }
	}
    }

    /**
     * If the ColorModel object is the same one that has already
     * been converted, then simply passes the pixels through with the
     * converted ColorModel, otherwise converts the buffer of integer
     * pixels to the default RGB ColorModel and passes the converted
     * buffer to the filterRGBPixels method to be converted one by one.
     * Converts a buffer of integer pixels to the default RGB ColorModel
     * and passes the converted buffer to the filterRGBPixels method.
     * @see ColorModel#getRGBdefault
     * @see #filterRGBPixels
     */
    public void setPixels(int x, int y, int w, int h,
			  ColorModel model, int pixels[], int off,
			  int scansize) {
	if (model == origmodel) {
	    consumer.setPixels(x, y, w, h, newmodel, pixels, off, scansize);
	} else {
	    int filteredpixels[] = new int[w];
	    int index = off;
	    for (int cy = 0; cy < h; cy++) {
		for (int cx = 0; cx < w; cx++) {
		    filteredpixels[cx] = model.getRGB(pixels[index]);
		    index++;
		}
		index += scansize - w;
		filterRGBPixels(x, y + cy, w, 1, filteredpixels, 0, w);
	    }
	}
    }

    /**
     * Subclasses must specify a method to convert a single input pixel
     * in the default RGB ColorModel to a single output pixel.
     * @see ColorModel#getRGBdefault
     * @see #filterRGBPixels
     */
    public abstract int filterRGB(int x, int y, int rgb);
}
