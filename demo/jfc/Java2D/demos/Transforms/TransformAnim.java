/*
 * @(#)TransformAnim.java	1.25 98/09/13
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

package demos.Transforms;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import AnimatingContext;
import DemoSurface;
import DemoPanel;


/**
 * Animation of shapes, text and images rotating, scaling and translating
 * around a canvas.
 */
public class TransformAnim extends DemoSurface implements AnimatingContext {

    private static BasicStroke bs = new BasicStroke(5.0f); 
    private static final int UP = 0;
    private static final int DOWN = 1;
    private static Image imgs[] = new Image[10];
    private static Color colors[] = { Color.blue, Color.cyan, Color.green,
                Color.magenta, Color.orange, Color.pink, Color.red, 
                Color.yellow, Color.lightGray, Color.white };
    private static FontRenderContext frc = 
            new FontRenderContext(null, false, false);

    private double[] xx, yy;
    private Object[] objects;
    private double[] ix, iy;
    private int rotate[];
    private double scale[];
    private int scaleDirection[];
    private int imgN;


    public TransformAnim() {

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
        objects[1] = new Rectangle2D.Float(0,0,30,60);
        objects[2] = new Ellipse2D.Float(0,0,30,30);
        objects[3] = new Arc2D.Float(0,0,35,35,45,270, Arc2D.PIE);
        Font f = new Font("Times New Roman", Font.PLAIN, 32);
        objects[4] = new TextLayout("Scale", f, frc).getOutline(null);
        f = new Font("serif", Font.BOLD + Font.ITALIC, 24);
        objects[5] = new TextLayout("Rotate", f, frc).getOutline(null);
        objects[6] = new QuadCurve2D.Float(0,0,20,60,60,0);
        objects[7] = new CubicCurve2D.Float(0,0,30,-60,60,60,90,0);
        objects[8] = new RoundRectangle2D.Float(0,0,40,40,10,10);
        objects[9] = getImage("duke.gif");


        rotate = new int[objects.length];
        for (int i = 0; i < objects.length; i++) {
            rotate[i] = (int)(Math.random() * 360);
        }

        scale = new double[objects.length];
        scaleDirection = new int[objects.length];
        for (int i = 0; i < objects.length; i++) {
            scale[i] = Math.random() * 1.5;
            scaleDirection[i] = ((i%2) == 0) ? UP : DOWN;
        }

        xx = new double[objects.length];
        yy = new double[objects.length];
        ix = new double[objects.length];
        iy = new double[objects.length];

        for (int i = 0; i < objects.length; i++ ) {
            ix[i] = 5.0;
            iy[i] = 3.0;
        }

    }


    public void reset(int w, int h) {
        for (int i = 0; i < objects.length; i++ ) {
            xx[i] = Math.random()*w;
            yy[i] = Math.random()*h;
        }
        objects[10] = new QuadCurve2D.Float(0,0,w/8,h/4,w/4,0);
        objects[11] = new Rectangle2D.Float(0,0,w/10,w/10);
        objects[12] = new RoundRectangle2D.Float(0,0,w/8,h/8,10,10);
        objects[13] = new Arc2D.Float(0,0,w/7,w/7,45,270, Arc2D.PIE);
        Font f = new Font("Times New Roman", Font.ITALIC, w/8);
        objects[14] = new TextLayout("Translate",f,frc).getOutline(null);
        objects[15] = new CubicCurve2D.Float(0,0,w/12,-w/2,w/6,w/2,w/3,0);
        GeneralPath p = new GeneralPath();
        p.moveTo(- w / 6.0f / 2.0f, - h / 6.0f / 8.0f);
        p.lineTo(+ w / 6.0f / 2.0f, - h / 6.0f / 8.0f);
        p.lineTo(- w / 6.0f / 4.0f, + h / 6.0f / 2.0f);
        p.lineTo(+         0.0f, - h / 6.0f / 2.0f);
        p.lineTo(+ w / 6.0f / 4.0f, + h / 6.0f / 2.0f);
        p.closePath();
        objects[16] = p;
    }


    public void step(int w, int h) {
        if (++imgN == imgs.length) {
            imgN = 0;
        }
        objects[0] = imgs[imgN];    
        for (int i = 0; i < objects.length; i++) {
            xx[i] += ix[i];
            yy[i] += iy[i];
            if (xx[i] > w) {
                xx[i] = w - 1;
                ix[i] = Math.random() * -w/32 - 1;
            }
            if (xx[i] < 0) {
                xx[i] = 2;
                ix[i] = Math.random() * w/32 + 1;
            }
            if (yy[i] > h ) {
                yy[i] = h-2;
                iy[i] = Math.random() * -h/32 - 1;
            }
            if (yy[i] < 0) {
                yy[i] = 2;
                iy[i] = Math.random() * h/32 + 1;
            }
            if ((rotate[i]+=5) == 360) {
                rotate[i] = 0;
            }
            if (scaleDirection[i] == UP) {
                if ((scale[i] += 0.05) > 1.5) {
                    scaleDirection[i] = DOWN;
                }
            } else if (scaleDirection[i] == DOWN) {
                if ((scale[i] -= .05) < 0.5) {
                    scaleDirection[i] = UP;
                }
            }
        }
    }


    public void drawDemo(int w, int h, Graphics2D g2) {

        AffineTransform at = new AffineTransform();
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] == null) {
                continue;
            }
            at.setToIdentity();
            at.translate(xx[i], yy[i]);
            at.rotate(Math.toRadians(rotate[i]));
            at.scale(scale[i],scale[i]);
            if (objects[i] instanceof Image) {
                g2.drawImage((Image) objects[i], at, this);
            } else {
                g2.setColor(colors[i%colors.length]);
                if (objects[i] instanceof QuadCurve2D 
                        || objects[i] instanceof CubicCurve2D) 
                {
                    g2.setStroke(bs);
                    g2.draw(at.createTransformedShape((Shape) objects[i]));
                } else {
                    g2.fill(at.createTransformedShape((Shape) objects[i]));
                }
            }
        }
    }


    public static void main(String argv[]) {
        final DemoPanel dp = new DemoPanel(new TransformAnim());
        Frame f = new Frame("Java2D Demo - TransformAnim");
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
