/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;

/**
 * A mutable version of <code>ComboBoxModel</code>.
 *
 * @version 1.9 02/06/02
 * @author Tom Santos
 */

public interface MutableComboBoxModel extends ComboBoxModel {
    /**
     * Adds an item to the end of the model.
     * @param obj  the <code>Object</code> to be added
     */
    public void addElement( Object obj );

    /**
     * Removes an item from the model.
     * @param obj  the <code>Object</code> to be removed
     */
    public void removeElement( Object obj );

    /**
     * Adds an item at a specific index
     * @param obj  the <code>Object</code> to be added
     * @param index  location to add the object
     */
    public void insertElementAt( Object obj, int index );

    /**
     * Removes an item at a specific index
     * @param index  location of object to be removed
     */
    public void removeElementAt( int index );
}


