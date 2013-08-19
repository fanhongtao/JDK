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
package org.apache.xpath;

import java.io.PrintStream;
import java.io.PrintWriter;

import javax.xml.transform.TransformerException;
import org.w3c.dom.Node;

/**
 * <meta name="usage" content="general"/>
 * This class implements an exception object that all
 * XPath classes will throw in case of an error.  This class
 * extends TransformerException, and may hold other exceptions. In the
 * case of nested exceptions, printStackTrace will dump
 * all the traces of the nested exceptions, not just the trace
 * of this object.
 */
public class XPathException extends TransformerException
{

  /** The home of the expression that caused the error.
   *  @serial  */
  Object m_styleNode = null;

  /**
   * Get the stylesheet node from where this error originated.
   * @return The stylesheet node from where this error originated, or null.
   */
  public Object getStylesheetNode()
  {
    return m_styleNode;
  }
  
  /**
   * Set the stylesheet node from where this error originated.
   * @param styleNode The stylesheet node from where this error originated, or null.
   */
  public void setStylesheetNode(Object styleNode)
  {
    m_styleNode = styleNode;
  }


  /** A nested exception.
   *  @serial   */
  protected Exception m_exception;

  /**
   * Create an XPathException object that holds
   * an error message.
   * @param message The error message.
   */
  public XPathException(String message, ExpressionNode ex)
  {
    super(message);
    this.setLocator(ex);
    setStylesheetNode(getStylesheetNode(ex));
  }
  
  /**
   * Create an XPathException object that holds
   * an error message.
   * @param message The error message.
   */
  public XPathException(String message)
  {
    super(message);
  }

  
  /**
   * Get the XSLT ElemVariable that this sub-expression references.  In order for 
   * this to work, the SourceLocator must be the owning ElemTemplateElement.
   * @return The dereference to the ElemVariable, or null if not found.
   */
  public org.w3c.dom.Node getStylesheetNode(ExpressionNode ex)
  {
  	
    ExpressionNode owner = getExpressionOwner(ex);

    if (null != owner && owner instanceof org.w3c.dom.Node)
    {
		return ((org.w3c.dom.Node)owner);
    }
    return null;

  }
  
  /**
   * Get the first non-Expression parent of this node.
   * @return null or first ancestor that is not an Expression.
   */
  protected ExpressionNode getExpressionOwner(ExpressionNode ex)
  {
  	ExpressionNode parent = ex.exprGetParent();
  	while((null != parent) && (parent instanceof Expression))
  		parent = parent.exprGetParent();
  	return parent;
  }



  /**
   * Create an XPathException object that holds
   * an error message and the stylesheet node that
   * the error originated from.
   * @param message The error message.
   * @param styleNode The stylesheet node that the error originated from.
   */
  public XPathException(String message, Object styleNode)
  {

    super(message);

    m_styleNode = styleNode;
  }

  /**
   * Create an XPathException object that holds
   * an error message, the stylesheet node that
   * the error originated from, and another exception
   * that caused this exception.
   * @param message The error message.
   * @param styleNode The stylesheet node that the error originated from.
   * @param e The exception that caused this exception.
   */
  public XPathException(String message, Node styleNode, Exception e)
  {

    super(message);

    m_styleNode = styleNode;
    this.m_exception = e;
  }

  /**
   * Create an XPathException object that holds
   * an error message, and another exception
   * that caused this exception.
   * @param message The error message.
   * @param e The exception that caused this exception.
   */
  public XPathException(String message, Exception e)
  {

    super(message);

    this.m_exception = e;
  }

  /**
   * Print the the trace of methods from where the error
   * originated.  This will trace all nested exception
   * objects, as well as this object.
   * @param s The stream where the dump will be sent to.
   */
  public void printStackTrace(java.io.PrintStream s)
  {

    if (s == null)
      s = System.err;

    try
    {
      super.printStackTrace(s);
    }
    catch (Exception e){}

    Throwable exception = m_exception;

    for (int i = 0; (i < 10) && (null != exception); i++)
    {
      s.println("---------");
      exception.printStackTrace(s);

      if (exception instanceof TransformerException)
      {
        TransformerException se = (TransformerException) exception;
        Throwable prev = exception;

        exception = se.getException();

        if (prev == exception)
          break;
      }
      else
      {
        exception = null;
      }
    }
  }

  /**
   * Find the most contained message.
   *
   * @return The error message of the originating exception.
   */
  public String getMessage()
  {

    String lastMessage = super.getMessage();
    Throwable exception = m_exception;

    while (null != exception)
    {
      String nextMessage = exception.getMessage();

      if (null != nextMessage)
        lastMessage = nextMessage;

      if (exception instanceof TransformerException)
      {
        TransformerException se = (TransformerException) exception;
        Throwable prev = exception;

        exception = se.getException();

        if (prev == exception)
          break;
      }
      else
      {
        exception = null;
      }
    }

    return (null != lastMessage) ? lastMessage : "";
  }

  /**
   * Print the the trace of methods from where the error
   * originated.  This will trace all nested exception
   * objects, as well as this object.
   * @param s The writer where the dump will be sent to.
   */
  public void printStackTrace(java.io.PrintWriter s)
  {

    if (s == null)
      s = new java.io.PrintWriter(System.err);

    try
    {
      super.printStackTrace(s);
    }
    catch (Exception e){}

    Throwable exception = m_exception;

    for (int i = 0; (i < 10) && (null != exception); i++)
    {
      s.println("---------");

      try
      {
        exception.printStackTrace(s);
      }
      catch (Exception e)
      {
        s.println("Could not print stack trace...");
      }

      if (exception instanceof TransformerException)
      {
        TransformerException se = (TransformerException) exception;
        Throwable prev = exception;

        exception = se.getException();

        if (prev == exception)
        {
          exception = null;

          break;
        }
      }
      else
      {
        exception = null;
      }
    }
  }

  /**
   *  Return the embedded exception, if any.
   *  Overrides javax.xml.transform.TransformerException.getException().
   * 
   *  @return The embedded exception, or null if there is none.
   */
  public Throwable getException()
  {
    return m_exception;
  }
}
