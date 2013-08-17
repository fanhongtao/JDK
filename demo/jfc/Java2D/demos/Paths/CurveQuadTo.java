/*
 * @(#)CurveQuadTo.java	1.15 98/09/13
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

package demos.Paths;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import DemoSurface;
import DemoPanel;


/**
 * Cubic & Quad curves implemented through GeneralPath.
 */
public class CurveQuadTo extends DemoSurface {

    public CurveQuadTo() {
        setBackground(Color.white);
    }


    public void drawDemo(int w, int h, Graphics2D g2) {
        GeneralPath p = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        p.moveTo(w*.2f, h*.25f);
        p.curveTo(w*.4f, h*.5f, w*.6f, 0.0f, w*.8f, h*.25f);
        p.moveTo(w*.2f, h*.6f);
        p.quadTo(w*.5f, h*1.0f, w*.8f, h*.6f);
        g2.setColor(Color.lightGray);
        g2.fill(p);
        g2.setColor(Color.black);
        g2.draw(p);
        g2.drawString("curveTo", (int) (w*.2), (int) (h*.25f)-5);
        g2.drawString("quadTo", (int) (w*.2), (int) (h*.6f)-5);
    }

    public static void main(String s[]) {
        Frame f = new Frame("Java2D Demo - CurveQuadTo");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        f.add("Center", new DemoPanel(new CurveQuadTo()));
        f.pack();
        f.setSize(new Dimension(400,300));
        f.show();
    }
}
