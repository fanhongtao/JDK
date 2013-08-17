/*
 * @(#)RasterFormatException.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.awt.image;


/**
 * The <code>RasterFormatException</code> is thrown if there is
 * invalid layout information in the {@link Raster}.
 * @version 10 Feb 1997
 */ 
public class RasterFormatException extends java.lang.RuntimeException {
    
    /**
     * Constructs a new <code>RasterFormatException</code> with the
     * specified message.
     * @param s the message to generate when a 
     * <code>RasterFormatException</code> is thrown
     */
    public RasterFormatException(String s) {
        super (s);
    }
}
