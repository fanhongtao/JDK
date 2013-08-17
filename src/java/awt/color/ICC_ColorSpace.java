/*
 * @(#)ICC_ColorSpace.java	1.14 98/05/04
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
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
 * device independent and device dependent color spaces is based on the ICC
 * Profile Format Specification, Version 3.4, August 15, 1997, from
 * the International Color Consortium (see <A href="http://www.color.org">
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
 * PCS which is one of the two specific device independent
 * spaces (one CIEXYZ space and one CIELab space) defined in the
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



public class ICC_ColorSpace
extends ColorSpace {
    private ICC_Profile    thisProfile;
    private ICC_Transform    this2srgb, srgb2this;
    private ICC_Transform    this2xyz, xyz2this;


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

        result = new float [3];

        this2srgb.colorConvert (1, colorvalue, result);
        
        return result;
    }

    /**
    * Transforms a color value assumed to be in the default CS_sRGB
    * color space into this ColorSpace.
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
        
        return result;
    }


    /**
    * Transforms a color value assumed to be in this ColorSpace
    * into the CS_CIEXYZ conversion color space.
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
            
            transformList[0] = new ICC_Transform (
                thisProfile, ICC_Transform.Any, ICC_Transform.In);
            transformList[1] = new ICC_Transform (
                xyzCS.getProfile(), ICC_Transform.Any, ICC_Transform.Out);

            this2xyz = new ICC_Transform (transformList);
        }

        result = new float [3];

        this2xyz.colorConvert (1, colorvalue, result);
        
        return result;
    }


    /**
    * Transforms a color value assumed to be in the CS_CIEXYZ conversion
    * color space into this ColorSpace.
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
            
            transformList[0] = new ICC_Transform (
                xyzCS.getProfile(), ICC_Transform.Any, ICC_Transform.In);
            transformList[1] = new ICC_Transform (
                thisProfile, ICC_Transform.Any, ICC_Transform.Out);

            xyz2this = new ICC_Transform (transformList);
        }

        result = new float [this.getNumComponents()];

        xyz2this.colorConvert (1, colorvalue, result);
        
        return result;
    }


}

