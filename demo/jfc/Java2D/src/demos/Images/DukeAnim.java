/*
 * @(#)DukeAnim.java	1.12 99/09/07
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

package demos.Images;

import java.awt.*;
import javax.swing.JButton;
import java.awt.image.ImageObserver;
import AnimatingSurface;
import DemoPanel;


/**
 * Animated gif with a transparent background.
 */
public class DukeAnim extends AnimatingSurface implements ImageObserver {

    private static Image agif, clouds;
    private static int aw, ah, cw;
    private int x;
    private JButton b;


    public DukeAnim() {
        setBackground(Color.white);
        clouds = getImage("clouds.jpg");
        agif = getImage("duke.running.gif");
        aw = agif.getWidth(this) / 2;
        ah = agif.getHeight(this) / 2;
        cw = clouds.getWidth(this);
        dontThread = true;
    }


    public void reset(int w, int h) { 
        b = ((DemoPanel) getParent()).tools.startStopB;
    }


    public void step(int w, int h) { }


    public void render(int w, int h, Graphics2D g2) {
        if ((x -= 3) <= -cw) {
            x = w;
        }
        g2.drawImage(clouds, x, 10, cw, h-20, this);
        g2.drawImage(agif, w/2-aw, h/2-ah, this);
    }


    public boolean imageUpdate(Image img, int infoflags,
                int x, int y, int width, int height)
    {
        if (b.isSelected() && (infoflags & ALLBITS) != 0)
            repaint();
        if (b.isSelected() && (infoflags & FRAMEBITS) != 0)
            repaint();
        return isShowing();
    }



    public static void main(String s[]) {
        createDemoFrame(new DukeAnim());
    }
}
