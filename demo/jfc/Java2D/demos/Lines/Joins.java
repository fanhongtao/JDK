/*
 * @(#)Joins.java	1.14 98/09/13
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

package demos.Lines;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.font.TextLayout;
import DemoSurface;
import DemoPanel;


/**
 * Shows the three different styles of joining strokes together.
 */
public class Joins extends DemoSurface {

    private static int join[] = { BasicStroke.JOIN_MITER, 
        BasicStroke.JOIN_ROUND, BasicStroke.JOIN_BEVEL };
    private static String desc[] = { "Mitered", "Rounded", "Beveled" };


    public Joins() {
        setBackground(Color.white);
    }


    public void drawDemo(int w, int h, Graphics2D g2) {

        AffineTransform at = g2.getTransform();
        g2.translate(w/3, h/9);
        g2.setColor(Color.black);

        for (int i=0; i < 3; i++) {
            BasicStroke bs = new BasicStroke(20.0f, 
                    BasicStroke.CAP_BUTT, join[i]);
            GeneralPath p = new GeneralPath();
            p.moveTo(0, 0);
            p.lineTo(w/12, h/10);
            p.lineTo(0,h/5);
            g2.setStroke(bs);
            g2.draw(p);
            g2.drawString(desc[i], w/12+20, h/10);
            g2.translate(0, h/4);
        }
        g2.setTransform(at);
    }


    public static void main(String s[]) {
        Frame f = new Frame("Java2D Demo - Joins");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        f.add("Center", new Joins());
        f.pack();
        f.setSize(new Dimension(400,300));
        f.show();
    }
}
