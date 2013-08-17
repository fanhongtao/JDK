/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import java.applet.Applet;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Hashtable;
import java.net.URL;
import java.awt.image.*;
import java.net.MalformedURLException;

/**
 * An image filter to highlight an image by brightening or darkening
 * the pixels in the images.
 *
 * @author 	Jim Graham
 * @version 	1.7, 02/06/02
 */
class HighlightFilter extends RGBImageFilter {
    boolean brighter;
    int percent;

    public HighlightFilter(boolean b, int p) {
	brighter = b;
	percent = p;
	canFilterIndexColorModel = true;
    }

    public int filterRGB(int x, int y, int rgb) {
	int r = (rgb >> 16) & 0xff;
	int g = (rgb >> 8) & 0xff;
	int b = (rgb >> 0) & 0xff;
	if (brighter) {
	    r = (255 - ((255 - r) * (100 - percent) / 100));
	    g = (255 - ((255 - g) * (100 - percent) / 100));
	    b = (255 - ((255 - b) * (100 - percent) / 100));
	} else {
	    r = (r * (100 - percent) / 100);
	    g = (g * (100 - percent) / 100);
	    b = (b * (100 - percent) / 100);
	}
	if (r < 0) r = 0;
	if (r > 255) r = 255;
	if (g < 0) g = 0;
	if (g > 255) g = 255;
	if (b < 0) b = 0;
	if (b > 255) b = 255;
	return (rgb & 0xff000000) | (r << 16) | (g << 8) | (b << 0);
    }
}

