/*
 * @(#)FadeAnim.java	1.26 98/09/13
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

package demos.Composite;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import AnimatingContext;
import DemoSurface;
import DemoPanel;


/**
 * Animation of compositing shapes, text and images fading in and out.
 */
public class FadeAnim extends DemoSurface implements AnimatingContext {

    private static Image imgs[] = new Image[10];
    private static final int UP = 0;
    private static final int DOWN = 1;
    private static Color colors[] = { Color.blue, Color.cyan, Color.green,
                        Color.magenta, Color.orange, Color.pink,
                        Color.red, Color.yellow, Color.lightGray,
                        Color.white };
    private static BasicStroke bs5 = new BasicStroke(5);
    private static FontRenderContext frc = 
            new FontRenderContext(null, false, false);

    private double[] xx, yy;
    private Object[] objects;
    private float alphas[];
    private int alphaDirection[];
    private int imgN;


    public FadeAnim() {
        setBackground(Color.black);

        objects = new Object[17];

        if (containsImage("jumptojavastrip-0.gif")) {
            for (int i = 0; i < 10; i++) {
                imgs[i] = getImage("jumptojavastrip-" + String.valueOf(i) + ".gif");
            }
        } else {
            objects[0] = getImage("jumptojavastrip.gif");
            for (int i=0, x=0; i < 10; i++, x+=80) {
                imgs[i] = getCroppedImage((Image) objects[0], x, 0, 80, 80);
            }
        }
        objects[0] = imgs[0];
        objects[1] = new Ellipse2D.Float();
        objects[2] = new Rectangle2D.Float();
        objects[3] = new Arc2D.Float();
        objects[4] = null;
        objects[5] = new RoundRectangle2D.Float();
        objects[6] = new Ellipse2D.Float();
        objects[7] = new Rectangle2D.Float();
        objects[8] = null;
        objects[9] = new Arc2D.Float();
        objects[10] = new QuadCurve2D.Float();
        objects[11] = new CubicCurve2D.Float();
        objects[12] = null;
        objects[13] = new RoundRectangle2D.Float();
        objects[14] = getImage("duke.gif");
        objects[15] = getImage("star7.gif");

        xx = new double[objects.length];
        yy = new double[objects.length];
        alphas = new float[objects.length];
        alphaDirection = new int[objects.length];
        for (int i = 0; i < objects.length; i++) {
            alphas[i] = (float) Math.random();
            alphaDirection[i] = ((i%2) == 0) ? UP : DOWN;
        }
    }


    public void getRandomXY(int i, int w, int h) {
        if (objects[i] instanceof TextLayout) {
            xx[i] = Math.random() * 
                    (w - ((TextLayout) objects[i]).getBounds().getWidth());
            double y = Math.random() * h;
            yy[i] = (y < ((TextLayout) objects[i]).getAscent()) 
                    ? ((TextLayout) objects[i]).getAscent() : y;
        } else if (objects[i] instanceof Image) {
            xx[i] = Math.random() * (w - ((Image) objects[i]).getWidth(this));
            yy[i] = Math.random() * (h - ((Image) objects[i]).getHeight(this));
        } else {
            Rectangle bounds = ((Shape) objects[i]).getBounds();
            xx[i] = Math.random() * (w - bounds.width);
            yy[i] = Math.random() * (h - bounds.height);
        }
    }


    public void reset(int w, int h) {
        Font f = new Font("Courier", Font.BOLD, 48);
        objects[4] = new TextLayout("Alpha", f, frc);
        f = new Font("serif", Font.BOLD + Font.ITALIC, 32);
        objects[8] = new TextLayout("Composite", f, frc);
        f = new Font("Helvetica", Font.PLAIN, 32);
        objects[12] = new TextLayout("Java2D", f, frc);
        GeneralPath p = new GeneralPath();
        p.moveTo(- w / 6.0f / 2.0f, - h / 6.0f / 8.0f);
        p.lineTo(+ w / 6.0f / 2.0f, - h / 6.0f / 8.0f);
        p.lineTo(- w / 6.0f / 4.0f, + h / 6.0f / 2.0f);
        p.lineTo(+         0.0f, - h / 6.0f / 2.0f);
        p.lineTo(+ w / 6.0f / 4.0f, + h / 6.0f / 2.0f);
        p.closePath();
        objects[16] = p;
        for (int i = 0; i < objects.length; i++ ) {
            getRandomXY(i, w, h);
        }
    }


    public void step(int w, int h) {
        if (++imgN == imgs.length) {
            imgN = 0;
        }
        objects[0] = imgs[imgN];    
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] == null) {
                continue;
            }
            if (alphaDirection[i] == UP) {
                if ((alphas[i] += 0.05) > .99) {
                    alphaDirection[i] = DOWN;
                    alphas[i] = 1.0f;
                }
            } else if (alphaDirection[i] == DOWN) {
                if ((alphas[i] -= .05) < 0.01) {
                    alphaDirection[i] = UP;
                    alphas[i] = 0;
                    getRandomXY(i, w, h);
                }
            }
            if (objects[i] instanceof Ellipse2D) {
                ((Ellipse2D) objects[i]).setFrame(0, 0, w/6, w/6);
            } else if (objects[i] instanceof Rectangle2D) {
                ((Rectangle2D) objects[i]).setRect(0, 0, w/6, h/6);
            } else if (objects[i] instanceof RoundRectangle2D) {
                ((RoundRectangle2D) objects[i]).setRoundRect(
                            0,0,w/8,w/8,10,10); 
            } else if (objects[i] instanceof Arc2D) {
                ((Arc2D) objects[i]).setArc(
                            0, 0, w/5, w/5, 45, 270, Arc2D.PIE);
            } else if (objects[i] instanceof QuadCurve2D) {
                ((QuadCurve2D) objects[i]).setCurve(
                            0, 0, +w/4, -h/8, +w/2, 0);
            } else if (objects[i] instanceof CubicCurve2D) {
                ((CubicCurve2D) objects[i]).setCurve(
                            0, 0, +30, -h/8, +h/8, +60, +90, 0);
            }
        }
    }


    public void drawDemo(int w, int h, Graphics2D g2) {

        for (int i = 0; i < objects.length; i++) {
            AlphaComposite ac = AlphaComposite.getInstance(
                                   AlphaComposite.SRC_OVER, alphas[i]);
            g2.setComposite(ac);
            g2.setColor(colors[i%colors.length]);
            g2.translate(xx[i], yy[i]);
            if (objects[i] instanceof TextLayout) {
                ((TextLayout)objects[i]).draw(g2, 0, 0);
            } else if (objects[i] instanceof Image ) {
                 g2.drawImage((Image) objects[i], 0, 0, this);
            } else if (objects[i] instanceof QuadCurve2D 
                        || objects[i] instanceof CubicCurve2D) {
                 g2.setStroke(bs5);
                 g2.draw((Shape) objects[i]);
             } else {
                 g2.fill((Shape) objects[i]);
             }
            g2.translate(-xx[i], -yy[i]);
        }
    }


    public static void main(String argv[]) {
        final DemoPanel dp = new DemoPanel(new FadeAnim());
        Frame f = new Frame("Java2D Demo - FadeAnim");
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
