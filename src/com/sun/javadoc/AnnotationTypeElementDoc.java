/*
 * @(#)AnnotationTypeElementDoc.java	1.3 04/04/08
 *
 * Copyright 2004 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

package com.sun.javadoc;


/**
 * Represents an element of an annotation type.
 * 
 * @author Scott Seligman
 * @version 1.3 04/04/08
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
