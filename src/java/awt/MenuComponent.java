/*
 * @(#)MenuComponent.java	1.2 00/01/12
 *
 * Copyright 1995-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package java.awt;

import java.awt.peer.MenuComponentPeer;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import sun.awt.AppContext;
import sun.awt.SunToolkit;

/**
 * The abstract class <code>MenuComponent</code> is the superclass 
 * of all menu-related components. In this respect, the class
 * <code>MenuComponent</code> is analogous to the abstract superclass
 * <code>Component</code> for AWT components.
 * <p>
 * Menu components receive and process AWT events, just as components do,
 * through the method <code>processEvent</code>.
 *
 * @version 	1.43, 08/31/98
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
        return null; // For strict compliance with prior JDKs, a MenuComponent
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

	appContext = AppContext.getAppContext();
	SunToolkit.insertTargetMapping(this, appContext);
    }

    /**
     * Initialize JNI field and method IDs
     */
    private static native void initIDs();
}
