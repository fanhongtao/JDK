/*
 * @(#)HighlightArea.java	1.7 98/03/18
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
import java.net.URL;
import java.net.MalformedURLException;

/**
 * An area highlighting ImageArea class.
 * This class extends the basic ImageArea Class to highlight an area of
 * the base image when the mouse enters the area.
 *
 * @author 	Jim Graham
 * @version 	1.7, 03/18/98
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
