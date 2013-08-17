/*
 * @(#)Checkbox.java	1.35 97/03/03
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

import java.awt.peer.CheckboxPeer;
import java.awt.event.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * A Checkbox object is a graphical user interface element that has a boolean 
 * state.
 *
 * @version	1.35 03/03/97
 * @author 	Sami Shaio
 */
public class Checkbox extends Component implements ItemSelectable {
    /**
     * The label of the Checkbox.
     */
    String label;

    /**
     * The state of the Checkbox.
     */
    boolean state;

    /**
     * The check box group.
     */
    CheckboxGroup group;

    transient ItemListener itemListener;

    private static final String base = "checkbox";
    private static int nameCounter = 0;

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = 7270714317450821763L;

    /**
     * Helper function for setState and CheckboxGroup.setSelectedCheckbox
     * Should remain package-private.
     */
    synchronized void setStateInternal(boolean state) {
	this.state = state;
	CheckboxPeer peer = (CheckboxPeer)this.peer;
	if (peer != null) {
	    peer.setState(state);
	}
    }

    /**
     * Constructs a Checkbox with an empty label.  The check box starts in a
     * false state and is not part of any check box group.
     */
    public Checkbox() {
        this("", false, null);
    }

    /**
     * Constructs a Checkbox with the specified label.  The check box
     * starts in a false state and is not part of any check box group.
     * @param label the label on the Checkbox
     */
    public Checkbox(String label) {
	this(label, false, null);
    }

    /**
     * Constructs a Checkbox with the specified label.  The check box
     * starts in the specified state and is not part of any check box
     * group. 
     * @param label the label on the Checkbox
     * @param state is the initial state of this Checkbox
     * @param group the CheckboxGroup this Checkbox is in
     */
    public Checkbox(String label, boolean state) {
        this(label, state, null);
    }

    /**
     * Constructs a Checkbox with the specified label, set to the
     * specified state, and in the specified check box group.
     * @param label the label on the Checkbox
     * @param state is the initial state of this Checkbox
     * @param group the CheckboxGroup this Checkbox is in
     */
    public Checkbox(String label, boolean state, CheckboxGroup group) {
	this.name = base + nameCounter++;        
	this.label = label;
	this.state = state;
	this.group = group;
	if (state && (group != null)) {
	    group.setSelectedCheckbox(this);
	}
    }

    /**
     * Constructs a Checkbox with the specified label, set to the
     * specified state, and in the specified check box group.
     */
    public Checkbox(String label, CheckboxGroup group, boolean state) {
    	this(label, state, group);
    }

    /**
     * Creates the peer of the Checkbox. The peer allows you to change the
     * look of the Checkbox without changing its functionality.
     */
    public void addNotify() {
	peer = getToolkit().createCheckbox(this);
	super.addNotify();
    }

    /**
     * Gets the label of the check box.
     * @see #setLabel
     */
    public String getLabel() {
	return label;
    }

    /**
     * Sets this check box's label to be the specified string.
     * @param label the label of the button
     * @see #getLabel
     */
    public synchronized void setLabel(String label) {
	this.label = label;
	CheckboxPeer peer = (CheckboxPeer)this.peer;
	if (peer != null) {
	    peer.setLabel(label);
	}
    }

    /** 
     * Returns the boolean state of the Checkbox. 
     * @see #setState
     */
    public boolean getState() {
	return state;
    }
	
    /** 
     * Sets the Checkbox to the specifed boolean state.
     * @param state the boolean state 
     * @see #getState
     */
    public void setState(boolean state) {
	/* Cannot hold check box lock when calling group.setSelectedCheckbox. */
    	CheckboxGroup group = this.group;
	if (group != null) {
	    if (state) {
		group.setSelectedCheckbox(this);
	    } else if (group.getSelectedCheckbox() == this) {
		state = true;
	    }
	}
	setStateInternal(state);
    }

    /**
     * Returns the an array (length 1) containing the checkbox
     * label or null if the checkbox is not selected.
     * @see ItemSelectable
     */
    public Object[] getSelectedObjects() {
        if (state) {
            Object[] items = new Object[1];
            items[0] = label;
            return items;
        }
        return null;
    }

    /**
     * Returns the checkbox group.
     * @see #setCheckboxGroup
     */
    public CheckboxGroup getCheckboxGroup() {
	return group;
    }

    /**
     * Sets the CheckboxGroup to the specified group.
     * @param g the new CheckboxGroup
     * @see #getCheckboxGroup
     */
    public void setCheckboxGroup(CheckboxGroup g) {
    	CheckboxGroup group = this.group;
	if (group != null) {
	    group.setSelectedCheckbox(null);
	}
	/* Locking check box above could cause deadlock with
	 * CheckboxGroup's setSelectedCheckbox method.
	 */
	synchronized (this) {
	    this.group = g;
	    CheckboxPeer peer = (CheckboxPeer)this.peer;
	    if (peer != null) {
		peer.setCheckboxGroup(g);
	    }
	}
    }

    /**
     * Adds the specified item listener to recieve item events from
     * this checkbox.
     * @param l the item listener
     */ 
    public synchronized void addItemListener(ItemListener l) {
        itemListener = AWTEventMulticaster.add(itemListener, l);
        newEventsOnly = true;
    }

    /**
     * Removes the specified item listener so that it no longer
     * receives item events from this checkbox.
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
     * Processes events on this checkbox. If the event is an ItemEvent,
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
     * Processes item events occurring on this checkbox by
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
     * Returns the parameter String of this Checkbox.
     */
    protected String paramString() {
	String str = super.paramString();
	String label = this.label;
	if (label != null) {
	    str += ",label=" + label;
	}
	return str + ",state=" + state;
    }


    /* Serialization support. 
     */

    private int checkboxSerializedDataVersion = 1;


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
