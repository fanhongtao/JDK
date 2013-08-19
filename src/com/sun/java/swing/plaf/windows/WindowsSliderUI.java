/*
 * @(#)WindowsSliderUI.java	1.13 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import java.awt.*;

import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.*;



/**
 * Windows rendition of the component.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 */
public class WindowsSliderUI extends BasicSliderUI
{
    public WindowsSliderUI(JSlider b){
	super(b);
    }

    public static ComponentUI createUI(JComponent b) {
        return new WindowsSliderUI((JSlider)b);
    }


    public void paintTrack(Graphics g)  {        
	XPStyle xp = XPStyle.getXP();
	if (xp != null) {
	    boolean vertical = (slider.getOrientation() == JSlider.VERTICAL);
	    String category = vertical ? "trackbar.trackvert" : "trackbar.track";
	    XPStyle.Skin skin = xp.getSkin(category);

	    if (vertical) {
		int x = (trackRect.width - skin.getWidth()) / 2;
		skin.paintSkin(g, trackRect.x + x, trackRect.y, skin.getWidth(), trackRect.height, 0);
	    } else {
		int y = (trackRect.height - skin.getHeight()) / 2;
		skin.paintSkin(g, trackRect.x, trackRect.y + y, trackRect.width, skin.getHeight(), 0);
	    }
	} else {
	    super.paintTrack(g);
	}
    }


    protected void paintMinorTickForHorizSlider( Graphics g, Rectangle tickBounds, int x ) {
	XPStyle xp = XPStyle.getXP();
	if (xp != null) {
	    g.setColor(xp.getColor("trackbar.tics.color", Color.black));
	}
	super.paintMinorTickForHorizSlider(g, tickBounds, x);
    }

    protected void paintMajorTickForHorizSlider( Graphics g, Rectangle tickBounds, int x ) {
	XPStyle xp = XPStyle.getXP();
	if (xp != null) {
	    g.setColor(xp.getColor("trackbar.tics.color", Color.black));
	}
	super.paintMajorTickForHorizSlider(g, tickBounds, x);
    }

    protected void paintMinorTickForVertSlider( Graphics g, Rectangle tickBounds, int y ) {
	XPStyle xp = XPStyle.getXP();
	if (xp != null) {
	    g.setColor(xp.getColor("trackbar.ticsvert.color", Color.black));
	}
	super.paintMinorTickForVertSlider(g, tickBounds, y);
    }

    protected void paintMajorTickForVertSlider( Graphics g, Rectangle tickBounds, int y ) {
	XPStyle xp = XPStyle.getXP();
	if (xp != null) {
	    g.setColor(xp.getColor("trackbar.ticsvert.color", Color.black));
	}
	super.paintMajorTickForVertSlider(g, tickBounds, y);
    }


    public void paintThumb(Graphics g)  {        
	XPStyle xp = XPStyle.getXP();
	if (xp != null) {
	    String category;
	    boolean vertical = (slider.getOrientation() == JSlider.VERTICAL);
	    boolean leftToRight = slider.getComponentOrientation().isLeftToRight();
	    if (!slider.getPaintTicks()) {
		category = vertical ? "trackbar.thumbvert"
				    : "trackbar.thumb";
	    } else {
		category = vertical ? (leftToRight ? "trackbar.thumbright" : "trackbar.thumbleft")
				    : "trackbar.thumbbottom";
	    }
	    // Pending: Implement all five states
	    int index = 0;
	    if (!slider.isEnabled()) {
		index = 4;
	    }
	    xp.getSkin(category).paintSkin(g, thumbRect.x, thumbRect.y, index);
	} else {
	    super.paintThumb(g);
	}
    }
}

