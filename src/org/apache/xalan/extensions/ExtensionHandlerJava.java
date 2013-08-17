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
package org.apache.xalan.extensions;

import java.util.Hashtable;

/**
 * <meta name="usage" content="internal"/>
 * Abstract base class handling the java language extensions for XPath.
 * This base class provides cache management shared by all of the
 * various java extension handlers.
 *
 */
public abstract class ExtensionHandlerJava extends ExtensionHandler
{

  /** Extension class name         */
  protected String m_className = "";

  /** Table of cached methods          */
  private Hashtable m_cachedMethods = new Hashtable();

  /**
   * Construct a new extension handler given all the information
   * needed.
   *
   * @param namespaceUri the extension namespace URI that I'm implementing
   * @param funcNames    string containing list of functions of extension NS
   * @param lang         language of code implementing the extension
   * @param srcURL       value of src attribute (if any) - treated as a URL
   *                     or a classname depending on the value of lang. If
   *                     srcURL is not null, then scriptSrc is ignored.
   * @param scriptSrc    the actual script code (if any)
   * @param scriptLang   the scripting language
   * @param className    the extension class name 
   */
  protected ExtensionHandlerJava(String namespaceUri, String scriptLang,
                                 String className)
  {

    super(namespaceUri, scriptLang);

    m_className = className;
  }

  /**
   * Look up the entry in the method cache.
   * @param methodKey   A key that uniquely identifies this invocation in
   *                    the stylesheet.
   * @param objType     A Class object or instance object representing the type
   * @param methodArgs  An array of the XObject arguments to be used for
   *                    function mangling.
   *
   * @return The given method from the method cache
   */
  public Object getFromCache(Object methodKey, Object objType,
                             Object[] methodArgs)
  {

    // Eventually, we want to insert code to mangle the methodKey with methodArgs
    return m_cachedMethods.get(methodKey);
  }

  /**
   * Add a new entry into the method cache.
   * @param methodKey   A key that uniquely identifies this invocation in
   *                    the stylesheet.
   * @param objType     A Class object or instance object representing the type
   * @param methodArgs  An array of the XObject arguments to be used for
   *                    function mangling.
   * @param methodObj   A Class object or instance object representing the method
   *
   * @return The cached method object
   */
  public Object putToCache(Object methodKey, Object objType,
                           Object[] methodArgs, Object methodObj)
  {

    // Eventually, we want to insert code to mangle the methodKey with methodArgs
    return m_cachedMethods.put(methodKey, methodObj);
  }
}
