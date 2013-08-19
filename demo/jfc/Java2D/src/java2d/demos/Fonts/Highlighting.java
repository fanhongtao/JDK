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
 * @(#)Highlighting.java	1.27 03/01/23
 */

package java2d.demos.Fonts;


import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextLayout;
import java.awt.font.TextHitInfo;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java2d.AnimatingSurface;


/**
 * Highlighting of text showing the caret, the highlight & the character
 * advances.
 */
public class Highlighting extends AnimatingSurface {

    private static String text[] = { "HILIGHTING", "Java2D" };
    private static Color colors[] = { Color.cyan, Color.lightGray };
    private static Font smallF = new Font("Monospaced", Font.PLAIN, 8);
    private int[] curPos;
    private TextLayout[] layouts;
    private Font[] fonts;


    public Highlighting() {
        setBackground(Color.white);
        fonts = new Font[2];
        layouts = new TextLayout[fonts.length];
        curPos = new int[fonts.length];
    }


    public void reset(int w, int h) {
        fonts[0] = new Font("Monospaced",Font.PLAIN,w/text[0].length()+8);
        fonts[1] = new Font("Serif", Font.BOLD,w/text[1].length());
        for (int i = 0; i < layouts.length; i++ ) {
            curPos[i] = 0;
        }
    }


    public void step(int w, int h) {
        setSleepAmount(900);
        for (int i = 0; i < 2; i++) {
            if (layouts[i] == null) {
                continue;
            }
            if (curPos[i]++ == layouts[i].getCharacterCount()) {
                curPos[i] = 0;
            }
        }
    }


    public void render(int w, int h, Graphics2D g2) {
        FontRenderContext frc = g2.getFontRenderContext();
        for (int i = 0; i < 2; i++) {
            layouts[i]  = new TextLayout(text[i], fonts[i], frc);
            float rw = layouts[i].getAdvance();
            float rh = layouts[i].getAscent() + layouts[i].getDescent();
            float rx = (float) ((w - rw) /2);
            float ry = (float) ((i == 0) ? h/3 : h * 0.75f);

            // draw highlighted shape
            Shape hilite = layouts[i].getLogicalHighlightShape(0, curPos[i]);
            AffineTransform at = AffineTransform.getTranslateInstance(rx, ry);
            hilite = at.createTransformedShape(hilite);
            float hy = (float) hilite.getBounds2D().getY();
            float hh = (float) hilite.getBounds2D().getHeight();
            g2.setColor(colors[i]);
            g2.fill(hilite);

            // get caret shape
            Shape[] shapes = layouts[i].getCaretShapes(curPos[i]);
            Shape caret = at.createTransformedShape(shapes[0]);

            g2.setColor(Color.black);
            layouts[i].draw(g2, rx, ry);
            g2.draw(caret);
            g2.draw(new Rectangle2D.Float(rx,hy,rw,hh));

            // Display character advances.
            for (int j = 0; j <= layouts[i].getCharacterCount(); j++) {
                float[] cInfo = layouts[i].getCaretInfo(TextHitInfo.leading(j));
                String str = String.valueOf((int) cInfo[0]);
                TextLayout tl = new TextLayout(str,smallF,frc);
                tl.draw(g2, (float) rx+cInfo[0]-tl.getAdvance()/2, hy+hh+tl.getAscent()+1.0f);
            }
        }
    }


    public static void main(String argv[]) {
        createDemoFrame(new Highlighting());
    }
}
