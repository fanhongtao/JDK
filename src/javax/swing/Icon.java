/*
 * @(#)Icon.java	1.13 00/02/02
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
