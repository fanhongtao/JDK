/*
 * @(#)AnnotationTypeElementDeclaration.java	1.3 04/04/20
 *
 * Copyright 2004 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

package com.sun.mirror.declaration;


/**
 * Represents an element of an annotation type.
 *
 * @author Joe Darcy
 * @author Scott Seligman
 * @version 1.3 04/04/20
 * @since 1.5
 */

public interface AnnotationTypeElementDeclaration extends MethodDeclaration {

    /**
     * Returns the default value of this element.
     *
     * @return the default value of this element, or null if this element
     * has no default.
     */
    AnnotationValue getDefaultValue();

    /**
     * {@inheritDoc}
     */
    AnnotationTypeDeclaration getDeclaringType();
}
