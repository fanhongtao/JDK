/*
 * @(#)Blink.java	1.6 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import java.awt.*;
import java.util.StringTokenizer;

/**
 * I love blinking things.
 *
 * @author Arthur van Hoff
 * @modified 04/24/96 Jim Hagen use getBackground
 * @modified 02/05/98 Mike McCloskey removed use of deprecated methods
 */

public class Blink extends java.applet.Applet implements Runnable
{
    Thread blinker = null;    // The thread that displays images
    String labelString;       // The label for the window
    int delay;                // the delay time between blinks

    public void init() {
	String blinkFrequency = getParameter("speed");
	delay = (blinkFrequency == null) ? 400 :
            (1000 / Integer.parseInt(blinkFrequency));
	labelString = getParameter("lbl");
	if (labelString == null)
            labelString = "Blink";
        Font font = new java.awt.Font("TimesRoman", Font.PLAIN, 24);
	setFont(font);
    }

    public void paint(Graphics g) {
        int fontSize = g.getFont().getSize();
	int x = 0, y = fontSize, space;
	int red =   (int)( 50 * Math.random());
	int green = (int)( 50 * Math.random());
	int blue =  (int)(256 * Math.random());
	Dimension d = getSize();
        g.setColor(Color.black);
	FontMetrics fm = g.getFontMetrics();
	space = fm.stringWidth(" ");
	for (StringTokenizer t = new StringTokenizer(labelString); t.hasMoreTokens();) {
	    String word = t.nextToken();
	    int w = fm.stringWidth(word) + space;
	    if (x + w > d.width) {
		x = 0;
		y += fontSize;
	    }
	    if (Math.random() < 0.5)
		g.setColor(new java.awt.Color((red + y*30) % 256, (green + x/3) % 256, blue));
	    else
                g.setColor(getBackground());
	    g.drawString(word, x, y);
	    x += w;
	}
    }

    public void start() {
	blinker = new Thread(this);
	blinker.start();
    }

    public void stop() {
	blinker = null;
    }

    public void run() {
        Thread me = Thread.currentThread();
	while (blinker == me) {
            try {
                Thread.currentThread().sleep(delay);
            }
            catch (InterruptedException e) {
            }
	    repaint();
	}
    }

  public String getAppletInfo() {
      return "Title: Blinker\nAuthor: Arthur van Hoff\nDisplays multicolored blinking text.";
  }

  public String[][] getParameterInfo() {
      String pinfo[][] = {
          {"speed", "string", "The blink frequency"},
          {"lbl", "string", "The text to blink."},
      };
      return pinfo;
  }

}

