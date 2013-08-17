/*
 * @(#)MetalSliderUI.java	1.24 98/08/28
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

import javax.swing.plaf.basic.BasicSliderUI;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.Insets;
import java.awt.Color;
import java.io.Serializable;
import java.awt.IllegalComponentStateException;
import java.awt.Polygon;
import java.beans.*;

import javax.swing.border.AbstractBorder;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;

/**
 * A Java L&F implementation of SliderUI.  
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.24 08/28/98
 * @author Tom Santos
 */
public class MetalSliderUI extends BasicSliderUI {

    protected final int TICK_BUFFER = 4;
    protected boolean filledSlider = false;
    protected static Color thumbColor;
    protected static Color highlightColor;
    protected static Color darkShadowColor;
    protected static int trackWidth;
    protected static int tickLength;
    protected static Icon horizThumbIcon;
    protected static Icon vertThumbIcon;


    protected final String SLIDER_FILL = "JSlider.isFilled";

    public static ComponentUI createUI(JComponent c)    {
        return new MetalSliderUI();
    }

    public MetalSliderUI() {
        super( null );
    }

    public void installUI( JComponent c ) {
        trackWidth = ((Integer)UIManager.get( "Slider.trackWidth" )).intValue();
        tickLength = ((Integer)UIManager.get( "Slider.majorTickLength" )).intValue();
        horizThumbIcon = UIManager.getIcon( "Slider.horizontalThumbIcon" );
        vertThumbIcon = UIManager.getIcon( "Slider.verticalThumbIcon" );

	super.installUI( c );

        thumbColor = UIManager.getColor("Slider.thumb");
        highlightColor = UIManager.getColor("Slider.highlight");
        darkShadowColor = UIManager.getColor("Slider.darkShadow");

        scrollListener.setScrollByBlock( false );

        Object sliderFillProp = c.getClientProperty( SLIDER_FILL );
        if ( sliderFillProp != null ) {
            filledSlider = ((Boolean)sliderFillProp).booleanValue();
        }
    }

    protected PropertyChangeListener createPropertyChangeListener( JSlider slider ) {
        return new MetalPropertyListener();
    }

    protected class MetalPropertyListener extends BasicSliderUI.PropertyChangeHandler {
        public void propertyChange( PropertyChangeEvent e ) {  // listen for slider fill
	    super.propertyChange( e );

	    String name = e.getPropertyName();
	    if ( name.equals( SLIDER_FILL ) ) {
	        if ( e.getNewValue() != null ) {
		    filledSlider = ((Boolean)e.getNewValue()).booleanValue();
		}
		else {
		    filledSlider = false;
		}
	    }
	}
    }

    public void paintThumb(Graphics g)  {
        Rectangle knobBounds = thumbRect;

        g.translate( knobBounds.x, knobBounds.y );

        if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
            horizThumbIcon.paintIcon( slider, g, 0, 0 );
        }
        else {
            vertThumbIcon.paintIcon( slider, g, 0, 0 );
        }

        g.translate( -knobBounds.x, -knobBounds.y );
    }

    public void paintTrack(Graphics g)  {
        Color trackColor = !slider.isEnabled() ? MetalLookAndFeel.getControlShadow() :
                           slider.getForeground();

        g.translate( trackRect.x, trackRect.y );

        int trackLeft = 0;
        int trackTop = 0;
        int trackRight = 0;
        int trackBottom = 0;

        // Draw the track
	if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
            trackBottom = (trackRect.height - 1) - getThumbOverhang();
            trackTop = trackBottom - (getTrackWidth() - 1);
            trackRight = trackRect.width - 1;
	}
	else {
            trackLeft = (trackRect.width - getThumbOverhang()) - getTrackWidth();
            trackRight = (trackRect.width - getThumbOverhang()) - 1;
            trackBottom = trackRect.height - 1;
	}

	if ( slider.isEnabled() ) {
	    g.setColor( MetalLookAndFeel.getControlDarkShadow() );
	    g.drawRect( trackLeft, trackTop,
			(trackRight - trackLeft) - 1, (trackBottom - trackTop) - 1 );
	    
	    g.setColor( MetalLookAndFeel.getControlHighlight() );
	    g.drawLine( trackLeft + 1, trackBottom, trackRight, trackBottom );
	    g.drawLine( trackRight, trackTop + 1, trackRight, trackBottom );

	    g.setColor( MetalLookAndFeel.getControlShadow() );
	    g.drawLine( trackLeft + 1, trackTop + 1, trackRight - 2, trackTop + 1 );
	    g.drawLine( trackLeft + 1, trackTop + 1, trackLeft + 1, trackBottom - 2 );
	}
	else {
	    g.setColor( MetalLookAndFeel.getControlShadow() );
	    g.drawRect( trackLeft, trackTop,
			(trackRight - trackLeft) - 1, (trackBottom - trackTop) - 1 );
	}

        // Draw the fill
	if ( filledSlider ) {
	    int middleOfThumb = 0;
	    int fillTop = 0;
	    int fillLeft = 0;
	    int fillBottom = 0;
	    int fillRight = 0;

	    if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
	        middleOfThumb = thumbRect.x + (thumbRect.width / 2);
		middleOfThumb -= trackRect.x; // To compensate for the g.translate()
		fillTop = !slider.isEnabled() ? trackTop : trackTop + 1;
		fillBottom = !slider.isEnabled() ? trackBottom - 1 : trackBottom - 2;
		
		if ( !slider.getInverted() ) {
		    fillLeft = !slider.isEnabled() ? trackLeft : trackLeft + 1;
		    fillRight = middleOfThumb;
		}
		else {
		    fillLeft = middleOfThumb;
		    fillRight = !slider.isEnabled() ? trackRight - 1 : trackRight - 2;
		}
	    }
	    else {
	        middleOfThumb = thumbRect.y + (thumbRect.height / 2);
		middleOfThumb -= trackRect.y; // To compensate for the g.translate()
		fillLeft = !slider.isEnabled() ? trackLeft : trackLeft + 1;
		fillRight = !slider.isEnabled() ? trackRight - 1 : trackRight - 2;
		
		if ( !slider.getInverted() ) {
		    fillTop = middleOfThumb;
		    fillBottom = !slider.isEnabled() ? trackBottom - 1 : trackBottom - 2;
		}
		else {
		    fillTop = !slider.isEnabled() ? trackTop : trackTop + 1;
		    fillBottom = middleOfThumb;
		}
	    }
	    
	    if ( slider.isEnabled() ) {
	        g.setColor( slider.getBackground() );
		g.drawLine( fillLeft, fillTop, fillRight, fillTop );
		g.drawLine( fillLeft, fillTop, fillLeft, fillBottom );

		g.setColor( MetalLookAndFeel.getControlShadow() );
		g.fillRect( fillLeft + 1, fillTop + 1,
			    fillRight - fillLeft, fillBottom - fillTop );
	    }
	    else {
	        g.setColor( MetalLookAndFeel.getControlShadow() );
		g.fillRect( fillLeft, fillTop,
			    fillRight - fillLeft, trackBottom - trackTop );
	    }
	}

        g.translate( -trackRect.x, -trackRect.y );
    }

    public void paintFocus(Graphics g)  {        
    }

    protected Dimension getThumbSize() {
        Dimension size = new Dimension();

        if ( slider.getOrientation() == JSlider.VERTICAL ) {
	    size.width = 16;
	    size.height = 15;
	}
	else {
	    size.width = 15;
	    size.height = 16;
	}

	return size;
    }

    /**
     * Gets the height of the tick area for horizontal sliders and the width of the
     * tick area for vertical sliders.  BasicSliderUI uses the returned value to
     * determine the tick area rectangle.
     */
    public int getTickLength() {
        return slider.getOrientation() == JSlider.HORIZONTAL ? tickLength + TICK_BUFFER + 1 :
        tickLength + TICK_BUFFER + 3;
    }

    /**
     * Returns the shorter dimension of the track.
     */
    protected int getTrackWidth() {
        // This strange calculation is here to keep the
        // track in proportion to the thumb.
        final double kIdealTrackWidth = 7.0;
	final double kIdealThumbHeight = 16.0;
        final double kWidthScalar = kIdealTrackWidth / kIdealThumbHeight;

        if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
	    return (int)(kWidthScalar * thumbRect.height);
	}
	else {
	    return (int)(kWidthScalar * thumbRect.width);
	}
    }

    /**
     * Returns the longer dimension of the slide bar.  (The slide bar is only the
     * part that runs directly under the thumb)
     */
    protected int getTrackLength() {   
        if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
            return trackRect.width; 
        }
        return trackRect.height;
    }

    /**
     * Returns the amount that the thumb goes past the slide bar.
     */
    protected int getThumbOverhang() {
        return 5;
    }

    protected void scrollDueToClickInTrack( int dir ) {
        scrollByUnit( dir );
    }

    protected void paintMinorTickForHorizSlider( Graphics g, Rectangle tickBounds, int x ) {
        g.setColor( slider.isEnabled() ? slider.getForeground() : MetalLookAndFeel.getControlShadow() );
        g.drawLine( x, TICK_BUFFER, x, TICK_BUFFER + (tickLength / 2) );
    }

    protected void paintMajorTickForHorizSlider( Graphics g, Rectangle tickBounds, int x ) {
        g.setColor( slider.isEnabled() ? slider.getForeground() : MetalLookAndFeel.getControlShadow() );
        g.drawLine( x, TICK_BUFFER , x, TICK_BUFFER + (tickLength - 1) );
    }

    protected void paintMinorTickForVertSlider( Graphics g, Rectangle tickBounds, int y ) {
        g.setColor( slider.isEnabled() ? slider.getForeground() : MetalLookAndFeel.getControlShadow() );
        g.drawLine( TICK_BUFFER, y, TICK_BUFFER + (tickLength / 2), y );
    }

    protected void paintMajorTickForVertSlider( Graphics g, Rectangle tickBounds, int y ) {
        g.setColor( slider.isEnabled() ? slider.getForeground() : MetalLookAndFeel.getControlShadow() );
        g.drawLine( TICK_BUFFER, y, TICK_BUFFER + tickLength, y );
    }
}
