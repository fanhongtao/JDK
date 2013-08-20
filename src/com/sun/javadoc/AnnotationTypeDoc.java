/*
 * @(#)AnnotationTypeDoc.java	1.3 04/04/08
 *
 * Copyright 2004 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

package com.sun.javadoc;


/**
 * Represents an annotation type.
 * 
 * @author Scott Seligman
 * @version 1.3 04/04/08
 * @since 1.5
 */
public interface AnnotationTypeDoc extends ClassDoc {

    /**
     * Returns the elements of this annotation type.
     * Returns an empty array if there are none.
     *
     * @return the elements of this annotation type.
     */
    AnnotationTypeElementDoc[] elements();
}
