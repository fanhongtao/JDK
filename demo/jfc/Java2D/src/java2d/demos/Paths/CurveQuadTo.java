/*
 * @(#)CurveQuadTo.java	1.24 04/07/26
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
 * @(#)CurveQuadTo.java	1.24 04/07/26
 */

package java2d.demos.Paths;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java2d.Surface;


/**
 * Cubic & Quad curves implemented through GeneralPath.
 */
public class CurveQuadTo extends Surface {

    public CurveQuadTo() {
        setBackground(Color.white);
    }


    public void render(int w, int h, Graphics2D g2) {
        GeneralPath p = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        p.moveTo(w*.2f, h*.25f);
        p.curveTo(w*.4f, h*.5f, w*.6f, 0.0f, w*.8f, h*.25f);
        p.moveTo(w*.2f, h*.6f);
        p.quadTo(w*.5f, h*1.0f, w*.8f, h*.6f);
        g2.setColor(Color.lightGray);
        g2.fill(p);
        g2.setColor(Color.black);
        g2.draw(p);
        g2.drawString("curveTo", (int) (w*.2), (int) (h*.25f)-5);
        g2.drawString("quadTo", (int) (w*.2), (int) (h*.6f)-5);
    }

    public static void main(String s[]) {
        createDemoFrame(new CurveQuadTo());
    }
}
