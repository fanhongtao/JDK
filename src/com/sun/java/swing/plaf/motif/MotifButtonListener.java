/*
 * @(#)MotifButtonListener.java	1.10 98/08/26
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
 

package com.sun.java.swing.plaf.motif;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;
import javax.swing.event.*;

/**
 * Button Listener
 * <p>
 *
 * @version 1.10 08/26/98
 * @author Rich Schiavi
 */
public class MotifButtonListener extends BasicButtonListener {
    public MotifButtonListener(AbstractButton b ) {
        super(b);
    }

    public void focusGained(FocusEvent e) { 
	AbstractButton b = (AbstractButton) e.getSource();
        if (b instanceof JButton && ((JButton)b).isDefaultCapable()) {
            // Only change the default button IF the root pane
            // containing this button has a default set.
            JRootPane root = SwingUtilities.getRootPane(b);
            if (root != null) {
                JButton current = root.getDefaultButton();
                if (current != null) {
                    root.setDefaultButton((JButton)b);
                }
            }
        }
	b.repaint();
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

    protected void checkOpacity(AbstractButton b) {
	b.setOpaque( false );
    }
}


