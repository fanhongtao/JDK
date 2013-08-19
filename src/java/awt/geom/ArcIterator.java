/*
 * @(#)ArcIterator.java	1.14 03/04/10
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.geom;

import java.util.*;

/**
 * A utility class to iterate over the path segments of an arc
 * through the PathIterator interface.
 *
 * @version 10 Feb 1997
 * @author	Jim Graham
 */
class ArcIterator implements PathIterator {
    double x, y, w, h, angStRad, angExtDeg;
    AffineTransform affine;
    int index;
    int arcSegs;
    int lineSegs;

    ArcIterator(Arc2D a, AffineTransform at) {
	this.w = a.getWidth() / 2;
	this.h = a.getHeight() / 2;
	this.x = a.getX() + w;
	this.y = a.getY() + h;
	this.angStRad = -Math.toRadians(a.getAngleStart());
	this.angExtDeg = -a.getAngleExtent();
	this.affine = at;
	double ext = Math.abs(angExtDeg);
	if (ext >= 360.0) {
	    arcSegs = 4;
	} else {
	    arcSegs = (int) Math.ceil(ext / 90.0);
	}
	switch (a.getArcType()) {
	case Arc2D.OPEN:
	    lineSegs = 0;
	    break;
	case Arc2D.CHORD:
	    lineSegs = 1;
	    break;
	case Arc2D.PIE:
	    lineSegs = 2;
	    break;
	}
	if (w < 0 || h < 0) {
	    arcSegs = lineSegs = -1;
	}
    }

    /**
     * Return the winding rule for determining the insideness of the
     * path.
     * @see #WIND_EVEN_ODD
     * @see #WIND_NON_ZERO
     */
    public int getWindingRule() {
	return WIND_NON_ZERO;
    }

    /**
     * Tests if there are more points to read.
     * @return true if there are more points to read
     */
    public boolean isDone() {
	return index > arcSegs + lineSegs;
    }

    /**
     * Moves the iterator to the next segment of the path forwards
     * along the primary direction of traversal as long as there are
     * more points in that direction.
     */
    public void next() {
	index++;
    }

    private static double btan(double increment) {
	increment /= 2.0;
	return 4.0 / 3.0 * Math.sin(increment) / (1.0 + Math.cos(increment));
    }

    /**
     * Returns the coordinates and type of the current path segment in
     * the iteration.
     * The return value is the path segment type:
     * SEG_MOVETO, SEG_LINETO, SEG_QUADTO, SEG_CUBICTO, or SEG_CLOSE.
     * A float array of length 6 must be passed in and may be used to
     * store the coordinates of the point(s).
     * Each point is stored as a pair of float x,y coordinates.
     * SEG_MOVETO and SEG_LINETO types will return one point,
     * SEG_QUADTO will return two points,
     * SEG_CUBICTO will return 3 points
     * and SEG_CLOSE will not return any points.
     * @see #SEG_MOVETO
     * @see #SEG_LINETO
     * @see #SEG_QUADTO
     * @see #SEG_CUBICTO
     * @see #SEG_CLOSE
     */
    public int currentSegment(float[] coords) {
	if (isDone()) {
	    throw new NoSuchElementException("arc iterator out of bounds");
	}
	double angle = angStRad;
	if (index == 0) {
	    coords[0] = (float) (x + Math.cos(angle) * w);
	    coords[1] = (float) (y + Math.sin(angle) * h);
	    if (affine != null) {
		affine.transform(coords, 0, coords, 0, 1);
	    }
	    return SEG_MOVETO;
	}
	if (index > arcSegs) {
	    if (index == arcSegs + lineSegs) {
		return SEG_CLOSE;
	    }
	    coords[0] = (float) x;
	    coords[1] = (float) y;
	    if (affine != null) {
		affine.transform(coords, 0, coords, 0, 1);
	    }
	    return SEG_LINETO;
	}
	double increment = angExtDeg;
	if (increment > 360.0) {
	    increment = 360.0;
	} else if (increment < -360.0) {
	    increment = -360.0;
	}
	increment /= arcSegs;
	increment = Math.toRadians(increment);
	angle += increment * (index - 1);
	double relx = Math.cos(angle);
	double rely = Math.sin(angle);
	double z = btan(increment);
	coords[0] = (float) (x + (relx - z * rely) * w);
	coords[1] = (float) (y + (rely + z * relx) * h);
	angle += increment;
	relx = Math.cos(angle);
	rely = Math.sin(angle);
	coords[2] = (float) (x + (relx + z * rely) * w);
	coords[3] = (float) (y + (rely - z * relx) * h);
	coords[4] = (float) (x + relx * w);
	coords[5] = (float) (y + rely * h);
	if (affine != null) {
	    affine.transform(coords, 0, coords, 0, 3);
	}
	return SEG_CUBICTO;
    }

    /**
     * Returns the coordinates and type of the current path segment in
     * the iteration.
     * The return value is the path segment type:
     * SEG_MOVETO, SEG_LINETO, SEG_QUADTO, SEG_CUBICTO, or SEG_CLOSE.
     * A double array of length 6 must be passed in and may be used to
     * store the coordinates of the point(s).
     * Each point is stored as a pair of double x,y coordinates.
     * SEG_MOVETO and SEG_LINETO types will return one point,
     * SEG_QUADTO will return two points,
     * SEG_CUBICTO will return 3 points
     * and SEG_CLOSE will not return any points.
     * @see #SEG_MOVETO
     * @see #SEG_LINETO
     * @see #SEG_QUADTO
     * @see #SEG_CUBICTO
     * @see #SEG_CLOSE
     */
    public int currentSegment(double[] coords) {
	if (isDone()) {
	    throw new NoSuchElementException("arc iterator out of bounds");
	}
	double angle = angStRad;
	if (index == 0) {
	    coords[0] = x + Math.cos(angle) * w;
	    coords[1] = y + Math.sin(angle) * h;
	    if (affine != null) {
		affine.transform(coords, 0, coords, 0, 1);
	    }
	    return SEG_MOVETO;
	}
	if (index > arcSegs) {
	    if (index == arcSegs + lineSegs) {
		return SEG_CLOSE;
	    }
	    coords[0] = x;
	    coords[1] = y;
	    if (affine != null) {
		affine.transform(coords, 0, coords, 0, 1);
	    }
	    return SEG_LINETO;
	}
	double increment = angExtDeg;
	if (increment > 360.0) {
	    increment = 360.0;
	} else if (increment < -360.0) {
	    increment = -360.0;
	}
	increment /= arcSegs;
	increment = Math.toRadians(increment);
	angle += increment * (index - 1);
	double relx = Math.cos(angle);
	double rely = Math.sin(angle);
	double z = btan(increment);
	coords[0] = x + (relx - z * rely) * w;
	coords[1] = y + (rely + z * relx) * h;
	angle += increment;
	relx = Math.cos(angle);
	rely = Math.sin(angle);
	coords[2] = x + (relx + z * rely) * w;
	coords[3] = y + (rely - z * relx) * h;
	coords[4] = x + relx * w;
	coords[5] = y + rely * h;
	if (affine != null) {
	    affine.transform(coords, 0, coords, 0, 3);
	}
	return SEG_CUBICTO;
    }
}
