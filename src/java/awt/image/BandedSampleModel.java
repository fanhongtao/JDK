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
 *  This class represents image data which is stored in a band interleaved
 *  fashion and for
 *  which each sample of a pixel occupies one data element of the DataBuffer.
 *  It subclasses ComponentSampleModel but provides a more efficent
 *  implementation for accessing band interleaved image data than is provided
 *  by ComponentSampleModel.  This class should typically be used when working
 *  with images which store sample data for each band in a different bank of the
 *  DataBuffer. Accessor methods are provided so that image data can be
 *  manipulated directly. Pixel stride is the number of
 *  data array elements between two samples for the same band on the same
 *  scanline. The pixel stride for a BandedSampleModel is one.
 *  Scanline stride is the number of data array elements between
 *  a given sample and the corresponding sample in the same column of the next
 *  scanline.  Band offsets denote the number
 *  of data array elements from the first data array element of the bank
 *  of the DataBuffer holding each band to the first sample of the band.
 *  The bands are numbered from 0 to N-1.
 *  Bank indices denote the correspondence between a bank of the data buffer
 *  and a band of image data.  This class supports 
 *  {@link DataBuffer#TYPE_BYTE TYPE_BYTE},
 *  {@link DataBuffer#TYPE_USHORT TYPE_USHORT},
 *  {@link DataBuffer#TYPE_SHORT TYPE_SHORT},
 *  {@link DataBuffer#TYPE_INT TYPE_INT} datatypes
 */


public final class BandedSampleModel extends ComponentSampleModel
{

    /**
     * Constructs a BandedSampleModel with the specified parameters.
     * The pixel stride will be one data element.  The scanline stride
     * will be the same as the width.  Each band will be stored in
     * a separate bank and all band offsets will be zero.
     * @param dataType  The data type for storing samples.
     * @param w 	The width (in pixels) of the region of
     *                  image data described. 
     * @param h 	The height (in pixels) of the region of image
     *                  data described.
     * @param numBands  The number of bands for the image data.
     * @throws IllegalArgumentException if <code>dataType</code> is not
     *         one of the supported data types
     */
    public BandedSampleModel(int dataType, int w, int h, int numBands) {
	super(dataType, w, h, 1, w,
              BandedSampleModel.createIndiciesArray(numBands),
              BandedSampleModel.createOffsetArray(numBands));
    }

    /**
     * Constructs a BandedSampleModel with the specified parameters. 
     * The number of bands will be inferred from the lengths of the
     * bandOffsets bankIndices arrays, which must be equal.  The pixel
     * stride will be one data element.
     * @param dataType  The data type for storing samples.
     * @param w 	The width (in pixels) of the region of
     *                  image data described.
     * @param h 	The height (in pixels) of the region of
     *                  image data described.
     * @param numBands  The number of bands for the image data.
     * @param scanlineStride The line stride of the of the image data.
     * @param bankIndices The bank index for each band.
     * @param bandOffsets The band offset for each band.
     * @throws IllegalArgumentException if <code>dataType</code> is not
     *         one of the supported data types
     */
    public BandedSampleModel(int dataType,
			     int w, int h,
			     int scanlineStride,
			     int bankIndices[],
                             int bandOffsets[]) {

	super(dataType, w, h, 1,scanlineStride, bankIndices, bandOffsets);
    }

    /**
     * Creates a new BandedSampleModel with the specified
     * width and height.  The new BandedSampleModel will have the same
     * number of bands, storage data type, and bank indices
     * as this BandedSampleModel.  The band offsets will be compressed
     * such that the offset between bands will be w*pixelStride and
     * the minimum of all of the band offsets is zero.
     * @param w the width of the resulting <code>BandedSampleModel</code>
     * @param h the height of the resulting <code>BandedSampleModel</code>
     * @return a new <code>BandedSampleModel</code> with the specified
     *         width and height.
     * @throws IllegalArgumentException if <code>w</code> or 
     *         <code>h</code> equals either
     *         <code>Integer.MAX_VALUE</code> or
     *         <code>Integer.MIN_VALUE</code>
     * @throws IllegalArgumentException if <code>dataType</code> is not
     *         one of the supported data types
     */
    public SampleModel createCompatibleSampleModel(int w, int h) {
        int[] bandOffs;

        if (numBanks == 1) {
            bandOffs = orderBands(bandOffsets, w*h);
        }
        else {
            bandOffs = new int[bandOffsets.length];
        }

        SampleModel sampleModel = 
            new BandedSampleModel(dataType, w, h, w, bankIndices, bandOffs);
	return sampleModel;
    }

    /**
     * Creates a new BandedSampleModel with a subset of the bands of this
     * BandedSampleModel.  The new BandedSampleModel can be
     * used with any DataBuffer that the existing BandedSampleModel
     * can be used with.  The new BandedSampleModel/DataBuffer
     * combination will represent an image with a subset of the bands
     * of the original BandedSampleModel/DataBuffer combination.
     * @throws RasterFormatException if the number of bands is greater than
     *                               the number of banks in this sample model.
     * @throws IllegalArgumentException if <code>dataType</code> is not
     *         one of the supported data types 
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

        return new BandedSampleModel(this.dataType, width, height,
				     this.scanlineStride,
				     newBankIndices, newBandOffsets);
    }

    /**
     * Creates a DataBuffer that corresponds to this BandedSampleModel,
     * The DataBuffer's data type, number of banks, and size
     * will be consistent with this BandedSampleModel.
     * @throws IllegalArgumentException if <code>dataType</code> is not
     *         either <code>DataBuffer.TYPE_BYTE</code>,
     *         <code>DataBuffer.TYPE_USHORT</code>, or
     *         <code>DataBuffer.TYPE_SHORT</code>, or
     *         <code>DataBuffer.TYPE_INT</code>
     */
    public DataBuffer createDataBuffer() {
        DataBuffer dataBuffer = null;

	int size = scanlineStride * height;
        switch (dataType) {
        case DataBuffer.TYPE_BYTE:
            dataBuffer = new DataBufferByte(size, numBands);
            break;
        case DataBuffer.TYPE_USHORT:
            dataBuffer = new DataBufferUShort(size, numBands);
            break;
        case DataBuffer.TYPE_SHORT:
            dataBuffer = new DataBufferShort(size, numBands);
            break;
        case DataBuffer.TYPE_INT:
            dataBuffer = new DataBufferInt(size, numBands);
            break;
        }

        return dataBuffer;
    }


    /** 
     * Returns data for a single pixel in a primitive array of type
     * TransferType.  For a BandedSampleModel, this will be the same
     * as the data type, and samples will be returned one per array
     * element.  Generally, obj
     * should be passed in as null, so that the Object will be created
     * automatically and will be of the right primitive data type.
     * <p>
     * The following code illustrates transferring data for one pixel from
     * DataBuffer <code>db1</code>, whose storage layout is described by
     * BandedSampleModel <code>bsm1</code>, to DataBuffer <code>db2</code>,
     * whose storage layout is described by
     * BandedSampleModel <code>bsm2</code>.
     * The transfer will generally be more efficient than using
     * getPixel/setPixel.
     * <pre>
     * 	     BandedSampleModel bsm1, bsm2;
     *	     DataBufferInt db1, db2;
     * 	     bsm2.setDataElements(x, y, bsm1.getDataElements(x, y, null, db1),
     *                            db2);
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
	int pixelOffset = y*scanlineStride + x;

	switch(type) {

	case DataBuffer.TYPE_BYTE:

	    byte[] bdata;

	    if (obj == null) {
		bdata = new byte[numDataElems];
            } else {
		bdata = (byte[])obj;
            }

	    for (int i=0; i<numDataElems; i++) {
		bdata[i] = (byte)data.getElem(bankIndices[i],
                                              pixelOffset + bandOffsets[i]);
	    }

	    obj = (Object)bdata;
	    break;

	case DataBuffer.TYPE_USHORT:
        case DataBuffer.TYPE_SHORT:
            
	    short[] sdata;

	    if (obj == null) {
		sdata = new short[numDataElems];
	    } else {
		sdata = (short[])obj;
            }

	    for (int i=0; i<numDataElems; i++) {
		sdata[i] = (short)data.getElem(bankIndices[i],
                                               pixelOffset + bandOffsets[i]);
	    }

	    obj = (Object)sdata;
	    break;

	case DataBuffer.TYPE_INT:

	    int[] idata;

	    if (obj == null) {
		idata = new int[numDataElems];
            } else {
		idata = (int[])obj;
            }

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
     * Returns all samples for the specified pixel in an int array.
     * ArrayIndexOutOfBoundsException may be thrown if the coordinates are
     * not in bounds.
     * @param x 	The X coordinate of the pixel location.
     * @param y 	The Y coordinate of the pixel location.
     * @param iArray 	If non-null, returns the samples in this array.
     * @param data 	The DataBuffer containing the image data.
     */
    public int[] getPixel(int x, int y, int iArray[], DataBuffer data) {

        int[] pixels;

	if (iArray != null) {
           pixels = iArray;
        } else {
           pixels = new int [numBands];
        }

        int pixelOffset = y*scanlineStride + x;
        for (int i=0; i<numBands; i++) {
            pixels[i] = data.getElem(bankIndices[i],
                                     pixelOffset + bandOffsets[i]);
        }
        return pixels;
    }

    /**
     * Returns all samples for the specified rectangle of pixels in
     * an int array, one sample per data array element.
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
        int[] pixels;

        if (iArray != null) {
           pixels = iArray;
        } else {
           pixels = new int[w*h*numBands];
        }

        for (int k = 0; k < numBands; k++) {
            int lineOffset = y*scanlineStride + x + bandOffsets[k];
            int srcOffset = k;
            int bank = bankIndices[k];

            for (int i = 0; i < h; i++) {
                int pixelOffset = lineOffset;
                for (int j = 0; j < w; j++) {
                    pixels[srcOffset] = data.getElem(bank, pixelOffset++);
                    srcOffset += numBands;
                }
                lineOffset += scanlineStride;
            }
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
        int sample =
            data.getElem(bankIndices[b],
                         y*scanlineStride + x + bandOffsets[b]);
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
                                    y*scanlineStride + x + bandOffsets[b]);
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
                                       y*scanlineStride + x + bandOffsets[b]);
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

        int lineOffset = y*scanlineStride + x + bandOffsets[b];
        int srcOffset = 0;
	int bank = bankIndices[b];

        for (int i = 0; i < h; i++) {
           int sampleOffset = lineOffset;
           for (int j = 0; j < w; j++) {
	       samples[srcOffset++] = data.getElem(bank, sampleOffset++);
           }
           lineOffset += scanlineStride;
        }
        return samples;
    }

    /** 
     * Sets the data for a single pixel in the specified DataBuffer from a
     * primitive array of type TransferType.  For a BandedSampleModel,
     * this will be the same as the data type, and samples are transferred
     * one per array element.
     * <p>
     * The following code illustrates transferring data for one pixel from
     * DataBuffer <code>db1</code>, whose storage layout is described by
     * BandedSampleModel <code>bsm1</code>, to DataBuffer <code>db2</code>,
     * whose storage layout is described by
     * BandedSampleModel <code>bsm2</code>.
     * The transfer will generally be more efficient than using
     * getPixel/setPixel.
     * <pre>
     * 	     BandedSampleModel bsm1, bsm2;
     *	     DataBufferInt db1, db2;
     * 	     bsm2.setDataElements(x, y, bsm1.getDataElements(x, y, null, db1),
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
     * @param obj       If non-null, returns the primitive array in this object.
     * @param data      The DataBuffer containing the image data.
     */
    public void setDataElements(int x, int y, Object obj, DataBuffer data) {
	int type = getTransferType();
	int numDataElems = getNumDataElements();
	int pixelOffset = y*scanlineStride + x;

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
		data.setElem(bankIndices[i], pixelOffset + bandOffsets[i],
                             iarray[i]);
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
     * @param iArray    The input samples in an int array.
     * @param data 	The DataBuffer containing the image data.
     */
    public void setPixel(int x, int y, int iArray[], DataBuffer data) {
       int pixelOffset = y*scanlineStride + x;
       for (int i=0; i<numBands; i++) {
           data.setElem(bankIndices[i], pixelOffset + bandOffsets[i],
                        iArray[i]);
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
     * @param h 	The height of the pixel rectangle.
     * @param iArray    The input samples in an int array.
     * @param data      The DataBuffer containing the image data.
     */
    public void setPixels(int x, int y, int w, int h,
                          int iArray[], DataBuffer data) {

        for (int k = 0; k < numBands; k++) {
            int lineOffset = y*scanlineStride + x + bandOffsets[k];
            int srcOffset = k;
            int bank = bankIndices[k];

            for (int i = 0; i < h; i++) {
                int pixelOffset = lineOffset;
                for (int j = 0; j < w; j++) {
                    data.setElem(bank, pixelOffset++, iArray[srcOffset]);
                    srcOffset += numBands;
                }
                lineOffset += scanlineStride;
           }
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
     * @param s         The input sample as an int.
     * @param data 	The DataBuffer containing the image data.
     */
    public void setSample(int x, int y, int b, int s,
                          DataBuffer data) {
        data.setElem(bankIndices[b],
                     y*scanlineStride + x + bandOffsets[b], s);
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
                          y*scanlineStride + x + bandOffsets[b], s);
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
                          y*scanlineStride + x + bandOffsets[b], s);
    }

    /**
     * Sets the samples in the specified band for the specified rectangle
     * of pixels from an int array containing one sample per data array element.
     * ArrayIndexOutOfBoundsException may be thrown if the coordinates are
     * not in bounds.
     * @param x 	The X coordinate of the upper left pixel location.
     * @param y 	The Y coordinate of the upper left pixel location.
     * @param w 	The width of the pixel rectangle.
     * @param h 	The height of the pixel rectangle.
     * @param b 	The band to set.
     * @param iArray    The input sample array.
     * @param data 	The DataBuffer containing the image data.
     */
    public void setSamples(int x, int y, int w, int h, int b,
                           int iArray[], DataBuffer data) {
        int lineOffset = y*scanlineStride + x + bandOffsets[b];
        int srcOffset = 0;
	int bank = bankIndices[b];

        for (int i = 0; i < h; i++) {
           int sampleOffset = lineOffset;
           for (int j = 0; j < w; j++) {
              data.setElem(bank, sampleOffset++, iArray[srcOffset++]);
           }
           lineOffset += scanlineStride;
        }
    }

    private static int[] createOffsetArray(int numBands) {
        int[] bandOffsets = new int[numBands];
        for (int i=0; i < numBands; i++) {
            bandOffsets[i] = 0;
        }
        return bandOffsets;
    }

    private static int[] createIndiciesArray(int numBands) {
        int[] bankIndicies = new int[numBands];
        for (int i=0; i < numBands; i++) {
            bankIndicies[i] = i;
        }
        return bankIndicies;
    }
}

