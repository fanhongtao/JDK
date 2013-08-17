/*
 * @(#)ComboBoxUI.java	1.15 00/02/02
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing.plaf;

import javax.swing.JComboBox;

/**
 * Pluggable look and feel interface for JComboBox.
 *
 * @version 1.15 02/02/00
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
