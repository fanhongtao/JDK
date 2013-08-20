/*
 * @(#)TransformAnim.java	1.38 04/07/26
 * 
 * Copyright (c) 2004 Sun Microsystems, Inc. All Rights Reserved.
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
 * @(#)TransformAnim.java	1.35 03/10/26
 */

package java2d.demos.Transforms;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.util.Vector;
import java2d.AnimatingControlsSurface;
import java2d.CustomControls;
import javax.swing.plaf.metal.MetalBorders.ButtonBorder;

/**
 * Animation of shapes, text and images rotating, scaling and translating
 * around a canvas.
 */
public class TransformAnim extends AnimatingControlsSurface {

    private static TexturePaint texture;
    static {
        BufferedImage bi = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        Graphics2D gi = bi.createGraphics();
        gi.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        gi.setColor(Color.red);
        gi.fillOval(0,0,9,9);
        texture = new TexturePaint(bi,new Rectangle(0,0,10,10));
    }

    private static BasicStroke bs = new BasicStroke(6); 
    private static Font fonts[] = {
                new Font("Times New Roman", Font.PLAIN, 48),
                new Font("serif", Font.BOLD + Font.ITALIC, 24),
                new Font("Courier", Font.BOLD, 36),
                new Font("Arial", Font.BOLD + Font.ITALIC, 64),
                new Font("Helvetica", Font.PLAIN, 52)};
    private static String strings[] = {
                "Transformation", "Rotate", "Translate",
                "Shear", "Scale" };
    private static String imgs[] = { "duke.gif" };
    private static Paint paints[] = { 
                Color.red, Color.blue, texture, Color.green, Color.magenta, 
                Color.orange, Color.pink, Color.cyan, 
                new Color(0, 255, 0, 128), new Color(0, 0, 255, 128), 
                Color.yellow, Color.lightGray, Color.white};
    private Vector vector = new Vector(13);
    private int numShapes, numStrings, numImages;
    protected boolean doRotate = true;
    protected boolean doTranslate = true;
    protected boolean doScale = true;
    protected boolean doShear;


    public TransformAnim() {
        setBackground(Color.black);
        setStrings(1);
        setImages(2);
        setShapes(10);
        setControls(new Component[] { new DemoControls(this) });
        setConstraints(new String[] { BorderLayout.EAST });
    }


    public void setImages(int num) {
        if (num < numImages) {
            Vector v = new Vector(vector.size());
            for (int i = 0; i < vector.size(); i++) {
                if (((ObjectData) vector.get(i)).object instanceof Image) {
                    v.addElement(vector.get(i));
                }
            }
            vector.removeAll(v);
            v.setSize(num);
            vector.addAll(v);
        } else {
            Dimension d = getSize();
            for (int i = numImages; i < num; i++) {
                Object obj = getImage(imgs[i % imgs.length]);
                ObjectData od = new ObjectData(obj, Color.black);
                od.reset(d.width, d.height);
                vector.addElement(od);
            }
        }
        numImages = num;
    }
        

    public void setStrings(int num) {
        if (num < numStrings) {
            Vector v = new Vector(vector.size());
            for (int i = 0; i < vector.size(); i++) {
                if (((ObjectData) vector.get(i)).object instanceof TextData) {
                    v.addElement(vector.get(i));
                }
            }
            vector.removeAll(v);
            v.setSize(num);
            vector.addAll(v);
        } else {
            Dimension d = getSize();
            for (int i = numStrings; i < num; i++) {
                int j = i % fonts.length;
                int k = i % strings.length;
                Object obj = new TextData(strings[k], fonts[j]); 
                ObjectData od = new ObjectData(obj, paints[i%paints.length]);
                od.reset(d.width, d.height);
                vector.addElement(od);
            }
        }
        numStrings = num;
    }
        

    public void setShapes(int num) {
        if (num < numShapes) {
            Vector v = new Vector(vector.size());
            for (int i = 0; i < vector.size(); i++) {
                if (((ObjectData) vector.get(i)).object instanceof Shape) {
                    v.addElement(vector.get(i));
                }
            }
            vector.removeAll(v);
            v.setSize(num);
            vector.addAll(v);
        } else {
            Dimension d = getSize();
            for (int i = numShapes; i < num; i++) {
                Object obj = null;
                switch (i % 7) {
                    case 0 : obj = new GeneralPath(); break;
                    case 1 : obj = new Rectangle2D.Double(); break;
                    case 2 : obj = new Ellipse2D.Double(); break;
                    case 3 : obj = new Arc2D.Double(); break;
                    case 4 : obj = new RoundRectangle2D.Double(); break;
                    case 5 : obj = new CubicCurve2D.Double(); break;
                    case 6 : obj = new QuadCurve2D.Double(); break;
                }
                ObjectData od = new ObjectData(obj, paints[i%paints.length]);
                od.reset(d.width, d.height);
                vector.addElement(od);
            }
        } 
        numShapes = num;
    }
        

    public void reset(int w, int h) {
        for (int i = 0; i < vector.size(); i++) {
            ((ObjectData) vector.get(i)).reset(w, h);
        }
    }


    public void step(int w, int h) {
        for (int i = 0; i < vector.size(); i++) {
            ((ObjectData) vector.get(i)).step(w, h, this);
        }
    }


    public void render(int w, int h, Graphics2D g2) {
        for (int i = 0; i < vector.size(); i++) {
            ObjectData od = (ObjectData) vector.get(i);
            g2.setTransform(od.at);
            g2.setPaint(od.paint);
            if (od.object instanceof Image) {
                g2.drawImage((Image) od.object, 0, 0, this);
            } else if (od.object instanceof TextData) {
                g2.setFont(((TextData) od.object).font);
                g2.drawString(((TextData) od.object).string, 0, 0);
            } else if (od.object instanceof QuadCurve2D 
                    || od.object instanceof CubicCurve2D) 
            {
                g2.setStroke(bs);
                g2.draw((Shape) od.object);
            } else if (od.object instanceof Shape) {
                g2.fill((Shape) od.object);
            }
        }
    }


    public static void main(String argv[]) {
        createDemoFrame(new TransformAnim());
    }


    static class TextData extends Object {

	public String string;
        public Font font;

        public TextData(String str, Font font) {
            string = str;
            this.font = font;
        }
    }


    static class ObjectData extends Object {
        Object object;
        Paint paint;
        static final int UP = 0;
        static final int DOWN = 1;
        double x, y;
        double ix=5, iy=3;
        int rotate;
        double scale, shear;
        int scaleDirection, shearDirection;
        AffineTransform at = new AffineTransform();


        public ObjectData(Object object, Paint paint) {
            this.object = object;
            this.paint = paint;
            rotate = (int)(Math.random() * 360);
            scale = Math.random() * 1.5;
            scaleDirection = Math.random() > 0.5 ? UP : DOWN;
            shear = Math.random() * 0.5;
            shearDirection = Math.random() > 0.5 ? UP : DOWN;
        }


        public void reset(int w, int h) {
            x = Math.random()*w;
            y = Math.random()*h;
            double ww = 20 + Math.random()*((w == 0 ? 400 : w)/4);
            double hh = 20 + Math.random()*((h == 0 ? 300 : h)/4);
            if (object instanceof Ellipse2D) {
                ((Ellipse2D) object).setFrame(0, 0, ww, hh);
            } else if (object instanceof Rectangle2D) {
                ((Rectangle2D) object).setRect(0, 0, ww, ww);
            } else if (object instanceof RoundRectangle2D) {
                ((RoundRectangle2D) object).setRoundRect(0, 0, hh, hh, 20, 20); 
            } else if (object instanceof Arc2D) {
                ((Arc2D) object).setArc(0, 0, hh, hh, 45, 270, Arc2D.PIE);
            } else if (object instanceof QuadCurve2D) {
                ((QuadCurve2D) object).setCurve(0, 0, w*.2, h*.4, w*.4, 0);
            } else if (object instanceof CubicCurve2D) {
                    ((CubicCurve2D) object).setCurve(0,0,30,-60,60,60,90,0);
            } else if (object instanceof GeneralPath) {
                GeneralPath p = new GeneralPath();
                float size = (float) ww;
                p.moveTo(- size / 2.0f, - size / 8.0f);
                p.lineTo(+ size / 2.0f, - size / 8.0f);
                p.lineTo(- size / 4.0f, + size / 2.0f);
                p.lineTo(+         0.0f, - size / 2.0f);
                p.lineTo(+ size / 4.0f, + size / 2.0f);
                p.closePath();
                object = p;
            }
        }


        public void step(int w, int h, TransformAnim demo) {
            at.setToIdentity();
            if (demo.doRotate) {
                if ((rotate+=5) == 360) {
                    rotate = 0;
                }
                at.rotate(Math.toRadians(rotate), x, y);
            }
            at.translate(x, y);
            if (demo.doTranslate) {
                x += ix;
                y += iy;
                if (x > w) {
                    x = w - 1;
                    ix = Math.random() * -w/32 - 1;
                }
                if (x < 0) {
                    x = 2;
                    ix = Math.random() * w/32 + 1;
                }
                if (y > h ) {
                    y = h - 2;
                    iy = Math.random() * -h/32 - 1;
                }
                if (y < 0) {
                    y = 2;
                    iy = Math.random() * h/32 + 1;
                }
            }
            if (demo.doScale && scaleDirection == UP) {
                if ((scale += 0.05) > 1.5) {
                    scaleDirection = DOWN;
                }
            } else if (demo.doScale && scaleDirection == DOWN) {
                if ((scale -= .05) < 0.5) {
                    scaleDirection = UP;
                }
            }
            if (demo.doScale) {
                at.scale(scale, scale);
            }
            if (demo.doShear && shearDirection == UP) {
                if ((shear += 0.05) > 0.5) {
                    shearDirection = DOWN;
                }
            } else if (demo.doShear && shearDirection == DOWN) {
                if ((shear -= .05) < -0.5) {
                    shearDirection = UP;
                }
            }
            if (demo.doShear) {
                at.shear(shear, shear);
            }
        }
    } // End ObjectData class



    static class DemoControls extends CustomControls implements ActionListener, ChangeListener {

        TransformAnim demo;
        JSlider shapeSlider, stringSlider, imageSlider;
        Font font = new Font("serif", Font.BOLD, 10);
        JToolBar toolbar;
        ButtonBorder buttonBorder = new ButtonBorder();

        public DemoControls(TransformAnim demo) {
            super(demo.name);
            this.demo = demo;
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            add(Box.createVerticalStrut(5));

            JToolBar bar = new JToolBar(JToolBar.VERTICAL);
            bar.setFloatable(false);
            shapeSlider = new JSlider(JSlider.HORIZONTAL,0,20,demo.numShapes);
            shapeSlider.addChangeListener(this);
            TitledBorder tb = new TitledBorder(new EtchedBorder());
            tb.setTitleFont(font);
            tb.setTitle(String.valueOf(demo.numShapes) + " Shapes");
            shapeSlider.setBorder(tb);
            shapeSlider.setOpaque(true);
            shapeSlider.setPreferredSize(new Dimension(80,44));
            bar.add(shapeSlider);
            bar.addSeparator();

            stringSlider = new JSlider(JSlider.HORIZONTAL,0,10,demo.numStrings);
            stringSlider.addChangeListener(this);
            tb = new TitledBorder(new EtchedBorder());
            tb.setTitleFont(font);
            tb.setTitle(String.valueOf(demo.numStrings) + " Strings");
            stringSlider.setBorder(tb);
            stringSlider.setOpaque(true);
            stringSlider.setPreferredSize(new Dimension(80,44));
            bar.add(stringSlider);
            bar.addSeparator();

            imageSlider = new JSlider(JSlider.HORIZONTAL,0,10,demo.numImages);
            imageSlider.addChangeListener(this);
            tb = new TitledBorder(new EtchedBorder());
            tb.setTitleFont(font);
            tb.setTitle(String.valueOf(demo.numImages) + " Images");
            imageSlider.setBorder(tb);
            imageSlider.setOpaque(true);
            imageSlider.setPreferredSize(new Dimension(80,44));
            bar.add(imageSlider);
            bar.addSeparator();
            add(bar);

            toolbar = new JToolBar();
            toolbar.setFloatable(false);
            addButton("T", "translate", demo.doTranslate);
            addButton("R", "rotate", demo.doRotate);
            addButton("SC", "scale", demo.doScale);
            addButton("SH", "shear", demo.doShear);
            add(toolbar);
        }


        public void addButton(String s, String tt, boolean state) {
            JToggleButton b = (JToggleButton) toolbar.add(new JToggleButton(s));
            b.setFont(font);
            b.setSelected(state);
            b.setToolTipText(tt);
            b.setFocusPainted(false);
            b.setBorder(buttonBorder);
            b.addActionListener(this);
        }


        public void actionPerformed(ActionEvent e) {
            JToggleButton b = (JToggleButton) e.getSource();
            if (b.getText().equals("T")) {
                demo.doTranslate = b.isSelected();
            } else if (b.getText().equals("R")) {
                demo.doRotate = b.isSelected();
            } else if (b.getText().equals("SC")) {
                demo.doScale = b.isSelected();
            } else if (b.getText().equals("SH")) {
                demo.doShear = b.isSelected();
            }
            if (demo.animating.thread == null) {
                demo.repaint();
            }
        }


        public void stateChanged(ChangeEvent e) {
            JSlider slider = (JSlider) e.getSource();
            int value = slider.getValue();
            TitledBorder tb = (TitledBorder) slider.getBorder();
            if (slider.equals(shapeSlider)) {
                tb.setTitle(String.valueOf(value) + " Shapes");
                demo.setShapes(value);
            } else if (slider.equals(stringSlider)) {
                tb.setTitle(String.valueOf(value) + " Strings");
                demo.setStrings(value);
            } else if (slider.equals(imageSlider)) {
                tb.setTitle(String.valueOf(value) + " Images");
                demo.setImages(value);
            } 
            if (demo.animating.thread == null) {
                demo.repaint();
            }
            slider.repaint();
        }


        public Dimension getPreferredSize() {
            return new Dimension(80,38);
        }


        public void run() {
            Thread me = Thread.currentThread();
            while (thread == me) {
                for (int i = 1; i < toolbar.getComponentCount(); i++) {
                    try {
                        thread.sleep(4444);
                    } catch (InterruptedException e) { return; }
                    ((AbstractButton) toolbar.getComponentAtIndex(i)).doClick();
                }
            }
            thread = null;
        }
    } // End DemoControls
} // End TransformAnim
