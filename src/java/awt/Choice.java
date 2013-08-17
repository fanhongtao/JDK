/*
 * @(#)Choice.java	1.48 98/07/01
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

import java.util.*;
import java.awt.peer.ChoicePeer;
import java.awt.event.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * The <code>Choice</code> class presents a pop-up menu of choices. 
 * The current choice is displayed as the title of the menu. 
 * <p>
 * The following code example produces a pop-up menu: 
 * <p>
 * <hr><blockquote><pre>
 * Choice ColorChooser = new Choice();
 * ColorChooser.add("Green");
 * ColorChooser.add("Red");
 * ColorChooser.add("Blue");
 * </pre></blockquote><hr>
 * <p>
 * After this choice menu has been added to a panel, 
 * it appears as follows in its normal state:
 * <p>
 * <img src="images-awt/Choice-1.gif"
 * ALIGN=center HSPACE=10 VSPACE=7> 
 * <p>
 * In the picture, <code>"Green"</code> is the current choice. 
 * Pushing the mouse button down on the object causes a menu to 
 * appear with the current choice highlighted. 
 * <p>
 * @version	1.48 07/01/98
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 * @since       JDK1.0
 */
public class Choice extends Component implements ItemSelectable {
    /**
     * The items for the Choice.
     */
    Vector pItems;

    /** 
     * The index of the current choice for this Choice.
     */
    int selectedIndex = -1;

    transient ItemListener itemListener;

    private static final String base = "choice";
    private static int nameCounter = 0;

    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = -4075310674757313071L;

    /** 
     * Creates a new choice menu. The menu initially has no items in it. 
     * <p>
     * By default, the first item added to the choice menu becomes the 
     * selected item, until a different selection is made by the user  
     * by calling one of the <code>select</code> methods. 
     * @see       java.awt.Choice#select(int)
     * @see       java.awt.Choice#select(java.lang.String)
     * @since     JDK1.0
     */
    public Choice() {
	pItems = new Vector();
    }

    /**
     * Construct a name for this component.  Called by getName() when the
     * name is null.
     */
    String constructComponentName() {
        return base + nameCounter++;
    }

    /**
     * Creates the Choice's peer.  This peer allows us to change the look
     * of the Choice without changing its functionality.
     * @see     java.awt.Toolkit#createChoice(java.awt.Choice)
     * @see     java.awt.Component#getToolkit()
     * @since   JDK1.0
     */
    public void addNotify() {
        synchronized (getTreeLock()) {
		if (peer == null)
	    	peer = getToolkit().createChoice(this);
	    super.addNotify();
        }
    }

    /**
     * Returns the number of items in this <code>Choice</code> menu.
     * @see     java.awt.Choice#getItem
     * @since   JDK1.1
     */
    public int getItemCount() {
	return countItems();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getItemCount()</code>.
     */
    public int countItems() {
	return pItems.size();
    }

    /**
     * Gets the string at the specified index in this 
     * <code>Choice</code> menu.
     * @param      index the index at which to begin.
     * @see        java.awt.Choice#getItemCount
     * @since      JDK1.0
     */
    public String getItem(int index) {
	return (String)pItems.elementAt(index);
    }

    /**
     * Adds an item to this <code>Choice</code> menu.
     * @param      item    the item to be added
     * @exception  NullPointerException   if the item's value is <code>null</code>.
     * @since      JDK1.1
     */
    public void add(String item) {
	addItem(item);
    }

    /**
     * Adds an item to this Choice.
     * @param item the item to be added
     * @exception NullPointerException If the item's value is equal to null.
     */
    public synchronized void addItem(String item) {
	if (item == null) {
	    throw new NullPointerException("cannot add null item to Choice");
	}
	pItems.addElement(item);
	ChoicePeer peer = (ChoicePeer)this.peer;
	if (peer != null) {
	    peer.addItem(item, pItems.size() - 1);
	}
	if (selectedIndex < 0) {
	    select(0);
	}
    }


    /**
     * Inserts the item into this choice at the specified position.
     * @param item the item to be inserted
     * @param index the position at which the item should be inserted
     * @exception IllegalArgumentException if index is less than 0.
     */

    public synchronized void insert(String item, int index) {
	if (index < 0) {
	    throw new IllegalArgumentException("index less than zero.");
	}

        int nitems = getItemCount();
	Vector tempItems = new Vector();

	/* Remove the item at index, nitems-index times 
	   storing them in a temporary vector in the
	   order they appear on the choice menu.
	   */
	for (int i = index ; i < nitems; i++) {
	    tempItems.addElement(getItem(index));
	    remove(index);
	}

	add(item);

	/* Add the removed items back to the choice menu, they 
	   are already in the correct order in the temp vector.
	   */
	for (int i = 0; i < tempItems.size()  ; i++) {
	    add((String)tempItems.elementAt(i));
	}
    }

    /**
     * Remove the first occurrence of <code>item</code> 
     * from the <code>Choice</code> menu.
     * @param      item  the item to remove from this <code>Choice</code> menu.
     * @exception  IllegalArgumentException  if the item doesn't 
     *                     exist in the choice menu.
     * @since      JDK1.1
     */
    public synchronized void remove(String item) {
    	int index = pItems.indexOf(item);
    	if (index < 0) {
	    throw new IllegalArgumentException("item " + item +
					       " not found in choice");
	} else {
	    remove(index);
	}
    }

    /**
     * Removes an item from the choice menu 
     * at the specified position.
     * @param      position the position of the item.
     * @since      JDK1.1
     */
    public synchronized void remove(int position) {
    	pItems.removeElementAt(position);
    	ChoicePeer peer = (ChoicePeer)this.peer;
    	if (peer != null) {
	    peer.remove(position);
	}
    	/* Adjust selectedIndex if selected item was removed. */
    	if (pItems.size() == 0) {
	    selectedIndex = -1;
	} else if (selectedIndex == position) {
	    select(0);
	} else if (selectedIndex > position) {
	    select(selectedIndex-1);
	}
    }

    /**
     * Removes all items from the choice menu.
     * @see       java.awt.Choice#remove
     * @since     JDK1.1
     */
    public synchronized void removeAll() {
        int nitems = getItemCount();
	for (int i = 0 ; i < nitems ; i++) {
	    remove(0);
	}
    }

    /**
     * Gets a representation of the current choice as a string.
     * @return    a string representation of the currently 
     *                     selected item in this choice menu.
     * @see       java.awt.Choice#getSelectedIndex
     * @since     JDK1.0
     */
    public synchronized String getSelectedItem() {
	return (selectedIndex >= 0) ? getItem(selectedIndex) : null;
    }

    /**
     * Returns an array (length 1) containing the currently selected
     * item.  If this choice has no items, returns null.
     * @see ItemSelectable
     */
    public synchronized Object[] getSelectedObjects() {
	if (selectedIndex >= 0) {
            Object[] items = new Object[1];
            items[0] = getItem(selectedIndex);
            return items;
        }
        return null;
    }

    /**
     * Returns the index of the currently selected item.
     * @see #getSelectedItem
     */
    public int getSelectedIndex() {
	return selectedIndex;
    }

    /**
     * Sets the selected item in this <code>Choice</code> menu to be the 
     * item at the specified position. 
     * @param      pos      the positon of the selected item.
     * @exception  IllegalArgumentException if the specified
     *                            position is invalid.
     * @see        java.awt.Choice#getSelectedItem
     * @see        java.awt.Choice#getSelectedIndex
     * @since      JDK1.0
     */
    public void select(int pos) {
	if (pos >= pItems.size()) {
	    throw new IllegalArgumentException("illegal Choice item position: " + pos);
	}
	if (pItems.size() > 0) {
	    selectedIndex = pos;
	    ChoicePeer peer = (ChoicePeer)this.peer;
	    if (peer != null) {
		peer.select(pos);
	    }
	}
    }

    /**
     * Sets the selected item in this <code>Choice</code> menu 
     * to be the item whose name is equal to the specified string. 
     * If more than one item matches (is equal to) the specified string, 
     * the one with the smallest index is selected. 
     * @param       str     the specified string
     * @see         java.awt.Choice#getSelectedItem
     * @see         java.awt.Choice#getSelectedIndex
     * @since       JDK1.0
     */
    public synchronized void select(String str) {
	int index = pItems.indexOf(str);
	if (index >= 0) {
	    select(index);
	}
    }

    /**
     * Adds the specified item listener to receive item events from
     * this <code>Choice</code> menu.
     * @param         l    the item listener.
     * @see           java.awt.event.ItemEvent
     * @see           java.awt.event.ItemListener
     * @see           java.awt.Choice#removeItemListener
     * @since         JDK1.1
     */ 
    public synchronized void addItemListener(ItemListener l) {
        itemListener = AWTEventMulticaster.add(itemListener, l);
        newEventsOnly = true;
    }

    /**
     * Removes the specified item listener so that it no longer receives 
     * item events from this <code>Choice</code> menu. 
     * @param         l    the item listener.
     * @see           java.awt.event.ItemEvent
     * @see           java.awt.event.ItemListener
     * @see           java.awt.Choice#addItemListener
     * @since         JDK1.1
     */ 
    public synchronized void removeItemListener(ItemListener l) {
        itemListener = AWTEventMulticaster.remove(itemListener, l);
    }

    // REMIND: remove when filtering is done at lower level
    boolean eventEnabled(AWTEvent e) {
        if (e.id == ItemEvent.ITEM_STATE_CHANGED) {
            if ((eventMask & AWTEvent.ITEM_EVENT_MASK) != 0 ||
                itemListener != null) {
                return true;
            } 
            return false;
        }
        return super.eventEnabled(e);
    }        

    /**
     * Processes events on this choice. If the event is an 
     * instance of <code>ItemEvent</code>, it invokes the 
     * <code>processItemEvent</code> method. Otherwise, it calls its
     * superclass's <code>processEvent</code> method.
     * @param      e the event.
     * @see        java.awt.event.ItemEvent
     * @see        java.awt.Choice#processItemEvent
     * @since      JDK1.1
     */
    protected void processEvent(AWTEvent e) {
        if (e instanceof ItemEvent) {
            processItemEvent((ItemEvent)e);
            return;
        }
	super.processEvent(e);
    }

    /** 
     * Processes item events occurring on this <code>Choice</code> 
     * menu by dispatching them to any registered 
     * <code>ItemListener</code> objects. 
     * <p>
     * This method is not called unless item events are 
     * enabled for this component. Item events are enabled 
     * when one of the following occurs:
     * <p><ul>
     * <li>An <code>ItemListener</code> object is registered 
     * via <code>addItemListener</code>.
     * <li>Item events are enabled via <code>enableEvents</code>.
     * </ul>
     * @param       e the item event.
     * @see         java.awt.event.ItemEvent
     * @see         java.awt.event.ItemListener
     * @see         java.awt.Choice#addItemListener
     * @see         java.awt.Component#enableEvents
     * @since       JDK1.1
     */  
    protected void processItemEvent(ItemEvent e) {
        if (itemListener != null) {
            itemListener.itemStateChanged(e);
        }
    }

    /**
     * Returns the parameter string representing the state of this 
     * choice menu. This string is useful for debugging. 
     * @return    the parameter string of this <code>Choice</code> menu.
     * @since     JDK1.0
     */
    protected String paramString() {
	return super.paramString() + ",current=" + getSelectedItem();
    }


    /* Serialization support. 
     */

    private int choiceSerializedDataVersion = 1;


    private void writeObject(ObjectOutputStream s)
      throws java.io.IOException 
    {
      s.defaultWriteObject();

      AWTEventMulticaster.save(s, itemListenerK, itemListener);
      s.writeObject(null);
    }


    private void readObject(ObjectInputStream s)
      throws ClassNotFoundException, IOException 
    {
      s.defaultReadObject();

      Object keyOrNull;
      while(null != (keyOrNull = s.readObject())) {
	String key = ((String)keyOrNull).intern();

	if (itemListenerK == key) 
	  addItemListener((ItemListener)(s.readObject()));

	else // skip value for unrecognized key
	  s.readObject();
      }
    }

}
