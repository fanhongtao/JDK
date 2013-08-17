/*
 * @(#)BasicPopupMenuSeparatorUI.java	1.6 98/11/05
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
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

/**
 * A Basic L&F implementation of PopupMenuSeparatorUI.  This implementation
 * is a "combined" view/controller.
 *
 * @version 1.6 11/05/98
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
