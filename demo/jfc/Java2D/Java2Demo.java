/*
 * @(#)Java2Demo.java	1.25 98/09/21
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
import java.awt.event.*;
import java.net.URL;
import java.io.File;
import javax.swing.*;
import javax.swing.border.*;


/**
 * A demo that shows Java2D features.
 *
 * @version @(#)Java2Demo.java	1.25 98/09/21
 * @author Brian Lichtenwalter  (framework, demos)
 * @author Jim Graham           (demos)
 */
public class Java2Demo extends JPanel implements ItemListener, ActionListener
{

    static Java2Demo demo;
    static Controls controls;
    static MemoryMonitor memorymonitor;
    static PerformanceMonitor performancemonitor;
    static JTabbedPane tabbedPane;
    static JLabel progressLabel;
    static JProgressBar progressBar;
    static DemoGroup group[];

    private JCheckBoxMenuItem memoryCB, perfCB;
    private JCheckBoxMenuItem controlsCB;
    private JMenuItem runMI, cloneMI, fileMI;
    private RunWindow runwindow = new RunWindow();
    private CloningFeature cloningfeature;
    private JFrame rf, cf;
    private GlobalPanel gp;


    /**
     * Construct the Java2D Demo.
     */
    public Java2Demo() {

        setLayout(new BorderLayout());
        setBorder(new EtchedBorder());

        add(createMenuBar(), BorderLayout.NORTH);

        new DemoImages();

        controls = new Controls();
        memorymonitor = new MemoryMonitor();
        performancemonitor = new PerformanceMonitor();

        gp = new GlobalPanel();

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("serif", Font.PLAIN, 12));
        tabbedPane.addChangeListener(gp);

        // Retrieve directory names in demos directory
        URL url = Java2Demo.class.getResource("demos");
        File demosDir = new File(url.getFile());

        if (demosDir != null && demosDir.isDirectory()) {
            String list[] = demosDir.list();
            group = new DemoGroup[list.length];
            progressBar.setMaximum(list.length);
            sort(list);
            for (int i = 0; i < list.length; i++) {
                File dirs = new File(demosDir.getPath(), list[i]);
                if (dirs != null && dirs.isDirectory()) {
                    progressLabel.setText("Loading demos." + dirs.getName());
                    group[i] = new DemoGroup(dirs.getName());
                    if (i == 0) {
        // Make the "always visible" global panel the first child so
        // that it always remains "on top" of the z-order
                        tabbedPane.addTab(dirs.getName(), gp);
                    } else {
                        tabbedPane.addTab(dirs.getName(), null);
                    }
                    progressBar.setValue(progressBar.getValue() + 1);
                }
            }
        } else {
            System.out.println("Fatal, couldn't find demos directory. ");
            System.exit(1);
        }

        add(tabbedPane, BorderLayout.CENTER);
    }


    private JMenuBar createMenuBar() {

        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        JMenuBar menuBar = new JMenuBar();

        if (Java2DemoApplet.applet == null) {
            JMenu file = (JMenu) menuBar.add(new JMenu("File"));
            fileMI = (JMenuItem) file.add(new JMenuItem("Exit"));
            fileMI.addActionListener(this);
        }

        JMenu options = (JMenu) menuBar.add(new JMenu("Options"));

        controlsCB = (JCheckBoxMenuItem) options.add(
                new JCheckBoxMenuItem("Global Controls", true));
        controlsCB.addItemListener(this);

        memoryCB = (JCheckBoxMenuItem) options.add(
                new JCheckBoxMenuItem("Memory Monitor", true));
        memoryCB.addItemListener(this);

        perfCB = (JCheckBoxMenuItem) options.add(
                new JCheckBoxMenuItem("Performance Monitor", true));
        perfCB.addItemListener(this);

        options.add(new JSeparator());

        runMI = (JMenuItem) options.add(new JMenuItem("Run Window"));
        runMI.addActionListener(this);

        cloneMI = (JMenuItem) options.add(new JMenuItem("Cloning Feature"));
        cloneMI.addActionListener(this);

        return menuBar;
    }


    /**
     * sort directory names
     */ 
    static void sort(String a[]) {
        for (int i = a.length; --i>=0; ) {
            boolean swapped = false;
            for (int j = 0; j<i; j++) {
                if (a[j].compareTo(a[j+1]) > 0) {
                    String T = a[j];
                    a[j] = a[j+1];
                    a[j+1] = T;
                    swapped = true;
                }
            }
            if (!swapped)
                return;
        }
    }


    private void createRunWindow() {
        if (rf != null) {
            return;
        }
        WindowListener l = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                runwindow.stop(); rf.dispose();
            }
            public void windowClosed(WindowEvent e) { 
                rf = null;
            }
        };
        rf = new JFrame("Run");
        rf.addWindowListener(l);
        rf.getContentPane().add("Center", runwindow);
        rf.pack();
        rf.setSize(new Dimension(240,140));
        rf.show();
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(fileMI)) {
            System.exit(0);
        } else if (e.getSource().equals(runMI)) {
            createRunWindow();
        } else if (e.getSource().equals(cloneMI)) {
            if (cloningfeature == null) {
                cloningfeature = new CloningFeature();
                WindowListener l = new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        cloningfeature.stop(); cf.dispose();
                    }
                    public void windowClosed(WindowEvent e) {
                        cloningfeature = null;
                    }
                };
                cf = new JFrame("Cloning Demo");
                cf.addWindowListener(l);
                cf.getContentPane().add("Center", cloningfeature);
                cf.pack();
                cf.setSize(new Dimension(320,300));
                cf.show();
            }
        }
    }


    public void itemStateChanged(ItemEvent e) {
        if (e.getSource().equals(controlsCB)) {
            if (controls.isVisible()) {
                controls.setVisible(false);
                Component cmps[] = controls.texturechooser.getComponents();
                for (int i = 0; i < cmps.length; i++) {
                    cmps[i].setVisible(false);
                }
            } else {
                controls.setVisible(true);
                Component cmps[] = controls.texturechooser.getComponents();
                for (int i = 0; i < cmps.length; i++) {
                    cmps[i].setVisible(true);
                }
            }
        } else if (e.getSource().equals(memoryCB)) {
            if (memorymonitor.isVisible()) {
                memorymonitor.setVisible(false);
                memorymonitor.mc.setVisible(false);
                memorymonitor.mc.stop();
            } else {
                memorymonitor.setVisible(true);
                memorymonitor.mc.setVisible(true);
                memorymonitor.mc.start();
            }
        } else if (e.getSource().equals(perfCB)) {
            if (performancemonitor.isVisible()) {
                performancemonitor.setVisible(false);
                performancemonitor.mc.setVisible(false);
                performancemonitor.mc.stop();
            } else {
                performancemonitor.setVisible(true);
                performancemonitor.mc.setVisible(true);
                performancemonitor.mc.start();
            }
        }
        validate();
    }


    public void start() {
        group[tabbedPane.getSelectedIndex()].setup(false);
        if (memorymonitor.mc.thread == null && memoryCB.getState()) {
            memorymonitor.mc.start();
        }
        if (performancemonitor.mc.thread == null && perfCB.getState()) {
            performancemonitor.mc.start();
        }
    }


    public void stop() {
        memorymonitor.mc.stop();
        performancemonitor.mc.stop();
        int i = tabbedPane.getSelectedIndex();
        group[i].shutDown(group[i].getPanel());
    }


    static void addToGridBag(JPanel panel, Component comp,
            int x, int y, int w, int h, double weightx, double weighty) {

        GridBagLayout gbl = (GridBagLayout) panel.getLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = w;
        c.gridheight = h;
        c.weightx = weightx;
        c.weighty = weighty;
        panel.add(comp);
        gbl.setConstraints(comp, c);
    }


    public static void main(String args[]) {

        JFrame frame = new JFrame("Java2D Demo");
        frame.getAccessibleContext().setAccessibleDescription("A sample application to demonstrate Java2D features");

        JOptionPane.setRootFrame(frame);

        JPanel progressPanel = new JPanel() {
            public Insets getInsets() {
                return new Insets(40,30,20,30);
            }
        };
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        frame.getContentPane().add(progressPanel, BorderLayout.CENTER);

        Dimension d = new Dimension(400, 20);
        progressLabel = new JLabel("Loading, please wait...");
        progressLabel.setAlignmentX(CENTER_ALIGNMENT);
        progressLabel.setMaximumSize(d);
        progressLabel.setPreferredSize(d);
        progressPanel.add(progressLabel);
        progressPanel.add(Box.createRigidArea(new Dimension(1,20)));

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressLabel.setLabelFor(progressBar);
        progressBar.setAlignmentX(CENTER_ALIGNMENT);
        progressBar.setMinimum(0);
        progressBar.setValue(0);
        progressBar.getAccessibleContext().setAccessibleName("Java2D loading progress");
        progressPanel.add(progressBar);

        int INITIAL_WIDTH = 400;
        int INITIAL_HEIGHT = 200;
        frame.setSize(INITIAL_WIDTH, INITIAL_HEIGHT);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(screenSize.width/2 - INITIAL_WIDTH/2,
                          screenSize.height/2 - INITIAL_HEIGHT/2);
        frame.show();

        frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        demo = new Java2Demo();

        WindowListener l = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
            public void windowDeiconified(WindowEvent e) { demo.start(); }
            public void windowIconified(WindowEvent e) { demo.stop(); }
        };
        frame.addWindowListener(l);

        frame.getContentPane().removeAll();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(demo, BorderLayout.CENTER);
        int WIDTH = 730;
        int HEIGHT = 560;
        frame.setLocation(screenSize.width/2 - WIDTH/2,
                          screenSize.height/2 - HEIGHT/2);

        frame.setSize(WIDTH, HEIGHT);
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        frame.validate();
        frame.repaint();
        demo.requestDefaultFocus();

        for (int i = 0; i < args.length; i++) {
            String s = args[i].substring(args[i].indexOf('=')+1);
            if (args[i].startsWith("-runs=")) {
                demo.createRunWindow();
                RunWindow.numRuns = Integer.parseInt(s);
                RunWindow.exit = true;
            } else if (args[i].startsWith("-delay=")) {
                RunWindow.setDelay(Integer.parseInt(s));
            } else if (args[i].startsWith("-screen=")) {
                demo.controls.imgTypeCombo.setSelectedIndex(Integer.parseInt(s));
            } else if (args[i].startsWith("-antialias=")) {
                demo.controls.aliasCB.setSelected(s.endsWith("true"));
            } else if (args[i].startsWith("-rendering=")) {
                demo.controls.renderCB.setSelected(s.endsWith("true"));
            } else if (args[i].startsWith("-texture=")) {
                demo.controls.textureCB.setSelected(s.endsWith("true"));
            } else if (args[i].startsWith("-composite=")) {
                demo.controls.compositeCB.setSelected(s.endsWith("true"));
            } else if (args[i].startsWith("-verbose")) {
                demo.controls.verboseCB.setSelected(true);
            }
        }

        if (RunWindow.exit) {
            RunWindow.runB.doClick();
        }
    }
}
