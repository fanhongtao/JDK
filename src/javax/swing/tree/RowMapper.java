/*
 * @(#)RowMapper.java	1.8 98/09/21
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
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
