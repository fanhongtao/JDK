/*
 * @(#)NotificationBuffer.java	1.5 05/08/31
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.jmx.remote.internal;

import java.util.Set;
import javax.management.remote.NotificationResult;
import javax.management.remote.TargetedNotification;

/** A buffer of notifications received from an MBean server. */
public interface NotificationBuffer {
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
        fetchNotifications(Set<ListenerInfo> listeners,
                           long startSequenceNumber,
                           long timeout,
                           int maxNotifications)
            throws InterruptedException;

    /**
     * <p>Discard this buffer.</p>
     */
    public void dispose();
}
