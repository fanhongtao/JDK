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

//import org.w3c.dom.Node;
//import org.w3c.dom.DOMException;
import org.apache.xml.dtm.DTM;

import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.utils.QName;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:apply-imports.
 * <pre>
 * <!ELEMENT xsl:apply-imports EMPTY>
 * </pre>
 * @see <a href="http://www.w3.org/TR/xslt#apply-imports">apply-imports in XSLT Specification</a>
 */
public class ElemApplyImport extends ElemTemplateElement
{

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return Token ID for xsl:apply-imports element types
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_APPLY_IMPORTS;
  }

  /**
   * Return the node name.
   *
   * @return Element name
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_APPLY_IMPORTS_STRING;
  }

  /**
   * Execute the xsl:apply-imports transformation.
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

    if (transformer.currentTemplateRuleIsNull())
    {
      transformer.getMsgMgr().error(this,
        XSLTErrorResources.ER_NO_APPLY_IMPORT_IN_FOR_EACH);  //"xsl:apply-imports not allowed in a xsl:for-each");
    }

    if (TransformerImpl.S_DEBUG)
      transformer.getTraceManager().fireTraceEvent(this);

    int sourceNode = transformer.getXPathContext().getCurrentNode();
    if (DTM.NULL != sourceNode)
    {

      // This will have to change to current template, (which will have 
      // to be the top of a current template stack).
      transformer.applyTemplateToNode(this, null, sourceNode);
    }
    else  // if(null == sourceNode)
    {
      transformer.getMsgMgr().error(this,
        XSLTErrorResources.ER_NULL_SOURCENODE_APPLYIMPORTS);  //"sourceNode is null in xsl:apply-imports!");
    }
    if (TransformerImpl.S_DEBUG)
      transformer.getTraceManager().fireTraceEndEvent(this);
  }

  /**
   * Add a child to the child list.
   * <!ELEMENT xsl:apply-imports EMPTY>
   *
   * @param newChild New element to append to this element's children list
   *
   * @return null, xsl:apply-Imports cannot have children 
   */
  public ElemTemplateElement appendChild(ElemTemplateElement newChild)
  {

    error(XSLTErrorResources.ER_CANNOT_ADD,
          new Object[]{ newChild.getNodeName(),
                        this.getNodeName() });  //"Can not add " +((ElemTemplateElement)newChild).m_elemName +

    //" to " + this.m_elemName);
    return null;
  }
}
