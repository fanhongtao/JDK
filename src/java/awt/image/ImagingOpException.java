/*
 * @(#)ImagingOpException.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
