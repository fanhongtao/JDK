/*
 * @(#)ACimages.java	1.21 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java2d.demos.Composite;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import java2d.Surface;


/**
 * Compositing shapes on images.
 */
public class ACimages extends Surface {

    private static String s[] = { "box", "fight", "magnify",
                        "boxwave", "globe", "snooze",
                        "tip", "thumbsup", "dukeplug"};
    private static Image imgs[] = new Image[s.length];
    private static Color colors[] = { Color.blue, Color.cyan, Color.green,
                        Color.magenta, Color.orange, Color.pink,
                        Color.red, Color.yellow, Color.lightGray };


    public ACimages() {
        setBackground(Color.white);
        for (int i = 0; i < imgs.length; i++) {
            imgs[i] = getImage(s[i] + ".gif");
        }
    }


    public void render(int w, int h, Graphics2D g2) {

        float alpha = 0.0f;
        int iw = w/3;
        int ih = (h-45)/3;
        float xx = 0, yy = 15;

        for (int i =0; i < imgs.length; i++) {

            xx = (i%3 == 0) ? 0 : xx+w/3;
            switch (i) {
                case 3 : yy = h/3+15; break;
                case 6 : yy = h/3*2+15;
            }

            g2.setComposite(AlphaComposite.SrcOver);
            g2.setColor(Color.black);
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha += .1f);
            String s = "a=" + Float.toString(alpha).substring(0,3);
            new TextLayout(s,g2.getFont(), g2.getFontRenderContext()).draw(g2, xx+3, yy-2);

            Shape shape=null;

            switch (i%3) {
                case 0 : shape = new Ellipse2D.Float(xx, yy, iw, ih);
                        break;
                case 1 : shape = new RoundRectangle2D.Float(xx, yy, iw, ih, 25, 25);
                        break;
                case 2 : shape = new Rectangle2D.Float(xx, yy, iw, ih);
                        break;
            }
            g2.setColor(colors[i]);
            g2.setComposite(ac);
            g2.fill(shape);
            g2.drawImage(imgs[i], (int) xx, (int) yy, iw, ih, null);
        }
    }


    public static void main(String s[]) {
        createDemoFrame(new ACimages());
    }
}
