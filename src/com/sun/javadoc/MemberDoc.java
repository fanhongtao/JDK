/*
 * @(#)MemberDoc.java	1.8 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.javadoc;

/**
 * Represents a member of a java class: field, constructor, or method.
 * This is an abstract class dealing with information common to
 * method, constructor and field members. Class members of a class
 * (innerclasses) are represented instead by ClassDoc.
 *
 * @see MethodDoc
 * @see FieldDoc
 * @see ClassDoc
 *
 * @author Kaiyang Liu (original)
 * @author Robert Field (rewrite)
 */
public interface MemberDoc extends ProgramElementDoc {

    /**
     * Returns true if this member was synthesized by the compiler.
     */
    boolean isSynthetic();
}
