/*
 * @(#)InterfaceType.java	1.1 04/01/26
 *
 * Copyright 2004 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

package com.sun.mirror.type;


import com.sun.mirror.declaration.*;


/**
 * Represents an interface type.
 * Note that an {@linkplain AnnotationType annotation type} is
 * a kind of interface.
 *
 * <p> While an {@link InterfaceDeclaration} represents the
 * <i>declaration</i> of an interface, an <tt>InterfaceType</tt>
 * represents an interface <i>type</i>.
 * See {@link TypeDeclaration} for more on this distinction.
 *
 * @author Joseph D. Darcy
 * @author Scott Seligman
 * @version 1.1 04/01/26
 * @since 1.5
 */

public interface InterfaceType extends DeclaredType {

    /**
     * {@inheritDoc}
     */
    InterfaceDeclaration getDeclaration();
}
