/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xml.utils.synthetic.reflection;

import java.lang.reflect.InvocationTargetException;

import org.apache.xml.utils.synthetic.SynthesisException;

/**
 * <meta name="usage" content="internal"/>
 * Constructor provides information about, and access to, a
 * single constructor for a class.
 *
 * Constructor permits widening conversions to occur when
 * matching the actual parameters to newInstance() with
 * the underlying constructor's formal parameters, but
 * throws an IllegalArgumentException if a narrowing
 * conversion would occur.
 *
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
  private org.apache.xml.utils.synthetic.Class declaringclass = null;

  /** Field realconstructor          */
  private java.lang.reflect.Constructor realconstructor = null;

  /** Field parametertypes          */
  private org.apache.xml.utils.synthetic.Class[] parametertypes;

  /** Field parameternames          */
  private String[] parameternames;

  /** Field exceptiontypes          */
  private org.apache.xml.utils.synthetic.Class[] exceptiontypes;

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
  public Constructor(org.apache.xml.utils.synthetic.Class declaringclass)
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
                     org.apache.xml.utils.synthetic.Class declaringclass)
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
        "Un-reified org.apache.xml.utils.synthetic.Class doesn't yet support invocation");
  }
}
