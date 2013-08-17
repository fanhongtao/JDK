/*
 * @(#)FillStroke.java	1.16 98/09/13
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
import java.awt.font.TextLayout;
import java.awt.geom.GeneralPath;
import DemoSurface;
import DemoPanel;


/**
 * Basic implementation of GeneralPath, filling & drawing a path w/o closing it.
 */
public class FillStroke extends DemoSurface {


    public FillStroke() {
        setBackground(Color.white);
    }


    public void drawDemo(int w, int h, Graphics2D g2) {
        GeneralPath p = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        p.moveTo( w*.5f, h*.15f);
        p.lineTo( w*.8f, h*.75f);
        p.lineTo( w*.2f, h*.75f);
        g2.setColor(Color.lightGray);
        g2.fill(p);
        g2.setColor(Color.black);
        g2.setStroke(new BasicStroke(10));
        g2.draw(p);
        TextLayout tl = new TextLayout("Fill, Stroke", 
                                g2.getFont(), g2.getFontRenderContext());
        tl.draw(g2, (float)(w/2-tl.getBounds().getWidth()/2), h*.85f);
    }


    public static void main(String s[]) {
        Frame f = new Frame("Java2D Demo - FillStroke");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        f.add("Center", new DemoPanel(new FillStroke()));
        f.pack();
        f.setSize(new Dimension(400,300));
        f.show();
    }
}
