/*
 * Copyright (c) 2003 Sun Microsystems, Inc. All  Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * -Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduct the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT
 * BE LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT
 * OF OR RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN
 * IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that Software is not designed, licensed or intended for
 * use in the design, construction, operation or maintenance of any nuclear
 * facility.
 */

/*
 * @(#)RoundHrefButtonArea.java	1.11 03/01/23
 */

/**
 * An improved, round, "Fetch a URL" ImageArea class.
 * This class extends the HrefButtonArea Class to make the 3D button
 * a rounded ellipse.  All of the same feedback and operational
 * charactistics as the HrefButtonArea apply.
 *
 * @author 	Jim Graham
 * @version 	1.11, 01/23/03
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
