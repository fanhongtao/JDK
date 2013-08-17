/*
 * @(#)Field.java	1.10 98/07/01
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
 * A Field provides information about, and dynamic access to, a
 * single field of a class or an interface.  The reflected field may
 * be a class (static) field or an instance field.
 *
 * <p>A Field permits widening conversions to occur during a get or
 * set access operation, but throws an IllegalArgumentException if a
 * narrowing conversion would occur.
 *
 * @see Member
 * @see java.lang.Class
 * @see java.lang.Class#getFields()
 * @see java.lang.Class#getField()
 * @see java.lang.Class#getDeclaredFields()
 * @see java.lang.Class#getDeclaredField()
 *
 * @author Nakul Saraiya
 */
public final
class Field implements Member {

    private Class		clazz;
    private int			slot;
    private String		name;
    private Class		type;

    /**
     * Constructor.  Only the Java Virtual Machine may construct a Field.
     */
    private Field() {}

    /**
     * Returns the Class object representing the class or interface
     * that declares the field represented by this Field object.
     */
    public Class getDeclaringClass() {
	return clazz;
    }

    /**
     * Returns the name of the field represented by this Field object.
     */
    public String getName() {
	return name;
    }

    /**
     * Returns the Java language modifiers for the field represented
     * by this Field object, as an integer. The Modifier class should
     * be used to decode the modifiers.
     *
     * @see Modifier
     */
    public native int getModifiers();

    /**
     * Returns a Class object that identifies the declared type for
     * the field represented by this Field object.
     */
    public Class getType() {
	return type;
    }

    /**
     * Compares this Field against the specified object.  Returns
     * true if the objects are the same.  Two Fields are the same if
     * they were declared by the same class and have the same name
     * and type.
     */
    public boolean equals(Object obj) {
	if (obj != null && obj instanceof Field) {
	    Field other = (Field)obj;
	    return (getDeclaringClass() == other.getDeclaringClass())
		&& (getName().equals(other.getName()))
		&& (getType() == other.getType());
	}
	return false;
    }

    /**
     * Returns a hashcode for this Field.  This is computed as the
     * exclusive-or of the hashcodes for the underlying field's
     * declaring class name and its name.
     */
    public int hashCode() {
	return getDeclaringClass().getName().hashCode() ^ getName().hashCode();
    }

    /**
     * Return a string describing this Field.  The format is
     * the access modifiers for the field, if any, followed
     * by the field type, followed by a space, followed by
     * the fully-qualified name of the class declaring the field,
     * followed by a period, followed by the name of the field.
     * For example:
     * <pre>
     *    public static final int java.lang.Thread.MIN_PRIORITY
     *    private int java.io.FileDescriptor.fd
     * </pre>
     *
     * <p>The modifiers are placed in canonical order as specified by
     * "The Java Language Specification".  This is <tt>public</tt>,
     * <tt>protected</tt> or <tt>private</tt> first, and then other
     * modifiers in the following order: <tt>static</tt>, <tt>final</tt>,
     * <tt>transient</tt>, <tt>volatile</tt>.
     */
    public String toString() {
	int mod = getModifiers();
	return (((mod == 0) ? "" : (Modifier.toString(mod) + " "))
	    + getTypeName(getType()) + " "
	    + getTypeName(getDeclaringClass()) + "."
	    + getName());
    }

    /**
     * Returns the value of the field represented by this Field, on
     * the specified object. The value is automatically wrapped in an
     * object if it has a primitive type.
     *
     * <p>The underlying field's value is obtained as follows:
     *
     * <p>If the underlying field is a static field, the object argument
     * is ignored; it may be null.
     *
     * <p>Otherwise, the underlying field is an instance field.  If the
     * specified object argument is null, the method throws a
     * NullPointerException. If the specified object is not an
     * instance of the class or interface declaring the underlying
     * field, the method throws an IllegalArgumentException.
     *
     * <p>If this Field object enforces Java language access control, and
     * the underlying field is inaccessible, the method throws an
     * IllegalAccessException.
     *
     * <p>Otherwise, the value is retrieved from the underlying instance
     * or static field.  If the field has a primitive type, the value
     * is wrapped in an object before being returned, otherwise it is
     * returned as is.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field.
     * @exception NullPointerException      if the specified object is null.
     */
    public native Object get(Object obj)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Get the value of a field as a boolean on specified object.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the field value cannot be
     *              converted to the return type by a widening conversion.
     * @see       Field#get
     */
    public native boolean getBoolean(Object obj)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Get the value of a field as a byte on specified object.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the field value cannot be
     *              converted to the return type by a widening conversion.
     * @see       Field#get
     */
    public native byte getByte(Object obj)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Get the value of a field as a char on specified object.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the field value cannot be
     *              converted to the return type by a widening conversion.
     * @see       Field#get
     */
    public native char getChar(Object obj)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Get the value of a field as a short on specified object.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the field value cannot be
     *              converted to the return type by a widening conversion.
     * @see       Field#get
     */
    public native short getShort(Object obj)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Get the value of a field as a int on specified object.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the field value cannot be
     *              converted to the return type by a widening conversion.
     * @see       Field#get
     */
    public native int getInt(Object obj)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Get the value of a field as a long on specified object.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the field value cannot be
     *              converted to the return type by a widening conversion.
     * @see       Field#get
     */
    public native long getLong(Object obj)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Get the value of a field as a float on specified object.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the field value cannot be
     *              converted to the return type by a widening conversion.
     * @see       Field#get
     */
    public native float getFloat(Object obj)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Get the value of a field as a double on specified object.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the field value cannot be
     *              converted to the return type by a widening conversion.
     * @see       Field#get
     */
    public native double getDouble(Object obj)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Sets the field represented by this Field object on the
     * specified object argument to the specified new value. The new
     * value is automatically unwrapped if the underlying field has a
     * primitive type.
     *
     * <p>The operation proceeds as follows:
     *
     * <p>If the underlying field is static, the object argument is
     * ignored; it may be null.
     *
     * <p>Otherwise the underlying field is an instance field.  If the
     * specified object argument is null, the method throws a
     * NullPointerException.  If the specified object argument is not
     * an instance of the class or interface declaring the underlying
     * field, the method throws an IllegalArgumentException.
     *
     * <p>If this Field object enforces Java language access control, and
     * the underlying field is inaccessible, the method throws an
     * IllegalAccessException.
     *
     * <p>If the underlying field is final, the method throws an
     * IllegalAccessException.
     *
     * <p>If the underlying field is of a primitive type, an unwrapping
     * conversion is attempted to convert the new value to a value of
     * a primitive type.  If this attempt fails, the method throws an
     * IllegalArgumentException.
     *
     * <p>If, after possible unwrapping, the new value cannot be
     * converted to the type of the underlying field by an identity or
     * widening conversion, the method throws an
     * IllegalArgumentException.
     *
     * <p>The field is set to the possibly unwrapped and widened new value.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field, or if an unwrapping conversion fails.
     * @exception NullPointerException      if the specified object is null.
     */
    public native void set(Object obj, Object value)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Set the value of a field as a boolean on specified object.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field, or if an unwrapping conversion fails.
     * @see       Field#set
     */
    public native void setBoolean(Object obj, boolean z)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Set the value of a field as a byte on specified object.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field, or if an unwrapping conversion fails.
     * @see       Field#set
     */
    public native void setByte(Object obj, byte b)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Set the value of a field as a char on specified object.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field, or if an unwrapping conversion fails.
     * @see       Field#set
     */
    public native void setChar(Object obj, char c)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Set the value of a field as a short on specified object.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field, or if an unwrapping conversion fails.
     * @see       Field#set
     */
    public native void setShort(Object obj, short s)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Set the value of a field as an int on specified object.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field, or if an unwrapping conversion fails.
     * @see       Field#set
     */
    public native void setInt(Object obj, int i)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Set the value of a field as a long on specified object.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field, or if an unwrapping conversion fails.
     * @see       Field#set
     */
    public native void setLong(Object obj, long l)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Set the value of a field as a float on specified object.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field, or if an unwrapping conversion fails.
     * @see       Field#set
     */
    public native void setFloat(Object obj, float f)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Set the value of a field as a double on specified object.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field, or if an unwrapping conversion fails.
     * @see       Field#set
     */
    public native void setDouble(Object obj, double d)
	throws IllegalArgumentException, IllegalAccessException;

    /*
     * Utility routine to paper over array type names
     */
    static String getTypeName(Class type) {
	if (type.isArray()) {
	    try {
		Class cl = type;
		int dimensions = 0;
		while (cl.isArray()) {
		    dimensions++;
		    cl = cl.getComponentType();
		}
		StringBuffer sb = new StringBuffer();
		sb.append(cl.getName());
		for (int i = 0; i < dimensions; i++) {
		    sb.append("[]");
		}
		return sb.toString();
	    } catch (Throwable e) { /*FALLTHRU*/ }
	}
	return type.getName();
    }

}
