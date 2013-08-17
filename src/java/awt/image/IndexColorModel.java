/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.image;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import sun.java2d.loops.ImageData;
import java.math.BigInteger;

/**
 * The <code>IndexColorModel</code> class is a <code>ColorModel</code>
 * class that works with pixel values consisting of a
 * single sample which is an index into a fixed colormap in the default
 * sRGB ColorSpace.  The colormap specifies red, green, blue, and
 * optional alpha components corresponding to each index.  All components
 * are represented in the colormap as 8-bit unsigned integral values.  If
 * alpha is not present, an opaque alpha component (alpha = 1.0) is
 * assumed for each entry.  An optional transparent
 * pixel value can be supplied that indicates a completely transparent
 * pixel, regardless of any alpha component recorded for that pixel value.
 * Note that alpha values in <code>IndexColorModel</code> objects are 
 * never premultiplied.
 * This color model is similar to an X11 PseudoColor visual.
 * <p>
 * The index represented by a pixel value is stored in the least
 * significant n bits of the pixel representations passed to the
 * methods of this class, where n is the pixel size specified to the
 * constructor for a particular <code>IndexColorModel</code> object 
 * and n must be between 1 and 16.  
 * Higher order bits in pixel representations are assumed to be zero.
 * For those methods that use a primitive array pixel representation of
 * type <code>transferType</code>, the array length is always one.  
 * The transfer types supported are DataBuffer.TYPE_BYTE and 
 * DataBuffer.TYPE_USHORT.  A single int pixel 
 * representation is valid for all objects of this class, since it is 
 * always possible to represent pixel values used with this class in a 
 * single int.  Therefore, methods that use this representation do 
 * not throw an <code>IllegalArgumentException</code> due to an invalid 
 * pixel value.
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
    private int rgb[];
    private int map_size;
    private int transparent_index = -1;
    private boolean allgrayopaque;
    private BigInteger validBits;
    
    static private native void initIDs();
    static {
        ColorModel.loadLibraries();
        initIDs();
    }
    /**
     * Constructs an <code>IndexColorModel</code> from the specified 
     * arrays of red, green, and blue components.  Pixels described 
     * by this color model all have alpha components of 255 
     * unnormalized (1.0&nbsp;normalized), which means they
     * are fully opaque.  All of the arrays specifying the color 
     * components must have at least the specified number of entries.  
     * The <code>ColorSpace</code> is the default sRGB space.  The 
     * transparency value is Transparency.OPAQUE.  The transfer type is
     * the smallest of DataBuffer.TYPE_BYTE or DataBuffer.TYPE_USHORT
     * that can hold a single pixel.
     * @param bits	the number of bits each pixel occupies
     * @param size	the size of the color component arrays
     * @param r		the array of red color components
     * @param g		the array of green color components
     * @param b		the array of blue color components
     * @throws IllegalArgumentException if <code>bits</code> is less
     *         than 1 or greater than 16
     * @throws IllegalArgumentException if <code>size</code> is less
     *         than 1
     */
    public IndexColorModel(int bits, int size,
			   byte r[], byte g[], byte b[]) {
	super(bits, IndexColorModel.setBits(bits, false),
              ColorSpace.getInstance(ColorSpace.CS_sRGB),
              false, false, Transparency.OPAQUE,
              ColorModel.getDefaultTransferType(bits));
        if (bits < 1 || bits > 16) {
            throw new IllegalArgumentException("Number of bits must be between"
                                               +" 1 and 16.");
        }
	setRGBs(size, r, g, b, null);
        checkAllGrayOpaque();
    }

    /**
     * Constructs an <code>IndexColorModel</code> from the given arrays 
     * of red, green, and blue components.  Pixels described by this color
     * model all have alpha components of 255 unnormalized 
     * (1.0&nbsp;normalized), which means they are fully opaque, except 
     * for the indicated transparent pixel.  All of the arrays
     * specifying the color components must have at least the specified
     * number of entries.  The ColorSpace is the default sRGB space.
     * The transparency value is Transparency.BITMASK.
     * The transfer type is the smallest of DataBuffer.TYPE_BYTE or
     * DataBuffer.TYPE_USHORT that can hold a
     * single pixel.
     * @param bits	the number of bits each pixel occupies
     * @param size	the size of the color component arrays
     * @param r		the array of red color components
     * @param g		the array of green color components
     * @param b		the array of blue color components
     * @param trans	the index of the transparent pixel
     * @throws IllegalArgumentException if <code>bits</code> is less than
     *          1 or greater than 16
     * @throws IllegalArgumentException if <code>size</code> is less than
     *          1
     */
    public IndexColorModel(int bits, int size,
			   byte r[], byte g[], byte b[], int trans) {
	super(bits, IndexColorModel.setBits(bits, (trans>=0)),
              ColorSpace.getInstance(ColorSpace.CS_sRGB),
              (trans > -1), false, Transparency.BITMASK,
              ColorModel.getDefaultTransferType(bits));
        if (bits < 1 || bits > 16) {
            throw new IllegalArgumentException("Number of bits must be between"
                                               +" 1 and 16.");
        }
	setRGBs(size, r, g, b, null);
        if (trans > -1) {
            transparency = Transparency.BITMASK;
            setTransparentPixel(trans);
        }
        checkAllGrayOpaque();
    }
 
    /**
     * Constructs an <code>IndexColorModel</code> from the given 
     * arrays of red, green, blue and alpha components.  All of the 
     * arrays specifying the components must have at least the specified 
     * number of entries.  The ColorSpace is the default sRGB space.
     * The transparency value is Transparency.TRANSLUCENT.
     * The transfer type is the smallest of DataBuffer.TYPE_BYTE or 
     * DataBuffer.TYPE_USHORT that can hold a single pixel.
     * @param bits	the number of bits each pixel occupies
     * @param size	the size of the color component arrays
     * @param r		the array of red color components
     * @param g		the array of green color components
     * @param b		the array of blue color components
     * @param a		the array of alpha value components
     * @throws IllegalArgumentException if <code>bits</code> is less
     *           than 1 or greater than 16
     * @throws IllegalArgumentException if <code>size</code> is less
     *           than 1
     */
    public IndexColorModel(int bits, int size,
			   byte r[], byte g[], byte b[], byte a[]) {
        super (bits, IndexColorModel.setBits(bits, true),
               ColorSpace.getInstance(ColorSpace.CS_sRGB),
               true, false, Transparency.TRANSLUCENT,
               ColorModel.getDefaultTransferType(bits));
        if (bits < 1 || bits > 16) {
            throw new IllegalArgumentException("Number of bits must be between"
                                               +" 1 and 16.");
        }
        setRGBs (size, r, g, b, a);
        setTransparentPixel (-1);
        checkAllGrayOpaque();
    }

    /**
     * Constructs an <code>IndexColorModel</code> from a single 
     * array of interleaved red, green, blue and optional alpha 
     * components.  The array must have enough values in it to 
     * fill all of the needed component arrays of the specified 
     * size.  The ColorSpace is the default sRGB space.  The 
     * transparency value is Transparency.TRANSLUCENT if 
     * <code>hasAlpha</code> is <code>true</code>, Transparency.OPAQUE
     * otherwise.  The transfer type is the smallest of
     * DataBuffer.TYPE_BYTE or DataBuffer.TYPE_USHORT 
     * that can hold a single pixel.
     * 
     * @param bits	the number of bits each pixel occupies
     * @param size	the size of the color component arrays
     * @param cmap	the array of color components
     * @param start	the starting offset of the first color component
     * @param hasalpha	indicates whether alpha values are contained in
     *			the <code>cmap</code> array
     * @throws IllegalArgumentException if <code>bits</code> is less
     *           than 1 or greater than 16
     * @throws IllegalArgumentException if <code>size</code> is less
     *           than 1
     */
    public IndexColorModel(int bits, int size, byte cmap[], int start,
			   boolean hasalpha) {
	this(bits, size, cmap, start, hasalpha, -1);
        if (bits < 1 || bits > 16) {
            throw new IllegalArgumentException("Number of bits must be between"
                                               +" 1 and 16.");
        }
    }

    /**
     * Constructs an <code>IndexColorModel</code> from a single array of 
     * interleaved red, green, blue and optional alpha components.  The 
     * specified transparent index represents a pixel that is considered
     * entirely transparent regardless of any alpha value specified
     * for it.  The array must have enough values in it to fill all
     * of the needed component arrays of the specified size.
     * The ColorSpace is the default sRGB space.  The transparency
     * value is Transparency.TRANSLUCENT if <code>hasAlpha</code>
     * is <code>true</code>; otherwise it is Transparency.BITMASK 
     * if <code>trans</code> is a valid index into the colormap 
     * (between 0 and size - 1) or Transparency.OPAQUE if <code>trans</code>
     * is not a valid index.  The transfer type is the smallest of
     * DataBuffer.TYPE_BYTE or DataBuffer.TYPE_USHORT
     * that can hold a single pixel.
     * @param bits	the number of bits each pixel occupies
     * @param size	the size of the color component arrays
     * @param cmap	the array of color components
     * @param start	the starting offset of the first color component
     * @param hasalpha	indicates whether alpha values are contained in
     *			the <code>cmap</code> array
     * @param trans	the index of the fully transparent pixel
     * @throws IllegalArgumentException if <code>bits</code> is less than
     *               1 or greater than 16
     * @throws IllegalArgumentException if <code>size</code> is less than
     *               1
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

        if (bits < 1 || bits > 16) {
            throw new IllegalArgumentException("Number of bits must be between"
                                               +" 1 and 16.");
        }
        if (size < 1) {
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
     * Constructs an <code>IndexColorModel</code> from an array of 
     * ints where each int is comprised of red, green, blue, and 
     * optional alpha components in the default RGB color model format.  
     * The specified transparent index represents a pixel that is considered
     * entirely transparent regardless of any alpha value specified
     * for it.  The array must have enough values in it to fill all
     * of the needed component arrays of the specified size.
     * The ColorSpace is the default sRGB space.  The transparency
     * value is Transparency.TRANSLUCENT if <code>hasAlpha</code> is 
     * <code>true</code>; otherwise it is Transparency.BITMASK if 
     * <code>trans</code> is a valid index into the colormap 
     * (between 0 and size - 1) or Transparency.OPAQUE if 
     * <code>trans</code> is not a valid index.  
     * @param bits	the number of bits each pixel occupies
     * @param size	the size of the color component arrays
     * @param cmap	the array of color components
     * @param start	the starting offset of the first color component
     * @param hasalpha	indicates whether alpha values are contained in
     *			the <code>cmap</code> array
     * @param trans	the index of the fully transparent pixel
     * @param transferType the data type of the array used to represent
     *           pixel values.  The data type must be either 
     *           <code>DataBuffer.TYPE_BYTE</code> or
     *           <code>DataBuffer.TYPE_USHORT</code>.
     * @throws IllegalArgumentException if <code>bits</code> is less
     *           than 1 or greater than 16
     * @throws IllegalArgumentException if <code>size</code> is less
     *           than 1
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
              transferType);

        if (bits < 1 || bits > 16) {
            throw new IllegalArgumentException("Number of bits must be between"
                                               +" 1 and 16.");
        }
        if (size < 1) {
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

    /**
     * Constructs an <code>IndexColorModel</code> from an 
     * <code>int</code> array where each <code>int</code> is 
     * comprised of red, green, blue, and alpha            
     * components in the default RGB color model format.  
     * The array must have enough values in it to fill all
     * of the needed component arrays of the specified size.
     * The <code>ColorSpace</code> is the default sRGB space.  
     * The transfer type is the smallest of
     * DataBuffer.TYPE_BYTE, DataBuffer.TYPE_USHORT, or DataBuffer.TYPE_INT
     * that can hold a single pixel.
     * The <code>BigInteger</code> object specifies the valid/invalid pixels
     * in the <code>cmap</code> array.  A pixel is valid if the 
     * <code>BigInteger</code> value at that index is set, and is invalid
     * if the <code>BigInteger</code> bit  at that index is not set.
     * @param bits the number of bits each pixel occupies
     * @param size the size of the color component array
     * @param cmap the array of color components
     * @param start the starting offset of the first color component
     * @param transferType the specified data type
     * @param validBits a <code>BigInteger</code> object.  If a bit is
     *    set in the BigInteger, the pixel at that index is valid.
     *    If a bit is not set, the pixel at that index
     *    is considered invalid.  If null, all pixels are valid.
     *    Only bits from 0 to map_size will be considered.
     *    
     */
    public IndexColorModel(int bits, int size, int cmap[], int start,
                           int transferType, BigInteger validBits) {
        super (bits, IndexColorModel.setBits(bits, true),
               ColorSpace.getInstance(ColorSpace.CS_sRGB),
               true, false, Transparency.TRANSLUCENT,
               transferType);
        
        if (bits < 1 || bits > 32) {
            throw new IllegalArgumentException("Number of bits must be between"
                                               +" 1 and 32.");
        }
        if (size < 1) {
            throw new IllegalArgumentException("Map size ("+size+
                                               ") must be >= 1");
        }

	map_size = size;
        if (validBits != null) {
            // Check to see if it is all valid
            for (int i=0; i < size; i++) {
                if (!validBits.testBit(i)) {
                    this.validBits = validBits;
                    break;
                }
            }
        }
        
	rgb = new int[Math.max(size, 256)];
	int j = start;
	int alpha;
        transparency = OPAQUE;

        for (int i = 0; i < size; i++, j++) {
            rgb[i] = cmap[j];
            alpha = cmap[j] & 0xff000000;
            if (alpha != 0xff000000 && transparency != TRANSLUCENT) {
                transparency =  (alpha == 0x0
                                 ? BITMASK
                                 : TRANSLUCENT);
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

    private BigInteger getAllValid() {
        BigInteger validBits;
	int numbytes = (map_size+7)/8;
	byte[] valid = new byte[numbytes];
	java.util.Arrays.fill(valid, (byte)0xff);
	valid[0] = (byte)(0xff >>> (numbytes*8 - map_size));

	return new BigInteger(1, valid);
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
     * @return the transparency of this <code>IndexColorModel</code> 
     * @see Transparency#OPAQUE
     * @see Transparency#BITMASK
     * @see Transparency#TRANSLUCENT
     */
    public int getTransparency() {
        return transparency;
    }

    /**
     * Returns an array of the number of bits for each color/alpha component.
     * The array contains the color components in the order red, green,
     * blue, followed by the alpha component, if present.
     * @return an array containing the number of bits of each color 
     *         and alpha component of this <code>IndexColorModel</code>
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
     * <code>IndexColorModel</code>.
     * @return the size of the color and alpha component arrays.
     */
    final public int getMapSize() {
        return map_size;
    }

    /**
     * Returns the index of the transparent pixel in this 
     * <code>IndexColorModel</code> or -1 if there is no transparent pixel.
     * @return the index of this <code>IndexColorModel</code> object's
     *       transparent pixel, or -1 if there is no such pixel.
     */
    final public int getTransparentPixel() {
	return transparent_index;
    }

    /**
     * Copies the array of red color components into the specified array.  
     * Only the initial entries of the array as specified by 
     * {@link #getMapSize() getMapSize} are written.
     * @param r the specified array into which the elements of the 
     *      array of red color components are copied 
     */
    final public void getReds(byte r[]) {
	for (int i = 0; i < map_size; i++) {
	    r[i] = (byte) (rgb[i] >> 16);
	}
    }

    /**
     * Copies the array of green color components into the specified array.  
     * Only the initial entries of the array as specified by 
     * <code>getMapSize</code> are written.
     * @param g the specified array into which the elements of the 
     *      array of green color components are copied 
     */
    final public void getGreens(byte g[]) {
	for (int i = 0; i < map_size; i++) {
	    g[i] = (byte) (rgb[i] >> 8);
	}
    }

    /**
     * Copies the array of blue color components into the specified array.  
     * Only the initial entries of the array as specified by 
     * <code>getMapSize</code> are written.
     * @param b the specified array into which the elements of the 
     *      array of blue color components are copied 
     */
    final public void getBlues(byte b[]) {
        for (int i = 0; i < map_size; i++) {
            b[i] = (byte) rgb[i];
        }
    }

    /**
     * Copies the array of alpha transparency components into the 
     * specified array.  Only the initial entries of the array as specified 
     * by <code>getMapSize</code> are written.
     * @param a the specified array into which the elements of the 
     *      array of alpha components are copied 
     */
    final public void getAlphas(byte a[]) {
        for (int i = 0; i < map_size; i++) {
            a[i] = (byte) (rgb[i] >> 24);
        }
    }

    /**
     * Converts data for each index from the color and alpha component
     * arrays to an int in the default RGB ColorModel format and copies
     * the resulting 32-bit ARGB values into the specified array.  Only
     * the initial entries of the array as specified by 
     * <code>getMapSize</code> are
     * written.
     * @param rgb the specified array into which the converted ARGB 
     *        values from this array of color and alpha components
     *        are copied.
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
     * is specified as an int.  The returned value is a
     * non pre-multiplied value.
     * @param pixel the specified pixel 
     * @return the value of the red color component for the specified pixel
     */
    final public int getRed(int pixel) {
	return (rgb[pixel] >> 16) & 0xff;
    }

    /**
     * Returns the green color component for the specified pixel, scaled
     * from 0 to 255 in the default RGB ColorSpace, sRGB.  The pixel value
     * is specified as an int.  The returned value is a
     * non pre-multiplied value.
     * @param pixel the specified pixel 
     * @return the value of the green color component for the specified pixel
     */
    final public int getGreen(int pixel) {
	return (rgb[pixel] >> 8) & 0xff;
    }

    /**
     * Returns the blue color component for the specified pixel, scaled
     * from 0 to 255 in the default RGB ColorSpace, sRGB.  The pixel value
     * is specified as an int.  The returned value is a
     * non pre-multiplied value.
     * @param pixel the specified pixel 
     * @return the value of the blue color component for the specified pixel
     */
    final public int getBlue(int pixel) {
	return rgb[pixel] & 0xff;
    }

    /**
     * Returns the alpha component for the specified pixel, scaled
     * from 0 to 255.  The pixel value is specified as an int.
     * @param pixel the specified pixel 
     * @return the value of the alpha component for the specified pixel
     */
    final public int getAlpha(int pixel) {
	return (rgb[pixel] >> 24) & 0xff;
    }

    /**
     * Returns the color/alpha components of the pixel in the default
     * RGB color model format.  The pixel value is specified as an int.
     * The returned value is in a non pre-multiplied format.
     * @param pixel the specified pixel 
     * @return the color and alpha components of the specified pixel
     * @see ColorModel#getRGBdefault
     */
    final public int getRGB(int pixel) {
	return rgb[pixel];
    }

    private static final int CACHESIZE = 40;
    private int lookupcache[] = new int[CACHESIZE];

    /**
     * Returns a data element array representation of a pixel in this
     * ColorModel, given an integer pixel representation in the
     * default RGB color model.  This array can then be passed to the 
     * {@link WritableRaster#setDataElements(int, int, java.lang.Object) setDataElements}
     * method of a {@link WritableRaster} object.  If the pixel variable is 
     * <code>null</code>, a new array is allocated.  If <code>pixel</code>
     * is not <code>null</code>, it must be
     * a primitive array of type <code>transferType</code>; otherwise, a
     * <code>ClassCastException</code> is thrown.  An 
     * <code>ArrayIndexOutOfBoundsException</code> is
     * thrown if <code>pixel</code> is not large enough to hold a pixel 
     * value for this <code>ColorModel</code>.  The pixel array is returned.
     * <p>
     * Since <code>IndexColorModel</code> can be subclassed, subclasses 
     * inherit the implementation of this method and if they don't 
     * override it then they throw an exception if they use an 
     * unsupported <code>transferType</code>.
     *
     * @param rgb the integer pixel representation in the default RGB
     * color model
     * @param pixel the specified pixel
     * @return an array representation of the specified pixel in this
     *  <code>IndexColorModel</code>.
     * @throws ClassCastException if <code>pixel</code>
     *  is not a primitive array of type <code>transferType</code>
     * @throws ArrayIndexOutOfBoundsException if
     *  <code>pixel</code> is not large enough to hold a pixel value
     *  for this <code>ColorModel</code>
     * @throws UnsupportedOperationException if <code>transferType</code>
     *         is invalid
     * @see WritableRaster#setDataElements
     * @see SampleModel#setDataElements
     */
    public synchronized Object getDataElements(int rgb, Object pixel) {
        int red = (rgb>>16) & 0xff;
        int green = (rgb>>8) & 0xff;
        int blue  = rgb & 0xff;
        int alpha = (rgb>>>24);
        int pix = 0;

	for (int i = CACHESIZE - 2; i >= 0; i -= 2) {
	    if ((pix = lookupcache[i]) == 0) {
		break;
	    }
	    if (rgb == lookupcache[i+1]) {
		return installpixel(pixel, ~pix);
	    }
	}

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

	    if (false && red == green && green == blue) {
		// Grayscale
	    }

            for (int i=0; i < map_size; i++) {
		int lutrgb = this.rgb[i];
		if (lutrgb == rgb) {
		    pix = i;
		    break;
		}
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
	System.arraycopy(lookupcache, 2, lookupcache, 0, CACHESIZE - 2);
	lookupcache[CACHESIZE - 1] = rgb;
	lookupcache[CACHESIZE - 2] = ~pix;
	return installpixel(pixel, pix);
    }

    private Object installpixel(Object pixel, int pix) {
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
            throw new UnsupportedOperationException("This method has not been "+
                             "implemented for transferType " + transferType);
        }
        return pixel;
    }

    /**
     * Returns an array of unnormalized color/alpha components for a 
     * specified pixel in this <code>ColorModel</code>.  The pixel value 
     * is specified as an int.  If the components array is <code>null</code>, 
     * a new array is allocated.  The components array is returned.  
     * Color/alpha components are stored in the components array starting 
     * at <code>offset</code> even if the array is allocated by this method.  
     * An <code>ArrayIndexOutOfBoundsException</code>
     * is thrown if  the components array is not <code>null</code> and is 
     * not large enough to hold all the color and alpha components 
     * starting at <code>offset</code>.
     * @param pixel the specified pixel
     * @param components the array to receive the color and alpha
     * components of the specified pixel
     * @param offset the offset into the <code>components</code> array at
     * which to start storing the color and alpha components
     * @return an array containing the color and alpha components of the
     * specified pixel starting at the specified offset.
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
     * Returns an array of unnormalized color/alpha components for
     * a specified pixel in this <code>ColorModel</code>.  The pixel 
     * value is specified by an array of data elements of type 
     * <code>transferType</code> passed in as an object reference.
     * If <code>pixel</code> is not a primitive array of type 
     * <code>transferType</code>, a <code>ClassCastException</code>
     * is thrown.  An <code>ArrayIndexOutOfBoundsException</code>
     * is thrown if <code>pixel</code> is not large enough to hold 
     * a pixel value for this <code>ColorModel</code>.  If the 
     * <code>components</code> array is <code>null</code>, a new array 
     * is allocated.  The <code>components</code> array is returned.  
     * Color/alpha components are stored in the <code>components</code> 
     * array starting at <code>offset</code> even if the array is
     * allocated by this method.  An 
     * <code>ArrayIndexOutOfBoundsException</code> is also
     * thrown if  the <code>components</code> array is not 
     * <code>null</code> and is not large enough to hold all the color 
     * and alpha components starting at <code>offset</code>.  
     * <p>
     * Since <code>IndexColorModel</code> can be subclassed, subclasses 
     * inherit the implementation of this method and if they don't
     * override it then they throw an exception if they use an 
     * unsupported <code>transferType</code>.
     *
     * @param pixel the specified pixel
     * @param components an array that receives the color and alpha  
     * components of the specified pixel
     * @param offset the index into the <code>components</code> array at
     * which to begin storing the color and alpha components of the
     * specified pixel
     * @return an array containing the color and alpha components of the
     * specified pixel starting at the specified offset.
     * @throws ArrayIndexOutOfBoundsException if <code>pixel</code>
     *            is not large enough to hold a pixel value for this
     *            <code>ColorModel</code> or if the 
     *            <code>components</code> array is not <code>null</code> 
     *            and is not large enough to hold all the color 
     *            and alpha components starting at <code>offset</code>
     * @throws ClassCastException if <code>pixel</code> is not a 
     *            primitive array of type <code>transferType</code>
     * @throws UnsupportedOperationException if <code>transferType</code>
     *         is not one of the supported transer types
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
     * Returns a pixel value represented as an int in this 
     * <code>ColorModel</code> given an array of unnormalized 
     * color/alpha components.  An 
     * <code>ArrayIndexOutOfBoundsException</code> 
     * is thrown if the <code>components</code> array is not large 
     * enough to hold all of the color and alpha components starting
     * at <code>offset</code>.  Since
     * <code>ColorModel</code> can be subclassed, subclasses inherit the
     * implementation of this method and if they don't override it then
     * they throw an exception if they use an unsupported transferType.
     * @param components an array of unnormalized color and alpha
     * components
     * @param offset the index into <code>components</code> at which to
     * begin retrieving the color and alpha components
     * @return an <code>int</code> pixel value in this
     * <code>ColorModel</code> corresponding to the specified components.
     * @throws ArrayIndexOutOfBoundsException if
     *  the <code>components</code> array is not large enough to
     *  hold all of the color and alpha components starting at
     *  <code>offset</code>
     * @throws UnsupportedOperationException if <code>transferType</code>
     *         is invalid
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
     * <code>ColorModel</code> given an array of unnormalized color/alpha 
     * components.  This array can then be passed to the 
     * <code>setDataElements</code> method of a <code>WritableRaster</code> 
     * object.  An <code>ArrayIndexOutOfBoundsException</code> is 
     * thrown if the 
     * <code>components</code> array is not large enough to hold all of the 
     * color and alpha components starting at <code>offset</code>.  
     * If the pixel variable is <code>null</code>, a new array
     * is allocated.  If <code>pixel</code> is not <code>null</code>, 
     * it must be a primitive array of type <code>transferType</code>; 
     * otherwise, a <code>ClassCastException</code> is thrown.
     * An <code>ArrayIndexOutOfBoundsException</code> is thrown if pixel 
     * is not large enough to hold a pixel value for this
     * <code>ColorModel</code>.
     * <p>
     * Since <code>IndexColorModel</code> can be subclassed, subclasses
     * inherit the implementation of this method and if they don't
     * override it then they throw an exception if they use an
     * unsupported <code>transferType</code>
     *
     * @param components an array of unnormalized color and alpha
     * components
     * @param offset the index into <code>components</code> at which to
     * begin retrieving color and alpha components
     * @param pixel the <code>Object</code> representing an array of color
     * and alpha components
     * @return an <code>Object</code> representing an array of color and
     * alpha components.
     * @throws ClassCastException if <code>pixel</code>
     *  is not a primitive array of type <code>transferType</code>
     * @throws ArrayIndexOutOfBoundsException if 
     *  <code>pixel</code> is not large enough to hold a pixel value
     *  for this <code>ColorModel</code> or the <code>components</code>
     *  array is not large enough to hold all of the color and alpha
     *  components starting at <code>offset</code>
     * @throws UnsupportedOperationException if <code>transferType</code>
     *         is not one of the supported transer types
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
     * Creates a <code>WritableRaster</code> with the specified width 
     * and height that has a data layout (<code>SampleModel</code>) 
     * compatible with this <code>ColorModel</code>.  This method
     * only works for color models with 16 or fewer bits per pixel.
     * <p>
     * Since <code>IndexColorModel</code> can be subclassed, any 
     * subclass that supports greater than 16 bits per pixel must
     * override this method.
     *
     * @param w the width to apply to the new <code>WritableRaster</code>
     * @param h the height to apply to the new <code>WritableRaster</code>
     * @return a <code>WritableRaster</code> object with the specified
     * width and height.
     * @throws UnsupportedOperationException if the number of bits in a
     *         pixel is greater than 16
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
      * Returns <code>true</code> if <code>raster</code> is compatible 
      * with this <code>ColorModel</code> or <code>false</code> if it 
      * is not compatible with this <code>ColorModel</code>.
      * @param raster the {@link Raster} object to test for compatibility
      * @return <code>true</code> if <code>raster</code> is compatible
      * with this <code>ColorModel</code>; <code>false</code> otherwise.
      * 
      */
    public boolean isCompatibleRaster(Raster raster) {

	int size = raster.getSampleModel().getSampleSize(0);
        return ((raster.getTransferType() == transferType) &&
		(raster.getNumBands() == 1) && ((1 << size) >= map_size));
    }
    
    /**
     * Creates a <code>SampleModel</code> with the specified 
     * width and height that has a data layout compatible with 
     * this <code>ColorModel</code>.  
     * @param w the width to apply to the new <code>SampleModel</code>
     * @param h the height to apply to the new <code>SampleModel</code> 
     * @return a <code>SampleModel</code> object with the specified
     * width and height.
     * @throws IllegalArgumentException if <code>w</code> or
     *         <code>h</code> is not greater than 0
     * @see SampleModel
     */
    public SampleModel createCompatibleSampleModel(int w, int h) {
        int[] off = new int[1];
        off[0] = 0;
        if (pixel_bits == 1 || pixel_bits == 2 || pixel_bits == 4) {
            return new MultiPixelPackedSampleModel(transferType, w, h,
                                                   pixel_bits);
        }
        else {
            return new ComponentSampleModel(transferType, w, h, 1, w,
                                            off);
        }
    }
    
    /** 
     * Checks if the specified <code>SampleModel</code> is compatible 
     * with this <code>ColorModel</code>.  If <code>sm</code> is
     * <code>null</code>, this method returns <code>false</code>.  
     * @param sm the specified <code>SampleModel</code>, 
     *           or <code>null</code>
     * @return <code>true</code> if the specified <code>SampleModel</code>
     * is compatible with this <code>ColorModel</code>; <code>false</code>
     * otherwise.
     * @see SampleModel 
     */
    public boolean isCompatibleSampleModel(SampleModel sm) {
        // fix 4238629
        if (! (sm instanceof ComponentSampleModel) &&
            ! (sm instanceof MultiPixelPackedSampleModel)   ) {
            return false;
        }

        // Transfer type must be the same
        if (sm.getTransferType() != transferType) {
            return false;
        }

        if (sm.getNumBands() != 1) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Returns a new <code>BufferedImage</code> of TYPE_INT_ARGB or 
     * TYPE_INT_RGB that has a <code>Raster</code> with pixel data 
     * computed by expanding the indices in the source <code>Raster</code>
     * using the color/alpha component arrays of this <code>ColorModel</code>.
     * If <code>forceARGB</code> is <code>true</code>, a TYPE_INT_ARGB image is
     * returned regardless of whether or not this <code>ColorModel</code>
     * has an alpha component array or a transparent pixel.
     * @param raster the specified <code>Raster</code>
     * @param forceARGB if <code>true</code>, the returned 
     *     <code>BufferedImage</code> is TYPE_INT_ARGB; otherwise it is
     *     TYPE_INT_RGB
     * @return a <code>BufferedImage</code> created with the specified
     *     <code>Raster</code>
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

    /**
     * Returns whether or not the pixel is valid.  
     * @param pixel the specified pixel value
     * @return <code>true</code> if <code>pixel</code>
     * is valid; <code>false</code> otherwise.
     */
    public boolean isValid(int pixel) {
        if (validBits == null) {
            return (pixel >= 0 && pixel < map_size);
        }
        else {
            return (pixel < map_size && validBits.testBit(pixel));
        }
    }
 		
    /**
     * Returns whether or not all of the pixels are valid.
     * @return <code>true</code> if all pixels are valid;
     * <code>false</code> otherwise.
     */
    public boolean isValid() {
        return (validBits == null);
    }
 		
    /**
     * Returns a <code>BigInteger</code> that indicates the valid/invalid
     * pixels in the colormap.  A bit is valid if the 
     * <code>BigInteger</code> value at that index is set, and is invalid
     * if the <code>BigInteger</code> value at that index is not set.
     * The only valid ranges to query in the <code>BigInteger</code> are
     * between 0 and map_size.
     * @return a <code>BigInteger</code> indicating the valid/invalid pixels.
     */
    public BigInteger getValidPixels() {
        if (validBits == null) {
            return getAllValid();
        }
        else {
            return validBits;
        }
    }
 
    private static int[] setBits(int bits, boolean hasAlpha) {
        int[] b = new int[3+(hasAlpha ? 1 : 0)];
        b[0] = b[1] = b[2] = 8;
        if (hasAlpha) {
            b[3] = 8;
        }
        return b;
    }

    /**
     * Disposes of system resources associated with this
     * <code>ColorModel</code> once this <code>ColorModel</code> is no
     * longer referenced.
     */    
    public void finalize() {
        ImageData.freeNativeICMData(this);
    }


    /**
     * Returns the <code>String</code> representation of the contents of
     * this <code>ColorModel</code>object.
     * @return a <code>String</code> representing the contents of this
     * <code>ColorModel</code> object.
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
