/*
 * @(#)BullsEye.java	1.20 06/08/29
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
 * @(#)BullsEye.java	1.20 06/08/29
 */

package java2d.demos.Colors;

import static java.awt.Color.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java2d.Surface;


/**
 * Creating colors with an alpha value.
 */
public class BullsEye extends Surface {


    public BullsEye() {
        setBackground(WHITE);
    }


    public void render(int w, int h, Graphics2D g2) {

        Color reds[] = { RED.darker(), RED };
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
