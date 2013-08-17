/*
 * @(#)BullsEye.java	1.11 99/09/07
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

package demos.Colors;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import Surface;


/**
 * Creating colors with an alpha value.
 */
public class BullsEye extends Surface {


    public BullsEye() {
        setBackground(Color.white);
    }


    public void render(int w, int h, Graphics2D g2) {

        Color reds[] = { Color.red.darker(), Color.red };
        for (int N = 0; N < 18; N++) {
            float i = (N + 2) / 2.0f;
            float x = (float) (5+i*(w/2/10));
            float y = (float) (5+i*(h/2/10));
            float ew = (w-10)-(i*w/10);
            float eh = (h-10)-(i*h/10);
            float alpha = (N == 0) ? 0.1f : 1.0f / (19.0f - N);
            if ( N >= 16 )
                g2.setColor(reds[N-16]);
            else
                g2.setColor(new Color(0f, 0f, 0f, alpha));
            g2.fill(new Ellipse2D.Float(x,y,ew,eh));
        }
    }


    public static void main(String s[]) {
        createDemoFrame(new BullsEye());
    }
}
