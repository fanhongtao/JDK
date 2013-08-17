/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/* ****************************************************************
 ******************************************************************
 ******************************************************************
 *** COPYRIGHT (c) Eastman Kodak Company, 1997
 *** As  an unpublished  work pursuant to Title 17 of the United
 *** States Code.  All rights reserved.
 ******************************************************************
 ******************************************************************
 ******************************************************************/


package java.awt.image;
import java.awt.Rectangle;
import java.awt.Point;

import sun.awt.image.ByteInterleavedRaster;
import sun.awt.image.ShortInterleavedRaster;
import sun.awt.image.IntegerInterleavedRaster;
import sun.awt.image.ByteBandedRaster;
import sun.awt.image.ShortBandedRaster;
import sun.awt.image.BytePackedRaster;

/**
 * A class representing a rectangular array of pixels.  A Raster
 * encapsulates a DataBuffer that stores the sample values and a
 * SampleModel that describes how to locate a given sample value in a
 * DataBuffer.
 * <p>
 * A Raster defines values for pixels occupying a particular
 * rectangular area of the plane, not necessarily including (0, 0).
 * The rectangle, known as the Raster's bounding rectangle and
 * available by means of the getBounds method, is defined by minX,
 * minY, width, and height values.  The minX and minY values define
 * the coordinate of the upper left corner of the Raster.  References
 * to pixels outside of the bounding rectangle may result in an
 * exception being thrown, or may result in references to unintended
 * elements of the Raster's associated DataBuffer.  It is the user's
 * responsibility to avoid accessing such pixels.
 * <p>
 * A SampleModel describes how samples of a Raster
 * are stored in the primitive array elements of a DataBuffer.
 * Samples may be stored one per data element, as in a
 * PixelInterleavedSampleModel or BandedSampleModel, or packed several to
 * an element, as in a SinglePixelPackedSampleModel or
 * MultiPixelPackedSampleModel.  The SampleModel is also
 * controls whether samples are sign extended, allowing unsigned
 * data to be stored in signed Java data types such as byte, short, and
 * int.
 * <p>
 * Although a Raster may live anywhere in the plane, a SampleModel
 * makes use of a simple coordinate system that starts at (0, 0).  A
 * Raster therefore contains a translation factor that allows pixel
 * locations to be mapped between the Raster's coordinate system and
 * that of the SampleModel.  The translation from the SampleModel
 * coordinate system to that of the Raster may be obtained by the
 * getSampleModelTranslateX and getSampleModelTranslateY methods.
 * <p>
 * A Raster may share a DataBuffer with another Raster either by
 * explicit construction or by the use of the createChild and
 * createTranslatedChild methods.  Rasters created by these methods
 * can return a reference to the Raster they were created from by
 * means of the getParent method.  For a Raster that was not
 * constructed by means of a call to createTranslatedChild or
 * createChild, getParent will return null.
 * <p>
 * The createTranslatedChild method returns a new Raster that
 * shares all of the data of the current Raster, but occupies a
 * bounding rectangle of the same width and height but with a
 * different starting point.  For example, if the parent Raster
 * occupied the region (10, 10) to (100, 100), and the translated
 * Raster was defined to start at (50, 50), then pixel (20, 20) of the
 * parent and pixel (60, 60) of the child occupy the same location in
 * the DataBuffer shared by the two Rasters.  In the first case, (-10,
 * -10) should be added to a pixel coordinate to obtain the
 * corresponding SampleModel coordinate, and in the second case (-50,
 * -50) should be added.
 * <p>
 * The translation between a parent and child Raster may be
 * determined by subtracting the child's sampleModelTranslateX and
 * sampleModelTranslateY values from those of the parent.
 * <p>
 * The createChild method may be used to create a new Raster
 * occupying only a subset of its parent's bounding rectangle
 * (with the same or a translated coordinate system) or
 * with a subset of the bands of its parent.
 * <p>
 * All constructors are protected.  The correct way to create a
 * Raster is to use one of the static create methods defined in this
 * class.  These methods create instances of Raster that use the
 * standard Interleaved, Banded, and Packed SampleModels and that may
 * be processed more efficiently than a Raster created by combining
 * an externally generated SampleModel and DataBuffer.
 * @see java.awt.image.DataBuffer
 * @see java.awt.image.SampleModel
 * @see java.awt.image.PixelInterleavedSampleModel
 * @see java.awt.image.BandedSampleModel
 * @see java.awt.image.SinglePixelPackedSampleModel
 * @see java.awt.image.MultiPixelPackedSampleModel
 */

// Code changes since beta 3:
//
// change baseRasterOffsetX to sampleModelTranslateX
//
// change baseRasterOffsetY to sampleModelTranslateY
//
// remove getBaseSubRasterOffsetX, replace with:
//   (getMinX() - getSampleModelTranslateX()
//
// remove getBaseSubRasterOffsetY, replace with:
//   (getMinY() - getSampleModelTranslateY()
//
// remove createSubRaster(Rectangle)
//
// remove createSubRaster(int x, int y, int width, int height),
//   replace with createChild(x, y, width, height, x, y, null)
//
// change createTranslatedRaster() to createTranslatedChild
//
// change createSubRaster() to createChild
//
// change getPixelData to getDataElements everywhere
//   -- need to propagate to SampleModels
//
// change getPixel() to getPixels() when getting a Rect
//   -- need to propagate to SampleModels
//
// change getSample() to getSamples() when getting a Rect
//   -- need to propagate to SampleModels
//
// change getBaseRasterOriginX() to getSampleModelTranslateX()
//
// change getBaseRasterOriginY() to getSampleModelTranslateY()
public class Raster {

    /**
     * The SampleModel that describes how pixels from this Raster
     * are stored in the DataBuffer.
     */
    protected SampleModel sampleModel;

    /** The DataBuffer that stores the image data. */
    protected DataBuffer dataBuffer;

    /** The X coordinate of the upper-left pixel of this Raster. */
    protected int minX;

    /** The Y coordinate of the upper-left pixel of this Raster. */
    protected int minY;

    /** The width of this Raster. */
    protected int width;

    /** The height of this Raster. */
    protected int height;

    /**
     * The X translation from the coordinate space of the
     * Raster's SampleModel to that of the Raster.
     */
    protected int sampleModelTranslateX;

    /**
     * The Y translation from the coordinate space of the
     * Raster's SampleModel to that of the Raster.
     */
    protected int sampleModelTranslateY;

    /** The number of bands in the Raster. */
    protected int numBands;

    /** The number of DataBuffer data elements per pixel. */
    protected int numDataElements;

    /** The parent of this Raster, or null. */
    protected Raster parent;

    static private native void initIDs();
    static {
        ColorModel.loadLibraries();
        initIDs();
    }

    /**
     * Creates a Raster based on a PixelInterleavedSampleModel with the
     * specified data type, width, height, and number of bands.
     *
     * <p> The upper left corner of the Raster is given by the
     * location argument.  If location is null, (0, 0) will be used.
     * The dataType parameter should be one of the enumerated values
     * defined in the DataBuffer class.
     *
     * <p> Note that interleaved <code>DataBuffer.TYPE_INT</code>
     * Rasters are not supported.  To create a 1-band Raster of type
     * <code>DataBuffer.TYPE_INT</code>, use
     * Raster.createPackedRaster().
     * <p> The only dataTypes supported currently are TYPE_BYTE
     * and TYPE_USHORT.
     */
    public static WritableRaster createInterleavedRaster(int dataType,
                                                         int w, int h,
                                                         int bands,
                                                         Point location) {
        int[] bandOffsets = new int[bands];
        for (int i = 0; i < bands; i++) {
            bandOffsets[i] = i;
        }
        return createInterleavedRaster(dataType, w, h, w*bands, bands,
                                       bandOffsets, location);
    }

    /**
     * Creates a Raster based on a PixelInterleavedSampleModel with the
     * specified data type, width, height, scanline stride, pixel
     * stride, and band offsets.  The number of bands is inferred from
     * bandOffsets.length.
     *
     * <p> The upper left corner of the Raster is given by the
     * location argument.  If location is null, (0, 0) will be used.
     * The dataType parameter should be one of the enumerated values
     * defined in the DataBuffer class.
     *
     * <p> Note that interleaved <code>DataBuffer.TYPE_INT</code>
     * Rasters are not supported.  To create a 1-band Raster of type
     * <code>DataBuffer.TYPE_INT</code>, use
     * Raster.createPackedRaster().
     * <p> The only dataTypes supported currently are TYPE_BYTE
     * and TYPE_USHORT.
     * @throws IllegalArgumentException if <code>dataType</code> is not
     *         one of the supported data types, which are
     *         <code>DataBuffer.TYPE_BYTE</code>, 
     *         <code>DataBuffer.TYPE_USHORT</code> 
     *         or <code>DataBuffer.TYPE_INT</code>
     */
    public static WritableRaster createInterleavedRaster(int dataType,
                                                         int w, int h,
                                                         int scanlineStride,
                                                         int pixelStride,
                                                         int bandOffsets[],
                                                         Point location) {
	DataBuffer d;
        int bands = bandOffsets.length;

        int maxBandOff = bandOffsets[0];
        for (int i=1; i < bands; i++) {
            if (bandOffsets[i] > maxBandOff) {
                maxBandOff = bandOffsets[i];
            }
        }
        int size = maxBandOff + scanlineStride*(h-1) + pixelStride*(w-1) + 1;
        switch(dataType) {
        case DataBuffer.TYPE_BYTE:
            d = new DataBufferByte(size);
            break;

        case DataBuffer.TYPE_USHORT:
            d = new DataBufferUShort(size);
            break;

        default:
            throw new IllegalArgumentException("Unsupported data type " +
                                                dataType);
        }

        return createInterleavedRaster(d, w, h, scanlineStride,
                                       pixelStride, bandOffsets, location);
    }

    /**
     * Creates a Raster based on a BandedSampleModel with the
     * specified data type, width, height, and number of bands.
     *
     * <p> The upper left corner of the Raster is given by the
     * location argument.  If location is null, (0, 0) will be used.
     * The dataType parameter should be one of the enumerated values
     * defined in the DataBuffer class.
     *
     * <p> The only dataTypes supported currently are TYPE_BYTE, TYPE_USHORT,
     * and TYPE_INT.
     * @throws ArrayIndexOutOfBoundsException if <code>bands</code>
     *         is less than 1
     */
    public static WritableRaster createBandedRaster(int dataType,
                                                    int w, int h,
                                                    int bands,
                                                    Point location) {
        if (bands < 1) {
            throw new ArrayIndexOutOfBoundsException("Number of bands ("+
                                                     bands+") must"+
                                                     " be greater than 0");
        }
        int[] bankIndices = new int[bands];
        int[] bandOffsets = new int[bands];
        for (int i = 0; i < bands; i++) {
            bankIndices[i] = i;
            bandOffsets[i] = 0;
        }

        return createBandedRaster(dataType, w, h, w,
                                  bankIndices, bandOffsets,
                                  location);
    }

    /**
     * Creates a Raster based on a BandedSampleModel with the
     * specified data type, width, height, scanline stride, bank
     * indices and band offsets.  The number of bands is inferred from
     * bankIndices.length and bandOffsets.length, which must be the
     * same.
     *
     * <p> The upper left corner of the Raster is given by the
     * location argument.  The dataType parameter should be one of the
     * enumerated values defined in the DataBuffer class.
     *
     * <p> The only dataTypes supported currently are TYPE_BYTE, TYPE_USHORT,
     * and TYPE_INT.
     * @throws IllegalArgumentException if <code>dataType</code> is not
     *         one of the supported data types, which are
     *         <code>DataBuffer.TYPE_BYTE</code>, 
     *         <code>DataBuffer.TYPE_USHORT</code> 
     *         or <code>DataBuffer.TYPE_INT</code>
     * @throws ArrayIndexOutOfBoundsException if <code>bankIndices</code> 
     *         or <code>bandOffsets</code> is <code>null</code>
     */
    public static WritableRaster createBandedRaster(int dataType,
                                                    int w, int h,
                                                    int scanlineStride,
                                                    int bankIndices[],
                                                    int bandOffsets[],
                                                    Point location) {
	DataBuffer d;
        int bands = bandOffsets.length;

        if (bankIndices == null) {
            throw new
                ArrayIndexOutOfBoundsException("Bank indices array is null");
        }
        if (bandOffsets == null) {
            throw new
                ArrayIndexOutOfBoundsException("Band offsets array is null");
        }

        // Figure out the #banks and the largest band offset
        int maxBank = bankIndices[0];
        int maxBandOff = bandOffsets[0];
        for (int i = 1; i < bands; i++) {
            if (bankIndices[i] > maxBank) {
                maxBank = bankIndices[i];
            }
            if (bandOffsets[i] > maxBandOff) {
                maxBandOff = bandOffsets[i];
            }
        }
        int banks = maxBank + 1;
        int size = maxBandOff + scanlineStride*(h-1) + (w-1) + 1;

        switch(dataType) {
        case DataBuffer.TYPE_BYTE:
            d = new DataBufferByte(size, banks);
            break;

        case DataBuffer.TYPE_USHORT:
            d = new DataBufferUShort(size, banks);
            break;

        case DataBuffer.TYPE_INT:
            d = new DataBufferInt(size, banks);
            break;

        default:
            throw new IllegalArgumentException("Unsupported data type " +
                                                dataType);
        }

        return createBandedRaster(d, w, h, scanlineStride,
                                  bankIndices, bandOffsets, location);
    }

    /**
     * Creates a Raster based on a SinglePixelPackedSampleModel with
     * the specified data type, width, height, and band masks.
     * The number of bands is inferred from bandMasks.length.
     * 
     * <p> The upper left corner of the Raster is given by the
     * location argument.  If location is null, (0, 0) will be used.
     * The dataType parameter should be one of the enumerated values
     * defined in the DataBuffer class.
     *
     * <p> The only dataTypes supported currently are TYPE_BYTE, TYPE_USHORT,
     * and TYPE_INT.
     * @throws IllegalArgumentException if <code>dataType</code> is not
     *         one of the supported data types, which are
     *         <code>DataBuffer.TYPE_BYTE</code>, 
     *         <code>DataBuffer.TYPE_USHORT</code> 
     *         or <code>DataBuffer.TYPE_INT</code>
     */
    public static WritableRaster createPackedRaster(int dataType,
                                                    int w, int h,
                                                    int bandMasks[],
                                                    Point location) {
        DataBuffer d;

        switch(dataType) {
        case DataBuffer.TYPE_BYTE:
            d = new DataBufferByte(w*h);
            break;

        case DataBuffer.TYPE_USHORT:
            d = new DataBufferUShort(w*h);
            break;

        case DataBuffer.TYPE_INT:
            d = new DataBufferInt(w*h);
            break;

        default:
            throw new IllegalArgumentException("Unsupported data type " +
                                                dataType);
        }

        return createPackedRaster(d, w, h, w, bandMasks, location);
    }

    /**
     * Creates a Raster based on a packed SampleModel with the
     * specified data type, width, height, number of bands, and bits
     * per band.  If the number of bands is one, the SampleModel will
     * be a MultiPixelPackedSampleModel.
     *
     * <p> If the number of bands is more than one, the SampleModel
     * will be a SinglePixelPackedSampleModel, with each band having
     * bitsPerBand bits.  In either case, the requirements on dataType
     * and bitsPerBand imposed by the corresponding SampleModel must
     * be met.
     *
     * <p> The upper left corner of the Raster is given by the
     * location argument.  If location is null, (0, 0) will be used.
     * The dataType parameter should be one of the enumerated values
     * defined in the DataBuffer class.
     *
     * <p> The only dataTypes supported currently are TYPE_BYTE, TYPE_USHORT,
     * and TYPE_INT.
     * @throws IllegalArgumentException if the product of 
     *         <code>bitsPerBand</code> and <code>bands</code> is 
     *         greater than the number of bits held by 
     *         <code>dataType</code>
     * @throws IllegalArgumentException if <code>bitsPerBand</code> or
     *         <code>bands</code> is not greater than zero  
     * @throws IllegalArgumentException if <code>dataType</code> is not
     *         one of the supported data types, which are
     *         <code>DataBuffer.TYPE_BYTE</code>, 
     *         <code>DataBuffer.TYPE_USHORT</code> 
     *         or <code>DataBuffer.TYPE_INT</code>     
     */
    public static WritableRaster createPackedRaster(int dataType,
                                                    int w, int h,
                                                    int bands,
                                                    int bitsPerBand,
                                                    Point location) {
        DataBuffer d;

        if (bands <= 0) {
            throw new IllegalArgumentException("Number of bands ("+bands+
                                               ") must be greater than 0");
        }

        if (bitsPerBand <= 0) {
            throw new IllegalArgumentException("Bits per band ("+bitsPerBand+
                                               ") must be greater than 0");
        }
        
        if (bands != 1) {
            int[] masks = new int[bands];
            int mask = (1 << bitsPerBand) - 1;
            int shift = (bands-1)*bitsPerBand;

            /* Make sure the total mask size will fit in the data type */
            if (shift+bitsPerBand > DataBuffer.getDataTypeSize(dataType)) {
                throw new IllegalArgumentException("bitsPerBand("+
                                                   bitsPerBand+") * bands is "+
                                                   " greater than data type "+
                                                   "size.");
            }
            switch(dataType) {
            case DataBuffer.TYPE_BYTE:
            case DataBuffer.TYPE_USHORT:
            case DataBuffer.TYPE_INT:
                break;
            default:
                throw new IllegalArgumentException("Unsupported data type " +
                                                    dataType);
            }

            for (int i = 0; i < bands; i++) {
                masks[i] = mask << shift;
                shift = shift - bitsPerBand;
            }

            return createPackedRaster(dataType, w, h, masks, location);
        }
        else {
            double fw = w;
            switch(dataType) {
            case DataBuffer.TYPE_BYTE:
                d = new DataBufferByte((int)(Math.ceil(fw/(8/bitsPerBand)))*h);
                break;

            case DataBuffer.TYPE_USHORT:
                d = new DataBufferUShort((int)(Math.ceil(fw/(16/bitsPerBand)))*h);
                break;

            case DataBuffer.TYPE_INT:
                d = new DataBufferInt((int)(Math.ceil(fw/(32/bitsPerBand)))*h);
                break;

            default:
                throw new IllegalArgumentException("Unsupported data type " +
                                                   dataType);
            }
            return createPackedRaster(d, w, h, bitsPerBand, location);
        }
    }

    /**
     * Creates a Raster based on a PixelInterleavedSampleModel with the
     * specified DataBuffer, width, height, scanline stride, pixel
     * stride, and band offsets.  The number of bands is inferred from
     * bandOffsets.length.  The upper left corner of the Raster
     * is given by the location argument.  If location is null, (0, 0)
     * will be used.
     * <p> Note that interleaved <code>DataBuffer.TYPE_INT</code>
     * Rasters are not supported.  To create a 1-band Raster of type
     * <code>DataBuffer.TYPE_INT</code>, use
     * Raster.createPackedRaster().
     * @throws IllegalArgumentException if <code>dataType</code> is not
     *         one of the supported data types, which are
     *         <code>DataBuffer.TYPE_BYTE</code>, 
     *         <code>DataBuffer.TYPE_USHORT</code>
     * @throws RasterFormatException if <code>dataBuffer</code> has more
     *         than one bank.
     */
    public static WritableRaster createInterleavedRaster(DataBuffer dataBuffer,
                                                         int w, int h,
                                                         int scanlineStride,
                                                         int pixelStride,
                                                         int bandOffsets[],
                                                         Point location) {
        if (location == null) {
            location = new Point(0, 0);
        }
        int dataType = dataBuffer.getDataType();

        PixelInterleavedSampleModel csm =
            new PixelInterleavedSampleModel(dataType, w, h,
                                            pixelStride,
                                            scanlineStride,
                                            bandOffsets);
        switch(dataType) {
        case DataBuffer.TYPE_BYTE:
            return new ByteInterleavedRaster(csm, dataBuffer, location);

        case DataBuffer.TYPE_USHORT:
            return new ShortInterleavedRaster(csm, dataBuffer, location);

        default:
            throw new IllegalArgumentException("Unsupported data type " +
                                                dataType);
        }
    }

    /**
     * Creates a Raster based on a BandedSampleModel with the
     * specified DataBuffer, width, height, scanline stride, bank
     * indices, and band offsets.  The number of bands is inferred
     * from bankIndices.length and bandOffsets.length, which must be
     * the same.  The upper left corner of the Raster is given by the
     * location argument.  If location is null, (0, 0) will be used.
     * @throws IllegalArgumentException if <code>dataType</code> is not
     *         one of the supported data types, which are
     *         <code>DataBuffer.TYPE_BYTE</code>, 
     *         <code>DataBuffer.TYPE_USHORT</code> 
     *         or <code>DataBuffer.TYPE_INT</code>
     */
    public static WritableRaster createBandedRaster(DataBuffer dataBuffer,
                                                    int w, int h,
                                                    int scanlineStride,
                                                    int bankIndices[],
                                                    int bandOffsets[],
                                                    Point location) {
        if (location == null) {
           location = new Point(0,0);
        }
        int dataType = dataBuffer.getDataType();

        int bands = bankIndices.length;
        if (bandOffsets.length != bands) {
            throw new IllegalArgumentException(
                                   "bankIndices.length != bandOffsets.length");
        }

        BandedSampleModel bsm =
            new BandedSampleModel(dataType, w, h,
                                  scanlineStride,
                                  bankIndices, bandOffsets);

        switch(dataType) {
        case DataBuffer.TYPE_BYTE:
            return new ByteBandedRaster(bsm, dataBuffer, location);

        case DataBuffer.TYPE_USHORT:
            return new ShortBandedRaster(bsm, dataBuffer, location);

        case DataBuffer.TYPE_INT:
            return new WritableRaster(bsm, dataBuffer, location);

        default:
            throw new IllegalArgumentException("Unsupported data type " +
                                                dataType);
        }
    }

    /**
     * Creates a Raster based on a SinglePixelPackedSampleModel with
     * the specified DataBuffer, width, height, scanline stride, and
     * band masks.  The number of bands is inferred from bandMasks.length.
     * The upper left corner of the Raster is given by
     * the location argument.  If location is null, (0, 0) will be used.
     * @throws IllegalArgumentException if <code>dataType</code> is not
     *         one of the supported data types, which are
     *         <code>DataBuffer.TYPE_BYTE</code>, 
     *         <code>DataBuffer.TYPE_USHORT</code> 
     *         or <code>DataBuffer.TYPE_INT</code>
     * @throws RasterFormatException if <code>dataBuffer</code> has more
     *         than one bank.
     */
    public static WritableRaster createPackedRaster(DataBuffer dataBuffer,
                                                    int w, int h,
                                                    int scanlineStride,
                                                    int bandMasks[],
                                                    Point location) {
        if (location == null) {
           location = new Point(0,0);
        }
        int dataType = dataBuffer.getDataType();

        SinglePixelPackedSampleModel sppsm =
            new SinglePixelPackedSampleModel(dataType, w, h, scanlineStride,
                                             bandMasks);

        switch(dataType) {
        case DataBuffer.TYPE_BYTE:
            return new ByteInterleavedRaster(sppsm, dataBuffer, location);

        case DataBuffer.TYPE_USHORT:
            return new ShortInterleavedRaster(sppsm, dataBuffer, location);

        case DataBuffer.TYPE_INT:
            return new IntegerInterleavedRaster(sppsm, dataBuffer, location);

        default:
            throw new IllegalArgumentException("Unsupported data type " +
                                                dataType);
        }
    }

    /**
     * Creates a Raster based on a MultiPixelPackedSampleModel with the
     * specified DataBuffer, width, height, and bits per pixel.  The upper
     * left corner of the Raster is given by the location argument.  If
     * location is null, (0, 0) will be used.
     * @throws IllegalArgumentException if <code>dataType</code> is not
     *         one of the supported data types, which are
     *         <code>DataBuffer.TYPE_BYTE</code>, 
     *         <code>DataBuffer.TYPE_USHORT</code> 
     *         or <code>DataBuffer.TYPE_INT</code>
     * @throws RasterFormatException if <code>dataBuffer</code> has more
     *         than one bank.
     */
    public static WritableRaster createPackedRaster(DataBuffer dataBuffer,
                                                    int w, int h,
                                                    int bitsPerPixel,
                                                    Point location) {
        if (location == null) {
           location = new Point(0,0);
        }
        int dataType = dataBuffer.getDataType();

        if (dataBuffer.getNumBanks() != 1) {
            throw new 
                RasterFormatException("DataBuffer for packed Rasters"+
                                      " must only have 1 bank.");
        }
        
        MultiPixelPackedSampleModel sbpsm =
                new MultiPixelPackedSampleModel(dataType, w, h, bitsPerPixel);

        switch(dataType) {
        case DataBuffer.TYPE_BYTE:
            return new BytePackedRaster(sbpsm, dataBuffer, location);

        case DataBuffer.TYPE_USHORT:
            return new WritableRaster(sbpsm, dataBuffer, location);

        case DataBuffer.TYPE_INT:
            return new WritableRaster(sbpsm, dataBuffer, location);

        default:
            throw new IllegalArgumentException("Unsupported data type " +
                                                dataType);
        }
    }


    /**
     *  Creates a Raster with the specified SampleModel and DataBuffer.
     *  The upper left corner of the Raster is given by the location argument.
     *  If location is null, (0, 0) will be used.
     * @throws RasterFormatException if <code>dataBuffer</code> has more
     *         than one bank and the <code>sampleModel</code> is
     *         PixelInterleavedSampleModel, SinglePixelPackedSampleModel,
     *         or MultiPixelPackedSampleModel.
     */
    public static Raster createRaster(SampleModel sm,
                                      DataBuffer db,
                                      Point location) {
        if (db == null) {
            throw new NullPointerException("DataBuffer cannot be null");
        }
        
        if (location == null) {
           location = new Point(0,0);
        }
        int dataType = sm.getDataType();

        if (sm instanceof PixelInterleavedSampleModel) {
            switch(dataType) {
                case DataBuffer.TYPE_BYTE:
                    return new ByteInterleavedRaster(sm, db, location);

                case DataBuffer.TYPE_USHORT:
                    return new ShortInterleavedRaster(sm, db, location);
            }
        } else if (sm instanceof SinglePixelPackedSampleModel) {
            switch(dataType) {
                case DataBuffer.TYPE_BYTE:
                    return new ByteInterleavedRaster(sm, db, location);

                case DataBuffer.TYPE_USHORT:
                    return new ShortInterleavedRaster(sm, db, location);

                case DataBuffer.TYPE_INT:
                    return new IntegerInterleavedRaster(sm, db, location);
            }
        } else if (sm instanceof MultiPixelPackedSampleModel &&
                   dataType == DataBuffer.TYPE_BYTE) {
            return new BytePackedRaster(sm, db, location);
        }

        // we couldn't do anything special - do the generic thing

        return new Raster(sm,db,location);
    }

    /**
     *  Creates a WritableRaster with the specified SampleModel.
     *  The upper left corner of the Raster is given by the location argument.
     *  If location is null, (0, 0) will be used.
     * <p> The only dataTypes supported currently are TYPE_BYTE, TYPE_USHORT,
     * and TYPE_INT.
     */
    public static WritableRaster createWritableRaster(SampleModel sm,
                                                      Point location) {
        if (location == null) {
           location = new Point(0,0);
        }

        return createWritableRaster(sm,
                                    sm.createDataBuffer(),
                                    location);
    }

    /**
     *  Creates a WritableRaster with the specified SampleModel and DataBuffer.
     *  The upper left corner of the Raster is given by the location argument.
     *  If location is null, (0, 0) will be used.
     * <p> The only dataTypes supported currently are TYPE_BYTE, TYPE_USHORT,
     * and TYPE_INT.
     * @throws RasterFormatException if <code>dataBuffer</code> has more
     *         than one bank and the <code>sampleModel</code> is
     *         PixelInterleavedSampleModel, SinglePixelPackedSampleModel,
     *         or MultiPixelPackedSampleModel.
     */
    public static WritableRaster createWritableRaster(SampleModel sm,
                                                      DataBuffer db,
                                                      Point location) {
        if (db == null) {
            throw new NullPointerException("DataBuffer cannot be null");
        }
        if (location == null) {
           location = new Point(0,0);
        }

        int dataType = sm.getDataType();

        if (sm instanceof PixelInterleavedSampleModel) {
            switch(dataType) {
                case DataBuffer.TYPE_BYTE:
                    return new ByteInterleavedRaster(sm, db, location);

                case DataBuffer.TYPE_USHORT:
                    return new ShortInterleavedRaster(sm, db, location);
            }
        } else if (sm instanceof SinglePixelPackedSampleModel) {
            switch(dataType) {
                case DataBuffer.TYPE_BYTE:
                    return new ByteInterleavedRaster(sm, db, location);

                case DataBuffer.TYPE_USHORT:
                    return new ShortInterleavedRaster(sm, db, location);

                case DataBuffer.TYPE_INT:
                    return new IntegerInterleavedRaster(sm, db, location);
            }
        } else if (sm instanceof MultiPixelPackedSampleModel &&
                   dataType == DataBuffer.TYPE_BYTE) {
            return new BytePackedRaster(sm, db, location);
        }

        // we couldn't do anything special - do the generic thing

        return new WritableRaster(sm,db,location);
    }

    /**
     *  Constructs a Raster with the given SampleModel.  The Raster's
     *  upper left corner is origin and it is the same size as the
     *  SampleModel.  A DataBuffer large enough to describe the
     *  Raster is automatically created.
     *  @param sampleModel     The SampleModel that specifies the layout.
     *  @param origin          The Point that specified the origin.
     */
    protected Raster(SampleModel sampleModel,
                     Point origin) {
	this(sampleModel,
	     sampleModel.createDataBuffer(),
	     new Rectangle(origin.x,
                           origin.y,
                           sampleModel.getWidth(),
                           sampleModel.getHeight()),
             origin,
             null);
    }

    /**
     *  Constructs a Raster with the given SampleModel and DataBuffer.
     *  The Raster's upper left corner is origin and it is the same size
     *  as the SampleModel.  The DataBuffer is not initialized and must
     *  be compatible with SampleModel.
     *  @param sampleModel     The SampleModel that specifies the layout.
     *  @param dataBuffer      The DataBuffer that contains the image data.
     *  @param origin          The Point that specifies the origin.
     */
    protected Raster(SampleModel sampleModel,
                     DataBuffer dataBuffer,
                     Point origin) {
	this(sampleModel,
             dataBuffer,
             new Rectangle(origin.x,
                           origin.y,
                           sampleModel.getWidth(),
                           sampleModel.getHeight()),
             origin,
             null);
    }

    /**
     * Constructs a Raster with the given SampleModel, DataBuffer, and
     * parent.  aRegion specifies the bounding rectangle of the new
     * Raster.  When translated into the base Raster's coordinate
     * system, aRegion must be contained by the base Raster.
     * (The base Raster is the Raster's ancestor which has no parent.)
     * sampleModelTranslate specifies the sampleModelTranslateX and
     * sampleModelTranslateY values of the new Raster.
     *
     * Note that this constructor should generally be called by other
     * constructors or create methods, it should not be used directly.
     * @param sampleModel     The SampleModel that specifies the layout.
     * @param dataBuffer      The DataBuffer that contains the image data.
     * @param aRegion         The Rectangle that specifies the image area.     
     * @param sampleModelTranslate  The Point that specifies the translation
     *                        from SampleModel to Raster coordinates.
     * @param parent          The parent (if any) of this raster.
     */
    protected Raster(SampleModel sampleModel,
                     DataBuffer dataBuffer,
                     Rectangle aRegion,
                     Point sampleModelTranslate,
	             Raster parent) {
        
        if (dataBuffer == null) {
            throw new NullPointerException("DataBuffer cannot be null");
        }
       this.sampleModel = sampleModel;
       this.dataBuffer = dataBuffer;
       minX = aRegion.x;
       minY = aRegion.y;
       width = aRegion.width;
       height = aRegion.height;
 
       sampleModelTranslateX = sampleModelTranslate.x;
       sampleModelTranslateY = sampleModelTranslate.y;

       numBands = sampleModel.getNumBands();
       numDataElements = sampleModel.getNumDataElements();
       this.parent = parent;
    }


    /** Returns the parent Raster (if any) of this Raster, or else null. */
    public Raster getParent() {
        return parent;
    }

    /**
     * Returns the X translation from the coordinate system of the
     * SampleModel to that of the Raster.  To convert a pixel's X
     * coordinate from the Raster coordinate system to the SampleModel
     * coordinate system, this value must be subtracted.
     */
    final public int getSampleModelTranslateX() {
	return sampleModelTranslateX;
    }

    /**
     * Returns the Y translation from the coordinate system of the
     * SampleModel to that of the Raster.  To convert a pixel's Y
     * coordinate from the Raster coordinate system to the SampleModel
     * coordinate system, this value must be subtracted.
     */
    final public int getSampleModelTranslateY() {
	return sampleModelTranslateY;
    }

    /**
     * Create a compatible WritableRaster the same size as this Raster with
     * the same SampleModel and a new initialized DataBuffer.
     */
    public WritableRaster createCompatibleWritableRaster() {
        return new WritableRaster(sampleModel, new Point(0,0));
    }

    /**
     * Create a compatible WritableRaster with the specified size, a new
     * SampleModel, and a new initialized DataBuffer.
     * @exception RasterFormatException if the width or height is less than
     *                               or equal to zero.
     */
    public WritableRaster createCompatibleWritableRaster(int w, int h) {
        if (w <= 0 || h <=0) {
            throw new RasterFormatException("negative " +
                                          ((w <= 0) ? "width" : "height"));
        }

        SampleModel sm = sampleModel.createCompatibleSampleModel(w,h);

        return new WritableRaster(sm, new Point(0,0));
    }

    /**
     * Create a compatible WritableRaster with location (minX, minY)
     * and size (width, height) specified by rect, a
     * new SampleModel, and a new initialized DataBuffer.
     */
    public WritableRaster createCompatibleWritableRaster(Rectangle rect) {
        return createCompatibleWritableRaster(rect.x, rect.y,
                                              rect.width, rect.height);
    }

    /**
     * Create a compatible WritableRaster with the specified
     * location (minX, minY) and size (width, height), a
     * new SampleModel, and a new initialized DataBuffer.
     */
    public WritableRaster createCompatibleWritableRaster(int x, int y,
                                                         int w, int h) {
        WritableRaster ret = createCompatibleWritableRaster(w, h);
        return ret.createWritableChild(0,0,w,h,x,y,null);
    }

    /**
     * Create a Raster with the same size, SampleModel and DataBuffer
     * as this one, but with a different location.  The new Raster
     * will possess a reference to the current Raster, accessible
     * through its getParent() method.
     * 
     * @param childMinX X coord of the upper left corner of the new Raster.
     * @param childMinY Y coord of the upper left corner of the new Raster.
     */
    public Raster createTranslatedChild(int childMinX, int childMinY) {
        return createChild(minX,minY,width,height,
                           childMinX,childMinY,null);
    }

    /**
     * Returns a new Raster which shares all or part of this Raster's
     * DataBuffer.  The new Raster will possess a reference to the
     * current Raster, accessible through its getParent() method.
     *
     * <p> The parentX, parentY, width and height parameters
     * form a Rectangle in this Raster's coordinate space,
     * indicating the area of pixels to be shared.  An error will
     * be thrown if this Rectangle is not contained with the bounds
     * of the current Raster.
     *
     * <p> The new Raster may additionally be translated to a
     * different coordinate system for the plane than that used by the current
     * Raster.  The childMinX and childMinY parameters give the new
     * (x, y) coordinate of the upper-left pixel of the returned
     * Raster; the coordinate (childMinX, childMinY) in the new Raster
     * will map to the same pixel as the coordinate (parentX, parentY)
     * in the current Raster.
     *
     * <p> The new Raster may be defined to contain only a subset of
     * the bands of the current Raster, possibly reordered, by means
     * of the bandList parameter.  If bandList is null, it is taken to
     * include all of the bands of the current Raster in their current
     * order.
     *
     * <p> To create a new Raster that contains a subregion of the current
     * Raster, but shares its coordinate system and bands,
     * this method should be called with childMinX equal to parentX,
     * childMinY equal to parentY, and bandList equal to null.
     *
     * @param parentX    X coordinate of the upper left corner in this Raster's
     *                   coordinates.
     * @param parentY    Y coordinate of the upper left corner in this Raster's
     *                   coordinates.
     * @param width      Width of the region starting at (parentX, parentY).
     * @param height     Height of the region starting at (parentX, parentY).
     * @param childMinX  X coordinate of the upper left corner of
     *                   the returned Raster.
     * @param childMinY  Y coordinate of the upper left corner of
     *                   the returned Raster.
     * @param bandList   Array of band indices, or null to use all bands.
     * @exception RasterFormatException if the specified subregion is outside
     *                               of the raster bounds.
     */
    public Raster createChild(int parentX, int parentY,
                              int width, int height,
                              int childMinX, int childMinY,
                              int bandList[]) {
        if (parentX < this.minX) {
            throw new RasterFormatException("parentX lies outside raster");
        }
        if (parentY < this.minY) {
            throw new RasterFormatException("parentY lies outside raster");
        }
        if (parentX + width > this.width + this.minX) {
            throw new RasterFormatException("(parentX + width) is outside raster");
        }
        if (parentY + height > this.height + this.minY) {
            throw new RasterFormatException("(parentY + height) is outside raster");
        }

        SampleModel subSampleModel;
        if (bandList == null) {
            subSampleModel = sampleModel;
        } else {
            subSampleModel =
                sampleModel.createCompatibleSampleModel(width, height);
            subSampleModel =
                subSampleModel.createSubsetSampleModel(bandList);
        }

        int deltaX = childMinX - parentX;
        int deltaY = childMinY - parentY;

	return new Raster(subSampleModel, dataBuffer,
			  new Rectangle(childMinX, childMinY, width, height),
			  new Point(sampleModelTranslateX + deltaX,
				    sampleModelTranslateY + deltaY), this);
    }

    /**
     * Returns the bounding Rectangle of this Raster. This function returns
     * the same information as getMinX/MinY/Width/Height.
     */
    public Rectangle getBounds() {
        return new Rectangle(minX, minY, width, height);
    }

    /** Returns the minimum valid X coordinate of the Raster. */
    final public int getMinX() {
        return minX;
    }

    /** Returns the minimum valid Y coordinate of the Raster. */
    final public int getMinY() {
        return minY;
    }

    /** Returns the width in pixels of the Raster.  */
    final public int getWidth() {
        return width;
    }

    /** Returns the height in pixels of the Raster.  */
    final public int getHeight() {
        return height;
    }

    /** Returns the number of bands (samples per pixel) in this Raster. */
    final public int getNumBands() {
        return numBands;
    }

    /** 
     *  Returns the number of data elements needed to transfer one pixel
     *  via the getDataElements and setDataElements methods.  When pixels
     *  are transferred via these methods, they may be transferred in a
     *  packed or unpacked format, depending on the implementation of the
     *  underlying SampleModel.  Using these methods, pixels are transferred
     *  as an array of getNumDataElements() elements of a primitive type given
     *  by getTransferType().  The TransferType may or may not be the same
     *  as the storage data type of the DataBuffer.
     */
    final public int getNumDataElements() {
        return sampleModel.getNumDataElements();
    }

    /** 
     *  Returns the TransferType used to transfer pixels via the
     *  getDataElements and setDataElements methods.  When pixels
     *  are transferred via these methods, they may be transferred in a
     *  packed or unpacked format, depending on the implementation of the
     *  underlying SampleModel.  Using these methods, pixels are transferred
     *  as an array of getNumDataElements() elements of a primitive type given
     *  by getTransferType().  The TransferType may or may not be the same
     *  as the storage data type of the DataBuffer.  The TransferType will
     *  be one of the types defined in DataBuffer.
     */
    final public int getTransferType() {
        return sampleModel.getTransferType();
    }

    /** Returns the DataBuffer associated with this Raster. */
    public DataBuffer getDataBuffer() {
        return dataBuffer;
    }

    /** Returns the SampleModel that describes the layout of the image data. */
    public SampleModel getSampleModel() {
        return sampleModel;
    }

    /**
     * Returns data for a single pixel in a primitive array of type
     * TransferType.  For image data supported by the Java 2D(tm) API, 
     * this will be one of DataBuffer.TYPE_BYTE, DataBuffer.TYPE_USHORT, 
     * or DataBuffer.TYPE_INT.  Data may be returned in a packed format,
     * thus increasing efficiency for data transfers.
     * There will be no explicit bounds checking on the parameters.
     * An ArrayIndexOutOfBoundsException may be thrown
     * if the coordinates are not in bounds.
     * A ClassCastException will be thrown if the input object is non null
     * and references anything other than an array of TransferType.
     * @see java.awt.image.SampleModel#getDataElements(int, int, Object, DataBuffer)
     * @param x        The X coordinate of the pixel location.
     * @param y        The Y coordinate of the pixel location.
     * @param outData  An object reference to an array of type defined by
     *                 getTransferType() and length getNumDataElements().  
     *                 If null, an array of appropriate type and size will be
     *                 allocated.
     * @return         An object reference to an array of type defined by
     *                 getTransferType() with the requested pixel data.
     */
    public Object getDataElements(int x, int y, Object outData) {
        return sampleModel.getDataElements(x - sampleModelTranslateX,
                                           y - sampleModelTranslateY,
                                           outData, dataBuffer);
    }

    /**
     * Returns the pixel data for the specified rectangle of pixels in a
     * primitive array of type TransferType.
     * For image data supported by the Java 2D API, this
     * will be one of DataBuffer.TYPE_BYTE, DataBuffer.TYPE_USHORT, or
     * DataBuffer.TYPE_INT.  Data may be returned in a packed format,
     * thus increasing efficiency for data transfers.
     * There will be no explicit bounds checking on the parameters.
     * An ArrayIndexOutOfBoundsException may be thrown
     * if the coordinates are not in bounds.
     * A ClassCastException will be thrown if the input object is non null
     * and references anything other than an array of TransferType.
     * @see java.awt.image.SampleModel#getDataElements(int, int, int, int, Object, DataBuffer)
     * @param x        The X coordinate of the upper left pixel location.
     * @param y        The Y coordinate of the upper left pixel location.
     * @param width    Width of the pixel rectangle.
     * @param height   Height of the pixel rectangle.
     * @param outData  An object reference to an array of type defined by
     *                 getTransferType() and length w*h*getNumDataElements().  
     *                 If null, an array of appropriate type and size will be
     *                 allocated.
     * @return         An object reference to an array of type defined by
     *                 getTransferType() with the requested pixel data.
     */
    public Object getDataElements(int x, int y, int w, int h, Object outData) {
        return sampleModel.getDataElements(x - sampleModelTranslateX,
                                           y - sampleModelTranslateY,
                                           w, h, outData, dataBuffer);
    }

    /**
     * Returns the samples in an array of int for the specified pixel.
     * An ArrayIndexOutOfBoundsException may be thrown
     * if the coordinates are not in bounds.
     * @param x    The X coordinate of the pixel location. 
     * @param y    The Y coordinate of the pixel location.
     * @param iArray An optionally preallocated int array.
     */
    public int[] getPixel(int x, int y, int iArray[]) {
        return sampleModel.getPixel(x - sampleModelTranslateX,
				    y - sampleModelTranslateY,
                                    iArray, dataBuffer);
    }

    /**
     * Returns the samples in an array of float for the 
     * specified pixel.
     * An ArrayIndexOutOfBoundsException may be thrown
     * if the coordinates are not in bounds.
     * @param x    The X coordinate of the pixel location.
     * @param y    The Y coordinate of the pixel location.
     * @param fArray An optionally preallocated float array.
     */
    public float[] getPixel(int x, int y, float fArray[]) {
        return sampleModel.getPixel(x - sampleModelTranslateX,
				    y - sampleModelTranslateY,
                                    fArray, dataBuffer);
    }
  
    /** 
     * Returns the samples in an array of double for the specified pixel.
     * An ArrayIndexOutOfBoundsException may be thrown
     * if the coordinates are not in bounds.
     * @param x      The X coordinate of the pixel location.
     * @param y      The Y coordinate of the pixel location.
     * @param dArray An optionally preallocated double array.
     */
    public double[] getPixel(int x, int y, double dArray[]) {
        return sampleModel.getPixel(x - sampleModelTranslateX,
				    y - sampleModelTranslateY,
				    dArray, dataBuffer);
    }
  
    /** 
     * Returns an int array containing all samples for a rectangle of pixels,
     * one sample per array element.
     * An ArrayIndexOutOfBoundsException may be thrown
     * if the coordinates are not in bounds.
     * @param x      The X coordinate of the upper left pixel location.
     * @param y      The Y coordinate of the upper left pixel location.
     * @param w      Width of the pixel rectangle.
     * @param h      Height of the pixel rectangle.
     * @param iArray An optionally pre-allocated int array.
     */
    public int[] getPixels(int x, int y, int w, int h, int iArray[]) {
        return sampleModel.getPixels(x - sampleModelTranslateX,
                                     y - sampleModelTranslateY, w, h,
                                     iArray, dataBuffer);
    }

    /**
     * Returns a float array containing all samples for a rectangle of pixels,
     * one sample per array element.
     * An ArrayIndexOutOfBoundsException may be thrown
     * if the coordinates are not in bounds.
     * @param x        The X coordinate of the upper left pixel location.
     * @param y        The Y coordinate of the upper left pixel location.
     * @param w        Width of the pixel rectangle.
     * @param h        Height of the pixel rectangle.
     * @param fArray   An optionally pre-allocated float array.
     */
    public float[] getPixels(int x, int y, int w, int h,
                             float fArray[]) {
        return sampleModel.getPixels(x - sampleModelTranslateX,
                                     y - sampleModelTranslateY, w, h,
                                     fArray, dataBuffer);
    }

    /**
     * Returns a double array containing all samples for a rectangle of pixels,
     * one sample per array element.
     * An ArrayIndexOutOfBoundsException may be thrown
     * if the coordinates are not in bounds.
     * @param x        The X coordinate of the upper left pixel location.
     * @param y        The Y coordinate of the upper left pixel location.
     * @param w        Width of the pixel rectangle.
     * @param h        Height of the pixel rectangle.
     * @param dArray   An optionally pre-allocated double array.
     */
    public double[] getPixels(int x, int y, int w, int h,
                              double dArray[]) {
        return sampleModel.getPixels(x - sampleModelTranslateX,
                                     y - sampleModelTranslateY,
                                     w, h, dArray, dataBuffer);
    }
  
  
    /** 
     * Returns the sample in a specified band for the pixel located
     * at (x,y) as an int.
     * An ArrayIndexOutOfBoundsException may be thrown
     * if the coordinates are not in bounds.
     * @param x        The X coordinate of the pixel location.
     * @param y        The Y coordinate of the pixel location.
     * @param b        The band to return.
     */
    public int getSample(int x, int y, int b) {
        return sampleModel.getSample(x - sampleModelTranslateX,
				     y - sampleModelTranslateY, b,
				     dataBuffer);
    }
    
    /** 
     * Returns the sample in a specified band
     * for the pixel located at (x,y) as a float.
     * An ArrayIndexOutOfBoundsException may be thrown
     * if the coordinates are not in bounds.
     * @param x        The X coordinate of the pixel location.
     * @param y        The Y coordinate of the pixel location.
     * @param b        The band to return.
     */
    public float getSampleFloat(int x, int y, int b) {
        return sampleModel.getSampleFloat(x - sampleModelTranslateX,
					  y - sampleModelTranslateY, b,
					  dataBuffer);
    }
  
    /** 
     * Returns the sample in a specified band
     * for a pixel located at (x,y) as a double.
     * An ArrayIndexOutOfBoundsException may be thrown
     * if the coordinates are not in bounds.
     * @param x        The X coordinate of the pixel location.
     * @param y        The Y coordinate of the pixel location.
     * @param b        The band to return.
     */
    public double getSampleDouble(int x, int y, int b) {
        return sampleModel.getSampleDouble(x - sampleModelTranslateX,
					   y - sampleModelTranslateY,
					   b, dataBuffer);
    }
  
    /** 
     * Returns the samples for a specified band for the specified rectangle
     * of pixels in an int array, one sample per array element.
     * An ArrayIndexOutOfBoundsException may be thrown
     * if the coordinates are not in bounds.
     * @param x        The X coordinate of the upper left pixel location.
     * @param y        The Y coordinate of the upper left pixel location.
     * @param w        Width of the pixel rectangle.
     * @param h        Height of the pixel rectangle.
     * @param b        The band to return.
     * @param iArray   An optionally pre-allocated int array.
     */
    public int[] getSamples(int x, int y, int w, int h, int b,
                            int iArray[]) {
        return sampleModel.getSamples(x - sampleModelTranslateX,
                                      y - sampleModelTranslateY,
                                      w, h, b, iArray,
                                      dataBuffer);
    }

    /**
     * Returns the samples for a specified band for the specified rectangle
     * of pixels in a float array, one sample per array element.
     * An ArrayIndexOutOfBoundsException may be thrown
     * if the coordinates are not in bounds.
     * @param x        The X coordinate of the upper left pixel location.
     * @param y        The Y coordinate of the upper left pixel location.
     * @param w        Width of the pixel rectangle.
     * @param h        Height of the pixel rectangle.
     * @param b        The band to return.
     * @param fArray   An optionally pre-allocated float array.
     */
    public float[] getSamples(int x, int y, int w, int h, int b,
                              float fArray[]) {
        return sampleModel.getSamples(x - sampleModelTranslateX,
                                      y - sampleModelTranslateY,
                                      w, h, b, fArray, dataBuffer);
    }

    /**
     * Returns the samples for a specified band for a specified rectangle
     * of pixels in a double array, one sample per array element.
     * An ArrayIndexOutOfBoundsException may be thrown
     * if the coordinates are not in bounds.
     * @param x        The X coordinate of the upper left pixel location.
     * @param y        The Y coordinate of the upper left pixel location.
     * @param w        Width of the pixel rectangle.
     * @param h        Height of the pixel rectangle.
     * @param b        The band to return.
     * @param dArray   An optionally pre-allocated double array.
     */
    public double[] getSamples(int x, int y, int w, int h, int b,
                               double dArray[]) {
         return sampleModel.getSamples(x - sampleModelTranslateX,
                                       y - sampleModelTranslateY,
                                       w, h, b, dArray, dataBuffer);
    }

}
