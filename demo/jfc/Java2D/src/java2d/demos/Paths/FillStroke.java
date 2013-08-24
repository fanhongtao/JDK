/*
 * @(#)FillStroke.java	1.28 06/08/29
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
 * @(#)FillStroke.java	1.28 06/08/29
 */

package java2d.demos.Paths;

import static java.awt.Color.*;
import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.GeneralPath;
import java2d.Surface;


/**
 * Basic implementation of GeneralPath, filling & drawing a path w/o closing it.
 */
public class FillStroke extends Surface {


    public FillStroke() {
        setBackground(WHITE);
    }


    public void render(int w, int h, Graphics2D g2) {
        GeneralPath p = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        p.moveTo( w*.5f, h*.15f);
        p.lineTo( w*.8f, h*.75f);
        p.lineTo( w*.2f, h*.75f);
        g2.setColor(LIGHT_GRAY);
        g2.fill(p);
        g2.setColor(BLACK);
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
