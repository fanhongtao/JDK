/*
 * @(#)ImageFormatException.java	1.3 98/06/09
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

/* ********************************************************************
 **********************************************************************
 **********************************************************************
 *** COPYRIGHT (c) Eastman Kodak Company, 1997                      ***
 *** As  an unpublished  work pursuant to Title 17 of the United    ***
 *** States Code.  All rights reserved.                             ***
 **********************************************************************
 **********************************************************************
 **********************************************************************/

package com.sun.image.codec.jpeg;

/**
 * Signals that an Image Format exception of some sort has occurred. 
 * <p>
 * Note that the classes in the com.sun.image.codec.jpeg package are not
 * part of the core Java APIs.  They are a part of Sun's JDK and JRE
 * distributions.  Although other licensees may choose to distribute these
 * classes, developers cannot depend on their availability in non-Sun
 * implementations.  We expect that equivalent functionality will eventually
 * be available in a core API or standard extension.
 * <p>
 *
 * @author  Tom Sausner
 * @see     JPEGImageEncoder
 * @see     JPEGImageDecoder
 * @since   JDK1.2
 */
public
class ImageFormatException extends RuntimeException {
    /**
     * Constructs an <code>ImageFormatException</code> with no detail message. 
     */
    public ImageFormatException() {
	super();
    }

    /**
     * Constructs an <code>ImageFormatException</code> with the specified
     * detailed message. 
     *
     * @param   s   the message.
     */
    public ImageFormatException(String s) {
	super(s);
    }
}
