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

//import org.w3c.dom.Node;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xalan.res.XSLMessages;

import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xml.utils.SAXSourceLocator;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.XMLString;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTM;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.ErrorListener;

/**
 * This abstract class serves as the base for all expression objects.  An
 * Expression can be executed to return a {@link org.apache.xpath.objects.XObject},
 * normally has a location within a document or DOM, can send error and warning
 * events, and normally do not hold state and are meant to be immutable once
 * construction has completed.  An exception to the immutibility rule is iterators
 * and walkers, which must be cloned in order to be used -- the original must
 * still be immutable.
 */
public abstract class Expression implements java.io.Serializable
{

  /**
   * The location where this expression was built from.  Need for diagnostic
   *  messages. May be null.
   *  @serial
   */
  protected SourceLocator m_slocator;

  /**
   * Tell if this expression or it's subexpressions can traverse outside
   * the current subtree.
   *
   * @return true if traversal outside the context node's subtree can occur.
   */
  public boolean canTraverseOutsideSubtree()
  {
    return false;
  }

  /**
   * Set the location where this expression was built from.
   *
   *
   * @param locator the location where this expression was built from, may be
   *                null.
   */
  public void setSourceLocator(SourceLocator locator)
  {
    m_slocator = locator;
  }

  /**
   * Execute an expression in the XPath runtime context, and return the
   * result of the expression.
   *
   *
   * @param xctxt The XPath runtime context.
   * @param currentNode The currentNode.
   *
   * @return The result of the expression in the form of a <code>XObject</code>.
   *
   * @throws javax.xml.transform.TransformerException if a runtime exception
   *         occurs.
   */
  public XObject execute(XPathContext xctxt, int currentNode)
          throws javax.xml.transform.TransformerException
  {

    // For now, the current node is already pushed.
    return execute(xctxt);
  }

  /**
   * Execute an expression in the XPath runtime context, and return the
   * result of the expression.
   *
   *
   * @param xctxt The XPath runtime context.
   * @param currentNode The currentNode.
   * @param dtm The DTM of the current node.
   * @param expType The expanded type ID of the current node.
   *
   * @return The result of the expression in the form of a <code>XObject</code>.
   *
   * @throws javax.xml.transform.TransformerException if a runtime exception
   *         occurs.
   */
  public XObject execute(
          XPathContext xctxt, int currentNode, DTM dtm, int expType)
            throws javax.xml.transform.TransformerException
  {

    // For now, the current node is already pushed.
    return execute(xctxt);
  }

  /**
   * Execute an expression in the XPath runtime context, and return the
   * result of the expression.
   *
   *
   * @param xctxt The XPath runtime context.
   *
   * @return The result of the expression in the form of a <code>XObject</code>.
   *
   * @throws javax.xml.transform.TransformerException if a runtime exception
   *         occurs.
   */
  public abstract XObject execute(XPathContext xctxt)
    throws javax.xml.transform.TransformerException;

  /**
   * Evaluate expression to a number.
   *
   *
   * NEEDSDOC @param xctxt
   * @return 0.0
   *
   * @throws javax.xml.transform.TransformerException
   */
  public double num(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {
    return execute(xctxt).num();
  }

  /**
   * Evaluate expression to a boolean.
   *
   *
   * NEEDSDOC @param xctxt
   * @return false
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean bool(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {
    return execute(xctxt).bool();
  }

  /**
   * Cast result object to a string.
   *
   *
   * NEEDSDOC @param xctxt
   * @return The string this wraps or the empty string if null
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XMLString xstr(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {
    return execute(xctxt).xstr();
  }

  /**
   * Tell if the expression is a nodeset expression.  In other words, tell
   * if you can execute {@link asNode() asNode} without an exception.
   * @return true if the expression can be represented as a nodeset.
   */
  public boolean isNodesetExpr()
  {
    return false;
  }

  /**
   * Return the first node out of the nodeset, if this expression is
   * a nodeset expression.
   * @param xctxt The XPath runtime context.
   * @return the first node out of the nodeset, or DTM.NULL.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public int asNode(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {
    return execute(xctxt).iter().nextNode();
  }

  /**
   * <meta name="usage" content="experimental"/>
   * Given an select expression and a context, evaluate the XPath
   * and return the resulting iterator.
   *
   * @param xctxt The execution context.
   * @param contextNode The node that "." expresses.
   *
   *
   * NEEDSDOC ($objectName$) @return
   * @throws TransformerException thrown if the active ProblemListener decides
   * the error condition is severe enough to halt processing.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public DTMIterator asIterator(XPathContext xctxt, int contextNode)
          throws javax.xml.transform.TransformerException
  {

    try
    {
      xctxt.pushCurrentNodeAndExpression(contextNode, contextNode);

      return execute(xctxt).iter();
    }
    finally
    {
      xctxt.popCurrentNodeAndExpression();
    }
  }

  /**
   * Execute an expression in the XPath runtime context, and return the
   * result of the expression.
   *
   *
   * @param xctxt The XPath runtime context.
   * NEEDSDOC @param handler
   *
   * @return The result of the expression in the form of a <code>XObject</code>.
   *
   * @throws javax.xml.transform.TransformerException if a runtime exception
   *         occurs.
   * @throws org.xml.sax.SAXException
   */
  public void executeCharsToContentHandler(
          XPathContext xctxt, ContentHandler handler)
            throws javax.xml.transform.TransformerException,
                   org.xml.sax.SAXException
  {

    XObject obj = execute(xctxt);

    obj.dispatchCharactersEvents(handler);
  }

  /**
   * Tell if this expression returns a stable number that will not change during 
   * iterations within the expression.  This is used to determine if a proximity 
   * position predicate can indicate that no more searching has to occur.
   * 
   *
   * @return true if the expression represents a stable number.
   */
  public boolean isStableNumber()
  {
    return false;
  }

  /**
   * This function is used to fixup variables from QNames to stack frame
   * indexes at stylesheet build time.
   * @param vars List of QNames that correspond to variables.  This list
   * should be searched backwards for the first qualified name that
   * corresponds to the variable reference qname.  The position of the
   * QName in the vector from the start of the vector will be its position
   * in the stack frame (but variables above the globalsTop value will need
   * to be offset to the current stack frame).
   * NEEDSDOC @param globalsSize
   */
  public abstract void fixupVariables(java.util.Vector vars, int globalsSize);

  /**
   * Warn the user of an problem.
   *
   * @param xctxt The XPath runtime context.
   * @param msg An error number that corresponds to one of the numbers found
   *            in {@link org.apache.xpath.res.XPATHErrorResources}, which is
   *            a key for a format string.
   * @param args An array of arguments represented in the format string, which
   *             may be null.
   *
   * @throws TransformerException if the current ErrorListoner determines to
   *                              throw an exception.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public void warn(XPathContext xctxt, int msg, Object[] args)
          throws javax.xml.transform.TransformerException
  {

    java.lang.String fmsg = XSLMessages.createXPATHWarning(msg, args);

    if (null != xctxt)
    {
      ErrorListener eh = xctxt.getErrorListener();

      // TO DO: Need to get stylesheet Locator from here.
      eh.warning(new TransformerException(fmsg, xctxt.getSAXLocator()));
    }
  }

  /**
   * Tell the user of an assertion error, and probably throw an
   * exception.
   *
   * @param b  If false, a runtime exception will be thrown.
   * @param msg The assertion message, which should be informative.
   *
   * @throws RuntimeException if the b argument is false.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public void assertion(boolean b, java.lang.String msg)
          throws javax.xml.transform.TransformerException
  {

    if (!b)
    {
      java.lang.String fMsg = XSLMessages.createXPATHMessage(
        XPATHErrorResources.ER_INCORRECT_PROGRAMMER_ASSERTION,
        new Object[]{ msg });

      throw new RuntimeException(fMsg);
    }
  }

  /**
   * Tell the user of an error, and probably throw an
   * exception.
   *
   * @param xctxt The XPath runtime context.
   * @param msg An error number that corresponds to one of the numbers found
   *            in {@link org.apache.xpath.res.XPATHErrorResources}, which is
   *            a key for a format string.
   * @param args An array of arguments represented in the format string, which
   *             may be null.
   *
   * @throws TransformerException if the current ErrorListoner determines to
   *                              throw an exception.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public void error(XPathContext xctxt, int msg, Object[] args)
          throws javax.xml.transform.TransformerException
  {

    java.lang.String fmsg = XSLMessages.createXPATHMessage(msg, args);

    if (null != xctxt)
    {
      ErrorListener eh = xctxt.getErrorListener();
      TransformerException te = new TransformerException(fmsg, m_slocator);

      eh.fatalError(te);
    }
  }
}
