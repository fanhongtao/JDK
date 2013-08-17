/*
 * @(#)ScrollPaneDemo.java	1.2 99/07/22
 *
 * Copyright (c) 1997-1999 by Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
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
 * @version 1.2 07/22/99
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

