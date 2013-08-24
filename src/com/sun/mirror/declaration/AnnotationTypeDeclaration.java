/*
 * @(#)AnnotationTypeDeclaration.java	1.3 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

package com.sun.mirror.declaration;


import java.util.Collection;


/**
 * Represents the declaration of an annotation type.
 *
 * @author Joseph D. Darcy
 * @author Scott Seligman
 * @version 1.3 05/11/17
 * @since 1.5
 */

public interface AnnotationTypeDeclaration extends InterfaceDeclaration {

    /**
     * Returns the annotation type elements of this annotation type.
     * These are the methods that are directly declared in the type's
     * declaration.
     *
     * @return the annotation type elements of this annotation type,
     * or an empty collection if there are none
     */
    Collection<AnnotationTypeElementDeclaration> getMethods();
}
