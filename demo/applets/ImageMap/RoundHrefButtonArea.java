/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/**
 * An improved, round, "Fetch a URL" ImageArea class.
 * This class extends the HrefButtonArea Class to make the 3D button
 * a rounded ellipse.  All of the same feedback and operational
 * charactistics as the HrefButtonArea apply.
 *
 * @author 	Jim Graham
 * @version 	1.8, 02/06/02
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
