/*
 * @(#)CloningFeature.java	1.12 98/09/04
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


import java.awt.Component;
import java.awt.Color;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.CompoundBorder;

/**
 * Illustration of how to use the clone feature of the demo.
 */
public class CloningFeature extends JPanel implements Runnable {

    private Thread thread;
    private JTextArea ta;


    public CloningFeature() {

	setLayout(new BorderLayout());
	EmptyBorder eb = new EmptyBorder(5,5,5,5);
	SoftBevelBorder sbb = new SoftBevelBorder(SoftBevelBorder.RAISED);
	setBorder(new CompoundBorder(eb, sbb));

	ta = new JTextArea("Cloning Demonstrated\n\nClicking once on a demo\n");
	ta.setMinimumSize(new Dimension(300,500));
	JScrollPane scroller = new JScrollPane();
	scroller.getViewport().add(ta);
	ta.setFont(new Font("Dialog", Font.PLAIN, 14));
	ta.setForeground(Color.black);
	ta.setBackground(Color.lightGray);
	ta.setEditable(false);

 	add("Center", scroller);

	start();
    }

    public void start() {
	thread = new Thread(this);
	thread.setPriority(Thread.MAX_PRIORITY);
	thread.setName("CloningFeature");
	thread.start();
    }

    public void stop() {
        thread = null;
    }


    private void sleep(long millis) {
	try {
	    Thread.sleep(millis);
	} catch (Exception e) {}
    }


    public void run() {

	sleep(2000);

	DemoGroup dg = Java2Demo.group[Java2Demo.tabbedPane.getSelectedIndex()];
	DemoPanel dp = (DemoPanel) dg.getPanel().getComponent(0);
	if (dp.surface == null) {
	    ta.append("Sorry your zeroth component is not a DemoSurface.");
	    return;
	}

	dg.mouseClicked(new MouseEvent(dp.surface, MouseEvent.MOUSE_CLICKED, 0, 0, 10, 10, 1, false));

	sleep(4000);

	ta.append("Clicking the ToolBar double document button\n");
	sleep(4000);

	dp = (DemoPanel) dg.clonePanels[0].getComponent(0);

	if (dp.toolbar != null) {
	    for (int i = 0; i < 3; i++) {
	        ta.append("   Cloning\n");
	        dp.toolbar.cloneB.doClick();
		sleep(4000);
	    }
	}

	ta.append("Changing attributes \n");

	sleep(4000);

	Component cmps[] = dg.clonePanels[0].getComponents();
	for (int i = 0; i < cmps.length; i++) {
	    dp = (DemoPanel) cmps[i];
	    if (dp.toolbar == null)
		continue;
	    switch (i) {
		case 0 : ta.append("   Changing AntiAliasing\n");
			 dp.toolbar.aliasB.doClick();
			 break;
		case 1 : ta.append("   Changing Composite & Texture\n");
			 dp.toolbar.compositeB.doClick();
		         dp.toolbar.textureB.doClick();
			 break;
		case 2 : ta.append("   Changing Screen\n");
			 dp.toolbar.imgTypeCombo.setSelectedIndex(4);
			 break;
		case 3 : ta.append("   Removing a clone\n");
			 dp.toolbar.cloneB.doClick();
	    }
	    sleep(4000);
	}

	ta.append("\nAll Done!");
    }
}
