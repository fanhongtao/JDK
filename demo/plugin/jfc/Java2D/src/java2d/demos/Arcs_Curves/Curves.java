/*
 * Copyright (c) 2003 Sun Microsystems, Inc. All  Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * -Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduct the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT
 * BE LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT
 * OF OR RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN
 * IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that Software is not designed, licensed or intended for
 * use in the design, construction, operation or maintenance of any nuclear
 * facility.
 */

/*
 * @(#)Curves.java	1.23 03/01/23
 */

package java2d.demos.Arcs_Curves;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.PathIterator;
import java.awt.geom.FlatteningPathIterator;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import java2d.Surface;


/**
 * CubicCurve2D & QuadCurve2D curves includes FlattenPathIterator example.
 */
public class Curves extends Surface {

    private static Color colors[] = { Color.blue, Color.green, Color.red };

    public Curves() {
        setBackground(Color.white);
    }


    public void render(int w, int h, Graphics2D g2) {

        int y = 0;
        g2.setColor(Color.black);
        FontRenderContext frc = g2.getFontRenderContext();
        TextLayout tl = new TextLayout("QuadCurve2D", g2.getFont(), frc);
        float xx = (float) (w*.5-tl.getBounds().getWidth()/2);
        tl.draw(g2, xx, tl.getAscent());

        tl = new TextLayout("CubicCurve2D", g2.getFont(), frc);
        xx = (float) (w*.5-tl.getBounds().getWidth()/2);
        tl.draw(g2, xx, h*.5f);
        g2.setStroke(new BasicStroke(5.0f));

        float yy = 20;

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                Shape shape = null;

                if (i == 0) {
                     shape = new QuadCurve2D.Float(w*.1f,yy,w*.5f,50,w*.9f,yy);
                } else {
                     shape = new CubicCurve2D.Float(w*.1f,yy,w*.4f,yy-15,
                                            w*.6f,yy+15,w*.9f,yy);
                }
                g2.setColor(colors[j]);
                if (j != 2)
                    g2.draw(shape);

                if (j == 1 ) {
                    g2.setColor(Color.lightGray);
                    PathIterator f = shape.getPathIterator(null);
                    while ( !f.isDone() ) {
                        float[] pts = new float[6];
                        switch ( f.currentSegment(pts) ) {
                            case PathIterator.SEG_MOVETO:
                            case PathIterator.SEG_LINETO:
                                g2.fill(new Rectangle2D.Float(pts[0], pts[1], 5, 5));
                                break;
                            case PathIterator.SEG_CUBICTO :
                            case PathIterator.SEG_QUADTO :
                                g2.fill(new Rectangle2D.Float(pts[0], pts[1], 5, 5));
                                if (pts[2] != 0) {
                                    g2.fill(new Rectangle2D.Float(pts[2], pts[3], 5, 5));
                                }
                                if (pts[4] != 0) {
                                    g2.fill(new Rectangle2D.Float(pts[4], pts[5], 5, 5));
                                }
                        }
                        f.next();
                    }
                } else if (j == 2) {
                    PathIterator p = shape.getPathIterator(null);
                    FlatteningPathIterator f = new FlatteningPathIterator(p,0.1);
                    while ( !f.isDone() ) {
                        float[] pts = new float[6];
                        switch ( f.currentSegment(pts) ) {
                            case PathIterator.SEG_MOVETO:
                            case PathIterator.SEG_LINETO:
                                g2.fill(new Ellipse2D.Float(pts[0], pts[1],3,3));
                        }
                        f.next();
                    }
                }
                yy += h/6;
            }
            yy = h/2+15;
        }
    }


    public static void main(String argv[]) {
        createDemoFrame(new Curves());
    }
}
