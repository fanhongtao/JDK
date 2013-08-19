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

import org.w3c.dom.*;

import org.xml.sax.*;

import org.apache.xpath.*;
import org.apache.xpath.objects.XString;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XRTreeFrag;
import org.apache.xml.utils.QName;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;
import javax.xml.transform.TransformerException;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:with-param.  xsl:with-param is allowed within
 * both xsl:call-template and xsl:apply-templates.
 * <pre>
 * <!ELEMENT xsl:with-param %template;>
 * <!ATTLIST xsl:with-param
 *   name %qname; #REQUIRED
 *   select %expr; #IMPLIED
 * >
 * </pre>
 * @see <a href="http://www.w3.org/TR/xslt#element-with-param">element-with-param in XSLT Specification</a>
 */
public class ElemWithParam extends ElemTemplateElement
{
  /**
   * This is the index to the stack frame being called, <emph>not</emph> the 
   * stack frame that contains this element.
   */
  int m_index;

  /**
   * The "select" attribute, which specifies the value of the
   * argument, if element content is not specified.
   * @serial
   */
  private XPath m_selectPattern = null;

  /**
   * Set the "select" attribute.
   * The "select" attribute specifies the value of the
   * argument, if element content is not specified.
   *
   * @param v Value to set for the "select" attribute. 
   */
  public void setSelect(XPath v)
  {
    m_selectPattern = v;
  }

  /**
   * Get the "select" attribute.
   * The "select" attribute specifies the value of the
   * argument, if element content is not specified.
   *
   * @return Value of the "select" attribute. 
   */
  public XPath getSelect()
  {
    return m_selectPattern;
  }

  /**
   * The required name attribute specifies the name of the
   * parameter (the variable the value of whose binding is
   * to be replaced). The value of the name attribute is a QName,
   * which is expanded as described in [2.4 Qualified Names].
   * @serial
   */
  private QName m_qname = null;
  
  int m_qnameID;

  /**
   * Set the "name" attribute.
   * DJD
   *
   * @param v Value to set for the "name" attribute.
   */
  public void setName(QName v)
  {
    m_qname = v;
  }

  /**
   * Get the "name" attribute.
   * DJD
   *
   * @return Value of the "name" attribute.
   */
  public QName getName()
  {
    return m_qname;
  }

  /**
   * Get an integer representation of the element type.
   *
   * @return An integer representation of the element, defined in the
   *     Constants class.
   * @see org.apache.xalan.templates.Constants
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_WITHPARAM;
  }


  /**
   * Return the node name.
   *
   * @return the node name.
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_WITHPARAM_STRING;
  }
  
  /**
   * This function is called after everything else has been
   * recomposed, and allows the template to set remaining
   * values that may be based on some other property that
   * depends on recomposition.
   */
  public void compose(StylesheetRoot sroot) throws TransformerException
  {
    // See if we can reduce an RTF to a select with a string expression.
    if(null == m_selectPattern  
       && org.apache.xalan.processor.TransformerFactoryImpl.m_optimize)
    {
      XPath newSelect = ElemVariable.rewriteChildToExpression(this);
      if(null != newSelect)
        m_selectPattern = newSelect;
    }
    m_qnameID = sroot.getComposeState().getQNameID(m_qname);
    super.compose(sroot);
    
    java.util.Vector vnames = sroot.getComposeState().getVariableNames();
    if(null != m_selectPattern)
      m_selectPattern.fixupVariables(vnames, sroot.getComposeState().getGlobalsSize());
      
    // m_index must be resolved by ElemApplyTemplates and ElemCallTemplate!
  }
  
  /**
   * Set the parent as an ElemTemplateElement.
   *
   * @param parent This node's parent as an ElemTemplateElement
   */
  public void setParentElem(ElemTemplateElement p)
  {
    super.setParentElem(p);
    p.m_hasVariableDecl = true;
  }
  
  /**
   * Get the XObject representation of the variable.
   *
   * @param transformer non-null reference to the the current transform-time state.
   * @param sourceNode non-null reference to the <a href="http://www.w3.org/TR/xslt#dt-current-node">current source node</a>.
   *
   * @return the XObject representation of the variable.
   *
   * @throws TransformerException
   */
  public XObject getValue(TransformerImpl transformer, int sourceNode)
          throws TransformerException
  {

    XObject var;
    XPathContext xctxt = transformer.getXPathContext();

    xctxt.pushCurrentNode(sourceNode);

    try
    {
      if (null != m_selectPattern)
      {
        var = m_selectPattern.execute(xctxt, sourceNode, this);

        var.allowDetachToRelease(false);

        if (TransformerImpl.S_DEBUG)
          transformer.getTraceManager().fireSelectedEvent(sourceNode, this,
                  "select", m_selectPattern, var);
      }
      else if (null == getFirstChildElem())
      {
        var = XString.EMPTYSTRING;
      }
      else
      {

        // Use result tree fragment
        int df = transformer.transformToRTF(this);

        var = new XRTreeFrag(df, xctxt, this);
      }
    }
    finally
    {
      xctxt.popCurrentNode();
    }

    return var;
  }
  
  /**
   * Call the children visitors.
   * @param visitor The visitor whose appropriate method will be called.
   */
  protected void callChildVisitors(XSLTVisitor visitor, boolean callAttrs)
  {
  	if(callAttrs && (null != m_selectPattern))
  		m_selectPattern.getExpression().callVisitors(m_selectPattern, visitor);
    super.callChildVisitors(visitor, callAttrs);
  }



}
