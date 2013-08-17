/*
 * @(#)Java2DemoApplet.java	1.9 98/09/13
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


import java.awt.*;
import javax.swing.*;


public class Java2DemoApplet extends JApplet {

    static JApplet applet;

    public void init() {

        applet = this;

        JPanel panel = new JPanel();
        getContentPane().add(panel,BorderLayout.CENTER);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        JPanel progressPanel = new JPanel() {
            public Insets getInsets() {
                return new Insets(40,30,20,30);
            }
        };
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));

        panel.add(Box.createGlue());
        panel.add(progressPanel);
        panel.add(Box.createGlue());

        progressPanel.add(Box.createGlue());

        Dimension d = new Dimension(400, 20);
        Java2Demo.progressLabel = new JLabel("Loading, please wait...");
        Java2Demo.progressLabel.setMaximumSize(d);
        progressPanel.add(Java2Demo.progressLabel);
        progressPanel.add(Box.createRigidArea(new Dimension(1,20)));

        Java2Demo.progressBar = new JProgressBar();
        Java2Demo.progressBar.setMaximumSize(d);
        Java2Demo.progressBar.setMinimum(0);
        Java2Demo.progressBar.setValue(0);
        progressPanel.add(Java2Demo.progressBar);
        progressPanel.add(Box.createGlue());
        progressPanel.add(Box.createGlue());

        Rectangle ab = getContentPane().getBounds();
        panel.setPreferredSize(new Dimension(ab.width,ab.height));
        getContentPane().add(panel,BorderLayout.CENTER);
        validate();
        setVisible(true);

        Java2Demo.demo = new Java2Demo();
        getContentPane().remove(panel);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(Java2Demo.demo, BorderLayout.CENTER);
        validate();
        repaint();
        Java2Demo.demo.requestDefaultFocus();
    }

    public void start() {
        Java2Demo.demo.start();
    }

    public void stop() {
        Java2Demo.demo.stop();
    }
}
