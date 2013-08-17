/*
 * @(#)Caps.java	1.14 98/09/13
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
import java.awt.geom.Line2D;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import DemoSurface;
import DemoPanel;


/**
 * Shows the three different styles of stroke ending.
 */
public class Caps extends DemoSurface {

    private static int cap[] = { BasicStroke.CAP_BUTT,
        BasicStroke.CAP_ROUND, BasicStroke.CAP_SQUARE };
    private static String desc[] = { "Butt Cap", "Round Cap", "Square Cap" };


    public Caps() {
        setBackground(Color.white);
    }


    public void drawDemo(int w, int h, Graphics2D g2) {

        FontRenderContext frc = g2.getFontRenderContext();
        Font font = g2.getFont();

        g2.setColor(Color.black);
        for (int i=0; i < 3; i++) {
            g2.setStroke(new BasicStroke(15, cap[i], BasicStroke.JOIN_MITER));
            g2.draw(new Line2D.Float(w/4,(i+1)*h/4,w-w/4,(i+1)*h/4));
            TextLayout tl = new TextLayout(desc[i], font, frc);
            tl.draw(g2,(float)(w/2-tl.getBounds().getWidth()/2),(i+1)*h/4-10);
        }
    }


    public static void main(String s[]) {
        Frame f = new Frame("Java2D Demo - Caps");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        f.add("Center", new DemoPanel(new Caps()));
        f.pack();
        f.setSize(new Dimension(400,300));
        f.show();
    }

}
