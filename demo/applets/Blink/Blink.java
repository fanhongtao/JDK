/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/**
 * I love blinking things.
 *
 * @author Arthur van Hoff
 * @modified 04/24/96 Jim Hagen use getBackground
 * @modified 02/05/98 Mike McCloskey removed use of deprecated methods
 * @modified 04/23/99 Josh Bloch, use timer instead of explicit multithreading.
 */

import java.awt.*;
import java.util.*;

public class Blink extends java.applet.Applet {
    Timer timer;
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
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {repaint();}
        }, delay, delay);
    }

    public void stop() {
        timer.cancel();
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
