/*
 * @(#)Point.java	1.12 97/01/27
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
 * A point representing a location in (x, y) coordinate space.
 *
 * @version 	1.12, 01/27/97
 * @author 	Sami Shaio
 */
public class Point implements java.io.Serializable {
    /**
     * The x coordinate.
     */
    public int x;

    /**
     * The y coordinate.
     */
    public int y;

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = -5276940640259749850L;

    /**
     * Constructs and initializes a Point initialized with (0, 0).
     */
    public Point() {
	this(0, 0);
    }

    /**
     * Constructs and initializes a Point with the same location as
     * the specified Point.
     * @param p a point
     */
    public Point(Point p) {
	this(p.x, p.y);
    }

    /**
     * Constructs and initializes a Point from the specified x and y 
     * coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public Point(int x, int y) {
	this.x = x;
	this.y = y;
    }

    /**
     * Returns the location of this point.
     * This method is included for completeness, to parallel the
     * getLocation method of Component.
     */
    public Point getLocation() {
	return new Point(x, y);
    }	

    /**
     * Changes the point to have the specificed location.
     * This method is included for completeness, to parallel the
     * setLocation method of Component.
     * @param p  the new location for the point
     */
    public void setLocation(Point p) {
	setLocation(p.x, p.y);
    }	

    /**
     * Changes the point to have the specificed location.
     * This method is included for completeness, to parallel the
     * setLocation method of Component.
     * @param x  the x coordinate of the new location
     * @param y  the y coordinate of the new location
     */
    public void setLocation(int x, int y) {
	move(x, y);
    }	

    /**
     * Changes the point to have the specified location.
     * @param x  the x coordinate of the new location
     * @param y  the y coordinate of the new location
     */
    public void move(int x, int y) {
	this.x = x;
	this.y = y;
    }	

    /**
     * Translates the point.
     */
    public void translate(int x, int y) {
	this.x += x;
	this.y += y;
    }	

    /**
     * Returns the hashcode for this Point.
     */
    public int hashCode() {
	return x ^ (y*31);
    }

    /**
     * Checks whether two pointers are equal.
     */
    public boolean equals(Object obj) {
	if (obj instanceof Point) {
	    Point pt = (Point)obj;
	    return (x == pt.x) && (y == pt.y);
	}
	return false;
    }

    /**
     * Returns the String representation of this Point's coordinates.
     */
    public String toString() {
	return getClass().getName() + "[x=" + x + ",y=" + y + "]";
    }
}
