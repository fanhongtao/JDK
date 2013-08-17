/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang.reflect;

/**
 * A <code>Method</code> provides information about, and access to, a single method
 * on a class or interface.  The reflected method may be a class method
 * or an instance method (including an abstract method).
 *
 * <p>A <code>Method</code> permits widening conversions to occur when matching the
 * actual parameters to invokewith the underlying method's formal
 * parameters, but it throws an <code>IllegalArgumentException</code> if a
 * narrowing conversion would occur.
 *
 * @see Member
 * @see java.lang.Class
 * @see java.lang.Class#getMethods()
 * @see java.lang.Class#getMethod(String, Class[])
 * @see java.lang.Class#getDeclaredMethods()
 * @see java.lang.Class#getDeclaredMethod(String, Class[])
 *
 * @author Nakul Saraiya
 */
public final
class Method extends AccessibleObject implements Member {

    private Class		clazz;
    private int			slot;
    private String		name;
    private Class		returnType;
    private Class[]		parameterTypes;
    private Class[]		exceptionTypes;
    private int			modifiers;

    /**
     * Constructor.  Only the Java Virtual Machine may construct a Method.
     */
    private Method() {}

    /**
     * Returns the <code>Class</code> object representing the class or interface
     * that declares the method represented by this <code>Method</code> object.
     */
    public Class getDeclaringClass() {
	return clazz;
    }

    /**
     * Returns the name of the method represented by this <code>Method</code> 
     * object, as a <code>String</code>.
     */
    public String getName() {
	return name;
    }

    /**
     * Returns the Java language modifiers for the method represented
     * by this <code>Method</code> object, as an integer. The <code>Modifier</code> class should
     * be used to decode the modifiers.
     *
     * @see Modifier
     */
    public int getModifiers() {
	return modifiers;
    }

    /**
     * Returns a <code>Class</code> object that represents the formal return type
     * of the method represented by this <code>Method</code> object.
     */
    public Class getReturnType() {
	return returnType;
    }

    /**
     * Returns an array of <code>Class</code> objects that represent the formal
     * parameter types, in declaration order, of the method
     * represented by this <code>Method</code> object.  Returns an array of length
     * 0 if the underlying method takes no parameters.
     */
    public Class[] getParameterTypes() {
	return copy(parameterTypes);
    }

    /**
     * Returns an array of <code>Class</code> objects that represent 
     * the types of the exceptions declared to be thrown
     * by the underlying method
     * represented by this <code>Method</code> object.  Returns an array of length
     * 0 if the method declares no exceptions in its <code>throws</code> clause.
     */
    public Class[] getExceptionTypes() {
	return copy(exceptionTypes);
    }

    /**
     * Compares this <code>Method</code> against the specified object.  Returns
     * true if the objects are the same.  Two <code>Methods</code> are the same if
     * they were declared by the same class and have the same name
     * and formal parameter types and return type.
     */
    public boolean equals(Object obj) {
	if (obj != null && obj instanceof Method) {
	    Method other = (Method)obj;
	    if ((getDeclaringClass() == other.getDeclaringClass())
		&& (getName().equals(other.getName()))) {
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
     * Returns a hashcode for this <code>Method</code>.  The hashcode is computed
     * as the exclusive-or of the hashcodes for the underlying
     * method's declaring class name and the method's name.
     */
    public int hashCode() {
	return getDeclaringClass().getName().hashCode() ^ getName().hashCode();
    }

    /**
     * Returns a string describing this <code>Method</code>.  The string is
     * formatted as the method access modifiers, if any, followed by
     * the method return type, followed by a space, followed by the
     * class declaring the method, followed by a period, followed by
     * the method name, followed by a parenthesized, comma-separated
     * list of the method's formal parameter types. If the method
     * throws checked exceptions, the parameter list is followed by a
     * space, followed by the word throws followed by a
     * comma-separated list of the thrown exception types.
     * For example:
     * <pre>
     *    public boolean java.lang.Object.equals(java.lang.Object)
     * </pre>
     *
     * <p>The access modifiers are placed in canonical order as
     * specified by "The Java Language Specification".  This is
     * <tt>public</tt>, <tt>protected</tt> or <tt>private</tt> first,
     * and then other modifiers in the following order:
     * <tt>abstract</tt>, <tt>static</tt>, <tt>final</tt>,
     * <tt>synchronized</tt> <tt>native</tt>.
     */
    public String toString() {
	try {
	    StringBuffer sb = new StringBuffer();
	    int mod = getModifiers();
	    if (mod != 0) {
		sb.append(Modifier.toString(mod) + " ");
	    }
	    sb.append(Field.getTypeName(getReturnType()) + " ");
	    sb.append(Field.getTypeName(getDeclaringClass()) + ".");
	    sb.append(getName() + "(");
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
     * Invokes the underlying method represented by this <code>Method</code> 
     * object, on the specified object with the specified parameters.
     * Individual parameters are automatically unwrapped to match
     * primitive formal parameters, and both primitive and reference
     * parameters are subject to widening conversions as
     * necessary. The value returned by the underlying method is
     * automatically wrapped in an object if it has a primitive type.
     *
     * <p>Method invocation proceeds with the following steps, in order:
     *
     * <p>If the underlying method is static, then the specified <code>obj</code> 
     * argument is ignored. It may be null.
     *
     * <p>Otherwise, the method is an instance method.  If the specified
     * object argument is null, the invocation throws a
     * <code>NullPointerException</code>.  Otherwise, if the specified object
     * argument is not an instance of the class or interface declaring
     * the underlying method (or of a subclass or implementor
     * thereof) the invocation throws an
     * <code>IllegalArgumentException</code>.
     *
     * <p>If this <code>Method</code> object enforces Java language access control and
     * the underlying method is inaccessible, the invocation throws an
     * <code>IllegalAccessException</code>.
     *
     * <p>If the number of actual parameters supplied via <code>args</code> is
     * different from the number of formal parameters required by the
     * underlying method, the invocation throws an
     * <code>IllegalArgumentException</code>.
     *
     * <p>For each actual parameter in the supplied <code>args</code> array:
     *
     * <p>If the corresponding formal parameter has a primitive type, an
     * unwrapping conversion is attempted to convert the object value
     * to a value of a primitive type.  If this attempt fails, the
     * invocation throws an <code>IllegalArgumentException</code>.
     *
     * <p>If, after possible unwrapping, the parameter value cannot be
     * converted to the corresponding formal parameter type by a
     * method invocation conversion, the invocation throws an
     * <code>IllegalArgumentException</code>.
     *
     * <p>If the underlying method is an instance method, it is invoked
     * using dynamic method lookup as documented in The Java Language
     * Specification, section 15.11.4.4; in particular, overriding
     * based on the runtime type of the target object will occur.
     *
     * <p>If the underlying method is static, the class that declared
     * the method is initialized if it has not already been initialized.
     * The underlying method is invoked directly.
     *
     * <p>Control transfers to the invoked method.  If the method
     * completes abruptly by throwing an exception, the exception is
     * placed in an <code>InvocationTargetException</code> and thrown in turn to
     * the caller of invoke.
     *
     * <p>If the method completes normally, the value it returns is
     * returned to the caller of invoke; if the value has a primitive
     * type, it is first appropriately wrapped in an object. If the
     * underlying method return type is void, the invocation returns
     * null.
     *
     * @exception IllegalAccessException    if the underlying method
     *              is inaccessible.
     * @exception IllegalArgumentException  if the number of actual and formal
     *              parameters differ, or if an unwrapping conversion fails.
     * @exception InvocationTargetException if the underlying method
     *              throws an exception.
     * @exception NullPointerException      if the specified object is null
     *             and the method is an instance method.
     * @exception ExceptionInInitializerError if the initialization
     *             provoked by this method fails.
     */
    public native Object invoke(Object obj, Object[] args)
	throws IllegalAccessException, IllegalArgumentException,
	    InvocationTargetException;

    /*
     * Avoid clone()
     */
    static Class[] copy(Class[] in) {
	int l = in.length;
	if (l == 0)
	    return in;
	Class[] out = new Class[l];
	for (int i = 0; i < l; i++)
	    out[i] = in[i];
	return out;
    }
}
