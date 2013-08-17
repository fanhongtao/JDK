/*
 * @(#)Ellipses.java	1.12 98/09/13
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

package demos.Arcs_Curves;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import AnimatingContext;
import DemoPanel;
import DemoSurface;

/**
 * Ellipse2D 25 animated expanding ellipses.
 */
public class Ellipses extends DemoSurface implements AnimatingContext {

    private static Color colors[] = { Color.blue, Color.cyan, Color.green,
                Color.magenta, Color.orange, Color.pink, Color.red, 
                Color.yellow, Color.lightGray, Color.white };
    private Ellipse2D.Float[] ellipses;
    private double esize[];
    private float estroke[];
    private double maxSize;


    public Ellipses() {
        setBackground(Color.black);
        ellipses = new Ellipse2D.Float[25];
        esize = new double[ellipses.length];
        estroke = new float[ellipses.length];
        for (int i = 0; i < ellipses.length; i++) {
            ellipses[i] = new Ellipse2D.Float();
            getRandomXY(i, 20 * Math.random(), 200, 200);
        }
    }


    public void getRandomXY(int i, double size, int w, int h) {
        esize[i] = size;
        estroke[i] = 1.0f;
        double x = Math.random() * (w-(maxSize/2));
        double y = Math.random() * (h-(maxSize/2));
        ellipses[i].setFrame(x, y, size, size);
    }


    public void reset(int w, int h) {
        maxSize = w/10;
        for (int i = 0; i < ellipses.length; i++ ) {
            getRandomXY(i, maxSize * Math.random(), w, h);
        }
    }


    public void step(int w, int h) {
        for (int i = 0; i < ellipses.length; i++) {
            estroke[i] += 0.025f;
            esize[i]++;
            if (esize[i] > maxSize) {
                getRandomXY(i, 1, w, h);
            } else {
                ellipses[i].setFrame(ellipses[i].getX(), ellipses[i].getY(),
                                     esize[i], esize[i]);
            }
        }
    }


    public void drawDemo(int w, int h, Graphics2D g2) {

        for (int i = 0; i < ellipses.length; i++) {
            g2.setColor(colors[i%colors.length]);
            g2.setStroke(new BasicStroke(estroke[i]));
            g2.draw(ellipses[i]);
        }
    }


    public static void main(String argv[]) {
        final DemoPanel dp = new DemoPanel(new Ellipses());
        Frame f = new Frame("Java2D Demo - Ellipses");
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
