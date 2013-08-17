/*
 * @(#)CustomControls.java	1.4 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java2d;

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
