/*
 * @(#)BezierAnim.java	1.11 98/09/13
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
import java.awt.geom.GeneralPath;
import AnimatingContext;
import DemoPanel;
import DemoSurface;

/**
 * Animated Bezier Curve.
 */
public class BezierAnim extends DemoSurface implements AnimatingContext {

    private static final int NUMPTS = 6;
    private static BasicStroke solid = new BasicStroke(10.0f, 
                        BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
    private float animpts[] = new float[NUMPTS * 2];
    private float deltas[] = new float[NUMPTS * 2];


    public BezierAnim() {
        setBackground(Color.white);
    }


    public void animate(float[] pts, float[] deltas, int index, int limit) {
        float newpt = pts[index] + deltas[index];
        if (newpt <= 0) {
            newpt = -newpt;
            deltas[index] = (float) (Math.random() * 4.0 + 2.0);
        } else if (newpt >= (float) limit) {
            newpt = 2.0f * limit - newpt;
            deltas[index] = - (float) (Math.random() * 4.0 + 2.0);
        }
        pts[index] = newpt;
    }


    public void reset(int w, int h) {
        for (int i = 0; i < animpts.length; i += 2) {
            animpts[i + 0] = (float) (Math.random() * w);
            animpts[i + 1] = (float) (Math.random() * h);
            deltas[i + 0] = (float) (Math.random() * 6.0 + 4.0);
            deltas[i + 1] = (float) (Math.random() * 6.0 + 4.0);
            if (animpts[i + 0] > w / 2.0f) {
                deltas[i + 0] = -deltas[i + 0];
            }
            if (animpts[i + 1] > h / 2.0f) {
                deltas[i + 1] = -deltas[i + 1];
            }
        }
    }


    public void step(int w, int h) {
        for (int i = 0; i < animpts.length; i += 2) {
            animate(animpts, deltas, i + 0, w);
            animate(animpts, deltas, i + 1, h);
        }
    }


    public void drawDemo(int w, int h, Graphics2D g2) {
        float[] ctrlpts = animpts;
        int len = ctrlpts.length;
        float prevx = ctrlpts[len - 2];
        float prevy = ctrlpts[len - 1];
        float curx = ctrlpts[0];
        float cury = ctrlpts[1];
        float midx = (curx + prevx) / 2.0f;
        float midy = (cury + prevy) / 2.0f;
        GeneralPath gp = new GeneralPath(GeneralPath.WIND_NON_ZERO);
        gp.moveTo(midx, midy);
        for (int i = 2; i <= ctrlpts.length; i += 2) {
            float x1 = (midx + curx) / 2.0f;
            float y1 = (midy + cury) / 2.0f;
            prevx = curx;
            prevy = cury;
            if (i < ctrlpts.length) {
                curx = ctrlpts[i + 0];
                cury = ctrlpts[i + 1];
            } else {
                curx = ctrlpts[0];
                cury = ctrlpts[1];
            }
            midx = (curx + prevx) / 2.0f;
            midy = (cury + prevy) / 2.0f;
            float x2 = (prevx + midx) / 2.0f;
            float y2 = (prevy + midy) / 2.0f;
            gp.curveTo(x1, y1, x2, y2, midx, midy);
        }
        gp.closePath();
        g2.setColor(Color.blue);
        g2.setStroke(solid);
        g2.draw(gp);
        g2.setColor(Color.green);
        g2.fill(gp);
    }


    public static void main(String argv[]) {
        final DemoPanel dp = new DemoPanel(new BezierAnim());
        Frame f = new Frame("Java2D Demo - BezierAnim");
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
