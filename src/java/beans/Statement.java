/*
 * @(#)Statement.java	1.18 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.beans;

import java.lang.reflect.*;
import java.util.*;

/**
 * A <code>Statement</code> object represents a primitive statement
 * in which a single method is applied to a target and
 * a set of arguments - as in <code>"a.setFoo(b)"</code>.
 * Note that where this example uses names
 * to denote the target and its argument, a statement
 * object does not require a name space and is constructed with
 * the values themselves.
 * The statement object associates the named method
 * with its environment as a simple set of values:
 * the target and an array of argument values.
 *
 * @since 1.4
 *
 * @version 1.18 01/23/03
 * @author Philip Milne
 */

public class Statement {

    private static Object[] emptyArray = new Object[]{};
    private static HashMap methodCache = null;

    static ExceptionListener defaultExceptionListener = new ExceptionListener() {
        public void exceptionThrown(Exception e) {
            System.err.println(e);
            // e.printStackTrace();
            System.err.println("Continuing ...");
        }
    };

    Object target;
    String methodName;
    Object[] arguments;

    /**
     * Creates a new <code>Statement</code> object with a <code>target</code>,
     * <code>methodName</code> and <code>arguments</code> as per the parameters.
     *
     * @param target The target of this statement.
     * @param methodName The methodName of this statement.
     * @param arguments The arguments of this statement.
     *
     */
    public Statement(Object target, String methodName, Object[] arguments) {
        this.target = target;
        this.methodName = methodName;
        this.arguments = (arguments == null) ? emptyArray : arguments;
    }

    /**
     * Returns the target of this statement.
     *
     * @return The target of this statement.
     */
    public Object getTarget() {
        return target;
    }

    /**
     * Returns the name of the method.
     *
     * @return The name of the method.
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Returns the arguments of this statement.
     *
     * @return the arguments of this statement.
     */
    public Object[] getArguments() {
        return arguments;
    }

    /**
     * The execute method finds a method whose name is the same
     * as the methodName property, and invokes the method on
     * the target.
     *
     * When the target's class defines many methods with the given name
     * the implementation should choose the most specific method using
     * the algorithm specified in the Java Language Specification
     * (15.11). The dynamic class of the target and arguments are used
     * in place of the compile-time type information and, like the
     * <code>java.lang.reflect.Method</code> class itself, conversion between
     * primitive values and their associated wrapper classes is handled
     * internally.
     * <p>
     * The following method types are handled as special cases:
     * <ul>
     * <li>
     * Static methods may be called by using a class object as the target.
     * <li>
     * The reserved method name "new" may be used to call a class's constructor
     * as if all classes defined static "new" methods. Constructor invocations
     * are typically considered <code>Expression</code>s rather than <code>Statement</code>s
     * as they return a value.
     * <li>
     * The method names "get" and "set" defined in the <code>java.util.List</code>
     * interface may also be applied to array instances, mapping to
     * the static methods of the same name in the <code>Array</code> class.
     * </ul>
     */
    public void execute() throws Exception {
        invoke();
    }

    /*pp*/ static Class typeToClass(Class type) {
        return type.isPrimitive() ? typeNameToClass(type.getName()) : type;

    }

    /*pp*/ static Class typeNameToClass(String typeName) {
        typeName = typeName.intern();
        if (typeName == "boolean") return Boolean.class;
        if (typeName == "byte") return Byte.class;
        if (typeName == "char") return Character.class;
        if (typeName == "short") return Short.class;
        if (typeName == "int") return Integer.class;
        if (typeName == "long") return Long.class;
        if (typeName == "float") return Float.class;
        if (typeName == "double") return Double.class;
        if (typeName == "void") return Void.class;
        return null;
    }

    private static Class typeNameToPrimitiveClass(String typeName) {
        typeName = typeName.intern();
        if (typeName == "boolean") return boolean.class;
        if (typeName == "byte") return byte.class;
        if (typeName == "char") return char.class;
        if (typeName == "short") return short.class;
        if (typeName == "int") return int.class;
        if (typeName == "long") return long.class;
        if (typeName == "float") return float.class;
        if (typeName == "double") return double.class;
        if (typeName == "void") return void.class;
        return null;
    }

    /*pp*/ static Class primitiveTypeFor(Class wrapper) {
        if (wrapper == Boolean.class) return Boolean.TYPE;
        if (wrapper == Byte.class) return Byte.TYPE;
        if (wrapper == Character.class) return Character.TYPE;
        if (wrapper == Short.class) return Short.TYPE;
        if (wrapper == Integer.class) return Integer.TYPE;
        if (wrapper == Long.class) return Long.TYPE;
        if (wrapper == Float.class) return Float.TYPE;
        if (wrapper == Double.class) return Double.TYPE;
        if (wrapper == Void.class) return Void.TYPE;
        return null;
    }

    static Class classForName(String name) throws ClassNotFoundException {
        // l.loadClass("int") fails.
        Class primitiveType = typeNameToPrimitiveClass(name);
        if (primitiveType != null) {
            return primitiveType;
        }
        ClassLoader l = Thread.currentThread().getContextClassLoader();
        return l.loadClass(name);
    }

    /**
     * Tests each element on the class arrays for assignability.
     *
     * @param argClasses arguments to be tested
     * @param argTypes arguments from Method
     * @return true if each class in argTypes is assignable from the 
     *         corresponding class in argClasses.
     */
    private static boolean matchArguments(Class[] argClasses, Class[] argTypes) {
        boolean match = (argClasses.length == argTypes.length);
	for(int j = 0; j < argClasses.length && match; j++) {
	    Class argType = argTypes[j];
	    if (argType.isPrimitive()) {
		argType = typeToClass(argType);
	    }
	    // Consider null an instance of all classes.
	    if (argClasses[j] != null && !(argType.isAssignableFrom(argClasses[j]))) {
		match = false;
	    }
	}
        return match;
    }

    /**
     * Tests each element on the class arrays for equality.
     *
     * @param argClasses arguments to be tested
     * @param argTypes arguments from Method
     * @return true if each class in argTypes is equal to the 
     *         corresponding class in argClasses.
     */
    private static boolean matchExplicitArguments(Class[] argClasses, Class[] argTypes) {
        boolean match = (argClasses.length == argTypes.length);
	for(int j = 0; j < argClasses.length && match; j++) {
	    Class argType = argTypes[j];
	    if (argType.isPrimitive()) {
		argType = typeToClass(argType);
	    }
	    if (argClasses[j] != argType) {
		match = false;
	    }
	}
        return match;
    }

    // Pending: throw when the match is ambiguous.
    private static Method findPublicMethod(Class declaringClass, String methodName, Class[] argClasses) {
        // Many methods are "getters" which take no arguments.
        // This permits the following optimisation which
        // avoids the expensive call to getMethods().
        if (argClasses.length == 0) {
            try {
                return declaringClass.getMethod(methodName, argClasses);
            }
            catch (NoSuchMethodException e) {
            	  return null;
            }
        }
        // System.out.println("getMethods " + declaringClass + " for " + methodName);
        Method[] methods = declaringClass.getMethods();
	ArrayList list = new ArrayList();
        for(int i = 0; i < methods.length; i++) {
	    // Collect all the methods which match the signature.
            Method method = methods[i];
            if (method.getName().equals(methodName)) {
                if (matchArguments(argClasses, method.getParameterTypes())) {
		    list.add(method);
                }
            }
	}
	if (list.size() > 0) {
	    if (list.size() == 1) {
		return (Method)list.get(0);
	    }
	    else {
		ListIterator iterator = list.listIterator();
		Method method;
		while (iterator.hasNext()) {
		    method = (Method)iterator.next();
		    if (matchExplicitArguments(argClasses, method.getParameterTypes())) {
			return method;
		    }
		}
		// This list is valid. Should return something.
		return (Method)list.get(0);		
	    }
	}	    
        return null;
    }

    // Pending: throw when the match is ambiguous.
    private static Method findMethod(Class targetClass, String methodName, Class[] argClasses) {
        Method m = findPublicMethod(targetClass, methodName, argClasses);
        if (m != null && Modifier.isPublic(m.getDeclaringClass().getModifiers())) {
            return m;
        }

        /*
        Search the interfaces for a public version of this method.

        Example: the getKeymap() method of a JTextField
        returns a package private implementation of the
        of the public Keymap interface. In the Keymap
        interface there are a number of "properties" one
        being the "resolveParent" property implied by the
        getResolveParent() method. This getResolveParent()
        cannot be called reflectively because the class
        itself is not public. Instead we search the class's
        interfaces and find the getResolveParent()
        method of the Keymap interface - on which invoke
        may be applied without error.

        So in :-

            JTextField o = new JTextField("Hello, world");
            Keymap km = o.getKeymap();
            Method m1 = km.getClass().getMethod("getResolveParent", new Class[0]);
            Method m2 = Keymap.class.getMethod("getResolveParent", new Class[0]);

        Methods m1 and m2 are different. The invocation of method
        m1 unconditionally throws an IllegalAccessException where
        the invocation of m2 will invoke the implementation of the
        method. Note that (ignoring the overloading of arguments)
        there is only one implementation of the named method which
        may be applied to this target.
        */
        for(Class type = targetClass; type != null; type = type.getSuperclass()) {
            Class[] interfaces = type.getInterfaces();
            for(int i = 0; i < interfaces.length; i++) {
                m = findPublicMethod(interfaces[i], methodName, argClasses);
                if (m != null) {
                    return m;
                }
            }
        }
        return null;
    }

    private static class Signature {
        Class targetClass;
        String methodName;
        Class[] argClasses;

        public Signature(Class targetClass, String methodName, Class[] argClasses) {
            this.targetClass = targetClass;
            this.methodName = methodName;
            this.argClasses = argClasses;
        }

        public boolean equals(Object o2) {
            Signature that = (Signature)o2;
            if (!(targetClass == that.targetClass)) {
                return false;
            }
            if (!(methodName.equals(that.methodName))) {
                return false;
            }
            if (argClasses.length != that.argClasses.length) {
                return false;
            }
            for (int i = 0; i < argClasses.length; i++) {
                if (!(argClasses[i] == that.argClasses[i])) {
                  return false;
                }
            }
            return true;
        }

        // Pending(milne) Seek advice an a suitable hash function to use here.
        public int hashCode() {
            return targetClass.hashCode() * 35 + methodName.hashCode();
        }
    }

    /** A wrapper to findMethod(), which will cache its results if
      * isCaching() returns true. See clear().
      */
    static Method getMethod(Class targetClass, String methodName, Class[] argClasses) {
        if (!isCaching()) {
            return findMethod(targetClass, methodName, argClasses);
        }
        Object signature = new Signature(targetClass, methodName, argClasses);
        Method m = (Method)methodCache.get(signature);
        if (m != null) {
            // System.out.println("findMethod found " + methodName + " for " + targetClass);
            return m;
        }
        // System.out.println("findMethod searching " + targetClass + " for " + methodName);
        m = findMethod(targetClass, methodName, argClasses);
        if (m != null) {
            methodCache.put(signature, m);
        }
        return m;
    }

    static void setCaching(boolean b) {
        methodCache = b ? new HashMap() : null;
    }

    private static boolean isCaching() {
        return methodCache != null;
    }

    Object invoke() throws Exception {
        // System.out.println("Invoking: " + toString());
        Object target = getTarget();
        String methodName = getMethodName();
        Object[] arguments = getArguments();
        // Class.forName() won't load classes outside
        // of core from a class inside core. Special
        // case this method.
        if (target == Class.class && methodName == "forName") {
            return classForName((String)arguments[0]);
        }
        Class[] argClasses = new Class[arguments.length];
        for(int i = 0; i < arguments.length; i++) {
            argClasses[i] = (arguments[i] == null) ? null : arguments[i].getClass();
        }

        AccessibleObject m = null;
        if (target instanceof Class) {
            /*
            For class methods, simluate the effect of a meta class
            by taking the union of the static methods of the
            actual class, with the instance methods of "Class.class"
            and the overloaded "newInstance" methods defined by the
            constructors.
            This way "System.class", for example, will perform both
            the static method getProperties() and the instance method
            getSuperclass() defined in "Class.class".
            */
            if (methodName == "new") {
                methodName = "newInstance";
            }
            // Provide a short form for array instantiation by faking an nary-constructor. 
            if (methodName == "newInstance" && ((Class)target).isArray()) {
                Object result = Array.newInstance(((Class)target).getComponentType(), arguments.length); 
                for(int i = 0; i < arguments.length; i++) { 
                    Array.set(result, i, arguments[i]); 
                }
                return result; 
            }
            if (methodName == "newInstance" && arguments.length != 0) {
                // The Character class, as of 1.4, does not have a constructor
                // which takes a String. All of the other "wrapper" classes
                // for Java's primitive types have a String constructor so we
                // fake such a constructor here so that this special case can be
                // ignored elsewhere.
                if (target == Character.class && arguments.length == 1 && argClasses[0] == String.class) {
                    return new Character(((String)arguments[0]).charAt(0));
                }
                Constructor[] constructors = ((Class)target).getConstructors();
                // PENDING: Implement the resolutuion of ambiguities properly.
                for(int i = 0; i < constructors.length; i++) {
                    Constructor constructor = constructors[i];
                    if (matchArguments(argClasses, constructor.getParameterTypes())) {
                        m = constructor;
                    }
                }
            }
            if (m == null) {
                m = getMethod((Class)target, methodName, argClasses);
            }
            if (m == null) {
                m = getMethod(Class.class, methodName, argClasses);
            }
        }
        else {
            /*
            This special casing of arrays is not necessary, but makes files
            involving arrays much shorter and simplifies the archiving infrastrcure.
            The Array.set() method introduces an unusual idea - that of a static method
            changing the state of an instance. Normally statements with side
            effects on objects are instance methods of the objects themselves
            and we reinstate this rule (perhaps temporarily) by special-casing arrays.
            */
            if (target.getClass().isArray() && (methodName == "set" || methodName == "get")) {
                int index = ((Integer)arguments[0]).intValue();
                if (methodName == "get") {
                    return Array.get(target, index);
                }
                else {
                    Array.set(target, index, arguments[1]);
                    return null;
                }
            }
            m = getMethod(target.getClass(), methodName, argClasses);
        }
        if (m != null) {
            // System.err.println("Calling \"" + methodName + "\"" + " on " + ((o == null) ? null : target.getClass()));
            try {
                if (m instanceof Method) {
                    return ((Method)m).invoke(target, arguments);
                }
                else {
                    return ((Constructor)m).newInstance(arguments);
                }
            }
            catch (IllegalAccessException iae) {
                throw new IllegalAccessException(toString());
            }
            catch (InvocationTargetException ite) {
                Throwable te = ite.getTargetException();
                if (te instanceof Exception) {
                    throw (Exception)te;
                }
                else {
                    throw ite;
                }
            }
        }
        throw new NoSuchMethodException(toString());
    }

    /*pp*/ String instanceName(Object instance) { 
        return (instance != null && instance.getClass() == String.class) 
            ? "\""+(String)instance + "\"" 
            : NameGenerator.instanceName(instance);
    }

    /**
     * Prints the value of this statement using a Java-style syntax.
     */
    public String toString() {
        // Respect a subclass's implementation here.
        Object target = getTarget();
        String methodName = getMethodName();
        Object[] arguments = getArguments();

        StringBuffer result = new StringBuffer(instanceName(target) + "." + methodName + "(");
        int n = arguments.length;
        for(int i = 0; i < n; i++) {
            result.append(instanceName(arguments[i]));
            if (i != n -1) {
                result.append(", ");
            }
        }
        result.append(");");
        return result.toString();
    }
}














































