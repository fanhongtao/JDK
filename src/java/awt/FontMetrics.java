/*
 * @(#)FontMetrics.java	1.18 98/07/01
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
 * A font metrics object, which gives information about the rendering 
 * of a particular font on a particular screen. Note that the 
 * implementations of these methods are inefficient, they are usually 
 * overridden with more efficient toolkit-specific implementations.
 * <p>
 * <b>Note to subclassers</b>: Since many of these methods form closed
 * mutually recursive loops, you must take care that you implement
 * at least one of the methods in each such loop in order to prevent
 * infinite recursion when your subclass is used.
 * In particular, the following is the minimal suggested set of methods
 * to override in order to ensure correctness and prevent infinite
 * recursion (though other subsets are equally feasible):
 * <ul>
 * <li><a href=#getAscent>getAscent</a>()
 * <li><a href=#getAscent>getDescent</a>()
 * <li><a href=#getLeading>getLeading</a>()
 * <li><a href=#getMaxAdvance>getMaxAdvance</a>()
 * <li><a href="#charWidth(char)">charWidth</a>(char ch)
 * <li><a href="#charsWidth(char[], int, int)">charsWidth</a>(char data[], int off, int len)
 * </ul>
 * <p>
 * <img src="images-awt/FontMetrics-1.gif" border=15 align
 * ALIGN=right HSPACE=10 VSPACE=7>
 * When an application asks AWT to place a character at the position 
 * (<i>x</i>,&nbsp;<i>y</i>), the character is placed so that its 
 * reference point (shown as the dot in the accompanying image) is 
 * put at that position. The reference point specifies a horizontal 
 * line called the <i>baseline</i> of the character. In normal 
 * printing, the baselines of characters should align. 
 * <p> 
 * In addition, every character in a font has an <i>ascent</i>, a 
 * <i>descent</i>, and an <i>advance width</i>. The ascent is the 
 * amount by which the character ascends above the baseline. The 
 * descent is the amount by which the character descends below the 
 * baseline. The advance width indicates the position at which AWT  
 * should place the next character. 
 * <p>
 * If the current character is placed with its reference point 
 * at the position (<i>x</i>,&nbsp;<i>y</i>), and 
 * the character's advance width is <i>w</i>, then the following 
 * character is placed with its reference point at the position 
 * (<i>x&nbsp;</i><code>+</code><i>&nbsp;w</i>,&nbsp;<i>y</i>). 
 * The advance width is often the same as the width of character's 
 * bounding box, but need not be so. In particular, oblique and 
 * italic fonts often have characters whose top-right corner extends 
 * slightly beyond the advance width. 
 * <p>
 * An array of characters or a string can also have an ascent, a 
 * descent, and an advance width. The ascent of the array is the 
 * maximum ascent of any character in the array. The descent is the 
 * maximum descent of any character in the array. The advance width 
 * is the sum of the advance widths of each of the characters in the 
 * array. 
 * @version 	1.18 07/01/98
 * @author 	Jim Graham
 * @see         java.awt.Font
 * @since       JDK1.0
 */
public abstract class FontMetrics implements java.io.Serializable {
    /**
     * The actual font.
     * @see #getFont
     * @since JDK1.0
     */
    protected Font font;

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = 1681126225205050147L;

    /**
     * Creates a new <code>FontMetrics</code> object for finding out 
     * height and width information about the specified font and  
     * specific character glyphs in that font. 
     * @param     font the font
     * @see       java.awt.Font
     * @since     JDK1.0
     */
    protected FontMetrics(Font font) {
	this.font = font;
    }

    /**
     * Gets the font described by this font metric.
     * @return    the font described by this font metric.
     * @since     JDK1.0
     */
    public Font getFont() {
	return font;
    }

    /**
     * Determines the <em>standard leading</em> of the font described by 
     * this font metric. The standard leading (interline spacing) is the 
     * logical amount of space to be reserved between the descent of one 
     * line of text and the ascent of the next line. The height metric is 
     * calculated to include this extra space. 
     * @return    the standard leading of the font.
     * @see       java.awt.FontMetrics#getHeight
     * @see       java.awt.FontMetrics#getAscent
     * @see       java.awt.FontMetrics#getDescent
     * @since     JDK1.0
     */
    public int getLeading() {
	return 0;
    }

    /**
     * Determines the <em>font ascent</em> of the font described by this 
     * font metric. The font ascent is the distance from the font's 
     * baseline to the top of most alphanumeric characters. Some 
     * characters in the font may extend above the font ascent line. 
     * @return     the font ascent of the font.
     * @see        java.awt.FontMetrics#getMaxAscent
     * @since      JDK1.0
     */
    public int getAscent() {
	return font.getSize();
    }

    /**
     * Determines the <em>font descent</em> of the font described by this 
     * font metric. The font descent is the distance from the font's 
     * baseline to the bottom of most alphanumeric characters with 
     * descenders. Some characters in the font may extend below the font 
     * descent line. 
     * @return     the font descent of the font.
     * @see        java.awt.FontMetrics#getMaxDescent
     * @since      JDK1.0
     */
    public int getDescent() {
	return 0;
    }

    /**
     * Gets the standard height of a line of text in this font.  This
     * is the distance between the baseline of adjacent lines of text.
     * It is the sum of the leading + ascent + descent.  There is no
     * guarantee that lines of text spaced at this distance will be
     * disjoint; such lines may overlap if some characters overshoot
     * either the standard ascent or the standard descent metric.
     * @return    the standard height of the font.
     * @see       java.awt.FontMetrics#getLeading
     * @see       java.awt.FontMetrics#getAscent
     * @see       java.awt.FontMetrics#getDescent
     * @since     JDK1.0
     */
    public int getHeight() {
	return getLeading() + getAscent() + getDescent();
    }

    /**
     * Determines the maximum ascent of the font described by this font 
     * metric. No character extends further above the font's baseline 
     * than this height. 
     * @return    the maximum ascent of any character in the font.
     * @see       java.awt.FontMetrics#getAscent
     * @since     JDK1.0
     */
    public int getMaxAscent() {
	return getAscent();
    }

    /**
     * Determines the maximum descent of the font described by this font 
     * metric. No character extends further below the font's baseline 
     * than this height. 
     * @return    the maximum descent of any character in the font.
     * @see       java.awt.FontMetrics#getDescent
     * @since     JDK1.0
     */
    public int getMaxDescent() {
	return getDescent();
    }

    /**
     * For backward compatibility only.
     * @see #getMaxDescent
     * @deprecated As of JDK version 1.1.1,
     * replaced by <code>getMaxDescent()</code>.
     */
    public int getMaxDecent() {
	return getMaxDescent();
    }

    /**
     * Gets the maximum advance width of any character in this Font. 
     * The advance width is the amount by which the current point is
     * moved from one character to the next in a line of text.
     * @return    the maximum advance width of any character 
     *            in the font, or <code>-1</code> if the 
     *            maximum advance width is not known.
     * @since     JDK1.0
     */
    public int getMaxAdvance() {
	return -1;
    }

    /** 
     * Returns the advance width of the specified character in this Font.
     * The advance width is the amount by which the current point is
     * moved from one character to the next in a line of text.
     * @param ch the character to be measured
     * @return    the advance width of the specified <code>char</code> 
     *                 in the font described by this font metric.
     * @see       java.awt.FontMetrics#charsWidth
     * @see       java.awt.FontMetrics#stringWidth
     * @since     JDK1.0
     */
    public int charWidth(int ch) {
	return charWidth((char)ch);
    }

    /** 
     * Returns the advance width of the specified character in this Font.
     * The advance width is the amount by which the current point is
     * moved from one character to the next in a line of text.
     * @param ch the character to be measured
     * @return     the advance width of the specified <code>char</code> >
     *                  in the font described by this font metric.
     * @see        java.awt.FontMetrics#charsWidth
     * @see        java.awt.FontMetrics#stringWidth
     * @since      JDK1.0
     */
    public int charWidth(char ch) {
	if (ch < 256) {
	    return getWidths()[ch];
	}
	char data[] = {ch};
	return charsWidth(data, 0, 1);
    }

    /** 
     * Returns the total advance width for showing the specified String
     * in this Font.
     * The advance width is the amount by which the current point is
     * moved from one character to the next in a line of text.
     * @param str the String to be measured
     * @return    the advance width of the specified string 
     *                  in the font described by this font metric.
     * @see       java.awt.FontMetrics#bytesWidth
     * @see       java.awt.FontMetrics#charsWidth
     * @since     JDK1.0
     */
    public int stringWidth(String str) {
	int len = str.length();
	char data[] = new char[len];
	str.getChars(0, len, data, 0);
	return charsWidth(data, 0, len);
    }

    /** 
     * Returns the total advance width for showing the specified array
     * of characters in this Font.
     * The advance width is the amount by which the current point is
     * moved from one character to the next in a line of text.
     * @param data the array of characters to be measured
     * @param off the start offset of the characters in the array
     * @param len the number of characters to be measured from the array
     * @return    the advance width of the subarray of the specified 
     *               <code>char</code> array in the font described by 
     *               this font metric.
     * @see       java.awt.FontMetrics#charWidth(int)
     * @see       java.awt.FontMetrics#charWidth(char)
     * @see       java.awt.FontMetrics#bytesWidth
     * @see       java.awt.FontMetrics#stringWidth
     * @since     JDK1.0
     */
    public int charsWidth(char data[], int off, int len) {
	return stringWidth(new String(data, off, len));
    }

    /** 
     * Returns the total advance width for showing the specified array
     * of bytes in this Font.
     * The advance width is the amount by which the current point is
     * moved from one character to the next in a line of text.
     * @param data the array of bytes to be measured
     * @param off the start offset of the bytes in the array
     * @param len the number of bytes to be measured from the array
     * @return    the advance width of the subarray of the specified 
     *               <code>byte</code> array in the font described by 
     *               this font metric.
     * @see       java.awt.FontMetrics#charsWidth
     * @see       java.awt.FontMetrics#stringWidth
     * @since     JDK1.0
     */
    public int bytesWidth(byte data[], int off, int len) {
	return stringWidth(new String(data, 0, off, len));
    }

    /**
     * Gets the advance widths of the first 256 characters in the Font.
     * The advance width is the amount by which the current point is
     * moved from one character to the next in a line of text.
     * @return    an array giving the advance widths of the 
     *                 characters in the font 
     *                 described by this font metric.
     * @since     JDK1.0
     */
    public int[] getWidths() {
	int widths[] = new int[256];
	for (char ch = 0 ; ch < 256 ; ch++) {
	    widths[ch] = charWidth(ch);
	}
	return widths;
    }

    /** 
     * Returns a representation of this <code>FontMetric</code> 
     * object's values as a string.
     * @return    a string representation of this font metric.
     * @since     JDK1.0.
     */
    public String toString() {
	return getClass().getName() + "[font=" + getFont() + "ascent=" +
	    getAscent() + ", descent=" + getDescent() + ", height=" + getHeight() + "]";
    }
}
