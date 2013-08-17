/*
 * @(#)MotifSliderUI.java	1.16 98/08/28
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package com.sun.java.swing.plaf.motif;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;

import javax.swing.plaf.basic.BasicSliderUI;

/**
 * Motif Slider
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.16 08/28/98
 * @author Jeff Dinkins
 */
public class MotifSliderUI extends BasicSliderUI {

    static final Dimension PREFERRED_HORIZONTAL_SIZE = new Dimension(164, 15);
    static final Dimension PREFERRED_VERTICAL_SIZE = new Dimension(15, 164);

    static final Dimension MINIMUM_HORIZONTAL_SIZE = new Dimension(43, 15);
    static final Dimension MINIMUM_VERTICAL_SIZE = new Dimension(15, 43);

    /**
     * MotifSliderUI Constructor
     */
    public MotifSliderUI(JSlider b)   {
        super(b);
    }

    /**
     * create a MotifSliderUI object
     */
    public static ComponentUI createUI(JComponent b)    {
        return new MotifSliderUI((JSlider)b);
    }

    public Dimension getPreferredHorizontalSize() {
        return PREFERRED_HORIZONTAL_SIZE;
    }

    public Dimension getPreferredVerticalSize() {
        return PREFERRED_VERTICAL_SIZE;
    }

    public Dimension getMinimumHorizontalSize() {
        return MINIMUM_HORIZONTAL_SIZE;
    }

    public Dimension getMinimumVerticalSize() {
        return MINIMUM_VERTICAL_SIZE;
    }

    protected Dimension getThumbSize() {
        if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
	    return new Dimension( 30, 15 );
	}
	else {
	    return new Dimension( 15, 30 );
	}
    }

    public void paintFocus(Graphics g)  {        
    }

    public void paintTrack(Graphics g)  {        
    }
  
    public void paintThumb(Graphics g)  {
        Rectangle knobBounds = thumbRect;

        int x = knobBounds.x;
        int y = knobBounds.y;       
        int w = knobBounds.width;
        int h = knobBounds.height;      

        if ( slider.isEnabled() ) {
            g.setColor(slider.getForeground());
        }
        else {
            // PENDING(jeff) - the thumb should be dithered when disabled
            g.setColor(slider.getForeground().darker());
        }

        if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
            g.translate(x, knobBounds.y-1);

            // fill
            g.fillRect(0, 1, w, h - 1);

            // highlight
            g.setColor(getHighlightColor());
            g.drawLine(0, 1, w - 1, 1);             // top
            g.drawLine(0, 1, 0, h);                     // left
            g.drawLine(w/2, 2, w/2, h-1);       // center

            // shadow
            g.setColor(getShadowColor());
            g.drawLine(0, h, w - 1, h);         // bottom
            g.drawLine(w - 1, 1, w - 1, h);     // right
            g.drawLine(w/2 - 1, 2, w/2 - 1, h); // center

            g.translate(-x, -(knobBounds.y-1));
        }
        else {
            g.translate(knobBounds.x-1, 0);

            // fill
            g.fillRect(1, y, w - 1, h);

            // highlight
            g.setColor(getHighlightColor());
            g.drawLine(1, y, w, y);                     // top
            g.drawLine(1, y+1, 1, y+h-1);               // left
            g.drawLine(2, y+h/2, w-1, y+h/2);           // center

            // shadow
            g.setColor(getShadowColor());
            g.drawLine(2, y+h-1, w, y+h-1);             // bottom
            g.drawLine(w, y+h-1, w, y);                 // right
            g.drawLine(2, y+h/2-1, w-1, y+h/2-1);       // center

            g.translate(-(knobBounds.x-1), 0);
        }
    }
   
    protected void installKeyboardActions( JSlider slider ) {
        super.installKeyboardActions( slider );
        slider.unregisterKeyboardAction( KeyStroke.getKeyStroke( KeyEvent.VK_PAGE_UP, 0 ) );
        slider.unregisterKeyboardAction( KeyStroke.getKeyStroke( KeyEvent.VK_PAGE_DOWN, 0 ) );

        slider.registerKeyboardAction(new ActionScroller(slider, POSITIVE_SCROLL, true),
                                      KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, Event.CTRL_MASK), 
                                      JComponent.WHEN_FOCUSED);

        slider.registerKeyboardAction(new ActionScroller(slider, NEGATIVE_SCROLL, true),
                                      KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, Event.CTRL_MASK), 
                                      JComponent.WHEN_FOCUSED);
    }

    protected void uninstallKeyboardActions( JSlider slider ) {
        slider.unregisterKeyboardAction( KeyStroke.getKeyStroke( KeyEvent.VK_RIGHT, 0 ) );
        slider.unregisterKeyboardAction( KeyStroke.getKeyStroke( KeyEvent.VK_DOWN, 0 ) );
        slider.unregisterKeyboardAction( KeyStroke.getKeyStroke( KeyEvent.VK_PAGE_DOWN, Event.CTRL_MASK ) );
        slider.unregisterKeyboardAction( KeyStroke.getKeyStroke( KeyEvent.VK_LEFT, 0 ) );
        slider.unregisterKeyboardAction( KeyStroke.getKeyStroke( KeyEvent.VK_UP, 0 ) );
        slider.unregisterKeyboardAction( KeyStroke.getKeyStroke( KeyEvent.VK_PAGE_UP, Event.CTRL_MASK ) );
        slider.unregisterKeyboardAction( KeyStroke.getKeyStroke( KeyEvent.VK_HOME, 0 ) );
        slider.unregisterKeyboardAction( KeyStroke.getKeyStroke( KeyEvent.VK_END, 0 ) );
    }
}

