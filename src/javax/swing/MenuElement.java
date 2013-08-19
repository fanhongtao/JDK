/*
 * @(#)MenuElement.java	1.10 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;

import java.awt.*;
import java.awt.event.*;

/**
 * Any component that can be placed into a menu should implement this interface.
 * This interface is used by MenuSelection to handle selection and navigation in
 * menu hierarchies.
 *
 * @version 1.10 01/23/03
 * @author Arnaud Weber
 */

public interface MenuElement {
    
    /**
     * Process a mouse event. event is a MouseEvent with source being the receiving element's component.
     * path is the path of the receiving element in the menu
     * hierarchy including the receiving element itself.
     * manager is the MenuSelectionManager for the menu hierarchy.
     * This method should process the MouseEvent and change the menu selection if necessary
     * by using MenuSelectionManager's API
     * Note: you do not have to forward the event to sub-components. This is done automatically
     * by the MenuSelectionManager
     */
    public void processMouseEvent(MouseEvent event,MenuElement path[],MenuSelectionManager manager);


    /**
     *  Process a key event. 
     */
    public void processKeyEvent(KeyEvent event,MenuElement path[],MenuSelectionManager manager);

    /**
     * Call by the MenuSelection when the MenuElement is added or remove from 
     * the menu selection.
     */
    public void menuSelectionChanged(boolean isIncluded);

    /**
     * This method should return an array containing the sub-elements for the receiving menu element
     *
     * @return an array of MenuElements
     */
    public MenuElement[] getSubElements();
    
    /**
     * This method should return the java.awt.Component used to paint the receiving element.
     * The returned component will be used to convert events and detect if an event is inside
     * a MenuElement's component.
     *
     * @return the Component value
     */
    public Component getComponent();
}

