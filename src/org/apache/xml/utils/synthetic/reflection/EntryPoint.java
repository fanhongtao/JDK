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

/*
 * OPEN ISSUES:
 *   Reflection doesn't tell us about deprecation; if we want
 *   that info, MFC advises mousing our way into the class (ugh).
 *   Should we at least model that for synthetics?
 */

/**
 * <meta name="usage" content="internal"/>
 * API/behaviors shared between Constructors and Methods.
 * They're mostly similar, except for what they proxy and
 * a few specific calls (name, invoke/getInstance).
 */
abstract public class EntryPoint implements Member
{

  /** Field realep          */
  protected Object realep;

  /** Field declaringclass          */
  private org.apache.xml.utils.synthetic.Class declaringclass = null;

  /** Field returntype          */
  protected org.apache.xml.utils.synthetic.Class returntype = null;

  /** Field parameternames          */
  private String[] parameternames = new String[0];

  /** Field parametertypes          */
  private org.apache.xml.utils.synthetic.Class[] parametertypes =
    new org.apache.xml.utils.synthetic.Class[0];

  /** Field exceptiontypes          */
  private org.apache.xml.utils.synthetic.Class[] exceptiontypes =
    new org.apache.xml.utils.synthetic.Class[0];
  ;

  /** Field modifiers          */
  private int modifiers;

  /** Field name          */
  protected String name = null;  // for Methods

  // For synthesis:

  /** Field body          */
  private StringBuffer body = null;

  /** Field language          */
  private String language = null;

  // For reifying:

  /** Field realE, realP          */
  Class[] realE, realP;

  /**
   * Insert the method's description here.
   * <p>
   * Creation date: (12-27-99 2:31:39 PM)
   * @param realConstructor java.lang.reflect.Constructor
   *
   * @param declaringclass
   */
  public EntryPoint(org.apache.xml.utils.synthetic.Class declaringclass)
  {
    this.declaringclass = declaringclass;
  }

  /**
   * Nonpublic constructor. Wrap this to appropriate "real" type 
   *
   * @param ep
   * @param declaringclass
   *
   * @throws IllegalArgumentException
   */
  protected EntryPoint(
          Object ep, org.apache.xml.utils.synthetic.Class declaringclass)
            throws IllegalArgumentException
  {

    realep = ep;
    this.declaringclass = declaringclass;

    if (ep instanceof java.lang.reflect.Method)
    {
      java.lang.reflect.Method m = (java.lang.reflect.Method) ep;

      if (declaringclass == null)
      {
        declaringclass = org.apache.xml.utils.synthetic.Class.forClass(
          m.getDeclaringClass());
      }

      name = m.getName();
      modifiers = m.getModifiers();
      returntype =
        org.apache.xml.utils.synthetic.Class.forClass(m.getReturnType());
      realP = m.getParameterTypes();
      realE = m.getExceptionTypes();
    }
    else if (ep instanceof java.lang.reflect.Constructor)
    {
      java.lang.reflect.Constructor c = (java.lang.reflect.Constructor) ep;

      if (declaringclass == null)
      {
        declaringclass = org.apache.xml.utils.synthetic.Class.forClass(
          c.getDeclaringClass());
      }

      name = declaringclass.getShortName();
      modifiers = c.getModifiers();
      returntype = declaringclass;
      realP = c.getParameterTypes();
      realE = c.getExceptionTypes();
    }
    else
      throw new IllegalArgumentException();
  }

  /**
   * Nonpublic constructor. Wrap this to appropriate "real" type 
   *
   * @param ep
   *
   * @throws IllegalArgumentException
   */
  protected EntryPoint(Object ep) throws IllegalArgumentException
  {
    this(ep, null);
  }

  /**
   * Compares this against the specified
   * object. Returns true if the objects are the same.
   * Two EntryPoints are the same if they were
   * declared by the same class, have the same name
   * (or are both ctors) and have the same
   * formal parameter types.
   *
   * @param obj
   *
   */
  public boolean equals(Object obj)
  {

    EntryPoint otherep = null;

    if (obj instanceof EntryPoint)
      otherep = (EntryPoint) obj;
    else if (obj instanceof java.lang.reflect.Constructor
             || obj instanceof java.lang.reflect.Method)
      otherep = (EntryPoint) obj;

    return (otherep != null && ((this instanceof Constructor && otherep instanceof Constructor) || (this instanceof Method && otherep instanceof Method && this.getName().equals(
      otherep.getName()))) && otherep.getDeclaringClass().equals(
        declaringclass) && otherep.getParameterTypes().equals(
        parametertypes));
  }

  /**
   * Returns the Class object representing the class that
   * declares the constructor represented by this
   * Constructor object.
   *
   */
  public org.apache.xml.utils.synthetic.Class getDeclaringClass()
  {
    return declaringclass;
  }

  /**
   * Returns the Class object representing the class that
   * will be returned by this EntryPoint. Needed by the Method
   * API, but made meaningful for Constructors as well.
   *
   */
  public org.apache.xml.utils.synthetic.Class getReturnType()
  {
    return returntype;
  }

  /**
   * Returns an array of Class objects that represent the
   * types of the checked exceptions thrown by the
   * underlying constructor represented by this
   * Constructor object. Returns an array of length 0 if
   * the constructor throws no checked exceptions.
   *
   */
  public org.apache.xml.utils.synthetic.Class[] getExceptionTypes()
  {

    if (realep != null && exceptiontypes == null)
    {
      exceptiontypes =
        new org.apache.xml.utils.synthetic.Class[realE.length];

      for (int i = 0; i < realE.length; ++i)
      {
        exceptiontypes[i] =
          org.apache.xml.utils.synthetic.Class.forClass(realE[i]);
      }

      realE = null;
    }

    return exceptiontypes;
  }

  /**
   * Method addExceptionType 
   *
   *
   * @param exception
   *
   * @throws SynthesisException
   */
  public void addExceptionType(
          org.apache.xml.utils.synthetic.Class exception)
            throws SynthesisException
  {

    if (realep != null)
      throw new SynthesisException(SynthesisException.REIFIED);

    org.apache.xml.utils.synthetic.Class[] e =
      new org.apache.xml.utils.synthetic.Class[exceptiontypes.length + 1];

    System.arraycopy(exceptiontypes, 0, e, 0, exceptiontypes.length);

    e[exceptiontypes.length] = exception;
    exceptiontypes = e;
  }

  /**
   * Returns the Java language modifiers for the
   * constructor represented by this Constructor object,
   * as an integer. The Modifier class should be used to
   * decode the modifiers.
   *
   */
  public int getModifiers()
  {
    return modifiers;
  }

  /**
   * Member method. C'tor's name is always that of the defining class.
   * Methods have a "real" name.
   * Creation date: (12-25-99 1:32:06 PM)
   * @return java.lang.String
   */
  public java.lang.String getName()
  {

    if (this instanceof Constructor)
      return declaringclass.getShortName();

    return name;
  }

  /**
   * Member method. C'tor's name is always that of the defining class.
   * Methods have a "real" name.
   * Creation date: (12-25-99 1:32:06 PM)
   *
   * @param name
   * @return java.lang.String
   *
   * @throws SynthesisException
   */
  public void setName(String name) throws SynthesisException
  {

    if (realep != null)
      throw new SynthesisException(SynthesisException.REIFIED);

    this.name = name;
  }

  /**
   * Returns an array of Class objects that represent the
   * formal parameter types, in declaration order, of the
   * constructor represented by this Constructor object.
   * Returns an array of length 0 if the underlying
   * constructor takes no parameters.
   *
   */
  public org.apache.xml.utils.synthetic.Class[] getParameterTypes()
  {

    if (realep != null && parametertypes == null)
    {
      parametertypes =
        new org.apache.xml.utils.synthetic.Class[realP.length];

      for (int i = 0; i < realP.length; ++i)
      {
        parametertypes[i] =
          org.apache.xml.utils.synthetic.Class.forClass(realP[i]);
      }

      realP = null;
    }

    return parametertypes;
  }

  /**
   * Method getParameterNames 
   *
   *
   * (getParameterNames) @return
   */
  public String[] getParameterNames()
  {
    return parameternames;
  }

  /**
   * Method addParameter 
   *
   *
   * @param type
   * @param name
   *
   * @throws SynthesisException
   */
  public void addParameter(
          org.apache.xml.utils.synthetic.Class type, String name)
            throws SynthesisException
  {

    if (realep != null)
      throw new SynthesisException(SynthesisException.REIFIED);

    org.apache.xml.utils.synthetic.Class[] types =
      new org.apache.xml.utils.synthetic.Class[parametertypes.length + 1];

    System.arraycopy(parametertypes, 0, types, 0, parametertypes.length);

    types[parametertypes.length] = type;
    parametertypes = types;

    String[] names = new String[parameternames.length + 1];

    System.arraycopy(parameternames, 0, names, 0, parameternames.length);

    names[parameternames.length] = name;
    parameternames = names;
  }

  /**
   * Returns a hashcode for this Constructor. The
   * hashcode is the same as the hashcode for the
   * underlying constructor's declaring class name,
   * xor'ed (for Methods) with the method name.
   * (Implemented in the subclasses rather than here.)
   *
   */
  abstract public int hashCode();

  /**
   * Assert the Class object representing the class that
   * declares the constructor represented by this
   * Constructor object.
   *
   * @param declaringClass
   *
   * @throws SynthesisException
   */
  public void setDeclaringClass(
          org.apache.xml.utils.synthetic.Class declaringClass)
            throws SynthesisException
  {

    if (realep != null)
      throw new SynthesisException(SynthesisException.REIFIED);

    this.declaringclass = declaringClass;
  }

  /**
   * Should only be accepted before a "real" entrypoint is bound.
   * Creation date: (12-25-99 1:28:28 PM)
   * @return int
   * @param modifiers int
   *
   * @throws SynthesisException
   */
  public void setModifiers(int modifiers) throws SynthesisException
  {

    if (realep != null)
      throw new SynthesisException(SynthesisException.REIFIED);

    this.modifiers = modifiers;
  }

  /**
   * Return a string describing this Constructor. The
   * string is formatted as the constructor access
   * modifiers, if any, followed by the fully-qualified
   * name of the declaring class, followed by a
   * parenthesized, comma-separated list of the
   * constructor's formal parameter types. For example:
   * <code>
   * public java.util.Hashtable(int,float)
   * </code>
   * <p>
   * The only possible modifiers for constructors are
   * the access modifiers public, protected or
   * private. Only one of these may appear, or none
   * if the constructor has default (package) access.
   * <p>
   * Methods will also display their checked exceptions.
   *
   */
  public String toString()
  {

    StringBuffer sb =
      new StringBuffer(java.lang.reflect.Modifier.toString(getModifiers()));

    if (this instanceof org.apache.xml.utils.synthetic.reflection.Method)
      sb.append(' ').append(getReturnType()).append(
        getDeclaringClass().getName()).append('.').append(getName());
    else
      sb.append(getDeclaringClass().getName());

    sb.append('(');

    org.apache.xml.utils.synthetic.Class[] p = getParameterTypes();

    if (p != null && p.length > 0)
    {
      sb.append(p[0].getName());

      for (int i = 1; i < p.length; ++i)
      {
        sb.append(',').append(p[i].getName());
      }
    }

    sb.append(')');

    if (this instanceof org.apache.xml.utils.synthetic.reflection.Method)
    {
      p = getExceptionTypes();

      if (p != null && p.length > 0)
      {
        sb.append(" throws ").append(p[0].getName());

        for (int i = 1; i < p.length; ++i)
        {
          sb.append(',').append(p[i].getName());
        }
      }
    }

    return sb.toString();
  }

  /**
   * Extension: For synthesis, we need a place to hang a
   * method body.
   *
   * @param language
   * @param body
   *
   * @throws SynthesisException
   */
  public void setBody(String language, StringBuffer body)
          throws SynthesisException
  {

    if (realep != null)
      throw new SynthesisException(SynthesisException.REIFIED);

    this.language = language;
    this.body = body;
  }

  /**
   * Extension: For synthesis, we need a place to hang a
   * method body. Note that this returns a mutable object,
   * for editing etc. Slightly sloppy first cut.
   *
   */
  public StringBuffer getBody()
  {

    if (body == null)
      body = new StringBuffer();

    return body;
  }

  /**
   * Extension: For synthesis, we need a place to hang a
   * method body.
   *
   */
  public String getLanguage()
  {
    return language;
  }

  /**
   * Generate Java code
   *
   * @param basetab
   *
   */
  public String toSource(String basetab)
  {

    StringBuffer sb = new StringBuffer();

    sb.append(basetab).append(
      java.lang.reflect.Modifier.toString(getModifiers()));

    if (this instanceof org.apache.xml.utils.synthetic.reflection.Method)
    {
      if (returntype != null)
        sb.append(" ").append(getReturnType().getJavaName());
      else
        sb.append(" void");
    }

    sb.append(" ").append(getName()).append("(");

    org.apache.xml.utils.synthetic.Class[] types = getParameterTypes();

    if (types != null & types.length > 0)
    {
      sb.append(types[0].getJavaName());

      if (parameternames != null)
        sb.append(' ').append(parameternames[0]);

      for (int i = 1; i < types.length; ++i)
      {
        sb.append(',').append(types[i].getJavaName());

        if (parameternames != null)
          sb.append(' ').append(parameternames[i]);
      }
    }

    sb.append(')');

    types = getExceptionTypes();

    if (types != null & types.length > 0)
    {
      sb.append(" throws ").append(types[0].getJavaName());

      for (int i = 1; i < types.length; ++i)
      {
        sb.append(',').append(types[i].getJavaName());
      }
    }

    if (body == null)
      sb.append("; // No method body available\n");
    else
    {
      sb.append("\n" + basetab + "{\n");

      if (language == null || "java".equals(language))
      {
        sb.append(basetab + "// ***** Should prettyprint this code...\n");
        sb.append(basetab + body + "\n");
      }
      else
      {
        sb.append(basetab + "// ***** Generate BSF invocation!?\n");
      }

      sb.append(basetab + "}\n");
    }

    return sb.toString();
  }
}
