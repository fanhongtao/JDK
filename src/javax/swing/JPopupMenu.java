/*
 * @(#)JPopupMenu.java	1.131 99/05/03
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

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.beans.*;

import java.util.Locale;
import java.util.Vector;
import java.util.Hashtable;
import javax.accessibility.*;
import javax.swing.plaf.PopupMenuUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.event.*;

import java.applet.Applet;

/**
 * An implementation of a Popup Menu -- a small window which pops up
 * and displays a series of choices. A JPopupMenu is used for the
 * menu that appears when the user selects an item on the menu bar.
 * It is also used for "pull-right" menu that appears when the
 * selects a menu item that activates it. Finally, a JPopupMenu
 * can also be used anywhere else you want a menu to appear -- for
 * example, when the user right-clicks in a specified area.
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JPopupMenu">JPopupMenu</a> key assignments.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.131 05/03/99
 * @author Georges Saab
 * @author David Karlton
 * @author Arnaud Weber
 */
public class JPopupMenu extends JComponent implements Accessible,MenuElement {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "PopupMenuUI";

    transient  Component invoker;
    transient  Popup popup;
    transient  Frame frame;
    private    String     label                   = null;
    private    boolean   paintBorder              = true;  
    private    Insets    margin                   = null;
    private    int desiredLocationX,desiredLocationY;
    private    int lastPopupType = LIGHT_WEIGHT_POPUP;

    private static final Object heavyPopupCacheKey = 
        new StringBuffer("JPopupMenu.heavyPopupCache");
    private static final Object lightPopupCacheKey = 
        new StringBuffer("JPopupMenu.lightPopupCache");
    private static final Object mediumPopupCacheKey = 
        new StringBuffer("JPopupMenu.mediumPopupCache");
    private static final Object defaultLWPopupEnabledKey = 
        new StringBuffer("JPopupMenu.defaultLWPopupEnabledKey");

    private static final int MAX_CACHE_SIZE = 5;
    private boolean lightWeightPopupEnabled = true;

    /** A light weight popup is used when it fits and light weight popups are enabled **/
    private static final int LIGHT_WEIGHT_POPUP   = 0;

    /** A "Medium weight" popup is a panel. We use this when downgrading an heavy weight in
     *  dialogs
     */
    private static final int MEDIUM_WEIGHT_POPUP  = 1;

    /** A popup implemented with a window */
    private static final int HEAVY_WEIGHT_POPUP   = 2;

    /*
     * Model for the selected subcontrol
     */
    private SingleSelectionModel selectionModel;

    /* Registry of listeners created for Action-JMenuItem
     * linkage.  This is needed so that references can
     * be cleaned up at remove time to allow GC.
     */
    private static Hashtable listenerRegistry = null;

    /* Lock object used in place of class object for synchronization. 
     * (4187686)
     */
    private static final Object classLock = new Object();

    /**
     *  Set the default value for the <b>lightWeightPopupEnabled</b>
     *  property.
     */
    /* Pending(arnaud) this property should scope to awt-context */
    public static void setDefaultLightWeightPopupEnabled(boolean aFlag) {
        SwingUtilities.appContextPut(defaultLWPopupEnabledKey, 
                                     new Boolean(aFlag));
    }

    /** 
     *  Return the default value for the <b>lightWeightPopupEnabled</b> 
     *  property.
     */
    public static boolean getDefaultLightWeightPopupEnabled() {
        Boolean b = (Boolean)
            SwingUtilities.appContextGet(defaultLWPopupEnabledKey);
        if (b == null) {
            SwingUtilities.appContextPut(defaultLWPopupEnabledKey, 
                                         Boolean.TRUE);
            return true;
        }
        return b.booleanValue();
    }

    private static Hashtable getHeavyPopupCache() {
        Hashtable cache = 
            (Hashtable)SwingUtilities.appContextGet(heavyPopupCacheKey);
        if (cache == null) {
            cache = new Hashtable(2);
            SwingUtilities.appContextPut(heavyPopupCacheKey, cache);
        }
        return cache;
    }

    private static Vector getLightPopupCache() {
        Vector cache = 
            (Vector)SwingUtilities.appContextGet(lightPopupCacheKey);
        if (cache == null) {
            cache = new Vector();
            SwingUtilities.appContextPut(lightPopupCacheKey, cache);
        }
        return cache;
    }

    private static Vector getMediumPopupCache() {
        Vector cache = 
            (Vector)SwingUtilities.appContextGet(mediumPopupCacheKey);
        if (cache == null) {
            cache = new Vector();
            SwingUtilities.appContextPut(mediumPopupCacheKey, cache);
        }
        return cache;
    }

    static void recycleHeavyPopup(Popup aPopup) {
	synchronized (classLock) {
	    Vector cache;
	    final Frame  f = getFrame((Component)aPopup);
	    Hashtable heavyPopupCache = getHeavyPopupCache();
	    if (heavyPopupCache.containsKey(f)) {
		cache = (Vector)heavyPopupCache.get(f);
	    } else {
		cache = new Vector();
		heavyPopupCache.put(f, cache);
		// Clean up if the Frame is closed
		f.addWindowListener(new WindowAdapter() {
		    public void windowClosed(WindowEvent e) {
			Hashtable heavyPopupCache2 = getHeavyPopupCache();
			heavyPopupCache2.remove(f);
		    }
		});
	    }
        
	    if(cache.size() < MAX_CACHE_SIZE) {
		cache.addElement(aPopup);
	    }
	}
    }

    static Popup getRecycledHeavyPopup(Frame f) {
	synchronized (classLock) {
	    Vector cache;
	    Hashtable heavyPopupCache = getHeavyPopupCache();
	    if (heavyPopupCache.containsKey(f)) {
		cache = (Vector)heavyPopupCache.get(f);
	    } else {
		return null;
	    }
	    int c;
	    if((c=cache.size()) > 0) {
		Popup r = (Popup)cache.elementAt(0);
		cache.removeElementAt(0);
		return r;
	    }
	    return null;
	}
    }

    
    static void recycleLightPopup(Popup aPopup) {
	synchronized (classLock) {
	    Vector lightPopupCache = getLightPopupCache();
	    if (lightPopupCache.size() < MAX_CACHE_SIZE) {
		lightPopupCache.addElement(aPopup);
	    }
	}
    }

    static Popup getRecycledLightPopup() {
	synchronized (classLock) {
	    Vector lightPopupCache = getLightPopupCache();
	    int c;
	    if((c=lightPopupCache.size()) > 0) {
		Popup r = (Popup)lightPopupCache.elementAt(0);
		lightPopupCache.removeElementAt(0);
		return r;
	    }
	    return null;
	}
    }

    
    static void recycleMediumPopup(Popup aPopup) {
	synchronized (classLock) {
	    Vector mediumPopupCache = getMediumPopupCache();
	    if(mediumPopupCache.size() < MAX_CACHE_SIZE) {
		mediumPopupCache.addElement(aPopup);
	    }
	}
    }

    static Popup getRecycledMediumPopup() {
	synchronized (classLock) {
	    Vector mediumPopupCache = getMediumPopupCache();
	    int c;
	    if((c=mediumPopupCache.size()) > 0) {
		Popup r = (Popup)mediumPopupCache.elementAt(0);
		mediumPopupCache.removeElementAt(0);
		return r;
	    }
	    return null;
	}
    }
    

    static void recyclePopup(Popup aPopup) {
        if(aPopup instanceof JPanelPopup)
            recycleLightPopup(aPopup);
        else if(aPopup instanceof WindowPopup) 
            recycleHeavyPopup(aPopup);
        else if(aPopup instanceof PanelPopup) 
            recycleMediumPopup(aPopup);
    }
    /**
     * Create a JPopupMenu without an "invoker".
     */
    public JPopupMenu() {
        this(null);
    }

    /**
     * Create a JPopupMenu with the specified title.
     *
     * @param label  The string that a UI may use to display as a title 
     * for the popup menu.
     */
    public JPopupMenu(String label) {
        this.label = label;
        this.lightWeightPopupEnabled = JPopupMenu.getDefaultLightWeightPopupEnabled();
        setSelectionModel(new DefaultSingleSelectionModel());
        addMouseListener(new MouseAdapter() {});
        updateUI();
    }



    /**
     * Returns the L&F object that renders this component.
     *
     * @return the PopupMenuUI object that renders this component
     */
    public PopupMenuUI getUI() {
        return (PopupMenuUI)ui;
    }
    
    /**
     * Sets the L&F object that renders this component.
     *
     * @param ui the new PopupMenuUI L&F object
     * @see UIDefaults#getUI
     * @beaninfo
     * description: The popup menu UI delegate
     *       bound: true
     *      expert: true
     *      hidden: true
     */
    public void setUI(PopupMenuUI ui) {
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
        setUI((PopupMenuUI)UIManager.getUI(this));
    }


    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return "PopupMenuUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }


    /**
     * Returns the model object that handles single selections.
     *
     * @return the SingleSelectionModel in use
     * @see SingleSelectionModel
     */
    public SingleSelectionModel getSelectionModel() {
        return selectionModel;
    }

    /**
     * Set the model object to handle single selections.
     *
     * @param model the SingleSelectionModel to use
     * @see SingleSelectionModel
     * @beaninfo
     * description: The selection model for the popup menu
     *      expert: true
     */
    public void setSelectionModel(SingleSelectionModel model) {
        selectionModel = model;
    }

    /**
     * Appends the specified menu item to the end of this menu. 
     *
     * @param c the JMenuItem to add
     * @return the JMenuItem added.
     */
    public JMenuItem add(JMenuItem menuItem) {
        super.add(menuItem);
        return menuItem;
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
     * Append a new menuitem to the end of the menu which 
     * dispatches the specified Action object.
     *
     * @param a the Action to add to the menu
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

    /**
     * Removes the specified component from this popup menu.
     *
     * @param       item the JMenuItem to be removed from the menu
     */
    public void remove(Component comp) {
	super.remove(comp);
	if (comp instanceof JMenuItem) {
	    JMenuItem item = (JMenuItem)comp;
	    unregisterMenuItemForAction(item);
	}
    }

    /**
     * Removes the component at the specified index from this popup menu.
     *
     * @param       index the position of the item to be removed. 
     * @exception   IllegalArgumentException if the value of 
     *                       <code>index</code> is less than 0.
     */
    public void remove(int pos) {
        if (pos < 0) {
            throw new IllegalArgumentException("index less than zero.");
        }
        if (pos > getComponentCount() -1) {
            throw new IllegalArgumentException("index greater than the number of items.");
        }
	Component c = getComponent(pos);
	if (c instanceof JMenuItem)
	    unregisterMenuItemForAction((JMenuItem)c);

	super.remove(pos);
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
     * When displaying the popup, JPopupMenu choose to use a light weight popup if
     * it fits. This method allows you to disable this feature. You have to do disable
     * it if your application mixes light weight and heavy weights components.
     * @beaninfo
     * description: Determines whether lightweight popups are used when possible
     *      expert: true
     */
    public void setLightWeightPopupEnabled(boolean aFlag) {
        lightWeightPopupEnabled = aFlag;
    }

    /**
     * Returns true if lightweight (all-Java) popups are in use,
     * or false if heavyweight (native peer) popups are being used.
     *
     * @return true if lightweight popups are in use
     */
    public boolean isLightWeightPopupEnabled() {
        return lightWeightPopupEnabled;
    }

    /**
     * Returns the popup menu's label
     * @return a String containing the popup menu's label
     * @see #setLabel
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * Sets the popup menu's label.  Different Look and Feels may choose
     * to display or not display this.
     * @param label a String specifying the label for the popup menu
     * @see #setLabel
     * @beaninfo
     * description: The label for the popup menu. 
     *       bound: true
     */
    public void setLabel(String label) {
        String oldValue = this.label;
        this.label = label;
        firePropertyChange("label", oldValue, label);
        if (accessibleContext != null) {
            accessibleContext.firePropertyChange(
                AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                oldValue, label);
        }
        invalidate();
        repaint();
    }

    /**
     * Appends a new separator at the end of the menu.
     */
    public void addSeparator() {
        add( new JPopupMenu.Separator() );
    }

    /**
     * Inserts a menu item for the specified Action object at a given
     * position.
     *
     * @param component  the Action object to insert
     * @param index      an int specifying the position at which
     *                   to insert the Action, where 0 is the first
     * @see Action
     */
    public void insert(Action a, int index) {
        throw new Error("void insert(Action, int) {} not yet implemented");
    }

    /**
     * Inserts the specified component into the menu at a given
     * position.
     *
     * @param component  the Component to insert
     * @param index      an int specifying the position at which
     *                   to insert the component, where 0 is the first
     */
    public void insert(Component component, int index) {
        if (index < 0) {
            throw new IllegalArgumentException("index less than zero.");
        }

        int nitems = getComponentCount();
        Vector tempItems = new Vector();

        /* Remove the item at index, nitems-index times 
           storing them in a temporary vector in the
           order they appear on the menu.
           */
        for (int i = index ; i < nitems; i++) {
            tempItems.addElement(getComponent(index));
            remove(index);
        }

        add(component);

        /* Add the removed items back to the menu, they are
           already in the correct order in the temp vector.
           */
        for (int i = 0; i < tempItems.size()  ; i++) {
            add((Component)tempItems.elementAt(i));
        }
    }

    /**
     *  Add a PopupMenu listener
     *
     * param l  the PopupMenuListener to add
     */
    public void addPopupMenuListener(PopupMenuListener l) {
        listenerList.add(PopupMenuListener.class,l);
    }

    /**
     * Remove a PopupMenu listener
     *
     * param l  the PopupMenuListener to remove
     */
    public void removePopupMenuListener(PopupMenuListener l) {
        listenerList.remove(PopupMenuListener.class,l);
    }

    /**
     * Notifies PopupMenuListeners that this popup menu will become
     * visible
     */
    protected void firePopupMenuWillBecomeVisible() {
        Object[] listeners = listenerList.getListenerList();
        PopupMenuEvent e=null;
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==PopupMenuListener.class) {
                if (e == null)
                    e = new PopupMenuEvent(this);
                ((PopupMenuListener)listeners[i+1]).popupMenuWillBecomeVisible(e);
            }
        }    
    }
    
    /**
     * Notifies PopupMenuListeners that this popup menu will become
     * invisible
     */
    protected void firePopupMenuWillBecomeInvisible() {
        Object[] listeners = listenerList.getListenerList();
        PopupMenuEvent e=null;
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==PopupMenuListener.class) {
                if (e == null)
                    e = new PopupMenuEvent(this);
                ((PopupMenuListener)listeners[i+1]).popupMenuWillBecomeInvisible(e);
            }
        }            
    }
    
    /**
     * Notifies PopupMenuListeners that this popup menu is canceled
     */
    protected void firePopupMenuCanceled() {
        Object[] listeners = listenerList.getListenerList();
        PopupMenuEvent e=null;
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==PopupMenuListener.class) {
                if (e == null)
                    e = new PopupMenuEvent(this);
                ((PopupMenuListener)listeners[i+1]).popupMenuCanceled(e);
            }
        }    
    }

    /**
     * Always return true since popups, by definition, should always
     * be on top of all other windows.
     */
    // package private
    boolean alwaysOnTop() {
	return true;
    }

    /**
     * Layout the container so that it uses the minimum space
     * needed to display its contents.
     */
    public void pack() {
        if(popup != null)
            popup.pack();
    }

    private Popup createLightWeightPopup() {
        Popup popup;
        popup = JPopupMenu.getRecycledLightPopup();
        if(popup == null) {
            popup = new JPanelPopup();
        }
        return popup;
    }
    
    private Popup createMediumWeightPopup() {
        Popup popup;
        popup = JPopupMenu.getRecycledMediumPopup();
        if(popup == null) {
            popup = new PanelPopup();
        }
        return popup;
    }

    private Popup createHeavyWeightPopup() {
        Frame frame = getFrame(invoker);

        if (frame != null) {
            popup = JPopupMenu.getRecycledHeavyPopup(frame);
        } else {
            frame = new Frame();
        }
        if (popup == null)
            popup = new WindowPopup(frame);

        return popup;
    }

    private boolean popupFit(Rectangle popupRectInScreen) {
        if(invoker != null) {
            Container parent;
            for(parent = invoker.getParent(); parent != null ; parent = parent.getParent()) {
                if(parent instanceof JFrame || parent instanceof JDialog ||
		   parent instanceof JWindow) {
                    return SwingUtilities.isRectangleContainingRectangle(parent.getBounds(),popupRectInScreen);
                } else if(parent instanceof JApplet) {
                    Rectangle r = parent.getBounds();
                    Point p  = parent.getLocationOnScreen();

                    r.x = p.x;
                    r.y = p.y;
                    return SwingUtilities.isRectangleContainingRectangle(r,popupRectInScreen);
                } else if(parent instanceof java.awt.Frame) {
                    return SwingUtilities.isRectangleContainingRectangle(parent.getBounds(),popupRectInScreen);                    
                }
            }
        }
        return false;
    }

    private boolean ancestorIsModalDialog(Component i) {
        Container parent = null;
	if (i !=null) {
	    for(parent = i.getParent() ; parent != null ; parent = parent.getParent())
		if ((parent instanceof Dialog) && (((Dialog)parent).isModal() == true))
		    return true;
	}
	return false;
    }

    private void replacePopup(int newType) {
        popup.removeComponent(this);
        recyclePopup(popup);
	popup = null;
        switch(newType) {
        case LIGHT_WEIGHT_POPUP:
            popup = createLightWeightPopup();
            break;
        case MEDIUM_WEIGHT_POPUP:
            popup = createMediumWeightPopup();
            break;
        case HEAVY_WEIGHT_POPUP:
            popup = createHeavyWeightPopup();
            break;
        }

        popup.setLocationOnScreen(desiredLocationX,desiredLocationY);
        popup.addComponent(this,"Center");
        invalidate();
        popup.setBackground(getBackground());
        popup.pack();
    }

    /**
     * Set the visibility of the popup menu.
     * 
     * @param b true to make the popup visible, or false to
     *          hide it
     * @beaninfo
     * description: Makes the popup visible
     */
    public void setVisible(boolean b) {
        // Is it a no-op?
        if (b == isVisible())
            return;

        // if closing, first close all Submenus
        if (b == false) {
            getSelectionModel().clearSelection();
	    
        } else {
            // This is a popupmenu with MenuElement children,
            // set selection path before popping up!
            if (isPopupMenu()) {
		if (getSubElements().length > 0) {
		    MenuElement me[] = new MenuElement[2];
		    me[0]=(MenuElement)this;
		    me[1]=getSubElements()[0];
		    MenuSelectionManager.defaultManager().setSelectedPath(me);
		} else {
		    MenuElement me[] = new MenuElement[1];
		    me[0]=(MenuElement)this;
		    MenuSelectionManager.defaultManager().setSelectedPath(me);
		}
	    }
        }

        if(b) {
            int popupType;
            int newPopupType;
            boolean shouldDowngradeHeavyWeight = ancestorIsModalDialog(invoker);

            firePopupMenuWillBecomeVisible();
            switch(lastPopupType) {
            case LIGHT_WEIGHT_POPUP:
                popup = createLightWeightPopup();
                break;
            case MEDIUM_WEIGHT_POPUP:
                popup = createMediumWeightPopup();
                break;
            case HEAVY_WEIGHT_POPUP:
                popup = createHeavyWeightPopup();
                break;
            }
            popupType = lastPopupType;

            popup.setLocationOnScreen(desiredLocationX,desiredLocationY);
            popup.addComponent(this,"Center");
            popup.setBackground(getBackground());
            popup.pack();

            Rectangle popupRect = new Rectangle(desiredLocationX,desiredLocationY,
                                                popup.getWidth(),popup.getHeight());

            if(popupFit(popupRect)) {
                if(lightWeightPopupEnabled)
                    newPopupType = LIGHT_WEIGHT_POPUP;
                else
                    newPopupType = MEDIUM_WEIGHT_POPUP;
            } else {
                if(shouldDowngradeHeavyWeight)
                    newPopupType = MEDIUM_WEIGHT_POPUP;
                else
                    newPopupType = HEAVY_WEIGHT_POPUP;
            }

	    if(invokerInHeavyWeightPopup(invoker))
		newPopupType = HEAVY_WEIGHT_POPUP;

	    if(invoker == null) {
		newPopupType = HEAVY_WEIGHT_POPUP;
	    }

            if(newPopupType != popupType) {
                replacePopup(newPopupType);
                popupType = newPopupType;
            }

            lastPopupType = popupType;
            popup.show(invoker);

        } else if(popup != null) {
            firePopupMenuWillBecomeInvisible();
            popup.hide();
            popup.removeComponent(this);
            recyclePopup(popup);
            popup = null;
        }
        if (accessibleContext != null) {
	    if (b) {
		accessibleContext.firePropertyChange(
			AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
			null, AccessibleState.VISIBLE);
	    } else {
		accessibleContext.firePropertyChange(
			AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
			AccessibleState.VISIBLE, null);
	    }
        }
    }

    /**
     * Returns true if the popupmenu is visible (currently
     * being displayed).
     */
    public boolean isVisible() {
        if(popup != null)
            return popup.isShowing();
        else
            return false;
    }


    /**
     * Set the location of the upper left corner of the
     * popup menu using x, y coordinates.
     *
     * @param x the x coordinate of the popup's new position
     * @param y the y coordinate of the popup's new position
     * @beaninfo
     * description: The location of the popup menu.
     */
    public void setLocation(int x, int y) {
        if(popup != null)
            popup.setLocationOnScreen(x, y);
        else {
            desiredLocationX = x;
            desiredLocationY = y;
        }
    }

    /**
     * Returns true if the popupmenu is a stand-alone popup menu
     * rather than the submenu of a JMenu.
     *
     * @return true if this menu is a stand-alone popup menu
     */
    private boolean isPopupMenu() {
        return  ((invoker != null) && !(invoker instanceof JMenu));
    }

    /**
     * Returns the component which is the 'invoker' of this 
     * popup menu.
     *
     * @return the Component in which the popup menu is displayed
     */
    public Component getInvoker() {
        return this.invoker;
    }

    /**
     * Sets the invoker of this popupmenu -- the component in which
     * the popupmenu menu is to be displayed.
     *
     * @param invoker the Component in which the popup menu is displayed
     * @beaninfo
     * description: The invoking component for the popup menu
     *      expert: true
     */
    public void setInvoker(Component invoker) {
        Component oldInvoker = this.invoker;
        this.invoker = invoker;
        if ((oldInvoker != this.invoker) && (ui != null)) {
            ui.uninstallUI(this);
            ui.installUI(this);
        }               
        invalidate();
    }


    /**
     * Display the popupmenu at the position x,y in the coordinate
     * space of the component invoker.
     *
     * @param invoker The component in whose space the popupmenu is to appear
     * @param x the x coordinate in invoker's coordinate space at which 
     * the popup menu is to be displayed
     * @param y the y coordinate in invoker's coordinate space at which 
     * the popup menu is to be displayed
     */
    public void show(Component invoker, int x, int y) {
        setInvoker(invoker);
        Frame newFrame = getFrame(invoker);
        if (newFrame != frame) {
            // Use the invoker's frame so that events 
            // are propogated properly
            if (newFrame!=null) {
                this.frame = newFrame;
                if(popup != null) {
                    setVisible(false);
                }
            }
        }
	Point invokerOrigin;
	if (invoker != null) {
	    invokerOrigin = invoker.getLocationOnScreen();
	    setLocation(invokerOrigin.x + x, 
			invokerOrigin.y + y);
	} else {
	    setLocation(x, y);
	}
        setVisible(true);       
    }

    /**
     * Returns the popupmenu which is at the root of the menu system
     * for this popupmenu.
     *
     * @return the topmost grandparent JPopupMenu
     */
    JPopupMenu getRootPopupMenu() {
        JPopupMenu mp = this;
        while((mp!=null) && (mp.isPopupMenu()!=true) &&
              (mp.getInvoker() != null) &&
              (mp.getInvoker().getParent() != null) &&
              (mp.getInvoker().getParent() instanceof JPopupMenu)
              ) {
            mp = (JPopupMenu) mp.getInvoker().getParent();
        }
        return mp;
    }

    /**
     * Returns the component at the specified index.
     * This method is obsolete, please use <code>getComponent(int i)</code> instead.
     * 
     * @param i  the index of the component, where 0 is the first 
     * @return the Component at that index 
     */
    public Component getComponentAtIndex(int i) {
        return getComponent(i);
    }

    /**
     * Returns the index of the specified component.
     * 
     * @param  the Component to find
     * @return the index of the component, where 0 is the first,
     *         or -1 if the component is not found
     */
    public int getComponentIndex(Component c) {
        int ncomponents = this.getComponentCount();
        Component[] component = this.getComponents();
        for (int i = 0 ; i < ncomponents ; i++) {
            Component comp = component[i];
            if (comp == c) 
                return i;
        }
        return -1;
    }

    /**
     * Sets the size of the Popup window using a Dimension object.
     *
     * @param <code>d</code> The dimension specifying the new size 
     * of this component.
     * @beaninfo
     * description: The size of the popup menu
     */
    public void setPopupSize(Dimension d) {
        if(popup != null)
	    popup.setSize(d.width,d.height);
    }

    /**
     * Sets the size of the Popup window to the specified width and
     * height.
     *
     * @param <code>width</code> The new width of the Popup in pixels.
     * @param <code>height</code> The new height of the Popup in pixels.
     * @beaninfo
     * description: The size of the popup menu
     */
    public void setPopupSize(int width, int height) {
        if(popup != null)
	    popup.setSize(width, height);
    }
    
    /**
     * Sets the currently selected component,  This will result
     * in a change to the selection model.
     *
     * @param sel the Component to select
     * @beaninfo
     * description: The selected component on the popup menu
     *      expert: true
     *      hidden: true
     */
    public void setSelected(Component sel) {    
        SingleSelectionModel model = getSelectionModel();
        int index = getComponentIndex(sel);
        model.setSelectedIndex(index);
    }

    /**
     * Checks whether the border should be painted.
     *
     * @return true if the border is painted
     * @see #setBorderPainted
     */
    public boolean isBorderPainted() {
        return paintBorder;
    }

    /**
     * Sets whether the border should be painted.
     *
     * @param b if true, the border is painted.
     * @see #isBorderPainted
     * @beaninfo
     * description: Is the border of the popup menu painted
     */
    public void setBorderPainted(boolean b) {
        paintBorder = b;
        repaint();
    }

    /**
     * Paint the popup menu's border if BorderPainted property is true.
     * 
     * @see JComponent#paint
     * @see JComponent#setBorder
     */
    protected void paintBorder(Graphics g) {    
        if (isBorderPainted()) {
            super.paintBorder(g);
        }
    }

    /**
     * Returns the margin between the popupmenu's border and
     * its containees.
     *
     * return an Insets object containing the margin values.
     */
    public Insets getMargin() {
        if(margin == null) {
            return new Insets(0,0,0,0);
        } else {
            return margin;
        }
    }


    boolean isSubPopupMenu(JPopupMenu popup) {
        int ncomponents = this.getComponentCount();
        Component[] component = this.getComponents();
        for (int i = 0 ; i < ncomponents ; i++) {
            Component comp = component[i];
            if (comp instanceof JMenu) {
                JMenu menu = (JMenu)comp;
                JPopupMenu subPopup = menu.getPopupMenu();
                if (subPopup == popup)
                    return true;
                if (subPopup.isSubPopupMenu(popup))
                    return true;
            }
        }
        return false;
    }

    private boolean invokerInHeavyWeightPopup(Component i) {
	if (i !=null) {
	    Container parent;
	    for(parent = i.getParent() ; parent != null ; parent =
		    parent.getParent()) {
		if(parent instanceof WindowPopup)
		    return true;
		else if(parent instanceof PanelPopup)
		    break;
		else if(parent instanceof JPanelPopup)
		    break;
	    }
	}
        return false;
    }


    private static Frame getFrame(Component c) {
        Component w = c;

        while(!(w instanceof Frame) && (w!=null)) {
            w = w.getParent();
        }
        return (Frame)w;
    }

    /*
     * The following interface describes what a popup should implement.
     * We do this because JPopupMenu uses popup that can be windows or
     * panels. 
     */
    private interface Popup {
        public void setSize(int width,int height);
        public int  getWidth();
        public int  getHeight();
        public void addComponent(Component aComponent,Object constraints);
        public void removeComponent(Component c);
        public void pack();
        public void setBackground(Color c);
        public void show(Component invoker);
        public void hide();
        public boolean isShowing();
        public Rectangle getBoundsOnScreen();
        public void setLocationOnScreen(int x,int y);
        public Component getComponent();
    }

  /**
   * A class used to popup a window.
   * <p>
   * <strong>Warning:</strong>
   * Serialized objects of this class will not be compatible with
   * future Swing releases.  The current serialization support is appropriate
   * for short term storage or RMI between applications running the same
   * version of Swing.  A future release of Swing will provide support for
   * long term persistence.
   */
  class WindowPopup extends JWindow implements Popup,Serializable,Accessible {
    int saveX,saveY;
    boolean  firstShow = true;

    public WindowPopup(Frame f) {
      super(f);
    }
        
    public Component getComponent() {
      return this;
    }

    public int  getWidth() {
      return getBounds().width;
    }

    public int  getHeight() {
      return getBounds().height;
    }

    public void update(Graphics g) {
      paint(g);
    }
        
    public void show(Component invoker) {
      this.setLocation(saveX,saveY);
      this.setVisible(true);

      /** This hack is to workaround a bug on Solaris where the windows does not really show
       *  the first time
       */
      if(firstShow) {
        this.hide();
        this.setVisible(true);
        firstShow = false;
      }
    }
        
    public void hide() {
      super.hide();
      /** We need to call removeNotify() here because hide() does something only if
       *  Component.visible is true. When the app frame is miniaturized, the parent 
       *  frame of this frame is invisible, causing AWT to believe that this frame
       *  is invisible and causing hide() to do nothing
       */
      removeNotify();
    }

    public Rectangle getBoundsOnScreen() {
      return getBounds();
    }

    public void setLocationOnScreen(int x,int y) {
      this.setLocation(x,y);
      saveX = x;
      saveY = y;
    }

    public void addComponent(Component aComponent,Object constraints) {
      this.getContentPane().add(aComponent,constraints);
    }

    public void removeComponent(Component c) {
      this.getContentPane().remove(c);
    }

    /////////////////
    // Accessibility support
    ////////////////

    protected AccessibleContext accessibleContext = null;
    
    /**
     * Get the AccessibleContext associated with this JWindow
     *
     * @return the AccessibleContext of this JWindow
     */
    public AccessibleContext getAccessibleContext() {
      if (accessibleContext == null) {
        accessibleContext = new AccessibleWindowPopup();
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
    protected class AccessibleWindowPopup extends AccessibleContext
    implements Serializable, AccessibleComponent {
    
      // AccessibleContext methods
      //
      /**
       * Get the role of this object.
       *
       * @return an instance of AccessibleRole describing the role of
       * the object
       * @see AccessibleRole
       */
      public AccessibleRole getAccessibleRole() {
        return AccessibleRole.WINDOW;
      }
    
      /**
       * Get the state of this object.
       *
       * @return an instance of AccessibleStateSet containing the 
       * current state set of the object
       * @see AccessibleState
       */
      public AccessibleStateSet getAccessibleStateSet() {
        AccessibleStateSet states = SwingUtilities.getAccessibleStateSet(WindowPopup.this);
        if (getFocusOwner() != null) {
          states.add(AccessibleState.ACTIVE);
        }
        return states;
      }

      /**
       * Get the Accessible parent of this object.  If the parent of this
       * object implements Accessible, this method should simply return
       * getParent().
       *
       * @return the Accessible parent of this object -- can be null if 
       * this object does not have an Accessible parent
       */
      public Accessible getAccessibleParent() {
          if (accessibleParent != null) {
	      return accessibleParent;
	  } else {
              Container parent = getParent();
              if (parent instanceof Accessible) {
                  return (Accessible) parent;
	      }
          }
          return null;
      }
    
      /**
       * Get the index of this object in its accessible parent. 
       *
       * @return the index of this object in its parent; -1 if this 
       * object does not have an accessible parent.
       * @see #getAccessibleParent
       */
      public int getAccessibleIndexInParent() {
        return SwingUtilities.getAccessibleIndexInParent(WindowPopup.this);
      }
    
      /**
       * Returns the number of accessible children in the object.  If all
       * of the children of this object implement Accessible, than this
       * method should return the number of children of this object.
       *
       * @return the number of accessible children in the object.
       */
      public int getAccessibleChildrenCount() {
        return SwingUtilities.getAccessibleChildrenCount(WindowPopup.this);
      }
    
      /**
       * Return the nth Accessible child of the object.  
       *
       * @param i zero-based index of child
       * @return the nth Accessible child of the object
       */
      public Accessible getAccessibleChild(int i) {
        return SwingUtilities.getAccessibleChild(WindowPopup.this,i);
      }
    
      /**
       * Return the locale of this object.
       *
       * @return the locale of this object
       */
      public Locale getLocale() {
        return WindowPopup.this.getLocale();
      }
    
      /**
       * Get the AccessibleComponent associated with this object if one
       * exists.  Otherwise return null.
       */
      public AccessibleComponent getAccessibleComponent() {
        return this;
      }
    
    
      // AccessibleComponent methods
      //
            /**
             * Get the background color of this object.
             *
             * @return the background color, if supported, of the object; 
             * otherwise, null
             */
            public Color getBackground() {
                return WindowPopup.this.getBackground();
            }
    
            /**
             * Set the background color of this object.
             *
             * @param c the new Color for the background
             */
            public void setBackground(Color c) {
                WindowPopup.this.setBackground(c);
            }
    
            /**
             * Get the foreground color of this object.
             *
             * @return the foreground color, if supported, of the object; 
             * otherwise, null
             */
            public Color getForeground() {
                return WindowPopup.this.getForeground();
            }
    
            /**
             * Set the foreground color of this object.
             *
             * @param c the new Color for the foreground
             */
            public void setForeground(Color c) {
                WindowPopup.this.setForeground(c);
            }
    
            /**
             * Get the Cursor of this object.
             *
             * @return the Cursor, if supported, of the object; otherwise, null
             */
            public Cursor getCursor() {
                return WindowPopup.this.getCursor();
            }
    
            /**
             * Set the Cursor of this object.
             *
             * @param c the new Cursor for the object
             */
            public void setCursor(Cursor cursor) {
                WindowPopup.this.setCursor(cursor);
            }
    
            /**
             * Get the Font of this object.
             *
             * @return the Font,if supported, for the object; otherwise, null
             */
            public Font getFont() {
                return WindowPopup.this.getFont();
            }
    
            /**
             * Set the Font of this object.
             *
             * @param f the new Font for the object
             */
            public void setFont(Font f) {
                WindowPopup.this.setFont(f);
            }
    
            /**
             * Get the FontMetrics of this object.
             *
             * @param f the Font
             * @return the FontMetrics, if supported, the object; 
             * otherwise, null
             * @see #getFont
             */
            public FontMetrics getFontMetrics(Font f) {
                return WindowPopup.this.getFontMetrics(f);
            }
    
            /**
             * Determine if the object is enabled.
             *
             * @return true if object is enabled; otherwise, false
             */
            public boolean isEnabled() {
                return WindowPopup.this.isEnabled();
            }
    
            /**
             * Set the enabled state of the object.
             *
             * @param b if true, enables this object; otherwise, disables it 
             */
            public void setEnabled(boolean b) {
                WindowPopup.this.setEnabled(b);
            }
            
            /**
             * Determine if the object is visible.  Note: this means that the
             * object intends to be visible; however, it may not in fact be
             * showing on the screen because one of the objects this object
             * is contained by is not visible.  To determine if an object is
             * showing on the screen, use isShowing().
             *
             * @return true if object is visible; otherwise, false
             */
            public boolean isVisible() {
                return WindowPopup.this.isVisible();
            }
    
            /**
             * Set the visible state of the object.
             *
             * @param b if true, shows this object; otherwise, hides it 
             */
            public void setVisible(boolean b) {
                WindowPopup.this.setVisible(b);
            }
    
            /**
             * Determine if the object is showing.  Determined by checking
             * the visibility of the object and ancestors of the object.  
             * This will return true even if the object is obscured by another 
             * (for example, it is underneath a menu that was pulled 
             * down).
             *
             * @return true if object is showing; otherwise, false
             */
            public boolean isShowing() {
                return WindowPopup.this.isShowing();
            }
    
            /** 
             * Checks if the specified point is within this object's bounds,
             * where the point's x and y coordinates are defined to be relative
             * to the coordinate system of the object. 
             *
             * @param p the Point relative to the coordinate system of the 
             * object
             * @return true if object contains Point; otherwise false
             */
            public boolean contains(Point p) {
                return WindowPopup.this.contains(p);
            }
        
            /** 
             * Returns the location of the object on the screen.
             *
             * @return location of object on screen -- can be null if this
             * object is not on the screen
             */
            public Point getLocationOnScreen() {
                return WindowPopup.this.getLocationOnScreen();
            }
    
            /** 
             * Gets the location of the object relative to the parent in the 
             * form of a point specifying the object's top-left corner in the
             * screen's coordinate space.
             *
             * @return An instance of Point representing the top-left corner 
             * of the objects's bounds in the coordinate space of the screen; 
             * null if this object or its parent are not on the screen
             */
            public Point getLocation() {
                return WindowPopup.this.getLocation();
            }
    
            /** 
             * Sets the location of the object relative to the parent.
             */
            public void setLocation(Point p) {
                WindowPopup.this.setLocation(p);
            }
    
            /** 
             * Gets the bounds of this object in the form of a Rectangle 
             * object.  The bounds specify this object's width, height, 
             * and location relative to its parent. 
             *
             * @return A rectangle indicating this component's bounds; null if 
             * this object is not on the screen.
             */
            public Rectangle getBounds() {
                return WindowPopup.this.getBounds();
            }
    
            /** 
             * Sets the bounds of this object in the form of a Rectangle 
             * object.  The bounds specify this object's width, height, 
             * and location relative to its parent.
             *      
             * @param A rectangle indicating this component's bounds
             */
            public void setBounds(Rectangle r) {
                WindowPopup.this.setBounds(r);
            }
    
            /** 
             * Returns the size of this object in the form of a Dimension 
             * object.  The height field of the Dimension object contains 
             * this objects's height, and the width field of the Dimension 
             * object contains this object's width. 
             *
             * @return A Dimension object that indicates the size of this 
             * component; null if this object is not on the screen
             */
            public Dimension getSize() {
                return WindowPopup.this.getSize();
            }
    
            /** 
             * Resizes this object so that it has width width and height. 
             *      
             * @param d - The dimension specifying the new size of the object. 
             */
            public void setSize(Dimension d) {
                WindowPopup.this.setSize(d);
            }
    
            /**
             * Returns the Accessible child, if one exists, contained at the 
             * local coordinate Point.
             *
             * @param p The point defining the top-left corner of the 
             * Accessible, given in the coordinate space of the object's 
             * parent. 
             * @return the Accessible, if it exists, at the specified 
             * location; else null
             */
            public Accessible getAccessibleAt(Point p) {
                return SwingUtilities.getAccessibleAt(WindowPopup.this,p);
            }
    
            /**
             * Returns whether this object can accept focus or not.
             *
             * @return true if object can accept focus; otherwise false
             */
            public boolean isFocusTraversable() {
                return WindowPopup.this.isFocusTraversable();
            }
    
            /**
             * Requests focus for this object.
             */
            public void requestFocus() {
                WindowPopup.this.requestFocus();
            }
    
            /**
             * Adds the specified focus listener to receive focus events from
             * this component. 
             *
             * @param l the focus listener
             */
            public void addFocusListener(FocusListener l) {
                WindowPopup.this.addFocusListener(l);
            }
    
            /**
             * Removes the specified focus listener so it no longer receives 
             * focus events from this component.
             *
             * @param l the focus listener
             */
            public void removeFocusListener(FocusListener l) {
                WindowPopup.this.removeFocusListener(l);
            }
        } // inner class AccessibleWindowPopup
    }

    /**
     * A class used to popup a JPanel.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    class JPanelPopup extends JPanel implements Popup,Serializable {
        int desiredLocationX,desiredLocationY;

        public JPanelPopup() {
            super();
            setLayout(new BorderLayout());
            setDoubleBuffered(true);
            this.setOpaque(true);
        }

        public Component getComponent() {
            return this;
        }

        public void addComponent(Component aComponent,Object constraints) {
            this.add(aComponent,constraints);
        }

        public void removeComponent(Component c) {
            this.remove(c);
        }

        public void update(Graphics g) {
            paint(g);
        }
        
        public void pack() {
            setSize(getPreferredSize());
        }


        public void show(Component invoker) {
	    Container parent = null;
	    if (invoker != null)
		parent = invoker.getParent();
            Window parentWindow = null;

            for(Container p = parent; p != null; p = p.getParent()) {
                if(p instanceof JRootPane) {
		    if(p.getParent() instanceof JInternalFrame)
			continue;		    
                    parent = ((JRootPane)p).getLayeredPane();
                    for(p = parent.getParent(); p != null && (!(p instanceof java.awt.Window)); 
                        p = p.getParent());
                    parentWindow = (Window)p;
                    break;
                } else if(p instanceof Window) {
                    parent = p;
                    parentWindow = (Window)p;
                    break;
                }
            }
            Point p = convertScreenLocationToParent(parent,desiredLocationX,desiredLocationY);
            this.setLocation(p.x,p.y);
            if(parent instanceof JLayeredPane) {
                ((JLayeredPane)parent).add(this,JLayeredPane.POPUP_LAYER,0);
            } else
                parent.add(this);
        }

        public void hide() {
            Container parent = getParent();
            Rectangle r = this.getBounds();
            if(parent != null)
                parent.remove(this);
            parent.repaint(r.x,r.y,r.width,r.height);
        }

        public Rectangle getBoundsOnScreen() {
            Container parent = getParent();
            if(parent != null) {
                Rectangle r = getBounds();
                Point p;
                p = convertParentLocationToScreen(parent,r.x,r.y);
                r.x = p.x;
                r.y = p.y;
                return r;
            } else 
                throw new Error("getBoundsOnScreen called on an invisible popup");
        }

        Point convertParentLocationToScreen(Container parent,int x,int y) {
            Window parentWindow = null;
            Rectangle r;
            Container p;
            Point pt;
            for(p = this; p != null; p = p.getParent()) {
                if(p instanceof Window) {
                    parentWindow = (Window)p;
                    break;
                }
            }
            if(parentWindow != null) {
                r = parentWindow.getBounds();
                pt = new Point(x,y);
                pt = SwingUtilities.convertPoint(parent,pt,null);
                pt.x += r.x;
                pt.y += r.y;
                return pt;
            } else
                throw new Error("convertParentLocationToScreen: no window ancestor found");                        }

        Point convertScreenLocationToParent(Container parent,int x,int y) {
            Window parentWindow = null;
            Rectangle r;
            for(Container p = parent; p != null; p = p.getParent()) {
                if(p instanceof Window) {
                    parentWindow = (Window)p;
                    break;
                }
            }
            if(parentWindow != null) {
                Point p = new Point(x,y);
                SwingUtilities.convertPointFromScreen(p,parent);
                return p;
            } else
                throw new Error("convertScreenLocationToParent: no window ancestor found");
        }

        public void setLocationOnScreen(int x,int y) {
            Container parent = getParent();
            if(parent != null) {
                Point p = convertScreenLocationToParent(parent,x,y);
                this.setLocation(p.x,p.y);
            } else {
                desiredLocationX = x;
                desiredLocationY = y;
            }
        }
    }

    /**
     * A class used to popup an AWT panel.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    class PanelPopup extends Panel implements Popup,Serializable {
        int desiredLocationX,desiredLocationY;
	JRootPane rootPane;
        public PanelPopup() {
            super();
            setLayout(new BorderLayout());
	    rootPane = new JRootPane();
	    this.add(rootPane, BorderLayout.CENTER);
        }

        public int getWidth() {
            return getBounds().width;
        }

        public int getHeight() {
            return getBounds().height;
        }

        public Component getComponent() {
            return this;
        }

        public void addComponent(Component aComponent,Object constraints) {
            rootPane.getContentPane().add(aComponent,constraints);
        }

        public void removeComponent(Component c) {
            rootPane.getContentPane().remove(c);
        }

        public void update(Graphics g) {
            paint(g);
        }
        
        public void paint(Graphics g) {
            super.paint(g);
        }
        
        public void pack() {
            setSize(getPreferredSize());
        }


        public void show(Component invoker) {
	    Container parent = null;
	    if (invoker != null)
		parent = invoker.getParent();
	    /*
	      Find the top level window,  
	      if it has a layered pane,
	      add to that, otherwise
	      add to the window. */

	    while(!(parent instanceof Window || parent instanceof Applet) && (parent!=null)) {
		parent = parent.getParent();
	    }
	    if (parent instanceof RootPaneContainer) {
		parent = ((RootPaneContainer)parent).getLayeredPane();
		Point p = convertScreenLocationToParent(parent,desiredLocationX,desiredLocationY);
		this.setLocation(p.x,p.y);
		((JLayeredPane)parent).add(this,JLayeredPane.POPUP_LAYER,0);
	    } else {
		Point p = convertScreenLocationToParent(parent,desiredLocationX,desiredLocationY);
		this.setLocation(p.x,p.y);
		parent.add(this);
	    }
	}
          
        public void hide() {
            Container parent = getParent();
            Rectangle r = this.getBounds();
            if(parent != null)
                parent.remove(this);
            parent.repaint(r.x,r.y,r.width,r.height);
        }

        public Rectangle getBoundsOnScreen() {
            Container parent = getParent();
            if(parent != null) {
                Rectangle r = getBounds();
                Point p;
                p = convertParentLocationToScreen(parent,r.x,r.y);
                r.x = p.x;
                r.y = p.y;
                return r;
            } else 
                throw new Error("getBoundsOnScreen called on an invisible popup");
        }

        Point convertParentLocationToScreen(Container parent,int x,int y) {
            Window parentWindow = null;
            Rectangle r;
            Container p;
            Point pt;
            for(p = this; p != null; p = p.getParent()) {
                if(p instanceof Window) {
                    parentWindow = (Window)p;
                    break;
                }
            }
            if(parentWindow != null) {
                r = parentWindow.getBounds();
                pt = new Point(x,y);
                pt = SwingUtilities.convertPoint(parent,pt,null);
                pt.x += r.x;
                pt.y += r.y;
                return pt;
            } else
                throw new Error("convertParentLocationToScreen: no window ancestor found");                        }

        Point convertScreenLocationToParent(Container parent,int x,int y) {
            Window parentWindow = null;
            Rectangle r;
            for(Container p = parent; p != null; p = p.getParent()) {
                if(p instanceof Window) {
                    parentWindow = (Window)p;
                    break;
                }
            }
            if(parentWindow != null) {
                Point p = new Point(x,y);
                SwingUtilities.convertPointFromScreen(p,parent);
                return p;
            } else
                throw new Error("convertScreenLocationToParent: no window ancestor found");
        }

        public void setLocationOnScreen(int x,int y) {
            Container parent = getParent();
            if(parent != null) {
                Point p = convertScreenLocationToParent(parent,x,y);
                this.setLocation(p.x,p.y);
            } else {
                desiredLocationX = x;
                desiredLocationY = y;
            }
        }
    }


    /**
     * Returns a string representation of this JPopupMenu. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this JPopupMenu.
     */
    protected String paramString() {
	String labelString = (label != null ?
			      label : "");
	String paintBorderString = (paintBorder ?
				    "true" : "false");
	String marginString = (margin != null ?
			      margin.toString() : "");
        String lastPopupTypeString;
        if (lastPopupType == LIGHT_WEIGHT_POPUP) {
            lastPopupTypeString = "LIGHT_WEIGHT_POPUP";
        } else if (lastPopupType == MEDIUM_WEIGHT_POPUP) {
            lastPopupTypeString = "MEDIUM_WEIGHT_POPUP";
        } else if (lastPopupType == HEAVY_WEIGHT_POPUP) {
            lastPopupTypeString = "HEAVY_WEIGHT_POPUP";
        } else lastPopupTypeString = "";
	String lightWeightPopupEnabledString = (lightWeightPopupEnabled ?
						"true" : "false");	
	return super.paramString() +
	",desiredLocationX=" + desiredLocationX +
	",desiredLocationY=" + desiredLocationY +
	",label=" + labelString +
	",lastPopupType=" + lastPopupTypeString +
	",lightWeightPopupEnabled=" + lightWeightPopupEnabledString +
	",margin=" + marginString +
	",paintBorder=" + paintBorderString;
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
            accessibleContext = new AccessibleJPopupMenu();
        }
        return accessibleContext;
    }

    protected class AccessibleJPopupMenu extends AccessibleJComponent {
        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of 
         * the object
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.POPUP_MENU;
        }
    } // inner class AccessibleJPopupMenu


////////////
// Serialization support.  
////////////
    private void writeObject(ObjectOutputStream s) throws IOException {
        Vector      values = new Vector();

        s.defaultWriteObject();
        // Save the invoker, if its Serializable.
        if(invoker != null && invoker instanceof Serializable) {
            values.addElement("invoker");
            values.addElement(invoker);
        }
        // Save the popup, if its Serializable.
        if(popup != null && popup instanceof Serializable) {
            values.addElement("popup");
            values.addElement(popup);
        }
        // Save the frame, if its Serializable.
        if(frame != null && frame instanceof Serializable) {
            values.addElement("frame");
            values.addElement(frame);
        }
        s.writeObject(values);

	if ((ui != null) && (getUIClassID().equals(uiClassID))) {
	    ui.installUI(this);
	}
    }

    // implements javax.swing.MenuElement
    private void readObject(ObjectInputStream s) 
        throws IOException, ClassNotFoundException {
        s.defaultReadObject();

        Vector          values = (Vector)s.readObject();
        int             indexCounter = 0;
        int             maxCounter = values.size();

        if(indexCounter < maxCounter && values.elementAt(indexCounter).
           equals("invoker")) {
            invoker = (Component)values.elementAt(++indexCounter);
            indexCounter++;
        }
        if(indexCounter < maxCounter && values.elementAt(indexCounter).
           equals("popup")) {
            popup = (Popup)values.elementAt(++indexCounter);
            indexCounter++;
        }
        if(indexCounter < maxCounter && values.elementAt(indexCounter).
           equals("frame")) {
            frame = (Frame)values.elementAt(++indexCounter);
            indexCounter++;
        }
    }

    
    // implements javax.swing.MenuElement
    public void processMouseEvent(MouseEvent event,MenuElement path[],MenuSelectionManager manager) {}

    // implements javax.swing.MenuElement
    public void processKeyEvent(KeyEvent e,MenuElement path[],MenuSelectionManager manager) {
    }

    // implements javax.swing.MenuElement
    public void menuSelectionChanged(boolean isIncluded) {
        if(invoker instanceof JMenu) {
            JMenu m = (JMenu) invoker;
            if(isIncluded) 
                m.setPopupMenuVisible(true);
            else
                m.setPopupMenuVisible(false);
        }
        if (isPopupMenu() && !isIncluded)
          setVisible(false);
    }

    // implements javax.swing.MenuElement
    public MenuElement[] getSubElements() {
        MenuElement result[];
        Vector tmp = new Vector();
        int c = getComponentCount();
        int i;
        Component m;

        for(i=0 ; i < c ; i++) {
            m = getComponent(i);
            if(m instanceof MenuElement)
                tmp.addElement(m);
        }

        result = new MenuElement[tmp.size()];
        for(i=0,c=tmp.size() ; i < c ; i++) 
            result[i] = (MenuElement) tmp.elementAt(i);
        return result;
    }

    public Component getComponent() {
        return this;
    }


    /**
     * A popupmenu-specific separator.
     */
    static public class Separator extends JSeparator
    {
        public Separator( )
	{
	    super( JSeparator.HORIZONTAL );
        }

        /**
	 * Returns the name of the L&F class that renders this component.
	 *
	 * @return "PopupMenuSeparatorUI"
	 * @see JComponent#getUIClassID
	 * @see UIDefaults#getUI
	 */
        public String getUIClassID()
	{
            return "PopupMenuSeparatorUI";
	}
    }

}

