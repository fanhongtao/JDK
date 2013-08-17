/*
 * @(#)SelectTx.java	1.20 98/09/13
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

package demos.Transforms;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import javax.swing.*;
import AnimatingContext;
import DemoSurface;
import DemoPanel;
import CustomControls;


/**
 * Scaling or Shearing or Rotating an image & rectangle.
 */
public class SelectTx extends DemoSurface implements AnimatingContext, CustomControls {

    protected static final int RIGHT = 0;
    private static final int LEFT = 1;
    private static final int XMIDDLE = 2;
    private static final int DOWN = 3;
    private static final int UP = 4;
    private static final int YMIDDLE = 5;
    private static final int XupYup = 6;
    private static final int XdownYdown = 7;
    private static final String[] title = { "Scale" , "Shear", "Rotate" };
    protected static final int SCALE = 0;
    protected static final int SHEAR = 1;
    protected static final int ROTATE = 2;
    protected int transformType = SHEAR;
    protected double sx, sy;
    protected double angdeg;
    protected int direction = RIGHT;
    private Image img, original;
    protected int iw, ih;


    public SelectTx() {
        setBackground(Color.white);
        original = getImage("painting.gif");
        iw = original.getWidth(this);
        ih = original.getHeight(this);
    }


    public String getCustomControlsConstraint() {
        return "North";
    }


    public Component getCustomControls() {
        return new SelectTxControls(this);
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
            }
        }

        if (transformType == ROTATE) {
            angdeg += 5;
            if (angdeg == 360) { 
                angdeg = 0;
            }
        }
    }


    public void drawDemo(int w, int h, Graphics2D g2) {

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
            s = (s.length() < 5) ? s.substring(0,s.length()) : s.substring(0,5);
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
        final DemoPanel dp = new DemoPanel(new SelectTx());
        Frame f = new Frame("Java2D Demo - SelectTx");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
            public void windowDeiconified(WindowEvent e) { 
                dp.surface.start(); 
            }
            public void windowIconified(WindowEvent e) { 
                dp.surface.stop(); 
            }
        });
        f.add("Center", dp);
        f.pack();
        f.setSize(new Dimension(400,300));
        f.show();
        dp.surface.start();
    }


    static class SelectTxControls extends JPanel implements ActionListener {

        SelectTx st;
        JToolBar toolbar;

        public SelectTxControls(SelectTx st) {
            this.st = st;
            setBackground(Color.gray);
            add(toolbar = new JToolBar());
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
                st.transformType = st.SCALE;
                st.direction = st.RIGHT;
                st.sx = st.sy = 1;
            } else if (b.getText().equals("Shear")) {
                st.transformType = st.SHEAR;
                st.direction = st.RIGHT;
                st.sx = st.sy = 0;
            } else if (b.getText().equals("Rotate")) {
                st.transformType = st.ROTATE;
                st.angdeg = 0;
            }
        }

        public Dimension getPreferredSize() {
            return new Dimension(200,36);
        }
    }
}
