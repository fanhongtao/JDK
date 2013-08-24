/*
 * @(#)AnnotationTypeDoc.java	1.4 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

package com.sun.javadoc;


/**
 * Represents an annotation type.
 * 
 * @author Scott Seligman
 * @version 1.4 05/11/17
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
