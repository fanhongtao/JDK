/*
 * @(#)ArrayNotificationBuffer.java	1.21 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.jmx.remote.internal;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

import javax.management.InstanceNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.management.MBeanServer;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationFilter;
import javax.management.NotificationFilterSupport;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.QueryEval;
import javax.management.QueryExp;

import javax.management.remote.NotificationResult;
import javax.management.remote.TargetedNotification;

import com.sun.jmx.remote.util.EnvHelp;
import com.sun.jmx.remote.util.ClassLogger;

/** A circular buffer of notifications received from an MBean server. */
public class ArrayNotificationBuffer implements NotificationBuffer {
    
    public static final int DEFAULT_BUFFER_SIZE = 1000;
    public static final String BUFFER_SIZE_PROPERTY =
	"jmx.remote.x.buffer.size";
    
    private boolean disposed = false;
    
    // FACTORY STUFF, INCLUDING SHARING
    
    private static final
	HashMap/*<MBeanServer,ArrayNotificationBuffer>*/ mbsToBuffer =
	new HashMap(1);
    private final Collection/*<ShareBuffer>*/ sharers = new HashSet(1);

    public static synchronized NotificationBuffer
	    getNotificationBuffer(MBeanServer mbs, Map env) {
	
	//Find out queue size
	int defaultQueueSize = DEFAULT_BUFFER_SIZE;

	try {
	    String s = (String)
		AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
			    return System.getProperty(BUFFER_SIZE_PROPERTY);
			}
		    });
	    if (s != null)
		    defaultQueueSize = Integer.parseInt(s);
	} catch (RuntimeException e) {
	    logger.warning("ServerNotifForwarder", 
			   "Can't use System property "+
			   BUFFER_SIZE_PROPERTY+ ": " + e);
	    logger.debug("ServerNotifForwarder", e);
	}	    
	
	int queueSize = defaultQueueSize;

	try {
	    queueSize = (int)
		EnvHelp.getIntegerAttribute(env,BUFFER_SIZE_PROPERTY,
					    defaultQueueSize,0,
					    Integer.MAX_VALUE);
	} catch (RuntimeException e) {
	    logger.warning("ServerNotifForwarder", 
			   "Can't determine queuesize (using default): "+
			   e);
	    logger.debug("ServerNotifForwarder", e);
	}
	
	ArrayNotificationBuffer buf =
	    (ArrayNotificationBuffer) mbsToBuffer.get(mbs);
	if (buf == null) {
	    buf = new ArrayNotificationBuffer(mbs, queueSize);
	    mbsToBuffer.put(mbs, buf);
	}
	return buf.new ShareBuffer(queueSize);
    }
    
    public static synchronized void removeNotificationBuffer(MBeanServer mbs){
	mbsToBuffer.remove(mbs);
    }
    
    synchronized void addSharer(ShareBuffer sharer) {
	if (sharer.getSize() > queueSize)
	    resize(sharer.getSize());
	sharers.add(sharer);
    }

    synchronized void removeSharer(ShareBuffer sharer) {
	sharers.remove(sharer);
	if (sharers.isEmpty())
	    dispose();
	else {
	    int max = 0;
	    for (Iterator it = sharers.iterator(); it.hasNext(); ) {
		ShareBuffer buf = (ShareBuffer) it.next();
		int bufsize = buf.getSize();
		if (bufsize > max)
		    max = bufsize;
	    }
	    if (max < queueSize)
		resize(max);
	}
    }

    private void resize(int newSize) {
	if (newSize == queueSize)
	    return;
	while (queue.size() > newSize)
	    dropNotification();
	queue.resize(newSize);
	queueSize = newSize;
    }

    private class ShareBuffer implements NotificationBuffer {
	ShareBuffer(int size) {
	    this.size = size;
	    addSharer(this);
	}

	public NotificationResult
	    fetchNotifications(Set/*<ListenerInfo>*/ listeners,
			       long startSequenceNumber,
			       long timeout,
			       int maxNotifications)
		throws InterruptedException {
	    NotificationBuffer buf = ArrayNotificationBuffer.this;
	    return buf.fetchNotifications(listeners, startSequenceNumber,
					  timeout, maxNotifications);
	}

	public void dispose() {
	    ArrayNotificationBuffer.this.removeSharer(this);
	}

	int getSize() {
	    return size;
	}

	private final int size;
    }


    // ARRAYNOTIFICATIONBUFFER IMPLEMENTATION

    private ArrayNotificationBuffer(MBeanServer mbs, int queueSize) {
        if (logger.traceOn())
            logger.trace("Constructor", "queueSize=" + queueSize);

        if (mbs == null || queueSize < 1)
            throw new IllegalArgumentException("Bad args");

        this.mBeanServer = mbs;
        this.queueSize = queueSize;
        this.queue = new ArrayQueue(queueSize);
        this.earliestSequenceNumber = System.currentTimeMillis();
        this.nextSequenceNumber = this.earliestSequenceNumber;

        createListeners();

        logger.trace("Constructor", "ends");
    }

    private synchronized boolean isDisposed() {
	return disposed;
    }

    public void dispose() {
        logger.trace("dispose", "starts");

	synchronized(this) {
	    removeNotificationBuffer(mBeanServer);
	    disposed = true;
	    //Notify potential waiting fetchNotification call
	    notifyAll();
	}

        destroyListeners();
	
        logger.trace("dispose", "ends");
    }

    /**
     * <p>Fetch notifications that match the given listeners.</p>
     *
     * <p>The operation only considers notifications with a sequence
     * number at least <code>startSequenceNumber</code>.  It will take
     * no longer than <code>timeout</code>, and will return no more
     * than <code>maxNotifications</code> different notifications.</p>
     *
     * <p>If there are no notifications matching the criteria, the
     * operation will block until one arrives, subject to the
     * timeout.</p>
     *
     * @param listeners a Set of {@link ListenerInfo} that reflects
     * the filters to be applied to notifications.  Accesses to this
     * Set are synchronized on the Set object.  The Set is consulted
     * for selected notifications that are present when the method
     * starts, and for selected notifications that arrive while it is
     * executing.  The contents of the Set can be modified, with
     * appropriate synchronization, while the method is running.
     * @param startSequenceNumber the first sequence number to
     * consider.
     * @param timeout the maximum time to wait.  May be 0 to indicate
     * not to wait if there are no notifications.
     * @param maxNotifications the maximum number of notifications to
     * return.  May be 0 to indicate a wait for eligible notifications
     * that will return a usable <code>nextSequenceNumber</code>.  The
     * {@link TargetedNotification} array in the returned {@link
     * NotificationResult} may contain more than this number of
     * elements but will not contain more than this number of
     * different notifications.
     */
    public NotificationResult
        fetchNotifications(Set/*<ListenerInfo>*/ listeners,
                           long startSequenceNumber,
                           long timeout,
                           int maxNotifications)
            throws InterruptedException {

        logger.trace("fetchNotifications", "starts");

	if (startSequenceNumber < 0 || isDisposed()) {
	    synchronized(this) {
		return new NotificationResult(earliestSequenceNumber(), 
					      nextSequenceNumber(), 
					      new TargetedNotification[0]);
	    }
	}
	
        // Check arg validity
        if (listeners == null
            || startSequenceNumber < 0 || timeout < 0
            || maxNotifications < 0) {
            logger.trace("fetchNotifications", "Bad args");
            throw new IllegalArgumentException("Bad args to fetch");
        }

        if (logger.debugOn()) {
            logger.trace("fetchNotifications",
                  "listener-length=" + listeners.size() + "; startSeq=" +
                  startSequenceNumber + "; timeout=" + timeout +
                  "; max=" + maxNotifications);
        }

        if (startSequenceNumber > nextSequenceNumber()) {
            final String msg = "Start sequence number too big: " +
                startSequenceNumber + " > " + nextSequenceNumber();
            logger.trace("fetchNotifications", msg);
            throw new IllegalArgumentException(msg);
        }

        /* Determine the end time corresponding to the timeout value.
           Caller may legitimately supply Long.MAX_VALUE to indicate no
           timeout.  In that case the addition will overflow and produce
           a negative end time.  Set end time to Long.MAX_VALUE in that
           case.  We assume System.currentTimeMillis() is positive.  */
        long endTime = System.currentTimeMillis() + timeout;
        if (endTime < 0) // overflow
            endTime = Long.MAX_VALUE;

        if (logger.debugOn())
            logger.debug("fetchNotifications", "endTime=" + endTime);

        /* We set earliestSeq the first time through the loop.  If we
           set it here, notifications could be dropped before we
           started examining them, so earliestSeq might not correspond
           to the earliest notification we examined.  */
        long earliestSeq = -1;
        long nextSeq = startSequenceNumber;
        List/*<TargetedNotification>*/ notifs = new ArrayList();

        /* On exit from this loop, notifs, earliestSeq, and nextSeq must
           all be correct values for the returned NotificationResult.  */
        while (true) {
            logger.debug("fetchNotifications", "main loop starts");

            NamedNotification candidate;

            /* Get the next available notification regardless of filters,
               or wait for one to arrive if there is none.  */
            synchronized (this) {
		
                /* First time through.  The current earliestSequenceNumber
                   is the first one we could have examined.  */
                if (earliestSeq < 0) {
                    earliestSeq = earliestSequenceNumber();
                    if (logger.debugOn()) {
                        logger.debug("fetchNotifications",
                              "earliestSeq=" + earliestSeq);
                    }
                    if (nextSeq < earliestSeq) {
                        nextSeq = earliestSeq;
                        logger.debug("fetchNotifications", 
				     "nextSeq=earliestSeq");
                    }
                } else
                    earliestSeq = earliestSequenceNumber();

                /* If many notifications have been dropped since the
                   last time through, nextSeq could now be earlier
                   than the current earliest.  If so, notifications
                   may have been lost and we return now so the caller
                   can see this next time it calls.  */
                if (nextSeq < earliestSeq) {
                    logger.trace("fetchNotifications",
                          "nextSeq=" + nextSeq + " < " + "earliestSeq=" +
                          earliestSeq + " so may have lost notifs");
                    break;
                }

                if (nextSeq < nextSequenceNumber()) {
                    candidate = notificationAt(nextSeq);
                    if (logger.debugOn()) {
                        logger.debug("fetchNotifications", "candidate: " + 
				     candidate);
                        logger.debug("fetchNotifications", "nextSeq now " + 
				     nextSeq);
                    }
                } else {
                    /* nextSeq is the largest sequence number.  If we
                       already got notifications, return them now.
                       Otherwise wait for some to arrive, with
                       timeout.  */
                    if (notifs.size() > 0) {
                        logger.debug("fetchNotifications",
                              "no more notifs but have some so don't wait");
                        break;
                    }
                    long toWait = endTime - System.currentTimeMillis();
                    if (toWait <= 0) {
                        logger.debug("fetchNotifications", "timeout");
                        break;
                    }
		    
		    /* dispose called */
		    if (isDisposed()) {
			if (logger.debugOn())
			    logger.debug("fetchNotifications", 
					 "dispose callled, no wait");
			return new NotificationResult(earliestSequenceNumber(),
						  nextSequenceNumber(), 
						  new TargetedNotification[0]);
		    }
		    
		    if (logger.debugOn())
			logger.debug("fetchNotifications", 
				     "wait(" + toWait + ")");
		    wait(toWait);
		    
                    continue;
                }
            }
	    
            /* We have a candidate notification.  See if it matches
               our filters.  We do this outside the synchronized block
               so we don't hold up everyone accessing the buffer
               (including notification senders) while we evaluate
               potentially slow filters.  */
            ObjectName name = candidate.getObjectName();
            Notification notif = candidate.getNotification();
            List/*<TargetedNotification>*/ matchedNotifs = new ArrayList();
            logger.debug("fetchNotifications", 
			 "applying filters to candidate");
            synchronized (listeners) {
                for (Iterator it = listeners.iterator(); it.hasNext(); ) {
                    ListenerInfo li = (ListenerInfo) it.next();
                    ObjectName pattern = li.getObjectName();
                    NotificationFilter filter = li.getNotificationFilter();

                    if (logger.debugOn()) {
                        logger.debug("fetchNotifications",
                              "pattern=<" + pattern + ">; filter=" + filter);
                    }

                    if (pattern.apply(name)) {
                        logger.debug("fetchNotifications", "pattern matches");
                        if (filter == null
                            || filter.isNotificationEnabled(notif)) {
                            logger.debug("fetchNotifications", 
					 "filter matches");
                            Integer listenerID = li.getListenerID();
                            TargetedNotification tn =
                                new TargetedNotification(notif, listenerID);
                            matchedNotifs.add(tn);
                        }
                    }
                }
            }

            if (matchedNotifs.size() > 0) {
                /* We only check the max size now, so that our
                   returned nextSeq is as large as possible.  This
                   prevents the caller from thinking it missed
                   interesting notifications when in fact we knew they
                   weren't.  */
                if (maxNotifications <= 0) {
                    logger.debug("fetchNotifications", 
				 "reached maxNotifications");
                    break;
                }
                --maxNotifications;
                if (logger.debugOn())
                    logger.debug("fetchNotifications", "add: " + 
				 matchedNotifs);
                notifs.addAll(matchedNotifs);
            }

            ++nextSeq;
        } // end while

        /* Construct and return the result.  */
        int nnotifs = notifs.size();
        TargetedNotification[] resultNotifs =
            new TargetedNotification[nnotifs];
        notifs.toArray(resultNotifs);
        NotificationResult nr =
            new NotificationResult(earliestSeq, nextSeq, resultNotifs);
        if (logger.debugOn())
            logger.debug("fetchNotifications", nr.toString());
        logger.trace("fetchNotifications", "ends");

        return nr;
    }

    synchronized long earliestSequenceNumber() {
        return earliestSequenceNumber;
    }

    synchronized long nextSequenceNumber() {
        return nextSequenceNumber;
    }

    synchronized void addNotification(NamedNotification notif) {
        if (logger.traceOn())
            logger.trace("addNotification", notif.toString());

        while (queue.size() >= queueSize) {
	    dropNotification();
            if (logger.debugOn()) {
                logger.debug("addNotification",
                      "dropped oldest notif, earliestSeq=" +
                      earliestSequenceNumber);
            }
        }
        queue.add(notif);
        nextSequenceNumber++;
        if (logger.debugOn())
            logger.debug("addNotification", "nextSeq=" + nextSequenceNumber);
        notifyAll();
    }

    private void dropNotification() {
	queue.remove(0);
	earliestSequenceNumber++;
    }

    synchronized NamedNotification notificationAt(long seqNo) {
        long index = seqNo - earliestSequenceNumber;
        if (index < 0 || index > Integer.MAX_VALUE) {
            final String msg = "Bad sequence number: " + seqNo + " (earliest "
                + earliestSequenceNumber + ")";
            logger.trace("notificationAt", msg);
            throw new IllegalArgumentException(msg);
        }
        return (NamedNotification) queue.get((int) index);
    }

    private static class NamedNotification {
        NamedNotification(ObjectName sender, Notification notif) {
            this.sender = sender;
            this.notification = notif;
        }

        ObjectName getObjectName() {
            return sender;
        }

        Notification getNotification() {
            return notification;
        }

        public String toString() {
            return "NamedNotification(" + sender + ", " + notification + ")";
        }

        private final ObjectName sender;
        private final Notification notification;
    }

    /*
     * Add our listener to every NotificationBroadcaster MBean
     * currently in the MBean server and to every
     * NotificationBroadcaster later created.
     *
     * It would be really nice if we could just do
     * mbs.addNotificationListener(new ObjectName("*:*"), ...);
     * Definitely something for the next version of JMX.
     *
     * There is a nasty race condition that we can't easily solve.  We
     * first register for MBean-creation notifications so we can add
     * listeners to new MBeans, then we query the existing MBeans to
     * add listeners to them.  The problem is that a new MBean could
     * arrive after we register for creations but before the query has
     * completed.  Then we could see the MBean both in the query and
     * in an MBean-creation notification, and we would end up
     * registering our listener twice.
     *
     * To solve this problem, we have separate listener instances for
     * the MBeans found by the query and the MBeans found by the
     * creation notifications.  When we get a creation notification,
     * we add the listener for that, then we remove the listener for
     * queries if it is there.  This means that there's a very small
     * window of time when we could get the same notification from
     * both listeners.  However, for that to happen we would have to
     * hit TWO unlikely race conditions: an MBean created during our
     * initial query, and a notification from that MBean emitted while
     * we were adding the listener from the creation notification.
     * And the behaviour if we do hit those two conditions is that one
     * or more notifications are duplicated during a very small
     * period.
     */
    private synchronized void createListeners() {
        logger.debug("createListeners", "starts");
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
		public Object run() throws InstanceNotFoundException {
		    mBeanServer.addNotificationListener(delegateName,
							creationListener,
							creationFilter,
							null);
		    return null;
		}
	    });
            logger.debug("createListeners", "added creationListener");
        } catch (Exception pe) {
            final Exception e = extractException(pe);
            final String msg = "Can't add listener to MBean server delegate: ";
            RuntimeException re = new IllegalArgumentException(msg + e);
            EnvHelp.initCause(re, e);
            logger.fine("createListeners", msg + e);
            logger.debug("createListeners", e);
            throw re;
        }

        Set names;
        try {
            names = (Set)
		AccessController.doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        return mBeanServer.queryNames(null, broadcasterQuery);
                    }
                });
        } catch (RuntimeException e) {
            logger.fine("createListeners", "Failed to query names: " + e);
	    logger.debug("createListeners", e);
            throw e;
        }
        for (Iterator it = names.iterator(); it.hasNext(); ) {
            ObjectName name = (ObjectName) it.next();
            addBufferListener(name, queryBufferListener);
        }
        logger.debug("createListeners", "ends");
    }

    private void addBufferListener(final ObjectName name,
                                final NotificationListener bufferListener) {

        if (logger.debugOn())
            logger.debug("addBufferListener", ""+name);
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
		public Object run() throws InstanceNotFoundException {
		    mBeanServer.addNotificationListener(name,
							bufferListener,
							null,
							name);
		    return null;
		}
	    });
        } catch (Exception e) {
            logger.trace("addBufferListener", extractException(e));
            /* This can happen if the MBean was unregistered just
               after the query.  Or user NotificationBroadcaster might
               throw unexpected exception.  */
        }
    }

    private synchronized void createdNotification(MBeanServerNotification n) {
        if (destroyed) {
            logger.trace("createNotification", 
			 "NotificationBuffer was destroyed");
            return;
        }

        final String shouldEqual =
            MBeanServerNotification.REGISTRATION_NOTIFICATION;
        if (!n.getType().equals(shouldEqual)) {
            logger.warning("createNotification", "bad type: " + n.getType());
            return;
        }

        final ObjectName name = n.getMBeanName();
        if (logger.debugOn())
            logger.debug("createdNotification", "for: " + name);
        try {
            Boolean instanceOf = (Boolean)
                AccessController.doPrivileged(new PrivilegedExceptionAction() {
		    public Object run() throws InstanceNotFoundException {
			return new Boolean(
			       mBeanServer.isInstanceOf(name,
							broadcasterClass));
		    }
		});
            if (!instanceOf.booleanValue()) {
                logger.debug("createdNotification", 
			     "not a NotificationBroadcaster");
                return;
            }
        } catch (Exception e) {
            logger.trace("createdNotification", extractException(e));
            /* Could happen if the MBean was immediately unregistered.  */
            return;
        }

	addBufferListener(name, creationBufferListener);

	try {
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
		public Object run() throws
		    InstanceNotFoundException, ListenerNotFoundException {
		    mBeanServer.removeNotificationListener(
						  name,
						  queryBufferListener);

		    return null;
		}
	    });
            logger.trace("createdNotification", 
			 "remove queryBufferListener worked!");
        } catch (PrivilegedActionException pe) {
            final Exception e = extractException(pe);
            if (e instanceof ListenerNotFoundException) {
                logger.debug("createdNotification",
                      "remove queryBufferListener got " +
                      "ListenerNotFoundException as expected");
                // Expected: see comment before createListeners()
            } else {
                logger.trace("createdNotification", e);
            }
        }
    }

    private class BufferListener implements NotificationListener {
	public void handleNotification(Notification notif, Object handback) {
	    if (logger.debugOn()) {
		logger.debug("BufferListener.handleNotification",
		      "notif=" + notif + "; handback=" + handback);
	    }
	    ObjectName name = (ObjectName) handback;
	    addNotification(new NamedNotification(name, notif));
	}
    }

    private final NotificationListener queryBufferListener =
	new BufferListener();
    private final NotificationListener creationBufferListener =
	new BufferListener();

    private static class BroadcasterQuery
            extends QueryEval implements QueryExp {
        public boolean apply(final ObjectName name) {
            final MBeanServer mbs = QueryEval.getMBeanServer();
            try {
                Boolean isBroadcaster = (Boolean)
                    AccessController.doPrivileged(
                        new PrivilegedExceptionAction() {
                                public Object run()
                                    throws InstanceNotFoundException {
                                    return new Boolean(
                                           mbs.isInstanceOf(name,
                                                            broadcasterClass));
                                }
                            });
                if (logger.debugOn())
                    logger.debug("BroadcasterQuery", name + " -> " + 
				 isBroadcaster);
                return isBroadcaster.booleanValue();
            } catch (PrivilegedActionException pe) {
                logger.debug("BroadcasterQuery", extractException(pe));
                return false;
            }
        }
    }
    private static final QueryExp broadcasterQuery = new BroadcasterQuery();

    private static final NotificationFilter creationFilter;
    static {
        NotificationFilterSupport nfs = new NotificationFilterSupport();
        nfs.enableType(MBeanServerNotification.REGISTRATION_NOTIFICATION);
        creationFilter = nfs;
    }

    private final NotificationListener creationListener =
	new NotificationListener() {
	    public void handleNotification(Notification notif,
					   Object handback) {
		logger.debug("creationListener", "handleNotification called");
		createdNotification((MBeanServerNotification) notif);
	    }
	};

    private synchronized void destroyListeners() {
        logger.debug("destroyListeners", "starts");
        destroyed = true;
        Set names = (Set) 
	    AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    return mBeanServer.queryNames(null, broadcasterQuery);
                }
            });
        for (Iterator it = names.iterator(); it.hasNext(); ) {
            final ObjectName name = (ObjectName) it.next();
            if (logger.debugOn())
                logger.debug("destroyListeners", 
			     "remove listener from " + name);

            // remove creationBufferListener or queryBufferListener
            for (int i = 0; i < 2; i++) {
		final boolean creation = (i == 0);
		final NotificationListener listener =
		    creation ? creationBufferListener : queryBufferListener;
		final String what =
		    (creation ?
		     "creationBufferListener" :
		     "queryBufferListener");
                try {
                    AccessController.doPrivileged(
                        new PrivilegedExceptionAction() {
			    public Object run()
				throws
				InstanceNotFoundException,
				ListenerNotFoundException {
				mBeanServer.removeNotificationListener(
							      name,
							      listener);
				return null;
			    }
			});
                    if (logger.debugOn()) {
                        logger.debug("destroyListeners", "removed " + what);
                    }
                } catch (PrivilegedActionException pe) {
                    final Exception e = extractException(pe);
                     if (e instanceof ListenerNotFoundException) {
                        if (logger.debugOn()) {
                            logger.debug("destroyListeners",
                                  "ListenerNotFoundException for " + what +
                                  " (normal)");
                        }
                    } else {
                        logger.trace("destroyListeners", e);
                    }
                }
            }
        }
        logger.debug("destroyListeners", "ends");
    }

    /**
     * Iterate until we extract the real exception
     * from a stack of PrivilegedActionExceptions.
     */
    private static Exception extractException(Exception e) {
        while (e instanceof PrivilegedActionException) {
            e = ((PrivilegedActionException)e).getException(); 
        }
        return e;
    }

    private static final ClassLogger logger =
	new ClassLogger("javax.management.remote.misc",
			"ArrayNotificationBuffer");

    private static final ObjectName delegateName;
    static {
        try {
            delegateName =
                ObjectName.getInstance("JMImplementation:" +
                                       "type=MBeanServerDelegate");
        } catch (MalformedObjectNameException e) {
            RuntimeException re =
                new RuntimeException("Can't create delegate name: " + e);
            EnvHelp.initCause(re, e);
            logger.error("<init>", "Can't create delegate name: " + e);
	    logger.debug("<init>",e);
            throw re;
        }
    }

    private final MBeanServer mBeanServer;
    private final ArrayQueue queue;
    private int queueSize;
    private long earliestSequenceNumber;
    private long nextSequenceNumber;
    private boolean destroyed;

    static final String broadcasterClass =
        NotificationBroadcaster.class.getName();
}
