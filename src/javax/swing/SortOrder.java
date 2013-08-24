/*
 * @(#)SortOrder.java	1.2 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;

/**
 * SortOrder is an enumeration of the possible sort orderings.
 *
 * @version 1.2 11/17/05
 * @see RowSorter
 * @since 1.6
 */
public enum SortOrder {
    /**
     * Enumeration value indicating the items are sorted in increasing order.
     * For example, the set <code>1, 4, 0</code> sorted in
     * <code>ASCENDING</code> order is <code>0, 1, 4</code>.
     */
    ASCENDING,

    /**
     * Enumeration value indicating the items are sorted in decreasing order.
     * For example, the set <code>1, 4, 0</code> sorted in
     * <code>DESCENDING</code> order is <code>4, 1, 0</code>.
     */
    DESCENDING,

    /**
     * Enumeration value indicating the items are unordered.
     * For example, the set <code>1, 4, 0</code> in
     * <code>UNSORTED</code> order is <code>1, 4, 0</code>.
     */
    UNSORTED
}
