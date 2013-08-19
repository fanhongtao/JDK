/*
 * @(#)WindowsInternalFrameUI.java	1.21 03/01/31
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import java.awt.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.ComponentUI;

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
public class WindowsInternalFrameUI extends BasicInternalFrameUI
{
    XPStyle xp = XPStyle.getXP();

    public void installDefaults() {
        super.installDefaults();

	if (xp != null) {
	    frame.setBorder(new XPBorder());
	} else {
	    frame.setBorder(UIManager.getBorder("InternalFrame.border"));
	}
    }

    public void installUI(JComponent c)   {
	super.installUI(c);

	c.setOpaque(xp == null);
    }

    public void uninstallDefaults() {
        frame.setBorder(null);
        super.uninstallDefaults();
    }

    public static ComponentUI createUI(JComponent b)    {
        return new WindowsInternalFrameUI((JInternalFrame)b);
    }

    public WindowsInternalFrameUI(JInternalFrame w){
	super(w);
    }

    protected DesktopManager createDesktopManager(){
        return new WindowsDesktopManager();
    }

    protected JComponent createNorthPane(JInternalFrame w) {
        titlePane = new WindowsInternalFrameTitlePane(w);
        return titlePane;
    }

    protected LayoutManager createLayoutManager(){
	if (XPStyle.getXP() != null) {
	    return new BasicInternalFrameUI.InternalFrameLayout() {
		public void layoutContainer(Container c) {
		    Insets i = frame.getInsets();
		    int cx, cy, cw, ch;

		    cx = i.left;
		    cy = 0;
		    cw = frame.getWidth() - i.left - i.right;
		    ch = frame.getHeight() - i.bottom;

		    if (getNorthPane() != null) {
			Dimension size = getNorthPane().getPreferredSize();
			// Ignore insets when placing the title pane
			getNorthPane().setBounds(0, 0, frame.getWidth(), size.height);
			cy += size.height;
			ch -= size.height;
		    }

		    if (getSouthPane() != null) {
			Dimension size = getSouthPane().getPreferredSize();
			getSouthPane().setBounds(cx, frame.getHeight() - i.bottom - size.height, 
						 cw, size.height);
			ch -= size.height;
		    }

		    if (getWestPane() != null) {
			Dimension size = getWestPane().getPreferredSize();
			getWestPane().setBounds(cx, cy, size.width, ch);
			cw -= size.width;
			cx += size.width;           
		    }

		    if (getEastPane() != null) {
			Dimension size = getEastPane().getPreferredSize();
			getEastPane().setBounds(cw - size.width, cy, size.width, ch);
			cw -= size.width;           
		    }

		    if (frame.getRootPane() != null) {
			frame.getRootPane().setBounds(cx, cy, cw, ch);
		    }
		}
	    };
	} else {
	    return super.createLayoutManager();
	}
    }
			
    private class XPBorder extends AbstractBorder {
	private XPStyle.Skin leftSkin   = xp.getSkin("window.frameleft");
	private XPStyle.Skin rightSkin  = xp.getSkin("window.frameright");
	private XPStyle.Skin bottomSkin = xp.getSkin("window.framebottom");

	/**
	 * @param x the x position of the painted border
	 * @param y the y position of the painted border
	 * @param width the width of the painted border
	 * @param height the height of the painted border
	 */
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
	    boolean isSelected = ((JInternalFrame)c).isSelected();
	    int topBorderHeight  = (titlePane != null) ? titlePane.getSize().height : 0;

	    bottomSkin.paintSkin(g, 0, height-bottomSkin.getHeight(),
				 width, bottomSkin.getHeight(),
				 isSelected ? 0 : 1);

	    leftSkin.paintSkin(g, 0, topBorderHeight-1,
			       leftSkin.getWidth(), height-topBorderHeight-bottomSkin.getHeight()+2,
			       isSelected ? 0 : 1);

	    rightSkin.paintSkin(g, width-rightSkin.getWidth(), topBorderHeight-1,
				rightSkin.getWidth(), height-topBorderHeight-bottomSkin.getHeight()+2,
				isSelected ? 0 : 1);

	}

	public Insets getBorderInsets(Component c)       { 
	    return getBorderInsets(c, new Insets(0, 0, 0, 0));
	}

	public Insets getBorderInsets(Component c, Insets insets) {
	    insets.top    = 4;
	    insets.left   = leftSkin.getWidth();
	    insets.right  = rightSkin.getWidth();
	    insets.bottom = bottomSkin.getHeight();

	    return insets;
	}

	public boolean isBorderOpaque() {
	    return true;
	}
    }

}
