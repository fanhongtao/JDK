/*
 * @(#)Constructor.java	1.14 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.lang.reflect;

/**
 * Constructor provides information about, and access to, a single
 * constructor for a class.
 *
 * <p>Constructor permits widening conversions to occur when matching the
 * actual parameters to newInstance() with the underlying
 * constructor's formal parameters, but throws an
 * IllegalArgumentException if a narrowing conversion would occur.
 *
 * @see Member
 * @see java.lang.Class
 * @see java.lang.Class#getConstructors()
 * @see java.lang.Class#getConstructor()
 * @see java.lang.Class#getDeclaredConstructors()
 *
 * @author	Nakul Saraiya
 */
public final
class Constructor implements Member {

    private Class		clazz;
    private int			slot;
    private Class[]		parameterTypes;
    private Class[]		exceptionTypes;

    /**
     * Constructor.  Only the Java Virtual Machine may construct
     * a Constructor.
     */
    private Constructor() {}

    /**
     * Returns the Class object representing the class that declares
     * the constructor represented by this Constructor object.
     */
    public Class getDeclaringClass() {
	return clazz;
    }

    /**
     * Returns the name of this constructor, as a string.  This is
     * always the same as the name of the constructor's declaring
     * class.
     */
    public String getName() {
	return getDeclaringClass().getName();
    }

    /**
     * Returns the Java language modifiers for the constructor
     * represented by this Constructor object, as an integer. The
     * Modifier class should be used to decode the modifiers.
     *
     * @see Modifier
     */
    public native int getModifiers();

    /**
     * Returns an array of Class objects that represent the formal
     * parameter types, in declaration order, of the constructor
     * represented by this Constructor object.  Returns an array of
     * length 0 if the underlying constructor takes no parameters.
     */
    public Class[] getParameterTypes() {
	return Method.copy(parameterTypes);
    }

    /**
     * Returns an array of Class objects that represent the types of
     * the checked exceptions thrown by the underlying constructor
     * represented by this Constructor object.  Returns an array of
     * length 0 if the constructor throws no checked exceptions.
     */
    public Class[] getExceptionTypes() {
	return Method.copy(exceptionTypes);
    }

    /**
     * Compares this Constructor against the specified object.
     * Returns true if the objects are the same.  Two Constructors are
     * the same if they were declared by the same class and have the
     * same formal parameter types.
     */
    public boolean equals(Object obj) {
	if (obj != null && obj instanceof Constructor) {
	    Constructor other = (Constructor)obj;
	    if (getDeclaringClass() == other.getDeclaringClass()) {
		/* Avoid unnecessary cloning */
		Class[] params1 = parameterTypes;
		Class[] params2 = other.parameterTypes;
		if (params1.length == params2.length) {
		    for (int i = 0; i < params1.length; i++) {
			if (params1[i] != params2[i])
			    return false;
		    }
		    return true;
		}
	    }
	}
	return false;
    }

    /**
     * Returns a hashcode for this Constructor. The hashcode is
     * the same as the hashcode for the underlying constructor's
     * declaring class name.
     */
    public int hashCode() {
	return getDeclaringClass().getName().hashCode();
    }

    /**
     * Return a string describing this Constructor.  The string is
     * formatted as the constructor access modifiers, if any,
     * followed by the fully-qualified name of the declaring class,
     * followed by a parenthesized, comma-separated list of the
     * constructor's formal parameter types.  For example:
     * <pre>
     *    public java.util.Hashtable(int,float)
     * </pre>
     *
     * <p>The only possible modifiers for constructors are the access
     * modifiers <tt>public</tt>, <tt>protected</tt> or
     * <tt>private</tt>.  Only one of these may appear, or none if the
     * constructor has default (package) access.
     */
    public String toString() {
	try {
	    StringBuffer sb = new StringBuffer();
	    int mod = getModifiers();
	    if (mod != 0) {
		sb.append(Modifier.toString(mod) + " ");
	    }
	    sb.append(Field.getTypeName(getDeclaringClass()));
	    sb.append("(");
	    Class[] params = parameterTypes; // avoid clone
	    for (int j = 0; j < params.length; j++) {
		sb.append(Field.getTypeName(params[j]));
		if (j < (params.length - 1))
		    sb.append(",");
	    }
	    sb.append(")");
	    Class[] exceptions = exceptionTypes; // avoid clone
	    if (exceptions.length > 0) {
		sb.append(" throws ");
		for (int k = 0; k < exceptions.length; k++) {
		    sb.append(exceptions[k].getName());
		    if (k < (exceptions.length - 1))
			sb.append(",");
		}
	    }
	    return sb.toString();
	} catch (Exception e) {
	    return "<" + e + ">";
	}
    }

    /**
     * Uses the constructor represented by this Constructor object to
     * create and initialize a new instance of the constructor's
     * declaring class, with the specified initialization parameters.
     * Individual parameters are automatically unwrapped to match
     * primitive formal parameters, and both primitive and reference
     * parameters are subject to widening conversions as necessary.
     * Returns the newly created and initialized object.
     *
     * <p>Creation proceeds with the following steps, in order:
     *
     * <p>If the class that declares the underlying constructor
     * represents an abstract class, the creation throws an
     * InstantiationException.
     *
     * <p>If this Constructor object enforces Java language access
     * control and the underlying constructor is inaccessible, the
     * creation throws an IllegalAccessException.
     *
     * <p>If the number of actual parameters supplied via initargs is
     * different from the number of formal parameters required by the
     * underlying constructor, the creation throws an
     * IllegalArgumentException.
     *
     * <p>A new instance of the constructor's declaring class is
     * created, and its fields are initialized to their default
     * initial values.
     *
     * <p>For each actual parameter in the supplied initargs array:
     *
     * <p>If the corresponding formal parameter has a primitive type,
     * an unwrapping conversion is attempted to convert the object
     * value to a value of the primitive type.  If this attempt fails,
     * the creation throws an IllegalArgumentException.
     *
     * <p>If, after possible unwrapping, the parameter value cannot be
     * converted to the corresponding formal parameter type by an
     * identity or widening conversion, the creation throws an
     * IllegalArgumentException.
     *
     * <p>Control transfers to the underlying constructor to
     * initialize the new instance.  If the constructor completes
     * abruptly by throwing an exception, the exception is placed in
     * an InvocationTargetException and thrown in turn to the caller
     * of newInstance.
     *
     * <p>If the constructor completes normally, returns the newly
     * created and initialized instance.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the number of actual and formal
     *              parameters differ, or if an unwrapping conversion fails.
     * @exception InstantiationException    if the class that declares the
     *              underlying constructor represents an abstract class.
     * @exception InvocationTargetException if the underlying constructor
     *              throws an exception.
     */
    public native Object newInstance(Object[] initargs)
	throws InstantiationException, IllegalAccessException,
	    IllegalArgumentException, InvocationTargetException;
}
