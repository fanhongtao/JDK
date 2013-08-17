/*
 * @(#)BezierScroller.java	1.22 98/09/13
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
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Vector;
import java.net.URL;
import javax.swing.*;
import AnimatingContext;
import DemoSurface;
import DemoPanel;
import CustomControls;


/**
 * Animated Bezier Curve shape with images at the controls points.
 * README.txt file scrolling up.
 * Composited Image fading in and out.
 */
public class BezierScroller extends DemoSurface implements AnimatingContext, CustomControls {

    private static final int NUMPTS = 6;
    private static FontRenderContext frc = 
            new FontRenderContext(null, false, false);
    private static Color greenBlend = new Color(0, 255, 0, 100);
    private static Color blueBlend = new Color(0, 0, 255, 100);
    private static BasicStroke bs = new BasicStroke(3.0f);
    private static Image hotj_img, jump_img[];
    private static Font font = new Font("serif", Font.PLAIN, 12);
    private static final int UP = 0;
    private static final int DOWN = 1;

    private float animpts[] = new float[NUMPTS * 2];
    private float deltas[] = new float[NUMPTS * 2];
    private BufferedReader reader;      
    private int nStrs;          
    private int strH;
    private int yy, ix, iy, imgN;
    private Vector vector;
    private float alpha = 0.2f;
    private int alphaDirection;
    protected boolean doImage, doShape, doText;


    public BezierScroller() {
        setBackground(Color.white);
        doShape = doText = true;
        hotj_img = getImage("HotJava-16.gif");
        jump_img = new Image[10];
        if (containsImage("jumptojavastrip-0.gif")) {
            for (int i = 0; i < 10; i++) {
                jump_img[i] = getImage("jumptojavastrip-" + String.valueOf(i) + ".gif");
            }
        } else {
            Image img = getImage("jumptojavastrip.gif");
            for (int i=0, x=0; i < 10; i++, x+=80) {
                jump_img[i] = getCroppedImage(img, x, 0, 80, 80);
            }
        }
    }


    public String getCustomControlsConstraint() {
        return "North";
    }


    public Component getCustomControls() {
        return new AnimControls(this);
    }


    public void animate(float[] pts, float[] deltas, int index, int limit) {
        float newpt = pts[index] + deltas[index];
        if (newpt <= 0) {
            newpt = -newpt;
            deltas[index] = (float) (Math.random() * 4.0 + 2.0);
        } else if (newpt >= (float) limit) {
            newpt = 2.0f * limit - newpt;
            deltas[index] = - (float) (Math.random() * 4.0 + 2.0);
        }
        pts[index] = newpt;
    }


    public void getFile() {
        String srcFile = "../../README.txt";
        URL url = BezierScroller.class.getResource(srcFile);
        if (url == null) {
            return;
        }
        File file = new File(url.getFile());
        try {
            if ((reader = new BufferedReader(new FileReader(file))) != null) {
                getLine();
            }
        } catch (Exception e) { e.printStackTrace(); reader = null; }
    }


    public String getLine() {
        if (reader == null) {
            return null;
        }
        String str = null;
        try {
            if ((str = reader.readLine()) != null) {
                if (str.length() == 0) {
                    str = " ";
                }
                vector.addElement(new TextLayout(str,font,frc));
            }
        } catch (Exception e) { e.printStackTrace(); reader = null; }
        return str;
    }


    public void reset(int w, int h) {
        for (int i = 0; i < animpts.length; i += 2) {
            animpts[i + 0] = (float) (Math.random() * w);
            animpts[i + 1] = (float) (Math.random() * h);
            deltas[i + 0] = (float) (Math.random() * 6.0 + 4.0);
            deltas[i + 1] = (float) (Math.random() * 6.0 + 4.0);
            if (animpts[i + 0] > w / 2.0f) {
                deltas[i + 0] = -deltas[i + 0];
            }
            if (animpts[i + 1] > h / 2.0f) {
                deltas[i + 1] = -deltas[i + 1];
            }
        }
        strH = (int) font.getLineMetrics("text", frc).getHeight();
        nStrs = h/strH+1;
        vector = new Vector(nStrs);
        ix = (int) (Math.random() * (w - jump_img[0].getWidth(this)));
        iy = (int) (Math.random() * (h - jump_img[0].getHeight(this)));
    }


    public void step(int w, int h) {
        if (doText && vector != null && vector.size() == 0) {
            getFile();
        }
        if (doText && reader != null) {
            String s = getLine();
            if (s == null || vector.size() == nStrs && vector.size() != 0) {
                vector.removeElementAt(0);
            }
            yy = (s == null) ? strH : h - vector.size() * strH;
        }
       
        for (int i = 0; i < animpts.length && doShape; i += 2) {
            animate(animpts, deltas, i + 0, w);
            animate(animpts, deltas, i + 1, h);
        }
        if (doImage && alphaDirection == UP) {
            if ((alpha += 0.025) > .99) {
                alphaDirection = DOWN;
                alpha = 1.0f;
            }
        } else if (doImage && alphaDirection == DOWN) {
            if ((alpha -= .02) < 0.01) {
                alphaDirection = UP;
                alpha = 0;
                ix = (int) (Math.random() * (w - jump_img[0].getWidth(this)));
                iy = (int) (Math.random() * (h - jump_img[0].getHeight(this)));
               }
        }
        if (doImage && ++imgN == jump_img.length) {
            imgN = 0;
        }
    }



    public void drawDemo(int w, int h, Graphics2D g2) {

        if (reader != null && doText) {
            g2.setColor(Color.lightGray);
            float y = yy;
            for (int i = 0; i < vector.size(); i++) {
                y += strH;
                ((TextLayout)vector.get(i)).draw(g2, 1, y);
            }
        }

        if (doShape) {
            float[] ctrlpts = animpts;
            int len = ctrlpts.length;
            float prevx = ctrlpts[len - 2];
            float prevy = ctrlpts[len - 1];
            float curx = ctrlpts[0];
            float cury = ctrlpts[1];
            float midx = (curx + prevx) / 2.0f;
            float midy = (cury + prevy) / 2.0f;
            GeneralPath gp = new GeneralPath(GeneralPath.WIND_NON_ZERO);
            gp.moveTo(midx, midy);
            for (int i = 2; i <= ctrlpts.length; i += 2) {
                float x1 = (midx + curx) / 2.0f;
                float y1 = (midy + cury) / 2.0f;
                prevx = curx;
                prevy = cury;
                if (i < ctrlpts.length) {
                    curx = ctrlpts[i + 0];
                    cury = ctrlpts[i + 1];
                } else {
                    curx = ctrlpts[0];
                    cury = ctrlpts[1];
                }
                midx = (curx + prevx) / 2.0f;
                midy = (cury + prevy) / 2.0f;
                float x2 = (prevx + midx) / 2.0f;
                float y2 = (prevy + midy) / 2.0f;
                gp.curveTo(x1, y1, x2, y2, midx, midy);
            }
            gp.closePath();

            g2.setColor(blueBlend);
            g2.setStroke(bs);
            g2.draw(gp);
            g2.setColor(greenBlend);
            g2.fill(gp);

            PathIterator pi = gp.getPathIterator(null);
            float pts[] = new float[6];
            while ( !pi.isDone() ) {
                if (pi.currentSegment(pts) == pi.SEG_CUBICTO) {
                    g2.drawImage(hotj_img, (int) pts[0], (int) pts[1], this);
                }
                pi.next();
            }
        }

        if (doImage) {
            AlphaComposite ac = AlphaComposite.getInstance(
                                   AlphaComposite.SRC_OVER, alpha);
            g2.setComposite(ac);
            g2.drawImage(jump_img[imgN%jump_img.length], ix, iy, this);
        }
    }


    public static void main(String argv[]) {
        final DemoPanel dp = new DemoPanel(new BezierScroller());
        Frame f = new Frame("Java2D Demo - BezierScroller");
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


    static class AnimControls extends JPanel implements ActionListener {

        BezierScroller anim;
        JToolBar toolbar;
        JComboBox combo;

        public AnimControls(BezierScroller anim) {
            this.anim = anim;
            setBackground(Color.gray);
            add(toolbar = new JToolBar());
            addTool("Image", false);
            addTool("Shape", true);
            addTool("Text", true);
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
            if (b.getText().equals("Image")) {
                anim.doImage = b.isSelected();
            } else if (b.getText().equals("Shape")) {
                anim.doShape = b.isSelected();
            } else {
                anim.doText = b.isSelected();
            }
        }

        public Dimension getPreferredSize() {
            return new Dimension(200,37);
        }
    }
}
