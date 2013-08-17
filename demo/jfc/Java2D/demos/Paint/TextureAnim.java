/*
 * @(#)TextureAnim.java	1.3  98/09/13
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package demos.Paint;
    
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.*;
import AnimatingContext;
import DemoSurface;
import DemoPanel;
import CustomControls;


/**
 * TexturePaint animation with controls for transformations.
 */
public class TextureAnim extends DemoSurface implements AnimatingContext, CustomControls {

    public static final Color colorblend = new Color(0f, 0f, 1f, .5f);
    private BufferedImage bimg;
    private Rectangle tilerect;
    private TexturePaint texture;
    private boolean newtexture;
    private boolean bouncesize = false;
    private boolean bouncerect = true;
    private boolean rotate = false;
    private boolean shearx = false;
    private boolean sheary = false;
    private boolean showanchor = true;
    private boolean quality = false;
    private int tilesize;
    private AnimVal w, h, x, y, rot, shx, shy;


    public TextureAnim() {
        makeImage(32);
        tilesize = bimg.getWidth();
        w = new AnimVal(0, 200, 3, 10, tilesize);
        h = new AnimVal(0, 200, 3, 10, tilesize);
        x = new AnimVal(0, 200, 3, 10, 0);
        y = new AnimVal(0, 200, 3, 10, 0);
        rot = new AnimVal(-360, 360, 5, 15, 0);
        shx = new AnimVal(-50, 50, 3, 10, 0);
        shy = new AnimVal(-50, 50, 3, 10, 0);
        tilerect = new Rectangle(x.getInt(), y.getInt(),
                                 w.getInt(), h.getInt());
        texture = new TexturePaint(bimg, tilerect);
    }


    public String getCustomControlsConstraint() {
        return "North";
    }


    public Component getCustomControls() {
        return new AnimControls(this);
    }


    public void makeImage(int size) {
        bimg = new BufferedImage(size, size, bimg.TYPE_INT_BGR);
        Graphics2D g2d = bimg.createGraphics();
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, size, size);
        for (int j = 0; j < size; j++) {
            float red = j / (float) size;
            for (int i = 0; i < size; i++) {
                float green = i / (float) size;
                g2d.setColor(new Color(1.0f - red, 1.0f - green, 0.0f, 1.0f));
                g2d.drawLine(i, j, i, j);
            }
        }
        newtexture = true;
    }


    public void reset(int width, int height) {
        x.newlimits(-width/4, width/4 - w.getInt());
        y.newlimits(-height/4, height/4 - h.getInt());
    }


    public void step(int width, int height) {
        if (tilesize != bimg.getWidth()) {
            tilesize = bimg.getWidth();
        }
        if (bouncesize) {
            w.anim();
            h.anim();
            x.newlimits(-width/4, width/4 - w.getInt());
            y.newlimits(-height/4, height/4 - h.getInt());
        } else {
            if (w.getInt() != tilesize) {
                w.set(tilesize);
                x.newlimits(-width/4, width/4 - w.getInt());
            }
            if (h.getInt() != tilesize) {
                h.set(tilesize);
                y.newlimits(-height/4, height/4 - h.getInt());
            }
        }
        if (bouncerect) {
            x.anim();
            y.anim();
        }
        if (newtexture ||
            x.getInt() != tilerect.x || y.getInt() != tilerect.y ||
            w.getInt() != tilerect.width || h.getInt() != tilerect.height)
        {
            newtexture = false;
            int X = x.getInt();
            int Y = y.getInt();
            int W = w.getInt();
            int H = h.getInt();
            tilerect = new Rectangle(X, Y, W, H);
            texture = new TexturePaint(bimg, tilerect);
        }
    }



    public void drawDemo(int width, int height, Graphics2D g2) {

        g2.translate(width/2, height/2);
        if (rotate) {
            rot.anim();
            g2.rotate(Math.toRadians(rot.getFlt()));
        } else {
            rot.set(0);
        }
        if (shearx) {
            shx.anim();
            g2.shear(shx.getFlt()/100, 0.0f);
        } else {
            shx.set(0);
        }
        if (sheary) {
            shy.anim();
            g2.shear(0.0f, shy.getFlt()/100);
        } else {
            shy.set(0);
        }
        g2.setPaint(texture);
        g2.fillRect(-1000, -1000, 2000, 2000);
        if (showanchor) {
            g2.setColor(Color.black);
            g2.setColor(colorblend);
            g2.fill(tilerect);
        }
    }


    public static void main(String argv[]) {
        final DemoPanel dp = new DemoPanel(new TextureAnim());
        JFrame f = new JFrame("Java2D Demo - TexureAnim");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
            public void windowDeiconified(WindowEvent e) { 
                dp.surface.start(); 
            }
            public void windowIconified(WindowEvent e) { 
                dp.surface.stop(); 
            }
        });
        f.getContentPane().add("Center", dp);
        f.pack();
        f.setSize(new Dimension(400,300));
        f.show();
        dp.surface.start();
    }


    static class AnimControls extends JPanel implements ActionListener {

        TextureAnim textureanim;
        JToolBar toolbar;
        JComboBox combo;

        public AnimControls(TextureAnim ta) {
            textureanim = ta;
            setBackground(Color.gray);
            add(toolbar = new JToolBar());
            addTool("BO", "bounce", true);
            addTool("RS", "resize", false);
            addTool("SA", "show anchor", true);
            addTool("RO", "rotate", false);
            addTool("SX", "shear x", false);
            addTool("SY", "shear y", false);
            add(combo = new JComboBox());
            combo.addActionListener(this);
            combo.addItem("8");
            combo.addItem("16");
            combo.addItem("32");
            combo.addItem("64");
            combo.setSelectedIndex(2);
        }


        public void addTool(String str, String toolTip, boolean state) {
            JButton b = (JButton) toolbar.add(new JButton(str));
            b.setBackground(state ? Color.green : Color.lightGray);
            b.setSelected(state);
            b.setToolTipText(toolTip);
            b.addActionListener(this);
        }


        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof JComboBox) {
                int size = Integer.parseInt((String) combo.getSelectedItem());
                textureanim.makeImage(size);
                return;
            }
            JButton b = (JButton) e.getSource();
            b.setSelected(!b.isSelected());
            b.setBackground(b.isSelected() ? Color.green : Color.lightGray);
            if (b.getText().equals("BO")) {
                textureanim.bouncerect = b.isSelected();
            } else if (b.getText().equals("RS")) {
                textureanim.bouncesize = b.isSelected();
            } else if (b.getText().equals("SA")) {
                textureanim.showanchor = b.isSelected();
            } else if (b.getText().equals("RO")) {
                textureanim.rotate = b.isSelected();
            } else if (b.getText().equals("SX")) {
                textureanim.shearx = b.isSelected();
            } else if (b.getText().equals("SY")) {
                textureanim.sheary = b.isSelected();
            }
        }

        public Dimension getPreferredSize() {
            return new Dimension(200,37);
        }
    }


    static class AnimVal {
        float curval;
        float lowval;
        float highval;
        float currate;
        float lowrate;
        float highrate;

        public AnimVal(int lowval, int highval,
                       int lowrate, int highrate) {
            this.lowval = lowval;
            this.highval = highval;
            this.lowrate = lowrate;
            this.highrate = highrate;
            this.curval = randval(lowval, highval);
            this.currate = randval(lowrate, highrate);
        }

        public AnimVal(int lowval, int highval,
                       int lowrate, int highrate,
                       int pos) {
            this(lowval, highval, lowrate, highrate);
            set(pos);
        }

        public float randval(float low, float high) {
            return (float) (low + Math.random() * (high - low));
        }

        public float getFlt() {
            return curval;
        }

        public int getInt() {
            return (int) curval;
        }

        public void anim() {
            curval += currate;
            clip();
        }

        public void set(float val) {
            curval = val;
            clip();
        }

        public void clip() {
            if (curval > highval) {
                curval = highval - (curval - highval);
                if (curval < lowval) {
                    curval = highval;
                }
                currate = - randval(lowrate, highrate);
            } else if (curval < lowval) {
                curval = lowval + (lowval - curval);
                if (curval > highval) {
                    curval = lowval;
                }
                currate = randval(lowrate, highrate);
            }
        }

        public void newlimits(int lowval, int highval) {
            this.lowval = lowval;
            this.highval = highval;
            clip();
        }
    }
}
