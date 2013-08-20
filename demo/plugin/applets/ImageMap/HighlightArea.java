/*
 * @(#)HighlightArea.java	1.13 04/07/26
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
 * @(#)HighlightArea.java	1.13 04/07/26
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
 * @version 	1.13, 07/26/04
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
