/*
 * @(#)TimerAlarmClockNotification.java	1.19 04/05/18
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management.timer;

/**
 * <p>Definitions of the notifications sent by TimerAlarmClock
 * MBeans.</p>
 *
 * @deprecated This class is of no use to user code.  It is retained
 * purely for compatibility reasons.
 *
 * @since 1.5
 * @since.unbundled JMX 1.1
 */
@Deprecated
public class TimerAlarmClockNotification
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
