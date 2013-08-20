/*
 * @(#)ArrayType.java	1.1 04/01/26
 *
 * Copyright 2004 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

package com.sun.mirror.type;


/**
 * Represents an array type.
 * A multidimensional array type is represented as an array type
 * whose component type is also an array type.
 *
 * @author Joseph D. Darcy
 * @author Scott Seligman
 * @version 1.1 04/01/26
 * @since 1.5
 */

public interface ArrayType extends ReferenceType {

    /**
     * Returns the component type of this array type.
     *
     * @return the component type of this array type
     */
    TypeMirror getComponentType();
}
