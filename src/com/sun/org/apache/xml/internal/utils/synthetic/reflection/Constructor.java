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
 * $Id: Constructor.java,v 1.7 2004/02/17 04:24:21 minchau Exp $
 */
package com.sun.org.apache.xml.internal.utils.synthetic.reflection;


/**
 * Constructor provides information about, and access to, a
 * single constructor for a class.
 *
 * Constructor permits widening conversions to occur when
 * matching the actual parameters to newInstance() with
 * the underlying constructor's formal parameters, but
 * throws an IllegalArgumentException if a narrowing
 * conversion would occur.
 *
 * @xsl.usage internal
 */
public class Constructor extends EntryPoint implements Member
{

  /**
   * Actual Java class object. When present, all interactions
   * are redirected to it. Allows our Class to function as a
   * wrapper for the Java version (in lieu of subclassing or
   * a shared Interface), and allows "in-place compilation"
   * to replace a generated description with an
   * directly runnable class.
   */
  private com.sun.org.apache.xml.internal.utils.synthetic.Class declaringclass = null;

  /** Field realconstructor          */
  private java.lang.reflect.Constructor realconstructor = null;

  /** Field parametertypes          */
  private com.sun.org.apache.xml.internal.utils.synthetic.Class[] parametertypes;

  /** Field parameternames          */
  private String[] parameternames;

  /** Field exceptiontypes          */
  private com.sun.org.apache.xml.internal.utils.synthetic.Class[] exceptiontypes;

  /** Field modifiers          */
  private int modifiers;

  /**
   * Insert the method's description here.
   * <p>
   * Creation date: (12-27-99 2:31:39 PM)
   * @param realConstructor java.lang.reflect.Constructor
   *
   * @param declaringclass
   */
  public Constructor(com.sun.org.apache.xml.internal.utils.synthetic.Class declaringclass)
  {
    super(declaringclass);
  }

  /**
   * Insert the method's description here.
   * <p>
   * Creation date: (12-27-99 2:31:39 PM)
   * @param realConstructor java.lang.reflect.Constructor
   *
   * @param ctor
   * @param declaringclass
   */
  public Constructor(java.lang.reflect.Constructor ctor,
                     com.sun.org.apache.xml.internal.utils.synthetic.Class declaringclass)
  {
    super(ctor, declaringclass);
  }

  /**
   * Insert the method's description here.
   * <p>
   * Creation date: (12-27-99 2:31:39 PM)
   * @param realConstructor java.lang.reflect.Constructor
   *
   * @param realconstructor
   */
  public Constructor(java.lang.reflect.Constructor realconstructor)
  {
    super(realconstructor);
  }

  /**
   * Returns a hashcode for this Constructor. The
   * hashcode is the same as the hashcode for the
   * underlying constructor's declaring class name.
   *
   * ($objectName$) @return
   */
  public int hashCode()
  {
    return getDeclaringClass().getName().hashCode();
  }

  /**
   * Uses the constructor represented by this
   * Constructor object to create and initialize a new
   * instance of the constructor's declaring class, with
   * the specified initialization parameters. Individual
   * parameters are automatically unwrapped to match
   * primitive formal parameters, and both primitive
   * and reference parameters are subject to widening
   * conversions as necessary. Returns the newly
   * created and initialized object.
   * <p>
   * Creation proceeds with the following steps, in
   * order:
   * <p>
   * If the class that declares the underlying constructor
   * represents an abstract class, the creation throws an
   * InstantiationException.
   * <p>
   * If this Constructor object enforces Java language
   * access control and the underlying constructor is
   * inaccessible, the creation throws an
   * IllegalAccessException.
   * <p>
   * If the number of actual parameters supplied via
   * initargs is different from the number of formal
   * parameters required by the underlying constructor,
   * the creation throws an IllegalArgumentException.
   * <p>
   * A new instance of the constructor's declaring class
   * is created, and its fields are initialized to their
   * default initial values.
   * <p>
   * For each actual parameter in the supplied initargs
   * array:
   * <p>
   * If the corresponding formal parameter has a
   * primitive type, an unwrapping conversion is
   * attempted to convert the object value to a value of
   * the primitive type. If this attempt fails, the
   * creation throws an IllegalArgumentException.
   * <p>
   *
   * If, after possible unwrapping, the parameter value
   * cannot be converted to the corresponding formal
   * parameter type by an identity or widening
   * conversion, the creation throws an
   * IllegalArgumentException.
   * <p>
   * Control transfers to the underlying constructor to
   * initialize the new instance. If the constructor
   * completes abruptly by throwing an exception, the
   * exception is placed in an
   * InvocationTargetException and thrown in turn to
   * the caller of newInstance.
   * <p>
   * If the constructor completes normally, returns the
   * newly created and initialized instance.
   *
   *
   * @param initargs initialization arguments.
   *
   * @return The new instance.
   * @throws  IllegalAccessException
   * if the underlying constructor is inaccessible.
   * @throws  IllegalArgumentException
   * if the number of actual and formal
   * parameters differ, or if an unwrapping
   * conversion fails.
   * @throws  InstantiationException
   * if the class that declares the underlying
   * constructor represents an abstract class.
   * @throws  InvocationTargetException
   * if the underlying constructor throws an
   * exception.
   * @throws java.lang.reflect.InvocationTargetException
   */
  public Object newInstance(Object initargs[])
          throws InstantiationException, IllegalAccessException,
                 IllegalArgumentException,
                 java.lang.reflect.InvocationTargetException
  {

    if (realep != null)
      return ((java.lang.reflect.Constructor) realep).newInstance(initargs);
    else
      throw new InstantiationException(
        "Un-reified com.sun.org.apache.xml.internal.utils.synthetic.Class doesn't yet support invocation");
  }
}
