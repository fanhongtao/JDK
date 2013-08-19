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
//import org.w3c.dom.traversal.NodeIterator;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.ref.DTMTreeWalker;

import org.xml.sax.*;

import org.apache.xpath.*;
import org.apache.xpath.objects.XObject;
import org.apache.xalan.trace.SelectionEvent;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xml.utils.QName;
import org.apache.xalan.transformer.TreeWalker2Result;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.ResultTreeHandler;

import javax.xml.transform.TransformerException;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:copy-of.
 * <pre>
 * <!ELEMENT xsl:copy-of EMPTY>
 * <!ATTLIST xsl:copy-of select %expr; #REQUIRED>
 * </pre>
 * @see <a href="http://www.w3.org/TR/xslt#copy-of">copy-of in XSLT Specification</a>
 */
public class ElemCopyOf extends ElemTemplateElement
{

  /**
   * The required select attribute contains an expression.
   * @serial
   */
  public XPath m_selectExpression = null;

  /**
   * Set the "select" attribute.
   * The required select attribute contains an expression.
   *
   * @param expr Expression for select attribute 
   */
  public void setSelect(XPath expr)
  {
    m_selectExpression = expr;
  }

  /**
   * Get the "select" attribute.
   * The required select attribute contains an expression.
   *
   * @return Expression for select attribute 
   */
  public XPath getSelect()
  {
    return m_selectExpression;
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
    m_selectExpression.fixupVariables(cstate.getVariableNames(), cstate.getGlobalsSize());
  }

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for this element
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_COPY_OF;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_COPY_OF_STRING;
  }

  /**
   * The xsl:copy-of element can be used to insert a result tree
   * fragment into the result tree, without first converting it to
   * a string as xsl:value-of does (see [7.6.1 Generating Text with
   * xsl:value-of]).
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

    try
    {
      XPathContext xctxt = transformer.getXPathContext();
      int sourceNode = xctxt.getCurrentNode();
      XObject value = m_selectExpression.execute(xctxt, sourceNode, this);

      if (TransformerImpl.S_DEBUG)
        transformer.getTraceManager().fireSelectedEvent(sourceNode, this,
                                                        "select", m_selectExpression, value);

      ResultTreeHandler handler = transformer.getResultTreeHandler();

      if (null != value)
                        {
        int type = value.getType();
        String s;

        switch (type)
        {
        case XObject.CLASS_BOOLEAN :
        case XObject.CLASS_NUMBER :
        case XObject.CLASS_STRING :
          s = value.str();

          handler.characters(s.toCharArray(), 0, s.length());
          break;
        case XObject.CLASS_NODESET :

          // System.out.println(value);
          DTMIterator nl = value.iter();

          // Copy the tree.
          DTMTreeWalker tw = new TreeWalker2Result(transformer, handler);
          int pos;

          while (DTM.NULL != (pos = nl.nextNode()))
          {
            DTM dtm = xctxt.getDTMManager().getDTM(pos);
            short t = dtm.getNodeType(pos);

            // If we just copy the whole document, a startDoc and endDoc get 
            // generated, so we need to only walk the child nodes.
            if (t == DTM.DOCUMENT_NODE)
            {
              for (int child = dtm.getFirstChild(pos); child != DTM.NULL;
                   child = dtm.getNextSibling(child))
              {
                tw.traverse(child);
              }
            }
            else if (t == DTM.ATTRIBUTE_NODE)
            {
              handler.addAttribute(pos);
            }
            else
            {
              tw.traverse(pos);
            }
          }
          // nl.detach();
          break;
        case XObject.CLASS_RTREEFRAG :
          handler.outputResultTreeFragment(value,
                                           transformer.getXPathContext());
          break;
        default :
          
          s = value.str();

          handler.characters(s.toCharArray(), 0, s.length());
          break;
        }
      }
                        
      // I don't think we want this.  -sb
      //  if (TransformerImpl.S_DEBUG)
      //  transformer.getTraceManager().fireSelectedEvent(sourceNode, this,
      //  "endSelect", m_selectExpression, value);

    }
    catch(org.xml.sax.SAXException se)
    {
      throw new TransformerException(se);
    }
    finally
    {
      if (TransformerImpl.S_DEBUG)
        transformer.getTraceManager().fireTraceEndEvent(this);
    }

  }

  /**
   * Add a child to the child list.
   *
   * @param newChild Child to add to this node's child list
   *
   * @return Child just added to child list
   */
  public ElemTemplateElement appendChild(ElemTemplateElement newChild)
  {

    error(XSLTErrorResources.ER_CANNOT_ADD,
          new Object[]{ newChild.getNodeName(),
                        this.getNodeName() });  //"Can not add " +((ElemTemplateElement)newChild).m_elemName +

    //" to " + this.m_elemName);
    return null;
  }
  
  /**
   * Call the children visitors.
   * @param visitor The visitor whose appropriate method will be called.
   */
  protected void callChildVisitors(XSLTVisitor visitor, boolean callAttrs)
  {
  	if(callAttrs)
  		m_selectExpression.getExpression().callVisitors(m_selectExpression, visitor);
    super.callChildVisitors(visitor, callAttrs);
  }

}
