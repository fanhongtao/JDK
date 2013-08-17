/*
 * @(#)ClickArea.java	1.8 98/03/18
 *
 * Copyright (c) 1995-1997 Sun Microsystems, Inc. All Rights Reserved.
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

/**
 * An click feedback ImageArea class.
 * This class extends the basic ImageArea Class to show the locations
 * of clicks in the image in the status message area.  This utility
 * ImageArea class is useful when setting up ImageMaps.
 *
 * @author 	Jim Graham
 * @version 	1.8, 03/18/98
 */
class ClickArea extends ImageMapArea {
    /** The X location of the last mouse press. */
    int startx;
    /** The Y location of the last mouse press. */
    int starty;
    /** A boolean to indicate whether we are currently being dragged. */
    boolean dragging;

    static String ptstr(int x, int y) {
	return "("+x+", "+y+")";
    }

    /**
     * When the user presses the mouse button, start showing coordinate
     * feedback in the status message line.
     */
    public boolean press(int x, int y) {
	showStatus("Clicked at "+ptstr(x, y));
	startx = x;
	starty = y;
	dragging = true;
	return false;
    }

    /**
     * Update the coordinate feedback every time the user moves the mouse
     * while he has the button pressed.
     */
    public boolean drag(int x, int y) {
	showStatus("Rectangle from "+ptstr(startx, starty)
		   +" to "+ptstr(x, y)
		   +" is "+(x-startx)+"x"+(y-starty));
	return false;
    }

    /**
     * Update the coordinate feedback one last time when the user releases
     * the mouse button.
     */
    public boolean lift(int x, int y) {
	dragging = false;
	return drag(x, y);
    }

    /**
     * This utility method returns the status string this area wants to
     * put into the status bar.  If this area is currently animating
     * a message, then that message takes precedence over any other area
     * that a higher stacked area may want to display, otherwise the
     * message from the higher stacked area takes precedence.
     */
    public String getStatus(String prevmsg) {
	if (dragging) {
	    return (status != null) ? status : prevmsg;
	} else {
	    return (prevmsg == null) ? status : prevmsg;
	}
    }
}

