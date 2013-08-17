/*
 * @(#)MenuBar.java	1.38 98/08/21
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

import java.util.Vector;
import java.util.Enumeration;
import java.awt.peer.MenuBarPeer;
import java.awt.event.KeyEvent;

/**
 * The <code>MenuBar</code> class encapsulates the platform's 
 * concept of a menu bar bound to a frame. In order to associate 
 * the menu bar with a <code>Frame</code> object, call the 
 * frame's <code>setMenuBar</code> method.
 * <p>
 * <A NAME="mbexample"></A><!-- target for cross references -->
 * This is what a menu bar might look like:
 * <p>
 * <img src="images-awt/MenuBar-1.gif" 
 * ALIGN=center HSPACE=10 VSPACE=7>
 * <p>
 * A menu bar handles keyboard shortcuts for menu items, passing them 
 * along to its child menus. 
 * (Keyboard shortcuts, which are optional, provide the user with
 * an alternative to the mouse for invoking a menu item and the
 * action that is associated with it.)
 * Each menu item can maintain an instance of <code>MenuShortcut</code>. 
 * The <code>MenuBar</code> class defines several methods, 
 * <A HREF="#shortcuts"><code>shortCuts</code></A> and 
 * <A HREF="#getShortcutMenuItem"><code>getShortcutMenuItem</code></A> 
 * that retrieve information about the shortcuts a given
 * menu bar is managing.
 *
 * @version 1.38, 08/21/98
 * @author Sami Shaio
 * @see        java.awt.Frame
 * @see        java.awt.Frame#setMenuBar(java.awt.MenuBar)
 * @see        java.awt.Menu
 * @see        java.awt.MenuItem
 * @see        java.awt.MenuShortcut
 * @since      JDK1.0
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
     * @since    JDK1.0
     */
    public MenuBar() {
    }

    /**
     * Construct a name for this MenuComponent.  Called by getName() when
     * the name is null.
     */
    String constructComponentName() {
        return base + nameCounter++;
    }

    /**
     * Creates the menu bar's peer.  The peer allows us to change the 
     * appearance of the menu bar without changing any of the menu bar's 
     * functionality.
     */
    public void addNotify() {
        synchronized (getTreeLock()) {
           if (peer == null) 
				peer = Toolkit.getDefaultToolkit().createMenuBar(this);

            int nmenus = getMenuCount();
            for (int i = 0 ; i < nmenus ; i++) {
                getMenu(i).addNotify();
            }
        }
    }

    /**
     * Removes the menu bar's peer.  The peer allows us to change the 
     * appearance of the menu bar without changing any of the menu bar's 
     * functionality.
     */
    public void removeNotify() {
        synchronized (getTreeLock()) {
            int nmenus = getMenuCount();
            for (int i = 0 ; i < nmenus ; i++) {
                getMenu(i).removeNotify();
            }
            super.removeNotify();
        }
    }

    /**
     * Gets the help menu on the menu bar.
     * @return    the help menu on this menu bar.
     * @since     JDK1.0
     */
    public Menu getHelpMenu() {
        return helpMenu;
    }

    /**
     * Sets the help menu on this menu bar to be the specified menu.
     * @param     m    the menu to be set as the help menu.
     * @since     JDK1.0
     */
    public void setHelpMenu(Menu m) {
        synchronized (getTreeLock()) {
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
    }

    /**
     * Adds the specified menu to the menu bar.
     * @param        m   the menu to be added.
     * @return       the menu added.
     * @see          java.awt.MenuBar#remove(int)
     * @see          java.awt.MenuBar#remove(java.awt.MenuComponent)
     * @since        JDK1.0
     */
    public Menu add(Menu m) {
        synchronized (getTreeLock()) {
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
    }

    /**
     * Removes the menu located at the specified 
     * index from this menu bar. 
     * @param        index   the position of the menu to be removed.
     * @see          java.awt.MenuBar#add(java.awt.Menu)
     * @since        JDK1.0
     */
    public void remove(int index) {
        synchronized (getTreeLock()) {
	    MenuBarPeer peer = (MenuBarPeer)this.peer;
	    if (peer != null) {
	        Menu m = getMenu(index);
	        m.removeNotify();
	        m.parent = null;
	        peer.delMenu(index);
	    }
	    menus.removeElementAt(index);
        }
    }

    /**
     * Removes the specified menu component from this menu bar.
     * @param        m the menu component to be removed.
     * @see          java.awt.MenuBar#add(java.awt.Menu)
     * @since        JDK1.0
     */
    public void remove(MenuComponent m) {
        synchronized(getTreeLock()) {
	    int index = menus.indexOf(m);
	    if (index >= 0) {
	        remove(index);
	    }
        }
    }

    /**
     * Gets the number of menus on the menu bar.
     * @return     the number of menus on the menu bar.
     * @since      JDK1.1
     */
    public int getMenuCount() {
        return countMenus();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getMenuCount()</code>.
     */
    public int countMenus() {
        return menus.size();
    }

    /**
     * Gets the specified menu.
     * @param      i the index position of the menu to be returned.
     * @return     the menu at the specified index of this menu bar.
     * @since      JDK1.0
     */
    public Menu getMenu(int i) {
        return (Menu)menus.elementAt(i);
    }

    /** 
     * Gets an enumeration of all menu shortcuts this menu bar 
     * is managing.  
     * @return      an enumeration of menu shortcuts that this
     *                      menu bar is managing.
     * @see         java.awt.MenuShortcut
     * @since       JDK1.1
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
     * Gets the instance of <code>MenuItem</code> associated 
     * with the specified <code>MenuShortcut</code> object,
     * or <code>null</code> if none has been specified.
     * @param        s the specified menu shortcut.
     * @see          java.awt.MenuItem
     * @see          java.awt.MenuShortcut
     * @since        JDK1.1
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
     * Deletes the specified menu shortcut.
     * @param     s the menu shortcut to delete.
     * @since     JDK1.1
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
