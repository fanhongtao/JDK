/*
 * @(#)RunWindow.java	1.27 04/07/26
 * 
 * Copyright (c) 2004 Sun Microsystems, Inc. All Rights Reserved.
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
 * @(#)RunWindow.java	1.27 04/07/26
 */

package java2d;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import javax.swing.border.*;
import java.util.Date;


/**
 * A separate window for running the Java2Demo.  Go from tab to tab or demo to
 * demo.
 */
public class RunWindow extends JPanel implements Runnable, ActionListener {

    static JButton runB;
    static int delay = 10;
    static int numRuns = 20;
    static boolean exit;
    static JCheckBox zoomCB = new JCheckBox("Zoom");
    static JCheckBox printCB = new JCheckBox("Print");
    static boolean buffersFlag;
    static int bufBeg, bufEnd;

    private JTextField delayTextField, runsTextField;
    private Thread thread;
    private JProgressBar pb;



    public RunWindow() {

        setLayout(new GridBagLayout());
        EmptyBorder eb = new EmptyBorder(5,5,5,5);
        setBorder(new CompoundBorder(eb, new BevelBorder(BevelBorder.LOWERED)));

        Font font = new Font("serif", Font.PLAIN, 10);

        runB = new JButton("Run");
        runB.setBackground(Color.green);
        runB.addActionListener(this);
        runB.setMinimumSize(new Dimension(70,30));
        Java2Demo.addToGridBag(this, runB, 0, 0, 1, 1, 0.0, 0.0);

        pb = new JProgressBar();
        pb.setPreferredSize(new Dimension(100,30));
        pb.setMinimum(0);
        Java2Demo.addToGridBag(this, pb, 1, 0, 2, 1, 1.0, 0.0);

        JPanel p1 = new JPanel(new GridLayout(2,2));

        JPanel p2 = new JPanel();
        JLabel l = new JLabel("Runs:");
        l.setFont(font);
        l.setForeground(Color.black);
        p2.add(l);
        p2.add(runsTextField = new JTextField(String.valueOf(numRuns)));
        runsTextField.setPreferredSize(new Dimension(30,20));
        runsTextField.addActionListener(this);
        p1.add(p2);
        p2 = new JPanel();
        l = new JLabel("Delay:");
        l.setFont(font);
        l.setForeground(Color.black);
        p2.add(l);
        p2.add(delayTextField = new JTextField(String.valueOf(delay)));
        delayTextField.setPreferredSize(new Dimension(30,20));
        delayTextField.addActionListener(this);
        p1.add(p2);

        zoomCB.setHorizontalAlignment(JButton.CENTER);
        zoomCB.setFont(font);
        printCB.setFont(font);
        p1.add(zoomCB); 
        p1.add(printCB);
        printCB.addActionListener(this);
        Java2Demo.addToGridBag(this, p1, 0, 1, 3, 1, 1.0, 1.0);
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(printCB)) {
            Java2Demo.printCB.setSelected(printCB.isSelected());
        } else if (e.getSource().equals(delayTextField)) {
            delay = Integer.parseInt(delayTextField.getText().trim());
        } else if (e.getSource().equals(runsTextField)) {
            numRuns = Integer.parseInt(runsTextField.getText().trim());
        } else if (e.getActionCommand() == "Run") {
            runB.setText("Stop");
            runB.setBackground(Color.red);
            start();
        } else if (e.getActionCommand() == "Stop") {
            stop();
        }
    }


    public void start() {
        thread = new Thread(this);
        thread.setPriority(Thread.NORM_PRIORITY+1);
        thread.setName("RunWindow");
        thread.start();
    }


    public synchronized void stop() {
        if (thread != null) {
            thread.interrupt();
        }
        thread = null;
        notifyAll();
    }


    public void sleepPerTab() {
        for (int j = 0; j < delay+1 && thread != null; j++) {
            for (int k = 0; k < 10 && thread != null; k++) {
                try {
                    thread.sleep(100);
                } catch (Exception e) { }
            }
            pb.setValue(pb.getValue() + 1);
            pb.repaint();
        }
    }


    private void printDemo(DemoGroup dg) {
        if (!Java2Demo.controls.toolBarCB.isSelected()) {
            Java2Demo.controls.toolBarCB.setSelected(true);
            dg.revalidate();
            try { thread.sleep(2000); } catch (Exception e) { }
        }
        JPanel p = dg.getPanel();
        for (int j = 0; j < p.getComponentCount(); j++) {
            DemoPanel dp = (DemoPanel) p.getComponent(j);
            if (dp.tools != null) {
               if (dp.surface.animating != null) {
                   if (dp.surface.animating.thread != null) {
                       dp.tools.startStopB.doClick();
                       try { thread.sleep(999); } catch (Exception e) {}
                   }
               }
               dp.tools.printB.doClick();
               try { thread.sleep(999); } catch (Exception e) {}
            }
        }
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
                DemoGroup dg = null;
                if (i != 0) {
                    dg = Java2Demo.group[i-1];
                    dg.invalidate();
                }
                Java2Demo.tabbedPane.setSelectedIndex(i);
                if (i != 0 && (zoomCB.isSelected() || buffersFlag)) {
                    DemoPanel dp = (DemoPanel) dg.getPanel().getComponent(0);
                    if (dg.tabbedPane == null && dp.surface != null) {
                        dg.mouseClicked(new MouseEvent(dp.surface, MouseEvent.MOUSE_CLICKED, 0, 0, 10, 10, 1, false));
                        try {thread.sleep(999);} catch (Exception e) {}
                    }
                    for (int j = 1; j < dg.tabbedPane.getTabCount() && thread != null; j++) {
                        pb.setValue(0);
                        pb.setMaximum(delay);
                        dg.tabbedPane.setSelectedIndex(j);
                        JPanel p = dg.getPanel();
                        if (buffersFlag && p.getComponentCount() == 1) {
                            dp = (DemoPanel) p.getComponent(0);
                            if (dp.surface.animating != null) {
                                dp.surface.animating.stop();
                            }
                            for (int k = bufBeg; k <= bufEnd && thread != null; k++) {
                                 dp.tools.cloneB.doClick();
                                 try {thread.sleep(500);} catch (Exception e) {}
                                 int n = p.getComponentCount();
                                 DemoPanel clone = (DemoPanel)p.getComponent(n-1);
                                 if (clone.surface.animating != null) {
                                     clone.surface.animating.stop();
                                 }
                                 clone.tools.issueRepaint = true;
                                 clone.tools.screenCombo.setSelectedIndex(k);
                                 clone.tools.issueRepaint = false;
                            }
                        }
                        if (printCB.isSelected()) {
                            printDemo(dg);
                        }
                        sleepPerTab();
                    }
                } else if (i != 0 && printCB.isSelected()) {
                    printDemo(dg);
                    sleepPerTab();
                } else {
                    sleepPerTab();
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
