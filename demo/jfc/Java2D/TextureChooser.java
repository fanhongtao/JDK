/*
 * @(#)TextureChooser.java	1.18 98/09/13
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


import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.net.URL;


/**
 * Four types of Paint displayed: Geometry, Text & Image Textures and
 * a Gradient Paint.  Paints can be selected with the Mouse.
 */
public class TextureChooser extends JPanel {

    static public Object texture = getGeomTexture();
    public int num;

    public TextureChooser(int num) {
        this.num = num;
        setLayout(new GridLayout(0,2,5,5));
        setBorder(new TitledBorder(new EtchedBorder(), "Texture Chooser"));

        add(new ChooserComponent(getGeomTexture(), this, 0));
        add(new ChooserComponent(getImageTexture(), this, 1));
        add(new ChooserComponent(getTextTexture(), this, 2));
        add(new ChooserComponent(getGradientPaint(), this, 3));
    }


    static public TexturePaint getGeomTexture() {
        BufferedImage bi = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        Graphics2D tG2 = bi.createGraphics();
        tG2.setBackground(Color.white);
        tG2.clearRect(0,0,5,5);
        tG2.setColor(new Color(211,211,211,200));
        tG2.fill(new Ellipse2D.Float(0,0,5,5));
        Rectangle r = new Rectangle(0,0,5,5);
        return new TexturePaint(bi,r);
    }

    public TexturePaint getImageTexture() {
        URL url = TextureChooser.class.getResource("images/HotJava-16.gif");
        Image img = getToolkit().getImage(url);
        try {
            MediaTracker tracker = new MediaTracker(this);
            tracker.addImage(img, 0);
            tracker.waitForID(0);
        } catch (Exception e) {}
        int iw = img.getWidth(this);
        int ih = img.getHeight(this);
        BufferedImage bi = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_RGB);
        Graphics2D tG2 = bi.createGraphics();
        tG2.drawImage(img, 0, 0, this);
        Rectangle r = new Rectangle(0,0,iw,ih);
        return new TexturePaint(bi,r);
    }


    public TexturePaint getTextTexture() {
        Font f = new Font("Times New Roman", Font.BOLD, 10);
        TextLayout tl = new TextLayout("Java2D", f, new FontRenderContext(null, false, false));
        int sw = (int) tl.getBounds().getWidth();
        int sh = (int) (tl.getAscent()+tl.getDescent());
        BufferedImage bi = new BufferedImage(sw, sh, BufferedImage.TYPE_INT_RGB);
        Graphics2D tG2 = bi.createGraphics();
        tG2.setBackground(Color.white);
        tG2.clearRect(0,0,sw,sh);
        tG2.setColor(Color.lightGray);
        tl.draw(tG2, 0, (float) tl.getAscent());
        Rectangle r = new Rectangle(0,0,sw,sh);
        return new TexturePaint(bi,r);
    }


    public GradientPaint getGradientPaint() {
        return new GradientPaint(0,0,Color.white,80,0,Color.green);
    }

    public class ChooserComponent extends JPanel implements MouseListener {

        public boolean clickedFrame;
        private int num;
        private TextureChooser tc;
        private boolean enterExitFrame = false;
        private Object t;

        public ChooserComponent(Object t, TextureChooser tc, int num) {
            setBackground(Color.white);
            this.t = t;
            this.tc = tc;
            this.clickedFrame = (num == tc.num);
            this.num = num;
            if (num == tc.num)
                tc.texture = t;
            addMouseListener(this);
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            int w = getSize().width;
            int h = getSize().height;
            if (t instanceof TexturePaint)
                g2.setPaint((TexturePaint) t);
            else {
                g2.setPaint((GradientPaint) t);
            }
            g2.fill(new Rectangle(0,0,w,h));
            if (clickedFrame || enterExitFrame) {
                g2.setColor(Color.gray);
                BasicStroke bs = new BasicStroke(3, BasicStroke.CAP_BUTT,
                                BasicStroke.JOIN_MITER);
                g2.setStroke(bs);
                g2.drawRect(0,0,w-1,h-1);
                tc.num = num;
            }
        }

        public void mouseClicked(MouseEvent e) {
            tc.texture = t;
            clickedFrame = true;

            Component cmps[] = tc.getComponents();
            for (int i = 0; i < cmps.length; i++) {
                if (cmps[i] instanceof ChooserComponent) {
                    ChooserComponent cc = (ChooserComponent) cmps[i];
                    if (!cc.equals(this) && cc.clickedFrame) {
                        cc.clickedFrame = false;
                        cc.repaint();
                    }
                }
            }
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
            enterExitFrame = true;
            repaint();
        }

        public void mouseExited(MouseEvent e) {
            enterExitFrame = false;
            repaint();
        }

        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

        public Dimension getPreferredSize() {
            return new Dimension(30,30);
        }

    }

    public static void main(String s[]) {
        Frame f = new Frame("Java2D Demo - TextureChooser");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        f.add("Center", new TextureChooser(0));
        f.pack();
        f.setSize(new Dimension(400,400));
        f.show();
    }
}
