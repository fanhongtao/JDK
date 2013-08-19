/*
 * @(#)MetacityBluecurve.java	1.5 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;


import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

/**
 * @version 1.5, 01/23/03
 */
class MetacityBluecurve extends Metacity {

    protected MetacityBluecurve(String themeDir) {
	super(themeDir, normalFrameGeometry);
    }


    // Constants from Bluecurve/metacity-theme-1.xml 2002-05-29 (use same variable names)
    /*
	<!-- define constants -->
	<constant name="ArrowWidth" value="7"/>
	<constant name="ArrowHeight" value="5"/>
	<constant name="ButtonIPad" value="3"/>
	<constant name="ThickLineWidth" value="3"/>
	<constant name="IconTitleSpacing" value="2"/>
	<constant name="LeftTitleTextPad" value="6"/>
	<constant name="IconShadowOffset" value="1"/>
    */
    private static final int ArrowWidth = 7;
    private static final int ArrowHeight = 5;
    private static final int ButtonIPad = 3;
    private static final int ThickLineWidth = 3;
    private static final int IconTitleSpacing = 2;
    private static final int LeftTitleTextPad = 6;
    private static final int IconShadowOffset = 1;

    /*
	<frame_geometry name="normal" rounded_top_left="true" rounded_top_right="true" rounded_bottom_left="true" rounded_bottom_right="true">
		<distance name="left_width" value="6"/>
		<distance name="right_width" value="6"/>
		<distance name="bottom_height" value="6"/>
		<distance name="left_titlebar_edge" value="0"/>
		<distance name="right_titlebar_edge" value="0"/>
		<distance name="title_vertical_pad" value="3"/>
		<border name="title_border" left="0" right="0" top="1" bottom="0"/>
		<border name="button_border" left="0" right="0" top="1" bottom="0"/>
		<aspect_ratio name="button" value="0.9"/>
	</frame_geometry>
    */
    private static class NormalFrameGeometry extends FrameGeometry {
	NormalFrameGeometry() {
	    left_width = 6;
	    right_width = 6;
	    bottom_height = 6;
	    left_titlebar_edge = 0;
	    right_titlebar_edge = 0;
	    title_vertical_pad = 3;
	    title_border = new Insets(1, 0, 0, 0);
	    button_border = new Insets(1, 0, 0, 0);
	    aspect_ratio = 0.9F;
	}
    };


    /*
	<!-- strip borders off the normal geometry -->
	<frame_geometry name="normal_maximized" parent="normal" rounded_top_left="false" rounded_top_right="false" rounded_bottom_left="false" rounded_bottom_right="false">
		<distance name="left_width" value="0"/>
		<distance name="right_width" value="0"/>
		<distance name="bottom_height" value="0"/>
		<distance name="left_titlebar_edge" value="0"/>
		<distance name="right_titlebar_edge" value="0"/>
	</frame_geometry>
    */
    private static class NormalMaximizedFrameGeometry extends NormalFrameGeometry {
	NormalMaximizedFrameGeometry() {
	    left_width = 0;
	    right_width = 0;
	    bottom_height = 0;
	    left_titlebar_edge = 0;
	    right_titlebar_edge = 0;
	}
    };

    private static final FrameGeometry normalFrameGeometry = new NormalFrameGeometry();
    private static final FrameGeometry normalMaximizedFrameGeometry = new NormalMaximizedFrameGeometry();



    // draw_ops

    private void titlebar_bg_unfocused(Graphics g, int width, int height) {
	/*
	  <gradient type="vertical" x="0" y="0" width="width" height="height">
		<color value="shade/gtk:bg[INSENSITIVE]/1.0"/>
		<color value="shade/gtk:bg[INSENSITIVE]/0.9"/>
	  </gradient>
	*/
	drawVerticalGradient(g,
			     shadeColor(getColor(DISABLED, GTKColorType.BACKGROUND), 1.0F),
			     shadeColor(getColor(DISABLED, GTKColorType.BACKGROUND), 0.9F),
			     0, 0, width, height);

	/*
	  <line color="shade/gtk:bg[INSENSITIVE]/0.6" x1="0" y1="height-1" x2="width" y2="height-1"/>
	*/
	g.setColor(shadeColor(getColor(DISABLED, GTKColorType.BACKGROUND), 0.6F));
	g.drawLine(0, height-1, width, height-1);

	/*
	  <!-- Highlight on top edge -->
	  <line color="shade/gtk:bg[INSENSITIVE]/1.2" x1="0" y1="0" x2="width" y2="0"/>
	*/
	g.setColor(shadeColor(getColor(DISABLED, GTKColorType.BACKGROUND), 1.2F));
	g.drawLine(0, 0, width, 0);
    }

    /*
	<!-- Buttons -->
    */

    private void button_generic_bg_before(Graphics g, int width, int height) {
	/*
		<!-- gradient from slightly darker than normal to slightly lighter --> 
		<gradient type="diagonal" x="0" y="1" width="width" height="height">
			<color value="shade/gtk:bg[NORMAL]/1.3"/>
			<color value="shade/gtk:bg[NORMAL]/0.9"/>
		</gradient>
		<gradient type="diagonal" x="1" y="1" width="width-2" height="height-2">
			<color value="shade/gtk:bg[NORMAL]/0.9"/>
			<color value="shade/gtk:bg[NORMAL]/1.3"/>
		</gradient>
	*/
	drawDiagonalGradient(g,
			     shadeColor(getColor(ENABLED, GTKColorType.BACKGROUND), 1.3F),
			     shadeColor(getColor(ENABLED, GTKColorType.BACKGROUND), 0.9F),
			     0, 1, width, height);
	drawDiagonalGradient(g,
			     shadeColor(getColor(ENABLED, GTKColorType.BACKGROUND), 0.9F),
			     shadeColor(getColor(ENABLED, GTKColorType.BACKGROUND), 1.3F),
			     1, 1, width-2, height-2);
    }

    private void button_generic_bg_before_unfocused(Graphics g, int width, int height) {
	/*
		<include name="titlebar_bg_unfocused"/>
		<gradient type="vertical" x="0" y="1" width="width" height="height-2" alpha="0.1">
			<color value="shade/gtk:bg[NORMAL]/1.5"/>
			<color value="shade/gtk:bg[NORMAL]/0.95"/>
		</gradient>
	*/
	titlebar_bg_unfocused(g, width, height);
	drawVerticalGradient(g,
			     shadeColor(getColor(ENABLED, GTKColorType.BACKGROUND), 1.5F),
			     shadeColor(getColor(ENABLED, GTKColorType.BACKGROUND), 0.95F),
			     0, 1, width, height-2, 0.1F);
    }

    private void button_generic_bg_after(Graphics g, int width, int height) {
	/*
		<!-- line on bottom edge -->
		<line color="shade/gtk:dark[NORMAL]/0.9" x1="0" y1="height-1" x2="width" y2="height-1"/>
	*/
	g.setColor(shadeColor(getColor(ENABLED, GTKColorType.DARK), 0.9F));
	g.drawLine(0, height-1, width, height-1);
    }

    private void center_button_bg(Graphics g, int width, int height) {
	/*
		<include name="button_generic_bg_before"/>
		<include name="button_generic_bg_after"/>
		<!-- highlight on top edge -->
		<line color="shade/gtk:bg[NORMAL]/1.2" x1="0" y1="0" x2="width" y2="0"/>
		<!-- dark line to separate from other buttons -->
		<line color="gtk:dark[NORMAL]" x1="width-1" y1="0" x2="width-1" y2="height"/>
	*/
	button_generic_bg_before(g, width, height);
	button_generic_bg_after(g, width, height);
	g.setColor(shadeColor(getColor(ENABLED, GTKColorType.BACKGROUND), 1.2F));
	g.drawLine(0, 0, width, 0);
	g.setColor(getColor(ENABLED, GTKColorType.DARK));
	g.drawLine(width-1, 0, width-1, height);

    }

    private void center_button_bg_unfocused(Graphics g, int width, int height) {
	/*
		<include name="button_generic_bg_before_unfocused"/>
	*/
	button_generic_bg_before_unfocused(g, width, height);
    }

    private void right_corner_outline(Graphics g, int width, int height) {
	/*
	  <draw_ops name="right_corner_outline">
		<!-- fix up the black edging -->
		<line color="#000000" x1="width-1" y1="0" x2="width-1" y2="height"/>
		<line color="#000000" x1="width-5" y1="0" x2="width-4" y2="0"/>
		<line color="#000000" x1="width-3" y1="1" x2="width-2" y2="1"/>
		<line color="#000000" x1="width-2" y1="2" x2="width-2" y2="3"/>
	  </draw_ops>
	*/
	g.setColor(Color.black);
	// Adjusted because button isn't clipped
 	g.drawLine(width-1, 4, width-1, height);
	g.drawLine(width-5, 0, width-4, 0);
	g.drawLine(width-3, 1, width-3, 1);
	g.drawLine(width-2, 2, width-2, 3);
    }

    private void right_corner_button_bg(Graphics g, int width, int height) {
	/*
		<include name="button_generic_bg_before"/>
		<include name="button_generic_bg_after"/>
		<include name="right_corner_outline"/>
		<!-- highlight on top edge -->
		<line color="shade/gtk:bg[NORMAL]/1.2" x1="0" y1="0" x2="width-6" y2="0"/>
		<!-- do some shading around the edges -->
		<line color="shade/gtk:bg[NORMAL]/1.1" x1="width-4" y1="1" x2="width-5" y2="1"/>
		<line color="shade/gtk:bg[NORMAL]/0.9" x1="width-3" y1="2" x2="width-3" y2="3"/>
		<line color="shade/gtk:bg[NORMAL]/0.8" x1="width-2" y1="4" x2="width-2" y2="height-2"/>
	*/
	button_generic_bg_before(g, width, height);
	button_generic_bg_after(g, width, height);
	right_corner_outline(g, width, height);
	g.setColor(shadeColor(getColor(ENABLED, GTKColorType.BACKGROUND), 1.2F));
	g.drawLine(0, 0, width-6, 0);
	g.setColor(shadeColor(getColor(ENABLED, GTKColorType.BACKGROUND), 1.1F));
	g.drawLine(width-4, 1, width-5, 1);
	g.setColor(shadeColor(getColor(ENABLED, GTKColorType.BACKGROUND), 0.9F));
	g.drawLine(width-3, 2, width-3, 3);
	g.setColor(shadeColor(getColor(ENABLED, GTKColorType.BACKGROUND), 0.8F));
	g.drawLine(width-2, 4, width-2, height-2);
    }

    private void right_corner_button_bg_unfocused(Graphics g, int width, int height) {
	/*
		<include name="button_generic_bg_before_unfocused"/>
		<include name="right_corner_outline"/>
		<!-- highlight on top edge -->
		<line color="shade/gtk:bg[INSENSITIVE]/1.2" x1="0" y1="0" x2="width-6" y2="0"/>
		<!-- do some shading around the edges -->
		<line color="shade/gtk:bg[INSENSITIVE]/1.1" x1="width-4" y1="1" x2="width-5" y2="1"/>
		<line color="shade/gtk:bg[INSENSITIVE]/0.9" x1="width-3" y1="2" x2="width-3" y2="3"/>
		<line color="shade/gtk:bg[INSENSITIVE]/0.8" x1="width-2" y1="4" x2="width-2" y2="height-2"/>
	*/
	button_generic_bg_before_unfocused(g, width, height);
	right_corner_outline(g, width, height);
	g.setColor(shadeColor(getColor(DISABLED, GTKColorType.BACKGROUND), 1.2F));
	g.drawLine(0, 0, width-6, 0);
	g.setColor(shadeColor(getColor(DISABLED, GTKColorType.BACKGROUND), 1.1F));
	g.drawLine(width-4, 1, width-5, 1);
	g.setColor(shadeColor(getColor(DISABLED, GTKColorType.BACKGROUND), 0.9F));
	g.drawLine(width-3, 2, width-3, 3);
	g.setColor(shadeColor(getColor(DISABLED, GTKColorType.BACKGROUND), 0.8F));
	g.drawLine(width-2, 4, width-2, height-2);
    }

    private void left_corner_outline(Graphics g, int width, int height) {
	/*
	  <draw_ops name="left_corner_outline">
		  <!-- fix up the black edging -->
		  <line color="#000000" x1="0" y1="0" x2="0" y2="height"/>
		  <line color="#000000" x1="1" y1="2" x2="1" y2="3"/>
		  <line color="#000000" x1="2" y1="1" x2="2" y2="1"/>
		  <line color="#000000" x1="3" y1="0" x2="4" y2="0"/>
	  </draw_ops>
	*/
	g.setColor(Color.black);
	// adjusted because button isn't clipped
 	g.drawLine(0, 4, 0, height);
	g.drawLine(1, 2, 1, 3);
	g.drawLine(2, 1, 2, 1);
	g.drawLine(3, 0, 4, 0);
    }

    private void left_corner_button_bg(Graphics g, int width, int height) {
	/*
		<include name="button_generic_bg_before"/>
		<include name="button_generic_bg_after"/>
		<include name="left_corner_outline"/>
		<!-- highlight on top edge -->
		<line color="shade/gtk:bg[NORMAL]/1.2" x1="5" y1="0" x2="width" y2="0"/>
		<!-- shading around the edges -->
		<line color="shade/gtk:light[NORMAL]/1.2" x1="1" y1="4" x2="1" y2="height-2"/>
		<line color="shade/gtk:light[NORMAL]/1.1" x1="3" y1="1" x2="4" y2="1"/>
		<line color="shade/gtk:light[NORMAL]/1.1" x1="2" y1="2" x2="2" y2="3"/>
	*/
	button_generic_bg_before(g, width, height);
	button_generic_bg_after(g, width, height);
	left_corner_outline(g, width, height);
	g.setColor(shadeColor(getColor(ENABLED, GTKColorType.BACKGROUND), 1.2F));
	g.drawLine(5, 0, width, 0);
	g.setColor(shadeColor(getColor(ENABLED, GTKColorType.LIGHT), 1.2F));
	g.drawLine(1, 4, 1, height-2);
	g.setColor(shadeColor(getColor(ENABLED, GTKColorType.LIGHT), 1.1F));
	g.drawLine(3, 1, 4, 1);
	g.drawLine(2, 2, 2, 3);
    }


    private void left_corner_button_bg_unfocused(Graphics g, int width, int height) {
	/*
		<include name="button_generic_bg_before_unfocused"/>
		<include name="left_corner_outline"/>
		<!-- highlight on top edge -->
		<line color="shade/gtk:bg[INSENSITIVE]/1.2" x1="5" y1="0" x2="width" y2="0"/>
		<!-- shading around the edges -->
		<line color="shade/gtk:light[INSENSITIVE]/1.2" x1="1" y1="4" x2="1" y2="height-2"/>
		<line color="shade/gtk:light[INSENSITIVE]/1.1" x1="3" y1="1" x2="4" y2="1"/>
		<line color="shade/gtk:light[INSENSITIVE]/1.1" x1="2" y1="2" x2="2" y2="3"/>
	*/
	button_generic_bg_before_unfocused(g, width, height);
	left_corner_outline(g, width, height);
	g.setColor(shadeColor(getColor(DISABLED, GTKColorType.BACKGROUND), 1.2F));
	g.drawLine(5, 0, width, 0);
	g.setColor(shadeColor(getColor(DISABLED, GTKColorType.LIGHT), 1.2F));
	g.drawLine(1, 4, 1, height-2);
	g.setColor(shadeColor(getColor(DISABLED, GTKColorType.LIGHT), 1.1F));
	g.drawLine(3, 1, 4, 1);
	g.drawLine(2, 2, 2, 3);
    }

    private void darken_tint(Graphics g, int width, int height) {
	/*
		<tint color="shade/gtk:bg[normal]/0.75" alpha="0.5" 
			x="0" y="0" width="width" height="height"/>
	*/
	tintRect(g, 0, 0, width, height,
		 shadeColor(getColor(ENABLED, GTKColorType.BACKGROUND), 0.75F),
		 0.5F);
    }


    private void prelight_tint(Graphics g, int width, int height) {
	/*
		<tint color="gtk:bg[PRELIGHT]" alpha="0.4"
			x="1" y="1" width="width-2" height="height-2"/>
	*/
	tintRect(g, 1, 1, width-2, height-2, getColor(MOUSE_OVER, GTKColorType.BACKGROUND), 0.4F);
    }


    private void minimize_icon(Graphics g, int width, int height) {
	/*
		<image filename="minimize.png"
			colorize="gtk:fg[NORMAL]"
			alpha="0.7"
			x="(width - object_width) / 2"
			y="(height - object_height) / 2"
			width="object_width"
			height="object_height"/>
	*/
	Image object = getImage("minimize", getColor(ENABLED, GTKColorType.FOREGROUND));
	drawImage(g, object,
		  getColor(ENABLED, GTKColorType.FOREGROUND),
		  0.7F,
		  (width - object.getWidth(null)) / 2,
		  (height - object.getHeight(null)) / 2);
    }


    private void menu_icon(Graphics g, int width, int height) {
	/*
		<image filename="menu.png"
			colorize="gtk:fg[NORMAL]"
			alpha="0.7"
			x="(width - object_width - 1) / 2"
			y="(height - object_height) / 2"
			width="object_width"
			height="object_height"/>
	*/
	Image object = getImage("menu", getColor(ENABLED, GTKColorType.FOREGROUND));
	drawImage(g, object,
		  getColor(ENABLED, GTKColorType.FOREGROUND),
		  0.7F,
		  (width - object.getWidth(null) - 1) / 2,
		  (height - object.getHeight(null)) / 2);
    }

    private void maximize_icon(Graphics g, int width, int height) {
	/*
		<image filename="maximize.png"
			colorize="gtk:fg[NORMAL]"
			alpha="0.7"
			x="(width - object_width) / 2"
			y="(height - object_height) / 2"
			width="object_width"
			height="object_height"/>
	*/
	Image object = getImage("maximize", getColor(ENABLED, GTKColorType.FOREGROUND));
	drawImage(g, object,
		  getColor(ENABLED, GTKColorType.FOREGROUND),
		  0.7F,
		  (width - object.getWidth(null)) / 2,
		  (height - object.getHeight(null)) / 2);
    }

    private void close_icon(Graphics g, int width, int height) {
	/*
		<image filename="close.png"
			colorize="gtk:fg[NORMAL]"
			alpha="0.7"
			x="(width - object_width) / 2"
			y="(height - object_height) / 2"
			width="object_width"
			height="object_height"/>
	*/
	Image object = getImage("close", getColor(ENABLED, GTKColorType.FOREGROUND));
	drawImage(g, object,
		  getColor(ENABLED, GTKColorType.FOREGROUND),
		  0.7F,
		  (width - object.getWidth(null)) / 2,
		  (height - object.getHeight(null)) / 2);
    }



    private void close_button(Graphics g, int width, int height) {
	/*
	  <include name="right_corner_button_bg"/>
	  <include name="close_icon"/>
	*/
	right_corner_button_bg(g, width, height);
	close_icon(g, width, height);
    }

    private void close_button_prelight(Graphics g, int width, int height) {
	/*
	  <include name="right_corner_button_bg"/>
	  <include name="prelight_tint"/>
	  <include name="close_icon"/>
	  <include name="close_icon"/>
	  <include name="right_corner_outline"/>
	*/
	right_corner_button_bg(g, width, height);
	prelight_tint(g, width, height);
	close_icon(g, width, height);
	right_corner_outline(g, width, height);
    }

    private void close_button_pressed(Graphics g, int width, int height) {
	/*
	  <include name="right_corner_button_bg"/>
	  <include name="darken_tint"/>
	  <include name="close_icon"/>
	*/
	right_corner_button_bg(g, width, height);
	darken_tint(g, width, height);
	close_icon(g, width, height);
    }

    private void menu_button(Graphics g, int width, int height) {
	/*
	  <include name="left_corner_button_bg"/>
	  <include name="menu_icon" x="2"/>
	*/
	left_corner_button_bg(g, width, height);
	g.translate(2, 0);
	menu_icon(g, width, height);
	g.translate(-2, 0);
    }

    private void menu_button_prelight(Graphics g, int width, int height) {
	/*
	  <include name="left_corner_button_bg"/>
	  <include name="prelight_tint"/>
	  <include name="menu_icon" x="2"/>
	  <include name="menu_icon" x="2"/>
	  <include name="left_corner_outline"/>
	*/
	left_corner_button_bg(g, width, height);
	prelight_tint(g, width, height);
	g.translate(2, 0);
	menu_icon(g, width, height);
	g.translate(-2, 0);
	left_corner_outline(g, width, height);
    }

    private void menu_button_pressed(Graphics g, int width, int height) {
	/*
	  <include name="left_corner_button_bg"/>
	  <include name="darken_tint"/>
	  <include name="menu_icon" x="2"/>
	*/
	left_corner_button_bg(g, width, height);
	darken_tint(g, width, height);
	g.translate(2, 0);
	menu_icon(g, width, height);
	g.translate(-2, 0);
    }

    private void minimize_button(Graphics g, int width, int height, boolean corner) {
	/*
	  <include name="center_button_bg"/>
	  <include name="minimize_icon"/>
	*/
	if (corner) {
	    right_corner_button_bg(g, width, height);
	} else {
	    center_button_bg(g, width, height);
	}
	minimize_icon(g, width, height);
    }

    private void minimize_button_prelight(Graphics g, int width, int height, boolean corner) {
	/*
	  <include name="center_button_bg"/>
	  <include name="prelight_tint"/>
	  <include name="minimize_icon"/>
	  <include name="minimize_icon"/>
	*/
	if (corner) {
	    right_corner_button_bg(g, width, height);
	} else {
	    center_button_bg(g, width, height);
	}
	prelight_tint(g, width, height);
	minimize_icon(g, width, height);
    }


    private void minimize_button_pressed(Graphics g, int width, int height, boolean corner) {
	/*
	  <include name="center_button_bg"/>
	  <include name="darken_tint"/>
	  <include name="minimize_icon"/>
	*/
	if (corner) {
	    right_corner_button_bg(g, width, height);
	} else {
	    center_button_bg(g, width, height);
	}
	darken_tint(g, width, height);
	minimize_icon(g, width, height);
    }

    private void maximize_button(Graphics g, int width, int height, boolean corner) {
	/*
	  <include name="center_button_bg"/>
	  <include name="maximize_icon"/>
	*/
	if (corner) {
	    right_corner_button_bg(g, width, height);
	} else {
	    center_button_bg(g, width, height);
	}
	maximize_icon(g, width, height);
    }

    private void maximize_button_prelight(Graphics g, int width, int height, boolean corner) {
	/*
	  <include name="center_button_bg"/>
	  <include name="prelight_tint"/>
	  <include name="maximize_icon"/>
	  <include name="maximize_icon"/>
	*/
	if (corner) {
	    right_corner_button_bg(g, width, height);
	} else {
	    center_button_bg(g, width, height);
	}
	prelight_tint(g, width, height);
	maximize_icon(g, width, height);
    }

    private void maximize_button_pressed(Graphics g, int width, int height, boolean corner) {
	/*
	  <include name="center_button_bg"/>
	  <include name="darken_tint"/>
	  <include name="maximize_icon"/>
	*/
	if (corner) {
	    right_corner_button_bg(g, width, height);
	} else {
	    center_button_bg(g, width, height);
	}
	darken_tint(g, width, height);
	maximize_icon(g, width, height);
    }


    void paintButtonBackground(SynthContext context, Graphics g, int x, int y, int w, int h) {
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
	setFrameGeometry(titlePane, frame.isMaximum() ? normalMaximizedFrameGeometry
						      : normalFrameGeometry);

	if (buttonName.equals("InternalFrameTitlePane.menuButton")) {
	    if ((buttonState & PRESSED) != 0) {
		menu_button_pressed(g, w, h);
	    } else if ((buttonState & MOUSE_OVER) != 0) {
		menu_button_prelight(g, w, h);
	    } else {
		menu_button(g, w, h);
	    }
	} else if (buttonName.equals("InternalFrameTitlePane.iconifyButton")) {
	    boolean corner = !(frame.isClosable() || frame.isMaximizable());
	    if ((buttonState & PRESSED) != 0) {
		minimize_button_pressed(g, w, h, corner);
	    } else if ((buttonState & MOUSE_OVER) != 0) {
		minimize_button_prelight(g, w, h, corner);
	    } else {
		minimize_button(g, w, h, corner);
	    }
	} else if (buttonName.equals("InternalFrameTitlePane.maximizeButton")) {
	    boolean corner = !frame.isClosable();
	    if ((buttonState & PRESSED) != 0) {
		maximize_button_pressed(g, w, h, corner);
	    } else if ((buttonState & MOUSE_OVER) != 0) {
		maximize_button_prelight(g, w, h, corner);
	    } else {
		maximize_button(g, w, h, corner);
	    }
	} else if (buttonName.equals("InternalFrameTitlePane.closeButton")) {
	    if ((buttonState & PRESSED) != 0) {
		close_button_pressed(g, w, h);
	    } else if ((buttonState & MOUSE_OVER) != 0) {
		close_button_prelight(g, w, h);
	    } else {
		close_button(g, w, h);
	    }
	}
    }


    private void outer_bevel(Graphics g, int width, int height) {
	FrameGeometry gm = getFrameGeometry();

	/*
	  <draw_ops name="outer_bevel">
		<!-- black edging -->
		<rectangle color="#000000" x="0" y="0" width="width-1" height="height-1"/>
	*/
	g.setColor(Color.black);
	//g.drawRect(0, 0, width-1, height-1);
	//Adjusted because we have no clipping set
	g.drawLine(5,       0,        width-6, 0);
	g.drawLine(5,       height-1, width-6, height-1);
	g.drawLine(0,       5,        0,       height-6);
	g.drawLine(width-1, 5,        width-1, height-6);

	/*
		<!-- left outside -->
		<line color="blend/gtk:light[NORMAL]/#FFFFFF/0.7" x1="1" y1="1" x2="1" y2="height-2"/>
	*/
	g.setColor(blendColor(getColor(ENABLED, GTKColorType.LIGHT), Color.white, 0.7F));
	//g.drawLine(1, 1, 1, height-2);
	g.drawLine(1, 3, 1, height-4);

	/*
		<!-- left inside -->
		<line color="gtk:dark[NORMAL]"
			x1="left_width-1" y1="top_height-1" x2="left_width-1" y2="height-bottom_height"/>
	*/
	g.setColor(getColor(ENABLED, GTKColorType.DARK));
	g.drawLine(gm.left_width-1, gm.top_height-1, gm.left_width-1, height-gm.bottom_height);

	/*
		<!-- outside of bottom -->
		<line color="blend/gtk:bg[NORMAL]/#000000/0.2" x1="2" y1="height-2" x2="width-2" y2="height-2"/>
	*/
	g.setColor(blendColor(getColor(ENABLED, GTKColorType.BACKGROUND), Color.black, 0.2F));
	//g.drawLine(2, height-2, width-2, height-2);
	g.drawLine(3, height-2, width-4, height-2);

	/*
		<!-- inside of bottom (in two lines, one dark one light) -->
		<line color="gtk:dark[NORMAL]"
			x1="left_width" y1="height-bottom_height" x2="width-right_width" y2="height-bottom_height"/>
		<line color="blend/gtk:light[NORMAL]/#FFFFFF/0.7"
			x1="left_width" y1="height-bottom_height+1" x2="width-right_width" y2="height-bottom_height+1"/>
	*/
	g.setColor(getColor(ENABLED, GTKColorType.DARK));
	g.drawLine(gm.left_width, height-gm.bottom_height, width-gm.right_width, height-gm.bottom_height);
	g.setColor(blendColor(getColor(ENABLED, GTKColorType.LIGHT), Color.white, 0.7F));
	g.drawLine(gm.left_width, height-gm.bottom_height+1, width-gm.right_width, height-gm.bottom_height+1);

	/*
		<!-- right inside, again in two lines -->
		<line color="gtk:dark[NORMAL]"
			x1="width-right_width" y1="top_height-1" x2="width-right_width" y2="height-bottom_height"/>
		<line color="blend/gtk:light[NORMAL]/#FFFFFF/0.7"
			x1="width-right_width+1" y1="top_height-1" x2="width-right_width+1" y2="height-bottom_height+1"/>
	*/
	g.setColor(getColor(ENABLED, GTKColorType.DARK));
	g.drawLine(width-gm.right_width,   gm.top_height-1, width-gm.right_width,   height-gm.bottom_height);
	g.setColor(blendColor(getColor(ENABLED, GTKColorType.LIGHT), Color.white, 0.7F));
	g.drawLine(width-gm.right_width+1, gm.top_height-1, width-gm.right_width+1, height-gm.bottom_height+1);

	/*
		<!-- right outside -->
		<line color="blend/gtk:bg[NORMAL]/#000000/0.2" x1="width-2" y1="top_height-1" x2="width-2" y2="height-2"/>
	    </draw_ops>
	*/
	g.setColor(blendColor(getColor(ENABLED, GTKColorType.BACKGROUND), Color.black, 0.2F));
	//g.drawLine(width-2, top_height-1, width-2, height-2);
	g.drawLine(width-2, 3, width-2, height-4);
    }	

    private void corners_unfocused(Graphics g, int width, int height) {
	Image object;

	/*
	  <!-- corners (unfocused) -->
	  <image filename="bottom_left.png" colorize="shade/gtk:bg[INSENSITIVE]/0.8" x="0" y="height-object_height" width="object_width" height="object_height"/> 
	  <image filename="bottom_right.png" colorize="shade/gtk:bg[INSENSITIVE]/0.8" x="width-object_width" y="height-object_height" width="object_width" height="object_height"/>
	*/
	Color c = shadeColor(getColor(DISABLED, GTKColorType.BACKGROUND), 0.8F);
	object = getImage("bottom_left", c);
	g.drawImage(object, 0, height-object.getHeight(null), null);
	object = getImage("bottom_right", c);
	g.drawImage(object, width-object.getWidth(null), height-object.getHeight(null), null);
    }

    private void corners_focused(Graphics g, int width, int height) {
	Image object;

	/*
	  <!-- corners -->
	  <image filename="bottom_left.png" colorize="shade/gtk:bg[SELECTED]/1.0" x="0" y="height-object_height" width="object_width" height="object_height"/> 
	  <image filename="bottom_right.png" colorize="shade/gtk:bg[SELECTED]/1.0" x="width-object_width" y="height-object_height" width="object_width" height="object_height"/>
	*/
	Color c = shadeColor(getColor(SELECTED, GTKColorType.BACKGROUND), 1.0F);
	object = getImage("bottom_left", c);
	g.drawImage(object, 0, height-object.getHeight(null), null);
	object = getImage("bottom_right", c);
	g.drawImage(object, width-object.getWidth(null), height-object.getHeight(null), null);
    }

    private void unfocus_background(Graphics g, int width, int height) {
	/*
	  <include name="outer_bevel"/>
	  <include name="corners_unfocused"/>
	*/
	outer_bevel(g, width, height);
	corners_unfocused(g, width, height);
    }

    private void focus_background(Graphics g, int width, int height) {
	/*
	  <include name="outer_bevel"/>
	  <include name="corners_focused"/>
	*/
	outer_bevel(g, width, height);
	corners_focused(g, width, height);
    }

    private void title_gradient(Graphics g, int width, int height, JInternalFrame frame) {
	Image object;
	int title_width = calculateTitleWidth(g, frame);

	/*
		<!-- Normally, there would be buttons placed over the outlines. This is to handle windows w/o buttons -->
		<include name="left_corner_outline"/>
		<include name="right_corner_outline"/>
	*/
	left_corner_outline(g, width, height);
	right_corner_outline(g, width, height);

	/*
		<!-- base vertical gradient -->
		<gradient type="vertical" x="0" y="0" width="width" height="height-1">
			<color value="shade/gtk:bg[SELECTED]/1.4"/>
			<color value="shade/gtk:bg[SELECTED]/1.0"/>
			<color value="shade/gtk:bg[SELECTED]/0.8"/>
		</gradient>
	*/
	drawVerticalGradient(g,
			     shadeColor(getColor(SELECTED, GTKColorType.BACKGROUND), 1.4F),
			     shadeColor(getColor(SELECTED, GTKColorType.BACKGROUND), 1.0F),
			     shadeColor(getColor(SELECTED, GTKColorType.BACKGROUND), 0.8F),
			     0, 0, width, height-1);

	/*
		<!-- stripes -->
		<image filename="white_stripes_tile.png" fill_type="tile" 
			x="(LeftTitleTextPad+title_width-40) `max` 1" 
			y="1+1" width="width-(LeftTitleTextPad+title_width-40)-2" height="height-4"
			alpha="0.0:0.3:0.4:0.4"/>
	*/
	object = getImage("white_stripes_tile");
	tileImage(g, object,
		  Math.max(LeftTitleTextPad+title_width-40, 1), 1+1,
		  width-(LeftTitleTextPad+title_width-40)-2, height-4,
		  new float[] { 0.0F, 0.3F, 0.4F, 0.4F });

	/*
		<!-- top title shine -->
		<gradient type="diagonal" x="0" y="0" width="width" height="2" alpha="0.2">
			<color value="shade/gtk:bg[SELECTED]/2.0"/>
			<color value="shade/gtk:bg[SELECTED]/1.7"/>
		</gradient>
		<gradient type="vertical" x="0" y="0" width="width" height="1" alpha="0.4">
			<color value="shade/gtk:bg[SELECTED]/2.0"/>
			<color value="shade/gtk:bg[SELECTED]/1.4"/>
		</gradient>
	*/
	drawDiagonalGradient(g,
			     shadeColor(getColor(SELECTED, GTKColorType.BACKGROUND), 2.0F),
			     shadeColor(getColor(SELECTED, GTKColorType.BACKGROUND), 1.7F),
			     0, 0, width, 2, 0.2F);
	drawVerticalGradient(g,
			     shadeColor(getColor(SELECTED, GTKColorType.BACKGROUND), 2.0F),
			     shadeColor(getColor(SELECTED, GTKColorType.BACKGROUND), 1.4F),
			     0, 0, width, 1, 0.4F);

	/*
		<!-- bottom title darken -->
		<gradient type="diagonal" x="0" y="height-2" width="width" height="2" alpha="0.2">
			<color value="shade/gtk:bg[SELECTED]/0.8"/>
			<color value="shade/gtk:bg[SELECTED]/0.5"/>
		</gradient>
		<gradient type="vertical" x="0" y="height-2" width="width" height="1" alpha="0.9">
			<color value="shade/gtk:bg[SELECTED]/1.0"/>
			<color value="shade/gtk:bg[SELECTED]/0.7"/>
		</gradient>
	*/
	drawDiagonalGradient(g,
			     shadeColor(getColor(SELECTED, GTKColorType.BACKGROUND), 0.8F),
			     shadeColor(getColor(SELECTED, GTKColorType.BACKGROUND), 0.5F),
			     0, height-2, width, 2, 0.2F);
	drawVerticalGradient(g,
			     shadeColor(getColor(SELECTED, GTKColorType.BACKGROUND), 1.0F),
			     shadeColor(getColor(SELECTED, GTKColorType.BACKGROUND), 0.7F),
			     0, height-2, width, 1, 0.9F);

	/*
		<!-- bottom seperator line -->
		<line color="shade/gtk:bg[SELECTED]/0.1" x1="0" y1="height-1" x2="width" y2="height-1"/>
	*/
	g.setColor(shadeColor(getColor(SELECTED, GTKColorType.BACKGROUND), 0.1F));
	g.drawLine(0, height-1, width, height-1);
    }

    private void title_text_focused_no_icon(Graphics g, int width, int height, JInternalFrame frame) {
	FontMetrics fm = g.getFontMetrics();
	int title_height = fm.getAscent() + fm.getDescent();

	/*
		<clip x="0" y="0" width="width-8" height="height"/>
		<title color="blend/gtk:fg[NORMAL]/gtk:bg[SELECTED]/0.7"
			x="LeftTitleTextPad+1"
			y="(((height - title_height) / 2) `max` 0)+1"/>
		<title color="gtk:fg[SELECTED]"
			x="LeftTitleTextPad"
			y="((height - title_height) / 2) `max` 0"/>
	*/
	Rectangle clipRect = g.getClipBounds();
	g.setClip(0, 0, width-8, height);
	g.setColor(blendColor(getColor(ENABLED, GTKColorType.TEXT_FOREGROUND),
			      getColor(SELECTED, GTKColorType.BACKGROUND), 0.7F));
	paintTitle(g, LeftTitleTextPad+1, Math.max((height - title_height) / 2, 0)+1, frame);
	g.setColor(getColor(SELECTED, ColorType.TEXT_FOREGROUND));
	paintTitle(g, LeftTitleTextPad, Math.max((height - title_height) / 2, 0), frame);
	g.setClip(clipRect);
    }

    private void title_text_no_icon(Graphics g, int width, int height, JInternalFrame frame) {
	FontMetrics fm = g.getFontMetrics();
	int title_height = fm.getAscent() + fm.getDescent();

	/*
		<clip x="0" y="0" width="width-8" height="height"/>
		<title color="blend/gtk:fg[INSENSITIVE]/gtk:bg[INSENSITIVE]/0.0"
			x="LeftTitleTextPad"
			y="((height - title_height) / 2) `max` 0"/>
	*/
	Rectangle clipRect = g.getClipBounds();
	g.setClip(0, 0, width-8, height);
	g.setColor(blendColor(getColor(DISABLED, GTKColorType.TEXT_FOREGROUND),
			      getColor(DISABLED, GTKColorType.BACKGROUND), 0.0F));
	paintTitle(g, LeftTitleTextPad, Math.max((height - title_height) / 2, 0), frame);
	g.setClip(clipRect);
    }

    private void title_normal(Graphics g, int width, int height, JInternalFrame frame) {
	/*
		<include name="titlebar_bg_unfocused"/>
		<include name="title_text_no_icon"/>
		<line color="blend/gtk:bg[INSENSITIVE]/gtk:fg[INSENSITIVE]/0.3" x1="0" y1="0" x2="0" y2="height-2"/>
		<line color="blend/gtk:bg[INSENSITIVE]/gtk:fg[INSENSITIVE]/0.3" x1="width-1" y1="0" x2="width-1" y2="height-2"/>
	*/
	titlebar_bg_unfocused(g, width, height);
	title_text_no_icon(g, width, height, frame);
	g.setColor(blendColor(getColor(DISABLED, GTKColorType.BACKGROUND),
			      getColor(DISABLED, GTKColorType.FOREGROUND), 0.3F));
	g.drawLine(0,       0, 0,       height-2);
	g.drawLine(width-1, 0, width-1, height-2);
    }

    private void title_focused(Graphics g, int width, int height, JInternalFrame frame) {
	/*
		<include name="title_gradient"/>
		<include name="title_text_focused_no_icon"/>
		<line color="blend/gtk:bg[SELECTED]/#000000/0.4" x1="0" y1="0" x2="0" y2="height"/>
		<line color="blend/gtk:bg[SELECTED]/#000000/0.4" x1="width-1" y1="0" x2="width-1" y2="height"/>
	*/
	title_gradient(g, width, height, frame);
	title_text_focused_no_icon(g, width, height, frame);
	g.setColor(blendColor(getColor(SELECTED, GTKColorType.BACKGROUND),
			      getColor(SELECTED, GTKColorType.FOREGROUND), 0.4F));
	g.drawLine(0,       0, 0,       height);
	g.drawLine(width-1, 0, width-1, height);
    }


    private void paintTitle(Graphics g, int x0, int y0, JInternalFrame frame) {
	String title = frame.getTitle();
        if (title != null) {
	    if (frame.getComponentOrientation().isLeftToRight()) {
		title = getTitle(title, g.getFontMetrics(), calculateTitleWidth(g, frame));
	    }
            g.drawString(title, x0, y0 + g.getFontMetrics().getAscent());
        }
    }

    private int calculateTitleWidth(Graphics g, JInternalFrame frame) {
	FrameGeometry gm = getFrameGeometry();
	String title = frame.getTitle();
	if (title != null) {
	    JComponent titlePane = findChild(frame, "InternalFrame.northPane");
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
// 	    return Math.min(SwingUtilities.computeStringWidth(fm, title),
// 			    buttonX - (left_width + ButtonWidth + IconTitleSpacing) - 3);
	    return SwingUtilities.computeStringWidth(g.getFontMetrics(), title);
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

	frame.setOpaque(false);
	findChild(titlePane, "InternalFrameTitlePane.closeButton").setOpaque(false);
	//boolean active = ((context.getComponentState() & SELECTED) != 0);
	boolean active = frame.isSelected();
	FrameGeometry gm = frame.isMaximum() ? normalMaximizedFrameGeometry
					     : normalFrameGeometry;
	setFrameGeometry(titlePane, gm);

	Font oldFont = g.getFont();
	g.setFont(titlePane.getFont());
	g.translate(x0, y0);
// 	Shape oldClip = g.getClip();
// 	g.setClip(new RoundRectangle2D.Float(0F, 0F,
// 					     (float)width, (float)height,
// 					     12F, 12F));

	// Paint border background because we are not opaque and have no rounded clip
	g.setColor(frame.getBackground());
	g.fillRect(0, gm.top_height,
		   gm.left_width, height - gm.top_height - gm.bottom_height);
	g.fillRect(width - gm.right_width - 1, gm.top_height,
		   gm.right_width, height - gm.top_height - gm.bottom_height);
	g.fillRect(gm.left_width, height - gm.bottom_height - 1,
		   width - gm.left_width - gm.right_width, gm.bottom_height);

	int titleX = 0;
	int titleWidth = width;
	JComponent menuButton = findChild(titlePane, "InternalFrameTitlePane.menuButton");
	if (menuButton != null) {
	    int x = menuButton.getX() + menuButton.getWidth() - x0;
	    titleX += x;
	    titleWidth -= x;
	}

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
	if (button != null) {
	    titleWidth = Math.min(width, button.getX() - x0);
	}

	if (!active) {
	    /*
		<frame focus="no" state="normal" resize="both" style="normal_unfocused"/>
		    <piece position="entire_background" draw_ops="unfocus_background"/>
		    <piece position="title" draw_ops="title_normal"/>
	    */
	    unfocus_background(g, width, height);
	    g.translate(titleX, 1);
	    title_normal(g, titleWidth, titlePane.getHeight()-1, frame);
	    g.translate(-titleX, -1);
	} else {
	    /*
		<frame focus="yes" state="normal" resize="both" style="normal_focused"/>
		    <piece position="entire_background" draw_ops="focus_background"/>
		    <piece position="title" draw_ops="title_focused"/>
	    */
	    focus_background(g, width, height);
	    g.translate(titleX, 1);
	    title_focused(g, titleWidth, titlePane.getHeight()-1, frame);
	    g.translate(-titleX, -1);
	}

// 	g.setClip(oldClip);
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
	    if (image != null) {
		images.put(key, image);
	    }
	}
	return image;
    }

    private void drawImage(Graphics g, Image im, Color colorize, float alpha, int x, int y) {
	if (g instanceof Graphics2D) {
	    Graphics2D g2 = (Graphics2D)g;
	    Composite oldComp = g2.getComposite();
	    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
	    g2.drawImage(im, x, y, null);
	    g2.setComposite(oldComp);
	}
    }
}


