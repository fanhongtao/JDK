/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @author Charlton Innovations, Inc.
 */

package java.awt.font;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Polygon;        // remind - need a floating point version
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.Shape;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphJustificationInfo;

/**
*   A <code>GlyphVector</code> object is a collection of glyphs
*   containing geometric information for the placement of each glyph 
*   in a transformed coordinate space which corresponds to the
*   device on which the <code>GlyphVector</code> is ultimately 
*   displayed. 
*   <p>
*   The <code>GlyphVector</code> does not attempt any interpretation of
*   the sequence of glyphs it contains.  Relationships between adjacent
*   glyphs in sequence are solely used to determine the placement of
*   the glyphs in the visual coordinate space.
*   <p>
*   Instances of <code>GlyphVector</code> are created by a {@link Font}.
*   <p>
*   In a text processing application that can cache intermediate
*   representations of text, creation and subsequent caching of a 
*   <code>GlyphVector</code> for use during rendering is the fastest
*   method to present the visual representation of characters to a user.
*   <p>
*   A <code>GlyphVector</code> is associated with exactly one 
*   <code>Font</code>, and can provide data useful only in relation to
*   this <code>Font</code>.  In addition, metrics obtained from a
*   <code>GlyphVector</code> are not generally geometrically scaleable
*   since the pixelization and spacing are dependent on grid-fitting
*   algorithms within a <code>Font</code>.  To facilitate accurate
*   measurement of a <code>GlyphVector</code> and its component
*   glyphs, you must specify a scaling transform, anti-alias mode, and
*   fractional metrics mode when creating the <code>GlyphVector</code>.
*   These characteristics can be derived from the destination device.
*   <p>
*   For each glyph in the <code>GlyphVector</code>, you can obtain:
*   <ul>
*   <li>the position of the glyph
*   <li>the transform associated with the glyph
*   <li>the metrics of the glyph in the context of the 
*	   <code>GlyphVector</code>.  The metrics of the glyph may be
*           different under different transforms, application specified
*           rendering hints, and the specific instance of the glyph within
*           the <code>GlyphVector</code>.
*   </ul>
*   <p>
*   Altering the data used to create the <code>GlyphVector</code> does not
*   alter the state of the <code>GlyphVector</code>.
*   <p>
*   Methods are provided to create new <code>GlyphVector</code>
*   objects which are the result of editing operations on the
*   <code>GlyphVector</code>, such as glyph insertion and deletion.  These
*   methods are most appropriate for applications that are forming
*   combinations such as ligatures from existing glyphs or are breaking
*   such combinations into their component parts for visual presentation.
*   <p>
*   Methods are provided to create new <code>GlyphVector</code>
*   objects that are the result of specifying new positions for the glyphs
*   within the <code>GlyphVector</code>.  These methods are most
*   appropriate for applications that are performing justification
*   operations for the presentation of the glyphs.
*   <p>
*   Methods are provided to return both the visual and logical bounds
*   of the entire <code>GlyphVector</code> or of individual glyphs within
*   the <code>GlyphVector</code>.
*   <p>
*   Methods are provided to return a {@link Shape} for the 
*   <code>GlyphVector</code>, and for individual glyphs within the
*   <code>GlyphVector</code>.
*   @see Font
*   @see GlyphMetrics
*   @see TextLayout
*   @version 19 Mar 1998
*   @author Charlton Innovations, Inc.
*/

public abstract class GlyphVector implements Cloneable {


    // methods associated with creation-time state
    /**
    *   Returns the <code>Font</code> associated with this 
    *   <code>GlyphVector</code>.
    *   @returns <code>Font</code> used to create this 
    *   <code>GlyphVector</code>.
    *   @see Font
    */
    public abstract Font getFont();

    /**
    *   Returns the {@link FontRenderContext} associated with this
    *   <code>GlyphVector</code>.
    *   @return <code>FontRenderContext</code> used to create this
    *   <code>GlyphVector</code>.
    *   @see FontRenderContext
    *   @see Font
    */
    public abstract FontRenderContext getFontRenderContext();

    // methods associated with the GlyphVector as a whole

    /**
    *   Assigns default positions to each glyph in this 
    *   <code>GlyphVector</code>.  No shaping, reordering, or contextual
    *   substitution is performed.
    */
    public abstract void performDefaultLayout();

    /**
    *   Returns the number of glyphs in this <code>GlyphVector</code>.
    *   This information is used to create arrays that are to be
    *   filled with results of other information retrieval
    *   operations.
    *   @return number of glyphs in this <code>GlyphVector</code>.
    */
    public abstract int getNumGlyphs();

    /**
    *   Returns the glyphcode of the specified glyph.
    *   This return value is meaningless to anything other
    *   than a <code>Font</code> object and can be used to ask the 
    *   <code>Font</code> object about the existence of ligatures and
    *   other context sensitive information.
    *   @param glyphIndex the index into this <code>GlyphVector</code>
    *   that corresponds to the glyph from which to retrieve the 
    *   glyphcode.
    *   @return the glyphcode of the glyph corresponding the the specified
    *   <code>glyphIndex</code>.
    *   @throws IndexOutOfBoundsException if <code>glyphIndex</code>
    *           is less than 0 or greater than or equal to the 
    *           number of glyphs in this <code>GlyphVector</code>
    */
    public abstract int getGlyphCode(int glyphIndex);

    /**
    *   Returns an array of glyphcodes for the specified glyphs.
    *   The contents of this return value are meaningless to anything other
    *   than a <code>Font</code> and can be used to ask the 
    *   <code>Font</code> about the existence of ligatures and other
    *   context sensitive information.  This method is used
    *   for convenience and performance when processing glyphcodes.
    *   If no array is passed in, an array is created.
    *   @param beginGlyphIndex the index into this
    *   <code>GlyphVector</code> at which to start retrieving glyphcodes
    *   for the corresponding glyphs
    *   @param numEntries the number of glyphs to retrieve
    *   @param codeReturn the array that receives the glyphcodes and is
    *		then returned
    *   @return an array of glyphcodes for the specified glyphs.
    *   @throws IllegalArgumentException if <code>numEntries</code> is 
    *           less than 0
    *   @throws IndexOutOfBoundsException if <code>beginGlyphIndex</code> 
    *           is less than 0
    *   @throws IndexOutOfBoundsException if the sum of 
    *           <code>beginGlyphIndex</code> and <code>numEntries</code> is 
    *           greater than the number of glyphs in this
    *           <code>GlyphVector</code>
    */
    public abstract int[] getGlyphCodes(int beginGlyphIndex, int numEntries,
                                        int[] codeReturn);

    // methods associated with the GlyphVector as a whole,
    // after layout.
    /**
    *   Returns the logical bounds of this <code>GlyphVector</code>.
    *   This method is used when positioning this <code>GlyphVector</code> 
    *   in relation to visually adjacent <code>GlyphVector</code> objects.
    *   @return a {@link Rectangle2D} that is the logical bounds of this
    *   <code>GlyphVector</code>.
    */
    public abstract Rectangle2D getLogicalBounds();

    /**
    *   Returns the visual bounds of this <code>GlyphVector</code>
    *   The visual bounds is the tightest rectangle enclosing all
    *   non-background pixels in the rendered representation of this
    *   <code>GlyphVector</code>.
    *   @return a <code>Rectangle2D</code> that is the tightest bounds
    *   of this <code>GlyphVector</code>.
    */
    public abstract Rectangle2D getVisualBounds();

    /**
    *   Returns a <code>Shape</code> whose interior corresponds to the
    *   visual representation of this <code>GlyphVector</code>.
    *   @return a <code>Shape</code> that is the outline of this
    *   <code>GlyphVector</code>.
    */
    public abstract Shape getOutline();

    /**
    *   Returns a <code>Shape</code> whose interior corresponds to the
    *   visual representation of this <code>GlyphVector</code>, offset
    *   to x,&nbsp;y.
    *   @param x,&nbsp;y the coordinates of the location of the outline
    *		<code>Shape</code>
    *   @return a <code>Shape</code> that is the outline of this
    *           <code>GlyphVector</code>, offset to the specified 
    *		coordinates.
    */
    public abstract Shape getOutline(float x, float y);

    /**
    *   Returns a <code>Shape</code> whose interior corresponds to the
    *   visual representation of the specified glyph
    *   within this <code>GlyphVector</code>.
    *   @param glyphIndex the index into this <code>GlyphVector</code>
    *   @return a <code>Shape</code> that is the outline of the glyph
    *		at the specified <code>glyphIndex</code> of this
    *		<code>GlyphVector</code>.
    *   @throws IndexOutOfBoundsException if <code>glyphIndex</code>
    *           is less than 0 or greater than or equal to the number
    *           of glyphs in this <code>GlyphVector</code>
    */
    public abstract Shape getGlyphOutline(int glyphIndex);

    /**
    *   Returns the position of the specified glyph within this
    *   <code>GlyphVector</code>.
    *   This position corresponds to the leading edge of the baseline for
    *   the glyph.
    *   If <code>glyphIndex</code> equals the number of of glyphs in 
    *   this <code>GlyphVector</code>, this method gets the position after
    *   the last glyph and this position is used to define the advance of 
    *   the entire <code>GlyphVector</code>.
    *   @param glyphIndex the index into this <code>GlyphVector</code>
    *   @return a {@link Point2D} object that is the position of the glyph
    *		at the specified <code>glyphIndex</code>.
    *   @throws IndexOutOfBoundsException if <code>glyphIndex</code>
    *           is less than 0 or greater than the number of glyphs
    *           in this <code>GlyphVector</code>
    */
    public abstract Point2D getGlyphPosition(int glyphIndex);

    /**
    *   Sets the position of the specified glyph within this
    *   <code>GlyphVector</code>.
    *   This position corresponds to the leading edge of the baseline for
    *   the glyph.
    *   If <code>glyphIndex</code> equals the number of of glyphs in 
    *   this <code>GlyphVector</code>, this method sets the position after
    *   the last glyph and this position is used to define the advance of 
    *   the entire <code>GlyphVector</code>.
    *   @param glyphIndex the index into this <code>GlyphVector</code>
    *   @param newPos the <code>Point2D</code> at which to position the
    *		glyph at the specified <code>glyphIndex</code>
    *   @throws IndexOutOfBoundsException if <code>glyphIndex</code>
    *           is less than 0 or greater than the number of glyphs
    *           in this <code>GlyphVector</code>
    */
    public abstract void setGlyphPosition(int glyphIndex, Point2D newPos);

    /**
    *   Gets the transform of the specified glyph within this
    *   <code>GlyphVector</code>.  The transform is relative to the
    *   glyph position.  If no special transform has been applied, 
    *   <code>null</code> can be returned.  Such a transform would 
    *   be an identity transform.
    *   @param glyphIndex the index into this <code>GlyphVector</code>
    *   @return an {@link AffineTransform} that is the transform of
    *		the glyph at the specified <code>glyphIndex</code>.
    *   @throws IndexOutOfBoundsException if <code>glyphIndex</code>
    *           is less than 0 or greater than or equal to the number 
    *           of glyphs in this <code>GlyphVector</code>
    */
    public abstract AffineTransform getGlyphTransform(int glyphIndex);

    /**
    *   Sets the transform of the specified glyph within this
    *   <code>GlyphVector</code>.  The transform is relative to the glyph
    *   position.  A <code>null</code> argument for <code>newTX</code>
    *   indicates that no special transform is applied for the specified
    *   glyph.
    *   This method can be used to rotate, mirror, translate and scale the
    *   glyph.  Adding a transform can result in signifant performance changes.
    *   @param glyphIndex the index into this <code>GlyphVector</code>
    *   @param newTx the specified transform that the transform of the
    *		glyph at the specified <code>glyphIndex</code> is set to
    *   @throws IndexOutOfBoundsException if <code>glyphIndex</code>
    *           is less than 0 or greater than or equal to the number 
    *           of glyphs in this <code>GlyphVector</code>
    */
    public abstract void setGlyphTransform(int glyphIndex, AffineTransform newTX);

    /**
    *   Returns an array of glyph positions for the specified glyphs.
    *   The position of each glyph corresponds to the leading edge of the
    *   baseline for that glyph.  This method is used for convenience and
    *   performance when processing glyph positions.
    *   If no array is passed in, a new array is created.
    *   Even numbered array entries beginning with position zero are the X
    *   coordinates of the glyph numbered beginGlyphIndex + position/2.  
    *   Odd numbered array entries beginning with position one are the Y
    *   coordinates of the glyph numbered beginGlyphIndex + (position-1)/2.
    *   If <code>beginGlyphIndex</code> equals the number of of glyphs in 
    *   this <code>GlyphVector</code>, this method gets the position after
    *   the last glyph and this position is used to define the advance of 
    *   the entire <code>GlyphVector</code>.
    *   @param beginGlyphIndex the index at which to begin retrieving
    *        glyph positions
    *   @param numEntries the number of glyphs to retrieve
    *   @param positionReturn the array that receives the glyph positions
    *     and is then returned.
    *   @return an array of glyph positions specified by
    *		<code>beginGlyphIndex</code> and <code>numEntries</code>.
    *   @throws IllegalArgumentException if <code>numEntries</code> is
    *           less than 0
    *   @throws IndexOutOfBoundsException if <code>beginGlyphIndex</code>
    *           is less than 0
    *   @throws IndexOutOfBoundsException if the sum of 
    *           <code>beginGlyphIndex</code> and <code>numEntries</code> 
    *           is greater than the number of glyphs in this 
    *           <code>GlyphVector</code> plus one
    */
    public abstract float[] getGlyphPositions(int beginGlyphIndex, int numEntries,
                                              float[] positionReturn);

    /**
    *   Returns the logical bounds of the specified glyph within this
    *   <code>GlyphVector</code>.
    *   These logical bounds have a total of four edges, with two edges
    *   parallel to the baseline under the glyph's transform and the other two
    *   edges are shared with adjacent glyphs if they are present.  This
    *   method is useful for hit-testing of the specified glyph,
    *   positioning of a caret at the leading or trailing edge of a glyph,
    *   and for drawing a highlight region around the specified glyph.
    *   @param glyphIndex the index into this <code>GlyphVector</code>
    *   that corresponds to the glyph from which to retrieve its logical
    *   bounds
    *   @return  a <code>Shape</code> that is the logical bounds of the
    *	glyph at the specified <code>glyphIndex</code>.
    *   @throws IndexOutOfBoundsException if <code>glyphIndex</code>
    *           is less than 0 or greater than or equal to the number 
    *           of glyphs in this <code>GlyphVector</code>
    *   @see #getGlyphVisualBounds
    */
    public abstract Shape getGlyphLogicalBounds(int glyphIndex);

    /**
    *   Returns the visual bounds of the specified glyph within the
    * 	<code>GlyphVector</code>.
    *   These visual bounds have a total of four edges, representing the
    *   tightest polygon enclosing non-background pixels in the rendered
    *   representation of the glyph whose edges are parallel to the edges
    *   of the logical bounds.  Useful for hit-testing of the specified glyph.
    *	@param glyphIndex the index into this <code>GlyphVector</code>
    *   that corresponds to the glyph from which to retrieve its visual
    *   bounds
    *	@return a <code>Shape</code> that is the visual bounds of the
    * 	glyph at the specified <code>glyphIndex</code>.
    *   @throws IndexOutOfBoundsException if <code>glyphIndex</code>
    *           is less than 0 or greater than or equal to the number 
    *           of glyphs in this <code>GlyphVector</code>
    *   @see #getGlyphLogicalBounds
    */
    public abstract Shape getGlyphVisualBounds(int glyphIndex);

   /**	
    *   Returns the metrics of the glyph at the specified index into
    *	this <code>GlyphVector</code>.
    *	@param glyphIndex the index into this <code>GlyphVector</code>
    *   that corresponds to the glyph from which to retrieve its metrics
    *	@return a {@link GlyphMetrics} object that represents the
    *	metrics of the glyph at the specified <code>glyphIndex</code> 
    *	into this <code>GlyphVector</code>.
    *   @throws IndexOutOfBoundsException if <code>glyphIndex</code>
    *           is less than 0 or greater than or equal to the number 
    *           of glyphs in this <code>GlyphVector</code>
    */
    public abstract GlyphMetrics getGlyphMetrics(int glyphIndex);

   /**
    *   Returns the justification information for the glyph at
    *   the specified index into this <code>GlyphVector</code>.
    * 	@param glyphIndex the index into this <code>GlyphVector</code>
    *	that corresponds to the glyph from which to retrieve its 
    *	justification properties
    *	@return a {@link GlyphJustificationInfo} object that
    *	represents the justification properties of the glyph at the
    *	specified <code>glyphIndex</code> into this
    *	<code>GlyphVector</code>.
    *   @throws IndexOutOfBoundsException if <code>glyphIndex</code>
    *           is less than 0 or greater than or equal to the number 
    *           of glyphs in this <code>GlyphVector</code>
    */
    public abstract GlyphJustificationInfo getGlyphJustificationInfo(int glyphIndex);
    // general utility methods
    /**
    *   Tests if the specified <code>GlyphVector</code> exactly
    *	equals this <code>GlyphVector</code>.
    *	@param set the specified <code>GlyphVector</code> to test
    *	@return <code>true</code> if the specified
    *	<code>GlyphVector</code> equals this <code>GlyphVector</code>;
    *	<code>false</code> otherwise.
    */
    public abstract boolean equals(GlyphVector set);
}
