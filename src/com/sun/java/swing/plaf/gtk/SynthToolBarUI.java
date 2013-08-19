/*
 * @(#)SynthToolBarUI.java	1.8 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import java.beans.*;

import javax.swing.border.*;
import javax.swing.plaf.*;


/**
 * A Basic L&F implementation of ToolBarUI.  This implementation 
 * is a "combined" view/controller.
 * <p>
 *
 * @version 1.8, 01/23/03 (based on BasicToolBarUI v 1.83)
 * @author Georges Saab
 * @author Jeff Shapiro
 */
class SynthToolBarUI extends ToolBarUI implements SwingConstants, SynthUI {
    protected JToolBar toolBar;
    private boolean floating;
    private int floatingX;
    private int floatingY;
    private JFrame floatingFrame;
    private RootPaneContainer floatingToolBar;
    protected DragWindow dragWindow;
    private Container dockingSource;
    private int dockingSensitivity = 0;
    protected int focusedCompIndex = -1;
    protected Icon handleIcon = null;
    protected Rectangle contentRect = null;

    protected MouseInputListener dockingListener;
    protected PropertyChangeListener propertyListener;

    protected ContainerListener toolBarContListener;
    protected FocusListener toolBarFocusListener;

    protected String constraintBeforeFloating = BorderLayout.NORTH;

    private SynthStyle style;
    private SynthStyle contentStyle;
    private SynthStyle dragWindowStyle;

    private static String FOCUSED_COMP_INDEX = "JToolBar.focusedCompIndex";

    public static ComponentUI createUI(JComponent c) {
	return new SynthToolBarUI();
    }

    protected String getPropertyPrefix() {
        return "ToolBar";
    }

    public static void loadActionMap(ActionMap map) {
        // NOTE: this needs to remain static. If you have a need to
        // have Actions that reference the UI in the ActionMap,
        // then you'll also need to change the registeration of the
        // ActionMap.
	map.put("navigateRight", new RightAction());
	map.put("navigateLeft", new LeftAction());
	map.put("navigateUp", new UpAction());
	map.put("navigateDown", new DownAction());
    }

    public void installUI(JComponent c) {
	toolBar = (JToolBar)c;
        contentRect = new Rectangle();

	// Set defaults
        installDefaults();
	installComponents();
        installListeners();
	installKeyboardActions();

        // Initialize instance vars
        dockingSensitivity = 0;
        floating = false;
        floatingX = floatingY = 0;
        floatingToolBar = null;

	setOrientation(toolBar.getOrientation());
	c.setOpaque(true);

	if (c.getClientProperty(FOCUSED_COMP_INDEX) != null) {
	    focusedCompIndex =((Integer)(c.getClientProperty(
                            FOCUSED_COMP_INDEX))).intValue();
	}
    }
    
    public void uninstallUI(JComponent c) {
        // Clear defaults
        uninstallDefaults();
	uninstallComponents();
        uninstallListeners();
	uninstallKeyboardActions();

        // Clear instance vars
	if (isFloating() == true)
	    setFloating(false, null);

        floatingToolBar = null;
        dragWindow = null;
        dockingSource = null;

        c.putClientProperty(FOCUSED_COMP_INDEX, new Integer(focusedCompIndex));
    }

    protected void installDefaults() {
        toolBar.setLayout(createLayout());
        fetchStyle(toolBar);
    }

    private void fetchStyle(JToolBar c) {
        SynthContext context = getContext(c, ENABLED);
        SynthStyle oldStyle = style;

        style = SynthLookAndFeel.updateStyle(context, this);
        if (oldStyle != style) {
            handleIcon =
                style.getIcon(context, getPropertyPrefix() + ".handleIcon");
        }
        context.dispose();

        context = getContext(c, Region.TOOL_BAR_CONTENT, ENABLED);
        contentStyle = SynthLookAndFeel.updateStyle(context, this);
        context.dispose();

        context = getContext(c, Region.TOOL_BAR_DRAG_WINDOW, ENABLED);
        dragWindowStyle = SynthLookAndFeel.updateStyle(context, this);
        context.dispose();
    }

    protected void uninstallDefaults() {
        SynthContext context = getContext(toolBar, ENABLED);

        style.uninstallDefaults(context);
        context.dispose();
        style = null;

        handleIcon = null;

        context = getContext(toolBar, Region.TOOL_BAR_CONTENT, ENABLED);
        contentStyle.uninstallDefaults(context);
        context.dispose();
        contentStyle = null;

        context = getContext(toolBar, Region.TOOL_BAR_DRAG_WINDOW, ENABLED);
        dragWindowStyle.uninstallDefaults(context);
        context.dispose();
        dragWindowStyle = null;

        toolBar.setLayout(null);
    }

    protected void installComponents() {
    }

    protected void uninstallComponents() {
    }

    protected void installListeners() {
        dockingListener = createDockingListener();

        if (dockingListener != null) {
	    toolBar.addMouseMotionListener( dockingListener );
	    toolBar.addMouseListener( dockingListener );
	}

	propertyListener = createPropertyListener();  // added in setFloating
	if (propertyListener != null) {
	    toolBar.addPropertyChangeListener(propertyListener);
	}

	toolBarContListener = createToolBarContListener();
        if ( toolBarContListener != null ) {
	    toolBar.addContainerListener( toolBarContListener );
	}

	toolBarFocusListener = createToolBarFocusListener();

        if ( toolBarFocusListener != null )
        {
	    // Put focus listener on all components in toolbar
	    Component[] components = toolBar.getComponents();

	    for ( int i = 0; i < components.length; ++i )
	    {
	        components[ i ].addFocusListener( toolBarFocusListener );
	    }
	}
    }

    protected void uninstallListeners( )
    {
        if ( dockingListener != null )
        {
	    toolBar.removeMouseMotionListener(dockingListener);
	    toolBar.removeMouseListener(dockingListener);

            dockingListener = null;
	}

	if ( propertyListener != null )
	{
	    toolBar.removePropertyChangeListener(propertyListener);
	    propertyListener = null;  // removed in setFloating
	}

	if ( toolBarContListener != null )
	{
	    toolBar.removeContainerListener( toolBarContListener );
	    toolBarContListener = null;
	}

        if ( toolBarFocusListener != null )
        {
	    // Remove focus listener from all components in toolbar
	    Component[] components = toolBar.getComponents();

	    for ( int i = 0; i < components.length; ++i )
	    {
	        components[ i ].removeFocusListener( toolBarFocusListener );
	    }

	    toolBarFocusListener = null;
	}
    }

    protected void installKeyboardActions() {
	InputMap km = getInputMap(JComponent.
				  WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

	SwingUtilities.replaceUIInputMap(toolBar, JComponent.
					 WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
					 km);
	LazyActionMap.installLazyActionMap(toolBar, SynthToolBarUI.class,
                                           "ToolBar.actionMap");
    }

    InputMap getInputMap(int condition) {
	if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) {
            SynthContext context = getContext(toolBar, ENABLED);
            InputMap map = (InputMap)context.getStyle().get(context,
                                             "ToolBar.ancestorInputMap");

            context.dispose();
            return map;
	}
	return null;
    }

    protected void uninstallKeyboardActions() {
	SwingUtilities.replaceUIActionMap(toolBar, null);
	SwingUtilities.replaceUIInputMap(toolBar, JComponent.
					 WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
					 null);
    }

    protected LayoutManager createLayout() {
        return new SynthToolBarLayoutManager();
    }

    public SynthContext getContext(JComponent c) {
        return getContext(c, getComponentState(c));
    }

    private SynthContext getContext(JComponent c, int state) {
        return SynthContext.getContext(SynthContext.class, c,
                    SynthLookAndFeel.getRegion(c), style, state);
    }

    private SynthContext getContext(JComponent c, Region region) {
        return getContext(c, region, getComponentState(c, region));
    }

    private SynthContext getContext(JComponent c, Region region, int state) {
        return SynthContext.getContext(SynthContext.class, c, region,
                                       dragWindowStyle, state);
    }

    private Region getRegion(JComponent c) {
        return SynthLookAndFeel.getRegion(c);
    }

    private int getComponentState(JComponent c) {
        return SynthLookAndFeel.getComponentState(c);
    }

    private int getComponentState(JComponent c, Region region) {
        return SynthLookAndFeel.getComponentState(c);
    }

    public void update(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        SynthLookAndFeel.update(context, g);
        paint(context, g);
        context.dispose();
    }

    public void paint(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        paint(context, g);
        context.dispose();
    }

    protected void paint(SynthContext context, Graphics g) {
        if (handleIcon != null && toolBar.isFloatable()) {
            int startX = toolBar.getComponentOrientation().isLeftToRight() ?
                0 : toolBar.getWidth() -
                    SynthIcon.getIconWidth(handleIcon, context);
            SynthIcon.paintIcon(handleIcon, context, g, startX, 0,
                    SynthIcon.getIconWidth(handleIcon, context),
                    SynthIcon.getIconHeight(handleIcon, context));
        }

        SynthContext subcontext = getContext(toolBar, Region.TOOL_BAR_CONTENT);
        paintContent(subcontext, g, contentRect);
        subcontext.dispose();
    }

    public void paintContent(SynthContext context, Graphics g,
            Rectangle bounds) {
        SynthLookAndFeel.updateSubregion(context, g, bounds);
    }

    protected void navigateFocusedComp(int direction) {
        int nComp = toolBar.getComponentCount();
	int j;

        switch (direction) {
	    case EAST:
	    case SOUTH:

		if (focusedCompIndex < 0 || focusedCompIndex >= nComp) break;

		j = focusedCompIndex + 1;

		while (j != focusedCompIndex) {
		    if (j >= nComp) j = 0;
		    Component comp = toolBar.getComponentAtIndex(j++);

		    if (comp != null && comp.isFocusTraversable()) {
		        comp.requestFocus();
			break;
		    }
		}

	        break;

	    case WEST:
	    case NORTH:

		if (focusedCompIndex < 0 || focusedCompIndex >= nComp) break;

		j = focusedCompIndex - 1;

		while (j != focusedCompIndex) {
		    if (j < 0) j = nComp - 1;
		    Component comp = toolBar.getComponentAtIndex(j--);

		    if (comp != null && comp.isFocusTraversable()) {
		        comp.requestFocus();
			break;
		    }
		}

	        break;

	    default:
	        break;
	}
    }

    /**
     * No longer used, use BasicToolBarUI.createFloatingWindow(JToolBar)
     * @see #createFloatingWindow
     */
    protected JFrame createFloatingFrame(JToolBar toolbar) {
	Window window = SwingUtilities.getWindowAncestor(toolbar);
	JFrame frame = new JFrame(toolbar.getName(),
				  (window != null) ? window.getGraphicsConfiguration() : null) {
	    // Override createRootPane() to automatically resize
	    // the frame when contents change
	    protected JRootPane createRootPane() {
		JRootPane rootPane = new JRootPane() {
		    private boolean packing = false;

		    public void validate() {
			super.validate();
			if (!packing) {
			    packing = true;
			    pack();
			    packing = false;
			}
		    }
		};
		rootPane.setOpaque(true);
		return rootPane;
	    }
	};
        frame.getRootPane().setName("ToolBar.FloatingFrame");
	frame.setResizable(false);
	WindowListener wl = createFrameListener();
	frame.addWindowListener(wl);
        return frame;
    }

    /**
     * Creates a window which contains the toolbar after it has been
     * dragged out from its container
     * @returns a <code>RootPaneContainer</code> object, containing the toolbar.
     */
    protected RootPaneContainer createFloatingWindow(JToolBar toolbar) {
	class ToolBarDialog extends JDialog {
	    public ToolBarDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
	    }

	    public ToolBarDialog(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
	    }

	    // Override createRootPane() to automatically resize
	    // the frame when contents change
	    protected JRootPane createRootPane() {
		JRootPane rootPane = new JRootPane() {
		    private boolean packing = false;

		    public void validate() {
			super.validate();
			if (!packing) {
			    packing = true;
			    pack();
			    packing = false;
			}
		    }
		};
		rootPane.setOpaque(true);
		return rootPane;
	    }
	}

	JDialog dialog;
	Window window = SwingUtilities.getWindowAncestor(toolbar);
	if (window instanceof Frame) {
	    dialog = new ToolBarDialog((Frame)window, toolbar.getName(), false);
	} else if (window instanceof Dialog) {
	    dialog = new ToolBarDialog((Dialog)window, toolbar.getName(), false);
	} else {
	    dialog = new ToolBarDialog((Frame)null, toolbar.getName(), false);
	}

        dialog.getRootPane().setName("ToolBar.FloatingWindow");
	dialog.setTitle(toolbar.getName());
	dialog.setResizable(false);
	WindowListener wl = createFrameListener();
	dialog.addWindowListener(wl);
        return dialog;
    }

    protected DragWindow createDragWindow(JToolBar toolbar) {
	Window frame = null;
	if(toolBar != null) {
	    Container p;
	    for(p = toolBar.getParent() ; p != null && !(p instanceof Window) ;
		p = p.getParent());
	    if(p != null && p instanceof Window)
		frame = (Window) p;
	}
	if(floatingToolBar == null) {
	    floatingToolBar = createFloatingWindow(toolBar);
	}
	if (floatingToolBar instanceof Window) frame = (Window) floatingToolBar;
	DragWindow dragWindow = new DragWindow(frame);
	return dragWindow;
    }

    public Dimension getMinimumSize(JComponent c) {
        return getPreferredSize(c);
    }

    public Dimension getPreferredSize(JComponent c) {
	return null;
    }

    public Dimension getMaximumSize(JComponent c) {
        return getPreferredSize(c);
    }

    public void setFloatingLocation(int x, int y) {
	floatingX = x;
	floatingY = y;
    }
    
    public boolean isFloating() {
	return floating;
    }

    public void setFloating(boolean b, Point p) {
 	if (toolBar.isFloatable() == true) {
	    if (dragWindow != null)
		dragWindow.setVisible(false);
	    this.floating = b;
	    if (b == true)
	    {
		if (dockingSource == null)
		{
		    dockingSource = toolBar.getParent();
		    dockingSource.remove(toolBar);
		}
		Point l = new Point();
		toolBar.getLocation(l);
		constraintBeforeFloating = calculateConstraint(dockingSource, l);
		if ( propertyListener != null )
                    UIManager.addPropertyChangeListener( propertyListener );
		if (floatingToolBar == null)
		    floatingToolBar = createFloatingWindow(toolBar);
		floatingToolBar.getContentPane().add(toolBar,BorderLayout.CENTER);
		setOrientation( JToolBar.HORIZONTAL );
		if (floatingToolBar instanceof Window) ((Window)floatingToolBar).pack();
		if (floatingToolBar instanceof Window) ((Window)floatingToolBar).setLocation(floatingX, floatingY);
		if (floatingToolBar instanceof Window) ((Window)floatingToolBar).show();
	    } else {
		if (floatingToolBar == null)
		    floatingToolBar = createFloatingWindow(toolBar);
		if (floatingToolBar instanceof Window) ((Window)floatingToolBar).setVisible(false);
		floatingToolBar.getContentPane().remove(toolBar);
		String constraint = getDockingConstraint(dockingSource,
							 p);
		int orientation = mapConstraintToOrientation(constraint);
		setOrientation(orientation);
		if (dockingSource== null)
		    dockingSource = toolBar.getParent();
		if ( propertyListener != null )
		    UIManager.removePropertyChangeListener( propertyListener );
		dockingSource.add(constraint, toolBar);
	    }
 	    dockingSource.invalidate();
 	    Container dockingSourceParent = dockingSource.getParent();
	    if (dockingSourceParent != null) 
		dockingSourceParent.validate();
	    dockingSource.repaint();
	}
    }

    private int mapConstraintToOrientation(String constraint) {
	int orientation = toolBar.getOrientation();

	if (constraint != null) {
	    if (constraint.equals(BorderLayout.EAST) ||
                    constraint.equals(BorderLayout.WEST))
	        orientation = JToolBar.VERTICAL;
	    else if (constraint.equals(BorderLayout.NORTH) ||
                    constraint.equals(BorderLayout.SOUTH))
	        orientation = JToolBar.HORIZONTAL;
	}

	return orientation;
    }
    
    public void setOrientation(int orientation) {	
        toolBar.setOrientation(orientation);

	if (dragWindow != null)
	    dragWindow.setOrientation(orientation);
    }
    
    public boolean canDock(Component c, Point p) {
	boolean b = false;
	if (c.contains(p)) {
	    dockingSensitivity = (toolBar.getOrientation() == JToolBar.HORIZONTAL) ? toolBar.getSize().height : toolBar.getSize().width;
	    // North
	    if (p.y < dockingSensitivity)
		b = true;
	    // South
	    if (p.y > c.getSize().height-dockingSensitivity)
		b = true;
	    // West  (Base distance on height for now!)
	    if (p.x < dockingSensitivity)
		b = true;
	    // East  (Base distance on height for now!)
	    if (p.x > c.getSize().width-dockingSensitivity)
		b = true;
	}
	return b;
    }

    private String calculateConstraint(Component c, Point p) {
	if (p == null) return constraintBeforeFloating;
	String s = BorderLayout.NORTH;
	if (c.contains(p)) {
	    dockingSensitivity = (toolBar.getOrientation() == JToolBar.HORIZONTAL) ? toolBar.getSize().height : toolBar.getSize().width;
	    if (p.y >= dockingSource.getSize().height-dockingSensitivity)
		s = BorderLayout.SOUTH;
	    // West  (Base distance on height for now!)
	    else if (p.x < dockingSensitivity && (toolBar.getOrientation() == JToolBar.VERTICAL))
		s = BorderLayout.WEST;
	    // East  (Base distance on height for now!)
	    else if (p.x >= dockingSource.getSize().width-dockingSensitivity && (toolBar.getOrientation() == JToolBar.VERTICAL))
		s = BorderLayout.EAST;
	    // North  (Base distance on height for now!)
	    else if (p.y < dockingSensitivity)
		s = BorderLayout.NORTH;
	}
	return s;
    }



    private String getDockingConstraint(Component c, Point p) {
	if (p == null) return constraintBeforeFloating;
	String s = BorderLayout.NORTH;
	if (c.contains(p)) {
	    dockingSensitivity = (toolBar.getOrientation() == JToolBar.HORIZONTAL) ? toolBar.getSize().height : toolBar.getSize().width;
	    if (p.y >= dockingSource.getSize().height-dockingSensitivity)
		s = BorderLayout.SOUTH;
	    // West  (Base distance on height for now!)
	    if (p.x < dockingSensitivity)
		s = BorderLayout.WEST;
	    // East  (Base distance on height for now!)
	    if (p.x >= dockingSource.getSize().width-dockingSensitivity)
		s = BorderLayout.EAST;
	    // North  (Base distance on height for now!)
	    if (p.y < dockingSensitivity)
		s = BorderLayout.NORTH;
	}
	return s;
    }

    protected void dragTo(Point position, Point origin)
    {
	if (toolBar.isFloatable() == true)
	{
	  try
	  {
	    if (dragWindow == null)
		dragWindow = createDragWindow(toolBar);
	    Point offset = dragWindow.getOffset();
	    if (offset == null) {
		Dimension size = toolBar.getPreferredSize();
		offset = new Point(size.width/2, size.height/2);
		dragWindow.setOffset(offset);
	    }
	    Point global = new Point(origin.x+ position.x,
				     origin.y+position.y);
	    Point dragPoint = new Point(global.x- offset.x, 
					global.y- offset.y);
	    if (dockingSource == null)
		dockingSource = toolBar.getParent();


		Point p = new Point(origin);
		SwingUtilities.convertPointFromScreen(p,toolBar.getParent());
		constraintBeforeFloating = calculateConstraint(dockingSource, p);	    
	    Point dockingPosition = dockingSource.getLocationOnScreen();
	    Point comparisonPoint = new Point(global.x-dockingPosition.x,
					      global.y-dockingPosition.y);
	    if (canDock(dockingSource, comparisonPoint)) {
		String constraint = getDockingConstraint(dockingSource,
							 comparisonPoint);
		int orientation = mapConstraintToOrientation(constraint);
		dragWindow.setOrientation(orientation);
	    } else {
		dragWindow.setOrientation( JToolBar.HORIZONTAL );
	    }
	    
	    dragWindow.setLocation(dragPoint.x, dragPoint.y);
	    if (dragWindow.isVisible() == false) {
		Dimension size = toolBar.getPreferredSize();
		dragWindow.setSize(size.width, size.height);
		dragWindow.show();
	    }
	  }
	  catch ( IllegalComponentStateException e )
	  {
	  }
	}
    }

    protected void floatAt(Point position, Point origin)
    {
	if(toolBar.isFloatable() == true)
	{
	  try
	  {
	    Point offset = dragWindow.getOffset();
	    if (offset == null) {
		offset = position;
		dragWindow.setOffset(offset);
	    }
	    Point global = new Point(origin.x+ position.x,
				     origin.y+position.y);
	    setFloatingLocation(global.x-offset.x, 
				global.y-offset.y);
	    if (dockingSource != null) { 
		Point dockingPosition = dockingSource.getLocationOnScreen();
		Point comparisonPoint = new Point(global.x-dockingPosition.x,
						  global.y-dockingPosition.y);
		if (canDock(dockingSource, comparisonPoint)) {
		    setFloating(false, comparisonPoint);
		} else {
		    setFloating(true, null);
		}
	    } else {
		setFloating(true, null);
	    }
	    dragWindow.setOffset(null);
	  }
	  catch ( IllegalComponentStateException e )
	  {
	  }
	}
    }

    protected ContainerListener createToolBarContListener( )
    {
	return new ToolBarContListener( );
    }

    protected FocusListener createToolBarFocusListener( )
    {
	return new ToolBarFocusListener( );
    }

    protected PropertyChangeListener createPropertyListener()
    {
        return new PropertyListener();
    }

    protected MouseInputListener createDockingListener( ) {
	return new DockingListener(toolBar);
    }
    
    protected WindowListener createFrameListener() {
	return new FrameListener();
    }

    // The private inner classes below should be changed to protected the
    // next time API changes are allowed.
  
    private static abstract class KeyAction extends AbstractAction {
        public boolean isEnabled() { 
            return true;
        }
    };

    private static class RightAction extends KeyAction {
        public void actionPerformed(ActionEvent e) {
	    JToolBar toolBar = (JToolBar)e.getSource();
            // PENDING:
	    SynthToolBarUI ui = (SynthToolBarUI)toolBar.getUI();
            ui.navigateFocusedComp(EAST);
        }
    };
    
    private static class LeftAction extends KeyAction {
        public void actionPerformed(ActionEvent e) {
	    JToolBar toolBar = (JToolBar)e.getSource();
	    SynthToolBarUI ui = (SynthToolBarUI)toolBar.getUI();
            ui.navigateFocusedComp(WEST);
        }
    };

    private static class UpAction extends KeyAction {
        public void actionPerformed(ActionEvent e) {
	    JToolBar toolBar = (JToolBar)e.getSource();
	    SynthToolBarUI ui = (SynthToolBarUI)toolBar.getUI();
            ui.navigateFocusedComp(NORTH);
        }
    };

    private static class DownAction extends KeyAction {
        public void actionPerformed(ActionEvent e) {
	    JToolBar toolBar = (JToolBar)e.getSource();
	    SynthToolBarUI ui = (SynthToolBarUI)toolBar.getUI();
            ui.navigateFocusedComp(SOUTH);
        }
    };

    protected class FrameListener extends WindowAdapter {
	public void windowClosing(WindowEvent w) {	    
	    if (toolBar.isFloatable() == true) {
		if (dragWindow != null)
		    dragWindow.setVisible(false);
		floating = false;
		if (floatingToolBar == null)
		    floatingToolBar = createFloatingWindow(toolBar);
		if (floatingToolBar instanceof Window) ((Window)floatingToolBar).setVisible(false);
		floatingToolBar.getContentPane().remove(toolBar);
		String constraint = constraintBeforeFloating;
		int orientation = mapConstraintToOrientation(constraint);
		setOrientation(orientation);
		if (dockingSource == null)
		    dockingSource = toolBar.getParent();
		if (propertyListener != null)
		    UIManager.removePropertyChangeListener(propertyListener);
		dockingSource.add(constraint, toolBar);
		dockingSource.invalidate();
		Container dockingSourceParent = dockingSource.getParent();
		if (dockingSourceParent != null)
			dockingSourceParent.validate();
		dockingSource.repaint();
		setFloating(false,null);
	    }
	}

    } 

    protected class ToolBarContListener implements ContainerListener
    {
        public void componentAdded( ContainerEvent e )	{
	    Component c = e.getChild();

	    if ( toolBarFocusListener != null ) {
	        c.addFocusListener( toolBarFocusListener );
	    }
	}

        public void componentRemoved( ContainerEvent e ) {
	    Component c = e.getChild();

	    if ( toolBarFocusListener != null ) {
	        c.removeFocusListener( toolBarFocusListener );
	    }
	}

    } // end class ToolBarContListener

    protected class ToolBarFocusListener implements FocusListener
    {
        public void focusGained( FocusEvent e )
	{
	    Component c = e.getComponent();

	    focusedCompIndex = toolBar.getComponentIndex( c );
	}

        public void focusLost( FocusEvent e )
	{
	}

    } // end class ToolBarFocusListener

    protected class PropertyListener implements PropertyChangeListener
    {
        public void propertyChange( PropertyChangeEvent e ) {
	    String propertyName = e.getPropertyName();

            if (SynthLookAndFeel.shouldUpdateStyle(e)) {
                fetchStyle(toolBar);
            }
	    if ( propertyName.equals("lookAndFeel") ) {
	        toolBar.updateUI();
            } else if (propertyName.equals("orientation")) {
		// Search for JSeparator components and change it's orientation
		// to match the toolbar and flip it's orientation.
		Component[] components = toolBar.getComponents();
		int orientation = ((Integer)e.getNewValue()).intValue();
		JToolBar.Separator separator;

		for( int i = 0; i < components.length; ++i ) {
		    if (components[i] instanceof JToolBar.Separator) {
			separator = (JToolBar.Separator)components[i];
			separator.setOrientation(orientation);
			Dimension size = separator.getSize();
			if (size.width != size.height) {
			    // Flip the orientation.
			    Dimension newSize = new Dimension(size.height, size.width);
			    separator.setSeparatorSize(newSize);
			}
		    }
		}
	    }
	}
    }

    class SynthToolBarLayoutManager implements LayoutManager {
        public void addLayoutComponent(String name, Component comp) {}

        public void removeLayoutComponent(Component comp) {}

        public Dimension minimumLayoutSize(Container parent) {
            JToolBar tb = (JToolBar)parent;
            Dimension dim = new Dimension();
            SynthContext context = getContext(tb);

            if (tb.getOrientation() == JToolBar.HORIZONTAL) {
                dim.width = SynthIcon.getIconWidth(handleIcon, context);
                Dimension compDim;
                for (int i = 0; i < tb.getComponentCount(); i++) {
                    compDim = tb.getComponent(i).getMinimumSize();
                    dim.width += compDim.width;
                    dim.height = Math.max(dim.height, compDim.height);
                }
            } else {
                dim.height =
                    SynthIcon.getIconHeight(handleIcon, context);
                Dimension compDim;
                for (int i = 0; i < tb.getComponentCount(); i++) {
                    compDim = tb.getComponent(i).getMinimumSize();
                    dim.width = Math.max(dim.width, compDim.width);
                    dim.height += compDim.height;
                }
            }
            context.dispose();
            return dim;
        }

        public Dimension preferredLayoutSize(Container parent) {
            JToolBar tb = (JToolBar)parent;
            Dimension dim = new Dimension();
            SynthContext context = getContext(tb);

            if (tb.getOrientation() == JToolBar.HORIZONTAL) {
                dim.width = SynthIcon.getIconWidth(handleIcon, context);
                Dimension compDim;
                for (int i = 0; i < tb.getComponentCount(); i++) {
                    compDim = tb.getComponent(i).getPreferredSize();
                    dim.width += compDim.width;
                    dim.height = Math.max(dim.height, compDim.height);
                }
            } else {
                dim.height =
                    SynthIcon.getIconHeight(handleIcon, context);
                Dimension compDim;
                for (int i = 0; i < tb.getComponentCount(); i++) {
                    compDim = tb.getComponent(i).getPreferredSize();
                    dim.width = Math.max(dim.width, compDim.width);
                    dim.height += compDim.height;
                }
            }
            context.dispose();
            return dim;
        }

        public void layoutContainer(Container parent) {
            JToolBar tb = (JToolBar)parent;
            boolean ltr = tb.getComponentOrientation().isLeftToRight();
            SynthContext context = getContext(tb);
            int handleWidth = SynthIcon.getIconWidth(handleIcon, context);

            Component c;
            Dimension d;
            if (tb.getOrientation() == JToolBar.HORIZONTAL) {
                int x = ltr ? handleWidth : tb.getWidth() - handleWidth;
                for (int i = 0; i < tb.getComponentCount(); i++) {
                    c = tb.getComponent(i);
                    d = c.getPreferredSize();
                    c.setBounds(ltr ? x : x - d.width, 0, d.width, d.height);
                    x = ltr ? x + d.width : x - d.width;
                }
                contentRect.x = ltr ?
                        SynthIcon.getIconWidth(handleIcon, context) : 0;
                contentRect.y = 0;
                contentRect.width = tb.getWidth() - contentRect.x;
                contentRect.height = tb.getHeight();
            } else {
                int y = SynthIcon.getIconHeight(handleIcon, context);
                for (int i = 0; i < tb.getComponentCount(); i++) {
                    c = tb.getComponent(i);
                    d = c.getPreferredSize();
                    c.setBounds(0, y, d.width, d.height);
                    y += d.height;
                }
                contentRect.x = 0;
                contentRect.y =
                    SynthIcon.getIconHeight(handleIcon, context);
                contentRect.width = tb.getWidth();
                contentRect.height = tb.getHeight() - contentRect.y;
            }
            context.dispose();
        }
    }

    class DockingListener implements MouseInputListener {
	protected JToolBar toolBar;
	protected boolean isDragging = false;
	protected Point origin = null;

	public DockingListener(JToolBar t) {
	    this.toolBar = t;
	} 

	public void mouseClicked(MouseEvent e) {}
	public void mousePressed(MouseEvent e) { 
            if (!toolBar.isEnabled()) {
                return;
            }
	    isDragging = false;
	}
	public void mouseReleased(MouseEvent e) {
            if (!toolBar.isEnabled()) {
                return;
            }
	    if (isDragging == true) {
		Point position = e.getPoint();
		if (origin == null)
		    origin = e.getComponent().getLocationOnScreen();
		floatAt(position, origin);
	    }
	    origin = null;
	    isDragging = false;
	}
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }

	public void mouseDragged(MouseEvent e) {
            if (!toolBar.isEnabled()) {
                return;
            }
	    isDragging = true;
	    Point position = e.getPoint();
	    if (origin == null)
		origin = e.getComponent().getLocationOnScreen();
	    dragTo(position, origin);
	}
	public void mouseMoved(MouseEvent e) {
	}
    }

    protected class DragWindow extends Window
    {
	int orientation = toolBar.getOrientation();
	Point offset; // offset of the mouse cursor inside the DragWindow

	DragWindow(Window w) {
	    super(w);
	}

	public void setOrientation(int o) {
	    if(isShowing()) {
		if (o == this.orientation)
		    return;	    
		this.orientation = o;
		Dimension size = getSize();
		setSize(new Dimension(size.height, size.width));
		if (offset!=null) {
                    if( SynthLookAndFeel.isLeftToRight(toolBar) ) {
                        setOffset(new Point(offset.y, offset.x));
                    } else if( o == JToolBar.HORIZONTAL ) {
                        setOffset(new Point( size.height-offset.y, offset.x));
                    } else {
                        setOffset(new Point(offset.y, size.width-offset.x));
                    }
                }
		repaint();
	    }
	}

	public Point getOffset() {
	    return offset;
	}

	public void setOffset(Point p) {
	    this.offset = p;
	}
	
	public void paint(Graphics g) {
            SynthContext context = getContext(toolBar,
                                              Region.TOOL_BAR_DRAG_WINDOW);

            SynthLookAndFeel.updateSubregion(context, g, new Rectangle(
                                             0, 0, getWidth(), getHeight()));
            context.dispose();
	}

	public Insets getInsets() {
	    return new Insets(1,1,1,1);
	}
    }
}
