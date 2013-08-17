/*
 * @(#)MenuBar.java	1.32 97/01/27
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

import java.util.Vector;
import java.util.Enumeration;
import java.awt.peer.MenuBarPeer;
import java.awt.event.KeyEvent;

/**
 * A class that encapsulates the platform's concept of a menu bar bound
 * to a Frame. In order to associate the MenuBar with an actual Frame,
 * the Frame.setMenuBar() method should be called.
 *
 * @see Frame#setMenuBar
 *
 * @version 1.32, 01/27/97
 * @author Sami Shaio
 *
 */
public class MenuBar extends MenuComponent implements MenuContainer {
    Vector menus = new Vector();
    Menu helpMenu;

    private static final String base = "menubar";
    private static int nameCounter = 0;

    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = -4930327919388951260L;

    /**
     * Creates a new menu bar.
     */
    public MenuBar() {
        this.name = base + nameCounter++;
    }

    /**
     * Creates the menu bar's peer.  The peer allows us to change the 
     * appearance of the menu bar without changing any of the menu bar's 
     * functionality.
     */
    public void addNotify() {
	peer = Toolkit.getDefaultToolkit().createMenuBar(this);

	int nmenus = getMenuCount();
	for (int i = 0 ; i < nmenus ; i++) {
	    getMenu(i).addNotify();
	}
    }

    /**
     * Removes the menu bar's peer.  The peer allows us to change the 
     * appearance of the menu bar without changing any of the menu bar's 
     * functionality.
     */
    public void removeNotify() {
	int nmenus = getMenuCount();
	for (int i = 0 ; i < nmenus ; i++) {
	    getMenu(i).removeNotify();
	}
	super.removeNotify();
    }

    /**
     * Gets the help menu on the menu bar.
     */
    public Menu getHelpMenu() {
	return helpMenu;
    }

    /**
     * Sets the help menu to the specified menu on the menu bar.
     * @param m the menu to be set
     */
    public synchronized void setHelpMenu(Menu m) {
	if (helpMenu == m) {
	    return;
	}
	if (helpMenu != null) {
	    helpMenu.removeNotify();
	    helpMenu.parent = null;
	}
	if (m.parent != this) {
	    add(m);
	}
	helpMenu = m;
	if (m != null) {
	    m.isHelpMenu = true;
	    m.parent = this;
	    MenuBarPeer peer = (MenuBarPeer)this.peer;
	    if (peer != null) {
		if (m.peer == null) {
		    m.addNotify();
		}
		peer.addHelpMenu(m);
	    }
	}
    }

    /**
     * Adds the specified menu to the menu bar.
     * @param m the menu to be added to the menu bar
     */
    public synchronized Menu add(Menu m) {
	if (m.parent != null) {
	    m.parent.remove(m);
	}
	menus.addElement(m);
	m.parent = this;

	MenuBarPeer peer = (MenuBarPeer)this.peer;
	if (peer != null) {
	    if (m.peer == null) {
		m.addNotify();
	    }
	    peer.addMenu(m);
	}
	return m;
    }

    /**
     * Removes the menu located at the specified index from the menu bar.
     * @param index the position of the menu to be removed
     */
    public synchronized void remove(int index) {
	MenuBarPeer peer = (MenuBarPeer)this.peer;
	if (peer != null) {
	    Menu m = getMenu(index);
	    m.removeNotify();
	    m.parent = null;
	    peer.delMenu(index);
	}
	menus.removeElementAt(index);
    }

    /**
     * Removes the specified menu from the menu bar.
     * @param m the menu to be removed
     */
    public synchronized void remove(MenuComponent m) {
	int index = menus.indexOf(m);
	if (index >= 0) {
	    remove(index);
	}
    }

    /**
     * Counts the number of menus on the menu bar.
     */
    public int getMenuCount() {
	return countMenus();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by getMenuCount().
     */
    public int countMenus() {
	return menus.size();
    }

    /**
     * Gets the specified menu.
     * @param i the menu to be returned
     */
    public Menu getMenu(int i) {
	return (Menu)menus.elementAt(i);
    }

    /** 
     * Get an Enumeration of all MenuShortcuts this MenuBar manages.
     */
    public synchronized Enumeration shortcuts() {
        Vector shortcuts = new Vector();
	int nmenus = getMenuCount();
	for (int i = 0 ; i < nmenus ; i++) {
            Enumeration e = getMenu(i).shortcuts();
            while (e.hasMoreElements()) {
                shortcuts.addElement(e.nextElement());
            }
	}
        return shortcuts.elements();
    }

    /**
     * Return the MenuItem associated with a MenuShortcut,
     * or null if none has been specified.
     * @param s the MenuShortcut to search for
     */
     public MenuItem getShortcutMenuItem(MenuShortcut s) {
	int nmenus = getMenuCount();
	for (int i = 0 ; i < nmenus ; i++) {
            MenuItem mi = getMenu(i).getShortcutMenuItem(s);
            if (mi != null) {
                return mi;
            }
	}
        return null;  // MenuShortcut wasn't found
     }

    /*
     * Post an ACTION_EVENT to the target of the MenuPeer 
     * associated with the specified keyboard event (on 
     * keydown).  Returns true if there is an associated 
     * keyboard event.
     */
    boolean handleShortcut(KeyEvent e) {
        // Is it a key event?
        int id = e.getID();
        if (id != KeyEvent.KEY_PRESSED && id != KeyEvent.KEY_RELEASED) {
            return false;
        }

        // Is the accelerator modifier key pressed?
        int accelKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        if ((e.getModifiers() & accelKey) == 0) {
            return false;
        }

        // Pass MenuShortcut on to child menus.
	int nmenus = getMenuCount();
	for (int i = 0 ; i < nmenus ; i++) {
	    Menu m = getMenu(i);
            if (m.handleShortcut(e)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Delete the specified MenuShortcut.
     * @param s the MenuShortcut to delete
     */
    public void deleteShortcut(MenuShortcut s) {
	int nmenus = getMenuCount();
	for (int i = 0 ; i < nmenus ; i++) {
	    getMenu(i).deleteShortcut(s);
        }
    }

    /* Serialization support.  Restore the (transient) parent 
     * fields of Menubar menus here.
     */

    private int menuBarSerializedDataVersion = 1;

    private void writeObject(java.io.ObjectOutputStream s)
      throws java.lang.ClassNotFoundException,
	     java.io.IOException 
    {
      s.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream s)
      throws java.lang.ClassNotFoundException,
	     java.io.IOException 
    {
      s.defaultReadObject();
      for (int i = 0; i < menus.size(); i++) {
	Menu m = (Menu)menus.elementAt(i);
	m.parent = this;
      }
    }
}
