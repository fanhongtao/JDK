/*
 * @(#)Append.java	1.26 06/08/29
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
 * @(#)Append.java	1.26 06/08/29
 */


package java2d.demos.Paths;

import static java.awt.Color.*;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java2d.Surface;


/**
 * Simple append of rectangle to path with & without the connect.
 */
public class Append extends Surface {

    public Append() { 
        setBackground(WHITE);
    }


    public void render(int w, int h, Graphics2D g2) {
        GeneralPath p = new GeneralPath(GeneralPath.WIND_NON_ZERO);
        p.moveTo(w*0.25f, h*0.2f);
        p.lineTo(w*0.75f, h*0.2f);
        p.closePath();
        p.append(new Rectangle2D.Double(w*.4, h*.3, w*.2, h*.1), false);
        g2.setColor(GRAY);
        g2.fill(p);
        g2.setColor(BLACK);
        g2.draw(p);
        g2.drawString("Append rect to path", (int)(w*.25), (int)(h*.2)-5);

        p.reset();
        p.moveTo(w*0.25f, h*0.6f);
        p.lineTo(w*0.75f, h*0.6f);
        p.closePath();
        p.append(new Rectangle2D.Double(w*.4, h*.7, w*.2, h*.1), true);
        g2.setColor(GRAY);
        g2.fill(p);
        g2.setColor(BLACK);
        g2.draw(p);
        g2.drawString("Append, connect", (int) (w*.25), (int) (h*.6)-5);
    }


    public static void main(String s[]) {
        createDemoFrame(new Append());
    }
}
