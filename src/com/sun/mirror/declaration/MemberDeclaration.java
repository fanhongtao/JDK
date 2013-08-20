/*
 * @(#)MemberDeclaration.java	1.1 04/01/26
 *
 * Copyright 2004 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

package com.sun.mirror.declaration;


/**
 * Represents a declaration that may be a member or constructor of a declared
 * type.  This includes fields, constructors, methods, and (since they
 * may be nested) declared types themselves.
 *
 * @author Joseph D. Darcy
 * @author Scott Seligman
 * @version 1.1 04/01/26
 * @since 1.5
 */

public interface MemberDeclaration extends Declaration {

    /**
     * Returns the type declaration within which this member or constructor
     * is declared.
     * If this is the declaration of a top-level type (a non-nested class
     * or interface), returns null.
     *
     * @return the type declaration within which this member or constructor
     * is declared, or null if there is none
     */
    TypeDeclaration getDeclaringType();
}
