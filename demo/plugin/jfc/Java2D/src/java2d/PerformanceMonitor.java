/*
 * @(#)PerformanceMonitor.java	1.31 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package java2d;

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
 * Displays the time for a Surface to paint. Displays the number
 * of frames per second on animated demos.  Up to four surfaces fit
 * in the display area.
 */
public class PerformanceMonitor extends JPanel {

    Surface surf;

    public PerformanceMonitor() {
        setLayout(new BorderLayout());
        setBorder(new TitledBorder(new EtchedBorder(), "Performance"));
        add(surf = new Surface());
    }


    public class Surface extends JPanel implements Runnable {
    
        public Thread thread;
        private BufferedImage bimg;
        private Font font = new Font("Times New Roman", Font.PLAIN, 12);
        private JPanel panel;
    
    
        public Surface() {
            setBackground(Color.black);
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (thread == null) start(); else stop();
                }
            });
        }
    
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }
    
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }
    
        public Dimension getPreferredSize() {
            int textH = getFontMetrics(font).getHeight();
            return new Dimension(135,2+textH*4);
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
                } catch (InterruptedException e) { return; }
            }
    
            Dimension d = getSize();
            bimg = (BufferedImage) createImage(d.width, d.height);
            Graphics2D big = bimg.createGraphics();
            big.setFont(font);
            FontMetrics fm = big.getFontMetrics();
            int ascent = fm.getAscent();
            int descent = fm.getDescent();
            setSurfaceState();
    
            while (thread == me && isShowing()) {
                big.setBackground(getBackground());
                big.clearRect(0, 0, d.width, d.height);
                if (panel == null) {
                    continue;
                }
                Component cmps[] = panel.getComponents();
                big.setColor(Color.green);
                int ssH = 1;
                for (int i = 0; i < cmps.length; i++) {
                    if (((DemoPanel) cmps[i]).surface != null) {
                        String pStr = ((DemoPanel) cmps[i]).surface.perfStr;
                        if (pStr != null) {
                            ssH += ascent;
                            big.drawString(pStr, 4, ssH+1);
                            ssH += descent;
                        }
                    }
                }
                repaint();

                try {
                    thread.sleep(999);
                } catch (InterruptedException e) { break; }
            }
            thread = null;
        }
    } // End Surface
} // End PeformanceMonitor
