/*
 * @(#)SynthSliderUI.java    1.94 01/12/03
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import java.awt.Component;
import java.awt.Container;
import java.awt.Adjustable;
import java.awt.event.*;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.Insets;
import java.awt.Color;
import java.awt.IllegalComponentStateException;
import java.awt.Polygon;
import java.beans.*;
import java.util.Dictionary;
import java.util.Enumeration;

import javax.swing.border.AbstractBorder;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;


/**
 * A Synth L&F implementation of SliderUI.
 *
 * @version 1.15, 01/23/03 (originally from version 1.94 of BasicSliderUI)
 * @author Tom Santos
 * @author Joshua Outwater
 */
class SynthSliderUI extends SliderUI implements SynthUI {
    public static final int POSITIVE_SCROLL = +1;
    public static final int NEGATIVE_SCROLL = -1;
    public static final int MIN_SCROLL = -2;
    public static final int MAX_SCROLL = +2;


    protected Insets insetCache = null;
    protected boolean leftToRightCache = true;
    protected Dimension contentDim = null;
    protected Rectangle labelRect = null;
    protected Rectangle tickRect = null;
    protected Rectangle trackRect = null;
    protected Rectangle thumbRect = null;
    protected Rectangle valueRect = null;
    protected boolean paintValue;

    /** The distance that the track is from the side of the control. */
    protected int trackBuffer = 0;

    private static final Dimension PREFERRED_HORIZONTAL_SIZE =
        new Dimension(200, 21);
    private static final Dimension PREFERRED_VERTICAL_SIZE =
        new Dimension(21, 200);
    private static final Dimension MINIMUM_HORIZONTAL_SIZE =
        new Dimension(36, 21);
    private static final Dimension MINIMUM_VERTICAL_SIZE =
        new Dimension(21, 36);

    private transient boolean isDragging;

    protected TrackListener trackListener;
    protected ChangeListener changeListener;
    protected ComponentListener componentListener;
    protected FocusListener focusListener;
    protected ScrollListener scrollListener;
    protected PropertyChangeListener propertyChangeListener;

    // Colors
    private Color shadowColor;
    private Color highlightColor;


    private static int trackHeight;
    private static int trackBorder;
    private static int thumbWidth;
    private static int thumbHeight;

    private SynthStyle style;
    private SynthStyle sliderTrackStyle;
    private SynthStyle sliderThumbStyle;

    // PENDING (joutwate):
    // 2 pixel gap on left and right of track reserved for focus.
    // If not focusable that space goes to the track.

    /** Used to determine the color to paint the thumb. */
    private transient boolean thumbActive;

    protected Timer scrollTimer;
    protected JSlider slider;

    ///////////////////////////////////////////////////
    // ComponentUI Interface Implementation methods
    ///////////////////////////////////////////////////
    public static ComponentUI createUI(JComponent c) {
        return new SynthSliderUI();
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


    /**
     * Returns the prefix used in looking up property values.
     */
    protected String getPropertyPrefix() {
        return "Slider.";
    }

    public void installUI(JComponent c) {
        this.slider = (JSlider)c;
        slider.setEnabled(slider.isEnabled());
        slider.setOpaque(true);
        isDragging = false;
        
        installDefaults();
        installListeners();
        installKeyboardActions();

        insetCache = slider.getInsets();
        leftToRightCache = SynthLookAndFeel.isLeftToRight(slider);
        contentDim = new Dimension();
        labelRect = new Rectangle();
        tickRect = new Rectangle();
        trackRect = new Rectangle();
        thumbRect = new Rectangle();
        valueRect = new Rectangle();

        calculateGeometry();
    }   

    public void uninstallUI(JComponent c) {
    }

    protected void installDefaults() {
        fetchStyle(slider);

        SynthContext context = getContext(slider, ENABLED);
        // PENDING: change me.
        highlightColor = UIManager.getColor("Slider.highlight");
        shadowColor = UIManager.getColor("Slider.shadow");
        context.dispose();
    }

    private void fetchStyle(JSlider c) {
        SynthContext context = getContext(c, ENABLED);
        SynthStyle oldStyle = style;
        style = SynthLookAndFeel.updateStyle(context, this);

        if (style != oldStyle) {
            thumbWidth =
                style.getInt(context, getPropertyPrefix() + "thumbWidth", 30);

            thumbHeight =
                style.getInt(context, getPropertyPrefix() + "thumbHeight", 14);

            trackBorder =
                style.getInt(context, getPropertyPrefix() + "trackBorder", 1);

            trackHeight = thumbHeight + trackBorder * 2;

            paintValue = style.getBoolean(context,
                    getPropertyPrefix() + "paintValue", true);
        }
        context.dispose();

        context = getContext(c, Region.SLIDER_TRACK, ENABLED);
        sliderTrackStyle =
            SynthLookAndFeel.updateStyle(context, this);
        context.dispose();

        context = getContext(c, Region.SLIDER_THUMB, ENABLED);
        sliderThumbStyle =
            SynthLookAndFeel.updateStyle(context, this);
        context.dispose();
    }

    protected void uninstallDefaults() {
        SynthContext context = getContext(slider, ENABLED);
        style.uninstallDefaults(context);
        context.dispose();
        style = null;

        context = getContext(slider, Region.SLIDER_TRACK, ENABLED);
        sliderTrackStyle.uninstallDefaults(context);
        context.dispose();
        sliderTrackStyle = null;

        context = getContext(slider, Region.SLIDER_THUMB, ENABLED);
        sliderThumbStyle.uninstallDefaults(context);
        context.dispose();
        sliderThumbStyle = null;
    }

    protected void installListeners() {
        if ((trackListener = createTrackListener()) != null) {
            slider.addMouseListener(trackListener);
            slider.addMouseMotionListener(trackListener);
        }
        if ((focusListener = createFocusListener()) != null) {
            slider.addFocusListener(focusListener);
        }
        if ((componentListener = createComponentListener()) != null) {
            slider.addComponentListener(componentListener);
        }
        if ((propertyChangeListener = createPropertyChangeListener()) != null) {
            slider.addPropertyChangeListener(propertyChangeListener);
        }
        if ((changeListener = createChangeListener()) != null) {
            slider.getModel().addChangeListener(changeListener);
        }
        if ((scrollListener = createScrollListener()) != null) {
            scrollTimer = new Timer(100, scrollListener);
            scrollTimer.setInitialDelay(300);
        }
    }

    protected void uninstallListeners() {
        if (trackListener != null) {
            slider.removeMouseListener(trackListener);
            slider.removeMouseMotionListener(trackListener);
        }
        if (focusListener != null) {
            slider.removeFocusListener(focusListener);
        }
        if (componentListener != null) {
            slider.removeComponentListener(componentListener);
        }
        if (propertyChangeListener != null) {
            slider.removePropertyChangeListener(propertyChangeListener);
        }
        if (changeListener != null) {
            slider.getModel().removeChangeListener(changeListener);
        }
        scrollTimer = null;
    }

    protected TrackListener createTrackListener() {
        return new TrackListener();
    }

    protected ScrollListener createScrollListener() {
        return new ScrollListener();
    }

    protected ComponentListener createComponentListener() {
        return new ComponentHandler();
    }

    protected FocusListener createFocusListener() {
        return new FocusHandler();
    }

    protected PropertyChangeListener createPropertyChangeListener() {
        return new PropertyChangeHandler();
    }

    protected ChangeListener createChangeListener() {
        return new ChangeHandler();
    }

    protected void installKeyboardActions() {
        InputMap km = getInputMap(JComponent.WHEN_FOCUSED);
        SwingUtilities.replaceUIInputMap(slider, JComponent.WHEN_FOCUSED, km);
        LazyActionMap.installLazyActionMap(slider, SynthSliderUI.class,
                                           "Slider.actionMap");
    }

    InputMap getInputMap(int condition) {
        if (condition == JComponent.WHEN_FOCUSED) {
            SynthContext context = getContext(slider, ENABLED);
            InputMap keyMap = (InputMap)style.get(context,
                "Slider.focusInputMap");
            InputMap rtlKeyMap;

            if (slider.getComponentOrientation().isLeftToRight() ||
                    ((rtlKeyMap = (InputMap)style.get(context,
                        "Slider.focusInputMap.RightToLeft")) == null)) {
                context.dispose();
                return keyMap;
            } else {
                rtlKeyMap.setParent(keyMap);
                context.dispose();
                return rtlKeyMap;
            }
        }
        return null;
    }

    protected void uninstallKeyboardActions() {
        SwingUtilities.replaceUIActionMap(slider, null);
        SwingUtilities.replaceUIInputMap(slider, JComponent.WHEN_FOCUSED, null);
    }

    protected TrackListener createTrackListener(JSlider slider) {
        return new TrackListener();
    }

    private void updateThumbState(int x, int y) {
        setThumbActive(thumbRect.contains(x, y));
    }

    private void setThumbActive(boolean active) {
        if (thumbActive != active) {
            thumbActive = active;
            slider.repaint(thumbRect);
        }
    }

    public Dimension getPreferredSize(JComponent c)  {
        recalculateIfInsetsChanged();
        Dimension d = new Dimension(contentDim);
        if (slider.getOrientation() == JSlider.VERTICAL) {
            d.height = 200;
        } else {
            d.width = 200;
        }
        return d;
    }

    public Dimension getMinimumSize(JComponent c) {
        recalculateIfInsetsChanged();
        Dimension d = new Dimension(contentDim);
        if (slider.getOrientation() == JSlider.VERTICAL) {
            d.height = thumbRect.height + insetCache.top + insetCache.bottom;
        } else {
            d.width = thumbRect.width + insetCache.left + insetCache.right;
        }
        return d;
    }

    public Dimension getMaximumSize(JComponent c) {
        Dimension d = getPreferredSize(c);
        if (slider.getOrientation() == JSlider.VERTICAL) {
            d.height = Short.MAX_VALUE;
        }
        else {
            d.width = Short.MAX_VALUE;
        }
        return d;
    }

    protected void calculateGeometry() {
        layout();
        calculateThumbLocation();
    }

    protected void layout() {
        SynthContext context = getContext(slider);
        SynthGraphics synthGraphics = style.getSynthGraphics(context);

        // Set the thumb size.
        Dimension size = getThumbSize();
        thumbRect.setSize(size.width, size.height);

        // Get the insets for the track.
        Insets trackInsets = new Insets(0, 0, 0, 0);
        style.getInsets(getContext(slider, Region.SLIDER_TRACK), trackInsets);

        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            // Calculate the height of all the subcomponents so we can center
            // them.
            valueRect.height = 0;
            if (paintValue) {
                valueRect.height =
                    synthGraphics.getMaximumCharHeight(slider);
            }

            trackRect.height = trackHeight;

            tickRect.height = 0;
            if (slider.getPaintTicks()) {
                tickRect.height = getTickLength();
            }

            labelRect.height = 0;
            if (slider.getPaintLabels()) {
                labelRect.height = getHeightOfTallestLabel();
            }

            contentDim.height = valueRect.height + trackRect.height
                + trackInsets.top + trackInsets.bottom
                + tickRect.height + labelRect.height + 4;
            contentDim.width = slider.getWidth() - insetCache.left
                - insetCache.right;


            int centerY = slider.getHeight() / 2 - contentDim.height / 2;

            // Layout the components.
            valueRect.x = trackRect.x = tickRect.x = labelRect.x =
                insetCache.left;
            valueRect.width = trackRect.width =
                tickRect.width = labelRect.width = contentDim.width;

            valueRect.y = centerY;
            centerY += valueRect.height + 2;

            trackRect.y = centerY + trackInsets.top;
            centerY += trackRect.height + trackInsets.top + trackInsets.bottom;

            tickRect.y = centerY;
            centerY += tickRect.height + 2;

            labelRect.y = centerY;
            centerY += labelRect.height;
        } else {
            // Calculate the width of all the subcomponents so we can center
            // them.
            trackRect.width = trackHeight;

            tickRect.width = 0;
            if (slider.getPaintTicks()) {
                tickRect.width = getTickLength();
            }

            labelRect.width = 0;
            if (slider.getPaintLabels()) {
                labelRect.width = getWidthOfWidestLabel();
            }

            valueRect.y = insetCache.top;
            valueRect.height = 0;
            if (paintValue) {
                valueRect.height =
                    synthGraphics.getMaximumCharHeight(slider);
            }

            contentDim.width = trackRect.width + trackInsets.left
                + trackInsets.right + tickRect.width
                + labelRect.width + 2 + insetCache.left + insetCache.right;
            contentDim.height = slider.getHeight()
                - insetCache.top - insetCache.bottom;

            int startX = slider.getWidth() / 2 - contentDim.width / 2;

            // Get the max width of the min or max value of the slider.
            valueRect.width = Math.max(
                synthGraphics.computeStringWidth(context, slider.getFont(),
                    slider.getToolkit().getFontMetrics(slider.getFont()),
                    "" + slider.getMaximum()),
                synthGraphics.computeStringWidth(context, slider.getFont(),
                    slider.getToolkit().getFontMetrics(slider.getFont()),
                    "" + slider.getMinimum()));
            
            // Check to see if we need to make the width larger due to the size
            // of the value string.  The value string is centered above the
            // track.
            if (valueRect.width > (trackRect.width + trackInsets.left
                        + trackInsets.right)) {
                int diff = (valueRect.width - (trackRect.width
                            + trackInsets.left + trackInsets.right)) / 2;
                contentDim.width += diff;
                startX += diff;
            }

            // Layout the components.
            trackRect.y = tickRect.y = labelRect.y =
                valueRect.y + valueRect.height;
            trackRect.height = tickRect.height = labelRect.height =
                contentDim.height - valueRect.height;

            trackRect.x = startX + trackInsets.left;
            startX += trackRect.width + trackInsets.right + trackInsets.left;

            tickRect.x = startX;
            startX += tickRect.width + 2;

            labelRect.x = startX;
            startX += labelRect.width;
        }
    }

    protected void calculateThumbSize() {
        Dimension size = getThumbSize();
        thumbRect.setSize(size.width, size.height);
    }

    protected Color getShadowColor() {
        return shadowColor;
    }

    protected Color getHighlightColor() {
        return highlightColor;
    }

    protected void calculateThumbLocation() {
        if (slider.getSnapToTicks()) {
            int sliderValue = slider.getValue();
            int snappedValue = sliderValue; 
            int majorTickSpacing = slider.getMajorTickSpacing();
            int minorTickSpacing = slider.getMinorTickSpacing();
            int tickSpacing = 0;
        
            if (minorTickSpacing > 0) {
                tickSpacing = minorTickSpacing;
            } else if (majorTickSpacing > 0) {
                tickSpacing = majorTickSpacing;
            }

            if (tickSpacing != 0) {
                // If it's not on a tick, change the value
                if ((sliderValue - slider.getMinimum()) % tickSpacing != 0) {
                    float temp = (float)(sliderValue - slider.getMinimum())
                        / (float)tickSpacing;
                    int whichTick = Math.round( temp );
                    snappedValue =
                        slider.getMinimum() + (whichTick * tickSpacing);
                }
        
                if (snappedValue != sliderValue) { 
                    slider.setValue(snappedValue);
                }
            }
        }
    
        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            int valuePosition = xPositionForValue(slider.getValue());
            thumbRect.x = valuePosition - (thumbRect.width / 2);
            thumbRect.y = trackRect.y + trackBorder;
        } else {
            int valuePosition = yPositionForValue(slider.getValue());
            thumbRect.x = trackRect.x + trackBorder;
            thumbRect.y = valuePosition - (thumbRect.height / 2);
        }
    }

    /**
     * Gets the height of the tick area for horizontal sliders and the width
     * of the tick area for vertical sliders.  SynthSliderUI uses the returned
     * value to determine the tick area rectangle.  If you want to give your
     * ticks some room, make this larger than you need and paint your ticks
     * away from the sides in paintTicks().
     */
    protected int getTickLength() {
        return 8;
    }

    protected void calculateTickRect() {
        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            tickRect.x = trackRect.x;
            tickRect.y = trackRect.y + trackRect.height + 2 + getTickLength();
            tickRect.width = trackRect.width;
            tickRect.height = getTickLength();
        
            if (!slider.getPaintTicks()) {
                --tickRect.y;
                tickRect.height = 0;
            }
        } else {
            if (SynthLookAndFeel.isLeftToRight(slider)) {
                tickRect.x = trackRect.x + trackRect.width;
                tickRect.width = getTickLength();
            } else {
                tickRect.width = getTickLength();
                tickRect.x = trackRect.x - tickRect.width;
            }
            tickRect.y = trackRect.y;
            tickRect.height = trackRect.height;

            if (!slider.getPaintTicks()) {
                --tickRect.x;
                tickRect.width = 0;
            }
        }
    }

    private static Rectangle unionRect = new Rectangle();

    public void setThumbLocation(int x, int y) {
        unionRect.setBounds(thumbRect);
        thumbRect.setLocation(x, y);
        SwingUtilities.computeUnion(thumbRect.x, thumbRect.y,
            thumbRect.width, thumbRect.height, unionRect); 
        slider.repaint(unionRect.x, unionRect.y,
            unionRect.width, unionRect.height);
        // Value rect is tied to the thumb location.  We need to repaint when
        // the thumb repaints.
        // PENDING (joutwate): look into optimizing the width that we repaint.
        slider.repaint(valueRect.x, valueRect.y,
                valueRect.width, valueRect.height);
        setThumbActive(false);
    }

    public void scrollByBlock(int direction) {
        synchronized(slider) {
            int oldValue = slider.getValue();
            int blockIncrement =
                (slider.getMaximum() - slider.getMinimum()) / 10;
            if (blockIncrement <= 0 &&
                    slider.getMaximum() > slider.getMinimum()) {
                blockIncrement = 1;
            }

            int delta = blockIncrement * ((direction > 0) ?
                POSITIVE_SCROLL : NEGATIVE_SCROLL);
            slider.setValue(oldValue + delta);          
        }
    }

    public void scrollByUnit(int direction) {
        synchronized(slider) {
            int oldValue = slider.getValue();
            int delta = 1 * ((direction > 0) ?
                POSITIVE_SCROLL : NEGATIVE_SCROLL);
            slider.setValue(oldValue + delta);  
        }       
    }

    /**
     * This function is called when a mousePressed was detected in the track,
     * not in the thumb.  The default behavior is to scroll by block.  You can
     * override this method to stop it from scrolling or to add additional
     * behavior.
     */
    protected void scrollDueToClickInTrack(int dir) {
        scrollByBlock(dir);
    }

    protected int xPositionForValue(int value) {
        int min = slider.getMinimum();
        int max = slider.getMaximum();
        int trackLeft = trackRect.x + thumbRect.width / 2 + trackBorder;
        int trackRight = trackRect.x + trackRect.width - thumbRect.width / 2
            - trackBorder;
        int trackLength = trackRight - trackLeft;
        double valueRange = (double)max - (double)min;
        double pixelsPerValue = (double)trackLength / valueRange;
        int xPosition;

        if (!drawInverted()) {
            xPosition = trackLeft;
            xPosition += Math.round( pixelsPerValue * ((double)value - min));
        } else {
            xPosition = trackRight;
            xPosition -= Math.round( pixelsPerValue * ((double)value - min));
        }

        xPosition = Math.max(trackLeft, xPosition);
        xPosition = Math.min(trackRight, xPosition);

        return xPosition;
    }

    protected int yPositionForValue(int value) {
        int min = slider.getMinimum();
        int max = slider.getMaximum();
        int trackTop = trackRect.y + thumbRect.height / 2 + trackBorder;
        int trackBottom = trackRect.y + trackRect.height
            - thumbRect.height / 2 - trackBorder;
        int trackLength = trackBottom - trackTop;
        double valueRange = (double)max - (double)min;
        double pixelsPerValue = (double)trackLength / (double)valueRange;
        int yPosition;

        if (!drawInverted()) {
            yPosition = trackTop;
            yPosition += Math.round(pixelsPerValue * ((double)max - value));
        } else {
            yPosition = trackTop;
            yPosition += Math.round(pixelsPerValue * ((double)value - min));
        }

        yPosition = Math.max(trackTop, yPosition);
        yPosition = Math.min(trackBottom, yPosition);

        return yPosition;
    }

    /**
     * Returns a value give a y position.  If yPos is past the track at the
     * top or the bottom it will set the value to the min or max of the
     * slider, depending if the slider is inverted or not.
     */
    public int valueForYPosition(int yPos) {
        int value;
        int minValue = slider.getMinimum();
        int maxValue = slider.getMaximum();
        int trackTop = trackRect.y + thumbRect.height / 2 + trackBorder;
        int trackBottom = trackRect.y + trackRect.height
            - thumbRect.height / 2 - trackBorder;
        int trackLength = trackBottom - trackTop;
        
        if (yPos <= trackTop) {
            value = drawInverted() ? minValue : maxValue;
        } else if (yPos >= trackBottom) {
            value = drawInverted() ? maxValue : minValue;
        } else {
            int distanceFromTrackTop = yPos - trackTop;
            double valueRange = (double)maxValue - (double)minValue;
            double valuePerPixel = valueRange / (double)trackLength;
            int valueFromTrackTop =
                (int)Math.round(distanceFromTrackTop * valuePerPixel);
            value = drawInverted() ?
                minValue + valueFromTrackTop : maxValue - valueFromTrackTop;
        }
        return value;
    }
  
    /**
     * Returns a value give an x position.  If xPos is past the track at the
     * left or the right it will set the value to the min or max of the
     * slider, depending if the slider is inverted or not.
     */
    public int valueForXPosition(int xPos) {
        int value;
        int minValue = slider.getMinimum();
        int maxValue = slider.getMaximum();
        int trackLeft = trackRect.x + thumbRect.width / 2 + trackBorder;
        int trackRight = trackRect.x + trackRect.width
            - thumbRect.width / 2 - trackBorder;
        int trackLength = trackRight - trackLeft;
        
        if (xPos <= trackLeft) {
            value = drawInverted() ? maxValue : minValue;
        } else if (xPos >= trackRight) {
            value = drawInverted() ? minValue : maxValue;
        } else {
            int distanceFromTrackLeft = xPos - trackLeft;
            double valueRange = (double)maxValue - (double)minValue;
            double valuePerPixel = valueRange / (double)trackLength;
            int valueFromTrackLeft =
                (int)Math.round(distanceFromTrackLeft * valuePerPixel);
            value = drawInverted() ?
                maxValue - valueFromTrackLeft : minValue + valueFromTrackLeft;
        }
        return value;
    }

    protected Dimension getThumbSize() {
        Dimension size = new Dimension();

        if (slider.getOrientation() == JSlider.VERTICAL) {
            size.width = thumbHeight;
            size.height = thumbWidth;
        } else {
            size.width = thumbWidth;
            size.height = thumbHeight;
        }
        return size;
    }

    protected int getWidthOfWidestLabel() {
        Dictionary dictionary = slider.getLabelTable();
        int widest = 0;
        if (dictionary != null) {
            Enumeration keys = dictionary.keys();
            while (keys.hasMoreElements()) {
                Component label = (Component)dictionary.get(keys.nextElement());
                widest = Math.max(label.getPreferredSize().width, widest);
            }
        }
        return widest;
    }

    protected int getHeightOfTallestLabel() {
        Dictionary dictionary = slider.getLabelTable();
        int tallest = 0;
        if (dictionary != null) {
            Enumeration keys = dictionary.keys();
            while (keys.hasMoreElements()) {
                Component label = (Component)dictionary.get(keys.nextElement());
                tallest = Math.max(label.getPreferredSize().height, tallest);
            }
        }
        return tallest;
    }

    protected int getWidthOfHighValueLabel() {
        Component label = getHighestValueLabel();
        int width = 0;

        if (label != null) {
            width = label.getPreferredSize().width;
        }
        return width;
    }

    protected int getWidthOfLowValueLabel() {
        Component label = getLowestValueLabel();
        int width = 0;

        if (label != null) {
            width = label.getPreferredSize().width;
        }
        return width;
    }

    protected int getHeightOfHighValueLabel() {
        Component label = getHighestValueLabel();
        int height = 0;

        if (label != null) {
            height = label.getPreferredSize().height;
        }
        return height;
    }

    protected int getHeightOfLowValueLabel() {
        Component label = getLowestValueLabel();
        int height = 0;

        if (label != null) {
            height = label.getPreferredSize().height;
        }
        return height;
    }

    protected boolean drawInverted() {
        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            if (SynthLookAndFeel.isLeftToRight(slider)) {
                return slider.getInverted();
            } else {
                return !slider.getInverted();
            }
        } else {
            return slider.getInverted();
        }
    }

    /**
     * Returns the label that corresponds to the highest slider value in the
     * label table.
     * @see JSlider#setLabelTable
     */
    protected Component getLowestValueLabel() {
        Dictionary dictionary = slider.getLabelTable();
        Component label = null;

        if (dictionary != null) {
            Enumeration keys = dictionary.keys();
            if (keys.hasMoreElements()) {
                int lowestValue = ((Integer)keys.nextElement()).intValue();

                while (keys.hasMoreElements()) {
                    int value = ((Integer)keys.nextElement()).intValue();
                    lowestValue = Math.min(value, lowestValue);
                }
                label = (Component)dictionary.get(new Integer(lowestValue));
            }
        }
        return label;
    }

    /**
     * Returns the label that corresponds to the lowest slider value in the
     * label table.
     * @see JSlider#setLabelTable
     */
    protected Component getHighestValueLabel() {
        Dictionary dictionary = slider.getLabelTable();
        Component label = null;

        if (dictionary != null) {
            Enumeration keys = dictionary.keys();
            if (keys.hasMoreElements()) {
                int highestValue = ((Integer)keys.nextElement()).intValue();

                while (keys.hasMoreElements()) {
                    int value = ((Integer)keys.nextElement()).intValue();
                    highestValue = Math.max(value, highestValue);
                }
                label = (Component)dictionary.get(new Integer(highestValue));
            }
        }
        return label;
    }

    protected void recalculateIfInsetsChanged() {
        Insets newInsets = style.getInsets(getContext(slider), null);
        if (!newInsets.equals(insetCache)) {
            insetCache = newInsets;
            calculateGeometry();
        }
    }

    protected void recalculateIfOrientationChanged() {
        boolean ltr = SynthLookAndFeel.isLeftToRight(slider);
        if (ltr != leftToRightCache) {
            leftToRightCache = ltr;
            calculateGeometry();
        }
    }

    public Region getRegion(JComponent c) {
        return SynthLookAndFeel.getRegion(c);
    }

    public SynthContext getContext(JComponent c) {
        return getContext(c, getComponentState(c));
    }

    public SynthContext getContext(JComponent c, int state) {
        return SynthContext.getContext(SynthContext.class, c,
                            SynthLookAndFeel.getRegion(c), style, state);
    }

    public SynthContext getContext(JComponent c, Region subregion) {
        return getContext(c, subregion, getComponentState(c, subregion));
    }

    private SynthContext getContext(JComponent c, Region subregion, int state) {
        SynthStyle style = null;
        Class klass = SynthContext.class;

        if (subregion == Region.SLIDER_TRACK) {
            style = sliderTrackStyle;
        } else if (subregion == Region.SLIDER_THUMB) {
            style = sliderThumbStyle;
        }
        return SynthContext.getContext(klass, c, subregion, style, state);
    }

    public int getComponentState(JComponent c) {
        return SynthLookAndFeel.getComponentState(c);
    }

    private int getComponentState(JComponent c, Region region) {
        if (region == Region.SLIDER_THUMB && thumbActive &&c.isEnabled()) {
            return MOUSE_OVER;
        }
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

    public void paint(SynthContext context, Graphics g) {
        recalculateIfInsetsChanged();
        recalculateIfOrientationChanged();
        Rectangle clip = g.getClipBounds();

        valueRect.x = (thumbRect.x + (thumbRect.width / 2)) -
            g.getFontMetrics().stringWidth("" + slider.getValue()) / 2;
        context.getStyle().getSynthGraphics(context).paintText(
                context, g, "" + slider.getValue(), valueRect.x,
                valueRect.y, -1);
        
        SynthContext subcontext = getContext(slider, Region.SLIDER_TRACK);
        paintTrack(subcontext, g, trackRect);
        subcontext.dispose();

        subcontext = getContext(slider, Region.SLIDER_THUMB);
        paintThumb(subcontext, g, thumbRect);
        subcontext.dispose();

        if (slider.getPaintTicks() && clip.intersects(tickRect)) {
            paintTicks(g);
        }

        if (slider.getPaintLabels() && clip.intersects(labelRect)) {
            paintLabels(g);
        }
    }

    public void paintThumb(SynthContext context, Graphics g,
            Rectangle thumbBounds)  {        
        SynthLookAndFeel.updateSubregion(context, g, thumbBounds);
    }

    public void paintTrack(SynthContext context, Graphics g,
            Rectangle trackBounds) {
        SynthLookAndFeel.updateSubregion(context, g, trackBounds);
    }

    public void paintLabels(Graphics g) {
        Rectangle labelBounds = labelRect;

        Dictionary dictionary = slider.getLabelTable();
        if (dictionary != null) {
            Enumeration keys = dictionary.keys();
            int minValue = slider.getMinimum();
            int maxValue = slider.getMaximum();
            while (keys.hasMoreElements()) {
                Integer key = (Integer)keys.nextElement();
                int value = key.intValue();
                if (value >= minValue && value <= maxValue) {
                    Component label = (Component)dictionary.get(key);
                    if (slider.getOrientation() == JSlider.HORIZONTAL) {
                        g.translate( 0, labelBounds.y );
                        paintHorizontalLabel( g, value, label );
                        g.translate( 0, -labelBounds.y );
                    }
                    else {
                        int offset = 0;
                        if (!SynthLookAndFeel.isLeftToRight(slider)) {
                            offset = labelBounds.width -
                                label.getPreferredSize().width;
                        }
                        g.translate(labelBounds.x + offset, 0);
                        paintVerticalLabel(g, value, label);
                        g.translate(-labelBounds.x - offset, 0);
                    }
                }
            }
        }
    }

    /**
     * Called for every label in the label table.  Used to draw the labels
     * for horizontal sliders.  The graphics have been translated to
     * labelRect.y already.
     * @see JSlider#setLabelTable
     */
    protected void paintHorizontalLabel(Graphics g, int value,
            Component label) {
        int labelCenter = xPositionForValue(value);
        int labelLeft = labelCenter - (label.getPreferredSize().width / 2);
        g.translate(labelLeft, 0);
        label.paint(g);
        g.translate(-labelLeft, 0);
    }

    /**
     * Called for every label in the label table.  Used to draw the labels
     * for vertical sliders.  The graphics have been translated to
     * labelRect.x already.
     * @see JSlider#setLabelTable
     */
    protected void paintVerticalLabel(Graphics g, int value,
            Component label) {
        int labelCenter = yPositionForValue(value);
        int labelTop = labelCenter - (label.getPreferredSize().height / 2);
        g.translate(0, labelTop);
        label.paint(g);
        g.translate(0, -labelTop);
    }

    public void paintTicks(Graphics g)  {        
        Rectangle tickBounds = tickRect;
        int i;
        int maj, min, max;
        int w = tickBounds.width;
        int h = tickBounds.height;
        int centerEffect, tickHeight;

        g.setColor(slider.getBackground());
        g.fillRect(tickBounds.x, tickBounds.y,
            tickBounds.width, tickBounds.height);  
        g.setColor(Color.black);

        maj = slider.getMajorTickSpacing();
        min = slider.getMinorTickSpacing();

        if (slider.getOrientation() == JSlider.HORIZONTAL) {
           g.translate(0, tickBounds.y);

            int value = slider.getMinimum();
            int xPos = 0;

            if (slider.getMinorTickSpacing() > 0) {
                while (value <= slider.getMaximum()) {
                    xPos = xPositionForValue(value);
                    paintMinorTickForHorizSlider(g, tickBounds, xPos);
                    value += slider.getMinorTickSpacing();
                }
            }

            if (slider.getMajorTickSpacing() > 0) {
                value = slider.getMinimum();

                while (value <= slider.getMaximum()) {
                    xPos = xPositionForValue(value);
                    paintMajorTickForHorizSlider(g, tickBounds, xPos);
                    value += slider.getMajorTickSpacing();
                }
            }

            g.translate(0, -tickBounds.y);
        }
        else {
           g.translate(tickBounds.x, 0);

            int value = slider.getMinimum();
            int yPos = 0;

            if (slider.getMinorTickSpacing() > 0) {
	        int offset = 0;
	        if (!SynthLookAndFeel.isLeftToRight(slider)) {
		    offset = tickBounds.width - tickBounds.width / 2;
		    g.translate(offset, 0);
		}

                while (value <= slider.getMaximum()) {
                    yPos = yPositionForValue(value);
                    paintMinorTickForVertSlider(g, tickBounds, yPos);
                    value += slider.getMinorTickSpacing();
                }

		if (!SynthLookAndFeel.isLeftToRight(slider)) {
		    g.translate(-offset, 0);
		}
            }

            if (slider.getMajorTickSpacing() > 0) {
                value = slider.getMinimum();
	        if (!SynthLookAndFeel.isLeftToRight(slider)) {
		    g.translate(2, 0);
		}

                while (value <= slider.getMaximum()) {
                    yPos = yPositionForValue(value);
                    paintMajorTickForVertSlider(g, tickBounds, yPos);
                    value += slider.getMajorTickSpacing();
                }

	        if (!SynthLookAndFeel.isLeftToRight(slider)) {
		    g.translate(-2, 0);
		}
            }
            g.translate(-tickBounds.x, 0);
        }
    }

    protected void paintMinorTickForHorizSlider(Graphics g,
            Rectangle tickBounds, int x) {
        g.drawLine(x, 0, x, tickBounds.height / 2 - 1);
    }

    protected void paintMajorTickForHorizSlider(Graphics g,
            Rectangle tickBounds, int x) {
        g.drawLine(x, 0, x, tickBounds.height - 2);
    }

    protected void paintMinorTickForVertSlider(Graphics g,
            Rectangle tickBounds, int y) {
        g.drawLine(0, y, tickBounds.width / 2 - 1, y);
    }

    protected void paintMajorTickForVertSlider(Graphics g,
            Rectangle tickBounds, int y) {
        g.drawLine(0, y,  tickBounds.width - 2, y);
    }

    class PropertyChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String propertyName = e.getPropertyName();
            if (propertyName.equals("orientation") ||
                    propertyName.equals("inverted") ||
                    propertyName.equals("labelTable") ||
                    propertyName.equals("majorTickSpacing") ||
                    propertyName.equals("minorTickSpacing") ||
                    propertyName.equals("paintTicks") ||
                    propertyName.equals("paintTrack") ||
                    propertyName.equals("paintLabels")) {
                calculateGeometry();
                slider.repaint();
            } else if (propertyName.equals("componentOrientation")) {
                calculateGeometry();
                slider.repaint();

                InputMap km = getInputMap(JComponent.WHEN_FOCUSED);
                SwingUtilities.replaceUIInputMap(slider,
                    JComponent.WHEN_FOCUSED, km);
            } else if (propertyName.equals("model")) {
                ((BoundedRangeModel)e.getOldValue()).removeChangeListener(
                    changeListener);
                ((BoundedRangeModel)e.getNewValue()).addChangeListener(
                    changeListener);
                calculateThumbLocation();
                slider.repaint();
            }
            if (SynthLookAndFeel.shouldUpdateStyle(e)) {
                fetchStyle((JSlider)e.getSource());
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////
    /// Model Listener Class
    /////////////////////////////////////////////////////////////////////////        
    /**
     * Data model listener.
     *
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */
    class ChangeHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            if (!isDragging) {
                calculateThumbLocation();
                slider.repaint();
            }
        }
    }

    //////////////////////////////////////////////////
    /// Track Listener Class
    //////////////////////////////////////////////////
    /**
     * Track mouse movements.
     */
    protected class TrackListener extends MouseInputAdapter {
        protected transient int offset;
        protected transient int currentMouseX, currentMouseY;

        public void mouseDragged(MouseEvent e) {
            SynthScrollBarUI ui;
            int thumbMiddle = 0;

            if (!slider.isEnabled()) {
                return;
            }

            currentMouseX = e.getX();
            currentMouseY = e.getY();

            if (!isDragging) {
                return;
            }

            slider.setValueIsAdjusting(true);

            switch (slider.getOrientation()) {
            case JSlider.VERTICAL:      
                int halfThumbHeight = thumbRect.height / 2;
                int thumbTop = e.getY() - offset;
                int trackTop = trackRect.y;
                int trackBottom = trackRect.y + trackRect.height
                    - halfThumbHeight - trackBorder;
                int vMax = yPositionForValue(slider.getMaximum() -
                    slider.getExtent());

                if (drawInverted()) {
                    trackBottom = vMax;
                } else {
                    trackTop = vMax;
                }
                thumbTop = Math.max(thumbTop, trackTop - halfThumbHeight);
                thumbTop = Math.min(thumbTop, trackBottom - halfThumbHeight);

                setThumbLocation(thumbRect.x, thumbTop);

                thumbMiddle = thumbTop + halfThumbHeight;
                slider.setValue(valueForYPosition(thumbMiddle));
                break;
            case JSlider.HORIZONTAL:
                int halfThumbWidth = thumbRect.width / 2;
                int thumbLeft = e.getX() - offset;
                int trackLeft = trackRect.x + halfThumbWidth + trackBorder;
                int trackRight = trackRect.x + trackRect.width
                    - halfThumbWidth - trackBorder;
                int hMax = xPositionForValue(slider.getMaximum() -
                    slider.getExtent());

                if (drawInverted()) {
                    trackLeft = hMax;
                } else {
                    trackRight = hMax;
                }
                thumbLeft = Math.max(thumbLeft, trackLeft - halfThumbWidth);
                thumbLeft = Math.min(thumbLeft, trackRight - halfThumbWidth);

                setThumbLocation(thumbLeft, thumbRect.y);

                thumbMiddle = thumbLeft + halfThumbWidth;
                slider.setValue(valueForXPosition(thumbMiddle));
                break;
            default:
                return;
            }

            if (slider.getValueIsAdjusting()) {
                setThumbActive(true);
            }
        }

        public void mouseReleased(MouseEvent e) {
            if (!slider.isEnabled()) {
                return;
            }

            offset = 0;
            scrollTimer.stop();

            // This is the way we have to determine snap-to-ticks.  It's
            // hard to explain but since ChangeEvents don't give us any idea
            // what has changed we don't have a way to stop the thumb bounds
            // from being recalculated.  Recalculating the thumb bounds moves
            // the thumb over the current value (i.e., snapping to the ticks).
            if ( slider.getSnapToTicks() /*|| slider.getSnapToValue()*/ ) {
                isDragging = false;
                slider.setValueIsAdjusting(false);
            }
            else {
                slider.setValueIsAdjusting(false);
                isDragging = false;
            }
        
            updateThumbState(e.getX(), e.getY());
            slider.repaint();
        }

        public void mouseMoved(MouseEvent e) {
            updateThumbState(e.getX(), e.getY());
        }

        /**
        * If the mouse is pressed above the "thumb" component
        * then reduce the scrollbars value by one page ("page up"), 
        * otherwise increase it by one page.  If there is no 
        * thumb then page up if the mouse is in the upper half
        * of the track.
        */
        public void mousePressed(MouseEvent e)                {
            if (!slider.isEnabled()) {
                return;
            }

            currentMouseX = e.getX();
            currentMouseY = e.getY();

            if (slider.isRequestFocusEnabled()) {
                slider.requestFocus();
            }

            // Clicked in the Thumb area?
            if (thumbRect.contains(currentMouseX, currentMouseY)) {
                switch (slider.getOrientation()) {
                case JSlider.VERTICAL:
                    offset = currentMouseY - thumbRect.y;
                    break;
                case JSlider.HORIZONTAL:
                    offset = currentMouseX - thumbRect.x;
                    break;
                }
                isDragging = true;
                return;
            }
            isDragging = false;
            slider.setValueIsAdjusting(true);

            Dimension sbSize = slider.getSize();
            int direction = POSITIVE_SCROLL;

            switch (slider.getOrientation()) {
            case JSlider.VERTICAL:
                if (thumbRect.isEmpty()) {
                    int scrollbarCenter = sbSize.height / 2;
                    if (!drawInverted()) {
                        direction = (currentMouseY < scrollbarCenter) ?
                            POSITIVE_SCROLL : NEGATIVE_SCROLL;
                    }
                    else {
                        direction = (currentMouseY < scrollbarCenter) ?
                            NEGATIVE_SCROLL : POSITIVE_SCROLL;
                    }
                }
                else {
                    int thumbY = thumbRect.y;
                    if (!drawInverted()) {
                        direction = (currentMouseY < thumbY) ?
                            POSITIVE_SCROLL : NEGATIVE_SCROLL;
                    }
                    else {
                        direction = (currentMouseY < thumbY) ?
                            NEGATIVE_SCROLL : POSITIVE_SCROLL;
                    }
                }
                break;                    
            case JSlider.HORIZONTAL:
                if (thumbRect.isEmpty()) {
                    int scrollbarCenter = sbSize.width / 2;
                    if (!drawInverted()) {
                        direction = (currentMouseX < scrollbarCenter) ?
                            NEGATIVE_SCROLL : POSITIVE_SCROLL;
                    }
                    else {
                        direction = (currentMouseX < scrollbarCenter) ?
                            POSITIVE_SCROLL : NEGATIVE_SCROLL;
                    }
                } else {
                    int thumbX = thumbRect.x;
                    if (!drawInverted()) {
                        direction = (currentMouseX < thumbX) ?
                            NEGATIVE_SCROLL : POSITIVE_SCROLL;
                    }
                    else {
                        direction = (currentMouseX < thumbX) ?
                            POSITIVE_SCROLL : NEGATIVE_SCROLL;
                    }
                }
                break;
            }
            scrollDueToClickInTrack(direction);
            Rectangle r = thumbRect;
            if (!r.contains(currentMouseX, currentMouseY)) {
                if (shouldScroll(direction)) {
                    scrollTimer.stop();
                    scrollListener.setDirection(direction);
                    scrollTimer.start();
                }
            }
        }

        public boolean shouldScroll(int direction) {
            Rectangle r = thumbRect;
            if (slider.getOrientation() == JSlider.VERTICAL) {
                if (drawInverted() ? direction < 0 : direction > 0) {
                    if (r.y + r.height  <= currentMouseY) {
                        return false;
                    }
                } else if (r.y >= currentMouseY) {
                    return false;
                }
            } else {
                if (drawInverted() ? direction < 0 : direction > 0) {
                    if (r.x + r.width  >= currentMouseX) {
                        return false;
                    }
                } else if (r.x <= currentMouseX) {
                    return false;
                }
            }

            if (direction >0 && slider.getValue()
                    + slider.getExtent() >= slider.getMaximum()) {
                return false;
            } else if (direction < 0
                    && slider.getValue() <= slider.getMinimum()) {
                return false;
            }
            return true;
        }
    }

    /**
     * Scroll-event listener.
     *
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */
    class ScrollListener implements ActionListener {
        // changed this class to public to avoid bogus IllegalAccessException
        // bug i InternetExplorer browser.  It was protected.  Work around
        // for 4109432.
        int direction = POSITIVE_SCROLL;
        boolean useBlockIncrement;

        public ScrollListener() {
            direction = POSITIVE_SCROLL;
            useBlockIncrement = true;
        }

        public ScrollListener(int dir, boolean block)   {
            direction = dir;
            useBlockIncrement = block;
        }

        public void setDirection(int direction) { this.direction = direction; }

        public void setScrollByBlock(boolean block) {
            this.useBlockIncrement = block;
        }

        public void actionPerformed(ActionEvent e) {
            if (useBlockIncrement) {
                scrollByBlock(direction);
            } else {
                scrollByUnit(direction);
            }
            if (!trackListener.shouldScroll(direction)) {
                ((Timer)e.getSource()).stop();
            }
        }
    }

    /**
     * Listener for resizing events.
     * <p>
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */
    class ComponentHandler extends ComponentAdapter {
        public void componentResized(ComponentEvent e)  {
            calculateGeometry();
            slider.repaint();
        }
    }

    /**
     * Focus-change listener.
     * <p>
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */
    class FocusHandler implements FocusListener {
        public void focusGained(FocusEvent e) { slider.repaint();} 
        public void focusLost(FocusEvent e) { slider.repaint();}
    }

    static class SharedActionScroller extends AbstractAction {
        int dir;
        boolean block;

        public SharedActionScroller(int dir, boolean block) {
            this.dir = dir;
            this.block = block;
        }

        public void actionPerformed(ActionEvent e) {
            JSlider slider = (JSlider)e.getSource();
            if (dir == NEGATIVE_SCROLL || dir == POSITIVE_SCROLL) {
                int realDir = dir;
                SynthSliderUI ui = (SynthSliderUI)slider.getUI();
                if (slider.getInverted()) {
                    realDir = dir == NEGATIVE_SCROLL ?
                        POSITIVE_SCROLL : NEGATIVE_SCROLL;
                }
                
                if (block) {
                    ui.scrollByBlock(realDir);
                } else {
                    ui.scrollByUnit(realDir);
                }
            } else {
                if (slider.getInverted()) {
                    if (dir == MIN_SCROLL) {
                        slider.setValue(slider.getMaximum());
                    } else if (dir == MAX_SCROLL) {
                        slider.setValue(slider.getMinimum());
                    }
                } else {
                    if (dir == MIN_SCROLL) {
                        slider.setValue(slider.getMinimum());
                    } else if (dir == MAX_SCROLL) {
                        slider.setValue(slider.getMaximum());
                    }
                }       
            }
        }
    }
}
