/*
 * @(#)Highlighting.java	1.20 99/09/07
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

package demos.Fonts;


import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextLayout;
import java.awt.font.TextHitInfo;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import AnimatingSurface;


/**
 * Highlighting of text showing the caret, the highlight & the character
 * advances.
 */
public class Highlighting extends AnimatingSurface {

    private static String text[] = { "HILIGHTING", "Java2D" };
    private static Color colors[] = { Color.cyan, Color.lightGray };
    private static Font smallF = new Font("Courier", Font.PLAIN, 8);
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
        fonts[0] = new Font("Courier",Font.PLAIN,w/text[0].length()+8);
        fonts[1] = new Font("serif", Font.BOLD,w/text[1].length());
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
            float rx = (float) (w/2-layouts[i].getBounds().getWidth()/2);
            float ry = (float) ((i == 0) ? h/3 : h * 0.75f);
            float rw = (float) (layouts[i].getBounds().getWidth());
            float rh = (float) (layouts[i].getBounds().getHeight());

            // draw highlighted shape
            Shape hilite = layouts[i].getLogicalHighlightShape(0, curPos[i]);
            AffineTransform at = AffineTransform.getTranslateInstance(rx, ry);
            hilite = at.createTransformedShape(hilite);
            float hy = (float) hilite.getBounds().getY();
            float hh = (float) hilite.getBounds().getHeight();
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
                tl.draw(g2, (float) rx+cInfo[0], hy+hh+tl.getAscent()+1.0f);
            }
        }
    }


    public static void main(String argv[]) {
        createDemoFrame(new Highlighting());
    }
}
