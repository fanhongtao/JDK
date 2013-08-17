/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang.reflect;

/**
 * A <code>Field</code> provides information about, and dynamic access to, a
 * single field of a class or an interface.  The reflected field may
 * be a class (static) field or an instance field.
 *
 * <p>A <code>Field</code> permits widening conversions to occur during a get or
 * set access operation, but throws an <code>IllegalArgumentException</code> if a
 * narrowing conversion would occur.
 *
 * @see Member
 * @see java.lang.Class
 * @see java.lang.Class#getFields()
 * @see java.lang.Class#getField(String)
 * @see java.lang.Class#getDeclaredFields()
 * @see java.lang.Class#getDeclaredField(String)
 *
 * @author Nakul Saraiya
 */
public final
class Field extends AccessibleObject implements Member {

    private Class		clazz;
    private int			slot;
    private String		name;
    private Class		type;
    private int			modifiers;

    /**
     * Constructor.  Only the Java Virtual Machine may construct a Field.
     */
    private Field() {}

    /**
     * Returns the <code>Class</code> object representing the class or interface
     * that declares the field represented by this <code>Field</code> object.
     */
    public Class getDeclaringClass() {
	return clazz;
    }

    /**
     * Returns the name of the field represented by this <code>Field</code> object.
     */
    public String getName() {
	return name;
    }

    /**
     * Returns the Java language modifiers for the field represented
     * by this <code>Field</code> object, as an integer. The <code>Modifier</code> class should
     * be used to decode the modifiers.
     *
     * @see Modifier
     */
    public int getModifiers() {
	return modifiers;
    }

    /**
     * Returns a <code>Class</code> object that identifies the declared type for
     * the field represented by this <code>Field</code> object.
     */
    public Class getType() {
	return type;
    }

    /**
     * Compares this <code>Field</code> against the specified object.  Returns
     * true if the objects are the same.  Two <code>Field</code> objects are the same if
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
     * Returns a hashcode for this <code>Field</code>.  This is computed as the
     * exclusive-or of the hashcodes for the underlying field's
     * declaring class name and its name.
     */
    public int hashCode() {
	return getDeclaringClass().getName().hashCode() ^ getName().hashCode();
    }

    /**
     * Returns a string describing this <code>Field</code>.  The format is
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
     * Returns the value of the field represented by this <code>Field</code>, on
     * the specified object. The value is automatically wrapped in an
     * object if it has a primitive type.
     *
     * <p>The underlying field's value is obtained as follows:
     *
     * <p>If the underlying field is a static field, the <code>obj</code> argument
     * is ignored; it may be null.
     *
     * <p>Otherwise, the underlying field is an instance field.  If the
     * specified <code>obj</code> argument is null, the method throws a
     * <code>NullPointerException.</code> If the specified object is not an
     * instance of the class or interface declaring the underlying
     * field, the method throws an <code>IllegalArgumentException</code>.
     *
     * <p>If this <code>Field</code> object enforces Java language access control, and
     * the underlying field is inaccessible, the method throws an
     * <code>IllegalAccessException</code>.
     * If the underlying field is static, the class that declared the
     * field is initialized if it has not already been initialized. 
     *
     * <p>Otherwise, the value is retrieved from the underlying instance
     * or static field.  If the field has a primitive type, the value
     * is wrapped in an object before being returned, otherwise it is
     * returned as is.
     *
     * <p>If the field is hidden in the type of <code>obj</code>,
     * the field's value is obtained according to the preceding rules.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field (or a subclass or implementor thereof).
     * @exception NullPointerException      if the specified object is null
     *              and the field is an instance field.
     * @exception ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     */
    public native Object get(Object obj)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Gets the value of a field as a <code>boolean</code> on the specified object.
     * This method is equivalent to
     * <code>((Boolean)get(obj)).booleanValue()</code>,
     * except that an <code>IllegalArgumentException</code> is thrown 
     * if the field value cannot be converted to the type
     * <code>boolean</code> by a widening conversion.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the specified object is not 
     *              an instance of the class or interface declaring the
     *              underlying field (or a subclass or implementor 
     *              thereof), or if the field value cannot be
     *              converted to the type <code>boolean</code> by a 
     *              widening conversion.
     * @exception NullPointerException      if the specified object is null
     *              and the field is an instance field.
     * @exception ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     * @see       Field#get
     */
    public native boolean getBoolean(Object obj)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Gets the value of a field as a <code>byte</code> on the specified object.
     * This method is equivalent to
     * <code>((Number)get(obj)).byteValue()</code>,
     * except that an <code>IllegalArgumentException</code> is thrown 
     * if the field value cannot be converted to the type
     * <code>byte</code> by a widening conversion.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the specified object is not 
     *              an instance of the class or interface declaring the
     *              underlying field (or a subclass or implementor 
     *              thereof), or if the field value cannot be
     *              converted to the type <code>byte</code> by a 
     *              widening conversion.
     * @exception NullPointerException      if the specified object is null
     *              and the field is an instance field.
     * @exception ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     * @see       Field#get
     */
    public native byte getByte(Object obj)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Gets the value of a field as a <code>char</code> on the specified object.
     * This method is equivalent to
     * <code>((Character)get(obj)).charValue()</code>,
     * except that an <code>IllegalArgumentException</code> is thrown 
     * if the field value cannot be converted to the type
     * <code>char</code> by a widening conversion.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the specified object is not 
     *              an instance of the class or interface declaring the
     *              underlying field (or a subclass or implementor 
     *              thereof), or if the field value cannot be
     *              converted to the type <code>char</code> by a 
     *              widening conversion.
     * @exception NullPointerException      if the specified object is null
     *              and the field is an instance field.
     * @exception ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     * @see       Field#get
     */
    public native char getChar(Object obj)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Gets the value of a field as a <code>short</code> on the specified object.
     * This method is equivalent to
     * <code>((Number)get(obj)).shortValue()</code>,
     * except that an <code>IllegalArgumentException</code> is thrown 
     * if the field value cannot be converted to the type
     * <code>short</code> by a widening conversion.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the specified object is not 
     *              an instance of the class or interface declaring the
     *              underlying field (or a subclass or implementor 
     *              thereof), or if the field value cannot be
     *              converted to the type <code>short</code> by a 
     *              widening conversion.
     * @exception NullPointerException      if the specified object is null
     *              and the field is an instance field.
     * @exception ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     * @see       Field#get
     */
    public native short getShort(Object obj)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Gets the value of a field as an <code>int</code> on the specified object.
     * This method is equivalent to
     * <code>((Number)get(obj)).intValue()</code>,
     * except that an <code>IllegalArgumentException</code> is thrown 
     * if the field value cannot be converted to the type
     * <code>int</code> by a widening conversion.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the specified object is not 
     *              an instance of the class or interface declaring the
     *              underlying field (or a subclass or implementor 
     *              thereof), or if the field value cannot be
     *              converted to the type <code>int</code> by a 
     *              widening conversion.
     * @exception NullPointerException      if the specified object is null
     *              and the field is an instance field.
     * @exception ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     * @see       Field#get
     */
    public native int getInt(Object obj)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Gets the value of a field as a <code>long</code> on the specified object.
     * This method is equivalent to
     * <code>((Number)get(obj)).longValue()</code>,
     * except that an <code>IllegalArgumentException</code> is thrown 
     * if the field value cannot be converted to the type
     * <code>long</code> by a widening conversion.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the specified object is not 
     *              an instance of the class or interface declaring the
     *              underlying field (or a subclass or implementor 
     *              thereof), or if the field value cannot be
     *              converted to the type <code>long</code> by a 
     *              widening conversion.
     * @exception NullPointerException      if the specified object is null
     *              and the field is an instance field.
     * @exception ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     * @see       Field#get
     */
    public native long getLong(Object obj)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Gets the value of a field as a <code>float</code> on the specified object.
     * This method is equivalent to
     * <code>((Number)get(obj)).floatValue()</code>,
     * except that an <code>IllegalArgumentException</code> is thrown 
     * if the field value cannot be converted to the type
     * <code>float</code> by a widening conversion.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the specified object is not 
     *              an instance of the class or interface declaring the
     *              underlying field (or a subclass or implementor 
     *              thereof), or if the field value cannot be
     *              converted to the type <code>float</code> by a 
     *              widening conversion.
     * @exception NullPointerException      if the specified object is null
     *              and the field is an instance field.
     * @exception ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     * @see       Field#get
     */
    public native float getFloat(Object obj)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Gets the value of a field as a <code>double</code> on the specified object.
     * This method is equivalent to
     * <code>((Number)get(obj)).doubleValue()</code>,
     * except that an <code>IllegalArgumentException</code> is thrown 
     * if the field value cannot be converted to the type
     * <code>double</code> by a widening conversion.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the specified object is not 
     *              an instance of the class or interface declaring the
     *              underlying field (or a subclass or implementor 
     *              thereof), or if the field value cannot be
     *              converted to the type <code>double</code> by a 
     *              widening conversion.
     * @exception NullPointerException      if the specified object is null
     *              and the field is an instance field.
     * @exception ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     * @see       Field#get
     */
    public native double getDouble(Object obj)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Sets the field represented by this <code>Field</code> object on the
     * specified object argument to the specified new value. The new
     * value is automatically unwrapped if the underlying field has a
     * primitive type.
     *
     * <p>The operation proceeds as follows:
     *
     * <p>If the underlying field is static, the <code>obj</code> argument is
     * ignored; it may be null.
     *
     * <p>Otherwise the underlying field is an instance field.  If the
     * specified object argument is null, the method throws a
     * <code>NullPointerException</code>.  If the specified object argument is not
     * an instance of the class or interface declaring the underlying
     * field, the method throws an <code>IllegalArgumentException</code>.
     *
     * <p>If this <code>Field</code> object enforces Java language access control, and
     * the underlying field is inaccessible, the method throws an
     * <code>IllegalAccessException</code>.
     *
     * <p>If the underlying field is final, the method throws an
     * <code>IllegalAccessException</code>.
     *
     * <p>If the underlying field is of a primitive type, an unwrapping
     * conversion is attempted to convert the new value to a value of
     * a primitive type.  If this attempt fails, the method throws an
     * <code>IllegalArgumentException</code>.
     *
     * <p>If, after possible unwrapping, the new value cannot be
     * converted to the type of the underlying field by an identity or
     * widening conversion, the method throws an
     * <code>IllegalArgumentException</code>.
     *
     * <p>If the underlying field is static, the class that declared the
     * field is initialized if it has not already been initialized.
     *
     * <p>The field is set to the possibly unwrapped and widened new value.
     *
     * <p>If the field is hidden in the type of <code>obj</code>,
     * the field's value is set according to the preceding rules.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field (or a subclass or implementor thereof), 
     *              or if an unwrapping conversion fails.
     * @exception NullPointerException      if the specified object is null
     *              and the field is an instance field.
     * @exception ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     */
    public native void set(Object obj, Object value)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Sets the value of a field as a <code>boolean</code> on the specified object.
     * This method is equivalent to
     * <code>set(obj, zObj)</code>,
     * where <code>zObj</code> is a <code>Boolean</code> object and 
     * <code>zObj.booleanValue() == z</code>.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field (or a subclass or implementor thereof), 
     *              or if an unwrapping conversion fails.
     * @exception NullPointerException      if the specified object is null
     *              and the field is an instance field.
     * @exception ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     * @see       Field#set
     */
    public native void setBoolean(Object obj, boolean z)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Sets the value of a field as a <code>byte</code> on the specified object.
     * This method is equivalent to
     * <code>set(obj, bObj)</code>,
     * where <code>bObj</code> is a <code>Byte</code> object and 
     * <code>bObj.byteValue() == b</code>.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field (or a subclass or implementor thereof), 
     *              or if an unwrapping conversion fails.
     * @exception NullPointerException      if the specified object is null
     *              and the field is an instance field.
     * @exception ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     * @see       Field#set
     */
    public native void setByte(Object obj, byte b)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Sets the value of a field as a <code>char</code> on the specified object.
     * This method is equivalent to
     * <code>set(obj, cObj)</code>,
     * where <code>cObj</code> is a <code>Character</code> object and 
     * <code>cObj.charValue() == c</code>.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field (or a subclass or implementor thereof), 
     *              or if an unwrapping conversion fails.
     * @exception NullPointerException      if the specified object is null
     *              and the field is an instance field.
     * @exception ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     * @see       Field#set
     */
    public native void setChar(Object obj, char c)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Sets the value of a field as a <code>short</code> on the specified object.
     * This method is equivalent to
     * <code>set(obj, sObj)</code>,
     * where <code>sObj</code> is a <code>Short</code> object and 
     * <code>sObj.shortValue() == s</code>.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field (or a subclass or implementor thereof), 
     *              or if an unwrapping conversion fails.
     * @exception NullPointerException      if the specified object is null
     *              and the field is an instance field.
     * @exception ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     * @see       Field#set
     */
    public native void setShort(Object obj, short s)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Sets the value of a field as an <code>int</code> on the specified object.
     * This method is equivalent to
     * <code>set(obj, iObj)</code>,
     * where <code>iObj</code> is a <code>Integer</code> object and 
     * <code>iObj.intValue() == i</code>.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field (or a subclass or implementor thereof), 
     *              or if an unwrapping conversion fails.
     * @exception NullPointerException      if the specified object is null
     *              and the field is an instance field.
     * @exception ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     * @see       Field#set
     */
    public native void setInt(Object obj, int i)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Sets the value of a field as a <code>long</code> on the specified object.
     * This method is equivalent to
     * <code>set(obj, lObj)</code>,
     * where <code>lObj</code> is a <code>Long</code> object and 
     * <code>lObj.longValue() == l</code>.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field (or a subclass or implementor thereof), 
     *              or if an unwrapping conversion fails.
     * @exception NullPointerException      if the specified object is null
     *              and the field is an instance field.
     * @exception ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     * @see       Field#set
     */
    public native void setLong(Object obj, long l)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Sets the value of a field as a <code>float</code> on the specified object.
     * This method is equivalent to
     * <code>set(obj, fObj)</code>,
     * where <code>fObj</code> is a <code>Float</code> object and 
     * <code>fObj.floatValue() == f</code>.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field (or a subclass or implementor thereof), 
     *              or if an unwrapping conversion fails.
     * @exception NullPointerException      if the specified object is null
     *              and the field is an instance field.
     * @exception ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     * @see       Field#set
     */
    public native void setFloat(Object obj, float f)
	throws IllegalArgumentException, IllegalAccessException;

    /**
     * Sets the value of a field as a <code>double</code> on the specified object.
     * This method is equivalent to
     * <code>set(obj, dObj)</code>,
     * where <code>dObj</code> is a <code>Double</code> object and 
     * <code>dObj.doubleValue() == d</code>.
     *
     * @exception IllegalAccessException    if the underlying constructor
     *              is inaccessible.
     * @exception IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field (or a subclass or implementor thereof), 
     *              or if an unwrapping conversion fails.
     * @exception NullPointerException      if the specified object is null
     *              and the field is an instance field.
     * @exception ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
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
