/*
 * @(#)ProgressBarDemo.java	1.12 05/11/17
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

/*
 * @(#)ProgressBarDemo.java	1.12 05/11/17
 */


import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.colorchooser.*;
import javax.swing.filechooser.*;
import javax.accessibility.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.io.*;
import java.applet.*;
import java.net.*;

/**
 * JProgressBar Demo
 *
 * @version 1.12 11/17/05
 * @author Jeff Dinkins
 # @author Peter Korn (accessibility support)
 */
public class ProgressBarDemo extends DemoModule {

    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
	ProgressBarDemo demo = new ProgressBarDemo(null);
	demo.mainImpl();
    }

    /**
     * ProgressBarDemo Constructor
     */
    public ProgressBarDemo(SwingSet2 swingset) {
	// Set the title for this demo, and an icon used to represent this
	// demo inside the SwingSet2 app.
	super(swingset, "ProgressBarDemo", "toolbar/JProgressBar.gif");

	createProgressPanel();
    }

    javax.swing.Timer timer = new javax.swing.Timer(18, createTextLoadAction());
    Action loadAction;
    Action stopAction;
    JProgressBar progressBar;
    JTextArea progressTextArea;

    void updateDragEnabled(boolean dragEnabled) {
        progressTextArea.setDragEnabled(dragEnabled);
    }
    
    public void createProgressPanel() {
	getDemoPanel().setLayout(new BorderLayout());

	JPanel textWrapper = new JPanel(new BorderLayout());
	textWrapper.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
	textWrapper.setAlignmentX(LEFT_ALIGNMENT);
	progressTextArea = new MyTextArea();
        
	progressTextArea.getAccessibleContext().setAccessibleName(getString("ProgressBarDemo.accessible_text_area_name"));
	progressTextArea.getAccessibleContext().setAccessibleName(getString("ProgressBarDemo.accessible_text_area_description"));
	textWrapper.add(new JScrollPane(progressTextArea), BorderLayout.CENTER);

	getDemoPanel().add(textWrapper, BorderLayout.CENTER);

	JPanel progressPanel = new JPanel();
	getDemoPanel().add(progressPanel, BorderLayout.SOUTH);

	progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, text.length()) {
	    public Dimension getPreferredSize() {
		return new Dimension(300, super.getPreferredSize().height);
	    }
	};
	progressBar.getAccessibleContext().setAccessibleName(getString("ProgressBarDemo.accessible_text_loading_progress"));

	progressPanel.add(progressBar);
	progressPanel.add(createLoadButton());
	progressPanel.add(createStopButton());
    }

    public JButton createLoadButton() {
	loadAction = new AbstractAction(getString("ProgressBarDemo.start_button")) {
	    public void actionPerformed(ActionEvent e) {
		loadAction.setEnabled(false);
		stopAction.setEnabled(true);
                if (progressBar.getValue() == progressBar.getMaximum()) {
                    progressBar.setValue(0);
                    textLocation = 0;
                    progressTextArea.setText("");
                }
		timer.start();
	    }
	};
	return createButton(loadAction);
    }

    public JButton createStopButton() {
	stopAction = new AbstractAction(getString("ProgressBarDemo.stop_button")) {
	    public void actionPerformed(ActionEvent e) {
		timer.stop();
		loadAction.setEnabled(true);
		stopAction.setEnabled(false);
	    }
	};
	return createButton(stopAction);
    }

    public JButton createButton(Action a) {
	JButton b = new JButton();
	// setting the following client property informs the button to show
	// the action text as it's name. The default is to not show the
	// action text.
	b.putClientProperty("displayActionText", Boolean.TRUE);
	b.setAction(a);
	return b;
    }


    int textLocation = 0;

    String text = getString("ProgressBarDemo.text");

    public Action createTextLoadAction() {
	return new AbstractAction("text load action") {
	    public void actionPerformed (ActionEvent e) {
		if(progressBar.getValue() < progressBar.getMaximum()) {
		    progressBar.setValue(progressBar.getValue() + 1);
		    progressTextArea.append(text.substring(textLocation, textLocation+1));
		    textLocation++;
		} else {
			timer.stop();
			loadAction.setEnabled(true);
			stopAction.setEnabled(false);
		}
	    }
	};
    }


    class MyTextArea extends JTextArea {
        public MyTextArea() {
            super(null, 0, 0);
	    setEditable(false);
	    setText("");
        }

        public float getAlignmentX () {
            return LEFT_ALIGNMENT;
        }
 
        public float getAlignmentY () {
            return TOP_ALIGNMENT;
        }
    }
}


