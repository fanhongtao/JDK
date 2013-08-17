/*
 * @(#)Stars3D.java	1.15 98/09/13
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

package demos.Mix;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.font.FontRenderContext;
import DemoSurface;
import DemoPanel;


/**
 * Generate a 3D text shape with GeneralPath, render a number of small
 * multi-colored rectangles and then render the 3D text shape.
 */
public class Stars3D extends DemoSurface {

    private static Color colors[] = { Color.red, Color.green, Color.white };
    private static AffineTransform at = AffineTransform.getTranslateInstance(-5, -5);
    private static Shape shape, tshape;
    private static Shape ribbon;

    static {
        Font font = new Font("serif.bolditalic", Font.PLAIN, 72);
        FontRenderContext frc = 
            new FontRenderContext(new AffineTransform(), true, true);
        shape = font.createGlyphVector(frc, "J2D").getOutline();
        tshape = at.createTransformedShape(shape);
        PathIterator pi = shape.getPathIterator(null);
        
        float seg[] = new float[6];
        float tseg[] = new float[6];
        
        GeneralPath working = new GeneralPath(GeneralPath.WIND_NON_ZERO);
        float x=0, y=0; // Current point on the path
        float tx=0, ty=0; // Transformed path point
        float cx=0, cy=0; // Last moveTo point, for SEG_CLOSE
        float tcx=0, tcy=0; // Transformed last moveTo point
        
        //
        // Iterate through the Shape and build the ribbon
        // by adding general path objects.
        //
        while(!pi.isDone()) {
            int segType = pi.currentSegment(seg);
            switch(segType) {
                case PathIterator.SEG_MOVETO:
                        at.transform(seg, 0, tseg, 0, 1);
                        x = seg[0];
                        y = seg[1];
                        tx = tseg[0];
                        ty = tseg[1];
                        cx = x;
                        cy = y;
                        tcx = tx;
                        tcy = ty;
                        break;
                case PathIterator.SEG_LINETO:
                        at.transform(seg, 0, tseg, 0, 1);
                        if (Line2D.relativeCCW(x, y, tx, ty,
                                               seg[0], seg[1]) < 0) {
                            working.moveTo(x, y);
                            working.lineTo(seg[0], seg[1]);
                            working.lineTo(tseg[0], tseg[1]);
                            working.lineTo(tx, ty);
                            working.lineTo(x, y);
                        } else {
                            working.moveTo(x, y);
                            working.lineTo(tx, ty);
                            working.lineTo(tseg[0], tseg[1]);
                            working.lineTo(seg[0], seg[1]);
                            working.lineTo(x, y);
                        }
                        
                        x = seg[0];
                        y = seg[1];
                        tx = tseg[0];
                        ty = tseg[1];
                        break;
                        
                case PathIterator.SEG_QUADTO:
                        at.transform(seg, 0, tseg, 0, 2);
                        if (Line2D.relativeCCW(x, y, tx, ty,
                                               seg[2], seg[3]) < 0) {
                            working.moveTo(x, y);
                            working.quadTo(seg[0], seg[1],
                                           seg[2], seg[3]);
                            working.lineTo(tseg[2], tseg[3]);
                            working.quadTo(tseg[0], tseg[1],
                                           tx, ty);
                            working.lineTo(x, y);
                        } else {
                            working.moveTo(x, y);
                            working.lineTo(tx, ty);
                            working.quadTo(tseg[0], tseg[1],
                                           tseg[2], tseg[3]);
                            working.lineTo(seg[2], seg[3]);
                            working.quadTo(seg[0], seg[1],
                                           x, y);
                        }
                
                        x = seg[2];
                        y = seg[3];
                        tx = tseg[2];
                        ty = tseg[3];
                        break;
        
                case PathIterator.SEG_CUBICTO:
                        at.transform(seg, 0, tseg, 0, 3);
                        if (Line2D.relativeCCW(x, y, tx, ty,
                                               seg[4], seg[5]) < 0) {
                            working.moveTo(x, y);
                            working.curveTo(seg[0], seg[1],
                                            seg[2], seg[3],
                                            seg[4], seg[5]);
                            working.lineTo(tseg[4], tseg[5]);
                            working.curveTo(tseg[2], tseg[3],
                                            tseg[0], tseg[1],
                                            tx, ty);
                            working.lineTo(x, y);
                        } else {
                            working.moveTo(x, y);
                            working.lineTo(tx, ty);
                            working.curveTo(tseg[0], tseg[1],
                                            tseg[2], tseg[3],
                                            tseg[4], tseg[5]);
                            working.lineTo(seg[4], seg[5]);
                            working.curveTo(seg[2], seg[3],
                                            seg[0], seg[1],
                                            x, y);
                        }
                
                        x = seg[4];
                        y = seg[5];
                        tx = tseg[4];
                        ty = tseg[5];
                        break;
        
                case PathIterator.SEG_CLOSE:
                        if (Line2D.relativeCCW(x, y, tx, ty,
                                               cx, cy) < 0) {
                            working.moveTo(x, y);
                            working.lineTo(cx, cy);
                            working.lineTo(tcx, tcy);
                            working.lineTo(tx, ty);
                            working.lineTo(x, y);
                        } else {
                            working.moveTo(x, y);
                            working.lineTo(tx, ty);
                            working.lineTo(tcx, tcy);
                            working.lineTo(cx, cy);
                            working.lineTo(x, y);
                        }
                        x = cx; 
                        y = cy;
                        tx = tcx;
                        ty = tcy;
            }
            pi.next();
        } // while
        ribbon = working;
    }


    public Stars3D() {
        setBackground(Color.black);
    }


    public void drawDemo(int w, int h, Graphics2D g2) {

        Rectangle2D tmp = new Rectangle2D.Double();
        for (int i = 0; i < 300; i++) {
            g2.setColor(colors[i%3]);
            g2.setComposite(AlphaComposite.getInstance(
                         AlphaComposite.SRC_OVER, (float) Math.random()));
            tmp.setRect(w*Math.random(), h*Math.random(),2,2);
            g2.fill(tmp);
        }

        g2.setComposite(AlphaComposite.SrcOver);
        Rectangle r = shape.getBounds();
        g2.translate(w*.5-r.width*.5,h*.5+r.height*.5);

        g2.setColor(Color.blue);
        g2.fill(tshape);
        g2.setColor(new Color(255, 255, 255, 200));
        g2.fill(ribbon);

        g2.setColor(Color.white);
        g2.fill(shape);

        g2.setColor(Color.blue);
        g2.draw(shape);
    }



    public static void main(String argv[]) {
        Frame f = new Frame("Java2D Demo - Stars3D");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        f.add("Center", new DemoPanel(new Stars3D()));
        f.pack();
        f.setSize(new Dimension(400,300));
        f.show();
    }
}
