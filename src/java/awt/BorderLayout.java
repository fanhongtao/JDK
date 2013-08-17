/*
 * @(#)BorderLayout.java	1.32 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.awt;

import java.util.Hashtable;

/**
 * A border layout lays out a container, arranging and resizing
 * its components to fit in five regions:
 * <code>North</code>, <code>South</code>, <code>East</code>, 
 * <code>West</code>, and <code>Center</code>.  When adding a 
 * component to a container with a border layout, use one of these
 * five names, for example:
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
 * <p>
 * The components are laid out according to their 
 * preferred sizes and the constraints of the container's size. 
 * The <code>North</code> and <code>South</code> components may 
 * be stretched horizontally; the <code>East</code> and 
 * <code>West</code> components may be stretched vertically; 
 * the <code>Center</code> component may stretch both horizontally 
 * and vertically to fill any space left over. 
 * <p>
 * Here is an example of five buttons in an applet laid out using 
 * the <code>BorderLayout</code> layout manager:
 * <p>
 * <img src="images-awt/BorderLayout-1.gif"
 * ALIGN=center HSPACE=10 VSPACE=7>
 * <p>
 * The code for this applet is as follows: 
 * <p>
 * <hr><blockquote><pre>
 * import java.awt.*;
 * import java.applet.Applet;
 * 
 * public class buttonDir extends Applet {
 *   public void init() {
 *     setLayout(new BorderLayout());
 *     add("North",  new Button("North"));
 *     add("South",  new Button("South"));
 *     add("East",   new Button("East"));
 *     add("West",   new Button("West"));
 *     add("Center", new Button("Center"));
 *   }
 * }
 * </pre></blockquote><hr>
 * <p>
 * @version 	1.27 02/11/97
 * @author 	Arthur van Hoff
 * @see         java.awt.Container.add(String, Component)
 * @since       JDK1.0
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
     * The north layout constraint (top of container).
     */
    public static final String NORTH  = "North";

    /**
     * The south layout constraint (bottom of container).
     */
    public static final String SOUTH  = "South";

    /**
     * The east layout constraint (left side of container).
     */
    public static final String EAST   = "East";

    /**
     * The west layout constraint (right side of container).
     */
    public static final String WEST   = "West";

    /**
     * The center layout constraint (middle of container).
     */
    public static final String CENTER = "Center";

    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = -8658291919501921765L;

    /**
     * Constructs a new border layout with  
     * no gaps between components.
     * @since     JDK1.0
     */
    public BorderLayout() {
	this(0, 0);
    }

    /**
     * Constructs a border layout with the specified gaps 
     * between components.
     * The horizontal gap is specified by <code>hgap</code> 
     * and the vertical gap is specified by <code>vgap</code>.
     * @param   hgap   the horizontal gap.
     * @param   vgap   the vertical gap.	
     * @since   JDK1.0
     */	
    public BorderLayout(int hgap, int vgap) {
	this.hgap = hgap;
	this.vgap = vgap;
    }

    /**
     * Returns the horizontal gap between components.
     * @since   JDK1.1
     */
    public int getHgap() {
	return hgap;
    }
    
    /**
     * Sets the horizontal gap between components.
     * @param hgap the horizontal gap between components
     * @since   JDK1.1
     */
    public void setHgap(int hgap) {
	this.hgap = hgap;
    }
    
    /**
     * Returns the vertical gap between components.
     * @since   JDK1.1
     */
    public int getVgap() {
	return vgap;
    }
    
    /**
     * Sets the vertical gap between components.
     * @param vgap the vertical gap between components
     * @since   JDK1.1
     */
    public void setVgap(int vgap) {
	this.vgap = vgap;
    }

    /**
     * Adds the specified component to the layout, using the specified
     * constraint object.  For border layouts, the constraint must be
     * one of the following strings:  <code>"North"</code>,
     * <code>"South"</code>, <code>"East"</code>,
     * <code>"West"</code>, or <code>"Center"</code>.  
     * <p>
     * Most applications do not call this method directly. This method 
     * is called when a component is added to a container using the 
     * <code>Container.add</code> method with the same argument types.
     * @param   comp         the component to be added.
     * @param   constraints  an object that specifies how and where 
     *                       the component is added to the layout.
     * @see     java.awt.Container#add(java.awt.Component, java.lang.Object)
     * @exception   IllegalArgumentException  if the constraint object is not
     *                 a string, or if it not one of the five specified strings.
     * @since   JDK1.1
     */
    public void addLayoutComponent(Component comp, Object constraints) {
      synchronized (comp.getTreeLock()) {
	if ((constraints == null) || (constraints instanceof String)) {
	    addLayoutComponent((String)constraints, comp);
	} else {
	    throw new IllegalArgumentException("cannot add to layout: constraint must be a string (or null)");
	}
      }
    }

    /**
     * @deprecated  replaced by <code>addLayoutComponent(Component, Object)</code>.
     */
    public void addLayoutComponent(String name, Component comp) {
      synchronized (comp.getTreeLock()) {
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
    }

    /**
     * Removes the specified component from this border layout. This 
     * method is called when a container calls its <code>remove</code> or 
     * <code>removeAll</code> methods. Most applications do not call this 
     * method directly. 
     * @param   comp   the component to be removed.
     * @see     java.awt.Container#remove(java.awt.Component)
     * @see     java.awt.Container#removeAll()
     * @since   JDK1.0
     */
    public void removeLayoutComponent(Component comp) {
      synchronized (comp.getTreeLock()) {
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
    }

    /**
     * Determines the minimum size of the <code>target</code> container 
     * using this layout manager. 
     * <p>
     * This method is called when a container calls its 
     * <code>getMinimumSize</code> method. Most applications do not call 
     * this method directly. 
     * @param   target   the container in which to do the layout.
     * @return  the minimum dimensions needed to lay out the subcomponents 
     *          of the specified container.
     * @see     java.awt.Container  
     * @see     java.awt.BorderLayout#preferredLayoutSize
     * @see     java.awt.Container#getMinimumSize()
     * @since   JDK1.0
     */
    public Dimension minimumLayoutSize(Container target) {
      synchronized (target.getTreeLock()) {
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
    }
    
    /**
     * Determines the preferred size of the <code>target</code> 
     * container using this layout manager, based on the components
     * in the container. 
     * <p>
     * Most applications do not call this method directly. This method
     * is called when a container calls its <code>getPreferredSize</code> 
     * method.
     * @param   target   the container in which to do the layout.
     * @return  the preferred dimensions to lay out the subcomponents 
     *          of the specified container.
     * @see     java.awt.Container  
     * @see     java.awt.BorderLayout#minimumLayoutSize  
     * @see     java.awt.Container#getPreferredSize()
     * @since   JDK1.0
     */
    public Dimension preferredLayoutSize(Container target) {
      synchronized (target.getTreeLock()) {
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
     * Lays out the container argument using this border layout. 
     * <p>
     * This method actually reshapes the components in the specified
     * container in order to satisfy the constraints of this 
     * <code>BorderLayout</code> object. The <code>North</code> 
     * and <code>South</code>components, if any, are placed at 
     * the top and bottom of the container, respectively. The 
     * <code>West</code> and <code>East</code> components are 
     * then placed on the left and right, respectively. Finally, 
     * the <code>Center</code> object is placed in any remaining 
     * space in the middle. 
     * <p>
     * Most applications do not call this method directly. This method 
     * is called when a container calls its <code>doLayout</code> method. 
     * @param   target   the container in which to do the layout.
     * @see     java.awt.Container  
     * @see     java.awt.Container#doLayout()
     * @since   JDK1.0
     */
    public void layoutContainer(Container target) {
      synchronized (target.getTreeLock()) {
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
    }
    
    /**
     * Returns a string representation of the state of this border layout.
     * @return    a string representation of this border layout.
     * @since     JDK1.0
     */
    public String toString() {
	return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + "]";
    }
}
