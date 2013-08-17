/*
 * @(#)MouseTrack.java	1.2 96/12/06
 *
 * Copyright (c) 1994-1996 Sun Microsystems, Inc. All Rights Reserved.
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
import java.lang.Math;

public class MouseTrack extends java.applet.Applet {

    int mx, my;
    int onaroll;

    public void init() {
	onaroll = 0;
	resize(500, 500);
    }

    public void paint(Graphics g) {
	g.drawRect(0, 0, size().width - 1, size().height - 1);
	mx = (int)(Math.random()*1000) % (size().width - (size().width/10));
	my = (int)(Math.random()*1000) % (size().height - (size().height/10));
	g.drawRect(mx, my, (size().width/10) - 1, (size().height/10) - 1);
    }

    /*
     * Mouse methods
     */
    public boolean mouseDown(java.awt.Event evt, int x, int y) {
	requestFocus();
	if((mx < x && x < mx+size().width/10-1) && (my < y && y < my+size().height/10-1)) {
	    if(onaroll > 0) {
		switch(onaroll%4) {
		case 0:
		    play(getCodeBase(), "sounds/tiptoe.thru.the.tulips.au");
		    break;
		case 1:
		    play(getCodeBase(), "sounds/danger,danger...!.au");
		    break;
		case 2:
		    play(getCodeBase(), "sounds/adapt-or-die.au");
		    break;
		case 3:
		    play(getCodeBase(), "sounds/cannot.be.completed.au");
		    break;
		}
		onaroll++;
		if(onaroll > 5)
		    getAppletContext().showStatus("You're on your way to THE HALL OF FAME:"
			+ onaroll + "Hits!");
		else
		    getAppletContext().showStatus("YOU'RE ON A ROLL:" + onaroll + "Hits!");
	    }
	    else {
		getAppletContext().showStatus("HIT IT AGAIN! AGAIN!");
		play(getCodeBase(), "sounds/that.hurts.au");
		onaroll = 1;
	    }
	}
	else {
	    getAppletContext().showStatus("You hit nothing at (" + x + ", " + y + "), exactly\n");
	    play(getCodeBase(), "sounds/thin.bell.au");
	    onaroll = 0;
	}
	repaint();
	return true;
    }

    public boolean mouseMove(java.awt.Event evt, int x, int y) {
	if((x % 3 == 0) && (y % 3 == 0))
	    repaint();
	return true;
    }

    public void mouseEnter() {
	repaint();
    }

    public void mouseExit() {
	onaroll = 0;
	repaint();
    }

    /**
     * Focus methods
     */
    public void keyDown(int key) {
	requestFocus();
	onaroll = 0;
	play(getCodeBase(), "sounds/ip.au");
    }
}
