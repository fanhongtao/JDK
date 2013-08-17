/*
 * @(#)Blink.java	1.4 97/02/05
 *
 * Copyright (c) 1994-1997 Sun Microsystems, Inc. All Rights Reserved.
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

import java.awt.*;
import java.util.StringTokenizer;

/**
 * I love blinking things.
 *
 * @author Arthur van Hoff
 * @modified 96/04/24 Jim Hagen : use getBackground
 */
public class Blink extends java.applet.Applet implements Runnable {
    Thread blinker;
    String lbl;
    Font font;
    int speed;

    public void init() {
	font = new java.awt.Font("TimesRoman", Font.PLAIN, 24);
	String att = getParameter("speed");
	speed = (att == null) ? 400 : (1000 / Integer.valueOf(att).intValue());
	att = getParameter("lbl");
	lbl = (att == null) ? "Blink" : att;
    }
    
    public void paint(Graphics g) {
	int x = 0, y = font.getSize(), space;
	int red = (int)(Math.random() * 50);
	int green = (int)(Math.random() * 50);
	int blue = (int)(Math.random() * 256);
	Dimension d = getSize();

	g.setColor(Color.black);
	g.setFont(font);
	FontMetrics fm = g.getFontMetrics();
	space = fm.stringWidth(" ");
	for (StringTokenizer t = new StringTokenizer(lbl) ; t.hasMoreTokens() ; ) {
	    String word = t.nextToken();
	    int w = fm.stringWidth(word) + space;
	    if (x + w > d.width) {
		x = 0;
		y += font.getSize();
	    }
	    if (Math.random() < 0.5) {
		g.setColor(new java.awt.Color((red + y * 30) % 256, (green + x / 3) % 256, blue));
	    } else {
                g.setColor(getBackground());
	    }
	    g.drawString(word, x, y);
	    x += w;
	}
    }

    public void start() {
	blinker = new Thread(this);
	blinker.start();
    }
    public void stop() {
	blinker.stop();
    }
    public void run() {
	while (true) {
	try {Thread.currentThread().sleep(speed);} catch (InterruptedException e){}
	    repaint();
	}
    }
}
