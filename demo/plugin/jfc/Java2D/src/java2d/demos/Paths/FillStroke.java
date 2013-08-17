/*
 * @(#)FillStroke.java	1.22 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java2d.demos.Paths;

import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.GeneralPath;
import java2d.Surface;


/**
 * Basic implementation of GeneralPath, filling & drawing a path w/o closing it.
 */
public class FillStroke extends Surface {


    public FillStroke() {
        setBackground(Color.white);
    }


    public void render(int w, int h, Graphics2D g2) {
        GeneralPath p = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        p.moveTo( w*.5f, h*.15f);
        p.lineTo( w*.8f, h*.75f);
        p.lineTo( w*.2f, h*.75f);
        g2.setColor(Color.lightGray);
        g2.fill(p);
        g2.setColor(Color.black);
        g2.setStroke(new BasicStroke(10));
        g2.draw(p);
        TextLayout tl = new TextLayout("Fill, Stroke w/o closePath", 
                                g2.getFont(), g2.getFontRenderContext());
        tl.draw(g2, (float)(w/2-tl.getBounds().getWidth()/2), h*.85f);
    }


    public static void main(String s[]) {
        createDemoFrame(new FillStroke());
    }
}
