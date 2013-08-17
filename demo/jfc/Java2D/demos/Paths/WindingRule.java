/*
 * @(#)WindingRule.java	1.15 98/09/13
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
 * Rectangles filled to illustrate the GenerPath winding rule, determining
 * the interior of a path.
 */
public class WindingRule extends DemoSurface {

    public WindingRule() {
        setBackground(Color.white);
    }


    public void drawDemo(int w, int h, Graphics2D g2) {

        g2.translate(w*.2, h*.2);

        GeneralPath p = new GeneralPath(GeneralPath.WIND_NON_ZERO);
        p.moveTo(0.0f, 0.0f);
        p.lineTo(w*.5f, 0.0f);
        p.lineTo(w*.5f, h*.2f);
        p.lineTo(0.0f, h*.2f);
        p.closePath();

        p.moveTo(w*.05f, h*.05f);
        p.lineTo(w*.55f, h*.05f);
        p.lineTo(w*.55f, h*.25f);
        p.lineTo(w*.05f, h*.25f);
        p.closePath();

        g2.setColor(Color.lightGray);
        g2.fill(p);
        g2.setColor(Color.black);
        g2.draw(p);
        g2.drawString("NON_ZERO rule", 0, -5);

        g2.translate(0.0f, h*.45);

        p.setWindingRule(GeneralPath.WIND_EVEN_ODD);
        g2.setColor(Color.lightGray);
        g2.fill(p);
        g2.setColor(Color.black);
        g2.draw(p);
        g2.drawString("EVEN_ODD rule", 0, -5);
    }

    public static void main(String s[]) {
        Frame f = new Frame("Java2D Demo - WindingRule");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        f.add("Center", new DemoPanel(new WindingRule()));
        f.pack();
        f.setSize(new Dimension(400,300));
        f.show();
    }
}
