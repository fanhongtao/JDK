/*
 * @(#)Texture.java	1.22 98/09/13
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

package demos.Paint;


import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import DemoSurface;
import DemoPanel;


/**
 * TexturePaint of image, text and shapes.
 */
public class Texture extends DemoSurface {

    private static FontRenderContext frc = 
            new FontRenderContext(null, false, false);
    private static TexturePaint tp_img, tp_geom1, tp_geom2, tp_txt;
    static {
        BufferedImage bi = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        Graphics2D gi = bi.createGraphics();
        gi.setBackground(Color.white);
        gi.clearRect(0,0,5,5);
        gi.setColor(Color.green);
        gi.fill(new Ellipse2D.Float(0,0,5,5));
        tp_geom1 = new TexturePaint(bi,new Rectangle(0,0,5,5));

        bi = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        gi = bi.createGraphics();
        gi.setColor(Color.black);
        gi.fillRect(0,0,5,5);
        gi.setColor(Color.gray);
        gi.fillRect(1,1,4,4);
        tp_geom2 = new TexturePaint(bi,new Rectangle(0,0,5,5));

        Font f = new Font("serif", Font.ITALIC, 10);
        TextLayout tl = new TextLayout("Java2D", f,frc);
        int sw = (int) tl.getBounds().getWidth();
        int sh = (int) (tl.getAscent()+tl.getDescent());
        bi = new BufferedImage(sw, sh, BufferedImage.TYPE_INT_RGB);
        gi = bi.createGraphics();
        gi.setBackground(Color.white);
        gi.clearRect(0,0,sw,sh);
        gi.setColor(Color.gray);
        tl.draw(gi, 0, sh-tl.getDescent());
        tp_txt = new TexturePaint(bi,new Rectangle(0,0,sw,sh));
    }


    public Texture() {
        setBackground(Color.white);

        Image img = getImage("HotJava-16.gif");
        int iw = img.getWidth(this);
        int ih = img.getWidth(this);
        BufferedImage bi = 
            new BufferedImage(iw, ih, BufferedImage.TYPE_INT_RGB);
        Graphics2D gi = bi.createGraphics();
        gi.setBackground(Color.white);
        gi.clearRect(0,0,iw,ih);
        gi.drawImage(img, 0, 0, null);
        tp_img = new TexturePaint(bi,new Rectangle(0,0,iw,ih));
    }


    public void drawDemo(int w, int h, Graphics2D g2) {

        Rectangle r = new Rectangle(10,10,w-20,h/2-20);
        g2.setPaint(tp_txt);
        g2.fill(r);
        g2.setPaint(tp_geom1);
        g2.setStroke(new BasicStroke(20));
        g2.draw(r);
        g2.setPaint(tp_geom2);
        g2.setStroke(new BasicStroke(10));
        g2.draw(r);

        Font f = new Font("Times New Roman", Font.BOLD, w/5);
        TextLayout tl = new TextLayout("Texture", f, frc);
        int sw = (int) tl.getBounds().getWidth();
        int sh = (int) tl.getBounds().getHeight();
        Shape sha = tl.getOutline(AffineTransform.getTranslateInstance(w/2-sw/2, h*.25+sh/2));
        g2.setColor(Color.black);
        g2.setStroke(new BasicStroke(3));
        g2.draw(sha);
        g2.setPaint(tp_geom2);
        g2.fill(sha);

        r.setLocation(10,h/2+10);
        g2.setPaint(tp_img);
        g2.fill(r);
        g2.setPaint(tp_geom2);
        g2.setStroke(new BasicStroke(20));
        g2.draw(r);
        g2.setPaint(Color.green);
        g2.setStroke(new BasicStroke(4));
        g2.draw(r);

        f = new Font("serif", Font.BOLD, w/4);
        tl = new TextLayout("Paint", f, frc);
        sw = (int) tl.getBounds().getWidth();
        sh = (int) tl.getBounds().getHeight();
        sha = tl.getOutline(AffineTransform.getTranslateInstance(w/2-sw/2, h*.75+sh/2));
        g2.setColor(Color.black);
        g2.setStroke(new BasicStroke(6));
        g2.draw(sha);
        g2.setPaint(tp_geom1);
        g2.fill(sha);
    }


    public static void main(String s[]) {
        Frame f = new Frame("Java2D Demo - Texture");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        f.add("Center", new DemoPanel(new Texture()));
        f.pack();
        f.setSize(new Dimension(400,300));
        f.show();
    }
}
