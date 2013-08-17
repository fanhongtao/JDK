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

import org.apache.xpath.XPath;
import org.apache.xml.utils.QName;

/**
 * <meta name="usage" content="internal"/>
 * Holds the attribute declarations for the xsl:keys element.
 * A stylesheet declares a set of keys for each document using
 * the xsl:key element. When this set of keys contains a member
 * with node x, name y and value z, we say that node x has a key
 * with name y and value z.
 * @see <a href="http://www.w3.org/TR/xslt#key">key in XSLT Specification</a>
 */
public class KeyDeclaration extends ElemTemplateElement
{

  /**
   * Constructs a new element representing the xsl:key.  The parameters
   * are needed to prioritize this key element as part of the recomposing
   * process.  For this element, they are not automatically created
   * because the element is never added on to the stylesheet parent.
   */
  public KeyDeclaration(Stylesheet parentNode, int docOrderNumber)
  {
    m_parentNode = parentNode;
    setUid(docOrderNumber);
  }

  /**
   * The "name" property.
   * @serial
   */
  private QName m_name;

  /**
   * Set the "name" attribute.
   * The name attribute specifies the name of the key. The value
   * of the name attribute is a QName, which is expanded as
   * described in [2.4 Qualified Names].
   *
   * @param name Value to set for the "name" attribute.
   */
  public void setName(QName name)
  {
    m_name = name;
  }

  /**
   * Get the "name" attribute.
   * The name attribute specifies the name of the key. The value
   * of the name attribute is a QName, which is expanded as
   * described in [2.4 Qualified Names].
   *
   * @return Value of the "name" attribute.
   */
  public QName getName()
  {
    return m_name;
  }

  /**
   * The "match" attribute.
   * @serial
   */
  private XPath m_matchPattern = null;

  /**
   * Set the "match" attribute.
   * The match attribute is a Pattern; an xsl:key element gives
   * information about the keys of any node that matches the
   * pattern specified in the match attribute.
   * @see <a href="http://www.w3.org/TR/xslt#patterns">patterns in XSLT Specification</a>
   *
   * @param v Value to set for the "match" attribute.
   */
  public void setMatch(XPath v)
  {
    m_matchPattern = v;
  }

  /**
   * Get the "match" attribute.
   * The match attribute is a Pattern; an xsl:key element gives
   * information about the keys of any node that matches the
   * pattern specified in the match attribute.
   * @see <a href="http://www.w3.org/TR/xslt#patterns">patterns in XSLT Specification</a>
   *
   * @return Value of the "match" attribute.
   */
  public XPath getMatch()
  {
    return m_matchPattern;
  }

  /**
   * The "use" attribute.
   * @serial
   */
  private XPath m_use;

  /**
   * Set the "use" attribute.
   * The use attribute is an expression specifying the values
   * of the key; the expression is evaluated once for each node
   * that matches the pattern.
   *
   * @param v Value to set for the "use" attribute.
   */
  public void setUse(XPath v)
  {
    m_use = v;
  }

  /**
   * Get the "use" attribute.
   * The use attribute is an expression specifying the values
   * of the key; the expression is evaluated once for each node
   * that matches the pattern.
   *
   * @return Value of the "use" attribute.
   */
  public XPath getUse()
  {
    return m_use;
  }
  
  /**
   * This function is called after everything else has been
   * recomposed, and allows the template to set remaining
   * values that may be based on some other property that
   * depends on recomposition.
   */
  public void compose(StylesheetRoot sroot) 
    throws javax.xml.transform.TransformerException
  {
    super.compose(sroot);
    java.util.Vector vnames = sroot.getComposeState().getVariableNames();
    if(null != m_matchPattern)
      m_matchPattern.fixupVariables(vnames, sroot.getComposeState().getGlobalsSize());
    if(null != m_use)
      m_use.fixupVariables(vnames, sroot.getComposeState().getGlobalsSize());
  }

  /**
   * This function is called during recomposition to
   * control how this element is composed.
   * @param root The root stylesheet for this transformation.
   */
  public void recompose(StylesheetRoot root)
  {
    root.recomposeKeys(this);
  }

}
