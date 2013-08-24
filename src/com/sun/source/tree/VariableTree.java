/*
 * @(#)VariableTree.java	1.4 06/07/11
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Use and Distribution is subject to the Java Research License available
 * at <http://wwws.sun.com/software/communitysource/jrl.html>.
 */

package com.sun.source.tree;

import javax.lang.model.element.Name;

/**
 * A tree node for a variable declaration.
 *
 * For example:
 * <pre>
 *   <em>modifiers</em> <em>type</em> <em>name</em> <em>initializer</em> ;
 * </pre>
 *
 * @see "The Java Language Specification, 3rd ed, sections 8.3 and 14.4"
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @since 1.6
 */
public interface VariableTree extends StatementTree {
    ModifiersTree getModifiers();
    Name getName();
    Tree getType();
    ExpressionTree getInitializer();
}
