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
import org.apache.xml.utils.QName;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;

import javax.xml.transform.TransformerException;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:processing-instruction.
 * <pre>
 * <!ELEMENT xsl:processing-instruction %char-template;>
 * <!ATTLIST xsl:processing-instruction
 *   name %avt; #REQUIRED
 *   %space-att;
 * >
 * </pre>
 * @see <a href="http://www.w3.org/TR/xslt#section-Creating-Processing-Instructions">section-Creating-Processing-Instructions in XSLT Specification</a>
 */
public class ElemPI extends ElemTemplateElement
{

  /**
   * The xsl:processing-instruction element has a required name
   * attribute that specifies the name of the processing instruction node.
   * The value of the name attribute is interpreted as an
   * attribute value template.
   * @serial
   */
  private AVT m_name_atv = null;

  /**
   * Set the "name" attribute.
   * DJD
   *
   * @param v Value for the name attribute
   */
  public void setName(AVT v)
  {
    m_name_atv = v;
  }

  /**
   * Get the "name" attribute.
   * DJD
   *
   * @return The value of the "name" attribute 
   */
  public AVT getName()
  {
    return m_name_atv;
  }
  
  /**
   * This function is called after everything else has been
   * recomposed, and allows the template to set remaining
   * values that may be based on some other property that
   * depends on recomposition.
   */
  public void compose(StylesheetRoot sroot) throws TransformerException
  {
    super.compose(sroot);
    java.util.Vector vnames = sroot.getComposeState().getVariableNames();
    if(null != m_name_atv)
      m_name_atv.fixupVariables(vnames, sroot.getComposeState().getGlobalsSize());
  }



  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for the element
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_PI;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_PI_STRING;
  }

  /**
   * Create a processing instruction in the result tree.
   * The content of the xsl:processing-instruction element is a
   * template for the string-value of the processing instruction node.
   * @see <a href="http://www.w3.org/TR/xslt#section-Creating-Processing-Instructions">section-Creating-Processing-Instructions in XSLT Specification</a>
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

    if (TransformerImpl.S_DEBUG)
      transformer.getTraceManager().fireTraceEvent(this);

    XPathContext xctxt = transformer.getXPathContext();
    int sourceNode = xctxt.getCurrentNode();
    
    String piName = m_name_atv == null ? null : m_name_atv.evaluate(xctxt, sourceNode, this);
    
    // Ignore processing instruction if name is null
    if (piName == null) return;

    if (piName.equalsIgnoreCase("xml"))
    {
     	transformer.getMsgMgr().warn(
        this, XSLTErrorResources.WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML,
              new Object[]{ Constants.ATTRNAME_NAME, piName });
		return;
    }
    
    // Only check if an avt was used (ie. this wasn't checked at compose time.)
    // Ignore processing instruction, if invalid
    else if ((!m_name_atv.isSimple()) && (!isValidNCName(piName)))
    {
     	transformer.getMsgMgr().warn(
        this, XSLTErrorResources.WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME,
              new Object[]{ Constants.ATTRNAME_NAME, piName });
		return;    	
    }

    // Note the content model is:
    // <!ENTITY % instructions "
    // %char-instructions;
    // | xsl:processing-instruction
    // | xsl:comment
    // | xsl:element
    // | xsl:attribute
    // ">
    String data = transformer.transformToString(this);

    try
    {
      transformer.getResultTreeHandler().processingInstruction(piName, data);
    }
    catch(org.xml.sax.SAXException se)
    {
      throw new TransformerException(se);
    }
    
    if (TransformerImpl.S_DEBUG)
      transformer.getTraceManager().fireTraceEndEvent(this);
  }

  /**
   * Add a child to the child list.
   *
   * @param newChild Child to add to child list
   *
   * @return The child just added to the child list
   *
   * @throws DOMException
   */
  public ElemTemplateElement appendChild(ElemTemplateElement newChild)
  {

    int type = ((ElemTemplateElement) newChild).getXSLToken();

    switch (type)
    {

    // char-instructions 
    case Constants.ELEMNAME_TEXTLITERALRESULT :
    case Constants.ELEMNAME_APPLY_TEMPLATES :
    case Constants.ELEMNAME_APPLY_IMPORTS :
    case Constants.ELEMNAME_CALLTEMPLATE :
    case Constants.ELEMNAME_FOREACH :
    case Constants.ELEMNAME_VALUEOF :
    case Constants.ELEMNAME_COPY_OF :
    case Constants.ELEMNAME_NUMBER :
    case Constants.ELEMNAME_CHOOSE :
    case Constants.ELEMNAME_IF :
    case Constants.ELEMNAME_TEXT :
    case Constants.ELEMNAME_COPY :
    case Constants.ELEMNAME_VARIABLE :
    case Constants.ELEMNAME_MESSAGE :

      // instructions 
      // case Constants.ELEMNAME_PI:
      // case Constants.ELEMNAME_COMMENT:
      // case Constants.ELEMNAME_ELEMENT:
      // case Constants.ELEMNAME_ATTRIBUTE:
      break;
    default :
      error(XSLTErrorResources.ER_CANNOT_ADD,
            new Object[]{ newChild.getNodeName(),
                          this.getNodeName() });  //"Can not add " +((ElemTemplateElement)newChild).m_elemName +

    //" to " + this.m_elemName);
    }

    return super.appendChild(newChild);
  }
}
