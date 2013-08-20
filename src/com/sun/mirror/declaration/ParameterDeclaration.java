/*
 * @(#)ParameterDeclaration.java	1.1 04/01/26
 *
 * Copyright 2004 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

package com.sun.mirror.declaration;


import com.sun.mirror.type.TypeMirror;


/**
 * Represents a formal parameter of a method or constructor.
 *
 * @author Joseph D. Darcy
 * @author Scott Seligman
 * @version 1.1 04/01/26
 * @since 1.5
 */

public interface ParameterDeclaration extends Declaration {

    /**
     * Returns the type of this parameter.
     *
     * @return the type of this parameter
     */
    TypeMirror getType();
}
