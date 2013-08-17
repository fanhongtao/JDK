/*
 * @(#)MenuComponent.java	1.25 97/04/25
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

import java.awt.peer.MenuComponentPeer;
import java.awt.event.ActionEvent;

/**
 * The super class of all menu related components.
 *
 * @version 	1.25, 04/25/97
 * @author 	Arthur van Hoff
 */
public abstract class MenuComponent implements java.io.Serializable {
    transient MenuComponentPeer peer;
    transient MenuContainer parent;
    Font font;
    String name;

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
     * Gets the name of the menu component.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the component to the specified string.
     * @param name  the name of the component.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the parent container.
     */
    public MenuContainer getParent() {
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
     * Gets the font used for this MenuItem.
     * @return the font if one is used; null otherwise.
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

    /**
     * Sets the font to be used for this MenuItem to the specified font.
     * @param f the font to be set
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
	MenuComponentPeer p = (MenuComponentPeer)this.peer;
	if (p != null) {
	    this.peer = null;
	    p.dispose();
	}
    }

    /**
     * Posts the specified event to the menu.
     * @param evt the event which is to take place
     * @deprecated As of JDK version 1.1,
     * replaced by dispatchEvent(AWTEvent).
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
     * @param e the event
     */   
    protected void processEvent(AWTEvent e) {
    }

    /**
     * Returns the String parameter of this MenuComponent.
     */
    protected String paramString() {
	return (name != null? name : "");
    }

    /**
     * Returns the String representation of this MenuComponent's values.
     */
    public String toString() {
	return getClass().getName() + "[" + paramString() + "]";
    }
}
