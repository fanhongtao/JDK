/*
 * @(#)SynthScrollBarUI.java	1.28 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.synth;

import java.awt.*;
import java.awt.event.*;

import java.beans.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import sun.swing.plaf.synth.SynthUI;


/**
 * Synth's ScrollBarUI.
 *
 * @version 1.28, 12/19/03
 * @author Scott Violet
 */
class SynthScrollBarUI extends BasicScrollBarUI implements
                                    PropertyChangeListener, SynthUI {
    private static final Insets tmpInsets = new Insets(0, 0, 0, 0);

    private SynthStyle style;
    private SynthStyle thumbStyle;
    private SynthStyle trackStyle;

    private int scrollBarWidth;


    public static ComponentUI createUI(JComponent c)    {
        return new SynthScrollBarUI();
    }

    protected void installDefaults() {
	trackHighlight = NO_HIGHLIGHT;
        if (scrollbar.getLayout() == null ||
                     (scrollbar.getLayout() instanceof UIResource)) {
            scrollbar.setLayout(this);
        }
        updateStyle(scrollbar);
    }

    protected void configureScrollBarColors() {
    }

    private void updateStyle(JScrollBar c) {
        SynthStyle oldStyle = style;
        SynthContext context = getContext(c, ENABLED);
        style = SynthLookAndFeel.updateStyle(context, this);
        if (style != oldStyle) {
            Insets insets = c.getInsets();
            scrollBarWidth = style.getInt(context,"ScrollBar.thumbHeight", 14);
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
            if (maximumThumbSize == null) {
                maximumThumbSize = new Dimension(4096, 4097);
            }
            if (oldStyle != null) {
                uninstallKeyboardActions();
                installKeyboardActions();
            }
        }
        context.dispose();

        context = getContext(c, Region.SCROLL_BAR_TRACK, ENABLED);
        trackStyle = SynthLookAndFeel.updateStyle(context, this);
        context.dispose();

        context = getContext(c, Region.SCROLL_BAR_THUMB, ENABLED);
        thumbStyle = SynthLookAndFeel.updateStyle(context, this);
        context.dispose();
    }

    protected void installListeners() {
        super.installListeners();
        scrollbar.addPropertyChangeListener(this);
    }

    protected void uninstallListeners() {
        super.uninstallListeners();
        scrollbar.removePropertyChangeListener(this);
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

        super.uninstallDefaults();
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
        if (region == Region.SCROLL_BAR_THUMB && isThumbRollover() &&
                                                 c.isEnabled()) {
            return MOUSE_OVER;
        }
        return SynthLookAndFeel.getComponentState(c);
    }

    public boolean getSupportsAbsolutePositioning() {
        SynthContext context = getContext(scrollbar);
        boolean value = style.getBoolean(context, 
                      "ScrollBar.allowsAbsolutePositioning", false);
        context.dispose();
	return value;
    }

    public void update(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        SynthLookAndFeel.update(context, g);
        context.getPainter().paintScrollBarBackground(context,
                          g, 0, 0, c.getWidth(), c.getHeight());
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

    public void paintBorder(SynthContext context, Graphics g, int x,
                            int y, int w, int h) {
        context.getPainter().paintScrollBarBorder(context, g, x, y, w, h);
    }

    protected void paintTrack(SynthContext ss, Graphics g,
                              Rectangle trackBounds) {
        SynthLookAndFeel.updateSubregion(ss, g, trackBounds);
        ss.getPainter().paintScrollBarTrackBackground(ss, g, trackBounds.x,
                        trackBounds.y, trackBounds.width, trackBounds.height);
        ss.getPainter().paintScrollBarTrackBorder(ss, g, trackBounds.x,
                        trackBounds.y, trackBounds.width, trackBounds.height);
    }

    protected void paintThumb(SynthContext ss, Graphics g,
                              Rectangle thumbBounds) {
        int orientation = scrollbar.getOrientation();
        ss.getPainter().paintScrollBarThumbBackground(ss, g, thumbBounds.x,
                        thumbBounds.y, thumbBounds.width, thumbBounds.height,
                        orientation);
        ss.getPainter().paintScrollBarThumbBorder(ss, g, thumbBounds.x,
                        thumbBounds.y, thumbBounds.width, thumbBounds.height,
                        orientation);
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

    protected void setThumbRollover(boolean active) {
        if (isThumbRollover() != active) {
            scrollbar.repaint(getThumbBounds());
            super.setThumbRollover(active);
        }
    }

    private void updateButtonDirections() {
        int orient = scrollbar.getOrientation();
        if (scrollbar.getComponentOrientation().isLeftToRight()) { 
            ((SynthArrowButton)incrButton).setDirection(
                        orient == HORIZONTAL? EAST : SOUTH);
            ((SynthArrowButton)decrButton).setDirection(
                        orient == HORIZONTAL? WEST : NORTH);
        }
        else {
            ((SynthArrowButton)incrButton).setDirection(
                        orient == HORIZONTAL? WEST : SOUTH);
            ((SynthArrowButton)decrButton).setDirection(
                        orient == HORIZONTAL ? EAST : NORTH);
        }
    }

    //
    // PropertyChangeListener
    //
    public void propertyChange(PropertyChangeEvent e) {
        String propertyName = e.getPropertyName();

        if (SynthLookAndFeel.shouldUpdateStyle(e)) {
            updateStyle((JScrollBar)e.getSource());
        }
        else if ("orientation" == propertyName) {
            updateButtonDirections();
        }
        else if ("componentOrientation" == propertyName) {
            updateButtonDirections();
	}
    }
}
