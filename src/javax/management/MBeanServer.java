/*
 * @(#)MBeanServer.java	1.140 04/05/18
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management;


// java import
import java.util.Set;
import java.io.ObjectInputStream;

// RI import
import javax.management.loading.ClassLoaderRepository;

/**
 * <p>This is the interface for MBean manipulation on the agent
 * side. It contains the methods necessary for the creation,
 * registration, and deletion of MBeans as well as the access methods
 * for registered MBeans.  This is the core component of the JMX
 * infrastructure.</p>
 *
 * <p>User code does not usually implement this interface.  Instead,
 * an object that implements this interface is obtained with one of
 * the methods in the {@link MBeanServerFactory} class.</p>
 *
 * <p>Every MBean which is added to the MBean server becomes
 * manageable: its attributes and operations become remotely
 * accessible through the connectors/adaptors connected to that MBean
 * server.  A Java object cannot be registered in the MBean server
 * unless it is a JMX compliant MBean.</p>
 *
 * <p>When an MBean is registered or unregistered in the MBean server
 * a {@link javax.management.MBeanServerNotification
 * MBeanServerNotification} Notification is emitted. To register an
 * object as listener to MBeanServerNotifications you should call the
 * MBean server method {@link #addNotificationListener
 * addNotificationListener} with <CODE>ObjectName</CODE> the
 * <CODE>ObjectName</CODE> of the {@link
 * javax.management.MBeanServerDelegate MBeanServerDelegate}.  This
 * <CODE>ObjectName</CODE> is: <BR>
 * <CODE>JMImplementation:type=MBeanServerDelegate</CODE>.</p>
 *
 * <p>An object obtained from the {@link
 * MBeanServerFactory#createMBeanServer(String) createMBeanServer} or
 * {@link MBeanServerFactory#newMBeanServer(String) newMBeanServer}
 * methods of the {@link MBeanServerFactory} class applies security
 * checks to its methods, as follows.</p>
 *
 * <p>First, if there is no security manager ({@link
 * System#getSecurityManager()} is null), then no checks are made.</p>
 *
 * <p>Assuming there is a security manager, the checks are made as
 * detailed below.  In what follows, <code>className</code> is the
 * string returned by {@link MBeanInfo#getClassName()} for the target
 * MBean.</p>
 *
 * <p>If a security check fails, the method throws {@link
 * SecurityException}.</p>
 *
 * <p>For methods that can throw {@link InstanceNotFoundException},
 * this exception is thrown for a non-existent MBean, regardless of
 * permissions.  This is because a non-existent MBean has no
 * <code>className</code>.</p>
 *
 * <ul>
 *
 * <li><p>For the {@link #invoke invoke} method, the caller's
 * permissions must imply {@link
 * MBeanPermission#MBeanPermission(String,String,ObjectName,String)
 * MBeanPermission(className, operationName, name, "invoke")}.</p>
 *
 * <li><p>For the {@link #getAttribute getAttribute} method, the
 * caller's permissions must imply {@link
 * MBeanPermission#MBeanPermission(String,String,ObjectName,String)
 * MBeanPermission(className, attribute, name, "getAttribute")}.</p>
 *
 * <li><p>For the {@link #getAttributes getAttributes} method, the
 * caller's permissions must imply {@link
 * MBeanPermission#MBeanPermission(String,String,ObjectName,String)
 * MBeanPermission(className, null, name, "getAttribute")}.
 * Additionally, for each attribute <em>a</em> in the {@link
 * AttributeList}, if the caller's permissions do not imply {@link
 * MBeanPermission#MBeanPermission(String,String,ObjectName,String)
 * MBeanPermission(className, <em>a</em>, name, "getAttribute")}, the
 * MBean server will behave as if that attribute had not been in the
 * supplied list.</p>
 *
 * <li><p>For the {@link #setAttribute setAttribute} method, the
 * caller's permissions must imply {@link
 * MBeanPermission#MBeanPermission(String,String,ObjectName,String)
 * MBeanPermission(className, attrName, name, "setAttribute")}, where
 * <code>attrName</code> is {@link Attribute#getName()
 * attribute.getName()}.</p>
 *
 * <li><p>For the {@link #setAttributes setAttributes} method, the
 * caller's permissions must imply {@link
 * MBeanPermission#MBeanPermission(String,String,ObjectName,String)
 * MBeanPermission(className, null, name, "setAttribute")}.
 * Additionally, for each attribute <em>a</em> in the {@link
 * AttributeList}, if the caller's permissions do not imply {@link
 * MBeanPermission#MBeanPermission(String,String,ObjectName,String)
 * MBeanPermission(className, <em>a</em>, name, "setAttribute")}, the
 * MBean server will behave as if that attribute had not been in the
 * supplied list.</p>
 *
 * <li><p>For the <code>addNotificationListener</code> methods,
 * the caller's permissions must imply {@link
 * MBeanPermission#MBeanPermission(String,String,ObjectName,String)
 * MBeanPermission(className, null, name,
 * "addNotificationListener")}.</p>
 *
 * <li><p>For the <code>removeNotificationListener</code> methods,
 * the caller's permissions must imply {@link
 * MBeanPermission#MBeanPermission(String,String,ObjectName,String)
 * MBeanPermission(className, null, name,
 * "removeNotificationListener")}.</p>
 *
 * <li><p>For the {@link #getMBeanInfo getMBeanInfo} method, the
 * caller's permissions must imply {@link
 * MBeanPermission#MBeanPermission(String,String,ObjectName,String)
 * MBeanPermission(className, null, name, "getMBeanInfo")}.</p>
 *
 * <li><p>For the {@link #getObjectInstance getObjectInstance} method,
 * the caller's permissions must imply {@link
 * MBeanPermission#MBeanPermission(String,String,ObjectName,String)
 * MBeanPermission(className, null, name, "getObjectInstance")}.</p>
 *
 * <li><p>For the {@link #isInstanceOf isInstanceOf} method, the
 * caller's permissions must imply {@link
 * MBeanPermission#MBeanPermission(String,String,ObjectName,String)
 * MBeanPermission(className, null, name, "isInstanceOf")}.</p>
 *
 * <li><p>For the {@link #queryMBeans queryMBeans} method, the
 * caller's permissions must imply {@link
 * MBeanPermission#MBeanPermission(String,String,ObjectName,String)
 * MBeanPermission(null, null, name, "queryMBeans")}.
 * Additionally, for each MBean that matches <code>name</code>,
 * if the caller's permissions do not imply {@link
 * MBeanPermission#MBeanPermission(String,String,ObjectName,String)
 * MBeanPermission(className, null, name, "queryMBeans")}, the
 * MBean server will behave as if that MBean did not exist.</p>
 *
 * <p>Certain query elements perform operations on the MBean server.
 * If the caller does not have the required permissions for a given
 * MBean, that MBean will not be included in the result of the query.
 * The standard query elements that are affected are {@link
 * Query#attr(String)}, {@link Query#attr(String,String)}, and {@link
 * Query#classattr()}.</p>
 *
 * <li><p>For the {@link #queryNames queryNames} method, the checks
 * are the same as for <code>queryMBeans</code> except that
 * <code>"queryNames"</code> is used instead of
 * <code>"queryMBeans"</code> in the <code>MBeanPermission</code>
 * objects.  Note that a <code>"queryMBeans"</code> permission implies
 * the corresponding <code>"queryNames"</code> permission.</p>
 *
 * <li><p>For the {@link #getDomains getDomains} method, the caller's
 * permissions must imply {@link
 * MBeanPermission#MBeanPermission(String,String,ObjectName,String)
 * MBeanPermission(null, null, name, "getDomains")}.  Additionally,
 * for each domain <var>d</var> in the returned array, if the caller's
 * permissions do not imply {@link
 * MBeanPermission#MBeanPermission(String,String,ObjectName,String)
 * MBeanPermission(null, null, new ObjectName("<var>d</var>:x=x"),
 * "getDomains")}, the domain is eliminated from the array.  Here,
 * <code>x=x</code> is any <var>key=value</var> pair, needed to
 * satisfy ObjectName's constructor but not otherwise relevant.</p>
 *
 * <li><p>For the {@link #getClassLoader getClassLoader} method, the
 * caller's permissions must imply {@link
 * MBeanPermission#MBeanPermission(String,String,ObjectName,String)
 * MBeanPermission(className, null, loaderName,
 * "getClassLoader")}.</p>
 *
 * <li><p>For the {@link #getClassLoaderFor getClassLoaderFor} method,
 * the caller's permissions must imply {@link
 * MBeanPermission#MBeanPermission(String,String,ObjectName,String)
 * MBeanPermission(className, null, mbeanName,
 * "getClassLoaderFor")}.</p>
 *
 * <li><p>For the {@link #getClassLoaderRepository
 * getClassLoaderRepository} method, the caller's permissions must
 * imply {@link
 * MBeanPermission#MBeanPermission(String,String,ObjectName,String)
 * MBeanPermission(null, null, null, "getClassLoaderRepository")}.</p>
 *
 * <li><p>For the deprecated <code>deserialize</code> methods, the
 * required permissions are the same as for the methods that replace
 * them.</p>
 *
 * <li><p>For the <code>instantiate</code> methods, the caller's
 * permissions must imply {@link
 * MBeanPermission#MBeanPermission(String,String,ObjectName,String)
 * MBeanPermission(className, null, null, "instantiate")}.</p>
 *
 * <li><p>For the {@link #registerMBean registerMBean} method, the
 * caller's permissions must imply {@link
 * MBeanPermission#MBeanPermission(String,String,ObjectName,String)
 * MBeanPermission(className, null, name, "registerMBean")}.  Here
 * <code>className</code> is the string returned by {@link
 * MBeanInfo#getClassName()} for an object of this class.
 *
 * <p>If the <code>MBeanPermission</code> check succeeds, the MBean's
 * class is validated by checking that its {@link
 * java.security.ProtectionDomain ProtectionDomain} implies {@link
 * MBeanTrustPermission#MBeanTrustPermission(String)
 * MBeanTrustPermission("register")}.</p>
 *
 * <p>Finally, if the <code>name</code> argument is null, another
 * <code>MBeanPermission</code> check is made using the
 * <code>ObjectName</code> returned by {@link
 * MBeanRegistration#preRegister MBeanRegistration.preRegister}.</p>
 *
 * <li><p>For the <code>createMBean</code> methods, the caller's
 * permissions must imply the permissions needed by the equivalent
 * <code>instantiate</code> followed by
 * <code>registerMBean</code>.</p>
 *
 * <li><p>For the {@link #unregisterMBean unregisterMBean} method,
 * the caller's permissions must imply {@link
 * MBeanPermission#MBeanPermission(String,String,ObjectName,String)
 * MBeanPermission(className, null, name, "unregisterMBean")}.</p>
 *
 * </ul>
 *
 * @since 1.5
 */
 
/* DELETED:
 *
 * <li><p>For the {@link #isRegistered isRegistered} method, the
 * caller's permissions must imply {@link
 * MBeanPermission#MBeanPermission(String,String,ObjectName,String)
 * MBeanPermission(null, null, name, "isRegistered")}.</p>
 */
public interface MBeanServer extends MBeanServerConnection {
    /**
     * <p>Instantiates and registers an MBean in the MBean server.  The
     * MBean server will use its {@link
     * javax.management.loading.ClassLoaderRepository Default Loader
     * Repository} to load the class of the MBean.  An object name is
     * associated to the MBean.	 If the object name given is null, the
     * MBean must provide its own name by implementing the {@link
     * javax.management.MBeanRegistration MBeanRegistration} interface
     * and returning the name from the {@link
     * MBeanRegistration#preRegister preRegister} method.</p>
     *
     * <p>This method is equivalent to {@link
     * #createMBean(String,ObjectName,Object[],String[])
     * createMBean(className, name, (Object[]) null, (String[])
     * null)}.</p>
     *
     * @param className The class name of the MBean to be instantiated.	   
     * @param name The object name of the MBean. May be null.	 
     *
     * @return An <CODE>ObjectInstance</CODE>, containing the
     * <CODE>ObjectName</CODE> and the Java class name of the newly
     * instantiated MBean.  If the contained <code>ObjectName</code>
     * is <code>n</code>, the contained Java class name is
     * <code>{@link #getMBeanInfo getMBeanInfo(n)}.getClassName()</code>.
     *
     * @exception ReflectionException Wraps a
     * <CODE>java.lang.ClassNotFoundException</CODE> or a
     * <CODE><CODE>java.lang.Exception</CODE></CODE> that occurred
     * when trying to invoke the MBean's constructor.
     * @exception InstanceAlreadyExistsException The MBean is already
     * under the control of the MBean server.
     * @exception MBeanRegistrationException The
     * <CODE>preRegister</CODE> (<CODE>MBeanRegistration</CODE>
     * interface) method of the MBean has thrown an exception. The
     * MBean will not be registered.
     * @exception MBeanException The constructor of the MBean has
     * thrown an exception
     * @exception NotCompliantMBeanException This class is not a JMX
     * compliant MBean
     * @exception RuntimeOperationsException Wraps a
     * <CODE>java.lang.IllegalArgumentException</CODE>: The className
     * passed in parameter is null, the <CODE>ObjectName</CODE> passed
     * in parameter contains a pattern or no <CODE>ObjectName</CODE>
     * is specified for the MBean.
     */
    public ObjectInstance createMBean(String className, ObjectName name)
	    throws ReflectionException, InstanceAlreadyExistsException,
		   MBeanRegistrationException, MBeanException,
		   NotCompliantMBeanException;

    /**
     * <p>Instantiates and registers an MBean in the MBean server.  The
     * class loader to be used is identified by its object name. An
     * object name is associated to the MBean. If the object name of
     * the loader is null, the ClassLoader that loaded the MBean
     * server will be used.  If the MBean's object name given is null,
     * the MBean must provide its own name by implementing the {@link
     * javax.management.MBeanRegistration MBeanRegistration} interface
     * and returning the name from the {@link
     * MBeanRegistration#preRegister preRegister} method.</p>
     *
     * <p>This method is equivalent to {@link
     * #createMBean(String,ObjectName,ObjectName,Object[],String[])
     * createMBean(className, name, loaderName, (Object[]) null,
     * (String[]) null)}.</p>
     *
     * @param className The class name of the MBean to be instantiated.	   
     * @param name The object name of the MBean. May be null.	 
     * @param loaderName The object name of the class loader to be used.
     *
     * @return An <CODE>ObjectInstance</CODE>, containing the
     * <CODE>ObjectName</CODE> and the Java class name of the newly
     * instantiated MBean.  If the contained <code>ObjectName</code>
     * is <code>n</code>, the contained Java class name is
     * <code>{@link #getMBeanInfo getMBeanInfo(n)}.getClassName()</code>.
     *
     * @exception ReflectionException Wraps a
     * <CODE>java.lang.ClassNotFoundException</CODE> or a
     * <CODE>java.lang.Exception</CODE> that occurred when trying to
     * invoke the MBean's constructor.
     * @exception InstanceAlreadyExistsException The MBean is already
     * under the control of the MBean server.
     * @exception MBeanRegistrationException The
     * <CODE>preRegister</CODE> (<CODE>MBeanRegistration</CODE>
     * interface) method of the MBean has thrown an exception. The
     * MBean will not be registered.
     * @exception MBeanException The constructor of the MBean has
     * thrown an exception
     * @exception NotCompliantMBeanException This class is not a JMX
     * compliant MBean
     * @exception InstanceNotFoundException The specified class loader
     * is not registered in the MBean server.
     * @exception RuntimeOperationsException Wraps a
     * <CODE>java.lang.IllegalArgumentException</CODE>: The className
     * passed in parameter is null, the <CODE>ObjectName</CODE> passed
     * in parameter contains a pattern or no <CODE>ObjectName</CODE>
     * is specified for the MBean.
     */
    public ObjectInstance createMBean(String className, ObjectName name,
				      ObjectName loaderName) 
	    throws ReflectionException, InstanceAlreadyExistsException,
		   MBeanRegistrationException, MBeanException,
		   NotCompliantMBeanException, InstanceNotFoundException;



    /**
     * Instantiates and registers an MBean in the MBean server.  The
     * MBean server will use its {@link
     * javax.management.loading.ClassLoaderRepository Default Loader
     * Repository} to load the class of the MBean.  An object name is
     * associated to the MBean.  If the object name given is null, the
     * MBean must provide its own name by implementing the {@link
     * javax.management.MBeanRegistration MBeanRegistration} interface
     * and returning the name from the {@link
     * MBeanRegistration#preRegister preRegister} method.
     *
     * @param className The class name of the MBean to be instantiated.
     * @param name The object name of the MBean. May be null.
     * @param params An array containing the parameters of the
     * constructor to be invoked.
     * @param signature An array containing the signature of the
     * constructor to be invoked.
     *
     * @return An <CODE>ObjectInstance</CODE>, containing the
     * <CODE>ObjectName</CODE> and the Java class name of the newly
     * instantiated MBean.  If the contained <code>ObjectName</code>
     * is <code>n</code>, the contained Java class name is
     * <code>{@link #getMBeanInfo getMBeanInfo(n)}.getClassName()</code>.
     *
     * @exception ReflectionException Wraps a
     * <CODE>java.lang.ClassNotFoundException</CODE> or a
     * <CODE>java.lang.Exception</CODE> that occurred when trying to
     * invoke the MBean's constructor.
     * @exception InstanceAlreadyExistsException The MBean is already
     * under the control of the MBean server.
     * @exception MBeanRegistrationException The
     * <CODE>preRegister</CODE> (<CODE>MBeanRegistration</CODE>
     * interface) method of the MBean has thrown an exception. The
     * MBean will not be registered.
     * @exception MBeanException The constructor of the MBean has
     * thrown an exception
     * @exception NotCompliantMBeanException This class is not a JMX
     * compliant MBean
     * @exception RuntimeOperationsException Wraps a
     * <CODE>java.lang.IllegalArgumentException</CODE>: The className
     * passed in parameter is null, the <CODE>ObjectName</CODE> passed
     * in parameter contains a pattern or no <CODE>ObjectName</CODE>
     * is specified for the MBean.
     */
    public ObjectInstance createMBean(String className, ObjectName name,
				      Object params[], String signature[]) 
	    throws ReflectionException, InstanceAlreadyExistsException,
	    	   MBeanRegistrationException, MBeanException,
	    	   NotCompliantMBeanException;

    /**
     * Instantiates and registers an MBean in the MBean server.  The
     * class loader to be used is identified by its object name. An
     * object name is associated to the MBean. If the object name of
     * the loader is not specified, the ClassLoader that loaded the
     * MBean server will be used.  If the MBean object name given is
     * null, the MBean must provide its own name by implementing the
     * {@link javax.management.MBeanRegistration MBeanRegistration}
     * interface and returning the name from the {@link
     * MBeanRegistration#preRegister preRegister} method.
     *
     * @param className The class name of the MBean to be instantiated.
     * @param name The object name of the MBean. May be null.
     * @param params An array containing the parameters of the
     * constructor to be invoked.
     * @param signature An array containing the signature of the
     * constructor to be invoked.
     * @param loaderName The object name of the class loader to be used.
     *
     * @return An <CODE>ObjectInstance</CODE>, containing the
     * <CODE>ObjectName</CODE> and the Java class name of the newly
     * instantiated MBean.  If the contained <code>ObjectName</code>
     * is <code>n</code>, the contained Java class name is
     * <code>{@link #getMBeanInfo getMBeanInfo(n)}.getClassName()</code>.
     *
     * @exception ReflectionException Wraps a
     * <CODE>java.lang.ClassNotFoundException</CODE> or a
     * <CODE>java.lang.Exception</CODE> that occurred when trying to
     * invoke the MBean's constructor.
     * @exception InstanceAlreadyExistsException The MBean is already
     * under the control of the MBean server.
     * @exception MBeanRegistrationException The
     * <CODE>preRegister</CODE> (<CODE>MBeanRegistration</CODE>
     * interface) method of the MBean has thrown an exception. The
     * MBean will not be registered.
     * @exception MBeanException The constructor of the MBean has
     * thrown an exception
     * @exception NotCompliantMBeanException This class is not a JMX
     * compliant MBean
     * @exception InstanceNotFoundException The specified class loader
     * is not registered in the MBean server.
     * @exception RuntimeOperationsException Wraps a
     * <CODE>java.lang.IllegalArgumentException</CODE>: The className
     * passed in parameter is null, the <CODE>ObjectName</CODE> passed
     * in parameter contains a pattern or no <CODE>ObjectName</CODE>
     * is specified for the MBean.
     *
     */
    public ObjectInstance createMBean(String className, ObjectName name,
				      ObjectName loaderName, Object params[],
				      String signature[]) 
	    throws ReflectionException, InstanceAlreadyExistsException,
	    	   MBeanRegistrationException, MBeanException,
	    	   NotCompliantMBeanException, InstanceNotFoundException;

    /**
     * Registers a pre-existing object as an MBean with the MBean
     * server. If the object name given is null, the MBean must
     * provide its own name by implementing the {@link
     * javax.management.MBeanRegistration MBeanRegistration} interface
     * and returning the name from the {@link
     * MBeanRegistration#preRegister preRegister} method.
     *
     * @param object The  MBean to be registered as an MBean.	  
     * @param name The object name of the MBean. May be null.
     *
     * @return An <CODE>ObjectInstance</CODE>, containing the
     * <CODE>ObjectName</CODE> and the Java class name of the newly
     * registered MBean.  If the contained <code>ObjectName</code>
     * is <code>n</code>, the contained Java class name is
     * <code>{@link #getMBeanInfo getMBeanInfo(n)}.getClassName()</code>.
     *
     * @exception InstanceAlreadyExistsException The MBean is already
     * under the control of the MBean server.
     * @exception MBeanRegistrationException The
     * <CODE>preRegister</CODE> (<CODE>MBeanRegistration</CODE>
     * interface) method of the MBean has thrown an exception. The
     * MBean will not be registered.
     * @exception NotCompliantMBeanException This object is not a JMX
     * compliant MBean
     * @exception RuntimeOperationsException Wraps a
     * <CODE>java.lang.IllegalArgumentException</CODE>: The object
     * passed in parameter is null or no object name is specified.
     */
    public ObjectInstance registerMBean(Object object, ObjectName name)
	    throws InstanceAlreadyExistsException, MBeanRegistrationException,
		   NotCompliantMBeanException;

    /**
     * Unregisters an MBean from the MBean server. The MBean is
     * identified by its object name. Once the method has been
     * invoked, the MBean may no longer be accessed by its object
     * name.
     *
     * @param name The object name of the MBean to be unregistered.
     *
     * @exception InstanceNotFoundException The MBean specified is not
     * registered in the MBean server.
     * @exception MBeanRegistrationException The preDeregister
     * ((<CODE>MBeanRegistration</CODE> interface) method of the MBean
     * has thrown an exception.
     * @exception RuntimeOperationsException Wraps a
     * <CODE>java.lang.IllegalArgumentException</CODE>: The object
     * name in parameter is null or the MBean you are when trying to
     * unregister is the {@link javax.management.MBeanServerDelegate
     * MBeanServerDelegate} MBean.
     *
     */
    public void unregisterMBean(ObjectName name)
	    throws InstanceNotFoundException, MBeanRegistrationException;

    /**
     * Gets the <CODE>ObjectInstance</CODE> for a given MBean
     * registered with the MBean server.
     *
     * @param name The object name of the MBean.
     *
     * @return The <CODE>ObjectInstance</CODE> associated with the MBean
     * specified by <VAR>name</VAR>.  The contained <code>ObjectName</code>
     * is <code>name</code> and the contained class name is
     * <code>{@link #getMBeanInfo getMBeanInfo(name)}.getClassName()</code>.
     *
     * @exception InstanceNotFoundException The MBean specified is not
     * registered in the MBean server.
     */
    public ObjectInstance getObjectInstance(ObjectName name)
	    throws InstanceNotFoundException;

    /**
     * Gets MBeans controlled by the MBean server. This method allows
     * any of the following to be obtained: All MBeans, a set of
     * MBeans specified by pattern matching on the
     * <CODE>ObjectName</CODE> and/or a Query expression, a specific
     * MBean. When the object name is null or no domain and key
     * properties are specified, all objects are to be selected (and
     * filtered if a query is specified). It returns the set of
     * <CODE>ObjectInstance</CODE> objects (containing the
     * <CODE>ObjectName</CODE> and the Java Class name) for the
     * selected MBeans.
     *
     * @param name The object name pattern identifying the MBeans to
     * be retrieved. If null or no domain and key properties are
     * specified, all the MBeans registered will be retrieved.
     * @param query The query expression to be applied for selecting
     * MBeans. If null no query expression will be applied for
     * selecting MBeans.
     *
     * @return A set containing the <CODE>ObjectInstance</CODE>
     * objects for the selected MBeans.  If no MBean satisfies the
     * query an empty set is returned.
     */
    public Set queryMBeans(ObjectName name, QueryExp query);

    /**
     * Gets the names of MBeans controlled by the MBean server. This
     * method enables any of the following to be obtained: The names
     * of all MBeans, the names of a set of MBeans specified by
     * pattern matching on the <CODE>ObjectName</CODE> and/or a Query
     * expression, a specific MBean name (equivalent to testing
     * whether an MBean is registered). When the object name is null
     * or no domain and key properties are specified, all objects are
     * selected (and filtered if a query is specified). It returns the
     * set of ObjectNames for the MBeans selected.
     *
     * @param name The object name pattern identifying the MBean names
     * to be retrieved. If null or no domain and key properties are
     * specified, the name of all registered MBeans will be retrieved.
     * @param query The query expression to be applied for selecting
     * MBeans. If null no query expression will be applied for
     * selecting MBeans.
     *
     * @return A set containing the ObjectNames for the MBeans
     * selected.  If no MBean satisfies the query, an empty set is
     * returned.
     */
    public Set queryNames(ObjectName name, QueryExp query);

    /**
     * Checks whether an MBean, identified by its object name, is
     * already registered with the MBean server.
     *
     * @param name The object name of the MBean to be checked.
     *
     * @return True if the MBean is already registered in the MBean
     * server, false otherwise.
     *
     * @exception RuntimeOperationsException Wraps a
     * <CODE>java.lang.IllegalArgumentException</CODE>: The object
     * name in parameter is null.
     */
    public boolean isRegistered(ObjectName name);

    /**
     * Returns the number of MBeans registered in the MBean server.
     *
     * @return the number of registered MBeans, wrapped in an Integer.
     * If the caller's permissions are restricted, this number may
     * be greater than the number of MBeans the caller can access.
     */
    public Integer getMBeanCount();

    /**
     * Gets the value of a specific attribute of a named MBean. The MBean
     * is identified by its object name.
     *
     * @param name The object name of the MBean from which the
     * attribute is to be retrieved.
     * @param attribute A String specifying the name of the attribute
     * to be retrieved.
     *
     * @return	The value of the retrieved attribute.
     *
     * @exception AttributeNotFoundException The attribute specified
     * is not accessible in the MBean.
     * @exception MBeanException Wraps an exception thrown by the
     * MBean's getter.
     * @exception InstanceNotFoundException The MBean specified is not
     * registered in the MBean server.
     * @exception ReflectionException Wraps a
     * <CODE>java.lang.Exception</CODE> thrown when trying to invoke
     * the setter.
     * @exception RuntimeOperationsException Wraps a
     * <CODE>java.lang.IllegalArgumentException</CODE>: The object
     * name in parameter is null or the attribute in parameter is
     * null.
     *
     * @see #setAttribute
     */
    public Object getAttribute(ObjectName name, String attribute)
	    throws MBeanException, AttributeNotFoundException,
	    	   InstanceNotFoundException, ReflectionException;

    /**
     * Gets the values of several attributes of a named MBean. The MBean
     * is identified by its object name.
     *
     * @param name The object name of the MBean from which the
     * attributes are retrieved.
     * @param attributes A list of the attributes to be retrieved.
     *
     * @return The list of the retrieved attributes.
     *
     * @exception InstanceNotFoundException The MBean specified is not
     * registered in the MBean server.
     * @exception ReflectionException An exception occurred when
     * trying to invoke the getAttributes method of a Dynamic MBean.
     * @exception RuntimeOperationsException Wrap a
     * <CODE>java.lang.IllegalArgumentException</CODE>: The object
     * name in parameter is null or attributes in parameter is null.
     *
     * @see #setAttributes
     */
    public AttributeList getAttributes(ObjectName name, String[] attributes)
	    throws InstanceNotFoundException, ReflectionException;

    /**
     * Sets the value of a specific attribute of a named MBean. The MBean
     * is identified by its object name.
     *
     * @param name The name of the MBean within which the attribute is
     * to be set.
     * @param attribute The identification of the attribute to be set
     * and the value it is to be set to.
     *
     * @exception InstanceNotFoundException The MBean specified is not
     * registered in the MBean server.
     * @exception AttributeNotFoundException The attribute specified
     * is not accessible in the MBean.
     * @exception InvalidAttributeValueException The value specified
     * for the attribute is not valid.
     * @exception MBeanException Wraps an exception thrown by the
     * MBean's setter.
     * @exception ReflectionException Wraps a
     * <CODE>java.lang.Exception</CODE> thrown when trying to invoke
     * the setter.
     * @exception RuntimeOperationsException Wraps a
     * <CODE>java.lang.IllegalArgumentException</CODE>: The object
     * name in parameter is null or the attribute in parameter is
     * null.
     *
     * @see #getAttribute
     */
    public void setAttribute(ObjectName name, Attribute attribute)
	    throws InstanceNotFoundException, AttributeNotFoundException,
		   InvalidAttributeValueException, MBeanException, 
		   ReflectionException;



    /**
     * Sets the values of several attributes of a named MBean. The MBean is
     * identified by its object name.
     *
     * @param name The object name of the MBean within which the
     * attributes are to be set.
     * @param attributes A list of attributes: The identification of
     * the attributes to be set and the values they are to be set to.
     *
     * @return The list of attributes that were set, with their new
     * values.
     *
     * @exception InstanceNotFoundException The MBean specified is not
     * registered in the MBean server.
     * @exception ReflectionException An exception occurred when
     * trying to invoke the setAttributes method of a Dynamic MBean.
     * @exception RuntimeOperationsException Wraps a
     * <CODE>java.lang.IllegalArgumentException</CODE>: The object
     * name in parameter is null or attributes in parameter is null.
     *
     * @see #getAttributes
     */
    public AttributeList setAttributes(ObjectName name,
				       AttributeList attributes)
	throws InstanceNotFoundException, ReflectionException;

    /**
     * Invokes an operation on an MBean.
     *
     * @param name The object name of the MBean on which the method is
     * to be invoked.
     * @param operationName The name of the operation to be invoked.
     * @param params An array containing the parameters to be set when
     * the operation is invoked
     * @param signature An array containing the signature of the
     * operation. The class objects will be loaded using the same
     * class loader as the one used for loading the MBean on which the
     * operation was invoked.
     *
     * @return The object returned by the operation, which represents
     * the result of invoking the operation on the MBean specified.
     *
     * @exception InstanceNotFoundException The MBean specified is not
     * registered in the MBean server.
     * @exception MBeanException Wraps an exception thrown by the
     * MBean's invoked method.
     * @exception ReflectionException Wraps a
     * <CODE>java.lang.Exception</CODE> thrown while trying to invoke
     * the method.
     */
    public Object invoke(ObjectName name, String operationName,
			 Object params[], String signature[])
	    throws InstanceNotFoundException, MBeanException,
		   ReflectionException;
 
    /**
     * Returns the default domain used for naming the MBean.
     * The default domain name is used as the domain part in the ObjectName
     * of MBeans if no domain is specified by the user.
     *
     * @return the default domain.
     */
    public String getDefaultDomain();

    /**
     * <p>Returns the list of domains in which any MBean is currently
     * registered.  A string is in the returned array if and only if
     * there is at least one MBean registered with an ObjectName whose
     * {@link ObjectName#getDomain() getDomain()} is equal to that
     * string.  The order of strings within the returned array is
     * not defined.</p>
     *
     * @return the list of domains.
     *
     * @since.unbundled JMX 1.2
     */
    public String[] getDomains();

    /**
     * <p>Adds a listener to a registered MBean.</p>
     *
     * <P> A notification emitted by the MBean will be forwarded by the
     * MBeanServer to the listener.  If the source of the notification
     * is a reference to the MBean object, the MBean server will replace it
     * by the MBean's ObjectName.  Otherwise the source is unchanged.
     *
     * @param name The name of the MBean on which the listener should
     * be added.
     * @param listener The listener object which will handle the
     * notifications emitted by the registered MBean.
     * @param filter The filter object. If filter is null, no
     * filtering will be performed before handling notifications.
     * @param handback The context to be sent to the listener when a
     * notification is emitted.
     *
     * @exception InstanceNotFoundException The MBean name provided
     * does not match any of the registered MBeans.
     *
     * @see #removeNotificationListener(ObjectName, NotificationListener)
     * @see #removeNotificationListener(ObjectName, NotificationListener,
     * NotificationFilter, Object)
     */
    public void addNotificationListener(ObjectName name,
					NotificationListener listener,
					NotificationFilter filter,
					Object handback)
	    throws InstanceNotFoundException;


    /**
     * <p>Adds a listener to a registered MBean.</p>
     *
     * <P> A notification emitted by the MBean will be forwarded by the
     * MBeanServer to the listener.  If the source of the notification
     * is a reference to the MBean object, the MBean server will replace it
     * by the MBean's ObjectName.  Otherwise the source is unchanged.
     *
     * <p>The listener object that receives notifications is the one
     * that is registered with the given name at the time this method
     * is called.  Even if it is subsequently unregistered, it will
     * continue to receive notifications.</p>
     *
     * @param name The name of the MBean on which the listener should
     * be added.
     * @param listener The object name of the listener which will
     * handle the notifications emitted by the registered MBean.
     * @param filter The filter object. If filter is null, no
     * filtering will be performed before handling notifications.
     * @param handback The context to be sent to the listener when a
     * notification is emitted.
     *
     * @exception InstanceNotFoundException The MBean name of the
     * notification listener or of the notification broadcaster does
     * not match any of the registered MBeans.
     * @exception RuntimeOperationsException Wraps an {@link
     * IllegalArgumentException}.  The MBean named by
     * <code>listener</code> exists but does not implement the {@link
     * NotificationListener} interface.
     *
     * @see #removeNotificationListener(ObjectName, ObjectName)
     * @see #removeNotificationListener(ObjectName, ObjectName,
     * NotificationFilter, Object)
     */
    public void addNotificationListener(ObjectName name,
					ObjectName listener,
					NotificationFilter filter,
					Object handback)
	    throws InstanceNotFoundException;

    /**
     * Removes a listener from a registered MBean.
     *
     * <P> If the listener is registered more than once, perhaps with
     * different filters or callbacks, this method will remove all
     * those registrations.
     *
     * @param name The name of the MBean on which the listener should
     * be removed.
     * @param listener The object name of the listener to be removed.
     *
     * @exception InstanceNotFoundException The MBean name provided
     * does not match any of the registered MBeans.
     * @exception ListenerNotFoundException The listener is not
     * registered in the MBean.
     *
     * @see #addNotificationListener(ObjectName, ObjectName,
     * NotificationFilter, Object)
     */
    public void removeNotificationListener(ObjectName name,
					   ObjectName listener) 
	throws InstanceNotFoundException, ListenerNotFoundException;

    /**
     * <p>Removes a listener from a registered MBean.</p>
     *
     * <p>The MBean must have a listener that exactly matches the
     * given <code>listener</code>, <code>filter</code>, and
     * <code>handback</code> parameters.  If there is more than one
     * such listener, only one is removed.</p>
     *
     * <p>The <code>filter</code> and <code>handback</code> parameters
     * may be null if and only if they are null in a listener to be
     * removed.</p>
     *
     * @param name The name of the MBean on which the listener should
     * be removed.
     * @param listener A listener that was previously added to this
     * MBean.
     * @param filter The filter that was specified when the listener
     * was added.
     * @param handback The handback that was specified when the
     * listener was added.
     *
     * @exception InstanceNotFoundException The MBean name provided
     * does not match any of the registered MBeans.
     * @exception ListenerNotFoundException The listener is not
     * registered in the MBean, or it is not registered with the given
     * filter and handback.
     *
     * @see #addNotificationListener(ObjectName, ObjectName,
     * NotificationFilter, Object)
     *
     * @since.unbundled JMX 1.2
     */
    public void removeNotificationListener(ObjectName name,
					   ObjectName listener,
					   NotificationFilter filter,
					   Object handback)
	    throws InstanceNotFoundException, ListenerNotFoundException;


    /**
     * <p>Removes a listener from a registered MBean.</p>
     *
     * <P> If the listener is registered more than once, perhaps with
     * different filters or callbacks, this method will remove all
     * those registrations.
     *
     * @param name The name of the MBean on which the listener should
     * be removed.
     * @param listener The object name of the listener to be removed.
     *
     * @exception InstanceNotFoundException The MBean name provided
     * does not match any of the registered MBeans.
     * @exception ListenerNotFoundException The listener is not
     * registered in the MBean.
     *
     * @see #addNotificationListener(ObjectName, NotificationListener,
     * NotificationFilter, Object)
     */
    public void removeNotificationListener(ObjectName name,
					   NotificationListener listener)
	    throws InstanceNotFoundException, ListenerNotFoundException;

    /**
     * <p>Removes a listener from a registered MBean.</p>
     *
     * <p>The MBean must have a listener that exactly matches the
     * given <code>listener</code>, <code>filter</code>, and
     * <code>handback</code> parameters.  If there is more than one
     * such listener, only one is removed.</p>
     *
     * <p>The <code>filter</code> and <code>handback</code> parameters
     * may be null if and only if they are null in a listener to be
     * removed.</p>
     *
     * @param name The name of the MBean on which the listener should
     * be removed.
     * @param listener A listener that was previously added to this
     * MBean.
     * @param filter The filter that was specified when the listener
     * was added.
     * @param handback The handback that was specified when the
     * listener was added.
     *
     * @exception InstanceNotFoundException The MBean name provided
     * does not match any of the registered MBeans.
     * @exception ListenerNotFoundException The listener is not
     * registered in the MBean, or it is not registered with the given
     * filter and handback.
     *
     * @see #addNotificationListener(ObjectName, NotificationListener,
     * NotificationFilter, Object)
     *
     * @since.unbundled JMX 1.2
     */
    public void removeNotificationListener(ObjectName name,
					   NotificationListener listener,
					   NotificationFilter filter,
					   Object handback)
	    throws InstanceNotFoundException, ListenerNotFoundException;

    /**
     * This method discovers the attributes and operations that an
     * MBean exposes for management.
     *
     * @param name The name of the MBean to analyze
     *
     * @return An instance of <CODE>MBeanInfo</CODE> allowing the
     * retrieval of all attributes and operations of this MBean.
     *
     * @exception IntrospectionException An exception occurred during
     * introspection.
     * @exception InstanceNotFoundException The MBean specified was
     * not found.
     * @exception ReflectionException An exception occurred when
     * trying to invoke the getMBeanInfo of a Dynamic MBean.
     */
    public MBeanInfo getMBeanInfo(ObjectName name)
	    throws InstanceNotFoundException, IntrospectionException,
	    	   ReflectionException;

 
    /**
     * <p>Returns true if the MBean specified is an instance of the
     * specified class, false otherwise.</p>
     *
     * <p>If <code>name</code> does not name an MBean, this method
     * throws {@link InstanceNotFoundException}.</p>
     *
     * <p>Otherwise, let<br>
     * X be the MBean named by <code>name</code>,<br>
     * L be the ClassLoader of X,<br>
     * N be the class name in X's {@link MBeanInfo}.</p>
     *
     * <p>If N equals <code>className</code>, the result is true.</p>
     *
     * <p>Otherwise, if L successfully loads both N and
     * <code>className</code>, and the second class is assignable from
     * the first, the result is true.</p>
     *
     * <p>Otherwise, the result is false.</p>
     * 
     * @param name The <CODE>ObjectName</CODE> of the MBean.
     * @param className The name of the class.
     *
     * @return true if the MBean specified is an instance of the
     * specified class according to the rules above, false otherwise.
     *
     * @exception InstanceNotFoundException The MBean specified is not
     * registered in the MBean server.
     *
     * @see Class#isAssignableFrom(Class)
     */
    public boolean isInstanceOf(ObjectName name, String className)
	    throws InstanceNotFoundException;

    /**
     * <p>Instantiates an object using the list of all class loaders
     * registered in the MBean server's {@link
     * javax.management.loading.ClassLoaderRepository Class Loader
     * Repository}.  The object's class should have a public
     * constructor.  This method returns a reference to the newly
     * created object.	The newly created object is not registered in
     * the MBean server.</p>
     *
     * <p>This method is equivalent to {@link
     * #instantiate(String,Object[],String[])
     * instantiate(className, (Object[]) null, (String[]) null)}.</p>
     *
     * @param className The class name of the object to be instantiated.    
     *
     * @return The newly instantiated object.	 
     *
     * @exception ReflectionException Wraps a
     * <CODE>java.lang.ClassNotFoundException</CODE> or the
     * <CODE>java.lang.Exception</CODE> that occurred when trying to
     * invoke the object's constructor.
     * @exception MBeanException The constructor of the object has
     * thrown an exception
     * @exception RuntimeOperationsException Wraps a
     * <CODE>java.lang.IllegalArgumentException</CODE>: The className
     * passed in parameter is null.
     */
    public Object instantiate(String className)
	    throws ReflectionException, MBeanException;


    /**
     * <p>Instantiates an object using the class Loader specified by its
     * <CODE>ObjectName</CODE>.	 If the loader name is null, the
     * ClassLoader that loaded the MBean Server will be used.  The
     * object's class should have a public constructor.	 This method
     * returns a reference to the newly created object.	 The newly
     * created object is not registered in the MBean server.</p>
     *
     * <p>This method is equivalent to {@link
     * #instantiate(String,ObjectName,Object[],String[])
     * instantiate(className, loaderName, (Object[]) null, (String[])
     * null)}.</p>
     *
     * @param className The class name of the MBean to be instantiated.	   
     * @param loaderName The object name of the class loader to be used.
     *
     * @return The newly instantiated object.	 
     *
     * @exception ReflectionException Wraps a
     * <CODE>java.lang.ClassNotFoundException</CODE> or the
     * <CODE>java.lang.Exception</CODE> that occurred when trying to
     * invoke the object's constructor.
     * @exception MBeanException The constructor of the object has
     * thrown an exception.
     * @exception InstanceNotFoundException The specified class loader
     * is not registered in the MBeanServer.
     * @exception RuntimeOperationsException Wraps a
     * <CODE>java.lang.IllegalArgumentException</CODE>: The className
     * passed in parameter is null.
     */
    public Object instantiate(String className, ObjectName loaderName) 
	    throws ReflectionException, MBeanException,
		   InstanceNotFoundException;

    /**
     * <p>Instantiates an object using the list of all class loaders
     * registered in the MBean server {@link
     * javax.management.loading.ClassLoaderRepository Class Loader
     * Repository}.  The object's class should have a public
     * constructor.  The call returns a reference to the newly created
     * object.	The newly created object is not registered in the
     * MBean server.</p>
     *
     * @param className The class name of the object to be instantiated.
     * @param params An array containing the parameters of the
     * constructor to be invoked.
     * @param signature An array containing the signature of the
     * constructor to be invoked.
     *
     * @return The newly instantiated object.	 
     *
     * @exception ReflectionException Wraps a
     * <CODE>java.lang.ClassNotFoundException</CODE> or the
     * <CODE>java.lang.Exception</CODE> that occurred when trying to
     * invoke the object's constructor.
     * @exception MBeanException The constructor of the object has
     * thrown an exception
     * @exception RuntimeOperationsException Wraps a
     * <CODE>java.lang.IllegalArgumentException</CODE>: The className
     * passed in parameter is null.
     */	   
    public Object instantiate(String className, Object params[],
			      String signature[]) 
	    throws ReflectionException, MBeanException; 

    /**
     * <p>Instantiates an object. The class loader to be used is
     * identified by its object name. If the object name of the loader
     * is null, the ClassLoader that loaded the MBean server will be
     * used.  The object's class should have a public constructor.
     * The call returns a reference to the newly created object.  The
     * newly created object is not registered in the MBean server.</p>
     *
     * @param className The class name of the object to be instantiated.
     * @param params An array containing the parameters of the
     * constructor to be invoked.
     * @param signature An array containing the signature of the
     * constructor to be invoked.
     * @param loaderName The object name of the class loader to be used.
     *
     * @return The newly instantiated object.	 
     *
     * @exception ReflectionException Wraps a <CODE>java.lang.ClassNotFoundException</CODE> or the <CODE>java.lang.Exception</CODE> that 
     * occurred when trying to invoke the object's constructor.	 
     * @exception MBeanException The constructor of the object has
     * thrown an exception
     * @exception InstanceNotFoundException The specified class loader
     * is not registered in the MBean server.
     * @exception RuntimeOperationsException Wraps a
     * <CODE>java.lang.IllegalArgumentException</CODE>: The className
     * passed in parameter is null.
     */	   
    public Object instantiate(String className, ObjectName loaderName,
			      Object params[], String signature[]) 
	    throws ReflectionException, MBeanException,
		   InstanceNotFoundException;

    /**
     * <p>De-serializes a byte array in the context of the class loader 
     * of an MBean.</p>
     *
     * @param name The name of the MBean whose class loader should be
     * used for the de-serialization.
     * @param data The byte array to be de-sererialized.
     *
     * @return The de-serialized object stream.
     *
     * @exception InstanceNotFoundException The MBean specified is not
     * found.
     * @exception OperationsException Any of the usual Input/Output
     * related exceptions.
     *
     * @deprecated Use {@link #getClassLoaderFor getClassLoaderFor} to
     * obtain the appropriate class loader for deserialization.
     */
    @Deprecated
    public ObjectInputStream deserialize(ObjectName name, byte[] data)
	    throws InstanceNotFoundException, OperationsException;


    /**
     * <p>De-serializes a byte array in the context of a given MBean
     * class loader.  The class loader is found by loading the class
     * <code>className</code> through the {@link
     * javax.management.loading.ClassLoaderRepository Class Loader
     * Repository}.  The resultant class's class loader is the one to
     * use.
     *
     * @param className The name of the class whose class loader should be
     * used for the de-serialization.
     * @param data The byte array to be de-sererialized.
     *
     * @return  The de-serialized object stream.
     *
     * @exception OperationsException Any of the usual Input/Output
     * related exceptions.
     * @exception ReflectionException The specified class could not be
     * loaded by the class loader repository
     *
     * @deprecated Use {@link #getClassLoaderRepository} to obtain the
     * class loader repository and use it to deserialize.
     */
    @Deprecated
    public ObjectInputStream deserialize(String className, byte[] data)
	    throws OperationsException, ReflectionException;

   
    /**
     * <p>De-serializes a byte array in the context of a given MBean
     * class loader.  The class loader is the one that loaded the
     * class with name "className".  The name of the class loader to
     * be used for loading the specified class is specified.  If null,
     * the MBean Server's class loader will be used.</p>
     *
     * @param className The name of the class whose class loader should be
     * used for the de-serialization.
     * @param data The byte array to be de-sererialized.
     * @param loaderName The name of the class loader to be used for
     * loading the specified class.  If null, the MBean Server's class
     * loader will be used.
     *
     * @return  The de-serialized object stream.
     *
     * @exception InstanceNotFoundException The specified class loader
     * MBean is not found.
     * @exception OperationsException Any of the usual Input/Output
     * related exceptions.
     * @exception ReflectionException The specified class could not be
     * loaded by the specified class loader.
     *
     * @deprecated Use {@link #getClassLoader getClassLoader} to obtain
     * the class loader for deserialization.
     */
    @Deprecated
    public ObjectInputStream deserialize(String className,
					 ObjectName loaderName,
					 byte[] data)
	    throws InstanceNotFoundException, OperationsException,
		   ReflectionException;

    /**
     * <p>Return the {@link java.lang.ClassLoader} that was used for
     * loading the class of the named MBean.</p>
     *
     * @param mbeanName The ObjectName of the MBean.
     *
     * @return The ClassLoader used for that MBean.  If <var>l</var>
     * is the MBean's actual ClassLoader, and <var>r</var> is the
     * returned value, then either:
     *
     * <ul>
     * <li><var>r</var> is identical to <var>l</var>; or
     * <li>the result of <var>r</var>{@link
     * ClassLoader#loadClass(String) .loadClass(<var>s</var>)} is the
     * same as <var>l</var>{@link ClassLoader#loadClass(String)
     * .loadClass(<var>s</var>)} for any string <var>s</var>.
     * </ul>
     *
     * What this means is that the ClassLoader may be wrapped in
     * another ClassLoader for security or other reasons.
     *
     * @exception InstanceNotFoundException if the named MBean is not found.
     *
     * @since.unbundled JMX 1.2
     */
    public ClassLoader getClassLoaderFor(ObjectName mbeanName) 
	throws InstanceNotFoundException;

    /**
     * <p>Return the named {@link java.lang.ClassLoader}.</p>
     *
     * @param loaderName The ObjectName of the ClassLoader.  May be
     * null, in which case the MBean server's own ClassLoader is
     * returned.
     *
     * @return The named ClassLoader.  If <var>l</var> is the actual
     * ClassLoader with that name, and <var>r</var> is the returned
     * value, then either:
     *
     * <ul>
     * <li><var>r</var> is identical to <var>l</var>; or
     * <li>the result of <var>r</var>{@link
     * ClassLoader#loadClass(String) .loadClass(<var>s</var>)} is the
     * same as <var>l</var>{@link ClassLoader#loadClass(String)
     * .loadClass(<var>s</var>)} for any string <var>s</var>.
     * </ul>
     *
     * What this means is that the ClassLoader may be wrapped in
     * another ClassLoader for security or other reasons.
     *
     * @exception InstanceNotFoundException if the named ClassLoader is 
     *    not found.
     *
     * @since.unbundled JMX 1.2
     */
    public ClassLoader getClassLoader(ObjectName loaderName)
	throws InstanceNotFoundException;

    /**
     * <p>Return the ClassLoaderRepository for this MBeanServer.
     * @return The ClassLoaderRepository for this MBeanServer.
     *
     * @since.unbundled JMX 1.2
     */
    public ClassLoaderRepository getClassLoaderRepository();
}
