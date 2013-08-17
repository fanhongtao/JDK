/*
 * @(#)DukeAnim.java	1.9 98/09/13
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

package demos.Images;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import DemoSurface;
import DemoPanel;


/**
 * Animated gif with a transparent background.
 */
public class DukeAnim extends DemoSurface implements ImageObserver {

    private static Image agif, clouds;
    private static int aw, ah, cw;
    private int x;


    public DukeAnim() {
        setBackground(Color.white);
        clouds = getImage("clouds.jpg");
        agif = getImage("duke.running.gif");
        aw = agif.getWidth(this) / 2;
        ah = agif.getHeight(this) / 2;
        cw = clouds.getWidth(this);
        observerRunning = true;
    }


    public void drawDemo(int w, int h, Graphics2D g2) {
        if ((x -= 3) <= -cw) {
            x = w;
        }
        g2.drawImage(clouds, x, 10, cw, h-20, this);
        g2.drawImage(agif, w/2-aw, h/2-ah, this);
    }


    public boolean imageUpdate(Image img, int infoflags,
                int x, int y, int width, int height)
    {
        if (observerRunning && (infoflags & ALLBITS) != 0)
            repaint();
        if (observerRunning && (infoflags & FRAMEBITS) != 0)
            repaint();
        return isShowing();
    }



    public static void main(String s[]) {
        WindowListener l = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        };
        Frame f = new Frame("Java2D Demo - DukeAnim");
        f.addWindowListener(l);
        f.add("Center", new DemoPanel(new DukeAnim()));
        f.pack();
        f.setSize(new Dimension(400,300));
        f.show();
    }
}
