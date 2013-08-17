/*
 * @(#)ToolBar.java	1.22  98/09/22
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
import java.awt.print.PrinterJob;
import javax.swing.*;
import java.awt.event.*;
import java.net.URL;


/**
 * ToolBar to control individual demo graphic attributes.  Also, control for
 * start & stop on animated demos; control for cloning the demo; control for
 * printing the demo.
 */
public class ToolBar extends JPanel implements ActionListener, Runnable {

    private static ImageIcon stopIcon = 
            new ImageIcon(ToolBar.class.getResource("images/stop.gif"));
    private static ImageIcon startIcon = 
            new ImageIcon(ToolBar.class.getResource("images/start.gif"));
    private static Font font = new Font("serif", Font.PLAIN, 10);

    private DemoSurface surface;
    private JButton printB;
    private JToolBar toolbar;
    private Thread thread;

    public JComboBox imgTypeCombo;
    public JButton renderB, aliasB;
    public JButton textureB, compositeB;
    public JButton startStopB;
    public JButton cloneB;
    public boolean issueRepaint = true;
    public boolean verboseFlag;


    public ToolBar(DemoSurface surface) {

        this.surface = surface;

        toolbar = new JToolBar();

        String s = surface.AntiAlias == RenderingHints.VALUE_ANTIALIAS_ON 
            ? "On" : "Off";
        aliasB = addTool( "A", "Antialiasing " + s, this);

        s = surface.Rendering == RenderingHints.VALUE_RENDER_SPEED
            ? "Speed" : "Quality";
        renderB = addTool("R", "Rendering " + s, this);

        s = surface.texture != null ? "On" : "Off";
        textureB = addTool("T", "Texture " + s, this);

        s = surface.composite != null ? "On" : "Off";
        compositeB = addTool("C", "Composite " + s, this);

        printB = addTool("print.gif", "Print the Surface", this);

        if (surface instanceof AnimatingContext || surface.observerRunning) {
            startStopB = addTool("start.gif", "Animation Running", this);
        }


        imgTypeCombo = new JComboBox();
        imgTypeCombo.setFont(font);
        imgTypeCombo.setBackground(Color.lightGray);
        imgTypeCombo.addItem("Auto Screen");
        imgTypeCombo.addItem("On Screen");
        imgTypeCombo.addItem("Off Screen");
        imgTypeCombo.addItem("INT_RGB");
        imgTypeCombo.addItem("INT_ARGB");
        imgTypeCombo.addItem("INT_ARGB_PRE");
        imgTypeCombo.addItem("INT_BGR");
        imgTypeCombo.addItem("3BYTE_BGR");
        imgTypeCombo.addItem("4BYTE_ABGR");
        imgTypeCombo.addItem("4BYTE_ABGR_PRE");
        imgTypeCombo.addItem("USHORT_565_RGB");
        imgTypeCombo.addItem("USHORT_555_RGB");
        imgTypeCombo.addItem("BYTE_GRAY");
        imgTypeCombo.addItem("USHORT_GRAY");
        imgTypeCombo.addItem("BYTE_BINARY");
        imgTypeCombo.setPreferredSize(new Dimension(100, 20));
        imgTypeCombo.setSelectedIndex(surface.imageType);
        imgTypeCombo.addActionListener(this);

        setLayout(new FlowLayout());
        setBackground(Color.gray);
        add(toolbar);
        add(imgTypeCombo);
    }


    public JButton addTool(String str, 
                           String toolTip,
                           ActionListener al) {
        JButton b = null;
        if (str.indexOf(".") == -1) {
            b = (JButton) toolbar.add(new JButton(str));
            if (toolTip.equals("Rendering Quality") ||
                    toolTip.equals("Antialiasing On") ||
                        toolTip.equals("Texture On")  ||
                            toolTip.equals("Composite On")) {
                b.setBackground(Color.green);
                b.setSelected(true);
            } else {
                b.setBackground(Color.lightGray);
                b.setSelected(false);
            }
        } else {
            URL url = ToolBar.class.getResource("images/" + str);
            b = (JButton) toolbar.add(new JButton(new ImageIcon(url)));
            b.setSelected(true);
        }
        b.setToolTipText(toolTip);
        b.addActionListener(al);
        return b;
    }


    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj.equals(printB)) {
            start();
            return;
        }
        if (obj instanceof JButton) {
            JButton b = (JButton) obj;
            b.setSelected(!b.isSelected());
            if (b.getIcon() == null) {
                b.setBackground(b.isSelected() ? Color.green : Color.lightGray);
            }
        }
        if (obj.equals(startStopB)) {
            if (startStopB.getToolTipText().equals("Animation Running")) {
                startStopB.setIcon(stopIcon);
                startStopB.setToolTipText("Animation Stopped");
                surface.observerRunning = false;
                surface.stop();
            } else {
                startStopB.setIcon(startIcon);
                startStopB.setToolTipText("Animation Running");
                surface.observerRunning = true;
                surface.start();
            }
        } else if (obj.equals(aliasB)) {
            if (aliasB.getToolTipText().equals("Antialiasing On")) {
                aliasB.setToolTipText("Antialiasing Off");
            } else {
                aliasB.setToolTipText("Antialiasing On");
            }
            surface.setAntiAlias(aliasB.isSelected());
        } else if (obj.equals(renderB)) {
            if (renderB.getToolTipText().equals("Rendering Quality")) {
                renderB.setToolTipText("Rendering Speed");
            } else {
                renderB.setToolTipText("Rendering Quality");
            }
            surface.setRendering(renderB.isSelected());
        } else if (obj.equals(textureB)) {
            Object texture = null;
            if (textureB.getToolTipText().equals("Texture On")) {
                textureB.setToolTipText("Texture Off");
                surface.setTexture(null);
                surface.clearSurface = true;
            } else {
                textureB.setToolTipText("Texture On");
                surface.setTexture(TextureChooser.texture);
            }
        } else if (obj.equals(compositeB)) {
            if (compositeB.getToolTipText().equals("Composite On")) {
                compositeB.setToolTipText("Composite Off");
            } else {
                compositeB.setToolTipText("Composite On");
            }
            surface.setComposite(compositeB.isSelected());
        } else if (obj.equals(imgTypeCombo)) {
            surface.setImageType(imgTypeCombo.getSelectedIndex());
        }

        if (issueRepaint && surface.thread != null) {
            if (surface.sleepAmount != 0) {
                surface.thread.interrupt();
            }
        } else if (issueRepaint) {
            surface.repaint();
        }

        if (verboseFlag) {
           surface.verbose();
        }
    }


    public Dimension getPreferredSize() {
        return new Dimension(200,38);
    }


    public void start() {
        thread = new Thread(this);
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.setName("Printing " + surface.name);
        thread.start();
    }


    public synchronized void stop() {
        thread = null;
        notifyAll();
    }


    public void run() {
        boolean stopped = false;
        if (surface.thread != null) {
            stopped = true;
            startStopB.doClick();
        }
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(surface);
        if (printJob.printDialog()) {
            try {
                printJob.print();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (stopped) {
            startStopB.doClick();
        }
        thread = null;
    }

}
