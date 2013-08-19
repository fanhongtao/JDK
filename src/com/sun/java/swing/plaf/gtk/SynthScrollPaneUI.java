/*
 * @(#)SynthScrollPaneUI.java	1.10 03/05/05
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

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
import java.awt.Toolkit;

/**
 * A default L&F implementation of ScrollPaneUI.
 *
 * @version 1.10, 05/05/03 (based on BasicScrollPaneUI v 1.65)
 * @author Hans Muller
 */
class SynthScrollPaneUI
    extends ScrollPaneUI implements ScrollPaneConstants, SynthUI
{
    private SynthStyle style;

    protected JScrollPane scrollpane;
    protected ChangeListener vsbChangeListener;
    protected ChangeListener hsbChangeListener;
    protected ChangeListener viewportChangeListener;
    protected PropertyChangeListener spPropertyChangeListener;
    private MouseWheelListener mouseScrollListener;

    /**
     * PropertyChangeListener installed on the vertical scrollbar.
     */
    private PropertyChangeListener vsbPropertyChangeListener;

    /**
     * PropertyChangeListener installed on the horizontal scrollbar.
     */
    private PropertyChangeListener hsbPropertyChangeListener;

    /**
     * The default implementation of createHSBPropertyChangeListener and
     * createVSBPropertyChangeListener share the PropertyChangeListener, which
     * is this ivar.
     */
    private PropertyChangeListener sbPropertyChangeListener;

    /**
     * State flag that shows whether setValue() was called from a user program
     * before the value of "extent" was set in right-to-left component
     * orientation.
     */
    private boolean setValueCalled = false;

    public static ComponentUI createUI(JComponent x) {
	return new SynthScrollPaneUI();
    }

    public static void loadActionMap(ActionMap map) {
        // NOTE: this needs to remain static. If you have a need to
        // have Actions that reference the UI in the ActionMap,
        // then you'll also need to change the registeration of the
        // ActionMap.
	map.put("scrollUp", new ScrollAction("scrollUp", SwingConstants.
						 VERTICAL, -1, true));
	map.put("scrollDown", new ScrollAction("scrollDown",
				     SwingConstants.VERTICAL, 1, true));
	map.put("scrollHome", new ScrollHomeAction("ScrollHome"));
	map.put("scrollEnd", new ScrollEndAction("ScrollEnd"));
	map.put("unitScrollUp", new ScrollAction
	       ("UnitScrollUp", SwingConstants.VERTICAL, -1,false));
	map.put("unitScrollDown", new ScrollAction
	       ("UnitScrollDown", SwingConstants.VERTICAL, 1, false));

        // PENDING: this is currently broke, the actions should do
        // the necessary mapping and not require us to reregister the actions.
        // In order to reregister actions we would have to make this
        // a per component action map.
        map.put("scrollLeft", new ScrollAction("scrollLeft",
				  SwingConstants.HORIZONTAL, -1, true));
        map.put("scrollRight", new ScrollAction("ScrollRight",
					SwingConstants.HORIZONTAL, 1, true));
        map.put("unitScrollRight", new ScrollAction
	       ("UnitScrollRight", SwingConstants.HORIZONTAL, 1, false));
        map.put("unitScrollLeft", new ScrollAction
	       ("UnitScrollLeft", SwingConstants.HORIZONTAL, -1, false));
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


    protected void installDefaults(JScrollPane scrollpane) {
        fetchStyle(scrollpane);
    }

    private void fetchStyle(JScrollPane c) {
        SynthContext context = getContext(c, ENABLED);
        SynthStyle oldStyle = style;

        style = SynthLookAndFeel.updateStyle(context, this);
        if (style != oldStyle) {
            Border vpBorder = scrollpane.getViewportBorder();
            if ((vpBorder == null) ||( vpBorder instanceof UIResource)) {
                scrollpane.setViewportBorder(new ViewportBorder(context));
            }
        }
        context.dispose();
    }


    protected void installListeners(JScrollPane c) 
    {
	vsbChangeListener = createVSBChangeListener();
        vsbPropertyChangeListener = createVSBPropertyChangeListener();
	hsbChangeListener = createHSBChangeListener();
        hsbPropertyChangeListener = createHSBPropertyChangeListener();
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
            vsb.addPropertyChangeListener(vsbPropertyChangeListener);
	}
	if (hsb != null) {
	    hsb.getModel().addChangeListener(hsbChangeListener);
            hsb.addPropertyChangeListener(hsbPropertyChangeListener);
	}

	scrollpane.addPropertyChangeListener(spPropertyChangeListener);

    mouseScrollListener = createMouseWheelListener();
    scrollpane.addMouseWheelListener(mouseScrollListener);

    }

    protected void installKeyboardActions(JScrollPane c) {
	InputMap inputMap = getInputMap(JComponent.
				  WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

	SwingUtilities.replaceUIInputMap(c, JComponent.
			       WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inputMap);
        LazyActionMap.installLazyActionMap(c, SynthScrollPaneUI.class,
                                           "ScrollPane.actionMap");
    }

    InputMap getInputMap(int condition) {
	if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) {
            SynthContext context = getContext(scrollpane, ENABLED);
            SynthStyle style = context.getStyle();

	    InputMap keyMap = (InputMap)style.get(context,
                                                "ScrollPane.ancestorInputMap");
	    InputMap rtlKeyMap;

	    if (!scrollpane.getComponentOrientation().isLeftToRight() &&
		   ((rtlKeyMap = (InputMap)style.get(context,
                   "ScrollPane.ancestorInputMap.RightToLeft")) != null)) {
		rtlKeyMap.setParent(keyMap);
		keyMap = rtlKeyMap;
	    }
            context.dispose();
            return keyMap;
	}
	return null;
    }

    public void installUI(JComponent x) {
	scrollpane = (JScrollPane)x;
	installDefaults(scrollpane);
	installListeners(scrollpane);
	installKeyboardActions(scrollpane);
    }


    protected void uninstallDefaults(JScrollPane c) {
        SynthContext context = getContext(c, ENABLED);

        style.uninstallDefaults(context);
        context.dispose();

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
            vsb.removePropertyChangeListener(vsbPropertyChangeListener);
	}
	if (hsb != null) {
	    hsb.getModel().removeChangeListener(hsbChangeListener);
            hsb.removePropertyChangeListener(hsbPropertyChangeListener);
	}

	scrollpane.removePropertyChangeListener(spPropertyChangeListener);

    if (mouseScrollListener != null) {
        scrollpane.removeMouseWheelListener(mouseScrollListener);
    }

	vsbChangeListener = null;
	hsbChangeListener = null;
	viewportChangeListener = null;
	spPropertyChangeListener = null;
        mouseScrollListener = null;
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


    public SynthContext getContext(JComponent c) {
        return getContext(c, getComponentState(c));
    }

    public SynthContext getContext(JComponent c, Region region) {
        return SynthContext.getContext(SynthContext.class, c,
                                       region, style, getComponentState(c));
    }

    private SynthContext getContext(JComponent c, int state) {
        return SynthContext.getContext(SynthContext.class, c,
                    SynthLookAndFeel.getRegion(c), style, state);
    }


    private Region getRegion(JComponent c) {
        return SynthLookAndFeel.getRegion(c);
    }


    private int getComponentState(JComponent c) {
        return SynthLookAndFeel.getComponentState(c);
    }


    protected void syncScrollPaneWithViewport()
    {
	JViewport viewport = scrollpane.getViewport();
	JScrollBar vsb = scrollpane.getVerticalScrollBar();
	JScrollBar hsb = scrollpane.getHorizontalScrollBar();
	JViewport rowHead = scrollpane.getRowHeader();
	JViewport colHead = scrollpane.getColumnHeader();
	boolean ltr = scrollpane.getComponentOrientation().isLeftToRight();

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
		int value;

		if (ltr) {
		    value = Math.max(0, Math.min(viewPosition.x, max - extent));
		} else {
		    int currentValue = hsb.getValue();

		    /* Use a particular formula to calculate "value"
		     * until effective x coordinate is calculated.
		     */
		    if (setValueCalled && ((max - currentValue) == viewPosition.x)) {
			value = Math.max(0, Math.min(max - extent, currentValue));
			/* After "extent" is set, turn setValueCalled flag off.
			 */
			if (extent != 0) {
			    setValueCalled = false;
			}
		    } else {
			if (extent > max) {
			    viewPosition.x = max - extent;
			    viewport.setViewPosition(viewPosition);
			    value = 0;
			} else {
			   /* The following line can't handle a small value of
			    * viewPosition.x like Integer.MIN_VALUE correctly
			    * because (max - extent - viewPositoiin.x) causes
			    * an overflow. As a result, value becomes zero.
			    * (e.g. setViewPosition(Integer.MAX_VALUE, ...)
			    *       in a user program causes a overflow.
			    *       Its expected value is (max - extent).)
			    * However, this seems a trivial bug and adding a
			    * fix makes this often-called method slow, so I'll
			    * leave it until someone claims.
			    */
			    value = Math.max(0, Math.min(max - extent, max - extent - viewPosition.x));
			}
		    }
		}
		hsb.setValues(value, extent, 0, max);
	    }

	    if (rowHead != null) {
		Point p = rowHead.getViewPosition();
		p.y = viewport.getViewPosition().y;
                p.x = 0;
		rowHead.setViewPosition(p);
	    }

	    if (colHead != null) {
		Point p = colHead.getViewPosition();
		if (ltr) {
		    p.x = viewport.getViewPosition().x;
		} else {
		    p.x = Math.max(0, viewport.getViewPosition().x);
		}
                p.y = 0;
		colHead.setViewPosition(p);
	    }
	}
    }


    /**
     * Listener for viewport events.
     */
    class ViewportChangeHandler implements ChangeListener
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
    class HSBChangeListener implements ChangeListener
    {
	public void stateChanged(ChangeEvent e)
	{
	    JViewport viewport = scrollpane.getViewport();
	    if (viewport != null) {
		BoundedRangeModel model = (BoundedRangeModel)(e.getSource());
		Point p = viewport.getViewPosition();
		int value = model.getValue();
		if (scrollpane.getComponentOrientation().isLeftToRight()) {
		    p.x = value;
		} else {
		    int max = viewport.getViewSize().width;
		    int extent = viewport.getExtentSize().width;
		    int oldX = p.x;

		    /* Set new X coordinate based on "value".
		     */
		    p.x = max - extent - value;

		    /* If setValue() was called before "extent" was fixed,
		     * turn setValueCalled flag on.
		     */
		    if ((extent == 0) && (value != 0) && (oldX == max)) {
			setValueCalled = true;
		    } else {
			/* When a pane without a horizontal scroll bar was
			 * reduced and the bar appeared, the viewport should
			 * show the right side of the view.
			 */
			if ((extent != 0) && (oldX < 0) && (p.x == 0)) {
			    p.x += value;
			}
		    }
		}
		viewport.setViewPosition(p);
	    }
	}
    }

    /**
     * Returns a <code>PropertyChangeListener</code> that will be installed
     * on the horizontal <code>JScrollBar</code>.
     */
    private PropertyChangeListener createHSBPropertyChangeListener() {
        return getSBPropertyChangeListener();
    }

    /**
     * Returns a shared <code>PropertyChangeListener</code> that will update
     * the listeners installed on the scrollbars as the model changes.
     */
    private PropertyChangeListener getSBPropertyChangeListener() {
        if (sbPropertyChangeListener == null) {
            sbPropertyChangeListener = new ScrollBarPropertyChangeHandler();
        }
        return sbPropertyChangeListener;
    }

    protected ChangeListener createHSBChangeListener() {
	return new HSBChangeListener();
    }


    /**
     * Vertical scrollbar listener.
     */
    class VSBChangeListener implements ChangeListener
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


    /**
     * PropertyChangeListener for the ScrollBars.
     */
    private class ScrollBarPropertyChangeHandler implements
                               PropertyChangeListener {
        // Listens for changes in the model property and reinstalls the
        // horizontal/vertical PropertyChangeListeners.
        public void propertyChange(PropertyChangeEvent e) {
            String propertyName = e.getPropertyName();
            Object source = e.getSource();

            if ("model".equals(propertyName)) {
                JScrollBar sb = scrollpane.getVerticalScrollBar();
                BoundedRangeModel oldModel = (BoundedRangeModel)e.
                                     getOldValue();
                ChangeListener cl = null;

                if (source == sb) {
                    cl = vsbChangeListener;
                }
                else if (source == scrollpane.getHorizontalScrollBar()) {
                    sb = scrollpane.getHorizontalScrollBar();
                    cl = hsbChangeListener;
                }
                if (cl != null) {
                    if (oldModel != null) {
                        oldModel.removeChangeListener(cl);
                    }
                    if (sb.getModel() != null) {
                        sb.getModel().addChangeListener(cl);
                    }
                }
            }
            else if ("componentOrientation".equals(propertyName)) {
                if (source == scrollpane.getHorizontalScrollBar()) {
		    JScrollBar hsb = scrollpane.getHorizontalScrollBar();
 		    JViewport viewport = scrollpane.getViewport();
                    Point p = viewport.getViewPosition();
                    if (scrollpane.getComponentOrientation().isLeftToRight()) {
                        p.x = hsb.getValue();
                    } else {
                        p.x = viewport.getViewSize().width - viewport.getExtentSize().width - hsb.getValue();
                    }
                    viewport.setViewPosition(p);
                }
            }
        }
    }

    /**
     * Returns a <code>PropertyChangeListener</code> that will be installed
     * on the vertical <code>JScrollBar</code>.
     */
    private PropertyChangeListener createVSBPropertyChangeListener() {
        return getSBPropertyChangeListener();
    }

    protected ChangeListener createVSBChangeListener() {
	return new VSBChangeListener();
    }

    /**
     * MouseWheelHandler is an inner class which implements the 
     * MouseWheelListener interface.  MouseWheelHandler responds to 
     * MouseWheelEvents by scrolling the JScrollPane appropriately.
     * If the scroll pane's
     * <code>isWheelScrollingEnabled</code>
     * method returns false, no scrolling occurs.
     * 
     * @see javax.swing.JScrollPane#isWheelScrollingEnabled
     * @see #createMouseWheelListener
     * @see java.awt.event.MouseWheelListener
     * @see java.awt.event.MouseWheelEvent
     * @since 1.4
     */
    protected class MouseWheelHandler implements MouseWheelListener {
        /**
         * Called when the mouse wheel is rotated while over a
         * JScrollPane.
         *
         * @param e     MouseWheelEvent to be handled
         * @since 1.4
         */
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (scrollpane.isWheelScrollingEnabled() &&
                e.getScrollAmount() != 0) {
                JScrollBar toScroll = scrollpane.getVerticalScrollBar();
                int direction = 0;
                // find which scrollbar to scroll, or return if none
                if (toScroll == null || !toScroll.isVisible()) { 
                    toScroll = scrollpane.getHorizontalScrollBar();
                    if (toScroll == null || !toScroll.isVisible()) { 
                        return;
                    }
                }
                direction = e.getWheelRotation() < 0 ? -1 : 1;
                if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
                    SynthScrollBarUI.scrollByUnits(toScroll, direction,
                                                         e.getScrollAmount());
                }
                else if (e.getScrollType() ==
                         MouseWheelEvent.WHEEL_BLOCK_SCROLL) {
                    SynthScrollBarUI.scrollByBlock(toScroll, direction);
                }
            }
        }
    }

    /**
     * Creates an instance of MouseWheelListener, which is added to the
     * JScrollPane by installUI().  The returned MouseWheelListener is used
     * to handle mouse wheel-driven scrolling.
     *
     * @return      MouseWheelListener which implements wheel-driven scrolling
     * @see #installUI
     * @see MouseWheelHandler
     * @since 1.4
     */
    protected MouseWheelListener createMouseWheelListener() {
        return new MouseWheelHandler();
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
	    if (scrollpane.getComponentOrientation().isLeftToRight()) {
		p.x = Math.max(p.x, 0);
	    } else {
		int max = newViewport.getViewSize().width;
		int extent = newViewport.getExtentSize().width;
		if (extent > max) {
		    p.x = max - extent;
		} else {
		    p.x = Math.max(0, Math.min(max - extent, p.x));
		}
	    }
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
	    if (viewport == null) {
		p.x = 0;
	    } else {
		if (scrollpane.getComponentOrientation().isLeftToRight()) {
		    p.x = viewport.getViewPosition().x;
		} else {
		    p.x = Math.max(0, viewport.getViewPosition().x);
		}
	    }
	    newColHead.setViewPosition(p);
	    scrollpane.add(newColHead, COLUMN_HEADER);
	}
    }

    private void updateHorizontalScrollBar(PropertyChangeEvent pce) {
	updateScrollBar(pce, hsbChangeListener, hsbPropertyChangeListener);
    }

    private void updateVerticalScrollBar(PropertyChangeEvent pce) {
	updateScrollBar(pce, vsbChangeListener, vsbPropertyChangeListener);
    }

    private void updateScrollBar(PropertyChangeEvent pce, ChangeListener cl,
                                 PropertyChangeListener pcl) {
        JScrollBar sb = (JScrollBar)pce.getOldValue();
        if (sb != null) {
            if (cl != null) {
                sb.getModel().removeChangeListener(cl);
            }
            if (pcl != null) {
                sb.removePropertyChangeListener(pcl);
            }
        }
        sb = (JScrollBar)pce.getNewValue();
        if (sb != null) {
            if (cl != null) {
                sb.getModel().addChangeListener(cl);
            }
            if (pcl != null) {
                sb.addPropertyChangeListener(pcl);
            }
	}
    }

    class PropertyChangeHandler implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            String propertyName = e.getPropertyName();

            if (SynthLookAndFeel.shouldUpdateStyle(e)) {
                fetchStyle((JScrollPane)e.getSource());
            }
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
	    else if (propertyName.equals("componentOrientation")) {
		scrollpane.revalidate();
		scrollpane.repaint();

		InputMap inputMap = getInputMap(JComponent.
					WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		SwingUtilities.replaceUIInputMap(scrollpane, JComponent.
				WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inputMap);
	    }
	}
    }



    /**
     * Creates an instance of PropertyChangeListener that's added to 
     * the JScrollPane by installUI().  Subclasses can override this method
     * to return a custom PropertyChangeListener, e.g.
     * <pre>
     * class MyScrollPaneUI extends BasicScrollPaneUI {
     *    protected PropertyChangeListener <b>createPropertyChangeListener</b>() {
     *        return new MyPropertyChangeListener();
     *    }
     *    class MyPropertyChangeListener extends PropertyChangeListener {
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
     * @see java.beans.PropertyChangeListener
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
		    if (scrollpane.getComponentOrientation().isLeftToRight()) {
			visRect.x += (amount * direction);
			if ((visRect.x + visRect.width) > vSize.width) {
			    visRect.x = Math.max(0, vSize.width - visRect.width);
			} else if (visRect.x < 0) {
			    visRect.x = 0;
			}
		    } else {
			visRect.x -= (amount * direction);
                        if (visRect.width > vSize.width) {
                            visRect.x = vSize.width - visRect.width;
                        } else {
                            visRect.x = Math.max(0, Math.min(vSize.width - visRect.width, visRect.x));
			}
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
		if (scrollpane.getComponentOrientation().isLeftToRight()) {
		    vp.setViewPosition(new Point(0, 0));
		} else {
		    Rectangle visRect = vp.getViewRect();
		    Rectangle bounds = view.getBounds();
		    vp.setViewPosition(new Point(bounds.width - visRect.width, 0));
		}
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
		if (scrollpane.getComponentOrientation().isLeftToRight()) {
		    vp.setViewPosition(new Point(bounds.width - visRect.width,
					     bounds.height - visRect.height));
		} else {
		    vp.setViewPosition(new Point(0, 
					     bounds.height - visRect.height));
		}
	    }
	}
    }


    private class ViewportBorder extends AbstractBorder implements UIResource {
        private Insets insets;

        ViewportBorder(SynthContext context) {
            this.insets = (Insets)context.getStyle().get(context,
                                            "ScrollPane.viewportBorderInsets");
            if (this.insets == null) {
                this.insets = SynthLookAndFeel.EMPTY_UIRESOURCE_INSETS;
            }
        }

        public void paintBorder(Component c, Graphics g, int x, int y,
                            int width, int height) {
            JComponent jc = (JComponent)c;
            SynthContext context = getContext(jc, Region.VIEWPORT);
            SynthStyle style = context.getStyle();
            if (style == null) {
                assert false: "SynthBorder is being used outside after the " +
                              " UI has been uninstalled";
                return;
            }
            SynthPainter painter = (SynthPainter)style.get(context,
                                          "ScrollPane.viewportBorderPainter");

            if (painter != null) {
                painter.paint(context, "border", g, x, y, width, height);
            }
            context.dispose();
        }

        public Insets getBorderInsets(Component c) { 
            return getBorderInsets(c, null);
        }

        public Insets getBorderInsets(Component c, Insets insets) {
            if (insets == null) {
                return new Insets(this.insets.top, this.insets.left,
                                  this.insets.bottom, this.insets.right);
            }
            insets.top = this.insets.top;
            insets.bottom = this.insets.bottom;
            insets.left = this.insets.left;
            insets.right = this.insets.left;
            return insets;
        }

        public boolean isBorderOpaque() {
            return false;
        }
    }
}
