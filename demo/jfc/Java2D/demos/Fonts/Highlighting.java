/*
 * @(#)Highlighting.java	1.17 98/09/13
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
import java.awt.font.TextLayout;
import java.awt.font.TextHitInfo;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import AnimatingContext;
import DemoSurface;
import DemoPanel;


/**
 * Highlighting of text showing the caret, the highlight & the character
 * advances.
 */
public class Highlighting extends DemoSurface implements AnimatingContext {

    private static Color colors[] = { Color.cyan, Color.lightGray };
    private static Font smallF = new Font("Courier", Font.PLAIN, 8);
    private static FontRenderContext frc = 
            new FontRenderContext(null, false, false);
    private int[] curPos;
    private TextLayout[] layouts;
    private Rectangle2D[]  rects;
    private Font[] fonts;


    public Highlighting() {
        setBackground(Color.white);
        fonts = new Font[2];
        layouts = new TextLayout[fonts.length];
        rects = new Rectangle2D[fonts.length];
        curPos = new int[fonts.length];
        sleepAmount = 900;
    }


    public void reset(int w, int h) {
        String text[] = { "HILIGHTING", "Java2D" };
        fonts[0] = new Font("Courier",Font.PLAIN,w/text[0].length()+8);
        fonts[1] = new Font("serif", Font.BOLD,w/text[1].length());
        for (int i = 0; i < layouts.length; i++ ) {
            layouts[i]  = new TextLayout(text[i], fonts[i], frc);
            float rx = (float) (w/2-layouts[i].getBounds().getWidth()/2);
            float ry = (float) ((i == 0) ? h/3 : h * 0.75f);
            float rw = (float) (layouts[i].getBounds().getWidth());
            float rh = (float) (layouts[i].getBounds().getHeight());
            rects[i] = new Rectangle2D.Float(rx, ry, rw, rh);
            curPos[i] = 0;
        }
    }


    public void step(int w, int h) {
        for (int i = 0; i < 2; i++) {
            if (layouts[i] == null) {
                continue;
            }
            if (curPos[i]++ == layouts[i].getCharacterCount()) {
                curPos[i] = 0;
            }
        }
    }


    public void drawDemo(int w, int h, Graphics2D g2) {
        for (int i = 0; i < 2; i++) {
            float rx = (float) rects[i].getX();
            float ry = (float) rects[i].getY();
            float rw = (float) rects[i].getWidth();
            float rh = (float) rects[i].getHeight();

            // draw highlighted shape
            Shape hilite = layouts[i].getLogicalHighlightShape(0, curPos[i]);
            AffineTransform at = AffineTransform.getTranslateInstance(rx, ry);
            hilite = at.createTransformedShape(hilite);
            float hy = (float) hilite.getBounds().getY();
            float hh = (float) hilite.getBounds().getHeight();
            g2.setColor(colors[i]);
            g2.fill(hilite);

            // get caret shape
            Shape[] shapes = layouts[i].getCaretShapes(curPos[i]);
            Shape caret = at.createTransformedShape(shapes[0]);

            g2.setColor(Color.black);
            layouts[i].draw(g2, rx, ry);
            g2.draw(caret);
            g2.draw(new Rectangle2D.Float(rx,hy,rw,hh));

            // Display character advances.
            for (int j = 0; j <= layouts[i].getCharacterCount(); j++) {
                float[] cInfo = layouts[i].getCaretInfo(TextHitInfo.leading(j));
                String str = String.valueOf((int) cInfo[0]);
                TextLayout tl = new TextLayout(str,smallF,frc);
                tl.draw(g2, (float) rx+cInfo[0], hy+hh+tl.getAscent()+1.0f);
            }
        }
    }


    public static void main(String argv[]) {
        final DemoPanel dp = new DemoPanel(new Highlighting());
        Frame f = new Frame("Java2D Demo - Highlighting");
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
