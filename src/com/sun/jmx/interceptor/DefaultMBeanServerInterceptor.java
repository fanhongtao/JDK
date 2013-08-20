/*
 * @(#)DefaultMBeanServerInterceptor.java	1.64 04/03/18
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.jmx.interceptor;

// java import
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.WeakHashMap;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.io.OptionalDataException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.IOException;
import java.security.AccessControlContext;
import java.security.Permission;
import java.security.ProtectionDomain;
import java.security.AccessController;
import java.security.PrivilegedAction;

// JMX import
import javax.management.*;
import javax.management.loading.ClassLoaderRepository;

// JMX RI
import com.sun.jmx.mbeanserver.ModifiableClassLoaderRepository;
import com.sun.jmx.mbeanserver.MetaData;
import com.sun.jmx.mbeanserver.MetaDataImpl;
import com.sun.jmx.mbeanserver.MBeanInstantiator;
import com.sun.jmx.mbeanserver.Repository;
import com.sun.jmx.mbeanserver.RepositorySupport;
import com.sun.jmx.mbeanserver.NamedObject;
import com.sun.jmx.defaults.ServiceName;
import com.sun.jmx.trace.Trace;

/**
 * This is the default class for MBean manipulation on the agent side. It
 * contains the methods necessary for the creation, registration, and
 * deletion of MBeans as well as the access methods for registered MBeans.
 * This is the core component of the JMX infrastructure.
 * <P>
 * Every MBean which is added to the MBean server becomes manageable: its attributes and operations
 * become remotely accessible through the connectors/adaptors connected to that MBean server.
 * A Java object cannot be registered in the MBean server unless it is a JMX compliant MBean.
 * <P>
 * When an MBean is registered or unregistered in the MBean server an
 * {@link javax.management.MBeanServerNotification MBeanServerNotification}
 * Notification is emitted. To register an object as listener to MBeanServerNotifications
 * you should call the MBean server method {@link #addNotificationListener addNotificationListener} with <CODE>ObjectName</CODE>
 * the <CODE>ObjectName</CODE> of the {@link javax.management.MBeanServerDelegate MBeanServerDelegate}.
 * This <CODE>ObjectName</CODE> is:
 * <BR>
 * <CODE>JMImplementation:type=MBeanServerDelegate</CODE>.
 *
 * @since 1.5
 * @since.unbundled JMX RI 1.2
 */
public class DefaultMBeanServerInterceptor implements MBeanServerInterceptor {

    /** MBeanServerDelegate ObjectName shared ref */
    private final static ObjectName _MBSDelegateObjectName;
    static {
	try {
            _MBSDelegateObjectName = new ObjectName(ServiceName.DELEGATE);
	} catch (MalformedObjectNameException e) {
	    throw new UnsupportedOperationException(e.getMessage());
	}
    }

    /** The MBeanInstantiator object used by the 
     *  DefaultMBeanServerInterceptor */
    private final transient MBeanInstantiator instantiator;

    /** The MBean server object that is associated to the 
     *  DefaultMBeanServerInterceptor */
    private transient MBeanServer server = null;

    /** The MBean server object taht associated to the 
     *  DefaultMBeanServerInterceptor */
    private final transient MBeanServerDelegate delegate;

    /** The Metadata object used by the DefaultMBeanServerInterceptor */
    private final transient MetaData meta;

    /** The Repository object used by the DefaultMBeanServerInterceptor */
    private final transient Repository repository;

    /** Wrappers for client listeners.  */
    /* See the comment before addNotificationListener below.  */
    private final transient WeakHashMap listenerWrappers = new WeakHashMap();

    /** The default domain of the object names */
    private final String domain;

    /** True if the repository perform queries, false otherwise */
    private boolean queryByRepo;

    /** The sequence number identifyng the notifications sent */
    // Now sequence number is handled by MBeanServerDelegate.
    // private int sequenceNumber=0;

    /** The name of this class to be used for tracing */
    private final static String dbgTag = "DefaultMBeanServerInterceptor";

    /**
     * Creates a DefaultMBeanServerInterceptor with the specified 
     * default domain name.
     * The default domain name is used as the domain part in the ObjectName
     * of MBeans if no domain is specified by the user.
     * <p>Do not forget to call <code>initialize(outer,delegate)</code>
     * before using this object.
     * @param domain The default domain name used by this MBeanServer.
     * @param outer A pointer to the MBeanServer object that must be
     *        passed to the MBeans when invoking their
     *        {@link javax.management.MBeanRegistration} interface.
     * @param delegate A pointer to the MBeanServerDelegate associated
     *        with the new MBeanServer. The new MBeanServer must register
     *        this MBean in its MBean repository.
     * @param instantiator The MBeanInstantiator that will be used to
     *        instantiate MBeans and take care of class loading issues.
     */
    public DefaultMBeanServerInterceptor(String              domain,
					 MBeanServer         outer, 
					 MBeanServerDelegate delegate,
					 MBeanInstantiator   instantiator) {
        this(outer, delegate, instantiator, null, 
	     new RepositorySupport((domain==null?ServiceName.DOMAIN:domain))); 
    }

    /**
     * Creates a DefaultMBeanServerInterceptor with the specified 
     * repository instance.
     * <p>Do not forget to call <code>initialize(outer,delegate)</code>
     * before using this object.
     * @param outer A pointer to the MBeanServer object that must be
     *        passed to the MBeans when invoking their
     *        {@link javax.management.MBeanRegistration} interface.
     * @param delegate A pointer to the MBeanServerDelegate associated
     *        with the new MBeanServer. The new MBeanServer must register
     *        this MBean in its MBean repository.
     * @param instantiator The MBeanInstantiator that will be used to
     *        instantiate MBeans and take care of class loading issues.
     * @param metadata The MetaData object that will be used by the 
     *        MBean server in order to invoke the MBean interface of
     *        the registered MBeans.
     * @param repository The repository to use for this MBeanServer
     */
    public DefaultMBeanServerInterceptor(MBeanServer         outer, 
					 MBeanServerDelegate delegate,
					 MBeanInstantiator   instantiator, 
					 MetaData            metadata,
					 Repository          repository)  {
	if (outer == null) throw new 
	    IllegalArgumentException("outer MBeanServer cannot be null");
	if (delegate == null) throw new 
	    IllegalArgumentException("MBeanServerDelegate cannot be null");
	if (instantiator == null) throw new 
	    IllegalArgumentException("MBeanInstantiator cannot be null");
	if (metadata == null)
	    metadata = new MetaDataImpl(instantiator);
	if (repository == null) 
	    repository = new RepositorySupport(ServiceName.DOMAIN);

	this.server   = outer;
	this.delegate = delegate; 
	this.instantiator = instantiator;
	this.meta         = metadata;
	this.repository   = repository;
	this.domain       = repository.getDefaultDomain();
    }

    public ObjectInstance createMBean(String className, ObjectName name)
        throws ReflectionException, InstanceAlreadyExistsException,
               MBeanRegistrationException, MBeanException,
               NotCompliantMBeanException {

	return createMBean(className, name, (Object[]) null, (String[]) null);

    }

    public ObjectInstance createMBean(String className, ObjectName name,
                                      ObjectName loaderName)
        throws ReflectionException, InstanceAlreadyExistsException,
               MBeanRegistrationException, MBeanException,
               NotCompliantMBeanException, InstanceNotFoundException {

	return createMBean(className, name, loaderName, (Object[]) null,
			   (String[]) null);
    }

    public ObjectInstance createMBean(String className, ObjectName name,
				      Object[] params, String[] signature)
        throws ReflectionException, InstanceAlreadyExistsException,
	       MBeanRegistrationException, MBeanException,
               NotCompliantMBeanException  {

	try {
	    return createMBean(className, name, null, true,
			       params, signature);
	} catch (InstanceNotFoundException e) {
	    /* Can only happen if loaderName doesn't exist, but we just
	       passed null, so we shouldn't get this exception.  */
	    throw new IllegalArgumentException("Unexpected exception: " + e);
	}
    }

    public ObjectInstance createMBean(String className, ObjectName name,
				      ObjectName loaderName,
				      Object[] params, String[] signature)
        throws ReflectionException, InstanceAlreadyExistsException,
	       MBeanRegistrationException, MBeanException,
               NotCompliantMBeanException, InstanceNotFoundException  {

	return createMBean(className, name, loaderName, false,
			   params, signature);
    }

    private ObjectInstance createMBean(String className, ObjectName name,
				       ObjectName loaderName,
				       boolean withDefaultLoaderRepository,
				       Object[] params, String[] signature)
        throws ReflectionException, InstanceAlreadyExistsException,
	       MBeanRegistrationException, MBeanException,
               NotCompliantMBeanException, InstanceNotFoundException {

        ObjectName logicalName = name;
        Class theClass;

	if (className == null) {
	    final RuntimeException wrapped =
		new IllegalArgumentException("The class name cannot be null");
	    throw new RuntimeOperationsException(wrapped,
                      "Exception occured during MBean creation");
	}

	if (name != null) {
	    if (name.isPattern()) {
		final RuntimeException wrapped =
		    new IllegalArgumentException("Invalid name->" +
						 name.toString());
		final String msg = "Exception occurred during MBean creation";
		throw new RuntimeOperationsException(wrapped, msg);
	    }

	    name = nonDefaultDomain(name);
	}

	/* Permission check */
	checkMBeanPermission(className, null, null, "instantiate");
	checkMBeanPermission(className, null, name, "registerMBean");

	/* Load the appropriate class. */
	if (withDefaultLoaderRepository) {
	    if (isTraceOn()) {
		trace(dbgTag, "createMBean", "ClassName = " + className +
		      ",ObjectName = " + name);
	    }
	    theClass =
		instantiator.findClassWithDefaultLoaderRepository(className);
	} else if (loaderName == null) {
	    if (isTraceOn()) {
		trace(dbgTag, "createMBean", "ClassName = " + className +
		      ",ObjectName = " + name + " Loader name = null");
	    }

	    theClass = instantiator.findClass(className,
				  server.getClass().getClassLoader());
	} else {
	    loaderName = nonDefaultDomain(loaderName);

	    if (isTraceOn()) {
                trace(dbgTag, "createMBean", "ClassName = " + className +
		      ",ObjectName = " + name + ",Loader name = "+
		      loaderName.toString());
            }

	    theClass = instantiator.findClass(className, loaderName);
	}

	/* Permission check */
	checkMBeanTrustPermission(theClass);

	// Check that the MBean can be instantiated by the MBeanServer.
	instantiator.testCreation(theClass);

	// Check the JMX compliance of the class
	meta.testCompliance(theClass);

	Object moi= instantiator.instantiate(theClass, params,  signature,
					     server.getClass().getClassLoader());

	final String infoClassName;
	try {
	    infoClassName = meta.getMBeanClassName(moi);
	} catch (IntrospectionException e) {
	    throw new NotCompliantMBeanException(e.getMessage());
	} 

	return registerCreatedObject(infoClassName, moi, name);
    }

    public ObjectInstance registerMBean(Object object, ObjectName name)
	throws InstanceAlreadyExistsException, MBeanRegistrationException,
	NotCompliantMBeanException  {

	// ------------------------------
	// ------------------------------
        Class theClass = object.getClass();

        // Check the JMX compliance of the class
        meta.testCompliance(theClass);

	/* Permission check */
	final String infoClassName;
	try {
	    infoClassName = meta.getMBeanClassName(object);
	} catch (IntrospectionException e) {
	    throw new NotCompliantMBeanException(e.getMessage());
	} 

	checkMBeanPermission(infoClassName, null, name, "registerMBean");
	checkMBeanTrustPermission(theClass);

	return registerObject(infoClassName, object, name);
    }

    public void unregisterMBean(ObjectName name)
	    throws InstanceNotFoundException, MBeanRegistrationException  {
        Object object;

        if (name == null) {
	    final RuntimeException wrapped =
		new IllegalArgumentException("Object name cannot be null");
            throw new RuntimeOperationsException(wrapped,
                      "Exception occured trying to unregister the MBean");
        }

	name = nonDefaultDomain(name);

	/* Permission check */
        Object instance = getMBean(name);
	String classname = null;
	try {
	    classname = meta.getMBeanClassName(instance);
	} catch (IntrospectionException e) {
	    classname = null;
	} catch (NotCompliantMBeanException e) {
	    classname = null;
	}
	checkMBeanPermission(classname, null, name, "unregisterMBean");

	/* We synchronize here to be sure that the preDeregister
	   method will be invoked exactly once, even if more than one
	   thread unregisters the MBean at the same time.  */
        synchronized(this) {
            object = repository.retrieve(name);
            if (object==null) {
                if (isTraceOn()) {
                    trace("unregisterMBean", name+": Found no object");
                }
                throw new InstanceNotFoundException(name.toString());
            }
            if (object instanceof MBeanRegistration) {
                meta.preDeregisterInvoker(object);
            }
            // Let the repository do the work.
            try {
		repository.remove(name);
            }
            catch (InstanceNotFoundException e) {
                throw e;
            }

	    /**
	     * Checks if the unregistered MBean is a ClassLoader
	     * If so, it removes the  MBean from the default loader repository.
	     */

            if (object instanceof ClassLoader
		&& object != server.getClass().getClassLoader()) {
		final ModifiableClassLoaderRepository clr =
		    instantiator.getClassLoaderRepository();
		if (clr != null) clr.removeClassLoader(name);
	    }
	}

	// ---------------------
	// Send deletion event
	// ---------------------
	if (isTraceOn()) {
	    trace("unregisterMBean", "Send delete notification of object "
		  + name.getCanonicalName());
	}
	sendNotification(MBeanServerNotification.UNREGISTRATION_NOTIFICATION,
			 name);

	if (object instanceof MBeanRegistration) {
	    meta.postDeregisterInvoker(object);
	}
    }

    public ObjectInstance getObjectInstance(ObjectName name)
	    throws InstanceNotFoundException {

	name = nonDefaultDomain(name);
        Object obj = getMBean(name);
	final String className;
	try {
	    className = meta.getMBeanClassName(obj);
	} catch (IntrospectionException x) {
	    debugX("getObjectInstance",x);
	    throw new JMRuntimeException("Can't obtain class name for " +
					 name + ": " + x);
	} catch (NotCompliantMBeanException x) {
	    debugX("getObjectInstance",x);
	    throw new JMRuntimeException("Can't obtain class name for " +
					 name + ": " + x);
	}

	/* Permission check */
	checkMBeanPermission(className, null, name, "getObjectInstance");

	return new ObjectInstance(name, className);
    }

    public Set queryMBeans(ObjectName name, QueryExp query) {
	/* Permission check */
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) {
	    // Check if the caller has the right to invoke 'queryMBeans'
	    //
	    checkMBeanPermission(null, null, null, "queryMBeans");

	    // Perform query without "query".
	    //
	    Set list = queryMBeansImpl(name, null);

	    // Check if the caller has the right to invoke 'queryMBeans'
	    // on each specific classname/objectname in the list.
	    //
	    Set allowedList = new HashSet(list.size());
	    for (Iterator i = list.iterator(); i.hasNext(); ) {
		try {
		    ObjectInstance oi = (ObjectInstance) i.next();
		    checkMBeanPermission(oi.getClassName(), null,
					 oi.getObjectName(), "queryMBeans");
		    allowedList.add(oi);
		} catch (SecurityException e) {
		    // OK: Do not add this ObjectInstance to the list
		}
	    }

	    // Apply query to allowed MBeans only.
	    //
            return filterListOfObjectInstances(allowedList, query);
	} else {
	    // Perform query.
	    //
	    return queryMBeansImpl(name, query);
	}
    }

    private Set queryMBeansImpl(ObjectName name, QueryExp query) {
	// Query the MBeans on the repository
	//
        Set list = null;
        synchronized(this) {
            list = repository.query(name, query);
        }
        // The repository performs the filtering
	//
        if (queryByRepo) {
	    return list;
	} else {
            // The filtering will be performed by the MBeanServer
	    //
            return (filterListOfObjects(list, query));
        }
    }

    public Set queryNames(ObjectName name, QueryExp query) {
	/* Permission check */
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) {
	    // Check if the caller has the right to invoke 'queryNames'
	    //
	    checkMBeanPermission(null, null, null, "queryNames");

	    // Perform query without "query".
	    //
	    Set list = queryMBeansImpl(name, null);

	    // Check if the caller has the right to invoke 'queryNames'
	    // on each specific classname/objectname in the list.
	    //
	    Set allowedList = new HashSet(list.size());
	    for (Iterator i = list.iterator(); i.hasNext(); ) {
		try {
		    ObjectInstance oi = (ObjectInstance) i.next();
		    checkMBeanPermission(oi.getClassName(), null,
					 oi.getObjectName(), "queryNames");
		    allowedList.add(oi);
		} catch (SecurityException e) {
		    // OK: Do not add this ObjectInstance to the list
		}
	    }

	    // Apply query to allowed MBeans only.
	    //
	    Set queryList = filterListOfObjectInstances(allowedList, query);
	    Set result = new HashSet(queryList.size());
	    for (Iterator i = queryList.iterator(); i.hasNext(); ) {
		ObjectInstance oi = (ObjectInstance) i.next();
		result.add(oi.getObjectName());
	    }
	    return result;
	} else {
	    // Perform query.
	    //
	    Set queryList = queryMBeansImpl(name, query);
	    Set result = new HashSet(queryList.size());
	    for (Iterator i = queryList.iterator(); i.hasNext(); ) {
		ObjectInstance oi = (ObjectInstance) i.next();
		result.add(oi.getObjectName());
	    }
	    return result;
	}
    }

    public boolean isRegistered(ObjectName name) {
        if (name == null) {
            throw new RuntimeOperationsException(
		     new IllegalArgumentException("Object name cannot be null"),
		     "Object name cannot be null");
        }

	name = nonDefaultDomain(name);

//  	/* Permission check */
//  	checkMBeanPermission(null, null, name, "isRegistered");

        synchronized(this) {
            return (repository.contains(name));
        }
    }

    public String[] getDomains()  {
	/* Permission check */
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) {
	    // Check if the caller has the right to invoke 'getDomains'
	    //
	    checkMBeanPermission(null, null, null, "getDomains");
	    
	    // Return domains
	    //
	    String[] domains = repository.getDomains();

	    // Check if the caller has the right to invoke 'getDomains'
	    // on each specific domain in the list.
	    //
	    ArrayList result = new ArrayList(domains.length);
	    for (int i = 0; i < domains.length; i++) {
		try {
		    ObjectName domain = new ObjectName(domains[i] + ":x=x");
		    checkMBeanPermission(null, null, domain, "getDomains");
		    result.add(domains[i]);
		} catch (MalformedObjectNameException e) {
		    // Should never occur... But let's log it just in case.
		    error("getDomains",
			  "Failed to check permission for domain=" + 
			  domains[i] + ". Error is: " + e);
		    debugX("getDomains",e);
		} catch (SecurityException e) {
		    // OK: Do not add this domain to the list
		}
	    }

	    // Make an array from result.
	    //
	    return (String[]) result.toArray(new String[result.size()]);
	} else {
	    return repository.getDomains();
	}
    }

    public Integer getMBeanCount()  {
        return (repository.getCount());
    }

    public Object getAttribute(ObjectName name, String attribute)
	throws MBeanException, AttributeNotFoundException,
	       InstanceNotFoundException, ReflectionException {

        if (name == null) {
            throw new RuntimeOperationsException(new
		IllegalArgumentException("Object name cannot be null"),
                "Exception occured trying to invoke the getter on the MBean");
        }
        if (attribute == null) {
            throw new RuntimeOperationsException(new
		IllegalArgumentException("Attribute cannot be null"),
                "Exception occured trying to invoke the getter on the MBean");
        }

	name = nonDefaultDomain(name);
	
        if (isTraceOn()) {
            trace("getAttribute", "Attribute= " + attribute +
		  ", obj= " + name);
        }

	/* Permission check */
        Object instance = getMBean(name);
	String classname = null;
	try {
	    classname = meta.getMBeanClassName(instance);
	} catch (IntrospectionException e) {
	    classname = null;
	} catch (NotCompliantMBeanException e) {
	    classname = null;
	}
	checkMBeanPermission(classname, attribute, name, "getAttribute");

        return meta.getAttribute(instance, attribute);
    }

    public AttributeList getAttributes(ObjectName name, String[] attributes)
        throws InstanceNotFoundException, ReflectionException  {

        if (name == null) {
            throw new RuntimeOperationsException(new
		IllegalArgumentException("ObjectName name cannot be null"),
                "Exception occured trying to invoke the getter on the MBean");
        }

        if (attributes == null) {
            throw new RuntimeOperationsException(new
		IllegalArgumentException("Attributes cannot be null"),
                "Exception occured trying to invoke the getter on the MBean");
        }

	name = nonDefaultDomain(name);

        if (isTraceOn()) {
            trace("getAttributes", "Object= " + name);
        }

	Object instance = getMBean(name);
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) {
	    /* Permission check */
	    String classname = null;
	    try {
		classname = meta.getMBeanClassName(instance);
	    } catch (IntrospectionException e) {
		classname = null;
	    } catch (NotCompliantMBeanException e) {
		classname = null;
	    }

	    // Check if the caller has the right to invoke 'getAttribute'
	    //
	    checkMBeanPermission(classname, null, name, "getAttribute");

	    // Check if the caller has the right to invoke 'getAttribute'
	    // on each specific attribute
	    //
	    ArrayList allowedList = new ArrayList(attributes.length);
	    for (int i = 0; i < attributes.length; i++) {
		try {
		    checkMBeanPermission(classname, attributes[i],
					 name, "getAttribute");
		    allowedList.add(attributes[i]);
		} catch (SecurityException e) {
		    // OK: Do not add this attribute to the list
		}
	    }
	    String[] allowedAttributes =
		(String[]) allowedList.toArray(new String[0]);
	    return meta.getAttributes(instance, allowedAttributes);
	} else {
	    return meta.getAttributes(instance, attributes);
	}
    }

    public void setAttribute(ObjectName name, Attribute attribute)
	throws InstanceNotFoundException, AttributeNotFoundException,
	       InvalidAttributeValueException, MBeanException,
	       ReflectionException  {

        if (name == null) {
            throw new RuntimeOperationsException(new
		IllegalArgumentException("ObjectName name cannot be null"),
                "Exception occured trying to invoke the setter on the MBean");
        }

        if (attribute == null) {
            throw new RuntimeOperationsException(new
		IllegalArgumentException("Attribute cannot be null"),
                "Exception occured trying to invoke the setter on the MBean");
        }

	name = nonDefaultDomain(name);

        if (isTraceOn()) {
            trace("setAttribute", "Object= " + name + ", attribute=" +
		  attribute.getName());
        }

	/* Permission check */
        Object instance = getMBean(name);
	String classname = null;
	try {
	    classname = meta.getMBeanClassName(instance);
	} catch (IntrospectionException e) {
	    classname = null;
	} catch (NotCompliantMBeanException e) {
	    classname = null;
	}
	checkMBeanPermission(classname, attribute.getName(),
			     name, "setAttribute");

        final Object o = meta.setAttribute(instance, attribute);
    }

    public AttributeList setAttributes(ObjectName name,
				       AttributeList attributes)
	    throws InstanceNotFoundException, ReflectionException  {

        if (name == null) {
            throw new RuntimeOperationsException(new
		IllegalArgumentException("ObjectName name cannot be null"),
		"Exception occured trying to invoke the setter on the MBean");
        }

        if (attributes == null) {
            throw new RuntimeOperationsException(new
            IllegalArgumentException("AttributeList  cannot be null"),
	    "Exception occured trying to invoke the setter on the MBean");
        }

	name = nonDefaultDomain(name);

	Object instance = getMBean(name);
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) {
	    /* Permission check */
	    String classname = null;
	    try {
		classname = meta.getMBeanClassName(instance);
	    } catch (IntrospectionException e) {
		classname = null;
	    } catch (NotCompliantMBeanException e) {
		classname = null;
	    }

	    // Check if the caller has the right to invoke 'setAttribute'
	    //
	    checkMBeanPermission(classname, null, name, "setAttribute");

	    // Check if the caller has the right to invoke 'setAttribute'
	    // on each specific attribute
	    //
	    AttributeList allowedAttributes =
		new AttributeList(attributes.size());
	    for (Iterator i = attributes.iterator(); i.hasNext();) {
		try {
		    Attribute attribute = (Attribute) i.next();
		    checkMBeanPermission(classname, attribute.getName(),
					 name, "setAttribute");
		    allowedAttributes.add(attribute);
		} catch (SecurityException e) {
		    // OK: Do not add this attribute to the list
		}
	    }
	    return meta.setAttributes(instance, allowedAttributes);
	} else {
	    return meta.setAttributes(instance, attributes);
	}
    }

    public Object invoke(ObjectName name, String operationName,
			 Object params[], String signature[])
	    throws InstanceNotFoundException, MBeanException,
		   ReflectionException {

	name = nonDefaultDomain(name);

	/* Permission check */
        Object instance = getMBean(name);
	String classname = null;
	try {
	    classname = meta.getMBeanClassName(instance);
	} catch (IntrospectionException e) {
	    classname = null;
	} catch (NotCompliantMBeanException e) {
	    classname = null;
	}
	checkMBeanPermission(classname, operationName, name, "invoke");

        return meta.invoke(instance, operationName, params, signature);
    }

    /**
     * Return the MetaData service object used by this interceptor.
     *
     **/
    protected MetaData meta() {
	return meta;
    }

    /**
     * Builds an ObjectInstance.
     * <ul>
     * <li> If the given <code>object</code> implements DynamicMBean,
     *      then ask its MBeanInfo for the class name.</li>
     * <li> Otherwise, uses the provided <code>className</code></li>
     * </ul>
     *
     * @return A new ObjectInstance for the given <code>object</code>.
     * @exception NotCompliantMBeanException if the <code>object</code>
     *            implements DynamicMBean but the class name can't be
     *            retrieved from its MBeanInfo.
     **/
    protected ObjectInstance makeObjectInstance(String className,
						Object object,
						ObjectName name)
	    throws NotCompliantMBeanException {

	// if the MBean is a dynamic MBean ask its MBeanInfo for the
	// class name
	if (object instanceof DynamicMBean) {
	    try {
		className = meta.getMBeanClassName(object);
	    } catch (SecurityException x) {
		debugX("makeObjectInstance",x);
		throw x;
	    } catch (IntrospectionException x) {
		debugX("makeObjectInstance",x);
		throw new NotCompliantMBeanException(
			   "Can't obtain class name for " + name + ": " + x);
	    } catch (JMRuntimeException x) {
		debugX("makeObjectInstance",x);
		throw new NotCompliantMBeanException(
			   "Can't obtain class name for " + name + ": " + x);
	    }
	}

	if (className == null) {
	    throw new NotCompliantMBeanException(
			     "The class Name returned is null");
	}

        return(new ObjectInstance(nonDefaultDomain(name), className));
    }

    /**
     * Register <code>object</code> in the repository, with the
     * given <code>name</code>.
     * This method is called by the various createMBean() flavours
     * and by registerMBean() after all MBean compliance tests
     * have been performed.
     * <p>
     * This method does not performed any kind of test compliance,
     * and the caller should make sure that the given <code>object</object>
     * is MBean compliant.
     * <p>
     * This methods performed all the basic steps needed for object
     * registration:
     * <ul>
     * <li>If the <code>object</code> implements the MBeanRegistration
     *     interface, it invokes preRegister() on the object.</li>
     * <li>Then the object is added to the repository with the given
     *     <code>name</code>.</li>
     * <li>Finally, if the <code>object</code> implements the
     *     MBeanRegistration interface, it invokes postRegister()
     *     on the object.</li>
     * </ul>
     * @param object A reference to a MBean compliant object.
     * @param name   The ObjectName of the <code>object</code> MBean.
     * @return the actual ObjectName with which the object was registered.
     * @exception InstanceAlreadyExistsException if an object is already
     *            registered with that name.
     * @exception MBeanRegistrationException if an exception occurs during
     *            registration.
     **/
    protected ObjectInstance registerObject(String classname,
					    Object object, ObjectName name)
	throws InstanceAlreadyExistsException, 
	       MBeanRegistrationException,
	       NotCompliantMBeanException {
      
        if (object == null) {
	    final RuntimeException wrapped =
		new IllegalArgumentException("Cannot add null object");
            throw new RuntimeOperationsException(wrapped,
                        "Exception occured trying to register the MBean");
        }

	name = nonDefaultDomain(name);

        if (isTraceOn()) {
            trace(dbgTag, "registerMBean", "ObjectName = " + name);
        }
	
	ObjectName logicalName = name;

        if (object instanceof MBeanRegistration) {
            logicalName = meta.preRegisterInvoker(object, name, server);
	    if (logicalName != name && logicalName != null) {
		logicalName =
		    ObjectName.getInstance(nonDefaultDomain(logicalName));
	    }
        }

	/* Permission check */
	checkMBeanPermission(classname, null, logicalName, "registerMBean");

	final ObjectInstance result;
        if (logicalName!=null) {
	    result = makeObjectInstance(classname, object, logicalName);
            internal_addObject(object, logicalName);
        } else {
            if (object instanceof MBeanRegistration ) {
                meta.postRegisterInvoker(object, false);
            }
	    final RuntimeException wrapped =
		new IllegalArgumentException("No object name specified");
            throw new RuntimeOperationsException(wrapped,
                        "Exception occured trying to register the MBean");
        }

        if (object instanceof MBeanRegistration)
            meta.postRegisterInvoker(object, true);

        /**
         * Checks if the newly registered MBean is a ClassLoader
	 * If so, tell the ClassLoaderRepository (CLR) about it.  We do
	 * this even if the object is a PrivateClassLoader.  In that
	 * case, the CLR remembers the loader for use when it is
	 * explicitly named (e.g. as the loader in createMBean) but
	 * does not add it to the list that is consulted by
	 * ClassLoaderRepository.loadClass.
         */
        if (object instanceof ClassLoader) {
	    final ModifiableClassLoaderRepository clr =
		instantiator.getClassLoaderRepository();
	    if (clr == null) {
		final RuntimeException wrapped =
		    new IllegalArgumentException(
		     "Dynamic addition of class loaders is not supported");
		throw new RuntimeOperationsException(wrapped,
	   "Exception occured trying to register the MBean as a class loader");
	    }
	    clr.addClassLoader(logicalName, (ClassLoader)object);
        }

	return result;
    }

    /**
     * Register an object from within createMBean().
     * This method wrapps registerObject() and is only called from within
     * createMBean().
     * It calls directly registerObject(). Its only purpose is to provide
     * hooks for derived classes.
     **/
    protected ObjectInstance registerCreatedObject(String classname,
						   Object object, 
						   ObjectName name)
	throws InstanceAlreadyExistsException, 
	       MBeanRegistrationException,
	       NotCompliantMBeanException {
	return registerObject(classname,object,name);
    }

    /**
     * Gets a specific MBean controlled by the DefaultMBeanServerInterceptor.
     * The name must have a non-default domain.
     */
    private Object getMBean(ObjectName name)
	throws InstanceNotFoundException {

        if (name == null) {
            throw new RuntimeOperationsException(new
		IllegalArgumentException("Object name cannot be null"),
			       "Exception occured trying to get an MBean");
        }
        Object obj = null;
        synchronized(this) {
            obj = repository.retrieve(name);
            if (obj == null) {
		if (isTraceOn()) {
		    trace("getMBean", name+": Found no object");
		}
		throw new InstanceNotFoundException(name.toString());
            }
        }
        return obj;
    }

    private ObjectName nonDefaultDomain(ObjectName name) {
	if (name == null || name.getDomain().length() > 0)
	    return name;

	/* The ObjectName looks like ":a=b", and that's what its
	   toString() will return in this implementation.  So
	   we can just stick the default domain in front of it
	   to get a non-default-domain name.  We depend on the
	   fact that toString() works like that and that it
	   leaves wildcards in place (so we can detect an error
	   if one is supplied where it shouldn't be).  */
	final String completeName = domain + name;

	try {
	    return new ObjectName(completeName);
	} catch (MalformedObjectNameException e) {
	    final String msg =
		"Unexpected default domain problem: " + completeName + ": " +
		e;
	    throw new IllegalArgumentException(msg);
	}
    }

    public String getDefaultDomain()  {
        return domain;
    }

    /*
     * Notification handling.
     *
     * This is not trivial, because the MBeanServer translates the
     * source of a received notification from a reference to an MBean
     * into the ObjectName of that MBean.  While that does make
     * notification sending easier for MBean writers, it comes at a
     * considerable cost.  We need to replace the source of a
     * notification, which is basically wrong if there are also
     * listeners registered directly with the MBean (without going
     * through the MBean server).  We also need to wrap the listener
     * supplied by the client of the MBeanServer with a listener that
     * performs the substitution before forwarding.  This is why we
     * strongly discourage people from putting MBean references in the
     * source of their notifications.  Instead they should arrange to
     * put the ObjectName there themselves.
     *
     * However, existing code relies on the substitution, so we are
     * stuck with it.
     *
     * Here's how we handle it.  When you add a listener, we make a
     * ListenerWrapper around it.  We look that up in the
     * listenerWrappers map, and if there was already a wrapper for
     * that listener with the given ObjectName, we reuse it.  This map
     * is a WeakHashMap, so a listener that is no longer registered
     * with any MBean can be garbage collected.
     *
     * We cannot use simpler solutions such as always creating a new
     * wrapper or always registering the same listener with the MBean
     * and using the handback to find the client's original listener.
     * The reason is that we need to support the removeListener
     * variant that removes all (listener,filter,handback) triples on
     * a broadcaster that have a given listener.  And we do not have
     * any way to inspect a broadcaster's internal list of triples.
     * So the same client listener must always map to the same
     * listener registered with the broadcaster.
     *
     * Another possible solution would be to map from ObjectName to
     * list of listener wrappers (or IdentityHashMap of listener
     * wrappers), making this list the first time a listener is added
     * on a given MBean, and removing it when the MBean is removed.
     * This is probably more costly in memory, but could be useful if
     * some day we don't want to rely on weak references.
     */
    public void addNotificationListener(ObjectName name,
					NotificationListener listener,
					NotificationFilter filter,
					Object handback)
	    throws InstanceNotFoundException {

	// ------------------------------
	// ------------------------------
        if (isTraceOn()) {
            trace("addNotificationListener", "obj= " + name);
        }

	/* Permission check */
        Object instance = getMBean(name);
	String classname = null;
	try {
	    classname = meta.getMBeanClassName(instance);
	} catch (IntrospectionException e) {
	    classname = null;
	} catch (NotCompliantMBeanException e) {
	    classname = null;
	}
	checkMBeanPermission(classname, null, name, "addNotificationListener");

        NotificationBroadcaster broadcaster;

	if (!(instance instanceof NotificationBroadcaster)) {
            throw new RuntimeOperationsException(new
		IllegalArgumentException(name.getCanonicalName() ),
                "The MBean " + name.getCanonicalName() +
                " does not implement the NotificationBroadcaster interface");
        }
	broadcaster = (NotificationBroadcaster) instance;

        // ------------------
        // Check listener
        // ------------------
        if (listener == null) {
	    throw new RuntimeOperationsException(new
		IllegalArgumentException("Null listener"),"Null listener");
	}

	NotificationListener listenerWrapper =
	    getListenerWrapper(listener, name, instance, true);
	broadcaster.addNotificationListener(listenerWrapper, filter, handback);
    }

    public void addNotificationListener(ObjectName name,
					ObjectName listener,
					NotificationFilter filter,
					Object handback)
	    throws InstanceNotFoundException {

	// ------------------------------
	// ------------------------------

        // ----------------
        // Get listener object
        // ----------------
        Object instance = getMBean(listener);
        if (!(instance instanceof NotificationListener)) {
	    throw new RuntimeOperationsException(new
		IllegalArgumentException(listener.getCanonicalName()),
		"The MBean " + listener.getCanonicalName() +
		"does not implement the NotificationListener interface") ;
        }

        // ----------------
        // Add a listener on an MBean
        // ----------------
        if (isTraceOn()) {
            trace("addNotificationListener", "obj= " + name + " listener= " +
		  listener);
        }
        server.addNotificationListener(name,(NotificationListener) instance,
				       filter, handback) ;
    }

    public void removeNotificationListener(ObjectName name,
					   NotificationListener listener)
	    throws InstanceNotFoundException, ListenerNotFoundException {
	removeNotificationListener(name, listener, null, null, true);
    }

    public void removeNotificationListener(ObjectName name,
					   NotificationListener listener,
					   NotificationFilter filter,
					   Object handback)
	    throws InstanceNotFoundException, ListenerNotFoundException {
	removeNotificationListener(name, listener, filter, handback, false);
    }

    public void removeNotificationListener(ObjectName name,
					   ObjectName listener)
	    throws InstanceNotFoundException, ListenerNotFoundException {
	NotificationListener instance = getListener(listener);

        if (isTraceOn()) {
            trace("removeNotificationListener", "obj= " + name +
		  " listener= " + listener);
        }
	server.removeNotificationListener(name, instance);
    }

    public void removeNotificationListener(ObjectName name,
					   ObjectName listener,
					   NotificationFilter filter,
					   Object handback)
	    throws InstanceNotFoundException, ListenerNotFoundException {

	NotificationListener instance = getListener(listener);

        if (isTraceOn()) {
            trace("removeNotificationListener", "obj= " + name +
		  " listener= " + listener);
        }
	server.removeNotificationListener(name, instance, filter, handback);
    }

    private NotificationListener getListener(ObjectName listener) 
	throws ListenerNotFoundException {
        // ----------------
        // Get listener object
        // ----------------
        final Object instance;
        try {
	    instance = getMBean(listener);
	} catch (InstanceNotFoundException e) {
	    throw new ListenerNotFoundException(e.getMessage()) ;
	}

        if (!(instance instanceof NotificationListener)) {
	    final RuntimeException exc =
		new IllegalArgumentException(listener.getCanonicalName());
	    final String msg =
		"MBean " + listener.getCanonicalName() + " does not " +
		"implement " + NotificationListener.class.getName();
            throw new RuntimeOperationsException(exc, msg);
        }
	return (NotificationListener) instance;
    }

    private void removeNotificationListener(ObjectName name,
					    NotificationListener listener,
					    NotificationFilter filter,
					    Object handback,
					    boolean removeAll)
	    throws InstanceNotFoundException, ListenerNotFoundException {

        if (isTraceOn()) {
            trace("removeNotificationListener", "obj= " + name);
        }

	/* Permission check */
        Object instance = getMBean(name);
	String classname = null;
	try {
	    classname = meta.getMBeanClassName(instance);
	} catch (IntrospectionException e) {
	    classname = null;
	} catch (NotCompliantMBeanException e) {
	    classname = null;
	}
	checkMBeanPermission(classname, null, name,
			     "removeNotificationListener");

	/* We could simplify the code by assigning broadcaster after
	   assigning listenerWrapper, but that would change the error
	   behaviour when both the broadcaster and the listener are
	   erroneous.  */
        NotificationBroadcaster broadcaster = null;
	NotificationEmitter emitter = null;
	if (removeAll) {
	    if (!(instance instanceof NotificationBroadcaster)) {
		final RuntimeException exc =
		    new IllegalArgumentException(name.getCanonicalName());
		final String msg =
		    "MBean " + name.getCanonicalName() + " does not " +
		    "implement " + NotificationBroadcaster.class.getName();
		throw new RuntimeOperationsException(exc, msg);
	    }
	    broadcaster = (NotificationBroadcaster) instance;
	} else {
	    if (!(instance instanceof NotificationEmitter)) {
		final RuntimeException exc =
		    new IllegalArgumentException(name.getCanonicalName());
		final String msg =
		    "MBean " + name.getCanonicalName() + " does not " +
		    "implement " + NotificationEmitter.class.getName();
		throw new RuntimeOperationsException(exc, msg);
	    }
	    emitter = (NotificationEmitter) instance;
	}

	NotificationListener listenerWrapper =
	    getListenerWrapper(listener, name, instance, false);

        if (listenerWrapper == null)
            throw new ListenerNotFoundException("Unknown listener");

	if (removeAll)
	    broadcaster.removeNotificationListener(listenerWrapper);
	else {
	    emitter.removeNotificationListener(listenerWrapper,
					       filter,
					       handback);
	}
    }

    public MBeanInfo getMBeanInfo(ObjectName name)
	throws InstanceNotFoundException, IntrospectionException,
	       ReflectionException {

	// ------------------------------
	// ------------------------------

        Object moi = getMBean(name);
	final MBeanInfo mbi = meta.getMBeanInfo(moi);
	if (mbi == null)
	    throw new JMRuntimeException("MBean " + name +
					 "has no MBeanInfo");

	/* Permission check */
	checkMBeanPermission(mbi.getClassName(), null, name, "getMBeanInfo");

	return mbi;
    }

    public boolean isInstanceOf(ObjectName name, String className)
	throws InstanceNotFoundException {

	/* Permission check */
        Object instance = getMBean(name);
	String classname = null;
	try {
	    classname = meta.getMBeanClassName(instance);
	} catch (IntrospectionException e) {
	    classname = null;
	} catch (NotCompliantMBeanException e) {
	    classname = null;
	}
	checkMBeanPermission(classname, null, name, "isInstanceOf");

	try {
	    return meta.isInstanceOf(instance, className);
	} catch (ReflectionException e) {
	    debugX("isInstanceOf",e);
	    return false;
	}
    }

    /**
     * <p>Return the {@link java.lang.ClassLoader} that was used for
     * loading the class of the named MBean.
     * @param mbeanName The ObjectName of the MBean.
     * @return The ClassLoader used for that MBean.
     * @exception InstanceNotFoundException if the named MBean is not found.
     */
    public ClassLoader getClassLoaderFor(ObjectName mbeanName) 
	throws InstanceNotFoundException {

	/* Permission check */
        Object instance = getMBean(mbeanName);
	String classname = null;
	try {
	    classname = meta.getMBeanClassName(instance);
	} catch (IntrospectionException e) {
	    classname = null;
	} catch (NotCompliantMBeanException e) {
	    classname = null;
	}
	checkMBeanPermission(classname, null, mbeanName, "getClassLoaderFor");

	return instance.getClass().getClassLoader();
    }
   
    /**
     * <p>Return the named {@link java.lang.ClassLoader}.
     * @param loaderName The ObjectName of the ClassLoader.
     * @return The named ClassLoader.
     * @exception InstanceNotFoundException if the named ClassLoader
     * is not found.
     */
    public ClassLoader getClassLoader(ObjectName loaderName)
	    throws InstanceNotFoundException {

	if (loaderName == null) {
	    checkMBeanPermission(null, null, null, "getClassLoader");
	    return server.getClass().getClassLoader();
	}

        Object instance = getMBean(loaderName);
	String classname = null;
	try {
	    classname = meta.getMBeanClassName(instance);
	} catch (IntrospectionException e) {
	    classname = null;
	} catch (NotCompliantMBeanException e) {
	    classname = null;
	}
	checkMBeanPermission(classname, null, loaderName, "getClassLoader");

	/* Check if the given MBean is a ClassLoader */
	if (!(instance instanceof ClassLoader))
	    throw new InstanceNotFoundException(loaderName.toString() +
                                                " is not a classloader");

	return (ClassLoader) instance;
    }

    /**
     * Adds a MBean in the repository
     */
    private void internal_addObject(Object object, ObjectName logicalName)
	throws InstanceAlreadyExistsException {

	// ------------------------------
	// ------------------------------

        // Let the repository do the work.

        synchronized(this) {
            try {
                repository.addMBean(object, logicalName);
            }
            catch (InstanceAlreadyExistsException e) {
                if (object instanceof MBeanRegistration ) {
                    meta.postRegisterInvoker(object,false);
                }
                throw e;
            }
        }
        // ---------------------
        // Send create event
        // ---------------------
        if (isTraceOn()) {
            trace("addObject", "Send create notification of object " +
		  logicalName.getCanonicalName());
        }

        sendNotification(MBeanServerNotification.REGISTRATION_NOTIFICATION,
			 logicalName ) ;
    }

    /**
     * Sends an MBeanServerNotifications with the specified type for the
     * MBean with the specified ObjectName
     */
    private void sendNotification(String NotifType, ObjectName name) {

	// ------------------------------
	// ------------------------------

        // ---------------------
        // Create notification
        // ---------------------
	MBeanServerNotification notif = new
	    MBeanServerNotification(NotifType,_MBSDelegateObjectName,0,name);

	if (isTraceOn()) {
	    trace("sendNotification", NotifType + " " + name);
	}

	delegate.sendNotification(notif);
    }

    /**
     * Performs the necessary initializations for the MBeanServer.
     * Creates and registers the MetaData service and the MBeanServer
     * identification MBean
     */
    private void initialize(String              domain,    
			    MBeanServer         outer, 
			    MBeanServerDelegate delegate,
			    MBeanInstantiator   inst, 
			    MetaData            meta, 
			    Repository          repos) {

	// ------------------------------
	// ------------------------------

	if (!this.domain.equals(repository.getDefaultDomain()))
	    throw new IllegalArgumentException("Domain Name Mismatch");
        try {
            queryByRepo = repository.isFiltering();
        } catch (SecurityException e) {
	    throw e;
        } catch (Exception e) {
            queryByRepo = false;
        }
    }

    /**
     * Applies the specified queries to the set of objects
     */
    private Set filterListOfObjects(Set list, QueryExp query) {
        Set result = new HashSet();

        // No query ...
        if (query == null ) {
            for (final Iterator i  = list.iterator(); i.hasNext(); ) {
                final NamedObject no = (NamedObject) i.next();
		final Object obj = no.getObject();
		String className = null;

		try {
		    className = meta.getMBeanClassName(obj);
		} catch (JMException x) {
		    if (isDebugOn()) {
			debug("filterListOfObjects",
			      "Can't obtain class name for " +
			      no.getName() + ": " + x);
			debugX("filterListOfObjects",x);
		    }
		}

		result.add(new ObjectInstance(no.getName(), className));
            }
        } else {
            // Access the filter
            for (final Iterator i  = list.iterator(); i.hasNext(); ) {
                final NamedObject no = (NamedObject) i.next();
                final Object obj = no.getObject();
                boolean res = false;
		MBeanServer oldServer = QueryEval.getMBeanServer();
		query.setMBeanServer(server);
                try {
                    res = query.apply(no.getName());
                } catch (Exception e) {
                    res = false;
                } finally {
		    /*
		     * query.setMBeanServer is probably
		     * QueryEval.setMBeanServer so put back the old
		     * value.  Since that method uses a ThreadLocal
		     * variable, this code is only needed for the
		     * unusual case where the user creates a custom
		     * QueryExp that calls a nested query on another
		     * MBeanServer.
		     */
		    query.setMBeanServer(oldServer);
		}
                if (res) {
		    // if the MBean is a dynamic MBean ask its MBeanInfo
		    // for the class name
		    String className = null;
		    try {
			className = meta.getMBeanClassName(obj);
		    } catch (JMException x) {
			if (isDebugOn()) {
			    debug("filterListOfObjects",
				  "Can't obtain class name for " +
				  no.getName() + ": " + x);
			    debugX("filterListOfObjects",x);
			}
		    }
		    result.add(new ObjectInstance(no.getName(), className));
                }
            }
        }
	return result;
    }

    /**
     * Applies the specified queries to the set of ObjectInstances.
     */
    private Set filterListOfObjectInstances(Set list, QueryExp query) {
        // Null query.
	//
        if (query == null) {
	    return list;
        } else {
	    Set result = new HashSet();
            // Access the filter.
	    //
            for (final Iterator i = list.iterator(); i.hasNext(); ) {
		final ObjectInstance oi = (ObjectInstance) i.next();
                boolean res = false;
		MBeanServer oldServer = QueryEval.getMBeanServer();
		query.setMBeanServer(server);
                try {
                    res = query.apply(oi.getObjectName());
                } catch (Exception e) {
                    res = false;
                } finally {
		    /*
		     * query.setMBeanServer is probably
		     * QueryEval.setMBeanServer so put back the old
		     * value.  Since that method uses a ThreadLocal
		     * variable, this code is only needed for the
		     * unusual case where the user creates a custom
		     * QueryExp that calls a nested query on another
		     * MBeanServer.
		     */
		    query.setMBeanServer(oldServer);
		}
                if (res) {
		    result.add(oi);
                }
            }
	    return result;
        }
    }

    /*
     * Get the existing wrapper for this listener, name, and mbean, if
     * there is one.  Otherwise, if "create" is true, create and
     * return one.  Otherwise, return null.
     *
     * We use a WeakHashMap so that if the only reference to a user
     * listener is in listenerWrappers, it can be garbage collected.
     * This requires a certain amount of care, because only the key in
     * a WeakHashMap is weak; the value is strong.  We need to recover
     * the existing wrapper object (not just an object that is equal
     * to it), so we would like listenerWrappers to map any
     * ListenerWrapper to the canonical ListenerWrapper for that
     * (listener,name,mbean) set.  But we do not want this canonical
     * wrapper to be referenced strongly.  Therefore we put it inside
     * a WeakReference and that is the value in the WeakHashMap.
     */
    private NotificationListener getListenerWrapper(NotificationListener l,
						    ObjectName name,
						    Object mbean,
						    boolean create) {
	NotificationListener wrapper = new ListenerWrapper(l, name, mbean);
	synchronized (listenerWrappers) {
	    WeakReference ref = (WeakReference) listenerWrappers.get(wrapper);
	    if (ref != null) {
		NotificationListener existing =
		    (NotificationListener) ref.get();
		if (existing != null)
		    return existing;
	    }
	    if (create) {
		listenerWrappers.put(wrapper, new WeakReference(wrapper));
		return wrapper;
	    } else
		return null;
	}
    }

    private static class ListenerWrapper implements NotificationListener {
	ListenerWrapper(NotificationListener l, ObjectName name,
			Object mbean) {
	    this.listener = l;
	    this.name = name;
	    this.mbean = mbean;
	}

	public void handleNotification(Notification notification,
				       Object handback) {
	    if (notification != null) {
		if (notification.getSource() == mbean)
		    notification.setSource(name);
	    }

	    /*
	     * Listeners are not supposed to throw exceptions.  If
	     * this one does, we could remove it from the MBean.  It
	     * might indicate that a connector has stopped working,
	     * for instance, and there is no point in sending future
	     * notifications over that connection.  However, this
	     * seems rather drastic, so instead we propagate the
	     * exception and let the broadcaster handle it.
	     */
	    listener.handleNotification(notification, handback);
	}

	public boolean equals(Object o) {
	    if (!(o instanceof ListenerWrapper))
		return false;
	    ListenerWrapper w = (ListenerWrapper) o;
	    return (w.listener == listener && w.mbean == mbean
		    && w.name.equals(name));
	    /*
	     * We compare all three, in case the same MBean object
	     * gets unregistered and then reregistered under a
	     * different name, or the same name gets assigned to two
	     * different MBean objects at different times.  We do the
	     * comparisons in this order to avoid the slow
	     * ObjectName.equals when possible.
	     */
	}

	public int hashCode() {
	    return (System.identityHashCode(listener) ^
		    System.identityHashCode(mbean));
	    /*
	     * We do not include name.hashCode() in the hash because
	     * computing it is slow and usually we will not have two
	     * instances of ListenerWrapper with the same mbean but
	     * different ObjectNames.  That can happen if the MBean is
	     * unregistered from one name and reregistered with
	     * another, and there is no garbage collection between; or
	     * if the same object is registered under two names (which
	     * is not recommended because MBeanRegistration will
	     * break).  But even in these unusual cases the hash code
	     * does not have to be unique.
	     */
	}

	private NotificationListener listener;
	private ObjectName name;
	private Object mbean;
    }

    // SECURITY CHECKS
    //----------------

    private static void checkMBeanPermission(String classname,
					     String member,
					     ObjectName objectName,
					     String actions)
	throws SecurityException {
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) {
	    Permission perm = new MBeanPermission(classname,
						  member,
						  objectName,
						  actions);
	    sm.checkPermission(perm);
	}
    }

    private static void checkMBeanTrustPermission(final Class theClass)
	throws SecurityException {
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) {
	    Permission perm = new MBeanTrustPermission("register");
	    ProtectionDomain pd = (ProtectionDomain)
		AccessController.doPrivileged(new PrivilegedAction() {
		    public Object run() {
			return theClass.getProtectionDomain();
		    }
		});
	    AccessControlContext acc =
		new AccessControlContext(new ProtectionDomain[] { pd });
	    sm.checkPermission(perm, acc);
	}
    }

    // TRACES & DEBUG
    //---------------

    private static boolean isTraceOn() {
        return Trace.isSelected(Trace.LEVEL_TRACE, Trace.INFO_MBEANSERVER);
    }

    private static void trace(String clz, String func, String info) {
        Trace.send(Trace.LEVEL_TRACE, Trace.INFO_MBEANSERVER, clz, func, info);
    }

    private static void trace(String func, String info) {
        trace(dbgTag, func, info);
    }

    private static void error(String func, String info) {
        Trace.send(Trace.LEVEL_ERROR,Trace.INFO_MBEANSERVER,dbgTag,func,info);
    }

    private static boolean isDebugOn() {
        return Trace.isSelected(Trace.LEVEL_DEBUG, Trace.INFO_MBEANSERVER);
    }

    private static void debug(String clz, String func, String info) {
        Trace.send(Trace.LEVEL_DEBUG, Trace.INFO_MBEANSERVER, clz, func, info);
    }

    private static void debug(String func, String info) {
        debug(dbgTag, func, info);
    }

    private static void debugX(String func,Throwable e) {
	if (isDebugOn()) {
	    final StringWriter s = new StringWriter();
	    e.printStackTrace(new PrintWriter(s));
	    final String stack = s.toString();

	    debug(dbgTag,func,"Exception caught in "+ func+"(): "+e);
	    debug(dbgTag,func,stack);

	    // java.lang.System.err.println("**** Exception caught in "+
	    //				 func+"(): "+e);
	    // java.lang.System.err.println(stack);
	}
    }
}
