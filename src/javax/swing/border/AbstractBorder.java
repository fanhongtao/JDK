/*
 * @(#)AbstractBorder.java	1.21 98/08/28
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
import java.awt.Component;
import java.io.Serializable;

/**
 * A class which implements an empty border with no size.  
 * This provides a convenient base class from which other border 
 * classes can be easily derived.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.21 08/28/98
 * @author David Kloba
 */
public abstract class AbstractBorder implements Border, Serializable
{

    /** This default implementation does no painting. */
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
    }

    /** This default implementation returns the value of getBorderMargins. */
    public Insets getBorderInsets(Component c)       { 
        return new Insets(0, 0, 0, 0);
    }

    /** 
     * Reinitialize the insets parameter with this Border's current Insets. 
     * @param c the component for which this border insets value applies
     * @param insets the object to be reinitialized
     */
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.top = insets.right = insets.bottom = 0;
        return insets;
    }

    /** This default implementation returns false. */
    public boolean isBorderOpaque() { return false; }

    /** This is a convience method that calls the static method. */
    public Rectangle getInteriorRectangle(Component c, int x, int y, int width, int height) {
	return getInteriorRectangle(c, this, x, y, width, height);
    } 

    /** This method returns a rectangle using the arguements minus the
      * insets of the border. This is useful for determining the area
      * that components should draw in that will not intersect the border.
      */
    public static Rectangle getInteriorRectangle(Component c, Border b, int x, int y, int width, int height) {
        Insets insets;
	if(b != null)
	    insets = b.getBorderInsets(c);
	else
	    insets = new Insets(0, 0, 0, 0);
        return new Rectangle(x + insets.left,
	                            y + insets.top,
	                            width - insets.right - insets.left,
	                            height - insets.top - insets.bottom);
    }

}
