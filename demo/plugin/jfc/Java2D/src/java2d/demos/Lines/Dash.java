/*
 * @(#)Dash.java	1.23 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java2d.demos.Lines;

import java.awt.*;
import java.awt.geom.*;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import java2d.Surface;


/**
 * Various shapes stroked with a dashing pattern.
 */
public class Dash extends Surface {

    public Dash() {
        setBackground(Color.white);
    }


    public void render(int w, int h, Graphics2D g2) {
        FontRenderContext frc = g2.getFontRenderContext();
        Font font = g2.getFont();
        TextLayout tl = new TextLayout("Dashes", font, frc);
        float sw = (float) tl.getBounds().getWidth();
        float sh = (float) tl.getAscent() + tl.getDescent();
        g2.setColor(Color.black);
        tl.draw(g2, (float) (w/2-sw/2), sh+5);

        BasicStroke dotted = new BasicStroke(3, BasicStroke.CAP_ROUND, 
                     BasicStroke.JOIN_ROUND, 0, new float[]{0,6,0,6}, 0);
        g2.setStroke(dotted);
        g2.drawRect(3,3,w-6,h-6);

        int x = 0; int y = h-34;
        BasicStroke bs[] = new BasicStroke[6];

        float j = 1.1f;
        for (int i = 0; i < bs.length; i++, j += 1.0f) {
            float dash[] = { j };
            BasicStroke b = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, 
                                BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
            g2.setStroke(b);
            g2.drawLine(20, y, w-20, y);
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
        createDemoFrame(new Dash());
    }
}
