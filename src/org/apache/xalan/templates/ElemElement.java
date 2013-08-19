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
import org.apache.xalan.transformer.ResultTreeHandler;

import javax.xml.transform.TransformerException;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:element
 * <pre>
 * <!ELEMENT xsl:element %template;>
 * <!ATTLIST xsl:element
 *   name %avt; #REQUIRED
 *   namespace %avt; #IMPLIED
 *   use-attribute-sets %qnames; #IMPLIED
 *   %space-att;
 * >
 * </pre>
 * @see <a href="http://www.w3.org/TR/xslt#section-Creating-Elements-with-xsl:element">XXX in XSLT Specification</a>
 */
public class ElemElement extends ElemUse
{

  /**
   * The name attribute is interpreted as an attribute value template.
   * It is an error if the string that results from instantiating the
   * attribute value template is not a QName.
   * @serial
   */
  protected AVT m_name_avt = null;

  /**
   * Set the "name" attribute.
   * The name attribute is interpreted as an attribute value template.
   * It is an error if the string that results from instantiating the
   * attribute value template is not a QName.
   *
   * @param v Name attribute to set for this element
   */
  public void setName(AVT v)
  {
    m_name_avt = v;
  }

  /**
   * Get the "name" attribute.
   * The name attribute is interpreted as an attribute value template.
   * It is an error if the string that results from instantiating the
   * attribute value template is not a QName.
   *
   * @return Name attribute for this element
   */
  public AVT getName()
  {
    return m_name_avt;
  }

  /**
   * If the namespace attribute is present, then it also is interpreted
   * as an attribute value template. The string that results from
   * instantiating the attribute value template should be a URI reference.
   * It is not an error if the string is not a syntactically legal URI reference.
   * @serial
   */
  protected AVT m_namespace_avt = null;

  /**
   * Set the "namespace" attribute.
   * If the namespace attribute is present, then it also is interpreted
   * as an attribute value template. The string that results from
   * instantiating the attribute value template should be a URI reference.
   * It is not an error if the string is not a syntactically legal URI reference.
   *
   * @param v NameSpace attribute to set for this element
   */
  public void setNamespace(AVT v)
  {
    m_namespace_avt = v;
  }

  /**
   * Get the "namespace" attribute.
   * If the namespace attribute is present, then it also is interpreted
   * as an attribute value template. The string that results from
   * instantiating the attribute value template should be a URI reference.
   * It is not an error if the string is not a syntactically legal URI reference.
   *
   * @return Namespace attribute for this element
   */
  public AVT getNamespace()
  {
    return m_namespace_avt;
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
    
    StylesheetRoot.ComposeState cstate = sroot.getComposeState();
    java.util.Vector vnames = cstate.getVariableNames();
    if(null != m_name_avt)
      m_name_avt.fixupVariables(vnames, cstate.getGlobalsSize());
    if(null != m_namespace_avt)
      m_namespace_avt.fixupVariables(vnames, cstate.getGlobalsSize());
  }


  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for this element
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_ELEMENT;
  }

  /**
   * Return the node name.
   *
   * @return This element's name 
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_ELEMENT_STRING;
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
    if(nodeName == null)
      return false;

    int len = nodeName.length();
    
    if(len == 0)
      return false;
      
    int indexOfNSSep = nodeName.indexOf(':');

    if(indexOfNSSep + 1 == len)
      return false;
      
    if(indexOfNSSep == 0)
      return false;
      
    String localName = QName.getLocalPart(nodeName);
      
    if(isValidNCName(localName))
    {
      String prefix = QName.getPrefixPart(nodeName);
      if(prefix.length() == 0)
        return true;
      if(isValidNCName(prefix))
        return true;
    }

    return false;
   }
   
  /**
   * Resolve the namespace into a prefix.  Meant to be
   * overidded by elemAttribute if this class is derived.
   *
   * @param rhandler The current result tree handler.
   * @param prefix The probable prefix if already known.
   * @param nodeNamespace  The namespace.
   *
   * @return The prefix to be used.
   */
  protected String resolvePrefix(ResultTreeHandler rhandler,
                                 String prefix, String nodeNamespace)
    throws TransformerException
  {

//    if (null != prefix && prefix.length() == 0)
//    {
//      String foundPrefix = rhandler.getPrefix(nodeNamespace);
//
//      // System.out.println("nsPrefix: "+nsPrefix);           
//      if (null == foundPrefix)
//        foundPrefix = "";
//    }
    return prefix;
  }
    
  /**
   * Create an element in the result tree.
   * The xsl:element element allows an element to be created with a
   * computed name. The expanded-name of the element to be created
   * is specified by a required name attribute and an optional namespace
   * attribute. The content of the xsl:element element is a template
   * for the attributes and children of the created element.
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(
          TransformerImpl transformer)
            throws TransformerException
  {

    if (TransformerImpl.S_DEBUG)
      transformer.getTraceManager().fireTraceEvent(this);
      
 	ResultTreeHandler rhandler = transformer.getResultTreeHandler();
    XPathContext xctxt = transformer.getXPathContext();
    int sourceNode = xctxt.getCurrentNode();
    
    
    String nodeName = m_name_avt == null ? null : m_name_avt.evaluate(xctxt, sourceNode, this);

    String prefix = null;
    String nodeNamespace = "";

    // Only validate if an AVT was used.
    if ((nodeName != null) && (!m_name_avt.isSimple()) && (!validateNodeName(nodeName)))
    {
      transformer.getMsgMgr().warn(
        this, XSLTErrorResources.WG_ILLEGAL_ATTRIBUTE_VALUE,
        new Object[]{ Constants.ATTRNAME_NAME, nodeName });

      nodeName = null;
    }

    else if (nodeName != null)
    {
      prefix = QName.getPrefixPart(nodeName);

      if (null != m_namespace_avt)
      {
        nodeNamespace = m_namespace_avt.evaluate(xctxt, sourceNode, this);
        if (null == nodeNamespace || 
            (prefix != null && prefix.length()>0 && nodeNamespace.length()== 0) )
          transformer.getMsgMgr().error(
              this, XSLTErrorResources.ER_NULL_URI_NAMESPACE);
        else
        {
        // Determine the actual prefix that we will use for this nodeNamespace

        prefix = resolvePrefix(rhandler, prefix, nodeNamespace);
        if (null == prefix)
          prefix = "";

        if (prefix.length() > 0)
          nodeName = (prefix + ":" + QName.getLocalPart(nodeName));
        else
          nodeName = QName.getLocalPart(nodeName);
        }
      }

      // No namespace attribute was supplied. Use the namespace declarations
      // currently in effect for the xsl:element element.
      else    
      {
        try
        {
          // Maybe temporary, until I get this worked out.  test: axes59
          nodeNamespace = getNamespaceForPrefix(prefix);

          // If we get back a null nodeNamespace, that means that this prefix could
          // not be found in the table.  This is okay only for a default namespace
          // that has never been declared.

          if ( (null == nodeNamespace) && (prefix.length() == 0) )
            nodeNamespace = "";
          else if (null == nodeNamespace)
          {
            transformer.getMsgMgr().warn(
              this, XSLTErrorResources.WG_COULD_NOT_RESOLVE_PREFIX,
              new Object[]{ prefix });

            nodeName = null;
          }

        }
        catch (Exception ex)
        {
          transformer.getMsgMgr().warn(
            this, XSLTErrorResources.WG_COULD_NOT_RESOLVE_PREFIX,
            new Object[]{ prefix });

          nodeName = null;
        }
      }
    }

    constructNode(nodeName, prefix, nodeNamespace, transformer);

    if (TransformerImpl.S_DEBUG)
      transformer.getTraceManager().fireTraceEndEvent(this);
  }
  
  /**
   * Construct a node in the result tree.  This method is overloaded by 
   * xsl:attribute. At this class level, this method creates an element.
   * If the node is null, we instantiate only the content of the node in accordance
   * with section 7.1.2 of the XSLT 1.0 Recommendation.
   *
   * @param nodeName The name of the node, which may be <code>null</code>.  If <code>null</code>,
   *                 only the non-attribute children of this node will be processed.
   * @param prefix The prefix for the namespace, which may be <code>null</code>.
   *               If not <code>null</code>, this prefix will be mapped and unmapped.
   * @param nodeNamespace The namespace of the node, which may be not be <code>null</code>.
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  void constructNode(
          String nodeName, String prefix, String nodeNamespace, TransformerImpl transformer)
            throws TransformerException
  {

    boolean shouldAddAttrs;

    try
    {
      ResultTreeHandler rhandler = transformer.getResultTreeHandler();

      if (null == nodeName)
      {
        shouldAddAttrs = false;
      }
      else
      {
        // Add namespace declarations.
        executeNSDecls(transformer);

        if (null != prefix)
        {
          rhandler.startPrefixMapping(prefix, nodeNamespace, true);
        }

        rhandler.startElement(nodeNamespace, QName.getLocalPart(nodeName),
                              nodeName, null);

        super.execute(transformer);

        shouldAddAttrs = true;
      }

      transformer.executeChildTemplates(this, shouldAddAttrs);

      // Now end the element if name was valid
      if (null != nodeName)
      {
        rhandler.endElement(nodeNamespace, QName.getLocalPart(nodeName),
                            nodeName);
        if (null != prefix)
        {
          rhandler.endPrefixMapping(prefix);
        }
        unexecuteNSDecls(transformer);
      }
    }
    catch (SAXException se)
    {
      throw new TransformerException(se);
    }
  }
  
  /**
   * Call the children visitors.
   * @param visitor The visitor whose appropriate method will be called.
   */
  protected void callChildVisitors(XSLTVisitor visitor, boolean callAttrs)
  {
  	if(callAttrs)
  	{
  	  if(null != m_name_avt)
  		m_name_avt.callVisitors(visitor);
  		
  	  if(null != m_namespace_avt)
  		m_namespace_avt.callVisitors(visitor);
  	}
  		
    super.callChildVisitors(visitor, callAttrs);
  }

}
