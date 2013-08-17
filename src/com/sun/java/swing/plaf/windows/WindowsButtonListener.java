/*
 * @(#)WindowsButtonListener.java	1.8 98/08/28
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
}


