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

import org.w3c.dom.Node;
import org.apache.xml.dtm.DTM;

import org.xml.sax.*;

import org.apache.xml.dtm.ref.DTMTreeWalker;
import org.apache.xml.utils.MutableAttrListImpl;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xpath.DOMHelper;
import org.apache.xpath.XPathContext;

/**
 * <meta name="usage" content="internal"/>
 * Handle a walk of a tree, but screen out attributes for
 * the result tree.
 */
public class TreeWalker2Result extends DTMTreeWalker
{

  /** The transformer instance          */
  TransformerImpl m_transformer;

  /** The result tree handler          */
  ResultTreeHandler m_handler;

  /** Node where to start the tree walk           */
  int m_startNode;

  /**
   * Constructor.
   *
   * @param transformer Non-null transformer instance
   * @param handler The Result tree handler to use
   */
  public TreeWalker2Result(TransformerImpl transformer,
                           ResultTreeHandler handler)
  {

    super(handler, null);

    m_transformer = transformer;
    m_handler = handler;
  }

  /**
   * Perform a pre-order traversal non-recursive style.
   *
   * @param pos Start node for traversal
   *
   * @throws TransformerException
   */
  public void traverse(int pos) throws org.xml.sax.SAXException
  {
    m_dtm = m_transformer.getXPathContext().getDTM(pos);
    m_startNode = pos;

    super.traverse(pos);
  }
        
        /**
   * End processing of given node 
   *
   *
   * @param node Node we just finished processing
   *
   * @throws org.xml.sax.SAXException
   */
  protected void endNode(int node) throws org.xml.sax.SAXException
  {
    super.endNode(node);
    if(DTM.ELEMENT_NODE == m_dtm.getNodeType(node))
    {
      m_transformer.getXPathContext().popCurrentNode();
    }
  }

  /**
   * Start traversal of the tree at the given node
   *
   *
   * @param node Starting node for traversal
   *
   * @throws TransformerException
   */
  protected void startNode(int node) throws org.xml.sax.SAXException
  {

    XPathContext xcntxt = m_transformer.getXPathContext();
    try
    {
      
      if (DTM.ELEMENT_NODE == m_dtm.getNodeType(node))
      {
        xcntxt.pushCurrentNode(node);                   
                                        
        if(m_startNode != node)
        {
          super.startNode(node);
        }
        else
        {
          String elemName = m_dtm.getNodeName(node);
          String localName = m_dtm.getLocalName(node);
          String namespace = m_dtm.getNamespaceURI(node);
                                        
          //xcntxt.pushCurrentNode(node);       
          m_handler.startElement(namespace, localName, elemName, null);

          boolean hasNSDecls = false;
          DTM dtm = m_dtm;
          for (int ns = dtm.getFirstNamespaceNode(node, true); 
               DTM.NULL != ns; ns = dtm.getNextNamespaceNode(node, ns, true))
          {
            m_handler.ensureNamespaceDeclDeclared(dtm, ns);
          }
                                                
          // %REVIEW% This flag is apparently never set true. Is that
          // a bug, or should this code be phased out?
          if(hasNSDecls)
          {
            m_handler.addNSDeclsToAttrs();
          }
                                                
          for (int attr = dtm.getFirstAttribute(node); 
               DTM.NULL != attr; attr = dtm.getNextAttribute(attr))
          {
            m_handler.addAttribute(attr);
          }
        }
                                
      }
      else
      {
        xcntxt.pushCurrentNode(node);
        super.startNode(node);
        xcntxt.popCurrentNode();
      }
    }
    catch(javax.xml.transform.TransformerException te)
    {
      throw new org.xml.sax.SAXException(te);
    }
  }
}
