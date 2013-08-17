/*
 * @(#)Class.java	1.57 98/07/01
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

package java.lang;
import java.lang.reflect.Member;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.io.InputStream;

/**
 * Instances of the class </code>Class</code> represent classes and 
 * interfaces in a running Java application.
 * Every array also belongs to a class that is reflected as a Class
 * object that is shared by all arrays with the same element type and
 * number of dimensions.  Finally, the either primitive Java types
 * (boolean, byte, char, short, int, long, float, and double) and
 * the keyword void are also represented as Class objects.
 * <p>
 * There is no public constructor for the class </code>Class</code>. 
 * </code>Class</code> objects are constructed automatically by the Java 
 * Virtual Machine as classes are loaded and by calls to the 
 * <code>defineClass</code> method in the class loader. 
 * <p>
 * The following example uses a Class object to print the Class name
 * of an object:
 * <p><pre><blockquote>
 *     void printClassName(Object obj) {
 *         System.out.println("The class of " + obj +
 *                            " is " + obj.getClass().getName());
 *     }
 * </blockquote></pre>
 *
 * @author  unascribed
 * @version 1.57, 07/01/98
 * @see     java.lang.ClassLoader#defineClass(byte[], int, int)
 * @since   JDK1.0
 */
public final
class Class implements java.io.Serializable {
    /*
     * Constructor. Only the Java Virtual Machine creates Class
     * objects.
     */
    private Class() {}

    /**
     * Converts the object to a string. The string representation is the 
     * string <code>"class"</code> or <code>"interface"</code> followed 
     * by a space and then the fully qualified name of the class. 
     * If this Class object represents a primitive type,
     * returns the name of the primitive type.
     *
     * @return  a string representation of this class object. 
     * @since   JDK1.0
     */
    public String toString() {
	return (isInterface() ? "interface " : (isPrimitive() ? "" : "class "))
	    + getName();
    }

    /**
     * Returns the <code>Class</code> object associated with the class 
     * with the given string name. 
     * Given the fully-qualified name for a class or interface, this
     * method attempts to locate, load and link the class.  If it
     * succeeds, returns the Class object representing the class.  If
     * it fails, the method throws a ClassNotFoundException.
     * <p>
     * For example, the following code fragment returns the runtime 
     * <code>Class</code> descriptor for the class named 
     * <code>java.lang.Thread</code>: 
     * <ul><code>
     *   Class&nbsp;t&nbsp;= Class.forName("java.lang.Thread")
     * </code></ul>
     *
     * @param      className   the fully qualified name of the desired class.
     * @return     the <code>Class</code> descriptor for the class with the
     *             specified name.
     * @exception  ClassNotFoundException  if the class could not be found.
     * @since      JDK1.0
     */
    public static native Class forName(String className)
	throws ClassNotFoundException;

    /**
     * Creates a new instance of a class. 
     *
     * @return     a newly allocated instance of the class represented by this
     *             object. This is done exactly as if by a <code>new</code>
     *             expression with an empty argument list.
     * @exception  IllegalAccessException  if the class or initializer is
     *               not accessible.
     * @exception  InstantiationException  if an application tries to
     *               instantiate an abstract class or an interface, or if the
     *               instantiation fails for some other reason.
     * @since     JDK1.0
     */
    public native Object newInstance() 
	throws InstantiationException, IllegalAccessException;

    /**
     * This method is the dynamic equivalent of the Java language
     * <code>instanceof</code> operator. The method returns true if
     * the specified Object argument is non-null and can be cast to
     * the reference type represented by this Class object without
     * raising a ClassCastException. It returns false otherwise.
     *
     * <p>Specifically, if this Class object represents a declared
     * class, returns true if the specified Object argument is an
     * instance of the represented class (or of any of its
     * subclasses); false otherwise. If this Class object represents
     * an array class, returns true if the specified Object argument
     * can be converted to an object of the array type by an identity
     * conversion or by a widening reference conversion; false
     * otherwise. If this Class object represents an interface,
     * returns true if the class or any superclass of the
     * specified Object argument implements this interface; false
     * otherwise. If this Class object represents a primitive type,
     * returns false.
     *
     * @param   obj The object to check
     * @since   JDK1.1
     */
    public native boolean isInstance(Object obj);

    /**
     * Determines if the class or interface
     * represented by this Class object is either the same as, or is a
     * superclass or superinterface of, the class or interface
     * represented by the specified Class parameter. It returns true
     * if so, false otherwise. If this Class object represents a
     * primitive type, returns true if the specified Class parameter
     * is exactly this Class object, false otherwise.
     *
     * <p>Specifically, this method tests whether the type represented
     * by the specified Class parameter can be converted to the type
     * represented by this Class object via an identity conversion or
     * via a widening reference conversion. See <em>The Java Language
     * Specification</em>, sections 5.1.1 and 5.1.4 , for details.
     *
     * @exception NullPointerException if the specified Class parameter is null.
     * @since   JDK1.1
     */
    public native boolean isAssignableFrom(Class cls);

    /**
     * Determines if the specified Class object represents an interface type.
     *
     * @return  <code>true</code> if this object represents an interface;
     *          <code>false</code> otherwise.
     * @since   JDK1.0
     */
    public native boolean isInterface();

    /**
     * If this Class object represents an array type, returns true,
     * otherwise returns false.
     *
     * @since   JDK1.1
     */
    public native boolean isArray();

    /**
     * Determines if the specified Class object represents a primitive Java
     * type.
     *
     * <p>There are nine predefined Class objects to represent the eight
     * primitive Java types and void.  These are created by the Java
     * Virtual Machine, and have the same names as the primitive types
     * that they represent, namely boolean, byte, char, short, int,
     * long, float, and double, and void.
     *
     * <p>These objects may only be accessed via the following public
     * static final variables, and are the only Class objects for
     * which this method returns true.
     *
     * @see     java.lang.Boolean#TYPE
     * @see     java.lang.Character#TYPE
     * @see     java.lang.Byte#TYPE
     * @see     java.lang.Short#TYPE
     * @see     java.lang.Integer#TYPE
     * @see     java.lang.Long#TYPE
     * @see     java.lang.Float#TYPE
     * @see     java.lang.Double#TYPE
     * @see     java.lang.Void#TYPE
     * @since   JDK1.1
     */
    public native boolean isPrimitive();

    /**
     * Returns the fully-qualified name of the type (class, interface,
     * array, or primitive) represented by this Class object, as a String.
     *
     * @return  the fully qualified name of the class or interface
     *          represented by this object.
     * @since   JDK1.0
     */
    public native String getName();

    /**
     * Determines the class loader for the class. 
     *
     * @return  the class loader that created the class or interface
     *          represented by this object, or <code>null</code> if the
     *          class was not created by a class loader.
     * @see     java.lang.ClassLoader
     * @since   JDK1.0
     */
    public native ClassLoader getClassLoader();

    /**
     * If this object represents any class other than the class 
     * </code>Object</code>, then the object that represents the superclass 
     * of that class is returned. 
     * <p>
     * If this object is the one that represents the class 
     * </code>Object</code> or this object represents an interface, 
     * </code>null</code> is returned. 
     *
     * @return  the superclass of the class represented by this object.
     * @since   JDK1.0
     */
    public native Class getSuperclass();

    /**
     * Determines the interfaces implemented by the class or interface 
     * represented by this object. 
     * <p>
     * If this object represents a class, the return value is an array 
     * containing objects representing all interfaces implemented by the 
     * class. The order of the interface objects in the array corresponds 
     * to the order of the interface names in the </code>implements</code> 
     * clause of the declaration of the class represented by this object. 
     * <p>
     * If this object represents an interface, the array contains 
     * objects representing all interfaces extended by the interface. The 
     * order of the interface objects in the array corresponds to the 
     * order of the interface names in the </code>extends</code> clause of 
     * the declaration of the interface represented by this object. 
     * <p>
     * If the class or interface implements no interfaces, the method 
     * returns an array of length 0. 
     *
     * @return  an array of interfaces implemented by this class.
     * @since   JDK1.0
     */
    public native Class[] getInterfaces();

    /**
     * If this class represents an array type, returns the Class
     * object representing the component type of the array; otherwise
     * returns null.
     *
     * @see     java.lang.reflect.Array
     * @since   JDK1.1
     */
    public native Class getComponentType();

    /**
     * Returns the Java language modifiers for this class or
     * interface, encoded in an integer. The modifiers consist of the
     * Java Virtual Machine's constants for public, protected,
     * private, final, and interface; they should be decoded using the
     * methods of class Modifier.
     *
     * <p>The modifier encodings are defined in <em>The Java Virtual
     * Machine Specification</em>, table 4.1.
     *
     * @see     java.lang.reflect.Modifier
     * @since   JDK1.1
     */
    public native int getModifiers();

    /**
     * Get the signers of this class.
     *
     * @since   JDK1.1
     */
    public native Object[] getSigners();
	
    /**
     * Set the signers of this class.
     */
    native void setSigners(Object[] signers);

    /**
     * Not implemented in this version of the 
     * Java<font size="-2"><sup>TM</sup></font> Development Kit. 
     * <p>
     * If the class or interface represented by this Class object is
     * a member of another class, returns the Class object
     * representing the class of which it is a member (its
     * <em>declaring class</em>).  Returns null if this class or
     * interface is not a member of any other class.
     *
     * @since   JDK1.1
     */
    public Class getDeclaringClass() {
	return null;				/* not implemented */
    }

    /**
     * Not implemented in this version of the 
     * Java<font size="-2"><sup>TM</sup></font> Development Kit. 
     * <p>
     * Returns an array containing Class objects representing all the
     * public classes and interfaces that are members of the class
     * represented by this Class object.  This includes public class
     * and interface members inherited from superclasses and public
     * class and interface members declared by the class.  Returns an
     * array of length 0 if the class has no public member classes or
     * interfaces, or if this Class object represents a primitive
     * type.
     *
     * @since   JDK1.1
     */
    public Class[] getClasses() {
	return new Class[0];			/* not implemented */
    }

    /**
     * Returns an array containing Field objects reflecting all the
     * accessible public fields of the class or interface represented
     * by this Class object.  Returns an array of length 0 if the
     * class or interface has no accessible public fields, or if it
     * represents an array type or a primitive type.
     *
     * <p>Specifically, if this Class object represents a class,
     * returns the public fields of this class and of all its
     * superclasses.  If this Class object represents an interface,
     * returns the fields of this interface and of all its
     * superinterfaces.  If this Class object represents an array type
     * or a primitive type, returns an array of length 0.
     *
     * <p>The implicit length field for array types is not reflected
     * by this method. User code should use the methods of class Array
     * to manipulate arrays.
     *
     * <p>See <em>The Java Language Specification</em>, sections 8.2 and 8.3.
     *
     * @exception SecurityException    if access to the information is denied.
     * @see       java.lang.reflect.Field
     * @since     JDK1.1
     */
    public Field[] getFields() throws SecurityException {
	checkMemberAccess(Member.PUBLIC);
	return getFields0(Member.PUBLIC);
    }

    /**
     * Returns an array containing Method objects reflecting all the
     * public <em>member</em> methods of the class or interface
     * represented by this Class object, including those declared by
     * the class or interface and and those inherited from
     * superclasses and superinterfaces. Returns an array of length 0
     * if the class or interface has no public member methods.
     *
     * <p>See <em>The Java Language Specification</em>, sections 8.2
     * and 8.4.
     *
     * @exception SecurityException    if access to the information is denied.
     * @see       java.lang.reflect.Method
     * @since     JDK1.1
     */
    public Method[] getMethods() throws SecurityException {
	checkMemberAccess(Member.PUBLIC);
	return getMethods0(Member.PUBLIC);
    }

    /**
     * Returns an array containing Constructor objects reflecting
     * all the public constructors of the class represented by this
     * Class object.  An array of length 0 is returned if the class
     * has no public constructors.
     *
     * @exception SecurityException    if access to the information is denied.
     * @see       java.lang.reflect.Constructor
     * @since     JDK1.1
     */
    public Constructor[] getConstructors() throws SecurityException {
	checkMemberAccess(Member.PUBLIC);
	return getConstructors0(Member.PUBLIC);
    }

    /**
     * Returns a Field object that reflects the specified public
     * member field of the class or interface represented by
     * this Class object. The name parameter is a String specifying
     * the simple name of the desired field.
     *
     * <p>The field to be reflected is located by searching all the
     * member fields of the class or interface represented by this
     * Class object for a public field with the specified name.
     *
     * <p>See <em>The Java Language Specification</em>, sections 8.2 and 8.3.
     *
     * @exception NoSuchFieldException if a field with the specified name is
     *              not found.
     * @exception SecurityException    if access to the information is denied.
     * @see       java.lang.reflect.Field
     * @since     JDK1.1
     */
    public Field getField(String name)
	throws NoSuchFieldException, SecurityException {
	checkMemberAccess(Member.PUBLIC);
	return getField0(name, Member.PUBLIC);
    }

    /**
     * Returns a Method object that reflects the specified public
     * member method of the class or interface represented by this
     * Class object. The name parameter is a String specifying the
     * simple name the desired method, and the parameterTypes
     * parameter is an array of Class objects that identify the
     * method's formal parameter types, in declared order.
     *
     * <p>The method to reflect is located by searching all the member
     * methods of the class or interface represented by this Class
     * object for a public method with the specified name and exactly
     * the same formal parameter types.
     *
     * <p>See <em>The Java Language Specification</em>, sections 8.2
     * and 8.4.
     *
     * @exception NoSuchMethodException if a matching method is not found.
     * @exception SecurityException    if access to the information is denied.
     * @see       java.lang.reflect.Method
     * @since     JDK1.1
     */
    public Method getMethod(String name, Class[] parameterTypes)
	throws NoSuchMethodException, SecurityException {
	checkMemberAccess(Member.PUBLIC);
	return getMethod0(name, parameterTypes, Member.PUBLIC);
    }

    /**
     * Returns a Constructor object that reflects the specified public
     * constructor of the class represented by this Class object. The
     * parameterTypes parameter is an array of Class objects that
     * identify the constructor's formal parameter types, in declared
     * order.
     *
     * <p>The constructor to reflect is located by searching all the
     * constructors of the class represented by this Class object for
     * a public constructor with the exactly the same formal parameter
     * types.
     *
     * @exception NoSuchMethodException if a matching method is not found.
     * @exception SecurityException     if access to the information is denied.
     * @see       java.lang.reflect.Constructor
     * @since     JDK1.1
     */
    public Constructor getConstructor(Class[] parameterTypes)
	throws NoSuchMethodException, SecurityException {
	checkMemberAccess(Member.PUBLIC);
	return getConstructor0(parameterTypes, Member.PUBLIC);
    }

    /**
     * Not implemented in this version of the 
     * Java<font size="-2"><sup>TM</sup></font> Development Kit. 
     * <p>
     * Returns an array of Class objects reflecting all the classes
     * and interfaces declared as members of the class represented by
     * this Class object. This includes public, protected, default
     * (package) access, and private classes and interfaces declared
     * by the class, but excludes inherited classes and interfaces.
     * Returns an array of length 0 if the class declares no classes
     * or interfaces as members, or if this Class object represents a
     * primitive type.
     *
     * @exception SecurityException    if access to the information is denied.
     * @since     JDK1.1
     */
    public Class[] getDeclaredClasses() throws SecurityException {
	checkMemberAccess(Member.DECLARED);
	return new Class[0];			/* not implemented */
    }

    /**
     * Returns an array of Field objects reflecting all the fields
     * declared by the class or interface represented by this Class
     * object. This includes public, protected, default (package)
     * access, and private fields, but excludes inherited
     * fields. Returns an array of length 0 if the class or interface
     * declares no fields, or if this Class object represents a
     * primitive type.
     *
     * See <em>The Java Language Specification</em>, sections 8.2 and
     * 8.3.
     *
     * @exception SecurityException    if access to the information is denied.
     * @see       java.lang.reflect.Field
     * @since     JDK1.1
     */
    public Field[] getDeclaredFields() throws SecurityException {
	checkMemberAccess(Member.DECLARED);
	return getFields0(Member.DECLARED);
    }

    /**
     * Returns an array of Method objects reflecting all the methods
     * declared by the class or interface represented by this Class
     * object. This includes public, protected, default (package)
     * access, and private methods, but excludes inherited
     * methods. Returns an array of length 0 if the class or interface
     * declares no methods, or if this Class object represents a
     * primitive type.
     *
     * <p>See <em>The Java Language Specification</em>, section 8.2.
     *
     * @exception SecurityException    if access to the information is denied.
     * @see       java.lang.reflect.Method
     * @since     JDK1.1
     */
    public Method[] getDeclaredMethods() throws SecurityException {
	checkMemberAccess(Member.DECLARED);
	return getMethods0(Member.DECLARED);
    }

    /**
     * Returns an array of Constructor objects reflecting all the
     * constructors declared by the class represented by this Class
     * object. These are public, protected, default (package) access,
     * and private constructors.  Returns an array of length 0 if this
     * Class object represents an interface or a primitive type.
     *
     * <p>See <em>The Java Language Specification</em>, section 8.2.
     *
     * @exception SecurityException    if access to the information is denied.
     * @see       java.lang.reflect.Constructor
     * @since     JDK1.1
     */
    public Constructor[] getDeclaredConstructors() throws SecurityException {
	checkMemberAccess(Member.DECLARED);
	return getConstructors0(Member.DECLARED);
    }

    /**
     * Returns a Field object that reflects the specified declared
     * field of the class or interface represented by this Class
     * object. The name parameter is a String that specifies the
     * simple name of the desired field.
     *
     * @exception NoSuchFieldException if a field with the specified name is
     *              not found.
     * @exception SecurityException    if access to the information is denied.
     * @see       java.lang.reflect.Field
     * @since     JDK1.1
     */
    public Field getDeclaredField(String name)
	throws NoSuchFieldException, SecurityException {
	checkMemberAccess(Member.DECLARED);
	return getField0(name, Member.DECLARED);
    }

    /**
     * Returns a Method object that reflects the specified declared
     * method of the class or interface represented by this Class
     * object. The name parameter is a String that specifies the
     * simple name of the desired method, and the parameterTypes
     * parameter is an array of Class objects that identify the
     * method's formal parameter types, in declared order.
     *
     * @exception NoSuchMethodException if a matching method is not found.
     * @exception SecurityException     if access to the information is denied.
     * @see       java.lang.reflect.Method
     * @since     JDK1.1
     */
    public Method getDeclaredMethod(String name, Class[] parameterTypes)
	throws NoSuchMethodException, SecurityException {
	checkMemberAccess(Member.DECLARED);
	return getMethod0(name, parameterTypes, Member.DECLARED);
    }

    /**
     * Returns a Constructor object that reflects the specified declared
     * constructor of the class or interface represented by this Class
     * object.  The parameterTypes parameter is an array of Class
     * objects that identify the constructor's formal parameter types,
     * in declared order.
     *
     * @exception NoSuchMethodException if a matching method is not found.
     * @exception SecurityException     if access to the information is denied.
     * @see       java.lang.reflect.Constructor
     * @since     JDK1.1
     */
    public Constructor getDeclaredConstructor(Class[] parameterTypes)
	throws NoSuchMethodException, SecurityException {
	checkMemberAccess(Member.DECLARED);
	return getConstructor0(parameterTypes, Member.DECLARED);
    }

    /**
     * Finds a resource with a given name.  Will return null if no
     * resource with this name is found.  The rules for searching a
     * resources associated with a given class are implemented by the
     * ClassLoader of the class.<p>
     *
     * The Class methods delegate to ClassLoader methods, after applying
     * a naming convention: if the resource name starts with "/", it is used
     * as is.  Otherwise, the name of the package is prepended, after
     * converting "." to "/".
     *
     * @param   name the string representing the resource to be found
     * @return  the <code>InputStream</code> object having the 
     *             specified name, or <code>null</code> if no 
     *             resource with the specified name is found.
     * @see     java.lang.ClassLoader
     * @see     java.lang.Class#getResource
     * @since   JDK1.1
     */
    public InputStream getResourceAsStream(String name) {
	name = resolveName(name);
	ClassLoader cl = getClassLoader();
	if (cl==null) {
	    // A system class.
	    return ClassLoader.getSystemResourceAsStream(name);
	}
	return cl.getResourceAsStream(name);
    }

    /**
     * Finds a resource with the specified name. The rules for searching 
     * for resources associated with a given class are implemented by 
     * the class loader of the class.
     * <p>
     * The Class methods delegate to ClassLoader methods, after applying
     * a naming convention: if the resource name starts with "/", it is used
     * as is.  Otherwise, the name of the package is prepended, after
     * converting "." to "/".
     *
     * @param   name the string representing the resource to be found.
     * @return  the <code>URL</code> object having the specified name,  
     *             or <code>null</code> if no resource with the specified 
     *             name is found.
     * @see     java.lang.ClassLoader 
     * @see     java.lang.Class#getResourceAsStream
     * @since   JDK1.1
     */
    public java.net.URL getResource(String name) {
	name = resolveName(name);
	ClassLoader cl = getClassLoader();
	if (cl==null) {
	    // A system class.
	    return ClassLoader.getSystemResource(name);
	}
	return cl.getResource(name);
    }

    /*
     * Return the Virtual Machine's Class object for the named
     * primitive type.
     */
    static native Class getPrimitiveClass(String name);

    /*
     * Check if client is allowed to access members.  If access is
     * denied, throw a SecurityException.
     *
     * <p>Default policy: allow all clients access with normal Java
     * access control.
     */
    private void checkMemberAccess(int which) {
	SecurityManager s = System.getSecurityManager();
	if (s != null) {
	    s.checkMemberAccess(this, which);
	}
    }

    /**
     * Add a package name prefix if the name is not absolute
     * Remove leading "/" if name is absolute
     */
    private String resolveName(String name) {
	if (name == null) {
	    return name;
	}
	if (!name.startsWith("/")) {
	    Class c = this;
	    while (c.isArray()) {
		c = c.getComponentType();
	    }
	    String baseName = c.getName();
	    int index = baseName.lastIndexOf('.');
	    if (index != -1) {
		name = baseName.substring(0, index).replace('.', '/')
		    +"/"+name;
	    }
	} else {
	    name = name.substring(1);
	}
	return name;
    }

    private native Field[] getFields0(int which);
    private native Method[] getMethods0(int which);
    private native Constructor[] getConstructors0(int which);
    private native Field getField0(String name, int which);
    private native Method getMethod0(String name, Class[] parameterTypes,
	int which);
    private native Constructor getConstructor0(Class[] parameterTypes,
	int which);

}
