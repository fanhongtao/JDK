/*
 * @(#)ImportTree.java	1.2 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Use and Distribution is subject to the Java Research License available
 * at <http://wwws.sun.com/software/communitysource/jrl.html>.
 */

package com.sun.source.tree;

/**
 * A tree node for an import statement.
 *
 * For example:
 * <pre>
 *   import <em>qualifiedIdentifier</em> ;
 *
 *   static import <em>qualifiedIdentifier</em> ;
 * </pre>
 *
 * @see "The Java Language Specification, 3rd ed, section 7.5"
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @since 1.6
 */
public interface ImportTree extends Tree {
    boolean isStatic();
    /**
     * @return a qualified identifier ending in "*" if and only if
     * this is an import-on-demand.
     */
    Tree getQualifiedIdentifier();
}
