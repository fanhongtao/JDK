/*
 * @(#)VoidType.java	1.1 04/01/26
 *
 * Copyright 2004 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

package com.sun.mirror.type;


import com.sun.mirror.declaration.MethodDeclaration;


/**
 * A pseudo-type representing the type of <tt>void</tt>.
 *
 * @author Joseph D. Darcy
 * @author Scott Seligman
 * @version 1.1 04/01/26
 *
 * @see MethodDeclaration#getReturnType()
 * @since 1.5
 */

public interface VoidType extends TypeMirror {
}
