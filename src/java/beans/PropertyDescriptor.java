/*
 * @(#)PropertyDescriptor.java	1.57 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.beans;

import java.lang.reflect.*;

/**
 * A PropertyDescriptor describes one property that a Java Bean
 * exports via a pair of accessor methods.
 */

public class PropertyDescriptor extends FeatureDescriptor {

    private Class propertyType;
    private Method readMethod;
    private Method writeMethod;
    private boolean bound;
    private boolean constrained;
    private Class propertyEditorClass;

    /**
     * Constructs a PropertyDescriptor for a property that follows
     * the standard Java convention by having getFoo and setFoo
     * accessor methods.  Thus if the argument name is "fred", it will
     * assume that the writer method is "setFred" and the reader method
     * is "getFred" (or "isFred" for a boolean property).  Note that the
     * property name should start with a lower case character, which will
     * be capitalized in the method names.
     *
     * @param propertyName The programmatic name of the property.
     * @param beanClass The Class object for the target bean.  For
     *		example sun.beans.OurButton.class.
     * @exception IntrospectionException if an exception occurs during
     *              introspection.
     */
    public PropertyDescriptor(String propertyName, Class beanClass)
		throws IntrospectionException {
	if (propertyName == null || propertyName.length() == 0) {
	    throw new IntrospectionException("bad property name");
	}
	setName(propertyName);
	String base = capitalize(propertyName);

	// Since there can be multiple setter methods but only one getter
        // method, find the getter method first so that you know what the
        // property type is.  For booleans, there can be "is" and "get"
        // methods.  If an "is" method exists, this is the official
	// reader method so look for this one first.
        try {
	    readMethod = Introspector.findMethod(beanClass, "is" + base, 0);
	} catch (Exception getterExc) {
            // no "is" method, so look for a "get" method.
	    readMethod = Introspector.findMethod(beanClass, "get" + base, 0);
	}
        Class params[] = { readMethod.getReturnType() };
        writeMethod = Introspector.findMethod(beanClass, "set" + base, 1,
                                              params);
	propertyType = findPropertyType(readMethod, writeMethod);
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
	if (propertyName == null || propertyName.length() == 0) {
	    throw new IntrospectionException("bad property name");
	}
	setName(propertyName);
	readMethod = Introspector.findMethod(beanClass, getterName, 0);
	if (readMethod != null) {
	    Class params[] = { readMethod.getReturnType() };
	    writeMethod = Introspector.findMethod(beanClass, setterName, 1,
                                                  params);
	} else {
	    writeMethod = Introspector.findMethod(beanClass, setterName, 1);
	}
	propertyType = findPropertyType(readMethod, writeMethod);
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
	if (propertyName == null || propertyName.length() == 0) {
	    throw new IntrospectionException("bad property name");
	}
	setName(propertyName);
	readMethod = getter;
	writeMethod = setter;
	propertyType = findPropertyType(readMethod, writeMethod);
    }

    /**
     * Gets the Class object for the property.
     *
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
     * Gets the method that should be used to read the property value.
     *
     * @return The method that should be used to read the property value.
     * May return null if the property can't be read.
     */
    public Method getReadMethod() {
	return readMethod;
    }

    /**
     * Sets the method that should be used to read the property value.
     *
     * @param getter The new getter method.
     */
    public void setReadMethod(Method getter)
				throws IntrospectionException {
	readMethod = getter;
	propertyType = findPropertyType(readMethod, writeMethod);
    }

    /**
     * Gets the method that should be used to write the property value.
     *
     * @return The method that should be used to write the property value.
     * May return null if the property can't be written.
     */
    public Method getWriteMethod() {
	return writeMethod;
    }

    /**
     * Sets the method that should be used to write the property value.
     *
     * @param setter The new setter method.
     */
    public void setWriteMethod(Method setter)
				throws IntrospectionException {
	writeMethod = setter;
	propertyType = findPropertyType(readMethod, writeMethod);
    }

    /**
     * Updates to "bound" properties will cause a "PropertyChange" event to
     * get fired when the property is changed.
     *
     * @return True if this is a bound property.
     */
    public boolean isBound() {
	return bound;
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
	return constrained;
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
     *
     * @param propertyEditorClass  The Class for the desired PropertyEditor.
     */
    public void setPropertyEditorClass(Class propertyEditorClass) {
	this.propertyEditorClass = propertyEditorClass;
    }

    /**
     * Gets any explicit PropertyEditor Class that has been registered
     * for this property.
     *
     * @return Any explicit PropertyEditor Class that has been registered
     *		for this property.  Normally this will return "null",
     *		indicating that no special editor has been registered,
     *		so the PropertyEditorManager should be used to locate
     *		a suitable PropertyEditor.
     */
    public Class getPropertyEditorClass() {
	return propertyEditorClass;
    }

    /**
     * Compares this <code>PropertyDescriptor</code> against the specified object.
     * Returns true if the objects are the same. Two <code>PropertyDescriptor</code>s
     * are the same if the read, write, property types, property editor and
     * flags  are equivalent.
     *
     * @since 1.4
     */
    public boolean equals(Object obj) {
	if (obj != null && obj instanceof PropertyDescriptor) {
	    PropertyDescriptor other = (PropertyDescriptor)obj;
	    Method otherReadMethod = other.getReadMethod();
	    Method otherWriteMethod = other.getWriteMethod();

	    if (!compareMethods(readMethod, otherReadMethod)) {
		return false;
	    }

	    if (!compareMethods(writeMethod, otherWriteMethod)) {
		return false;
	    }

	    if (propertyType == other.getPropertyType() && 
		propertyEditorClass == other.getPropertyEditorClass() &&
		bound == other.isBound() && constrained == other.isConstrained()) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Package private helper method for Descriptor .equals methods.
     *
     * @param a first method to compare
     * @param b second method to compare
     * @return boolean to indicate that the methods are equivalent
     */
    boolean compareMethods(Method a, Method b) {
	// Note: perhaps this should be a protected method in FeatureDescriptor
	if ((a == null) != (b == null)) {
	    return false;
	}

	if (a != null && b != null) {
	    if (!a.equals(b)) {
		return false;
	    }
	}
	return true;
    }


    /**
     * Package-private constructor.
     * Merge two property descriptors.  Where they conflict, give the
     * second argument (y) priority over the first argument (x).
     *
     * @param x  The first (lower priority) PropertyDescriptor
     * @param y  The second (higher priority) PropertyDescriptor
     */
    PropertyDescriptor(PropertyDescriptor x, PropertyDescriptor y) {
	super(x,y);

	// Figure out the merged read method.
	Method xr = x.readMethod;
	Method yr = y.readMethod;
	readMethod = xr;
	// Normally give priority to y's readMethod.
	if (yr != null) {
	    readMethod = yr;
	}
	// However, if both x and y reference read methods in the same class,
	// give priority to a boolean "is" method over a boolean "get" method.
	if (xr != null && yr != null &&
		   xr.getDeclaringClass() == yr.getDeclaringClass() &&
		   xr.getReturnType() == boolean.class &&
		   yr.getReturnType() == boolean.class &&
		   xr.getName().indexOf("is") == 0 &&
		   yr.getName().indexOf("get") == 0) {
	    readMethod = xr;
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
	    propertyType = findPropertyType(readMethod, writeMethod);
	} catch (IntrospectionException ex) {
	    // Given we're merging two valid PDs, this "should never happen".
	    throw new Error("PropertyDescriptor: internal error while merging PDs: " + ex.getMessage());
	}
    }

    /*
     * Package-private dup constructor.
     * This must isolate the new object from any changes to the old object.
     */
    PropertyDescriptor(PropertyDescriptor old) {
	super(old);
	readMethod = old.readMethod;;
	writeMethod = old.writeMethod;
	propertyEditorClass = old.propertyEditorClass;
	bound = old.bound;
	constrained = old.constrained;
	propertyType = old.propertyType;
    }

    /**
     * Returns the property type that corresponds to the read and write method.
     */
    private Class findPropertyType(Method readMethod, Method writeMethod)
	throws IntrospectionException {
	Class propertyType = null;
	try {
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
	return propertyType;
    }

    static String capitalize(String s) {
	if (s.length() == 0) {
 	    return s;
	}
	char chars[] = s.toCharArray();
	chars[0] = Character.toUpperCase(chars[0]);
	return new String(chars);
    }

}
