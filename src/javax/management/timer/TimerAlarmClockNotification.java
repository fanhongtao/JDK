/*
 * @(#)TimerAlarmClockNotification.java	1.21 06/03/29
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management.timer;

/**
 * <p>Definitions of the notifications sent by TimerAlarmClock
 * MBeans.</p>
 */
class TimerAlarmClockNotification
    extends javax.management.Notification { 

    /* Serial version */
    private static final long serialVersionUID = -4841061275673620641L;

    /*
     * ------------------------------------------
     *  CONSTRUCTORS
     * ------------------------------------------
     */
    
    /**
     * Constructor.
     *
     * @param source the source.
     */
    public TimerAlarmClockNotification(TimerAlarmClock source) {
        super("", source, 0);
    }
}
