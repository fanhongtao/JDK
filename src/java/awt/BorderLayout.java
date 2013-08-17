/*
 * @(#)BorderLayout.java	1.27 97/01/27
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

import java.util.Hashtable;

/**
 * A TNT style border bag layout. It will layout a container
 * using members named "North", "South", "East", "West" and
 * "Center".
 *
 * When you add a component to a container that has a BorderLayout
 * layout manager, be sure to specify a string for where to add the
 * component, for example:
 * <pre>
 *    Panel p = new Panel();
 *    p.setLayout(new BorderLayout());
 *    p.add(new Button("Okay"), "South");
 * </pre>
 * As a convenience, BorderLayout interprets the absence of a string
 * specification the same as "Center":
 * <pre>
 *    Panel p2 = new Panel();
 *    p2.setLayout(new BorderLayout());
 *    p2.add(new TextArea());  // Same as p.add(new TextArea(), "Center");
 * </pre>
 *
 * The "North", "South", "East" and "West" components get layed out
 * according to their preferred sizes and the constraints of the
 * container's size. The "Center" component will get any space left
 * over. 
 * 
 * @version 	1.27 01/27/97
 * @author 	Arthur van Hoff
 */
public class BorderLayout implements LayoutManager2,
				     java.io.Serializable {
    int hgap;
    int vgap;

    Component north;
    Component west;
    Component east;
    Component south;
    Component center;

    /**
     * The north layout constraint.
     */
    public static final String NORTH  = "North";

    /**
     * The south layout constraint.
     */
    public static final String SOUTH  = "South";

    /**
     * The east layout constraint.
     */
    public static final String EAST   = "East";

    /**
     * The west layout constraint.
     */
    public static final String WEST   = "West";

    /**
     * The center layout constraint.
     */
    public static final String CENTER = "Center";

    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = -8658291919501921765L;

    /**
     * Constructs a new BorderLayout with no gaps between components.
     */
    public BorderLayout() {
	this(0, 0);
    }

    /**
     * Constructs a BorderLayout with the specified gaps.
     * @param hgap the horizontal gap
     * @param vgap the vertical gap
     */
    public BorderLayout(int hgap, int vgap) {
	this.hgap = hgap;
	this.vgap = vgap;
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
     * Adds the specified component to the layout, using the specified
     * constraint object.
     * @param comp the component to be added
     * @param constraints  where/how the component is added to the layout.
     */
    public void addLayoutComponent(Component comp, Object constraints) {
	if ((constraints == null) || (constraints instanceof String)) {
	    addLayoutComponent((String)constraints, comp);
	} else {
	    throw new IllegalArgumentException("cannot add to layout: constraint must be a string (or null)");
	}
    }

    /**
     * Replaced by addLayoutComponent(Component, Object).
     * @deprecated
     */
    public void addLayoutComponent(String name, Component comp) {

	/* Special case:  treat null the same as "Center". */
	if (name == null) {
	    name = "Center";
	}

	/* Assign the component to one of the known regions of the layout.
	 */
	if ("Center".equals(name)) {
	    center = comp;
	} else if ("North".equals(name)) {
	    north = comp;
	} else if ("South".equals(name)) {
	    south = comp;
	} else if ("East".equals(name)) {
	    east = comp;
	} else if ("West".equals(name)) {
	    west = comp;
	} else {
	    throw new IllegalArgumentException("cannot add to layout: unknown constraint: " + name);
	}
    }

    /**
     * Removes the specified component from the layout.
     * @param comp the component to be removed
     */
    public void removeLayoutComponent(Component comp) {
	if (comp == center) {
	    center = null;
	} else if (comp == north) {
	    north = null;
	} else if (comp == south) {
	    south = null;
	} else if (comp == east) {
	    east = null;
	} else if (comp == west) {
	    west = null;
	}
    }

    /**
     * Returns the minimum dimensions needed to layout the components
     * contained in the specified target container. 
     * @param target the Container on which to do the layout
     * @see Container
     * @see #preferredLayoutSize
     */
    public Dimension minimumLayoutSize(Container target) {
	Dimension dim = new Dimension(0, 0);

	if ((east != null) && east.visible) {
	    Dimension d = east.getMinimumSize();
	    dim.width += d.width + hgap;
	    dim.height = Math.max(d.height, dim.height);
	}
	if ((west != null) && west.visible) {
	    Dimension d = west.getMinimumSize();
	    dim.width += d.width + hgap;
	    dim.height = Math.max(d.height, dim.height);
	}
	if ((center != null) && center.visible) {
	    Dimension d = center.getMinimumSize();
	    dim.width += d.width;
	    dim.height = Math.max(d.height, dim.height);
	}
	if ((north != null) && north.visible) {
	    Dimension d = north.getMinimumSize();
	    dim.width = Math.max(d.width, dim.width);
	    dim.height += d.height + vgap;
	}
	if ((south != null) && south.visible) {
	    Dimension d = south.getMinimumSize();
	    dim.width = Math.max(d.width, dim.width);
	    dim.height += d.height + vgap;
	}

	Insets insets = target.getInsets();
	dim.width += insets.left + insets.right;
	dim.height += insets.top + insets.bottom;

	return dim;
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

	if ((east != null) && east.visible) {
	    Dimension d = east.getPreferredSize();
	    dim.width += d.width + hgap;
	    dim.height = Math.max(d.height, dim.height);
	}
	if ((west != null) && west.visible) {
	    Dimension d = west.getPreferredSize();
	    dim.width += d.width + hgap;
	    dim.height = Math.max(d.height, dim.height);
	}
	if ((center != null) && center.visible) {
	    Dimension d = center.getPreferredSize();
	    dim.width += d.width;
	    dim.height = Math.max(d.height, dim.height);
	}
	if ((north != null) && north.visible) {
	    Dimension d = north.getPreferredSize();
	    dim.width = Math.max(d.width, dim.width);
	    dim.height += d.height + vgap;
	}
	if ((south != null) && south.visible) {
	    Dimension d = south.getPreferredSize();
	    dim.width = Math.max(d.width, dim.width);
	    dim.height += d.height + vgap;
	}

	Insets insets = target.getInsets();
	dim.width += insets.left + insets.right;
	dim.height += insets.top + insets.bottom;

	return dim;
    }

    /**
     * Returns the maximum dimensions for this layout given the components
     * in the specified target container.
     * @param target the component which needs to be laid out
     * @see Container
     * @see #minimumLayoutSize
     * @see #preferredLayoutSize
     */
    public Dimension maximumLayoutSize(Container target) {
	return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Returns the alignment along the x axis.  This specifies how
     * the component would like to be aligned relative to other 
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getLayoutAlignmentX(Container parent) {
	return 0.5f;
    }

    /**
     * Returns the alignment along the y axis.  This specifies how
     * the component would like to be aligned relative to other 
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getLayoutAlignmentY(Container parent) {
	return 0.5f;
    }

    /**
     * Invalidates the layout, indicating that if the layout manager
     * has cached information it should be discarded.
     */
    public void invalidateLayout(Container target) {
    }
				      
    /**
     * Lays out the specified container. This method will actually reshape the
     * components in the specified target container in order to satisfy the 
     * constraints of the BorderLayout object. 
     * @param target the component being laid out
     * @see Container
     */
    public void layoutContainer(Container target) {
	Insets insets = target.getInsets();
	int top = insets.top;
	int bottom = target.height - insets.bottom;
	int left = insets.left;
	int right = target.width - insets.right;

	if ((north != null) && north.visible) {
	    north.setSize(right - left, north.height);
	    Dimension d = north.getPreferredSize();
	    north.setBounds(left, top, right - left, d.height);
	    top += d.height + vgap;
	}
	if ((south != null) && south.visible) {
	    south.setSize(right - left, south.height);
	    Dimension d = south.getPreferredSize();
	    south.setBounds(left, bottom - d.height, right - left, d.height);
	    bottom -= d.height + vgap;
	}
	if ((east != null) && east.visible) {
	    east.setSize(east.width, bottom - top);
	    Dimension d = east.getPreferredSize();
	    east.setBounds(right - d.width, top, d.width, bottom - top);
	    right -= d.width + hgap;
	}
	if ((west != null) && west.visible) {
	    west.setSize(west.width, bottom - top);
	    Dimension d = west.getPreferredSize();
	    west.setBounds(left, top, d.width, bottom - top);
	    left += d.width + hgap;
	}
	if ((center != null) && center.visible) {
	    center.setBounds(left, top, right - left, bottom - top);
	}
    }
    
    /**
     * Returns the String representation of this BorderLayout's values.
     */
    public String toString() {
	return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + "]";
    }
}
