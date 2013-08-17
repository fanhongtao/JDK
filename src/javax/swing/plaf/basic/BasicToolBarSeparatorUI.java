/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JToolBar;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicSeparatorUI;


/**
 * A Basic L&F implementation of ToolBarSeparatorUI.  This implementation 
 * is a "combined" view/controller.
 * <p>
 *
 * @version 1.8 02/06/02
 * @author Jeff Shapiro
 */

public class BasicToolBarSeparatorUI extends BasicSeparatorUI
{
    public static ComponentUI createUI( JComponent c )
    {
        return new BasicToolBarSeparatorUI();
    }

    protected void installDefaults( JSeparator s )
    {
        Dimension size = ( (JToolBar.Separator)s ).getSeparatorSize();

	if ( size == null || size instanceof UIResource )
	{
	    size = ( Dimension )( UIManager.get( "ToolBar.separatorSize" ) );
	    ( (JToolBar.Separator)s ).setSeparatorSize( size );
	}
    }

    public void paint( Graphics g, JComponent c )
    {
    }

    public Dimension getPreferredSize( JComponent c )
    {
        Dimension size = ( (JToolBar.Separator)c ).getSeparatorSize();

	if ( size != null )
	{
	    return size.getSize();
	}
	else
	{
	    return null;
	}
    }

    public Dimension getMinimumSize( JComponent c )
    {
        return getPreferredSize( c );
    }

    public Dimension getMaximumSize( JComponent c )
    {
        return getPreferredSize( c );
    }

}



