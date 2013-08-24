/*
 * @(#)ClassTree.java	1.5 06/07/11
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Use and Distribution is subject to the Java Research License available
 * at <http://wwws.sun.com/software/communitysource/jrl.html>.
 */

package com.sun.source.tree;

import java.util.List;
import javax.lang.model.element.Name;

/**
 * A tree node for a class, interface, enum, or annotation
 * type declaration.
 *
 * For example:
 * <pre>
 *   <em>modifiers</em> class <em>simpleName</em> <em>typeParameters</em>
 *       extends <em>extendsClause</em>
 *       implements <em>implementsClause</em>
 *   {
 *       <em>members</em>
 *   }
 * </pre>
 *
 * @see "The Java Language Specification, 3rd ed,
 * sections 8.1, 8.9, 9.1, and 9.6"
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @since 1.6
 */
public interface ClassTree extends StatementTree {
    ModifiersTree getModifiers();
    Name getSimpleName();
    List<? extends TypeParameterTree> getTypeParameters();
    Tree getExtendsClause();
    List<? extends Tree> getImplementsClause();
    List<? extends Tree> getMembers();
}
