/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import sun.awt.image.IntegerComponentRaster;


class AlphaCompositeContext implements CompositeContext {

    ColorModel srcCM;
    ColorModel dstCM;
    boolean srcNeedConvert;
    boolean dstNeedConvert;
    int rule;
    float extraAlpha;

    public AlphaCompositeContext(ColorModel s, ColorModel d, int rule,
                                 float extraAlpha) {

        srcCM = s;
        if (srcCM.equals(ColorModel.getRGBdefault())) {
            srcNeedConvert = false;
        } else {
            srcNeedConvert = true;
        }

        dstCM = d;
        if (dstCM.equals(ColorModel.getRGBdefault())) {
            dstNeedConvert = false;
        } else {
            dstNeedConvert = true;
        }
        
        this.rule = rule;
        this.extraAlpha = extraAlpha;
    }
    
    /*
     * Convert a given Raster to the desired data format.
     */
    WritableRaster convertRaster(Raster inRaster, ColorModel inCM, ColorModel outCM) {
        // Use a faster conversion if this is an IndexColorModel
        if (inCM instanceof IndexColorModel &&
            outCM.equals(ColorModel.getRGBdefault())) {
            IndexColorModel icm = (IndexColorModel) inCM;
            BufferedImage dbi = icm.convertToIntDiscrete(inRaster, false);
            return dbi.getRaster();
        }

        BufferedImage dbi =
            new BufferedImage(outCM,
                    outCM.createCompatibleWritableRaster(inRaster.getWidth(),
                              inRaster.getHeight()),
                              outCM.isAlphaPremultiplied(),
                              null);
        
//         ColorSpace[] cs = {inCM.getColorSpace(), outCM.getColorSpace()};
//         ColorConvertOp cOp = new ColorConvertOp(cs);
//         cOp.filter(sbi, dbi);
        // use this slow method to convert untill ColorConvertOp is available.
        // Does not take in to account quality dithering if applicable.
        
        for (int i = 0 ; i < inRaster.getHeight() ; i++) {
            for (int j = 0 ; j < inRaster.getWidth() ; j++) {
                dbi.setRGB(j, i,
                           inCM.getRGB(inRaster.getDataElements(j,i,null)));
            }
        }
        return dbi.getRaster();
    }

    /**
     * Release resources allocated for context.
     */
    public void dispose() {
    }

//     native void alphaComposite(Raster s, Raster d,
//                                int width, int height,
//                                int rule, float extraAlpha);
    
    /**
     * This method composes the two source tiles
     * and places the result in the destination tile. Note that
     * the destination can be the same object as either
     * the first or second source.
     * @param src1 The first source tile for the compositing operation.
     * @param src2 The second source tile for the compositing operation.
     * @param dst The tile where the result of the operation is stored.
     */
    public void compose(Raster src1, Raster src2, WritableRaster dst) {
        IntegerComponentRaster s;
        IntegerComponentRaster d;
        WritableRaster dstOrg = dst;
        int w;
        int h;
        
        if (srcNeedConvert) {
            src1 = convertRaster(src1, srcCM, ColorModel.getRGBdefault());
            src2 = convertRaster(src2, srcCM, ColorModel.getRGBdefault());
        }
        if (dstNeedConvert && !(dst == src1 || dst == src2)) {
            dst = convertRaster(dst, dstCM, ColorModel.getRGBdefault());
        }

        if (dst == src1) {
            s = (IntegerComponentRaster)src2;
        } else if (dst == src2) {
            s = (IntegerComponentRaster)src1;
        } else {
            dst.setDataElements(0, 0, src2);
            s = (IntegerComponentRaster)src1;
        }
        d = (IntegerComponentRaster)dst;

        w = Math.min(s.getWidth(), d.getWidth());
        h = Math.min(s.getHeight(), d.getHeight());
        
        // REMIND: May need a wrapper in RasterOutputManager.c to avoid
        // reference to sun.awt.image package from here:
//         alphaComposite(s, d, w, h, rule, extraAlpha);
        
        sun.java2d.loops.RasterOutputManager.ARGBpaintARGB(s, false, d, rule,
                                                        extraAlpha, null,
                                                        0, 0, 0, 0, 0, 0,
                                                        w, h, 0);

        if (dstNeedConvert) {
            dst = convertRaster(dst, ColorModel.getRGBdefault(), dstCM);
            dstOrg.setDataElements(0, 0, dst);
        }
        
    }
}

