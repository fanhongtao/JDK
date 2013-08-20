/*
 * @(#)ColorConvert.java	1.36 04/07/26
 * 
 * Copyright (c) 2004 Sun Microsystems, Inc. All Rights Reserved.
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
 * @(#)ColorConvert.java	1.36 04/07/26
 */

package java2d.demos.Colors;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import java2d.Surface;


/**
 * ColorConvertOp a ColorSpace.TYPE_RGB BufferedImage to a ColorSpace.CS_GRAY 
 * BufferedImage.
 */
public class ColorConvert extends Surface {

    private static Image img;
    private static Color colors[] = { Color.red, Color.pink, Color.orange, 
            Color.yellow, Color.green, Color.magenta, Color.cyan, Color.blue};


    public ColorConvert() {
        setBackground(Color.white);
        img = getImage("clouds.jpg");
    }


    public void render(int w, int h, Graphics2D g2) {

        int iw = img.getWidth(this);
        int ih = img.getHeight(this);
        FontRenderContext frc = g2.getFontRenderContext();
        Font font = g2.getFont();
        g2.setColor(Color.black);
        TextLayout tl = new TextLayout("ColorConvertOp RGB->GRAY", font, frc);
        tl.draw(g2, (float) (w/2-tl.getBounds().getWidth()/2), 
                            tl.getAscent()+tl.getLeading());

        BufferedImage srcImg = 
            new BufferedImage(iw, ih, BufferedImage.TYPE_INT_RGB);
        Graphics2D srcG = srcImg.createGraphics();
        RenderingHints rhs = g2.getRenderingHints();
        srcG.setRenderingHints(rhs);
        srcG.drawImage(img, 0, 0, null);

        String s = "JavaColor";
        Font f = new Font("serif", Font.BOLD, iw/6);
        tl = new TextLayout(s, f, frc);
        Rectangle2D tlb = tl.getBounds();
        char[] chars = s.toCharArray();
        float charWidth = 0.0f;
        int rw = iw/chars.length;
        int rh = ih/chars.length;
        for (int i = 0; i < chars.length; i++) {
            tl = new TextLayout(String.valueOf(chars[i]), f, frc);
            Shape shape = tl.getOutline(null);
            srcG.setColor(colors[i%colors.length]);
            tl.draw(srcG, (float) (iw/2-tlb.getWidth()/2+charWidth), 
                        (float) (ih/2+tlb.getHeight()/2));
            charWidth += (float) shape.getBounds().getWidth();
            srcG.fillRect(i*rw, ih-rh, rw, rh);
            srcG.setColor(colors[colors.length-1-i%colors.length]);
            srcG.fillRect(i*rw, 0, rw, rh);
        }

        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorConvertOp theOp = new ColorConvertOp(cs, rhs);

        BufferedImage dstImg = 
                new BufferedImage(iw, ih, BufferedImage.TYPE_INT_RGB);
        theOp.filter(srcImg, dstImg);

        g2.drawImage(srcImg, 10, 20, w/2-20, h-30, null);
        g2.drawImage(dstImg, w/2+10, 20, w/2-20, h-30, null);
    }


    public static void main(String s[]) {
        createDemoFrame(new ColorConvert());
    }
}
