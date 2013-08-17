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
package org.apache.xpath.compiler;

import java.lang.Class;

import org.apache.xpath.res.XPATHErrorResources;

import org.w3c.dom.Node;

import java.util.Vector;

import org.apache.xpath.XPathContext;
import org.apache.xpath.XPath;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.functions.Function;

/**
 * <meta name="usage" content="advanced"/>
 * Lazy load of functions into the function table as needed, so we don't 
 * have to load all the functions allowed in XPath and XSLT on startup.
 */
public class FuncLoader
{

  /** The function ID, which may correspond to one of the FUNC_XXX values 
   *  found in {@link org.apache.xpath.compiler.FunctionTable}, but may 
   *  be a value installed by an external module.  */
  private int m_funcID;

  /** The class name of the function.  Must not be null.   */
  private String m_funcName;

  /**
   * Get the local class name of the function class.  If function name does 
   * not have a '.' in it, it is assumed to be relative to 
   * 'org.apache.xpath.functions'.
   *
   * @return The class name of the {org.apache.xpath.functions.Function} class.
   */
  public String getName()
  {
    return m_funcName;
  }

  /**
   * Construct a function loader
   *
   * @param funcName The class name of the {org.apache.xpath.functions.Function} 
   *             class, which, if it does not have a '.' in it, is assumed to 
   *             be relative to 'org.apache.xpath.functions'. 
   * @param funcID  The function ID, which may correspond to one of the FUNC_XXX 
   *    values found in {@link org.apache.xpath.compiler.FunctionTable}, but may 
   *    be a value installed by an external module. 
   */
  public FuncLoader(String funcName, int funcID)
  {

    super();

    m_funcID = funcID;
    m_funcName = funcName;
  }

  /**
   * Get a Function instance that this instance is liaisoning for.
   *
   * @return non-null reference to Function derivative.
   *
   * @throws javax.xml.transform.TransformerException if ClassNotFoundException, 
   *    IllegalAccessException, or InstantiationException is thrown.
   */
  public Function getFunction() throws javax.xml.transform.TransformerException
  {

    try
    {
      Class function;

      // first get package name if necessary
      if (m_funcName.indexOf(".") < 0)
      {

        // String thisName = this.getClass().getName();
        // int lastdot = thisName.lastIndexOf(".");
        // String classname = thisName.substring(0,lastdot+1) + m_funcName; 
        String classname = "org.apache.xpath.functions." + m_funcName;

        function = Class.forName(classname);
      }
      else
        function = Class.forName(m_funcName);

      Function func = (Function) function.newInstance();

      return func;
    }
    catch (ClassNotFoundException e)
    {
      throw new javax.xml.transform.TransformerException(e);
    }
    catch (IllegalAccessException e)
    {
      throw new javax.xml.transform.TransformerException(e);
    }
    catch (InstantiationException e)
    {
      throw new javax.xml.transform.TransformerException(e);
    }
  }
}
