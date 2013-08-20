/*
 * @(#)MetalPopupMenuSeparatorUI.java	1.8 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.metal;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.plaf.*;


/**
 * A Metal L&F implementation of PopupMenuSeparatorUI.  This implementation 
 * is a "combined" view/controller.
 *
 * @version 1.8 12/19/03
 * @author Jeff Shapiro
 */

public class MetalPopupMenuSeparatorUI extends MetalSeparatorUI
{
    public static ComponentUI createUI( JComponent c )
    {
        return new MetalPopupMenuSeparatorUI();
    }

    public void paint( Graphics g, JComponent c )
    {
        Dimension s = c.getSize();

        g.setColor( c.getForeground() );
        g.drawLine( 0, 1, s.width, 1 );

        g.setColor( c.getBackground() );
        g.drawLine( 0, 2, s.width, 2 );
        g.drawLine( 0, 0, 0, 0 );
        g.drawLine( 0, 3, 0, 3 );
    }

    public Dimension getPreferredSize( JComponent c )
    { 
        return new Dimension( 0, 4 );
    }
}




