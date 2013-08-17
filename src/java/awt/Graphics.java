/*
 * @(#)Graphics.java	1.43 98/08/19
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

import java.io.*;
import java.lang.*;
import java.util.*;
import java.awt.image.ImageObserver;

/**
 * The <code>Graphics</code> class is the abstract base class for 
 * all graphics contexts that allow an application to draw onto 
 * components that are realized on various devices, as well as 
 * onto off-screen images.
 * <p>
 * A <code>Graphics</code> object encapsulates state information needed
 * for the basic rendering operations that Java supports.  This
 * state information includes the following properties:
 * <p>
 * <ul>
 * <li>The <code>Component</code> object on which to draw.
 * <li>A translation origin for rendering and clipping coordinates.
 * <li>The current clip.
 * <li>The current color.
 * <li>The current font.
 * <li>The current logical pixel operation function (XOR or Paint).
 * <li>The current XOR alternation color 
 *     (see <a href="#setXORMode"><code>setXORMode</code></a>).
 * </ul>
 * <p>
 * Coordinates are infinitely thin and lie between the pixels of the
 * output device.
 * Operations which draw the outline of a figure operate by traversing
 * an infinitely thin path between pixels with a pixel-sized pen that hangs
 * down and to the right of the anchor point on the path.
 * Operations which fill a figure operate by filling the interior
 * of that infinitely thin path.
 * Operations which render horizontal text render the ascending
 * portion of character glyphs entirely above the baseline coordinate.
 * <p>
 * The graphics pen hangs down and to the right from the path it traverses. 
 * This has the following implications:
 * <p><ul>
 * <li>If you draw a figure that covers a given rectangle, that 
 * figure occupies one extra row of pixels on the right and bottom edges 
 * as compared to filling a figure that is bounded by that same rectangle.
 * <li>If you draw a horizontal line along the same <i>y</i> coordinate as
 * the baseline of a line of text, that line is drawn entirely below
 * the text, except for any descenders.
 * </ul><p>
 * All coordinates which appear as arguments to the methods of this
 * <code>Graphics</code> object are considered relative to the 
 * translation origin of this <code>Graphics</code> object prior to 
 * the invocation of the method.
 * All rendering operations modify only pixels which lie within the
 * area bounded by both the current clip of the graphics context
 * and the extents of the component used to create the 
 * <code>Graphics</code> object.
 * All drawing or writing is done in the current color, 
 * using the current paint mode, and in the current font. 
 * 
 * @version 	1.43, 08/19/98
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 * @see     java.awt.Component
 * @see     java.awt.Graphics#clipRect(int, int, int, int)
 * @see     java.awt.Graphics#setColor(java.awt.Color)
 * @see     java.awt.Graphics#setPaintMode()
 * @see     java.awt.Graphics#setXORMode(java.awt.Color)
 * @see     java.awt.Graphics#setFont(java.awt.Font)
 * @since       JDK1.0
 */
public abstract class Graphics {

    /**
     * Constructs a new <code>Graphics</code> object.  
     * This constructor is the default contructor for a graphics 
     * context. 
     * <p>
     * Since <code>Graphics</code> is an abstract class, applications 
     * cannot call this constructor directly. Graphics contexts are 
     * obtained from other graphics contexts or are created by calling 
     * <code>getGraphics</code> on a component. 
     * @see        java.awt.Graphics#create()
     * @see        java.awt.Component#getGraphics
     * @since      JDK1.0
     */
    protected Graphics() {
    }

    /**
     * Creates a new <code>Graphics</code> object that is 
     * a copy of this <code>Graphics</code> object.
     * @return     a new graphics context that is a copy of 
     *                       this graphics context.
     * @since      JDK1.0
     */
    public abstract Graphics create();

    /**
     * Creates a new <code>Graphics</code> object based on this 
     * <code>Graphics</code> object, but with a new translation and clip area.
     * The new <code>Graphics</code> object has its origin 
     * translated to the specified point (<i>x</i>,&nbsp;<i>y</i>). 
     * Its clip area is determined by the intersection of the original
     * clip area with the specified rectangle.  The arguments are all
     * interpreted in the coordinate system of the original 
     * <code>Graphics</code> object. The new graphics context is 
     * identical to the original, except in two respects: 
     * <p>
     * <ul>
     * <li>
     * The new graphics context is translated by (<i>x</i>,&nbsp;<i>y</i>).  
     * That is to say, the point (<code>0</code>,&nbsp;<code>0</code>) in the 
     * new graphics context is the same as (<i>x</i>,&nbsp;<i>y</i>) in 
     * the original graphics context. 
     * <li>
     * The new graphics context has an additional clipping rectangle, in 
     * addition to whatever (translated) clipping rectangle it inherited 
     * from the original graphics context. The origin of the new clipping 
     * rectangle is at (<code>0</code>,&nbsp;<code>0</code>), and its size  
     * is specified by the <code>width</code> and <code>height</code> arguments.
     * </ul>
     * <p>
     * @param      x   the <i>x</i> coordinate.
     * @param      y   the <i>y</i> coordinate.
     * @param      width   the width of the clipping rectangle.
     * @param      height   the height of the clipping rectangle.
     * @return     a new graphics context.
     * @see        java.awt.Graphics#translate
     * @see        java.awt.Graphics#clipRect
     * @since      JDK1.0
     */
    public Graphics create(int x, int y, int width, int height) {
	Graphics g = create();
	g.translate(x, y);
	g.clipRect(0, 0, width, height);
	return g;
    }

    /**
     * Translates the origin of the graphics context to the point
     * (<i>x</i>,&nbsp;<i>y</i>) in the current coordinate system. 
     * Modifies this graphics context so that its new origin corresponds 
     * to the point (<i>x</i>,&nbsp;<i>y</i>) in this graphics context's 
     * original coordinate system.  All coordinates used in subsequent 
     * rendering operations on this graphics context will be relative 
     * to this new origin.
     * @param  x   the <i>x</i> coordinate.
     * @param  y   the <i>y</i> coordinate.
     * @since   JDK1.0
     */
    public abstract void translate(int x, int y);

    /**
     * Gets this graphics context's current color.
     * @return    this graphics context's current color.
     * @see       java.awt.Color
     * @see       java.awt.Graphics#setColor
     * @since     JDK1.0
     */
    public abstract Color getColor();

    /**
     * Sets this graphics context's current color to the specified 
     * color. All subsequent graphics operations using this graphics 
     * context use this specified color. 
     * @param     c   the new rendering color.
     * @see       java.awt.Color
     * @see       java.awt.Graphics#getColor
     * @since     JDK1.0
     */
    public abstract void setColor(Color c);

    /**
     * Sets the paint mode of this graphics context to overwrite the 
     * destination with this graphics context's current color. 
     * This sets the logical pixel operation function to the paint or
     * overwrite mode.  All subsequent rendering operations will
     * overwrite the destination with the current color. 
     * @since   JDK1.0
     */
    public abstract void setPaintMode();

    /**
     * Sets the paint mode of this graphics context to alternate between 
     * this graphics context's current color and the new specified color. 
     * This specifies that logical pixel operations are performed in the 
     * XOR mode, which alternates pixels between the current color and 
     * a specified XOR color. 
     * <p>
     * When drawing operations are performed, pixels which are the 
     * current color are changed to the specified color, and vice versa. 
     * <p>
     * Pixels that are of colors other than those two colors are changed 
     * in an unpredictable but reversible manner; if the same figure is 
     * drawn twice, then all pixels are restored to their original values. 
     * @param     c1 the XOR alternation color
     * @since     JDK1.0
     */
    public abstract void setXORMode(Color c1);

    /**
     * Gets the current font.
     * @return    this graphics context's current font.
     * @see       java.awt.Font
     * @see       java.awt.Graphics#setFont
     * @since     JDK1.0
     */
    public abstract Font getFont();

    /**
     * Sets this graphics context's font to the specified font. 
     * All subsequent text operations using this graphics context 
     * use this font. 
     * @param  font   the font.
     * @see     java.awt.Graphics#getFont
     * @see     java.awt.Graphics#drawChars(java.lang.String, int, int)
     * @see     java.awt.Graphics#drawString(byte[], int, int, int, int)
     * @see     java.awt.Graphics#drawBytes(char[], int, int, int, int)
     * @since   JDK1.0
    */
    public abstract void setFont(Font font);

    /**
     * Gets the font metrics of the current font.
     * @return    the font metrics of this graphics 
     *                    context's current font.
     * @see       java.awt.Graphics#getFont
     * @see       java.awt.FontMetrics
     * @see       java.awt.Graphics#getFontMetrics(Font)
     * @since     JDK1.0
     */
    public FontMetrics getFontMetrics() {
	return getFontMetrics(getFont());
    }

    /**
     * Gets the font metrics for the specified font.
     * @return    the font metrics for the specified font.
     * @param     f the specified font
     * @see       java.awt.Graphics#getFont
     * @see       java.awt.FontMetrics
     * @see       java.awt.Graphics#getFontMetrics()
     * @since     JDK1.0
     */
    public abstract FontMetrics getFontMetrics(Font f);


    /**
     * Returns the bounding rectangle of the current clipping area.
     * The coordinates in the rectangle are relative to the coordinate
     * system origin of this graphics context.
     * @return      the bounding rectangle of the current clipping area.
     * @see         java.awt.Graphics#getClip
     * @see         java.awt.Graphics#clipRect
     * @see         java.awt.Graphics#setClip(int, int, int, int)
     * @see         java.awt.Graphics#setClip(Shape)
     * @since       JDK1.1
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
     * @param       x the <i>x</i> coordinate of the new clip rectangle.
     * @param       y the <i>y</i> coordinate of the new clip rectangle.
     * @param       width the width of the new clip rectangle.
     * @param       height the height of the new clip rectangle.
     * @see         java.awt.Graphics#clipRect
     * @see         java.awt.Graphics#setClip(Shape)
     * @since       JDK1.1
     */
    public abstract void setClip(int x, int y, int width, int height);

    /**
     * Gets the current clipping area.
     * @return      a <code>Shape</code> object representing the 
     *                      current clipping area.
     * @see         java.awt.Graphics#getClipBounds
     * @see         java.awt.Graphics#clipRect
     * @see         java.awt.Graphics#setClip(int, int, int, int)
     * @see         java.awt.Graphics#setClip(Shape)
     * @since       JDK1.1
     */
    public abstract Shape getClip();

    /**
     * Sets the current clipping area to an arbitrary clip shape.
     * Not all objects which implement the <code>Shape</code> 
     * interface can be used to set the clip.  The only 
     * <code>Shape</code> objects which are guaranteed to be 
     * supported are <code>Shape</code> objects which are
     * obtained via the <code>getClip</code> method and via 
     * <code>Rectangle</code> objects.
     * @see         java.awt.Graphics#getClip()
     * @see         java.awt.Graphics#clipRect
     * @see         java.awt.Graphics#setClip(int, int, int, int)
     * @since       JDK1.1
     */
    public abstract void setClip(Shape clip);

    /**
     * Copies an area of the component by a distance specified by 
     * <code>dx</code> and <code>dy</code>. From the point specified
     * by <code>x</code> and <code>y</code>, this method
     * copies downwards and to the right.  To copy an area of the 
     * component to the left or upwards, specify a negative value for 
     * <code>dx</code> or <code>dy</code>.
     * If a portion of the source rectangle lies outside the bounds 
     * of the component, or is obscured by another window or component, 
     * <code>copyArea</code> will be unable to copy the associated
     * pixels. The area that is omitted can be refreshed by calling 
     * the component's <code>paint</code> method.
     * @param       x the <i>x</i> coordinate of the source rectangle.
     * @param       y the <i>y</i> coordinate of the source rectangle.
     * @param       width the width of the source rectangle.
     * @param       height the height of the source rectangle.
     * @param       dx the horizontal distance to copy the pixels.
     * @param       dy the vertical distance to copy the pixels.
     * @since       JDK1.0
     */
    public abstract void copyArea(int x, int y, int width, int height,
				  int dx, int dy);

    /** 
     * Draws a line, using the current color, between the points 
     * <code>(x1,&nbsp;y1)</code> and <code>(x2,&nbsp;y2)</code> 
     * in this graphics context's coordinate system. 
     * @param   x1  the first point's <i>x</i> coordinate.
     * @param   y1  the first point's <i>y</i> coordinate.
     * @param   x2  the second point's <i>x</i> coordinate.
     * @param   y2  the second point's <i>y</i> coordinate.
     * @since   JDK1.0
     */
    public abstract void drawLine(int x1, int y1, int x2, int y2);

    /** 
     * Fills the specified rectangle. 
     * The left and right edges of the rectangle are at 
     * <code>x</code> and <code>x&nbsp;+&nbsp;width&nbsp;-&nbsp;1</code>. 
     * The top and bottom edges are at 
     * <code>y</code> and <code>y&nbsp;+&nbsp;height&nbsp;-&nbsp;1</code>. 
     * The resulting rectangle covers an area 
     * <code>width</code> pixels wide by 
     * <code>height</code> pixels tall.
     * The rectangle is filled using the graphics context's current color. 
     * @param         x   the <i>x</i> coordinate 
     *                         of the rectangle to be filled.
     * @param         y   the <i>y</i> coordinate 
     *                         of the rectangle to be filled.
     * @param         width   the width of the rectangle to be filled.
     * @param         height   the height of the rectangle to be filled.
     * @see           java.awt.Graphics#fillRect
     * @see           java.awt.Graphics#clearRect
     * @since         JDK1.0
     */
    public abstract void fillRect(int x, int y, int width, int height);

    /** 
     * Draws the outline of the specified rectangle. 
     * The left and right edges of the rectangle are at 
     * <code>x</code> and <code>x&nbsp;+&nbsp;width</code>. 
     * The top and bottom edges are at 
     * <code>y</code> and <code>y&nbsp;+&nbsp;height</code>. 
     * The rectangle is drawn using the graphics context's current color.
     * @param         x   the <i>x</i> coordinate 
     *                         of the rectangle to be drawn.
     * @param         y   the <i>y</i> coordinate 
     *                         of the rectangle to be drawn.
     * @param         width   the width of the rectangle to be drawn.
     * @param         height   the height of the rectangle to be drawn.
     * @see          java.awt.Graphics#fillRect
     * @see          java.awt.Graphics#clearRect
     * @since        JDK1.0
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
     * color of the current drawing surface. This operation does not 
     * use the current paint mode. 
     * <p>
     * Beginning with Java&nbsp;1.1, the background color 
     * of offscreen images may be system dependent. Applications should 
     * use <code>setColor</code> followed by <code>fillRect</code> to 
     * ensure that an offscreen image is cleared to a specific color. 
     * @param       x the <i>x</i> coordinate of the rectangle to clear.
     * @param       y the <i>y</i> coordinate of the rectangle to clear.
     * @param       width the width of the rectangle to clear.
     * @param       height the height of the rectangle to clear.
     * @see         java.awt.Graphics#fillRect(int, int, int, int)
     * @see         java.awt.Graphics#drawRect
     * @see         java.awt.Graphics#setColor(java.awt.Color)
     * @see         java.awt.Graphics#setPaintMode
     * @see         java.awt.Graphics#setXORMode(java.awt.Color)
     * @since       JDK1.0
     */
    public abstract void clearRect(int x, int y, int width, int height);

    /** 
     * Draws an outlined round-cornered rectangle using this graphics 
     * context's current color. The left and right edges of the rectangle 
     * are at <code>x</code> and <code>x&nbsp;+&nbsp;width</code>, 
     * respectively. The top and bottom edges of the rectangle are at 
     * <code>y</code> and <code>y&nbsp;+&nbsp;height</code>. 
     * @param      x the <i>x</i> coordinate of the rectangle to be drawn.
     * @param      y the <i>y</i> coordinate of the rectangle to be drawn.
     * @param      width the width of the rectangle to be drawn.
     * @param      height the height of the rectangle to be drawn.
     * @param      arcWidth the horizontal diameter of the arc 
     *                    at the four corners.
     * @param      arcHeight the vertical diameter of the arc 
     *                    at the four corners.
     * @see        java.awt.Graphics#fillRoundRect
     * @since      JDK1.0
     */
    public abstract void drawRoundRect(int x, int y, int width, int height,
				       int arcWidth, int arcHeight);

    /** 
     * Fills the specified rounded corner rectangle with the current color.
     * The left and right edges of the rectangle 
     * are at <code>x</code> and <code>x&nbsp;+&nbsp;width&nbsp;-&nbsp;1</code>, 
     * respectively. The top and bottom edges of the rectangle are at 
     * <code>y</code> and <code>y&nbsp;+&nbsp;height&nbsp;-&nbsp;1</code>. 
     * @param       x the <i>x</i> coordinate of the rectangle to be filled.
     * @param       y the <i>y</i> coordinate of the rectangle to be filled.
     * @param       width the width of the rectangle to be filled.
     * @param       height the height of the rectangle to be filled.
     * @param       arcWidth the horizontal diameter 
     *                     of the arc at the four corners.
     * @param       arcHeight the vertical diameter 
     *                     of the arc at the four corners.
     * @see         java.awt.Graphics#drawRoundRect
     * @since       JDK1.0
     */
    public abstract void fillRoundRect(int x, int y, int width, int height,
				       int arcWidth, int arcHeight);

    /**
     * Draws a 3-D highlighted outline of the specified rectangle.
     * The edges of the rectangle are highlighted so that they
     * appear to be beveled and lit from the upper left corner.
     * <p>
     * The colors used for the highlighting effect are determined 
     * based on the current color.
     * The resulting rectangle covers an area that is 
     * <code>width&nbsp;+&nbsp;1</code> pixels wide
     * by <code>height&nbsp;+&nbsp;1</code> pixels tall.
     * @param       x the <i>x</i> coordinate of the rectangle to be drawn.
     * @param       y the <i>y</i> coordinate of the rectangle to be drawn.
     * @param       width the width of the rectangle to be drawn.
     * @param       height the height of the rectangle to be drawn.
     * @param       raised a boolean that determines whether the rectangle
     *                      appears to be raised above the surface 
     *                      or sunk into the surface.
     * @see         java.awt.Graphics#fill3DRect
     * @since       JDK1.0
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
     * @param       x the <i>x</i> coordinate of the rectangle to be filled.
     * @param       y the <i>y</i> coordinate of the rectangle to be filled.
     * @param       width the width of the rectangle to be filled.
     * @param       height the height of the rectangle to be filled.
     * @param       raised a boolean value that determines whether the 
     *                      rectangle appears to be raised above the surface 
     *                      or etched into the surface.
     * @see         java.awt.Graphics#draw3DRect
     * @since       JDK1.0
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
     * Draws the outline of an oval.
     * The result is a circle or ellipse that fits within the 
     * rectangle specified by the <code>x</code>, <code>y</code>, 
     * <code>width</code>, and <code>height</code> arguments. 
     * <p> 
     * The oval covers an area that is 
     * <code>width&nbsp;+&nbsp;1</code> pixels wide 
     * and <code>height&nbsp;+&nbsp;1<code> pixels tall. 
     * @param       x the <i>x</i> coordinate of the upper left 
     *                     corner of the oval to be drawn.
     * @param       y the <i>y</i> coordinate of the upper left 
     *                     corner of the oval to be drawn.
     * @param       width the width of the oval to be drawn.
     * @param       height the height of the oval to be drawn.
     * @see         java.awt.Graphics#fillOval
     * @since       JDK1.0
     */
    public abstract void drawOval(int x, int y, int width, int height);

    /** 
     * Fills an oval bounded by the specified rectangle with the
     * current color.
     * @param       x the <i>x</i> coordinate of the upper left corner 
     *                     of the oval to be filled.
     * @param       y the <i>y</i> coordinate of the upper left corner 
     *                     of the oval to be filled.
     * @param       width the width of the oval to be filled.
     * @param       height the height of the oval to be filled.
     * @see         java.awt.Graphics#drawOval
     * @since       JDK1.0
     */
    public abstract void fillOval(int x, int y, int width, int height);

    /**
     * Draws the outline of a circular or elliptical arc 
     * covering the specified rectangle.
     * <p>
     * The resulting arc begins at <code>startAngle</code> and extends  
     * for <code>arcAngle</code> degrees, using the current color.
     * Angles are interpreted such that 0&nbsp;degrees 
     * is at the 3&nbsp;o'clock position. 
     * A positive value indicates a counter-clockwise rotation
     * while a negative value indicates a clockwise rotation.
     * <p>
     * The center of the arc is the center of the rectangle whose origin 
     * is (<i>x</i>,&nbsp;<i>y</i>) and whose size is specified by the 
     * <code>width</code> and <code>height</code> arguments. 
     * <p>
     * The resulting arc covers an area 
     * <code>width&nbsp;+&nbsp;1</code> pixels wide
     * by <code>height&nbsp;+&nbsp;1</code> pixels tall.
     * @param        x the <i>x</i> coordinate of the 
     *                    upper-left corner of the arc to be drawn.
     * @param        y the <i>y</i>  coordinate of the 
     *                    upper-left corner of the arc to be drawn.
     * @param        width the width of the arc to be drawn.
     * @param        height the height of the arc to be drawn.
     * @param        startAngle the beginning angle.
     * @param        arcAngle the angular extent of the arc, 
     *                    relative to the start angle.
     * @see         java.awt.Graphics#fillArc
     * @since       JDK1.0
     */
    public abstract void drawArc(int x, int y, int width, int height,
				 int startAngle, int arcAngle);

    /** 
     * Fills a circular or elliptical arc covering the specified rectangle.
     * <p>
     * The resulting arc begins at <code>startAngle</code> and extends  
     * for <code>arcAngle</code> degrees.
     * Angles are interpreted such that 0&nbsp;degrees 
     * is at the 3&nbsp;o'clock position. 
     * A positive value indicates a counter-clockwise rotation
     * while a negative value indicates a clockwise rotation.
     * <p>
     * The center of the arc is the center of the rectangle whose origin 
     * is (<i>x</i>,&nbsp;<i>y</i>) and whose size is specified by the 
     * <code>width</code> and <code>height</code> arguments. 
     * <p>
     * The resulting arc covers an area 
     * <code>width&nbsp;+&nbsp;1</code> pixels wide
     * by <code>height&nbsp;+&nbsp;1</code> pixels tall.
     * @param        x the <i>x</i> coordinate of the 
     *                    upper-left corner of the arc to be filled.
     * @param        y the <i>y</i>  coordinate of the 
     *                    upper-left corner of the arc to be filled.
     * @param        width the width of the arc to be filled.
     * @param        height the height of the arc to be filled.
     * @param        startAngle the beginning angle.
     * @param        arcAngle the angular extent of the arc, 
     *                    relative to the start angle.
     * @see         java.awt.Graphics#drawArc
     * @since       JDK1.0
     */
    public abstract void fillArc(int x, int y, int width, int height,
				 int startAngle, int arcAngle);

    /** 
     * Draws a sequence of connected lines defined by 
     * arrays of <i>x</i> and <i>y</i> coordinates. 
     * Each pair of (<i>x</i>,&nbsp;<i>y</i>) coordinates defines a point.
     * The figure is not closed if the first point 
     * differs from the last point.
     * @param       xPoints an array of <i>x</i> points
     * @param       yPoints an array of <i>y</i> points
     * @param       nPoints the total number of points
     * @see         java.awt.Graphics#drawPolygon(int[], int[], int)
     * @since       JDK1.1
     */
    public abstract void drawPolyline(int xPoints[], int yPoints[],
				      int nPoints);

    /** 
     * Draws a closed polygon defined by 
     * arrays of <i>x</i> and <i>y</i> coordinates. 
     * Each pair of (<i>x</i>,&nbsp;<i>y</i>) coordinates defines a point.
     * <p>
     * This method draws the polygon defined by <code>nPoint</code> line 
     * segments, where the first <code>nPoint&nbsp;-&nbsp;1</code> 
     * line segments are line segments from 
     * <code>(xPoints[i&nbsp;-&nbsp;1],&nbsp;yPoints[i&nbsp;-&nbsp;1])</code> 
     * to <code>(xPoints[i],&nbsp;yPoints[i])</code>, for 
     * 1&nbsp;&le;&nbsp;<i>i</i>&nbsp;&le;&nbsp;<code>nPoints</code>.  
     * The figure is automatically closed by drawing a line connecting
     * the final point to the first point, if those points are different.
     * @param        xPoints   a an array of <code>x</code> coordinates.
     * @param        yPoints   a an array of <code>y</code> coordinates.
     * @param        nPoints   a the total number of points.
     * @see          java.awt.Graphics#fillPolygon
     * @see          java.awt.Graphics#drawPolyline
     * @since        JDK1.0
     */
    public abstract void drawPolygon(int xPoints[], int yPoints[],
				     int nPoints);

    /** 
     * Draws the outline of a polygon defined by the specified 
     * <code>Polygon</code> object. 
     * @param        p the polygon to draw.
     * @see          java.awt.Graphics#fillPolygon
     * @see          java.awt.Graphics#drawPolyline
     * @since        JDK1.0
     */
    public void drawPolygon(Polygon p) {
	drawPolygon(p.xpoints, p.ypoints, p.npoints);
    }

    /** 
     * Fills a closed polygon defined by 
     * arrays of <i>x</i> and <i>y</i> coordinates. 
     * <p>
     * This method draws the polygon defined by <code>nPoint</code> line 
     * segments, where the first <code>nPoint&nbsp;-&nbsp;1</code> 
     * line segments are line segments from 
     * <code>(xPoints[i&nbsp;-&nbsp;1],&nbsp;yPoints[i&nbsp;-&nbsp;1])</code> 
     * to <code>(xPoints[i],&nbsp;yPoints[i])</code>, for 
     * 1&nbsp;&le;&nbsp;<i>i</i>&nbsp;&le;&nbsp;<code>nPoints</code>.  
     * The figure is automatically closed by drawing a line connecting
     * the final point to the first point, if those points are different.
     * <p>
     * The area inside the polygon is defined using an 
     * even-odd fill rule, also known as the alternating rule.
     * @param        xPoints   a an array of <code>x</code> coordinates.
     * @param        yPoints   a an array of <code>y</code> coordinates.
     * @param        nPoints   a the total number of points.
     * @see          java.awt.Graphics#drawPolygon(int[], int[], int)
     * @since        JDK1.0
     */
    public abstract void fillPolygon(int xPoints[], int yPoints[],
				     int nPoints);

    /** 
     * Fills the polygon defined by the specified Polygon object with
     * the graphics context's current color. 
     * <p>
     * The area inside the polygon is defined using an 
     * even-odd fill rule, also known as the alternating rule.
     * @param        p the polygon to fill.
     * @see          java.awt.Graphics#drawPolygon(int[], int[], int)
     * @since        JDK1.0
     */
    public void fillPolygon(Polygon p) {
	fillPolygon(p.xpoints, p.ypoints, p.npoints);
    }

    /** 
     * Draws the text given by the specified string, using this 
     * graphics context's current font and color. The baseline of the 
     * first character is at position (<i>x</i>,&nbsp;<i>y</i>) in this 
     * graphics context's coordinate system. 
     * @param       str      the string to be drawn.
     * @param       x        the <i>x</i> coordinate.
     * @param       y        the <i>y</i> coordinate.
     * @see         java.awt.Graphics#drawBytes
     * @see         java.awt.Graphics#drawChars
     * @since       JDK1.0
     */
    public abstract void drawString(String str, int x, int y);

    /** 
     * Draws the text given by the specified character array, using this 
     * graphics context's current font and color. The baseline of the 
     * first character is at position (<i>x</i>,&nbsp;<i>y</i>) in this 
     * graphics context's coordinate system. 
     * @param data the array of characters to be drawn
     * @param offset the start offset in the data
     * @param length the number of characters to be drawn
     * @param x the <i>x</i> coordinate of the baseline of the text
     * @param y the <i>y</i> coordinate of the baseline of the text
     * @see         java.awt.Graphics#drawBytes
     * @see         java.awt.Graphics#drawString
     * @since       JDK1.0
     */
    public void drawChars(char data[], int offset, int length, int x, int y) {
	drawString(new String(data, offset, length), x, y);
    }

    /** 
     * Draws the text given by the specified byte array, using this 
     * graphics context's current font and color. The baseline of the 
     * first character is at position (<i>x</i>,&nbsp;<i>y</i>) in this 
     * graphics context's coordinate system.
     * @param data the data to be drawn
     * @param offset the start offset in the data
     * @param length the number of bytes that are drawn
     * @param x the <i>x</i> coordinate of the baseline of the text
     * @param y the <i>y</i> coordinate of the baseline of the text
     * @see         java.awt.Graphics#drawChars
     * @see         java.awt.Graphics#drawString
     * @since       JDK1.0
     */
    public void drawBytes(byte data[], int offset, int length, int x, int y) {
	drawString(new String(data, 0, offset, length), x, y);
    }

    /** 
     * Draws as much of the specified image as is currently available.
     * The image is drawn with its top-left corner at 
     * (<i>x</i>,&nbsp;<i>y</i>) in this graphics context's coordinate 
     * space. Transparent pixels in the image do not affect whatever 
     * pixels are already there. 
     * <p>
     * This method returns immediately in all cases, even if the
     * complete image has not yet been loaded, and it has not been dithered 
     * and converted for the current output device.
     * <p>
     * If the image has not yet been completely loaded, then
     * <code>drawImage</code> returns <code>false</code>. As more of
     * the image becomes available, the process that draws the image notifies 
     * the specified image observer.
     * @param    img the specified image to be drawn.
     * @param    x   the <i>x</i> coordinate.
     * @param    y   the <i>y</i> coordinate.
     * @param    observer    object to be notified as more of 
     *                          the image is converted.
     * @see      java.awt.Image
     * @see      java.awt.image.ImageObserver
     * @see      java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int, int, int, int, int)
     * @since    JDK1.0
     */
    public abstract boolean drawImage(Image img, int x, int y, 
				      ImageObserver observer);

    /**
     * Draws as much of the specified image as has already been scaled
     * to fit inside the specified rectangle.
     * <p>
     * The image is drawn inside the specified rectangle of this 
     * graphics context's coordinate space, and is scaled if 
     * necessary. Transparent pixels do not affect whatever pixels
     * are already there. 
     * <p>
     * This method returns immediately in all cases, even if the
     * entire image has not yet been scaled, dithered, and converted
     * for the current output device.
     * If the current output representation is not yet complete, then
     * <code>drawImage</code> returns <code>false</code>. As more of
     * the image becomes available, the process that draws the image notifies 
     * the image observer by calling its <code>imageUpdate</code> method.
     * <p>
     * A scaled version of an image will not necessarily be
     * available immediately just because an unscaled version of the
     * image has been constructed for this output device.  Each size of
     * the image may be cached separately and generated from the original
     * data in a separate image production sequence.
     * @param    img    the specified image to be drawn.
     * @param    x      the <i>x</i> coordinate.
     * @param    y      the <i>y</i> coordinate.
     * @param    width  the width of the rectangle.
     * @param    height the height of the rectangle.
     * @param    observer    object to be notified as more of 
     *                          the image is converted.
     * @see      java.awt.Image
     * @see      java.awt.image.ImageObserver
     * @see      java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int, int, int, int, int)
     * @since    JDK1.0
     */
    public abstract boolean drawImage(Image img, int x, int y,
				      int width, int height, 
				      ImageObserver observer);
    
    /** 
     * Draws as much of the specified image as is currently available.
     * The image is drawn with its top-left corner at 
     * (<i>x</i>,&nbsp;<i>y</i>) in this graphics context's coordinate 
     * space.  Transparent pixels are drawn in the specified
     * background color.
     * <p> 
     * This operation is equivalent to filling a rectangle of the
     * width and height of the specified image with the given color and then
     * drawing the image on top of it, but possibly more efficient.
     * <p>
     * This method returns immediately in all cases, even if the
     * complete image has not yet been loaded, and it has not been dithered 
     * and converted for the current output device.
     * <p>
     * If the image has not yet been completely loaded, then
     * <code>drawImage</code> returns <code>false</code>. As more of
     * the image becomes available, the process that draws the image notifies 
     * the specified image observer.
     * @param    img    the specified image to be drawn.
     * @param    x      the <i>x</i> coordinate.
     * @param    y      the <i>y</i> coordinate.
     * @param    bgcolor the background color to paint under the
     *                         non-opaque portions of the image.
     * @param    observer    object to be notified as more of 
     *                          the image is converted.
     * @see      java.awt.Image
     * @see      java.awt.image.ImageObserver
     * @see      java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int, int, int, int, int)
     * @since    JDK1.0
     */
    public abstract boolean drawImage(Image img, int x, int y, 
				      Color bgcolor,
				      ImageObserver observer);

    /**
     * Draws as much of the specified image as has already been scaled
     * to fit inside the specified rectangle.
     * <p>
     * The image is drawn inside the specified rectangle of this 
     * graphics context's coordinate space, and is scaled if 
     * necessary. Transparent pixels are drawn in the specified
     * background color. 
     * This operation is equivalent to filling a rectangle of the
     * width and height of the specified image with the given color and then
     * drawing the image on top of it, but possibly more efficient.
     * <p>
     * This method returns immediately in all cases, even if the
     * entire image has not yet been scaled, dithered, and converted
     * for the current output device.
     * If the current output representation is not yet complete then
     * <code>drawImage</code> returns <code>false</code>. As more of
     * the image becomes available, the process that draws the image notifies 
     * the specified image observer.
     * <p>
     * A scaled version of an image will not necessarily be
     * available immediately just because an unscaled version of the
     * image has been constructed for this output device.  Each size of
     * the image may be cached separately and generated from the original
     * data in a separate image production sequence.
     * @param    img       the specified image to be drawn.
     * @param    x         the <i>x</i> coordinate.
     * @param    y         the <i>y</i> coordinate.
     * @param    width     the width of the rectangle.
     * @param    height    the height of the rectangle.
     * @param    bgcolor   the background color to paint under the
     *                         non-opaque portions of the image.
     * @param    observer    object to be notified as more of 
     *                          the image is converted.
     * @see      java.awt.Image
     * @see      java.awt.image.ImageObserver
     * @see      java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int, int, int, int, int)
     * @since    JDK1.0
     */
    public abstract boolean drawImage(Image img, int x, int y,
				      int width, int height, 
				      Color bgcolor,
				      ImageObserver observer);
    
    /**
     * Draws as much of the specified area of the specified image as is
     * currently available, scaling it on the fly to fit inside the
     * specified area of the destination drawable surface. Transparent pixels 
     * do not affect whatever pixels are already there.
     * <p>
     * This method returns immediately in all cases, even if the
     * image area to be drawn has not yet been scaled, dithered, and converted
     * for the current output device.
     * If the current output representation is not yet complete then
     * <code>drawImage</code> returns <code>false</code>. As more of
     * the image becomes available, the process that draws the image notifies 
     * the specified image observer.
     * <p>
     * This method always uses the unscaled version of the image
     * to render the scaled rectangle and performs the required
     * scaling on the fly. It does not use a cached, scaled version
     * of the image for this operation. Scaling of the image from source
     * to destination is performed such that the first coordinate
     * of the source rectangle is mapped to the first coordinate of
     * the destination rectangle, and the second source coordinate is
     * mapped to the second destination coordinate. The subimage is
     * scaled and flipped as needed to preserve those mappings.
     * @param       img the specified image to be drawn
     * @param       dx1 the <i>x</i> coordinate of the first corner of the
     *                    destination rectangle.
     * @param       dy1 the <i>y</i> coordinate of the first corner of the
     *                    destination rectangle.
     * @param       dx2 the <i>x</i> coordinate of the second corner of the
     *                    destination rectangle.
     * @param       dy2 the <i>y</i> coordinate of the second corner of the
     *                    destination rectangle.
     * @param       sx1 the <i>x</i> coordinate of the first corner of the
     *                    source rectangle.
     * @param       sy1 the <i>y</i> coordinate of the first corner of the
     *                    source rectangle.
     * @param       sx2 the <i>x</i> coordinate of the second corner of the
     *                    source rectangle.
     * @param       sy2 the <i>y</i> coordinate of the second corner of the
     *                    source rectangle.
     * @param       observer object to be notified as more of the image is
     *                    scaled and converted.
     * @see         java.awt.Image
     * @see         java.awt.image.ImageObserver
     * @see         java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int, int, int, int, int)
     * @since       JDK1.1
     */
    public abstract boolean drawImage(Image img,
				      int dx1, int dy1, int dx2, int dy2,
				      int sx1, int sy1, int sx2, int sy2,
				      ImageObserver observer);

    /**
     * Draws as much of the specified area of the specified image as is
     * currently available, scaling it on the fly to fit inside the
     * specified area of the destination drawable surface. 
     * <p>
     * Transparent pixels are drawn in the specified background color. 
     * This operation is equivalent to filling a rectangle of the
     * width and height of the specified image with the given color and then
     * drawing the image on top of it, but possibly more efficient.
     * <p>
     * This method returns immediately in all cases, even if the
     * image area to be drawn has not yet been scaled, dithered, and converted
     * for the current output device.
     * If the current output representation is not yet complete then
     * <code>drawImage</code> returns <code>false</code>. As more of
     * the image becomes available, the process that draws the image notifies 
     * the specified image observer.
     * <p>
     * This method always uses the unscaled version of the image
     * to render the scaled rectangle and performs the required
     * scaling on the fly. It does not use a cached, scaled version
     * of the image for this operation. Scaling of the image from source
     * to destination is performed such that the first coordinate
     * of the source rectangle is mapped to the first coordinate of
     * the destination rectangle, and the second source coordinate is
     * mapped to the second destination coordinate. The subimage is
     * scaled and flipped as needed to preserve those mappings.
     * @param       img the specified image to be drawn
     * @param       dx1 the <i>x</i> coordinate of the first corner of the
     *                    destination rectangle.
     * @param       dy1 the <i>y</i> coordinate of the first corner of the
     *                    destination rectangle.
     * @param       dx2 the <i>x</i> coordinate of the second corner of the
     *                    destination rectangle.
     * @param       dy2 the <i>y</i> coordinate of the second corner of the
     *                    destination rectangle.
     * @param       sx1 the <i>x</i> coordinate of the first corner of the
     *                    source rectangle.
     * @param       sy1 the <i>y</i> coordinate of the first corner of the
     *                    source rectangle.
     * @param       sx2 the <i>x</i> coordinate of the second corner of the
     *                    source rectangle.
     * @param       sy2 the <i>y</i> coordinate of the second corner of the
     *                    source rectangle.
     * @param       bgcolor the background color to paint under the
     *                    non-opaque portions of the image.
     * @param       observer object to be notified as more of the image is
     *                    scaled and converted.
     * @see         java.awt.Image
     * @see         java.awt.image.ImageObserver
     * @see         java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int, int, int, int, int)
     * @since       JDK1.1
     */
    public abstract boolean drawImage(Image img,
				      int dx1, int dy1, int dx2, int dy2,
				      int sx1, int sy1, int sx2, int sy2,
				      Color bgcolor,
				      ImageObserver observer);

    /**
     * Disposes of this graphics context and releases 
     * any system resources that it is using. 
     * A <code>Graphics</code> object cannot be used after 
     * <code>dispose</code>has been called.
     * <p>
     * When a Java program runs, a large number of <code>Graphics</code>
     * objects can be created within a short time frame.
     * Although the finalization process of the garbage collector 
     * also disposes of the same system resources, it is preferable 
     * to manually free the associated resources by calling this
     * method rather than to rely on a finalization process which 
     * may not run to completion for a long period of time.
     * <p>
     * Graphics objects which are provided as arguments to the 
     * <code>paint</code> and <code>update</code> methods 
     * of components are automatically released by the system when 
     * those methods return. For efficiency, programmers should
     * call <code>dispose</code> when finished using
     * a <code>Graphics</code> object only if it was created 
     * directly from a component or another <code>Graphics</code> object.
     * @see         java.awt.Graphics#finalize
     * @see         java.awt.Component#paint
     * @see         java.awt.Component#update
     * @see         java.awt.Component#getGraphics
     * @see         java.awt.Graphics#create
     * @since       JDK1.0
     */
    public abstract void dispose();

    /**
     * Disposes of this graphics context once it is no longer referenced.
     * @see #dispose
     * @since JDK1.0
     */
    public void finalize() {
	dispose();
    }

    /**
     * Returns a <code>String</code> object representing this 
     *                        <code>Graphics</code> object's value.
     * @return       a string representation of this graphics context.
     * @since        JDK1.0
     */
    public String toString() {	
	return getClass().getName() + "[font=" + getFont() + ",color=" + getColor() + "]";
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getClipBounds()</code>.
     */
    public Rectangle getClipRect() {
	return getClipBounds();
    }
}
