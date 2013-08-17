/*
 * @(#)BullsEye.java	1.9 98/09/13
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

package demos.Colors;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import DemoSurface;
import DemoPanel;


/**
 * Creating colors with an alpha value.
 */
public class BullsEye extends DemoSurface {


    public BullsEye() {
        setBackground(Color.white);
    }


    public void drawDemo(int w, int h, Graphics2D g2) {

        Color reds[] = { Color.red.darker(), Color.red };
        for (int N = 0; N < 18; N++) {
            float i = (N + 2) / 2.0f;
            float x = (float) (5+i*(w/2/10));
            float y = (float) (5+i*(h/2/10));
            float ew = (w-10)-(i*w/10);
            float eh = (h-10)-(i*h/10);
            float alpha = (N == 0) ? 0.1f : 1.0f / (19.0f - N);
            if ( N >= 16 )
                g2.setColor(reds[N-16]);
            else
                g2.setColor(new Color(0f, 0f, 0f, alpha));
            g2.fill(new Ellipse2D.Float(x,y,ew,eh));
        }
    }


    public static void main(String s[]) {
        Frame f = new Frame("Java2D Demo - BullsEye");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        f.add("Center", new DemoPanel(new BullsEye()));
        f.pack();
        f.setSize(new Dimension(400,300));
        f.show();
    }
}
