/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import java.awt.Graphics;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * The classic "Fetch a URL" ImageArea class.
 * This class extends the basic ImageArea Class to fetch a URL when
 * the user clicks in the area.
 *
 * @author 	Jim Graham
 * @version 	1.9, 02/06/02
 */
class LinkArea extends ImageMapArea {
    /** The URL to be fetched when the user clicks on this area. */
    URL anchor;

    /**
     * The argument string is the URL to be fetched.
     */
    public void handleArg(String arg) {
	try {
	    anchor = new URL(parent.getDocumentBase(), arg);
	} catch (MalformedURLException e) {
	    anchor = null;
	}
    }

    /**
     * The isTerminal method indicates whether events should propagate
     * to the areas underlying this one.
     */
    public boolean isTerminal() {
	return true;
    }

    /**
     * The status message area is updated to show the destination URL.
     */
    public void enter() {
	showStatus((anchor != null)
		   ? "Go To " + anchor.toExternalForm()
		   : null);
    }

    /**
     * The status message area is updated to show the destination URL.
     */
    public void exit() {
	showStatus(null);
    }

    /**
     * The new URL is fetched when the user releases the mouse button
     * only if they are still in the area.
     */
    public boolean lift(int x, int y) {
	if (inside(x, y) && anchor != null) {
	    showDocument(anchor);
	}
	return true;
    }
}
