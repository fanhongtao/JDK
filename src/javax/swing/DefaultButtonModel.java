/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
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
 * The default implementation of a Button component's data model.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.33 02/06/02
 * @author Jeff Dinkins
 */
public class DefaultButtonModel implements ButtonModel, Serializable {

    protected int stateMask = 0;
    protected String actionCommand = null;
    protected ButtonGroup group = null;
        
    protected int mnemonic = 0;

    /**
     * Only one ChangeEvent is needed per button model instance since the
     * event's only state is the source property.  The source of events
     * generated is always "this".
     */
    protected transient ChangeEvent changeEvent = null;
    protected EventListenerList listenerList = new EventListenerList();
    
        
    /**
     * Constructs a JButtonModel
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
     * Sets the actionCommand string that gets sent as part of the
     * event when the button is pressed.
     *
     * @param s the String that identifies the generated event
     */
    public void setActionCommand(String actionCommand) {
        this.actionCommand = actionCommand;
    }
        
    /**
     * Returns the action command for this button. 
     *
     * @return the String that identifies the generated event
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
     * Indicates if the button can be selected or pressed by
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
     * Indicates if button has been pressed.
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
     *          false deselects the button.
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
            fireActionPerformed(
                new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                                getActionCommand())
                );
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
     * Adds a ChangeListener to the button.
     *
     * @param l the listener to add
     */
    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }
    
    /**
     * Removes a ChangeListener from the button.
     *
     * @param l the listener to remove
     */
    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
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
     * Adds an ActionListener to the button.
     *
     * @param l the listener to add
     */
    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }
        
    /**
     * Removes an ActionListener from the button.
     *
     * @param l the listener to remove
     */
    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     *
     * @param e the ActionEvent to deliver to listeners
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
     * Adds an ItemListener to the button.
     *
     * @param l the listener to add
     */
    public void addItemListener(ItemListener l) {
        listenerList.add(ItemListener.class, l);
    }
        
    /**
     * Removes an ItemListener from the button.
     *
     * @param l the listener to remove
     */
    public void removeItemListener(ItemListener l) {
        listenerList.remove(ItemListener.class, l);
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     *
     * @param e the ItemEvent to deliver to listeners
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
     * Return an array of all the listeners of the given type that 
     * were added to this model. 
     *
     * @returns all of the objects recieving <em>listenerType</em> notifications 
     *          from this model
     * 
     * @since 1.3
     */
    public EventListener[] getListeners(Class listenerType) { 
	return listenerList.getListeners(listenerType); 
    }

    /** Overriden to return null */
    public Object[] getSelectedObjects() {
        return null; 
    }

    /**
     * Identifies the group this button belongs to --
     * needed for radio buttons, which are mutually
     * exclusive within their group.
     *
     * @param group the ButtonGroup this button belongs to
     */
    public void setGroup(ButtonGroup group) {
        this.group = group;
    }

    /**
     * Returns the group that this button belongs to.
     * Normally used with radio buttons, which are mutually
     * exclusive within their group.
     *
     * @return a ButtonGroup that this button belongs to
     */
    public ButtonGroup getGroup() {
        return group;
    }

}
