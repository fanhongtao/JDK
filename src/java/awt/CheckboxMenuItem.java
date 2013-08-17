/*
 * @(#)CheckboxMenuItem.java	1.25 97/03/03
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

import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.event.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * This class produces a checkbox that represents a choice in a menu.
 *
 * @version 1.25, 03/03/97
 * @author 	Sami Shaio
 */
public class CheckboxMenuItem extends MenuItem implements ItemSelectable {

    boolean state = false;

    transient ItemListener itemListener;

    private static final String base = "chkmenuitem";
    private static int nameCounter = 0;

    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = 6190621106981774043L;

    /**
     * Creates a checkbox menu item with an empty label, initially set
     * to off (false state).
     */
    public CheckboxMenuItem() {
	this("", false);
    }

    /**
     * Creates the checkbox item with the specified label, initially
     * set to off (false state).
     * @param label the button label
     */
    public CheckboxMenuItem(String label) {
	this(label, false);
    }

    /**
     * Creates a checkbox menu item with the specified label and state.
     * @param label the button label
     * @param state the initial state of the menu item:  true
     * indicates "on"; false indicates "off".
     */
    public CheckboxMenuItem(String label, boolean state) {
        super(label);
	this.name = base + nameCounter++;
    	this.state = state;
    }

    /**
     * Creates the peer of the checkbox item.  This peer allows us to
     * change the look of the checkbox item without changing its 
     * functionality.
     */
    public void addNotify() {
	peer = Toolkit.getDefaultToolkit().createCheckboxMenuItem(this);
	super.addNotify();
    }

    /**
     * Returns the state of this MenuItem. This method is only valid for a 
     * Checkbox.
     */
    public boolean getState() {
	return state;
    }

    /**
     * Sets the state of this MenuItem if it is a Checkbox.
     * @param t the specified state of the checkbox
     */
    public synchronized void setState(boolean b) {
	state = b;
	CheckboxMenuItemPeer peer = (CheckboxMenuItemPeer)this.peer;
	if (peer != null) {
	    peer.setState(b);
	}
    }

    /**
     * Returns the an array (length 1) containing the checkbox menu item
     * label or null if the checkbox is not selected.
     * @see ItemSelectable
     */
    public synchronized Object[] getSelectedObjects() {
        if (state) {
            Object[] items = new Object[1];
            items[0] = label;
            return items;
        }
        return null;
    }

    /**
     * Adds the specified item listener to recieve item events from
     * this checkbox menu item.
     * @param l the item listener
     */ 
    public synchronized void addItemListener(ItemListener l) {
        itemListener = AWTEventMulticaster.add(itemListener, l);
        newEventsOnly = true;
    }

    /**
     * Removes the specified item listener so it no longer receives
     * item events from this checkbox menu item.
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
     * Processes events on this checkbox menu item. If the event is
     * an ItemEvent, it invokes the handleItemEvent method.
     * NOTE: Checkbox menu items currently only support action and
     * item events.
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
     * Processes item events occurring on this checkbox menu item by
     * dispatching them to any registered ItemListener objects.
     * NOTE: This method will not be called unless item events
     * are enabled for this checkbox menu item; this happens when one of the
     * following occurs:
     * a) An ItemListener object is registered via addItemListener()
     * b) Item events are enabled via enableEvents()
     * @see MenuItem#enableEvents
     * @param e the item event
     */  
    protected void processItemEvent(ItemEvent e) {
        if (itemListener != null) {
            itemListener.itemStateChanged(e);
        }
    }

    /**
     * Returns the parameter String of this button.
     */
    public String paramString() {
	return super.paramString() + ",state=" + state;
    }

    /* Serialization support. 
     */

    private int checkboxMenuItemSerializedDataVersion = 1;


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
