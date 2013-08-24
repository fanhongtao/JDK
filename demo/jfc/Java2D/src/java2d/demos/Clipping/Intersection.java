/*
 * @(#)Intersection.java	1.32 06/08/29
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

/*
 * @(#)Intersection.java	1.32 06/08/29
 */

package java2d.demos.Clipping;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import javax.swing.*;
import java2d.AnimatingControlsSurface;
import java2d.CustomControls;

import static java.awt.Color.*;



/**
 * Animated intersection clipping of lines, an image and a textured rectangle.
 */
public class Intersection extends AnimatingControlsSurface {

    private static final int HEIGHT_DECREASE = 0;
    private static final int HEIGHT_INCREASE = 1;
    private static final int  WIDTH_DECREASE = 2;
    private static final int  WIDTH_INCREASE = 3;

    private int xx, yy, ww, hh;
    private int direction = HEIGHT_DECREASE;
    private int angdeg;
    private Shape textshape;
    private double sw, sh;
    private GeneralPath ovals;
    private Rectangle2D rectshape;
    private DemoControls controls;
    protected boolean doIntersection = true;
    protected boolean doOvals = true;
    protected boolean doText;
    protected boolean threeSixty;


    public Intersection() {
        setBackground(WHITE);
        setControls(new Component[] { new DemoControls(this) });
    }


    public void reset(int w, int h) {
        xx = yy = 0;
        ww = w-1; hh = h;
        direction = HEIGHT_DECREASE;
        angdeg = 0;
        FontRenderContext frc = new FontRenderContext(null, true, false);
        Font f = new Font("serif",Font.BOLD,32);
        TextLayout tl = new TextLayout("J2D", f, frc);
        sw = tl.getBounds().getWidth();
        sh = tl.getBounds().getHeight();
        int size = Math.min(w, h);
        double sx = (size-40)/sw;
        double sy = (size-100)/sh;
        AffineTransform Tx = AffineTransform.getScaleInstance(sx, sy);
        textshape = tl.getOutline(Tx);
        rectshape = textshape.getBounds();
        sw = rectshape.getWidth();
        sh = rectshape.getHeight();
        ovals = new GeneralPath();
        ovals.append(new Ellipse2D.Double(  10,   10, 20, 20), false);
        ovals.append(new Ellipse2D.Double(w-30,   10, 20, 20), false);
        ovals.append(new Ellipse2D.Double(  10, h-30, 20, 20), false);
        ovals.append(new Ellipse2D.Double(w-30, h-30, 20, 20), false);
    }


    public void step(int w, int h) {
        if (direction == HEIGHT_DECREASE) {
            yy+=2; hh-=4;
            if (yy >= h/2) {
                direction = HEIGHT_INCREASE;
            }
        } else if (direction == HEIGHT_INCREASE) {
            yy-=2; hh+=4;
            if (yy <= 0) {
                direction = WIDTH_DECREASE;
                hh = h-1; yy = 0;
            }
        }
        if (direction == WIDTH_DECREASE) {
            xx+=2; ww-=4;
            if (xx >= w/2) {
                direction = WIDTH_INCREASE;
            }
        } else if (direction == WIDTH_INCREASE) {
            xx-=2; ww+=4;
            if (xx <= 0) {
                direction = HEIGHT_DECREASE;
                ww = w-1; xx = 0;
            }
        }
        if ((angdeg += 5) == 360) { 
            angdeg = 0;
            threeSixty = true;
        }
    }


    public void render(int w, int h, Graphics2D g2) {

        Rectangle rect = new Rectangle(xx, yy, ww, hh);
       
        AffineTransform Tx = new AffineTransform();
        Tx.rotate(Math.toRadians(angdeg),w/2,h/2);
        Tx.translate(w/2-sw/2, sh+(h-sh)/2);

        GeneralPath path = new GeneralPath();
        if (doOvals) {
            path.append(ovals, false);
        } 
        if (doText) {
            path.append(Tx.createTransformedShape(textshape), false);
        } else {
            path.append(Tx.createTransformedShape(rectshape), false);
        }

        if (doIntersection) {
            g2.clip(rect);
            g2.clip(path);
        }

        g2.setColor(GREEN);
        g2.fill(rect);

        g2.setClip(new Rectangle(0, 0, w, h));

        g2.setColor(LIGHT_GRAY);
        g2.draw(rect);
        g2.setColor(BLACK);
        g2.draw(path);
    }


    public static void main(String argv[]) {
        createDemoFrame(new Intersection());
    }


    static class DemoControls extends CustomControls implements ActionListener {

        Intersection demo;
        JToolBar toolbar;

        public DemoControls(Intersection demo) {
            super(demo.name);
            this.demo = demo;
            add(toolbar = new JToolBar());
            toolbar.setFloatable(false);
            addTool("Intersect", true );
            addTool("Text",      false);
            addTool("Ovals",     true );
        }


        public void addTool(String str, boolean state) {
            JToggleButton b = (JToggleButton) toolbar.add(new JToggleButton(str));
            b.setFocusPainted(false);
            b.setSelected(state);
            b.addActionListener(this);
            int width = b.getPreferredSize().width;
            Dimension prefSize = new Dimension(width, 21);
            b.setPreferredSize(prefSize);
            b.setMaximumSize(  prefSize);
            b.setMinimumSize(  prefSize);
        }


        public void actionPerformed(ActionEvent e) {
            JToggleButton b = (JToggleButton) e.getSource();
            if (b.getText().equals("Intersect")) {
                demo.doIntersection = b.isSelected();
            } else if (b.getText().equals("Ovals")) {
                demo.doOvals = b.isSelected();
            } else if (b.getText().equals("Text")) {
                demo.doText = b.isSelected();
            }
            if (demo.animating.thread == null) {
                demo.repaint();
            }
        }

        public Dimension getPreferredSize() {
            return new Dimension(200,40);
        }


        public void run() {
            Thread me = Thread.currentThread();
            while (thread == me) {
                if (demo.threeSixty) {
                    ((AbstractButton) toolbar.getComponentAtIndex(1)).doClick();
                    demo.threeSixty = false;
                }
                try {
                    thread.sleep(500);
                } catch (InterruptedException e) { return; }
            }
            thread = null;
        }
    } // End DemoControls
} // End Intersection
