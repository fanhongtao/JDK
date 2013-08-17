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

/**
 * Object to hold an xsl:namespace element.
 * A stylesheet can use the xsl:namespace-alias element to declare
 * that one namespace URI is an alias for another namespace URI.
 * @see <a href="http://www.w3.org/TR/xslt#literal-result-element">literal-result-element in XSLT Specification</a>
 */
public class NamespaceAlias extends ElemTemplateElement
{
  
  /**
   * Constructor NamespaceAlias
   * 
   * @param docOrderNumber The document order number
   *
   */
  public NamespaceAlias(int docOrderNumber)
  {
    super();
    m_docOrderNumber = docOrderNumber;
  }

  /**
   * The "stylesheet-prefix" attribute.
   * @serial
   */
  private String m_StylesheetPrefix;

  /**
   * Set the "stylesheet-prefix" attribute.
   *
   * @param v non-null prefix value.
   */
  public void setStylesheetPrefix(String v)
  {
    m_StylesheetPrefix = v;
  }

  /**
   * Get the "stylesheet-prefix" attribute.
   *
   * @return non-null prefix value.
   */
  public String getStylesheetPrefix()
  {
    return m_StylesheetPrefix;
  }
  
  /**
   * The namespace in the stylesheet space.
   * @serial
   */
  private String m_StylesheetNamespace;

  /**
   * Set the value for the stylesheet namespace.
   *
   * @param v non-null prefix value.
   */
  public void setStylesheetNamespace(String v)
  {
    m_StylesheetNamespace = v;
  }

  /**
   * Get the value for the stylesheet namespace.
   *
   * @return non-null prefix value.
   */
  public String getStylesheetNamespace()
  {
    return m_StylesheetNamespace;
  }

  /**
   * The "result-prefix" attribute.
   * @serial
   */
  private String m_ResultPrefix;

  /**
   * Set the "result-prefix" attribute.
   *
   * @param v non-null prefix value.
   */
  public void setResultPrefix(String v)
  {
    m_ResultPrefix = v;
  }

  /**
   * Get the "result-prefix" attribute.
   *
   * @return non-null prefix value.
   */
  public String getResultPrefix()
  {
    return m_ResultPrefix;
  }
  
  /**
   * The result namespace.
   * @serial
   */
  private String m_ResultNamespace;

  /**
   * Set the result namespace.
   *
   * @param v non-null namespace value
   */
  public void setResultNamespace(String v)
  {
    m_ResultNamespace = v;
  }

  /**
   * Get the result namespace value.
   *
   * @return non-null namespace value.
   */
  public String getResultNamespace()
  {
    return m_ResultNamespace;
  }

  /**
   * This function is called to recompose() all of the namespace alias properties elements.
   * 
   * @param root The owning root stylesheet
   */
  public void recompose(StylesheetRoot root)
  {
    root.recomposeNamespaceAliases(this);
  }

}
