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
package org.apache.xalan.processor;

import java.lang.StringBuffer;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.ElemText;
import org.apache.xalan.templates.ElemTextLiteral;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.Constants;
import org.apache.xml.utils.XMLCharacterRecognizer;

/**
 * This class processes character events for a XSLT template element.
 * @see <a href="http://www.w3.org/TR/xslt#dtd">XSLT DTD</a>
 * @see <a href="http://www.w3.org/TR/xslt#section-Creating-the-Result-Tree">section-Creating-the-Result-Tree in XSLT Specification</a>
 */
public class ProcessorCharacters extends XSLTElementProcessor
{

  /**
   * Receive notification of the start of the non-text event.  This
   * is sent to the current processor when any non-text event occurs.
   *
   * @param handler non-null reference to current StylesheetHandler that is constructing the Templates.
   */
  public void startNonText(StylesheetHandler handler) throws org.xml.sax.SAXException
  {
    if (this == handler.getCurrentProcessor())
    {
      handler.popProcessor();
    }

    int nChars = m_accumulator.length();

    if ((nChars > 0)
            && ((null != m_xslTextElement)
                ||!XMLCharacterRecognizer.isWhiteSpace(m_accumulator)) 
                || handler.isSpacePreserve())
    {
      ElemTextLiteral elem = new ElemTextLiteral();

      elem.setDOMBackPointer(m_firstBackPointer);
      elem.setLocaterInfo(handler.getLocator());
      try
      {
        elem.setPrefixes(handler.getNamespaceSupport());
      }
      catch(TransformerException te)
      {
        throw new org.xml.sax.SAXException(te);
      }

      boolean doe = (null != m_xslTextElement)
                    ? m_xslTextElement.getDisableOutputEscaping() : false;

      elem.setDisableOutputEscaping(doe);
      elem.setPreserveSpace(true);

      char[] chars = new char[nChars];

      m_accumulator.getChars(0, nChars, chars, 0);
      elem.setChars(chars);

      ElemTemplateElement parent = handler.getElemTemplateElement();

      parent.appendChild(elem);
    }

    m_accumulator.setLength(0);
    m_firstBackPointer = null;
  }
  
  protected Node m_firstBackPointer = null;

  /**
   * Receive notification of character data inside an element.
   *
   *
   * @param handler non-null reference to current StylesheetHandler that is constructing the Templates.
   * @param ch The characters.
   * @param start The start position in the character array.
   * @param length The number of characters to use from the
   *               character array.
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#characters
   */
  public void characters(
          StylesheetHandler handler, char ch[], int start, int length)
            throws org.xml.sax.SAXException
  {

    m_accumulator.append(ch, start, length);
    
    if(null == m_firstBackPointer)
      m_firstBackPointer = handler.getOriginatingNode();

    // Catch all events until a non-character event.
    if (this != handler.getCurrentProcessor())
      handler.pushProcessor(this);
  }

  /**
   * Receive notification of the end of an element.
   *
   * @param handler The calling StylesheetHandler/TemplatesBuilder.
   * @param uri The Namespace URI, or the empty string if the
   *        element has no Namespace URI or if Namespace
   *        processing is not being performed.
   * @param localName The local name (without prefix), or the
   *        empty string if Namespace processing is not being
   *        performed.
   * @param rawName The raw XML 1.0 name (with prefix), or the
   *        empty string if raw names are not available.
   * @param atts The attributes attached to the element.  If
   *        there are no attributes, it shall be an empty
   *        Attributes object.
   * @see org.apache.xalan.processor.StylesheetHandler#startElement
   * @see org.apache.xalan.processor.StylesheetHandler#endElement
   * @see org.xml.sax.ContentHandler#startElement
   * @see org.xml.sax.ContentHandler#endElement
   * @see org.xml.sax.Attributes
   */
  public void endElement(
          StylesheetHandler handler, String uri, String localName, String rawName)
            throws org.xml.sax.SAXException
  {

    // Since this has been installed as the current processor, we 
    // may get and end element event, in which case, we pop and clear 
    // and then call the real element processor.
    startNonText(handler);
    handler.getCurrentProcessor().endElement(handler, uri, localName,
                                             rawName);
    handler.popProcessor();
  }

  /**
   * Accumulate characters, until a non-whitespace event has
   * occured.
   */
  private StringBuffer m_accumulator = new StringBuffer();

  /**
   * The xsl:text processor will call this to set a
   * preserve space state.
   */
  private ElemText m_xslTextElement;

  /**
   * Set the current setXslTextElement. The xsl:text 
   * processor will call this to set a preserve space state.
   *
   * @param xslTextElement The current xslTextElement that 
   *                       is preserving state, or null.
   */
  void setXslTextElement(ElemText xslTextElement)
  {
    m_xslTextElement = xslTextElement;
  }
}
