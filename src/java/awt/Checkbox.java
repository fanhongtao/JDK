/*
 * @(#)Checkbox.java	1.42 98/07/01
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

import java.awt.peer.CheckboxPeer;
import java.awt.event.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * A check box is a graphical component that can be in either an 
 * "on" (<code>true</code>) or "off" (<code>false</code>) state.
 * Clicking on a check box changes its state from 
 * "on" to "off," or from "off" to "on." 
 * <p>
 * The following code example creates a set of check boxes in
 * a grid layout: 
 * <p>
 * <hr><blockquote><pre>
 * setLayout(new GridLayout(3, 1));
 * add(new Checkbox("one", null, true));
 * add(new Checkbox("two"));
 * add(new Checkbox("three"));
 * </pre></blockquote><hr>
 * <p>
 * This image depicts the check boxes and grid layout
 * created by this code example:
 * <p>
 * <img src="images-awt/Checkbox-1.gif"
 * ALIGN=center HSPACE=10 VSPACE=7>
 * <p>
 * The button labeled <code>one</code> is in the "on" state, and the 
 * other two are in the "off" state. In this example, which uses the
 * <code>GridLayout</code> class, the states of the three check 
 * boxes are set independently.
 * <p>
 * Alternatively, several check boxes can be grouped together under 
 * the control of a single object, using the 
 * <code>CheckboxGroup</code> class. 
 * In a check box group, at most one button can be in the "on" 
 * state at any given time. Clicking on a check box to turn it on
 * forces any other check box in the same group that is on 
 * into the "off" state.
 *
 * @version	1.42 07/01/98
 * @author 	Sami Shaio
 * @see         java.awt.GridLayout
 * @see         java.awt.CheckboxGroup
 * @since       JDK1.0
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
    void setStateInternal(boolean state) {
	this.state = state;
	CheckboxPeer peer = (CheckboxPeer)this.peer;
	if (peer != null) {
	    peer.setState(state);
	}
    }

    /**
     * Creates a check box with no label. The state of this 
     * check box is set to "off," and it is not part of any 
     * check box group. 
     * @since     JDK1.0
     */
    public Checkbox() {
        this("", false, null);
    }

    /**
     * Creates a check box with the specified label.  The state 
     * of this check box is set to "off," and it is not part of  
     * any check box group. 
     * @param     label   a string label for this check box, 
     *                        or <code>null</code> for no label.
     * @since     JDK1.0
     */
    public Checkbox(String label) {
	this(label, false, null);
    }

    /**
     * Creates a check box with the specified label.  The state 
     * of this check box is as specified by the <code>state</code> 
     * argument, and it is not part of any check box group. 
     * @param     label   a string label for this check box, 
     *                        or <code>null</code> for no label.
     * @param     state    the initial state of this check box.
     * @since     JDK1.0
     */
    public Checkbox(String label, boolean state) {
        this(label, state, null);
    }

    /**
     * Creates a check box with the specified label, in the specified 
     * check box group, and set to the specified state. 

     * @param     label   a string label for this check box, 
     *                        or <code>null</code> for no label.
     * @param     state   the initial state of this check box.
     * @param     group   a check box group for this check box, 
     *                           or <code>null</code> for no group.
     * @since     JDK1.1
     */
    public Checkbox(String label, boolean state, CheckboxGroup group) {
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
     * Construct a name for this component.  Called by getName() when the
     * name is null.
     */
    String constructComponentName() {
        return base + nameCounter++;
    }

    /**
     * Creates the peer of the Checkbox. The peer allows you to change the
     * look of the Checkbox without changing its functionality.
     * @see     java.awt.Toolkit#createCheckbox(java.awt.Checkbox)
     * @see     java.awt.Component#getToolkit()
     * @since   JDK1.0
     */
    public void addNotify() {
        synchronized (getTreeLock()) {
	    if (peer == null)
			peer = getToolkit().createCheckbox(this);
	    super.addNotify();
        }
    }

    /**
     * Gets the label of this check box.
     * @return   the label of this check box, or <code>null</code> 
     *                  if this check box has no label.
     * @see      java.awt.Checkbox#setLabel
     * @since    JDK1.0
     */
    public String getLabel() {
	return label;
    }

    /**
     * Sets this check box's label to be the string argument. 
     * @param    label   a string to set as the new label, or 
     *                        <code>null</code> for no label.
     * @see      java.awt.Checkbox#getLabel 
     * @since    JDK1.0
     */
    public synchronized void setLabel(String label) {
	this.label = label;
	CheckboxPeer peer = (CheckboxPeer)this.peer;
	if (peer != null) {
	    peer.setLabel(label);
	}
    }

    /** 
     * Determines whether this check box is in the "on" or "off" state.
     * The boolean value <code>true</code> indicates the "on" state,  
     * and <code>false</code> indicates the "off" state.
     * @return    the state of this check box, as a boolean value. 
     * @see       java.awt.Checkbox#setState  
     * @since     JDK1.0
     */
    public boolean getState() {
	return state;
    }
	
    /** 
     * Sets the state of this check box to the specified state. 
     * The boolean value <code>true</code> indicates the "on" state,  
     * and <code>false</code> indicates the "off" state.
     * @param     state   the boolean state of the check box.
     * @see       java.awt.Checkbox#getState  
     * @since     JDK1.0
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
     * Determines this check box's group. 
     * @return     this check box's group, or <code>null</code> 
     *               if the check box is not part of a check box group.
     * @see        java.awt.Checkbox#setCheckboxGroup 
     * @since      JDK1.0
     */
    public CheckboxGroup getCheckboxGroup() {
	return group;
    }

    /**
     * Sets this check box's group to be the specified check box group. 
     * If this check box is already in a different check box group, 
     * it is first taken out of that group. 
     * @param     g   the new check box group, or <code>null</code> 
     *                to remove this check box from any check box group.
     * @see       java.awt.Checkbox#getCheckboxGroup  
     * @since     JDK1.0
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
     * Adds the specified item listener to receive item events from
     * this check box.
     * @param         l    the item listener.
     * @see           java.awt.event.ItemEvent
     * @see           java.awt.event.ItemListener
     * @see           java.awt.Checkbox#removeItemListener
     * @since         JDK1.1
     */ 
    public synchronized void addItemListener(ItemListener l) {
        itemListener = AWTEventMulticaster.add(itemListener, l);
        newEventsOnly = true;
    }

    /**
     * Removes the specified item listener so that the item listener 
     * no longer receives item events from this check box. 
     * @param         l    the item listener.
     * @see           java.awt.event.ItemEvent
     * @see           java.awt.event.ItemListener
     * @see           java.awt.Checkbox#addItemListener
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
     * Processes events on this check box. 
     * If the event is an instance of <code>ItemEvent</code>,
     * this method invokes the <code>processItemEvent</code> method. 
     * Otherwise, it calls its superclass's <code>processEvent</code> method.
     * @param         e the event.
     * @see           java.awt.event.ItemEvent
     * @see           java.awt.Checkbox#processItemEvent
     * @since         JDK1.1
     */
    protected void processEvent(AWTEvent e) {
        if (e instanceof ItemEvent) {
            processItemEvent((ItemEvent)e);
            return;
        }
	super.processEvent(e);
    }

    /** 
     * Processes item events occurring on this check box by
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
     * @see         java.awt.Checkbox#addItemListener
     * @see         java.awt.Component#enableEvents
     * @since       JDK1.1
     */  
    protected void processItemEvent(ItemEvent e) {
        if (itemListener != null) {
            itemListener.itemStateChanged(e);
        }
    }

    /**
     * Returns the parameter string representing the state of 
     * this check box. This string is useful for debugging. 
     * @return    the parameter string of this check box.
     * @since     JDK1.0
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
