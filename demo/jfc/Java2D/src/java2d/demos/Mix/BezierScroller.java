/*
 * @(#)BezierScroller.java	1.43 06/08/29
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
 * @(#)BezierScroller.java	1.43 06/08/29
 */

package java2d.demos.Mix;

import static java.awt.Color.*;
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
import javax.swing.*;
import java2d.AnimatingControlsSurface;
import java2d.CustomControls;

import static java.lang.Math.random;


/**
 * Animated Bezier Curve shape with images at the control points.
 * README.txt file scrolling up. Composited Image fading in and out.
 */
public class BezierScroller extends AnimatingControlsSurface {

    private static String appletStrs[] = 
        {  " ", "Java2Demo",  
           "BezierScroller - Animated Bezier Curve shape with images", 
           "For README.txt file scrolling run in application mode", " " };
    private static final int NUMPTS = 6;
    private static Color greenBlend = new Color(0, 255, 0, 100);
    private static Color  blueBlend = new Color(0, 0, 255, 100);
    private static Font font = new Font("serif", Font.PLAIN, 12);
    private static BasicStroke bs = new BasicStroke(3.0f);
    private static Image hotj_img;
    private static BufferedImage img;
    private static final int UP = 0;
    private static final int DOWN = 1;

    private float animpts[] = new float[NUMPTS * 2];
    private float  deltas[] = new float[NUMPTS * 2];
    private BufferedReader reader;      
    private int nStrs;          
    private int strH;
    private int yy, ix, iy, imgX;
    private Vector vector, appletVector;
    private float alpha = 0.2f;
    private int alphaDirection;
    protected boolean doImage, doShape, doText;
    protected boolean buttonToggle;


    public BezierScroller() {
        setBackground(WHITE);
        doShape = doText = true;
        hotj_img = getImage("java-logo.gif");
        Image image = getImage("jumptojavastrip.png");
        int iw = image.getWidth(this);
        int ih = image.getHeight(this);
        img = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_RGB);
        img.createGraphics().drawImage(image, 0, 0, this);
        setControls(new Component[] { new DemoControls(this) });
    }


    public void animate(float[] pts, float[] deltas, int index, int limit) {
        float newpt = pts[index] + deltas[index];
        if (newpt <= 0) {
            newpt = -newpt;
            deltas[index] = (float) (random() * 4.0 + 2.0);
        } else if (newpt >= (float) limit) {
            newpt = 2.0f * limit - newpt;
            deltas[index] = - (float) (random() * 4.0 + 2.0);
        }
        pts[index] = newpt;
    }


    public void getFile() {
        try {
            String fName = "README.txt";
            if ((reader = new BufferedReader(new FileReader(fName))) != null) {
                getLine();
            }
        } catch (Exception e) { reader = null; }
        if (reader == null) {
            appletVector = new Vector(100);
            for (int i = 0; i < 100; i++) {
                appletVector.addElement(appletStrs[i%appletStrs.length]);
            }
            getLine();
        }
        buttonToggle = true;
    }


    public String getLine() {
        String str = null;
        if (reader != null) {
            try {
                if ((str = reader.readLine()) != null) {
                    if (str.length() == 0) {
                        str = " ";
                    }
                    vector.addElement(str);
                }
            } catch (Exception e) { e.printStackTrace(); reader = null; }
        } else {
            if (appletVector.size() != 0) {
                vector.addElement(str = (String) appletVector.remove(0));
            }
        }
        return str;
    }


    public void reset(int w, int h) {
        for (int i = 0; i < animpts.length; i += 2) {
            animpts[i + 0] = (float) (random() * w);
            animpts[i + 1] = (float) (random() * h);
             deltas[i + 0] = (float) (random() * 6.0 + 4.0);
             deltas[i + 1] = (float) (random() * 6.0 + 4.0);
            if (animpts[i + 0] > w / 2.0f) {
                deltas[i + 0] = -deltas[i + 0];
            }
            if (animpts[i + 1] > h / 2.0f) {
                deltas[i + 1] = -deltas[i + 1];
            }
        }
        FontMetrics fm = getFontMetrics(font);
        strH = fm.getAscent()+fm.getDescent();
        nStrs = h/strH+2;
        vector = new Vector(nStrs);
        ix = (int) (random() * (w - 80));
        iy = (int) (random() * (h - 80));
    }


    public void step(int w, int h) {
        if (doText && vector.size() == 0) {
            getFile();
        }
        if (doText) {
            String s = getLine();
            if (s == null || vector.size() == nStrs && vector.size() != 0) {
                vector.removeElementAt(0);
            }
            yy = (s == null) ? 0 : h - vector.size() * strH;
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
                ix = (int) (random() * (w - 80));
                iy = (int) (random() * (h - 80));
            }
        }
        if (doImage) {
            if ((imgX += 80) == 800) {
                imgX = 0;
            }
        }
    }



    public void render(int w, int h, Graphics2D g2) {

        if (doText) {
            g2.setColor(LIGHT_GRAY);
            g2.setFont(font);
            float y = yy;
            for (int i = 0; i < vector.size(); i++) {
                g2.drawString((String)vector.get(i), 1, y += strH);
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
            g2.drawImage(img.getSubimage(imgX,0,80,80), ix, iy, this);
        }
    }


    public static void main(String argv[]) {
        createDemoFrame(new BezierScroller());
    }


    static class DemoControls extends CustomControls implements ActionListener {

        BezierScroller demo;
        JToolBar toolbar;
        JComboBox combo;

        public DemoControls(BezierScroller demo) {
            super(demo.name);
            this.demo = demo;
            add(toolbar = new JToolBar());
            toolbar.setFloatable(false);
            addTool("Image", false);
            addTool("Shape", true);
            addTool("Text", true);
        }


        public void addTool(String str, boolean state) {
            JToggleButton b = (JToggleButton) toolbar.add(new JToggleButton(str));
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
            if (b.getText().equals("Image")) {
                demo.doImage = b.isSelected();
            } else if (b.getText().equals("Shape")) {
                demo.doShape = b.isSelected();
            } else {
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
            int i = 0;
            while (thread == me) {
                try {
                    thread.sleep(250);
                } catch (InterruptedException e) { return; }
                if (demo.buttonToggle) {
                    ((AbstractButton) toolbar.getComponentAtIndex(i++%2)).doClick();
                    demo.buttonToggle = false;
                }
            }
            thread = null;
        }
    } // End DemoControls
} // End BezierScroller
