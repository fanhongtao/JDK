/*
 * @(#)TickerArea.java	1.7 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version	1.7, 12/03/01
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

