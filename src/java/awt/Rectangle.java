/*
 * @(#)Rectangle.java	1.28 98/07/01
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
 * A rectangle specifies an area in a coordinate space that is 
 * defined by the rectangle's top-left point (<i>x</i>,&nbsp;<i>y</i>) 
 * in the coordinate space, its width, and its height. 
 * <p>
 * A rectangle's <code>width</code> and <code>height</code> are 
 * public fields. The constructors that allow you to create a 
 * rectangle, and the methods that allow you to modify one, do not 
 * prevent you from setting a negative value for width or height. 
 * <p>
 * A rectangle whose width or height is negative is considered 
 * empty, and all methods defined by the <code>Rectangle</code> class  
 * behave accordingly. If the rectangle is empty, then the method 
 * <code>isEmpty</code> returns <code>true</code>. No point can be 
 * contained by or inside an empty rectangle, however the values of 
 * <code>width</code> and <code>height</code> are still valid. An 
 * empty rectangle still has a location in the coordinate space, and 
 * methods that change its size or location remain valid. The 
 * behavior of methods that operate on more than one rectangle is 
 * undefined if any of the participating rectangles has a negative 
 * <code>width</code> or <code>height</code>. These methods include 
 * <code>intersects</code>, <code>intersection</code>, and 
 * <code>union</code>. 
 *
 * @version 	1.28, 07/01/98
 * @author 	Sami Shaio
 * @since       JDK1.0
 */
public class Rectangle implements Shape, java.io.Serializable {

    /**
     * The <i>x</i> coordinate of the rectangle.
     * @since     JDK1.0
     */
    public int x;

    /**
     * The <i>y</i> coordinate of the rectangle.
     * @since     JDK1.0
     */
    public int y;

    /**
     * The width of the rectangle.
     * @since     JDK1.0.
     */
    public int width;

    /**
     * The height of the rectangle.
     * @since     JDK1.0
     */
    public int height;

    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = -4345857070255674764L;

    /**
     * Constructs a new rectangle whose top-left corner is at (0,&nbsp;0) 
     * in the coordinate space, and whose width and height are zero. 
     * @since     JDK1.0
     */
    public Rectangle() {
    	this(0, 0, 0, 0);
    }

    /**
     * Constructs a new rectangle, initialized to match the values of
     * the specificed rectangle.
     * @param r  a rectangle from which to copy initial values.
     * @since JDK1.1
     */
    public Rectangle(Rectangle r) {
    	this(r.x, r.y, r.width, r.height);
    }

    /**
     * Constructs a new rectangle whose top-left corner is specified as
     * (<code>x</code>,&nbsp;<code>y</code>) and whose width and height 
     * are specified by the arguments of the same name. 
     * @param     x   the <i>x</i> coordinate.
     * @param     y   the <i>y</i> coordinate.
     * @param     width    the width of the rectangle.
     * @param     height   the height of the rectangle.
     * @since     JDK1.0
     */
    public Rectangle(int x, int y, int width, int height) {
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
    }

    /**
     * Constructs a new rectangle whose top-left corner is at (0,&nbsp;0) 
     * in the coordinate space, and whose width and height are specified 
     * by the arguments of the same name. 
     * @param     width    the width of the rectangle.
     * @param     height   the height of the rectangle.
     * @since     JDK1.0
     */
    public Rectangle(int width, int height) {
	this(0, 0, width, height);
    }

    /**
     * Constructs a new rectangle whose top-left corner is specified 
     * by the <code>point</code> argument, and whose width and height  
     * are specified by the <code>dimension</code> argument. 
     * @param     p   a point, the top-left corner of the rectangle.
     * @param     d   a dimension, representing the width and height.
     * @since     JDK1.0 
     */
    public Rectangle(Point p, Dimension d) {
	this(p.x, p.y, d.width, d.height);
    }
    
    /**
     * Constructs a new rectangle whose top-left corner is the  
     * specified point, and whose width and height are zero. 
     * @param     p   the top left corner of the rectangle.
     * @since     JDK1.0
     */
    public Rectangle(Point p) {
	this(p.x, p.y, 0, 0);
    }
    
    /**
     * Constructs a new rectangle whose top left corner is  
     * (0,&nbsp;0) and whose width and height are specified  
     * by the <code>dimension</code> argument. 
     * @param     d   a dimension, specifying width and height.
     * @since     JDK1.0
     */
    public Rectangle(Dimension d) {
	this(0, 0, d.width, d.height);
    }

    /**
     * Gets the bounding rectangle of this rectangle.
     * <p>
     * This method is included for completeness, to parallel the
     * <code>getBounds</code> method of <code>Component</code>.
     * @return    a new rectangle, equal to the bounding rectangle 
     *                for this rectangle.
     * @see       java.awt.Component#getBounds
     * @since     JDK1.1
     */
    public Rectangle getBounds() {
	return new Rectangle(x, y, width, height);
    }	

    /**
     * Sets the bounding rectangle of this rectangle to match 
     * the specified rectangle.
     * <p>
     * This method is included for completeness, to parallel the
     * <code>setBounds</code> method of <code>Component</code>.
     * @param     r   a rectangle.
     * @see       java.awt.Component#setBounds(java.awt.Rectangle)
     * @since     JDK1.1
     */
    public void setBounds(Rectangle r) {
	setBounds(r.x, r.y, r.width, r.height);
    }	

    /**
     * Sets the bounding rectangle of this rectangle to the specified 
     * values for <code>x</code>, <code>y</code>, <code>width</code>, 
     * and <code>height</code>.
     * <p>
     * This method is included for completeness, to parallel the
     * <code>setBounds</code> method of <code>Component</code>.
     * @param     x       the new <i>x</i> coordinate for the top-left
     *                    corner of this rectangle.
     * @param     y       the new <i>y</i> coordinate for the top-left
     *                    corner of this rectangle.
     * @param     width   the new width for this rectangle.
     * @param     height  the new height for this rectangle.
     * @see       java.awt.Component#setBounds(int, int, int, int)
     * @since     JDK1.1
     */
    public void setBounds(int x, int y, int width, int height) {
    	reshape(x, y, width, height);
    }	

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>setBounds(int, int, int, int)</code>.
     */
    public void reshape(int x, int y, int width, int height) {
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
    }	

    /**
     * Returns the location of this rectangle.
     * <p>
     * This method is included for completeness, to parallel the
     * <code>getLocation</code> method of <code>Component</code>.
     * @see       java.awt.Component#getLocation
     * @since     JDK1.1
     */
    public Point getLocation() {
	return new Point(x, y);
    }	

    /**
     * Moves the rectangle to the specified location.
     * <p>
     * This method is included for completeness, to parallel the
     * <code>setLocation</code> method of <code>Component</code>.
     * @param     p  the new location for the point.
     * @see       java.awt.Component#setLocation(java.awt.Point)
     * @since     JDK1.1
     */
    public void setLocation(Point p) {
	setLocation(p.x, p.y);
    }	

    /**
     * Moves the rectangle to the specified location.
     * <p>
     * This method is included for completeness, to parallel the
     * <code>setLocation</code> method of <code>Component</code>.
     * @param     x  the <i>x</i> coordinate of the new location.
     * @param     y  the <i>y</i> coordinate of the new location.
     * @see       java.awt.Component#setLocation(int, int)
     * @since     JDK1.1
     */
    public void setLocation(int x, int y) {
	move(x, y);
    }	

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>setLocation(int, int)</code>.
     */
    public void move(int x, int y) {
	this.x = x;
	this.y = y;
    }	

    /**
     * Translates the rectangle the indicated distance,
     * to the right along the <i>x</i> coordinate axis, and 
     * downward along the <i>y</i> coordinate axis.
     * @param     dx   the distance to move the rectangle 
     *                 along the <i>x</i> axis.
     * @param     dy   the distance to move the rectangle 
     *                 along the <i>y</i> axis.
     * @see       java.awt.Rectangle#setLocation(int, int)
     * @see       java.awt.Rectangle#setLocation(java.awt.Point)
     * @since     JDK1.0
     */
    public void translate(int x, int y) {
	this.x += x;
	this.y += y;
    }	

    /**
     * Gets the size (width and height) of this rectangle.
     * <p>
     * This method is included for completeness, to parallel the
     * <code>getSize</code> method of <code>Component</code>.
     * @return    a dimension, representing the size.
     * @see       java.awt.Component#getSize
     * @since     JDK1.1
     */
    public Dimension getSize() {
	return new Dimension(width, height);
    }	

    /**
     * Sets the size of this rectangle to match the specified dimension.
     * <p>
     * This method is included for completeness, to parallel the
     * <code>setSize</code> method of <code>Component</code>.
     * @param d  the new size for the Dimension object
     * @see       java.awt.Component#setSize(java.awt.Dimension)
     * @since     JDK1.1
     */
    public void setSize(Dimension d) {
	setSize(d.width, d.height);
    }	

    /**
     * Sets the size of this rectangle to the specified width and height.
     * <p>
     * This method is included for completeness, to parallel the
     * <code>setSize</code> method of <code>Component</code>.
     * @param     width    the new width for this rectangle object.
     * @param     height   the new height for this rectangle object.
     * @see       java.awt.Component#setSize(int, int)
     * @since     JDK1.1
     */
    public void setSize(int width, int height) {
    	resize(width, height);
    }	

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>setSize(int, int)</code>.
     */
    public void resize(int width, int height) {
	this.width = width;
	this.height = height;
    }	

    /**
     * Checks whether this rectangle contains the specified point.
     * @param p the point (location) to test.
     * @return    <code>true</code> if the point 
     *            (<i>x</i>,&nbsp;<i>y</i>) is inside this rectangle; 
     *            <code>false</code> otherwise.
     * @since     JDK1.1
     */
    public boolean contains(Point p) {
	return contains(p.x, p.y);
    }

    /**
     * Checks whether this rectangle contains the point
     * at the specified location (<i>x</i>,&nbsp;<i>y</i>).
     * @param     x   the <i>x</i> coordinate.
     * @param     y   the <i>y</i> coordinate.
     * @return    <code>true</code> if the point 
     *            (<i>x</i>,&nbsp;<i>y</i>) is inside this rectangle; 
     *            <code>false</code> otherwise.
     * @since     JDK1.1
     */
    public boolean contains(int x, int y) {
	return inside(x, y);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>contains(int, int)</code>.
     */
    public boolean inside(int x, int y) {
	return (x >= this.x) && ((x - this.x) < this.width) && (y >= this.y) && ((y-this.y) < this.height);
    }

    /**
     * Determines whether this rectangle and the specified rectangle  
     * intersect. Two rectangles intersect if their intersection is 
     * nonempty. 
     * @param     r   a rectangle.
     * @return    <code>true</code> if the specified rectangle 
     *            and this rectangle insersect; 
     *            <code>false</code> otherwise.
     * @since     JDK1.0
     */
    public boolean intersects(Rectangle r) {
	return !((r.x + r.width <= x) ||
		 (r.y + r.height <= y) ||
		 (r.x >= x + width) ||
		 (r.y >= y + height));
    }

    /**
     * Computes the intersection of this rectangle with the 
     * specified rectangle. Returns a new rectangle that 
     * represents the intersection of the two rectangles.
     * @param     r   a rectangle.
     * @return    the largest rectangle contained in both the 
     *            specified rectangle and in this rectangle.
     * @since   JDK1.0
     */
    public Rectangle intersection(Rectangle r) {
	int x1 = Math.max(x, r.x);
	int x2 = Math.min(x + width, r.x + r.width);
	int y1 = Math.max(y, r.y);
	int y2 = Math.min(y + height, r.y + r.height);
	return new Rectangle(x1, y1, x2 - x1, y2 - y1);
    }

    /**
     * Computes the union of this rectangle with the 
     * specified rectangle. Returns a new rectangle that 
     * represents the union of the two rectangles.
     * @param     r   a rectangle.
     * @return    the smallest rectangle containing both the specified 
     *            rectangle and this rectangle.
     * @since     JDK1.0
     */
    public Rectangle union(Rectangle r) {
	int x1 = Math.min(x, r.x);
	int x2 = Math.max(x + width, r.x + r.width);
	int y1 = Math.min(y, r.y);
	int y2 = Math.max(y + height, r.y + r.height);
	return new Rectangle(x1, y1, x2 - x1, y2 - y1);
    }

    /**
     * Adds a point, specified by the integer arguments <code>newx</code>
     * and <code>newy</code>, to this rectangle. The resulting rectangle is
     * the smallest rectangle that contains both the original rectangle 
     * and the specified point.
     * <p>
     * After adding a point, a call to <code>contains<code> with the 
     * added point as an argument will not necessarily return 
     * <code>true</code>. The <code>contains</code> method does not 
     * return <code>true</code> for points on the right or bottom 
     * edges of a rectangle. Therefore if the added point falls on 
     * the left or bottom edge of the enlarged rectangle, 
     * <code>contains</code> will return <code>false</code> for that point.
     * 
     * @param     newx   the <i>x</i> coordinate of the new point.
     * @param     newy   the <i>y</i> coordinate of the new point.
     * @since     JDK1.0
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
     * Adds the point <code>pt</code> to this rectangle. The resulting 
     * rectangle is the smallest rectangle that contains both the 
     * original rectangle and the specified point.
     * <p>
     * After adding a point, a call to <code>contains<code> with the 
     * added point as an argument will not necessarily return 
     * <code>true</code>. The <code>contains</code> method does not 
     * return <code>true</code> for points on the right or bottom 
     * edges of a rectangle. Therefore if the added point falls on 
     * the left or bottom edge of the enlarged rectangle, 
     * <code>contains</code> will return <code>false</code> for that point.
     * 
     * @param     pt the new point to add to the rectangle.
     * @since     JDK1.0
     */
    public void add(Point pt) {
	add(pt.x, pt.y);
    }

    /**
     * Adds a rectangle to this rectangle. The resulting rectangle is
     * the union of the two rectangles. 
     * @param     a rectangle.
     * @since     JDK1.0
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
     * Grows the rectangle both horizontally and vertically.
     * <p>
     * This method modifies the rectangle so that it is 
     * <code>h</code> units larger on both the left and right side, 
     * and <code>v</code> units larger at both the top and bottom. 
     * <p>
     * The new rectangle has (<code>x&nbsp;-&nbsp;h</code>, 
     * <code>y&nbsp;-&nbsp;v</code>) as its top-left corner, a 
     * width of 
     * <code>width</code>&nbsp;<code>+</code>&nbsp;<code>2h</code>, 
     * and a height of 
     * <code>height</code>&nbsp;<code>+</code>&nbsp;<code>2v</code>. 
     * <p>
     * If negative values are supplied for <code>h</code> and 
     * <code>v</code>, the size of the rectangle decreases accordingly. 
     * The <code>grow</code> method does not check whether the resulting 
     * values of <code>width</code> and <code>height</code> are 
     * non-negative. 
     * @param     h   the horizontal expansion.
     * @param     v   the vertical expansion.
     * @since     JDK1.0
     */
    public void grow(int h, int v) {
	x -= h;
	y -= v;
	width += h * 2;
	height += v * 2;
    }

    /**
     * Determines whether this rectangle is empty. A rectangle is empty if 
     * its width or its height is less than or equal to zero. 
     * @return     <code>true</code> if this rectangle is empty; 
     *             <code>false</code> otherwise.
     * @since      JDK1.0
     */
    public boolean isEmpty() {
	return (width <= 0) || (height <= 0);
    }

    /**
     * Returns the hashcode for this rectangle.
     * @return     the hashcode for this rectangle.
     * @since      JDK1.0
     */
    public int hashCode() {
	return x ^ (y*37) ^ (width*43) ^ (height*47);
    }

    /**
     * Checks whether two rectangles are equal.
     * <p>
     * The result is <kbd>true</kbd> if and only if the argument is not 
     * <kbd>null</kbd> and is a <kbd>Rectangle</kbd> object that has the 
     * same top-left corner, width, and height as this rectangle. 
     * @param     obj   the object to compare with.
     * @return    <code>true</code> if the objects are equal; 
     *            <code>false</code> otherwise.
     * @since     JDK1.0
     */
    public boolean equals(Object obj) {
	if (obj instanceof Rectangle) {
	    Rectangle r = (Rectangle)obj;
	    return (x == r.x) && (y == r.y) && (width == r.width) && (height == r.height);
	}
	return false;
    }

    /**
     * Returns a string representation of this rectangle 
     * and its values.
     * @return     a string representation of this rectangle.
     * @since      JDK1.0
     */
    public String toString() {
	return getClass().getName() + "[x=" + x + ",y=" + y + ",width=" + width + ",height=" + height + "]";
    }
}
