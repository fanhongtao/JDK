/*
 * @(#)Blink.java	1.9 01/12/03
 *
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
 * @modified 07/10/00 Daniel Peek brought to code conventions, minor changes
 */

import java.awt.*;
import java.util.*;

public class Blink extends java.applet.Applet {
    private Timer timer;              // Schedules the blinking
    private String labelString;       // The label for the window
    private int delay;                // the delay time between blinks

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

    public void start() {
        timer = new Timer();     //creates a new timer to schedule the blinking
        timer.schedule(new TimerTask() {      //creates a timertask to schedule
            // overrides the run method to provide functionality 
            public void run() {  
                repaint();
            }
        }
            , delay, delay);
    }

    public void paint(Graphics g) {
        int fontSize = g.getFont().getSize();
        int x = 0, y = fontSize, space;
        int red =   (int) ( 50 * Math.random());
        int green = (int) ( 50 * Math.random());
        int blue =  (int) (256 * Math.random());
        Dimension d = getSize();
        g.setColor(Color.black);
        FontMetrics fm = g.getFontMetrics();
        space = fm.stringWidth(" ");
        for (StringTokenizer t = new StringTokenizer(labelString); 
             t.hasMoreTokens();) {
            String word = t.nextToken();
            int w = fm.stringWidth(word) + space;
            if (x + w > d.width) {
                x = 0;
                y += fontSize;  //move word to next line if it doesn't fit
            }
            if (Math.random() < 0.5)
                g.setColor(new java.awt.Color((red + y*30) % 256, 
                                              (green + x/3) % 256, blue));
            else
                g.setColor(getBackground());
            g.drawString(word, x, y);
            x += w;  //shift to the right to draw the next word
        }
    }
    
    public void stop() {
        timer.cancel();  //stops the timer
    }

    public String getAppletInfo() {
        return "Title: Blinker\n"
            + "Author: Arthur van Hoff\n" 
            + "Displays multicolored blinking text.";
    }
    
    public String[][] getParameterInfo() {
        String pinfo[][] = {
            {"speed", "string", "The blink frequency"},
            {"lbl", "string", "The text to blink."},
        };
        return pinfo;
    }
}
