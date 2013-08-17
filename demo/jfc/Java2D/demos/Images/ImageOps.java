/*
 * @(#)ImageOps.java	1.20 98/09/13
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
import java.awt.image.*;
import java.awt.geom.AffineTransform;
import java.awt.font.TextLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;
import DemoSurface;
import DemoPanel;


/**
 * Images drawn using operators such as ConvolveOp LowPass & Sharpen,
 * LookupOp and RescaleOp.
 */
public class ImageOps extends DemoSurface {

    private static String imgNames[] = { "bld.jpg", "boat.gif" };
    private static BufferedImage bi[] = new BufferedImage[imgNames.length];
    private static float[][] data = 
                          {{0.1f, 0.1f, 0.1f,    // low-pass filter
                            0.1f, 0.2f, 0.1f,
                            0.1f, 0.1f, 0.1f},
                          { 0.f, -1.f,  0.f,    // high-pass filter
                           -1.f,  5.f, -1.f,
                            0.f, -1.f,  0.f}};
    private static String opStr[] = { "Convolve LowPass", "Convolve HighPass",
                             "LookupOp", "RescaleOp" };


    public ImageOps() {
        setBackground(Color.white);
        for (int i = 0; i < bi.length; i++) {
            Image img = getImage(imgNames[i]);
            int iw = img.getWidth(this);
            int ih = img.getHeight(this);
            bi[i] = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_RGB);
            Graphics2D big = bi[i].createGraphics();
            big.drawImage(img,0,0,this);
        }
    }


    public void drawDemo(int w, int h, Graphics2D g2) {

        g2.setColor(Color.black);
        for (int i = 0; i < 4; i++) {
            BufferedImage img = bi[i<2?0:1];
            int iw = img.getWidth(this);
            int ih = img.getHeight(this);
            int x = 0, y = 0;

            AffineTransform at = new AffineTransform();
            at.scale((w-14)/2.0/iw, (h-34)/2.0/ih);

            BufferedImageOp biop = null;
            BufferedImage bimg = new BufferedImage(iw,ih,BufferedImage.TYPE_INT_RGB);

            switch (i) {
                case 0 :
                case 1 : x = i==0?5:w/2+3; y = 15;
                         Kernel kernel = new Kernel(3,3,data[i]);
                         ConvolveOp cop = new ConvolveOp(kernel,
                                                       ConvolveOp.EDGE_NO_OP,
                                                       null);
                         cop.filter(img,bimg);
                         biop = new AffineTransformOp(at,
                                     AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                         break;
                case 2 : x = 5; y = h/2+15;
                         byte chlut[] = new byte[256];
                         for (int j=0;j<200 ;j++ ) {
                             chlut[j]=(byte)(256-j);
                         }
                         ByteLookupTable blut=new ByteLookupTable(0,chlut);
                         LookupOp lop = new LookupOp(blut, null);
                         lop.filter(img,bimg);
                         biop = new AffineTransformOp(at,
                                         AffineTransformOp.TYPE_BILINEAR);
                         break;
                case 3 : x = w/2+3; y = h/2+15;
                         RescaleOp rop = new RescaleOp(1.1f,20.0f, null);
                         rop.filter(img,bimg);
                         biop = new AffineTransformOp(at,
                                         AffineTransformOp.TYPE_BILINEAR);
            }
            g2.drawImage(bimg,biop,x,y);
            g2.drawString(opStr[i], x, y-4);
        }
    }


    public static void main(String s[]) {
        Frame f = new Frame("Java2D Demo - ImageOps");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        f.add("Center", new DemoPanel(new ImageOps()));
        f.pack();
        f.setSize(new Dimension(400,300));
        f.show();
    }
}
