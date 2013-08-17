/*
 * @(#)Menu.java	1.37 97/05/05
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
import java.awt.peer.MenuPeer;
import java.awt.event.KeyEvent;

/**
 * A Menu that is a component of a menu bar.
 *
 * @version 1.37, 05/05/97
 * @author Sami Shaio
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
     * Constructs a new Menu with an empty label.  This menu can
     * not be torn off.
     */
    public Menu() {
	this("", false);
    }

    /** 
     * Constructs a new Menu with the specified label.  This menu can
     * not be torn off.

     * @param label the label to be added to this menu 
     */
    public Menu(String label) {
	this(label, false);
    }

    /** 
     * Constructs a new Menu with the specified label. If tearOff is
     * true, the menu can be torn off - the menu will then be displayed
     * in a separate native dialog.
     *
     * NOTE:  tear-off functionality may not be supported by all AWT
     * implementations.  If a particular implementation doesn't support
     * tear-offs, this value will be silently ignored.
     *
     * @param label the label to be added to this menu
     * @param tearOff the boolean indicating whether or not the menu will be
     * able to be torn off.
     */
    public Menu(String label, boolean tearOff) {
	super(label);
	this.name = base + nameCounter++;
	this.tearOff = tearOff;
    }

    /**
     * Creates the menu's peer.  The peer allows us to modify the 
     * appearance of the menu without changing its functionality.
     */
    public void addNotify() {
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

    /**
     * Removes the menu's peer.  The peer allows us to modify the appearance
     * of the menu without changing its functionality.
     */
    public void removeNotify() {
	int nitems = getItemCount();
	for (int i = 0 ; i < nitems ; i++) {
	    getItem(i).removeNotify();
	}
	super.removeNotify();
    }

    /**
     * Returns true if this is a tear-off menu.  
     *
     * NOTE:  tear-off functionality may not be supported by all AWT
     * implementations.  If a particular implementation doesn't support
     * tear-offs, this value will be silently ignored.
     */
    public boolean isTearOff() {
	return tearOff;
    }

    /** 
      * Returns the number of elements in this menu.
      */
    public int getItemCount() {
	return countItems();
    }

    /** 
     * @deprecated As of JDK version 1.1,
     * replaced by getItemCount().
     */
    public int countItems() {
	return items.size();
    }

    /**
     * Returns the item located at the specified index of this menu.
     * @param index the position of the item to be returned
     */
    public MenuItem getItem(int index) {
	return (MenuItem)items.elementAt(index);
    }

    /**
     * Adds the specified item to this menu.
     * @param mi the item to be added
     */
    public synchronized MenuItem add(MenuItem mi) {
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

    /**
     * Adds an item with with the specified label to this menu.
     * @param label the text on the item
     */
    public void add(String label) {
	add(new MenuItem(label));
    }

    /**
     * Inserts the MenuItem to this menu at the specified position.
     * @param menuitem the menu item to be inserted
     * @param index the position at which the menu item should be inserted
     * @exception IllegalArgumentException if index is less than 0.
     */

    public synchronized void insert(MenuItem menuitem, int index) {
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

    /**
     * Inserts an item with the specified label to this menu 
     * at the specified position.
     * @param label the text on the item
     * @param index the position at which the menu item should be inserted
     */

    public void insert(String label, int index) {
        insert(new MenuItem(label), index);
    }
      
    /**
     * Adds a separator line, or a hypen, to the menu at the current position.
     */
    public void addSeparator() {
	add("-");
    }

    /**
     * Inserts a separator at the specified position
     * @param index the position at which the menu separator should be inserted
     * @exception IllegalArgumentException if index is less than 0.
     */

    public void insertSeparator(int index) {
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

    /**
     * Deletes the item from this menu at the specified index.
     * @param index the position of the item to be removed 
     */
    public synchronized void remove(int index) {
	MenuItem mi = getItem(index);
	items.removeElementAt(index);
    	MenuPeer peer = (MenuPeer)this.peer;
	if (peer != null) {
	    mi.removeNotify();
	    mi.parent = null;
	    peer.delItem(index);
	}
    }

    /**
     * Deletes the specified item from this menu.
     * @param item the item to be removed from the menu
     */
    public synchronized void remove(MenuComponent item) {
	int index = items.indexOf(item);
	if (index >= 0) {
	    remove(index);
	}
    }

    /**
     * Deletes all items from this menu.
     */
    public synchronized void removeAll() {
        int nitems = getItemCount();
	for (int i = 0 ; i < nitems ; i++) {
	    remove(0);
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
     * Returns the String parameter of the menu.
     */
    public String paramString() {
        String str = ",tearOff=" + tearOff+",isHelpMenu=" + isHelpMenu;
        return super.paramString() + str;
    }
}

