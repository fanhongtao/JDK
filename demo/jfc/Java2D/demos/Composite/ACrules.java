/*
 * @(#)ACrules.java	1.8 98/09/22
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

package demos.Composite;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import AnimatingContext;
import DemoSurface;
import DemoPanel;


/**
 * All the AlphaCompositing Rules demonstrated.
 */
public class ACrules extends DemoSurface implements AnimatingContext {

    private static String compNames[] = {
        "(Source)",
        "Src",
        "SrcOver",
        "SrcIn",
        "SrcOut",
        "(Dest)",
        "Clear",
        "DstOver",
        "DstIn",
        "DstOut"
    };

    private static AlphaComposite compObjs[] = {
        AlphaComposite.Src,
        AlphaComposite.Src,
        AlphaComposite.SrcOver,
        AlphaComposite.SrcIn,
        AlphaComposite.SrcOut,
        AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.0f),
        AlphaComposite.Clear,
        AlphaComposite.DstOver,
        AlphaComposite.DstIn,
        AlphaComposite.DstOut
    };

    private int fadeIndex;
    private static float fadeValues[][] = {
        { 1.0f,-0.1f, 0.0f, 1.0f, 0.0f, 1.0f},
        { 0.0f, 0.1f, 1.0f, 1.0f,-0.1f, 0.0f},
        { 1.0f, 0.0f, 1.0f, 0.0f, 0.1f, 1.0f},
    };
    private static String fadeNames[] = {
        "Src => transparent, Dest opaque",
        "Src => opaque, Dest => transparent",
        "Src opaque, Dest => opaque",
    };
    private static FontRenderContext frc = new FontRenderContext(null, false, false);
    private static Font f = new Font("serif", Font.PLAIN, 10);
    private float srca = fadeValues[fadeIndex][0];
    private float dsta = fadeValues[fadeIndex][3];
    private String fadeLabel = fadeNames[0];
    private BufferedImage statBI, animBI;
    private int PADLEFT, PADRIGHT, HPAD;
    private int PADABOVE, PADBELOW, VPAD;
    private int RECTWIDTH, RECTHEIGHT;
    private int PADDEDWIDTH, PADDEDHEIGHT;
    private GeneralPath srcpath = new GeneralPath();
    private GeneralPath dstpath = new GeneralPath();
    private LineMetrics lm;
    private BufferedImage dBI, sBI;
    private GradientPaint gradientDst, gradientSrc;


    public ACrules() {
        setBackground(Color.white);
        sleepAmount = 400;
    }


    public void reset(int w, int h) {
        lm = f.getLineMetrics(compNames[0], frc);
        
        PADLEFT  = (w < 150) ? 10 : 15;
        PADRIGHT = (w < 150) ? 10 : 15;
        HPAD     = (PADLEFT + PADRIGHT);
        PADABOVE = 2 + (int) lm.getHeight();
        PADBELOW = 2;
        VPAD     = (PADABOVE + PADBELOW);
        RECTWIDTH = w/4 - HPAD;
        RECTWIDTH = (RECTWIDTH < 6) ? 6 : RECTWIDTH;
        RECTHEIGHT = (h-VPAD)/5 - VPAD;
        RECTHEIGHT = (RECTHEIGHT < 6) ? 6 : RECTHEIGHT;
        PADDEDWIDTH  = (RECTWIDTH  + HPAD);
        PADDEDHEIGHT = (RECTHEIGHT + VPAD);
        
        srcpath.reset();
        srcpath.moveTo(0,0);
        srcpath.lineTo(RECTWIDTH,0);
        srcpath.lineTo(RECTWIDTH,RECTHEIGHT/2);
        srcpath.lineTo(0,RECTHEIGHT);
        srcpath.closePath();
        
        dstpath.reset();
        dstpath.moveTo(0,0);
        dstpath.lineTo(0,RECTHEIGHT/2);
        dstpath.lineTo(RECTWIDTH,RECTHEIGHT);
        dstpath.lineTo(RECTWIDTH,0);
        dstpath.closePath();
        
        dBI = new BufferedImage(RECTWIDTH, RECTHEIGHT,
                                    BufferedImage.TYPE_INT_ARGB);
        sBI = new BufferedImage(RECTWIDTH, RECTHEIGHT,
                                    BufferedImage.TYPE_INT_ARGB);
        gradientDst = new GradientPaint(0, 0,
                                      new Color(1.0f, 0.0f, 0.0f, 1.0f),
                                      0, RECTHEIGHT,
                                      new Color(1.0f, 0.0f, 0.0f, 0.0f));
        gradientSrc = new GradientPaint(0, 0,
                                      new Color(0.0f, 0.0f, 1.0f, 1.0f),
                                      RECTWIDTH, 0,
                                      new Color(0.0f, 0.0f, 1.0f, 0.0f));
        statBI = new BufferedImage(w/2, h, BufferedImage.TYPE_INT_RGB);
        statBI = drawCompBI(statBI, true);
        animBI = new BufferedImage(w/2, h, BufferedImage.TYPE_INT_RGB);
    }


    public void step(int w, int h) {
        if (sleepAmount == 5000) {
            sleepAmount = 200;
        }

        srca = srca + fadeValues[fadeIndex][1];
        dsta = dsta + fadeValues[fadeIndex][4];
        fadeLabel = fadeNames[fadeIndex];
        if (srca < 0 || srca > 1.0 || dsta < 0 || dsta > 1.0) {
            sleepAmount = 5000;
            srca = fadeValues[fadeIndex][2];
            dsta = fadeValues[fadeIndex][5];
            if (fadeIndex++ == fadeValues.length-1) {
                fadeIndex = 0;
            }
        }
    }


    public void drawDemo(int w, int h, Graphics2D g2) {

        if (statBI == null || animBI == null) {
            return;
        }
        g2.drawImage(statBI, 0, 0, null);
        g2.drawImage(drawCompBI(animBI, false), w/2, 0, null);

        g2.setColor(Color.black);
        TextLayout tl = new TextLayout("AC Rules", g2.getFont(), frc);
        tl.draw(g2, 15.0f, (float) tl.getBounds().getHeight()+3.0f);

        tl = new TextLayout(fadeLabel, f, frc);
        float x = (float) (w*0.75-tl.getBounds().getWidth()/2);
        if ((x + tl.getBounds().getWidth()) > w) {
            x = (float) (w - tl.getBounds().getWidth());
        }
        tl.draw(g2, x, (float) tl.getBounds().getHeight()+3.0f);
    }


    private BufferedImage drawCompBI(BufferedImage bi, boolean doGradient)
    {
        Graphics2D big = bi.createGraphics();
        big.setColor(getBackground());
        big.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        big.setRenderingHint(RenderingHints.KEY_ANTIALIASING, AntiAlias);
        big.setFont(f);

        Graphics2D gD = dBI.createGraphics();
        gD.setRenderingHint(RenderingHints.KEY_ANTIALIASING, AntiAlias);
        Graphics2D gS = sBI.createGraphics();
        gS.setRenderingHint(RenderingHints.KEY_ANTIALIASING, AntiAlias);

        int x = 0, y = 0;
        int yy = (int) lm.getHeight() + VPAD;

        for (int i = 0; i < compNames.length; i++) {
            y = (i == 0 || i == 5) ? yy : y + PADDEDHEIGHT;
            x = (i >= 5) ? bi.getWidth()/2+PADLEFT : PADLEFT;
            big.translate(x, y);

            gD.setComposite(AlphaComposite.Clear);
            gD.fillRect(0, 0, RECTWIDTH, RECTHEIGHT);
            gD.setComposite(AlphaComposite.Src);
            if (doGradient) {
                gD.setPaint(gradientDst);
                gD.fillRect(0, 0, RECTWIDTH, RECTHEIGHT);
            } else {
                gD.setPaint(new Color(1.0f, 0.0f, 0.0f, dsta));
                gD.fill(dstpath);
            }

            gS.setComposite(AlphaComposite.Clear);
            gS.fillRect(0, 0, RECTWIDTH, RECTHEIGHT);
            gS.setComposite(AlphaComposite.Src);
            if (doGradient) {
                gS.setPaint(gradientSrc);
                gS.fillRect(0, 0, RECTWIDTH, RECTHEIGHT);
            } else {
                gS.setPaint(new Color(0.0f, 0.0f, 1.0f, srca));
                gS.fill(srcpath);
            }

            gD.setComposite(compObjs[i]);
            gD.drawImage(sBI, 0, 0, null);

            big.drawImage(dBI, 0, 0, null);
            big.setColor(Color.black);
            big.drawString(compNames[i], 0, -lm.getDescent());
            big.drawRect(0, 0, RECTWIDTH, RECTHEIGHT);
            big.translate(-x, -y);
        }

        gD.dispose();
        gS.dispose();
        big.dispose();

        return bi;
    }


    public static void main(String argv[]) {
        final DemoPanel dp = new DemoPanel(new ACrules());
        Frame f = new Frame("Java2D Demo - ACrules");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
            public void windowDeiconified(WindowEvent e) { 
                dp.surface.start(); 
            }
            public void windowIconified(WindowEvent e) { 
                dp.surface.stop(); 
            }
        });
        f.add("Center", dp);
        f.pack();
        f.setSize(new Dimension(400,300));
        f.show();
        dp.surface.start();
    }
}
