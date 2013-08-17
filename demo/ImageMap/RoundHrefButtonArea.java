/*
 * @(#)RoundHrefButtonArea.java	1.7 98/03/18
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

/**
 * An improved, round, "Fetch a URL" ImageArea class.
 * This class extends the HrefButtonArea Class to make the 3D button
 * a rounded ellipse.  All of the same feedback and operational
 * charactistics as the HrefButtonArea apply.
 *
 * @author 	Jim Graham
 * @version 	1.7, 03/18/98
 */
class RoundHrefButtonArea extends HrefButtonArea {
    /**
     * The filter used to create the 3D look for the button when it is up.
     */
    RoundButtonFilter roundfilter;

    /**
     * Test if the coordinate is inside the round region.  Use the test
     * provided by the filter that creates the 3D look for consistency.
     */
    public boolean inside(int x, int y) {
	return roundfilter.inside(x - X, y - Y);
    }

    /**
     * Construct the 3D look images that this area uses to draw the button.
     */
    public void makeImages() {
	roundfilter = new RoundButtonFilter(false, parent.hlpercent,
					    border, W, H);
	upImage = parent.getHighlight(X, Y, W, H, roundfilter);
	downImage = parent.getHighlight(X, Y, W, H,
					new RoundButtonFilter(true,
							      parent.hlpercent,
							      border, W, H));
    }
}
