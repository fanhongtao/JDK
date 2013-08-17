/*
 * @(#)CardLayout.java	1.19 96/12/23
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
import java.util.Enumeration;

/**
 * A layout manager for a container that contains several
 * 'cards'. Only one card is visible at a time,
 * allowing you to flip through the cards.
 *
 * @version 	1.19 12/23/96
 * @author 	Arthur van Hoff
 */

public class CardLayout implements LayoutManager2,
				   java.io.Serializable {
    Hashtable tab = new Hashtable();
    int hgap;
    int vgap;

    /**
     * Creates a new card layout with gaps of size zero.
     */
    public CardLayout() {
	this(0, 0);
    }

    /**
     * Creates a card layout with the specified gaps.
     * @param hgap the horizontal gap
     * @param vgap the vertical gap
     */
    public CardLayout(int hgap, int vgap) {
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
	if (constraints instanceof String) {
	    addLayoutComponent((String)constraints, comp);
	} else {
	    throw new IllegalArgumentException("cannot add to layout: constraint must be a string");
	}
    }

    /**
     * Replaced by addLayoutComponent(Component, Object).
     * @deprecated
     */
    public void addLayoutComponent(String name, Component comp) {
	if (tab.size() > 0) {
	    comp.hide();
	}
	tab.put(name, comp);
    }

    /**
     * Removes the specified component from the layout.
     * @param comp the component to be removed
     */
    public void removeLayoutComponent(Component comp) {
	for (Enumeration e = tab.keys() ; e.hasMoreElements() ; ) {
	    String key = (String)e.nextElement();
	    if (tab.get(key) == comp) {
		tab.remove(key);
		return;
	    }
	}
    }

    /** 
     * Calculates the preferred size for the specified panel.
     * @param parent the name of the parent container
     * @return the dimensions of this panel. 
     * @see #minimumLayoutSize
     */
    public Dimension preferredLayoutSize(Container parent) {
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

    /** 
     * Calculates the minimum size for the specified panel.
     * @param parent the name of the parent container
     * @return the dimensions of this panel. 
     * @see #preferredLayoutSize
     */
    public Dimension minimumLayoutSize(Container parent) {
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
     * Performs a layout in the specified panel.
     * @param parent the name of the parent container 
     */
    public void layoutContainer(Container parent) {
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
     * Flip to the first card.
     * @param parent the name of the parent container
     */
    public void first(Container parent) {
	synchronized (Component.LOCK) {
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
     * Flips to the next card of the specified container.
     * @param parent the name of the container
     */
    public void next(Container parent) {
	synchronized (Component.LOCK) {
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
     * Flips to the previous card of the specified container.
     * @param parent the name of the parent container
     */
    public void previous(Container parent) {
	synchronized (Component.LOCK) {
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
     * Flips to the last card of the specified container.
     * @param parent the name of the parent container
     */
    public void last(Container parent) {
	synchronized (Component.LOCK) {
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
     * Flips to the specified component name in the specified container.
     * @param parent the name of the parent container
     * @param name the component name
     */
    public void show(Container parent, String name) {
	synchronized (Component.LOCK) {
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
     * Returns the String representation of this CardLayout's values.
     */
    public String toString() {
	return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + "]";
    }
}
