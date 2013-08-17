/*
 * @(#)Observable.java	1.20 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.util;

/**
 * This class represents an observable object, or "data"
 * in the model-view paradigm. It can be subclassed to represent an 
 * object that the application wants to have observed. 
 * <p>
 * An observable object can have one or more observers. After an 
 * observable instance changes, an application calling the 
 * <code>Observable</code>'s <code>notifyObservers</code> method  
 * causes all of its observers to be notified of the change by a call 
 * to their <code>update</code> method. 
 *
 * @author  Chris Warth
 * @version 1.20, 07/01/98
 * @see     java.util.Observable#notifyObservers()
 * @see     java.util.Observable#notifyObservers(java.lang.Object)
 * @see     java.util.Observer
 * @see     java.util.Observer#update(java.util.Observable, java.lang.Object)
 * @since   JDK1.0
 */
public class Observable {
    private boolean changed = false;
    private Vector obs;
    /* a temporary array buffer, used as a snapshot of the state of
     * current Observers.  We do notifications on this snapshot while
     * not under synchronization.
     */
    private Observer[] arr = new Observer[2];
    /** Construct an Observable with zero Observers */

    public Observable() {
	obs = new Vector();
    }

    /**
     * Adds an observer to the set of observers for this object. 
     *
     * @param   o   an observer to be added.
     * @since   JDK1.0
     */
    public synchronized void addObserver(Observer o) {
	if (!obs.contains(o)) {
	    obs.addElement(o);
	}
    }

    /**
     * Deletes an observer from the set of observers of this object. 
     *
     * @param   o   the observer to be deleted.
     * @since   JDK1.0
     */
    public synchronized void deleteObserver(Observer o) {
	    obs.removeElement(o);
    }

    /**
     * If this object has changed, as indicated by the 
     * <code>hasChanged</code> method, then notify all of its observers 
     * and then call the <code>clearChanged</code> method to 
     * indicate that this object has no longer changed. 
     * <p>
     * Each observer has its <code>update</code> method called with two
     * arguments: this observable object and <code>null</code>. 
     *
     * @see     java.util.Observable#clearChanged()
     * @see     java.util.Observable#hasChanged()
     * @see     java.util.Observer#update(java.util.Observable, java.lang.Object)
     * @since   JDK1.0
     */
    public void notifyObservers() {
	notifyObservers(null);
    }

    /**
     * If this object has changed, as indicated by the 
     * <code>hasChanged</code> method, then notify all of its observers 
     * and then call the <code>clearChanged</code> method to indicate 
     * that this object has no longer changed. 
     * <p>
     * Each observer has its <code>update</code> method called with two
     * arguments: this observable object and the <code>arg</code> argument.
     *
     * @param   arg   any object.
     * @see     java.util.Observable#clearChanged()
     * @see     java.util.Observable#hasChanged()
     * @see     java.util.Observer#update(java.util.Observable, java.lang.Object)
     * @since   JDK1.0
     */
    public void notifyObservers(Object arg) {

	int size=0;

	synchronized (this) {
	    /* We don't want the Observer doing callbacks into
	     * into arbitrary code while holding its own Monitor.
	     * The code where we extract each Observable from 
	     * the Vector and store the state of the Observer
	     * needs synchronization, but notifying observers
	     * does not (should not).  The worst result of any 
	     * potential race-condition here is that:
	     * 1) a newly-added Observer will miss a
	     *   notification in progress
	     * 2) a recently unregistered Observer will be
	     *   wrongly notified when it doesn't care
	     */
	    if (!hasChanged())
		return;
	    size = obs.size();
	    if (size > arr.length) {
		arr = new Observer[size];
	    }
	    obs.copyInto(arr);
	    clearChanged();
	}

	for (int i = size -1; i>=0; i--) {
	    if (arr[i] != null) {
		arr[i].update(this, arg);
	    }
	}
    }

    /**
     * Clears the observer list so that this object no longer has any observers.
     *
     * @since   JDK1.0
     */
    public synchronized void deleteObservers() {
	obs.removeAllElements();
    }

    /**
     * Indicates that this object has changed. 
     *
     * @since   JDK1.0
     */
    protected synchronized void setChanged() {
	changed = true;
    }

    /**
     * Indicates that this object has no longer changed, or that it has 
     * already notified all of its observers of its most recent change. 
     * This method is called automatically by the 
     * <code>notifyObservers</code> methods. 
     *
     * @see     java.util.Observable#notifyObservers()
     * @see     java.util.Observable#notifyObservers(java.lang.Object)
     * @since   JDK1.0
     */
    protected synchronized void clearChanged() {
	changed = false;
    }

    /**
     * Tests if this object has changed. 
     *
     * @return  <code>true</code> if the <code>setChanged</code> method
     *          has been called more recently than the <code>clearChanged</code>
     *          method on this object; <code>false</code> otherwise.
     * @see     java.util.Observable#clearChanged()
     * @see     java.util.Observable#setChanged()
     * @since   JDK1.0
     */
    public synchronized boolean hasChanged() {
	return changed;
    }

    /**
     * Returns the number of observers of this object.
     *
     * @return  the number of observers of this object.
     * @since   JDK1.0
     */
    public synchronized int countObservers() {
	return obs.size();
    }
}
