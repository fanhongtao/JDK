/*
 * @(#)JMenuItem.java	1.77 00/09/22
 *
 * Copyright 1997-1999 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package javax.swing;

import java.util.EventListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.event.*;
import javax.accessibility.*;

/**
 * An implementation of a MenuItem. A menu item is essentially a button
 * sitting in a list. When the user selects the "button", the action
 * associated with the menu item is performed. A JMenuItem contained
 * in a JPopupMenu performs exactly that function.
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JMenuItem">JMenuItem</a> key assignments.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.77 09/22/00
 * @author Georges Saab
 * @author David Karlton
 * @see JPopupMenu
 * @see JMenu
 * @see JCheckBoxMenuItem
 * @see JRadioButtonMenuItem
 */
public class JMenuItem extends AbstractButton implements Accessible,MenuElement  {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "MenuItemUI";

    /**
     * Creates a menuItem with no set text or icon.
     */
    public JMenuItem() {
        this(null, (Icon)null);
        setRequestFocusEnabled(false);
    }

    /**
     * Creates a menuItem with an icon.
     *
     * @param icon the icon of the MenuItem.
     */
    public JMenuItem(Icon icon) {
        this(null, icon);
        setRequestFocusEnabled(false);
    }

    /**
     * Creates a menuItem with text.
     *
     * @param text the text of the MenuItem.
     */
    public JMenuItem(String text) {
        this(text, (Icon)null);
    }
    
    /**
     * Creates a menuItem with the supplied text and icon.
     *
     * @param text the text of the MenuItem.
     * @param icon the icon of the MenuItem.
     */
    public JMenuItem(String text, Icon icon) {
        setModel(new DefaultButtonModel());
        init(text, icon);
    }

    /**
     * Creates a menuItem with the specified text and
     * keyboard mnemonic.
     *
     * @param text the text of the MenuItem.
     * @param mnemonic the keyboard mnemonic for the MenuItem
     */
    public JMenuItem(String text, int mnemonic) {
        setModel(new DefaultButtonModel());
        init(text, null);
        setMnemonic(mnemonic);
    }

    /**
     * Initialize the menu item with the specified text and icon.
     *
     * @param text the text of the MenuItem.
     * @param icon the icon of the MenuItem.
     */
    protected void init(String text, Icon icon) {
        if(text != null) {
            setText(text);
        }
        
        if(icon != null) {
            setIcon(icon);
        }
        
        // Listen for Focus events
        addFocusListener(new MenuItemFocusListener());
	setBorderPainted(false);
        setFocusPainted(false);
        setHorizontalTextPosition(JButton.TRAILING);
        setHorizontalAlignment(JButton.LEADING);
	updateUI();
    }

    private static class MenuItemFocusListener implements FocusListener,
        Serializable {
        public void focusGained(FocusEvent event) {}
        public void focusLost(FocusEvent event) {
            // When focus is lost, repaint if 
            // the focus information is painted
            JMenuItem mi = (JMenuItem)event.getSource();
            if(mi.isFocusPainted()) {
                mi.repaint();
            }
        }
    }
        
    
    /**
     * Sets the L&F object that renders this component.
     *
     * @param ui  the MenuItemUI L&F object
     * @see UIDefaults#getUI
     * @beaninfo
     * description: The menu item's UI delegate
     *       bound: true
     *      expert: true
     *      hidden: true
     */
    public void setUI(MenuItemUI ui) {
        super.setUI(ui);
    }
    
    /**
     * Notification from the UIFactory that the L&F has changed. 
     * Called to replace the UI with the latest version from the 
     * UIFactory.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        setUI((MenuItemUI)UIManager.getUI(this));
    }


    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return "MenuItemUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }


    /**
     * Identifies the menu item as "armed". If the mouse button is
     * released while it is over this item, the menu's action event
     * will fire. If the mouse button is released elsewhere, the
     * event will not fire and the menu item will be disarmed.
     * 
     * @param b true to arm the menu item so it can be selected
     * @beaninfo
     *    description: Mouse release will fire an action event
     *         hidden: true
     */
    public void setArmed(boolean b) {
        ButtonModel model = (ButtonModel) getModel();

        boolean oldValue = model.isArmed();
        if ((accessibleContext != null) && (oldValue != b)) {
            if (b) {
                accessibleContext.firePropertyChange(
                        AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                        null, 
                        AccessibleState.ARMED);
            } else {
                accessibleContext.firePropertyChange(
                        AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                        AccessibleState.ARMED, 
                        null);
            }
        }
        if(model.isArmed() != b) {
            model.setArmed(b);
        }
    }

    /**
     * Returns whether the menu item is "armed".
     * 
     * @return true if the menu item is armed, and it can be selected
     * @see #setArmed
     */
    public boolean isArmed() {
        ButtonModel model = (ButtonModel) getModel();
        return model.isArmed();
    }

    /**
     * Enable or disable the menu item.
     *
     * @param b  true to enable the item
     * @beaninfo
     *    description: Does the component react to user interaction
     *          bound: true
     *      preferred: true
     */
    public void setEnabled(boolean b) {
        // Make sure we aren't armed!
        if (b == false)
            setArmed(false);
        super.setEnabled(b);
    }


    /**
     * Always return true since Menus, by definition, 
     * should always be on top of all other windows.
     */
    // package private
    boolean alwaysOnTop() {
	return true;
    }


    /* The keystroke which acts as the menu item's accelerator
     */
    private KeyStroke accelerator;

    /**
     * Set the key combination which invokes the Menu Item's
     * action listeners without navigating the menu hierarchy.
     *
     * @param keyStroke the KeyStroke which will serve as an accelerator
     * @beaninfo
     *     description: The keystroke combination which will invoke the JMenuItem's
     *                  actionlisteners without navigating the menu hierarchy
     *           bound: true
     *       preferred: true
     */
    public void setAccelerator(KeyStroke keyStroke) {
	KeyStroke oldAccelerator = accelerator;
        if (oldAccelerator != null)
            unregisterKeyboardAction(oldAccelerator);

        // PENDING(ges) Make this implement Serializable
	if (keyStroke != null) {
	    registerKeyboardAction(new ActionListener(){
		public void actionPerformed(ActionEvent e) {
		    MenuSelectionManager.defaultManager().clearSelectedPath();
		    doClick();
		}
	    } , keyStroke, WHEN_IN_FOCUSED_WINDOW);
	}
        this.accelerator = keyStroke;
	firePropertyChange("accelerator", oldAccelerator, accelerator);
    }

    /**
     * Returns the KeyStroke which serves as an accelerator 
     * for the menu item.
     * @return a KeyStroke object identifying the accelerator key
     */
    public KeyStroke getAccelerator() {
        return this.accelerator;
    }

    /**
     * Process a mouse event forwarded from the MenuSelectionManager.
     * @param event          A MouseEvent with source being the receiving component.
     * @param componentPath  The MenuElement path array to the receiving component.
     * @param manager        The MenuSelectionManager for the menu hierarchy.
     * This method should process the MouseEvent and change the menu selection if necessary
     * by using MenuSelectionManager's API.
     * <p>
     * Note: you do not have to forward the event to sub-components. This is done automatically
     * by the MenuSelectionManager
     */
    public void processMouseEvent(MouseEvent e,MenuElement path[],MenuSelectionManager manager) {
	processMenuDragMouseEvent(
		 new MenuDragMouseEvent(e.getComponent(), e.getID(),
					e.getWhen(),
					e.getModifiers(), e.getX(), e.getY(),
					e.getClickCount(), e.isPopupTrigger(),
					path, manager));
    }


    /**
     * Process a key event forwarded from the MenuSelectionManager.
     * @param event          A KeyEvent with source being the receiving component.
     * @param componentPath  The MenuElement path array to the receiving component.
     * @param manager        The MenuSelectionManager for the menu hierarchy.
     * This method should process the KeyEvent and change the menu selection if necessary
     * by using MenuSelectionManager's API.
     * <p>
     * Note: you do not have to forward the event to sub-components. This is done automatically
     * by the MenuSelectionManager
     */
    public void processKeyEvent(KeyEvent e,MenuElement path[],MenuSelectionManager manager) {
     
        MenuKeyEvent mke = new MenuKeyEvent(e.getComponent(), e.getID(),
					     e.getWhen(), e.getModifiers(),
					     e.getKeyCode(), e.getKeyChar(),
					     path, manager);

	processMenuKeyEvent(mke);
	
	if (mke.isConsumed()) 
	  e.consume();
	  
    }



    /**
     * Handle mouse drag in a menu.
     *
     * @param e  a MenuDragMouseEvent object
     */
    public void processMenuDragMouseEvent(MenuDragMouseEvent e) {
	switch (e.getID()) {
	case MouseEvent.MOUSE_ENTERED:
	    fireMenuDragMouseEntered(e); break;
	case MouseEvent.MOUSE_EXITED:
	    fireMenuDragMouseExited(e); break;
	case MouseEvent.MOUSE_DRAGGED:
	    fireMenuDragMouseDragged(e); break;
	case MouseEvent.MOUSE_RELEASED:
	    fireMenuDragMouseReleased(e); break;
	default: 
	    break;
	}
    }

    /**
     * Handle a keystroke in a menu.
     *
     * @param e  a MenuKeyEvent object
     */
    public void processMenuKeyEvent(MenuKeyEvent e) {
	switch (e.getID()) {
	case KeyEvent.KEY_PRESSED:
	    fireMenuKeyPressed(e); break;
	case KeyEvent.KEY_RELEASED:
	    fireMenuKeyReleased(e); break;
	case KeyEvent.KEY_TYPED:
	    fireMenuKeyTyped(e); break;
	default: 
	    break;
	}
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type. 
     * @see EventListenerList
     */
    protected void fireMenuDragMouseEntered(MenuDragMouseEvent event) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==MenuDragMouseListener.class) {
                // Lazily create the event:
                ((MenuDragMouseListener)listeners[i+1]).menuDragMouseEntered(event);
            }          
        }
    }   

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  
     * @see EventListenerList
     */
    protected void fireMenuDragMouseExited(MenuDragMouseEvent event) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==MenuDragMouseListener.class) {
                // Lazily create the event:
                ((MenuDragMouseListener)listeners[i+1]).menuDragMouseExited(event);
            }          
        }
    }   

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.
     * @see EventListenerList
     */
    protected void fireMenuDragMouseDragged(MenuDragMouseEvent event) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==MenuDragMouseListener.class) {
                // Lazily create the event:
                ((MenuDragMouseListener)listeners[i+1]).menuDragMouseDragged(event);
            }          
        }
    }   

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type. 
     * @see EventListenerList
     */
    protected void fireMenuDragMouseReleased(MenuDragMouseEvent event) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==MenuDragMouseListener.class) {
                // Lazily create the event:
                ((MenuDragMouseListener)listeners[i+1]).menuDragMouseReleased(event);
            }          
        }
    }   

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type. 
     * @see EventListenerList
     */
    protected void fireMenuKeyPressed(MenuKeyEvent event) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==MenuKeyListener.class) {
                // Lazily create the event:
                ((MenuKeyListener)listeners[i+1]).menuKeyPressed(event);
            }          
        }
    }   

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type. 
     * @see EventListenerList
     */
    protected void fireMenuKeyReleased(MenuKeyEvent event) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==MenuKeyListener.class) {
                // Lazily create the event:
                ((MenuKeyListener)listeners[i+1]).menuKeyReleased(event);
            }          
        }
    }   

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type. 
     * @see EventListenerList
     */
    protected void fireMenuKeyTyped(MenuKeyEvent event) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==MenuKeyListener.class) {
                // Lazily create the event:				 
                ((MenuKeyListener)listeners[i+1]).menuKeyTyped(event);
            }          
        }
    }   

    /**
     * Called by the MenuSelectionManager when the MenuElement is selected
     * or unselected.
     * 
     * @param isIncluded  true if this menu item is on the part of the menu
     *                    path that changed, false if this menu is part of the
     *                    a menu path that changed, but this particular part of
     *                    that path is still the same
     * @see MenuSelectionManager#setSelectedPath(MenuElement[])
     */
    public void menuSelectionChanged(boolean isIncluded) {
        setArmed(isIncluded);
    }

    /**
     * This method returns an array containing the sub-menu components for this menu component.
     *
     * @return an array of MenuElements
     */
    public MenuElement[] getSubElements() {
        return new MenuElement[0];
    }
    
    /**
     * This method returns the java.awt.Component used to paint this object.
     * The returned component will be used to convert events and detect if an event is inside
     * a menu component.
     *
     * @return the Component that paints this menu item
     */
    public Component getComponent() {
        return this;
    }

    /**
     * Adds a MenuDragMouseListener to the menu item
     */
    public void addMenuDragMouseListener(MenuDragMouseListener l) {
        listenerList.add(MenuDragMouseListener.class, l);
    }

    /**
     * Removes a MenuDragMouseListener from the menu item
     */
    public void removeMenuDragMouseListener(MenuDragMouseListener l) {
        listenerList.remove(MenuDragMouseListener.class, l);
    }

    /**
     * Adds a MenuKeyListener to the menu item
     */
    public void addMenuKeyListener(MenuKeyListener l) {
        listenerList.add(MenuKeyListener.class, l);
    }

    /**
     * Removes a MenuKeyListener from the menu item
     */
    public void removeMenuKeyListener(MenuKeyListener l) {
        listenerList.remove(MenuKeyListener.class, l);
    }

    /**
     * See JComponent.readObject() for information about serialization
     * in Swing.
     */
    private void readObject(ObjectInputStream s) 
	throws IOException, ClassNotFoundException 
    {
        s.defaultReadObject();
	if (getUIClassID().equals(uiClassID)) {
	    updateUI();
	}
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
	if ((ui != null) && (getUIClassID().equals(uiClassID))) {
		ui.installUI(this);
	}
    }


    /**
     * Returns a string representation of this JMenuItem. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this JMenuItem.
     */
    protected String paramString() {
	return super.paramString();
    }

/////////////////
// Accessibility support
////////////////

    /**
     * Get the AccessibleContext associated with this JComponent
     *
     * @return the AccessibleContext of this JComponent
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJMenuItem();
        }
        return accessibleContext;
    }


    /**
     * The class used to obtain the accessible role for this object.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    protected class AccessibleJMenuItem extends AccessibleAbstractButton implements ChangeListener {

        AccessibleJMenuItem() {
            super();
            JMenuItem.this.addChangeListener(this);
        }

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the 
         * object
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.MENU_ITEM;
        }

        /**
         * Supports the change listener interface and fires property change
         */
        public void stateChanged(ChangeEvent e) {
            firePropertyChange(AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY, 
                               new Boolean(false), new Boolean(true));
        }
    } // inner class AccessibleJMenuItem
}

