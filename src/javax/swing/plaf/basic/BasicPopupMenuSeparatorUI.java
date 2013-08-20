/*
 * @(#)BasicPopupMenuSeparatorUI.java	1.11 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.plaf.ComponentUI;

/**
 * A Basic L&F implementation of PopupMenuSeparatorUI.  This implementation
 * is a "combined" view/controller.
 *
 * @version 1.11 12/19/03
 * @author Jeff Shapiro
 */

public class BasicPopupMenuSeparatorUI extends BasicSeparatorUI
{
    public static ComponentUI createUI( JComponent c )
    {
        return new BasicPopupMenuSeparatorUI();
    }

    public void paint( Graphics g, JComponent c )
    {
        Dimension s = c.getSize();
	
	g.setColor( c.getForeground() );
	g.drawLine( 0, 0, s.width, 0 );

	g.setColor( c.getBackground() );
	g.drawLine( 0, 1, s.width, 1 );
    }

    public Dimension getPreferredSize( JComponent c )
    {
        return new Dimension( 0, 2 );
    }

}
