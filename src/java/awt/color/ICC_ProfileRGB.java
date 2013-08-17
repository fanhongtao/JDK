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

import java.awt.image.LookupTable;
import sun.awt.color.ProfileDeferralInfo;

/**
 *
 * A subclass of the ICC_Profile class which represents profiles
 * which meet the following criteria: the color space type of the
 * profile is RGB and the profile includes the redColorantTag,
 * greenColorantTag, blueColorantTag, redTRCTag, greenTRCTag,
 * blueTRCTag, and mediaWhitePointTag tags.  Examples of this
 * kind of profile are three-component matrix-based input profiles
 * and RGB display profiles.  The getInstance methods in the
 * ICC_Profile class will return an ICC_ProfileRGB object
 * when the above conditions are met.  The advantage of this class
 * is that it provides color transform matrices and lookup tables
 * that Java or native methods may be able to use directly to
 * optimize color conversion in some cases.
 * <p>
 * To transform from a device profile color space to the CIEXYZ Profile
 * Connection Space, each device color component is first linearized by
 * a lookup through the corresponding tone reproduction curve (TRC).
 * Then the resulting linear RGB components are converted via a 3x3 matrix
 * (constructed from the RGB colorants) to the CIEXYZ PCS.
<pre>

&nbsp;               linearR = redTRC[deviceR]

&nbsp;               linearG = greenTRC[deviceG]

&nbsp;               linearB = blueTRC[deviceB]

&nbsp; _      _       _                                             _   _         _
&nbsp;[  PCSX  ]     [  redColorantX  greenColorantX  blueColorantX  ] [  linearR  ]
&nbsp;[        ]     [                                               ] [           ]
&nbsp;[  PCSY  ]  =  [  redColorantY  greenColorantY  blueColorantY  ] [  linearG  ]
&nbsp;[        ]     [                                               ] [           ]
&nbsp;[_ PCSZ _]     [_ redColorantZ  greenColorantZ  blueColorantZ _] [_ linearB _]

</pre>
 * The inverse transform is done by converting PCS XYZ components to linear
 * RGB components via the inverse of the above 3x3 matrix, and then converting
 * linear RGB to device RGB via inverses of the TRCs.
 * <p>
 */



public class ICC_ProfileRGB
extends ICC_Profile {

    /**
     * To request a gamma value or TRC for the red component.
     */
    public static final int REDCOMPONENT = 0;

    /**
     * To request a gamma value or TRC for the green component.
     */
    public static final int GREENCOMPONENT = 1;

    /**
     * To request a gamma value or TRC for the blue component.
     */
    public static final int BLUECOMPONENT = 2;


    /**
     * Constructs an new ICC_ProfileRGB from a CMM ID.
     */
    ICC_ProfileRGB(long ID) {
        super(ID);
    }

    /**
     * Constructs a new ICC_ProfileRGB from a ProfileDeferralInfo object.
     */
    ICC_ProfileRGB(ProfileDeferralInfo pdi) {
        super(pdi);
    }


    /**
     * Returns a float array of length 3 containing the X, Y, and Z
     * components of the mediaWhitePointTag in the ICC profile.
     */
    public float[] getMediaWhitePoint() {
        return super.getMediaWhitePoint();
    }


    /**
     * Returns a 3x3 float matrix constructed from the X, Y, and Z
     * components of the redColorantTag, greenColorantTag, and
     * blueColorantTag in the ICC profile, as described above.
     * This matrix can be used for color transforms in the forward
     * direction of the profile, i.e. from the profile color space
     * to the CIEXYZ PCS.
     */
    public float[][] getMatrix() {
        float[][] theMatrix = new float[3][3];
        float[] tmpMatrix;

        tmpMatrix = getXYZTag(ICC_Profile.icSigRedColorantTag);
        theMatrix[0][0] = tmpMatrix[0];
        theMatrix[1][0] = tmpMatrix[1];
        theMatrix[2][0] = tmpMatrix[2];
        tmpMatrix = getXYZTag(ICC_Profile.icSigGreenColorantTag);
        theMatrix[0][1] = tmpMatrix[0];
        theMatrix[1][1] = tmpMatrix[1];
        theMatrix[2][1] = tmpMatrix[2];
        tmpMatrix = getXYZTag(ICC_Profile.icSigBlueColorantTag);
        theMatrix[0][2] = tmpMatrix[0];
        theMatrix[1][2] = tmpMatrix[1];
        theMatrix[2][2] = tmpMatrix[2];
        return theMatrix;
    }

    /**
     * Returns a gamma value representing the tone reproduction curve
     * (TRC) for a particular component.  Component must be one of
     * REDCOMPONENT, GREENCOMPONENT, or BLUECOMPONENT.  If the profile
     * represents the TRC for the corresponding component
     * as a table rather than a single gamma value, then an
     * exception is thrown.  In this case the actual table
     * can be obtained via getTRC().  When using a gamma value,
     * the linear component (R, G, or B) is computed as follows:
<pre>

&nbsp;                                         gamma
&nbsp;        linearComponent = deviceComponent

</pre>
     * @return the gamma value as a float.
     * @exception ProfileDataException if the profile does not specify
     *            the corresponding TRC as a single gamma value.
     */
    public float getGamma(int component) {
    float theGamma;
    int theSignature;

        switch (component) {
        case REDCOMPONENT:
            theSignature = ICC_Profile.icSigRedTRCTag;
            break;

        case GREENCOMPONENT:
            theSignature = ICC_Profile.icSigGreenTRCTag;
            break;

        case BLUECOMPONENT:
            theSignature = ICC_Profile.icSigBlueTRCTag;
            break;

        default:
            throw new IllegalArgumentException("Must be Red, Green, or Blue");
        }

        theGamma = super.getGamma(theSignature);

        return theGamma;
    }

    /**
     * Returns the TRC for a particular component as an array of
     * shorts.  Component must be one of REDCOMPONENT, GREENCOMPONENT, or
     * BLUECOMPONENT.  If the profile has specified the corresponding TRC
     * as linear (gamma = 1.0) or as a simple gamma value, this method
     * throws an exception, and the getGamma() method should be used
     * to get the gamma value.  Otherwise the short array returned here
     * represents a lookup table where the input component value
     * is conceptually in the range [0.0, 1.0].  Value 0.0 maps
     * to array index 0 and value 1.0 maps to array index length-1.
     * Interpolation may be used to generate output values for
     * input values which do not map exactly to an index in the
     * array.  Output values also map linearly to the range [0.0, 1.0].
     * Value 0.0 is represented by an array value of 0x0000 and
     * value 1.0 by 0xFFFF, i.e. the values are really unsigned
     * short values, although they are returned in a short array.
     * @return a short array representing the TRC.
     * @exception ProfileDataException if the profile does not specify
     *            the corresponding TRC as a table.
     */
    public short[] getTRC(int component) {
    short[] theTRC;
    int theSignature;

        switch (component) {
        case REDCOMPONENT:
            theSignature = ICC_Profile.icSigRedTRCTag;
            break;

        case GREENCOMPONENT:
            theSignature = ICC_Profile.icSigGreenTRCTag;
            break;

        case BLUECOMPONENT:
            theSignature = ICC_Profile.icSigBlueTRCTag;
            break;

        default:
            throw new IllegalArgumentException("Must be Red, Green, or Blue");
        }

        theTRC = super.getTRC(theSignature);

        return theTRC;
    }

}

