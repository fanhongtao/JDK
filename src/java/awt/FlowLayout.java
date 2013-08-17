/*
 * @(#)FlowLayout.java	1.27 98/07/01
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

/**
 * A flow layout arranges components in a left-to-right flow, much 
 * like lines of text in a paragraph. Flow layouts are typically used 
 * to arrange buttons in a panel. It will arrange
 * buttons left to right until no more buttons fit on the same line.
 * Each line is centered.
 * <p>
 * For example, the following picture shows an applet using the flow 
 * layout manager (its default layout manager) to position three buttons:
 * <p>
 * <img src="images-awt/FlowLayout-1.gif" 
 * ALT="Graphic of Layout for Three Buttons" 
 * ALIGN=center HSPACE=10 VSPACE=7>
 * <p>
 * Here is the code for this applet: 
 * <p>
 * <hr><blockquote><pre>
 * import java.awt.*;
 * import java.applet.Applet;
 * 
 * public class myButtons extends Applet {
 *     Button button1, button2, button3;
 *     public void init() {
 *         button1 = new Button("Ok");
 *         button2 = new Button("Open");
 *         button3 = new Button("Close");
 *         add(button1);
 *         add(button2);
 *         add(button3);
 *     }
 * }
 * </pre></blockquote><hr>
 * <p>
 * A flow layout lets each component assume its natural (preferred) size. 
 *
 * @version 	1.27, 07/01/98
 * @author 	Arthur van Hoff
 * @author 	Sami Shaio
 * @since       JDK1.0
 */
public class FlowLayout implements LayoutManager, java.io.Serializable {

    /**
     * This value indicates that each row of components
     * should be left-justified. 
     * @since   JDK1.0 
     */
    public static final int LEFT 	= 0;

    /**
     * This value indicates that each row of components
     * should be centered. 
     * @since   JDK1.0 
     */
    public static final int CENTER 	= 1;

    /**
     * This value indicates that each row of components
     * should be right-justified. 
     * @since   JDK1.0
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
     * @since JDK1.0
     */
    public FlowLayout() {
	this(CENTER, 5, 5);
    }

    /**
     * Constructs a new Flow Layout with the specified alignment and a
     * default 5-unit horizontal and vertical gap.
     * The value of the alignment argument must be one of 
     * <code>FlowLayout.LEFT</code>, <code>FlowLayout.RIGHT</code>, 
     * or <code>FlowLayout.CENTER</code>.
     * @param align the alignment value
     * @since JDK1.0
     */
    public FlowLayout(int align) {
	this(align, 5, 5);
    }

    /**
     * Creates a new flow layout manager with the indicated alignment 
     * and the indicated horizontal and vertical gaps. 
     * <p>
     * The value of the alignment argument must be one of 
     * <code>FlowLayout.LEFT</code>, <code>FlowLayout.RIGHT</code>, 
     * or <code>FlowLayout.CENTER</code>.  
     * @param      align   the alignment value.
     * @param      hgap    the horizontal gap between components.
     * @param      vgap    the vertical gap between components.
     * @since      JDK1.0
     */
    public FlowLayout(int align, int hgap, int vgap) {
	this.align = align;
	this.hgap = hgap;
	this.vgap = vgap;
    }

    /**
     * Gets the alignment for this layout.
     * Possible values are <code>FlowLayout.LEFT</code>,  
     * <code>FlowLayout.RIGHT</code>, or <code>FlowLayout.CENTER</code>.  
     * @return     the alignment value for this layout.
     * @see        java.awt.FlowLayout#setAlignment
     * @since      JDK1.1
     */
    public int getAlignment() {
	return align;
    }
    
    /**
     * Sets the alignment for this layout.
     * Possible values are <code>FlowLayout.LEFT</code>,  
     * <code>FlowLayout.RIGHT</code>, and <code>FlowLayout.CENTER</code>.  
     * @param      align the alignment value.
     * @see        java.awt.FlowLayout#getAlignment
     * @since      JDK1.1
     */
    public void setAlignment(int align) {
	this.align = align;
    }

    /**
     * Gets the horizontal gap between components.
     * @return     the horizontal gap between components.
     * @see        java.awt.FlowLayout#setHgap
     * @since      JDK1.1
     */
    public int getHgap() {
	return hgap;
    }
    
    /**
     * Sets the horizontal gap between components.
     * @param hgap the horizontal gap between components
     * @see        java.awt.FlowLayout#getHgap
     * @since      JDK1.1
     */
    public void setHgap(int hgap) {
	this.hgap = hgap;
    }
    
    /**
     * Gets the vertical gap between components.
     * @return     the vertical gap between components.
     * @see        java.awt.FlowLayout#setVgap
     * @since      JDK1.1
     */
    public int getVgap() {
	return vgap;
    }
    
    /**
     * Sets the vertical gap between components.
     * @param vgap the vertical gap between components
     * @see        java.awt.FlowLayout#getVgap
     * @since      JDK1.1
     */
    public void setVgap(int vgap) {
	this.vgap = vgap;
    }

    /**
     * Adds the specified component to the layout. Not used by this class.
     * @param name the name of the component
     * @param comp the component to be added
     * @since JDK1.0
     */
    public void addLayoutComponent(String name, Component comp) {
    }

    /**
     * Removes the specified component from the layout. Not used by
     * this class.  
     * @param comp the component to remove
     * @see       java.awt.Container#removeAll
     * @since     JDK1.0
     */
    public void removeLayoutComponent(Component comp) {
    }

    /**
     * Returns the preferred dimensions for this layout given the components
     * in the specified target container.
     * @param target the component which needs to be laid out
     * @return    the preferred dimensions to lay out the 
     *                    subcomponents of the specified container.
     * @see Container
     * @see #minimumLayoutSize
     * @see       java.awt.Container#getPreferredSize
     * @since     JDK1.0
     */
    public Dimension preferredLayoutSize(Container target) {
      synchronized (target.getTreeLock()) {
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
    }

    /**
     * Returns the minimum dimensions needed to layout the components
     * contained in the specified target container.
     * @param target the component which needs to be laid out 
     * @return    the minimum dimensions to lay out the 
     *                    subcomponents of the specified container.
     * @see #preferredLayoutSize
     * @see       java.awt.Container
     * @see       java.awt.Container#doLayout
     * @since     JDK1.0
     */
    public Dimension minimumLayoutSize(Container target) {
      synchronized (target.getTreeLock()) {
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
      synchronized (target.getTreeLock()) {
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
    }

    /**
     * Lays out the container. This method lets each component take 
     * its preferred size by reshaping the components in the 
     * target container in order to satisfy the constraints of
     * this <code>FlowLayout</code> object. 
     * @param target the specified component being laid out.
     * @see Container
     * @see       java.awt.Container#doLayout
     * @since     JDK1.0
     */
    public void layoutContainer(Container target) {
      synchronized (target.getTreeLock()) {
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
    }
    
    /**
     * Returns a string representation of this <code>FlowLayout</code>
     * object and its values.
     * @return     a string representation of this layout.
     * @since      JDK1.0
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
