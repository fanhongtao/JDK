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

package java.awt.image;

import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.color.*;
import sun.awt.color.ICC_Transform;
import sun.awt.color.ProfileDeferralMgr;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.RenderingHints;

/**
 * This class performs a pixel-by-pixel color conversion of the data in
 * the source image.  The resulting color values are scaled to the precision
 * of the destination image.  Color conversion can be specified
 * via an array of ColorSpace objects or an array of ICC_Profile objects.
 * <p>
 * If the source is a BufferedImage with premultiplied alpha, the
 * color components are divided by the alpha component before color conversion.
 * If the destination is a BufferedImage with premultiplied alpha, the
 * color components are multiplied by the alpha component after conversion.
 * Rasters are treated as having no alpha channel, i.e. all bands are
 * color bands.
 * <p>
 * If a RenderingHints object is specified in the constructor, the
 * color rendering hint and the dithering hint may be used to control
 * color conversion.
 * <p>
 * Note that Source and Destination may be the same object.
 * <p>
 * @see java.awt.RenderingHints#KEY_COLOR_RENDERING
 * @see java.awt.RenderingHints#KEY_DITHERING
 */
public class ColorConvertOp implements BufferedImageOp, RasterOp {
    ICC_Profile[]    profileList;
    ColorSpace[]     CSList;
    ICC_Transform    thisTransform, thisRasterTransform;
    ICC_Profile      thisSrcProfile, thisDestProfile;
    RenderingHints   hints;
    boolean          gotProfiles;

    /* the class initializer */
    static {
        if (ProfileDeferralMgr.deferring) {
            ProfileDeferralMgr.activateProfiles();
        }
    }

    /**
     * Constructs a new ColorConvertOp which will convert
     * from a source color space to a destination color space.
     * The RenderingHints argument may be null.
     * This Op can be used only with BufferedImages, and will convert
     * directly from the ColorSpace of the source image to that of the
     * destination.  The destination argument of the filter method
     * cannot be specified as null.
     * @param hints the <code>RenderingHints</code> object used to control
     *        the color conversion, or <code>null</code>
     */
    public ColorConvertOp (RenderingHints hints)
    {
        profileList = new ICC_Profile [0];    /* 0 length list */
        this.hints  = hints;
    }

    /**
     * Constructs a new ColorConvertOp from a ColorSpace object.
     * The RenderingHints argument may be null.  This
     * Op can be used only with BufferedImages, and is primarily useful
     * when the {@link #filter(BufferedImage, BufferedImage) filter}
     * method is invoked with a destination argument of null.
     * In that case, the ColorSpace defines the destination color space
     * for the destination created by the filter method.  Otherwise, the
     * ColorSpace defines an intermediate space to which the source is
     * converted before being converted to the destination space.
     * @param cspace defines the destination <code>ColorSpace</code> or an
     *        intermediate <code>ColorSpace</code> 
     * @param hints the <code>RenderingHints</code> object used to control
     *        the color conversion, or <code>null</code>
     */
    public ColorConvertOp (ColorSpace cspace, RenderingHints hints)
    {
        if (cspace instanceof ICC_ColorSpace) {
            profileList = new ICC_Profile [1];    /* 1 profile in the list */

            profileList [0] = ((ICC_ColorSpace) cspace).getProfile();
        }
        else {
            CSList = new ColorSpace[1]; /* non-ICC case: 1 ColorSpace in list */
            CSList[0] = cspace;
        }
        this.hints  = hints;
    }


    /**
     * Constructs a new ColorConvertOp from two ColorSpace objects.
     * The RenderingHints argument may be null.
     * This Op is primarily useful for calling the filter method on
     * Rasters, in which case the two ColorSpaces define the operation
     * to be performed on the Rasters.  In that case, the number of bands
     * in the source Raster must match the number of components in
     * srcCspace, and the number of bands in the destination Raster
     * must match the number of components in dstCspace.  For BufferedImages,
     * the two ColorSpaces define intermediate spaces through which the
     * source is converted before being converted to the destination space.
     * @param srcCspace the source <code>ColorSpace</code>
     * @param dstCspace the destination <code>ColorSpace</code>
     * @param hints the <code>RenderingHints</code> object used to control
     *        the color conversion, or <code>null</code>
     */
    public ColorConvertOp(ColorSpace srcCspace, ColorSpace dstCspace,
                           RenderingHints hints)
    {
        if ((srcCspace instanceof ICC_ColorSpace) &&
            (dstCspace instanceof ICC_ColorSpace)) {
            profileList = new ICC_Profile [2];    /* 2 profiles in the list */

            profileList [0] = ((ICC_ColorSpace) srcCspace).getProfile();
            profileList [1] = ((ICC_ColorSpace) dstCspace).getProfile();
        }
        else {
            /* non-ICC case: 2 ColorSpaces in list */
            CSList = new ColorSpace[2];
            CSList[0] = srcCspace;
            CSList[1] = dstCspace;
        }
        this.hints  = hints;
    }


     /**
     * Constructs a new ColorConvertOp from an array of ICC_Profiles.
     * The RenderingHints argument may be null.
     * The sequence of profiles may include profiles that represent color
     * spaces, profiles that represent effects, etc.  If the whole sequence
     * does not represent a well-defined color conversion, an exception is
     * thrown.
     * <p>For BufferedImages, if the ColorSpace
     * of the source BufferedImage does not match the requirements of the
     * first profile in the array,
     * the first conversion is to an appropriate ColorSpace.
     * If the requirements of the last profile in the array are not met
     * by the ColorSpace of the destination BufferedImage,
     * the last conversion is to the destination's ColorSpace.
     * <p>For Rasters, the number of bands in the source Raster must match
     * the requirements of the first profile in the array, and the
     * number of bands in the destination Raster must match the requirements
     * of the last profile in the array.  The array must have at least two
     * elements or calling the filter method for Rasters will throw an
     * IllegalArgumentException.
     * @param profiles the array of <code>ICC_Profile</code> objects
     * @param hints the <code>RenderingHints</code> object used to control
     *        the color conversion, or <code>null</code>
     * @exception IllegalArgumentException when the profile sequence does not
     *             specify a well-defined color conversion
     */
    public ColorConvertOp (ICC_Profile[] profiles, RenderingHints hints)
    {
        gotProfiles = true;
        profileList = new ICC_Profile[profiles.length];
        for (int i1 = 0; i1 < profiles.length; i1++) {
	    profileList[i1] = profiles[i1];
	}
        this.hints  = hints;
    }


    /**
     * Returns the array of ICC_Profiles used to construct this ColorConvertOp.
     * Returns null if the ColorConvertOp was not constructed from such an
     * array.
     * @return the array of <code>ICC_Profile</code> objects of this 
     *         <code>ColorConvertOp</code>, or <code>null</code> if this
     *         <code>ColorConvertOp</code> was not constructed with an
     *         array of <code>ICC_Profile</code> objects.
     */
    public final ICC_Profile[] getICC_Profiles() {
        if (gotProfiles) {
            ICC_Profile[] profiles = new ICC_Profile[profileList.length];
            for (int i1 = 0; i1 < profileList.length; i1++) {
                profiles[i1] = profileList[i1];
            }
            return profiles;
        }
        return null;
    }

    /**
     * ColorConverts the source BufferedImage.  
     * If the destination image is null,
     * a BufferedImage will be created with an appropriate ColorModel.
     * @param src the source <code>BufferedImage</code> to be converted
     * @param dest the destination <code>BufferedImage</code>, 
     *        or <code>null</code>
     * @return <code>dest</code> color converted from <code>src</code> 
     *         or a new, converted <code>BufferedImage</code>
     *         if <code>dest</code> is <code>null</code>
     * @exception IllegalArgumentException if dest is null and this op was
     *             constructed using the constructor which takes only a
     *             RenderingHints argument, since the operation is ill defined.
     */
    public final BufferedImage filter(BufferedImage src, BufferedImage dest) {
        ColorSpace srcColorSpace, destColorSpace;
        BufferedImage savdest = null;

        if (src.getColorModel() instanceof IndexColorModel) {
            IndexColorModel icm = (IndexColorModel) src.getColorModel();
            src = icm.convertToIntDiscrete(src.getRaster(), true);
        }
        srcColorSpace = src.getColorModel().getColorSpace();
        if (dest != null) {
            if (dest.getColorModel() instanceof IndexColorModel) {
                savdest = dest;
                dest = null;
                destColorSpace = null;
            } else {
                destColorSpace = dest.getColorModel().getColorSpace();
            }
        } else {
            destColorSpace = null;
        }

        if ((CSList != null) ||
            (!(srcColorSpace instanceof ICC_ColorSpace)) ||
            ((dest != null) &&
             (!(destColorSpace instanceof ICC_ColorSpace)))) {
            /* non-ICC case */
            dest = nonICCBIFilter(src, srcColorSpace, dest, destColorSpace);
        } else {
            dest = ICCBIFilter(src, srcColorSpace, dest, destColorSpace);
        }

        if (savdest != null) {
            Graphics2D big = savdest.createGraphics();
	    try {
	        big.drawImage(dest, 0, 0, null);
	    } finally {
	        big.dispose();
	    }
            return savdest;
        } else {
            return dest;
        }
    }

    private final BufferedImage ICCBIFilter(BufferedImage src,
                                            ColorSpace srcColorSpace,
                                            BufferedImage dest,
                                            ColorSpace destColorSpace) {
    ICC_Profile[]    theProfiles;
    int              nProfiles = profileList.length;
    int              i1, nTransforms, whichTrans, renderState;
    ICC_Transform[]  theTransforms;
    ICC_Profile      srcProfile = null, destProfile = null;

        srcProfile = ((ICC_ColorSpace) srcColorSpace).getProfile();

        if (dest == null) {        /* last profile in the list defines
                                      the output color space */
            if (nProfiles == 0) {
                throw new IllegalArgumentException(
                    "Destination ColorSpace is undefined");
            }
            nTransforms = nProfiles + 1;
            destProfile = profileList [nProfiles - 1];
            dest = createCompatibleDestImage(src, null);
        }
        else {
            if (src.getHeight() != dest.getHeight() ||
                src.getWidth() != dest.getWidth()) {
                throw new IllegalArgumentException(
                    "Width or height of BufferedImages do not match");
            }
            nTransforms = nProfiles +2;
            destProfile = ((ICC_ColorSpace) destColorSpace).getProfile();
        }

        /* make a new transform if needed */
        if ((thisTransform == null) || (thisSrcProfile != srcProfile) ||
            (thisDestProfile != destProfile) ) {

            /* make the profile list */
            theProfiles = new ICC_Profile[nTransforms]; /* the list of profiles
                                                           for this Op */

            theProfiles [0] = srcProfile;  /* insert source as first profile */

            for (i1 = 1; i1 < nTransforms - 1; i1++) {
                                       /* insert profiles defined in this Op */
                theProfiles [i1] = profileList [i1 -1];
            }

            theProfiles [nTransforms - 1] = destProfile; /* insert dest as last
                                                            profile */

            /* make the transform list */
            theTransforms = new ICC_Transform [nTransforms];

            /* initialize transform get loop */
            if (theProfiles[0].getProfileClass() == ICC_Profile.CLASS_OUTPUT) {
                                            /* if first profile is a printer
                                               render as colorimetric */
                renderState = ICC_Profile.icRelativeColorimetric;
            }
            else {
                renderState = ICC_Profile.icPerceptual; /* render any other
                                                           class perceptually */
            }

            whichTrans = ICC_Transform.In;

            /* get the transforms from each profile */
            for (i1 = 0; i1 < nTransforms; i1++) {
                if (i1 == nTransforms -1) {         /* last profile? */
                    whichTrans = ICC_Transform.Out; /* get output transform */
                }
                else {	/* check for abstract profile */
                    if ((whichTrans == ICC_Transform.Simulation) &&
                        (theProfiles[i1].getProfileClass () ==
                         ICC_Profile.CLASS_ABSTRACT)) {
                	renderState = ICC_Profile.icPerceptual;
                        whichTrans = ICC_Transform.In;
                    }
                }

                theTransforms[i1] = new ICC_Transform (theProfiles[i1],
                                                       renderState, whichTrans);

                /* get this profile's rendering intent to select transform
                   from next profile */
                renderState = getRenderingIntent(theProfiles[i1]);

                /* "middle" profiles use simulation transform */
                whichTrans = ICC_Transform.Simulation;
            }

            /* make the net transform */
            thisTransform = new ICC_Transform (theTransforms);

            /* update corresponding source and dest profiles */
            thisSrcProfile = srcProfile;
            thisDestProfile = destProfile;
        }

        /* color convert the image */
        thisTransform.colorConvert(src, dest);

        return dest;
    }

    /**
     * ColorConverts the image data in the source Raster.
     * If the destination Raster is null, a new Raster will be created.
     * The number of bands in the source and destination Rasters must
     * meet the requirements explained above.  The constructor used to
     * create this ColorConvertOp must have provided enough information
     * to define both source and destination color spaces.  See above.
     * Otherwise, an exception is thrown.
     * @param src the source <code>Raster</code> to be converted
     * @param dest the destination <code>WritableRaster</code>, 
     *        or <code>null</code>
     * @return <code>dest</code> color converted from <code>src</code> 
     *         or a new, converted <code>WritableRaster</code>
     *         if <code>dest</code> is <code>null</code>
     * @exception IllegalArgumentException if the number of source or
     *             destination bands is incorrect, the source or destination
     *             color spaces are undefined, or this op was constructed
     *             with one of the constructors that applies only to
     *             operations on BufferedImages.
     */
    public final WritableRaster filter (Raster src, WritableRaster dest)  {

        if (CSList != null) {
            /* non-ICC case */
            return nonICCRasterFilter(src, dest);
        }
        int nProfiles = profileList.length;
        if (nProfiles < 2) {
            throw new IllegalArgumentException(
                "Source or Destination ColorSpace is undefined");
        }
        if (src.getNumBands() != profileList[0].getNumComponents()) {
            throw new IllegalArgumentException(
                "Numbers of source Raster bands and source color space " +
                "components do not match");
        }
        if (dest == null) {
            dest = createCompatibleDestRaster(src);
        }
        else {
            if (src.getHeight() != dest.getHeight() ||
                src.getWidth() != dest.getWidth()) {
                throw new IllegalArgumentException(
                    "Width or height of Rasters do not match");
            }
            if (dest.getNumBands() !=
                profileList[nProfiles-1].getNumComponents()) {
                throw new IllegalArgumentException(
                    "Numbers of destination Raster bands and destination " +
                    "color space components do not match");
            }
        }

        /* make a new transform if needed */
        if (thisRasterTransform == null) {
            int              i1, whichTrans, renderState;
            ICC_Transform[]  theTransforms;

            /* make the transform list */
            theTransforms = new ICC_Transform [nProfiles];

            /* initialize transform get loop */
            if (profileList[0].getProfileClass() == ICC_Profile.CLASS_OUTPUT) {
                                            /* if first profile is a printer
                                               render as colorimetric */
                renderState = ICC_Profile.icRelativeColorimetric;
            }
            else {
                renderState = ICC_Profile.icPerceptual; /* render any other
                                                           class perceptually */
            }

            whichTrans = ICC_Transform.In;

            /* get the transforms from each profile */
            for (i1 = 0; i1 < nProfiles; i1++) {
                if (i1 == nProfiles -1) {         /* last profile? */
                    whichTrans = ICC_Transform.Out; /* get output transform */
                }
                else {	/* check for abstract profile */
                    if ((whichTrans == ICC_Transform.Simulation) &&
                        (profileList[i1].getProfileClass () ==
                         ICC_Profile.CLASS_ABSTRACT)) {
                	renderState = ICC_Profile.icPerceptual;
                        whichTrans = ICC_Transform.In;
                    }
                }

                theTransforms[i1] = new ICC_Transform (profileList[i1],
                                                       renderState, whichTrans);

                /* get this profile's rendering intent to select transform
                   from next profile */
                renderState = getRenderingIntent(profileList[i1]);

                /* "middle" profiles use simulation transform */
                whichTrans = ICC_Transform.Simulation;
            }

            /* make the net transform */
            thisRasterTransform = new ICC_Transform (theTransforms);
        }

        /* color convert the raster */
        thisRasterTransform.colorConvert(src, dest);

        return dest;
    }

    /**
     * Returns the bounding box of the destination, given this source.
     * Note that this will be the same as the the bounding box of the
     * source.
     * @param src the source <code>BufferedImage</code>
     * @return a <code>Rectangle2D</code> that is the bounding box
     *         of the destination, given the specified <code>src</code>
     */
    public final Rectangle2D getBounds2D (BufferedImage src) {
        return getBounds2D(src.getRaster());
    }

    /**
     * Returns the bounding box of the destination, given this source.
     * Note that this will be the same as the the bounding box of the
     * source.
     * @param src the source <code>Raster</code>
     * @return a <code>Rectangle2D</code> that is the bounding box
     *         of the destination, given the specified <code>src</code>
     */
    public final Rectangle2D getBounds2D (Raster src) {
        /*        return new Rectangle (src.getXOffset(),
                              src.getYOffset(),
                              src.getWidth(), src.getHeight()); */
        return src.getBounds();
    }

    /**
     * Creates a zeroed destination image with the correct size and number of
     * bands, given this source.
     * @param src       Source image for the filter operation.
     * @param destCM    ColorModel of the destination.  If null, an
     *                  appropriate ColorModel will be used.
     * @throws IllegalArgumentException if <code>destCM</code> is 
     *         <code>null</code> and this <code>ColorConvertOp</code> was 
     *         created without any <code>ICC_Profile</code> or 
     *         <code>ColorSpace</code> defined for the destination
     */
    public BufferedImage createCompatibleDestImage (BufferedImage src,
                                                    ColorModel destCM) {
        ColorSpace cs = null;;
        if (destCM == null) {
            if (CSList == null) {
                /* ICC case */
                int nProfiles = profileList.length;
                if (nProfiles == 0) {
                    throw new IllegalArgumentException(
                        "Destination ColorSpace is undefined");
                }
                ICC_Profile destProfile = profileList[nProfiles - 1];
                cs = new ICC_ColorSpace(destProfile);
            } else {
                /* non-ICC case */
                int nSpaces = CSList.length;
                cs = CSList[nSpaces - 1];
            }
        }
        return createCompatibleDestImage(src, destCM, cs);
    }

    private BufferedImage createCompatibleDestImage(BufferedImage src,
                                                    ColorModel destCM,
                                                    ColorSpace destCS) {
        BufferedImage image;
        if (destCM == null) {
            ColorModel srcCM = src.getColorModel();
            int nbands = destCS.getNumComponents();
            boolean hasAlpha = srcCM.hasAlpha();
            if (hasAlpha) {
               nbands += 1;
            }
            int[] nbits = new int[nbands];
            for (int i = 0; i < nbands; i++) {
                nbits[i] = 8;
            }
            destCM = new ComponentColorModel(destCS, nbits, hasAlpha,
                                             srcCM.isAlphaPremultiplied(),
                                             srcCM.getTransparency(),
                                             DataBuffer.TYPE_BYTE);
        }
        int w = src.getWidth();
        int h = src.getHeight();
        image = new BufferedImage(destCM,
                                  destCM.createCompatibleWritableRaster(w, h),
                                  destCM.isAlphaPremultiplied(), null);
        return image;
    }


    /**
     * Creates a zeroed destination Raster with the correct size and number of
     * bands, given this source.
     * @param src the specified <code>Raster</code>
     * @return a <code>WritableRaster</code> with the correct size and number
     *         of bands from the specified <code>src</code>
     * @throws IllegalArgumentException if this <code>ColorConvertOp</code> 
     *         was created without sufficient information to define the 
     *         <code>dst</code> and <code>src</code> color spaces
     */
    public WritableRaster createCompatibleDestRaster (Raster src) {
        int ncomponents;

        if (CSList != null) {
            /* non-ICC case */
            if (CSList.length != 2) {
                throw new IllegalArgumentException(
                    "Destination ColorSpace is undefined");
            }
            ncomponents = CSList[1].getNumComponents();
        } else {
            /* ICC case */
            int nProfiles = profileList.length;
            if (nProfiles < 2) {
                throw new IllegalArgumentException(
                    "Destination ColorSpace is undefined");
            }
            ncomponents = profileList[nProfiles-1].getNumComponents();
        }

        WritableRaster dest =
            Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
                                  src.getWidth(),
                                  src.getHeight(),
                                  ncomponents,
                                  new Point(src.getMinX(), src.getMinY()));
        return dest;
    }

    /**
     * Returns the location of the destination point given a
     * point in the source.  If <code>dstPt</code> is non-null, 
     * it will be used to hold the return value.  Note that 
     * for this class, the destination point will be the same 
     * as the source point.
     * @param srcPt the specified source <code>Point2D</code>
     * @param dstPt the destination <code>Point2D</code>
     * @return <code>dstPt</code> after setting its location to be
     *         the same as <code>srcPt</code>
     */
    public final Point2D getPoint2D (Point2D srcPt, Point2D dstPt) {
        if (dstPt == null) {
            dstPt = new Point2D.Float();
        }
        dstPt.setLocation(srcPt.getX(), srcPt.getY());

        return dstPt;
    }


    /**
     * Returns the RenderingIntent from the specified ICC Profile.
     */
    private int getRenderingIntent (ICC_Profile profile) {
        byte[] header = profile.getData(ICC_Profile.icSigHead);
        int index = ICC_Profile.icHdrRenderingIntent;
        return (((header[index]   & 0xff) << 24) |
                ((header[index+1] & 0xff) << 16) |
                ((header[index+2] & 0xff) <<  8) |
                 (header[index+3] & 0xff));
    }

    /**
     * Returns the rendering hints used by this op.
     * @return the <code>RenderingHints</code> object of this 
     *         <code>ColorConvertOp</code>
     */
    public final RenderingHints getRenderingHints() {
        return hints;
    }

    private final BufferedImage nonICCBIFilter(BufferedImage src,
                                               ColorSpace srcColorSpace,
                                               BufferedImage dest,
                                               ColorSpace destColorSpace) {

        int w = src.getWidth();
        int h = src.getHeight();
        ColorModel srcCM = src.getColorModel();
        boolean srcalpha = srcCM.hasAlpha();
        boolean srcpremult = false;
        ColorModel srcCMnp = null;
        BufferedImage nsrc = src;
        ColorSpace ciespace = ColorSpace.getInstance(ColorSpace.CS_CIEXYZ);
        if (srcalpha && srcCM.isAlphaPremultiplied()) {
            /* UNPREMULT */
            srcpremult = true;
            srcCMnp = srcCM.coerceData(src.getRaster(), false);
            nsrc = new BufferedImage(srcCMnp, src.getRaster(), false, null);
        }
        if (dest == null) {
            dest = createCompatibleDestImage(src, null);
            destColorSpace = dest.getColorModel().getColorSpace();
        } else {
            if ((h != dest.getHeight()) || (w != dest.getWidth())) {
                throw new IllegalArgumentException(
                    "Width or height of BufferedImages do not match");
            }
        }
        ColorModel destCM = dest.getColorModel();
        boolean destalpha = destCM.hasAlpha();
        boolean destpremult = false;
        ColorModel destCMnp = null;
        BufferedImage ndest = dest;
        if (destalpha && destCM.isAlphaPremultiplied()) {
            destpremult = true;
            if (src != dest) {
                /* REMIND - GROSS HACK to get a non-premultiplied dest CM */
                destCMnp = destCM.coerceData(dest.getRaster(), false);
            } else {
                destCMnp = srcCMnp;
            }
            ndest = new BufferedImage(destCMnp, dest.getRaster(), false, null);
        }
        if ((CSList == null) && (profileList.length != 0)) {
            /* possible non-ICC src, some profiles, possible non-ICC dest */
            BufferedImage stmp, dtmp;
            if (!(srcColorSpace instanceof ICC_ColorSpace)) {
                /* convert from non-ICC space to CIEXYZ space */
                stmp = createCompatibleDestImage(nsrc, null, ciespace);
                convertBItoCIEXYZ(nsrc, stmp);
            } else {
                stmp = nsrc;
            }
            if (!(destColorSpace instanceof ICC_ColorSpace)) {
                if (stmp != nsrc) {
                    dtmp = stmp;
                } else {
                    dtmp = createCompatibleDestImage(nsrc, null, ciespace);
                }
                dtmp = ICCBIFilter(stmp,
                                   stmp.getColorModel().getColorSpace(),
                                   dtmp,
                                   ciespace);
                convertBIfromCIEXYZ(dtmp, ndest);
            } else {
                ICCBIFilter(stmp, stmp.getColorModel().getColorSpace(),
                            ndest, destColorSpace);
            }
        } else {
            /* possible non-ICC src, possible CSList, possible non-ICC dest */
            BufferedImage stmp, dtmp;
            ColorSpace[] list;
            int i;
            if (CSList == null) {
                list = new ColorSpace[2];
                list[0] = srcColorSpace;
                list[1] = destColorSpace;
            }
            else {
                list = new ColorSpace[CSList.length + 2];
                list[0] = srcColorSpace;
                for (i = 0; i < CSList.length; i++) {
                    list[i + 1] = CSList[i];
                }
                list[list.length - 1] = destColorSpace;
            }
            stmp = nsrc;
            for (i = 1; i < list.length; i++) {
                dtmp = createCompatibleDestImage(stmp, null, list[i]);
                convertBItoBI(stmp, dtmp);
                stmp = dtmp;
            }
        }

        if (srcalpha && srcpremult) {
            /* REPREMULT */
            srcCMnp.coerceData(src.getRaster(), true);
        }
        if (destalpha) {
            fixDestAlpha(src, dest, srcalpha, destpremult, destCMnp);
        }

        return dest;
    }

    private void fixDestAlpha(BufferedImage src,
                              BufferedImage dest,
                              boolean srcHasAlpha,
                              boolean destPremult,
                              ColorModel destCMnp) {
        if (srcHasAlpha && (src != dest)) {
            /* COPY SRC ALPHA TO DEST */
            /* src and dest should be same size */
            Raster srcraster = src.getRaster();
            WritableRaster destraster = dest.getRaster();
            int salphaband = srcraster.getNumBands() - 1;
            int dalphaband = destraster.getNumBands() - 1;
            int xs, ys;
            int xd = destraster.getMinX();
            int yd = destraster.getMinY();
            int xstart = srcraster.getMinX();
            int ystart = srcraster.getMinY();
            int xlimit = xstart + srcraster.getWidth();
            int ylimit = ystart + srcraster.getHeight();
            int alpha;
            int sbits =
                src.getColorModel().getComponentSize(salphaband);
            int dbits =
                dest.getColorModel().getComponentSize(dalphaband);
            int lshift = dbits - sbits;
            int rshift = -lshift;

            for (ys = ystart; ys < ylimit; ys++,yd++) {
                if (lshift > 0) {
                    for (xs = xstart; xs < xlimit; xs++,xd++) {
                        alpha = srcraster.getSample(xs, ys, salphaband);
                        alpha <<= lshift;
                        destraster.setSample(xd, yd, dalphaband, alpha);
                    }
                } else if (lshift == 0) {
                    for (xs = xstart; xs < xlimit; xs++,xd++) {
                        alpha = srcraster.getSample(xs, ys, salphaband);
                        destraster.setSample(xd, yd, dalphaband, alpha);
                    }
                } else {
                    for (xs = xstart; xs < xlimit; xs++,xd++) {
                        alpha = srcraster.getSample(xs, ys, salphaband);
                        alpha >>>= rshift;
                        destraster.setSample(xd, yd, dalphaband, alpha);
                    }
                }
            }

            if (destPremult) {
                destCMnp.coerceData(destraster, true);
            }
        } else if (!srcHasAlpha) {
            /* FILL DST ALPHA with 1.0 */
            WritableRaster destraster = dest.getRaster();
            int alphaband = destraster.getNumBands() - 1;
            int x, y;
            int xstart = destraster.getMinX();
            int ystart = destraster.getMinY();
            int xlimit = xstart + destraster.getWidth();
            int ylimit = ystart + destraster.getHeight();
            int alpha =
             (1 << dest.getColorModel().getComponentSize(alphaband))
                 - 1;

            for (y = ystart; y < ylimit; y++) {
                for (x = xstart; x < xlimit; x++) {
                    destraster.setSample(x, y, alphaband, alpha);
                }
            }
        }
    }

    private void convertBItoCIEXYZ(BufferedImage srcbi, BufferedImage destbi) {
        WritableRaster src = srcbi.getRaster();
        WritableRaster dest = destbi.getRaster();
        int srcBands = srcbi.getColorModel().getNumColorComponents();
        int destBands = destbi.getColorModel().getNumColorComponents();
        int[] srcpixel = null;
        int[] destpixel = new int[destBands];
        float[] srcNorm = new float[srcBands];
        float[] cieColor = null;
        int[] srcbits = srcbi.getColorModel().getComponentSize();
        int[] destbits = destbi.getColorModel().getComponentSize();
        float[] srcNormFactor = new float[srcBands];
        float[] destNormFactor = new float[destBands];
        ColorSpace srcCS = srcbi.getColorModel().getColorSpace();
        int h = src.getHeight();
        int w = src.getWidth();
        int sx, sy, dx, dy;

        for (int k = 0; k < srcBands; k++) {
            srcNormFactor[k] = (float) ((1 << srcbits[k]) - 1);
        }
        for (int k = 0; k < destBands; k++) {
            destNormFactor[k] = (float) ((1 << destbits[k]) - 1);
        }
        sy = src.getMinY();
        dy = dest.getMinY();
        for (int i = 0; i < h; i++, sy++, dy++) {
            sx = src.getMinX();
            dx = dest.getMinX();
            for (int j = 0; j < w; j++, sx++, dx++) {
                srcpixel = src.getPixel(sx, sy, srcpixel);
                /* normalize the src pixel */
                for (int k = 0; k < srcBands; k++) {
                    srcNorm[k] = ((float) srcpixel[k]) / srcNormFactor[k];
                }
                cieColor = srcCS.toCIEXYZ(srcNorm);
                /* denormalize the dest pixel */
                for (int k = 0; k < destBands; k++) {
                    destpixel[k] = (int) (cieColor[k] * destNormFactor[k]);
                }
                dest.setPixel(dx, dy, destpixel);
            }
        }
    }

    private void convertBIfromCIEXYZ(BufferedImage srcbi,
                                     BufferedImage destbi) {
        WritableRaster src = srcbi.getRaster();
        WritableRaster dest = destbi.getRaster();
        int srcBands = srcbi.getColorModel().getNumColorComponents();
        int destBands = destbi.getColorModel().getNumColorComponents();
        int[] srcpixel = null;
        int[] destpixel = new int[destBands];
        float[] cieColor = new float[srcBands];
        float[] destNorm = null;
        int[] srcbits = srcbi.getColorModel().getComponentSize();
        int[] destbits = destbi.getColorModel().getComponentSize();
        float[] srcNormFactor = new float[srcBands];
        float[] destNormFactor = new float[destBands];
        ColorSpace destCS = destbi.getColorModel().getColorSpace();
        int h = src.getHeight();
        int w = src.getWidth();
        int sx, sy, dx, dy;

        for (int k = 0; k < srcBands; k++) {
            srcNormFactor[k] = (float) ((1 << srcbits[k]) - 1);
        }
        for (int k = 0; k < destBands; k++) {
            destNormFactor[k] = (float) ((1 << destbits[k]) - 1);
        }
        sy = src.getMinY();
        dy = dest.getMinY();
        for (int i = 0; i < h; i++, sy++, dy++) {
            sx = src.getMinX();
            dx = dest.getMinX();
            for (int j = 0; j < w; j++, sx++, dx++) {
                srcpixel = src.getPixel(sx, sy, srcpixel);
                /* normalize the src pixel */
                for (int k = 0; k < srcBands; k++) {
                    cieColor[k] = ((float) srcpixel[k]) / srcNormFactor[k];
                }
                destNorm = destCS.fromCIEXYZ(cieColor);
                /* denormalize the dest pixel */
                for (int k = 0; k < destBands; k++) {
                    destpixel[k] = (int) (destNorm[k] * destNormFactor[k]);
                }
                dest.setPixel(dx, dy, destpixel);
            }
        }
    }

    private void convertBItoBI(BufferedImage srcbi, BufferedImage destbi) {
        WritableRaster src = srcbi.getRaster();
        WritableRaster dest = destbi.getRaster();
        int srcBands = srcbi.getColorModel().getNumColorComponents();
        int destBands = destbi.getColorModel().getNumColorComponents();
        int[] srcpixel = null;
        int[] destpixel = new int[destBands];
        float[] srcNorm = new float[srcBands];
        float[] destNorm = null;
        float[] cieColor = null;
        int[] srcbits = srcbi.getColorModel().getComponentSize();
        int[] destbits = destbi.getColorModel().getComponentSize();
        float[] srcNormFactor = new float[srcBands];
        float[] destNormFactor = new float[destBands];
        ColorSpace srcCS = srcbi.getColorModel().getColorSpace();
        ColorSpace destCS = destbi.getColorModel().getColorSpace();
        int h = src.getHeight();
        int w = src.getWidth();
        int sx, sy, dx, dy;

        for (int k = 0; k < srcBands; k++) {
            srcNormFactor[k] = (float) ((1 << srcbits[k]) - 1);
        }
        for (int k = 0; k < destBands; k++) {
            destNormFactor[k] = (float) ((1 << destbits[k]) - 1);
        }
        sy = src.getMinY();
        dy = dest.getMinY();
        for (int i = 0; i < h; i++, sy++, dy++) {
            sx = src.getMinX();
            dx = dest.getMinX();
            for (int j = 0; j < w; j++, sx++, dx++) {
                srcpixel = src.getPixel(sx, sy, srcpixel);
                /* normalize the src pixel */
                for (int k = 0; k < srcBands; k++) {
                    srcNorm[k] = ((float) srcpixel[k]) / srcNormFactor[k];
                }
                cieColor = srcCS.toCIEXYZ(srcNorm);
                destNorm = destCS.fromCIEXYZ(cieColor);
                /* denormalize the dest pixel */
                for (int k = 0; k < destBands; k++) {
                    destpixel[k] = (int) (destNorm[k] * destNormFactor[k]);
                }
                dest.setPixel(dx, dy, destpixel);
            }
        }
    }

    private final WritableRaster nonICCRasterFilter(Raster src,
                                                    WritableRaster dest)  {

        if (CSList.length != 2) {
            throw new IllegalArgumentException(
                "Destination ColorSpace is undefined");
        }
        if (src.getNumBands() != CSList[0].getNumComponents()) {
            throw new IllegalArgumentException(
                "Numbers of source Raster bands and source color space " +
                "components do not match");
        }
        if (dest == null) {
            dest = createCompatibleDestRaster(src);
        } else {
            if (src.getHeight() != dest.getHeight() ||
                src.getWidth() != dest.getWidth()) {
                throw new IllegalArgumentException(
                    "Width or height of Rasters do not match");
            }
            if (dest.getNumBands() != CSList[1].getNumComponents()) {
                throw new IllegalArgumentException(
                    "Numbers of destination Raster bands and destination " +
                    "color space components do not match");
            }
        }

        int srcBands = src.getNumBands();
        int destBands = dest.getNumBands();
        int[] srcpixel = null;
        int[] destpixel = new int[destBands];
        float[] srcNorm = new float[srcBands];
        float[] destNorm = null;
        float[] cieColor = null;
        int[] srcbits = src.getSampleModel().getSampleSize();
        int[] destbits = dest.getSampleModel().getSampleSize();
        float[] srcNormFactor = new float[srcBands];
        float[] destNormFactor = new float[destBands];
        int h = src.getHeight();
        int w = src.getWidth();
        int sx, sy, dx, dy;

        for (int k = 0; k < srcBands; k++) {
            srcNormFactor[k] = (float) ((1 << srcbits[k]) - 1);
        }
        for (int k = 0; k < destBands; k++) {
            destNormFactor[k] = (float) ((1 << destbits[k]) - 1);
        }
        sy = src.getMinY();
        dy = dest.getMinY();
        for (int i = 0; i < h; i++, sy++, dy++) {
            sx = src.getMinX();
            dx = dest.getMinX();
            for (int j = 0; j < w; j++, sx++, dx++) {
                srcpixel = src.getPixel(sx, sy, srcpixel);
                /* normalize the src pixel */
                for (int k = 0; k < srcBands; k++) {
                    srcNorm[k] = ((float) srcpixel[k]) / srcNormFactor[k];
                }
                cieColor = CSList[0].toCIEXYZ(srcNorm);
                destNorm = CSList[1].fromCIEXYZ(cieColor);
                /* denormalize the dest pixel */
                for (int k = 0; k < destBands; k++) {
                    destpixel[k] = (int) (destNorm[k] * destNormFactor[k]);
                }
                dest.setPixel(dx, dy, destpixel);
            }
        }

        return dest;
    }

}
