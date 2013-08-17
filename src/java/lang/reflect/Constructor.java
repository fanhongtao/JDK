/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang.reflect;

/**
 * <code>Constructor</code> provides information about, and access to, a single
 * constructor for a class.
 *
 * <p><code>Constructor</code> permits widening conversions to occur when matching the
 * actual parameters to newInstance() with the underlying
 * constructor's formal parameters, but throws an
 * <code>IllegalArgumentException</code> if a narrowing conversion would occur.
 *
 * @see Member
 * @see java.lang.Class
 * @see java.lang.Class#getConstructors()
 * @see java.lang.Class#getConstructor(Class[])
 * @see java.lang.Class#getDeclaredConstructors()
 *
 * @author	Nakul Saraiya
 */
public final
class Constructor extends AccessibleObject implements Member {

    private Class		clazz;
    private int			slot;
    private Class[]		parameterTypes;
    private Class[]		exceptionTypes;
    private int			modifiers;

    /**
     * Constructor.  Only the Java Virtual Machine may construct
     * a Constructor.
     */
    private Constructor() {}

    /**
     * Returns the <code>Class</code> object representing the class that declares
     * the constructor represented by this <code>Constructor</code> object.
     */
    public Class getDeclaringClass() {
	return clazz;
    }

    /**
     * Returns the name of this constructor, as a string.  This is
     * always the same as the simple name of the constructor's declaring
     * class.
     */
    public String getName() {
	return getDeclaringClass().getName();
    }

    /**
     * Returns the Java language modifiers for the constructor
     * represented by this <code>Constructor</code> object, as an integer. The
     * <code>Modifier</code> class should be used to decode the modifiers.
     *
     * @see Modifier
     */
    public int getModifiers() {
	return modifiers;
    }

    /**
     * Returns an array of <code>Class</code> objects that represent the formal
     * parameter types, in declaration order, of the constructor
     * represented by this <code>Constructor</code> object.  Returns an array of
     * length 0 if the underlying constructor takes no parameters.
     */
    public Class[] getParameterTypes() {
	return Method.copy(parameterTypes);
    }

    /**
     * Returns an array of <code>Class</code> objects that represent the types of
     * of exceptions declared to be thrown by the underlying constructor
     * represented by this <code>Constructor</code> object.  Returns an array of
     * length 0 if the constructor declares no exceptions in its <code>throws</code> clause.
     */
    public Class[] getExceptionTypes() {
	return Method.copy(exceptionTypes);
    }

    /**
     * Compares this <code>Constructor</code> against the specified object.
     * Returns true if the objects are the same.  Two <code>Constructor</code> objects are
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
     * Returns a hashcode for this <code>Constructor</code>. The hashcode is
     * the same as the hashcode for the underlying constructor's
     * declaring class name.
     */
    public int hashCode() {
	return getDeclaringClass().getName().hashCode();
    }

    /**
     * Returns a string describing this <code>Constructor</code>.  The string is
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
     * Uses the constructor represented by this <code>Constructor</code> object to
     * create and initialize a new instance of the constructor's
     * declaring class, with the specified initialization parameters.
     * Individual parameters are automatically unwrapped to match
     * primitive formal parameters, and both primitive and reference
     * parameters are subject to method invocation conversions as necessary.
     * Returns the newly created and initialized object.
     *
     * <p>Creation proceeds with the following steps, in order:
     *
     * <p>If the class that declares the underlying constructor
     * represents an abstract class, the creation throws an
     * <code>InstantiationException</code>.
     *
     * <p>If this <code>Constructor</code> object enforces Java language access
     * control and the underlying constructor is inaccessible, the
     * creation throws an <code>IllegalAccessException</code>.
     *
     * <p>If the number of actual parameters supplied via <code>initargs</code> is
     * different from the number of formal parameters required by the
     * underlying constructor, the creation throws an
     * <code>IllegalArgumentException</code>.
     *
     * <p>A new instance of the constructor's declaring class is
     * created, and its fields are initialized to their default
     * initial values.
     *
     * <p>For each actual parameter in the supplied <code>initargs</code> array:
     *
     * <p>If the corresponding formal parameter has a primitive type,
     * an unwrapping conversion is attempted to convert the object
     * value to a value of the primitive type.  If this attempt fails,
     * the creation throws an <code>IllegalArgumentException</code>.
     *
     * <p>If, after possible unwrapping, the parameter value cannot be
     * converted to the corresponding formal parameter type by a
     * method invocation conversion, the creation throws an
     * <code>IllegalArgumentException</code>.
     *
     * <p> The constructor's declaring class is initialized if it has
     * not already been initialized.  A new instance of the constructor's
     * declaring class is created, and its fields are initialized to
     * their default initial values.
     *
     * <p>Control transfers to the underlying constructor to
     * initialize the new instance.  If the constructor completes
     * abruptly by throwing an exception, the exception is placed in
     * an <code>InvocationTargetException</code> and thrown in turn to the caller
     * of <code>newInstance</code>.
     *
     * <p>If the constructor completes normally, returns the newly
     * created and initialized instance.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the number of actual and formal
     *              parameters differ, or if an unwrapping  or method
     *              invocation conversion fails.
     * @exception InstantiationException    if the class that declares the
     *              underlying constructor represents an abstract class.
     * @exception InvocationTargetException if the underlying constructor
     *              throws an exception.
     * @exception ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     */
    public native Object newInstance(Object[] initargs)
	throws InstantiationException, IllegalAccessException,
	    IllegalArgumentException, InvocationTargetException;
}
