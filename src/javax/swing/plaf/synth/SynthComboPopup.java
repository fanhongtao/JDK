/*
 * @(#)SynthComboPopup.java	1.7 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.synth;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.Serializable;


/**
 * Synth's ComboPopup.
 *
 * @version 1.7, 12/19/03
 * @author Scott Violet
 */
class SynthComboPopup extends BasicComboPopup {
    public SynthComboPopup( JComboBox combo ) {
        super(combo);
    }

    /**
     * Configures the list which is used to hold the combo box items in the
     * popup. This method is called when the UI class
     * is created.
     *
     * @see #createList
     */
    protected void configureList() {
        list.setFont( comboBox.getFont() );
        list.setCellRenderer( comboBox.getRenderer() );
        list.setFocusable( false );
        list.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        int selectedIndex = comboBox.getSelectedIndex();
        if ( selectedIndex == -1 ) {
            list.clearSelection();
        }
        else {
            list.setSelectedIndex( selectedIndex );
	    list.ensureIndexIsVisible( selectedIndex );
        }
        installListListeners();
    }
}
