/*
 * @(#)ImageOps.java	1.35 07/05/30
 * 
 * Copyright (c) 2007 Sun Microsystems, Inc. All Rights Reserved.
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
 * @(#)ImageOps.java	1.35 07/05/30
 */

package java2d.demos.Images;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;
import javax.swing.event.*;
import java2d.ControlsSurface;
import java2d.CustomControls;



/**
 * Images drawn using operators such as ConvolveOp LowPass & Sharpen,
 * LookupOp and RescaleOp.
 */
public class ImageOps extends ControlsSurface implements ChangeListener {

    protected JSlider slider1, slider2;
    private static String imgName[] = { "bld.jpg", "boat.png" };
    private static BufferedImage img[] = new BufferedImage[imgName.length];
    private static String opsName[] = { 
              "Threshold", "RescaleOp" ,"Invert", "Yellow Invert", "3x3 Blur", 
              "3x3 Sharpen", "3x3 Edge", "5x5 Edge"};
    private static BufferedImageOp biop[] = new BufferedImageOp[opsName.length];
    private static int rescaleFactor = 128;
    private static float rescaleOffset = 0;
    private static int low = 100, high = 200;
    private int opsIndex, imgIndex;
 
    static {
        thresholdOp(low, high);
        int i = 1;
        biop[i++] = new RescaleOp(1.0f, 0, null);
        byte invert[] = new byte[256];
        byte ordered[] = new byte[256];
        for (int j = 0; j < 256 ; j++) {
            invert[j] = (byte) (256-j);
            ordered[j] = (byte) j;
        }
        biop[i++] = new LookupOp(new ByteLookupTable(0,invert), null);
        byte[][] yellowInvert = new byte[][] { invert, invert, ordered };
        biop[i++] = new LookupOp(new ByteLookupTable(0,yellowInvert), null);
        int dim[][] = {{3,3}, {3,3}, {3,3}, {5,5}};
        float data[][] = { {0.1f, 0.1f, 0.1f,              // 3x3 blur
                            0.1f, 0.2f, 0.1f,
                            0.1f, 0.1f, 0.1f},
                           {-1.0f, -1.0f, -1.0f,           // 3x3 sharpen
                            -1.0f, 9.0f, -1.0f,
                            -1.0f, -1.0f, -1.0f},
                           { 0.f, -1.f,  0.f,                  // 3x3 edge
                            -1.f,  5.f, -1.f,
                             0.f, -1.f,  0.f},
                           {-1.0f, -1.0f, -1.0f, -1.0f, -1.0f,  // 5x5 edge
                            -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,
                            -1.0f, -1.0f, 24.0f, -1.0f, -1.0f,
                            -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,
                            -1.0f, -1.0f, -1.0f, -1.0f, -1.0f}};
        for (int j = 0; j < data.length; j++, i++) {
            biop[i] = new ConvolveOp(new Kernel(dim[j][0],dim[j][1],data[j]));
        }
    }


    public ImageOps() {
        setDoubleBuffered(true);
        setBackground(Color.white);
        for (int i = 0; i < imgName.length; i++) {
            Image image = getImage(imgName[i]);
            int iw = image.getWidth(this);
            int ih = image.getHeight(this);
            img[i] = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_RGB);
            img[i].createGraphics().drawImage(image,0,0,null);
        }
        slider1 = new JSlider(JSlider.VERTICAL, 0, 255, low);
        slider1.setPreferredSize(new Dimension(15, 100));
        slider1.addChangeListener(this);
        slider2 = new JSlider(JSlider.VERTICAL, 0, 255, high);
        slider2.setPreferredSize(new Dimension(15, 100));
        slider2.addChangeListener(this);
        setControls(new Component[]{new DemoControls(this),slider1,slider2});
        setConstraints(new String[] { 
            BorderLayout.NORTH, BorderLayout.WEST, BorderLayout.EAST });
    }


    public static void thresholdOp(int low, int high) {
        byte threshold[] = new byte[256];
        for (int j = 0; j < 256 ; j++) {
            if (j > high) {
                threshold[j] = (byte) 255;
            } else if (j < low) {
                threshold[j] = (byte) 0;
            } else {
                threshold[j] = (byte) j;
            }
        }
        biop[0] = new LookupOp(new ByteLookupTable(0,threshold), null);
    }


    public void render(int w, int h, Graphics2D g2) {
        int iw = img[imgIndex].getWidth(null);
        int ih = img[imgIndex].getHeight(null);
        AffineTransform oldXform = g2.getTransform();
        g2.scale(((double)w) / iw, ((double)h) / ih);
        g2.drawImage(img[imgIndex], biop[opsIndex], 0, 0);
        g2.setTransform(oldXform);
    }


    public void stateChanged(ChangeEvent e) {
        if (e.getSource().equals(slider1)) {
            if (opsIndex == 0) {
                thresholdOp(slider1.getValue(), high);
            } else {
                rescaleFactor = slider1.getValue();
                biop[1] = new RescaleOp((float)rescaleFactor/128.0f, rescaleOffset, null);
            }
        } else {
            if (opsIndex == 0) {
                thresholdOp(low, slider2.getValue());
            } else {
                rescaleOffset = (float) slider2.getValue();
                biop[1] = new RescaleOp((float)rescaleFactor/128.0f, rescaleOffset, null);
            }

        }
        repaint();
    }


    public static void main(String s[]) {
        createDemoFrame(new ImageOps());
    }


    static class DemoControls extends CustomControls implements ActionListener {

        ImageOps demo;
        JComboBox imgCombo, opsCombo;
        Font font = new Font("serif", Font.PLAIN, 10);

        public DemoControls(ImageOps demo) {
            super(demo.name);
            this.demo = demo;
            add(imgCombo = new JComboBox());
            imgCombo.setFont(font);
            for (String name : ImageOps.imgName) {
                imgCombo.addItem(name);
            }
            imgCombo.addActionListener(this);
            add(opsCombo = new JComboBox());
            opsCombo.setFont(font);
            for (String name : ImageOps.opsName) {
                opsCombo.addItem(name);
            }
            opsCombo.addActionListener(this);
        }


        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(opsCombo)) {
                demo.opsIndex = opsCombo.getSelectedIndex();
                if (demo.opsIndex == 0) {
                    demo.slider1.setValue(demo.low);
                    demo.slider2.setValue(demo.high);
                    demo.slider1.setEnabled(true);
                    demo.slider2.setEnabled(true);
                } else if (demo.opsIndex == 1) {
                    demo.slider1.setValue(demo.rescaleFactor);
                    demo.slider2.setValue((int) demo.rescaleOffset);
                    demo.slider1.setEnabled(true);
                    demo.slider2.setEnabled(true);
                } else {
                    demo.slider1.setEnabled(false);
                    demo.slider2.setEnabled(false);
                }
            } else if (e.getSource().equals(imgCombo)) {
                demo.imgIndex = imgCombo.getSelectedIndex();
            }
            demo.repaint(10);
        }


        public Dimension getPreferredSize() {
            return new Dimension(200,39);
        }


        public void run() {
            try { thread.sleep(1111); } catch (Exception e) { return; }
            Thread me = Thread.currentThread();
            while (thread == me) {
                for (int i = 0; i < ImageOps.imgName.length; i++) {
                    imgCombo.setSelectedIndex(i);
                    for (int j = 0; j < ImageOps.opsName.length; j++) {
                        opsCombo.setSelectedIndex(j);
                        if (j <= 1) {
                            for (int k = 50; k <= 200; k+=10) {
                                demo.slider1.setValue(k);
                                try {
                                    thread.sleep(200);
                                } catch (InterruptedException e) { return; }
                            }
                        }
                        try {
                            thread.sleep(4444);
                        } catch (InterruptedException e) { return; }
                    }
                }
            }
            thread = null;
        }
    } // End DemoControls
} // End ImageOps
