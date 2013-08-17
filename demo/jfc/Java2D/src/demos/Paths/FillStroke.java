/*
 * @(#)FillStroke.java	1.19 99/09/07
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

package demos.Paths;

import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.GeneralPath;
import Surface;


/**
 * Basic implementation of GeneralPath, filling & drawing a path w/o closing it.
 */
public class FillStroke extends Surface {


    public FillStroke() {
        setBackground(Color.white);
    }


    public void render(int w, int h, Graphics2D g2) {
        GeneralPath p = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        p.moveTo( w*.5f, h*.15f);
        p.lineTo( w*.8f, h*.75f);
        p.lineTo( w*.2f, h*.75f);
        g2.setColor(Color.lightGray);
        g2.fill(p);
        g2.setColor(Color.black);
        g2.setStroke(new BasicStroke(10));
        g2.draw(p);
        TextLayout tl = new TextLayout("Fill, Stroke w/o closePath", 
                                g2.getFont(), g2.getFontRenderContext());
        tl.draw(g2, (float)(w/2-tl.getBounds().getWidth()/2), h*.85f);
    }


    public static void main(String s[]) {
        createDemoFrame(new FillStroke());
    }
}
