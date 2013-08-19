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

import org.apache.xalan.templates.Stylesheet;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMFilter;
import org.apache.xml.utils.XMLString;

import javax.xml.transform.TransformerException;
import org.xml.sax.Attributes;

import org.apache.xpath.XPathContext;
import org.apache.xalan.res.XSLTErrorResources;

/**
 * <meta name="usage" content="internal"/>
 * Class used to clone a node, possibly including its children to 
 * a result tree.
 */
public class ClonerToResultTree
{

  /** Result tree handler for the cloned tree           */
  private ResultTreeHandler m_rth;

  /** Transformer instance to use for cloning          */
  private TransformerImpl m_transformer;

  /**
   * Constructor ClonerToResultTree
   *
   *
   * @param transformer non-null transformer instance to use for the cloning
   * @param rth non-null result tree handler for the cloned tree
   */
  public ClonerToResultTree(TransformerImpl transformer,
                            ResultTreeHandler rth)
  {
    m_rth = rth;
    m_transformer = transformer;
  }

//  /**
//   * Clone an element with or without children.
//   * TODO: Fix or figure out node clone failure!
//   * the error condition is severe enough to halt processing.
//   *
//   * @param node The node to clone
//   * @param shouldCloneAttributes Flag indicating whether to 
//   * clone children attributes
//   * 
//   * @throws TransformerException
//   */
//  public void cloneToResultTree(int node, boolean shouldCloneAttributes)
//    throws TransformerException
//  {
//
//    try
//    {
//      XPathContext xctxt = m_transformer.getXPathContext();
//      DTM dtm = xctxt.getDTM(node);
//
//      int type = dtm.getNodeType(node);
//      switch (type)
//      {
//      case DTM.TEXT_NODE :
//        dtm.dispatchCharactersEvents(node, m_rth, false);
//        break;
//      case DTM.DOCUMENT_FRAGMENT_NODE :
//      case DTM.DOCUMENT_NODE :
//
//        // Can't clone a document, but refrain from throwing an error
//        // so that copy-of will work
//        break;
//      case DTM.ELEMENT_NODE :
//        {
//          Attributes atts;
//
//          if (shouldCloneAttributes)
//          {
//            m_rth.addAttributes(node);
//            m_rth.processNSDecls(node, type, dtm);
//          }
//
//          String ns = dtm.getNamespaceURI(node);
//          String localName = dtm.getLocalName(node);
//
//          m_rth.startElement(ns, localName, dtm.getNodeNameX(node), null);
//        }
//        break;
//      case DTM.CDATA_SECTION_NODE :
//        m_rth.startCDATA();          
//        dtm.dispatchCharactersEvents(node, m_rth, false);
//        m_rth.endCDATA();
//        break;
//      case DTM.ATTRIBUTE_NODE :
//        m_rth.addAttribute(node);
//        break;
//      case DTM.COMMENT_NODE :
//        XMLString xstr = dtm.getStringValue (node);
//        xstr.dispatchAsComment(m_rth);
//        break;
//      case DTM.ENTITY_REFERENCE_NODE :
//        m_rth.entityReference(dtm.getNodeNameX(node));
//        break;
//      case DTM.PROCESSING_INSTRUCTION_NODE :
//        {
//          // %REVIEW% Is the node name the same as the "target"?
//          m_rth.processingInstruction(dtm.getNodeNameX(node), 
//                                      dtm.getNodeValue(node));
//        }
//        break;
//      default :
//        //"Can not create item in result tree: "+node.getNodeName());
//        m_transformer.getMsgMgr().error(null, 
//                         XSLTErrorResources.ER_CANT_CREATE_ITEM,
//                         new Object[]{ dtm.getNodeName(node) });  
//      }
//    }
//    catch(org.xml.sax.SAXException se)
//    {
//      throw new TransformerException(se);
//    }
//  }  // end cloneToResultTree function
  
  /**
   * Clone an element with or without children.
   * TODO: Fix or figure out node clone failure!
   * the error condition is severe enough to halt processing.
   *
   * @param node The node to clone
   * @param shouldCloneAttributes Flag indicating whether to 
   * clone children attributes
   * 
   * @throws TransformerException
   */
  public static void cloneToResultTree(int node, int nodeType, DTM dtm, 
                                             ResultTreeHandler rth,
                                             boolean shouldCloneAttributes)
    throws TransformerException
  {

    try
    {
      switch (nodeType)
      {
      case DTM.TEXT_NODE :
        dtm.dispatchCharactersEvents(node, rth, false);
        break;
      case DTM.DOCUMENT_FRAGMENT_NODE :
      case DTM.DOCUMENT_NODE :
        // Can't clone a document, but refrain from throwing an error
        // so that copy-of will work
        break;
      case DTM.ELEMENT_NODE :
        {
          // Note: SAX apparently expects "no namespace" to be
          // represented as "" rather than null.
          String ns = dtm.getNamespaceURI(node);
          if (ns==null) ns="";
          String localName = dtm.getLocalName(node);
          rth.startElement(ns, localName, dtm.getNodeNameX(node), null);
          
	  // If outputting attrs as separate events, they must
	  // _follow_ the startElement event. (Think of the
	  // xsl:attribute directive.)
          if (shouldCloneAttributes)
          {
            rth.addAttributes(node);
            rth.processNSDecls(node, nodeType, dtm);
          }
        }
        break;
      case DTM.CDATA_SECTION_NODE :
        rth.startCDATA();          
        dtm.dispatchCharactersEvents(node, rth, false);
        rth.endCDATA();
        break;
      case DTM.ATTRIBUTE_NODE :
        rth.addAttribute(node);
        break;
			case DTM.NAMESPACE_NODE:
				// %REVIEW% Normally, these should have been handled with element.
				// It's possible that someone may write a stylesheet that tries to
				// clone them explicitly. If so, we need the equivalent of
				// rth.addAttribute().
  			rth.processNSDecls(node,DTM.NAMESPACE_NODE,dtm);
				break;
      case DTM.COMMENT_NODE :
        XMLString xstr = dtm.getStringValue (node);
        xstr.dispatchAsComment(rth);
        break;
      case DTM.ENTITY_REFERENCE_NODE :
        rth.entityReference(dtm.getNodeNameX(node));
        break;
      case DTM.PROCESSING_INSTRUCTION_NODE :
        {
          // %REVIEW% Is the node name the same as the "target"?
          rth.processingInstruction(dtm.getNodeNameX(node), 
                                      dtm.getNodeValue(node));
        }
        break;
      default :
        //"Can not create item in result tree: "+node.getNodeName());
        throw new  TransformerException(
                         "Can't clone node: "+dtm.getNodeName(node));
      }
    }
    catch(org.xml.sax.SAXException se)
    {
      throw new TransformerException(se);
    }
  }  // end cloneToResultTree function
}
