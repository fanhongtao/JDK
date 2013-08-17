/*
 * @(#)Kernel.java	1.14 98/08/03
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

package java.awt.image;


/**
 * This class defines a Kernel object.  A kernel is a matrix describing
 * how a given pixel and its surrounding pixels affect the value computed
 * for the given pixel's position in the output image of a filtering
 * operation.  The X origin and Y origin indicate the kernel matrix element
 * which corresponds to the pixel position for which an output value is
 * being computed.
 *
 * @see ConvolveOp
 * @version 10 Feb 1997
 */
public class Kernel implements Cloneable {
    private int  width;
    private int  height;
    private int  xOrigin;
    private int  yOrigin;
    private float data[];

    private static native void initIDs();
    static {
        ColorModel.loadLibraries();
        initIDs();
    }

    /**
     * Constructs a Kernel object from an array of floats.  The first
     * width*height elements of the data array are
     * copied.  If the length of the data array is less than width*height,
     * an IllegalArgumentException is thrown.
     * The X origin will be (width-1)/2 and the Y origin will be (height-1)/2.
     * @param width         Width of the kernel.
     * @param height        Height of the kernel.
     * @param data          Kernel data in row major order.
     */
    public Kernel(int width, int height, float data[]) {
        this.width  = width;
        this.height = height;
        this.xOrigin  = (width-1)>>1;
        this.yOrigin  = (height-1)>>1;
        int len = width*height;
        if (data.length < len) {
            throw new IllegalArgumentException("Data array too small "+
                                               "(is "+data.length+
                                               " and should be "+len);
        }
        this.data = new float[len];
        System.arraycopy(data, 0, this.data, 0, len);

    }

    /**
     * Returns the X origin.
     */
    final public int getXOrigin(){
        return xOrigin;
    }

    /**
     * Returns the Y origin.
     */
    final public int getYOrigin() {
        return yOrigin;
    }

    /**
     * Returns the width.
     */
    final public int getWidth() {
        return width;
    }

    /**
     * Returns the height.
     */
    final public int getHeight() {
        return height;
    }

    /**
     * Returns the kernel data in row major order.  The data array is
     * returned.  If data is null, a new array will be allocated.
     * @param data        If non-null, will contain the returned kernel data.
     */
    final public float[] getKernelData(float[] data) {
        if (data == null) {
            data = new float[this.data.length];
        }
        else if (data.length < this.data.length) {
            throw new IllegalArgumentException("Data array too small "+
                                               "(should be "+this.data.length+
                                               " but is "+
                                               data.length+" )");
        }
        System.arraycopy(this.data, 0, data, 0, this.data.length);

        return data;
    }

    /**
     * Clones this object.
     */
    public Object clone() {
	try {
	    return super.clone();
	} catch (CloneNotSupportedException e) {
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
    }
}
