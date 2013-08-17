/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.event.*;
import java.io.Serializable;


/**
 * A default L&F implementation of ScrollPaneUI.
 *
 * @version 1.53 02/06/02
 * @author Hans Muller
 */
public class BasicScrollPaneUI
    extends ScrollPaneUI implements ScrollPaneConstants
{
    protected JScrollPane scrollpane;
    protected ChangeListener vsbChangeListener;
    protected ChangeListener hsbChangeListener;
    protected ChangeListener viewportChangeListener;
    protected PropertyChangeListener spPropertyChangeListener;


    public static ComponentUI createUI(JComponent x) {
	return new BasicScrollPaneUI();
    }


    public void paint(Graphics g, JComponent c) {
	Border vpBorder = scrollpane.getViewportBorder();
	if (vpBorder != null) {
	    Rectangle r = scrollpane.getViewportBorderBounds();
	    vpBorder.paintBorder(scrollpane, g, r.x, r.y, r.width, r.height);
	}

    }


    /**
     * @return null which indicates that the LayoutManager will compute the value
     * @see JComponent#getPreferredSize
     */
    public Dimension getPreferredSize(JComponent c) {
	return null;
    }


    /**
     * @return the preferred size
     * @see #getPreferredSize
     */
    public Dimension getMinimumSize(JComponent c) {
	return getPreferredSize(c);
    }


    /**
     * @return new Dimension(Short.MAX_VALUE, Short.MAX_VALUE)
     */
    public Dimension getMaximumSize(JComponent c) {
	return new Dimension(Short.MAX_VALUE, Short.MAX_VALUE);
    }


    protected void installDefaults(JScrollPane scrollpane) 
    {
	LookAndFeel.installBorder(scrollpane, "ScrollPane.border");
	LookAndFeel.installColorsAndFont(scrollpane, 
	    "ScrollPane.background", 
	    "ScrollPane.foreground", 
            "ScrollPane.font");

        Border vpBorder = scrollpane.getViewportBorder();
        if ((vpBorder == null) ||( vpBorder instanceof UIResource)) {
	    vpBorder = UIManager.getBorder("ScrollPane.viewportBorder");
	    scrollpane.setViewportBorder(vpBorder);
        }
    }


    protected void installListeners(JScrollPane c) 
    {
	vsbChangeListener = createVSBChangeListener();
	hsbChangeListener = createHSBChangeListener();
	viewportChangeListener = createViewportChangeListener();
	spPropertyChangeListener = createPropertyChangeListener();

	JViewport viewport = scrollpane.getViewport();
	JScrollBar vsb = scrollpane.getVerticalScrollBar();
	JScrollBar hsb = scrollpane.getHorizontalScrollBar();

	if (viewport != null) {
	    viewport.addChangeListener(viewportChangeListener);
	}
	if (vsb != null) {
	    vsb.getModel().addChangeListener(vsbChangeListener);
	}
	if (hsb != null) {
	    hsb.getModel().addChangeListener(hsbChangeListener);
	}

	scrollpane.addPropertyChangeListener(spPropertyChangeListener);
    }


    protected void installKeyboardActions(JScrollPane c) {
	InputMap inputMap = getInputMap(JComponent.
				  WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

	SwingUtilities.replaceUIInputMap(c, JComponent.
			       WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inputMap);
	ActionMap actionMap = getActionMap();

	SwingUtilities.replaceUIActionMap(c, actionMap);
    }

    InputMap getInputMap(int condition) {
	if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) {
	    return (InputMap)UIManager.get("ScrollPane.ancestorInputMap");
	}
	return null;
    }

    ActionMap getActionMap() {
	ActionMap map = (ActionMap)UIManager.get("ScrollPane.actionMap");

	if (map == null) {
	    map = createActionMap();
	    if (map != null) {
		UIManager.put("ScrollPane.actionMap", map);
	    }
	}
	return map;
    }

    ActionMap createActionMap() {
	ActionMap map = new ActionMapUIResource();
	map.put("scrollUp", new ScrollAction("scrollUp", SwingConstants.
						 VERTICAL, -1, true));
	map.put("scrollDown", new ScrollAction("scrollDown",
				     SwingConstants.VERTICAL, 1, true));
	map.put("scrollLeft", new ScrollAction("scrollLeft",
				  SwingConstants.HORIZONTAL, -1, true));

	map.put("scrollRight", new ScrollAction("ScrollRight",
					SwingConstants.HORIZONTAL, 1, true));
	map.put("scrollHome", new ScrollHomeAction("ScrollHome"));
	map.put("scrollEnd", new ScrollEndAction("ScrollEnd"));
	map.put("unitScrollRight", new ScrollAction
	       ("UnitScrollRight", SwingConstants.HORIZONTAL, 1, false));
	map.put("unitScrollLeft", new ScrollAction
	       ("UnitScrollLeft", SwingConstants.HORIZONTAL, -1, false));
	map.put("unitScrollUp", new ScrollAction
	       ("UnitScrollUp", SwingConstants.VERTICAL, -1,false));
	map.put("unitScrollDown", new ScrollAction
	       ("UnitScrollDown", SwingConstants.VERTICAL, 1, false));
	return map;
    }

    public void installUI(JComponent x) {
	scrollpane = (JScrollPane)x;
	installDefaults(scrollpane);
	installListeners(scrollpane);
	installKeyboardActions(scrollpane);
    }


    protected void uninstallDefaults(JScrollPane c) {
	LookAndFeel.uninstallBorder(scrollpane);

        if (scrollpane.getViewportBorder() instanceof UIResource) {
            scrollpane.setViewportBorder(null);
        }
    }


    protected void uninstallListeners(JComponent c) {
	JViewport viewport = scrollpane.getViewport();
	JScrollBar vsb = scrollpane.getVerticalScrollBar();
	JScrollBar hsb = scrollpane.getHorizontalScrollBar();

	if (viewport != null) {
	    viewport.removeChangeListener(viewportChangeListener);
	}
	if (vsb != null) {
	    vsb.getModel().removeChangeListener(vsbChangeListener);
	}
	if (hsb != null) {
	    hsb.getModel().removeChangeListener(hsbChangeListener);
	}

	scrollpane.removePropertyChangeListener(spPropertyChangeListener);

	vsbChangeListener = null;
	hsbChangeListener = null;
	viewportChangeListener = null;
	spPropertyChangeListener = null;
    }


    protected void uninstallKeyboardActions(JScrollPane c) {
	SwingUtilities.replaceUIActionMap(c, null);
	SwingUtilities.replaceUIInputMap(c, JComponent.
			   WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, null);
    }


    public void uninstallUI(JComponent c) {
	uninstallDefaults(scrollpane);
	uninstallListeners(scrollpane);
	uninstallKeyboardActions(scrollpane);
	scrollpane = null;
    }


    protected void syncScrollPaneWithViewport()
    {
	JViewport viewport = scrollpane.getViewport();
	JScrollBar vsb = scrollpane.getVerticalScrollBar();
	JScrollBar hsb = scrollpane.getHorizontalScrollBar();
	JViewport rowHead = scrollpane.getRowHeader();
	JViewport colHead = scrollpane.getColumnHeader();

	if (viewport != null) {
	    Dimension extentSize = viewport.getExtentSize();
	    Dimension viewSize = viewport.getViewSize();
	    Point viewPosition = viewport.getViewPosition();

	    if (vsb != null) {
		int extent = extentSize.height;
		int max = viewSize.height;
		int value = Math.max(0, Math.min(viewPosition.y, max - extent));
		vsb.setValues(value, extent, 0, max);
	    }

	    if (hsb != null) {
		int extent = extentSize.width;
		int max = viewSize.width;
		int value = Math.max(0, Math.min(viewPosition.x, max - extent));
		hsb.setValues(value, extent, 0, max);
	    }

	    if (rowHead != null) {
		Point p = rowHead.getViewPosition();
		p.y = viewport.getViewPosition().y;
		rowHead.setViewPosition(p);
	    }

	    if (colHead != null) {
		Point p = colHead.getViewPosition();
		p.x = viewport.getViewPosition().x;
		colHead.setViewPosition(p);
	    }
	}
    }


    /**
     * Listener for viewport events.
     */
    public class ViewportChangeHandler implements ChangeListener
    {
	public void stateChanged(ChangeEvent e) {
	    syncScrollPaneWithViewport();
	}
    }

    protected ChangeListener createViewportChangeListener() {
	return new ViewportChangeHandler();
    }


    /**
     * Horizontal scrollbar listener.
     */
    public class HSBChangeListener implements ChangeListener
    {
	public void stateChanged(ChangeEvent e)
	{
	    JViewport viewport = scrollpane.getViewport();
	    if (viewport != null) {
		BoundedRangeModel model = (BoundedRangeModel)(e.getSource());
		Point p = viewport.getViewPosition();
		p.x = model.getValue();
		viewport.setViewPosition(p);
	    }
	}
    }

    protected ChangeListener createHSBChangeListener() {
	return new HSBChangeListener();
    }


    /**
     * Vertical scrollbar listener.
     */
    public class VSBChangeListener implements ChangeListener
    {
	public void stateChanged(ChangeEvent e)
	{
	    JViewport viewport = scrollpane.getViewport();
	    if (viewport != null) {
		BoundedRangeModel model = (BoundedRangeModel)(e.getSource());
		Point p = viewport.getViewPosition();
		p.y = model.getValue();
		viewport.setViewPosition(p);
	    }
	}
    }

    protected ChangeListener createVSBChangeListener() {
	return new VSBChangeListener();
    }

    
    protected void updateScrollBarDisplayPolicy(PropertyChangeEvent e) {
	scrollpane.revalidate();
	scrollpane.repaint();
    }


    protected void updateViewport(PropertyChangeEvent e) 
    {
	JViewport oldViewport = (JViewport)(e.getOldValue());
	JViewport newViewport = (JViewport)(e.getNewValue());

	if (oldViewport != null) {
	    oldViewport.removeChangeListener(viewportChangeListener);
	}
	
	if (newViewport != null) {
	    Point p = newViewport.getViewPosition();
	    p.x = Math.max(p.x, 0);
	    p.y = Math.max(p.y, 0);
	    newViewport.setViewPosition(p);
	    newViewport.addChangeListener(viewportChangeListener);
	}
    }


    protected void updateRowHeader(PropertyChangeEvent e) 
    {
	JViewport newRowHead = (JViewport)(e.getNewValue());
	if (newRowHead != null) {
	    JViewport viewport = scrollpane.getViewport();
	    Point p = newRowHead.getViewPosition();
	    p.y = (viewport != null) ? viewport.getViewPosition().y : 0;
	    newRowHead.setViewPosition(p);
	}
    }


    protected void updateColumnHeader(PropertyChangeEvent e) 
    {
	JViewport newColHead = (JViewport)(e.getNewValue());
	if (newColHead != null) {
	    JViewport viewport = scrollpane.getViewport();
	    Point p = newColHead.getViewPosition();
	    p.x = (viewport != null) ? viewport.getViewPosition().x : 0;
	    newColHead.setViewPosition(p);
	    scrollpane.add(newColHead, COLUMN_HEADER);
	}
    }

    private void updateHorizontalScrollBar(PropertyChangeEvent pce) {
	updateScrollBar(pce, hsbChangeListener);
    }

    private void updateVerticalScrollBar(PropertyChangeEvent pce) {
	updateScrollBar(pce, vsbChangeListener);
    }

    private void updateScrollBar(PropertyChangeEvent pce, ChangeListener cl) {
	if (cl != null) {
	    JScrollBar sb = (JScrollBar)pce.getOldValue();
	    if (sb != null) {
		sb.getModel().removeChangeListener(cl);
	    }
	    sb = (JScrollBar)pce.getNewValue();
	    if (sb != null) {
		sb.getModel().addChangeListener(cl);
	    }
	}
    }

    public class PropertyChangeHandler implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            String propertyName = e.getPropertyName();

	    if (propertyName.equals("verticalScrollBarDisplayPolicy")) {
		updateScrollBarDisplayPolicy(e);
	    }
	    else if (propertyName.equals("horizontalScrollBarDisplayPolicy")) {
		updateScrollBarDisplayPolicy(e);
	    }
	    else if (propertyName.equals("viewport")) {
		updateViewport(e);
	    }
	    else if (propertyName.equals("rowHeader")) {
		updateRowHeader(e);
	    }
	    else if (propertyName.equals("columnHeader")) {
		updateColumnHeader(e);
	    }
	    else if (propertyName.equals("verticalScrollBar")) {
		updateVerticalScrollBar(e);
	    }
	    else if (propertyName.equals("horizontalScrollBar")) {
		updateHorizontalScrollBar(e);
	    }
	}
    }



    /**
     * Creates an instance of PropertyChangeListener that's added to 
     * the JScrollPane by installUI().  Subclasses can override this method
     * to return a custom PropertyChangeListener, e.g.
     * <pre>
     * class MyScrollPaneUI extends BasicScrollPaneUI {
     *    protected PropertyChangeListener <b>createPropertyListener</b>() {
     *        return new MyPropertyListener();
     *    }
     *    public class MyPropertyListener extends PropertyListener {
     *        public void propertyChange(PropertyChangeEvent e) {
     *            if (e.getPropertyName().equals("viewport")) {
     *                // do some extra work when the viewport changes
     *            }
     *            super.propertyChange(e);
     *        }
     *    }
     * }
     * </pre>
     * 
     * @see PropertyListener
     * @see #installUI
     */
    protected PropertyChangeListener createPropertyChangeListener() {
        return new PropertyChangeHandler();
    }


    /**
     * Action to scroll left/right/up/down.
     */
    private static class ScrollAction extends AbstractAction {
	/** Direction to scroll. */
	protected int orientation;
	/** 1 indicates scroll down, -1 up. */
	protected int direction;
	/** True indicates a block scroll, otherwise a unit scroll. */
	private boolean block;

	protected ScrollAction(String name, int orientation, int direction,
			       boolean block) {
	    super(name);
	    this.orientation = orientation;
	    this.direction = direction;
	    this.block = block;
	}

	public void actionPerformed(ActionEvent e) {
	    JScrollPane scrollpane = (JScrollPane)e.getSource();
	    JViewport vp = scrollpane.getViewport();
	    Component view;
	    if (vp != null && (view = vp.getView()) != null) {
		Rectangle visRect = vp.getViewRect();
		Dimension vSize = view.getSize();
		int amount;

		if (view instanceof Scrollable) {
		    if (block) {
			amount = ((Scrollable)view).getScrollableBlockIncrement
			         (visRect, orientation, direction);
		    }
		    else {
			amount = ((Scrollable)view).getScrollableUnitIncrement
			         (visRect, orientation, direction);
		    }
		}
		else {
		    if (block) {
			if (orientation == SwingConstants.VERTICAL) {
			    amount = visRect.height;
			}
			else {
			    amount = visRect.width;
			}
		    }
		    else {
			amount = 10;
		    }
		}
		if (orientation == SwingConstants.VERTICAL) {
		    visRect.y += (amount * direction);
		    if ((visRect.y + visRect.height) > vSize.height) {
			visRect.y = Math.max(0, vSize.height - visRect.height);
		    }
		    else if (visRect.y < 0) {
			visRect.y = 0;
		    }
		}
		else {
		    visRect.x += (amount * direction);
		    if ((visRect.x + visRect.width) > vSize.width) {
			visRect.x = Math.max(0, vSize.width - visRect.width);
		    }
		    else if (visRect.x < 0) {
			visRect.x = 0;
		    }
		}
		vp.setViewPosition(visRect.getLocation());
	    }
	}
    }


    /**
     * Action to scroll to x,y location of 0,0.
     */
    private static class ScrollHomeAction extends AbstractAction {
	protected ScrollHomeAction(String name) {
	    super(name);
	}

	public void actionPerformed(ActionEvent e) {
	    JScrollPane scrollpane = (JScrollPane)e.getSource();
	    JViewport vp = scrollpane.getViewport();
	    Component view;
	    if (vp != null && (view = vp.getView()) != null) {
		vp.setViewPosition(new Point(0, 0));
	    }
	}
    }


    /**
     * Action to scroll to last visible location.
     */
    private static class ScrollEndAction extends AbstractAction {
	protected ScrollEndAction(String name) {
	    super(name);
	}

	public void actionPerformed(ActionEvent e) {
	    JScrollPane scrollpane = (JScrollPane)e.getSource();
	    JViewport vp = scrollpane.getViewport();
	    Component view;
	    if (vp != null && (view = vp.getView()) != null) {
		Rectangle visRect = vp.getViewRect();
		Rectangle bounds = view.getBounds();
		vp.setViewPosition(new Point(bounds.width - visRect.width,
					     bounds.height - visRect.height));
	    }
	}
    }
}

