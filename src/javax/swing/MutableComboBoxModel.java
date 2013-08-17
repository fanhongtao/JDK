/*
 * @(#)MutableComboBoxModel.java	1.2 00/01/12
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package javax.swing;

/**
 * A mutable version of ComboBoxModel.
 *
 * @version 1.5 08/26/98
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


