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

import org.apache.xml.utils.QName;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.ResultTreeHandler;
import org.apache.xpath.XPathContext;

import org.xml.sax.SAXException;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:attribute.
 * <pre>
 * &amp;!ELEMENT xsl:attribute %char-template;>
 * &amp;!ATTLIST xsl:attribute
 *   name %avt; #REQUIRED
 *   namespace %avt; #IMPLIED
 *   %space-att;
 * &amp;
 * </pre>
 * @see <a href="http://www.w3.org/TR/xslt#creating-attributes">creating-attributes in XSLT Specification</a>
 */
public class ElemAttribute extends ElemElement
{

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for this element
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_ATTRIBUTE;
  }

  /**
   * Return the node name.
   *
   * @return The element name 
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_ATTRIBUTE_STRING;
  }

  /**
   * Create an attribute in the result tree.
   * @see <a href="http://www.w3.org/TR/xslt#creating-attributes">creating-attributes in XSLT Specification</a>
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(
          TransformerImpl transformer)
            throws TransformerException
  {
    ResultTreeHandler rhandler = transformer.getResultTreeHandler();

    // If they are trying to add an attribute when there isn't an 
    // element pending, it is an error.
    // I don't think we need this check here because it is checked in 
    // ResultTreeHandler.addAttribute.  (is)
//    if (!rhandler.isElementPending())
//    {
//      // Make sure the trace event is sent.
//      if (TransformerImpl.S_DEBUG)
//        transformer.getTraceManager().fireTraceEvent(this);
//
//      XPathContext xctxt = transformer.getXPathContext();
//      int sourceNode = xctxt.getCurrentNode();
//      String attrName = m_name_avt.evaluate(xctxt, sourceNode, this);
//      transformer.getMsgMgr().warn(this,
//                                   XSLTErrorResources.WG_ILLEGAL_ATTRIBUTE_POSITION,
//                                   new Object[]{ attrName });
//
//      if (TransformerImpl.S_DEBUG)
//        transformer.getTraceManager().fireTraceEndEvent(this);
//      return;
//
//      // warn(templateChild, sourceNode, "Trying to add attribute after element child has been added, ignoring...");
//    }
    
    super.execute(transformer);
    
  }
  
  /**
   * Resolve the namespace into a prefix.  At this level, if no prefix exists, 
   * then return a manufactured prefix.
   *
   * @param rhandler The current result tree handler.
   * @param prefix The probable prefix if already known.
   * @param nodeNamespace  The namespace, which should not be null.
   *
   * @return The prefix to be used.
   */
  protected String resolvePrefix(ResultTreeHandler rhandler,
                                 String prefix, String nodeNamespace)
    throws TransformerException
  {

    if (null != prefix && (prefix.length() == 0 || prefix.equals("xmlns")))
    {
      // Since we can't use default namespace, in this case we try and 
      // see if a prefix has already been defined or this namespace.
      prefix = rhandler.getPrefix(nodeNamespace);

      // System.out.println("nsPrefix: "+nsPrefix);           
      if (null == prefix || prefix.length() == 0 || prefix.equals("xmlns"))
      {
        if(nodeNamespace.length() > 0)
        {
          prefix = rhandler.getNewUniqueNSPrefix();
        }
        else
          prefix = "";
      }
    }
    return prefix;
  }
  
  /**
   * Validate that the node name is good.
   * 
   * @param nodeName Name of the node being constructed, which may be null.
   * 
   * @return true if the node name is valid, false otherwise.
   */
   protected boolean validateNodeName(String nodeName)
   {
      if(null == nodeName)
        return false;
      if(nodeName.equals("xmlns"))
        return false;
      return super.validateNodeName(nodeName);
   }
  
  /**
   * Construct a node in the result tree.  This method is overloaded by 
   * xsl:attribute. At this class level, this method creates an element.
   *
   * @param nodeName The name of the node, which may be null.
   * @param prefix The prefix for the namespace, which may be null.
   * @param nodeNamespace The namespace of the node, which may be null.
   * @param transformer non-null reference to the the current transform-time state.
   * @param sourceNode non-null reference to the <a href="http://www.w3.org/TR/xslt#dt-current-node">current source node</a>.
   * @param mode reference, which may be null, to the <a href="http://www.w3.org/TR/xslt#modes">current mode</a>.
   *
   * @throws TransformerException
   */
  void constructNode(
          String nodeName, String prefix, String nodeNamespace, TransformerImpl transformer)
            throws TransformerException
  {

    if(null != nodeName && nodeName.length() > 0)
    {
      ResultTreeHandler rhandler = transformer.getResultTreeHandler();
      if(prefix != null && prefix.length() > 0)
      {
        try
        {
          rhandler.startPrefixMapping(prefix, nodeNamespace, false);
        }
        catch(SAXException se)
        {
          throw new TransformerException(se);
        }
      }
      String val = transformer.transformToString(this);
      String localName = QName.getLocalPart(nodeName);
      rhandler.addAttribute(nodeNamespace, localName, nodeName, "CDATA", val);
    }
  }


  /**
   * Add a child to the child list.
   * <!ELEMENT xsl:attribute %char-template;>
   * <!ATTLIST xsl:attribute
   *   name %avt; #REQUIRED
   *   namespace %avt; #IMPLIED
   *   %space-att;
   * >
   *
   * @param newChild Child to append to the list of this node's children
   *
   * @return The node we just appended to the children list 
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
	/**
	 * @see ElemElement#setName(AVT)
	 */
	public void setName(AVT v) {
        if (v.isSimple())
        {
            if (v.getSimpleString().equals("xmlns"))
            {
                throw new IllegalArgumentException();
            }
        }
		super.setName(v);
	}

}
