/*
 * @(#)Ellipses.java	1.14 99/09/07
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

package demos.Arcs_Curves;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import AnimatingSurface;


/**
 * Ellipse2D 25 animated expanding ellipses.
 */
public class Ellipses extends AnimatingSurface {

    private static Color colors[] = { Color.blue, Color.cyan, Color.green,
                Color.magenta, Color.orange, Color.pink, Color.red, 
                Color.yellow, Color.lightGray, Color.white };
    private Ellipse2D.Float[] ellipses;
    private double esize[];
    private float estroke[];
    private double maxSize;


    public Ellipses() {
        setBackground(Color.black);
        ellipses = new Ellipse2D.Float[25];
        esize = new double[ellipses.length];
        estroke = new float[ellipses.length];
        for (int i = 0; i < ellipses.length; i++) {
            ellipses[i] = new Ellipse2D.Float();
            getRandomXY(i, 20 * Math.random(), 200, 200);
        }
    }


    public void getRandomXY(int i, double size, int w, int h) {
        esize[i] = size;
        estroke[i] = 1.0f;
        double x = Math.random() * (w-(maxSize/2));
        double y = Math.random() * (h-(maxSize/2));
        ellipses[i].setFrame(x, y, size, size);
    }


    public void reset(int w, int h) {
        maxSize = w/10;
        for (int i = 0; i < ellipses.length; i++ ) {
            getRandomXY(i, maxSize * Math.random(), w, h);
        }
    }


    public void step(int w, int h) {
        for (int i = 0; i < ellipses.length; i++) {
            estroke[i] += 0.025f;
            esize[i]++;
            if (esize[i] > maxSize) {
                getRandomXY(i, 1, w, h);
            } else {
                ellipses[i].setFrame(ellipses[i].getX(), ellipses[i].getY(),
                                     esize[i], esize[i]);
            }
        }
    }


    public void render(int w, int h, Graphics2D g2) {
        for (int i = 0; i < ellipses.length; i++) {
            g2.setColor(colors[i%colors.length]);
            g2.setStroke(new BasicStroke(estroke[i]));
            g2.draw(ellipses[i]);
        }
    }


    public static void main(String argv[]) {
        createDemoFrame(new Ellipses());
    }
}
