/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
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
 * @version 1.16 06/27/03
 * @author Rich Schiavi
 */
public class MotifButtonListener extends BasicButtonListener {
    public MotifButtonListener(AbstractButton b ) {
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

    protected void checkOpacity(AbstractButton b) {
	b.setOpaque( false );
    }
}


