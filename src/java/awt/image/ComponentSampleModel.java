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

/**
 *  This class represents image data which is stored such that each sample
 *  of a pixel occupies one data element of the DataBuffer.  It stores the
 *  N samples which make up a pixel in N separate data array elements.
 *  Different bands may be in different banks of the DataBuffer.
 *  Accessor methods are provided so that image data can be manipulated
 *  directly. This class can support different kinds of interleaving, e.g.
 *  band interleaving, scanline interleaving, and pixel interleaving.
 *  Pixel stride is the number of data array elements between two samples
 *  for the same band on the same scanline. Scanline stride is the number
 *  of data array elements between a given sample and the corresponding sample 
 *  in the same column of the next scanline.  Band offsets denote the number
 *  of data array elements from the first data array element of the bank
 *  of the DataBuffer holding each band to the first sample of the band.
 *  The bands are numbered from 0 to N-1.  This class can represent image
 *  data for which each sample is an integral number which can be
 *  stored in 8, 16, or 32 bits (all samples of a given ComponentSampleModel
 *  are stored with the same precision).  All strides and offsets must be
 *  non-negative.  This class supports 
 *  {@link DataBuffer#TYPE_BYTE TYPE_BYTE}, 
 *  {@link DataBuffer#TYPE_USHORT TYPE_USHORT}, 
 *  {@link DataBuffer#TYPE_SHORT TYPE_SHORT}, 
 *  {@link DataBuffer#TYPE_INT TYPE_INT}, 
 *  @see java.awt.image.PixelInterleavedSampleModel
 *  @see java.awt.image.BandedSampleModel
 */

public class ComponentSampleModel extends SampleModel
{
    /** Offsets for all bands in data array elements. */
    protected int bandOffsets[];

    /** Index for each bank storing a band of image data. */
    protected int[] bankIndices;

    /** 
     * The number of bands in this 
     * <code>ComponentSampleModel</code>.
     */
    protected int numBands = 1;

    /**
     * The number of banks in this 
     * <code>ComponentSampleModel</code>.
     */
    protected int numBanks = 1;

    /**
     *  Line stride (in data array elements) of the region of image
     *  data described by this ComponentSampleModel.
     */
    protected int scanlineStride;

    /** Pixel stride (in data array elements) of the region of image
     *  data described by this ComponentSampleModel.
     */
    protected int pixelStride;

    static private native void initIDs();
    static {
        ColorModel.loadLibraries();
        initIDs();
    }

    /**
     * Constructs a ComponentSampleModel with the specified parameters.
     * The number of bands will be given by the length of the bandOffsets array.
     * All bands will be stored in the first bank of the DataBuffer.
     * @param dataType 	The data type for storing samples.
     * @param w 	The width (in pixels) of the region of
     * image data described.
     * @param h 	The height (in pixels) of the region of
     * image data described.
     * @param pixelStride The pixel stride of the region of image
     * data described.
     * @param scanlineStride The line stride of the region of image
     * data described.
     * @param bandOffsets The offsets of all bands.
     * @throws IllegalArgumentException if <code>w</code> or
     *         <code>h</code> is not greater than 0
     * @throws IllegalArgumentException if <code>pixelStride</code>
     *         is less than 0
     * @throws IllegalArgumentException if <code>scanlineStride</code>
     *         is less than 0
     * @throws IllegalArgumentException if <code>numBands</code>
     *         is less than 1
     * @throws IllegalArgumentException if the product of <code>w</code>
     *         and <code>h</code> is greater than
     *         <code>Integer.MAX_VALUE</code>
     * @throws IllegalArgumentException if <code>dataType</code> is not
     *         one of the supported data types
     */
    public ComponentSampleModel(int dataType,
                                int w, int h,
                                int pixelStride,
                                int scanlineStride,
                                int bandOffsets[]) {
	super(dataType, w, h, bandOffsets.length);
	this.dataType = dataType;
	this.pixelStride = pixelStride;
	this.scanlineStride  = scanlineStride;
	this.bandOffsets = (int[])bandOffsets.clone();
        numBands = bandOffsets.length;
        if (pixelStride < 0) {
            throw new IllegalArgumentException("Pixel stride must be >= 0");
        }
        if (scanlineStride < 0) {
            throw new IllegalArgumentException("Scanline stride must be >= 0");
        }
        if (numBands < 1) {
            throw new IllegalArgumentException("Must have at least one band.");
        }
	bankIndices = new int[numBands];
	for (int i=0; i<numBands; i++) {
	    bankIndices[i] = 0;
        }
    }


    /**
     * Constructs a ComponentSampleModel with the specified parameters.
     * The number of bands will be given by the length of the bandOffsets array.
     * Different bands may be stored in different banks of the DataBuffer.
     * @param dataType 	The data type for storing samples.
     * @param w 	The width (in pixels) of the region of
     * image data described. 
     * @param h 	The height (in pixels) of the region of
     * image data described.
     * @param pixelStride The pixel stride of the region of image
     * data described.	
     * @param scanlineStride The line stride of the region of image
     * data described. 
     * @param bandIndices The bank indices of all bands. 
     * @param bandOffsets The band offsets of all bands. 
     * @throws IllegalArgumentException if <code>w</code> or
     *         <code>h</code> is not greater than 0
     * @throws IllegalArgumentException if <code>pixelStride</code>
     *         is less than 0
     * @throws IllegalArgumentException if <code>scanlineStride</code>
     *         is less than 0
     * @throws IllegalArgumentException if the length of 
     *         <code>bankIndices</code> does not equal the length of 
     *         <code>bankOffsets</code>
     * @throws IllegalArgumentException if any of the bank indices 
     *         of <code>bandIndices</code> is less than 0
     */
    public ComponentSampleModel(int dataType,
                                int w, int h,
                                int pixelStride,
                                int scanlineStride,
                                int bankIndices[],
                                int bandOffsets[]) {
        super(dataType, w, h, bandOffsets.length);
	this.dataType = dataType;
	this.pixelStride = pixelStride;
	this.scanlineStride  = scanlineStride;
	this.bandOffsets = (int[])bandOffsets.clone();
        this.bankIndices = (int[]) bankIndices.clone();
        if (pixelStride < 0) {
            throw new IllegalArgumentException("Pixel stride must be >= 0");
        }
        if (scanlineStride < 0) {
            throw new IllegalArgumentException("Scanline stride must be >= 0");
        }
        int maxBank = bankIndices[0];
        if (maxBank < 0) {
            throw new IllegalArgumentException("Index of bank 0 is less than "+
                                               "0 ("+maxBank+")");
        }
        for (int i=1; i < bankIndices.length; i++) {
            if (bankIndices[i] > maxBank) {
                maxBank = bankIndices[i];
            }
            else if (bankIndices[i] < 0) {
                throw new IllegalArgumentException("Index of bank "+i+
                                                   " is less than 0 ("+
                                                   maxBank+")");
            }
        }
        numBanks         = maxBank+1;
        numBands         = bandOffsets.length;
        if (bandOffsets.length != bankIndices.length) {
            throw new IllegalArgumentException("Length of bandOffsets must "+
                                               "equal length of bankIndices.");
        }
    }

    /**
     * Returns the size of the data buffer (in data elements) needed
     * for a data buffer that matches this ComponentSampleModel.
     */
     private long getBufferSize() {
         int maxBandOff=bandOffsets[0];
         for (int i=1; i<bandOffsets.length; i++)
             maxBandOff = Math.max(maxBandOff,bandOffsets[i]);

         long size = 0;
         if (maxBandOff >= 0)
             size += maxBandOff+1;
         if (pixelStride > 0)
             size += pixelStride * (width-1);
         if (scanlineStride > 0)
             size += scanlineStride*(height-1);
         return size;
     }

     /**
      * Preserves band ordering with new step factor...
      */
    int []orderBands(int orig[], int step) {
        int map[] = new int[orig.length];
        int ret[] = new int[orig.length];

        for (int i=0; i<map.length; i++) map[i] = i;

        for (int i = 0; i < ret.length; i++) {
            int index = i;
            for (int j = i+1; j < ret.length; j++) {
                if (orig[map[index]] > orig[map[j]]) {
                    index = j;
                }
            }
            ret[map[index]] = i*step;
            map[index]  = map[i];
        }
        return ret;
    }

    /**
     * Creates a new ComponentSampleModel with the specified
     * width and height.  The new SampleModel will have the same
     * number of bands, storage data type, interleaving scheme, and
     * pixel stride as this SampleModel.
     * @param w the width of the resulting <code>SampleModel</code>
     * @param h the height of the resulting <code>SampleModel</code>
     * @throws IllegalArgumentException if <code>w</code> or
     *         <code>h</code> is not greater than 0
     */
    public SampleModel createCompatibleSampleModel(int w, int h) {
        SampleModel ret=null;
        long size;
        int minBandOff=bandOffsets[0];
        int maxBandOff=bandOffsets[0];
        for (int i=1; i<bandOffsets.length; i++) {
            minBandOff = Math.min(minBandOff,bandOffsets[i]);
            maxBandOff = Math.max(maxBandOff,bandOffsets[i]);
        }
        maxBandOff -= minBandOff;

        int bands   = bandOffsets.length;
        int bandOff[];
        int pStride = Math.abs(pixelStride);
        int lStride = Math.abs(scanlineStride);
        int bStride = Math.abs(maxBandOff);

        if (pStride > lStride) {
            if (pStride > bStride) {
                if (lStride > bStride) { // pix > line > band
                    bandOff = new int[bandOffsets.length];
                    for (int i=0; i<bands; i++)
                        bandOff[i] = bandOffsets[i]-minBandOff;
                    lStride = bStride+1;
                    pStride = lStride*h;
                } else { // pix > band > line
                    bandOff = orderBands(bandOffsets,lStride*h);
                    pStride = bands*lStride*h;
                }
            } else { // band > pix > line
                pStride = lStride*h;
                bandOff = orderBands(bandOffsets,pStride*w);
            }
        } else {
            if (pStride > bStride) { // line > pix > band
                bandOff = new int[bandOffsets.length];
                for (int i=0; i<bands; i++)
                    bandOff[i] = bandOffsets[i]-minBandOff;
                pStride = bStride+1;
                lStride = pStride*w;
            } else {
                if (lStride > bStride) { // line > band > pix
                    bandOff = orderBands(bandOffsets,pStride*w);
                    lStride = bands*pStride*w;
                } else { // band > line > pix
                    lStride = pStride*w;
                    bandOff = orderBands(bandOffsets,lStride*h);
                }
            }
        }

        // make sure we make room for negative offsets...
        int base = 0;
        if (scanlineStride < 0) {
            base += lStride*h;
            lStride *= -1;
        }
        if (pixelStride    < 0) {
            base += pStride*w;
            pStride *= -1;
        }

        for (int i=0; i<bands; i++)
            bandOff[i] += base;
        return new ComponentSampleModel(dataType, w, h, pStride,
                                        lStride, bandOff);
    }

    /**
     * This creates a new ComponentSampleModel with a subset of the bands
     * of this ComponentSampleModel.  The new ComponentSampleModel can be
     * used with any DataBuffer that the existing ComponentSampleModel
     * can be used with.  The new ComponentSampleModel/DataBuffer
     * combination will represent an image with a subset of the bands
     * of the original ComponentSampleModel/DataBuffer combination.
     */
    public SampleModel createSubsetSampleModel(int bands[]) {
       if (bands.length > bankIndices.length)
            throw new RasterFormatException("There are only " +
                                            bankIndices.length +
                                            " bands");
        int newBankIndices[] = new int[bands.length];
        int newBandOffsets[] = new int[bands.length];

        for (int i=0; i<bands.length; i++) {
            newBankIndices[i] = bankIndices[bands[i]];
            newBandOffsets[i] = bandOffsets[bands[i]];
        }

        return new ComponentSampleModel(this.dataType, width, height,
                                        this.pixelStride,
                                        this.scanlineStride,
                                        newBankIndices, newBandOffsets);
    }

    /**
     * Creates a DataBuffer that corresponds to this ComponentSampleModel.
     * The DataBuffer's data type, number of banks, and size
     * will be consistent with this ComponentSampleModel.
     */
    public DataBuffer createDataBuffer() {
        DataBuffer dataBuffer = null;

        int size = (int)getBufferSize();
        switch (dataType) {
        case DataBuffer.TYPE_BYTE:
            dataBuffer = new DataBufferByte(size, numBanks);
            break;
        case DataBuffer.TYPE_USHORT:
            dataBuffer = new DataBufferUShort(size, numBanks);
            break;
        case DataBuffer.TYPE_SHORT:
            dataBuffer = new DataBufferShort(size, numBanks);
            break;
        case DataBuffer.TYPE_INT:
            dataBuffer = new DataBufferInt(size, numBanks);
            break;
        }

        return dataBuffer;
    }


    /** Gets the offset for the first band of pixel (x,y).
     *  A sample of the first band can be retrieved from a DataBuffer
     *  <code>data</code> with a ComponentSampleModel <code>csm</code> as
     * <pre>
     *        data.getElem(csm.getOffset(x, y));
     * </pre>
     */
    public int getOffset(int x, int y) {
        int offset = y*scanlineStride + x*pixelStride + bandOffsets[0];
        return offset;
    }
 
    /** Gets the offset for band b of pixel (x,y).
     *  A sample of band <code>b</code> can be retrieved from a
     *  DataBuffer <code>data</code>
     *  with a ComponentSampleModel <code>csm</code> as
     * <pre>
     *       data.getElem(csm.getOffset(x, y, b));
     * </pre>
     */
    public int getOffset(int x, int y, int b) {
        int offset = y*scanlineStride + x*pixelStride + bandOffsets[b];
        return offset;
    }
 
    /** Returns the number of bits per sample for all bands. */
    public final int[] getSampleSize() {
        int sampleSize[] = new int [numBands];
        int sizeInBits = getSampleSize(0);

        for (int i=0; i<numBands; i++)
            sampleSize[i] = sizeInBits;

        return sampleSize;
    }

    /** Returns the number of bits per sample for the specified band. */
    public final int getSampleSize(int band) {
        return DataBuffer.getDataTypeSize(dataType);
    }

    /** Returns the bank indices for all bands. */
    public final int [] getBankIndices() {
        return (int[]) bankIndices.clone();
    }

    /** Returns the band offset for all bands. */
    public final int [] getBandOffsets() {
        return (int[])bandOffsets.clone();
    }

    /** Returns the scanline stride of this ComponentSampleModel. */
    public final int getScanlineStride() {
        return scanlineStride;
    }

    /** Returns the pixel stride of this ComponentSampleModel. */
    public final int getPixelStride() {
        return pixelStride;
    }

    /**
     * Returns the number of data elements needed to transfer a pixel
     * via the getDataElements and setDataElements methods.
     * For a ComponentSampleModel this is identical to the
     * number of bands.
     * @see java.awt.image.SampleModel#getNumDataElements
     */ 
    public final int getNumDataElements() {
	return getNumBands();
    }

    /** 
     * Returns data for a single pixel in a primitive array of type
     * TransferType.  For a ComponentSampleModel, this will be the same
     * as the data type, and samples will be returned one per array
     * element.  Generally, obj
     * should be passed in as null, so that the Object will be created
     * automatically and will be of the right primitive data type.
     * <p>
     * The following code illustrates transferring data for one pixel from
     * DataBuffer <code>db1</code>, whose storage layout is described by
     * ComponentSampleModel <code>csm1</code>, to DataBuffer <code>db2</code>,
     * whose storage layout is described by
     * ComponentSampleModel <code>csm2</code>.
     * The transfer will generally be more efficient than using
     * getPixel/setPixel.
     * <pre>
     * 	     ComponentSampleModel csm1, csm2;
     *	     DataBufferInt db1, db2;
     * 	     csm2.setDataElements(x, y,
     *                            csm1.getDataElements(x, y, null, db1), db2);
     * </pre>
     * Using getDataElements/setDataElements to transfer between two
     * DataBuffer/SampleModel pairs is legitimate if the SampleModels have
     * the same number of bands, corresponding bands have the same number of
     * bits per sample, and the TransferTypes are the same.
     * <p>
     * If obj is non-null, it should be a primitive array of type TransferType.
     * Otherwise, a ClassCastException is thrown.  An
     * ArrayIndexOutOfBoundsException may be thrown if the coordinates are
     * not in bounds, or if obj is non-null and is not large enough to hold
     * the pixel data.
     * @param x 	The X coordinate of the pixel location.
     * @param y 	The Y coordinate of the pixel location.
     * @param obj       If non-null, a primitive array in which to return
     *                  the pixel data.
     * @param data      The DataBuffer containing the image data.
     */
    public Object getDataElements(int x, int y, Object obj, DataBuffer data) {

	int type = getTransferType();
	int numDataElems = getNumDataElements();
	int pixelOffset = y*scanlineStride + x*pixelStride;

	switch(type) {

	case DataBuffer.TYPE_BYTE:

	    byte[] bdata;

	    if (obj == null)
		bdata = new byte[numDataElems];
	    else
		bdata = (byte[])obj;

	    for (int i=0; i<numDataElems; i++) {
		bdata[i] = (byte)data.getElem(bankIndices[i],
                                              pixelOffset + bandOffsets[i]);
	    }

	    obj = (Object)bdata;
	    break;

	case DataBuffer.TYPE_USHORT:
        case DataBuffer.TYPE_SHORT:
            
	    short[] sdata;

	    if (obj == null)
		sdata = new short[numDataElems];
	    else
		sdata = (short[])obj;

	    for (int i=0; i<numDataElems; i++) {
		sdata[i] = (short)data.getElem(bankIndices[i],
                                               pixelOffset + bandOffsets[i]);
	    }

	    obj = (Object)sdata;
	    break;

	case DataBuffer.TYPE_INT:

	    int[] idata;

	    if (obj == null)
		idata = new int[numDataElems];
	    else
		idata = (int[])obj;

	    for (int i=0; i<numDataElems; i++) {
		idata[i] = data.getElem(bankIndices[i],
                                        pixelOffset + bandOffsets[i]);
	    }

	    obj = (Object)idata;
	    break;
        }
        
	return obj;
    }

    /**
     * Returns all samples for the specified pixel in an int array,
     * one sample per array element.
     * ArrayIndexOutOfBoundsException may be thrown if the coordinates are
     * not in bounds.
     * @param x 	The X coordinate of the pixel location.
     * @param y 	The Y coordinate of the pixel location.
     * @param iArray 	If non-null, returns the samples in this array.
     * @param data 	The DataBuffer containing the image data.
     */
    public int[] getPixel(int x, int y, int iArray[], DataBuffer data) {
        int pixels[];
        if (iArray != null) {
           pixels = iArray;
        } else {
           pixels = new int [numBands];
        }
        int pixelOffset = y*scanlineStride + x*pixelStride;
        for (int i=0; i<numBands; i++) {
            pixels[i] = data.getElem(bankIndices[i],
                                     pixelOffset + bandOffsets[i]);
        }
        return pixels;
    }

    /**
     * Returns all samples for the specified rectangle of pixels in
     * an int array, one sample per array element.
     * ArrayIndexOutOfBoundsException may be thrown if the coordinates are
     * not in bounds.
     * @param x 	The X coordinate of the upper left pixel location.
     * @param y 	The Y coordinate of the upper left pixel location.
     * @param w 	The width of the pixel rectangle.
     * @param h 	The height of the pixel rectangle.
     * @param iArray 	If non-null, returns the samples in this array.
     * @param data 	The DataBuffer containing the image data.
     */
    public int[] getPixels(int x, int y, int w, int h,
                           int iArray[], DataBuffer data) {
        int pixels[];
        if (iArray != null) {
           pixels = iArray;
        } else {
           pixels = new int [w*h*numBands];
        }
        int lineOffset = y*scanlineStride + x*pixelStride;
        int srcOffset = 0;

        for (int i = 0; i < h; i++) {
           int pixelOffset = lineOffset;
           for (int j = 0; j < w; j++) {
              for (int k=0; k < numBands; k++) {
                 pixels[srcOffset++] =
                    data.getElem(bankIndices[k], pixelOffset + bandOffsets[k]);
              }
              pixelOffset += pixelStride;
           }
           lineOffset += scanlineStride;
        }
        return pixels;
    }

    /**
     * Returns as int the sample in a specified band for the pixel
     * located at (x,y).
     * ArrayIndexOutOfBoundsException may be thrown if the coordinates are
     * not in bounds.
     * @param x 	The X coordinate of the pixel location.
     * @param y 	The Y coordinate of the pixel location.
     * @param b 	The band to return.
     * @param data 	The DataBuffer containing the image data.
     */
    public int getSample(int x, int y, int b, DataBuffer data) {
        int sample = data.getElem(bankIndices[b],
                                  y*scanlineStride + x*pixelStride +
                                  bandOffsets[b]);
        return sample;
    }

    /**
     * Returns the sample in a specified band
     * for the pixel located at (x,y) as a float.
     * ArrayIndexOutOfBoundsException may be thrown if the coordinates are
     * not in bounds.
     * @param x 	The X coordinate of the pixel location.
     * @param y 	The Y coordinate of the pixel location.
     * @param b 	The band to return.
     * @param data 	The DataBuffer containing the image data.
     */
    public float getSampleFloat(int x, int y, int b, DataBuffer data) {

        float sample = data.getElemFloat(bankIndices[b],
                                         y*scanlineStride + x*pixelStride +
                                         bandOffsets[b]);
        return sample;
    }

    /**
     * Returns the sample in a specified band
     * for a pixel located at (x,y) as a double.
     * ArrayIndexOutOfBoundsException may be thrown if the coordinates are
     * not in bounds.
     * @param x 	The X coordinate of the pixel location.
     * @param y 	The Y coordinate of the pixel location.
     * @param b 	The band to return.
     * @param data 	The DataBuffer containing the image data.
     */
    public double getSampleDouble(int x, int y, int b, DataBuffer data) {

        double sample = data.getElemDouble(bankIndices[b],
                                           y*scanlineStride + x*pixelStride +
                                           bandOffsets[b]);
	return sample;
    }

    /**
     * Returns the samples in a specified band for the specified rectangle
     * of pixels in an int array, one sample per data array element.
     * ArrayIndexOutOfBoundsException may be thrown if the coordinates are
     * not in bounds.
     * @param x 	The X coordinate of the upper left pixel location.
     * @param y 	The Y coordinate of the upper left pixel location.
     * @param w 	The width of the pixel rectangle.
     * @param h 	The height of the pixel rectangle.
     * @param b 	The band to return.
     * @param iArray 	If non-null, returns the samples in this array.
     * @param data 	The DataBuffer containing the image data.
     */
    public int[] getSamples(int x, int y, int w, int h, int b,
                            int iArray[], DataBuffer data) {
        int samples[];
        if (iArray != null) {
           samples = iArray;
        } else {
           samples = new int [w*h];
        }
        int lineOffset = y*scanlineStride + x*pixelStride +  bandOffsets[b];
        int srcOffset = 0;

        for (int i = 0; i < h; i++) {
           int sampleOffset = lineOffset;
           for (int j = 0; j < w; j++) {
              samples[srcOffset++] = data.getElem(bankIndices[b],
                                                  sampleOffset);
              sampleOffset += pixelStride;
           }
           lineOffset += scanlineStride;
        }
        return samples;
    }

    /** 
     * Sets the data for a single pixel in the specified DataBuffer from a
     * primitive array of type TransferType.  For a ComponentSampleModel,
     * this will be the same as the data type, and samples are transferred
     * one per array element.
     * <p>
     * The following code illustrates transferring data for one pixel from
     * DataBuffer <code>db1</code>, whose storage layout is described by
     * ComponentSampleModel <code>csm1</code>, to DataBuffer <code>db2</code>,
     * whose storage layout is described by
     * ComponentSampleModel <code>csm2</code>.
     * The transfer will generally be more efficient than using
     * getPixel/setPixel.
     * <pre>
     * 	     ComponentSampleModel csm1, csm2;
     *	     DataBufferInt db1, db2;
     * 	     csm2.setDataElements(x, y, csm1.getDataElements(x, y, null, db1),
     *                            db2);
     * </pre>
     * Using getDataElements/setDataElements to transfer between two
     * DataBuffer/SampleModel pairs is legitimate if the SampleModels have
     * the same number of bands, corresponding bands have the same number of
     * bits per sample, and the TransferTypes are the same.
     * <p>
     * obj must be a primitive array of type TransferType.  Otherwise,
     * a ClassCastException is thrown.  An
     * ArrayIndexOutOfBoundsException may be thrown if the coordinates are
     * not in bounds, or if obj is not large enough to hold the pixel data.
     * @param x 	The X coordinate of the pixel location.
     * @param y 	The Y coordinate of the pixel location.
     * @param obj       A primitive array containing pixel data.
     * @param data      The DataBuffer containing the image data.
     */
    public void setDataElements(int x, int y, Object obj, DataBuffer data) {

	int type = getTransferType();
	int numDataElems = getNumDataElements();
	int pixelOffset = y*scanlineStride + x*pixelStride;

	switch(type) {

	case DataBuffer.TYPE_BYTE:

	    byte[] barray = (byte[])obj;

	    for (int i=0; i<numDataElems; i++) {
		data.setElem(bankIndices[i], pixelOffset + bandOffsets[i],
			   ((int)barray[i])&0xff);
	    }
	    break;

	case DataBuffer.TYPE_USHORT:
        case DataBuffer.TYPE_SHORT:
            
	    short[] sarray = (short[])obj;

	    for (int i=0; i<numDataElems; i++) {
		data.setElem(bankIndices[i], pixelOffset + bandOffsets[i],
			   ((int)sarray[i])&0xffff);
	    }
	    break;

	case DataBuffer.TYPE_INT:

	    int[] iarray = (int[])obj;

	    for (int i=0; i<numDataElems; i++) {
		data.setElem(bankIndices[i],
                             pixelOffset + bandOffsets[i], iarray[i]);
	    }
	    break;

	case DataBuffer.TYPE_FLOAT:

	    float[] farray = (float[])obj;

	    for (int i=0; i<numDataElems; i++) {
		data.setElemFloat(bankIndices[i],
                             pixelOffset + bandOffsets[i], farray[i]);
	    }
	    break;

	case DataBuffer.TYPE_DOUBLE:

	    double[] darray = (double[])obj;

	    for (int i=0; i<numDataElems; i++) {
		data.setElemDouble(bankIndices[i],
                             pixelOffset + bandOffsets[i], darray[i]);
	    }
	    break;

	}
    }

    /**
     * Sets a pixel in the DataBuffer using an int array of samples for input.
     * ArrayIndexOutOfBoundsException may be thrown if the coordinates are
     * not in bounds.
     * @param x 	The X coordinate of the pixel location.
     * @param y 	The Y coordinate of the pixel location.
     * @param iArray 	The input samples in an int array.
     * @param data 	The DataBuffer containing the image data.
     */
    public void setPixel(int x, int y, int iArray[], DataBuffer data) {
       int pixelOffset = y*scanlineStride + x*pixelStride;
       for (int i=0; i<numBands; i++) {
           data.setElem(bankIndices[i],
                        pixelOffset + bandOffsets[i],iArray[i]);
       }
    }

    /**
     * Sets all samples for a rectangle of pixels from an int array containing
     * one sample per array element.
     * ArrayIndexOutOfBoundsException may be thrown if the coordinates are
     * not in bounds.
     * @param x 	The X coordinate of the upper left pixel location.
     * @param y 	The Y coordinate of the upper left pixel location.
     * @param w 	The width of the pixel rectangle.
     * @param h         The height of the pixel rectangle.
     * @param iArray 	The input samples in an int array.
     * @param data 	The DataBuffer containing the image data.
     */
    public void setPixels(int x, int y, int w, int h,
                          int iArray[], DataBuffer data) {

        int lineOffset = y*scanlineStride + x*pixelStride;
        int srcOffset = 0;

        for (int i = 0; i < h; i++) {
           int pixelOffset = lineOffset;
           for (int j = 0; j < w; j++) {
              for (int k=0; k < numBands; k++) {
                 data.setElem(bankIndices[k], pixelOffset + bandOffsets[k],
                              iArray[srcOffset++]);
              }
              pixelOffset += pixelStride;
           }
           lineOffset += scanlineStride;
        }
    }

    /**
     * Sets a sample in the specified band for the pixel located at (x,y)
     * in the DataBuffer using an int for input.
     * ArrayIndexOutOfBoundsException may be thrown if the coordinates are
     * not in bounds.
     * @param x 	The X coordinate of the pixel location.
     * @param y 	The Y coordinate of the pixel location.
     * @param b 	The band to set.
     * @param s 	The input sample as an int.
     * @param data 	The DataBuffer containing the image data.
     */
    public void setSample(int x, int y, int b, int s,
                          DataBuffer data) {
        data.setElem(bankIndices[b],
                     y*scanlineStride + x*pixelStride + bandOffsets[b], s);
    }

    /**
     * Sets a sample in the specified band for the pixel located at (x,y)
     * in the DataBuffer using a float for input.
     * ArrayIndexOutOfBoundsException may be thrown if the coordinates are
     * not in bounds.
     * @param x 	The X coordinate of the pixel location.
     * @param y 	The Y coordinate of the pixel location.
     * @param b 	The band to set.
     * @param s 	The input sample as a float.
     * @param data 	The DataBuffer containing the image data.
     */
    public void setSample(int x, int y, int b,
			  float s ,
			  DataBuffer data) {
        data.setElemFloat(bankIndices[b],
                          y*scanlineStride + x*pixelStride + bandOffsets[b],
                          s);
    }

    /**
     * Sets a sample in the specified band for the pixel located at (x,y)
     * in the DataBuffer using a double for input.
     * ArrayIndexOutOfBoundsException may be thrown if the coordinates are
     * not in bounds.
     * @param x 	The X coordinate of the pixel location.
     * @param y 	The Y coordinate of the pixel location.
     * @param b 	The band to set.
     * @param s 	The input sample as a double.
     * @param data 	The DataBuffer containing the image data.
     */
    public void setSample(int x, int y, int b,
			  double s,
			  DataBuffer data) {
        data.setElemDouble(bankIndices[b],
                          y*scanlineStride + x*pixelStride + bandOffsets[b],
                          s);
    }

    /**
     * Sets the samples in the specified band for the specified rectangle
     * of pixels from an int array containing one sample per data array element.
     * ArrayIndexOutOfBoundsException may be thrown if the coordinates are
     * not in bounds.
     * @param x 	The X coordinate of the upper left pixel location.
     * @param y 	The Y coordinate of the upper left pixel location.
     * @param w 	The width of the pixel rectangle.
     * @param h         The height of the pixel rectangle.
     * @param b 	The band to set.
     * @param iArray 	The input samples in an int array.
     * @param data 	The DataBuffer containing the image data.
     */
    public void setSamples(int x, int y, int w, int h, int b,
                           int iArray[], DataBuffer data) {
        int lineOffset = y*scanlineStride + x*pixelStride + bandOffsets[b];
        int srcOffset = 0;

        for (int i = 0; i < h; i++) {
           int sampleOffset = lineOffset;
           for (int j = 0; j < w; j++) {
              data.setElem(bankIndices[b], sampleOffset, iArray[srcOffset++]);
              sampleOffset += pixelStride;
           }
           lineOffset += scanlineStride;
        }
    }
}

