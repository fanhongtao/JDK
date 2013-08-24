/*
 * @(#)MemberSelectTree.java	1.3 06/07/11
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
 * A tree node for a member access expression.
 *
 * For example:
 * <pre>
 *   <em>expression</em> . <em>identifier</em>
 * </pre>
 *
 * @see "The Java Language Specification, 3rd ed, sections 6.5,
 * 15.11, and 15.12"
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @since 1.6
 */
public interface MemberSelectTree extends ExpressionTree {
    ExpressionTree getExpression();
    Name getIdentifier();
}
