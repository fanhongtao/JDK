/*
 * @(#)EmptyStatementTree.java	1.2 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Use and Distribution is subject to the Java Research License available
 * at <http://wwws.sun.com/software/communitysource/jrl.html>.
 */

package com.sun.source.tree;

/**
 * A tree node for an empty (skip) statement.
 *
 * For example:
 * <pre>
 *    ;
 * </pre>
 *
 * @see "The Java Language Specification, 3rd ed, section 14.6"
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @since 1.6
 */
public interface EmptyStatementTree extends StatementTree {}
