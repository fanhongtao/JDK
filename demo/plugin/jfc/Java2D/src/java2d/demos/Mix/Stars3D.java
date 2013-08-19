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
 * @(#)Stars3D.java	1.24 03/01/23
 */

package java2d.demos.Mix;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.font.FontRenderContext;
import javax.swing.*;
import java2d.ControlsSurface;
import java2d.CustomControls;



/**
 * Generate a 3D text shape with GeneralPath, render a number of small
 * multi-colored rectangles and then render the 3D text shape.
 */
public class Stars3D extends ControlsSurface {

    private static Color colors[] = { Color.red, Color.green, Color.white };
    private static AffineTransform at = AffineTransform.getTranslateInstance(-5, -5);
    private Shape shape, tshape;
    private Shape ribbon;
    protected int fontSize = 72;
    protected String text = "Java2D";
    protected int numStars = 300;
    private DemoControls controls;


    public Stars3D() {
        setBackground(Color.black);
        setControls(new Component[] { new DemoControls(this) });
    }


    public void render(int w, int h, Graphics2D g2) {

        Rectangle2D rect = new Rectangle2D.Double();
        for (int i = 0; i < numStars; i++) {
            g2.setColor(colors[i%3]);
            g2.setComposite(AlphaComposite.getInstance(
                         AlphaComposite.SRC_OVER, (float) Math.random()));
            rect.setRect(w*Math.random(), h*Math.random(),2,2);
            g2.fill(rect);
        }

        FontRenderContext frc = g2.getFontRenderContext();
        Font font = new Font("serif.bolditalic", Font.PLAIN, fontSize);
        shape = font.createGlyphVector(frc, text).getOutline();
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
        createDemoFrame(new Stars3D());
    }


    static class DemoControls extends CustomControls implements ActionListener {

        Stars3D demo;
        JTextField tf1, tf2;

        public DemoControls(Stars3D demo) {
            super(demo.name);
            this.demo = demo;
            setBackground(Color.gray);
            JLabel l = new JLabel("  Text:");
            l.setForeground(Color.black);
            add(l);
            add(tf1 = new JTextField(demo.text));
            tf1.setPreferredSize(new Dimension(60,20));
            tf1.addActionListener(this);
            l = new JLabel("  Size:");
            l.setForeground(Color.black);
            add(l);
            add(tf2 = new JTextField(String.valueOf(demo.fontSize)));
            tf2.setPreferredSize(new Dimension(30,20));
            tf2.addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            try { 
                if (e.getSource().equals(tf1)) {
                    demo.text = tf1.getText().trim();
                } else if (e.getSource().equals(tf2)) {
                    demo.fontSize = Integer.parseInt(tf2.getText().trim());
                    if (demo.fontSize < 10) {
                        demo.fontSize = 10;
                    }
                }
                demo.repaint();
            } catch (Exception ex) {}
        }

        public Dimension getPreferredSize() {
            return new Dimension(200,32);
        }


        public void run() {
            Thread me = Thread.currentThread();
            try { thread.sleep(999); } catch (Exception e) { return; }
            int width = getSize().width;
            int size[] = { (int)(width*.25), (int)(width*.35)};
            String str[] = { "JAVA", "J2D" };
            while (thread == me) {
                for (int i = 0; i < 2; i++) {
                    demo.fontSize = size[i];
                    tf2.setText(String.valueOf(demo.fontSize));
                    tf1.setText(demo.text = str[i]);
                    demo.repaint();
                    try {
                        thread.sleep(5555);
                    } catch (InterruptedException e) { return; }
                }
            }
            thread = null;
        }
    } // End DemoControls
} // End Stars3D
