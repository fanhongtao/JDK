/*
 * @(#)RectangularShape.java	1.18 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.geom;

import java.awt.Shape;
import java.awt.Rectangle;

/**
 * <code>RectangularShape</code> is the base class for a number of 
 * {@link Shape} objects whose geometry is defined by a rectangular frame.
 * This class does not directly specify any specific geometry by
 * itself, but merely provides manipulation methods inherited by
 * a whole category of <code>Shape</code> objects.
 * The manipulation methods provided by this class can be used to
 * query and modify the rectangular frame, which provides a reference
 * for the subclasses to define their geometry.
 *
 * @version 	1.18, 12/19/03
 * @author	Jim Graham
 */
public abstract class RectangularShape implements Shape, Cloneable {
    /**
     * This is an abstract class that cannot be instantiated directly.
     *
     * @see Arc2D
     * @see Ellipse2D
     * @see Rectangle2D
     * @see RoundRectangle2D
     */
    protected RectangularShape() {
    }

    /**
     * Returns the X coordinate of the upper left corner of 
     * the framing rectangle in <code>double</code> precision.
     * @return the x coordinate of the upper left corner of
     * the framing rectangle.
     */
    public abstract double getX();

    /**
     * Returns the Y coordinate of the upper left corner of 
     * the framing rectangle in <code>double</code> precision.
     * @return the y coordinate of the upper left corner of
     * the framing rectangle.
     */
    public abstract double getY();

    /**
     * Returns the width of the framing rectangle in 
     * <code>double</code> precision.
     * @return the width of the framing rectangle.
     */
    public abstract double getWidth();

    /**
     * Returns the height of the framing rectangle
     * in <code>double</code> precision.
     * @return the height of the framing rectangle.
     */
    public abstract double getHeight();

    /**
     * Returns the smallest X coordinate of the framing
     * rectangle of the <code>Shape</code> in <code>double</code>
     * precision.
     * @return the smallest x coordinate of the framing 
     * 		rectangle of the <code>Shape</code>.
     */
    public double getMinX() {
	return getX();
    }

    /**
     * Returns the smallest Y coordinate of the framing
     * rectangle of the <code>Shape</code> in <code>double</code> 
     * precision.
     * @return the smallest y coordinate of the framing 
     * 		rectangle of the <code>Shape</code>.
     */
    public double getMinY() {
	return getY();
    }

    /**
     * Returns the largest X coordinate of the framing 
     * rectangle of the <code>Shape</code> in <code>double</code>
     * precision.
     * @return the largest x coordinate of the framing
     * 		rectangle of the <code>Shape</code>.
     */
    public double getMaxX() {
	return getX() + getWidth();
    }

    /**
     * Returns the largest Y coordinate of the framing 
     * rectangle of the <code>Shape</code> in <code>double</code> 
     * precision.
     * @return the largest y coordinate of the framing 
     *		rectangle of the <code>Shape</code>.
     */
    public double getMaxY() {
	return getY() + getHeight();
    }

    /**
     * Returns the X coordinate of the center of the framing
     * rectangle of the <code>Shape</code> in <code>double</code>
     * precision.
     * @return the x coordinate of the framing rectangle 
     * 		of the <code>Shape</code> object's center.
     */
    public double getCenterX() {
	return getX() + getWidth() / 2.0;
    }

    /**
     * Returns the Y coordinate of the center of the framing 
     * rectangle of the <code>Shape</code> in <code>double</code>
     * precision.
     * @return the y coordinate of the framing rectangle 
     * 		of the <code>Shape</code> object's center.
     */
    public double getCenterY() {
	return getY() + getHeight() / 2.0;
    }

    /**
     * Returns the framing {@link Rectangle2D}
     * that defines the overall shape of this object.
     * @return a <code>Rectangle2D</code>, specified in
     * <code>double</code> coordinates.
     * @see #setFrame(double, double, double, double)
     * @see #setFrame(Point2D, Dimension2D)
     * @see #setFrame(Rectangle2D)
     */
    public Rectangle2D getFrame() {
	return new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());
    }

    /**
     * Determines whether the <code>RectangularShape</code> is empty.
     * When the <code>RectangularShape</code> is empty, it encloses no
     * area.
     * @return <code>true</code> if the <code>RectangularShape</code> is empty; 
     * 		<code>false</code> otherwise.
     */
    public abstract boolean isEmpty();

    /**
     * Sets the location and size of the framing rectangle of this
     * <code>Shape</code> to the specified rectangular values.
     * The framing rectangle is used by the subclasses of 
     * <code>RectangularShape</code> to define their geometry.
     * @param x,&nbsp;y the coordinates of the upper-left corner of the
     * 		specified rectangular shape
     * @param w the width of the specified rectangular shape
     * @param h the height of the specified rectangular shape
     * @see #getFrame
     */
    public abstract void setFrame(double x, double y, double w, double h);

    /**
     * Sets the location and size of the framing rectangle of this 
     * <code>Shape</code> to the specified {@link Point2D} and 
     * {@link Dimension2D}, respectively.  The framing rectangle is used 
     * by the subclasses of <code>RectangularShape</code> to define 
     * their geometry.
     * @param loc the specified <code>Point2D</code>
     * @param size the specified <code>Dimension2D</code>
     * @see #getFrame
     */
    public void setFrame(Point2D loc, Dimension2D size) {
	setFrame(loc.getX(), loc.getY(), size.getWidth(), size.getHeight());
    }

    /**
     * Sets the framing rectangle of this <code>Shape</code> to 
     * be the specified <code>Rectangle2D</code>.  The framing rectangle is
     * used by the subclasses of <code>RectangularShape</code> to define
     * their geometry.
     * @param r the specified <code>Rectangle2D</code>
     * @see #getFrame
     */
    public void setFrame(Rectangle2D r) {
	setFrame(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    /**
     * Sets the diagonal of the framing rectangle of this <code>Shape</code>
     * based on the two specified coordinates.  The framing rectangle is
     * used by the subclasses of <code>RectangularShape</code> to define
     * their geometry.
     * @param x1,&nbsp;y1 the first specified coordinates
     * @param x2,&nbsp;y2 the second specified coordinates
     */
    public void setFrameFromDiagonal(double x1, double y1,
				     double x2, double y2) {
	if (x2 < x1) {
	    double t = x1;
	    x1 = x2;
	    x2 = t;
	}
	if (y2 < y1) {
	    double t = y1;
	    y1 = y2;
	    y2 = t;
	}
	setFrame(x1, y1, x2 - x1, y2 - y1);
    }

    /**
     * Sets the diagonal of the framing rectangle of this <code>Shape</code> 
     * based on two specified <code>Point2D</code> objects.  The framing 
     * rectangle is used by the subclasses of <code>RectangularShape</code> 
     * to define their geometry.
     * @param p1,&nbsp;p2 the two specified <code>Point2D</code> objects
     */
    public void setFrameFromDiagonal(Point2D p1, Point2D p2) {
	setFrameFromDiagonal(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    /**
     * Sets the framing rectangle of this <code>Shape</code>
     * based on the specified center point coordinates and corner point
     * coordinates.  The framing rectangle is used by the subclasses of 
     * <code>RectangularShape</code> to define their geometry.
     * @param centerX,&nbsp;centerY the center point coordinates
     * @param cornerX,&nbsp;cornerY the corner point coordinates
     */
    public void setFrameFromCenter(double centerX, double centerY,
				   double cornerX, double cornerY) {
	double halfW = Math.abs(cornerX - centerX);
	double halfH = Math.abs(cornerY - centerY);
	setFrame(centerX - halfW, centerY - halfH, halfW * 2.0, halfH * 2.0);
    }

    /**
     * Sets the framing rectangle of this <code>Shape</code> based on a 
     * specified center <code>Point2D</code> and corner 
     * <code>Point2D</code>.  The framing rectangle is used by the subclasses 
     * of <code>RectangularShape</code> to define their geometry.
     * @param center the specified center <code>Point2D</code>
     * @param corner the specified corner <code>Point2D</code>
     */
    public void setFrameFromCenter(Point2D center, Point2D corner) {
	setFrameFromCenter(center.getX(), center.getY(),
			   corner.getX(), corner.getY());
    }

    /**
     * Tests if a specified <code>Point2D</code> is inside the boundary 
     * of the <code>Shape</code>.
     * @param p the specified <code>Point2D</code>
     * @return <code>true</code> if the <code>Point2D</code> is inside the
     * 			<code>Shape</code> object's boundary;
     *			 <code>false</code> otherwise.
     */
    public boolean contains(Point2D p) {
	return contains(p.getX(), p.getY());
    }

    /**
     * Tests if the interior of the<code>Shape</code> intersects the 
     * interior of a specified <code>Rectangle2D</code>.
     * @param r the specified <code>Rectangle2D</code>
     * @return <code>true</code> if the <code>Shape</code> and the 
     * 		specified <code>Rectangle2D</code> intersect each other; 
     * 		<code>false</code> otherwise.
     */
    public boolean intersects(Rectangle2D r) {
	return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    /**
     * Tests if the interior of the <code>Shape</code> entirely contains the
     * specified <code>Rectangle2D</code>.
     * @param r the specified <code>Rectangle2D</code>
     * @return <code>true</code> if the <code>Shape</code> entirely contains
     * 			the specified <code>Rectangle2D</code>;
     *		       <code>false</code> otherwise.
     */
    public boolean contains(Rectangle2D r) {
	return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    /**
     * Returns the bounding box of the <code>Shape</code>.
     * @return a {@link Rectangle} object that bounds the 
     * 		<code>Shape</code>.
     */
    public Rectangle getBounds() {
	double width = getWidth();
	double height = getHeight();
	if (width < 0 || height < 0) {
	    return new Rectangle();
	}
	double x = getX();
	double y = getY();
	double x1 = Math.floor(x);
	double y1 = Math.floor(y);
	double x2 = Math.ceil(x + width);
	double y2 = Math.ceil(y + height);
	return new Rectangle((int) x1, (int) y1,
				      (int) (x2 - x1), (int) (y2 - y1));
    }

    /**
     * Returns an iterator object that iterates along the 
     * <code>Shape</code> object's boundary and provides access to a
     * flattened view of the outline of the <code>Shape</code>
     * object's geometry.
     * <p>
     * Only SEG_MOVETO, SEG_LINETO, and SEG_CLOSE point types will
     * be returned by the iterator.
     * <p>
     * The amount of subdivision of the curved segments is controlled
     * by the <code>flatness</code> parameter, which specifies the
     * maximum distance that any point on the unflattened transformed
     * curve can deviate from the returned flattened path segments.
     * An optional {@link AffineTransform} can
     * be specified so that the coordinates returned in the iteration are
     * transformed accordingly.
     * @param at an optional <code>AffineTransform</code> to be applied to the
     * 		coordinates as they are returned in the iteration, 
     *		or <code>null</code> if untransformed coordinates are desired.
     * @param flatness the maximum distance that the line segments used to
     *          approximate the curved segments are allowed to deviate
     *          from any point on the original curve
     * @return a <code>PathIterator</code> object that provides access to 
     * 		the <code>Shape</code> object's flattened geometry.
     */
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
	return new FlatteningPathIterator(getPathIterator(at), flatness);
    }

    /**
     * Creates a new object of the same class and with the same
     * contents as this object.
     * @return     a clone of this instance.
     * @exception  OutOfMemoryError            if there is not enough memory.
     * @see        java.lang.Cloneable
     * @since      1.2
     */
    public Object clone() {
	try {
	    return super.clone();
	} catch (CloneNotSupportedException e) {
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
    }
}
