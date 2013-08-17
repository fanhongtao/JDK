/*
 * @(#)Graphics.java	1.38 97/01/21
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

import java.io.*;
import java.lang.*;
import java.util.*;
import java.awt.image.ImageObserver;

/**
 * Graphics is the abstract base class for all graphics contexts
 * which allow an application to draw onto components realized on
 * various devices or onto off-screen images.
 * A Graphics object encapsulates the state information needed
 * for the various rendering operations that Java supports.  This
 * state information includes:
 * <ul>
 * <li>The Component to draw on
 * <li>A translation origin for rendering and clipping coordinates
 * <li>The current clip
 * <li>The current color
 * <li>The current font
 * <li>The current logical pixel operation function (XOR or Paint)
 * <li>The current XOR alternation color
 *     (see <a href=#setXORMode>setXORMode</a>)
 * </ul>
 * <p>
 * Coordinates are infinitely thin and lie between the pixels of the
 * output device.
 * Operations which draw the outline of a figure operate by traversing
 * along the infinitely thin path with a pixel-sized pen that hangs
 * down and to the right of the anchor point on the path.
 * Operations which fill a figure operate by filling the interior
 * of the infinitely thin path.
 * Operations which render horizontal text render the ascending
 * portion of the characters entirely above the baseline coordinate.
 * <p>
 * Some important points to consider are that drawing a figure that
 * covers a given rectangle will occupy one extra row of pixels on
 * the right and bottom edges compared to filling a figure that is
 * bounded by that same rectangle.
 * Also, drawing a horizontal line along the same y coordinate as
 * the baseline of a line of text will draw the line entirely below
 * the text except for any descenders.
 * Both of these properties are due to the pen hanging down and to
 * the right from the path that it traverses.
 * <p>
 * All coordinates which appear as arguments to the methods of this
 * Graphics object are considered relative to the translation origin
 * of this Graphics object prior to the invocation of the method.
 * All rendering operations modify only pixels which lie within the
 * area bounded by both the current clip of the graphics context
 * and the extents of the Component used to create the Graphics object.
 * 
 * @version 	1.38, 01/21/97
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 */
public abstract class Graphics {

    /**
     * Constructs a new Graphics object.  Since Graphics is an abstract
     * class, and since it must be customized by subclasses for different
     * output devices, Graphics objects cannot be created directly.
     * Instead, Graphics objects must be obtained from another Graphics
     * object or created by a Component.
     * @see Component#getGraphics
     * @see #create
     */
    protected Graphics() {
    }

    /**
     * Creates a new Graphics object that is a copy of this Graphics object.
     */
    public abstract Graphics create();

    /**
     * Creates a new Graphics object based on this Graphics object,
     * but with a new translation and clip area.
     * The new Graphics object will have its origin translated to the
     * specified coordinate (x, y) and will also have its current clip
     * intersected with the specified rectangle.  The arguments are all
     * interpreted in the coordinate system of the original Graphics
     * object.
     * @param x the x coordinate of the new translation origin
     * and rectangle to intersect the clip with
     * @param y the y coordinate of the new translation origin
     * and rectangle to intersect the clip with
     * @param width the width of the rectangle to intersect the clip with
     * @param height the height of the rectangle to intersect the clip with
     * @see #translate
     * @see #clipRect
     */
    public Graphics create(int x, int y, int width, int height) {
	Graphics g = create();
	g.translate(x, y);
	g.clipRect(0, 0, width, height);
	return g;
    }

    /**
     * Translates the origin of the graphics context to the point
     * (x, y) in the current coordinate system.  All coordinates
     * used in subsequent rendering operations on this graphics
     * context will be relative to this new origin.
     * @param x the x coordinate of the new translation origin
     * @param y the y coordinate of the new translation origin
     */
    public abstract void translate(int x, int y);

    /**
     * Gets the current color.
     * @see Color
     * @see #setColor
     */
    public abstract Color getColor();

    /**
     * Sets the current color to the specified color.  All subsequent
     * rendering operations will use this specified color.
     * @param c the new rendering color
     * @see Color
     * @see #getColor
     */
    public abstract void setColor(Color c);

    /**
     * Sets the logical pixel operation function to the Paint, or
     * overwrite mode.  All subsequent rendering operations will
     * overwrite the destination with the current color. 
     */
    public abstract void setPaintMode();

    /**
     * Sets the logical pixel operation function to the XOR mode,
     * which alternates pixels between the current color and a new
     * specified XOR alternation color.
     * When subsequent rendering operations are performed on top of
     * pixels which are the specified XOR alternation color, they
     * will be changed to the current color and vice versa.
     * Drawing an image on a region of pixels of the specified XOR
     * alternation color will also change those pixels to the colors
     * in the image.
     * Drawing on pixels of other colors will not necessarily result
     * in a predictable final color but all rendering operations will
     * always be reversible in this mode; if you draw the same figure
     * twice then all pixels will be restored to their original values.
     * @param c1 the XOR alternation color
     */
    public abstract void setXORMode(Color c1);

    /**
     * Gets the current font.
     * @see Font
     * @see #setFont
     */
    public abstract Font getFont();

    /**
     * Sets the font for all subsequent text rendering operations.
     * @param font the specified font
     * @see Font
     * @see #getFont
     * @see #drawString
     * @see #drawBytes
     * @see #drawChars
    */
    public abstract void setFont(Font font);

    /**
     * Gets the font metrics of the current font.
     * @see #getFont
     */
    public FontMetrics getFontMetrics() {
	return getFontMetrics(getFont());
    }

    /**
     * Gets the font metrics for the specified font.
     * @param f the specified font
     * @see #getFont
     * @see #getFontMetrics
     */
    public abstract FontMetrics getFontMetrics(Font f);


    /**
     * Returns the bounding rectangle of the current clipping area.
     * The coordinates in the rectangle are relative to the coordinate
     * system origin of this graphics context.
     * @see #getClip
     * @see #clipRect
     * @see #setClip(int, int, int, int)
     * @see #setClip(Shape)
     */
    public abstract Rectangle getClipBounds();

    /** 
     * Intersects the current clip with the specified rectangle.
     * The resulting clipping area is the intersection of the current
     * clipping area and the specified rectangle.
     * This method can only be used to make the current clip smaller.
     * To set the current clip larger, use any of the setClip methods.
     * Rendering operations have no effect outside of the clipping area.
     * @param x the x coordinate of the rectangle to intersect the clip with
     * @param y the y coordinate of the rectangle to intersect the clip with
     * @param width the width of the rectangle to intersect the clip with
     * @param height the height of the rectangle to intersect the clip with
     * @see #setClip(int, int, int, int)
     * @see #setClip(Shape)
     */
    public abstract void clipRect(int x, int y, int width, int height);

    /**
     * Sets the current clip to the rectangle specified by the given
     * coordinates.
     * Rendering operations have no effect outside of the clipping area.
     * @param x the x coordinate of the new clip rectangle
     * @param y the y coordinate of the new clip rectangle
     * @param width the width of the new clip rectangle
     * @param height the height of the new clip rectangle
     * @see #clipRect
     * @see #setClip(Shape)
     */
    public abstract void setClip(int x, int y, int width, int height);

    /**
     * Return a Shape object representing the current clipping area.
     * @see #getClipBounds
     * @see #clipRect
     * @see #setClip(int, int, int, int)
     * @see #setClip(Shape)
     */
    public abstract Shape getClip();

    /**
     * Set the current clipping area to an arbitrary clip shape.
     * Not all objects which implement the Shape interface can be
     * used to set the clip.  The only Shape objects which are
     * guaranteed to be supported are Shape objects which are
     * obtained from the getClip() method and Rectangle objects.
     * @see #getClip()
     * @see #clipRect
     * @see #setClip(int, int, int, int)
     */
    public abstract void setClip(Shape clip);

    /**
     * Copies an area of the Component by a distance specified by dx
     * and dy to the right and down.  To copy a portion of the Component
     * to the left or upwards, specify a negative distance for either
     * dx or dy.
     * If a portion of the source rectangle to be copied lies outside of
     * the bounds of the Component or is obscured by another Component
     * or window, the damage resulting from not being able to copy the
     * associated pixels will be repaired by a call to the paint method
     * of the Component.
     * @param x the x coordinate of the source rectangle
     * @param y the y coordinate of the source rectangle
     * @param width the width of the source rectangle
     * @param height the height of the source rectangle
     * @param dx the horizontal distance to copy the pixels to the right
     * @param dy the vertical distance to copy the pixels downward
     */
    public abstract void copyArea(int x, int y, int width, int height,
				  int dx, int dy);

    /** 
     * Draws a line between the coordinates (x1,y1) and (x2,y2) using
     * the current color.
     * @param x1 the x coordinate of the start of the line
     * @param y1 the y coordinate of the start of the line
     * @param x2 the x coordinate of the end of the line
     * @param y2 the y coordinate of the end of the line
     */
    public abstract void drawLine(int x1, int y1, int x2, int y2);

    /** 
     * Fills the specified rectangle with the current color. 
     * @param x the x coordinate of the rectangle to be filled
     * @param y the y coordinate of the rectangle to be filled
     * @param width the width of the rectangle to be filled
     * @param height the height of the rectangle to be filled
     * @see #drawRect
     * @see #clearRect
     */
    public abstract void fillRect(int x, int y, int width, int height);

    /** 
     * Draws the outline of the specified rectangle using the current
     * color.  The resulting rectangle will cover an area (width + 1)
     * pixels wide by (height + 1) pixels tall.
     * @param x the x coordinate of the rectangle to be drawn
     * @param y the y coordinate of the rectangle to be drawn
     * @param width the width of the rectangle to be drawn
     * @param height the height of the rectangle to be drawn
     * @see #fillRect
     * @see #clearRect
     */
    public void drawRect(int x, int y, int width, int height) {
	if ((width < 0) || (height < 0)) {
	    return;
	}

	if (height == 0 || width == 0) {
	    drawLine(x, y, x + width, y + height);
	} else {
	    drawLine(x, y, x + width - 1, y);
	    drawLine(x + width, y, x + width, y + height - 1);
	    drawLine(x + width, y + height, x + 1, y + height);
	    drawLine(x, y + height, x, y + 1);
	}
    }
    
    /** 
     * Clears the specified rectangle by filling it with the background
     * color of the current drawing surface.
     * @param x the x coordinate of the rectangle to clear
     * @param y the y coordinate of the rectangle to clear
     * @param width the width of the rectangle to clear
     * @param height the height of the rectangle to clear
     * @see #fillRect
     * @see #drawRect
     */
    public abstract void clearRect(int x, int y, int width, int height);

    /** 
     * Draws the outline of the specified rounded corner rectangle
     * using the current color.
     * The resulting rectangle will cover an area (width + 1) pixels wide
     * by (height + 1) pixels tall.
     * @param x the x coordinate of the rectangle to be drawn
     * @param y the y coordinate of the rectangle to be drawn
     * @param width the width of the rectangle to be drawn
     * @param height the height of the rectangle to be drawn
     * @param arcWidth the horizontal diameter of the arc at the four corners
     * @param arcHeight the vertical diameter of the arc at the four corners
     * @see #fillRoundRect
     */
    public abstract void drawRoundRect(int x, int y, int width, int height,
				       int arcWidth, int arcHeight);

    /** 
     * Fills the specified rounded corner rectangle with the current color.
     * @param x the x coordinate of the rectangle to be filled
     * @param y the y coordinate of the rectangle to be filled
     * @param width the width of the rectangle to be filled
     * @param height the height of the rectangle to be filled
     * @param arcWidth the horizontal diameter of the arc at the four corners
     * @param arcHeight the vertical diameter of the arc at the four corners
     * @see #drawRoundRect
     */
    public abstract void fillRoundRect(int x, int y, int width, int height,
				       int arcWidth, int arcHeight);

    /**
     * Draws a 3-D highlighted outline of the specified rectangle.
     * The edges of the rectangle will be highlighted so that it appears
     * as if the edges were beveled and lit from the upper left corner.
     * The colors used for the highlighting effect will be determined from
     * the current color.
     * The resulting rectangle will cover an area (width + 1) pixels wide
     * by (height + 1) pixels tall.
     * @param x the x coordinate of the rectangle to be drawn
     * @param y the y coordinate of the rectangle to be drawn
     * @param width the width of the rectangle to be drawn
     * @param height the height of the rectangle to be drawn
     * @param raised a boolean that determines whether the rectangle
     * appears to be raised above the surface or sunk into the surface
     * @see Color#brighter
     * @see Color#darker
     */
    public void draw3DRect(int x, int y, int width, int height,
			   boolean raised) {
	Color c = getColor();
	Color brighter = c.brighter();
	Color darker = c.darker();

	setColor(raised ? brighter : darker);
	drawLine(x, y, x, y + height);
	drawLine(x + 1, y, x + width - 1, y);
	setColor(raised ? darker : brighter);
	drawLine(x + 1, y + height, x + width, y + height);
	drawLine(x + width, y, x + width, y + height - 1);
	setColor(c);
    }    

    /**
     * Paints a 3-D highlighted rectangle filled with the current color.
     * The edges of the rectangle will be highlighted so that it appears
     * as if the edges were beveled and lit from the upper left corner.
     * The colors used for the highlighting effect will be determined from
     * the current color.
     * @param x the x coordinate of the rectangle to be filled
     * @param y the y coordinate of the rectangle to be filled
     * @param width the width of the rectangle to be filled
     * @param height the height of the rectangle to be filled
     * @param raised a boolean that determines whether the rectangle
     * appears to be raised above the surface or sunk into the surface
     * @see Color#brighter
     * @see Color#darker
     */
    public void fill3DRect(int x, int y, int width, int height,
			   boolean raised) {
	Color c = getColor();
	Color brighter = c.brighter();
	Color darker = c.darker();

	if (!raised) {
	    setColor(darker);
	}
	fillRect(x+1, y+1, width-2, height-2);
	setColor(raised ? brighter : darker);
	drawLine(x, y, x, y + height - 1);
	drawLine(x + 1, y, x + width - 2, y);
	setColor(raised ? darker : brighter);
	drawLine(x + 1, y + height - 1, x + width - 1, y + height - 1);
	drawLine(x + width - 1, y, x + width - 1, y + height - 2);
	setColor(c);
    }    

    /** 
     * Draws the outline of an oval covering the specified rectangle
     * using the current color.
     * The resulting oval will cover an area (width + 1) pixels wide
     * by (height + 1) pixels tall.
     * @param x the x coordinate of the upper left corner of the oval
     * to be drawn
     * @param y the y coordinate of the upper left corner of the oval
     * to be drawn
     * @param width the width of the oval to be drawn
     * @param height the height of the oval to be drawn
     * @see #fillOval
     */
    public abstract void drawOval(int x, int y, int width, int height);

    /** 
     * Fills an oval bounded by the specified rectangle with the
     * current color.
     * @param x the x coordinate of the upper left corner of the oval
     * to be filled
     * @param y the y coordinate of the upper left corner of the oval
     * to be filled
     * @param width the width of the oval to be filled
     * @param height the height of the oval to be filled
     * @see #drawOval
     */
    public abstract void fillOval(int x, int y, int width, int height);

    /**
     * Draws the outline of an arc covering the specified rectangle,
     * starting at startAngle and extending for arcAngle degrees,
     * using the current color.
     * The angles are interpreted such that 0 degrees is at the 3-o'clock
     * position, and positive values indicate counter-clockwise rotations
     * while negative values indicate clockwise rotations.
     * The resulting arc will cover an area (width + 1) pixels wide
     * by (height + 1) pixels tall.
     * @param x the x coordinate of the upper left corner of the arc
     * to be drawn
     * @param y the y coordinate of the upper left corner of the arc
     * to be drawn
     * @param width the width of the arc to be drawn
     * @param height the height of the arc to be drawn
     * @param startAngle the beginning angle
     * @param arcAngle the angular extent of the arc (relative to startAngle)
     * @see #fillArc
     */
    public abstract void drawArc(int x, int y, int width, int height,
				 int startAngle, int arcAngle);

    /** 
     * Fills an arc bounded by the specified rectangle, starting at
     * startAngle and extending for arcAngle degrees, with the
     * current color.
     * This method generates a pie shape.
     * The angles are interpreted such that 0 degrees is at the 3-o'clock
     * position, and positive values indicate counter-clockwise rotations
     * while negative values indicate clockwise rotations.
     * @param x the x coordinate of the upper left corner of the arc
     * to be filled
     * @param y the y coordinate of the upper left corner of the arc
     * to be filled
     * @param width the width of the arc to be filled
     * @param height the height of the arc to be filled
     * @param startAngle the beginning angle
     * @param arcAngle the angular extent of the arc (relative to startAngle).
     * @see #drawArc
     */
    public abstract void fillArc(int x, int y, int width, int height,
				 int startAngle, int arcAngle);

    /** 
     * Draws a sequence of connected lines defined by arrays of x coordinates
     * and y coordinates using the current color.
     * The figure is not automatically closed if the first coordinate is
     * different from the last coordinate.
     * @param xPoints an array of x points
     * @param yPoints an array of y points
     * @param nPoints the total number of points
     * @see #drawPolygon
     */
    public abstract void drawPolyline(int xPoints[], int yPoints[],
				      int nPoints);

    /** 
     * Draws the outline of a polygon defined by arrays of x coordinates
     * and y coordinates using the current color.
     * The figure is automatically closed by drawing a line connecting
     * the first coordinate to the last if they are different.
     * @param xPoints an array of x points
     * @param yPoints an array of y points
     * @param nPoints the total number of points
     * @see #drawPolyline
     * @see #fillPolygon
     */
    public abstract void drawPolygon(int xPoints[], int yPoints[],
				     int nPoints);

    /** 
     * Draws the outline of a polygon defined by the specified Polygon
     * object using the current color.
     * @param p the Polygon object to outline
     * @see #fillPolygon
     */
    public void drawPolygon(Polygon p) {
	drawPolygon(p.xpoints, p.ypoints, p.npoints);
    }

    /** 
     * Fills a polygon defined by arrays of x coordinates and y
     * coordinates with the current color using an even-odd fill
     * rule (otherwise known as an alternating rule).
     * @param xPoints an array of x points
     * @param yPoints an array of y points
     * @param nPoints the total number of points
     * @see #drawPolygon
     */
    public abstract void fillPolygon(int xPoints[], int yPoints[],
				     int nPoints);

    /** 
     * Fills the polygon defined by the specified Polygon object with
     * the current color using an even-odd fill rule (otherwise known
     * as an alternating rule).
     * @param p the Polygon object to fill
     * @see #drawPolygon
     */
    public void fillPolygon(Polygon p) {
	fillPolygon(p.xpoints, p.ypoints, p.npoints);
    }

    /** 
     * Draws the specified String using the current font and color.
     * The x,y position is the starting point of the baseline of the String.
     * @param str the String to be drawn
     * @param x the x coordinate of the baseline of the text
     * @param y the y coordinate of the baseline of the text
     * @see #drawChars
     * @see #drawBytes
     */
    public abstract void drawString(String str, int x, int y);

    /** 
     * Draws the specified characters using the current font and color.
     * @param data the array of characters to be drawn
     * @param offset the start offset in the data
     * @param length the number of characters to be drawn
     * @param x the x coordinate of the baseline of the text
     * @param y the y coordinate of the baseline of the text
     * @see #drawString
     * @see #drawBytes
     */
    public void drawChars(char data[], int offset, int length, int x, int y) {
	drawString(new String(data, offset, length), x, y);
    }

    /** 
     * Draws the specified bytes using the current font and color.
     * @param data the data to be drawn
     * @param offset the start offset in the data
     * @param length the number of bytes that are drawn
     * @param x the x coordinate of the baseline of the text
     * @param y the y coordinate of the baseline of the text
     * @see #drawString
     * @see #drawChars
     */
    public void drawBytes(byte data[], int offset, int length, int x, int y) {
	drawString(new String(data, 0, offset, length), x, y);
    }

    /** 
     * Draws as much of the specified image as is currently available at
     * the specified coordinate (x, y).
     * This method will return immediately in all cases, even if the
     * entire image has not yet been scaled, dithered and converted
     * for the current output device.
     * If the current output representation is not yet complete then
     * the method will return false and the indicated ImageObserver
     * object will be notified as the conversion process progresses.
     * @param img the specified image to be drawn
     * @param x the x coordinate
     * @param y the y coordinate
     * @param observer object to be notified as more of the image is
     * converted
     * @see Image
     * @see ImageObserver
     */
    public abstract boolean drawImage(Image img, int x, int y, 
				      ImageObserver observer);

    /**
     * Draws as much of the specified image as has already been scaled
     * to fit inside the specified rectangle.
     * This method will return immediately in all cases, even if the
     * entire image has not yet been scaled, dithered and converted
     * for the current output device.
     * If the current output representation is not yet complete then
     * the method will return false and the indicated ImageObserver
     * object will be notified as the conversion process progresses.
     * <p>
     * Note that a scaled version of an image will not necessarily be
     * immediately available just because an unscaled version of the
     * image has been constructed for this output device.  Each size of
     * the image may be cached separately and generated from the original
     * data in a separate image production sequence.
     * @param img the specified image to be drawn
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param observer object to be notified as more of the image is
     * scaled and converted
     * @see Image
     * @see ImageObserver
     */
    public abstract boolean drawImage(Image img, int x, int y,
				      int width, int height, 
				      ImageObserver observer);
    
    /** 
     * Draws as much of the specified image as is currently available at
     * the specified coordinate (x, y) with the given solid background color.
     * This operation should be equivalent to filling a rectangle of the
     * width and height of the specified image with the given color and then
     * drawing the image on top of it, but possibly more efficient.
     * This method will return immediately in all cases, even if the
     * entire image has not yet been scaled, dithered and converted
     * for the current output device.
     * If the current output representation is not yet complete then
     * the method will return false and the indicated ImageObserver
     * object will be notified as the conversion process progresses.
     * @param img the specified image to be drawn
     * @param x the x coordinate
     * @param y the y coordinate
     * @param bgcolor the background color to paint under the
     * non-opaque portions of the image
     * @param observer object to be notified as more of the image is
     * converted
     * @see Image
     * @see ImageObserver
     */
    public abstract boolean drawImage(Image img, int x, int y, 
				      Color bgcolor,
				      ImageObserver observer);

    /**
     * Draws as much of the specified image as has already been scaled
     * to fit inside the specified rectangle with the given solid
     * background color.
     * This operation should be equivalent to filling a rectangle of the
     * specified width and height with the given color and then
     * drawing the image on top of it, but possibly more efficient.
     * This method will return immediately in all cases, even if the
     * entire image has not yet been scaled, dithered and converted
     * for the current output device.
     * If the current output representation is not yet complete then
     * the method will return false and the indicated ImageObserver
     * object will be notified as the conversion process progresses.
     * <p>
     * Note that a scaled version of an image will not necessarily be
     * immediately available just because an unscaled version of the
     * image has been constructed for this output device.  Each size of
     * the image may be cached separately and generated from the original
     * data in a separate image production sequence.
     * @param img the specified image to be drawn
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param bgcolor the background color to paint under the
     * non-opaque portions of the image
     * @param observer object to be notified as more of the image is
     * scaled and converted
     * @see Image
     * @see ImageObserver
     */
    public abstract boolean drawImage(Image img, int x, int y,
				      int width, int height, 
				      Color bgcolor,
				      ImageObserver observer);
    
    /**
     * Draws as much of the specified area of the specified image as is
     * currently available, scaling it on the fly to fit inside the
     * specified area of the destination drawable surface.
     * This method will return immediately in all cases, even if the
     * entire area of the specified image has not yet been scaled,
     * dithered and converted for the current output device.
     * If the current output representation is not yet complete then
     * the method will return false and the indicated ImageObserver
     * object will be notified as the conversion process progresses.
     * <p>
     * This method will always use the unscaled version of the image
     * to render the scaled rectangle and will perform the required
     * scaling on the fly.  It will not use a cached scaled version
     * of the image for this operation.  The scaling from source
     * to destination is performed such that the first coordinate
     * of the source rectangle is mapped to the first coordinate of
     * the destination rectangle and the second source coordinate is
     * mapped to the second destination coordinate.  The subimage is
     * scaled and flipped as needed to preserve those mappings.
     * @param img the specified image to be drawn
     * @param dx1 the x coordinate of the first corner of the
     * destination rectangle
     * @param dy1 the y coordinate of the first corner of the
     * destination rectangle
     * @param dx2 the x coordinate of the second corner of the
     * destination rectangle
     * @param dy2 the y coordinate of the second corner of the
     * destination rectangle
     * @param sx1 the x coordinate of the first corner of the
     * source rectangle
     * @param sy1 the y coordinate of the first corner of the
     * source rectangle
     * @param sx2 the x coordinate of the second corner of the
     * source rectangle
     * @param sy2 the y coordinate of the second corner of the
     * source rectangle
     * @param observer object to be notified as more of the image is
     * scaled and converted
     * @see Image
     * @see ImageObserver
     */
    public abstract boolean drawImage(Image img,
				      int dx1, int dy1, int dx2, int dy2,
				      int sx1, int sy1, int sx2, int sy2,
				      ImageObserver observer);

    /**
     * Draws as much of the specified area of the specified image as is
     * currently available, scaling it on the fly to fit inside the
     * specified area of the destination drawable surface with the
     * given solid background color.
     * This operation should be equivalent to filling a rectangle of the
     * specified width and height with the given color and then
     * drawing the image on top of it, but possibly more efficient.
     * This method will return immediately in all cases, even if the
     * entire area of the specified image has not yet been scaled,
     * dithered and converted for the current output device.
     * If the current output representation is not yet complete then
     * the method will return false and the indicated ImageObserver
     * object will be notified as the conversion process progresses.
     * <p>
     * This method will always use the unscaled version of the image
     * to render the scaled rectangle and will perform the required
     * scaling on the fly.  It will not use a cached scaled version
     * of the image for this operation.  The scaling from source
     * to destination is performed such that the first coordinate
     * of the source rectangle is mapped to the first coordinate of
     * the destination rectangle and the second source coordinate is
     * mapped to the second destination coordinate.  The subimage is
     * scaled and flipped as needed to preserve those mappings.
     * @param img the specified image to be drawn
     * @param dx1 the x coordinate of the first corner of the
     * destination rectangle
     * @param dy1 the y coordinate of the first corner of the
     * destination rectangle
     * @param dx2 the x coordinate of the second corner of the
     * destination rectangle
     * @param dy2 the y coordinate of the second corner of the
     * destination rectangle
     * @param sx1 the x coordinate of the first corner of the
     * source rectangle
     * @param sy1 the y coordinate of the first corner of the
     * source rectangle
     * @param sx2 the x coordinate of the second corner of the
     * source rectangle
     * @param sy2 the y coordinate of the second corner of the
     * source rectangle
     * @param bgcolor the background color to paint under the
     * non-opaque portions of the image
     * @param observer object to be notified as more of the image is
     * scaled and converted
     * @see Image
     * @see ImageObserver
     */
    public abstract boolean drawImage(Image img,
				      int dx1, int dy1, int dx2, int dy2,
				      int sx1, int sy1, int sx2, int sy2,
				      Color bgcolor,
				      ImageObserver observer);

    /**
     * Dispose of the system resources used by this graphics context.
     * The Graphics context cannot be used after being disposed of.
     * While the finalization process of the garbage collector will
     * also dispose of the same system resources, due to the number
     * of Graphics objects that can be created in short time frames
     * it is preferable to manually free the associated resources
     * using this method rather than to rely on a finalization
     * process which may not happen for a long period of time.
     * <p>
     * Graphics objects which are provided as arguments to the paint
     * and update methods of Components are automatically disposed
     * by the system when those methods return.  Programmers should,
     * for efficiency, call the dispose method when finished using
     * a Graphics object only if it was created directly from a
     * Component or another Graphics object.
     * @see #finalize
     * @see Component#paint
     * @see Component#update
     * @see Component#getGraphics
     * @see #create
     */
    public abstract void dispose();

    /**
     * Disposes of this graphics context once it is no longer referenced.
     * @see #dispose
     */
    public void finalize() {
	dispose();
    }

    /**
     * Returns a String object representing this Graphics object's value.
     */
    public String toString() {	
	return getClass().getName() + "[font=" + getFont() + ",color=" + getColor() + "]";
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by getClipBounds().
     */
    public Rectangle getClipRect() {
	return getClipBounds();
    }
}
