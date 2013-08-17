/*
 * @(#)LayoutManager.java	1.15 98/07/01
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
 * Defines the interface for classes that know how to layout Containers.
 *
 * @see Container
 *
 * @version	1.15, 07/01/98
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
     * @param comp the component ot be removed
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
