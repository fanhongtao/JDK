/*
 * @(#)ClientListenerInfo.java	1.2 04/01/26
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.jmx.remote.internal;

import javax.security.auth.Subject;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.NotificationFilter;
import javax.management.ObjectName;

public class ClientListenerInfo extends ListenerInfo {
    public ClientListenerInfo(Integer listenerID,
			      ObjectName name, 
			      NotificationListener listener, 
			      NotificationFilter filter, 
			      Object handback,
			      Subject delegationSubject) {
	super(listenerID, name, listener, filter, handback,
	      delegationSubject);
    }
    
    public boolean sameAs(ObjectName name,
			  NotificationListener listener,
			  NotificationFilter filter,
			  Object handback) {
	return ( getObjectName().equals(name) &&
		 getListener() == listener &&
		 getNotificationFilter() == filter &&
		 getHandback() == handback);
    }
    
    public boolean sameAs(ObjectName name, 
			  NotificationListener listener) {
	return ( getObjectName().equals(name) &&
		 getListener() == listener);
    }
    
    public boolean sameAs(ObjectName name) {
	return (getObjectName().equals(name));
    }
}
