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

import java.util.Vector;
import java.lang.reflect.Method;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xml.utils.QName;

// Temp??
import org.apache.xalan.transformer.TransformerImpl;

/**
 * <meta name="usage" content="internal"/>
 * Abstract base class for handling an extension namespace for XPath.
 * Provides functions to test a function's existence and call a function.
 * Also provides functions for calling an element and testing for
 * an element's existence.
 *
 * @author Sanjiva Weerawarana (sanjiva@watson.ibm.com)
 */
public abstract class ExtensionHandler
{

  /** uri of the extension namespace          */
  protected String m_namespaceUri; 

  /** scripting language of implementation          */
  protected String m_scriptLang;

  /** a zero length Object array used in getClassForName() */
  private static final Object NO_OBJS[] = new Object[0];

  /** the Method object for getContextClassLoader */
  private static Method getCCL;

  static
  {
    try
    {
      getCCL = Thread.class.getMethod("getContextClassLoader", new Class[0]);
    }
    catch (Exception e)
    {
      getCCL = null;
    }
  }

  /**
   * Replacement for Class.forName.  This method loads a class using the context class loader
   * if we're running under Java2 or higher.  If we're running under Java1, this
   * method just uses Class.forName to load the class.
   * 
   * @param className Name of the class to load
   */
  public static Class getClassForName(String className)
      throws ClassNotFoundException
  {
    Class result = null;
    
    // Hack for backwards compatibility with XalanJ1 stylesheets
    if(className.equals("org.apache.xalan.xslt.extensions.Redirect"))
      className = "org.apache.xalan.lib.Redirect";
      
    if (getCCL != null)
    {
      try {
        ClassLoader contextClassLoader =
                              (ClassLoader) getCCL.invoke(Thread.currentThread(), NO_OBJS);
        result = contextClassLoader.loadClass(className);
      }
      catch (ClassNotFoundException cnfe)
      {
        result = Class.forName(className);
      }
      catch (Exception e)
      {
        getCCL = null;
        result = Class.forName(className);
      }
    }

    else
       result = Class.forName(className);

    return result;
 }


  /**
   * Construct a new extension namespace handler given all the information
   * needed.
   *
   * @param namespaceUri the extension namespace URI that I'm implementing
   * @param scriptLang   language of code implementing the extension
   */
  protected ExtensionHandler(String namespaceUri, String scriptLang)
  {
    m_namespaceUri = namespaceUri;
    m_scriptLang = scriptLang;
  }

  /**
   * Tests whether a certain function name is known within this namespace.
   * @param function name of the function being tested
   * @return true if its known, false if not.
   */
  public abstract boolean isFunctionAvailable(String function);

  /**
   * Tests whether a certain element name is known within this namespace.
   * @param function name of the function being tested
   *
   * @param element Name of element to check
   * @return true if its known, false if not.
   */
  public abstract boolean isElementAvailable(String element);

  /**
   * Process a call to a function.
   *
   * @param funcName Function name.
   * @param args     The arguments of the function call.
   * @param methodKey A key that uniquely identifies this class and method call.
   * @param exprContext The context in which this expression is being executed.
   *
   * @return the return value of the function evaluation.
   *
   * @throws TransformerException          if parsing trouble
   */
  public abstract Object callFunction(
    String funcName, Vector args, Object methodKey,
      ExpressionContext exprContext) throws TransformerException;

  /**
   * Process a call to this extension namespace via an element. As a side
   * effect, the results are sent to the TransformerImpl's result tree.
   *
   * @param localPart      Element name's local part.
   * @param element        The extension element being processed.
   * @param transformer      Handle to TransformerImpl.
   * @param stylesheetTree The compiled stylesheet tree.
   * @param mode           The current mode.
   * @param sourceTree     The root of the source tree (but don't assume
   *                       it's a Document).
   * @param sourceNode     The current context node.
   * @param methodKey      A key that uniquely identifies this class and method call.
   *
   * @throws XSLProcessorException thrown if something goes wrong
   *            while running the extension handler.
   * @throws MalformedURLException if loading trouble
   * @throws FileNotFoundException if loading trouble
   * @throws IOException           if loading trouble
   * @throws TransformerException          if parsing trouble
   */
  public abstract void processElement(
    String localPart, ElemTemplateElement element, TransformerImpl transformer,
      Stylesheet stylesheetTree, Object methodKey) throws TransformerException, IOException;
}
