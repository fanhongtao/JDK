/*
 * @(#)RowMapper.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing.tree;

import javax.swing.tree.TreePath;

/**
 * Defines the requirements for an object that translates paths in
 * the tree into display rows.
 *
 * @version 1.8 09/21/98
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
