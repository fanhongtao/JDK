/*
 * @(#)EnumType.java	1.1 04/01/26
 *
 * Copyright 2004 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

package com.sun.mirror.type;


import com.sun.mirror.declaration.EnumDeclaration;


/**
 * Represents an enum type.
 *
 * @author Joseph D. Darcy
 * @author Scott Seligman
 * @version 1.1 04/01/26
 * @since 1.5
 */

public interface EnumType extends ClassType {

    /**
     * {@inheritDoc}
     */
    EnumDeclaration getDeclaration();
}
