/*
 * @(#)PixelInterleavedSampleModel.java	1.8 98/08/03
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
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
 *  This class represents image data which is stored in a pixel interleaved
 *  fashion and for
 *  which each sample of a pixel occupies one data element of the DataBuffer.
 *  It subclasses ComponentSampleModel but provides a more efficent
 *  implementation for accessing pixel interleaved image data than is provided
 *  by ComponentSampleModel.  This class 
 *  stores sample data for all bands in a single bank of the
 *  DataBuffer. Accessor methods are provided so that image data can be
 *  manipulated directly. Pixel stride is the number of
 *  data array elements between two samples for the same band on the same
 *  scanline. Scanline stride is the number of data array elements between
 *  a given sample and the corresponding sample in the same column of the next
 *  scanline.  Band offsets denote the number
 *  of data array elements from the first data array element of the bank
 *  of the DataBuffer holding each band to the first sample of the band.
 *  The bands are numbered from 0 to N-1.
 *  Bank indices denote the correspondence between a bank of the data buffer
 *  and a band of image data.
 */

public class PixelInterleavedSampleModel extends ComponentSampleModel
{
    /**
     * Constructs a PixelInterleavedSampleModel with the specified parameters.
     * The number of bands will be given by the length of the bandOffsets array.     * @param dataType  The data type for storing samples.
     * @param w         The width (in pixels) of the region of
     *                  image data described.
     * @param h         The height (in pixels) of the region of
     *                  image data described.
     * @param pixelStride The pixel stride of the image data.
     * @param scanlineStride The line stride of the image data.
     * @param bandOffsets The offsets of all bands.
     */
    public PixelInterleavedSampleModel(int dataType,
                                       int w, int h,
                                       int pixelStride,
                                       int scanlineStride,
                                       int bandOffsets[]) {
        super(dataType, w, h, pixelStride, scanlineStride, bandOffsets);
        int minBandOff=bandOffsets[0];
        int maxBandOff=bandOffsets[0];
        for (int i=1; i<bandOffsets.length; i++) {
            minBandOff = Math.min(minBandOff,bandOffsets[i]);
            maxBandOff = Math.max(maxBandOff,bandOffsets[i]);
        }
        maxBandOff -= minBandOff;
        if (maxBandOff > scanlineStride) {
            throw new IllegalArgumentException("Offsets between bands must be"+
                                               " less than the scanline "+
                                               " stride");
        }
        if (pixelStride*w > scanlineStride) {
            throw new IllegalArgumentException("Pixel stride times width "+
                                               "must be less "+
                                               "than the scanline stride");
        }
        if (pixelStride < maxBandOff) {
            throw new IllegalArgumentException("Pixel stride must be greater"+
                                               " than or equal to the offsets"+
                                               " between bands");
        }
    }

    /**
     * Creates a new PixelInterleavedSampleModel with the specified
     * width and height.  The new PixelInterleavedSampleModel will have the
     * same number of bands, storage data type, and pixel stride
     * as this PixelInterleavedSampleModel.  The band offsets may be
     * compressed such that the minimum of all of the band offsets is zero.
     */
    public SampleModel createCompatibleSampleModel(int w, int h) {
        int minBandoff=bandOffsets[0];
        int numBands = bandOffsets.length;
        for (int i=1; i < numBands; i++) {
            if (bandOffsets[i] < minBandoff) {
                minBandoff = bandOffsets[i];
            }
        }
        int[] bandOff;
        if (minBandoff > 0) {
            bandOff = new int[numBands];
            for (int i=0; i < numBands; i++) {
                bandOff[i] = bandOffsets[i] - minBandoff;
            }
        }
        else {
            bandOff = bandOffsets;
        }
        return new PixelInterleavedSampleModel(dataType, w, h, pixelStride,
                                               pixelStride*w, bandOff);
    }

    /**
     * Creates a new PixelInterleavedSampleModel with a subset of the
     * bands of this PixelInterleavedSampleModel.  The new
     * PixelInterleavedSampleModel can be used with any DataBuffer that the
     * existing PixelInterleavedSampleModel can be used with.  The new
     * PixelInterleavedSampleModel/DataBuffer combination will represent
     * an image with a subset of the bands of the original
     * PixelInterleavedSampleModel/DataBuffer combination.
     */
    public SampleModel createSubsetSampleModel(int bands[]) {
        int newBandOffsets[] = new int[bands.length];
        for (int i=0; i<bands.length; i++) {
            newBandOffsets[i] = bandOffsets[bands[i]];
        }
        return new PixelInterleavedSampleModel(this.dataType, width, height,
                                               this.pixelStride,
                                               scanlineStride, newBandOffsets);
    }

}
