/*
 * @(#)SynthCheckBoxMenuItemUI.java	1.5 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.sun.java.swing.plaf.gtk;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.border.*;
import java.io.Serializable;


/**
 * SynthCheckboxMenuItem implementation derived from BasicCheckBoxMenuItemUI
 *
 * @version 1.5 01/23/03 (based on BasicCheckBoxMenuItemUI v 1.51)
 * @author Leif Samuelsson
 * @author Georges Saab
 * @author David Karlton
 * @author Arnaud Weber
 */
class SynthCheckBoxMenuItemUI extends SynthMenuItemUI {

    public static ComponentUI createUI(JComponent c) {
        return new SynthCheckBoxMenuItemUI();
    }

    protected String getPropertyPrefix() {
	return "CheckBoxMenuItem";
    }

    public void processMouseEvent(JMenuItem item, MouseEvent e,
				  MenuElement path[], MenuSelectionManager manager) {
        Point p = e.getPoint();
        if (p.x >= 0 && p.x < item.getWidth() && p.y >= 0 && p.y < item.getHeight()) {
            if (e.getID() == MouseEvent.MOUSE_RELEASED) {
                manager.clearSelectedPath();
                item.doClick(0);
            } else {
                manager.setSelectedPath(path);
	    }
        } else if (item.getModel().isArmed()) {
	    int c = path.length - 1;
            MenuElement newPath[] = new MenuElement[c];
            for (int i = 0; i < c; i++) {
                newPath[i] = path[i];
	    }
            manager.setSelectedPath(newPath);
        }
    }
}








