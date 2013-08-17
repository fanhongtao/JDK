/*
 * @(#)TickerArea.java	1.5 98/03/18
 *
 * Copyright (c) 1996, 1997 Sun Microsystems, Inc. All Rights Reserved.
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
 * @version	1.5, 03/18/98
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
	String fontname = "TimesRoman";

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

