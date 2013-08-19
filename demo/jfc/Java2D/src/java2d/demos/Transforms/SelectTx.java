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
 * @(#)SelectTx.java	1.28 03/01/23
 */

package java2d.demos.Transforms;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import javax.swing.*;
import java2d.AnimatingControlsSurface;
import java2d.CustomControls;


/**
 * Scaling or Shearing or Rotating an image & rectangle.
 */
public class SelectTx extends AnimatingControlsSurface {

    protected static final int RIGHT = 0;
    private static final int LEFT = 1;
    private static final int XMIDDLE = 2;
    private static final int DOWN = 3;
    private static final int UP = 4;
    private static final int YMIDDLE = 5;
    private static final int XupYup = 6;
    private static final int XdownYdown = 7;
    private static final String[] title = { "Scale" , "Shear", "Rotate" };
    private Image img, original;
    private int iw, ih;
    protected static final int SCALE = 0;
    protected static final int SHEAR = 1;
    protected static final int ROTATE = 2;
    protected int transformType = SHEAR;
    protected double sx, sy;
    protected double angdeg;
    protected int direction = RIGHT;
    protected int transformToggle;


    public SelectTx() {
        setBackground(Color.white);
        original = getImage("painting.gif");
        iw = original.getWidth(this);
        ih = original.getHeight(this);
        setControls(new Component[] { new DemoControls(this) });
    }


    public void reset(int w, int h) {
        iw = w/3;
        ih = h/3;
        img = createImage(iw, ih);
        Graphics big = img.getGraphics();
        big.drawImage(original, 0, 0, iw, ih, Color.orange, null);
        if (transformType == SCALE) {
            direction = RIGHT;
            sx = sy = 1.0;
        } else if (transformType == SHEAR) {
            direction = RIGHT;
            sx = sy = 0;
        } else {
            angdeg = 0;
        }
    }


    public void step(int w, int h) {
        int rw = iw + 10;
        int rh = ih + 10;

        if (transformType == SCALE && direction == RIGHT) {
            sx += .05;
            if (w * .5 - iw * .5 + rw * sx + 10 > w) {
                direction = DOWN;
            }
        } else if (transformType == SCALE && direction == DOWN) {
           sy += .05;
           if (h * .5 - ih * .5 + rh * sy + 20 > h) {
               direction = LEFT;
            }
        } else if (transformType == SCALE && direction == LEFT) {
            sx -= .05;
            if (rw * sx - 10 <= -(w * .5 - iw * .5)) {
                direction = UP;
            }
        } else if (transformType == SCALE && direction == UP) {
            sy -= .05;
            if (rh * sy - 20 <= -(h * .5 - ih * .5)) {
                direction = RIGHT;
                transformToggle = SHEAR;
            }
        }

        if (transformType == SHEAR && direction == RIGHT) {
            sx += .05;
            if (rw + 2 * rh * sx + 20 > w) {
                direction = LEFT;
                sx -= .1;
            }
        } else if (transformType == SHEAR && direction == LEFT) {
            sx -= .05;
            if (rw - 2 * rh * sx + 20 > w) {
                direction = XMIDDLE;
            }
        } else if (transformType == SHEAR && direction == XMIDDLE) {
            sx += .05;
            if (sx > 0) {
                direction = DOWN;
                sx = 0;
            }
        } else if (transformType == SHEAR && direction == DOWN) {
            sy -= .05;
            if (rh - 2 * rw * sy + 20 > h) {
                direction = UP;
                sy += .1;
            }
        } else if (transformType == SHEAR && direction == UP) {
            sy += .05;
            if (rh + 2 * rw * sy + 20 > h) {
                direction = YMIDDLE;
            }
        } else if (transformType == SHEAR && direction == YMIDDLE) {
            sy -= .05;
            if (sy < 0) {
                direction = XupYup;
                sy = 0;
            }
        } else if (transformType == SHEAR && direction == XupYup) {
            sx += .05; sy += .05;
            if (rw + 2 * rh * sx + 30 > w || rh + 2 * rw * sy + 30 > h) {
                direction = XdownYdown;
            }
        } else if (transformType == SHEAR && direction == XdownYdown) {
            sy -= .05; sx -= .05;
            if (sy < 0) {
                direction = RIGHT;
                sx = sy = 0.0;
                transformToggle = ROTATE;
            }
        }

        if (transformType == ROTATE) {
            angdeg += 5;
            if (angdeg == 360) { 
                angdeg = 0;
                transformToggle = SCALE;
            }
        }
    }


    public void render(int w, int h, Graphics2D g2) {

        Font font = g2.getFont();
        FontRenderContext frc = g2.getFontRenderContext();
        TextLayout tl = new TextLayout(title[transformType], font, frc);
        g2.setColor(Color.black);
        tl.draw(g2, (float) (w/2-tl.getBounds().getWidth()/2), 
            (float) (tl.getAscent()+tl.getDescent()));

        if (transformType == ROTATE) {
            String s = Double.toString(angdeg);
            g2.drawString("angdeg=" + s, 2, h-4);
        } else {
            String s = Double.toString(sx);
            s = (s.length() < 5) ? s : s.substring(0,5);
            TextLayout tlsx = new TextLayout("sx=" + s, font, frc);
            tlsx.draw(g2, 2, h-4);

            s = Double.toString(sy);
            s = (s.length() < 5) ? s : s.substring(0,5);
            g2.drawString("sy=" + s,(int)(tlsx.getBounds().getWidth()+4), h-4);
        }

        if (transformType == SCALE) {
            g2.translate(w/2-iw/2, h/2-ih/2);
            g2.scale(sx, sy);
        } else if (transformType == SHEAR) {
            g2.translate(w/2-iw/2,h/2-ih/2);
            g2.shear(sx, sy);
        } else {
            g2.rotate(Math.toRadians(angdeg),w/2,h/2);
            g2.translate(w/2-iw/2,h/2-ih/2);
        }
        
        g2.setColor(Color.orange);
        g2.fillRect(0, 0, iw+10, ih+10);
        g2.drawImage(img, 5, 5, this);
    }


    public static void main(String argv[]) {
        createDemoFrame(new SelectTx());
    }


    static class DemoControls extends CustomControls implements ActionListener {

        SelectTx demo;
        JToolBar toolbar;

        public DemoControls(SelectTx demo) {
            super(demo.name);
            this.demo = demo;
            setBackground(Color.gray);
            add(toolbar = new JToolBar());
            toolbar.setFloatable(false);
            addTool("Scale", false);
            addTool("Shear", true);
            addTool("Rotate", false);
        }

        public void addTool(String str, boolean state) {
            JButton b = (JButton) toolbar.add(new JButton(str));
            b.setBackground(state ? Color.green : Color.lightGray);
            b.addActionListener(this);
        }


        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < toolbar.getComponentCount(); i++) {
                JButton b = (JButton) toolbar.getComponentAtIndex(i);
                b.setBackground(Color.lightGray);
            }
            JButton b = (JButton) e.getSource();
            b.setBackground(Color.green);
            if (b.getText().equals("Scale")) {
                demo.transformType = demo.SCALE;
                demo.direction = demo.RIGHT;
                demo.sx = demo.sy = 1;
            } else if (b.getText().equals("Shear")) {
                demo.transformType = demo.SHEAR;
                demo.direction = demo.RIGHT;
                demo.sx = demo.sy = 0;
            } else if (b.getText().equals("Rotate")) {
                demo.transformType = demo.ROTATE;
                demo.angdeg = 0;
            }
        }

        public Dimension getPreferredSize() {
            return new Dimension(200,36);
        }


        public void run() {
            Thread me = Thread.currentThread();
            demo.transformToggle = demo.transformType;
            while (thread == me) {
                try {
                    thread.sleep(222);
                } catch (InterruptedException e) { return; }
                if (demo.transformToggle != demo.transformType) {
                    ((JButton) toolbar.getComponentAtIndex(demo.transformToggle)).doClick();
                }
            }
            thread = null;
        }
    } // End DemoControls class
} // End SelectTx class
