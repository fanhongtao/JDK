/*
 * @(#)Polygon.java	1.15 97/01/27
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
 */
package java.awt;

/**
 * A polygon consists of a list of x and y coordinates.
 *
 * @version 	1.15, 01/27/97
 * @author 	Sami Shaio
 * @author      Herb Jellinek
 */
public class Polygon implements Shape, java.io.Serializable {

    /**
     * The total number of points.
     */
    public int npoints = 0;

    /**
     * The array of x coordinates.
     */
    public int xpoints[] = new int[4];

    /**
     * The array of y coordinates.
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
     */
    public Polygon() {
    }

    /**
     * Constructs and initializes a Polygon from the specified parameters.
     * @param xpoints the array of x coordinates
     * @param ypoints the array of y coordinates
     * @param npoints the total number of points in the Polygon
     */
    public Polygon(int xpoints[], int ypoints[], int npoints) {
	this.npoints = npoints;
	this.xpoints = new int[npoints];
	this.ypoints = new int[npoints];
	System.arraycopy(xpoints, 0, this.xpoints, 0, npoints);
	System.arraycopy(ypoints, 0, this.ypoints, 0, npoints);	
    }
    
    /**
     * Translates the vertices by deltaX, deltaY.
     * @param deltaX the amount to move the X coords
     * @param deltaY the amount to move the Y coords
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
	bounds.x = Math.min(bounds.x, x);
	bounds.width = Math.max(bounds.width, x - bounds.x);
	bounds.y = Math.min(bounds.y, y);
	bounds.height = Math.max(bounds.height, y - bounds.y);
    }	

    /**
     * Appends a point to a polygon.  If inside(x, y) or another
     * operation that calculates the bounding box has already been
     * performed, this method updates the bounds accordingly.

     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
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
     * Returns the bounding box of the shape.
     * @return a Rectangle defining the bounds of the Polygon.
     */
    public Rectangle getBounds() {
	return getBoundingBox();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by getBounds().
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
     * Determines whether the point (x,y) is inside the Polygon. Uses
     * an even-odd insideness rule (also known as an alternating
     * rule).
     * @param x the X coordinate of the point to be tested
     * @param y the Y coordinate of the point to be tested
     *
     * <p>Kindly donated by Hanpeter van Vliet <hvvliet@inter.nl.net>.
     */
    public boolean contains(int x, int y) {
	return inside(x, y);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by contains(int, int).
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
