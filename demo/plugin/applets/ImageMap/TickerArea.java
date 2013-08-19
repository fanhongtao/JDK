/*
 * Copyright (c) 2003 Sun Microsystems, Inc. All  Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * -Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduct the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT
 * BE LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT
 * OF OR RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN
 * IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that Software is not designed, licensed or intended for
 * use in the design, construction, operation or maintenance of any nuclear
 * facility.
 */

/*
 * @(#)TickerArea.java	1.10 03/01/23
 */

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.StringTokenizer;

/**
 * This ImageArea renders a string of text that constantly scrolls across
 * the indicated area of the ImageMap in the specified color.
 *
 * @author	Jim Graham
 * @version	1.10, 01/23/03
 */
class TickerArea extends ImageMapArea {

    String tickertext;
    Color  tickercolor;
    Font   tickerfont;
    int    speed;		// In pixels per second for scrolling

    int tickerx;
    int tickery;
    int tickerlen;
    long lasttick;

    public void handleArg(String s) {
	StringTokenizer st = new StringTokenizer(s, ",");

	tickertext = st.nextToken();
	tickercolor = Color.black;
	speed = 100;
	String fontname = "Serif";

	if (st.hasMoreTokens()) {
	    fontname = st.nextToken();
	    if (st.hasMoreTokens()) {
		String str = st.nextToken();
		if (str.startsWith("#")) {
		    str = str.substring(1);
		}
		try {
		    int colorval = Integer.parseInt(str, 16);
		    tickercolor = new Color((colorval >> 16) & 0xff,
					    (colorval >> 8) & 0xff,
					    (colorval >> 0) & 0xff);
		} catch (Exception e) {
		    tickercolor = Color.black;
		}
		if (st.hasMoreTokens()) {
		    str = st.nextToken();
		    try {
			speed = Integer.parseInt(str);
		    } catch (Exception e) {
			speed = 100;
		    }
		}
	    }
	}

	FontMetrics fm;
	int size;
	int nextsize = H;
	do {
	    size = nextsize;
	    tickerfont = new Font(fontname, Font.PLAIN, size);
	    fm = parent.getFontMetrics(tickerfont);
	    nextsize = (size * 9) / 10;
	} while (fm.getHeight() > H && size > 0);
	tickerlen = fm.stringWidth(tickertext);
	tickery = fm.getAscent();
    }

    public void getMedia() {
	tickerx = 0;
	repaint();
	lasttick = System.currentTimeMillis();
    }

    public boolean animate() {
	long curtick = System.currentTimeMillis();
	tickerx -= ((speed * (curtick - lasttick)) / 1000);
	if (tickerx > W || tickerx + tickerlen < 0) {
	    tickerx = W;
	}
	repaint();
	lasttick = curtick;
	return true;
    }

    public void highlight(Graphics g) {
	g.setColor(tickercolor);
	g.setFont(tickerfont);
	g.drawString(tickertext, X+tickerx, Y+tickery);
    }
}

