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
import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.io.IOException;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import org.apache.xalan.transformer.TransformerImpl;

import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemExsltFunction;
import org.apache.xalan.templates.ElemExsltFuncResult;
import org.apache.xalan.templates.Constants;

import org.apache.xml.utils.QName;

import org.apache.xpath.XPathContext;
import org.apache.xpath.VariableStack;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import javax.xml.transform.TransformerException;

/**
 * Execute EXSLT functions, determine the availability of EXSLT functions, and the
 * availability of an EXSLT result element.
 */
public class ExtensionHandlerExsltFunction extends ExtensionHandler
{
  private String m_namespace;
  private StylesheetRoot m_stylesheet;
  private static final QName RESULTQNAME = 
                  new QName(Constants.S_EXSLT_FUNCTIONS_URL,
                            Constants.EXSLT_ELEMNAME_FUNCRESULT_STRING);
  /**
   * Constructor called from ElemExsltFunction runtimeInit().
   */  
  public ExtensionHandlerExsltFunction(String ns, StylesheetRoot stylesheet)
  {
    super(ns, "xml"); // required by ExtensionHandler interface.
    m_namespace = ns;
    m_stylesheet = stylesheet;
  }
  
  /**
   * Required by ExtensionHandler (an abstract method). No-op.
   */
  public void processElement(
    String localPart, ElemTemplateElement element, TransformerImpl transformer,
    Stylesheet stylesheetTree, Object methodKey) throws TransformerException, IOException
  {}
  
  /**
   * Get the ElemExsltFunction element associated with the 
   * function.
   * 
   * @param funcName Local name of the function.
   * @return the ElemExsltFunction element associated with
   * the function, null if none exists.
   */
  public ElemExsltFunction getFunction(String funcName)
  {
    QName qname = new QName(m_namespace, funcName);
    ElemTemplate templ = m_stylesheet.getTemplateComposed(qname);
    if (templ != null && templ instanceof ElemExsltFunction)
      return (ElemExsltFunction) templ;
    else
      return null;    
  }
  
  
  /**
   * Does the EXSLT function exist?
   * 
   * @param funcName Local name of the function.
   * @return true if the function exists.
   */  
  public boolean isFunctionAvailable(String funcName)
  {
    return getFunction(funcName)!= null;
  }
    
   /** If an element-available() call applies to an EXSLT result element within 
   * an EXSLT function element, return true.
   *
   * Note: The EXSLT function element is a template-level element, and 
   * element-available() returns false for it.
   * 
   * @param Local name of the function.
   * @return true if the function is available.
   */
  public boolean isElementAvailable(String elemName)
  {
    if (!(new QName(m_namespace, elemName).equals(RESULTQNAME)))
    {
      return false;
    }
    else
    {
      ElemTemplateElement elem = m_stylesheet.getFirstChildElem();
      while (elem != null && elem != m_stylesheet)
      {
        if (elem instanceof ElemExsltFuncResult && ancestorIsFunction(elem))
          return true;
        ElemTemplateElement  nextElem = elem.getFirstChildElem();
        if (nextElem == null)
          nextElem = elem.getNextSiblingElem();
        if (nextElem == null)
          nextElem = elem.getParentElem();
        elem = nextElem;
      }
    }
    return false;
  }

  /**
   * Determine whether the func:result element is within a func:function element. 
   * If not, it is illegal.
   */
  private boolean ancestorIsFunction(ElemTemplateElement child)
  {
    while (child.getParentElem() != null 
           && !(child.getParentElem() instanceof StylesheetRoot))
    {
      if (child.getParentElem() instanceof ElemExsltFunction)
        return true;
      child = child.getParentElem();      
    }
    return false;
  }

  /**
   * Execute the EXSLT function and return the result value.
   * 
   * @param funcName Name of the EXSLT function.
   * @param args     The arguments of the function call.
   * @param methodKey Not used.
   * @param exprContext Used to get the XPathContext.
   * @return the return value of the function evaluation.
   * @throws TransformerException
   */
  public Object callFunction(
      String funcName, Vector args, Object methodKey,
      ExpressionContext exprContext) throws TransformerException
  {
    XObject[] methodArgs;
    methodArgs = new XObject[args.size()];
    try
    {
      for (int i = 0; i < methodArgs.length; i++)
      {
        methodArgs[i] =  XObject.create(args.elementAt(i));
      }
      ElemExsltFunction elemFunc = getFunction(funcName);
      XPathContext context = exprContext.getXPathContext();
      TransformerImpl transformer = (TransformerImpl)context.getOwnerObject();
      elemFunc.execute(transformer, methodArgs);
      
      VariableStack varStack = context.getVarStack();
      XObject val = new XString(""); // value returned if no result element.
      
      int resultIndex = elemFunc.getResultIndex();   
      if (varStack.isLocalSet(resultIndex))
        val = varStack.getLocalVariable(context, resultIndex);
      return val;
    }
    catch (Exception e)
    {
      // e.printStackTrace();
      throw new TransformerException(e);
    }
  }
  
}
