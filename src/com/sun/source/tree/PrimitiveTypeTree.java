/*
 * @(#)PrimitiveTypeTree.java	1.2 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Use and Distribution is subject to the Java Research License available
 * at <http://wwws.sun.com/software/communitysource/jrl.html>.
 */

package com.sun.source.tree;

import javax.lang.model.type.TypeKind;

/**
 * A tree node for a primitive type.
 *
 * For example:
 * <pre>
 *   <em>primitiveTypeKind</em>
 * </pre>
 *
 * @see "The Java Language Specification, 3rd ed, section 4.2"
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @since 1.6
 */
public interface PrimitiveTypeTree extends Tree {
    TypeKind getPrimitiveTypeKind();
}
