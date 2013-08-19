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
package org.apache.xalan.templates;

import org.apache.xpath.*;
import org.apache.xpath.Expression;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.XPathContext;
import org.apache.xpath.compiler.XPathParser;
import org.apache.xml.utils.FastStringBuffer;
import org.apache.xpath.ExpressionOwner;

//import org.w3c.dom.*;
import org.apache.xml.dtm.DTM;

/**
 * <meta name="usage" content="internal"/>
 * Simple string part of a complex AVT.
 */
public class AVTPartXPath extends AVTPart
{

  /**
   * The XPath object contained in this part.
   * @serial
   */
  private XPath m_xpath;
  
  /**
   * This function is used to fixup variables from QNames to stack frame 
   * indexes at stylesheet build time.
   * @param vars List of QNames that correspond to variables.  This list 
   * should be searched backwards for the first qualified name that 
   * corresponds to the variable reference qname.  The position of the 
   * QName in the vector from the start of the vector will be its position 
   * in the stack frame (but variables above the globalsTop value will need 
   * to be offset to the current stack frame).
   */
  public void fixupVariables(java.util.Vector vars, int globalsSize)
  {
    m_xpath.fixupVariables(vars, globalsSize);
  }
  
  /**
   * Tell if this expression or it's subexpressions can traverse outside 
   * the current subtree.
   * 
   * @return true if traversal outside the context node's subtree can occur.
   */
   public boolean canTraverseOutsideSubtree()
   {
    return m_xpath.getExpression().canTraverseOutsideSubtree();
   }

  /**
   * Construct a simple AVT part.
   *
   * @param xpath Xpath section of AVT 
   */
  public AVTPartXPath(XPath xpath)
  {
    m_xpath = xpath;
  }

  /**
   * Construct a simple AVT part.
   * 
   * @param val A pure string section of an AVT.
   * @param nsNode An object which can be used to determine the
   * Namespace Name (URI) for any Namespace prefix used in the XPath. 
   * Usually this is based on the context where the XPath was specified,
   * such as a node within a Stylesheet.
   * @param xpathProcessor XPath parser
   * @param factory XPath factory
   * @param liaison An XPathContext object, providing infomation specific
   * to this invocation and this thread. Maintains SAX output state, 
   * variables, error handler and so on, so the transformation/XPath 
   * object itself can be simultaneously invoked from multiple threads.
   *
   * @throws javax.xml.transform.TransformerException
   * TODO: Fix or remove this unused c'tor.
   */
  public AVTPartXPath(
          String val, org.apache.xml.utils.PrefixResolver nsNode, 
          XPathParser xpathProcessor, XPathFactory factory, 
          XPathContext liaison)
            throws javax.xml.transform.TransformerException
  {
    m_xpath = new XPath(val, null, nsNode, XPath.SELECT, liaison.getErrorListener());
  }

  /**
   * Get the AVT part as the original string.
   *
   * @return the AVT part as the original string.
   */
  public String getSimpleString()
  {
    return "{" + m_xpath.getPatternString() + "}";
  }

  /**
   * Write the value into the buffer.
   *
   * @param xctxt An XPathContext object, providing infomation specific
   * to this invocation and this thread. Maintains SAX state, variables, 
   * error handler and  so on, so the transformation/XPath object itself
   * can be simultaneously invoked from multiple threads.
   * @param buf Buffer to write into.
   * @param context The current source tree context.
   * @param nsNode The current namespace context (stylesheet tree context).
   * @param NodeList The current Context Node List.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public void evaluate(
          XPathContext xctxt, FastStringBuffer buf, int context, org.apache.xml.utils.PrefixResolver nsNode)
            throws javax.xml.transform.TransformerException
  {

    XObject xobj = m_xpath.execute(xctxt, context, nsNode);

    if (null != xobj)
    {
      xobj.appendToFsb(buf);
    }
  }
  
  /**
   * @see XSLTVisitable#callVisitors(XSLTVisitor)
   */
  public void callVisitors(XSLTVisitor visitor)
  {
  	m_xpath.getExpression().callVisitors(m_xpath, visitor);
  }
}
