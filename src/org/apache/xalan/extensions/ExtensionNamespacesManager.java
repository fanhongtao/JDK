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

import org.apache.xalan.templates.Constants;
import java.util.Vector;

/**
 * Used during assembly of a stylesheet to collect the information for each 
 * extension namespace that is required during the transformation process 
 * to generate an {@link ExtensionHandler}.
 * 
 */
public class ExtensionNamespacesManager
{
  /**
   * Vector of ExtensionNamespaceSupport objects to be used to generate ExtensionHandlers.
   */
  private Vector m_extensions = new Vector();
  /**
   * Vector of ExtensionNamespaceSupport objects for predefined ExtensionNamespaces. Elements
   * from this vector are added to the m_extensions vector when encountered in the stylesheet.
   */
  private Vector m_predefExtensions = new Vector(7);
  /**
   * Vector of extension namespaces for which sufficient information is not yet available to
   * complete the registration process.
   */
  private Vector m_unregisteredExtensions = new Vector();
  
  /**
   * An ExtensionNamespacesManager is instantiated the first time an extension function or
   * element is found in the stylesheet. During initialization, a vector of ExtensionNamespaceSupport
   * objects is created, one for each predefined extension namespace.
   */
  public ExtensionNamespacesManager()
  {
    setPredefinedNamespaces();
  }
  
  /**
   * If necessary, register the extension namespace found compiling a function or 
   * creating an extension element. 
   * 
   * If it is a predefined namespace, create a
   * support object to simplify the instantiate of an appropriate ExtensionHandler
   * during transformation runtime. Otherwise, add the namespace, if necessary,
   * to a vector of undefined extension namespaces, to be defined later.
   * 
   */
  public void registerExtension(String namespace)
  {
    if (namespaceIndex(namespace, m_extensions) == -1)
    {
      int predef = namespaceIndex(namespace, m_predefExtensions);
      if (predef !=-1)
        m_extensions.addElement(m_predefExtensions.elementAt(predef));
      else if (!(m_unregisteredExtensions.contains(namespace)))
        m_unregisteredExtensions.addElement(namespace);       
    }
  }
  
  /**
   * Register the extension namespace for an ElemExtensionDecl or ElemFunction,
   * and prepare a support object to launch the appropriate ExtensionHandler at 
   * transformation runtime.
   */  
  public void registerExtension(ExtensionNamespaceSupport extNsSpt)
  {
    String namespace = extNsSpt.getNamespace();
    if (namespaceIndex(namespace, m_extensions) == -1)
    {
      m_extensions.addElement(extNsSpt);
      if (m_unregisteredExtensions.contains(namespace))
        m_unregisteredExtensions.removeElement(namespace);
    }
    
  }
  
  /**
   * Get the index for a namespace entry in the extension namespace Vector, -1 if
   * no such entry yet exists.
   */
  public int namespaceIndex(String namespace, Vector extensions)
  {
    for (int i = 0; i < extensions.size(); i++)
    {
      if (((ExtensionNamespaceSupport)extensions.elementAt(i)).getNamespace().equals(namespace))
        return i;
    }
    return -1;
  }
  
    
  /**
   * Get the vector of extension namespaces. Used to provide
   * the extensions table access to a list of extension
   * namespaces encountered during composition of a stylesheet.
   */
  public Vector getExtensions()
  {
    return m_extensions;
  }
  
  /**
   * Attempt to register any unregistered extension namespaces.
   */
  public void registerUnregisteredNamespaces()
  {
    for (int i = 0; i < m_unregisteredExtensions.size(); i++)
    {
      String ns = (String)m_unregisteredExtensions.elementAt(i);
      ExtensionNamespaceSupport extNsSpt = defineJavaNamespace(ns);
      if (extNsSpt != null)
        m_extensions.addElement(extNsSpt);
    }    
  }
  
    /**
   * For any extension namespace that is not either predefined or defined 
   * by a "component" declaration or exslt function declaration, attempt 
   * to create an ExtensionNamespaceSuport object for the appropriate 
   * Java class or Java package Extension Handler.
   * 
   * Called by StylesheetRoot.recompose(), after all ElemTemplate compose()
   * operations have taken place, in order to set up handlers for
   * the remaining extension namespaces.
   * 
   * @param ns The extension namespace URI.
   * @return   An ExtensionNamespaceSupport object for this namespace
   * (which defines the ExtensionHandler to be used), or null if such 
   * an object cannot be created. 
   *
   * @throws javax.xml.transform.TransformerException
   */
  public ExtensionNamespaceSupport defineJavaNamespace(String ns)
  {
    return defineJavaNamespace(ns, ns);
  }
  public ExtensionNamespaceSupport defineJavaNamespace(String ns, String classOrPackage)
  {
    if(null == ns || ns.trim().length() == 0) // defensive. I don't think it's needed.  -sb
      return null;

    // Prepare the name of the actual class or package, stripping
    // out any leading "class:".  Next, see if there is a /.  If so,
    // only look at the text to the right of the rightmost /.
    String className = classOrPackage;
    if (className.startsWith("class:"))
      className = className.substring(6);

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
      return new ExtensionNamespaceSupport(
                           ns, 
                           "org.apache.xalan.extensions.ExtensionHandlerJavaClass",                                         
                           new Object[]{ns, "javaclass", className});
    }
    catch (ClassNotFoundException e)
    {
      return new ExtensionNamespaceSupport(
                            ns, 
                            "org.apache.xalan.extensions.ExtensionHandlerJavaPackage",
                            new Object[]{ns, "javapackage", className + "."});
    }
  }
  
/*
  public ExtensionNamespaceSupport getSupport(int index, Vector extensions)
  {
    return (ExtensionNamespaceSupport)extensions.elementAt(index);
  }
*/
  
  
  /**
   * Set up a Vector for predefined extension namespaces.
   */
  private void setPredefinedNamespaces()
  {    
    String uri = Constants.S_EXTENSIONS_JAVA_URL;
    String handlerClassName = "org.apache.xalan.extensions.ExtensionHandlerJavaPackage";
    String lang = "javapackage";
    String lib = "";
    m_predefExtensions.addElement(new ExtensionNamespaceSupport(uri, handlerClassName,
                                             new Object[]{uri, lang, lib}));
   
    uri = Constants.S_EXTENSIONS_OLD_JAVA_URL;
    m_predefExtensions.addElement(new ExtensionNamespaceSupport(uri, handlerClassName,
                                             new Object[]{uri, lang, lib}));
    
    uri = Constants.S_EXTENSIONS_LOTUSXSL_JAVA_URL;
    m_predefExtensions.addElement(new ExtensionNamespaceSupport(uri, handlerClassName,
                                             new Object[]{uri, lang, lib}));
    
    uri = Constants.S_EXTENSIONS_XALANLIB_URL;
    handlerClassName = "org.apache.xalan.extensions.ExtensionHandlerJavaClass";
    lang = "javaclass"; // for remaining predefined extension namespaces.    
    lib = "org.apache.xalan.lib.Extensions";
    m_predefExtensions.addElement(new ExtensionNamespaceSupport(uri, handlerClassName,
                                             new Object[]{uri, lang, lib}));
    
    // Xalan extension namespaces (redirect, pipe and SQL).
    uri = Constants.S_EXTENSIONS_REDIRECT_URL;
    lib = "org.apache.xalan.lib.Redirect";
    m_predefExtensions.addElement(new ExtensionNamespaceSupport(uri, handlerClassName,
                                             new Object[]{uri, lang, lib}));
 
    uri = Constants.S_EXTENSIONS_PIPE_URL;
    lib = "org.apache.xalan.lib.PipeDocument";
    m_predefExtensions.addElement(new ExtensionNamespaceSupport(uri, handlerClassName,
                                             new Object[]{uri, lang, lib}));
 
    uri = Constants.S_EXTENSIONS_SQL_URL;
    lib = "org.apache.xalan.lib.sql.XConnection";
    m_predefExtensions.addElement(new ExtensionNamespaceSupport(uri, handlerClassName,
                                             new Object[]{uri, lang, lib}));
 
    
    //EXSLT namespaces (not including EXSLT function namespaces which are
    // registered by the associated ElemFunction.
    uri = Constants.S_EXSLT_COMMON_URL;
    lib = "org.apache.xalan.lib.ExsltCommon";
    m_predefExtensions.addElement(new ExtensionNamespaceSupport(uri, handlerClassName,
                                             new Object[]{uri, lang, lib}));

    uri = Constants.S_EXSLT_MATH_URL;
    lib = "org.apache.xalan.lib.ExsltMath";
    m_predefExtensions.addElement(new ExtensionNamespaceSupport(uri, handlerClassName,
                                             new Object[]{uri, lang, lib}));
    
    uri = Constants.S_EXSLT_SETS_URL;
    lib = "org.apache.xalan.lib.ExsltSets";
    m_predefExtensions.addElement(new ExtensionNamespaceSupport(uri, handlerClassName,
                                             new Object[]{uri, lang, lib}));
    
    uri = Constants.S_EXSLT_DATETIME_URL;
    lib = "org.apache.xalan.lib.ExsltDatetime";
    m_predefExtensions.addElement(new ExtensionNamespaceSupport(uri, handlerClassName,
                                             new Object[]{uri, lang, lib}));
                                             
    uri = Constants.S_EXSLT_DYNAMIC_URL;
    lib = "org.apache.xalan.lib.ExsltDynamic";
    m_predefExtensions.addElement(new ExtensionNamespaceSupport(uri, handlerClassName,
                                             new Object[]{uri, lang, lib}));

    uri = Constants.S_EXSLT_STRINGS_URL;
    lib = "org.apache.xalan.lib.ExsltStrings";
    m_predefExtensions.addElement(new ExtensionNamespaceSupport(uri, handlerClassName,
                                             new Object[]{uri, lang, lib}));                                             
  }    
  
}