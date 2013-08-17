/*
 * @(#)BasicStroke.java	1.27 98/06/24
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

import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import sun.dc.path.FastPathProducer;
import sun.dc.path.PathConsumer;
import sun.dc.path.PathException;
import sun.dc.pr.PathStroker;
import sun.dc.pr.PathDasher;
import sun.dc.pr.Rasterizer;

/**
 * The <code>BasicStroke</code> class defines a basic set of rendering
 * attributes for the outlines of graphics primitives.
 * These attributes describe the shape of the mark made by a pen drawn along 
 * the outline of a {@link Shape} object and the decorations applied at
 * the ends and joins of path segments of the <code>Shape</code> object.
 * These attributes include:
 * <dl compact>
 * <dt><i>width</i>
 * <dd>The pen width, measured perpendicularly to the pen trajectory.
 * <dt><i>end caps</i>
 * <dd>The decoration applied to the ends of unclosed subpaths or dash
 * segments.
 * <dt><i>line joins</i>
 * <dd>The decoration applied where two path segments are joined.
 * <dt><i>dash attributes</i>
 * <dd>The definition of how to make a dash pattern by alternating
 * between opaque and transparent sections.
 * </dl>
 *
 * @version 10 Feb 1997
 * @author Jim Graham
 */
public class BasicStroke implements Stroke {

    /**
     * Joins path segments by extending their outside edges until
     * they meet.
     */
    public final static int JOIN_MITER = 0;

    /**
     * Joins path segments by rounding off the corner at a radius
     * of half the line width.
     */
    public final static int JOIN_ROUND = 1;

    /**
     * Joins path segments by connecting the outer corners of their
     * wide outlines with a straight segment.
     */
    public final static int JOIN_BEVEL = 2;

    /**
     * Ends unclosed subpaths and dash segments with no added
     * decoration.
     */
    public final static int CAP_BUTT = 0;

    /**
     * Ends unclosed subpaths and dash segments with a round
     * decoration that has a radius equal to half of the width
     * of the pen.
     */
    public final static int CAP_ROUND = 1;

    /**
     * Ends unclosed subpaths and dash segments with a square
     * projection that extends beyond the end of the segment
     * to a distance equal to half of the line width.
     */
    public final static int CAP_SQUARE = 2;

    float width;

    int join;
    int cap;
    float miterlimit;

    float dash[];
    float dash_phase;

    /**
     * Constructs a new <code>BasicStroke</code> with the specified
     * attributes.
     * @param width the width of the <code>BasicStroke</code>
     * @param cap the decoration of the ends of a <code>BasicStroke</code>
     * @param join the decoration applied where path segments meet
     * @param miterlimit the limit to trim the miter join
     * @param dash the array representing the dashing pattern
     * @param dash_phase the offset to start the dashing pattern
     */
    public BasicStroke(float width, int cap, int join, float miterlimit,
		       float dash[], float dash_phase) {
	if (width < 0.0f) {
	    throw new IllegalArgumentException("negative width");
	}
	if (cap != CAP_BUTT && cap != CAP_ROUND && cap != CAP_SQUARE) {
	    throw new IllegalArgumentException("illegal end cap value");
	}
	if (join == JOIN_MITER) {
	    if (miterlimit < 1.0f) {
		throw new IllegalArgumentException("miter limit < 1");
	    }
	} else if (join != JOIN_ROUND && join != JOIN_BEVEL) {
	    throw new IllegalArgumentException("illegal line join value");
	}
	if (dash != null) {
	    if (dash_phase < 0.0f) {
		throw new IllegalArgumentException("negative dash phase");
	    }
	    boolean allzero = true;
	    for (int i = 0; i < dash.length; i++) {
		float d = dash[i];
		if (d > 0.0) {
		    allzero = false;
		} else if (d < 0.0) {
		    throw new IllegalArgumentException("negative dash length");
		}
	    }
	    if (allzero) {
		throw new IllegalArgumentException("dash lengths all zero");
	    }
	}
	this.width	= width;
	this.cap	= cap;
	this.join	= join;
	this.miterlimit	= miterlimit;
        if (dash != null) {
            this.dash = (float []) dash.clone();
        }
	this.dash_phase	= dash_phase;
    }

    /**
     * Constructs a solid <code>BasicStroke</code> with the specified 
     * attributes.
     * @param width the width of the <code>BasicStroke</code>
     * @param cap the decoration of the ends of a <code>BasicStroke</code>
     * @param join the decoration applied where path segments meet
     * @param miterlimit the limit to trim the miter join
     */
    public BasicStroke(float width, int cap, int join, float miterlimit) {
	this(width, cap, join, miterlimit, null, 0.0f);
    }

    /**
     * Constructs a solid <code>BasicStroke</code> with the specified 
     * attributes.  The <code>miterlimit</code> parameter is 
     * unnecessary in cases where the default is allowable or the 
     * line joins are not specified as JOIN_MITER.
     * @param width the width of the <code>BasicStroke</code>
     * @param cap the decoration of the ends of a <code>BasicStroke</code>
     * @param join the decoration applied where path segments meet
     */
    public BasicStroke(float width, int cap, int join) {
	this(width, cap, join, 10.0f, null, 0.0f);
    }

    /**
     * Constructs a solid <code>BasicStroke</code> with the specified 
     * line width and with default values for the cap and join 
     * styles.
     * @param width the width of the <code>BasicStroke</code>
     */
    public BasicStroke(float width) {
	this(width, CAP_SQUARE, JOIN_MITER, 10.0f, null, 0.0f);
    }

    /**
     * Constructs a new <code>BasicStroke</code> with defaults for all 
     * attributes.
     * The default attributes are a solid line of width 1.0, CAP_SQUARE,
     * JOIN_MITER, a miter limit of 10.0.
     */
    public BasicStroke() {
	this(1.0f, CAP_SQUARE, JOIN_MITER, 10.0f, null, 0.0f);
    }


    /**
     * Returns a <code>Shape</code> whose interior defines the 
     * stroked outline of a specified <code>Shape</code>.
     * @param s the <code>Shape</code> boundary be stroked
     * @return the <code>Shape</code> of the stroked outline.
     */
    public Shape createStrokedShape(Shape s) {
	FillAdapter filler = new FillAdapter();
	PathStroker stroker = new PathStroker(filler);
	PathConsumer consumer;

	stroker.setPenDiameter(width);
	stroker.setPenT4(null);
	stroker.setCaps(RasterizerCaps[cap]);
	stroker.setCorners(RasterizerCorners[join], miterlimit);
	if (dash != null) {
	    PathDasher dasher = new PathDasher(stroker);
	    dasher.setDash(dash, dash_phase);
	    dasher.setDashT4(null);
	    consumer = dasher;
	} else {
	    consumer = stroker;
	}

	PathIterator pi = s.getPathIterator(null);

	try {
	    consumer.beginPath();
	    boolean pathClosed = false;
	    float mx = 0.0f;
	    float my = 0.0f;
	    float point[]  = new float[6];

	    while (!pi.isDone()) {
		int type = pi.currentSegment(point);
		if (pathClosed == true) {
		    pathClosed = false;
		    if (type != PathIterator.SEG_MOVETO) {
			// Force current point back to last moveto point
			consumer.beginSubpath(mx, my);
		    }
		}
		switch (type) {
		case PathIterator.SEG_MOVETO:
		    mx = point[0];
		    my = point[1];
		    consumer.beginSubpath(point[0], point[1]);
		    break;
		case PathIterator.SEG_LINETO:
		    consumer.appendLine(point[0], point[1]);
		    break;
		case PathIterator.SEG_QUADTO:
		    // Quadratic curves take two points
		    consumer.appendQuadratic(point[0], point[1],
					     point[2], point[3]);
		    break;
		case PathIterator.SEG_CUBICTO:
		    // Cubic curves take three points
		    consumer.appendCubic(point[0], point[1],
					 point[2], point[3],
					 point[4], point[5]);
		    break;
		case PathIterator.SEG_CLOSE:
		    consumer.closedSubpath();
		    pathClosed = true;
		    break;
		}
		pi.next();
	    }

	    consumer.endPath();
	} catch (PathException e) {
	    throw new InternalError("Unable to Stroke shape ("+
				    e.getMessage()+")");
	}

	return filler.getShape();
    }

    /**
     * Returns the line width.  Line width is represented in user space.
     * @return the line width of this <code>BasicStroke</code>.
     */
    public float getLineWidth() {
	return width;
    }

    /**
     * Returns the end cap style.
     * @return the end cap style of this <code>BasicStroke</code> as one
     * of the static <code>int</code> values that define possible end cap
     * styles.
     */
    public int getEndCap() {
	return cap;
    }

    /**
     * Returns the line join style.
     * @return the line join style of the <code>BasicStroke</code> as one
     * of the static <code>int</code> values that define possible line
     * join styles.
     */
    public int getLineJoin() {
	return join;
    }

    /**
     * Returns the limit of miter joins.
     * @return the limit of miter joins of the <code>BasicStroke</code>.
     */
    public float getMiterLimit() {
	return miterlimit;
    }

    /**
     * Returns the array representing the lengths of the dash segments.
     * Alternate entries in the array represent the user space lengths
     * of the opaque and transparent segments of the dashes.
     * As the pen moves along the outline of the <code>Shape</code>
     * to be stroked, the user space
     * distance that the pen travels is accumulated.  The distance
     * value is used to index into the dash array.
     * The pen is opaque when its current cumulative distance maps
     * to an even element of the dash array and transparent otherwise.
     * @return the dash array.
     */
    public float[] getDashArray() {
        if (dash == null) {
            return null;
        }

        return (float[]) dash.clone();
    }

    /**
     * Returns the current dash phase.
     * The dash phase is a distance specified in user coordinates that 
     * represents an offset into the dashing pattern. In other words, the dash 
     * phase defines the point in the dashing pattern that will correspond to 
     * the beginning of the stroke.
     * @return the dash phase as a <code>float</code> value.
     */
    public float getDashPhase() {
	return dash_phase;
    }

    /**
     * Returns the hashcode for this stroke.
     * @return      a hash code for this stroke.
     */
    public int hashCode() {
	int hash = Float.floatToIntBits(width);
	hash = hash * 31 + join;
	hash = hash * 31 + cap;
	hash = hash * 31 + Float.floatToIntBits(miterlimit);
	if (dash != null) {
	    hash = hash * 31 + Float.floatToIntBits(dash_phase);
	    for (int i = 0; i < dash.length; i++) {
		hash = hash * 31 + Float.floatToIntBits(dash[i]);
	    }
	}
	return hash;
    }

    /**
     * Returns true if this BasicStroke represents the same
     * stroking operation as the given argument.
     */
   /**
    * Tests if a specified object is equal to this <code>BasicStroke</code>
    * by first testing if it is a <code>BasicStroke</code> and then comparing 
    * its width, join, cap, miter limit, dash, and dash phase attributes with 
    * those of this <code>BasicStroke</code>.
    * @param  obj the specified object to compare to this 
    *              <code>BasicStroke</code>
    * @return <code>true</code> if the width, join, cap, miter limit, dash, and
    *            dash phase are the same for both objects;
    *            <code>false</code> otherwise.
    */
    public boolean equals(Object obj) {
        if (!(obj instanceof BasicStroke)) {
            return false;
        }

        BasicStroke bs = (BasicStroke) obj;
        if (width != bs.width) {
            return false;
        }

        if (join != bs.join) {
            return false;
        }

        if (cap != bs.cap) {
            return false;
        }

        if (miterlimit != bs.miterlimit) {
            return false;
        }

        if (dash != null) {
	    if (dash_phase != bs.dash_phase) {
		return false;
	    }

	    if (!java.util.Arrays.equals(dash, bs.dash)) {
		return false;
	    }
        }
        else if (bs.dash != null) {
            return false;
        }

        return true;
    }

    private static final int RasterizerCaps[] = {
	Rasterizer.BUTT, Rasterizer.ROUND, Rasterizer.SQUARE
    };

    private static final int RasterizerCorners[] = {
	Rasterizer.MITER, Rasterizer.ROUND, Rasterizer.BEVEL
    };

    private class FillAdapter implements PathConsumer {
	boolean closed;
	GeneralPath path;

	public FillAdapter() {
	    path = new GeneralPath(GeneralPath.WIND_NON_ZERO);
	}

	public Shape getShape() {
	    return path;
	}

	public void beginPath() {}

	public void beginSubpath(float x0, float y0) {
	    if (closed) {
		path.closePath();
		closed = false;
	    }
	    path.moveTo(x0, y0);
	}

	public void appendLine(float x1, float y1) {
	    path.lineTo(x1, y1);
	}

	public void appendQuadratic(float xm, float ym, float x1, float y1) {
	    path.quadTo(xm, ym, x1, y1);
	}

	public void appendCubic(float xm, float ym,
				float xn, float yn,
				float x1, float y1) {
	    path.curveTo(xm, ym, xn, yn, x1, y1);
	}

	public void closedSubpath() {
	    closed = true;
	}

	public void endPath() {
	    if (closed) {
		path.closePath();
		closed = false;
	    }
	}

	public void useProxy(FastPathProducer proxy)
	    throws PathException
	{
	    proxy.sendTo(this);
	}

	public long getCPathConsumer() {
	    return 0;
	}
    }
}
