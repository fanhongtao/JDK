/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
