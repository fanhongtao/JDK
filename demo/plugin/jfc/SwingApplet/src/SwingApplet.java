/*
 * @(#)SwingApplet.java	1.14 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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


