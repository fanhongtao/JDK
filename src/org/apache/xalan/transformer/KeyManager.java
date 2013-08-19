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

import java.util.Vector;

//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
import org.apache.xml.dtm.DTM;

import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.objects.XNodeSet;

/**
 * This class manages the key tables.
 */
public class KeyManager
{

  /**
   * Table of tables of element keys.
   * @see org.apache.xalan.transformer.KeyTable
   */
  private transient Vector m_key_tables = null;

  /**
   * Given a valid element key, return the corresponding node list.
   *
   * @param xctxt The XPath runtime state
   * @param doc The document node
   * @param name The key element name
   * @param ref The key value we're looking for 
   * @param nscontext The prefix resolver for the execution context
   *
   * @return A nodelist of nodes mathing the given key
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XNodeSet getNodeSetDTMByKey(
          XPathContext xctxt, int doc, QName name, XMLString ref, PrefixResolver nscontext)
            throws javax.xml.transform.TransformerException
  {

    XNodeSet nl = null;
    ElemTemplateElement template = (ElemTemplateElement) nscontext;  // yuck -sb

    if ((null != template)
            && null != template.getStylesheetRoot().getKeysComposed())
    {
      boolean foundDoc = false;

      if (null == m_key_tables)
      {
        m_key_tables = new Vector(4);
      }
      else
      {
        int nKeyTables = m_key_tables.size();

        for (int i = 0; i < nKeyTables; i++)
        {
          KeyTable kt = (KeyTable) m_key_tables.elementAt(i);

          if (kt.getKeyTableName().equals(name) && doc == kt.getDocKey())
          {
            nl = kt.getNodeSetDTMByKey(name, ref);

            if (nl != null)
            {
              foundDoc = true;

              break;
            }
          }
        }
      }

      if ((null == nl) &&!foundDoc /* && m_needToBuildKeysTable */)
      {
        KeyTable kt =
          new KeyTable(doc, nscontext, name,
                       template.getStylesheetRoot().getKeysComposed(),
                       xctxt);

        m_key_tables.addElement(kt);

        if (doc == kt.getDocKey())
        {
          foundDoc = true;
          nl = kt.getNodeSetDTMByKey(name, ref);
        }
      }
    }

    return nl;
  }
}
