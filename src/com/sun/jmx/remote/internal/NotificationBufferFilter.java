/*
 * @(#)NotificationBufferFilter.java	1.1 06/02/20
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.jmx.remote.internal;

import java.util.List;
import javax.management.Notification;
import javax.management.ObjectName;
import javax.management.remote.TargetedNotification;

public interface NotificationBufferFilter {
    /**
     * Add the given notification coming from the given MBean to the list
     * iff it matches this filter's rules.
     */
    public void apply(List<TargetedNotification> targetedNotifs,
            ObjectName source, Notification notif);
}
