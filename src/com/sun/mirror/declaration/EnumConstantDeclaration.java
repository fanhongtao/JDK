/*
 * @(#)EnumConstantDeclaration.java	1.2 04/03/09
 *
 * Copyright 2004 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

package com.sun.mirror.declaration;


/**
 * Represents an enum constant declaration.
 *
 * @author Joseph D. Darcy
 * @author Scott Seligman
 * @version 1.2 04/03/09
 * @since 1.5
 */

public interface EnumConstantDeclaration extends FieldDeclaration {
    /**
     * {@inheritDoc}
     */
    EnumDeclaration getDeclaringType();
}
