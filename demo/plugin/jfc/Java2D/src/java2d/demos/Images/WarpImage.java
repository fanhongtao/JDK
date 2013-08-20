/*
 * @(#)WarpImage.java	1.25 04/07/26
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
 * @(#)WarpImage.java	1.25 04/07/26
 */

package java2d.demos.Images;

import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;
import java2d.AnimatingSurface;


/**
 * Warps a image on a CubicCurve2D flattened path.
 */
public class WarpImage extends AnimatingSurface {

    private static int iw, ih, iw2, ih2;
    private static Image img;
    private static final int FORWARD = 0;
    private static final int BACK = 1;
    private Point2D pts[];
    private int direction = FORWARD;
    private int pNum;
    private int x, y;


    public WarpImage() {
        setBackground(Color.white);
        img = getImage("surfing.gif");
        iw = img.getWidth(this);
        ih = img.getHeight(this);
        iw2 = iw/2;
        ih2 = ih/2;
    }


    public void reset(int w, int h) {
        pNum = 0;
        direction = FORWARD;
        CubicCurve2D cc = new CubicCurve2D.Float(
                        w*.2f, h*.5f, w*.4f,0, w*.6f,h,w*.8f,h*.5f);
        PathIterator pi = cc.getPathIterator(null, 0.1);
        Point2D tmp[] = new Point2D[200];
        int i = 0;
        while ( !pi.isDone() ) {
            float[] coords = new float[6];
            switch ( pi.currentSegment(coords) ) {
                case PathIterator.SEG_MOVETO:
                case PathIterator.SEG_LINETO:
                        tmp[i] = new Point2D.Float(coords[0], coords[1]);
            }
            i++;
            pi.next();
        }
        pts = new Point2D[i];
        System.arraycopy(tmp,0,pts,0,i);
    }


    public void step(int w, int h) {
        if (pts == null) {
            return;
        }
        x = (int) pts[pNum].getX();
        y = (int) pts[pNum].getY();
        if (direction == FORWARD)
            if (++pNum == pts.length)
                direction = BACK;
        if (direction == BACK)
            if (--pNum == 0)
                direction = FORWARD;
    }


    public void render(int w, int h, Graphics2D g2) {
        g2.drawImage(img,
                        0,              0,              x,              y,
                        0,              0,              iw2,            ih2,
                        this);
        g2.drawImage(img,
                        x,              0,              w,              y,
                        iw2,            0,              iw,             ih2,
                        this);
        g2.drawImage(img,
                        0,              y,              x,              h,
                        0,              ih2,            iw2,            ih,
                        this);
        g2.drawImage(img,
                        x,              y,              w,              h,
                        iw2,            ih2,            iw,             ih,
                        this);
    }


    public static void main(String argv[]) {
        createDemoFrame(new WarpImage());
    }
}
