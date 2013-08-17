/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/**********************************************************************
 **********************************************************************
 **********************************************************************
 *** COPYRIGHT (c) Eastman Kodak Company, 1997                      ***
 *** As  an unpublished  work pursuant to Title 17 of the United    ***
 *** States Code.  All rights reserved.                             ***
 **********************************************************************
 **********************************************************************
 **********************************************************************/

package java.awt.color;

import sun.awt.color.ICC_Transform;


/**
 *  
 * An implementation of the abstract ColorSpace class.  This representation of
 * device independent and device dependent color spaces is based on the
 * International Color Consortium Specification ICC.1:1998-09, File Format for
 * Color Profiles, September 1998, and the addendum ICC.1A:1999-04, April 1999,
 * to that specification (see <A href="http://www.color.org">
 * http://www.color.org</A>).
 * <p>
 * Typically, a Color or ColorModel would be associated with an ICC
 * Profile which is either an input, display, or output profile (see
 * the ICC specification).  There are other types of ICC Profiles, e.g.
 * abstract profiles, device link profiles, and named color profiles,
 * which do not contain information appropriate for representing the color
 * space of a color, image, or device (see ICC_Profile).
 * Attempting to create an ICC_ColorSpace object from an inappropriate ICC
 * Profile is an error.
 * <p> 
 * ICC Profiles represent transformations from the color space of
 * the profile (e.g. a monitor) to a Profile Connection Space (PCS).
 * Profiles of interest for tagging images or colors have a
 * PCS which is one of the device independent
 * spaces (one CIEXYZ space and two CIELab spaces) defined in the
 * ICC Profile Format Specification.  Most profiles of interest
 * either have invertible transformations or explicitly specify
 * transformations going both directions.  Should an ICC_ColorSpace
 * object be used in a way requiring a conversion from PCS to
 * the profile's native space and there is inadequate data to
 * correctly perform the conversion, the ICC_ColorSpace object will
 * produce output in the specified type of color space (e.g. TYPE_RGB,
 * TYPE_CMYK, etc.), but the specific color values of the output data
 * will be undefined.
 * <p>
 * The details of this class are not important for simple applets,
 * which draw in a default color space or manipulate and display
 * imported images with a known color space.  At most, such applets
 * would need to get one of the default color spaces via
 * ColorSpace.getInstance().
 * <p>
 * @see ColorSpace
 * @see ICC_Profile
 */



public class ICC_ColorSpace extends ColorSpace {

    private ICC_Profile    thisProfile;

    // {to,from}{RGB,CIEXYZ} methods create and cache these when needed
    private transient ICC_Transform this2srgb;
    private transient ICC_Transform srgb2this;
    private transient ICC_Transform this2xyz;
    private transient ICC_Transform xyz2this;
  

    /**
    * Constructs a new ICC_ColorSpace from an ICC_Profile object.
    * @exception IllegalArgumentException if profile is inappropriate for
    *            representing a ColorSpace.
    */
    public ICC_ColorSpace (ICC_Profile profile) {
        super (profile.getColorSpaceType(), profile.getNumComponents());

        int profileClass = profile.getProfileClass();

        /* REMIND - is NAMEDCOLOR OK? */
        if ((profileClass != ICC_Profile.CLASS_INPUT) &&
            (profileClass != ICC_Profile.CLASS_DISPLAY) &&
            (profileClass != ICC_Profile.CLASS_OUTPUT) &&
            (profileClass != ICC_Profile.CLASS_COLORSPACECONVERSION) &&
            (profileClass != ICC_Profile.CLASS_NAMEDCOLOR) ) {
            throw new IllegalArgumentException("Invalid profile type");
        }

        thisProfile = profile;
    }

    /**
    * Returns the ICC_Profile for this ICC_ColorSpace.
    */
    public ICC_Profile getProfile() {
        return thisProfile;
    }

    /**
     * Transforms a color value assumed to be in this ColorSpace
     * into a value in the default CS_sRGB color space.
     * <p>
     * This method transforms color values using algorithms designed
     * to produce the best perceptual match between input and output
     * colors.  In order to do colorimetric conversion of color values,
     * you should use the <code>toCIEXYZ</code>
     * method of this color space to first convert from the input
     * color space to the CS_CIEXYZ color space, and then use the
     * <code>fromCIEXYZ</code> method of the CS_sRGB color space to
     * convert from CS_CIEXYZ to the output color space.
     * See {@link #toCIEXYZ(float[]) toCIEXYZ} and
     * {@link #fromCIEXYZ(float[]) fromCIEXYZ} for further information.
     * <p>
     * @param colorvalue a float array with length of at least the number
           of components in this ColorSpace.
     * @return a float array of length 3.
     */
    public float[]    toRGB (float[] colorvalue) {
    ICC_Transform[]    transformList;
    float[]        result;
    ICC_ColorSpace    srgbCS;
    
        if (this2srgb == null) {
            transformList = new ICC_Transform [2];
            
            srgbCS = (ICC_ColorSpace) ColorSpace.getInstance (CS_sRGB);
            
            transformList[0] = new ICC_Transform (
                thisProfile, ICC_Transform.Any, ICC_Transform.In);
            transformList[1] = new ICC_Transform (
                srgbCS.getProfile(), ICC_Transform.Any, ICC_Transform.Out);

            this2srgb = new ICC_Transform (transformList);
        }

        float[] tmp;
        if (ColorSpace.isCS_CIEXYZ(this)) {
            // Fix for 4267139.  We do a specific fix here for the fact that the
            // ICC encoding for CIEXYZ maps to a range of 0.0 to 2.0, rather than
            // 0.0 to 1.0 to allow for X and Z values greater than 1.0 (Y values
            // should never be greater than 1.0).  What this means is that the
            // Y values passed to xyz2this.colorConvert (1, tmp, result)
            // should range from 0.0 to 0.5, so they need to be halved here.
            // REMIND: The only encoding we can know about right now that
            // requires this fix (given our interfaces to the underlying CMM)
            // is CIEXYZ, but this probably needs a more general long-term fix.
            tmp = new float[3];
            float ALMOST_ONE_HALF = 1.0f / (1.0f + (32767.0f / 32768.0f));
            tmp[0] = colorvalue[0] * ALMOST_ONE_HALF;
            tmp[1] = colorvalue[1] * ALMOST_ONE_HALF;
            tmp[2] = colorvalue[2] * ALMOST_ONE_HALF;
        } else {
            tmp = colorvalue;
        }

        result = new float [3];

        this2srgb.colorConvert (1, tmp, result);
        
        return result;
    }

    /**
     * Transforms a color value assumed to be in the default CS_sRGB
     * color space into this ColorSpace.
     * <p>
     * This method transforms color values using algorithms designed
     * to produce the best perceptual match between input and output
     * colors.  In order to do colorimetric conversion of color values,
     * you should use the <code>toCIEXYZ</code>
     * method of the CS_sRGB color space to first convert from the input
     * color space to the CS_CIEXYZ color space, and then use the
     * <code>fromCIEXYZ</code> method of this color space to
     * convert from CS_CIEXYZ to the output color space.
     * See {@link #toCIEXYZ(float[]) toCIEXYZ} and
     * {@link #fromCIEXYZ(float[]) fromCIEXYZ} for further information.
     * <p>
     * @param rgbvalue a float array with length of at least 3.
     * @return a float array with length equal to the number of
            components in this ColorSpace.
     */
    public float[]    fromRGB(float[] rgbvalue) {
    ICC_Transform[]    transformList;
    float[]        result;
    ICC_ColorSpace    srgbCS;
    
        if (srgb2this == null) {
            transformList = new ICC_Transform [2];
            
            srgbCS = (ICC_ColorSpace) ColorSpace.getInstance (CS_sRGB);
            
            transformList[0] = new ICC_Transform (
                srgbCS.getProfile(), ICC_Transform.Any, ICC_Transform.In);
            transformList[1] = new ICC_Transform (
                thisProfile, ICC_Transform.Any, ICC_Transform.Out);

            srgb2this = new ICC_Transform (transformList);
        }

        result = new float [this.getNumComponents()];

        srgb2this.colorConvert (1, rgbvalue, result);
        
        if (ColorSpace.isCS_CIEXYZ(this)) {
            // Fix for 4267139.  We do a specific fix here for the fact that the
            // ICC encoding for CIEXYZ maps to a range of 0.0 to 2.0, rather than
            // 0.0 to 1.0 to allow for X and Z values greater than 1.0 (Y values
            // should never be greater than 1.0).  What this means is that the
            // result Y values returned from this2xyz.colorConvert(1,colorvalue,
            // result) will range from 0.0 to 0.5, so they need to be doubled
            // here.
            // REMIND:  The only encoding we can know about right now that
            // requires this fix (given our interfaces to the underlying CMM)
            // is CIEXYZ, but this probably needs a more general long-term fix.
            float ALMOST_TWO = 1.0f + (32767.0f / 32768.0f);
            result[0] *= ALMOST_TWO;
            result[1] *= ALMOST_TWO;
            result[2] *= ALMOST_TWO;
        }

        return result;
    }


    /**
     * Transforms a color value assumed to be in this ColorSpace
     * into the CS_CIEXYZ conversion color space.
     * <p>
     * This method transforms color values using relative colorimetry,
     * as defined by the ICC Specification.  This
     * means that the XYZ values returned by this method are represented
     * relative to the D50 white point of the CS_CIEXYZ color space.
     * This representation is useful in a two-step color conversion
     * process in which colors are transformed from an input color
     * space to CS_CIEXYZ and then to an output color space.  This
     * representation is not the same as the XYZ values that would
     * be measured from the given color value by a colorimeter.
     * A further transformation is necessary to compute the XYZ values
     * that would be measured using current CIE recommended practices.
     * The paragraphs below explain this in more detail.
     * <p>
     * The ICC standard uses a device independent color space (DICS) as the
     * mechanism for converting color from one device to another device.  In
     * this architecture, colors are converted from the source device's color
     * space to the ICC DICS and then from the ICC DICS to the destination
     * device's color space.  The ICC standard defines device profiles which
     * contain transforms which will convert between a device's color space
     * and the ICC DICS.  The overall conversion of colors from a source
     * device to colors of a destination device is done by connecting the
     * device-to-DICS transform of the profile for the source device to the
     * DICS-to-device transform of the profile for the destination device.
     * For this reason, the ICC DICS is commonly referred to as the profile
     * connection space (PCS).  The color space used in the methods
     * toCIEXYZ and fromCIEXYZ is the CIEXYZ PCS defined by the ICC
     * Specification.  This is also the color space represented by
     * ColorSpace.CS_CIEXYZ.
     * <p>
     * The XYZ values of a color are often represented as relative to some
     * white point, so the actual meaning of the XYZ values cannot be known
     * without knowing the white point of those values.  This is known as
     * relative colorimetry.  The PCS uses a white point of D50, so the XYZ
     * values of the PCS are relative to D50.  For example, white in the PCS
     * will have the XYZ values of D50, which is defined to be X=.9642,
     * Y=1.000, and Z=0.8249.  This white point is commonly used for graphic
     * arts applications, but others are often used in other applications.
     * <p>
     * To quantify the color characteristics of a device such as a printer
     * or monitor, measurements of XYZ values for particular device colors
     * are typically made.  For purposes of this discussion, the term
     * device XYZ values is used to mean the XYZ values that would be
     * measured from device colors using current CIE recommended practices.
     * <p>
     * Converting between device XYZ values and the PCS XYZ values returned
     * by this method corresponds to converting between the device's color
     * space, as represented by CIE colorimetric values, and the PCS.  There
     * are many factors involved in this process, some of which are quite
     * subtle.  The most important, however, is the adjustment made to account
     * for differences between the device's white point and the white point of
     * the PCS.  There are many techniques for doing this and it is the
     * subject of much current research and controversy.  Some commonly used
     * methods are XYZ scaling, the von Kries transform, and the Bradford
     * transform.  The proper method to use depends upon each particular
     * application.
     * <p>
     * The simplest method is XYZ scaling.  In this method each device XYZ
     * value is  converted to a PCS XYZ value by multiplying it by the ratio
     * of the PCS white point (D50) to the device white point.
     * <pre>
     * 
     * Xd, Yd, Zd are the device XYZ values
     * Xdw, Ydw, Zdw are the device XYZ white point values
     * Xp, Yp, Zp are the PCS XYZ values
     * Xd50, Yd50, Zd50 are the PCS XYZ white point values
     * 
     * Xp = Xd * (Xd50 / Xdw)
     * Yp = Yd * (Yd50 / Ydw)
     * Zp = Zd * (Zd50 / Zdw)
     * 
     * </pre>
     * <p>
     * Conversion from the PCS to the device would be done by inverting these
     * equations:
     * <pre>
     * 
     * Xd = Xp * (Xdw / Xd50)
     * Yd = Yp * (Ydw / Yd50)
     * Zd = Zp * (Zdw / Zd50)
     * 
     * </pre>
     * <p>
     * Note that the media white point tag in an ICC profile is not the same
     * as the device white point.  The media white point tag is expressed in
     * PCS values and is used to represent the difference between the XYZ of
     * device illuminant and the XYZ of the device media when measured under
     * that illuminant.  The device white point is expressed as the device
     * XYZ values corresponding to white displayed on the device.  For
     * example, displaying the RGB color (1.0, 1.0, 1.0) on an sRGB device
     * will result in a measured device XYZ value of D65.  This will not
     * be the same as the media white point tag XYZ value in the ICC
     * profile for an sRGB device.
     * <p>
     * @param colorvalue a float array with length of at least the number
     *        of components in this ColorSpace.
     * @return a float array of length 3.
     */
    public float[]    toCIEXYZ(float[] colorvalue) {
    ICC_Transform[]    transformList;
    float[]        result;
    ICC_ColorSpace    xyzCS;
    
        if (this2xyz == null) {
            transformList = new ICC_Transform [2];
            
            xyzCS = (ICC_ColorSpace) ColorSpace.getInstance (CS_CIEXYZ);
            
            try {
                transformList[0] = new ICC_Transform (thisProfile,
                    ICC_Profile.icRelativeColorimetric, ICC_Transform.In);
            } catch (CMMException e) {
                transformList[0] = new ICC_Transform (thisProfile,
                    ICC_Transform.Any, ICC_Transform.In);
            }
            transformList[1] = new ICC_Transform (xyzCS.getProfile(),
                ICC_Transform.Any, ICC_Transform.Out);

            this2xyz = new ICC_Transform (transformList);
        }

        result = new float [3];

        this2xyz.colorConvert (1, colorvalue, result);

        // Fix for 4267139.  We do a specific fix here for the fact that the
        // ICC encoding for CIEXYZ maps to a range of 0.0 to 2.0, rather than
        // 0.0 to 1.0 to allow for X and Z values greater than 1.0 (Y values
        // should never be greater than 1.0).  What this means is that the
        // result Y values returned from this2xyz.colorConvert (1, colorvalue,
        // result) will range from 0.0 to 0.5, so they need to be doubled here.
        // REMIND:  The only encoding we can know about right now that requires
        // this fix (given our interfaces to the underlying CMM) is CIEXYZ,
        // but this probably needs a more general long-term fix.
        float ALMOST_TWO = 1.0f + (32767.0f / 32768.0f);
        result[0] *= ALMOST_TWO;
        result[1] *= ALMOST_TWO;
        result[2] *= ALMOST_TWO;
        
        return result;
    }


    /**
     * Transforms a color value assumed to be in the CS_CIEXYZ conversion
     * color space into this ColorSpace.
     * <p>
     * This method transforms color values using relative colorimetry,
     * as defined by the ICC Specification.  This
     * means that the XYZ argument values taken by this method are represented
     * relative to the D50 white point of the CS_CIEXYZ color space.
     * This representation is useful in a two-step color conversion
     * process in which colors are transformed from an input color
     * space to CS_CIEXYZ and then to an output color space.  The color
     * values returned by this method are not those that would produce
     * the XYZ value passed to the method when measured by a colorimeter.
     * If you have XYZ values corresponding to measurements made using
     * current CIE recommended practices, they must be converted to D50
     * relative values before being passed to this method.
     * The paragraphs below explain this in more detail.
     * <p>
     * The ICC standard uses a device independent color space (DICS) as the
     * mechanism for converting color from one device to another device.  In
     * this architecture, colors are converted from the source device's color
     * space to the ICC DICS and then from the ICC DICS to the destination
     * device's color space.  The ICC standard defines device profiles which
     * contain transforms which will convert between a device's color space
     * and the ICC DICS.  The overall conversion of colors from a source
     * device to colors of a destination device is done by connecting the
     * device-to-DICS transform of the profile for the source device to the
     * DICS-to-device transform of the profile for the destination device.
     * For this reason, the ICC DICS is commonly referred to as the profile
     * connection space (PCS).  The color space used in the methods
     * toCIEXYZ and fromCIEXYZ is the CIEXYZ PCS defined by the ICC
     * Specification.  This is also the color space represented by
     * ColorSpace.CS_CIEXYZ.
     * <p>
     * The XYZ values of a color are often represented as relative to some
     * white point, so the actual meaning of the XYZ values cannot be known
     * without knowing the white point of those values.  This is known as
     * relative colorimetry.  The PCS uses a white point of D50, so the XYZ
     * values of the PCS are relative to D50.  For example, white in the PCS
     * will have the XYZ values of D50, which is defined to be X=.9642,
     * Y=1.000, and Z=0.8249.  This white point is commonly used for graphic
     * arts applications, but others are often used in other applications.
     * <p>
     * To quantify the color characteristics of a device such as a printer
     * or monitor, measurements of XYZ values for particular device colors
     * are typically made.  For purposes of this discussion, the term
     * device XYZ values is used to mean the XYZ values that would be
     * measured from device colors using current CIE recommended practices.
     * <p>
     * Converting between device XYZ values and the PCS XYZ values taken as
     * arguments by this method corresponds to converting between the device's
     * color space, as represented by CIE colorimetric values, and the PCS.
     * There are many factors involved in this process, some of which are quite
     * subtle.  The most important, however, is the adjustment made to account
     * for differences between the device's white point and the white point of
     * the PCS.  There are many techniques for doing this and it is the
     * subject of much current research and controversy.  Some commonly used
     * methods are XYZ scaling, the von Kries transform, and the Bradford
     * transform.  The proper method to use depends upon each particular
     * application.
     * <p>
     * The simplest method is XYZ scaling.  In this method each device XYZ
     * value is  converted to a PCS XYZ value by multiplying it by the ratio
     * of the PCS white point (D50) to the device white point.
     * <pre>
     * 
     * Xd, Yd, Zd are the device XYZ values
     * Xdw, Ydw, Zdw are the device XYZ white point values
     * Xp, Yp, Zp are the PCS XYZ values
     * Xd50, Yd50, Zd50 are the PCS XYZ white point values
     * 
     * Xp = Xd * (Xd50 / Xdw)
     * Yp = Yd * (Yd50 / Ydw)
     * Zp = Zd * (Zd50 / Zdw)
     * 
     * </pre>
     * <p>
     * Conversion from the PCS to the device would be done by inverting these
     * equations:
     * <pre>
     * 
     * Xd = Xp * (Xdw / Xd50)
     * Yd = Yp * (Ydw / Yd50)
     * Zd = Zp * (Zdw / Zd50)
     * 
     * </pre>
     * <p>
     * Note that the media white point tag in an ICC profile is not the same
     * as the device white point.  The media white point tag is expressed in
     * PCS values and is used to represent the difference between the XYZ of
     * device illuminant and the XYZ of the device media when measured under
     * that illuminant.  The device white point is expressed as the device
     * XYZ values corresponding to white displayed on the device.  For
     * example, displaying the RGB color (1.0, 1.0, 1.0) on an sRGB device
     * will result in a measured device XYZ value of D65.  This will not
     * be the same as the media white point tag XYZ value in the ICC
     * profile for an sRGB device.
     * <p>
     * <p>
     * @param colorvalue a float array with length of at least 3.
     * @return a float array with length equal to the number of
     *         components in this ColorSpace.
     */
    public float[]    fromCIEXYZ(float[] colorvalue) {
    ICC_Transform[]    transformList;
    float[]        result;
    ICC_ColorSpace    xyzCS;
    
        if (xyz2this == null) {
            transformList = new ICC_Transform [2];
            
            xyzCS = (ICC_ColorSpace) ColorSpace.getInstance (CS_CIEXYZ);
            
            transformList[0] = new ICC_Transform (xyzCS.getProfile(),
                ICC_Transform.Any, ICC_Transform.In);
            try {
                transformList[1] = new ICC_Transform (thisProfile,
                    ICC_Profile.icRelativeColorimetric, ICC_Transform.Out);
            } catch (CMMException e) {
                transformList[1] = new ICC_Transform (thisProfile,
                    ICC_Transform.Any, ICC_Transform.Out);
            }

            xyz2this = new ICC_Transform (transformList);
        }

        // Fix for 4267139.  We do a specific fix here for the fact that the
        // ICC encoding for CIEXYZ maps to a range of 0.0 to 2.0, rather than
        // 0.0 to 1.0 to allow for X and Z values greater than 1.0 (Y values
        // should never be greater than 1.0).  What this means is that the
        // Y values passed to xyz2this.colorConvert (1, tmp, result)
        // should range from 0.0 to 0.5, so they need to be halved here.
        // REMIND: The only encoding we can know about right now that requires
        // this fix (given our interfaces to the underlying CMM) is CIEXYZ,
        // but this probably needs a more general long-term fix.
        float tmp[] = new float[3];
        float ALMOST_ONE_HALF = 1.0f / (1.0f + (32767.0f / 32768.0f));
        tmp[0] = colorvalue[0] * ALMOST_ONE_HALF;
        tmp[1] = colorvalue[1] * ALMOST_ONE_HALF;
        tmp[2] = colorvalue[2] * ALMOST_ONE_HALF;

        result = new float [this.getNumComponents()];

        xyz2this.colorConvert (1, tmp, result);
        
        return result;
    }
}
