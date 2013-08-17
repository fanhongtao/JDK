/*
 * @(#)Intersection.java	1.21 98/09/13
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
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import AnimatingContext;
import DemoSurface;
import DemoPanel;


/**
 * Animated intersection clipping of lines, an image and a textured rectangle.
 */
public class Intersection extends DemoSurface implements AnimatingContext {

    private static Image img;
    private static TexturePaint tp;
    private static final int IN = 0;
    private static final int OUT = 1;

    private int rectW, rectH, x1, y1, x2, y2;
    private int direction = IN;
    private int fillType = 0;


    public Intersection() {
        img = getImage("clouds.jpg");
        Font f = new Font("serif", Font.ITALIC, 12);    
        FontRenderContext frc = new FontRenderContext(null, false, false);
        TextLayout t = new TextLayout("Java2D", f, frc);        
        int sw = (int) t.getBounds().getWidth();
        int sh = (int) (t.getAscent() + t.getDescent());
        BufferedImage bi = new BufferedImage(sw, sh, BufferedImage.TYPE_INT_RGB);
        Graphics2D big = bi.createGraphics();
        big.setBackground(Color.gray);
        big.setColor(Color.cyan);
        t.draw(big, 0, (float) t.getAscent());
        tp = new TexturePaint(bi, new Rectangle(0,0,sw,sh));
    }


    public Color getBackground() {
        return Color.white;
    }


    public void reset(int w, int h) {
        rectW = w/3;
        rectH = h/3;
        x1 = w/6;
        y1 = h/6;
        x2 = x1+rectW;
        y2 = y1+rectH;
    }


    public void step(int w, int h) {
        if (direction == IN) {
            ++x1; ++y1; --x2; --y2;
            if (x1 >= x2 || y1 >= y2)
                direction = OUT;
        }
        if (direction == OUT) {
            --x1; --y1; ++x2; ++y2;
            if (x1 <= w/6) {
                direction = IN;
                fillType++;
            }
        }
    }


    public void drawDemo(int w, int h, Graphics2D g2) {

        g2.setColor(Color.black);
        Font font = g2.getFont();
        FontRenderContext frc = g2.getFontRenderContext();
        TextLayout tl = new TextLayout("Intersection Clipping", font, frc);
        Rectangle2D rect1 = tl.getBounds();
        tl.draw(g2, (int) (w/2-rect1.getWidth()/2), (int) rect1.getHeight());
        
        rect1.setRect(x1, y1, rectW, rectH);
        g2.setClip(rect1);
        Rectangle rect2 = new Rectangle(x2, y2, rectW, rectH);
        g2.clip(rect2);

        switch (fillType%3) {
            case 0 : 
                g2.setColor(Color.gray);
                g2.fill(rect1);
                g2.setColor(Color.yellow);
                for (int j = y1; j < y1+rectH; j+=5) {
                    g2.drawLine(0,j,w,j);
                }
                break;
            case 1 : 
                g2.drawImage(img, x1, y1, rectW, rectH, null);
                break;
            case 2 : 
                g2.setPaint(tp);
                g2.fill(rect1);
        }

        g2.setClip(new Rectangle(0, 0, w, h));

        g2.setColor(Color.black);
        g2.draw(rect1);
        g2.draw(rect2);
    }


    public static void main(String argv[]) {
        final DemoPanel dp = new DemoPanel(new Intersection());
        Frame f = new Frame("Java2D Demo - Intersection");
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
