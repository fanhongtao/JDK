/*
 *  @(#)IndexColorModel.java	1.73 98/09/14
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

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import sun.java2d.loops.ImageData;

/**
 * A ColorModel class that works with pixel values consisting of a
 * single sample which is an index into a fixed colormap in the default
 * sRGB ColorSpace.  The colormap specifies red, green, blue, and
 * optional alpha components corresponding to each index.  All components
 * are represented in the colormap as 8-bit unsigned integral values.  If
 * alpha is not present, an opaque alpha component (alpha = 1.0) will be
 * assumed for each entry.  An optional transparent
 * pixel value can be supplied which indicates a completely transparent
 * pixel, regardless of any alpha component recorded for that pixel value.
 * Note that alpha values in IndexColorModels are never premultiplied.
 * This color model is similar to an X11 PseudoColor visual.
 * <p>
 * The index represented by a pixel value is stored in the least
 * significant n bits of the pixel representations passed to the
 * methods of this class, where n is the pixel size specified to the
 * constructor for a particular IndexColorModel object.  Higher order
 * bits in pixel representations are assumed to be zero.
 * For those methods which use a primitive array pixel representation of
 * type transferType, the array length is always one.  The transfer types
 * supported are DataBuffer.TYPE_BYTE, DataBuffer.TYPE_USHORT, and
 * DataBuffer.TYPE_INT.  A single int pixel representation is valid for all
 * objects of this class, since it is always possible to represent pixel
 * values used with this class in a single int.  Therefore, methods which
 * use this representation will not throw an IllegalArgumentException due
 * to an invalid pixel value.
 * <p>
 * Many of the methods in this class are final.  The reason for
 * this is that the underlying native graphics code makes assumptions
 * about the layout and operation of this class and those assumptions
 * are reflected in the implementations of the methods here that are
 * marked final.  You can subclass this class for other reaons, but
 * you cannot override or modify the behaviour of those methods.
 *
 * @see ColorModel
 * @see ColorSpace
 * @see DataBuffer
 *
 * @version 10 Feb 1997
 */
public class IndexColorModel extends ColorModel {
    private int rgb[] = null;
    private int map_size = 0;
    private int transparent_index = -1;
    private boolean allgrayopaque;

    static private native void initIDs();
    static {
        ColorModel.loadLibraries();
        initIDs();
    }
    /**
     * Constructs an IndexColorModel from the given arrays of red,
     * green, and blue components.  Pixels described by this color
     * model will all have alpha components of 255 unnormalized (1.0
     * normalized), i.e. fully opaque.
     * All of the arrays specifying the color components must have
     * at least the specified number of entries.  The ColorSpace will
     * be the default sRGB space.  The transparency value will be
     * Transparency.OPAQUE.  The transfer type will be the smallest
     * of DataBuffer.TYPE_BYTE, DataBuffer.TYPE_USHORT, or
     * DataBuffer.TYPE_INT that can hold a single pixel.
     * @param bits	The number of bits each pixel occupies.
     * @param size	The size of the color component arrays.
     * @param r		The array of red color components.
     * @param g		The array of green color components.
     * @param b		The array of blue color components.
     */
    public IndexColorModel(int bits, int size,
			   byte r[], byte g[], byte b[]) {
	super(bits, IndexColorModel.setBits(bits, false),
              ColorSpace.getInstance(ColorSpace.CS_sRGB),
              false, false, Transparency.OPAQUE,
              ColorModel.getDefaultTransferType(bits));
        if (bits < 1 || bits > 32) {
            throw new IllegalArgumentException("Number of bits must be between"
                                               +" 1 and 32.");
        }
	setRGBs(size, r, g, b, null);
        checkAllGrayOpaque();
    }

    /**
     * Constructs an IndexColorModel from the given arrays of red,
     * green, and blue components.  Pixels described by this color
     * model will all have alpha components of 255 unnormalized (1.0
     * normalized), i.e. fully opaque, except for the indicated
     * transparent pixel.  All of the arrays
     * specifying the color components must have at least the specified
     * number of entries.  The ColorSpace will be the default sRGB space.
     * The transparency value will be Transparency.BITMASK.
     * The transfer type will be the smallest of DataBuffer.TYPE_BYTE,
     * DataBuffer.TYPE_USHORT, or DataBuffer.TYPE_INT that can hold a
     * single pixel.
     * @param bits	The number of bits each pixel occupies.
     * @param size	The size of the color component arrays.
     * @param r		The array of red color components.
     * @param g		The array of green color components.
     * @param b		The array of blue color components.
     * @param trans	The index of the transparent pixel.
     */
    public IndexColorModel(int bits, int size,
			   byte r[], byte g[], byte b[], int trans) {
	super(bits, IndexColorModel.setBits(bits, (trans>=0)),
              ColorSpace.getInstance(ColorSpace.CS_sRGB),
              (trans > -1), false, Transparency.BITMASK,
              ColorModel.getDefaultTransferType(bits));
        if (bits < 1 || bits > 32) {
            throw new IllegalArgumentException("Number of bits must be between"
                                               +" 1 and 32.");
        }
	setRGBs(size, r, g, b, null);
        if (trans > -1) {
            transparency = Transparency.BITMASK;
            setTransparentPixel(trans);
        }
        checkAllGrayOpaque();
    }
 
    /**
     * Constructs an IndexColorModel from the given arrays of red,
     * green, blue and alpha components.  All of the arrays specifying
     * the components must have at least the specified number
     * of entries.  The ColorSpace will be the default sRGB space.
     * The transparency value will be Transparency.TRANSLUCENT.
     * The transfer type will be the smallest of DataBuffer.TYPE_BYTE, 
     * DataBuffer.TYPE_USHORT, or DataBuffer.TYPE_INT that can hold a
     * single pixel.
     * @param bits	The number of bits each pixel occupies.
     * @param size	The size of the color component arrays.
     * @param r		The array of red color components.
     * @param g		The array of green color components.
     * @param b		The array of blue color components.
     * @param a		The array of alpha value components.
     */
    public IndexColorModel(int bits, int size,
			   byte r[], byte g[], byte b[], byte a[]) {
        super (bits, IndexColorModel.setBits(bits, true),
               ColorSpace.getInstance(ColorSpace.CS_sRGB),
               true, false, Transparency.TRANSLUCENT,
               ColorModel.getDefaultTransferType(bits));
        if (bits < 1 || bits > 32) {
            throw new IllegalArgumentException("Number of bits must be between"
                                               +" 1 and 32.");
        }
        setRGBs (size, r, g, b, a);
        setTransparentPixel (-1);
        checkAllGrayOpaque();
    }

    /**
     * Constructs an IndexColorModel from a single array of interleaved
     * red, green, blue and optional alpha components.  The array
     * must have enough values in it to fill all of the needed
     * component arrays of the specified size.  The ColorSpace will
     * be the default sRGB space.  The transparency value will be
     * Transparency.TRANSLUCENT if hasAlpha is true, Transparency.OPAQUE
     * otherwise.  The transfer type will be the smallest of
     * DataBuffer.TYPE_BYTE, DataBuffer.TYPE_USHORT, or DataBuffer.TYPE_INT
     * that can hold a single pixel.
     * sRGB ColorSpace.
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
        if (bits < 1 || bits > 32) {
            throw new IllegalArgumentException("Number of bits must be between"
                                               +" 1 and 32.");
        }
    }

    /**
     * Constructs an IndexColorModel from a single array of interleaved
     * red, green, blue and optional alpha components.  The specified
     * transparent index represents a pixel that will be considered
     * entirely transparent regardless of any alpha value specified
     * for it.  The array must have enough values in it to fill all
     * of the needed component arrays of the specified size.
     * The ColorSpace will be the default sRGB space.  The transparency
     * value will be Transparency.TRANSLUCENT if hasAlpha is true;
     * otherwise it will be Transparency.BITMASK if trans is a valid
     * index into the colormap (between 0 and size - 1) or
     * Transparency.OPAQUE if trans is not a valid index.
     * The transfer type will be the smallest of
     * DataBuffer.TYPE_BYTE, DataBuffer.TYPE_USHORT, or DataBuffer.TYPE_INT
     * that can hold a single pixel.
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
	super(bits, IndexColorModel.setBits(bits, hasalpha || (trans > -1)),
              ColorSpace.getInstance(ColorSpace.CS_sRGB),
              (hasalpha || (trans > -1)), false,
              hasalpha ? Transparency.TRANSLUCENT
                       : (trans >= 0 ? Transparency.BITMASK
                                     : Transparency.OPAQUE),
              ColorModel.getDefaultTransferType(bits));

        if (bits < 1 || bits > 32) {
            throw new IllegalArgumentException("Number of bits must be between"
                                               +" 1 and 32.");
        }
        if (size <= 1) {
            throw new IllegalArgumentException("Map size ("+size+
                                               ") must be >= 1");
        }
	map_size = size;
	rgb = new int[Math.max(size, 256)];
	int j = start;
	int alpha = 0xff;
        transparency = OPAQUE;
	for (int i = 0; i < size; i++) {
	    rgb[i] = ((cmap[j++] & 0xff) << 16)
		| ((cmap[j++] & 0xff) << 8)
		| (cmap[j++] & 0xff);
	    if (hasalpha) {
		alpha = cmap[j++];
		if (alpha != 0xff && transparency != TRANSLUCENT) {
                    transparency =  (alpha == 0x0
                                     ? BITMASK
                                     : TRANSLUCENT);
		}
	    }
	    rgb[i] |= (alpha << 24);
	}

        setTransparentPixel(trans);
        if (transparent_index >= 0) {
            if (transparency == OPAQUE) {
                transparency = BITMASK;
            }
        }
        else if (transparency == OPAQUE) {
            // Force it in case transparent_index was invalid
            supportsAlpha = false;
            numComponents = 3;
        }
        
        if (supportsAlpha) {
            nBits = new int[4];
            nBits[0] = nBits[1] = nBits[2] = nBits[3] = 8;
        }
        else {
            if (transparent_index > -1) {
                nBits = new int[4];
                nBits[3] = 1;
            }
            else {
                nBits = new int[3];
            }
            nBits[0] = nBits[1] = nBits[2] = 8;
        }
        checkAllGrayOpaque();
    }

    /**
     * Constructs an IndexColorModel from an array of ints where each
     * int is comprised of red, green, blue, and optional alpha components
     * in the default RGB color model format.  The specified
     * transparent index represents a pixel that will be considered
     * entirely transparent regardless of any alpha value specified
     * for it.  The array must have enough values in it to fill all
     * of the needed component arrays of the specified size.
     * The ColorSpace will be the default sRGB space.  The transparency
     * value will be Transparency.TRANSLUCENT if hasAlpha is true;
     * otherwise it will be Transparency.BITMASK if trans is a valid
     * index into the colormap (between 0 and size - 1) or
     * Transparency.OPAQUE if trans is not a valid index.
     * The transfer type will be the smallest of
     * DataBuffer.TYPE_BYTE, DataBuffer.TYPE_USHORT, or DataBuffer.TYPE_INT
     * that can hold a single pixel.
     * @param bits	The number of bits each pixel occupies.
     * @param size	The size of the color component arrays.
     * @param cmap	The array of color components.
     * @param start	The starting offset of the first color component.
     * @param hasalpha	Indicates whether alpha values are contained in
     *			the cmap array.
     * @param trans	The index of the fully transparent pixel.
     */
    public IndexColorModel(int bits, int size,
                           int cmap[], int start,
			   boolean hasalpha, int trans, int transferType) {
	// REMIND: This assumes the ordering: RGB[A]
	super(bits, IndexColorModel.setBits(bits, hasalpha),
              ColorSpace.getInstance(ColorSpace.CS_sRGB),
              hasalpha ? true : (trans >= 0 ? true : false),
              false,
              hasalpha ? Transparency.TRANSLUCENT
                       : (trans >= 0 ? Transparency.BITMASK
                                    : Transparency.OPAQUE),
              ColorModel.getDefaultTransferType(bits));

        if (bits < 1 || bits > 32) {
            throw new IllegalArgumentException("Number of bits must be between"
                                               +" 1 and 32.");
        }
        if (size <= 1) {
            throw new IllegalArgumentException("Map size ("+size+
                                               ") must be >= 1");
        }
	map_size = size;
	rgb = new int[Math.max(size, 256)];
	int j = start;
	int alpha = 0xff000000;
        transparency = OPAQUE;
        if (!hasalpha) {
            // Need to make sure that the alpha is 0xff
            for (int i=0; i < size; i++, j++) {
                rgb[i] = cmap[j] | 0xff000000;
            }
        }
        else {
            for (int i = 0; i < size; i++, j++) {
                rgb[i] = cmap[j];
                alpha = cmap[j] & 0xff000000;
                if (alpha != 0xff000000 && transparency != TRANSLUCENT) {
                    transparency =  (alpha == 0x0
                                     ? BITMASK
                                     : TRANSLUCENT);
                }
            }
	}

        setTransparentPixel(trans);
        if (transparent_index >= 0) {
            if (transparency == OPAQUE) {
                transparency = BITMASK;
            }
        }
        checkAllGrayOpaque();
    }

    private void setRGBs(int size, byte r[], byte g[], byte b[], byte a[]) {
        if (size < 1) {
            throw new IllegalArgumentException("Map size ("+size+
                                               ") must be >= 1");
        }
	map_size = size;
	rgb = new int[Math.max(size, 256)];
	int alpha = 0xff;
        transparency = OPAQUE;
	for (int i = 0; i < size; i++) {
	    if (a != null) {
		alpha = (a[i] & 0xff);
		if (alpha != 0xff && transparency != TRANSLUCENT) {
                    transparency =  (alpha == 0x0
                                     ? BITMASK
                                     : TRANSLUCENT);
		}
	    }
	    rgb[i] = (alpha << 24)
		| ((r[i] & 0xff) << 16)
		| ((g[i] & 0xff) << 8)
		| (b[i] & 0xff);
	}
        nBits = new int[4];
        nBits[0] = nBits[1] = nBits[2] = nBits[3] = 8;
        maxBits = 8;
        
    }

    private void checkAllGrayOpaque() {
        int c;

        allgrayopaque = false;
        if ((transparent_index >= 0) || (transparency == TRANSLUCENT)) {
            return;
        }
        for (int i = 0; i < map_size; i++) {
            c = rgb[i];
            if (c == 0x0) {
                /* ignore transparent black */
                continue;
            }
            if ((c & 0xff000000) != 0xff000000) {
                return;
            }
            if ((((c >> 16) & 0xff) != ((c >> 8) & 0xff)) ||
                (((c >> 8) & 0xff) != (c & 0xff))) {
                return;
            }
        }
        allgrayopaque = true;
        transparency = OPAQUE;
    }

    /**
     * Returns the transparency.  Returns either OPAQUE, BITMASK,
     * or TRANSLUCENT
     * @see Transparency#OPAQUE
     * @see Transparency#BITMASK
     * @see Transparency#TRANSLUCENT
     */
    public int getTransparency() {
        return transparency;
    }

    /**
     * Returns an array of the number of bits per color/alpha component.
     * The array contains the color components in the order red, green,
     * blue, followed by the alpha component, if present.
     */
    public int[] getComponentSize() {
        if (nBits == null) {
            if (supportsAlpha) {
                nBits = new int[4];
                nBits[3] = 8;
            }
            else {
                nBits = new int[3];
            }
            nBits[0] = nBits[1] = nBits[2] = 8;
        }
        return nBits;
    }
    
    /**
     * Returns the size of the color/alpha component arrays in this
     * IndexColorModel.
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
     * written.
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
     * Copies the array of alpha transparency components into the given array.
     * Only the initial entries of the array as specified by getMapSize() will
     * be written.
     */
    final public void getAlphas(byte a[]) {
        for (int i = 0; i < map_size; i++) {
            a[i] = (byte) (rgb[i] >> 24);
        }
    }

    /**
     * Converts data for each index from the color and alpha component
     * arrays to an int in the default RGB ColorModel format and copies
     * the resulting 32-bit ARGB values into the given array.  Only
     * the initial entries of the array as specified by getMapSize() will
     * be written.
     */
    final public void getRGBs(int rgb[]) {
        System.arraycopy(this.rgb, 0, rgb, 0, map_size);
    }

    private void setTransparentPixel(int trans) {
	if (trans >= map_size || trans < 0) {
	    trans = -1;
	} else {
	    rgb[trans] &= 0x00ffffff;
	}
	transparent_index = trans;
    }

    /**
     * Returns the red color component for the specified pixel, scaled
     * from 0 to 255 in the default RGB ColorSpace, sRGB.  The pixel value
     * is specified as an int.  The returned value will be a
     * non pre-multiplied value.
     */
    final public int getRed(int pixel) {
	return (rgb[pixel] >> 16) & 0xff;
    }

    /**
     * Returns the green color component for the specified pixel, scaled
     * from 0 to 255 in the default RGB ColorSpace, sRGB.  The pixel value
     * is specified as an int.  The returned value will be a
     * non pre-multiplied value.
     */
    final public int getGreen(int pixel) {
	return (rgb[pixel] >> 8) & 0xff;
    }

    /**
     * Returns the blue color component for the specified pixel, scaled
     * from 0 to 255 in the default RGB ColorSpace, sRGB.  The pixel value
     * is specified as an int.  The returned value will be a
     * non pre-multiplied value.
     */
    final public int getBlue(int pixel) {
	return rgb[pixel] & 0xff;
    }

    /**
     * Returns the alpha component for the specified pixel, scaled
     * from 0 to 255.  The pixel value is specified as an int.
     */
    final public int getAlpha(int pixel) {
	return (rgb[pixel] >> 24) & 0xff;
    }

    /**
     * Returns the color/alpha components of the pixel in the default
     * RGB color model format.  The pixel value is specified as an int.
     * The returned value will be in a non pre-multiplied format.
     * @see ColorModel#getRGBdefault
     */
    final public int getRGB(int pixel) {
	return rgb[pixel];
    }

    /**
     * Returns a data element array representation of a pixel in this
     * ColorModel, given an integer pixel representation in the
     * default RGB color model.
     * This array can then be passed to the setDataElements method of
     * a WritableRaster object.  If the pixel variable is null, a new
     * array will be allocated.  If pixel is not null, it must be
     * a primitive array of type transferType; otherwise, a
     * ClassCastException is thrown.  An ArrayIndexOutOfBoundsException is
     * thrown if pixel is not large enough to hold a pixel value for this
     * ColorModel.  The pixel array will be returned.
     * @see WritableRaster#setDataElements
     * @see SampleModel#setDataElements
     */
    public Object getDataElements(int rgb, Object pixel) {
        int red = (rgb>>16) & 0xff;
        int green = (rgb>>8) & 0xff;
        int blue  = rgb & 0xff;
        int alpha = (rgb>>>24);
        int pix = 0;

        if (alpha == 0) {
            // Look for another transparent pixel
            if (transparent_index > -1) {
                pix = transparent_index;
            }
            else {
                // Search for one
                for (int i=0; i < map_size; i++) {
		    if (this.rgb[i] < (1 << 24)) {
                        transparent_index = i;
                        pix = i;
                        break;
                    }
                }
            }
        } else {
            // a heuristic which says find the closest color,
            // after finding the closest alpha
            // if user wants different behavior, they can derive
            // a class and override this method
            // SLOW --- but accurate
            // REMIND - need a native implementation, and inverse color-map
            int smallestError = 255 * 255 * 255;   // largest possible
	    int smallestAlphaError = 255;

            for (int i=0; i < map_size; i++) {
		int lutrgb = this.rgb[i];
                int tmp = (lutrgb>>>24) - alpha;
		if (tmp < 0) {
		    tmp = -tmp;
		}
		if (tmp <= smallestAlphaError) {
		    smallestAlphaError = tmp;
		    tmp = ((lutrgb>>16) & 0xff) - red;
		    int currentError = tmp * tmp;
		    if (currentError < smallestError) {
			tmp = ((lutrgb>>8) & 0xff) - green;
			currentError += tmp * tmp;
			if (currentError < smallestError) {
			    tmp = (lutrgb & 0xff) - blue;
			    currentError += tmp * tmp;
			    if (currentError < smallestError) {
				pix = i;
				smallestError = currentError;
			    }
			}
		    }
		}
            }
        }

        if (red == green && green == blue) {
            // Grayscale
        }
        switch (transferType) {
        case DataBuffer.TYPE_INT:
	    int[] intObj;
	    if (pixel == null) {
		pixel = intObj = new int[1];
	    } else {
		intObj = (int[]) pixel;
	    }
	    intObj[0] = pix;
            break;
        case DataBuffer.TYPE_BYTE:
	    byte[] byteObj;
	    if (pixel == null) {
		pixel = byteObj = new byte[1];
	    } else {
		byteObj = (byte[]) pixel;
	    }
	    byteObj[0] = (byte) pix;
            break;
        case DataBuffer.TYPE_USHORT:
	    short[] shortObj;
	    if (pixel == null) {
		pixel = shortObj = new short[1];
	    } else {
		shortObj = (short[]) pixel;
	    }
	    shortObj[0] = (short) pix;
            break;
        default:
            throw new IllegalArgumentException("This method has not been "+
                             "implemented for transferType " + transferType);
        }
        return pixel;
    }

    /**
     * Returns an array of unnormalized color/alpha components given a pixel
     * in this ColorModel.  The pixel value is specified as an int.  If the
     * components array is null, a new array will be allocated.  The
     * components array will be returned.  Color/alpha components are
     * stored in the components array starting at offset (even if the
     * array is allocated by this method).  An ArrayIndexOutOfBoundsException
     * is thrown if  the components array is not null and is not large
     * enough to hold all the color and alpha components (starting at offset).
     */
    public int[] getComponents(int pixel, int[] components, int offset) {
        if (components == null) {
            components = new int[offset+numComponents];
        }

        // REMIND: Needs to change if different color space
        components[offset+0] = getRed(pixel);
        components[offset+1] = getGreen(pixel);
        components[offset+2] = getBlue(pixel);
        if (supportsAlpha && (components.length-offset) > 3) {
            components[offset+3] = getAlpha(pixel);
        }
        
        return components;
    }
    
    /**
     * Returns an array of unnormalized color/alpha components given a pixel
     * in this ColorModel.  The pixel value is specified by an array of
     * data elements of type transferType passed in as an object reference.
     * If pixel is not a primitive array of type transferType, a
     * ClassCastException is thrown.  An ArrayIndexOutOfBoundsException is
     * thrown if pixel is not large enough to hold a pixel value for this
     * ColorModel.  If the components array is null, a new
     * array will be allocated.  The components array will be returned.
     * Color/alpha components are
     * stored in the components array starting at offset (even if the
     * array is allocated by this method).  An ArrayIndexOutOfBoundsException
     * is thrown if  the components array is not null and is not large
     * enough to hold all the color and alpha components (starting at offset).
     */
    public int[] getComponents(Object pixel, int[] components, int offset) {
        int intpixel;
        switch (transferType) {
            case DataBuffer.TYPE_BYTE:
               byte bdata[] = (byte[])pixel;
               intpixel = bdata[0] & 0xff;
            break;
            case DataBuffer.TYPE_USHORT:
               short sdata[] = (short[])pixel;
               intpixel = sdata[0] & 0xffff;
            break;
            case DataBuffer.TYPE_INT:
               int idata[] = (int[])pixel;
               intpixel = idata[0];
            break;
            default:
               throw new UnsupportedOperationException("This method has not been "+
                   "implemented for transferType " + transferType);
        }
        return getComponents(intpixel, components, offset);
    }
    
    /**
     * Returns a pixel value represented as an int in this ColorModel,
     * given an array of unnormalized color/alpha components.  An
     * ArrayIndexOutOfBoundsException is thrown if  the components array is
     * not large enough to hold all the color and alpha components (starting
     * at offset).
     */
    public int getDataElement(int[] components, int offset) {
        int rgb = (components[offset+0]<<16)
            | (components[offset+1]<<8) | (components[offset+2]);
        if (supportsAlpha) {
            rgb |= (components[offset+3]<<24);
        }
        else {
            rgb |= 0xff000000;
        }
        Object inData = getDataElements(rgb, null);
        int pixel;
        switch (transferType) {
            case DataBuffer.TYPE_BYTE:
               byte bdata[] = (byte[])inData;
               pixel = bdata[0] & 0xff;
            break;
            case DataBuffer.TYPE_USHORT:
               short sdata[] = (short[])inData;
               pixel = sdata[0];
            break;
            case DataBuffer.TYPE_INT:
               int idata[] = (int[])inData;
               pixel = idata[0];
            break;
            default:
               throw new UnsupportedOperationException("This method has not been "+
                   "implemented for transferType " + transferType);
        }
        return pixel;
    }
    
    /**
     * Returns a data element array representation of a pixel in this
     * ColorModel, given an array of unnormalized color/alpha components.
     * This array can then be passed to the setDataElements method of
     * a WritableRaster object.
     * An ArrayIndexOutOfBoundsException is thrown if  the components array
     * is not large enough to hold all the color and alpha components
     * (starting at offset).  If the pixel variable is null, a new array
     * will be allocated.  If pixel is not null, it must be a primitive array
     * of type transferType; otherwise, a ClassCastException is thrown.
     * An ArrayIndexOutOfBoundsException is thrown if pixel is not large
     * enough to hold a pixel value for this ColorModel.
     * @see WritableRaster#setDataElements
     * @see SampleModel#setDataElements
     */
    public Object getDataElements(int[] components, int offset, Object pixel) {
        int rgb = (components[offset+0]<<16) | (components[offset+1]<<8)
            | (components[offset+2]);
        if (supportsAlpha) {
            rgb |= (components[offset+3]<<24);
        }
        else {
            rgb &= 0xff000000;
        }
        return getDataElements(rgb, pixel);
    }

    /**
     * Creates a WritableRaster with the specified width and height that 
     * has a data layout (SampleModel) compatible with this ColorModel.  
     * @throws UnsupportedOperationException if the number of pixel bits
     * in this ColorModel is greater than 16.
     * @see WritableRaster
     * @see SampleModel
     */
    public WritableRaster createCompatibleWritableRaster(int w, int h) {
        WritableRaster raster;

        if (pixel_bits == 1 || pixel_bits == 2 || pixel_bits == 4) {
            // TYPE_BINARY
            raster = Raster.createPackedRaster(DataBuffer.TYPE_BYTE,
                                               w, h, 1, pixel_bits, null);
        }
        else if (pixel_bits <= 8) {
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
                                                  w,h,1,null);
        }
        else if (pixel_bits <= 16) {
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_USHORT,
                                                  w,h,1,null);
        }
        else {
            throw new
                UnsupportedOperationException("This method is not supported "+
                                              " for pixel bits > 16.");
        }
        return raster;
    }

    /**
      * Returns true if raster is compatible with this ColorModel and
      * false if it is not.
      */
    public boolean isCompatibleRaster(Raster raster) {

	int size = raster.getSampleModel().getSampleSize(0);
        return ((raster.getTransferType() == transferType) &&
		(raster.getNumBands() == 1) && ((1 << size) >= map_size));
    }
    
    /**
     * Creates a SampleModel with the specified width and height that 
     * has a data layout compatible with this ColorModel.  
     * @see SampleModel
     */
    public SampleModel createCompatibleSampleModel(int w, int h) {
        int[] off = new int[1];
        off[0] = 0;
        return new ComponentSampleModel(transferType, w, h, 1, w,
                                        off);
    }
    
    /** Checks if the SampleModel is compatible with this ColorModel.
     * @see SampleModel 
     */
    public boolean isCompatibleSampleModel(SampleModel sm) {
        if (! (sm instanceof ComponentSampleModel)) {
            return false;
        }

        // Transfer type must be the same
        if (sm.getTransferType() != transferType) {
            return false;
        }

        if (numComponents != sm.getNumBands()) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Returns a new BufferedImage of TYPE_INT_ARGB or TYPE_INT_RGB which
     * has a Raster with pixel data computed by expanding the indices
     * in the source Raster using the color/alpha component arrays of
     * this ColorModel.  If forceARGB is true, a TYPE_INT_ARGB image is
     * returned regardless of whether this ColorModel has an alpha component
     * array or a transparent pixel.
     */
    public BufferedImage convertToIntDiscrete(Raster raster, 
                                              boolean forceARGB) {
        ColorModel cm;

        if (forceARGB || transparency == TRANSLUCENT) {
            cm = ColorModel.getRGBdefault();
        }
        else if (transparency == BITMASK) {
            cm = new DirectColorModel(25, 0xff0000, 0x00ff00, 0x0000ff,
                                      0x1000000);
        }
        else {
            cm = new DirectColorModel(24, 0xff0000, 0x00ff00, 0x0000ff);
        }

        int w = raster.getWidth();
        int h = raster.getHeight();
        WritableRaster discreteRaster = 
                  cm.createCompatibleWritableRaster(w, h);
        Object obj = null;
        int[] data = null;

        int rX = raster.getMinX();
        int rY = raster.getMinY();

        for (int y=0; y < h; y++, rY++) {
            obj = raster.getDataElements(rX, rY, w, 1, obj);
            if (obj instanceof int[]) {
                data = (int[])obj;
            } else {
                data = DataBuffer.toIntArray(obj);
            }
            for (int x=0; x < w; x++) {
                data[x] = rgb[data[x]];
            }
            discreteRaster.setDataElements(0, y, w, 1, data);
        }
        
        return new BufferedImage(cm, discreteRaster, false, null);
    }

    private static int[] setBits(int bits, boolean hasAlpha) {
        int[] b = new int[3+(hasAlpha ? 1 : 0)];
        b[0] = b[1] = b[2] = 8;
        if (hasAlpha) {
            b[3] = 8;
        }
        return b;
    }

    public void finalize() {
        ImageData.freeNativeICMData(this);
    }


    /**
     * Prints the contents of this object
     */
    public String toString() {
       return new String("IndexColorModel: #pixelBits = "+pixel_bits
                         + " numComponents = "+numComponents
                         + " color space = "+colorSpace
                         + " transparency = "+transparency
                         + " transIndex   = "+transparent_index
                         + " has alpha = "+supportsAlpha
                         + " isAlphaPre = "+isAlphaPremultiplied
                         );
    }

}

