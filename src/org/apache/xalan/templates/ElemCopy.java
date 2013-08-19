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

//import org.w3c.dom.*;
import org.apache.xml.dtm.DTM;

import org.xml.sax.*;

import org.apache.xpath.*;

import java.util.*;

import org.apache.xml.utils.QName;
import org.apache.xalan.trace.*;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.ResultTreeHandler;
import org.apache.xalan.transformer.ClonerToResultTree;

import javax.xml.transform.TransformerException;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:copy.
 * <pre>
 * <!ELEMENT xsl:copy %template;>
 * <!ATTLIST xsl:copy
 *   %space-att;
 *   use-attribute-sets %qnames; #IMPLIED
 * >
 * </pre>
 * @see <a href="http://www.w3.org/TR/xslt#copying">copying in XSLT Specification</a>
 */
public class ElemCopy extends ElemUse
{

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for this element 
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_COPY;
  }

  /**
   * Return the node name.
   *
   * @return This element's name
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_COPY_STRING;
  }

  /**
   * The xsl:copy element provides an easy way of copying the current node.
   * Executing this function creates a copy of the current node into the
   * result tree.
   * <p>The namespace nodes of the current node are automatically
   * copied as well, but the attributes and children of the node are not
   * automatically copied. The content of the xsl:copy element is a
   * template for the attributes and children of the created node;
   * the content is instantiated only for nodes of types that can have
   * attributes or children (i.e. root nodes and element nodes).</p>
   * <p>The root node is treated specially because the root node of the
   * result tree is created implicitly. When the current node is the
   * root node, xsl:copy will not create a root node, but will just use
   * the content template.</p>
   *
   * @param transformer non-null reference to the the current transform-time state.
   * @param sourceNode non-null reference to the <a href="http://www.w3.org/TR/xslt#dt-current-node">current source node</a>.
   * @param mode reference, which may be null, to the <a href="http://www.w3.org/TR/xslt#modes">current mode</a>.
   *
   * @throws TransformerException
   */
  public void execute(
          TransformerImpl transformer)
            throws TransformerException
  {
                XPathContext xctxt = transformer.getXPathContext();
      
    try
    {
      int sourceNode = xctxt.getCurrentNode();
      xctxt.pushCurrentNode(sourceNode);
      DTM dtm = xctxt.getDTM(sourceNode);
      short nodeType = dtm.getNodeType(sourceNode);

      if ((DTM.DOCUMENT_NODE != nodeType) && (DTM.DOCUMENT_FRAGMENT_NODE != nodeType))
      {
        ResultTreeHandler rthandler = transformer.getResultTreeHandler();

        if (TransformerImpl.S_DEBUG)
          transformer.getTraceManager().fireTraceEvent(this);
            
        // TODO: Process the use-attribute-sets stuff
        ClonerToResultTree.cloneToResultTree(sourceNode, nodeType, dtm, 
                                             rthandler, false);

        if (DTM.ELEMENT_NODE == nodeType)
        {
          super.execute(transformer);
          rthandler.processNSDecls(sourceNode, nodeType, dtm);
          transformer.executeChildTemplates(this, true);
          
          String ns = dtm.getNamespaceURI(sourceNode);
          String localName = dtm.getLocalName(sourceNode);
          transformer.getResultTreeHandler().endElement(ns, localName,
                                                        dtm.getNodeName(sourceNode));
        }
        if (TransformerImpl.S_DEBUG)
		  transformer.getTraceManager().fireTraceEndEvent(this);         
      }
      else
      {
        if (TransformerImpl.S_DEBUG)
          transformer.getTraceManager().fireTraceEvent(this);

        super.execute(transformer);
        transformer.executeChildTemplates(this, true);

        if (TransformerImpl.S_DEBUG)
          transformer.getTraceManager().fireTraceEndEvent(this);
      }
    }
    catch(org.xml.sax.SAXException se)
    {
      throw new TransformerException(se);
    }
    finally
    {
      xctxt.popCurrentNode();
    }
  }
}
