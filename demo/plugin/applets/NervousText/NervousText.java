/*
 * @(#)NervousText.java	1.14 06/02/22
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
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN")
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
 * @(#)NervousText.java	1.14 06/02/22
 */

import java.awt.event.*;
import java.awt.Graphics;
import java.awt.Font;
import java.applet.Applet;

/**
 * An applet that displays jittering text on the screen.
 *
 * @author Daniel Wyszynski 04/12/95
 * @version 1.10, 02/05/97
 * @modified 05/09/95 kwalrath Changed string; added thread suspension
 * @modified 02/06/98 madbot removed use of suspend and resume and cleaned up
 */

public class NervousText extends Applet implements Runnable, MouseListener {
    String banner;		// The text to be displayed
    char bannerChars[];		// The same text as an array of characters
    char attributes[];		// Character attributes ('^' for superscript)
    Thread runner = null;	// The thread that is displaying the text
    boolean threadSuspended;	// True when thread suspended (via mouse click)

    static final int REGULAR_WD = 15;
    static final int REGULAR_HT = 36;
    static final int SMALL_WD = 12;
    static final int SMALL_HT = 24;

    Font regularFont = new Font("Serif", Font.BOLD, REGULAR_HT);
    Font smallFont = new Font("Serif", Font.BOLD, SMALL_HT);

    public void init() {
	banner = getParameter("text");
	if (banner == null) {
	    banner = "HotJava";
	}

        int bannerLength = banner.length();
	StringBuffer bc = new StringBuffer(bannerLength);
	StringBuffer attrs = new StringBuffer(bannerLength);
	int wd = 0;
	for (int i = 0; i < bannerLength; i++) {
	    char c = banner.charAt(i);
	    char a = 0;
	    if (c == '^') {
		i++;
		if (i < bannerLength) {
		    c = banner.charAt(i);
		    a = '^';
		    wd += SMALL_WD - REGULAR_WD;
		} else {
		    break;
		}
	    }
	    bc.append(c);
	    attrs.append(a);
	    wd += REGULAR_WD;
	}

	bannerLength = bc.length();
	bannerChars =  new char[bannerLength];
	attributes = new char[bannerLength];
	bc.getChars(0, bannerLength, bannerChars, 0);
	attrs.getChars(0, bannerLength, attributes, 0);

        threadSuspended = false;
	resize(wd + 10, 50);
	addMouseListener(this);
    }

    public void destroy() {
        removeMouseListener(this);
    }

    public void start() {
        runner = new Thread(this);
        runner.start();
    }

    public synchronized void stop() {
	runner = null;
        if (threadSuspended) {
            threadSuspended = false;
            notify();
        }
    }

    public void run() {
        Thread me = Thread.currentThread();
        while (runner == me) {
            try {
                Thread.sleep(100);
                synchronized(this) {
                    while (threadSuspended) {
                        wait();
                    }
                }
            } catch (InterruptedException e){
            }
            repaint();
        }
    }

    public void paint(Graphics g) {
	int length = bannerChars.length;
        for (int i = 0, x = 0; i < length; i++) {
	    int wd, ht;
	    if (attributes[i] == '^') {
		wd = SMALL_WD;
		ht = SMALL_HT;
		g.setFont(smallFont);
	    } else {
		wd = REGULAR_WD;
		ht = REGULAR_HT;
		g.setFont(regularFont);
	    }
            int px = (int) (10 * Math.random() + x);
            int py = (int) (10 * Math.random() + ht);
            g.drawChars(bannerChars, i, 1, px, py);
	    x += wd;
	}
    }

    public synchronized void mousePressed(MouseEvent e) {
        e.consume();
        threadSuspended = !threadSuspended;
        if (!threadSuspended)
            notify();
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public String getAppletInfo() {
        return "Title: NervousText\nAuthor: Daniel Wyszynski\nDisplays a text banner that jitters.";
    }

    public String[][] getParameterInfo() {
        String pinfo[][] = {
            {"text", "string", "Text to display"},
        };
        return pinfo;
    }
}
