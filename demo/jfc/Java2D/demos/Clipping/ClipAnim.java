/*
 * @(#)ClipAnim.java	1.14 98/09/13
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

package demos.Clipping;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import AnimatingContext;
import DemoSurface;
import DemoPanel;


/**
 * Animated clipping of an image & composited shapes.
 */
public class ClipAnim extends DemoSurface implements AnimatingContext {

    private static Image img;
    private static Color redBlend = new Color(255, 0, 0, 120);
    private static Color greenBlend = new Color(0, 255, 0, 120);
    private static BasicStroke bs = new BasicStroke(20.0f);
    private double ix = 5.0;
    private double iy = 3.0;
    private double iw = 5.0;
    private double ih = 3.0;
    private double x, y;
    private double ew, eh;   // ellipse width & height


    public ClipAnim() {
        img = getImage("clouds.jpg");
        setBackground(Color.white);
    }


    public void reset(int w, int h) {
        x = Math.random()*w;
        y = Math.random()*h;
        ew = (Math.random()*w)/2;
        eh = (Math.random()*h)/2;
    }


    public void step(int w, int h) {
        x += ix;
        y += iy;
        ew += iw;
        eh += ih;
        if (ew > w/2) {
            ew = w/2;
            iw = Math.random() * -w/16 - 1;
        }
        if (ew < w/8) {
            ew = w/8;
            iw = Math.random() * w/16 + 1;
        }
        if (eh > h/2) {
            eh = h/2;
            ih = Math.random() * -h/16 - 1;
        }
        if (eh < h/8) {
            eh = h/8;
            ih = Math.random() * h/16 + 1;
        }
        if ((x+ew) > w) {
            x = (w - ew)-1;
            ix = Math.random() * -w/32 - 1;
        }
        if (x < 0) {
            x = 2;
            ix = Math.random() * w/32 + 1;
        }
        if ((y+eh) > h) {
            y = (h - eh)-2;
            iy = Math.random() * -h/32 - 1;
        }
        if (y < 0) {
            y = 2;
            iy = Math.random() * h/32 + 1;
        }
    }


    public void drawDemo(int w, int h, Graphics2D g2) {

        Ellipse2D ellipse = new Ellipse2D.Double(x, y, ew, eh);
        g2.setClip(ellipse);

        Rectangle2D rect = new Rectangle2D.Double(x+5, y+5, ew-10, eh-10);
        g2.clip(rect);

        g2.drawImage(img, 0, 0, w, h, null);

        GeneralPath p = new GeneralPath();
        p.moveTo(- w / 2.0f, - h / 8.0f);
        p.lineTo(+ w / 2.0f, - h / 8.0f);
        p.lineTo(- w / 4.0f, + h / 2.0f);
        p.lineTo(+     0.0f, - h / 2.0f);
        p.lineTo(+ w / 4.0f, + h / 2.0f);
        p.closePath();

        g2.setStroke(bs);
        g2.setPaint(redBlend);
        AffineTransform at = AffineTransform.getTranslateInstance(w*.5,h*.5);
        g2.draw(at.createTransformedShape(p));

        g2.setPaint(greenBlend);
        Arc2D arc = new Arc2D.Double();
        RoundRectangle2D roundRect = new RoundRectangle2D.Double();

        for (int yy = 0; yy < h; yy += 50) {
            for (int xx = 0, i=0; xx < w; i++, xx += 50) {
                switch (i) {
                case 0 : arc.setArc(xx, yy, 25, 25, 45, 270, Arc2D.PIE);
                         g2.fill(arc); break;
                case 1 : ellipse.setFrame(xx, yy, 25, 25);
                         g2.fill(ellipse); break;
                case 2 : roundRect.setRoundRect(xx, yy, 25, 25, 4, 4);
                         g2.fill(roundRect); break;
                case 3 : rect.setRect(xx, yy, 25, 25);
                         g2.fill(rect);
                         i = -1;
                }
            }
        }

        rect.setRect(0, 0, w, h);
        g2.setClip(rect);
    }


    public static void main(String argv[]) {
        final DemoPanel dp = new DemoPanel(new ClipAnim());
        Frame f = new Frame("Java2D Demo - ClipAnim");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
            public void windowDeiconified(WindowEvent e) { 
                dp.surface.start(); 
            }
            public void windowIconified(WindowEvent e) { 
                dp.surface.stop(); 
            }
        });
        f.add("Center", dp);
        f.pack();
        f.setSize(new Dimension(400,300));
        f.show();
        dp.surface.start();
    }
}
