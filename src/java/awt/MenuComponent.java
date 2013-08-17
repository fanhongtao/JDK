/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt;

import java.awt.peer.MenuComponentPeer;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import javax.accessibility.*;

/**
 * The abstract class <code>MenuComponent</code> is the superclass 
 * of all menu-related components. In this respect, the class
 * <code>MenuComponent</code> is analogous to the abstract superclass
 * <code>Component</code> for AWT components.
 * <p>
 * Menu components receive and process AWT events, just as components do,
 * through the method <code>processEvent</code>.
 *
 * @version 	1.57, 02/06/02
 * @author 	Arthur van Hoff
 * @since       JDK1.0
 */
public abstract class MenuComponent implements java.io.Serializable {

    static {
        /* ensure that the necessary native libraries are loaded */
	Toolkit.loadLibraries();
        initIDs();
    }

    transient MenuComponentPeer peer;
    transient MenuContainer parent;

    /**
     * The AppContext of the MenuComponent.  This is set in the constructor
     * and never changes.
     */
    transient AppContext appContext;

    /**
     * The Menu Components Font.
     * This value can be null at which point a default will be used.
     *
     * @serial
     * @see setFont()
     * @see getFont()
     */
    Font font;
    /**
     * The Menu Components name.
     * @serial
     * @see getName()
     * @see setName()
     */
    private String name;
    /**
     * A variable to indicate whether a name is explicitly set.
     * If it is true the name will be set explicitly.
     * @serial
     * @see setName()
     */
    private boolean nameExplicitlySet = false;
    /**
     * @serial
     * @see dispatchEvent()
     */
    boolean newEventsOnly = false;
 
    /*
     * Internal constants for serialization 
     */
    final static String actionListenerK = Component.actionListenerK;
    final static String itemListenerK = Component.itemListenerK;

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = -4536902356223894379L;

    /**
     * This object is used as a key for internal hashtables.
     */
    transient private Object privateKey = new Object();

    /**
     * Constructor for MenuComponent.
     */
    public MenuComponent() {
	appContext = AppContext.getAppContext();
	SunToolkit.insertTargetMapping(this, appContext);
    }

    /**
     * Construct a name for this MenuComponent.  Called by getName() when
     * the name is null.
     */
    String constructComponentName() {
        return null; // For strict compliance with prior platform versions, a MenuComponent
                     // that doesn't set its name should return null from
                     // getName()
    }

    /**
     * Gets the name of the menu component.
     * @return        the name of the menu component.
     * @see           java.awt.MenuComponent#setName(java.lang.String)
     * @since         JDK1.1
     */
    public String getName() {
        if (name == null && !nameExplicitlySet) {
            synchronized(this) {
                if (name == null && !nameExplicitlySet)
                    name = constructComponentName();
            }
        }
        return name;
    }

    /**
     * Sets the name of the component to the specified string.
     * @param         name    the name of the menu component.
     * @see           java.awt.MenuComponent#getName
     * @since         JDK1.1
     */
    public void setName(String name) {
        synchronized(this) {
            this.name = name;
            nameExplicitlySet = true;
        }
    }

    /**
     * Returns the parent container for this menu component.
     * @return    the menu component containing this menu component, 
     *                 or <code>null</code> if this menu component 
     *                 is the outermost component, the menu bar itself.
     */
    public MenuContainer getParent() {
	return getParent_NoClientCode();
    }
    // NOTE: This method may be called by privileged threads.
    //       This functionality is implemented in a package-private method 
    //       to insure that it cannot be overridden by client subclasses. 
    //       DO NOT INVOKE CLIENT CODE ON THIS THREAD!
    final MenuContainer getParent_NoClientCode() {
	return parent;
    }

    /**
     * @deprecated As of JDK version 1.1,
     * programs should not directly manipulate peers.
     */
    public MenuComponentPeer getPeer() {
	return peer;
    }

    /**
     * Gets the font used for this menu component.
     * @return   the font used in this menu component, if there is one; 
     *                  <code>null</code> otherwise.
     * @see     java.awt.MenuComponent#setFont
     */
    public Font getFont() {
	Font font = this.font;
	if (font != null) {
	    return font;
	}
	MenuContainer parent = this.parent;
	if (parent != null) {
	    return parent.getFont();
	}
	return null;
    }

    // NOTE: This method may be called by privileged threads.
    //       This functionality is implemented in a package-private method 
    //       to insure that it cannot be overridden by client subclasses. 
    //       DO NOT INVOKE CLIENT CODE ON THIS THREAD!
    final Font getFont_NoClientCode() {
	Font font = this.font;
	if (font != null) {
	    return font;
	}

	// The MenuContainer interface does not have getFont_NoClientCode()
	// and it cannot, because it must be package-private. Because of
	// this, we must manually cast classes that implement 
	// MenuContainer.
	Object parent = this.parent;
	if (parent != null) {
	    if (parent instanceof Component) {
		font = ((Component)parent).getFont_NoClientCode();
	    } else if (parent instanceof MenuComponent) {
		font = ((MenuComponent)parent).getFont_NoClientCode();
	    }
	}
	return font;
    } // getFont_NoClientCode()


    /**
     * Sets the font to be used for this menu component to the specified 
     * font. This font is also used by all subcomponents of this menu 
     * component, unless those subcomponents specify a different font. 
     * @param     f   the font to be set.
     * @see       java.awt.MenuComponent#getFont
     */
    public void setFont(Font f) {
	font = f;
    }

    /**
     * Removes the menu component's peer.  The peer allows us to modify the
     * appearance of the menu component without changing the functionality of
     * the menu component.
     */
    public void removeNotify() {
        synchronized (getTreeLock()) {
	    MenuComponentPeer p = (MenuComponentPeer)this.peer;
	    if (p != null) {
	        Toolkit.getEventQueue().removeSourceEvents(this);
		this.peer = null;
		p.dispose();
	    }
	}
    }

    /**
     * Posts the specified event to the menu.
     * This method is part of the Java&nbsp;1.0 event system
     * and it is maintained only for backwards compatibility.
     * Its use is discouraged, and it may not be supported
     * in the future.
     * @param evt the event which is to take place
     * @deprecated As of JDK version 1.1,
     * replaced by <code>dispatchEvent(AWTEvent)</code>.
     */
    public boolean postEvent(Event evt) {
	MenuContainer parent = this.parent;
	if (parent != null) {
	    parent.postEvent(evt);
	}
	return false;
    }

    /*
     * Delivers an event to this component or one of its sub components.
     * @param e the event
     */
    public final void dispatchEvent(AWTEvent e) {
        dispatchEventImpl(e);
    }

    void dispatchEventImpl(AWTEvent e) {
        Toolkit.getDefaultToolkit().notifyAWTEventListeners(e);

        if (newEventsOnly || 
            (parent != null && parent instanceof MenuComponent &&
             ((MenuComponent)parent).newEventsOnly)) {
            if (eventEnabled(e)) {
                processEvent(e);
            } else if (e instanceof ActionEvent && parent != null) {
                ((MenuComponent)parent).dispatchEvent(new ActionEvent(parent, 
                                         e.getID(),
                                         ((ActionEvent)e).getActionCommand()));
            }
                
        } else { // backward compatibility
            Event olde = e.convertToOld();
            if (olde != null) {
                postEvent(olde);
            }
        }
    }

    // REMIND: remove when filtering is done at lower level
    boolean eventEnabled(AWTEvent e) {
        return false;
    }        
    /** 
     * Processes events occurring on this menu component.  
     *
     * @param e the event
     * @since JDK1.1
     */   
    protected void processEvent(AWTEvent e) {
    }

    /**
     * Returns the parameter string representing the state of this  
     * menu component. This string is useful for debugging. 
     * @return     the parameter string of this menu component.
     */
    protected String paramString() {
        String thisName = getName();
        return (thisName != null? thisName : "");
    }

    /**
     * Returns a representation of this menu component as a string. 
     * @return  a string representation of this menu component.
     */
    public String toString() {
	return getClass().getName() + "[" + paramString() + "]";
    }

    /**
     * Gets this component's locking object (the object that owns the thread 
     * sychronization monitor) for AWT component-tree and layout
     * operations.
     * @return This component's locking object.
     */
    protected final Object getTreeLock() {
        return Component.LOCK;
    }

    private void readObject(ObjectInputStream s)
        throws ClassNotFoundException, IOException
    {
        s.defaultReadObject();

        privateKey = new Object();
	appContext = AppContext.getAppContext();
	SunToolkit.insertTargetMapping(this, appContext);
    }

    /**
     * Initialize JNI field and method IDs
     */
    private static native void initIDs();


    /*
     * --- Accessibility Support ---
     *
     *  MenuComponent will contain all of the methods in interface Accessible,
     *  though it won't actually implement the interface - that will be up
     *  to the individual objects which extend MenuComponent.
     */

    AccessibleContext accessibleContext = null;

    /**
     * Get the AccessibleContext associated with this MenuComponent
     *
     * @return the AccessibleContext of this MenuComponent
     */
    public AccessibleContext getAccessibleContext() {
        return accessibleContext;
    }

    /**
     * Inner class of MenuComponent used to provide default support for
     * accessibility.  This class is not meant to be used directly by
     * application developers, but is instead meant only to be
     * subclassed by menu component developers.
     * <p>
     * The class used to obtain the accessible role for this object.
     */
    protected abstract class AccessibleAWTMenuComponent 
	extends AccessibleContext
        implements java.io.Serializable, AccessibleComponent,
	AccessibleSelection {

	/**
	 * Although the class is abstract, this should be called by
	 * all sub-classes. 
	 */
	protected AccessibleAWTMenuComponent() {
        }

        // AccessibleContext methods
        //

	/**
	 * Get the AccessibleSelection associated with this object which allows 
         * its Accessible children to be selected.
	 *
	 * @return AccessibleSelection if supported by object; else return null
	 * @see AccessibleSelection
	 */
	public AccessibleSelection getAccessibleSelection() {
	    return this;
	}

        /**
         * Get the accessible name of this object.  This should almost never
         * return java.awt.MenuComponent.getName(), as that generally isn't
         * a localized name, and doesn't have meaning for the user.  If the
         * object is fundamentally a text object (e.g. a menu item), the
         * accessible name should be the text of the object (e.g. "save").
         * If the object has a tooltip, the tooltip text may also be an
         * appropriate String to return.
         *
         * @return the localized name of the object -- can be null if this
         *         object does not have a name
         * @see AccessibleContext#setAccessibleName
         */
        public String getAccessibleName() {
	    return accessibleName;
        }

        /**
         * Get the accessible description of this object.  This should be
         * a concise, localized description of what this object is - what
         * is its meaning to the user.  If the object has a tooltip, the
         * tooltip text may be an appropriate string to return, assuming
         * it contains a concise description of the object (instead of just
         * the name of the object - e.g. a "Save" icon on a toolbar that
         * had "save" as the tooltip text shouldn't return the tooltip
         * text as the description, but something like "Saves the current
         * text document" instead).
         *
         * @return the localized description of the object -- can be null if
         * this object does not have a description
         * @see AccessibleContext#setAccessibleDescription
         */
        public String getAccessibleDescription() {
	    return accessibleDescription;
        }

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the
         * object
         * @see AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.AWT_COMPONENT; // Non-specific -- overridden in subclasses
        }

        /**
         * Get the state of this object.
         *
         * @return an instance of AccessibleStateSet containing the current
         * state set of the object
         * @see AccessibleState
         */
        public AccessibleStateSet getAccessibleStateSet() {
            return MenuComponent.this.getAccessibleStateSet();
        }

        /**
         * Get the Accessible parent of this object.  If the parent of this
         * object implements Accessible, this method should simply return
         * getParent().
         *
         * @return the Accessible parent of this object -- can be null if this
         * object does not have an Accessible parent
         */
        public Accessible getAccessibleParent() {
            if (accessibleParent != null) {
                return accessibleParent;
            } else {
                MenuContainer parent = MenuComponent.this.getParent();
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
            return MenuComponent.this.getAccessibleIndexInParent();
        }

        /**
         * Returns the number of accessible children in the object.  If all
         * of the children of this object implement Accessible, than this
         * method should return the number of children of this object.
         *
         * @return the number of accessible children in the object.
         */
        public int getAccessibleChildrenCount() {
            return 0; // MenuComponents don't have children
        }

        /**
         * Return the nth Accessible child of the object.
         *
         * @param i zero-based index of child
         * @return the nth Accessible child of the object
         */
        public Accessible getAccessibleChild(int i) {
            return null; // MenuComponents don't have children
        }

        /**
         * Return the locale of this object.
         *
         * @return the locale of this object
         */
        public java.util.Locale getLocale() {
            MenuContainer parent = MenuComponent.this.getParent();
            if (parent instanceof Component)
                return ((Component)parent).getLocale();
            else
                return java.util.Locale.getDefault();
        }

        /**
         * Get the AccessibleComponent associated with this object if one
         * exists.  Otherwise return null.
	 *
	 * @return the component
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
            return null; // Not supported for MenuComponents
        }

        /**
         * Set the background color of this object.
         * (For transparency, see <code>isOpaque</code>.)
         *
         * @param c the new Color for the background
         * @see Component#isOpaque
         */
        public void setBackground(Color c) {
            // Not supported for MenuComponents
        }

        /**
         * Get the foreground color of this object.
         *
         * @return the foreground color, if supported, of the object;
         * otherwise, null
         */
        public Color getForeground() {
            return null; // Not supported for MenuComponents
        }

        /**
         * Set the foreground color of this object.
         *
         * @param c the new Color for the foreground
         */
        public void setForeground(Color c) {
            // Not supported for MenuComponents
        }

        /**
         * Get the Cursor of this object.
         *
         * @return the Cursor, if supported, of the object; otherwise, null
         */
        public Cursor getCursor() {
            return null; // Not supported for MenuComponents
        }

        /**
         * Set the Cursor of this object.
         *
         * @param c the new Cursor for the object
         */
        public void setCursor(Cursor cursor) {
            // Not supported for MenuComponents
        }

        /**
         * Get the Font of this object.
         *
         * @return the Font,if supported, for the object; otherwise, null
         */
        public Font getFont() {
            return MenuComponent.this.getFont();
        }

        /**
         * Set the Font of this object.
         *
         * @param f the new Font for the object
         */
        public void setFont(Font f) {
            MenuComponent.this.setFont(f);
        }

        /**
         * Get the FontMetrics of this object.
         *
         * @param f the Font
         * @return the FontMetrics, if supported, the object; otherwise, null
         * @see #getFont
         */
        public FontMetrics getFontMetrics(Font f) {
            return null; // Not supported for MenuComponents
        }

        /**
         * Determine if the object is enabled.
         *
         * @return true if object is enabled; otherwise, false
         */
        public boolean isEnabled() {
            return true; // Not supported for MenuComponents
        }

        /**
         * Set the enabled state of the object.
         *
         * @param b if true, enables this object; otherwise, disables it
         */
        public void setEnabled(boolean b) {
            // Not supported for MenuComponents
        }

        /**
         * Determine if the object is visible.  Note: this means that the
         * object intends to be visible; however, it may not in fact be
         * showing on the screen because one of the objects that this object
         * is contained by is not visible.  To determine if an object is
         * showing on the screen, use isShowing().
         *
         * @return true if object is visible; otherwise, false
         */
        public boolean isVisible() {
            return true; // Not supported for MenuComponents
        }

        /**
         * Set the visible state of the object.
         *
         * @param b if true, shows this object; otherwise, hides it
         */
        public void setVisible(boolean b) {
            // Not supported for MenuComponents
        }

        /**
         * Determine if the object is showing.  This is determined by checking
         * the visibility of the object and ancestors of the object.  Note:
         * this will return true even if the object is obscured by another
         * (for example, it happens to be underneath a menu that was pulled
         * down).
         *
         * @return true if object is showing; otherwise, false
         */
        public boolean isShowing() {
            return true; // Not supported for MenuComponents
        }

        /**
         * Checks whether the specified point is within this object's bounds,
         * where the point's x and y coordinates are defined to be relative to
         * the coordinate system of the object.
         *
         * @param p the Point relative to the coordinate system of the object
         * @return true if object contains Point; otherwise false
         */
        public boolean contains(Point p) {
            return false; // Not supported for MenuComponents
        }

        /**
         * Returns the location of the object on the screen.
         *
         * @return location of object on screen -- can be null if this object
         * is not on the screen
         */
        public Point getLocationOnScreen() {
            return null; // Not supported for MenuComponents
        }

        /**
         * Gets the location of the object relative to the parent in the form
         * of a point specifying the object's top-left corner in the screen's
         * coordinate space.
         *
         * @return An instance of Point representing the top-left corner of
         * the object's bounds in the coordinate space of the screen; null if
         * this object or its parent are not on the screen
         */
        public Point getLocation() {
            return null; // Not supported for MenuComponents
        }

        /**
         * Sets the location of the object relative to the parent.
         */
        public void setLocation(Point p) {
            // Not supported for MenuComponents
        }

        /**
         * Gets the bounds of this object in the form of a Rectangle object.
         * The bounds specify this object's width, height, and location
         * relative to its parent.
         *
         * @return A rectangle indicating this component's bounds; null if
         * this object is not on the screen.
         */
        public Rectangle getBounds() {
            return null; // Not supported for MenuComponents
        }

        /**
         * Sets the bounds of this object in the form of a Rectangle object.
         * The bounds specify this object's width, height, and location
         * relative to its parent.
         *
         * @param A rectangle indicating this component's bounds
         */
        public void setBounds(Rectangle r) {
            // Not supported for MenuComponents
        }

        /**
         * Returns the size of this object in the form of a Dimension object.
         * The height field of the Dimension object contains this object's
         * height, and the width field of the Dimension object contains this
         * object's width.
         *
         * @return A Dimension object that indicates the size of this
         *         component; null if this object is not on the screen
         */
        public Dimension getSize() {
            return null; // Not supported for MenuComponents
        }

        /**
         * Resizes this object..
         *
         * @param d - The dimension specifying the new size of the object.
         */
        public void setSize(Dimension d) {
            // Not supported for MenuComponents
        }

        /**
         * Returns the Accessible child, if one exists, contained at the local
         * coordinate Point.
         *
         * @param p The point defining the top-left corner of the Accessible,
         * given in the coordinate space of the object's parent.
         * @return the Accessible, if it exists, at the specified location;
         * else null
         */
        public Accessible getAccessibleAt(Point p) {
            return null; // MenuComponents don't have children
        }

        /**
         * Returns whether this object can accept focus or not.
         *
         * @return true if object can accept focus; otherwise false
         */
        public boolean isFocusTraversable() {
            return true; // Not supported for MenuComponents
        }

        /**
         * Requests focus for this object.
         */
        public void requestFocus() {
            // Not supported for MenuComponents
        }

        /**
         * Adds the specified focus listener to receive focus events from this
         * component.
         *
         * @param l the focus listener
         */
        public void addFocusListener(java.awt.event.FocusListener l) {
            // Not supported for MenuComponents
        }

        /**
         * Removes the specified focus listener so it no longer receives focus
         * events from this component.
         *
         * @param l the focus listener
         */
        public void removeFocusListener(java.awt.event.FocusListener l) {
            // Not supported for MenuComponents
        }

	// AccessibleSelection methods
	//

	/**
	 * Returns the number of Accessible children currently selected.
	 * If no children are selected, the return value will be 0.
	 *
	 * @return the number of items currently selected.
	 */
	 public int getAccessibleSelectionCount() {
	     return 0;	//  To be fully implemented in a future release
	 }

	/**
	 * Returns an Accessible representing the specified selected child
	 * in the object.  If there isn't a selection, or there are
	 * fewer children selected than the integer passed in, the return
	 * value will be null.
	 * <p>Note that the index represents the i-th selected child, which
	 * is different from the i-th child.
	 *
	 * @param i the zero-based index of selected children
	 * @return the i-th selected child
	 * @see #getAccessibleSelectionCount
	 */
	 public Accessible getAccessibleSelection(int i) {
	     return null;  //  To be fully implemented in a future release
	 }

	/**
	 * Determines if the current child of this object is selected.
	 *
	 * @return true if the current child of this object is selected; 
	 * else false.
	 * @param i the zero-based index of the child in this Accessible object.
	 * @see AccessibleContext#getAccessibleChild
	 */
	 public boolean isAccessibleChildSelected(int i) {
	     return false;  //  To be fully implemented in a future release
	 }

	/**
	 * Adds the specified Accessible child of the object to the object's
	 * selection.  If the object supports multiple selections,
	 * the specified child is added to any existing selection, otherwise
	 * it replaces any existing selection in the object.  If the
	 * specified child is already selected, this method has no effect.
	 *
	 * @param i the zero-based index of the child
	 * @see AccessibleContext#getAccessibleChild
	 */
	 public void addAccessibleSelection(int i) {
	       //  To be fully implemented in a future release
	 }

	/**
	 * Removes the specified child of the object from the object's
	 * selection.  If the specified item isn't currently selected, this
	 * method has no effect.
	 *
	 * @param i the zero-based index of the child
	 * @see AccessibleContext#getAccessibleChild
	 */
	 public void removeAccessibleSelection(int i) {
	       //  To be fully implemented in a future release
	 }

	/**
	 * Clears the selection in the object, so that no children in the
	 * object are selected.
	 */
	 public void clearAccessibleSelection() {
	       //  To be fully implemented in a future release
	 }

	/**
	 * Causes every child of the object to be selected
	 * if the object supports multiple selections.
	 */
	 public void selectAllAccessibleSelection() {
	       //  To be fully implemented in a future release
	 }

    } // inner class AccessibleAWTComponent

    /**
     * Get the index of this object in its accessible parent.
     *
     * @return -1 of this object does not have an accessible parent.
     * Otherwise, the index of the child in its accessible parent.
     */
    int getAccessibleIndexInParent() {
        return -1; // Overridden in subclasses
    }

    /**
     * Get the state of this object.
     *
     * @return an instance of AccessibleStateSet containing the current state
     * set of the object
     * @see AccessibleState
     */
    AccessibleStateSet getAccessibleStateSet() {
        AccessibleStateSet states = new AccessibleStateSet();
        return states;
    }

}
