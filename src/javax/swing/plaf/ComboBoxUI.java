/*
 * @(#)ComboBoxUI.java	1.18 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf;

import javax.swing.JComboBox;

/**
 * Pluggable look and feel interface for JComboBox.
 *
 * @version 1.18 01/23/03
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
