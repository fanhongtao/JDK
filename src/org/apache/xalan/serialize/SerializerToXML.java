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

import java.io.Writer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.IOException;

import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Properties;
import java.util.BitSet;

import org.xml.sax.*;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.DeclHandler;

import org.w3c.dom.Node;

import org.apache.xalan.serialize.Serializer;
import org.apache.xalan.serialize.DOMSerializer;
import org.apache.xml.utils.QName;
import org.apache.xalan.templates.OutputProperties;
import org.apache.xml.utils.BoolStack;
import org.apache.xml.utils.TreeWalker;
import org.apache.xml.utils.WrappedRuntimeException;
import org.apache.xml.utils.SystemIDResolver;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.res.XSLMessages;
import org.apache.xpath.res.XPATHErrorResources;

import javax.xml.transform.Result;
import javax.xml.transform.OutputKeys;

/**
 * <meta name="usage" content="general"/>
 * SerializerToXML formats SAX-style events into XML.
 */
public class SerializerToXML
        implements ContentHandler, LexicalHandler, DeclHandler, Serializer,
                   DOMSerializer
{

  /**
   * The writer where the XML will be written.
   */
  protected Writer m_writer = null;

  /** True if we control the buffer, and we should flush the output on endDocument. */
  boolean m_shouldFlush = true;

  //  /** The output stream where the result stream is written. */
  //  protected OutputStream m_outputStream = System.out;

  /**
   * The character encoding.  Must match the encoding used for the printWriter.
   */
  protected String m_encoding = null;

  /**
   * Assume java encoding names are the same as the ISO encoding names if this is true.
   */
  static boolean javaEncodingIsISO = false;

  /**
   * Tells if we should write the XML declaration.
   */
  public boolean m_shouldNotWriteXMLHeader = false;

  /**
   * Tells the XML version, for writing out to the XML decl.
   */
  public String m_version = null;

  /**
   * A stack of Boolean objects that tell if the given element
   * has children.
   */
  protected BoolStack m_elemStack = new BoolStack();

  /** Stack to keep track of disabling output escaping. */
  protected BoolStack m_disableOutputEscapingStates = new BoolStack();

  /** True will be pushed, if characters should be in CDATA section blocks. */
  protected BoolStack m_cdataSectionStates = new BoolStack();

  /** List of QNames obtained from the xsl:output properties. */
  protected Vector m_cdataSectionNames = null;

  /** True if the current characters should be in CDATA blocks. */
  protected boolean m_inCData = false;

  /**
   * Tell if the character escaping should be disabled for the current state.
   *
   * @return true if the character escaping should be disabled.
   */
  protected boolean isEscapingDisabled()
  {
    return m_disableOutputEscapingStates.peekOrFalse();
  }

  /**
   * Tell if the characters in the current state should be put in
   * cdata section blocks.
   *
   * @return true if the characters in the current state should be put in
   * cdata section blocks.
   */
  protected boolean isCDataSection()
  {
    return m_inCData || m_cdataSectionStates.peekOrFalse();
  }

  /**
   * Use the system line seperator to write line breaks.
   */
  protected final char[] m_lineSep =
    System.getProperty("line.separator").toCharArray();

  /**
   * The length of the line seperator, since the write is done
   * one character at a time.
   */
  protected final int m_lineSepLen = m_lineSep.length;

  /**
   * Output a system-dependent line break.
   *
   * @throws org.xml.sax.SAXException
   */
  protected final void outputLineSep() throws org.xml.sax.SAXException
  {

    try
    {
      m_writer.write(m_lineSep, 0, m_lineSepLen);
    }
    catch (IOException ioe)
    {
      throw new SAXException(ioe);
    }
  }

  /**
   * State flag to tell if preservation of whitespace
   * is important.
   */
  protected boolean m_ispreserve = false;

  /**
   * Stack to keep track of whether or not we need to
   * preserve whitespace.
   */
  protected BoolStack m_preserves = new BoolStack();

  /**
   * State flag that tells if the previous node processed
   * was text, so we can tell if we should preserve whitespace.
   */
  protected boolean m_isprevtext = false;

  /**
   * Flag to tell if indenting (pretty-printing) is on.
   */
  protected boolean m_doIndent = false;

  /**
   * Flag to keep track of the indent amount.
   */
  protected int m_currentIndent = 0;

  /**
   * Amount to indent.
   */
  public int m_indentAmount = 0;

  /**
   * Current level of indent.
   */
  protected int level = 0;

  /**
   * Flag to signal that a newline should be added.
   */
  boolean m_startNewLine;

  /**
   * Flag to tell that we need to add the doctype decl,
   * which we can't do until the first element is
   * encountered.
   */
  boolean m_needToOutputDocTypeDecl = true;

  /**
   * The System ID for the doc type.
   */
  String m_doctypeSystem;

  /**
   * The public ID for the doc type.
   */
  String m_doctypePublic;

  /**
   * The standalone value for the doctype.
   */
  boolean m_standalone = false;

  /**
   * True if standalone was specified.
   */
  boolean m_standaloneWasSpecified = false;

  /**
   * The mediatype.  Not used right now.
   */
  String m_mediatype;

  /**
   * Tells if we're in an EntityRef event.
   */
  protected boolean m_inEntityRef = false;

  /**
   * Tells if we're in an internal document type subset.
   */
  private boolean m_inDoctype = false;

  /**
   * Map that tells which XML characters should have special treatment, and it
   *  provides character to entity name lookup.
   */
  protected static CharInfo m_xmlcharInfo =
    new CharInfo(CharInfo.XML_ENTITIES_RESOURCE);

  /**
   * Map that tells which characters should have special treatment, and it
   *  provides character to entity name lookup.
   */
  protected CharInfo m_charInfo;

  /** Table of user-specified char infos. */
  private static Hashtable m_charInfos = null;

  /**
   * Flag to quickly tell if the encoding is UTF8.
   */
  boolean m_isUTF8 = false;

  /**
   * The maximum character size before we have to resort
   * to escaping.
   */
  protected int m_maxCharacter = Encodings.getLastPrintable();

  /**
   * Add space before '/>' for XHTML.
   */
  public boolean m_spaceBeforeClose = false;

  /** The xsl:output properties. */
  protected Properties m_format;

  /** Indicate whether running in Debug mode */
  private static final boolean DEBUG = false;

  /** This flag is set while receiving events from the external DTD */
  private boolean m_inExternalDTD = false;

  /**
   * Default constructor.
   */
  public SerializerToXML()
  {
    m_charInfo = m_xmlcharInfo;
  }

  /**
   * Copy properties from another SerializerToXML.
   *
   * @param xmlListener non-null reference to a SerializerToXML object.
   */
  public void CopyFrom(SerializerToXML xmlListener)
  {

    m_writer = xmlListener.m_writer;

    // m_outputStream = xmlListener.m_outputStream;
    m_encoding = xmlListener.m_encoding;
    javaEncodingIsISO = xmlListener.javaEncodingIsISO;
    m_shouldNotWriteXMLHeader = xmlListener.m_shouldNotWriteXMLHeader;

    // m_shouldNotWriteXMLHeader = xmlListener.m_shouldNotWriteXMLHeader;
    m_elemStack = xmlListener.m_elemStack;

    // m_lineSep = xmlListener.m_lineSep;
    // m_lineSepLen = xmlListener.m_lineSepLen;
    m_ispreserve = xmlListener.m_ispreserve;
    m_preserves = xmlListener.m_preserves;
    m_isprevtext = xmlListener.m_isprevtext;
    m_doIndent = xmlListener.m_doIndent;
    m_currentIndent = xmlListener.m_currentIndent;
    m_indentAmount = xmlListener.m_indentAmount;
    level = xmlListener.level;
    m_startNewLine = xmlListener.m_startNewLine;
    m_needToOutputDocTypeDecl = xmlListener.m_needToOutputDocTypeDecl;
    m_doctypeSystem = xmlListener.m_doctypeSystem;
    m_doctypePublic = xmlListener.m_doctypePublic;
    m_standalone = xmlListener.m_standalone;
    m_mediatype = xmlListener.m_mediatype;
    m_maxCharacter = xmlListener.m_maxCharacter;
    m_spaceBeforeClose = xmlListener.m_spaceBeforeClose;
    m_inCData = xmlListener.m_inCData;

    // m_pos = xmlListener.m_pos;
    m_pos = 0;
  }

  /**
   * Initialize the serializer with the specified writer and output format.
   * Must be called before calling any of the serialize methods.
   *
   * @param writer The writer to use
   * @param format The output format
   */
  public synchronized void init(Writer writer, Properties format)
  {
    init(writer, format, false);
  }

  /**
   * Initialize the serializer with the specified writer and output format.
   * Must be called before calling any of the serialize methods.
   *
   * @param writer The writer to use
   * @param format The output format
   * @param shouldFlush True if the writer should be flushed at EndDocument.
   */
  private synchronized void init(Writer writer, Properties format,
                                 boolean shouldFlush)
  {

    m_shouldFlush = shouldFlush;
    m_writer = writer;
    m_format = format;
    m_cdataSectionNames =
      OutputProperties.getQNameProperties(OutputKeys.CDATA_SECTION_ELEMENTS,
                                          format);
    m_indentAmount =
      OutputProperties.getIntProperty(OutputProperties.S_KEY_INDENT_AMOUNT,
                                      format);
    m_doIndent = OutputProperties.getBooleanProperty(OutputKeys.INDENT,
            format);
    m_shouldNotWriteXMLHeader =
      OutputProperties.getBooleanProperty(OutputKeys.OMIT_XML_DECLARATION,
                                          format);
    m_doctypeSystem = format.getProperty(OutputKeys.DOCTYPE_SYSTEM);
    m_doctypePublic = format.getProperty(OutputKeys.DOCTYPE_PUBLIC);
    m_standaloneWasSpecified = (null != format.get(OutputKeys.STANDALONE));
    m_standalone = OutputProperties.getBooleanProperty(OutputKeys.STANDALONE,
            format);
    m_mediatype = format.getProperty(OutputKeys.MEDIA_TYPE);

    if (null != m_doctypePublic)
    {
      if (m_doctypePublic.startsWith("-//W3C//DTD XHTML"))
        m_spaceBeforeClose = true;
    }

    // initCharsMap();
    if (null == m_encoding)
      m_encoding =
        Encodings.getMimeEncoding(format.getProperty(OutputKeys.ENCODING));

    m_isUTF8 = m_encoding.equals(Encodings.DEFAULT_MIME_ENCODING);
    m_maxCharacter = Encodings.getLastPrintable(m_encoding);

    // Access this only from the Hashtable level... we don't want to 
    // get default properties.
    String entitiesFileName =
      (String) format.get(OutputProperties.S_KEY_ENTITIES);

    if (null != entitiesFileName)
    {
      try
      {
        m_charInfo = null;

        if (null == m_charInfos)
        {
          synchronized (m_xmlcharInfo)
          {
            if (null == m_charInfos)  // secondary check
              m_charInfos = new Hashtable();
          }
        }
        else
        {
          m_charInfo = (CharInfo) m_charInfos.get(entitiesFileName);
        }

        if (null == m_charInfo)
        {
          try
          {
            m_charInfo = new CharInfo(entitiesFileName);
          }
          catch(Exception e)
          {
          	m_charInfo = null;
          }
        }

        if (null == m_charInfo)
        {
          String absoluteEntitiesFileName;

          if (entitiesFileName.indexOf(':') < 0)
          {
            absoluteEntitiesFileName =
              SystemIDResolver.getAbsoluteURIFromRelative(entitiesFileName);
          }
          else
          {
            absoluteEntitiesFileName =
              SystemIDResolver.getAbsoluteURI(entitiesFileName, null);
          }

          m_charInfo = new CharInfo(absoluteEntitiesFileName);

          m_charInfos.put(entitiesFileName, m_charInfo);
        }
      }
      catch (javax.xml.transform.TransformerException te)
      {
        throw new org.apache.xml.utils.WrappedRuntimeException(te);
      }
    }
  }

  /**
   * Initialize the serializer with the specified output stream and output format.
   * Must be called before calling any of the serialize methods.
   *
   * @param output The output stream to use
   * @param format The output format
   * @throws UnsupportedEncodingException The encoding specified
   *   in the output format is not supported
   */
  public synchronized void init(OutputStream output, Properties format)
          throws UnsupportedEncodingException
  {

    if (null == format)
    {
      OutputProperties op = new OutputProperties(Method.XML);

      format = op.getProperties();
    }

    m_encoding =
      Encodings.getMimeEncoding(format.getProperty(OutputKeys.ENCODING));

    if (m_encoding.equalsIgnoreCase("UTF-8"))
    {
      m_isUTF8 = true;
      if(output instanceof java.io.BufferedOutputStream)
      {
        init(new WriterToUTF8(output), format, true);
      }
      else if(output instanceof java.io.FileOutputStream)
      {
        init(new WriterToUTF8Buffered(output), format, true);
      }
      else
      {
        // Not sure what to do in this case.  I'm going to be conservative 
        // and not buffer.
        init(new WriterToUTF8(output), format, true);
      }
      
    }
    else if (m_encoding.equals("WINDOWS-1250")
             || m_encoding.equals("US-ASCII") || m_encoding.equals("ASCII"))
    {
      init(new WriterToASCI(output), format, true);
    }
    else
    {
      Writer osw;

      try
      {
        osw = Encodings.getWriter(output, m_encoding);
      }
      catch (UnsupportedEncodingException uee)
      {
        System.out.println("Warning: encoding \"" + m_encoding
                           + "\" not supported" + ", using "
                           + Encodings.DEFAULT_MIME_ENCODING);

        m_encoding = Encodings.DEFAULT_MIME_ENCODING;
        osw = Encodings.getWriter(output, m_encoding);
      }

      m_maxCharacter = Encodings.getLastPrintable(m_encoding);

      init(osw, format, true);
    }
    
  }

  /**
   * Receive an object for locating the origin of SAX document events.
   *
   * @param locator An object that can return the location of
   *                any SAX document event.
   * @see org.xml.sax.Locator
   */
  public void setDocumentLocator(Locator locator)
  {

    // I don't do anything with this yet.
  }

  /**
   * Output the doc type declaration.
   *
   * @param name non-null reference to document type name.
   * NEEDSDOC @param closeDecl
   *
   * @throws org.xml.sax.SAXException
   */
  void outputDocTypeDecl(String name, boolean closeDecl)
          throws org.xml.sax.SAXException
  {
    try
    {
      final Writer writer = m_writer;
  
      writer.write("<!DOCTYPE ");
      writer.write(name);
  
      if (null != m_doctypePublic)
      {
        writer.write(" PUBLIC \"");
        writer.write(m_doctypePublic);
        writer.write('\"');
      }
  
      if (null != m_doctypeSystem)
      {
        if (null == m_doctypePublic)
          writer.write(" SYSTEM \"");
        else
          writer.write(" \"");
  
        writer.write(m_doctypeSystem);
  
        if (closeDecl)
        {
          writer.write("\">");
          writer.write(m_lineSep, 0, m_lineSepLen);;
        }
        else
          writer.write('\"');
      }
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }
  }

  /**
   * Output the doc type declaration.
   *
   * @param name non-null reference to document type name.
   * NEEDSDOC @param value
   *
   * @throws org.xml.sax.SAXException
   */
  void outputEntityDecl(String name, String value)
          throws org.xml.sax.SAXException
  {

    try
    {
      final Writer writer = m_writer;
      writer.write("<!ENTITY ");
      writer.write(name);
      writer.write(" \"");
      writer.write(value);
      writer.write("\">");
      writer.write(m_lineSep, 0, m_lineSepLen);
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }
  }

  /**
   * Receive notification of the beginning of a document.
   *
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   *
   * @throws org.xml.sax.SAXException
   */
  public void startDocument() throws org.xml.sax.SAXException
  {

    if (m_inEntityRef)
      return;

    m_needToOutputDocTypeDecl = true;
    m_startNewLine = false;

    if (m_shouldNotWriteXMLHeader == false)
    {
      String encoding = Encodings.getMimeEncoding(m_encoding);
      String version = (null == m_version) ? "1.0" : m_version;
      String standalone;

      if (m_standaloneWasSpecified)
      {
        standalone = " standalone=\"" + (m_standalone ? "yes" : "no") + "\"";
      }
      else
      {
        standalone = "";
      }

      try
      {
        final Writer writer = m_writer;
        writer.write("<?xml version=\"");
        writer.write(version);
        writer.write("\" encoding=\"");
        writer.write(encoding);
        writer.write('\"');
        writer.write(standalone);
        writer.write("?>");
        writer.write(m_lineSep, 0, m_lineSepLen);
      }
      catch(IOException ioe)
      {
        throw new SAXException(ioe);
      }
    }
  }

  /**
   * Receive notification of the end of a document.
   *
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   *
   * @throws org.xml.sax.SAXException
   */
  public void endDocument() throws org.xml.sax.SAXException
  {

    if (m_doIndent &&!m_isprevtext)
    {
      outputLineSep();
    }

    flushWriter();
  }

  /**
   * Report the start of DTD declarations, if any.
   *
   * Any declarations are assumed to be in the internal subset
   * unless otherwise indicated.
   *
   * @param name The document type name.
   * @param publicId The declared public identifier for the
   *        external DTD subset, or null if none was declared.
   * @param systemId The declared system identifier for the
   *        external DTD subset, or null if none was declared.
   * @throws org.xml.sax.SAXException The application may raise an
   *            exception.
   * @see #endDTD
   * @see #startEntity
   */
  public void startDTD(String name, String publicId, String systemId)
          throws org.xml.sax.SAXException
  {
    m_doctypeSystem = systemId;
    m_doctypePublic = publicId;

    if ((true == m_needToOutputDocTypeDecl))  // && (null != m_doctypeSystem))
    {
      outputDocTypeDecl(name, false);
    }

    m_needToOutputDocTypeDecl = false;
    m_inDoctype = true;
  }

  /**
   * Report the end of DTD declarations.
   *
   * @throws org.xml.sax.SAXException The application may raise an exception.
   * @see #startDTD
   */
  public void endDTD() throws org.xml.sax.SAXException
  {
    try
    {
      if (!m_inDoctype)
        m_writer.write("]>");
      else
      {
        m_writer.write('>');
      }
  
      m_writer.write(m_lineSep, 0, m_lineSepLen);
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }

    // Do nothing for now.
  }

  /**
   * Begin the scope of a prefix-URI Namespace mapping.
   * @see org.xml.sax.ContentHandler#startPrefixMapping
   *
   * @param prefix The Namespace prefix being declared.
   * @param uri The Namespace URI the prefix is mapped to.
   * @throws org.xml.sax.SAXException The client may throw
   *            an exception during processing.
   */
  public void startPrefixMapping(String prefix, String uri)
          throws org.xml.sax.SAXException{}

  /**
   * End the scope of a prefix-URI Namespace mapping.
   * @see org.xml.sax.ContentHandler#endPrefixMapping
   *
   * @param prefix The prefix that was being mapping.
   * @throws org.xml.sax.SAXException The client may throw
   *            an exception during processing.
   */
  public void endPrefixMapping(String prefix)
          throws org.xml.sax.SAXException{}

  /**
   * Tell if two strings are equal, without worry if the first string is null.
   *
   * @param p String reference, which may be null.
   * @param t String reference, which may be null.
   *
   * @return true if strings are equal.
   */
  protected static final boolean subPartMatch(String p, String t)
  {
    return (p == t) || ((null != p) && (p.equals(t)));
  }

  /**
   * Push a boolean state based on if the name of the element
   * is found in the list of qnames.  A state is always pushed,
   * one way or the other.
   *
   * @param namespaceURI Should be a non-null reference to the namespace URL
   *        of the element that owns the state, or empty string.
   * @param localName Should be a non-null reference to the local name
   *        of the element that owns the state.
   * @param qnames Vector of qualified names of elements, or null.
   * @param state The stack where the state should be pushed.
   */
  protected void pushState(String namespaceURI, String localName,
                           Vector qnames, BoolStack state)
  {

    boolean b;

    if (null != qnames)
    {
      b = false;

      if ((null != namespaceURI) && namespaceURI.length() == 0)
        namespaceURI = null;

      int nElems = qnames.size();

      for (int i = 0; i < nElems; i++)
      {
        QName q = (QName) qnames.elementAt(i);

        if (q.getLocalName().equals(localName)
                && subPartMatch(namespaceURI, q.getNamespaceURI()))
        {
          b = true;

          break;
        }
      }
    }
    else
    {
      b = state.peekOrFalse();
    }

    state.push(b);
  }

  /**
   * Receive notification of the beginning of an element.
   *
   *
   * @param namespaceURI The Namespace URI, or the empty string if the
   *        element has no Namespace URI or if Namespace
   *        processing is not being performed.
   * @param localName The local name (without prefix), or the
   *        empty string if Namespace processing is not being
   *        performed.
   * @param name The element type name.
   * @param atts The attributes attached to the element, if any.
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#startElement
   * @see org.xml.sax.ContentHandler#endElement
   * @see org.xml.sax.AttributeList
   *
   * @throws org.xml.sax.SAXException
   */
  public void startElement(
          String namespaceURI, String localName, String name, Attributes atts)
            throws org.xml.sax.SAXException
  {

    if (DEBUG)
    {
      System.out.println("SerializerToXML - startElement: " + namespaceURI
                         + ", " + localName);

      int n = atts.getLength();

      for (int i = 0; i < n; i++)
      {
        System.out.println("atts[" + i + "]: " + atts.getQName(i) + " = "
                           + atts.getValue(i));
      }

      if (null == namespaceURI)
      {
        (new RuntimeException(localName
                              + " has a null namespace!")).printStackTrace();
      }
    }

    if (m_inEntityRef)
      return;

    if ((true == m_needToOutputDocTypeDecl) && (null != m_doctypeSystem))
    {
      outputDocTypeDecl(name, true);
    }

    m_needToOutputDocTypeDecl = false;

    writeParentTagEnd();
    pushState(namespaceURI, localName, m_cdataSectionNames,
              m_cdataSectionStates);

    // pushState(namespaceURI, localName, m_format.getNonEscapingElements(),
    //          m_disableOutputEscapingStates);
    m_ispreserve = false;

    //  System.out.println(name+": m_doIndent = "+m_doIndent+", m_ispreserve = "+m_ispreserve+", m_isprevtext = "+m_isprevtext);
    if (shouldIndent() && m_startNewLine)
    {
      indent(m_currentIndent);
    }

    m_startNewLine = true;

    try
    {
      m_writer.write('<');
      m_writer.write(name);
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }

    int nAttrs = atts.getLength();

    for (int i = 0; i < nAttrs; i++)
    {
      processAttribute(atts.getQName(i), atts.getValue(i));
    }

    // Flag the current element as not yet having any children.
    openElementForChildren();

    m_currentIndent += m_indentAmount;
    m_isprevtext = false;
  }

  /**
   * Check to see if a parent's ">" has been written, and, if
   * it has not, write it.
   *
   * @throws org.xml.sax.SAXException
   */
  protected void writeParentTagEnd() throws org.xml.sax.SAXException
  {

    // See if the parent element has already been flagged as having children.
    if (!m_elemStack.peekOrTrue())
    {
      try
      {
        m_writer.write('>');
      }
      catch(IOException ioe)
      {
        throw new SAXException(ioe);
      }

      m_isprevtext = false;

      m_elemStack.setTop(true);
      m_preserves.push(m_ispreserve);
    }
  }

  /**
   * Flag the current element as not yet having any
   * children.
   */
  protected void openElementForChildren()
  {

    // Flag the current element as not yet having any children.
    m_elemStack.push(false);
  }

  /**
   * Tell if child nodes have been added to the current
   * element.  Must be called in balance with openElementForChildren().
   *
   * @return true if child nodes were added.
   */
  protected boolean childNodesWereAdded()
  {
    return m_elemStack.isEmpty() ? false : m_elemStack.pop();
  }

  /**
   * Receive notification of the end of an element.
   *
   *
   * @param namespaceURI The Namespace URI, or the empty string if the
   *        element has no Namespace URI or if Namespace
   *        processing is not being performed.
   * @param localName The local name (without prefix), or the
   *        empty string if Namespace processing is not being
   *        performed.
   * @param name The element type name
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   *
   * @throws org.xml.sax.SAXException
   */
  public void endElement(String namespaceURI, String localName, String name)
          throws org.xml.sax.SAXException
  {

    if (m_inEntityRef)
      return;

    m_currentIndent -= m_indentAmount;

    boolean hasChildNodes = childNodesWereAdded();

    try
    {
      final Writer writer = m_writer;
      if (hasChildNodes)
      {
        if (shouldIndent())
          indent(m_currentIndent);
  
        writer.write('<');
        writer.write('/');
        writer.write(name);
        writer.write('>');
      }
      else
      {
        if (m_spaceBeforeClose)
          writer.write(" />");
        else
          writer.write("/>");
      }
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }

    if (hasChildNodes)
    {
      m_ispreserve = m_preserves.isEmpty() ? false : m_preserves.pop();
    }

    m_isprevtext = false;

    // m_disableOutputEscapingStates.pop();
    m_cdataSectionStates.pop();
  }

  /**
   * Process an attribute.
   * @param   name   The name of the attribute.
   * @param   value   The value of the attribute.
   *
   * @throws org.xml.sax.SAXException
   */
  protected void processAttribute(String name, String value)
          throws org.xml.sax.SAXException
  {
    try
    {
      final Writer writer = m_writer;
      writer.write(' ');
      writer.write(name);
      writer.write("=\"");
      writeAttrString(value, m_encoding);
      writer.write('\"');
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }
  }

  /**
   * Starts an un-escaping section. All characters printed within an
   * un-escaping section are printed as is, without escaping special
   * characters into entity references. Only XML and HTML serializers
   * need to support this method.
   * <p>
   * The contents of the un-escaping section will be delivered through
   * the regular <tt>characters</tt> event.
   *
   * @throws org.xml.sax.SAXException
   */
  public void startNonEscaping() throws org.xml.sax.SAXException
  {
    m_disableOutputEscapingStates.push(true);
  }

  /**
   * Ends an un-escaping section.
   *
   * @see #startNonEscaping
   *
   * @throws org.xml.sax.SAXException
   */
  public void endNonEscaping() throws org.xml.sax.SAXException
  {
    m_disableOutputEscapingStates.pop();
  }

  /**
   * Starts a whitespace preserving section. All characters printed
   * within a preserving section are printed without indentation and
   * without consolidating multiple spaces. This is equivalent to
   * the <tt>xml:space=&quot;preserve&quot;</tt> attribute. Only XML
   * and HTML serializers need to support this method.
   * <p>
   * The contents of the whitespace preserving section will be delivered
   * through the regular <tt>characters</tt> event.
   *
   * @throws org.xml.sax.SAXException
   */
  public void startPreserving() throws org.xml.sax.SAXException
  {

    // Not sure this is really what we want.  -sb
    m_preserves.push(true);

    m_ispreserve = true;
  }

  /**
   * Ends a whitespace preserving section.
   *
   * @see #startPreserving
   *
   * @throws org.xml.sax.SAXException
   */
  public void endPreserving() throws org.xml.sax.SAXException
  {

    // Not sure this is really what we want.  -sb
    m_ispreserve = m_preserves.isEmpty() ? false : m_preserves.pop();
  }

  /**
   * Receive notification of a processing instruction.
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
    if (m_inEntityRef)
      return;

    if (target.equals(Result.PI_DISABLE_OUTPUT_ESCAPING))
    {
      startNonEscaping();
    }
    else if (target.equals(Result.PI_ENABLE_OUTPUT_ESCAPING))
    {
      endNonEscaping();
    }
    else
    {
      try
      {
        final Writer writer = m_writer;
        writeParentTagEnd();
  
        if (shouldIndent())
          indent(m_currentIndent);
  
        writer.write('<');
        writer.write('?');
        writer.write(target);
  
        if (data.length() > 0 &&!Character.isSpaceChar(data.charAt(0)))
          writer.write(' ');
  
        int indexOfQLT = data.indexOf("?>");
  
        if (indexOfQLT >= 0)
        {
  
          // See XSLT spec on error recovery of "?>" in PIs.
          if (indexOfQLT > 0)
          {
            writer.write(data.substring(0, indexOfQLT));
          }
  
          writer.write("? >");  // add space between.
  
          if ((indexOfQLT + 2) < data.length())
          {
            writer.write(data.substring(indexOfQLT + 2));
          }
        }
        else
        {
          writer.write(data);
        }
  
        writer.write('?');
        writer.write('>');
  
        // Always output a newline char if not inside of an 
        // element. The whitespace is not significant in that
        // case.
        if (m_elemStack.isEmpty())
          writer.write(m_lineSep, 0, m_lineSepLen);
  
        m_startNewLine = true;
      }
      catch(IOException ioe)
      {
        throw new SAXException(ioe);
      }
    }
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

    if (m_inEntityRef)
      return;

    writeParentTagEnd();

    if (shouldIndent())
      indent(m_currentIndent);

    try
    {
      final Writer writer = m_writer;
      final int limit = start + length;
      boolean wasDash = false;
      writer.write("<!--");

      // Detect occurrences of two consecutive dashes, handle as necessary.
      for (int i = start; i < limit; i++) {
        if (wasDash && ch[i] == '-') {
          writer.write(ch, start, i - start);
          writer.write(" -");
          start = i + 1;     	  
        }
        wasDash = (ch[i] == '-');
      }      	 
      
      // Output the remaining characters.
      writer.write(ch, start, limit - start);
      // Protect comment end from a single trailing dash
      if (ch[limit-1] == '-')
        writer.write(' ');

      writer.write("-->");
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }

    m_startNewLine = true;
  }

  /**
   * Report the start of a CDATA section.
   *
   * @throws org.xml.sax.SAXException The application may raise an exception.
   * @see #endCDATA
   */
  public void startCDATA() throws org.xml.sax.SAXException
  {
    m_inCData = true;
  }

  /**
   * Report the end of a CDATA section.
   *
   * @throws org.xml.sax.SAXException The application may raise an exception.
   * @see #startCDATA
   */
  public void endCDATA() throws org.xml.sax.SAXException
  {
    m_inCData = false;
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
   *
   * @throws org.xml.sax.SAXException
   */
  public void cdata(char ch[], int start, int length)
          throws org.xml.sax.SAXException
  {

    try
    {
      writeParentTagEnd();

      m_ispreserve = true;

      if (shouldIndent())
        indent(m_currentIndent);

      boolean writeCDataBrackets = (((length >= 1) && canConvert(ch[start])));

      if (writeCDataBrackets)
      {
        m_writer.write("<![CDATA[");
      }

      // m_writer.write(ch, start, length);
      if (isEscapingDisabled())
      {
        charactersRaw(ch, start, length);
      }
      else
        writeNormalizedChars(ch, start, length, true);

      if (writeCDataBrackets)
      {
        m_writer.write("]]>");
      }
    }
    catch (IOException ioe)
    {
      throw new org.xml.sax.SAXException(
        XSLMessages.createXPATHMessage(XPATHErrorResources.ER_OIERROR, null),
        ioe);  //"IO error", ioe);
    }
  }

  /** The current position in the m_charBuf or m_byteBuf. */
  protected int m_pos = 0;

  /**
   * Append a character to the buffer.
   *
   * @param b byte to be written to result stream.
   *
   * @throws org.xml.sax.SAXException
   */
  protected final void accum(char b) throws org.xml.sax.SAXException
  {

    try
    {
      m_writer.write(b);
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }
  }
  
  /**
   * Append a character to the buffer.
   *
   * @param chars non-null reference to character array.
   * @param start Start of characters to be written.
   * @param length Number of characters to be written.
   *
   * @throws org.xml.sax.SAXException
   */
  protected final void accum(char chars[], int start, int length)
          throws org.xml.sax.SAXException
  {

    try
    {
      m_writer.write(chars, start, length);
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }
  }
  /**
   * Append a character to the buffer.
   *
   * @param s non-null reference to string to be written to the character buffer.
   *
   * @throws org.xml.sax.SAXException
   */
  protected final void accum(String s) throws org.xml.sax.SAXException
  {

    try
    {
      m_writer.write(s);
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }
  }

  /**
   * Flush the formatter's result stream.
   *
   * @throws org.xml.sax.SAXException
   */
  public final void flushWriter() throws org.xml.sax.SAXException
  {

    if (null != m_writer)
    {
      try
      {
        if (m_writer instanceof WriterToUTF8Buffered)
        {
          if(m_shouldFlush)
            ((WriterToUTF8Buffered) m_writer).flush();
          else
            ((WriterToUTF8Buffered) m_writer).flushBuffer();
        }
        if (m_writer instanceof WriterToUTF8)
        {
          if(m_shouldFlush)
            m_writer.flush(); 
        }
        else if (m_writer instanceof WriterToASCI)
        {
          if(m_shouldFlush)
            m_writer.flush();
        }
        else
        {
          // Flush always. 
          // Not a great thing if the writer was created 
          // by this class, but don't have a choice.
          m_writer.flush(); 
        }
      }
      catch (IOException ioe)
      {
        throw new org.xml.sax.SAXException(ioe);
      }
    }
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
   * @param chars The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #ignorableWhitespace
   * @see org.xml.sax.Locator
   *
   * @throws org.xml.sax.SAXException
   */
  public void characters(char chars[], int start, int length)
          throws org.xml.sax.SAXException
  {
    if(0 == length)
      return;

    //    if (m_inEntityRef)
    //      return;
    //    else 
    if (m_inCData || m_cdataSectionStates.peekOrFalse())
    {
      cdata(chars, start, length);

      return;
    }
    
    try
    {
      
      if (m_disableOutputEscapingStates.peekOrFalse())
      {
        charactersRaw(chars, start, length);
  
        return;
      }
    
      final Writer writer = m_writer;
      if (!m_elemStack.peekOrTrue())
      {
        writer.write('>');
  
        m_isprevtext = false;
  
        m_elemStack.setTop(true);
        m_preserves.push(m_ispreserve);
      }
  
      int startClean = start;
      int lengthClean = 0;
  
      // int pos = 0;
      int end = start + length;
      boolean checkWhite = true;
      final int maxCharacter = m_maxCharacter;
      final BitSet specialsMap = m_charInfo.m_specialsMap;
  
      for (int i = start; i < end; i++)
      {
        char ch = chars[i];
  
        if (checkWhite
                && ((ch > 0x20)
                    ||!((ch == 0x20) || (ch == 0x09) || (ch == 0xD)
                        || (ch == 0xA))))
        {
          m_ispreserve = true;
          checkWhite = false;
        }
  
        if ((canConvert(ch) && (!specialsMap.get(ch))) || ('"' == ch))
        {
          lengthClean++;
        }
        else
        {
          if (lengthClean > 0)
          {
            writer.write(chars, startClean, lengthClean);
  
            lengthClean = 0;
          }
  
          if (CharInfo.S_LINEFEED == ch)
          {
            writer.write(m_lineSep, 0, m_lineSepLen);
  
            startClean = i + 1;
          }
          else
          {
            startClean = accumDefaultEscape(ch, i, chars, end, false);
            i = startClean - 1;
          }
        }
      }
  
      if (lengthClean > 0)
      {
        writer.write(chars, startClean, lengthClean);
      }
    
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }


    m_isprevtext = true;
  }

  /**
   * If available, when the disable-output-escaping attribute is used,
   * output raw text without escaping.
   *
   * @param ch The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   *
   * @throws org.xml.sax.SAXException
   */
  public void charactersRaw(char ch[], int start, int length)
          throws org.xml.sax.SAXException
  {

    try
    {
      if (m_inEntityRef)
        return;
  
      writeParentTagEnd();
  
      m_ispreserve = true;
  
      m_writer.write(ch, start, length);
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }
  }

  /**
   * Return true if the character is the high member of a surrogate pair.
   *
   * NEEDSDOC @param c
   *
   * NEEDSDOC ($objectName$) @return
   */
  static final boolean isUTF16Surrogate(char c)
  {
    return (c & 0xFC00) == 0xD800;
  }

  /**
   * Once a surrogate has been detected, get the pair as a single
   * integer value.
   *
   * @param c the first part of the surrogate.
   * @param ch Character array.
   * @param i position Where the surrogate was detected.
   * @param end The end index of the significant characters.
   * @return i+1.
   * @throws org.xml.sax.SAXException if invalid UTF-16 surrogate detected.
   */
  int getURF16SurrogateValue(char c, char ch[], int i, int end)
          throws org.xml.sax.SAXException
  {

    int next;

    if (i + 1 >= end)
    {
      throw new org.xml.sax.SAXException(
        XSLMessages.createXPATHMessage(
          XPATHErrorResources.ER_INVALID_UTF16_SURROGATE,
          new Object[]{ Integer.toHexString((int) c) }));  //"Invalid UTF-16 surrogate detected: "

      //+Integer.toHexString((int)c)+ " ?");
    }
    else
    {
      next = ch[++i];

      if (!(0xdc00 <= next && next < 0xe000))
        throw new org.xml.sax.SAXException(
          XSLMessages.createXPATHMessage(
            XPATHErrorResources.ER_INVALID_UTF16_SURROGATE,
            new Object[]{
              Integer.toHexString((int) c) + " "
              + Integer.toHexString(next) }));  //"Invalid UTF-16 surrogate detected: "

      //+Integer.toHexString((int)c)+" "+Integer.toHexString(next));
      next = ((c - 0xd800) << 10) + next - 0xdc00 + 0x00010000;
    }

    return next;
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

    m_writer.write('&');
    m_writer.write('#');

    // m_writer.write('x');
    m_writer.write(Integer.toString(surrogateValue));
    m_writer.write(';');

    return i;
  }

  /**
   * Normalize the characters, but don't escape.
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
      else if (isCData && (!canConvert(c)))
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
          m_writer.write("&#");

          String intStr = Integer.toString((int) c);

          m_writer.write(intStr);
          m_writer.write(';');
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
        if (canConvert(c))
        {
          m_writer.write(c);
        }

        // This needs to go into a function... 
        else if (isUTF16Surrogate(c))
        {
          i = writeUTF16Surrogate(c, ch, i, end);
        }
        else
        {
          m_writer.write("&#");

          String intStr = Integer.toString((int) c);

          m_writer.write(intStr);
          m_writer.write(';');
        }
      }
    }
  }

  /**
   * Receive notification of ignorable whitespace in element content.
   *
   * Not sure how to get this invoked quite yet.
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

    if (0 == length)
      return;

    characters(ch, start, length);
  }

  /**
   * Receive notification of a skipped entity.
   * @see org.xml.sax.ContentHandler#skippedEntity
   *
   * @param name The name of the skipped entity.  If it is a
   *        parameter entity, the name will begin with '%', and if
   *        it is the external DTD subset, it will be the string
   *        "[dtd]".
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  public void skippedEntity(String name) throws org.xml.sax.SAXException
  {

    // TODO: Should handle
  }

  /**
   * Report the beginning of an entity.
   *
   * The start and end of the document entity are not reported.
   * The start and end of the external DTD subset are reported
   * using the pseudo-name "[dtd]".  All other events must be
   * properly nested within start/end entity events.
   *
   * @param name The name of the entity.  If it is a parameter
   *        entity, the name will begin with '%'.
   * @throws org.xml.sax.SAXException The application may raise an exception.
   * @see #endEntity
   * @see org.xml.sax.ext.DeclHandler#internalEntityDecl
   * @see org.xml.sax.ext.DeclHandler#externalEntityDecl
   */
  public void startEntity(String name) throws org.xml.sax.SAXException
  {
    if (name.equals("[dtd]")) 
      m_inExternalDTD = true;
    m_inEntityRef = true;
  }

  /**
   * Report the end of an entity.
   *
   * @param name The name of the entity that is ending.
   * @throws org.xml.sax.SAXException The application may raise an exception.
   * @see #startEntity
   */
  public void endEntity(String name) throws org.xml.sax.SAXException
  {
    if (name.equals("[dtd]")) 
      m_inExternalDTD = false;
    m_inEntityRef = false;
  }

  /**
   * Receive notivication of a entityReference.
   *
   * @param name The name of the entity.
   *
   * @throws org.xml.sax.SAXException
   */
  public void entityReference(String name) throws org.xml.sax.SAXException
  {
    writeParentTagEnd();

    if (shouldIndent())
      indent(m_currentIndent);

    try
    {
      final Writer writer = m_writer;
      writer.write("&");
      writer.write(name);
      writer.write(";");
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }

  }

  // Implement DeclHandler

  /**
   *   Report an element type declaration.
   *  
   *   <p>The content model will consist of the string "EMPTY", the
   *   string "ANY", or a parenthesised group, optionally followed
   *   by an occurrence indicator.  The model will be normalized so
   *   that all whitespace is removed,and will include the enclosing
   *   parentheses.</p>
   *  
   *   @param name The element type name.
   *   @param model The content model as a normalized string.
   *   @exception SAXException The application may raise an exception.
   */
  public void elementDecl(String name, String model) throws SAXException
  {
    // Do not inline external DTD
    if (m_inExternalDTD) return; 
    
    try
    {
      final Writer writer = m_writer;
      if (m_inDoctype)
      {
        writer.write(" [");
        writer.write(m_lineSep, 0, m_lineSepLen);
  
        m_inDoctype = false;
      }
  
      writer.write("<!ELEMENT ");
      writer.write(name);
      writer.write(' ');
      writer.write(model);
      writer.write('>');
      writer.write(m_lineSep, 0, m_lineSepLen);
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }
  }

  /** NEEDSDOC Field m_elemName          */
  private String m_elemName = "";

  /**
   * Report an attribute type declaration.
   *
   * <p>Only the effective (first) declaration for an attribute will
   * be reported.  The type will be one of the strings "CDATA",
   * "ID", "IDREF", "IDREFS", "NMTOKEN", "NMTOKENS", "ENTITY",
   * "ENTITIES", or "NOTATION", or a parenthesized token group with
   * the separator "|" and all whitespace removed.</p>
   *
   * @param eName The name of the associated element.
   * @param aName The name of the attribute.
   * @param type A string representing the attribute type.
   * @param valueDefault A string representing the attribute default
   *        ("#IMPLIED", "#REQUIRED", or "#FIXED") or null if
   *        none of these applies.
   * @param value A string representing the attribute's default value,
   *        or null if there is none.
   * @exception SAXException The application may raise an exception.
   */
  public void attributeDecl(
          String eName, String aName, String type, String valueDefault, String value)
            throws SAXException
  {
    // Do not inline external DTD
    if (m_inExternalDTD) return; 

    try
    {
      final Writer writer = m_writer;
      if (m_inDoctype)
      {
        writer.write(" [");
        writer.write(m_lineSep, 0, m_lineSepLen);
  
        m_inDoctype = false;
      }

       
      writer.write("<!ATTLIST ");
      writer.write(eName);
      writer.write(" "); 

      writer.write(aName);
      writer.write(" ");
      writer.write(type);
      if(valueDefault!=null)
      {
	writer.write(" ");
	writer.write(valueDefault);
      }
  
      //m_writer.write(" ");
      //m_writer.write(value);
      writer.write(">");
      writer.write(m_lineSep, 0, m_lineSepLen);
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }
  }

  /**
   * Report an internal entity declaration.
   *
   * <p>Only the effective (first) declaration for each entity
   * will be reported.</p>
   *
   * @param name The name of the entity.  If it is a parameter
   *        entity, the name will begin with '%'.
   * @param value The replacement text of the entity.
   * @exception SAXException The application may raise an exception.
   * @see #externalEntityDecl
   * @see org.xml.sax.DTDHandler#unparsedEntityDecl
   */
  public void internalEntityDecl(String name, String value)
          throws SAXException
  {
    // Do not inline external DTD
    if (m_inExternalDTD) return; 

    try
    {
      if (m_inDoctype)
      {
        m_writer.write(" [");
        m_writer.write(m_lineSep, 0, m_lineSepLen);
  
        m_inDoctype = false;
      }
  
      outputEntityDecl(name, value);
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }
  }

  /**
   * Report a parsed external entity declaration.
   *
   * <p>Only the effective (first) declaration for each entity
   * will be reported.</p>
   *
   * @param name The name of the entity.  If it is a parameter
   *        entity, the name will begin with '%'.
   * @param publicId The declared public identifier of the entity, or
   *        null if none was declared.
   * @param systemId The declared system identifier of the entity.
   * @exception SAXException The application may raise an exception.
   * @see #internalEntityDecl
   * @see org.xml.sax.DTDHandler#unparsedEntityDecl
   */
  public void externalEntityDecl(
          String name, String publicId, String systemId) throws SAXException{}

  /**
   * Handle one of the default entities, return false if it
   * is not a default entity.
   *
   * @param ch character to be escaped.
   * @param i index into character array.
   * @param chars non-null reference to character array.
   * @param len length of chars.
   * @param escLF true if the linefeed should be escaped.
   *
   * @return i+1 if the character was written, else i.
   *
   * @throws org.xml.sax.SAXException
   */
  protected int accumDefaultEntity(
          char ch, int i, char[] chars, int len, boolean escLF)
            throws org.xml.sax.SAXException
  {

    try
    {
      if (!escLF && CharInfo.S_LINEFEED == ch)
      {
        m_writer.write(m_lineSep, 0, m_lineSepLen);
      }
      else
      {
        if (m_charInfo.isSpecial(ch))
        {
          String entityRef = m_charInfo.getEntityNameForChar(ch);
  
          if (null != entityRef)
          {
            final Writer writer = m_writer;
            writer.write('&');
            writer.write(entityRef);
            writer.write(';');
          }
          else
            return i;
        }
        else
          return i;
      }
  
      return i + 1;
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }
  }

  /**
   * Escape and m_writer.write a character.
   *
   * @param ch character to be escaped.
   * @param i index into character array.
   * @param chars non-null reference to character array.
   * @param len length of chars.
   * @param escLF true if the linefeed should be escaped.
   *
   * @return i+1 if the character was written, else i.
   *
   * @throws org.xml.sax.SAXException
   */
  protected int accumDefaultEscape(
          char ch, int i, char[] chars, int len, boolean escLF)
            throws org.xml.sax.SAXException
  {

    int pos = accumDefaultEntity(ch, i, chars, len, escLF);

    if (i == pos)
    {
      pos++;

      try
      {
        if (0xd800 <= ch && ch < 0xdc00)
        {
  
          // UTF-16 surrogate
          int next;
  
          if (i + 1 >= len)
          {
            throw new org.xml.sax.SAXException(
              XSLMessages.createXPATHMessage(
                XPATHErrorResources.ER_INVALID_UTF16_SURROGATE,
                new Object[]{ Integer.toHexString(ch) }));  //"Invalid UTF-16 surrogate detected: "
  
            //+Integer.toHexString(ch)+ " ?");
          }
          else
          {
            next = chars[++i];
  
            if (!(0xdc00 <= next && next < 0xe000))
              throw new org.xml.sax.SAXException(
                XSLMessages.createXPATHMessage(
                  XPATHErrorResources.ER_INVALID_UTF16_SURROGATE,
                  new Object[]{
                    Integer.toHexString(ch) + " "
                    + Integer.toHexString(next) }));  //"Invalid UTF-16 surrogate detected: "
  
            //+Integer.toHexString(ch)+" "+Integer.toHexString(next));
            next = ((ch - 0xd800) << 10) + next - 0xdc00 + 0x00010000;
          }
  
          m_writer.write("&#");
          m_writer.write(Integer.toString(next));
          m_writer.write(";");
  
          /*} else if (null != ctbc && !ctbc.canConvert(ch)) {
          sb.append("&#x");
          sb.append(Integer.toString((int)ch, 16));
          sb.append(";");*/
        }
        else
        {
          if (!canConvert(ch) || (m_charInfo.isSpecial(ch)))
          {
            m_writer.write("&#");
            m_writer.write(Integer.toString(ch));
            m_writer.write(";");
          }
          else
          {
            m_writer.write(ch);
          }
        }
      }
      catch(IOException ioe)
      {
        throw new SAXException(ioe);
      }
    }

    return pos;
  }
  
  /**
   * Opaque reference to the sun.io.CharToByteConverter for this 
   * encoding.
   */
  Object m_charToByteConverter = null;
  
  /**
   * Method reference to the sun.io.CharToByteConverter#canConvert method 
   * for this encoding.  Invalid if m_charToByteConverter is null.
   */
  java.lang.reflect.Method m_canConvertMeth;
  
  /**
   * Boolean that tells if we already tried to get the converter.
   */
  boolean m_triedToGetConverter = false;
  
  /**
   * Tell if this character can be written without escaping.
   */
  public boolean canConvert(char ch)
  {
    if(ch < 127)
    {
      if(ch >= 0x20 || (0x0A == ch || 0x0D == ch || 0x09 == ch) )
        return true;
      else
        return false;
    }
    
    if(null == m_charToByteConverter && false == m_triedToGetConverter)
    {
      m_triedToGetConverter = true;
      try
      {
        m_charToByteConverter = Encodings.getCharToByteConverter(m_encoding);
        if(null != m_charToByteConverter)
        {
          Class argsTypes[] = new Class[1];
          argsTypes[0] = Character.TYPE;
          Class convClass = m_charToByteConverter.getClass();
          m_canConvertMeth = convClass.getMethod("canConvert", argsTypes);
        }
      }
      catch(Exception e)
      {
       // This is just an assert: no action at the moment.
        System.err.println("Warning: "+e.getMessage());
      }
    }
    if(null != m_charToByteConverter)
    {
      try
      {
        Object args[] = new Object[1];
        args[0] = new Character( ch );
        Boolean bool 
          = (Boolean)m_canConvertMeth.invoke(m_charToByteConverter, args);
        return bool.booleanValue() ? !Character.isISOControl(ch) : false;
      }
      catch(java.lang.reflect.InvocationTargetException ite)
      {
        // This is just an assert: no action at the moment.
        System.err.println("Warning: InvocationTargetException in canConvert!");
      }
      catch(java.lang.IllegalAccessException iae)
      {
        // This is just an assert: no action at the moment.
        System.err.println("Warning: IllegalAccessException in canConvert!");
      }
    }
    // fallback!
    return ( ch <= m_maxCharacter );
  }


  /**
   * Returns the specified <var>string</var> after substituting <VAR>specials</VAR>,
   * and UTF-16 surrogates for chracter references <CODE>&amp;#xnn</CODE>.
   *
   * @param   string      String to convert to XML format.
   * @param   encoding    CURRENTLY NOT IMPLEMENTED.
   *
   * @throws org.xml.sax.SAXException
   */
  public void writeAttrString(String string, String encoding)
          throws org.xml.sax.SAXException
  {

    try
    {
      final char[] stringChars = string.toCharArray();
      final int len = stringChars.length;
      final Writer writer = m_writer;
  
      for (int i = 0; i < len; i++)
      {
        char ch = stringChars[i];
  
        if (canConvert(ch) && (!m_charInfo.isSpecial(ch)))
        {
          writer.write(ch);
        }
        else
        {
  
          // I guess the parser doesn't normalize cr/lf in attributes. -sb
          if ((CharInfo.S_CARRIAGERETURN == ch) && ((i + 1) < len)
                  && (CharInfo.S_LINEFEED == stringChars[i + 1]))
          {
            i++;
  
            ch = CharInfo.S_LINEFEED;
          }
  
          accumDefaultEscape(ch, i, stringChars, len, true);
        }
      }
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }
  }

  /**
   * Tell if, based on space preservation constraints and the doIndent property,
   * if an indent should occur.
   *
   * @return True if an indent should occur.
   */
  protected boolean shouldIndent()
  {
    return m_doIndent && (!m_ispreserve &&!m_isprevtext);
  }

  /**
   * Prints <var>n</var> spaces.
   * @param pw        The character output stream to use.
   * @param n         Number of spaces to print.
   *
   * @throws org.xml.sax.SAXException if an error occurs when writing.
   */
  public void printSpace(int n) throws org.xml.sax.SAXException
  {

    try
    {
      for (int i = 0; i < n; i++)
      {
        m_writer.write(' ');
      }
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }
  }

  /**
   * Prints a newline character and <var>n</var> spaces.
   * @param pw        The character output stream to use.
   * @param n         Number of spaces to print.
   *
   * @throws org.xml.sax.SAXException if an error occurs during writing.
   */
  public void indent(int n) throws org.xml.sax.SAXException
  {

    if (m_startNewLine)
      outputLineSep();

    if (m_doIndent)
    {
      printSpace(n);
    }
  }

  /**
   * Specifies an output stream to which the document should be
   * serialized. This method should not be called while the
   * serializer is in the process of serializing a document.
   * <p>
   * The encoding specified in the output properties is used, or
   * if no encoding was specified, the default for the selected
   * output method.
   *
   * @param output The output stream
   */
  public void setOutputStream(OutputStream output)
  {

    try
    {
      init(output, m_format);
    }
    catch (UnsupportedEncodingException uee)
    {

      // Should have been warned in init, I guess...
    }
  }

  /**
   * Get the output stream where the events will be serialized to.
   *
   * @return reference to the result stream, or null of only a writer was
   * set.
   */
  public OutputStream getOutputStream()
  {

    if (m_writer instanceof WriterToUTF8Buffered)
      return ((WriterToUTF8Buffered) m_writer).getOutputStream();
    if (m_writer instanceof WriterToUTF8)
      return ((WriterToUTF8) m_writer).getOutputStream();
    else if (m_writer instanceof WriterToASCI)
      return ((WriterToASCI) m_writer).getOutputStream();
    else
      return null;
  }

  /**
   * Specifies a writer to which the document should be serialized.
   * This method should not be called while the serializer is in
   * the process of serializing a document.
   *
   * @param writer The output writer stream
   */
  public void setWriter(Writer writer)
  {
    m_writer = writer;
  }

  /**
   * Get the character stream where the events will be serialized to.
   *
   * @return Reference to the result Writer, or null.
   */
  public Writer getWriter()
  {
    return m_writer;
  }

  /**
   * Specifies an output format for this serializer. It the
   * serializer has already been associated with an output format,
   * it will switch to the new format. This method should not be
   * called while the serializer is in the process of serializing
   * a document.
   *
   * @param format The output format to use
   */
  public void setOutputFormat(Properties format)
  {

    boolean shouldFlush = m_shouldFlush;

    init(m_writer, format, false);

    m_shouldFlush = shouldFlush;
  }

  /**
   * Returns the output format for this serializer.
   *
   * @return The output format in use
   */
  public Properties getOutputFormat()
  {
    return m_format;
  }

  /**
   * Return a {@link ContentHandler} interface into this serializer.
   * If the serializer does not support the {@link ContentHandler}
   * interface, it should return null.
   *
   * @return A {@link ContentHandler} interface into this serializer,
   *  or null if the serializer is not SAX 2 capable
   * @throws IOException An I/O exception occured
   */
  public ContentHandler asContentHandler() throws IOException
  {
    return this;
  }

  /**
   * Return a {@link DOMSerializer} interface into this serializer.
   * If the serializer does not support the {@link DOMSerializer}
   * interface, it should return null.
   *
   * @return A {@link DOMSerializer} interface into this serializer,
   *  or null if the serializer is not DOM capable
   * @throws IOException An I/O exception occured
   */
  public DOMSerializer asDOMSerializer() throws IOException
  {
    return this;  // for now
  }

  /**
   * Resets the serializer. If this method returns true, the
   * serializer may be used for subsequent serialization of new
   * documents. It is possible to change the output format and
   * output stream prior to serializing, or to use the existing
   * output format and output stream.
   *
   * @return True if serializer has been reset and can be reused
   */
  public boolean reset()
  {
    return false;
  }

  /**
   * Serializes the DOM node. Throws an exception only if an I/O
   * exception occured while serializing.
   *
   * @param elem The element to serialize
   *
   * @param node Node to serialize.
   * @throws IOException An I/O exception occured while serializing
   */
  public void serialize(Node node) throws IOException
  {

    try
    {
      TreeWalker walker = new TreeWalker(this,
                                         new org.apache.xpath.DOM2Helper());

      walker.traverse(node);
    }
    catch (org.xml.sax.SAXException se)
    {
      throw new WrappedRuntimeException(se);
    }
  }
}  //ToXMLStringVisitor

