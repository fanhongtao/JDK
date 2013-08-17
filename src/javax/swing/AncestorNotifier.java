/*
 * @(#)AncestorNotifier.java	1.12 99/04/22
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


import javax.swing.event.*;
import java.awt.event.*;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import java.io.Serializable;


/**
 * @version 1.12 04/22/99
 * @author Dave Moore
 */

class AncestorNotifier implements ComponentListener, PropertyChangeListener, Serializable 
{
    Component firstInvisibleAncestor;
    EventListenerList listenerList = new EventListenerList();
    JComponent root;

    AncestorNotifier(JComponent root) {
	this.root = root;
       	addListeners(root, true);
    }

    void addAncestorListener(AncestorListener l) {
	listenerList.add(AncestorListener.class, l);
    }

    void removeAncestorListener(AncestorListener l) {
	listenerList.remove(AncestorListener.class, l);
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    protected void fireAncestorAdded(JComponent source, int id, Container ancestor, Container ancestorParent) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==AncestorListener.class) {
		// Lazily create the event:
		AncestorEvent ancestorEvent = 
		    new AncestorEvent(source, id, ancestor, ancestorParent);
		((AncestorListener)listeners[i+1]).ancestorAdded(ancestorEvent);
	    }	       
	}
    }	
    
    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    protected void fireAncestorRemoved(JComponent source, int id, Container ancestor, Container ancestorParent) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==AncestorListener.class) {
		// Lazily create the event:
		AncestorEvent ancestorEvent = 
		    new AncestorEvent(source, id, ancestor, ancestorParent);
		((AncestorListener)listeners[i+1]).ancestorRemoved(ancestorEvent);
	    }	       
	}
    }	
    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    protected void fireAncestorMoved(JComponent source, int id, Container ancestor, Container ancestorParent) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==AncestorListener.class) {
		// Lazily create the event:
		AncestorEvent ancestorEvent = 
		    new AncestorEvent(source, id, ancestor, ancestorParent);
		((AncestorListener)listeners[i+1]).ancestorMoved(ancestorEvent);
	    }	       
	}
    }	

    void removeAllListeners() {
	removeListeners(root);
    }

    void addListeners(Component ancestor, boolean addToFirst) {
	Component a;

	firstInvisibleAncestor = null;
	for (a = ancestor;
	     firstInvisibleAncestor == null;
	     a = a.getParent()) {
	    if (addToFirst || a != ancestor) {
		a.addComponentListener(this);

		if (a instanceof JComponent) {
		    JComponent jAncestor = (JComponent)a;

		    jAncestor.addPropertyChangeListener(this);
		}
	    }
	    if (!a.isVisible() || a.getParent() == null) {
		firstInvisibleAncestor = a;
	    }
	}
	if (firstInvisibleAncestor instanceof Window) {
	    firstInvisibleAncestor = null;
	}
    }

    void removeListeners(Component ancestor) {
	Component a;
	for (a = ancestor; a != null; a = a.getParent()) {
	    a.removeComponentListener(this);
	    if (a instanceof JComponent) {
		JComponent jAncestor = (JComponent)a;
		jAncestor.removePropertyChangeListener(this);
	    }
	    if (a == firstInvisibleAncestor) {
		break;
	    }
	}
    }

    public void componentResized(ComponentEvent e) {}

    public void componentMoved(ComponentEvent e) {
	Component source = e.getComponent();

	fireAncestorMoved(root, AncestorEvent.ANCESTOR_MOVED,
			  (Container)source, source.getParent());
    }

    public void componentShown(ComponentEvent e) {
	Component ancestor = e.getComponent();

	if (ancestor == firstInvisibleAncestor) {
	    addListeners(ancestor, false);
	    if (firstInvisibleAncestor == null) {
		fireAncestorAdded(root, AncestorEvent.ANCESTOR_ADDED,
				  (Container)ancestor, ancestor.getParent());
	    }
	}
    }
    
    public void componentHidden(ComponentEvent e) {
	Component ancestor = e.getComponent();
	boolean needsNotify = firstInvisibleAncestor == null;

	removeListeners(ancestor.getParent());
	firstInvisibleAncestor = ancestor;
	if (needsNotify) {
	    fireAncestorRemoved(root, AncestorEvent.ANCESTOR_REMOVED,
				(Container)ancestor, ancestor.getParent());
	}
    }

    public void propertyChange(PropertyChangeEvent evt) {
	String s = evt.getPropertyName();

	if (s!=null && (s.equals("parent") || s.equals("ancestor"))) {
	    JComponent component = (JComponent)evt.getSource();

	    if (evt.getNewValue() != null) {
		if (component == firstInvisibleAncestor) {
		    addListeners(component, false);
		    if (firstInvisibleAncestor == null) {
			fireAncestorAdded(root, AncestorEvent.ANCESTOR_ADDED,
					  component, component.getParent());
		    }
		}
	    } else {
		boolean needsNotify = firstInvisibleAncestor == null;
		Container oldParent = (Container)evt.getOldValue();

		removeListeners(oldParent);
		firstInvisibleAncestor = component;
		if (needsNotify) {
		    fireAncestorRemoved(root, AncestorEvent.ANCESTOR_REMOVED,
					component, oldParent);
		}
	    }
	}
    }
}
