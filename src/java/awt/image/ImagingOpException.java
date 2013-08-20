/*
 * @(#)ImagingOpException.java	1.9 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.image;


/**
 * The <code>ImagingOpException</code> is thrown if one of the
 * {@link BufferedImageOp} or {@link RasterOp} filter methods cannot
 * process the image.
 * @version 10 Feb 1997
 */ 
public class ImagingOpException extends java.lang.RuntimeException {

    /**
     * Constructs an <code>ImagingOpException</code> object with the
     * specified message.
     * @param s the message to generate when a
     * <code>ImagingOpException</code> is thrown
     */
    public ImagingOpException(String s) {
        super (s);
    }
}
