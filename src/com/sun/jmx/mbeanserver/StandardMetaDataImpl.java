/*
 * @(#)StandardMetaDataImpl.java	1.24 05/05/27
 * 
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.jmx.mbeanserver;



// java import

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;
import java.util.Iterator;
import java.io.PrintWriter;
import java.io.StringWriter;

// RI import
import javax.management.* ; 
import com.sun.jmx.trace.Trace; 
import com.sun.jmx.mbeanserver.GetPropertyAction;

/**
 * The MetaData class provides local access to the metadata service in
 * an agent.
 *
 * @since 1.5
 * @since.unbundled JMX RI 1.2
 */
class StandardMetaDataImpl extends BaseMetaDataImpl {


    /** The name of this class to be used for tracing */
    private final static String dbgTag = "StandardMetaDataImpl";
    
    /**
     * Cache of MBeanInfo objects.
     */
    private static  java.util.Map mbeanInfoCache = 
	new java.util.WeakHashMap();
    
    /**
     * Cache of MBean Interface objects.
     */
    private static  java.util.Map mbeanInterfaceCache = 
	new java.util.WeakHashMap();

    /**
     * True if RuntimeExceptions from getters, setters, and operations
     * should be wrapped in RuntimeMBeanException.  We do not have
     * similar logic for Errors because DynamicMetaDataImpl does not
     * re-wrap RuntimeErrorException as it would
     * RuntimeMBeanException.
     */
    private final boolean wrapRuntimeExceptions;

    /**
     * objects maps from primitive classes to primitive object classes.
     */
    // private static Hashtable primitiveobjects = new Hashtable();
    // {
    //  primitiveobjects.put(Boolean.TYPE, getClass("java.lang.Boolean"));
    //  primitiveobjects.put(Character.TYPE, getClass("java.lang.Character"));
    //  primitiveobjects.put(Byte.TYPE, getClass("java.lang.Byte"));
    //  primitiveobjects.put(Short.TYPE, getClass("java.lang.Short"));
    //  primitiveobjects.put(Integer.TYPE, getClass("java.lang.Integer"));
    //  primitiveobjects.put(Long.TYPE, getClass("java.lang.Long"));
    //  primitiveobjects.put(Float.TYPE, getClass("java.lang.Float"));
    //  primitiveobjects.put(Double.TYPE, getClass("java.lang.Double"));
    // }
    private final static Hashtable primitiveClasses = new Hashtable(8);
    {
        primitiveClasses.put(Boolean.TYPE.toString(), Boolean.TYPE);
        primitiveClasses.put(Character.TYPE.toString(), Character.TYPE);
        primitiveClasses.put(Byte.TYPE.toString(), Byte.TYPE);
        primitiveClasses.put(Short.TYPE.toString(), Short.TYPE);
        primitiveClasses.put(Integer.TYPE.toString(), Integer.TYPE);
        primitiveClasses.put(Long.TYPE.toString(), Long.TYPE);
        primitiveClasses.put(Float.TYPE.toString(), Float.TYPE);
        primitiveClasses.put(Double.TYPE.toString(), Double.TYPE);
     }    



    /**
     * Creates a Metadata Service.
     */
    public StandardMetaDataImpl()  {
        this(true);
    }

    StandardMetaDataImpl(boolean wrapRuntimeExceptions) {
	this.wrapRuntimeExceptions = wrapRuntimeExceptions;
    }

    /** 
     * Builds the MBeanInfo from the given concrete MBean class.
     * @param c The concrete MBean class from which the MBeanInfo
     *        must be built.
     *
     * @exception NotCompliantMBeanException if the given class
     *   is not MBean compliant.
     * @return the MBeanInfo built from class <var>c</var>, or null
     *   if class <var>c</var> implements 
     *   {@link javax.management.DynamicMBean}
     */    
    public synchronized MBeanInfo buildMBeanInfo(Class c) 
	throws NotCompliantMBeanException {
	return Introspector.testCompliance(c);
    }

    /** 
     * Builds the MBeanInfo from the given concrete MBean class,
     * using the given <var>mbeanInterface</var> as Management Interface.
     *
     * @param c The concrete MBean class from which the MBeanInfo
     *        must be built.
     * @param mbeanInterface The management interface of the MBean.
     *        If <code>null</code>, will use the regular design pattern
     *        to determine the management interface. 
     * @exception NotCompliantMBeanException if the given class and interface
     *   are not MBean compliant. Does not enforce that if class <var>c</var>
     *   is "X", then interface <var>mbeanInterface</var> is "XMBean".
     * @return the MBeanInfo built from class <var>c</var>, according
     *   to interface <var>mbeanInterface</var>. Does not check whether
     *   class <var>c</var> implements {@link javax.management.DynamicMBean}.
     **/ 
    public synchronized 
	MBeanInfo buildMBeanInfo(Class c, Class mbeanInterface) 
	throws NotCompliantMBeanException {
	return Introspector.testCompliance(c,mbeanInterface);	
    }

    /** 
     * This methods tests if the MBean is JMX compliant
     */    
    public synchronized void testCompliance(Class c) 
	throws NotCompliantMBeanException {
	// ------------------------------ 
	// ------------------------------

	final MBeanInfo mbeanInfo  = buildMBeanInfo(c);
	final Class mbeanInterface = Introspector.getMBeanInterface(c);
	cacheMBeanInfo(c,mbeanInterface,mbeanInfo);
    }
	
    /** 
     * This methods tests if the MBean is JMX compliant.
     * <li>It does not enforce that if c="X", mbeanInterface="XMBean".</li>
     * <li>It does not check whether c is a DynamicMBean</li>
     */    
    public synchronized void testCompliance(Class c, Class mbeanInterface) 
	throws NotCompliantMBeanException {
	// ------------------------------ 
	// ------------------------------

	final MBeanInfo mbeanInfo = 
	    buildMBeanInfo(c,mbeanInterface);
	if (mbeanInterface == null) 
	    mbeanInterface = Introspector.getStandardMBeanInterface(c);
	cacheMBeanInfo(c,mbeanInterface,mbeanInfo);
    }

    /** 
     * This methods returns the MBean interface of an MBean
     */ 
    public Class getMBeanInterfaceFromClass(Class c) {
	final Class itf = getCachedMBeanInterface(c);
	if (itf != null) return itf;
	synchronized (this) {
	    return Introspector.getMBeanInterface(c);
	}
    }

    /** 
     * This methods analizes the passed MBean class and
     * returns the default MBean interface according to JMX patterns.
     */ 
    public Class getStandardMBeanInterface(Class c) {
	synchronized (this) {
	    return Introspector.getStandardMBeanInterface(c);
	}
    }

    /**
     * This method discovers the attributes and operations that an MBean 
     * exposes for management.
     *
     * @param beanClass The class to be analyzed.
     *
     * @return  An instance of MBeanInfo allowing to retrieve all methods
     *          and operations of this class.
     *
     * @exception IntrospectionException if an exception occurs during
     *            introspection.
     * @exception NotCompliantMBeanException if the MBean class is not
     *            MBean compliant.
     *
     */
    public MBeanInfo getMBeanInfoFromClass(Class beanClass) 
	throws IntrospectionException, NotCompliantMBeanException {

	// Check the mbean information cache.
	MBeanInfo bi = getCachedMBeanInfo(beanClass);

	// Make an independent copy of the MBeanInfo.        
	if (bi != null) return (MBeanInfo) bi.clone() ;

	// We don't have have any MBeanInfo for that class yet.
	// => test compliance.
	testCompliance(beanClass);

	bi = getCachedMBeanInfo(beanClass);;

	// Make an independent copy of the MBeanInfo.
	if (bi != null) return (MBeanInfo) bi.clone() ;
	return bi;
    } 
       

    //---------------------------------------------------------------------
    //
    // From the MetaData interface
    //
    //---------------------------------------------------------------------

    public String getMBeanClassName(Object moi) 
	throws IntrospectionException, NotCompliantMBeanException {
	return moi.getClass().getName();
    }

    public MBeanInfo getMBeanInfo(Object moi) 
	throws IntrospectionException {
	try {
	    final MBeanInfo mbi = getMBeanInfoFromClass(moi.getClass());
	    return new MBeanInfo(mbi.getClassName(), mbi.getDescription(), 
				 mbi.getAttributes(), 
				 mbi.getConstructors(), 
				 mbi.getOperations(), 
				 findNotifications(moi));
	} catch (NotCompliantMBeanException x) {
	    debugX("getMBeanInfo",x);
	    throw new IntrospectionException("Can't build MBeanInfo for "+
					     moi.getClass().getName());
	}
    }

    public Object getAttribute(Object instance, String attribute)  
	throws MBeanException, AttributeNotFoundException, 
	       ReflectionException {

        Class mbeanClass = getMBeanInterfaceFromInstance(instance);
	if (isDebugOn()) {
	    debug("getAttribute","MBean Class is " + instance.getClass());
	    debug("getAttribute","MBean Interface is " + mbeanClass);
	}

        return getAttribute(instance, attribute, mbeanClass);
    }

 
    public AttributeList getAttributes(Object instance, String[] attributes) 
	throws ReflectionException {

	final Class mbeanClass = 
	    getMBeanInterfaceFromInstance(instance);

	if (isDebugOn()) {
	    debug("getAttributes","MBean Class is " + instance.getClass());
	    debug("getAttributes","MBean Interface is " + mbeanClass);
	}

        if (attributes == null) {
            throw new RuntimeOperationsException(new 
		IllegalArgumentException("Attributes cannot be null"), 
                "Exception occured trying to invoke the getter on the MBean");
        }

	// Go through the list of attributes
        //
        final int maxLimit = attributes.length;
	final AttributeList result = new AttributeList(maxLimit);

        for (int i=0;i<maxLimit;i++) { 
            final String elmt = (String)attributes[i];
            try {        
                final Object value = 
		    getAttribute(instance, elmt, mbeanClass);
                result.add(new Attribute(elmt, value));
            } catch (Exception excep) {
                if (isDebugOn()) {
                    debug("getAttributes", "Object= " + instance + 
			  ", Attribute=" + elmt + " failed: " + excep);
                }          
            }
        }
        return result;
    }


    public AttributeList setAttributes(Object instance, 
				       AttributeList attributes) 
	throws ReflectionException {
	
	final Class objClass       = instance.getClass();
        final Class mbeanClass     = getMBeanInterfaceFromInstance(instance);
	final ClassLoader aLoader  = objClass.getClassLoader();
	
	if (isDebugOn()) {
	    debug("setAttributes","MBean Class is " + instance.getClass());
	    debug("setAttributes","MBean Interface is " + mbeanClass);
	}

	if (attributes == null) return new AttributeList();

	final AttributeList result = new AttributeList(attributes.size());

	// Go through the list of attributes
        for (final Iterator i = attributes.iterator(); i.hasNext();) {
            final Attribute attr = (Attribute) i.next();
            final String id          = attr.getName();
            final Object value       = attr.getValue();          
            try {
                final Object newValue = 
		    setAttribute(instance, attr, mbeanClass);  
                if (isTraceOn()) {
                    trace("setAttributes", "Updating the list\n");
                }                                                
                result.add(new Attribute(id, newValue));
            } catch (Exception excep) {
                if (isDebugOn()) {
                    debug("setAttributes", "Unexpected exception occured: " +
			  excep.getClass().getName());
                }
            }
        }
        return result;	

    }
    
    public Object setAttribute(Object instance, Attribute attribute) 
	throws AttributeNotFoundException, InvalidAttributeValueException, 
	       MBeanException, ReflectionException {

        final Class mbeanClass = 
	    getMBeanInterfaceFromInstance(instance);

	if (isDebugOn()) {
	    debug("setAttribute","MBean Class is " + instance.getClass());
	    debug("setAttribute","MBean Interface is " + mbeanClass);
	}

	return setAttribute(instance,attribute,mbeanClass);
    }

    public Object invoke(Object instance, String operationName, 
			 Object params[], String signature[]) 
	throws  MBeanException, ReflectionException {

        if (operationName == null) {
	    final RuntimeException r = 
	      new IllegalArgumentException("Operation name cannot be null");
            throw new RuntimeOperationsException(r, 
              "Exception occured trying to invoke the operation on the MBean");
        } 

	final Class objClass = instance.getClass();
        final Class mbeanClass = getMBeanInterfaceFromInstance(instance);
	final ClassLoader aLoader = objClass.getClassLoader();

	if (isDebugOn()) {
	    debug("invoke","MBean Class is " + instance.getClass());
	    debug("invoke","MBean Interface is " + mbeanClass);
	}

        // Build the signature of the method
        //
        final Class[] tab = 
	    ((signature == null)?null:
	     findSignatureClasses(signature,aLoader));
	
        // Query the metadata service to get the right method
        //    
        Method mth= findMethod(mbeanClass, operationName, tab);
        
        if (mth == null) {
            if (isTraceOn()) {
                trace("invoke", operationName + " not found in class " +
		      mbeanClass.getName());
            }
            throw new ReflectionException(
		          new NoSuchMethodException(operationName), 
                          "The operation with name " + operationName + 
			  " could not be found");
        }

        // Make it impossible to call getters and setters through invoke()
        //
        forbidInvokeGetterSetter(mth, operationName);

        // invoke the method        
        if (isTraceOn()) {
            trace("invoke", "Invoking " + operationName);
        }           
        Object result=null;
        try {
            result= mth.invoke(instance, params); 
        } catch (IllegalAccessException e) {
	    debugX("invoke",e);
            throw new ReflectionException(e, "IllegalAccessException" + 
		   " occured trying to invoke operation " + operationName);
        } catch (RuntimeException e) {
	    debugX("invoke",e);
            throw new RuntimeOperationsException(e, "RuntimeException" + 
                   " occured trying to invoke operation " + operationName);
        } catch (InvocationTargetException e) {
            // Wrap the exception.         
            Throwable t = e.getTargetException();
	    debugX("invoke",t);
            if (t instanceof RuntimeException) {
		final String msg = "RuntimeException thrown in operation " +
		    operationName;
		throw wrapRuntimeException((RuntimeException) t, msg);
            } else if (t instanceof Error) {
                throw new RuntimeErrorException((Error) t, 
                   "Error thrown in operation " + operationName);
            } else {
                throw new MBeanException((Exception) t, 
                   "Exception thrown in operation " + operationName);
            }
        }
        if (isTraceOn()) {
            trace("invoke", "Send the result");
        }      
        return (result);
    }

    private static boolean startsWithAndHasMore(String s, String prefix) {
	return (s.startsWith(prefix) && s.length() > prefix.length());
    }

    private static void forbidInvokeGetterSetter(Method mth,
						 String operationName)
	    throws ReflectionException {

        final Class argTypes[] = mth.getParameterTypes();
        final Class resultType = mth.getReturnType();
        final int argCount = argTypes.length;

	boolean isInvokeGetterSetter = false;

	switch (argCount) {
	case 0: // might be a getter
	    if ((startsWithAndHasMore(operationName, "get") &&
		 resultType != Void.TYPE) ||
		(startsWithAndHasMore(operationName, "is") &&
		 resultType == Boolean.TYPE)) {
		// Operation is a getter
		isInvokeGetterSetter = true;
	    }
	    break;

	case 1: // might be a setter
	    if (startsWithAndHasMore(operationName, "set") &&
		resultType == Void.TYPE) {
		// Operation is a setter
		isInvokeGetterSetter = true;
	    }
	    break;
	}

	if (isInvokeGetterSetter) {
	    boolean allow;
	    try {
		GetPropertyAction getProp =
		    new GetPropertyAction("jmx.invoke.getters");
		allow = (AccessController.doPrivileged(getProp) != null);
	    } catch (SecurityException e) {
		// too bad, don't allow it
		allow = false;
	    }
	    if (!allow) {
		final String msg =
		    "Cannot invoke getter or setter (" + operationName +
		    ") as operation unless jmx.invoke.getters property is set";
		final Exception nested =
		    new NoSuchMethodException(operationName);
		throw new ReflectionException(nested, msg);
	    }
	}
    }

    public boolean isInstanceOf(Object instance, String className) 
	throws ReflectionException {
	
	final Class c = 
	    findClass(className, instance.getClass().getClassLoader());

	return c.isInstance(instance);
    }

   /** 
    * This methods returns the MBean interface of the given MBean
    * instance. 
    * <p>It does so by calling 
    * <code>getMBeanInterfaceFromClass(instance.getClass());</code>
    * @param instance the MBean instance.
    */ 
    Class getMBeanInterfaceFromInstance(Object instance) {
	if (instance == null) return null;
	return getMBeanInterfaceFromClass(instance.getClass());
    }

    /**
     * Cache the MBeanInfo and MBean interface obtained for class 
     * <var>c</var>.
     * <p>This method is called by <code>testCompliance(...)</code>
     * after compliance is successfully verified. It uses two 
     * {@link java.util.WeakHashMap WeakHashMaps} - one for the
     * MBeanInfo, one for the MBeanInterface, with calss <var>c</var>
     * as the key.
     *
     * @param c The concrete MBean class from which the MBeanInfo
     *        was be built.
     *
     * @param mbeanInterface The management interface of the MBean.
     *        Note that caching will not work if two MBeans of the same
     *        class can have different mbeanInterface's. If you want
     *        to use caching nonetheless, you will have to 
     *        to do it by redefining the method 
     *        {@link #getMBeanInterfaceFromInstance(java.lang.Object) 
     *                getMBeanInterfaceFromInstance()}.
     * @param info The MBeanInfo obtained from class <var>c</var> using
     *        interface <var>mbeanInterface</var>.
     *
     **/
    void cacheMBeanInfo(Class c, Class mbeanInterface, 
				  MBeanInfo info) 
	throws NotCompliantMBeanException {
	if (info != null) {
	    synchronized (mbeanInfoCache) {
		if (mbeanInfoCache.get(c) == null) {
		    mbeanInfoCache.put(c, info);
		}
	    }
	}
	if (mbeanInterface != null) {
	    synchronized (mbeanInterfaceCache) {
		if ((mbeanInterfaceCache.get(c) == null) || (((WeakReference)mbeanInterfaceCache.get(c)).get() == null)) {
		    mbeanInterfaceCache.put(c, new WeakReference(mbeanInterface));
		}
	    }
	}
    }
  
    /**
     * Returns the MBean interface that was cached for class <var>c</var>.
     * @param  c The concrete MBean class.
     * @return The cached MBean interface if found, null otherwise.
     **/
    Class getCachedMBeanInterface(Class c) {
	synchronized (mbeanInterfaceCache) {
	    return (Class)(((WeakReference)mbeanInterfaceCache.get(c)).get());
	}
    }
    
    /**
     * Returns the MBeanInfo that was cached for class <var>c</var>.
     * @param  c The concrete MBean class.
     * @return The cached MBeanInfo if found, null otherwise.
     **/
    MBeanInfo getCachedMBeanInfo(Class c) {
        synchronized (mbeanInfoCache) {
	    return (MBeanInfo)mbeanInfoCache.get(c);
	}
    }

    /**
     * Find a class using the specified ClassLoader.
     **/
    Class findClass(String className, ClassLoader loader) 
	throws ReflectionException {
	return MBeanInstantiatorImpl.loadClass(className, 
					       loader);
    }

    /**
     * Find the classes from a signature using the specified ClassLoader.
     **/
    Class[] findSignatureClasses(String[] signature, 
					   ClassLoader loader) 
	throws ReflectionException {
	return ((signature == null)?null:
		MBeanInstantiatorImpl.loadSignatureClasses(signature,loader));
    }

    /**
     * Invoke getAttribute through reflection on a standard MBean instance.
     **/
    Object getAttribute(Object instance, String attribute, 
				  Class mbeanClass)  
	throws MBeanException, AttributeNotFoundException, 
	       ReflectionException {

        if (attribute == null) {
	    final RuntimeException r = 
		new IllegalArgumentException("Attribute name cannot be null");
            throw new RuntimeOperationsException(r, 
                "Exception occured trying to invoke the getter on the MBean");
        }  
 
	// Standard MBean: need to reflect...
        Method meth = null;
        meth = findGetter(mbeanClass, attribute);
        if (meth == null) {
	    if (isTraceOn()) {
		trace("getAttribute", "Cannot find getter for "+attribute+
		      " in class " + mbeanClass.getName());
	    }                 
            throw new AttributeNotFoundException(attribute + 
						 " not accessible");
        }

        // Invoke the getter     
        if (isTraceOn()) {
            trace("getAttribute", "Invoke callback");
        }                 
        Object result= null;
        try {
            result = meth.invoke(instance, (Object[]) null);      
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof RuntimeException) {
		debugX("getAttribute",t);
		final String msg =
		    "RuntimeException thrown in the getter for the attribute "
		    + attribute;
		throw wrapRuntimeException((RuntimeException) t, msg);
            } else if (t instanceof Error) {
		debugX("getAttribute",t);
                throw new RuntimeErrorException((Error) t ,
		  "Error thrown in the getter for the attribute " + 
						attribute);    
            } else {
		debugX("getAttribute",t);
                throw new MBeanException((Exception) t, 
		  "Exception thrown in the getter for the attribute " + 
		  attribute);    
            }
        } catch (RuntimeException e) {
	    debugX("getAttribute",e);
            throw new RuntimeOperationsException(e, 
                  "RuntimeException thrown trying to invoke the getter" +
		  " for the attribute " + attribute);    
        } catch (IllegalAccessException e) {
	    debugX("getAttribute",e);
            throw new ReflectionException(e, "Exception thrown trying to" +
                  " invoke the getter for the attribute " + attribute);
        } catch (Error e) {
	    debugX("getAttribute",e);
            throw new RuntimeErrorException((Error)e, 
		  "Error thrown trying to invoke the getter " +
		  " for the attribute " + attribute);
        }

        if (isTraceOn()) {
            trace("getAttribute", attribute + "= " + result + "\n");
        }                 
        return result; 
    }

    /**
     * Invoke setAttribute through reflection on a standard MBean instance.
     **/
    Object setAttribute(Object instance, Attribute attribute, 
				  Class mbeanClass)  
	throws AttributeNotFoundException, InvalidAttributeValueException, 
	       MBeanException, ReflectionException {

        if (attribute == null) {
	    final RuntimeException r = 
		new IllegalArgumentException("Attribute name cannot be null");
            throw new RuntimeOperationsException(r, 
                "Exception occured trying to invoke the setter on the MBean");
        }  
 
	final Class objClass = instance.getClass();
	final ClassLoader aLoader = objClass.getClassLoader();

        Object result = null;
        final Object value   = attribute.getValue();
	final String attname = attribute.getName();

        // Query the metadata service to get the appropriate setter
        // of the object.
        Method meth = null;

	if (value == null) {
	    meth = findSetter(mbeanClass, attname);
	} else {
	    meth = findSetter(mbeanClass, attname, value.getClass());
	}
        if (meth == null) {       
            // Check whether the type is a primitive one       
            Class primClass = findPrimForClass(value);     
       
            if (primClass != null) {       
                meth = findSetter(mbeanClass, attname, primClass);     
            }
        }     
        if (meth == null) {
            // Try to check if the attribute name does correspond to a 
	    // valid property       
            meth= findSetter(mbeanClass, attname);
	    if (meth == null) {
		if (isTraceOn()) {
		    trace("setAttribute", "Cannot find setter for "+attribute+
			  " in class " + mbeanClass.getName());
		}                 
                throw new AttributeNotFoundException( attname + 
						      " not accessible");
            } else {
		final Object v = attribute.getValue();
		if (v == null) {
		    throw new InvalidAttributeValueException("attribute= " + 
                                attname + " value = null");
		} else {
		    throw new InvalidAttributeValueException("attribute= " + 
                                attname + " value = " + v);
		}
            }
        }     
        // Invoke the setter     
        if (isTraceOn()) {
            trace("setAttribute", "Invoking the set method for " + 
		  attname);
        }   
     
        final Object[] values = new Object[1];
        values[0] = value;

        try {
            result = meth.invoke(instance,values);
        } catch (IllegalAccessException e) {
	    debugX("setAttribute",e);
            // Wrap the exception.            
            throw new ReflectionException(e, "IllegalAccessException" + 
                          " occured trying to invoke the setter on the MBean");
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
	    debugX("setAttribute",t);
	    if (t instanceof RuntimeException) {
		final String msg =
		    "RuntimeException thrown in the setter for the attribute "
		    + attribute;
		throw wrapRuntimeException((RuntimeException) t, msg);
            } else if (t instanceof Error) {
                throw new RuntimeErrorException((Error) t, 
                           "Error thrown in the MBean's setter");   
            } else {
                throw new MBeanException((Exception) t, 
                           "Exception thrown in the MBean's setter");
            }
        }
        if (isTraceOn()) {
            trace("setAttribute", attname + "= " + value);
        }         
        return value;
    }


    /**
     * Returns the MBeanNotificationInfo of the MBeans that implement
     * the NotificationBroadcaster interface.
     */
    MBeanNotificationInfo[] findNotifications(Object moi) {
        
        if (moi instanceof javax.management.NotificationBroadcaster) {       
            MBeanNotificationInfo[] mbn = 
	    ((NotificationBroadcaster)moi).getNotificationInfo();
            if (mbn == null) {
                return new MBeanNotificationInfo[0];
            }
            MBeanNotificationInfo[] result = 
	    new MBeanNotificationInfo[mbn.length];
            for (int i = 0; i < mbn.length; i++) {
                result[i] = (MBeanNotificationInfo) mbn[i].clone();
            }
            return result;
        }
        return new MBeanNotificationInfo[0];
    }

    /**
     * Finds a specific method of an object.
     * Returns the method or null if not found
     */    
    public static Method findMethod(Class classObj, String name,
			     Class parameterTypes[]) {
	Method method=null;   
	try {
	    method= classObj.getMethod(name, parameterTypes); 
	} catch(Exception e) {
	    // OK: will return null.
	}
	
	return method;
    }
    
    /**
     * Finds a specific method of an object without knowing the parameter 
     * types.
     * Returns the method or null if not found
     */
    public static Method findMethod(Class classObj, String name) {
	Method method = null ;
	
	try {
	    Method[] methods=classObj.getMethods();
	    int i = 0;
	    while ((i < methods.length) && 
		   !methods[i].getName().equals(name)) {
		i++;
	    }
	    if (i < methods.length) { 
		method = methods[i];
	    }
	} catch(Exception e) {
	    // OK: will return null.
	}
	return method;
    }

 
    /**
    * Finds a specific method of an object given the number of parameters.
    * Returns the method or null if not found
    */
    public static Method findMethod(Class classObj, String name, 
				    int paramCount) {
 
	Method method = null;
	try {
            Method[] methods=classObj.getMethods();
            int i = 0;
            boolean found = false;
            while ((i < methods.length) && !found) {
                found = methods[i].getName().equals(name);
                if (found) { // Now check if the number of parameters
                    found = (methods[i].getParameterTypes().length == 
			     paramCount);
                }
                i++;
            }
            if (found) { 
                method = methods[i-1] ; // Note i-1 !
            }
        } catch(Exception e) {
	    // OK: will return null;
        }
        return method;
    }
    
   
    /**
     * Finds the getter of a specific attribute in an object.
     * Returns the method for accessing the attributes, null otherwise
     */
    public static Method findGetter(Class classObj, String attribute)  {
	// Methods called "is" or "get" tout court are not getters
	if (attribute.length() == 0)
	    return null;

	// Look for a method T getX(), where T is not void

	Method m = findMethod(classObj, "get" + attribute, null);
	if (m != null && m.getReturnType() != void.class)
	    return m;


	// Look for a method boolean isX()
	// must not be any other type than "boolean", including not "Boolean"

	m = findMethod(classObj, "is" + attribute, null);
	if (m != null && m.getReturnType() == boolean.class)
	    return m;

	return null;
    }
    
   
    /**
     * Finds the setter of a specific attribute in an object.
     * Returns the method for accessing the attribute, null otherwise
     */
    public static Method findSetter(Class classObj, String attribute, 
				    Class type)  {	

	Method mth= findMethod(classObj, "set" + attribute, 1);
	if (mth != null) {
	    Class[] pars = mth.getParameterTypes();
	    if (pars[0].isAssignableFrom(type)) {
		return mth;
	    }
	}
	return null;
    }   

    /**
     * Finds the setter of a specific attribute without knowing its type.   
     * Returns the method for accessing the attribute, null otherwise
     */    
    public static Method findSetter(Class classObj, String attribute)  {
	return findMethod(classObj, "set" + attribute, 1) ;
    }
      
   /**
    * Finds a specific constructor of a class
    * Returns the requested constructor or null if not found
    */
    public static Constructor findConstructor(Class theClass,
					      Class parameterTypes[]) {
	// Get the list of methods		
	Constructor mth = null;
	
	try {
	    mth = theClass.getConstructor(parameterTypes);
	} catch(Exception e) {
	    return null;
	}
	return mth;
    }
   
    /** 
     * Get the class of the constructed type 
     * corresponding to the given primitive type
     */    
    public static Class findClassForPrim(String primName) {
	return (Class) primitiveClasses.get(primName);
    }
      
    /**
     * Get the class of the primitive type 
     * corresponding to the given constructed object.
     */
    public static Class findPrimForClass(Object value) {
	if (value instanceof Boolean)
	    return Boolean.TYPE;
	else if (value instanceof Character)
	    return Character.TYPE;
	else if (value instanceof Byte)
	    return Byte.TYPE;
	else if (value instanceof Short)
	    return Short.TYPE;
	else if (value instanceof Integer)
	    return Integer.TYPE;
	else if (value instanceof Long)
	 return Long.TYPE;
	else if (value instanceof Float)
	    return Float.TYPE;
	else if (value instanceof Double)
	    return Double.TYPE;
	return null;
    }

    /**
     * Converts the array of classes to an array of class signatures.
     */
    static String[] findSignatures(Class[] clz) {
        String signers[] = new String[clz.length];
        for (int i = 0; i < clz.length; i++) {
            signers[i] = findSignature(clz[i]);
        }
        return signers;
    }
    
    /**
     * Converts the class to a class signature.
     */
    static String findSignature(Class clz) {
        return clz.getName();
    }

    private RuntimeException wrapRuntimeException(RuntimeException re,
						  String msg) {
	if (wrapRuntimeExceptions)
	    return new RuntimeMBeanException(re, msg);
	else
	    return re;
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
	    // 			     func+"(): "+e);
	    // java.lang.System.err.println(stack);
	}
    }
    
}
