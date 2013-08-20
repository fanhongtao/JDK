/*
 * @(#)RowMapper.java	1.13 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.tree;

import javax.swing.tree.TreePath;

/**
 * Defines the requirements for an object that translates paths in
 * the tree into display rows.
 *
 * @version 1.13 12/19/03
 * @author Scott Violet
 */
public interface RowMapper
{
    /**
     * Returns the rows that the TreePath instances in <code>path</code>
     * are being displayed at. The receiver should return an array of
     * the same length as that passed in, and if one of the TreePaths
     * in <code>path</code> is not valid its entry in the array should
     * be set to -1.
     */
    int[] getRowsForPaths(TreePath[] path);
}
