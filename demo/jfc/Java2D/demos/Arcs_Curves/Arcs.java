/*
 * @(#)Arcs.java	1.13 98/09/13
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
import java.awt.geom.Arc2D;
import java.awt.geom.AffineTransform;
import DemoPanel;
import DemoSurface;
import AnimatingContext;


/**
 * Arc2D Open, Chord & Pie arcs; Animated Pie Arc.
 */
public class Arcs extends DemoSurface implements AnimatingContext {

    private static String types[] = {"Arc2D.CHORD","Arc2D.OPEN","Arc2D.PIE"};
    private static final int CLOSE = 0;
    private static final int OPEN = 1;
    private static final int FORWARD = 0;
    private static final int BACKWARD = 1;
    private static final int DOWN = 2;
    private static final int UP = 3;

    private int aw, ah; // animated arc width & height
    private int x, y;
    private int angleStart = 45;
    private int angleExtent = 270;
    private int mouth = CLOSE;
    private int direction = FORWARD;


    public Arcs() {
        setBackground(Color.white);
    }


    public void reset(int w, int h) {
        x = 0; y = 0;
        aw = w/12; ah = h/12;
    }


    public void step(int w, int h) {
      // Compute direction
        if (x+aw >= w-5 && direction == FORWARD)
            direction = DOWN;
        if (y+ah >= h-5 && direction == DOWN)
            direction = BACKWARD;
        if (x-aw <= 5 && direction == BACKWARD)
            direction = UP;
        if (y-ah <= 5 && direction == UP)
            direction = FORWARD;

      // compute angle start & extent
        if (mouth == CLOSE) {
            angleStart -= 5;
            angleExtent += 10;
        }
        if (mouth == OPEN) {
            angleStart += 5;
            angleExtent -= 10;
        }
        if (direction == FORWARD) {
            x += 5; y = 0;
        }
        if (direction == DOWN) {
            x = w; y += 5;
        }
        if (direction == BACKWARD) {
            x -= 5; y = h;
        }
        if (direction == UP) {
            x = 0; y -= 5;
        }
        if (angleStart == 0)
            mouth = OPEN;
        if (angleStart > 45)
            mouth = CLOSE;
    }


    public void drawDemo(int w, int h, Graphics2D g2) {

        g2.setStroke(new BasicStroke(5.0f));
      // Draw Arcs
        for (int i = 0; i < types.length; i++) {
            Arc2D arc = new Arc2D.Float(i);
            arc.setFrame((i+1)*w*.2, (i+1)*h*.2, w*.17, h*.17);
            arc.setAngleStart(45);
            arc.setAngleExtent(270);
            g2.setColor(Color.blue);
            g2.draw(arc);
            g2.setColor(Color.gray);
            g2.fill(arc);
            g2.setColor(Color.black);
            g2.drawString(types[i], (int)((i+1)*w*.2), (int)((i+1)*h*.2-3));
        }

      // Draw Animated Pie Arc
        Arc2D pieArc = new Arc2D.Float(Arc2D.PIE);
        pieArc.setFrame(0, 0, aw, ah);
        pieArc.setAngleStart(angleStart);
        pieArc.setAngleExtent(angleExtent);
        AffineTransform at = AffineTransform.getTranslateInstance(x, y);
        switch (direction) {
            case DOWN : at.rotate(Math.toRadians(90)); break;
            case BACKWARD : at.rotate(Math.toRadians(180)); break;
            case UP : at.rotate(Math.toRadians(270));
        }
        g2.setColor(Color.blue);
        g2.fill(at.createTransformedShape(pieArc));
    }


    public static void main(String argv[]) {
        final DemoPanel dp = new DemoPanel(new Arcs());
        Frame f = new Frame("Java2D Demo - Arcs");
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
