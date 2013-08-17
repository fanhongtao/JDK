/*
 * @(#)SwingSetApplet.java	1.12 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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

