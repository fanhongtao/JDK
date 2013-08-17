/*
 * @(#)Dash.java	1.17 98/09/13
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
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import DemoSurface;
import DemoPanel;


/**
 * Various shapes stroked with a dashing pattern.
 */
public class Dash extends DemoSurface {

    public Dash() {
        setBackground(Color.white);
    }


    public void drawDemo(int w, int h, Graphics2D g2) {

        FontRenderContext frc = g2.getFontRenderContext();
        Font font = g2.getFont();
        TextLayout tl = new TextLayout("Dashes", font, frc);
        float sw = (float) tl.getBounds().getWidth();
        float sh = (float) tl.getAscent() + tl.getDescent();
        g2.setColor(Color.black);
        tl.draw(g2, (float) (w/2-sw/2), sh);

        BasicStroke bs[] = new BasicStroke[6];

        int x = 0; int y = h-30;

        float j = 1.1f;
        for (int i = 0; i < bs.length; i++, j += 1.0f) {
            float dash[] = { j };
            BasicStroke b = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, 
                                BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
            g2.setStroke(b);
            g2.drawLine(5, y, w-5, y);
            bs[i] = new BasicStroke(3.0f, BasicStroke.CAP_BUTT, 
                                BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
            y += 5;
        }

        Shape shape = null;
        y = 0;
        for (int i = 0; i < 6; i++) {
            x = (i == 0 || i == 3) ? (w/3-w/5)/2 : x + w/3;
            y = (i <= 2) ? (int) sh+h/12 : h/2;

            g2.setStroke(bs[i]);
            g2.translate(x, y);  
            switch (i) {
                case 0 : shape = new Arc2D.Float(0.0f, 0.0f, w/5, h/4, 45, 270, Arc2D.PIE);
                         break;
                case 1 : shape = new Ellipse2D.Float(0.0f, 0.0f, w/5, h/4);
                         break;
                case 2 : shape = new RoundRectangle2D.Float(0.0f, 0.0f, w/5, h/4, 10.0f, 10.0f);
                         break;
                case 3 : shape = new Rectangle2D.Float(0.0f, 0.0f, w/5, h/4);
                         break;
                case 4 : shape = new QuadCurve2D.Float(0.0f,0.0f,w/10, h/2,w/5,0.0f);
                         break;
                case 5 : shape = new CubicCurve2D.Float(0.0f,0.0f,w/15,h/2, w/10,h/4,w/5,0.0f);
                         break;
            }

            g2.draw(shape);
            g2.translate(-x, -y);
        }
    }


    public static void main(String argv[]) {
        Frame f = new Frame("Java2D Demo - Dash");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        f.add("Center", new DemoPanel(new Dash()));
        f.pack();
        f.setSize(new Dimension(400,300));
        f.show();
    }
}
