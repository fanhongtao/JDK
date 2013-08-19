/*
 * @(#)SynthScrollBarUI.java	1.24 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import java.awt.*;
import java.awt.event.*;

import java.beans.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;


/**
 * Implementation of ScrollBarUI for the Basic Look and Feel
 *
 * @version 1.24, 01/23/03 (based on BasicScrollBarUI v 1.74)
 * @author Rich Schiavi
 * @author David Kloba
 * @author Hans Muller
 */
// ScrollBar consists of the following parts:
//   Button to increase the current value
//   Button to decrese the current value
//   Track
//   Thumb
class SynthScrollBarUI extends ScrollBarUI implements SynthUI, LayoutManager, SwingConstants, FocusListener {
    private static final int POSITIVE_SCROLL = 1;
    private static final int NEGATIVE_SCROLL = -1;

    private static final int MIN_SCROLL = 2;
    private static final int MAX_SCROLL = 3;

    private static final Insets tmpInsets = new Insets(0, 0, 0, 0);

    private SynthStyle style;
    private SynthStyle thumbStyle;
    private SynthStyle trackStyle;

    protected Dimension minimumThumbSize;
    protected Dimension maximumThumbSize;

    protected JScrollBar scrollbar;
    protected JButton incrButton;
    protected JButton decrButton;
    protected boolean isDragging;
    protected TrackListener trackListener;
    protected ArrowButtonListener buttonListener;
    protected ModelListener modelListener;

    protected Rectangle thumbRect;
    protected Rectangle trackRect;

    protected int trackHighlight;

    protected static final int NO_HIGHLIGHT = 0;
    protected static final int DECREASE_HIGHLIGHT = 1;
    protected static final int INCREASE_HIGHLIGHT = 2;

    protected ScrollListener scrollListener;
    protected PropertyChangeListener propertyChangeListener; 
    protected Timer scrollTimer;

    /**
     * True if the mouse is over the thumb.
     */
    private boolean thumbActive;

    private final static int scrollSpeedThrottle = 60; // delay in milli seconds

    /** True indicates a middle click will absolutely position the
     * scrollbar. */
    private boolean supportsAbsolutePositioning;

    /** Hint as to what width (when vertical) or height (when horizontal)
     * should be.
     */
    private int scrollBarWidth;


    public static ComponentUI createUI(JComponent c)    {
        return new SynthScrollBarUI();
    }

    public static void loadActionMap(ActionMap map) {
        // NOTE: this needs to remain static. If you have a need to
        // have Actions that reference the UI in the ActionMap,
        // then you'll also need to change the registeration of the
        // ActionMap.
        map.put("positiveUnitIncrement", new SharedActionScroller
		(POSITIVE_SCROLL, false));
        map.put("positiveBlockIncrement", new SharedActionScroller
		(POSITIVE_SCROLL, true));
        map.put("negativeUnitIncrement", new SharedActionScroller
		(NEGATIVE_SCROLL, false));
        map.put("negativeBlockIncrement", new SharedActionScroller
		(NEGATIVE_SCROLL, true));
        map.put("minScroll", new SharedActionScroller(MIN_SCROLL, true));
        map.put("maxScroll", new SharedActionScroller(MAX_SCROLL, true));
    }


    public void installUI(JComponent c)   {
	scrollbar = (JScrollBar)c;
        thumbRect = new Rectangle(0, 0, 0, 0);
        trackRect = new Rectangle(0, 0, 0, 0);
	installDefaults();
	installComponents();
	installKeyboardActions();
	installListeners();
    }

    public void uninstallUI(JComponent c) {
	scrollbar = (JScrollBar)c;
	uninstallListeners();
	uninstallKeyboardActions();
	uninstallComponents();
	uninstallDefaults();
	c.setLayout(null);
	thumbRect = null;
	scrollbar = null;
	incrButton = null;
	decrButton = null;
    }
    

    protected void installDefaults() {
        fetchStyle(scrollbar);
	trackHighlight = NO_HIGHLIGHT;
        scrollbar.setLayout(this);
    }

    private void fetchStyle(JScrollBar c) {
        SynthContext context = getContext(c, ENABLED);
        SynthStyle oldStyle = style;
        style = SynthLookAndFeel.updateStyle(context, this);
        // Add properties other than JComponent colors, Borders and
        // opacity settings here:
        if (style != oldStyle) {
            Insets insets = c.getInsets();
            scrollBarWidth =
                style.getInt(context, "ScrollBar.thumbHeight", 14);
            minimumThumbSize = new Dimension();
            if (c.getOrientation() == JScrollBar.VERTICAL) {
                    minimumThumbSize.width = scrollBarWidth;
                    minimumThumbSize.height = 7;
                    scrollBarWidth += insets.left + insets.right;
            } else {
                    minimumThumbSize.width = 7;
                    minimumThumbSize.height = scrollBarWidth;
                    scrollBarWidth += insets.top + insets.bottom;
            }
            maximumThumbSize = (Dimension)style.get(context,
                        "ScrollBar.maximumThumbSize");
            supportsAbsolutePositioning = style.getBoolean(context,
                        "ScrollBar.allowsAbsolutePositioning", false);
        }
        context.dispose();

        context = getContext(c, Region.SCROLL_BAR_TRACK, ENABLED);
        trackStyle = SynthLookAndFeel.updateStyle(context, this);
        context.dispose();

        context = getContext(c, Region.SCROLL_BAR_THUMB, ENABLED);
        thumbStyle = SynthLookAndFeel.updateStyle(context, this);
        context.dispose();
    }

    protected void installComponents(){
        switch (scrollbar.getOrientation()) {
        case JScrollBar.VERTICAL:
            incrButton = createIncreaseButton(SOUTH);
            decrButton = createDecreaseButton(NORTH);
            break;
            
        case JScrollBar.HORIZONTAL:
            if (scrollbar.getComponentOrientation().isLeftToRight()) {    
                incrButton = createIncreaseButton(EAST);
                decrButton = createDecreaseButton(WEST);
            } else {
                incrButton = createIncreaseButton(WEST);
                decrButton = createDecreaseButton(EAST);
            }
            break;
        }
        scrollbar.add(incrButton);
        scrollbar.add(decrButton);
    }

    protected void uninstallComponents() {
	scrollbar.remove(incrButton);
	scrollbar.remove(decrButton);
    }

    protected void installListeners() {
	trackListener = createTrackListener();
    	buttonListener = createArrowButtonListener();
    	modelListener = createModelListener();
	propertyChangeListener = createPropertyChangeListener();

	scrollbar.addMouseListener(trackListener);
	scrollbar.addMouseMotionListener(trackListener);
        scrollbar.getModel().addChangeListener(modelListener);
	scrollbar.addPropertyChangeListener(propertyChangeListener);

        if (incrButton != null) {
            incrButton.addMouseListener(buttonListener);
	}
        if (decrButton != null)	{
            decrButton.addMouseListener(buttonListener);
	}

	scrollListener = createScrollListener();
	scrollTimer = new Timer(scrollSpeedThrottle, scrollListener);
	scrollTimer.setInitialDelay(300);  // default InitialDelay?
        // PENDING: there should be a property for this to avoid installing
        // the listener unless necessary.
        scrollbar.addFocusListener(this);
    }

    protected void installKeyboardActions() {
        LazyActionMap.installLazyActionMap(scrollbar, SynthScrollBarUI.class,
                                           "ScrollBar.actionMap");

	InputMap inputMap = getInputMap(JComponent.WHEN_FOCUSED);
	SwingUtilities.replaceUIInputMap(scrollbar, JComponent.WHEN_FOCUSED,
					 inputMap);
    }

    protected void uninstallKeyboardActions(){
	SwingUtilities.replaceUIInputMap(scrollbar, JComponent.WHEN_FOCUSED,
					 null);
	SwingUtilities.replaceUIActionMap(scrollbar, null);
    }

    private InputMap getInputMap(int condition) {
	if (condition == JComponent.WHEN_FOCUSED) {
            SynthContext context = getContext(scrollbar, ENABLED);
            SynthStyle style = context.getStyle();
            InputMap keyMap = (InputMap)style.get(context,
                                                  "ScrollBar.focusInputMap");
            InputMap rtlKeyMap;

            if (scrollbar.getComponentOrientation().isLeftToRight() &&
                ((rtlKeyMap = (InputMap)style.get(context,
                      "ScrollBar.focusInputMap.RightToLeft")) != null)) {
                rtlKeyMap.setParent(keyMap);
                keyMap = rtlKeyMap;
            }
            context.dispose();
            return keyMap;
	}
	return null;
    }

    protected void uninstallListeners() {
	scrollTimer.stop();
	scrollTimer = null;

	if (decrButton != null){
	    decrButton.removeMouseListener(buttonListener);
	}
	if (incrButton != null){
	    incrButton.removeMouseListener(buttonListener);
	}
    
	scrollbar.getModel().removeChangeListener(modelListener);
	scrollbar.removeMouseListener(trackListener);
	scrollbar.removeMouseMotionListener(trackListener);
	scrollbar.removePropertyChangeListener(propertyChangeListener);
        scrollbar.removeFocusListener(this);
    }


    protected void uninstallDefaults(){
        SynthContext context = getContext(scrollbar, ENABLED);
        style.uninstallDefaults(context);
        context.dispose();
        style = null;

        context = getContext(scrollbar, Region.SCROLL_BAR_TRACK, ENABLED);
        trackStyle.uninstallDefaults(context);
        context.dispose();
        trackStyle = null;

        context = getContext(scrollbar, Region.SCROLL_BAR_THUMB, ENABLED);
        thumbStyle.uninstallDefaults(context);
        context.dispose();
        thumbStyle = null;
    }


    public SynthContext getContext(JComponent c) {
        return getContext(c, getComponentState(c));
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

    private SynthContext getContext(JComponent c, Region region) {
        return getContext(c, region, getComponentState(c, region));
    }

    private SynthContext getContext(JComponent c, Region region, int state) {
        SynthStyle style = trackStyle;

        if (region == Region.SCROLL_BAR_THUMB) {
            style = thumbStyle;
        }
        return SynthContext.getContext(SynthContext.class, c, region, style,
                                       state);
    }

    private int getComponentState(JComponent c, Region region) {
        if (region == Region.SCROLL_BAR_THUMB && thumbActive &&c.isEnabled()) {
            return MOUSE_OVER;
        }
        return SynthLookAndFeel.getComponentState(c);
    }

    protected TrackListener createTrackListener(){
	return new TrackListener();
    }

    protected ArrowButtonListener createArrowButtonListener(){
	return new ArrowButtonListener();
    }
    
    protected ModelListener createModelListener(){
	return new ModelListener();
    }

    protected ScrollListener createScrollListener(){
	return new ScrollListener();
    }
    
    protected PropertyChangeListener createPropertyChangeListener() {
	return new PropertyChangeHandler();
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
        SynthContext subcontext = getContext(scrollbar,
                                             Region.SCROLL_BAR_TRACK);
	paintTrack(subcontext, g, getTrackBounds());
        subcontext.dispose();

        subcontext = getContext(scrollbar, Region.SCROLL_BAR_THUMB);
	paintThumb(subcontext, g, getThumbBounds());
        subcontext.dispose();
    }

    protected void paintTrack(SynthContext ss, Graphics g,
                              Rectangle trackBounds) {
        SynthLookAndFeel.updateSubregion(ss, g, trackBounds);
    }

    protected void paintThumb(SynthContext ss, Graphics g,
                              Rectangle thumbBounds) {
        SynthLookAndFeel.updateSubregion(ss, g, thumbBounds);
    }

    int getTrackHighlight() {
        return trackHighlight;
    }

    Rectangle getDecreaseHighlightRegion(Rectangle rect) {
	Insets insets = scrollbar.getInsets(tmpInsets);
	Rectangle thumbR = getThumbBounds();

	if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            rect.x = insets.left;
	    rect.y = decrButton.getY() + decrButton.getHeight();
	    rect.width = scrollbar.getWidth() - (insets.left + insets.right);
	    rect.height = thumbR.y - rect.y;
	}
	else {
	    rect.x = decrButton.getX() + decrButton.getHeight();
	    rect.y = insets.top;
	    rect.width = thumbR.x - rect.x;
	    rect.height = scrollbar.getHeight() - (insets.top + insets.bottom);
	}
        return rect;
    }

    Rectangle getIncreaseHighlightRegion(Rectangle rect) {
	Insets insets = scrollbar.getInsets(tmpInsets);
	Rectangle thumbR = getThumbBounds();

	if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
	    rect.x = insets.left;
	    rect.y = thumbR.y + thumbR.height;
	    rect.width = scrollbar.getWidth() - (insets.left + insets.right);
	    rect.height = incrButton.getY() - rect.y;
	} 
	else {
	    rect.x = thumbR.x + thumbR.width;
	    rect.y = insets.top;
	    rect.width = incrButton.getX() - rect.x;
	    rect.height = scrollbar.getHeight() - (insets.top + insets.bottom);
	}
        return rect;
    }

    /**
     * A vertical scrollbar's preferred width is the maximum of 
     * preferred widths of the (non <code>null</code>)
     * increment/decrement buttons,
     * and the minimum width of the thumb. The preferred height is the 
     * sum of the preferred heights of the same parts.  The basis for 
     * the preferred size of a horizontal scrollbar is similar. 
     * <p>
     * The <code>preferredSize</code> is only computed once, subsequent
     * calls to this method just return a cached size.
     * 
     * @param c the <code>JScrollBar</code> that's delegating this method to us
     * @return the preferred size of a Basic JScrollBar
     * @see #getMaximumSize
     * @see #getMinimumSize
     */
    public Dimension getPreferredSize(JComponent c) {
	return (scrollbar.getOrientation() == JScrollBar.VERTICAL)
	    ? new Dimension(scrollBarWidth, 48)
	    : new Dimension(48, scrollBarWidth);
    }


    /**
     * A vertical scrollbar's minimum width is the largest 
     * minimum width of the (non <code>null</code>) increment/decrement buttons,
     * and the minimum width of the thumb. The minimum height is the 
     * sum of the minimum heights of the same parts.  The basis for 
     * the preferred size of a horizontal scrollbar is similar. 
     * <p>
     * The <code>minimumSize</code> is only computed once, subsequent
     * calls to this method just return a cached size. 
     * 
     * @param c the <code>JScrollBar</code> that's delegating this method to us
     * @return the minimum size of a basic <code>JScrollBar</code>
     * @see #getMaximumSize
     * @see #getPreferredSize
     */
    public Dimension getMinimumSize(JComponent c) {
	return getPreferredSize(c);
    }

    
    /**
     * @param c The JScrollBar that's delegating this method to us.
     * @return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
     * @see #getMinimumSize
     * @see #getPreferredSize
     */
    public Dimension getMaximumSize(JComponent c) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    	    
    protected JButton createDecreaseButton(int orientation)  {
        SynthArrowButton synthArrowButton = new SynthArrowButton(orientation);
        synthArrowButton.setName("ScrollBar.button");
        return synthArrowButton;
    }

    protected JButton createIncreaseButton(int orientation)  {
        SynthArrowButton synthArrowButton = new SynthArrowButton(orientation);
        synthArrowButton.setName("ScrollBar.button");
        return synthArrowButton;
    }
    /** 
     * Return the smallest acceptable size for the thumb.  If the scrollbar
     * becomes so small that this size isn't available, the thumb will be
     * hidden.  
     * <p>
     * <b>Warning </b>: the value returned by this method should not be
     * be modified, it's a shared static constant.
     *
     * @return The smallest acceptable size for the thumb.
     * @see #getMaximumThumbSize
     */
    protected Dimension getMinimumThumbSize() { 
	return minimumThumbSize;
    }

    /** 
     * Return the largest acceptable size for the thumb.  To create a fixed 
     * size thumb one make this method and <code>getMinimumThumbSize</code> 
     * return the same value.
     * <p>
     * <b>Warning </b>: the value returned by this method should not be
     * be modified, it's a shared static constant.
     *
     * @return The largest acceptable size for the thumb.
     * @see #getMinimumThumbSize
     */
    protected Dimension getMaximumThumbSize()	{ 
	return maximumThumbSize;
    }


    /*
     * LayoutManager Implementation
     */

    public void addLayoutComponent(String name, Component child) {}
    public void removeLayoutComponent(Component child) {}
    
    public Dimension preferredLayoutSize(Container scrollbarContainer)  {
        return getPreferredSize((JComponent)scrollbarContainer);
    }
    
    public Dimension minimumLayoutSize(Container scrollbarContainer) {
        return getMinimumSize((JComponent)scrollbarContainer);
    }
    

    protected void layoutVScrollbar(JScrollBar sb)  
    {
        Dimension sbSize = sb.getSize();
        Insets sbInsets = sb.getInsets();

	/*
	 * Width and left edge of the buttons and thumb.
	 */
        // Same size as the thumb.
	int itemW = sbSize.width - (sbInsets.left + sbInsets.right);
	int itemX = sbInsets.left;
        
        /* Nominal locations of the buttons, assuming their preferred
	 * size will fit.
	 */
        int decrButtonH = itemW;
        int decrButtonY = sbInsets.top;
        
        int incrButtonH = itemW;
        int incrButtonY = sbSize.height - (sbInsets.bottom + incrButtonH);
        
        /* The thumb must fit within the height left over after we
	 * subtract the preferredSize of the buttons and the insets.
	 */
        int sbInsetsH = sbInsets.top + sbInsets.bottom;
        int sbButtonsH = decrButtonH + incrButtonH;
        float trackH = sbSize.height - (sbInsetsH + sbButtonsH);
        
        /* Compute the height and origin of the thumb.   The case
	 * where the thumb is at the bottom edge is handled specially 
	 * to avoid numerical problems in computing thumbY.  Enforce
	 * the thumbs min/max dimensions.  If the thumb doesn't
	 * fit in the track (trackH) we'll hide it later.
	 */
	float min = sb.getMinimum();
	float extent = sb.getVisibleAmount();
	float range = sb.getMaximum() - min;
	float value = sb.getValue();

        int thumbH = (range <= 0) 
	    ? getMaximumThumbSize().height : (int)(trackH * (extent / range));
	thumbH = Math.max(thumbH, getMinimumThumbSize().height);
	thumbH = Math.min(thumbH, getMaximumThumbSize().height);
        
	int thumbY = incrButtonY - thumbH;  
	if (sb.getValue() < (sb.getMaximum() - sb.getVisibleAmount())) {
	    float thumbRange = trackH - thumbH;
	    thumbY = (int)(0.5f + (thumbRange * ((value - min) / (range - extent))));
	    thumbY +=  decrButtonY + decrButtonH;
	}

        /* If the buttons don't fit, allocate half of the available 
	 * space to each and move the lower one (incrButton) down.
	 */
        int sbAvailButtonH = (sbSize.height - sbInsetsH);
        if (sbAvailButtonH < sbButtonsH) {
            incrButtonH = decrButtonH = sbAvailButtonH / 2;
            incrButtonY = sbSize.height - (sbInsets.bottom + incrButtonH);
        }
        decrButton.setBounds(itemX, decrButtonY, itemW, decrButtonH);
        incrButton.setBounds(itemX, incrButtonY, itemW, incrButtonH);

	/* Update the trackRect field.
	 */	
	int itrackY = decrButtonY + decrButtonH;
	int itrackH = incrButtonY - itrackY;
	trackRect.setBounds(itemX, itrackY, itemW, itrackH);
	
	/* If the thumb isn't going to fit, zero it's bounds.  Otherwise
	 * make sure it fits between the buttons.  Note that setting the
	 * thumbs bounds will cause a repaint.
	 */
	if(thumbH >= (int)trackH)	{
	    setThumbBounds(0, 0, 0, 0);
	}
	else {
	    if ((thumbY + thumbH) > incrButtonY) {
		thumbY = incrButtonY - thumbH;
	    }
	    if (thumbY  < (decrButtonY + decrButtonH)) {
		thumbY = decrButtonY + decrButtonH + 1;
	    }
	    setThumbBounds(itemX, thumbY, itemW, thumbH);
	}
    }
    

    protected void layoutHScrollbar(JScrollBar sb)  
    {
        Dimension sbSize = sb.getSize();
        Insets sbInsets = sb.getInsets();
        
	/* Height and top edge of the buttons and thumb.
	 */
	int itemH = sbSize.height - (sbInsets.top + sbInsets.bottom);
	int itemY = sbInsets.top;

        boolean ltr = sb.getComponentOrientation().isLeftToRight();

        /* Nominal locations of the buttons, assuming their preferred
	 * size will fit.
	 */
        int leftButtonW = itemH;
        int leftButtonX = sbInsets.left;
        
        int rightButtonW = itemH;
        int rightButtonX = sbSize.width - (sbInsets.right + rightButtonW);
        
        /* The thumb must fit within the width left over after we
	 * subtract the preferredSize of the buttons and the insets.
	 */
        int sbInsetsW = sbInsets.left + sbInsets.right;
        int sbButtonsW = leftButtonW + rightButtonW;
        float trackW = sbSize.width - (sbInsetsW + sbButtonsW);
        
        /* Compute the width and origin of the thumb.  Enforce
	 * the thumbs min/max dimensions.  The case where the thumb 
	 * is at the right edge is handled specially to avoid numerical 
	 * problems in computing thumbX.  If the thumb doesn't
	 * fit in the track (trackH) we'll hide it later.
	 */
        float min = sb.getMinimum();
        float max = sb.getMaximum();
        float extent = sb.getVisibleAmount();
        float range = max - min;
        float value = sb.getValue();

        int thumbW = (range <= 0) 
	    ? getMaximumThumbSize().width : (int)(trackW * (extent / range));
        thumbW = Math.max(thumbW, getMinimumThumbSize().width);
        thumbW = Math.min(thumbW, getMaximumThumbSize().width);
        
	int thumbX = ltr ? rightButtonX - thumbW : leftButtonX + leftButtonW;
	if (sb.getValue() < (max - sb.getVisibleAmount())) {
	    float thumbRange = trackW - thumbW;
            if( ltr ) {
                thumbX = (int)(0.5f + (thumbRange * ((value - min) / (range - extent))));
            } else {
                thumbX = (int)(0.5f + (thumbRange * ((max - extent - value) / (range - extent))));
            }
	    thumbX +=  leftButtonX + leftButtonW;
	}

        /* If the buttons don't fit, allocate half of the available 
         * space to each and move the right one over.
         */
        int sbAvailButtonW = (sbSize.width - sbInsetsW);
        if (sbAvailButtonW < sbButtonsW) {
            rightButtonW = leftButtonW = sbAvailButtonW / 2;
            rightButtonX = sbSize.width - (sbInsets.right + rightButtonW);
        }
        
        (ltr ? decrButton : incrButton).setBounds(leftButtonX, itemY, leftButtonW, itemH);
        (ltr ? incrButton : decrButton).setBounds(rightButtonX, itemY, rightButtonW, itemH);

	/* Update the trackRect field.
	 */	
	int itrackX = leftButtonX + leftButtonW;
	int itrackW = rightButtonX - itrackX;
	trackRect.setBounds(itrackX, itemY, itrackW, itemH);

	/* Make sure the thumb fits between the buttons.  Note 
	 * that setting the thumbs bounds causes a repaint.
	 */
	if (thumbW >= (int)trackW) {
	    setThumbBounds(0, 0, 0, 0);
	}
	else {
	    if (thumbX + thumbW > rightButtonX) {
		thumbX = rightButtonX - thumbW;
	    }
	    if (thumbX  < leftButtonX + leftButtonW) {
		thumbX = leftButtonX + leftButtonW + 1;
	    }
	    setThumbBounds(thumbX, itemY, thumbW, itemH);
	}
    }
    

    public void layoutContainer(Container scrollbarContainer) 
    {
	/* If the user is dragging the value, we'll assume that the 
	 * scrollbars layout is OK modulo the thumb which is being 
	 * handled by the dragging code.
	 */
	if (isDragging) {
	    return;
	}

        JScrollBar scrollbar = (JScrollBar)scrollbarContainer;
        switch (scrollbar.getOrientation()) {
        case JScrollBar.VERTICAL:
            layoutVScrollbar(scrollbar);
            break;
            
        case JScrollBar.HORIZONTAL:
            layoutHScrollbar(scrollbar);
            break;
        }
    }


    /**
     * Set the bounds of the thumb and force a repaint that includes
     * the old thumbBounds and the new one.
     *
     * @see #getThumbBounds
     */
    protected void setThumbBounds(int x, int y, int width, int height)
    {
	/* If the thumbs bounds haven't changed, we're done.
	 */
	if ((thumbRect.x == x) && 
	    (thumbRect.y == y) && 
	    (thumbRect.width == width) && 
	    (thumbRect.height == height)) {
	    return;
	}

	/* Update thumbRect, and repaint the union of x,y,w,h and 
	 * the old thumbRect.
	 */
	int minX = Math.min(x, thumbRect.x);
	int minY = Math.min(y, thumbRect.y);
	int maxX = Math.max(x + width, thumbRect.x + thumbRect.width);
	int maxY = Math.max(y + height, thumbRect.y + thumbRect.height);

	thumbRect.setBounds(x, y, width, height);
	scrollbar.repaint(minX, minY, maxX - minX, maxY - minY);

        // PENDING: Is there a way to get the current mouse location?
        setThumbActive(false);
    }


    /**
     * Return the current size/location of the thumb.
     * <p>
     * <b>Warning </b>: the value returned by this method should not be
     * be modified, it's a reference to the actual rectangle, not a copy.
     *
     * @return The current size/location of the thumb.
     * @see #setThumbBounds
     */
    protected Rectangle getThumbBounds() {
	return thumbRect;
    }


    /**
     * Returns the current bounds of the track, i.e. the space in between
     * the increment and decrement buttons, less the insets.  The value
     * returned by this method is updated each time the scrollbar is
     * laid out (validated).
     * <p>
     * <b>Warning </b>: the value returned by this method should not be
     * be modified, it's a reference to the actual rectangle, not a copy.
     *
     * @return the current bounds of the scrollbar track
     * @see #layoutContainer
     */
    protected Rectangle getTrackBounds() {
	return trackRect;
    }

    /*
     * Method for scrolling by a block increment.
     * Added for mouse wheel scrolling support, RFE 4202656.
     */
    static void scrollByBlock(JScrollBar scrollbar, int direction) {
        // This method is called from BasicScrollPaneUI to implement wheel
        // scrolling, and also from scrollByBlock().
	    int oldValue = scrollbar.getValue();
	    int blockIncrement = scrollbar.getBlockIncrement(direction);
	    int delta = blockIncrement * ((direction > 0) ? +1 : -1);

	    scrollbar.setValue(oldValue + delta);			
    }

    protected void scrollByBlock(int direction)
    {
        scrollByBlock(scrollbar, direction);
	    trackHighlight = direction > 0 ? INCREASE_HIGHLIGHT : DECREASE_HIGHLIGHT;
	    Rectangle dirtyRect = getTrackBounds();
	    scrollbar.repaint(dirtyRect.x, dirtyRect.y, dirtyRect.width, dirtyRect.height);
    }
    
    /*
     * Method for scrolling by a unit increment.
     * Added for mouse wheel scrolling support, RFE 4202656.
     */
    static void scrollByUnits(JScrollBar scrollbar, int direction,
                                   int units) {
        // This method is called from BasicScrollPaneUI to implement wheel
        // scrolling, as well as from scrollByUnit().
        int delta = units;

        if (direction > 0) {
            delta *= scrollbar.getUnitIncrement(direction);
        }
        else {
            delta *= -scrollbar.getUnitIncrement(direction);
        }

        int oldValue = scrollbar.getValue();
        int newValue = oldValue + delta;

        // Check for overflow.
        if (delta > 0 && newValue < oldValue) {
            newValue = scrollbar.getMaximum();
        }
        else if (delta < 0 && newValue > oldValue) {
            newValue = scrollbar.getMinimum();
        }
        scrollbar.setValue(newValue);
    }

    protected void scrollByUnit(int direction)	{
        scrollByUnits(scrollbar, direction, 1);
    }

    /**
     * Indicates whether the user can absolutely position the offset with
     * a mouse click (usually the middle mouse button).
     * <p>The return value is determined from the UIManager property
     * ScrollBar.allowsAbsolutePositioning.
     */
    private boolean getSupportsAbsolutePositioning() {
	return supportsAbsolutePositioning;
    }

    private void updateThumbState(int x, int y) {
        Rectangle rect = getThumbBounds();

        setThumbActive(rect.contains(x, y));
    }

    private void setThumbActive(boolean active) {
        if (thumbActive != active) {
            thumbActive = active;
            scrollbar.repaint(getThumbBounds());
        }
    }

    public boolean isThumbActive() {
        return thumbActive;
    }

    /**
     * A listener to listen for model changes.
     *
     */
    protected class ModelListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
	    layoutContainer(scrollbar);
        }
    }

    //
    // FocusListener
    //
    public void focusGained(FocusEvent e) {
        scrollbar.repaint();
    }

    public void focusLost(FocusEvent e) {
        scrollbar.repaint();
    }

    /**
     * Track mouse drags.
     */
    protected class TrackListener
        extends MouseAdapter implements MouseMotionListener
    {
	protected transient int offset;
	protected transient int currentMouseX, currentMouseY;
 	private transient int direction = +1;
		
        public void mouseReleased(MouseEvent e)
        {
            if (isDragging) {
                updateThumbState(e.getX(), e.getY());
            }
            // PENDING: this should come from the look and feel
	    if (SwingUtilities.isRightMouseButton(e) ||
		(!getSupportsAbsolutePositioning() &&
		 SwingUtilities.isMiddleMouseButton(e)))
		return;
	    if(!scrollbar.isEnabled())
		return;

	    Rectangle r = getTrackBounds();
	    scrollbar.repaint(r.x, r.y, r.width, r.height);

	    trackHighlight = NO_HIGHLIGHT;
	    isDragging = false;
	    offset = 0;
	    scrollTimer.stop();
	    scrollbar.setValueIsAdjusting(false);
	}
		

        /**
	 * If the mouse is pressed above the "thumb" component
	 * then reduce the scrollbars value by one page ("page up"), 
	 * otherwise increase it by one page.  If there is no 
	 * thumb then page up if the mouse is in the upper half
	 * of the track.
	 */
        public void mousePressed(MouseEvent e) 
	{
	    if (SwingUtilities.isRightMouseButton(e) ||
		(!getSupportsAbsolutePositioning() &&
		 SwingUtilities.isMiddleMouseButton(e)))
		return;
	    if(!scrollbar.isEnabled())
		return;

	    if (!scrollbar.hasFocus() && scrollbar.isRequestFocusEnabled()) {
		scrollbar.requestFocus();
	    }

	    scrollbar.setValueIsAdjusting(true);

            currentMouseX = e.getX();
            currentMouseY = e.getY();
	
	    // Clicked in the Thumb area?
	    if(getThumbBounds().contains(currentMouseX, currentMouseY))	{
                switch (scrollbar.getOrientation()) {
                case JScrollBar.VERTICAL:
		    offset = currentMouseY - getThumbBounds().y;
                    break;
                case JScrollBar.HORIZONTAL:
		    offset = currentMouseX - getThumbBounds().x;
                    break;
                }
		isDragging = true;
		return;
	    }
	    else if (getSupportsAbsolutePositioning() &&
		     SwingUtilities.isMiddleMouseButton(e)) {
                switch (scrollbar.getOrientation()) {
                case JScrollBar.VERTICAL:
		    offset = getThumbBounds().height / 2;
                    break;
                case JScrollBar.HORIZONTAL:
		    offset = getThumbBounds().width / 2;
                    break;
                }
		isDragging = true;
		setValueFrom(e);
		return;
	    }
	    isDragging = false;
							
            Dimension sbSize = scrollbar.getSize();
            direction = +1;

            switch (scrollbar.getOrientation()) {
            case JScrollBar.VERTICAL:
                if (getThumbBounds().isEmpty()) {
                    int scrollbarCenter = sbSize.height / 2;
		    direction = (currentMouseY < scrollbarCenter) ? -1 : +1;
                } else {
                    int thumbY = getThumbBounds().y;
		    direction = (currentMouseY < thumbY) ? -1 : +1;
                }
                break;                    
            case JScrollBar.HORIZONTAL:
                if (getThumbBounds().isEmpty()) {
                    int scrollbarCenter = sbSize.width / 2;
                    direction = (currentMouseX < scrollbarCenter) ? -1 : +1;
                } else {
                    int thumbX = getThumbBounds().x;
                    direction = (currentMouseX < thumbX) ? -1 : +1;
                }
		if (!scrollbar.getComponentOrientation().isLeftToRight()) {
		    direction = -direction;
		}
                break;
            }
	    scrollByBlock(direction);
	    scrollTimer.stop();
	    scrollListener.setDirection(direction);
 	    scrollListener.setScrollByBlock(true);
	    startScrollTimerIfNecessary();
        }
		

        /** 
	 * Set the models value to the position of the thumb's top of Vertical
	 * scrollbar, or the left/right of Horizontal scrollbar in
	 * left-to-right/right-to-left scrollbar relative to the origin of the
	 * track.
	 */
	public void mouseDragged(MouseEvent e) {
	    setValueFrom(e);
	    if (SwingUtilities.isRightMouseButton(e) ||
		(!getSupportsAbsolutePositioning() &&
		 SwingUtilities.isMiddleMouseButton(e)))
                return;
	    if(!scrollbar.isEnabled() || getThumbBounds().isEmpty()) {
		return;
	    }
	    if (isDragging) {
		setValueFrom(e);
	    } else {
		currentMouseX = e.getX();
		currentMouseY = e.getY();
		startScrollTimerIfNecessary();
            }
        }

	private void setValueFrom(MouseEvent e) {
            boolean active = isThumbActive();
	    BoundedRangeModel model = scrollbar.getModel();
	    Rectangle thumbR = getThumbBounds();
	    float trackLength;
	    int thumbMin, thumbMax, thumbPos;

            if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
		thumbMin = decrButton.getY() + decrButton.getHeight();
		thumbMax = incrButton.getY() - thumbR.height;
		thumbPos = Math.min(thumbMax, Math.max(thumbMin, (e.getY() - offset)));
		setThumbBounds(thumbR.x, thumbPos, thumbR.width, thumbR.height);
		trackLength = getTrackBounds().height;
	    }
	    else {
		if (scrollbar.getComponentOrientation().isLeftToRight()) {
		    thumbMin = decrButton.getX() + decrButton.getWidth();
		    thumbMax = incrButton.getX() - thumbR.width;
		} else {
		    thumbMin = incrButton.getX() + incrButton.getWidth();
		    thumbMax = decrButton.getX() - thumbR.width;
		}
		thumbPos = Math.min(thumbMax, Math.max(thumbMin, (e.getX() - offset)));
		setThumbBounds(thumbPos, thumbR.y, thumbR.width, thumbR.height);
		trackLength = getTrackBounds().width;
	    }

	    /* Set the scrollbars value.  If the thumb has reached the end of
	     * the scrollbar, then just set the value to its maximum.  Otherwise
	     * compute the value as accurately as possible.
	     */
	    if (thumbPos == thumbMax) {
		if (scrollbar.getOrientation() == JScrollBar.VERTICAL || 
		    scrollbar.getComponentOrientation().isLeftToRight()) {
		    scrollbar.setValue(model.getMaximum() - model.getExtent());
		} else {
		    scrollbar.setValue(model.getMinimum());
		}
	    }
	    else {
		float valueMax = model.getMaximum() - model.getExtent();
		float valueRange = valueMax - model.getMinimum();
		float thumbValue = thumbPos - thumbMin;
		float thumbRange = thumbMax - thumbMin;
		int value;
		if (scrollbar.getOrientation() == JScrollBar.VERTICAL || 
		    scrollbar.getComponentOrientation().isLeftToRight()) {
		    value = (int)(0.5 + ((thumbValue / thumbRange) * valueRange));
		} else {
		    value = (int)(0.5 + (((thumbMax - thumbPos) / thumbRange) * valueRange));
		}
		scrollbar.setValue(value + model.getMinimum());
	    }
            setThumbActive(active);
        }
		
	private void startScrollTimerIfNecessary() {
	    if (scrollTimer.isRunning()) {
		return;
	    }
	    switch (scrollbar.getOrientation()) {
	    case JScrollBar.VERTICAL:
		if (direction >0) {
		    if (getThumbBounds().y + getThumbBounds().height <
			trackListener.currentMouseY) {
			scrollTimer.start();
		    }
		} else if (getThumbBounds().y >
			   trackListener.currentMouseY) {
		    scrollTimer.start();
		}
		break;
	    case JScrollBar.HORIZONTAL:
		if (direction >0) {
		    if (getThumbBounds().x + getThumbBounds().width <
			trackListener.currentMouseX) {
			scrollTimer.start();
		    }
		} else if (getThumbBounds().x >
			   trackListener.currentMouseX) {
		    scrollTimer.start();
		}
		break;
	    }
	}
		
	public void mouseMoved(MouseEvent e) {
            if (!isDragging) {
                updateThumbState(e.getX(), e.getY());
            }
	}

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
            if (!isDragging) {
                setThumbActive(false);
            }
        }
    }
        

    /**
     * Listener for cursor keys.
     */
    protected class ArrowButtonListener extends MouseAdapter
    {		
	// Because we are handling both mousePressed and Actions
	// we need to make sure we don't fire under both conditions.
	// (keyfocus on scrollbars causes action without mousePress
	boolean handledEvent;
	
	public void mousePressed(MouseEvent e)		{
	    if(!scrollbar.isEnabled()) { return; }
	    // not an unmodified left mouse button
	    //if(e.getModifiers() != InputEvent.BUTTON1_MASK) {return; }
	    if( ! SwingUtilities.isLeftMouseButton(e)) { return; }

	    int direction = (e.getSource() == incrButton) ? 1 : -1;
		
	    scrollByUnit(direction);
	    scrollTimer.stop();
	    scrollListener.setDirection(direction);
	    scrollListener.setScrollByBlock(false);
	    scrollTimer.start();

	    handledEvent = true;	
	    if (!scrollbar.hasFocus() && scrollbar.isRequestFocusEnabled()) {
		scrollbar.requestFocus();
	    }
	}

	public void mouseReleased(MouseEvent e)		{
	    scrollTimer.stop();
	    handledEvent = false;
	    scrollbar.setValueIsAdjusting(false);
	}
    }


    /**
     * Listener for scrolling events initiated in the
     * <code>ScrollPane</code>.
     */
    protected class ScrollListener implements ActionListener
    {
	int direction = +1;
	boolean useBlockIncrement;

	public ScrollListener()	{
	    direction = +1;
	    useBlockIncrement = false;
	}

        public ScrollListener(int dir, boolean block)	{
	    direction = dir;
	    useBlockIncrement = block;
	}
	
	public void setDirection(int direction) { this.direction = direction; }
	public void setScrollByBlock(boolean block) { this.useBlockIncrement = block; }
					
	public void actionPerformed(ActionEvent e) {
	    if(useBlockIncrement)	{
		scrollByBlock(direction);		
		// Stop scrolling if the thumb catches up with the mouse
		if(scrollbar.getOrientation() == JScrollBar.VERTICAL)	{
		    if(direction > 0)	{
			if(getThumbBounds().y + getThumbBounds().height 
				>= trackListener.currentMouseY)
				    ((Timer)e.getSource()).stop();
		    } else if(getThumbBounds().y <= trackListener.currentMouseY)	{
			((Timer)e.getSource()).stop();
		    }
		} else {
		    if(direction > 0)	{
			if(getThumbBounds().x + getThumbBounds().width 
				>= trackListener.currentMouseX)
				    ((Timer)e.getSource()).stop();
		    } else if(getThumbBounds().x <= trackListener.currentMouseX)	{
		        ((Timer)e.getSource()).stop();
		    }
	        }
	    } else {
		scrollByUnit(direction);
	    }

	    if(direction > 0 
		&& scrollbar.getValue()+scrollbar.getVisibleAmount() 
			>= scrollbar.getMaximum())
		((Timer)e.getSource()).stop();
	    else if(direction < 0 
		&& scrollbar.getValue() <= scrollbar.getMinimum())
	        ((Timer)e.getSource()).stop();
	}
    }


    class PropertyChangeHandler implements PropertyChangeListener 
    {
        public void propertyChange(PropertyChangeEvent e) {
	    String propertyName = e.getPropertyName();

            if (SynthLookAndFeel.shouldUpdateStyle(e)) {
                fetchStyle((JScrollBar)e.getSource());
            }
	    if ("model".equals(propertyName)) {
	        BoundedRangeModel oldModel = (BoundedRangeModel)e.getOldValue();
	        BoundedRangeModel newModel = (BoundedRangeModel)e.getNewValue();
		oldModel.removeChangeListener(modelListener);
		newModel.addChangeListener(modelListener);
		scrollbar.repaint();
		scrollbar.revalidate();
	    } else if ("orientation".equals(propertyName)) {
                Integer orient = (Integer)e.getNewValue();

                if (scrollbar.getComponentOrientation().isLeftToRight()) { 
                    if (incrButton instanceof SynthArrowButton) {
                        ((SynthArrowButton)incrButton).setDirection(orient.intValue() == HORIZONTAL?
                                                                EAST : SOUTH);
                    }
                    if (decrButton instanceof SynthArrowButton) {
                        ((SynthArrowButton)decrButton).setDirection(orient.intValue() == HORIZONTAL?
                                                                WEST : NORTH);
                    }
                } else {
                    if (incrButton instanceof SynthArrowButton) {
                        ((SynthArrowButton)incrButton).setDirection(orient.intValue() == HORIZONTAL?
                                                                WEST : SOUTH);
                    }
                    if (decrButton instanceof SynthArrowButton) {
                        ((SynthArrowButton)decrButton).setDirection(orient.intValue() == HORIZONTAL?
                                                                EAST : NORTH);
                    }
                }
            } else if ("componentOrientation".equals(propertyName)) {
                ComponentOrientation co = scrollbar.getComponentOrientation();
                incrButton.setComponentOrientation(co);
                decrButton.setComponentOrientation(co);

                if (scrollbar.getOrientation() == JScrollBar.HORIZONTAL) {
                    if (co.isLeftToRight()) {
                        if (incrButton instanceof SynthArrowButton) {
                            ((SynthArrowButton)incrButton).setDirection(EAST);
                        }
                        if (decrButton instanceof SynthArrowButton) {
                            ((SynthArrowButton)decrButton).setDirection(WEST);
                        }
                    } else {
                        if (incrButton instanceof SynthArrowButton) {
                            ((SynthArrowButton)incrButton).setDirection(WEST);
                        }
                        if (decrButton instanceof SynthArrowButton) {
                            ((SynthArrowButton)decrButton).setDirection(EAST);
                        }
                    }
                }

                InputMap inputMap = getInputMap(JComponent.WHEN_FOCUSED);
                SwingUtilities.replaceUIInputMap(scrollbar, JComponent.WHEN_FOCUSED, inputMap);
            }
	}
    }


    /**
     * Used for scrolling the scrollbar.
     */
    private static class SharedActionScroller extends AbstractAction {
        private int dir;
        private boolean block;

        SharedActionScroller(int dir, boolean block) {
            this.dir = dir;
            this.block = block;
        }

        public void actionPerformed(ActionEvent e) {
	    JScrollBar scrollBar = (JScrollBar)e.getSource();
	    if (dir == NEGATIVE_SCROLL || dir == POSITIVE_SCROLL) {
		int amount;
		// Don't use the BasicScrollBarUI.scrollByXXX methods as we
		// don't want to use an invokeLater to reset the trackHighlight
		// via an invokeLater
		if (block) {
		    if (dir == NEGATIVE_SCROLL) {
			amount = -1 * scrollBar.getBlockIncrement(-1);
		    }
		    else {
			amount = scrollBar.getBlockIncrement(1);
		    }
		}
		else {
		    if (dir == NEGATIVE_SCROLL) {
			amount = -1 * scrollBar.getUnitIncrement(-1);
		    }
		    else {
			amount = scrollBar.getUnitIncrement(1);
		    }
		}
		scrollBar.setValue(scrollBar.getValue() + amount);
	    }
	    else if (dir == MIN_SCROLL) {
		scrollBar.setValue(scrollBar.getMinimum());
	    }
	    else if (dir == MAX_SCROLL) {
		scrollBar.setValue(scrollBar.getMaximum());
	    }
	}
    }
}
            
            
