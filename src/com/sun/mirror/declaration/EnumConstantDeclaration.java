/*
 * @(#)EnumConstantDeclaration.java	1.3 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

package com.sun.mirror.declaration;


/**
 * Represents an enum constant declaration.
 *
 * @author Joseph D. Darcy
 * @author Scott Seligman
 * @version 1.3 05/11/17
 * @since 1.5
 */

public interface EnumConstantDeclaration extends FieldDeclaration {
    /**
     * {@inheritDoc}
     */
    EnumDeclaration getDeclaringType();
}
