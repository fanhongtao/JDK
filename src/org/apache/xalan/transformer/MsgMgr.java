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
package org.apache.xalan.transformer;

import org.apache.xalan.res.XSLMessages;

import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.dom.DOMLocator;
import javax.xml.transform.SourceLocator;

/**
 * This class will manage error messages, warning messages, and other types of
 * message events.
 */
public class MsgMgr
{

  /**
   * Create a message manager object.
   *
   * @param transformer non transformer instance
   */
  public MsgMgr(TransformerImpl transformer)
  {
    m_transformer = transformer;
  }

  /** Transformer instance          */
  private TransformerImpl m_transformer;

  /** XSLMessages instance, sets things up for issuing messages          */
  private static XSLMessages m_XSLMessages = new XSLMessages();

  /**
   * Warn the user of a problem.
   * This is public for access by extensions.
   *
   * @param msg The message text to issue
   * @param terminate Flag indicating whether to terminate this process
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws TransformerException
   */
  public void message(SourceLocator srcLctr, String msg, boolean terminate) throws TransformerException
  {

    ErrorListener errHandler = m_transformer.getErrorListener();

    if (null != errHandler)
    {
      errHandler.warning(new TransformerException(msg, srcLctr));
    }
    else
    {
      if (terminate)
        throw new TransformerException(msg, srcLctr);
      else
        System.out.println(msg);
    }
  }

  /**
   * <meta name="usage" content="internal"/>
   * Warn the user of a problem.
   *
   * @param msg Message text to issue
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws TransformerException
   */
  public void warn(SourceLocator srcLctr, String msg) throws TransformerException
  {
    warn(srcLctr, null, null, msg, null);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Warn the user of a problem.
   *
   * @param msg Message text to issue
   * @param args Arguments to pass to the message
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws TransformerException
   */
  public void warn(SourceLocator srcLctr, String msg, Object[] args) throws TransformerException
  {
    warn(srcLctr, null, null, msg, args);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Warn the user of a problem.
   *
   * 
   * @param styleNode Stylesheet node
   * @param sourceNode Source tree node
   * @param msg Message text to issue
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws TransformerException
   */
  public void warn(SourceLocator srcLctr, Node styleNode, Node sourceNode, String msg)
          throws TransformerException
  {
    warn(srcLctr, styleNode, sourceNode, msg, null);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Warn the user of a problem.
   *
   * @param styleNode Stylesheet node
   * @param sourceNode Source tree node
   * @param msg Message text to issue
   * @param args Arguments to pass to the message
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws TransformerException
   */
  public void warn(SourceLocator srcLctr, Node styleNode, Node sourceNode, String msg, Object args[])
          throws TransformerException
  {

    String formattedMsg = m_XSLMessages.createWarning(msg, args);
    ErrorListener errHandler = m_transformer.getErrorListener();

    if (null != errHandler)
      errHandler.warning(new TransformerException(formattedMsg, srcLctr));
    else
      System.out.println(formattedMsg);
  }

  /* This method is not properly i18nized. We need to use the following method
   * <meta name="usage" content="internal"/>
   * Tell the user of an error, and probably throw an
   * exception.
   *
   * @param msg Message text to issue
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws TransformerException
   *
  public void error(SourceLocator srcLctr, String msg) throws TransformerException
  {

    // Locator locator = m_stylesheetLocatorStack.isEmpty()
    //                  ? null :
    //                    ((Locator)m_stylesheetLocatorStack.peek());
    // Locator locator = null;
    ErrorListener errHandler = m_transformer.getErrorListener();

    if (null != errHandler)
      errHandler.fatalError(new TransformerException(msg, srcLctr));
    else
      throw new TransformerException(msg, srcLctr);
  }

 */

  /**
   * <meta name="usage" content="internal"/>
   * Tell the user of an error, and probably throw an
   * exception.
   *
   * @param msg Message text to issue
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws TransformerException
   */
  public void error(SourceLocator srcLctr, String msg) throws TransformerException
  {
    error(srcLctr, null, null, msg, null);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Tell the user of an error, and probably throw an
   * exception.
   *
   * @param msg Message text to issue
   * @param args Arguments to be passed to the message 
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws TransformerException
   */
  public void error(SourceLocator srcLctr, String msg, Object[] args) throws TransformerException
  {
    error(srcLctr, null, null, msg, args);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Tell the user of an error, and probably throw an
   * exception.
   *
   * @param msg Message text to issue
   * @param e Exception to throw
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws TransformerException
   */
  public void error(SourceLocator srcLctr, String msg, Exception e) throws TransformerException
  {
    error(srcLctr, msg, null, e);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Tell the user of an error, and probably throw an
   * exception.
   *
   * @param msg Message text to issue
   * @param args Arguments to use in message
   * @param e Exception to throw
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws TransformerException
   */
  public void error(SourceLocator srcLctr, String msg, Object args[], Exception e) throws TransformerException
  {

    //msg  = (null == msg) ? XSLTErrorResources.ER_PROCESSOR_ERROR : msg;
    String formattedMsg = m_XSLMessages.createMessage(msg, args);

    // Locator locator = m_stylesheetLocatorStack.isEmpty()
    //                   ? null :
    //                    ((Locator)m_stylesheetLocatorStack.peek());
    // Locator locator = null;
    ErrorListener errHandler = m_transformer.getErrorListener();

    if (null != errHandler)
      errHandler.fatalError(new TransformerException(formattedMsg, srcLctr));
    else
      throw new TransformerException(formattedMsg, srcLctr);
  }

  /**
   *  <meta name="usage" content="internal"/>
   * Tell the user of an error, and probably throw an
   * exception.
   *
   * @param styleNode Stylesheet node
   * @param sourceNode Source tree node
   * @param msg Message text to issue
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws TransformerException
   */
  public void error(SourceLocator srcLctr, Node styleNode, Node sourceNode, String msg)
          throws TransformerException
  {
    error(srcLctr, styleNode, sourceNode, msg, null);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Tell the user of an error, and probably throw an
   * exception.
   *
   * @param styleNode Stylesheet node
   * @param sourceNode Source tree node
   * @param msg Message text to issue
   * @param args Arguments to use in message
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws TransformerException
   */
  public void error(SourceLocator srcLctr, Node styleNode, Node sourceNode, String msg, Object args[])
          throws TransformerException
  {

    String formattedMsg = m_XSLMessages.createMessage(msg, args);

    // Locator locator = m_stylesheetLocatorStack.isEmpty()
    //                   ? null :
    //                    ((Locator)m_stylesheetLocatorStack.peek());
    // Locator locator = null;
    ErrorListener errHandler = m_transformer.getErrorListener();

    if (null != errHandler)
      errHandler.fatalError(new TransformerException(formattedMsg, srcLctr));
    else
      throw new TransformerException(formattedMsg, srcLctr);
  }
}
