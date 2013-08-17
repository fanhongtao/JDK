/*
 * @(#)PackedColorModel.java	1.27 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.image;

import java.awt.Transparency;
import java.awt.color.ColorSpace;

/**
 * An abstract ColorModel class that works with pixel values which represent
 * color and alpha information as separate samples and which pack all
 * samples for a single pixel into a single int, short, or byte quantity.
 * This class can be used with an arbitrary ColorSpace.  The number of
 * color samples in the pixel values must be same as the number of color
 * components in the ColorSpace.  There may be a single alpha sample.
 * For those methods which use a primitive array pixel representation of
 * type transferType, the array length is always one.  Color and alpha
 * samples are stored in the single element of the array in bits indicated
 * by bit masks.  Each bit mask must be contiguous and masks must not overlap.
 * The same masks apply to the single int
 * pixel representation used by other methods.  The correspondence of
 * masks and color/alpha samples is as follows.  Masks are identified by
 * indices running from 0 through getNumComponents() - 1.  The first
 * getNumColorComponents() indices refer to color samples.  If an
 * alpha sample is present, it corresponds the last index.  The order
 * of the color indices is specified by the ColorSpace.  Typically, this
 * reflects the name of the color space type, e.g. for TYPE_RGB, index 0
 * corresponds to red, index 1 to green, and index 2 to blue.  The transfer
 * types supported are DataBuffer.TYPE_BYTE, DataBuffer.TYPE_USHORT,
 * and DataBuffer.TYPE_INT.
 * <p>
 * The translation from pixel values to color/alpha components for
 * display or processing purposes is a one-to-one correspondence of
 * samples to components.
 * A PackedColorModel is typically used with image data which uses masks
 * to define packed samples.  For example, a PackedColorModel can be used in
 * conjunction with a SinglePixelPackedSampleModel to construct a
 * BufferedImage.  Normally the masks used by the SampleModel and the
 * ColorModel would be the same.  However, if they are different, the
 * color interpretation of pixel data will be done according to the
 * masks of the ColorModel.
 * <p>
 * A single int pixel representation is valid for all objects of this
 * class, since it is always possible to represent pixel values used with
 * this class in a single int.  Therefore, methods which use this
 * representation will not throw an IllegalArgumentException due to
 * an invalid pixel value.
 * <p>
 * A subclass of PackedColorModel is DirectColorModel, which is similar to
 * an X11 TrueColor visual.
 *
 * @see DirectColorModel
 * @see SinglePixelPackedSampleModel
 * @see BufferedImage
 * @version 10 Feb 1997
 */

public abstract class PackedColorModel extends ColorModel {
    int[] maskArray;
    int[] maskOffsets;
    double[] scaleFactors;

    /**
     * Constructs a PackedColorModel from a color mask array, which specifies
     * which bits in an int pixel representation contain each of the
     * color samples, and an alpha mask.  Color components will be in the
     * specified ColorSpace.  The length of colorMaskArray should be the
     * number of components in the ColorSpace.  All of the bits in each mask
     * must be contiguous and fit in the specified number of least significant
     * bits of an int pixel representation.  If the alphaMask is 0,
     * there is no alpha.  If there is alpha, the boolean isAlphaPremultiplied
     * specifies how to interpret color and alpha samples in pixel values.
     * If the boolean is true, color samples are assumed to have been
     * multiplied by the alpha sample.  The transparency specifies what
     * alpha values can be represented by this color model.  The transfer type
     * is the type of primitive array used to represent pixel values.
     */
    public PackedColorModel (ColorSpace space, int bits,
                             int[] colorMaskArray, int alphaMask,
                             boolean isAlphaPremultiplied,
                             int trans, int transferType) {
        super(bits, PackedColorModel.createBitsArray(colorMaskArray,
                                                     alphaMask),
              space, (alphaMask == 0 ? false : true),
              isAlphaPremultiplied, trans, transferType);
        if (bits < 1 || bits > 32) {
            throw new IllegalArgumentException("Number of bits must be between"
                                               +" 1 and 32.");
        }
        maskArray   = new int[numComponents];
        maskOffsets = new int[numComponents];
        nBits       = new int[numComponents];
        scaleFactors = new double[numComponents];

        for (int i=0; i < numColorComponents; i++) {
            // Get the mask offset and #bits
            DecomposeMask(colorMaskArray[i], i, space.getName(i));
        }
        if (alphaMask != 0) {
            DecomposeMask(alphaMask , numColorComponents,
                          space.getName(numColorComponents));
            if (nBits[numComponents-1] == 1) {
                transparency = Transparency.BITMASK;
            }
        }
    }

    /**
     * Constructs a PackedColorModel from the given masks specifying
     * which bits in an int pixel representation contain the alpha, red,
     * green and blue color samples.  Color components will be in the
     * specified ColorSpace, which must be of type ColorSpace.TYPE_RGB.  All
     * of the bits in each mask must be contiguous and fit in the specified
     * number of least significant bits of an int pixel representation.  If
     * amask is 0, there is no alpha.  If there is alpha, the boolean
     * isAlphaPremultiplied specifies how to interpret color and alpha samples
     * in pixel values.  If the boolean is true, color samples are assumed
     * to have been multiplied by the alpha sample.  The transparency
     * specifies what  alpha values can be represented by this color model.
     * The transfer type is the type of primitive array used to represent
     * pixel values.
     * @see ColorSpace
     */
    public PackedColorModel(ColorSpace space, int bits, int rmask, int gmask,
                            int bmask, int amask,
                            boolean isAlphaPremultiplied,
                            int trans, int transferType) {
        super (bits, PackedColorModel.createBitsArray(rmask, gmask, bmask,
                                                      amask),
               space, (amask == 0 ? false : true),
               isAlphaPremultiplied, trans, transferType);

        if (space.getType() != ColorSpace.TYPE_RGB) {
            throw new IllegalArgumentException("ColorSpace must be TYPE_RGB.");
        }
        maskArray = new int[numComponents];
        maskOffsets = new int[numComponents];
        nBits       = new int[numComponents];
        scaleFactors = new double[numComponents];

        DecomposeMask(rmask, 0, "red");

        DecomposeMask(gmask, 1, "green");

        DecomposeMask(bmask, 2, "blue");

        if (amask != 0) {
            DecomposeMask(amask, 3, "alpha");
            if (nBits[3] == 1) {
                transparency = Transparency.BITMASK;
            }
        }
    }

    /**
     * Returns the mask indicating which bits in an int pixel representation
     * contain the specified color/alpha sample.  For color samples, the index
     * corresponds to the placement of color sample names in the color
     * space.  Thus, index 0 for a CMYK ColorSpace would correspond to
     * Cyan and index 1 would correspond to Magenta.  If there is alpha,
     * the alpha index would be:
     * <pre>
     *      alphaIndex = numComponents() - 1;
     * </pre>
     */
    final public int getMask(int index) {
	return maskArray[index];
    }

    /**
     * Returns a mask array indicating which bits in an int pixel
     * representation contain the color and alpha samples.  
     */
    final public int[] getMasks() {
	return (int[]) maskArray.clone();
    }

    /*
     * A utility function to decompose a single mask and verify that it
     * fits in the specified pixel size, and that it does not overlap any
     * other color component.
     */
    private void DecomposeMask(int mask,  int idx, String componentName) {
	int off = 0;
	int count = 0;

        // Store the mask
        maskArray[idx]   = mask;

        // Now find the #bits and shifts
	if (mask != 0) {
	    while ((mask & 1) == 0) {
		mask >>>= 1;
		off++;
	    }
	    while ((mask & 1) == 1) {
		mask >>>= 1;
		count++;
	    }
	}
	if (mask != 0) {
	    throw new IllegalArgumentException(componentName + " mask "+
                                        Integer.toHexString(maskArray[idx])+
                                               " bits not contiguous");
	}
	if (off + count > pixel_bits) {
	    throw new IllegalArgumentException(componentName + " mask "+
                                        Integer.toHexString(maskArray[idx])+
                                               " overflows pixel (expecting "+
                                               pixel_bits+" bits");
	}

	maskOffsets[idx] = off;
	nBits[idx]       = count;
	if (count == 0) {
	    // High enough to scale any 0-ff value down to 0.0, but not
	    // high enough to get Infinity when scaling back to pixel bits
	    scaleFactors[idx] = 256.0;
	} else {
	    scaleFactors[idx] = 255. /((1 << count) - 1);
	}

        // Store the maximum #bits per component
        if (maxBits < count) {
            maxBits = count;
        }
    }

    /**
     * Creates a SampleModel with the specified width and height that 
     * has a data layout compatible with this ColorModel.  
     * @see SampleModel
     */
    public SampleModel createCompatibleSampleModel(int w, int h) {
        return new SinglePixelPackedSampleModel(transferType, w, h,
                                                maskArray);
    }
    
    /** Checks if the SampleModel is compatible with this ColorModel.
     * @see SampleModel 
     */
    public boolean isCompatibleSampleModel(SampleModel sm) {
        if (! (sm instanceof SinglePixelPackedSampleModel)) {
            return false;
        }

        // Transfer type must be the same
        if (sm.getTransferType() != transferType) {
            return false;
        }

        SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel) sm;
        // Now compare the specific masks
        int[] bitMasks = sppsm.getBitMasks();
        if (bitMasks.length != maskArray.length) {
            return false;
        }
        for (int i=0; i < bitMasks.length; i++) {
            if (bitMasks[i] != maskArray[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns a Raster representing the alpha channel of an image,
     * extracted from the input Raster.
     * This method assumes that Rasters associated with this ColorModel
     * store the alpha band, if present, as the last band of image data.
     * Returns null if there is no separate spatial alpha channel
     * associated with this ColorModel.
     * This method will create a new Raster (but will share the data
     * array).
     */
    public WritableRaster getAlphaRaster(WritableRaster raster) {
        if (hasAlpha() == false) {
            return null;
        }

        int x = raster.getMinX();
        int y = raster.getMinY();
        int[] band = new int[1];
        band[0] = raster.getNumBands() - 1;
        return raster.createWritableChild(x, y, raster.getWidth(),
                                          raster.getHeight(), x, y,
                                          band);
    }

    /** 
     * Tests if the specified <code>Object</code> is an instance
     * of <code>PackedColorModel</code> and equals this
     * <code>PackedColorModel</code>.
     * @param obj the <code>Object</code> to test for equality
     * @return <code>true</code> if the specified <code>Object</code>
     * is an instance of <code>PackedColorModel</code> and equals this
     * <code>PackedColorModel</code>; <code>false</code> otherwise.
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof PackedColorModel)) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        PackedColorModel cm = (PackedColorModel) obj;
        int numC = cm.getNumComponents();
        if (numC != numComponents) {
            return false;
        }
        for(int i=0; i < numC; i++) {
            if (maskArray[i] != cm.getMask(i)) {
                return false;
            }
        }
        return true;
    }

    private final static int[] createBitsArray(int[]colorMaskArray,
                                               int alphaMask) {
        int numColors = colorMaskArray.length;
        int numAlpha = (alphaMask == 0 ? 0 : 1);
        int[] arr = new int[numColors+numAlpha];
        for (int i=0; i < numColors; i++) {
            arr[i] = countBits(colorMaskArray[i]);
            if (arr[i] < 0) {
                throw new IllegalArgumentException("Noncontiguous color mask ("
                                     + Integer.toHexString(colorMaskArray[i])+
                                     "at index "+i);
            }
        }
        if (alphaMask != 0) {
            arr[numColors] = countBits(alphaMask);
            if (arr[numColors] < 0) {
                throw new IllegalArgumentException("Noncontiguous alpha mask ("
                                     + Integer.toHexString(alphaMask));
            }
        }
        return arr;
    }

    private final static int[] createBitsArray(int rmask, int gmask, int bmask,
                                         int amask) {
        int[] arr = new int[3 + (amask == 0 ? 0 : 1)];
        arr[0] = countBits(rmask);
        arr[1] = countBits(gmask);
        arr[2] = countBits(bmask);
        if (arr[0] < 0) {
            throw new IllegalArgumentException("Noncontiguous red mask ("
                                     + Integer.toHexString(rmask));
        }
        else if (arr[1] < 0) {
            throw new IllegalArgumentException("Noncontiguous green mask ("
                                     + Integer.toHexString(gmask));
        }
        else if (arr[2] < 0) {
            throw new IllegalArgumentException("Noncontiguous blue mask ("
                                     + Integer.toHexString(bmask));
        }
        if (amask != 0) {
            arr[3] = countBits(amask);
            if (arr[3] < 0) {
                throw new IllegalArgumentException("Noncontiguous alpha mask ("
                                     + Integer.toHexString(amask));
            }
        }
        return arr;
    }

    private final static int countBits(int mask) {
        int saveMask = mask;
        int count = 0;
	if (mask != 0) {
	    while ((mask & 1) == 0) {
		mask >>>= 1;
	    }
	    while ((mask & 1) == 1) {
		mask >>>= 1;
		count++;
	    }
	}
        if (mask != 0) {
            return -1;
        }
        return count;
    }

}

