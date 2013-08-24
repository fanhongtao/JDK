/*
 * @(#)Java2DemoApplet.java	1.22 06/08/25
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */


package java2d;

import java.awt.*;
import javax.swing.*;


/**
 * A demo that shows Java2D features. 
 *
 * Parameters that can be used in the Java2Demo.html file inside
 * the applet tag to customize demo runs :
              <param name="runs" value="10">
              <param name="delay" value="10">
              <param name="ccthread" value=" ">
              <param name="screen" value="5">
              <param name="antialias" value="true">
              <param name="rendering" value="true">
              <param name="texture" value="true">
              <param name="composite" value="true">
              <param name="verbose" value=" ">
              <param name="buffers" value="3,10">
              <param name="verbose" value=" ">
              <param name="zoom" value=" ">
 *
 * @version @(#)Java2DemoApplet.java	1.22 06/08/25
 * @author Brian Lichtenwalter  (Framework, Intro, demos)
 * @author Jim Graham           (demos)
 */
public class Java2DemoApplet extends JApplet {

    public static JApplet applet;


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
        Java2Demo.progressBar.setStringPainted(true);
        Java2Demo.progressLabel.setLabelFor(Java2Demo.progressBar);
        Java2Demo.progressBar.setAlignmentX(CENTER_ALIGNMENT);
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

        String param = null;

        if ((param = getParameter("delay")) != null) {
            RunWindow.delay = Integer.parseInt(param);
        } 
        if (getParameter("ccthread") != null) {
            Java2Demo.demo.ccthreadCB.setSelected(true);
        }
        if ((param = getParameter("screen")) != null) {
            Java2Demo.demo.controls.screenCombo.setSelectedIndex(Integer.parseInt(param));
        } 
        if ((param = getParameter("antialias")) != null) {
            Java2Demo.demo.controls.aliasCB.setSelected(param.endsWith("true"));
        } 
        if ((param = getParameter("rendering")) != null) {
            Java2Demo.demo.controls.renderCB.setSelected(param.endsWith("true"));
        } 
        if ((param = getParameter("texture")) != null) {
            Java2Demo.demo.controls.textureCB.setSelected(param.endsWith("true"));
        } 
        if ((param = getParameter("composite")) != null) {
            Java2Demo.demo.controls.compositeCB.setSelected(param.endsWith("true"));
        } 
        if (getParameter("verbose") != null) {
            Java2Demo.demo.verboseCB.setSelected(true);
        } 
        if ((param = getParameter("columns")) != null) {
            DemoGroup.columns = Integer.parseInt(param);
        } 
        if ((param = getParameter("buffers")) != null) {
            // usage -buffers=3,10
            RunWindow.buffersFlag = true;
            int i = param.indexOf(',');
            String s1 = param.substring(0, i);
            RunWindow.bufBeg = Integer.parseInt(s1);
            s1 = param.substring(i+1, param.length());
            RunWindow.bufEnd = Integer.parseInt(s1);
        } 
        if (getParameter("zoom") != null) {
            RunWindow.zoomCB.setSelected(true);
        }
        if ((param = getParameter("runs")) != null) {
            RunWindow.numRuns = Integer.parseInt(param);
            Java2Demo.demo.createRunWindow();
            RunWindow.runB.doClick();
        } 
        validate();
        repaint();
        requestDefaultFocus();
    }

    private void requestDefaultFocus() {
        Container nearestRoot = getFocusCycleRootAncestor();
        if (nearestRoot != null) {
            nearestRoot.getFocusTraversalPolicy()
                       .getDefaultComponent(nearestRoot)
                       .requestFocus();
        } 
    }

    public void start() {
        Java2Demo.demo.start();
    }

    public void stop() {
        Java2Demo.demo.stop();
    }
}
