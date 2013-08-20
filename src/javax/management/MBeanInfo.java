/*
 * @(#)MBeanInfo.java	1.44 04/06/03
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * <p>Describes the management interface exposed by an MBean; that is,
 * the set of attributes and operations which are available for
 * management operations.  Instances of this class are immutable.
 * Subclasses may be mutable but this is not recommended.</p>
 *
 * <p>The contents of the <code>MBeanInfo</code> for a Dynamic MBean
 * are determined by its {@link DynamicMBean#getMBeanInfo
 * getMBeanInfo()} method.  This includes Open MBeans and Model
 * MBeans, which are kinds of Dynamic MBeans.</p>
 *
 * <p>The contents of the <code>MBeanInfo</code> for a Standard MBean
 * are determined by the MBean server as follows:</p>
 *
 * <ul>
 *
 * <li>{@link #getClassName()} returns the Java class name of the MBean
 * object;
 *
 * <li>{@link #getConstructors()} returns the list of all public
 * constructors in that object;
 *
 * <li>{@link #getAttributes()} returns the list of all attributes
 * whose existence is deduced from the presence in the MBean interface
 * of a <code>get<i>Name</i></code>, <code>is<i>Name</i></code>, or
 * <code>set<i>Name</i></code> method that conforms to the conventions
 * for Standard MBeans;
 *
 * <li>{@link #getOperations()} returns the list of all methods in
 * the MBean interface that do not represent attributes;
 *
 * <li>{@link #getNotifications()} returns an empty array if the MBean
 * does not implement the {@link NotificationBroadcaster} interface,
 * otherwise the result of calling {@link
 * NotificationBroadcaster#getNotificationInfo()} on it.
 *
 * </ul>
 *
 * <p>The remaining details of the <code>MBeanInfo</code> for a
 * Standard MBean are not specified.  This includes the description of
 * the <code>MBeanInfo</code> and of any contained constructors,
 * attributes, operations, and notifications; and the names and
 * descriptions of parameters to constructors and operations.
 *
 * @since 1.5
 */
public class MBeanInfo  implements Cloneable, java.io.Serializable  {

    /* Serial version */
    static final long serialVersionUID = -6451021435135161911L;

    /**
     * @serial The human readable description of the class.
     */
    private final String description;

    /**
     * @serial The MBean qualified name.
     */
    private final String className;

    /**
     * @serial The MBean attribute descriptors.
     */
    private final MBeanAttributeInfo[] attributes;

    /**
     * @serial The MBean operation descriptors.
     */
    private final MBeanOperationInfo[] operations;

     /**
     * @serial The MBean constructor descriptors.
     */
    private final MBeanConstructorInfo[] constructors;

    /**
     * @serial The MBean notification descriptors.
     */
    private final MBeanNotificationInfo[] notifications;

    private transient int hashCode;

    /**
     * <p>True if this class is known not to override the getters of
     * MBeanInfo.  Obviously true for MBeanInfo itself, and true
     * for a subclass where we succeed in reflecting on the methods
     * and discover they are not overridden.</p>
     *
     * <p>The purpose of this variable is to avoid cloning the arrays
     * when doing operations like {@link #equals} where we know they
     * will not be changed.  If a subclass overrides a getter, we
     * cannot access the corresponding array directly.</p>
     */
    private final transient boolean immutable;

    /**
     * Constructs an <CODE>MBeanInfo</CODE>.
     *
     * @param className The name of the Java class of the MBean described
     * by this <CODE>MBeanInfo</CODE>.  This value may be any
     * syntactically legal Java class name.  It does not have to be a
     * Java class known to the MBean server or to the MBean's
     * ClassLoader.  If it is a Java class known to the MBean's
     * ClassLoader, it is recommended but not required that the
     * class's public methods include those that would appear in a
     * Standard MBean implementing the attributes and operations in
     * this MBeanInfo.
     * @param description A human readable description of the MBean (optional).
     * @param attributes The list of exposed attributes of the MBean.
     * This may be null with the same effect as a zero-length array.
     * @param constructors The list of public constructors of the
     * MBean.  This may be null with the same effect as a zero-length
     * array.
     * @param operations The list of operations of the MBean.  This
     * may be null with the same effect as a zero-length array.
     * @param notifications The list of notifications emitted.  This
     * may be null with the same effect as a zero-length array.
     */
    public MBeanInfo(String	className,
		     String	description,
		     MBeanAttributeInfo[] attributes,
		     MBeanConstructorInfo[] constructors,
		     MBeanOperationInfo[] operations,
		     MBeanNotificationInfo[]	notifications)
	    throws IllegalArgumentException {

	this.className = className;

	this.description = description;

	if (attributes == null)
	    attributes = MBeanAttributeInfo.NO_ATTRIBUTES;
	this.attributes = attributes;

	if (operations == null)
	    operations = MBeanOperationInfo.NO_OPERATIONS;
	this.operations = operations;

	if (constructors == null)
	    constructors = MBeanConstructorInfo.NO_CONSTRUCTORS;
	this.constructors = constructors;

	if (notifications == null)
	    notifications = MBeanNotificationInfo.NO_NOTIFICATIONS;
	this.notifications = notifications;

	this.immutable = isImmutableClass(this.getClass(), MBeanInfo.class);
    }

    /**
     * <p>Returns a shallow clone of this instance.
     * The clone is obtained by simply calling <tt>super.clone()</tt>,
     * thus calling the default native shallow cloning mechanism
     * implemented by <tt>Object.clone()</tt>.
     * No deeper cloning of any internal field is made.</p>
     *
     * <p>Since this class is immutable, the clone method is chiefly of
     * interest to subclasses.</p>
     */
     public Object clone () {
	 try {
	     return  super.clone() ;
	 } catch (CloneNotSupportedException e) {
	     // should not happen as this class is cloneable
	     return null;
	 }
     }


    /**
     * Returns the name of the Java class of the MBean described by
     * this <CODE>MBeanInfo</CODE>.
     *
     * @return the class name.
     */
    public String getClassName()  {
	return className;
    }

    /**
     * Returns a human readable description of the MBean.
     *
     * @return the description.
     */
    public String getDescription()  {
	return description;
    }

    /**
     * Returns the list of attributes exposed for management.
     * Each attribute is described by an <CODE>MBeanAttributeInfo</CODE> object.
     *
     * The returned array is a shallow copy of the internal array,
     * which means that it is a copy of the internal array of
     * references to the <CODE>MBeanAttributeInfo</CODE> objects
     * but that each referenced <CODE>MBeanAttributeInfo</CODE> object is not copied.
     *
     * @return  An array of <CODE>MBeanAttributeInfo</CODE> objects.
     */
    public MBeanAttributeInfo[] getAttributes()   {
	MBeanAttributeInfo[] as = nonNullAttributes();
	if (as.length == 0)
	    return as;
	else
	    return (MBeanAttributeInfo[]) as.clone();
    }

    private MBeanAttributeInfo[] fastGetAttributes() {
	if (immutable)
	    return nonNullAttributes();
	else
	    return getAttributes();
    }

    /**
     * Return the value of the attributes field, or an empty array if
     * the field is null.  This can't happen with a
     * normally-constructed instance of this class, but can if the
     * instance was deserialized from another implementation that
     * allows the field to be null.  It would be simpler if we enforced
     * the class invariant that these fields cannot be null by writing
     * a readObject() method, but that would require us to define the
     * various array fields as non-final, which is annoying because
     * conceptually they are indeed final.
     */
    private MBeanAttributeInfo[] nonNullAttributes() {
	return (attributes == null) ?
	    MBeanAttributeInfo.NO_ATTRIBUTES : attributes;
    }

    /**
     * Returns the list of operations  of the MBean.
     * Each operation is described by an <CODE>MBeanOperationInfo</CODE> object.
     *
     * The returned array is a shallow copy of the internal array,
     * which means that it is a copy of the internal array of
     * references to the <CODE>MBeanOperationInfo</CODE> objects
     * but that each referenced <CODE>MBeanOperationInfo</CODE> object is not copied.
     *
     * @return  An array of <CODE>MBeanOperationInfo</CODE> objects.
     */
    public MBeanOperationInfo[] getOperations()  {
	MBeanOperationInfo[] os = nonNullOperations();
	if (os.length == 0)
	    return os;
	else
	    return (MBeanOperationInfo[]) os.clone();
    }

    private MBeanOperationInfo[] fastGetOperations() {
	if (immutable)
	    return nonNullOperations();
	else
	    return getOperations();
    }

    private MBeanOperationInfo[] nonNullOperations() {
	return (operations == null) ?
	    MBeanOperationInfo.NO_OPERATIONS : operations;
    }

    /**
     * <p>Returns the list of the public constructors of the MBean.
     * Each constructor is described by an
     * <CODE>MBeanConstructorInfo</CODE> object.</p>
     *
     * <p>The returned array is a shallow copy of the internal array,
     * which means that it is a copy of the internal array of
     * references to the <CODE>MBeanConstructorInfo</CODE> objects but
     * that each referenced <CODE>MBeanConstructorInfo</CODE> object
     * is not copied.</p>
     *
     * <p>The returned list is not necessarily exhaustive.  That is,
     * the MBean may have a public constructor that is not in the
     * list.  In this case, the MBean server can construct another
     * instance of this MBean's class using that constructor, even
     * though it is not listed here.</p>
     *
     * @return  An array of <CODE>MBeanConstructorInfo</CODE> objects.
     */
    public MBeanConstructorInfo[] getConstructors()  {
	MBeanConstructorInfo[] cs = nonNullConstructors();
	if (cs.length == 0)
	    return cs;
	else
	    return (MBeanConstructorInfo[]) cs.clone();
    }

    private MBeanConstructorInfo[] fastGetConstructors() {
	if (immutable)
	    return nonNullConstructors();
	else
	    return getConstructors();
    }

    private MBeanConstructorInfo[] nonNullConstructors() {
	return (constructors == null) ?
	    MBeanConstructorInfo.NO_CONSTRUCTORS : constructors;
    }

    /**
     * Returns the list of the notifications emitted by the MBean.
     * Each notification is described by an <CODE>MBeanNotificationInfo</CODE> object.
     *
     * The returned array is a shallow copy of the internal array,
     * which means that it is a copy of the internal array of
     * references to the <CODE>MBeanNotificationInfo</CODE> objects
     * but that each referenced <CODE>MBeanNotificationInfo</CODE> object is not copied.
     *
     * @return  An array of <CODE>MBeanNotificationInfo</CODE> objects.
     */
    public MBeanNotificationInfo[] getNotifications()  {
	MBeanNotificationInfo[] ns = nonNullNotifications();
	if (ns.length == 0)
	    return ns;
	else
	    return (MBeanNotificationInfo[]) ns.clone();
    }

    private MBeanNotificationInfo[] fastGetNotifications() {
	if (immutable)
	    return nonNullNotifications();
	else
	    return getNotifications();
    }

    private MBeanNotificationInfo[] nonNullNotifications() {
	return (notifications == null) ?
	    MBeanNotificationInfo.NO_NOTIFICATIONS : notifications;
    }

    /**
     * <p>Compare this MBeanInfo to another.  Two MBeanInfo objects
     * are equal iff they return equal values for {@link
     * #getClassName()} and for {@link #getDescription()}, and the
     * arrays returned by the two objects for {@link
     * #getAttributes()}, {@link #getOperations()}, {@link
     * #getConstructors()}, and {@link #getNotifications()} are
     * pairwise equal.  Here "equal" means {@link
     * Object#equals(Object)}, not identity.</p>
     *
     * <p>If two MBeanInfo objects return the same values in one of
     * their arrays but in a different order then they are not equal.</p>
     *
     * @param o the object to compare to.
     *
     * @return true iff <code>o</code> is an MBeanInfo that is equal
     * to this one according to the rules above.
     */
    public boolean equals(Object o) {
	if (o == this)
	    return true;
	if (!(o instanceof MBeanInfo))
	    return false;
	MBeanInfo p = (MBeanInfo) o;
	if (!p.getClassName().equals(getClassName()) ||
	    !p.getDescription().equals(getDescription()))
	    return false;
	return
	    (Arrays.equals(p.fastGetAttributes(), fastGetAttributes()) &&
	     Arrays.equals(p.fastGetOperations(), fastGetOperations()) &&
	     Arrays.equals(p.fastGetConstructors(), fastGetConstructors()) &&
	     Arrays.equals(p.fastGetNotifications(), fastGetNotifications()));
    }

    public int hashCode() {
	/* Since computing the hashCode is quite expensive, we cache it.
	   If by some terrible misfortune the computed value is 0, the
	   caching won't work and we will recompute it every time.

	   We don't bother synchronizing, because, at worst, n different
	   threads will compute the same hashCode at the same time.  */
	if (hashCode != 0)
	    return hashCode;

	hashCode =
	    getClassName().hashCode() ^
	    arrayHashCode(fastGetAttributes()) ^
	    arrayHashCode(fastGetOperations()) ^
	    arrayHashCode(fastGetConstructors()) ^
	    arrayHashCode(fastGetNotifications());

	return hashCode;
    }

    private static int arrayHashCode(Object[] array) {
	int hash = 0;
	for (int i = 0; i < array.length; i++)
	    hash ^= array[i].hashCode();
	return hash;
    }

    /**
     * Cached results of previous calls to isImmutableClass.  Maps
     * Class to Boolean.  This is a WeakHashMap so that we don't
     * prevent a class from being garbage collected just because
     * we know whether it's immutable.
     */
    private static final Map immutability = new WeakHashMap();

    /**
     * Return true if <code>subclass</code> is known to preserve the
     * immutability of <code>immutableClass</code>.  The class
     * <code>immutableClass</code> is a reference class that is known
     * to be immutable.  The subclass <code>subclass</code> is
     * considered immutable if it does not override any public method
     * of <code>immutableClass</code> whose name begins with "get" or
     * "is".  This is obviously not an infallible test for immutability,
     * but it works for the public interfaces of the MBean*Info classes.
    */
    static boolean isImmutableClass(Class subclass, Class immutableClass) {
	if (subclass == immutableClass)
	    return true;
	synchronized (immutability) {
	    Boolean immutable = (Boolean) immutability.get(subclass);
	    if (immutable == null) {
		try {
		    PrivilegedAction immutabilityAction =
			new ImmutabilityAction(subclass, immutableClass);
		    immutable = (Boolean)
			AccessController.doPrivileged(immutabilityAction);
		} catch (Exception e) { // e.g. SecurityException
		    /* We don't know, so we assume it isn't.  */
		    immutable = Boolean.FALSE;
		}
		immutability.put(subclass, immutable);
	    }
	    return immutable.booleanValue();
	}
    }

    /*
     * The PrivilegedAction stuff is probably overkill.  We can be
     * pretty sure the caller does have the required privileges -- a
     * JMX user that can't do reflection can't even use Standard
     * MBeans!  But there's probably a performance gain by not having
     * to check the whole call stack.
     */
    private static class ImmutabilityAction implements PrivilegedAction {
	private final Class subclass;
	private final Class immutableClass;

	ImmutabilityAction(Class subclass, Class immutableClass) {
	    this.subclass = subclass;
	    this.immutableClass = immutableClass;
	}

	public Object run() {
	    Method[] methods = immutableClass.getMethods();
	    for (int i = 0; i < methods.length; i++) {
		Method method = methods[i];
		String methodName = method.getName();
		if (methodName.startsWith("get")
		    || methodName.startsWith("is")) {
		    Class[] paramTypes = method.getParameterTypes();
		    try {
			Method submethod =
			    subclass.getMethod(methodName, paramTypes);
			if (!submethod.equals(method))
			    return Boolean.FALSE;
		    } catch (NoSuchMethodException e) {
			return Boolean.FALSE;
		    }
		}
	    }
	    return Boolean.TRUE;
	}
    }
}
