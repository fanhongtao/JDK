/*
 * @(#)Field.java	1.31 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang.reflect;

import sun.reflect.FieldAccessor;
import sun.reflect.Reflection;

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
 * @author Kenneth Russell
 * @author Nakul Saraiya
 */
public final
class Field extends AccessibleObject implements Member {

    private Class		clazz;
    private int			slot;
    // This is guaranteed to be interned by the VM in the 1.4
    // reflection implementation
    private String		name;
    private Class		type;
    private int			modifiers;
    private volatile FieldAccessor fieldAccessor;
    // For sharing of FieldAccessors. This branching structure is
    // currently only two levels deep (i.e., one root Field and
    // potentially many Field objects pointing to it.)
    private Field               root;

    // More complicated security check cache needed here than for
    // Class.newInstance() and Constructor.newInstance()
    private volatile Class      securityCheckTargetClassCache;

    /**
     * Package-private constructor used by ReflectAccess to enable
     * instantiation of these objects in Java code from the java.lang
     * package via sun.reflect.LangReflectAccess.
     */
    Field(Class declaringClass,
          String name,
          Class type,
          int modifiers,
          int slot)
    {
        this.clazz = declaringClass;
        this.name = name;
        this.type = type;
        this.modifiers = modifiers;
        this.slot = slot;
    }

    /**
     * Package-private routine (exposed to java.lang.Class via
     * ReflectAccess) which returns a copy of this Field. The copy's
     * "root" field points to this Field.
     */
    Field copy() {
        // This routine enables sharing of FieldAccessor objects
        // among Field objects which refer to the same underlying
        // method in the VM. (All of this contortion is only necessary
        // because of the "accessibility" bit in AccessibleObject,
        // which implicitly requires that new java.lang.reflect
        // objects be fabricated for each reflective call on Class
        // objects.)
        Field res = new Field(clazz, name, type, modifiers, slot);
        res.root = this;
        // Might as well eagerly propagate this if already present
        res.fieldAccessor = fieldAccessor;
        return res;
    }

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
     * Returns a <code>Class</code> object that identifies the
     * declared type for the field represented by this
     * <code>Field</code> object.
     *
     * @return a <code>Class</code> object identifying the declared
     * type of the field represented by this object
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
                && (getName() == other.getName())
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
     * @param obj object from which the represented field's value is
     * to be extracted
     * @return the value of the represented field in object
     * <tt>obj</tt>; primitive values are wrapped in an appropriate
     * object before being returned
     *
     * @exception IllegalAccessException    if the underlying field
     *              is inaccessible.
     * @exception IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field (or a subclass or implementor thereof).
     * @exception NullPointerException      if the specified object is null
     *              and the field is an instance field.
     * @exception ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     */
    public Object get(Object obj)
        throws IllegalArgumentException, IllegalAccessException
    {
        return getFieldAccessor(obj).get(obj);
    }

    /**
     * Gets the value of a static or instance <code>boolean</code> field.
     *
     * @param obj the object to extract the <code>boolean</code> value
     * from
     * @return the value of the <code>boolean</code> field
     *
     * @exception IllegalAccessException    if the underlying field
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
    public boolean getBoolean(Object obj)
	throws IllegalArgumentException, IllegalAccessException
    {
        return getFieldAccessor(obj).getBoolean(obj);
    }

    /**
     * Gets the value of a static or instance <code>byte</code> field.
     *
     * @param obj the object to extract the <code>byte</code> value
     * from
     * @return the value of the <code>byte</code> field
     *
     * @exception IllegalAccessException    if the underlying field
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
    public byte getByte(Object obj)
	throws IllegalArgumentException, IllegalAccessException
    {
        return getFieldAccessor(obj).getByte(obj);
    }

    /**
     * Gets the value of a static or instance field of type
     * <code>char</code> or of another primitive type convertible to
     * type <code>char</code> via a widening conversion.
     *
     * @param obj the object to extract the <code>char</code> value
     * from
     * @return the value of the field converted to type <code>char</code>
     *
     * @exception IllegalAccessException    if the underlying field
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
     * @see Field#get
     */
    public char getChar(Object obj)
	throws IllegalArgumentException, IllegalAccessException
    {
        return getFieldAccessor(obj).getChar(obj);
    }

    /**
     * Gets the value of a static or instance field of type
     * <code>short</code> or of another primitive type convertible to
     * type <code>short</code> via a widening conversion.
     *
     * @param obj the object to extract the <code>short</code> value
     * from
     * @return the value of the field converted to type <code>short</code>
     *
     * @exception IllegalAccessException    if the underlying field
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
    public short getShort(Object obj)
	throws IllegalArgumentException, IllegalAccessException
    {
        return getFieldAccessor(obj).getShort(obj);
    }

    /**
     * Gets the value of a static or instance field of type
     * <code>int</code> or of another primitive type convertible to
     * type <code>int</code> via a widening conversion.
     *
     * @param obj the object to extract the <code>int</code> value
     * from
     * @return the value of the field converted to type <code>int</code>
     *
     * @exception IllegalAccessException    if the underlying field
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
    public int getInt(Object obj)
	throws IllegalArgumentException, IllegalAccessException
    {
        return getFieldAccessor(obj).getInt(obj);
    }

    /**
     * Gets the value of a static or instance field of type
     * <code>long</code> or of another primitive type convertible to
     * type <code>long</code> via a widening conversion.
     *
     * @param obj the object to extract the <code>long</code> value
     * from
     * @return the value of the field converted to type <code>long</code>
     *
     * @exception IllegalAccessException    if the underlying field
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
    public long getLong(Object obj)
	throws IllegalArgumentException, IllegalAccessException
    {
        return getFieldAccessor(obj).getLong(obj);
    }

    /**
     * Gets the value of a static or instance field of type
     * <code>float</code> or of another primitive type convertible to
     * type <code>float</code> via a widening conversion.
     *
     * @param obj the object to extract the <code>float</code> value
     * from
     * @return the value of the field converted to type <code>float</code>
     *
     * @exception IllegalAccessException    if the underlying field
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
     * @see Field#get
     */
    public float getFloat(Object obj)
	throws IllegalArgumentException, IllegalAccessException
    {
        return getFieldAccessor(obj).getFloat(obj);
    }

    /**
     * Gets the value of a static or instance field of type
     * <code>double</code> or of another primitive type convertible to
     * type <code>double</code> via a widening conversion.
     *
     * @param obj the object to extract the <code>double</code> value
     * from
     * @return the value of the field converted to type <code>double</code>
     *
     * @exception IllegalAccessException    if the underlying field
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
    public double getDouble(Object obj)
	throws IllegalArgumentException, IllegalAccessException
    {
        return getFieldAccessor(obj).getDouble(obj);
    }

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
     * @param obj the object whose field should be modified
     * @param value the new value for the field of <code>obj</code>
     * being modified
     * 
     * @exception IllegalAccessException    if the underlying field
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
    public void set(Object obj, Object value)
	throws IllegalArgumentException, IllegalAccessException
    {
        getFieldAccessor(obj).set(obj, value);
    }

    /**
     * Sets the value of a field as a <code>boolean</code> on the specified object.
     * This method is equivalent to
     * <code>set(obj, zObj)</code>,
     * where <code>zObj</code> is a <code>Boolean</code> object and 
     * <code>zObj.booleanValue() == z</code>.
     *
     * @param obj the object whose field should be modified
     * @param z   the new value for the field of <code>obj</code>
     * being modified
     * 
     * @exception IllegalAccessException    if the underlying field
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
    public void setBoolean(Object obj, boolean z)
	throws IllegalArgumentException, IllegalAccessException
    {
        getFieldAccessor(obj).setBoolean(obj, z);
    }

    /**
     * Sets the value of a field as a <code>byte</code> on the specified object.
     * This method is equivalent to
     * <code>set(obj, bObj)</code>,
     * where <code>bObj</code> is a <code>Byte</code> object and 
     * <code>bObj.byteValue() == b</code>.
     *
     * @param obj the object whose field should be modified
     * @param b   the new value for the field of <code>obj</code>
     * being modified
     * 
     * @exception IllegalAccessException    if the underlying field
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
    public void setByte(Object obj, byte b)
	throws IllegalArgumentException, IllegalAccessException
    {
        getFieldAccessor(obj).setByte(obj, b);
    }

    /**
     * Sets the value of a field as a <code>char</code> on the specified object.
     * This method is equivalent to
     * <code>set(obj, cObj)</code>,
     * where <code>cObj</code> is a <code>Character</code> object and 
     * <code>cObj.charValue() == c</code>.
     *
     * @param obj the object whose field should be modified
     * @param c   the new value for the field of <code>obj</code>
     * being modified
     * 
     * @exception IllegalAccessException    if the underlying field
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
    public void setChar(Object obj, char c)
	throws IllegalArgumentException, IllegalAccessException
    {
        getFieldAccessor(obj).setChar(obj, c);
    }

    /**
     * Sets the value of a field as a <code>short</code> on the specified object.
     * This method is equivalent to
     * <code>set(obj, sObj)</code>,
     * where <code>sObj</code> is a <code>Short</code> object and 
     * <code>sObj.shortValue() == s</code>.
     *
     * @param obj the object whose field should be modified
     * @param s   the new value for the field of <code>obj</code>
     * being modified
     * 
     * @exception IllegalAccessException    if the underlying field
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
    public void setShort(Object obj, short s)
	throws IllegalArgumentException, IllegalAccessException
    {
        getFieldAccessor(obj).setShort(obj, s);
    }

    /**
     * Sets the value of a field as an <code>int</code> on the specified object.
     * This method is equivalent to
     * <code>set(obj, iObj)</code>,
     * where <code>iObj</code> is a <code>Integer</code> object and 
     * <code>iObj.intValue() == i</code>.
     *
     * @param obj the object whose field should be modified
     * @param i   the new value for the field of <code>obj</code>
     * being modified
     * 
     * @exception IllegalAccessException    if the underlying field
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
    public void setInt(Object obj, int i)
	throws IllegalArgumentException, IllegalAccessException
    {
        getFieldAccessor(obj).setInt(obj, i);
    }

    /**
     * Sets the value of a field as a <code>long</code> on the specified object.
     * This method is equivalent to
     * <code>set(obj, lObj)</code>,
     * where <code>lObj</code> is a <code>Long</code> object and 
     * <code>lObj.longValue() == l</code>.
     *
     * @param obj the object whose field should be modified
     * @param l   the new value for the field of <code>obj</code>
     * being modified
     * 
     * @exception IllegalAccessException    if the underlying field
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
    public void setLong(Object obj, long l)
	throws IllegalArgumentException, IllegalAccessException
    {
        getFieldAccessor(obj).setLong(obj, l);
    }

    /**
     * Sets the value of a field as a <code>float</code> on the specified object.
     * This method is equivalent to
     * <code>set(obj, fObj)</code>,
     * where <code>fObj</code> is a <code>Float</code> object and 
     * <code>fObj.floatValue() == f</code>.
     *
     * @param obj the object whose field should be modified
     * @param f   the new value for the field of <code>obj</code>
     * being modified
     * 
     * @exception IllegalAccessException    if the underlying field
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
    public void setFloat(Object obj, float f)
	throws IllegalArgumentException, IllegalAccessException
    {
        getFieldAccessor(obj).setFloat(obj, f);
    }

    /**
     * Sets the value of a field as a <code>double</code> on the specified object.
     * This method is equivalent to
     * <code>set(obj, dObj)</code>,
     * where <code>dObj</code> is a <code>Double</code> object and 
     * <code>dObj.doubleValue() == d</code>.
     *
     * @param obj the object whose field should be modified
     * @param d   the new value for the field of <code>obj</code>
     * being modified
     * 
     * @exception IllegalAccessException    if the underlying field
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
    public void setDouble(Object obj, double d)
	throws IllegalArgumentException, IllegalAccessException
    {
        getFieldAccessor(obj).setDouble(obj, d);
    }

    // Convenience routine which performs security checks
    private FieldAccessor getFieldAccessor(Object obj)
        throws IllegalAccessException
    {
        doSecurityCheck(obj);
        if (fieldAccessor == null) {
            acquireFieldAccessor();
        }
        return fieldAccessor;
    }

    // NOTE that there is no synchronization used here. It is correct
    // (though not efficient) to generate more than one FieldAccessor
    // for a given Field. However, avoiding synchronization will
    // probably make the implementation more scalable.
    private void acquireFieldAccessor() {
        // First check to see if one has been created yet, and take it
        // if so
        FieldAccessor tmp = null;
        if (root != null) tmp = root.getFieldAccessor();
        if (tmp != null) {
            fieldAccessor = tmp;
            return;
        }
        // Otherwise fabricate one and propagate it up to the root
        tmp = reflectionFactory.newFieldAccessor(this);
        setFieldAccessor(tmp);
    }

    // Returns FieldAccessor for this Field object, not looking up
    // the chain to the root
    private FieldAccessor getFieldAccessor() {
        return fieldAccessor;
    }

    // Sets the FieldAccessor for this Field object and
    // (recursively) its root
    private void setFieldAccessor(FieldAccessor accessor) {
        fieldAccessor = accessor;
        // Propagate up
        if (root != null) {
            root.setFieldAccessor(accessor);
        }
    }

    // NOTE: be very careful if you change the stack depth of this
    // routine. The depth of the "getCallerClass" call is hardwired so
    // that the compiler can have an easier time if this gets inlined.
    private void doSecurityCheck(Object obj) throws IllegalAccessException {
        if (!override) {
            if (!Reflection.quickCheckMemberAccess(clazz, modifiers)) {
                Class caller = Reflection.getCallerClass(4);
                Class targetClass = ((obj == null || !Modifier.isProtected(modifiers))
                                     ? clazz
                                     : obj.getClass());
                if (securityCheckCache != caller ||
                    targetClass != securityCheckTargetClassCache) {
                    Reflection.ensureMemberAccess(caller, clazz, obj, modifiers);
                    securityCheckCache = caller;
                    securityCheckTargetClassCache = targetClass;
                }
            }
        }
    }

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
