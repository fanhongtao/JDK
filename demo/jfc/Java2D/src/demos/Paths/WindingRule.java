/*
 * @(#)WindingRule.java	1.17 99/09/07
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
import java.awt.geom.GeneralPath;
import Surface;


/**
 * Rectangles filled to illustrate the GenerPath winding rule, determining
 * the interior of a path.
 */
public class WindingRule extends Surface {

    public WindingRule() {
        setBackground(Color.white);
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

        g2.setColor(Color.lightGray);
        g2.fill(p);
        g2.setColor(Color.black);
        g2.draw(p);
        g2.drawString("NON_ZERO rule", 0, -5);

        g2.translate(0.0f, h*.45);

        p.setWindingRule(GeneralPath.WIND_EVEN_ODD);
        g2.setColor(Color.lightGray);
        g2.fill(p);
        g2.setColor(Color.black);
        g2.draw(p);
        g2.drawString("EVEN_ODD rule", 0, -5);
    }

    public static void main(String s[]) {
        createDemoFrame(new WindingRule());
    }
}
