/*
 * @(#)UISwitchListener.java	1.3 98/08/26
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

import java.awt.*;
import java.beans.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


/**
  * This class listens for UISwitches, and updates a given component.
  *
  * @version 1.3 08/26/98
  * @author Steve Wilson
  */
public class UISwitchListener implements PropertyChangeListener {
    JComponent componentToSwitch;

    public UISwitchListener(JComponent c) {
        componentToSwitch = c;
    }

    public void propertyChange(PropertyChangeEvent e) {
        String name = e.getPropertyName();
	if (name.equals("lookAndFeel")) {
	    SwingUtilities.updateComponentTreeUI(componentToSwitch);
	    componentToSwitch.invalidate();
	    componentToSwitch.validate();
	    componentToSwitch.repaint();
	}
    }
}
