/*
 * @(#)MetacityCrux.java	1.7 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;


import java.awt.*;
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

/**
 * @version 1.7, 01/23/03
 */
class MetacityCrux extends Metacity {

    protected MetacityCrux(String themeDir) {
	super(themeDir, normalFrameGeometry);
    }



    // Constants from Metacity 2.4.1 Crux/metacity-theme-1.xml (use same variable names)
    /*
	<constant name="IconTitleSpacing" value="2"/> <!-- Space between menu button and title -->
	<constant name="CenterTitlePieceWidth" value="48"/> <!-- Width of center title piece -->
	<constant name="ButtonWidth" value="16"/> <!-- Button width -->
    */
    private static final int IconTitleSpacing = 2;
    private static final int CenterTitlePieceWidth = 48;
    private static final int ButtonWidth = 16;

    /*
	<frame_geometry name="normal">
	  <distance name="left_width" value="5"/>
	  <distance name="right_width" value="6"/>
	  <distance name="bottom_height" value="6"/>
	  <distance name="left_titlebar_edge" value="5"/>
	  <distance name="right_titlebar_edge" value="6"/>
	  <aspect_ratio name="button" value="1.0"/>
	  <distance name="title_vertical_pad" value="0"/>
	  <border name="title_border" left="0" right="0" top="3" bottom="3"/>
	  <border name="button_border" left="0" right="0" top="3" bottom="3"/>
	</frame_geometry>
    */
    private static class NormalFrameGeometry extends FrameGeometry {
	NormalFrameGeometry() {
	    left_width = 5;
	    right_width = 6;
	    bottom_height = 6;
	    left_titlebar_edge = 5;
	    right_titlebar_edge = 6;
	    aspect_ratio = 1.0F;
	    title_vertical_pad = 0;
	    title_border = new Insets(3, 0, 3, 0);
	    button_border = new Insets(3, 0, 3, 0);
	}
    };

    private static final FrameGeometry normalFrameGeometry = new NormalFrameGeometry();



    // draw_ops

    // <!-- Buttons -->

    private void active_button(Graphics g, int width, int height) {
	/*
	  <image filename="active-button.png" x="0" y="0" width="width" height="height"/>
	*/
	g.drawImage(getImage("active-button"), 0, 0, width, height, null);
    }

    private void active_button_pressed(Graphics g, int width, int height) {
	/*
	  <image filename="active-button-pressed.png" x="0" y="0" width="width" height="height"/>
	*/
	g.drawImage(getImage("active-button-pressed"), 0, 0, width, height, null);
    }

    private void active_button_prelight(Graphics g, int width, int height) {
	/*
	  <image filename="active-button-prelight.png" x="0" y="0" width="width" height="height"/>
	*/
	g.drawImage(getImage("active-button-prelight"), 0, 0, width, height, null);
    }


    private void inactive_button(Graphics g, int width, int height) {
	/*
	  <image filename="inactive-button.png" x="0" y="0" width="width" height="height"/>
	*/
	g.drawImage(getImage("inactive-button"), 0, 0, width, height, null);
    }

    private void inactive_button_pressed(Graphics g, int width, int height) {
	/*
	  <image filename="inactive-button-pressed.png" x="0" y="0" width="width" height="height"/>
	*/
	g.drawImage(getImage("inactive-button-pressed"), 0, 0, width, height, null);
    }

    private void inactive_button_prelight(Graphics g, int width, int height) {
	/*
	  <image filename="inactive-button-prelight.png" x="0" y="0" width="width" height="height"/>
	*/
	g.drawImage(getImage("inactive-button-prelight"), 0, 0, width, height, null);
    }

    void paintButtonBackground(SynthContext context, Graphics g, int x, int y, int w,int h) {
	this.context = context;
	JButton button = (JButton)context.getComponent();
	String buttonName = button.getName();
	int buttonState = context.getComponentState();

	JComponent titlePane = (JComponent)button.getParent();
	Container titlePaneParent = titlePane.getParent();

	JInternalFrame frame;
	if (titlePaneParent instanceof JInternalFrame) {
	    frame = (JInternalFrame)titlePaneParent;
	} else if (titlePaneParent instanceof JInternalFrame.JDesktopIcon) {
	    frame = ((JInternalFrame.JDesktopIcon)titlePaneParent).getInternalFrame();
	} else {
	    return;
	}

	boolean active = frame.isSelected();
	setFrameGeometry(titlePane, normalFrameGeometry);

	if (!active) {
	    if (buttonName.equals("InternalFrameTitlePane.menuButton")) {
		if ((buttonState & SynthConstants.PRESSED) != 0) {
		    /*
      <image filename="inactive-menu-button-pressed.png" x="0" y="0" width="width" height="height"/>
      		    */
		    g.drawImage(getImage("inactive-menu-button-pressed"), x, y, w, h, null);
		} else if ((buttonState & SynthConstants.MOUSE_OVER) != 0) {
		    /*
      <image filename="inactive-menu-button-prelight.png" x="0" y="0" width="width" height="height"/>
		    */
		    g.drawImage(getImage("inactive-menu-button-prelight"), x, y, w, h, null);
		} else {
		    /*
      <image filename="inactive-menu-button.png" x="0" y="0" width="width" height="height"/>
		    */
		    g.drawImage(getImage("inactive-menu-button"), x, y, w, h, null);
		}
	    } else if (buttonName.equals("InternalFrameTitlePane.iconifyButton")) {
		if ((buttonState & SynthConstants.PRESSED) != 0) {
		    /*
		      <include name="inactive_button_pressed"/>
		      <image filename="inactive-minimize-button.png" x="2" y="2" width="width-4" height="height-4"/>
		    */
		    inactive_button_pressed(g, w, h);
		    g.drawImage(getImage("inactive-minimize-button"), x+2, y+2, w-4, h-4, null);
		} else if ((buttonState & SynthConstants.MOUSE_OVER) != 0) {
		    /*
		      <include name="inactive_button_prelight"/>
		      <image filename="inactive-minimize-button.png" x="2" y="2" width="width-4" height="height-4"/>
		    */
		    inactive_button_prelight(g, w, h);
		    g.drawImage(getImage("inactive-minimize-button"), x+2, y+2, w-4, h-4, null);
		} else {
		    /*
		      <include name="inactive_button"/>
		      <image filename="inactive-minimize-button.png" x="2" y="2" width="width-4" height="height-4"/>
		    */
		    inactive_button(g, w, h);
		    g.drawImage(getImage("inactive-minimize-button"), x+2, y+2, w-4, h-4, null);
		}
	    } else if (buttonName.equals("InternalFrameTitlePane.maximizeButton")) {
		if ((buttonState & SynthConstants.PRESSED) != 0) {
		    /*
		      <include name="inactive_button_pressed"/>
		      <image filename="inactive-maximize-button.png" x="2" y="2" width="width-4" height="height-4"/>
		    */
		    inactive_button_pressed(g, w, h);
		    g.drawImage(getImage("inactive-maximize-button"), x+2, y+2, w-4, h-4, null);
		} else if ((buttonState & SynthConstants.MOUSE_OVER) != 0) {
		    /*
		      <include name="inactive_button_prelight"/>
		      <image filename="inactive-maximize-button.png" x="2" y="2" width="width-4" height="height-4"/>
		    */
		    inactive_button_prelight(g, w, h);
		    g.drawImage(getImage("inactive-maximize-button"), x+2, y+2, w-4, h-4, null);
		} else {
		    /*
		      <include name="inactive_button"/>
		      <image filename="inactive-maximize-button.png" x="2" y="2" width="width-4" height="height-4"/>
		    */
		    inactive_button(g, w, h);
		    g.drawImage(getImage("inactive-maximize-button"), x+2, y+2, w-4, h-4, null);
		}
	    } else if (buttonName.equals("InternalFrameTitlePane.closeButton")) {
		if ((buttonState & SynthConstants.PRESSED) != 0) {
		    /*
		      <include name="inactive_button_pressed"/>
		      <image filename="inactive-close-button.png" x="2" y="2" width="width-4" height="height-4"/>
		    */
		    inactive_button_pressed(g, w, h);
		    g.drawImage(getImage("inactive-close-button"), x+2, y+2, w-4, h-4, null);
		} else if ((buttonState & SynthConstants.MOUSE_OVER) != 0) {
		    /*
		      <include name="inactive_button_prelight"/>
		      <image filename="inactive-close-button.png" x="2" y="2" width="width-4" height="height-4"/>
		    */
		    inactive_button_prelight(g, w, h);
		    g.drawImage(getImage("inactive-close-button"), x+2, y+2, w-4, h-4, null);
		} else {
		    /*
		      <include name="inactive_button"/>
		      <image filename="inactive-close-button.png" x="2" y="2" width="width-4" height="height-4"/>
		    */
		    inactive_button(g, w, h);
		    g.drawImage(getImage("inactive-close-button"), x+2, y+2, w-4, h-4, null);
		}
	    }
	} else {
	    if (buttonName.equals("InternalFrameTitlePane.menuButton")) {
		if ((buttonState & SynthConstants.PRESSED) != 0) {
		    /*
	  <image colorize="gtk:bg[SELECTED]" filename="active-menu-button-pressed.png" x="0" y="0" width="width" height="height"/>
		    */
		    g.drawImage(getImage("active-menu-button-pressed",
					 getColor(SELECTED, GTKColorType.BACKGROUND)),
				x, y, w, h, null);
		} else if ((buttonState & SynthConstants.MOUSE_OVER) != 0) {
		    /*
	  <image colorize="gtk:bg[SELECTED]" filename="active-menu-button-prelight.png" x="0" y="0" width="width" height="height"/>
		    */
		    g.drawImage(getImage("active-menu-button-prelight",
					 getColor(SELECTED, GTKColorType.BACKGROUND)),
				x, y, w, h, null);
		} else {
		    /*
	  <image colorize="gtk:bg[SELECTED]" filename="active-menu-button.png" x="0" y="0" width="width" height="height"/>
		    */
		    g.drawImage(getImage("active-menu-button",
					 getColor(SELECTED, GTKColorType.BACKGROUND)),
				x, y, w, h, null);
		}
	    } else if (buttonName.equals("InternalFrameTitlePane.iconifyButton")) {
		if ((buttonState & SynthConstants.PRESSED) != 0) {
		    /*
		      <include name="active_button_pressed"/>
		      <image filename="active-minimize-button.png" x="2" y="2" width="width-4" height="height-4"/>
		    */
		    active_button_pressed(g, w, h);
		    g.drawImage(getImage("active-minimize-button"), x+2, y+2, w-4, h-4, null);
		} else if ((buttonState & SynthConstants.MOUSE_OVER) != 0) {
		    /*
		      <include name="active_button_prelight"/>
		      <image filename="active-minimize-button.png" x="2" y="2" width="width-4" height="height-4"/>
		    */
		    active_button_prelight(g, w, h);
		    g.drawImage(getImage("active-minimize-button"), x+2, y+2, w-4, h-4, null);
		} else {
		    /*
		      <include name="active_button"/>
		      <image filename="active-minimize-button.png" x="2" y="2" width="width-4" height="height-4"/>
		    */
		    active_button(g, w, h);
		    g.drawImage(getImage("active-minimize-button"), x+2, y+2, w-4, h-4, null);
		}
	    } else if (buttonName.equals("InternalFrameTitlePane.maximizeButton")) {
		if ((buttonState & SynthConstants.PRESSED) != 0) {
		    /*
		      <include name="active_button_pressed"/>
		      <image filename="active-maximize-button.png" x="2" y="2" width="width-4" height="height-4"/>
		    */
		    active_button_pressed(g, w, h);
		    g.drawImage(getImage("active-maximize-button"), x+2, y+2, w-4, h-4, null);
		} else if ((buttonState & SynthConstants.MOUSE_OVER) != 0) {
		    /*
		      <include name="active_button_prelight"/>
		      <image filename="active-maximize-button.png" x="2" y="2" width="width-4" height="height-4"/>
		    */
		    active_button_prelight(g, w, h);
		    g.drawImage(getImage("active-maximize-button"), x+2, y+2, w-4, h-4, null);
		} else {
		    /*
		      <include name="active_button"/>
		      <image filename="active-maximize-button.png" x="2" y="2" width="width-4" height="height-4"/>
		    */
		    active_button(g, w, h);
		    g.drawImage(getImage("active-maximize-button"), x+2, y+2, w-4, h-4, null);
		}
	    } else if (buttonName.equals("InternalFrameTitlePane.closeButton")) {
		if ((buttonState & SynthConstants.PRESSED) != 0) {
		    /*
		      <include name="active_button_pressed"/>
		      <image filename="active-close-button.png" x="2" y="2" width="width-4" height="height-4"/>
		    */
		    active_button_pressed(g, w, h);
		    g.drawImage(getImage("active-close-button"), x+2, y+2, w-4, h-4, null);
		} else if ((buttonState & SynthConstants.MOUSE_OVER) != 0) {
		    /*
		      <include name="active_button_prelight"/>
		      <image filename="active-close-button.png" x="2" y="2" width="width-4" height="height-4"/>
		    */
		    active_button_prelight(g, w, h);
		    g.drawImage(getImage("active-close-button"), x+2, y+2, w-4, h-4, null);
		} else {
		    /*
		      <include name="active_button"/>
		      <image filename="active-close-button.png" x="2" y="2" width="width-4" height="height-4"/>
		    */
		    active_button(g, w, h);
		    g.drawImage(getImage("active-close-button"), x+2, y+2, w-4, h-4, null);
		}
	    }
	}
    }


    private void paintTitleBar(Graphics g, int width, int height,
			       boolean active, JInternalFrame frame, JComponent titlePane) {

	Image leftTopBorder     = getImage("left-top-border", active);
	Image topCenterLeft     = getImage("top-center-left", active);
	Image topCenterRight    = getImage("top-center-right", active);
	Image rightTopBorder    = getImage("right-top-border", active);
	Image topCenterMidLeft  = getImage("top-center-mid-left", active);
	Image topCenterMidRight = getImage("top-center-mid-right", active);
	Image topCenterMid      = getImage("top-center-mid", active);

	FrameGeometry gm = getFrameGeometry();
	FontMetrics fm = g.getFontMetrics();
	int title_width = calculateTitleWidth(frame, titlePane, fm);

	int x, y, w, h;
	Image object;

	if (!active) {
	    /*
	      <image filename="inactive-left-top-border.png" 
	           x="0" y="0" width="object_width" height="height"/>
	    */
	    object = leftTopBorder;
	    x = 0; y = 0; w = object.getWidth(null); h = height;
	    g.drawImage(object, x, y, w, h, null);

	    /*
	      <image filename="inactive-right-top-border.png" 
	           x="width - object_width" y="0" width="object_width" height="height"/>
	    */
	    object = rightTopBorder;
	    x = width - object.getWidth(null); y = 0; w = object.getWidth(null); h = height;
	    g.drawImage(object, x, y, w, h, null);

	    /*
	     <image filename="inactive-top-center-left.png" 
	           x="4" y="0" 
	           width="(left_width + ButtonWidth + IconTitleSpacing + title_width) `min`
	           (width - right_width - 3 * ButtonWidth
	                                   - CenterTitlePieceWidth * height / 22 - 3)" 
	           height="height"/>
	    */
	    object = topCenterLeft;
	    x = 4; y = 0;
	    w = Math.min(gm.left_width + ButtonWidth + IconTitleSpacing + title_width,
			 width - gm.right_width - 3 * ButtonWidth
			 - CenterTitlePieceWidth * height / 22 - 3);
	    h = height;
	    g.drawImage(object, x, y, w, h, null);

	    /*
	    <image filename="inactive-top-center-mid.png" 
	           x="((left_width + ButtonWidth + IconTitleSpacing + title_width) `min`
	                (width - object_width * height / 22 - right_width - 3 * ButtonWidth)) + 1"
	           y="0" width="object_width * height / 22" height="height"/>
	    */
	    object = topCenterMid;
	    if (topCenterMid == null) {
		object = topCenterMidLeft;
	    }
	    x = Math.min(gm.left_width + ButtonWidth + IconTitleSpacing + title_width,
			 width - object.getWidth(null) * height / 22 - gm.right_width
			    - 3 * ButtonWidth) + 1;
	    y = 0; w = object.getWidth(null) * height / 22; h = height;
	    g.drawImage(object, x, y, w, h, null);
	    if (topCenterMid == null) {
		object = topCenterMidRight;
		g.drawImage(object, x, y, w, h, null);
	    }

	    /*
	    <image filename="inactive-top-center-right.png"
	           x="((left_width + ButtonWidth + IconTitleSpacing + title_width
	                + CenterTitlePieceWidth * height / 22) `min`
	                (width - 3 * ButtonWidth - right_width)) + 1"
	           y="0"
	           width="(width - title_width - left_width - ButtonWidth - IconTitleSpacing
	                     - CenterTitlePieceWidth * height / 22 - right_width) `max`
	                   (3 * ButtonWidth)"
	           height="height"/>
	    */
	    object = topCenterRight;
	    x = Math.min(gm.left_width + ButtonWidth + IconTitleSpacing + title_width
			 + CenterTitlePieceWidth * height / 22,
			 width - 3 * ButtonWidth - gm.right_width) + 1;
	    y = 0;
	    w = Math.max(width - title_width - gm.left_width - ButtonWidth - IconTitleSpacing
			 - CenterTitlePieceWidth * height / 22 - gm.right_width,
			 3 * ButtonWidth);
	    h = height;
	    g.drawImage(object, x, y, w, h, null);

	} else {
	    /*
	    <image filename="active-left-top-border.png" 
	         colorize="gtk:bg[SELECTED]"
	         x="0" y="0" width="object_width" height="height"/>
	    */
	    object = getImage("active-left-top-border", getColor(SELECTED, GTKColorType.BACKGROUND));
	    x = 0; y = 0; w = object.getWidth(null); h = height;
	    g.drawImage(object, x, y, w, h, null);

	    /*
	    <image filename="active-right-top-border.png" 
	         x="width - object_width" y="0" width="object_width" height="height"/>
	    */
	    object = rightTopBorder;
	    x = width - object.getWidth(null); y = 0; w = object.getWidth(null); h = height;
	    g.drawImage(object, x, y, w, h, null);

	    /*
	    <image filename="active-top-center-left.png" 
	         colorize="gtk:bg[SELECTED]"
	         x="4" y="0" 
	         width="(left_width + ButtonWidth + IconTitleSpacing + title_width) `min`
	                (width - right_width - 3 * ButtonWidth
	                                   - CenterTitlePieceWidth * height / 22 - 3)" 
	         height="height"/>
	    */
	    object = getImage("active-top-center-left", getColor(SELECTED, GTKColorType.BACKGROUND));
	    x = 4; y = 0;
	    w = Math.min(gm.left_width + ButtonWidth + IconTitleSpacing + title_width,
			 width - gm.right_width - 3 * ButtonWidth
			 - CenterTitlePieceWidth * height / 22 - 3);
	    h = height;
	    g.drawImage(object, x, y, w, h, null);

	    /*
	    <image filename="active-top-center-mid-left.png" 
	         colorize="gtk:bg[SELECTED]"
	         x="((left_width + ButtonWidth + IconTitleSpacing + title_width) `min`
	              (width - object_width * height / 22 - right_width - 3 * ButtonWidth)) + 1"
	         y="0" width="object_width * height / 22" height="height"/>
	    */
	    object = getImage("active-top-center-mid-left", getColor(SELECTED, GTKColorType.BACKGROUND));
	    x = Math.min(gm.left_width + ButtonWidth + IconTitleSpacing + title_width,
			 width - object.getWidth(null) * height / 22 - gm.right_width - 3 * ButtonWidth) + 1;
	    y = 0; w = object.getWidth(null) * height / 22; h = height;
	    g.drawImage(object, x, y, w, h, null);

	    /*
	    <image filename="active-top-center-mid-right.png" 
	         x="((left_width + ButtonWidth + IconTitleSpacing + title_width) `min`
	               (width - object_width * height / 22 - right_width - 3 * ButtonWidth)) + 1"
	         y="0" width="object_width * height / 22" height="height"/>
	    */
	    object = topCenterMidRight;
	    x = Math.min(gm.left_width + ButtonWidth + IconTitleSpacing + title_width,
			 width - object.getWidth(null) * height / 22 - gm.right_width - 3 * ButtonWidth) + 1;
	    y = 0; w = object.getWidth(null) * height / 22; h = height;
	    g.drawImage(object, x, y, w, h, null);

	    /*
	    <image filename="active-top-center-right.png"
	         x="((left_width + ButtonWidth + IconTitleSpacing + title_width
	               + CenterTitlePieceWidth * height / 22) `min`
	             (width - 3 * ButtonWidth - right_width)) + 1"
	         y="0"
	         width="(width - title_width - left_width - ButtonWidth - IconTitleSpacing
	                     - CenterTitlePieceWidth * height / 22 - right_width) `max`
	                  (3 * ButtonWidth)"
	         height="height"/>
	    */
	    object = topCenterRight;
	    x = Math.min(gm.left_width + ButtonWidth + IconTitleSpacing + title_width
			 + CenterTitlePieceWidth * height / 22,
			 width - 3 * ButtonWidth - gm.right_width) + 1;
	    y = 0;
	    w = Math.max(width - title_width - gm.left_width - ButtonWidth - IconTitleSpacing
			 - CenterTitlePieceWidth * height / 22 - gm.right_width,
			 3 * ButtonWidth);
	    h = height;
	    g.drawImage(object, x, y, w, h, null);
	}

	Color textColor;

	if (!active) {
	    /*
	      <piece position="title">
		<draw_ops>
		  <title color="white" x="IconTitleSpacing" y="0"/>
		</draw_ops>
	      </piece>
	    */
	    textColor = Color.white;
	} else {
	    /*
	      <piece position="title">
		<draw_ops>
		  <title color="white" x="IconTitleSpacing" y="((height - title_height) / 2) `max` 0"/>
		</draw_ops>
	      </piece>
	    */
	    textColor = Color.white;
	}	
	String title = frame.getTitle();
        if (title != null) {
            // Center text vertically.
            int baseline = (height + fm.getAscent() - fm.getLeading() - fm.getDescent()) / 2;

	    int titleX;
	    if (frame.getComponentOrientation().isLeftToRight()) {
		title = getTitle(title, fm, title_width);
		titleX = gm.left_width + ButtonWidth + IconTitleSpacing;
	    } else {
		titleX = width - gm.right_width - ButtonWidth - 2 - SwingUtilities.computeStringWidth(fm, title);
	    }
	    g.setColor(textColor);
            g.drawString(title, titleX, baseline);
        }
    }

    private int calculateTitleWidth(JInternalFrame frame, JComponent titlePane, FontMetrics fm) {
	FrameGeometry gm = getFrameGeometry();
	String title = frame.getTitle();
	if (title != null) {
	    JComponent button = null;
	    if (frame.isIconifiable()) {
		button = findChild(titlePane, "InternalFrameTitlePane.iconifyButton");
	    } 
	    if (button == null && frame.isMaximizable()) {
		button = findChild(titlePane, "InternalFrameTitlePane.maximizeButton");
	    } 
	    if (button == null && frame.isClosable()) {
		button = findChild(titlePane, "InternalFrameTitlePane.closeButton");
	    }
	    int buttonX = (button != null) ? button.getX() : (titlePane.getWidth() - gm.right_titlebar_edge);
	    return Math.min(SwingUtilities.computeStringWidth(fm, title),
			    buttonX - (gm.left_width + ButtonWidth + IconTitleSpacing) - 3);
	}
	return 0;
    }

    private String getTitle(String text, FontMetrics fm, int availTextWidth) {
        if ((text == null) || (text.equals(""))) {
	    return "";
	}
        int textWidth = SwingUtilities.computeStringWidth(fm, text);
        String clipString = "...";
        if (textWidth > availTextWidth) {
            int totalWidth = SwingUtilities.computeStringWidth(fm, clipString);
            int nChars;
            for (nChars = 0; nChars < text.length(); nChars++) {
                totalWidth += fm.charWidth(text.charAt(nChars));
                if (totalWidth > availTextWidth) {
                    break;
                }
            }
            text = text.substring(0, nChars) + clipString;
        }
        return text;
      }


    void paintFrameBorder(SynthContext context, Graphics g, int x0, int y0, int width, int height) {
	this.context = context;
	JInternalFrame frame = (JInternalFrame)context.getComponent();
	JComponent titlePane = findChild(frame, "InternalFrame.northPane");

	//boolean active = ((context.getComponentState() & SynthConstants.SELECTED) != 0);
	boolean active = frame.isSelected();
	setFrameGeometry(titlePane, normalFrameGeometry);

	Image leftBorder        = getImage("left-border", active);
	Image rightBorder       = getImage("right-border", active);
	Image bottomLeftCorner  = getImage("bottom-left-corner", active);
	Image bottomLeftBorder  = getImage("bottom-left-border", active);
	Image bottomRightBorder = getImage("bottom-right-border", active);
	Image bottomRightCorner = getImage("bottom-right-corner", active);
	Image bottomMidBorder = getImage("bottom-mid-border", active);
	Image bottomMidLeftBorder  = getImage("bottom-mid-left-border", active);
	Image bottomMidRightBorder = getImage("bottom-mid-right-border", active);

	Font oldFont = g.getFont();
	g.setFont(titlePane.getFont());
	g.translate(x0, y0);

	paintTitleBar(g, width, titlePane.getHeight(), active, frame, titlePane);

	FontMetrics fm = g.getFontMetrics();
	int title_width = calculateTitleWidth(frame, titlePane, fm);
	int title_height = (titlePane != null) ? titlePane.getPreferredSize().height : 16;

	int x, y, w, h;
	Image object;

	if (leftBorder == null) {
	    // Ouch, we don't have any images. Let's just paint a border for now.
	    g.setColor(active ? Color.black : Color.gray);
	    g.drawRect(0, 0, width-1, height-1);
	} else if (!active) {
	    /*
	      <image filename="inactive-left-border.png" x="0" y="0" width="object_width" height="height"/>
	    */
	    object = leftBorder; x = 0; y = 0; w = object.getWidth(null); h = height;
	    // Adjust for our positioning system
	    y = title_height;
	    h = height - title_height - bottomLeftCorner.getHeight(null);
	    g.drawImage(object, x, y, w, h, null);

	    /*
	      <image filename="inactive-right-border.png" x="0" y="0" width="object_width" height="height"/>
	    */
	    object = rightBorder; x = 0; y = 0; w = object.getWidth(null); h = height;
	    // Adjust for our positioning system
	    x = width - rightBorder.getWidth(null);
	    y = title_height;
	    h = height - title_height - bottomRightCorner.getHeight(null);
	    g.drawImage(object, x, y, w, h, null);

	    /*
	      <image filename="inactive-bottom-left-corner.png" 
	         x="0" y="height - object_height" width="object_width" height="object_height"/>
	    */
	    object = bottomLeftCorner;
	    x = 0; y = height - object.getHeight(null);
	    w = object.getWidth(null); h = object.getHeight(null);
	    g.drawImage(object, x, y, w, h, null);

	    /*
	      <image filename="inactive-bottom-left-border.png" x="5" y="height - object_height" 
	         width="((title_width + height / 2 - 4) `min` (width - object_width - 26))"
	         height="object_height"/>
	    */
	    object = bottomLeftBorder; x = 5; y = height - object.getHeight(null);
	    w = Math.min((title_width + height / 2 - 4), (width - object.getWidth(null) - 26));
	    h = object.getHeight(null);
	    g.drawImage(object, x, y, w, h, null);

	    /*
	      <image filename="inactive-bottom-mid-border.png" 
	         x="((title_width + height / 2) `min` (width - object_width - 6)) + 1" 
	         y="height - object_height" width="object_width" height="object_height"/>
	    */
	    object = bottomMidBorder;
	    if (bottomMidBorder == null) {
		object = bottomMidLeftBorder;
	    }
	    x = Math.min((title_width + height / 2), (width - object.getWidth(null) - 6)) + 1;
	    y = height - object.getHeight(null);
	    w = object.getWidth(null); h = object.getHeight(null);
	    g.drawImage(object, x, y, w, h, null);
	    if (bottomMidBorder == null) {
		object = bottomMidRightBorder;
		g.drawImage(object, x, y, w, h, null);
	    }

	    /*
	      <image filename="inactive-bottom-right-border.png"
	         x="((title_width + height / 2 + 32)) + 1" 
	         y="height - object_height" 
	         width="(width - title_width - height / 2 - 32 - 7) `max` 0"
	         height="object_height"/>
	    */
	    object = bottomRightBorder;
	    x = (title_width + height / 2 + 32) + 1;
	    y = height - object.getHeight(null); 
	    w = Math.max((width - title_width - height / 2 - 32 - 7), 0);
	    h = object.getHeight(null);
	    g.drawImage(object, x, y, w, h, null);

	    /*
	      <image filename="inactive-bottom-right-corner.png" 
	         x="width - object_width" y="height - object_height"
	         width="object_height" height="object_height"/>
	    */
	    object = bottomRightCorner;
	    x = width - object.getWidth(null); y = height - object.getHeight(null);
	    w = object.getWidth(null); h = object.getHeight(null);
	    g.drawImage(object, x, y, w, h, null);
	} else {
	    /*
	      <image colorize="gtk:bg[SELECTED]" filename="active-left-border.png"
	         x="0" y="0" width="object_width" height="height"/>
	    */
	    object = getImage("active-left-border", getColor(SELECTED, GTKColorType.BACKGROUND));
	    x = 0; y = 0; w = object.getWidth(null); h = height;
	    // Adjust for our positioning system
	    y = title_height;
	    h = height - title_height - bottomLeftCorner.getHeight(null);
	    g.drawImage(object, x, y, w, h, null);


	    /*
	      <image filename="active-right-border.png"
	         x="0" y="0" width="object_width" height="height"/>
	    */
	    object = rightBorder; x = 0; y = 0; w = object.getWidth(null); h = height;
	    // Adjust for our positioning system
	    x = width - rightBorder.getWidth(null);
	    y = title_height;
	    h = height - title_height - bottomRightCorner.getHeight(null);
	    g.drawImage(object, x, y, w, h, null);

	    /*
	      <image filename="active-bottom-left-corner.png" 
	         colorize="gtk:bg[SELECTED]"
	         x="0" y="height - object_height" width="object_width" height="object_height"/>
	    */
	    object = getImage("active-bottom-left-corner", getColor(SELECTED, GTKColorType.BACKGROUND));
	    x = 0; y = height - object.getHeight(null);
	    w = object.getWidth(null); h = object.getHeight(null);
	    g.drawImage(object, x, y, w, h, null);

	    /*
	      <image filename="active-bottom-left-border.png" x="5" y="height - object_height" 
	         colorize="gtk:bg[SELECTED]"
	         width="((title_width + height / 2 - 4) `min` (width - object_width - 26))"
	         height="object_height"/>
	    */
	    object = getImage("active-bottom-left-border", getColor(SELECTED, GTKColorType.BACKGROUND));
	    x = 5; y = height - object.getHeight(null);
	    w = Math.min((title_width + height / 2 - 4), (width - object.getWidth(null) - 26));
	    h = object.getHeight(null);
	    g.drawImage(object, x, y, w, h, null);

	    /*
	      <image filename="active-bottom-mid-left-border.png" 
	         colorize="gtk:bg[SELECTED]"
	         x="((title_width + height / 2) `min` (width - object_width - 6)) + 1" 
	         y="height - object_height" width="object_width" height="object_height"/>
	    */
	    object = getImage("active-bottom-mid-left-border", getColor(SELECTED, GTKColorType.BACKGROUND));
	    x = Math.min((title_width + height / 2), (width - object.getWidth(null) - 6)) + 1;
	    y = height - object.getHeight(null); w = object.getWidth(null); h = object.getHeight(null);
	    g.drawImage(object, x, y, w, h, null);

	    /*
	      <image filename="active-bottom-mid-right-border.png" 
	         x="((title_width + height / 2) `min` (width - object_width - 6)) + 1" 
	         y="height - object_height" width="object_width" height="object_height"/>
	    */
	    object = bottomMidRightBorder;
	    x = Math.min((title_width + height / 2), (width - object.getWidth(null) - 6)) + 1;
	    y = height - object.getHeight(null); w = object.getWidth(null); h = object.getHeight(null);
	    g.drawImage(object, x, y, w, h, null);

	    /*
	      <image filename="active-bottom-right-border.png"
	         x="((title_width + height / 2 + 32)) + 1" 
	         y="height - object_height" 
	         width="(width - title_width - height / 2 - 32 - 7) `max` 0"
	         height="object_height"/>
	    */
	    object = bottomRightBorder;
	    x = (title_width + height / 2 + 32) + 1;
	    y = height - object.getHeight(null); 
	    w = Math.max((width - title_width - height / 2 - 32 - 7), 0);
	    h = object.getHeight(null);
	    g.drawImage(object, x, y, w, h, null);

	    /*
	      <image filename="active-bottom-right-corner.png" 
	         x="width - object_width" y="height - object_height"
	         width="object_height" height="object_height"/>
	    */
	    object = bottomRightCorner;
	    x = width - object.getWidth(null); y = height - object.getHeight(null);
	    w = object.getWidth(null); h = object.getHeight(null);
	    g.drawImage(object, x, y, w, h, null);
	}

	g.translate(-x0, -y0);
	g.setFont(oldFont);
    }

    Insets getBorderInsets(SynthContext context, Insets insets) {
	FrameGeometry gm = getFrameGeometry();
	if (insets == null) {
	    insets = new Insets(0, 0, 0, 0);
	}
	insets.top    = gm.title_border.top;
	insets.bottom = gm.bottom_height;
	insets.left   = gm.left_width;
	insets.right  = gm.right_width;
	return insets;
    }


    private HashMap images = new HashMap();

    private Image getImage(String key, Color c) {
	Image image = (Image)images.get(key+"-"+c.getRGB());
	if (image == null) {
	    image = colorizeImage(getImage(key), c);
	    if (image != null) {
		images.put(key+"-"+c.getRGB(), image);
	    }
	}
	return image;
    }

    private Image getImage(String key) {
	Image image = (Image)images.get(key);
	if (image == null) {
	    if (themeDir != null) {
		final String file = themeDir + File.separator + key + ".png";
		image = (Image)AccessController.doPrivileged(new PrivilegedAction() {
		    public Object run() {
			return new ImageIcon(file).getImage();
		    }
		});
	    } else {
		String filename = "resources/metacity/"+key+".png";
		URL url = getClass().getResource(filename);
		if  (url != null) {
		    image = new ImageIcon(url).getImage();
		}
	    }
	    if (image == null && key.startsWith("inactive")) {
		image = getImage(key.substring(2));
	    }
	    if (image != null) {
		images.put(key, image);
	    }
	}
	return image;
    }

    private Image getImage(String key, boolean active) {
	return getImage((active ? "active" : "inactive") + "-" + key);
    }
}


