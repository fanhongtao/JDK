/*
 * @(#)MBeanInstantiator.java	1.25 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.jmx.mbeanserver;

import javax.management.*; 
import java.io.ObjectInputStream;

/**
 * Contains methods for instantiating objects, finding the class given
 * its name and using different class loaders, deserializing objects
 * in the context of a given class loader.
 *
 * @since 1.5
 * @since.unbundled JMX RI 1.2
 */
public interface MBeanInstantiator {

    /** 
     * This methods tests if the MBean class makes it possible to 
     * instantiate an MBean of this class in the MBeanServer.
     * e.g. it must have a public constructor, be a concrete class...
     */    
    public void testCreation(Class c) throws NotCompliantMBeanException;

    /**
     * Loads the class with the specified name using this object's 
     * Default Loader Repository. 
     **/
    public Class findClassWithDefaultLoaderRepository(String className) 
	throws ReflectionException;


    /**
     * Return the Default Loader Repository used by this instantiator object.
     **/
    public ModifiableClassLoaderRepository getClassLoaderRepository();

    /**
     * Gets the class for the specified class name using the MBean 
     * Interceptor's classloader
     */
    public Class findClass(String className, ClassLoader loader) 
	throws ReflectionException;

    /**
     * Gets the class for the specified class name using the specified 
     * class loader
     */
    public Class findClass(String className, ObjectName loaderName) 
        throws ReflectionException, InstanceNotFoundException ;

    /**
     * Return an array of Class corresponding to the given signature, using
     * the specified class loader.
     */
    public Class[] findSignatureClasses(String signature[],
					ClassLoader loader)
	throws ReflectionException;
    /**
     * De-serializes a byte array in the context of a classloader.
     *
     * @param loader the classloader to use for de-serialization
     * @param data The byte array to be de-sererialized.
     *
     * @return  The de-serialized object stream.
     *
     * @exception OperationsException Any of the usual Input/Output related
     * exceptions.
     */
    public ObjectInputStream deserialize(ClassLoader loader, byte[] data)
	throws OperationsException;

    /**
     * De-serializes a byte array in the context of a given MBean class loader.
     * <P>The class loader is the one that loaded the class with name
     * "className".
     * <P>The name of the class loader to be used for loading the specified
     * class is specified. If null, a default one has to be provided (for a
     * MBean Server, its own class loader will be used).
     *
     * @param className The name of the class whose class loader should 
     *  be used for the de-serialization.
     * @param data The byte array to be de-sererialized.
     * @param loaderName The name of the class loader to be used for loading
     * the specified class. If null, a default one has to be provided (for a
     * MBean Server, its own class loader will be used).
     *
     * @return  The de-serialized object stream.
     *
     * @exception InstanceNotFoundException The specified class loader MBean is
     * not found.          
     * @exception OperationsException Any of the usual Input/Output related
     * exceptions.
     * @exception ReflectionException The specified class could not be loaded
     * by the specified class loader.
     */
    public ObjectInputStream deserialize(String className,
					 ObjectName loaderName,
					 byte[] data,
					 ClassLoader loader)
	throws InstanceNotFoundException,
	       OperationsException,
	       ReflectionException;

    /**
     * Instantiates an object using the list of all class loaders registered
     * in the MBean Interceptor
     * (using its {@link javax.management.loading.ClassLoaderRepository}).
     * <P>The object's class should have a public constructor.
     * <P>It returns a reference to the newly created object.
     * <P>The newly created object is not registered in the MBean Interceptor.
     *
     * @param className The class name of the object to be instantiated.    
     *
     * @return The newly instantiated object.    
     *
     * @exception ReflectionException Wraps a
     * <CODE>java.lang.ClassNotFoundException</CODE> or the
     * <CODE>java.lang.Exception</CODE> that occurred when trying to invoke the
     * object's constructor.
     * @exception MBeanException The constructor of the object has thrown an
     * exception
     * @exception RuntimeOperationsException Wraps a
     * <CODE>java.lang.IllegalArgumentException</CODE>: the className passed in
     * parameter is null.
     */
    public Object instantiate(String className)
	throws ReflectionException,
	       MBeanException;

    /**
     * Instantiates an object using the class Loader specified by its
     * <CODE>ObjectName</CODE>.
     * <P>If the loader name is null, a default one has to be provided (for a
     * MBean Server, the ClassLoader that loaded it will be used).
     * <P>The object's class should have a public constructor.
     * <P>It returns a reference to the newly created object.
     * <P>The newly created object is not registered in the MBean Interceptor.
     *
     * @param className The class name of the MBean to be instantiated.    
     * @param loaderName The object name of the class loader to be used.
     *
     * @return The newly instantiated object.    
     *
     * @exception ReflectionException Wraps a
     * <CODE>java.lang.ClassNotFoundException</CODE> or the
     * <CODE>java.lang.Exception</CODE> that occurred when trying to invoke the
     * object's constructor.
     * @exception MBeanException The constructor of the object has thrown an
     * exception.
     * @exception InstanceNotFoundException The specified class loader is not
     * registered in the MBeanServerInterceptor.
     * @exception RuntimeOperationsException Wraps a
     * <CODE>java.lang.IllegalArgumentException</CODE>: the className passed in
     * parameter is null.
     */
    public Object instantiate(String className, ObjectName loaderName, ClassLoader loader) 
        throws ReflectionException,
	       MBeanException,
	       InstanceNotFoundException;

    /**
     * Instantiates an object using the list of all class loaders registered
     * in the MBean server
     * (using its {@link javax.management.loading.ClassLoaderRepository}).
     * <P>The object's class should have a public constructor.
     * <P>The call returns a reference to the newly created object.
     * <P>The newly created object is not registered in the MBean Interceptor.
     *
     * @param className The class name of the object to be instantiated.
     * @param params An array containing the parameters of the constructor to
     * be invoked.
     * @param signature An array containing the signature of the constructor to
     * be invoked.     
     *
     * @return The newly instantiated object.    
     *
     * @exception ReflectionException Wraps a
     * <CODE>java.lang.ClassNotFoundException</CODE> or the
     * <CODE>java.lang.Exception</CODE> that occurred when trying to invoke the
     * object's constructor.  
     * @exception MBeanException The constructor of the object has thrown an
     * exception
     * @exception RuntimeOperationsException Wraps a
     * <CODE>java.lang.IllegalArgumentException</CODE>: the className passed in
     * parameter is null.
     */    
    public Object instantiate(String className,
			      Object params[],
			      String signature[],
			      ClassLoader loader) 
        throws ReflectionException,
	       MBeanException ; 


    /**
     * Instantiates an object. The class loader to be used is identified by its
     * object name.
     * <P>If the object name of the loader is null, a default has to be
     * provided (for example, for a MBean Server, the ClassLoader that loaded
     * it will be used).
     * <P>The object's class should have a public constructor.
     * <P>The call returns a reference to the newly created object.
     * <P>The newly created object is not registered in the MBean server.
     *
     * @param className The class name of the object to be instantiated.
     * @param params An array containing the parameters of the constructor to
     * be invoked.
     * @param signature An array containing the signature of the constructor to
     * be invoked.     
     * @param loaderName The object name of the class loader to be used.
     *
     * @return The newly instantiated object.    
     *
     * @exception ReflectionException Wraps a
     * <CODE>java.lang.ClassNotFoundException</CODE> or the
     * <CODE>java.lang.Exception</CODE> that occurred when trying to invoke the
     * object's constructor.  
     * @exception MBeanException The constructor of the object has thrown an
     * exception
     * @exception InstanceNotFoundException The specified class loader is not
     * registered in the MBean Interceptor. 
     * @exception RuntimeOperationsException Wraps a
     * <CODE>java.lang.IllegalArgumentException</CODE>: the className passed in
     * parameter is null.
     */    
    public Object instantiate(String className,
			      ObjectName loaderName,
			      Object params[],
			      String signature[],
			      ClassLoader loader) 
        throws ReflectionException,
	       MBeanException,
	       InstanceNotFoundException;


    /**
     * Instantiates an object given its class, using its empty constructor.
     * The call returns a reference to the newly created object.
     */
    public Object instantiate(Class theClass) throws ReflectionException, MBeanException;


   /**
     * Instantiates an object given its class, the parameters and signature of its constructor
     * The call returns a reference to the newly created object.
     */
    public Object instantiate(Class theClass, Object params[], String signature[], ClassLoader loader) throws ReflectionException, MBeanException;
}
