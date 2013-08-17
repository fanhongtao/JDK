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
import java.util.Vector;

import org.apache.xpath.objects.XNull;
import org.apache.xpath.XPathProcessorException;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;

/**
 * <meta name="usage" content="internal"/>
 * Class holding a table registered extension namespace handlers
 */
public class ExtensionsTable
{

  /**
   * <meta name="usage" content="internal"/>
   * Table of extensions that may be called from the expression language
   * via the call(name, ...) function.  Objects are keyed on the call
   * name.
   */
  public Hashtable m_extensionFunctionNamespaces = new Hashtable();

  /**
   * <meta name="usage" content="internal"/>
   * Primes the new ExtensionsTable object with built-in namespaces.
   */
  public ExtensionsTable()
  {

    // register the java namespace as being implemented by the 
    // xslt-javaclass engine. Note that there's no real code
    // per se for this extension as the functions carry the 
    // object on which to call etc. and all the logic of breaking
    // that up is in the xslt-javaclass engine.
    String uri = "http://xml.apache.org/xslt/java";
    ExtensionHandler fh = new ExtensionHandlerJavaPackage(uri,
                            "xslt-javaclass", "");

    addExtensionNamespace(uri, fh);

    uri = "http://xsl.lotus.com/java";

    addExtensionNamespace(uri, fh);

    uri = "http://xml.apache.org/xalan";
    fh = new ExtensionHandlerJavaClass(uri, "javaclass",
                                       "org.apache.xalan.lib.Extensions");

    addExtensionNamespace(uri, fh);
  }

  /**
   * Get an ExtensionHandler object that represents the
   * given namespace.
   * @param extns A valid extension namespace.
   *
   * @return ExtensionHandler object that represents the
   * given namespace.
   */
  public ExtensionHandler get(String extns)
  {
    return (ExtensionHandler) m_extensionFunctionNamespaces.get(extns);
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Register an extension namespace handler. This handler provides
   * functions for testing whether a function is known within the
   * namespace and also for invoking the functions.
   *
   * @param uri the URI for the extension.
   * @param extNS the extension handler.
   */
  public void addExtensionNamespace(String uri, ExtensionHandler extNS)
  {
    m_extensionFunctionNamespaces.put(uri, extNS);
  }

  /**
   * Execute the function-available() function.
   * @param ns       the URI of namespace in which the function is needed
   * @param funcName the function name being tested
   *
   * @return whether the given function is available or not.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean functionAvailable(String ns, String funcName)
          throws javax.xml.transform.TransformerException
  {

    boolean isAvailable = false;

    if (null != ns)
    {
      ExtensionHandler extNS =
        (ExtensionHandler) m_extensionFunctionNamespaces.get(ns);

      if (extNS == null)
      {
        extNS = makeJavaNamespace(ns);

        addExtensionNamespace(ns, extNS);
      }

      if (extNS != null)
      {
        isAvailable = extNS.isFunctionAvailable(funcName);
      }
    }

    // System.err.println (">>> functionAvailable (ns=" + ns + 
    //                    ", func=" + funcName + ") = " + isAvailable);
    return isAvailable;
  }

  /**
   * Execute the element-available() function.
   * @param ns       the URI of namespace in which the function is needed
   * @param funcName the function name being tested
   * @param elemName name of element being tested
   *
   * @return whether the given function is available or not.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean elementAvailable(String ns, String elemName)
          throws javax.xml.transform.TransformerException
  {

    boolean isAvailable = false;

    if (null != ns)
    {
      ExtensionHandler extNS =
        (ExtensionHandler) m_extensionFunctionNamespaces.get(ns);

      if (extNS == null)
      {
        extNS = makeJavaNamespace(ns);

        addExtensionNamespace(ns, extNS);
      }

      if (extNS != null)
      {
        isAvailable = extNS.isElementAvailable(elemName);
      }
    }

    // System.err.println (">>> elementAvailable (ns=" + ns + 
    //                    ", elem=" + elemName + ") = " + isAvailable);
    return isAvailable;
  }

  /**
   * Handle an extension function.
   * @param ns        the URI of namespace in which the function is needed
   * @param funcName  the function name being called
   * @param argVec    arguments to the function in a vector
   * @param methodKey a unique key identifying this function instance in the
   *                  stylesheet
   * @param exprContext a context which may be passed to an extension function
   *                  and provides callback functions to access various
   *                  areas in the environment
   *
   * @return result of executing the function
   *
   * @throws javax.xml.transform.TransformerException
   */
  public Object extFunction(
          String ns, String funcName, Vector argVec, Object methodKey, 
          ExpressionContext exprContext)
            throws javax.xml.transform.TransformerException
  {

    Object result = null;

    if (null != ns)
    {
      ExtensionHandler extNS =
        (ExtensionHandler) m_extensionFunctionNamespaces.get(ns);

      // If the handler for this extension URI is not found try to auto declare 
      // this extension namespace:
      if (null == extNS)
      {
        extNS = makeJavaNamespace(ns);

        addExtensionNamespace(ns, extNS);
      }

      if (null != extNS)
      {
        try
        {
          result = extNS.callFunction(funcName, argVec, methodKey,
                                      exprContext);
        }
        catch (javax.xml.transform.TransformerException e)
        {
          throw e;
        }
        catch (Exception e)
        {
          throw new javax.xml.transform.TransformerException(e);
        }
      }
      else
      {
        throw new XPathProcessorException(XSLMessages.createMessage(XSLTErrorResources.ER_EXTENSION_FUNC_UNKNOWN, new Object[]{ns, funcName })); //"Extension function '" + ns + ":"
                                         // + funcName + "' is unknown");
      }
    }

    return result;
  }

  /**
   * Declare the appropriate java extension handler.
   * @param ns        the URI of namespace in which the function is needed
   * @return          an ExtensionHandler for this namespace, or null if 
   *                  not found.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public ExtensionHandler makeJavaNamespace(String ns)
          throws javax.xml.transform.TransformerException
  {
    if(null == ns || ns.trim().length() == 0) // defensive. I don't think it's needed.  -sb
      return null;

    // First, prepare the name of the actual class or package.  We strip
    // out any leading "class:".  Next, we see if there is a /.  If so,
    // only look at anything to the right of the rightmost /.
    // In the documentation, we state that any classes or packages
    // declared using this technique must start with xalan://.  However,
    // in this version, we don't enforce that.
    String className = ns;

    if (className.startsWith("class:"))
    {
      className = className.substring(6);
    }

    int lastSlash = className.lastIndexOf("/");

    if (-1 != lastSlash)
      className = className.substring(lastSlash + 1);
      
    // The className can be null here, and can cause an error in getClassForName
    // in JDK 1.8.
    if(null == className || className.trim().length() == 0) 
      return null;

    try
    {
      ExtensionHandler.getClassForName(className);

      return new ExtensionHandlerJavaClass(ns, "javaclass", className);
    }
    catch (ClassNotFoundException e)
    {
      return new ExtensionHandlerJavaPackage(ns, "javapackage",
                                             className + ".");
    }
  }
}
