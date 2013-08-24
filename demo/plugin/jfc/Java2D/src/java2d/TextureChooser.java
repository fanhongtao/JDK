/*
 * @(#)TextureChooser.java	1.34 06/08/29
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
 * @(#)TextureChooser.java	1.34 06/08/29
 */

package java2d;

import static java.awt.Color.*;
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

        add(new Surface(  getGeomTexture(), this, 0));
        add(new Surface( getImageTexture(), this, 1));
        add(new Surface(  getTextTexture(), this, 2));
        add(new Surface(getGradientPaint(), this, 3));
    }


    static public TexturePaint getGeomTexture() {
        BufferedImage bi = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        Graphics2D tG2 = bi.createGraphics();
        tG2.setBackground(WHITE);
        tG2.clearRect(0,0,5,5);
        tG2.setColor(new Color(211,211,211,200));
        tG2.fill(new Ellipse2D.Float(0,0,5,5));
        Rectangle r = new Rectangle(0,0,5,5);
        return new TexturePaint(bi,r);
    }

    public TexturePaint getImageTexture() {
        Image img = DemoImages.getImage("java-logo.gif", this);
        int iw = img.getWidth( this);
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
        tG2.setBackground(WHITE);
        tG2.clearRect(0,0,sw,sh);
        tG2.setColor(LIGHT_GRAY);
        tl.draw(tG2, 0, (float) tl.getAscent());
        Rectangle r = new Rectangle(0,0,sw,sh);
        return new TexturePaint(bi,r);
    }


    public GradientPaint getGradientPaint() {
        return new GradientPaint(0,0,WHITE,80,0,GREEN);
    }

    public class Surface extends JPanel implements MouseListener {

        public boolean clickedFrame;
        private int num;
        private TextureChooser tc;
        private boolean enterExitFrame = false;
        private Object t;

        public Surface(Object t, TextureChooser tc, int num) {
            setBackground(WHITE);
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
                g2.setColor(GRAY);
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

            for (Component comp : tc.getComponents()) {
                if (comp instanceof Surface) {
                    Surface surf = (Surface) comp;
                    if (!surf.equals(this) && surf.clickedFrame) {
                        surf.clickedFrame = false;
                        surf.repaint();
                    }
                }
            }
            
            // ABP
            if (Java2Demo.controls.textureCB.isSelected()) {
	            Java2Demo.controls.textureCB.doClick();
	            Java2Demo.controls.textureCB.doClick();
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
        f.setVisible(true);
    }
}
