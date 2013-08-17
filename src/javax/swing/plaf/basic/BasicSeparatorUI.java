/*
 * @(#)BasicSeparatorUI.java	1.17 98/08/26
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

package javax.swing.plaf.basic;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SeparatorUI;


/**
 * A Basic L&F implementation of SeparatorUI.  This implementation 
 * is a "combined" view/controller.
 *
 * @version 1.17 08/26/98
 * @author Georges Saab
 * @author Jeff Shapiro
 */

public class BasicSeparatorUI extends SeparatorUI
{
    protected Color shadow;
    protected Color highlight;

    public static ComponentUI createUI( JComponent c )
    {
        return new BasicSeparatorUI();
    }

    public void installUI( JComponent c )
    {
        installDefaults( (JSeparator)c );
        installListeners( (JSeparator)c );
    }

    public void uninstallUI(JComponent c)
    {
        uninstallDefaults( (JSeparator)c );
        uninstallListeners( (JSeparator)c );
    }

    protected void installDefaults( JSeparator s )
    {
        shadow = UIManager.getColor( "Separator.shadow" );
        highlight = UIManager.getColor( "Separator.highlight" );
    }

    protected void uninstallDefaults( JSeparator s )
    {
        shadow = null;
        highlight = null;
    }

    protected void installListeners( JSeparator s )
    {
    }

    protected void uninstallListeners( JSeparator s )
    {
    }

    public void paint( Graphics g, JComponent c )
    {
        Dimension s = c.getSize();

	BasicGraphicsUtils.drawGroove( g, 0, 0, s.width, s.height, shadow, highlight );
    }

    public Dimension getPreferredSize( JComponent c )
    { 
	if ( ((JSeparator)c).getOrientation() == JSeparator.VERTICAL )
	    return new Dimension( 2, 0 );
	else
	    return new Dimension( 0, 2 );
    }

    public Dimension getMinimumSize( JComponent c ) { return null; }
    public Dimension getMaximumSize( JComponent c ) { return null; }
}




