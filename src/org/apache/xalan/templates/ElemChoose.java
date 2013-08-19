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
import org.apache.xpath.objects.XObject;
import org.apache.xalan.trace.SelectionEvent;
import org.apache.xml.utils.QName;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;

import javax.xml.transform.TransformerException;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:choose.
 * <pre>
 * <!ELEMENT xsl:choose (xsl:when+, xsl:otherwise?)>
 * <!ATTLIST xsl:choose %space-att;>
 * </pre>
 * @see <a href="http://www.w3.org/TR/xslt#section-Conditional-Processing-with-xsl:choose">XXX in XSLT Specification</a>
 */
public class ElemChoose extends ElemTemplateElement
{

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for this element
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_CHOOSE;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_CHOOSE_STRING;
  }

  /**
   * Constructor ElemChoose
   *
   */
  public ElemChoose(){}

  /**
   * Execute the xsl:choose transformation.
   *
   *
   * @param transformer non-null reference to the the current transform-time state.
   * @param sourceNode non-null reference to the <a href="http://www.w3.org/TR/xslt#dt-current-node">current source node</a>.
   * @param mode reference, which may be null, to the <a href="http://www.w3.org/TR/xslt#modes">current mode</a>.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {

    if (TransformerImpl.S_DEBUG)
      transformer.getTraceManager().fireTraceEvent(this);

    boolean found = false;

    for (ElemTemplateElement childElem = getFirstChildElem();
            childElem != null; childElem = childElem.getNextSiblingElem())
    {
      int type = childElem.getXSLToken();

      if (Constants.ELEMNAME_WHEN == type)
      {
        found = true;

        ElemWhen when = (ElemWhen) childElem;

        // must be xsl:when
        XPathContext xctxt = transformer.getXPathContext();
        int sourceNode = xctxt.getCurrentNode();
        
        // System.err.println("\""+when.getTest().getPatternString()+"\"");
        
        // if(when.getTest().getPatternString().equals("COLLECTION/icuser/ictimezone/LITERAL='GMT +13:00 Pacific/Tongatapu'"))
        // 	System.err.println("Found COLLECTION/icuser/ictimezone/LITERAL");

        if (TransformerImpl.S_DEBUG)
        {
          XObject test = when.getTest().execute(xctxt, sourceNode, when);

          if (TransformerImpl.S_DEBUG)
            transformer.getTraceManager().fireSelectedEvent(sourceNode, when,
                    "test", when.getTest(), test);

          if (test.bool())
          {
            transformer.getTraceManager().fireTraceEvent(when);
            
            transformer.executeChildTemplates(when, true);

	        transformer.getTraceManager().fireTraceEndEvent(when); 
	                  
            return;
          }

        }
        else if (when.getTest().bool(xctxt, sourceNode, when))
        {
          transformer.executeChildTemplates(when, true);

          return;
        }
      }
      else if (Constants.ELEMNAME_OTHERWISE == type)
      {
        found = true;

        if (TransformerImpl.S_DEBUG)
          transformer.getTraceManager().fireTraceEvent(childElem);

        // xsl:otherwise                
        transformer.executeChildTemplates(childElem, true);

        if (TransformerImpl.S_DEBUG)
	      transformer.getTraceManager().fireTraceEndEvent(childElem); 
        return;
      }
    }

    if (!found)
      transformer.getMsgMgr().error(
        this, XSLTErrorResources.ER_CHOOSE_REQUIRES_WHEN);
        
    if (TransformerImpl.S_DEBUG)
	  transformer.getTraceManager().fireTraceEndEvent(this);         
  }

  /**
   * Add a child to the child list.
   *
   * @param newChild Child to add to this node's child list
   *
   * @return The child that was just added to the child list
   *
   * @throws DOMException
   */
  public ElemTemplateElement appendChild(ElemTemplateElement newChild)
  {

    int type = ((ElemTemplateElement) newChild).getXSLToken();

    switch (type)
    {
    case Constants.ELEMNAME_WHEN :
    case Constants.ELEMNAME_OTHERWISE :

      // TODO: Positional checking
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
   * Tell if this element can accept variable declarations.
   * @return true if the element can accept and process variable declarations.
   */
  public boolean canAcceptVariables()
  {
  	return false;
  }

}
