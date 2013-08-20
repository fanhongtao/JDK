/*
 * @(#)MotifButtonListener.java	1.18 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
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
 * @version 1.18 12/19/03
 * @author Rich Schiavi
 */
public class MotifButtonListener extends BasicButtonListener {
    public MotifButtonListener(AbstractButton b ) {
        super(b);
    }

    protected void checkOpacity(AbstractButton b) {
	b.setOpaque( false );
    }
}


