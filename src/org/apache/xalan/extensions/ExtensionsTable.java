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
import org.apache.xml.utils.StringVector;

import org.apache.xpath.objects.XNull;
import org.apache.xpath.XPathProcessorException;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;

import org.apache.xalan.transformer.TransformerImpl;

import org.apache.xalan.templates.Constants;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.TemplateList;

import org.apache.xpath.XPathContext;

import org.apache.xml.utils.QName;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
   * The StylesheetRoot associated with this extensions table.
   */
  private StylesheetRoot m_sroot;
  
  /**
   * <meta name="usage" content="advanced"/>
   * The constructor (called from TransformerImpl) registers the
   * StylesheetRoot for the transformation and instantiates an
   * ExtensionHandler for each extension namespace.
   */
  public ExtensionsTable(StylesheetRoot sroot)
    throws javax.xml.transform.TransformerException
  {
    m_sroot = sroot;
    Vector extensions = m_sroot.getExtensions();
    for (int i = 0; i < extensions.size(); i++)
    {
      ExtensionNamespaceSupport extNamespaceSpt = 
                 (ExtensionNamespaceSupport)extensions.elementAt(i);
      ExtensionHandler extHandler = extNamespaceSpt.launch();
        if (extHandler != null)
          addExtensionNamespace(extNamespaceSpt.getNamespace(), extHandler);
      }
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
      if (extNS != null)
        isAvailable = extNS.isFunctionAvailable(funcName);
    }
    return isAvailable;
  }
  
  /**
   * Execute the element-available() function.
   * @param ns       the URI of namespace in which the function is needed
   * @param elemName name of element being tested
   *
   * @return whether the given element is available or not.
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
      if (extNS != null) // defensive
        isAvailable = extNS.isElementAvailable(elemName);
    } 
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
  public Object extFunction(String ns, String funcName, 
                            Vector argVec, Object methodKey, 
                            ExpressionContext exprContext)
            throws javax.xml.transform.TransformerException
  {
    Object result = null;
    if (null != ns)
    {
      ExtensionHandler extNS =
        (ExtensionHandler) m_extensionFunctionNamespaces.get(ns);
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
        throw new XPathProcessorException(XSLMessages.createMessage(XSLTErrorResources.ER_EXTENSION_FUNC_UNKNOWN, new Object[]{ns, funcName })); 
        //"Extension function '" + ns + ":" + funcName + "' is unknown");
      }
    }
    return result;    
  }
}
