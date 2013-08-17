/*
 * @(#)Tree.java	1.16 98/09/13
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

package demos.Fonts;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import AnimatingContext;
import DemoSurface;
import DemoPanel;


/**
 * Transformation of characters.
 */
public class Tree extends DemoSurface implements AnimatingContext {

    private char theC = 'A';
    private Character theT = new Character(theC);
    private Character theR = new Character((char) ((int) theC + 1));


    public Tree() {
        setBackground(Color.white);
        sleepAmount = 4000;
    }


    public void reset(int w, int h) { }


    public void step(int w, int h) {
        theT = new Character(theC = ((char) ((int) theC + 1)));
        theR = new Character((char) ((int) theC + 1));
        if (theR.compareTo(new Character('z')) == 0) {
            theC = 'A';
        }
    }


    public void drawDemo(int w, int h, Graphics2D g2) {
        int mindim = Math.min(w, h);
        AffineTransform at = new AffineTransform();
        at.translate((w - mindim) / 2.0,
                     (h - mindim) / 2.0);
        at.scale(mindim, mindim);
        at.translate(0.5, 0.5);
        at.scale(0.3, 0.3);
        at.translate(-(Twidth + Rwidth), FontHeight / 4.0);
        g2.transform(at);
        tree(g2, mindim * 0.3, 0);

    }


    static Font theFont = new Font("serif", Font.PLAIN, 1);
    static double Twidth = 0.6;
    static double Rwidth = 0.6;
    static double FontHeight = 0.75;
    static Color colors[] = {Color.blue,
                             Color.red.darker(),
                             Color.green.darker()};


    public void tree(Graphics2D g2d, double size, int phase) {
        g2d.setColor(colors[phase % 3]);
        new TextLayout(theT.toString(),theFont,g2d.getFontRenderContext()).draw(g2d, 0.0f, 0.0f);
        if (size > 10.0) {
            AffineTransform at = new AffineTransform();
            at.setToTranslation(Twidth, -0.1);
            at.scale(0.6, 0.6);
            g2d.transform(at);
            size *= 0.6;
            new TextLayout(theR.toString(),theFont, g2d.getFontRenderContext()).draw(g2d, 0.0f, 0.0f);
            at.setToTranslation(Rwidth+0.75, 0);
            g2d.transform(at);
            Graphics2D g2dt = (Graphics2D) g2d.create();
            at.setToRotation(-Math.PI / 2.0);
            g2dt.transform(at);
            tree(g2dt, size, phase + 1);
            g2dt.dispose();
            at.setToTranslation(.75, 0);
            at.rotate(-Math.PI / 2.0);
            at.scale(-1.0, 1.0);
            at.translate(-Twidth, 0);
            g2d.transform(at);
            tree(g2d, size, phase);
        }
        g2d.setTransform(new AffineTransform());
    }


    public static void main(String argv[]) {
        final DemoPanel dp = new DemoPanel(new Tree());
        Frame f = new Frame("Java2D Demo - Tree");
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
