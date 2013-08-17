/*
 * @(#)MetalToolBarUI.java	1.15 98/08/26
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

package javax.swing.plaf.metal;

import javax.swing.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.*;
import java.util.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;

import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

/**
 * A Metal Look and Feel implementation of ToolBarUI.  This implementation 
 * is a "combined" view/controller.
 * <p>
 *
 * @version 1.15 08/26/98
 * @author Jeff Shapiro
 */
public class MetalToolBarUI extends BasicToolBarUI
{
    private static Border rolloverBorder = new MetalBorders.RolloverButtonBorder();
    private static Border nonRolloverBorder = new MetalBorders.ButtonBorder();

    protected ContainerListener contListener;
    protected PropertyChangeListener rolloverListener;

    private Hashtable borderTable = new Hashtable();
    private boolean rolloverBorders = false;

    private static String IS_ROLLOVER = "JToolBar.isRollover";


    public static ComponentUI createUI( JComponent c )
    {
	return new MetalToolBarUI();
    }

    public void installUI( JComponent c )
    {
        super.installUI( c );

	Object rolloverProp = c.getClientProperty( IS_ROLLOVER );

	if ( rolloverProp != null )
	{
	    setRolloverBorders( ((Boolean)rolloverProp).booleanValue() );
	}
	else
	{
	    setRolloverBorders( false );
	}
    }

    public void uninstallUI( JComponent c )
    {
        super.uninstallUI( c );

	installNormalBorders( c );
   }

    protected void installListeners( )
    {
        super.installListeners( );

        contListener = createContainerListener( );
	if ( contListener != null ) toolBar.addContainerListener( contListener );

        rolloverListener = createRolloverListener( );
	if ( rolloverListener != null ) toolBar.addPropertyChangeListener( rolloverListener );
    }

    protected void uninstallListeners( )
    {
        super.uninstallListeners( );

	if ( contListener != null ) toolBar.removeContainerListener( contListener );
	contListener = null;

        if ( rolloverListener != null ) toolBar.removePropertyChangeListener( rolloverListener );
	rolloverListener = null;
    }

    protected ContainerListener createContainerListener( )
    {
	return new MetalContainerListener( );
    }

    protected PropertyChangeListener createRolloverListener( )
    {
	return new MetalRolloverListener( );
    }

    protected MouseInputListener createDockingListener( )
    {
	return new MetalDockingListener( toolBar );
    }

    protected void setDragOffset( Point p )
    {
        if (dragWindow == null)
	    dragWindow = createDragWindow(toolBar);

        dragWindow.setOffset( p );
    }

    public boolean isRolloverBorders()
    {
        return rolloverBorders;
    }

    public void setRolloverBorders( boolean rollover )
    {
        rolloverBorders = rollover;
	    
	if ( rolloverBorders )
	{
	    installRolloverBorders( toolBar );
	}
	else
	{
	    installNonRolloverBorders( toolBar );
	}
    }

    protected void installRolloverBorders ( JComponent c )
    {
	// Put rollover borders on buttons
	Component[] components = c.getComponents();

	for ( int i = 0; i < components.length; ++i )
	{
	  if ( components[ i ] instanceof JComponent )
	  {
	      ( (JComponent)components[ i ] ).updateUI();

	      setBorderToRollover( components[ i ] );
	  }
	}
    }

    protected void installNonRolloverBorders ( JComponent c )
    {
	// Put nonrollover borders on buttons
	Component[] components = c.getComponents();

	for ( int i = 0; i < components.length; ++i )
	{
	  if ( components[ i ] instanceof JComponent )
	  {
	      ( (JComponent)components[ i ] ).updateUI();

	      setBorderToNonRollover( components[ i ] );
	  }
	}
    }

    protected void installNormalBorders ( JComponent c )
    {
	// Put back the normal borders on buttons
	Component[] components = c.getComponents();

	for ( int i = 0; i < components.length; ++i )
	{
	    setBorderToNormal( components[ i ] );
	}
    }

    protected void setBorderToRollover( Component c )
    {
        if ( c instanceof JButton )
	{
	    JButton b = (JButton)c;

	    if ( b.getUI() instanceof MetalButtonUI )
	    {
	        if ( b.getBorder() instanceof UIResource )
		{
		    borderTable.put( b, b.getBorder() );
		}

		if ( b.getBorder() instanceof UIResource || b.getBorder() == nonRolloverBorder )
		{
		    b.setBorder( rolloverBorder );
		}
		
		b.setRolloverEnabled( true );
	    }
	}
    }

    protected void setBorderToNonRollover( Component c )
    {
        if ( c instanceof JButton )
	{
	    JButton b = (JButton)c;

	    if ( b.getUI() instanceof MetalButtonUI )
	    {
	        if ( b.getBorder() instanceof UIResource )
		{
		    borderTable.put( b, b.getBorder() );
		}

		if ( b.getBorder() instanceof UIResource || b.getBorder() == rolloverBorder )
		{
		    b.setBorder( nonRolloverBorder );
		}

		b.setRolloverEnabled( false );
	    }
	}
    }

    protected void setBorderToNormal( Component c )
    {
        if ( c instanceof JButton )
	{
	    JButton b = (JButton)c;

	    if ( b.getUI() instanceof MetalButtonUI )
	    {
	        if ( b.getBorder() == rolloverBorder || b.getBorder() == nonRolloverBorder )
		{
		    b.setBorder( (Border)borderTable.remove( b ) );
		}

		b.setRolloverEnabled( false );
	    }
	}
    }


    protected class MetalContainerListener implements ContainerListener
    {
        public void componentAdded( ContainerEvent e )
	{
	    Component c = e.getChild();

	    if ( rolloverBorders )
	    {
	        setBorderToRollover( c );
	    }
	    else
	    {
	        setBorderToNonRollover( c );
	    }
	}

        public void componentRemoved( ContainerEvent e )
	{
            Component c = e.getChild();
	    setBorderToNormal( c );
	}

    } // end class MetalContainerListener


    protected class MetalRolloverListener implements PropertyChangeListener
    {
        public void propertyChange( PropertyChangeEvent e )
	{
	    String name = e.getPropertyName();

	    if ( name.equals( IS_ROLLOVER ) )
	    {
	        if ( e.getNewValue() != null )
		{
		    setRolloverBorders( ((Boolean)e.getNewValue()).booleanValue() );
		}
		else
		{
		    setRolloverBorders( false );
		}
	    }
	}
    } // end class MetalRolloverListener


    protected class MetalDockingListener extends DockingListener
    {
        private boolean pressedInBumps = false;

	public MetalDockingListener( JToolBar t )
	{
	    super( t );
	} 

	public void mousePressed( MouseEvent e )
	{ 
	    super.mousePressed( e );
            if (!toolBar.isEnabled()) {
                return;
            }
	    setDragOffset( e.getPoint() );
	    pressedInBumps = false;

	    Rectangle bumpRect = new Rectangle();

	    if ( toolBar.getSize().height <= toolBar.getSize().width )  // horizontal
	    {
		bumpRect.setBounds( 0, 0, 14, toolBar.getSize().height );
	    }
	    else  // vertical
	    {
		bumpRect.setBounds( 0, 0, toolBar.getSize().width, 14 );
	    }

	    if ( bumpRect.contains( e.getPoint() ) )
	    {
	        pressedInBumps = true;
	    }
	}

	public void mouseDragged( MouseEvent e )
	{
	    if ( pressedInBumps )
	    {
	        super.mouseDragged( e );
	    }
	}

    } // end class MetalDockingListener

}


