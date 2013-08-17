/*
 * @(#)Curves.java	1.15 98/09/13
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
import java.awt.geom.Rectangle2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.PathIterator;
import java.awt.geom.FlatteningPathIterator;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import DemoPanel;
import DemoSurface;


/**
 * CubicCurve2D & QuadCurve2D curves includes FlattenPathIterator example.
 */
public class Curves extends DemoSurface {

    private static Color colors[] = { Color.blue, Color.green, Color.red };

    public Curves() {
        setBackground(Color.white);
    }


    public void drawDemo(int w, int h, Graphics2D g2) {

        int y = 0;
        g2.setColor(Color.black);
        FontRenderContext frc = g2.getFontRenderContext();
        TextLayout tl = new TextLayout("QuadCurve2D", g2.getFont(), frc);
        float xx = (float) (w*.5-tl.getBounds().getWidth()/2);
        tl.draw(g2, xx, tl.getAscent());

        tl = new TextLayout("CubicCurve2D", g2.getFont(), frc);
        xx = (float) (w*.5-tl.getBounds().getWidth()/2);
        tl.draw(g2, xx, h*.5f);
        g2.setStroke(new BasicStroke(5.0f));

        float yy = 20;

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                Shape shape = null;

                if (i == 0) {
                     shape = new QuadCurve2D.Float(w*.1f,yy,w*.5f,50,w*.9f,yy);
                } else {
                     shape = new CubicCurve2D.Float(w*.1f,yy,w*.4f,yy-15,
                                            w*.6f,yy+15,w*.9f,yy);
                }
                g2.setColor(colors[j]);
                if (j != 2)
                    g2.draw(shape);

                if (j == 1 ) {
                    g2.setColor(Color.lightGray);
                    PathIterator f = shape.getPathIterator(null);
                    while ( !f.isDone() ) {
                        float[] pts = new float[6];
                        switch ( f.currentSegment(pts) ) {
                            case f.SEG_MOVETO:
                            case f.SEG_LINETO:
                                g2.fill(new Rectangle2D.Float(pts[0], pts[1], 5, 5));
                                break;
                            case f.SEG_CUBICTO :
                            case f.SEG_QUADTO :
                                g2.fill(new Rectangle2D.Float(pts[0], pts[1], 5, 5));
                                if (pts[2] != 0) {
                                    g2.fill(new Rectangle2D.Float(pts[2], pts[3], 5, 5));
                                }
                                if (pts[4] != 0) {
                                    g2.fill(new Rectangle2D.Float(pts[4], pts[5], 5, 5));
                                }
                        }
                        f.next();
                    }
                } else if (j == 2) {
                    PathIterator p = shape.getPathIterator(null);
                    FlatteningPathIterator f = new FlatteningPathIterator(p,0.1);
                    while ( !f.isDone() ) {
                        float[] pts = new float[6];
                        switch ( f.currentSegment(pts) ) {
                            case f.SEG_MOVETO:
                            case f.SEG_LINETO:
                                g2.fill(new Ellipse2D.Float(pts[0], pts[1],3,3));
                        }
                        f.next();
                    }
                }
                yy += h/6;
            }
            yy = h/2+15;
        }
    }


    public static void main(String argv[]) {
        Frame f = new Frame("Java2D Demo - Curves");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        f.add("Center", new DemoPanel(new Curves()));
        f.pack();
        f.setSize(new Dimension(400,300));
        f.show();
    }
}
