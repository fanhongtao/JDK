/*
 * @(#)MutableComboBoxModel.java	1.5 98/08/26
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
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


