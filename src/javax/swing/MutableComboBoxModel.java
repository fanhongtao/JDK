/*
 * @(#)MutableComboBoxModel.java	1.6 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;

/**
 * A mutable version of ComboBoxModel.
 *
 * @version 1.6 11/29/01
 * @author Tom Santos
 */

public interface MutableComboBoxModel extends ComboBoxModel {
    /**
     * Adds an item to the end of the model.
     */
    public void addElement( Object obj );

    /**
     * Adds an item to the end of the model.
     */
    public void removeElement( Object obj );

    /**
     * Adds an item at a specific index
     */
    public void insertElementAt( Object obj, int index );

    /**
     * Removes an item at a specific index
     */
    public void removeElementAt( int index );
}


