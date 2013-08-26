/*
 * @(#)VoidType.java	1.3 10/03/23
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

package com.sun.mirror.type;


import com.sun.mirror.declaration.MethodDeclaration;


/**
 * A pseudo-type representing the type of <tt>void</tt>.
 *
 * @author Joseph D. Darcy
 * @author Scott Seligman
 * @version 1.3 10/03/23
 *
 * @see MethodDeclaration#getReturnType()
 * @since 1.5
 */

public interface VoidType extends TypeMirror {
}
