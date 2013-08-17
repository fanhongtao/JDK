/*
 * @(#)GradientPaintContext.java	1.14 98/06/29
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.awt;

import java.awt.color.ColorSpace;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import sun.awt.image.IntegerComponentRaster;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.geom.Point2D;

class GradientPaintContext implements PaintContext {
    static ColorModel xrgbmodel =
	new DirectColorModel(24, 0x00ff0000, 0x0000ff00, 0x000000ff);

    double x1;
    double y1;
    double dx;
    double dy;
    boolean cyclic;
    int interp[];
    Raster saved;
    ColorModel model;

    public GradientPaintContext(Point2D p1, Point2D p2,
				Color c1, Color c2,
				boolean cyclic) {
	double x1, y1, x2, y2;
	int rgb1, rgb2;
	x1 = p1.getX();
	x2 = p2.getX();
	if (x1 > x2) {
	    y1 = x1;
	    x1 = x2;
	    x2 = y1;
	    y1 = p2.getY();
	    y2 = p1.getY();
	    rgb1 = c2.getRGB();
	    rgb2 = c1.getRGB();
	} else {
	    y1 = p1.getY();
	    y2 = p2.getY();
	    rgb1 = c1.getRGB();
	    rgb2 = c2.getRGB();
	}
	double dx = x2 - x1;
	double dy = y2 - y1;
	double lenSq = dx * dx + dy * dy;
	this.x1 = x1;
	this.y1 = y1;
	if (lenSq >= Double.MIN_VALUE) {
	    dx = dx / lenSq;
	    dy = dy / lenSq;
	    if (cyclic) {
		dx = dx % 1.0;
		dy = dy % 1.0;
	    }
	}
	this.dx = dx;
	this.dy = dy;
	this.cyclic = cyclic;
	int a1 = (rgb1 >> 24) & 0xff;
	int r1 = (rgb1 >> 16) & 0xff;
	int g1 = (rgb1 >>  8) & 0xff;
	int b1 = (rgb1      ) & 0xff;
	int da = ((rgb2 >> 24) & 0xff) - a1;
	int dr = ((rgb2 >> 16) & 0xff) - r1;
	int dg = ((rgb2 >>  8) & 0xff) - g1;
	int db = ((rgb2      ) & 0xff) - b1;
	if (((rgb1 & rgb2) >>> 24) == 0xff) {
	    model = xrgbmodel;
	} else {
	    model = ColorModel.getRGBdefault();
	}
	interp = new int[cyclic ? 513 : 257];
	for (int i = 0; i <= 256; i++) {
	    float rel = i / 256.0f;
	    int rgb =
		(((int) (a1 + da * rel)) << 24) |
		(((int) (r1 + dr * rel)) << 16) |
		(((int) (g1 + dg * rel)) <<  8) |
		(((int) (b1 + db * rel))      );
	    interp[i] = rgb;
	    if (cyclic) {
		interp[512 - i] = rgb;
	    }
	}
    }

    /**
     * Release the resources allocated for the operation.
     */
    public void dispose() {
	saved = null;
    }

    /**
     * Return the ColorModel of the output.
     */
    public ColorModel getColorModel() {
        return model;
    }

    /**
     * Return a Raster containing the colors generated for the graphics
     * operation.
     * @param x,y,w,h The area in device space for which colors are
     * generated.
     */
    public Raster getRaster(int x, int y, int w, int h) {
	double rowrel = (x - x1) * dx + (y - y1) * dy;

	Raster rast = saved;
	if (rast == null || rast.getWidth() < w || rast.getHeight() < h) {
	    rast = getColorModel().createCompatibleWritableRaster(w, h);
	    saved = rast;
	}
	IntegerComponentRaster irast = (IntegerComponentRaster) rast;
	int off = irast.getDataOffset(0);
	int adjust = irast.getScanlineStride() - w;
	int[] pixels = irast.getDataStorage();

	if (cyclic) {
	    cycleFillRaster(pixels, off, adjust, w, h, rowrel, dx, dy);
	} else {
	    clipFillRaster(pixels, off, adjust, w, h, rowrel, dx, dy);
	}

	return rast;
    }

    void cycleFillRaster(int[] pixels, int off, int adjust, int w, int h,
			 double rowrel, double dx, double dy) {
	rowrel = rowrel % 2.0;
	int irowrel = ((int) (rowrel * (1 << 30))) << 1;
	int idx = (int) (-dx * (1 << 31));
	int idy = (int) (-dy * (1 << 31));
	while (--h >= 0) {
	    int icolrel = irowrel;
	    for (int j = w; j > 0; j--) {
		pixels[off++] = interp[icolrel >>> 23];
		icolrel += idx;
	    }

	    off += adjust;
	    irowrel += idy;
        }
    }

    void clipFillRaster(int[] pixels, int off, int adjust, int w, int h,
			double rowrel, double dx, double dy) {
	while (--h >= 0) {
	    double colrel = rowrel;
	    int j = w;
	    if (colrel <= 0.0) {
		int rgb = interp[0];
		do {
		    pixels[off++] = rgb;
		    colrel += dx;
		} while (--j > 0 && colrel <= 0.0);
	    }
	    while (colrel < 1.0 && --j >= 0) {
		pixels[off++] = interp[(int) (colrel * 256)];
		colrel += dx;
	    }
	    if (j > 0) {
		int rgb = interp[256];
		do {
		    pixels[off++] = rgb;
		} while (--j > 0);
	    }

	    off += adjust;
	    rowrel += dy;
        }
    }
}
