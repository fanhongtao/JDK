/*
 * @(#)ComboBoxUI.java	1.13 98/08/26
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

package javax.swing.plaf;

import javax.swing.JComboBox;

/**
 * Pluggable look and feel interface for JComboBox.
 *
 * @version 1.13 08/26/98
 * @author Arnaud Weber
 * @author Tom Santos
 */
public abstract class ComboBoxUI extends ComponentUI {

    /**
     * Set the visiblity of the popup
     */
    public abstract void setPopupVisible( JComboBox c, boolean v );

    /** 
     * Determine the visibility of the popup
     */
    public abstract boolean isPopupVisible( JComboBox c );

    /** 
     * Determine whether or not the combo box itself is traversable 
     */
    public abstract boolean isFocusTraversable( JComboBox c );
}
