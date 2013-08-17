/*
 * @(#)SubPaths.java	1.17 98/09/13
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
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.font.TextLayout;
import DemoSurface;
import DemoPanel;


/**
 * Clipping an image with shapes appended to a path.
 */
public class SubPaths extends DemoSurface {

    private static Image img;

    public SubPaths() {
        setBackground(Color.white);
        img = getImage("clouds.jpg");
    }


    public void drawDemo(int w, int h, Graphics2D g2) {

        TextLayout tl = new TextLayout("Subpaths Clipping", 
                                    g2.getFont(), g2.getFontRenderContext());
        float sw = (float) tl.getBounds().getWidth();
        float sh = (float) (tl.getAscent() + tl.getDescent());
        g2.setColor(Color.black);
        tl.draw(g2, (float) (w/2-sw/2), sh);

        g2.translate(0, sh/2);

        GeneralPath p = new GeneralPath(GeneralPath.WIND_EVEN_ODD);

        for (int i = 0; i < 3; i++)
            p.append(new Rectangle(w/8+(i*w/4),h/8+(i*h/4),w/4,h/4), false);

        for (int i = 1; i < 3; i++)
            p.append(new Ellipse2D.Float(w/8+(i*w/4),h/8,w/4,h/4), false);

        for (int i = 0; i < 3; i++)
            p.append(new Arc2D.Float(5+w/8+(i*w/4),h/8+h/4,w/4,h/4,45,270, Arc2D.PIE), false);

        for (int i = 0; i < 2; i++)
            p.append(new Ellipse2D.Float(w/8+(i*w/4),h/8+h/2,w/4,h/4), false);

        g2.setClip(p);

        g2.drawImage(img,0,0,w,h,null);

        g2.setClip(new Rectangle(0,0,w,h));

        Color colors[] = { Color.green.darker(), Color.lightGray, Color.black };
        for (int i = 0, j = 7; i < colors.length; i++, j -= 3) {
            g2.setColor(colors[i]);
            g2.setStroke(new BasicStroke((float) j));
            g2.draw(p);
        }
    }


    public static void main(String s[]) {
        Frame f = new Frame("Java2D Demo - SubPaths");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        final DemoPanel dp = new DemoPanel(new SubPaths());
        f.add("Center", dp);
        f.pack();
        f.setSize(new Dimension(400,300));
        f.show();
    }
}
