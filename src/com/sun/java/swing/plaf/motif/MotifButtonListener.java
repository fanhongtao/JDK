/*
 * @(#)MotifButtonListener.java	1.16 01/12/03
 *
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
 * @version 1.16 12/03/01
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


