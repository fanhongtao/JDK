/*
 * @(#)GlobalPanel.java	1.9 98/09/21
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


import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * Panel that holds the Demo groups, Controls and Monitors for each tab.
 * It's a special "always visible" panel for the Controls, MemoryMonitor &
 * PerformanceMonitor.
 */
public class GlobalPanel extends JPanel implements ChangeListener {


    public GlobalPanel() {
        setLayout(new BorderLayout());
        JPanel p = new JPanel(new GridBagLayout());
        EmptyBorder eb = new EmptyBorder(5,0,5,5);
        BevelBorder bb = new BevelBorder(BevelBorder.LOWERED);
        p.setBorder(new CompoundBorder(eb,bb));
        Java2Demo.addToGridBag(p,Java2Demo.controls,0,0,1,1,0,0);
        Java2Demo.addToGridBag(p,Java2Demo.memorymonitor,0,1,1,1,0,0);
        Java2Demo.addToGridBag(p,Java2Demo.performancemonitor,0,2,1,1,0,0);
        add(p, BorderLayout.EAST);
    }

    private int index;

    public void stateChanged(ChangeEvent e) {
        Java2Demo.group[index].shutDown(Java2Demo.group[index].getPanel());
        remove(Java2Demo.group[index]);
        index = Java2Demo.tabbedPane.getSelectedIndex();
        add(Java2Demo.group[index]);
        Java2Demo.group[index].setup(false);
        validate();
    }
}
