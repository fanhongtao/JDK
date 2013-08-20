/*
 * @(#)NotificationFilter.java	4.17 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management; 


/**
 * To be implemented by a any class acting as a notification filter.
 * It allows a registered notification listener to filter the notifications of interest.
 *
 * @since 1.5
 */
public interface NotificationFilter extends java.io.Serializable { 

    /**
     * Invoked before sending the specified notification to the listener.
     *   
     * @param notification The notification to be sent.
     * @return <CODE>true</CODE> if the notification has to be sent to the listener, <CODE>false</CODE> otherwise.
     */  
    public boolean isNotificationEnabled(Notification notification);  
}
