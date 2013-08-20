/*
 * @(#)DefaultButtonModel.java	1.45 04/05/05
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.Serializable;
import java.util.EventListener;
import javax.swing.event.*;

/**
 * The default implementation of a <code>Button</code> component's data model.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @version 1.45 05/05/04
 * @author Jeff Dinkins
 */
public class DefaultButtonModel implements ButtonModel, Serializable {

    protected int stateMask = 0;
    protected String actionCommand = null;
    protected ButtonGroup group = null;
        
    protected int mnemonic = 0;

    /**
     * Only one <code>ChangeEvent</code> is needed per button model
     * instance since the
     * event's only state is the source property.  The source of events
     * generated is always "this".
     */
    protected transient ChangeEvent changeEvent = null;
    protected EventListenerList listenerList = new EventListenerList();
    
        
    /**
     * Constructs a default <code>JButtonModel</code>.
     *
     */
    public DefaultButtonModel() {
        stateMask = 0;
        setEnabled(true);
    }
        
    /**
     * Indicates partial commitment towards choosing the
     * button.
     */
    public final static int ARMED = 1 << 0;
        
    /**
     * Indicates that the button has been selected. Only needed for
     * certain types of buttons - such as RadioButton or Checkbox.
     */
    public final static int SELECTED = 1 << 1;
        
    /**
     * Indicates that the button has been "pressed"
     * (typically, when the mouse is released).
     */
    public final static int PRESSED = 1 << 2;
        
    /**
     * Indicates that the button can be selected by
     * an input device (such as a mouse pointer).
     */
    public final static int ENABLED = 1 << 3;

    /**
     * Indicates that the mouse is over the button.
     */
    public final static int ROLLOVER = 1 << 4;
        
    /**
     * Sets the <code>actionCommand</code> string that gets sent as
     * part of the event when the button is pressed.
     *
     * @param actionCommand the <code>String</code> that identifies
     * the generated event
     */
    public void setActionCommand(String actionCommand) {
        this.actionCommand = actionCommand;
    }
        
    /**
     * Returns the action command for this button. 
     *
     * @return the <code>String</code> that identifies the generated event
     * @see #setActionCommand
     */
    public String getActionCommand() {
        return actionCommand;
    }

    /**
     * Indicates partial commitment towards pressing the
     * button. 
     *
     * @return true if the button is armed, and ready to be pressed
     * @see #setArmed
     */
    public boolean isArmed() {
        return (stateMask & ARMED) != 0;
    }
        
    /**
     * Indicates if the button has been selected. Only needed for
     * certain types of buttons - such as RadioButton or Checkbox.
     *
     * @return true if the button is selected
     */
    public boolean isSelected() {
        return (stateMask & SELECTED) != 0;
    }
        
    /**
     * Indicates whether the button can be selected or pressed by
     * an input device (such as a mouse pointer). (Checkbox-buttons
     * are selected, regular buttons are "pressed".)
     *
     * @return true if the button is enabled, and therefore
     *         selectable (or pressable)
     */
    public boolean isEnabled() {
        return (stateMask & ENABLED) != 0;
    }
        
    /**
     * Indicates whether button has been pressed.
     *
     * @return true if the button has been pressed
     */
    public boolean isPressed() {
        return (stateMask & PRESSED) != 0;
    }
        
    /**
     * Indicates that the mouse is over the button.
     *
     * @return true if the mouse is over the button
     */
    public boolean isRollover() {
        return (stateMask & ROLLOVER) != 0;
    }
        
    /**
     * Marks the button as "armed". If the mouse button is
     * released while it is over this item, the button's action event
     * fires. If the mouse button is released elsewhere, the
     * event does not fire and the button is disarmed.
     * 
     * @param b true to arm the button so it can be selected
     */
    public void setArmed(boolean b) {
        if((isArmed() == b) || !isEnabled()) {
            return;
        }
            
        if (b) {
            stateMask |= ARMED;
        } else {
            stateMask &= ~ARMED;
        }
            
        fireStateChanged();
    }

    /**
     * Enables or disables the button.
     * 
     * @param b true to enable the button
     * @see #isEnabled
     */
    public void setEnabled(boolean b) {
        if(isEnabled() == b) {
            return;
        }
            
        if (b) {
            stateMask |= ENABLED;
        } else {
            stateMask &= ~ENABLED;
	    // unarm and unpress, just in case
            stateMask &= ~ARMED;
            stateMask &= ~PRESSED;
        }

            
        fireStateChanged();
    }
        
    /**
     * Selects or deselects the button.
     *
     * @param b true selects the button,
     *          false deselects the button
     */
    public void setSelected(boolean b) {
        if (this.isSelected() == b) {
            return;
        }

        if (b) {
            stateMask |= SELECTED;
        } else {
            stateMask &= ~SELECTED;
        }

        fireItemStateChanged(
                new ItemEvent(this,
                              ItemEvent.ITEM_STATE_CHANGED,
                              this,
                              b ?  ItemEvent.SELECTED : ItemEvent.DESELECTED));
        
        fireStateChanged();
        
    }
        
        
    /**
     * Sets the button to pressed or unpressed.
     * 
     * @param b true to set the button to "pressed"
     * @see #isPressed
     */
    public void setPressed(boolean b) {
        if((isPressed() == b) || !isEnabled()) {
            return;
        }
        
        if (b) {
            stateMask |= PRESSED;
        } else {
            stateMask &= ~PRESSED;
        }

        if(!isPressed() && isArmed()) {
            int modifiers = 0;
            AWTEvent currentEvent = EventQueue.getCurrentEvent();
            if (currentEvent instanceof InputEvent) {
                modifiers = ((InputEvent)currentEvent).getModifiers();
            } else if (currentEvent instanceof ActionEvent) {
                modifiers = ((ActionEvent)currentEvent).getModifiers();
            }
            fireActionPerformed(
                new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                                getActionCommand(),
                                EventQueue.getMostRecentEventTime(),
                                modifiers));
        }
            
        fireStateChanged();
    }   

    /**
     * Sets or clears the button's rollover state
     * 
     * @param b true to turn on rollover
     * @see #isRollover
     */
    public void setRollover(boolean b) {
        if((isRollover() == b) || !isEnabled()) {
            return;
        }
        
        if (b) {
            stateMask |= ROLLOVER;
        } else {
            stateMask &= ~ROLLOVER;
        }

        fireStateChanged();
    }

    /**
     * Sets the keyboard mnemonic (shortcut key or
     * accelerator key) for this button.
     *
     * @param key an int specifying the accelerator key
     */
    public void setMnemonic(int key) {
	mnemonic = key;
	fireStateChanged();
    }

    /**
     * Gets the keyboard mnemonic for this model
     *
     * @return an int specifying the accelerator key
     * @see #setMnemonic
     */
    public int getMnemonic() {
	return mnemonic;
    }

    /**
     * Adds a <code>ChangeListener</code> to the button.
     *
     * @param l the listener to add
     */
    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }
    
    /**
     * Removes a <code>ChangeListener</code> from the button.
     *
     * @param l the listener to remove
     */
    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }

    /**
     * Returns an array of all the change listeners
     * registered on this <code>DefaultButtonModel</code>.
     *
     * @return all of this model's <code>ChangeListener</code>s 
     *         or an empty
     *         array if no change listeners are currently registered
     * 
     * @see #addChangeListener
     * @see #removeChangeListener
     * 
     * @since 1.4
     */
    public ChangeListener[] getChangeListeners() {
        return (ChangeListener[])listenerList.getListeners(
                ChangeListener.class);
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is created lazily.
     *
     * @see EventListenerList
     */
    protected void fireStateChanged() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ChangeListener.class) {
                // Lazily create the event:
                if (changeEvent == null)
                    changeEvent = new ChangeEvent(this);
                ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
            }          
        }
    }   
    
    /**
     * Adds an <code>ActionListener</code> to the button.
     *
     * @param l the listener to add
     */
    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }
        
    /**
     * Removes an <code>ActionListener</code> from the button.
     *
     * @param l the listener to remove
     */
    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }

    /**
     * Returns an array of all the action listeners
     * registered on this <code>DefaultButtonModel</code>.
     *
     * @return all of this model's <code>ActionListener</code>s 
     *         or an empty
     *         array if no action listeners are currently registered
     *
     * @see #addActionListener
     * @see #removeActionListener
     *
     * @since 1.4
     */
    public ActionListener[] getActionListeners() {
        return (ActionListener[])listenerList.getListeners(
                ActionListener.class);
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.
     *
     * @param e the <code>ActionEvent</code> to deliver to listeners
     * @see EventListenerList
     */
    protected void fireActionPerformed(ActionEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ActionListener.class) {
                // Lazily create the event:
                // if (changeEvent == null)
                // changeEvent = new ChangeEvent(this);
                ((ActionListener)listeners[i+1]).actionPerformed(e);
            }          
        }
    }   

    /**
     * Adds an <code>ItemListener</code> to the button.
     *
     * @param l the listener to add
     */
    public void addItemListener(ItemListener l) {
        listenerList.add(ItemListener.class, l);
    }
        
    /**
     * Removes an <code>ItemListener</code> from the button.
     *
     * @param l the listener to remove
     */
    public void removeItemListener(ItemListener l) {
        listenerList.remove(ItemListener.class, l);
    }

    /**
     * Returns an array of all the item listeners
     * registered on this <code>DefaultButtonModel</code>.
     *
     * @return all of this model's <code>ItemListener</code>s 
     *         or an empty
     *         array if no item listeners are currently registered
     *
     * @see #addItemListener
     * @see #removeItemListener
     *
     * @since 1.4
     */
    public ItemListener[] getItemListeners() {
        return (ItemListener[])listenerList.getListeners(ItemListener.class);
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  
     *
     * @param e the <code>ItemEvent</code> to deliver to listeners
     * @see EventListenerList
     */
    protected void fireItemStateChanged(ItemEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ItemListener.class) {
                // Lazily create the event:
                // if (changeEvent == null)
                // changeEvent = new ChangeEvent(this);
                ((ItemListener)listeners[i+1]).itemStateChanged(e);
            }          
        }
    }   

    /**
     * Returns an array of all the objects currently registered as
     * <code><em>Foo</em>Listener</code>s
     * upon this model.
     * <code><em>Foo</em>Listener</code>s
     * are registered using the <code>add<em>Foo</em>Listener</code> method.
     * <p>
     * You can specify the <code>listenerType</code> argument
     * with a class literal, such as <code><em>Foo</em>Listener.class</code>.
     * For example, you can query a <code>DefaultButtonModel</code>
     * instance <code>m</code>
     * for its action listeners
     * with the following code:
     *
     * <pre>ActionListener[] als = (ActionListener[])(m.getListeners(ActionListener.class));</pre>
     *
     * If no such listeners exist,
     * this method returns an empty array.
     *
     * @param listenerType  the type of listeners requested;
     *          this parameter should specify an interface
     *          that descends from <code>java.util.EventListener</code>
     * @return an array of all objects registered as
     *          <code><em>Foo</em>Listener</code>s
     *          on this model,
     *          or an empty array if no such
     *          listeners have been added
     * @exception ClassCastException if <code>listenerType</code> doesn't
     *          specify a class or interface that implements
     *          <code>java.util.EventListener</code>
     *
     * @see #getActionListeners
     * @see #getChangeListeners
     * @see #getItemListeners
     *
     * @since 1.3
     */
    public <T extends EventListener> T[] getListeners(Class<T> listenerType) { 
	return listenerList.getListeners(listenerType); 
    }

    /** Overridden to return <code>null</code>. */
    public Object[] getSelectedObjects() {
        return null; 
    }

    /**
     * Identifies the group this button belongs to --
     * needed for radio buttons, which are mutually
     * exclusive within their group.
     *
     * @param group the <code>ButtonGroup</code> this button belongs to
     */
    public void setGroup(ButtonGroup group) {
        this.group = group;
    }

    /**
     * Returns the group that this button belongs to.
     * Normally used with radio buttons, which are mutually
     * exclusive within their group.
     *
     * @return a <code>ButtonGroup</code> that this button belongs to
     *
     * @since 1.3
     */
    public ButtonGroup getGroup() {
        return group;
    }

}
