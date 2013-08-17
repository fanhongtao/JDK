/*
 * @(#)RunWindow.java	1.16 98/09/13
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


import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JButton;
import javax.swing.JProgressBar;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.border.*;
import java.util.Date;


/**
 * A separate window for running the Java2Demo.  Go from tab to tab forever.
 */
public class RunWindow extends JPanel implements Runnable, ActionListener, ChangeListener {

    private Thread thread;
    private JProgressBar pb;

    static JSlider jslider;
    static JButton runB;
    static int delay = 10;
    static int numRuns = 20;
    static boolean exit;


    public RunWindow() {

        setLayout(new GridBagLayout());
        EmptyBorder eb = new EmptyBorder(5,5,5,5);
        setBorder(new CompoundBorder(eb, new BevelBorder(BevelBorder.LOWERED)));

        runB = new JButton("Run");
        runB.setBackground(Color.green);
        runB.addActionListener(this);
        runB.setMinimumSize(new Dimension(66,35));
        Java2Demo.addToGridBag(this, runB, 0, 0, 1, 1, 0.0, 0.0);

        jslider = new JSlider(JSlider.HORIZONTAL, 5, 60, delay);
        jslider.setPaintTicks(true);
        jslider.setMajorTickSpacing(10);
        jslider.setMinorTickSpacing(5);
        jslider.addChangeListener(this);
        TitledBorder tb = new TitledBorder(new EtchedBorder());
        tb.setTitle("Delay = " + String.valueOf(delay));
        jslider.setBorder(tb);
        jslider.setMinimumSize(new Dimension(60,35));
        Java2Demo.addToGridBag(this, jslider, 1, 0, 1, 1, 1.0, 1.0);

        pb = new JProgressBar();
        pb.setMinimum(0);
        Java2Demo.addToGridBag(this, pb, 0, 1, 2, 1, 1.0, 1.0);
    }


    public void stateChanged(ChangeEvent e) {
        setDelay(jslider.getValue());
    }


    static void setDelay(int delay) {
        if (delay < jslider.getMinimum()) {
            jslider.setMinimum(delay);
        }
        if (delay > jslider.getMaximum()) {
            jslider.setMaximum(delay);
        }
        RunWindow.delay = delay;
        TitledBorder tb = (TitledBorder) jslider.getBorder();
        tb.setTitle("Delay = " + String.valueOf(delay));
        jslider.validate();
        jslider.repaint();
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "Run") {
            runB.setText("Stop");
            runB.setBackground(Color.red);
            start();
        } else if (e.getActionCommand() == "Stop") {
            stop();
        }
    }


    public void start() {
        thread = new Thread(this);
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.setName("RunWindow");
        thread.start();
    }


    public synchronized void stop() {
        thread = null;
        notifyAll();
    }


    public void run() {

        System.out.println("\nJava2D Demo RunWindow : " + 
                numRuns + " Runs, " + 
                delay + " second delay between tabs\n" + 
                "java version: " + System.getProperty("java.version") + 
                "\n" + System.getProperty("os.name") + " " + 
                System.getProperty("os.version") + "\n");
        Runtime r = Runtime.getRuntime();

        for (int runNum = 0; runNum < numRuns && thread != null; runNum++) {

            Date d = new Date();
            System.out.print("#" + runNum + " " + d.toString() + ", ");
            r.gc();
            float freeMemory = (float) r.freeMemory();
            float totalMemory = (float) r.totalMemory();
            System.out.println(((totalMemory - freeMemory)/1024) + "K used");

            for (int i = 0; i < Java2Demo.tabbedPane.getTabCount() && thread != null; i++) {
                pb.setValue(0);
                pb.setMaximum(delay);
                Java2Demo.tabbedPane.setSelectedIndex(i);
                for (int j = 0; j < delay+1 && thread != null; j++) {
                    for (int k = 0; k < 10 && thread != null; k++) {
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) { e.printStackTrace(); }
                    }
                    pb.setValue(pb.getValue() + 1);
                    pb.repaint();
                }
            }
            if (runNum+1 == numRuns) {
                System.out.println("Finished.");
                if (exit && thread != null) {
                    System.out.println("System.exit(0).");
                    System.exit(0);
                }
            }
        }
        thread = null;
        runB.setText("Run");
        runB.setBackground(Color.green);
        pb.setValue(0);
    }
}
