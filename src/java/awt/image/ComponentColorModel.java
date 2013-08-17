/*
 * @(#)ComponentColorModel.java	1.47 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.image;

import java.awt.color.ColorSpace;

/**
 * A <CODE>ColorModel</CODE> class that works with pixel values that 
 * represent color and alpha information as separate samples and that 
 * store each sample in a separate data element.  This class can be 
 * used with an arbitrary <CODE>ColorSpace</CODE>.  The number of 
 * color samples in the pixel values must be same as the number of 
 * color components in the <CODE>ColorSpace</CODE>. There may be a 
 * single alpha sample.  
 * <p>
 * For those methods that use
 * a primitive array pixel representation of type <CODE>transferType</CODE>,
 * the array length is the same as the number of color and alpha samples.
 * Color samples are stored first in the array followed by the alpha
 * sample, if present.  The order of the color samples is specified
 * by the <CODE>ColorSpace</CODE>.  Typically, this order reflects the 
 * name of the color space type. For example, for <CODE>TYPE_RGB</CODE>, 
 * index 0 corresponds to red, index 1 to green, and index 2 to blue.  
 * The transfer types supported are <CODE>DataBuffer.TYPE_BYTE</CODE>, 
 * <CODE>DataBuffer.TYPE_USHORT</CODE>, and <CODE>DataBuffer.TYPE_INT</CODE>.
 * <p>
 * The translation from pixel values to color/alpha components for
 * display or processing purposes is a one-to-one correspondence of
 * samples to components.
 * The number of bits in a color or alpha sample of a pixel value might not
 * be the same as the number of bits for the corresponding color or alpha
 * component passed to the <CODE>ComponentColorModel</CODE> constructor.  
 * This class assumes that the least significant n bits of a sample value 
 * hold the component value, where n is the number of significant bits 
 * for the component passed to the constructor.  It also assumes that 
 * any higher-order bits in a sample value are zero.
 * <p>
 * Methods that use a single int pixel representation throw
 * an <CODE>IllegalArgumentException</CODE>, unless the number of components 
 * for the <CODE>ComponentColorModel</CODE> is one--in other words,  a single 
 * color component and no alpha.
 * <p>
 * A <CODE>ComponentColorModel</CODE> can be used in conjunction with a 
 * <CODE>ComponentSampleModel</CODE>, a <CODE>BandedSampleModel</CODE>, 
 * or a <CODE>PixelInterleavedSampleModel</CODE> to construct a
 * <CODE>BufferedImage</CODE>.
 *
 * @see ColorModel
 * @see ColorSpace
 * @see ComponentSampleModel
 * @see BandedSampleModel
 * @see PixelInterleavedSampleModel
 * @see BufferedImage
 *
 * @version 10 Feb 1997
 */
public class ComponentColorModel extends ColorModel {
    
    /**
     * Constructs a <CODE>ComponentColorModel</CODE> from the specified 
     * parameters. Color components will be in the specified <CODE>ColorSpace</CODE>.  
     * The <CODE>bits</CODE> array specifies the number of significant bits 
     * per color and alpha component.  Its length should be the number 
     * of components in the <CODE>ColorSpace</CODE> if there is no alpha 
     * information in the pixel values, or one more than this number if 
     * there is alpha information.  An <CODE>IllegalArgumentException</CODE> 
     * is thrown if the length of the array does not match the number of components.  
     * <CODE>hasAlpha</CODE> indicates whether alpha information is present.  If 
     * <CODE>hasAlpha</CODE> is true, then the boolean <CODE>isAlphaPremultiplied</CODE> 
     * specifies how to interpret color and alpha samples in pixel values.  
     * If the boolean is true, color samples are assumed to have been multiplied 
     * by the alpha sample. The <CODE>transparency</CODE> specifies what alpha 
     * values can be represented by this color model.  The <CODE>transferType</CODE> 
     * is the type of primitive array used to represent pixel values.  Note that 
     * the <CODE>bits</CODE> array contains the number of significant bits per 
     * color/alpha component after the translation from pixel values.
     *
     * @param colorSpace       The <CODE>ColorSpace</CODE> associated with this color model.
     * @param bits             The number of significant bits per component.
     * @param hasAlpha         If true, this color model supports alpha.
     * @param isAlphaPremultiplied If true, alpha is premultiplied.
     * @param transparency     Specifies what alpha values can be represented
     *                         by this color model.
     * @param transferType     Specifies the type of primitive array used to
     *                         represent pixel values.
     *
     * @throws IllegalArgumentException If the length of the length of the <CODE>bits</CODE> 
     * array does not match the number of components.
     *
     * @see ColorSpace
     * @see java.awt.Transparency
     */
    public ComponentColorModel (ColorSpace colorSpace,
                                int[] bits,
                                boolean hasAlpha,
                                boolean isAlphaPremultiplied,
                                int transparency,
                                int transferType) {
        super (DataBuffer.getDataTypeSize(transferType)*bits.length,
               bits, colorSpace, hasAlpha, isAlphaPremultiplied, transparency,
               transferType);
    }

    /**
     * Returns the red color component for the specified pixel, scaled
     * from 0 to 255 in the default RGB ColorSpace, sRGB.  A color conversion
     * is done if necessary.  The pixel value is specified as an int.
     * The returned value will be a non pre-multiplied value. 
     * If the alpha is premultiplied, this method divides
     * it out before returning the value (if the alpha value is 0,
     * the red value will be 0).
     *
     * @param pixel The pixel from which you want to get the red color component.
     *
     * @return The red color component for the specified pixel, as an int.
     *
     * @throws IllegalArgumentException If there is more than
     * one component in this <CODE>ColorModel</CODE>. 
     */
    public int getRed(int pixel) {
        if (numComponents > 1) {
            throw new
                IllegalArgumentException("More than one component per pixel");
        }
        // Since there is only 1 component, there is no alpha

        // Normalize the pixel in order to convert it
        float[] norm = { (float) pixel / ((1<<nBits[0]) - 1) };
        float[] rgb = colorSpace.toRGB(norm);

        return (int) rgb[0]*255;
    }

    /**
     * Returns the green color component for the specified pixel, scaled
     * from 0 to 255 in the default RGB ColorSpace, sRGB.  A color conversion
     * is done if necessary.  The pixel value is specified as an int.
     * The returned value will be a non
     * pre-multiplied value. If the alpha is premultiplied, this method
     * divides it out before returning the value (if the alpha value is 0,
     * the green value will be 0).
     *
     * @param pixel The pixel from which you want to get the green color component.
     *
     * @return The green color component for the specified pixel, as an int.
     *
     * @throws IllegalArgumentException If there is more than
     * one component in this <CODE>ColorModel</CODE>. 
     */
    public int getGreen(int pixel) {
        if (numComponents > 1) {
            throw new
                IllegalArgumentException("More than one component per pixel");
        }
        // Since there is only 1 component, there is no alpha

        // Normalize the pixel in order to convert it
        float[] norm = { (float) pixel / ((1<<nBits[0]) - 1) };
        float[] rgb = colorSpace.toRGB(norm);
        
        return (int) rgb[1]*255;
    }

    /**
     * Returns the blue color component for the specified pixel, scaled
     * from 0 to 255 in the default RGB ColorSpace, sRGB.  A color conversion
     * is done if necessary.  The pixel value is specified as an int.
     * The returned value will be a non
     * pre-multiplied value. If the alpha is premultiplied, this method
     * divides it out before returning the value (if the alpha value is 0,
     * the blue value will be 0).
     *
     * @param pixel The pixel from which you want to get the blue color component.
     *
     * @return The blue color component for the specified pixel, as an int.
     *
     * @throws IllegalArgumentException If there is more than
     * one component in this <CODE>ColorModel</CODE>. 
     */
    public int getBlue(int pixel) {
        if (numComponents > 1) {
            throw new
                IllegalArgumentException("More than one component per pixel");
        }
        // Since there is only 1 component, there is no alpha

        // Normalize the pixel in order to convert it
        float[] norm = { (float) pixel / ((1<<nBits[0]) - 1) };
        float[] rgb = colorSpace.toRGB(norm);
        
        return (int) rgb[2]*255;
    }

    /**
     * Returns the alpha component for the specified pixel, scaled
     * from 0 to 255.   The pixel value is specified as an int.
     *
     * @param pixel The pixel from which you want to get the alpha component.
     *
     * @return The alpha component for the specified pixel, as an int.
     *
     * @throws IllegalArgumentException If there is more than
     * one component in this <CODE>ColorModel</CODE>. 
     */
    public int getAlpha(int pixel) {
        if (supportsAlpha == false) {
            return 255;
        }
        if (numComponents > 1) {
            throw new
                IllegalArgumentException("More than one component per pixel");
        }

        return (pixel/((1<<nBits[0])-1))*255;
    }

    /**
     * Returns the color/alpha components of the pixel in the default
     * RGB color model format.  A color conversion is done if necessary.
     * The returned value will be in a non pre-multiplied format. If
     * the alpha is premultiplied, this method divides it out of the
     * color components (if the alpha value is 0, the color values will be 0).
     *
     * @param pixel The pixel from which you want to get the color/alpha components.
     *
     * @return The color/alpha components for the specified pixel, as an int.
     *
     * @throws IllegalArgumentException If there is more than
     * one component in this <CODE>ColorModel</CODE>. 
     */
    public int getRGB(int pixel) {
        if (numComponents > 1) {
            throw new
                IllegalArgumentException("More than one component per pixel");
        }

	return (getAlpha(pixel) << 24)
	    | (getRed(pixel) << 16)
	    | (getGreen(pixel) << 8)
	    | (getBlue(pixel) << 0);
    }


    /**
     * Returns the red color component for the specified pixel, scaled
     * from 0 to 255 in the default RGB ColorSpace, sRGB.  A color conversion
     * is done if necessary.  The <CODE>pixel</CODE> value is specified by an array
     * of data elements of type <CODE>transferType</CODE> passed in as an object 
     * reference. The returned value will be a non pre-multiplied value. If the
     * alpha is premultiplied, this method divides it out before returning
     * the value (if the alpha value is 0, the red value will be 0).
     *
     * @param inData The pixel from which you want to get the red color component, 
     * specified by an array of data elements of type <CODE>transferType</CODE>.
     *
     * @return The red color component for the specified pixel, as an int.
     *
     * @throws ClassCastException If <CODE>inData</CODE> is not a primitive array 
     * of type <CODE>transferType</CODE>. 
     * @throws ArrayIndexOutOfBoundsException if <CODE>inData</CODE> is not 
     * large enough to hold a pixel value for this
     * <CODE>ColorModel</CODE>.
     * @throws UnsupportedOperationException If the transfer type of 
     * this <CODE>ComponentColorModel</CODE> is not one of the supported transfer 
     * types:   <CODE>DataBuffer.TYPE_BYTE</CODE>, <CODE>DataBuffer.TYPE_USHORT</CODE>, 
     * or <CODE>DataBuffer.TYPE_INT</CODE>.     
     */
    public int getRed(Object inData) {
        // REMIND: Should this be CS_sRGB or TYPE_RGB?
        if (colorSpaceType == ColorSpace.TYPE_RGB) {
            boolean needAlpha = (supportsAlpha && isAlphaPremultiplied);
            int alp = 0;
            int red = 0;
            switch (transferType) {
                case DataBuffer.TYPE_BYTE:
                   byte bdata[] = (byte[])inData;
                   red = bdata[0] & 0xff;
                   if (needAlpha) {
                       alp = bdata[numColorComponents] & 0xff;
                   }
                break;
                case DataBuffer.TYPE_USHORT:
                   short sdata[] = (short[])inData;
                   red = sdata[0]&0xffff;
                   if (needAlpha) {
                       alp = sdata[numColorComponents] & 0xffff;
                   }
                break;
                case DataBuffer.TYPE_INT:
                   int idata[] = (int[])inData;
                   red = idata[0];
                   if (needAlpha) {
                       alp = idata[numColorComponents];
                   }
                break;
                default:
                   throw new
                       UnsupportedOperationException("This method has not "+
                       "been implemented for transferType " + transferType);
            }
            if (nBits[0] != 8) {
                int shift = nBits[0] - 8;
                red = ((shift > 0) 
                       ? (red>>shift)
                       : (red<<(-shift)));
            }
            if (needAlpha) {
                return (int) (red*((1<<nBits[numColorComponents])-1.f)/alp);
            }
            else {
                return red;
            }
        }
        else if (colorSpaceType == ColorSpace.TYPE_GRAY) {
            return getGray(inData);
        }

        // Not TYPE_GRAY or TYPE_RGB ColorSpace
        int pixel[];
        if (inData instanceof int[]) {
            pixel = (int[])inData;
        } else {
            pixel = DataBuffer.toIntArray(inData);
            if (pixel == null) {
               throw new UnsupportedOperationException("This method has not been "+
                   "implemented for transferType " + transferType);
            }
        }

        // Normalize the pixel in order to convert it
        float[] norm = getNormalizedComponents(pixel, 0, null, 0);
        float[] rgb = colorSpace.toRGB(norm);
        int color;
        
        if (isAlphaPremultiplied && supportsAlpha) {
            color = (int) (rgb[0]*255*norm[numColorComponents]);
        }
        else {
            color = (int) rgb[0]*255;
        }
        
        return color;
    }
   

    /**
     * Returns the green color component for the specified pixel, scaled
     * from 0 to 255 in the default RGB <CODE>ColorSpace</CODE>, sRGB.  
     * A color conversion is done if necessary.  The <CODE>pixel</CODE> value 
     * is specified by an array of data elements of type <CODE>transferType</CODE> 
     * passed in as an object reference. The returned value is a non pre-multiplied 
     * value. If the alpha is premultiplied, this method divides it out before 
     * returning the value (if the alpha value is 0, the green value will be 0).
     *
     * @param inData The pixel from which you want to get the green color component, 
     * specified by an array of data elements of type <CODE>transferType</CODE>.
     *
     * @return The green color component for the specified pixel, as an int.
	 *
     * @throws ClassCastException If <CODE>inData</CODE> is not a primitive array 
     * of type <CODE>transferType</CODE>. 
     * @throws ArrayIndexOutOfBoundsException if <CODE>inData</CODE> is not 
     * large enough to hold a pixel value for this
     * <CODE>ColorModel</CODE>.
     * @throws UnsupportedOperationException If the transfer type of 
     * this <CODE>ComponentColorModel</CODE>
     * is not one of the supported transfer types:  
     * <CODE>DataBuffer.TYPE_BYTE</CODE>, <CODE>DataBuffer.TYPE_USHORT</CODE>, 
     * or <CODE>DataBuffer.TYPE_INT</CODE>.
     */
    public int getGreen(Object inData) {
        // REMIND: Should this be CS_sRGB or TYPE_RGB?
        if (colorSpaceType == ColorSpace.TYPE_RGB) {
            boolean needAlpha = (supportsAlpha && isAlphaPremultiplied);
            int alp = 0;
            int green = 0;
            switch (transferType) {
                case DataBuffer.TYPE_BYTE:
                   byte bdata[] = (byte[])inData;
                   green = bdata[1] & 0xff;
                   if (needAlpha) {
                       alp = bdata[numColorComponents] & 0xff;
                   }
                break;
                case DataBuffer.TYPE_USHORT:
                   short sdata[] = (short[])inData;
                   green = sdata[1] & 0xffff;
                   if (needAlpha) {
                       alp = sdata[numColorComponents] & 0xffff;
                   }
                break;
                case DataBuffer.TYPE_INT:
                   int idata[] = (int[])inData;
                   green = idata[1];
                   if (needAlpha) {
                       alp = idata[numColorComponents];
                   }
                break;
                default:
                   throw new
                       UnsupportedOperationException("This method has not "+
                          "been implemented for transferType " + transferType);
            }
            if (nBits[1] != 8) {
                int shift = nBits[1] - 8;
                green = ((shift > 0) 
                        ? (green>>shift)
                        : (green<<(-shift)));

            }
            if (needAlpha) {
                return (int) (green*((1<<nBits[numColorComponents])-1.f)/alp);
            }
            else {
                return green;
            }
        }
        else if (colorSpaceType == ColorSpace.TYPE_GRAY) {
            return getGray(inData);
        }

        int pixel[];
        if (inData instanceof int[]) {
            pixel = (int[])inData;
        } else {
            pixel = DataBuffer.toIntArray(inData);
            if (pixel == null) {
               throw new UnsupportedOperationException("This method has not been "+
                   "implemented for transferType " + transferType);
            }
        }

        // Normalize the pixel in order to convert it
        float[] norm = getNormalizedComponents(pixel, 0, null, 0);
        float[] rgb = colorSpace.toRGB(norm);
        int color;
        
        if (isAlphaPremultiplied && supportsAlpha) {
            color = (int) (rgb[1]*255*norm[numColorComponents]);
        }
        else {
            color = (int) rgb[1]*255;
        }
        
        return color;
    }
   

    /**
     * Returns the blue color component for the specified pixel, scaled
     * from 0 to 255 in the default RGB <CODE>ColorSpace</CODE>, sRGB.  
     * A color conversion is done if necessary.  The <CODE>pixel</CODE> value is 
     * specified by an array of data elements of type <CODE>transferType</CODE> 
     * passed in as an object reference. The returned value is a non pre-multiplied 
     * value. If the alpha is premultiplied, this method divides it out before
     *  returning the value (if the alpha value is 0, the blue value will be 0).
     *
     * @param inData The pixel from which you want to get the blue color component, 
     * specified by an array of data elements of type <CODE>transferType</CODE>.
     *
     * @return The blue color component for the specified pixel, as an int.
	 *
     * @throws ClassCastException If <CODE>inData</CODE> is not a primitive array 
     * of type <CODE>transferType</CODE>. 
     * @throws ArrayIndexOutOfBoundsException if <CODE>inData</CODE> is not 
     * large enough to hold a pixel value for this
     * <CODE>ColorModel</CODE>.
     * @throws UnsupportedOperationException If the transfer type of 
     * this <CODE>ComponentColorModel</CODE>
     * is not one of the supported transfer types:  
     * <CODE>DataBuffer.TYPE_BYTE</CODE>, <CODE>DataBuffer.TYPE_USHORT</CODE>, 
     * or <CODE>DataBuffer.TYPE_INT</CODE>.
     */
    public int getBlue(Object inData) {
        // REMIND: Should this be CS_sRGB or TYPE_RGB?
        if (colorSpaceType == ColorSpace.TYPE_RGB) {
            boolean needAlpha = (supportsAlpha && isAlphaPremultiplied);
            int alp = 0;
            int blue = 0;
            switch (transferType) {
                case DataBuffer.TYPE_BYTE:
                   byte bdata[] = (byte[])inData;
                   blue = bdata[2] & 0xff;
                   if (needAlpha) {
                       alp = bdata[numColorComponents] & 0xff;
                   }
                break;
                case DataBuffer.TYPE_USHORT:
                   short sdata[] = (short[])inData;
                   blue = sdata[2] & 0xffff;
                   if (needAlpha) {
                       alp = sdata[numColorComponents] & 0xffff;
                   }
                break;
                case DataBuffer.TYPE_INT:
                   int idata[] = (int[])inData;
                   blue = idata[2];
                   if (needAlpha) {
                       alp = idata[numColorComponents];
                   }
                break;
                default:
                   throw new
                       UnsupportedOperationException("This method has not "+
                       "been implemented for transferType " + transferType);
            }
            if (nBits[2] != 8) {
                int shift = nBits[2] - 8;
                return ((shift > 0) 
                        ? (blue>>shift)
                        : (blue<<(-shift)));
            }
            if (needAlpha) {
                return (int) (blue*((1<<nBits[numColorComponents])-1.f)/alp);
            }
            else {
                return blue;
            }
        }
        else if (colorSpaceType == ColorSpace.TYPE_GRAY) {
            return getGray(inData);
        }

        int pixel[];
        if (inData instanceof int[]) {
            pixel = (int[])inData;
        } else {
            pixel = DataBuffer.toIntArray(inData);
            if (pixel == null) {
               throw new UnsupportedOperationException("This method has not been "+
                   "implemented for transferType " + transferType);
            }
        }

        // Normalize the pixel in order to convert it
        float[] norm = getNormalizedComponents(pixel, 0, null, 0);
        float[] rgb = colorSpace.toRGB(norm);
        int color;
        
        if (isAlphaPremultiplied && supportsAlpha) {
            color = (int) (rgb[2]*255*norm[numColorComponents]);
        }
        else {
            color = (int) rgb[2]*255;
        }
        
        return color;
    }

    /**
     * Returns the alpha component for the specified pixel, scaled from
     * 0 to 255.  The pixel value is specified by an array of data
     * elements of type <CODE>transferType</CODE> passed in as an 
     * object reference.
     *
     * @param inData The pixel from which you want to get the alpha component, 
     * specified by an array of data elements of type <CODE>transferType</CODE>.
     *
     * @return The alpha component for the specified pixel, as an int.
	 *
     * @throws ClassCastException If <CODE>inData</CODE> is not a primitive array 
     * of type <CODE>transferType</CODE>. 
     * @throws ArrayIndexOutOfBoundsException if <CODE>inData</CODE> is not 
     * large enough to hold a pixel value for this
     * <CODE>ColorModel</CODE>.
     * @throws UnsupportedOperationException If the transfer type of 
     * this <CODE>ComponentColorModel</CODE>
     * is not one of the supported transfer types:  
     * <CODE>DataBuffer.TYPE_BYTE</CODE>, <CODE>DataBuffer.TYPE_USHORT</CODE>, 
     * or <CODE>DataBuffer.TYPE_INT</CODE>.
     */
    public int getAlpha(Object inData) {
        if (supportsAlpha == false) {
            return 255;
        }

        int alpha = 0;
        int aIdx = numColorComponents;
        switch (transferType) {
            case DataBuffer.TYPE_BYTE:
               byte bdata[] = (byte[])inData;
               alpha = bdata[aIdx] & 0xff;
            break;
            case DataBuffer.TYPE_USHORT:
               short sdata[] = (short[])inData;
               alpha = sdata[aIdx]&0xffff;
            break;
            case DataBuffer.TYPE_INT:
               int idata[] = (int[])inData;
               alpha = idata[aIdx];
            break;
            default:
               throw new
                   UnsupportedOperationException("This method has not "+
                   "been implemented for transferType " + transferType);
        }

        if (nBits[aIdx] == 8) {
            return alpha;
        }
        else {
            int shift = nBits[aIdx] - 8;
            return ((shift > 0) 
                    ? (alpha>>shift)
                    : (alpha<<(-shift)));
        }
    }

    // This method returns a gray color that can be mapped to
    // RGB values in getRGB().  It assumes that the colorspace is TYPE_GRAY.
    private int getGray(Object inData) {
        boolean needAlpha = (supportsAlpha && isAlphaPremultiplied);
        int alp = 0;
        int gray;
        switch (transferType) {
        case DataBuffer.TYPE_BYTE:
            byte bdata[] = (byte[])inData;
            gray = bdata[0] & 0xff;
            if (needAlpha) {
                alp = bdata[1]&0xff;
            }
            break;
        case DataBuffer.TYPE_USHORT:
            short sdata[] = (short[])inData;
            gray = sdata[0] & 0xffff;
            if (needAlpha) {
                alp = sdata[1]&0xff;
            }
            break;
        case DataBuffer.TYPE_INT:
            int idata[] = (int[])inData;
            gray = idata[0];
            if (needAlpha) {
                alp = idata[1]&0xff;
            }
            break;
        default:
            throw new UnsupportedOperationException("This method has not been"+
                                        " implemented for transferType " +
                                                    transferType);
        }

        if (nBits[0] != 8) {
            int shift = nBits[0] - 8;
            gray = ((shift > 0)
                    ? (gray>>shift)
                    : (gray<<(-shift)));
        }
        return (!needAlpha
                ? gray
                : (int)(gray*(1<<nBits[numColorComponents])-1.f)/alp
                   );

    }
    
    private float getNormAlpha(int pixel[]) {
        return (float) (pixel[numColorComponents] /
                        ((1<<nBits[numColorComponents])-1.f));
    }
    
    /**
     * Returns the color/alpha components for the specified pixel in the
     * default RGB color model format.  A color conversion is done if
     * necessary.  The pixel value is specified by an 
     * array of data elements of type <CODE>transferType</CODE> passed 
     * in as an object reference.
     * The returned value is in a non pre-multiplied format. If
     * the alpha is premultiplied, this method divides it out of the
     * color components (if the alpha value is 0, the color values will be 0).
     *
     * @param inData The pixel from which you want to get the color/alpha components, 
     * specified by an array of data elements of type <CODE>transferType</CODE>.
     *
     * @return The color/alpha components for the specified pixel, as an int.
	 *
     * @throws ClassCastException If <CODE>inData</CODE> is not a primitive array 
     * of type <CODE>transferType</CODE>. 
     * @throws ArrayIndexOutOfBoundsException if <CODE>inData</CODE> is not 
     * large enough to hold a pixel value for this
     * <CODE>ColorModel</CODE>.
     * @throws UnsupportedOperationException If the transfer type of 
     * this <CODE>ComponentColorModel</CODE>
     * is not one of the supported transfer types:  
     * <CODE>DataBuffer.TYPE_BYTE</CODE>, <CODE>DataBuffer.TYPE_USHORT</CODE>, 
     * or <CODE>DataBuffer.TYPE_INT</CODE>.
     * @see ColorModel#getRGBdefault
     */
    public int getRGB(Object inData) {
        if (colorSpaceType == ColorSpace.TYPE_GRAY) {
            int gray = getGray(inData);
            return (getAlpha(inData) << 24)
                | (gray << 16)
                | (gray <<  8)
                | gray;
        }
        return (getAlpha(inData) << 24)
            | (getRed(inData) << 16)
            | (getGreen(inData) << 8)
            | (getBlue(inData));
    }

    /**
     * Returns a data element array representation of a pixel in this
     * <CODE>ColorModel</CODE>, given an integer pixel representation 
     * in the default RGB color model.
     * This array can then be passed to the <CODE>setDataElements</CODE> 
     * method of a <CODE>WritableRaster</CODE> object.  If the <CODE>pixel</CODE> 
     * parameter is null, a new array is allocated.  
     *
     * @param rgb
     * @param pixel The integer representation of the pixel.
     *
     * @throws ClassCastException If <CODE>pixel</CODE> is not null and 
     * is not a primitive array of type <CODE>transferType</CODE>.  
     * @throws ArrayIndexOutOfBoundsException If <CODE>pixel</CODE> is 
     * not large enough to hold a pixel value for this
     * <CODE>ColorModel</CODE>. 
     * @throws UnsupportedOperationException If the transfer type of 
     * this <CODE>ComponentColorModel</CODE>
     * is not one of the supported transfer types:  
     * <CODE>DataBuffer.TYPE_BYTE</CODE>, <CODE>DataBuffer.TYPE_USHORT</CODE>, 
     * or <CODE>DataBuffer.TYPE_INT</CODE>.
     *
     * @see WritableRaster#setDataElements
     * @see SampleModel#setDataElements
     */
    public Object getDataElements(int rgb, Object pixel) {
        //REMIND: maybe more efficient not to use int array for
        //DataBuffer.TYPE_USHORT and DataBuffer.TYPE_INT
        int intpixel[] = null;
        if (transferType == DataBuffer.TYPE_INT &&
            pixel != null) {
           intpixel = (int[])pixel;
        } else {
            intpixel = new int[numComponents];
        }
        // REMIND: Use rendering hints?
        
        if (! is_sRGB) {
            if (colorSpaceType == ColorSpace.TYPE_GRAY) {
                double gray = ((((rgb>>16)&0xff)*.299/255) +
                               (((rgb>>8) &0xff)*.587/255) +
                               (((rgb)    &0xff)*.114/255));
                
                intpixel[0] = (int) (gray * (1 << nBits[0]));

                if (supportsAlpha) {
                    if (nBits[1] == 8) {
                        intpixel[1] = (rgb>>24)&0xff;
                    }
                    else {
                        intpixel[1] =
                            (int)(((rgb>>24)&0xff)/255.f * ((1<<nBits[1])-1));
                    }
                }
            }
            else {
                // Need to convert the color
                float[] norm = new float[3];
                norm[0] = ((rgb>>16)&0xff)/255.f;
                norm[1] = ((rgb>>8)&0xff)/255.f;
                norm[2] = ((rgb>>0)&0xff)/255.f;

                norm = colorSpace.fromRGB(norm);
                for (int i = 0; i < numColorComponents; i++) {
                    intpixel[i] = (int)(norm[i]*((1<<nBits[i]) - 1));
                }
                if (supportsAlpha) {
                    if (nBits[numColorComponents] == 8) {
                        intpixel[numColorComponents] = (rgb>>24)&0xff;
                    }
                    else {
                        intpixel[numColorComponents] =
                            (int)(((rgb>>24)&0xff)/255.f * ((1<<nBits[1])-1));
                    }
                }
            }
        }
        else {
            int alp = (rgb>>24)&0xff;
            int red = (rgb>>16)&0xff;
            int grn = (rgb>>8) &0xff;
            int blu = (rgb)    &0xff;
            if (isAlphaPremultiplied) {
                float norm = alp/255.f;
                if (nBits[0] == 8) {
                    intpixel[0] = (int)(red*norm);
                }
                else {
                    intpixel[0] = (int)((red*norm)/255.f * ((1<<nBits[0])-1));
                }
                if (nBits[1] == 8) {
                    intpixel[1] = (int)(grn*norm);
                }
                else {
                    intpixel[1] = (int)((grn*norm)/255.f * ((1<<nBits[1])-1));
                }
                if (nBits[2] == 8) {
                    intpixel[2] = (int)(blu*norm);
                }
                else {
                    intpixel[2] = (int)((blu*norm)/255.f * ((1<<nBits[2])-1));
                }
                if (supportsAlpha) {
                    if (nBits[3] == 8) {
                        intpixel[3] = alp;
                    }
                    else {
                        intpixel[3] = (int)(norm * ((1<<nBits[3])-1));
                    }
                }
            }
            else {
                if (nBits[0] == 8) {
                    intpixel[0] = red;
                }
                else {
                    intpixel[0] = (int)(red/255.f * ((1<<nBits[0])-1));
                }
                if (nBits[1] == 8) {
                    intpixel[1] = (int)(grn);
                }
                else {
                    intpixel[1] = (int)(grn/255.f * ((1<<nBits[1])-1));
                }
                if (nBits[2] == 8) {
                    intpixel[2] = (int)(blu);
                }
                else {
                    intpixel[2] = (int)(blu/255.f * ((1<<nBits[2])-1));
                }
                if (supportsAlpha) {
                    if (nBits[3] == 8) {
                        intpixel[3] = alp;
                    }
                    else {
                        intpixel[3] = (int)(alp/255.f * ((1<<nBits[3])-1));
                    }
                }
            }
        }
        
        switch (transferType) {
            case DataBuffer.TYPE_BYTE: {
               byte bdata[];
               if (pixel == null) {
                   bdata = new byte[numComponents];
               } else {
                   bdata = (byte[])pixel;
               }
               for (int i = 0; i < numComponents; i++) {
                   bdata[i] = (byte)(0xff&intpixel[i]);
               }
               return bdata;
            }
            case DataBuffer.TYPE_USHORT:{
               short sdata[];
               if (pixel == null) {
                   sdata = new short[numComponents];
               } else {
                   sdata = (short[])pixel;
               }
               for (int i = 0; i < numComponents; i++) {
                   sdata[i] = (short)(intpixel[i]&0xffff);
               }
               return sdata;
            }
            case DataBuffer.TYPE_INT:
               return intpixel;
        }
        throw new IllegalArgumentException("This method has not been "+
                 "implemented for transferType " + transferType);
    }
    
   /** Returns an array of unnormalized color/alpha components given a pixel
     * in this <CODE>ColorModel</CODE>.  Color/alpha components are
     * stored in the <CODE>components</CODE> array starting at <CODE>offset</CODE> 
     * (even if the array is allocated by this method).   
     *
     * @param pixel The pixel value specified as an integer.
     * @param components An integer array in which to store the unnormalized 
     * color/alpha components. If the <CODE>components</CODE> array is null,
     * a new array is allocated.  
     * @param offset An offset into the <CODE>components</CODE> array.
     *
     * @return The components array.  
     *
     * @throws IllegalArgumentException If there is more than one
     * component in this <CODE>ColorModel</CODE>.
     * @throws ArrayIndexOutOfBoundsException If the <CODE>components</CODE> 
     * array is not null and is not large enough to hold all the color and 
     * alpha components (starting at offset).
     */
    public int[] getComponents(int pixel, int[] components, int offset) {
        if (numComponents > 1) {
            throw new
                IllegalArgumentException("More than one component per pixel");
        }
        if (components == null) {
            components = new int[offset+1];
        }

        components[offset+0] = (pixel & ((1<<nBits[0]) - 1));
        return components;
    }
    
    /**
     * Returns an array of unnormalized color/alpha components given a pixel
     * in this <CODE>ColorModel</CODE>.  The pixel value is specified by an 
     * array of data elements of type <CODE>transferType</CODE> passed in as  
     * an object reference. 
     * Color/alpha components are stored in the <CODE>components</CODE> array 
     * starting at  <CODE>offset</CODE> (even if the array is allocated by 
     * this method). 
     *
     * @param pixel A pixel value specified by an array of data elements of
     * type <CODE>transferType</CODE>.
     * @param components An integer array in which to store the unnormalized 
     * color/alpha components. If the <CODE>components</CODE> array is null, 
     * a new array is allocated. 
     * @param offset An offset into the <CODE>components</CODE> array.
     *
     * @return The <CODE>components</CODE> array.
     *
     * @throws UnsupportedOperationException If the transfer type of 
     * this <CODE>ComponentColorModel</CODE>
     * is not one of the supported transfer types:  
     * <CODE>DataBuffer.TYPE_BYTE</CODE>, <CODE>DataBuffer.TYPE_USHORT</CODE>, 
     * or <CODE>DataBuffer.TYPE_INT</CODE>.
     * @throws ClassCastException If <CODE>pixel</CODE> is not a primitive 
     * array of type <CODE>transferType</CODE>.
     * @throws IllegalArgumentException If the <CODE>components</CODE> array is 
     * not null and is not large enough to hold all the color and alpha 
     * components (starting at offset), or if <CODE>pixel</CODE> is not large 
     * enough to hold a pixel value for this ColorModel.
     */
    public int[] getComponents(Object pixel, int[] components, int offset) {
        int intpixel[];
        if (pixel instanceof int[]) {
            intpixel = (int[])pixel;
        } else {
            intpixel = DataBuffer.toIntArray(pixel);
            if (intpixel == null) {
               throw new UnsupportedOperationException("This method has not been "+
                   "implemented for transferType " + transferType);
            }
        }
        if (intpixel.length < numComponents) {
            throw new IllegalArgumentException
                ("Length of pixel array < number of components in model");
        }
        if (components == null) {
            components = new int[offset+numComponents];
        }
        else if ((components.length-offset) < numComponents) {
            throw new IllegalArgumentException
                ("Length of components array < number of components in model");
        }
        System.arraycopy(intpixel, 0, components, offset, numComponents);

        return components;
    }
    
    /**
     * Returns a pixel value represented as an int in this <CODE>ColorModel</CODE>,
     * given an array of unnormalized color/alpha components.  
     * 
     * @param components An array of unnormalized color/alpha components.
     * @param offset An offset into the <CODE>components</CODE> array.
     *
     * @return A pixel value represented as an int.
     *
     * @throws IllegalArgumentException If there is more than one component 
     * in this <CODE>ColorModel</CODE>.
     */
    public int getDataElement(int[] components, int offset) {
        if (numComponents == 1) {
            return components[offset+0];
        }
        throw new IllegalArgumentException("This model returns "+
                                           numComponents+
                                           " elements in the pixel array.");
    }
    
    /**
     * Returns a data element array representation of a pixel in this
     * <CODE>ColorModel</CODE>, given an array of unnormalized color/alpha 
     * components. This array can then be passed to the <CODE>setDataElements</CODE> 
     * method of a <CODE>WritableRaster</CODE> object.
     * 
     * @param components An array of unnormalized color/alpha components.
     * @param offset The integer offset into the <CODE>components</CODE> array.
     * @param obj The object in which to store the data element array 
     * representation of the pixel. If <CODE>obj</CODE> variable is null, 
     * a new array is allocated.  If <CODE>obj</CODE> is not null, it must 
     * be a primitive array of type <CODE>transferType</CODE>. An 
     * <CODE>ArrayIndexOutOfBoundsException</CODE> is thrown if 
     * <CODE>obj</CODE> is not large enough to hold a pixel value 
     * for this <CODE>ColorModel</CODE>.
     *
     * @return The data element array representation of a pixel 
     * in this <CODE>ColorModel</CODE>.
     *
     * @throws IllegalArgumentException If the components array
     * is not large enough to hold all the color and alpha components
     * (starting at offset).  
     * @throws ClassCastException If <CODE>obj</CODE> is not null and is not a 
     * primitive  array of type <CODE>transferType</CODE>.
     * @throws ArrayIndexOutOfBoundsException If <CODE>obj</CODE> is not large
     * enough to hold a pixel value for this <CODE>ColorModel</CODE>.    
     * @throws UnsupportedOperationException If the transfer type of 
     * this <CODE>ComponentColorModel</CODE>
     * is not one of the supported transfer types:  
     * <CODE>DataBuffer.TYPE_BYTE</CODE>, <CODE>DataBuffer.TYPE_USHORT</CODE>, 
     * or <CODE>DataBuffer.TYPE_INT</CODE>.
     *    
     * @see WritableRaster#setDataElements
     * @see SampleModel#setDataElements
     */ 
    public Object getDataElements(int[] components, int offset, Object obj) {
        if ((components.length-offset) < numComponents) {
            throw new IllegalArgumentException("Component array too small"+
                                               " (should be "+numComponents);
        }
        switch(transferType) {
        case DataBuffer.TYPE_INT:
            {
                int[] pixel;
                if (obj == null) {
                    pixel = new int[components.length];
                }
                else {
                    pixel = (int[]) obj;
                }
                System.arraycopy(components, offset, pixel, 0,
                                 numComponents);
                return pixel;
            }
        
        case DataBuffer.TYPE_BYTE:
            {
                byte[] pixel;
                if (obj == null) {
                    pixel = new byte[components.length];
                }
                else {
                    pixel = (byte[]) obj;
                }
                for (int i=0; i < numComponents; i++) {
                    pixel[i] = (byte) (components[offset+i]&0xff);
                }
                return pixel;
            }
        
        case DataBuffer.TYPE_USHORT:
            {
                short[] pixel;
                if (obj == null) {
                    pixel = new short[components.length];
                }
                else {
                    pixel = (short[]) obj;
                }
                for (int i=0; i < numComponents; i++) {
                    pixel[i] = (short) (components[offset+i]&0xffff);
                }
                return pixel;
            }
        
        default:
            throw new UnsupportedOperationException("This method has not been "+
                                        "implemented for transferType " +
                                        transferType);
        }
    }

    /**
     * Forces the raster data to match the state specified in the
     * <CODE>isAlphaPremultiplied</CODE> variable, assuming the data 
     * is currently correctly described by this <CODE>ColorModel</CODE>.  
     * It may multiply or divide the color raster data by alpha, or 
     * do nothing if the data is in the correct state.  If the data needs 
     * to be coerced, this method also returns an instance of 
     * this <CODE>ColorModel</CODE> with
     * the <CODE>isAlphaPremultiplied</CODE> flag set appropriately.
     *
     * @throws UnsupportedOperationException If the transfer type of 
     * this <CODE>ComponentColorModel</CODE>
     * is not one of the supported transfer types:  
     * <CODE>DataBuffer.TYPE_BYTE</CODE>, <CODE>DataBuffer.TYPE_USHORT</CODE>, 
     * or <CODE>DataBuffer.TYPE_INT</CODE>.
     *
     * @throws UnsupportedOperationException If the transfer type of 
     * this <CODE>ComponentColorModel</CODE>
     * is not one of the supported transfer types:  
     * <CODE>DataBuffer.TYPE_BYTE</CODE>, <CODE>DataBuffer.TYPE_USHORT</CODE>, 
     * or <CODE>DataBuffer.TYPE_INT</CODE>.
     */
    public ColorModel coerceData (WritableRaster raster, 
                                  boolean isAlphaPremultiplied) {
        if ((supportsAlpha == false) ||
            (this.isAlphaPremultiplied == isAlphaPremultiplied))
        {
            // Nothing to do
            return this;
        }
        
        int w = raster.getWidth();
        int h = raster.getHeight();
        int aIdx = raster.getNumBands() - 1;
        int alpha;
        int rminX = raster.getMinX();
        int rY = raster.getMinY();
        int rX;
        if (isAlphaPremultiplied) {
            switch (transferType) {
                case DataBuffer.TYPE_BYTE: {
                    byte pixel[] = null;
                    for (int y = 0; y < h; y++, rY++) {
                        rX = rminX;
                        for (int x = 0; x < w; x++, rX++) {
                            pixel = (byte[])raster.getDataElements(rX, rY,
                                                                   pixel);
                            alpha = pixel[aIdx] & 0xff;
                            if (alpha != 0) {
                                for (int c=0; c < aIdx; c++) {
                                    pixel[c] = (byte)((pixel[c]&0xff) * alpha);
                                }
                                raster.setDataElements(rX, rY, pixel);
                            }
                        }
                    }
                }
                break;
                case DataBuffer.TYPE_USHORT: {
                    short pixel[] = null;
                    for (int y = 0; y < h; y++, rY++) {
                        rX = rminX;
                        for (int x = 0; x < w; x++, rX++) {
                            pixel = (short[])raster.getDataElements(rX, rY,
                                                                    pixel);
                            alpha = pixel[aIdx] &0xffff;
                            if (alpha != 0) {
                                for (int c=0; c < aIdx; c++) {
                                    pixel[c] = (short)
                                        ((pixel[c]&0xffff) * alpha);
                                }
                                raster.setDataElements(rX, rY, pixel);
                            }
                        }
                    }
                }
                break;
                case DataBuffer.TYPE_INT: {
                    int pixel[] = null;
                    for (int y = 0; y < h; y++, rY++) {
                        rX = rminX;
                        for (int x = 0; x < w; x++, rX++) {
                            pixel = (int[])raster.getDataElements(rX, rY,
                                                                  pixel);
                            alpha = pixel[aIdx];
                            if (alpha != 0) {
                                for (int c=0; c < aIdx; c++) {
                                    pixel[c] *= alpha;
                                }
                                raster.setDataElements(rX, rY, pixel);
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
                            byte pixel[] = null;
                            pixel = (byte[])raster.getDataElements(rX, rY, pixel);
                            alpha = pixel[aIdx] & 0xff;
                            for (int c=0; c < aIdx; c++) {
                                if (alpha != 0) {
                                    pixel[c] = (byte)
                                        ((pixel[c]&0xff) / alpha);
                                }
                            }
                            raster.setDataElements(rX, rY, pixel);
                        }
                    }
                }
                break;
                case DataBuffer.TYPE_USHORT: {
                    for (int y = 0; y < h; y++, rY++) {
                        rX = rminX;
                        for (int x = 0; x < w; x++, rX++) {
                            short pixel[] = null;
                            pixel = (short[])raster.getDataElements(rX, rY,
                                                                 pixel);
                            alpha = pixel[aIdx] & 0xffff;
                            for (int c=0; c < aIdx; c++) {
                                if (alpha != 0) {
                                    pixel[c] = (short)
                                        ((pixel[c]&0xffff) / alpha);
                                }
                            }
                            raster.setDataElements(rX, rY, pixel);
                        }
                    }
                }
                break;
                case DataBuffer.TYPE_INT: {
                    for (int y = 0; y < h; y++, rY++) {
                        rX = rminX;
                        for (int x = 0; x < w; x++, rX++) {
                            int pixel[] = null;
                            pixel = (int[])raster.getDataElements(rX, rY,
                                                                  pixel);
                            alpha = pixel[aIdx];
                            for (int c=0; c < aIdx; c++) {
                                if (alpha != 0) {
                                    pixel[c] /= alpha;
                                }
                            }
                            raster.setDataElements(rX, rY, pixel);
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
        return new ComponentColorModel(colorSpace, nBits, supportsAlpha,
                                       isAlphaPremultiplied, transparency,
                                       transferType);

    }

    /**
      * Returns true if <CODE>raster</CODE> is compatible with this 
      * <CODE>ColorModel</CODE>; false if it is not.
      *
      * @param raster The <CODE>Raster</CODE> object to test for compatibility.
      *
      * @return <CODE>true</CODE> if <CODE>raster</CODE> is compatible with this 
      * <CODE>ColorModel</CODE>, <CODE>false</CODE> if it is not.
      */
    public boolean isCompatibleRaster(Raster raster) {

	boolean flag = true;
        SampleModel sm = raster.getSampleModel();

        if (sm instanceof ComponentSampleModel) {
            if (sm.getNumBands() != getNumComponents()) {
                return false;
            }
            for (int i=0; i<nBits.length; i++) {
                if (sm.getSampleSize(i) < nBits[i])
                    flag = false;
            }
        }
        else {
            return false;
        }
        return ( (raster.getTransferType() == transferType) && flag);
    }
    
    /**
     * Creates a <CODE>WritableRaster</CODE> with the specified width and height, 
     * that  has a data layout (<CODE>SampleModel</CODE>) compatible with 
     * this <CODE>ColorModel</CODE>.  
     *
     * @param w The width of the <CODE>WritableRaster</CODE> you want to create.
     * @param h The height of the <CODE>WritableRaster</CODE> you want to create.
     *
     * @return A <CODE>WritableRaster</CODE> that is compatible with 
     * this <CODE>ColorModel</CODE>.
     * @see WritableRaster
     * @see SampleModel
     */
    public WritableRaster createCompatibleWritableRaster (int w, int h) {
        int dataSize = w*h*numComponents;
        WritableRaster raster = null;

        // Create the raster
        raster = Raster.createInterleavedRaster(transferType,
                                                w, h,
                                                numComponents, null);

        return raster;
    }

    /**
     * Creates a <CODE>SampleModel</CODE> with the specified width and height, 
     * that  has a data layout compatible with this <CODE>ColorModel</CODE>.  
     *
     * @param w The width of the <CODE>SampleModel</CODE> you want to create.
     * @param h The height of the <CODE>SampleModel</CODE> you want to create.
     *
     * @return A <CODE>SampleModel</CODE> that is compatible with this
     * <CODE>ColorModel</CODE>.
	 *
     * @see SampleModel	 
     */
    public SampleModel createCompatibleSampleModel(int w, int h) {
        int[] bandOffsets = new int[numComponents];
        for (int i=0; i < numComponents; i++) {
            bandOffsets[i] = 0;
        }
        return new PixelInterleavedSampleModel(transferType, w, h,
                                               numComponents, w*numComponents,
                                               bandOffsets);
    }
    
    /** 
     * Checks whether or not the specified <CODE>SampleModel</CODE> 
     * is compatible with this <CODE>ColorModel</CODE>.
     *
     * @param sm The <CODE>SampleModel</CODE> to test for compatibility.
     *
     * @return <CODE>true</CODE> if the <CODE>SampleModel</CODE> is 
     * compatible with this <CODE>ColorModel</CODE>, <CODE>false</CODE> 
     * if it is not.
     *
     * @see SampleModel 
     */
    public boolean isCompatibleSampleModel(SampleModel sm) {
        if (sm instanceof ComponentSampleModel) {
            return false;
        }

        if (sm.getTransferType() != transferType) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Returns a <CODE>Raster</CODE> representing the alpha channel of an image,
     * extracted from the input <CODE>Raster</CODE>.
     * This method assumes that <CODE>Raster</CODE> objects associated with 
     * this <CODE>ColorModel</CODE> store the alpha band, if present, as 
     * the last band of image data. Returns null if there is no separate spatial 
     * alpha channel associated with this <CODE>ColorModel</CODE>.
     * This method creates a new <CODE>Raster</CODE>, but will share the data
     * array.
     *
     * @param raster The <CODE>WritableRaster</CODE> from which to extract the 
     * alpha  channel.
     *
     * @return A <CODE>WritableRaster</CODE> containing the image's alpha channel.
     *
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
     * Compares this color model with another for equality.  
     *
     * @param obj The object to compare with this color model.
     * @return <CODE>true</CODE> if the color model objects are equal, 
     * <CODE>false</CODE> if they are not.
     */
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        if (obj.getClass() !=  getClass()) {
            return false;
        }
        
        return true;
    }

}

