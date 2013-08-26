/*
 * @(#)AnnotationTypeElementDeclaration.java	1.5 10/03/23
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

package com.sun.mirror.declaration;


/**
 * Represents an element of an annotation type.
 *
 * @author Joe Darcy
 * @author Scott Seligman
 * @version 1.5 10/03/23
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
