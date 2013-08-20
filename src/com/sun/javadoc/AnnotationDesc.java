/*
 * @(#)AnnotationDesc.java	1.3 04/04/08
 *
 * Copyright 2004 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

package com.sun.javadoc;


/**
 * Represents an annotation.
 * An annotation associates a value with each element of an annotation type.
 * 
 * @author Scott Seligman
 * @version 1.3 04/04/08
 * @since 1.5
 */
public interface AnnotationDesc {

    /**
     * Returns the annotation type of this annotation.
     *
     * @return the annotation type of this annotation.
     */
    AnnotationTypeDoc annotationType();

    /**
     * Returns this annotation's elements and their values.
     * Only those explicitly present in the annotation are
     * included, not those assuming their default values.
     * Returns an empty array if there are none.
     *
     * @return this annotation's elements and their values.
     */
    ElementValuePair[] elementValues();


    /**
     * Represents an association between an annotation type element
     * and one of its values.
     * 
     * @author Scott Seligman
     * @version 1.3 04/04/08
     * @since 1.5
     */
    public interface ElementValuePair {

	/**
	 * Returns the annotation type element.
	 *
	 * @return the annotation type element.
	 */
	AnnotationTypeElementDoc element();

	/**
	 * Returns the value associated with the annotation type element.
	 *
	 * @return the value associated with the annotation type element.
	 */
	AnnotationValue value();
    }
}
