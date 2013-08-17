/*
 * @(#)Outline.java	1.18 98/09/13
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
import java.awt.geom.AffineTransform;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import java.text.AttributedString;
import java.text.AttributedCharacterIterator;
import DemoSurface;
import DemoPanel;


/**
 * Rendering text as an outline shape.
 */
public class Outline extends DemoSurface {

    public Outline() {
        setBackground(Color.white);
    }


    public void drawDemo(int w, int h, Graphics2D g2) {

        FontRenderContext frc = g2.getFontRenderContext();
        Font f = new Font("sansserif",Font.PLAIN,w/8);
        Font f1 = new Font("sansserif",Font.ITALIC,w/8);
        String s = "AttributedString";
        AttributedString as = new AttributedString(s);
        as.addAttribute(TextAttribute.FONT, f, 0, 10 );
        as.addAttribute(TextAttribute.FONT, f1, 10, s.length() );
        AttributedCharacterIterator aci = as.getIterator();
        TextLayout tl = new TextLayout (aci, frc);
        float sw = (float) tl.getBounds().getWidth();
        float sh = (float) tl.getBounds().getHeight();
        Shape sha = tl.getOutline(AffineTransform.getTranslateInstance(w/2-sw/2, h*0.2+sh/2));
        g2.setColor(Color.blue);
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(sha);
        g2.setColor(Color.magenta);
        g2.fill(sha);

        f = new Font("serif", Font.BOLD,w/6);
        tl = new TextLayout("Outline", f, frc);
        sw = (float) tl.getBounds().getWidth();
        sh = (float) tl.getBounds().getHeight();
        sha = tl.getOutline(AffineTransform.getTranslateInstance(w/2-sw/2,h*0.5+sh/2));
        g2.setColor(Color.black);
        g2.draw(sha);
        g2.setColor(Color.red);
        g2.fill(sha);

        f = new Font("sansserif",Font.ITALIC,w/8);
        AffineTransform fontAT = new AffineTransform();
        fontAT.shear(-0.2, 0.0);
        Font derivedFont = f.deriveFont(fontAT);
        tl = new TextLayout("Italic-Shear", derivedFont, frc);
        sw = (float) tl.getBounds().getWidth();
        sh = (float) tl.getBounds().getHeight();
        sha = tl.getOutline(AffineTransform.getTranslateInstance(w/2-sw/2,h*0.80f+sh/2));
        g2.setColor(Color.green);
        g2.draw(sha);
        g2.setColor(Color.black);
        g2.fill(sha);
    }


    public static void main(String s[]) {
        Frame f = new Frame("Java2D Demo - Outline");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        f.add("Center", new DemoPanel(new Outline()));
        f.pack();
        f.setSize(new Dimension(400,300));
        f.show();
    }
}
