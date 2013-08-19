/*
 * @(#)BasicCheckBoxMenuItemUI.java	1.52 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package javax.swing.plaf.basic;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.border.*;
import java.io.Serializable;


/**
 * BasicCheckboxMenuItem implementation
 *
 * @version 1.52 01/23/03
 * @author Georges Saab
 * @author David Karlton
 * @author Arnaud Weber
 */
public class BasicCheckBoxMenuItemUI extends BasicMenuItemUI {

    public static ComponentUI createUI(JComponent c) {
        return new BasicCheckBoxMenuItemUI();
    }

    protected String getPropertyPrefix() {
	return "CheckBoxMenuItem";
    }

    public void processMouseEvent(JMenuItem item,MouseEvent e,MenuElement path[],MenuSelectionManager manager) {
        Point p = e.getPoint();
        if(p.x >= 0 && p.x < item.getWidth() &&
           p.y >= 0 && p.y < item.getHeight()) {
            if(e.getID() == MouseEvent.MOUSE_RELEASED) {
                manager.clearSelectedPath();
                item.doClick(0);
            } else
                manager.setSelectedPath(path);
        } else if(item.getModel().isArmed()) {
            MenuElement newPath[] = new MenuElement[path.length-1];
            int i,c;
            for(i=0,c=path.length-1;i<c;i++)
                newPath[i] = path[i];
            manager.setSelectedPath(newPath);
        }
    }
}








