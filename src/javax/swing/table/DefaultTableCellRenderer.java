/*
 * @(#)DefaultTableCellRenderer.java	1.2 00/01/12
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing.table;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.border.*;

import java.awt.Component;
import java.awt.Color;

import java.io.Serializable;

/**
 * The standard class for rendering (displaying) individual cells
 * in a JTable.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.15 09/01/98
 * @author Philip Milne 
 * @see JTable
 */
public class DefaultTableCellRenderer extends JLabel
    implements TableCellRenderer, Serializable
{

    protected static Border noFocusBorder; 
    
    // We need a place to store the color the JLabel should be returned 
    // to after its foreground and background colors have been set 
    // to the selection background color. 
    // These ivars will be made protected when their names are finalized. 
    private Color unselectedForeground; 
    private Color unselectedBackground; 

    /**
     * Creates a default table cell renderer.
     */
    public DefaultTableCellRenderer() {
	super();
        noFocusBorder = new EmptyBorder(1, 2, 1, 2);
	setOpaque(true);
        setBorder(noFocusBorder);
    }

    /**
     * Overrides <code>JComponent.setForeground</code> to specify
     * the unselected-foreground color using the specified color.
     */
    public void setForeground(Color c) {
        super.setForeground(c); 
        unselectedForeground = c; 
    }
    
    /**
     * Overrides <code>JComponent.setForeground</code> to specify
     * the unselected-background color using the specified color.
     */
    public void setBackground(Color c) {
        super.setBackground(c); 
        unselectedBackground = c; 
    }

    /**
     * Notification from the UIManager that the L&F has changed. 
     * Replaces the current UI object with the latest version from the 
     * UIManager.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        super.updateUI(); 
	setForeground(null);
	setBackground(null);
    }
    
    // implements javax.swing.table.TableCellRenderer
    public Component getTableCellRendererComponent(JTable table, Object value,
                          boolean isSelected, boolean hasFocus, int row, int column) {

	if (isSelected) {
	   super.setForeground(table.getSelectionForeground());
	   super.setBackground(table.getSelectionBackground());
	}
	else {
	    super.setForeground((unselectedForeground != null) ? unselectedForeground 
	                                                       : table.getForeground());
	    super.setBackground((unselectedBackground != null) ? unselectedBackground 
	                                                       : table.getBackground());
	}
	
	setFont(table.getFont());

	if (hasFocus) {
	    setBorder( UIManager.getBorder("Table.focusCellHighlightBorder") );
	    if (table.isCellEditable(row, column)) {
	        super.setForeground( UIManager.getColor("Table.focusCellForeground") );
	        super.setBackground( UIManager.getColor("Table.focusCellBackground") );
	    }
	} else {
	    setBorder(noFocusBorder);
	}

        setValue(value); 
        
	return this;
    }
    
    protected void setValue(Object value) {
	setText((value == null) ? "" : value.toString());
    }


    /**
     * A subclass of DefaultTableCellRenderer that implements UIResource.
     * DefaultTableCellRenderer doesn't implement UIResource
     * directly so that applications can safely override the
     * cellRenderer property with DefaultTableCellRenderer subclasses.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    public static class UIResource extends DefaultTableCellRenderer 
        implements javax.swing.plaf.UIResource
    {
    }

}


