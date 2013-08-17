/*
 * @(#)Polygon.java	1.18 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
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

/**
 * The <code>Polygon</code> class encapsulates a description of a 
 * closed, two-dimensional region within a coordinate space. This 
 * region is bounded by an arbitrary number of line segments, each of 
 * which is one side of the polygon. Internally, a polygon 
 * comprises of a list of (<i>x</i>,&nbsp;<i>y</i>) coordinate pairs, 
 * where each pair defines a <i>vertex</i> of the polygon, and two 
 * successive pairs are the endpoints of a line that is a side of the 
 * polygon. The first and final pairs of (<i>x</i>,&nbsp;<i>y</i>) 
 * points are joined by a line segment that closes the polygon.
 *
 * @version 	1.18, 07/01/98
 * @author 	Sami Shaio
 * @author      Herb Jellinek
 * @since       JDK1.0
 */
public class Polygon implements Shape, java.io.Serializable {

    /**
     * The total number of points.
     * @since JDK1.0
     */
    public int npoints = 0;

    /**
     * The array of <i>x</i> coordinates. 
     * @since   JDK1.0
     */
    public int xpoints[] = new int[4];

    /**
     * The array of <i>y</i> coordinates. 
     * @since   JDK1.0
     */
    public int ypoints[] = new int[4];
    
    /*
     * Bounds of the polygon.
     */
    protected Rectangle bounds = null;
    
    /* 
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = -6460061437900069969L;

    /**
     * Creates an empty polygon.
     * @since JDK1.0
     */
    public Polygon() {
    }

    /**
     * Constructs and initializes a polygon from the specified 
     * parameters. 
     * @param      xpoints   an array of <i>x</i> coordinates.
     * @param      ypoints   an array of <i>y</i> coordinates.
     * @param      npoints   the total number of points in the polygon.
     * @exception  NegativeArraySizeException if the value of
     *                       <code>npoints</code> is negative.
     * @since      JDK1.0
     */
    public Polygon(int xpoints[], int ypoints[], int npoints) {
	this.npoints = npoints;
	this.xpoints = new int[npoints];
	this.ypoints = new int[npoints];
	System.arraycopy(xpoints, 0, this.xpoints, 0, npoints);
	System.arraycopy(ypoints, 0, this.ypoints, 0, npoints);	
    }
    
    /**
     * Translates the vertices by <code>deltaX</code> along the 
     * <i>x</i> axis and by <code>deltaY</code> along the 
     * <i>y</i> axis.
     * @param deltaX the amount to translate along the <i>x</i> axis
     * @param deltaY the amount to translate along the <i>y</i> axis
     * @since JDK1.1
     */
    public void translate(int deltaX, int deltaY) {
	for (int i = 0; i < npoints; i++) {
	    xpoints[i] += deltaX;
	    ypoints[i] += deltaY;
	}
	if (bounds != null) {
	    bounds.translate(deltaX, deltaY);
	}
    }

    /*
     * Calculate the bounding box of the points passed to the constructor.
     * Sets 'bounds' to the result.
     */
    void calculateBounds(int xpoints[], int ypoints[], int npoints) {
	int boundsMinX = Integer.MAX_VALUE;
	int boundsMinY = Integer.MAX_VALUE;
	int boundsMaxX = Integer.MIN_VALUE;
	int boundsMaxY = Integer.MIN_VALUE;
	
	for (int i = 0; i < npoints; i++) {
	    int x = xpoints[i];
	    boundsMinX = Math.min(boundsMinX, x);
	    boundsMaxX = Math.max(boundsMaxX, x);
	    int y = ypoints[i];
	    boundsMinY = Math.min(boundsMinY, y);
	    boundsMaxY = Math.max(boundsMaxY, y);
	}
	bounds = new Rectangle(boundsMinX, boundsMinY,
			       boundsMaxX - boundsMinX,
			       boundsMaxY - boundsMinY);
    }

    /*
     * Update the bounding box to fit the point x, y.
     */
    void updateBounds(int x, int y) {
	if (x < bounds.x) {
	    bounds.width = bounds.width + (bounds.x - x);
	    bounds.x = x;
	}
	else {
	    bounds.width = Math.max(bounds.width, x - bounds.x);
	    // bounds.x = bounds.x;
	}

	if (y < bounds.y) {
	    bounds.height = bounds.height + (bounds.y - y);
	    bounds.y = y;
	}
	else {
	    bounds.height = Math.max(bounds.height, y - bounds.y);
	    // bounds.y = bounds.y;
	}
    }	

    /**
     * Appends a point to this polygon. 
     * <p>
     * If an operation that calculates the bounding box of this polygon
     * has already been performed, such as <code>getBounds</code> 
     * or <code>contains</code>, then this method updates the bounding box. 
     * @param       x   the <i>x</i> coordinate of the point.
     * @param       y   the <i>y</i> coordinate of the point.
     * @see         java.awt.Polygon#getBounds
     * @see         java.awt.Polygon#contains
     * @since       JDK1.0
     */
    public void addPoint(int x, int y) {
	if (npoints == xpoints.length) {
	    int tmp[];

	    tmp = new int[npoints * 2];
	    System.arraycopy(xpoints, 0, tmp, 0, npoints);
	    xpoints = tmp;

	    tmp = new int[npoints * 2];
	    System.arraycopy(ypoints, 0, tmp, 0, npoints);
	    ypoints = tmp;
	}
	xpoints[npoints] = x;
	ypoints[npoints] = y;
	npoints++;
	if (bounds != null) {
	    updateBounds(x, y);
	}
    }

    /**
     * Gets the bounding box of this polygon. The bounding box is the
     * smallest rectangle whose sides are parallel to the <i>x</i> and
     * <i>y</i> axes of the coordinate space, and that can completely
     * contain the polygon.
     * @return      a rectangle that defines the bounds of this polygon.
     * @since       JDK1.1
     */
    public Rectangle getBounds() {
	return getBoundingBox();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getBounds()</code>.
     */
    public Rectangle getBoundingBox() {
	if (bounds == null) {
	    calculateBounds(xpoints, ypoints, npoints);
	}
	return bounds;
    }

    /**
     * Determines whether the specified point is inside the Polygon.
     * Uses an even-odd insideness rule (also known as an alternating
     * rule).
     * @param p the point to be tested
     */
    public boolean contains(Point p) {
	return contains(p.x, p.y);
    }

    /**
     * Determines whether the specified point is contained by this polygon.   
     * <p>
     * (The <code>contains</code> method is based on code by 
     * Hanpeter van Vliet [hvvliet@inter.nl.net].) 
     * @param      x  the <i>x</i> coordinate of the point to be tested.
     * @param      y  the <i>y</i> coordinate of the point to be tested.
     * @return     <code>true</code> if the point (<i>x</i>,&nbsp;<i>y</i>) 
     *                       is contained by this polygon; 
     *                       <code>false</code> otherwise.
     * @since      JDK1.1
     */
    public boolean contains(int x, int y) {
	return inside(x, y);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>contains(int, int)</code>.
     */
    public boolean inside(int x, int y) {
        if (getBoundingBox().inside(x, y)) {
            int hits = 0;
            int ySave = 0;

            // Find a vertex that's not on the halfline
            int i = 0;
            while (i < npoints && ypoints[i] == y) {
                i++;
	    }

            // Walk the edges of the polygon
            for (int n = 0; n < npoints; n++) {
                int j = (i + 1) % npoints;

                int dx = xpoints[j] - xpoints[i];
                int dy = ypoints[j] - ypoints[i];

                // Ignore horizontal edges completely
                if (dy != 0) {
                    // Check to see if the edge intersects
                    // the horizontal halfline through (x, y)
                    int rx = x - xpoints[i];
                    int ry = y - ypoints[i];

                    // Deal with edges starting or ending on the halfline
                    if (ypoints[j] == y && xpoints[j] >= x) {
                        ySave = ypoints[i];
		    }
                    if (ypoints[i] == y && xpoints[i] >= x) {
                        if ((ySave > y) != (ypoints[j] > y)) {
			    hits--;
			}
		    }

                    // Tally intersections with halfline
                    float s = (float)ry / (float)dy;
                    if (s >= 0.0 && s <= 1.0 && (s * dx) >= rx) {
                        hits++;
		    }
                }
                i = j;
            }

            // Inside if number of intersections odd
            return (hits % 2) != 0;
        }
        return false;
    }
}
