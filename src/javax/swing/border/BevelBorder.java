/*
 * @(#)BevelBorder.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package javax.swing.border;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Component;


/**
 * A class which implements a simple 2 line bevel border.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.11 08/28/98
 * @author David Kloba
 */
public class BevelBorder extends AbstractBorder
{
    /** Raised bevel type. */
    public static final int RAISED  = 0;
    /** Lowered bevel type. */
    public static final int LOWERED = 1;

    protected int bevelType;
    protected Color highlightOuter;
    protected Color highlightInner;
    protected Color shadowInner;
    protected Color shadowOuter;

    /**
     * Creates a bevel border with the specified type and whose
     * colors will be derived from the background color of the
     * component passed into the paintBorder method.
     * @param bevelType the type of bevel for the border
     */
    public BevelBorder(int bevelType) {
        this.bevelType = bevelType;
    }

    /**
     * Creates a bevel border with the specified type, highlight and
     * shadow colors.
     * @param bevelType the type of bevel for the border
     * @param highlight the color to use for the bevel highlight
     * @param shadow the color to use for the bevel shadow
     */
    public BevelBorder(int bevelType, Color highlight, Color shadow) {
        this(bevelType, highlight.brighter(), highlight, shadow, shadow.brighter());
    }

    /**
     * Creates a bevel border with the specified type, highlight
     * shadow colors.
     * @param bevelType the type of bevel for the border
     * @param highlightOuter the color to use for the bevel outer highlight
     * @param highlightInner the color to use for the bevel inner highlight
     * @param shadowOuter the color to use for the bevel outer shadow
     * @param shadowInner the color to use for the bevel inner shadow
     */
    public BevelBorder(int bevelType, Color highlightOuter, Color highlightInner,
                        Color shadowOuter, Color shadowInner) {
        this(bevelType);
        this.highlightOuter = highlightOuter;
        this.highlightInner = highlightInner;
        this.shadowOuter = shadowOuter;
        this.shadowInner = shadowInner;
    }

    /**
     * Paints the border for the specified component with the specified
     * position and size.
     * @param c the component for which this border is being painted
     * @param g the paint graphics
     * @param x the x position of the painted border
     * @param y the y position of the painted border
     * @param width the width of the painted border
     * @param height the height of the painted border
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (bevelType == RAISED) {
             paintRaisedBevel(c, g, x, y, width, height);

        } else if (bevelType == LOWERED) {
             paintLoweredBevel(c, g, x, y, width, height);
        }
    }

    /**
     * Returns the insets of the border.
     * @param c the component for which this border insets value applies
     */
    public Insets getBorderInsets(Component c)       {
	return new Insets(2, 2, 2, 2);
    }

    /** 
     * Reinitialize the insets parameter with this Border's current Insets. 
     * @param c the component for which this border insets value applies
     * @param insets the object to be reinitialized
     */
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.top = insets.right = insets.bottom = 2;
        return insets;
    }

    /**
     * Returns the outer highlight color of the bevel border.
     */
    public Color getHighlightOuterColor(Component c)   {
        return highlightOuter != null? highlightOuter : 
                                       c.getBackground().brighter().brighter();
    }

    /**
     * Returns the inner highlight color of the bevel border.
     */
    public Color getHighlightInnerColor(Component c)   {
        return highlightInner != null? highlightInner :
                                       c.getBackground().brighter();
    }

    /**
     * Returns the inner shadow color of the bevel border.
     */
    public Color getShadowInnerColor(Component c)      {
        return shadowInner != null? shadowInner :
                                    c.getBackground().darker();
    }

    /**
     * Returns the outer shadow color of the bevel border.
     */
    public Color getShadowOuterColor(Component c)      {
        return shadowOuter != null? shadowOuter :
                                    c.getBackground().darker().darker();
    }

    /**
     * Returns the type of the bevel border.
     */
    public int getBevelType()       {
        return bevelType;
    }

    /**
     * Returns whether or not the border is opaque.
     */
    public boolean isBorderOpaque() { return true; }

    protected void paintRaisedBevel(Component c, Graphics g, int x, int y,
                                    int width, int height)  {
        Color oldColor = g.getColor();
        int h = height;
        int w = width;

        g.translate(x, y);

        g.setColor(getHighlightOuterColor(c));
        g.drawLine(0, 0, 0, h-1);
        g.drawLine(1, 0, w-1, 0);

        g.setColor(getHighlightInnerColor(c));
        g.drawLine(1, 1, 1, h-2);
        g.drawLine(2, 1, w-2, 1);

        g.setColor(getShadowOuterColor(c));
        g.drawLine(1, h-1, w-1, h-1);
        g.drawLine(w-1, 1, w-1, h-2);

        g.setColor(getShadowInnerColor(c));
        g.drawLine(2, h-2, w-2, h-2);
        g.drawLine(w-2, 2, w-2, h-3);

        g.translate(-x, -y);
        g.setColor(oldColor);

    }

    protected void paintLoweredBevel(Component c, Graphics g, int x, int y,
                                        int width, int height)  {
        Color oldColor = g.getColor();
        int h = height;
        int w = width;

        g.translate(x, y);

        g.setColor(getShadowInnerColor(c));
        g.drawLine(0, 0, 0, h-1);
        g.drawLine(1, 0, w-1, 0);

        g.setColor(getShadowOuterColor(c));
        g.drawLine(1, 1, 1, h-2);
        g.drawLine(2, 1, w-2, 1);

        g.setColor(getHighlightOuterColor(c));
        g.drawLine(1, h-1, w-1, h-1);
        g.drawLine(w-1, 1, w-1, h-2);

        g.setColor(getHighlightInnerColor(c));
        g.drawLine(2, h-2, w-2, h-2);
        g.drawLine(w-2, 2, w-2, h-3);

        g.translate(-x, -y);
        g.setColor(oldColor);

    }

}
