/*
 * @(#)Menu.java	1.41 98/08/21
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
import java.awt.peer.MenuPeer;
import java.awt.event.KeyEvent;

/**
 * A <code>Menu</code> object is a pull-down menu component 
 * that is deployed from a menu bar. 
 * <p>
 * A menu can optionally be a <i>tear-off</i> menu. A tear-off menu 
 * can be opened and dragged away from its parent menu bar or menu. 
 * It remains on the screen after the mouse button has been released. 
 * The mechanism for tearing off a menu is platform dependent, since 
 * the look and feel of the tear-off menu is determined by its peer.
 * On platforms that do not support tear-off menus, the tear-off
 * property is ignored.
 * <p>
 * Each item in a menu must belong to the <code>MenuItem</code> 
 * class. It can be an instance of <code>MenuItem</code>, a submenu 
 * (an instance of <code>Menu</code>), or a check box (an instance of 
 * <code>CheckboxMenuItem</code>).
 *
 * @version 1.41, 08/21/98
 * @author Sami Shaio
 * @see     java.awt.MenuItem
 * @see     java.awt.CheckboxMenuItem
 * @since   JDK1.0
 */
public class Menu extends MenuItem implements MenuContainer {
    Vector		items = new Vector();
    boolean		tearOff;
    boolean		isHelpMenu;

    private static final String base = "menu";
    private static int nameCounter = 0;

    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = -8809584163345499784L;

    /** 
     * Constructs a new menu with an empty label. This menu is not
     * a tear-off menu.
     * @since      JDK1.1
     */
    public Menu() {
	this("", false);
    }

    /** 
     * Constructs a new menu with the specified label. This menu is not
     * a tear-off menu.
     * @param       label the menu's label in the menu bar, or in 
     *                   another menu of which this menu is a submenu.
     * @since       JDK1.0
     */
    public Menu(String label) {
	this(label, false);
    }

    /** 
     * Constructs a new menu with the specified label. If the 
     * value of <code>tearOff</code> is <code>true</code>,
     * the menu can be torn off.
     * <p>
     * Tear-off functionality may not be supported by all 
     * implementations of AWT.  If a particular implementation doesn't 
     * support tear-off menus, this value is silently ignored.
     * @param       label the menu's label in the menu bar, or in 
     *                   another menu of which this menu is a submenu.
     * @param       tearOff   if <code>true</code>, the menu 
     *                   is a tear-off menu.
     * @since       JDK1.0.
     */
    public Menu(String label, boolean tearOff) {
	super(label);
	this.tearOff = tearOff;
    }

    /**
     * Construct a name for this MenuComponent.  Called by getName() when
     * the name is null.
     */
    String constructComponentName() {
	return base + nameCounter++;
    }

    /**
     * Creates the menu's peer.  The peer allows us to modify the 
     * appearance of the menu without changing its functionality.
     */
    public void addNotify() {
        synchronized (getTreeLock()) {
	    if (peer == null) {
	        peer = Toolkit.getDefaultToolkit().createMenu(this);
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
     * Removes the menu's peer.  The peer allows us to modify the appearance
     * of the menu without changing its functionality.
     */
    public void removeNotify() {
        synchronized (getTreeLock()) {
	    int nitems = getItemCount();
	    for (int i = 0 ; i < nitems ; i++) {
	        getItem(i).removeNotify();
	    }
	    super.removeNotify();
        }
    }

    /**
     * Indicates whether this menu is a tear-off menu.  
     * <p>
     * Tear-off functionality may not be supported by all 
     * implementations of AWT.  If a particular implementation doesn't 
     * support tear-off menus, this value is silently ignored.
     * @return      <code>true</code> if this is a tear-off menu; 
     *                         <code>false</code> otherwise.
     * @since       JDK1.0
     */
    public boolean isTearOff() {
	return tearOff;
    }

    /** 
      * Get the number of items in this menu.
      * @return     the number of items in this menu.
      * @since      JDK1.1
      */
    public int getItemCount() {
	return countItems();
    }

    /** 
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getItemCount()</code>.
     */
    public int countItems() {
	return items.size();
    }

    /**
     * Gets the item located at the specified index of this menu.
     * @param     index the position of the item to be returned.
     * @return    the item located at the specified index.
     * @since     JDK1.0
     */
    public MenuItem getItem(int index) {
	return (MenuItem)items.elementAt(index);
    }

    /**
     * Adds the specified menu item to this menu. If the 
     * menu item has been part of another menu, remove it  
     * from that menu. 
     * @param       mi   the menu item to be added.
     * @return      the menu item added.
     * @see         java.awt.Menu#insert(java.lang.String, int)
     * @see         java.awt.Menu#insert(java.awt.MenuItem, int)
     * @since       JDK1.0
     */
    public MenuItem add(MenuItem mi) {
        synchronized (getTreeLock()) {
	    if (mi.parent != null) {
	        mi.parent.remove(mi);
	    }
	    items.addElement(mi);
	    mi.parent = this;
    	    MenuPeer peer = (MenuPeer)this.peer;
	    if (peer != null) {
	        mi.addNotify();
	        peer.addItem(mi);
	    }
	    return mi;
        }
    }

    /**
     * Adds an item with the specified label to this menu. 
     * @param       label   the text on the item.
     * @see         java.awt.Menu#insert(java.lang.String, int)
     * @see         java.awt.Menu#insert(java.awt.MenuItem, int)
     * @since       JDK1.0
     */
    public void add(String label) {
	add(new MenuItem(label));
    }

    /**
     * Inserts a menu item into this menu 
     * at the specified position.
     * @param         menuitem  the menu item to be inserted.
     * @param         index     the position at which the menu  
     *                          item should be inserted.
     * @see           java.awt.Menu#add(java.lang.String)
     * @see           java.awt.Menu#add(java.awt.MenuItem)
     * @exception     IllegalArgumentException if the value of
     *                    <code>index</code> is less than zero.
     * @since         JDK1.1
     */

    public void insert(MenuItem menuitem, int index) {
        synchronized(getTreeLock()) {
	    if (index < 0) {
	        throw new IllegalArgumentException("index less than zero.");
	    }

            int nitems = getItemCount();
	    Vector tempItems = new Vector();

	    /* Remove the item at index, nitems-index times 
	       storing them in a temporary vector in the
	       order they appear on the menu.
	     */
	    for (int i = index ; i < nitems; i++) {
	        tempItems.addElement(getItem(index));
	        remove(index);
	    }

	    add(menuitem);

	    /* Add the removed items back to the menu, they are
	       already in the correct order in the temp vector.
	     */
	    for (int i = 0; i < tempItems.size()  ; i++) {
	        add((MenuItem)tempItems.elementAt(i));
	    }
        }
    }

    /**
     * Inserts a menu item with the specified label into this menu 
     * at the specified position.
     * @param       label the text on the item.
     * @param       index the position at which the menu item 
     *                      should be inserted.
     * @see         java.awt.Menu#add(java.lang.String)
     * @see         java.awt.Menu#add(java.awt.MenuItem)
     * @since       JDK1.1
     */

    public void insert(String label, int index) {
        insert(new MenuItem(label), index);
    }
      
    /**
     * Adds a separator line, or a hypen, to the menu at the current position.
     * @see         java.awt.Menu#insertSeparator(int)
     * @since       JDK1.0
     */
    public void addSeparator() {
	add("-");
    }

    /**
     * Inserts a separator at the specified position.
     * @param       index the position at which the 
     *                       menu separator should be inserted.
     * @exception   IllegalArgumentException if the value of 
     *                       <code>index</code> is less than 0.
     * @see         java.awt.Menu#addSeparator
     * @since       JDK1.1
     */

    public void insertSeparator(int index) {
        synchronized(getTreeLock()) {
	    if (index < 0) {
	        throw new IllegalArgumentException("index less than zero.");
	    }

            int nitems = getItemCount();
	    Vector tempItems = new Vector();

	    /* Remove the item at index, nitems-index times 
	       storing them in a temporary vector in the
	       order they appear on the menu.
	       */
	    for (int i = index ; i < nitems; i++) {
	        tempItems.addElement(getItem(index));
	        remove(index);
	    }

	    addSeparator();

	    /* Add the removed items back to the menu, they are
	       already in the correct order in the temp vector.
	       */
	    for (int i = 0; i < tempItems.size()  ; i++) {
	        add((MenuItem)tempItems.elementAt(i));
	    }
        }
    }

    /**
     * Removes the menu item at the specified index from this menu.
     * @param       index the position of the item to be removed. 
     * @since       JDK1.0 
     */
    public void remove(int index) {
        synchronized (getTreeLock()) {
	    MenuItem mi = getItem(index);
	    items.removeElementAt(index);
    	    MenuPeer peer = (MenuPeer)this.peer;
	    if (peer != null) {
	        mi.removeNotify();
	        mi.parent = null;
	        peer.delItem(index);
	    }
        }
    }

    /**
     * Removes the specified menu item from this menu.
     * @param       item the item to be removed from the menu
     * @since       JDK1.0
     */
    public void remove(MenuComponent item) {
        synchronized(getTreeLock()) {
	    int index = items.indexOf(item);
	    if (index >= 0) {
	         remove(index);
	    }
        }
    }

    /**
     * Removes all items from this menu.
     * @since       JDK1.0.
     */
    public void removeAll() {
      synchronized(getTreeLock()) {
        int nitems = getItemCount();
	for (int i = 0 ; i < nitems ; i++) {
	    remove(0);
	}
      }
    }

    /*
     * Post an ActionEvent to the target of the MenuPeer 
     * associated with the specified keyboard event (on 
     * keydown).  Returns true if there is an associated 
     * keyboard event.
     */
    boolean handleShortcut(KeyEvent e) {
        int nitems = getItemCount();
        for (int i = 0 ; i < nitems ; i++) {
            MenuItem mi = getItem(i);
            if (mi.handleShortcut(e)) {
                return true;
            }
        }
        return false;
    }

    MenuItem getShortcutMenuItem(MenuShortcut s) {
	int nitems = getItemCount();
	for (int i = 0 ; i < nitems ; i++) {
            MenuItem mi = getItem(i).getShortcutMenuItem(s);
            if (mi != null) {
                return mi;
            }
	}
        return null;
    }

    synchronized Enumeration shortcuts() {
        Vector shortcuts = new Vector();
        int nitems = getItemCount();
	for (int i = 0 ; i < nitems ; i++) {
            MenuItem mi = getItem(i);
            if (mi instanceof Menu) {
                Enumeration e = ((Menu)mi).shortcuts();
                while (e.hasMoreElements()) {
                    shortcuts.addElement(e.nextElement());
                }
            } else {
                MenuShortcut ms = mi.getShortcut();
                if (ms != null) {
                    shortcuts.addElement(ms);
                }
            }
	}
        return shortcuts.elements();
    }

    void deleteShortcut(MenuShortcut s) {
	int nitems = getItemCount();
	for (int i = 0 ; i < nitems ; i++) {
	    getItem(i).deleteShortcut();
	}
    }


    /* Serialization support.  A MenuContainer is responsible for
     * restoring the parent fields of its children. 
     */

    private int menuSerializedDataVersion = 1;

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
      for(int i = 0; i < items.size(); i++) {
	MenuItem item = (MenuItem)items.elementAt(i);
	item.parent = this;
      }
    }

    /**
     * Gets the parameter string representing the state of this menu. 
     * This string is useful for debugging.
     * @since      JDK1.0nu.
     */
    public String paramString() {
        String str = ",tearOff=" + tearOff+",isHelpMenu=" + isHelpMenu;
        return super.paramString() + str;
    }
}

