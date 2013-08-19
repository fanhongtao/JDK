/*
 * Copyright (c) 2003 Sun Microsystems, Inc. All  Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * -Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduct the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT
 * BE LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT
 * OF OR RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN
 * IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that Software is not designed, licensed or intended for
 * use in the design, construction, operation or maintenance of any nuclear
 * facility.
 */

/*
 * @(#)Outline.java	1.25 03/01/23
 */

package java2d.demos.Fonts;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import java.text.AttributedString;
import java.text.AttributedCharacterIterator;
import java2d.Surface;


/**
 * Rendering text as an outline shape.
 */
public class Outline extends Surface {

    public Outline() {
        setBackground(Color.white);
    }


    public void render(int w, int h, Graphics2D g2) {

        FontRenderContext frc = g2.getFontRenderContext();
        Font f = new Font("sansserif",Font.PLAIN,w/8);
        Font f1 = new Font("sansserif",Font.ITALIC,w/8);
        String s = "AttributedString";
        AttributedString as = new AttributedString(s);
        as.addAttribute(TextAttribute.FONT, f, 0, 10 );
        as.addAttribute(TextAttribute.FONT, f1, 10, s.length() );
        AttributedCharacterIterator aci = as.getIterator();
        TextLayout tl = new TextLayout (aci, frc);
        float sw = (float) tl.getBounds().getWidth();
        float sh = (float) tl.getBounds().getHeight();
        Shape sha = tl.getOutline(AffineTransform.getTranslateInstance(w/2-sw/2, h*0.2+sh/2));
        g2.setColor(Color.blue);
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(sha);
        g2.setColor(Color.magenta);
        g2.fill(sha);

        f = new Font("serif", Font.BOLD,w/6);
        tl = new TextLayout("Outline", f, frc);
        sw = (float) tl.getBounds().getWidth();
        sh = (float) tl.getBounds().getHeight();
        sha = tl.getOutline(AffineTransform.getTranslateInstance(w/2-sw/2,h*0.5+sh/2));
        g2.setColor(Color.black);
        g2.draw(sha);
        g2.setColor(Color.red);
        g2.fill(sha);

        f = new Font("sansserif",Font.ITALIC,w/8);
        AffineTransform fontAT = new AffineTransform();
        fontAT.shear(-0.2, 0.0);
        Font derivedFont = f.deriveFont(fontAT);
        tl = new TextLayout("Italic-Shear", derivedFont, frc);
        sw = (float) tl.getBounds().getWidth();
        sh = (float) tl.getBounds().getHeight();
        sha = tl.getOutline(AffineTransform.getTranslateInstance(w/2-sw/2,h*0.80f+sh/2));
        g2.setColor(Color.green);
        g2.draw(sha);
        g2.setColor(Color.black);
        g2.fill(sha);
    }


    public static void main(String s[]) {
        createDemoFrame(new Outline());
    }
}
