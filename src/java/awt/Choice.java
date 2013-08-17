/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt;

import java.util.*;
import java.awt.peer.ChoicePeer;
import java.awt.event.*;
import java.util.EventListener;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import javax.accessibility.*;

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
 * <img src="doc-files/Choice-1.gif"
 * ALIGN=center HSPACE=10 VSPACE=7>
 * <p>
 * In the picture, <code>"Green"</code> is the current choice.
 * Pushing the mouse button down on the object causes a menu to
 * appear with the current choice highlighted.
 * <p>
 * Some native platforms do not support arbitrary resizing of Choice
 * components and the behavior of setSize()/getSize() is bound by 
 * such limitations.
 * Native GUI Choice components' size are often bound by such
 * attributes as font size and length of items contained within 
 * the Choice.
 * <p>
 * @version	1.65 02/06/02
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 * @since       JDK1.0
 */
public class Choice extends Component implements ItemSelectable, Accessible {
    /**
     * The items for the Choice.
	 * This can be a null value.
	 * @serial
     * @see add()
     * @see addItem()
     * @see getItem()
     * @see getItemCount()
     * @see insert()
     * @see remove()
     */
    Vector pItems;

    /**
     * The index of the current choice for this Choice.
	 * @serial
     * @see getSelectedItem
     * @see select()
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
     */
    public Choice() {
	pItems = new Vector();
    }

    /**
     * Construct a name for this component.  Called by getName() when the
     * name is null.
     */
    String constructComponentName() {
        synchronized (getClass()) {
	    return base + nameCounter++;
	}
    }

    /**
     * Creates the Choice's peer.  This peer allows us to change the look
     * of the Choice without changing its functionality.
     * @see     java.awt.Toolkit#createChoice(java.awt.Choice)
     * @see     java.awt.Component#getToolkit()
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
     */
    public String getItem(int index) {
	return getItemImpl(index);
    }

    /*
     * This is called by the native code, so client code can't
     * be called on the toolkit thread.
     */
    final String getItemImpl(int index) {
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
    public void addItem(String item) {
        synchronized (this) {
	    addItemNoInvalidate(item);
	}

	// This could change the preferred size of the Component.
	if (valid) {
	    invalidate();
	}
    }

    /**
     * Adds an item to this Choice, but does not invalidate the Choice.
     * Client methods must provide their own synchronization before
     * invoking this method.
     * @param item the item to be added
     * @exception NullPointerException If the item's value is equal to null.
     */
    private void addItemNoInvalidate(String item) {
        if (item == null) {
	    throw new 
	        NullPointerException("cannot add null item to Choice");
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
    public void insert(String item, int index) {
        synchronized (this) {
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
		removeNoInvalidate(index);
	    }

	    addItemNoInvalidate(item);

	    /* Add the removed items back to the choice menu, they 
	       are already in the correct order in the temp vector.
	    */
	    for (int i = 0; i < tempItems.size()  ; i++) {
	        addItemNoInvalidate((String)tempItems.elementAt(i));
	    }
	}

	// This could change the preferred size of the Component.
	if (valid) {
	    invalidate();
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
    public void remove(String item) {
        synchronized (this) {
	    int index = pItems.indexOf(item);
	    if (index < 0) {
	        throw new IllegalArgumentException("item " + item +
						   " not found in choice");
	    } else {
	        removeNoInvalidate(index);
	    }
	}

	// This could change the preferred size of the Component.
	if (valid) {
	    invalidate();
	}
    }

    /**
     * Removes an item from the choice menu
     * at the specified position.
     * @param      position the position of the item.
     * @since      JDK1.1
     */
    public void remove(int position) {
        synchronized (this) {
	    removeNoInvalidate(position);
	}

	// This could change the preferred size of the Component.
	if (valid) {
	    invalidate();
	}
    }

    /**
     * Removes an item from the Choice at the specified position, but
     * does not invalidate the Choice. Client methods must provide their
     * own synchronization before invoking this method.
     * @param      position the position of the item.
     */
    private void removeNoInvalidate(int position) {
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
    public void removeAll() {
        synchronized (this) {
            if (peer != null) {
                ((ChoicePeer)peer).removeAll();
            }
            pItems.removeAllElements();
            selectedIndex = -1;
	}

	// This could change the preferred size of the Component.
	if (valid) {
	    invalidate();
	}
    }

    /**
     * Gets a representation of the current choice as a string.
     * @return    a string representation of the currently
     *                     selected item in this choice menu.
     * @see       java.awt.Choice#getSelectedIndex
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
     */
    public synchronized void select(int pos) {
	if ((pos >= pItems.size()) || (pos < 0)) {
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
     * If l is null, no exception is thrown and no action is performed.
     * @param         l    the item listener.
     * @see           java.awt.event.ItemEvent
     * @see           java.awt.event.ItemListener
     * @see           java.awt.Choice#removeItemListener
     * @since         JDK1.1
     */
    public synchronized void addItemListener(ItemListener l) {
	if (l == null) {
	   return;
	}
        itemListener = AWTEventMulticaster.add(itemListener, l);
        newEventsOnly = true;
    }

    /**
     * Removes the specified item listener so that it no longer receives
     * item events from this <code>Choice</code> menu.
     * If l is null, no exception is thrown and no action is performed.
     * @param         l    the item listener.
     * @see           java.awt.event.ItemEvent
     * @see           java.awt.event.ItemListener
     * @see           java.awt.Choice#addItemListener
     * @since         JDK1.1
     */
    public synchronized void removeItemListener(ItemListener l) {
	if (l == null) {
	    return;
	}
        itemListener = AWTEventMulticaster.remove(itemListener, l);
    }

    /**
     * Return an array of all the listeners that were added to the Choice
     * with addXXXListener(), where XXX is the name of the <code>listenerType</code>
     * argument.  For example, to get all of the ItemListener(s) for the
     * given Choice <code>c</code>, one would write:
     * <pre>
     * ItemListener[] ils = (ItemListener[])(c.getListeners(ItemListener.class))
     * </pre>
     * If no such listener list exists, then an empty array is returned.
     * 
     * @param    listenerType   Type of listeners requested
     * @return	 all of the listeners of the specified type supported by this choice
     * @since 1.3
     */
    public EventListener[] getListeners(Class listenerType) { 
	EventListener l = null; 
	if  (listenerType == ItemListener.class) { 
	    l = itemListener;
	} else {
	    return super.getListeners(listenerType);
	}
	return AWTEventMulticaster.getListeners(l, listenerType);
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
     */
    protected String paramString() {
	return super.paramString() + ",current=" + getSelectedItem();
    }


    /* Serialization support.
     */

	/*
    * Choice Serial Data Version.
    * @serial
    */
    private int choiceSerializedDataVersion = 1;

	/**
    * Writes default serializable fields to stream.  Writes
    * a list of serializable ItemListener(s) as optional data.
    * The non-serializable ItemListner(s) are detected and
    * no attempt is made to serialize them.
    *
    * @serialData Null terminated sequence of 0 or more pairs.
    *             The pair consists of a String and Object.
    *             The String indicates the type of object and
    *             is one of the following :
    *             itemListenerK indicating and ItemListener object.
    *
    * @see AWTEventMulticaster.save(ObjectOutputStream, String, EventListener)
    * @see java.awt.Component.itemListenerK
    */
    private void writeObject(ObjectOutputStream s)
      throws java.io.IOException
    {
      s.defaultWriteObject();

      AWTEventMulticaster.save(s, itemListenerK, itemListener);
      s.writeObject(null);
    }

	/*
    * Read the ObjectInputStream and if it isnt null
    * add a listener to receive item events fired
    * by the Choice item.
    * Unrecognised keys or values will be Ignored.
    * @serial
    * @see removeActionListener()
    * @see addActionListener()
    */
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


/////////////////
// Accessibility support
////////////////


    /**
     * Gets the AccessibleContext associated with this Choice. 
     * For Choice components, the AccessibleContext takes the form of an 
     * AccessibleAWTChoice. 
     * A new AccessibleAWTChoice instance is created if necessary.
     *
     * @return an AccessibleAWTChoice that serves as the 
     *         AccessibleContext of this Choice
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTChoice();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the 
     * <code>Choice</code> class.  It provides an implementation of the 
     * Java Accessibility API appropriate to choice user-interface elements.
     */
    protected class AccessibleAWTChoice extends AccessibleAWTComponent
    implements AccessibleAction {

	public AccessibleAWTChoice() {
	    super();
	}

	/**
         * Get the AccessibleAction associated with this object.  In the
         * implementation of the Java Accessibility API for this class, 
	 * return this object, which is responsible for implementing the
         * AccessibleAction interface on behalf of itself.
	 * 
	 * @return this object
	 * @see AccessibleAction
	 */
	public AccessibleAction getAccessibleAction() {
	    return this;
	}

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the 
	 * object
         * @see AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.COMBO_BOX;
        }

	/**
	 * Returns the number of accessible actions available in this object
	 * If there are more than one, the first one is considered the "default"
	 * action of the object.
	 *
	 * @return the zero-based number of Actions in this object
	 */
	public int getAccessibleActionCount() {
	    return 0;  //  To be fully implemented in a future release
	}

	/**
	 * Returns a description of the specified action of the object.
	 *
	 * @param i zero-based index of the actions
	 * @return a String description of the action
	 * @see #getAccessibleActionCount
	 */
	public String getAccessibleActionDescription(int i) {
	    return null;  //  To be fully implemented in a future release
	}

	/**
	 * Perform the specified Action on the object
	 *
	 * @param i zero-based index of actions
	 * @return true if the action was performed; otherwise false.
	 * @see #getAccessibleActionCount
	 */
	public boolean doAccessibleAction(int i) {
	    return false;  //  To be fully implemented in a future release
	}

    } // inner class AccessibleAWTChoice

}
