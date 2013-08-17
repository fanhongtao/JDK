/*
 * @(#)PopupMenu.java	1.11 98/10/12
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

import java.awt.peer.PopupMenuPeer;


/**
 * A class that implements a menu which can be dynamically popped up
 * at a specified position within a component.
 *
 * @version	1.11 10/12/98
 * @author 	Amy Fowler
 */
public class PopupMenu extends Menu {

    private static final String base = "popup";
    static int nameCounter = 0;

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = -4620452533522760060L;

    /**
     * Creates a new popup menu.
     */
    public PopupMenu() {
	this("");
    }

    /**
     * Creates a new popup menu with the specified name.
     * @param title the title string for the popup menu
     */
    public PopupMenu(String label) {
	super(label);
	this.name = base + nameCounter++;
    }

    /**
     * Creates the popup menu's peer.  The peer allows us to change the 
     * appearance of the popup menu without changing any of the popup menu's 
     * functionality.
     */
    public void addNotify() {
      synchronized (getTreeLock()) {
	if (peer == null) {
	    peer = Toolkit.getDefaultToolkit().createPopupMenu(this);
	}
	int nitems = getItemCount();
	for (int i = 0 ; i < nitems ; i++) {
	    MenuItem mi = getItem(i);
	    mi.parent = this;
	    mi.addNotify();
	}
      }
    }

   /**
     * Shows the popup menu at the x, y position relative to an origin component.
     * The origin component must be contained within the component hierarchy 
     * of the popup menu's parent.  Both the origin and the parent must be 
     * showing on the screen for this method to be valid.
     * @param origin the component which defines the coordinate space
     * @param x the x coordinate position to popup the menu
     * @param y the y coordinate position to popup the menu
     */
    public void show(Component origin, int x, int y) {
	Component p = (Component)parent;
	if (p == null) {
	    throw new NullPointerException("parent is null");
	}
	if (p != origin &&
	    p instanceof Container && !((Container)p).isAncestorOf(origin)) {
	    throw new IllegalArgumentException("origin not in parent's hierarchy");
	}
	if (p.getPeer() == null || !p.isShowing()) {
	    throw new RuntimeException("parent not showing on screen");
	}
	if (peer == null) {
	    addNotify();
	}
        synchronized (getTreeLock()) {
            if (peer != null) {
                ((PopupMenuPeer)peer).show(
                    new Event(origin, 0, Event.MOUSE_DOWN, x, y, 0, 0));
            }
        }
    }
}
