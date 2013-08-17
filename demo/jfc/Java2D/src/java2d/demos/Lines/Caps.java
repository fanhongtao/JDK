/*
 * @(#)Caps.java	1.19 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package java2d.demos.Lines;


import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import java2d.Surface;


/**
 * Shows the three different styles of stroke ending.
 */
public class Caps extends Surface {

    private static int cap[] = { BasicStroke.CAP_BUTT,
        BasicStroke.CAP_ROUND, BasicStroke.CAP_SQUARE };
    private static String desc[] = { "Butt Cap", "Round Cap", "Square Cap" };


    public Caps() {
        setBackground(Color.white);
    }


    public void render(int w, int h, Graphics2D g2) {
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
        createDemoFrame(new Caps());
    }
}
