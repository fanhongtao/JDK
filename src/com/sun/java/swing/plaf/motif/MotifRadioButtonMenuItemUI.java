/*
 * @(#)MotifRadioButtonMenuItemUI.java	1.40 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.sun.java.swing.plaf.motif;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicRadioButtonMenuItemUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;

import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;


/**
 * MotifRadioButtonMenuItem implementation
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.40 01/23/03
 * @author Georges Saab
 * @author Rich Schiavi
 */
public class MotifRadioButtonMenuItemUI extends BasicRadioButtonMenuItemUI
{
    protected ChangeListener changeListener;

    public static ComponentUI createUI(JComponent b) {
        return new MotifRadioButtonMenuItemUI();
    }

    protected void installListeners() {
	super.installListeners();
        changeListener = createChangeListener(menuItem);
        menuItem.addChangeListener(changeListener);	
    }
    
    protected void uninstallListeners() {
	super.uninstallListeners();
	menuItem.removeChangeListener(changeListener);
    }

    protected ChangeListener createChangeListener(JComponent c) {
	return new ChangeHandler();
    }

    protected class ChangeHandler implements ChangeListener, Serializable {
	public void stateChanged(ChangeEvent e) {
	    JMenuItem c = (JMenuItem)e.getSource();
	    if (c.isArmed()) {
		c.setBorderPainted(true);
	    } else {
		c.setBorderPainted(false);
	    }
	}
    }

    public void paint(Graphics g, JComponent c) {
	MotifGraphicsUtils.paintMenuItem(g, c, 
					 checkIcon,
					 arrowIcon,
					 selectionBackground, 
					 selectionForeground,
					 defaultTextIconGap);
    }

    protected MouseInputListener createMouseInputListener(JComponent c) {
	return new MouseInputHandler();
    }


    protected class MouseInputHandler implements MouseInputListener {
	public void mouseClicked(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {
	    MenuSelectionManager manager = MenuSelectionManager.defaultManager();
	    manager.setSelectedPath(getPath());
	}
	public void mouseReleased(MouseEvent e) {
	    MenuSelectionManager manager = 
		MenuSelectionManager.defaultManager();
	    JMenuItem menuItem = (JMenuItem)e.getComponent();
	    Point p = e.getPoint();
	    if(p.x >= 0 && p.x < menuItem.getWidth() &&
	       p.y >= 0 && p.y < menuItem.getHeight()) {
                manager.clearSelectedPath();
                menuItem.doClick(0);
	    } else {
		manager.processMouseEvent(e);
	    }
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseDragged(MouseEvent e) {
	    MenuSelectionManager.defaultManager().processMouseEvent(e);
	}
	public void mouseMoved(MouseEvent e) { }
    }

}








