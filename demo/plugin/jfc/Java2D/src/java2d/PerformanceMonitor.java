/*
 * @(#)PerformanceMonitor.java	1.39 06/08/25
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
 * @(#)PerformanceMonitor.java	1.39 06/08/25
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
            if (panel != null) {
                for (Component comp : panel.getComponents()) {
                    if (((DemoPanel) comp).surface != null) {
                        ((DemoPanel) comp).surface.setMonitor(thread != null);
                    }
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
    
            Dimension d = new Dimension(0, 0);
            Graphics2D big = null;
            FontMetrics fm = null;
            int ascent  = 0;
            int descent = 0;
            
            while (thread == me && isShowing()) {

                if (getWidth() != d.width || getHeight() != d.height) {
                    d = getSize();
                    bimg = (BufferedImage) createImage(d.width, d.height);
                    big = bimg.createGraphics();
                    big.setFont(font);
                    fm = big.getFontMetrics();
                    ascent  = fm.getAscent();
                    descent = fm.getDescent();
                    setSurfaceState();
                }

                big.setBackground(getBackground());
                big.clearRect(0, 0, d.width, d.height);
                if (panel == null) {
                    continue;
                }
                big.setColor(Color.green);
                int ssH = 1;
                for (Component comp : panel.getComponents()) {
                    if (((DemoPanel) comp).surface != null) {
                        String pStr = ((DemoPanel) comp).surface.perfStr;
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
