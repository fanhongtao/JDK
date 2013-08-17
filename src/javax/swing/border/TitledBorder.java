/*
 * @(#)TitledBorder.java	1.22 98/08/28
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
package javax.swing.border;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.UIManager;

/**
 * A class which implements an arbitrary border
 * with the addition of a String title in a
 * specified position and justification.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.22 08/28/98 
 * @author David Kloba
 */
public class TitledBorder extends AbstractBorder
{
    protected String title;
    protected Border border;
    protected int    titlePosition;
    protected int    titleJustification;
    protected Font   titleFont;
    protected Color  titleColor;

    /**
     * Use the default vertical orientation for the title text.
     * (The default value depends on the look and feel.)
     */
    static public final int     DEFAULT_POSITION        = 0;
    /** Position the title above the border's top line. */
    static public final int     ABOVE_TOP       = 1;
    /** Position the title in the middle of the border's top line. */
    static public final int     TOP             = 2;
    /** Position the title below the border's top line. */
    static public final int     BELOW_TOP       = 3;
    /** Position the title above the border's bottom line. */
    static public final int     ABOVE_BOTTOM    = 4;
    /** Position the title in the middle of the border's bottom line. */
    static public final int     BOTTOM          = 5;
    /** Position the title below the border's bottom line. */
    static public final int     BELOW_BOTTOM    = 6;

    /**
     * Use the default justification for the title text.
     * (The default value depends on the look and feel.)
     */
    static public final int     DEFAULT_JUSTIFICATION   = 0;
    /** Position title text at the left side of the border line. */
    static public final int     LEFT    = 1;
    /** Position title text in the center of the border line. */
    static public final int     CENTER  = 2;
    /** Position title text at the right side of the border line. */
    static public final int     RIGHT   = 3;

    // Space between the border and the component's edge
    static protected final int EDGE_SPACING = 2;

    // Space between the border and text
    static protected final int TEXT_SPACING = 2;

    // Horizontal inset of text that is left or right justified
    static protected final int TEXT_INSET_H = 5;

    /**
     * Creates a TitledBorder instance which uses an etched border.
     * 
     * @param title  the title the border should display
     */
    public TitledBorder(String title)     {
        this(null, title, LEFT, TOP, null, null);
    }

    /**
     * Creates a TitledBorder instance with the specified border
     * and an empty title.
     * 
     * @param border  the border
     */
    public TitledBorder(Border border)       {
        this(border, "", LEFT, TOP, null, null);
    }

    /**
     * Creates a TitledBorder instance with the specified border
     * and title.
     * 
     * @param border  the border
     * @param title  the title the border should display
     */
    public TitledBorder(Border border, String title) {
        this(border, title, LEFT, TOP, null, null);
    }

    /**
     * Creates a TitledBorder instance with the specified border,
     * title, title-justification, and title-position.
     * 
     * @param border  the border
     * @param title  the title the border should display
     * @param titleJustification the justification for the title
     * @param titlePosition the position for the title
     */
    public TitledBorder(Border border, 
                        String title,
                        int titleJustification,
                        int titlePosition)      {
        this(border, title, titleJustification,
                        titlePosition, null, null);
    }

    /**
     * Creates a TitledBorder instance with the specified border,
     * title, title-justification, title-position, and title-font.
     * 
     * @param border  the border
     * @param title  the title the border should display
     * @param titleJustification the justification for the title
     * @param titlePosition the position for the title
     * @param titleFont the font for rendering the title
     */
    public TitledBorder(Border border, 			
                        String title,
                        int titleJustification,
                        int titlePosition,
                        Font titleFont) {
        this(border, title, titleJustification,
                        titlePosition, titleFont, null);
    }

    /**
     * Creates a TitledBorder instance with the specified border,
     * title, title-justification, title-position, title-font, and
     * title-color.
     * 
     * @param border  the border
     * @param title  the title the border should display
     * @param titleJustification the justification for the title
     * @param titlePosition the position for the title
     * @param titleFont the font of the title
     * @param titleColor the color of the title
     */
    public TitledBorder(Border border,                     
                        String title,
                        int titleJustification,
                        int titlePosition,
                        Font titleFont,
                        Color titleColor)       {
        this.title = title;
        this.border = border;
        this.titleFont = titleFont;
        this.titleColor = titleColor;

        setTitleJustification(titleJustification);
        setTitlePosition(titlePosition);
    }

    /**
     * Paints the border for the specified component with the 
     * specified position and size.
     * @param c the component for which this border is being painted
     * @param g the paint graphics
     * @param x the x position of the painted border
     * @param y the y position of the painted border
     * @param width the width of the painted border
     * @param height the height of the painted border
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

        if (getTitle() == null || getTitle().equals("")) {
            getBorder().paintBorder(c, g, x, y, width, height);
            return;
        }

        Rectangle grooveRect = new Rectangle(x + EDGE_SPACING, y + EDGE_SPACING,
                                             width - (EDGE_SPACING * 2),
                                             height - (EDGE_SPACING * 2));
        Font font = g.getFont();
        Color color = g.getColor();

        g.setFont(getFont(c));

        FontMetrics fm = g.getFontMetrics();
        int         fontHeight = fm.getHeight();
        int         descent = fm.getDescent();
        int         ascent = fm.getAscent();
        Point       textLoc = new Point();
        int         diff;
        int         stringWidth = fm.stringWidth(getTitle());
        Insets      insets;

        if(getBorder() != null)
            insets = getBorder().getBorderInsets(c);
        else
            insets = new Insets(0, 0, 0, 0);

        switch (getTitlePosition()) {
            case ABOVE_TOP:
                diff = ascent + descent + (Math.max(EDGE_SPACING,
                                TEXT_SPACING*2) - EDGE_SPACING);
                grooveRect.y += diff;
                grooveRect.height -= diff;
                textLoc.y = grooveRect.y - (descent + TEXT_SPACING);
                break;
            case TOP:
            case DEFAULT_POSITION:
                diff = Math.max(0, ((ascent/2) + TEXT_SPACING) - EDGE_SPACING);
                grooveRect.y += diff;
                grooveRect.height -= diff;
                textLoc.y = (grooveRect.y - descent) +
                (insets.top + ascent + descent)/2;
                break;
            case BELOW_TOP:
                textLoc.y = grooveRect.y + insets.top + ascent + TEXT_SPACING;
                break;
            case ABOVE_BOTTOM:
                textLoc.y = (grooveRect.y + grooveRect.height) -
                (insets.bottom + descent + TEXT_SPACING);
                break;
            case BOTTOM:
                grooveRect.height -= fontHeight/2;
                textLoc.y = ((grooveRect.y + grooveRect.height) - descent) +
                        ((ascent + descent) - insets.bottom)/2;
                break;
            case BELOW_BOTTOM:
                grooveRect.height -= fontHeight;
                textLoc.y = grooveRect.y + grooveRect.height + ascent +
                        TEXT_SPACING;
                break;
        }

        getBorder().paintBorder(c, g, grooveRect.x, grooveRect.y,
                                  grooveRect.width, grooveRect.height);

        switch (getTitleJustification()) {
            case LEFT:
            case DEFAULT_JUSTIFICATION:
                textLoc.x = grooveRect.x + TEXT_INSET_H + insets.left;
                break;
            case RIGHT:
                textLoc.x = (grooveRect.x + grooveRect.width) -
                        (stringWidth + TEXT_INSET_H + insets.right);
                break;
            case CENTER:
                textLoc.x = grooveRect.x +
                        ((grooveRect.width - stringWidth) / 2);
                break;
        }
	g.setColor(c.getBackground());
        g.fillRect(textLoc.x - TEXT_SPACING, textLoc.y - (fontHeight-descent),
                   stringWidth + (2 * TEXT_SPACING), fontHeight - descent);
        g.setColor(getTitleColor());
        g.drawString(getTitle(), textLoc.x, textLoc.y);

        g.setFont(font);
        g.setColor(color);
    }

    /**
     * Returns the insets of the border.
     * @param c the component for which this border insets value applies
     */
    public Insets getBorderInsets(Component c) {
        return getBorderInsets(c, new Insets(0, 0, 0, 0));
    }

    /** 
     * Reinitialize the insets parameter with this Border's current Insets. 
     * @param c the component for which this border insets value applies
     * @param insets the object to be reinitialized
     */
    public Insets getBorderInsets(Component c, Insets insets) {
        FontMetrics fm;
        int         descent = 0;
        int         ascent = 16;

        Border border = getBorder();
        if (border != null) {
            if (border instanceof AbstractBorder) {
                insets = ((AbstractBorder)border).getBorderInsets(c, insets);
            } else {
                // Can't reuse border insets because the Border interface
                // can't be enhanced.
                insets = border.getBorderInsets(c);
            }
        } else {
            insets.left = insets.top = insets.right = insets.bottom = 0;
        }

        insets.left += EDGE_SPACING + TEXT_SPACING;
        insets.right += EDGE_SPACING + TEXT_SPACING;
        insets.top += EDGE_SPACING + TEXT_SPACING;
        insets.bottom += EDGE_SPACING + TEXT_SPACING;

        if(c == null || getTitle() == null || getTitle().equals(""))    {
            return insets;
        }

        Font font = getFont(c);

        fm = c.getFontMetrics(font);

	if(fm != null) {
  	   descent = fm.getDescent();
	   ascent = fm.getAscent();
	}

        switch (getTitlePosition()) {
          case ABOVE_TOP:
              insets.top += ascent + descent
                            + (Math.max(EDGE_SPACING, TEXT_SPACING*2)
                            - EDGE_SPACING);
              break;
          case TOP:
          case DEFAULT_POSITION:
              insets.top += ascent + descent;
              break;
          case BELOW_TOP:
              insets.top += ascent + descent + TEXT_SPACING;
              break;
          case ABOVE_BOTTOM:
              insets.bottom += ascent + descent + TEXT_SPACING;
              break;
          case BOTTOM:
              insets.bottom += ascent + descent;
              break;
          case BELOW_BOTTOM:
              insets.bottom += ascent + TEXT_SPACING;
              break;
        }
        return insets;
    }

    /**
     * Returns whether or not the border is opaque.
     */
    public boolean isBorderOpaque() { return false; }

    /**
     * Returns the title of the titled border.
     */
    public String getTitle()        {       return title;   }

    /**
     * Returns the border of the titled border.
     */
    public Border getBorder()       {       
        Border b = border;
	if (b == null)
	    b = UIManager.getBorder("TitledBorder.border");
        return b; 
    }

    /**
     * Returns the title-position of the titled border.
     */
    public int getTitlePosition()   {       return titlePosition;   }

    /**
     * Returns the title-justification of the titled border.
     */
    public int getTitleJustification()      {       return titleJustification;      }

    /**
     * Returns the title-font of the titled border.
     */
    public Font getTitleFont()      {       
        Font f = titleFont;
	if (f == null)
	    f = UIManager.getFont("TitledBorder.font");
        return f;       
    }

    /**
     * Returns the title-color of the titled border.
     */
    public Color getTitleColor()    {       
        Color c = titleColor;
	if (c == null)
	    c = UIManager.getColor("TitledBorder.titleColor");
        return c;  
    }


    // REMIND(aim): remove all or some of these set methods?

    /**
     * Sets the title of the titled border.
     * param title the title for the border
     */
    public void setTitle(String title)      {       this.title = title;     }

    /**
     * Sets the border of the titled border.
     * @param border the border
     */
    public void setBorder(Border border)    {       this.border = border;   }

    /**
     * Sets the title-position of the titled border.
     * @param titlePosition the position for the border
     */
    public void setTitlePosition(int titlePosition) {
        switch (titlePosition) {
          case ABOVE_TOP:
          case TOP:
          case BELOW_TOP:
          case ABOVE_BOTTOM:
          case BOTTOM:
          case BELOW_BOTTOM:
          case DEFAULT_POSITION:
                this.titlePosition = titlePosition;
                break;
          default:
            throw new IllegalArgumentException(titlePosition +
                                        " is not a valid title position.");
        }
    }

    /**
     * Sets the title-justification of the titled border.
     * @param titleJustification the justification for the border
     */
    public void setTitleJustification(int titleJustification)       {
        switch (titleJustification) {
          case LEFT:
          case CENTER:
          case RIGHT:
          case DEFAULT_JUSTIFICATION:
            this.titleJustification = titleJustification;
            break;
          default:
            throw new IllegalArgumentException(titleJustification +
                                        " is not a valid title justification.");
        }
    }

    /**
     * Sets the title-font of the titled border.
     * @param titleFont the font for the border title
     */
    public void setTitleFont(Font titleFont) {       
        this.titleFont = titleFont;     
    }

    /**
     * Sets the title-color of the titled border.
     * @param titleColor the color for the border title
     */
    public void setTitleColor(Color titleColor) {       
      this.titleColor = titleColor;   
    }

    /**
     * Returns the minimum dimensions this border requires
     * in order to fully display the border and title.
     * @param c the component where this border will be drawn
     */
    public Dimension getMinimumSize(Component c) {
        Insets insets = getBorderInsets(c);
        Dimension minSize = new Dimension(insets.right+insets.left, 
                                          insets.top+insets.bottom);
        Font font = getFont(c);
        FontMetrics fm = c.getFontMetrics(font);
        switch (titlePosition) {
          case ABOVE_TOP:
          case BELOW_BOTTOM:
              minSize.width = Math.max(fm.stringWidth(getTitle()), minSize.width);
              break;
          case BELOW_TOP:
          case ABOVE_BOTTOM:
          case TOP:
          case BOTTOM:
          case DEFAULT_POSITION:       
          default:
              minSize.width += fm.stringWidth(getTitle());
        }
        return minSize;       
    }

    protected Font getFont(Component c) {
        Font font;
        if ((font = getTitleFont()) != null) {
            return font;
        } else if (c != null && (font = c.getFont()) != null) {
            return font;
        } 
        return new Font("Dialog", Font.PLAIN, 12);
    }       
}
