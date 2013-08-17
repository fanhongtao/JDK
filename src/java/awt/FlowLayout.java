/*
 * @(#)FlowLayout.java	1.23 97/01/27
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
 */
package java.awt;

/**
 * Flow layout is used to layout buttons in a panel. It will arrange
 * buttons left to right until no more buttons fit on the same line.
 * Each line is centered.
 *
 * @version 	1.23, 01/27/97
 * @author 	Arthur van Hoff
 * @author 	Sami Shaio
 */
public class FlowLayout implements LayoutManager, java.io.Serializable {

    /**
     * The left alignment variable. 
     */
    public static final int LEFT 	= 0;

    /**
     * The right alignment variable. 
     */
    public static final int CENTER 	= 1;

    /**
     * The right alignment variable.
     */
    public static final int RIGHT 	= 2;

    int align;
    int hgap;
    int vgap;

    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = -7262534875583282631L;

    /**
     * Constructs a new Flow Layout with a centered alignment and a
     * default 5-unit horizontal and vertical gap.
     */
    public FlowLayout() {
	this(CENTER, 5, 5);
    }

    /**
     * Constructs a new Flow Layout with the specified alignment and a
     * default 5-unit horizontal and vertical gap.
     * @param align the alignment value
     */
    public FlowLayout(int align) {
	this(align, 5, 5);
    }

    /**
     * Constructs a new Flow Layout with the specified alignment and gap
     * values.
     * @param align the alignment value
     * @param hgap the horizontal gap variable
     * @param vgap the vertical gap variable
     */
    public FlowLayout(int align, int hgap, int vgap) {
	this.align = align;
	this.hgap = hgap;
	this.vgap = vgap;
    }

    /**
     * Returns the alignment value for this layout, one of LEFT,
     * CENTER, or RIGHT.
     */
    public int getAlignment() {
	return align;
    }
    
    /**
     * Sets the alignment value for this layout.
     * @param align the alignment value, one of LEFT, CENTER, or RIGHT.
     */
    public void setAlignment(int align) {
	this.align = align;
    }

    /**
     * Returns the horizontal gap between components.
     */
    public int getHgap() {
	return hgap;
    }
    
    /**
     * Sets the horizontal gap between components.
     * @param hgap the horizontal gap between components
     */
    public void setHgap(int hgap) {
	this.hgap = hgap;
    }
    
    /**
     * Returns the vertical gap between components.
     */
    public int getVgap() {
	return vgap;
    }
    
    /**
     * Sets the vertical gap between components.
     * @param vgap the vertical gap between components
     */
    public void setVgap(int vgap) {
	this.vgap = vgap;
    }

    /**
     * Adds the specified component to the layout. Not used by this class.
     * @param name the name of the component
     * @param comp the the component to be added
     */
    public void addLayoutComponent(String name, Component comp) {
    }

    /**
     * Removes the specified component from the layout. Not used by
     * this class.  
     * @param comp the component to remove
     */
    public void removeLayoutComponent(Component comp) {
    }

    /**
     * Returns the preferred dimensions for this layout given the components
     * in the specified target container.
     * @param target the component which needs to be laid out
     * @see Container
     * @see #minimumLayoutSize
     */
    public Dimension preferredLayoutSize(Container target) {
	Dimension dim = new Dimension(0, 0);
	int nmembers = target.getComponentCount();

	for (int i = 0 ; i < nmembers ; i++) {
	    Component m = target.getComponent(i);
	    if (m.visible) {
		Dimension d = m.getPreferredSize();
		dim.height = Math.max(dim.height, d.height);
		if (i > 0) {
		    dim.width += hgap;
		}
		dim.width += d.width;
	    }
	}
	Insets insets = target.getInsets();
	dim.width += insets.left + insets.right + hgap*2;
	dim.height += insets.top + insets.bottom + vgap*2;
	return dim;
    }

    /**
     * Returns the minimum dimensions needed to layout the components
     * contained in the specified target container.
     * @param target the component which needs to be laid out 
     * @see #preferredLayoutSize
     */
    public Dimension minimumLayoutSize(Container target) {
	Dimension dim = new Dimension(0, 0);
	int nmembers = target.getComponentCount();

	for (int i = 0 ; i < nmembers ; i++) {
	    Component m = target.getComponent(i);
	    if (m.visible) {
		Dimension d = m.getMinimumSize();
		dim.height = Math.max(dim.height, d.height);
		if (i > 0) {
		    dim.width += hgap;
		}
		dim.width += d.width;
	    }
	}
	Insets insets = target.getInsets();
	dim.width += insets.left + insets.right + hgap*2;
	dim.height += insets.top + insets.bottom + vgap*2;
	return dim;
    }

    /** 
     * Centers the elements in the specified row, if there is any slack.
     * @param target the component which needs to be moved
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width dimensions
     * @param height the height dimensions
     * @param rowStart the beginning of the row
     * @param rowEnd the the ending of the row
     */
    private void moveComponents(Container target, int x, int y, int width, int height, int rowStart, int rowEnd) {
	switch (align) {
	case LEFT:
	    break;
	case CENTER:
	    x += width / 2;
	    break;
	case RIGHT:
	    x += width;
	    break;
	}
	for (int i = rowStart ; i < rowEnd ; i++) {
	    Component m = target.getComponent(i);
	    if (m.visible) {
		m.setLocation(x, y + (height - m.height) / 2);
		x += hgap + m.width;
	    }
	}
    }

    /**
     * Lays out the container. This method will actually reshape the
     * components in the target in order to satisfy the constraints of
     * the BorderLayout object. 
     * @param target the specified component being laid out.
     * @see Container
     */
    public void layoutContainer(Container target) {
	Insets insets = target.getInsets();
	int maxwidth = target.width - (insets.left + insets.right + hgap*2);
	int nmembers = target.getComponentCount();
	int x = 0, y = insets.top + vgap;
	int rowh = 0, start = 0;

	for (int i = 0 ; i < nmembers ; i++) {
	    Component m = target.getComponent(i);
	    if (m.visible) {
		Dimension d = m.getPreferredSize();
		m.setSize(d.width, d.height);
	
		if ((x == 0) || ((x + d.width) <= maxwidth)) {
		    if (x > 0) {
			x += hgap;
		    }
		    x += d.width;
		    rowh = Math.max(rowh, d.height);
		} else {
		    moveComponents(target, insets.left + hgap, y, maxwidth - x, rowh, start, i);
		    x = d.width;
		    y += vgap + rowh;
		    rowh = d.height;
		    start = i;
		}
	    }
	}
	moveComponents(target, insets.left + hgap, y, maxwidth - x, rowh, start, nmembers);
    }
    
    /**
     * Returns the String representation of this FlowLayout's values.
     */
    public String toString() {
	String str = "";
	switch (align) {
	  case LEFT:    str = ",align=left"; break;
	  case CENTER:  str = ",align=center"; break;
	  case RIGHT:   str = ",align=right"; break;
	}
	return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + str + "]";
    }
}
