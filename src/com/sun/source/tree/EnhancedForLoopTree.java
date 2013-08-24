/*
 * @(#)EnhancedForLoopTree.java	1.4 06/03/14
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Use and Distribution is subject to the Java Research License available
 * at <http://wwws.sun.com/software/communitysource/jrl.html>.
 */

package com.sun.source.tree;

/**
 * A tree node for an "enhanced" 'for' loop statement.
 *
 * For example:
 * <pre>
 *   for ( <em>variable</em> : <em>expression</em> )
 *       <em>statement</em>
 * </pre>
 *
 * @see "The Java Language Specification, 3rd ed, section 14.14.2"
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @since 1.6
 */
public interface EnhancedForLoopTree extends StatementTree {
    VariableTree getVariable();
    ExpressionTree getExpression();
    StatementTree getStatement();
}
