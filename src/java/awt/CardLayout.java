/*
 * @(#)CardLayout.java	1.22 98/07/01
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
import java.util.Enumeration;

/**
 * A <code>CardLayout</code> object is a layout manager for a 
 * container. It treats each component in the container as a card.
 * Only one card is visible at a time, and the container acts as
 * a stack of cards.  
 * <p>
 * The ordering of cards is determined by the container's own internal
 * ordering of its component objects. <code>CardLayout</code> 
 * defines a set of methods that allow an application to flip 
 * through these cards sequentially, or to show a specified card.
 * The <A HREF="#addLayoutComponent"><code>addLayoutComponent</code></A> 
 * method can be used to associate a string identifier with a given card
 * for fast random access.
 *
 * @version 	1.22 07/01/98
 * @author 	Arthur van Hoff
 * @see         java.awt.Container
 * @since       JDK1.0
 */

public class CardLayout implements LayoutManager2,
				   java.io.Serializable {
    Hashtable tab = new Hashtable();
    int hgap;
    int vgap;

    /**
     * Creates a new card layout with gaps of size zero.
     * @since  JDK1.0
     */
    public CardLayout() {
	this(0, 0);
    }

    /**
     * Creates a new card layout with the specified horizontal and 
     * vertical gaps. The horizontal gaps are placed at the left and 
     * right edges. The vertical gaps are placed at the top and bottom 
     * edges. 
     * @param     hgap   the horizontal gap.
     * @param     vgap   the vertical gap.
     * @since     JDK1.0
     */
    public CardLayout(int hgap, int vgap) {
	this.hgap = hgap;
	this.vgap = vgap;
    }

    /**
     * Gets the horizontal gap between components.
     * @return    the horizontal gap between components.
     * @see       java.awt.CardLayout#setHgap(int)
     * @see       java.awt.CardLayout#getVgap()
     * @since     JDK1.1
     */
    public int getHgap() {
	return hgap;
    }
    
    /**
     * Sets the horizontal gap between components.
     * @param hgap the horizontal gap between components.
     * @see       java.awt.CardLayout#getHgap()
     * @see       java.awt.CardLayout#setVgap(int)
     * @since     JDK1.1
     */
    public void setHgap(int hgap) {
	this.hgap = hgap;
    }
    
    /**
     * Gets the vertical gap between components.
     * @return the vertical gap between components.
     * @see       java.awt.CardLayout#setVgap(int)
     * @see       java.awt.CardLayout#getHgap()
     */
    public int getVgap() {
	return vgap;
    }
    
    /**
     * Sets the vertical gap between components.
     * @param     vgap the vertical gap between components.
     * @see       java.awt.CardLayout#getVgap()
     * @see       java.awt.CardLayout#setHgap(int)
     * @since     JDK1.1
     */
    public void setVgap(int vgap) {
	this.vgap = vgap;
    }

    /**
     * Adds the specified component to this card layout's internal
     * table of names. The object specified by <code>constraints</code>  
     * must be a string. The card layout stores this string as a key-value
     * pair that can be used for random access to a particular card.
     * By calling the <code>show</code> method, an application can  
     * display the component with the specified name. 
     * @param     comp          the component to be added.
     * @param     constraints   a tag that identifies a particular 
     *                                        card in the layout.
     * @see       java.awt.CardLayout#show(java.awt.Container, java.lang.String)
     * @exception  IllegalArgumentException  if the constraint is not a string.
     * @since     JDK1.0
     */
    public void addLayoutComponent(Component comp, Object constraints) {
      synchronized (comp.getTreeLock()) {
	if (constraints instanceof String) {
	    addLayoutComponent((String)constraints, comp);
	} else {
	    throw new IllegalArgumentException("cannot add to layout: constraint must be a string");
	}
      }
    }

    /**
     * @deprecated   replaced by 
     *      <code>addLayoutComponent(Component, Object)</code>.
     */
    public void addLayoutComponent(String name, Component comp) {
      synchronized (comp.getTreeLock()) {
	if (tab.size() > 0) {
	    comp.hide();
	}
	tab.put(name, comp);
      }
    }

    /**
     * Removes the specified component from the layout.
     * @param   comp   the component to be removed.
     * @see     java.awt.Container#remove(java.awt.Component)
     * @see     java.awt.Container#removeAll()
     * @since   JDK1.0
     */
    public void removeLayoutComponent(Component comp) {
      synchronized (comp.getTreeLock()) {
	for (Enumeration e = tab.keys() ; e.hasMoreElements() ; ) {
	    String key = (String)e.nextElement();
	    if (tab.get(key) == comp) {
		tab.remove(key);
		return;
	    }
	}
      }
    }

    /** 
     * Determines the preferred size of the container argument using 
     * this card layout.
     * @param   parent the name of the parent container.
     * @return  the preferred dimensions to lay out the subcomponents 
     *                of the specified container.
     * @see     java.awt.Container#getPreferredSize
     * @see     java.awt.CardLayout#minimumLayoutSize
     * @since   JDK1.0
     */
    public Dimension preferredLayoutSize(Container parent) {
      synchronized (parent.getTreeLock()) {
	Insets insets = parent.getInsets();
	int ncomponents = parent.getComponentCount();
	int w = 0;
	int h = 0;

	for (int i = 0 ; i < ncomponents ; i++) {
	    Component comp = parent.getComponent(i);
	    Dimension d = comp.getPreferredSize();
	    if (d.width > w) {
		w = d.width;
	    }
	    if (d.height > h) {
		h = d.height;
	    }
	}
	return new Dimension(insets.left + insets.right + w + hgap*2, 
			     insets.top + insets.bottom + h + vgap*2);
      }
    }

    /** 
     * Calculates the minimum size for the specified panel.
     * @param     parent the name of the parent container 
     *                in which to do the layout.
     * @return    the minimum dimensions required to lay out the 
     *                subcomponents of the specified container.
     * @see       java.awt.Container#doLayout
     * @see       java.awt.CardLayout#preferredLayoutSize 
     * @since     JDK1.0
     */
    public Dimension minimumLayoutSize(Container parent) {
      synchronized (parent.getTreeLock()) {
	Insets insets = parent.getInsets();
	int ncomponents = parent.getComponentCount();
	int w = 0;
	int h = 0;

	for (int i = 0 ; i < ncomponents ; i++) {
	    Component comp = parent.getComponent(i);
	    Dimension d = comp.getMinimumSize();
	    if (d.width > w) {
		w = d.width;
	    }
	    if (d.height > h) {
		h = d.height;
	    }
	}
	return new Dimension(insets.left + insets.right + w + hgap*2, 
			     insets.top + insets.bottom + h + vgap*2);
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
     * Lays out the specified container using this card layout. 
     * <p>
     * Each component in the <code>parent</code> container is reshaped 
     * to be the size of the container, minus space for surrounding 
     * insets, horizontal gaps, and vertical gaps. 
     * 
     * @param     parent the name of the parent container 
     *                             in which to do the layout. 
     * @see       java.awt.Container#doLayout
     * @since     JDK1.0 
     */
    public void layoutContainer(Container parent) {
      synchronized (parent.getTreeLock()) {
	Insets insets = parent.getInsets();
	int ncomponents = parent.getComponentCount();
	for (int i = 0 ; i < ncomponents ; i++) {
	    Component comp = parent.getComponent(i);
	    if (comp.visible) {
		comp.setBounds(hgap + insets.left, vgap + insets.top, 
			       parent.width - (hgap*2 + insets.left + insets.right), 
			       parent.height - (vgap*2 + insets.top + insets.bottom));
	    }
	}
      }
    }

    /**
     * Make sure that the Container really has a CardLayout installed.
     * Otherwise havoc can ensue!
     */
    void checkLayout(Container parent) {
	if (parent.getLayout() != this) {
	    throw new IllegalArgumentException("wrong parent for CardLayout");
	}
    }

    /**
     * Flips to the first card of the container. 
     * @param     parent   the name of the parent container 
     *                          in which to do the layout.
     * @see       java.awt.CardLayout#last
     * @since     JDK1.0
     */
    public void first(Container parent) {
	synchronized (parent.getTreeLock()) {
	    checkLayout(parent);
	    int ncomponents = parent.getComponentCount();
	    for (int i = 0 ; i < ncomponents ; i++) {
		Component comp = parent.getComponent(i);
		if (comp.visible) {
		    comp.hide();
		    comp = parent.getComponent(0);
		    comp.show();
		    parent.validate();
		    return;
		}
	    }
	}
    }

    /**
     * Flips to the next card of the specified container. If the 
     * currently visible card is the last one, this method flips to the 
     * first card in the layout. 
     * @param     parent   the name of the parent container 
     *                          in which to do the layout.
     * @see       java.awt.CardLayout#previous
     * @since     JDK1.0
     */
    public void next(Container parent) {
	synchronized (parent.getTreeLock()) {
	    checkLayout(parent);
	    int ncomponents = parent.getComponentCount();
	    for (int i = 0 ; i < ncomponents ; i++) {
		Component comp = parent.getComponent(i);
		if (comp.visible) {
		    comp.hide();
		    comp = parent.getComponent((i + 1 < ncomponents) ? i+1 : 0);
		    comp.show();
		    parent.validate();
		    return;
		}
	    }
	}
    }

    /**
     * Flips to the previous card of the specified container. If the 
     * currently visible card is the first one, this method flips to the 
     * last card in the layout. 
     * @param     parent   the name of the parent container 
     *                          in which to do the layout.
     * @see       java.awt.CardLayout#next
     * @since     JDK1.0
     */
    public void previous(Container parent) {
	synchronized (parent.getTreeLock()) {
	    checkLayout(parent);
	    int ncomponents = parent.getComponentCount();
	    for (int i = 0 ; i < ncomponents ; i++) {
		Component comp = parent.getComponent(i);
		if (comp.visible) {
		    comp.hide();
		    comp = parent.getComponent((i > 0) ? i-1 : ncomponents-1);
		    comp.show();
		    parent.validate();
		    return;
		}
	    }
	}
    }

    /**
     * Flips to the last card of the container. 
     * @param     parent   the name of the parent container 
     *                          in which to do the layout.
     * @see       java.awt.CardLayout#first
     * @since     JDK1.0
     */
    public void last(Container parent) {
	synchronized (parent.getTreeLock()) {
	    checkLayout(parent);
	    int ncomponents = parent.getComponentCount();
	    for (int i = 0 ; i < ncomponents ; i++) {
		Component comp = parent.getComponent(i);
		if (comp.visible) {
		    comp.hide();
		    comp = parent.getComponent(ncomponents - 1);
		    comp.show();
		    parent.validate();
		    return;
		}
	    }
	}
    }

    /**
     * Flips to the component that was added to this layout with the  
     * specified <code>name</code>, using <code>addLayoutComponent</code>.  
     * If no such component exists, then nothing happens. 
     * @param     parent   the name of the parent container 
     *                     in which to do the layout.
     * @param     name     the component name.
     * @see       java.awt.CardLayout#addLayoutComponent(java.awt.Component, java.lang.Object)
     * @since     JDK1.0
     */
    public void show(Container parent, String name) {
	synchronized (parent.getTreeLock()) {
	    checkLayout(parent);
	    Component next = (Component)tab.get(name);
	    if ((next != null) && !next.visible){
		int ncomponents = parent.getComponentCount();
		for (int i = 0 ; i < ncomponents ; i++) {
		    Component comp = parent.getComponent(i);
		    if (comp.visible) {
			comp.hide();
			break;
		    }
		}
		next.show();
		parent.validate();
	    }
	}
    }
    
    /**
     * Returns a string representation of the state of this card layout.
     * @return    a string representation of this card layout.
     * @since     JDK1.0
     */
    public String toString() {
	return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + "]";
    }

}
