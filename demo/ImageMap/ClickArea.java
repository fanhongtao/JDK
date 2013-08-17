/*
 * @(#)ClickArea.java	1.9 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import java.awt.Graphics;

/**
 * An click feedback ImageArea class.
 * This class extends the basic ImageArea Class to show the locations
 * of clicks in the image in the status message area.  This utility
 * ImageArea class is useful when setting up ImageMaps.
 *
 * @author 	Jim Graham
 * @version 	1.9, 12/10/01
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

