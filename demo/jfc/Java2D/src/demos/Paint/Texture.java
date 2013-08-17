/*
 * @(#)Texture.java	1.25 99/09/07
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

package demos.Paint;


import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import Surface;


/**
 * TexturePaint of gradient, buffered image and shapes.
 */
public class Texture extends Surface {

    private static TexturePaint bluedots, greendots, triangles;
    private static TexturePaint blacklines, gradient;
    static {
        BufferedImage bi = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        Graphics2D gi = bi.createGraphics();
        gi.setBackground(Color.white);
        gi.clearRect(0,0,10,10);
        GeneralPath p1 = new GeneralPath();
        p1.moveTo(0,0);
        p1.lineTo(5,10);
        p1.lineTo(10,0);
        p1.closePath();
        gi.setColor(Color.lightGray);
        gi.fill(p1);
        triangles = new TexturePaint(bi,new Rectangle(0,0,10,10));

        bi = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        gi = bi.createGraphics();
        gi.setColor(Color.black);
        gi.fillRect(0,0,5,5);
        gi.setColor(Color.gray);
        gi.fillRect(1,1,4,4);
        blacklines = new TexturePaint(bi,new Rectangle(0,0,5,5));

        int w = 30; int h = 30;
        bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        gi = bi.createGraphics();
        Color oc = Color.white; Color ic = Color.lightGray;
        gi.setPaint(new GradientPaint(0,0,oc,w*.35f,h*.35f,ic));
        gi.fillRect(0, 0, w/2, h/2);
        gi.setPaint(new GradientPaint(w,0,oc,w*.65f,h*.35f,ic));
        gi.fillRect(w/2, 0, w/2, h/2);
        gi.setPaint(new GradientPaint(0,h,oc,w*.35f,h*.65f,ic));
        gi.fillRect(0, h/2, w/2, h/2);
        gi.setPaint(new GradientPaint(w,h,oc,w*.65f,h*.65f,ic));
        gi.fillRect(w/2, h/2, w/2, h/2);
        gradient = new TexturePaint(bi,new Rectangle(0,0,w,h));

        bi = new BufferedImage(2,2,BufferedImage.TYPE_INT_RGB);
        bi.setRGB(0, 0, 0xffffffff); bi.setRGB(1, 0, 0xffffffff);
        bi.setRGB(0, 1, 0xffffffff); bi.setRGB(1, 1, 0xff0000ff);
        bluedots = new TexturePaint(bi,new Rectangle(0,0,2,2));

        bi = new BufferedImage(2,2,BufferedImage.TYPE_INT_RGB);
        bi.setRGB(0, 0, 0xffffffff); bi.setRGB(1, 0, 0xffffffff);
        bi.setRGB(0, 1, 0xffffffff); bi.setRGB(1, 1, 0xff00ff00);
        greendots = new TexturePaint(bi,new Rectangle(0,0,2,2));
    }


    public Texture() {
        setBackground(Color.white);
    }


    public void render(int w, int h, Graphics2D g2) {

        Rectangle r = new Rectangle(10,10,w-20,h/2-20);
        g2.setPaint(gradient);
        g2.fill(r);
        g2.setPaint(Color.green);
        g2.setStroke(new BasicStroke(20));
        g2.draw(r);
        g2.setPaint(blacklines);
        g2.setStroke(new BasicStroke(15));
        g2.draw(r);

        Font f = new Font("Times New Roman", Font.BOLD, w/5);
        TextLayout tl = new TextLayout("Texture", f, g2.getFontRenderContext());
        int sw = (int) tl.getBounds().getWidth();
        int sh = (int) tl.getBounds().getHeight();
        Shape sha = tl.getOutline(AffineTransform.getTranslateInstance(w/2-sw/2, h*.25+sh/2));
        g2.setColor(Color.black);
        g2.setStroke(new BasicStroke(3));
        g2.draw(sha);
        g2.setPaint(greendots);
        g2.fill(sha);

        r.setLocation(10,h/2+10);
        g2.setPaint(triangles);
        g2.fill(r);
        g2.setPaint(blacklines);
        g2.setStroke(new BasicStroke(20));
        g2.draw(r);
        g2.setPaint(Color.green);
        g2.setStroke(new BasicStroke(4));
        g2.draw(r);

        f = new Font("serif", Font.BOLD, w/4);
        tl = new TextLayout("Paint", f, g2.getFontRenderContext());
        sw = (int) tl.getBounds().getWidth();
        sh = (int) tl.getBounds().getHeight();
        sha = tl.getOutline(AffineTransform.getTranslateInstance(w/2-sw/2, h*.75+sh/2));
        g2.setColor(Color.black);
        g2.setStroke(new BasicStroke(5));
        g2.draw(sha);
        g2.setPaint(bluedots);
        g2.fill(sha);
    }


    public static void main(String s[]) {
        createDemoFrame(new Texture());
    }
}
