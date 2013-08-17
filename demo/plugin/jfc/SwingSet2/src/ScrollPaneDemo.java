/*
 * @(#)ScrollPaneDemo.java	1.4 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.colorchooser.*;
import javax.swing.filechooser.*;
import javax.accessibility.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.io.*;
import java.applet.*;
import java.net.*;

/**
 * Scroll Pane Demo
 *
 * @version 1.4 12/03/01
 * @author Jeff Dinkins
 */
public class ScrollPaneDemo extends DemoModule {

    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
	ScrollPaneDemo demo = new ScrollPaneDemo(null);
	demo.mainImpl();
    }

    /**
     * ScrollPaneDemo Constructor
     */
    public ScrollPaneDemo(SwingSet2 swingset) {
	super(swingset, "ScrollPaneDemo", "toolbar/JScrollPane.gif");

	ImageIcon crayons = createImageIcon("scrollpane/crayons.jpg",  getString("ScrollPaneDemo.crayons"));
	getDemoPanel().add(new ImageScroller(this, crayons), BorderLayout.CENTER);
    }


    /**
     * ScrollPane class that demonstrates how to set the various column and row headers
     * and corners.
     */
    class ImageScroller extends JScrollPane {
	public ImageScroller(ScrollPaneDemo demo, Icon icon) {
	    super();

	    // Panel to hold the icon image
	    JPanel p = new JPanel(new BorderLayout());
	    p.add(new JLabel(icon), BorderLayout.CENTER);
	    getViewport().add(p);

	    // Create and add a column header to the scrollpane
	    JLabel colHeader = new JLabel(
		demo.createImageIcon("scrollpane/colheader.jpg", getString("ScrollPaneDemo.colheader")));
	    setColumnHeaderView(colHeader);

	    // Create and add a row header to the scrollpane
	    JLabel rowHeader = new JLabel(
		demo.createImageIcon("scrollpane/rowheader.jpg", getString("ScrollPaneDemo.rowheader")));
	    setRowHeaderView(rowHeader);

	    // Create and add the upper left corner
	    JLabel cornerUL = new JLabel(
		demo.createImageIcon("scrollpane/upperleft.jpg", getString("ScrollPaneDemo.upperleft")));
	    setCorner(UPPER_LEFT_CORNER, cornerUL);

	    // Create and add the upper right corner
	    JLabel cornerUR = new JLabel(
		demo.createImageIcon("scrollpane/upperright.jpg", getString("ScrollPaneDemo.upperright")));
	    setCorner(UPPER_RIGHT_CORNER, cornerUR);

	    // Create and add the lower left corner
	    JLabel cornerLL = new JLabel(
		demo.createImageIcon("scrollpane/lowerleft.jpg", getString("ScrollPaneDemo.lowerleft")));
	    setCorner(LOWER_LEFT_CORNER, cornerLL);

	    JScrollBar vsb = getVerticalScrollBar();
	    JScrollBar hsb = getHorizontalScrollBar();

	    vsb.setValue(icon.getIconHeight());
	    hsb.setValue(icon.getIconWidth()/10);
	}
    }

}

