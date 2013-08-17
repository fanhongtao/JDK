/*
 * @(#)ItemListener.java	1.10 98/09/21
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
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
 * @see ItemSelectable 
 * @see ItemEvent
 * @see <a href="http://java.sun.com/docs/books/tutorial/post1.0/ui/itemlistener.html">Tutorial: Writing an Item Listener</a>
 * @see <a href="http://www.awl.com/cp/javaseries/jcl1_2.html">Reference: The Java Class Libraries (update file)</a>
 *
 * @version 1.10 09/21/98
 * @author Amy Fowler
 */
public interface ItemListener extends EventListener {

    /**
     * Invoked when an item has been selected or deselected.
     * The code written for this method performs the operations
     * that need to occur when an item is selected (or deselected).
     */    
    void itemStateChanged(ItemEvent e);

}
