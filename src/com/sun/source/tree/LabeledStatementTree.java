/*
 * @(#)LabeledStatementTree.java	1.3 06/07/11
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
 * A tree node for a labeled statement.
 *
 * For example:
 * <pre>
 *   <em>label</em> : <em>statement</em>
 * </pre>
 *
 * @see "The Java Language Specification, 3rd ed, section 14.7"
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @since 1.6
 */
public interface LabeledStatementTree extends StatementTree {
    Name getLabel();
    StatementTree getStatement();
}
