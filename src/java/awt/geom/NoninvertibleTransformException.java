/*
 * @(#)NoninvertibleTransformException.java	1.9 98/06/29
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

package java.awt.geom;

/**
 * The <code>NoninvertibleTransformException</code> class represents
 * an exception that is thrown if an operation is performed requiring
 * the inverse of an {@link AffineTransform} object but the 
 * <code>AffineTransform</code> is in a non-invertible state.
 * @version 10 Feb 1997
 */

public class NoninvertibleTransformException extends java.lang.Exception {
    /**
     * Constructs an instance of
     * <code>NoninvertibleTransformException</code>
     * with the specified detail message.
     * @param   s     the detail message
     * @since   JDK1.2
     */
    public NoninvertibleTransformException(String s) {
        super (s);
    }
}
