/*
 * @(#)ItemListener.java	1.20 06/04/13
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.event;

import java.util.EventListener;

/**
 * The listener interface for receiving item events.
 * The class that is interested in processing an item event
 * implements this interface. The object created with that 
 * class is then registered with a component using the 
 * component's <code>addItemListener</code> method. When an
 * item-selection event occurs, the listener object's 
 * <code>itemStateChanged</code> method is invoked.
 *
 * @author Amy Fowler
 * @version 1.20 04/13/06
 *
 * @see java.awt.ItemSelectable 
 * @see ItemEvent
 * @see <a href="http://java.sun.com/docs/books/tutorial/post1.0/ui/itemlistener.html">Tutorial: Writing an Item Listener</a>
 *
 * @since 1.1
 */
public interface ItemListener extends EventListener {

    /**
     * Invoked when an item has been selected or deselected by the user.
     * The code written for this method performs the operations
     * that need to occur when an item is selected (or deselected).
     */    
    void itemStateChanged(ItemEvent e);

}
