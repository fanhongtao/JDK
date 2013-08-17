/*
 * @(#)Choice.java	1.39 97/03/03 Sami Shaio
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

import java.util.*;
import java.awt.peer.ChoicePeer;
import java.awt.event.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * The Choice class is a pop-up menu of choices. The current choice is
 * displayed as the title of the menu.
 *
 * @version	1.39 03/03/97
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
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
     * Constructs a new Choice.
     */
    public Choice() {
        this.name = base + nameCounter++;
	pItems = new Vector();
    }

    /**
     * Creates the Choice's peer.  This peer allows us to change the look
     * of the Choice without changing its functionality.
     */
    public void addNotify() {
	peer = getToolkit().createChoice(this);
	super.addNotify();
    }

    /**
     * Returns the number of items in this Choice.
     * @see #getItem
     */
    public int getItemCount() {
	return countItems();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by getItemCount().
     */
    public int countItems() {
	return pItems.size();
    }

    /**
     * Returns the String at the specified index in the Choice.
     * @param index the index at which to begin
     * @see #getItemCount
     */
    public String getItem(int index) {
	return (String)pItems.elementAt(index);
    }

    /**
     * Adds an item to this Choice.
     * @param item the item to be added
     * @exception NullPointerException If the item's value is equal to null.
     */
    public synchronized void add(String item) {
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
     * Remove the first occurrence of item from the choice menu.
     * @param item  the item to remove from the choice menu
     * @exception IllegalArgumentException  If the item doesn't exist in the choice menu.
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
     * Removes an item from the choice menu.
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
	}
    }

    /**
     * Removes all items from the choice menu.
     * @see #remove
     */
    public synchronized void removeAll() {
        int nitems = getItemCount();
	for (int i = 0 ; i < nitems ; i++) {
	    remove(0);
	}
    }

    /**
     * Returns a String representation of the current choice.
     * @see #getSelectedIndex
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
     * Selects the item with the specified postion.
     * @param pos the choice item position
     * @exception IllegalArgumentException If the choice item position is 
     * invalid.
     * @see #getSelectedItem
     * @see #getSelectedIndex
     */
    public synchronized void select(int pos) {
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
     * Selects the item with the specified String.
     * @param str the specified String
     * @see #getSelectedItem
     * @see #getSelectedIndex
     */
    public synchronized void select(String str) {
	int index = pItems.indexOf(str);
	if (index >= 0) {
	    select(index);
	}
    }

    /**
     * Adds the specified item listener to recieve item events from
     * this choice.
     * @param l the item listener
     */ 
    public synchronized void addItemListener(ItemListener l) {
        itemListener = AWTEventMulticaster.add(itemListener, l);
        newEventsOnly = true;
    }

    /**
     * Removes the specified item listener so that it no longer
     * receives item events from this choice.
     * @param l the item listener
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
     * Processes events on this choice. If the event is an ItemEvent,
     * it invokes the handleItemEvent method, else it calls its
     * superclass's processEvent.
     * @param e the event
     */
    protected void processEvent(AWTEvent e) {
        if (e instanceof ItemEvent) {
            processItemEvent((ItemEvent)e);
            return;
        }
	super.processEvent(e);
    }

    /** 
     * Processes item events occurring on this choice by
     * dispatching them to any registered ItemListener objects.
     * NOTE: This method will not be called unless item events
     * are enabled for this component; this happens when one of the
     * following occurs:
     * a) An ItemListener object is registered via addItemListener()
     * b) Item events are enabled via enableEvents()
     * @see Component#enableEvents
     * @param e the item event
     */  
    protected void processItemEvent(ItemEvent e) {
        if (itemListener != null) {
            itemListener.itemStateChanged(e);
        }
    }

    /**
     * Returns the parameter String of this Choice.
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
