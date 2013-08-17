/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;


import java.awt.*;
import java.io.Serializable;

/**
 * For the convenience of layout managers,
 * calculates information about the size and position of components.
 * All size and position calculation methods are class methods
 * that take arrays of SizeRequirements as arguments.
 * The SizeRequirements class supports two types of layout:
 *
 * <blockquote>
 * <dl>
 * <dt> tiled
 * <dd> The components are placed end-to-end,
 *      starting at coordinate 0
 *      (the leftmost or topmost position).
 *
 * <dt> aligned
 * <dd> The components are aligned as specified
 *      by each component's X or Y alignment value.
 * </dl>
 * </blockquote>
 *
 * <p>
 *
 * Each SizeRequirements object contains information
 * about either the width (and X alignment)
 * or height (and Y alignment)
 * of a single component or a group of components:
 *
 * <blockquote>
 * <dl>
 * <dt> <code>minimum</code>
 * <dd> The smallest reasonable width/height of the component
 *      or component group, in pixels.
 *
 * <dt> <code>preferred</code>
 * <dd> The natural width/height of the component
 *      or component group, in pixels.
 *
 * <dt> <code>maximum</code>
 * <dd> The largest reasonable width/height of the component
 *      or component group, in pixels.
 *
 * <dt> <code>alignment</code>
 * <dd> The X/Y alignment of the component
 *      or component group.
 * </dl>
 * </blockquote>
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @see Component#getMinimumSize
 * @see Component#getPreferredSize
 * @see Component#getMaximumSize
 * @see Component#getAlignmentX
 * @see Component#getAlignmentY
 *
 * @version 1.27 02/06/02
 * @author Timothy Prinzing
 */
public class SizeRequirements implements Serializable {

    /**
     * The minimum size required.
     * For a component <code>comp</code>, this should be equal to either
     * <code>comp.getMinimumSize().width</code> or
     * <code>comp.getMinimumSize().height</code>.
     */
    public int minimum;

    /**
     * The preferred (natural) size.
     * For a component <code>comp</code>, this should be equal to either
     * <code>comp.getPreferredSize().width</code> or
     * <code>comp.getPreferredSize().height</code>.
     */
    public int preferred;

    /**
     * The maximum size allowed.
     * For a component <code>comp</code>, this should be equal to either
     * <code>comp.getMaximumSize().width</code> or
     * <code>comp.getMaximumSize().height</code>.
     */
    public int maximum;

    /**
     * The alignment, specified as a value between 0.0 and 1.0,
     * inclusive.
     * To specify centering, the alignment should be 0.5.
     */
    public float alignment;

    /**
     * Creates a SizeRequirements object with the minimum, preferred,
     * and maximum sizes set to zero and an alignment value of 0.5
     * (centered).
     */
    public SizeRequirements() {
	minimum = 0;
	preferred = 0;
	maximum = 0;
	alignment = 0.5f;
    }

    /**
     * Creates a SizeRequirements object with the specified minimum, preferred,
     * and maximum sizes and the specified alignment.
     *
     * @param min the minimum size >= 0
     * @param pref the preferred size >= 0
     * @param max the maximum size >= 0
     * @param a the alignment >= 0.0f && <= 1.0f
     */
    public SizeRequirements(int min, int pref, int max, float a) {
	minimum = min;
	preferred = pref;
	maximum = max;
	alignment = a > 1.0f ? 1.0f : a < 0.0f ? 0.0f : a;
    }

    /**
     * Returns a string describing the minimum, preferred, and maximum
     * size requirements, along with the alignment.
     *
     * @return the string
     */
    public String toString() {
	return "[" + minimum + "," + preferred + "," + maximum + "]@" + alignment;
    }

    /**
     * Determines the total space necessary to
     * place a set of components end-to-end.  The needs
     * of each component in the set are represented by an entry in the
     * passed-in SizeRequirements array.
     * The returned SizeRequirements object has an alignment of 0.5
     * (centered).  The space requirement is never more than
     * Integer.MAX_VALUE.
     *
     * @param children  the space requirements for a set of components.
     *   The vector may be of zero length, which will result in a
     *   default SizeRequirements object instance being passed back.
     * @return  the total space requirements.
     */
    public static SizeRequirements getTiledSizeRequirements(SizeRequirements[]
							    children) {
	SizeRequirements total = new SizeRequirements();
	for (int i = 0; i < children.length; i++) {
	    SizeRequirements req = children[i];
	    total.minimum = (int) Math.min((long) total.minimum + (long) req.minimum, Integer.MAX_VALUE);
	    total.preferred = (int) Math.min((long) total.preferred + (long) req.preferred, Integer.MAX_VALUE);
	    total.maximum = (int) Math.min((long) total.maximum + (long) req.maximum, Integer.MAX_VALUE);
	}
	return total;
    }

    /**
     * Determines the total space necessary to
     * align a set of components.  The needs
     * of each component in the set are represented by an entry in the
     * passed-in SizeRequirements array.  The total space required will
     * never be more than Integer.MAX_VALUE.
     *
     * @param children  the set of child requirements.  If of zero length,
     *  the returns result will be a default instance of SizeRequirements.
     * @return  the total space requirements.
     */
    public static SizeRequirements getAlignedSizeRequirements(SizeRequirements[]
							      children) {
	SizeRequirements totalAscent = new SizeRequirements();
	SizeRequirements totalDescent = new SizeRequirements();
	for (int i = 0; i < children.length; i++) {
	    SizeRequirements req = children[i];

	    int ascent = (int) (req.alignment * req.minimum);
	    int descent = req.minimum - ascent;
	    totalAscent.minimum = Math.max(ascent, totalAscent.minimum);
	    totalDescent.minimum = Math.max(descent, totalDescent.minimum);

	    ascent = (int) (req.alignment * req.preferred);
	    descent = req.preferred - ascent;
	    totalAscent.preferred = Math.max(ascent, totalAscent.preferred);
	    totalDescent.preferred = Math.max(descent, totalDescent.preferred);

	    ascent = (int) (req.alignment * req.maximum);
	    descent = req.maximum - ascent;
	    totalAscent.maximum = Math.max(ascent, totalAscent.maximum);
	    totalDescent.maximum = Math.max(descent, totalDescent.maximum);
	}
	int min = (int) Math.min((long) totalAscent.minimum + (long) totalDescent.minimum, Integer.MAX_VALUE);
	int pref = (int) Math.min((long) totalAscent.preferred + (long) totalDescent.preferred, Integer.MAX_VALUE);
	int max = (int) Math.min((long) totalAscent.maximum + (long) totalDescent.maximum, Integer.MAX_VALUE);
	float alignment = 0.0f;
	if (min > 0) {
	    alignment = (float) totalAscent.minimum / min;
	    alignment = alignment > 1.0f ? 1.0f : alignment < 0.0f ? 0.0f : alignment;
	}
	return new SizeRequirements(min, pref, max, alignment);
    }

    /**
     * Creates a bunch of offset/span pairs representing how to
     * lay out a set of components end-to-end.
     * This method requires that you specify
     * the total amount of space to be allocated,
     * the size requirements for each component to be placed
     * (specified as an array of SizeRequirements), and
     * the total size requirement of the set of components.
     * You can get the total size requirement
     * by invoking the getTiledSizeRequirements method.
     *
     * @param allocated the total span to be allocated >= 0.
     * @param total     the total of the children requests.  This argument
     *  is optional and may be null.
     * @param children  the size requirements for each component.
     * @param offsets   the offset from 0 for each child where
     *   the spans were allocated (determines placement of the span).
     * @param spans     the span allocated for each child to make the
     *   total target span.
     */
    public static void calculateTiledPositions(int allocated,
					       SizeRequirements total,
				               SizeRequirements[] children,
				               int[] offsets,
					       int[] spans) {
	// The total argument turns out to be a bad idea since the
	// total of all the children can overflow the integer used to
	// hold the total.  The total must therefore be calculated and
	// stored in long variables.
	long min = 0;
	long pref = 0;
	long max = 0;
	for (int i = 0; i < children.length; i++) {
	    min += children[i].minimum;
	    pref += children[i].preferred;
	    max += children[i].maximum;
	}
	if (allocated >= pref) {
	    expandedTile(allocated, min, pref, max, children, offsets, spans);
	} else {
	    compressedTile(allocated, min, pref, max, children, offsets, spans);
	}
    }

    private static void compressedTile(int allocated, long min, long pref, long max,
				       SizeRequirements[] request,
				       int[] offsets, int[] spans) {

	// ---- determine what we have to work with ----
	float totalPlay = Math.min(pref - allocated, pref - min);
	float factor = (pref - min == 0) ? 0.0f : totalPlay / (pref - min);

	// ---- make the adjustments ----
	int totalOffset = 0;
	for (int i = 0; i < spans.length; i++) {
	    offsets[i] = totalOffset;
	    SizeRequirements req = request[i];
	    int play = (int)(factor * (req.preferred - req.minimum));
	    spans[i] = req.preferred - play;
	    totalOffset = (int) Math.min((long) totalOffset + (long) spans[i], Integer.MAX_VALUE);
	}
    }

    private static void expandedTile(int allocated, long min, long pref, long max,
				     SizeRequirements[] request,
				     int[] offsets, int[] spans) {

	// ---- determine what we have to work with ----
	float totalPlay = Math.min(allocated - pref, max - pref);
	float factor = (max - pref == 0) ? 0.0f : totalPlay / (max - pref);

	// ---- make the adjustments ----
	int totalOffset = 0;
	for (int i = 0; i < spans.length; i++) {
	    offsets[i] = totalOffset;
	    SizeRequirements req = request[i];
	    int play = (int)(factor * (req.maximum - req.preferred));
	    spans[i] = (int) Math.min((long) req.preferred + (long) play, Integer.MAX_VALUE);
	    totalOffset = (int) Math.min((long) totalOffset + (long) spans[i], Integer.MAX_VALUE);
	}
    }

    /**
     * Creates a bunch of offset/span pairs specifying how to
     * lay out a set of components with the specified alignments.
     * The resulting span allocations will overlap, with each one
     * fitting as well as possible into the given total allocation.
     * This method requires that you specify
     * the total amount of space to be allocated,
     * the size requirements for each component to be placed
     * (specified as an array of SizeRequirements), and
     * the total size requirements of the set of components
     * (only the alignment field of which is actually used).
     * You can get the total size requirement by invoking
     * getAlignedSizeRequirements.
     *
     * @param allocated the total span to be allocated >= 0.
     * @param total     the total of the children requests.
     * @param children  the size requirements for each component.
     * @param offsets   the offset from 0 for each child where
     *   the spans were allocated (determines placement of the span).
     * @param spans     the span allocated for each child to make the
     *   total target span.
     */
    public static void calculateAlignedPositions(int allocated,
                                                 SizeRequirements total,
				                 SizeRequirements[] children,
				                 int[] offsets,
						 int[] spans) {
	int totalAscent = (int) (allocated * total.alignment);
	int totalDescent = allocated - totalAscent;
	for (int i = 0; i < children.length; i++) {
	    SizeRequirements req = children[i];
	    int maxAscent = (int) (req.maximum * req.alignment);
	    int maxDescent = req.maximum - maxAscent;
	    int ascent = Math.min(totalAscent, maxAscent);
	    int descent = Math.min(totalDescent, maxDescent);

	    offsets[i] = totalAscent - ascent;
	    spans[i] = (int) Math.min((long) ascent + (long) descent, Integer.MAX_VALUE);
	}
    }

    // This method was used by the JTable - which now uses a different technique. 
    /**
     * Adjust a specified array of sizes by a given amount.
     *
     * @param delta     an int specifying the size difference
     * @param children  an array of SizeRequirements objects
     * @return an array of ints containing the final size for each item
     */
    public static int[] adjustSizes(int delta, SizeRequirements[] children) {
      return new int[0];
    } 
}
