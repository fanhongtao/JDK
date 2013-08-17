/*
 * @(#)BorderFactory.java	1.15 98/10/20
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
package javax.swing;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.border.*;

/**
 * Factory class for vending standard Border objects.  Whereever
 * possible, this factory will hand out references to shared
 * Border instances.
 *
 * @version 1.15 10/20/98
 * @author David Kloba
 */
public class BorderFactory 
{

    /** Don't let anyone instantiate this class */
    private BorderFactory() {
    }


//// LineBorder ///////////////////////////////////////////////////////////////
    /**
     * Creates a line border withe the specified color.
     *
     * @param color  a Color to use for the line
     * @return the Border object
     */
    public static Border createLineBorder(Color color) {
        return new LineBorder(color, 1);
    }

    /**
     * Creates a line border withe the specified color
     * and width. The width applies to all 4 sides of the
     * border. To specify widths individually for the top,
     * bottom, left, and right, use 
     * {@link #createMatteBorder(int,int,int,int,Color)}.
     *
     * @param color  a Color to use for the line
     * @param thickness  an int specifying the width in pixels
     * @return the Border object
     */
    public static Border createLineBorder(Color color, int thickness)  {
        return new LineBorder(color, thickness);
    }
    
//    public static Border createLineBorder(Color color, int thickness, 
//					boolean drawRounded)  {
//        return new JLineBorder(color, thickness, drawRounded);
//    }

//// BevelBorder /////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
    static final Border sharedRaisedBevel = new BevelBorder(BevelBorder.RAISED);
    static final Border sharedLoweredBevel = new BevelBorder(BevelBorder.LOWERED);

    /**
     * Created a border with a raised beveled edge, using
     * brighter shades of the component's current background color
     * for highlighting, and darker shading for shadows.
     * (In a raised border, highlights are on top and shadows
     *  are underneath.)
     *
     * @return the Border object
     */
    public static Border createRaisedBevelBorder() {
        return createSharedBevel(BevelBorder.RAISED);
    }

    /**
     * Created a border with a lowered beveled edge, using
     * brighter shades of the component's current background color
     * for highlighting, and darker shading for shadows.
     * (In a lowered border, shadows are on top and highlights
     *  are underneath.)
     *
     * @return the Border object
     */
    public static Border createLoweredBevelBorder() {
        return createSharedBevel(BevelBorder.LOWERED);
    }

    /**
     * Create a beveled border of the specified type, using
     * brighter shades of the component's current background color
     * for highlighting, and darker shading for shadows.
     * (In a lowered border, shadows are on top and highlights
     *  are underneath.).
     *
     * @param type  an int specifying either BevelBorder.LOWERED
     *              or BevelBorder.LOWERED
     * @return the Border object
     */
    public static Border createBevelBorder(int type) {
	return createSharedBevel(type);
    }
	
    /**
     * Create a beveled border of the specified type, using
     * the specified highlighting and shadowing. The outer 
     * edge of the highlighted area uses a brighter shade of
     * the highlight color. The inner edge of the shadow area
     * uses a brighter shade of the shadaw color.
     * 
     * @param type  an int specifying either BevelBorder.LOWERED
     *              or BevelBorder.LOWERED
     * @param highlight  a Color object for highlights
     * @param shadow     a Color object for shadows
     * @return the Border object
     */
    public static Border createBevelBorder(int type, Color highlight, Color shadow) {
        return new BevelBorder(type, highlight, shadow);
    }

    /**
     * Create a beveled border of the specified type, using
     * the specified colors for the inner and outer highlight
     * and shadow areas. 
     * 
     * @param type  an int specifying either BevelBorder.LOWERED
     *              or BevelBorder.LOWERED
     * @param highlightOuter  a Color object for the outer edge of the highlight area
     * @param highlightInner  a Color object for the inner edge of the highlight area
     * @param shadowOuter     a Color object for the outer edge of the shadow area
     * @param shadowInner     a Color object for the inner edge of the shadow area
     * @return the Border object
     */
    public static Border createBevelBorder(int type,
                        Color highlightOuter, Color highlightInner,
                        Color shadowOuter, Color shadowInner) {
        return new BevelBorder(type, highlightOuter, highlightInner, 
					shadowOuter, shadowInner);
    }

    static Border createSharedBevel(int type)	{
	if(type == BevelBorder.RAISED) {
	    return sharedRaisedBevel;
	} else if(type == BevelBorder.LOWERED) {
	    return sharedLoweredBevel;
	}
	return null;
    }
//// EtchedBorder ///////////////////////////////////////////////////////////
    static final Border sharedEtchedBorder = new EtchedBorder();

    /**
     * Create a border with an "etched" look using
     * the component's current background color for 
     * highlighting and shading.
     *
     * @return the Border object
     */
    public static Border createEtchedBorder()    {
	return sharedEtchedBorder;
    }

    /**
     * Create a border with an "etched" look using
     * the specified highlighting and shading colors.
     *
     * @param highlight  a Color object for the border highlights
     * @param shadow     a Color object for the border shadows
     * @return the Border object 
     */
    public static Border createEtchedBorder(Color highlight, Color shadow)    {
        return new EtchedBorder(highlight, shadow);
    }

//// TitledBorder ////////////////////////////////////////////////////////////
    /**
     * Create a new title border specifying the text of the title, using
     * the default border (etched), using the default text position (sitting on the top
     * line) and default justification (left) and using the default
     * font and text color determined by the current look and feel.
     *
     * @param title               a String containing the text of the title
     * @return the TitledBorder object
     */
    public static TitledBorder createTitledBorder(String title)     {
        return new TitledBorder(title);
    }

    /**
     * Create a new title border with an empty title specifying the
     * border object, using the default text position (sitting on the top
     * line) and default justification (left) and using the default
     * font, text color, and border determined by the current look and feel.
     * (The Motif and Windows look and feels use an etched border;
     * The Java look and feel use a gray border.)
     *
     * @param border  the Border object to add the title to
     * @return the TitledBorder object
     */
    public static TitledBorder createTitledBorder(Border border)       {
        return new TitledBorder(border);
    }

    /**
     * Add a title to an existing border, specifying the text of
     * the title, using the default positioning (sitting on the top
     * line) and default justification (left) and using the default
     * font and text color determined by the current look and feel.
     *
     * @param border              the Border object to add the title to
     * @param title               a String containing the text of the title
     * @return the TitledBorder object
     */
    public static TitledBorder createTitledBorder(Border border, 
						   String title) {
        return new TitledBorder(border, title);
    }

    /**
     * Add a title to an existing border, specifying the text of
     * the title along with its positioning, using the default
     * font and text color determined by the current look and feel.
     *
     * @param border              the Border object to add the title to
     * @param title               a String containing the text of the title
     * @param titleJustification  an int specifying the left/right position
     *        of the title -- one of TitledBorder.LEFT, TitledBorder.CENTER,
     *        or TitledBorder.RIGHT, TitledBorder.DEFAULT_JUSTIFICATION (left).
     * @param titlePosition       an int specifying the vertical position of
     *        the text in relation to the border -- one of: TitledBorder.ABOVE_TOP, TitledBorder.TOP
     *        (sitting on the top line), TitledBorder.BELOW_TOP, TitledBorder.ABOVE_BOTTOM, TitledBorder.BOTTOM
     *        (sitting on the bottom line), TitledBorder.BELOW_BOTTOM, or 
     *        TitledBorder.DEFAULT_POSITION (top).
     * @return the TitledBorder object
     */
    public static TitledBorder createTitledBorder(Border border, 
                        String title,
                        int titleJustification,
                        int titlePosition)      {
        return new TitledBorder(border, title, titleJustification,
                        titlePosition);
    }

    /**
     * Add a title to an existing border, specifying the text of
     * the title along with its positioning and font, using the
     * default text color determined by the current look and feel.
     *
     * @param border              the Border object to add the title to
     * @param title               a String containing the text of the title
     * @param titleJustification  an int specifying the left/right position
     *        of the title -- one of TitledBorder.LEFT, TitledBorder.CENTER,
     *        or TitledBorder.RIGHT, TitledBorder.DEFAULT_JUSTIFICATION (left).
     * @param titlePosition       an int specifying the vertical position of
     *        the text in relation to the border -- one of: TitledBorder.ABOVE_TOP, TitledBorder.TOP
     *        (sitting on the top line), TitledBorder.BELOW_TOP, TitledBorder.ABOVE_BOTTOM, TitledBorder.BOTTOM
     *        (sitting on the bottom line), TitledBorder.BELOW_BOTTOM, or 
     *        TitledBorder.DEFAULT_POSITION (top).
     * @param titleFont           a Font object specifying the title font
     * @return the TitledBorder object
     */
    public static TitledBorder createTitledBorder(Border border,       	
                        String title,
                        int titleJustification,
                        int titlePosition,
                        Font titleFont) {
        return new TitledBorder(border, title, titleJustification,
                        titlePosition, titleFont);
    }

    /**
     * Add a title to an existing border, specifying the text of
     * the title along with its positioning, font, and color.
     *
     * @param border              the Border object to add the title to
     * @param title               a String containing the text of the title
     * @param titleJustification  an int specifying the left/right position
     *        of the title -- one of TitledBorder.LEFT, TitledBorder.CENTER,
     *        or TitledBorder.RIGHT, TitledBorder.DEFAULT_JUSTIFICATION (left).
     * @param titlePosition       an int specifying the vertical position of
     *        the text in relation to the border -- one of: TitledBorder.ABOVE_TOP, TitledBorder.TOP
     *        (sitting on the top line), TitledBorder.BELOW_TOP, TitledBorder.ABOVE_BOTTOM, TitledBorder.BOTTOM
     *        (sitting on the bottom line), TitledBorder.BELOW_BOTTOM, or 
     *        TitledBorder.DEFAULT_POSITION (top).
     * @param titleFont           a Font object specifying the title font
     * @param titleColor          a Color object specifying the title color
     * @return the TitledBorder object
     */
    public static TitledBorder createTitledBorder(Border border,                     
                        String title,
                        int titleJustification,
                        int titlePosition,
                        Font titleFont,
                        Color titleColor)       {
        return new TitledBorder(border, title, titleJustification,
                        titlePosition, titleFont, titleColor);
    }
//// EmptyBorder ///////////////////////////////////////////////////////////	
    final static Border emptyBorder = new EmptyBorder(0, 0, 0, 0);

    /**
     * Creates an empty border that takes up no space. (The width
     * of the top, bottom, left, and right sides are all zero.)
     *
     * @return the Border object
     */
    public static Border createEmptyBorder() {
	return emptyBorder;
    }

    /**
     * Creates an empty border that takes up no space but which does
     * no drawing, specifying the width of the top, left, bottom, and
     * right sides.
     *
     * @param top     an int specifying the width of the top in pixels
     * @param left    an int specifying the width of the left side in pixels
     * @param bottom  an int specifying the width of the right side in pixels
     * @param right   an int specifying the width of the bottom in pixels
     * @return the Border object
     */
    public static Border createEmptyBorder(int top, int left, 
						int bottom, int right) {
	return new EmptyBorder(top, left, bottom, right);
    }

//// CompoundBorder ////////////////////////////////////////////////////////
    /**
     * Create a compound border with a null inside edge and a null
     * outside edge.
     *
     * @return the CompoundBorder object
     */
    public static CompoundBorder createCompoundBorder() { 
	return new CompoundBorder(); 
    }

    /**
     * Create a compound border specifying the border objects to use
     * for the outside and inside edges.
     *
     * @param outsideBorder  a Border object for the outer edge of the compound border
     * @param insideBorder   a Border object for the inner edge of the compound border
     * @return the CompoundBorder object
     */
    public static CompoundBorder createCompoundBorder(Border outsideBorder, 
						Border insideBorder) { 
	return new CompoundBorder(outsideBorder, insideBorder); 
    }

//// MatteBorder ////////////////////////////////////////////////////////
    /**
     * Create a matte-look border using a solid color. (The difference between
     * this border and a line border is that you can specify the individual
     * border dimensions.)
     *
     * @param top     an int specifying the width of the top in pixels
     * @param left    an int specifying the width of the left side in pixels
     * @param bottom  an int specifying the width of the right side in pixels
     * @param right   an int specifying the width of the bottom in pixels
     * @param color   a Color to use for the border
     * @return the MatteBorder object 
     */
    public static MatteBorder createMatteBorder(int top, int left, int bottom, int right, 
                                                Color color) {
        return new MatteBorder(top, left, bottom, right, color);
    }

    /**
     * Create a matte-look border that consists of multiple tiles of a 
     * specified icon. Multiple copies of the icon are placed side-by-side
     * to fill up the border area.
     * <p>
     * Note:<br> 
     * If the icon doesn't load, the border area is painted gray.
     *
     * @param top     an int specifying the width of the top in pixels
     * @param left    an int specifying the width of the left side in pixels
     * @param bottom  an int specifying the width of the right side in pixels
     * @param right   an int specifying the width of the bottom in pixels
     * @param tileIcon  the Icon object used for the border tiles
     * @return the MatteBorder object
     */
    public static MatteBorder createMatteBorder(int top, int left, int bottom, int right, 
                                                Icon tileIcon) {
        return new MatteBorder(top, left, bottom, right, tileIcon);
    }
}
