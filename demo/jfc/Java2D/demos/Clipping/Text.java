/*
 * @(#)Text.java	1.20 98/09/13
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

package demos.Clipping;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import javax.swing.*;
import DemoSurface;
import DemoPanel;
import CustomControls;


/**
 * Clipping an image, lines, text, texture and gradient with text.
 */
public class Text extends DemoSurface implements CustomControls {

    static final int LINES = 0;
    static final int IMAGE = 1;
    static final int TPAINT = 2;
    static final int GPAINT = 3;
    static final int TEXT = 4;
    static Image img;
    static TexturePaint texture;
    static {
        Font f = new Font("Helvetica", Font.BOLD, 10);
        TextLayout tl = new TextLayout("java", f, new FontRenderContext(null, false, false));
        int sw = (int) tl.getBounds().getWidth();
        int sh = (int) tl.getAscent();
        BufferedImage bi = new BufferedImage(sw, sh, BufferedImage.TYPE_INT_RGB);
        Graphics2D big = bi.createGraphics();
        big.setBackground(Color.black);
        big.clearRect(0,0,sw,sh);
        big.setColor(Color.cyan);
        tl.draw(big, 0, (float) tl.getAscent());
        Rectangle r = new Rectangle(0,0,sw,sh);
        texture = new TexturePaint(bi,r);
    }
    private int clipType;


    public Text() {
        setBackground(Color.white);
        img = getImage("clouds.jpg");
    }


    public String getCustomControlsConstraint() {
        return "North";
    }


    public Component getCustomControls() {
        return new TextControls(this);
    }


    public void drawDemo(int w, int h, Graphics2D g2) {

        FontRenderContext frc = g2.getFontRenderContext();
        Font f = new Font("Helvetica",Font.BOLD,32);
        String s = new String("JAVA");
        TextLayout tl = new TextLayout(s, f, frc);
        double sw = tl.getBounds().getWidth();
        double sh = tl.getBounds().getHeight();
        double sx = (w-40)/sw;
        double sy = (h-40)/sh;
        AffineTransform Tx = AffineTransform.getScaleInstance(sx, sy);
        Shape shape = tl.getOutline(Tx);
        sw = shape.getBounds().getWidth();
        sh = shape.getBounds().getHeight();
        Tx = AffineTransform.getTranslateInstance(w/2-sw/2, sh+(h-sh)/2);
        shape = Tx.createTransformedShape(shape);
        Rectangle r = shape.getBounds();

        g2.setColor(Color.gray);
        g2.setStroke(new BasicStroke(3));
        g2.draw(shape);
        g2.setClip(shape);

        if (clipType == LINES) {
            g2.setColor(Color.black);
            g2.fill(r);
            g2.setColor(Color.yellow);
            g2.setStroke(new BasicStroke(1.5f));
            for (int j = r.y; j < r.y + r.height; j=j+3) {
                Line2D line = new Line2D.Float( 0.0f, (float) j,
                                            (float) w, (float) j);
                g2.draw(line);
            }
        } else if (clipType == IMAGE) {
            g2.drawImage(img, r.x, r.y, r.width, r.height, null);
        } else if (clipType == TPAINT) {
            g2.setPaint(texture);
            g2.fill(r);
        } else if (clipType == GPAINT) {
            g2.setPaint(new GradientPaint(0,0,Color.blue,w,h,Color.yellow));
            g2.fill(r);
        } else if (clipType == TEXT) {
            g2.setColor(Color.black);
            g2.fill(shape.getBounds());
            g2.setColor(Color.cyan);
            f = new Font("Helvetica",Font.BOLD,10);
            tl = new TextLayout("java", f, frc);
            sw = tl.getBounds().getWidth();
    
            int x = r.x;
            int y = r.y;
            while ( y < (r.y + r.height+(int) tl.getAscent()) ) {
                tl.draw(g2, x, y);
                if ((x += (int) sw) > (r.x+r.width)) {
                    x = r.x;
                    y += (int) tl.getAscent();
                }
            }
        }
    }


    public static void main(String s[]) {
        Frame f = new Frame("Java2D Demo - Text");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        final DemoPanel dp = new DemoPanel(new Text());
        f.add("Center", dp);
        f.pack();
        f.setSize(new Dimension(400,300));
        f.show();
    }


    static class TextControls extends JPanel implements ActionListener {

        Text tx;
        JToolBar toolbar;

        public TextControls(Text tx) {
            this.tx = tx;
            setBackground(Color.gray);
            add(toolbar = new JToolBar());
            addTool("Lines", true);
            addTool("Image", false);
            addTool("TP", false);
            addTool("GP", false);
            addTool("Text", false);
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
            if (b.getText().equals("Lines")) {
                tx.clipType = tx.LINES;
            } else if (b.getText().equals("Image")) {
                tx.clipType = tx.IMAGE;
            } else if (b.getText().equals("TP")) {
                tx.clipType = tx.TPAINT;
            } else if (b.getText().equals("GP")) {
                tx.clipType = tx.GPAINT;
            } else if (b.getText().equals("Text")) {
                tx.clipType = tx.TEXT;
            }
            tx.repaint();
        }

        public Dimension getPreferredSize() {
            return new Dimension(200,36);
        }
    }
}
