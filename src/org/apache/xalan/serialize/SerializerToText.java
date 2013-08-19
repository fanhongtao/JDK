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
package org.apache.xalan.serialize;

import org.xml.sax.*;

import java.util.*;

import java.io.*;

import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.OutputProperties;

import javax.xml.transform.OutputKeys;

/**
 * <meta name="usage" content="general"/>
 * This class takes SAX events (in addition to some extra events
 * that SAX doesn't handle yet) and produces simple text only.
 */
public class SerializerToText extends SerializerToXML
{

  /**
   * Default constructor.
   */
  public SerializerToText()
  {
    super();
  }

   /**
   * Receive an object for locating the origin of SAX document events.
   *
   * <p>SAX parsers are strongly encouraged (though not absolutely
   * required) to supply a locator: if it does so, it must supply
   * the locator to the application by invoking this method before
   * invoking any of the other methods in the DocumentHandler
   * interface.</p>
   *
   * <p>The locator allows the application to determine the end
   * position of any document-related event, even if the parser is
   * not reporting an error.  Typically, the application will
   * use this information for reporting its own errors (such as
   * character content that does not match an application's
   * business rules).  The information returned by the locator
   * is probably not sufficient for use with a search engine.</p>
   *
   * <p>Note that the locator will return correct information only
   * during the invocation of the events in this interface.  The
   * application should not attempt to use it at any other time.</p>
   *
   * @param locator An object that can return the location of
   *                any SAX document event.
   * @see org.xml.sax.Locator
   */
  public void setDocumentLocator(Locator locator)
  {

    // No action for the moment.
  }

  /**
   * Receive notification of the beginning of a document.
   *
   * <p>The SAX parser will invoke this method only once, before any
   * other methods in this interface or in DTDHandler (except for
   * setDocumentLocator).</p>
   *
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   *
   * @throws org.xml.sax.SAXException
   */
  public void startDocument() throws org.xml.sax.SAXException
  {

    // No action for the moment.
  }

  /**
   * Receive notification of the end of a document.
   *
   * <p>The SAX parser will invoke this method only once, and it will
   * be the last method invoked during the parse.  The parser shall
   * not invoke this method until it has either abandoned parsing
   * (because of an unrecoverable error) or reached the end of
   * input.</p>
   *
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   *
   * @throws org.xml.sax.SAXException
   */
  public void endDocument() throws org.xml.sax.SAXException
  {
    flushWriter();
  }

  /**
   * Receive notification of the beginning of an element.
   *
   * <p>The Parser will invoke this method at the beginning of every
   * element in the XML document; there will be a corresponding
   * endElement() event for every startElement() event (even when the
   * element is empty). All of the element's content will be
   * reported, in order, before the corresponding endElement()
   * event.</p>
   *
   * <p>If the element name has a namespace prefix, the prefix will
   * still be attached.  Note that the attribute list provided will
   * contain only attributes with explicit values (specified or
   * defaulted): #IMPLIED attributes will be omitted.</p>
   *
   *
   * @param namespaceURI The Namespace URI, or the empty string if the
   *        element has no Namespace URI or if Namespace
   *        processing is not being performed.
   * @param localName The local name (without prefix), or the
   *        empty string if Namespace processing is not being
   *        performed.
   * @param name The qualified name (with prefix), or the
   *        empty string if qualified names are not available.
   * @param atts The attributes attached to the element, if any.
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #endElement
   * @see org.xml.sax.AttributeList
   *
   * @throws org.xml.sax.SAXException
   */
  public void startElement(
          String namespaceURI, String localName, String name, Attributes atts)
            throws org.xml.sax.SAXException
  {

    // No action for the moment.
  }

  /**
   * Receive notification of the end of an element.
   *
   * <p>The SAX parser will invoke this method at the end of every
   * element in the XML document; there will be a corresponding
   * startElement() event for every endElement() event (even when the
   * element is empty).</p>
   *
   * <p>If the element name has a namespace prefix, the prefix will
   * still be attached to the name.</p>
   *
   *
   * @param namespaceURI The Namespace URI, or the empty string if the
   *        element has no Namespace URI or if Namespace
   *        processing is not being performed.
   * @param localName The local name (without prefix), or the
   *        empty string if Namespace processing is not being
   *        performed.
   * @param name The qualified name (with prefix), or the
   *        empty string if qualified names are not available.
   * @param name The element type name
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   *
   * @throws org.xml.sax.SAXException
   */
  public void endElement(String namespaceURI, String localName, String name)
          throws org.xml.sax.SAXException
  {

    // No action for the moment.
  }

  /**
   * Receive notification of character data.
   *
   * <p>The Parser will call this method to report each chunk of
   * character data.  SAX parsers may return all contiguous character
   * data in a single chunk, or they may split it into several
   * chunks; however, all of the characters in any single event
   * must come from the same external entity, so that the Locator
   * provides useful information.</p>
   *
   * <p>The application must not attempt to read from the array
   * outside of the specified range.</p>
   *
   * <p>Note that some parsers will report whitespace using the
   * ignorableWhitespace() method rather than this one (validating
   * parsers must do so).</p>
   *
   * @param ch The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #ignorableWhitespace
   * @see org.xml.sax.Locator
   */
  public void characters(char ch[], int start, int length)
          throws org.xml.sax.SAXException
  {

    // this.accum(ch, start, length);
    try
    {
      writeNormalizedChars(ch, start, length, false);
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }
    this.flushWriter();

    // flushWriter();
  }

  /**
   * If available, when the disable-output-escaping attribute is used,
   * output raw text without escaping.
   *
   * @param ch The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   *
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  public void charactersRaw(char ch[], int start, int length)
          throws org.xml.sax.SAXException
  {

    // accum(ch, start, length);
    try
    {
      writeNormalizedChars(ch, start, length, false);
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }
    flushWriter();

    // flushWriter();
  }
  
  /**
   * Once a surrogate has been detected, write the pair as a single
   * character reference.
   *
   * @param c the first part of the surrogate.
   * @param ch Character array.
   * @param i position Where the surrogate was detected.
   * @param end The end index of the significant characters.
   * @return i+1.
   * @throws IOException
   * @throws org.xml.sax.SAXException if invalid UTF-16 surrogate detected.
   */
  protected int writeUTF16Surrogate(char c, char ch[], int i, int end)
          throws IOException, org.xml.sax.SAXException
  {

    // UTF-16 surrogate
    int surrogateValue = getURF16SurrogateValue(c, ch, i, end);

    i++;

    // m_writer.write('x');
    m_writer.write(surrogateValue);

    return i;
  }

  
  /**
   * Normalize the characters, but don't escape.  Different from 
   * SerializerToXML#writeNormalizedChars because it does not attempt to do 
   * XML escaping at all.
   *
   * @param ch The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   * @param isCData true if a CDATA block should be built around the characters.
   *
   * @throws IOException
   * @throws org.xml.sax.SAXException
   */
  void writeNormalizedChars(char ch[], int start, int length, boolean isCData)
          throws IOException, org.xml.sax.SAXException
  {

    int end = start + length;

    for (int i = start; i < end; i++)
    {
      char c = ch[i];

      if (CharInfo.S_LINEFEED == c)
      {
        m_writer.write(m_lineSep, 0, m_lineSepLen);
      }
      else if (isCData && (c > m_maxCharacter))
      {
        if (i != 0)
          m_writer.write("]]>");

        // This needs to go into a function... 
        if (isUTF16Surrogate(c))
        {
          i = writeUTF16Surrogate(c, ch, i, end);
        }
        else
        {
          m_writer.write(c);
        }

        if ((i != 0) && (i < (end - 1)))
          m_writer.write("<![CDATA[");
      }
      else if (isCData
               && ((i < (end - 2)) && (']' == c) && (']' == ch[i + 1])
                   && ('>' == ch[i + 2])))
      {
        m_writer.write("]]]]><![CDATA[>");

        i += 2;
      }
      else
      {
        if (c <= m_maxCharacter)
        {
          m_writer.write(c);
        }

        else if (isUTF16Surrogate(c))
        {
          i = writeUTF16Surrogate(c, ch, i, end);
        }
        else
        {
          m_writer.write(c);
        }
      }
    }
  }

  /**
   * Receive notification of cdata.
   *
   * <p>The Parser will call this method to report each chunk of
   * character data.  SAX parsers may return all contiguous character
   * data in a single chunk, or they may split it into several
   * chunks; however, all of the characters in any single event
   * must come from the same external entity, so that the Locator
   * provides useful information.</p>
   *
   * <p>The application must not attempt to read from the array
   * outside of the specified range.</p>
   *
   * <p>Note that some parsers will report whitespace using the
   * ignorableWhitespace() method rather than this one (validating
   * parsers must do so).</p>
   *
   * @param ch The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #ignorableWhitespace
   * @see org.xml.sax.Locator
   */
  public void cdata(char ch[], int start, int length)
          throws org.xml.sax.SAXException
  {

    // accum(ch, start, length);
    try
    {
      writeNormalizedChars(ch, start, length, false);
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }
    flushWriter();

    // flushWriter();
  }

  /**
   * Receive notification of ignorable whitespace in element content.
   *
   * <p>Validating Parsers must use this method to report each chunk
   * of ignorable whitespace (see the W3C XML 1.0 recommendation,
   * section 2.10): non-validating parsers may also use this method
   * if they are capable of parsing and using content models.</p>
   *
   * <p>SAX parsers may return all contiguous whitespace in a single
   * chunk, or they may split it into several chunks; however, all of
   * the characters in any single event must come from the same
   * external entity, so that the Locator provides useful
   * information.</p>
   *
   * <p>The application must not attempt to read from the array
   * outside of the specified range.</p>
   *
   * @param ch The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #characters
   *
   * @throws org.xml.sax.SAXException
   */
  public void ignorableWhitespace(char ch[], int start, int length)
          throws org.xml.sax.SAXException
  {

    try
    {
      writeNormalizedChars(ch, start, length, false);
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }
    flushWriter();
  }

  /**
   * Receive notification of a processing instruction.
   *
   * <p>The Parser will invoke this method once for each processing
   * instruction found: note that processing instructions may occur
   * before or after the main document element.</p>
   *
   * <p>A SAX parser should never report an XML declaration (XML 1.0,
   * section 2.8) or a text declaration (XML 1.0, section 4.3.1)
   * using this method.</p>
   *
   * @param target The processing instruction target.
   * @param data The processing instruction data, or null if
   *        none was supplied.
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   *
   * @throws org.xml.sax.SAXException
   */
  public void processingInstruction(String target, String data)
          throws org.xml.sax.SAXException
  {

    // No action for the moment.
  }

  /**
   * Called when a Comment is to be constructed.
   * Note that Xalan will normally invoke the other version of this method.
   * %REVIEW% In fact, is this one ever needed, or was it a mistake?
   *
   * @param   data  The comment data.
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  public void comment(String data) throws org.xml.sax.SAXException
  {
    // No action for the moment.
  }

  /**
   * Report an XML comment anywhere in the document.
   *
   * This callback will be used for comments inside or outside the
   * document element, including comments in the external DTD
   * subset (if read).
   *
   * @param ch An array holding the characters in the comment.
   * @param start The starting position in the array.
   * @param length The number of characters to use from the array.
   * @throws org.xml.sax.SAXException The application may raise an exception.
   */
  public void comment(char ch[], int start, int length)
          throws org.xml.sax.SAXException
  {
    // No action for the moment.
  }

  /**
   * Receive notivication of a entityReference.
   *
   * @param name non-null reference to the name of the entity.
   *
   * @throws org.xml.sax.SAXException
   */
  public void entityReference(String name) throws org.xml.sax.SAXException
  {

    // No action for the moment.
  }
}
