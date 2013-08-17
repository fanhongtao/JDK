/*
 * @(#)Icon.java	1.11 98/08/26
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

import java.awt.Graphics;
import java.awt.Component;


/**
 * A small fixed size picture, typically used to decorate components.
 * 
 * @see ImageIcon
 */

public interface Icon 
{
    /**
     * Draw the icon at the specified location.  Icon implementations
     * may use the Component argument to get properties useful for 
     * painting, e.g. the foreground or background color.
     */
    void paintIcon(Component c, Graphics g, int x, int y);
    
    /**
     * Returns the icon's width.
     *
     * @return an int specifying the fixed width of the icon.
     */
    int getIconWidth();

    /**
     * Returns the icon's height.
     *
     * @return an int specifying the fixed height of the icon.
     */
    int getIconHeight();
}
