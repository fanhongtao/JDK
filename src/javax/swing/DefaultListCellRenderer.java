/*
 * @(#)DefaultListCellRenderer.java	1.2 00/01/12
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import java.awt.Component;
import java.awt.Color;

import java.io.Serializable;


/**
 * Renders an item in a list.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.12 04/22/99
 * @author Philip Milne
 * @author Hans Muller
 */
public class DefaultListCellRenderer extends JLabel
    implements ListCellRenderer, Serializable
{

    protected static Border noFocusBorder;

    /**
     * Constructs a default renderer object for an item
     * in a list.
     */
    public DefaultListCellRenderer() {
	super();
       	noFocusBorder = new EmptyBorder(1, 1, 1, 1);
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
        
        setComponentOrientation(list.getComponentOrientation());
        
	if (isSelected) {
	    setBackground(list.getSelectionBackground());
	    setForeground(list.getSelectionForeground());
	}
	else {
	    setBackground(list.getBackground());
	    setForeground(list.getForeground());
	}

	if (value instanceof Icon) {
	    setIcon((Icon)value);
	}
	else {
	    setText((value == null) ? "" : value.toString());
	}

	setEnabled(list.isEnabled());
	setFont(list.getFont());
	setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

	return this;
    }


    /**
     * A subclass of DefaultListCellRenderer that implements UIResource.
     * DefaultListCellRenderer doesn't implement UIResource
     * directly so that applications can safely override the
     * cellRenderer property with DefaultListCellRenderer subclasses.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    public static class UIResource extends DefaultListCellRenderer
        implements javax.swing.plaf.UIResource
    {
    }

}
