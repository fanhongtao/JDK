/*
 * @(#)EventListenerList.java	1.22 98/08/28
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package javax.swing.event;

import java.io.*;
import java.util.*;

/**
 * A class which holds a list of EventListeners.  A single instance
 * can be used to hold all listeners (of all types) for the instance
 * using the lsit.  It is the responsiblity of the class using the
 * EventListenerList to provide type-safe API (preferably conforming
 * to the JavaBeans spec) and methods which dispatch event notification
 * methods to appropriate Event Listeners on the list.
 * 
 * The main benefits which this class provides are that it is relatively
 * cheap in the case of no listeners, and provides serialization for 
 * eventlistener lists in a single place, as well as a degree of MT safety
 * (when used correctly).
 *
 * Usage example:
 *    Say one is defining a class which sends out FooEvents, and wantds
 * to allow users of the class to register FooListeners and receive 
 * notification when FooEvents occur.  The following should be added
 * to the class definition:
   <pre>
   EventListenerList listenrList = new EventListnerList();
   FooEvent fooEvent = null;

   public void addFooListener(FooListener l) {
       listenerList.add(FooListener.class, l);
   }

   public void removeFooListener(FooListener l) {
       listenerList.remove(FooListener.class, l);
   }

 
    // Notify all listeners that have registered interest for
    // notification on this event type.  The event instance 
    // is lazily created using the parameters passed into 
    // the fire method.

    protected void firefooXXX() {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==FooListener.class) {
		// Lazily create the event:
		if (fooEvent == null)
		    fooEvent = new FooEvent(this);
		((FooListener)listeners[i+1]).fooXXX(fooEvent);
	    }	       
	}
    }	
   </pre>
 * foo should be changed to the appropriate name, and Method to the
 * appropriate method name (one fire method should exist for each
 * notification method in the FooListener interface).
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.22 08/28/98
 * @author Georges Saab
 * @author Hans Muller
 * @author James Gosling
 */
public class EventListenerList implements Serializable {
    /* A null array to be shared by all empty listener lists*/
    private final static Object[] NULL_ARRAY = new Object[0];
    /* The list of ListenerType - Listener pairs */
    protected transient Object[] listenerList = NULL_ARRAY;

    /**
     * This passes back the event listener list as an array
     * of ListenerType - listener pairs.  Note that for 
     * performance reasons, this implementation passes back 
     * the actual data structure in which the listner data
     * is stored internally!  
     * This method is guaranteed to pass back a non-null
     * array, so that no null-checking is required in 
     * fire methods.  A zero-length array of Object should
     * be returned if there are currently no listeners.
     * 
     * WARNING!!! Absolutely NO modification of
     * the data contained in this array should be made -- if
     * any such manipulation is necessary, it should be done
     * on a copy of the array returned rather than the array 
     * itself.
     */
    public Object[] getListenerList() {
	return listenerList;
    }

    /**
     * Return the total number of listeners for this listenerlist
     */
    public int getListenerCount() {
	return listenerList.length/2;
    }

    /**
     * Return the total number of listeners of the supplied type 
     * for this listenerlist.
     */
    public int getListenerCount(Class t) {
	int count = 0;
	Object[] lList = listenerList;
	for (int i = 0; i < lList.length; i+=2) {
	    if (t == (Class)lList[i])
		count++;
	}
	return count;
    }
    /**
     * Add the listener as a listener of the specified type.
     * @param t the type of the listener to be added
     * @param l the listener to be added
     */
    public synchronized void add(Class t, EventListener l) {
	if (!t.isInstance(l)) {
	    throw new IllegalArgumentException("Listener " + l +
					 " is not of type " + t);
	}
	if (l ==null) {
	    throw new IllegalArgumentException("Listener " + l +
					 " is null");
	}
	if (listenerList == NULL_ARRAY) {
	    // if this is the first listener added, 
	    // initialize the lists
	    listenerList = new Object[] { t, l };
	} else {
	    // Otherwise copy the array and add the new listener
	    int i = listenerList.length;
	    Object[] tmp = new Object[i+2];
	    System.arraycopy(listenerList, 0, tmp, 0, i);

	    tmp[i] = t;
	    tmp[i+1] = l;

	    listenerList = tmp;
	}
    }

    /**
     * Remove the listener as a listener of the specified type.
     * @param t the type of the listener to be removed
     * @param l the listener to be removed
     */
    public synchronized void remove(Class t, EventListener l) {
	if (!t.isInstance(l)) {
	    throw new IllegalArgumentException("Listener " + l +
					 " is not of type " + t);
	}
	if (l ==null) {
	    throw new IllegalArgumentException("Listener " + l +
					 " is null");
	}

	// Is l on the list?
	int index = -1;
	for (int i = listenerList.length-2; i>=0; i-=2) {
	    if ((listenerList[i]==t) && (listenerList[i+1].equals(l) == true)) {
		index = i;
		break;
	    }
	}
	
	// If so,  remove it
	if (index != -1) {
	    Object[] tmp = new Object[listenerList.length-2];
	    // Copy the list up to index
	    System.arraycopy(listenerList, 0, tmp, 0, index);
	    // Copy from two past the index, up to
	    // the end of tmp (which is two elements
	    // shorter than the old list)
	    if (index < tmp.length)
		System.arraycopy(listenerList, index+2, tmp, index, 
				 tmp.length - index);
	    // set the listener array to the new array or null
	    listenerList = (tmp.length == 0) ? NULL_ARRAY : tmp;
	    }
    }

    // Serialization support.  
    private void writeObject(ObjectOutputStream s) throws IOException {
	Object[] lList = listenerList;
	s.defaultWriteObject();
	
	// Save the non-null event listeners:
	for (int i = 0; i < lList.length; i+=2) {
	    Class t = (Class)lList[i];
	    EventListener l = (EventListener)lList[i+1];
	    if ((l!=null) && (l instanceof Serializable)) {
		s.writeObject(t.getName());
		s.writeObject(l);
	    }
	}
	
	s.writeObject(null);
    }

    private void readObject(ObjectInputStream s) 
	throws IOException, ClassNotFoundException {
        listenerList = NULL_ARRAY;
	s.defaultReadObject();
	Object listenerTypeOrNull;
	
	while (null != (listenerTypeOrNull = s.readObject())) {
	    EventListener l = (EventListener)s.readObject();
	    add(Class.forName((String)listenerTypeOrNull), l);
	}	    
    }

    /**
     * Return a string representation of the EventListenerList.
     */
    public String toString() {
	Object[] lList = listenerList;
	String s = "EventListenerList: ";
	s += lList.length/2 + " listeners: ";
	for (int i = 0 ; i <= lList.length-2 ; i+=2) {
	    s += " type " + ((Class)lList[i]).getName();
	    s += " listener " + lList[i+1];
	}
	return s;
    }
}
