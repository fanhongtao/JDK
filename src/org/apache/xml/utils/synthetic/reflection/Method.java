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
 * A Method provides information about, and access to, a
 * single method on a class or interface. The reflected
 * method may be a class method or an instance method
 * (including an abstract method).
 * <p>
 * A Method permits widening conversions to occur when
 * matching the actual parameters to invokewith the
 * underlying method's formal parameters, but it throws an
 * IllegalArgumentException if a narrowing conversion
 * would occur.
 * <p>
 *  Need to add method body, a la Matt's codebuffer.
 * That may or may not imply retaining the final return value
 * separately and passing in a how-to-use-it mechanism...?
 *
 */
public class Method extends EntryPoint implements Member
{

  /**
   * Insert the method's description here.
   * <p>
   * Creation date: (12-27-99 2:31:39 PM)
   * @param realConstructor java.lang.reflect.Constructor
   *
   * @param name
   * @param declaringclass
   */
  public Method(String name,
                org.apache.xml.utils.synthetic.Class declaringclass)
  {

    super(declaringclass);

    this.name = name;
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
  public Method(java.lang.reflect.Method ctor,
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
   * @param realmethod
   */
  public Method(java.lang.reflect.Method realmethod)
  {
    super(realmethod);
  }

  /**
   * Returns a hashcode for this Method. The hashcode
   * is computed as the exclusive-or of the hashcodes
   * for the underlying method's declaring class name
   * and the method's name.
   *
   */

  /**
   * Returns a hashcode for this Constructor. The
   * hashcode for a Method is the hashcode for the
   * underlying constructor's declaring class name,
   * XORed with the name of this method.
   */
  public int hashCode()
  {
    return getDeclaringClass().getName().hashCode() ^ getName().hashCode();
  }

  /**
   * Invokes the underlying method represented by this
   * Method object, on the specified object with the
   * specified parameters. Individual parameters are
   * automatically unwrapped to match primitive
   * formal parameters, and both primitive and
   * reference parameters are subject to widening
   * conversions as necessary. The value returned by
   * the underlying method is automatically wrapped
   * in an object if it has a primitive type.
   *
   * Method invocation proceeds with the following
   * steps, in order:
   *
   * If the underlying method is static, then the
   * specified object argument is ignored. It may be
   * null.
   *
   * Otherwise, the method is an instance method. If
   * the specified object argument is null, the
   * invocation throws a NullPointerException.
   * Otherwise, if the specified object argument is not
   * an instance of the class or interface declaring the
   * underlying method, the invocation throws an
   * IllegalArgumentException.
   *
   * If this Method object enforces Java language access
   * control and the underlying method is inaccessible,
   * the invocation throws an IllegalAccessException.
   *
   * If the number of actual parameters supplied via
   * args is different from the number of formal
   * parameters required by the underlying method, the
   * invocation throws an IllegalArgumentException.
   *
   * For each actual parameter in the supplied args
   * array:
   *
   * If the corresponding formal parameter has a
   * primitive type, an unwrapping conversion is
   * attempted to convert the object value to a value of
   * a primitive type. If this attempt fails, the
   * invocation throws an IllegalArgumentException.
   *
   * If, after possible unwrapping, the parameter value
   * cannot be converted to the corresponding formal
   * parameter type by an identity or widening
   * conversion, the invocation throws an
   * IllegalArgumentException.
   *
   * If the underlying method is an instance method, it
   * is invoked using dynamic method lookup as
   * documented in The Java Language Specification,
   * section 15.11.4.4; in particular, overriding based
   * on the runtime type of the target object will occur.
   *
   * If the underlying method is static, it is invoked as
   * exactly the method on the declaring class.
   *
   * Control transfers to the underlying method. If the
   * method completes abruptly by throwing an
   * exception, the exception is placed in an
   * InvocationTargetException and thrown in turn to
   * the caller of invoke.
   *
   * If the method completes normally, the value it
   * returns is returned to the caller of invoke; if the
   * value has a primitive type, it is first appropriately
   * wrapped in an object. If the underlying method
   * return type is void, the invocation returns null.
   *
   * Throws: IllegalAccessException
   * if the underlying method is inaccessible.
   * Throws: IllegalArgumentException
   * if the number of actual and formal
   * parameters differ, or if an unwrapping
   * conversion fails.
   * Throws: InvocationTargetException
   * if the underlying method throws an
   * exception.
   * Throws: NullPointerException
   * if the specified object is null.
   *
   * @param obj
   * @param args
   *
   *
   * @throws IllegalAccessException
   * @throws IllegalArgumentException
   * @throws java.lang.reflect.InvocationTargetException
   */
  public Object invoke(Object obj, Object args[])
          throws IllegalAccessException, IllegalArgumentException,
                 java.lang.reflect.InvocationTargetException
  {

    if (realep != null)
      return ((java.lang.reflect.Method) realep).invoke(obj, args);
    else
      throw new IllegalAccessException(
        "Un-reified org.apache.xml.utils.synthetic.Class doesn't yet support invocation");
  }

  /**
   * Method setReturnType 
   *
   *
   * @param returntype
   *
   * @throws SynthesisException
   */
  public void setReturnType(org.apache.xml.utils.synthetic.Class returntype)
          throws SynthesisException
  {

    if (realep != null)
      throw new SynthesisException(SynthesisException.REIFIED);

    this.returntype = returntype;
  }
}
