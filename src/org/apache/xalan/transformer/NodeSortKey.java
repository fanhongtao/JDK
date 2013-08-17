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

import java.util.Locale;

import org.apache.xpath.XPath;

import java.text.Collator;

import org.apache.xalan.res.XSLTErrorResources;

/**
 * <meta name="usage" content="internal"/>
 * Data structure for use by the NodeSorter class.
 */
class NodeSortKey
{

  /** Select pattern for this sort key          */
  XPath m_selectPat;

  /** Flag indicating whether to treat thee result as a number     */
  boolean m_treatAsNumbers;

  /** Flag indicating whether to sort in descending order      */
  boolean m_descending;

  /** Flag indicating by case          */
  boolean m_caseOrderUpper;

  /** Collator instance          */
  Collator m_col;

  /** Locale we're in          */
  Locale m_locale;

  /** Prefix resolver to use          */
  org.apache.xml.utils.PrefixResolver m_namespaceContext;

  /** Transformer instance          */
  TransformerImpl m_processor;  // needed for error reporting.

  /**
   * Constructor NodeSortKey
   *
   *
   * @param transformer non null transformer instance
   * @param selectPat Select pattern for this key 
   * @param treatAsNumbers Flag indicating whether the result will be a number
   * @param descending Flag indicating whether to sort in descending order
   * @param langValue Lang value to use to get locale
   * @param caseOrderUpper Flag indicating whether case is relevant
   * @param namespaceContext Prefix resolver
   *
   * @throws javax.xml.transform.TransformerException
   */
  NodeSortKey(
          TransformerImpl transformer, XPath selectPat, boolean treatAsNumbers, 
          boolean descending, String langValue, boolean caseOrderUpper, 
          org.apache.xml.utils.PrefixResolver namespaceContext)
            throws javax.xml.transform.TransformerException
  {

    m_processor = transformer;
    m_namespaceContext = namespaceContext;
    m_selectPat = selectPat;
    m_treatAsNumbers = treatAsNumbers;
    m_descending = descending;
    m_caseOrderUpper = caseOrderUpper;

    if (null != langValue && m_treatAsNumbers == false)
    {
      // See http://nagoya.apache.org/bugzilla/show_bug.cgi?id=2851
      // The constructor of Locale is defined as 
      //   public Locale(String language, String country)
      // with
      //   language - lowercase two-letter ISO-639 code
      //   country - uppercase two-letter ISO-3166 code
      // a) language must be provided as a lower-case ISO-code 
      //    instead of an upper-case code
      // b) country must be provided as an ISO-code 
      //    instead of a full localized country name (e.g. "France")
      m_locale = new Locale(langValue.toLowerCase(), 
                  Locale.getDefault().getCountry());
                  
      // (old, before bug report 2851).
      //  m_locale = new Locale(langValue.toUpperCase(),
      //                        Locale.getDefault().getDisplayCountry());                    

      if (null == m_locale)
      {

        // m_processor.warn("Could not find locale for <sort xml:lang="+langValue);
        m_locale = Locale.getDefault();
      }
    }
    else
    {
      m_locale = Locale.getDefault();
    }

    m_col = Collator.getInstance(m_locale);

    if (null == m_col)
    {
      m_processor.getMsgMgr().warn(null, XSLTErrorResources.WG_CANNOT_FIND_COLLATOR,
                                   new Object[]{ langValue });  //"Could not find Collator for <sort xml:lang="+langValue);

      m_col = Collator.getInstance();
    }
  }
}
