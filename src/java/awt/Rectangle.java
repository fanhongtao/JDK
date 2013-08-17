/*
 * @(#)Rectangle.java	1.25 97/01/27
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
 * A rectangle defined by x, y, width and height.
 *
 * @version 	1.25, 01/27/97
 * @author 	Sami Shaio
 */
public class Rectangle implements Shape, java.io.Serializable {

    /**
     * The x coordinate of the rectangle.
     */
    public int x;

    /**
     * The y coordinate of the rectangle.
     */
    public int y;

    /**
     * The width of the rectangle.
     */
    public int width;

    /**
     * The height of the rectangle.
     */
    public int height;

    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = -4345857070255674764L;

    /**
     * Constructs a new rectangle, initialized to location (0, 0) and
     * size (0, 0).
     */
    public Rectangle() {
    	this(0, 0, 0, 0);
    }

    /**
     * Constructs a new rectangle, initialized to match the values of
     * the specificed rectangle.
     * @param r  a rectangle from which to copy initial values
     */
    public Rectangle(Rectangle r) {
    	this(r.x, r.y, r.width, r.height);
    }

    /**
     * Constructs and initializes a rectangle with the specified parameters.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     */
    public Rectangle(int x, int y, int width, int height) {
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
    }

    /**
     * Constructs a rectangle and initializes it with the specified width and 
     * height parameters.
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     */
    public Rectangle(int width, int height) {
	this(0, 0, width, height);
    }

    /**
     * Constructs a rectangle and initializes it to a specified point and  
     * dimension.
     * @param p the point
     * @param d dimension 
     */
    public Rectangle(Point p, Dimension d) {
	this(p.x, p.y, d.width, d.height);
    }
    
    /**
     * Constructs a rectangle and initializes it to the specified point.
     * @param p the value of the x and y coordinate
     */
    public Rectangle(Point p) {
	this(p.x, p.y, 0, 0);
    }
    
    /**
     * Constructs a rectangle and initializes it to the specified width and 
     * height.
     * @param d the value of the width and height
     */
    public Rectangle(Dimension d) {
	this(0, 0, d.width, d.height);
    }

    /**
     * Returns the bounds of this rectangle.
     * This method is included for completeness, to parallel the
     * getBounds method of Component.
     */
    public Rectangle getBounds() {
	return new Rectangle(x, y, width, height);
    }	

    /**
     * Set the bounds of this rectangle to match the specified rectangle.
     * This method is included for completeness, to parallel the
     * setBounds method of Component.
     * @param d  the new size for the Dimension object
     */
    public void setBounds(Rectangle r) {
	setBounds(r.x, r.y, r.width, r.height);
    }	

    /**
     * Set the bounds of this rectangle to the specified x, y, width,
     * and height.
     * This method is included for completeness, to parallel the
     * setBounds method of Component.
     * @param width  the new width for the Dimension object
     * @param height  the new height for the Dimension object
     */
    public void setBounds(int x, int y, int width, int height) {
    	reshape(x, y, width, height);
    }	

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by setBounds(int, int, int, int).
     */
    public void reshape(int x, int y, int width, int height) {
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
    }	

    /**
     * Returns the location of this rectangle.
     * This method is included for completeness, to parallel the
     * getLocation method of Component.
     */
    public Point getLocation() {
	return new Point(x, y);
    }	

    /**
     * Moves the rectangle to have the specificed location.
     * This method is included for completeness, to parallel the
     * setLocation method of Component.
     * @param p  the new location for the point
     */
    public void setLocation(Point p) {
	setLocation(p.x, p.y);
    }	

    /**
     * Moves the rectangle to have the specificed location.
     * This method is included for completeness, to parallel the
     * setLocation method of Component.
     * @param x  the x coordinate of the new location
     * @param y  the y coordinate of the new location
     */
    public void setLocation(int x, int y) {
	move(x, y);
    }	

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by setLocation(int, int).
     */
    public void move(int x, int y) {
	this.x = x;
	this.y = y;
    }	

    /**
     * Translates the rectangle.
     */
    public void translate(int x, int y) {
	this.x += x;
	this.y += y;
    }	

    /**
     * Returns the size (width by height) of this rectangle.
     * This method is included for completeness, to parallel the
     * getSize method of Component.
     */
    public Dimension getSize() {
	return new Dimension(width, height);
    }	

    /**
     * Set the size of this rectangle to match the specified size.
     * This method is included for completeness, to parallel the
     * setSize method of Component.
     * @param d  the new size for the Dimension object
     */
    public void setSize(Dimension d) {
	setSize(d.width, d.height);
    }	

    /**
     * Set the size of this rectangle to the specified width and height.
     * This method is included for completeness, to parallel the
     * setSize method of Component.
     * @param width  the new width for the Dimension object
     * @param height  the new height for the Dimension object
     */
    public void setSize(int width, int height) {
    	resize(width, height);
    }	

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by setSize(int, int).
     */
    public void resize(int width, int height) {
	this.width = width;
	this.height = height;
    }	

    /**
     * Checks whether this rectangle contains the specified point.
     * @param p the point (location) to test
     */
    public boolean contains(Point p) {
	return contains(p.x, p.y);
    }

    /**
     * Checks whether this rectangle contains the specified point.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public boolean contains(int x, int y) {
	return inside(x, y);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by contains(int, int).
     */
    public boolean inside(int x, int y) {
	return (x >= this.x) && ((x - this.x) < this.width) && (y >= this.y) && ((y-this.y) < this.height);
    }

    /**
     * Checks if two rectangles intersect.
     */
    public boolean intersects(Rectangle r) {
	return !((r.x + r.width <= x) ||
		 (r.y + r.height <= y) ||
		 (r.x >= x + width) ||
		 (r.y >= y + height));
    }

    /**
     * Computes the intersection of two rectangles.
     */
    public Rectangle intersection(Rectangle r) {
	int x1 = Math.max(x, r.x);
	int x2 = Math.min(x + width, r.x + r.width);
	int y1 = Math.max(y, r.y);
	int y2 = Math.min(y + height, r.y + r.height);
	return new Rectangle(x1, y1, x2 - x1, y2 - y1);
    }

    /**
     * Computes the union of two rectangles.
     */
    public Rectangle union(Rectangle r) {
	int x1 = Math.min(x, r.x);
	int x2 = Math.max(x + width, r.x + r.width);
	int y1 = Math.min(y, r.y);
	int y2 = Math.max(y + height, r.y + r.height);
	return new Rectangle(x1, y1, x2 - x1, y2 - y1);
    }

    /**
     * Adds a point to a rectangle. This results in the smallest
     * rectangle that contains both the rectangle and the point.
     */
    public void add(int newx, int newy) {
	int x1 = Math.min(x, newx);
	int x2 = Math.max(x + width, newx);
	int y1 = Math.min(y, newy);
	int y2 = Math.max(y + height, newy);
	x = x1;
	y = y1;
	width = x2 - x1;
	height = y2 - y1;
    }

    /**
     * Adds a point to a rectangle. This results in the smallest
     * rectangle that contains both the rectangle and the point.
     */
    public void add(Point pt) {
	add(pt.x, pt.y);
    }

    /**
     * Adds a rectangle to a rectangle. This results in the union
     * of the two rectangles.
     */
    public void add(Rectangle r) {
	int x1 = Math.min(x, r.x);
	int x2 = Math.max(x + width, r.x + r.width);
	int y1 = Math.min(y, r.y);
	int y2 = Math.max(y + height, r.y + r.height);
	x = x1;
	y = y1;
	width = x2 - x1;
	height = y2 - y1;
    }

    /**
     * Grows the rectangle horizontally and vertically.
     */
    public void grow(int h, int v) {
	x -= h;
	y -= v;
	width += h * 2;
	height += v * 2;
    }

    /**
     * Determines whether the rectangle is empty.
     */
    public boolean isEmpty() {
	return (width <= 0) || (height <= 0);
    }

    /**
     * Returns the hashcode for this Rectangle.
     */
    public int hashCode() {
	return x ^ (y*37) ^ (width*43) ^ (height*47);
    }

    /**
     * Checks whether two rectangles are equal.
     */
    public boolean equals(Object obj) {
	if (obj instanceof Rectangle) {
	    Rectangle r = (Rectangle)obj;
	    return (x == r.x) && (y == r.y) && (width == r.width) && (height == r.height);
	}
	return false;
    }

    /**
     * Returns the String representation of this Rectangle's values.
     */
    public String toString() {
	return getClass().getName() + "[x=" + x + ",y=" + y + ",width=" + width + ",height=" + height + "]";
    }
}
