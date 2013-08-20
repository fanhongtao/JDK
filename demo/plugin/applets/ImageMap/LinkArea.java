/*
 * @(#)LinkArea.java	1.14 04/07/26
 * 
 * Copyright (c) 2004 Sun Microsystems, Inc. All Rights Reserved.
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
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
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
 * @(#)LinkArea.java	1.14 04/07/26
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
 * @version 	1.14, 07/26/04
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
