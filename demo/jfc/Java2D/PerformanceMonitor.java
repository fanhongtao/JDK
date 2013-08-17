/*
 * @(#)PerformanceMonitor.java	1.24 98/09/13
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
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;


/**
 * Displays the time for a DemoSurface to paint. Displays the number
 * of frames per second on animated demos.  Up to four surfaces fit
 * in the display area.
 */
public class PerformanceMonitor extends JPanel {

    MonitorComponent mc;

    public PerformanceMonitor() {
        setLayout(new BorderLayout());
        setBorder(new TitledBorder(new EtchedBorder(), "Performance"));
        mc = new MonitorComponent();
        add(mc);
    }

public class MonitorComponent extends JPanel implements Runnable {

    public Thread thread;
    private BufferedImage bimg;
    private Font font = new Font("Times New Roman", Font.PLAIN, 12);
    private JPanel panel;


    public MonitorComponent() {
        setBackground(Color.black);
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (thread == null) start(); else stop();
            }
        });
        start();
    }

    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    public Dimension getMaximumSize() {
        return getPreferredSize();
    }


    public Dimension getPreferredSize() {
        TextLayout tl = new TextLayout("Nothing",font,
            new FontRenderContext(null, false, false));
        int h = (int)(tl.getAscent()+tl.getDescent());
        return new Dimension(140,4+h*4);
    }


    public void paint(Graphics g) {
        if (bimg != null) {
            g.drawImage(bimg, 0, 0, this);
        }
    }


    public void start() {
        thread = new Thread(this);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.setName("PerformanceMonitor");
        thread.start();
    }


    public synchronized void stop() {
        thread = null;
        setSurfaceState();
        notify();
    }


    public void setSurfaceState() {
        if (panel == null) {
            return;
        }
        Component cmps[] = panel.getComponents();
        for (int i = 0; i < cmps.length; i++) {
            if (((DemoPanel) cmps[i]).surface != null) {
                ((DemoPanel) cmps[i]).surface.setMonitor(thread != null);
            }
        }
    }


    public void setPanel(JPanel panel) {
        this.panel = panel;
    }


    public void run() {

        Thread me = Thread.currentThread();

        while (thread == me && !isShowing() || getSize().width == 0) {
            try {
                thread.sleep(500);
            } catch (InterruptedException e) { thread = null; return; }
        }


        bimg = (BufferedImage) createImage(getSize().width, getSize().height);
        Graphics2D big = bimg.createGraphics();
        big.setFont(font);
        FontMetrics fm = big.getFontMetrics();
        int ascent = fm.getAscent();
        int descent = fm.getDescent();
        Dimension d = getSize();
        setSurfaceState();

        while (thread == me && isShowing()) {

            try {
                thread.sleep(999);
            } catch (InterruptedException e) { thread = null; return; }

            big.setBackground(getBackground());
            big.clearRect(0, 0, d.width, d.height);
            big.setColor(Color.green);
            if (panel == null) {
                continue;
            }
            Component cmps[] = panel.getComponents();
            int ssH = 1;
            for (int i = 0; i < cmps.length; i++) {
                DemoSurface surface = ((DemoPanel) cmps[i]).surface;
                if (surface != null && surface.perfStr != null) {
                    ssH += ascent;
                    big.drawString(surface.perfStr, 4, ssH+1);
                    ssH += descent;
                }
            }
            repaint();
        }
        thread = null;
    }
} // End MonitorComponent
} // End PeformanceMonitor
