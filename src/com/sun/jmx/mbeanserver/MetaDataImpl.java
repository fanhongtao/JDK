/*
 * @(#)MetaDataImpl.java	1.28 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.jmx.mbeanserver;



// java import

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.Iterator;
import java.io.PrintWriter;
import java.io.StringWriter;

// RI import
import javax.management.* ; 
import com.sun.jmx.trace.Trace; 

/**
 * The MetaData class provides local access to the metadata service in
 * an agent.
 *
 * @since 1.5
 * @since.unbundled JMX RI 1.2
 */
public class MetaDataImpl implements MetaData {

    /** The name of this class to be used for tracing */
    private final static String dbgTag = "MetaDataImpl";

    /** MetaData for DynamicMBeans **/
    private final DynamicMetaDataImpl  dynamic;
    private final StandardMetaDataImpl standard;

    /**
     * The MBeanInstantiator associated to the MetaData
     */
    protected final MBeanInstantiator instantiator;

    // Not sure we need this...
    private final class PrivateStandardMeta extends StandardMetaDataImpl {
	PrivateStandardMeta() {
	    super();
	}
	// public synchronized void testCompliance(Class c) {
	//    MetaDataImpl.this.testStrictCompliance(c);
	// }
	protected Class findClass(String className, ClassLoader loader) 
	    throws ReflectionException {
	    return MetaDataImpl.this.findClass(className,loader);
	}
	protected Class[] findSignatureClasses(String[] signature, 
					       ClassLoader loader) 
	    throws ReflectionException {
	    return MetaDataImpl.this.findSignatureClasses(signature,loader);
	}
	
    }

    // Not sure we need this...
    private final class PrivateDynamicMeta extends DynamicMetaDataImpl {
	PrivateDynamicMeta() {
	    super();
	}
	// public synchronized void testCompliance(Class c) {
	//    MetaDataImpl.this.testStrictCompliance(c);
	// }
	protected Class findClass(String className, ClassLoader loader) 
	    throws ReflectionException {
	    return MetaDataImpl.this.findClass(className,loader);
	}
	protected Class[] findSignatureClasses(String[] signature, 
					       ClassLoader loader) 
	    throws ReflectionException {
	    return MetaDataImpl.this.findSignatureClasses(signature,loader);
	}
	
    }

    /**
     * Creates a Metadata Service.
     * @param instantiator The MBeanInstantiator that will be used to
     *        take care of class loading issues.
     *        This parameter may not be null.
     * @exception IllegalArgumentException if the instantiator is null.
     */
    public MetaDataImpl(MBeanInstantiator instantiator)  {
	if (instantiator == null) throw new 
	    IllegalArgumentException("instantiator must not be null.");
	this.instantiator = instantiator;
	this.dynamic  = new PrivateDynamicMeta();
	this.standard = new PrivateStandardMeta();
	// ------------------------------ 
	// ------------------------------ 
    } 


    protected MetaData getMetaData(Class c) {
	if (DynamicMBean.class.isAssignableFrom(c)) 
	    return dynamic;
	else
	    return standard;
    }

    protected MetaData getMetaData(Object moi) {
	if (moi instanceof DynamicMBean) 
	    return dynamic;
	else
	    return standard;
    }

    /** 
     * This methods tests if the MBean is JMX compliant
     */    
    public synchronized void testCompliance(Class c) 
	throws NotCompliantMBeanException {
	final  MetaData meta = getMetaData(c);
	meta.testCompliance(c);
    }

  
    /** 
     * This methods returns the MBean interface of an MBean
     */    
    public Class getMBeanInterfaceFromClass(Class c) {
	return standard.getMBeanInterfaceFromClass(c);
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
	return standard.getMBeanInfoFromClass(beanClass);
    } 
       

    //---------------------------------------------------------------------
    //
    // From the MetaData interface
    //
    //---------------------------------------------------------------------

    public final String getMBeanClassName(Object moi) 
	throws IntrospectionException, NotCompliantMBeanException {
	final  MetaData meta = getMetaData(moi);
	return meta.getMBeanClassName(moi);
    }

    public final MBeanInfo getMBeanInfo(Object moi) 
	throws IntrospectionException {
	final  MetaData meta = getMetaData(moi);
	return meta.getMBeanInfo(moi);
    }

    public final Object getAttribute(Object instance, String attribute)  
	throws MBeanException, AttributeNotFoundException, 
	       ReflectionException {
	
	final  MetaData meta = getMetaData(instance);
	return meta.getAttribute(instance,attribute);
    }

    public final AttributeList getAttributes(Object instance, 
					     String[] attributes) 
	throws ReflectionException {

	final  MetaData meta = getMetaData(instance);
	return meta.getAttributes(instance, attributes);
    }

    public final AttributeList setAttributes(Object instance, 
				       AttributeList attributes) 
	throws ReflectionException {
	
	final  MetaData meta = getMetaData(instance);
	return meta.setAttributes(instance,attributes);
    }
    

    public final Object setAttribute(Object instance, Attribute attribute) 
	throws AttributeNotFoundException, InvalidAttributeValueException, 
	       MBeanException, ReflectionException {

	final  MetaData meta = getMetaData(instance);
	return meta.setAttribute(instance,attribute);
    }

    public final Object invoke(Object instance, String operationName, 
			 Object params[], String signature[]) 
	throws  MBeanException, ReflectionException {

        if (operationName == null) {
	    final RuntimeException r = 
	      new IllegalArgumentException("Operation name cannot be null");
            throw new RuntimeOperationsException(r, 
              "Exception occured trying to invoke the operation on the MBean");
        } 
	final  MetaData meta = getMetaData(instance);
	return meta.invoke(instance,operationName,params,signature);
    }

    public boolean isInstanceOf(Object instance, String className) 
	throws ReflectionException {

	// XXX revisit here: ModelMBean ???
	final MetaData meta  = getMetaData(instance);
	return meta.isInstanceOf(instance,className);
    }

    public ObjectName preRegisterInvoker(Object moi, ObjectName name, 
					 MBeanServer mbs) 
	throws InstanceAlreadyExistsException, MBeanRegistrationException {
   
	if (!(moi instanceof MBeanRegistration)) return name;
	final MetaData meta  = getMetaData(moi);
	return meta.preRegisterInvoker(moi,name,mbs);
    }
   
    public void postRegisterInvoker(Object moi, boolean registrationDone) {
	if (!(moi instanceof MBeanRegistration)) return;

	final MetaData meta  = getMetaData(moi);
	meta.postRegisterInvoker(moi,registrationDone);
    }
    
    public void preDeregisterInvoker(Object moi) 
	throws MBeanRegistrationException {
	if (!(moi instanceof MBeanRegistration)) return;
	final MetaData meta  = getMetaData(moi);
	meta.preDeregisterInvoker(moi);
    }

  
    public void postDeregisterInvoker(Object moi) {
	if (!(moi instanceof MBeanRegistration)) return;
	final MetaData meta  = getMetaData(moi);
	meta.postDeregisterInvoker(moi);
    }

    /**
     * Find a class using the specified ClassLoader.
     **/
    protected Class findClass(String className, ClassLoader loader) 
	throws ReflectionException {
	return instantiator.findClass(className, loader);
    }

    /**
     * Find the classes from a signature using the specified ClassLoader.
     **/
    protected Class[] findSignatureClasses(String[] signature, 
					   ClassLoader loader) 
	throws ReflectionException {
	return ((signature == null)?null:
		instantiator.findSignatureClasses(signature,loader));
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
