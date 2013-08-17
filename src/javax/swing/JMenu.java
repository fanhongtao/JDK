/*
 * @(#)JMenu.java	1.124 00/09/22
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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.*;
import java.beans.*;

import java.util.*;

import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import java.awt.event.KeyEvent;

import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.accessibility.*;


/**
 * An implementation of a menu -- a popup window containing <code>JMenuItem</code>s that
 * is displayed when the user selects an item on the <code>JMenuBar</code>. In addition
 * to JMenuItems, a JMenu can also contain <code>JSeparator</code>s. 
 * <p>
 * In essence, a menu is a button with an associated JPopupMenu.
 * When the "button" is pressed, the JPopupMenu appears. If the
 * "button" is on the JMenuBar, the menu is a top-level window.
 * If the "button" is another menu item, then the JPopupMenu is
 * "pull-right" menu.
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JMenu">JMenu</a> key assignments.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.124 09/22/00
 * @author Georges Saab
 * @author David Karlton
 * @author Arnaud Weber
 * @see JMenuItem
 * @see JSeparator
 * @see JMenuBar
 * @see JPopupMenu
 */
public class JMenu extends JMenuItem implements Accessible,MenuElement
{
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "MenuUI";

    /*
     * The popup menu portion of the menu.
     */
    private JPopupMenu popupMenu;

    /*
     * The button's model listeners.
     */
    private ChangeListener menuChangeListener = null;

    /*
     * Only one MenuEvent is needed per menu instance since the
     * event's only state is the source property.  The source of events
     * generated is always "this".
     */
    private MenuEvent menuEvent = null;

    /* Registry of listeners created for Action-JMenuItem
     * linkage.  This is needed so that references can
     * be cleaned up at remove time to allow GC.
     */
    private static Hashtable listenerRegistry = null;

    private int delay;
    private boolean receivedKeyPressed=false;

    /**
     * Creates a new JMenu with no text.
     */
    public JMenu() {
        this("");
    }

    /**
     * Creates a new JMenu with the supplied string as its text
     *
     * @param s  The text for the menu label
     */
    public JMenu(String s) {
	super(s);
    }

    /**
     * Creates a new JMenu with the supplied string as its text
     * and specified as a tear-off menu or not.
     *
     * @param s The text for the menu label
     * @param b can the menu be torn off (not yet implemented)
     */
    public JMenu(String s, boolean b) {
        this(s);
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

        if ( popupMenu != null )
          {
            popupMenu.setUI((PopupMenuUI)UIManager.getUI(popupMenu));
          }

    }


    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return "MenuUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }

    //    public void repaint(long tm, int x, int y, int width, int height) {
    //        Thread.currentThread().dumpStack();
    //        super.repaint(tm,x,y,width,height);
    //    }

    /**
     * Set the data model for the "menu button" -- the label
     * that the user clicks to open or close the menu.
     *
     * @param m the ButtonModel
     * @see #getModel
     * @beaninfo
     * description: The menu's model
     *       bound: true
     *      expert: true
     *      hidden: true
     */
    public void setModel(ButtonModel newModel) {
        ButtonModel oldModel = getModel();

        super.setModel(newModel);

        if (oldModel != null && menuChangeListener != null) {
            oldModel.removeChangeListener(menuChangeListener);
            menuChangeListener = null;
        }
        
        model = newModel;
        
        if (newModel != null) {
            menuChangeListener = createMenuChangeListener();
            newModel.addChangeListener(menuChangeListener);
        }
    }

    /**
     * Returns true if the menu is currently selected (popped up).
     *
     * @return true if the menu is open, else false
     */
    public boolean isSelected() {
        return getModel().isSelected();
    }

    /**
     * Sets the selection status of the menu.
     *
     * @param b  a boolean value -- true to select the menu and 
     *           open it, false to unselect the menu and close it
     * @beaninfo
     *      description: When the menu is selected, its popup child is shown.
     *           expert: true
     *           hidden: true
     */
    public void setSelected(boolean b) {
        ButtonModel model = getModel();
        boolean oldValue = model.isSelected();

        if ((accessibleContext != null) && (oldValue != b)) {
            if (b) {
                 accessibleContext.firePropertyChange(
                         AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                         null, AccessibleState.SELECTED);
            } else {
                 accessibleContext.firePropertyChange(
                         AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                         AccessibleState.SELECTED, null);
            }
        }
        if (b != model.isSelected()) {
            getModel().setSelected(b);
        }
    }

    /**
     * Returns true if the menu's popup window is visible.
     *
     * @return true if the menu is visible, else false
     */
    public boolean isPopupMenuVisible() {
        ensurePopupMenuCreated();
        return popupMenu.isVisible();
    }

    /**
     * Set the visibility of the Menu's popup portion.  The popup
     * may only be made visible if the menu is itself showing on
     * the screen.
     *
     * @param b  a boolean value -- true to make the menu visible,
     *           false to hide it
     * @beaninfo
     *      description: The popup menu's visibility
     *           expert: true
     *           hidden: true
     */
    public void setPopupMenuVisible(boolean b) {
	if (!isEnabled())
	    return;
	boolean isVisible = isPopupMenuVisible();
        if (b != isVisible) {
            ensurePopupMenuCreated();
            // Set location of popupMenu (pulldown or pullright)
            //  Perhaps this should be dictated by L&F
            if ((b==true) && isShowing()) {
		Point p = getPopupMenuOrigin();
		getPopupMenu().show(this, p.x, p.y);
            } else {
                getPopupMenu().setVisible(false);
            }
        }

    }

    /**
     * Compute the origin for the JMenu's popup menu.
     *
     * @returns a Point in the coordinate space of the menu instance
     * which should be used as the origin of the JMenu's popup menu.
     */
    private Point getPopupMenuOrigin() {
	int x = 0;
	int y = 0;
	JPopupMenu pm = getPopupMenu();
	// Figure out the sizes needed to caclulate the menu position
	Dimension screenSize =Toolkit.getDefaultToolkit().getScreenSize();
	Dimension s = getSize();
	Dimension pmSize = pm.getSize();
	// For the first time the menu is popped up, 
	// the size has not yet been initiated
	if (pmSize.width==0) {
	    pmSize = pm.getPreferredSize();
	}
	Point position = getLocationOnScreen();
	
	Container parent = getParent();
	if (parent instanceof JPopupMenu) {
	    // We are a submenu (pull-right)

            if( SwingUtilities.isLeftToRight(this) ) {
                // First determine x:
                if (position.x+s.width + pmSize.width < screenSize.width) {
                    x = s.width;         // Prefer placement to the right
                } else {
                    x = 0-pmSize.width;  // Otherwise place to the left
                }
            } else {
                // First determine x:
                if (position.x < pmSize.width) {
                    x = s.width;         // Prefer placement to the right
                } else {
                    x = 0-pmSize.width;  // Otherwise place to the left
                }
            }
            // Then the y:
            if (position.y+pmSize.height < screenSize.height) {
                y = 0;                       // Prefer dropping down
            } else {
                y = s.height-pmSize.height;  // Otherwise drop 'up'
            }
	} else {
	    // We are a toplevel menu (pull-down)

            if( SwingUtilities.isLeftToRight(this) ) {
                // First determine the x:
                if (position.x+pmSize.width < screenSize.width) {
                    x = 0;                     // Prefer extending to right 
                } else {
                    x = s.width-pmSize.width;  // Otherwise extend to left
                }
            } else {
                // First determine the x:
                if (position.x+s.width < pmSize.width) {
                    x = 0;                     // Prefer extending to right 
                } else {
                    x = s.width-pmSize.width;  // Otherwise extend to left
                }
            }
	    // Then the y:
	    if (position.y+s.height+pmSize.height < screenSize.height) {
		y = s.height;          // Prefer dropping down
	    } else {
		y = 0-pmSize.height;   // Otherwise drop 'up'
	    }
	}
	return new Point(x,y);
    }


    /**
     * Returns the suggested delay before the menu's PopupMenu is popped up or down.
     * Each look and feel may determine its own policy for observing the delay
     * property.  In most cases, the delay is not observed for top level menus
     * or while dragging.
     *
     * @return an int -- the number of milliseconds to delay
     */
    public int getDelay() {
        return delay;
    }
    
    /**
     * Sets the suggested delay before the menu's PopupMenu is popped up or down.
     * Each look and feel may determine its own policy for observing the delay
     * property.  In most cases, the delay is not observed for top level menus
     * or while dragging.
     *
     * @param       d the number of milliseconds to delay
     * @exception   IllegalArgumentException if the value of 
     *                       <code>d</code> is less than 0.
     * @beaninfo
     *      description: The delay between menu selection and making the popup menu visible
     *           expert: true
     */
    public void setDelay(int d) {
        if (d < 0)
            throw new IllegalArgumentException("Delay must be a positive integer");

        delay = d;
    }

    /**
     * The window-closing listener for the popup.
     *
     * @see WinListener
     */
    protected WinListener popupListener;

    private void ensurePopupMenuCreated() {
        if (popupMenu == null) {            
            final JMenu thisMenu = this;
            this.popupMenu = new JPopupMenu();
            popupMenu.setInvoker(this);
            popupListener = createWinListener(popupMenu);
            popupMenu.addPopupMenuListener(new PopupMenuListener() {
                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                }
                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                }
                public void popupMenuCanceled(PopupMenuEvent e) {
                    fireMenuCanceled();
                }
            });
        }
    }

    /**
     * Set the location of the popup component
     *
     * @param x the x coordinate of the popup's new position
     * @param y the y coordinate of the popup's new position
     */
    public void setMenuLocation(int x, int y) {
        if (popupMenu != null)
	    popupMenu.setLocation(x, y);
    }

    /**
     * Appends a menuitem to the end of this menu. 
     * Returns the menuitem added.
     *
     * @param menuItem the JMenuitem to be added
     * @return the JMenuItem added
     */
    public JMenuItem add(JMenuItem menuItem) {
        AccessibleContext ac = menuItem.getAccessibleContext();
        ac.setAccessibleParent(this);
        ensurePopupMenuCreated();
        return popupMenu.add(menuItem);
    }

    /**
     * Appends a component to the end of this menu.
     * Returns the component added.
     *
     * @param c the Component to add
     * @return the Component added
     */
    public Component add(Component c) {
 	if (c instanceof JComponent) {	
	    AccessibleContext ac = ((JComponent) c).getAccessibleContext();
	    if (ac != null) {
		ac.setAccessibleParent(this);
	    }
	}
        ensurePopupMenuCreated();
        popupMenu.add(c);
        return c;
    }

    /**
     * Creates a new menuitem with the specified text and appends
     * it to the end of this menu.
     *  
     * @param s the string for the menuitem to be added
     */
    public JMenuItem add(String s) {
        return add(new JMenuItem(s));
    }

    /**
     * Creates a new menuitem attached to the specified 
     * Action object and appends it to the end of this menu.
     *
     * @param a the Action for the menuitem to be added
     * @see Action
     */
    public JMenuItem add(Action a) {
        JMenuItem mi = new JMenuItem((String)a.getValue(Action.NAME),
                                     (Icon)a.getValue(Action.SMALL_ICON));
        mi.setHorizontalTextPosition(JButton.RIGHT);
        mi.setVerticalTextPosition(JButton.CENTER);
        mi.setEnabled(a.isEnabled());   
        mi.addActionListener(a);
        add(mi);
	registerMenuItemForAction(mi, a);
        return mi;
    }

    protected PropertyChangeListener createActionChangeListener(JMenuItem b) {
        return new ActionChangedListener(b);
    }

    private class ActionChangedListener implements PropertyChangeListener {
        JMenuItem menuItem;
        
        ActionChangedListener(JMenuItem mi) {
            super();
	    setTarget(mi);
        }
        public void propertyChange(PropertyChangeEvent e) {
            String propertyName = e.getPropertyName();
            if (e.getPropertyName().equals(Action.NAME)) {
                String text = (String) e.getNewValue();
                menuItem.setText(text);
            } else if (propertyName.equals("enabled")) {
                Boolean enabledState = (Boolean) e.getNewValue();
                menuItem.setEnabled(enabledState.booleanValue());
            } else if (e.getPropertyName().equals(Action.SMALL_ICON)) {
                Icon icon = (Icon) e.getNewValue();
                menuItem.setIcon(icon);
                menuItem.invalidate();
                menuItem.repaint();
            } 
        }
	public void setTarget(JMenuItem b) {
	    this.menuItem = b;
	}
    }

    /**
     * Append a new separator to the end of the menu.
     */
    public void addSeparator()
    {
        ensurePopupMenuCreated();
        popupMenu.addSeparator();
    }

    /**
     * Insert a new menuitem with the specified text at a 
     * given position.
     *
     * @param s the text for the menuitem to add
     * @param pos an int giving the position at which to add the 
     *               new menuitem
     */
    public void insert(String s, int pos) {
        if (pos < 0) {
            throw new IllegalArgumentException("index less than zero.");
        }

        ensurePopupMenuCreated();
        popupMenu.insert(new JMenuItem(s), pos);
    }

    /**
     * Insert the specified JMenuitem at a given position.
     *
     * @param mi the JMenuitem to add
     * @param pos an int giving the position at which to add the 
     *               new JMenuitem
     */
    public JMenuItem insert(JMenuItem mi, int pos) {
        if (pos < 0) {
            throw new IllegalArgumentException("index less than zero.");
        }
        AccessibleContext ac = mi.getAccessibleContext();
        ac.setAccessibleParent(this);
        ensurePopupMenuCreated();
        popupMenu.insert(mi, pos);
        return mi;
    }

    /**
     * Insert a new menuitem attached to the specified Action 
     * object at a given position.
     *
     * @param a the Action object for the menuitem to add
     * @param pos an int giving the position at which to add the 
     *               new menuitem
     */
    public JMenuItem insert(Action a, int pos) {
        if (pos < 0) {
            throw new IllegalArgumentException("index less than zero.");
        }

        ensurePopupMenuCreated();
        JMenuItem mi = new JMenuItem((String)a.getValue(Action.NAME),
				     (Icon)a.getValue(Action.SMALL_ICON));
        mi.setHorizontalTextPosition(JButton.RIGHT);
        mi.setVerticalTextPosition(JButton.CENTER);
        mi.setEnabled(a.isEnabled());   
        mi.addActionListener(a);
        popupMenu.insert(mi, pos);
	registerMenuItemForAction(mi, a);
        return mi;
    }

    private void registerMenuItemForAction(JMenuItem mi, Action a) {
        PropertyChangeListener actionPropertyChangeListener = 
            createActionChangeListener(mi);
	if (listenerRegistry == null) {
	    listenerRegistry = new Hashtable();
	}
	listenerRegistry.put(mi, actionPropertyChangeListener);
	listenerRegistry.put(actionPropertyChangeListener, a);
        a.addPropertyChangeListener(actionPropertyChangeListener);
    }

    private void unregisterMenuItemForAction(JMenuItem item) {
	if (listenerRegistry != null) { 
	    ActionChangedListener p = (ActionChangedListener)listenerRegistry.remove(item);
	    if (p!=null) {
		Action a = (Action)listenerRegistry.remove(p);
		if (a!=null) {
		    item.removeActionListener(a);		
		    a.removePropertyChangeListener(p);
		}
		p.setTarget(null);
	    }
	}
    }

    private void clearListenerRegistry() {
	// Some GCs we have run across are not so good at removing
	// circular references, so we'll null these out ourselves:
	if (listenerRegistry!=null) {
	    for (Enumeration e = listenerRegistry.keys() ; e.hasMoreElements() ;) {
		Object key = e.nextElement();
		if (key == this) {     // Only snarf our own listings! 4190759
		    JMenuItem item = (JMenuItem)key;
		    ActionChangedListener p = 
			(ActionChangedListener)listenerRegistry.get(item);
		    if (p!=null) {
			Action a = (Action)listenerRegistry.get(p);
			if (a!=null) {
			    item.removeActionListener(a);		
			    a.removePropertyChangeListener(p);
			}
			p.setTarget(null);
		    }
		    
		}     
	    }
	    listenerRegistry.clear();
	}
    }

    /**
     * Inserts a separator at the specified position.
     *
     * @param       index an int giving the position at which to 
     *                    insert the menu separator
     * @exception   IllegalArgumentException if the value of 
     *                       <code>index</code> is less than 0.
     */
    public void insertSeparator(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("index less than zero.");
        }

        ensurePopupMenuCreated();
        popupMenu.insert( new JPopupMenu.Separator(), index );
    }

    /** 
     * Returns the JMenuItem at the specified position.
     * If the specified position contains a separator, this JMenu
     * is returned.  
     *
     * @param pos    an int giving the position
     * @exception   IllegalArgumentException if the value of 
     *                       <code>index</code> is less than 0.
     */
    public JMenuItem getItem(int pos) {
        if (pos < 0) {
            throw new IllegalArgumentException("index less than zero.");
        }

        Component c = getMenuComponent(pos);
        if (c instanceof JMenuItem) {
            JMenuItem mi = (JMenuItem) c;
            return mi;
        }

        // 4173633
        return null;
    }

    /**
     * Returns the number of items on the menu, including separators.
     * This method is included for AWT compatibility.
     *
     * @return an int equal to the number of items on the menu
     * @see #getMenuComponentCount
     */
    public int getItemCount() {
        return getMenuComponentCount();
    }

    /**
     * Returns true if the menu can be torn off.
     *
     * @return true if the menu can be torn off, else false
     */
    public boolean isTearOff() {
        throw new Error("boolean isTearOff() {} not yet implemented");
    }

    /**
     * Removes the specified menu item from this menu.
     *
     * @param       item the JMenuItem to be removed from the menu
     */
    public void remove(JMenuItem item) {
        if (popupMenu != null)
	    popupMenu.remove(item);
	unregisterMenuItemForAction(item);
    }

    /**
     * Removes the menu item at the specified index from this menu.
     *
     * @param       index the position of the item to be removed. 
     * @exception   IllegalArgumentException if the value of 
     *                       <code>index</code> is less than 0.
     */
    public void remove(int pos) {
        if (pos < 0) {
            throw new IllegalArgumentException("index less than zero.");
        }
        if (pos > getItemCount()) {
            throw new IllegalArgumentException("index greater than the number of items.");
        }
	Component c = getItem(pos);
	if (c instanceof JMenuItem)
	    unregisterMenuItemForAction((JMenuItem)c);
        if (popupMenu != null)
	    popupMenu.remove(pos);
    }

    /**
     * Removes the Component from this menu.
     *
     * @param       c the component to be removed
     */
    public void remove(Component c) {
        if (popupMenu != null)
	    popupMenu.remove(c);
    }

    /**
     * Remove all menu items from this menu.
     */
    public void removeAll() {
        if (popupMenu != null)
	    popupMenu.removeAll();
	clearListenerRegistry();
    }

    /**
     * Returns the number of components on the menu.
     *
     * @return an int -- the number of components on the menu
     */
    public int getMenuComponentCount() {
        int componentCount = 0;
        if (popupMenu != null)
            componentCount = popupMenu.getComponentCount();
        return componentCount;
    }

    /**
     * Returns the component at position n
     *
     * @param n the position of the component to be returned
     */
    public Component getMenuComponent(int n) {
        if (popupMenu != null)
            return popupMenu.getComponent(n);
        
        return null;
    }

    /**
     * Returns an array of the menu's subcomponents
     *
     * @return an array of Components
     */
    public Component[] getMenuComponents() {
        if (popupMenu != null)
            return popupMenu.getComponents();
        
        return new Component[0];
    }

    /**
     * Returns true if the menu is a 'top-level menu', that is, if it is
     * the direct child of a menubar.
     *
     * @return true if the menu is activated from the menu bar,
     *         false if the menu is activated from a menu item
     *         on another menu
     */
    public boolean isTopLevelMenu() {
        if (getParent() instanceof JMenuBar)
            return true;
        
        return false;
    }

    /**
     * Returns true if the specified component exists in the 
     * submenu hierarchy.
     *
     * @param c the Component to be tested
     * @return true if the component exists
     */
    public boolean isMenuComponent(Component c) {
        // Are we in the MenuItem part of the menu
        if (c == this)
            return true;
        // Are we in the PopupMenu?
        if (c instanceof JPopupMenu) {
            JPopupMenu comp = (JPopupMenu) c;
            if (comp == this.getPopupMenu())
                return true;
        }
        // Are we in a Component on the PopupMenu
        int ncomponents = this.getMenuComponentCount();
        Component[] component = this.getMenuComponents();
        for (int i = 0 ; i < ncomponents ; i++) {
            Component comp = component[i];
            // Are we in the current component?
            if (comp == c)
                return true;
            // Hmmm, what about Non-menu containers?

            // Recursive call for the Menu case
            if (comp instanceof JMenu) {
                JMenu subMenu = (JMenu) comp;
                if (subMenu.isMenuComponent(c))
                    return true;
            }
        }
        return false;
    }


    /*
     * Returns a point in the coordinate space of this menu's popupmenu
     * which corresponds to the point p in the menu's coordinate space.
     *
     * @param p the point to be translated
     */
    private Point translateToPopupMenu(Point p) {
        return translateToPopupMenu(p.x, p.y);
    }

    /*
     * Returns a point in the coordinate space of this menu's popupmenu
     * which corresponds to the point (x,y) in the menu's coordinate space.
     * @param x the x coordinate of the point to be translated
     * @param y the y coordinate of the point to be translated
     */
    private Point translateToPopupMenu(int x, int y) {
            int newX;
            int newY;

            if (getParent() instanceof JPopupMenu) {
                newX = x - getSize().width;
                newY = y;
            } else {
                newX = x;
                newY = y - getSize().height;
            }

            return new Point(newX, newY);
        }

    /**
     * Returns the popupmenu associated with this menu
     */
    public JPopupMenu getPopupMenu() {
        ensurePopupMenuCreated();
        return popupMenu;
    }

    /**
     * Add a listener for menu events
     *
     * @param l the listener to be added
     */
    public void addMenuListener(MenuListener l) {
        listenerList.add(MenuListener.class, l);
    }
    
    /**
     * Remove a listener for menu events
     *
     * @param l the listener to be removed
     */
    public void removeMenuListener(MenuListener l) {
        listenerList.remove(MenuListener.class, l);
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     *
     * @see EventListenerList
     */
    protected void fireMenuSelected() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==MenuListener.class) {
                if (listeners[i+1]== null) {
		    throw new Error(getText() +" has a NULL Listener!! " + i);		    
                } else {
                    // Lazily create the event:
                    if (menuEvent == null)
                        menuEvent = new MenuEvent(this);
                    ((MenuListener)listeners[i+1]).menuSelected(menuEvent);
                }              
            }
        }
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     *
     * @see EventListenerList
     */
    protected void fireMenuDeselected() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==MenuListener.class) {
                if (listeners[i+1]== null) {
                    throw new Error(getText() +" has a NULL Listener!! " + i);
                } else {
                    // Lazily create the event:
                    if (menuEvent == null)
                        menuEvent = new MenuEvent(this);
                    ((MenuListener)listeners[i+1]).menuDeselected(menuEvent);
                }              
            }
        }
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     *
     * @see EventListenerList
     */
    protected void fireMenuCanceled() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==MenuListener.class) {
                if (listeners[i+1]== null) {
                    throw new Error(getText() +" has a NULL Listener!! " 
                                       + i);
                } else {
                    // Lazily create the event:
                    if (menuEvent == null)
                        menuEvent = new MenuEvent(this);
                    ((MenuListener)listeners[i+1]).menuCanceled(menuEvent);
                }              
            }
        }
    }

    class MenuChangeListener implements ChangeListener, Serializable {
        boolean isSelected = false;
        public void stateChanged(ChangeEvent e) {               
            ButtonModel model = (ButtonModel) e.getSource();
            boolean modelSelected = model.isSelected();

            if (modelSelected != isSelected) {
                if (modelSelected == true) {
                    fireMenuSelected();
                } else {
                    fireMenuDeselected();
                }
                isSelected = modelSelected;
            }
        }
    }

    private ChangeListener createMenuChangeListener() {
        return new MenuChangeListener();
    }


    /**
     * Create a window-closing listener for the popup.
     *
     * @param p the JPopupMenu
     * @see WinListener
     */
    protected WinListener createWinListener(JPopupMenu p) {
        return new WinListener(p);
    }

    /**
     * A listener class that watches for a popup window closing.
     * When the popup is closing, the listener deselects the menu.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    protected class WinListener extends WindowAdapter implements Serializable {
        JPopupMenu popupMenu;
        /**
         *  Create the window listener for the specified popup.
         */
        public WinListener(JPopupMenu p) {
            this.popupMenu = p;
        }
        /**
         * Deselect the menu when the popup is closed from outside.
         */
        public void windowClosing(WindowEvent e) {
            setSelected(false);
        }
    }

    /**
     * Messaged when the menubar selection changes to activate or
     * deactivate this menu.
     * Overrides <code>JMenuItem.menuSelectionChanged</code>.
     *
     * @param isIncluded  true if this menu is active, false if
     *        it is not
     */
    public void menuSelectionChanged(boolean isIncluded) {
        setSelected(isIncluded);
    }

    /**
     * Returns an array containing the sub-menu components for this menu component
     * @return an array of MenuElement objects
     */
    public MenuElement[] getSubElements() {
        if(popupMenu == null)
            return new MenuElement[0];
        else {
            MenuElement result[] = new MenuElement[1];
            result[0] = popupMenu;
            return result;
        }
    }

    
    // implements javax.swing.MenuElement
    /**
     * This method returns the java.awt.Component used to paint this MenuElement.
     * The returned component is used to convert events and detect if an event is inside
     * a menu component.
     */   
    public Component getComponent() {
        return this;
    }


    /** 
     * setAccelerator() is not defined for JMenu.  Use setMnemonic() instead. 
     *
     * @beaninfo
     *     description: The keystroke combination which will invoke the JMenuItem's
     *                  actionlisteners without navigating the menu hierarchy
     *          hidden: true
     */
    public void setAccelerator(KeyStroke keyStroke) {
        throw new Error("setAccelerator() is not defined for JMenu.  Use setMnemonic() instead.");
    }

    /**
     *
     */
    protected void processKeyEvent(KeyEvent e) {

	/* fix for bug 4213634 */	
      boolean createMenuEvent=false;

      switch (e.getID()) {
      
      case KeyEvent.KEY_PRESSED:
	if (isSelected())
	  createMenuEvent = receivedKeyPressed = true;
	else receivedKeyPressed = false;
	break;
      case KeyEvent.KEY_RELEASED:
	if (receivedKeyPressed) {
	  receivedKeyPressed = false;
	  createMenuEvent = true;
	}
	break;
      default:
	createMenuEvent = receivedKeyPressed;
	break;
      }
      
      if (createMenuEvent && isSelected()) {
	MenuSelectionManager.defaultManager().processKeyEvent(e);
      }
      
      if(e.isConsumed()) 
	return;
      
	/* The "if" block below  fixes bug #4108907.
	   Without this code, opened menus that
	   weren't interested in TAB key events (most menus are not) would
	   allow such events to propagate up until a component was found
	   that was interested in the event. This would often result in
	   the focus being moved to another component as a result of the
	   TAB, while the menu stayed open. The behavior that is most
	   probably desired is that menus are modal, and thus consume
	   all keyboard events while they are open. This is implemented
	   by the inner "if" clause. But if the desired behavior on TABs
	   is that the menu should close and allow the focus to move,
	   the "else" clause takes care of that. Note that this is probably
	   not the right way to implement that behavior; instead, the menu
	   should unpost whenever it looses focus, which would also fix
	   another bug: 4156858.
	   The fact that one has to special-case TABS here in JMenu code
	   also offends me...
	   hania 23 July 1998 */
      if(isSelected() && (e.getKeyCode() == KeyEvent.VK_TAB
			  || e.getKeyChar() == '\t')) {
	if ((Boolean) UIManager.get("Menu.consumesTabs") == Boolean.TRUE) {
	  e.consume();
	  return;
	} else {
	  MenuSelectionManager.defaultManager().clearSelectedPath();
	}
      }
      super.processKeyEvent(e);
    }

  /* fix for bug 4213634, on win32, JMenu lost focus when 
     is not showing. On solaris, it still has focus when is not 
     showing
   */ 
  protected void processFocusEvent(FocusEvent e) {
    switch (e.getID()) {
    case FocusEvent.FOCUS_LOST:
      receivedKeyPressed = false;
      break;
    default:
      break;
    }
    super.processFocusEvent(e);
  }

    /**
     * Programatically perform a "click".  This overrides the method
     * AbstractButton.doClick(int) in order to make the menu pop up.
     */
    public void doClick(int pressTime) {
	MenuElement me[] = buildMenuElementArray(this);
	MenuSelectionManager.defaultManager().setSelectedPath(me);
    }

    /*
     * Build an array of menu elements - from my PopupMenu to the root
     * JMenuBar
     */
    private MenuElement[] buildMenuElementArray(JMenu leaf) {
	Vector elements = new Vector();
	Component current = leaf.getPopupMenu();
	JPopupMenu pop;
	JMenu menu;
	JMenuBar bar;

	while (true) {
	    if (current instanceof JPopupMenu) {
		pop = (JPopupMenu) current;
		elements.insertElementAt(pop, 0);
		current = pop.getInvoker();
	    } else if (current instanceof JMenu) {
		menu = (JMenu) current;
		elements.insertElementAt(menu, 0);
		current = menu.getParent();
	    } else if (current instanceof JMenuBar) {
		bar = (JMenuBar) current;
		elements.insertElementAt(bar, 0);
		MenuElement me[] = new MenuElement[elements.size()];
		elements.copyInto(me);
		return me;
	    }
	}
    }


    /** 
     * See readObject() and writeObject() in JComponent for more 
     * information about serialization in Swing.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
	if ((ui != null) && (getUIClassID().equals(uiClassID))) {
	    ui.installUI(this);
	}
    }


    /**
     * Returns a string representation of this JMenu. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this JMenu.
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
            accessibleContext = new AccessibleJMenu();
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
    protected class AccessibleJMenu extends AccessibleJMenuItem 
	implements AccessibleSelection {

        /**
         * Returns the number of accessible children in the object.  If all
         * of the children of this object implement Accessible, than this
         * method should return the number of children of this object.
         *
         * @return the number of accessible children in the object.
         */
        public int getAccessibleChildrenCount() {
            Component[] children = getMenuComponents();
            int count = 0;
            for (int j = 0; j < children.length; j++) {
                if (children[j] instanceof Accessible) {
                    count++;
                }
            }
            return count;
        }

        /**
         * Return the nth Accessible child of the object.  
         *
         * @param i zero-based index of child
         * @return the nth Accessible child of the object
         */
        public Accessible getAccessibleChild(int i) {
            Component[] children = getMenuComponents();
            int count = 0;
            for (int j = 0; j < children.length; j++) {
                if (children[j] instanceof Accessible) {
                    if (count == i) {
                        if (children[j] instanceof JComponent) {
                            // FIXME:  [[[WDW - probably should set this when
                            // the component is added to the menu.  I tried
                            // to do this in most cases, but the separators
                            // added by addSeparator are hard to get to.]]]
                            AccessibleContext ac = ((Accessible) children[j]).getAccessibleContext();
                            ac.setAccessibleParent(JMenu.this);
                        }
                        return (Accessible) children[j];
                    } else {
                        count++;
                    }
                }
            }
            return null;
        }

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the 
         * object
         * @see AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.MENU;
        }

        /**
         * Get the AccessibleSelection associated with this object if one
         * exists.  Otherwise return null.
         */
        public AccessibleSelection getAccessibleSelection() {
            return this;
        }

        /**
         * Returns 1 if a sub-menu is currently selected in this menu.
         *
         * @return 1 if a menu is currently selected, else 0
         */
        public int getAccessibleSelectionCount() {
	    MenuElement me[] =
		MenuSelectionManager.defaultManager().getSelectedPath();
	    if (me != null) {
		for (int i = 0; i < me.length; i++) {
		    if (me[i] == JMenu.this) {   // this menu is selected
			if (i+1 < me.length) {
			    return 1;
			}
		    }
		}
	    }
	    return 0;
        }

        /**
         * Returns the currently selected sub-menu if one is selected,
         * otherwise null (there can only be one selection, and it can
	 * only be a sub-menu, as otherwise menu items don't remain
	 * selected).
         */
        public Accessible getAccessibleSelection(int i) {
	    // if i is a sub-menu & popped, return it
	    if (i < 0 || i >= getItemCount()) {
		return null;
	    }
	    MenuElement me[] = 
		MenuSelectionManager.defaultManager().getSelectedPath();
            if (me != null) {
		for (int j = 0; j < me.length; j++) {
		    if (me[j] == JMenu.this) {   // this menu is selected
			// so find the next JMenuItem in the MenuElement 
			// array, and return it!
			while (++j < me.length) {
			    if (me[j] instanceof JMenuItem) {
				return (Accessible) me[j];
			    }
			}
		    }
		}
	    }
	    return null;
        }

        /**
         * Returns true if the current child of this object is selected.
	 * (i.e. if this child is a pop-ed up sub-menu)
         *
         * @param i the zero-based index of the child in this Accessible
         * object.
         * @see AccessibleContext#getAccessibleChild
         */
        public boolean isAccessibleChildSelected(int i) {
	    // if i is a sub-menu and is pop-ed up, return true, else false
	    MenuElement me[] = 
		MenuSelectionManager.defaultManager().getSelectedPath();
	    if (me != null) {
		JMenuItem mi = JMenu.this.getItem(i);
		for (int j = 0; j < me.length; j++) {
		    if (me[j] == mi) {
			return true;
		    }
		}
	    }
	    return false;
        }


        /**
         * Selects the nth menu in the menu.  If that item is a sub-menu,
         * it will pop up in response.  If a different item is already
	 * popped up, this will force it to close.  If this is a sub-menu
	 * that is already poppoed up (selected), this method has no
	 * effect.
         *
         * @param i the zero-based index of selectable items
         * @see #getAccessibleStateSet
         */
        public void addAccessibleSelection(int i) {
	    if (i < 0 || i >= getItemCount()) {
		return;
	    }
	    JMenuItem mi = getItem(i);
	    if (mi != null) {
		if (mi instanceof JMenu) {
		    MenuElement me[] = buildMenuElementArray((JMenu) mi);
		    MenuSelectionManager.defaultManager().setSelectedPath(me);
		} else {
		    mi.doClick();
		    MenuSelectionManager.defaultManager().setSelectedPath(null);
	        }
	    }
        }

        /**
         * Removes the nth item from the selection.  In general, menus 
	 * can only have one item within them selected at a time 
	 * (e.g. one sub-menu popped open).
         *
         * @param i the zero-based index of the selected item
         */
        public void removeAccessibleSelection(int i) {
	    if (i < 0 || i >= getItemCount()) {
		return;
	    }
	    JMenuItem mi = getItem(i);
	    if (mi != null && mi instanceof JMenu) {
		if (((JMenu) mi).isSelected()) {
		    MenuElement old[] =
			MenuSelectionManager.defaultManager().getSelectedPath();
		    MenuElement me[] = new MenuElement[old.length-1];
		    for (int j = 0; j < old.length -1; j++) {
			me[j] = old[j];
		    }
		    MenuSelectionManager.defaultManager().setSelectedPath(me);
		}
            }
        }

        /**
         * Clears the selection in the object, so that nothing in the
         * object is selected.  This will close any open sub-menu.
         */
        public void clearAccessibleSelection() {
	    // if this menu is selected, reset selection to only go
	    // to this menu; else do nothing
	    MenuElement old[] = 
		MenuSelectionManager.defaultManager().getSelectedPath();
	    if (old != null) {
		for (int j = 0; j < old.length; j++) {
		    if (old[j] == JMenu.this) {  // menu is in the selection!
			MenuElement me[] = new MenuElement[j+1];
			System.arraycopy(old, 0, me, 0, j);
			me[j] = JMenu.this.getPopupMenu();
			MenuSelectionManager.defaultManager().setSelectedPath(me);
		    }
		}
            }
        }

        /**
         * Normally causes every selected item in the object to be selected
         * if the object supports multiple selections.  This method
         * makes no sense in a menu bar, and so does nothing.
         */
        public void selectAllAccessibleSelection() {
        }
    } // inner class AccessibleJMenu
}

