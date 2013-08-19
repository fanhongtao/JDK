/*
 * Copyright (c) 2003 Sun Microsystems, Inc. All  Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * -Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduct the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT
 * BE LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT
 * OF OR RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN
 * IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that Software is not designed, licensed or intended for
 * use in the design, construction, operation or maintenance of any nuclear
 * facility.
 */

/*
 * @(#)Tools.java	1.36 03/01/23
 */

package java2d;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterJob;
import javax.print.attribute.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.net.URL;


/**
 * Tools to control individual demo graphic attributes.  Also, control for
 * start & stop on animated demos; control for cloning the demo; control for
 * printing the demo.  Expand and collapse the Tools panel with ToggleIcon.
 */
public class Tools extends JPanel implements ActionListener, ChangeListener, MouseListener, Runnable {

    private ImageIcon stopIcon, startIcon;
    private Font font = new Font("serif", Font.PLAIN, 10);
    private Color roColor = new Color(204, 204, 255);
    private Surface surface;
    private Thread thread;
    private JPanel toolbarPanel;
    private JPanel sliderPanel;
    private JLabel label;
    private ToggleIcon bumpyIcon, rolloverIcon;

    protected boolean focus;

    public JButton toggleB;
    public JButton printB;
    public JComboBox screenCombo;
    public JButton renderB, aliasB;
    public JButton textureB, compositeB;
    public JButton startStopB;
    public JButton cloneB;
    public boolean issueRepaint = true;
    public JToolBar toolbar;
    public JSlider slider;
    public boolean doSlider;
    public boolean isExpanded;

    public Tools(Surface surface) {
        this.surface = surface;
        setBackground(Color.gray);
        setLayout(new BorderLayout());

        stopIcon = new ImageIcon(DemoImages.getImage("stop.gif", this));
        startIcon = new ImageIcon(DemoImages.getImage("start.gif",this));
        bumpyIcon = new ToggleIcon(this, Color.lightGray);
        rolloverIcon = new ToggleIcon(this, roColor);
        toggleB = new JButton(bumpyIcon);
        toggleB.addMouseListener(this);
        isExpanded = false;
        toggleB.addActionListener(this);
        toggleB.setMargin(new Insets(0,0,-4,0));
        toggleB.setBorderPainted(false);
        toggleB.setFocusPainted(false);
        toggleB.setContentAreaFilled(false);
        toggleB.setRolloverIcon(rolloverIcon);
        add("North", toggleB);


        toolbar = new JToolBar();
        toolbar.setPreferredSize(new Dimension(100, 26));
        toolbar.setFloatable(false);

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

        if (surface instanceof AnimatingSurface) {
            startStopB = addTool("stop.gif", "Stop Animation", this);
            toolbar.setPreferredSize(new Dimension(122, 26));
        }

        screenCombo = new JComboBox();
        screenCombo.setPreferredSize(new Dimension(100, 18));
        screenCombo.setFont(font);
        for (int i = 0; i < GlobalControls.screenNames.length; i++) {
            screenCombo.addItem(GlobalControls.screenNames[i]);
        } 
        screenCombo.addActionListener(this);
        toolbarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,5,0));
        toolbarPanel.setBackground(Color.gray);
        toolbarPanel.setLocation(0,6);
        toolbarPanel.setVisible(false);
        toolbarPanel.add(toolbar);
        toolbarPanel.add(screenCombo);
        add(toolbarPanel);

        setPreferredSize(new Dimension(200,6));

        if (surface instanceof AnimatingSurface) {
            sliderPanel = new JPanel(new GridLayout(0,2,5,5));
            sliderPanel.setBackground(Color.gray);
            label = new JLabel("sleep = 30 ms");
            label.setForeground(Color.black);
            sliderPanel.add(label);
            slider = new JSlider(JSlider.HORIZONTAL, 0, 200, 30);
            slider.setBackground(Color.gray);
            slider.addChangeListener(this);
            TitledBorder tb = new TitledBorder(new EtchedBorder());
            tb.setTitleFont(new Font("serif", Font.PLAIN, 8));
            tb.setTitle("sleep = 30 ms");
            EmptyBorder eb = new EmptyBorder(4,5,4,5);
            slider.setBorder(new CompoundBorder(eb, new EtchedBorder()));
            sliderPanel.add(slider);

            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                   if (toolbarPanel.isVisible()) {
                       invalidate();
                       if ((doSlider = !doSlider)) {
                           remove(toolbarPanel);
                           add(sliderPanel);
                       } else {
                           remove(sliderPanel);
                           add(toolbarPanel);
                       }
                       validate();
                       repaint();
                   }
                }
            });
        }
    }


    public JButton addTool(String name, 
                           String toolTip,
                           ActionListener al) {
        JButton b = null;
        if (name.indexOf(".") == -1) {
            b = (JButton) toolbar.add(new JButton(name));
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
            b.setPreferredSize(new Dimension(18, 22));
            b.setMaximumSize(new Dimension(18, 22));
            b.setMinimumSize(new Dimension(18, 22));
        } else {
            Image img = DemoImages.getImage(name, this);
            b = (JButton) toolbar.add(new JButton(new ImageIcon(img)));
            b.setSelected(true);
        }
        b.setToolTipText(toolTip);
        b.addActionListener(al);
        return b;
    }


    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
	if (obj instanceof JButton) {
            JButton b = (JButton) obj;
            b.setSelected(!b.isSelected());
            if (b.getIcon() == null) {
                b.setBackground(b.isSelected() ? Color.green : Color.lightGray);
            }
        }
        if (obj.equals(toggleB)) {
	    isExpanded = !isExpanded;
            if (isExpanded) {
                setPreferredSize(new Dimension(200,38));
            } else {
                setPreferredSize(new Dimension(200,6));
            }
            toolbarPanel.setVisible(isExpanded);
            if (sliderPanel != null) {
                sliderPanel.setVisible(isExpanded);
            }
            getParent().validate();
            toggleB.getModel().setRollover(false);
            return;
        }
        if (obj.equals(printB)) {
            start();
            return;
        }
       
        if (obj.equals(startStopB)) {
            if (startStopB.getToolTipText().equals("Stop Animation")) {
                startStopB.setIcon(startIcon);
                startStopB.setToolTipText("Start Animation");
                surface.animating.stop();
            } else {
                startStopB.setIcon(stopIcon);
                startStopB.setToolTipText("Stop Animation");
                surface.animating.start();
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
        } else if (obj.equals(screenCombo)) {
            surface.setImageType(screenCombo.getSelectedIndex());
        }

        if (issueRepaint && surface.animating != null) {
            if (surface.getSleepAmount() != 0) {
                if (surface.animating.thread != null) {
                    surface.animating.thread.interrupt();
                }
            }
        } else if (issueRepaint) {
            surface.repaint();
        }
    }


    public void stateChanged(ChangeEvent e) {
        int value = slider.getValue();
        label.setText("sleep = " + String.valueOf(value) + " ms");
        label.repaint();
        surface.setSleepAmount(value);
    }


    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
        focus = true;
        bumpyIcon.start();
    }

    public void mouseExited(MouseEvent e) {
        focus = false;
        bumpyIcon.stop();
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
        if (surface.animating != null && surface.animating.thread != null) {
            stopped = true;
            startStopB.doClick();
        }

        try {
            PrinterJob printJob = PrinterJob.getPrinterJob();
            printJob.setPrintable(surface);
            boolean pDialogState = true;
            PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();

            if (!Java2Demo.printCB.isSelected()) {
                pDialogState = printJob.printDialog(aset);
            }
            if (pDialogState) {
                printJob.print(aset);
            }
        } catch (java.security.AccessControlException ace) {
            String errmsg = "Applet access control exception; to allow " +
                            "access to printer, run policytool and set\n" +
                            "permission for \"queuePrintJob\" in " +
                            "RuntimePermission.";
            JOptionPane.showMessageDialog(this, errmsg, "Printer Access Error",
                                          JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (stopped) {
            startStopB.doClick();
        }
        thread = null;
    }


    /**
     * Expand and Collapse the Tools Panel with this bumpy button.
     */
    static class ToggleIcon implements Icon, Runnable {

        private Color topColor = new Color(153, 153, 204);
        private Color shadowColor = new Color(102, 102, 153);
        private Color backColor = new Color(204, 204, 255);
        private Tools tools;
        private Thread thread;
        private Color fillColor;


        public ToggleIcon(Tools tools, Color fillColor) {
            this.tools = tools;
            this.fillColor = fillColor;
        }

    
        public void paintIcon(Component c, Graphics g, int x, int y ) {
	    int w = getIconWidth();
	    int h = getIconHeight();
	    g.setColor(fillColor);
	    g.fillRect(0, 0, w, h);
            for (; x < w-2; x+=4) {
                g.setColor(Color.white);
                g.fillRect(x, 1, 1, 1);
                g.fillRect(x+2, 3, 1, 1);
                g.setColor(shadowColor);
                g.fillRect(x+1, 2, 1, 1);
                g.fillRect(x+3, 4, 1, 1);
            }
        }

        public int getIconWidth() {
            return tools.getSize().width;
        }

        public int getIconHeight() {
            return 6;
        }


        public void start() {
            thread = new Thread(this);
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.setName("ToggleIcon");
            thread.start();
        }


        public synchronized void stop() {
            if (thread != null) {
                thread.interrupt();
            }
            thread = null;
        }


        public void run() {
            try {
               thread.sleep(400);
            } catch (InterruptedException e) { }
            if (tools.focus && thread != null) {
                tools.toggleB.doClick();
            }
            thread = null;
        }
    }
} // End Tools class
