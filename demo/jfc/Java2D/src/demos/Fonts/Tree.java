/*
 * @(#)Tree.java	1.19 99/09/07
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
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import AnimatingSurface;


/**
 * Transformation of characters.
 */
public class Tree extends AnimatingSurface {

    private char theC = 'A';
    private Character theT = new Character(theC);
    private Character theR = new Character((char) ((int) theC + 1));


    public Tree() {
        setBackground(Color.white);
    }


    public void reset(int w, int h) { }


    public void step(int w, int h) {
        setSleepAmount(4000);
        theT = new Character(theC = ((char) ((int) theC + 1)));
        theR = new Character((char) ((int) theC + 1));
        if (theR.compareTo(new Character('z')) == 0) {
            theC = 'A';
        }
    }


    public void render(int w, int h, Graphics2D g2) {
        int mindim = Math.min(w, h);
        AffineTransform at = new AffineTransform();
        at.translate((w - mindim) / 2.0,
                     (h - mindim) / 2.0);
        at.scale(mindim, mindim);
        at.translate(0.5, 0.5);
        at.scale(0.3, 0.3);
        at.translate(-(Twidth + Rwidth), FontHeight / 4.0);
        g2.transform(at);
        tree(g2, mindim * 0.3, 0);

    }


    static Font theFont = new Font("serif", Font.PLAIN, 1);
    static double Twidth = 0.6;
    static double Rwidth = 0.6;
    static double FontHeight = 0.75;
    static Color colors[] = {Color.blue,
                             Color.red.darker(),
                             Color.green.darker()};


    public void tree(Graphics2D g2d, double size, int phase) {
        g2d.setColor(colors[phase % 3]);
        new TextLayout(theT.toString(),theFont,g2d.getFontRenderContext()).draw(g2d, 0.0f, 0.0f);
        if (size > 10.0) {
            AffineTransform at = new AffineTransform();
            at.setToTranslation(Twidth, -0.1);
            at.scale(0.6, 0.6);
            g2d.transform(at);
            size *= 0.6;
            new TextLayout(theR.toString(),theFont, g2d.getFontRenderContext()).draw(g2d, 0.0f, 0.0f);
            at.setToTranslation(Rwidth+0.75, 0);
            g2d.transform(at);
            Graphics2D g2dt = (Graphics2D) g2d.create();
            at.setToRotation(-Math.PI / 2.0);
            g2dt.transform(at);
            tree(g2dt, size, phase + 1);
            g2dt.dispose();
            at.setToTranslation(.75, 0);
            at.rotate(-Math.PI / 2.0);
            at.scale(-1.0, 1.0);
            at.translate(-Twidth, 0);
            g2d.transform(at);
            tree(g2d, size, phase);
        }
        g2d.setTransform(new AffineTransform());
    }


    public static void main(String argv[]) {
        createDemoFrame(new Tree());
    }
}
