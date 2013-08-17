/*
 * @(#)LineMetrics.java	1.8 98/10/19
 *
 * Copyright 1998-1998 by Sun Microsystems, Inc.,
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
* @(#)LineMetrics.java	1.4 98/09/21
*/

/**
* The <code>LineMetrics</code> class allows access to the
* metrics needed to layout characters along a line
* and to layout of a set of lines.  A <code>LineMetrics</code>
* object encapsulates the measurement information associated
* with a {@link java.awt.Font Font} object.
*/
package java.awt.font;

public abstract class LineMetrics {

    /**
     * Returns the number of characters in the <code>Font</code>.
     * @return the number of characters in the <code>Font</code>.
     */
    public abstract int getNumChars();

    /**
     * Returns the ascent of the <code>Font</code>.  The ascent
     * of a <code>Font</code> is the distance from the baseline
     * to the ascender line.  The ascent usually represents the
     * the height of the capital letters of the <code>Font</code>.
     * @return the ascent of the <code>Font</code>.
     */
    public abstract float getAscent();

    /**
     * Returns the descent of the <code>Font</code>.  The descent
     * of a <code>Font</code> is the distance from the baseline 
     * to the descender line.  The characters of the 
     * <code>Font</code> usually do not extend below the 
     * descender line.
     * @return the descent of the <code>Font</code>.
     */
    public abstract float getDescent();

    /**
     * Returns the leading of the <code>Font</code>. The
     * leading of a <code>Font</code> is the recommended 
     * distance from the bottom of the descender line to the
     * top of next line.
     * @return the leading of the <code>Font</code>.
     */
    public abstract float getLeading();

    /**
     * Returns the height of the <code>Font</code>.  The
     * height is equal to the sum of the ascent, the
     * descent and the leading.
     * @return the height of the <code>Font</code>.
     */
    public abstract float getHeight();

    /**
     * Returns the current baseline index used for
     * laying out the text of the <code>Font</code>.
     * @return the baseline index of the <code>Font</code>.
     */
    public abstract int getBaselineIndex();

    /**
     * Returns the baseline offsets as defined in the
     * <code>Font</code>, relative to y=0.
     * @return the baseline offsets of the <code>Font</code>.
     */
    public abstract float[] getBaselineOffsets();

    /**
     * Returns the position of the strike through line 
     * relative to the baseline.
     * @return the position of the strike through.
     */
    public abstract float getStrikethroughOffset();

    /**
     * Returns the thickness of the strike through line.
     * @return the thickness of the strike through line.         
     */
    public abstract float getStrikethroughThickness();

    /**
     * Returns the position of the underline relative to
     * the descender.   
     * @return the position of the underline.
     */
    public abstract float getUnderlineOffset();

    /**
     * Returns the thickness of the underline.
     * @return the thickness of the underline.
     */
    public abstract float getUnderlineThickness();
}
