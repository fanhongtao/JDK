/*
 * @(#)Append.java	1.15 98/09/13
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
import java.awt.geom.Rectangle2D;
import DemoSurface;
import DemoPanel;


/**
 * Simple append of rectangle to path with & without the connect.
 */
public class Append extends DemoSurface {

    public Append() { 
        setBackground(Color.white);
    }


    public void drawDemo(int w, int h, Graphics2D g2) {
        GeneralPath p = new GeneralPath(GeneralPath.WIND_NON_ZERO);
        p.moveTo(w*0.25f, h*0.2f);
        p.lineTo(w*0.75f, h*0.2f);
        p.closePath();
        p.append(new Rectangle2D.Double(w*.4, h*.3, w*.2, h*.1), false);
        g2.setColor(Color.gray);
        g2.fill(p);
        g2.setColor(Color.black);
        g2.draw(p);
        g2.drawString("Append rect to path", (int)(w*.25), (int)(h*.2)-5);

        p.reset();
        p.moveTo(w*0.25f, h*0.6f);
        p.lineTo(w*0.75f, h*0.6f);
        p.closePath();
        p.append(new Rectangle2D.Double(w*.4, h*.7, w*.2, h*.1), true);
        g2.setColor(Color.gray);
        g2.fill(p);
        g2.setColor(Color.black);
        g2.draw(p);
        g2.drawString("Append, connect", (int) (w*.25), (int) (h*.6)-5);
    }


    public static void main(String s[]) {
        Frame f = new Frame("Java2D Demo - Append");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        f.add("Center", new DemoPanel(new Append()));
        f.pack();
        f.setSize(new Dimension(400,300));
        f.show();
    }
}
