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
 
import org.xml.sax.*;

import org.apache.xml.utils.QName;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.ResultTreeHandler;

import javax.xml.transform.TransformerException;

/**
 * <meta name="usage" content="advanced"/>
 * Implement a text literal.
 * @see <a href="http://www.w3.org/TR/xslt#section-Creating-Text">section-Creating-Text in XSLT Specification</a>
 */
public class ElemTextLiteral extends ElemTemplateElement
{

  /**
   * Tell if space should be preserved.
   * @serial
   */
  private boolean m_preserveSpace;

  /**
   * Set whether or not space should be preserved.
   *
   * @param v Boolean flag indicating whether 
   * or not space should be preserved
   */
  public void setPreserveSpace(boolean v)
  {
    m_preserveSpace = v;
  }

  /**
   * Get whether or not space should be preserved.
   *
   * @return Boolean flag indicating whether 
   * or not space should be preserved 
   */
  public boolean getPreserveSpace()
  {
    return m_preserveSpace;
  }

  /**
   * The character array.
   * @serial
   */
  private char m_ch[];
  
  /**
   * The character array as a string.
   * @serial
   */
  private String m_str;

  /**
   * Set the characters that will be output to the result tree..
   *
   * @param v Array of characters that will be output to the result tree 
   */
  public void setChars(char[] v)
  {
    m_ch = v;
  }

  /**
   * Get the characters that will be output to the result tree..
   *
   * @return Array of characters that will be output to the result tree
   */
  public char[] getChars()
  {
    return m_ch;
  }
  
  /**
   * Get the value of the node as a string.
   *
   * @return null
   */
  public synchronized String getNodeValue()
  {

    if(null == m_str)
    {
      m_str = new String(m_ch);
    }

    return m_str;
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
   * @param v Boolean value for "disable-output-escaping" attribute.
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
   * @return Boolean value of "disable-output-escaping" attribute.
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
    return Constants.ELEMNAME_TEXTLITERALRESULT;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName()
  {
    return "#Text";
  }

  /**
   * Copy the text literal to the result tree.
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
      ResultTreeHandler rth = transformer.getResultTreeHandler();
      if (m_disableOutputEscaping)
      {
        rth.processingInstruction(javax.xml.transform.Result.PI_DISABLE_OUTPUT_ESCAPING, "");
      }

      rth.characters(m_ch, 0, m_ch.length);

      if (m_disableOutputEscaping)
      {
        rth.processingInstruction(javax.xml.transform.Result.PI_ENABLE_OUTPUT_ESCAPING, "");
      }
    }
    catch(SAXException se)
    {
      throw new TransformerException(se);
    }
    finally
    {
      if (TransformerImpl.S_DEBUG)
        transformer.getTraceManager().fireTraceEndEvent(this);
    }
  }
}
