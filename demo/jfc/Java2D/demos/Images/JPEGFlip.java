/*
 * @(#)JPEGFlip.java	1.15 98/09/22
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

package demos.Images;

import java.awt.*;
import java.awt.event.*;
import com.sun.image.codec.jpeg.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.geom.GeneralPath;
import java.io.*;
import DemoSurface;
import DemoPanel;


/**
 * Render a filled star & duke into a BufferedImage, save the BufferedImage
 * as a JPEG, display the BufferedImage, using the decoded JPEG BufferedImage
 * DataBuffer flip the elements, display the JPEG flipped BufferedImage.
 */
public class JPEGFlip extends DemoSurface {

    private static Image img;

    public JPEGFlip() {
        setBackground(Color.white);
        img = getImage("duke.gif");
    }


    public void drawDemo(int w, int h, Graphics2D g2) {

        int hh = h/2;

        BufferedImage bi = (BufferedImage) createImage(w, hh);
        Graphics2D big = bi.createGraphics();

        // .. use rendering hints from J2DCanvas ..
        big.setRenderingHints(g2.getRenderingHints());

        big.setBackground(Color.white);
        big.clearRect(0, 0, w, hh);

        big.setColor(Color.green.darker());
        GeneralPath p = new GeneralPath(GeneralPath.WIND_NON_ZERO);
        p.moveTo(- w / 2.0f, - hh / 8.0f);
        p.lineTo(+ w / 2.0f, - hh / 8.0f);
        p.lineTo(- w / 4.0f, + hh / 2.0f);
        p.lineTo(+     0.0f, - hh / 2.0f);
        p.lineTo(+ w / 4.0f, + hh / 2.0f);
        p.closePath();
        big.translate(w/2, hh/2);
        big.fill(p);

        int iw = img.getWidth(this);
        int ih = img.getHeight(this);
        if (hh < ih * 1.5)
            ih = (int) (ih * ((hh / (ih*1.5))));
        big.drawImage(img, -img.getWidth(this)/2, -ih/2, iw, ih, this);

        g2.drawImage(bi, 0, 0, this);
        g2.setFont(new Font("Dialog", Font.PLAIN, 10));
        g2.setColor(Color.black);
        g2.drawString("BufferedImage", 4, 12);


        BufferedImage bi1 = null;

        try {
            // To write the jpeg to a file uncomment the File* lines and
            // comment out the ByteArray*Stream lines.
            //File file = new File("images", "test.jpg");
            //FileOutputStream out = new FileOutputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
            param.setQuality(1.0f, false);
            encoder.setJPEGEncodeParam(param);
            encoder.encode(bi);

            //FileInputStream in = new FileInputStream(file);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(in);
            bi1 = decoder.decodeAsBufferedImage();
        } catch (Exception ex) {
            g2.setColor(Color.red);
            g2.drawString("write permissions on images/test.jpg?", 5, hh*2-5);
        }

        if (bi1 == null) {
            g2.setColor(Color.red);
            g2.drawString("decodeAsBufferedImage=null", 5, hh*2-5);
            return;
        }

        BufferedImage bi2 = new BufferedImage(bi1.getWidth(),bi1.getHeight(),bi1.getType());
        DataBuffer db1 = bi1.getRaster().getDataBuffer();
        DataBuffer db2 = bi2.getRaster().getDataBuffer();

        for (int i = db1.getSize()-1, j = 0; i >= 0; --i, j++) {
            db2.setElem(j, db1.getElem(i));
        }

        g2.drawImage(bi2, 0, hh, this);

        g2.drawString("JPEGImage Flipped", 4, hh*2-4);
        g2.drawLine(0, hh, w, hh);
    }


    public static void main(String s[]) {
        Frame f = new Frame("Java2D Demo - JPEGFlip");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        f.add("Center", new DemoPanel(new JPEGFlip()));
        f.pack();
        f.setSize(new Dimension(400,300));
        f.show();
    }
}
