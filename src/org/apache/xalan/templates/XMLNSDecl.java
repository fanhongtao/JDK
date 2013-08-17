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
 * Represents an xmlns declaration
 */
public class XMLNSDecl
        implements java.io.Serializable // 20001009 jkess
{

  /**
   * Constructor XMLNSDecl
   *
   * @param prefix non-null reference to prefix, using "" for default namespace.
   * @param uri non-null reference to namespace URI.
   * @param isExcluded true if this namespace declaration should normally be excluded.
   */
  public XMLNSDecl(String prefix, String uri, boolean isExcluded)
  {

    m_prefix = prefix;
    m_uri = uri;
    m_isExcluded = isExcluded;
  }

  /** non-null reference to prefix, using "" for default namespace.
   *  @serial */
  private String m_prefix;

  /**
   * Return the prefix.
   * @return The prefix that is associated with this URI, or null
   * if the XMLNSDecl is declaring the default namespace.
   */
  public String getPrefix()
  {
    return m_prefix;
  }

  /** non-null reference to namespace URI.
   *  @serial  */
  private String m_uri;

  /**
   * Return the URI.
   * @return The URI that is associated with this declaration.
   */
  public String getURI()
  {
    return m_uri;
  }

  /** true if this namespace declaration should normally be excluded.
   *  @serial  */
  private boolean m_isExcluded;

  /**
   * Tell if this declaration should be excluded from the
   * result namespace.
   *
   * @return true if this namespace declaration should normally be excluded.
   */
  public boolean getIsExcluded()
  {
    return m_isExcluded;
  }
}
