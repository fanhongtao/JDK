/*
 * @(#)MetalToolBarUI.java	1.33 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.metal;

import javax.swing.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.*;

import java.beans.PropertyChangeListener;

import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

/**
 * A Metal Look and Feel implementation of ToolBarUI.  This implementation 
 * is a "combined" view/controller.
 * <p>
 *
 * @version 1.33 01/23/03
 * @author Jeff Shapiro
 */
public class MetalToolBarUI extends BasicToolBarUI
{
    /**
     * This protected field is implemenation specific. Do not access directly
     * or override. Use the create method instead.
     *
     * @see #createContainerListener
     */
    protected ContainerListener contListener;

    /**
     * This protected field is implemenation specific. Do not access directly
     * or override. Use the create method instead.
     *
     * @see #createRolloverListener
     */
    protected PropertyChangeListener rolloverListener;

    private static Border nonRolloverBorder;

    public static ComponentUI createUI( JComponent c )
    {
	return new MetalToolBarUI();
    }

    public void installUI( JComponent c )
    {
        super.installUI( c );
    }

    public void uninstallUI( JComponent c )
    {
        super.uninstallUI( c );
	nonRolloverBorder = null;
    }

    protected void installListeners() {
        super.installListeners();

	contListener = createContainerListener();
	if (contListener != null) {
	    toolBar.addContainerListener(contListener);
	}
	rolloverListener = createRolloverListener();
	if (rolloverListener != null) {
	    toolBar.addPropertyChangeListener(rolloverListener);
	}
    }

    protected void uninstallListeners() {
        super.uninstallListeners();

	if (contListener != null) {
	    toolBar.removeContainerListener(contListener);
	}
	rolloverListener = createRolloverListener();
	if (rolloverListener != null) {
	    toolBar.removePropertyChangeListener(rolloverListener);
	}
    }

    protected Border createRolloverBorder() {
	return new BorderUIResource.CompoundBorderUIResource(new MetalBorders.RolloverButtonBorder(), 
				  new MetalBorders.RolloverMarginBorder());
    }

    protected Border createNonRolloverBorder() {
	return new BorderUIResource.CompoundBorderUIResource(new MetalBorders.ButtonBorder(),
				  new MetalBorders.RolloverMarginBorder());
    }


    /**
     * Creates a non rollover border for Toggle buttons in the toolbar.
     */
    private Border createNonRolloverToggleBorder() {
	return createNonRolloverBorder();
    }
    
    protected void setBorderToNonRollover(Component c) {
	super.setBorderToNonRollover(c);
	if (c instanceof AbstractButton) {
	    AbstractButton b = (AbstractButton)c;
	    if (b.getBorder() instanceof UIResource) {
		if (b instanceof JToggleButton && !(b instanceof JCheckBox)) {
		    // only install this border for the ToggleButton
		    if (nonRolloverBorder == null) {
			nonRolloverBorder = createNonRolloverToggleBorder();
		    }
		    b.setBorder(nonRolloverBorder);
		}
	    }
	}
    }


    /**
     * Creates a container listener that will be added to the JToolBar.
     * If this method returns null then it will not be added to the 
     * toolbar.
     *
     * @return an instance of a <code>ContainerListener</code> or null
     */
    protected ContainerListener createContainerListener() {
	return null;
    }

    /**
     * Creates a property change listener that will be added to the JToolBar.
     * If this method returns null then it will not be added to the 
     * toolbar.
     *
     * @return an instance of a <code>PropertyChangeListener</code> or null
     */
    protected PropertyChangeListener createRolloverListener() {
	return null;
    }

    protected MouseInputListener createDockingListener( )
    {
	return new MetalDockingListener( toolBar );
    }

    protected void setDragOffset(Point p) {
	if (!GraphicsEnvironment.isHeadless()) {
	    if (dragWindow == null) {
		dragWindow = createDragWindow(toolBar);
	    }
	    dragWindow.setOffset(p);
	}
    }

    // No longer used. Cannot remove for compatibility reasons
    protected class MetalContainerListener
	extends BasicToolBarUI.ToolBarContListener {}

    // No longer used. Cannot remove for compatibility reasons
    protected class MetalRolloverListener 
	extends BasicToolBarUI.PropertyListener {}

    protected class MetalDockingListener extends DockingListener {
        private boolean pressedInBumps = false;

	public MetalDockingListener(JToolBar t) {
	    super(t);
	} 

	public void mousePressed(MouseEvent e) { 
	    super.mousePressed(e);
            if (!toolBar.isEnabled()) {
                return;
            }
	    pressedInBumps = false;
	    Rectangle bumpRect = new Rectangle();

	    if (toolBar.getOrientation() == JToolBar.HORIZONTAL) {
		int x = MetalUtils.isLeftToRight(toolBar) ? 0 : toolBar.getSize().width-14;
		bumpRect.setBounds(x, 0, 14, toolBar.getSize().height);
	    } else {  // vertical
		bumpRect.setBounds(0, 0, toolBar.getSize().width, 14);
	    }
	    if (bumpRect.contains(e.getPoint())) {
	        pressedInBumps = true;
                Point dragOffset = e.getPoint();
                if (!MetalUtils.isLeftToRight(toolBar)) {
                    dragOffset.x -= (toolBar.getSize().width 
				     - toolBar.getPreferredSize().width);
                }
                setDragOffset(dragOffset);
	    }
	}

	public void mouseDragged(MouseEvent e) {
	    if (pressedInBumps) {
	        super.mouseDragged(e);
	    }
	}
    } // end class MetalDockingListener
}
