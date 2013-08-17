/*
 * @(#)PerformanceMonitor.java	1.28 99/09/07
 *
 * Copyright (c) 1998, 1999 by Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
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
