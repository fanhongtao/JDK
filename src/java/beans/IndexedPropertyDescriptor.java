/*
 * @(#)IndexedPropertyDescriptor.java	1.24 98/07/01
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.beans;

import java.lang.reflect.*;

/**
 * An IndexedPropertyDescriptor describes a property that acts like an
 * array and has an indexed read and/or indexed write method to access
 * specific elements of the array.
 * <p>
 * An indexed property may also provide simple non-indexed read and write
 * methods.  If these are present, they read and write arrays of the type
 * returned by the indexed read method.
 */

public class IndexedPropertyDescriptor extends PropertyDescriptor {

    /**
     * This constructor constructs an IndexedPropertyDescriptor for a property
     * that follows the standard Java conventions by having getFoo and setFoo 
     * accessor methods, for both indexed access and array access.
     * <p>
     * Thus if the argument name is "fred", it will assume that there
     * is an indexed reader method "getFred", a non-indexed (array) reader
     * method also called "getFred", an indexed writer method "setFred",
     * and finally a non-indexed writer method "setFred".
     *
     * @param propertyName The programmatic name of the property.
     * @param beanClass The Class object for the target bean.
     * @exception IntrospectionException if an exception occurs during
     *              introspection.
     */
    public IndexedPropertyDescriptor(String propertyName, Class beanClass)
		throws IntrospectionException {
	this(propertyName, beanClass,
			 "get" + capitalize(propertyName),
			 "set" + capitalize(propertyName),
			 "get" + capitalize(propertyName),
			 "set" + capitalize(propertyName));
    }

    /**
     * This constructor takes the name of a simple property, and method
     * names for reading and writing the property, both indexed
     * and non-indexed.
     *
     * @param propertyName The programmatic name of the property.
     * @param beanClass  The Class object for the target bean.
     * @param getterName The name of the method used for reading the property
     *		 values as an array.  May be null if the property is write-only
     *		 or must be indexed.
     * @param setterName The name of the method used for writing the property
     *		 values as an array.  May be null if the property is read-only
     *		 or must be indexed.
     * @param indexedGetterName The name of the method used for reading
     *		an indexed property value.
     *		May be null if the property is write-only.
     * @param indexedSetterName The name of the method used for writing
     *		an indexed property value.  
     *		May be null if the property is read-only.
     * @exception IntrospectionException if an exception occurs during
     *              introspection.
     */
    public IndexedPropertyDescriptor(String propertyName, Class beanClass,
		String getterName, String setterName,
		String indexedGetterName, String indexedSetterName)
		throws IntrospectionException {
	super(propertyName, beanClass, getterName, setterName);
	indexedReadMethod = Introspector.findMethod(beanClass, indexedGetterName, 1);
	indexedWriteMethod = Introspector.findMethod(beanClass, indexedSetterName, 2);
	findIndexedPropertyType();
    }

    /**
     * This constructor takes the name of a simple property, and Method
     * objects for reading and writing the property.
     *
     * @param propertyName The programmatic name of the property.
     * @param getter The method used for reading the property values as an array.
     *		May be null if the property is write-only or must be indexed.
     * @param setter The method used for writing the property values as an array.
     *		May be null if the property is read-only or must be indexed.
     * @param indexedGetter The method used for reading an indexed property value.
     *		May be null if the property is write-only.
     * @param indexedSetter The method used for writing an indexed property value.  
     *		May be null if the property is read-only.
     * @exception IntrospectionException if an exception occurs during
     *              introspection.
     */
    public IndexedPropertyDescriptor(String propertyName, Method getter, Method setter,
 					    Method indexedGetter, Method indexedSetter)
		throws IntrospectionException {
	super(propertyName, getter, setter);
	indexedReadMethod = indexedGetter;
	indexedWriteMethod = indexedSetter;
	findIndexedPropertyType();
    }
    
    /**
     * @return The method that should be used to read an indexed
     * property value.
     * May return null if the property isn't indexed or is write-only.
     */
    public Method getIndexedReadMethod() {
	return indexedReadMethod;
    }

    /**
     * @return The method that should be used to write an indexed
     * property value.
     * May return null if the property isn't indexed or is read-only.
     */
    public Method getIndexedWriteMethod() {
	return indexedWriteMethod;
    }

    /**
     * @return The Java Class for the indexed properties type.  Note that
     * the Class may describe a primitive Java type such as "int".
     * <p>
     * This is the type that will be returned by the indexedReadMethod.
     */
    public Class getIndexedPropertyType() {
	return indexedPropertyType;
    }


    private void findIndexedPropertyType() throws IntrospectionException {
	try {
	    indexedPropertyType = null;
	    if (indexedReadMethod != null) {
		Class params[] = indexedReadMethod.getParameterTypes();
		if (params.length != 1) {
		    throw new IntrospectionException("bad indexed read method arg count");
		}
		if (params[0] != Integer.TYPE) {
		    throw new IntrospectionException("non int index to indexed read method");
		}
		indexedPropertyType = indexedReadMethod.getReturnType();
		if (indexedPropertyType == Void.TYPE) {
		    throw new IntrospectionException("indexed read method returns void");
		}
	    }
	    if (indexedWriteMethod != null) {
		Class params[] = indexedWriteMethod.getParameterTypes();
		if (params.length != 2) {
		    throw new IntrospectionException("bad indexed write method arg count");
		}
		if (params[0] != Integer.TYPE) {
		    throw new IntrospectionException("non int index to indexed write method");
		}
		if (indexedPropertyType != null && indexedPropertyType != params[1]) {
		    throw new IntrospectionException(
			"type mismatch between indexed read and indexed write methods");
		}
		indexedPropertyType = params[1];
	    }
	    if (indexedPropertyType == null) {
	        throw new IntrospectionException(
			"no indexed getter or setter");
	    }
	    Class propertyType = getPropertyType();
	    if (propertyType != null && (!propertyType.isArray() ||
			propertyType.getComponentType() != indexedPropertyType)) {
	        throw new IntrospectionException(
			"type mismatch between indexed and non-indexed methods");
	    }
	} catch (IntrospectionException ex) {
	    throw ex;
	}
    }


    /*
     * Package-private constructor.
     * Merge two property descriptors.  Where they conflict, give the
     * second argument (y) priority over the first argumnnt (x).
     * @param x  The first (lower priority) PropertyDescriptor
     * @param y  The second (higher priority) PropertyDescriptor
     */

    IndexedPropertyDescriptor(PropertyDescriptor x, PropertyDescriptor y) {
	super(x,y);
	if (x instanceof IndexedPropertyDescriptor) {
	    IndexedPropertyDescriptor ix = (IndexedPropertyDescriptor)x;
	    indexedReadMethod = ix.indexedReadMethod;
	    indexedWriteMethod = ix.indexedWriteMethod;
	    indexedPropertyType = ix.indexedPropertyType;
	}
	if (y instanceof IndexedPropertyDescriptor) {
	    IndexedPropertyDescriptor iy = (IndexedPropertyDescriptor)y;
	    if (iy.indexedReadMethod != null) {
	        indexedReadMethod = iy.indexedReadMethod;
	    }
	    if (iy.indexedWriteMethod != null) {
	        indexedWriteMethod = iy.indexedWriteMethod;
	    }
	    indexedPropertyType = iy.indexedPropertyType;
	}
	
    }


    private static String capitalize(String s) {
	char chars[] = s.toCharArray();
	chars[0] = Character.toUpperCase(chars[0]);
	return new String(chars);
    }

    private Class indexedPropertyType;
    private Method indexedReadMethod;
    private Method indexedWriteMethod;
}
