/*
 * @(#)Tools.java	1.44 06/08/29
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
 * @(#)Tools.java	1.44 06/08/29
 */

package java2d;

import static java.awt.Color.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterJob;
import javax.print.attribute.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.net.URL;
import java.text.DecimalFormat;


/**
 * Tools to control individual demo graphic attributes.  Also, control for
 * start & stop on animated demos; control for cloning the demo; control for
 * printing the demo.  Expand and collapse the Tools panel with ToggleIcon.
 */
public class Tools extends JPanel implements ActionListener, ChangeListener, MouseListener, Runnable {

    private ImageIcon stopIcon, startIcon;
    private Font font = new Font("serif", Font.PLAIN, 10);
    private Color roColor = new Color(187, 213, 238); 
    private Surface surface;
    private Thread thread;
    private JPanel toolbarPanel;
    private JPanel sliderPanel;
    private JLabel label;
    private ToggleIcon bumpyIcon, rolloverIcon;
    
    private DecimalFormat decimalFormat = new DecimalFormat("000"); 

    protected boolean focus;

    public JToggleButton toggleB;
    public JButton printB;
    public JComboBox screenCombo;
    public JToggleButton renderB, aliasB; 
    public JToggleButton textureB, compositeB; 
    public JButton startStopB;
    public JButton cloneB;
    public boolean issueRepaint = true;
    public JToolBar toolbar;
    public JSlider slider;
    public boolean doSlider;
    public boolean isExpanded;

    public Tools(Surface surface) {
        this.surface = surface;
        setLayout(new BorderLayout());

        stopIcon     = new  ImageIcon(DemoImages.getImage( "stop.gif",this));
        startIcon    = new  ImageIcon(DemoImages.getImage("start.gif",this));
        bumpyIcon    = new ToggleIcon(this, LIGHT_GRAY);
        rolloverIcon = new ToggleIcon(this, roColor);
        toggleB = new JToggleButton(bumpyIcon);
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
        toolbar.setPreferredSize(new Dimension(112, 26));
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

        Image printBImg = DemoImages.getImage("print.gif", this);
        printB = addTool(printBImg, "Print the Surface", this);

        if (surface instanceof AnimatingSurface) {
            Image stopImg = DemoImages.getImage("stop.gif", this);
            startStopB = addTool(stopImg, "Stop Animation", this);
            toolbar.setPreferredSize(new Dimension(132, 26));
        }

        screenCombo = new JComboBox();
        screenCombo.setPreferredSize(new Dimension(100, 18));
        screenCombo.setFont(font);
        for (String name : GlobalControls.screenNames) {
            screenCombo.addItem(name);
        } 
        screenCombo.addActionListener(this);
        toolbarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,5,0));
        toolbarPanel.setLocation(0,6);
        toolbarPanel.setVisible(false);
        toolbarPanel.add(toolbar);
        toolbarPanel.add(screenCombo);
        toolbarPanel.setBorder(new EtchedBorder());
        add(toolbarPanel);

        setPreferredSize(new Dimension(200,8));

        if (surface instanceof AnimatingSurface) {
            sliderPanel = new JPanel(new BorderLayout());
            label = new JLabel(" Sleep = 030 ms");
            label.setForeground(BLACK);
            sliderPanel.add(label, BorderLayout.WEST);
            slider = new JSlider(JSlider.HORIZONTAL, 0, 200, 30);
            slider.addChangeListener(this);
            sliderPanel.setBorder(new EtchedBorder());
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


    public JButton addTool(Image img,
                           String toolTip,
                           ActionListener al) {
        JButton b = new JButton(new ImageIcon(img)) {
            Dimension prefSize = new Dimension(21, 22);
            public Dimension getPreferredSize() {
                return prefSize;
            }
            public Dimension getMaximumSize() {
                return prefSize;
            }
            public Dimension getMinimumSize() {
                return prefSize;
            }
        };
        toolbar.add(b);
        b.setFocusPainted(false);
        b.setSelected(true);
        b.setToolTipText(toolTip);
        b.addActionListener(al);
        return b;
    }

    public JToggleButton addTool(String name,
                                 String toolTip,
                                 ActionListener al) {
        JToggleButton b = new JToggleButton(name) {
            Dimension prefSize = new Dimension(21, 22);
            public Dimension getPreferredSize() {
                return prefSize;
            }
            public Dimension getMaximumSize() {
                return prefSize;
            }
            public Dimension getMinimumSize() {
                return prefSize;
            }
        };
        toolbar.add(b);
        b.setFocusPainted(false);
        if (toolTip.equals("Rendering Quality") ||
            toolTip.equals("Antialiasing On") ||
            toolTip.equals("Texture On")  ||
            toolTip.equals("Composite On")) {
            b.setSelected(true);
        } else {
            b.setSelected(false);
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
                b.setBackground(b.isSelected() ? GREEN : LIGHT_GRAY);
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
        label.setText(" Sleep = " + decimalFormat.format(value) + " ms");
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

        private Color    topColor = new Color(153, 153, 204);
        private Color shadowColor = new Color(102, 102, 153);
        private Color   backColor = new Color(204, 204, 255);
        private Color   fillColor;
        private Tools tools;
        private Thread thread;


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
                g.setColor(WHITE);
                g.fillRect(x  , 1, 1, 1);
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
