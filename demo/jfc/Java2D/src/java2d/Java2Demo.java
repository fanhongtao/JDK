/*
 * @(#)Java2Demo.java	1.50 06/08/29
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
 * @(#)Java2Demo.java	1.50 06/08/29
 */


package java2d;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import javax.swing.*;
import javax.swing.border.*;

import static java2d.CustomControlsContext.State.*;


/**
 * A demo that shows Java 2D(TM) features.
 *
 * @version @(#)Java2Demo.java	1.33 99/08/16
 * @author Brian Lichtenwalter  (Framework, Intro, demos)
 * @author Jim Graham           (demos)
 */
public class Java2Demo extends JPanel implements ItemListener, ActionListener
{

    static Java2Demo demo;
    static GlobalControls controls;
    static MemoryMonitor memorymonitor;
    static PerformanceMonitor performancemonitor;
    static JTabbedPane tabbedPane;
    static JLabel progressLabel;
    static JProgressBar progressBar;
    static DemoGroup group[];
    static JCheckBoxMenuItem verboseCB;
    static JCheckBoxMenuItem ccthreadCB;
    static JCheckBoxMenuItem printCB = new JCheckBoxMenuItem("Default Printer");
    static Color backgroundColor;
    static JCheckBoxMenuItem memoryCB, perfCB;
    static Intro intro;
    static String[][] demos = 
    { 
        {"Arcs_Curves", "Arcs", "BezierAnim", "Curves", "Ellipses"},
        {"Clipping", "Areas", "ClipAnim", "Intersection", "Text"},
        {"Colors", "BullsEye", "ColorConvert", "Rotator3D"},
        {"Composite", "ACimages", "ACrules", "FadeAnim"},
        {"Fonts", "AttributedStr", "Highlighting", "Outline", "Tree"},
        {"Images", "DukeAnim", "ImageOps", "JPEGFlip", "WarpImage"},
        {"Lines", "Caps", "Dash", "Joins", "LineAnim"},
        {"Mix", "Balls", "BezierScroller", "Stars3D"},
        {"Paint", "GradAnim", "Gradient", "Texture", "TextureAnim"},
        {"Paths", "Append", "CurveQuadTo", "FillStroke", "WindingRule"},
        {"Transforms", "Rotate", "SelectTx", "TransformAnim"}
    };

    private JCheckBoxMenuItem controlsCB;
    private JMenuItem runMI, cloneMI, fileMI, backgMI;
    private JMenuItem ccthreadMI, verboseMI;
    private RunWindow runwindow;
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

        // hard coding 14 = 11 demo dirs + images + fonts + Intro
        progressBar.setMaximum(13);   
        progressLabel.setText("Loading images");
        new DemoImages();
        progressBar.setValue(progressBar.getValue() + 1);
        progressLabel.setText("Loading fonts");
        new DemoFonts();
        progressBar.setValue(progressBar.getValue() + 1);
        progressLabel.setText("Loading Intro");
        intro = new Intro();
        progressBar.setValue(progressBar.getValue() + 1);
        UIManager.put("Button.margin", new Insets(0,0,0,0));
       
        controls = new GlobalControls();
        memorymonitor = new MemoryMonitor();
        performancemonitor = new PerformanceMonitor();

        GlobalPanel gp = new GlobalPanel();

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("serif", Font.PLAIN, 12));
        tabbedPane.addTab("", new J2DIcon(), gp);
        tabbedPane.addChangeListener(gp);

        group = new DemoGroup[demos.length];
        for (int i = 0; i < demos.length; i++) {
            progressLabel.setText("Loading demos." + demos[i][0]);
            group[i] = new DemoGroup(demos[i][0]);
            tabbedPane.addTab(demos[i][0], null);
            progressBar.setValue(progressBar.getValue() + 1);
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

        ccthreadCB = (JCheckBoxMenuItem) options.add(
                new JCheckBoxMenuItem("Custom Controls Thread"));
        ccthreadCB.addItemListener(this);

        printCB = (JCheckBoxMenuItem) options.add(printCB);

        verboseCB = (JCheckBoxMenuItem) options.add(
                new JCheckBoxMenuItem("Verbose"));

        options.add(new JSeparator());

        backgMI = (JMenuItem) options.add(new JMenuItem("Background Color"));
	backgMI.addActionListener(this);

        runMI = (JMenuItem) options.add(new JMenuItem("Run Window"));
        runMI.addActionListener(this);

        cloneMI = (JMenuItem) options.add(new JMenuItem("Cloning Feature"));
        cloneMI.addActionListener(this);

        return menuBar;
    }


    public void createRunWindow() {
        if (rf != null) {
            rf.toFront();
            return;
        }
        runwindow = new RunWindow();
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
        if (Java2DemoApplet.applet == null) {
            rf.setSize(new Dimension(200,125));
        } else {
            rf.setSize(new Dimension(200,150));
        }
        rf.setVisible(true);
    }


    public void startRunWindow() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                runwindow.doRunAction();
            }
        });
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
                cf.setSize(new Dimension(320,330));
                cf.setVisible(true);
            } else {
                cf.toFront();
            }
        } else if (e.getSource().equals(backgMI)) {
	    backgroundColor = 
                JColorChooser.showDialog(this, "Background Color", Color.white);
            for (int i = 1; i < tabbedPane.getTabCount(); i++) {
                JPanel p = group[i-1].getPanel();
                for (int j = 0; j < p.getComponentCount(); j++) {
                    DemoPanel dp = (DemoPanel) p.getComponent(j);
                    if (dp.surface != null) {
                        dp.surface.setBackground(backgroundColor);
                    }
                } 
            } 
        }
    }


    public void itemStateChanged(ItemEvent e) {
        if (e.getSource().equals(controlsCB)) {
            boolean newVisibility = !controls.isVisible();
            controls.setVisible(newVisibility);
            for (Component cmp : controls.texturechooser.getComponents()) {
                cmp.setVisible(newVisibility);
            }
        } else if (e.getSource().equals(memoryCB)) {
            if (memorymonitor.isVisible()) {
                memorymonitor.setVisible(false);
                memorymonitor.surf.setVisible(false);
                memorymonitor.surf.stop();
            } else {
                memorymonitor.setVisible(true);
                memorymonitor.surf.setVisible(true);
                memorymonitor.surf.start();
            }
        } else if (e.getSource().equals(perfCB)) {
            if (performancemonitor.isVisible()) {
                performancemonitor.setVisible(false);
                performancemonitor.surf.setVisible(false);
                performancemonitor.surf.stop();
            } else {
                performancemonitor.setVisible(true);
                performancemonitor.surf.setVisible(true);
                performancemonitor.surf.start();
            }
        } else if (e.getSource().equals(ccthreadCB)) {
            CustomControlsContext.State state =
                ccthreadCB.isSelected() ? START : STOP;
            if (tabbedPane.getSelectedIndex() != 0) {
                JPanel p = group[tabbedPane.getSelectedIndex()-1].getPanel();
                for (int i = 0; i < p.getComponentCount(); i++) {
                    DemoPanel dp = (DemoPanel) p.getComponent(i);
                    if (dp.ccc != null) {
                        dp.ccc.handleThread(state);
                    }
                } 
            }
        }
        revalidate();
    }


    public void start() {
        if (tabbedPane.getSelectedIndex() == 0) {
            intro.start();
        } else {
            group[tabbedPane.getSelectedIndex()-1].setup(false);
            if (memorymonitor.surf.thread == null && memoryCB.getState()) {
                memorymonitor.surf.start();
            }
            if (performancemonitor.surf.thread == null && perfCB.getState()) {
                performancemonitor.surf.start();
            }
        }
    }


    public void stop() {
        if (tabbedPane.getSelectedIndex() == 0) {
            intro.stop();
        } else {
            memorymonitor.surf.stop();
            performancemonitor.surf.stop();
            int i = tabbedPane.getSelectedIndex()-1;
            group[i].shutDown(group[i].getPanel());
        }
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


    /**
     * The Icon for the Intro tab.
     */
    static class J2DIcon implements Icon {

        private static Color myBlue  = new Color(94, 105, 176); 
        private static Color myBlack = new Color(20,  20,  20); 
        private static Font font = new Font("serif", Font.BOLD, 12);
        private FontRenderContext frc = new FontRenderContext(null,true,true);
        private TextLayout tl = new TextLayout("Java2D", font, frc);

        public void paintIcon(Component c, Graphics g, int x, int y ) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setFont(font);
            if (tabbedPane.getSelectedIndex() == 0) {
                g2.setColor(myBlue);
            } else {
                g2.setColor(myBlack);
            }
            tl.draw(g2, x, y + 15);
        }

        public int getIconWidth() {
            return 40;
        }

        public int getIconHeight() {
            return 22;
        }
    }



    public static void main(String args[]) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-h") || args[i].startsWith("-help")) {
                String s = "\njava -jar Java2Demo.jar -runs=5 -delay=5 -screen=5 " +
                           "-antialias=true -rendering=true -texture=true " +
                           "-composite=true -verbose -print -columns=3 " +
                           "-buffers=5,10 -ccthread -zoom -maxscreen \n";
                System.out.println(s);
                s = "    -runs=5       Number of runs to execute\n" +
                    "    -delay=5      Sleep amount between tabs\n" +
                    "    -antialias=   true or false for antialiasing\n" +
                    "    -rendering=   true or false for quality or speed\n" + 
                    "    -texture=     true or false for texturing\n" +
                    "    -composite=   true or false for compositing\n" +
                    "    -verbose      output Surface graphic states \n" +
                    "    -print        during run print the Surface, " +
                    "use the Default Printer\n" +
                    "    -columns=3    # of columns to use in clone layout \n" +
                    "    -screen=3     demos all use this screen type \n" +
                    "    -buffers=5,10 during run - clone to see screens " +
                    "five through ten\n" +
                    "    -ccthread     Invoke the Custom Controls Thread \n" +
                    "    -zoom         mouseClick on surface for zoom in  \n" +
                    "    -maxscreen    take up the entire monitor screen \n";
                System.out.println(s);
                s = "Examples : \n" +
                    "    Print all of the demos : \n" +
                    "        java -jar Java2Demo.jar -runs=1 -delay=60 -print \n" +
                    "    Run zoomed in with custom control thread \n" +
                    "        java -jar Java2Demo.jar -runs=10 -zoom -ccthread\n"; 
                System.out.println(s);
                System.exit(0);
            } else if (args[i].startsWith("-delay=")) {
                String s = args[i].substring(args[i].indexOf('=')+1);
                RunWindow.delay = Integer.parseInt(s);
            }
        }

        JFrame frame = new JFrame("Java 2D(TM) Demo");
        frame.getAccessibleContext().setAccessibleDescription("A sample application to demonstrate Java2D features");
        int WIDTH = 400, HEIGHT = 200;
        frame.setSize(WIDTH, HEIGHT);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(d.width/2 - WIDTH/2, d.height/2 - HEIGHT/2);
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
            public void windowDeiconified(WindowEvent e) { 
                if (demo != null) { demo.start(); }
            }
            public void windowIconified(WindowEvent e) { 
                if (demo != null) { demo.stop(); }
            }
        });
        JOptionPane.setRootFrame(frame);

        JPanel progressPanel = new JPanel() {
            public Insets getInsets() {
                return new Insets(40,30,20,30);
            }
        };
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        frame.getContentPane().add(progressPanel, BorderLayout.CENTER);

        Dimension labelSize = new Dimension(400, 20);
        progressLabel = new JLabel("Loading, please wait...");
        progressLabel.setAlignmentX(CENTER_ALIGNMENT);
        progressLabel.setMaximumSize(labelSize);
        progressLabel.setPreferredSize(labelSize);
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

        frame.setVisible(true);

        demo = new Java2Demo();

        frame.getContentPane().removeAll();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(demo, BorderLayout.CENTER);
        WIDTH = 730; HEIGHT = 570;
        frame.setLocation(d.width/2 - WIDTH/2, d.height/2 - HEIGHT/2);
        frame.setSize(WIDTH, HEIGHT);
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            String s = arg.substring(arg.indexOf('=')+1);
            if (arg.startsWith("-runs=")) {
                RunWindow.numRuns = Integer.parseInt(s);
                RunWindow.exit = true;
                demo.createRunWindow();
            } else if (arg.startsWith("-screen=")) {
                demo.controls.screenCombo.setSelectedIndex(Integer.parseInt(s));
            } else if (arg.startsWith("-antialias=")) {
                demo.controls.aliasCB.setSelected(s.endsWith("true"));
            } else if (arg.startsWith("-rendering=")) {
                demo.controls.renderCB.setSelected(s.endsWith("true"));
            } else if (arg.startsWith("-texture=")) {
                demo.controls.textureCB.setSelected(s.endsWith("true"));
            } else if (arg.startsWith("-composite=")) {
                demo.controls.compositeCB.setSelected(s.endsWith("true"));
            } else if (arg.startsWith("-verbose")) {
                demo.verboseCB.setSelected(true);
            } else if (arg.startsWith("-print")) {
                demo.printCB.setSelected(true);
                RunWindow.printCB.setSelected(true);
            } else if (arg.startsWith("-columns=")) {
                DemoGroup.columns = Integer.parseInt(s);
            } else if (arg.startsWith("-buffers=")) {
                // usage -buffers=3,10
                RunWindow.buffersFlag = true;
                int i1 = arg.indexOf('=')+1;
                int i2 = arg.indexOf(',');
                String s1 = arg.substring(i1, i2);
                RunWindow.bufBeg = Integer.parseInt(s1);
                s1 = arg.substring(i2+1, arg.length());
                RunWindow.bufEnd = Integer.parseInt(s1);
            } else if (arg.startsWith("-ccthread")) {
                demo.ccthreadCB.setSelected(true);
            } else if (arg.startsWith("-zoom")) {
                RunWindow.zoomCB.setSelected(true);
            } else if (arg.startsWith("-maxscreen")) {
                frame.setLocation(0, 0);
                frame.setSize(d.width, d.height);
            }
        }

        frame.validate();
        frame.repaint();
        frame.getFocusTraversalPolicy()
             .getDefaultComponent(frame)
             .requestFocus();
        demo.start();

        if (RunWindow.exit) {
            demo.startRunWindow();
        }
    }
}
