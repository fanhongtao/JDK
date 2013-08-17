/*
 * @(#)DirectColorModel.java	1.60 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.image;

import java.awt.color.ColorSpace;
import java.awt.Transparency;

/**
 * A ColorModel class that works with pixel values which represent RGB
 * color and alpha information as separate samples and which pack all
 * samples for a single pixel into a single int, short, or byte quantity.
 * This class can be used only with ColorSpaces of type ColorSpace.TYPE_RGB.
 * There must be three color samples in the pixel values and there may
 * be a single alpha sample.  For those methods which use a primitive array
 * pixel representation of type transferType, the array length is always
 * one.  Color and alpha samples are stored in the single element of the
 * array in bits indicated by bit masks.  Each bit mask must be contiguous
 * and masks must not overlap.  The same masks apply to the single int
 * pixel representation used by other methods.  The correspondence of
 * masks and color/alpha samples is as follows.  Masks are identified by
 * indices running from 0 through 2, if no alpha is present, or 3.  The
 * first three indices refer to color samples; index 0 corresponds to red,
 * index 1 to green, and index 2 to blue.  Index 3 corresponds to the alpha
 * sample, if present.  The transfer types supported are DataBuffer.TYPE_BYTE,
 * DataBuffer.TYPE_USHORT, and DataBuffer.TYPE_INT.
 * <p>
 * The translation from pixel values to color/alpha components for
 * display or processing purposes is a one-to-one correspondence of
 * samples to components.
 * A DirectColorModel is typically used with image data which uses masks
 * to define packed samples.  For example, a DirectColorModel can be used in
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
 * This color model is similar to an X11 TrueColor visual.
 * The default RGB ColorModel specified by the ColorModel.getRGBdefault
 * method is a DirectColorModel with the following parameters:
 * <pre>
 * Number of bits:        32
 * Red mask:              0x00ff0000
 * Green mask:            0x0000ff00
 * Blue mask:             0x000000ff
 * Alpha mask:            0xff000000
 * Color space:           sRGB
 * isAlphaPremultiplied:  False
 * Transparency:          Transparency.TRANSLUCENT
 * transferType:          DataBuffer.TYPE_INT
 * </pre>
 * <p>
 * Many of the methods in this class are final. This is because the
 * underlying native graphics code makes  assumptions about the layout
 * and operation of this class  and those assumptions are reflected in
 * the implementations of the methods here that are marked final.  You
 * can subclass this class  for other reasons,  but you cannot override
 * or modify the behavior of those methods.
 *
 * @see ColorModel
 * @see ColorSpace
 * @see SinglePixelPackedSampleModel
 * @see BufferedImage
 * @see ColorModel#getRGBdefault
 *
 * @version 10 Feb 1997
 */
public class DirectColorModel extends PackedColorModel {
    private int red_mask;
    private int green_mask;
    private int blue_mask;
    private int alpha_mask;
    private int red_offset;
    private int green_offset;
    private int blue_offset;
    private int alpha_offset;
    private int red_scale;
    private int green_scale;
    private int blue_scale;
    private int alpha_scale;

    /**
     * Constructs a DirectColorModel from the given masks specifying which
     * bits in an int pixel representation contain the red, green and blue
     * color samples.  Pixel values do not contain alpha information, so
     * all pixels will be treated as opaque (alpha = 1.0).  All of the bits
     * in each mask must be contiguous and fit in the specified number
     * of least significant bits of an int pixel representation.  The
     * ColorSpace will be the default sRGB space. The
     * transparency value will be Transparency.OPAQUE.  The transfer type
     * will be the smallest of DataBuffer.TYPE_BYTE, DataBuffer.TYPE_USHORT,
     * or DataBuffer.TYPE_INT that can hold a single pixel.
     */
    public DirectColorModel(int bits,
			    int rmask, int gmask, int bmask) {
	this(bits, rmask, gmask, bmask, 0);
    }

    /**
     * Constructs a DirectColorModel from the given masks specifying which
     * bits in an int pixel representation contain the red, green and blue
     * color samples and the alpha sample, if present.  If amask is 0,
     * pixel values do not contain alpha information, so all pixels will
     * be treated as opaque (alpha = 1.0).  All of the bits in each mask must
     * be contiguous and fit in the specified number of least significant bits
     * of an int pixel representation.  Alpha, if present, will not be
     * premultiplied.  The ColorSpace will be the default sRGB space. The
     * transparency value will be Transparency.OPAQUE, if no alpha is
     * present, or Transparency.TRANSLUCENT otherwise.  The transfer type
     * will be the smallest of DataBuffer.TYPE_BYTE, DataBuffer.TYPE_USHORT,
     * or DataBuffer.TYPE_INT that can hold a single pixel.
     */
    public DirectColorModel(int bits, int rmask, int gmask,
                            int bmask, int amask) {
        super (ColorSpace.getInstance(ColorSpace.CS_sRGB),
               bits, rmask, gmask, bmask, amask, false,
               amask == 0 ? Transparency.OPAQUE : Transparency.TRANSLUCENT,
               ColorModel.getDefaultTransferType(bits));
        setFields();
    }

    /**
     * Constructs a DirectColorModel from the specified parameters.
     * Color components will be in the specified ColorSpace, which must
     * be of type ColorSpace.TYPE_RGB.
     * The masks specify which bits in an int pixel representation contain
     * the red, green and blue color samples and the alpha sample, if present.
     * If amask is 0, pixel values do not contain alpha information, so
     * all pixels will be treated as opaque (alpha = 1.0).  All of the bits
     * in each mask must be contiguous and fit in the specified number of
     * least significant bits of an int pixel representation.  If there
     * is alpha, the boolean isAlphaPremultiplied specifies how to interpret
     * color and alpha samples in pixel values.  If the boolean is true, color
     * samples are assumed to have been multiplied by the alpha sample.
     * The transparency value will be Transparency.OPAQUE, if no alpha is
     * present, or Transparency.TRANSLUCENT otherwise.  The transfer type
     * is the type of primitive array used to represent pixel values and
     * must be one of DataBuffer.TYPE_BYTE, DataBuffer.TYPE_USHORT, or
     * DataBuffer.TYPE_INT.
     */
    public DirectColorModel(ColorSpace space, int bits, int rmask,
                            int gmask, int bmask, int amask,
                            boolean isAlphaPremultiplied,
                            int transferType) {
        super (space, bits, rmask, gmask, bmask, amask,
               isAlphaPremultiplied,
               amask == 0 ? Transparency.OPAQUE : Transparency.TRANSLUCENT,
               transferType);
        setFields();
    }

    /**
     * Returns the mask indicating which bits in an int pixel representation
     * contain the red color component.
     */
    final public int getRedMask() {
	return maskArray[0];
    }

    /**
     * Returns the mask indicating which bits in an int pixel representation
     * contain the green color component.
     */
    final public int getGreenMask() {
	return maskArray[1];
    }

    /**
     * Returns the mask indicating which bits in an int pixel representation
     * contain the blue color component.
     */
    final public int getBlueMask() {
	return maskArray[2];
    }

    /**
     * Returns the mask indicating which bits in an int pixel representation
     * contain the alpha component.
     */
    final public int getAlphaMask() {
        if (supportsAlpha) {
            return maskArray[3];
        } else {
            return 0;
        }
    }


    /**
     * Returns the red color component for the specified pixel, scaled
     * from 0 to 255 in the default RGB ColorSpace, sRGB.  A color conversion
     * is done if necessary.  The pixel value is specified as an int.
     * The returned value will be a non pre-multiplied value, i.e. if the
     * alpha is premultiplied, this method will divide it out before returning
     * the value (if the alpha value is 0, the red value will be 0).
     */
    final public int getRed(int pixel) {
	int r = ((pixel & maskArray[0]) >>> maskOffsets[0]);
	if (scaleFactors[0] != 1.) {
	    r = (int)(r * scaleFactors[0]);
	}
        if (isAlphaPremultiplied) {
            int a = getAlpha(pixel);
            r = (a == 0) ? 0 : (r * 255/a);
            if (r > 255) {
                r = 255;
            }
        }
	return r;
    }

    /**
     * Returns the green color component for the specified pixel, scaled
     * from 0 to 255 in the default RGB ColorSpace, sRGB.  A color conversion
     * is done if necessary.  The pixel value is specified as an int.
     * The returned value will be a non pre-multiplied value, i.e. if the
     * alpha is premultiplied, this method will divide it out before returning
     * the value (if the alpha value is 0, the green value will be 0).
     */
    final public int getGreen(int pixel) {
	int g = ((pixel & maskArray[1]) >>> maskOffsets[1]);
	if (scaleFactors[1] != 1.) {
	    g = (int) (g * scaleFactors[1]);
	}

        if (isAlphaPremultiplied) {
            int a = getAlpha(pixel);
            g = (a == 0) ? 0 : (g * 255/a);
            if (g > 255) {
                g = 255;
            }
        }
	return g;
    }

    /**
     * Returns the blue color component for the specified pixel, scaled
     * from 0 to 255 in the default RGB ColorSpace, sRGB.  A color conversion
     * is done if necessary.  The pixel value is specified as an int.
     * The returned value will be a non pre-multiplied value, i.e. if the
     * alpha is premultiplied, this method will divide it out before returning
     * the value (if the alpha value is 0, the blue value will be 0).
     */
    final public int getBlue(int pixel) {
	int b = ((pixel & maskArray[2]) >>> maskOffsets[2]);
	if (scaleFactors[2] != 1.) {
	    b = (int)(b * scaleFactors[2]);
	}

        if (isAlphaPremultiplied) {
            int a = getAlpha(pixel);
            b = (a == 0) ? 0 : (b * 255/a);
            if (b > 255) {
                b = 255;
            }
        }
	return b;
    }

    /**
     * Returns the alpha component for the specified pixel, scaled
     * from 0 to 255.  The pixel value is specified as an int.
     */
    final public int getAlpha(int pixel) {
	if (!supportsAlpha) return 255;
	int a = ((pixel & maskArray[3]) >>> maskOffsets[3]);
	if (scaleFactors[3] != 1.) {
	    a = (int)(a * scaleFactors[3]);
	}
	return a;
    }

    /**
     * Returns the color/alpha components of the pixel in the default
     * RGB color model format.  A color conversion is done if necessary.
     * The pixel value is specified as an int.
     * The returned value will be in a non pre-multiplied format, i.e. if
     * the alpha is premultiplied, this method will divide it out of the
     * color components (if the alpha value is 0, the color values will be 0).
     * @see ColorModel#getRGBdefault
     */
    final public int getRGB(int pixel) {
	return (getAlpha(pixel) << 24)
	    | (getRed(pixel) << 16)
	    | (getGreen(pixel) << 8)
	    | (getBlue(pixel) << 0);
    }

    /**
     * Returns the red color component for the specified pixel, scaled
     * from 0 to 255 in the default RGB ColorSpace, sRGB.  A color conversion
     * is done if necessary.  The pixel value is specified by an array
     * of data elements of type transferType passed in as an object reference.
     * The returned value will be a non pre-multiplied value, i.e. if the
     * alpha is premultiplied, this method will divide it out before returning
     * the value (if the alpha value is 0, the red value will be 0).
     * If inData is not a primitive array of type transferType, a
     * ClassCastException is thrown.  An ArrayIndexOutOfBoundsException is
     * thrown if inData is not large enough to hold a pixel value for this
     * ColorModel.
     */
    public int getRed(Object inData) {
        int pixel=0;
        switch (transferType) {
            case DataBuffer.TYPE_BYTE:
               byte bdata[] = (byte[])inData;
               pixel = bdata[0] & 0xff;
            break;
            case DataBuffer.TYPE_USHORT:
               short sdata[] = (short[])inData;
               pixel = sdata[0] & 0xffff;
            break;
            case DataBuffer.TYPE_INT:
               int idata[] = (int[])inData;
               pixel = idata[0];
            break;
            default:
               throw new UnsupportedOperationException("This method has not been "+
                   "implemented for transferType " + transferType);
        }
        return getRed(pixel);
    }


    /**
     * Returns the green color component for the specified pixel, scaled
     * from 0 to 255 in the default RGB ColorSpace, sRGB.  A color conversion
     * is done if necessary.  The pixel value is specified by an array
     * of data elements of type transferType passed in as an object reference.
     * The returned value will be a non pre-multiplied value, i.e. if the
     * alpha is premultiplied, this method will divide it out before returning
     * the value (if the alpha value is 0, the green value will be 0).
     * If inData is not a primitive array of type transferType, a
     * ClassCastException is thrown.  An ArrayIndexOutOfBoundsException is
     * thrown if inData is not large enough to hold a pixel value for this
     * ColorModel.
     */
    public int getGreen(Object inData) {
        int pixel=0;
        switch (transferType) {
            case DataBuffer.TYPE_BYTE:
               byte bdata[] = (byte[])inData;
               pixel = bdata[0] & 0xff;
            break;
            case DataBuffer.TYPE_USHORT:
               short sdata[] = (short[])inData;
               pixel = sdata[0] & 0xffff;
            break;
            case DataBuffer.TYPE_INT:
               int idata[] = (int[])inData;
               pixel = idata[0];
            break;
            default:
               throw new UnsupportedOperationException("This method has not been "+
                   "implemented for transferType " + transferType);
        }
        return getGreen(pixel);
    }


    /**
     * Returns the blue color component for the specified pixel, scaled
     * from 0 to 255 in the default RGB ColorSpace, sRGB.  A color conversion
     * is done if necessary.  The pixel value is specified by an array
     * of data elements of type transferType passed in as an object reference.
     * The returned value will be a non pre-multiplied value, i.e. if the
     * alpha is premultiplied, this method will divide it out before returning
     * the value (if the alpha value is 0, the blue value will be 0).
     * If inData is not a primitive array of type transferType, a
     * ClassCastException is thrown.  An ArrayIndexOutOfBoundsException is
     * thrown if inData is not large enough to hold a pixel value for this
     * ColorModel.
     */
    public int getBlue(Object inData) {
        int pixel=0;
        switch (transferType) {
            case DataBuffer.TYPE_BYTE:
               byte bdata[] = (byte[])inData;
               pixel = bdata[0] & 0xff;
            break;
            case DataBuffer.TYPE_USHORT:
               short sdata[] = (short[])inData;
               pixel = sdata[0] & 0xffff;
            break;
            case DataBuffer.TYPE_INT:
               int idata[] = (int[])inData;
               pixel = idata[0];
            break;
            default:
               throw new UnsupportedOperationException("This method has not been "+
                   "implemented for transferType " + transferType);
        }
        return getBlue(pixel);
    }

    /**
     * Returns the alpha component for the specified pixel, scaled
     * from 0 to 255.  The pixel value is specified by an array of data
     * elements of type transferType passed in as an object reference.
     * If inData is not a primitive array of type transferType, a
     * ClassCastException is thrown.  An ArrayIndexOutOfBoundsException is
     * thrown if inData is not large enough to hold a pixel value for this
     * ColorModel.
     */
    public int getAlpha(Object inData) {
        int pixel=0;
        switch (transferType) {
            case DataBuffer.TYPE_BYTE:
               byte bdata[] = (byte[])inData;
               pixel = bdata[0] & 0xff;
            break;
            case DataBuffer.TYPE_USHORT:
               short sdata[] = (short[])inData;
               pixel = sdata[0] & 0xffff;
            break;
            case DataBuffer.TYPE_INT:
               int idata[] = (int[])inData;
               pixel = idata[0];
            break;
            default:
               throw new UnsupportedOperationException("This method has not been "+
                   "implemented for transferType " + transferType);
        }
        return getAlpha(pixel);
    }

    /**
     * Returns the color/alpha components for the specified pixel in the
     * default RGB color model format.  A color conversion is done if
     * necessary.  The pixel value is specified by an array of data
     * elements of type transferType passed in as an object reference.
     * If inData is not a primitive array of type transferType, a
     * ClassCastException is thrown.  An ArrayIndexOutOfBoundsException is
     * thrown if inData is not large enough to hold a pixel value for this
     * ColorModel.
     * The returned value will be in a non pre-multiplied format, i.e. if
     * the alpha is premultiplied, this method will divide it out of the
     * color components (if the alpha value is 0, the color values will be 0).
     * @see ColorModel#getRGBdefault
     */
    public int getRGB(Object inData) {
        int pixel=0;
        switch (transferType) {
            case DataBuffer.TYPE_BYTE:
               byte bdata[] = (byte[])inData;
               pixel = bdata[0] & 0xff;
            break;
            case DataBuffer.TYPE_USHORT:
               short sdata[] = (short[])inData;
               pixel = sdata[0] & 0xffff;
            break;
            case DataBuffer.TYPE_INT:
               int idata[] = (int[])inData;
               pixel = idata[0];
            break;
            default:
               throw new UnsupportedOperationException("This method has not been "+
                   "implemented for transferType " + transferType);
        }
        return getRGB(pixel);
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
        //REMIND: maybe more efficient not to use int array for
        //DataBuffer.TYPE_USHORT and DataBuffer.TYPE_INT
        int i;
        int intpixel[] = null;
        if (transferType == DataBuffer.TYPE_INT &&
            pixel != null) {
           intpixel = (int[])pixel;
        } else {
            intpixel = new int[1];
        }

        ColorModel defaultCM = ColorModel.getRGBdefault();
        if (this == defaultCM || equals(defaultCM)) {
            intpixel[0] = rgb;
        } else {
            double normAlpha = 1.;
            int c;
            if (isAlphaPremultiplied) {
                normAlpha = ((rgb>>24)&0xff)/255.;
            }

            double value;
            int[] shifts = {16, 8, 0, 24};
            int nbands = 3+(supportsAlpha ? 1 : 0);

            intpixel[0] = 0;
            for (i=0; i < nbands; i++) {
                c = (int)(((rgb>>shifts[i])&0xff)*normAlpha/scaleFactors[i]);
                intpixel[0] |= (c << maskOffsets[i]) & maskArray[i];
            }
        }
        switch (transferType) {
            case DataBuffer.TYPE_BYTE: {
               byte bdata[];
               if (pixel == null) {
                   bdata = new byte[1];
               } else {
                   bdata = (byte[])pixel;
               }
               bdata[0] = (byte)(0xff&intpixel[0]);
               return bdata;
            }
            case DataBuffer.TYPE_USHORT:{
               short sdata[];
               if (pixel == null) {
                   sdata = new short[1];
               } else {
                   sdata = (short[])pixel;
               }
               sdata[0] = (short)(intpixel[0]&0xffff);
               return sdata;
            }
            case DataBuffer.TYPE_INT:
               return intpixel;
        }
        throw new UnsupportedOperationException("This method has not been "+
                 "implemented for transferType " + transferType);

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
    final public int[] getComponents(int pixel, int[] components, int offset) {
        if (components == null) {
            components = new int[offset+numComponents];
        }

        for (int i=0; i < numComponents; i++) {
            components[offset+i] = (pixel & maskArray[i]) >>> maskOffsets[i];
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
    final public int[] getComponents(Object pixel, int[] components,
                                     int offset) {
        int intpixel=0;
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
     * Creates a WritableRaster with the specified width and height that
     * has a data layout (SampleModel) compatible with this ColorModel.
     * @see WritableRaster
     * @see SampleModel
     */
    final public WritableRaster createCompatibleWritableRaster (int w,
                                                                int h) {
        int[] bandmasks;
        if (supportsAlpha) {
            bandmasks = new int[4];
            bandmasks[3] = alpha_mask;
        }
        else {
            bandmasks = new int[3];
        }
        bandmasks[0] = red_mask;
        bandmasks[1] = green_mask;
        bandmasks[2] = blue_mask;

        if (pixel_bits > 16) {
	    return Raster.createPackedRaster(DataBuffer.TYPE_INT,
                                             w,h,bandmasks,null);
        }
        else if (pixel_bits > 8) {
	    return Raster.createPackedRaster(DataBuffer.TYPE_USHORT,
                                             w,h,bandmasks,null);
        }
        else {
	    return Raster.createPackedRaster(DataBuffer.TYPE_BYTE,
                                             w,h,bandmasks,null);
        }
    }

    /**
     * Returns a pixel value represented as an int in this ColorModel,
     * given an array of unnormalized color/alpha components.   An
     * ArrayIndexOutOfBoundsException is thrown if  the components array is
     * not large enough to hold all the color and alpha components (starting
     * at offset).
     */
    public int getDataElement(int[] components, int offset) {
        int pixel = 0;
        for (int i=0; i < numComponents; i++) {
            pixel |= ((components[offset+i]<<maskOffsets[i])&maskArray[i]);
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
     * (starting at offset).  If the obj variable is null, a new array
     * will be allocated.  If obj is not null, it must be a primitive array
     * of type transferType; otherwise, a ClassCastException is thrown.
     * An ArrayIndexOutOfBoundsException is thrown if obj is not large
     * enough to hold a pixel value for this ColorModel.
     * @see WritableRaster#setDataElements
     * @see SampleModel#setDataElements
     */
    public Object getDataElements(int[] components, int offset, Object obj) {
        int pixel = 0;
        for (int i=0; i < numComponents; i++) {
            pixel |= ((components[offset+i]<<maskOffsets[i])&maskArray[i]);
        }
        switch (transferType) {
            case DataBuffer.TYPE_BYTE:
               if (obj instanceof byte[]) {
                   byte bdata[] = (byte[])obj;
                   bdata[0] = (byte)(pixel&0xff);
                   return bdata;
               } else {
                   byte bdata[] = {(byte)(pixel&0xff)};
                   return bdata;
               }
            case DataBuffer.TYPE_USHORT:
               if (obj instanceof short[]) {
                   short sdata[] = (short[])obj;
                   sdata[0] = (short)(pixel&0xffff);
                   return sdata;
               } else {
                   short sdata[] = {(short)(pixel&0xffff)};
                   return sdata;
               }
            case DataBuffer.TYPE_INT:
               if (obj instanceof int[]) {
                   int idata[] = (int[])obj;
                   idata[0] = pixel;
                   return idata;
               } else {
                   int idata[] = {pixel};
                   return idata;
               }
            default:
               throw new ClassCastException("This method has not been "+
                   "implemented for transferType " + transferType);
        }
    }

    /**
     * Forces the Raster data to match the state specified in the
     * isAlphaPremultiplied variable, assuming the data is currently
     * correctly described by this ColorModel.  It may multiply or divide
     * the color Raster data by alpha, or do nothing if the data is
     * in the correct state.  If the data needs to be coerced, this
     * method will also return an instance of this ColorModel with
     * the isAlphaPremultiplied flag set appropriately.
     */
    final public ColorModel coerceData (WritableRaster raster,
                                        boolean isAlphaPremultiplied)
    {
        if (!supportsAlpha ||
            this.isAlphaPremultiplied() == isAlphaPremultiplied) {
            return this;
        }

        int w = raster.getWidth();
        int h = raster.getHeight();
        int aIdx = numColorComponents;
        float normAlpha;
        int alphaScale = (1 << nBits[aIdx]) - 1;

        int rminX = raster.getMinX();
        int rY = raster.getMinY();
        int rX;
        int pixel[] = null;

        if (isAlphaPremultiplied) {
            // Must mean that we are currently not premultiplied so
            // multiply by alpha
            switch (transferType) {
                case DataBuffer.TYPE_BYTE: {
                    for (int y = 0; y < h; y++, rY++) {
                        rX = rminX;
                        for (int x = 0; x < w; x++, rX++) {
                            pixel = raster.getPixel(rX, rY, pixel);
                            normAlpha = pixel[aIdx]/alphaScale;
                            if (normAlpha != 0.f) {
                                for (int c=0; c < aIdx; c++) {
                                    pixel[c] = (int)(pixel[c]*normAlpha);
                                }
                                raster.setPixel(rX, rY, pixel);
                            }
                        }
                    }
                }
                break;
                case DataBuffer.TYPE_USHORT: {
                    for (int y = 0; y < h; y++, rY++) {
                        rX = rminX;
                        for (int x = 0; x < w; x++, rX++) {
                            pixel = raster.getPixel(rX, rY, pixel);
                            normAlpha = pixel[aIdx]/alphaScale;
                            if (normAlpha != 0.f) {
                                for (int c=0; c < aIdx; c++) {
                                    pixel[c] = (int)(pixel[c]*normAlpha);
                                }
                                raster.setPixel(rX, rY, pixel);
                            }
                        }
                    }
                }
                break;
                case DataBuffer.TYPE_INT: {
                    for (int y = 0; y < h; y++, rY++) {
                        rX = rminX;
                        for (int x = 0; x < w; x++, rX++) {
                            pixel = raster.getPixel(rX, rY, pixel);
                            normAlpha = pixel[aIdx]/alphaScale;
                            if (normAlpha != 0.f) {
                                for (int c=0; c < aIdx; c++) {
                                    pixel[c] = (int)(pixel[c]*normAlpha);
                                }
                                raster.setPixel(rX, rY, pixel);
                            }
                        }
                    }
                }
                break;
                default:
                    throw new UnsupportedOperationException("This method has not been "+
                         "implemented for transferType " + transferType);
            }
        }
        else {
            // We are premultiplied and want to divide it out
            switch (transferType) {
                case DataBuffer.TYPE_BYTE: {
                    for (int y = 0; y < h; y++, rY++) {
                        rX = rminX;
                        for (int x = 0; x < w; x++, rX++) {
                            pixel = raster.getPixel(rX, rY, pixel);
                            normAlpha = pixel[aIdx]/alphaScale;
                            if (normAlpha != 0) {
                                for (int c=0; c < aIdx; c++) {
                                    pixel[c] /= normAlpha;
                                }
                            }
                            raster.setPixel(rX, rY, pixel);
                        }
                    }
                }
                break;
                case DataBuffer.TYPE_USHORT: {
                    for (int y = 0; y < h; y++, rY++) {
                        rX = rminX;
                        for (int x = 0; x < w; x++, rX++) {
                            pixel = raster.getPixel(rX, rY, pixel);
                            normAlpha = pixel[aIdx]/alphaScale;
                            if (normAlpha != 0) {
                                for (int c=0; c < aIdx; c++) {
                                    pixel[c] /= normAlpha;
                                }
                            }
                            raster.setPixel(rX, rY, pixel);
                        }
                    }
                }
                break;
                case DataBuffer.TYPE_INT: {
                    for (int y = 0; y < h; y++, rY++) {
                        rX = rminX;
                        for (int x = 0; x < w; x++, rX++) {
                            pixel = raster.getPixel(rX, rY, pixel);
                            normAlpha = pixel[aIdx]/alphaScale;
                            if (normAlpha != 0) {
                                for (int c=0; c < aIdx; c++) {
                                    pixel[c] /= normAlpha;
                                }
                            }
                            raster.setPixel(rX, rY, pixel);
                        }
                    }
                }
                break;
                default:
                    throw new UnsupportedOperationException("This method has not been "+
                         "implemented for transferType " + transferType);
            }
        }

        // Return a new color model
        return new DirectColorModel(colorSpace, pixel_bits, maskArray[0],
                                    maskArray[1], maskArray[2], maskArray[3],
                                    isAlphaPremultiplied,
                                    transferType);

    }

    /**
      * Returns true if raster is compatible with this ColorModel and
      * false if it is not.
      */
    public boolean isCompatibleRaster(Raster raster) {
        SampleModel sm = raster.getSampleModel();
        SinglePixelPackedSampleModel spsm;
        if (sm instanceof SinglePixelPackedSampleModel) {
            spsm = (SinglePixelPackedSampleModel) sm;
        }
        else {
            return false;
        }
        if (spsm.getNumBands() != getNumComponents()) {
            return false;
        }
	boolean flag = true;
	int[] bitMasks = spsm.getBitMasks();
	int[] bitSizes = spsm.getSampleSize();
        int[] bitOffsets = spsm.getBitOffsets();
	int totalBitSize = 0;
        /*if[Linux]
        int testBitSize=0;
        end[Linux]*/

	for(int i=0; i<bitSizes.length; i++) {
            if (bitSizes[i]+bitOffsets[i] > totalBitSize) {
                totalBitSize = bitSizes[i]+bitOffsets[i];
            }
        }

	for (int i=0; i<numComponents; i++) {
	    if (bitMasks[i] != maskArray[i])
		flag = false;
/*if[Linux]
            testBitSize+=bitSizes[i];
end[Linux]*/
	}

        return ( (raster.getTransferType() == transferType) &&
        /*if[Linux]
                 (totalBitSize == testBitSize ) && flag );
        else[Linux]*/
		 (totalBitSize == pixel_bits ) && flag );
        /*end[Linux]*/
    }

    private void setFields() {
        // Set the private fields
        // REMIND: Get rid of these from the native code
        red_mask     = maskArray[0];
        red_offset   = maskOffsets[0];
        green_mask   = maskArray[1];
        green_offset = maskOffsets[1];
        blue_mask    = maskArray[2];
        blue_offset  = maskOffsets[2];
        if (nBits[0] < 8) {
            red_scale = (1 << nBits[0]) - 1;
        }
        if (nBits[1] < 8) {
            green_scale = (1 << nBits[1]) - 1;
        }
        if (nBits[2] < 8) {
            blue_scale = (1 << nBits[2]) - 1;
        }
        if (supportsAlpha) {
            alpha_mask   = maskArray[3];
            alpha_offset = maskOffsets[3];
            if (nBits[3] < 8) {
                alpha_scale = (1 << nBits[3]) - 1;
            }
        }
    }

    /**
     * Returns a <code>String</code> that represents this
     * <code>DirectColorModel</code>.
     * @return a <code>String</code> representing this
     * <code>DirectColorModel</code>.
     */
    public String toString() {
        return new String("DirectColorModel: rmask="
                          +Integer.toHexString(red_mask)+" gmask="
                          +Integer.toHexString(green_mask)+" bmask="
                          +Integer.toHexString(blue_mask)+" amask="
                          +Integer.toHexString(alpha_mask));
    }
}

