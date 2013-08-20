/*
 * @(#)MutableComboBoxModel.java	1.12 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;

/**
 * A mutable version of <code>ComboBoxModel</code>.
 *
 * @version 1.12 12/19/03
 * @author Tom Santos
 */

public interface MutableComboBoxModel extends ComboBoxModel {

    /**
     * Adds an item at the end of the model. The implementation of this method
     * should notify all registered <code>ListDataListener</code>s that the 
     * item has been added.
     * 
     * @param obj the <code>Object</code> to be added
     */
    public void addElement( Object obj );

    /**
     * Removes an item from the model. The implementation of this method should
     * should notify all registered <code>ListDataListener</code>s that the 
     * item has been removed.
     *
     * @param obj the <code>Object</code> to be removed
     */
    public void removeElement( Object obj );

    /**
     * Adds an item at a specific index.  The implementation of this method
     * should notify all registered <code>ListDataListener</code>s that the 
     * item has been added.
     *
     * @param obj  the <code>Object</code> to be added
     * @param index  location to add the object
     */
    public void insertElementAt( Object obj, int index );

    /**
     * Removes an item at a specific index. The implementation of this method 
     * should notify all registered <code>ListDataListener</code>s that the 
     * item has been removed.
     *
     * @param index  location of object to be removed
     */
    public void removeElementAt( int index );
}


