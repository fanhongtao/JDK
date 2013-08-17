/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt;

/** 
 * Defines the interface for classes that know how to layout Containers.
 *
 * @see Container
 *
 * @version	1.22, 02/06/02
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 */
public interface LayoutManager {
    /**
     * Adds the specified component with the specified name to
     * the layout.
     * @param name the component name
     * @param comp the component to be added
     */
    void addLayoutComponent(String name, Component comp);

    /**
     * Removes the specified component from the layout.
     * @param comp the component to be removed
     */
    void removeLayoutComponent(Component comp);

    /**
     * Calculates the preferred size dimensions for the specified 
     * panel given the components in the specified parent container.
     * @param parent the component to be laid out
     *  
     * @see #minimumLayoutSize
     */
    Dimension preferredLayoutSize(Container parent);

    /** 
     * Calculates the minimum size dimensions for the specified 
     * panel given the components in the specified parent container.
     * @param parent the component to be laid out
     * @see #preferredLayoutSize
     */
    Dimension minimumLayoutSize(Container parent);

    /** 
     * Lays out the container in the specified panel.
     * @param parent the component which needs to be laid out 
     */
    void layoutContainer(Container parent);
}
