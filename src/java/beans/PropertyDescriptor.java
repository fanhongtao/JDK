/*
 * @(#)PropertyDescriptor.java	1.37 98/07/01
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
 * A PropertyDescriptor describes one property that a Java Bean
 * exports via a pair of accessor methods.
 */

public class PropertyDescriptor extends FeatureDescriptor {

    /**
     * Constructs a PropertyDescriptor for a property that follows
     * the standard Java convention by having getFoo and setFoo 
     * accessor methods.  Thus if the argument name is "fred", it will
     * assume that the reader method is "getFred" and the writer method 
     * is "setFred".  Note that the property name should start with a lower
     * case character, which will be capitalized in the method names.
     *
     * @param propertyName The programmatic name of the property.
     * @param beanClass The Class object for the target bean.  For
     *		example sun.beans.OurButton.class.
     * @exception IntrospectionException if an exception occurs during
     *              introspection.
     */
    public PropertyDescriptor(String propertyName, Class beanClass)
		throws IntrospectionException {
	setName(propertyName);
	String base = capitalize(propertyName);
	writeMethod = Introspector.findMethod(beanClass, "set" + base, 1);
	// If it's a boolean property check for an "isFoo" first.
	if (writeMethod.getParameterTypes()[0] == Boolean.TYPE) {
	    try {
		readMethod = Introspector.findMethod(beanClass, "is" + base, 0);
	    } catch (Exception ex) {
	    }
	}
	if (readMethod == null) {
	    readMethod = Introspector.findMethod(beanClass, "get" + base, 0);
	}
	findPropertyType();
    }

    /**
     * This constructor takes the name of a simple property, and method
     * names for reading and writing the property.
     *
     * @param propertyName The programmatic name of the property.
     * @param beanClass The Class object for the target bean.  For
     *		example sun.beans.OurButton.class.
     * @param getterName The name of the method used for reading the property
     *		 value.  May be null if the property is write-only.
     * @param setterName The name of the method used for writing the property
     *		 value.  May be null if the property is read-only.
     * @exception IntrospectionException if an exception occurs during
     *              introspection.
     */
    public PropertyDescriptor(String propertyName, Class beanClass,
		String getterName, String setterName)
		throws IntrospectionException {
	setName(propertyName);
	readMethod = Introspector.findMethod(beanClass, getterName, 0);
	writeMethod = Introspector.findMethod(beanClass, setterName, 1);
	findPropertyType();
    }

    /**
     * This constructor takes the name of a simple property, and Method
     * objects for reading and writing the property.
     *
     * @param propertyName The programmatic name of the property.
     * @param getter The method used for reading the property value.
     *		May be null if the property is write-only.
     * @param setter The method used for writing the property value.  
     *		May be null if the property is read-only.
     * @exception IntrospectionException if an exception occurs during
     *              introspection.
     */
    public PropertyDescriptor(String propertyName, Method getter, Method setter)
		throws IntrospectionException {
	setName(propertyName);
	readMethod = getter;
	writeMethod = setter;
	findPropertyType(); 
    }
    
    /**
     * @return The Java type info for the property.  Note that
     * the "Class" object may describe a built-in Java type such as "int".
     * The result may be "null" if this is an indexed property that
     * does not support non-indexed access.
     * <p>
     * This is the type that will be returned by the ReadMethod.
     */
    public Class getPropertyType() {
	return propertyType;
    }

    /**
     * @return The method that should be used to read the property value.
     * May return null if the property can't be read.
     */
    public Method getReadMethod() {
	return readMethod;
    }

    /**
     * @return The method that should be used to write the property value.
     * May return null if the property can't be written.
     */
    public Method getWriteMethod() {
	return (writeMethod);
    }

    /**
     * Updates to "bound" properties will cause a "PropertyChange" event to
     * get fired when the property is changed.
     *
     * @return True if this is a bound property.
     */
    public boolean isBound() {
	return (bound);
    }

    /**
     * Updates to "bound" properties will cause a "PropertyChange" event to 
     * get fired when the property is changed.
     *
     * @param bound True if this is a bound property.
     */
    public void setBound(boolean bound) {
	this.bound = bound;
    }

    /**
     * Attempted updates to "Constrained" properties will cause a "VetoableChange"
     * event to get fired when the property is changed.
     *
     * @return True if this is a constrained property.
     */
    public boolean isConstrained() {
	return (constrained);
    }

    /**
     * Attempted updates to "Constrained" properties will cause a "VetoableChange"
     * event to get fired when the property is changed.
     *
     * @param constrained True if this is a constrained property.
     */
    public void setConstrained(boolean constrained) {
	this.constrained = constrained;
    }


    /**
     * Normally PropertyEditors will be found using the PropertyEditorManager.
     * However if for some reason you want to associate a particular
     * PropertyEditor with a given property, then you can do it with
     * this method.
     * @param propertyEditorClass  The Class for the desired PropertyEditor.
     */
    public void setPropertyEditorClass(Class propertyEditorClass) {
	this.propertyEditorClass = propertyEditorClass;
    }

    /**
     * @return Any explicit PropertyEditor Class that has been registered
     *		for this property.  Normally this will return "null",
     *		indicating that no special editor has been registered,
     *		so the PropertyEditorManager should be used to locate
     *		a suitable PropertyEditor.
     */
    public Class getPropertyEditorClass() {
	return propertyEditorClass;
    }

    /*
     * Package-private constructor.
     * Merge two property descriptors.  Where they conflict, give the
     * second argument (y) priority over the first argumnnt (x).
     * @param x  The first (lower priority) PropertyDescriptor
     * @param y  The second (higher priority) PropertyDescriptor
     */

    PropertyDescriptor(PropertyDescriptor x, PropertyDescriptor y) {
	super(x,y);
	readMethod = x.readMethod;
	propertyType = x.propertyType;
	if (y.readMethod != null) {
	    readMethod = y.readMethod;
	}
	writeMethod = x.writeMethod;
	if (y.writeMethod != null) {
	    writeMethod = y.writeMethod;
	}
	propertyEditorClass = x.propertyEditorClass;
	if (y.propertyEditorClass != null) {
	    propertyEditorClass = y.propertyEditorClass;
	}
	bound = x.bound | y.bound;
	constrained = x.constrained | y.constrained;
	try {
	    findPropertyType();
	} catch (IntrospectionException ex) {
	    // Given we're merging two valid PDs, this "should never happen".
	    throw new Error("PropertyDescriptor: internal error while merging PDs");
	}
    }

    private void findPropertyType() throws IntrospectionException {
	try {
	    propertyType = null;
	    if (readMethod != null) {
		if (readMethod.getParameterTypes().length != 0) {
		    throw new IntrospectionException("bad read method arg count");
		}
		propertyType = readMethod.getReturnType();
		if (propertyType == Void.TYPE) {
		    throw new IntrospectionException("read method " + 
					readMethod.getName() + " returns void");
		}
	    }
	    if (writeMethod != null) {
		Class params[] = writeMethod.getParameterTypes();
		if (params.length != 1) {
		    throw new IntrospectionException("bad write method arg count");
		}
		if (propertyType != null && propertyType != params[0]) {
		    throw new IntrospectionException("type mismatch between read and write methods");
		}
		propertyType = params[0];
	    }
	} catch (IntrospectionException ex) {
	    throw ex;
	}
    }

    private String capitalize(String s) {
	char chars[] = s.toCharArray();
	chars[0] = Character.toUpperCase(chars[0]);
	return new String(chars);
    }

    private Class propertyType;
    private Method readMethod;
    private Method writeMethod;
    private boolean bound;
    private boolean constrained;
    private Class propertyEditorClass;
}
