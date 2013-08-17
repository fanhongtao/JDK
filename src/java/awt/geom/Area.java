/*
 * @(#)Area.java	1.21 98/07/06
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

/*
 * (C) Copyright Taligent, Inc. 1996 - 1997, All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - 1998, All Rights Reserved
 *
 * The original version of this source code and documentation is
 * copyrighted and owned by Taligent, Inc., a wholly-owned subsidiary
 * of IBM. These materials are provided under terms of a License
 * Agreement between Taligent and Sun. This technology is protected
 * by multiple US and International patents.
 *
 * This notice and attribution to Taligent may not be removed.
 * Taligent is a registered trademark of Taligent, Inc.
 *
 */

package java.awt.geom;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;

import sun.awt.Albert.TGrafMatrix;
import sun.awt.Albert.TGCurve;
import sun.awt.Albert.TGEllipse;
import sun.awt.Albert.TGLoop;
import sun.awt.Albert.TGPoint;
import sun.awt.Albert.TGPolygon;
import sun.awt.Albert.TGRect;
import sun.awt.Albert.TEllipseGeometry;
import sun.awt.Albert.TLoopGeometry;
import sun.awt.Albert.TPolygonGeometry;
import sun.awt.Albert.TRectGeometry;
import sun.awt.Albert.TCAGRoot;
import sun.awt.Albert.TOutlineMakerVertexEngine;
import sun.awt.Albert.TPathExtractor;
import sun.awt.Albert.TSamplingExtractor;
import sun.awt.Albert.MAreaGeometry;
import sun.awt.Albert.AreaPathIterator;

//--+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+
/**
 * The <code>Area</code> class is a device-independent specification of an
 * arbitrarily-shaped area.  The <code>Area</code> object is defined as an
 * object that performs certain binary CAG (Constructive Area Geometry)
 * operations on other area-enclosing geometries, such as rectangles,
 * ellipses, and polygons. The CAG operations are Add(union), Subtract,
 * Intersect, and ExclusiveOR. For example, an <code>Area</code> can be
 * made up of the area of a rectangle minus the area of an ellipse.
 */
public class Area implements Shape, Cloneable {

/*    ________________________________________________________________________
 *    Constructors
 */

    /**
     * Default constructor which creates an empty area.
     */
    public Area() {
    }

    /**
     * The <code>Area</code> class creates an area geometry from the specified 
     * {@link Shape} object.  The geometry is explicitly closed, if the 
     * <code>Shape</code> is not already closed.  The fill rule 
     * (even-odd or winding) specified by the geometry of the <code>Shape</code> 
     * is used to determine the resulting enclosed area.
     * @param g  the <code>Shape</code> from which the area is constructed
     */
    public Area(Shape g) {
        if (g == null)
            return;

	if (g instanceof Rectangle2D) {
	    TGRect geometry = new TGRect((RectangularShape) g);
	    fGeometry = new TRectGeometry(geometry);
	    return;
	}

	if (g instanceof Ellipse2D) {
	    TGRect geometry = new TGRect((RectangularShape) g);
	    fGeometry = new TEllipseGeometry(new TGEllipse(geometry));
	    return;
	}

	if (g instanceof Polygon) {
	    TGPolygon temp = new TGPolygon((Polygon) g);
	    if (temp.getNumberOfPoints() == 4 &&
		temp.isRectilinear() &&
		temp.getPoint(0).equals(temp.getPoint(2)) &&
		temp.getPoint(1).equals(temp.getPoint(3)))
	    {
		fGeometry = new TRectGeometry(temp.getBounds());
	    } else {
		fGeometry = new TPolygonGeometry(temp);
	    }
	    return;
	}

        // In the earlier implementation, a new Area object
        // would point to the same underlying geometry object. This is
        // incorrect in the situation when we call the copy constructor.
        //
        //    Area newOne = new Area (exisitingAreaObject);
        //
        // Our expectation is that when we change the newOne object, say
        // we transform it, then the 'existingAreaObject' should not
        // change.

        // The fix for this problem is to clone the geometry object.
        // All MAreaGeometries have implemented the clone method and
        // in Java it is safe to do.

        if (g instanceof Area) {
            // fGeometry = ((Area)g).fGeometry; //  originally.
	    MAreaGeometry mag = ((Area) g).fGeometry;
	    if (mag != null) {
		fGeometry = (MAreaGeometry) mag.clone();
	    }
            return;
        }

        TGCurve theCurve = new TGCurve();
        double tmp[] = new double[6];    // array of points
        TGPoint close = new TGPoint();
        TGPoint lastP = new TGPoint();
        TGPoint nextP = new TGPoint();
        TGPoint tmpP = null;
        PathIterator i = g.getPathIterator(null);
        for (; !i.isDone(); i.next()) {
            switch (i.currentSegment(tmp)) {
            default:
            case PathIterator.SEG_MOVETO:
                lastP.x = tmp[0];
                lastP.y = tmp[1];
                close.copyFrom(lastP);
                break;
            case PathIterator.SEG_LINETO:
                nextP.x = tmp[0];
                nextP.y = tmp[1];
                theCurve.concatenate(new TGCurve(lastP, nextP));
                tmpP = lastP; lastP = nextP; nextP = tmpP;
                break;
            case PathIterator.SEG_QUADTO:
                nextP.x = tmp[2];
                nextP.y = tmp[3];
                tmpP = new TGPoint(tmp[0], tmp[1]);
                theCurve.concatenate(new TGCurve(lastP, tmpP, nextP));
                tmpP = lastP; lastP = nextP; nextP = tmpP;
                break;
            case PathIterator.SEG_CUBICTO:
                nextP.x = tmp[4];
                nextP.y = tmp[5];
                tmpP = new TGPoint(tmp[0], tmp[1]);
                TGPoint p23 = new TGPoint(tmp[2], tmp[3]);
                theCurve.concatenate(new TGCurve(lastP, tmpP, p23, nextP));
                tmpP = lastP; lastP = nextP; nextP = tmpP;
                break;
            case PathIterator.SEG_CLOSE:
                if (! close.equals (lastP)) {
                    theCurve.concatenate(new TGCurve(lastP, close));
                    lastP.copyFrom(close);
                }
                break;
            }
        }
        boolean EOFill = (i.getWindingRule() == PathIterator.WIND_EVEN_ODD);
        TGLoop geometry = new TGLoop(theCurve, EOFill);
        fGeometry = new TLoopGeometry(geometry);
    }

/*    ________________________________________________________________________
 *    computational geometry functions
 */

    /**
     * Transforms the geometry of this <code>Area</code> using the specified 
     * {@link AffineTransform}.  The geometry is transformed in place, which 
     * permanently changes the enclosed area defined by this object.
     * @param t  the matrix used to transform the area
     */
    public void transform(AffineTransform t) {
        TGrafMatrix matrix = new TGrafMatrix(t);
        if ((fGeometry != null) && !matrix.isIdentity())
            fGeometry = fGeometry.cloneAndTransform(matrix);
    }

    /**
     * Removes all the basic geometry from this area and restores it to
     * an empty area.
     */
    public void reset() {
        fGeometry = null;
    }

    /**
     * Tests whether this area contains any geometry.
     * @return    <code>true</code> if this area contains no basic geometry, or
     * is an empty area; <code>false</code> otherwise.
     */
    public boolean isEmpty() {
        return ((fGeometry == null) ||
		fGeometry.isEmpty() ||
		getPathIterator(null).isDone());
    }

    /**
     * Tests whether the area consists entirely of straight edged
     * polygonal geometry.
     * @return    <code>true</code> if the area consists completely of polygon 
     * 		edges; <code>false</code> otherwise.
     */
    public boolean isPolygonal() {
        return (fGeometry == null) || fGeometry.isPolygonal();
    }

    /**
     * Tests whether the area is rectangular in shape.
     * @return    <code>true</code> if the area is rectangular in shape;
     *		<code>false</code> otherwise.
     */
    public boolean isRectangular() {
        return (fGeometry == null) || fGeometry.isRectangular();
    }

    /**
     * Tests whether the area is comprised of a single basic geometry.
     * @return    <code>true</code> if the area is comprised of a single 
     *		basic geometry; <code>false</code> otherwise.
     */
    public boolean isSingular() {
        return (fGeometry == null) || fGeometry.isSingular();
    }

    /**
     * Returns a bounding {@link Rectangle} that completely encloses the area.
     * @return    the bounding <code>Rectangle</code> for the area.
     */
    public Rectangle getBounds() {
        return internalGetBounds().getBounds();
    }

    /**
     * Returns a high precision bounding {@link Rectangle2D} that completely
     * encloses the area.
     * @return    the bounding <code>Rectangle2D</code> for the area.
     */
    public Rectangle2D getBounds2D() {
        return internalGetBounds();
    }

    /**
     * Tests whether the interior of the area intersects the interior
     * of the specified rectangular area.
     * @param x,&nbsp;y the coordinates of the upper left corner of the specified
     *		rectangular area
     * @param w the width of the specified rectangular area
     * @param h the height of teh specified rectangular area
     * @return    <code>true</code> if the interior intersects the specified 
     *		rectangular area; <code>false</code> otherwise;
     */
    public boolean intersects(double x, double y, double w, double h) {
        TGRect rect = new TGRect(x, y, x + w, y + h);
        return ((fGeometry != null) &&
		(fGeometry.intersects(rect) ||
		 fGeometry.contains(rect)));
    }

    /**
     * Tests whether the interior of the area intersects the interior
     * of the specified <code>Rectangle2D</code>.
     * @param     r  the <code>Rectangle2D</code> to test for intersection
     * @return    <code>true</code> if the interior intersects the 
     *			specified <code>Rectangle2D</code>; 
     *			<code>false</code> otherwise.
     */
    public boolean intersects(Rectangle2D r) {
        TGRect rect = new TGRect(r);
        return (fGeometry != null) && fGeometry.intersects(rect);
    }

    /**
     * Tests if a specifed point lies inside the boundary of the shape.
     * @param     x,&nbsp;y the specified point
     * @return    <code>true</code> if the point lies completely within the 
     *			interior of the area; <code>false</code> otherwise.
     */
    public boolean contains(double x, double y) {
        TGPoint pt = new TGPoint(x, y);
        return (fGeometry != null) && fGeometry.contains(pt);
    }

    /**
     * Tests if a specified {@link Point2D} lies inside the boundary of the 
     * shape.
     * @param     p  the <code>Point2D</code> to test
     * @return    <code>true</code> if the specified <code>Point2D</code>
     *		 lies completely within the interior of the area; 
     *		 <code>false</code> otherwise.
     */
    public boolean contains(Point2D p) {
        TGPoint pt = new TGPoint(p);
        return (fGeometry != null) && fGeometry.contains(pt);
    }

    /**
     * Tests whether or not the interior of the area completely contains the
     * specified rectangular area.
     * @param x,&nbsp;y the coordinates of the upper left corner of the specified
     *		rectangular area
     * @param w the width of the specified rectangular area
     * @param h the height of the specified rectangular area
     * @return    <code>true</code> if the specified rectangular area lies completely 
     *		within the interior of the area; <code>false</code> otherwise.
     */
    public boolean contains(double x, double y, double w, double h) {
        TGRect rect = new TGRect(x, y, x + w, y + h);
        return (fGeometry != null) && fGeometry.contains(rect);
    }

    /**
     * Tests whether or not the interior of the area completely contains the
     * specified <code>Rectangle2D</code>.
     * @param     r  the <code>Rectangle2D</code> to test
     * @return    <code>true</code> if the <code>Rectangle2D</code> lies 
     *		completely within the interior of the area;
     *		<code>false</code> otherwise.
     */
    public boolean contains(Rectangle2D r) {
        TGRect rect = new TGRect(r);
        return (fGeometry != null) && fGeometry.contains(rect);
    }

/*    ________________________________________________________________________
 *    Constructive Area Geometry (CAG) functions
 */

    /**
     * Adds the shape of the specified <code>Area</code> to the current shape.
     * Addition is achieved through union.
     * @param     rhs  the <code>Area</code> to be added to the current shape
     */
    public void add(Area rhs) {
        MAreaGeometry srcGeometry = rhs.fGeometry;
        if (fGeometry == null)  // null + X == X
        {
            if (srcGeometry != null)
                fGeometry = srcGeometry;
        }
        else if (srcGeometry != null)    // X + null == X
            fGeometry = MAreaGeometry.add(fGeometry, srcGeometry);
    }

    /**
     * Subtracts the shape of the specified <code>Area</code> from the 
     * current shape.
     * @param     rhs  the <code>Area</code> to be subtracted from the 
     *		current shape
     */
    public void subtract(Area rhs) {
        if (fGeometry != null)  // null - X == null
        {
            MAreaGeometry srcGeometry = rhs.fGeometry;
            if (srcGeometry != null)     // null - null == null
                fGeometry = MAreaGeometry.subtract(fGeometry, srcGeometry);
        }
    }

    /**
     * Sets the shape of this <code>Area</code> to the intersection of 
     * the current shape and the shape of the specified <code>Area</code>.
     * @param     rhs  the <code>Area</code> to be intersected with this
     *		<code>Area</code>
     */
    public void intersect(Area rhs) {
        if (fGeometry != null)  // null * X == null
        {
            MAreaGeometry srcGeometry = rhs.fGeometry;
            if (srcGeometry == null)     // X * null == null
                fGeometry = srcGeometry;
            else
                fGeometry = MAreaGeometry.intersect(fGeometry, srcGeometry);
        }
    }

    /**
     * Sets the shape of this <code>Area</code> to be the combined area of the 
     * current shape and the shape of the specified <code>Area</code>, 
     * minus their intersection.
     * @param     rhs  the <code>Area</code> to be exclusive ORed with this 
     *		<code>Area</code>.
     */
    public void exclusiveOr(Area rhs) {
        MAreaGeometry srcGeometry = rhs.fGeometry;
        if (srcGeometry != null) {
            if (fGeometry == null)   // null ^ X == X
                fGeometry = srcGeometry;
            else
                fGeometry = MAreaGeometry.exclusiveOr(fGeometry, srcGeometry);
        }
        // else X ^ null == X
    }

/*    ________________________________________________________________________
 *    Compatability functions
 */

    /**
     * clone function ... to be compatible with Cloneable
     * @return    Created clone object
     */
    public Object clone() {
        return (Object)(new Area(fGeometry));
    }

    /**
     * Tests whether the two object's geometries are equal.
     * @param     rhs  the <code>Area</code> to be compared to this
     *		<code>Area</code>
     * @return    <code>true</code> if the two area geometries are equal;
     *		<code>false</code> otherwise.
     */
    public boolean equals(Area rhs) {
        MAreaGeometry that = rhs.fGeometry;
        return (fGeometry != null) &&
               (that != null) &&
               fGeometry.equals(that);
    }

    /**
     * Creates a {@link PathIterator} for the outline of this 
     * <code>Area</code> object.  This <code>Area</code> object is unchanged.
     * @param t an optional <code>AffineTransform</code> to be applied to the
     * coordinates as they are returned in the iteration, or <code>null</code>
     * if untransformed coordinates are desired
     * @return    the <code>PathIterator</code> object that returns the 
     *		geometry of the outline of this <code>Area</code>, one 
     *		segment at a time.
     */
    public PathIterator getPathIterator(AffineTransform t) {
	if (true) {
	    // As a temporary workaround to stability problems in the
	    // non-flattening path extraction, we will always return
	    // a polygonal answer here for now, until more extensive
	    // fixes can be developed.
	    // The non-flattening extraction was exhibiting long run
	    // times which in many cases appeared to be infinite loops
	    // (taking many minutes to complete even a conceptually
	    // simple operation) and frequently exceptions would be thrown
	    // as internal calculations would overflow temporary result
	    // arrays.  Attempts to install quick fixes for the problems
	    // led to much longer run times for simple cases and did not
	    // resolve the stability problems enough to justify such a
	    // performance loss.
	    return getPathIterator(t, 0.01);
	}
        if (t == null || t.isIdentity()) {
            return new AreaPathIterator(extract(Double.POSITIVE_INFINITY));
        }
        else {
            TGLoop areaPath =
		createTransformedArea(t).extract(Double.POSITIVE_INFINITY);
            return new AreaPathIterator(areaPath);
        }
    }

    /**
     * Creates a <code>PathIterator</code> for the flattened outline of 
     * this <code>Area</code> object.  Only uncurved path segments represented 
     * by the SEG_MOVETO, SEG_LINETO, and SEG_CLOSE point types are returned 
     * by the iterator.  This <code>Area</code> object is unchanged.
     * @param t an optional <code>AffineTransform</code> to be applied to the
     * coordinates as they are returned in the iteration, or <code>null</code>
     * if untransformed coordinates are desired
     * @param flatness the maximum amount that the control points
     * for a given curve can vary from colinear before a subdivided
     * curve is replaced by a straight line connecting the endpoints
     * @return    the <code>PathIterator</code> object that returns the 
     * geometry of the outline of this <code>Area</code>, one segment at a time.
     */
    public PathIterator getPathIterator(AffineTransform t, double f) {
        if (t == null || t.isIdentity()) {
            return new AreaPathIterator(extract(f));
        }
        else {
            return new AreaPathIterator(createTransformedArea(t).extract(f));
        }
    }

    /**
     * Creates a new <code>Area</code> that is the geometry of this 
     * <code>Area</code> and transforms the new <code>Area</code> with the 
     * specified <code>AffineTransform</code>.  This <code>Area</code> object 
     * is unchanged.
     * @param t  the specified <code>AffineTransform</code> used to transform 
     *		the new <code>Area</code>
     * @return    a new <code>Area</code> object representing the transformed 
     *			geometry.
     */
    public Area createTransformedArea(AffineTransform t) {
        Area result = new Area(this);
        result.transform(t);
        return result;
    }

/*    ________________________________________________________________________
 *    Private Internal things
 */

    Area(MAreaGeometry geometry) {
        fGeometry = geometry;
    }

    long getTimeStamp() {
        if (fGeometry == null)
            return (long)0;
        else
            return fGeometry.getTimeStamp();
    }

    /*
     * Why are we new'ing another one?
     * We don't want anyone from modifying our copy of the bounds information.
     */

    TGRect internalGetBounds() {
        if (fGeometry != null) {
            TGRect  retval = fGeometry.getBounds();
            return new TGRect (retval);
        }
        else
            // Why? Look at sun.awt.Albert.TGStatics.java
            return new TGRect (0.0, 0.0, 0.0, 0.0);
    }

    // the Area geometry data
    private MAreaGeometry fGeometry = null;

    // caching what was last extracted...
    private TGLoop fCachedPath = null;
    private double fCachedTimeStamp = 0;
    private boolean fCachedVertices = false;

    TGLoop extract(double epsilon) {

        TGLoop result = null;
        long stamp = getTimeStamp();

        if (fGeometry != null) {

            boolean isPolygonal =
                (epsilon != Double.POSITIVE_INFINITY) ||
                (fGeometry.isPolygonal());

            // cache hit??
            if (fCachedPath != null &&
                fCachedTimeStamp == stamp &&
                fCachedVertices == isPolygonal) {
                return fCachedPath;
            }

            if (isPolygonal) {
                result = new TGLoop();
                TOutlineMakerVertexEngine outliner =
                    new TOutlineMakerVertexEngine(result);
                TSamplingExtractor sampler =
                    new TSamplingExtractor(outliner, epsilon);

                TCAGRoot root = new TCAGRoot();
                fGeometry.extract(sampler, root);
                sampler.render(root);
            }
            else {
                TPathExtractor pather = new TPathExtractor();
                TCAGRoot root = new TCAGRoot();
                fGeometry.extract(pather, root);
                pather.render(root);
                result = pather.getPath();
            }
            // cache it...
            fCachedTimeStamp = stamp;
            fCachedVertices = isPolygonal;
            fCachedPath = result;
        }
        return result;
    }
}
