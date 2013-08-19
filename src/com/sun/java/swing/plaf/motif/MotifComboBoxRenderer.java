/*
 * @(#)MotifComboBoxRenderer.java	1.12 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.motif;

/**
 * A renderer for combo box with motif look and feel
 *
 * @version 1.12 01/23/03
 * @author Arnaud Weber
 */

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import java.awt.Component;
import java.awt.Color;

import java.io.Serializable;

/**
 * Motif rendition of the combo box renderer.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 */
public class MotifComboBoxRenderer extends JLabel
    implements ListCellRenderer, Serializable
{
    protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

    public MotifComboBoxRenderer() {
	super();
	setOpaque(true);
	setBorder(noFocusBorder);
    }


    public Component getListCellRendererComponent(
        JList list, 
	Object value,
        int index, 
        boolean isSelected, 
        boolean cellHasFocus)
    {

        setHorizontalAlignment(SwingConstants.LEFT);
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
	
	if (value instanceof Icon) {
	    setIcon((Icon)value);
	}
	else {
	    setText((value == null) ? "" : value.toString());
	}
	return this;
    }


    /**
     * A subclass of MotifComboBoxRenderer that implements UIResource.
     * MotifComboBoxRenderer doesn't implement UIResource
     * directly so that applications can safely override the
     * cellRenderer property with MotifListCellRenderer subclasses.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    public static class UIResource extends MotifComboBoxRenderer 
        implements javax.swing.plaf.UIResource
    {
    }



}


