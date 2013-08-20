/*
 * @(#)MetaData.java	1.54 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.jmx.mbeanserver;

import javax.management.* ; 
 

/**
 * The MetaData interface provides local to the metadata service in
 * an agent.
 *
 * @since 1.5
 * @since.unbundled JMX RI 1.2
 */
public interface MetaData {
    
    /** 
     * This methods tests if the MBean is JMX compliant
     */    
    public void testCompliance(Class c) throws NotCompliantMBeanException;

    /**
     * Invokes the  preRegister method  of an MBean that implements 
     * MBeanRegistration
     */
    public ObjectName preRegisterInvoker(Object moi, ObjectName name, 
					 MBeanServer mbs) 
	throws InstanceAlreadyExistsException, MBeanRegistrationException;

    /**
     * Invokes the  postRegister method  of an MBean that implements 
     * MBeanRegistration
     */
    public void postRegisterInvoker(Object moi, boolean registrationDone);

    
    /**
     * Invokes the  preDeregister method  of an MBean that implements 
     * MBeanRegistration
     */
    public void preDeregisterInvoker(Object moi) 
	throws MBeanRegistrationException;

    /**
     * Invokes the  postDeregister method  of an MBean that implements 
     * MBeanRegistration
     */
    public void postDeregisterInvoker(Object moi);


    /**
     * This method discovers the attributes and operations that an MBean 
     * exposes for management.
     *
     * @param instance The MBean whose class is to be analyzed.
     *
     * @return  An instance of MBeanInfo allowing to retrieve all methods 
     *          and operations of this MBean.
     *
     * @exception IntrospectionException if an exception occurs during 
     *             introspection.
     *
     */
    public MBeanInfo getMBeanInfo(Object instance) 
	throws IntrospectionException ;
       
    
    /**
     * This method returns the class name of an MBean.
     *
     * @param instance The MBean whose class is to be analyzed.
     *
     * @return The class name of the MBean, as registered in its MBeanInfo. 
     *
     * @exception IntrospectionException if an exception occurs during 
     *             introspection.
     *
     */
    public String getMBeanClassName(Object instance) 
	throws IntrospectionException, NotCompliantMBeanException ;
       
    
    /**
     * Gets the value of a specific attribute of an MBean. 
     *
     * @param instance The MBean from which the attribute is to be retrieved.
     * @param attribute An String specifying the name of the attribute to be
     * retrieved.
     *
     * @return  The value of the retrieved attribute.
     *
     * @exception AttributeNotFoundException The specified attribute is 
     *            not accessible in the MBean.
     * @exception MBeanException  Wraps an exception thrown by the MBean's 
     *            getter.
     * @exception ReflectionException  Wraps a java.lang.Exception thrown 
     *            while trying to invoke the getter.   
     */    
    public Object getAttribute(Object instance, String attribute)  
	throws MBeanException, AttributeNotFoundException, ReflectionException;


    /**
     * Enables the values of several attributes of an MBean.
     *
     * @param instance The MBean from which the attributes are to be retrieved.
     * @param attributes A list of the attributes to be retrieved.
     *
     * @return The list of the retrieved attributes.
     *
     * @exception ReflectionException An exception occurred when trying to invoke the getAttributes method of a Dynamic MBean.
     *
     */
      public AttributeList getAttributes(Object instance, String[] attributes)
        throws ReflectionException ;


    /**
     * Sets the value of a specific attribute of an MBean. 
     *
     * @param instance The MBean within which the attribute is to be set.
     * @param attribute The identification of the attribute to be set and 
     *        the value it is to be set to.
     *
     * @return  The value of the attribute that has been set.
     *
     * @exception AttributeNotFoundException  The specified attribute is 
     *            not accessible in the MBean.
     * @exception InvalidAttributeValueException The specified value for 
     *            the attribute is not valid.
     * @exception MBeanException Wraps an exception thrown by the MBean's 
     *            setter.
     * @exception ReflectionException  Wraps a java.lang.Exception thrown 
     *            while trying to invoke the setter. 
     */
    public Object setAttribute(Object instance, Attribute attribute) 
	throws AttributeNotFoundException, InvalidAttributeValueException, 
	       MBeanException, ReflectionException;
  

    /**
     * Sets the values of several attributes of an MBean.
     *
     * @param instance The MBean within which the attributes are to be set.
     * @param attributes A list of attributes: The identification of the
     * attributes to be set and  the values they are to be set to.
     *
     * @return  The list of attributes that were set, with their new values.
     *
     * @exception ReflectionException An exception occurred when trying to 
     *            invoke the getAttributes method of a Dynamic MBean.
     *
     */
    public AttributeList setAttributes(Object instance, 
				       AttributeList attributes)
        throws ReflectionException;


    /**
     * Invokes an operation on an MBean.
     *
     * @param instance The MBean on which the method is to be invoked.
     * @param operationName The name of the operation to be invoked.
     * @param params An array containing the parameters to be set when the operation is
     * invoked
     * @param signature An array containing the signature of the operation. The class objects will
     * be loaded using the same class loader as the one used for loading the
     * MBean on which the operation was invoked.
     *
     * @return  The object returned by the operation, which represents the result of
     * invoking the operation on the MBean specified.
     *
     * @exception MBeanException  Wraps an exception thrown by the MBean's invoked method.
     * @exception ReflectionException  Wraps a java.lang.Exception thrown while trying to invoke the method.
     */
    public Object invoke(Object instance, String operationName, 
			 Object params[],String signature[]) 
	throws  MBeanException, ReflectionException;         
 
    /**
     * Determine whether the given MBean is an instance of a given
     * class/interface.
     * 
     * @param instance The MBean concerned.
     * @param className The name of the class or interface.
     * @return <code>true</code> if the MBean is an instance of the
     *         given <code>class</code>, <code>false</code> otherwise.
     * @exception ReflectionException if 
     **/
    public boolean isInstanceOf(Object instance, String className)
	throws ReflectionException;

}
