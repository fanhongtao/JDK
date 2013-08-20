/*
 * @(#)NotificationBroadcasterSupport.java	1.56 04/09/08
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sun.jmx.trace.Trace;

/**
 * <p>Provides an implementation of {@link
 * javax.management.NotificationEmitter NotificationEmitter}
 * interface.  This can be used as the super class of an MBean that
 * sends notifications.</p>
 *
 * <p>It is not specified whether the notification dispatch model is
 * synchronous or asynchronous.  That is, when a thread calls {@link
 * #sendNotification sendNotification}, the {@link
 * NotificationListener#handleNotification
 * NotificationListener.handleNotification} method of each listener
 * may be called within that thread (a synchronous model) or within
 * some other thread (an asynchronous model).</p>
 *
 * <p>Applications should not depend on notification dispatch being
 * synchronous or being asynchronous.  Thus:</p>
 *
 * <ul>
 *
 * <li>Applications should not assume a synchronous model.  When the
 * {@link #sendNotification sendNotification} method returns, it is
 * not guaranteed that every listener's {@link
 * NotificationListener#handleNotification handleNotification} method
 * has been called.  It is not guaranteed either that a listener will
 * see notifications in the same order as they were generated.
 * Listeners that depend on order should use the sequence number of
 * notifications to determine their order (see {@link
 * Notification#getSequenceNumber()}).
 *
 * <li>Applications should not assume an asynchronous model.  If the
 * actions performed by a listener are potentially slow, the listener
 * should arrange for them to be performed in another thread, to avoid
 * holding up other listeners and the caller of {@link
 * #sendNotification sendNotification}.
 *
 * </ul>
 *
 * @since 1.5
 */
public class NotificationBroadcasterSupport implements NotificationEmitter { 
    
    /**
     * Adds a listener.
     *
     * @param listener The listener to receive notifications.
     * @param filter The filter object. If filter is null, no filtering will be performed before handling notifications.
     * @param handback An opaque object to be sent back to the listener when a notification is emitted. This object
     * cannot be used by the Notification broadcaster object. It should be resent unchanged with the notification
     * to the listener.
     *
     * @exception IllegalArgumentException thrown if the listener is null.
     *
     * @see #removeNotificationListener
     */
    public void addNotificationListener(NotificationListener listener,
					NotificationFilter filter,
					Object handback) {

        if (listener == null) {
            throw new IllegalArgumentException ("Listener can't be null") ;
        }

	/* Adding a new listener takes O(n) time where n is the number
	   of existing listeners.  If you have a very large number of
	   listeners performance could degrade.  That's a fairly
	   surprising configuration, and it is hard to avoid this
	   behaviour while still retaining the property that the
	   listenerList is not synchronized while notifications are
	   being sent through it.  If this becomes a problem, a
	   possible solution would be a multiple-readers single-writer
	   setup, so any number of sendNotification() calls could run
	   concurrently but they would exclude an
	   add/removeNotificationListener.  A simpler but less
	   efficient solution would be to clone the listener list
	   every time a notification is sent.  */
	synchronized (lock) {
	    List newList = new ArrayList(listenerList.size() + 1);
	    newList.addAll(listenerList);
	    newList.add(new ListenerInfo(listener, filter, handback));
	    listenerList = newList;
	}
    }

    public void removeNotificationListener(NotificationListener listener)
        throws ListenerNotFoundException {

	synchronized (lock) {
	    List newList = new ArrayList(listenerList);
	    /* We scan the list of listeners in reverse order because
	       in forward order we would have to repeat the loop with
	       the same index after a remove.  */
	    for (int i=newList.size()-1; i>=0; i--) {
		ListenerInfo li = (ListenerInfo)newList.get(i);

		if (li.listener == listener)
		    newList.remove(i);
	    }
	    if (newList.size() == listenerList.size())
		throw new ListenerNotFoundException("Listener not registered");
	    listenerList = newList;
	}
    }

    public void removeNotificationListener(NotificationListener listener,
					   NotificationFilter filter,
					   Object handback)
	    throws ListenerNotFoundException {

	boolean found = false;

	synchronized (lock) {
	    List newList = new ArrayList(listenerList);
	    final int size = newList.size();
	    for (int i = 0; i < size; i++) {
		ListenerInfo li = (ListenerInfo) newList.get(i);

		if (li.listener == listener) {
		    found = true;
		    if (li.filter == filter
			&& li.handback == handback) {
			newList.remove(i);
			listenerList = newList;
			return;
		    }
		}
	    }
	}

	if (found) {
	    /* We found this listener, but not with the given filter
	     * and handback.  A more informative exception message may
	     * make debugging easier.  */
	    throw new ListenerNotFoundException("Listener not registered " +
						"with this filter and " +
						"handback");
	} else {
	    throw new ListenerNotFoundException("Listener not registered");
	}
    }

    public MBeanNotificationInfo[] getNotificationInfo() {
        return new MBeanNotificationInfo[0];
    }


    /**
     * Sends a notification.
     *   
     * @param notification The notification to send.
     */
    public void sendNotification(Notification notification) {

	if (notification == null) {
	    return;
	}
        
	List currentList;
	synchronized (lock) {
	    currentList = listenerList;
	}

	final int size = currentList.size();
	for (int i = 0; i < size; i++) {
	    ListenerInfo li = (ListenerInfo) currentList.get(i);

	    if (li.filter == null
		|| li.filter.isNotificationEnabled(notification)) {
		try {
		    this.handleNotification(li.listener, notification,
					    li.handback);
		} catch (Exception e) {
		    trace("sendNotification",
			  "exception from listener: " + e);
		}
	    }
	}
    }

    /**
     * <p>This method is called by {@link #sendNotification
     * sendNotification} for each listener in order to send the
     * notification to that listener.  It can be overridden in
     * subclasses to change the behavior of notification delivery,
     * for instance to deliver the notification in a separate
     * thread.</p>
     *
     * <p>It is not guaranteed that this method is called by the same
     * thread as the one that called {@link #sendNotification
     * sendNotification}.</p>
     *
     * <p>The default implementation of this method is equivalent to
     * <pre>
     * listener.handleNotification(notif, handback);
     * </pre>
     *
     * @param listener the listener to which the notification is being
     * delivered.
     * @param notif the notification being delivered to the listener.
     * @param handback the handback object that was supplied when the
     * listener was added.
     *
     * @since.unbundled JMX 1.2
     */
    protected void handleNotification(NotificationListener listener,
				      Notification notif, Object handback) {
	listener.handleNotification(notif, handback);
    }

    // private stuff

    private static void trace(String method, String message) {
	if (Trace.isSelected(Trace.LEVEL_TRACE, Trace.INFO_MISC)) {
	    Trace.send(Trace.LEVEL_TRACE, Trace.INFO_MISC,
		       NotificationBroadcasterSupport.class.getName(),
		       method, message);
	}
    }

    private class ListenerInfo {
	public NotificationListener listener;
	NotificationFilter filter;
	Object handback;

	public ListenerInfo(NotificationListener listener,
			    NotificationFilter filter,
			    Object handback) {
	    this.listener = listener;
	    this.filter = filter;
	    this.handback = handback;
	}
    }

    /**
     * Current list of listeners, a List of ListenerInfo.  The object
     * referenced by this field is never modified.  Instead, the field
     * is set to a new object when a listener is added or removed,
     * within a synchronized(lock).  In this way, there is no need to
     * synchronize when traversing the list to send a notification to
     * the listeners in it.  That avoids potential deadlocks if the
     * listeners end up depending on other threads that are themselves
     * accessing this NotificationBroadcasterSupport.
     */
    private List listenerList = Collections.EMPTY_LIST;

    /**
     * We don't want to synchronize on "this", since a subclass might
     * use the "this" lock for its own purposes and we could get a
     * deadlock (bug 5093922).  We can't synchronize on listenerList
     * because when we want to change it we would be replacing the
     * object we are synchronizing on.  (In fact, it *might* be possible
     * to synchronize on listenerList provided the code verified after
     * getting the lock that the listenerList field still corresponds
     * to the object synchronized on.  This is the sort of thing that
     * might be all right with the new memory model.  But let's not
     * make life unnecessarily difficult for ourselves just to save
     * one field.
     * In a future version we will use CopyOnWriteArrayList instead,
     * since it does pretty much exactly what we want.  There are a
     * few tricky details related to the semantics of the two
     * removeNotificationListener operations, however.
     */
    private final Object lock = new Object();
}
