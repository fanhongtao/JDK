/*
 * @(#)ClipAnim.java	1.30 06/08/29
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
 * @(#)ClipAnim.java	1.30 06/08/29
 */

package java2d.demos.Clipping;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java2d.AnimatingControlsSurface;
import java2d.CustomControls;

import static java.lang.Math.random;
import static java.awt.Color.*;


/**
 * Animated clipping of an image & composited shapes.
 */
public class ClipAnim extends AnimatingControlsSurface {

    private static Image dimg, cimg;
    private static Color   redBlend = new Color(255,   0, 0, 120);
    private static Color greenBlend = new Color(  0, 255, 0, 120);
    private static BasicStroke bs = new BasicStroke(20.0f);
    static TexturePaint texture;
    static {
        BufferedImage bi = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        Graphics2D big = bi.createGraphics();
        big.setBackground(YELLOW);
        big.clearRect(0,0,5,5);
        big.setColor(RED);
        big.fillRect(0,0,3,3);
        texture = new TexturePaint(bi,new Rectangle(0,0,5,5));
    }
    private AnimVal animval[] = new AnimVal[3]; 
    protected boolean doObjects = true;
    private Font originalFont = new Font("serif", Font.PLAIN, 12);
    private Font font;
    private GradientPaint gradient;
    private int strX, strY;
    private int dukeX, dukeY;


    public ClipAnim() {
        cimg = getImage("clouds.jpg");
        dimg = getImage("duke.gif");
        setBackground(WHITE);
        animval[0] = new AnimVal(true);
        animval[1] = new AnimVal(false);
        animval[2] = new AnimVal(false);
        setControls(new Component[] { new DemoControls(this) });
    }


    public void reset(int w, int h) {
        for (AnimVal a : animval) {
            a.reset(w, h);
        }
        gradient = new GradientPaint(0,h/2,RED,w*.4f,h*.9f,YELLOW);
        dukeX = (int) (w*.25 - dimg.getWidth (this)/2);
        dukeY = (int) (h*.25 - dimg.getHeight(this)/2);
        FontMetrics fm = getFontMetrics(originalFont);
        double sw = fm.stringWidth("CLIPPING");
        double sh = fm.getAscent() + fm.getDescent();
        double sx = (w/2-30)/sw;
        double sy = (h/2-30)/sh;
        AffineTransform Tx = AffineTransform.getScaleInstance(sx, sy);
        font = originalFont.deriveFont(Tx);
        fm = getFontMetrics(font);
        strX = (int) (w*.75 - fm.stringWidth("CLIPPING")/2);
        strY = (int) (h*.72 + fm.getAscent()/2);
    }


    public void step(int w, int h) {
        for (AnimVal a : animval) {
            if (a.isSelected) {
                a.step(w, h);
            }
        }
    }


    public void render(int w, int h, Graphics2D g2) {

        GeneralPath p1 = new GeneralPath();
        GeneralPath p2 = new GeneralPath();

        for (AnimVal a : animval) {
            if (a.isSelected) {
                double x  = a.x;
                double y  = a.y;
                double ew = a.ew;
                double eh = a.eh;
                p1.append(new   Ellipse2D.Double(x,  y,  ew,   eh   ), false);
                p2.append(new Rectangle2D.Double(x+5,y+5,ew-10,eh-10), false);
            }
        }
        if (animval[0].isSelected ||
            animval[1].isSelected || 
            animval[2].isSelected) 
        {
            g2.setClip(p1);
            g2.clip(p2);
        }

        if (doObjects) {
            int w2 = w/2;
            int h2 = h/2;
            g2.drawImage(cimg, 0, 0, w2, h2, null);
            g2.drawImage(dimg, dukeX, dukeY, null);

            g2.setPaint(texture);
            g2.fillRect(w2, 0, w2, h2);

            g2.setPaint(gradient);
            g2.fillRect(0, h2, w2, h2);

            g2.setColor(LIGHT_GRAY);
            g2.fillRect(w2, h2, w2, h2);
            g2.setColor(RED);
            g2.drawOval(w2, h2, w2-1, h2-1);
            g2.setFont(font);
            g2.drawString("CLIPPING", strX, strY);
        } else {
            g2.setColor(LIGHT_GRAY);
            g2.fillRect(0, 0, w, h);
        }
    }


    public static void main(String argv[]) {
        createDemoFrame(new ClipAnim());
    }


    public class AnimVal {
        double ix = 5.0;
        double iy = 3.0;
        double iw = 5.0;
        double ih = 3.0;
        double x, y;
        double ew, eh;   // ellipse width & height
        boolean isSelected;

        public AnimVal(boolean isSelected) {
            this.isSelected = isSelected;
        }


        public void step(int w, int h) {
            x  += ix;
            y  += iy;
            ew += iw;
            eh += ih;

            if (    ew > w/2) { ew = w/2;       iw = random() * -w/16 - 1; }
            if (    ew < w/8) { ew = w/8;       iw = random() *  w/16 + 1; }
            if (    eh > h/2) { eh = h/2;       ih = random() * -h/16 - 1; }
            if (    eh < h/8) { eh = h/8;       ih = random() *  h/16 + 1; }

            if ((x+ew) > w  ) { x = (w - ew)-1; ix = random() * -w/32 - 1; }
            if ((y+eh) > h  ) { y = (h - eh)-2; iy = random() * -h/32 - 1; }
            if (     x < 0  ) { x = 2;          ix = random() *  w/32 + 1; }
            if (     y < 0  ) { y = 2;          iy = random() *  h/32 + 1; }
        }


        public void reset(int w, int h) {
            x  =  random()*w;
            y  =  random()*h;
            ew = (random()*w)/2;
            eh = (random()*h)/2;
        }
    }


    static class DemoControls extends CustomControls implements ActionListener {

        ClipAnim demo;
        JToolBar toolbar;

        public DemoControls(ClipAnim demo) {
            super(demo.name);
            this.demo = demo;
            add(toolbar = new JToolBar());
            toolbar.setFloatable(false);
            addTool("Objects", true );
            addTool("Clip1",   true );
            addTool("Clip2",   false);
            addTool("Clip3",   false);
        }


        public void addTool(String str, boolean state) {
            JToggleButton b =
                    (JToggleButton) toolbar.add(new JToggleButton(str));
            b.setFocusPainted(false);
            b.setSelected(state);
            b.addActionListener(this);
            int width = b.getPreferredSize().width;
            Dimension prefSize = new Dimension(width, 21);
            b.setPreferredSize(prefSize);
            b.setMaximumSize(prefSize);
            b.setMinimumSize(prefSize);
        }


        public void actionPerformed(ActionEvent e) {
            JToggleButton b = (JToggleButton) e.getSource();
            if (b.getText().equals("Objects")) {
                demo.doObjects = b.isSelected();
            } else if (b.getText().equals("Clip1")) {
                demo.animval[0].isSelected = b.isSelected();
            } else if (b.getText().equals("Clip2")) {
                demo.animval[1].isSelected = b.isSelected();
            } else if (b.getText().equals("Clip3")) {
                demo.animval[2].isSelected = b.isSelected();
            }
            if (demo.animating.thread == null) {
                demo.repaint();
            }
        }

        public Dimension getPreferredSize() {
            return new Dimension(200,40);
        }


        public void run() {
            try { 
                thread.sleep(5000);
            } catch (InterruptedException e) { return; }
            ((AbstractButton) toolbar.getComponentAtIndex(2)).doClick();
            try { 
                thread.sleep(5000);
            } catch (InterruptedException e) { return; }
            if (getSize().width > 400) {
                ((AbstractButton) toolbar.getComponentAtIndex(3)).doClick();
            }
            thread = null;
        }
    } // End DemoControls
} // End ClipAnim
