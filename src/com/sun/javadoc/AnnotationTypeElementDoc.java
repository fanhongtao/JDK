/*
 * @(#)AnnotationTypeElementDoc.java	1.4 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

package com.sun.javadoc;


/**
 * Represents an element of an annotation type.
 * 
 * @author Scott Seligman
 * @version 1.4 05/11/17
 * @since 1.5
 */
public interface AnnotationTypeElementDoc extends MethodDoc {

    /**
     * Returns the default value of this element.
     * Returns null if this element has no default.
     *
     * @return the default value of this element.
     */
    AnnotationValue defaultValue();
}
