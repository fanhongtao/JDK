/*
 * @(#)SwingApplet.java	1.11 98/08/26
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

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.applet.*;
import javax.swing.*;

/**
 * A very simple applet.
 */
public class SwingApplet extends JApplet {

    JButton button;

    public void init() {

	// Force SwingApplet to come up in the System L&F
	String laf = UIManager.getSystemLookAndFeelClassName();
	try {
	    UIManager.setLookAndFeel(laf);
	    // If you want the Cross Platform L&F instead, comment out the above line and
	    // uncomment the following:
	    // UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
	} catch (UnsupportedLookAndFeelException exc) {
	    System.err.println("Warning: UnsupportedLookAndFeel: " + laf);
	} catch (Exception exc) {
	    System.err.println("Error loading " + laf + ": " + exc);
	}

        getContentPane().setLayout(new FlowLayout());
        button = new JButton("Hello, I'm a Swing Button!");
        getContentPane().add(button);
    }

    public void stop() {
        if (button != null) {
            getContentPane().remove(button);
            button = null;
        }
    }
}


