/*
 * @(#)WindowsSliderUI.java	1.18 06/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import java.awt.*;

import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.*;

import com.sun.java.swing.plaf.windows.TMSchema.*;
import com.sun.java.swing.plaf.windows.XPStyle.Skin;


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
            Part part = vertical ? Part.TKP_TRACKVERT : Part.TKP_TRACK;
            Skin skin = xp.getSkin(slider, part);

	    if (vertical) {
		int x = (trackRect.width - skin.getWidth()) / 2;
                skin.paintSkin(g, trackRect.x + x, trackRect.y,
                               skin.getWidth(), trackRect.height, null);
	    } else {
		int y = (trackRect.height - skin.getHeight()) / 2;
                skin.paintSkin(g, trackRect.x, trackRect.y + y,
                               trackRect.width, skin.getHeight(), null);
	    }
	} else {
	    super.paintTrack(g);
	}
    }


    protected void paintMinorTickForHorizSlider( Graphics g, Rectangle tickBounds, int x ) {
	XPStyle xp = XPStyle.getXP();
	if (xp != null) {
            g.setColor(xp.getColor(slider, Part.TKP_TICS, null, Prop.COLOR, Color.black));
	}
	super.paintMinorTickForHorizSlider(g, tickBounds, x);
    }

    protected void paintMajorTickForHorizSlider( Graphics g, Rectangle tickBounds, int x ) {
	XPStyle xp = XPStyle.getXP();
	if (xp != null) {
            g.setColor(xp.getColor(slider, Part.TKP_TICS, null, Prop.COLOR, Color.black));
	}
	super.paintMajorTickForHorizSlider(g, tickBounds, x);
    }

    protected void paintMinorTickForVertSlider( Graphics g, Rectangle tickBounds, int y ) {
	XPStyle xp = XPStyle.getXP();
	if (xp != null) {
            g.setColor(xp.getColor(slider, Part.TKP_TICSVERT, null, Prop.COLOR, Color.black));
	}
	super.paintMinorTickForVertSlider(g, tickBounds, y);
    }

    protected void paintMajorTickForVertSlider( Graphics g, Rectangle tickBounds, int y ) {
	XPStyle xp = XPStyle.getXP();
	if (xp != null) {
            g.setColor(xp.getColor(slider, Part.TKP_TICSVERT, null, Prop.COLOR, Color.black));
	}
	super.paintMajorTickForVertSlider(g, tickBounds, y);
    }


    public void paintThumb(Graphics g)  {        
	XPStyle xp = XPStyle.getXP();
	if (xp != null) {
	    // Pending: Implement all five states
            Part part = getXPThumbPart();
            State state = slider.isEnabled() ? State.NORMAL : State.DISABLED;
            xp.getSkin(slider, part).paintSkin(g, thumbRect.x, thumbRect.y, state);
	} else {
	    super.paintThumb(g);
	}
    }

    protected Dimension getThumbSize() {
        XPStyle xp = XPStyle.getXP();
	if (xp != null) {
            Dimension size = new Dimension();
            Skin s = xp.getSkin(slider, getXPThumbPart());
            size.width = s.getWidth();
            size.height = s.getHeight();
            return size;
	} else {
	    return super.getThumbSize();
	}
    }

    private Part getXPThumbPart() {
	XPStyle xp = XPStyle.getXP();
        Part part;
        boolean vertical = (slider.getOrientation() == JSlider.VERTICAL);
	boolean leftToRight = slider.getComponentOrientation().isLeftToRight();
	Boolean paintThumbArrowShape =
		(Boolean)slider.getClientProperty("Slider.paintThumbArrowShape");
	if ((!slider.getPaintTicks() && paintThumbArrowShape == null) ||
            paintThumbArrowShape == Boolean.FALSE) {
            part = vertical ? Part.TKP_THUMBVERT
                : Part.TKP_THUMB;
	} else {
            part = vertical 
                ? (leftToRight ? Part.TKP_THUMBRIGHT : Part.TKP_THUMBLEFT)
                : Part.TKP_THUMBBOTTOM;
	}
        return part;
    }
}

