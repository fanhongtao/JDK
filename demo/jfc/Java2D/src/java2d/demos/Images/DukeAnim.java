/*
 * @(#)DukeAnim.java	1.20 05/11/17
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
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
 * @(#)DukeAnim.java	1.20 05/11/17
 */

package java2d.demos.Images;

import java.awt.*;
import javax.swing.JButton;
import java.awt.image.ImageObserver;
import java2d.AnimatingSurface;
import java2d.DemoPanel;


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
