/*
 * @(#)Monitor.java	4.42 04/05/18
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management.monitor;

// java imports
//
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

// jmx imports
//
import javax.management.MBeanServer;
import javax.management.MBeanRegistration;
import javax.management.ObjectName;

import com.sun.jmx.trace.Trace;

/**
 * Defines the common part to all monitor MBeans.
 * A monitor MBean monitors values of an attribute common to a set of observed
 * MBeans. The observed attribute is monitored at intervals specified by the
 * granularity period. A gauge value (derived gauge) is derived from the values
 * of the observed attribute.
 *
 * @version     4.42     05/18/04
 * @author      Sun Microsystems, Inc
 *
 * @since 1.5
 */
public abstract class Monitor
  extends javax.management.NotificationBroadcasterSupport
  implements MonitorMBean, javax.management.MBeanRegistration
{
    /*
     * ------------------------------------------
     *  PRIVATE VARIABLES
     * ------------------------------------------
     */

    /**
     * List of MBeans to which the attribute to observe belongs.
     * <BR>The default value is set to null.
     */
    private List observedObjects = new ArrayList();

    /**
     * Attribute to observe.
     * <BR>The default value is set to null.
     */
    private String observedAttribute = null;

    /**
     * Monitor granularity period (in milliseconds).
     * <BR>The default value is set to 10 seconds.
     */
    private long granularityPeriod = 10000;


    /*
     * ------------------------------------------
     *  PROTECTED VARIABLES
     * ------------------------------------------
     */

    /**
     * The amount by which the capacity of the monitor arrays are
     * automatically incremented when their size becomes greater than
     * their capacity.
     */
    protected final static int capacityIncrement = 16;

    /**
     * The number of valid components in the vector of observed objects.
     *
     * @since.unbundled JMX 1.2
     */
    protected int elementCount = 0;

    /**
     * Monitor errors that have already been notified.
     * @deprecated equivalent to {@link #alreadyNotifieds}[0].
     */
    @Deprecated
    protected int alreadyNotified = 0;

    /**
     * <p>Selected monitor errors that have already been notified.</p>
     *
     * <p>Each element in this array corresponds to an observed object
     * in the vector.  It contains a bit mask of the flags {@link
     * #OBSERVED_OBJECT_ERROR_NOTIFIED} etc, indicating whether the
     * corresponding notification has already been sent for the MBean
     * being monitored.</p>
     *
     * @since.unbundled JMX 1.2
     */
    protected int alreadyNotifieds[] = new int[capacityIncrement];

    /**
     * Reference on the MBean server.  This reference is null when the
     * monitor MBean is not registered in an MBean server.  This
     * reference is initialized before the monitor MBean is registered
     * in the MBean server.
     * @see #preRegister(MBeanServer server, ObjectName name)
     */
    protected MBeanServer server = null;

    // Flags defining possible monitor errors.
    //

    /**
     * This flag is used to reset the {@link #alreadyNotifieds
     * alreadyNotifieds} monitor attribute.
     */
    protected static final int RESET_FLAGS_ALREADY_NOTIFIED             = 0;

    /**
     * Flag denoting that a notification has occurred after changing
     * the observed object.  This flag is used to check that the new
     * observed object is registered in the MBean server at the time
     * of the first notification.
     */
    protected static final int OBSERVED_OBJECT_ERROR_NOTIFIED           = 1;

    /**
     * Flag denoting that a notification has occurred after changing
     * the observed attribute.  This flag is used to check that the
     * new observed attribute belongs to the observed object at the
     * time of the first notification.
     */
    protected static final int OBSERVED_ATTRIBUTE_ERROR_NOTIFIED        = 2;

    /**
     * Flag denoting that a notification has occurred after changing
     * the observed object or the observed attribute.  This flag is
     * used to check that the observed attribute type is correct
     * (depending on the monitor in use) at the time of the first
     * notification.
     */
    protected static final int OBSERVED_ATTRIBUTE_TYPE_ERROR_NOTIFIED   = 4;

    /**
     * Flag denoting that a notification has occurred after changing
     * the observed object or the observed attribute.  This flag is
     * used to notify any exception (except the cases described above)
     * when trying to get the value of the observed attribute at the
     * time of the first notification.
     */
    protected static final int RUNTIME_ERROR_NOTIFIED                   = 8;

    /**
     * This field is retained for compatibility but should not be referenced.
     *
     * @deprecated No replacement.
     */
    @Deprecated
    protected String dbgTag = "Monitor";


    /*
     * ------------------------------------------
     *  PACKAGE VARIABLES
     * ------------------------------------------
     */

    /**
     * Monitor state.
     * The default value is set to <CODE>false</CODE>.
     */
    boolean isActive = false;

    /**
     * Monitor sequence number.
     * The default value is set to 0.
     */
    long sequenceNumber = 0;

    // TRACES & DEBUG
    //---------------

    static boolean isTraceOn() {
        return Trace.isSelected(Trace.LEVEL_TRACE, Trace.INFO_MONITOR);
    }

    static void trace(String clz, String func, String info) {
        Trace.send(Trace.LEVEL_TRACE, Trace.INFO_MONITOR, clz, func, info);
    }

    void trace(String func, String info) {
        trace(dbgTag, func, info);
    }

    static boolean isDebugOn() {
        return Trace.isSelected(Trace.LEVEL_DEBUG, Trace.INFO_MONITOR);
    }

    static void debug(String clz, String func, String info) {
        Trace.send(Trace.LEVEL_DEBUG, Trace.INFO_MONITOR, clz, func, info);
    }

    void debug(String func, String info) {
        debug(dbgTag, func, info);
    }

    /*
     * ------------------------------------------
     *  PUBLIC METHODS
     * ------------------------------------------
     */

    /**
     * Allows the monitor MBean to perform any operations it needs
     * before being registered in the MBean server.
     * <P>
     * Initializes the reference to the MBean server.
     *
     * @param server The MBean server in which the monitor MBean will
     * be registered.
     * @param name The object name of the monitor MBean.
     *
     * @return The name of the monitor MBean registered.
     *
     * @exception java.lang.Exception
     */
    public ObjectName preRegister(MBeanServer server, ObjectName name)
        throws java.lang.Exception {

        if (isTraceOn()) {
            trace("preRegister",
		  "initialize the reference on the MBean server");
        }

        this.server = server;
        return name;
    }

    /**
     * Allows the monitor MBean to perform any operations needed after
     * having been registered in the MBean server or after the
     * registration has failed.
     * <P>
     * Not used in this context.
     */
    public void postRegister (Boolean registrationDone) {
    }

    /**
     * Allows the monitor MBean to perform any operations it needs
     * before being unregistered by the MBean server.
     * <P>
     * Stops the monitor.
     *
     * @exception java.lang.Exception
     */
    public void preDeregister() throws java.lang.Exception {

        if (isTraceOn()) {
            trace("preDeregister", "stop the monitor");
        }

        // Stop the Monitor.
        //
        stop();
    }

    /**
     * Allows the monitor MBean to perform any operations needed after
     * having been unregistered by the MBean server.
     * <P>
     * Not used in this context.
     */
    public void postDeregister() {
    }

    /**
     * Starts the monitor.
     */
    public abstract void start();

    /**
     * Stops the monitor.
     */
    public abstract void stop();

    // GETTERS AND SETTERS
    //--------------------

    /**
     * Returns the object name of the first object in the set of observed
     * MBeans, or <code>null</code> if there is no such object.
     *
     * @return The object being observed.
     *
     * @see #setObservedObject(ObjectName)
     *
     * @deprecated As of JMX 1.2, replaced by {@link #getObservedObjects}
     */
    @Deprecated
    public ObjectName getObservedObject() {
	synchronized(this) {
	    if (observedObjects.isEmpty()) {
		return null;
	    } else {
        return (ObjectName)observedObjects.get(0);
    }
	}
    }

    /**
     * Removes all objects from the set of observed objects, and then adds the
     * specified object.
     *
     * @param object The object to observe.
     * @exception java.lang.IllegalArgumentException The specified
     * object is null.
     *
     * @see #getObservedObject()
     *
     * @deprecated As of JMX 1.2, replaced by {@link #addObservedObject}
     */
    @Deprecated
    public synchronized void setObservedObject(ObjectName object)
	    throws IllegalArgumentException {
	while (!observedObjects.isEmpty()) {
	    removeObservedObject((ObjectName)observedObjects.get(0));
	}

	addObservedObject(object);
    }

    /**
     * Adds the specified object in the set of observed MBeans, if this object
     * is not already present.
     *
     * @param object The object to observe.
     * @exception IllegalArgumentException The specified object is null.
     *
     * @since.unbundled JMX 1.2
     */
    public synchronized void addObservedObject(ObjectName object)
	    throws IllegalArgumentException {

        if (object == null) {
            throw new IllegalArgumentException("Null observed object");
        }

	// Check that the specified object is not already contained
	//
	if (observedObjects.contains(object)) {
	    return;
	}

	// Add the specified object in the list.
	//
	observedObjects.add(object);

	// Update alreadyNotified array.
	//
	int value = RESET_FLAGS_ALREADY_NOTIFIED;
	value &= ~(OBSERVED_OBJECT_ERROR_NOTIFIED |
		   OBSERVED_ATTRIBUTE_ERROR_NOTIFIED |
		   OBSERVED_ATTRIBUTE_TYPE_ERROR_NOTIFIED);
	if (alreadyNotifieds.length >= elementCount)
	    alreadyNotifieds = expandArray(alreadyNotifieds);
	alreadyNotifieds[elementCount] = value;

	updateDeprecatedAlreadyNotified();

	// Update other specific arrays.
	//
	insertSpecificElementAt(elementCount);

	// Update elementCount.
	//
	elementCount++;
    }

    /**
     * Removes the specified object from the set of observed MBeans.
     *
     * @param object The object to remove.
     *
     * @since.unbundled JMX 1.2
     */
    public void removeObservedObject(ObjectName object) {
	synchronized(this) {
            int index = observedObjects.indexOf(object);
	    if (index >= 0) {
		observedObjects.remove(index);

            // Update alreadyNotifieds array.
            //
            removeElementAt(alreadyNotifieds, index);
	    updateDeprecatedAlreadyNotified();

            // Update other specific arrays.
            //
            removeSpecificElementAt(index);

            // Update elementCount.
            //
            elementCount--;
        }
    }
    }

    /**
     * Tests whether the specified object is in the set of observed MBeans.
     *
     * @param object The object to check.
     * @return <CODE>true</CODE> if the specified object is present,
     * <CODE>false</CODE> otherwise.
     *
     * @since.unbundled JMX 1.2
     */
    public boolean containsObservedObject(ObjectName object) {
	synchronized(this) {
        return observedObjects.contains(object);
    }
    }

    /**
     * Returns an array containing the objects being observed.
     *
     * @return The objects being observed.
     *
     * @since.unbundled JMX 1.2
     */
    public ObjectName[] getObservedObjects() {
	ObjectName[] objects;
	synchronized(this) {
	    objects = new ObjectName[elementCount];
	    for (int i=0; i<elementCount; i++) {
          objects[i] = (ObjectName)observedObjects.get(i);
        }
	}
        return objects;
    }

    /**
     * Gets the attribute being observed.
     * <BR>The observed attribute is not initialized by default (set to null).
     *
     * @return The attribute being observed.
     *
     * @see #setObservedAttribute
     */
    public String getObservedAttribute() {
        return observedAttribute;
    }

    /**
     * Sets the attribute to observe.
     * <BR>The observed attribute is not initialized by default (set to null).
     *
     * @param attribute The attribute to observe.
     * @exception java.lang.IllegalArgumentException The specified
     * attribute is null.
     *
     * @see #getObservedAttribute
     */
    public void setObservedAttribute(String attribute)
	    throws IllegalArgumentException {

        if (attribute == null) {
            throw new IllegalArgumentException("Null observed attribute");
        }

        // Update alreadyNotified array.
        //
	synchronized(this) {
	    observedAttribute = attribute;

        for (int i = 0; i < elementCount; i++) {
	    resetAlreadyNotified(i,
				 OBSERVED_ATTRIBUTE_ERROR_NOTIFIED |
				 OBSERVED_ATTRIBUTE_TYPE_ERROR_NOTIFIED);
        }
    }
    }

    /**
     * Gets the granularity period (in milliseconds).
     * <BR>The default value of the granularity period is 10 seconds.
     *
     * @return The granularity period value.
     *
     * @see #setGranularityPeriod
     */
    public synchronized long getGranularityPeriod() {
        return granularityPeriod;
    }

    /**
     * Sets the granularity period (in milliseconds).
     * <BR>The default value of the granularity period is 10 seconds.
     *
     * @param period The granularity period value.
     * @exception java.lang.IllegalArgumentException The granularity
     * period is less than or equal to zero.
     *
     * @see #getGranularityPeriod
     */
    public synchronized void setGranularityPeriod(long period)
	    throws IllegalArgumentException {

        if (period <= 0) {
            throw new IllegalArgumentException("Nonpositive granularity " +
					       "period");
        }
        granularityPeriod = period;
    }

    /**
     * Tests whether the monitor MBean is active.  A monitor MBean is
     * marked active when the {@link #start start} method is called.
     * It becomes inactive when the {@link #stop stop} method is
     * called.
     *
     * @return <CODE>true</CODE> if the monitor MBean is active,
     * <CODE>false</CODE> otherwise.
     */
    /* This method must be synchronized so that the monitoring thread will
       correctly see modifications to the isActive variable.  See the various
       AlarmClock threads in the subclasses.  */
    public synchronized boolean isActive() {
        return isActive;
    }

    /*
     * ------------------------------------------
     *  PACKAGE METHODS
     * ------------------------------------------
     */

    /**
     * Gets the {@link ObjectName} of the object at the specified
     * index in the list of observed MBeans.
     * @return The observed object at the specified index.
     * @exception ArrayIndexOutOfBoundsException If the index is invalid.
     */
    synchronized ObjectName getObservedObject(int index)
	    throws ArrayIndexOutOfBoundsException {
        return (ObjectName)observedObjects.get(index);
    }

    /**
     * Update the deprecated {@link #alreadyNotified} field.
     */
    synchronized void updateDeprecatedAlreadyNotified() {
	if (elementCount > 0)
	    alreadyNotified = alreadyNotifieds[0];
	else
	    alreadyNotified = 0;
    }

    /**
     * Set the given bits in the given element of {@link #alreadyNotifieds}.
     * Ensure the deprecated {@link #alreadyNotified} field is updated
     * if appropriate.
     */
    synchronized void setAlreadyNotified(int index, int mask) {
	alreadyNotifieds[index] |= mask;
	if (index == 0)
	    updateDeprecatedAlreadyNotified();
    }

    /**
     * Reset the given bits in the given element of {@link #alreadyNotifieds}.
     * Ensure the deprecated {@link #alreadyNotified} field is updated
     * if appropriate.
     */
    synchronized void resetAlreadyNotified(int index, int mask) {
	alreadyNotifieds[index] &= ~mask;
	if (index == 0)
	    updateDeprecatedAlreadyNotified();
    }

    synchronized boolean alreadyNotified(int index, int mask) {
	return ((alreadyNotifieds[index] & mask) != 0);
    }

    /**
     * Reset all bits in the given element of {@link #alreadyNotifieds}.
     * Ensure the deprecated {@link #alreadyNotified} field is updated
     * if appropriate.
     */
    synchronized void resetAllAlreadyNotified(int index) {
	alreadyNotifieds[index] = 0;
	if (index == 0)
	    updateDeprecatedAlreadyNotified();
    }

    int[] expandArray(int[] array) {
	int[] newArray = new int[array.length + capacityIncrement];
	System.arraycopy(array, 0, newArray, 0, array.length);
	return newArray;
    }

    long[] expandArray(long[] array) {
	long[] newArray = new long[array.length + capacityIncrement];
	System.arraycopy(array, 0, newArray, 0, array.length);
	return newArray;
    }

    Number[] expandArray(Number[] array) {
	Number[] newArray = new Number[array.length + capacityIncrement];
	System.arraycopy(array, 0, newArray, 0, array.length);
	return newArray;
    }

    boolean[] expandArray(boolean[] array) {
	boolean[] newArray = new boolean[array.length + capacityIncrement];
	System.arraycopy(array, 0, newArray, 0, array.length);
	return newArray;
    }

    String[] expandArray(String[] array) {
	String[] newArray = new String[array.length + capacityIncrement];
	System.arraycopy(array, 0, newArray, 0, array.length);
	return newArray;
    }

    /**
     * Removes the component at the specified index from the specified
     * int array.
     */
    synchronized void removeElementAt(int[] array, int index) {
        if (index < 0 || index >= elementCount)
            return;
        int j = elementCount - index - 1;
        if (j > 0) {
            System.arraycopy(array, index + 1, array, index, j);
        }
    }

    /**
     * Removes the component at the specified index from the specified
     * long array.
     */
    synchronized void removeElementAt(long[] array, int index) {
        if (index < 0 || index >= elementCount)
            return;
        int j = elementCount - index - 1;
        if (j > 0) {
            System.arraycopy(array, index + 1, array, index, j);
        }
    }

    /**
     * Removes the component at the specified index from the specified
     * boolean array.
     */
    synchronized void removeElementAt(boolean[] array, int index) {
        if (index < 0 || index >= elementCount)
            return;
        int j = elementCount - index - 1;
        if (j > 0) {
            System.arraycopy(array, index + 1, array, index, j);
        }
    }

    /**
     * Removes the component at the specified index from the specified
     * Number array.
     */
    synchronized void removeElementAt(Object[] array, int index) {
        if (index < 0 || index >= elementCount)
            return;
        int j = elementCount - index - 1;
        if (j > 0) {
            System.arraycopy(array, index + 1, array, index, j);
        }
	array[elementCount - 1] = null;
    }

    /**
     * Searches for the first occurence of the given argument, testing
     * for equality using the equals method.
     */
    synchronized int indexOf(ObjectName object) {
        return observedObjects.indexOf(object);
    }

    /**
     * This method is overridden by the specific monitor classes
     * (Counter, Gauge and String).  It updates all the specific
     * arrays after adding a new observed object in the list.
     *
     * The method is not abstract so that this class can be subclassed
     * by classes outside this package.
     */
    void insertSpecificElementAt(int index) {}

    /**
     * This method is overridden by the specific monitor classes
     * (Counter, Gauge and String).  It updates all the specific
     * arrays after removing an observed object from the vector.
     *
     * The method is not abstract so that this class can be subclassed
     * by classes outside this package.
     */
    void removeSpecificElementAt(int index) {}

    /**
     * This method is used by the monitor MBean create and send a
     * monitor notification to all the listeners registered for this
     * kind of notification.
     *
     * @param type The notification type.
     * @param timeStamp The notification emission date.
     * @param msg The notification message.
     * @param derGauge The derived gauge.
     * @param trigger The threshold/string (depending on the monitor
     * type) that triggered off the notification.
     * @param index The index of the observed object that triggered
     * off the notification.
     */
    void sendNotification(String type, long timeStamp, String msg,
			  Object derGauge, Object trigger, int index) {

	if (isTraceOn()) {
	    trace("sendNotification", "send notification:" +
		  "\n\tNotification observed object = " +
		  getObservedObject(index) +
		  "\n\tNotification observed attribute = " +
		  observedAttribute +
		  "\n\tNotification derived gauge = " +
		  derGauge);
	}

	long seqno;
	synchronized (this) {
	    seqno = sequenceNumber++;
	}

	sendNotification(new MonitorNotification(type,
						 this,
						 seqno,
						 timeStamp,
						 msg,
						 getObservedObject(index),
						 observedAttribute,
						 derGauge,
						 trigger));
    }
}
