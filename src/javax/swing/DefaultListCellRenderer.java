/*
 * @(#)DefaultListCellRenderer.java	1.9 98/08/28
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
 * @version 1.9 08/28/98
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
