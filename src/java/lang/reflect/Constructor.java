/*
 * @(#)Constructor.java	1.33 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang.reflect;

import sun.reflect.ConstructorAccessor;
import sun.reflect.Reflection;

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
 * @author	Kenneth Russell
 * @author	Nakul Saraiya
 */
public final
class Constructor extends AccessibleObject implements Member {

    private Class		clazz;
    private int			slot;
    private Class[]		parameterTypes;
    private Class[]		exceptionTypes;
    private int			modifiers;
    private volatile ConstructorAccessor constructorAccessor;
    // For sharing of ConstructorAccessors. This branching structure
    // is currently only two levels deep (i.e., one root Constructor
    // and potentially many Constructor objects pointing to it.)
    private Constructor         root;

    /**
     * Package-private constructor used by ReflectAccess to enable
     * instantiation of these objects in Java code from the java.lang
     * package via sun.reflect.LangReflectAccess.
     */
    Constructor(Class declaringClass,
                Class[] parameterTypes,
                Class[] checkedExceptions,
                int modifiers,
                int slot)
    {
        this.clazz = declaringClass;
        this.parameterTypes = parameterTypes;
        this.exceptionTypes = checkedExceptions;
        this.modifiers = modifiers;
        this.slot = slot;
    }

    /**
     * Package-private routine (exposed to java.lang.Class via
     * ReflectAccess) which returns a copy of this Constructor. The copy's
     * "root" field points to this Constructor.
     */
    Constructor copy() {
        // This routine enables sharing of ConstructorAccessor objects
        // among Constructor objects which refer to the same underlying
        // method in the VM. (All of this contortion is only necessary
        // because of the "accessibility" bit in AccessibleObject,
        // which implicitly requires that new java.lang.reflect
        // objects be fabricated for each reflective call on Class
        // objects.)
        Constructor res = new Constructor(clazz, parameterTypes,
                                          exceptionTypes, modifiers, slot);
        res.root = this;
        // Might as well eagerly propagate this if already present
        res.constructorAccessor = constructorAccessor;
        return res;
    }

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
     *
     * @return the parameter types for the constructor this object
     * represents
     */
    public Class[] getParameterTypes() {
	return Method.copy(parameterTypes);
    }

    /**
     * Returns an array of <code>Class</code> objects that represent the types of
     * of exceptions declared to be thrown by the underlying constructor
     * represented by this <code>Constructor</code> object.  Returns an array of
     * length 0 if the constructor declares no exceptions in its <code>throws</code> clause.
     *
     * @return the exception types declared as being thrown by the
     * constructor this object represents
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
     *
     * <p>If the number of formal parameters required by the underlying constructor
     * is 0, the supplied <code>initargs</code> array may be of length 0 or null.
     *
     * <p>If the required access and argument checks succeed and the
     * instantiation will proceed, the constructor's declaring class
     * is initialized if it has not already been initialized.
     *
     * <p>If the constructor completes normally, returns the newly
     * created and initialized instance.
     *
     * @param initargs array of objects to be passed as arguments to
     * the constructor call; values of primitive types are wrapped in
     * a wrapper object of the appropriate type (e.g. a <tt>float</tt>
     * in a {@link java.lang.Float Float})
     *
     * @return a new object created by calling the constructor
     * this object represents
     * 
     * @exception IllegalAccessException    if this <code>Constructor</code> object
     *              enforces Java language access control and the underlying
     *              constructor is inaccessible.
     * @exception IllegalArgumentException  if the number of actual
     *              and formal parameters differ; if an unwrapping
     *              conversion for primitive arguments fails; or if,
     *              after possible unwrapping, a parameter value
     *              cannot be converted to the corresponding formal
     *              parameter type by a method invocation conversion.
     * @exception InstantiationException    if the class that declares the
     *              underlying constructor represents an abstract class.
     * @exception InvocationTargetException if the underlying constructor
     *              throws an exception.
     * @exception ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     */
    public Object newInstance(Object[] initargs)
	throws InstantiationException, IllegalAccessException,
               IllegalArgumentException, InvocationTargetException
    {
        if (!override) {
            if (!Reflection.quickCheckMemberAccess(clazz, modifiers)) {
                Class caller = Reflection.getCallerClass(2);
                if (securityCheckCache != caller) {
                    Reflection.ensureMemberAccess(caller, clazz, null, modifiers);
                    securityCheckCache = caller;
                }
            }
        }
        if (constructorAccessor == null) acquireConstructorAccessor();
        return constructorAccessor.newInstance(initargs);
    }

    // NOTE that there is no synchronization used here. It is correct
    // (though not efficient) to generate more than one
    // ConstructorAccessor for a given Constructor. However, avoiding
    // synchronization will probably make the implementation more
    // scalable.
    private void acquireConstructorAccessor() {
        // First check to see if one has been created yet, and take it
        // if so.
        ConstructorAccessor tmp = null;
        if (root != null) tmp = root.getConstructorAccessor();
        if (tmp != null) {
            constructorAccessor = tmp;
            return;
        }
        // Otherwise fabricate one and propagate it up to the root
        tmp = reflectionFactory.newConstructorAccessor(this);
        setConstructorAccessor(tmp);
    }

    // Returns ConstructorAccessor for this Constructor object, not
    // looking up the chain to the root
    ConstructorAccessor getConstructorAccessor() {
        return constructorAccessor;
    }

    // Sets the ConstructorAccessor for this Constructor object and
    // (recursively) its root
    void setConstructorAccessor(ConstructorAccessor accessor) {
        constructorAccessor = accessor;
        // Propagate up
        if (root != null) {
            root.setConstructorAccessor(accessor);
        }
    }

    int getSlot() {
        return slot;
    }
}
