/*
 * @(#)ColorPaintContext.java	1.16 98/06/29
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

import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;


class ColorPaintContext implements PaintContext {
    int color;
    ColorModel cm;
    WritableRaster savedTile;
    Object c;

    protected ColorPaintContext(int color, ColorModel cm) {
        this.color = color;
        this.cm = cm;
        c = cm.getDataElements(color, null);
    }

    public void dispose() {
    }

    public ColorModel getColorModel() {
        return cm;
    }

    public synchronized Raster getRaster(int x, int y, int w, int h) {
	WritableRaster t = savedTile;

        if (t == null || w > t.getWidth() || h > t.getHeight()) {
            t = cm.createCompatibleWritableRaster(w, h);
            for (int i = 0 ; i < h ; i++) {
                for (int j = 0 ; j < w ; j++) {
                    t.setDataElements(j, i, c);
                }
            }
	    if (w <= 64 && h <= 64) {
		savedTile = t;
	    }
        }

        return t;
    }
}
