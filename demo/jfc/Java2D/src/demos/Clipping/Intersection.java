/*
 * @(#)Intersection.java	1.20 99/09/07
 *
 * Copyright (c) 1998, 1999 by Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

package demos.Clipping;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import javax.swing.*;
import AnimatingControlsSurface;
import CustomControls;



/**
 * Animated intersection clipping of lines, an image and a textured rectangle.
 */
public class Intersection extends AnimatingControlsSurface {

    private static final int HEIGHTDECREASE = 0;
    private static final int HEIGHTINCREASE = 1;
    private static final int WIDTHDECREASE = 2;
    private static final int WIDTHINCREASE = 3;

    private int xx, yy, ww, hh;
    private int direction = HEIGHTDECREASE;
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
        setBackground(Color.white);
        setControls(new Component[] { new DemoControls(this) });
    }


    public void reset(int w, int h) {
        xx = yy = 0;
        ww = w-1; hh = h;
        direction = HEIGHTDECREASE;
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
        ovals.append(new Ellipse2D.Double(10, 10, 20, 20), false);
        ovals.append(new Ellipse2D.Double(w-30, 10, 20, 20), false);
        ovals.append(new Ellipse2D.Double(10, h-30, 20, 20), false);
        ovals.append(new Ellipse2D.Double(w-30, h-30, 20, 20), false);
    }


    public void step(int w, int h) {
        if (direction == HEIGHTDECREASE) {
            yy+=2; hh-=4;
            if (yy >= h/2) {
                direction = HEIGHTINCREASE;
            }
        } else if (direction == HEIGHTINCREASE) {
            yy-=2; hh+=4;
            if (yy <= 0) {
                direction = WIDTHDECREASE;
                hh = h-1; yy = 0;
            }
        }
        if (direction == WIDTHDECREASE) {
            xx+=2; ww-=4;
            if (xx >= w/2) {
                direction = WIDTHINCREASE;
            }
        } else if (direction == WIDTHINCREASE) {
            xx-=2; ww+=4;
            if (xx <= 0) {
                direction = HEIGHTDECREASE;
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

        g2.setColor(Color.green);
        g2.fill(rect);

        g2.setClip(new Rectangle(0, 0, w, h));

        g2.setColor(Color.lightGray);
        g2.draw(rect);
        g2.setColor(Color.black);
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
            setBackground(Color.gray);
            add(toolbar = new JToolBar());
            toolbar.setFloatable(false);
            addTool("Intersect", true);
            addTool("Text", false);
            addTool("Ovals", true);
        }


        public void addTool(String str, boolean state) {
            JButton b = (JButton) toolbar.add(new JButton(str));
            b.setBackground(state ? Color.green : Color.lightGray);
            b.setSelected(state);
            b.addActionListener(this);
        }


        public void actionPerformed(ActionEvent e) {
            JButton b = (JButton) e.getSource();
            b.setSelected(!b.isSelected());
            b.setBackground(b.isSelected() ? Color.green : Color.lightGray);
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
            return new Dimension(200,37);
        }


        public void run() {
            Thread me = Thread.currentThread();
            while (thread == me) {
                if (demo.threeSixty) {
                    ((JButton) toolbar.getComponentAtIndex(1)).doClick();
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
