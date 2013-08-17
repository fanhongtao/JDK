/*
 * @(#)IndexColorModel.java	1.16 98/07/01
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

/**
 * A ColorModel class that specifies a translation from pixel values
 * to alpha, red, green, and blue color components for pixels which
 * represent indices into a fixed colormap.  An optional transparent
 * pixel value can be supplied which indicates a completely transparent
 * pixel, regardless of any alpha value recorded for that pixel value.
 * This color model is similar to an X11 PseudoColor visual.
 * <p>Many of the methods in this class are final.  The reason for
 * this is that the underlying native graphics code makes assumptions
 * about the layout and operation of this class and those assumptions
 * are reflected in the implementations of the methods here that are
 * marked final.  You can subclass this class for other reaons, but
 * you cannot override or modify the behaviour of those methods.
 *
 * @see ColorModel
 *
 * @version	1.16 07/01/98
 * @author 	Jim Graham
 */
public class IndexColorModel extends ColorModel {
    private int rgb[];
    private int map_size;
    private boolean opaque;

    private int transparent_index;

    /**
     * Constructs an IndexColorModel from the given arrays of red,
     * green, and blue components.  Pixels described by this color
     * model will all have alpha components of 255 (fully opaque).
     * All of the arrays specifying the color components must have
     * at least the specified number of entries.
     * @param bits	The number of bits each pixel occupies.
     * @param size	The size of the color component arrays.
     * @param r		The array of red color components.
     * @param g		The array of green color components.
     * @param b		The array of blue color components.
     */
    public IndexColorModel(int bits, int size,
			   byte r[], byte g[], byte b[]) {
	super(bits);
	setRGBs(size, r, g, b, null);
    }

    /**
     * Constructs an IndexColorModel from the given arrays of red,
     * green, and blue components.  Pixels described by this color
     * model will all have alpha components of 255 (fully opaque),
     * except for the indicated transparent pixel.  All of the arrays
     * specifying the color components must have at least the specified
     * number of entries.
     * @param bits	The number of bits each pixel occupies.
     * @param size	The size of the color component arrays.
     * @param r		The array of red color components.
     * @param g		The array of green color components.
     * @param b		The array of blue color components.
     * @param trans	The index of the transparent pixel.
     */
    public IndexColorModel(int bits, int size,
			   byte r[], byte g[], byte b[], int trans) {
	super(bits);
	setRGBs(size, r, g, b, null);
	setTransparentPixel(trans);
    }

    /**
     * Constructs an IndexColorModel from the given arrays of red,
     * green, blue and alpha components.  All of the arrays specifying
     * the color components must have at least the specified number
     * of entries.
     * @param bits	The number of bits each pixel occupies.
     * @param size	The size of the color component arrays.
     * @param r		The array of red color components.
     * @param g		The array of green color components.
     * @param b		The array of blue color components.
     * @param a		The array of alpha value components.
     */
    public IndexColorModel(int bits, int size,
			   byte r[], byte g[], byte b[], byte a[]) {
	super(bits);
	if (size > 0 && a == null) {
	    throw new NullPointerException();
	}
	setRGBs(size, r, g, b, a);
    }

    private void setRGBs(int size, byte r[], byte g[], byte b[], byte a[]) {
	map_size = size;
	rgb = new int[Math.max(size, 256)];
	int alpha = 0xff;
	opaque = true;
	for (int i = 0; i < size; i++) {
	    if (a != null) {
		alpha = (a[i] & 0xff);
		if (alpha != 0xff) {
		    opaque = false;
		}
	    }
	    rgb[i] = (alpha << 24)
		| ((r[i] & 0xff) << 16)
		| ((g[i] & 0xff) << 8)
		| (b[i] & 0xff);
	}
    }

    /**
     * Constructs an IndexColorModel from a single arrays of packed
     * red, green, blue and optional alpha components.  The array
     * must have enough values in it to fill all of the needed
     * component arrays of the specified size.
     * @param bits	The number of bits each pixel occupies.
     * @param size	The size of the color component arrays.
     * @param cmap	The array of color components.
     * @param start	The starting offset of the first color component.
     * @param hasalpha	Indicates whether alpha values are contained in
     *			the cmap array.
     */
    public IndexColorModel(int bits, int size, byte cmap[], int start,
			   boolean hasalpha) {
	this(bits, size, cmap, start, hasalpha, -1);
    }

    /**
     * Constructs an IndexColorModel from a single arrays of packed
     * red, green, blue and optional alpha components.  The specified
     * transparent index represents a pixel which will be considered
     * entirely transparent regardless of any alpha value specified
     * for it.  The array must have enough values in it to fill all
     * of the needed component arrays of the specified size.
     * @param bits	The number of bits each pixel occupies.
     * @param size	The size of the color component arrays.
     * @param cmap	The array of color components.
     * @param start	The starting offset of the first color component.
     * @param hasalpha	Indicates whether alpha values are contained in
     *			the cmap array.
     * @param trans	The index of the fully transparent pixel.
     */
    public IndexColorModel(int bits, int size, byte cmap[], int start,
			   boolean hasalpha, int trans) {
	// REMIND: This assumes the ordering: RGB[A]
	super(bits);
	map_size = size;
	rgb = new int[Math.max(size, 256)];
	int j = start;
	int alpha = 0xff;
	opaque = true;
	for (int i = 0; i < size; i++) {
	    rgb[i] = ((cmap[j++] & 0xff) << 16)
		| ((cmap[j++] & 0xff) << 8)
		| (cmap[j++] & 0xff);
	    if (hasalpha) {
		alpha = cmap[j++];
		if (alpha != 0xff) {
		    opaque = false;
		}
	    }
	    rgb[i] |= (alpha << 24);
	}
	setTransparentPixel(trans);
    }

    /**
     * Returns the size of the color component arrays in this IndexColorModel.
     */
    final public int getMapSize() {
	return map_size;
    }

    /**
     * Returns the index of the transparent pixel in this IndexColorModel
     * or -1 if there is no transparent pixel.
     */
    final public int getTransparentPixel() {
	return transparent_index;
    }

    /**
     * Copies the array of red color components into the given array.  Only
     * the initial entries of the array as specified by getMapSize() are
     * written.
     */
    final public void getReds(byte r[]) {
	for (int i = 0; i < map_size; i++) {
	    r[i] = (byte) (rgb[i] >> 16);
	}
    }

    /**
     * Copies the array of green color components into the given array.  Only
     * the initial entries of the array as specified by getMapSize() are
     *  written.
     */
    final public void getGreens(byte g[]) {
	for (int i = 0; i < map_size; i++) {
	    g[i] = (byte) (rgb[i] >> 8);
	}
    }

    /**
     * Copies the array of blue color components into the given array.  Only
     * the initial entries of the array as specified by getMapSize() will
     * be written.
     */
    final public void getBlues(byte b[]) {
	for (int i = 0; i < map_size; i++) {
	    b[i] = (byte) rgb[i];
	}
    }

    /**
     * Copies the array of alpha transparency values into the given array.  Only
     * the initial entries of the array as specified by getMapSize() will
     * be written.
     */
    final public void getAlphas(byte a[]) {
	for (int i = 0; i < map_size; i++) {
	    a[i] = (byte) (rgb[i] >> 24);
	}
    }

    private void setTransparentPixel(int trans) {
	if (trans >= map_size || trans < 0) {
	    trans = -1;
	} else {
	    rgb[trans] &= 0x00ffffff;
	    opaque = false;
	}
	transparent_index = trans;
    }

    /**
     * Returns the red color compoment for the specified pixel in the
     * range 0-255.
     */
    final public int getRed(int pixel) {
	return (rgb[pixel] >> 16) & 0xff;
    }

    /**
     * Returns the green color compoment for the specified pixel in the
     * range 0-255.
     */
    final public int getGreen(int pixel) {
	return (rgb[pixel] >> 8) & 0xff;
    }

    /**
     * Returns the blue color compoment for the specified pixel in the
     * range 0-255.
     */
    final public int getBlue(int pixel) {
	return rgb[pixel] & 0xff;
    }

    /**
     * Returns the alpha transparency value for the specified pixel in the
     * range 0-255.
     */
    final public int getAlpha(int pixel) {
	return (rgb[pixel] >> 24) & 0xff;
    }

    /**
     * Returns the color of the pixel in the default RGB color model.
     * @see ColorModel#getRGBdefault
     */
    final public int getRGB(int pixel) {
	return rgb[pixel];
    }
}
