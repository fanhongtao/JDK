/*
 * @(#)List.java	1.62 99/01/22
 *
 * Copyright 1995-1999 by Sun Microsystems, Inc.,
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
import java.awt.peer.ListPeer;
import java.awt.event.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * The <code>List</code> component presents the user with a 
 * scrolling list of text items. The list can be set up so that  
 * the user can choose either one item or multiple items. 
 * <p>
 * For example, the code&nbsp;.&nbsp;.&nbsp;.
 * <p>
 * <hr><blockquote><pre>
 * List lst = new List(4, false);
 * lst.add("Mercury");
 * lst.add("Venus");
 * lst.add("Earth");
 * lst.add("JavaSoft");
 * lst.add("Mars");
 * lst.add("Jupiter");
 * lst.add("Saturn");
 * lst.add("Uranus");
 * lst.add("Neptune");
 * lst.add("Pluto");
 * cnt.add(lst);
 * </pre></blockquote><hr>
 * <p>
 * where <code>cnt</code> is a container, produces the following 
 * scrolling list:
 * <p>
 * <img src="images-awt/List-1.gif" 
 * ALIGN=center HSPACE=10 VSPACE=7> 
 * <p>
 * Clicking on an item that isn't selected selects it. Clicking on 
 * an item that is already selected deselects it. In the preceding 
 * example, only one item from the scrolling list can be selected 
 * at a time, since the second argument when creating the new scrolling 
 * list is <code>false</code>. Selecting an item causes any other 
 * selected item to be automatically deselected. 
 * <p>
 * Beginning with Java&nbsp;1.1, the Abstract Window Toolkit 
 * sends the <code>List</code> object all mouse, keyboard, and focus events 
 * that occur over it. (The old AWT event model is being maintained
 * only for backwards compatibility, and its use is discouraged.)
 * <p>
 * When an item is selected or deselected, AWT sends an instance  
 * of <code>ItemEvent</code> to the list.
 * When the user double-clicks on an item in a scrolling list,  
 * AWT sends an instance of <code>ActionEvent</code> to the 
 * list following the item event. AWT also generates an action event 
 * when the user presses the return key while an item in the 
 * list is selected.
 * <p>
 * If an application wants to perform some action based on an item 
 * in this list being selected or activated, it should implement 
 * <code>ItemListener</code> or <code>ActionListener</code> 
 * as appropriate and register the new listener to receive 
 * events from this list. 
 * <p>
 * For multiple-selection scrolling lists, it is considered a better 
 * user interface to use an external gesture (such as clicking on a 
 * button) to trigger the action. 
 * @version 	1.62, 01/22/99
 * @author 	Sami Shaio
 * @see         java.awt.event.ItemEvent
 * @see         java.awt.event.ItemListener
 * @see         java.awt.event.ActionEvent
 * @see         java.awt.event.ActionListener
 * @since       JDK1.0
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
     * Creates a new scrolling list. Initially there are no visible 
     * lines, and only one item can be selected from the list. 
     * @since       JDK1.0
     */
    public List() {
	this(0, false);
    }

    /**
     * Creates a new scrolling list initialized with the specified 
     * number of visible lines. By default, multiple selections are
     * not allowed.
     * @param       rows the number of items to show.
     * @since       JDK1.1
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
     * Creates a new scrolling list initialized to display the specified 
     * number of rows. If the value of <code>multipleMode</code> is 
     * <code>true</code>, then the user can select multiple items from  
     * the list. If it is <code>false</code>, only one item at a time 
     * can be selected. 
     * @param       rows   the number of items to show.
     * @param       multipleMode   if <code>true</code>,
     *                     then multiple selections are allowed; 
     *                     otherwise, only one item can be selected at a time.
     * @since       JDK1.0
     */
    public List(int rows, boolean multipleMode) {
	this.rows = (rows != 0) ? rows : DEFAULT_VISIBLE_ROWS; 
	this.multipleMode = multipleMode;
    }

    /**
     * Construct a name for this component.  Called by getName() when the
     * name is null.
     */
    String constructComponentName() {
        return base + nameCounter++;
    }

    /**
     * Creates the peer for the list.  The peer allows us to modify the
     * list's appearance without changing its functionality.
     */
    public void addNotify() {
        synchronized (getTreeLock()) {
	    if (peer == null)
			peer = getToolkit().createList(this);
	    super.addNotify();
	    visibleIndex = -1;
        }
    }

    /**
     * Removes the peer for this list.  The peer allows us to modify the
     * list's appearance without changing its functionality.
     */
    public void removeNotify() {
    	synchronized (getTreeLock()) {
	    ListPeer peer = (ListPeer)this.peer;
	    if (peer != null) {
		selected = peer.getSelectedIndexes();
	    }
            super.removeNotify();
	}
    }
    
    /**
     * Gets the number of items in the list.
     * @return     the number of items in the list.
     * @see        java.awt.List#getItem
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
     * Gets the item associated with the specified index.
     * @return       an item that is associated with 
     *                    the specified index.
     * @param        index the position of the item.
     * @see          java.awt.List#getItemCount
     * @since        JDK1.0
     */
    public String getItem(int index) {
	return (String)items.elementAt(index);
    }

    /**
     * Gets the items in the list.
     * @return       a string array containing items of the list.
     * @see          java.awt.List#select
     * @see          java.awt.List#deselect
     * @see          java.awt.List#isIndexSelected
     * @since        JDK1.1
     */
    public synchronized String[] getItems() {
	String itemCopies[] = new String[items.size()];
    	items.copyInto(itemCopies);
	return itemCopies;
    }

    /**
     * Adds the specified item to the end of scrolling list.
     * @param item the item to be added.
     * @since JDK1.1
     */
    public void add(String item) {
	addItem(item);
    }

    /**
     * Adds the specified item to the end of scrolling list.
     * @param item the item to be added.
     */
    public void addItem(String item) {
	addItem(item, -1);
    }

    /**
     * Adds the specified item to the end of the scrolling list.
     * The index is zero-based. If value of the index is 
     * <code>-1</code> then the item is added to the end. 
     * If value of the index is greater than the number of 
     * items in the list, the item is added at the end. 
     * @param       item   the item to be added.
     * @param       index  the position at which to add the item. 
     * @since       JDK1.1
     */
    public void add(String item, int index) {
	addItem(item, index);
    }

    /**
     * Adds the specified item to the end of the scrolling list.
     * The index is zero-based. If value of the index is 
     * <code>-1</code> then the item is added to the end. 
     * If value of the index is greater than the number of 
     * items in the list, the item is added at the end. 
     * @param       item   the item to be added.
     * @param       index  the position at which to add the item. 
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
     * Replaces the item at the specified index in the scrolling list
     * with the new string.
     * @param       newValue   a new string to replace an existing item.
     * @param       index      the position of the item to replace.
     * @since       JDK1.0
     */
    public synchronized void replaceItem(String newValue, int index) {
	remove(index);
	add(newValue, index);
    }

    /**
     * Removes all items from this list.
     * @see #remove
     * @see #delItems
     * @since JDK1.1
     */
    public void removeAll() {
	clear();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>removeAll()</code>.
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
     * Removes the first occurrence of an item from the list.
     * @param        item  the item to remove from the list.
     * @exception    IllegalArgumentException  
     *                     if the item doesn't exist in the list.
     * @since        JDK1.1
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
     * Remove the item at the specified position  
     * from this scrolling list. 
     * @param      position   the index of the item to delete.
     * @see        java.awt.List#add(String, int)
     * @since      JDK1.1
     */
    public void remove(int position) {
	delItem(position);
    }

    /**
     * Removes the item at the specified position from this list.
     */
    public void delItem(int position) {
	delItems(position, position);
    }

    /**
     * Gets the index of the selected item on the list, 
     * @return        the index of the selected item, or 
     *                     <code>-1</code> if no item is selected,
     *                     or if more that one item is selected.
     * @see           java.awt.List#select
     * @see           java.awt.List#deselect
     * @see           java.awt.List#isIndexSelected
     * @since         JDK1.0
     */
    public synchronized int getSelectedIndex() {
	int sel[] = getSelectedIndexes();
	return (sel.length == 1) ? sel[0] : -1;
    }

    /**
     * Gets the selected indexes on the list.
     * @return        an array of the selected indexes 
     *                            of this scrolling list. 
     * @see           java.awt.List#select
     * @see           java.awt.List#deselect
     * @see           java.awt.List#isIndexSelected
     * @since         JDK1.0
     */
    public synchronized int[] getSelectedIndexes() {
	ListPeer peer = (ListPeer)this.peer;
	if (peer != null) {
	    selected = ((ListPeer)peer).getSelectedIndexes();
	}
	return selected;
    }

    /**
     * Get the selected item on this scrolling list.
     * @return        the selected item on the list, 
     *                     or null if no item is selected.
     * @see           java.awt.List#select
     * @see           java.awt.List#deselect
     * @see           java.awt.List#isIndexSelected
     * @since         JDK1.0
     */
    public synchronized String getSelectedItem() {
	int index = getSelectedIndex();
	return (index < 0) ? null : getItem(index);
    }

    /**
     * Get the selected items on this scrolling list.
     * @return        an array of the selected items 
     *                            on this scrolling list.
     * @see           java.awt.List#select
     * @see           java.awt.List#deselect
     * @see           java.awt.List#isIndexSelected
     * @since         JDK1.0
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
     * Selects the item at the specified index in the scrolling list.
     * @param        index the position of the item to select.
     * @see          java.awt.List#getSelectedItem
     * @see          java.awt.List#deselect
     * @see          java.awt.List#isIndexSelected
     * @since        JDK1.0
     */
    public void select(int index) { 
        // Bug #4059614: select can't be synchronized while calling the peer, 
        // because it is called from the Window Thread.  It is sufficient to 
        // synchronize the code that manipulates 'selected' except for the 
        // case where the peer changes.  To handle this case, we simply 
        // repeat the selection process. 
         
        ListPeer peer; 
        do { 
            peer = (ListPeer)this.peer; 
            if (peer != null) { 
                peer.select(index); 
                return; 
            } 
             
            synchronized(this) 
            { 
                boolean alreadySelected = false; 
 
                for (int i = 0 ; i < selected.length ; i++) { 
                    if (selected[i] == index) { 
                        alreadySelected = true; 
                        break; 
                    } 
                } 
 
                if (!alreadySelected) { 
                    if (!multipleMode) { 
                        selected = new int[1]; 
                        selected[0] = index; 
                    } else { 
                        int newsel[] = new int[selected.length + 1]; 
                        System.arraycopy(selected, 0, newsel, 0, 
                                         selected.length); 
                        newsel[selected.length] = index; 
                        selected = newsel; 
                    } 
                } 
            } 
        } while (peer != this.peer); 
    } 

    /**
     * Deselects the item at the specified index.
     * <p>
     * If the item at the specified index is not selected, or if the 
     * index is out of range, then the operation is ignored. 
     * @param        index the position of the item to deselect.
     * @see          java.awt.List#select
     * @see          java.awt.List#getSelectedItem
     * @see          java.awt.List#isIndexSelected
     * @since        JDK1.0
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
     * Determines if the specified item in this scrolling list is 
     * selected. 
     * @param      index   the item to be checked.
     * @return     <code>true</code> if the specified item has been
     *                       selected; <code>false</code> otherwise.
     * @see        java.awt.List#select
     * @see        java.awt.List#deselect
     * @since      JDK1.1
     */
    public boolean isIndexSelected(int index) {
	return isSelected(index);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>isIndexSelected(int)</code>.
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
     * Get the number of visible lines in this list.
     * @return     the number of visible lines in this scrolling list.
     * @since      JDK1.0
     */
    public int getRows() {
	return rows;
    }

    /**
     * Determines whether this list allows multiple selections.
     * @return     <code>true</code> if this list allows multiple 
     *                 selections; otherwise, <code>false</code>.
     * @see        java.awt.List#setMultipleMode
     * @since      JDK1.1
     */
    public boolean isMultipleMode() {
	return allowsMultipleSelections();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>isMultipleMode()</code>.
     */
    public boolean allowsMultipleSelections() {
	return multipleMode;
    }

    /**
     * Sets the flag that determines whether this list 
     * allows multiple selections.
     * @param       b   if <code>true</code> then multiple selections
     *                      are allowed; otherwise, only one item from
     *                      the list can be selected at once.
     * @see         java.awt.List#isMultipleMode
     * @since       JDK1.1
     */
    public void setMultipleMode(boolean b) {
    	setMultipleSelections(b);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>setMultipleMode(boolean)</code>.
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
     * Gets the index of the item that was last made visible by 
     * the method <code>makeVisible</code>.
     * @return      the index of the item that was last made visible.
     * @see         java.awt.List#makeVisible
     * @since       JDK1.0
     */
    public int getVisibleIndex() {
	return visibleIndex;
    }

    /**
     * Makes the item at the specified index visible. 
     * @param       index    the position of the item.
     * @see         java.awt.List#getVisibleIndex
     * @since       JDK1.0
     */
    public synchronized void makeVisible(int index) {
	visibleIndex = index;
	ListPeer peer = (ListPeer)this.peer;
	if (peer != null) {
	    peer.makeVisible(index);
	}
    }

    /**
     * Gets the preferred dimensions for a list with the specified
     * number of rows.  
     * @param      rows    number of rows in the list.
     * @return     the preferred dimensions for displaying this scrolling list.
     * @see        java.awt.Component#getPreferredSize
     * @since      JDK1.1
     */
    public Dimension getPreferredSize(int rows) {
	return preferredSize(rows);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getPreferredSize(int)</code>.
     */
    public Dimension preferredSize(int rows) {
        synchronized (getTreeLock()) {
  	    ListPeer peer = (ListPeer)this.peer;
 	    return (peer != null) ?
  		       peer.preferredSize(rows) :
  		       super.preferredSize();
  	}
    }

    /**
     * Gets the preferred size of this scrolling list.  
     * @return     the preferred dimensions for displaying this scrolling list.
     * @see        java.awt.Component#getPreferredSize
     * @since      JDK1.1 
     */
    public Dimension getPreferredSize() {
	return preferredSize();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getPreferredSize()</code>.
     */
    public Dimension preferredSize() {
	synchronized (getTreeLock()) {
	    return (rows > 0) ?
		       preferredSize(rows) :
		       super.preferredSize();
	}
    }

    /**
     * Gets the minumum dimensions for a list with the specified
     * number of rows.
     * @param      rows    number of rows in the list.
     * @return     the minimum dimensions for displaying this scrolling list.
     * @see        java.awt.Component#getMinimumSize
     * @since      JDK1.1
     */
    public Dimension getMinimumSize(int rows) {
	return minimumSize(rows);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getMinimumSize(int)</code>.
     */
    public Dimension minimumSize(int rows) {
  	synchronized (getTreeLock()) {
  	    ListPeer peer = (ListPeer)this.peer;
 	    return (peer != null) ?
     	    	       peer.minimumSize(rows) :
 		       super.minimumSize();
  	}
    }

    /**
     * Determines the minimum size of this scrolling list. 
     * @return       the minimum dimensions needed 
     *                        to display this scrolling list.
     * @see          java.awt.Component#getMinimumSize()
     * @since        JDK1.1
     */
    public Dimension getMinimumSize() {
	return minimumSize();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getMinimumSize()</code>.
     */
    public Dimension minimumSize() {
	synchronized (getTreeLock()) {
	    return (rows > 0) ? minimumSize(rows) : super.minimumSize();
	}
    }

    /**
     * Adds the specified item listener to receive item events from
     * this list.
     * @param         l the item listener.
     * @see           java.awt.event.ItemEvent
     * @see           java.awt.event.ItemListener
     * @see           java.awt.List#removeItemListener
     * @since         JDK1.1
     */ 
    public synchronized void addItemListener(ItemListener l) {
        itemListener = AWTEventMulticaster.add(itemListener, l);
        newEventsOnly = true;
    }

    /**
     * Removes the specified item listener so that it no longer 
     * receives item events from this list. 
     * @param         l the item listener.
     * @see           java.awt.event.ItemEvent
     * @see           java.awt.event.ItemListener
     * @see           java.awt.List#addItemListener
     * @since         JDK1.1
     */ 
    public synchronized void removeItemListener(ItemListener l) {
        itemListener = AWTEventMulticaster.remove(itemListener, l);
    }

    /**
     * Adds the specified action listener to receive action events from
     * this list. Action events occur when a user double-clicks
     * on a list item.
     * @param         l the action listener.
     * @see           java.awt.event.ActionEvent
     * @see           java.awt.event.ActionListener
     * @see           java.awt.List#removeActionListener
     * @since         JDK1.1
     */ 
    public synchronized void addActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.add(actionListener, l);
        newEventsOnly = true;	
    }

    /**
     * Removes the specified action listener so that it no longer 
     * receives action events from this list. Action events 
     * occur when a user double-clicks on a list item.
     * @param         l     the action listener.
     * @see           java.awt.event.ActionEvent
     * @see           java.awt.event.ActionListener
     * @see           java.awt.List#addActionListener
     * @since         JDK1.1
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
     * Processes events on this scrolling list. If an event is 
     * an instance of <code>ItemEvent</code>, it invokes the 
     * <code>processItemEvent</code> method. Else, if the 
     * event is an instance of <code>ActionEvent</code>, 
     * it invokes <code>processActionEvent</code>.
     * If the event is not an item event or an action event,
     * it invokes <code>processEvent</code> on the superclass.
     * @param        e the event.
     * @see          java.awt.event.ActionEvent
     * @see          java.awt.event.ItemEvent
     * @see          java.awt.List#processActionEvent
     * @see          java.awt.List#processItemEvent
     * @since        JDK1.1
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
     * dispatching them to any registered 
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
     * @see         java.awt.List#addItemListener
     * @see         java.awt.Component#enableEvents
     * @since       JDK1.1
     */  
    protected void processItemEvent(ItemEvent e) {
        if (itemListener != null) {
            itemListener.itemStateChanged(e);
        }
    }

    /** 
     * Processes action events occurring on this component 
     * by dispatching them to any registered 
     * <code>ActionListener</code> objects.
     * <p>
     * This method is not called unless action events are 
     * enabled for this component. Action events are enabled 
     * when one of the following occurs:
     * <p><ul>
     * <li>An <code>ActionListener</code> object is registered 
     * via <code>addActionListener</code>.
     * <li>Action events are enabled via <code>enableEvents</code>.
     * </ul>
     * @param       e the action event.
     * @see         java.awt.event.ActionEvent
     * @see         java.awt.event.ActionListener
     * @see         java.awt.List#addActionListener
     * @see         java.awt.Component#enableEvents
     * @since       JDK1.1
     */  
    protected void processActionEvent(ActionEvent e) {
        if (actionListener != null) {
            actionListener.actionPerformed(e);
        }
    }

    /**
     * Returns the parameter string representing the state of this 
     * scrolling list. This string is useful for debugging. 
     * @return    the parameter string of this scrolling list. 
     * @since     JDK1.0
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
