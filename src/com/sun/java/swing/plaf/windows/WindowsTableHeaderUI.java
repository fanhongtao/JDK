/*
 * @(#)WindowsTableHeaderUI.java	1.11 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import java.awt.*;

import javax.swing.plaf.basic.*;
import javax.swing.plaf.*;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.swing.*;



public class WindowsTableHeaderUI extends BasicTableHeaderUI {
    private TableCellRenderer originalHeaderRenderer;

    public static ComponentUI createUI(JComponent h) {
        return new WindowsTableHeaderUI();
    }

    public void installUI(JComponent c) {
	super.installUI(c);

	if (XPStyle.getXP() != null) {
	    originalHeaderRenderer = header.getDefaultRenderer();
	    if (originalHeaderRenderer instanceof UIResource) {
		header.setDefaultRenderer(new XPDefaultRenderer());
	    }
	}
    }

    public void uninstallUI(JComponent c) {
	if (header.getDefaultRenderer() instanceof XPDefaultRenderer) {
	    header.setDefaultRenderer(originalHeaderRenderer);
	}
	super.uninstallUI(c);
    }

    private class XPDefaultRenderer extends DefaultTableCellRenderer implements UIResource  {
	XPStyle.Skin skin = XPStyle.getXP().getSkin("header.headeritem");
	boolean isSelected, hasFocus;
	int column;

	public Component getTableCellRendererComponent(JTable table, Object value,
						       boolean isSelected, boolean hasFocus,
						       int row, int column) {
	    if (table != null) {
		JTableHeader header = table.getTableHeader();
		if (header != null) {
		    setFont(header.getFont());
		}
	    }
	    this.isSelected = isSelected;
	    this.hasFocus = hasFocus;
	    this.column = column;
	    setText((value == null) ? "" : value.toString());
	    setBorder(new EmptyBorder(skin.getContentMargin()));

	    return this;
	}

	private int viewIndexForColumn(TableColumn aColumn) {
	    TableColumnModel cm = header.getColumnModel();
	    for (int column = 0; column < cm.getColumnCount(); column++) {
		if (cm.getColumn(column) == aColumn) {
		    return column;
		}
	    }
	    return -1;
	}

	public void paint(Graphics g) {
	    Dimension size = getSize();
	    int index = 0;
	    if (column == viewIndexForColumn(header.getDraggedColumn())) {
		index = 2;
	    } else if (isSelected || hasFocus) {
		index = 1;
	    }
	    skin.paintSkin(g, 0, 0, size.width-1, size.height-1, index);
	    super.paint(g);
	}
    }
}

