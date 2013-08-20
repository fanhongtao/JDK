/*
 * @(#)SynthSliderUI.java    1.94 01/12/03
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.synth;

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
import javax.swing.plaf.basic.BasicSliderUI;
import sun.swing.plaf.synth.SynthUI;
import com.sun.java.swing.SwingUtilities2;


/**
 * Synth's SliderUI.
 *
 * @version 1.21, 12/19/03
 * @author Joshua Outwater
 */
class SynthSliderUI extends BasicSliderUI implements PropertyChangeListener,
        SynthUI {
    protected Dimension contentDim = new Dimension();
    protected Rectangle valueRect = new Rectangle();
    protected boolean paintValue;

    private int trackHeight;
    private int trackBorder;
    private int thumbWidth;
    private int thumbHeight;

    private SynthStyle style;
    private SynthStyle sliderTrackStyle;
    private SynthStyle sliderThumbStyle;

    /** Used to determine the color to paint the thumb. */
    private transient boolean thumbActive;

    ///////////////////////////////////////////////////
    // ComponentUI Interface Implementation methods
    ///////////////////////////////////////////////////
    public static ComponentUI createUI(JComponent c) {
        return new SynthSliderUI((JSlider)c);
    }

    public SynthSliderUI(JSlider c) {
        super(c);
    }

    protected void installDefaults(JSlider slider) {
        updateStyle(slider);
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

    protected void installListeners(JSlider slider) {
        super.installListeners(slider);
        slider.addPropertyChangeListener(this);
    }

    protected void uninstallListeners() {
        slider.removePropertyChangeListener(this);
        super.uninstallListeners(slider);
    }

    private void updateStyle(JSlider c) {
        SynthContext context = getContext(c, ENABLED);
        SynthStyle oldStyle = style;
        style = SynthLookAndFeel.updateStyle(context, this);

        if (style != oldStyle) {
            thumbWidth =
                style.getInt(context, "Slider.thumbWidth", 30);

            thumbHeight =
                style.getInt(context, "Slider.thumbHeight", 14);

            trackBorder =
                style.getInt(context, "Slider.trackBorder", 1);

            trackHeight = thumbHeight + trackBorder * 2;

            paintValue = style.getBoolean(context,
                    "Slider.paintValue", true);
            if (oldStyle != null) {
                uninstallKeyboardActions(c);
                installKeyboardActions(c);
            }
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

    protected TrackListener createTrackListener(JSlider s) {
        return new SynthTrackListener();
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

    protected void calculateGeometry() {
        layout();
        calculateThumbLocation();
    }

    protected void layout() {
        SynthContext context = getContext(slider);
        SynthGraphicsUtils synthGraphics = style.getGraphicsUtils(context);

        // Set the thumb size.
        Dimension size = getThumbSize();
        thumbRect.setSize(size.width, size.height);

        // Get the insets for the track.
        Insets trackInsets = new Insets(0, 0, 0, 0);
        SynthContext trackContext = getContext(slider, Region.SLIDER_TRACK);
        style.getInsets(trackContext, trackInsets);
        trackContext.dispose();

        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            // Calculate the height of all the subcomponents so we can center
            // them.
            valueRect.height = 0;
            if (paintValue) {
                valueRect.height =
                    synthGraphics.getMaximumCharHeight(context);
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
                    synthGraphics.getMaximumCharHeight(context);
            }

            contentDim.width = trackRect.width + trackInsets.left
                + trackInsets.right + tickRect.width
                + labelRect.width + 2 + insetCache.left + insetCache.right;
            contentDim.height = slider.getHeight()
                - insetCache.top - insetCache.bottom;

            int startX = slider.getWidth() / 2 - contentDim.width / 2;

            // Get the max width of the min or max value of the slider.
            FontMetrics fm = slider.getFontMetrics(slider.getFont());
            valueRect.width = Math.max(
                synthGraphics.computeStringWidth(context, slider.getFont(),
                    fm, "" + slider.getMaximum()),
                synthGraphics.computeStringWidth(context, slider.getFont(),
                    fm, "" + slider.getMinimum()));
            
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

            if (SynthLookAndFeel.isLeftToRight(slider)) {
                trackRect.x = startX + trackInsets.left;
                startX += trackRect.width + trackInsets.right +
                    trackInsets.left;

                tickRect.x = startX;
                startX += tickRect.width + 2;

                labelRect.x = startX;
            } else {
                labelRect.x = startX;
                startX += labelRect.width + 2;

                tickRect.x = startX;
                startX += tickRect.width + trackInsets.left;

                trackRect.x = startX;
            }
        }
        context.dispose();
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
        super.setThumbLocation(x, y);
        // Value rect is tied to the thumb location.  We need to repaint when
        // the thumb repaints.
        slider.repaint(valueRect.x, valueRect.y,
                valueRect.width, valueRect.height);
        setThumbActive(false);
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

    protected void recalculateIfInsetsChanged() {
        SynthContext context = getContext(slider);
        Insets newInsets = style.getInsets(context, null);
        if (!newInsets.equals(insetCache)) {
            insetCache = newInsets;
            calculateGeometry();
        }
        context.dispose();
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
        context.getPainter().paintSliderBackground(context,
                          g, 0, 0, c.getWidth(), c.getHeight());
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

        if (paintValue) {
            FontMetrics fm = SwingUtilities2.getFontMetrics(slider, g);
            valueRect.x = (thumbRect.x + (thumbRect.width / 2)) -
                context.getStyle().getGraphicsUtils(context).
                computeStringWidth(context, g.getFont(), fm,
                "" + slider.getValue()) / 2;
            g.setColor(context.getStyle().getColor(
                    context, ColorType.TEXT_FOREGROUND));
            context.getStyle().getGraphicsUtils(context).paintText(
                    context, g, "" + slider.getValue(), valueRect.x,
                    valueRect.y, -1);
        }
        
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

    public void paintBorder(SynthContext context, Graphics g, int x,
                            int y, int w, int h) {
        context.getPainter().paintSliderBorder(context, g, x, y, w, h);
    }

    public void paintThumb(SynthContext context, Graphics g,
            Rectangle thumbBounds)  {        
        int orientation = slider.getOrientation();
        SynthLookAndFeel.updateSubregion(context, g, thumbBounds);
        context.getPainter().paintSliderThumbBackground(context, g,
                             thumbBounds.x, thumbBounds.y, thumbBounds.width,
                             thumbBounds.height, orientation);
        context.getPainter().paintSliderThumbBorder(context, g,
                             thumbBounds.x, thumbBounds.y, thumbBounds.width,
                             thumbBounds.height, orientation);
    }

    public void paintTrack(SynthContext context, Graphics g,
            Rectangle trackBounds) {
        SynthLookAndFeel.updateSubregion(context, g, trackBounds);
        context.getPainter().paintSliderTrackBackground(context, g,
                trackBounds.x, trackBounds.y, trackBounds.width,
                trackBounds.height);
        context.getPainter().paintSliderTrackBorder(context, g,
                trackBounds.x, trackBounds.y, trackBounds.width,
                trackBounds.height);
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (SynthLookAndFeel.shouldUpdateStyle(e)) {
            updateStyle((JSlider)e.getSource());
        }
    }

    //////////////////////////////////////////////////
    /// Track Listener Class
    //////////////////////////////////////////////////
    /**
     * Track mouse movements.
     */
    protected class SynthTrackListener extends TrackListener {

        public void mouseExited(MouseEvent e) {
            setThumbActive(false);
        }

        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);
            updateThumbState(e.getX(), e.getY());
        }

        public void mouseDragged(MouseEvent e) {
            SynthScrollBarUI ui;
            int thumbMiddle = 0;

            if (!slider.isEnabled()) {
                return;
            }

            currentMouseX = e.getX();
            currentMouseY = e.getY();

            if (!isDragging()) {
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

        public void mouseMoved(MouseEvent e) {
            updateThumbState(e.getX(), e.getY());
        }
    }
}
