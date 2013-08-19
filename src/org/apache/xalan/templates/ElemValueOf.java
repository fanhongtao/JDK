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
import org.apache.xml.dtm.DTMIterator;

import org.xml.sax.*;

import org.apache.xpath.*;
import org.apache.xpath.objects.XString;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xalan.trace.SelectionEvent;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.ResultTreeHandler;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.XMLString;

import javax.xml.transform.TransformerException;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:value-of.
 * <pre>
 * <!ELEMENT xsl:value-of EMPTY>
 * <!ATTLIST xsl:value-of
 *   select %expr; #REQUIRED
 *   disable-output-escaping (yes|no) "no"
 * >
 * </pre>
 * @see <a href="http://www.w3.org/TR/xslt#value-of">value-of in XSLT Specification</a>
 */
public class ElemValueOf extends ElemTemplateElement
{

  /**
   * The select expression to be executed.
   * @serial
   */
  private XPath m_selectExpression = null;

  /**
   * True if the pattern is a simple ".".
   * @serial
   */
  private boolean m_isDot = false;

  /**
   * Set the "select" attribute.
   * The required select attribute is an expression; this expression
   * is evaluated and the resulting object is converted to a
   * string as if by a call to the string function.
   *
   * @param v The value to set for the "select" attribute.
   */
  public void setSelect(XPath v)
  {

    if (null != v)
    {
      String s = v.getPatternString();

      m_isDot = (null != s) && s.equals(".");
    }

    m_selectExpression = v;
  }

  /**
   * Get the "select" attribute.
   * The required select attribute is an expression; this expression
   * is evaluated and the resulting object is converted to a
   * string as if by a call to the string function.
   *
   * @return The value of the "select" attribute.
   */
  public XPath getSelect()
  {
    return m_selectExpression;
  }

  /**
   * Tells if this element should disable escaping.
   * @serial
   */
  private boolean m_disableOutputEscaping = false;

  /**
   * Set the "disable-output-escaping" attribute.
   * Normally, the xml output method escapes & and < (and
   * possibly other characters) when outputting text nodes.
   * This ensures that the output is well-formed XML. However,
   * it is sometimes convenient to be able to produce output
   * that is almost, but not quite well-formed XML; for
   * example, the output may include ill-formed sections
   * which are intended to be transformed into well-formed
   * XML by a subsequent non-XML aware process. For this reason,
   * XSLT provides a mechanism for disabling output escaping.
   * An xsl:value-of or xsl:text element may have a
   * disable-output-escaping attribute; the allowed values
   * are yes or no; the default is no; if the value is yes,
   * then a text node generated by instantiating the xsl:value-of
   * or xsl:text element should be output without any escaping.
   * @see <a href="http://www.w3.org/TR/xslt#disable-output-escaping">disable-output-escaping in XSLT Specification</a>
   *
   * @param v The value to set for the "disable-output-escaping" attribute.
   */
  public void setDisableOutputEscaping(boolean v)
  {
    m_disableOutputEscaping = v;
  }

  /**
   * Get the "disable-output-escaping" attribute.
   * Normally, the xml output method escapes & and < (and
   * possibly other characters) when outputting text nodes.
   * This ensures that the output is well-formed XML. However,
   * it is sometimes convenient to be able to produce output
   * that is almost, but not quite well-formed XML; for
   * example, the output may include ill-formed sections
   * which are intended to be transformed into well-formed
   * XML by a subsequent non-XML aware process. For this reason,
   * XSLT provides a mechanism for disabling output escaping.
   * An xsl:value-of or xsl:text element may have a
   * disable-output-escaping attribute; the allowed values
   * are yes or no; the default is no; if the value is yes,
   * then a text node generated by instantiating the xsl:value-of
   * or xsl:text element should be output without any escaping.
   * @see <a href="http://www.w3.org/TR/xslt#disable-output-escaping">disable-output-escaping in XSLT Specification</a>
   *
   * @return The value of the "disable-output-escaping" attribute.
   */
  public boolean getDisableOutputEscaping()
  {
    return m_disableOutputEscaping;
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
    return Constants.ELEMNAME_VALUEOF;
  }

  /**
   * This function is called after everything else has been
   * recomposed, and allows the template to set remaining
   * values that may be based on some other property that
   * depends on recomposition.
   *
   * NEEDSDOC @param sroot
   *
   * @throws TransformerException
   */
  public void compose(StylesheetRoot sroot) throws TransformerException
  {

    super.compose(sroot);

    java.util.Vector vnames = sroot.getComposeState().getVariableNames();

    if (null != m_selectExpression)
      m_selectExpression.fixupVariables(
        vnames, sroot.getComposeState().getGlobalsSize());
  }

  /**
   * Return the node name.
   *
   * @return The node name
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_VALUEOF_STRING;
  }

  /**
   * Execute the string expression and copy the text to the
   * result tree.
   * The required select attribute is an expression; this expression
   * is evaluated and the resulting object is converted to a string
   * as if by a call to the string function. The string specifies
   * the string-value of the created text node. If the string is
   * empty, no text node will be created. The created text node will
   * be merged with any adjacent text nodes.
   * @see <a href="http://www.w3.org/TR/xslt#value-of">value-of in XSLT Specification</a>
   *
   * @param transformer non-null reference to the the current transform-time state.
   * @param sourceNode non-null reference to the <a href="http://www.w3.org/TR/xslt#dt-current-node">current source node</a>.
   * @param mode reference, which may be null, to the <a href="http://www.w3.org/TR/xslt#modes">current mode</a>.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {

    XPathContext xctxt = transformer.getXPathContext();
    ResultTreeHandler rth = transformer.getResultTreeHandler();

    if (TransformerImpl.S_DEBUG)
      transformer.getTraceManager().fireTraceEvent(this);

    try
    {
      // Optimize for "."
      if (false && m_isDot && !TransformerImpl.S_DEBUG)
      {
        int child = xctxt.getCurrentNode();
        DTM dtm = xctxt.getDTM(child);

        xctxt.pushCurrentNode(child);

        if (m_disableOutputEscaping)
          rth.processingInstruction(
            javax.xml.transform.Result.PI_DISABLE_OUTPUT_ESCAPING, "");

        try
        {
          dtm.dispatchCharactersEvents(child, rth, false);
        }
        finally
        {
          if (m_disableOutputEscaping)
            rth.processingInstruction(
              javax.xml.transform.Result.PI_ENABLE_OUTPUT_ESCAPING, "");

          xctxt.popCurrentNode();
        }
      }
      else
      {
        xctxt.pushNamespaceContext(this);

        int current = xctxt.getCurrentNode();

        xctxt.pushCurrentNodeAndExpression(current, current);

        if (m_disableOutputEscaping)
          rth.processingInstruction(
            javax.xml.transform.Result.PI_DISABLE_OUTPUT_ESCAPING, "");

        try
        {
          Expression expr = m_selectExpression.getExpression();

          if (TransformerImpl.S_DEBUG)
          {
            XObject obj = expr.execute(xctxt);

            transformer.getTraceManager().fireSelectedEvent(current, this,
                    "select", m_selectExpression, obj);
            obj.dispatchCharactersEvents(rth);
          }
          else
          {
            expr.executeCharsToContentHandler(xctxt, rth);
          }
        }
        finally
        {
          if (m_disableOutputEscaping)
            rth.processingInstruction(
              javax.xml.transform.Result.PI_ENABLE_OUTPUT_ESCAPING, "");

          xctxt.popNamespaceContext();
          xctxt.popCurrentNodeAndExpression();
        }
      }
    }
    catch (SAXException se)
    {
      throw new TransformerException(se);
    }
    catch (RuntimeException re) {
    	TransformerException te = new TransformerException(re);
    	te.setLocator(this);
    	throw te;
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
   * @param newChild Child to add to children list
   *
   * @return Child just added to children list
   *
   * @throws DOMException
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
