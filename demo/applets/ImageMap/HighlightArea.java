/*
 * @(#)HighlightArea.java	1.8 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import java.awt.Graphics;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * An area highlighting ImageArea class.
 * This class extends the basic ImageArea Class to highlight an area of
 * the base image when the mouse enters the area.
 *
 * @author 	Jim Graham
 * @version 	1.8, 11/29/01
 */
class HighlightArea extends ImageMapArea {
    int hlmode;
    int hlpercent;

    /**
     * The argument string is the highlight mode to be used.
     */
    public void handleArg(String arg) {
	if (arg == null) {
	    hlmode = parent.hlmode;
	    hlpercent = parent.hlpercent;
	} else {
	    if (arg.startsWith("darker")) {
		hlmode = parent.DARKER;
		arg = arg.substring("darker".length());
	    } else {
		hlmode = parent.BRIGHTER;
		if (arg.startsWith("brighter")) {
		    arg = arg.substring("brighter".length());
		}
	    }
	    hlpercent = Integer.parseInt(arg);
	}
    }

    public void makeImages() {
	setHighlight(parent.getHighlight(X, Y, W, H, hlmode, hlpercent));
    }

    public void highlight(Graphics g) {
	if (entered) {
	    g.drawImage(hlImage, X, Y, this);
	}
    }

    /**
     * The area is repainted when the mouse enters.
     */
    public void enter() {
	repaint();
    }

    /**
     * The area is repainted when the mouse leaves.
     */
    public void exit() {
	repaint();
    }
}
