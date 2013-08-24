/*
 * @(#)WindowsTableHeaderUI.java	1.21 06/03/16
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.table.*;

import static com.sun.java.swing.plaf.windows.TMSchema.*;
import static com.sun.java.swing.plaf.windows.XPStyle.*;
import sun.swing.table.*;


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

    @Override
    protected void rolloverColumnUpdated(int oldColumn, int newColumn) {
        if (XPStyle.getXP() != null) {
            header.repaint(header.getHeaderRect(oldColumn));
            header.repaint(header.getHeaderRect(newColumn));
        }
    }

    private class XPDefaultRenderer extends DefaultTableCellHeaderRenderer {
        Skin skin;
	boolean isSelected, hasFocus, hasRollover;
	int column;

        XPDefaultRenderer() {
            setHorizontalAlignment(LEADING);
        }

	public Component getTableCellRendererComponent(JTable table, Object value,
						       boolean isSelected, boolean hasFocus,
						       int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected,
                                                hasFocus, row, column);
	    this.isSelected = isSelected;
	    this.hasFocus = hasFocus;
	    this.column = column;
            this.hasRollover = (column == getRolloverColumn());
            if(skin == null || skin.getContentMargin() == null) {
                skin = XPStyle.getXP().getSkin(header, Part.HP_HEADERITEM);
            }
            Insets margins = skin.getContentMargin();
            if(margins == null) {
                margins = new Insets(0, 0, 0, 0);
            }
            setBorder(new EmptyBorder(margins));
	    return this;
	}

	private int viewIndexForColumn(TableColumn aColumn) {
            if (aColumn != null) {
                return header.getTable().convertColumnIndexToView(
                        aColumn.getModelIndex());
            }
	    return -1;
	}

	public void paint(Graphics g) {
	    Dimension size = getSize();
	    State state = State.NORMAL;
	    if (column == viewIndexForColumn(header.getDraggedColumn())) {
		state = State.PRESSED;
	    } else if (isSelected || hasFocus || hasRollover) {
		state = State.HOT;
	    }
	    skin.paintSkin(g, 0, 0, size.width-1, size.height-1, state);
	    super.paint(g);
	}
    }
}

