/*
 * @(#)Introspector.java	1.68 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.jmx.mbeanserver;


// Java import
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

// RI Import
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.NotCompliantMBeanException;


/**
 * This class contains the methods for performing all the tests needed to verify
 * that a class represents a JMX compliant MBean.
 *
 * @since 1.5
 */
public class Introspector {
    

    /*
     * ------------------------------------------
     *  PRIVATE VARIABLES
     * ------------------------------------------
     */

    private static final String attributeDescription =
	"Attribute exposed for management";
    private static final String operationDescription =
	"Operation exposed for management";
    private static final String constructorDescription =
	"Public constructor of the MBean";
    private static final String mbeanInfoDescription =
	"Information on the management interface of the MBean";
    

     /*
     * ------------------------------------------
     *  PRIVATE CONSTRUCTORS
     * ------------------------------------------
     */

    // private constructor defined to "hide" the default public constructor
    private Introspector() {

	// ------------------------------ 
	// ------------------------------
	
    }
    
    /*
     * ------------------------------------------
     *  PUBLIC METHODS
     * ------------------------------------------
     */

    /**
     * Tell whether a MBean of the given class is a Dynamic MBean.
     * This method does nothing more than returning
     * <pre>
     * javax.management.DynamicMBean.class.isAssignableFrom(c)
     * </pre>
     * This method does not check for any JMX MBean compliance:
     * <ul><li>If <code>true</code> is returned, then instances of 
     *     <code>c</code> are DynamicMBean.</li>
     *     <li>If <code>false</code> is returned, then no further
     *     assumption can be made on instances of <code>c</code>.
     *     In particular, instances of <code>c</code> may, or may not
     *     be JMX standard MBeans.</li>
     * </ul>
     * @param c The class of the MBean under examination.
     * @return <code>true</code> if instances of <code>c</code> are
     *         Dynamic MBeans, <code>false</code> otherwise. 
     *
     * @since.unbundled JMX RI 1.2
     **/
    public static final boolean isDynamic(final Class c) {
	// Check if the MBean implements the DynamicMBean interface
	return javax.management.DynamicMBean.class.isAssignableFrom(c);
    }

    /**
     * Basic method for testing that a MBean of a given class can be
     * instantiated by the MBean server.<p>
     * This method checks that:
     * <ul><li>The given class is a concrete class.</li>
     *     <li>The given class exposes at least one public constructor.</li>
     * </ul>
     * If these conditions are not met, throws a NotCompliantMBeanException.
     * @param c The class of the MBean we want to create.
     * @exception NotCompliantMBeanException if the MBean class makes it
     *            impossible to instantiate the MBean from within the
     *            MBeanServer.
     *
     * @since.unbundled JMX RI 1.2
     **/
    public static void testCreation(Class c) 
	throws NotCompliantMBeanException {
	// Check if the class is a concrete class
	final int mods = c.getModifiers(); 
	if (Modifier.isAbstract(mods) || Modifier.isInterface(mods)) {
	    throw new NotCompliantMBeanException("MBean class must be concrete");
	}

	// Check if the MBean has a public constructor 
	final Constructor[] consList = c.getConstructors();     
	if (consList.length == 0) {
	    throw new NotCompliantMBeanException("MBean class must have public constructor");
	}
    }
    
    /**
     * Basic method for testing if a given class is a JMX compliant MBean.
     *
     * @param baseClass The class to be tested
     *
     * @return <code>null</code> if the MBean is a DynamicMBean, 
     *         the computed {@link javax.management.MBeanInfo} otherwise.
     * @exception NotCompliantMBeanException The specified class is not a 
     *            JMX compliant MBean
     */    
    public static MBeanInfo testCompliance(Class baseClass) 
	throws NotCompliantMBeanException {

	// ------------------------------ 
	// ------------------------------
	
	// Check if the MBean implements the MBean or the Dynamic 
	// MBean interface
	if (isDynamic(baseClass)) 
	    return null;
	
	return testCompliance(baseClass, null);
    }

    
    /**
     * Basic method for testing if a given class is a JMX compliant MBean.
     *
     * @param baseClass The class to be tested
     *
     * @return <code>null</code> if the MBean is a DynamicMBean, 
     *         the computed {@link javax.management.MBeanInfo} otherwise.
     * @exception NotCompliantMBeanException The specified class is not a 
     *            JMX compliant MBean
     */    
    static MBeanInfo testCompliance(final Class baseClass,
				    Class mbeanInterface) 
	    throws NotCompliantMBeanException {
	
	if (baseClass.isInterface()) 
	    throw new NotCompliantMBeanException(baseClass.getName() + 
						 " must be a class.");
	// ------------------------------ 
	// ------------------------------
	if (mbeanInterface == null)
	    // No interface specified: look for default MBean interface.
	    mbeanInterface = getStandardMBeanInterface(baseClass);
	else if (! mbeanInterface.isAssignableFrom(baseClass)) {
	    // specified interface not implemented by given class
	    final String msg =
		baseClass.getName() + " does not implement the " + 
		mbeanInterface.getName() + " interface";
	    throw new NotCompliantMBeanException(msg);
	} else if (! mbeanInterface.isInterface()) {
	    // Base class X, but XMBean is not an interface
	    final String msg =
		baseClass.getName() + ": " + mbeanInterface.getName() + 
		" is not an interface";
	    throw new NotCompliantMBeanException(msg);
	}


	if (mbeanInterface == null) {
	    // Error: MBean does not implement javax.management.DynamicMBean 
	    // nor MBean interface
	    final String baseClassName = baseClass.getName();
	    final String msg =
		baseClassName + " does not implement the " + baseClassName +
		"MBean interface or the DynamicMBean interface";
	    throw new NotCompliantMBeanException(msg);
	}
	
	final int mods = mbeanInterface.getModifiers();
	if (!Modifier.isPublic(mods)) 
	    throw new NotCompliantMBeanException(mbeanInterface.getName() + 
						 " implemented by " +
						 baseClass.getName() + 
						 " must be public");

	return (introspect(baseClass, mbeanInterface));
    }

    
    /**
     * Get the MBean interface implemented by a JMX standard MBean 
     * class.
     *
     * @param baseClass The class to be tested
     * 
     * @return The MBean interface implemented by the MBean. 
     *         Return <code>null</code> if the MBean is a DynamicMBean, 
     *         or if no MBean interface is found.
     *
     */
    public static Class getMBeanInterface(Class baseClass) {

	// ------------------------------ 
	// ------------------------------
	
	// Check if the MBean implements the MBean or the Dynamic 
	// MBean interface
	if (isDynamic(baseClass)) return null;

	return getStandardMBeanInterface(baseClass);     
    }
    
    /**
     * Get the MBean interface implemented by a JMX standard MBean 
     * class.
     *
     * @param baseClass The class to be tested
     * 
     * @return The MBean interface implemented by the MBean. 
     *         Return <code>null</code> if no MBean interface is found.
     *         Does not check whether the MBean is a DynamicMBean.
     *
     */
    static Class getStandardMBeanInterface(Class baseClass) {

	// ------------------------------ 
	// ------------------------------
	
	Class current = baseClass;
	Class mbeanInterface = null;
	
	while (current != null) {
	    mbeanInterface = 
		findMBeanInterface(current, current.getName());
	    if (mbeanInterface != null) break;
	    current = current.getSuperclass();
	}
	return mbeanInterface;
    }

    /*
     * ------------------------------------------
     *  PRIVATE METHODS
     * ------------------------------------------
     */
    

    /**
     * Try to find the MBean interface corresponding to the class aName 
     * - i.e. <i>aName</i>MBean, from within aClass and its superclasses.
     **/
    private static Class findMBeanInterface(Class aClass, String aName) {
	Class current = aClass;
	while (current != null) {
	    final Class[] interfaces = current.getInterfaces();   
	    final int len = interfaces.length;
	    for (int i=0;i<len;i++)  {	     
		final Class inter = 
		    implementsMBean(interfaces[i], aName);
		if (inter != null) return inter;
	    }
	    current = current.getSuperclass();
	}
	return null;     
    }


    /**
     * Discovers the getters, setters, operations of the class
     *
     * @param baseClass The XX base class.
     * @param beanClass The XXMBean interface implemented by the tested class.
     *
     * @exception NotCompliantMBeanException The tested class is not a
     * JMX compliant MBean
     */    
    private static MBeanInfo introspect(Class baseClass, Class beanClass)
	    throws NotCompliantMBeanException {

	// ------------------------------ 
	// ------------------------------

	List/*<MBeanAttributeInfo>*/ attributes =
	    new ArrayList/*<MBeanAttributeInfo>*/();
	List/*<MBeanOperationInfo>*/ operations =
	    new ArrayList/*<MBeanOperationInfo>*/();
	
	Method methodList[] = beanClass.getMethods();
	
	// Now analyze each method.        
	for (int i = 0; i < methodList.length; i++) { 
	    Method method = methodList[i];
	    String name = method.getName();            
	    Class argTypes[] = method.getParameterTypes();
	    Class resultType = method.getReturnType();
	    int argCount = argTypes.length;

	    try {
		final MBeanAttributeInfo attr;

		if (name.startsWith("get") && !name.equals("get")
		    && argCount == 0 && !resultType.equals(void.class)) {
		    // if the method is "T getX()" it is a getter
		    attr = new MBeanAttributeInfo(name.substring(3),
						  attributeDescription,
						  method, null);
		} else if (name.startsWith("set") && !name.equals("set")
			   && argCount == 1 && resultType.equals(void.class)) {
		    // if the method is "void setX(T x)" it is a setter
		    attr = new MBeanAttributeInfo(name.substring(3),
						  attributeDescription,
						  null, method);
		} else if (name.startsWith("is") && !name.equals("is")
			   && argCount == 0
			   && resultType.equals(boolean.class)) {
		    // if the method is "boolean isX()" it is a getter
		    attr = new MBeanAttributeInfo(name.substring(2),
						  attributeDescription,
						  method, null);
		} else {
		    // in all other cases it is an operation
		    attr = null;
		}

		if (attr != null) {
		    if (testConsistency(attributes, attr))
			attributes.add(attr);
		} else {
		    final MBeanOperationInfo oper =
			new MBeanOperationInfo(operationDescription, method);
		    operations.add(oper);
		}
	    } catch (IntrospectionException e) {
		// Should not happen (MBeanAttributeInfo constructor)
		error("introspect", e);
	    }
	}

	return constructResult(baseClass, attributes, operations); 
    }

    /**
     * Checks if the types and the signatures of
     * getters/setters/operations are conform to the MBean design
     * patterns.
     *
     * Error cases:
     * 	-  It exposes a method void Y getXX() AND a method void setXX(Z)
     *     (parameter type mismatch) 
     * 	-  It exposes a method void setXX(Y) AND a method void setXX(Z)
     *     (parameter type mismatch) 
     *  -  It exposes a  boolean isXX() method AND a YY getXX() or a void setXX(Y).
     * Returns false if the attribute is already in attributes List
     */    
    private static boolean testConsistency(List/*<MBeanAttributeInfo>*/attributes,
					   MBeanAttributeInfo attr)
	throws NotCompliantMBeanException {
	for (Iterator it = attributes.iterator(); it.hasNext(); ) {
	    MBeanAttributeInfo mb = (MBeanAttributeInfo) it.next();
	    if (mb.getName().equals(attr.getName())) {
		if ((attr.isReadable() && mb.isReadable()) && 
		    (attr.isIs() != mb.isIs())) {
		    final String msg =
			"Conflicting getters for attribute " + mb.getName();
		    throw new NotCompliantMBeanException(msg);
		}  
		if (!mb.getType().equals(attr.getType())) {
		    if (mb.isWritable() && attr.isWritable()) {
			final String msg =
			    "Type mismatch between parameters of set" +
			    mb.getName() + " methods";
			throw new NotCompliantMBeanException(msg);
		    } else {
			final String msg =
			    "Type mismatch between parameters of get or is" +
			    mb.getName() + ", set" + mb.getName() + " methods";
			throw new NotCompliantMBeanException(msg);
		    }
		}
		if (attr.isReadable() && mb.isReadable()) {
		    return false;
		}
		if (attr.isWritable() && mb.isWritable()) {
		    return false;
		}
	    }
	}
	return true;
    }

    /**
     * Discovers the constructors of the MBean
     */
    static MBeanConstructorInfo[] getConstructors(Class baseClass) {
	Constructor[] consList = baseClass.getConstructors();
	List constructors = new ArrayList();
	
	// Now analyze each Constructor.        
	for (int i = 0; i < consList.length; i++) {
	    Constructor constructor = consList[i];    	    
	    MBeanConstructorInfo mc = null;
	    try {               
		mc = new MBeanConstructorInfo(constructorDescription, constructor);		     		                
	    } catch (Exception ex) {
		mc = null;
	    }
	    if (mc != null) {
		constructors.add(mc);
	    }
	}
	// Allocate and populate the result array.
	MBeanConstructorInfo[] resultConstructors =
	    new MBeanConstructorInfo[constructors.size()];
	constructors.toArray(resultConstructors);
	return resultConstructors;
    }
    
    /**
     * Constructs the MBeanInfo of the MBean.
     */
    private static MBeanInfo constructResult(Class baseClass,
					     List/*<MBeanAttributeInfo>*/ attributes,
					     List/*<MBeanOperationInfo>*/ operations) {
	
	final int len = attributes.size();
	final MBeanAttributeInfo[] attrlist = new MBeanAttributeInfo[len];
	attributes.toArray(attrlist);
	final ArrayList mergedAttributes = new ArrayList();
	
	for (int i=0;i<len;i++) {
	    final MBeanAttributeInfo bi = attrlist[i];
	    
	    // bi can be null if it has already been eliminated
	    // by the loop below at an earlier iteration
	    // (cf. attrlist[j]=null;) In this case, just skip it.
	    //
	    if (bi == null) continue;

	    // Placeholder for the final attribute info we're going to
	    // keep.
	    //
	    MBeanAttributeInfo att = bi;

	    // The loop below will try to find whether bi is also present
	    // elsewhere further down the list. 
	    // If it is not, att will be left unchanged.
	    // Otherwise, the found attribute info will be merged with
	    // att and `removed' from the array by setting them to `null'
	    //
            for (int j=i+1;j<len;j++) {
		MBeanAttributeInfo mi = attrlist[j];
		
		// mi can be null if it has already been eliminated
		// by this loop at an earlier iteration.
		// (cf. attrlist[j]=null;) In this case, just skip it.
		//
		if (mi == null) continue;
                if ((mi.getName().compareTo(bi.getName()) == 0)) {
		    // mi and bi have the same name, which means that 
		    // that the attribute has been inserted twice in 
		    // the list, which means that it is a read-write
		    // attribute.
		    // So we're going to replace att with a new 
		    // attribute info with read-write mode.
		    // We also set attrlist[j] to null in order to avoid
		    // duplicates (attrlist[j] and attrlist[i] are now
		    // merged into att).
		    //
		    attrlist[j]=null;
		    att = new MBeanAttributeInfo(bi.getName(), 
						 bi.getType(), 
						 attributeDescription, 
						 true, true, bi.isIs());
		    // I think we could break, but it is probably
		    // safer not to...
		    //
		    // break;
		}
	    }
                
	    // Now all attributes info which had the same name than bi
	    // have been merged together in att. 
	    // Simply add att to the merged list.
	    //
	    mergedAttributes.add(att);	    
        }
 
        final MBeanAttributeInfo[] resultAttributes =
	    new MBeanAttributeInfo[mergedAttributes.size()];
	mergedAttributes.toArray(resultAttributes);

        final MBeanOperationInfo[] resultOperations =
	    new MBeanOperationInfo[operations.size()];
	operations.toArray(resultOperations);

	final MBeanConstructorInfo[] resultConstructors =
	    getConstructors(baseClass);     

        final MBeanInfo resultMBeanInfo =
	    new MBeanInfo(baseClass.getName(), mbeanInfoDescription, 
			  resultAttributes, resultConstructors, 
			  resultOperations, null);
	return resultMBeanInfo;
    }

    /**
     * Returns the XXMBean interface or null if no such interface exists
     *
     * @param c The interface to be tested
     * @param clName The name of the class implementing this interface
     */
    static Class implementsMBean(Class c, String clName) {
	if (c.getName().compareTo(clName + "MBean") == 0) {
	    return c;
	}   
	Class current = c;
	Class[] interfaces = c.getInterfaces();
	for (int i = 0;i < interfaces.length; i++) {

	    try {
		if (interfaces[i].getName().compareTo(clName + "MBean") == 0) {
		    return interfaces[i];
		}     
	    } catch (Exception e) {
		return null;
	    }  	
	}
	
	return null;
    }

    private static void error(String method,Throwable t) {
	com.sun.jmx.trace.Trace.send(com.sun.jmx.trace.Trace.LEVEL_ERROR,
				     com.sun.jmx.trace.Trace.INFO_MBEANSERVER,
				     "Introspector",
				     method,
				     t);
				     
    }
}
