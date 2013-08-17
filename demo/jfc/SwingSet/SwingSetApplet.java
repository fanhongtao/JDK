/*
 * @(#)SwingSetApplet.java	1.10 98/08/26
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

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

import java.applet.*;
import SwingSet;

public class SwingSetApplet extends JApplet {
    JPanel panel;
    
    public void init() {

        String vers = System.getProperty("java.version");
        final Applet thisApplet = this;

        if (vers.compareTo("1.1.2") < 0) {
            System.out.println("!!!WARNING: Swing must be run with a " +
                               "1.1.2 or higher version VM!!!");
        }

	// Force SwingSet to come up in the Cross Platform L&F
	try {
	    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
	    // If you want the System L&F instead, comment out the above line and
	    // uncomment the following:
	    // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (Exception exc) {
	    System.err.println("Error loading L&F: " + exc);
	}

        panel = new JPanel();
        getContentPane().add(panel,BorderLayout.CENTER);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        JPanel progressPanel = SwingSet.createVerticalPanel(false);
        panel.add(Box.createGlue());
        panel.add(progressPanel);
        panel.add(Box.createGlue());

        progressPanel.add(Box.createGlue());

        Dimension d = new Dimension(400, 20);
        SwingSet.progressLabel = new JLabel("Loading, please wait...");
        SwingSet.progressLabel.setMaximumSize(d);
        progressPanel.add(SwingSet.progressLabel);
        progressPanel.add(Box.createRigidArea(new Dimension(1,20)));

        SwingSet.progressBar = new JProgressBar();
        SwingSet.progressBar.setMaximumSize(d);
        SwingSet.progressBar.setMinimum(0);
        SwingSet.progressBar.setMaximum(SwingSet.totalPanels);
        SwingSet.progressBar.setValue(0);
        progressPanel.add(SwingSet.progressBar);
        progressPanel.add(Box.createGlue());
        progressPanel.add(Box.createGlue());

        // show the panel
        Rectangle ab = getContentPane().getBounds();
        panel.setPreferredSize(new Dimension(ab.width,ab.height));
        getContentPane().add(panel,BorderLayout.CENTER);
        validate();
        setVisible(true);

        SwingSet sw = new SwingSet(thisApplet);
        getContentPane().remove(panel);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(sw, BorderLayout.CENTER);
        validate();
        repaint();
        sw.requestDefaultFocus();
    }
}

