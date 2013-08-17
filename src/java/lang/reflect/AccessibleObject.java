/*
 * @(#)AccessibleObject.java	1.10 98/09/24
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.lang.reflect;

/**
 * The AccessibleObject class is the base class for Field, Method and
 * Constructor objects.  It provides the ability to flag a reflected
 * object as suppressing default Java language access control checks
 * when it is used.  The access checks--for public, default (package)
 * access, protected, and private members--are performed when Fields,
 * Methods or Constructors are used to set or get fields, to invoke
 * methods, or to create and initialize new instances of classes,
 * respectively.
 *
 * <p>Setting the <tt>accessible</tt> flag in a reflected object
 * permits sophisticated applications with sufficient privilege, such
 * as Java Object Serialization or other persistence mechanisms, to
 * manipulate objects in a manner that would normally be prohibited.
 *
 * @see Field
 * @see Method
 * @see Constructor
 * @see ReflectPermission
 *
 * @since JDK1.2
 */
public
class AccessibleObject {

    /**
     * The Permission object that is used to check whether a client
     * has sufficient privilege to defeat Java language access
     * control checks.
     */
    static final private java.security.Permission ACCESS_PERMISSION =
	new ReflectPermission("suppressAccessChecks");

    /**
     * Convenience method to set the <tt>accessible</tt> flag for an
     * array of objects with a single security check (for efficiency).
     *
     * <p>First, if there is a security manager, its <code>checkPermission</code> 
     * method is called with a 
     * <code>ReflectPermission("suppressAccessChecks")</code> permission. 
     * 
     * @param array the array of AccessibleObjects
     * @param flag the new value for the <tt>accessible</tt> flag in each object
     * @throws SecurityException if the request is denied.
     * @see SecurityManager#checkPermission
     * @see java.lang.RuntimePermission
     */
    public static void setAccessible(AccessibleObject[] array, boolean flag)
	throws SecurityException {
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) sm.checkPermission(ACCESS_PERMISSION);
	for (int i = 0; i < array.length; i++) {
	    array[i].override = flag;
	}
    }

    /**
     * Set the <tt>accessible</tt> flag for this object to
     * the indicated boolean value.  A value of <tt>true</tt> indicates that
     * the reflected object should suppress Java language access
     * checking when it is used.  A value of <tt>false</tt> indicates 
     * that the reflected object should enforce Java language access checks.
     *
     * <p>First, if there is a security manager, its <code>checkPermission</code> 
     * method is called with a 
     * <code>ReflectPermission("suppressAccessChecks")</code> permission. 
     * 
     * @param flag the new value for the <tt>accessible</tt> flag
     * @throws SecurityException if the request is denied.
     * @see SecurityManager#checkPermission
     * @see java.lang.RuntimePermission
     */
    public void setAccessible(boolean flag) throws SecurityException {
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) sm.checkPermission(ACCESS_PERMISSION);
	this.override = flag;
    }

    /**
     * Get the value of the <tt>accessible</tt> flag for this object.
     */
    public boolean isAccessible() {
	return override;
    }

    /**
     * Constructor: only used by the Java Virtual Machine.
     */
    protected AccessibleObject() {}

    // N.B. jvm depends on this field name, and initializes to <tt>false</tt>.
    private boolean override;

}
