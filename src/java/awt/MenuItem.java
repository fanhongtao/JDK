/*
 * @(#)MenuItem.java	1.43 97/03/03
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

import java.awt.peer.MenuItemPeer;
import java.awt.event.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * A String item that represents a choice in a menu.
 *
 * @version 1.43, 03/03/97
 * @author Sami Shaio
 */
public class MenuItem extends MenuComponent {
    boolean enabled = true;
    String label;
    String actionCommand;

    // The eventMask is ONLY set by subclasses via enableEvents.
    // The mask should NOT be set when listeners are registered
    // so that we can distinguish the difference between when
    // listeners request events and subclasses request them.
    long eventMask;

    transient ActionListener actionListener;

    private MenuShortcut shortcut = null;

    private static final String base = "menuitem";
    private static int nameCounter = 0;

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = -21757335363267194L;

    /** 
     * Constructs a new MenuItem with an empty label and no keyboard
     * shortcut.
     */
    public MenuItem() {
	this("", null);
    }

    /** 
     * Constructs a new MenuItem with the specified label and no
     * keyboard shortcut.
     * @param label the label for this menu item. Note that "-" is
     * reserved to mean a separator between menu items.
     */
    public MenuItem(String label) {
	this(label, null);
    }

    /**
     * Create a MenuItem with an associated keyboard shortcut.
     * @param label the label for this menu item. Note that "-" is
     * reserved to mean a separator between menu items.
     * @param s the MenuShortcut associated with this MenuItem.
     */
    public MenuItem(String label, MenuShortcut s) {
        this.name = base + nameCounter++;
	this.label = label;
        this.shortcut = s;
    }

    /**
     * Creates the menu item's peer.  The peer allows us to modify the 
     * appearance of the menu item without changing its functionality.
     */
    public void addNotify() {
	if (peer == null) {
	    peer = Toolkit.getDefaultToolkit().createMenuItem(this);
	}
    }

    /**
     * Gets the label for this menu item.
     */
    public String getLabel() {
	return label;
    }

    /**
     * Sets the label to be the specified label.
     * @param label the label for this menu item
     */
    public synchronized void setLabel(String label) {
	this.label = label;
	MenuItemPeer peer = (MenuItemPeer)this.peer;
	if (peer != null) {
	    peer.setLabel(label);
	}
    }

    /**
     * Checks whether the menu item is enabled.
     */
    public boolean isEnabled() {
	return enabled;
    }

    /**
     * Sets whether or not this menu item can be chosen.
     * @param b  if true, enables this menu item; otherwise, disables it
     */
    public synchronized void setEnabled(boolean b) {
    	enable(b);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by setEnabled(boolean).
     */
    public synchronized void enable() {
	enabled = true;
	MenuItemPeer peer = (MenuItemPeer)this.peer;
	if (peer != null) {
	    peer.enable();
	}
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by setEnabled(boolean).
     */
    public void enable(boolean b) {
    	if (b) {
	    enable();
	} else {
	    disable();
	}
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by setEnabled(boolean).
     */
    public synchronized void disable() {
	enabled = false;
	MenuItemPeer peer = (MenuItemPeer)this.peer;
	if (peer != null) {
	    peer.disable();
	}
    }

    /**
     * Return the MenuShortcut associated with this MenuItem, 
     * or null if none has been specified.
     */
    public MenuShortcut getShortcut() {
        return shortcut;
    }

    /** 
     * Set this MenuItem's MenuShortcut.  If a MenuShortcut is already
     * associated with this MenuItem, it will be replaced.
     * @param s the MenuShortcut to associate with this MenuItem.
     */
    public void setShortcut(MenuShortcut s) {
        shortcut = s;
	MenuItemPeer peer = (MenuItemPeer)this.peer;
	if (peer != null) {
	    peer.setLabel(label);
	}
    }

    /**
     * Delete any MenuShortcut associated with this MenuItem.
     */
    public void deleteShortcut() {
        shortcut = null;
	MenuItemPeer peer = (MenuItemPeer)this.peer;
	if (peer != null) {
	    peer.setLabel(label);
	}
    }

    /*
     * Delete a matching MenuShortcut associated with this MenuItem.
     * Used when iterating Menus.
     */
    void deleteShortcut(MenuShortcut s) {
        if (s.equals(shortcut)) {
            shortcut = null;
            MenuItemPeer peer = (MenuItemPeer)this.peer;
            if (peer != null) {
                peer.setLabel(label);
            }
        }
    }

    /*
     * Post an ActionEvent to the target (on 
     * keydown).  Returns true if there is an associated 
     * shortcut.
     */
    boolean handleShortcut(KeyEvent e) {
        MenuShortcut s = new MenuShortcut(e.getKeyCode(), 
                             (e.getModifiers() & InputEvent.SHIFT_MASK) > 0);
        if (s.equals(shortcut) && enabled) {
            // MenuShortcut match -- issue an event on keydown.
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                Toolkit.getEventQueue().postEvent(
                          new ActionEvent(this, ActionEvent.ACTION_PERFORMED, 
                                          actionCommand));
            } else {
                // silently eat key release.
            }
            return true;
	}
        return false;
    }

    MenuItem getShortcutMenuItem(MenuShortcut s) {
        return (s.equals(shortcut)) ? this : null;
    }

    /**
     * Enables the events defined by the specified event mask parameter
     * to be delivered to this menu item.  Event types are automatically
     * enabled when a listener for that type is added to the menu item,
     * therefore this method only needs to be invoked by subclasses of
     * a menu item which desire to have the specified event types 
     * delivered to processEvent regardless of whether a listener is
     * registered. 
     * @param eventsToEnable the event mask defining the event types
     */
    protected final void enableEvents(long eventsToEnable) {
        eventMask |= eventsToEnable;
    }

    /**
     * Disables the events defined by the specified event mask parameter
     * from being delivered to this menu item.  
     * @param eventsToDisable the event mask defining the event types
     */
    protected final void disableEvents(long eventsToDisable) {
        eventMask &= ~eventsToDisable;  
    }  

    /**
     * Sets the command name of the action event fired by this menu item.
     * By default this will be set to the label of the menu item.
     */
    public void setActionCommand(String command) {
        actionCommand = command;
    }

    /**
     * Returns the command name of the action event fired by this menu item.
     */
    public String getActionCommand() {
        return (actionCommand == null? label : actionCommand);
    }

    /**
     * Adds the specified action listener to receive action events
     * from this menu item.
     * @param l the action listener
     */ 
    public synchronized void addActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.add(actionListener, l);
        newEventsOnly = true;	
    }

    /**
     * Removes the specified action listener so it no longer receives
     * action events from this menu item.
     * @param l the action listener
     */ 
    public synchronized void removeActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.remove(actionListener, l);
    }

    /**
     * Processes events on this menu item. If the event is an ActionEvent,
     * it invokes the handleActionEvent method.
     * NOTE: Menu items currently only support action events.
     * @param e the event
     */
    protected void processEvent(AWTEvent e) {
        if (e instanceof ActionEvent) {
            processActionEvent((ActionEvent)e);     
        }
    }

    // REMIND: remove when filtering is done at lower level
    boolean eventEnabled(AWTEvent e) {
        if (e.id == ActionEvent.ACTION_PERFORMED) {
            if ((eventMask & AWTEvent.ACTION_EVENT_MASK) != 0 ||
                actionListener != null) {
                return true;
            } 
            return false;
        }
        return super.eventEnabled(e);
    }        

    /** 
     * Processes action events occurring on this menu item by
     * dispatching them to any registered ActionListener objects.
     * NOTE: This method will not be called unless action events
     * are enabled for this component; this happens when one of the
     * following occurs:
     * a) An ActionListener object is registered via addActionListener()
     * b) Action events are enabled via enableEvents()
     * @see #enableEvents
     * @param e the action event
     */  
    protected void processActionEvent(ActionEvent e) {
        if (actionListener != null) {
            actionListener.actionPerformed(e);
        }
    }

    /**
     * Returns the String parameter of the menu item.
     */
    public String paramString() {
        String str = ",label=" + label;
        if (shortcut != null) {
            str += ",shortcut=" + shortcut;
        }
        return super.paramString() + str;
    }


    /* Serialization support. 
     */

    private int menuItemSerializedDataVersion = 1;


    private void writeObject(ObjectOutputStream s)
      throws IOException 
    {
      s.defaultWriteObject();

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

	if (actionListenerK == key) 
	  addActionListener((ActionListener)(s.readObject()));

	else // skip value for unrecognized key
	  s.readObject();
      }
    }

}
