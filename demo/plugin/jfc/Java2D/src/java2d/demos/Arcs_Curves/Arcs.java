/*
 * @(#)Arcs.java	1.19 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java2d.demos.Arcs_Curves;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Arc2D;
import java.awt.geom.AffineTransform;
import java2d.AnimatingSurface;


/**
 * Arc2D Open, Chord & Pie arcs; Animated Pie Arc.
 */
public class Arcs extends AnimatingSurface {

    private static String types[] = {"Arc2D.OPEN","Arc2D.CHORD","Arc2D.PIE"};
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


    public void render(int w, int h, Graphics2D g2) {

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
        createDemoFrame(new Arcs());
    }
}
