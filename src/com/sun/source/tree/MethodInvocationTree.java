/*
 * @(#)MethodInvocationTree.java	1.2 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Use and Distribution is subject to the Java Research License available
 * at <http://wwws.sun.com/software/communitysource/jrl.html>.
 */

package com.sun.source.tree;

import java.util.List;

/**
 * A tree node for a method invocation expression.
 *
 * For example:
 * <pre>
 *   <em>identifier</em> ( <em>arguments</em> )
 *
 *   this . <em>typeArguments</em> <em>identifier</em> ( <em>arguments</em> )
 * </pre>
 *
 * @see "The Java Language Specification, 3rd ed, section 15.12"
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @since 1.6
 */
public interface MethodInvocationTree extends ExpressionTree {
    List<? extends Tree> getTypeArguments();
    ExpressionTree getMethodSelect();
    List<? extends ExpressionTree> getArguments();
}
