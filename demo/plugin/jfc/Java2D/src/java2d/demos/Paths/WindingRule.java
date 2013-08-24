/*
 * @(#)WindingRule.java	1.26 06/08/29
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
 * @(#)WindingRule.java	1.26 06/08/29
 */

package java2d.demos.Paths;

import static java.awt.Color.*;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java2d.Surface;


/**
 * Rectangles filled to illustrate the GenerPath winding rule, determining
 * the interior of a path.
 */
public class WindingRule extends Surface {

    public WindingRule() {
        setBackground(WHITE);
    }


    public void render(int w, int h, Graphics2D g2) {

        g2.translate(w*.2, h*.2);

        GeneralPath p = new GeneralPath(GeneralPath.WIND_NON_ZERO);
        p.moveTo(0.0f, 0.0f);
        p.lineTo(w*.5f, 0.0f);
        p.lineTo(w*.5f, h*.2f);
        p.lineTo(0.0f, h*.2f);
        p.closePath();

        p.moveTo(w*.05f, h*.05f);
        p.lineTo(w*.55f, h*.05f);
        p.lineTo(w*.55f, h*.25f);
        p.lineTo(w*.05f, h*.25f);
        p.closePath();

        g2.setColor(LIGHT_GRAY);
        g2.fill(p);
        g2.setColor(BLACK);
        g2.draw(p);
        g2.drawString("NON_ZERO rule", 0, -5);

        g2.translate(0.0f, h*.45);

        p.setWindingRule(GeneralPath.WIND_EVEN_ODD);
        g2.setColor(LIGHT_GRAY);
        g2.fill(p);
        g2.setColor(BLACK);
        g2.draw(p);
        g2.drawString("EVEN_ODD rule", 0, -5);
    }

    public static void main(String s[]) {
        createDemoFrame(new WindingRule());
    }
}
