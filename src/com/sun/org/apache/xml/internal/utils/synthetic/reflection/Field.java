/*
 * Copyright 1999-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: Field.java,v 1.6 2004/02/17 04:24:21 minchau Exp $
 */
package com.sun.org.apache.xml.internal.utils.synthetic.reflection;

import com.sun.org.apache.xml.internal.utils.synthetic.Class;
import com.sun.org.apache.xml.internal.utils.synthetic.SynthesisException;

/**
 * A Field provides information about, and dynamic access
 * to, a single field of a class or an interface. The reflected
 * field may be a class (static) field or an instance field.
 * <p>
 * A Field permits widening conversions to occur during a
 * get or set access operation, but throws an
 * IllegalArgumentException if a narrowing conversion
 * would occur.
 *
 * @xsl.usage internal
 */
public class Field extends Object implements Member
{

  /** Field name, initializer          */
  public String name, initializer = null;

  /** Field modifiers          */
  int modifiers;

  /** Field realfield          */
  java.lang.reflect.Field realfield = null;

  /** Field declaringClass, type          */
  Class declaringClass, type;

  /**
   * Proxy constructor 
   *
   * @param realfield
   * @param declaringClass
   */
  public Field(java.lang.reflect.Field realfield,
               com.sun.org.apache.xml.internal.utils.synthetic.Class declaringClass)
  {

    this(realfield.getName(), declaringClass);

    this.realfield = realfield;
    this.type =
      com.sun.org.apache.xml.internal.utils.synthetic.Class.forClass(realfield.getType());
  }

  /**
   * Synthesis constructor 
   *
   * @param name
   * @param declaringClass
   */
  public Field(String name,
               com.sun.org.apache.xml.internal.utils.synthetic.Class declaringClass)
  {
    this.name = name;
    this.declaringClass = declaringClass;
  }

  /**
   * Compares this Field against the specified object.
   * Returns true if the objects are the same. Two
   * Fields are the same if they were declared by the
   * same class and have the same name and type.
   *
   * @param obj
   *
   */
  public boolean equals(Object obj)
  {

    if (realfield != null)
      return realfield.equals(obj);
    else if (obj instanceof Field)
    {
      Field objf = (Field) obj;

      return (declaringClass.equals(objf.declaringClass)
              && name.equals(objf.name) && type.equals(objf.type));
    }
    else
      return false;
  }

  /**
   * Returns the value of the field represented by this
   * Field, on the specified object. The value is
   * automatically wrapped in an object if it has a
   * primitive type.
   * <p>
   * The underlying field's value is obtained as follows:
   * <p>
   * If the underlying field is a static field, the object
   * argument is ignored; it may be null.
   * <p>
   * Otherwise, the underlying field is an instance
   * field. If the specified object argument is null, the
   * method throws a NullPointerException. If the
   * specified object is not an instance of the class or
   * interface declaring the underlying field, the
   * method throws an IllegalArgumentException.
   * <p>
   * If this Field object enforces Java language access
   * control, and the underlying field is inaccessible,
   * the method throws an IllegalAccessException.
   * <p>
   * Otherwise, the value is retrieved from the
   * underlying instance or static field. If the field has a
   * primitive type, the value is wrapped in an object
   * before being returned, otherwise it is returned as
   * is.
   *
   *
   * @param obj
   *
   * @throws IllegalAccessException
   * if the underlying constructor is inaccessible.
   * @throws IllegalArgumentException
   * if the specified object is not an instance of
   * the class or interface declaring the
   * underlying field.
   * @throws NullPointerException
   * if the specified object is null.
   */
  public Object get(Object obj)
          throws IllegalArgumentException, IllegalAccessException
  {

    if (realfield != null)
      return realfield.get(obj);

    throw new java.lang.IllegalStateException();
  }

  /**
   * Get the value of a field as a boolean on specified
   * object.
   *
   *
   * @param obj
   *
   * @throws IllegalAccessException
   * if the underlying constructor is inaccessible.
   * @throws IllegalArgumentException
   * if the field value cannot be converted to the
   * return type by a widening conversion.
   */
  public boolean getBoolean(Object obj)
          throws IllegalArgumentException, IllegalAccessException
  {

    if (realfield != null)
      return realfield.getBoolean(obj);

    throw new java.lang.IllegalStateException();
  }

  /**
   * Get the value of a field as a byte on specified
   * object.
   *
   *
   * @param obj
   *
   * @throws IllegalAccessException
   * if the underlying constructor is inaccessible.
   * @throws IllegalArgumentException
   * if the field value cannot be converted to the
   * return type by a widening conversion.
   */
  public byte getByte(Object obj)
          throws IllegalArgumentException, IllegalAccessException
  {

    if (realfield != null)
      return realfield.getByte(obj);

    throw new java.lang.IllegalStateException();
  }

  /**
   * Get the value of a field as a char on specified
   * object.
   *
   *
   * @param obj
   *
   * @throws IllegalAccessException
   * if the underlying constructor is inaccessible.
   * @throws IllegalArgumentException
   * if the field value cannot be converted to the
   * return type by a widening conversion.
   */
  public char getChar(Object obj)
          throws IllegalArgumentException, IllegalAccessException
  {

    if (realfield != null)
      return realfield.getChar(obj);

    throw new java.lang.IllegalStateException();
  }

  /**
   * Returns the Class object representing the class or
   * interface that declares the field represented by this
   * Field object.
   *
   */
  public com.sun.org.apache.xml.internal.utils.synthetic.Class getDeclaringClass()
  {

    if (realfield != null)
      return com.sun.org.apache.xml.internal.utils.synthetic.Class.forClass(
        realfield.getDeclaringClass());

    throw new java.lang.IllegalStateException();
  }

  /**
   * Get the value of a field as a double on specified
   * object.
   *
   *
   * @param obj
   *
   * @throws IllegalAccessException
   * if the underlying constructor is inaccessible.
   * @throws IllegalArgumentException
   * if the field value cannot be converted to the
   * return type by a widening conversion.
   */
  public double getDouble(Object obj)
          throws IllegalArgumentException, IllegalAccessException
  {

    if (realfield != null)
      return realfield.getDouble(obj);

    throw new java.lang.IllegalStateException();
  }

  /**
   * Get the value of a field as a float on specified
   * object.
   *
   *
   * @param obj
   *
   * @throws IllegalAccessException
   * if the underlying constructor is inaccessible.
   * @throws IllegalArgumentException
   * if the field value cannot be converted to the
   * return type by a widening conversion.
   */
  public float getFloat(Object obj)
          throws IllegalArgumentException, IllegalAccessException
  {

    if (realfield != null)
      return realfield.getFloat(obj);

    throw new java.lang.IllegalStateException();
  }

  /**
   * Get the value of a field as a int on specified object.
   *
   *
   * @param obj
   *
   * @throws IllegalAccessException
   * if the underlying constructor is inaccessible.
   * @throws IllegalArgumentException
   * if the field value cannot be converted to the
   * return type by a widening conversion.
   */
  public int getInt(Object obj)
          throws IllegalArgumentException, IllegalAccessException
  {

    if (realfield != null)
      return realfield.getInt(obj);

    throw new java.lang.IllegalStateException();
  }

  /**
   * Get the value of a field as a long on specified
   * object.
   *
   *
   * @param obj
   *
   * @throws IllegalAccessException
   * if the underlying constructor is inaccessible.
   * @throws IllegalArgumentException
   * if the field value cannot be converted to the
   * return type by a widening conversion.
   */
  public long getLong(Object obj)
          throws IllegalArgumentException, IllegalAccessException
  {

    if (realfield != null)
      return realfield.getLong(obj);

    throw new java.lang.IllegalStateException();
  }

  /**
   * Returns the Java language modifiers for the field
   * represented by this Field object, as an integer. The
   * Modifier class should be used to decode the
   * modifiers.
   *
   */
  public int getModifiers()
  {

    if (realfield != null)
      modifiers = realfield.getModifiers();

    return modifiers;
  }

  /**
   * Method getInitializer 
   *
   *
   * (getInitializer) @return
   */
  public String getInitializer()
  {
    return initializer;
  }

  /**
   * Method setInitializer 
   *
   *
   * @param i
   *
   * @throws SynthesisException
   */
  public void setInitializer(String i) throws SynthesisException
  {

    if (realfield != null)
      throw new SynthesisException(SynthesisException.REIFIED);

    initializer = i;
  }

  /**
   * Insert the method's description here.
   * Creation date: (12-25-99 2:02:26 PM)
   * @return java.lang.String
   */
  public java.lang.String getName()
  {
    return name;
  }

  /**
   * Get the value of a field as a short on specified
   * object.
   *
   *
   * @param obj
   *
   * @throws IllegalAccessException
   * if the underlying constructor is inaccessible.
   * @throws IllegalArgumentException
   * if the field value cannot be converted to the
   * return type by a widening conversion.
   */
  public short getShort(Object obj)
          throws IllegalArgumentException, IllegalAccessException
  {

    if (realfield != null)
      return realfield.getShort(obj);

    throw new java.lang.IllegalStateException();
  }

  /**
   * Returns a Class object that identifies the declared
   * type for the field represented by this Field object.
   *
   */
  public Class getType()
  {

    if (realfield != null)
      type = Class.forClass(realfield.getType());

    return type;
  }

  /**
   * Method setType 
   *
   *
   * @param type
   *
   * @throws SynthesisException
   */
  public void setType(com.sun.org.apache.xml.internal.utils.synthetic.Class type)
          throws SynthesisException
  {

    if (realfield != null)
      throw new SynthesisException(SynthesisException.REIFIED);

    this.type = type;
  }

  /**
   * Returns a hashcode for this Field. This is
   * computed as the exclusive-or of the hashcodes for
   * the underlying field's declaring class name and its
   * name.
   *
   */
  public int hashCode()
  {

    if (realfield != null)
      return realfield.hashCode();
    else
      return declaringClass.getName().hashCode() ^ name.hashCode();
  }

  /**
   * Sets the field represented by this Field object on
   * the specified object argument to the specified new
   * value. The new value is automatically unwrapped
   * if the underlying field has a primitive type.
   *
   * The operation proceeds as follows:
   *
   * If the underlying field is static, the object
   * argument is ignored; it may be null.
   *
   * Otherwise the underlying field is an instance field.
   * If the specified object argument is null, the
   * method throws a NullPointerException. If the
   * specified object argument is not an instance of the
   * class or interface declaring the underlying field,
   * the method throws an IllegalArgumentException.
   *
   * If this Field object enforces Java language access
   * control, and the underlying field is inaccessible,
   * the method throws an IllegalAccessException.
   *
   * If the underlying field is final, the method throws
   * an IllegalAccessException.
   *
   * If the underlying field is of a primitive type, an
   * unwrapping conversion is attempted to convert the
   * new value to a value of a primitive type. If this
   * attempt fails, the method throws an
   * IllegalArgumentException.
   *
   * If, after possible unwrapping, the new value
   * cannot be converted to the type of the underlying
   * field by an identity or widening conversion, the
   * method throws an IllegalArgumentException.
   *
   * The field is set to the possibly unwrapped and
   * widened new value.
   *
   *
   * @param obj
   * @param value
   * @throws IllegalAccessException
   * if the underlying constructor is inaccessible.
   * @throws IllegalArgumentException
   * if the specified object is not an instance of
   * the class or interface declaring the
   * underlying field, or if an unwrapping
   * conversion fails.
   * @throws NullPointerException
   * if the specified object is null.
   */
  public void set(Object obj, Object value)
          throws IllegalArgumentException, IllegalAccessException
  {

    if (realfield != null)
      realfield.set(obj, value);

    throw new java.lang.IllegalStateException();
  }

  /**
   * Set the value of a field as a boolean on specified
   * object.
   *
   *
   * @param obj
   * @param z
   * @throws IllegalAccessException
   * if the underlying constructor is inaccessible.
   * @throws IllegalArgumentException
   * if the specified object is not an instance of
   * the class or interface declaring the
   * underlying field, or if an unwrapping
   * conversion fails.
   */
  public void setBoolean(Object obj, boolean z)
          throws IllegalArgumentException, IllegalAccessException
  {

    if (realfield != null)
      realfield.setBoolean(obj, z);

    throw new java.lang.IllegalStateException();
  }

  /**
   * Set the value of a field as a byte on specified
   * object.
   *
   *
   * @param obj
   * @param b
   * @throws IllegalAccessException
   * if the underlying constructor is inaccessible.
   * @throws IllegalArgumentException
   * if the specified object is not an instance of
   * the class or interface declaring the
   * underlying field, or if an unwrapping
   * conversion fails.
   */
  public void setByte(Object obj, byte b)
          throws IllegalArgumentException, IllegalAccessException
  {

    if (realfield != null)
      realfield.setByte(obj, b);

    throw new java.lang.IllegalStateException();
  }

  /**
   * Set the value of a field as a char on specified
   * object.
   *
   *
   * @param obj
   * @param c
   * @throws IllegalAccessException
   * if the underlying constructor is inaccessible.
   * @throws IllegalArgumentException
   * if the specified object is not an instance of
   * the class or interface declaring the
   * underlying field, or if an unwrapping
   * conversion fails.
   */
  public void setChar(Object obj, char c)
          throws IllegalArgumentException, IllegalAccessException
  {

    if (realfield != null)
      realfield.setChar(obj, c);

    throw new java.lang.IllegalStateException();
  }

  /**
   * Returns the Class object representing the class that
   * declares the constructor represented by this
   * Constructor object.
   *
   * @param declaringClass
   */
  public void setDeclaringClass(
          com.sun.org.apache.xml.internal.utils.synthetic.Class declaringClass)
  {
    this.declaringClass = declaringClass;
  }

  /**
   * Set the value of a field as a double on specified
   * object.
   *
   *
   * @param obj
   * @param d
   * @throws IllegalAccessException
   * if the underlying constructor is inaccessible.
   * @throws IllegalArgumentException
   * if the specified object is not an instance of
   * the class or interface declaring the
   * underlying field, or if an unwrapping
   * conversion fails.
   */
  public void setDouble(Object obj, double d)
          throws IllegalArgumentException, IllegalAccessException
  {

    if (realfield != null)
      realfield.setDouble(obj, d);

    throw new java.lang.IllegalStateException();
  }

  /**
   * Set the value of a field as a float on specified
   * object.
   *
   *
   * @param obj
   * @param f
   * @throws IllegalAccessException
   * if the underlying constructor is inaccessible.
   * @throws IllegalArgumentException
   * if the specified object is not an instance of
   * the class or interface declaring the
   * underlying field, or if an unwrapping
   * conversion fails.
   */
  public void setFloat(Object obj, float f)
          throws IllegalArgumentException, IllegalAccessException
  {

    if (realfield != null)
      realfield.setFloat(obj, f);

    throw new java.lang.IllegalStateException();
  }

  /**
   * Set the value of a field as an int on specified
   * object.
   *
   *
   * @param obj
   * @param i
   * @throws IllegalAccessException
   * if the underlying constructor is inaccessible.
   * @throws IllegalArgumentException
   * if the specified object is not an instance of
   * the class or interface declaring the
   * underlying field, or if an unwrapping
   * conversion fails.
   */
  public void setInt(Object obj, int i)
          throws IllegalArgumentException, IllegalAccessException
  {

    if (realfield != null)
      realfield.setInt(obj, i);

    throw new java.lang.IllegalStateException();
  }

  /**
   * Set the value of a field as a long on specified
   * object.
   *
   *
   * @param obj
   * @param l
   * @throws IllegalAccessException
   * if the underlying constructor is inaccessible.
   * @throws IllegalArgumentException
   * if the specified object is not an instance of
   * the class or interface declaring the
   * underlying field, or if an unwrapping
   * conversion fails.
   */
  public void setLong(Object obj, long l)
          throws IllegalArgumentException, IllegalAccessException
  {

    if (realfield != null)
      realfield.setLong(obj, l);

    throw new java.lang.IllegalStateException();
  }

  /**
   * Insert the method's description here.
   * Creation date: (12-25-99 1:28:28 PM)
   * @return int
   * @param modifiers int
   *
   * @throws SynthesisException
   */
  public void setModifiers(int modifiers) throws SynthesisException
  {

    if (realfield != null)
      throw new SynthesisException(SynthesisException.REIFIED);

    this.modifiers = modifiers;
  }

  /**
   * Set the value of a field as a short on specified
   * object.
   *
   *
   * @param obj
   * @param s
   * @throws IllegalAccessException
   * if the underlying constructor is inaccessible.
   * @throws IllegalArgumentException
   * if the specified object is not an instance of
   * the class or interface declaring the
   * underlying field, or if an unwrapping
   * conversion fails.
   */
  public void setShort(Object obj, short s)
          throws IllegalArgumentException, IllegalAccessException
  {

    if (realfield != null)
      realfield.setShort(obj, s);

    throw new java.lang.IllegalStateException();
  }

  /**
   * Return a string describing this Field. The format is
   * the access modifiers for the field, if any, followed
   * by the field type, followed by a space, followed by
   * the fully-qualified name of the class declaring the
   * field, followed by a period, followed by the name
   * of the field. For example:
   * <code>
   * public static final int java.lang.Thread.MIN_PRIORITY
   * private int java.io.FileDescriptor.fd
   * </code>
   *
   * The modifiers are placed in canonical order as
   * specified by "The Java Language Specification".
   * This is public, protected or private first,
   * and then other modifiers in the following order:
   * static, final, transient, volatile.
   *
   */
  public String toString()
  {

    if (realfield != null)
      return realfield.toString();

    throw new java.lang.IllegalStateException();
  }

  /**
   * Output the Field as Java sourcecode
   *
   */
  public String toSource()
  {

    StringBuffer sb = new StringBuffer(
      java.lang.reflect.Modifier.toString(getModifiers())).append(' ').append(
      getType().getJavaName()).append(' ').append(getName());
    String i = getInitializer();

    if (i != null && i.length() > 0)
      sb.append('=').append(i);

    sb.append(';');

    return sb.toString();
  }
}
