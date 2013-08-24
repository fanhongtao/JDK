/*
 * @(#)Balls.java	1.32 06/08/29
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
 * @(#)Balls.java	1.32 06/08/29
 */

package java2d.demos.Mix;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import java2d.AnimatingControlsSurface;
import java2d.CustomControls;

import static java.awt.Color.*;
import static java.lang.Math.*;


/**
 * Animated color bouncing balls with custom controls.
 */
public class Balls extends AnimatingControlsSurface {

    private static Color colors[] = 
            { RED, ORANGE, YELLOW, GREEN.darker(), BLUE,
                new Color(75, 00, 82), new Color(238,130,238) };
    private long now, deltaT, lasttime;
    private boolean active;
    protected Ball balls[] = new Ball[colors.length];
    protected boolean clearToggle;
    protected JComboBox combo;


    public Balls() {
        setBackground(WHITE);
        for (int i = 0; i < colors.length; i++) {
            balls[i] = new Ball(colors[i], 30);
        }
        balls[0].isSelected = true;
        balls[3].isSelected = true;
        balls[4].isSelected = true;
        balls[6].isSelected = true;
        setControls(new Component[] { new DemoControls(this) });
    }


    public void reset(int w, int h) {
        if (w > 400 && h > 100) {
            combo.setSelectedIndex(5);
        }
    }


    public void step(int w, int h) {
        if (lasttime == 0) {
            lasttime = System.currentTimeMillis();
        }
        now = System.currentTimeMillis();
        deltaT = now - lasttime;
        active = false;
        for (Ball ball : balls) {
            if (ball == null) {
                return;
            }
            ball.step(deltaT, w, h);
            if (ball.Vy > .02 || -ball.Vy > .02 ||
                    ball.y + ball.bsize < h) {
                active = true;
            }
        }
        if (!active) {
            for (Ball ball : balls) {
                ball.Vx =  (float)random() / 4.0f - 0.125f;
                ball.Vy = -(float)random() / 4.0f - 0.2f;
            }
            clearToggle = true;
        }
    }


    public void render(int w, int h, Graphics2D g2) {
        for (Ball b : balls) {
            if (b == null || b.imgs[b.index] == null || !b.isSelected) {
                continue;
            }
            g2.drawImage(b.imgs[b.index], (int) b.x, (int) b.y, this);
        }
        lasttime = now;
    }


    public static void main(String argv[]) {
        createDemoFrame(new Balls());
    }


    static class Ball {
    
        public static final int nImgs = 5;

        public int bsize;
        public float x, y;
        public float Vx = 0.1f;
        public float Vy = 0.05f;
        public BufferedImage imgs[];
        // Pick a random starting image index, but not the last: we're going UP
        // and that would throw us off the end. 
        public int index = (int) (random() * (nImgs-1));
    
        private static final float inelasticity = .96f;
        private static final float Ax = 0.0f;
        private static final float Ay = 0.0002f;
        private static final float Ar = 0.9f;
        private static final int UP   = 0;
        private static final int DOWN = 1;

        private int indexDirection = UP;
        private boolean collision_x, collision_y;
        private float jitter;
        private Color color;
        private boolean isSelected;
    
    
        public Ball(Color color, int bsize) {
            this.color = color;
            makeImages(bsize);
        }
    
    
        public void makeImages(int bsize) {
            this.bsize = bsize*2;
            int R = bsize;
            byte[] data = new byte[R * 2 * R * 2];
            int maxr = 0;
            for (int Y = 2 * R; --Y >= 0;) {
                int x0 = (int) (sqrt(R * R - (Y - R) * (Y - R)) + 0.5);
                int p = Y * (R * 2) + R - x0;
                for (int X = -x0; X < x0; X++) {
                    int x = X + 15;
                    int y = Y - R + 15;
                    int r = (int) (sqrt(x * x + y * y) + 0.5);
                    if (r > maxr) {
                        maxr = r;
                    }
                    data[p++] = r <= 0 ? 1 : (byte) r;
                }
            }
    
            imgs = new BufferedImage[nImgs];
    
            int bg = 255;
            byte   red[] = new byte[256];   red[0] = (byte) bg;
            byte green[] = new byte[256]; green[0] = (byte) bg;
            byte  blue[] = new byte[256];  blue[0] = (byte) bg;
    
            for (int r = 0; r < imgs.length; r++) {
                float b = 0.5f + (float) ((r+1f)/imgs.length/2f);
                for (int i = maxr; i >= 1; --i) {
                    float d = (float) i / maxr;
                      red[i] = (byte)
                              blend(blend(color.getRed(),   255, d), bg, b);
                    green[i] = (byte)
                              blend(blend(color.getGreen(), 255, d), bg, b);
                     blue[i] = (byte)
                              blend(blend(color.getBlue(),  255, d), bg, b);
                }
                IndexColorModel icm = new IndexColorModel(8, maxr + 1,
                            red, green, blue, 0);
                DataBufferByte dbb = new DataBufferByte(data, data.length);
                int bandOffsets[] = {0};
                WritableRaster wr = Raster.createInterleavedRaster(dbb,
                    R*2,R*2,R*2,1, bandOffsets,null);
                imgs[r] = new BufferedImage(icm, wr,icm.isAlphaPremultiplied(),null);
            }
        }
    
    
        private final int blend(int fg, int bg, float fgfactor) {
            return (int) (bg + (fg - bg) * fgfactor);
        }
    
    
        public void step(long deltaT, int w, int h) {
            collision_x = false;
            collision_y = false;
    
            jitter = (float) random() * .01f - .005f;
    
            x += Vx * deltaT + (Ax / 2.0) * deltaT * deltaT;
            y += Vy * deltaT + (Ay / 2.0) * deltaT * deltaT;
            if (x <= 0.0f) {
                x = 0.0f;
                Vx = -Vx * inelasticity + jitter;
                collision_x = true;
            }
            if (x + bsize >= w) {
                x = w - bsize;
                Vx = -Vx * inelasticity + jitter;
                collision_x = true;
            }
            if (y <= 0) {
                y = 0;
                Vy = -Vy * inelasticity + jitter;
                collision_y = true;
            }
            if (y + bsize >= h) {
                y = h - bsize;
                Vx *= inelasticity;
                Vy = -Vy * inelasticity + jitter;
                collision_y = true;
            }
            Vy = Vy + Ay * deltaT;
            Vx = Vx + Ax * deltaT;
    
            if (indexDirection == UP) {
                index++; 
            }
            if (indexDirection == DOWN) {
                --index; 
            }
            if (index+1 == nImgs) {
                indexDirection = DOWN;
            }
            if (index == 0) {
                indexDirection = UP;
            }
        }
    }  // End class Ball



    class DemoControls extends CustomControls implements ActionListener {

        Balls demo;
        JToolBar toolbar;

        public DemoControls(Balls demo) {
            super(demo.name);
            this.demo = demo;
            add(toolbar = new JToolBar());
            toolbar.setFloatable(false);
            addTool("Clear", true);
            addTool("R", demo.balls[0].isSelected);
            addTool("O", demo.balls[1].isSelected);
            addTool("Y", demo.balls[2].isSelected);
            addTool("G", demo.balls[3].isSelected);
            addTool("B", demo.balls[4].isSelected);
            addTool("I", demo.balls[5].isSelected);
            addTool("V", demo.balls[6].isSelected);
            add(combo = new JComboBox());
            combo.addItem("10");
            combo.addItem("20");
            combo.addItem("30");
            combo.addItem("40");
            combo.addItem("50");
            combo.addItem("60");
            combo.addItem("70");
            combo.addItem("80");
            combo.setSelectedIndex(2);
            combo.addActionListener(this);
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
            if (e.getSource() instanceof JComboBox) {
                int size = Integer.parseInt((String) combo.getSelectedItem());
                for (Ball ball : demo.balls) {
                    ball.makeImages(size);
                }
                return;
            }
            JToggleButton b = (JToggleButton) e.getSource();
            if (b.getText().equals("Clear")) {
                demo.clearSurface = b.isSelected();
            } 
            else {
                int index = toolbar.getComponentIndex(b)-1;
                demo.balls[index].isSelected = b.isSelected();
            }
        }

        public Dimension getPreferredSize() {
            return new Dimension(200,40);
        }


        public void run() {
            try { thread.sleep(999); } catch (Exception e) { return; }
            Thread me = Thread.currentThread();
            ((AbstractButton) toolbar.getComponentAtIndex(2)).doClick();
            while (thread == me) {
                try {
                    thread.sleep(222);
                } catch (InterruptedException e) { return; }
                if (demo.clearToggle) {
                    if (demo.clearSurface) {
                        combo.setSelectedIndex((int) (random()*5));
                    }
                    ((AbstractButton) toolbar.getComponentAtIndex(0)).doClick();
                    demo.clearToggle = false;
                }
            }
            thread = null;
        }
    } // End DemoControls
} // End Balls
