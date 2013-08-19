/*
 * @(#)NoninvertibleTransformException.java	1.17 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.geom;

/**
 * The <code>NoninvertibleTransformException</code> class represents
 * an exception that is thrown if an operation is performed requiring
 * the inverse of an {@link AffineTransform} object but the 
 * <code>AffineTransform</code> is in a non-invertible state.
 * @version 	1.17, 01/23/03
 */

public class NoninvertibleTransformException extends java.lang.Exception {
    /**
     * Constructs an instance of
     * <code>NoninvertibleTransformException</code>
     * with the specified detail message.
     * @param   s     the detail message
     * @since   1.2
     */
    public NoninvertibleTransformException(String s) {
        super (s);
    }
}
