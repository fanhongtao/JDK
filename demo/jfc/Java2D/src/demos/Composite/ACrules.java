/*
 * @(#)ACrules.java	1.11 99/09/07
 *
 * Copyright (c) 1998, 1999 by Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

package demos.Composite;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import AnimatingSurface;


/**
 * All the AlphaCompositing Rules demonstrated.
 */
public class ACrules extends AnimatingSurface {

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
    }


    public void reset(int w, int h) {
        setSleepAmount(400);
        FontRenderContext frc = new FontRenderContext(null, false, false);
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
        if (getSleepAmount() == 5000) {
            setSleepAmount(200);
        }

        srca = srca + fadeValues[fadeIndex][1];
        dsta = dsta + fadeValues[fadeIndex][4];
        fadeLabel = fadeNames[fadeIndex];
        if (srca < 0 || srca > 1.0 || dsta < 0 || dsta > 1.0) {
            setSleepAmount(5000);
            srca = fadeValues[fadeIndex][2];
            dsta = fadeValues[fadeIndex][5];
            if (fadeIndex++ == fadeValues.length-1) {
                fadeIndex = 0;
            }
        }
    }


    public void render(int w, int h, Graphics2D g2) {

        if (statBI == null || animBI == null) {
            return;
        }
        g2.drawImage(statBI, 0, 0, null);
        g2.drawImage(drawCompBI(animBI, false), w/2, 0, null);

        g2.setColor(Color.black);
        FontRenderContext frc = g2.getFontRenderContext();
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
        createDemoFrame(new ACrules());
    }
}
