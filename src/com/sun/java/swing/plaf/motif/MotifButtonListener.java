/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 1.14 02/06/02
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

    protected void checkOpacity(AbstractButton b) {
	b.setOpaque( false );
    }
}


