/*
 * @(#)CustomControls.java	1.1	99/09/07
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
import javax.swing.*;

/**
 * A convenience class for demos that use Custom Controls.  This class
 * sets up the thread for running the custom control.  A notifier thread
 * is started as well, a flashing 2x2 rect is drawn in the upper right corner
 * while the custom control thread continues to run.
 */
public abstract class CustomControls extends JPanel implements Runnable {


    protected Thread thread;
    protected boolean doNotifier;
    private CCNotifierThread ccnt; 
    private String name = "foo.bar Demo";
    private static final Color blue = new Color(204, 204, 255);


    public CustomControls() { 
        setBackground(Color.gray);
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (thread == null) { start(); } else { stop(); }
            }
        });
    }

    public CustomControls(String name) {
        this();
        this.name = name + " Demo";
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(doNotifier ? blue : Color.gray);
        g.fillRect(getSize().width-2, 0, 2, 2);
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.setName(name + " ccthread");
            thread.start();
            (ccnt = new CCNotifierThread()).start();
            ccnt.setName(name + " ccthread notifier");
        }
    }

    public synchronized void stop() {
        if (thread != null) {
            thread.interrupt();
            if (ccnt != null) {
                ccnt.interrupt();
            }
        }
        thread = null;
    }


    // Custom Controls override the run method
    public void run() { }


    /**
     * Notifier that the custom control thread is running.
     */
    class CCNotifierThread extends Thread {
        public void run() {
            while (thread != null) {
                doNotifier = !doNotifier;
                repaint();
                try { Thread.sleep(444); } catch (Exception ex) { break; }
            }
            doNotifier = false; repaint();
        }
    }
}
