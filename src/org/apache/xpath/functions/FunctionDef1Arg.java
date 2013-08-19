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
package org.apache.xpath.functions;

//import org.w3c.dom.Node;

import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XString;
import org.apache.xalan.res.XSLMessages;
import org.apache.xpath.res.XPATHErrorResources;

import org.apache.xml.utils.XMLString;

import org.apache.xml.dtm.DTM;

/**
 * <meta name="usage" content="advanced"/>
 * Base class for functions that accept one argument that can be defaulted if
 * not specified.
 */
public class FunctionDef1Arg extends FunctionOneArg
{

  /**
   * Execute the first argument expression that is expected to return a
   * nodeset.  If the argument is null, then return the current context node.
   *
   * @param xctxt Runtime XPath context.
   *
   * @return The first node of the executed nodeset, or the current context
   *         node if the first argument is null.
   *
   * @throws javax.xml.transform.TransformerException if an error occurs while
   *                                   executing the argument expression.
   */
  protected int getArg0AsNode(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {

    return (null == m_arg0)
           ? xctxt.getCurrentNode() : m_arg0.asNode(xctxt);
  }
  
  /**
   * Tell if the expression is a nodeset expression.
   * @return true if the expression can be represented as a nodeset.
   */
  public boolean Arg0IsNodesetExpr()
  {
    return (null == m_arg0) ? true : m_arg0.isNodesetExpr();
  }

  /**
   * Execute the first argument expression that is expected to return a
   * string.  If the argument is null, then get the string value from the
   * current context node.
   *
   * @param xctxt Runtime XPath context.
   *
   * @return The string value of the first argument, or the string value of the
   *         current context node if the first argument is null.
   *
   * @throws javax.xml.transform.TransformerException if an error occurs while
   *                                   executing the argument expression.
   */
  protected XMLString getArg0AsString(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {
    if(null == m_arg0)
    {
      int currentNode = xctxt.getCurrentNode();
      if(DTM.NULL == currentNode)
        return XString.EMPTYSTRING;
      else
      {
        DTM dtm = xctxt.getDTM(currentNode);
        return dtm.getStringValue(currentNode);
      }
      
    }
    else
      return m_arg0.execute(xctxt).xstr();   
  }

  /**
   * Execute the first argument expression that is expected to return a
   * number.  If the argument is null, then get the number value from the
   * current context node.
   *
   * @param xctxt Runtime XPath context.
   *
   * @return The number value of the first argument, or the number value of the
   *         current context node if the first argument is null.
   *
   * @throws javax.xml.transform.TransformerException if an error occurs while
   *                                   executing the argument expression.
   */
  protected double getArg0AsNumber(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {

    if(null == m_arg0)
    {
      int currentNode = xctxt.getCurrentNode();
      if(DTM.NULL == currentNode)
        return 0;
      else
      {
        DTM dtm = xctxt.getDTM(currentNode);
        XMLString str = dtm.getStringValue(currentNode);
        return str.toDouble();
      }
      
    }
    else
      return m_arg0.execute(xctxt).num();
  }

  /**
   * Check that the number of arguments passed to this function is correct.
   *
   * @param argNum The number of arguments that is being passed to the function.
   *
   * @throws WrongNumberArgsException if the number of arguments is not 0 or 1.
   */
  public void checkNumberArgs(int argNum) throws WrongNumberArgsException
  {
    if (argNum > 1)
      reportWrongNumberArgs();
  }

  /**
   * Constructs and throws a WrongNumberArgException with the appropriate
   * message for this function object.
   *
   * @throws WrongNumberArgsException
   */
  protected void reportWrongNumberArgs() throws WrongNumberArgsException {
      throw new WrongNumberArgsException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_ZERO_OR_ONE, null)); //"0 or 1");
  }

  /**
   * Tell if this expression or it's subexpressions can traverse outside
   * the current subtree.
   *
   * @return true if traversal outside the context node's subtree can occur.
   */
  public boolean canTraverseOutsideSubtree()
  {
    return (null == m_arg0) ? false : super.canTraverseOutsideSubtree();
  }
}
