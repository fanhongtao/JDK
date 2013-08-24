/*
 * @(#)WindowsTableHeaderUI.java	1.15 06/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import java.awt.*;
import java.awt.event.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.*;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.swing.*;
import javax.swing.event.*;

import com.sun.java.swing.plaf.windows.TMSchema.*;
import com.sun.java.swing.plaf.windows.XPStyle.Skin;


public class WindowsTableHeaderUI extends BasicTableHeaderUI {
    private TableCellRenderer originalHeaderRenderer;
    private int rolloverColumn = -1;

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

    private void updateRolloverColumn(MouseEvent e) {
	if (header.getDraggedColumn() == null &&
	    header.contains(e.getPoint())) {

	    int col = header.columnAtPoint(e.getPoint());
	    if (col != rolloverColumn) {
		rolloverColumn = col;
		header.repaint();
	    }
	}
    }

    protected MouseInputListener createMouseInputListener() {
	if (XPStyle.getXP() != null) {
	    return new MouseInputHandler() {
		public void mouseMoved(MouseEvent e) { 
		    super.mouseMoved(e);
		    updateRolloverColumn(e);
		}

		public void mouseEntered(MouseEvent e) {
		    super.mouseEntered(e);
		    updateRolloverColumn(e);
		}

		public void mouseExited(MouseEvent e) {
		    super.mouseExited(e);
		    rolloverColumn = -1;
		    header.repaint();
		}

		public void mousePressed(MouseEvent e) {
		    super.mousePressed(e);
		    if (header.getReorderingAllowed()) {
			rolloverColumn = -1;
			header.repaint();
		    }
		}

		public void mouseDragged(MouseEvent e) {
		    super.mouseDragged(e);
		    updateRolloverColumn(e);
		}

		public void mouseReleased(MouseEvent e) {
		    super.mouseReleased(e);
		    updateRolloverColumn(e);
		}
	    };
	} else {
	    return super.createMouseInputListener();
	}
    }

    public void uninstallUI(JComponent c) {
	if (header.getDefaultRenderer() instanceof XPDefaultRenderer) {
	    header.setDefaultRenderer(originalHeaderRenderer);
	}
	super.uninstallUI(c);
    }

    private class XPDefaultRenderer extends DefaultTableCellRenderer implements UIResource  {
        Skin skin;
	boolean isSelected, hasFocus, hasRollover;
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
	    this.hasRollover = (column == rolloverColumn);
            if (skin == null || skin.getContentMargin() == null) {
                skin = XPStyle.getXP().getSkin(header, Part.HP_HEADERITEM);
            }
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

