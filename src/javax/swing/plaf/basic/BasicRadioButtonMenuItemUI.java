/*
 * @(#)BasicRadioButtonMenuItemUI.java	1.39 98/08/26
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
 
package javax.swing.plaf.basic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.plaf.*;
import javax.swing.border.*;

/**
 * BasicRadioButtonMenuItem implementation
 *
 * @version 1.39 08/26/98
 * @author Georges Saab
 * @author David Karlton
 */
public class BasicRadioButtonMenuItemUI extends BasicMenuItemUI
{
    public static ComponentUI createUI(JComponent b) {
        return new BasicRadioButtonMenuItemUI();
    }

    protected void installDefaults() {
 	super.installDefaults();
	String prefix = getPropertyPrefix();
 	if (menuItem.getSelectedIcon() == null ||
 	    menuItem.getSelectedIcon() instanceof UIResource) {
 	    menuItem.setSelectedIcon(
			 UIManager.getIcon(prefix + ".checkIcon"));
 	}
    }

    protected String getPropertyPrefix() {
	return "RadioButtonMenuItem";
    }
    
    public void processMouseEvent(JMenuItem item,MouseEvent e,MenuElement path[],MenuSelectionManager manager) {
        Point p = e.getPoint();
        if(p.x >= 0 && p.x < item.getWidth() &&
           p.y >= 0 && p.y < item.getHeight()) {
            if(e.getID() == MouseEvent.MOUSE_RELEASED) {
                manager.clearSelectedPath();
                item.doClick(0);
                item.setArmed(false);
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








