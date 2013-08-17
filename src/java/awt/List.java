/*
 * @(#)List.java	1.49 97/03/03
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
import java.awt.peer.ListPeer;
import java.awt.event.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * A scrolling list of text items.
 *
 * @version 	1.49, 03/03/97
 * @author 	Sami Shaio
 */
public class List extends Component implements ItemSelectable {
    Vector	items = new Vector();
    int		rows = 0;
    boolean	multipleMode = false;
    int		selected[] = new int[0];
    int		visibleIndex = -1;

    transient ActionListener actionListener;
    transient ItemListener itemListener;

    private static final String base = "list";
    private static int nameCounter = 0;

    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = -3304312411574666869L;

    /**
     * Creates a new scrolling list initialized with no visible Lines
     * or multiple selections.
     */
    public List() {
	this(0, false);
    }

    /**
     * Creates a new scrolling list initialized with the specified 
     * number of visible lines and a boolean stating whether multiple
     * selections are allowed or not.
     * @param rows the number of items to show.
     * @param multipleMode if true then multiple selections are allowed.
     */
    public List(int rows) {
    	this(rows, false);
    }

    /** 
     * The default number of visible rows is 4.  A list with
     * zero rows is unusable and unsightly.
     */
    final static int 	DEFAULT_VISIBLE_ROWS = 4;

    /**
     * Creates a new scrolling list initialized with the specified 
     * number of visible lines and a boolean stating whether multiple
     * selections are allowed or not.
     * @param rows the number of items to show.
     * @param multipleMode if true then multiple selections are allowed.
     */
    public List(int rows, boolean multipleMode) {
        this.name = base + nameCounter++;
	this.rows = (rows != 0) ? rows : DEFAULT_VISIBLE_ROWS; 
	this.multipleMode = multipleMode;
    }

    /**
     * Creates the peer for the list.  The peer allows us to modify the
     * list's appearance without changing its functionality.
     */
    public void addNotify() {
	peer = getToolkit().createList(this);
	super.addNotify();
    	synchronized (this) {
	    visibleIndex = -1;
	}
    }

    /**
     * Removes the peer for this list.  The peer allows us to modify the
     * list's appearance without changing its functionality.
     */
    public void removeNotify() {
    	synchronized (this) {
	    ListPeer peer = (ListPeer)this.peer;
	    if (peer != null) {
		selected = peer.getSelectedIndexes();
	    }
	}
	super.removeNotify();
    }
    
    /**
     * Returns the number of items in the list.
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
	return items.size();
    }

    /**
     * Gets the item associated with the specified index.
     * @param index the position of the item
     * @see #getItemCount
     */
    public String getItem(int index) {
	return (String)items.elementAt(index);
    }

    /**
     * Returns the items in the list.
     * @see #select
     * @see #deselect
     * @see #isIndexSelected
     */
    public synchronized String[] getItems() {
	String itemCopies[] = new String[items.size()];
    	items.copyInto(itemCopies);
	return itemCopies;
    }

    /**
     * Adds the specified item to the end of scrolling list.
     * @param item the item to be added
     */
    public void add(String item) {
	addItem(item);
    }

    /**
     * Adds the specified item to the end of scrolling list.
     * @param item the item to be added
     */
    public void addItem(String item) {
	addItem(item, -1);
    }

    /**
     * Adds the specified item to the scrolling list at the specified
     * position.
     * @param item the item to be added
     * @param index the position at which to put in the item. The
     * index is zero-based. If index is -1 then the item is added to
     * the end. If index is greater than the number of items in the
     * list, the item gets added at the end. 
     */
    public synchronized void add(String item, int index) {
	addItem(item, index);
    }

    /**
     * Adds the specified item to the scrolling list at the specified
     * position.
     * @param item the item to be added
     * @param index the position at which to put in the item. The
     * index is zero-based. If index is -1 then the item is added to
     * the end. If index is greater than the number of items in the
     * list, the item gets added at the end. 
     */
    public synchronized void addItem(String item, int index) {
	if (index < -1 || index >= items.size()) {
	    index = -1;
	}
	if (index == -1) {
	    items.addElement(item);
	} else {
	    items.insertElementAt(item, index);
	}
	ListPeer peer = (ListPeer)this.peer;
	if (peer != null) {
	    peer.addItem(item, index);
	}
    }

    /**
     * Replaces the item at the given index.
     * @param newValue the new value to replace the existing item
     * @param index the position of the item to replace
     */
    public synchronized void replaceItem(String newValue, int index) {
	remove(index);
	add(newValue, index);
    }

    /**
     * Removes all items from the list.
     * @see #remove
     * @see #delItems
     */
    public synchronized void removeAll() {
	clear();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by removeAll().
     */
    public synchronized void clear() {
	ListPeer peer = (ListPeer)this.peer;
	if (peer != null) {
	    peer.clear();
	}
	items = new Vector();
	selected = new int[0];
    }

    /**
     * Remove the first occurrence of item from the list.
     * @param item  the item to remove from the list
     * @exception IllegalArgumentException  If the item doesn't exist in the list.
     */
    public synchronized void remove(String item) {
    	int index = items.indexOf(item);
    	if (index < 0) {
	    throw new IllegalArgumentException("item " + item +
					       " not found in list");
	} else {
	    remove(index);
	}
    }

    /**
     * Removes an item from the list.
     */
    public synchronized void remove(int position) {
	delItem(position);
    }

    /**
     * Removes an item from the list.
     */
    public synchronized void delItem(int position) {
	delItems(position, position);
    }

    /**
     * Get the selected item on the list or -1 if no item is selected.
     * @see #select
     * @see #deselect
     * @see #isIndexSelected
     */
    public synchronized int getSelectedIndex() {
	int sel[] = getSelectedIndexes();
	return (sel.length == 1) ? sel[0] : -1;
    }

    /**
     * Returns the selected indexes on the list.
     * @see #select
     * @see #deselect
     * @see #isIndexSelected
     */
    public synchronized int[] getSelectedIndexes() {
	ListPeer peer = (ListPeer)this.peer;
	if (peer != null) {
	    selected = ((ListPeer)peer).getSelectedIndexes();
	}
	return selected;
    }

    /**
     * Returns the selected item on the list or null if no item is selected.
     * @see #select
     * @see #deselect
     * @see #isIndexSelected
     */
    public synchronized String getSelectedItem() {
	int index = getSelectedIndex();
	return (index < 0) ? null : getItem(index);
    }

    /**
     * Returns the selected items on the list in an array of Strings.
     * @see #select
     * @see #deselect
     * @see #isIndexSelected
     */
    public synchronized String[] getSelectedItems() {
	int sel[] = getSelectedIndexes();
	String str[] = new String[sel.length];
	for (int i = 0 ; i < sel.length ; i++) {
	    str[i] = getItem(sel[i]);
	}
	return str;
    }

    /**
     * Returns the selected items on the list in an array of Objects.
     * @see ItemSelectable
     */
    public Object[] getSelectedObjects() {
        return getSelectedItems();
    }

    /**
     * Selects the item at the specified index.
     * @param index the position of the item to select
     * @see #getSelectedItem
     * @see #deselect
     * @see #isIndexSelected
     */
    public synchronized void select(int index) {
	ListPeer peer = (ListPeer)this.peer;
	if (peer != null) {
	    peer.select(index);
	    return;
	}

	for (int i = 0 ; i < selected.length ; i++) {
	    if (selected[i] == index) {
		return;
	    }
	}
	if (!multipleMode) {
	    selected = new int[1];
	    selected[0] = index;
	} else {
	    int newsel[] = new int[selected.length + 1];
	    System.arraycopy(selected, 0, newsel, 0, selected.length);
	    newsel[selected.length] = index;
	    selected = newsel;
	}
    }

    /**
     * Deselects the item at the specified index.
     * @param index the position of the item to deselect
     * @see #select
     * @see #getSelectedItem
     * @see #isIndexSelected
     */
    public synchronized void deselect(int index) {
	ListPeer peer = (ListPeer)this.peer;
	if (peer != null) {
	    peer.deselect(index);
	}

	for (int i = 0 ; i < selected.length ; i++) {
	    if (selected[i] == index) {
		int newsel[] = new int[selected.length - 1];
		System.arraycopy(selected, 0, newsel, 0, i);
		System.arraycopy(selected, i+1, newsel, i, selected.length - (i+1));
		selected = newsel;
		return;
	    }
	}
    }

    /**
     * Returns true if the item at the specified index has been selected;
     * false otherwise.
     * @param index the item to be checked
     * @see #select
     * @see #deselect
     */
    public boolean isIndexSelected(int index) {
	return isSelected(index);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by isIndexSelected(int).
     */
    public boolean isSelected(int index) {
	int sel[] = getSelectedIndexes();
	for (int i = 0 ; i < sel.length ; i++) {
	    if (sel[i] == index) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Returns the number of visible lines in this list.
     */
    public int getRows() {
	return rows;
    }

    /**
     * Returns true if this list allows multiple selections.
     * @see #setMultipleMode
     */
    public boolean isMultipleMode() {
	return allowsMultipleSelections();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by isMultipleMode().
     */
    public boolean allowsMultipleSelections() {
	return multipleMode;
    }

    /**
     * Sets whether this list should allow multiple selections or not.
     * @param v the boolean to allow multiple selections
     * @see #isMultipleMode
     */
    public synchronized void setMultipleMode(boolean b) {
    	setMultipleSelections(b);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by setMultipleMode(boolean).
     */
    public synchronized void setMultipleSelections(boolean b) {
	if (b != multipleMode) {
	    multipleMode = b;
	    ListPeer peer = (ListPeer)this.peer;
	    if (peer != null) {
		peer.setMultipleSelections(b);
	    }
	}
    }

    /**
     * Gets the index of the item that was last made visible by the method
     * makeVisible.
     */
    public int getVisibleIndex() {
	return visibleIndex;
    }

    /**
     * Forces the item at the specified index to be visible.
     * @param index the position of the item
     * @see #getVisibleIndex
     */
    public synchronized void makeVisible(int index) {
	visibleIndex = index;
	ListPeer peer = (ListPeer)this.peer;
	if (peer != null) {
	    peer.makeVisible(index);
	}
    }

    /**
     * Returns the preferred dimensions needed for the list with the specified
     * amount of rows.
     * @param rows amount of rows in list.
     */
    public Dimension getPreferredSize(int rows) {
	return preferredSize(rows);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by getPreferredSize(int).
     */
    public Dimension preferredSize(int rows) {
        synchronized (Component.LOCK) {
  	    ListPeer peer = (ListPeer)this.peer;
 	    return (peer != null) ?
  		       peer.preferredSize(rows) :
  		       super.preferredSize();
  	}
    }

    /**
     * Returns the preferred dimensions needed for the list.
     * @return the preferred size with the specified number of rows if the 
     * row size is greater than 0. 
     * 
     */
    public Dimension getPreferredSize() {
	return preferredSize();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by getPreferredSize().
     */
    public Dimension preferredSize() {
	synchronized (Component.LOCK) {
	    return (rows > 0) ?
		       preferredSize(rows) :
		       super.preferredSize();
	}
    }

    /**
     * Returns the minimum dimensions needed for the amount of rows in the 
     * list.
     * @param rows minimum amount of rows in the list
     */
    public Dimension getMinimumSize(int rows) {
	return minimumSize(rows);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by getMinimumSize(int).
     */
    public Dimension minimumSize(int rows) {
  	synchronized (Component.LOCK) {
  	    ListPeer peer = (ListPeer)this.peer;
 	    return (peer != null) ?
     	    	       peer.minimumSize(rows) :
 		       super.minimumSize();
  	}
    }

    /**
     * Returns the minimum dimensions needed for the list.
     * @return the preferred size with the specified number of rows if
     * the row size is greater than zero.
     */
    public Dimension getMinimumSize() {
	return minimumSize();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by getMinimumSize().
     */
    public Dimension minimumSize() {
	synchronized (Component.LOCK) {
	    return (rows > 0) ? minimumSize(rows) : super.minimumSize();
	}
    }

    /**
     * Adds the specified item listener to recieve item events from
     * this list.
     * @param l the item listener
     */ 
    public synchronized void addItemListener(ItemListener l) {
        itemListener = AWTEventMulticaster.add(itemListener, l);
        newEventsOnly = true;
    }

    /**
     * Removes the specified item listener so it no longer receives
     * item events from this list.
     * @param l the item listener
     */ 
    public synchronized void removeItemListener(ItemListener l) {
        itemListener = AWTEventMulticaster.remove(itemListener, l);
    }

    /**
     * Adds the specified action listener to receive action events
     * from this list.  Action events occur when a list item is
     * double-clicked.
     * @param l the action listener
     */ 
    public synchronized void addActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.add(actionListener, l);
        newEventsOnly = true;	
    }

    /**
     * Removes the specified action listener so it no longer receives
     * action events from this list.
     * @param l the action listener
     */ 
    public synchronized void removeActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.remove(actionListener, l);
    }

    // REMIND: remove when filtering is done at lower level
    boolean eventEnabled(AWTEvent e) {
        switch(e.id) {
          case ActionEvent.ACTION_PERFORMED:
            if ((eventMask & AWTEvent.ACTION_EVENT_MASK) != 0 ||
                actionListener != null) {
                return true;
            } 
            return false;
          case ItemEvent.ITEM_STATE_CHANGED:
            if ((eventMask & AWTEvent.ITEM_EVENT_MASK) != 0 ||
                itemListener != null) {
                return true;
            }
            return false;
          default:
            break;
        } 
        return super.eventEnabled(e);
    }          

    /**
     * Processes events on this list. If the event is an ItemEvent,
     * it invokes the processItemEvent method, else if the event is an
     * ActionEvent, it invokes the processActionEvent method, else it
     * invokes the superclass's processEvent.
     * @param e the event
     */
    protected void processEvent(AWTEvent e) {
        if (e instanceof ItemEvent) {
            processItemEvent((ItemEvent)e);
            return;
        } else if (e instanceof ActionEvent) {
            processActionEvent((ActionEvent)e);
            return;
        }
	super.processEvent(e);
    }

    /** 
     * Processes item events occurring on this list by
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
     * Processes action events occurring on this component by
     * dispatching them to any registered ActionListener objects.
     * NOTE: This method will not be called unless action events
     * are enabled for this component; this happens when one of the
     * following occurs:
     * a) An ActionListener object is registered via addActionListener()
     * b) Action events are enabled via enableEvents()
     * @see Component#enableEvents
     * @param e the action event
     */  
    protected void processActionEvent(ActionEvent e) {
        if (actionListener != null) {
            actionListener.actionPerformed(e);
        }
    }

    /**
     * Returns the parameter String of this list. 
     */
    protected String paramString() {
	return super.paramString() + ",selected=" + getSelectedItem();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * Not for public use in the future.
     * This method is expected to be retained only as a package
     * private method.
     */
    public synchronized void delItems(int start, int end) {
	for (int i = end; i >= start; i--) {
	    items.removeElementAt(i);
	}
	ListPeer peer = (ListPeer)this.peer;
	if (peer != null) {
	    peer.delItems(start, end);
	}
    }

    /* 
     * Serialization support.  Since the value of the selected
     * field isn't neccessarily up to date we sync it up with the 
     * peer before serializing.
     */

    private int listSerializedDataVersion = 1;


    private void writeObject(ObjectOutputStream s)
      throws IOException 
    {
      synchronized (this) {
	ListPeer peer = (ListPeer)this.peer;
	if (peer != null) {
	  selected = peer.getSelectedIndexes();
	}
      }
      s.defaultWriteObject();

      AWTEventMulticaster.save(s, itemListenerK, itemListener);
      AWTEventMulticaster.save(s, actionListenerK, actionListener);
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

	else if (actionListenerK == key) 
	  addActionListener((ActionListener)(s.readObject()));

	else // skip value for unrecognized key
	  s.readObject();
      }
    }


}
