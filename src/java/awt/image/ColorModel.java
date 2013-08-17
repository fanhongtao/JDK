/*
 * @(#)ColorModel.java	1.13 98/07/01
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
 * A class that encapsulates the methods for translating from pixel values
 * to alpha, red, green, and blue color components for an image.  This
 * class is abstract.
 *
 * @see IndexColorModel
 * @see DirectColorModel
 *
 * @version	1.13 07/01/98
 * @author 	Jim Graham
 */
public abstract class ColorModel {
    private int pData;		// Placeholder for data for native functions

    protected int pixel_bits;

    private static ColorModel RGBdefault;

    /**
     * Return a ColorModel which describes the default format for
     * integer RGB values used throughout the AWT image interfaces.
     * The format for the RGB values is an integer with 8 bits
     * each of alpha, red, green, and blue color components ordered
     * correspondingly from the most significant byte to the least
     * significant byte, as in:  0xAARRGGBB
     */
    public static ColorModel getRGBdefault() {
	if (RGBdefault == null) {
	    RGBdefault = new DirectColorModel(32,
					      0x00ff0000,	// Red
					      0x0000ff00,	// Green
					      0x000000ff,	// Blue
					      0xff000000	// Alpha
					      );
	}
	return RGBdefault;
    }

    /**
     * Constructs a ColorModel which describes a pixel of the specified
     * number of bits.
     */
    public ColorModel(int bits) {
	pixel_bits = bits;
    }

    /**
     * Returns the number of bits per pixel described by this ColorModel.
     */
    public int getPixelSize() {
	return pixel_bits;
    }

    /**
     * The subclass must provide a function which provides the red
     * color compoment for the specified pixel.
     * @return		The red color component ranging from 0 to 255
     */
    public abstract int getRed(int pixel);

    /**
     * The subclass must provide a function which provides the green
     * color compoment for the specified pixel.
     * @return		The green color component ranging from 0 to 255
     */
    public abstract int getGreen(int pixel);

    /**
     * The subclass must provide a function which provides the blue
     * color compoment for the specified pixel.
     * @return		The blue color component ranging from 0 to 255
     */
    public abstract int getBlue(int pixel);

    /**
     * The subclass must provide a function which provides the alpha
     * color compoment for the specified pixel.
     * @return		The alpha transparency value ranging from 0 to 255
     */
    public abstract int getAlpha(int pixel);

    /**
     * Returns the color of the pixel in the default RGB color model.
     * @see ColorModel#getRGBdefault
     */
    public int getRGB(int pixel) {
	return (getAlpha(pixel) << 24)
	    | (getRed(pixel) << 16)
	    | (getGreen(pixel) << 8)
	    | (getBlue(pixel) << 0);
    }

    /* Throw away the compiled data stored in pData */
    private native void deletepData();

    public void finalize() {
	deletepData();
    }
}
