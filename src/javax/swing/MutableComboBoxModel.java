/*
 * @(#)MutableComboBoxModel.java	1.8 00/02/02
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package javax.swing;

/**
 * A mutable version of <code>ComboBoxModel</code>.
 *
 * @version 1.8 02/02/00
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


