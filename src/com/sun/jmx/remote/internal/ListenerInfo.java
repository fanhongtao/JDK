/*
 * @(#)ListenerInfo.java	1.4 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.jmx.remote.internal;

import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import javax.security.auth.Subject;


/**
 * <p>An identified listener.  A listener has an Integer id that is
 * unique per connector server.  It selects notifications based on the
 * ObjectName of the originator and an optional
 * NotificationFilter.</p>
 *
 * <p>Two ListenerInfo objects are considered equal if and only if
 * they have the same listenerId.  This means that ListenerInfo
 * objects can be stored in a Set or Map and retrieved using another
 * ListenerInfo with the same listenerId but arbitrary ObjectNme and
 * NotificationFilter values.</p>
 */
public class ListenerInfo {
    public ListenerInfo(Integer listenerID,
			ObjectName name,
			NotificationFilter filter) {
	this.listenerID = listenerID;
	this.name = name;
	this.filter = filter;
    }

    public ListenerInfo(Integer listenerID,
			ObjectName name,
			NotificationListener listener,
			NotificationFilter filter,
			Object handback,
			Subject delegationSubject) {
	this.listenerID = listenerID;
	this.name = name;
	this.listener = listener;
	this.filter = filter;
	this.handback = handback;
	this.delegationSubject = delegationSubject;
    }

    public boolean equals(Object o) {
	if (!(o instanceof ListenerInfo)) {
	    return false;
	}

	return listenerID.equals(((ListenerInfo)o).listenerID);
    }

    public int hashCode() {
	return listenerID.intValue();
    }

    public ObjectName getObjectName() {
	return name;
    }

    public Integer getListenerID() {
	return listenerID;
    }

    public NotificationFilter getNotificationFilter() {
	return filter;
    }

    public NotificationListener getListener() {
	return listener;
    }

    public Object getHandback() {
	return handback;
    }

    public Subject getDelegationSubject() {
	return delegationSubject;
    }

    private ObjectName name;
    private Integer listenerID;
    private NotificationFilter filter;

    private NotificationListener listener = null;
    private Object handback = null;
    private Subject delegationSubject = null;
}
