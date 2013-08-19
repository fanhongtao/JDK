/*
 * @(#)WindowsButtonListener.java	1.13 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 

package com.sun.java.swing.plaf.windows;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;
import javax.swing.event.*;

/**
 * Button Listener
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.2 11/03/97
 * @author Rich Schiavi
 */
public class WindowsButtonListener extends BasicButtonListener
{
    public WindowsButtonListener(AbstractButton b ) {
	super(b);
    }
    
/*
    // Here for rollover purposes
    public void mouseEntered(MouseEvent e) {
	AbstractButton button = (AbstractButton)e.getSource();
	button.getModel().setRollover(true);
    }
    
    // Here for rollover purposes
    public void mouseExited(MouseEvent e) {
	AbstractButton button = (AbstractButton)e.getSource();
	button.getModel().setRollover(false);
    }
*/

}


